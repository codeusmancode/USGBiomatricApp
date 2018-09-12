/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp;

import usgbiomatricsapp.customs.usg.helper.FPTXmlReader;
import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import usgbiomatricsapp.customs.Global;
import usgbiomatricsapp.customs.NetworkStatus;
import usgbiomatricsapp.customs.usg.UI.FPDialogRegister;
import usgbiomatricsapp.customs.usg.UI.FPDialogVerification;
import usgbiomatricsapp.customs.usg.helper.DBConnection;
import usgbiomatricsapp.customs.usg.helper.DBHandler;
import usgbiomatricsapp.customs.usg.pojo.Employee;

public class MainForm extends JFrame {

    public static String TEMPLATE_PROPERTY = "template";
    NetworkStatus networkStatus;

    public class TemplateFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".fpt");
        }

        @Override
        public String getDescription() {
            return "Fingerprint Template File (*.fpt)";
        }
    }

    

    // CONSTRUCTOR
    public MainForm() {

        
        //settings
        setState(Frame.NORMAL);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("USG Biomatrics Application");
        setResizable(false);

        final JButton enroll = new JButton("Fingerprint Enrollment");
        enroll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Fingerprint Enrollment Clicked");
                onEnroll();
            }
        });

        final JButton verify = new JButton("Fingerprint Verification");
        verify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Fingerprint Verification Clicked");
                onVerify();
            }
        });

        final JButton save = new JButton("Save Fingerprint Template");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Save Fingerprint Template Clicked");
                onSave();
            }
        });

        final JButton load = new JButton("Read Fingerprint Template");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Read Fingerprint Template Clicked");
                onLoad();
            }
        });

        //to exit
        final JButton quit = new JButton("Close");
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //This gets called when you click any button. specially to enable/disable template read/save buttons
        this.addPropertyChangeListener(TEMPLATE_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getNewValue() == evt.getOldValue()) {
                    return;
                }

            }
        });

        //setting the layout of jframe
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(4, 1, 0, 5));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
        center.add(enroll);
        center.add(verify);
        center.add(save);
        center.add(load);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        bottom.add(quit);

        setLayout(new BorderLayout());
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.PAGE_END);

        pack();
        setSize((int) (getSize().width * 1.6), getSize().height);
        setLocationRelativeTo(null);
        //setTemplate(null);commented by usman riaz..default behavior of the app
        //setTemplates(new DPFPTemplate[0]);//replaced by above line. 
        setVisible(true);
        Thread th = new Thread() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //loadTemplates();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            }

        };
        th.start();
    }

    /**
     * Written by usmanriaz Loading XML from database and convert them into
     * Thumb templates and storing in array
     */
    private void loadTemplates() throws Exception {

    }

    private void onEnroll() {
//        EnrollmentForm form = new EnrollmentForm(this);
//        form.setVisible(true);
        FPDialogRegister form = new FPDialogRegister(this);
        form.setVisible(true);
    }

    private void onVerify() {
        FPDialogVerification form = new FPDialogVerification(this, false);
        form.setVisible(true);
    }

    private void onSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new TemplateFileFilter());
        while (true) {
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    if (!file.toString().toLowerCase().endsWith(".fpt")) {
                        file = new File(file.toString() + ".fpt");
                    }
                    if (file.exists()) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                String.format("File \"%1$s\" already exists.\nDo you want to replace it?", file.toString()),
                                "Fingerprint saving",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (choice == JOptionPane.NO_OPTION) {
                            continue;
                        } else if (choice == JOptionPane.CANCEL_OPTION) {
                            break;
                        }
                    }
                    FileOutputStream stream = new FileOutputStream(file);

                    stream.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Fingerprint saving", JOptionPane.ERROR_MESSAGE);
                }
            }
            break;
        }
    }

    private void onLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new TemplateFileFilter());
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainForm();
            }
        });
    }

}
