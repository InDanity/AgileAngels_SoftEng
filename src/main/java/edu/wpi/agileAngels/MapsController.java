package edu.wpi.agileAngels;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javax.swing.*;

// TODO: Close app button is broken when displaying floor 1

public class MapsController extends MainController implements Initializable {

  @FXML
  private ImageView floorOneMap, floorTwoMap, floorThreeMap, lowerLevelOneMap, lowerLevelTwoMap;
  @FXML
  private Button floorOne,
      floorTwo,
      floorThree,
      lowerLevelOne,
      lowerLevelTwo,
      editButton,
      addButton,
      removeButton,
      switchToAddButton,
      switchToEditButton;
  @FXML private TextField nodeIDField, nameField, xCoordField, yCoordField, typeField;

  @FXML Pane mapPane;
  @FXML AnchorPane anchor;

  Node currentNode = null;

  Pane pane1 = new Pane();
  Pane pane2 = new Pane();
  Pane pane3 = new Pane();
  Pane paneL1 = new Pane();
  Pane paneL2 = new Pane();

  NodeManager nodeManager = new NodeManager(this);

  /**
   * Called on page load, creates panes for each map, adds the images for each map to its pane, and
   * sets their initial visibility
   *
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mapPane.getChildren().add(pane1);
    pane1.getChildren().add((floorOneMap));
    pane1.setVisible(true);
    mapPane.getChildren().add(pane2);
    pane2.getChildren().add((floorTwoMap));
    pane2.setVisible(false);
    mapPane.getChildren().add(pane3);
    pane3.getChildren().add((floorThreeMap));
    pane3.setVisible(false);
    mapPane.getChildren().add(paneL1);
    paneL1.getChildren().add((lowerLevelOneMap));
    paneL1.setVisible(false);
    paneL2.getChildren().add((lowerLevelTwoMap));
    mapPane.getChildren().add(paneL2);
    paneL2.setVisible(false);
    floorOne.setViewOrder(-100);
    floorTwo.setViewOrder(-100);
    floorThree.setViewOrder(-100);
    lowerLevelOne.setViewOrder(-100);
    lowerLevelTwo.setViewOrder(-100);

    nodeManager.createNodesFromDB();
  }

  /**
   * Populates the text fields on the page with data of a node
   *
   * @param node the node whose data is populated
   */
  public void populateNodeData(Node node) {
    System.out.println(node.getNodeID());
    nodeIDField.setText(node.getNodeID());
    nameField.setText(node.getName());
    typeField.setText(node.getNodeType());
    xCoordField.setText(Double.toString(node.getXCoord()));
    yCoordField.setText(Double.toString(node.getYCoord()));

    currentNode = node;
  }

  @FXML
  private void addNode() {
    // adds node to page.
  }

  /**
   * Edits the parameters of currentNode
   *
   * @throws IOException NumberFormatException from parseDouble
   */
  @FXML
  private void editNode() throws IOException {
    currentNode.changeLocationXCoord(Double.parseDouble(xCoordField.getText()));
    currentNode.changeLocationYCoord(Double.parseDouble(yCoordField.getText()));
    currentNode.changeLocationName(nameField.getText());
    currentNode.changeLocationType(typeField.getText());
    currentNode.resetLocation();
    currentNode = null;
  }

  /**
   * Removes currentNode from its pane and calls NodeManager.removeNode(), which removes the node
   * from the hashmap in NodeManager and the database
   */
  @FXML
  private void removeNode() {
    // delete node from pane in MapsController
    // call deleteNode() in NodeManager, which:
    //   deletes the node from the hashmap in NodeManager
    //   deletes the node from the database

    // im not sure if the below line works  we might have to reset the page
    // locationDAO.deleteLocation(currentNode.getLocation());
  }

  /**
   * Switches between the "add a location" mode and the "edit or delete a location" mode on a button
   * press
   *
   * @param event the button that was pressed, which is either switchToAddButton or the
   *     switchToEditButton
   */
  @FXML
  private void switchMode(ActionEvent event) {
    if (event.getSource() == switchToAddButton) {
      switchToAddButton.setVisible(false);
      switchToEditButton.setVisible(true);
      addButton.setVisible(true);
      editButton.setVisible(false);
      removeButton.setVisible(false);
    } else {
      switchToAddButton.setVisible(true);
      switchToEditButton.setVisible(false);
      addButton.setVisible(false);
      editButton.setVisible(true);
      removeButton.setVisible(true);
    }
  }

  /**
   * Adds a node to the pane corresponding to its floor
   *
   * @param node the node to be added to a pane
   */
  public void displayNode(Node node) {
    if (node.getFloor().equals("1")) {
      pane1.getChildren().add(node.getButton());
    } else if (node.getFloor().equals("2")) {
      pane2.getChildren().add(node.getButton());
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
    pane1.setVisible(false);
    pane2.setVisible(false);
    pane3.setVisible(false);
    paneL1.setVisible(false);
    paneL2.setVisible(false);
    if (event.getSource() == floorOne) {
      pane1.setVisible(true);
    } else if (event.getSource() == floorTwo) {
      pane2.setVisible(true);
    } else if (event.getSource() == floorThree) {
      pane3.setVisible(true);
    } else if (event.getSource() == lowerLevelOne) {
      paneL1.setVisible(true);
    } else if (event.getSource() == lowerLevelTwo) {
      paneL2.setVisible(true);
    }
  }
}
