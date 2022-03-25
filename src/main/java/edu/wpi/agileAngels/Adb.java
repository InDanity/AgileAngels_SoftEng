package edu.wpi.agileAngels;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.*;

public class Adb {
  private HashMap<String, Location> data;
  public Connection connection = null;

  public void main(String[] args) throws IOException, InterruptedException {

    // menu();
    // Apache Derby and table creation
    System.out.println("-------Embedded Apache Derby Connection Testing --------");
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    } catch (ClassNotFoundException e) {
      System.out.println("Apache Derby Driver not found. Add the classpath to your module.");
      System.out.println("For IntelliJ do the following:");
      System.out.println("File | Project Structure, Modules, Dependency tab");
      System.out.println("Add by clicking on the green plus icon on the right of the window");
      System.out.println(
          "Select JARs or directories. Go to the folder where the database JAR is located");
      System.out.println("Click OK, now you can compile your program and run it.");
      e.printStackTrace();
      return;
    }

    System.out.println("Apache Derby driver registered!");
    

    Statement statement;
    try {
      // substitute your database name for myDB
      connection = DriverManager.getConnection("jdbc:derby:myDB;create=true");
      statement = connection.createStatement();

      String query =
          "CREATE TABLE Locations( "
              + "NodeID VARCHAR(50),"
              + "xcoord VARCHAR(50),"
              + "ycoord VARCHAR(50),"
              + "Floor VARCHAR(50),"
              + "building VARCHAR(50),"
              + "NodeType VARCHAR(50),"
              + "longName VARCHAR(50),"
              + "shortName VARCHAR(50))";
      statement.execute(query);

    } catch (SQLException e) {
      System.out.println("Connection failed. Check output console.");
      e.printStackTrace();
      return;
    }
    System.out.println("Apache Derby connection established!");
    
    Parser parser = new Parser();
    parser.createTable(connection);
    data = parser.locationData; // Updates the big hashmap
  }

  /** Menu Creation for User* */
  private void menu() {

    Scanner myObj = new Scanner(System.in); // Create a Scanner object
    System.out.println("1 - Location Information");
    System.out.println("2 - Change Floor and Type");
    System.out.println("3 - Enter Location");
    System.out.println("4 - Delete Location");
    System.out.println("5 - Save Locations to CSV File");
    System.out.println("6 - Exit Program");

    String select = myObj.nextLine();

    // TODO: make a selection

    if (select.equals("1")) {
      System.out.println("Location Information");
      // TODO call location information function

    }
    if (select.equals("2")) {
      System.out.println("Change Floor and Type");
      // TODO call change floor and type function

    }
    if (select.equals("3")) {
      System.out.println("Enter Location");
      // TODO call adding location function

    }
    if (select.equals("4")) {
      System.out.println("Delete Location");
      // TODO call delete location function

    }
    if (select.equals("5")) {
      System.out.println("Save Locations to CSV File");
      // TODO call save locations to csv file function

    }
    if (select.equals("6")) {
      System.out.println("Exit Program");
      // TODO call exit program function

    } else {
      System.out.println("Wrong Input, Select From Menu");
    }
    menu();
  }

  public void changeType(String NodeID) {
    Location l = new Location(NodeID, null);
    Scanner myObj = new Scanner(System.in); // Create a Scanner object
    System.out.println("What is the NodeID of the location that you want to change?");
    String select = myObj.nextLine();
    // see if it exists
    System.out.println("The current floor is :" + l.getFloor());
    System.out.println("The current type is: " + l.getNodeType());
    System.out.println(
        "Do you want to change the floor or type?\nClick 1 for floor and 2 for type: ");
    int choice = myObj.nextInt();
    if (choice == 1) {
      System.out.println("What do you want to change the floor to?");
      String newFloor = myObj.nextLine();
      l.setFloor(newFloor);
      System.out.println("Do you also want to change the type?\nSelect 1 for Yes and 0 for No");
      int choice2 = myObj.nextInt();
      if (choice2 == 1) {
        System.out.println("What do you want to change the type to?");
        String newType = myObj.nextLine();
        l.setNodeType(newType);
      } else {
      }
    }
    if (choice == 2) {
      System.out.println("What do you want to change the type to?");
      String newType = myObj.nextLine();
      l.setNodeType(newType);
      System.out.println("Do you also want to change the floor?\nSelect 1 for Yes and 0 for No");
      int choice2 = myObj.nextInt();
      if (choice2 == 1) {
        System.out.println("What do you want to change the floor to?");
        String newFloor = myObj.nextLine();
        l.setFloor(newFloor);
      } else {
      }
    } else {
      throw exception();
    }
  }
}
