package edu.wpi.agileAngels.Controllers;

import edu.wpi.agileAngels.Database.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javafx.scene.layout.Pane;

public class PatientTransportController extends MainController
    implements Initializable, PropertyChangeListener {

  @FXML Pane popOut;
  @FXML
  MenuButton transportID,
      transportLocation,
      transportEmployee,
      transportStatus,
      transportType,
      transportDestination;
  @FXML Button modifyButton, cancelRequest, submitRequest, clearRequest, deleteRequest;
  @FXML TableView transportTable;
  @FXML
  private TableColumn nameColumn,
      destinationColumn,
      typeColumn,
      locationColumn,
      employeeColumn,
      statusColumn,
      descriptionColumn;
  @FXML TextField transportDescription, employeeFilterField, statusFilterField;
  @FXML Label notStartedNumber, inProgressNumber, completedNumber;

  // DAOs, HashMaps, and Lists required for functionality

  private LocationDAOImpl locDAO = LocationDAOImpl.getInstance();
  private EmployeeManager empDAO = EmployeeManager.getInstance();
  private RequestDAOImpl mainRequestImpl =
      RequestDAOImpl.getInstance("TransportRequest"); // looks sus
  private HashMap<String, Location> locationsHash = locDAO.getAllLocations();
  private ArrayList<Location> locationsList = new ArrayList<>(locationsHash.values());
  private HashMap<String, Employee> employeeHash = empDAO.getAllEmployees();
  private static ObservableList<Request> transportData = FXCollections.observableArrayList();
  private int statusNotStarted, statusInProgress, statusComplete;
  private AppController appController = AppController.getInstance();

  public PatientTransportController() throws SQLException {}

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    appController.addPropertyChangeListener(this);
    popOut.setVisible(false);
    statusNotStarted = 0;
    statusInProgress = 0;
    statusComplete = 0;

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
    employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));
    locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    destinationColumn.setCellValueFactory(
        new PropertyValueFactory<>("attribute2")); // location 2 looks sus again

    // Populates the table from UI list
    if (transportData.isEmpty()) {
      for (Map.Entry<String, Request> entry : mainRequestImpl.getAllRequests().entrySet()) {
        Request req = entry.getValue();
        transportData.add(req);
      }
    }
    dashboardLoad();
    transportTable.setItems(transportData);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    String changeType = evt.getPropertyName();
    int newValue = (int) evt.getNewValue();
    appController.displayAlert();
  }

  @FXML
  public void modifyRequest(ActionEvent event) {
    popOut.setVisible(true);

    if (transportLocation.getItems().size() == 0) {
      // Populates locations dropdown
      for (Location loc : locationsList) {
        MenuItem item = new MenuItem(loc.getNodeID());
        item.setOnAction(this::mainLocationMenu);
        transportLocation.getItems().add(item);
      }
      // Populates destinations dropdown
      for (Location dest : locationsList) {
        MenuItem item = new MenuItem(dest.getNodeID());
        item.setOnAction(this::mainDestinationMenu);
        transportDestination.getItems().add(item);
      }

      // Populates employees dropdown
      for (Map.Entry<String, Employee> entry : employeeHash.entrySet()) {
        Employee emp = entry.getValue();
        MenuItem item = new MenuItem(emp.getName());
        item.setOnAction(this::mainEmployeeMenu);
        transportEmployee.getItems().add(item);
      }

      // Populates ID dropdown
      for (Request req : transportData) {
        MenuItem item = new MenuItem(req.getName());
        item.setOnAction(this::mainIDMenu);
        transportID.getItems().add(item);
      }
      MenuItem item1 = new MenuItem("Add New Request");
      item1.setOnAction(this::mainIDMenu);
      transportID.getItems().add(item1);
    }
  }

  @FXML
  public void submit(ActionEvent event) {
    String loc = transportLocation.getText();
    String dest = transportDestination.getText();
    String emp = transportEmployee.getText();
    String stat = transportStatus.getText();
    String desc = transportDescription.getText();
    String type = transportType.getText();
    Location desty = locDAO.getLocation(dest);
    // Adding
    if (transportID.getText().equals("Add New Request")) {
      Request req =
          new Request(
              "",
              employeeHash.get(emp),
              locationsHash.get(loc),
              type,
              stat,
              desc,
              "N/A",
              desty.getLongName());
      transportData.add(req);
      mainRequestImpl.addRequest(req);

      transportID.getItems().remove(0, transportID.getItems().size());
      // Populates ID dropdown
      for (Request request : transportData) {
        MenuItem item = new MenuItem(request.getName());
        item.setOnAction(this::mainIDMenu);
        transportID.getItems().add(item);
      }
      MenuItem item1 = new MenuItem("Add New Request");
      item1.setOnAction(this::mainIDMenu);
      transportID.getItems().add(item1);

    } else { // Editing
      Request req = mainRequestImpl.getAllRequests().get(transportID.getText());
      if (!req.getLocation().getNodeID().equals(loc)) {
        Location newLoc = locationsHash.get(loc);
        mainRequestImpl.updateLocation(req, newLoc);
      }
      if (!req.getAttribute2().equals(dest)) {
        mainRequestImpl.updateAttribute2(req, dest);
      }
      if (!req.getEmployee().getName().equals(emp)) {
        mainRequestImpl.updateEmployeeName(req, emp);
      }
      if (!req.getStatus().equals(stat)) {
        mainRequestImpl.updateStatus(req, stat);
      }
      if (!req.getDescription().equals(desc)) {
        mainRequestImpl.updateDescription(req, desc);
      }
      if (!req.getType().equals(desc)) {
        mainRequestImpl.updateType(req, type);
      }
      for (int i = 0; i < transportData.size(); i++) {
        if (transportData.get(i).getName().equals(req.getName())) {
          transportData.set(i, req);
        }
      }
    }

    clear(event);
  }

  @FXML
  public void cancel(ActionEvent event) {
    clear(event);
    popOut.setVisible(false);
  }

  @FXML
  public void delete(ActionEvent event) {
    String id = transportID.getText();

    // removes the request from the table and dropdown
    for (int i = 0; i < transportData.size(); i++) {
      if (transportData.get(i).getName().equals(id)) {
        transportData.remove(i);
        transportID.getItems().remove(i);
      }
    }

    // delete from hash map and database table
    mainRequestImpl.deleteRequest(mainRequestImpl.getAllRequests().get(id));

    clear(event);
  }

  @FXML
  public void clear(ActionEvent event) {
    transportID.setText("ID");
    transportLocation.setText("Location");
    transportEmployee.setText("Employee");
    transportStatus.setText("Status");
    transportDescription.setText("");
    transportType.setText("Type");
    transportDestination.setText("Destination");
  }

  /** Does filterReqsTable methods when "Submit Filters" is clicked, or "onAction." */
  @FXML
  public void filterReqOnAction(ActionEvent event) {
    if (!employeeFilterField.getText().isEmpty() && !statusFilterField.getText().isEmpty()) {

      ObservableList<Request> empFilteredList = filterReqEmployee(employeeFilterField.getText());
      ObservableList<Request> trueFilteredList =
          filterFilteredReqListStatus(statusFilterField.getText(), empFilteredList);

      transportTable.setItems(trueFilteredList);
    } else if (!employeeFilterField.getText().isEmpty()) {
      filterReqsTableEmployee(employeeFilterField.getText());
    } else if (!statusFilterField.getText().isEmpty()) {
      filterReqsTableStatus(statusFilterField.getText());
    }
  }

  @FXML
  public void clearFilters(ActionEvent event) {
    transportTable.setItems(transportData);
  }

  @FXML
  public void mainIDMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportID.setText(button.getText());
    // If editing or deleting an existing request:
    if (!button.getText().equals("Add New Request")) {
      populate(button.getText());
    }
  }

  @FXML
  public void mainLocationMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportLocation.setText(button.getText());
  }

  @FXML
  public void mainEmployeeMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportEmployee.setText(button.getText());
  }

  @FXML
  public void mainStatusMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportStatus.setText(button.getText());
  }

  @FXML
  public void mainTypeMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportType.setText(button.getText());
  }

  @FXML
  public void mainDestinationMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    transportDestination.setText(button.getText());
  }

  /**
   * This is the cleaner version of Justin's dashboard code. Note that it may need a for loop as
   * shown on line 83/84 if used elsewhere. Note: Unlikely.
   */
  @FXML
  private void dashboardLoad() {
    if (notStartedNumber.getText().equals("-")
        && inProgressNumber.getText().equals("-")
        && completedNumber.getText().equals("-")) {

      Iterator var3 = mainRequestImpl.getAllRequests().entrySet().iterator();
      while (var3.hasNext()) {
        Map.Entry<String, Request> entry = (Map.Entry) var3.next();
        Request object = (Request) entry.getValue();
        if (entry.getValue().getStatus().equals("inProgress")) {
          statusInProgress++;
        }
        if (entry.getValue().getStatus().equals("notStarted")) {
          statusNotStarted++;
        }
        if (entry.getValue().getStatus().equals("Complete")
            || entry.getValue().getStatus().equals("complete")) {
          statusComplete++;
        }
      }
      setDashboard(statusNotStarted, statusInProgress, statusComplete);
    }
  }

  /**
   * Will set the dashboard's numbers to the certain types of statuses.
   *
   * @param notStarted Requests not started
   * @param inProgress Requests in progress
   * @param complete Requests completed
   */
  @FXML
  private void setDashboard(int notStarted, int inProgress, int complete) {
    String notStart = Integer.toString(notStarted);
    String inProg = Integer.toString(inProgress);
    String comp = Integer.toString(complete);
    notStartedNumber.setText(notStart);
    inProgressNumber.setText(inProg);
    completedNumber.setText(comp);
  }

  /**
   * Filters requests in the equipment table so only those with the given Employee remain.
   *
   * @param employeeName The Employee the requests must have to remain on the table.
   */
  private void filterReqsTableEmployee(String employeeName) {
    ObservableList<Request> filteredList = filterReqEmployee(employeeName);

    // Sets table to only have contents of the filtered list.
    transportTable.setItems(filteredList);
  }

  /**
   * Filters out requests in labData based on the given Employee.
   *
   * @param employeeName The Employee that the requests must have to be in the new list.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterReqEmployee(String employeeName) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : transportData) {
      if (req.getEmployee().getName().equals(employeeName)) {
        newList.add(req);
      }
    }

    return newList;
  }

  /**
   * Filters requests in the maintenance table so only those with the given status remain.
   *
   * @param reqStatus The status the requests must have to remain on the table.
   */
  private void filterReqsTableStatus(String reqStatus) {
    ObservableList<Request> filteredList = filterReqStatus(reqStatus);
    // Sets table to only have contents of the filtered list.
    transportTable.setItems(filteredList);
  }

  /**
   * Filters out requests in mainData based on the given status.
   *
   * @param reqStatus The status that the requests must have to be in the new list.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterReqStatus(String reqStatus) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : transportData) {
      if (req.getStatus().equals(reqStatus)) {
        newList.add(req);
      }
    }
    return newList;
  }

  /* Methods to filter lists n times */

  /**
   * Filters out requests in mainData based on the given status.
   *
   * @param reqStatus The status that the requests must have to be in the new list.
   * @param filteredList The list that was presumably filtered.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterFilteredReqListStatus(
      String reqStatus, ObservableList<Request> filteredList) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : filteredList) {
      if (req.getStatus().equals(reqStatus)) {
        newList.add(req);
      }
    }
    return newList;
  }

  /**
   * Filters out requests in mainData based on the given Employee.
   *
   * @param employeeName The Employee that the requests must have to be in the new list.
   * @param filteredList The list that was presumably filtered.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterFilteredReqListEmployee(
      String employeeName, ObservableList<Request> filteredList) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : filteredList) {
      if (req.getEmployee().getName().equals(employeeName)) {
        newList.add(req);
      }
    }

    return newList;
  }

  /**
   * Populates fields once a node id is chosen when editing an existing request.
   *
   * @param id Request ID
   */
  private void populate(String id) {
    Request req = mainRequestImpl.getAllRequests().get(id);
    transportLocation.setText(req.getLocation().getNodeID());
    transportEmployee.setText(req.getEmployee().getName());
    transportStatus.setText(req.getStatus());
    transportDescription.setText(req.getDescription());
    transportType.setText(req.getType());
    transportDestination.setText(req.getAttribute2());
  }
}