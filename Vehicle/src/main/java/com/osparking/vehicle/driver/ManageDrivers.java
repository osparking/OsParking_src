
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
package com.osparking.vehicle.driver;

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import com.osparking.global.ChangedComponentClear;
import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.FIRST_ROW;
import static com.osparking.global.CommonData.NOT_LISTED;
import static com.osparking.global.CommonData.ODS_DIRECTORY;
import static com.osparking.global.CommonData.PROMPTER_KEY;
import static com.osparking.global.CommonData.adminOperationEnabled;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.downloadSample;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.normGUIwidth;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.setKeyboardLanguage;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.DataSheet.saveODSfile;
import com.osparking.global.Globals;
import static com.osparking.vehicle.driver.DriverTable.updateRow;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import static java.awt.event.KeyEvent.VK_SHIFT;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.getRecordCount;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.SetAColumnWidth;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.attachNumberCondition;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.findLoginIdentity;
import static com.osparking.global.Globals.getPrompter;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.isManager;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingOperation;
import static com.osparking.global.Globals.refreshComboBox;
import static com.osparking.global.Globals.rejectUserInput;
import static com.osparking.global.Globals.showLicensePanel;
import com.osparking.global.IMainGUI;
import com.osparking.global.names.ControlEnums;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMessages.*;
import static com.osparking.global.names.ControlEnums.DialogMsg.DRIVER_ODS_SAMPLE_FILE;
import static com.osparking.global.names.ControlEnums.DialogMsg.M_OWNER_DEL_CONF_1;
import static com.osparking.global.names.ControlEnums.DialogMsg.M_OWNER_DEL_CONF_2;
import static com.osparking.global.names.ControlEnums.DialogMsg.M_OWNER_DEL_CONF_3;
import static com.osparking.global.names.ControlEnums.DialogMsg.M_OWNER_DEL_CONF_4;
import static com.osparking.global.names.ControlEnums.DialogMsg.M_OWNER_DEL_CONF_5;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_CONF_1;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_CONF_2;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_CONF_3;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_CONF_4;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_RESU_1;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_RESU_2;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_RESU_3;
import static com.osparking.global.names.ControlEnums.DialogMsg.OWNER_DEL_RESU_4;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.*;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.FormModeString.SEARCH;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FOCUS_MOVE_NOTE;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_DRIVER_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODIFY_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REQUIRE_FIELD_NOTE;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import com.osparking.global.names.ControlEnums.OsPaTable;
import static com.osparking.global.names.ControlEnums.OsPaTable.CarDriver;
import static com.osparking.global.names.ControlEnums.TitleTypes.DRIVER_LIST_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.*;
import static com.osparking.global.names.ControlEnums.TextType.*;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CELL_PHONE_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_PNG;
import static com.osparking.global.names.ControlEnums.ToolTipContent.LANDLINE_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.SEARCH_BTN_TOOLTIP;
import com.osparking.global.names.InnoComboBoxItem;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.CellPhone;
import static com.osparking.global.names.OSP_enums.DriverCol.DriverName;
import static com.osparking.global.names.OSP_enums.DriverCol.LandLine;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import static com.osparking.global.names.OSP_enums.OpLogLevel.UserCarChange;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.WrappedInt;
import static com.osparking.vehicle.CommonData.DTCW_BN;
import static com.osparking.vehicle.CommonData.DTCW_CP;
import static com.osparking.vehicle.CommonData.DTCW_DN;
import static com.osparking.vehicle.CommonData.DTCW_L1;
import static com.osparking.vehicle.CommonData.DTCW_L2;
import static com.osparking.vehicle.CommonData.DTCW_LL;
import static com.osparking.vehicle.CommonData.DTCW_MAX;
import static com.osparking.vehicle.CommonData.DTCW_RN;
import static com.osparking.vehicle.CommonData.DTCW_UN;
import static com.osparking.vehicle.CommonData.DTC_MARGIN;
import static com.osparking.vehicle.CommonData.attachLikeCondition;
import static com.osparking.vehicle.CommonData.wantToSaveFile;
import static com.osparking.vehicle.CommonData.invalidCell;
import static com.osparking.vehicle.CommonData.invalidName;
import static com.osparking.vehicle.CommonData.invalidPhone;
import static com.osparking.vehicle.CommonData.prependEscape;
import static com.osparking.vehicle.CommonData.setHelpDialogLoc;
import com.osparking.vehicle.LabelBlinker;
import com.osparking.vehicle.ODS_HelpJDialog;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.awt.event.ItemListener;
import java.io.InputStream;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ManageDrivers extends javax.swing.JFrame {

    private FormMode formMode = FormMode.NormalMode; 
    private DriverSelection driverSelectionForm = null; 
    
    private boolean nameHintShown = true;
    private boolean cellHintShown = true;    
    private boolean phoneHintShown = true;    
    private String prevSearchCondition = null;    
    private String currSearchCondition = "";    
    private String prevSearchString = null;    
    private String currSearchString = "";       
    private List<String> prevKeyList = new ArrayList<String>();
    private List<String> currKeyList = new ArrayList<String>();
    static private ChangedComponentClear changedControls; 
    IMainGUI mainForm = null;
    boolean isStandalone = false;    
    /**
     * Key of parent combobox item for which (this) child combobox item 
     * list is formed.
     */
    private int[] prevListParentKey = new int[OSP_enums.DriverCol.values().length];
    
    /**
     * Key of parent combobox item for which currently shown child 
     * combobox item selection is determined.
     */
    private int[] prevItemParentKey = new int[OSP_enums.DriverCol.values().length];
    
    /**
     * Creates new form ManageDrivers
     */
    public ManageDrivers(IMainGUI mainForm, DriverSelection driverSelectionForm) {
        initComponents();
        this.mainForm = mainForm;
        if (mainForm == null) {
            isStandalone = true;
        }        
        this.driverSelectionForm = driverSelectionForm;
        changedControls = new ChangedComponentClear(clearButton);
        
        setIconImages(OSPiconList);      
        for (int i = 0; i <prevItemParentKey.length; i++) {
            prevItemParentKey[i] = -1;
        }
        
        /**
         * Change buttons text.
         */
        insertSave_Button.setText(CREATE_BTN.getContent());
        modiSave_Button.setText(MODIFY_BTN.getContent());
        deleteButton.setText(DELETE_BTN.getContent());
        cancelButton.setText(CANCEL_BTN.getContent());
        deleteAll_button.setText(DELETE_ALL_BTN.getContent());
        readSheet_Button.setText(READ_ODS_BTN.getContent());
        saveSheet_Button.setText(SAVE_ODS_BTN.getContent());
        closeFormButton.setText(CLOSE_BTN.getContent());
        
        setLocation(0, 0); 
        
        refreshComboBox(searchL1ComboBox, getPrompter(AffiliationL1, searchL1ComboBox),
                AffiliationL1, -1, getPrevListParentKey());
        searchL2ComboBox.addItem(getPrompter(AffiliationL2, searchL1ComboBox));
        
        refreshComboBox(searchBuildingComboBox, getPrompter(BuildingNo, searchBuildingComboBox),
                BuildingNo, -1, getPrevListParentKey());        
        searchUnitComboBox.addItem(getPrompter(UnitNo, searchBuildingComboBox));        

        /**
         * Tune driver table visual effects.
         */
        changeTableSizeEtc();
        
        affiliationL1CBox.setFont(null);
        
        setupHigherComboBox(affiliationL1CBox, AffiliationL1, AffiliationL2);
        setupLowerComboBox(AffiliationL2);
        setupHigherComboBox(buildingCBox, BuildingNo, UnitNo);
        setupLowerComboBox(UnitNo);
        
        /**
         * Disable sorting for each and every column of this table
         */
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(driverTable.getModel());
        for (int i=0 ; i<driverTable.getColumnCount() ; i++) {
            sorter.setSortable(i, false);
        }
        driverTable.setRowSorter(sorter);  
        adminOperationEnabled(true, deleteAll_button, odsHelpButton, sampleButton, readSheet_Button);

        /**
         * Initialize table with real driver information
         */
        loadDriverData(FIRST_ROW, "", "");
        setFormMode(FormMode.NormalMode);
        setSearchEnabled(true);
    }
    
    /** 
     * Change lower level combobox prompt when an item in this combobox selected.
     * @param e an event that happen on this level of combobox.
     * @param thisCBox current(=this) level combobox where possibly an item is selected.
     * @param childColumn table column for the lower level combobox.
     */
    private void mayChangeLowerCBoxPrompt(AWTEvent e, DriverCol childColumn) 
    {
        JComboBox cBox = (JComboBox)(e.getSource());
        ConvComboBoxItem item = (ConvComboBoxItem)cBox.getSelectedItem();
        
        if (item == null) {
            return;
        } else {
            int currKey = (Integer)item.getKeyValue(); 
            int rowIdx = driverTable.getSelectedRow();
            int colIdx = childColumn.getNumVal() - 1; 

            if (rowIdx != -1 && getPrevItemParentKey()[childColumn.getNumVal()] != currKey) {
                getPrevItemParentKey()[childColumn.getNumVal()] = currKey;
                System.out.println("Row: " + rowIdx + ", Col: " + colIdx);
                TableCellEditor editor = driverTable.getCellEditor(rowIdx, colIdx);
                Object parentCBox = (JComboBox)(((DefaultCellEditor)editor).getComponent());

                driverTable.setValueAt(
                        getPrompter(childColumn, parentCBox),
                        rowIdx, 
                        childColumn.getNumVal());
            }
        }
    }

    static boolean checkIfUserWantsToSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static JComboBox affiliationL1CBox = new PComboBox();
    static JComboBox buildingCBox = new PComboBox();
    /**
     * @return the formMode
     */
    public FormMode getFormMode() {
        return formMode;
    }
    
    int releaseCount = 0;

    private void changeSearchButtonEnabled() {
        currSearchCondition = formSearchCondition();
        
        currKeyList = new ArrayList<String>();
        currSearchString = formSearchString(currKeyList);
        if (currSearchCondition.equals(prevSearchCondition) 
                && currSearchString.equals(prevSearchString)
                && currKeyList.equals(prevKeyList)) 
        {
            searchButton.setEnabled(false);
        } else {
            searchButton.setEnabled(true);
        }
    }

    private void setSearchEnabled(boolean flag) {
        searchName.setEnabled(flag);
        searchCell.setEnabled(flag);
        searchPhone.setEnabled(flag);
        searchL1ComboBox.setEnabled(flag);
        searchL2ComboBox.setEnabled(flag);
        searchBuildingComboBox.setEnabled(flag);
        searchUnitComboBox.setEnabled(flag);
        closeFormButton.setEnabled(flag);     
        cancelButton.setEnabled(!flag);    
        
        if (flag && isManager) {
            if (driverTable.getRowCount() > 0) {
                saveSheet_Button.setEnabled(true);
            } else {
                saveSheet_Button.setEnabled(false);
            }
        } else {
            saveSheet_Button.setEnabled(false);
        }
    }    
    
    /**
     * @param newMode the formMode to set
     */
    public void setFormMode(FormMode newMode) {
        FormMode prevMode = formMode;

        formMode = newMode;
        switch (newMode) {
            case CreateMode:
                formModeLabel.setText(CREATE_MODE_LABEL.getContent());
                setSearchEnabled(false);
                insertSave_Button.setText(SAVE_BTN.getContent());
                insertSave_Button.setMnemonic('s');
                modiSave_Button.setEnabled(false);
                deleteButton.setEnabled(false);
                deleteAll_button.setEnabled(false);
                adminOperationEnabled(false, deleteAll_button, odsHelpButton, sampleButton, readSheet_Button);
                tipLabel.setVisible(true);
                break;
                
            case UpdateMode:
                formModeLabel.setText(MODIFY_MODE_LABEL.getContent());
                setSearchEnabled(false);
                insertSave_Button.setEnabled(false);
                modiSave_Button.setText(SAVE_BTN.getContent());
                modiSave_Button.setMnemonic('s');
                deleteButton.setEnabled(false);
                adminOperationEnabled(false, deleteAll_button, odsHelpButton, sampleButton, readSheet_Button);
                tipLabel.setVisible(true);                
                break;
                
            case NormalMode:
                formModeLabel.setText(SEARCH_MODE_LABEL.getContent());
                setSearchEnabled(true);
                if (prevMode == FormMode.CreateMode) {
                    insertSave_Button.setText(CREATE_BTN.getContent());
                    insertSave_Button.setMnemonic('r');
                } else if (prevMode == FormMode.UpdateMode) {
                    modiSave_Button.setText(MODIFY_BTN.getContent());
                    modiSave_Button.setMnemonic('m');
                }
                insertSave_Button.setEnabled(true && isManager);
                
                if (driverTable.getSelectedRowCount() > 0) {
                    modiSave_Button.setEnabled(isManager);
                    deleteButton.setEnabled(isManager);
                }
                adminOperationEnabled(true, deleteAll_button, odsHelpButton, sampleButton, readSheet_Button);
                tipLabel.setVisible(false);
                break;
            default:
                break;
        }
        initPrevParentKey(newMode);
    }
    
    static int changeCount = 0;
    private void addDriverSelectionListener() {
        
        ListSelectionModel cellSelectionModel = driverTable.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            @Override
            public void valueChanged(ListSelectionEvent  e) {
                if (e.getValueIsAdjusting()) 
                    return;
                
                switch(getFormMode()) {
                    case NormalMode:
                        if (driverTable.getSelectedRowCount() == 0) {
                            modiSave_Button.setEnabled(false);
                            deleteButton.setEnabled(false);
                        } else {
                            modiSave_Button.setEnabled(isManager);
                            deleteButton.setEnabled(isManager);
                        }
                        break;
                    case CreateMode: 
                        int rowIndex = driverTable.getRowCount() - 1;

                        if (driverTable.getSelectedRow() != rowIndex) {
                            // return to the row being created and and start editing the cell
                            // that is at the same column as the selected cell.
                            int clickCol = driverTable.getSelectedColumn();

                            if (driverTable.editCellAt(rowIndex, clickCol)) {
                                startEditingCell(rowIndex, (clickCol == 0 ? 1 : clickCol));
                            }
                        }
                        break;
                    case UpdateMode:
                        if (driverTable.getSelectedRow() != updateRow) {
                            // return to the row being created and and start editing the cell
                            // that is at the same column as the selected cell.
                            int clickCol = driverTable.getSelectedColumn();

                            if (driverTable.editCellAt(updateRow, clickCol)) {
                                startEditingCell(updateRow, (clickCol == 0 ? 1 : clickCol));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }); 
    }

    private int insertCarDriver(String driverName, int rowIndex) {
            
        Connection conn = null;
        PreparedStatement insertDriver = null;
        String excepMsg = "failed creation of a car driver: " + driverName;

        int result = 0;
        String landLine = null;
        String itemL2name = null;
        String itemUnitName = null;
        try {
            //<editor-fold desc="insert the new driver into the database">
            String sql = "Insert Into cardriver (name, CELLPHONE, PHONE, L2_NO" +
                            ", UNIT_SEQ_NO, CREATIONDATE)" + 
                            " Values (?, ?, ?, ?, ?, current_timestamp)";

            //<editor-fold desc="prepare property values for the new driver">
            TableModel model = driverTable.getModel();
            String cellPhone = ((String)model.getValueAt(rowIndex, 
                    DriverCol.CellPhone.getNumVal())).trim();
            landLine = ((String)model.getValueAt(rowIndex, 
                    DriverCol.LandLine.getNumVal())).trim();

            String itemL2_NO = null;
            InnoComboBoxItem itemL2 = (InnoComboBoxItem)(model.getValueAt(rowIndex, 
                    DriverCol.AffiliationL2.getNumVal()));
            
            if (itemL2.getKeys()[0] != PROMPTER_KEY) {
                itemL2_NO = String.valueOf(itemL2.getKeys()[0]);
                itemL2name = itemL2.getLabels()[0];
            }

            String itemUnitSEQ_NO = null;
            InnoComboBoxItem itemUnit = (InnoComboBoxItem)(model.getValueAt(rowIndex, 
                    DriverCol.UnitNo.getNumVal()));
            
            if (itemUnit.getKeys()[0] != PROMPTER_KEY) {
                itemUnitSEQ_NO = String.valueOf(itemUnit.getKeys()[0]);
                itemUnitName = itemUnit.getLabels()[0];
            }
            //</editor-fold>
                            
            conn = getConnection();
            insertDriver = conn.prepareStatement(sql);
            int paraIdx = 1;
            insertDriver.setString(paraIdx++, driverName);
            insertDriver.setString(paraIdx++, cellPhone);
            insertDriver.setString(paraIdx++, landLine);
            insertDriver.setString(paraIdx++, itemL2_NO);
            insertDriver.setString(paraIdx++, itemUnitSEQ_NO);

            result = insertDriver.executeUpdate();
            //</editor-fold>
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                rejectUserInput(driverTable, rowIndex, "<Name, Cell Phone> pair");                     
            }
            else {
                logParkingException(Level.SEVERE, ex, excepMsg);
            }                 
        } finally {
            closeDBstuff(conn, insertDriver, null, excepMsg);
            if (result == 1)
            {
                //<editor-fold desc="redisplay driver list with a new driver just added">     
                // if insertion was successful, then redisplay the list
                rowIndex = driverTable.getSelectedRow();
                int colM = driverTable.convertColumnIndexToModel(
                        DriverCol.DriverName.getNumVal());

                // conditions that make driver information insufficient: 1, 2
                driverName = ((String)driverTable.getModel().getValueAt(rowIndex, colM)).trim(); 
                colM = driverTable.convertColumnIndexToModel(
                        DriverCol.CellPhone.getNumVal());
                String cell = String.valueOf(driverTable.getValueAt(rowIndex, colM)).toLowerCase();                    

                setFormMode(FormMode.NormalMode);
                loadDriverData(UNKNOWN, driverName, cell); // insert > refresh list
                
                StringBuffer driverProperties = new StringBuffer();
            
                driverProperties.append("Driver Creation Summary: " + System.lineSeparator());
                
                getDriverProperties(driverName, cell, driverProperties, rowIndex, landLine, itemL2name, itemUnitName);  
                Globals.logParkingOperation(UserCarChange, driverProperties.toString(), Globals.GENERAL_DEVICE);
                
                // Redisplay the skinny driver selection form which this driver manage form is invoked
                if (driverSelectionForm != null) {
                    driverSelectionForm.loadSkinnyDriverTable(0); // 0: highlight first row
                }      
                
                JOptionPane.showConfirmDialog(this, CREATION_SUCCESS_DIALOG.getContent() + driverName,
                        CREATION_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);   
                //</editor-fold>
            }else {
                JOptionPane.showConfirmDialog(null, DRIVER_CREATRION_FAIL_DIALOG.getContent(),
                        CREATION_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            }            
        }
        return result;        
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        odsFileChooser = new javax.swing.JFileChooser();
        saveFileChooser = new javax.swing.JFileChooser();
        northPanel = new javax.swing.JPanel();
        westPanel = new javax.swing.JPanel();
        wholePanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        titleLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        myMetaKeyLabel = new javax.swing.JLabel();
        topButtonPanel = new javax.swing.JPanel();
        topLTpanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        topMid_1 = new javax.swing.JPanel();
        tipLabel = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(100, 0), new java.awt.Dimension(32767, 0));
        topCenter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        formModeLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(100, 0), new java.awt.Dimension(32767, 0));
        balancer = new javax.swing.JPanel();
        topRHpanel = new javax.swing.JPanel();
        clearButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        filler15_6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        searchPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchName = new javax.swing.JTextField();
        searchCell = new javax.swing.JTextField();
        searchPhone = new javax.swing.JTextField();
        searchL1ComboBox = new PComboBox();
        searchL2ComboBox = new PComboBox<InnoComboBoxItem>();
        searchBuildingComboBox = new PComboBox();
        searchUnitComboBox = new PComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        driversScrollPane = new javax.swing.JScrollPane();
        driversTable = new javax.swing.JTable();
        filler15_5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        bottomButtonPanel = new javax.swing.JPanel();
        leftButtons = new javax.swing.JPanel();
        insertSave_Button = new javax.swing.JButton();
        modiSave_Button = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        rightButtons = new javax.swing.JPanel();
        deleteAll_button = new javax.swing.JButton();
        readSheet_Button = new javax.swing.JButton();
        saveSheet_Button = new javax.swing.JButton();
        closeFormButton = new javax.swing.JButton();
        HelpPanel = new javax.swing.JPanel();
        odsHelpButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        sampleButton = new javax.swing.JButton();
        eastPanel = new javax.swing.JPanel();
        southPanel = new javax.swing.JPanel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        odsFileChooser.setCurrentDirectory(ODS_DIRECTORY);

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        saveFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(DRIVER_LIST_FRAME_TITLE.getContent());
        setMinimumSize(new Dimension(normGUIwidth, normGUIheight));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        northPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        northPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        northPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        northPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 45, 15));
        getContentPane().add(northPanel, java.awt.BorderLayout.NORTH);

        westPanel.setMaximumSize(new java.awt.Dimension(40, 32767));
        westPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        westPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

        wholePanel.setMinimumSize(new Dimension(normGUIwidth - 80,normGUIheight - 80));
        wholePanel.setPreferredSize(new Dimension(normGUIwidth - 80,normGUIheight - 80));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        titlePanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        titlePanel.setMinimumSize(new java.awt.Dimension(110, 40));
        titlePanel.setPreferredSize(new java.awt.Dimension(210, 40));
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText(LicenseButton.getContent());
        seeLicenseButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        seeLicenseButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        titlePanel.add(seeLicenseButton);
        titlePanel.add(filler2);

        titleLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(DRIVER_LIST_FRAME_TITLE.getContent());
        titleLabel.setMaximumSize(new java.awt.Dimension(110, 28));
        titleLabel.setMinimumSize(new java.awt.Dimension(110, 28));
        titleLabel.setPreferredSize(new java.awt.Dimension(110, 28));
        titlePanel.add(titleLabel);
        titlePanel.add(filler3);

        jPanel1.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        jPanel1.add(myMetaKeyLabel);

        titlePanel.add(jPanel1);

        wholePanel.add(titlePanel);

        topButtonPanel.setMaximumSize(new java.awt.Dimension(33095, 40));
        topButtonPanel.setMinimumSize(new Dimension(normGUIwidth - 80,40));
        topButtonPanel.setPreferredSize(new Dimension(normGUIwidth - 80,40));
        topButtonPanel.setLayout(new javax.swing.BoxLayout(topButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        topLTpanel.setMaximumSize(new java.awt.Dimension(160, 40));
        topLTpanel.setMinimumSize(new java.awt.Dimension(160, 40));
        topLTpanel.setPreferredSize(new java.awt.Dimension(160, 40));
        topLTpanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 22));

        countLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        countLbl.setText(COUNT_LABEL.getContent());
        JLabel tempLabel = new JLabel(COUNT_LABEL.getContent());
        tempLabel.setFont(countLbl.getFont());
        Dimension dim = tempLabel.getPreferredSize();
        countLbl.setMaximumSize(new java.awt.Dimension(110, 27));
        countLbl.setMinimumSize(new java.awt.Dimension(90, 27));
        countLbl.setPreferredSize(new Dimension(dim.width + 1, dim.height));
        topLTpanel.add(countLbl);

        countValue.setForeground(pointColor);
        countValue.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countValue.setText("count");
        topLTpanel.add(countValue);

        topButtonPanel.add(topLTpanel);

        topMid_1.setMaximumSize(new java.awt.Dimension(210, 40));
        topMid_1.setMinimumSize(new java.awt.Dimension(210, 40));
        topMid_1.setPreferredSize(new java.awt.Dimension(210, 40));
        topMid_1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 22));

        tipLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tipLabel.setMaximumSize(new java.awt.Dimension(200, 18));
        tipLabel.setMinimumSize(new java.awt.Dimension(200, 18));
        tipLabel.setPreferredSize(new java.awt.Dimension(200, 18));
        topMid_1.add(tipLabel);

        topButtonPanel.add(topMid_1);
        topButtonPanel.add(filler4);

        topCenter.setMaximumSize(new java.awt.Dimension(300, 40));
        topCenter.setMinimumSize(new java.awt.Dimension(159, 40));
        topCenter.setPreferredSize(new java.awt.Dimension(210, 40));
        topCenter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 10));

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setText(MODE_LABEL.getContent());
        topCenter.add(jLabel2);

        formModeLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        formModeLabel.setForeground(pointColor);
        formModeLabel.setText(SEARCH.getContent());
        formModeLabel.setToolTipText("");
        topCenter.add(formModeLabel);

        topButtonPanel.add(topCenter);
        topButtonPanel.add(filler5);

        balancer.setMaximumSize(new java.awt.Dimension(163, 40));
        balancer.setMinimumSize(new java.awt.Dimension(163, 40));
        balancer.setName(""); // NOI18N
        balancer.setPreferredSize(new java.awt.Dimension(163, 40));

        javax.swing.GroupLayout balancerLayout = new javax.swing.GroupLayout(balancer);
        balancer.setLayout(balancerLayout);
        balancerLayout.setHorizontalGroup(
            balancerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 163, Short.MAX_VALUE)
        );
        balancerLayout.setVerticalGroup(
            balancerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        topButtonPanel.add(balancer);

        topRHpanel.setMaximumSize(new java.awt.Dimension(210, 40));
        topRHpanel.setMinimumSize(new java.awt.Dimension(210, 40));
        topRHpanel.setPreferredSize(new java.awt.Dimension(210, 40));
        topRHpanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        clearButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearButton.setMnemonic('l');
        clearButton.setText("초기화(L)");
        clearButton.setEnabled(false);
        clearButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        topRHpanel.add(clearButton);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText("검색(S)");
        searchButton.setToolTipText(SEARCH_BTN_TOOLTIP.getContent());
        searchButton.setEnabled(false);
        searchButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        topRHpanel.add(searchButton);

        topButtonPanel.add(topRHpanel);

        wholePanel.add(topButtonPanel);
        wholePanel.add(filler15_6);

        searchPanel.setMaximumSize(new java.awt.Dimension(2095, 28));
        searchPanel.setLayout(new javax.swing.BoxLayout(searchPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(SEARCH_LABEL.getContent());
        jLabel3.setMaximumSize(new Dimension(DTCW_RN - DTC_MARGIN, 28));
        jLabel3.setMinimumSize(new Dimension(DTCW_RN - DTC_MARGIN, 28));
        jLabel3.setPreferredSize(new Dimension(DTCW_RN - DTC_MARGIN, 28));
        searchPanel.add(jLabel3);
        jLabel3.getAccessibleContext().setAccessibleName("default");

        searchPanel.add(filler66);

        searchName.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchName.setForeground(tipColor);
        searchName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchName.setText(DRIVER_TF.getContent());
        searchName.setToolTipText(DRIVER_INPUT_TOOLTIP.getContent());
        searchName.setMaximumSize(new Dimension(DTCW_DN * DTCW_MAX - DTC_MARGIN, 28));
        searchName.setMinimumSize(new Dimension(DTCW_DN - DTC_MARGIN, 28));
        searchName.setPreferredSize(new Dimension(DTCW_DN - DTC_MARGIN, 28));
        searchName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchNameFocusLost(evt);
            }
        });
        searchName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchNameMousePressed(evt);
            }
        });
        searchName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchNameKeyTyped(evt);
            }
        });
        searchPanel.add(searchName);
        searchName.getAccessibleContext().setAccessibleName("");

        searchCell.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCell.setForeground(tipColor);
        searchCell.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchCell.setText(CELL_PHONE_TF.getContent());
        searchCell.setToolTipText(CELL_PHONE_INPUT_TOOLTIP.getContent());
        searchCell.setMaximumSize(new Dimension(DTCW_CP*DTCW_MAX - DTC_MARGIN, 28));
        searchCell.setMinimumSize(new Dimension(DTCW_CP - DTC_MARGIN, 28));
        searchCell.setPreferredSize(new Dimension(DTCW_CP - DTC_MARGIN, 28));
        searchCell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchCellFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchCellFocusLost(evt);
            }
        });
        searchCell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchCellMousePressed(evt);
            }
        });
        searchCell.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchCellKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchCellKeyTyped(evt);
            }
        });
        searchPanel.add(searchCell);
        searchCell.getAccessibleContext().setAccessibleName("");

        searchPhone.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchPhone.setForeground(tipColor);
        searchPhone.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchPhone.setText(LANDLINE_TF.getContent());
        searchPhone.setToolTipText(LANDLINE_INPUT_TOOLTIP.getContent());
        searchPhone.setMaximumSize(new Dimension(DTCW_LL * DTCW_MAX - DTC_MARGIN, 28));
        searchPhone.setMinimumSize(new Dimension(DTCW_LL - DTC_MARGIN, 28));
        searchPhone.setPreferredSize(new Dimension(DTCW_LL - DTC_MARGIN, 28));
        searchPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchPhoneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchPhoneFocusLost(evt);
            }
        });
        searchPhone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchPhoneMousePressed(evt);
            }
        });
        searchPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchPhoneKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchPhoneKeyTyped(evt);
            }
        });
        searchPanel.add(searchPhone);
        searchPhone.getAccessibleContext().setAccessibleName("");

        searchL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchL1ComboBox.setToolTipText("Search Affiliation2");
        searchL1ComboBox.setMaximumSize(new Dimension(DTCW_L1*DTCW_MAX - DTC_MARGIN, 28));
        searchL1ComboBox.setMinimumSize(new Dimension(DTCW_L1 - DTC_MARGIN, 28));
        searchL1ComboBox.setPreferredSize(new Dimension(DTCW_L1 - DTC_MARGIN, 28));
        searchL1ComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchL1ComboBoxItemStateChanged(evt);
            }
        });
        searchL1ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchL1ComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchL1ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchL1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchL1ComboBoxActionPerformed(evt);
            }
        });
        searchPanel.add(searchL1ComboBox);
        searchL1ComboBox.getAccessibleContext().setAccessibleName("");

        searchL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL2ComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchL2ComboBox.setToolTipText("Search Affiliation1");
        searchL2ComboBox.setMaximumSize(new Dimension(DTCW_L2*DTCW_MAX - DTC_MARGIN, 28));
        searchL2ComboBox.setMinimumSize(new Dimension(DTCW_L2 - DTC_MARGIN, 28));
        searchL2ComboBox.setPreferredSize(new Dimension(DTCW_L2 - DTC_MARGIN, 28));
        searchL2ComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchL2ComboBoxItemStateChanged(evt);
            }
        });
        searchL2ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchL2ComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchL2ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchPanel.add(searchL2ComboBox);
        searchL2ComboBox.getAccessibleContext().setAccessibleName("");

        searchBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBuildingComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchBuildingComboBox.setToolTipText("Search Building");
        searchBuildingComboBox.setMaximumSize(new Dimension(DTCW_BN*DTCW_MAX - DTC_MARGIN, 28));
        searchBuildingComboBox.setMinimumSize(new Dimension(DTCW_BN - DTC_MARGIN, 28));
        searchBuildingComboBox.setPreferredSize(new Dimension(DTCW_BN - DTC_MARGIN, 28));
        searchBuildingComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchBuildingComboBoxItemStateChanged(evt);
            }
        });
        searchBuildingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchBuildingComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        searchBuildingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBuildingComboBoxActionPerformed(evt);
            }
        });
        searchPanel.add(searchBuildingComboBox);
        searchBuildingComboBox.getAccessibleContext().setAccessibleName("");

        searchUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchUnitComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchUnitComboBox.setToolTipText("Search Unit");
        searchUnitComboBox.setMaximumSize(new Dimension(DTCW_UN*DTCW_MAX - DTC_MARGIN, 28));
        searchUnitComboBox.setMinimumSize(new Dimension(DTCW_UN - DTC_MARGIN, 28));
        searchUnitComboBox.setPreferredSize(new Dimension(DTCW_UN - DTC_MARGIN, 28));
        searchUnitComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchUnitComboBoxItemStateChanged(evt);
            }
        });
        searchUnitComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchUnitComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchUnitComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchPanel.add(searchUnitComboBox);
        searchUnitComboBox.getAccessibleContext().setAccessibleName("");

        searchPanel.add(filler1);

        wholePanel.add(searchPanel);

        driversScrollPane.setPreferredSize(new java.awt.Dimension(452, 200));

        driversTable.setAutoCreateRowSorter(true);
        driversTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driversTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driversTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        driversTable.setInheritsPopupMenu(true);
        driversTable.setPreferredSize(new java.awt.Dimension(300, 0));
        driversTable.setRowHeight(tableRowHeight);
        driversTable.setRowSelectionAllowed(false);
        driversTable.setRowSorter(null);
        driversTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                driversTableFocusGained(evt);
            }
        });
        driversTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                driversTableKeyReleased(evt);
            }
        });
        driversScrollPane.setViewportView(driversTable);

        wholePanel.add(driversScrollPane);
        wholePanel.add(filler15_5);

        bottomButtonPanel.setMaximumSize(new java.awt.Dimension(33727, 70));
        bottomButtonPanel.setPreferredSize(new java.awt.Dimension(902, 70));

        leftButtons.setMaximumSize(new java.awt.Dimension(32767, 40));
        leftButtons.setMinimumSize(new java.awt.Dimension(300, 40));
        leftButtons.setPreferredSize(new java.awt.Dimension(400, 40));
        leftButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        insertSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        insertSave_Button.setMnemonic('r');
        insertSave_Button.setText("생성(R)");
        insertSave_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSave_ButtonActionPerformed(evt);
            }
        });
        leftButtons.add(insertSave_Button);

        modiSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modiSave_Button.setMnemonic('m');
        modiSave_Button.setText("수정(M)");
        modiSave_Button.setEnabled(false);
        modiSave_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modiSave_ButtonActionPerformed(evt);
            }
        });
        leftButtons.add(modiSave_Button);

        deleteButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteButton.setMnemonic('d');
        deleteButton.setText("삭제(D)");
        deleteButton.setEnabled(false);
        deleteButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        leftButtons.add(deleteButton);

        cancelButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cancelButton.setMnemonic('C');
        cancelButton.setText("취소(C)");
        cancelButton.setEnabled(false);
        cancelButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        leftButtons.add(cancelButton);

        rightButtons.setAlignmentX(1.0F);
        rightButtons.setMaximumSize(new java.awt.Dimension(32767, 70));
        rightButtons.setMinimumSize(new java.awt.Dimension(350, 70));
        rightButtons.setPreferredSize(new java.awt.Dimension(470, 70));
        java.awt.GridBagLayout rightButtonsLayout = new java.awt.GridBagLayout();
        rightButtonsLayout.columnWidths = new int[] {0, 10, 0, 10, 0, 10, 0, 10, 0};
        rightButtonsLayout.rowHeights = new int[] {0, 0, 0};
        rightButtons.setLayout(rightButtonsLayout);

        deleteAll_button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteAll_button.setMnemonic('a');
        deleteAll_button.setText("전체삭제(E)");
        deleteAll_button.setEnabled(false);
        deleteAll_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deleteAll_button.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAll_button.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAll_button.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAll_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAll_buttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rightButtons.add(deleteAll_button, gridBagConstraints);

        readSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet_Button.setMnemonic('O');
        readSheet_Button.setText("ods읽기");
        readSheet_Button.setEnabled(false);
        readSheet_Button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        readSheet_Button.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheet_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        rightButtons.add(readSheet_Button, gridBagConstraints);

        saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveSheet_Button.setMnemonic('A');
        saveSheet_Button.setText("ods저장(A)");
        saveSheet_Button.setEnabled(false);
        saveSheet_Button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveSheet_Button.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSheet_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        rightButtons.add(saveSheet_Button, gridBagConstraints);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText("닫기(C)");
        closeFormButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        rightButtons.add(closeFormButton, gridBagConstraints);

        HelpPanel.setMaximumSize(new java.awt.Dimension(110, 70));
        HelpPanel.setMinimumSize(new java.awt.Dimension(110, 30));
        HelpPanel.setPreferredSize(new java.awt.Dimension(110, 30));
        HelpPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        odsHelpButton.setBackground(new java.awt.Color(153, 255, 153));
        odsHelpButton.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        odsHelpButton.setIcon(getQuest20_Icon());
        odsHelpButton.setToolTipText(DRIVER_ODS_UPLOAD_SAMPLE_PNG.getContent());
        odsHelpButton.setIconTextGap(0);
        odsHelpButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        odsHelpButton.setMaximumSize(new java.awt.Dimension(20, 20));
        odsHelpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        odsHelpButton.setOpaque(false);
        odsHelpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        odsHelpButton.setRequestFocusEnabled(false);
        odsHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                odsHelpButtonActionPerformed(evt);
            }
        });
        HelpPanel.add(odsHelpButton);
        HelpPanel.add(filler6);

        sampleButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        sampleButton.setMnemonic('P');
        sampleButton.setText(SAMPLE_BTN.getContent());
        sampleButton.setToolTipText(DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD.getContent());
        sampleButton.setEnabled(false);
        sampleButton.setMaximumSize(new java.awt.Dimension(80, 30));
        sampleButton.setMinimumSize(new java.awt.Dimension(80, 30));
        sampleButton.setPreferredSize(new java.awt.Dimension(80, 30));
        sampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleButtonActionPerformed(evt);
            }
        });
        HelpPanel.add(sampleButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        rightButtons.add(HelpPanel, gridBagConstraints);

        javax.swing.GroupLayout bottomButtonPanelLayout = new javax.swing.GroupLayout(bottomButtonPanel);
        bottomButtonPanel.setLayout(bottomButtonPanelLayout);
        bottomButtonPanelLayout.setHorizontalGroup(
            bottomButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomButtonPanelLayout.createSequentialGroup()
                .addComponent(leftButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        bottomButtonPanelLayout.setVerticalGroup(
            bottomButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        wholePanel.add(bottomButtonPanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        eastPanel.setMaximumSize(new java.awt.Dimension(40, 32767));
        eastPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        eastPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    int rowBeforeCreate = -1;
    int colBeforeCreate = -1;
    
    private void insertSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSave_ButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        
        setKeyboardLanguage(driverTable, KOREAN);
        if (getFormMode() == FormMode.CreateMode) {
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
            }
            finalizeDriverCreation();            
        } else {
            //<editor-fold desc="-- Process driver creation request">            
            if (rowV != -1)
                rowBeforeCreate = driverTable.convertRowIndexToModel(rowV);
            colBeforeCreate = driverTable.getSelectedColumn();  
            
            DefaultTableModel model = (DefaultTableModel)driverTable.getModel();
            model.addRow(new Object [] {null, "", "", "", 
                getPrompter(AffiliationL1, null), 
                getPrompter(AffiliationL2, null), 
                getPrompter(BuildingNo, null), 
                getPrompter(UnitNo, null), 
                null});

            int rowIndex = driverTable.getRowCount() - 1;
            boolean isNameEmpty = driverTable.getValueAt(rowIndex, 1).equals("");
            
            creatingRowM = rowIndex;
            if (!isNameEmpty) 
            {
                rowIndex = 0;
            }

            setFormMode(FormMode.CreateMode);

            highlightTableRow(driverTable, rowIndex);
            if (driverTable.editCellAt(rowIndex, 1))
            {
                startEditingCell(rowIndex, 1);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_insertSave_ButtonActionPerformed

    static int creatingRowM = -1;    
    
    private void deleteAll_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAll_buttonActionPerformed
        int driverCount = getRecordCount(CarDriver, -1);
        int vehiclecount = getRecordCount(OsPaTable.Vehicles, -1);
        
        String dialogMessage = DELETE_ALL_DRIVER_P1.getContent() + System.lineSeparator() +
                DELETE_ALL_DRIVER_P2.getContent() + driverCount + System.lineSeparator() +
                DELETE_ALL_DRIVER_P3.getContent() + vehiclecount;
        
        int result = JOptionPane.showConfirmDialog(this, dialogMessage,
                DELETE_ALL_DAILOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement deleteDrivers = null;
            String excepMsg = "every driver deletion";

            result = 0;
            try {
                String sql = "Delete From cardriver";
                
                conn = getConnection();
                deleteDrivers = conn.prepareStatement(sql);
                result = deleteDrivers.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, "(All Driver Deletion)");
            } finally {
                closeDBstuff(conn, deleteDrivers, null, excepMsg);
            }

            loadDriverData(UNKNOWN, "", "");
            JOptionPane.showConfirmDialog(this, DRIVER_DELETE_ALL_RESULT_DAILOG.getContent(),
                    DELETE_ALL_RESULT_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteAll_buttonActionPerformed
   
    private void modiSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modiSave_ButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        int rowM = driverTable.convertRowIndexToModel(rowV);
        int colV = driverTable.getSelectedColumn();
        
        if (driverTable.getSelectedRows().length > 1) {
            JOptionPane.showConfirmDialog(this, DRIVER_MODIFY_FAIL_DAILOG.getContent(),
                    MODIFY_FAIL_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);            
            return;
        }

        if (getFormMode() == FormMode.UpdateMode) {
            //<editor-fold desc="-- Process modification save request">
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
            }
            finalizeDriverUpdate(rowM);            
            //</editor-fold>
        } else {        
            if (colV <= 0) {
                colV = 2; 
            }

            int driverSeqNo = (Integer)driverTable.getModel().getValueAt(rowM, 
                    DriverCol.SEQ_NO.getNumVal());        

            int response = driverTable.askUserOnUpdate((String)
                    driverTable.getModel().getValueAt(rowM, DriverCol.DriverName.getNumVal()),
                    getRecordCount(OsPaTable.Vehicles, driverSeqNo));

            if (response == JOptionPane.YES_OPTION) {        
                updateRow = rowM;
                setUpdateMode(true);   
                highlightTableRow(driverTable, rowV);
                if (driverTable.editCellAt(rowV, colV))
                {
                    startEditingCell(rowV, colV);
                }  
            }
        }
    }//GEN-LAST:event_modiSave_ButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        int rowM = -1;
        if (rowV != -1)
            rowM = driverTable.convertRowIndexToModel(rowV);
        int colV = driverTable.getSelectedColumn();        

        // delete a driver's record currently selected 
        int[] deleteIndice = driverTable.getSelectedRows();
        if (deleteIndice.length == 0)
        {
            return;
        }

        int result = -1;
        String driverName = (String)driverTable.getValueAt(deleteIndice[0], 1);
        int modal_Index = driverTable.convertRowIndexToModel(deleteIndice[0]);
        int CD_SEQ_NO = (int)driverTable.getModel().getValueAt(modal_Index, 
                DriverCol.SEQ_NO.getNumVal());
        int count = getRecordCount(OsPaTable.Vehicles, CD_SEQ_NO);

        if (deleteIndice.length == 1) {
            String dialogMessage = OWNER_DEL_CONF_1.getContent() + System.getProperty("line.separator") +
                            OWNER_DEL_CONF_2.getContent() + driverName + OWNER_DEL_CONF_3.getContent() + 
                    count + OWNER_DEL_CONF_4.getContent();

            result = JOptionPane.showConfirmDialog(this, dialogMessage,
                        DELETE_DIALOGTITLE.getContent(), 
                        JOptionPane.YES_NO_OPTION);
        } else {
            String dialogMessage = M_OWNER_DEL_CONF_1.getContent() + deleteIndice.length + 
                    M_OWNER_DEL_CONF_2.getContent() + System.getProperty("line.separator") + 
                    M_OWNER_DEL_CONF_3.getContent() + driverName + 
                    M_OWNER_DEL_CONF_4.getContent() + count + M_OWNER_DEL_CONF_5.getContent();

            result = JOptionPane.showConfirmDialog(this, dialogMessage, 
                        DELETE_DIALOGTITLE.getContent(), 
                        JOptionPane.YES_NO_OPTION);
        }

        if (result == JOptionPane.YES_OPTION) {
            // <editor-fold defaultstate="collapsed" desc="-- delete driver and car information ">   
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = "(while deleting a driver: " + driverName + ")";
            int totalDeletion = 0;

            result = -1;
            try {
                String sql = "Delete From carDriver Where SEQ_NO = ?";

                conn = getConnection();
                createBuilding = conn.prepareStatement(sql);
                for (int indexNo : deleteIndice) {
                    modal_Index = driverTable.convertRowIndexToModel(indexNo);
                    createBuilding.setInt(1, (int)driverTable.getModel().getValueAt(modal_Index, 
                            DriverCol.SEQ_NO.getNumVal()));
                    result = createBuilding.executeUpdate();
                    totalDeletion += result;
                }
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);

                if (result == 1) {
                    loadDriverData(deleteIndice[0], "", ""); // passes index of the deleted row

                    String dialogMessage = OWNER_DEL_RESU_1.getContent() + driverName + 
                            OWNER_DEL_RESU_2.getContent() + System.lineSeparator() + 
                            OWNER_DEL_RESU_3.getContent() + totalDeletion + OWNER_DEL_RESU_4.getContent();  

                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void driversTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_driversTableKeyReleased
        if (getFormMode() == FormMode.NormalMode 
                || evt.getKeyCode() == VK_SHIFT ) {
            return; // in view mode, don't need to save update or insertion.
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {  
                if (getFormMode() == FormMode.CreateMode ) {
                    finalizeDriverCreation();
                } else {
                    int row = driverTable.convertRowIndexToModel(driverTable.getSelectedRow());
                    finalizeDriverUpdate(row);
                }
            }
        });  
    }//GEN-LAST:event_driversTableKeyReleased

    private void driversTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_driversTableFocusGained

        int row = driverTable. getSelectedRow();
        int col = driverTable. getSelectedColumn();
        
        if (col == -1) {
            col = 2;
        }

        if (getFormMode() == FormMode.UpdateMode) {
            if (col != editingCol) {
                row = updateRow;

                if (driverTable.editCellAt(row, col))
                {
                    startEditingCell(row, col);
                } else {
                    if (driverTable.editCellAt(row, 1))
                        startEditingCell(row, 1);
                }
            }
        } else if (getFormMode() == FormMode.CreateMode) {
            if (col != editingCol) 
            {
                row = driverTable.getRowCount() - 1;

                if (driverTable.editCellAt(row, col))
                {
                    startEditingCell(row, col);
                } else {
                    if (driverTable.editCellAt(row, 1))
                        startEditingCell(row, 1);
                }
            }
        }
    }//GEN-LAST:event_driversTableFocusGained

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadDriverData(FIRST_ROW, "", "");
        focusLeftMostCondition();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
        TableColumnModel NumberTableModel = driverTable.getColumnModel();
        NumberTableModel.addColumn(hiddenSN);   
        
        saveODSfile(this, driverTable, saveFileChooser, DRIVER_SAVE_ODS_FAIL_DIALOG.getContent());
        NumberTableModel.removeColumn(hiddenSN);   
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        closeFrameGracefully();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeFrameGracefully();
    }//GEN-LAST:event_formWindowClosing

    private void searchL1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchL1ComboBoxActionPerformed
        Globals.mayChangeLowerCBoxPrompt(searchL1ComboBox, searchL2ComboBox, 
                AffiliationL2, getPrevItemParentKey());
    }//GEN-LAST:event_searchL1ComboBoxActionPerformed

    private void searchBuildingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxActionPerformed
        Globals.mayChangeLowerCBoxPrompt(searchBuildingComboBox, searchUnitComboBox, 
                UnitNo, getPrevItemParentKey());
    }//GEN-LAST:event_searchBuildingComboBoxActionPerformed

    private void searchL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeVisible
        mayRefreshLowerComboBox(searchL1ComboBox, searchL2ComboBox, AffiliationL2);
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeVisible

    private void searchUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeVisible
        mayRefreshLowerComboBox(searchBuildingComboBox, searchUnitComboBox, UnitNo);
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeVisible

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearButton.setEnabled(false);
        
        showNameTip();
        showCellTip();
        showPhoneTip();
        
        searchPhone.setText(LANDLINE_TF.getContent());
        searchL1ComboBox.setSelectedIndex(0);
        searchL2ComboBox.setSelectedIndex(0);
        searchBuildingComboBox.setSelectedIndex(0);
        searchUnitComboBox.setSelectedIndex(0);
        
        changeSearchButtonEnabled();
        driverTable.requestFocus();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void showNameTip() {
        searchName.setText(DRIVER_TF.getContent());
        nameHintShown = true;
        searchName.setForeground(tipColor);
    }
    
    private void searchNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusLost
        if (searchName.getText().trim().length() == 0) {
            showNameTip();
        }           
    }//GEN-LAST:event_searchNameFocusLost

    private void searchNameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchNameMousePressed
        searchName.selectAll();
    }//GEN-LAST:event_searchNameMousePressed

    private void showCellTip() {
        searchCell.setText(CELL_PHONE_TF.getContent());
        cellHintShown = true;
        searchCell.setForeground(tipColor);
    }
            
    private void searchCellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusLost
        if (searchCell.getText().trim().length() == 0) {
            showCellTip();
        }           
    }//GEN-LAST:event_searchCellFocusLost
    
    private void showPhoneTip() {
        searchPhone.setText(LANDLINE_TF.getContent());
        phoneHintShown = true;
        searchPhone.setForeground(tipColor);
    }

    private void searchCellMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchCellMousePressed
        searchCell.selectAll();
    }//GEN-LAST:event_searchCellMousePressed

    private void searchPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchPhoneFocusLost
        if (searchPhone.getText().trim().length() == 0) {
            showPhoneTip();
        }         
    }//GEN-LAST:event_searchPhoneFocusLost

    private void searchPhoneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchPhoneMousePressed
        searchPhone.selectAll();
    }//GEN-LAST:event_searchPhoneMousePressed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
                                                    
        int rowV = driverTable.getSelectedRow();
        int rowM = -1;
        if (rowV != -1)
            rowM = driverTable.convertRowIndexToModel(rowV);
        int colV = driverTable.getSelectedColumn();        

        if (getFormMode() == FormMode.CreateMode) {
            //<editor-fold desc="-- Handle driver creation cancellation request">
            int response = JOptionPane.showConfirmDialog(this, 
                    DRIVER_CREATE_CANCEL_DIALOG.getContent(),
                    CANCEL_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        
            if (response == JOptionPane.YES_OPTION) {
                // Confirmed cancel request
                setFormMode(FormMode.NormalMode);
                
                // remove last row which was prepared for the new driver information
                ((DefaultTableModel)driverTable.getModel()).setRowCount(driverTable.getRowCount() - 1);     
                
                // highlight originally selected row if existed.
                if (rowBeforeCreate != -1) {
                    if (colBeforeCreate != -1) 
                        driverTable.changeSelection(rowBeforeCreate, colBeforeCreate, false, false);
                    highlightTableRow(driverTable, rowBeforeCreate);
                    driverTable.requestFocusInWindow();
                }
            } else {
                if (driverTable.editCellAt(rowM, colV))
                    startEditingCell(rowM, colV);
            }         
            //</editor-fold>
        } else if (getFormMode() == FormMode.UpdateMode) {
            //<editor-fold desc="-- Process modification cancel request">
            int response = JOptionPane.showConfirmDialog(this, 
                    DRIVER_MODIFY_CANCEL_DAILOG.getContent(),
                    CANCEL_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);            
            
            if (response == JOptionPane.NO_OPTION) {
                if (driverTable.editCellAt(rowV, colV))
                    startEditingCell(rowV, colV);
            } else {
                restoreDriverList(rowM);
            } 
            //</editor-fold>            
        } else {
            // It is Search Mode.
            // delete a driver's record currently selected 
            int[] deleteIndice = driverTable.getSelectedRows();
            if (deleteIndice.length == 0)
            {
                return;
            }

            int result = -1;
            String driverName = (String)driverTable.getValueAt(deleteIndice[0], 1);
            int modal_Index = driverTable.convertRowIndexToModel(deleteIndice[0]);
            int CD_SEQ_NO = (int)driverTable.getModel().getValueAt(modal_Index, 
                    DriverCol.SEQ_NO.getNumVal());
            int count = getRecordCount(OsPaTable.Vehicles, CD_SEQ_NO);

            if (deleteIndice.length == 1) {
                
                String dialogMessage = OWNER_DEL_CONF_1.getContent() + System.getProperty("line.separator") +
                        OWNER_DEL_CONF_2.getContent() + driverName + OWNER_DEL_CONF_3.getContent() + count + 
                        OWNER_DEL_CONF_4.getContent();
                
                result = JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_DIALOGTITLE.getContent(), 
                            JOptionPane.YES_NO_OPTION);
            } else {
                String dialogMessage = M_OWNER_DEL_CONF_1.getContent() + deleteIndice.length + 
                        M_OWNER_DEL_CONF_2.getContent() + System.getProperty("line.separator") + 
                        M_OWNER_DEL_CONF_3.getContent() + driverName + 
                        M_OWNER_DEL_CONF_4.getContent() + count + M_OWNER_DEL_CONF_5.getContent();                
                
                result = JOptionPane.showConfirmDialog(this, dialogMessage, 
                            DELETE_DIALOGTITLE.getContent(), 
                            JOptionPane.YES_NO_OPTION);
            }

            if (result == JOptionPane.YES_OPTION) {
                // <editor-fold defaultstate="collapsed" desc="-- delete driver and car information ">   
                Connection conn = null;
                PreparedStatement createBuilding = null;
                String excepMsg = "(while deleting a driver: " + driverName + ")";
                int totalDeletion = 0;

                result = -1;
                try {
                    String sql = "Delete From carDriver Where SEQ_NO = ?";

                    conn = getConnection();
                    createBuilding = conn.prepareStatement(sql);
                    for (int indexNo : deleteIndice) {
                        modal_Index = driverTable.convertRowIndexToModel(indexNo);
                        createBuilding.setInt(1, (int)driverTable.getModel().getValueAt(modal_Index, 
                                DriverCol.SEQ_NO.getNumVal()));
                        result = createBuilding.executeUpdate();
                        totalDeletion += result;
                    }
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                } finally {
                    closeDBstuff(conn, createBuilding, null, excepMsg);

                    if (result == 1) {
                        loadDriverData(deleteIndice[0], "", ""); // passes index of the deleted row

                        String dialogMessage = 
                                OWNER_DEL_RESU_1.getContent() + driverName + OWNER_DEL_RESU_2.getContent() + 
                                System.lineSeparator() + OWNER_DEL_RESU_3.getContent() + totalDeletion + OWNER_DEL_RESU_4.getContent();                          
                        
                        JOptionPane.showConfirmDialog(this, dialogMessage,
                                DELETE_RESULT_DIALOGTITLE.getContent(),
                                JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                    }
                }
                //</editor-fold>
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void readSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_ButtonActionPerformed
        try {
            int returnVal = odsFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {                
                File file = odsFileChooser.getSelectedFile();
                ArrayList<Point> wrongCells = new ArrayList<Point>();

                Sheet sheet = null;
                try {
                    sheet = SpreadSheet.createFromFile(file).getSheet(0);
                } catch (IOException ex) {
                    Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (sheet != null)
                {
                    ODSReader objODSReader = new ODSReader();

                    WrappedInt driverTotal = new WrappedInt();

                    if (objODSReader.isDriverODScheckGood(sheet, wrongCells, driverTotal))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(READ_DRIVER_ODS_CONF_1.getContent());
                        sb.append(System.getProperty("line.separator"));
                        sb.append(READ_DRIVER_ODS_CONF_2.getContent());
                        sb.append(driverTotal.getValue());
                        sb.append(System.getProperty("line.separator"));
                        sb.append(READ_DRIVER_ODS_CONF_3.getContent());
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                ODS_CHECK_RESULT_TITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readDriverODS(sheet, this);
                        }
                    } else {
                        // display wrong cell points if existed
                        if (wrongCells.size() > 0) {
                            JOptionPane.showConfirmDialog(null, READ_ODS_FAIL_DIALOG.getContent() 
                                    + System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                                    READ_ODS_FAIL_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                      
                        }                        
                    }
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User Operation: loading drivers records from an ods file)");
        }         
    }//GEN-LAST:event_readSheet_ButtonActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void searchNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusGained
        setKeyboardLanguage(searchName, KOREAN);
        if (searchName.getText().equals(DRIVER_TF.getContent())) {
            searchName.setText("");
            nameHintShown = false;
            searchName.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchNameFocusGained

    private void searchNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNameKeyReleased
        String searchStr = searchName.getText().trim();
        
        if (!nameHintShown && searchStr.length() > 0) {
            changedControls.add(searchName);
        } else {
            changedControls.remove(searchName);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });      
    }//GEN-LAST:event_searchNameKeyReleased

    private void searchNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNameKeyTyped
  
    }//GEN-LAST:event_searchNameKeyTyped

    private void searchCellFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusGained
        if (searchCell.getText().equals(CELL_PHONE_TF.getContent())) {
            searchCell.setText("");
            cellHintShown = false;            
            searchCell.setForeground(new Color(0, 0, 0));
        }        
    }//GEN-LAST:event_searchCellFocusGained

    private void searchCellKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCellKeyTyped
         
    }//GEN-LAST:event_searchCellKeyTyped

    private void searchPhoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchPhoneFocusGained
        if (searchPhone.getText().equals(LANDLINE_TF.getContent())) {
            searchPhone.setText("");
            phoneHintShown = false;            
            searchPhone.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchPhoneFocusGained

    private void searchPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchPhoneKeyTyped

    }//GEN-LAST:event_searchPhoneKeyTyped

    private void searchL1ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL1ComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
    }//GEN-LAST:event_searchL1ComboBoxPopupMenuWillBecomeInvisible

    private void searchL2ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
        mayPropagateBackward(searchL2ComboBox, searchL1ComboBox);
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible

    private void searchBuildingComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
    }//GEN-LAST:event_searchBuildingComboBoxPopupMenuWillBecomeInvisible

    private void searchUnitComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
        mayPropagateBackward(searchUnitComboBox, searchBuildingComboBox);
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible

    private void searchL1ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL1ComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
    }//GEN-LAST:event_searchL1ComboBoxPopupMenuWillBecomeVisible

    private void odsHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_odsHelpButtonActionPerformed
        JDialog helpDialog = new ODS_HelpJDialog(this, false, 
                HELP_DRIVER_TITLE.getContent(), ODS_TYPE.DRIVER);
        
        setHelpDialogLoc(odsHelpButton, helpDialog);
        helpDialog.setVisible(true);        
    }//GEN-LAST:event_odsHelpButtonActionPerformed

    private void sampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleButtonActionPerformed
        String sampleFile = DRIVER_ODS_SAMPLE_FILE.getContent();
            
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

    private void searchCellKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCellKeyReleased
        String searchStr = searchCell.getText().trim();
        
        if (!cellHintShown && searchStr.length() > 0) {
            changedControls.add(searchCell);
        } else {
            changedControls.remove(searchCell);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });
    }//GEN-LAST:event_searchCellKeyReleased

    private void searchPhoneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchPhoneKeyReleased
        String searchStr = searchPhone.getText().trim();
        
        if (!phoneHintShown && searchStr.length() > 0) {
            changedControls.add(searchPhone);
        } else {
            changedControls.remove(searchPhone);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });
    }//GEN-LAST:event_searchPhoneKeyReleased

    private void searchL1ComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchL1ComboBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchL1ComboBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchL1ComboBox);
            } else {
                changedControls.add(searchL1ComboBox);
            }
        }
    }//GEN-LAST:event_searchL1ComboBoxItemStateChanged

    private void searchL2ComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchL2ComboBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchL2ComboBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchL2ComboBox);
            } else {
                changedControls.add(searchL2ComboBox);
            }
        }
    }//GEN-LAST:event_searchL2ComboBoxItemStateChanged

    private void searchBuildingComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchBuildingComboBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchBuildingComboBox);
            } else {
                changedControls.add(searchBuildingComboBox);
            }
        }
    }//GEN-LAST:event_searchBuildingComboBoxItemStateChanged

    private void searchUnitComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchUnitComboBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchUnitComboBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchUnitComboBox);
            } else {
                changedControls.add(searchUnitComboBox);
            }
        }
    }//GEN-LAST:event_searchUnitComboBoxItemStateChanged
    
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
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        if (findLoginIdentity() != null) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ManageDrivers mainForm = new ManageDrivers(null, null);
                    mainForm.setLocation(0, 0);
                    mainForm.setVisible(true);
                }
            });
        }
    }
    
    static ConvComboBoxItem prevItem = null;
    static int prevRow = -1;
    static int prevCol = -1;
    
    final int UNKNOWN = -1;
    
    /**
     * load driver list on the driver table view. Either viewIndex or driverName and cellPhone
     * pair is used to determine the row to select after the loading is finished.
     * 
     * @param viewIndex row number to highlight after loading done
     * @param driverName with cellPhone, part of record key to highlight after loading done
     * @param cellPhone with driverName, part of record key to highlight after loading done
     */
    public void loadDriverData(int viewIndex, String driverName, String cellPhone) {

        Connection conn = null;
        PreparedStatement fetchDrivers = null;
        ResultSet rs = null;
        String excepMsg = "(vehicle driver infomation loading)";

        DefaultTableModel model = (DefaultTableModel) driverTable.getModel();  
        int model_Index = 0;
        
        try {
            model.setRowCount(0);
            // <editor-fold defaultstate="collapsed" desc="-- load car driver list">     
            // <editor-fold defaultstate="collapsed" desc="-- construct SQL statement">  
            StringBuffer sb = new StringBuffer(); 
            sb.append("SELECT @ROWNUM := @ROWNUM + 1 recNo, TA.* ");
            sb.append("FROM (SELECT CD.NAME,  CD.CELLPHONE, CD.PHONE,"); 
            sb.append("    L1.PARTY_NAME AS L1_NAME, L2.PARTY_NAME L2_NAME,");
            sb.append("    BT.BLDG_NO, BU.UNIT_NO, CD.SEQ_NO CD_SEQ_NO,");
            sb.append("    L2.L1_NO, CD.L2_NO, BT.SEQ_NO B_SEQ_NO,");
            sb.append("    CD.UNIT_SEQ_NO U_SEQ_NO");
            sb.append("  FROM CARDRIVER CD");
            sb.append("  LEFT JOIN L2_affiliation L2 ON CD.L2_NO = L2.L2_NO");
            sb.append("  LEFT JOIN L1_affiliation L1 ON L2.L1_NO = L1.L1_NO");
            sb.append("  LEFT JOIN building_unit BU ON UNIT_SEQ_NO = BU.SEQ_NO");
            sb.append("  LEFT JOIN building_table BT ON BLDG_SEQ_NO = BT.SEQ_NO) TA,");
            sb.append("  (SELECT @rownum := 0) r ");

            prevSearchCondition = currSearchCondition;
            prevSearchString = currSearchString;
            prevKeyList = currKeyList;            
            
            if (currSearchCondition.length() > 0 || currSearchString.length() > 0) {
                sb.append(" Where ");
                if (currSearchCondition.length() > 0) {
                    sb.append(currSearchCondition);
                }
                if (currSearchString.length() > 0) {
                    if (currSearchCondition.length() > 0) {
                        sb.append(" and ");
                    }
                    sb.append(currSearchString);
                }
            }    
            
            sb.append(" ORDER BY NAME, L1_NAME, TA.L2_NAME, TA.BLDG_NO, TA.UNIT_NO");
            //</editor-fold>
            
            conn = getConnection();
            fetchDrivers = conn.prepareStatement(sb.toString());
            
            int index = 1;
            for (String searchKey : currKeyList) {
                fetchDrivers.setString(index++, "%" + prependEscape(searchKey) + "%");                
            }            
            
            rs = fetchDrivers.executeQuery();
            while (rs.next()) {
                if (viewIndex == UNKNOWN) // refreshing list after a new driver insertion
                {
                    if (driverName.equals(rs.getString("NAME")) &&
                            cellPhone.equals(rs.getString("CELLPHONE")))
                    {
                        model_Index = model.getRowCount();
                    }
                }
                //<editor-fold defaultstate="collapsed" desc="-- construct a driver info' to show"> 
                Object L1Item = null;
                if (rs.getString("L1_NAME") == null) {
                    L1Item = getPrompter(AffiliationL1, null);
                } else {
                    L1Item = new ConvComboBoxItem(rs.getInt("L1_NO"), rs.getString("L1_NAME"));
                }
                
                Object  L2Item = null;
                if (rs.getString("L2_NAME") == null) {
                    L2Item = getPrompter(AffiliationL2, null);
                } else {
                    L2Item = new InnoComboBoxItem(
                            new int[]{rs.getInt("L2_NO")}, new String[]{rs.getString("L2_NAME")});
                }
                
                Object bldgItem = null;
                
                if (rs.getString("BLDG_NO") == null) {
                    bldgItem = getPrompter(BuildingNo, null);
                } else {
                    bldgItem = new ConvComboBoxItem(
                            rs.getInt("B_SEQ_NO"), rs.getString("BLDG_NO"));
                }
                
                Object unitItem = null;
                
                if (rs.getString("UNIT_NO") == null) {
                    unitItem = getPrompter(UnitNo, null);
                } else {
                    unitItem = new InnoComboBoxItem(
                            new int[]{rs.getInt("U_SEQ_NO")}, new String[] {rs.getString("UNIT_NO")});
                }
                
                model.addRow(new Object[] {
                     rs.getInt("recNo"),   rs.getString("NAME"), rs.getString("CELLPHONE"),
                     rs.getString("PHONE"),  L1Item, L2Item, bldgItem, unitItem,
                     rs.getInt("CD_SEQ_NO")
                });
                //</editor-fold>
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {            
            closeDBstuff(conn, fetchDrivers, rs, excepMsg);
            countValue.setText(String.valueOf(driverTable.getRowCount()));                        
        }
            
        int numRows = model.getRowCount();
        
        saveSheet_Button.setEnabled(false);
        if (numRows > 0) {  
            // <editor-fold defaultstate="collapsed" desc="-- Highlight a selected driver">                          
            if (driverName.length() > 0) {
                viewIndex = driverTable.convertRowIndexToView(model_Index);
            } else if (viewIndex == numRows) {
                // If the index of the deleted record is the same as the number of remaining records,
                // then it means that the record deleted were the last row within the list of records.
                // In this case, display(highlight) the row just above the deleted one.
                viewIndex--;
            }
            if (0 <= viewIndex && viewIndex < numRows) {
                highlightTableRow(driverTable, viewIndex);
            }
            if (isManager) {
                saveSheet_Button.setEnabled(true);
            }
            //</editor-fold>
        }        
        searchButton.setEnabled(false);
    }
        
    // <editor-fold defaultstate="collapsed" desc="-- Netbeans Generated Control Item Variables ">                               
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel HelpPanel;
    private javax.swing.JPanel balancer;
    private javax.swing.JPanel bottomButtonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    public javax.swing.JButton closeFormButton;
    private javax.swing.JLabel countLbl;
    private javax.swing.JLabel countValue;
    private javax.swing.JButton deleteAll_button;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane driversScrollPane;
    public static javax.swing.JTable driversTable;
    private javax.swing.JPanel eastPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler15_5;
    private javax.swing.Box.Filler filler15_6;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler66;
    private javax.swing.JLabel formModeLabel;
    public javax.swing.JButton insertSave_Button;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel leftButtons;
    private javax.swing.JButton modiSave_Button;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JButton odsHelpButton;
    private javax.swing.JButton readSheet_Button;
    private javax.swing.JPanel rightButtons;
    private javax.swing.JButton sampleButton;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JComboBox searchBuildingComboBox;
    public javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCell;
    private javax.swing.JComboBox searchL1ComboBox;
    private javax.swing.JComboBox searchL2ComboBox;
    private javax.swing.JTextField searchName;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField searchPhone;
    private javax.swing.JComboBox searchUnitComboBox;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel tipLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel topButtonPanel;
    private javax.swing.JPanel topCenter;
    private javax.swing.JPanel topLTpanel;
    private javax.swing.JPanel topMid_1;
    private javax.swing.JPanel topRHpanel;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>       
    
    static DriverTable driverTable = (DriverTable) driversTable;
 
    void setUpdateMode (boolean toModify) {
        setFormMode(toModify ? FormMode.UpdateMode : FormMode.NormalMode);
        if (toModify) {
            modiSave_Button.setText(SAVE_BTN.getContent());
        } else {
            modiSave_Button.setText(MODIFY_BTN.getContent());
        }
        tipLabel.setVisible(toModify);
    }

    public int updateCarDriver(String driverName, StringBuffer driverProperties) {
        TableModel model = driverTable.getModel();
        int result = 0;
        String cellPhone = (String)model.getValueAt(updateRow, CellPhone.getNumVal());
        String landLine = (String)model.getValueAt(updateRow, LandLine.getNumVal());
        String itemL2name = null, itemUnitName = null;
        String excepMsg = "modifying info for (<name, cellphone>: " + driverName + ", " 
                +  (String)cellPhone + ")";

        Connection conn = null;
        PreparedStatement updateDriver = null;
        try {
            // <editor-fold defaultstate="collapsed" desc="-- Driver information update">                 
            String sql = "Update cardriver Set NAME = ?," 
                    + " CELLPHONE = ?, phone = ?, L2_NO = ?, UNIT_SEQ_NO = ?"
                    + " Where SEQ_NO = ?";

            //<editor-fold desc="-- Collect driver properties for the update">            
            String itemL2key = null;
            InnoComboBoxItem itemL2 = (InnoComboBoxItem)(model.getValueAt(updateRow, 
                    DriverCol.AffiliationL2.getNumVal()));
            
            if (itemL2.getKeys()[0] != PROMPTER_KEY) {
                itemL2key = String.valueOf(itemL2.getKeys()[0]);
                itemL2name = itemL2.getLabels()[0];
            }

            String itemUnitKey = null;
            InnoComboBoxItem itemUnit = (InnoComboBoxItem)(model.getValueAt(updateRow, 
                    DriverCol.UnitNo.getNumVal()));
            
            if (itemUnit.getKeys()[0] != PROMPTER_KEY) {
                itemUnitKey = String.valueOf(itemUnit.getKeys()[0]);
                itemUnitName = itemUnit.getLabels()[0];
            }
            //</editor-fold>

            conn = getConnection();
            updateDriver = conn.prepareStatement(sql);
            
            int paraIdx = 1;            
            updateDriver.setString(paraIdx++, driverName);
            updateDriver.setString(paraIdx++, cellPhone); 
            updateDriver.setString(paraIdx++, landLine);
            updateDriver.setString(paraIdx++, itemL2key);
            updateDriver.setString(paraIdx++, itemUnitKey);
            updateDriver.setInt(paraIdx++, (Integer)model.getValueAt(updateRow, DriverCol.SEQ_NO.getNumVal()));
            result = updateDriver.executeUpdate();
            //</editor-fold>
        } catch (SQLException ex) {
            // <editor-fold defaultstate="collapsed" desc="-- handle exception">                                          
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                rejectUserInput(driverTable, updateRow, excepMsg);                     
            }
            else {
                logParkingException(Level.SEVERE, ex, excepMsg);
                JOptionPane.showConfirmDialog(null, "see log/exception folder for details.",
                        "Driver '" + driverName + "' Update Failure", 
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                
            }
            //</editor-fold>                
        } finally {
            //<editor-fold desc="-- Return resources and summarize update.">
            closeDBstuff(conn, updateDriver, null, excepMsg);
            if (result == 1) {
                driverProperties.append("Driver Update Summary: " + System.lineSeparator());
                driverProperties.append(" - Name: " + driverName + System.lineSeparator());                
                driverProperties.append(" - Cell phone: " + cellPhone + System.lineSeparator());                
                driverProperties.append(" - Landline: " + landLine + System.lineSeparator());                
                driverProperties.append(" - Level 2: " + itemL2name + System.lineSeparator());                
                driverProperties.append(" - Unit no: " + itemUnitName + System.lineSeparator());
            } else {
                driverProperties.append("Update for driver '" + driverName + "' failed.");
            }
            //</editor-fold>
        }     
        return result;
    }
    
    TableColumn hiddenSN;

    private void changeTableSizeEtc() {
        Object[][] data = { /*{1, "Henry Ford", "452-1234-5678", "567-1111-2222", {"Engineering", 2},
            {"Mechanical Engr.", 4}, {"1", 162}, {"101", 5024}, 3} */            
        };
        String[] columnNames = {
            ORDER_HEADER.getContent(), 
            NAME_HEADER.getContent() + "(" + REQUIRED.getContent() + ")",
            CELL_PHONE_HEADER.getContent() + "(" + REQUIRED.getContent() + ")",
            PHONE_HEADER.getContent(), 
            HIGHER_HEADER.getContent(), 
            LOWER_HEADER.getContent(), 
            BUILDING_HEADER.getContent(), 
            ROOM_HEADER.getContent(), 
            "CD_SEQ_NO"};
        
        driverTable = new DriverTable(data, columnNames, this);
        driverTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTable.setAutoCreateRowSorter(true);
        
        driverTable.setSurrendersFocusOnKeystroke(true);
        
        driverTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                driversTableFocusGained(evt);
            }
        });

        driversScrollPane.setViewportView(driverTable); 
        
        // Hide drivers table sequence number which is used by only inside the code
        TableColumnModel NumberTableModel = driverTable.getColumnModel();
        hiddenSN = NumberTableModel.getColumn(DriverCol.SEQ_NO.getNumVal());
        
        NumberTableModel.removeColumn(hiddenSN);        
        
        driverTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);        
        
        // <editor-fold defaultstate="collapsed" desc="-- Adjust Column Width ">                    
        TableColumnModel tcm = driverTable.getColumnModel();
        
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(0), DTCW_RN, DTCW_RN, DTCW_RN); // 0: row number
        SetAColumnWidth(tcm.getColumn(1), DTCW_DN, DTCW_DN, DTCW_DN* DTCW_MAX); // 1: driver name
        SetAColumnWidth(tcm.getColumn(2), DTCW_CP, DTCW_CP, DTCW_CP* DTCW_MAX); // 2: cell phone
        SetAColumnWidth(tcm.getColumn(3), DTCW_LL, DTCW_LL, DTCW_LL* DTCW_MAX); // 3: land line
        SetAColumnWidth(tcm.getColumn(4), DTCW_L1, DTCW_L1, DTCW_L1* DTCW_MAX); // 4: affiliation level 1
        SetAColumnWidth(tcm.getColumn(5), DTCW_L2, DTCW_L2, DTCW_L2* DTCW_MAX); // 5: affiliation level 2
        SetAColumnWidth(tcm.getColumn(6), DTCW_BN, DTCW_BN, DTCW_BN* DTCW_MAX); // 6: building number
        SetAColumnWidth(tcm.getColumn(7), DTCW_UN, DTCW_UN, DTCW_UN* DTCW_MAX); // 7: building unit number  
        //</editor-fold>
        
        addDriverSelectionListener();
    }

    /**
     * Handles driver information update trial in one of the following three ways.
     * 1. requires the user enter driver name information
     * 2. requires the user enter driver cell phone number
     * 3. update driver information in the database in case everything is OK
     * @param nextRowV Row number to highlight after update operation
     */
    public void finalizeDriverUpdate(int nextRowV) {
        int row = updateRow; // driverTable.getSelectedRow();
        int colName = driverTable.convertColumnIndexToModel(DriverCol.DriverName.getNumVal());
        String name = ((String)driverTable.getModel().getValueAt(row, colName)).trim();     
        int colCell = driverTable.convertColumnIndexToModel(DriverCol.CellPhone.getNumVal());
        String cell = String.valueOf(driverTable.getValueAt(row, colCell)).toLowerCase().trim();        

        if (somePropertiesInvalid(row, name, cell)) {
            return;
        }
        
        // <editor-fold defaultstate="collapsed" desc="-- save modified driver info">  
        int response = JOptionPane.showOptionDialog(this, 
                USER_UPDATE_SUCCESS_DIALOG.getContent() + name,
                SAVE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, 
                null, null, null);

        setFormMode(FormMode.NormalMode);
        // if insertion was successful, then redisplay the list
        int nextRowM = driverTable.convertRowIndexToModel(nextRowV);

        // conditions that make driver information insufficient: 1, 2        
        String nextName = ((String)driverTable.getModel().getValueAt(nextRowM, colName)).trim();
        String nextCell = ((String)driverTable.getModel().getValueAt(nextRowM, colCell)).trim();
        if (response == JOptionPane.YES_OPTION) {
            StringBuffer driverProperties = new StringBuffer();
            if (updateCarDriver(name, driverProperties) == 1) {

                // also redisplay driver selection form this form invoked from it
                if (driverSelectionForm != null) {
                    driverSelectionForm.loadSkinnyDriverTable(0); // 0: highlight first row
                }
                logParkingOperation(OSP_enums.OpLogLevel.SettingsChange, driverProperties.toString());
            }
        }
        loadDriverData(UNKNOWN, nextName, nextCell);
        driverTable.requestFocusInWindow();
        //</editor-fold>            
    }     
    
    /**
     * Handles new driver information insertion trial in one of the following three ways
     * 1. removes new driver information row from the table when name is not provided
     * 2. asks driver cell phone number when it is not provided
     * 3. insert new driver information into the database table in case everything is fine.
     */
    public void finalizeDriverCreation() {
        int row = driverTable.getSelectedRow();
        int colName = driverTable.convertColumnIndexToModel(DriverCol.DriverName.getNumVal());
        String name = ((String)driverTable.getModel().getValueAt(row, colName)).trim();
        int colCell = driverTable.convertColumnIndexToModel(DriverCol.CellPhone.getNumVal());
        String cell = String.valueOf(driverTable.getValueAt(row, colCell)).toLowerCase().trim();
        
        if (somePropertiesInvalid(row, name, cell))
            return;
        
        // both driver name and his/her cell phone number are supplied
        int response = JOptionPane.showConfirmDialog(this, 
                USER_CREATE_SUCCESS_DIALOG.getContent() + name,
                SAVE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION){
            insertCarDriver(name, row);
        } else {
            cancelButtonActionPerformed(null);
        }
            
        driverTable.requestFocusInWindow();
    }
    
    /**
     * Set up complex item selection upward propagation for the lower 
     * comboboxes. Lower comboboxes are for AffiliationL2 and UnitNo columns.
     * @param driverCol table column for which a lower level combobox is the editor.
     */
    private void setupLowerComboBox(DriverCol driverCol) {    
        if (driverCol != AffiliationL2 && driverCol != UnitNo) {
            return;
        }
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("ComboBox pops up if clicked");
        renderer.setHorizontalTextPosition(SwingConstants.CENTER);
        
        PComboBox<InnoComboBoxItem> comboBox = new PComboBox<InnoComboBoxItem>();

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                mayPropagateBackward(event, driverCol);
            }
        });      
        
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                mayPropagateBackward(event, driverCol);
            }
        });        
        
        TableColumn comboCol = driverTable.getColumnModel().getColumn(driverCol.getNumVal());    
        comboCol.setCellEditor(new DefaultCellEditor(comboBox));
    }

    /**
     * For a lower combobox in the driver table, propagate 
     * a complex item selection to its parent combobox.
     * @param event item selection event on the lower combobox.
     * @param driverCol column type of the lower combobox.
     */
    private void mayPropagateBackward(AWTEvent event, DriverCol driverCol) {
        JComboBox childCBox = (JComboBox)event.getSource();
        InnoComboBoxItem innoItem = (InnoComboBoxItem)childCBox.getSelectedItem();

        if (innoItem == null || innoItem.getKeys().length == 1) {
            return; // Lower combobox item is just a prompter or an atomic item.
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //<editor-fold defaultstate="collapsed" desc="-- propagate selection to parent">            
                int colM = driverTable.convertColumnIndexToModel(driverCol.getNumVal());
                int row = driverTable.getSelectedRow();
                int highKey = (Integer)(innoItem.getKeys()[1]);

                TableCellEditor editor = driverTable.getCellEditor(row, colM - 1);
                PComboBox<ConvComboBoxItem> cCoBox = 
                        ((PComboBox)(((DefaultCellEditor)editor).getComponent()));
                cCoBox.setSelectedItem(new ConvComboBoxItem(highKey, innoItem.getLabels()[1]));
                driverTable.setValueAt(
                        new ConvComboBoxItem(highKey, innoItem.getLabels()[1]), 
                        row, driverTable.convertColumnIndexToView(colM - 1));                        
                driverTable.setValueAt(
                        new InnoComboBoxItem (new int[]{innoItem.getKeys()[0]},
                                new String[]{innoItem.getLabels()[0]}), 
                        row, colM);
                //</editor-fold>
                getPrevItemParentKey()[driverCol.getNumVal()] = highKey;
            }
        });
    }
    
    boolean nameReqBlinked = false;
    boolean cellReqBlinked = false;
    int editingCol = -1;
    public void startEditingCell(int rowM, int columnIndex) {
        
        editingCol = columnIndex;
        driverTable.changeSelection(rowM, columnIndex, false, false);
        if (columnIndex == 1) {
            (new LabelBlinker()).displayHelpMessages(tipLabel, 
                    REQUIRE_FIELD_NOTE.getContent(), FOCUS_MOVE_NOTE.getContent(), !nameReqBlinked);  
            nameReqBlinked = true;
        } else if (columnIndex == 2) {
            (new LabelBlinker()).displayHelpMessages(tipLabel, 
                    REQUIRE_FIELD_NOTE.getContent(), FOCUS_MOVE_NOTE.getContent(), !cellReqBlinked);  
            cellReqBlinked = true;
        } else {
            LabelBlinker.setCounter(4);
            tipLabel.setForeground(Color.gray);
            tipLabel.setText(FOCUS_MOVE_NOTE.getContent());
        }
        driverTable.requestFocusInWindow();
        driverTable.getEditorComponent().requestFocusInWindow();
    }

    public static String getL2PartyName(int L2No) {
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;         
        String result = null;
        String excepMsg = "(while fetching party name for L2_no : " + L2No;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("select party_name from l2_affiliation where L2_NO = " + L2No);
            rs = stmt.executeQuery(sb.toString());
            if (rs.next()) {
                result = rs.getString("party_name");
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }         
        return result;
    }

    private void closeFrameGracefully() {
        if (formMode == FormMode.NormalMode) {
            disposeExit();
        } else {
            int response = JOptionPane.showConfirmDialog(null,  
                    CREATE_MODE_LABEL.getContent() + DRIVER_CLOSE_FORM_DIALOG.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, 
                    WARNING_MESSAGE);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                disposeExit();
            } else {
                // do nothing on NO_OPTION
            }            
        }  
    }

    /**
     * Propagate a complex item of a lower level combobox to its parent.
     * First, it checks if an item were selected and were a complex one.
     * @param childCBox lower level combobox
     * @param parentCBox higher level(=parent) combobox
     */
    public static void mayPropagateBackward(final JComboBox childCBox, final JComboBox parentCBox) 
    {
        InnoComboBoxItem innoItem = (InnoComboBoxItem)childCBox.getSelectedItem();
        
        // Check if the selected item of the lower level combobox is not a complex one.
        if (innoItem == null || innoItem.getKeys().length == 1) {
            return; // not selected or atomic item selected, so nothing to propagate.
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConvComboBoxItem parent = new ConvComboBoxItem(
                        innoItem.getKeys()[1], innoItem.getLabels()[1]);
                parentCBox.setSelectedItem(parent); // select part of the ocomplex item

                childCBox.removeAllItems();
                /**
                 * Assign the child combobox a single item 'child'.
                 */
                InnoComboBoxItem child = new InnoComboBoxItem(
                        new int[]{(Integer)(innoItem.getKeys()[0])}, 
                        new String[]{innoItem.getLabels()[0]});
                childCBox.addItem(child);
                childCBox.setSelectedItem(child);
            }
        });  
    }
    
    /**
     * Propagate a complex item of a lower level combobox to its parent.
     * First, it checks if an item were selected and were a complex one.
     * @param childCBox lower level combobox
     * @param parentCBox higher level(=parent) combobox
     */
    public static void mayResolveComplex(final JComboBox childCBox, final JComboBox parentCBox) 
    {
        InnoComboBoxItem innoItem = (InnoComboBoxItem)childCBox.getSelectedItem();
        ConvComboBoxItem parent = new ConvComboBoxItem(
                innoItem.getKeys()[1], innoItem.getLabels()[1]);
        
        parentCBox.setSelectedItem(parent); // select part of the ocomplex item
        childCBox.removeAllItems();
        /**
         * Assign the child combobox a single item 'child'.
         */
        InnoComboBoxItem child = new InnoComboBoxItem(
                new int[]{(Integer)(innoItem.getKeys()[0])}, 
                new String[]{innoItem.getLabels()[0]});
        childCBox.addItem(child);
        childCBox.setSelectedItem(child);
    }

    private void getDriverProperties(String name, String cell, StringBuffer driverProperties, int row,
            String landLine, String itemL2name, String itemUnitName) 
    {
        driverProperties.append("  name: " + (String)name + System.lineSeparator());
        driverProperties.append("  cell phone: " + (String)cell + System.lineSeparator());
        driverProperties.append("  phone: " + landLine + System.lineSeparator());    
        driverProperties.append("  2nd affiliation: " + itemL2name + System.lineSeparator());
        driverProperties.append("  Room#: " + itemUnitName + System.lineSeparator());
    }

    private String formSearchCondition() {
        StringBuffer cond = new StringBuffer();

        /**
         * Append affiliation condition if applicable.
         */
        InnoComboBoxItem lower_Item = (InnoComboBoxItem)searchL2ComboBox.getSelectedItem();
        
        if (lower_Item == null) {
            return "";
        }
        int lower_Index = lower_Item.getKeys().length - 1;
        
        attachNumberCondition(cond, "L1_NO", "L2_NO", 
                (Integer)((ConvComboBoxItem)searchL1ComboBox.getSelectedItem()).getKeyValue(),
                (Integer)(lower_Item.getKeys()[lower_Index]));

        /**
         * Append building-unit condition if applicable.
         */        
        lower_Item = (InnoComboBoxItem)searchUnitComboBox.getSelectedItem();
        lower_Index = lower_Item.getKeys().length - 1;
        attachNumberCondition(cond, "B_SEQ_NO", "U_SEQ_NO", (Integer)
                ((ConvComboBoxItem)searchBuildingComboBox.getSelectedItem()).getKeyValue(),
                (Integer)(lower_Item.getKeys()[lower_Index]));   

        return cond.toString();
    }

    /**
     * Focus leftmost search condition control among all set condition criteria.
     */
    private void focusLeftMostCondition() {
        if (!cellHintShown && searchCell.getText().trim().length() > 0) {
            searchCell.requestFocus();
        } else if (!phoneHintShown && searchPhone.getText().trim().length() > 0) {
            searchPhone.requestFocus();
        } else if (searchL1ComboBox.getSelectedIndex() > 0) {
            searchL1ComboBox.requestFocus();
        } else if (searchL2ComboBox.getSelectedIndex() > 0) {
            searchL2ComboBox.requestFocus();
        } else if (searchBuildingComboBox.getSelectedIndex() > 0) {
            searchBuildingComboBox.requestFocus();
        } else if (searchUnitComboBox.getSelectedIndex() > 0) {
            searchUnitComboBox.requestFocus();
        }
    }

    private boolean somePropertiesInvalid(int row, String name, String cell) 
    {       
        int colPhone = driverTable.convertColumnIndexToModel(DriverCol.LandLine.getNumVal());
        String phone = String.valueOf(driverTable.getValueAt(row, colPhone)).toLowerCase().trim();
        
        String L1_item = driverTable.getValueAt(row, AffiliationL1.getNumVal()).toString();
        InnoComboBoxItem L2_item = (InnoComboBoxItem)driverTable.getValueAt(row, AffiliationL2.getNumVal());
        int L2_NO = (Integer) L2_item.getKeys()[0];
        
        String building_item = driverTable.getValueAt(row, BuildingNo.getNumVal()).toString();
        InnoComboBoxItem unit_item = (InnoComboBoxItem)driverTable.getValueAt(row, UnitNo.getNumVal());
        int SEQ_NO = (Integer)unit_item.getKeys()[0];
        
        if (invalidName(name)) {
            //<editor-fold defaultstate="collapsed" desc="-- handle missing driver name">   
            // it has driver's name, but not his/her cell phone number  
            int response = JOptionPane.showConfirmDialog(null, 
                    MISSING_NAME_HANDLING.getContent() 
                            + formModeLabel.getText().toLowerCase()
                            + MISSING_NAME_2.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);                    
            supplyORrestore(response, row, DriverName);
            return true;
            //</editor-fold>               
        } else if (invalidCell(cell)) {
            // <editor-fold defaultstate="collapsed" desc="-- handle insufficient cell phone">   
            // request correct driver cell phone number              
            int response = JOptionPane.showConfirmDialog(null, 
                    MISSING_CELL_HANDLING.getContent()
                            + formModeLabel.getText().toLowerCase()
                            + MISSING_NAME_2.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);                    
            supplyORrestore(response, row, CellPhone);
            return true;
            //</editor-fold>          
        } else if (invalidPhone(phone)) {
            // <editor-fold defaultstate="collapsed" desc="-- handle insufficient landline phone">  
            // give chance to supply correct phone number
            int response = JOptionPane.showConfirmDialog(null, 
                    MISSING_PHONE_HANDLING.getContent()
                            + formModeLabel.getText().toLowerCase()
                            + MISSING_NAME_2.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);                    
            supplyORrestore(response, row, LandLine);
            return true;
            //</editor-fold>          
        }
        
        if (!L1_item.equals(HIGHER_CB_ITEM.getContent()) && L2_NO == PROMPTER_KEY) {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing L2 item"> 
            int response = JOptionPane.showConfirmDialog(null, SUGGEST_SUPPLY_L2.getContent(),
                    LOW_AFFILI_MISSING.getContent() + ERROR_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            supplyORrestore(response, row, AffiliationL2);
            //</editor-fold>  
            return true;            
        }
        
        if(!building_item.equals(BUILDING_CB_ITEM.getContent()) && SEQ_NO == PROMPTER_KEY)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing Unit item"> 
            int response = JOptionPane.showConfirmDialog(null, SUGGEST_SUPPLY_UNIT.getContent(),
                    LOW_UNIT_MISSING.getContent() + ERROR_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            supplyORrestore(response, row, UnitNo);
            return true;
            //</editor-fold>   
        }
        return false;            
    }

    private void initPrevParentKey(FormMode formMode) {
        Object parentObj = null;
        switch (formMode) {
            case CreateMode:
                getPrevListParentKey()[AffiliationL2.getNumVal()] = NOT_LISTED;
                getPrevListParentKey()[UnitNo.getNumVal()] = NOT_LISTED;
                getPrevItemParentKey()[AffiliationL2.getNumVal()] = PROMPTER_KEY;
                getPrevItemParentKey()[UnitNo.getNumVal()] = PROMPTER_KEY;
                break;
                
            case UpdateMode:
                parentObj = driverTable.getValueAt(updateRow, AffiliationL1.getNumVal());
                getPrevListParentKey()[AffiliationL2.getNumVal()] = NOT_LISTED;
                getPrevItemParentKey()[AffiliationL2.getNumVal()] =
                        (Integer)(((ConvComboBoxItem)parentObj).getKeyValue());

                parentObj = driverTable.getValueAt(updateRow, BuildingNo.getNumVal());
                getPrevListParentKey()[UnitNo.getNumVal()] = NOT_LISTED;
                getPrevItemParentKey()[UnitNo.getNumVal()] = 
                        (Integer)(((ConvComboBoxItem)parentObj).getKeyValue());
                break;
                
            case NormalMode:
                parentObj = searchL1ComboBox.getSelectedItem();
                getPrevListParentKey()[AffiliationL2.getNumVal()] = NOT_LISTED;
                getPrevItemParentKey()[AffiliationL2.getNumVal()] =
                        (Integer)(((ConvComboBoxItem)parentObj).getKeyValue());

                parentObj = searchBuildingComboBox.getSelectedItem();
                getPrevListParentKey()[UnitNo.getNumVal()] = NOT_LISTED;
                getPrevItemParentKey()[UnitNo.getNumVal()] = 
                        (Integer)(((ConvComboBoxItem)parentObj).getKeyValue());
                break;
                
            default:
                break;
        }
    }

    private void restoreDriverList(int updateRow) {
        if (driverTable.getCellEditor() != null) {
            driverTable.getCellEditor().cancelCellEditing();
        }
        setFormMode(FormMode.NormalMode);
        loadDriverData(updateRow, "", "");
        driverTable.requestFocusInWindow();      
    }

    private void supplyORrestore(int response, int row, DriverCol driverCol) {
        if (response == JOptionPane.YES_OPTION) 
        {
            int column = driverTable.convertColumnIndexToModel(driverCol.getNumVal());
            
            if (driverTable.editCellAt(row, column))
            {
                highlightTableRow(driverTable, row);
                startEditingCell(row, column);
            }                  
        } else {
            if (getFormMode() == FormMode.CreateMode) {
                ((DefaultTableModel)driverTable.getModel()).setRowCount(driverTable.getRowCount() - 1);
                setFormMode(FormMode.NormalMode);
                if (rowBeforeCreate != -1) {
                    highlightTableRow(driverTable, rowBeforeCreate);
                }                
            } else {
                restoreDriverList(updateRow);                  
            }
        }         
    }

    /**
     * @return the prevParentKey
     */
    public int[] getPrevListParentKey() {
        return prevListParentKey;
    }

    /**
     * @return the prevItemParentKey
     */
    public int[] getPrevItemParentKey() {
        return prevItemParentKey;
    }

    /**
     * Driver table property column combobox is setup.
     * @param higherCBox
     * @param higherCol
     * @param lowerCol 
     */
    private void setupHigherComboBox(JComboBox higherCBox, 
            DriverCol higherCol, DriverCol lowerCol) 
    {
        refreshComboBox(higherCBox, getPrompter(higherCol, null), 
                higherCol, PROMPTER_KEY, getPrevListParentKey());
        
        higherCBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    mayChangeLowerCBoxPrompt(event, lowerCol);
                }
            }
        });
        higherCBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mayChangeLowerCBoxPrompt(e, lowerCol);
            }
        }); 
    }

    private void mayRefreshLowerComboBox(
            JComboBox higherCBox, JComboBox lowerCBox, DriverCol lowerCol) 
    {
        int parentKey = (Integer)((ConvComboBoxItem)higherCBox.getSelectedItem()).getKeyValue();
        
        if (lowerCBox.getItemCount() == 1 || 
                getPrevListParentKey()[lowerCol.getNumVal()] != parentKey) 
        {
            Object selItem = lowerCBox.getSelectedItem();
            refreshComboBox(lowerCBox, getPrompter(lowerCol, higherCBox),
                    lowerCol, parentKey, getPrevListParentKey());        
            lowerCBox.setSelectedItem(selItem);
        }    
    }

    private String formSearchString(List<String> keyList) {
        StringBuffer cond = new StringBuffer();
        String searchStr = searchName.getText().trim();
        
        if (!nameHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "name", searchStr.length());
            keyList.add(searchStr);            
        }
        
        searchStr = searchCell.getText().trim();
        if (!cellHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "cellphone", searchStr.length());
            keyList.add(searchStr);            
        }
        
        searchStr = searchPhone.getText().trim();
        if (!phoneHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "phone", searchStr.length());
            keyList.add(searchStr);            
        }
        
        return cond.toString();
    }

    private void disposeExit() {
        if (mainForm != null) {
            mainForm.getTopForms()[ControlEnums.TopForms.CarOwner.ordinal()] = null;
        }
        dispose();
        if (isStandalone) {
            System.exit(0);
        } 
    }
}
