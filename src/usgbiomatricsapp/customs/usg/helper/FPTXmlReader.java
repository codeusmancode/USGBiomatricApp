/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.helper;

import usgbiomatricsapp.customs.usg.pojo.Thumb;
import java.io.File;
import java.io.StringReader;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author usmanriaz
 */
public class FPTXmlReader {
    JAXBContext jaxbContext;
    Unmarshaller unMarshaller;
    public FPTXmlReader(){
        try{
            jaxbContext = JAXBContext.newInstance(Thumb.class);
            unMarshaller = jaxbContext.createUnmarshaller();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "BytesToXml Cunstructor:"+ex.getMessage());
        }
    }
    
    public byte[] getThumbBytes(String thumbXml) throws Exception{
        
        Thumb thumb= (Thumb) unMarshaller.unmarshal(new StringReader(thumbXml));
        
        return thumb.getThumbTemplateBytes();
    }
}
