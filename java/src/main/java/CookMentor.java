import com.bezirk.hardwareevents.light.*;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.lang.String;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 10/22/16.
 */
public class CookMentor {
    private final Bezirk sendBezirk;
    private final Bezirk receiveBezirk;
    private final int MAX_ITEM = 5;
    Map<String, Boolean> map;

    public CookMentor() {
        //Initialize the map
        map = new HashMap<String, Boolean>();
        Config config = new Config.ConfigBuilder().setGroupName("CookMentor").create();
        BezirkMiddleware.initialize(config);
        sendBezirk = BezirkMiddleware.registerZirk("Cook Mentor Send Zirk");
        receiveBezirk = BezirkMiddleware.registerZirk("Cook Mentor Receive Zirk");

        // Give Bezirk 2 seconds to fully initialize before publishing events
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initReceive() {
        EventSet lightEvents = new EventSet(CurrentLightStateEvent.class);
        lightEvents.setEventReceiver((Event event, ZirkEndPoint sender) -> {
            System.out.println("Receive even from lightEvent");
            if (event instanceof LightEvent) {
                final Light newLight = ((LightEvent) event).getLight();

                if (event instanceof CurrentLightStateEvent) {
                    System.out.printf("Light Info: %s\n", newLight.toString());
                    int newId = Integer.valueOf(newLight.getId());

                    map.put(String.valueOf(newId), false);
                    String toBeSent = String.valueOf(newId + 1);
                    if (map.containsKey(toBeSent) && map.get(toBeSent)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendTurnOnEvent(toBeSent);
                    } else {
                        System.out.println("The list has been empty, please restart!");
                        initAllItems();
                        for (String id : map.keySet()) {
                            sendTurnOffEvent(id);
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendTurnOnEvent("1");
                        return;
                    }
                }
            }
        });
        receiveBezirk.subscribe(lightEvents);
    }

    public void addItem(String id) {
        int ItemId = Integer.valueOf(id);
        if (ItemId > MAX_ITEM) {
            System.out.printf("Invalid Item id, must be less than %d\n", MAX_ITEM);
            return;
        }
        map.put(id, true);
    }

    public void initAllItems() {
        for (int i = 1; i <= MAX_ITEM; i++) {
            addItem(String.valueOf(i));
        }
    }

    public void sendTurnOnEvent(String id) {
        Light light = new Light(id, "philiphs.hue.bulb.color");
        TurnLightOnEvent turnLightOnEvent = new TurnLightOnEvent(light);
        sendBezirk.sendEvent(turnLightOnEvent);
        System.out.println("light turn on event sent!");
    }

    public void sendTurnOffEvent(String id) {
        Light light = new Light(id, "philiphs.hue.bulb.color");
        TurnLightOffEvent turnLightOffEvent = new TurnLightOffEvent(light);
        sendBezirk.sendEvent(turnLightOffEvent);
        System.out.println("light turn off event sent!");
    }

    public void sendAllEvents() {
        for (String id : map.keySet()) {
            System.out.printf("id: %s\n", id);
            sendTurnOnEvent(id);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kickOff() {
        for (String id : map.keySet()) {
            sendTurnOffEvent(id);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendTurnOnEvent("1");
    }

    public static void main(String args[]) {
        CookMentor cookMentor = new CookMentor();

        cookMentor.initReceive();
        cookMentor.initAllItems();
        cookMentor.kickOff();
        System.out.println("Complete Demo!");
    }
}
