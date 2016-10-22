package com.bezirk.simulator.pane;

import com.bezirk.hardwareevents.beacon.Beacon;
import com.bezirk.hardwareevents.beacon.BeaconsDetectedEvent;
import com.bezirk.middleware.Bezirk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

public class BeaconPane extends DevicePane {
    private final Bezirk bezirk;
    String uuid = UUID.randomUUID().toString();
    String shortenedUUID = uuid.substring(0, Math.min(uuid.length(), 8)); //Using only up to first 8 chars for brevity.
    private final TextField beaconIdTextField = new TextField(shortenedUUID);
    private final TextField majorTextField = new TextField("0");
    private final TextField minorTextField = new TextField("0");
    private final TextField rssiTextField = new TextField("80");
    public BeaconPane(Bezirk bezirk, Pane parent) {
        super(parent);

        this.bezirk = bezirk;

        layoutPane();
    }

    private void layoutPane() {
        setStyle("-fx-border-color: white gray gray gray;");

        addTitlePane("Beacon");

        final Label idLabel = new Label("Beacon ID");
        idLabel.setLayoutX(38);
        idLabel.setLayoutY(45);
        idLabel.setFont(LABEL_FONT);

        beaconIdTextField.setLayoutX(38);
        beaconIdTextField.setLayoutY(75);
        beaconIdTextField.setPrefSize(111, 25);
        beaconIdTextField.setEditable(false);

        final Label majorLabel = new Label("Major");
        majorLabel.setLayoutX(184);
        majorLabel.setLayoutY(45);
        majorLabel.setFont(LABEL_FONT);

        majorTextField.setLayoutX(184);
        majorTextField.setLayoutY(75);
        majorTextField.setPrefSize(111, 25);

        final Label minorLabel = new Label("Minor");
        minorLabel.setLayoutX(328);
        minorLabel.setLayoutY(45);
        minorLabel.setFont(LABEL_FONT);

        minorTextField.setLayoutX(328);
        minorTextField.setLayoutY(75);
        minorTextField.setPrefSize(111, 25);

        final Label rssiLabel = new Label("RSSI");
        rssiLabel.setLayoutX(472);
        rssiLabel.setLayoutY(45);
        rssiLabel.setFont(LABEL_FONT);

        rssiTextField.setLayoutX(472);
        rssiTextField.setLayoutY(75);
        rssiTextField.setPrefSize(111, 25);

        final Button sendBeaconDetectedEventButton = new Button("Send Beacon Detected");
        sendBeaconDetectedEventButton.setLayoutX(643);
        sendBeaconDetectedEventButton.setLayoutY(62);
        sendBeaconDetectedEventButton.setPrefSize(126, 65);
        sendBeaconDetectedEventButton.setTextAlignment(TextAlignment.CENTER);
        sendBeaconDetectedEventButton.setWrapText(true);
        sendBeaconDetectedEventButton.setOnAction((event) -> sendBeaconDetectedEvent());

        final Pane bodyPane = new Pane(idLabel, beaconIdTextField, majorLabel, majorTextField,
                minorLabel, minorTextField, rssiLabel, rssiTextField,
                sendBeaconDetectedEventButton);

        addStatusArea(bodyPane);

        bodyPane.setStyle("-fx-border-color: white white white gray;");

        getChildren().add(bodyPane);
    }

    private void sendBeaconDetectedEvent() {
        final List<Beacon> beacons = new ArrayList<>();
        final Beacon beacon = new Beacon(beaconIdTextField.getText(),
                Integer.parseInt(majorTextField.getText()), Integer.parseInt(minorTextField.getText()),
                Integer.parseInt(rssiTextField.getText()), "bezirk.simulator.beacon");
        beacons.add(beacon);

        appendStatus("Sending Beacon Detected Event: " + beacon.toString() + "\n");

        final BeaconsDetectedEvent beaconsDetectedEvt = new BeaconsDetectedEvent(beacons);

        bezirk.sendEvent(beaconsDetectedEvt);
    }

    public Beacon getBeacon() {
        return new Beacon(beaconIdTextField.getText(),
                Integer.parseInt(majorTextField.getText()), Integer.parseInt(minorTextField.getText()),
                Integer.parseInt(rssiTextField.getText()), "bezirk.simulator.beacon");
    }
}
