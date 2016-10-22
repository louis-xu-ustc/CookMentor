package com.bezirk.simulator.pane;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class DevicePane extends HBox {
    protected static final Font LABEL_FONT = new Font("System Bold", 12);

    private final Pane parent;
    private TextArea statusTextArea;
    private Optional<DeviceClosingListener> deviceClosingListener = Optional.empty();

    public DevicePane(Pane parent) {
        this.parent = parent;
    }

    private void close() {
        if (deviceClosingListener.isPresent())
            deviceClosingListener.get().deviceClosing();

        parent.getChildren().remove(this);
    }

    public void setDeviceClosingListener(DeviceClosingListener deviceClosingListener) {
        this.deviceClosingListener = Optional.ofNullable(deviceClosingListener);
    }

    protected void addTitlePane(String title) {
        final Label titleLabel = new Label(title);
        titleLabel.setFont(new Font("Arial Bold", 18));
        titleLabel.setPadding(new Insets(12, 12, 12, 12));

        final Pane titlePane = new Pane(titleLabel);
        titlePane.setPrefWidth(180);

        final Button closeDeviceButton = new Button("X");
        closeDeviceButton.setTextFill(Color.DARKRED);
        closeDeviceButton.setFont(new Font("System Bold", 16));

        closeDeviceButton.setOnAction((event) -> close());

        getChildren().addAll(titlePane, closeDeviceButton);
    }

    protected void addStatusArea(Pane bodyPane) {
        final Label statusLabel = new Label("Status");
        statusLabel.setLayoutX(780);
        statusLabel.setLayoutY(35);
        statusLabel.setFont(LABEL_FONT);

        statusTextArea = new TextArea();
        statusTextArea.setLayoutX(780);
        statusTextArea.setLayoutY(59);
        statusTextArea.setPrefSize(413, 93);
        statusTextArea.setEditable(false);
        statusTextArea.setWrapText(true);

        bodyPane.getChildren().addAll(statusLabel, statusTextArea);
    }

    protected void appendStatus(String status) {
        Platform.runLater(() -> statusTextArea.appendText(status));
    }
}
