/* 
 * Copyright (C) 2015 Open Source Parking Inc.(www.osparking.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.osparking.camera;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import com.osparking.camera.stat.CommandPerformance;
import com.osparking.deviceglobal.AcceptManagerTask;
import com.osparking.deviceglobal.DeviceGUI;
import static com.osparking.deviceglobal.DeviceGlobals.setIconList;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.io.FileWriter;
import java.util.HashMap;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.DB_Access.readSettings;
import com.osparking.global.names.JustOneLock;
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import com.osparking.global.names.OSP_enums.VersionType;
import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.ToleranceLevel;
import java.awt.Image;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
<<<<<<< HEAD
 * Camera Simulator GUI -- Part of OsParking simulator package which is developed by Open Source 
=======
 * Camera Simulator GUI -- Part of OSParking simulator package which is developed by Open Source 
>>>>>>> osparking/master
 * Parking Inc.
 * <p>Company Web Site : <a href="http://www.osparking.com">http://www.osparking.com</a><p>
 * <p>(Company logo: <img src ="doc-files/64px.png"/>)</p>
 * 
 * @author Open Source Parking Inc.
 */
public class CameraGUI extends javax.swing.JFrame implements DeviceGUI {
    public static final Object MUTEX_OUT = new Object();
    private byte cameraID = 0;    
    private boolean cameraPausing;
    
    private CameraReader reader = null;
    private Object socketMUTEX = new Object();
    private Object ID_MUTEX = new Object();
    
    private ParkingTimer acceptManagerTimer = null;
    public FileWriter imageID_logFile = null;    

    /**
     * Data relating to Car arrival image transmission.
     */    
    java.util.Timer imageGenerationTimer = null;
    long imageGenerationTimeMs = 0;  // = first transmission start time  
    boolean imageID_Acked = true;
    ParkingTimer imageTransmissionTimer = null;
    
    int generationSN = 0; // to ensure only one image is processed on the manager at any given time
    int imageFileNo = 0; 
    CommandPerformance imagePerf = new CommandPerformance("Image");

    /**
     * stores ACK arrival delay for car image message to the manager program.
     */
    private double delayACKavg; // average
    private double delayACKstdv; // standard deviation
    public static final int POP_SIZE = 100; // population size of delay analysis 
    
    public boolean IP_ID_Verified = false;
    private boolean ID_Ack_arrived = false;
    
    public boolean displayTrialStatus = false;
    private ToleranceLevel tolerance = new ToleranceLevel();
    
    Timer LED_Timer = null;
    private Object socketConnection = new Object();    
    
    private boolean SHUT_DOWN = false;
    
    /**
     * Creates new form CameraGUI
     */
    public CameraGUI(String[] args) {
        initComponents();
        
        cameraID = getUniqueCameraID();
        setTitle("Camera #" + cameraID);
        cameraID_TextField.setText(Integer.toString(cameraID));
        
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        PID_Label.setText("(PID:" + processName.substring(0, processName.indexOf("@")) + ")");
        
        // put this frme at the top/bottom right corner
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int gui_height = (screen.height - TASK_BAR_HEIGHT) / 2;
//        if (this.getSize().height > gui_height)
//            gui_height = this.getSize().height;
        this.setSize(CAMERA_GUI_WIDTH, gui_height);
        int xPoint = screen.width - this.getSize().width;  
        if (cameraID == 1)
            setLocation(new Point(xPoint, 0));
        else {
            setLocation(new Point(xPoint, this.getSize().height));
        }
        
        try {
            String myIP = Inet4Address.getLocalHost().getHostAddress();
            managerIPaddr.setText(myIP);
        } catch (UnknownHostException e) {}        
        
        /**
         * Set icon for the simulated camera program
         */
        List<Image> iconList = new ArrayList<Image>();
        String[] iconFilenames = {
            "/c16px.png",             
            "/c32px.png",             
            "/c48px.png",             
            "/c64px.png", 
        };          
        setIconList(iconFilenames, iconList);              
        setIconImages(iconList);        

        displayCarEntry(getPicLabel(), "readyPicture.png");

        imageTransmissionTimer = new ParkingTimer("Camera" + cameraID + "_TransmissionTimer", false, 
                null, 0, (int)(RESEND_PERIOD * 1.5));
        
        reader = new CameraReader((this));
        reader.start();
        
        acceptConnectionRequest();
    }
    
    /**
     * Determine the ID of sendPicTask being created considering the IDs of cameras already generated.
     * @return a proper ID number for a sendPicTask
     */
    private byte getUniqueCameraID() {
        byte currID = 1; // minimum ID number
        JustOneLock ua;
        
        while (true) {
            if (versionType == VersionType.DEVELOP) 
                ua = new JustOneLock("No" + currID + "CameraDev");
            else
                ua = new JustOneLock("No" + currID + "CameraRun");
            if (ua.isAppActive()) // a sendPicTask with this ID is not running
                currID++; // consider next ID value
            else
                return currID;
        }
    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        wholePanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        picPanel = new javax.swing.JPanel();
        picLabel = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        settingPanel = new javax.swing.JPanel();
        ipPanel = new javax.swing.JPanel();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 32767));
        managerIPaddr = new javax.swing.JTextField();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        showAckTm_Button = new javax.swing.JButton();
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        two_ID_Panel = new javax.swing.JPanel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        cameraID_TextField = new javax.swing.JTextField();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        PID_Label = new javax.swing.JLabel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        pauseButton = new javax.swing.JButton();
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        connStatPanel = new javax.swing.JPanel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        labelServerConnection = new javax.swing.JLabel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        exitButton = new javax.swing.JButton();
        error_Panel = new javax.swing.JPanel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));
        errorCheckBox = new javax.swing.JCheckBox();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        errIncButton = new javax.swing.JButton();
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        errDecButton = new javax.swing.JButton();
        filler25 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        left_Third_Panel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        messageScrollPane = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        criticalInfoTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Camera");
        setFocusTraversalPolicy(new CameraTraversalPolicy());
        setMinimumSize(new java.awt.Dimension(600, 500));
        getContentPane().add(filler3, java.awt.BorderLayout.NORTH);
        getContentPane().add(filler1, java.awt.BorderLayout.WEST);
        getContentPane().add(filler2, java.awt.BorderLayout.EAST);
        getContentPane().add(filler4, java.awt.BorderLayout.SOUTH);

        wholePanel.setMinimumSize(new java.awt.Dimension(351, 460));
        wholePanel.setPreferredSize(new java.awt.Dimension(645, 500));
        wholePanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setMinimumSize(new java.awt.Dimension(351, 460));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.LINE_AXIS));

        leftPanel.setMaximumSize(new java.awt.Dimension(318, 32767));
        leftPanel.setMinimumSize(new java.awt.Dimension(318, 423));
        leftPanel.setPreferredSize(new java.awt.Dimension(318, 435));
        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.PAGE_AXIS));

        picPanel.setMaximumSize(new java.awt.Dimension(318, 231));
        picPanel.setPreferredSize(new java.awt.Dimension(328, 230));

        picLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        picLabel.setPreferredSize(new java.awt.Dimension(318, 231));
        picPanel.add(picLabel);

        leftPanel.add(picPanel);
        leftPanel.add(filler7);

        settingPanel.setMaximumSize(new java.awt.Dimension(318, 164));
        settingPanel.setMinimumSize(new java.awt.Dimension(318, 164));
        settingPanel.setPreferredSize(new java.awt.Dimension(318, 164));
        settingPanel.setLayout(new javax.swing.BoxLayout(settingPanel, javax.swing.BoxLayout.Y_AXIS));

        ipPanel.setMaximumSize(new java.awt.Dimension(318, 40));
        ipPanel.setMinimumSize(new java.awt.Dimension(318, 40));
        ipPanel.setPreferredSize(new java.awt.Dimension(318, 40));
        ipPanel.setLayout(new javax.swing.BoxLayout(ipPanel, javax.swing.BoxLayout.LINE_AXIS));
        ipPanel.add(filler26);

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setText("Camera IP");
        ipPanel.add(jLabel3);
        ipPanel.add(filler27);

        managerIPaddr.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        managerIPaddr.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        managerIPaddr.setText("127.0.0.1");
        managerIPaddr.setMaximumSize(new java.awt.Dimension(120, 28));
        managerIPaddr.setMinimumSize(new java.awt.Dimension(120, 28));
        managerIPaddr.setName("managerIPaddr"); // NOI18N
        managerIPaddr.setPreferredSize(new java.awt.Dimension(120, 28));
        ipPanel.add(managerIPaddr);
        ipPanel.add(filler17);

        showAckTm_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        showAckTm_Button.setText("Statistics");
        showAckTm_Button.setMaximumSize(new java.awt.Dimension(100, 40));
        showAckTm_Button.setMinimumSize(new java.awt.Dimension(100, 40));
        showAckTm_Button.setPreferredSize(new java.awt.Dimension(100, 40));
        showAckTm_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAckTm_ButtonActionPerformed(evt);
            }
        });
        ipPanel.add(showAckTm_Button);

        settingPanel.add(ipPanel);
        settingPanel.add(filler30);

        two_ID_Panel.setMaximumSize(new java.awt.Dimension(318, 40));
        two_ID_Panel.setMinimumSize(new java.awt.Dimension(318, 40));
        two_ID_Panel.setPreferredSize(new java.awt.Dimension(318, 40));
        two_ID_Panel.setLayout(new javax.swing.BoxLayout(two_ID_Panel, javax.swing.BoxLayout.LINE_AXIS));
        two_ID_Panel.add(filler9);

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setText("ID ");
        two_ID_Panel.add(jLabel1);
        two_ID_Panel.add(filler20);

        cameraID_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cameraID_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cameraID_TextField.setText("1");
        cameraID_TextField.setMaximumSize(new java.awt.Dimension(30, 28));
        cameraID_TextField.setMinimumSize(new java.awt.Dimension(30, 28));
        cameraID_TextField.setName("cameraID_TextField"); // NOI18N
        cameraID_TextField.setPreferredSize(new java.awt.Dimension(30, 28));
        cameraID_TextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cameraID_TextFieldFocusLost(evt);
            }
        });
        two_ID_Panel.add(cameraID_TextField);
        two_ID_Panel.add(filler21);

        PID_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        PID_Label.setText("(PID)");
        two_ID_Panel.add(PID_Label);
        two_ID_Panel.add(filler18);

        pauseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        pauseButton.setText("Pause");
        pauseButton.setToolTipText("");
        pauseButton.setEnabled(false);
        pauseButton.setMaximumSize(new java.awt.Dimension(100, 40));
        pauseButton.setMinimumSize(new java.awt.Dimension(100, 40));
        pauseButton.setPreferredSize(new java.awt.Dimension(100, 40));
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });
        two_ID_Panel.add(pauseButton);

        settingPanel.add(two_ID_Panel);
        settingPanel.add(filler29);

        connStatPanel.setMaximumSize(new java.awt.Dimension(318, 40));
        connStatPanel.setMinimumSize(new java.awt.Dimension(318, 40));
        connStatPanel.setPreferredSize(new java.awt.Dimension(318, 40));
        connStatPanel.setLayout(new javax.swing.BoxLayout(connStatPanel, javax.swing.BoxLayout.LINE_AXIS));
        connStatPanel.add(filler12);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Socket");
        jLabel2.setToolTipText("");
        connStatPanel.add(jLabel2);
        connStatPanel.add(filler22);

        labelServerConnection.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelServerConnection.setForeground(new java.awt.Color(255, 0, 0));
        labelServerConnection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelServerConnection.setText("X");
        connStatPanel.add(labelServerConnection);
        connStatPanel.add(filler19);

        exitButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        exitButton.setText("Exit");
        exitButton.setMaximumSize(new java.awt.Dimension(100, 40));
        exitButton.setMinimumSize(new java.awt.Dimension(100, 40));
        exitButton.setPreferredSize(new java.awt.Dimension(100, 40));
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        connStatPanel.add(exitButton);

        settingPanel.add(connStatPanel);

        error_Panel.setMaximumSize(new java.awt.Dimension(318, 30));
        error_Panel.setMinimumSize(new java.awt.Dimension(318, 30));
        error_Panel.setPreferredSize(new java.awt.Dimension(318, 30));
        error_Panel.setLayout(new javax.swing.BoxLayout(error_Panel, javax.swing.BoxLayout.LINE_AXIS));
        error_Panel.add(filler11);

        errorCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errorCheckBox.setText("error");
        errorCheckBox.setToolTipText("");
        errorCheckBox.setMaximumSize(new java.awt.Dimension(100, 28));
        errorCheckBox.setMinimumSize(new java.awt.Dimension(100, 28));
        errorCheckBox.setName("errorCheckBox"); // NOI18N
        errorCheckBox.setPreferredSize(new java.awt.Dimension(100, 28));
        errorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorCheckBoxActionPerformed(evt);
            }
        });
        error_Panel.add(errorCheckBox);
        error_Panel.add(filler23);

        ImageIcon plusIcon = getPlusIcon();
        errIncButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errIncButton.setIcon(plusIcon);
        errIncButton.setBorder(null);
        errIncButton.setPreferredSize(new java.awt.Dimension(20, 20));
        errIncButton.setBorderPainted(false);
        errIncButton.setContentAreaFilled(false);
        errIncButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        errIncButton.setMaximumSize(new java.awt.Dimension(20, 20));
        errIncButton.setMinimumSize(new java.awt.Dimension(20, 20));
        errIncButton.setPreferredSize(new Dimension(plusIcon.getIconWidth(), plusIcon.getIconHeight()));
        error_Panel.add(errIncButton);
        errIncButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errIncButtonActionPerformed(e);
            }

            private void errIncButtonActionPerformed(ActionEvent e) {
                if (errorCheckBox.isSelected()) {
                    if (ERROR_RATE < 0.9) {
                        ERROR_RATE += 0.1f;
                    } else {
                        criticalInfoTextField.setText("current error rate(="
                            + getFormattedRealNumber(ERROR_RATE, 2) + ") is max!");
                    }
                    errorCheckBox.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
                } else {
                    criticalInfoTextField.setText("First, select error check box, OK?");
                }
            }
        });
        error_Panel.add(filler24);

        errDecButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errDecButton.setIcon(getMinusIcon());
        errDecButton.setBorder(null);
        errDecButton.setBorderPainted(false);
        errDecButton.setContentAreaFilled(false);
        errDecButton.setMargin(new java.awt.Insets(2, 2, 2, 14));
        errDecButton.setPreferredSize(new java.awt.Dimension(20, 20));
        error_Panel.add(errDecButton);
        ImageIcon iconMinus = getMinusIcon();
        errDecButton.setIcon(iconMinus);
        errDecButton.setBorder(null);
        errDecButton.setPreferredSize(
            new Dimension(iconMinus.getIconWidth(), iconMinus.getIconHeight()));

        errDecButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errDecButtonActionPerformed(e);
            }

            private void errDecButtonActionPerformed(ActionEvent e) {
                if (errorCheckBox.isSelected()) {
                    if (ERROR_RATE > 0.10) {
                        ERROR_RATE -= 0.1f;
                    } else {
                        criticalInfoTextField.setText("current error rate(="
                            + getFormattedRealNumber(ERROR_RATE, 2) + ") is min!");
                    }
                    errorCheckBox.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
                } else {
                    criticalInfoTextField.setText("First, select error check box, OK?");
                }
            }
        });
        error_Panel.add(filler25);
        error_Panel.add(filler16);

        settingPanel.add(error_Panel);

        leftPanel.add(settingPanel);

        left_Third_Panel.setMaximumSize(new java.awt.Dimension(32832, 28));
        left_Third_Panel.setMinimumSize(new java.awt.Dimension(65, 28));
        left_Third_Panel.setPreferredSize(new java.awt.Dimension(461, 28));
        left_Third_Panel.setLayout(new javax.swing.BoxLayout(left_Third_Panel, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        left_Third_Panel.add(seeLicenseButton);
        left_Third_Panel.add(filler5);

        leftPanel.add(left_Third_Panel);

        centerPanel.add(leftPanel);
        centerPanel.add(filler6);

        messageScrollPane.setPreferredSize(new java.awt.Dimension(163, 250));

        messageTextArea.setEditable(false);
        messageTextArea.setColumns(35);
        messageTextArea.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        messageTextArea.setLineWrap(true);
        messageTextArea.setToolTipText("");
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setAutoscrolls(false);
        messageTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        messageTextArea.setMaximumSize(new java.awt.Dimension(32767, 32767));
        messageTextArea.setOpaque(false);
        messageScrollPane.setViewportView(messageTextArea);

        centerPanel.add(messageScrollPane);

        wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        criticalInfoTextField.setEditable(false);
        criticalInfoTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        criticalInfoTextField.setText("<Critical Status Information>");
        criticalInfoTextField.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        criticalInfoTextField.setMinimumSize(new java.awt.Dimension(6, 28));
        criticalInfoTextField.setPreferredSize(new java.awt.Dimension(166, 28));
        wholePanel.add(criticalInfoTextField, java.awt.BorderLayout.SOUTH);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        setSHUT_DOWN(true);
        
        if (getReader() != null)
            getReader().disconnectSocket(null, "Camera application shut down");
        
        if (LED_Timer != null) {
            LED_Timer.cancel();
            LED_Timer.purge();
            System.out.println("LED11");
        }

        if (reader != null)
            reader.interrupt();
        
        if (imageGenerationTimer != null) {
            imageGenerationTimer.cancel();
            imageGenerationTimer.purge();
        }
        
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed
                   
    private void showAckTm_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAckTm_ButtonActionPerformed
        if (imagePerf.hasData()) {
            String performanceDescription = "Camera #" + cameraID + " performance" + System.lineSeparator() 
                    + imagePerf.getPerformanceDescription(errorCheckBox.isSelected()) + System.lineSeparator();
            addMessageLine(messageTextArea, performanceDescription);
            messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength() 
                    - performanceDescription.length() - 2); // places the caret at the bottom of the display area
        }        
    }//GEN-LAST:event_showAckTm_ButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        if (isCameraPausing() ) {
            pauseButton.setText("Pause");
        } else {
            pauseButton.setText("Continue");
        }
        setCameraPausing(!isCameraPausing());
    }//GEN-LAST:event_pauseButtonActionPerformed
    
    private void acceptConnectionRequest() {
        /**
         * Disconnect manager socket when this gate bar is already connected to the server. 
         */
        //<editor-fold desc="--proceed with syntactically correct manager IP address">
        criticalInfoTextField.setText("");
        criticalInfoTextField.setFont(new Font(
                criticalInfoTextField.getFont().getFontName(), Font.PLAIN, 
                criticalInfoTextField.getFont().getSize()));     

        managerIPaddr.setEnabled(false);
        cameraID_TextField.setEnabled(false);

        acceptManagerTimer = new ParkingTimer("Camera" + cameraID + "_AcceptTimer", false, null,
                0, PULSE_PERIOD);
        acceptManagerTimer.runOnce(new AcceptManagerTask(this, Camera));
        //</editor-fold>

        if (imageGenerationTimer != null) {
            imageGenerationTimer.cancel();
            imageGenerationTimer.purge();
        }
        
        imageGenerationTimer = new Timer("Camera" + cameraID + "_ImageGenerationTimer");
        imageGenerationTimer.schedule(new ImageGenerationTask(this, cameraID) , 0, CAR_PERIOD);
        pauseButton.setEnabled(true);
        messageTextArea.requestFocus();
    }                                            

    private void cameraID_TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cameraID_TextFieldFocusLost
        
        String idStr = cameraID_TextField.getText();
        Integer idInteger = null;
        try {
            idInteger = Integer.decode(idStr);
        } catch(NumberFormatException ne) {
            JOptionPane.showMessageDialog(this, "Entered ID: " + idStr + System.lineSeparator()
                + "Correct example: 1, 2, ...", "ID Number format error", WARNING_MESSAGE);
            cameraID_TextField.requestFocus();
            return;
        }

        if (0 < idInteger && idInteger <= 127) {
            byte tempID = idInteger.byteValue();
            if (tempID != cameraID) {
                cameraID = tempID;

                setTitle("Camera #" + cameraID);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Entered ID: " + idStr + System.lineSeparator()
                + "Correct ID range: 1 ~ 127", "ID Number our of range", WARNING_MESSAGE);
            cameraID_TextField.requestFocus();
        }
    }//GEN-LAST:event_cameraID_TextFieldFocusLost
    
    String managerIPaddrBefore = "";
    
    String cameraID_Before = "";

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Camera Simulator");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void errorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorCheckBoxActionPerformed
        // TODO add your handling code here:
        if(!errorCheckBox.isSelected())
            errorCheckBox.setText("error");
        else
            errorCheckBox.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
    }//GEN-LAST:event_errorCheckBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CameraGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CameraGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CameraGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CameraGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
 
        readSettings();
        initializeLoggers();
        checkOptions(args);  

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // begin dummy sendPicTask operation now
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);                
                CameraGUI mainGUI = new CameraGUI(args);
                mainGUI.setVisible(true); 
                shortLicenseDialog(mainGUI, "Camera Simulator Program", "left lower");                
            }
        });
    }   
    
    public void displayCarEntry(JLabel imageLabel, String filename) {
        try {
            ImageIcon iIcon = null;
            BufferedImage bufImage 
                    = ImageIO.read(getClass().getResourceAsStream("/" + filename));
            iIcon = createStretchedIcon(imageLabel.getPreferredSize(), bufImage, false);
            imageLabel.setIcon(iIcon);  // display car image on the label for the gate
        } catch (IOException e) {
            logParkingExceptionStatus(Level.SEVERE, e, "while reading artificial car image", 
                    criticalInfoTextField, cameraID);
        }
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel PID_Label;
    javax.swing.JTextField cameraID_TextField;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel connStatPanel;
    private javax.swing.JTextField criticalInfoTextField;
    javax.swing.JButton errDecButton;
    private javax.swing.JButton errIncButton;
    javax.swing.JCheckBox errorCheckBox;
    private javax.swing.JPanel error_Panel;
    public javax.swing.JButton exitButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler25;
    private javax.swing.Box.Filler filler26;
    private javax.swing.Box.Filler filler27;
    private javax.swing.Box.Filler filler29;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler30;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel ipPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelServerConnection;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel left_Third_Panel;
    javax.swing.JTextField managerIPaddr;
    private javax.swing.JScrollPane messageScrollPane;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JButton pauseButton;
    private javax.swing.JLabel picLabel;
    private javax.swing.JPanel picPanel;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel settingPanel;
    private javax.swing.JButton showAckTm_Button;
    private javax.swing.JPanel two_ID_Panel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Car entry image display control (JLabel)
     * @return a JLabel for the image display
     */
    public javax.swing.JLabel getPicLabel() {
        return picLabel;
    }

    /**
     * @return car arrival history textual display area
     */
    public javax.swing.JTextArea getMessageTextArea() {
        return messageTextArea;
    }

    /**
     * @return the critical(often exception related) information display text line
     */
    public javax.swing.JTextField getCriticalInfoTextField() {
        return criticalInfoTextField;
    }

    /**
     * @return sendPicTask activity status: 
        -- true, when the most recent car image has confirmed (ACKed) by the manager
        which means the sendPicTask can proceed with the next car arrival. 
        -- false, otherwise.
     */
    public boolean isCameraPausing() {
        return cameraPausing;
    }

    /**
     * @return car entry image ACK arrival delay time (in unit ms) average value for the latest POP_SIZE arrivals.
     */
    public double getDelayACKavg() {
        return delayACKavg;
    }

    /**
     * @param delayACKavg car entry image ACK arrival delay time (in unit ms) average value to set.
     */
    public void setDelayACKavg(double delayACKavg) {
        this.delayACKavg = delayACKavg;
    }

    /**
     * @return car entry image ACK arrival delay time (in unit ms) standard deviation value for the latest 
     * POP_SIZE arrivals.
     */
    public double getDelayACKstdv() {
        return delayACKstdv;
    }

    /**
     * @param delayACKstdv entry image ACK arrival delay time (in unit ms) standard deviation value for the latest 
     * POP_SIZE arrivals to set
     */
    public void setDelayACKstdv(double delayACKstdv) {
        this.delayACKstdv = delayACKstdv;
    }

    /**
     * Generates a descriptive string for the ACK delay statistics.
     * adjusts IMAGE_SEND_PERIOD value based on the gathered statistics.
     * @param avg ACK arrival delay average value
     * @param stdv ACK arrival delay standard deviation value
     * @return ACK statistics description string
     */
    String getACKstatString(double avg, double stdv, int delayMax, float retransCountAvg) {
        StringBuffer sb = new StringBuffer("ACK delay -- avg: ");
        sb.append(String.format("%.2f", avg));
        sb.append("ms, stdv: ");
        sb.append(String.format("%.2f", stdv));
        sb.append("ms, ");
        sb.append(System.lineSeparator());
        sb.append("        max: ");
        sb.append(delayMax);
        sb.append("ms, avg resend count: ");
        sb.append(String.format("%.2f", retransCountAvg));
        sb.append("times/image");
        
        return sb.toString();
    }

    /**
     * @return the managerIP_TextField
     */
    public javax.swing.JTextField getManagerIP_TextField() {
        return getManagerIPaddr();
    }

    /**
     * @param managerIP_TextField the managerIP_TextField to set
     */
    public void setManagerIP_TextField(javax.swing.JTextField managerIP_TextField) {
        this.managerIPaddr = managerIP_TextField;
    }

    /**
     * @param cameraPausing the cameraPausing to set
     */
    public void setCameraPausing(boolean cameraPausing) {
        this.cameraPausing = cameraPausing;
    }

    /**
     * @return the reader
     */
    @Override
    public DeviceReader getReader() {
        return reader;
    }

    @Override
    public byte getID() {
        return cameraID;
    }

    /**
     * @return the managerIPaddr
     */
    public javax.swing.JTextField getManagerIPaddr() {
        return managerIPaddr;
    }

    /**
     * @return the ID_Ack_arrived
     */
    public boolean isID_Ack_arrived() {
        return ID_Ack_arrived;
    }

    /**
     * @return the tolerance
     */
    public ToleranceLevel getTolerance() {
        return tolerance;
    }

    /**
     * @return the labelServerConnection
     */
    public javax.swing.JLabel getConnectionLED() {
        return labelServerConnection;
    }

    /**
     * @return the imageTransmissionTimer
     */
    public ParkingTimer getImageTransmissionTimer() {
        return imageTransmissionTimer;
    }

    /**
     * @return the socketConnection
     */
    public Object getSocketConnection() {
        return socketConnection;
    }

    /**
     * @return the socketMUTEX
     */
    public Object getSocketMUTEX() {
        return socketMUTEX;
    }
    
    public void sendCarImage(byte imageFileNo, int generationSN) {
        String filename = "car" + imageFileNo + ".jpg";
        
        /**
         * send car arrival image to the manager
         */
        imageID_Acked = false;
        this.imageFileNo = imageFileNo;
        imageGenerationTimeMs = System.currentTimeMillis();

        getImageTransmissionTimer().reschedule(new ImageTransmissionTask(this, generationSN, filename));

        // reflect this car arrival to the camera GUI
        addMessageLine(getMessageTextArea(), getTagNumber(imageFileNo) + "(seq #: " + generationSN + ")");
        displayCarEntry(getPicLabel(), filename);        
    }             

    /**
     * @return the ID_MUTEX
     */
    public Object getID_MUTEX() {
        return ID_MUTEX;
    }

    @Override
    public String getDeviceType() {
        return Camera.name();
    }

    @Override
    public boolean isSHUT_DOWN() {
        return SHUT_DOWN;
    }

    /**
     * @param SHUT_DOWN the SHUT_DOWN to set
     */
    public void setSHUT_DOWN(boolean SHUT_DOWN) {
        this.SHUT_DOWN = SHUT_DOWN;
    }

    /**
     * @return the acceptManagerTimer
     */
    public ParkingTimer getAcceptManagerTimer() {
        return acceptManagerTimer;
    }
}

class CameraTraversalPolicy extends FocusTraversalPolicy {
    private static HashMap<String,Component> componentMap = null;

    void makeGUI_ComponentMap(Container aContainer) {
        if (componentMap == null) {
            CameraGUI cameraGUI = (CameraGUI)aContainer;
            
            Component[] components = { cameraGUI.managerIPaddr, cameraGUI.cameraID_TextField,
                cameraGUI.errorCheckBox};
//            componentMap = makeComponentMap(components);
             augmentComponentMap(cameraGUI, componentMap);
        }        
    }
    
    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        makeGUI_ComponentMap(aContainer);
        
        if (aComponent.getName() == null)
            return null;
        
        if (aComponent.getName().equals("managerIPaddr")) {
            return getComponentByName(componentMap, "cameraID_TextField");
        } else if (aComponent.getName().equals("cameraID_TextField")) {
            return getComponentByName(componentMap, "errorCheckBox");
        } else if (aComponent.getName().equals("errorCheckBox")) {
            return getComponentByName(componentMap, "managerIPaddr");
        } else 
            return null;
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        makeGUI_ComponentMap(aContainer);
        
        if (aComponent.getName().equals("cameraID_TextField")) {
            return getComponentByName(componentMap, "managerIPaddr");
        } else if (aComponent.getName().equals("errorCheckBox")) {
            return getComponentByName(componentMap, "cameraID_TextField");
        } else if (aComponent.getName().equals("managerIPaddr")) {
            return getComponentByName(componentMap, "errorCheckBox");
        } else 
            return null;        
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        makeGUI_ComponentMap(aContainer);
        return getComponentByName(componentMap, "managerIPaddr");
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        makeGUI_ComponentMap(aContainer);
        return getComponentByName(componentMap, "errorCheckBox");
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        makeGUI_ComponentMap(aContainer);
        return getComponentByName(componentMap, "managerIPaddr");
    }
}
