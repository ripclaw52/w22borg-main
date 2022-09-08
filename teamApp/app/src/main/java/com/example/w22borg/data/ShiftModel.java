package com.example.w22borg.data;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftModel implements Serializable {
    private int id;
    private LocalDate localDate;
    private int shiftType;

    private ArrayList<EmployeeModel> employeeModels;

    // Constructors

    public ShiftModel(@Nullable LocalDate localDate, int shiftType) {
        this.localDate = localDate;
        this.shiftType = shiftType;
        this.employeeModels = new ArrayList<>();
    }

    public ShiftModel(@Nullable int id, LocalDate localDate, int shiftType) {
        this.id = id;
        this.localDate = localDate;
        this.shiftType = shiftType;
        this.employeeModels = new ArrayList<>();
    }

    public ShiftModel(@Nullable int id, LocalDate localDate, int shiftType, ArrayList<EmployeeModel> employeeModels) {
        this.id = id;
        this.localDate = localDate;
        this.shiftType = shiftType;
        this.employeeModels = employeeModels;
    }

    // CLONE
    public ShiftModel(@Nullable ShiftModel clone) {
        this.id = clone.getId();
        this.localDate = clone.getLocalDate();
        this.shiftType = clone.getShiftType();
        this.employeeModels = clone.getEmployeeModels();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getLocalDate() { return localDate; }
    public void setLocalDate(LocalDate localDate) { this.localDate = localDate; }
    public void setLocalDate(String localDate) { this.localDate = LocalDate.parse(localDate); }

    public int getShiftType() { return shiftType; }
    public void setShiftType(int shiftType) { this.shiftType = shiftType; }

    public ArrayList<EmployeeModel> getEmployeeModels() { return employeeModels; }
    public void setEmployeeModels(ArrayList<EmployeeModel> employeeModels) { this.employeeModels = employeeModels; }

    public String getEmployeeIDS() {
        StringBuilder tempString = new StringBuilder();
        switch (employeeModels.size()) {
            case 1:
                tempString.append(String.valueOf(employeeModels.get(0).getId()));
                break;
            case 2:
                tempString.append(String.valueOf(employeeModels.get(0).getId()) + ", ");
                tempString.append(String.valueOf(employeeModels.get(1).getId()));
                break;
            case 3:
                tempString.append(String.valueOf(employeeModels.get(0).getId()) + ", ");
                tempString.append(String.valueOf(employeeModels.get(1).getId()) + ", ");
                tempString.append(String.valueOf(employeeModels.get(2).getId()));
                break;
            default:
                break;
        }
        return tempString.toString();
    }

    // Clears the data from the selected EmployeeModel list
    public void removeAllEmployeeModelsFromList() {
        employeeModels.clear(); }
    public List<EmployeeModel> addEmployeeModelIntoList(EmployeeModel employee) {
        employeeModels.add(employee);
        return employeeModels;
    }
    public List<EmployeeModel> removeEmployeeModelFromList(EmployeeModel employee) {
        employeeModels.remove(new EmployeeModel(employee));
        return employeeModels;
    }

    // Check if a given employee exists in the list
    public boolean locateEmployeeInList(EmployeeModel employee) {
        for (EmployeeModel employeeData: employeeModels) {
            if (employee.getId() == employeeData.getId()) { return true; }
        }
        return false;
    }

    // open shift
    public boolean checkIfEmployeesCanOpen() {
        for (EmployeeModel employeeCanOpen: employeeModels) {
            if (employeeCanOpen.isCanOpenStore()) {
                return true; }
        } return false; }

    // close shift
    public boolean checkIfEmployeesCanClose() {
        for (EmployeeModel employeeCanClose: employeeModels) {
            if (employeeCanClose.isCanCloseStore()) {
                return true; }
        } return false; }

    // all-day shift
    public boolean checkIfEmployeesCanOpenAndClose() {
        boolean canOpen = false;
        boolean canClose = false;
        for (EmployeeModel employeeAllDay: employeeModels) {
            if (employeeAllDay.isCanOpenStore()) { canOpen = true; }
            if (employeeAllDay.isCanCloseStore()) { canClose = true; } }
        if (canOpen == canClose) {
            return canOpen;
        } else {
            return false;} }
}