package com.bezirk.simulator.pane;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.light.CurrentLightStateEvent;
import com.bezirk.hardwareevents.light.GetLightStateEvent;
import com.bezirk.hardwareevents.light.Light;
import com.bezirk.hardwareevents.light.LightEvent;
import com.bezirk.hardwareevents.light.SetLightBrightnessEvent;
import com.bezirk.hardwareevents.light.SetLightColorEvent;
import com.bezirk.hardwareevents.light.TurnLightOffEvent;
import com.bezirk.hardwareevents.light.TurnLightOnEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Blend;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;

public class PhilipsHueColorPane extends DevicePane {
    private static int hueLightCount = 0;

    private final Bezirk bezirk;
    private final Light light;

    private final TextField lightIdTextField;
    private final ImageView lightBulbImageView = new ImageView(new Image("/light_bulb.png"));
    private final RadioButton lightOnRadioButton = new RadioButton("On");
    private final RadioButton lightOffRadioButton = new RadioButton("Off");
    private final Circle lightBulbCircle = new Circle();
    private final ToggleGroup lightStateToggleGroup = new ToggleGroup();
    private final ColorPicker lightColorPicker = new ColorPicker();

    public PhilipsHueColorPane(Bezirk bezirk, Pane parent) {
        super(parent);

        hueLightCount++;

        this.bezirk = bezirk;
        String lightId = String.valueOf(hueLightCount);
        this.light = new Light(lightId, "philips.hue.bulb.color");
        lightIdTextField = new TextField(lightId);

        layoutPane();

        final EventSet lightEvents = new EventSet(TurnLightOnEvent.class, TurnLightOffEvent.class,
                SetLightBrightnessEvent.class, SetLightColorEvent.class, GetLightStateEvent.class);

        lightEvents.setEventReceiver((Event event, ZirkEndPoint sender) -> {
            System.out.println("received event in lightEvents receiver");

            if (event instanceof LightEvent) {
                final Light light = ((LightEvent) event).getLight();

                if (!this.light.equals(light)) return;

                appendStatus(String.format("Got light event: %s%n", event.toString()));

                if (event instanceof TurnLightOnEvent) {
                    turnLightOn();
                } else if (event instanceof TurnLightOffEvent) {
                    turnLightOff();
                } else if (event instanceof SetLightBrightnessEvent) {
                    // TODO: Support setting brightness
                } else if (event instanceof SetLightColorEvent) {
                    final SetLightColorEvent colorEvent = (SetLightColorEvent) event;
                    setLightColor(colorEvent.getColor());
                } else if (event instanceof GetLightStateEvent) {
                    sendLightState();
                }
            }
        });

        bezirk.subscribe(lightEvents);
    }

    private void layoutPane() {
        setStyle("-fx-border-color: white gray gray gray;");

        addTitlePane("Philips Hue Light");

        final Label idLabel = new Label("Light ID");
        idLabel.setLayoutX(38);
        idLabel.setLayoutY(45);
        idLabel.setFont(LABEL_FONT);

        lightIdTextField.setLayoutX(38);
        lightIdTextField.setLayoutY(75);
        lightIdTextField.setPrefSize(111, 25);
        lightIdTextField.setEditable(false);

        lightBulbImageView.setFitWidth(200);
        lightBulbImageView.setFitHeight(150);
        lightBulbImageView.setLayoutX(178);
        lightBulbImageView.setLayoutY(21);
        lightBulbImageView.setPickOnBounds(true);
        lightBulbImageView.setPreserveRatio(true);
        lightBulbImageView.setEffect(new Blend());

        lightBulbCircle.setFill(Color.valueOf("#fbffab"));
        lightBulbCircle.setLayoutX(221);
        lightBulbCircle.setLayoutY(65);
        lightBulbCircle.setOpacity(0);
        lightBulbCircle.setRadius(39);
        lightBulbCircle.setStroke(Color.BLACK);
        lightBulbCircle.setStrokeType(StrokeType.INSIDE);
        lightBulbCircle.setStrokeWidth(0);

        lightOnRadioButton.setLayoutX(346);
        lightOnRadioButton.setLayoutY(51);
        lightOnRadioButton.setOnAction((event) -> lightWasToggled());
        lightOnRadioButton.setToggleGroup(lightStateToggleGroup);

        lightOffRadioButton.setLayoutX(346);
        lightOffRadioButton.setLayoutY(90);
        lightOffRadioButton.setOnAction((event) -> lightWasToggled());
        lightOffRadioButton.setToggleGroup(lightStateToggleGroup);
        lightOffRadioButton.setSelected(true);

        final Label colorLabel = new Label("Color");
        colorLabel.setLayoutX(435);
        colorLabel.setLayoutY(24);
        colorLabel.setFont(LABEL_FONT);

        lightColorPicker.setDisable(true);
        lightColorPicker.setLayoutX(395);
        lightColorPicker.setLayoutY(51);
        lightColorPicker.setOnAction((event) -> lightWasToggled());
        lightColorPicker.setPrefSize(105, 44);

        final Button sendLightStateEventButton = new Button("Send Light State");
        sendLightStateEventButton.setLayoutX(643);
        sendLightStateEventButton.setLayoutY(62);
        sendLightStateEventButton.setPrefSize(126, 65);
        sendLightStateEventButton.setTextAlignment(TextAlignment.CENTER);
        sendLightStateEventButton.setWrapText(true);
        sendLightStateEventButton.setOnAction((event) -> sendLightState());

        final Pane bodyPane = new Pane(idLabel, lightIdTextField, lightBulbImageView, lightBulbCircle, lightOnRadioButton,
                lightOffRadioButton, colorLabel, lightColorPicker, sendLightStateEventButton);

        addStatusArea(bodyPane);

        bodyPane.setStyle("-fx-border-color: white white white gray;");

        getChildren().add(bodyPane);
    }

    public void sendLightState() {
        final CurrentLightStateEvent.LightState lightState;

        if (lightOnRadioButton.isSelected()) {
            lightState = CurrentLightStateEvent.LightState.ON;
        } else {
            lightState = CurrentLightStateEvent.LightState.OFF;
        }

        final int brightness = 0; // TODO: Add support for brightness
        final HexColor hexColor = new HexColor(toHexString(lightColorPicker.getValue()));
        final CurrentLightStateEvent lightStateEvent = new CurrentLightStateEvent(
                light, lightState, brightness, hexColor);

        bezirk.sendEvent(lightStateEvent);

        appendStatus(String.format("Sending %s%n", lightStateEvent.toString()));
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void setLightColor(HexColor hexColor) {
        final Color lightColor = Color.web(hexColor.getHexString());

        Platform.runLater(() -> {
            lightBulbCircle.setFill(lightColor);
            lightBulbCircle.setOpacity(1);
            lightBulbCircle.setEffect(new GaussianBlur(20));
            lightOnRadioButton.setSelected(true);
            lightColorPicker.setDisable(false);
            lightColorPicker.setValue(lightColor);
        });
    }

    private void turnLightOff() {
        lightColorPicker.setDisable(true);
        lightBulbCircle.setOpacity(0);
        lightOffRadioButton.setSelected(true);
    }

    private void turnLightOn() {
        lightColorPicker.setDisable(false);
        lightBulbCircle.setOpacity(1);
        lightBulbCircle.setEffect(new GaussianBlur(20));
        lightBulbCircle.setFill(lightColorPicker.getValue());
        lightOnRadioButton.setSelected(true);
    }

    public void lightWasToggled() {
        final RadioButton selectedBtn = (RadioButton) lightStateToggleGroup.getSelectedToggle();
        if (selectedBtn.getText().equals("On")) {
            turnLightOn();
        } else {
            turnLightOff();
        }
    }

    public Light getLight() {
        return light;
    }
}
