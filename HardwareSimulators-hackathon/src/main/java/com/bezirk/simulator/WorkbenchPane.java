package com.bezirk.simulator;

import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvent;
import com.bezirk.hardwareevents.environment.EnvironmentalSensor;
import com.bezirk.hardwareevents.environment.EnvironmentalSensorsDetectedEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightsDetectedEvent;
import com.bezirk.hardwareevents.robot.Robot;
import com.bezirk.hardwareevents.robot.RobotsDetectedEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;
import com.bezirk.simulator.pane.BeaconPane;
import com.bezirk.simulator.pane.CookItemPane;
import com.bezirk.simulator.pane.DevicePane;
import com.bezirk.simulator.pane.EnvironmentalSensorPane;
import com.bezirk.simulator.pane.PhilipsHueColorPane;
import com.bezirk.simulator.pane.RobotPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WorkbenchPane extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(WorkbenchPane.class);


    private final Bezirk bezirk;
    private boolean isShowingWorkbenchLabel = true;
    private final Stage deviceSelectionStage = new Stage();
    private final VBox deviceContainer = new VBox();

    private final Map<Class<?>, Set<DevicePane>> devices = new HashMap<>();

    public WorkbenchPane() throws Exception {

        String groupID = "CookMentor"; //Set a hard-to-guess groupID for your group


        if (groupID == "") {
            throw new Exception("Error: No Group ID set. Please set a group ID.");
        }
        else {
            Config config = new Config.ConfigBuilder().setGroupName(groupID).create();
            BezirkMiddleware.initialize(config);
        }
        
        bezirk = BezirkMiddleware.registerZirk("Hardware Simulator Zirk");
        final int prefHeight = 37, prefWidth = 200;
        final Button addDeviceButton = new Button("Add a device");
        //addDeviceButton.setTextFill(Color.DARKGREEN);
        //addDeviceButton.setFont(new Font("System bold", 14));
        addDeviceButton.setPrefHeight(prefHeight);
        addDeviceButton.setPrefWidth(prefWidth);

        addDeviceButton.setOnAction((event) -> {
            if (deviceSelectionStage.getOwner() == null)
                deviceSelectionStage.initOwner(getScene().getWindow());
            deviceSelectionStage.show();
        });

        /*
        final Button discoverDevicesButton = new Button("Send device-detected events");
        discoverDevicesButton.setPrefHeight(prefHeight);
        discoverDevicesButton.setPrefWidth(prefWidth);


        discoverDevicesButton.setOnAction((event) -> {
            if (devices.containsKey(BeaconPane.class)) {
                final List<Beacon> beacons = new ArrayList<>();

                for (DevicePane p : devices.get(BeaconPane.class)) {
                    beacons.add(((BeaconPane) p).getBeacon());
                }

                bezirk.sendEvent(new BeaconsDetectedEvent(beacons));
            }

            if (devices.containsKey(EnvironmentalSensorPane.class)) {
                final Set<EnvironmentalSensor> environmentalSensors = new HashSet<>();

                for (DevicePane p : devices.get(EnvironmentalSensorPane.class)) {
                    environmentalSensors.add(((EnvironmentalSensorPane) p).getSensor());
                }

                bezirk.sendEvent(new EnvironmentalSensorsDetectedEvent(environmentalSensors));
            }

            if (devices.containsKey(PhilipsHueColorPane.class)) {
                final Set<Light> lights = new HashSet<>();

                for (DevicePane p : devices.get(PhilipsHueColorPane.class)) {
                    lights.add(((PhilipsHueColorPane) p).getLight());
                }

                bezirk.sendEvent(new LightsDetectedEvent(lights));
            }

            if (devices.containsKey(RobotPane.class)) {
                final Set<Robot> robots = new HashSet<>();

                for (DevicePane p : devices.get(RobotPane.class)) {
                    robots.add(((RobotPane) p).getRobot());
                }

                bezirk.sendEvent(new RobotsDetectedEvent(robots));
            }
        });

        */
        /*
        final Button helpButton = new Button("Help");
        helpButton.setPrefWidth(prefWidth);
        helpButton.setPrefHeight(prefHeight);
        */

        //setTop(new HBox(addDeviceButton, discoverDevicesButton));
        setTop(new HBox(addDeviceButton));

        final Label emptyWorkbenchLabel = new Label("Empty workbench");
        emptyWorkbenchLabel.setPrefSize(1400, 800);
        emptyWorkbenchLabel.setAlignment(Pos.CENTER);
        emptyWorkbenchLabel.setFont(new Font("System", 30));
        setCenter(emptyWorkbenchLabel);

        createDeviceSelectionStage();
    }

    private void createDeviceSelectionStage() {
        final TilePane devicePane = new TilePane();

        devicePane.setHgap(20);
        devicePane.setVgap(20);
        devicePane.setPrefColumns(2);

//        devicePane.getChildren().add(createDeviceButton("Beacon", BeaconPane.class));
//        devicePane.getChildren().add(createDeviceButton("Environmental Sensor", EnvironmentalSensorPane.class));
//        devicePane.getChildren().add(createDeviceButton("Philips Hue Color", PhilipsHueColorPane.class));
//        devicePane.getChildren().add(createDeviceButton("Robot", RobotPane.class));
        devicePane.getChildren().add(createDeviceButton("Cook Item", CookItemPane.class));

        deviceSelectionStage.setTitle("Click a Device to Add to the Workbench");
        deviceSelectionStage.setScene(new Scene(devicePane));
        deviceSelectionStage.initModality(Modality.APPLICATION_MODAL);
        deviceSelectionStage.sizeToScene();
    }

    private Button createDeviceButton(String name, Class<?> deviceClass) {
        final Button deviceButton = new Button(name);
        deviceButton.setPrefSize(150, 150);

        deviceButton.setOnAction((event) -> {
            try {
                final DevicePane devicePane =
                        (DevicePane) deviceClass.getConstructor(Bezirk.class, Pane.class).newInstance(bezirk, deviceContainer);
                deviceContainer.getChildren().add(devicePane);
                devicePane.setDeviceClosingListener(() -> devices.get(deviceClass).remove(devicePane));

                if (!devices.containsKey(deviceClass)) devices.put(deviceClass, new HashSet<>());
                devices.get(deviceClass).add(devicePane);

                if (isShowingWorkbenchLabel) {
                    setCenter(new ScrollPane(deviceContainer));
                    isShowingWorkbenchLabel = false;
                }

                deviceSelectionStage.hide();
            } catch (ReflectiveOperationException e) {
                logger.error("Error adding new device to workbench", e);
            }
        });

        return deviceButton;
    }
}