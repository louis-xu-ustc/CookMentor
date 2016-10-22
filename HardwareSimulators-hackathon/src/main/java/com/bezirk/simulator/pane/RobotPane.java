package com.bezirk.simulator.pane;

import com.bezirk.hardwareevents.HexColor;
import com.bezirk.hardwareevents.robot.ChangeRobotColorEvent;
import com.bezirk.hardwareevents.robot.MoveRobotEvent;
import com.bezirk.hardwareevents.robot.Robot;
import com.bezirk.hardwareevents.robot.RobotEvent;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.util.UUID;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class RobotPane extends DevicePane {
    private final Rectangle robotArena = new Rectangle();
    private final Circle robotCircle = new Circle();
    private final Robot robot;

    String uuid = UUID.randomUUID().toString();
    String shortenedUUID = uuid.substring(0, Math.min(uuid.length(), 8)); //Using only up to first 8 chars for brevity.
    private final TextField robotIdTextField = new TextField(shortenedUUID);

    public RobotPane(Bezirk bezirk, Pane parent) {
        super(parent);

        layoutPane();


        this.robot = new Robot(shortenedUUID, "bezirk.simulator.robot");

        final EventSet eventSet = new EventSet(MoveRobotEvent.class, ChangeRobotColorEvent.class);

        eventSet.setEventReceiver((Event event, ZirkEndPoint sender) -> {
            if (event instanceof RobotEvent) {
                final Robot robot = ((RobotEvent) event).getRobot();

                if (!this.robot.equals(robot)) return;

                if (event instanceof MoveRobotEvent) {
                    final MoveRobotEvent moveRobotEvent = (MoveRobotEvent) event;
                    moveRobot(moveRobotEvent.getHeading(), moveRobotEvent.getSpeed(),
                            moveRobotEvent.getDuration());
                } else if (event instanceof ChangeRobotColorEvent) {
                    final ChangeRobotColorEvent robotColorEvent = (ChangeRobotColorEvent) event;
                    Platform.runLater(() -> changeRobotColor(robotColorEvent.getColor()));
                }

                appendStatus(String.format("Received %s%n", event.toString()));

            }
        });

        bezirk.subscribe(eventSet);
    }

    private void layoutPane() {
        setStyle("-fx-border-color: white gray gray gray;");

        addTitlePane("Robot");


        final Label idLabel = new Label("Robot ID");
        idLabel.setLayoutX(38);
        idLabel.setLayoutY(45);
        idLabel.setFont(LABEL_FONT);

        robotIdTextField.setLayoutX(38);
        robotIdTextField.setLayoutY(75);
        robotIdTextField.setPrefSize(111, 25);
        robotIdTextField.setEditable(false);

        robotArena.setArcHeight(5);
        robotArena.setArcWidth(5);
        robotArena.setFill(Color.WHITE);
        robotArena.setLayoutX(186);
        robotArena.setLayoutY(21);
        robotArena.setStroke(Color.valueOf("#bfbfbf"));
        robotArena.setStrokeType(StrokeType.INSIDE);
        robotArena.setWidth(452);
        robotArena.setHeight(322);

        robotCircle.setFill(Color.valueOf("#ff851f"));
        robotCircle.setRadius(13);
        robotCircle.setStroke(Color.BLACK);
        robotCircle.setStrokeType(StrokeType.INSIDE);
        robotCircle.setStrokeWidth(0);
        resetRobotPosition();

        /* These controls did not appear to be useful

        final Polygon upPolygon = createDirectionPolygon();
        upPolygon.setLayoutX(547);
        upPolygon.setLayoutY(121);
        upPolygon.setOnMouseClicked((event) -> moveRobotUp());

        final Polygon rightPolygon = createDirectionPolygon();
        rightPolygon.setLayoutX(582);
        rightPolygon.setLayoutY(157);
        rightPolygon.setRotate(90);
        rightPolygon.setOnMouseClicked((event) -> moveRobotRight());

        final Polygon downPolygon = createDirectionPolygon();
        downPolygon.setLayoutX(547);
        downPolygon.setLayoutY(195);
        downPolygon.setRotate(180);
        downPolygon.setOnMouseClicked((event) -> moveRobotDown());

        final Polygon leftPolygon = createDirectionPolygon();
        leftPolygon.setLayoutX(511);
        leftPolygon.setLayoutY(157);
        leftPolygon.setRotate(270);
        leftPolygon.setOnMouseClicked((event) -> moveRobotLeft());
        */

        final Button resetPositionButton = new Button("Reset Position");
        resetPositionButton.setLayoutX(652);
        resetPositionButton.setLayoutY(234);
        resetPositionButton.setOnAction((event) -> resetRobotPosition());



        final Pane bodyPane = new Pane(idLabel, robotIdTextField, robotArena, robotCircle, resetPositionButton);
        //final Pane bodyPane = new Pane(robotArena, robotCircle, upPolygon, rightPolygon, downPolygon,
        //       leftPolygon, resetPositionButton);

        addStatusArea(bodyPane);

        bodyPane.setStyle("-fx-border-color: white white white gray;");

        getChildren().add(bodyPane);
    }

    /*
    private Polygon createDirectionPolygon() {
        final Polygon directionPolygon = new Polygon(-50, 40, 50, 40, 0, -60);
        directionPolygon.setFill(Color.valueOf("#ff880098"));
        directionPolygon.setOpacity(0.06);
        directionPolygon.setScaleX(0.3);
        directionPolygon.setScaleY(0.3);
        directionPolygon.setStroke(Color.BLACK);
        directionPolygon.setStrokeType(StrokeType.INSIDE);
        directionPolygon.setStrokeWidth(0);

        return directionPolygon;
    }
    */

    private void moveRobotUp() {
        moveRobot(270, 10, 1000);
    }

    private void moveRobotDown() {
        moveRobot(90, 10, 1000);
    }

    private void moveRobotLeft() {
        moveRobot(180, 10, 1000);
    }

    private void moveRobotRight() {
        moveRobot(0, 10, 1000);
    }

    private void changeRobotColor(HexColor hexColor) {
        robotCircle.setFill(Color.web(hexColor.getHexString()));
    }

    private void moveRobot(double heading, double speed, double duration /* ms */) {
        if (speed == 0 || duration == 0) return;

        final double curX = robotCircle.getLayoutX();
        final double curY = robotCircle.getLayoutY();
        if (heading < 0) heading = 0;
        if (heading > 360) heading = 360;
        double theta = heading * Math.PI / 180;

        Platform.runLater(() -> {
            // Move speed pixels every half second until duration is reached
            int stepCount = (int) (duration / 500);

            final double newX = curX + speed * stepCount * Math.cos(theta);
            final double newY = curY + speed * stepCount * Math.sin(theta);

            robotCircle.setLayoutX(newX);
            robotCircle.setLayoutY(newY);

            checkBounds();
        });
    }


    private void resetRobotPosition() {
        final double fieldMidX = robotArena.getBoundsInParent().getMinX() + (robotArena.getWidth() / 2);
        final double fieldMidY = robotArena.getBoundsInParent().getMinY() + (robotArena.getHeight() / 2);
        robotCircle.relocate(fieldMidX - robotCircle.getRadius(), fieldMidY - robotCircle.getRadius());
    }

    private void checkBounds() {
        final double rightBorder = robotArena.getLayoutX() + robotArena.getWidth();
        final double leftBorder = robotArena.getLayoutX();
        final double topBorder = robotArena.getLayoutY();
        final double bottomBorder = robotArena.getLayoutY() + robotArena.getHeight();
        final double roboX = robotCircle.getLayoutX();
        final double roboY = robotCircle.getLayoutY();
        final double roboRadius = robotCircle.getRadius();
        boolean touchedEdge = false;

        if (roboX + roboRadius >= rightBorder) {
            robotCircle.setLayoutX(rightBorder - roboRadius);
            touchedEdge = true;
        }
        if (roboX - roboRadius <= leftBorder) {
            robotCircle.setLayoutX(leftBorder + roboRadius);
            touchedEdge = true;
        }
        if (roboY - roboRadius <= topBorder) {
            robotCircle.setLayoutY(topBorder + roboRadius);
            touchedEdge = true;
        }
        if (roboY + roboRadius >= bottomBorder) {
            robotCircle.setLayoutY(bottomBorder - roboRadius);

            touchedEdge = true;
        }

        if (touchedEdge) {
            robotArena.setStroke(Color.ORANGE);
        } else {
            robotArena.setStroke(Color.GRAY);
        }
    }

    public Robot getRobot() {
        return robot;
    }
}
