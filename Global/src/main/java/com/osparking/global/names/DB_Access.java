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
package com.osparking.global.names;

import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.ADMIN_ID;
import static com.osparking.global.CommonData.statCountArr;
import com.osparking.global.Globals;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Level;
import static com.osparking.global.Globals.*;
import static com.osparking.global.Globals.sdf;
import static com.osparking.global.names.ControlEnums.DialogMessages.NAME_ADMIN;
import static com.osparking.global.names.ControlEnums.DialogMessages.NAME_GUEST;
import static com.osparking.global.names.ControlEnums.DialogMessages.NAME_MANAGER;
import static com.osparking.global.names.ControlEnums.DialogMessages.WELCOMING_OS;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DEFAULT_USER_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.DEFAULT_USER_LINE1;
import static com.osparking.global.names.ControlEnums.LabelContent.DEFAULT_USER_LINE2;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_LABEL;
import static com.osparking.global.names.ControlEnums.Languages.ENGLISH;
import static com.osparking.global.names.ControlEnums.Languages.KOREAN;
import static com.osparking.global.names.ControlEnums.MsgContent.OS_PARKINGLOT;
import com.osparking.global.names.ControlEnums.OsPaTable;
import static com.osparking.global.names.ControlEnums.TextType.LETEST_MSG;
import static com.osparking.global.names.ControlEnums.TextType.PASSING_MSG;
import static com.osparking.global.names.ControlEnums.TextType.SECOND_MSG;
import static com.osparking.global.names.JDBCMySQL.OSP_PHONE;
import static com.osparking.global.names.JDBCMySQL.PASSWORD;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.OSP_enums.EBD_Colors;
import com.osparking.global.names.OSP_enums.EBD_ContentType;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.*;
import com.osparking.global.names.OSP_enums.EBD_CycleType;
import com.osparking.global.names.OSP_enums.EBD_DisplayUsage;
import com.osparking.global.names.OSP_enums.EBD_Effects;
import static com.osparking.global.names.OSP_enums.EBD_Effects.BLINKING;
import static com.osparking.global.names.OSP_enums.EBD_Effects.LTOR_FLOW;
import static com.osparking.global.names.OSP_enums.EBD_Effects.RTOL_FLOW;
import com.osparking.global.names.OSP_enums.EBD_Fonts;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.OSP_enums.PWStrengthLevel;
import com.osparking.global.names.OSP_enums.PermissionType;
import java.awt.Component;
import java.util.Date;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DB_Access {

    /**
     * System settings variable.
     * <p>
     * a flag that shows if major operation performance evaluation is needed or not.
     */
    public static String parkingLotName = "";
    public static boolean storePassingDelay = false;
    
    /**
     * System settings variable.
     * <p>
     * an integral(short) value that contains the degree how complex the user password 
     * should be
     */
    public static short pwStrengthLevel = (short) PWStrengthLevel.FourDigit.ordinal();
        
    /**
     * System settings variable.
     * <p>
     * a short value that stores a normal operation logging level combo box index.
     */    
    public static short opLoggingIndex = (short) OpLogLevel.LogAlways.ordinal();
        
    /**
     * Stores information in which country this program is running and what language it's using.
     * That is country code and the language code.
     */
    public static Locale locale = null;
    
    /**
     * System settings variable.
     * <p>
     * an index of the selected item of the locale combo box on the ConfigureSettings form
     */       
    public static short localeIndex = -1;
    
    /**
     * EBDsettingsChange evaluation data: count of data to accumulate for a statistical analysis.
     * (e.g., 20 : 20 execution times are to be added to get the average of it.)
     */
    public static int statCount = 0;
    public static int statCountIndex = 0;
    
    public static int[] passingCountCurrent;
    
    public static int[] passingDelayCurrentTotalMs;
    
    public static int maxMessageLines = 2000;
    
    public static int maxMaintainDate = 90;

    public static int PIC_WIDTH = 752;

    public static int PIC_HEIGHT = 480;
    
    /**
     * Radio button choice DB stored value in the Car arrivals record search window's Duration Panel.
     */
    public static int  SEARCH_PERIOD = -1;

    public static int  EBD_flowCycle = 0;
    
    public static int  EBD_blinkCycle = 0;
    
    public static short gateCount = 1;
    
    public static String[] gateNames = null;    
    
    public static String[][] deviceIP = null;
    
    public static byte [][] deviceType = null;    
    
    public static String[][] devicePort = null;
    public static byte [][] connectionType = null;  
    
    public static String[][] deviceComID = null;

    public static void initEBoardSettings() {
        
        setEBoardRow(1, WELCOMING_OS.getContent() , VERBATIM.ordinal(), 2, 2, 1);
        setEBoardRow(2, "", CURRENT_DATE.ordinal(), 3, 3, 1);
        setEBoardRow(3, "", VEHICLE_TAG.ordinal(), 3, 1, 0);
        setEBoardRow(4, "", REGISTRATION_STAT.ordinal(), 0, 0, 1);        
    }
    
    public static void initSystemSettings() {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            String sql = "Update settingstable " +
                    "Set Lot_Name = '" + OS_PARKINGLOT + "', perfEvalNeeded = 1, " +
                    "PWStrengthLevel = 0, OptnLoggingLevel = 0, " +
                    "languageCode = 'ko', countryCode = 'KR', " +
                    "localeIndex = 110, statCount = 1, " +
                    "MaxMessageLines = 300, GateCount = 2, " +
                    "PictureWidth = 1280, PictureHeight = 960, " +
                    "SearchPeriod = 2, MAX_MAINTAIN_DATE = 30, " +
                    "EBD_flow_cycle= 8000, EBD_blink_cycle = 1000;";
            result = selectStmt.executeUpdate(sql);
            if (result != 1) {
                logParkingException(Level.SEVERE, null, 
                        "non 1 result: while initializing settings table ");                
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "while initializing settings table ");
        } finally {
            closeDBstuff(conn, selectStmt, rs, "while initializing settings table ");
        }           
    }

    /**
     * Make sure 3 default users(admin, manager, guest) exists.
     */
    public static void makeSureBasicUserExistance(Component parent) {
        /**
         * Check if admin, manager and guest user accounts exists and create it not.
         */
        // admin
        int cuCount = 0; // Created Users
        String cuIDstr = "";
        
        String id = ADMIN_ID;
        if (getRecordCount("users_osp", "id", id) == 0) {
            /**
             * Create admin acount.
             */
            cuCount += createDefaultUsers(id, NAME_ADMIN.getContent(), true);
            cuIDstr = id;
        }
        
        id = "manager";
        if (getRecordCount("users_osp", "id", id) == 0) {
            /**
             * Create admin acount.
             */
            cuCount += createDefaultUsers(id, NAME_MANAGER.getContent(), true);
            if (cuIDstr.length() > 0) {
                cuIDstr += ", " + id;
            } else {
                cuIDstr = id;
            }
        }
        
        id = "guest";
        if (getRecordCount("users_osp", "id", id) == 0) {
            /**
             * Create admin acount.
             */
            cuCount += createDefaultUsers(id, NAME_GUEST.getContent(), false);
            if (cuIDstr.length() > 0) {
                cuIDstr += ", " + id;
            } else {
                cuIDstr = id;
            }
        }
        
        if (cuCount > 0) {
            /**
             * Let user know about the creation of some default users.
             */
            String message = DEFAULT_USER_LINE1.getContent() + System.lineSeparator() 
                    + DEFAULT_USER_LINE2.getContent() + cuIDstr;
            JOptionPane.showMessageDialog(parent, message, DEFAULT_USER_TITLE.getContent(), 
                    INFORMATION_MESSAGE);
        }
    } 

    public static String getDisallowReason(String tagRecogedAs) {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String remark = null;
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            String sql = "SELECT remark FROM Vehicles Where PLATE_NUMBER = '" + tagRecogedAs + "'";
            rs = selectStmt.executeQuery(sql);
            if (rs.next()) {
                remark = rs.getString(1);
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "while fetching disallow reason(remark) for " + tagRecogedAs);
        } finally {
            closeDBstuff(conn, selectStmt, rs, "while fetching disallow reason(remark) for " + tagRecogedAs);
        }    
        
        return remark;        
    }    
    
    private static int createDefaultUsers(String id, String name, boolean isManager) {
        Connection conn = null;        
        PreparedStatement createAttendant = null;
        String sql = "Insert Into  users_osp (id, name, password, isManager, phone)" 
                + " Values (?, ?, md5(?), ?, ?)";
        int result = -1;
        try {
            int pIndex = 1;
            conn = JDBCMySQL.getConnection();
            createAttendant = conn.prepareStatement(sql);
            
            // <editor-fold defaultstate="collapsed" desc="-- Provide actual value to each field">
            createAttendant.setString(pIndex++, id);
            createAttendant.setString(pIndex++, name);
            createAttendant.setString(pIndex++, PASSWORD);
            if (isManager) {
                createAttendant.setInt(pIndex++, 1);
            } else {
                createAttendant.setInt(pIndex++, 0);
            }
            createAttendant.setString(pIndex++, OSP_PHONE);
            // </editor-fold>

            result = createAttendant.executeUpdate();

        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(ID: " + id + 
                    ", name: " + name + 
                    ", phone: " + OSP_PHONE + ")");                    
        } finally {
            closeDBstuff(conn, createAttendant, null, "(ID: " + id + ")");
            return result;
        }
    }    
    
    public static boolean passwordMatched(String userID, String passwd) 
    {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        try 
        {
            // Check if ID exists and password matches
            conn = JDBCMySQL.getConnection();
            pstmt = conn.prepareStatement("Select md5(?) as hashedPW, password as pwInDB " + 
                    "from users_osp where id = ?");
            pstmt.setString(1, passwd);
            pstmt.setString(2, userID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                //<editor-fold defaultstate="collapsed" desc="-- For the existing ID, check if password matches.">
                String hashedPW = rs.getString("hashedPW");                               
                String pwInDB = rs.getString("pwInDB");                               
                if (pwInDB.equals(hashedPW)) {
                    result = true;
                }
                //</editor-fold>                
            }
        } catch(Exception e) {
            logParkingException(Level.SEVERE, e, "(userID: " + userID + ")");
        } finally {
            Globals.closeDBstuff(conn, pstmt, rs, "(finally-userID: " + userID + ")");
        }
        return result;        
    }        
    
    public static String recordPerformance(int gateID, long miliSeconds) {
        String msg = null;
        // For every vehicle passinig, record performance and show statistics periodically.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String query = "SELECT passingCountCurrent, passingDelayCurrentTotalMs " +
                           "FROM gatedevices WHERE gateid = " + gateID;
        try {
            int passingCountCurrent;
            int passingDelayCurrentTotalMs;
            
            // create the java statement
            conn = JDBCMySQL.getConnection();
            stmt = conn.prepareStatement(query);

            // execute the query, and get a java resultset
            rs = stmt.executeQuery(query);   
            if (rs.next()) {
                passingCountCurrent = rs.getInt("passingCountCurrent");
                passingDelayCurrentTotalMs = rs.getInt("passingDelayCurrentTotalMs");
                passingCountCurrent++;
                passingDelayCurrentTotalMs += miliSeconds;
                
                if (passingCountCurrent % statCount == 0) {
                    float average = ((float)passingDelayCurrentTotalMs)/statCount;
                    msg = GATE_LABEL.getContent() + " #" + gateID + ": " + 
                            LETEST_MSG.getContent() + statCount + PASSING_MSG.getContent() + 
                            String.format("%.3f" + SECOND_MSG.getContent(), average/1000f);
                    //msg = ((String[])Globals.LabelsText.get(GATE_LABEL.ordinal()))[ourLang] 
                    //        + " #" + gateID + ": " + ((String[])Globals.TextFieldList.get(LETEST_MSG.ordinal()))[ourLang] + " " 
                    //        + statCount + ((String[])Globals.TextFieldList.get(PASSING_MSG.ordinal()))[ourLang] + ": "
                    //        + String.format("%.3f " + ((String[])Globals.LabelsText.get(SECONDS_LABEL.ordinal()))[ourLang], average/1000f);
                    recordPassingDelay(gateID, average);
                    passingCountCurrent = 0;         // Initialize passing delay statistics for the next cycle
                    passingDelayCurrentTotalMs = 0;
                }
                // Save passing delay statistics which is either updated or initialized
                updatePassingDelayStat(gateID, passingCountCurrent, passingDelayCurrentTotalMs);
            }
        } catch (SQLException se) {
            logParkingException(Level.SEVERE, se, "(read passing delay before update)");
        } finally {
            closeDBstuff(conn, stmt, rs, "(read passing delay before update)");
            return msg;
        }
    }    

    private static void updatePassingDelayStat(int gateID, int passingCountCurr, int passingDelayCurrTotal) 
    {
        int result = 0;
        Connection conn = null;
        PreparedStatement pStmt = null;    
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE gatedevices ");
        sql.append("SET passingCountCurrent = ?, ");
        sql.append("  passingDelayCurrentTotalMs = ? ");
        sql.append("WHERE gateid = ?");
        
        try 
        {
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(sql.toString());
            
            int loc = 1;
            pStmt.setInt(loc++, passingCountCurr);
            pStmt.setInt(loc++, passingDelayCurrTotal);
            pStmt.setInt(loc++, gateID);
            result = pStmt.executeUpdate();
        } catch(Exception e) {
            logParkingException(Level.SEVERE, e, "(update passing delay statistics)");
        } finally {
            if (result == 1) {
                passingCountCurrent[gateID] = passingCountCurr;
                passingDelayCurrentTotalMs[gateID] = passingDelayCurrTotal;
            } else {
                logParkingException(Level.SEVERE, null, "(failed update of passing delay statistics)");
            }
                
            closeDBstuff(conn, pStmt, null, "(update passing delay statistics)");
        }        
    }  
    
    /**
     * Reads global system settings(variables) from a database table.
     */
    public static void readSettings() {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery("SELECT * FROM SettingsTable");
            if (rs.next()) {
                parkingLotName = rs.getString("Lot_Name");
                if (rs.getInt("perfEvalNeeded") == 1)
                    storePassingDelay = true;
                else 
                    storePassingDelay = false;
                            
                parkingLotName = rs.getString("Lot_Name");
                pwStrengthLevel = rs.getShort("PWStrengthLevel");   // Password Complexity Level
                
                opLoggingIndex = rs.getShort("OptnLoggingLevel");
                locale = new Locale(rs.getString("languageCode"), rs.getString("countryCode"));
                switch(locale.getLanguage()){
                    case "ko" : 
                        language = KOREAN;
                        font_Type = "맑은 고딕";
                        break;
                    default:
                        language = ENGLISH;
                        break;
                }
                localeIndex = rs.getShort("localeIndex");
                statCountIndex = rs.getInt("statCount");     // Statistics Population Size
                statCount = statCountArr[statCountIndex];     // Statistics Population Size
                maxMessageLines = rs.getInt("maxMessageLines");
                maxMaintainDate = rs.getInt("max_maintain_date");   // image keeping duration
                gateCount = rs.getShort("GateCount");  // number of gates
                PIC_WIDTH = rs.getShort("PictureWidth");
                PIC_HEIGHT = rs.getShort("PictureHeight");
                /**
                 * Radio button choice DB stored value in the Car arrivals record search window's Duration Panel.
                 */
                SEARCH_PERIOD = rs.getShort("SearchPeriod");  
                EBD_flowCycle = rs.getInt("EBD_flow_cycle");
                EBD_blinkCycle = rs.getInt("EBD_blink_cycle");
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "(Loading System Settings from the DB)");
        } finally {
            CommonData.pwValidator = new PasswordValidator();
            closeDBstuff(conn, selectStmt, rs, "(Releasing system DB resources after settings loading)");
        }
        readGateDevices();
        initDeviceTypes();
    }
    
    /**
     * Read Electrical Display Board Settings from the database
     * @param EBD_DisplaySettings global variable which needs to be filled -- Call by reference.
     */
    public static EBD_DisplaySetting[] readEBoardSettings() {
        EBD_DisplaySetting[] EBD_DisplaySettings =
                new EBD_DisplaySetting[OSP_enums.EBD_DisplayUsage.values().length];           
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        
        try{
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery("Select E.*, S.EBD_flow_cycle, "
                    + "S.EBD_blink_cycle From eboard_settings E, SettingsTable S ");
            
            while(rs.next()){
                EBD_ContentType contentType = null;
                String verbatimContent = "";
                OSP_enums.EBD_Effects displayPattern = null;
                int displayCycle = 0; 
                    
                contentType = EBD_ContentType.values()[rs.getByte("content_type")];
                if (contentType == VERBATIM) {
                    verbatimContent = rs.getString("verbatim_content");
                }

                displayPattern = OSP_enums.EBD_Effects.values()[rs.getByte("display_pattern")];

                if (displayPattern == EBD_Effects.LTOR_FLOW || displayPattern == EBD_Effects.RTOL_FLOW)
                    displayCycle = rs.getInt("EBD_flow_cycle");  // global : EBD_flowCycle
                else if (displayPattern == BLINKING)
                    displayCycle = rs.getInt("EBD_blink_cycle"); // global: EBD_blinkCycle

                EBD_DisplaySettings[rs.getInt("usage_row") - 1] = new EBD_DisplaySetting(
                        verbatimContent, 
                        contentType, 
                        displayPattern, 
                        OSP_enums.EBD_Colors.values()[rs.getByte("text_color")],
                        OSP_enums.EBD_Fonts.values()[rs.getByte("text_font")], 
                        displayCycle);
            }
        } catch (SQLException ex) {
            Globals.logParkingException(Level.SEVERE, ex, "while reading e-board settings");  
        } finally {
            closeDBstuff(conn, selectStmt, rs, "Resource return used in eboard setting loading for ROW: ");
            return EBD_DisplaySettings;
        }
    }    
    
    public static EBD_DisplaySetting readEBoardUsageSettings(EBD_DisplayUsage usageRow) {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        
        EBD_DisplaySetting resultSetting = null;
        
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery("Select E.*, S.EBD_flow_cycle, S.EBD_blink_cycle " +
                    "From eboard_settings E, SettingsTable S " +
                    "Where E.usage_row = " +usageRow.getVal());
            
            if (rs.next()) {
                EBD_ContentType contentType = null;
                String verbatimContent = null;
                EBD_Effects displayPattern = null;
                int displayCycle = 0; 
                
                contentType = EBD_ContentType.values()[rs.getByte("content_type")];
                if (contentType == VERBATIM) {
                    verbatimContent = rs.getString("verbatim_content");
                }
                
                displayPattern = EBD_Effects.values()[rs.getByte("display_pattern")];
                
                if (displayPattern == RTOL_FLOW || displayPattern == LTOR_FLOW)
                    displayCycle = rs.getInt("EBD_flow_cycle"); // global : EBD_flowCycle
                else if (displayPattern == BLINKING)
                    displayCycle = rs.getInt("EBD_blink_cycle"); // global: EBD_blinkCycle
                    
                resultSetting = new EBD_DisplaySetting(verbatimContent, contentType,
                        displayPattern,
                        EBD_Colors.values()[rs.getByte("text_color")],
                        EBD_Fonts.values()[rs.getByte("text_font")],
                        displayCycle
                );
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "ROW for which eboard setting is loaded: " + usageRow);
        } finally {
            closeDBstuff(conn, selectStmt, rs, 
                    "Resource return used in eboard setting loading for ROW: " + usageRow);
        }
        return resultSetting;
    }    
    

    public static boolean parkingPermitted(String tagEnteredAs) {
        boolean result = false;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;        
        String sql = "Select permitted From Vehicles Where PLATE_NUMBER = '" + tagEnteredAs + "'";

        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int permissionCode = rs.getInt("permitted");
                if (permissionCode == 0) {
                    result =  true;
                }
            } 
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "while checking vehicle park perm");
        } finally {
            closeDBstuff(conn, stmt, rs, "check vehicle parking permission");
            return result;
        }    
    }
    
    
    public static PermissionType enteranceAllowed(String tagRecognized, 
            StringBuffer tagEnteredAs, StringBuffer remark) {
        
        PermissionType result = PermissionType.UNREGISTERED;
        StringBuilder firstPart = new StringBuilder();
        int secondStart = getFirstPart(tagRecognized, firstPart); 
        
        StringBuilder secondPart = new StringBuilder();
        getSecondPart(tagRecognized, secondStart, secondPart);
        
        if (!isInteger(firstPart.toString()) || !isInteger(secondPart.toString())) {
            return PermissionType.BADTAGFORMAT;
        }
                
        Connection conn = null;
        PreparedStatement fetchPermission = null;
        ResultSet rs = null;        
        StringBuffer sb = new StringBuffer("Select Plate_Number, Permitted, Whole_required, Remark ");
        sb.append("From vehicles ");
        sb.append("Where PLATE_NUMBER like ?");

        try {
            conn = JDBCMySQL.getConnection();
            fetchPermission = conn.prepareStatement(sb.toString());
            String searchKey = "%" + firstPart + "%" + secondPart + "%";
            fetchPermission.setString(1, searchKey);            
            rs = fetchPermission.executeQuery();

            if (rs.next()) {
                tagEnteredAs.append(rs.getString("Plate_Number"));
                int permissionCode = rs.getInt("Permitted");
                int all_number = rs.getInt("Whole_required");
                String remarkDB = rs.getString("Remark");
                if (remarkDB != null) {
                    remark.append(remarkDB);
                }
                
                if (all_number == OSP_FALSE || tagRecognized.equals(tagEnteredAs.toString())) {
                    if (permissionCode == PermissionType.ALLOWED.ordinal()) {
                        result = PermissionType.ALLOWED;                
                    } else if (permissionCode == PermissionType.DISALLOWED.ordinal()) {
                        result = PermissionType.DISALLOWED;                
                    } else {
                        result = PermissionType.UNREGISTERED;           
                    }
                } else {
                    result = PermissionType.UNREGISTERED;           
                }
            } 
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "while checking vehicle park perm");
        } finally {
            closeDBstuff(conn, fetchPermission, rs, "check vehicle parking permission");
            return result;
        }         
    }

    public static int getCycleFromDB(EBD_CycleType cycleType) {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        int cycle = 0;
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery("SELECT " + cycleType + " FROM SettingsTable");
            if (rs.next()) {
                cycle = rs.getInt(1);
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "while loading cycle of type: " + cycleType);
        } finally {
            closeDBstuff(conn, selectStmt, rs, "while closing resource of loading cycle of type: " + cycleType);
        }        
        return cycle;
    }

    public static int getRecordCount(OsPaTable tableName, int CD_SEQ_NO) {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        int vehicleCount = 0;
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            String sql = "SELECT count(*) FROM " + tableName;
            if (CD_SEQ_NO >= 0) {
                sql += " WHERE DRIVER_SEQ_NO = " + CD_SEQ_NO;
            }
            rs = selectStmt.executeQuery(sql);
            if (rs.next()) {
                vehicleCount = rs.getInt(1);
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "while loading VEHICLE COUNT FOR USER SN: " + CD_SEQ_NO);
        } finally {
            closeDBstuff(conn, selectStmt, rs, "WHEN loading VEHICLE COUNT FOR USER SN: " + CD_SEQ_NO);
        }    
        
        return vehicleCount;        
    }
    
    public static int getRecordCount(String tableName, String columnName, String columnValue) {
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int count = 0;
        String excepMsg = "table: " + tableName + ", column: " + columnName + ", col' value: " + columnValue;
        try {
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(
                    "SELECT count(*) FROM " + tableName + " WHERE " + columnName + " = ?");
            pStmt.setString(1, columnValue);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pStmt, rs, excepMsg);
        }    
        return count;        
    }

    private static int getFirstPart(String tagRecognized, StringBuilder firstPart) {
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

    private static void getSecondPart(String tagRecognized, int secondStart, StringBuilder secondPart) {
        int idx = secondStart;
        for (   ; idx < tagRecognized.length() && secondPart.length() < 4; idx++) {
            if (Character.isDigit(tagRecognized.charAt(idx))) {
                secondPart.append(tagRecognized.charAt(idx));
            } else {
                if (secondPart.length() > 0)
                    return;
            }
        }
        return;
    }       

    private static void readGateDevices() {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        
        try {
            conn = JDBCMySQL.getConnection();
            selectStmt = conn.createStatement();
            
            int gateID = 0;
            String strField;
            gateNames = new String[MAX_GATES + 1];   // textual gate names assigned
            deviceIP = new String[DeviceType.values().length][MAX_GATES + 1];
            devicePort = new String[DeviceType.values().length][MAX_GATES + 1];
            deviceType = new byte[DeviceType.values().length][MAX_GATES + 1];
            deviceComID = new String[DeviceType.values().length][MAX_GATES + 1];
            connectionType = new byte[DeviceType.values().length][MAX_GATES + 1];
            passingCountCurrent = new int[MAX_GATES + 1];
            passingDelayCurrentTotalMs = new int[MAX_GATES + 1];
            
            rs = selectStmt.executeQuery("SELECT * FROM gatedevices order by GateID");
            while (rs.next() ) {
                gateID = rs.getInt("GateID");
                if (gateID > MAX_GATES)
                    break;
                strField = rs.getString("gatename");
                gateNames[gateID] = (strField == null ? "(anonymous)" : strField);
                
                strField = rs.getString("cameraIP");
                deviceIP[Camera.ordinal()][gateID] = (strField == null ? "127.0.0.1" : strField);

                strField = rs.getString("cameraPort");
                devicePort[Camera.ordinal()][gateID] = getPortNumber(strField);
                deviceType[Camera.ordinal()][gateID] = (byte) rs.getInt("cameraType");
                
                strField = rs.getString("e_boardIP");
                deviceIP[E_Board.ordinal()][gateID] = (strField == null ? "127.0.0.1" : strField);
                strField = rs.getString("e_boardPort");
                devicePort[E_Board.ordinal()][gateID] = getPortNumber(strField);
                deviceType[E_Board.ordinal()][gateID] = (byte) rs.getInt("e_boardType");
                connectionType[E_Board.ordinal()][gateID] = (byte) rs.getInt("e_boardConnType");
                deviceComID[E_Board.ordinal()][gateID] = rs.getString("e_boardCOM_ID");
                
                strField = rs.getString("gatebarIP");
                deviceIP[GateBar.ordinal()][gateID] = (strField == null ? "127.0.0.1" : strField);
                strField = rs.getString("gatebarPort");
                devicePort[GateBar.ordinal()][gateID] = getPortNumber(strField);
                deviceType[GateBar.ordinal()][gateID] = (byte) rs.getInt("gateBarType");
                connectionType[GateBar.ordinal()][gateID] = (byte) rs.getInt("gateBarConnType");
                deviceComID[GateBar.ordinal()][gateID] =  rs.getString("gatebarCOM_ID");
                
                passingCountCurrent[gateID] = rs.getInt("passingCountCurrent");
                
                // Total car passing delays witnessed so far in this period of delay accumulation
                passingDelayCurrentTotalMs[gateID] = rs.getInt("passingDelayCurrentTotalMs");
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "(Loading gate, device settings)");
        } finally {
            closeDBstuff(conn, selectStmt, rs, "(releasing after gate, device settings loading)");
        }        

    }

    private static void recordPassingDelay(int gateID, float average) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pStmt = null;    
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE gatedevices ");
        sql.append("SET passingDelayPreviousAverageMs = ?,");
        sql.append(    " passingDelayPreviousPopulation = ?, ");
        sql.append(    " passingDelayCalculationTime = ? ");
        sql.append("WHERE gateid = " + gateID);
        
        try 
        {
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(sql.toString());
            String recordTmStr = sdf.format(new Date());
            
            int loc = 1;
            pStmt.setFloat(loc++, average);
            pStmt.setInt(loc++, statCount);
            pStmt.setString(loc++, recordTmStr);
            
            result = pStmt.executeUpdate();
        } catch(Exception e) {
            logParkingException(Level.SEVERE, e, "(record passing delay statistics)");
        } finally {
            if (result != 1) {
                logParkingException(Level.SEVERE, null, "(failed record of passing delay statistics)");
            }
            closeDBstuff(conn, pStmt, null, "(record passing delay statistics)");
        }          
    }

    private static String getPortNumber(String strField) {
        if (strField == null) 
            return "8080";
        else if (strField.length() == 0)
            return "8080";
        else 
            return strField;
    }
    
    public static int insertOneVehicle(String plateNo, int seqNo, int notification, 
            int wholeTag, int parkPermit, String reasonTxt, String otherTxt) {
        Connection conn = null;
        PreparedStatement createDriver = null;
        String excepMsg = "in creation of a car with tag number: " + plateNo;

        int result = 0;
        try {
            StringBuffer sb = new StringBuffer("Insert Into Vehicles (");
            sb.append(" PLATE_NUMBER, DRIVER_SEQ_NO, NOTI_REQUESTED,");
            sb.append(" WHOLE_REQUIRED, PERMITTED, Remark, OTHER_INFO, ");
            sb.append(" CREATIONDATE) Values (?, ?, ?, ?, ?, ?, ?, current_timestamp)");

            conn = getConnection();
            createDriver = conn.prepareStatement(sb.toString());
            int loc = 1;
            
            createDriver.setString(loc++, plateNo);
            createDriver.setInt(loc++, seqNo);
            createDriver.setInt(loc++, notification);
            createDriver.setInt(loc++, wholeTag);
            createDriver.setInt(loc++, parkPermit);
            createDriver.setString(loc++, reasonTxt);
            createDriver.setString(loc++, otherTxt);
            
            result = createDriver.executeUpdate();
        } catch (SQLException e) {
            logParkingException(Level.SEVERE, e, excepMsg);
        } finally {
            closeDBstuff(conn, createDriver, null, excepMsg);
            return result;
        }
    }    

    private static void setEBoardRow(int row, String verbatim, int type, 
            int pattern, int color, int font) {
        Connection conn = null;
        PreparedStatement ebdSetting = null;
        ResultSet rs = null;
        int result = 0;
        int index = 1;
        try {
            String sql = "Update eboard_settings " +
                    "Set verbatim_content = ?, content_type = ?, " +
                    "display_pattern = ?, text_color = ?, text_font = ? " +
                    "Where usage_row = ?";
            
            conn = JDBCMySQL.getConnection();
            ebdSetting = conn.prepareStatement(sql);
            
            ebdSetting.setString(index++, verbatim);
            ebdSetting.setInt(index++, type);
            ebdSetting.setInt(index++, pattern);
            ebdSetting.setInt(index++, color);
            ebdSetting.setInt(index++, font);
            ebdSetting.setInt(index++, row);
            
            result = ebdSetting.executeUpdate();
            if (result != 1) {
                logParkingException(Level.SEVERE, null, 
                        "non 1 result: while initializing e-board settings table ");
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "while initializing e-board settings table ");
        } finally {
            closeDBstuff(conn, ebdSetting, rs, "while initializing e-board settings table ");
        }   
    }
}
