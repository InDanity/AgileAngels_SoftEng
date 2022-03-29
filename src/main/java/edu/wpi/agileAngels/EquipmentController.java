package edu.wpi.agileAngels;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EquipmentController extends MainController {
  @FXML private MenuButton eqptDropdown;
  @FXML private MenuItem bed, recliner, xray, infusion;
  @FXML private TextField equipLocation;
  @FXML private Label equipmentConfirmation;

  @FXML
  private void submitEquipment() {
    equipmentConfirmation.setText(
        "Thank you, the "
            + eqptDropdown.getText()
            + " you requested will be delivered shortly to "
            + equipLocation.getText()
            + ".");
  }

  @FXML
  private void setEquipmentType(ActionEvent event) throws IOException {
    if (event.getSource() == bed) {
      eqptDropdown.setText("Bed");
    }
    if (event.getSource() == recliner) {
      eqptDropdown.setText("Recliner");
    }
    if (event.getSource() == xray) {
      eqptDropdown.setText("X-Ray Machine");
    }
    if (event.getSource() == infusion) {
      eqptDropdown.setText("Infusion Pump");
    }
  }

  @FXML
  private void clearPage() throws IOException {

    Stage stage;
    Parent root;

    stage = (Stage) homeButton.getScene().getWindow();
    root = FXMLLoader.load(getClass().getResource("views/equipment-view.fxml"));

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
