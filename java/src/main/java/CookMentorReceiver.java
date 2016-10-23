import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
//import com.sun.org.apache.xpath.internal.operations.String;

/**
 * Created by claire on 10/22/16.
 */

public class CookMentorReceiver {

    public CookMentorReceiver() {
        Config config = new Config.ConfigBuilder().setGroupName("CookMentor").create();
        BezirkMiddleware.initialize(config);
        Bezirk bezirk = BezirkMiddleware.registerZirk("Cook Mentor Zirk");

        EventSet lightEvents = new EventSet(CurrentLightStateEvent.class);
        lightEvents.setEventReceiver((Event event, ZirkEndPoint sender)-> {
            System.out.println("Receive even from lightEvent");
            if (event instanceof LightEvent) {
                final Light new_light = ((LightEvent)event).getLight();

                if (event instanceof CurrentLightStateEvent) {
                    System.out.printf("Light Info: %s\n", new_light.toString());
                }
            }
        });
        bezirk.subscribe(lightEvents);
    }

    public static void main(String args[]) {
        CookMentorReceiver receiver = new CookMentorReceiver();
        System.out.println("Listening for hello world...");
    }
}
