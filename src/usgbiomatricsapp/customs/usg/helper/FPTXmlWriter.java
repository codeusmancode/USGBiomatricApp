package usgbiomatricsapp.customs.usg.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import usgbiomatricsapp.customs.usg.pojo.Thumb;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Base64;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author usmanriaz
 */
public class FPTXmlWriter {

    JAXBContext jaxbContext;
    Marshaller marshaller;

    public FPTXmlWriter() {
        try {
            jaxbContext = JAXBContext.newInstance(Thumb.class);
            marshaller = jaxbContext.createMarshaller();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "BytesToXml Cunstructor:" + ex.getMessage());
        }
    }

    public void writeBytes(Thumb thumb, StringWriter out) throws Exception {

        marshaller.marshal(thumb, out);

    }

    public void writeBytesd(byte[] bytes) throws Exception {
        //Object o = toObject(bytes);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        System.out.println(new String(encoded));   // Outputs "SGVsbG8="
        //marshaller.marshal(o, System.out);

    }

    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }
}
