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
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.buttonWidthWide;
import static com.osparking.global.CommonData.getStringWidth;
import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.resizeComponentFor;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
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
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.loginID;
import static com.osparking.global.Globals.removeEmptyRow;
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
import static com.osparking.global.names.ControlEnums.DialogMessages.DUPLICATE_HIGH_AFFILI2;
import static com.osparking.global.names.ControlEnums.DialogMessages.EMPTY_HIGH_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMessages.EMPTY_LOW_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.FormMode.CreateMode;
import static com.osparking.global.names.ControlEnums.FormMode.NormalMode;
import static com.osparking.global.names.ControlEnums.FormMode.UpdateMode;
import static com.osparking.global.names.ControlEnums.FormModeString.CREATE;
import static com.osparking.global.names.ControlEnums.FormModeString.MODIFY;
import static com.osparking.global.names.ControlEnums.FormModeString.SEARCH;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_RESULT;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L3;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.TableType.L1_TABLE;
import static com.osparking.global.names.ControlEnums.TableType.L2_TABLE;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.ControlEnums.ToolTipContent.INSERT_TOOLTIP;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import static com.osparking.vehicle.AffiliationBuildingForm.prevL1Name;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
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
        
        lev1_Table.getColumnModel().getColumn(1).setCellEditor(
                new TableCellEditorKor(KOREAN, lev1_Table.getFont()));
        
        /**
         * Add table model listener to process 'enter key' for affiliation tables.
         */
        lev1_Table.getModel().addTableModelListener(new TableModelListener() {
           @Override
            public void tableChanged(TableModelEvent e) {
                if (formMode == UpdateMode) {
                    updateAffiliation(lev1_Table, prevL1Name, "L1_NO",  
                            DUPLICATE_HIGH_AFFILI2, L1_TABLE);
                } else if (formMode == CreateMode) {
                    insertAffiliation(lev1_Table, DUPLICATE_HIGH_AFFILI2, L1_TABLE);
                }
            }
        });        

        adjustAffiliationTable(lev1_Table);
        addAffiliationSelectionListener();      
        
        loadLev1_Table(0, "");    
        setFormMode(NormalMode);
        changeItemsEnabled(lev1_Table, true);
    }

    private void insertAffiliation(JTable table, ControlEnums.DialogMessages diagMsg, 
            ControlEnums.TableType tableType) 
    {
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
                
                if (tableType == L1_TABLE) {
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
                    cancelButton.setEnabled(false);
                    closeFormButton.setEnabled(true);
                    
                    setLev_Editable(currLevel, false);
                    table.getCellEditor().stopCellEditing();
                    changeItemsEnabled(lev1_Table, lev1_Radio.isSelected());
                    if (tableType == L1_TABLE) {
                        loadLev1_Table(-1, affiliName); // Refresh the list
                    } else {
//                        loadL2_Affiliation(parentKey, -1, affiliName); // Refresh the list                        
                    }
                } else if (result == ER_DUP_ENTRY) {
                    abortInsertion(affiliName + diagMsg.getContent(), lev1_Table);
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
        backToNormal(table, L1_TABLE);
        loadLev1_Table(table.getSelectedRow(), "");        
    }    
    
    private void updateAffiliation(JTable affiliTable, Object prevAffiliName, String keyCol, 
            ControlEnums.DialogMessages msgForDuplicate, ControlEnums.TableType tableType) 
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
            backToNormal(affiliTable, tableType);

            if (affiliTable == lev1_Table) {
                loadLev1_Table(-1, affiliName); // Refresh higher affiliation list
//            } else if (affiliTable == L2_Affiliation) {
//                int index = lev1_Table.convertRowIndexToModel(lev1_Table.getSelectedRow());
//                Object L1_no = lev1_Table.getModel().getValueAt(index, 2);
//                
//                loadL2_Affiliation(L1_no, 0, "");
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

//        saveSheet_Button.setEnabled(false);
        
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
            
            if (isManager) {
//                saveSheet_Button.setEnabled(true);
            }
            // Adjust first column width checking row count, etc.
            String countStr = Integer.toString(numRows);
            int numWidthFont = getStringWidth(countStr, lev1_Table.getFont());
            int width = numWidthFont + 40;
            TableColumn column = lev1_Table.getColumnModel().getColumn(0);
            
            if (column.getPreferredWidth() < width) {
                column.setPreferredWidth(width); 
                column.setMinWidth(width); 
            }
        }
        else
        {
//            modifyL1_Button.setEnabled(false);                
//            deleteL1_Button.setEnabled(false);                      
//            loadL2_Affiliation(null, 0, "");
        }
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
        topPanel = new javax.swing.JPanel();
        GUI_Title_Panel = new javax.swing.JPanel();
        workPanel = new javax.swing.JLabel();
        modeString = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        westPanel = new javax.swing.JPanel();
        wholePanel = new javax.swing.JPanel();
        lev1_Panel = new javax.swing.JPanel();
        title1_Panel = new javax.swing.JPanel();
        affiTopTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lev1_Table = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return lev_Editable[currLevel];
            }
        }
        ;
        radio1Panel = new javax.swing.JPanel();
        lev1_Radio = new javax.swing.JRadioButton();
        recordMenuPanel = new javax.swing.JPanel();
        insertSaveButton = new javax.swing.JButton();
        updateSaveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        southPanel = new javax.swing.JPanel();
        tableMenuPanel = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        sampleButton = new javax.swing.JButton();
        ODSAffiliHelp = new javax.swing.JButton();
        readSheet = new javax.swing.JButton();
        saveSheet_Button = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        eastPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(540, 498));
        setPreferredSize(new java.awt.Dimension(540, 618));

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

        westPanel.setPreferredSize(new java.awt.Dimension(40, 626));

        javax.swing.GroupLayout westPanelLayout = new javax.swing.GroupLayout(westPanel);
        westPanel.setLayout(westPanelLayout);
        westPanelLayout.setHorizontalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        westPanelLayout.setVerticalGroup(
            westPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 626, Short.MAX_VALUE)
        );

        getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

        wholePanel.setMinimumSize(new java.awt.Dimension(460, 20));
        wholePanel.setPreferredSize(new java.awt.Dimension(460, 626));

        lev1_Panel.setMinimumSize(new java.awt.Dimension(200, 200));
        lev1_Panel.setPreferredSize(new java.awt.Dimension(220, 360));

        affiTopTitle.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        affiTopTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        affiTopTitle.setText(AFFILIATION_LIST_LABEL.getContent());
        affiTopTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                affiTopTitleMouseClicked(evt);
            }
        });
        title1_Panel.add(affiTopTitle);

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
        jScrollPane1.setViewportView(lev1_Table);

        radio1Panel.setMaximumSize(new java.awt.Dimension(32767, 31));

        affi_Group.add(lev1_Radio);
        lev1_Radio.setSelected(true);
        lev1_Radio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lev1_RadioItemStateChanged(evt);
            }
        });
        radio1Panel.add(lev1_Radio);

        javax.swing.GroupLayout lev1_PanelLayout = new javax.swing.GroupLayout(lev1_Panel);
        lev1_Panel.setLayout(lev1_PanelLayout);
        lev1_PanelLayout.setHorizontalGroup(
            lev1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(title1_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(radio1Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        lev1_PanelLayout.setVerticalGroup(
            lev1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lev1_PanelLayout.createSequentialGroup()
                .addComponent(title1_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(radio1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        insertSaveButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        insertSaveButton.setMnemonic('R');
        insertSaveButton.setText(CREATE_BTN.getContent());
        insertSaveButton.setEnabled(false);
        insertSaveButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSaveButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSaveButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
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

        jSeparator1.setMinimumSize(new java.awt.Dimension(50, 20));
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 20));

        javax.swing.GroupLayout wholePanelLayout = new javax.swing.GroupLayout(wholePanel);
        wholePanel.setLayout(wholePanelLayout);
        wholePanelLayout.setHorizontalGroup(
            wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(recordMenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(wholePanelLayout.createSequentialGroup()
                .addComponent(lev1_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addGap(255, 255, 255))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        wholePanelLayout.setVerticalGroup(
            wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wholePanelLayout.createSequentialGroup()
                .addComponent(lev1_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(recordMenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        southPanel.setMinimumSize(new java.awt.Dimension(530, 110));
        southPanel.setPreferredSize(new java.awt.Dimension(10, 85));
        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.Y_AXIS));

        tableMenuPanel.setMaximumSize(new Dimension(4000, 50));
        tableMenuPanel.setMinimumSize(new Dimension(150, 50));
        tableMenuPanel.setPreferredSize(new Dimension(300, 45));

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

        javax.swing.GroupLayout tableMenuPanelLayout = new javax.swing.GroupLayout(tableMenuPanel);
        tableMenuPanel.setLayout(tableMenuPanelLayout);
        tableMenuPanelLayout.setHorizontalGroup(
            tableMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableMenuPanelLayout.createSequentialGroup()
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(sampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(ODSAffiliHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(readSheet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(closeFormButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        tableMenuPanelLayout.setVerticalGroup(
            tableMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableMenuPanelLayout.createSequentialGroup()
                .addGroup(tableMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tableMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(ODSAffiliHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(tableMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(saveSheet_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(readSheet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sampleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(closeFormButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(tableMenuPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        southPanel.add(tableMenuPanel);
        southPanel.add(filler1);

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        eastPanel.setPreferredSize(new java.awt.Dimension(40, 626));

        javax.swing.GroupLayout eastPanelLayout = new javax.swing.GroupLayout(eastPanel);
        eastPanel.setLayout(eastPanelLayout);
        eastPanelLayout.setHorizontalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        eastPanelLayout.setVerticalGroup(
            eastPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 626, Short.MAX_VALUE)
        );

        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void insertSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSaveButtonActionPerformed
        if (lev1_Radio.isSelected()) {
            if (formMode == NormalMode) {
//                setFormMode(FormMode.CreateMode);
                //<editor-fold desc="--Prepare to create a new affiliation">
                /**
                 * Change management button enabled properties.
                 */                
                insertSaveButton.setText(SAVE_BTN.getContent());
                insertSaveButton.setMnemonic('s');            
                updateSaveButton.setEnabled(false);
                deleteButton.setEnabled(false);
                cancelButton.setEnabled(true);
                closeFormButton.setEnabled(false);
                
                DefaultTableModel model = (DefaultTableModel)lev1_Table.getModel();
                model.setRowCount(lev1_Table.getRowCount() + 1);
                int rowIndex = lev1_Table.getRowCount() - 1;

                if (lev1_Table.getValueAt(rowIndex, 1) != null) 
                {
                    rowIndex = 0;
                }
                lev1_Table.setRowSelectionInterval(rowIndex, rowIndex);

                ((DefaultTableModel)lev1_Table.getModel()).
                        setValueAt(INSERT_TOOLTIP.getContent(), rowIndex, 1);

                
                setLev_Editable(currLevel, true);
                if (lev1_Table.editCellAt(rowIndex, 1))
                {
                    lev1_Table.getEditorComponent().requestFocus();
                    lev1_Table.scrollRectToVisible(
                            new Rectangle(lev1_Table.getCellRect(rowIndex, 0, true)));
                }
                setFormMode(FormMode.CreateMode);
                
                //</editor-fold>
            } else {
                lev1_Table.getCellEditor().stopCellEditing();                
            }
        }
    }//GEN-LAST:event_insertSaveButtonActionPerformed

    private void updateSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateSaveButtonActionPerformed
        try {
            switch (formMode) {
                case NormalMode:
                    // <editor-fold defaultstate="collapsed" desc="-- Prepare to update user information">
                    startModify(lev1_Table);
                    break;
                    // </editor-fold>                
                case UpdateMode:
                    // <editor-fold defaultstate="collapsed" desc="-- save modified user information ">
                    lev1_Table.getCellEditor().stopCellEditing();
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                        "(User requested command: " + evt.getActionCommand() + ")");             
        }        
        
//        int col = affiTable.getSelectedColumn();
//        setUpdateMode(true, 1);
//        setUpdateMode(true, (col == -1 ? 0 : col));        
    }//GEN-LAST:event_updateSaveButtonActionPerformed

    private void startModify(JTable table) {
        if (lev1_Radio.isSelected()) {
            if (formMode == NormalMode) {
                setFormMode(FormMode.UpdateMode);
                /**
                 * Change management button enabled properties.
                 */
                insertSaveButton.setEnabled(false);
                updateSaveButton.setText(SAVE_BTN.getContent());
                updateSaveButton.setMnemonic('s');            
                deleteButton.setEnabled(false);
                cancelButton.setEnabled(true);
                closeFormButton.setEnabled(false);
                
                int rowIndex = table.getSelectedRow();
                int model_index = table.convertRowIndexToModel(rowIndex);
                TableModel model = table.getModel();

                prevL1Name = model.getValueAt(model_index, 1);
//                prevL2Name = model.getValueAt(model_index, 1);
//                result = getUserConfirmationL2(model_index);

                setLev_Editable(currLevel, true);
                if (lev1_Table.editCellAt(rowIndex, 1)) {
                    lev1_Table.getEditorComponent().requestFocus();
                }
            } else {
                setFormMode(FormMode.UpdateMode);
                insertSaveButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }
    }
    

    private void enableRadioButtons(boolean b) {
        lev1_Radio.setEnabled(b);
//        changeControlEnabledForTable(L1_TABLE);
//        lev2_Radio.setEnabled(b);
//        changeControlEnabledForTable(L2_TABLE);
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
                
//                changeRadioButtonsEnabled(false);
//                changeBottomButtonsEnbled(false);   
//                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
//                        CREATE_SAVE_HELP.getContent(), !createBlinked);  
//                createBlinked = true;
//                adminOperationEnabled(false, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;   
                
            case NormalMode:               
                modeString.setText(SEARCH.getContent());
                enableRadioButtons(true);
                
//                changeRadioButtonsEnabled(true);
//                changeBottomButtonsEnbled(true);
//                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
//                        CHOOSE_PANEL_DIALOG.getContent(), !normalBlinked); 
//                normalBlinked = true;
//                adminOperationEnabled(true, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            case UpdateMode:
                modeString.setText(MODIFY.getContent());
                enableRadioButtons(false);
                
//                changeRadioButtonsEnabled(false);
//                changeBottomButtonsEnbled(false);   
//                (new LabelBlinker()).displayHelpMessage(csHelpLabel, UPDATE_SAVE_HELP.getContent(), 
//                        !updateBlinked);                  
//                updateBlinked = true;
//                adminOperationEnabled(false, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            default:
                break;
        } 
    }    
    
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // Delete currently selected higher affiliation
        int viewIndex = lev1_Table.getSelectedRow();
        if (viewIndex == -1)
        {
            return;
        }
        String affiliation = (String)lev1_Table.getValueAt(viewIndex, 1);
        int modal_Index = lev1_Table.convertRowIndexToModel(viewIndex);
        int L1_no = (int)lev1_Table.getModel().getValueAt(modal_Index, 2);
        int count = getL2RecordCount(L1_no);

        String dialogMessage = AFFILI_DEL_L1.getContent() + System.getProperty("line.separator")
        + AFFILI_DIAG_L2.getContent() + affiliation + System.getProperty("line.separator")
        + AFFILI_DIAG_L3.getContent() + count;

        int result = JOptionPane.showConfirmDialog(this, dialogMessage,
            DELETE_DIALOGTITLE.getContent(),
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            //<editor-fold desc="delete upper level affliation (unit name)">
            String excepMsg = "(In deletion of: " + affiliation + ")";
            String sql = "Delete From L1_Affiliation Where L1_no = ?";

            result = deleteHigherRow(excepMsg, sql, L1_no);

            if (result == 1) {
                loadLev1_Table(viewIndex, ""); // Deliver the index of deleted row

                dialogMessage = AFFILI_DEL_RESULT.getContent() +
                System.getProperty("line.separator") +
                AFFILI_DIAG_L2.getContent() + affiliation;

                JOptionPane.showConfirmDialog(this, dialogMessage,
                    DELETE_RESULT_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    
    // Delete currently selected table row
    private int deleteHigherRow(String excepMsg, String sql, int bldg_seq_no) 
    {
        //<editor-fold desc="-- Actual deletion of a building number">
        Connection conn = null;
        PreparedStatement createBuilding = null;
        int result = 0;
        
        try {
            conn = getConnection();
            createBuilding = conn.prepareStatement(sql);
            createBuilding.setInt(1, bldg_seq_no);

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
        if (formMode == FormMode.CreateMode) {
//            abortCreation(L1_TABLE);
            removeToNormal(lev1_Table);
        } else if (formMode == FormMode.UpdateMode) {
            backToNormal(lev1_Table, L1_TABLE);
            loadLev1_Table(lev1_Table.getSelectedRow(), "");
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void removeToNormal(JTable table)
    {
        setFormMode(FormMode.NormalMode);  
        insertSaveButton.setText(CREATE_BTN.getContent());
        insertSaveButton.setMnemonic('R');                     
        cancelButton.setEnabled(false);
        closeFormButton.setEnabled(true);
        
        setLev_Editable(currLevel, false);
        table.getCellEditor().stopCellEditing();
        changeItemsEnabled(lev1_Table, lev1_Radio.isSelected());
        
        ((DefaultTableModel)table.getModel()).setRowCount(table.getRowCount() - 1);
        
        // Return to read mode as if [Enter] key pressed.
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }    
    
    private void lev1_RadioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lev1_RadioItemStateChanged
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                manageSelection(evt, lev1_Table);
                changeControlEnabledForTable(L1_TABLE);
            }

            private void manageSelection(ItemEvent evt, JTable table) {
                if (evt.getStateChange() == SELECTED) {
                    table.setSelectionBackground(DARK_BLUE);
                } else {
                    table.setSelectionBackground(LIGHT_BLUE);
                }
            }
        });
    }//GEN-LAST:event_lev1_RadioItemStateChanged

    private void affiTopTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_affiTopTitleMouseClicked
//        affiL1_Control.setSelected(true);
    }//GEN-LAST:event_affiTopTitleMouseClicked

    private void readSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheetActionPerformed
        if (currTable ==  L1_TABLE || currTable == L2_TABLE)
        {
//            loadAffiliationsFromODS();
        } else {
//            loadBuildingsFromODS();
        }
    }//GEN-LAST:event_readSheetActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
//        if (affiL1_Control.isSelected()) {
//            saveODSfileName(this, L1_Affiliation, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), L1_TABLE.getContent());
//        } else if (affiL2_Control.isSelected()) {
//            saveODSfileName(this, L2_Affiliation, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), affiBotTitle.getText());
//        } else if (buildingControl.isSelected()) {
//            saveODSfileName(this, BuildingTable, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), Building.getContent());
//        } else if (unitControl.isSelected()) {
//            saveODSfileName(this, UnitTable, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), UnitLabel.getText());
//        }
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void ODSAffiliHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSAffiliHelpActionPerformed
        JDialog helpDialog;

//        if (chosenPanelFor() == AFFILIATION) {
//            helpDialog = new ODS_HelpJDialog(this, false,
//                HELP_AFFIL_LABEL.getContent(),
//                ODS_TYPE.AFFILIATION);
//        } else {
//            helpDialog = new ODS_HelpJDialog(this, false,
//                HELP_BUILDING_LABEL.getContent(),
//                ODS_TYPE.BUILDING);
//        }
//        setHelpDialogLoc(ODSAffiliHelp, helpDialog);
//        helpDialog.setVisible(true);
    }//GEN-LAST:event_ODSAffiliHelpActionPerformed

    private void sampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleButtonActionPerformed
        String sampleFile = "";

//        if (chosenPanelFor() == AFFILIATION) {
//            sampleFile = "/affiliations";
//        } else {
//            sampleFile = "/buildings";
//        }

        // Ask user the name and location for the ods file to save
        StringBuffer odsFullPath = new StringBuffer();

//        if (wantToSaveFile(this, saveFileChooser, odsFullPath, sampleFile)) {
//            // Read sample ods resource file
//            String extension = saveFileChooser.getFileFilter().getDescription();
//
//            if (extension.indexOf("*.ods") >= 0 && !odsFullPath.toString().endsWith(".ods")) {
//                odsFullPath.append(".ods");
//            }
//
//            InputStream sampleIn = getClass().getResourceAsStream(sampleFile + ".ods");
//
//            downloadSample(odsFullPath.toString(), sampleIn, sampleFile);
//            if (sampleIn != null) {
//                try {
//                    sampleIn.close();
//                } catch (IOException e) {
//                    logParkingException(Level.SEVERE, e, sampleFile + " istrm close error");
//                }
//            }
//        }
    }//GEN-LAST:event_sampleButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        tryToCloseSettingsForm();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void backToNormal(JTable table, ControlEnums.TableType tableType) {
        setFormMode(FormMode.NormalMode);
        updateSaveButton.setText(MODIFY_BTN.getContent());
        updateSaveButton.setMnemonic('M');
        cancelButton.setEnabled(false);
        closeFormButton.setEnabled(true);
            
        setLev_Editable(currLevel, false);
        
        table.getCellEditor().stopCellEditing();
//        changeControlEnabledForTable(tableType);
        changeItemsEnabled(lev1_Table, lev1_Radio.isSelected());
    }    
    
    ControlEnums.TableType currTable = null;
    

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
//        if (firstRowIsDummy(table)) {
//            ((DefaultTableModel)table.getModel()).setRowCount(0);
//        }
        
        int idx = followAndGetTrueIndex(table);    
        
        //<editor-fold desc="-- Change selected panel border">
        if (selected) {
            if (lev1_Radio.isSelected()) {
//                    topLeft.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
//                    affiTopTitle.setForeground(pointColor);
            } else {
//                    botLeft.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
//                    affiBotTitle.setForeground(pointColor);
            }
        } else {
            if (lev1_Radio.isSelected()) {
//                    topLeft.setBorder(null);
//                    affiTopTitle.setForeground(black);
            } else {
//                    botLeft.setBorder(null);
//                    affiBotTitle.setForeground(black);
            }
        }
        //</editor-fold>
        if (selected) {
            if (idx == -1 && table.getRowCount() > 0) {
                idx = 0;
                table.setRowSelectionInterval(idx, idx);
            }
        }
        
        if (idx >= 0)
        {
            Object L1_no = table.getModel().getValueAt(idx, 2);
            boolean enable = L1_no != null 
                    && selected 
                    && formMode == FormMode.NormalMode;
            if (isManager) {
                updateSaveButton.setEnabled(enable);
                deleteButton.setEnabled(enable);
            } else {
                updateSaveButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        }
        
        if (isManager) {
            insertSaveButton.setEnabled(formMode == FormMode.NormalMode && selected);
        } else {
            insertSaveButton.setEnabled(false);
        }
        table.setEnabled(selected);
//        if (selected) {
//            resizeComponentFor(modeString, affiTopTitle.getText());
//            modeString.setText(affiTopTitle.getText());
//        }
    }

    private void changeControlEnabledForTable(ControlEnums.TableType table) {
        switch (table) {
            case L1_TABLE: 
                changeItemsEnabled(lev1_Table, lev1_Radio.isSelected());
                break;
            case L2_TABLE: 
//                changeItemsEnabled(L2_Affiliation, affiL2_Control.isSelected(),
//                        insertL2_Button, modifyL2_Button, deleteL2_Button);
//                if (!affiL2_Control.isSelected()) {
//                    addDummyFirstRow((DefaultTableModel)L2_Affiliation.getModel());
//                }
                break;
            default:
                break;
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
    private javax.swing.JLabel affiTopTitle;
    private javax.swing.ButtonGroup affi_Group;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel eastPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton insertSaveButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel lev1_Panel;
    private javax.swing.JRadioButton lev1_Radio;
    private javax.swing.JTable lev1_Table;
    private javax.swing.JLabel modeString;
    private javax.swing.JPanel radio1Panel;
    private javax.swing.JButton readSheet;
    private javax.swing.JPanel recordMenuPanel;
    private javax.swing.JButton sampleButton;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel tableMenuPanel;
    private javax.swing.JPanel title1_Panel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JButton updateSaveButton;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    private javax.swing.JLabel workPanel;
    // End of variables declaration//GEN-END:variables

//    private void setUpdateMode(boolean editIt, int col) {
//        setLev_Editable(currLevel, editIt);
//        
//        if (editIt) {
////            jTable1.changeSelection(0, col, false, false);
//            lev1_Table.editCellAt(0, col);
//            lev1_Table.getEditorComponent().requestFocus();
//        }
////        cancelButton.setEnabled(editIt);
//        updateSaveButton.setEnabled(!editIt);
//    }

    private void setLev_Editable(int index, boolean b) {
        lev_Editable[index] = b;
    }

    private void addAffiliationSelectionListener() {
        ListSelectionModel cellSelectionModel = lev1_Table.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
                            int index1 = followAndGetTrueIndex(lev1_Table);

                            if (index1 >= 0) 
                            {
                                Object L1_no = lev1_Table.getModel().getValueAt(index1, 2);
                                updateSaveButton.setEnabled(L1_no != null && isManager ? true : false);
                                deleteButton.setEnabled(L1_no != null && isManager ? true : false);
//                                loadL2_Affiliation(L1_no, 0, "");
                            }
                            else
                            {
                                // loadL2_Affiliation(null, 0, ""); abcd
                            }

                            // clear L2List selection
//                            L2_Affiliation.removeEditor();
//                            L2_Affiliation.getSelectionModel().clearSelection();  

                            // Delete an empty row if existed
                            if (emptyLastRowPossible(insertSaveButton, lev1_Table))
                            {
                                removeEmptyRow(insertSaveButton, lev1_Table);                    
                            }
                        }
                    }
                });                 
            }
        }); 
        
//        cellSelectionModel = L2_Affiliation.getSelectionModel();
//        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
//        {
//            public void valueChanged(ListSelectionEvent  e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        if (!e.getValueIsAdjusting())
//                        {
//                            int index2 = followAndGetTrueIndex(L2_Affiliation);
//                            if (index2 >= 0)
//                            {
//                                Object L2_no = L2_Affiliation.getModel().getValueAt(index2, 2);
//                                deleteL2_Button.setEnabled(L2_no != null && isManager ? true : false); 
//                                modifyL2_Button.setEnabled(L2_no != null && isManager ? true : false);
//                            }
//                            else
//                            {
//                                deleteL2_Button.setEnabled(false);     
//                                modifyL2_Button.setEnabled(false);     
//                            }
//
//                            // Delete an empty row if existed
//                            if (emptyLastRowPossible(insertL2_Button, L2_Affiliation))
//                            {
//                                removeEmptyRow(insertL2_Button, L2_Affiliation);
//                            }
//                        }
//                    }
//                });
//            }
//        });
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
}
