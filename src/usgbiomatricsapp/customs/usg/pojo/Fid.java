/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.pojo;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author usmanriaz
 */
@XmlRootElement(name = "Fid")
public class Fid {

    @XmlElement(name = "Bytes")
    public byte[] Bytes;
    @XmlElement(name = "Format")
    public String Format;
    @XmlElement(name = "Version")
    public String Version;

    public Fid() {}

    public Fid(byte[] bytes, String format, String version) {
        
        this.Bytes = bytes;
        this.Format = format;
        this.Version = version;
        
        
    }

}
