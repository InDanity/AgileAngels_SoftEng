package edu.wpi.agileAngels.Controllers;

import edu.wpi.agileAngels.Database.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

// similar to equip controller
public class LabController extends MainController implements Initializable {

  @FXML
  private TextField labTestLocation, labEmployeeText, labStatus, labDelete, labEdit, labDescription;

  private RequestDAOImpl LabDAO = RequestDAOImpl.getInstance("LabRequest");
  private LocationDAOImpl locDAO = LocationDAOImpl.getInstance();
  private HashMap<String, Employee> employeeHashMap = new HashMap<>();
  private EmployeeManager empDAO = EmployeeManager.getInstance();
  private static ObservableList<Request> labData = FXCollections.observableArrayList();
  private int statusNotStarted, statusInProgress, statusComplete;

  AppController appController = AppController.getInstance();

  @FXML private TableView labTable;
  @FXML
  private TableColumn nameColumn,
      availableColumn,
      typeColumn,
      locationColumn,
      employeeColumn,
      statusColumn,
      descriptionColumn;
  @FXML
  private Label labTestConfirmation,
      dropdownButtonText,
      completedLabel,
      inProgressLabel,
      notStartedNumber,
      inProgressNumber,
      completedNumber,
      bloodLabel,
      urineLabel,
      tumorLabel,
      covidLabel;

  public LabController() throws SQLException {}

  /**
   * Will check if the table is empty and if so will populate it.Otherwise, just calls upon the
   * database for the data.
   *
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    statusNotStarted = 0;
    statusInProgress = 0;
    statusComplete = 0;
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    locDAO.getAllLocations();
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
    employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));
    locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    availableColumn.setCellValueFactory(new PropertyValueFactory<>("attribute1"));
    if (labData.isEmpty()) {
      System.out.println("THE TABLE IS CURRENTLY EMPTY I WILL POPuLATE");
      LabDAO.csvRead();
      Iterator var3 = LabDAO.getAllRequests().entrySet().iterator();

      for (Map.Entry<String, Request> entry : LabDAO.getAllRequests().entrySet()) {
        Request req = entry.getValue();

        labData.add(req);
        if (entry.getValue().getStatus().equals("inProgress")) {
          statusInProgress++;
        }
        if (entry.getValue().getStatus().equals("notStarted")
            || entry.getValue().getStatus().equals("Not Started")) {
          statusNotStarted++;
        }
        if (entry.getValue().getStatus().equals("Complete")
            || entry.getValue().getStatus().equals("complete")) {
          statusComplete++;
        }
        System.out.println(entry.getValue().getStatus());
      }
      // System.out.println("I'm gay");
      setDashboard(statusNotStarted, statusInProgress, statusComplete);
    }
    if (notStartedNumber.getText().equals("-")
        && inProgressNumber.getText().equals("-")
        && completedNumber.getText().equals("-")) {
      System.out.println("THE NUMBERS ARE EMPTY, RELEASE THE HOUNDS");
      LabDAO.csvRead();
      Iterator var3 = LabDAO.getAllRequests().entrySet().iterator();
      while (var3.hasNext()) {
        Map.Entry<String, Request> entry = (Map.Entry) var3.next();
        Request object = (Request) entry.getValue();
        if (entry.getValue().getStatus().equals("Progress")) {
          statusInProgress++;
        }
        if (entry.getValue().getStatus().equals("NotStarted")) {
          statusNotStarted++;
        }
        if (entry.getValue().getStatus().equals("Complete")
            || entry.getValue().getStatus().equals("complete")) {
          statusComplete++;
        }
        System.out.println(entry.getValue().getStatus());
      }
      // System.out.println("I'm gay");
      setDashboard(statusNotStarted, statusInProgress, statusComplete);
    }
    labTable.setItems(labData);
  }

  /**
   * Will set the dashboard's numbers to the certain types of status's.
   *
   * @param notStarted
   * @param inProgress
   * @param complete
   */
  @FXML
  private void setDashboard(int notStarted, int inProgress, int complete) {
    String notStart = Integer.toString(notStarted);
    String inProg = Integer.toString(inProgress);
    String comp = Integer.toString(complete);
    // Should put the numbers on the not started area on the dashboard.
    notStartedNumber.setText(notStart); // perhaps string value?
    // Should put the numbers on the in progress area of dash.
    inProgressNumber.setText(inProg);
    // Should put the numbers of the completed statuses into dash.
    completedNumber.setText(comp);
  }

  @FXML
  private void submitLabTest() {
    // String dropDown = dropdownButtonText.getText();
    String dropDown = "test";
    String location = labTestLocation.getText();
    String employee = labEmployeeText.getText();
    String status = labStatus.getText();
    String delete = labDelete.getText();
    String edit = labEdit.getText();
    String description = labDescription.getText();
    //  boolean logic = (dropDown.isEmpty() || location.isEmpty() || employee.isEmpty());
    if (!delete.isEmpty()) {
      deleteLabRequest(delete);
    } else if (!labEdit.getText().isEmpty()) {
      editLabRequest(dropDown, location, employee, status, description);
    } else {
      System.out.println(locDAO.getLocation(location) + " " + empDAO.getEmployee(employee));
      addLabRequest(
          "available",
          dropDown,
          locDAO.getLocation(location),
          empDAO.getEmployee(employee),
          status,
          description);
    }
  }

  /**
   * Removes requests off the UI and the database.
   *
   * @param deleteString
   */
  private void deleteLabRequest(String deleteString) {
    if (!deleteString.isEmpty()) {
      System.out.println("DELETE REQUEST");
      for (int i = 0; i < labData.size(); i++) {
        Request object = labData.get(i);
        if (0 == deleteString.compareTo(object.getName())) {
          labData.remove(i);
          LabDAO.deleteRequest(object);
        }
      }
      labTable.setItems(labData);

      String status = LabDAO.getAllRequests().get(deleteString).getStatus();
      if (status.equals("inProgress")) {
        statusInProgress--;
      }
      if (status.equals("complete")) {
        statusComplete--;
      }
      if (status.equals("notStarted")) {
        statusNotStarted--;
      }
      setDashboard(statusNotStarted, statusInProgress, statusComplete);
    }
  }

  /**
   * Add method for labrequest, will add information onto the UI and database within here and set
   * the confirmation text to display for the user.
   *
   * @param available
   * @param dropDown
   * @param location
   * @param employee
   * @param status
   */
  private void addLabRequest(
      String available,
      String dropDown,
      Location location,
      Employee employee,
      String status,
      String description) {
    labTestConfirmation.setText(
        "Thank you! Your "
            + dropDown
            + " you requested will be delivered shortly to "
            + location.getLongName()
            + " by "
            + employee.getName()
            + ".");

    Request request =
        new Request("", employee, location, dropDown, status, description, "available", "");

    LabDAO.addRequest(request);
    labData.add(request);
    labTable.setItems(labData);
    if (status.equals("inProgress")) {
      statusInProgress++;
    }
    if (status.equals("complete")) {
      statusComplete++;
    }
    if (status.equals("notStarted")) {
      statusNotStarted++;
    }
    setDashboard(statusNotStarted, statusInProgress, statusComplete);
  }

  private void editLabRequest(
      String dropDown, String location, String employee, String status, String description) {
    Request found = null;
    int num = 0;
    for (int i = 0; i < labData.size(); i++) {
      Request device = labData.get(i);
      if (0 == labEdit.getText().compareTo(device.getName())) {
        found = device;
        num = i;
      }
    }
    Employee emp = empDAO.getEmployee(employee);
    Location loc = locDAO.getLocation(location);

    if (found != null) {
      if (!dropDown.isEmpty()) {
        found.setType(dropDown);
        // LabDAO.updateType(found, dropDown);
      }
      if (!location.isEmpty()) {
        // Location loc = locDAO.getLocation(location);
        found.setLocation(loc);
        // LabDAO.updateLocation(found, loc);
      }
      if (!employee.isEmpty()) {
        found.setEmployee(empDAO.getEmployee(employee));
        // LabDAO.updateEmployeeName(found, employee); // uhhh will this work?
      }
      if (!status.isEmpty()) {
        found.setStatus(employee);
        // LabDAO.updateStatus(found, status);
      }

      if (!description.isEmpty()) { // New description field.
        found.setDescription(description);
        LabDAO.updateDescription(found, description);
      }
      labData.set(num, found);
      // Request found = null;
      // int num = 0;
      for (int i = 0; i < labData.size(); i++) {
        Request device = labData.get(i);
        if (0 == labEdit.getText().compareTo(device.getName())) {
          found = device;
          num = i;
        }
      }
      if (found != null) {
        if (!dropdownButtonText.getText().isEmpty()) {
          String type = dropdownButtonText.getText();
          found.setType(type);
          LabDAO.updateType(found, type);
        }
        if (!labTestLocation.getText().isEmpty()) {
          // Location loc = locDAO.getLocation(location);
          found.setLocation(loc);
          LabDAO.updateLocation(found, loc);
        }
        if (!labEmployeeText.getText().isEmpty()) {
          // Employee emp = empDAO.getEmployee(employee);
          found.setEmployee(emp);
          LabDAO.updateEmployeeName(found, employee);
        }
        if (!labDescription.getText().isEmpty()) { // New Description for whatever this part is.
          found.setDescription(description);
          found.setDescription(description); // I am unsure if this is correct.
        }
        labData.set(num, found);

        labTable.setItems(labData);
      }

    } else {
      labTestConfirmation.setText(
          "Thank you! Your "
              + dropdownButtonText.getText()
              + " you requested will be delivered shortly to "
              + labTestLocation.getText()
              + " by "
              + labEmployeeText.getText()
              + ".");

      Request request = new Request("", emp, loc, dropDown, status, "", "", "");

      LabDAO.addRequest(request);

      labData.add(request);

      labTable.setItems(labData);
    }
  }

  public void clearPage(ActionEvent actionEvent) {
    appController.clearPage();
  }
}
