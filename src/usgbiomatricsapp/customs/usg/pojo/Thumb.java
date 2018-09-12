/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Thumb")
public class Thumb {
    private byte[] thumbTemplateBytes;
    private String employeeCode;
    public Thumb(){}

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setThumbTemplateBytes(byte[] thumbTemplateBytes) {
        this.thumbTemplateBytes = thumbTemplateBytes;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public byte[] getThumbTemplateBytes() {
        return thumbTemplateBytes;
    }
    
}
