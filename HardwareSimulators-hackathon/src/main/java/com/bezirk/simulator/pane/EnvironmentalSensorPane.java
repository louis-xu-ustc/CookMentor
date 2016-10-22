package com.bezirk.simulator.pane;

import com.bezirk.hardwareevents.Pressure;
import com.bezirk.hardwareevents.Temperature;
import com.bezirk.hardwareevents.environment.BarometricPressureReadingEvent;
import com.bezirk.hardwareevents.environment.EnvironmentalSensor;
import com.bezirk.hardwareevents.environment.GetEnvironmentSensorReadingEvent;
import com.bezirk.hardwareevents.environment.HumidityReadingEvent;
import com.bezirk.hardwareevents.environment.TemperatureReadingEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.messages.EventSet;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

public class EnvironmentalSensorPane extends DevicePane {
    private final Bezirk bezirk;
    private final EnvironmentalSensor sensor;
    private final TextField sensorIdTextField;
    private final TextField temperatureTextField = new TextField("75.0");
    private final TextField humidityTextField = new TextField("60.0");
    private final TextField barometricPressureTextField = new TextField("1020.0");

    public EnvironmentalSensorPane(Bezirk bezirk, Pane parent) {
        super(parent);

        this.bezirk = bezirk;

        final Set<EnvironmentalSensor.SensorCapability> sensorCapabilities = new HashSet<>();
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.TEMPERATURE);
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.HUMIDITY);
        sensorCapabilities.add(EnvironmentalSensor.SensorCapability.BAROMETRIC_PRESSURE);

        String uuid = UUID.randomUUID().toString();
        String shortenedUUID = uuid.substring(0, Math.min(uuid.length(), 8)); //Using only up to first 8 chars for brevity.
        sensorIdTextField = new TextField(shortenedUUID);

        sensor = new EnvironmentalSensor(shortenedUUID, sensorCapabilities,
                "bezirk.simulator.environment.sensor");

        layoutPane();

        final EventSet environmentalEvents = new EventSet(GetEnvironmentSensorReadingEvent.class);

        environmentalEvents.setEventReceiver((event, sender) -> {
            if (event instanceof GetEnvironmentSensorReadingEvent) {
                GetEnvironmentSensorReadingEvent getReadingsEvent =
                        (GetEnvironmentSensorReadingEvent) event;

                final EnvironmentalSensor sensor = getReadingsEvent.getSensor();

                if (!this.sensor.equals(sensor)) return;

                appendStatus("Received " + getReadingsEvent.toString());

                for (EnvironmentalSensor.SensorCapability capability :
                        getReadingsEvent.getCapabilities()) {
                    switch (capability) {
                        case TEMPERATURE:
                            sendTemperatureReading();
                            break;

                        case HUMIDITY:
                            sendHumidityReading();
                            break;

                        case BAROMETRIC_PRESSURE:
                            sendBarometricPressureReading();
                            break;

                        case LIGHT:
                            // Ignore currently unsupported capability
                            break;
                    }
                }
            }
        });

        bezirk.subscribe(environmentalEvents);
    }

    private void layoutPane() {
        setStyle("-fx-border-color: white gray gray gray;");

        addTitlePane("Environment\nSensor");


        final Label idLabel = new Label("Sensor ID");
        idLabel.setLayoutX(38);
        idLabel.setLayoutY(45);
        idLabel.setFont(LABEL_FONT);

        sensorIdTextField.setLayoutX(38);
        sensorIdTextField.setLayoutY(75);
        sensorIdTextField.setPrefSize(111, 25);
        sensorIdTextField.setEditable(false);

        final Label temperatureLabel = new Label("Temperature (F)");
        temperatureLabel.setLayoutX(184);
        temperatureLabel.setLayoutY(45);
        temperatureLabel.setFont(LABEL_FONT);

        temperatureTextField.setLayoutX(184);
        temperatureTextField.setLayoutY(75);
        temperatureTextField.setPrefSize(111, 25);

        final Label humidityLabel = new Label("Relative Humidity (%)");
        humidityLabel.setLayoutX(328);
        humidityLabel.setLayoutY(45);
        humidityLabel.setFont(LABEL_FONT);

        humidityTextField.setLayoutX(328);
        humidityTextField.setLayoutY(75);
        humidityTextField.setPrefSize(111, 25);

        final Label barometricPressureLabel = new Label("Barometric Pressure (hPa)");
        barometricPressureLabel.setLayoutX(480);
        barometricPressureLabel.setLayoutY(45);
        barometricPressureLabel.setFont(LABEL_FONT);

        barometricPressureTextField.setLayoutX(480);
        barometricPressureTextField.setLayoutY(75);
        barometricPressureTextField.setPrefSize(111, 25);

        final Button sendEnvironmentReadingsButton = new Button("Send Environment Readings");
        sendEnvironmentReadingsButton.setLayoutX(643);
        sendEnvironmentReadingsButton.setLayoutY(59);
        sendEnvironmentReadingsButton.setPrefSize(126, 65);
        sendEnvironmentReadingsButton.setTextAlignment(TextAlignment.CENTER);
        sendEnvironmentReadingsButton.setWrapText(true);
        sendEnvironmentReadingsButton.setOnAction((event) -> sendEnvironmentReadings());

        final Pane bodyPane = new Pane(idLabel, sensorIdTextField, temperatureLabel, temperatureTextField,
                humidityLabel, humidityTextField, barometricPressureLabel, barometricPressureTextField,
                sendEnvironmentReadingsButton);

        addStatusArea(bodyPane);

        bodyPane.setStyle("-fx-border-color: white white white gray;");

        getChildren().add(bodyPane);
    }

    private void sendTemperatureReading() {
        final TemperatureReadingEvent temperatureReading = new TemperatureReadingEvent(sensor,
                new Temperature(Double.parseDouble(temperatureTextField.getText()),
                        Temperature.TemperatureUnit.FAHRENHEIT));

        appendStatus(String.format("Sending temperature reading: %s%n",
                temperatureReading.toString()));

        bezirk.sendEvent(temperatureReading);
    }

    private void sendHumidityReading() {
        final HumidityReadingEvent humidityReading = new HumidityReadingEvent(sensor,
                Double.parseDouble(humidityTextField.getText()));

        appendStatus(String.format("Sending humidity reading: %s%n",
                humidityReading.toString()));

        bezirk.sendEvent(humidityReading);
    }

    private void sendBarometricPressureReading() {
        final BarometricPressureReadingEvent barometricPressureReading =
                new BarometricPressureReadingEvent(sensor,
                        new Pressure(Double.parseDouble(barometricPressureTextField.getText()),
                                Pressure.PressureUnit.HECTOPASCALS));

        appendStatus(String.format("Sending barometric pressure reading: %s%n",
                barometricPressureReading.toString()));

        bezirk.sendEvent(barometricPressureReading);
    }

    private void sendEnvironmentReadings() {
        sendTemperatureReading();
        sendHumidityReading();
        sendBarometricPressureReading();
    }

    public EnvironmentalSensor getSensor() {
        return sensor;
    }
}
