/*
 * Copyright (C) 2016 Open Source Parking, Inc.(www.osparking.com)
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
package com.osparking.vehicle;

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_NO;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_YES;
import static com.osparking.global.CommonData.DARK_BLUE;
import static com.osparking.global.CommonData.LIGHT_BLUE;
import static com.osparking.global.CommonData.ODS_DIRECTORY;
import static com.osparking.global.CommonData.adminOperationEnabled;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.buttonWidthWide;
import static com.osparking.global.CommonData.downloadSample;
import static com.osparking.global.CommonData.getStringWidth;
import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.numberCellRendererW;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.DataSheet.saveODSfileName;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.emptyLastRowPossible;
import static com.osparking.global.Globals.findLoginIdentity;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.insertBuilding;
import static com.osparking.global.Globals.insertUnit;
import static com.osparking.global.Globals.isManager;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.loginID;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.Globals.setComponentSize;
import com.osparking.global.IMainGUI;
import com.osparking.global.names.ControlEnums;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CANCEL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CREATE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.DELETE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.MODIFY_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.READ_ODS_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAMPLE2_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_ODS_BTN;
import com.osparking.global.names.ControlEnums.DialogMessages;
import static com.osparking.global.names.ControlEnums.DialogMessages.AFFILI_SAVE_ODS_FAIL_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.DUPLICATE_BUILDING;
import static com.osparking.global.names.ControlEnums.DialogMessages.DUPLICATE_UNIT;
import static com.osparking.global.names.ControlEnums.DialogMsg.UNIT_DEL_1;
import static com.osparking.global.names.ControlEnums.DialogMsg.UNIT_DEL_2;
import static com.osparking.global.names.ControlEnums.DialogMsg.UNIT_DEL_FAIL;
import static com.osparking.global.names.ControlEnums.DialogMsg.UNIT_DEL_RES_1;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ODS_CHECK_RESULT_TITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.FormMode.CreateMode;
import static com.osparking.global.names.ControlEnums.FormMode.NormalMode;
import static com.osparking.global.names.ControlEnums.FormMode.UpdateMode;
import static com.osparking.global.names.ControlEnums.FormModeString.CREATE;
import static com.osparking.global.names.ControlEnums.FormModeString.FETCH;
import static com.osparking.global.names.ControlEnums.FormModeString.MODIFY;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BUILDING_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_AFFIL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.UNIT_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_3;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DELETE_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DELETE_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DEL_FAIL;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DEL_RESULT;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DIAG_L21;
import static com.osparking.global.names.ControlEnums.OsPaLang.ENGLISH;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILDING_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILD_ROOM_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ROOM_HEADER;
import static com.osparking.global.names.ControlEnums.TitleTypes.BUILDING_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.ControlEnums.ToolTipContent.NUMBER_FORMAT_ERROR_MSG;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.WrappedInt;
import static com.osparking.vehicle.CommonData.setHelpDialogLoc;
import static com.osparking.vehicle.CommonData.tableColumnLanguage;
import static com.osparking.vehicle.CommonData.wantToSaveFile;
import com.osparking.vehicle.driver.ODSReader;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class Buildings extends javax.swing.JFrame {
    private boolean lev_Editable[] = {false, false, false};
    private int currLevel = 1;
    private ControlEnums.FormMode formMode = NormalMode;
    IMainGUI mainForm = null;
    private boolean isStand_Alone = false;
    
    static Object prevUnitNo = null;
    static Object prevBldgNo = null;

    /**
     * Creates new form Affiliations
     */
    public Buildings(IMainGUI mainForm) {
        initComponents();
        setIconImages(OSPiconList);
        
        this.mainForm = mainForm;
        if (mainForm == null) {
            isStand_Alone = true;
        }        
           
        /**
         * Set default input language to Korean for 2 affiliation tables.
         */
        tableColumnLanguage(BuildingTable, 1, ENGLISH);
        tableColumnLanguage(UnitTable, 1, ENGLISH);        
        
        /**
         * Add table model listener for insertion(=creation) or update completion.
         */
        addTableListener(BuildingTable, prevBldgNo, "BLDG_NO", DUPLICATE_BUILDING);
        addTableListener(UnitTable, prevUnitNo, "UNIT_NO", DUPLICATE_UNIT);

        adjustTableDimension(BuildingTable);
        adjustTableDimension(UnitTable);
        addRowSelectionListener(BuildingTable);      
        addRowSelectionListener(UnitTable);  
        
        addHeaderMouseListener(BuildingTable);
        addHeaderMouseListener(UnitTable);
        
        setComponentSize(insertSaveButton, new Dimension(buttonWidthNorm, buttonHeightNorm));
        
        loadBuilding(0, 0);
        setFormMode(NormalMode);
        changeItemsEnabled(BuildingTable, true);
    }

    /**
     * 
     * @param viewIndex
     * @param bldgNo building number of the row to highlight(select)
     */
    public void loadBuilding(int viewIndex, int bldgNo) {
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(upload building and unit number list)";
                
        DefaultTableModel model = (DefaultTableModel) BuildingTable.getModel();
        int model_Index = 0;            
        try {
            // <editor-fold defaultstate="collapsed" desc="-- Load building number list">
            conn = getConnection();
            selectStmt = conn.createStatement();
            
            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT @ROWNUM := @ROWNUM + 1 AS recNo, BLDG_NO, SEQ_NO");
            sb.append(" FROM Building_Table, (SELECT @rownum := 0) r ");
            sb.append(" ORDER BY BLDG_NO");
            
            rs = selectStmt.executeQuery(sb.toString());

            model.setRowCount(0);
            while (rs.next()) {
                if (viewIndex == HIGHLIGHT_NEW) { // the case of a new building creation
                    if (bldgNo == rs.getInt("BLDG_NO")) {
                        model_Index = model.getRowCount();
                    }
                }
                model.addRow(new Object[] {rs.getInt("recNo"),  rs.getInt("BLDG_NO"), rs.getInt("SEQ_NO")});
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, selectStmt, rs, excepMsg);
        }
        // <editor-fold defaultstate="collapsed" desc="-- highlight a building and load its room list">
        int numRows = model.getRowCount();
        if (numRows > 0)
        {
            if (viewIndex == HIGHLIGHT_NEW) { // handle the case of a new building creation
                viewIndex = BuildingTable.convertRowIndexToView(model_Index);
            } else if (viewIndex == numRows) {
                // "number of remaining rows == deleted row index" means
                // the row deleted was the last row
                // In this case, highlight the previous row      
                viewIndex--;
            }
            
            final int highRow = viewIndex;
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    highlightTableRow(BuildingTable, highRow);
                }
            });
            
            // Adjust first column width checking row count, etc.
            adjustColumnWidth(BuildingTable, numRows);
            
            if (isManager) {
                saveSheet_Button.setEnabled(true);
            }              
        } else {
            loadUnit(0, null, 0, 0);
        }
        bldg_Count.setText(Integer.toString(numRows));
        fixButtonEnabled();
        //</editor-fold>
    }    
    
    /**
     * Load unit numbers from DB and use them to refresh the unit_Table.
     * @param bldgNo human readable building number
     * @param bldg_seq_no DB key for the building number
     * @param viewIndex row index to highlight on the list
     * @param unitNo human readable unit number
     */
    private void loadUnit(int bldgNo, Object bldg_seq_no, int viewIndex, int unitNo) 
    {
        if (bldg_seq_no == null)
        {
            Unit_Title.setText(UNIT_LABEL.getContent());
            ((DefaultTableModel) UnitTable.getModel()).setRowCount(0);
        } else {
            String label = null;
            
            if (language == Locale.KOREAN) {
                label = bldgNo + UNIT_LABEL.getContent();
            } else if (language == Locale.ENGLISH) {
                label = UNIT_LABEL.getContent() + bldgNo; 
            }              
            
            Unit_Title.setText(label); 
            
            Connection conn = null;
            Statement selectStmt = null;
            ResultSet rs = null;
            String excepMsg = "refresh unit list that belongs to building no: " + bldgNo;
                
            DefaultTableModel model = (DefaultTableModel) UnitTable.getModel();
            int model_index = -1;    
            
            // List up room numbers on the table for room numbers
            try {            
                // <editor-fold defaultstate="collapsed" desc="-- Load Room Numbers of a Building">                
                StringBuffer sb = new StringBuffer();
                sb.append(" SELECT @ROWNUM := @ROWNUM + 1 AS recNo, ");
                sb.append("   UNIT_NO, SEQ_NO");
                sb.append(" FROM BUILDING_UNIT, (SELECT @rownum := 0) r");
                sb.append(" WHERE BLDG_SEQ_NO = " + bldg_seq_no.toString());
                sb.append(" ORDER BY UNIT_NO");

                conn = getConnection();
                selectStmt = conn.createStatement();
                rs = selectStmt.executeQuery(sb.toString());
                model.setRowCount(0);
                while (rs.next()) {
                    if (viewIndex == HIGHLIGHT_NEW) { // Handle the case of a new Unit creation
                        if (unitNo == rs.getInt("UNIT_NO")) {
                            model_index = model.getRowCount();
                        }
                    }                    
                    model.addRow(new Object[] 
                        {rs.getInt("recNo"),  rs.getInt("UNIT_NO"), rs.getInt("SEQ_NO")});
                }
                //</editor-fold>
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, selectStmt, rs, excepMsg);
            }
                
            // "number of remaining rows == deleted row index" means the row deleted was the last row
            // In this case, highlight the previous row               
            int numRows = model.getRowCount();
            if (numRows > 0) {
                if (viewIndex == -1) { // handle the case of new room creation
                    viewIndex = UnitTable.convertRowIndexToView(model_index);
                } else if (viewIndex == numRows) { 
                    viewIndex--; // when the row deleted was the last row
                }
                
                final int highRow = viewIndex;
                if (viewIndex != NO_HIGHLIGHT) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            highlightTableRow(UnitTable, highRow);
                        }
                    });                    
                }
                adjustColumnWidth(UnitTable, numRows);                
            }
            unit_Count.setText(Integer.toString(numRows));
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fixButtonEnabled();
            }
        });        
    }    
    
    /**
     * Check if the string at 1st column of the hightlighted row is OK.
     * @return 
     */
    private IntCheckResult checkBldgUnitNumber(JTable table) {
        if (table.getSelectedRow() == -1) {
            return new IntCheckResult(true, null, 0);
        }        
        
        int rowIndex = table.convertRowIndexToModel (table.getSelectedRow());
        TableModel model = table.getModel();
        String inputStr = (String)(model.getValueAt(rowIndex, 1));
        
        Integer bunInteger = tryParseInt(inputStr);
        
        if (bunInteger == null) {
//            abortInsertion(NUMBER_FORMAT_ERROR_MSG.getContent(), table);
            return new IntCheckResult(true, NUMBER_FORMAT_ERROR_MSG.getContent(), 0);
        }  
//        
//        if (bunInteger == null) {
//            DialogMessages message = 
//                    (table == BuildingTable ? EMPTY_BUILDING : EMPTY_UNIT);
////            abortInsertion(message.getContent(), table);
//            return new IntCheckResult(true, NUMBER_FORMAT_ERROR_MSG.getContent(), 0);
//        }   
        return new IntCheckResult(false, null, bunInteger);
    }
    
    private void insertBldgUnit(JTable table, ControlEnums.DialogMessages diagMsg) 
    {
        //if (model.getValueAt(rowIndex, 0) == null) {                     
        IntCheckResult checkResult = checkBldgUnitNumber(table);
        Integer bunInteger;
        
        if (checkResult.isBadFormat()) {
            abortInsertion(checkResult.getAbortMsg(), table);
            return;
        } else {
            bunInteger = checkResult.value;
        }
        
        // <editor-fold defaultstate="collapsed" desc="-- Insert New Higher name and Refresh the List">
        int result = ER_YES;
        TableModel bModel = BuildingTable.getModel();
        int bIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
        Object bldgSeqNoObj = bModel.getValueAt(bIndex, 2);

        if (currLevel == 1) {
            result = insertBuilding(bunInteger);
        } else {
            result = insertUnit(bunInteger, (Integer)bldgSeqNoObj);                    
        }

        if (result == ER_NO) {
            setFormMode(FormMode.NormalMode);
            insertSaveButton.setText(CREATE_BTN.getContent());
            insertSaveButton.setMnemonic('R');                        

            setLev_Editable(currLevel, false);
            table.getCellEditor().stopCellEditing();
            if (currLevel == 1) {
                changeItemsEnabled(BuildingTable, bldg_Radio.isSelected());
                loadBuilding(HIGHLIGHT_NEW, bunInteger);
            } else {
                int bldgNo = (Integer)bModel.getValueAt(bIndex, 1);

                changeItemsEnabled(UnitTable, lev2_Radio.isSelected());
                loadUnit(bldgNo, bldgSeqNoObj, HIGHLIGHT_NEW, bunInteger);
            }
        } else if (result == ER_DUP_ENTRY) {
            abortInsertion(bunInteger + diagMsg.getContent(), table);
        }
        // </editor-fold>
    }    

    private void abortModification(DialogMessages dialog, String name, int row, JTable table) {
        abortModification(dialog.getContent() + name, row, table);
    }

    private void abortModification(DialogMessages dialog, int row, JTable table) {
        abortModification(dialog.getContent(), row, table);
    }
    
    private void abortModification(String message, int row, JTable table) {
        JOptionPane.showMessageDialog(this, message, ERROR_DIALOGTITLE.getContent(), 
                JOptionPane.WARNING_MESSAGE); 
        backToNormal(table);
        if (table == BuildingTable) {
            loadBuilding(row, 0);
        } else {
            int bIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
            int bldgNo = (Integer)BuildingTable.getModel().getValueAt(bIndex, 1);
            Object bldgSeqNoObj = BuildingTable.getModel().getValueAt(bIndex, 2);               
            
            loadUnit(bldgNo, bldgSeqNoObj, row, 0);
        }
    }
    
    private void updateBldgUnit(JTable buTable, Object prevAffiliName, 
            String updateCol, ControlEnums.DialogMessages msgForDuplicate) 
    {
        // <editor-fold defaultstate="collapsed" desc="-- Update high affiliation name">
        int rowIndex = buTable.convertRowIndexToModel (buTable.getSelectedRow());
        TableModel model = buTable.getModel();
        IntCheckResult checkResult = checkBldgUnitNumber(buTable);
        Integer bunInteger;
        
        if (checkResult.isBadFormat()) {
            abortModification(checkResult.getAbortMsg(), rowIndex, buTable);
            return;
        } else {
            bunInteger = checkResult.value;
        }        
        
        Object keyVal = model.getValueAt(rowIndex, 2);
        int result = 0;
        Connection conn = null;
        PreparedStatement pUpdateStmt = null;
        
        try {
            String sql = "Update " + buTable.getName() + " Set " + updateCol + 
                    " = ? Where SEQ_NO = ?";

            conn = getConnection();
            pUpdateStmt = conn.prepareStatement(sql);
            pUpdateStmt.setInt(1, bunInteger);
            pUpdateStmt.setInt(2, (Integer)keyVal);
            result = pUpdateStmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                abortModification(bunInteger.toString() + msgForDuplicate.getContent(), 
                        rowIndex, buTable);
                return;
            }
        } finally {
            closeDBstuff(conn, pUpdateStmt, null, 
                    "(Original " + buTable.getName() + "name : " + prevAffiliName + ")");
        }    
        if (result == 1) {
            backToNormal(buTable);

            if (buTable == BuildingTable) {
                loadBuilding(HIGHLIGHT_NEW, bunInteger);
            } else {
                int bIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
                int bldgNo = (Integer)BuildingTable.getModel().getValueAt(bIndex, 1);
                Object bldgSeqNoObj = BuildingTable.getModel().getValueAt(bIndex, 2);                
                
                loadUnit(bldgNo, bldgSeqNoObj, -1, bunInteger);
            }
        }         
    }
        
    private void adjustTableDimension(JTable AffiliationTable) {
        
        // Hide affiliation number field which is used internally.
        TableColumnModel BelongModel = AffiliationTable.getColumnModel();
        BelongModel.removeColumn(BelongModel.getColumn(2));
        
        // Decrease the first column width
        TableColumn column = AffiliationTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(60); //row number column is narrow
        column.setMinWidth(60); //row number column is narrow
        column.setMaxWidth(10000); //row number column is narrow
        
        column = AffiliationTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(130); //row number column is narrow
        column.setMinWidth(130); //row number column is narrow
        column.setMaxWidth(30000); //row number column is narrow        
    }     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        affi_Group = new javax.swing.ButtonGroup();
        saveFileChooser = new javax.swing.JFileChooser();
        odsFileChooser = new javax.swing.JFileChooser();
        topPanel = new javax.swing.JPanel();
        GUI_Title_Panel = new javax.swing.JPanel();
        workPanel = new javax.swing.JLabel();
        modeString = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        westPanel = new javax.swing.JPanel();
        wholePanel = new javax.swing.JPanel();
        levs_Panel = new javax.swing.JPanel();
        lev1_Panel = new javax.swing.JPanel();
        title1_Panel = new javax.swing.JPanel();
        Building_Title = new javax.swing.JLabel();
        count1_Panel = new javax.swing.JPanel();
        countLabel = new javax.swing.JLabel();
        bldg_Count = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        BuildingTable = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return lev_Editable[currLevel];
            }
        }
        ;
        radio1Panel = new javax.swing.JPanel();
        bldg_Radio = new javax.swing.JRadioButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        lev2_Panel = new javax.swing.JPanel();
        title2_Panel = new javax.swing.JPanel();
        Unit_Title = new javax.swing.JLabel();
        count2_Panel = new javax.swing.JPanel();
        countLabel1 = new javax.swing.JLabel();
        unit_Count = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        UnitTable = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return lev_Editable[currLevel];
            }
        }
        ;
        radio2Panel = new javax.swing.JPanel();
        lev2_Radio = new javax.swing.JRadioButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 12), new java.awt.Dimension(0, 12), new java.awt.Dimension(32767, 12));
        recordMenuPanel = new javax.swing.JPanel();
        insertSaveButton = new javax.swing.JButton();
        updateSaveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jSeparator1 = new javax.swing.JSeparator();
        southPanel = new javax.swing.JPanel();
        tableMenuPanel = new javax.swing.JPanel();
        sampleButton = new javax.swing.JButton();
        ODSAffiliHelp = new javax.swing.JButton();
        readSheet = new javax.swing.JButton();
        saveSheet_Button = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        eastPanel = new javax.swing.JPanel();

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        saveFileChooser.setFileFilter(new OdsFileOnly());

        odsFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        odsFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(BUILDING_FRAME_TITLE.getContent());
        setMinimumSize(new java.awt.Dimension(560, 609));

        topPanel.setMinimumSize(new java.awt.Dimension(530, 76));
        topPanel.setPreferredSize(new java.awt.Dimension(530, 76));

        GUI_Title_Panel.setPreferredSize(new java.awt.Dimension(419, 36));

        workPanel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        workPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        workPanel.setText(MODE_LABEL.getContent());
        workPanel.setMaximumSize(new java.awt.Dimension(140, 28));
        workPanel.setMinimumSize(new java.awt.Dimension(50, 26));
        workPanel.setPreferredSize(new java.awt.Dimension(90, 26));
        GUI_Title_Panel.add(workPanel);

        modeString.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modeString.setForeground(tipColor);
        modeString.setText("조회");
        modeString.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        modeString.setMaximumSize(new java.awt.Dimension(200, 28));
        modeString.setMinimumSize(new java.awt.Dimension(40, 26));
        modeString.setPreferredSize(new java.awt.Dimension(40, 26));
        GUI_Title_Panel.add(modeString);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGap(287, 287, 287)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(253, Short.MAX_VALUE))
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(topPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(GUI_Title_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 36, Short.MAX_VALUE))
            .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                    .addGap(0, 40, Short.MAX_VALUE)
                    .addComponent(GUI_Title_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        westPanel.setPreferredSize(new java.awt.Dimension(40, 0));

        javax.swing.GroupLayout westPanelLayout = new javax.swing.GroupLayout(westPanel);
        westPanel.setLayout(westPanelLayout);
        westPanelLayout.setHorizontalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        westPanelLayout.setVerticalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
        );

        getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

        wholePanel.setMinimumSize(new java.awt.Dimension(460, 355));
        wholePanel.setPreferredSize(new java.awt.Dimension(460, 365));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.Y_AXIS));

        levs_Panel.setMinimumSize(new java.awt.Dimension(457, 270));
        levs_Panel.setPreferredSize(new java.awt.Dimension(460, 315));
        levs_Panel.setLayout(new javax.swing.BoxLayout(levs_Panel, javax.swing.BoxLayout.X_AXIS));

        lev1_Panel.setMinimumSize(new java.awt.Dimension(200, 214));
        lev1_Panel.setPreferredSize(new java.awt.Dimension(220, 340));
        lev1_Panel.setLayout(new javax.swing.BoxLayout(lev1_Panel, javax.swing.BoxLayout.Y_AXIS));

        title1_Panel.setMaximumSize(new java.awt.Dimension(32767, 25));
        title1_Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                title1_PanelMouseClicked(evt);
            }
        });
        title1_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        Building_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        Building_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Building_Title.setText(BUILDING_LIST_LABEL.getContent());
        title1_Panel.add(Building_Title);

        lev1_Panel.add(title1_Panel);

        count1_Panel.setMaximumSize(new java.awt.Dimension(32767, 20));
        count1_Panel.setMinimumSize(new java.awt.Dimension(86, 20));
        count1_Panel.setPreferredSize(new java.awt.Dimension(86, 20));
        count1_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        countLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLabel.setText(COUNT_LABEL.getContent());
        count1_Panel.add(countLabel);

        bldg_Count.setForeground(pointColor);
        bldg_Count.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        bldg_Count.setText("0");
        count1_Panel.add(bldg_Count);

        lev1_Panel.add(count1_Panel);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 200));

        BuildingTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BuildingTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BuildingTable.setModel(
            new javax.swing.table.DefaultTableModel(
                new Object [][] {{1, 101, 5}, {2, 102, 6}},
                new String [] {
                    ORDER_HEADER.getContent(), BUILDING_HEADER.getContent(), "SEQ_NO"}
            )
            {  @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return Integer.class;
                }
            }
        );
        ((DefaultTableCellRenderer)BuildingTable.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        BuildingTable.getColumnModel().getColumn(0).setCellRenderer(numberCellRenderer);
        BuildingTable.getColumnModel().getColumn(1).setCellRenderer(numberCellRendererW);
        BuildingTable.setEnabled(false);
        BuildingTable.setName("Building_Table"); // NOI18N
        BuildingTable.setRowHeight(tableRowHeight);
        BuildingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BuildingTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(BuildingTable);

        lev1_Panel.add(jScrollPane1);

        radio1Panel.setMaximumSize(new java.awt.Dimension(32767, 31));
        radio1Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radio1PanelMouseClicked(evt);
            }
        });

        affi_Group.add(bldg_Radio);
        bldg_Radio.setSelected(true);
        bldg_Radio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                bldg_RadioItemStateChanged(evt);
            }
        });
        radio1Panel.add(bldg_Radio);

        lev1_Panel.add(radio1Panel);

        levs_Panel.add(lev1_Panel);
        levs_Panel.add(filler4);

        lev2_Panel.setMinimumSize(new java.awt.Dimension(200, 214));
        lev2_Panel.setPreferredSize(new java.awt.Dimension(220, 340));
        lev2_Panel.setLayout(new javax.swing.BoxLayout(lev2_Panel, javax.swing.BoxLayout.Y_AXIS));

        title2_Panel.setMaximumSize(new java.awt.Dimension(32767, 25));
        title2_Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                title2_PanelMouseClicked(evt);
            }
        });
        title2_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        Unit_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        Unit_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Unit_Title.setText(UNIT_LABEL.getContent());
        title2_Panel.add(Unit_Title);

        lev2_Panel.add(title2_Panel);

        count2_Panel.setMaximumSize(new java.awt.Dimension(32767, 20));
        count2_Panel.setMinimumSize(new java.awt.Dimension(86, 20));
        count2_Panel.setPreferredSize(new java.awt.Dimension(86, 20));
        count2_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        countLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLabel1.setText(COUNT_LABEL.getContent());
        count2_Panel.add(countLabel1);

        bldg_Count.setForeground(pointColor);
        unit_Count.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        unit_Count.setForeground(pointColor);
        unit_Count.setText("0");
        count2_Panel.add(unit_Count);

        lev2_Panel.add(count2_Panel);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(454, 200));

        UnitTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        UnitTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        UnitTable.setModel(
            new javax.swing.table.DefaultTableModel(
                new Object [][] {{1, 803, 1}, {2, 805, 2}},
                new String [] {
                    ORDER_HEADER.getContent(), ROOM_HEADER.getContent(), "SEQ_NO"})
            {  @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return Integer.class;
                }
            }
        );
        ((DefaultTableCellRenderer)UnitTable.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        UnitTable.setEnabled(false);
        UnitTable.getColumnModel().getColumn(0).setCellRenderer(numberCellRenderer);
        UnitTable.getColumnModel().getColumn(1).setCellRenderer(numberCellRendererW);
        UnitTable.setName("Building_Unit"); // NOI18N
        UnitTable.setRowHeight(tableRowHeight);
        UnitTable.setSelectionBackground(DARK_BLUE);
        UnitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UnitTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                UnitTableFocusLost(evt);
            }
        });
        UnitTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UnitTableMouseClicked(evt);
            }
        });
        UnitTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                UnitTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(UnitTable);

        lev2_Panel.add(jScrollPane2);

        radio2Panel.setMaximumSize(new java.awt.Dimension(32767, 31));
        radio2Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radio2PanelMouseClicked(evt);
            }
        });

        affi_Group.add(lev2_Radio);
        lev2_Radio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lev2_RadioItemStateChanged(evt);
            }
        });
        radio2Panel.add(lev2_Radio);

        lev2_Panel.add(radio2Panel);

        levs_Panel.add(lev2_Panel);

        wholePanel.add(levs_Panel);
        wholePanel.add(filler6);

        recordMenuPanel.setMaximumSize(new java.awt.Dimension(32767, 35));
        recordMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        insertSaveButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        insertSaveButton.setMnemonic('R');
        insertSaveButton.setText(CREATE_BTN.getContent());
        insertSaveButton.setEnabled(false);
        insertSaveButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSaveButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSaveButton.setPreferredSize(new java.awt.Dimension(80, 25));
        insertSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSaveButtonActionPerformed(evt);
            }
        });
        recordMenuPanel.add(insertSaveButton);

        updateSaveButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        updateSaveButton.setMnemonic('M');
        updateSaveButton.setText(MODIFY_BTN.getContent());
        updateSaveButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        updateSaveButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        updateSaveButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        updateSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateSaveButtonActionPerformed(evt);
            }
        });
        recordMenuPanel.add(updateSaveButton);

        deleteButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteButton.setMnemonic('D');
        deleteButton.setText(DELETE_BTN.getContent());
        deleteButton.setEnabled(false);
        deleteButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        recordMenuPanel.add(deleteButton);

        cancelButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cancelButton.setMnemonic('C');
        cancelButton.setText(CANCEL_BTN.getContent());
        cancelButton.setEnabled(false);
        cancelButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        recordMenuPanel.add(cancelButton);

        wholePanel.add(recordMenuPanel);
        wholePanel.add(filler5);

        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 40));
        jSeparator1.setMinimumSize(new java.awt.Dimension(50, 40));
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 40));
        wholePanel.add(jSeparator1);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        southPanel.setMinimumSize(new java.awt.Dimension(530, 80));
        southPanel.setPreferredSize(new java.awt.Dimension(10, 80));
        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.Y_AXIS));

        tableMenuPanel.setMaximumSize(new Dimension(4000, buttonHeightNorm));
        tableMenuPanel.setMinimumSize(new Dimension(150, buttonHeightNorm));
        tableMenuPanel.setPreferredSize(new Dimension(300, buttonHeightNorm));
        tableMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        sampleButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        sampleButton.setMnemonic('S');
        sampleButton.setText(SAMPLE2_BTN.getContent());
        sampleButton.setToolTipText(DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD.getContent());
        sampleButton.setEnabled(false);
        sampleButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        sampleButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        sampleButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        sampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleButtonActionPerformed(evt);
            }
        });
        tableMenuPanel.add(sampleButton);

        ODSAffiliHelp.setBackground(new java.awt.Color(153, 255, 153));
        ODSAffiliHelp.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        ODSAffiliHelp.setIcon(getQuest20_Icon());
        ODSAffiliHelp.setAlignmentY(0.0F);
        ODSAffiliHelp.setMargin(new java.awt.Insets(2, 4, 2, 4));
        ODSAffiliHelp.setMinimumSize(new java.awt.Dimension(20, 20));
        ODSAffiliHelp.setOpaque(false);
        ODSAffiliHelp.setPreferredSize(new java.awt.Dimension(25, 25));
        ODSAffiliHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ODSAffiliHelpActionPerformed(evt);
            }
        });
        tableMenuPanel.add(ODSAffiliHelp);

        readSheet.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet.setMnemonic('O');
        readSheet.setText(READ_ODS_BTN.getContent());
        readSheet.setToolTipText("");
        readSheet.setEnabled(false);
        readSheet.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheetActionPerformed(evt);
            }
        });
        tableMenuPanel.add(readSheet);

        saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveSheet_Button.setMnemonic('A');
        saveSheet_Button.setText(SAVE_ODS_BTN.getContent());
        saveSheet_Button.setEnabled(false);
        saveSheet_Button.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSheet_ButtonActionPerformed(evt);
            }
        });
        tableMenuPanel.add(saveSheet_Button);
        tableMenuPanel.add(filler3);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText(CLOSE_BTN.getContent());
        closeFormButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        tableMenuPanel.add(closeFormButton);

        southPanel.add(tableMenuPanel);
        southPanel.add(filler1);

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        eastPanel.setPreferredSize(new java.awt.Dimension(40, 0));

        javax.swing.GroupLayout eastPanelLayout = new javax.swing.GroupLayout(eastPanel);
        eastPanel.setLayout(eastPanelLayout);
        eastPanelLayout.setHorizontalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        eastPanelLayout.setVerticalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
        );

        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void insertSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSaveButtonActionPerformed
        if (bldg_Radio.isSelected()) {
            if (formMode == NormalMode) {
                createBldgUnit(BuildingTable);
            } else {
                BuildingTable.getCellEditor().stopCellEditing();                
            }
        } else {
            if (formMode == NormalMode) {
                createBldgUnit(UnitTable);
            } else {
                UnitTable.getCellEditor().stopCellEditing();                
            }
        }
    }//GEN-LAST:event_insertSaveButtonActionPerformed

    private void updateSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateSaveButtonActionPerformed
        try {
            switch (formMode) {
                case NormalMode:
                    // <editor-fold defaultstate="collapsed" desc="-- Prepare to update user information">
                    if (currLevel == 1)
                        startModify(BuildingTable);
                    else 
                        startModify(UnitTable);
                    // </editor-fold>                
                    break;
                case UpdateMode:
                    // <editor-fold defaultstate="collapsed" desc="-- save modified user information ">
                    if (currLevel == 1)
                        BuildingTable.getCellEditor().stopCellEditing();
                    else 
                        UnitTable.getCellEditor().stopCellEditing();
                    // </editor-fold>                
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                        "(User requested command: " + evt.getActionCommand() + ")");             
        }        
    }//GEN-LAST:event_updateSaveButtonActionPerformed

    private void startModify(JTable table) {
        setFormMode(FormMode.UpdateMode);
        /**
         * Change management button enabled properties.
         */
        updateSaveButton.setText(SAVE_BTN.getContent());
        updateSaveButton.setMnemonic('s');            

        int rowIndex = table.getSelectedRow();
        int model_index = table.convertRowIndexToModel(rowIndex);
        TableModel model = table.getModel();

        if (currLevel == 1) {
            prevBldgNo = model.getValueAt(model_index, 1);
        } else {
            prevUnitNo = model.getValueAt(model_index, 1);
        }

        setLev_Editable(currLevel, true);
        if (table.editCellAt(rowIndex, 1)) {
            table.getEditorComponent().requestFocus();
        }
    }

    private void enableRadioButtons(boolean b) {
        bldg_Radio.setEnabled(b);
        lev2_Radio.setEnabled(b);
    }    
    
    /**
     * @param formMode the formMode to set
     */
    public void setFormMode(ControlEnums.FormMode formMode) {
        this.formMode = formMode;
        
        switch (formMode) {
            case CreateMode:
                modeString.setText(CREATE.getContent());
                enableRadioButtons(false);
                changeBottomButtonsEnbled(false);   
                adminOperationEnabled(false, ODSAffiliHelp, sampleButton, readSheet);                
                break;   
                
            case NormalMode:               
                modeString.setText(FETCH.getContent());
                enableRadioButtons(true);
                changeBottomButtonsEnbled(true);
                adminOperationEnabled(true, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            case UpdateMode:
                modeString.setText(MODIFY.getContent());
                enableRadioButtons(false);
                changeBottomButtonsEnbled(false);   
                adminOperationEnabled(false, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            default:
                break;
        }
        fixButtonEnabled();        
    }    

    private void changeBottomButtonsEnbled(boolean b) {
        if (isManager) {
            saveSheet_Button.setEnabled(b);
            readSheet.setEnabled(b);
        } else {
            saveSheet_Button.setEnabled(false);
            readSheet.setEnabled(false);
        }
        closeFormButton.setEnabled(b);
    }    
    
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // Delete currently selected higher affiliation
        JTable table;
        
        if (currLevel == 1) {
            table = BuildingTable;
        } else {
            table = UnitTable;
        }
            
        int viewIndex = table.getSelectedRow();
        
        if (viewIndex == -1) {
            return;
        } else {            
            int modal_Index = table.convertRowIndexToModel(viewIndex);
            int bu_No = (Integer)table.getModel().getValueAt(modal_Index, 1);
            int bu_seq_no = (Integer)table.getModel().getValueAt(modal_Index, 2);
            
            String message = null;
            
            if (currLevel == 1) {
                int count = getUnitCount(bu_seq_no);

                message = BLDG_DELETE_L1.getContent() + System.getProperty("line.separator") 
                        + BLDG_DIAG_L2.getContent() + bu_No + BLDG_DELETE_L3.getContent() 
                        + count + ")";
            } else {
                message = UNIT_DEL_1.getContent() + System.getProperty("line.separator") 
                        + UNIT_DEL_2.getContent() + bu_No;            
            }
            
            int result = JOptionPane.showConfirmDialog(this, message,
                    DELETE_DIALOGTITLE.getContent(), JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                //<editor-fold desc="delete upper level affliation (unit name)">
                String excepMsg = null;
                String sql = null;
                
                if (currLevel == 1) {
                    excepMsg = "(while deleting building No: " + bu_No + ")";
                    sql = "Delete From BUILDING_TABLE Where SEQ_NO = ?";
                } else {
                    excepMsg = "(failed deletion of unit no: " + bu_No + ")";
                    sql = "Delete From BUILDING_UNIT Where SEQ_NO = ?";
                }
                
                result = deleteBldgUnit(excepMsg, sql, bu_seq_no);

                if (result == 1) {
                    if (currLevel == 1) {
                        loadBuilding(viewIndex, 0); // Deliver the index of deleted row
                        message = BLDG_DEL_RESULT.getContent()
                                +  System.getProperty("line.separator") 
                                + BLDG_DIAG_L2.getContent() + bu_No;

                        JOptionPane.showMessageDialog(this, message,
                                    DELETE_RESULT_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE);
                    } else {
                        int bIndex = BuildingTable.convertRowIndexToView(
                                BuildingTable.getSelectedRow());
                        int bldgNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 1));  
                        int bldgSeqNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 2));
                        
                        loadUnit(bldgNo, bldgSeqNo, modal_Index, bu_No);
                        message = UNIT_DEL_RES_1.getContent()
                                + System.getProperty("line.separator") 
                                + UNIT_DEL_2.getContent() + bu_No;

                        JOptionPane.showMessageDialog(this, message,
                                DELETE_RESULT_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE);   
                    }
                } else {
                    if (currLevel == 1) {
                        message = BLDG_DEL_FAIL.getContent()
                                + System.getProperty("line.separator") 
                                + BLDG_DIAG_L21.getContent() + bu_No;
                    } else {
                        message = UNIT_DEL_FAIL.getContent() 
                                + System.getProperty("line.separator") 
                                + UNIT_DEL_2.getContent() + bu_No;
                    }

                    JOptionPane.showMessageDialog(this, message, 
                            DELETE_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE);                       
                }
                //</editor-fold>
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed
    
    private int getUnitCount(int bldg_seq_no ) {
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "get count of units that belong to a buliding (no: " + bldg_seq_no + ")";
        
        int result = -1;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("Select count(*) from BUILDING_UNIT where BLDG_SEQ_NO = ?");
            pstmt.setInt(1, bldg_seq_no);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getInt(1);
        }
        catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }
        return result;
    }    
    
    // Delete currently selected table row
    private int deleteBldgUnit(String excepMsg, String sql, int bu_seq_no) 
    {
        //<editor-fold desc="-- Actual deletion of a building number">
        Connection conn = null;
        PreparedStatement createBuilding = null;
        int result = 0;
        
        try {
            conn = getConnection();
            createBuilding = conn.prepareStatement(sql);
            createBuilding.setInt(1, bu_seq_no);

            result = createBuilding.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, createBuilding, null, excepMsg);
            return result;
        }    
        //</editor-fold>        
    }    
    
    // Delete currently selected table row
    private int deleteAffiliation(String excepMsg, String sql, int key_No) 
    {
        //<editor-fold desc="-- Actual deletion of a building number">
        Connection conn = null;
        PreparedStatement createBuilding = null;
        int result = 0;
        
        try {
            conn = getConnection();
            createBuilding = conn.prepareStatement(sql);
            createBuilding.setInt(1, key_No);
            result = createBuilding.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, createBuilding, null, excepMsg);
            return result;
        }    
        //</editor-fold>        
    }    
    
    private int getL2RecordCount(int L1_no) {
        int result = -1;
        
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        String excepMsg = "getting count of L2 record that belong to L1 no: " + L1_no;

        String sql = "Select count(*) from L2_Affiliation where L1_NO = ?";
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, L1_no);
            rs = pstmt.executeQuery();
            rs.next();
            result = rs.getInt(1);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, pstmt, rs, excepMsg);
        }
        return result;        
    }    
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Stop creating or updating a level 1 affiliation.
        JTable table = (currLevel == 1 ? BuildingTable : UnitTable);
        
        if (formMode == FormMode.CreateMode) {
            removeToNormal(table);
        } else if (formMode == FormMode.UpdateMode) {
            backToNormal(table);
            int index = followAndGetTrueIndex(table);
            
            if (currLevel == 1) {
                loadBuilding(index, 0);
            } else { 
                int bIndex = followAndGetTrueIndex(BuildingTable);
                int bldgNo = (Integer)BuildingTable.getModel().getValueAt(bIndex, 1);
                Object bldgSeqNoObj = BuildingTable.getModel().getValueAt(bIndex, 2);                   
                
                loadUnit(bldgNo, bldgSeqNoObj, index, 0);
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void removeToNormal(JTable table)
    {
        setFormMode(FormMode.NormalMode);  
        insertSaveButton.setText(CREATE_BTN.getContent());
        insertSaveButton.setMnemonic('R');                     
        
        setLev_Editable(currLevel, false);
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        JRadioButton radioBtn = (table == BuildingTable ? bldg_Radio : lev2_Radio);
        changeItemsEnabled(table, radioBtn.isSelected());
        
        ((DefaultTableModel)table.getModel()).setRowCount(table.getRowCount() - 1);
        
        // Return to read mode as if [Enter] key pressed.
        if (originalRow != -1) {
            highlightTableRow(table, originalRow);
        }
    }    
    
    private void bldg_RadioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_bldg_RadioItemStateChanged
        currLevel = 1;
        applyTableChange(evt, BuildingTable, bldg_Radio);
    }//GEN-LAST:event_bldg_RadioItemStateChanged

    private void affiTopTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_affiTopTitleMouseClicked
    }//GEN-LAST:event_affiTopTitleMouseClicked

    private void readSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheetActionPerformed
        loadAffiliationsFromODS();
    }//GEN-LAST:event_readSheetActionPerformed

    private void loadAffiliationsFromODS() {
        try {
            int returnVal = odsFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {                
                File file = odsFileChooser.getSelectedFile();

                Sheet sheet = null;
                try {
                    sheet = SpreadSheet.createFromFile(file).getSheet(0);
                } catch (IOException ex) {
                    Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (sheet != null)
                {
                    ODSReader objODSReader = new ODSReader();

                    WrappedInt level1_total = new WrappedInt();
                    WrappedInt level2_total = new WrappedInt();

                    if (objODSReader.checkAffiliationODS(sheet, level1_total, level2_total))
                    {
                        StringBuilder sb = new StringBuilder();

                        sb.append(AFFILI_ODS_DIAG_1.getContent());
                        sb.append(System.getProperty("line.separator"));
                        sb.append(System.getProperty("line.separator"));
                        sb.append(AFFILI_ODS_DIAG_2.getContent() + level1_total.getValue());
                        sb.append(System.getProperty("line.separator"));
                        sb.append(AFFILI_ODS_DIAG_3.getContent() + level2_total.getValue());
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                ODS_CHECK_RESULT_TITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readBuildingODS(sheet, this);
                        }
                    }                     
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(User Action: upload affiliation data from an ods sheet)");             
        }               
    }    

    /**
     * Handles a button to save into an 'ods' file of ALL affiliation names.
     * @param evt 
     */
    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
       
        /**
         * Get the unit item count to list on one line in the range 1 to 15.
         */
        UnitColCount dlg = new UnitColCount(this, true);
        Integer colCount = dlg.showDialog();
        /**
         * Prepare a model for the temporary JTable 'table'.
         */
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("");
        model.addColumn("");
        
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(load buildings to save in an ods file)";
        
        try {
            //<editor-fold defaultstate="collapsed" desc="-- Read names and put put them in the model">
            StringBuffer sb = new StringBuffer();
            sb.append("Select bt.BLDG_NO, ut.UNIT_NO ");
            sb.append("From building_table bt ");
            sb.append("Left outer join building_unit ut ");
            sb.append("On bt.SEQ_NO = ut.BLDG_SEQ_NO ");
            
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            
            Integer prevBN = null, currBN;
            
            model.setRowCount(0);
            while (rs.next()) {
                currBN = rs.getInt("BLDG_NO");
                if (!currBN.equals(prevBN)) {
                    prevBN = currBN;
                    model.addRow(new Object[] {currBN, ""});
                }
                
                Integer unit_no = rs.getInt("UNIT_NO");

                if (unit_no != null) {
                    model.addRow(new Object[] {"", unit_no});
                }
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, selectStmt, rs, excepMsg);
        }        
        saveODSfileName(this, table, odsFileChooser,
            AFFILI_SAVE_ODS_FAIL_DIALOG.getContent(), BUILD_ROOM_HEADER.getContent());
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void ODSAffiliHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSAffiliHelpActionPerformed
        JDialog helpDialog;

        helpDialog = new ODS_HelpJDialog(this, false, HELP_AFFIL_LABEL.getContent(),
            ODS_TYPE.AFFILIATION);
            
        setHelpDialogLoc(ODSAffiliHelp, helpDialog);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_ODSAffiliHelpActionPerformed

    private void sampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleButtonActionPerformed
        String sampleFile = "/affiliations";

        // Ask user the name and location for the ods file to save
        StringBuffer odsFullPath = new StringBuffer();

        if (wantToSaveFile(this, saveFileChooser, odsFullPath, sampleFile)) {
            // Read sample ods resource file
            String extension = saveFileChooser.getFileFilter().getDescription();

            if (extension.indexOf("*.ods") >= 0 && !odsFullPath.toString().endsWith(".ods")) {
                odsFullPath.append(".ods");
            }

            InputStream sampleIn = getClass().getResourceAsStream(sampleFile + ".ods");

            downloadSample(odsFullPath.toString(), sampleIn, sampleFile);
            if (sampleIn != null) {
                try {
                    sampleIn.close();
                } catch (IOException e) {
                    logParkingException(Level.SEVERE, e, sampleFile + " istrm close error");
                }
            }
        }
    }//GEN-LAST:event_sampleButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        tryToCloseSettingsForm();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void lev2_RadioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lev2_RadioItemStateChanged
        currLevel = 2;
        applyTableChange(evt, UnitTable, lev2_Radio);
    }//GEN-LAST:event_lev2_RadioItemStateChanged

    private void BuildingTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BuildingTableMouseClicked
        changeTableSelection(1);
    }//GEN-LAST:event_BuildingTableMouseClicked

    private void UnitTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UnitTableMouseClicked
        changeTableSelection(2);
    }//GEN-LAST:event_UnitTableMouseClicked

    private void radio1PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radio1PanelMouseClicked
        changeTableSelection(1);
    }//GEN-LAST:event_radio1PanelMouseClicked

    private void radio2PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radio2PanelMouseClicked
        changeTableSelection(2);
    }//GEN-LAST:event_radio2PanelMouseClicked

    private void title1_PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_title1_PanelMouseClicked
        changeTableSelection(1);
    }//GEN-LAST:event_title1_PanelMouseClicked

    private void title2_PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_title2_PanelMouseClicked
        changeTableSelection(2);
    }//GEN-LAST:event_title2_PanelMouseClicked

    private void UnitTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UnitTableKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_UnitTableKeyReleased

    private void UnitTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UnitTableFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_UnitTableFocusLost

    private void backToNormal(JTable table) {
        setFormMode(FormMode.NormalMode);
        updateSaveButton.setText(MODIFY_BTN.getContent());
        updateSaveButton.setMnemonic('M');
            
        setLev_Editable(currLevel, false);
        table.getCellEditor().stopCellEditing();
        if (table == BuildingTable) {
            changeItemsEnabled(table, bldg_Radio.isSelected());
        } else {
            changeItemsEnabled(table, lev2_Radio.isSelected());
        }
    }    
    
    private int followAndGetTrueIndex(JTable theTable) {
        theTable.scrollRectToVisible(
                new Rectangle(theTable.getCellRect(theTable.getSelectedRow(), 0, true)));
        if (theTable.getSelectedRow() == -1)
            return -1;
        else
            return theTable.convertRowIndexToModel(theTable.getSelectedRow());                
    }    

    private void changeItemsEnabled(JTable table, boolean selected)
    {
        int idx = followAndGetTrueIndex(table);    
        
        if (selected) {
            if (idx == -1 && table.getRowCount() > 0) {
                idx = 0;
                table.setRowSelectionInterval(idx, idx);
            }
        }
        table.setEnabled(selected);
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
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Buildings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Buildings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Buildings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Buildings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (loginID != null ||
                        loginID == null && findLoginIdentity() != null) 
                {
                    Buildings runForm = new Buildings(null);
                    runForm.setVisible(true);
                    runForm.setDefaultCloseOperation(
                            javax.swing.WindowConstants.EXIT_ON_CLOSE);
                }                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable BuildingTable;
    private javax.swing.JLabel Building_Title;
    private javax.swing.JPanel GUI_Title_Panel;
    private javax.swing.JButton ODSAffiliHelp;
    private javax.swing.JTable UnitTable;
    private javax.swing.JLabel Unit_Title;
    private javax.swing.ButtonGroup affi_Group;
    private javax.swing.JLabel bldg_Count;
    private javax.swing.JRadioButton bldg_Radio;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JPanel count1_Panel;
    private javax.swing.JPanel count2_Panel;
    private javax.swing.JLabel countLabel;
    private javax.swing.JLabel countLabel1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel eastPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JButton insertSaveButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel lev1_Panel;
    private javax.swing.JPanel lev2_Panel;
    private javax.swing.JRadioButton lev2_Radio;
    private javax.swing.JPanel levs_Panel;
    private javax.swing.JLabel modeString;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JPanel radio1Panel;
    private javax.swing.JPanel radio2Panel;
    private javax.swing.JButton readSheet;
    private javax.swing.JPanel recordMenuPanel;
    private javax.swing.JButton sampleButton;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel tableMenuPanel;
    private javax.swing.JPanel title1_Panel;
    private javax.swing.JPanel title2_Panel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel unit_Count;
    private javax.swing.JButton updateSaveButton;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    private javax.swing.JLabel workPanel;
    // End of variables declaration//GEN-END:variables

    private void setLev_Editable(int index, boolean b) {
        lev_Editable[index] = b;
    }

    final int NO_HIGHLIGHT = -2;
    final int HIGHLIGHT_NEW = -1;
    
    private void addRowSelectionListener(JTable table) {
        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
                            int index = followAndGetTrueIndex(table);
                            
                            if (index >= 0) {
                                Object key_no = table.getModel().getValueAt(index, 2);
                                
                                if (table == BuildingTable) {
                                    Object bnoObj = table.getModel().getValueAt(index, 1);
                                    
                                    if (bnoObj != null) {
                                        loadUnit((Integer)bnoObj, key_no, NO_HIGHLIGHT, 0);
                                    }
                                }
                            } else {
                                if (table == BuildingTable) {
                                    loadUnit(0, null, 0, 0);
                                }
                            }
                            if (table == BuildingTable) {
                                // clear L2List selection
                                UnitTable.removeEditor();
                                UnitTable.getSelectionModel().clearSelection();
                            }

                            // Delete an empty row if existed
                            if (emptyLastRowPossible(insertSaveButton, table))
                            {
                                removeEmptyRow(insertSaveButton, table);
                            }
                        }
                    }
                });                 
            }
        }); 
    }    
    
    private void tryToCloseSettingsForm() {
        if (mainForm != null) {
            mainForm.getTopForms()[ControlEnums.TopForms.Settings.ordinal()] = null;
        }

        if (isStand_Alone) {
            this.setVisible(false);
            System.exit(0);
        } else {
            dispose();
        }  
    }

    private void abortInsertion(String message, JTable table) {
        JOptionPane.showMessageDialog(this, message, ERROR_DIALOGTITLE.getContent(), 
                JOptionPane.WARNING_MESSAGE); 
        removeToNormal(table);
    }

    private void applyTableChange(ItemEvent evt, JTable lev_Table, JRadioButton lev_Radio) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                manageSelection(evt, lev_Table);
                
                changeItemsEnabled(lev_Table, lev_Radio.isSelected());                
                fixButtonEnabled();
            }

            private void manageSelection(ItemEvent evt, JTable table) {
                if (evt.getStateChange() == SELECTED) {
                    table.setSelectionBackground(DARK_BLUE);
                } else {
                    table.setSelectionBackground(LIGHT_BLUE);
                }
            }
        });
    }

    int originalRow = -1;
    
    private void createBldgUnit(JTable buTable) {
        //<editor-fold desc="--Prepare to create a new affiliation">
        originalRow = buTable.getSelectedRow();
        /**
         * Change management button enabled properties.
         */                
        insertSaveButton.setText(SAVE_BTN.getContent());
        insertSaveButton.setMnemonic('s');            

        DefaultTableModel model = (DefaultTableModel)buTable.getModel();
        model.setRowCount(buTable.getRowCount() + 1);
        int rowIndex = buTable.getRowCount() - 1;

        if (buTable.getValueAt(rowIndex, 1) != null) 
        {
            rowIndex = 0;
        }
        buTable.setRowSelectionInterval(rowIndex, rowIndex);

        setLev_Editable(currLevel, true);
        buTable.scrollRectToVisible(
                new Rectangle(buTable.getCellRect(rowIndex, 0, true)));
        
        if (buTable.editCellAt(rowIndex, 1))
        {
            buTable.getEditorComponent().requestFocus();
        }
        setFormMode(FormMode.CreateMode);
        //</editor-fold>
    }

    private void addTableListener(JTable buTable, Object prevName, String updateCol, 
            DialogMessages message) 
    {
        buTable.getModel().addTableModelListener(new TableModelListener() {
           @Override
            public void tableChanged(TableModelEvent e) {
                if (formMode == UpdateMode) {
                    updateBldgUnit(buTable, prevName, updateCol, message);
                } else if (formMode == CreateMode) {
                    insertBldgUnit(buTable, message);
                }
            }
        });   
    }

    private void adjustColumnWidth(JTable lev_Table, int numRows) {
        String countStr = Integer.toString(numRows);
        int numWidthFont = getStringWidth(countStr, lev_Table.getFont());
        int width = numWidthFont + 40;
        TableColumn column = lev_Table.getColumnModel().getColumn(0);

        if (column.getPreferredWidth() < width) {
            column.setPreferredWidth(width); 
            column.setMinWidth(width); 
        }
    }

    private void addHeaderMouseListener(final JTable table) {
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (formMode == NormalMode) {
                    if (table == BuildingTable) {
                        bldg_Radio.setSelected(true);
                    } else {
                        lev2_Radio.setSelected(true);
                    }
                }
            }
        });
    }
    
    /**
     * Fix enable/disable affiliation management buttons in one place.
     *        Button : Insert     Update     Delete     Cancel
     *      Form Mod : !update    !insert    normal     !normal  
     * Row selection :   N/A      selected   selected   selected
     *    User level : -------------- Manager ------------------
     */
    private void fixButtonEnabled() {
        if (isManager) {
            /**
             * Insert Button
             */
            if (formMode == UpdateMode) {
                insertSaveButton.setEnabled(false);
            } else {
                insertSaveButton.setEnabled(true);
            }
            
            JTable table = (currLevel == 1 ? BuildingTable : UnitTable);
            boolean rowSelected = table.getSelectedRow() != -1;

            /**
             * Update button
             */
            if (formMode != CreateMode && rowSelected) {
                updateSaveButton.setEnabled(true);
            } else {
                updateSaveButton.setEnabled(false);
            }
            
            /**
             * Delete button
             */
            if (formMode == NormalMode && rowSelected) {
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }
            
            /**
             * Cancel button
             */
            if (formMode != NormalMode && rowSelected) {
                cancelButton.setEnabled(true);
                closeFormButton.setEnabled(false);
            } else {
                cancelButton.setEnabled(false);
                closeFormButton.setEnabled(true);
            }
        } else {
            /**
             * Disable each button for a non-manager user.
             */
            insertSaveButton.setEnabled(false);
            updateSaveButton.setEnabled(false);
            deleteButton.setEnabled(false);
            cancelButton.setEnabled(false);
        }
    }

    /**
     * Changes table under consideration via a Radio button selection.
     * This method is called from four places:
     * <p><ul>
     * <li>Table title
     * <li>Table column heading
     * <li>Table row clicking
     * <li>Table radio and its surrounding panel.
     * </ul></p>
     * @param tab_no table number, either 1 or 2.
     */
    private void changeTableSelection(int tab_no) {
        if (formMode == NormalMode) {
            if (tab_no == 1) {
                bldg_Radio.setSelected(true);
            } else {
                lev2_Radio.setSelected(true);
            }
        }
    }

    private Integer tryParseInt(String inputStr) {
        try {
            return Integer.parseInt(inputStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
