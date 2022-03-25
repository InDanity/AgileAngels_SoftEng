package edu.wpi.agileAngels;

import java.awt.*;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Acontroller {

  @FXML private Button equipmentButton, labButton, homeButton, foodButton;
  @FXML private MenuButton mealDropdown;
  @FXML private TextField roomInput;
  @FXML private TextArea restrictions;
  @FXML private Label confirm;
  private String meal = "null";

  // Switches to a new scene depending on which button is pressed
  @FXML
  private void openScene(ActionEvent event) throws IOException {
    Stage stage;
    Parent root;

    // If the equipment request button on the default scene is pressed,
    // switch to the equipment scene
    if (event.getSource() == equipmentButton) {
      stage = (Stage) equipmentButton.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("views/equipment-view.fxml"));
    }
    // If the lab request button on the default scene is pressed,
    // switch to the lab scene
    else if (event.getSource() == labButton) {
      stage = (Stage) equipmentButton.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("views/lab-view.fxml"));
    } else if (event.getSource() == foodButton) {
      stage = (Stage) foodButton.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("views/mealRequest-view.fxml"));
    }
    // If the home button is pressed, switch to the default scene
    else {
      stage = (Stage) homeButton.getScene().getWindow();
      root = FXMLLoader.load(getClass().getResource("views/default-view.fxml"));
    }
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  private void setChicken() {
    mealDropdown.setText("Chicken");
    meal = "Chicken";
  }

  @FXML
  private void setSteak() {
    mealDropdown.setText("steak");
    meal = "Steak";
  }

  @FXML
  private void setSalad() {
    mealDropdown.setText("Salad");
    meal = "Salad";
  }

  @FXML
  private void placeOrder() {
    confirm.setText(
        "Order confirmed to room "
            + roomInput.getText()
            + " for "
            + meal
            + "\n"
            + " Special Instructions: "
            + restrictions.getText());
  }
}
