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

import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXB;
import usgbiomatricsapp.customs.FPCaptureThread;
import usgbiomatricsapp.customs.Global;
import usgbiomatricsapp.customs.RegisterEvent;
import usgbiomatricsapp.customs.usg.helper.DBConnection;
import usgbiomatricsapp.customs.usg.helper.DBHandler;
import usgbiomatricsapp.customs.usg.pojo.Employee;

/**
 *
 * @author usmanriaz
 */
public class FPDialogRegister extends javax.swing.JDialog implements ActionListener {
    
    private ReaderCollection readerCollection;
    private Reader reader;
    private boolean cancel = false;
    private ImageIcon questionMarkImgIco;
    private usgbiomatricsapp.customs.usg.pojo.Fid fid;
    private boolean employeeSearched = false;
    private boolean capturing = true;
    private String[] leftFingerCodes = {"LT", "LI", "LM", "LR", "LL"};
    private String[] rightFingerCodes = {"RT", "RI", "RM", "RR", "RL"};

    public FPDialogRegister(java.awt.Frame parent) {
        super(parent);
        //hookup the connection to the database
        Global.appConnection = DBConnection.getInstance().getConnection();
        initComponents();
        addComponentListener(new CustomComponentListener());
        
        
    }
    
    private class CustomComponentListener extends ComponentAdapter {
        
        @Override
        public void componentShown(ComponentEvent e) {
            init();
        }
        
        @Override
        public void componentHidden(ComponentEvent e) {
            
            try {
                FPDialogRegister.this.reader.CancelCapture();
                FPDialogRegister.this.reader.Close();
                cancel = true;
                System.out.println("closing everything");
            } catch (UareUException ex) {
                Logger.getLogger(FPDialogRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void lookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FPDialogRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FPDialogRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FPDialogRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FPDialogRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void init() {
        try {
            /**
             * GET ALL THE READERS LIST.
             */
            readerCollection = UareUGlobal.GetReaderCollection();
            readerCollection.GetReaders();
            reader = readerCollection.get(0);//AS WE KNOW THERE IS JUST ONE READER CONNECTED WITH THE COMPUTER
            // FIRST OF ALL, WE WILL OPEN THE READER.

            reader.Open(Reader.Priority.COOPERATIVE);
            //START THE REGISTRATION THREAD
            new RegisterThread(FPDialogRegister.this).start();
            
        } catch (UareUException ex) {
            Logger.getLogger(FPDialogRegister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FPDialogRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtEmpCode = new javax.swing.JTextField();
        txtEmpName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFatherName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDesignation = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JTextField();
        txtShiftName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtShiftID = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblEmpImage = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblUnit = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlRight = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlLeft = new javax.swing.JList<>();
        rbLeft = new javax.swing.JRadioButton();
        rbRight = new javax.swing.JRadioButton();
        lblFingerCode = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        thumbImage = new javax.swing.JLabel();
        btnCapture = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Finger Print Registration (USG BIOMATRICS)");
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(800, 800));

        jLabel1.setText("1: Kindly Scan your finger 4 times");

        jLabel2.setText("2:This is a long long long long long long long long long long long description....................................");

        jLabel3.setText("3:Another long instruciton........................................................some more text.......................some more text");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Employee Information"));

        txtEmpName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtEmpName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEmpName.setEnabled(false);
        txtEmpName.setSelectionColor(new java.awt.Color(0, 0, 0));

        jLabel6.setText("Employee Name");

        txtFatherName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtFatherName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFatherName.setEnabled(false);

        jLabel7.setText("Father Name");

        txtDesignation.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtDesignation.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDesignation.setEnabled(false);

        jLabel8.setText("Designation");

        jLabel9.setText("Department");

        txtDepartment.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtDepartment.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDepartment.setEnabled(false);

        txtShiftName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtShiftName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtShiftName.setEnabled(false);

        jLabel10.setText("Shift Name");

        txtShiftID.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtShiftID.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtShiftID.setEnabled(false);

        jLabel11.setText("Shift ID");

        lblEmpImage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Employee Picture", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 153))); // NOI18N

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jlRight.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Thumb", "Index", "Middle", "Ring", "Little" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jlRight.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlRightValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jlRight);

        jlLeft.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Thumb", "Index", "Middle", "Ring", "Little" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jlLeft);

        buttonGroup1.add(rbLeft);
        rbLeft.setText("Left");
        rbLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbLeftActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbRight);
        rbRight.setText("Right");

        lblFingerCode.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblFingerCode.setText("......");

        jCheckBox1.setText("jCheckBox1");

        jCheckBox2.setText("jCheckBox1");

        jCheckBox3.setText("jCheckBox1");

        jCheckBox4.setText("jCheckBox1");

        jCheckBox5.setText("jCheckBox1");

        jCheckBox6.setText("jCheckBox1");

        jCheckBox7.setText("jCheckBox1");

        jCheckBox8.setText("jCheckBox1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(16, 16, 16)
                                                .addComponent(jLabel11))))
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)))
                            .addComponent(jLabel7)))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEmpName, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtFatherName, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtDesignation, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtDepartment, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtShiftName, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtShiftID, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtEmpCode, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblUnit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(32, 32, 32))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(rbLeft))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rbRight)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(lblFingerCode))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(lblEmpImage, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox5))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox4)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblEmpImage, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmpCode, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUnit))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtEmpName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtFatherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtDesignation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtShiftName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtShiftID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblFingerCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbRight, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbLeft))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox8)
                    .addComponent(jCheckBox7)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox5)))
        );

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usgbiomatricsapp/customs/usg/images/logo.png"))); // NOI18N

        thumbImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        btnCapture.setText("Capture");
        btnCapture.setEnabled(false);
        btnCapture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCaptureActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(78, 78, 78)
                        .addComponent(btnCapture))
                    .addComponent(thumbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btnCapture)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(thumbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (txtEmpCode.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(null, "Please enter employee code to search.");
            return;
        }
        Employee employee = new DBHandler().getEmployee(txtEmpCode.getText());
        txtEmpName.setText(employee.getName());
        txtDepartment.setText(employee.getDepartment());
        txtDesignation.setText(employee.getDesignation());
        txtEmpCode.setText(employee.getEmpCode());
        txtFatherName.setText(employee.getFatherName());
        txtShiftID.setText(employee.getShiftID());
        txtShiftName.setText(employee.getShiftName());
        lblUnit.setText(employee.getUnitName());
        lblFingerCode.setText(employee.getFingerCode());
        if (employee.getFingerCode().charAt(0)=='L'){
            switch(employee.getFingerCode().charAt(1)){
                case 'T':
                    jlLeft.setSelectedIndex(0);
                    break;
                case 'I':
                    jlLeft.setSelectedIndex(1);
                    break;
                case 'M':
                    jlLeft.setSelectedIndex(2);
                    break;
                case 'R':
                    jlLeft.setSelectedIndex(3);
                    break;
                case 'L':
                    jlLeft.setSelectedIndex(4);
                    break;
            }
        }else if (employee.getFingerCode().charAt(0)=='R'){
            switch(employee.getFingerCode().charAt(1)){
                case 'T':
                    jlRight.setSelectedIndex(0);
                    break;
                case 'I':
                    jlRight.setSelectedIndex(1);
                    break;
                case 'M':
                    jlRight.setSelectedIndex(2);
                    break;
                case 'R':
                    jlRight.setSelectedIndex(3);
                    break;
                case 'L':
                    jlRight.setSelectedIndex(4);
                    break;
            }
        }
        employeeSearched = true;
        if (employeeSearched) {// && fid != null) {
            btnSave.setEnabled(true);
        } else {
            //System.out.println("here");
        }
        Thread imageLoadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lblEmpImage.setIcon(new ImageIcon(getImage(txtEmpCode.getText().trim())));
                    //getImage("163965");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        imageLoadingThread.start();

    }//GEN-LAST:event_btnSearchActionPerformed
    private BufferedImage getImage(String empCode) throws IOException {
        File file = new File("A:\\" + empCode + ".jpg");
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
        } else {
            System.out.println("jpg  not exists");
        }
        BufferedImage bi = ImageIO.read(file);
        return bi;
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        DBHandler db = new DBHandler();
        //if (fid != null) {
        String employeeCode = txtEmpCode.getText();
        String fingerCode = null;
        if (employeeCode.trim().length() > 0) {
            StringWriter sw = new StringWriter();
            //if user is intending to update other information other then thumb.
            if (fid != null) {
                JAXB.marshal(fid, sw);
            } else {
                sw.append("NO_UPDATE");
                System.out.println("no update");
            }
            if (rbLeft.isSelected()) {
                fingerCode = leftFingerCodes[jlLeft.getSelectedIndex()];
            } else if (rbRight.isSelected()) {
                fingerCode = rightFingerCodes[jlRight.getSelectedIndex()];
            } else {
                fingerCode = "NF";//NO FINGER
            }            
            db.updateEmployee(sw.toString(), employeeCode, fingerCode);//fid.Format is supposed to be employee, actually it IS employee :D 
            fid = null;//reset the fid for security purpose
            //disable save button
            btnSave.setEnabled(false);
            capturing = false;
            employeeSearched = false;
            btnCapture.setEnabled(!capturing);
            thumbImage.setIcon(null);
        } else {
            JOptionPane.showMessageDialog(null, "Please search employee first.");
        }
        //} else {
        //  JOptionPane.showMessageDialog(null, "Please scan your finger first.");
        //}
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCaptureActionPerformed
        // TODO add your handling code here:
        new RegisterThread(FPDialogRegister.this).start();
        capturing = true;
        btnCapture.setEnabled(!capturing);
    }//GEN-LAST:event_btnCaptureActionPerformed

    private void jlRightValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlRightValueChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jlRightValueChanged

    private void rbLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLeftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbLeftActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        
        //System.exit(0);
        
    }//GEN-LAST:event_btnCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapture;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> jlLeft;
    private javax.swing.JList<String> jlRight;
    private javax.swing.JLabel lblEmpImage;
    private javax.swing.JLabel lblFingerCode;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JRadioButton rbLeft;
    private javax.swing.JRadioButton rbRight;
    private javax.swing.JLabel thumbImage;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtDesignation;
    private javax.swing.JTextField txtEmpCode;
    private javax.swing.JTextField txtEmpName;
    private javax.swing.JTextField txtFatherName;
    private javax.swing.JTextField txtShiftID;
    private javax.swing.JTextField txtShiftName;
    // End of variables declaration//GEN-END:variables

    private void displayImage(Fid fid) {
        Fid.Fiv view = fid.getViews()[0];
        BufferedImage bImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        bImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        thumbImage.setIcon(new ImageIcon(bImage));
    }
    

    //THIS METHOD WILL BE CALLED WHEN USER HAS SUCCESSFULLY SCANNED HIS FINGER AND TEMPLATE IS COMPLETE. 
    private void process(Fmd enrollmentFmd) {
        if (enrollmentFmd != null) {
            //enable the save button
            //System.out.println("enrollment Fmd isnot null");
            fid = new usgbiomatricsapp.customs.usg.pojo.Fid(enrollmentFmd.getData(), txtEmpCode.getText(), "1.0.0");
            //System.out.println("bool: " + employeeSearched);
            if (employeeSearched) {// && fid != null) {
                btnSave.setEnabled(true);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        RegisterEvent evt = (RegisterEvent) e;
        if (e.getActionCommand().equals(RegisterEvent.ACTION_FEATURES)) {
            if (evt.exception == null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        displayImage(evt.fingerCaptureResult.image);
                    }
                });
                
            } else {
                JOptionPane.showMessageDialog(null, evt.exception);
            }
        } else if (e.getActionCommand().equals(RegisterEvent.ACTION_DONE)) {
            if (evt.exception != null) {
                JOptionPane.showMessageDialog(null, evt.exception);
            } else {
                
                process(evt.enrollmentFmd);
                JOptionPane.showMessageDialog(null, "Registration Complete, size:" + evt.enrollmentFmd.getData().length);
            }
        } else if (e.getActionCommand().equals(RegisterEvent.ACTION_CAPTURE)) {
            if (evt.fingerCaptureResult != null) {
                JOptionPane.showMessageDialog(null, "Bad Quality: " + evt.fingerCaptureResult.quality);
            } else if (null != evt.exception) {
                JOptionPane.showMessageDialog(null, "Error: " + evt.exception);
            } else if (null != evt.readerStatus) {
                JOptionPane.showMessageDialog(null, "Reader Status:" + evt.readerStatus);
            }
        }
        
    }
    
    private class RegisterThread extends Thread implements Engine.EnrollmentCallback {
        
        private ActionListener listener;
        
        public RegisterThread(ActionListener listener) {
            this.listener = listener;
        }
        
        @Override
        public void run() {
            try {
                Engine engine = UareUGlobal.GetEngine();
                /**
                 * BELOW METHOD WILL ADD THE ENROLLMENT CALLBACKK. EXECUTION
                 * WILL NOT PROCEED FURTHER UNLESS WE GET AND FMD FROM
                 * CreateEnrollmentFmd method.
                 */
                
                Fmd fmd = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);
                if (fmd != null) {
                    SendToListener(RegisterEvent.ACTION_DONE, fmd, null, null, null);
                }
            } catch (UareUException e) {
                e.printStackTrace();
                SendToListener(RegisterEvent.ACTION_DONE, null, null, null, e);
            } finally {
                //System.out.println("thread working finished");
            }
            
        }

        /**
         *
         * @param format
         * @return THIS METHOD IS THE CALLBACK METHOD. AFTER CALLING
         * CreteEnrollmentFmd METHOD IN RUN, THIS METHOD GETS CALLED EVERY TIME
         * BY THE ENGINE WHEN USER SCANS THE FINGER. EACH TIME THIS METHOD WILL
         * RETURN A PreEnrollmentFmd, THIS METHOD WILL BE CALLED many TIMES
         * (possibly 4 times if scanned correctly) AND AFTER THIS
         * CreateEnrollmentFmd RETURNS AN EnrollmentFmd.
         */
        public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
            Engine.PreEnrollmentFmd prefmd = null;
            while (prefmd == null && cancel == false) {
                FPCaptureThread captureThread = new FPCaptureThread(FPDialogRegister.this.reader,
                        Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
                captureThread.startFPCaptureThread(null);
                try {
                    captureThread.join(0);//WAIT UNTILL THE CAPTURE THREAD CAPTURES THE FINGER.I.E. WHEN USER SCANS THE FINGER.WE WILL WAIT HERE :)
                } catch (Exception e) {
                }
                
                Engine engine = UareUGlobal.GetEngine();
                
                FPCaptureThread.FPCaptureEvent evt = captureThread.getLastCaptureEvent();
                if (evt.captureResult != null) {
                    if (evt.captureResult.quality == Reader.CaptureQuality.CANCELED) {
                        break;//capturing cancelled
                    } else if (evt.captureResult.quality == Reader.CaptureQuality.GOOD && evt.captureResult.image != null) {
                        try {
                            
                            Fmd fmd = engine.CreateFmd(evt.captureResult.image, Fmd.Format.ANSI_378_2004);
                            //if (fmd.getData().length >= 440) {
                            prefmd = new Engine.PreEnrollmentFmd();
                            prefmd.fmd = fmd;
                            prefmd.view_index = 0;
                            SendToListener(RegisterEvent.ACTION_FEATURES, null, evt.captureResult, null, null);
                            //} else {RegisterEvent.ACTION_FEATURES
                            //  JOptionPane.showMessageDialog(null, "bytes: "+fmd.getData().length);
                            //thumbImage.setIcon(questionMarkImgIco);
                            //}

                        } catch (UareUException ex) {
                            System.out.println("exception");
                            SendToListener(RegisterEvent.ACTION_FEATURES, null, null, null, ex);
                        }
                        
                    } else {
                        System.out.println("quality not good");
                        SendToListener(RegisterEvent.ACTION_CAPTURE, null, evt.captureResult, evt.readerStatus, evt.exception);
                    }
                } else {
                    System.out.println("capture error");
                    SendToListener(RegisterEvent.ACTION_CAPTURE, null, evt.captureResult, evt.readerStatus, evt.exception);
                }
                
            }
            
            return prefmd;
        }
        
        private void SendToListener(String action, Fmd fmd, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
            
            if (null == listener || null == action || action.equals("")) {
                return;
            }
            
            final RegisterEvent evt = new RegisterEvent(this, action, fmd, cr, st, ex);

            //invoke listener on EDT thread
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        listener.actionPerformed(evt);
                    }
                });
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                new FPDialogRegister(null).setVisible(true);
            }
        });
    }
}
