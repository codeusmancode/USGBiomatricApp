/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs.usg.UI;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import usgbiomatricsapp.customs.AttendanceMarkerListener;

import usgbiomatricsapp.customs.FPCaptureThread;
import usgbiomatricsapp.customs.Global;
import usgbiomatricsapp.customs.NetworkStatus;
import usgbiomatricsapp.customs.usg.helper.DBHandler;
import usgbiomatricsapp.customs.usg.pojo.Employee;
import usgbiomatricsapp.customs.usg.pojo.Thumb;

/**
 *
 * @author usmanriaz
 */
public class FPDialogVerification extends javax.swing.JDialog implements ActionListener {

    private FPCaptureThread captureThread;
    private Reader reader;
    private ReaderCollection readerCollection;
    private boolean cancel = false;
    private Fmd[] fmds;
    private Employee[] emps;
    private boolean recordsLoaded = false;
    private NetworkStatus ns;
    private Image biConnected;
    private Image biDisconnected;
    private Image[] messages;
    private boolean infoDisplayed;
    private int infoDisplayedTime = 0;
    private Timer t;
    private int autoWidth;
    private int autoHeight;
    private DBHandler database;
    private static int INFO_DISPLAYED_MAX_TIME = 5000;
    private static int ONE_SEC = 1000;
    private JFrame parent;
    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public FPDialogVerification(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = (JFrame) parent;

        //setting global variable first

        /*change required here to create new*/
        Global.operatingUnit = "UNIT 2";
        //Global.operatingUnit = "UNIT 3/4";
        //Global.operatingUnit = "UNIT 5";
        //Global.operatingUnit = "MANAGEMENT";
        //Global.operatingUnit= "FEMALE";
        //Global.operatingUnit = "Line 7";
        //Global.operatingUnit = "Finance";
        //Global.operatingUnit = "Fashion";
        //Global.operatingUnit = "Corporate";
        //Global.operatingUnit = "UNIT 1";

        /**
         * ***********************************
         */
        database = new DBHandler();
        new DBWorker().execute();

        //register network notifications..yes i made it :-)i am usman riaz....
        ns = new NetworkStatus(this);
        
        autoWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
        autoHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        initComponents();

        //below timer will update the time on lable
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        Date date = new Date(System.currentTimeMillis());
                        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                        String dateFormatted = formatter.format(date);
                        lblTime.setText(dateFormatted);

                    }
                });
            }
        });
        timer.start();

        //below timer will be fired every 1 hour and update the day lable
        //i could also do this by adding 24 hours delay but i didn't. don't know why...
        jlMonth.setText(months[Calendar.getInstance().get(Calendar.MONTH)]);
        jlDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
        jlYear.setText(Calendar.getInstance().get(Calendar.YEAR) + "");
        Timer timerDay = new Timer(3600000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        int dayNumber = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        lblDay.setText(days[dayNumber - 1].toUpperCase());

                        jlMonth.setText(months[Calendar.getInstance().get(Calendar.MONTH)]);
                        jlDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "");
                        jlYear.setText(Calendar.getInstance().get(Calendar.YEAR) + "");

                    }
                });
            }
        });
        timerDay.start();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                init();
                if (reader != null) {
                    StartCaptureThread();
                }

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                try {
                    if (reader != null) {
                        reader.CancelCapture();
                        reader.Close();

                    }
                    System.out.println("component hidden");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        });
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

        System.out.println("Screen Resolution:" + autoWidth + " x " + autoHeight);
        jPanel1.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 100), (int) getPercentage(autoHeight, 20)));
        //System.out.println((int)getPercentage(autoWidth, 100)+" <<< >>>"+(int)getPercentage(autoHeight, 80));
        jPanel2.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 100), (int) getPercentage(autoHeight, 80)));
        jpCompanyName.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 42), (int) getPercentage(getParent().getSize().height, 100)));
        jpLogo.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 17), (int) getPercentage(getParent().getSize().height, 100)));
        jpClock.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 41), (int) getPercentage(getParent().getSize().height, 100)));

        jpCalendar.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 15), (int) getPercentage(getParent().getSize().height, 100)));
        jpInfo.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 70), (int) getPercentage(getParent().getSize().height, 100)));
        jpImages.setPreferredSize(new Dimension((int) getPercentage(autoWidth, 15), (int) getPercentage(getParent().getSize().height, 100)));
        lblCompanyName.setText(lblCompanyName.getText());
        lblUnit.setText(Global.operatingUnit);
        int dayNumber = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        lblDay.setText(days[dayNumber - 1].toUpperCase());

        try {
            // ImageIcon i = new ImageIcon(getClass().getResource("/usgbiomatricsapp/customs/usg/images/connected.png"));

            /*biConnected = getScaledImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\connected.png")), 130, 130);
            biDisconnected = getScaledImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\disconnected_1.png")), 130, 130);
            messages = new Image[11];
            //store all the message images in the array
            messages[0] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\24horwork.jpg"));
            messages[1] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\goodbye.jpg"));
            messages[2] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\NO_CARD_IN.jpg"));
            messages[3] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\NO_CARD_IN_24HR_WORK.jpg"));
            messages[4] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\proinatt.jpg"));
            messages[5] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\promise.jpg"));
            messages[6] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\reenter_IN.jpg"));
            messages[7] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\reenter_OUT.jpg"));
            messages[8] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\think.jpg"));
            messages[9] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\wellcome.jpg"));
            messages[10] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("\\usgbiomatricsapp\\customs\\usg\\images\\messages\\wellcome_late.jpg"));
             */
            biConnected = getScaledImage(ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/connected.png")), 130, 130);
            biDisconnected = getScaledImage(ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/disconnected_1.png")), 130, 130);
            messages = new Image[11];
            //store all the message images in the array
            messages[0] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/24horwork.jpg"));
            messages[1] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/goodbye.jpg"));
            messages[2] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/NO_CARD_IN.jpg"));
            messages[3] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/NO_CARD_IN_24HR_WORK.jpg"));
            messages[4] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/proinatt.jpg"));
            messages[5] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/promise.JPG"));
            messages[6] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/reenter_IN.jpg"));
            messages[7] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/reenter_OUT.jpg"));
            messages[8] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/think.JPG"));
            messages[9] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/wellcome.jpg"));
            messages[10] = ImageIO.read(getClass().getResourceAsStream("/usgbiomatricsapp/customs/usg/images/messages/wellcome_late.jpg"));

        } catch (IOException ex) {
            Logger.getLogger(FPDialogVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class InsertAttendanceLog extends Thread {

        private String employeeCode;
        private int width = 738;
        private int height = 200;

        public InsertAttendanceLog(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        @Override
        public void run() {

            int respnose = database.logAttendance(this.employeeCode);
            switch (respnose) {
                case 0:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[4], width, height)));//problem in attendance
                    break;
                case 1:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[9], width, height)));//welcome
                    lblExtraMessage.setIcon(new ImageIcon(getScaledImage(messages[5], width, 70)));
                    break;
                case 2:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[1], width, height)));
                    lblExtraMessage.setIcon(new ImageIcon(getScaledImage(messages[8], width, 70)));
                    break;
                case 3:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[0], width, height)));
                    //lblMainExtra.setText("آوٹ نا کرنے کی وجۃ سے اپکی پچھلی غیر حاضری مارک کردی گئ ھے۔ٹائم افس سے رابطہ کریں۔");
                    break;
                case 5:

                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[7], width, height)));
                    break;
                case 4:

                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[6], width, height)));
                    break;
                case 6:

                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[3], width, height)));
                    break;
                case 7:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[10], width, height)));

                    break;
                case 8:
                    // lblMessage.setFont(new Font(lblMainExtra.getFont().getFontName(), Font.BOLD, 24));
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[2], width, height)));
                    break;
                case 9:
                    //lblMessage.setFont(new Font(lblMainExtra.getFont().getFontName(), Font.BOLD, 24));
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[2], width, height)));
                    break;
                default:
                    lblMessage.setIcon(new ImageIcon(getScaledImage(messages[4], width, height)));
                    break;
            }
        }

    }

    private void hideInfo() {
        lblThumb.setIcon(null);
        lblEmployeeImage.setIcon(null);
        lblDepartment.setText(".....");
        lblDesignation.setText(".....");
        lblEmpCode.setText(".....");
        lblEmpName.setText(".....");
        lblExtraMessage.setIcon(null);

        lblMessage.setIcon(null);

    }

    private float getPercentage(int of, int percent) {
        return (of * 1.0f) * (percent * 1.0f / 100f);
    }

    private void displayImage(Fid fid) {
        Fid.Fiv view = fid.getViews()[0];
        BufferedImage bImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        bImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        lblThumb.setIcon(new ImageIcon(getScaledImage(bImage, 180, 220)));
    }

    private BufferedImage getImage(String empCode) throws IOException {
        try {
            File file = new File("A:\\" + empCode + ".jpg");
            //File file = new File("\\\\Us2fileserver\\usg_emp_pics\\" + empCode + ".jpg");

            if (!file.exists()) {
                System.out.println("jpg does not exists");
                SeekableStream s = new FileSeekableStream(new File("A:\\" + empCode + ".tif"));

                TIFFDecodeParam param = null;
                ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
                RenderedImage op = dec.decodeAsRenderedImage(0);

                FileOutputStream fos = new FileOutputStream("A:\\" + empCode + ".jpg");
                JPEGEncodeParam jpgparam = new JPEGEncodeParam();
                jpgparam.setQuality(67);
                ImageEncoder en = ImageCodec.createImageEncoder("jpeg", fos, jpgparam);
                en.encode(op);
                fos.flush();
                fos.close();
            }
            BufferedImage bi = ImageIO.read(file);
            System.out.println("getting image... end");
            return bi;
        } catch (Exception ex) {
            System.out.println("getImage():" + ex.getMessage());
            return null;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(FPCaptureThread.ACT_CAPTURE)) {
            FPCaptureThread.FPCaptureEvent evt = (FPCaptureThread.FPCaptureEvent) e;
            if (evt.captureResult != null) {
                if (evt.captureResult.quality == Reader.CaptureQuality.GOOD && evt.captureResult.image != null) {
                    if (Global.online) {
                        if (t != null) {
                            t.stop();
                        }
                        infoDisplayed = false;
                        infoDisplayedTime = 0;
                        hideInfo();

                        process(evt.captureResult.image);
                    }

                }
            } else if (evt.exception != null) {
                cancel = true;
            } else if (evt.readerStatus != null) {
                cancel = true;
            }
            //If everything is okay then start capturing again
            if (!cancel) {
                StartCaptureThread();
            } else {
                setVisible(false);//if anything goes wrong i.e. cancel = true then hide this form , this will cause reader to cancel capture and close.
            }
        } else if (e.getActionCommand().equals(NetworkStatus.NETWORK_ACTION)) {
            NetworkStatus.NetworkEvent evtn = (NetworkStatus.NetworkEvent) e;
            boolean network = evtn.networkConnected;
            boolean server = evtn.serverRunning;
            if (!network || !server) {
                Global.online = false;
                jLabel3.setText("ھم معزرت خواہ ھیں،ٹکنیکی خامی کی وجھَ سے حاضری نھں لگ سکتی،ٹایم آفس سے رجوع کریں۔شکریۃ-");
//
                lblConnectionStatus.setIcon(new ImageIcon(getScaledImage(biDisconnected, 165, 165)));
            } else {
                Global.online = true;
                lblConnectionStatus.setIcon(new ImageIcon(getScaledImage(biConnected, 165, 165)));
                jLabel3.setText("حاضری لگانے کے لیے اپنی انگلی سکینر پر رکھیں");
            }

        }

    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private class DisplayImageThread extends Thread {

        private Fid fid;
        private Employee emp;

        public DisplayImageThread(Fid fid, Employee emp) {
            this.fid = fid;
            this.emp = emp;
        }

        @Override
        public void run() {
            super.run(); //To change body of generated methods, choose Tools | Templates.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    displayImage(fid);

                    try {
                        lblDepartment.setText(emp.getDepartment());
                        lblDesignation.setText(emp.getDesignation());
                        lblEmpName.setText(emp.getName());
                        lblEmpCode.setText(emp.getEmpCode());
//
                        lblEmployeeImage.setIcon(new ImageIcon(getScaledImage(getImage(emp.getEmpCode()), 180, 220)));

                        infoDisplayed = true;
                    } catch (Exception ex) {
                        System.out.println("DisplayImageThread:" + ex.getMessage());
                        /**
                         * if network is gone and user scans a finger just
                         * before setting Global.online variable. hide employee
                         * information because other information will be visible
                         * after getting exception trying capturing employee
                         * image from the server. So we need to hide it to give
                         * overall impression of form not working.
                         */
                        hideInfo();
                    }
                }
            });
            resetFormAfter2();

        }

    }

    private void resetFormAfter2() {
    //    JOptionPane.showMessageDialog(null, "sdfsdfsdf");
        t = new Timer(ONE_SEC, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //IF EMPLOYEE INFORMATION IS DISPLAYED THEN START INCREASING infoDisplayedTime by ONE_SEC which is 1000ms
                if (infoDisplayed) {
                    infoDisplayedTime += ONE_SEC;
                }
                if (infoDisplayedTime == INFO_DISPLAYED_MAX_TIME) {
                    hideInfo();
                    infoDisplayedTime = 0;
                    infoDisplayed = false;
                    t.stop();
                }
            }
        });
        t.start();
    }

    private void process(Fid fid) {

        Engine engine = UareUGlobal.GetEngine();
        try {
            Fmd sample = engine.CreateFmd(fid, Fmd.Format.ANSI_378_2004);
            long t1 = System.currentTimeMillis();
            int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; //target rate is 0.00001, one in one lakh chances of a wrong finger print

            Engine.Candidate[] candidates = engine.Identify(sample, 0, fmds, target_falsematch_rate, 1);//last parameter is how many matching candidate will this function return.it returns 1 most of the time..i mean mooooost of the time.
            long t2 = System.currentTimeMillis();

            if (candidates.length == 1) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new DisplayImageThread(fid, emps[candidates[0].fmd_index]).start();//give image to another thread to display on the lable becaue we don't want things to stuck
                        //new InsertAttendanceLog(emps[candidates[0].fmd_index].getEmpCode()).start();

                        //code that handles the new requirement of letting the user apply leave when he leaves early
                        //uncomment this code to add this functionalty
                        DBHandler db = new DBHandler();
                        String early = db.isLeavingEarly(emps[candidates[0].fmd_index].getEmpCode());
                        System.out.println(emps[candidates[0].fmd_index].getDeptID() + "");
                        //early = "NO";
                        if (early.equals("YES")) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {

                                    showInteractiveForm(emps[candidates[0].fmd_index],"LEAVING");
                                }
                            });
                        } else {
                            System.out.println("coming");
                            
                            String late = db.isComingLate(emps[candidates[0].fmd_index].getEmpCode());
                            //late = "YES";
                            if (late.equals("YES")) {
                                showInteractiveForm(emps[candidates[0].fmd_index],"COMING");
                            } else {
                                //JOptionPane.showMessageDialog(null, "normal attendance");
                            }

                            //new InsertAttendanceLog(emps[candidates[0].fmd_index].getEmpCode()).start();
                        } //PUT END COMENT HERE
                    }
                });

            } else {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        lblMessage.setIcon(new ImageIcon(getScaledImage(messages[4], 738, 200)));
                        displayImage(fid);
                    }
                });
                infoDisplayed = true;

                resetFormAfter2();
            }
        } catch (UareUException ex) {
            Logger.getLogger(FPDialogVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showInteractiveForm(Employee employee,String mode) {
        RuntimeAttendanceMarker pe = new RuntimeAttendanceMarker(FPDialogVerification.this.parent, true);
        pe.setEmp(employee);
        pe.setMode(mode);
        pe.addAttendanceMarkerListener(new AttendanceMarkerListener() {
            @Override
            public void ok(String m) {
                //JOptionPane.showMessageDialog(null, employee.getEmpCode()+"");
                new InsertAttendanceLog(employee.getEmpCode()).start();
                infoDisplayed = true;
                resetFormAfter2();
            }

            @Override
            public void cancel() {
                //JOptionPane.showMessageDialog(null, " no attendance brother, go play!!!");

            }
        });
        pe.setLocation(220, 200);
        pe.setVisible(true);
    }

    private void StartCaptureThread() {

        captureThread = new FPCaptureThread(this.reader, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        captureThread.startFPCaptureThread(this);

    }

    private class DBWorker extends SwingWorker<String, Object> {

        JDialog dlg;

        public DBWorker() {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    dlg = new JDialog(FPDialogVerification.this, "Loading....", true);
                    dlg.setUndecorated(true);
                    JProgressBar dpb = new JProgressBar(0, 500);
                    dpb.setIndeterminate(true);
                    dlg.add(BorderLayout.CENTER, dpb);
                    dlg.add(BorderLayout.NORTH, new JLabel("Loading Finger Prints From the Database, Please Wait..."));
                    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    dlg.setSize(300, 75);
                    dlg.setLocationRelativeTo(FPDialogVerification.this);
                    dlg.setVisible(true);
                }
            });
        }

        @Override
        protected String doInBackground() throws Exception {
            try {
                emps = database.getEmployees(Global.operatingUnit);
                fmds = new Fmd[emps.length];
                System.out.println("Total Employees Loaded in Memory:" + emps.length);
                int index = 0;
                for (Employee e : emps) {
                    usgbiomatricsapp.customs.usg.pojo.Fid f = JAXB.unmarshal(new StringReader(e.getXml_p()), usgbiomatricsapp.customs.usg.pojo.Fid.class);
                    fmds[index] = UareUGlobal.GetImporter().ImportFmd(f.Bytes, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
                    index += 1;
                }
                System.out.println("Total FMDs Loaded in Memory:" + fmds.length);
            } catch (Exception ex) {
                System.out.println("error:" + ex.getMessage());
            }
            return "done";
        }

        @Override
        protected void done() {
            super.done(); //To change body of generated methods, choose Tools | Templates.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dlg.setVisible(false);
                }
            });
            recordsLoaded = true;

        }

    }

    private void init() {
        try {
            /**
             * GET ALL THE READERS LIST.
             */
            readerCollection = UareUGlobal.GetReaderCollection();
            readerCollection.GetReaders();
            if (readerCollection.size() > 0) {
                reader = readerCollection.get(0);//AS WE KNOW THERE IS JUST ONE READER CONNECTED WITH THE COMPUTER
                // FIRST OF ALL, WE WILL OPEN THE READER.

                reader.Open(Reader.Priority.COOPERATIVE);
                

            } else {
                JOptionPane.showMessageDialog(null, "Reader Not Attached");
            }
            //System.out.println("reader status:"+reader.GetStatus().toString());
        } catch (UareUException ex) {
            Logger.getLogger(FPDialogRegister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FPDialogRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Biomatric.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Biomatric.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Biomatric.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Biomatric.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FPDialogVerification(null, false).setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jpClock = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTime = new javax.swing.JLabel();
        lblDay = new javax.swing.JLabel();
        jpLogo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jpCompanyName = new javax.swing.JPanel();
        lblCompanyName = new javax.swing.JLabel();
        lblUnit = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jpCalendar = new javax.swing.JPanel();
        lblConnectionStatus = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jlMonth = new javax.swing.JLabel();
        jlDate = new javax.swing.JLabel();
        jlYear = new javax.swing.JLabel();
        jpImages = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblEmployeeImage = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblThumb = new javax.swing.JLabel();
        jpInfo = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblMessage = new javax.swing.JLabel();
        lblExtraMessage = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblEmpCode = new javax.swing.JLabel();
        lblEmpName = new javax.swing.JLabel();
        lblDesignation = new javax.swing.JLabel();
        lblDepartment = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("USG Biomatric Verification");
        setBackground(new java.awt.Color(153, 51, 0));
        setPreferredSize(new java.awt.Dimension(1366, 728));
        setSize(new java.awt.Dimension(1366, 728));
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        jPanel1.setBackground(new java.awt.Color(33, 88, 46));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.setPreferredSize(new java.awt.Dimension(1294, 145));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jpClock.setBackground(new java.awt.Color(102, 0, 102));
        jpClock.setOpaque(false);
        jpClock.setPreferredSize(new java.awt.Dimension(409, 49));
        jpClock.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 204, 102));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(409, 50));

        lblTime.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lblTime.setForeground(new java.awt.Color(255, 255, 255));
        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTime.setText("00:00:00 AM");

        lblDay.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblDay.setForeground(new java.awt.Color(255, 255, 255));
        lblDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDay.setText(".......");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpClock.add(jPanel3, java.awt.BorderLayout.LINE_END);

        jPanel1.add(jpClock, java.awt.BorderLayout.EAST);

        jpLogo.setOpaque(false);
        jpLogo.setPreferredSize(new java.awt.Dimension(100, 146));
        jpLogo.setLayout(new java.awt.BorderLayout());

        jLabel2.setBackground(new java.awt.Color(51, 255, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usgbiomatricsapp/customs/usg/images/logo_t.png"))); // NOI18N
        jpLogo.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel1.add(jpLogo, java.awt.BorderLayout.CENTER);

        jpCompanyName.setBackground(new java.awt.Color(102, 0, 51));
        jpCompanyName.setOpaque(false);
        jpCompanyName.setPreferredSize(new java.awt.Dimension(550, 200));

        lblCompanyName.setFont(new java.awt.Font("Tahoma", 1, 43)); // NOI18N
        lblCompanyName.setForeground(new java.awt.Color(255, 255, 255));
        lblCompanyName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCompanyName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usgbiomatricsapp/customs/usg/images/sdsss.png"))); // NOI18N

        lblUnit.setFont(new java.awt.Font("Times New Roman", 0, 30)); // NOI18N
        lblUnit.setForeground(new java.awt.Color(255, 255, 255));
        lblUnit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUnit.setText("jLabel1");

        javax.swing.GroupLayout jpCompanyNameLayout = new javax.swing.GroupLayout(jpCompanyName);
        jpCompanyName.setLayout(jpCompanyNameLayout);
        jpCompanyNameLayout.setHorizontalGroup(
            jpCompanyNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jpCompanyNameLayout.setVerticalGroup(
            jpCompanyNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpCompanyNameLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(lblCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUnit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22))
        );

        jPanel1.add(jpCompanyName, java.awt.BorderLayout.LINE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new java.awt.BorderLayout(4, 4));

        jpCalendar.setBackground(new java.awt.Color(33, 88, 46));
        jpCalendar.setPreferredSize(new java.awt.Dimension(250, 5));
        jpCalendar.setLayout(new java.awt.GridLayout(2, 1));

        lblConnectionStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConnectionStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usgbiomatricsapp/customs/usg/images/connected.png"))); // NOI18N
        lblConnectionStatus.setPreferredSize(new java.awt.Dimension(165, 165));
        jpCalendar.add(lblConnectionStatus);

        jPanel6.setOpaque(false);

        jlMonth.setFont(new java.awt.Font("Times New Roman", 0, 66)); // NOI18N
        jlMonth.setForeground(new java.awt.Color(255, 255, 255));
        jlMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlMonth.setText("APR");

        jlDate.setFont(new java.awt.Font("Times New Roman", 1, 120)); // NOI18N
        jlDate.setForeground(new java.awt.Color(255, 255, 255));
        jlDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlDate.setText("09");

        jlYear.setFont(new java.awt.Font("Times New Roman", 0, 55)); // NOI18N
        jlYear.setForeground(new java.awt.Color(255, 255, 255));
        jlYear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlYear.setText("2018");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlMonth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlYear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jlMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlYear, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpCalendar.add(jPanel6);

        jPanel2.add(jpCalendar, java.awt.BorderLayout.LINE_START);

        jpImages.setBackground(new java.awt.Color(33, 88, 46));
        jpImages.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jpImages.setPreferredSize(new java.awt.Dimension(300, 5));
        jpImages.setLayout(new java.awt.GridLayout(2, 1));

        jPanel7.setOpaque(false);

        lblEmployeeImage.setBackground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addComponent(lblEmployeeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblEmployeeImage, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jpImages.add(jPanel7);

        jPanel8.setOpaque(false);

        lblThumb.setBackground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblThumb, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 261, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblThumb, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jpImages.add(jPanel8);

        jPanel2.add(jpImages, java.awt.BorderLayout.LINE_END);

        jpInfo.setBackground(new java.awt.Color(0, 0, 0));
        jpInfo.setPreferredSize(new Dimension((int)getPercentage(autoWidth, 60), (int)getPercentage(getParent().getSize().height, 100)));
        jpInfo.setLayout(new java.awt.GridLayout(2, 1, 2, 5));

        jPanel5.setBackground(new java.awt.Color(33, 88, 46));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblExtraMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblExtraMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
            .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(lblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblExtraMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jpInfo.add(jPanel5);

        jPanel4.setBackground(new java.awt.Color(33, 88, 46));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblEmpCode.setFont(new java.awt.Font("Monospaced", 1, 30)); // NOI18N
        lblEmpCode.setForeground(new java.awt.Color(255, 255, 255));
        lblEmpCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmpCode.setText("..........");

        lblEmpName.setFont(new java.awt.Font("Monospaced", 1, 30)); // NOI18N
        lblEmpName.setForeground(new java.awt.Color(255, 255, 255));
        lblEmpName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmpName.setText("..........");

        lblDesignation.setFont(new java.awt.Font("Monospaced", 1, 30)); // NOI18N
        lblDesignation.setForeground(new java.awt.Color(255, 255, 255));
        lblDesignation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDesignation.setText("..........");

        lblDepartment.setFont(new java.awt.Font("Monospaced", 1, 30)); // NOI18N
        lblDepartment.setForeground(new java.awt.Color(255, 255, 255));
        lblDepartment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDepartment.setText("..........");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("..........");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblEmpCode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
            .addComponent(lblEmpName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblDesignation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEmpCode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEmpName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDesignation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDepartment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jpInfo.add(jPanel4);

        jPanel2.add(jpInfo, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel jlDate;
    private javax.swing.JLabel jlMonth;
    private javax.swing.JLabel jlYear;
    private javax.swing.JPanel jpCalendar;
    private javax.swing.JPanel jpClock;
    private javax.swing.JPanel jpCompanyName;
    private javax.swing.JPanel jpImages;
    private javax.swing.JPanel jpInfo;
    private javax.swing.JPanel jpLogo;
    private javax.swing.JLabel lblCompanyName;
    private javax.swing.JLabel lblConnectionStatus;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblDepartment;
    private javax.swing.JLabel lblDesignation;
    private javax.swing.JLabel lblEmpCode;
    private javax.swing.JLabel lblEmpName;
    private javax.swing.JLabel lblEmployeeImage;
    private javax.swing.JLabel lblExtraMessage;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblThumb;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblUnit;
    // End of variables declaration//GEN-END:variables
}
