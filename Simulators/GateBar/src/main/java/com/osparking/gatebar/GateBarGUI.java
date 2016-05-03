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
package com.osparking.gatebar;

import com.osparking.deviceglobal.AcceptManagerTask;
import com.osparking.deviceglobal.DeviceGUI;
import static com.osparking.deviceglobal.DeviceGlobals.setIconList;
import com.osparking.global.Globals;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.readSettings;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.ERROR_RATE;
import static com.osparking.global.Globals.E_BOARD_WIDTH;
import static com.osparking.global.Globals.GATE_BAR_HEIGHT;
import static com.osparking.global.Globals.GATE_BAR_WIDTH;
import static com.osparking.global.Globals.PULSE_PERIOD;
import static com.osparking.global.Globals.TASK_BAR_HEIGHT;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.getMinusIcon;
import static com.osparking.global.Globals.getPlusIcon;
import static com.osparking.global.Globals.getUniqueGateBarID;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.showLicensePanel;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.ToleranceLevel;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import no.geosoft.cc.graphics.*;

import no.geosoft.cc.geometry.Matrix4x4;

/**
<<<<<<< HEAD
 * Gate Bar Simulator GUI -- Part of OsParking simulator package which is developed by Open Source 
=======
 * Gate Bar Simulator GUI -- Part of OSParking simulator package which is developed by Open Source 
>>>>>>> osparking/master
 * Parking Inc.
 * <p>Company Web Site : <a href="http://www.osparking.com">http://www.osparking.com</a><p>
 * <p>(Company logo: <img src ="doc-files/64px.png"/>)</p>
 * @author Open Source Parking Inc.
 */
public class GateBarGUI extends javax.swing.JFrame implements DeviceGUI {

    //<editor-fold desc="--data members">
    final static int GateOpeningDelayMs = 1000; // unit: ms, from horizontal shape to vertical 
    final static int FigureRedrawPeriodMS = 20; // once every this time, gate bar is redrawn
    final static int MaxCarPassingDelayMS = 2500; // gate bar begins drops after this time in maximum
    final static int MinCarPassingDelayMS = 1200; // gate bar begins drops after this time in minimum
    
    private byte gateBarID = 0;    
    
    BarSimulator gateBar = null; 

    private ParkingTimer timerRotateBar = null;    
    
    private GateBarReader reader = null;
    public ParkingTimer acceptManagerTimer = new ParkingTimer("GateBar_acceptTimer", false, null, 0, PULSE_PERIOD);
    
    boolean ID_Ack_arrived;
    
    public Object ID_confirmed = new Object();
    private Object socketMUTEX = new Object();
    private Object socketConnection = new Object();    

    public ToleranceLevel tolerance = new ToleranceLevel();
    int prevCommandID = 0;
    
    Timer LED_Timer = null;
    
    private boolean SHUT_DOWN = false;  
    private int passingDelay = 0;
    
    //</editor-fold>
    
    /**
     * Creates new form GateBarGUI
     */
    public GateBarGUI(byte gateBarID) {
        initComponents();
        this.gateBarID = gateBarID;
        gateID_TextField.setText(Integer.toString(gateBarID));
        
        setResizable(false);

        /**
         * Set icon for the simulated camera program
         */
        List<Image> iconList = new ArrayList<Image>();
        String[] iconFilenames = {
            "/g16px.png",             
            "/g32px.png",             
            "/g48px.png",             
            "/g64px.png", 
        };          
        setIconList(iconFilenames, iconList);
        setIconImages(iconList);
        setTitle("Gate Bar #" + gateBarID);
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        PID_Label.setText("(PID:" + processName.substring(0, processName.indexOf("@")) + ")");
        
        //<editor-fold desc="-- Create a canvas and display a gate bar on it">
        GWindow window = new GWindow (new Color (210, 235, 255));
        window.getCanvas().setPreferredSize(new Dimension(280, 185));
        barPanel.add(window.getCanvas());
        
        // Create scene with default viewport and world extent settings
        GScene scene = new GScene (window);
        double w0[] = {  0.0,   0.0, 0.0};
        double w1[] = {280.0,   0.0, 0.0};
        double w2[] = {  0.0, 185.0, 0.0};
        scene.setWorldExtent (w0, w1, w2);

        // Create gate bar
        gateBar = new BarSimulator (105, 67, 70); // 410, 
        scene.add (gateBar);        
        barPanel.add(scene.getWindow().getCanvas());
        //</editor-fold>
        
        pack();
        setSize (new Dimension (GATE_BAR_WIDTH, GATE_BAR_HEIGHT));
        
        // put this frme at the top/bottom right corner
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (gateBarID == 1)
            setLocation(new Point(0, screen.height - GATE_BAR_HEIGHT - TASK_BAR_HEIGHT));
        else {
            setLocation(new Point(GATE_BAR_WIDTH + E_BOARD_WIDTH, 
                    screen.height - GATE_BAR_HEIGHT - TASK_BAR_HEIGHT));
        }             

        try {
            String myIP = Inet4Address.getLocalHost().getHostAddress();
            managerIPaddr.setText(myIP);
        } catch (UnknownHostException e) {}        
        
        getContentPane().invalidate();
        
        reader = new GateBarReader(this);
        reader.setName("GateBar_" + gateBarID + "Reader");
        reader.start();   
        //</editor-fold>

        window.startInteraction (new ZoomInteraction (scene));      
        acceptManagerTimer.runOnce(new AcceptManagerTask(this, GateBar));
    }

    /**
     * This method is called from within the constructor to assignMAX the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wholePanel = new javax.swing.JPanel();
        gatePanel = new javax.swing.JPanel();
        barPanel = new javax.swing.JPanel();
        settingPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        gateID_TextField = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 32767));
        jLabel2 = new javax.swing.JLabel();
        managerIPaddr = new javax.swing.JTextField();
        errorPanel = new javax.swing.JPanel();
        PID_Label = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(32767, 0));
        errorCheckBox = new javax.swing.JCheckBox();
        errIncButton = new javax.swing.JButton();
        errDecButton = new javax.swing.JButton();
        ConnectionLED = new javax.swing.JLabel();
        aboutPanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        seeLicenseButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        textFieldPanel = new javax.swing.JPanel();
        criticalInfoTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wholePanel.setRequestFocusEnabled(false);
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        gatePanel.setLayout(new java.awt.BorderLayout());

        barPanel.setAlignmentX(0.0F);
        barPanel.setAlignmentY(0.0F);
        barPanel.setOpaque(false);
        barPanel.setPreferredSize(new java.awt.Dimension(294, 180));
        gatePanel.add(barPanel, java.awt.BorderLayout.CENTER);

        wholePanel.add(gatePanel);

        settingPanel.setMaximumSize(new java.awt.Dimension(32767, 28));
        settingPanel.setMinimumSize(new java.awt.Dimension(123, 28));

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setText("ID");
        settingPanel.add(jLabel1);

        gateID_TextField.setEditable(false);
        gateID_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateID_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gateID_TextField.setText("1");
        gateID_TextField.setEnabled(false);
        gateID_TextField.setMaximumSize(new java.awt.Dimension(30, 28));
        gateID_TextField.setMinimumSize(new java.awt.Dimension(30, 28));
        gateID_TextField.setPreferredSize(new java.awt.Dimension(30, 28));
        //gateID_TextField.addMouseListener(new MouseAdapter(){
            //    public void mouseClicked(MouseEvent e){
                //        startButton.setEnabled(true);
                //    }
            //});
    gateID_TextField.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            gateID_TextFieldFocusLost(evt);
        }
    });
    settingPanel.add(gateID_TextField);
    settingPanel.add(filler5);

    jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    jLabel2.setText("GateBar IP");
    settingPanel.add(jLabel2);

    managerIPaddr.setEditable(false);
    managerIPaddr.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    managerIPaddr.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    managerIPaddr.setText("127.0.0.1");
    managerIPaddr.setToolTipText("Gate Bar IP address");
    managerIPaddr.setEnabled(false);
    managerIPaddr.setMaximumSize(new java.awt.Dimension(120, 28));
    managerIPaddr.setMinimumSize(new java.awt.Dimension(120, 28));
    managerIPaddr.setPreferredSize(new java.awt.Dimension(120, 28));
    //managerIPaddr.addMouseListener(new MouseAdapter(){
        //    public void mouseClicked(MouseEvent e){
            //        startButton.setEnabled(true);
            //    }
        //});
settingPanel.add(managerIPaddr);

wholePanel.add(settingPanel);

errorPanel.setMaximumSize(new java.awt.Dimension(32767, 28));
errorPanel.setMinimumSize(new java.awt.Dimension(182, 28));
errorPanel.setPreferredSize(new java.awt.Dimension(247, 38));

PID_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
PID_Label.setText("(PID)");
errorPanel.add(PID_Label);
errorPanel.add(filler4);

errorCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
errorCheckBox.setText("error");
errorCheckBox.setToolTipText("");
errorCheckBox.setMaximumSize(new java.awt.Dimension(100, 23));
errorCheckBox.setMinimumSize(new java.awt.Dimension(100, 23));
errorCheckBox.setPreferredSize(new java.awt.Dimension(100, 23));
errorCheckBox.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        errorCheckBoxActionPerformed(evt);
    }
    });
    errorPanel.add(errorCheckBox);

    errIncButton.setIcon(getPlusIcon());
    errIncButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    errIncButton.setBorderPainted(false);
    errIncButton.setContentAreaFilled(false);
    errIncButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
    errIncButton.setPreferredSize(new java.awt.Dimension(25, 25));
    errorPanel.add(errIncButton);
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
    ImageIcon iconPlus = getMinusIcon();
    errIncButton.setBorder(null);
    errIncButton.setPreferredSize(
        new Dimension(iconPlus.getIconWidth(), iconPlus.getIconHeight()));

    errDecButton.setIcon(getMinusIcon());
    errDecButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    errDecButton.setBorderPainted(false);
    errDecButton.setContentAreaFilled(false);
    errDecButton.setMargin(new java.awt.Insets(2, 2, 2, 14));
    errDecButton.setPreferredSize(new java.awt.Dimension(25, 25));
    errorPanel.add(errDecButton);
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

    ConnectionLED.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    ConnectionLED.setForeground(new java.awt.Color(255, 0, 0));
    ConnectionLED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    ConnectionLED.setText("X");
    ConnectionLED.setMaximumSize(new java.awt.Dimension(23, 23));
    ConnectionLED.setPreferredSize(new java.awt.Dimension(23, 23));
    errorPanel.add(ConnectionLED);

    wholePanel.add(errorPanel);

    aboutPanel.setMaximumSize(new java.awt.Dimension(32832, 28));
    aboutPanel.setMinimumSize(new java.awt.Dimension(65, 28));
    aboutPanel.setPreferredSize(new java.awt.Dimension(294, 38));
    aboutPanel.setLayout(new javax.swing.BoxLayout(aboutPanel, javax.swing.BoxLayout.LINE_AXIS));
    aboutPanel.add(filler2);

    seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    seeLicenseButton.setText("About");
    seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            seeLicenseButtonActionPerformed(evt);
        }
    });
    aboutPanel.add(seeLicenseButton);
    aboutPanel.add(filler1);

    wholePanel.add(aboutPanel);

    textFieldPanel.setLayout(new javax.swing.BoxLayout(textFieldPanel, javax.swing.BoxLayout.LINE_AXIS));

    criticalInfoTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size-3));
    criticalInfoTextField.setText("Press [Connect] to start");
    criticalInfoTextField.setAlignmentX(1.0F);
    criticalInfoTextField.setAutoscrolls(false);
    criticalInfoTextField.setMaximumSize(new java.awt.Dimension(32767, 28));
    criticalInfoTextField.setName(""); // NOI18N
    criticalInfoTextField.setPreferredSize(new java.awt.Dimension(294, 28));
    criticalInfoTextField.setVerifyInputWhenFocusTarget(false);
    textFieldPanel.add(criticalInfoTextField);

    wholePanel.add(textFieldPanel);

    getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void gateID_TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gateID_TextFieldFocusLost
        String idStr = gateID_TextField.getText();
        Integer idInteger = null;
        try {
            idInteger = Integer.decode(idStr);
        } catch(NumberFormatException ne) {
            JOptionPane.showMessageDialog(this, "Entered ID: " + idStr + System.lineSeparator() 
                    + "Correct example: 1, 2, ...", "ID Number format error", WARNING_MESSAGE);
            gateID_TextField.requestFocus();
            return;
        }
        
        if (0 < idInteger && idInteger <= 127) {
            gateBarID = idInteger.byteValue();
            setTitle("Gate Bar #" + gateBarID);
        }
        else {
            JOptionPane.showMessageDialog(this, "Entered ID: " + idStr + System.lineSeparator() 
                    + "Correct ID range: 1 ~ 127", "ID Number our of range", WARNING_MESSAGE);
            gateID_TextField.requestFocus();
        }
    }//GEN-LAST:event_gateID_TextFieldFocusLost

    private void errorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorCheckBoxActionPerformed
        if(!errorCheckBox.isSelected())
            errorCheckBox.setText("error");
        else
            errorCheckBox.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
    }//GEN-LAST:event_errorCheckBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setSHUT_DOWN(true);
    }//GEN-LAST:event_formWindowClosing

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Gate Bar Simulator");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

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
            java.util.logging.Logger.getLogger(GateBarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GateBarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GateBarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GateBarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                Byte gateBarID = getUniqueGateBarID(DeviceType.GateBar);

                if (gateBarID > gateCount ) {
                    JOptionPane.showMessageDialog(null, "Number of Gate Bars running: " + gateCount + System.lineSeparator()
                        + "No More Gate Bars Creation.", "Too Many Gate Bars", JOptionPane.OK_OPTION);
                    return;
                }                
                
                GateBarGUI mainGUI = new GateBarGUI(gateBarID);
                mainGUI.setVisible(true);
                Globals.shortLicenseDialog(mainGUI, "Gate Bar Simulator", "lower left");
            }
        });
    }

    public void orderOpenGate(int delayMS) {
        passingDelay = delayMS;
        if (timerRotateBar == null) {
            timerRotateBar = new ParkingTimer("GateBar_RotateBar" + gateBarID + "Timer", false, new BarRotator(gateBar), 
                    0, FigureRedrawPeriodMS);
        }       
        synchronized (gateBar) {
            gateBar.setBarStatus(BarStatus.Opening);
            timerRotateBar.reschedule();
        }
    }

    /**
     * @return the criticalInfoTextField
     */
    public javax.swing.JTextField getCriticalInfoTextField() {
        return criticalInfoTextField;
    }

    
    @Override
    public byte getID() {
        return gateBarID;
    }

    @Override
    public DeviceReader getReader() {
        return reader;
    }

    @Override
    public JTextField getManagerIPaddr() {
        return managerIPaddr;
    }

    @Override
    public ToleranceLevel getTolerance() {
        return tolerance;
    }

    public JLabel getLabelServerConnection() {
        return ConnectionLED;
    }

    @Override
    public JTextArea getMessageTextArea() {
        return null;
    }

    public Object getSocketMUTEX() {
        return socketMUTEX;
    }

    @Override
    public Object getSocketConnection() {
        return socketConnection;
    }

    @Override
    public String getDeviceType() {
        return GateBar.name();
    }

    @Override
    public JLabel getConnectionLED() {
        return ConnectionLED;
    }

    @Override
    public boolean isSHUT_DOWN() {
        return SHUT_DOWN;
    }

    @Override
    public com.osparking.global.names.ParkingTimer getAcceptManagerTimer() {
        return acceptManagerTimer;
    }

    /**
     * @param SHUT_DOWN the SHUT_DOWN to set
     */
    public void setSHUT_DOWN(boolean SHUT_DOWN) {
        this.SHUT_DOWN = SHUT_DOWN;
    }

    private class BarRotator extends TimerTask
    {
        private BarSimulator gateBar;

        public BarRotator (BarSimulator gateBar)
        {
            this.gateBar = gateBar;
        }

        public void run()
        {
            gateBar.update();
            gateBar.refresh();
        }
    }
    
    private class BarSimulator extends GObject
    {
        private double      x0_, y0_;
        private double      radius_;
        private GSegment    background_;
        private GSegment    barHousing;
        private GSegment    stoppingBar;
        private double[]    stoppingBarGeometry_;

        private BarStatus barStatus = BarStatus.Closed;
        private double barAngle = 0;
        Random rand = new Random();

        public BarSimulator (double x0, double y0, double radius)
        {
            x0_     = x0;
            y0_     = y0;
            radius_ = radius;

            background_ = new GSegment();
            GStyle backgroundStyle = new GStyle();
            backgroundStyle.setBackgroundColor (new Color (122, 136, 161));
            backgroundStyle.setForegroundColor (new Color (0, 0, 0));
            background_.setStyle (backgroundStyle);
            addSegment (background_);

            GStyle barStyle = new GStyle();
            barStyle.setForegroundColor (new Color (0, 0, 0));
            barStyle.setLineWidth (1);

            GStyle gateHousingStyle = new GStyle();
            gateHousingStyle.setForegroundColor (new Color (0.0f, 0.0f, 0.0f, 0.5f));
            gateHousingStyle.setBackgroundColor (new Color (1.0f, 0.85f, 0.0f, 0.9f));
            gateHousingStyle.setLineWidth (3);  

            GStyle gateBarStyle = new GStyle();
            gateBarStyle.setForegroundColor (new Color (0.0f, 0.0f, 0.0f, 0.5f));
            gateBarStyle.setBackgroundColor (new Color (0.0f, 0.0f, 0.0f, 0.3f));
            gateBarStyle.setLineWidth (1);      

            barHousing = new GSegment();
            barHousing.setStyle (gateHousingStyle);
            addSegment (barHousing);

            stoppingBar = new GSegment();
            stoppingBar.setStyle (gateBarStyle);      
            addSegment (stoppingBar);

            stoppingBarGeometry_ = new double[] {
                                   - 0.20*radius_, - 0.04*radius_,  // top left
                                   - 0.20*radius_, + 0.04*radius_, // bottom left
                                   + 1.30*radius_, + 0.04*radius_, // bottom right
                                   + 1.31*radius_, 0.0,                     // center right point
                                   + 1.30*radius_, - 0.04*radius_, // top right
                                   - 0.20*radius_, - 0.04*radius_  // top left
            };
        }

        public void draw()
        {    
          // Draw the housing of the gate bar
          double[] geometry = new double[] {
              80, 70, // upper left corner
              114, 70, // upper right corner
              114, 4, // lower right corner
              80, 4, // lower left corner
              80, 70}; // upper left corner
          barHousing.setGeometryXy (geometry);
          update();
        }    

        private void update()
        {    
            switch (barStatus) {

                case Closing:
                    synchronized (gateBar) {
                        setBarAngle(getBarAngle() - Math.PI / (2.0 * GateOpeningDelayMs / FigureRedrawPeriodMS));
                        if (getBarAngle() <= 0) {
                            setBarAngle(0);
                            barStatus = BarStatus.Closed;
                            timerRotateBar.cancelTask();
                        }
                    }
                    break;

                case Opening:
                    synchronized (gateBar) {
                        setBarAngle(getBarAngle() + 
                                  Math.PI / (2.0 * GateOpeningDelayMs / FigureRedrawPeriodMS));
                        if (getBarAngle() >=  Math.PI / 2) {
                            setBarAngle(Math.PI / 2);
                            barStatus = BarStatus.Closing;
                            
                            timerRotateBar.cancelTask();

                            // schedule to close the gate bar
                            if (timerRotateBar == null) {
                                timerRotateBar = new ParkingTimer("timerRotateBar_" + gateBarID, false, 
                                        new BarRotator(gateBar), 0, FigureRedrawPeriodMS);
                            }                         
                            timerRotateBar.reschedule(passingDelay);    
                        }
                    }
                    break;
                    
                default:
                    break;
            }

            double[] geometry = new double[stoppingBarGeometry_.length];
            System.arraycopy (stoppingBarGeometry_, 0, geometry, 0,
                              stoppingBarGeometry_.length);
            Matrix4x4 m = new Matrix4x4();
            m.rotateZ (getBarAngle());
            m.translate (x0_, y0_, 0.0);      
            m.transformXyPoints (geometry);
            stoppingBar.setGeometryXy (geometry);
        }

        /**
         * @return the barStatus
         */
        public BarStatus getBarStatus() {
            return barStatus;
        }

        /**
         * @param barStatus the barStatus to set
         */
        public void setBarStatus(BarStatus barStatus) {
            this.barStatus = barStatus;
        }

        /**
         * @return the barAngle
         */
        public double getBarAngle() {
            return barAngle;
        }

        /**
         * @param minuteAngle the barAngle to set
         */
        public void setBarAngle(double minuteAngle) {
            this.barAngle = minuteAngle;
        }
    }    
    
    //<editor-fold desc="--visual components-GUI generated">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel ConnectionLED;
    private javax.swing.JLabel PID_Label;
    private javax.swing.JPanel aboutPanel;
    private javax.swing.JPanel barPanel;
    javax.swing.JTextField criticalInfoTextField;
    javax.swing.JButton errDecButton;
    private javax.swing.JButton errIncButton;
    javax.swing.JCheckBox errorCheckBox;
    private javax.swing.JPanel errorPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JTextField gateID_TextField;
    private javax.swing.JPanel gatePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    javax.swing.JTextField managerIPaddr;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel settingPanel;
    private javax.swing.JPanel textFieldPanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}

enum BarStatus {
    Closed,
    Closing,
    Opening
}
