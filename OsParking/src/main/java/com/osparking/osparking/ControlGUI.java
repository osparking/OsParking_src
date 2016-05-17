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
package com.osparking.osparking;

import com.osparking.attendant.AttListForm;
import com.osparking.attendant.LoginEventListener;
import com.osparking.attendant.LoginForm;
import com.osparking.attendant.LoginWindowEvent;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import com.osparking.global.names.CarAdmission;
import static com.osparking.global.names.ControlEnums.ButtonTypes.ARRIVALS_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CAR_ARRIVAL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.STATISTICS_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.USERS_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.VEHICLES_BTN;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.MAIN_GUI_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.OPEN_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.STATUS_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.AFFILIATION_MENU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.ARRIVAL_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.BOOTING_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.DRIVERS_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.LOGIN_MENU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.LOGIN_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.LOGIN_RECORD_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.LOGOUT_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.MANAGER_MANU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.MANAGE_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.QUIT_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.RECORD_MENU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.SETTING_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.SYSTEM_MENU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.USERS_MENU;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.VEHICLE_MANAGE_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.VEHICLE_MENU;
import static com.osparking.global.names.DB_Access.deviceType;
import static com.osparking.global.names.DB_Access.enteranceAllowed;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.gateNames;
import static com.osparking.global.names.DB_Access.readEBoardSettings;
import static com.osparking.global.names.DB_Access.readSettings;
import com.osparking.global.names.EBD_DisplaySetting;
import com.osparking.global.names.GatePanel;
import com.osparking.global.names.ImageDisplay;
import com.osparking.global.names.JDBCMySQL;
import com.osparking.global.names.ManagerGUI;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.BarOperation;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.GATE_NAME;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.REGISTRATION_STAT;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.VEHICLE_TAG;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.VERBATIM;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.CAR_ENTRY_TOP_ROW;
import com.osparking.global.names.OSP_enums.EBD_Row;
import com.osparking.global.names.OSP_enums.E_BoardType;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT1;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT2;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.OSP_enums.PermissionType;
import static com.osparking.global.names.OSP_enums.PermissionType.ALLOWED;
import static com.osparking.global.names.OSP_enums.PermissionType.BADTAGFORMAT;
import static com.osparking.global.names.OSP_enums.PermissionType.DISALLOWED;
import static com.osparking.global.names.OSP_enums.PermissionType.UNREGISTERED;
import com.osparking.global.names.ParentGUI;
import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.SocketConnStat;
import com.osparking.global.names.ToleranceLevel;
import com.osparking.osparking.device.CameraManager;
import com.osparking.osparking.device.CameraMessage;
import com.osparking.osparking.device.ConnectDeviceTask;
import com.osparking.osparking.device.EBoardManager;
import com.osparking.osparking.device.GateBarManager;
import com.osparking.global.names.IDevice;
import com.osparking.global.names.IDevice.IE_Board;
import static com.osparking.global.names.OSP_enums.GateBarType.NaraBar;
import com.osparking.osparking.device.BlackFly.BlackFlyManager;
import static com.osparking.osparking.device.BlackFly.BlackFlyManager.ImgHeight;
import static com.osparking.osparking.device.BlackFly.BlackFlyManager.ImgWidth;
import com.osparking.osparking.device.LED_Task;
import com.osparking.osparking.device.LEDnotice.FinishLEDnoticeIntrTask;
import com.osparking.osparking.device.LEDnotice.LEDnoticeManager;
import static com.osparking.osparking.device.LEDnotice.LEDnoticeManager.ledNoticeSettings;
import com.osparking.osparking.device.LEDnotice.LedProtocol;
import com.osparking.osparking.device.NaraBar.NaraBarMan;
import com.osparking.osparking.device.NaraBar.NaraEnums.Nara_MsgType;
import com.osparking.osparking.device.NaraBar.NaraMessageQueue.NaraMsgItem;
import com.osparking.osparking.device.SendEBDMessageTask;
import com.osparking.osparking.device.SendGateOpenTask;
import com.osparking.osparking.statistics.DeviceCommand;
import com.osparking.osparking.statistics.PassingDelayStat;
import com.osparking.statistics.CarArrivals;
import com.osparking.vehicle.AffiliationBuildingForm;
import com.osparking.vehicle.VehiclesForm;
import com.osparking.vehicle.driver.ManageDrivers;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bytedeco.javacpp.FlyCapture2;
import static org.bytedeco.javacpp.FlyCapture2.PIXEL_FORMAT_BGR;

/**
 * Main GUI of OsParking -- Open Source Parking Lot Management Program which is
 * developed by Open Source Parking Inc.
 * <p>Company Web Site : <a href="http://www.osparking.com">http://www.osparking.com</a></p>
 * <p>(Company logo: <img src ="doc-files/64px.png"/>)</p>
 * 
 * @author Open Source Parking Inc.
 */
public final class ControlGUI extends javax.swing.JFrame implements ActionListener, ManagerGUI, ParentGUI {
    
    private LoginForm loginForm = null;
    AttListForm attendantsListForm = null;
    AffiliationBuildingForm affiliationBuildingForm = null;
    Rectangle rect = new Rectangle();
    private Settings_System configureSettingsForm = null;
    RunRecordList showRunRecordForm = null;
    LoginRecordList showLoginRecordForm = null;
    static int count = 0;
    static boolean resizedEventDisabled = false;
    ListSelectionModel[] listSelectionModel = null;
    private static int[] shownImageRow = new int[5];
    java.util.Timer hwSimulationTimer = null;
    public boolean[] isGateBusy = {false, false, false, false, false};
        // for simulated camera (process) only
    private JPanel [] statusPanels = null;
    private JLabel [] e_boardLEDs = null;
    private JLabel [] gateBarLEDs = null;
    private boolean SHUT_DOWN = false;  
    
    /**
     * Data members for peripheral devices (camera, gate bar, e-board)
     */
    private IDevice.IManager[][] deviceManagers = null;
    private JLabel[][] deviceConnectionLabels;
    public ToleranceLevel[][] tolerance = null;
    private Object[][] socketMutex = null;
    private SocketConnStat[][] sockConnStat = null; // Socket Connection Status
    private DeviceCommand[][] perfomStatistics = null;    

    private ParkingTimer[] openGateCmdTimer = null;
    
    public static EBD_DisplaySetting[] EBD_DisplaySettings 
            = new EBD_DisplaySetting[OSP_enums.EBD_DisplayUsage.values().length];   
       
    public int[] prevImgSN = null; // the ID of the most recently processed car entry image    
    
    // data items for gate bars
    public int[] openCommandIDs = null;    
    public boolean[] openCommAcked = null;
    public long[] openCommandIssuedMs = null; // time when a gate open command issued.
    
    /**
     * text file used to store open command IDs acked from the gate bar.
     */
    private FileWriter[][] IDLogFile = null; 
    
    /**
     * Data relating to E-Board interrupt messages.
     */
    boolean[] interruptsAcked = null;
    
    /** 
     * Storage for vehicle processing performance by this system(Parking Manager).
     */
    private PassingDelayStat[] passingDelayStat = null;
        
    /**
     * @return the shownImageRow
     */
    public static int[] getShownImageRow() {
        return shownImageRow;
    }

    /**
     * @param aShownImageRow the shownImageRow to set
     */
    public static void setShownImageRow(int[] aShownImageRow) {
        shownImageRow = aShownImageRow;
    }

    /**
     * @return the gatePanel
     */
    public static GatePanel getGatePanel() {
        return gatePanel;
    }
    
    /**
     * used to notify user intention to shut down the manager to all threads 
     * and make them gracefully finish their jobs
     */
    public DeviceType deviceToContinue = null;
    public Object[] BarConnection = null;
    Timer periodicallyCheckSystemTimer = new Timer("ospMaxRecordChecker");
    
    private ParkingTimer[][] connectDeviceTimer = null;
    public long[][] eBoardMsgSentMs = null;
    private ParkingTimer[][] sendEBDmsgTimer = null;
    
    public int[] msgSNs = null; 
    public Timer LED_Timer = null;

    static Random gateRandomOpen = new Random(System.currentTimeMillis());
    
    Properties prop = null;
    
    /**
     * Creates new form MainForm
     */
    public ControlGUI() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        for (int i = 1; i <= gateCount; i++) {
            admissionListModel[i] = new DefaultListModel<CarAdmission>();
        }
            
        initComponents();   
        
        /**
         * Hide unnecessary status panels
         */
        statusPanels = new JPanel[MAX_GATES + 1];
        statusPanels[1] = statusPanelGate1;
        statusPanels[2] = statusPanelGate2;
        statusPanels[3] = statusPanelGate3;
        for (int i = gateCount + 1; i <=MAX_GATES; i++) {
            statusPanels[i].setVisible(false);
        }        

        deviceConnectionLabels = new JLabel[DeviceType.values().length][MAX_GATES + 1];
        deviceConnectionLabels[DeviceType.Camera.ordinal()][1] = labelCamera1;
        deviceConnectionLabels[DeviceType.Camera.ordinal()][2] = labelCamera2;
        deviceConnectionLabels[DeviceType.Camera.ordinal()][3] = labelCamera3;
        
        deviceConnectionLabels[DeviceType.E_Board.ordinal()][1] = labelE_Board1;
        deviceConnectionLabels[DeviceType.E_Board.ordinal()][2] = labelE_Board2;
        deviceConnectionLabels[DeviceType.E_Board.ordinal()][3] = labelE_Board3;
        
        deviceConnectionLabels[DeviceType.GateBar.ordinal()][1] = labelBar1;
        deviceConnectionLabels[DeviceType.GateBar.ordinal()][2] = labelBar2;
        deviceConnectionLabels[DeviceType.GateBar.ordinal()][3] = labelBar3;
        
        /**
         * Set icon for the simulated camera program
         */
        setIconImages(OSPiconList);
        
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/OsParking.properties");
        this.prop = new Properties();
        try
        {
            this.prop.load( resourceAsStream );
        } catch (IOException e){
        }
        setTitle("OsParking(" + this.prop.getProperty("osparking.current.version") 
                + ")--" + MAIN_GUI_TITLE.getContent());   
        
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        PID_Label.setText("(PID:" + processName.substring(0, processName.indexOf("@")) + ")");        
        
        // Create Panels for gates on the major lower right area
        setGatesAndRestPanel();
        
        Container wholePane = this.getContentPane();
        wholePane.setBackground(MainBackground);
        
        startClock();
        initMessagelLines();
        initCarEntryList();
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        // Put the window at the top left corner
        setLocation(new Point(0, 0));
        setSize(screen.width - CAMERA_GUI_WIDTH, screen.height - GATE_BAR_HEIGHT - TASK_BAR_HEIGHT);
        
        // Info' make below lines comments after ths camera-less simulation phase completes
        formCameraMessageArray();
        addMessageLine(MessageTextArea, "" );
        String message = "System started";
        addMessageLine(MessageTextArea, message);
        logParkingOperation(OpLogLevel.LogAlways, message);
        
        int deviceCount = DeviceType.values().length; // dc: Device (type)  Count
        tolerance = new ToleranceLevel[deviceCount][gateCount + 1];
        socketMutex = new Object[deviceCount][gateCount + 1];
        
        for (int di = 0; di <deviceCount; di++ ) {
            for (int gi = 1; gi <= gateCount; gi++) {
                tolerance[di][gi] = new ToleranceLevel();
                socketMutex[di][gi] = new Object();            
            }
        }

        perfomStatistics = new DeviceCommand[DeviceType.values().length][gateCount + 1]; // column 0 is unused
        openGateCmdTimer = new ParkingTimer[gateCount + 1];
        IDLogFile = new FileWriter[DeviceType.values().length][gateCount + 1]; 
        
        sockConnStat = new SocketConnStat[DeviceType.values().length][gateCount + 1];
        connectDeviceTimer = new ParkingTimer[DeviceType.values().length][gateCount + 1];
        deviceManagers = new IDevice.IManager[DeviceType.values().length][gateCount + 1]; 
        sendEBDmsgTimer = new ParkingTimer[gateCount + 1][4]; // 2: for two rows of each elec' board.
        
        errorCheckBox.setEnabled(DEBUG);
        errIncButton.setEnabled(DEBUG);
        errDecButton.setEnabled(DEBUG);

        openCommandIDs = new int[gateCount + 1];        
        openCommAcked = new boolean[gateCount + 1];        
        BarConnection = new Object[gateCount + 1];
        passingDelayStat = new PassingDelayStat[gateCount + 1];
        
        openCommAcked[0] = true;        
        
        for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
            openCommandIDs[gateNo] = 0;
            openCommAcked[gateNo] = true;    
            openGateCmdTimer[gateNo] 
                    = new ParkingTimer("ospOpenBar" + gateNo + "Timer", false, null, 0, RESEND_PERIOD);            
            BarConnection[gateNo] = new Object();
            passingDelayStat[gateNo] = new PassingDelayStat();
            
            for (DeviceType type : DeviceType.values()) {     
                connectDeviceTimer[type.ordinal()][gateNo] = new ParkingTimer(
                        "ospConnect_" + type + "_" + gateNo + "_timer", false);            
            }
            
            for (EBD_Row row : EBD_Row.values()) {
                String timerName = "ospEBD" + gateNo + "_R" + row.getValue() + "_msgTimer";
                sendEBDmsgTimer[gateNo][row.ordinal()] 
                        = new ParkingTimer(timerName, false, null, 0L, RESEND_PERIOD); 
            }            
        }          
        
        for (DeviceType type : DeviceType.values()) {     
            for (int gNo = 1; gNo <= gateCount; gNo++) {
                if (DEBUG) {
                    /**
                     * Prepare camera image ID log file.
                     */
                    prepareIDLogFile(type, gNo);
                }
                String command = null;
                if (type == GateBar)
                    command = "Open";
                else 
                    command = "Interrupt";
                perfomStatistics[type.ordinal()][gNo] = new DeviceCommand(command);
            }  
            
            for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
                sockConnStat[type.ordinal()][gateNo] = new SocketConnStat(this, type, gateNo);
                
                switch (type) {
                    case Camera: 
                        switch (Globals.gateDeviceTypes[gateNo].cameraType) {
                            case Blackfly:
                                deviceManagers[type.ordinal()][gateNo]
                                        = (IDevice.IManager)new BlackFlyManager(this, gateNo);
                                break;
                                
                            default:
                                deviceManagers[type.ordinal()][gateNo] 
                                        = (IDevice.IManager)new CameraManager(this, gateNo);
                                break;
                        }                        
                        break;
                        
                    case E_Board: 
                        switch (Globals.gateDeviceTypes[gateNo].eBoardType) {
                            case LEDnotice:
                                deviceManagers[type.ordinal()][gateNo]
                                        = (IDevice.IManager)new LEDnoticeManager(this, gateNo);
                                break;
                                
                            default:
                                deviceManagers[type.ordinal()][gateNo] 
                                        = (IDevice.IManager)new EBoardManager(this, gateNo);
                                break;
                        }
                        break;
                        
                    case GateBar: // deviceType[GateBar.ordinal()][gateID]
                        switch (Globals.gateDeviceTypes[gateNo].gateBarType) {
                            case NaraBar:
                                deviceManagers[type.ordinal()][gateNo] 
                                        = (IDevice.IManager)new NaraBarMan(this, gateNo);
                                break;
                                
                            default:
                                deviceManagers[type.ordinal()][gateNo] 
                                        = (IDevice.IManager)new GateBarManager(this, gateNo);
                                break;
                        }
                        break;
                }
                // start server socket listeners for all types of devices
                connectDeviceTimer[type.ordinal()][gateNo].runOnce(new ConnectDeviceTask(this, type, gateNo));
                
                if (deviceManagers[type.ordinal()][gateNo] != null) {
//                    JOptionPane.showMessageDialog(this, "start");
                    deviceManagers[type.ordinal()][gateNo].start();
                }
            }
        }             

        openCommandIssuedMs = new long[gateCount + 1];
        
        /**
         * Delete car arrival records older than some designated number of days.
         * Old car arrival records are checked once every 6 hours after the initial check when system boots.
         */
        periodicallyCheckSystemTimer.schedule(new SystemChecker(this), 1000, SIX_HOURS);        
        
        prevImgSN = new int[gateCount + 1];
        
        msgSNs = new int[gateCount + 1];        
        interruptsAcked = new boolean[gateCount + 1];        
        eBoardMsgSentMs = new long[gateCount + 1][2];
        
        interruptsAcked[0] = true;           
        for (int gateNo = 1; gateNo <= gateCount; gateNo++) {
            interruptsAcked[gateNo] = true;
        }             
        
        /**
         * Start socket connection status display timer and schedule it.
         */
        LED_Timer = new Timer("ospLEDtimer", true);
        LED_Timer.schedule(new LED_Task(this, getDeviceManagers()), 0, LED_PERIOD);
        
        processLogIn(null); // shortcut
    }
    
    private void prepareIDLogFile(DeviceType devType, int gateNo) {
        StringBuilder pathname = new StringBuilder();
        StringBuilder daySB = new StringBuilder();

        getPathAndDay("operation", pathname, daySB);

        // full path name of the today's text file for Open command ID logging
        String barOpenLogFilePathname = pathname + File.separator 
                + daySB.toString() + "_" + devType + "_" + gateNo + ".txt";
        try {
            IDLogFile[devType.ordinal()][gateNo] = new FileWriter(barOpenLogFilePathname, false); 
            String header = "";
            
            switch (devType) {
                case Camera:
                    header = "* IDs of images came from Camera #" + gateNo
                            + System.lineSeparator() + "<current> <previous>";
                    break;
                case E_Board:
                    header = "* Seq' numbers of display INTERRUPT messages sent to E-Board #" + gateNo
                            + System.lineSeparator() + "<Sequence number>";
                    break;
                case GateBar:
                    header = "* ID numbers of Open commands sent to Gate #" + gateNo 
                            + System.lineSeparator() + "<ID number>(negative: random attendant)";
                    break;
            }
            IDLogFile[devType.ordinal()][gateNo].write(header + System.lineSeparator());
            IDLogFile[devType.ordinal()][gateNo].flush();
        } catch (FileNotFoundException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "prepare logging file", getStatusTextField(), GENERAL_DEVICE);
        } catch (IOException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "prepare logging file", getStatusTextField(), GENERAL_DEVICE);
        }                 
    }      
    
    /**
     * @return the perfomStatistics
     */
    public DeviceCommand[][] getPerfomStatistics() {
        return perfomStatistics;
    }    

    static void show100percentSizeImageOfGate(int gateNo, BufferedImage originalImage) {
        if (getGatePanel().getCarPicLabels()[gateNo].getIcon() != null)
        {
            int picIconWidth = getGatePanel().getCarPicLabels()[gateNo].getIcon().getIconWidth();
            if (originalImgWidth[gateNo] * 0.95 > picIconWidth)
            {
                ImageDisplay bigImage = new ImageDisplay(originalImage,
                    gateNames[gateNo] + "--100% car arrival image");
                bigImage.setVisible(true);               
            }        
        }
    }   
    
    private void changeLogIOitemVisibility() {
        if (Globals.loginID == null) {
            MenuItems_setEnabled(false);
            LogInOutMenu.setText("<HTML>Log <U>I</U>n</HTML>");
            UserIDLabelMenu.setText(IDBeforeLogin);
            IsManagerLabelMenu.setText("Manager : -  ");
        } else {
            MenuItems_setEnabled(true);
            
            LogInOutMenu.setText("<HTML>Log <U>O</U>ut</HTML>");
            UserIDLabelMenu.setText("ID: " + Globals.loginID);
            if(isManager)
                IsManagerLabelMenu.setText("Manager : O  ");
            else
                IsManagerLabelMenu.setText("Manager : X  ");
            AttendantTask_setEnabled(true);
        }
    }    
    
    public void actionPerformed(ActionEvent e) {
    }
    
    private void enableAdminOnlyItem(boolean flag) {
        RunRecordItem.setEnabled(flag);
        LoginRecordItem.setEnabled(flag);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fullPanel = new javax.swing.JPanel();
        PanelMainTop = new javax.swing.JPanel();
        MainToolBar = new javax.swing.JToolBar();
        fillerLeft = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0));
        CarIOListButton = new javax.swing.JButton();
        VehiclesButton = new javax.swing.JButton();
        UsersButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        autoGateOpenCheckBox = new javax.swing.JCheckBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        ClockLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(0, 0));
        WholePanel = new javax.swing.JPanel();
        Panel_MainMsgList = new javax.swing.JPanel();
        LeftSide_Label = new javax.swing.JLabel();
        MainScrollPane = new javax.swing.JScrollPane();
        MessageTextArea = new javax.swing.JTextArea();
        Status_Panel = new javax.swing.JPanel();
        status_topPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        PID_Label = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        errorCheckBox = new javax.swing.JCheckBox();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        errIncButton = new javax.swing.JButton();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        errDecButton = new javax.swing.JButton();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel4 = new javax.swing.JPanel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        errorLabel = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 50), new java.awt.Dimension(0, 50), new java.awt.Dimension(32767, 50));
        jPanel2 = new javax.swing.JPanel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 30), new java.awt.Dimension(32767, 30));
        entryPanel = new javax.swing.JPanel();
        carEntryButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        showStatisticsBtn = new javax.swing.JButton();
        status_botPanel = new javax.swing.JPanel();
        statusPanelGate1 = new javax.swing.JPanel();
        labelCamera1 = new javax.swing.JLabel();
        labelE_Board1 = new javax.swing.JLabel();
        labelBar1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(32767, 3));
        statusPanelGate2 = new javax.swing.JPanel();
        labelCamera2 = new javax.swing.JLabel();
        labelE_Board2 = new javax.swing.JLabel();
        labelBar2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(32767, 3));
        statusPanelGate3 = new javax.swing.JPanel();
        labelCamera3 = new javax.swing.JLabel();
        labelE_Board3 = new javax.swing.JLabel();
        labelBar3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(32767, 3));
        statusTextPanel = new javax.swing.JPanel();
        statusTextField = new javax.swing.JTextField();
        visibleMenuBar = new javax.swing.JMenuBar();
        RecordsMenu = new javax.swing.JMenu();
        EntryRecordItem = new javax.swing.JMenuItem();
        RunRecordItem = new javax.swing.JMenuItem();
        LoginRecordItem = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        VehicleListItem = new javax.swing.JMenuItem();
        DriverListItem = new javax.swing.JMenuItem();
        BuildingMenu = new javax.swing.JMenu();
        BuildingListItem = new javax.swing.JMenuItem();
        AttendantMenu = new javax.swing.JMenu();
        AttendantListItem = new javax.swing.JMenuItem();
        CommandMenu = new javax.swing.JMenu();
        SettingsItem = new javax.swing.JMenuItem();
        CloseProgramItem = new javax.swing.JMenuItem();
        licenseMenuItem = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        LogInOutMenu = new javax.swing.JMenu();
        LoginUser = new javax.swing.JMenuItem();
        LogoutUser = new javax.swing.JMenuItem();
        IsManagerLabelMenu = new javax.swing.JMenu();
        UserIDLabelMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(MainBackground);
        setFocusCycleRoot(false);
        setMinimumSize(new java.awt.Dimension(930, 640));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeButtonClicked(evt);
            }
        });

        fullPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        fullPanel.setLayout(new java.awt.BorderLayout());

        PanelMainTop.setBackground(MainBackground);
        PanelMainTop.setAlignmentY(0.0F);
        PanelMainTop.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        PanelMainTop.setPreferredSize(new java.awt.Dimension(840, 32));
        PanelMainTop.setLayout(new java.awt.BorderLayout());

        MainToolBar.setBackground(MainBackground);
        MainToolBar.setRollover(true);
        MainToolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        MainToolBar.setBorderPainted(false);
        MainToolBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        MainToolBar.setMinimumSize(new java.awt.Dimension(473, 32));
        MainToolBar.setPreferredSize(new java.awt.Dimension(483, 32));
        MainToolBar.add(fillerLeft);

        CarIOListButton.setBackground(MainBackground);
        CarIOListButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        CarIOListButton.setText(ARRIVALS_BTN.getContent());
        CarIOListButton.setAlignmentY(0.0F);
        CarIOListButton.setFocusable(false);
        CarIOListButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CarIOListButton.setMaximumSize(new java.awt.Dimension(120, 23));
        CarIOListButton.setMinimumSize(new java.awt.Dimension(120, 23));
        CarIOListButton.setPreferredSize(new java.awt.Dimension(120, 23));
        CarIOListButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CarIOListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CarIOListButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(CarIOListButton);

        VehiclesButton.setBackground(MainBackground);
        VehiclesButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        VehiclesButton.setText(VEHICLES_BTN.getContent());
        VehiclesButton.setAlignmentY(0.0F);
        VehiclesButton.setEnabled(false);
        VehiclesButton.setFocusable(false);
        VehiclesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        VehiclesButton.setMaximumSize(new java.awt.Dimension(120, 23));
        VehiclesButton.setMinimumSize(new java.awt.Dimension(120, 23));
        VehiclesButton.setPreferredSize(new java.awt.Dimension(120, 23));
        VehiclesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        VehiclesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VehiclesButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(VehiclesButton);

        UsersButton.setBackground(MainBackground);
        UsersButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        UsersButton.setText(USERS_BTN.getContent());
        UsersButton.setAlignmentY(0.0F);
        UsersButton.setEnabled(false);
        UsersButton.setFocusable(false);
        UsersButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        UsersButton.setMaximumSize(new java.awt.Dimension(120, 23));
        UsersButton.setMinimumSize(new java.awt.Dimension(120, 23));
        UsersButton.setPreferredSize(new java.awt.Dimension(120, 23));
        UsersButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        UsersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsersButtonActionPerformed(evt);
            }
        });
        MainToolBar.add(UsersButton);
        MainToolBar.add(Box.createHorizontalGlue());
        MainToolBar.add(filler3);

        autoGateOpenCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        autoGateOpenCheckBox.setText(OPEN_LABEL.getContent());
        autoGateOpenCheckBox.setAlignmentY(0.0F);
        autoGateOpenCheckBox.setFocusable(false);
        autoGateOpenCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        autoGateOpenCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        autoGateOpenCheckBox.setMaximumSize(new java.awt.Dimension(77, 23));
        autoGateOpenCheckBox.setMinimumSize(new java.awt.Dimension(77, 23));
        autoGateOpenCheckBox.setPreferredSize(new java.awt.Dimension(130, 23));
        MainToolBar.add(autoGateOpenCheckBox);
        MainToolBar.add(filler4);

        ClockLabel.setBackground(MainBackground);
        ClockLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ClockLabel.setForeground(new java.awt.Color(255, 51, 51));
        ClockLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ClockLabel.setText("(clock)");
        ClockLabel.setAlignmentY(0.0F);
        ClockLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ClockLabel.setMaximumSize(new java.awt.Dimension(150, 23));
        ClockLabel.setMinimumSize(new java.awt.Dimension(130, 23));
        ClockLabel.setPreferredSize(new java.awt.Dimension(130, 23));
        MainToolBar.add(ClockLabel);
        MainToolBar.add(Box.createRigidArea(new Dimension(50, 0)));
        MainToolBar.add(filler1);

        PanelMainTop.add(MainToolBar, java.awt.BorderLayout.CENTER);

        fullPanel.add(PanelMainTop, java.awt.BorderLayout.PAGE_START);

        WholePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        WholePanel.setPreferredSize(new java.awt.Dimension(864, 497));
        WholePanel.setLayout(new java.awt.BorderLayout());

        Panel_MainMsgList.setBackground(MainBackground);
        Panel_MainMsgList.setAlignmentX(Component.LEFT_ALIGNMENT);
        Panel_MainMsgList.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        Panel_MainMsgList.setPreferredSize(new java.awt.Dimension(300, 550));
        Panel_MainMsgList.setLayout(new java.awt.BorderLayout());

        LeftSide_Label.setBackground(MainBackground);
        LeftSide_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LeftSide_Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LeftSide_Label.setText(STATUS_LABEL.getContent());
        LeftSide_Label.setToolTipText("");
        LeftSide_Label.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        LeftSide_Label.setMaximumSize(new java.awt.Dimension(280, 17));
        LeftSide_Label.setMinimumSize(new java.awt.Dimension(80, 17));
        LeftSide_Label.setName(""); // NOI18N
        LeftSide_Label.setOpaque(true);
        LeftSide_Label.setPreferredSize(new java.awt.Dimension(80, 17));
        Panel_MainMsgList.add(LeftSide_Label, java.awt.BorderLayout.PAGE_START);

        MainScrollPane.setAlignmentX(0.0F);
        MainScrollPane.setHorizontalScrollBar(null);
        MainScrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        MainScrollPane.setPreferredSize(new java.awt.Dimension(286, 225));

        MessageTextArea.setEditable(false);
        MessageTextArea.setColumns(35);
        MessageTextArea.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        MessageTextArea.setLineWrap(true);
        MessageTextArea.setToolTipText("");
        MessageTextArea.setWrapStyleWord(true);
        MessageTextArea.setAlignmentX(0.0F);
        MessageTextArea.setAlignmentY(0.0F);
        MessageTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        MessageTextArea.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        MessageTextArea.setMinimumSize(new java.awt.Dimension(280, 180));
        MessageTextArea.setName(""); // NOI18N
        MessageTextArea.setOpaque(false);
        MainScrollPane.setViewportView(MessageTextArea);
        MessageTextArea.getAccessibleContext().setAccessibleName("");

        Panel_MainMsgList.add(MainScrollPane, java.awt.BorderLayout.CENTER);

        Status_Panel.setBackground(MainBackground);
        Status_Panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Status_Panel.setAlignmentX(0.0F);
        Status_Panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        Status_Panel.setMinimumSize(new java.awt.Dimension(280, 180));
        Status_Panel.setPreferredSize(new java.awt.Dimension(280, 225));
        Status_Panel.setLayout(new java.awt.BorderLayout());

        status_topPanel.setBackground(MainBackground);
        status_topPanel.setLayout(new javax.swing.BoxLayout(status_topPanel, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBackground(MainBackground);
        jPanel3.setMaximumSize(new java.awt.Dimension(32882, 32767));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setBackground(MainBackground);
        jPanel5.setMaximumSize(new java.awt.Dimension(32882, 32767));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));
        jPanel5.add(filler12);

        PID_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        PID_Label.setText("(PID)");
        PID_Label.setMaximumSize(new java.awt.Dimension(40, 15));
        jPanel5.add(PID_Label);
        jPanel5.add(filler13);

        jPanel3.add(jPanel5);

        jPanel1.setBackground(MainBackground);
        jPanel1.setMaximumSize(new java.awt.Dimension(32882, 200));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(filler15);

        errorCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errorCheckBox.setText("error");
        errorCheckBox.setEnabled(false);
        errorCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        errorCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        errorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(errorCheckBox);
        jPanel1.add(filler8);

        errIncButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errIncButton.setIcon(getPlusIcon());
        errIncButton.setToolTipText("Inc by 0.1");
        errIncButton.setAlignmentX(0.5F);
        errIncButton.setBorder(null);
        errIncButton.setBorderPainted(false);
        errIncButton.setContentAreaFilled(false);
        errIncButton.setEnabled(false);
        errIncButton.setIconTextGap(0);
        errIncButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        errIncButton.setMinimumSize(new java.awt.Dimension(16, 16));
        errIncButton.setPreferredSize(new java.awt.Dimension(16, 16));
        errIncButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errIncButtonActionPerformed(evt);
            }
        });
        jPanel1.add(errIncButton);
        jPanel1.add(filler16);

        errDecButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errDecButton.setIcon(getMinusIcon());
        errDecButton.setToolTipText("Dec by 0.1");
        errDecButton.setAlignmentX(0.5F);
        errDecButton.setBorder(null);
        errDecButton.setBorderPainted(false);
        errDecButton.setContentAreaFilled(false);
        errDecButton.setEnabled(false);
        errDecButton.setIconTextGap(0);
        errDecButton.setMargin(new java.awt.Insets(0, 14, 0, 14));
        errDecButton.setMinimumSize(new java.awt.Dimension(16, 16));
        errDecButton.setPreferredSize(new java.awt.Dimension(16, 16));
        errDecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errDecButtonActionPerformed(evt);
            }
        });
        jPanel1.add(errDecButton);
        jPanel1.add(filler14);

        jPanel3.add(jPanel1);

        jPanel4.setBackground(MainBackground);
        jPanel4.setToolTipText("");
        jPanel4.setMaximumSize(new java.awt.Dimension(32882, 200));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        jPanel4.add(filler11);

        errorLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        errorLabel.setToolTipText("");
        errorLabel.setMaximumSize(new java.awt.Dimension(100, 23));
        errorLabel.setMinimumSize(new java.awt.Dimension(100, 23));
        errorLabel.setPreferredSize(new java.awt.Dimension(100, 23));
        jPanel4.add(errorLabel);
        jPanel4.add(filler10);

        jPanel3.add(jPanel4);
        jPanel3.add(filler9);

        status_topPanel.add(jPanel3);

        jPanel2.setBackground(MainBackground);
        jPanel2.setMaximumSize(new java.awt.Dimension(32882, 12000));
        jPanel2.setMinimumSize(new java.awt.Dimension(120, 110));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(120, 110));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel2.add(filler17);

        entryPanel.setAlignmentX(0.0F);
        entryPanel.setMaximumSize(new java.awt.Dimension(120, 40));
        entryPanel.setMinimumSize(new java.awt.Dimension(120, 40));
        entryPanel.setOpaque(false);
        entryPanel.setPreferredSize(new java.awt.Dimension(120, 40));
        entryPanel.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        carEntryButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        carEntryButton.setText(CAR_ARRIVAL_BTN.getContent());
        carEntryButton.setMaximumSize(new java.awt.Dimension(120, 40));
        carEntryButton.setMinimumSize(new java.awt.Dimension(120, 40));
        carEntryButton.setPreferredSize(new java.awt.Dimension(120, 40));
        carEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                carEntryButtonActionPerformed(evt);
            }
        });
        entryPanel.add(carEntryButton);

        jPanel2.add(entryPanel);
        entryPanel.getAccessibleContext().setAccessibleDescription("");

        jPanel2.add(filler7);

        showStatisticsBtn.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        showStatisticsBtn.setText(STATISTICS_BTN.getContent());
        showStatisticsBtn.setMaximumSize(new java.awt.Dimension(120, 40));
        showStatisticsBtn.setMinimumSize(new java.awt.Dimension(120, 40));
        showStatisticsBtn.setPreferredSize(new java.awt.Dimension(120, 40));
        showStatisticsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStatisticsBtnActionPerformed(evt);
            }
        });
        jPanel2.add(showStatisticsBtn);

        status_topPanel.add(jPanel2);
        jPanel2.getAccessibleContext().setAccessibleName("");

        Status_Panel.add(status_topPanel, java.awt.BorderLayout.CENTER);

        status_botPanel.setBackground(MainBackground);
        status_botPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 15, 2, 2));
        status_botPanel.setLayout(new javax.swing.BoxLayout(status_botPanel, javax.swing.BoxLayout.PAGE_AXIS));

        statusPanelGate1.setBackground(MainBackground);

        labelCamera1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelCamera1.setForeground(new java.awt.Color(255, 0, 0));
        labelCamera1.setText("Camera");

        labelE_Board1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelE_Board1.setForeground(new java.awt.Color(255, 0, 0));
        labelE_Board1.setText("E-Board");

        labelBar1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelBar1.setForeground(new java.awt.Color(255, 0, 0));
        labelBar1.setText("G-Bar");

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setText("Gate1");

        javax.swing.GroupLayout statusPanelGate1Layout = new javax.swing.GroupLayout(statusPanelGate1);
        statusPanelGate1.setLayout(statusPanelGate1Layout);
        statusPanelGate1Layout.setHorizontalGroup(
            statusPanelGate1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(25, 25, 25)
                .addComponent(labelCamera1)
                .addGap(18, 18, 18)
                .addComponent(labelE_Board1)
                .addGap(18, 18, 18)
                .addComponent(labelBar1)
                .addGap(72, 72, 72))
        );
        statusPanelGate1Layout.setVerticalGroup(
            statusPanelGate1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(statusPanelGate1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCamera1)
                    .addComponent(labelE_Board1)
                    .addComponent(labelBar1)
                    .addComponent(jLabel2)))
        );

        status_botPanel.add(statusPanelGate1);
        status_botPanel.add(filler2);

        statusPanelGate2.setBackground(MainBackground);

        labelCamera2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelCamera2.setForeground(new java.awt.Color(255, 0, 0));
        labelCamera2.setText("Camera");

        labelE_Board2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelE_Board2.setForeground(new java.awt.Color(255, 0, 0));
        labelE_Board2.setText("E-Board");

        labelBar2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelBar2.setForeground(new java.awt.Color(255, 0, 0));
        labelBar2.setText("G-Bar");

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setText("Gate2");

        javax.swing.GroupLayout statusPanelGate2Layout = new javax.swing.GroupLayout(statusPanelGate2);
        statusPanelGate2.setLayout(statusPanelGate2Layout);
        statusPanelGate2Layout.setHorizontalGroup(
            statusPanelGate2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(25, 25, 25)
                .addComponent(labelCamera2)
                .addGap(18, 18, 18)
                .addComponent(labelE_Board2)
                .addGap(18, 18, 18)
                .addComponent(labelBar2)
                .addGap(72, 72, 72))
        );
        statusPanelGate2Layout.setVerticalGroup(
            statusPanelGate2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(statusPanelGate2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCamera2)
                    .addComponent(labelE_Board2)
                    .addComponent(labelBar2)
                    .addComponent(jLabel3)))
        );

        status_botPanel.add(statusPanelGate2);
        status_botPanel.add(filler5);

        statusPanelGate3.setBackground(MainBackground);

        labelCamera3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelCamera3.setForeground(new java.awt.Color(255, 0, 0));
        labelCamera3.setText("Camera");

        labelE_Board3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelE_Board3.setForeground(new java.awt.Color(255, 0, 0));
        labelE_Board3.setText("E-Board");

        labelBar3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        labelBar3.setForeground(new java.awt.Color(255, 0, 0));
        labelBar3.setText("G-Bar");

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setText("Gate3");

        javax.swing.GroupLayout statusPanelGate3Layout = new javax.swing.GroupLayout(statusPanelGate3);
        statusPanelGate3.setLayout(statusPanelGate3Layout);
        statusPanelGate3Layout.setHorizontalGroup(
            statusPanelGate3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate3Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(25, 25, 25)
                .addComponent(labelCamera3)
                .addGap(18, 18, 18)
                .addComponent(labelE_Board3)
                .addGap(18, 18, 18)
                .addComponent(labelBar3)
                .addGap(72, 72, 72))
        );
        statusPanelGate3Layout.setVerticalGroup(
            statusPanelGate3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelGate3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(statusPanelGate3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCamera3)
                    .addComponent(labelE_Board3)
                    .addComponent(labelBar3)
                    .addComponent(jLabel4)))
        );

        status_botPanel.add(statusPanelGate3);

        Status_Panel.add(status_botPanel, java.awt.BorderLayout.PAGE_END);

        Panel_MainMsgList.add(Status_Panel, java.awt.BorderLayout.PAGE_END);

        WholePanel.add(Panel_MainMsgList, java.awt.BorderLayout.WEST);

        fullPanel.add(WholePanel, java.awt.BorderLayout.CENTER);
        fullPanel.add(filler6, java.awt.BorderLayout.LINE_START);

        statusTextPanel.setBackground(MainBackground);
        statusTextPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        statusTextPanel.setLayout(new java.awt.BorderLayout());

        statusTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        statusTextField.setText("<Critical Status Information>");
        statusTextField.setToolTipText("");
        statusTextField.setMargin(new java.awt.Insets(0, 5, 0, 5));
        statusTextField.setPreferredSize(new java.awt.Dimension(166, 25));
        statusTextPanel.add(statusTextField, java.awt.BorderLayout.PAGE_END);

        fullPanel.add(statusTextPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(fullPanel, java.awt.BorderLayout.CENTER);

        visibleMenuBar.setBackground(MainBackground);
        visibleMenuBar.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        visibleMenuBar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        visibleMenuBar.setMinimumSize(new java.awt.Dimension(110, 32));
        visibleMenuBar.setOpaque(false);
        visibleMenuBar.setPreferredSize(new java.awt.Dimension(660, 32));

        RecordsMenu.setBackground(MainBackground);
        RecordsMenu.setText(RECORD_MENU.getContent());
        RecordsMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        RecordsMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RecordsMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        RecordsMenu.setMaximumSize(new java.awt.Dimension(120, 32767));
        RecordsMenu.setPreferredSize(new java.awt.Dimension(100, 24));

        EntryRecordItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        EntryRecordItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EntryRecordItem.setText(ARRIVAL_MENU_ITEM.getContent());
        EntryRecordItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EntryRecordItemActionPerformed(evt);
            }
        });
        RecordsMenu.add(EntryRecordItem);

        RunRecordItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        RunRecordItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        RunRecordItem.setText(BOOTING_MENU_ITEM.getContent());
        RunRecordItem.setEnabled(false);
        RunRecordItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunRecordItemActionPerformed(evt);
            }
        });
        RecordsMenu.add(RunRecordItem);

        LoginRecordItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        LoginRecordItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LoginRecordItem.setText(LOGIN_RECORD_MENU_ITEM.getContent());
        LoginRecordItem.setEnabled(false);
        LoginRecordItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginRecordItemActionPerformed(evt);
            }
        });
        RecordsMenu.add(LoginRecordItem);

        visibleMenuBar.add(Box.createRigidArea(new Dimension(20, 0)));

        visibleMenuBar.add(RecordsMenu);

        jMenu6.setBackground(MainBackground);
        jMenu6.setText(VEHICLE_MENU.getContent());
        jMenu6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jMenu6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMenu6.setMaximumSize(new java.awt.Dimension(120, 32767));
        jMenu6.setPreferredSize(new java.awt.Dimension(100, 24));

        VehicleListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK));
        VehicleListItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        VehicleListItem.setText(VEHICLE_MANAGE_MENU_ITEM.getContent());
        VehicleListItem.setEnabled(false);
        VehicleListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processVehicleList(evt);
            }
        });
        jMenu6.add(VehicleListItem);

        DriverListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        DriverListItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        DriverListItem.setText(DRIVERS_MENU_ITEM.getContent());
        DriverListItem.setEnabled(false);
        DriverListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DriverListItemActionPerformed(evt);
            }
        });
        jMenu6.add(DriverListItem);

        visibleMenuBar.add(jMenu6);

        BuildingMenu.setBackground(MainBackground);
        BuildingMenu.setText(AFFILIATION_MENU.getContent());
        BuildingMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BuildingMenu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BuildingMenu.setMaximumSize(new java.awt.Dimension(120, 32767));
        BuildingMenu.setPreferredSize(new java.awt.Dimension(100, 24));
        BuildingMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BuildingMenuActionPerformed(evt);
            }
        });

        BuildingListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        BuildingListItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BuildingListItem.setText(MANAGE_MENU_ITEM.getContent());
        BuildingListItem.setEnabled(false);
        BuildingListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BuildingListItemActionPerformed(evt);
            }
        });
        BuildingMenu.add(BuildingListItem);

        visibleMenuBar.add(BuildingMenu);

        AttendantMenu.setBackground(MainBackground);
        AttendantMenu.setText(USERS_MENU.getContent());
        AttendantMenu.setDoubleBuffered(true);
        AttendantMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        AttendantMenu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        AttendantMenu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        AttendantMenu.setMaximumSize(new java.awt.Dimension(120, 32767));
        AttendantMenu.setPreferredSize(new java.awt.Dimension(100, 24));

        AttendantListItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        AttendantListItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        AttendantListItem.setText(MANAGE_MENU_ITEM.getContent());
        AttendantListItem.setEnabled(false);
        AttendantListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processAttendantList(evt);
            }
        });
        AttendantMenu.add(AttendantListItem);

        visibleMenuBar.add(AttendantMenu);

        CommandMenu.setBackground(MainBackground);
        CommandMenu.setText(SYSTEM_MENU.getContent());
        CommandMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        CommandMenu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        CommandMenu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        CommandMenu.setMaximumSize(new java.awt.Dimension(120, 32767));
        CommandMenu.setPreferredSize(new java.awt.Dimension(100, 24));

        SettingsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        SettingsItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        SettingsItem.setText(SETTING_MENU_ITEM.getContent());
        SettingsItem.setEnabled(false);
        SettingsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsItemActionPerformed(evt);
            }
        });
        CommandMenu.add(SettingsItem);

        CloseProgramItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        CloseProgramItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        CloseProgramItem.setText(QUIT_MENU_ITEM.getContent());
        CloseProgramItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processCloseProgram(evt);
            }
        });
        CommandMenu.add(CloseProgramItem);

        licenseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        licenseMenuItem.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        licenseMenuItem.setText("About");
        licenseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseMenuItemActionPerformed(evt);
            }
        });
        CommandMenu.add(licenseMenuItem);

        visibleMenuBar.add(CommandMenu);

        jMenu5.setBackground(MainBackground);
        jMenu5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jMenu5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMenu5.setPreferredSize(new java.awt.Dimension(85, 24));
        visibleMenuBar.add(jMenu5);
        visibleMenuBar.add(Box.createHorizontalGlue());

        LogInOutMenu.setBackground(MainBackground);
        LogInOutMenu.setText(LOGIN_MENU.getContent());
        LogInOutMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LogInOutMenu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LogInOutMenu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        LogInOutMenu.setMaximumSize(new java.awt.Dimension(110, 32767));
        LogInOutMenu.setMinimumSize(new java.awt.Dimension(110, 0));
        LogInOutMenu.setPreferredSize(new java.awt.Dimension(110, 22));
        LogInOutMenu.setRequestFocusEnabled(false);

        LoginUser.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        LoginUser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LoginUser.setText(LOGIN_MENU_ITEM.getContent());
        LoginUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processLogIn(evt);
            }
        });
        LogInOutMenu.add(LoginUser);

        LogoutUser.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        LogoutUser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LogoutUser.setText(LOGOUT_MENU_ITEM.getContent());
        LogoutUser.setEnabled(false);
        LogoutUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutUserActionPerformed(evt);
            }
        });
        LogInOutMenu.add(LogoutUser);

        visibleMenuBar.add(LogInOutMenu);

        IsManagerLabelMenu.setText(MANAGER_MANU.getContent());
        IsManagerLabelMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        IsManagerLabelMenu.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        visibleMenuBar.add(IsManagerLabelMenu);

        UserIDLabelMenu.setBackground(MainBackground);
        UserIDLabelMenu.setText(IDBeforeLogin);
        UserIDLabelMenu.setToolTipText("");
        UserIDLabelMenu.setAlignmentX(0.0F);
        UserIDLabelMenu.setFocusPainted(true);
        UserIDLabelMenu.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        UserIDLabelMenu.setHideActionText(true);
        UserIDLabelMenu.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        UserIDLabelMenu.setInheritsPopupMenu(true);
        UserIDLabelMenu.setMaximumSize(new java.awt.Dimension(140, 32767));
        UserIDLabelMenu.setMinimumSize(new java.awt.Dimension(140, 0));
        UserIDLabelMenu.setPreferredSize(new java.awt.Dimension(140, 24));
        visibleMenuBar.add(UserIDLabelMenu);

        setJMenuBar(visibleMenuBar);

        setSize(new java.awt.Dimension(895, 667));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
          
    private void closeButtonClicked(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeButtonClicked
        askUserIntentionOnProgramStop(false);
    }//GEN-LAST:event_closeButtonClicked

    static ParentGUI parentGUI ;
    private void processAttendantList(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processAttendantList
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                if (attendantsListForm == null)
                    attendantsListForm = new AttListForm(parentGUI, loginID, loginPW, isManager);
                attendantsListForm.setVisible(true);
            }
        });
    }//GEN-LAST:event_processAttendantList

    private void processLogIn(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processLogIn
        if (getLoginForm() == null) {
            try {
                setLoginForm(new LoginForm());
                getLoginForm().addLoginEventListener(new LoginEventListener() {
                    public void loginEventOccurred(LoginWindowEvent e) {
                        Globals.loginID = e.getUID();
                        Globals.loginPW = e.getPW();
                        Globals.isManager = e.getIsManager();
                        if (Globals.isManager) {
                            enableAdminOnlyItem(true);
                        }
                        changeLogIOitemVisibility();
                        recordLogin();
                        addMessageLine(MessageTextArea, "User '" + Globals.loginID + "' logged in" );
                    }
                });
            } catch (Exception ex) {
                logParkingException(Level.SEVERE, ex, "(user login processing)");
            }
        }  
         
        getLoginForm().setVisible(true);        

        // <editor-fold defaultstate="collapsed" desc="-- automatic login during development">        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                loginForm.handleLoginAttempt();       
//                SettingsItemActionPerformed(null);
            }
        });  
        //</editor-fold>        
    }//GEN-LAST:event_processLogIn
    
    private void processCloseProgram(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processCloseProgram
        askUserIntentionOnProgramStop(false);
    }//GEN-LAST:event_processCloseProgram

    public void askUserIntentionOnProgramStop(boolean forced) {
        if (Globals.loginID != null) {
            if (! processLogOut(forced))
                return;
        }
        
        int result = 0;
        if (forced) {
            JOptionPane.showMessageDialog(this, 
                "Program stops running.");
            result = JOptionPane.YES_OPTION;
        } else {
            result = JOptionPane.showConfirmDialog(this, 
                    "Do you want to stop the system?", "Shutdown Confirmation", JOptionPane.YES_NO_OPTION);
        }
        
        if (result == JOptionPane.YES_OPTION) {
            stopRunningTheProgram();
        }   
    }
    
    private void processVehicleList(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processVehicleList
        (new VehiclesForm()).setVisible(true);                
//        VehiclesForm vehicleManageForm = new VehiclesForm();
//        vehicleManageForm.setVisible(true);                
    }//GEN-LAST:event_processVehicleList

    private void VehiclesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VehiclesButtonActionPerformed
        (new VehiclesForm()).setVisible(true);                
    }//GEN-LAST:event_VehiclesButtonActionPerformed

    private void UsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersButtonActionPerformed
        processAttendantList(evt);
    }//GEN-LAST:event_UsersButtonActionPerformed

    private void SettingsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsItemActionPerformed
        if (configureSettingsForm == null) 
        { 
            setConfigureSettingsForm(new Settings_System(this));
        }
        configureSettingsForm.setVisible(true);
    }//GEN-LAST:event_SettingsItemActionPerformed

    private void RunRecordItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunRecordItemActionPerformed
        if (showRunRecordForm == null) 
        {
            showRunRecordForm = new RunRecordList();
        }
        showRunRecordForm.setVisible(true);
    }//GEN-LAST:event_RunRecordItemActionPerformed

    private void LoginRecordItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginRecordItemActionPerformed
        if (showLoginRecordForm == null) 
        {
            showLoginRecordForm = new LoginRecordList();
        }
        showLoginRecordForm.getDatesRefreshTable();
        showLoginRecordForm.setVisible(true);        
    }//GEN-LAST:event_LoginRecordItemActionPerformed
        
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        // get the size of this frame
        if (resizedEventDisabled) {
            return;
        } else {
            resizedEventDisabled = true;
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                long startMil = System.currentTimeMillis();
                Dimension contentPanel = getContentPane().getSize();
                Dimension gatesPanelSize = new Dimension(contentPanel.width - 290, contentPanel.height - 68);

                getGatePanel().resizeComponents(gatesPanelSize);
                resizedEventDisabled = false;
            }
        });                
    }//GEN-LAST:event_formComponentResized

    public static int manualSimulationImageID = 0;
    private void carEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_carEntryButtonActionPerformed
        Random randomInteger = new Random();
        byte gateNo = (byte) (randomInteger.nextInt(gateCount) + 1);
        
        getPassingDelayStat()[gateNo].setICodeArrivalTime(System.currentTimeMillis());
        
        while ((isGateBusy[gateNo])) {
            gateNo = (byte) (randomInteger.nextInt(gateCount) + 1);
            getPassingDelayStat()[gateNo].setICodeArrivalTime(System.currentTimeMillis());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                logParkingException(Level.SEVERE, ex, "Car entry simulation button");
            }
        }
        
        if (gateDeviceTypes[gateNo].cameraType == OSP_enums.CameraType.Blackfly) {
            // Given the camera is connected, order it to take a picture
            if ((getSockConnStat()[Camera.ordinal()][gateNo]).isConnected()) {
                BlackFlyManager camMan = 
                        (BlackFlyManager)deviceManagers[DeviceType.Camera.ordinal()][gateNo];
                synchronized (camMan.takePicture) {
                    camMan.takePicture.notify();
                }
                try {
                    synchronized(camMan.processTag) {
                        camMan.processTag.wait();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ControlGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!(isGateBusy[gateNo])) {
                    processCarEntry(gateNo, --manualSimulationImageID,
                            camMan.carTagNumber, null, camMan.rawImage); 
                }   
                    
            } else {
                System.out.println("not connected");
                addMessageLine(getMessageTextArea(), "카메라 #" + gateNo +" 단절 상태임");
            }
        } else {
            int imageNo = randomInteger.nextInt(6) + 1;
            String tagNumber = dummyMessages[imageNo].getCarNumber();
            BufferedImage carImage = dummyMessages[imageNo].getBufferedImg();
            
            processCarEntry(gateNo, --manualSimulationImageID, tagNumber, carImage, null);
        }
    }//GEN-LAST:event_carEntryButtonActionPerformed

    private void BuildingListItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuildingListItemActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (affiliationBuildingForm == null) {
                    affiliationBuildingForm = new AffiliationBuildingForm();
                }
                affiliationBuildingForm.setVisible(true);
            }
        });
    }//GEN-LAST:event_BuildingListItemActionPerformed

    private void BuildingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuildingMenuActionPerformed
        // there
    }//GEN-LAST:event_BuildingMenuActionPerformed

    private void DriverListItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DriverListItemActionPerformed
        ManageDrivers driversForm = new ManageDrivers(null);
        driversForm.setVisible(true);    
    }//GEN-LAST:event_DriverListItemActionPerformed

    private void EntryRecordItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EntryRecordItemActionPerformed
        new ManageArrivalList().run();
    }//GEN-LAST:event_EntryRecordItemActionPerformed

    private void errorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorCheckBoxActionPerformed
        if (getErrorCheckBox().isSelected()) {
            addMessageLine(MessageTextArea, "Artificial error is on");
            addMessageLine(MessageTextArea, "\tprob of error: " + getFormattedRealNumber(ERROR_RATE, 2));
            errorLabel.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
        } else {
            addMessageLine(MessageTextArea, "No artificial error");
            errorLabel.setText("");
        }
    }//GEN-LAST:event_errorCheckBoxActionPerformed

    /**
     * @return the getErrorCheckBox control
     */
    public javax.swing.JCheckBox getErrorCheckBox() {
        return errorCheckBox;
    }

    /**
     * @param errorCheckBox the ErrorCheckBox to set
     */
    public void setErrorCheckBox(javax.swing.JCheckBox errorCheckBox) {
        this.errorCheckBox = errorCheckBox;
    }    
    
    public Object MsgAreaMutex  = new Object();
      
    private void errIncButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errIncButtonActionPerformed
        if (errorCheckBox.isSelected()) {
           if (ERROR_RATE < 0.9) {
                ERROR_RATE += 0.1f;
            } else {
                
               addMessageLine(MessageTextArea,"current error rate(=" 
                        + getFormattedRealNumber(ERROR_RATE, 2) + ") is max!");
            }
            errorLabel.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
        } else {
            addMessageLine(MessageTextArea, "First, select error check box, OK?");
        }
    }//GEN-LAST:event_errIncButtonActionPerformed

    private void errDecButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errDecButtonActionPerformed
        if (errorCheckBox.isSelected()) {
            if (ERROR_RATE > 0.10) {
                ERROR_RATE -= 0.1f;
            } else {
                
               addMessageLine(MessageTextArea,"current error rate(=" 
                        + getFormattedRealNumber(ERROR_RATE, 2) + ") is max!");
            }
            errorLabel.setText("error : " + getFormattedRealNumber(ERROR_RATE, 2));
        } else {
            addMessageLine(MessageTextArea, "First, select error check box, OK?");
        }
    }//GEN-LAST:event_errDecButtonActionPerformed

    private void showStatisticsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showStatisticsBtnActionPerformed
        // show camera sockConnStat statistics
        StringBuffer perfDesc = new StringBuffer();

        if (DEBUG) {
            //<editor-fold desc="-- Show detailed performance statistics">
            perfDesc.append("Artificial error rate: ");
            if (errorCheckBox.isSelected()) {
                perfDesc.append(getFormattedRealNumber(ERROR_RATE, 2));
            } else {
                perfDesc.append("N/A");
            }
            perfDesc.append(System.lineSeparator());

            for (DeviceType type : DeviceType.values()) {
                for (int gateNo = 1; gateNo <= gateCount; gateNo++ ) {
                    perfDesc.append("            ");
                    perfDesc.append(type + " #" + gateNo 
                            + getSockConnStat()[type.ordinal()][gateNo].getPerformanceDescription());
                }
                perfDesc.append(System.lineSeparator());
            }

            StringBuffer gateBar = new StringBuffer();
            for (DeviceType type : DeviceType.values()) {
                for (int gateNo = 1; gateNo <= gateCount; gateNo++) {
                    DeviceCommand deviceCommand = getPerfomStatistics()[type.ordinal()][gateNo];
                    
                    if (deviceCommand != null && deviceCommand.hasData()) 
                    {
                        gateBar.append("            ");
                        gateBar.append(getDevType(type, (byte)gateNo) + "#" + gateNo + "- "
                                + getPerfomStatistics()[type.ordinal()][gateNo].getPerformanceDescription());
                    }
                }   
            }
            perfDesc.append(gateBar);
            perfDesc.append(System.lineSeparator());        
            perfDesc.append("            ");
            //</editor-fold>
        }
        
        /**
         * Display major performance statistics.
         */
        
        perfDesc.append("[Passing Delay Average(ms)]" + System.lineSeparator());
        
        fetchPassingDelay();
        for (int gateNo = 1; gateNo <= gateCount; gateNo++) {
            perfDesc.append("            Gate #" + gateNo + " :");
            perfDesc.append(getPassingDelayStat()[gateNo].getPassingDelayAvg());
            perfDesc.append(System.lineSeparator());        
        }  
        perfDesc.append("            ");
        perfDesc.append("* Passing delay is from " + System.lineSeparator() 
                + "    <first byte arrival of car image> to" + System.lineSeparator());
        perfDesc.append("    ");
        perfDesc.append("<gate bar open ACK arrival>." + System.lineSeparator());
        
        addMessageLine(MessageTextArea, perfDesc.toString());        
        MessageTextArea.setCaretPosition(MessageTextArea.getDocument().getLength() 
                - perfDesc.length() - 2); // places the caret at the bottom of the display area        
    }//GEN-LAST:event_showStatisticsBtnActionPerformed

    private void LogoutUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutUserActionPerformed
        processLogOut(false);
    }//GEN-LAST:event_LogoutUserActionPerformed

    private void licenseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseMenuItemActionPerformed
        showLicensePanel(this, "License Notice on OsParking program");
    }//GEN-LAST:event_licenseMenuItemActionPerformed

    private void CarIOListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CarIOListButtonActionPerformed
        new ManageArrivalList().run();
    }//GEN-LAST:event_CarIOListButtonActionPerformed

    LedProtocol ledNoticeProtocol = new LedProtocol(); 

    private String getDevType(DeviceType type, byte gateNo) {
        String typeName = "";
        switch (type) {
            case Camera:
                switch (Globals.gateDeviceTypes[gateNo].cameraType) {
                    case Simulator:
                        typeName = "CamSim";
                        break;
                    default:
                        typeName = Globals.gateDeviceTypes[gateNo].cameraType.toString();
                        break;
                } 
                break;
                
            case E_Board:
                switch (Globals.gateDeviceTypes[gateNo].eBoardType) {
                    case Simulator:
                        typeName = "EBDsim";
                        break;
                    default:
                        typeName = Globals.gateDeviceTypes[gateNo].eBoardType.toString();
                        break;
                } 
                break;
                
            case GateBar:
                switch (Globals.gateDeviceTypes[gateNo].gateBarType) {
                    case Simulator:
                        typeName = "BarSim";
                        break;
                    default:
                        typeName = Globals.gateDeviceTypes[gateNo].gateBarType.toString();
                        break;
                } 
                break;
        }
        return typeName;
    }

    /**
     * Store a car arrival information in the DB and show car image on GUI.
     * 
     * @param permission Vehicle entry permission status
     * @param cameraID Camera ID(1,2,3,...), equivalent to the gate ID
     * @param arrivalTime Car arrival time at the gate
     * @param tagRecognized Car tag number recognized by the LPR module
     * @param tagRegistered Given it is a registered car, tag number in the DB, or empty string
     * @param filenameModified 
     * @param image Car arrival image
     * @param isOpen True, if car is not eligible to park but bar is simulated 
     * to open by the attendant.
     */
    private void insertDisplayImage(PermissionType permission, byte cameraID, 
            Date arrivalTime, String tagRecognized, String tagRegistered, 
            BufferedImage image, boolean isOpen, boolean isStopped) 
    {
        BarOperation barOptn;
        long arrSeqNo = -1;
        
        if (isStopped) {
            barOptn = BarOperation.STOPPED;
        } else 
            if (autoGateOpenCheckBox.isSelected()) 
        {
            //<editor-fold desc="-- Automatic Gate Open"">
            if (permission == PermissionType.ALLOWED || permission == PermissionType.DISALLOWED) 
            {
                // Registered, possibly disallowed to park; door is automatically opened
                barOptn = BarOperation.REGISTERED_CAR_OPENED;
            } else {
                barOptn = BarOperation.AUTO_OPENED;
            }
            //</editor-fold>
        } else {
            if (permission == ALLOWED) {
                barOptn = BarOperation.REGISTERED_CAR_OPENED;
            } else {
                //<editor-fold desc="-- Handle Not allowed cars ">
                if (Globals.RANDOM_ATTENDANT && isOpen) {
                    // Handle as if [Open] button were pressed in the "DisAllowedCar" form
                    barOptn = BarOperation.MANUAL;
                } else {
                    // Handle as if [Close bar] button were pressed in the "DisAllowedCar" form
                    barOptn = BarOperation.REMAIN_CLOSED;
                }
                //</editor-fold>
            }
        }
        arrSeqNo = insertDBrecord(cameraID, arrivalTime, tagRecognized, tagRegistered, 
                image,  -1, -1, null, barOptn);
        updateMainForm(cameraID, tagRecognized, arrSeqNo, barOptn);
    }

    private void controlStoppedCar(byte cameraID, int imageSN, String tagRecognized, 
            BufferedImage image, Date arrivalTime, String tagEnteredAs, 
            String remark, PermissionType permission, int carPassingDelayMs) 
    {
        switch (permission) {

            case DISALLOWED:
                //<editor-fold desc="-- Handle Parking Disallowed Car"">
                isGateBusy[cameraID] = true;
                new DisAllowedCar(this, tagRecognized, arrivalTime,
                        tagEnteredAs, remark, cameraID, imageSN, 
                        image, carPassingDelayMs).setVisible(true);
                break;   
                //</editor-fold>

            case UNREGISTERED:
                //<editor-fold desc="-- Handle Unregistered Car"">
                isGateBusy[cameraID] = true;
                new VisitingCar(this, tagRecognized, arrivalTime, cameraID,
                        imageSN, image, carPassingDelayMs).setVisible(true);
                break;
                //</editor-fold>

            case BADTAGFORMAT:
                // popup form, allow parking or not, store the result
                break;

            default:
                break;  
            }
    }

    public void processCarEntry(byte gateNo, int imageID, String tagRecognized, 
            BufferedImage carImage, FlyCapture2.Image blackFlyImage) 
    {
        Date arrivalTime = Calendar.getInstance().getTime();
        StringBuffer tagRegistered = new StringBuffer();
        StringBuffer remark = new StringBuffer();    
        
        PermissionType permission = enteranceAllowed(tagRecognized, tagRegistered, remark);
        
        int carPassingDelayMs = rand.nextInt(MAX_PASSING_DELAY)  + CAR_PERIOD;        

        interruptEBoardDisplay(gateNo, tagRecognized, 
                permission, remark.toString(), tagRegistered.toString(),
                imageID, carPassingDelayMs);

        if (permission == ALLOWED 
                || autoGateOpenCheckBox.isSelected()
                || Globals.RANDOM_ATTENDANT) 
        {
            getPassingDelayStat()[gateNo].setAccumulatable(true);
        } else {
            getPassingDelayStat()[gateNo].setAccumulatable(false);
        }

        float fValue = gateRandomOpen.nextFloat();
        boolean isOpen = fValue < 0.8;
        
        if (autoGateOpenCheckBox.isSelected() || 
                permission == ALLOWED || 
                (Globals.RANDOM_ATTENDANT && isOpen))
        {
            getPassingDelayStat()[gateNo].setAccumulatable(true);
            
            if (!autoGateOpenCheckBox.isSelected() && permission != ALLOWED) {
                imageID = -imageID;
            }
            raiseGateBar(gateNo, imageID, carPassingDelayMs);
            if (carImage == null) {
                carImage = convertRaw2Buffered(blackFlyImage);
            }
            insertDisplayImage(permission, gateNo, arrivalTime, tagRecognized,
                    tagRegistered.toString(), carImage, isOpen, false);
        } else {
            // Handle unregistered or not permitted cars
            getPassingDelayStat()[gateNo].setAccumulatable(false);
            
            if (carImage == null) {
                carImage = convertRaw2Buffered(blackFlyImage);
            }
            insertDisplayImage(permission, gateNo, arrivalTime, tagRecognized,
                    tagRegistered.toString(), carImage, isOpen, true);
            controlStoppedCar(gateNo, imageID, tagRecognized, carImage,
                    arrivalTime, tagRegistered.toString(),
                    remark.toString(), permission, carPassingDelayMs);
        }
    }

    private BufferedImage convertRaw2Buffered(FlyCapture2.Image rawImage) {
        int width = BlackFlyManager.ImgWidth;
        int height = BlackFlyManager.ImgHeight;
        BufferedImage bufferedImage = new BufferedImage(width, height, TYPE_BYTE_GRAY);
        byte[] pxBytes = new byte[width * height];
        int [] pxInts = new int[width * height];
        int[] matrix = new int[width * height];
        DataBufferInt buffer = new DataBufferInt(matrix, matrix.length);
        int[] bandMasks = {0xFF};
        WritableRaster ras = Raster.createPackedRaster(
                buffer, width, height, width, bandMasks, null);

        rawImage.GetData().get(pxBytes);
        for (int i = 0; i < pxBytes.length; i++) {
            // Reverse pixel order to display image naturally
            pxInts[i] = pxBytes[pxBytes.length - i - 1];
        }
        ras.setPixels(0, 0, width, height, pxInts);
        bufferedImage.setData(ras);   
        return bufferedImage;
    }

    class ManageArrivalList extends Thread {
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            CarArrivals arrivals = new CarArrivals();
            arrivals.setVisible(true);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="-- Form Control Items ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AttendantListItem;
    private javax.swing.JMenu AttendantMenu;
    private javax.swing.JMenuItem BuildingListItem;
    private javax.swing.JMenu BuildingMenu;
    private javax.swing.JButton CarIOListButton;
    private javax.swing.JLabel ClockLabel;
    private javax.swing.JMenuItem CloseProgramItem;
    private javax.swing.JMenu CommandMenu;
    private javax.swing.JMenuItem DriverListItem;
    private javax.swing.JMenuItem EntryRecordItem;
    private javax.swing.JMenu IsManagerLabelMenu;
    private javax.swing.JLabel LeftSide_Label;
    private javax.swing.JMenu LogInOutMenu;
    private javax.swing.JMenuItem LoginRecordItem;
    private javax.swing.JMenuItem LoginUser;
    private javax.swing.JMenuItem LogoutUser;
    private javax.swing.JScrollPane MainScrollPane;
    private javax.swing.JToolBar MainToolBar;
    private javax.swing.JTextArea MessageTextArea;
    private javax.swing.JLabel PID_Label;
    private javax.swing.JPanel PanelMainTop;
    private javax.swing.JPanel Panel_MainMsgList;
    private javax.swing.JMenu RecordsMenu;
    private javax.swing.JMenuItem RunRecordItem;
    private javax.swing.JMenuItem SettingsItem;
    private javax.swing.JPanel Status_Panel;
    private javax.swing.JMenu UserIDLabelMenu;
    private javax.swing.JButton UsersButton;
    private javax.swing.JMenuItem VehicleListItem;
    private javax.swing.JButton VehiclesButton;
    private javax.swing.JPanel WholePanel;
    private javax.swing.JCheckBox autoGateOpenCheckBox;
    private javax.swing.JButton carEntryButton;
    private javax.swing.JPanel entryPanel;
    private javax.swing.JButton errDecButton;
    private javax.swing.JButton errIncButton;
    public javax.swing.JCheckBox errorCheckBox;
    private javax.swing.JLabel errorLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.Box.Filler fillerLeft;
    private javax.swing.JPanel fullPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel labelBar1;
    private javax.swing.JLabel labelBar2;
    private javax.swing.JLabel labelBar3;
    private javax.swing.JLabel labelCamera1;
    private javax.swing.JLabel labelCamera2;
    private javax.swing.JLabel labelCamera3;
    private javax.swing.JLabel labelE_Board1;
    private javax.swing.JLabel labelE_Board2;
    private javax.swing.JLabel labelE_Board3;
    private javax.swing.JMenuItem licenseMenuItem;
    public javax.swing.JButton showStatisticsBtn;
    private javax.swing.JPanel statusPanelGate1;
    private javax.swing.JPanel statusPanelGate2;
    private javax.swing.JPanel statusPanelGate3;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JPanel statusTextPanel;
    private javax.swing.JPanel status_botPanel;
    private javax.swing.JPanel status_topPanel;
    private javax.swing.JMenuBar visibleMenuBar;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    
    // JongbumPark's declaration
    private static GatePanel gatePanel;
    
    public Timer formClockTimer = null;

    private void startClock() {
        formClockTimer = new Timer("ospFormClockTimer"); 
            // 1000, new ActionListener() {
        TimerTask displayTime = new TimerTask() {
            @Override
            
            public void run() {
                ClockLabel.setText(new SimpleDateFormat ("hh:mm:ss a").format(new Date( )));
            }            
        };  
        formClockTimer.schedule(displayTime, 0, 1000);
    }

    public void stopRunningTheProgram() {

        // 전광판 기본문구 표시
        for (int gateNo = 1; gateNo <= gateCount; gateNo++) { 
            IE_Board ebdMan = (IE_Board) deviceManagers[E_Board.ordinal()][gateNo];
            ebdMan.showDefaultMessage();
        }
        

        setSHUT_DOWN(true);

        // Cancel timer here
        for (int gateNo = 1; gateNo <= gateCount; gateNo++) { 
            openGateCmdTimer[gateNo].cancelTask();
            openGateCmdTimer[gateNo].cancel();
            openGateCmdTimer[gateNo].purge();
            openGateCmdTimer[gateNo] = null;
        }

        for (DeviceType type: DeviceType.values()) {
            for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
                connectDeviceTimer[type.ordinal()][gateNo].cancelTask();
                connectDeviceTimer[type.ordinal()][gateNo].cancel();
                connectDeviceTimer[type.ordinal()][gateNo].purge();
                connectDeviceTimer[type.ordinal()][gateNo] = null;
                
                // Handle some specific real hardware
                if (type == E_Board &&
                        Globals.gateDeviceTypes[gateNo].eBoardType == E_BoardType.LEDnotice) 
                {
                    getSendEBDmsgTimer()[gateNo][EBD_Row.TOP.ordinal()].reRunOnce(
                        new FinishLEDnoticeIntrTask(this, gateNo, EBD_Row.TOP), 0);                        
                }
                if (deviceManagers[type.ordinal()][gateNo] != null) {
                    deviceManagers[type.ordinal()][gateNo].stopOperation("System shutdown");
                }
            }
        }
        
        periodicallyCheckSystemTimer.cancel();
        periodicallyCheckSystemTimer.purge();

        if (formClockTimer != null) {
            formClockTimer.cancel(); 
            formClockTimer.purge();
        }
          
        if (DEBUG)
            closeIDLogFile();
        
        recordSystemStop();
        saveMessageLines();            
        System.exit(0);
    }

    private void AttendantTask_setEnabled(boolean b) {
        AttendantListItem.setEnabled(b);
        VehicleListItem.setEnabled(b);
        DriverListItem.setEnabled(b);
    }

    private void MenuItems_setEnabled(boolean loggedIn) {
        BuildingListItem.setEnabled(loggedIn);
        LoginUser.setEnabled(!loggedIn);
        LogoutUser.setEnabled(loggedIn);
        VehiclesButton.setEnabled(loggedIn);  
        UsersButton.setEnabled(loggedIn);  
        SettingsItem.setEnabled(isManager);
    }
    
    private int recordSystemStart() {
        int result = tryToUpdateStopRecord();
        if (result != 1) {
            // Failed to update system stop event and just store system booting time
            Connection conn = null;
            Statement recordOperation = null;
            String sql = "insert into systemRun(startTm) values (curtime())";
            result = -1;
            String excepMsg = "(System run record: booting-insert)";

            try {
                conn = JDBCMySQL.getConnection();
                recordOperation = conn.createStatement(); 
                result = recordOperation.executeUpdate(sql);
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
                System.out.println("ex:" + ex.getMessage());
            } finally {
                closeDBstuff(conn, recordOperation, null, excepMsg);
            }             
        }
        
        return result;
    }

    private int tryToUpdateStopRecord() {
        Connection conn = null;
        PreparedStatement recordOperation = null;
        StringBuilder sb = new StringBuilder();
        sb.append("update systemrun set startTm = curtime() ");
        sb.append("where recNO in ");
        sb.append(    "(select recNo ");
        sb.append(    "from(select recNo, startTm ");
        sb.append(        " from systemrun ");
        sb.append(        " order by recNo desc limit 1 ) AS LastRowTable ");
        sb.append(        " where LastRowTable.startTm is null )");
        
        int result = -1;
        String excepMsg = "(System run record: booting-update)";
        
        try {
            conn = JDBCMySQL.getConnection();
            recordOperation = conn.prepareStatement(sb.toString());
            result = recordOperation.executeUpdate();
        } catch (SQLException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, excepMsg, getStatusTextField(), GENERAL_DEVICE);
        } finally {
            closeDBstuff(conn, recordOperation, null, excepMsg);
            return result;
        }          
    }
  
    private int recordSystemStop() {
        Connection conn = null;
        Statement stmt = null;
        int result = -1;
        String logMsg = "(System run record: shutdown-insert)";
        
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            result = stmt.executeUpdate("insert into SystemRun(stopTm) values (curtime())");
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, logMsg);
        } finally {
            closeDBstuff(conn, stmt, null, logMsg);
            String message = "System stopped";
            addMessageLine(MessageTextArea, message);
            logParkingOperation(OpLogLevel.LogAlways, message);

            return result;
        }
    }    

    private int recordLogin() {
        Connection conn = null;
        PreparedStatement stmt = null;
        int result = -1;
        String excepMsg = "(User activity: login[=insert])";
        
        try {
            int pIndex = 1;
            
            conn = JDBCMySQL.getConnection();
            stmt = conn.prepareStatement("insert into LoginRecord(userID, loginTS) values (?, curtime())");
            stmt.setString(pIndex++, loginID);
            result = stmt.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, null, excepMsg);
            return result;
        }              
    }

    private int tryToUpdateLoginRecord() {
        Connection conn = null;
        PreparedStatement recordOperation = null;
        String sql = "update loginrecord set logoutTS = curtime() " + 
                "where recNo in (select recNo from " + 
                "( select recNo, userID, logoutTS from loginRecord order by recNo desc limit 1) " +
                "as MaxRowTab " +
                "where MaxRowTab.userID = ? and MaxRowTab.logoutTS is null)";
        int result = -1;
        String excepMsg = "(User activity: logout[=update])";
        
        try {
            int pIndex = 1;
            conn = JDBCMySQL.getConnection();
            recordOperation = conn.prepareStatement(sql);
            recordOperation.setString(pIndex++, loginID);
            result = recordOperation.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, recordOperation, null, excepMsg);
            return result;
        }       
    }    
    
    private int recordLogout() {
        int result = tryToUpdateLoginRecord();
        if (result != 1) {
            // Failed to update login record. So, just stores logout time into the DB.
            Connection conn = null;
            PreparedStatement recordOperation = null;
            String sql = "insert into LoginRecord(userID, logoutTS) values (?, curtime())";
            result = -1;
            String excepMsg = "(User operation: Logout-record insertion)";

            try {
                int pIndex = 1;
                conn = JDBCMySQL.getConnection();
                recordOperation = conn.prepareStatement(sql);
                recordOperation.setString(pIndex++, loginID);
                result = recordOperation.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, recordOperation, null, excepMsg);
            }    
        }
        return result;
    }  

    private void processLogoutReally() {
        recordLogout();
        addMessageLine(MessageTextArea, "User '" + Globals.loginID + "' logged out" );
        Globals.loginID = null;
        Globals.loginPW = null;
        Globals.isManager = false;
        changeLogIOitemVisibility();
        AttendantTask_setEnabled(false);
    }

    private void saveMessageLines() {
        if (getMessageTextArea().getText().length() > 0) {
            // save the text a text file in log directory by
            // 1. make sure 'log' directory exists
            File fPath = new File("log");
            fPath.mkdirs();
            FileWriter writer = null;
            try {
                // 2. create text file("MessageList.txt") to store list
                writer = new FileWriter("log" + File.separator + "MessageList.txt");

                // 3. write list contents into the file
                writer.write(getMessageTextArea().getText());
            } catch (IOException ioe) {
                logParkingException(Level.SEVERE, ioe, "(message list writer write)");
            } finally {
                // 4. close system resources
                if (writer != null) {
                    try {
                    writer.close();
                    } catch (IOException ioe) {
                        logParkingException(Level.SEVERE, ioe, "(message list writer close)");
                    }
                }
            }
        }
    }

    private void initMessagelLines() {
        try (BufferedReader br = new BufferedReader(new FileReader("log" + File.separator + "MessageList.txt"))) 
        {
            String line = null; 
            while ((line = br.readLine()) != null) {
                getMessageTextArea().append(line + System.getProperty("line.separator"));
            }

            int len = getMessageTextArea().getDocument().getLength();
            getMessageTextArea().setCaretPosition(len); // places the caret at the bottom of the display area
            
        }  catch (FileNotFoundException fe) {
            addMessageLine(MessageTextArea, "Very First Run of OsParking!");
            logParkingException(Level.SEVERE, fe, "First Run of Parking Lot Manager");
        }  catch (IOException ie) {
            logParkingException(Level.SEVERE, ie, "(message list file IO exception)");
        }
    }

    static int changeCount = 0;
    
    public Component getComponentByName(HashMap compMap, String name) {
        if (compMap.containsKey(name)) {
            return (Component) compMap.get(name);
        }
        else {
            return null;
        }
    }

    private void setGatesAndRestPanel() {
        if (gateCount == 1) {
            gatePanel = new PanelFor1Gate();
        } else if (gateCount == 2) {
            gatePanel = new PanelFor2Gates();
        }
        // put titles to the gate panels
        for (int gateNo =1; gateNo <= gateCount; gateNo++)
        {
            TitledBorder tBorder = (TitledBorder)getGatePanel().getPanel_Gate(gateNo).getBorder();
            tBorder.setTitle(gateNames[gateNo]);
        }
        WholePanel.add(getGatePanel(), java.awt.BorderLayout.CENTER);
    }

    static CameraMessage[] dummyMessages = new CameraMessage[7];

    private void formCameraMessageArray() {
        for (byte idx = 1; idx <= 6; idx++) {
            dummyMessages[idx] 
                    = new CameraMessage( "car" + idx + ".jpg", getTagNumber(idx), getBufferedImage(idx)); 
    }
        // 2: full comparison. 
        // 3: unregistered
        // 5: not permitted
    }

    /**
     * Process an arrival of a vehicle which is represented by the arriving car image.
     * 
     * @param cameraID ID number of camera, usually same as the gate number where the camera is
     * @param imageSN   unique sequence number of the passed image
     * @param tagRecognized car tag number extracted from the image by some image processing SW
     * @param image car tag image of the arriving vehicle
     */

    private void initCarEntryList() {
        listSelectionModel = new ListSelectionModel[gateCount + 1];
        for (int gateNo = 1; gateNo <= gateCount; gateNo++)
        {
            listSelectionModel[gateNo] = getGatePanel().getEntryList(gateNo).getSelectionModel();
            listSelectionModel[gateNo].addListSelectionListener(new ListSelectionChangeHandler(gateNo));
            try {
                KeyPressedEventHandler handler = new KeyPressedEventHandler (gateNo);
                getGatePanel().getEntryList(gateNo).addKeyListener(handler);
            } catch (Exception ex) {
                System.out.println("");
            }
            loadPreviousEntries(gateNo);
        }
    }

    private void loadPreviousEntries(int gateNo) {
        @SuppressWarnings("unchecked")
        DefaultListModel<CarAdmission> listModel 
                = (DefaultListModel<CarAdmission>)admissionListModel[gateNo];
        
        StringBuffer sb = new StringBuffer("select arrseqno, concat ('-', ");
        sb.append("substr(arrivaltime, 9, 2), ");
        sb.append("substr(arrivaltime, 11), ' ', ");
        sb.append("ifnull(TagEnteredAs, TagRecognized)) as msgLine, ");
        sb.append("If (BarOperation = "); 
        sb.append(BarOperation.REMAIN_CLOSED.ordinal());
        sb.append(", '(closed)', '') as BarOptn ");
        sb.append("from car_arrival ");
        sb.append("where gateno = ? ");
        sb.append("order by arrSeqno desc ");
        sb.append("limit ? ");
 
        Connection conn = null;
        PreparedStatement pStmt = null;    
        ResultSet rs = null;
        long arrSeqNo = 0;
        
        try {
            // create the java statement
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(sb.toString());
            pStmt.setInt(1, gateNo);
            pStmt.setInt(2, RECENT_COUNT);
            rs = pStmt.executeQuery();
            
            String msgLine, barOptn ;
            while (rs.next()) {
                msgLine = rs.getString("msgLine");
                barOptn = rs.getString("BarOptn");
                arrSeqNo = rs.getLong("arrseqno");
                listModel.addElement(new CarAdmission(msgLine + barOptn, arrSeqNo));
            }
        } catch (SQLException se) {
            logParkingException(Level.SEVERE, se, 
                    "(select car_recent for seqNo: " + String.valueOf(arrSeqNo));
        } finally {
            closeDBstuff(conn, pStmt, rs, "(Loading previous car arrival records)");
        }
    }

    public synchronized void updateMainForm(int gateNo, String tagRecognized,  
            long arrSeqNo, BarOperation barOptn) {
        try
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd HH:mm:ss ");    
            
            Date currentTime = calendar.getTime();
            String tmDisplay = displayFormat.format(currentTime);
            
            @SuppressWarnings("unchecked")
            DefaultListModel<CarAdmission> listModel =
                    (DefaultListModel<CarAdmission>) admissionListModel[gateNo];
            
            String suffix = "";
            if (barOptn == BarOperation.REMAIN_CLOSED) {
                suffix = "(closed)";
            } else if (barOptn == BarOperation.STOPPED) {
                suffix = "(stopped)";
            }
            // add a row to the recent entry list for the gate
           listModel.add(0, new CarAdmission("-" + tmDisplay + tagRecognized + suffix, 
                   arrSeqNo));
            
            // display entry image on the label for the gate
            listSelectionModel[gateNo].setSelectionInterval(0, 0);
            showImage(gateNo);
            
            int lastIdx = listModel.getSize() - 1;
            while (lastIdx >= RECENT_COUNT)
            {
                listModel.remove(lastIdx);
                lastIdx = listModel.getSize() - 1;
            }  
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "(update main form display)");
        } 
    }

    public long insertDBrecord(int gateNo, Date arrivalTm, String tagRecognized, 
            String tagEnteredAs, BufferedImage bufferedImage, int unitSeqNo, int l2No, 
            String visitReason, BarOperation barOp) {

        String arrivalTmStr = sdf.format(arrivalTm);

        StringBuffer sb = new StringBuffer(" Insert Into Car_arrival (GateNo, ArrivalTime, ");
        sb.append("TagRecognized, TagEnteredAs, ImageBlob, AttendantID, ");
        if (unitSeqNo != -1) { sb.append("unitSeqNo, "); }
        if (l2No != -1) { sb.append("L2_No, "); }
        if (visitReason != null) { sb.append("VisitReason, "); }
        sb.append("BarOperation) "); 
        sb.append("Values (?, ?, ?, ?, ?, ?, "); 
        if (unitSeqNo != -1) { sb.append("?, "); }
        if (l2No != -1) { sb.append("?, "); }
        if (visitReason != null) { sb.append("?, "); }
        sb.append("?) ");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long arrSeqNo = -1;

        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            stmt.setInt(index++, gateNo);
            stmt.setString(index++, arrivalTmStr);
            stmt.setString(index++, tagRecognized);
            stmt.setString(index++, tagEnteredAs);
//            stmt.setString(index++, imageFilename);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            InputStream imageInStream = new ByteArrayInputStream(baos.toByteArray());            
            stmt.setBlob(index++, imageInStream);
            closeInputStream(imageInStream, "closing input strream for DB record insertion");
            
            stmt.setString(index++, Globals.loginID);
            if (unitSeqNo != -1) { stmt.setInt(index++, unitSeqNo); }
            if (l2No != -1) { stmt.setInt(index++, l2No); }
            if (visitReason != null) { stmt.setString(index++, visitReason); }
            stmt.setInt(index++, barOp.ordinal());
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                arrSeqNo = rs.getLong(1);
            }
        } catch (FileNotFoundException ex) {
            logParkingException(Level.SEVERE, ex, "image file not found");
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "error in car arrival insertion");
        } finally {
            closeDBstuff(conn, stmt, rs, "(insert car arrival tuple)");
            return arrSeqNo;
        }     
    }

    /**
     * send an open command to a gate bar.
     * It creates a Runnable object and use it to reschedule a timer which sends open command.
     * @param gateNo gate number where the bar exists
     * @param openCmdID unique ID assigned to the current open command
     * @param carPassingDelayMs duration to maintain the gate open while a car passes
     */
    public void openGate(int gateNo, int openCmdID, int carPassingDelayMs) {
        
        openCommandIssuedMs[gateNo] = System.currentTimeMillis();
        if (deviceType[GateBar.ordinal()][gateNo] == NaraBar.ordinal()) {
            NaraBarMan gateMan = (NaraBarMan) getDeviceManagers()[GateBar.ordinal()][gateNo];
            gateMan.getNaraBarMessages().add(new NaraMsgItem(Nara_MsgType.GateUp));
            // carPassingDelayMs ms 경과 후 차단기 닫는 명령을 add 하는 task run once 함
            gateMan.scheduleGateCloseAction(gateMan, carPassingDelayMs);
        } else {
            SendGateOpenTask sendOpenTask = 
                    new SendGateOpenTask(this, (byte) gateNo, openCmdID, carPassingDelayMs);
            getOpenGateCmdTimer()[gateNo].reschedule(sendOpenTask);
        }
    }    
    
    Object AckFileMutex = new Object();
    
    /**
     * Gives a gate open command after initializing ACK flag and sender timer.
     * 
     * @param gateNo ID of the gate to open
     * @param openCommandID Unique ID assigned for this open command
     * @param carPassingDelayMs Duration to maintain the gate opened
     */
    public void raiseGateBar(byte gateNo, int openCommandID, int carPassingDelayMs) {
        // the increment operator generates a unique ID for each open command created.

        if (deviceManagers[GateBar.ordinal()][gateNo].isNeverConnected()) {
            return;
        }
        
        if (DEBUG) {
            try {
                /**
                 * Save gate open command ID for a book keeping
                 */
                getIDLogFile()[GateBar.ordinal()][gateNo].write(openCommandID + System.lineSeparator());
                getIDLogFile()[GateBar.ordinal()][gateNo].flush();
            } catch (IOException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "saving open ID", getStatusTextField(), GENERAL_DEVICE);
            }
        }
        ParkingTimer openCmdTimer = getOpenGateCmdTimer()[gateNo];
        synchronized (openCmdTimer) {
            if (openCommAcked[gateNo]) {
                openCommAcked[gateNo] = false; 
            } else {
                if (openCmdTimer.hasTask()) {
                    if (DEBUG) {
                        int resends = ((SendGateOpenTask)openCmdTimer.getParkingTask()).getResendCount();
                        logParkingOperation(OpLogLevel.LogAlways, "Open ID: " + openCommandIDs[gateNo] 
                                + " cancelled after " + resends + " trials", gateNo);
                    }
                    openCmdTimer.cancelTask();
                }
            }
            openCommandIDs[gateNo] = openCommandID;
            openGate(gateNo, openCommandID, carPassingDelayMs);
        }
    }

    /**
     * Returns the text display area of the Main GUI.
     * 
     * @return the MessageTextArea
     */
    public javax.swing.JTextArea getMessageTextArea() {
        MessageTextArea.getCaret().setVisible(true);
        return MessageTextArea;
    }

    /**
     * @return the statusTextField
     */
    @Override
    public javax.swing.JTextField getStatusTextField() {
        return statusTextField;
    }

    /**
     * @return the SHUT_DOWN
     */
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
     * @return the loginForm
     */
    public LoginForm getLoginForm() {
        return loginForm;
    }

    /**
     * @param loginForm the loginForm to set
     */
    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public JLabel[][] getDeviceConnectionLEDs() {
        return deviceConnectionLabels;
    }

    /**
     */
    public SocketConnStat[][] getSockConnStat() {
        return sockConnStat;
    }

    /**
     * @return the openBarCmdTimer
     */
    public ParkingTimer[] getOpenGateCmdTimer() {
        return openGateCmdTimer;
    }

    /**
     * @return the socketMutex
     */
    public Object[][] getSocketMutex() {
        return socketMutex;
    }

    /**
     * @return the connectDeviceTimer
     */
    public ParkingTimer[][] getConnectDeviceTimer() {
        return connectDeviceTimer;
    }

    /**
     * @return the sendEBDmsgTimer
     */
    public ParkingTimer[][] getSendEBDmsgTimer() {
        return sendEBDmsgTimer;
    }

    private void closeIDLogFile() {
        for (DeviceType type : DeviceType.values()) {
            for (int gNo = 1; gNo <= gateCount; gNo++) {
                if (getIDLogFile()[gNo] != null) {
                    try {
                        getIDLogFile()[type.ordinal()][gNo].close();
                        IDLogFile[gNo] = null;
                    } catch (IOException e) {}
                }
            }
        }
    }        

    /**
     * @return the IDLogFile
     */
    public FileWriter[][] getIDLogFile() {
        return IDLogFile;
    }

    @Override
    public IDevice.IManager[][] getDeviceManagers() {
        return deviceManagers;
    }

    @Override
    public JLabel[][] getDeviceConnectionLabels() {
        return deviceConnectionLabels;
    }

    @Override
    public ToleranceLevel getTolerance(DeviceType type, byte deviceID) {
        return this.tolerance[type.ordinal()][deviceID];
    }

    @Override
    public void setTolerance(DeviceType type, byte deviceID, ToleranceLevel tolerance) {
        this.tolerance[type.ordinal()][deviceID] = tolerance;
    }

    Random rand = new Random();
    
    public synchronized void interruptEBoardDisplay(byte gateNo, String tagRecognized, 
            PermissionType permission, String remark, String tagRegistered, int imageSN, int carPassingDelayMs)
    {
        String tagNumber = tagRecognized;
        
        if (permission != UNREGISTERED)
            tagNumber = tagRegistered;
        
        interruptsAcked[gateNo] = false;
        IDevice.IManager eManager = deviceManagers[E_Board.ordinal()][gateNo];
        if (eManager == null) {
            statusTextField.setText("E-Board #" + gateNo + " manager isn't alive");
        } else {
            if (IDevice.isConnected(eManager, E_Board, gateNo))
//                    || Globals.gateDeviceTypes[gateNo].eBoardType == E_BoardType.LEDnotice)
            {
                long currTimeMs = System.currentTimeMillis();

                //<editor-fold desc="-- Init debug information">
                if (DEBUG) {
                    eBoardMsgSentMs[gateNo][EBD_Row.TOP.ordinal()] = currTimeMs;
                    eBoardMsgSentMs[gateNo][EBD_Row.BOTTOM.ordinal()] = currTimeMs;
                }
                //</editor-fold>

                // check E-Board type and process accordingly
                switch (Globals.gateDeviceTypes[gateNo].eBoardType) {
                    case LEDnotice:
                        //<editor-fold desc="-- Car arrival interrupt message for the LEDnotice hardware">
                        LEDnoticeManager manager = (LEDnoticeManager)deviceManagers[E_Board.ordinal()][gateNo];
                        manager.sendCarArrival_interruptMessage(
                                ledNoticeSettings[CAR_ENTRY_TOP_ROW.ordinal()],
                                ledNoticeSettings[CAR_ENTRY_BOTTOM_ROW.ordinal()],
                                gateNo, tagNumber, permission, remark, 
                                carPassingDelayMs);
                        //</editor-fold>
                        break;

                    default:
                        //<editor-fold desc="-- Car arrival interrupt message for the e-board simulator">
                        getSendEBDmsgTimer()[gateNo][EBD_Row.TOP.ordinal()].reschedule(
                                new SendEBDMessageTask(
                                        this, gateNo, EBD_Row.TOP, 
                                        getIntMessage(permission, tagNumber, gateNo, EBD_Row.TOP, 
                                                imageSN * 2 + EBD_Row.TOP.ordinal(), carPassingDelayMs), 
                                        imageSN * 2 + EBD_Row.TOP.ordinal()
                                )
                        );

                        getSendEBDmsgTimer()[gateNo][EBD_Row.BOTTOM.ordinal()].reschedule(
                                new SendEBDMessageTask(
                                        this, gateNo, EBD_Row.BOTTOM, 
                                        getIntMessage(permission, tagNumber, gateNo, EBD_Row.BOTTOM, 
                                                imageSN * 2 + EBD_Row.BOTTOM.ordinal(), carPassingDelayMs), 
                                        imageSN * 2 + EBD_Row.BOTTOM.ordinal()
                                )
                        );
                        //</editor-fold>
                        break;
                }

                //<editor-fold desc="-- Save debug information">
                if (DEBUG) 
                {
                    /**
                     * Save EBD interrupt message serial numbers for book keeping
                     */
                    try {
                        getIDLogFile()[E_Board.ordinal()][gateNo]
                                .write(imageSN * 2 + EBD_Row.TOP.ordinal() + System.lineSeparator());
                        getIDLogFile()[E_Board.ordinal()][gateNo]
                                .write(imageSN * 2 + EBD_Row.BOTTOM.ordinal() + System.lineSeparator());
                        getIDLogFile()[E_Board.ordinal()][gateNo].flush();
                    } catch (IOException ex) {
                        logParkingExceptionStatus(Level.SEVERE, ex, "saving open ID", getStatusTextField(), 
                                GENERAL_DEVICE);
                    }    
                }
                //</editor-fold>
            }
        }
    }
    
    byte[] getIntMessage(PermissionType permission, String  tagRecogedAs, 
            byte deviceNo, EBD_Row row, int msgSN, int delay) 
    {
        EBD_DisplaySetting setting = null;
        
        setting = EBD_DisplaySettings[row == EBD_Row.TOP ? 
                CAR_ENTRY_TOP_ROW.ordinal() : CAR_ENTRY_BOTTOM_ROW.ordinal()];
            
        String displayText = null;
        //<editor-fold desc="-- determind display text using e-board settings value like contentType">
        switch (setting.contentType) {
            case VERBATIM:
                displayText = setting.verbatimContent;
                break;
                
            case VEHICLE_TAG:
            case REGISTRATION_STAT:
                StringBuffer tagEnteredAs = new StringBuffer(); // call-by-reference usage
                StringBuffer remark = new StringBuffer();               

                // fetch vehicle registration status from DB
                if (setting.contentType == VEHICLE_TAG) {
                    if (permission == UNREGISTERED)
                        displayText = tagRecogedAs;
                    else
                        displayText = tagEnteredAs.toString();
                } else { // REGISTRATION_STAT
                    if (permission == ALLOWED)
                        displayText = "Registered Car";
                    else if (permission == DISALLOWED )
                        displayText = "Registered Car(" + remark + ")";
                    else if (permission == UNREGISTERED) {
                        displayText = "A Visiting Car";
                    }
                }
                break;
            case GATE_NAME:
                displayText = gateNames[deviceNo];
                break;
                
            default:
                displayText = "";
                break;
        }
        //</editor-fold>
        
        byte[] displayTextBytes = null;
        try {
            displayTextBytes = displayText. getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logParkingException(Level.SEVERE, ex, "getting byte array for : " + displayText, deviceNo);  
        }
        int displayTextLength = displayTextBytes.length;
        
        // <code:1><length:2><row:1><msgSN:4><text:?><type:1><color:1><font:1><pattern:1><cycle:4>
        // <delay:4><check:2>
        byte code = (byte) (row == EBD_Row.TOP ? EBD_INTERRUPT1.ordinal() : EBD_INTERRUPT2.ordinal());
        short wholeMessageLen // length of 10 fields from <length> to <check>
                = (short)(displayTextLength + 21); // 21 == sum of 10 fields == 11 fields except <text>
        byte[] lenBytes //  {--Len[1], --Len[0]}
                = {(byte)((wholeMessageLen >> 8) & 0xff), (byte)(wholeMessageLen & 0xff)}; 
        byte[] wholeMessageBytes = new byte[wholeMessageLen + 1];
        
        formMessageExceptCheckShort(code, lenBytes, row, msgSN, displayTextBytes, setting, delay, 
                wholeMessageBytes);
        
        //<editor-fold desc="complete making message byte array by assigning 2 check bytes">
        // calculate 2 check bytes by adding all bytes in the of 9 fields: from <code:1> to <delay:4>
        byte[] check = new byte[2];
        addUpBytes(wholeMessageBytes, check);
        
        int idx = wholeMessageBytes.length - 2;
        wholeMessageBytes[idx++] = check[0];
        wholeMessageBytes[idx++] = check[1];
        //</editor-fold>
        
        return wholeMessageBytes;
    }    

    /**
     * @return the passingDelayStat
     */
    public PassingDelayStat[] getPassingDelayStat() {
        return passingDelayStat;
    }

    private boolean processLogOut(boolean forced) {
        boolean isLoggedOut = false;
        try {
            int result = 0;
            if (forced) {
                JOptionPane.showMessageDialog(this, 
                    "'" + Globals.loginID + "' will be logged out.");
                result = JOptionPane.YES_OPTION;
            } else {
                result = JOptionPane.showConfirmDialog(this, 
                    "Does '" + Globals.loginID + "' want to logout?",
                    "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            }
            if (result == JOptionPane.YES_OPTION) {
                processLogoutReally();
                enableAdminOnlyItem(false);
                isLoggedOut = true;
            }
        } catch (Exception ex) {
            com.osparking.global.Globals.logParkingException(Level.SEVERE, ex, "(Process User Logout)");
        }    
        return isLoggedOut;
    }

    private void fetchPassingDelay() {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        
        for (int gateID = 1; gateID <= gateCount; gateID++) 
        {
            try {
                conn = JDBCMySQL.getConnection();
                selectStmt = conn.createStatement();
                rs = selectStmt.executeQuery("SELECT passingDelayPreviousAverageMs, "
                        + "passingDelayPreviousPopulation, passingDelayCalculationTime " 
                        + "FROM gatedevices WHERE gateid = " + gateID);
                while (rs.next() ) {
                    float average = rs.getFloat("passingDelayPreviousAverageMs");
                    getPassingDelayStat()[gateID]
                            .setPassingDelayPreviousAverageMs((average == 0 ? -1.0f : average));
                    getPassingDelayStat()[gateID]
                            .setPassingDelayPreviousPopulation(rs.getInt("passingDelayPreviousPopulation"));
                    getPassingDelayStat()[gateID]
                            .setPassingDelayCalculationTime(rs.getTimestamp("passingDelayCalculationTime"));
                }
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, "(Loading passing delay records)");
            } finally {
                closeDBstuff(conn, selectStmt, rs, "(Loading passing delay records)");
            }
        }
    }

    /**
     * @param configureSettingsForm the configureSettingsForm to set
     */
    public void setConfigureSettingsForm(Settings_System configureSettingsForm) {
        this.configureSettingsForm = configureSettingsForm;
    }

    @Override
    public void clearAttendantManageForm() {
        attendantsListForm = null;
    }

    private static class ListSelectionChangeHandler implements ListSelectionListener {
        int gateNo = 0;
        
        private ListSelectionChangeHandler(int gateNo) {
            this.gateNo = gateNo;
        }
        
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting())
            {
                return;
            }
            final ListSelectionModel lsm = (ListSelectionModel)e.getSource();  
            showImage( gateNo);
        }
    }

    public static void showImage(final int gateNo) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() 
            {
                if (getGatePanel().getEntryList(gateNo).getSelectedIndex() > -1) 
                {
                    getShownImageRow()[gateNo] = getGatePanel().getEntryList(gateNo).getSelectedIndex();

                    JLabel picLabel = getGatePanel().getCarPicLabels()[gateNo];
//                    System.out.println("gate: " + gateNo + ", w: " + picLabel.getSize().width + ", h: " + picLabel.getHeight());
                    CarAdmission carEntry = (CarAdmission)getGatePanel().getEntryList(gateNo).getSelectedValue();

                    String sql = new String( "Select imageblob from car_arrival where ArrSeqNo = ?");
                    Connection conn = null;
                    PreparedStatement pStmt = null;    
                    ResultSet rs = null;
                    try {
                        // <editor-fold defaultstate="collapsed" desc="-- Fetch image from the DB and show it ">
                        conn = JDBCMySQL.getConnection();
                        pStmt = conn.prepareStatement(sql);
                        pStmt.setLong(1, carEntry.getArrSeqNo());
                        rs = pStmt.executeQuery();
                        if (rs.next()) {
                            try {
                                InputStream imageInStream = rs.getBinaryStream("ImageBlob");
                                
                                if (imageInStream == null) {
//                                    picLabel.setIcon(createStretchedIcon(picLabel.getSize(), noPictureImg, false));
//                                    originalImgWidth[gateNo] = noPictureImg.getWidth(); 
                                    picLabel.setIcon(null);
                                    picLabel.setText("No Image Exists");
                                } else {
                                    picLabel.setText(null);
                                    BufferedImage imageRead = ImageIO.read(imageInStream);
                                    picLabel.setIcon(createStretchedIcon(picLabel.getSize(), imageRead, false));
                                    closeInputStream(imageInStream, "(image loading from DB)");
                                    originalImgWidth[gateNo] = imageRead.getWidth(); 
                                    gatePanel.setGateImage((byte)gateNo, imageRead);
                                }
                            } catch (IOException ex) {
                                logParkingException(Level.SEVERE, ex, "(image loading from DB)");
                            }
                        }
                        // </editor-fold>
                    }
                    catch(Exception e) {
                        logParkingException(Level.SEVERE, e, "(in car arrival image display routine)");
                    }
                    finally {
                        closeDBstuff(conn, pStmt, rs, "(in car arrival image resource )");
                    }
                }
            }
        });
    }
    
    private void formMessageExceptCheckShort(byte code, byte[] lenBytes, EBD_Row row, int msgSN, 
            byte[] coreMsg, EBD_DisplaySetting setting, int delay, byte[] wholeMessageBytes) 
    {
        int idx = 0;
        
        wholeMessageBytes[idx++] = code;
        wholeMessageBytes[idx++] = lenBytes[0];
        wholeMessageBytes[idx++] = lenBytes[1];
        wholeMessageBytes[idx++] = (byte)row.ordinal();

        for (byte dByte : ByteBuffer.allocate(4).putInt(msgSN).array()) {
            wholeMessageBytes[idx++] = dByte;
        }        
        
        if (coreMsg != null) {
            for (byte aByte: coreMsg) {
                wholeMessageBytes[idx++] = aByte;
            }
        }
        wholeMessageBytes[idx++] = (byte)setting.contentType.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.textColor.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.textFont.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.displayPattern.ordinal();

        if (code == EBD_INTERRUPT1.ordinal() || code == EBD_INTERRUPT2.ordinal()) {
            for (byte cByte : ByteBuffer.allocate(4).putInt(setting.displayCycle).array()) {
                wholeMessageBytes[idx++] = cByte;
            }
        }
        
        for (byte dByte : ByteBuffer.allocate(4).putInt(delay).array()) {
            wholeMessageBytes[idx++] = dByte;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    javax.swing.UIManager.getLookAndFeelDefaults().put("ScrollBar.minimumThumbSize", new Dimension(30, 30)); 
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logParkingException(Level.SEVERE, ex, "(ClassNotFoundException)");
        } catch (InstantiationException ex) {
            logParkingException(Level.SEVERE, ex, "(InstantiationException)");
        } catch (IllegalAccessException ex) {
            logParkingException(Level.SEVERE, ex, "(IllegalAccessException)");
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logParkingException(Level.SEVERE, ex, "(UnsupportedLookAndFeelException)");
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        readEBoardSettings(EBD_DisplaySettings);
        Thread.currentThread().setPriority((Thread.MAX_PRIORITY));
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                testUniqueness("ParkingLotManager", "OsParking");
                ControlGUI mainForm = new ControlGUI();
                parentGUI = mainForm;
                mainForm.recordSystemStart();
                mainForm.setVisible(true);
                Globals.shortLicenseDialog(mainForm);
            }
        });
    }
}
