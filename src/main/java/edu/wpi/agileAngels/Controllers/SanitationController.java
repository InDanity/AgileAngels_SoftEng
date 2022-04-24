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

public class SanitationController implements Initializable, PropertyChangeListener {

  @FXML Pane popOut;
  @FXML MenuButton saniID, saniLocation, saniEmployee, saniStatus, saniType;
  @FXML Button modifyButton, cancelRequest, submitRequest, clearRequest, deleteRequest;
  @FXML TableView SaniTable;
  @FXML
  private TableColumn nameColumn,
      typeColumn,
      locationColumn,
      employeeColumn,
      statusColumn,
      descriptionColumn; // , availableColumn,;
  @FXML TextField saniDescription, employeeFilterField, statusFilterField;
  @FXML Label notStartedNumber, inProgressNumber, completedNumber;

  private LocationDAOImpl locDAO = LocationDAOImpl.getInstance();
  private EmployeeManager empDAO = EmployeeManager.getInstance();
  private RequestDAOImpl saniRequestImpl = RequestDAOImpl.getInstance("SanitationRequest");
  private HashMap<String, Location> locationsHash = locDAO.getAllLocations();
  private ArrayList<Location> locationsList = new ArrayList<>(locationsHash.values());
  private HashMap<String, Employee> employeeHash = empDAO.getAllEmployees();
  private static ObservableList<Request> saniData = FXCollections.observableArrayList();
  AppController appController = AppController.getInstance();
  HashMap<String, String> locationIDsByLongName = new HashMap<>();

  private int statusNotStarted, statusInProgress, statusComplete;

  public SanitationController() throws SQLException {}

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    appController.addPropertyChangeListener(this);
    popOut.setVisible(false);
    statusNotStarted = 0;
    statusInProgress = 0;
    statusComplete = 0;

    for (Location loc : locationsHash.values()) {
      locationIDsByLongName.put(loc.getLongName(), loc.getNodeID());
    }

    nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
    employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));
    locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    // availableColumn.setCellValueFactory(new PropertyValueFactory<>("attribute1"));

    saniData.clear();
    for (Map.Entry<String, Request> entry : saniRequestImpl.getAllRequests().entrySet()) {
      Request req = entry.getValue();
      saniData.add(req);
    }
    dashboardLoad();
    SaniTable.setItems(saniData);
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
    if (saniLocation.getItems().size() == 0) {
      // Populates locations dropdown
      for (Location loc : locationsList) {
        MenuItem item = new MenuItem(loc.getLongName());
        item.setOnAction(this::mealLocationMenu);
        saniLocation.getItems().add(item);
      }

      // Populates employees dropdown
      for (Map.Entry<String, Employee> entry : employeeHash.entrySet()) {
        Employee emp = entry.getValue();
        MenuItem item = new MenuItem(emp.getName());
        item.setOnAction(this::mealEmployeeMenu);
        saniEmployee.getItems().add(item);
      }

      // Populates ID dropdown
      for (Request req : saniData) {
        MenuItem item = new MenuItem(req.getName());
        item.setOnAction(this::mealIDMenu);
        saniID.getItems().add(item);
      }
      MenuItem item1 = new MenuItem("Add New Request");
      item1.setOnAction(this::mealIDMenu);
      saniID.getItems().add(item1);
    }
  }

  @FXML
  public void submit(ActionEvent event) {
    String loc = locationIDsByLongName.get(saniLocation.getText());
    String emp = saniEmployee.getText();
    String stat = saniStatus.getText();
    String desc = saniDescription.getText();
    String type = saniType.getText();

    // Adding
    if (saniID.getText().equals("Add New Request")) {
      Request req =
          new Request(
              "", employeeHash.get(emp), locationsHash.get(loc), type, stat, desc, "N/A", "N/A");
      saniData.add(req);
      saniRequestImpl.addRequest(req);

      saniID.getItems().remove(0, saniID.getItems().size());
      // Populates ID dropdown
      for (Request request : saniData) {
        MenuItem item = new MenuItem(request.getName());
        item.setOnAction(this::mealIDMenu);
        saniID.getItems().add(item);
      }
      MenuItem item1 = new MenuItem("Add New Request");
      item1.setOnAction(this::mealIDMenu);
      saniID.getItems().add(item1);
      updateDashAdding(stat);
    } else { // Editing
      Request req = saniRequestImpl.getAllRequests().get(saniID.getText());
      if (!req.getLocation().getNodeID().equals(loc)) {
        Location newLoc = locationsHash.get(loc);
        saniRequestImpl.updateLocation(req, newLoc);
      }
      if (!req.getEmployee().getName().equals(emp)) {
        saniRequestImpl.updateEmployeeName(req, emp);
      }
      if (!req.getStatus().equals(stat)) {
        updateDashAdding(stat);
        updateDashSubtracting(req.getStatus());
        saniRequestImpl.updateStatus(req, stat);
      }
      if (!req.getDescription().equals(desc)) {
        saniRequestImpl.updateDescription(req, desc);
      }
      if (!req.getType().equals(type)) {
        saniRequestImpl.updateType(req, type);
      }

      for (int i = 0; i < saniData.size(); i++) {
        if (saniData.get(i).getName().equals(req.getName())) {
          saniData.set(i, req);
        }
      }
    }

    clear(event);
    popOut.setVisible(false);
  }

  @FXML
  public void cancel(ActionEvent event) {
    clear(event);
    popOut.setVisible(false);
  }

  @FXML
  public void delete(ActionEvent event) {
    String id = saniID.getText();
    updateDashSubtracting(saniRequestImpl.getAllRequests().get(id).getStatus());
    // removes the request from the table and dropdown
    for (int i = 0; i < saniData.size(); i++) {
      if (saniData.get(i).getName().equals(id)) {
        saniData.remove(i);
        saniID.getItems().remove(i);
      }
    }

    // delete from hash map and database table
    saniRequestImpl.deleteRequest(saniRequestImpl.getAllRequests().get(id));

    clear(event);
    popOut.setVisible(false);
  }

  @FXML
  public void clear(ActionEvent event) {
    saniID.setText("ID");
    saniType.setText("Type");
    saniEmployee.setText("Employee");
    saniLocation.setText("Location");
    saniStatus.setText("Status");
    saniDescription.setText("");
    saniDescription.setPromptText("Description");
  }

  @FXML
  public void mealIDMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    saniID.setText(button.getText());

    if (!button.getText().equals("Add New Request")) {
      populate(button.getText());
    }
  }

  @FXML
  public void mealLocationMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    saniLocation.setText(button.getText());
  }

  @FXML
  public void mealEmployeeMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    saniEmployee.setText(button.getText());
  }

  @FXML
  public void saniStatusMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    saniStatus.setText(button.getText());
  }

  @FXML
  public void saniTypeMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    saniType.setText(button.getText());
  }

  @FXML
  public void filterReqOnAction(ActionEvent event) {
    if (!employeeFilterField.getText().isEmpty() && !statusFilterField.getText().isEmpty()) {

      ObservableList<Request> empFilteredList = filterReqEmployee(employeeFilterField.getText());
      ObservableList<Request> trueFilteredList =
          filterFilteredReqListStatus(statusFilterField.getText(), empFilteredList);

      SaniTable.setItems(trueFilteredList);
    } else if (!employeeFilterField.getText().isEmpty()) {
      filterReqsTableEmployee(employeeFilterField.getText());
    } else if (!statusFilterField.getText().isEmpty()) {
      filterReqsTableStatus(statusFilterField.getText());
    }
  }

  @FXML
  public void clearFilters(ActionEvent event) {
    SaniTable.setItems(saniData);
    employeeFilterField.clear();
    statusFilterField.clear();
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

      Iterator var3 = saniRequestImpl.getAllRequests().entrySet().iterator();
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
    SaniTable.setItems(filteredList);
  }

  /**
   * Filters out requests in mealData based on the given Employee.
   *
   * @param employeeName The Employee that the requests must have to be in the new list.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterReqEmployee(String employeeName) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : saniData) {
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
    SaniTable.setItems(filteredList);
  }

  /**
   * Filters out requests in mealData based on the given status.
   *
   * @param reqStatus The status that the requests must have to be in the new list.
   * @return The new filtered list.
   */
  private ObservableList<Request> filterReqStatus(String reqStatus) {
    ObservableList<Request> newList = FXCollections.observableArrayList();

    for (Request req : saniData) {
      if (req.getStatus().equals(reqStatus)) {
        newList.add(req);
      }
    }
    return newList;
  }

  /**
   * Filters out requests in mealData based on the given status.
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
   * Filters out requests in mealData based on the given Employee.
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
    Request req = saniRequestImpl.getAllRequests().get(id);
    saniLocation.setText(req.getLocation().getLongName());
    saniEmployee.setText(req.getEmployee().getName());
    saniStatus.setText(req.getStatus());
    saniDescription.setText(req.getDescription());
    saniType.setText(req.getType());
  }

  private void updateDashAdding(String status) {
    if (status.equals("not started")
        || status.equals("Not Started")
        || status.equals("notStarted")) {
      statusNotStarted++;
    }
    if (status.equals("in progress")
        || status.equals("In Progress")
        || status.equals("inProgress")) {
      statusInProgress++;
    }
    if (status.equals("complete") || status.equals("Complete")) {
      statusComplete++;
    }
    setDashboard(statusNotStarted, statusInProgress, statusComplete);
  }

  private void updateDashSubtracting(String status) {
    if (status.equals("not started")
        || status.equals("Not Started")
        || status.equals("notStarted")) {
      statusNotStarted--;
    }
    if (status.equals("in progress")
        || status.equals("In Progress")
        || status.equals("inProgress")) {
      statusInProgress--;
    }
    if (status.equals("complete") || status.equals("Complete")) {
      statusComplete--;
    }
    setDashboard(statusNotStarted, statusInProgress, statusComplete);
  }
}
