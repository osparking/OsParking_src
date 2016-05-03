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
package com.osparking.vehicle.driver;

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import com.osparking.vehicle.AffiliationBuildingForm;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static com.osparking.global.names.DB_Access.readSettings;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.insertNewBuilding;
import static com.osparking.global.Globals.insertNewBuildingUnit;
import static com.osparking.global.Globals.insertNewLevel1Affiliation;
import static com.osparking.global.Globals.insertNewLevel2Affiliation;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.CHECK_BUILDING_ODS_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.READ_FAIL_AFFILIATION_ODS_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.*;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.DriverCol;
import com.osparking.global.names.OSP_enums.VehicleCol;
import com.osparking.global.names.WrappedInt;
import com.osparking.vehicle.VehiclesForm;
import java.sql.Statement;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
/**
 *
 * @author Open Source Parking Inc.
 */
public class ODSReader {
    VehiclesForm vehicle = new VehiclesForm();
    
    public static String getWrongCellPointString(ArrayList<Point> wrongCells) {
        // make point list string in the following format
        // (1, 2), (1,4)
        // (2, 5)
        // (4, 3)
        
        StringBuffer sb = new StringBuffer();
        int prevRow = -1;
        for (Point pt : wrongCells) {
            if (prevRow != -1 && prevRow != pt.x) {
                sb.append(System.getProperty("line.separator"));
            } else {
                if (sb.length() > 0) 
                    sb.append(", ");
            }
            sb.append("(" + pt.x + ", " + pt.y + ")");
            prevRow = pt.x;
        }
        return sb.toString();
    }
    
    private static void readBuildingAndUnits(Sheet sheet) {
        ODSReader objODSReader = new ODSReader();
        
        ArrayList<Point> wrongCells = new ArrayList<Point>();
        WrappedInt buildingTotal = new WrappedInt();
        WrappedInt unitTotal = new WrappedInt();

        if (objODSReader.checkODS(sheet, wrongCells, buildingTotal, unitTotal))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Following data recognized, do you want to load them?");
            sb.append(System.getProperty("line.separator"));
            sb.append(" - Data count: number of buildings " + buildingTotal.getValue());
            sb.append(", number of rooms " + unitTotal.getValue());

            int result = JOptionPane.showConfirmDialog(null, 
                            sb.toString(),
                            "Sheet Check Result", 
                            JOptionPane.YES_NO_OPTION);            

            if (result == JOptionPane.YES_OPTION) {                
                objODSReader.readODS(sheet, null);
            }
        } else {
            // display wrong cell points if existed
            if (wrongCells.size() > 0) {
                JOptionPane.showConfirmDialog(null, 
                        "Cells containing data other than numbers" + 
                            System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                       "Sheet Cell Value Error", 
                       JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                      
            }
        }    
    }

    private static void readAffiliationLevels(Sheet sheet) {
        ODSReader objODSReader = new ODSReader();
        
        WrappedInt level1_total = new WrappedInt();
        WrappedInt level2_total = new WrappedInt();

        if (objODSReader.checkAffiliationODS(sheet, level1_total, level2_total))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Following data recognized, do you want to load them?");
            
            sb.append(System.getProperty("line.separator"));
            sb.append(" - Data count: higher affiliation " + level1_total.getValue());
            sb.append(", lower affiliation " + level2_total.getValue());

            int result = JOptionPane.showConfirmDialog(null,
                                sb.toString(),
                                "Sheet Check Result", 
                                JOptionPane.YES_NO_OPTION);            
            if (result == JOptionPane.YES_OPTION) {                
                objODSReader.readAffiliationODS(sheet, null);
            }
        } 
    }

    final int MAX_BLANK_ROW = 5;
    private static boolean upperLevelMissingWarningNotGiven = true;
    
    public void readODS(Sheet sheet, AffiliationBuildingForm parentForm)
    {
        //Getting the 0th sheet for manipulation| pass sheet name as string
        MutableCell cell = null;

        //<editor-fold defaultstate="collapsed" desc="-- Method variables">
        boolean goodBuilding = false;
        int bldgSeqNo = 0;
        int buildingCount = 0, buildingReject = 0;
        int unitCount = 0, unitReject = 0;
        //</editor-fold>

        for (int nRowIndex = 0; true; nRowIndex++)
        {
            boolean rowDataStarted = false;

            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">               
            for(int nColIndex = 0; true; nColIndex++)
            {
                try {
                    cell = sheet.getCellAt(nColIndex, nRowIndex);
                } catch (Exception e) {
                    if (e.getClass() == ArrayIndexOutOfBoundsException.class)
                    {
                        break; // desert a completely empty row
                    }
                }                
                Object cellObj = cell.getValue();
                WrappedInt cellValue = new WrappedInt();
                if (cellObj != null && isInteger(cellObj.toString(), cellValue))
                {
                    rowDataStarted = true;
                    if (nColIndex == 0) {
                        //<editor-fold defaultstate="collapsed" desc="-- Process building number column ">
                        if (cellValue.getValue() == -1) {
                            if (parentForm != null) {
                                parentForm.loadBuilding(0, 0);
                            }
                            
                            StringBuilder sb = new StringBuilder();
                            
                            switch (language) {
                                case KOREAN:
                                    sb.append("자료 요약");
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 불러온 건물 수: " + buildingCount);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 거부된 건물 수: " + buildingReject);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 불러온 호실 수: " + unitCount);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 거부된 호실 수: " + unitReject);
                                    break;

                                case ENGLISH:
                                    sb.append("Loaded Data Summary");
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Loaded Buildings: " + buildingCount);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Rejected Buildings: " + buildingReject);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Loaded Rooms: " + unitCount);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Rejected Rooms: " + unitReject);
                                    break;

                                default:
                                    break;
                            }
                            
                            JOptionPane.showConfirmDialog(null, sb.toString(),
                                    READ_ODS_DIALOGTITLE.getContent(), 
                                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                               
                            return;
                        }
                        else {
                            int result = 0;
                            try {
                                result = insertNewBuilding(cellValue.getValue());
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == ER_DUP_ENTRY)
                                {
                                    goodBuilding = true;
                                }
                                else
                                {
                                    logParkingException(Level.SEVERE, ex, "(inserted building: " 
                                            + cellValue.getValue() + ")");
                                }
                            }  finally {
                                if (result == 1) {
                                    buildingCount++;
                                    goodBuilding = true;
                                } else {
                                    buildingReject++;
                                }
                            }                                  

                            if (goodBuilding) {
                                bldgSeqNo = getBldgSeqNo(cellValue.getValue());
                            }
                        }
                        //</editor-fold>
                    } else {
                        //<editor-fold defaultstate="collapsed" desc="-- Process unit number columns ">
                        if (goodBuilding) {
                            int result = 0;
                            // try to insert unit number with the building sequence number
                            try {
                                result = insertNewBuildingUnit(cellValue.getValue(), bldgSeqNo);
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() != ER_DUP_ENTRY) {
                                    logParkingException(Level.SEVERE, ex, 
                                            "(inserted UNIT: " + cellValue.getValue() + ")");
                                }
                            }  finally {
                                if (result == 1)
                                    unitCount++;
                                else
                                    unitReject++;
                            }    
                        } else {
                            unitReject++;
                            if (upperLevelMissingWarningNotGiven)
                            {
                                upperLevelMissingWarningNotGiven = false;
                                
                                String dialogMessage = "";
                                
                                switch (language) {
                                    case KOREAN:
                                        dialogMessage = "호실이 정확하지 않습니다." 
                                                    + System.getProperty("line.separator") 
                                                    + " - 호실 ID#: " + cellValue.getValue();
                                        break;

                                    case ENGLISH:
                                        dialogMessage = "It's unknown where this room belongs" 
                                                    + System.getProperty("line.separator") 
                                                    + " - room ID#: " + cellValue.getValue();
                                        break;

                                    default:
                                        break;
                                }                                
                                
                                JOptionPane.showConfirmDialog(null, dialogMessage,
                                        READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE); 
                            }                            
                        }
                        //</editor-fold>
                    }
                } else {
                    if (rowDataStarted) {
                        break;
                    }
                }
            }
            //</editor-fold>
        }
    }

    private int getBldgSeqNo(int bldgNo) {
        Connection conn = null;
        int result = 0;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "select for BLDG_NO = " + bldgNo; 
        
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("Select SEQ_NO from building_table where BLDG_NO = ?");
            pstmt.setInt(1, bldgNo);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getInt(1);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg + 
                    bldgNo +")");
        }
        finally {
            Globals.closeDBstuff(conn, pstmt, rs, excepMsg);
        }
        return result;   
    }
    
    final String DATA_END_MARKER = "END";

    public boolean checkAffiliationODS(Sheet sheet, WrappedInt level1_Total, WrappedInt level2_Total) {
        
        int numBlankRow = 0;
        MutableCell cell = null;
        
        loops:
        for (int nRowIndex = 0; true; nRowIndex++)
        {
            if (nRowIndex == 28) 
            {
                int x = nRowIndex;
            }
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    break;
                } else {
                    continue;
                }
            }
            
            boolean rowDataStarted = false;   
            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">    
            for(int nColIndex = 0; true; nColIndex++)
            {
                try {
                    cell = sheet.getCellAt(nColIndex, nRowIndex);
                } catch (Exception e) {
                    if (e.getClass() == ArrayIndexOutOfBoundsException.class ) {
                        if (rowDataStarted) {
                            break;
                        }                    
                    }
                }
                String cellStr = cell.getValue().toString();
                if (cellStr.length() == 0)
                {
                    if (rowDataStarted) {
                        break;
                    }
                } else {
                    rowDataStarted = true;
                    if (nColIndex == 0) {
                        if (cellStr.equals(DATA_END_MARKER)) {
                            break loops;
                        } else {
                            level1_Total.setValue(level1_Total.getValue() + 1);
                        }
                    } else {
                        level2_Total.setValue(level2_Total.getValue() + 1);
                    }
                }
            }
            //</editor-fold>
        }
        if (numBlankRow > MAX_BLANK_ROW) {
            // Give warning that data end(building number '-1') mark is missing
            String dialogMessage = "";
            
            switch(language){
                case KOREAN :
                    dialogMessage ="상위 및 하위 소속 조직명칭 종료마크(아래)가 누락됨" + 
                                System.getProperty("line.separator") +  
                                "종료마크: 첫 열이 " + DATA_END_MARKER + "인 행";
                    break;
                    
                case ENGLISH:                    
                    dialogMessage = "Higher and Lower Affiliation Name End Mark Missing" + 
                                System.getProperty("line.separator") +  
                                "End Mark: a row that begins with '" + DATA_END_MARKER + "' (except quit mark)";
                    break;
                    
                default:
                    break;
            }            
            
            JOptionPane.showConfirmDialog(null, dialogMessage,
                            READ_ODS_FAIL_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                
            return false;
        } else {
            return true;
        }
    }    
    
    public boolean checkODS(Sheet sheet, ArrayList<Point> wrongCells,
            WrappedInt buildingTotal, WrappedInt unitTotal) {
        
        int numBlankRow = 0;
        MutableCell cell = null;
        
        loops:
        for (int nRowIndex = 0; true; nRowIndex++)
        {
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    break;
                } else {
                    continue;
                }
            }
            
            boolean rowDataStarted = false;            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">    
            for(int nColIndex = 0; true; nColIndex++)
            {
                System.out.println("row: " + nRowIndex + ", col: " + nColIndex);
                cell = sheet.getCellAt(nColIndex, nRowIndex);
                Object cellObj = cell.getValue();
                WrappedInt cellValue = new WrappedInt();                
                if (isInteger(cellObj.toString(), cellValue))
                {
                    rowDataStarted = true;
                    if (nColIndex == 0) {
                        if (cellValue.getValue() == -1) {
                            break loops;
                        } else {
                            buildingTotal.setValue(buildingTotal.getValue() + 1);
                        }
                    } else {
                        unitTotal.setValue(unitTotal.getValue() + 1);
                    }
                } else {
                    if (cellObj.toString().length() == 0) {
                        if (rowDataStarted) {
                            break;
                        }
                    } else {
                        rowDataStarted = true;
                        wrongCells.add(new Point(nRowIndex+1, nColIndex+1));
                    }
                }
            }
            //</editor-fold>
        }
        if (numBlankRow > MAX_BLANK_ROW) {
            // Give warning that data end(building number '-1') mark is missing
            JOptionPane.showConfirmDialog(null, CHECK_BUILDING_ODS_DIALOG.getContent(), 
                    READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                
            return false;
        } else {
            if (wrongCells.size() > 0) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    private boolean aBlankRow(Sheet sheet, int nRowIndex) {
        boolean result = false;
        MutableCell cell_1 = null, cell_2 = null;
        Object cell_1Obj = null, cell_2Obj = null;
        try {

            cell_1 = sheet.getCellAt(0, nRowIndex);
            cell_1Obj = cell_1.getValue();

            cell_2 = sheet.getCellAt(1, nRowIndex);
            cell_2Obj = cell_2.getValue();

            if (cell_1Obj.toString().length() == 0 && cell_2Obj.toString().length() == 0)
            {
                result = true;
            } 
        } catch (Exception ex) {
            if (cell_1Obj == null) {
                result = true;
            } else
            if (cell_1Obj.toString().length() == 0 &&
                    ex.getClass() == IndexOutOfBoundsException.class)
            {
                result = true;
            } else {
                logParkingException(Level.SEVERE, ex, 
                        "(in checking ods, row index: " + nRowIndex +")"); 
            }
        } finally {
            return result;
        }
    }

    private static boolean isInteger(String s, WrappedInt cellValue) {
        try { 
            cellValue.setValue(Integer.parseInt(s)); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }    

    public void readAffiliationODS(Sheet sheet, AffiliationBuildingForm parentForm) {
        //Getting the 0th sheet for manipulation| pass sheet name as string
        MutableCell cell = null;

        //<editor-fold defaultstate="collapsed" desc="-- Method variables">
        boolean goodLevel1 = false;
        int L1_no = 0;
        int level1Count = 0, level1Reject = 0;
        int level2Count = 0, level2Reject = 0;
        //</editor-fold>

        for (int nRowIndex = 0; true; nRowIndex++)
        {
            boolean rowDataStarted = false;

            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">               
            for(int nColIndex = 0; true; nColIndex++)
            {
                try {
                    cell = sheet.getCellAt(nColIndex, nRowIndex);
                } catch (Exception e) {
                    if (e.getClass() == ArrayIndexOutOfBoundsException.class)
                    {
                        break; // desert a completely empty row
                    }
                }
                String cellStr = cell.getValue().toString();
                if (cellStr != null && cellStr.length() > 0)
                {
                    rowDataStarted = true;
                    if (nColIndex == 0) {
                        //<editor-fold defaultstate="collapsed" desc="-- Process Level 1 affiliations ">
                        if (cellStr.equals(DATA_END_MARKER)) {
                            if (parentForm != null) 
                            {
                                parentForm.loadL1_Affiliation(0, "");
                            }

                            StringBuilder sb = new StringBuilder();
                            
                            switch (language) {
                                case KOREAN:
                                    sb.append("ods파일 불러오기 결과");
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 불러온 상위 소속 수: " + level1Count);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 거부된 상위 소속 수: " + level1Reject);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 불러온 하위 소속 수: " + level2Count);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - 거부된 하위 소속 수: " + level2Reject);
                                    break;

                                case ENGLISH:
                                    sb.append("Result of ods file loading");
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Higher Affiliation Loaded: " + level1Count);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Higher Affiliation Rejected: " + level1Reject);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Lower Affiliation Loaded: " + level2Count);
                                    sb.append(System.getProperty("line.separator"));
                                    sb.append(" - Lower Affiliation Rejected: " + level2Reject);
                                    break;

                                default:
                                    break;
                            }                            
                            
                            JOptionPane.showConfirmDialog(null, sb.toString(),
//                                    getTextFor(READ_AFFILIATION_ODS_DIALOG, sb, level1Count, level1Reject, 
//                                            level2Count, level2Reject).toString(), 
                                     READ_ODS_DIALOGTITLE.getContent(), 
                                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                               
                            return;
                        }
                        else {
                            int result = 0;
                            try {
                                result = insertNewLevel1Affiliation(cellStr);
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() == ER_DUP_ENTRY)
                                {
                                    goodLevel1 = true;
                                }
                                else
                                {
                                    logParkingException(Level.SEVERE, ex, 
                                            "(insertion tried level 1 name: " + cellStr + ")");
                                }
                            }  finally {
                                if (result == 1) {
                                    level1Count++;
                                    goodLevel1 = true;
                                } else {
                                    level1Reject++;
                                }
                            }                                  

                            if (goodLevel1) {
                                // considering the case of level 1 is duplicate and can't be inserted
                                // it is safer to fetch key value in a completely seperated manner.
                                L1_no = getLevel1_No(cellStr);
                            }
                        }
                        //</editor-fold>
                    } else {
                        //<editor-fold defaultstate="collapsed" desc="-- Process L2 name columns ">
                        if (goodLevel1) {
                            int result = 0;
                            // try to insert Level 2 name with the Level 1 number
                            try {
                                result = insertNewLevel2Affiliation(L1_no, cellStr);
                            } catch (SQLException ex) {
                                if (ex.getErrorCode() != ER_DUP_ENTRY) {
                                    logParkingException(Level.SEVERE, ex, 
                                            "(insertion tried level 2: " + cellStr + ")");
                                }
                            }  finally {
                                if (result == 1)
                                    level2Count++;
                                else
                                    level2Reject++;
                            }                              
                        } else {
                            level2Reject++;
                            if (upperLevelMissingWarningNotGiven)
                            {
                                upperLevelMissingWarningNotGiven = false;
                                JOptionPane.showConfirmDialog(null, 
                                        READ_FAIL_AFFILIATION_ODS_DIALOG.getContent() + cellStr,
//                                        getTextFor(READ_FAIL_AFFILIATION_ODS_DIALOG, cellStr), 
                                        READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE); 
                            }
                        }
                        //</editor-fold>
                    }
                } else {
                    if (rowDataStarted) {
                        break;
                    }
                }
            }
            //</editor-fold>
        }
    }

    private int getLevel1_No(String L1_name) {
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "get number for name = " + L1_name;
        String sql = "Select L1_NO from L1_Affiliation where PARTY_NAME = ?";
        int result = 0;
        try 
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, L1_name);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getInt(1);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            Globals.closeDBstuff(conn, pstmt, rs, excepMsg);
        }
        return result;       
    }
    
    public static void main(String[] args) {
        //Creating File object for the .ods file to read
        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        ODSReader objODSReader = new ODSReader();

        URL url = null;
        File file = null;
        try {
            url = objODSReader.getClass().getResource("/AffiliBuild.ods");
            file = new File(url.toURI());
        } catch(URISyntaxException e) {
            file = new File(url.getPath());
        }            

        Sheet sheet = null;
        try {
            sheet = SpreadSheet.createFromFile(file).getSheet(0);
        } catch (IOException ex) {
            Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Point> wrongCells = new ArrayList<Point>();
        WrappedInt buildingTotal = new WrappedInt();
        WrappedInt unitTotal = new WrappedInt();

        if (objODSReader.checkODS(sheet, wrongCells, buildingTotal, unitTotal))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Following data recognized. Want to continue loading?");
            sb.append(System.getProperty("line.separator"));
            sb.append(" -Date: " + buildingTotal.getValue() + " building numbers, ");
            sb.append(unitTotal.getValue() + " room numbers");

            int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                    "Sheed Scan Result", JOptionPane.YES_NO_OPTION);            
            if (result == JOptionPane.YES_OPTION) {                
                objODSReader.readODS(sheet, null);
            }
        } else {
            // display wrong cell points if existed
            if (wrongCells.size() > 0) {
                JOptionPane.showConfirmDialog(null, "Cells containing data other than numbers" + 
                        System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                        "Sheet Cell Value Error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                      
            }
        }
    }   

    public void readVehiclesODS(Sheet sheet, VehiclesForm parentForm) {
        int vehicleCount = 0, vehicleReject = 0;
        int duplicateCount = 0;
        int numBlankRow = 0;
        for (int nRowIndex = 1; true; nRowIndex++)
        {   
            //<editor-fold defaultstate="collapsed" desc="-- finish loading or skip row">              
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    if (parentForm != null) {
                        parentForm.loadVehicleTable(0, ""); 
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    
                    switch (language) {
                        case KOREAN:
                            sb.append("자료 불러오기 결과");
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 불러온 차량: " + vehicleCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 중복 자료: " + duplicateCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 총 거부 자료: " + vehicleReject);
                            break;

                        case ENGLISH:
                            sb.append("Sheet Loading Result");
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Loaded vehicle: " + vehicleCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Duplicates rejected: " + duplicateCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Total rejected: " + vehicleReject);
                            break;

                        default:
                            break;
                    }
                    
                    JOptionPane.showConfirmDialog(null, sb.toString(),
//                            getTextFor(READ_VEHICLE_ODS_DIALOG, sb, 
//                                    vehicleCount, duplicateCount, vehicleReject).toString(), 
                            READ_ODS_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                               
                    return;                    
                } else {
                    continue;
                }
            }      
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">  
            String carTag = (String)getCellValueAt(sheet, VehicleCol.PlateNumber.getNumVal(), nRowIndex).getValue();
            String driver = (String)getCellValueAt(sheet, VehicleCol.Name.getNumVal(), nRowIndex).getValue();
            String affiliation = (String)getCellValueAt(sheet, VehicleCol.Affiliation.getNumVal(), nRowIndex).getValue();
            String building = (String)getCellValueAt(sheet, VehicleCol.Building.getNumVal(), nRowIndex).getValue();
            String otherInfo = (String)getCellValueAt(sheet, VehicleCol.OtherInfo.getNumVal(), nRowIndex).getValue();
            String cellPhone = (String)getCellValueAt(sheet, VehicleCol.CellPhone.getNumVal(), nRowIndex).getValue();
            String phone = (String)getCellValueAt(sheet, VehicleCol.Phone.getNumVal(), nRowIndex).getValue();
            int notification = Integer.parseInt(getCellValueAt(sheet, VehicleCol.Notification.getNumVal(), nRowIndex).getValue().toString());
            int whole = Integer.parseInt(getCellValueAt(sheet, VehicleCol.Whole.getNumVal(), nRowIndex).getValue().toString());
            int permitted = Integer.parseInt(getCellValueAt(sheet, VehicleCol.Permitted.getNumVal(), nRowIndex).getValue().toString());
            String causes = (String)getCellValueAt(sheet, VehicleCol.Causes.getNumVal(), nRowIndex).getValue();
            String creation = (String)getCellValueAt(sheet, VehicleCol.Creation.getNumVal(), nRowIndex).getValue();
            String modification = (String)getCellValueAt(sheet, VehicleCol.Modification.getNumVal(), nRowIndex).getValue();
            if(modification.equals(""))
                modification = null;
            int SeqNo = Integer.parseInt(getCellValueAt(sheet, VehicleCol.SeqNo.getNumVal(), nRowIndex).getValue().toString());
            //</editor-fold>

            Connection conn = null;
            PreparedStatement createDriver = null;
            String excepMsg = "while creating car driver info for : " + carTag;
    
            int result = 0;
            try {
                //<editor-fold defaultstate="collapsed" desc="--Insert driver info into Database">  
                
                String sql = "Insert Into vehicles (PLATE_NUMBER, DRIVER_SEQ_NO, "
                        + "NOTI_REQUESTED, WHOLE_REQUIRED, OTHER_INFO, " 
                        + " PERMITTED, CREATIONDATE, LASTMODIDATE, Remark)" 
                        + " Values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                conn = getConnection();
                createDriver = conn.prepareStatement(sql);
                createDriver.setString(1, carTag);
                createDriver.setInt(2, SeqNo);
                createDriver.setInt(3, notification);
                createDriver.setInt(4, whole);
                createDriver.setString(5, otherInfo);
                createDriver.setInt(6, permitted);
                createDriver.setString(7, creation);
                createDriver.setString(8, modification);
                createDriver.setString(9, causes);

                result = createDriver.executeUpdate();
                //</editor-fold>
            } catch (SQLException ex) {
                if (ex.getErrorCode() == ER_DUP_ENTRY) {
                    duplicateCount++;
                } else {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                }
            } finally {
                if (result == 1) {
                    vehicleCount++;
                } else {
                    vehicleReject++;
                }  
                closeDBstuff(conn, createDriver, null, excepMsg);
            }
        }
    }

    int searchDriver(String name){
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        int seq = 0;
        try{
            String sql = "select SEQ_NO FROM cardriver where name = '"+ name +"'";
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sql);
            while(rs.next()){
                seq = rs.getInt("SEQ_NO");
            }
            
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "no Search name");
        }finally{
            closeDBstuff(conn, selectStmt, rs, "no Search name");
            return seq;
        }
    }

    void readDriverODS(Sheet sheet, ManageDrivers parentForm) {
        int driverCount = 0, driverReject = 0;
        int duplicateCount = 0;
        int numBlankRow = 0;

        for (int nRowIndex = 1; true; nRowIndex++)
        {   
            //<editor-fold defaultstate="collapsed" desc="-- finish loading or skip row">              
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    if (parentForm != null) {
                        parentForm.loadDriverData(0, "", "");
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    
                    switch (language) {
                        case KOREAN:
                            sb.append("자료 불러오기 결과");
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 불러온 운전자: " + driverCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 중복 자료: " + duplicateCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - 총 거부 자료: " + driverReject);
                            break;
                            
                        case ENGLISH:
                            sb.append("Sheet Loading Result");
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Loaded drivers: " + driverCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Duplicates rejected: " + duplicateCount);
                            sb.append(System.getProperty("line.separator"));
                            sb.append(" - Total rejected: " + driverReject);
                            break;
                            
                        default:
                            break;
                    }
                    
                    JOptionPane.showConfirmDialog(null, sb.toString(),
                            READ_ODS_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                               
                    return;                    
                } else {
                    continue;
                }
            }      
            //</editor-fold>
            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">  
            String driverName = (String)getCellValueAt(sheet, 0, nRowIndex).getValue();
            String cellPhone = (String)getCellValueAt(sheet, 1, nRowIndex).getValue();
            String landLine = (String)getCellValueAt(sheet, 2, nRowIndex).getValue();
            String L1Name = (String)getCellValueAt(sheet, 3, nRowIndex).getValue();
            String L2Name = (String)getCellValueAt(sheet, 4, nRowIndex).getValue();
            
            String L2NoStr = null;
            
            if (L1Name.length() > 0 && L2Name.length() > 0)
                L2NoStr = Integer.toString(getL2No(L1Name, L2Name));
            
            int buildingNo = 0;
            int unitNo = 0;
            String cellContent = getCellValueAt(sheet, 5, nRowIndex).getValue().toString();
            if (cellContent.length() > 0)
                buildingNo = Integer.parseInt(cellContent);
            
            cellContent = getCellValueAt(sheet, 6, nRowIndex).getValue().toString();
            if (cellContent.length() > 0)
                unitNo = Integer.parseInt(cellContent);
            
            String unitSeqNoStr = null;
            if (buildingNo != 0 && unitNo != 0)
                unitSeqNoStr = Integer.toString(getUnitSeqNo(buildingNo, unitNo));
            //</editor-fold>

            Connection conn = null;
            PreparedStatement createDriver = null;
            String excepMsg = "while creating car driver info for : " + driverName;
    
            int result = 0;
            try {
                //<editor-fold defaultstate="collapsed" desc="--Insert driver info into Database">  
                String sql = "Insert Into cardriver (name, CELLPHONE, PHONE, L2_NO" +
                                ", UNIT_SEQ_NO, CREATIONDATE)" + 
                                " Values (?, ?, ?, ?, ?, current_timestamp)";

                conn = getConnection();
                createDriver = conn.prepareStatement(sql);
                createDriver.setString(DriverCol.DriverName.getNumVal(), driverName);
                createDriver.setString(DriverCol.CellPhone.getNumVal(), cellPhone);
                createDriver.setString(DriverCol.LandLine.getNumVal(), landLine);
                createDriver.setString(DriverCol.AffiliationL2.getNumVal() - 1, L2NoStr);
                createDriver.setString(DriverCol.UnitNo.getNumVal() - 2, unitSeqNoStr);

                result = createDriver.executeUpdate();
                //</editor-fold>
            } catch (SQLException ex) {
                if (ex.getErrorCode() == ER_DUP_ENTRY) {
                    duplicateCount++;
                } else {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                }
            } finally {
                if (result == 1) {
                    driverCount++;
                } else {
                    driverReject++;
                }  
                closeDBstuff(conn, createDriver, null, excepMsg);
            }
        }
    }
    
    public boolean chekcVehiclesODS(Sheet sheet, ArrayList<Point> wrongCells, WrappedInt driverTotal){
        int numBlankRow = 0;
        MutableCell cell = null;
        
        loops:
        for (int nRowIndex = 1; true; nRowIndex++)
        {
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    break;
                } else {
                    continue;
                }
            }
            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">    
            for(int nColIndex =0 ; nColIndex < 15; nColIndex++)
            {
                cell = sheet.getCellAt(nColIndex, nRowIndex);
                Object cellObj = cell.getValue();
                int column = nColIndex;
                
                if (column == VehicleCol.PlateNumber.getNumVal()) {
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if (column == VehicleCol.Name.getNumVal()) {
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if(column == VehicleCol.Notification.getNumVal()){
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if(column == VehicleCol.Whole.getNumVal()){
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if(column == VehicleCol.Permitted.getNumVal()){
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if(column == VehicleCol.Creation.getNumVal()){
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if(column == VehicleCol.SeqNo.getNumVal()){
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                }
            }
            //</editor-fold>
            driverTotal.setValue(driverTotal.getValue() + 1);
        }
        
        
        
        if (wrongCells.size() > 0) {
            return false;
        }
        else {
            return true;
        }
    }
    
    boolean checkDriverODS(Sheet sheet, ArrayList<Point> wrongCells, WrappedInt driverTotal) {
        int numBlankRow = 0;
        MutableCell cell = null;
        
        loops:
        for (int nRowIndex = 1; true; nRowIndex++)
        {
            // check if column 1 and 2 are empty, if so, consider it a blank row
            if (aBlankRow(sheet, nRowIndex)) {
                numBlankRow++;
                if (numBlankRow > MAX_BLANK_ROW) {
                    break;
                } else {
                    continue;
                }
            }
            
            //<editor-fold defaultstate="collapsed" desc="-- Iterate through each column">    
            for(int nColIndex = 0; nColIndex < 7; nColIndex++)
            {
                cell = sheet.getCellAt(nColIndex, nRowIndex);
                Object cellObj = cell.getValue();
                int column = nColIndex + 1;
                
                if (column == DriverCol.DriverName.getNumVal()) {
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if (column == DriverCol.CellPhone.getNumVal()) {
                    if (cellObj.toString().trim().length() == 0) {
                        wrongCells.add(new Point(nRowIndex+1, column));
                    }
                } else if (column == DriverCol.AffiliationL2.getNumVal()) {
                    //<editor-fold defaultstate="collapsed" desc="-- check 2 affiliations">    
                    cell = sheet.getCellAt(nColIndex, nRowIndex);
                    cellObj = cell.getValue();   
                    String level2Name = cellObj.toString().trim();
                    
                    cell = sheet.getCellAt(nColIndex - 1, nRowIndex);
                    cellObj = cell.getValue();   
                    String level1Name = cellObj.toString().trim();
                    
                    if (level1Name.length() > 0 && level2Name.length() > 0) {
                        if (!existInDB(level1Name, level2Name)) {
                            wrongCells.add(new Point(nRowIndex+1, column - 1));                        
                            wrongCells.add(new Point(nRowIndex+1, column));                        
                        }
                    } else if (level1Name.length() > 0 || level2Name.length() > 0) {
                        wrongCells.add(new Point(nRowIndex+1, column - 1));                        
                        wrongCells.add(new Point(nRowIndex+1, column));  
                    }
                    //</editor-fold>
                } else if (column == DriverCol.UnitNo.getNumVal()) {
                    //<editor-fold defaultstate="collapsed" desc="-- check building and unit">    
                    cell = sheet.getCellAt(nColIndex, nRowIndex);
                    cellObj = cell.getValue();   
                    String unitNo = cellObj.toString().trim();
                    
                    cell = sheet.getCellAt(nColIndex - 1, nRowIndex);
                    cellObj = cell.getValue();   
                    String buildingNo = cellObj.toString().trim();
                    
                    if (buildingNo.length() > 0 && unitNo.length() > 0) {
                        if (!existInDB(Integer.parseInt(buildingNo), Integer.parseInt(unitNo))) {
                            wrongCells.add(new Point(nRowIndex+1, column - 1));                        
                            wrongCells.add(new Point(nRowIndex+1, column));                        
                        }
                    } else if (buildingNo.length() > 0 || unitNo.length() > 0) {
                        wrongCells.add(new Point(nRowIndex+1, column - 1));                        
                        wrongCells.add(new Point(nRowIndex+1, column));  
                    }
                    //</editor-fold>
                } 
            }
            //</editor-fold>
            driverTotal.setValue(driverTotal.getValue() + 1);
        }
        
        if (wrongCells.size() > 0) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean existInDB(String level1Name, String level2Name) {
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "checking DB existance of L1 name: " + level1Name + ", L2 name: " + level2Name;
        boolean result = false;

        try {
            StringBuffer sb = new StringBuffer("Select count(*)");
            sb.append(" from l1_affiliation l1, l2_affiliation l2");
            sb.append(" where l1.PARTY_NAME = ? and");
            sb.append("   l2.PARTY_NAME = ? and");
            sb.append("   l2.L1_NO = l1.L1_NO");
            
            conn = getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, level1Name);
            pstmt.setString(2, level2Name);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0)
                result = true;
        }
        
        catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }        
        
        return result;
    }

    private boolean existInDB(int buildingNo, int unitNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "checking DB existance of building: " + buildingNo + ", unit: " + unitNo;
        boolean result = false;

        try {
            StringBuffer sb = new StringBuffer("Select count(*)");
            sb.append(" from building_table bt, building_unit ut");
            sb.append(" where bt.bldg_no = ? and");
            sb.append("   ut.unit_no = ? and");
            sb.append("   ut.bldg_seq_no = bt.seq_no");
            
            conn = getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setInt(1, buildingNo);
            pstmt.setInt(2, unitNo);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0)
                result = true;
        }
        catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        }
        finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }        
        
        return result;
    }

    private MutableCell getCellValueAt(Sheet sheet, int nColIndex, int nRowIndex) {
        MutableCell cell = null;

        try {
            cell = sheet.getCellAt(nColIndex, nRowIndex);
        } catch (Exception e) {
            if (e.getClass() == ArrayIndexOutOfBoundsException.class)
            {
                logParkingException(Level.SEVERE, e, "wrong cell (row: " 
                        + nRowIndex + ", column: " + nColIndex);
            }
        } finally {
            return cell;
        }       
    }

    private int getL2No(String level1Name, String level2Name) {
        int result = 0;
        
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = " get L2 number for level 1: '" + level1Name + "', level 2: " + level2Name;

        try {
            StringBuffer sb = new StringBuffer("select L2_NO");
            sb.append(" from l2_affiliation L2, l1_affiliation L1");
            sb.append(" where L1.PARTY_NAME = ? and");
            sb.append("   L2.PARTY_NAME = ? and");
            sb.append("   L1.L1_NO = L2.L1_NO");
        
            conn = getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setString(1, level1Name);
            pstmt.setString(2, level2Name);
            rs = pstmt.executeQuery();
            if (rs.next())
                result = rs.getInt(1);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }        
        
        return result;        
    }

    private int getUnitSeqNo(int buildingNo, int unitNo) {        
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "getting Unit SeqNo for building: " + buildingNo + ", unit: " + unitNo;
        int result = 0;

        try 
        {
            StringBuffer sb = new StringBuffer();
            sb.append(" select ut.seq_no");
            sb.append(" from building_table bt, building_unit ut");
            sb.append(" where bt.bldg_no = ? and");
            sb.append("   ut.unit_no = ? and");
            sb.append("   ut.bldg_seq_no = bt.seq_no");
            
            conn = getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setInt(1, buildingNo);
            pstmt.setInt(2, unitNo);
            rs = pstmt.executeQuery();
            if (rs.next())
                result = rs.getInt(1);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }        
        
        return result;          
    }
}
