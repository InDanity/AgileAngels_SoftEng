package edu.wpi.agileAngels.Controllers;

import edu.wpi.agileAngels.Database.Employee;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javax.swing.*;

public class MapsController implements Initializable, PropertyChangeListener {

  @FXML
  private ImageView floorTwoMap,
      floorThreeMap,
      floorFourMap,
      floorFiveMap,
      lowerLevelOneMap,
      lowerLevelTwoMap;

  @FXML private ScrollPane mapScroll;
  @FXML HBox addButtonBox;

  @FXML
  private Button locationEdit,
      locationDelete,
      requestEdit,
      requestDelete,
      switchModeButton,
      zoomIn,
      zoomOut,
      floorUp,
      floorDown;
  @FXML private MenuItem floorTwo, floorThree, floorFour, floorFive, lowerLevelOne, lowerLevelTwo;
  @FXML private TextField locationName, addLocationName;
  @FXML Pane mapPane, locationEditPane, requestEditPane, locationAddPane;
  @FXML AnchorPane anchor;
  @FXML Label requestName, floorLabel, nodeIDField, addNodeIDField;
  @FXML
  MenuButton locationTypeDropdown,
      requestTypeDropdown,
      requestStatusDropdown,
      requestEmployeeDropdown, addLocationTypeDropdown;

  public final ContextMenu contextMenu = new ContextMenu();
  MenuItem addNode = new MenuItem("Add Node");

  LocationNode currentLocationNode = null;
  RequestNode currentRequestNode = null;
  private String currentFloor = "2";
  EquipmentNode currentEquipmentNode = null;

  Pane pane2 = new Pane();
  Pane pane3 = new Pane();
  Pane pane4 = new Pane();
  Pane pane5 = new Pane();
  Pane paneL1 = new Pane();
  Pane paneL2 = new Pane();

  LocationNodeManager locationNodeManager = new LocationNodeManager(this);
  RequestNodeManager requestNodeManager = new RequestNodeManager(this);
  EquipmentNodeManager equipmentNodeManager = new EquipmentNodeManager(this);

  AppController appController = AppController.getInstance();

  public MapsController() throws SQLException {}

  private double scale = 1;

  public double panX = 0;
  public double panY = 0;

  double rightClickX;
  double rightClickY;

  private double croppedMapXOffset = 1054;
  private double croppedMapYOffset = 544;

  private double croppedMapWidth = 2000;

  private double imagePaneWidth = 1400;

  public double getCroppedMapXOffset() {
    return croppedMapXOffset;
  }

  public double getCroppedMapYOffset() {
    return croppedMapYOffset;
  }

  public double getCroppedMapWidth() {
    return croppedMapWidth;
  }

  public double getImagePaneWidth() {
    return imagePaneWidth;
  }

  /**
   * Called on page load, creates panes for each map, adds the images for each map to its pane, and
   * sets their initial visibility
   *
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    appController.addPropertyChangeListener(this);

    mapPane.getChildren().add(pane2);
    pane2.getChildren().add((floorTwoMap));
    pane2.setVisible(true);
    mapPane.getChildren().add(pane3);
    pane3.getChildren().add((floorThreeMap));
    pane3.setVisible(false);
    mapPane.getChildren().add(pane4);
    pane4.getChildren().add((floorFourMap));
    pane4.setVisible(false);
    mapPane.getChildren().add(pane5);
    pane5.getChildren().add((floorFiveMap));
    pane5.setVisible(false);
    mapPane.getChildren().add(paneL1);
    paneL1.getChildren().add((lowerLevelOneMap));
    paneL1.setVisible(false);
    paneL2.getChildren().add((lowerLevelTwoMap));
    mapPane.getChildren().add(paneL2);
    paneL2.setVisible(false);

    contextMenu.getItems().addAll(addNode);
    addNode.setOnAction((ActionEvent event) -> addNode());

    mapScroll.setOnMousePressed(
        (MouseEvent event) -> {
          if (event.isSecondaryButtonDown()) {
            contextMenu.show(mapScroll, event.getScreenX(), event.getScreenY());
            rightClickY = event.getSceneY();
            rightClickX= event.getSceneX();
          }
        });

    locationNodeManager.createNodesFromDB();
    try {
      requestNodeManager.createNodesFromDB();
      equipmentNodeManager.createNodesFromDB();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    for (Employee e : requestNodeManager.employeeHash.values()) {
      MenuItem menuItem = new MenuItem(e.getName());
      menuItem.setOnAction(
          (ActionEvent event) -> {
            MenuItem button = (MenuItem) event.getSource();
            requestEmployeeDropdown.setText(button.getText());
          });
      try {
        requestEmployeeDropdown.getItems().add(menuItem);
      } catch (NullPointerException e2) {

      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    String changeType = evt.getPropertyName();
    int newValue = (int) evt.getNewValue();
    appController.displayAlert();
  }

  /**
   * Populates the text fields on the page with data of a node
   *
   * @param locationNode the node whose data is populated
   */
  public void populateLocationNodeData(LocationNode locationNode) {
    requestEditPane.setVisible(false);
    locationEditPane.setVisible(true);
    nodeIDField.setText(locationNode.getNodeID());
    locationName.setText(locationNode.getName());
    locationTypeDropdown.setText(locationNode.getNodeType());
    currentLocationNode = locationNode;
  }

  public double getScale() {
    return scale;
  }

  /**
   * Populates the text fields on the page with data of a node
   *
   * @param requestNode the node whose data is populated
   */
  public void populateRequestNodeData(RequestNode requestNode) {
    requestEditPane.setVisible(true);
    locationEditPane.setVisible(false);
    requestTypeDropdown.setText(requestNode.getRequest().getType());
    requestName.setText(requestNode.getName());
    requestEmployeeDropdown.setText(requestNode.getEmployee());
    requestStatusDropdown.setText(requestNode.getStatus());
    currentRequestNode = requestNode;
  }

  /**
   * Populates the text fields on the page with data of a node
   *
   * @param equipmentNode the node whose data is populated
   */
  public void populateEquipmentNodeData(EquipmentNode equipmentNode) {
    // clean.setVisible(true);
    requestName.setText(equipmentNode.getID());
    locationName.setText(equipmentNode.getClean());
    requestTypeDropdown.setText(equipmentNode.getStatus());
    currentEquipmentNode = equipmentNode;
  }

  @FXML
  private void addNode() {
    locationEdit.setVisible(true);
  }

  /**
   * Edits the parameters of currentNode
   *
   * @throws IOException NumberFormatException from parseDouble
   */
  //  @FXML
  //  private void editNode() throws IOException {
  //    String name = locationName.getText();
  //    String type = locationTypeDropdown.getText();
  //    currentLocationNode.changeLocationName(name);
  //    currentLocationNode.changeLocationType(type);
  //    locationNodeManager.editNode(currentLocationNode, name, type);
  //    currentLocationNode.resetLocation();
  //    currentLocationNode = null;
  //  }
  @FXML
  public void locationEdit() {
    String name = locationName.getText();
    String type = locationTypeDropdown.getText();
    currentLocationNode.changeLocationName(name);
    currentLocationNode.changeLocationType(type);
    locationNodeManager.editNode(currentLocationNode, name, type);
    currentLocationNode.resetLocation();
    currentLocationNode = null;
  }

  @FXML
  public void requestEdit(ActionEvent event) {
    currentRequestNode.setEmployee(requestEmployeeDropdown.getText());
    currentRequestNode.getRequest().setStatus(requestStatusDropdown.getText());
    currentRequestNode.getRequest().setType(requestTypeDropdown.getText());
    currentRequestNode = null;
  }

  /**
   * Removes currentNode from its pane and calls NodeManager.deleteNode(), which removes the node
   * from the hashmap in NodeManager and the database
   */
  @FXML
  private void removeNode() {
    if (currentLocationNode.getFloor().equals("2")) {
      pane2.getChildren().remove(currentLocationNode.getButton());
    } else if (currentLocationNode.getFloor().equals("3")) {
      pane3.getChildren().remove(currentLocationNode.getButton());
    } else if (currentLocationNode.getFloor().equals("4")) {
      pane3.getChildren().remove(currentLocationNode.getButton());
    } else if (currentLocationNode.getFloor().equals("5")) {
      pane3.getChildren().remove(currentLocationNode.getButton());
    } else if (currentLocationNode.getFloor().equals("L1")) {
      paneL1.getChildren().remove(currentLocationNode.getButton());
    } else if (currentLocationNode.getFloor().equals("L2")) {
      paneL2.getChildren().remove(currentLocationNode.getButton());
    }
    locationNodeManager.deleteNode(currentLocationNode.getNodeID());
    clearFields();
  }

  /**
   * Switches between the "add a location" mode and the "edit or delete a location" mode on a button
   * press
   *
   * @param event the button that was pressed, which is either switchToAddButton or the
   *     switchToEditButton
   */

  /**
   * Adds the button for a location node to the pane corresponding to its floor
   *
   * @param node the node whose button is added to a pane
   */
  public void displayLocationNode(LocationNode node) {
    if (node.getFloor().equals("2")) {
      pane2.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("5")) {
      pane5.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("4")) {
      pane4.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("3")) {
      pane3.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L1")) {
      paneL1.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L2")) {
      paneL2.getChildren().add(node.getButton());
    }
  }

  /**
   * Adds the button for a request node to the pane corresponding to its floor
   *
   * @param node the node whose button is added to a pane
   */
  public void displayRequestNode(RequestNode node) {
    if (node.getFloor().equals("2")) {
      pane2.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("5")) {
      pane5.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("4")) {
      pane4.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("3")) {
      pane3.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L1")) {
      paneL1.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L2")) {
      paneL2.getChildren().add(node.getButton());
    }
  }

  /**
   * Adds the button for an equipment node to the pane corresponding to its floor
   *
   * @param node the node whose button is added to a pane
   */
  public void displayEquipmentNode(EquipmentNode node) {
    if (node.getFloor().equals("2")) {
      pane2.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("5")) {
      pane5.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("4")) {
      pane4.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("3")) {
      pane3.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L1")) {
      paneL1.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("L2")) {
      paneL2.getChildren().add(node.getButton());
    }
  }

  /**
   * Switches between the panes for each floor when the button for each floor is pressed
   *
   * @param event one of the floor buttons
   */
  public void changeMap(ActionEvent event) {
    pane2.setVisible(false);
    pane3.setVisible(false);
    pane4.setVisible(false);
    pane5.setVisible(false);
    paneL1.setVisible(false);
    paneL2.setVisible(false);
    if (event.getSource() == floorTwo) {
      pane2.setVisible(true);
      currentFloor = "2";
      floorLabel.setText("2");
    } else if (event.getSource() == floorThree) {
      pane3.setVisible(true);
      currentFloor = "3";
      floorLabel.setText("3");
    } else if (event.getSource() == floorFour) {
      pane4.setVisible(true);
      currentFloor = "4";
      floorLabel.setText("4");
    } else if (event.getSource() == floorFive) {
      pane5.setVisible(true);
      currentFloor = "5";
      floorLabel.setText("5");
    } else if (event.getSource() == lowerLevelOne) {
      paneL1.setVisible(true);
      currentFloor = "L1";
      floorLabel.setText("1");
    } else if (event.getSource() == lowerLevelTwo) {
      paneL2.setVisible(true);
      currentFloor = "L2";
      floorLabel.setText("2");
    } else if (event.getSource() == floorUp) {
      if (currentFloor == "L1") {
        paneL2.setVisible(true);
        currentFloor = "L2";
        floorLabel.setText("L2");
      } else if (currentFloor == "L2") {
        pane2.setVisible(true);
        currentFloor = "2";
        floorLabel.setText("2");
      } else if (currentFloor == "2") {
        pane3.setVisible(true);
        currentFloor = "3";
        floorLabel.setText("3");
      } else if (currentFloor == "3") {
        pane4.setVisible(true);
        currentFloor = "4";
        floorLabel.setText("4");
      } else if (currentFloor == "4") {
        pane5.setVisible(true);
        currentFloor = "5";
        floorLabel.setText("5");
      } else if (currentFloor == "5") {
        pane5.setVisible(true);
        currentFloor = "5";
        floorLabel.setText("5");
      }
    } else if (event.getSource() == floorDown) {
      if (currentFloor == "2") {
        paneL2.setVisible(true);
        currentFloor = "L2";
        floorLabel.setText("L2");
      } else if (currentFloor == "3") {
        pane2.setVisible(true);
        currentFloor = "2";
        floorLabel.setText("2");
      } else if (currentFloor == "4") {
        pane3.setVisible(true);
        currentFloor = "3";
        floorLabel.setText("3");
      } else if (currentFloor == "5") {
        pane4.setVisible(true);
        currentFloor = "4";
        floorLabel.setText("4");
      } else if (currentFloor == "L2") {
        paneL1.setVisible(true);
        currentFloor = "L1";
        floorLabel.setText("L1");
      } else if (currentFloor == "L1") {
        paneL1.setVisible(true);
        currentFloor = "L1";
        floorLabel.setText("L1");
      }
    }
  }

  void clearFields() {
    // nameField.clear();
    locationTypeDropdown.setText("Node Type");
    // requestName.setText("Node ID:");
  }

  public void typeMenu(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    locationTypeDropdown.setText(button.getText());
  }

  public void zoomableMap(ActionEvent event) {

    Node content = mapScroll.getContent();
    Group contentGroup = new Group();
    contentGroup.getChildren().add(content);
    mapScroll.setContent(contentGroup);
    if (event.getSource() == zoomIn) {
      Scale scaleTransform = new Scale(1.05, 1.05, 0, 0);
      scale *= 1.05;
      contentGroup.getTransforms().add(scaleTransform);

    } else if (event.getSource() == zoomOut && scale >= 1.05) {
      Scale scaleTransform = new Scale(.95, .95, 0, 0);
      scale *= 0.95;
      contentGroup.getTransforms().add(scaleTransform);
      if (mapScroll.getHvalue() > 0.8 || mapScroll.getVvalue() > 0.8) {
        mapScroll.setHvalue(0.6);
        mapScroll.setVvalue(0.6);
      }
    }
  }

  public double getMapXCoordFromClick(MouseEvent click) {
    panX = mapScroll.getHvalue() * (mapPane.getWidth() - mapScroll.getWidth() / scale);
    double panX2 =
        mapScroll.getHvalue() * 244; // no idea what these numbers are i guessed and checked
    if (scale == 1) {
      return (((click.getSceneX() - mapScroll.getLayoutX()) / scale) + panX - 8)
              * (croppedMapWidth / imagePaneWidth)
          + croppedMapXOffset;
    } else {
      return (((click.getSceneX() - mapScroll.getLayoutX()) / scale) + panX + panX2 - 8)
              * (croppedMapWidth / imagePaneWidth)
          + croppedMapXOffset;
    }
  }

  public double getMapYCoordFromClick(MouseEvent click) {
    panY = mapScroll.getVvalue() * (mapPane.getHeight() - mapScroll.getHeight() / scale);
    double panY2 =
        mapScroll.getVvalue() * 10; // no idea what these numbers are i guessed and checked
    if (scale == 1) {
      return (((click.getSceneY() - mapScroll.getLayoutY()) / scale) + panY - 8)
              * (croppedMapWidth / imagePaneWidth)
          + croppedMapYOffset;
    } else {
      return (((click.getSceneY() - mapScroll.getLayoutY()) / scale) + panY + panY2 - 8)
              * (croppedMapWidth / imagePaneWidth)
          + croppedMapYOffset;
    }
  }

  public void setCoordsOnMouseEvent(MouseEvent click) {
    try {
      locationEdit();
    } catch (NullPointerException e) {
    }
  }

  @FXML
  public void cleanEquip() {
    equipmentNodeManager.makeClean(currentEquipmentNode);
    currentEquipmentNode.resetLocation();
    populateEquipmentNodeData(currentEquipmentNode);
  }

  public void clearPage(ActionEvent actionEvent) {
    appController.clearPage();
  }

  public void changeStatus(ActionEvent event) {
    MenuItem button = (MenuItem) event.getSource();
    requestStatusDropdown.setText(button.getText());
  }

  public void deselect(MouseEvent mouseEvent) {
    requestEditPane.setVisible(false);
    locationEditPane.setVisible(false);
  }

  public void locationDelete(ActionEvent event) {}

  public void requestDelete(ActionEvent event) {}

  public void locationAdd(ActionEvent event) {

    int typeCount = (locationNodeManager.getTypeCount(typeDropdown.getText(), currentFloor));

    String nodeID =
            "A"
                    + typeDropdown.getText()
                    + String.format("%03d", typeCount)
                    + ((currentFloor.length() == 1) ? ("0" + currentFloor) : (currentFloor));

    Location newLocation =
            new Location(
                    nodeID,
                    (Double.parseDouble(xCoordField.getText())),
                    (Double.parseDouble(yCoordField.getText())),
                    currentFloor,
                    "TOWER",
                    typeDropdown.getText(),
                    nameField.getText(),
                    nodeID);
    displayLocationNode(locationNodeManager.addNode(newLocation));
    clearFields();



  }
}
