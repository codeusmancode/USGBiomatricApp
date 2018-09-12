/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author usmanriaz
 */
public class FPCaptureThread extends Thread {

    private boolean cancel;
    private ActionListener listener;
    private Reader reader;
    private Fid.Format format;
    private Reader.ImageProcessing proc;
    private FPCaptureEvent capture;
    public static final String ACT_CAPTURE = "capture_thread_captured";
    private FPCaptureEvent lastCapture;

    public FPCaptureThread(Reader reader, Fid.Format imgFormat, Reader.ImageProcessing imgProc) {
        cancel = false;
        this.reader = reader;
        format = imgFormat;
        proc = imgProc;
    }

    public void startFPCaptureThread(ActionListener listener) {
        this.listener = listener;
        super.start();
    }

    @Override
    public void run() {
        Capture();
    }

    public FPCaptureEvent getLastCaptureEvent() {
        return lastCapture;
    }

    private void Capture() {
        try {
            //wait for reader to become ready
            boolean bReady = false;
            while (!bReady && !cancel) {
                System.out.println(reader.GetStatus().status+"");
                //System.out.println("reader is not ready");
                Reader.Status rs = reader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rs.status) {
                    //if busy, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                    //ready for capture
                    bReady = true;
                    break;
                } else {
                    
                    //reader failure
                    notifyListener(ACT_CAPTURE, null, rs, null);
                    break;
                }
            }
            if (cancel) {
                Reader.CaptureResult cr = new Reader.CaptureResult();
                cr.quality = Reader.CaptureQuality.CANCELED;
                notifyListener(ACT_CAPTURE, cr, null, null);

            }

            if (bReady) {
                //capture
                Reader.CaptureResult cr = reader.Capture(format, proc, 500, -1);
                System.out.println("waiting.........scan you finger.....go on......i'm watching.....");
                notifyListener(ACT_CAPTURE, cr, null, null);
                System.out.println("you just did that");

            }
        } catch (UareUException e) {
            notifyListener(ACT_CAPTURE, null, null, e);
        }
    }

    private void notifyListener(String action, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
        final FPCaptureEvent evt = new FPCaptureEvent(this, action, cr, st, ex);
        lastCapture = evt;
        if (listener == null) {
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.actionPerformed(evt);
            }
        });
    }

    public class FPCaptureEvent extends ActionEvent {

        private static final long serialVersionUID = 101;

        public Reader.CaptureResult captureResult;
        public Reader.Status readerStatus;
        public UareUException exception;

        public FPCaptureEvent(Object source, String action, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
            super(source, ActionEvent.ACTION_PERFORMED, action);
            captureResult = cr;
            readerStatus = st;
            exception = ex;
        }
    }
}
