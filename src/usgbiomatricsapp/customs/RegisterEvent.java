/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import java.awt.event.ActionEvent;

public class RegisterEvent extends ActionEvent {

    public static final String ACTION_PROMPT = "enrollment_prompt";
    public static final String ACTION_CAPTURE = "enrollment_capture";
    public static final String ACTION_FEATURES = "enrollment_features";
    public static final String ACTION_DONE = "enrollment_done";
    public static final String ACTION_CANCELED = "enrollment_canceled";
    private static final long serialVersionUID = 102;

    public Reader.CaptureResult fingerCaptureResult;
    public Reader.Status readerStatus;
    public UareUException exception;
    public Fmd enrollmentFmd;

    public RegisterEvent(Object source, String action, Fmd fmd, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
        super(source, ActionEvent.ACTION_PERFORMED, action);
        fingerCaptureResult = cr;
        readerStatus = st;
        exception = ex;
        enrollmentFmd = fmd;
    }
}
