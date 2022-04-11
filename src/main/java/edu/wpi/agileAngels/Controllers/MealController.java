package edu.wpi.agileAngels.Controllers;

import edu.wpi.agileAngels.Database.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MealController extends MainController {
  @FXML private Button mealDropdown, pasta, steak, burger, pizza;
  @FXML private TextField roomInput, mealEmployeeText, mealStatus, restrictions;
  @FXML private Label dropdownButtonText, confirm, pastaLabel, stealLabel, burgerLabel, pizzaLabel;
  @FXML Pane drop, drop2;

  private RequestDAOImpl mealDAO;
  private ObservableList mealList = FXCollections.observableArrayList();
  LocationDAOImpl locDAO = LocationDAOImpl.getInstance();
  private EmployeeManager empDAO = EmployeeManager.getInstance();

  public MealController() throws SQLException {}

  public void initialize(URL location, ResourceBundle resources) throws SQLException {
    HashMap<String, Request> mealData = new HashMap<String, Request>();
    // mealDAO = new RequestDAOImpl("./Meal.csv", mealData, 0);
  }

  @FXML
  private void submitMeal() {
    String dropDown = dropdownButtonText.getText();
    String location = roomInput.getText();
    Location loc = locDAO.getLocation(location);
    String employee = mealEmployeeText.getText();
    Employee emp = empDAO.getEmployee(employee);
    String status = mealStatus.getText();
    // String restrictions = restrictions.getText();
    if (dropdownButtonText.getText().isEmpty()
        || roomInput.getText().isEmpty()
        || dropdownButtonText.getText().isEmpty()) {
      confirm.setText("Please fill out all the required fields");
    } else {
      confirm.setText(
          "Your order "
              + dropDown
              + " will be delivered by "
              + emp
              + " to room "
              + loc
              + ". Special Instructions: "
              + restrictions.getText());

      Request request =
          new Request(
              "",
              empDAO.getEmployee(mealEmployeeText.getText()),
              locDAO.getLocation(roomInput.getText()),
              dropdownButtonText.getText(),
              mealStatus.getText(),
              restrictions.getText(),
              "",
              "");
      mealDAO.addRequest(request);
      mealList.add(request);
      // add table
    }
  }

  private void addMealRequest() {}
}
