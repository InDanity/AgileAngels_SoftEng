package edu.wpi.agileAngels;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// Implementation of RequestDAO
public class RequestDAOImpl implements RequestDAO {

  private String CSV_FILE_PATH = "./MedData.csv";
  private HashMap<String, Request> reqData = new HashMap<>(); // each type has its own request
  private int count; // how many requests there are
  private ArrayList<MedicalEquip> equipment = new ArrayList<MedicalEquip>();

  public RequestDAOImpl(String CSV_FILE_PATH, HashMap<String, Request> reqData, int count)
      throws SQLException {
    this.CSV_FILE_PATH = CSV_FILE_PATH;
    this.reqData = reqData;
    this.count = count;
    equipment.add(new MedicalEquip("Bed", 20));
    equipment.add(new MedicalEquip("Recliners", 6));
    equipment.add(new MedicalEquip("X-Ray Machine", 1));
    equipment.add(new MedicalEquip("Infusion Pump", 30));
    Adb.addMedicalEquipment(equipment);
  }

  public HashMap<String, Request> getAllRequests() {
    return reqData;
  }

  public void updateEmployeeName(Request request, String newName) {
    request.setEmployee(newName);
    Adb.updateRequest(request, "EmployeeName", newName);
  }

  public void updateRequestType(Request request, int requestType) {
    request.setRequestType(requestType);
  }

  public void updateType(Request request, String newType) {
    request.setType(newType);
    Adb.updateRequest(request, "Type", newType);
  }

  public void updateLocation(Request request, String newLocation) {
    request.setLocation(newLocation);
    Adb.updateRequest(request, "Location", newLocation);
  }

  public void updateDescription(Request request, String description) {
    request.setDescription(description);
    Adb.updateRequest(request, "Description", description);
  }

  public void updateStatus(Request request, String newStatus) {
    request.setStatus(newStatus);
    Adb.updateRequest(request, "Status", newStatus);
  }

  public void deleteRequest(Request request) {

    reqData.remove(request.getDescription()); // change to the key
    Adb.removeRequest(request);
  }
  // add request based on count and requestType
  public void addRequest(Request request) {
    // TODO debug this
    String letter;
    count = count + 1;
    if (request.getRequestType() == 0) {
      letter = "a";
    } else {
      letter = "b";
    }
    letter = letter + Integer.toString(count);
    request.setName(letter);
    reqData.put(letter, request);
    Adb.addRequest(request);
    // change to the key
  }
}
