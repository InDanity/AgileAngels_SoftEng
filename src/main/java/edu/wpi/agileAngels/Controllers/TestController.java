package edu.wpi.agileAngels.Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

// test button on the front end
public class TestController extends MainController implements Initializable {

  @FXML Button button;

  @FXML Circle circle1, circle2, circle3;

  ArrayList<Circle> circles = new ArrayList<>();

  private double x;
  private double y;

  Circle closest = null;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    button.setOnMouseDragged(
        (MouseEvent mouseEvent) -> {
          button.setLayoutX(mouseEvent.getSceneX() + x);
          button.setLayoutY(mouseEvent.getSceneY() + y);
        });

    circles.add(circle1);
    circles.add(circle2);
    circles.add(circle3);
  }

  public void buttonPreessed(MouseEvent mouseEvent) {
    x = button.getLayoutX() - mouseEvent.getSceneX();
    y = button.getLayoutY() - mouseEvent.getSceneY();
  }

  public void buttonRelease(MouseEvent mouseEvent) {

    double dist = 0;

    for (Circle circle : circles) {
      if ((dist(button, circle) < dist) || dist == 0) {
        closest = circle;
        dist = dist(button, circle);
      }
    }

    button.setLayoutX(closest.getLayoutX());
    button.setLayoutY(closest.getLayoutY());
  }

  double dist(Node node1, Node node2) {
    return Math.sqrt(
        Math.pow((node1.getLayoutX() - node2.getLayoutX()), 2)
            + Math.pow((node1.getLayoutY() - node2.getLayoutY()), 2));
  }
}
