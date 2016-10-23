/**
 * Created by claire on 10/21/16.
 */
//import com.apple.eawt.AppEvent;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;

import java.lang.String;

import java.util.HashMap;
import java.util.Map;

public class CookMentorSender {
    private final Bezirk bezirk;
    private final int MAX_ITEM = 20;
    Map<String, String> map;

    public CookMentorSender() {
        Config config = new Config.ConfigBuilder().setGroupName("CookMentor").create();
        BezirkMiddleware.initialize(config);
        this.bezirk = BezirkMiddleware.registerZirk("CookMentor Zirk");

        // Give Bezirk 2 seconds to fully initialize before publishing events
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Initialize the map
        this.map = new HashMap<String, String>();
    }

    public void addItem(String id, String name) {
        int ItemId = Integer.valueOf(id);
        if (ItemId > MAX_ITEM) {
            System.out.printf("Invalid Item id, must be less than %d\n", MAX_ITEM);
            return;
        }
        map.put(id, name);
    }

    public void sendSingleEvent(String id) {
        Light light = new Light(id, "philiphs.hue.bulb.color");
        TurnLightOnEvent turnLightOnEvent = new TurnLightOnEvent(light);
        bezirk.sendEvent(turnLightOnEvent);
        System.out.println("Event sent!");
    }

    public void sendAllEvents() {
        for (String id : map.keySet()) {
            System.out.printf("id: %s\n", id);
            sendSingleEvent(id);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        CookMentorSender sender = new CookMentorSender();

        sender.addItem("1", "Oven");
        sender.addItem("2", "Knife");
        sender.addItem("3", "Cook");
        sender.sendAllEvents();
        System.out.println("Complete Hello!");
    }
}
