/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.pojo;

import java.io.Serializable;



/**
 *
 * @author usmanriaz
 */
public class Employee implements Serializable{
    private String empCode;
    private String name;
    private String fatherName;
    private String designation;
    private String department;
    private String fingerCode;
    private String  deptID;
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    private String unitName;
    private String shiftName;
    private String shiftID;
    private String active;
    private String xml_p;
    public Employee(){
        
    }

    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    
    public String getFingerCode() {
        return fingerCode;
    }

    public void setFingerCode(String fingerCode) {
        this.fingerCode = fingerCode;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }

    public String getActive() {
        return active;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getXml_p() {
        return xml_p;
    }

    public void setXml_p(String xml_p) {
        this.xml_p = xml_p;
    }

    
}
