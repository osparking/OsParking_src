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
import static com.osparking.global.Globals.insertLevel1Affiliation;
import static com.osparking.global.Globals.insertLevel2Affiliation;
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
import static com.osparking.global.names.ControlEnums.DialogMessages.DUPLICATE_HIGH_AFFILI2;
import static com.osparking.global.names.ControlEnums.DialogMessages.DUPLICATE_LOW_AFFILI2;
import static com.osparking.global.names.ControlEnums.DialogMessages.EMPTY_HIGH_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMessages.EMPTY_LOW_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMessages.LEVEL2_NAME_DIALOG;
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
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_AFFIL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_RESULT;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_ODS_DIAG_3;
import com.osparking.global.names.ControlEnums.OsPaLang;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOWER_COL_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TitleTypes.AFFILIATION_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.ControlEnums.ToolTipContent.INSERT_TOOLTIP;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.WrappedInt;
import static com.osparking.vehicle.CommonData.setHelpDialogLoc;
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
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
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
public class Affiliations extends javax.swing.JFrame {
    private boolean lev_Editable[] = {false, false, false};
    private int currLevel = 1;
    private ControlEnums.FormMode formMode = NormalMode;
    IMainGUI mainForm = null;
    private boolean isStand_Alone = false;
    
    static Object prevL1Name = null;
    static Object prevL2Name = null;    

    /**
     * Creates new form Affiliations
     */
    public Affiliations(IMainGUI mainForm) {
        initComponents();
        setIconImages(OSPiconList);
        
        this.mainForm = mainForm;
        if (mainForm == null) {
            isStand_Alone = true;
        }        
        
        /**
         * Set default input language to Korean for 2 affiliation tables.
         */
        tableColumnLanguage(lev1_Table, 1, KOREAN);
        tableColumnLanguage(lev2_Table, 1, KOREAN);
        
        /**
         * Add table model listener to complete update and create operation.
         */
        addTableListener(lev1_Table, prevL1Name, "L1_NO", DUPLICATE_HIGH_AFFILI2);
        addTableListener(lev2_Table, prevL2Name, "L2_NO", DUPLICATE_LOW_AFFILI2);

        adjustAffiliationTable(lev1_Table);
        adjustAffiliationTable(lev2_Table);
        addAffiliationSelectionListener(lev1_Table);      
        addAffiliationSelectionListener(lev2_Table);  
        
        addHeaderMouseListener(lev1_Table);
        addHeaderMouseListener(lev2_Table);
        setComponentSize(insertSaveButton, new Dimension(buttonWidthNorm, buttonHeightNorm));
        
        loadLev1_Table(0, "");    
        setFormMode(NormalMode);
        changeItemsEnabled(lev1_Table, true);
    }

    private void insertAffiliation(JTable table, ControlEnums.DialogMessages diagMsg) 
    {
        if (table.getSelectedRow() == -1) {
            return;
        }
        
        int rowIndex = table.convertRowIndexToModel (table.getSelectedRow());
        TableModel model = table.getModel();
        String affiliName = ((String)model.getValueAt(rowIndex, 1)).trim();

        // Conditions to make this a new higher affiliation: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        {                     
            if (affiliName == null 
                    || affiliName.isEmpty()
                    || affiliName.equals(INSERT_TOOLTIP.getContent())) 
            {
                DialogMessages message = 
                        (table == lev1_Table ? EMPTY_HIGH_AFFILI : EMPTY_LOW_AFFILI);
                abortInsertion(message.getContent(), table);
                return;
            } else {
                // Cond 2. Name field has string of meaningful affiliation name
                // <editor-fold defaultstate="collapsed" desc="-- Insert New Higher name and Refresh the List">
                int result = ER_YES;
                Object parentKey = null;
                
                if (currLevel == 1) {
                    result = insertLevel1Affiliation(affiliName);
                } else {
                    int index = lev1_Table.convertRowIndexToModel(lev1_Table.getSelectedRow());
                    parentKey = lev1_Table.getModel().getValueAt(index, 2); 
                    
                    result = insertLevel2Affiliation((Integer)parentKey, affiliName);
                }

                if (result == ER_NO) {
                    setFormMode(FormMode.NormalMode);
                    insertSaveButton.setText(CREATE_BTN.getContent());
                    insertSaveButton.setMnemonic('R');                        
                    
                    setLev_Editable(currLevel, false);
                    table.getCellEditor().stopCellEditing();
                    if (currLevel == 1) {
                        changeItemsEnabled(lev1_Table, lev1_Radio.isSelected());
                        loadLev1_Table(-1, affiliName); // Refresh the list
                    } else {
                        changeItemsEnabled(lev2_Table, lev2_Radio.isSelected());
                        loadLev2_Table(parentKey, -1, affiliName); // Refresh the list                        
                    }
                } else if (result == ER_DUP_ENTRY) {
                    abortInsertion(affiliName + diagMsg.getContent(), table);
                }
                // </editor-fold>
            }
        }                
    }    

    private void abortModification(ControlEnums.DialogMessages dialog, String name, JTable table) {
        abortModification(name + dialog.getContent(), table);
    }

    private void abortModification(ControlEnums.DialogMessages dialog, JTable table) {
        abortModification(dialog.getContent(), table);
    }
    
    private void abortModification(String message, JTable table) {
        JOptionPane.showMessageDialog(this, message, ERROR_DIALOGTITLE.getContent(), 
                JOptionPane.WARNING_MESSAGE); 
        backToNormal(table);
        if (table == lev1_Table) {
            loadLev1_Table(table.getSelectedRow(), "");
        } else {
            int index1 = followAndGetTrueIndex(lev1_Table);
            Object L1_no = lev1_Table.getModel().getValueAt(index1, 2);
            
            loadLev2_Table(L1_no, table.getSelectedRow(), "");
        }
    }    
    
    private void updateAffiliation(JTable affiliTable, Object prevAffiliName, String keyCol, 
            ControlEnums.DialogMessages msgForDuplicate) 
    {
        // <editor-fold defaultstate="collapsed" desc="-- Update high affiliation name">
        int rowIndex = affiliTable.convertRowIndexToModel (affiliTable.getSelectedRow());
        TableModel model = affiliTable.getModel();
        String affiliName = ((String)model.getValueAt(rowIndex, 1)).trim();    
        
        if (affiliName.isEmpty()) {
            abortModification(
                    affiliTable == lev1_Table ? EMPTY_HIGH_AFFILI : EMPTY_LOW_AFFILI, 
                    affiliTable);                      
            return;            
        } 
        Object keyVal = model.getValueAt(rowIndex, 2);
        int result = 0;
        Connection conn = null;
        PreparedStatement pUpdateStmt = null;
        
        try {
            String sql = "Update " + affiliTable.getName() + " Set PARTY_NAME = ? Where " + 
                    keyCol + " = ?";

            conn = getConnection();
            pUpdateStmt = conn.prepareStatement(sql);
            pUpdateStmt.setString(1, affiliName);
            pUpdateStmt.setInt(2, (Integer)keyVal);
            result = pUpdateStmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                abortModification(msgForDuplicate, affiliName, affiliTable);                      
                return;
            }
        } finally {
            closeDBstuff(conn, pUpdateStmt, null, 
                    "(Original " + affiliTable.getName() + "name : " + prevAffiliName + ")");
        }    
        if (result == 1) {
            backToNormal(affiliTable);

            if (affiliTable == lev1_Table) {
                loadLev1_Table(-1, affiliName); // Refresh higher affiliation list
            } else {
                int index = lev1_Table.convertRowIndexToModel(lev1_Table.getSelectedRow());
                Object L1_no = lev1_Table.getModel().getValueAt(index, 2);
                
                loadLev2_Table(L1_no, HIGHLIGHT_NEW, affiliName);
            }
        }         
    }
        
    private void adjustAffiliationTable(JTable AffiliationTable) {
        
        // Hide affiliation number field which is used internally.
        TableColumnModel BelongModel = AffiliationTable.getColumnModel();
        BelongModel.removeColumn(BelongModel.getColumn(2));
        
        // Decrease the first column width
        TableColumn column = AffiliationTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50); //row number column is narrow
        column.setMinWidth(50); //row number column is narrow
        column.setMaxWidth(10000); //row number column is narrow
        
        column = AffiliationTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(150); //row number column is narrow
        column.setMinWidth(150); //row number column is narrow
        column.setMaxWidth(30000); //row number column is narrow        
    }     
    
    public void loadLev1_Table(int viewIndexToHighlight, String insertedName) {

        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(load upper and lower level affiliations)";

        DefaultTableModel model = (DefaultTableModel) lev1_Table.getModel();
        int model_Index = 0;
        
        try {
            //<editor-fold defaultstate="collapsed" desc="-- Load higher level affiliations">
            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT @ROWNUM := @ROWNUM + 1 AS recNo, PARTY_NAME, L1_NO ");
            sb.append(" FROM L1_Affiliation, (SELECT @rownum := 0) r ");
            sb.append(" ORDER BY party_name");
            
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            
            model.setRowCount(0);
            while (rs.next()) {
                if (viewIndexToHighlight == -1) // loading right after a new affiliation is created
                {
                    if (insertedName.equals(rs.getString("PARTY_NAME")))
                    {
                        model_Index = model.getRowCount();  // store index to select
                    }
                }
                model.addRow(new Object[] {rs.getInt("recNo"),  rs.getString("PARTY_NAME"), rs.getInt("L1_NO")});
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, selectStmt, rs, excepMsg);
        }
        
        saveSheet_Button.setEnabled(false);
        
        // <editor-fold defaultstate="collapsed" desc="-- Selection of a higher affiliation and loading of its lower affil'">
        int numRows = model.getRowCount();
        if (numRows > 0)
        {
            if (viewIndexToHighlight == -1) // loading right after a new affiliation is created
            {
                viewIndexToHighlight = lev1_Table.convertRowIndexToView(model_Index);
            } else if (viewIndexToHighlight == numRows)
            {
                // "number of remaining rows == deleted row index" means the row deleted was the last row
                // In this case, highlight the previous row
                viewIndexToHighlight--;
            }
            
            final int highRow = viewIndexToHighlight;
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    highlightTableRow(lev1_Table, highRow);
                }
            });
            
            // Adjust first column width checking row count, etc.
            adjustColumnWidth(lev1_Table, numRows);
            
            if (isManager) {
                saveSheet_Button.setEnabled(true);
            }            
        } else {
            loadLev2_Table(null, 0, "");
        }
        lev1_Count.setText(Integer.toString(numRows));
        fixButtonEnabled();        
        //</editor-fold>
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
        lev1_Title = new javax.swing.JLabel();
        count1_Panel = new javax.swing.JPanel();
        countLabel = new javax.swing.JLabel();
        lev1_Count = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lev1_Table = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return lev_Editable[currLevel];
            }
        }
        ;
        radio1Panel = new javax.swing.JPanel();
        lev1_Radio = new javax.swing.JRadioButton();
        lev2_Panel = new javax.swing.JPanel();
        title2_Panel = new javax.swing.JPanel();
        lev2_Title = new javax.swing.JLabel();
        count2_Panel = new javax.swing.JPanel();
        countLabel1 = new javax.swing.JLabel();
        lev2_Count = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lev2_Table = new javax.swing.JTable(){
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
        setTitle(AFFILIATION_FRAME_TITLE.getContent());
        setMinimumSize(new java.awt.Dimension(560, 609));
        setPreferredSize(new java.awt.Dimension(560, 609));

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

        lev1_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        lev1_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lev1_Title.setText(AFFILIATION_LIST_LABEL.getContent());
        title1_Panel.add(lev1_Title);

        lev1_Panel.add(title1_Panel);

        count1_Panel.setMaximumSize(new java.awt.Dimension(32767, 20));
        count1_Panel.setMinimumSize(new java.awt.Dimension(86, 20));
        count1_Panel.setPreferredSize(new java.awt.Dimension(86, 20));
        count1_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        countLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLabel.setText(COUNT_LABEL.getContent());
        count1_Panel.add(countLabel);

        lev1_Count.setForeground(pointColor);
        lev1_Count.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev1_Count.setText("0");
        count1_Panel.add(lev1_Count);

        lev1_Panel.add(count1_Panel);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(454, 200));

        lev1_Table.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev1_Table.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev1_Table.setModel(
            new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {1, "Janitor's Office", 2},
                    {2, "Engineering Bldg", 1}
                },
                new String[]{
                    ORDER_HEADER.getContent(), HIGHER_HEADER.getContent(), "L1_NO"}
            )
            {  @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0)
                    return Integer.class;
                    else
                    return String.class;
                }
            }
        );
        ((DefaultTableCellRenderer)lev1_Table.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        lev1_Table.getColumnModel().getColumn(0).setCellRenderer(numberCellRenderer);
        lev1_Table.setName("L1_Affiliation"); // NOI18N
        lev1_Table.setRowHeight(tableRowHeight);
        lev1_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lev1_TableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lev1_Table);

        lev1_Panel.add(jScrollPane1);

        radio1Panel.setMaximumSize(new java.awt.Dimension(32767, 31));
        radio1Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radio1PanelMouseClicked(evt);
            }
        });

        affi_Group.add(lev1_Radio);
        lev1_Radio.setSelected(true);
        lev1_Radio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lev1_RadioItemStateChanged(evt);
            }
        });
        radio1Panel.add(lev1_Radio);

        lev1_Panel.add(radio1Panel);

        levs_Panel.add(lev1_Panel);

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

        lev2_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        lev2_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lev2_Title.setText(LOWER_LIST_LABEL.getContent());
        title2_Panel.add(lev2_Title);

        lev2_Panel.add(title2_Panel);

        count2_Panel.setMaximumSize(new java.awt.Dimension(32767, 20));
        count2_Panel.setMinimumSize(new java.awt.Dimension(86, 20));
        count2_Panel.setPreferredSize(new java.awt.Dimension(86, 20));
        count2_Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        countLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLabel1.setText(COUNT_LABEL.getContent());
        count2_Panel.add(countLabel1);

        lev1_Count.setForeground(pointColor);
        lev2_Count.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev2_Count.setForeground(pointColor);
        lev2_Count.setText("0");
        count2_Panel.add(lev2_Count);

        lev2_Panel.add(count2_Panel);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(454, 200));

        lev2_Table.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev2_Table.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lev2_Table.setModel(
            new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String[]{
                    ORDER_HEADER.getContent(), LOWER_COL_TITLE.getContent(), "PARTY_NO"}
            )
            {  @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0)
                    return Integer.class;
                    else
                    return String.class;
                }
            }
        );
        ((DefaultTableCellRenderer)lev2_Table.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        lev2_Table.setEnabled(false);
        lev2_Table.getColumnModel().getColumn(0)
        .setCellRenderer(numberCellRenderer);
        lev2_Table.setName("L2_Affiliation"); // NOI18N
        lev2_Table.setRowHeight(tableRowHeight);
        lev2_Table.setSelectionBackground(DARK_BLUE);
        lev2_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lev2_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lev2_TableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lev2_Table);

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
        if (lev1_Radio.isSelected()) {
            if (formMode == NormalMode) {
                createAffiliation(lev1_Table);
            } else {
                lev1_Table.getCellEditor().stopCellEditing();                
            }
        } else {
            if (formMode == NormalMode) {
                createAffiliation(lev2_Table);
            } else {
                lev2_Table.getCellEditor().stopCellEditing();                
            }
        }
    }//GEN-LAST:event_insertSaveButtonActionPerformed

    private void updateSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateSaveButtonActionPerformed
        try {
            switch (formMode) {
                case NormalMode:
                    // <editor-fold defaultstate="collapsed" desc="-- Prepare to update user information">
                    if (currLevel == 1)
                        startModify(lev1_Table);
                    else 
                        startModify(lev2_Table);
                    // </editor-fold>                
                    break;
                case UpdateMode:
                    // <editor-fold defaultstate="collapsed" desc="-- save modified user information ">
                    if (currLevel == 1)
                        lev1_Table.getCellEditor().stopCellEditing();
                    else 
                        lev2_Table.getCellEditor().stopCellEditing();
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
            prevL1Name = model.getValueAt(model_index, 1);
        } else {
            prevL2Name = model.getValueAt(model_index, 1);
        }

        setLev_Editable(currLevel, true);
        if (table.editCellAt(rowIndex, 1)) {
            table.getEditorComponent().requestFocus();
        }
    }

    private void enableRadioButtons(boolean b) {
        lev1_Radio.setEnabled(b);
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
            table = lev1_Table;
        } else {
            table = lev2_Table;
        }
            
        int viewIndex = table.getSelectedRow();
        
        if (viewIndex == -1) {
            return;
        } else {            
            String affiliation = (String)table.getValueAt(viewIndex, 1);
            int modal_Index = table.convertRowIndexToModel(viewIndex);
            int key_No = (int)table.getModel().getValueAt(modal_Index, 2);
            
            String message;
            
            if (currLevel == 1) {
                int count = getL2RecordCount(key_No);

                message = AFFILI_DEL_L1.getContent() + 
                        System.getProperty("line.separator") + 
                        AFFILI_DIAG_L2.getContent() + affiliation + 
                        System.getProperty("line.separator") + 
                        AFFILI_DIAG_L3.getContent() + count;
            } else {
                message = AFFILI_DEL_L2.getContent() +
                        System.getProperty("line.separator") + 
                        LEVEL2_NAME_DIALOG.getContent() + " : " + affiliation;                
            }
            
            int result = JOptionPane.showConfirmDialog(this, message,
                DELETE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                //<editor-fold desc="delete upper level affliation (unit name)">
                String excepMsg;
                String sql;
                
                if (currLevel == 1) {
                    excepMsg = "(In deletion of: " + affiliation + ")";
                    sql = "Delete From L1_Affiliation Where L1_no = ?";
                } else {
                    excepMsg = "(Deletion of Lower Party No: " + key_No + ")";
                    sql = "Delete From L2_Affiliation Where L2_no = ?";
                }
                
                result = deleteAffiliation(excepMsg, sql, key_No);

                if (result == 1) {
                    if (currLevel == 1) {
                        loadLev1_Table(viewIndex, ""); // Deliver the index of deleted row

                        message = AFFILI_DEL_RESULT.getContent() + System.getProperty("line.separator") +
                                AFFILI_DIAG_L2.getContent() + affiliation;
                    } else {
                        int index1 = lev1_Table.convertRowIndexToView(lev1_Table.getSelectedRow());
                        Object L1_No = lev1_Table.getModel().getValueAt(index1, 2);                    
                        loadLev2_Table((Integer)L1_No, viewIndex, ""); // Deliver the deleted L2 affiliation name

                        message = AFFILI_DEL_RESULT.getContent() + System.getProperty("line.separator") +
                                AFFILI2_DIAG_L2.getContent() + affiliation;
                    }
                    JOptionPane.showConfirmDialog(this, message,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                        
                }
                //</editor-fold>
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed
    
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
        JTable table = (currLevel == 1 ? lev1_Table : lev2_Table);
        
        if (formMode == FormMode.CreateMode) {
            removeToNormal(table);
        } else if (formMode == FormMode.UpdateMode) {
            backToNormal(table);
            if (currLevel == 1) {
                loadLev1_Table(table.getSelectedRow(), "");
            } else { 
                int index1 = followAndGetTrueIndex(lev1_Table);
                Object L1_no = lev1_Table.getModel().getValueAt(index1, 2);
                    
                loadLev2_Table(L1_no, table.getSelectedRow(), "");                
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void removeToNormal(JTable table)
    {
        setFormMode(FormMode.NormalMode);  
        insertSaveButton.setText(CREATE_BTN.getContent());
        insertSaveButton.setMnemonic('R');                     
        
        setLev_Editable(currLevel, false);
        table.getCellEditor().stopCellEditing();
        JRadioButton radioBtn = (table == lev1_Table ? lev1_Radio : lev2_Radio);
        changeItemsEnabled(table, radioBtn.isSelected());
        
        ((DefaultTableModel)table.getModel()).setRowCount(table.getRowCount() - 1);
        
        // Return to read mode as if [Enter] key pressed.
        if (originalRow != -1) {
            highlightTableRow(table, originalRow);
        }
    }    
    
    private void lev1_RadioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lev1_RadioItemStateChanged
        currLevel = 1;
        applyTableChange(evt, lev1_Table, lev1_Radio);
    }//GEN-LAST:event_lev1_RadioItemStateChanged

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
                            objODSReader.readAffiliationODS(sheet, this);
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
         * Prepare a model for the temporary JTable 'table'.
         */
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("");
        model.addColumn("");
        
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(load affiliations to save into ods format)";
        
        try {
            //<editor-fold defaultstate="collapsed" desc="-- Read names and put put them in the model">
            StringBuffer sb = new StringBuffer();
            sb.append("Select l1.PARTY_NAME L1_Name, l2.PARTY_NAME L2_Name ");
            sb.append("From l1_affiliation l1 ");
            sb.append("Left Outer Join l2_affiliation l2 ");
            sb.append("On l1.L1_NO = l2.L1_NO");
            
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            
            String prevL1 = null, currL1;
            
            model.setRowCount(0);
            while (rs.next()) {
                currL1 = rs.getString("L1_Name");
                if (!currL1.equals(prevL1)) {
                    prevL1 = currL1;
                    model.addRow(new Object[] {currL1, ""});
                }
                
                String l2Name = rs.getString("L2_Name");

                if (l2Name != null) {
                    model.addRow(new Object[] {"", l2Name});
                }
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, selectStmt, rs, excepMsg);
        }        
        saveODSfileName(this, table, odsFileChooser,
            AFFILI_SAVE_ODS_FAIL_DIALOG.getContent(), AFFILIATION_LABEL.getContent());
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
        applyTableChange(evt, lev2_Table, lev2_Radio);
    }//GEN-LAST:event_lev2_RadioItemStateChanged

    private void lev1_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lev1_TableMouseClicked
        changeTableSelection(1);
    }//GEN-LAST:event_lev1_TableMouseClicked

    private void lev2_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lev2_TableMouseClicked
        changeTableSelection(2);
    }//GEN-LAST:event_lev2_TableMouseClicked

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

    private void backToNormal(JTable table) {
        setFormMode(FormMode.NormalMode);
        updateSaveButton.setText(MODIFY_BTN.getContent());
        updateSaveButton.setMnemonic('M');
            
        setLev_Editable(currLevel, false);
        table.getCellEditor().stopCellEditing();
        if (table == lev1_Table) {
            changeItemsEnabled(table, lev1_Radio.isSelected());
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
            java.util.logging.Logger.getLogger(Affiliations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Affiliations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Affiliations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Affiliations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new Affiliations().setVisible(true);

                if (loginID != null ||
                        loginID == null && findLoginIdentity() != null) 
                {
                    Affiliations runForm = new Affiliations(null);
                    runForm.setVisible(true);
                    runForm.setDefaultCloseOperation(
                            javax.swing.WindowConstants.EXIT_ON_CLOSE);
                }                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GUI_Title_Panel;
    private javax.swing.JButton ODSAffiliHelp;
    private javax.swing.ButtonGroup affi_Group;
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
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JButton insertSaveButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lev1_Count;
    private javax.swing.JPanel lev1_Panel;
    private javax.swing.JRadioButton lev1_Radio;
    private javax.swing.JTable lev1_Table;
    private javax.swing.JLabel lev1_Title;
    private javax.swing.JLabel lev2_Count;
    private javax.swing.JPanel lev2_Panel;
    private javax.swing.JRadioButton lev2_Radio;
    private javax.swing.JTable lev2_Table;
    private javax.swing.JLabel lev2_Title;
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
    private javax.swing.JButton updateSaveButton;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    private javax.swing.JLabel workPanel;
    // End of variables declaration//GEN-END:variables

    private void setLev_Editable(int index, boolean b) {
        lev_Editable[index] = b;
    }

    private void loadLev2_Table(Object L1_no, int viewIndex, String l2Name) {
        if (L1_no == null)
        {
            lev2_Title.setText(LOWER_LIST_LABEL.getContent());
            ((DefaultTableModel) lev2_Table.getModel()).setRowCount(0);
        } else {
            int L1_index = lev1_Table.
                    convertRowIndexToModel(lev1_Table.getSelectedRow());
            String L1_Affil = lev1_Table.getModel().getValueAt(L1_index, 1).toString();
            
            String label = "";
            
            if (language == Locale.KOREAN) {
                label = L1_Affil + LOWER_LABEL.getContent();
            } else if (language == Locale.ENGLISH) {
                label = LOWER_LABEL.getContent() + L1_Affil; 
            } else {
            }  
            lev2_Title.setText(label);
            
            Connection conn = null;
            Statement selectStmt = null;
            ResultSet rs = null;
            String excepMsg = "change selected L2 name to " + l2Name + " for L1 no: " + L1_no;

            DefaultTableModel model = (DefaultTableModel) lev2_Table.getModel();
            int model_index = -1;
            
            // List lower affiliations of a higher affiliation whose key value is L1_NO
            try {
                //<editor-fold defaultstate="collapsed" desc="-- List all lower affiliations of a higher affiliation">                
                conn = getConnection();
                selectStmt = conn.createStatement();
                StringBuffer sb = new StringBuffer();
                sb.append(" SELECT @ROWNUM := @ROWNUM + 1 AS recNo, ");
                sb.append("   PARTY_NAME, L2_NO");
                sb.append(" FROM L2_affiliation, (SELECT @rownum := 0) r");
                sb.append(" WHERE L1_NO = " + (int)L1_no);
                sb.append(" ORDER BY party_name");

                rs = selectStmt.executeQuery(sb.toString());
                model.setRowCount(0);
                while (rs.next()) {
                    if (viewIndex == HIGHLIGHT_NEW) { // After creation/update, first find the new/modified affiliation name
                        if (l2Name.equals(rs.getString("PARTY_NAME"))) {
                            model_index = model.getRowCount();
                        }
                    }                       
                    model.addRow(new Object[] {
                         rs.getInt("recNo"),  rs.getString("PARTY_NAME"), rs.getInt("L2_NO")
                    });
                }
                //</editor-fold>
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, selectStmt, rs, excepMsg);
            }     
            
            int numRows = model.getRowCount();
            if (numRows > 0) {
                if (viewIndex == -1) // handle the case of a newly created affiliation.
                {
                    viewIndex = lev2_Table.convertRowIndexToView(model_index);
                } else {
                    // "number of remaining rows == deleted row index" means the row deleted was the last row
                    // In this case, highlight the previous row                    
                    if (viewIndex == numRows)
                    { 
                        viewIndex--; 
                    }
                }
                
                final int highRow = viewIndex;
                if (viewIndex != NO_HIGHLIGHT) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            highlightTableRow(lev2_Table, highRow);
                        }
                    });                    
                }
                adjustColumnWidth(lev2_Table, numRows);
            }
            lev2_Count.setText(Integer.toString(numRows));
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fixButtonEnabled();
            }
        });
    }
    
    final int NO_HIGHLIGHT = -2;
    final int HIGHLIGHT_NEW = -1;
    
    private void addAffiliationSelectionListener(JTable table) {
        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
                            int index = followAndGetTrueIndex(table);
                            if (index >= 0) 
                            {
                                Object key_no = table.getModel().getValueAt(index, 2);
                                
                                if (table == lev1_Table) {
                                    loadLev2_Table(key_no, NO_HIGHLIGHT, "");
                                }
                            }
                            else
                            {
                                if (table == lev1_Table) {
                                    loadLev2_Table(null, 0, "");
                                }
                            }
                            if (table == lev1_Table) {
                                // clear L2List selection
                                lev2_Table.removeEditor();
                                lev2_Table.getSelectionModel().clearSelection();
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
    
    private void createAffiliation(JTable lev_Table) {
        //<editor-fold desc="--Prepare to create a new affiliation">
        originalRow = lev_Table.getSelectedRow();
        /**
         * Change management button enabled properties.
         */                
        insertSaveButton.setText(SAVE_BTN.getContent());
        insertSaveButton.setMnemonic('s');            

        DefaultTableModel model = (DefaultTableModel)lev_Table.getModel();
        model.setRowCount(lev_Table.getRowCount() + 1);
        int rowIndex = lev_Table.getRowCount() - 1;

        if (lev_Table.getValueAt(rowIndex, 1) != null) 
        {
            rowIndex = 0;
        }
        lev_Table.setRowSelectionInterval(rowIndex, rowIndex);

        setLev_Editable(currLevel, true);
        lev_Table.scrollRectToVisible(
                new Rectangle(lev_Table.getCellRect(rowIndex, 0, true)));
        
        if (lev_Table.editCellAt(rowIndex, 1))
        {
            lev_Table.getEditorComponent().requestFocus();
        }
        setFormMode(FormMode.CreateMode);
        //</editor-fold>
    }

    private void tableColumnLanguage(JTable table, int i, OsPaLang lang) {
        table.getColumnModel().getColumn(i).setCellEditor(
                new TableCellEditorKor(lang, table.getFont()));
    }

    private void addTableListener(JTable lev_Table, Object prevName, String column, 
            DialogMessages message) 
    {
        lev_Table.getModel().addTableModelListener(new TableModelListener() {
           @Override
            public void tableChanged(TableModelEvent e) {
                if (formMode == UpdateMode) {
                    updateAffiliation(lev_Table, prevName, column, message);
                } else if (formMode == CreateMode) {
                    insertAffiliation(lev_Table, message);
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
                    if (table == lev1_Table) {
                        lev1_Radio.setSelected(true);
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
            
            JTable table = (currLevel == 1 ? lev1_Table : lev2_Table);
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
                lev1_Radio.setSelected(true);
            } else {
                lev2_Radio.setSelected(true);
            }
        }
    }
}
