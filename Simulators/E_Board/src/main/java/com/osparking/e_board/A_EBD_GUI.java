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
package com.osparking.e_board;

import com.osparking.deviceglobal.AcceptManagerTask;
import com.osparking.deviceglobal.DeviceGUI;
import static com.osparking.deviceglobal.DeviceGlobals.setIconList;
import com.osparking.global.names.EBD_DisplaySetting;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.osparking.global.names.Blink_Task;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.*;

import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.ToleranceLevel;
import static com.osparking.global.names.OSP_enums.EBD_Colors.*;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.*;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.*;
import static com.osparking.global.names.OSP_enums.EBD_Fonts.*;
import static com.osparking.global.names.OSP_enums.EBD_Effects.*;
import static com.osparking.global.names.DB_Access.*;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.global.names.OSP_enums.DisplayArea;
import static com.osparking.global.names.OSP_enums.DisplayArea.BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.DisplayArea.TOP_ROW;
import com.osparking.global.names.ParkingTimer;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.text.html.HTML.Tag.HEAD;

/**
<<<<<<< HEAD
 * E-Board Simulator GUI -- Part of OsParking simulator package which is developed by Open Source 
=======
 * E-Board Simulator GUI -- Part of OSParking simulator package which is developed by Open Source 
>>>>>>> osparking/master
 * Parking Inc.
 * Electronic Board simulator consists of a socket reader and display form module.
 * <p>Company Web Site : <a href="http://www.osparking.com">http://www.osparking.com</a><p>
 * <p>(Company logo: <img src ="doc-files/64px.png"/>)</p>
 * 
 * @author Song, YongSeok <Song, YongSeok at Open Source Parking Inc.>
 */
public class A_EBD_GUI extends javax.swing.JFrame implements DeviceGUI {
    ParkingTimer[] parking_Display_OuterTimer =  null;
    ParkingTimer[] parking_Display_InnerTimer = null;
    OuterCycleTask[] outerCycleTask = null;
    InnerCycleTask[] innerCycleTask = null;
    
    private byte ID = 0;
    
    private Timer statusBlinkLED_Timer = null;
    private ParkingTimer acceptManagerTimer = null;
    private DeviceReader reader = null;
    
    private ToleranceLevel tolerance = new ToleranceLevel();
    private Object socketMUTEX = new Object();
    
    private ParkingTimer[] displayRestoreTimer = new ParkingTimer[2];
    public TimerTask displayDefaultTask = null;
    
    public EBD_DisplaySetting defaultDisplaySettings[] = new EBD_DisplaySetting[2]; 
    public int[] prevMsgSN = new int[2]; // the Serial Number of the most recently processed display message
    
    boolean finishingOperation = false;
    
    /**
     * Creates new form Display
     */
    public A_EBD_GUI(byte displayID) {
        initComponents();
        this.ID = displayID;
        
        setResizable(false);
        List<Image> iconList = new ArrayList<Image>();
        String[] iconFilenames = {
            "/e16px.png",             
            "/e32px.png",             
            "/e48px.png",             
            "/e64px.png", 
        };          
        setIconList(iconFilenames, iconList);        
        setIconImages(iconList);            
        
        setTitle("E-Board #" + displayID);
        IDtextField.setText(Integer.toString(displayID));
        
        // put this frme at the top/bottom right corner
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(E_BOARD_WIDTH, E_BOARD_HEIGHT));
        
        if (ID == 1)
            setLocation(new Point(GATE_BAR_WIDTH, 
                    screen.height - E_BOARD_HEIGHT - TASK_BAR_HEIGHT));
        else {
            setLocation(new Point(GATE_BAR_WIDTH * 2 + E_BOARD_WIDTH, 
                    screen.height - E_BOARD_HEIGHT - TASK_BAR_HEIGHT));
        }                     
        
        try {
            String myIP = Inet4Address.getLocalHost().getHostAddress();
            IPaddrTextField.setText(myIP);
        } catch (UnknownHostException e) {}
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        PID_Label.setText("(PID:" + processName.substring(0, processName.indexOf("@")) + ")");
        
        parking_Display_OuterTimer = new ParkingTimer[2];
        parking_Display_InnerTimer = new ParkingTimer[2];
        
        for (int rowNum = 0; rowNum <= 1; rowNum++) {
            parking_Display_OuterTimer[rowNum] = 
                    new ParkingTimer("E_Board" + ID + "_Row" + rowNum + "_outerTimer", false);
            parking_Display_InnerTimer[rowNum] = 
                    new ParkingTimer("E_Board" + ID + "_Row" + rowNum + "_innerTimer", false);
            displayRestoreTimer[rowNum] =
                    new ParkingTimer("E_Board" + ID + "row_" + rowNum + "_displayRestoreTimer", false);
        }        
        
        outerCycleTask = new OuterCycleTask[2];
        innerCycleTask = new InnerCycleTask[2];
        
        //<editor-fold desc="create timers and threads">
        // timer and it's task for the status textbox blinking
        statusBlinkLED_Timer = new Timer("E_Board" + ID + "_MsgBlinkingTimer");
        statusBlinkLED_Timer.scheduleAtFixedRate(
                new Blink_Task(criticalInfoTextField, "Press [Start] to begin operation"), 0, LED_PERIOD);
        
        // create a reader for the socket to the manager and start it
        //</editor-fold>                
//<<<<<<< HEAD
        defaultDisplaySettings[TOP_ROW.ordinal()] = readEBoardUsageSettings(DEFAULT_TOP_ROW);
        defaultDisplaySettings[BOTTOM_ROW.ordinal()] = readEBoardUsageSettings(DEFAULT_BOTTOM_ROW);        
        
        changeE_BoardDisplay(TOP_ROW, defaultDisplaySettings[TOP_ROW.ordinal()]);
        changeE_BoardDisplay(BOTTOM_ROW, defaultDisplaySettings[BOTTOM_ROW.ordinal()]);
//=======
//        defaultDisplaySettings[TOP_ROW] = readEBoardUsageSettings(DEFAULT_TOP_ROW);
//        defaultDisplaySettings[BOTTOM_ROW] = readEBoardUsageSettings(DEFAULT_BOTTOM_ROW);        
//        
//        changeE_BoardDisplay(TOP_ROW, defaultDisplaySettings[TOP_ROW]);
//        changeE_BoardDisplay(BOTTOM_ROW, defaultDisplaySettings[BOTTOM_ROW]);
//>>>>>>> osparking/master
        
        if (DEBUG)
            System.out.println("E Board #" + ID + " started");
        
        reader = new EBoardReader(this);
        reader.start();
        
        statusBlinkLED_Timer.cancel();
        /**
         * Start electrical board socket listener.
         */
        acceptManagerTimer = new ParkingTimer("E_BoardAcceptManagerTimer", false, null, 0, PULSE_PERIOD);
        acceptManagerTimer.runOnce(new AcceptManagerTask(this, E_Board));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        torbBG = new javax.swing.ButtonGroup();
        topTextField = new javax.swing.JTextField();
        botTextField = new javax.swing.JTextField();
        criticalInfoTextField = new javax.swing.JTextField();
        IPaddrTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        PID_Label = new javax.swing.JLabel();
        errorCheckBox = new javax.swing.JCheckBox();
        errIncButton = new javax.swing.JButton();
        errDecButton = new javax.swing.JButton();
        connectionLED = new javax.swing.JLabel();
        PID_Label1 = new javax.swing.JLabel();
        IDtextField = new javax.swing.JTextField();
        seeLicenseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        topTextField.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        topTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        topTextField.setText("E-Board Top Row");
        topTextField.setEnabled(false);
        topTextField.setPreferredSize(new java.awt.Dimension(300, 35));
        topTextField.setVerifyInputWhenFocusTarget(false);

        botTextField.setFont(new java.awt.Font("Arial Black", 1, 20)); // NOI18N
        botTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        botTextField.setText("E-Board Bottom Row");
        botTextField.setEnabled(false);

        criticalInfoTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));

        IPaddrTextField.setEditable(false);
        IPaddrTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        IPaddrTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        IPaddrTextField.setText("127.0.0.1");

        PID_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        PID_Label.setText("(PID)");

        errorCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errorCheckBox.setText("error");

        errIncButton.setIcon(getPlusIcon());
        errIncButton.setBorder(null);
        errIncButton.setBorderPainted(false);
        errIncButton.setContentAreaFilled(false);
        errIncButton.setPreferredSize(new java.awt.Dimension(25, 25));

        errDecButton.setIcon(getMinusIcon());
        errDecButton.setBorder(null);
        errDecButton.setBorderPainted(false);
        errDecButton.setContentAreaFilled(false);
        errDecButton.setPreferredSize(new java.awt.Dimension(25, 25));

        connectionLED.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        connectionLED.setForeground(new java.awt.Color(255, 0, 0));
        connectionLED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        connectionLED.setText("X");
        connectionLED.setToolTipText("");
        connectionLED.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(PID_Label)
                .addGap(18, 18, 18)
                .addComponent(errorCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errIncButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errDecButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectionLED, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(errDecButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(errIncButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(PID_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(errorCheckBox))
                    .addComponent(connectionLED, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        errIncButton.setBorder(null);
        errIncButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errIncButtonActionPerformed(e);
            }

            private void errIncButtonActionPerformed(ActionEvent e) {
                if (errorCheckBox.isSelected()) {
                    if (ERROR_RATE < 0.9)
                    ERROR_RATE += 0.1f;
                    criticalInfoTextField.setText("error probability: "
                        + getFormattedRealNumber(ERROR_RATE, 2));
                } else {
                    criticalInfoTextField.setText("First, select error check box, OK?");
                }
            }
        });
        errDecButton.setBorder(null);
        errDecButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errDecButtonActionPerformed(e);
            }

            private void errDecButtonActionPerformed(ActionEvent e) {
                if (errorCheckBox.isSelected()) {
                    if (ERROR_RATE > 0.10)
                    ERROR_RATE -= 0.1f;
                    criticalInfoTextField.setText("error probability: "
                        + getFormattedRealNumber(ERROR_RATE, 2));
                } else {
                    criticalInfoTextField.setText("First, select error check box, OK?");
                }
            }
        });

        PID_Label1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        PID_Label1.setText("ID");

        IDtextField.setEditable(false);
        IDtextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        IDtextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        IDtextField.setText("1");

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(criticalInfoTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(topTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(botTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 3, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(PID_Label1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(IDtextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(IPaddrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(seeLicenseButton))
                        .addGap(0, 104, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPaddrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PID_Label1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(IDtextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seeLicenseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(criticalInfoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {IDtextField, IPaddrTextField});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {botTextField, topTextField});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        finishingOperation = true;
        closeSocket(reader.getManagerSocket(), "form window is closing");
        if (acceptManagerTimer != null)
            acceptManagerTimer.cancel();
        
        if (reader != null)
            reader.stopOperation("form window is closing");
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on E-Board Simulator");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed
    
    public static int getStrHeight(JTextField jTextField){
        int TextHeight;
        
        TextHeight = jTextField.getFont().getSize();
        
        return TextHeight;
    }
    
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
            java.util.logging.Logger.getLogger(A_EBD_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(A_EBD_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(A_EBD_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(A_EBD_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        initializeLoggers();
        checkOptions(args);
        readSettings();        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Byte displayID = getUniqueGateBarID(DeviceType.E_Board);

                if (displayID > gateCount ) {
                    JOptionPane.showMessageDialog(null, 
                            "Currently " + gateCount + " E-Board programs are running" + System.lineSeparator() 
                            + "No more e-board programs can run", "E-Board Count Exceeded", 
                            JOptionPane.OK_OPTION);
                    return;
                }

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);                
                A_EBD_GUI mainGUI = new A_EBD_GUI(displayID);
                mainGUI.setVisible(true);
                shortLicenseDialog(mainGUI, "E-Board Simulator Program", "upper left");                
            }
        });
    }
    
    /**
     * @return the criticalInfoTextField
     */
    public javax.swing.JTextField getCriticalInfoTextField() {
        return criticalInfoTextField;
    }
    
    /**
     * @return the statusBlinkLED_Timer
     */
    public Timer getStatusBlinkLED_Timer() {
        return statusBlinkLED_Timer;
    }
    
    public void carEnter(){
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField IDtextField;
    public javax.swing.JTextField IPaddrTextField;
    private javax.swing.JLabel PID_Label;
    private javax.swing.JLabel PID_Label1;
    public javax.swing.JTextField botTextField;
    public javax.swing.JLabel connectionLED;
    public javax.swing.JTextField criticalInfoTextField;
    private javax.swing.JButton errDecButton;
    private javax.swing.JButton errIncButton;
    javax.swing.JCheckBox errorCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton seeLicenseButton;
    public javax.swing.JTextField topTextField;
    private javax.swing.ButtonGroup torbBG;
    // End of variables declaration//GEN-END:variables

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public JTextField getManagerIPaddr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ToleranceLevel getTolerance() {
        return tolerance;
    }

    public void setTolerance(ToleranceLevel tolerance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JTextArea getMessageTextArea() {
        return null;
    }

    @Override
    public Object getSocketConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDeviceType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getSocketMUTEX() {
        return socketMUTEX;
    }

    @Override
    public DeviceReader getReader() {
        return reader;
    }

    @Override
    public boolean isSHUT_DOWN() {
        return finishingOperation;
    }

//<<<<<<< HEAD
    synchronized void  changeE_BoardDisplay(DisplayArea row, EBD_DisplaySetting rowSetting)
//=======
//    synchronized void  changeE_BoardDisplay(byte row, EBD_DisplaySetting rowSetting)
//>>>>>>> osparking/master
    {
        JTextField rowTextField = (row == TOP_ROW ? topTextField : botTextField);
         
        rowTextField.setText(rowSetting.verbatimContent);
        
        //<editor-fold desc="-- set new font">
        int currStyle = rowTextField.getFont().getStyle();
        int currSize = rowTextField.getFont().getSize();
        
        switch (rowSetting.textFont) {
            case Dialog:
                rowTextField.setFont(new Font("Dialog", currStyle, currSize));
                break;
                
            case Microsoft_NeoGothic:
                rowTextField.setFont(new Font("Microsoft NeoGothic", currStyle, currSize));
                break;
                
            case Monospaced:
                rowTextField.setFont(new Font("Monospaced", currStyle, currSize));
                break;
                
            case Sans_Serif:
                rowTextField.setFont(new Font("Sans Serif", currStyle, currSize));
                break;
                
            default:
                break;
        }   
        //</editor-fold>        
        
//<<<<<<< HEAD
        ParkingTimer outerTaskTimer = parking_Display_OuterTimer[row.ordinal()];
//=======
//        ParkingTimer outerTaskTimer = parking_Display_OuterTimer[row];
//>>>>>>> osparking/master
        if (outerTaskTimer.hasTask()) {
            outerTaskTimer.cancelTask();
        }
    
//<<<<<<< HEAD
        ParkingTimer innerTaskTimer = parking_Display_InnerTimer[row.ordinal()]; 
//=======
//        ParkingTimer innerTaskTimer = parking_Display_InnerTimer[row]; 
//>>>>>>> osparking/master
        if (innerTaskTimer.hasTask()) {
            innerTaskTimer.cancelTask();
        }        
        
        if (rowSetting.displayPattern == STILL_FRAME && rowSetting.contentType == VERBATIM) {
            rowTextField.setMargin(new Insets(2, 2, 2, 2) );            
            rowTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        } else {
//<<<<<<< HEAD
            outerCycleTask[row.ordinal()] = new OuterCycleTask(this, row, rowSetting);
            if (rowSetting.displayPattern == BLINKING) {
                rowTextField.setMargin(new Insets(2, 2, 2, 2) );            
                rowTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);            
                outerTaskTimer.reschedule(outerCycleTask[row.ordinal()], 0, rowSetting.displayCycle);
            } else {
                outerTaskTimer.runOnce(outerCycleTask[row.ordinal()]);
//=======
//            outerCycleTask[row] = new OuterCycleTask(this, row, rowSetting);
//            if (rowSetting.displayPattern == BLINKING) {
//                rowTextField.setMargin(new Insets(2, 2, 2, 2) );            
//                rowTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);            
//                outerTaskTimer.reschedule(outerCycleTask[row], 0, rowSetting.displayCycle);
//            } else {
//                outerTaskTimer.runOnce(outerCycleTask[row]);
//>>>>>>> osparking/master
            }
        }
        
        //<editor-fold desc="-- set text color">
        switch (rowSetting.textColor) {
            case RED : rowTextField.setDisabledTextColor(new java.awt.Color(255, 0, 0)); break;
            case ORANGE : rowTextField.setDisabledTextColor(new java.awt.Color(255, 125, 0)); break;
            case GREEN : rowTextField.setDisabledTextColor(new java.awt.Color(0, 175, 56)); break;
            case BLACK : rowTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0)); break;
            case BLUE : rowTextField.setDisabledTextColor(new java.awt.Color(0, 0, 255)); break;
        }
        //</editor-fold>
    }

    /**
     * @return the acceptManagerTimer
     */
    public ParkingTimer getAcceptManagerTimer() {
        return acceptManagerTimer;
    }

    /**
     * @param deviceReader the deviceReader to set
     */
    public void setReader(DeviceReader deviceReader) {
        this.reader = deviceReader;
    }

    /**
     * @return the displayRestoreTimer
     */
    public ParkingTimer[] getDisplayRestoreTimer() {
        return displayRestoreTimer;
    }

    /**
     * @return the defaultDisplaySettings
     */
    public EBD_DisplaySetting[] getDefaultDisplaySettings() {
        return defaultDisplaySettings;
    }

    @Override
    public JLabel getConnectionLED() {
        return connectionLED;
    }
}