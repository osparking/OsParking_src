/* 
 * Copyright (C) 2015, 2016  Open Source Parking, Inc.(www.osparking.com)
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
package com.osparking.global;

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_NO;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_YES;
import static com.osparking.global.CommonData.ADMIN_ID;
import static com.osparking.global.CommonData.PROMPTER_KEY;
import static com.osparking.global.names.ControlEnums.ButtonTypes.LicenseButton;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.BUILDING_CB_ITEM;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.HIGHER_CB_ITEM;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.LOWER_CB_ITEM;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.LOWER_HIGHER_CB_ITEM;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.ROOM_BUILDING_CB_ITEM;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.ROOM_CB_ITEM;
import static com.osparking.global.names.ControlEnums.DialogMessages.CONN_TRIAL;
import static com.osparking.global.names.ControlEnums.TextType.DISCONN_MSG;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.PIC_HEIGHT;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import com.osparking.global.names.JDBCMySQL;
import com.osparking.global.names.JustOneLock;
import com.osparking.global.names.LogFileHandler;
import com.osparking.global.names.Manager;
import static com.osparking.global.names.DB_Access.PIC_WIDTH;
import static com.osparking.global.names.DB_Access.deviceIP;
import static com.osparking.global.names.DB_Access.devicePort;
import static com.osparking.global.names.DB_Access.deviceType;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.gateNames;
import static com.osparking.global.names.DB_Access.locale;
import static com.osparking.global.names.DB_Access.maxMessageLines;
import static com.osparking.global.names.DB_Access.opLoggingIndex;
import com.osparking.global.names.InnoComboBoxItem;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.CameraType;
import com.osparking.global.names.OSP_enums.E_BoardType;
import com.osparking.global.names.OSP_enums.GateBarType;
import javax.swing.JPanel;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.OSP_enums.EBD_ContentType;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.OSP_enums.VersionType;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.SocketConnStat;
import java.awt.Container;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JViewport;

/**
 * Defines names and methods used globally in the Parking Lot manager application developed by 
 * Open Source Parking Inc. <p>(Company logo: <img src ="doc-files/64px.png"/>)</p>
 * 
 * @author Open Source Parking Inc.
 * @version "%I%,%G%"
 */
public class Globals {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;    

    public static float ERROR_RATE = 0.01f;
    public static int TASK_BAR_HEIGHT = 47;
    public static int GATE_GUI_HEIGHT = 641;
    public static int CAMERA_GUI_WIDTH = 641;
    public static int GATE_BAR_WIDTH = 300;
    public static int GATE_BAR_HEIGHT = 370;    
    public static int E_BOARD_WIDTH = 321;
    public static int E_BOARD_HEIGHT = 300;
    public static int BLDG_TAB_WIDTH = 250;
    public static int PARKING_LOT_NAME_LENGTH_MAX = 30;
    public static int VERBATIM_CONTENT_LENGTH_MAX = 100;
    public static int GATE_NAME_LENGTH_MAX = 50;
    public static int PORT_NUMBER_LENGTH_MAX = 5;
    public static int SIMULATOR = 0;
    public static DefaultListModel<?>[] admissionListModel = new DefaultListModel<?> [5];
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); 
    public static SimpleDateFormat timeFormatMMSS = new SimpleDateFormat("mm_ss"); 
    public static SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
    public static int SIX_HOURS = 3600 * 1000 * 6;
    
    public static void setTableFocusAt(JTable driverTable, 
            int rowIdx, int colIdx) 
    {
        driverTable.setRowSelectionInterval(rowIdx, rowIdx); 
        driverTable.setColumnSelectionInterval(colIdx, colIdx);     
    }    
    
    public static String findLoginIdentity() {
        String managerID = "manager";
        String guestID = "guest";
        Object[] possibleValues = { ADMIN_ID, managerID, guestID};
        
        Object selectedValue = JOptionPane.showInputDialog(null,
            "Choose Login User Level", "User Type", 
            JOptionPane.QUESTION_MESSAGE, null,
            possibleValues, possibleValues[0]);
        
        loginID = (String)selectedValue;
        if (loginID != null) {
            if (loginID.equals(ADMIN_ID)) {
                Globals.isAdmin = true;
                Globals.isManager = true;
                if (DEBUG_FLAG) {
                    DEBUG = true;
                }
                if (RANDOM_FLAG) {
                    RANDOM_ATTENDANT = true;
                }
            } else if (loginID.equals(managerID)) {
                Globals.isAdmin = false;
                Globals.isManager = true;
            } else if (loginID.equals(guestID)) {
                Globals.isAdmin = false;
                Globals.isManager = false;                 
            }
        }
        return loginID;
    }
    
    public static void augmentComponentMap(
            Object component, HashMap<String, Component> componentMap) 
    {
        String name = null;
        if (component instanceof JTextField) {
            name = ((JTextField)component).getName();

            if (name != null && name.length() > 0) {
                componentMap.put(name, (JTextField)component);
            }
        } else if (component instanceof JComboBox) {
            name = ((JComboBox)component).getName();

            if (name != null && name.length() > 0) {
                componentMap.put(name, (JComboBox)component);
            }
        } else if (component instanceof JCheckBox) {
            name = ((JCheckBox)component).getName();

            if (name != null && name.length() > 0) {
                componentMap.put(name, (JCheckBox)component);
            }
        } else if (component instanceof JButton) {
            name = ((JButton)component).getName();

            if (name != null && name.length() > 0) {
                componentMap.put(name, (JButton)component);
            }
        } else if (component instanceof JLabel) {
            name = ((JLabel)component).getName();

            if (name != null && name.length() > 0) {
                componentMap.put(name, (JLabel)component);
            }
        } else if (component instanceof Container) {
            
            if (component instanceof JPanel) {
                name = ((JPanel)component).getName();

                if (name != null && name.length() > 0) {
                    componentMap.put(name, (JPanel)component);
                }
            } 
            
            for (Component innerComponent : ((Container)component).getComponents()) {
                augmentComponentMap(innerComponent, componentMap);
            }
        }
    }    
    
    public static Point getLocationOnCurrentScreen(final Component c) {
        final Point relativeLocation = c.getLocationOnScreen();

        final Rectangle currentScreenBounds = c.getGraphicsConfiguration().getBounds();

        relativeLocation.x -= currentScreenBounds.x;
        relativeLocation.y -= currentScreenBounds.y;

        return relativeLocation;
    }    
    
    public static Component getComponentByName(
            HashMap<String, Component> componentMap, String name) 
    {
        
        if (componentMap.containsKey(name)) {
            return (Component) componentMap.get(name);
        }
        else 
            return null;
    }         
    
    public static class GateDeviceType {
        public OSP_enums.CameraType cameraType = OSP_enums.CameraType.Simulator;
        public OSP_enums.E_BoardType eBoardType = OSP_enums.E_BoardType.Simulator;
        public OSP_enums.GateBarType gateBarType = OSP_enums.GateBarType.Simulator;
    }    
    
    public static GateDeviceType[] gateDeviceTypes;
    
    public static void initDeviceTypes() {
        gateDeviceTypes = new GateDeviceType[gateCount +1];
        
        for (int gateID = 1; gateID <= gateCount; gateID++) {
            gateDeviceTypes[gateID] = new GateDeviceType();
//            int subTypeOrd = deviceType[Camera.ordinal()][gateID];
//            gateDeviceTypes[gateID].cameraType = CameraType.values()[(gateID == 1) ? subTypeOrd : subTypeOrd + 1];
            gateDeviceTypes[gateID].cameraType = CameraType.values()[deviceType[Camera.ordinal()][gateID]];
            gateDeviceTypes[gateID].eBoardType = E_BoardType.values()[deviceType[E_Board.ordinal()][gateID]];
            gateDeviceTypes[gateID].gateBarType = GateBarType.values()[deviceType[GateBar.ordinal()][gateID]];
        }
        
    }  
    
    public static int getGateDevicePortNo(DeviceType deviceType, byte deviceID) {
        int portNo = 0;
        
        switch (deviceType) {
            case Camera: 
                if (gateDeviceTypes[deviceID].cameraType != OSP_enums.CameraType.Simulator) {
                    portNo = Integer.parseInt(devicePort[deviceType.ordinal()][deviceID]);
                }
                break;
                
            case E_Board: 
                if (gateDeviceTypes[deviceID].eBoardType != OSP_enums.E_BoardType.Simulator) {
                    portNo = Integer.parseInt(devicePort[deviceType.ordinal()][deviceID]);
                }
                break;
                
            case GateBar: 
                if (gateDeviceTypes[deviceID].gateBarType != OSP_enums.GateBarType.Simulator) {
                    portNo = Integer.parseInt(devicePort[deviceType.ordinal()][deviceID]);
                }
                break;
        }
        if (portNo == 0) // which means this device at that gate is a simulator
            portNo = getPort(deviceType, deviceID, Globals.versionType) + deviceID;
        
        return portNo;
    }    
    
    /**
     * background color for the main frame window.
     */    
    public static Color MainBackground = Color.decode("#f0fff0");
    
    public final static int OSP_FALSE = 0;
    public final static int OSP_TRUE = 1;
    /**
     * background color for the frames generated directly from the main frame window.
     */
    public static Color PopUpBackground = Color.decode("#D6FFFF");

    static String[] iconFilenames = {
        "/16px.png",             
        "/32px.png",             
        "/48px.png",             
        "/64px.png", 
    };        
        
    public static List<Image> OSPiconList = new ArrayList<Image>();
    static {
        for (String iconPath: iconFilenames) {
            URL iconURL = new Globals().getClass().getResource(iconPath);
            if (iconURL == null) {
                JOptionPane.showMessageDialog(null,
                    "Can't find icon file below." + System.lineSeparator() + "File: " + iconPath, 
                    "File Not Found", JOptionPane.ERROR_MESSAGE);                
            } else {
                OSPiconList.add(new ImageIcon(iconURL).getImage());
            }
        }    
    }

    public static ImageIcon plusIcon = null; 
    static {
        plusIcon = new javax.swing.ImageIcon(new Globals().getClass().getResource("/plus.png"));        
    }
    
    public static ImageIcon getPlusIcon() {
        return plusIcon;
    }
    
    public static ImageIcon osParkingIcon = null; 
    static {
        osParkingIcon = new javax.swing.ImageIcon(new Globals().getClass().getResource("/64px.png"));        
    }
    
    public static ImageIcon getOsParkingIcon() {
        return osParkingIcon;
    }
     
    public static ImageIcon GPL30_Icon = null; 
    static {
        GPL30_Icon = new javax.swing.ImageIcon(new Globals().getClass().getResource("/gplv3-127x51.png"));        
    }
    
    public static ImageIcon getLGPN30_Icon() {
        return GPL30_Icon;
    }
    
    public static ImageIcon quest20_Icon = null; 
    static {
        quest20_Icon = new javax.swing.ImageIcon(new Globals().getClass().getResource("/quest20.png"));        
    }
    
    public static ImageIcon getQuest20_Icon() {
        return quest20_Icon;
    }
    
    public static ImageIcon minusIcon = null; 
    static {
        minusIcon = new javax.swing.ImageIcon(new Globals().getClass().getResource("/minus.png"));        
    }
    
    public static ImageIcon getMinusIcon() {
        return minusIcon;
    }
    
    public final static int GENERAL_DEVICE = 0;
    
    static String[] carImageFiles = {
        "/car1.jpg",
        "/car2.jpg",
        "/car3.jpg",
        "/car4.jpg",
        "/car5.jpg",
        "/car6.jpg"
    }; 
    
    public static List<BufferedImage> carImgList = new ArrayList<BufferedImage>();
    static {
        BufferedImage img = null;
        for (String carImageFile: carImageFiles) {
            try {
                img = ImageIO.read(new Globals().getClass().getResource(carImageFile)); 
                carImgList.add(img);
            } catch (Exception ex) {
                if (img == null) {
                    JOptionPane.showMessageDialog(null,
                        "Can't find icon file below." + System.lineSeparator() + "File: " + carImageFile, "File Not Found",
                        JOptionPane.ERROR_MESSAGE);                
                }
            }
        }    
    }    
    
    public static BufferedImage getBufferedImage(int index) {
        if (1 <= index && index <= carImgList.size()) {
            return carImgList.get(index - 1);
        } else {
            return null;
        }
    }       
    
    public static void shortLicenseDialog(JFrame parentForm, String name, String location) {
        
        String message = 
                name + "," + System.lineSeparator() +
                "Copyright (C) 2015, 2016  Open Source Parking, Inc." + System.lineSeparator() +
                "This program comes with ABSOLUTELY NO WARRANTY;" + System.lineSeparator() +
                "for details click [" + LicenseButton.getContent() +"] button which is at the " +
                location + " corner." 
                + System.lineSeparator() +
                "This is free software, and you are welcome to redistribute it" + System.lineSeparator() +
                "under certain conditions; use [" + LicenseButton.getContent() 
                +"] button for more details.";
        JOptionPane.showMessageDialog(null, message, "License Notice", 
                JOptionPane.PLAIN_MESSAGE, getLGPN30_Icon());    
    }
    
    public static void shortLicenseDialog(JFrame parentForm) {
        String message = 
                "OsParking, a parking lot management program," + System.lineSeparator() +
                "Copyright (C) 2015, 2016  Open Source Parking, Inc." + System.lineSeparator() +
                "This program comes with ABSOLUTELY NO WARRANTY;" + System.lineSeparator() +
                "for details use top menu [System] > [" + LicenseButton.getContent() 
                +"] command." + System.lineSeparator() +
                "This is free software, and you are welcome to redistribute it" + System.lineSeparator() +
                "under certain conditions; use [" + LicenseButton.getContent()
                +"] command for more details.";
        JOptionPane.showMessageDialog(null, message, "License Notice", 
                JOptionPane.PLAIN_MESSAGE, getLGPN30_Icon());    
    }
    
    public static int stringLengthInPixels(String displayText, JTextField textField) {
        AffineTransform affinetransform = new AffineTransform();     
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
        int textWidth = (int)(textField.getFont().getStringBounds(displayText, frc).getWidth());  
        return textWidth;
    }    
    
    /**
     * It recognizes the car tag number in this way since there is no LPR software employed yet.
     * It is for the simulated camera version without proper car number recognizer program.
     * @param size number of bytes of car image file received
     * @return tag number of the car entering
     */    
    public static String getTagNumber(byte picNo) {
        String tagNumber = null;
        
        if (language == Locale.KOREAN) {
            switch (picNo) {
                case 1:
                    tagNumber = "26누8648"; // parking permitted
                    break;
                case 2:
                    tagNumber = "12거3456"; // parking permitted
                    break;
                case 3:
                    tagNumber = "26로3689"; // permitted, exact comparision
                    break;
                case 4:
                    tagNumber = "서울11가1234"; // not permitted,bad parking habit
                    break;
                case 5:
                    tagNumber = "52가3108"; // unregistered car, visitor
                    break;
                case 6:
                    tagNumber = "39나2764"; // unregistered car, visitor
                    break;
                default:
                    break;
            }
        } else {
            switch (picNo) {
                case 1:
                    tagNumber = "26Nu8648"; 
                    break;
                case 2:
                    tagNumber = "12GA3456"; 
                    break;
                case 3:
                    tagNumber = "26RO3689"; 
                    break;
                case 4:
                    tagNumber = "SOUL11GA1234"; 
                    break;
                case 5:
                    tagNumber = "52GA3108"; 
                    break;
                case 6:
                    tagNumber = "39NA2764"; 
                    break;
                default:
                    break;
            }
        }
        
        return tagNumber;
    }      
    
    public static byte getNextCarNum(Random rand, byte imageFileID) {
        byte picNo = 0;
        do {
             picNo = (byte)(1 + rand.nextInt(6)); // correct one
        } while (picNo == imageFileID);
        return picNo;
    }    
    
    // wholeMessageBytes
    public static void addUpBytes(byte[] wholeMessageBytes, byte[] checkShort) {
        int total = 0;

        for (byte aByte : wholeMessageBytes) {
            total += aByte;
        }
        short result = (short) (total % Math.pow(2, 16));
        checkShort[0] = (byte)((result >> 8) & 0xff);
        checkShort[1] = (byte)(result & 0xff);
    }    
    
    /**
     * System settings variable.
     * <p>
     * a string that holds language code spoken by the people who are using this program
     */       
    public static String languageCode = "ko";
    
    /**
     * System settings variable.
     * <p>
     * a string that holds country code where this program is going to run in
     */       
    public static String countryCode = "kr";
    
    /**
     * fonts used in OsParking GUI
     */
    public final static byte PLAIN = 0;
    public final static byte BOLD = 1;
    public final static byte ITALIC = 2;
    public final static byte BOLD_ITALIC = 3;

    public static String font_Type = "SansSerif";
    public static int font_Size = 14;
    public static int font_Size_List = 12;
    public static int LABEL_INC = 3;
    public static int CONTENT_INC = 5;
    public static int head_font_Size = 16;
    public static int font_Style = BOLD;        
        
    /**
     * a sample car tag image file full path name
     */
    public static final String restAreaImage = "/osparking_logo.jpg";
    
    /**
     * an initial value of the ID label on the main form
     */
//    public static String IDBeforeLogin = "(n/a)";
    
    /**
     * As a single user program, this variable('loginID') stores the user ID of current user.
     * In case no user logged in, it holds 'Null' value.
     */
    public static String loginID = null;
    
    /**
     * This variable stores the default encrypted password(41 bytes long) of currently logged in 
     * user
     */
    public static String loginPW = null;
    
    /**
     * This flag is set to true when the current logged in user has an administrator authority.
     * Having administrator authority means that more menus and/or commands are available
     * to the current user.
     */
    public static boolean isManager = false;
    public static boolean isAdmin = false;
    
    /**
     *  Records the fact that the operation logging level has changed from a logging level 
     *  into non-logging(=off).
     * <p>
     *  The difficulty comes from the situation that this system settings change should be
     *  logged from within a non-logging mode.
     * <p>
     *  After checking this variable, the program is able to log system settings change by 
     *  increasing the operation log level as high as 'Level.INFO' temporarily.
     */
    public static boolean isFinalWishLog = false;
    
    /**
     * Variable that connects to the MySQL database instance for the parking lot program
     */
//    public static Connection conn = null; 

    /**
     * It is used to log the occurrence of operation registered to do so using the system
     * settings menu.
     */
    private static Logger operationLog = null;
    public static String operationLogName = "operation";
    
    /**
     * It is used to log the occurrence of an exception during the execution of the parking lot 
     * management program.
     */
    public static Logger exceptionLog = null;
    public static Logger exceptionLogDev = null;
    public static String exceptionLogName = "exception";    
    public static String exceptionPerDevice = "exceptionPerDev";    
     
    /**
     * Various constants used in the E-Board display control messages.
     */
    // public final static byte TOP_ROW = 0;
    // public final static byte BOTTOM_ROW = 1;    

    public static int getFirstPart(String tagRecognized, StringBuilder firstPart) {
        //        String firstPart = tagRecognized.substring(
        //                tagRecognized.length() - 7, tagRecognized.length() - 5);
        int idx = 0;
        for (   ; idx < tagRecognized.length() && firstPart.length() < 2; idx++) {
            if (Character.isDigit(tagRecognized.charAt(idx))) {
                firstPart.append(tagRecognized.charAt(idx));
            } else {
                if (firstPart.length() > 0)
                    return idx;
            }
        }
        return idx;
    }    
    
    public static void showLicensePanel(JFrame parent, String title) {
        JDialog licenseDialog = new JDialog(parent, title, true);
        licenseDialog.setResizable(false);
        licenseDialog.getContentPane().add(new LicensePanel(licenseDialog));
        licenseDialog.pack();
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - licenseDialog.getWidth()) / 2;
        final int y = (screenSize.height - licenseDialog.getHeight()) / 2;
        licenseDialog.setLocation(x, y);        
        licenseDialog.setVisible(true);           
    }
    
    public static void initializeLoggers() {
        // Initialize OsParking loggers
        setExceptionLog(Logger.getLogger(exceptionLogName));
        getExceptionLog().setLevel(Level.ALL);
        
        setExceptionLogDev(Logger.getLogger(exceptionPerDevice));
        getExceptionLogDev().setLevel(Level.ALL);   
                
        setOperationLog(Logger.getLogger(operationLogName));
        getOperationLog().setLevel(index2Level(opLoggingIndex));
                
    }    
    
    /**
     * Setter for the global operation logger
     * @param operationLog Logger instance to which the global operation logger is set
     */
    public static void setOperationLog(Logger operationLog) {
        Globals.operationLog = operationLog;
    }

    /**
     * Setter for the global exception logger
     * @param exceptionLog Logger instance to which the global operation logger is set
     */   
    public static void setExceptionLog(Logger exceptionLog) {
        Globals.exceptionLog = exceptionLog;
    }

    /**
     * @param aExceptionLogDev the exceptionLogDev to set
     */
    public static void setExceptionLogDev(Logger aExceptionLogDev) {
        exceptionLogDev = aExceptionLogDev;
    }    

    /**
     * Getter for the global operation logger
     * @return the global operation logger instance
     */
    public static Logger getOperationLog() {
        return operationLog;
    }

    /**
     * Getter for the global exception logger
     * @return the global exception logger instance
     */
    public static Logger getExceptionLog() {
        return exceptionLog;
    }

    /**
     * @return the exceptionLogDev
     */
    public static Logger getExceptionLogDev() {
        return exceptionLogDev;
    }
         
    public static int LIST_HEIGHT_MIN = 240;
    
    /**
     * Defines the car image width on the main form in the pixel unit.
     */
    public final static int FRAME_WIDTH = 450;
    
    /**
     * Defines the car image height on the main form in the pixel unit.
     */
    public final static int FRAME_HEIGHT = 287;
    
    private static JLabel[] CarPicLabels = new JLabel[5];
    
    public static int[] originalImgWidth = new int[5];
    public static BufferedImage[] originalImg = new BufferedImage[5];

    /**
     * Time during which image sender waits before it resend image if it didn't see ACK 
     * message from manager.
     */
    public final static long RESEND_PERIOD = 10;    
    public static int PULSE_PERIOD = 1000;
    public static int LED_PERIOD = 1000;
    public static int CAR_PERIOD = LED_PERIOD * 2;
    public static int MAX_PASSING_DELAY = 10000; // unit: miliseconds
    public static float ARRIVAL_PROBABILITY = 0.25f;
    
    /**
     * Number of LED label blinking cycles before a communicating party disconnects socket connection
     * voluntarily if it doesn't see a heartbeat message consecutively from the other party of  
     * commnuication. 
     * <p>That is, the party is willingly tolerate this amount of cycle in deficit of the other party
     * heartbeat.</p>
     */
    public static int MAX_TOLERANCE = 3;
    
    public static int RECENT_COUNT = 500;
    
    /**
     * Determines if this run of the program is to collect data for program debugging or not.
     */
    public static boolean DEBUG = false;
    public static boolean DEBUG_FLAG = false;
    
    public static boolean RANDOM_ATTENDANT = false;    
    public static boolean RANDOM_FLAG = false;    
    
    /**
     * Signifies the version type of the program which is used to differenciate port numbers for each device type.
     * <p>This variable allows programmers to run multiple copies of the same program simultaneously.</p>
     */
    public static VersionType versionType = VersionType.RELEASE;    
      
    /**
     * Strings used to check the program command line argument option types.
     */
    public final static String DEBUG_OPTION = "-debug";
    public final static String VERSION_TYPE = "-version";
    
    public final static String DEFAULT_MESSAGE = "<Critical Status Information>";
    public final static int MESSAGE_DISPLAY_PERIOD_IN_ms = 3 * 60 * 1000; // 3 minutes
    public final static int MAX_GATES = 4;
    
    public final static int CAMERA_PORT_DEV = 6110;
    public final static int CAMERA_PORT_RUN = 6120;
    public final static int GATE_BAR_PORT_DEV = 6130;
    public final static int GATE_BAR_PORT_RUN = 6140;
    public final static int E_BOARD_PORT_DEV = 6150;
    public final static int E_BOARD_PORT_RUN = 6160;
    
    public final static String CAMERA_LOCK_FILE_DEV = "camera_dev";
    public final static String CAMERA_LOCK_FILE_RUN = "camera_run";
    public final static String GATE_BAR_LOCK_FILE_DEV = "gateBar_dev";
    public final static String GATE_BAR_LOCK_FILE_RUN = "gateBar_run";
    public final static String E_BOARD_LOCK_FILE_DEV = "e_board_dev";
    public final static String E_BOARD_LOCK_FILE_RUN = "e_board_run";

    public final static String DEVELOP = "develop";
    public final static String RELEASE = "release";
    public final static String TESTRUN = "testrun";
    /**
     * Choose randomly the processing of stopped cars like a visiting car
     * where precessing include allow parking or disallow parking.
     */
    public final static String RANDOM_ATT = "random";
    
    public static void checkOptions(String args[]) {
        if (args.length > 0) {
            for (String argument : args) {
                String oneArg = argument.toLowerCase();

                if (oneArg.indexOf(DEBUG_OPTION) == 0) {
                    DEBUG_FLAG = true;
                } else if (oneArg.indexOf(VERSION_TYPE) == 0) {
                    String version = oneArg.substring(9);

                    if (version.equals(DEVELOP)) 
                        versionType = VersionType.DEVELOP;
                    else if (version.equals(TESTRUN)) 
                        versionType = VersionType.TESTRUN;
                    else if (version.equals(RELEASE)) 
                        versionType = VersionType.RELEASE;

                } else if (oneArg.indexOf(RANDOM_ATT) >= 0) {
                    RANDOM_FLAG = true;
                }
            }
        }   
    } 
    
    public static void setComponentSize(JComponent component, Dimension dim) 
    {
        component.setPreferredSize(dim);
        component.setMaximumSize(dim);
        component.setMinimumSize(dim);
        component.setSize(dim);
    }
    
    public static String getSizeString(JComponent component) {
        StringBuffer sb = new StringBuffer();
        sb.append("SW:" + component.getSize().width);
        sb.append(", SH:" + component.getSize().height);
        sb.append(", PW:" + component.getPreferredSize().width);
        sb.append(", PH:" + component.getPreferredSize().height);
        sb.append(", Iw:" + component.getMinimumSize().width);
        sb.append(", Ih:" + component.getMinimumSize().height);
        sb.append(", xw:" + component.getMaximumSize().width);
        sb.append(", xh:" + component.getMaximumSize().height);
        
        return sb.toString();
    }    
    
    public static String getRenderedContent(EBD_ContentType contentType, byte ID) {
        String practicalContent = null;
        
        switch (contentType) {
            case CURRENT_DATE:
                practicalContent = new SimpleDateFormat("yyyy-MM-dd(EEE)", locale).format(new Date());
                break;
                
            case CURRENT_TIME:
                practicalContent = new SimpleDateFormat("hh:mm a").format(new Date());
                break;
                
            case CURRENT_DATE_TIME:
                practicalContent = new SimpleDateFormat("yyyy-MM-dd(EEE) hh:mm a").format(new Date());
                break;
                
            case GATE_NAME:
                practicalContent = gateNames[ID];
                break;
                
            case REGISTRATION_STAT:
                practicalContent = "(Registration status: N/A)";
                break;
                
            case VEHICLE_TAG:
                practicalContent = "(Vehicle tag no.: N/A)";
                break;
                
            default :
                practicalContent = "unexpected content type: " + contentType;
                break;
        }
        
        return practicalContent;
    }
    
    /**
     * 
     * @param picDim        size of a given picture frame
     * @param originalImg   image to show on the frame
     * @param distortable   signifies that the image can be squashed to fit the frame
     * @return imageIcon that whose dimension changed as ordered
     */
    public static ImageIcon createStretchedIcon(Dimension picDim, BufferedImage originalImg, boolean distortable)
    {
        Image stretchedImg = null;  
        int frame_w = picDim.width;
        int frame_h = picDim.height;
        
        if (frame_h == 0 || frame_w == 0)
        {
            return null;
        }
        
        try {
            boolean imageWiderThanPictureFrame = false;
            
            imageWiderThanPictureFrame = originalImg.getWidth() /(float)originalImg.getHeight() 
                    > frame_w/(float)frame_h;
            if (distortable)
            {
                stretchedImg = originalImg.getScaledInstance(frame_w, frame_h, Image.SCALE_FAST);                    
            } else if (imageWiderThanPictureFrame) 
            {
                // for a landscape, preserve the whole width
                if ( (float)frame_w / originalImg.getWidth() > 0.95)
                {
                    stretchedImg = originalImg;
                }
                else 
                {
                    int height = originalImg.getHeight() * frame_w / originalImg.getWidth();
                    stretchedImg = originalImg.getScaledInstance(frame_w, height, Image.SCALE_FAST);      
                }
            } else {
                if ( (float)frame_h /originalImg.getHeight() > 0.95)
                {
                    stretchedImg = originalImg;
                }
                else
                {
                // for a portrait, preserve the whole height
                    int width = originalImg.getWidth() * frame_h / originalImg.getHeight();
                    stretchedImg = originalImg.getScaledInstance(width, frame_h, Image.SCALE_FAST);   
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(Stretching Car Image)");
        } finally {         
            return new javax.swing.ImageIcon(stretchedImg);
        }
    }          
    
    /**
     * Creates a dimension object for a car image size.
     * 
     * @return a Dimension object for a car (and its plate tag) image.
     */
    public static Dimension getPIC_DIM()
    {
        return new Dimension(PIC_WIDTH, PIC_HEIGHT);
    } 
    
    /**
     * Associate a text file for today to a event logger.
     * 
     * @param log an event logger to which a text file needs to be associated.
     * @param progType type of the program, usually ID of a device the program is representing
     * @return a text file relative pathname
     */    
    public synchronized static String associateTextFileToLogger(Logger log, int progType) {

        StringBuilder pathnameSB = new StringBuilder();
        StringBuilder daySB = new StringBuilder();
        
        String logType = null;
        if (log.getName().indexOf(exceptionLogName) == 0) {
            logType = exceptionLogName;
        } else {
            logType = operationLogName;
        }
        getPathAndDay(logType, pathnameSB, daySB);
        // pathname + File.separator
        // full path name of the today's text file for  logging
        String suffix = (progType == GENERAL_DEVICE ? "" : "_" + progType);
        String todayFilePath = pathnameSB.toString() + File.separator + daySB.toString() + suffix+ ".txt";
        
        if (log.getHandlers().length > 0) {
            String currentFile = ((LogFileHandler)log.getHandlers()[0]).getLogFilePath();
            if (currentFile.equals(todayFilePath))
                return todayFilePath;
            else {
                Handler handler = log.getHandlers()[0];
                handler.close();
                log.removeHandler(handler);                
            }
        }        
        
        try {
            FileHandler fh = new LogFileHandler(todayFilePath, true); // true : append mode
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
        } catch (SecurityException | IOException e) {
            logParkingException(Level.SEVERE, e, "log file association error");
            int response = JOptionPane.showConfirmDialog(null, "while associating text file to a logger");
            System.out.println("response");
        } finally {
            return todayFilePath;
        }
    }

    /**
     * Records(logs) an occurrence of an exceptional situation.
     * 
     * When the level of the exception were lower than the changeable level property of
     * the log for the exception, the logging request is discarded without being recorded.
     * <p>
     * When the level is higher than the threshold, the information conveyed with 
     * the exception object and an additionally coined information is stored in a daily 
     * text file(*.txt)
     * 
     * @param level the importance level of this exception. With the threshold level configured to
     *                  the log object(global variable 'exceptiolog'), it affects whether this exception
     *                  will be recorded(logged) or not.
     * @param se    exception object which contains information on the exceptional situation
     * @param extraInfo additional information presumably helpful to the program analyzers
     */
    public synchronized static String logParkingException(Level level, Exception se, String extraInfo, int progType) 
    {
        StringBuffer message = new StringBuffer("User provided status: " + extraInfo);
        String textLogFile ="";
        
        if (se != null) {
            message.append(System.getProperty("line.separator"));
            for (StackTraceElement ste: se.getStackTrace()) {
                message.append(ste.toString());
                message.append(System.getProperty("line.separator"));
            }
        }
        
        if (progType == GENERAL_DEVICE) {
            textLogFile = associateTextFileToLogger(exceptionLog, progType);
            exceptionLog.log(level, message.toString());
            // since this log is shared among multiple devices, better to close after using it.
            if (exceptionLog.getHandlers().length > 0) {
                Handler handler = exceptionLog.getHandlers()[0];
                handler.close();
                exceptionLog.removeHandler(handler);
            }
        } else {
            textLogFile = associateTextFileToLogger(exceptionLogDev, progType);
            exceptionLogDev.log(level, message.toString());
        }
        return textLogFile;
    }   
    
    public static String logParkingException(Level level, Exception se, String extraInfo) {
        return logParkingException(level, se, extraInfo, GENERAL_DEVICE);
    }
    
    /**
     * Records(logs) an occurrence of an exceptional situation.
     * 
     * When the level of the exception were lower than the changeable level property of
     * the log for the exception, the logging request is discarded without being recorded.
     * <p>
     * When the level is higher than the threshold, the information conveyed with 
     * the exception object and an additionally coined information is stored in a daily 
     * text file(*.txt)
     * 
     * @param level the importance level of this exception. With the threshold level configured to
     *                  the log object(global variable 'exceptiolog'), it affects whether this exception
     *                  will be recorded(logged) or not.
     * @param se    exception object which contains information on the exceptional situation
     * @param extraInfo additional information presumably helpful to the program analyzers
     */
    public static void logParkingExceptionStatus(
            Level level, Exception se, String extraInfo, JTextField criticalField, int progType)
    {
        String textLogFile = logParkingException(level, se, extraInfo, progType);  

        if (level == Level.SEVERE) {
            synchronized (criticalField) {
                criticalField.setText(timeFormat.format(new Date()) + "--Exception(" + extraInfo + ") Occurred, Content: "
                        + textLogFile);
            }
        }
    }
        
    /**
     * Writes System's normal operation in a text file for each day.
     * 
     * @param level importance level of the operation
     * @param operationInfo detailed information on the operation
     */
    public synchronized static void logParkingOperation(OpLogLevel level, String operationInfo) {
        logParkingOperation(level, operationInfo, GENERAL_DEVICE);
    }
    
    /**
     * Records(logs) the occurrence of an operation after checking the level supplied. 
     * 
     * The supplied level is compared with log instance level(log level).  
     * If the supplied level were log level then the logging request is discarded without 
     * being recorded. 
     * <p>
     * When the level is higher than the threshold, the information conveyed with 
     * the exception object and an additionally coined information is stored in a daily 
     * text file(*.txt)
     * 
     * @param level the importance level of this operation. With the threshold level configured to
     *                  the log object(global variable 'operationLog'), whether this operation
     *                  will be recorded(logged) or not is determined.
     * @param operationInfo additional information presumably helpful to the program analyzers
     */
    public synchronized static void logParkingOperation(OpLogLevel level, String operationInfo, int progType) {
        if (level.ordinal() > opLoggingIndex)
            return;
        else {
            associateTextFileToLogger(operationLog, progType);
            if (isFinalWishLog) {
                operationLog.setLevel(Level.INFO); // to make following logging operation always succeed.
            }

            operationLog.log(Level.INFO, operationLogName + ": " + operationInfo); 

            if (isFinalWishLog) {
                operationLog.setLevel(Level.OFF);
                isFinalWishLog = false;
            }
        }
    }
    
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    /**
     * Converts a short value(combo box item index) into a logging Level
     * 
     * @param opLoggingIndex a short value to convert 
     * @return Logger Level instance  for the given short index value
     */
    public static Level index2Level(short opLoggingIndex) {
        
        switch (opLoggingIndex) {
            case 0:
                return Level.INFO; 
            case 1:
                return Level.CONFIG;
            case 2:
                return Level.FINE;
            default:
                return Level.OFF;
        }        
    }
    
    /**
     * 
     * @param insertButton
     * @param listTable
     * @return 
     */
    public static boolean removeEmptyRow(JButton insertButton, JTable listTable) {
        int rowIndex = listTable.getRowCount() - 1;
        Object content = listTable.getModel().getValueAt(rowIndex, 1);        
        
        /**
         * Check if the last row is empty, if yes, remove it.
         */
        if (content == null ||
                content.getClass() == String.class && ((String) content).trim().length() == 0)
        {
            insertButton.setEnabled(true);
            DefaultTableModel model = (DefaultTableModel)listTable.getModel();
            model.setRowCount(listTable.getRowCount() - 1); 
            return true;
        } else {
            return false;
        }
    }    
    
    public static boolean emptyLastRowPossible(JButton insertButton, JTable listTable)
    {
        if (!insertButton.isEnabled() && 
                listTable.getSelectedRow() != listTable.getRowCount() - 1) {
            return true;
        }
        else
        {
            return false;
        }
    }   
    
    /**
     * Position a cell at the center of the visible area of a JTable.
     * @param table table that has the visible area
     * @param rowIndex row of the cell to put at the center
     * @param vColIndex column of the cell to put at the center
     */
    public static void scrollToCenter(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
          return;
        }
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Rectangle viewRect = viewport.getViewRect();
        
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

        int centerX = (viewRect.width - rect.width) / 2;
        int centerY = (viewRect.height - rect.height) / 2;
        if (rect.x < centerX) {
          centerX = -centerX;
        }
        if (rect.y < centerY) {
          centerY = -centerY;
        }
        rect.translate(centerX, centerY);
        viewport.scrollRectToVisible(rect);
    }    
    
    public static int followAndGetTrueIndex(JTable theTable) {
        theTable.scrollRectToVisible(new Rectangle(
                theTable.getCellRect(theTable.getSelectedRow(), 0, true)));
        if (theTable.getSelectedRow() == -1)
            return -1;
        else
            return theTable.convertRowIndexToModel(theTable.getSelectedRow());                  
    }    
    
    /**
     * Sets the width of a column in a JTable
     * 
     * @param col a JTable column for which the width is set
     * @param min minimum width of the column to set
     * @param pref preferred width of the column to set
     * @param max maximum width of the column to set
     */
    public static void SetAColumnWidth(TableColumn col, int min, int pref, int max)
    {
        col.setMinWidth(min);
        col.setPreferredWidth(pref);
        col.setMaxWidth(max);
    }  
    
    /**
     * From a given date, calculates a date that differs (in day unit) from the give date by some 
     * amount. In other words, calculate the date that is n days away from a given date.
     * For example, when it is given '2014-09-29' and 7, it returns '2014-10-06'.
     * 
     * @param givenDate starting date to calculate the result date
     * @param days the number of days by which the result date differs from 'givenDate'
     * @return the date that is 'days' days away from the 'givenDate'
     */
    public static Date getDateFromGivenDate(Date givenDate, int days) {
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(givenDate);
        gCal.add(Calendar.DAY_OF_MONTH, days);
        return gCal.getTime();
    }

    /**
     * Calculates an appropriate location for the help dialog to display. The location is determined
     * considering the location of the help button and the location of the frame that has the button
     * on. Finally, the help dialog will be placed on the right of the button, slightly above the 
     * button on the whole monitor.
     * 
     * @param thisFrame the frame on which the button is located
     * @param helpDialog the help dialog to display on the screen
     * @param helpButton the help button pressed for the help dialog
     */
    public static void locateAndShowHelpDialog(JFrame thisFrame, JDialog helpDialog, JButton helpButton) {
        Point buttonPoint = new Point();
        helpButton.getLocation(buttonPoint);
        
        Point framePoint = new Point();
        thisFrame.getLocation(framePoint);
        
        Point topLeft = new Point(framePoint.x + buttonPoint.x + 70, framePoint.y + buttonPoint.y - 60);
        helpDialog.setLocation(topLeft);
        helpDialog.setVisible(true);        
    }
    
    public static Point getTopLeftPointToPutThisFrameAtScreenCenter(JFrame thisFrame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int xPoint = dim.width/2 - thisFrame.getSize().width/2;
        int yPoint = dim.height/2 - thisFrame.getSize().height/2;
        
        return new Point(xPoint, yPoint);
    }
    
    public static Comparator<Long> comparator = new Comparator<Long>() {
        public int compare (Long rownum1, Long rownum2) {
            if ( rownum1 > rownum2) {
                return 1;
            } else if ( rownum1 < rownum2) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    
    public static DefaultTableModel buildTableModel(ResultSet rs)
        throws SQLException 
    {

        ResultSetMetaData metaData = rs.getMetaData();

        // Get suggested column heading label of each column
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnLabel(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
    
    public static int insertBuilding(int bldgNo) {
        int result = ER_YES;
        Connection conn = null;
        PreparedStatement createBuilding = null;

        try {
            conn = JDBCMySQL.getConnection();
            String sql = "Insert Into Building_Table(BLDG_NO) Values (?)";
            createBuilding = conn.prepareStatement(sql);
            createBuilding.setInt(1, bldgNo);
            if (createBuilding.executeUpdate() == 1) {
                result = ER_NO;
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                result = ER_DUP_ENTRY;
                logParkingException(Level.SEVERE, ex, bldgNo + " already existing building");
            } else {
                logParkingException(Level.SEVERE, ex, "(Insertion failed building : " + bldgNo);
            }            
        } finally {
            closeDBstuff(conn, createBuilding, null, "(inserted building: " + bldgNo + ")");
            return result;
        }
    }
    
    public static void closeInputStream(InputStream inStream, String statusMsg) {
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException ex) {
                logParkingException(Level.SEVERE, ex, statusMsg);
            }
        }        
    }
    
    public static void closeOutputStream(OutputStream outStream, String statusMsg) {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException ex) {
                logParkingException(Level.SEVERE, ex, statusMsg);
            }
        }        
    }
    
    public static void closeSocket(Socket server, String statusMsg) {
        if (server != null) {
            try {
                server.close();
                server = null;
            } catch (IOException ex) {
                logParkingException(Level.SEVERE, ex, statusMsg);
            }
        }
    }    
    
    public static void closeDBstuff(Connection conn, Statement stmt, ResultSet rs, String statusMsg) {
        try {
            if (conn != null)
                conn.close();
            
            if (stmt != null)
                stmt.close();
            
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, statusMsg);
        }        
    }     
    
    public static int insertUnit(int unitNo, int bldgSeqNo) {   
        int result = ER_YES;
        Connection conn = null; 
        PreparedStatement insertUnit = null;

        try {
            // <editor-fold defaultstate="collapsed" desc="-- Create a room unit"> 
            String sql = "Insert Into BUILDING_UNIT(UNIT_NO, BLDG_SEQ_NO) Values (?, ?)";
            conn = JDBCMySQL.getConnection();        
            insertUnit = conn.prepareStatement(sql);
            insertUnit.setInt(1, unitNo);
            insertUnit.setInt(2, bldgSeqNo);
            if (insertUnit.executeUpdate() == 1) {
                result = ER_NO;
            }
            //</editor-fold>
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                result = ER_DUP_ENTRY;
                logParkingException(Level.SEVERE, ex, unitNo + " already existing unit number");
            } else {
                logParkingException(Level.SEVERE, ex, "(Insertion failed unit : " + unitNo);
            }             
        } finally {
            closeDBstuff(conn, insertUnit, null, "insert unit " + unitNo);
            return result;    
        }
    }     
    
    public static int insertLevel1Affiliation(String level1Name) {
        int result = ER_YES;
        Connection conn = null;
        PreparedStatement createLevel1 = null;

        try {
            String sql = "Insert Into L1_Affiliation(PARTY_NAME) Values (?)";
            conn = JDBCMySQL.getConnection();
            createLevel1 = conn.prepareStatement(sql);
            createLevel1.setString(1, level1Name);
            result = createLevel1.executeUpdate();
            if (result == 1) {
                result = ER_NO;
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                result = ER_DUP_ENTRY;
                logParkingException(Level.SEVERE, ex, level1Name + " already existing level1");
            } else {
                logParkingException(Level.SEVERE, ex, "(Failed insertion trial of '" + level1Name + "'");
            }
        } finally {
            closeDBstuff(conn, createLevel1, null, "insert " + level1Name);
            return result;
        }
    }
    
    public static int insertLevel2Affiliation(Integer L1_No, String level2Name) {   
        int result = ER_YES;
        Connection conn = null;
        PreparedStatement insertL2name = null;

        try {
            // <editor-fold defaultstate="collapsed" desc="-- Create a lower level affiliation name"> 
            String sql = "Insert Into L2_Affiliation(L1_NO, PARTY_NAME) Values (?, ?)";
            conn = JDBCMySQL.getConnection();
            insertL2name = conn.prepareStatement(sql);
            insertL2name.setInt(1, L1_No);
            insertL2name.setString(2, level2Name);
            result = insertL2name.executeUpdate();
            if (result == 1) {
                result = ER_NO;
            }
            //</editor-fold>
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                result = ER_DUP_ENTRY;
                logParkingException(Level.SEVERE, ex, level2Name + " already exist as level 2");
            } else {
                logParkingException(Level.SEVERE, ex, "(Failed insertion trial of '" + level2Name + "'");
            }            
        } finally {
            closeDBstuff(conn, insertL2name, null, "insert L1-no: " + L1_No + ", L2-name: " + level2Name);
            return result;    
        }
    }     

    public static void highlightTableRow(JTable table, int rowIndex) {
//        table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        table.changeSelection(rowIndex, 1, false, false);
        scrollToCenter(table, rowIndex, 1);
        
//        table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 1, true)));
//        table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 2, true)));
    }   
    
    public static void rejectEmptyInput(JTable thisTable, int rowIndex, String msg) {
        if (thisTable.editCellAt(rowIndex, 1))
        {
            thisTable.getEditorComponent().requestFocus();  
        }                  
        showMessageDialog(null, msg, "Input Date Error", JOptionPane.INFORMATION_MESSAGE);            
    }
    
    public static int getNumericDigitCount(String cellPhone) {
        int numCount = 0;
        
        if (cellPhone == null || cellPhone.length() == 0)
        {
            return 0;
        }
        for (char aChar : cellPhone.toCharArray()) {
            if (aChar >= '0' && aChar <= '9') {
                numCount++;
            }
        }
        return numCount;
    }    
    
    public static void rejectUserInput(JTable thisTable, int rowIndex, String tableName) {
        if (thisTable.editCellAt(rowIndex, 1))
        {
            thisTable.getEditorComponent().requestFocus();  
        }        
        showMessageDialog(null, "Same data exists in '" + tableName + "'",
                "Duplicate Input Error", JOptionPane.WARNING_MESSAGE);     
    }  

    public static int getUserDecisionOnSaving(int lineNo, String name) {
        Object[] options = {"Save", "Cancel"};

        return JOptionPane.showOptionDialog(null, 
                "Want to save modified driver information?" 
                        + System.getProperty("line.separator") + " - Order: " + lineNo  
                        + System.getProperty("line.separator") + " - Name: " + name, 
                "User Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                options, options[0]);    
    }      

    public static void attachCondition(StringBuffer cond, String column, String content) {
        if (content.length() > 0) {
            if (cond.length() > 0)
                cond.append(" and ");
            cond.append(column + " like '%" + content + "%' ");
        }            
    }      

    public static void attachConditionT3(StringBuffer cond, String column, String content) {
        if (content.length() > 0) {
            if (cond.length() > 0)
                cond.append(" and ");
            cond.append(CommonData.CA_ROW_VAR + "." + column + " like '%" + content + "%' ");
        }            
    }      
    
    public static void attachIntCondition(StringBuffer cond, String column, Integer value) {
        if (value != -1) {
            if (cond.length() > 0)
                cond.append(" and ");
            cond.append(column + " = " + value);
        }
    }      

    public static void attachNumberCondition(StringBuffer cond, 
            String highCol, String lowCol, int highKey, int lowKey)
    {
        if (highKey != PROMPTER_KEY) {
            if (lowKey == PROMPTER_KEY)
            {
                if (cond.length() > 0) {
                    cond.append(" and ");
                }
                cond.append(highCol + " = " + highKey); 
            }
            else
            {
                if (cond.length() > 0) {
                    cond.append(" and ");
                }
                // putting high key value condition is redundant and causes inefficiency
                cond.append(lowCol + " = " + lowKey); 
            }
        } else {
            if (lowKey != PROMPTER_KEY) {
                cond.append(lowCol + " = " + lowKey); 
            }
        }
    }    
    
    public static void attachIntConditionT3(StringBuffer cond, String column, Integer value) {
        if (value != -1) {
            if (cond.length() > 0)
                cond.append(" and ");
            cond.append(CommonData.CA_ROW_VAR + "." + column + " = " + value);
        }
    }      
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }    
      
    /**
     * Make a pretty printed file size string. It uses storage size unit suffixes appropriately.
     * This routine supplies 2 digit precision after the decimal point.
     * @param filesize
     * @return 
     */
    public static String getFilesizeStr(int filesize) {
        String result = new String();
        String suffix = "";
        double mantisa = 0;
        if ( filesize > 1024) { 
            if (filesize > 1024 * 1024) {
                if (filesize > 1024 * 1024 * 1024) {
                        mantisa = (double)filesize / (1024 * 1024 * 1024);
                        suffix = "GB";
                } else { // less than a giga-byte
                        mantisa = (double)filesize / (1024 * 1024);
                        suffix = "MB";
                } 
            } else { // less than a mega-byte
                    mantisa = (double)filesize / 1024;
                    suffix = "KB";
            }
        } else { // less than a kilo-byte
            mantisa = filesize;
        }
        DecimalFormat szForm = new DecimalFormat("###.##");
        String str =  szForm.format(mantisa);
        result = szForm.format(mantisa).substring(0, 
                        (str.length() >= 4) ?
                                        (str.charAt(3) == '.' ? 3 : 4) // drop last dot('.') [eg. 224.]
                                        : str.length()  );
        result = result + suffix;	// 224KB

        return result;
    }

    public static void appendLine(JTextArea msgArea, String line) {
        synchronized (msgArea) 
        {
            msgArea.append(timeFormat.format(new Date()) + "--" + line);
            msgArea.append(System.getProperty("line.separator"));
            limitTextAreaLines(msgArea);
            int len = msgArea.getDocument().getLength();
            msgArea.setCaretPosition(len);
        }
    }  
    
    public static void limitTextAreaLines(JTextArea textArea)
    {
        int lines = textArea.getLineCount();
        if (lines > maxMessageLines) {
            int linesToRemove = lines - maxMessageLines -1;
            int lengthToRemove = 0;
            try {
                lengthToRemove = textArea.getLineStartOffset(linesToRemove);
            } catch (BadLocationException ble) {
                logParkingException(Level.SEVERE, ble, "(Wrong message line location)");
            }
            textArea.replaceRange("", 0, lengthToRemove);
        }  
    }

    public static final Object MUTEX_DEBUG_SEQ_VALUE = new Object();
    public static boolean isConnected(Socket socket) {
//        logParkingException(Level.INFO, null, "P a", 1);
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
//            logParkingException(Level.INFO, null, "P b", 1);
            return false;
        } else {
//            logParkingException(Level.INFO, null, "P c", 1);
            return true;
        }
    }
    
    /**
     * Check if some camera needs to be connected for the manager to be fully functional.
     * @param deviceSockets array of device Socket object
     * @return true, if some device were not connected yet; false, otherwise
     */
    public static boolean someDeviceDisconnected(Socket[] deviceSockets) {
        
        for (int idx = 1; idx < deviceSockets.length; idx++) {
            Socket s = deviceSockets[idx];
            if (s == null || s.isClosed() || !s.isConnected()) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     * Determines if any device of some type is not connected yet.
     * @param managerArr array of device manager references
     * @return true when at least one device isn't connected, false otherwise
     */
    public static boolean someDeviceDisconnected(Manager[] managerArr) {
        
        for (int idx = 1; idx <= gateCount; idx++) {
            if (managerArr[idx] == null) 
                return true;
            else {
                Socket sock= managerArr[idx].getSocket();
                if (sock == null || sock.isClosed() || !sock.isConnected())
                    return true;
            }
        }
        return false;
    }        

    /**
     * Returns whether an artificial error occurred or not.
     * 
     * @param errorCBox one determining factor for the artificial error
     * @return true when a random number exceeds a error threshold, false otherwise.
     */
    public static boolean noArtificialErrorInserted(JCheckBox errorCBox) {
        boolean result = false; // error occurred
        
        if (errorCBox.isSelected() ) {
            Random r = new Random();
            float chance = r.nextFloat();
            
            if (chance > ERROR_RATE) {
                result = true; // no error occurred
            }
        } else {
            result = true;
        }
        return result;
    }    
    
    public static boolean registeredDevice(DeviceType devType, String devIP, byte devID) {
        
        if (devID > gateCount)
            return false;
        
        // use registered device IP and ID infomation arrays
        // cameraIP, gateBarIP, e_BoardIP
        
        if (deviceIP[devType.ordinal()][devID].equals(devIP))
            return true;
        else
            return false;
    }
    

    /**
     * returns a formatted string that represent a number.
     * @param realNumber any number like integer, float, double, etc
     * @param precision number of digits after decimal point
     * @return 
     */
    public static String getPercentString(double realNumber) {
        double percentNumber = realNumber * 100;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        return df.format(percentNumber) + "%"; 
    }    
    
    public static String getFormattedRealNumber(double realNumber, int precision) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(precision);
        df.setMinimumFractionDigits(precision);
        return df.format(realNumber); 
    }      

    /**
     * Depending on the log type passed, calculates folder(directory) path name and date string.
     * (e.g.) for the day of Oct. 16 of 2015, it computes "log\operation\2015\10" and "16".
     * @param logType either "exception" or "operation"
     * @param pathname a call by reference paramenter, path of the folder which will store text log file. 
     * @param dayStr two digit date string which will be used as a part of the text log file.
     */
    public static void getPathAndDay(String logType, StringBuilder pathname, StringBuilder dayStr) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);        

        // directory(=folder) name of the location in which the text file will be stored
        pathname.append("log" + File.separator);
        pathname.append(logType); // either 'exception' or 'operation' is appended
        pathname.append(File.separator + year + File.separator);
        pathname.append(String.format("%02d", month + 1));
        
        File fPath = new File(pathname.toString());
        fPath.mkdirs(); // make sure the parent directory exists for the log file        

        dayStr.append(String.format("%02d", day));
    }
    
   /**
     * Determine the ID of camera being created considering the IDs of cameras already generated.
     * @return a proper ID number for a camera
     */
    public static byte getUniqueGateBarID(DeviceType deviceType) {
        byte currID = 1; // minimum ID number
        
        while (true) {
            String lockFileName = null;
            
            switch (deviceType) {
                case E_Board: 
                    lockFileName = "No" + currID + (Globals.versionType == VersionType.DEVELOP ? 
                            Globals.E_BOARD_LOCK_FILE_DEV : Globals.E_BOARD_LOCK_FILE_RUN);                    
                    break;
                    
                case Camera:
                    break;
                    
                case GateBar: 
                    lockFileName = "No" + currID + (Globals.versionType == VersionType.DEVELOP ? 
                            Globals.GATE_BAR_LOCK_FILE_DEV : Globals.GATE_BAR_LOCK_FILE_RUN);
                    break;
                    
                default:
                    break;
            }
            JustOneLock ua = new JustOneLock(lockFileName);
            if (!ua.isAppActive()) // a camera with this ID is not running
                return currID;
            else
                currID++; // consider next ID value
        }
    }  
    
    public synchronized static void addMessageLine(JTextArea messageArea, String message) {
        
        String messageLine
                = timeFormat.format(new Date()) + "--" + message + System.getProperty("line.separator");
        
        messageArea.append(messageLine);
        
        if (messageArea.getDocument().getLength()
                < messageArea.getCaretPosition() + messageLine.length() + 100) 
        {
            limitLines(messageArea);
            int len = messageArea.getDocument().getLength();
            messageArea.setCaretPosition(len); // places the caret at the bottom of the display area                    
        }
        messageArea.getCaret().setVisible(true);
    }    
    
    public static void limitLines (JTextArea textArea) {
        int lines = textArea.getLineCount();
        if (lines > maxMessageLines) {
            int linesToRemove = lines - maxMessageLines -1;
            int lengthToRemove = 0;
            try {
                lengthToRemove = textArea.getLineStartOffset(linesToRemove);
            } catch (BadLocationException ble) {
                logParkingException(Level.SEVERE, ble, "(Wrong message line location)");
            }            
            textArea.replaceRange("", 0, lengthToRemove);
        }
    }    

    public static int getPort(DeviceType devType, byte deviceID, VersionType versionType) {
        int portNo = 0;
        
        switch (devType) {
            case Camera: 
                portNo = (versionType == VersionType.DEVELOP ? 
                        Globals.CAMERA_PORT_DEV : Globals.CAMERA_PORT_RUN);
                break;
                
            case E_Board: 
                portNo = (versionType == VersionType.DEVELOP ? 
                        Globals.E_BOARD_PORT_DEV : Globals.E_BOARD_PORT_RUN);
                break;
                
            case GateBar: 
                portNo = (versionType == VersionType.DEVELOP ? 
                        Globals.GATE_BAR_PORT_DEV : Globals.GATE_BAR_PORT_RUN);
                break;
        }
        return portNo;
    }
    
    public static void testUniqueness(String fileName, String readableName) {
        
        JustOneLock ua = null;
        
        if (versionType == VersionType.DEVELOP) 
            ua = new JustOneLock(fileName + "Dev");
        else
            ua = new JustOneLock(fileName + "Run");

        if (ua.isAppActive()) {
            JOptionPane.showConfirmDialog(null, "one copy of '" + readableName + "' program is " + 
                    System.getProperty("line.separator") +  "already running!",
                    "Duplicate Execution Error", JOptionPane.PLAIN_MESSAGE, 
                    WARNING_MESSAGE);              
            
            System.exit(1);
        }       
    }
    
    /**
     * closes socket connection to a device.
     * 
     * before closing the socket, it cancels any existing relevant tasks.
     */
    public static void gfinishConnection(DeviceType devType, 
            Exception e, String description, byte gateNo,
            Object sockMutex, Socket socket, javax.swing.JTextArea textArea,
            SocketConnStat connStat, ParkingTimer connTimer, boolean beingShutdown) {

        synchronized(sockMutex) 
        {
            if (isConnected(socket))
            {
                String msg =  devType.getContent() + " " + "#" + gateNo;

                addMessageLine(textArea, "  ------" + msg + " " + DISCONN_MSG.getContent() );
                logParkingException(Level.INFO, e, description + " " + msg);

                long closeTm = System.currentTimeMillis();

                connStat.recordSocketDisconnection(closeTm);

                closeSocket(socket, "while gate bar socket closing");
                socket = null;
            }                
        } 
        
        if (connTimer != null) {
            if (!beingShutdown) {
                connTimer.reRunOnce();
                addMessageLine(textArea, CONN_TRIAL.getContent() + "#" 
                        + gateNo + " " + devType.getContent());
            }
        }        
    }
    
    public static String getModifiedFilename(Date arrivalTime, String tagRecognized) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_");    
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");   
        String tmStr = timeFormat.format(arrivalTime);
        return dateFormat.format(arrivalTime) + tmStr + "_" + tagRecognized + ".jpg";        
    }
    
    /**
     * load item list for a comboBox given
     * @param comboBox box for which item list is created
     * @param column number that identifies comboBox type
     * @param keyValue key of the higher level comboBox item, in case the comboBox is 
     * lower in level. It will be used to select the lower level items from the DB table. 
     * keyValue of PROMPTER_KEY(=0) means either the comboBox is not a lower one or 
     * the higher combobox item is not selected.
     */
    public static void loadComboBoxItems(JComboBox comboBox, OSP_enums.DriverCol column, int keyValue) 
    {
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;      
        String excepMsg = "(CBox Item Loading for : " + column + "column";

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            
            //<editor-fold defaultstate="collapsed" desc="-- make SQL statement">            
            switch (column) {
                case AffiliationL1: 
                    sb.append("select * from L1_affiliation order by PARTY_NAME");
                    break;
                    
                case BuildingNo: 
                    sb.append("select * from building_table order by bldg_no");
                    break;
                    
                case AffiliationL2: 
                    if (keyValue == PROMPTER_KEY) {
                        // Korea(ROK) Patent Registered(No: 1016595460000) SQL select statement
                        sb.append("SELECT L2.L2_NO, L2.PARTY_NAME AS L2_NAME, "
                                + "L1.L1_NO, L1.PARTY_NAME AS L1_NAME ");
                        sb.append("FROM L2_affiliation L2, L1_affiliation L1 "); 
                        sb.append("WHERE L2.L1_NO = L1.L1_NO "); 
                        sb.append("ORDER BY L2.PARTY_NAME, L1.PARTY_NAME"); 
                    } else {
                        sb.append("Select party_name as L2_NAME, L2_NO From L2_affiliation ");
                        sb.append("Where L1_NO = ").append(keyValue);
                        sb.append(" Order by PARTY_NAME");
                    }
                    break;
                    
                case UnitNo: 
                    if (keyValue == PROMPTER_KEY) {
                        // Korea(ROK) Patent Registered(No: 1016595460000) SQL select statement                        
                        sb.append("SELECT U.SEQ_NO AS UNIT_SN, U.UNIT_NO, ");
                        sb.append("  T.SEQ_NO BLDG_SN, T.BLDG_NO ");
                        sb.append("FROM building_unit U, building_table T ");
                        sb.append("WHERE U.BLDG_SEQ_NO = T.SEQ_NO ");
                        sb.append("ORDER BY U.UNIT_NO, T.BLDG_NO; ");
                    } else {
                        sb.append("SELECT UNIT_NO, SEQ_NO as UNIT_SN FROM building_UNIT ");
                        sb.append("WHERE BLDG_SEQ_NO = ").append(keyValue);
                        sb.append(" Order by UNIT_NO");                    
                    }
                    break;
                    
                default:
                    break;
            }
            //</editor-fold>
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                //<editor-fold defaultstate="collapsed" desc="-- Add combobox items one by one">                            
                Object item = null;
                switch (column) {
                    case AffiliationL1: 
                        item = new ConvComboBoxItem(new Integer(rs.getInt("L1_NO")), rs.getString("PARTY_NAME"));
                        break;
                    case AffiliationL2: 
                        if (keyValue == PROMPTER_KEY) {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("L2_NO"), rs.getInt("L1_NO")},
                                    new String[] {rs.getString("L2_NAME"), rs.getString("L1_NAME")});
                        } else {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("L2_NO")}, new String[] {rs.getString("L2_NAME")});
                        }
                        break;
                    case BuildingNo: 
                        item = new ConvComboBoxItem(new Integer(rs.getInt("SEQ_NO")), rs.getString("BLDG_NO"));
                        break;
                    case UnitNo: 
                        if (keyValue == PROMPTER_KEY) {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("UNIT_SN"), rs.getInt("BLDG_SN")},
                                    new String[] {rs.getString("UNIT_NO"), rs.getString("BLDG_NO")});   
                        } else {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("UNIT_SN")}, new String[] {rs.getString("UNIT_NO")});
                        }                             
                        break;
                    default:
                        break;
                }                
                comboBox.addItem(item);
            //</editor-fold>                
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }        
    }    
    
    /**
     * Update(=refresh) items listing for a combo box.
     * 
     * @param comboBox the combo box to refresh
     * @param prompter the prompter(=first placeholder) item of the combo box
     * @param column table column index of the combo box
     * @param parentkey the key value of the parent combo box item for which 
     *                     this combo box item listing is to be formed.
     */
    public static void refreshComboBox(JComboBox comboBox, 
            Object prompter, OSP_enums.DriverCol column, int parentkey,
            int[] getPrevListParentKey)
    {
        comboBox.removeAllItems();
        comboBox.addItem(prompter);
        loadComboBoxItems(comboBox, column, parentkey);
        if (column == AffiliationL2 || column == UnitNo) {
            getPrevListParentKey[column.getNumVal()] = parentkey;
        }
    }

    public static void mayChangeLowerCBoxPrompt(JComboBox parentCBox, JComboBox childCBox, 
            OSP_enums.DriverCol childCol, int[] prevItemParentKey) {
        /**
         * Check if parent combo box is opened by the user 
         * -- Korea(ROK) Patent Registered(No: 1016595460000) technology
         */
        int currKey = (Integer)(((ConvComboBoxItem)parentCBox.getSelectedItem()).getKeyValue()); 
        
        if (prevItemParentKey[childCol.getNumVal()] != currKey) {
            prevItemParentKey[childCol.getNumVal()] = currKey;        
            // Change the just prompter of the child combobox and select the prompter.
            childCBox.removeAllItems();
            childCBox.addItem(new InnoComboBoxItem(
                    new int[]{PROMPTER_KEY}, 
                    new String[]{getPrompter(childCol, parentCBox).toString()}));
            childCBox.setSelectedIndex(0);
        }    
    }    
    
    /**
     * Finds appropriate prompter for a combo box
     * -- Korea(ROK) Patent Registered(No: 1016595460000) Technology Implementing method.
     * 
     * @param column table ComboBox column for which the prompter is sought for
     * @param parentObj 1st level affiliation ComboBox, should be not null when 
     *                      column is of the 2nd level affiliation
     * @return 
     */
    public static Object getPrompter(OSP_enums.DriverCol column, Object parentObj) {
        boolean complexItem = false;

        if (column == AffiliationL2 || column == UnitNo) {
            if (parentObj == null)
                complexItem = true;
            else {
                if (parentObj.getClass().equals(PComboBox.class)) {
                    if (((PComboBox)parentObj).getSelectedIndex() <= 0)
                        complexItem = true;
                }
                else {
                    if (parentObj.getClass() != InnoComboBoxItem.class) {
                        int parentIndex =   (Integer) (((ConvComboBoxItem)parentObj).getKeyValue());
                        if (parentIndex == PROMPTER_KEY)
                            complexItem = true;
                    }
                }
            }
        }
        
        String label = null;
        switch (column) {
            case AffiliationL1: 
                label = HIGHER_CB_ITEM.getContent();
                break;
            case BuildingNo: 
                label  = BUILDING_CB_ITEM.getContent();
                break;
            case AffiliationL2:  
                label = complexItem ? LOWER_HIGHER_CB_ITEM.getContent() 
                        : LOWER_CB_ITEM.getContent();
                break;
            case UnitNo: 
                label = complexItem ? ROOM_BUILDING_CB_ITEM.getContent() 
                        : ROOM_CB_ITEM.getContent();
                break;
            default:
                break;
        }
        if (column == AffiliationL1 || column == BuildingNo) {
            return new ConvComboBoxItem(new Integer(PROMPTER_KEY), label);
        } else {
            return new InnoComboBoxItem(new int [] {PROMPTER_KEY}, new String[] {label});
        }
    }
    
    
    static int count = 0;
    
    public static Locale language;
}
