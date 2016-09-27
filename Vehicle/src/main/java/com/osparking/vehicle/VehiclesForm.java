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
package com.osparking.vehicle;

import com.osparking.global.ChangedComponentClear;
import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.FIRST_ROW;
import static com.osparking.global.CommonData.ODS_DIRECTORY;
import static com.osparking.global.CommonData.PROMPTER_KEY;
import static com.osparking.global.CommonData.adminOperationEnabled;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonHeightShort;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.carTagWidth;
import static com.osparking.global.CommonData.downloadSample;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.normGUIwidth;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.putCellCenter;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.DataSheet.saveODSfile;
import com.osparking.vehicle.driver.DriverSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.*;
import com.osparking.global.IMainGUI;
import com.osparking.global.names.ControlEnums;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMessages.*;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.*;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.LabelContent.CAR_TAG_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CELL_PHONE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DATA_REQUIRED;
import static com.osparking.global.names.ControlEnums.LabelContent.EXACT_COMP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_VEHICLE_ODS;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODIFY_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODI_DATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NOTIFICATION_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.OTHER_INFO_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PARK_ALLOWED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PHONE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REASON_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REGI_DATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REQUIRED1_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_MODE_LABEL;
import com.osparking.global.names.ControlEnums.MenuITemTypes;
import static com.osparking.global.names.ControlEnums.TableType.Vehicles;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILD_ROOM_HEADER;
import static com.osparking.global.names.ControlEnums.TitleTypes.VEHICLESFORM_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.CAR_TAG_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.DRIVER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGH_LOW_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.OTHER_INFO_HEDER;
import static com.osparking.global.names.ControlEnums.TableTypes.REASON_HEADER;
import static com.osparking.global.names.ControlEnums.TextType.*;
import static com.osparking.global.names.ControlEnums.ToolTipContent.AFFILIATION_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.BUILDING_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CAR_TAG_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_PNG;
import static com.osparking.global.names.ControlEnums.ToolTipContent.OTHER_TOOLTIP;
import static com.osparking.global.names.DB_Access.insertOneVehicle;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.JTextFieldLimit;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.OSP_enums.VehicleCol;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.WrappedInt;
import static com.osparking.vehicle.CommonData.attachLikeCondition;
import static com.osparking.vehicle.CommonData.prependEscape;
import static com.osparking.vehicle.CommonData.setHelpDialogLoc;
import static com.osparking.vehicle.CommonData.vAffiliWidth;
import static com.osparking.vehicle.CommonData.vBuildingWidth;
import static com.osparking.vehicle.CommonData.vCauseWidth;
import static com.osparking.vehicle.CommonData.vDriverNmWidth;
import static com.osparking.vehicle.CommonData.vOtherWidth;
import static com.osparking.vehicle.CommonData.vPlateNoWidth;
import static com.osparking.vehicle.CommonData.vRowNoWidth;
import static com.osparking.vehicle.CommonData.wantToSaveFile;
import com.osparking.vehicle.driver.ODSReader;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import static java.awt.event.ItemEvent.SELECTED;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
/**
 *
 * @author Open Source Parking Inc.
 */
public class VehiclesForm extends javax.swing.JFrame {

    static VehiclesForm mySelf = null;
    private FormMode formMode; 
    private DriverObj driverObj = new DriverObj("", 0);
    final int UNKNOWN = -1;
    private String prevSearchCondition = null;    
    private String currSearchCondition = null;       
    private String prevSearchString = null;    
    private String currSearchString = null;       
    private List<String> prevKeyList = new ArrayList<String>();
    private List<String> currKeyList = new ArrayList<String>();
    static private ChangedComponentClear changedControls; 
    IMainGUI mainForm = null;
    boolean isStandalone = false;
    
    /**
     * Creates new form Vehicles
     */
    public VehiclesForm(IMainGUI mainForm) {
        initComponents();
        this.mainForm = mainForm;
        if (mainForm == null) {
            isStandalone = true;
        }
        changedControls = new ChangedComponentClear(clearButton);
        
        setIconImages(OSPiconList);
        
        fixControlDimensions();
        
        attachEnterHandler(searchCarTag);
        attachEnterHandler(searchDriver);
        attachEnterHandler(searchAffiliCBox);
        attachEnterHandler(searchBldgCBox);
        attachEnterHandler(searchETC);
        attachEnterHandler(disallowReason);
        
        setFormMode(FormMode.NormalMode);
        setSearchEnabled(true);
        
        loadSearchBox();
        attachEventListenerToVehicleTable();
        
        adminOperationEnabled(true, deleteAllVehicles, odsHelpButton, sampleButton, readSheet_Button);
        loadVehicleTable(FIRST_ROW, "");
        driverTextField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (driverTextField.getText().trim().length() == 0) {
                    openDriverSelectionForm(mySelf);
                } else {    
                    cellTextField.requestFocus();
                }
            }

        });  
        mySelf = this;
        
        // limit the number of characters that can be entered to the (dis)allow reason field
        reasonTextField.setDocument(new JTextFieldLimit(20));    
    }

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
    
    private void openDriverSelectionForm(VehiclesForm mySelf) {
        int seqNo = 0; 
        
        if (formMode == formMode.UpdateMode) {
            seqNo = Integer.parseInt((String)vehiclesTable.getModel().getValueAt(
                    vehiclesTable.getSelectedRow(), VehicleCol.SeqNo.getNumVal()));
        }
        new DriverSelection(mySelf, seqNo).setVisible(true);
    }
            
    private void fineTuneColumnWidth() {
        TableColumnModel tcm = vehiclesTable.getColumnModel();
       
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(VehicleCol.RowNo.getNumVal()), 
                vRowNoWidth, vRowNoWidth, vRowNoWidth); // 0: row number
        SetAColumnWidth(tcm.getColumn(VehicleCol.PlateNumber.getNumVal()),
                vPlateNoWidth, vPlateNoWidth, 32767); // 1: vehicle tag number
        SetAColumnWidth(tcm.getColumn(VehicleCol.Name.getNumVal()), 
                vDriverNmWidth, vDriverNmWidth, 32767); // 2: driver name
        SetAColumnWidth(tcm.getColumn(VehicleCol.Affiliation.getNumVal()), 
                10, vAffiliWidth, 32767); // 3: affiliation level 1, 2
        SetAColumnWidth(tcm.getColumn(VehicleCol.Building.getNumVal()), 
                10, vBuildingWidth, 32767); // 4: building and unit no
        SetAColumnWidth(tcm.getColumn(VehicleCol.OtherInfo.getNumVal()), 
                10, vOtherWidth, 32767); // 5: etc info
        SetAColumnWidth(tcm.getColumn(VehicleCol.CellPhone.getNumVal()), 0, 0, 0); // 6: cellphone
        SetAColumnWidth(tcm.getColumn(VehicleCol.Phone.getNumVal()), 0, 0, 0); // 7: LandLine
        SetAColumnWidth(tcm.getColumn(VehicleCol.Notification.getNumVal()), 0, 0, 0); // 8: NOTification
        SetAColumnWidth(tcm.getColumn(VehicleCol.Whole.getNumVal()), 0, 0, 0); // 9: extra comp`
        SetAColumnWidth(tcm.getColumn(VehicleCol.Permitted.getNumVal()), 0, 0, 0); // 10: Alloed
        SetAColumnWidth(tcm.getColumn(VehicleCol.Causes.getNumVal()), 10, 
                vCauseWidth, 32767); // 11: reason
        SetAColumnWidth(tcm.getColumn(VehicleCol.Creation.getNumVal()), 0, 0, 0); // 12: registered On
        SetAColumnWidth(tcm.getColumn(VehicleCol.Modification.getNumVal()), 0, 0, 0); // 13: Modify On
        SetAColumnWidth(tcm.getColumn(VehicleCol.SeqNo.getNumVal()), 0, 0, 0); // 14: drvseqNo
        
        tcm.getColumn(VehicleCol.Building.getNumVal()).setCellRenderer(putCellCenter);

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

        saveFileChooser = new javax.swing.JFileChooser();
        odsFileChooser = new javax.swing.JFileChooser();
        filler40_1 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        topMarginPanel = new javax.swing.JPanel();
        dummyButton1 = new javax.swing.JButton();
        wholePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        aboutjPanel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 22), new java.awt.Dimension(40, 22), new java.awt.Dimension(32767, 22));
        mdePlusRight = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 100));
        modePanel = new javax.swing.JPanel();
        modeStringPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        formModeLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        RequiredPanel1 = new javax.swing.JPanel();
        legendLLabel = new javax.swing.JLabel();
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        legendString = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        detailPanel = new javax.swing.JPanel();
        rowNumLabel = new javax.swing.JLabel();
        rowNumTextField = new javax.swing.JTextField();
        tarTagLabel = new javax.swing.JLabel();
        carTagTextField = new javax.swing.JTextField();
        selectDriverButton = new javax.swing.JButton();
        driverTextField = new javax.swing.JTextField();
        cellTextLabel = new javax.swing.JLabel();
        cellTextField = new javax.swing.JTextField();
        phoneTextLabel = new javax.swing.JLabel();
        phoneTextField = new javax.swing.JTextField();
        lastModiLabel = new javax.swing.JLabel();
        lastModiTextField = new javax.swing.JTextField();
        notiLabel = new javax.swing.JLabel();
        notiCheckBox = new javax.swing.JCheckBox();
        wholeLabel = new javax.swing.JLabel();
        wholeCheckBox = new javax.swing.JCheckBox();
        permitLabel = new javax.swing.JLabel();
        permitCheckBox = new javax.swing.JCheckBox();
        reasonLabel = new javax.swing.JLabel();
        reasonTextField = new javax.swing.JTextField();
        otherLabel = new javax.swing.JLabel();
        otherInfoTextField = new javax.swing.JTextField();
        creationLabel = new javax.swing.JLabel();
        creationTextField = new javax.swing.JTextField();
        isTagReqLabel = new javax.swing.JLabel();
        isIDreqLabel1 = new javax.swing.JLabel();
        centerPanel = new javax.swing.JPanel();
        titlePanelReal = new javax.swing.JPanel();
        formTitleLabel = new javax.swing.JLabel();
        titleBelow = new javax.swing.JPanel();
        centerTopLeft = new javax.swing.JPanel();
        metaKeyPanel = new javax.swing.JPanel();
        metaLabel = new javax.swing.JLabel();
        countPanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(1, 0), new java.awt.Dimension(1, 0), new java.awt.Dimension(1, 32767));
        centerTopRight = new javax.swing.JPanel();
        rowCountSz = new javax.swing.JPanel();
        clearButton = new javax.swing.JButton();
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        searchButton = new javax.swing.JButton();
        centerFirstPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        filler53 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jLabel11 = new javax.swing.JLabel();
        searchCarTag = new javax.swing.JTextField();
        searchDriver = new javax.swing.JTextField();
        searchAffiliCBox = new PComboBox();
        searchBldgCBox = new PComboBox();
        searchETC = new javax.swing.JTextField();
        disallowReason = new javax.swing.JTextField();
        filler50 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(3, 32767));
        jScrollPane1 = new javax.swing.JScrollPane();
        vehiclesTable = new RXTable(Vehicles);
        allButtonsPanel = new javax.swing.JPanel();
        filler55 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        centerThridPanel = new javax.swing.JPanel();
        insertSave_Button = new javax.swing.JButton();
        modiSave_Button = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        cancel_Button = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        rightButtons = new javax.swing.JPanel();
        deleteAllVehicles = new javax.swing.JButton();
        readSheet_Button = new javax.swing.JButton();
        saveSheet_Button = new javax.swing.JButton();
        closeFormButton = new javax.swing.JButton();
        HelpPanel = new javax.swing.JPanel();
        odsHelpButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        sampleButton = new javax.swing.JButton();
        filler40_2 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        southPanel = new javax.swing.JPanel();

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        saveFileChooser.setFileFilter(new OdsFileOnly());

        odsFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        odsFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(VEHICLESFORM_FRAME_TITLE.getContent());
        setMinimumSize(new Dimension(normGUIwidth, normGUIheight + 61));
        setPreferredSize(new Dimension(normGUIwidth, normGUIheight + 61));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().add(filler40_1, java.awt.BorderLayout.WEST);

        topMarginPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        topMarginPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        topMarginPanel.setPreferredSize(new java.awt.Dimension(1190, 40));
        topMarginPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 15));

        dummyButton1.setMnemonic('N');
        dummyButton1.setToolTipText("");
        dummyButton1.setMaximumSize(new java.awt.Dimension(0, 0));
        dummyButton1.setMinimumSize(new java.awt.Dimension(0, 0));
        dummyButton1.setPreferredSize(new java.awt.Dimension(0, 0));
        dummyButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dummyButton1ActionPerformed(evt);
            }
        });
        topMarginPanel.add(dummyButton1);

        getContentPane().add(topMarginPanel, java.awt.BorderLayout.NORTH);

        wholePanel.setMinimumSize(new java.awt.Dimension(1190, 790));
        wholePanel.setPreferredSize(new java.awt.Dimension(1190, 790));
        wholePanel.setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        wholePanel.add(topPanel, java.awt.BorderLayout.NORTH);

        leftPanel.setMaximumSize(new java.awt.Dimension(393204, 32767));
        leftPanel.setMinimumSize(new java.awt.Dimension(300, 690));
        leftPanel.setPreferredSize(new java.awt.Dimension(300, 690));
        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.Y_AXIS));

        aboutjPanel.setMaximumSize(new java.awt.Dimension(300, 40));
        aboutjPanel.setMinimumSize(new java.awt.Dimension(300, 40));
        aboutjPanel.setPreferredSize(new java.awt.Dimension(300, 40));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText(LicenseButton.getContent());
        seeLicenseButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout aboutjPanelLayout = new javax.swing.GroupLayout(aboutjPanel);
        aboutjPanel.setLayout(aboutjPanelLayout);
        aboutjPanelLayout.setHorizontalGroup(
            aboutjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutjPanelLayout.createSequentialGroup()
                .addComponent(seeLicenseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(229, Short.MAX_VALUE))
        );
        aboutjPanelLayout.setVerticalGroup(
            aboutjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutjPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(seeLicenseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        leftPanel.add(aboutjPanel);
        leftPanel.add(filler1);

        mdePlusRight.setMaximumSize(new java.awt.Dimension(2147483647, 56));
        mdePlusRight.setPreferredSize(new java.awt.Dimension(300, 56));
        mdePlusRight.setLayout(new java.awt.BorderLayout());
        mdePlusRight.add(filler3, java.awt.BorderLayout.EAST);

        modePanel.setMaximumSize(new java.awt.Dimension(32877, 56));
        modePanel.setMinimumSize(new java.awt.Dimension(280, 56));
        modePanel.setPreferredSize(new java.awt.Dimension(280, 56));
        modePanel.setLayout(new javax.swing.BoxLayout(modePanel, javax.swing.BoxLayout.Y_AXIS));

        modeStringPanel.setMaximumSize(new java.awt.Dimension(32767, 26));
        modeStringPanel.setMinimumSize(new java.awt.Dimension(196, 26));
        modeStringPanel.setPreferredSize(new java.awt.Dimension(119, 26));
        modeStringPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel12.setText(MODE_LABEL.getContent());
        JLabel tempLabel2 = new JLabel(MODE_LABEL.getContent());
        tempLabel2.setFont(jLabel12.getFont());
        Dimension dim2 = tempLabel2.getPreferredSize();
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel12.setMaximumSize(new java.awt.Dimension(95, 27));
        jLabel12.setMinimumSize(new java.awt.Dimension(95, 26));
        jLabel12.setPreferredSize(new Dimension(dim2.width + 1, dim2.height));
        modeStringPanel.add(jLabel12);

        formModeLabel.setForeground(pointColor);
        formModeLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        formModeLabel.setText(SEARCH_MODE_LABEL.getContent());
        formModeLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        formModeLabel.setMaximumSize(new java.awt.Dimension(80, 27));
        formModeLabel.setMinimumSize(new java.awt.Dimension(86, 27));
        modeStringPanel.add(formModeLabel);

        modePanel.add(modeStringPanel);

        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 2));
        modePanel.add(jSeparator1);

        RequiredPanel1.setMaximumSize(new java.awt.Dimension(32877, 26));
        RequiredPanel1.setMinimumSize(new java.awt.Dimension(300, 26));
        RequiredPanel1.setPreferredSize(new java.awt.Dimension(300, 26));
        RequiredPanel1.setLayout(new javax.swing.BoxLayout(RequiredPanel1, javax.swing.BoxLayout.LINE_AXIS));

        legendLLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendLLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        legendLLabel.setText(DATA_REQUIRED.getContent());
        legendLLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        legendLLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        legendLLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        legendLLabel.setName(""); // NOI18N
        legendLLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        RequiredPanel1.add(legendLLabel);
        RequiredPanel1.add(filler42);

        legendString.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendString.setText("\u25CF " + REQUIRED1_LABEL.getContent());
        legendString.setMaximumSize(new java.awt.Dimension(1100, 30));
        legendString.setMinimumSize(new java.awt.Dimension(155, 21));
        legendString.setPreferredSize(new java.awt.Dimension(155, 21));
        RequiredPanel1.add(legendString);

        modePanel.add(RequiredPanel1);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 2));
        modePanel.add(jSeparator2);

        mdePlusRight.add(modePanel, java.awt.BorderLayout.CENTER);

        leftPanel.add(mdePlusRight);

        detailPanel.setMinimumSize(new java.awt.Dimension(270, 460));
        detailPanel.setPreferredSize(new java.awt.Dimension(270, 460));
        java.awt.GridBagLayout detailPanelLayout = new java.awt.GridBagLayout();
        detailPanelLayout.columnWidths = new int[] {0, 2, 0, 2, 0};
        detailPanelLayout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        detailPanel.setLayout(detailPanelLayout);

        rowNumLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rowNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        rowNumLabel.setText(ORDER_LABEL.getContent());
        rowNumLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        rowNumLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        rowNumLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        detailPanel.add(rowNumLabel, gridBagConstraints);

        rowNumTextField.setEditable(false);
        rowNumTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rowNumTextField.setEnabled(false);
        rowNumTextField.setFocusable(false);
        rowNumTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        rowNumTextField.setMaximumSize(new java.awt.Dimension(125, 30));
        rowNumTextField.setMinimumSize(new java.awt.Dimension(125, 30));
        rowNumTextField.setName(""); // NOI18N
        rowNumTextField.setPreferredSize(new java.awt.Dimension(125, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        detailPanel.add(rowNumTextField, gridBagConstraints);

        tarTagLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tarTagLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tarTagLabel.setText(CAR_TAG_LABEL.getContent());
        tarTagLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        tarTagLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        tarTagLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        detailPanel.add(tarTagLabel, gridBagConstraints);

        carTagTextField.setEditable(false);
        carTagTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        carTagTextField.setEnabled(false);
        carTagTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        carTagTextField.setMaximumSize(new java.awt.Dimension(125, 30));
        carTagTextField.setMinimumSize(new java.awt.Dimension(125, 30));
        carTagTextField.setName(""); // NOI18N
        carTagTextField.setPreferredSize(new java.awt.Dimension(125, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        detailPanel.add(carTagTextField, gridBagConstraints);

        selectDriverButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        selectDriverButton.setMnemonic('D');
        selectDriverButton.setText(OWNER_BTN.getContent());
        selectDriverButton.setEnabled(false);
        selectDriverButton.setFocusable(false);
        selectDriverButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectDriverButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectDriverButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightShort));
        selectDriverButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightShort));
        selectDriverButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightShort));
        selectDriverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectDriverButtonActionPerformed(evt);
            }
        });
        selectDriverButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectDriverButtonKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        detailPanel.add(selectDriverButton, gridBagConstraints);

        driverTextField.setEditable(false);
        driverTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        driverTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        driverTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        driverTextField.setName(""); // NOI18N
        driverTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        driverTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                driverTextFieldMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        detailPanel.add(driverTextField, gridBagConstraints);

        cellTextLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cellTextLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cellTextLabel.setText(CELL_PHONE_LABEL.getContent());
        cellTextLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        cellTextLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        cellTextLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        detailPanel.add(cellTextLabel, gridBagConstraints);

        cellTextField.setEditable(false);
        cellTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cellTextField.setEnabled(false);
        cellTextField.setFocusable(false);
        cellTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cellTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        cellTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        cellTextField.setName(""); // NOI18N
        cellTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        detailPanel.add(cellTextField, gridBagConstraints);

        phoneTextLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        phoneTextLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        phoneTextLabel.setText(PHONE_LABEL.getContent());
        phoneTextLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        phoneTextLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        phoneTextLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        detailPanel.add(phoneTextLabel, gridBagConstraints);

        phoneTextField.setEditable(false);
        phoneTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        phoneTextField.setEnabled(false);
        phoneTextField.setFocusable(false);
        phoneTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        phoneTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        phoneTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        phoneTextField.setName(""); // NOI18N
        phoneTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        detailPanel.add(phoneTextField, gridBagConstraints);

        lastModiLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lastModiLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lastModiLabel.setText(MODI_DATE_LABEL.getContent());
        lastModiLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        lastModiLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        lastModiLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        detailPanel.add(lastModiLabel, gridBagConstraints);

        lastModiTextField.setEditable(false);
        lastModiTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lastModiTextField.setEnabled(false);
        lastModiTextField.setFocusable(false);
        lastModiTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        lastModiTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        lastModiTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        lastModiTextField.setName(""); // NOI18N
        lastModiTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        detailPanel.add(lastModiTextField, gridBagConstraints);

        notiLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        notiLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        notiLabel.setText(NOTIFICATION_LABEL.getContent());
        notiLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        notiLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        notiLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        detailPanel.add(notiLabel, gridBagConstraints);

        notiCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        notiCheckBox.setEnabled(false);
        notiCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        notiCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        notiCheckBox.setMaximumSize(new Dimension(carTagWidth, 30));
        notiCheckBox.setMinimumSize(new Dimension(carTagWidth, 30));
        notiCheckBox.setName(""); // NOI18N
        notiCheckBox.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        detailPanel.add(notiCheckBox, gridBagConstraints);

        wholeLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        wholeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        wholeLabel.setText(EXACT_COMP_LABEL.getContent());
        wholeLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        wholeLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        wholeLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        detailPanel.add(wholeLabel, gridBagConstraints);

        wholeCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        wholeCheckBox.setEnabled(false);
        wholeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wholeCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        wholeCheckBox.setMaximumSize(new Dimension(carTagWidth, 30));
        wholeCheckBox.setMinimumSize(new Dimension(carTagWidth, 30));
        wholeCheckBox.setName(""); // NOI18N
        wholeCheckBox.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        detailPanel.add(wholeCheckBox, gridBagConstraints);

        permitLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        permitLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        permitLabel.setText(PARK_ALLOWED_LABEL.getContent());
        permitLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        permitLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        permitLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        detailPanel.add(permitLabel, gridBagConstraints);

        permitCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        permitCheckBox.setEnabled(false);
        permitCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        permitCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        permitCheckBox.setMaximumSize(new Dimension(carTagWidth, 30));
        permitCheckBox.setMinimumSize(new Dimension(carTagWidth, 30));
        permitCheckBox.setName(""); // NOI18N
        permitCheckBox.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        detailPanel.add(permitCheckBox, gridBagConstraints);

        reasonLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        reasonLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        reasonLabel.setText(REASON_LABEL.getContent());
        reasonLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        reasonLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        reasonLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        detailPanel.add(reasonLabel, gridBagConstraints);

        reasonTextField.setEditable(false);
        reasonTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        reasonTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        reasonTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        reasonTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        reasonTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        reasonTextField.setName(""); // NOI18N
        reasonTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 18;
        detailPanel.add(reasonTextField, gridBagConstraints);

        otherLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        otherLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        otherLabel.setText(OTHER_INFO_LABEL.getContent());
        otherLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        otherLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        otherLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        detailPanel.add(otherLabel, gridBagConstraints);

        otherInfoTextField.setEditable(false);
        otherInfoTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        otherInfoTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        otherInfoTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        otherInfoTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        otherInfoTextField.setName(""); // NOI18N
        otherInfoTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 20;
        detailPanel.add(otherInfoTextField, gridBagConstraints);

        creationLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        creationLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        creationLabel.setText(REGI_DATE_LABEL.getContent());
        creationLabel.setMaximumSize(new java.awt.Dimension(90, 27));
        creationLabel.setMinimumSize(new java.awt.Dimension(90, 27));
        creationLabel.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        detailPanel.add(creationLabel, gridBagConstraints);

        creationTextField.setEditable(false);
        creationTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        creationTextField.setEnabled(false);
        creationTextField.setFocusable(false);
        creationTextField.setMargin(new java.awt.Insets(0, 2, 0, 2));
        creationTextField.setMaximumSize(new Dimension(carTagWidth, 30));
        creationTextField.setMinimumSize(new Dimension(carTagWidth, 30));
        creationTextField.setName(""); // NOI18N
        creationTextField.setPreferredSize(new Dimension(carTagWidth, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 22;
        detailPanel.add(creationTextField, gridBagConstraints);

        isTagReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        isTagReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        isTagReqLabel.setText("\u25CF");
        isTagReqLabel.setToolTipText("");
        isTagReqLabel.setMaximumSize(new java.awt.Dimension(24, 26));
        isTagReqLabel.setMinimumSize(new java.awt.Dimension(24, 21));
        isTagReqLabel.setPreferredSize(new java.awt.Dimension(15, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        detailPanel.add(isTagReqLabel, gridBagConstraints);

        isIDreqLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        isIDreqLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        isIDreqLabel1.setText("\u25CF");
        isIDreqLabel1.setToolTipText("");
        isIDreqLabel1.setMaximumSize(new java.awt.Dimension(24, 26));
        isIDreqLabel1.setMinimumSize(new java.awt.Dimension(24, 21));
        isIDreqLabel1.setPreferredSize(new java.awt.Dimension(15, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        detailPanel.add(isIDreqLabel1, gridBagConstraints);

        leftPanel.add(detailPanel);

        wholePanel.add(leftPanel, java.awt.BorderLayout.WEST);

        centerPanel.setMinimumSize(new java.awt.Dimension(850, 670));
        centerPanel.setPreferredSize(new java.awt.Dimension(850, 670));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanelReal.setMaximumSize(new java.awt.Dimension(32767, 30));
        titlePanelReal.setMinimumSize(new java.awt.Dimension(113, 30));
        titlePanelReal.setPreferredSize(new java.awt.Dimension(200, 30));
        titlePanelReal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        formTitleLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        formTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        formTitleLabel.setText(VEHICLESFORM_FRAME_TITLE.getContent());
        formTitleLabel.setMaximumSize(new java.awt.Dimension(300, 30));
        formTitleLabel.setMinimumSize(new java.awt.Dimension(113, 30));
        formTitleLabel.setPreferredSize(new java.awt.Dimension(200, 30));
        titlePanelReal.add(formTitleLabel);

        centerPanel.add(titlePanelReal);

        titleBelow.setMaximumSize(new java.awt.Dimension(2147483647, 50));
        titleBelow.setMinimumSize(new java.awt.Dimension(213, 50));
        titleBelow.setPreferredSize(new java.awt.Dimension(849, 50));
        titleBelow.setLayout(new java.awt.BorderLayout());

        centerTopLeft.setMaximumSize(new java.awt.Dimension(32767, 50));
        centerTopLeft.setMinimumSize(new java.awt.Dimension(220, 50));
        centerTopLeft.setPreferredSize(new java.awt.Dimension(310, 50));
        centerTopLeft.setLayout(new javax.swing.BoxLayout(centerTopLeft, javax.swing.BoxLayout.Y_AXIS));

        metaKeyPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        metaLabel.setText(MenuITemTypes.META_KEY_LABEL.getContent());
        metaLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        metaLabel.setForeground(tipColor);
        metaKeyPanel.add(metaLabel);

        centerTopLeft.add(metaKeyPanel);

        countLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        countLbl.setText(COUNT_LABEL.getContent());
        JLabel tempLabel = new JLabel(COUNT_LABEL.getContent());
        tempLabel.setFont(countLbl.getFont());
        Dimension dim = tempLabel.getPreferredSize();
        countLbl.setMaximumSize(new java.awt.Dimension(110, 27));
        countLbl.setMinimumSize(new java.awt.Dimension(90, 27));
        countLbl.setPreferredSize(new Dimension(dim.width + 1, dim.height));

        countValue.setForeground(pointColor);
        countValue.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countValue.setText("count");

        javax.swing.GroupLayout countPanelLayout = new javax.swing.GroupLayout(countPanel);
        countPanel.setLayout(countPanelLayout);
        countPanelLayout.setHorizontalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(countValue)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        countPanelLayout.setVerticalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGroup(countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(countValue)
                        .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(countPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        centerTopLeft.add(countPanel);

        titleBelow.add(centerTopLeft, java.awt.BorderLayout.WEST);

        centerTopRight.setMaximumSize(new java.awt.Dimension(32767, 50));
        centerTopRight.setMinimumSize(new java.awt.Dimension(220, 50));
        centerTopRight.setPreferredSize(new java.awt.Dimension(310, 50));

        rowCountSz.setMaximumSize(new java.awt.Dimension(200, 40));
        rowCountSz.setMinimumSize(new java.awt.Dimension(200, 40));
        rowCountSz.setPreferredSize(new java.awt.Dimension(220, 40));
        rowCountSz.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        clearButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearButton.setMnemonic('l');
        clearButton.setText(CLEAR_BTN.getContent());
        clearButton.setEnabled(false);
        clearButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        rowCountSz.add(clearButton);
        rowCountSz.add(filler31);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setEnabled(false);
        searchButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        rowCountSz.add(searchButton);

        javax.swing.GroupLayout centerTopRightLayout = new javax.swing.GroupLayout(centerTopRight);
        centerTopRight.setLayout(centerTopRightLayout);
        centerTopRightLayout.setHorizontalGroup(
            centerTopRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, centerTopRightLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(rowCountSz, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        centerTopRightLayout.setVerticalGroup(
            centerTopRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, centerTopRightLayout.createSequentialGroup()
                .addGap(0, 40, Short.MAX_VALUE)
                .addComponent(rowCountSz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        titleBelow.add(centerTopRight, java.awt.BorderLayout.EAST);

        centerPanel.add(titleBelow);

        centerFirstPanel.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        centerFirstPanel.setMinimumSize(new java.awt.Dimension(850, 40));
        centerFirstPanel.setPreferredSize(new java.awt.Dimension(850, 40));
        centerFirstPanel.setLayout(new javax.swing.BoxLayout(centerFirstPanel, javax.swing.BoxLayout.PAGE_AXIS));

        searchPanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        searchPanel.setMinimumSize(new java.awt.Dimension(850, 40));
        searchPanel.setPreferredSize(new java.awt.Dimension(850, 40));
        searchPanel.setLayout(new javax.swing.BoxLayout(searchPanel, javax.swing.BoxLayout.LINE_AXIS));
        searchPanel.add(filler53);

        jLabel11.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText(SEARCH_LABEL.getContent());
        jLabel11.setMaximumSize(new Dimension(vRowNoWidth, 28));
        jLabel11.setMinimumSize(new Dimension(vRowNoWidth, 27));
        jLabel11.setPreferredSize(new Dimension(vRowNoWidth, 28));
        searchPanel.add(jLabel11);

        searchCarTag.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCarTag.setForeground(tipColor);
        searchCarTag.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchCarTag.setText(CAR_TAG_TF.getContent());
        searchCarTag.setToolTipText(CAR_TAG_INPUT_TOOLTIP.getContent());
        searchCarTag.setMaximumSize(new java.awt.Dimension(32767, 28));
        searchCarTag.setMinimumSize(new Dimension(vPlateNoWidth - 10, 28));
        searchCarTag.setPreferredSize(new Dimension(vPlateNoWidth - 5, 28));
        searchCarTag.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchCarTagFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchCarTagFocusLost(evt);
            }
        });
        searchCarTag.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchCarTagMousePressed(evt);
            }
        });
        searchCarTag.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchCarTagKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchCarTagKeyTyped(evt);
            }
        });
        searchPanel.add(searchCarTag);

        searchDriver.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchDriver.setForeground(tipColor);
        searchDriver.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchDriver.setText(DRIVER_TF.getContent());
        searchDriver.setToolTipText(DRIVER_INPUT_TOOLTIP.getContent());
        searchDriver.setMaximumSize(new java.awt.Dimension(32767, 28));
        searchDriver.setMinimumSize(new Dimension(vDriverNmWidth - 10, 28));
        searchDriver.setPreferredSize(new Dimension(vDriverNmWidth - 5, 28));
        searchDriver.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchDriverFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchDriverFocusLost(evt);
            }
        });
        searchDriver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchDriverMousePressed(evt);
            }
        });
        searchDriver.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchDriverKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchDriverKeyTyped(evt);
            }
        });
        searchPanel.add(searchDriver);

        searchAffiliCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchAffiliCBox.setToolTipText(AFFILIATION_TOOLTIP.getContent());
        searchAffiliCBox.setMaximumSize(new java.awt.Dimension(32767, 30));
        searchAffiliCBox.setMinimumSize(new Dimension(vAffiliWidth - 10, 30));
        searchAffiliCBox.setPreferredSize(new Dimension(vAffiliWidth - 5, 30));
        searchAffiliCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchAffiliCBoxItemStateChanged(evt);
            }
        });
        searchPanel.add(searchAffiliCBox);

        searchBldgCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBldgCBox.setToolTipText(BUILDING_TOOLTIP.getContent());
        searchBldgCBox.setMaximumSize(new java.awt.Dimension(32767, 30));
        searchBldgCBox.setMinimumSize(new Dimension(vBuildingWidth - 10, 30));
        searchBldgCBox.setPreferredSize(new Dimension(vBuildingWidth - 5, 30));
        searchBldgCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchBldgCBoxItemStateChanged(evt);
            }
        });
        searchPanel.add(searchBldgCBox);

        searchETC.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchETC.setForeground(tipColor);
        searchETC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchETC.setText(OTHER_INFO_TF.getContent());
        searchETC.setToolTipText(OTHER_TOOLTIP.getContent());
        searchETC.setMaximumSize(new java.awt.Dimension(32767, 28));
        searchETC.setMinimumSize(new Dimension(vOtherWidth - 10, 28));
        searchETC.setPreferredSize(new Dimension(vOtherWidth - 5, 28));
        searchETC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchETCFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchETCFocusLost(evt);
            }
        });
        searchETC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchETCMousePressed(evt);
            }
        });
        searchETC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchETCKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchETCKeyTyped(evt);
            }
        });
        searchPanel.add(searchETC);

        disallowReason.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        disallowReason.setForeground(tipColor);
        disallowReason.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        disallowReason.setText(DIS_REASON_TF.getContent());
        disallowReason.setToolTipText(OTHER_TOOLTIP.getContent());
        disallowReason.setMaximumSize(new java.awt.Dimension(32767, 28));
        disallowReason.setMinimumSize(new Dimension(vCauseWidth - 10, 28));
        disallowReason.setPreferredSize(new Dimension(vCauseWidth - 5, 28));
        disallowReason.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                disallowReasonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                disallowReasonFocusLost(evt);
            }
        });
        disallowReason.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                disallowReasonMousePressed(evt);
            }
        });
        disallowReason.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                disallowReasonKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                disallowReasonKeyTyped(evt);
            }
        });
        searchPanel.add(disallowReason);
        searchPanel.add(filler50);

        centerFirstPanel.add(searchPanel);

        centerPanel.add(centerFirstPanel);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 0));

        vehiclesTable.setAutoCreateRowSorter(true);
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null}
            },
            new String [] {
                ORDER_HEADER.getContent(),
                CAR_TAG_HEADER.getContent(),
                DRIVER_HEADER.getContent(),
                HIGH_LOW_HEADER.getContent(),
                BUILD_ROOM_HEADER.getContent(),
                OTHER_INFO_HEDER.getContent(),
                "Cell Phone", "Land Line",
                "Notif'", "Exact", "Allowed",
                REASON_HEADER.getContent(),
                "Registered On",
                "Modified On", "drvSeqNo"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    return Integer.class;
                    default:
                    return String.class;
                }
            }
        };
        vehiclesTable.setAutoCreateRowSorter(true);
        vehiclesTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        vehiclesTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        vehiclesTable.setModel(tableModel);
        //hideSomeColumns();
        fineTuneColumnWidth();
        vehiclesTable.setMinimumSize(new java.awt.Dimension(600, 400));
        ((DefaultTableCellRenderer)vehiclesTable.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        vehiclesTable.setRowHeight(tableRowHeight);
        jScrollPane1.setViewportView(vehiclesTable);

        centerPanel.add(jScrollPane1);

        wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        allButtonsPanel.setPreferredSize(new java.awt.Dimension(1270, 95));
        allButtonsPanel.setLayout(new javax.swing.BoxLayout(allButtonsPanel, javax.swing.BoxLayout.Y_AXIS));
        allButtonsPanel.add(filler55);

        centerThridPanel.setMaximumSize(new java.awt.Dimension(33397, 70));
        centerThridPanel.setMinimumSize(new java.awt.Dimension(769, 70));
        centerThridPanel.setPreferredSize(new java.awt.Dimension(769, 70));

        insertSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        insertSave_Button.setMnemonic('r');
        insertSave_Button.setText(CREATE_BTN.getContent());
        insertSave_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        insertSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSave_ButtonActionPerformed(evt);
            }
        });

        modiSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modiSave_Button.setMnemonic('m');
        modiSave_Button.setText(MODIFY_BTN.getContent());
        modiSave_Button.setEnabled(false);
        modiSave_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modiSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modiSave_ButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteButton.setMnemonic('d');
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

        cancel_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cancel_Button.setMnemonic('C');
        cancel_Button.setText(CANCEL_BTN.getContent());
        cancel_Button.setEnabled(false);
        cancel_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancel_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancel_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_ButtonActionPerformed(evt);
            }
        });

        rightButtons.setMaximumSize(new java.awt.Dimension(470, 70));
        rightButtons.setMinimumSize(new java.awt.Dimension(470, 70));
        rightButtons.setPreferredSize(new java.awt.Dimension(470, 70));
        java.awt.GridBagLayout rightButtonsLayout = new java.awt.GridBagLayout();
        rightButtonsLayout.columnWidths = new int[] {0, 10, 0, 10, 0, 10, 0};
        rightButtonsLayout.rowHeights = new int[] {0, 0, 0};
        rightButtons.setLayout(rightButtonsLayout);

        deleteAllVehicles.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteAllVehicles.setMnemonic('e');
        deleteAllVehicles.setText(DELETE_ALL_BTN.getContent());
        deleteAllVehicles.setEnabled(false);
        deleteAllVehicles.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllVehicles.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllVehicles.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllVehicles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllVehiclesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        rightButtons.add(deleteAllVehicles, gridBagConstraints);

        readSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet_Button.setMnemonic('o');
        readSheet_Button.setText(READ_ODS_BTN.getContent());
        readSheet_Button.setEnabled(false);
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
        saveSheet_Button.setMnemonic('a');
        saveSheet_Button.setText(SAVE_ODS_BTN.getContent());
        saveSheet_Button.setEnabled(false);
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
        closeFormButton.setText(CLOSE_BTN.getContent());
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

        javax.swing.GroupLayout centerThridPanelLayout = new javax.swing.GroupLayout(centerThridPanel);
        centerThridPanel.setLayout(centerThridPanelLayout);
        centerThridPanelLayout.setHorizontalGroup(
            centerThridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerThridPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(insertSave_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(modiSave_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(cancel_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(centerThridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(centerThridPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, centerThridPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                        .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        centerThridPanelLayout.setVerticalGroup(
            centerThridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerThridPanelLayout.createSequentialGroup()
                .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(centerThridPanelLayout.createSequentialGroup()
                .addGroup(centerThridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(insertSave_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modiSave_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        allButtonsPanel.add(centerThridPanel);

        wholePanel.add(allButtonsPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(filler40_2, java.awt.BorderLayout.EAST);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(0, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(1270, 40));

        javax.swing.GroupLayout southPanelLayout = new javax.swing.GroupLayout(southPanel);
        southPanel.setLayout(southPanelLayout);
        southPanelLayout.setHorizontalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1270, Short.MAX_VALUE)
        );
        southPanelLayout.setVerticalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void insertSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSave_ButtonActionPerformed
        
        if (getFormMode() == FormMode.NormalMode) {
            //<editor-fold defaultstate="collapsed" desc="--change to insertion mode ">
            setFormMode(FormMode.CreateMode);
            // clear vehicle detail text fields on the left side panel
            clearVehicleDetail();

            // move focus to the car tag number input field
            carTagTextField.requestFocus();
            //</editor-fold>
        } else if (getFormMode() == FormMode.CreateMode) {
            //<editor-fold defaultstate="collapsed" desc="--check and save vehicle info' ">
            // check if all the required fields are supplied
            if (carTagTextField.getText().trim().length() == 0) {
                 JOptionPane.showConfirmDialog(null, CAR_TAG_DIALOG.getContent(),
                                VEHICLE_CHECK_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);  
                carTagTextField.requestFocus();
            } else if (driverTextField.getText().trim().length() == 0) {
                JOptionPane.showConfirmDialog(null, DRIVER_DIALOG.getContent(),
                                VEHICLE_CHECK_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);  
                selectDriverButton.requestFocus();

            } else {
                // insert new vehicle information.
                int result = 0;
                StringBuffer plateNo = new StringBuffer();
                StringBuffer vehicleContents = new StringBuffer();
                
                result = insertNewVehicle(plateNo, vehicleContents);
                if (result == 1) {
                    setFormMode(FormMode.NormalMode);
                    loadVehicleTable(-1, plateNo.toString()); 
                } else {
                    JOptionPane.showConfirmDialog(null, VEHICLE_CREATION_FAIL_DIALOG.getContent(),
                            CREATION_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, 
                        WARNING_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_insertSave_ButtonActionPerformed

    /**
     * Depending on the mode of the form, it performs one of the following functions
     * 1. modify the detail info of currently selected vehicle
     * 2. save current modification of the vehicle
     * @param evt 
     */
    private void modiSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modiSave_ButtonActionPerformed
        if (getFormMode() == FormMode.NormalMode) {
            //<editor-fold defaultstate="collapsed" desc="--change to insertion mode ">
            setFormMode(FormMode.UpdateMode);
            
            // move focus to the car tag number input field
            highlightTableRow(vehiclesTable, vehiclesTable.getSelectedRow());
            selectDriverButton.requestFocus();
            //</editor-fold>
        } else if (getFormMode() == FormMode.UpdateMode) {
            //<editor-fold defaultstate="collapsed" desc="--check and save updated vehicle">
            // save updated vehicle information
            StringBuffer vehicleModification = new StringBuffer();
            
            if (saveUpdatedVehicle(vehicleModification) == 1)
            {
                setFormMode(FormMode.NormalMode);
                loadVehicleTable(-1, carTagTextField.getText()); 
                logParkingOperation(OpLogLevel.UserCarChange, vehicleModification.toString());
            } else {
                JOptionPane.showConfirmDialog(null, VEHICLE_MODIFY_FAIL_DAILOG.getContent(),
                                VEHICLE_MODIFY_FAIL_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_modiSave_ButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // delete current row(vehicle)
        if (formMode == FormMode.NormalMode) {
            int selRow = vehiclesTable.getSelectedRow();
            highlightTableRow(vehiclesTable, selRow);
            // stop processing if no row selected currently
            if (selRow == -1) 
                return;
            else {
                deleteVehicles();
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        closeFrameGracefully();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeFrameGracefully();
    }//GEN-LAST:event_formWindowClosing

    private void deleteAllVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllVehiclesActionPerformed

        int result = JOptionPane.showConfirmDialog(this, VEHICLE_DELETE_ALL_DAILOG.getContent(),
                        DELETE_ALL_DAILOGTITLE.getContent(), 
                        JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement deleteVehicles = null;
            String excepMsg = "(All Vehicle Deletion)";
            result = 0;

            try {
                conn = getConnection();
                deleteVehicles = conn.prepareStatement("Delete From vehicles");
                result = deleteVehicles.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, deleteVehicles, null, excepMsg);
            }

            if (result >= 1) {
                loadVehicleTable(0, "");
                
                JOptionPane.showConfirmDialog(this, VEHICLE_DELETE_ALL_RESULT_DAILOG.getContent(),
                    DELETE_ALL_RESULT_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            }
        }        
    }//GEN-LAST:event_deleteAllVehiclesActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadVehicleTable(FIRST_ROW, "");
    }//GEN-LAST:event_searchButtonActionPerformed

    private void readSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_ButtonActionPerformed

        try{
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

                    if (objODSReader.checkVehiclesODS(sheet, wrongCells, driverTotal))
                    {
                        StringBuilder sb = new StringBuilder();
                        
                        switch (language) {
                            case KOREAN:
                                sb.append("자료가 인식되었습니다. 자료를 불러오시겠습니까?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -자료 갯수: 차량 기록" + driverTotal.getValue() + " 개");
                                break;
                                
                            case ENGLISH:
                                sb.append("Following data has been recognized. Want to load these data?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -Data content: vehicle records " + driverTotal.getValue() + " rows");
                                break;
                                
                            default:
                                break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                        ODS_CHECK_RESULT_TITLE.getContent(), 
                                        JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readVehiclesODS(sheet, this);
                        }
                        
                    } else {
                        // display wrong cell points if existed
                        if (wrongCells.size() > 0) {
                            JOptionPane.showConfirmDialog(null, READ_ODS_FAIL_DIALOG.getContent() +
                                    System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                                    READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);     
                        }
                    }
                }
            }
        }catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User Operation: loading drivers records from an ods file)");
        }
    }//GEN-LAST:event_readSheet_ButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
        saveODSfile(this, vehiclesTable, saveFileChooser, VEHICLE_SAVE_ODS_FAIL_DIALOG.getContent());
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void searchCarTagMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchCarTagMousePressed
        searchCarTag.selectAll();
    }//GEN-LAST:event_searchCarTagMousePressed

    private void searchCarTagFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCarTagFocusLost
        if (searchCarTag.getText().trim().length() == 0) {
            showCarTagTip();
        }
    }//GEN-LAST:event_searchCarTagFocusLost

    private void searchDriverMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchDriverMousePressed
        searchDriver.selectAll();
    }//GEN-LAST:event_searchDriverMousePressed

    private void searchDriverFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchDriverFocusLost
        if (searchDriver.getText().trim().length() == 0) {
            showDriverTip();
        }
    }//GEN-LAST:event_searchDriverFocusLost

    private void searchETCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchETCMousePressed
        searchETC.selectAll();
    }//GEN-LAST:event_searchETCMousePressed

    private void searchETCFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchETCFocusLost
        if(searchETC.getText().trim().length() == 0) {
            showEtcTip();
        }
    }//GEN-LAST:event_searchETCFocusLost

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearButton.setEnabled(false);

        showCarTagTip();
        showDriverTip();
        searchAffiliCBox.setSelectedIndex(0);
        searchBldgCBox.setSelectedIndex(0);
        showEtcTip();
        showReasonTip();
        vehiclesTable.requestFocus();
        changeSearchButtonEnabled();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void disallowReasonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_disallowReasonFocusLost
        if(disallowReason.getText().trim().length() == 0) {
            showReasonTip();
        }
    }//GEN-LAST:event_disallowReasonFocusLost

    private void disallowReasonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disallowReasonMousePressed
        disallowReason.selectAll();
    }//GEN-LAST:event_disallowReasonMousePressed

    private void driverTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_driverTextFieldMouseClicked
        if (evt.isConsumed() || formMode == FormMode.NormalMode)
        return;
        else
        evt.consume();
        openDriverSelectionForm(this);
    }//GEN-LAST:event_driverTextFieldMouseClicked

    private void selectDriverButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectDriverButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            openDriverSelectionForm(this);
        }
    }//GEN-LAST:event_selectDriverButtonKeyReleased

    private void selectDriverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDriverButtonActionPerformed
        if (formMode == FormMode.NormalMode)
            return;
        openDriverSelectionForm(this);
    }//GEN-LAST:event_selectDriverButtonActionPerformed

    private void cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_ButtonActionPerformed
        // depending on the mode of the form, it performs one of the following functions
        // 1. cancel current insertion,  2.  cancel current update
        
        if (formMode == FormMode.CreateMode) {
            //<editor-fold defaultstate="collapsed" desc="--handle cancelling insertion">
            int response = JOptionPane.showConfirmDialog(null, VEHICLE_CREATE_CANCEL_DIALOG.getContent(),
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                setFormMode(FormMode.NormalMode);
                insertSave_Button.setText(CREATE_BTN.getContent());
                int selRow = vehiclesTable.getSelectedRow();
                if (selRow >= 0)
                    showVehicleDetail(selRow);  
                else
                    clearVehicleDetail();
            } 
            //</editor-fold>
            
        } else if (formMode == FormMode.UpdateMode) {
            //<editor-fold defaultstate="collapsed" desc="--handle cancelling update">
            int response = JOptionPane.showConfirmDialog(null, VEHICLE_MODIFY_CANCEL_DAILOG.getContent(),
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                setFormMode(FormMode.NormalMode);
                modiSave_Button.setText(MODIFY_BTN.getContent());
                int selRow = vehiclesTable.getSelectedRow();
                if (selRow >= 0)
                    showVehicleDetail(selRow);  
                else
                    clearVehicleDetail();
                driverObj = new DriverObj("", 0);           
            } 
            //</editor-fold>
        }
    }//GEN-LAST:event_cancel_ButtonActionPerformed

    private void dummyButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dummyButton1ActionPerformed
        carTagTextField.requestFocus();
    }//GEN-LAST:event_dummyButton1ActionPerformed

    private void searchCarTagKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCarTagKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        }); 
    }//GEN-LAST:event_searchCarTagKeyTyped

    private void searchDriverKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchDriverKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        }); 
    }//GEN-LAST:event_searchDriverKeyTyped

    private void searchETCKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchETCKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        }); 
    }//GEN-LAST:event_searchETCKeyTyped

    private void disallowReasonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_disallowReasonKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        }); 
    }//GEN-LAST:event_disallowReasonKeyTyped

    private void searchAffiliCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchAffiliCBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchAffiliCBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchAffiliCBox);
            } else {
                changedControls.add(searchAffiliCBox);
            }
            changeSearchButtonEnabled();
        }
    }//GEN-LAST:event_searchAffiliCBoxItemStateChanged

    private void searchBldgCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchBldgCBoxItemStateChanged
        if (evt.getStateChange() == SELECTED) {
            if (searchBldgCBox.getSelectedIndex() == PROMPTER_KEY) {
                changedControls.remove(searchBldgCBox);
            } else {
                changedControls.add(searchBldgCBox);
            }            
            changeSearchButtonEnabled();
        }
    }//GEN-LAST:event_searchBldgCBoxItemStateChanged

    private void odsHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_odsHelpButtonActionPerformed
        JDialog helpDialog = new ODS_HelpJDialog(this, false,
            HELP_VEHICLE_ODS.getContent(), ODS_TYPE.VEHICLE);

        setHelpDialogLoc(odsHelpButton, helpDialog);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_odsHelpButtonActionPerformed

    private void sampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleButtonActionPerformed
        String sampleFile = "";

        switch(language){
            case ENGLISH:
            sampleFile = "/vehiclesEng";
            break;

            default:
            sampleFile = "/vehiclesKor";
            break;
        }

        // Read sample ods resource file
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

    private void searchCarTagFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCarTagFocusGained
        if (carTagHintShown) {
            searchCarTag.setText("");
            carTagHintShown = false;
            searchCarTag.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchCarTagFocusGained

    private void searchDriverFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchDriverFocusGained
        if (driverHintShown) {
            searchDriver.setText("");
            driverHintShown = false;
            searchDriver.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchDriverFocusGained

    private void searchETCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchETCFocusGained
        if (etcHintShown) {
            searchETC.setText("");
            etcHintShown = false;
            searchETC.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchETCFocusGained

    private void disallowReasonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_disallowReasonFocusGained
        if (reasonHintShown) {
            disallowReason.setText("");
            reasonHintShown = false;
            disallowReason.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_disallowReasonFocusGained

    private void searchCarTagKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCarTagKeyReleased
        String searchStr = searchCarTag.getText().trim();
        
        if (!carTagHintShown && searchStr.length() > 0) {
            changedControls.add(searchCarTag);
        } else {
            changedControls.remove(searchCarTag);
        }
    }//GEN-LAST:event_searchCarTagKeyReleased

    private void searchDriverKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchDriverKeyReleased
        String searchStr = searchDriver.getText().trim();
        
        if (!driverHintShown && searchStr.length() > 0) {
            changedControls.add(searchDriver);
        } else {
            changedControls.remove(searchDriver);
        }
    }//GEN-LAST:event_searchDriverKeyReleased

    private void searchETCKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchETCKeyReleased
        String searchStr = searchETC.getText().trim();
        
        if (!etcHintShown && searchStr.length() > 0) {
            changedControls.add(searchETC);
        } else {
            changedControls.remove(searchETC);
        }
    }//GEN-LAST:event_searchETCKeyReleased

    private void disallowReasonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_disallowReasonKeyReleased
        String searchStr = disallowReason.getText().trim();
        
        if (!reasonHintShown && searchStr.length() > 0) {
            changedControls.add(disallowReason);
        } else {
            changedControls.remove(disallowReason);
        }
    }//GEN-LAST:event_disallowReasonKeyReleased
    
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
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        if (findLoginIdentity() != null) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    VehiclesForm mainForm = new VehiclesForm(null);
                    mainForm.setVisible(true);
                    if (!DEBUG) {
                        shortLicenseDialog(mainForm, "Vehicle Manager Program", "upper left");
                    }
                }
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="-- automaticically defined variables">                              
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel HelpPanel;
    private javax.swing.JPanel RequiredPanel1;
    private javax.swing.JPanel aboutjPanel;
    private javax.swing.JPanel allButtonsPanel;
    private javax.swing.JButton cancel_Button;
    private javax.swing.JTextField carTagTextField;
    private javax.swing.JTextField cellTextField;
    private javax.swing.JLabel cellTextLabel;
    private javax.swing.JPanel centerFirstPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel centerThridPanel;
    private javax.swing.JPanel centerTopLeft;
    private javax.swing.JPanel centerTopRight;
    private javax.swing.JButton clearButton;
    public javax.swing.JButton closeFormButton;
    private javax.swing.JLabel countLbl;
    private javax.swing.JPanel countPanel;
    private javax.swing.JLabel countValue;
    private javax.swing.JLabel creationLabel;
    private javax.swing.JTextField creationTextField;
    private javax.swing.JButton deleteAllVehicles;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JTextField disallowReason;
    private javax.swing.JTextField driverTextField;
    private javax.swing.JButton dummyButton1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler31;
    private javax.swing.Box.Filler filler40_1;
    private javax.swing.Box.Filler filler40_2;
    private javax.swing.Box.Filler filler42;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler50;
    private javax.swing.Box.Filler filler53;
    private javax.swing.Box.Filler filler55;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JLabel formModeLabel;
    private javax.swing.JLabel formTitleLabel;
    public javax.swing.JButton insertSave_Button;
    private javax.swing.JLabel isIDreqLabel1;
    private javax.swing.JLabel isTagReqLabel;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lastModiLabel;
    private javax.swing.JTextField lastModiTextField;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JLabel legendLLabel;
    private javax.swing.JLabel legendString;
    private javax.swing.JPanel mdePlusRight;
    private javax.swing.JPanel metaKeyPanel;
    private javax.swing.JLabel metaLabel;
    private javax.swing.JPanel modePanel;
    private javax.swing.JPanel modeStringPanel;
    private javax.swing.JButton modiSave_Button;
    private javax.swing.JCheckBox notiCheckBox;
    private javax.swing.JLabel notiLabel;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JButton odsHelpButton;
    private javax.swing.JTextField otherInfoTextField;
    private javax.swing.JLabel otherLabel;
    private javax.swing.JCheckBox permitCheckBox;
    private javax.swing.JLabel permitLabel;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JLabel phoneTextLabel;
    private javax.swing.JButton readSheet_Button;
    private javax.swing.JLabel reasonLabel;
    private javax.swing.JTextField reasonTextField;
    private javax.swing.JPanel rightButtons;
    private javax.swing.JPanel rowCountSz;
    private javax.swing.JLabel rowNumLabel;
    private javax.swing.JTextField rowNumTextField;
    private javax.swing.JButton sampleButton;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JComboBox searchAffiliCBox;
    private javax.swing.JComboBox searchBldgCBox;
    public javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCarTag;
    private javax.swing.JTextField searchDriver;
    private javax.swing.JTextField searchETC;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JButton selectDriverButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel tarTagLabel;
    private javax.swing.JPanel titleBelow;
    private javax.swing.JPanel titlePanelReal;
    private javax.swing.JPanel topMarginPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTable vehiclesTable;
    private javax.swing.JCheckBox wholeCheckBox;
    private javax.swing.JLabel wholeLabel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    private String formSearchCondition() {
        StringBuffer cond = new StringBuffer();       

        Object keyObj = ((ConvComboBoxItem)searchAffiliCBox.getSelectedItem()).getKeyValue();
        attachIntCondition(cond, "L2_NO", (Integer) keyObj); 

        if (searchBldgCBox.getSelectedItem() != null) {
            keyObj = ((ConvComboBoxItem)searchBldgCBox.getSelectedItem()).getKeyValue();
            attachIntCondition(cond, "UNIT_SEQ_NO", (Integer)keyObj);
        }

        if (cond.length() > 0) {
            return cond.toString();
        } else {
            return "";
        }            
    }
    
    private String formSearchString(List<String> keyList) {
        StringBuffer cond = new StringBuffer(); 
        String searchStr = searchCarTag.getText().trim();
        
        if (!carTagHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "PLATE_NUMBER", searchStr.length());
            keyList.add(searchStr);
        }
        
        searchStr = searchDriver.getText().trim();
        if (!driverHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "NAME", searchStr.length());
            keyList.add(searchStr);
        }   

        searchStr = searchETC.getText().trim();
        if (!etcHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "OTHER_INFO", searchStr.length());
            keyList.add(searchStr);
        }

        searchStr = disallowReason.getText().trim();
        if (!reasonHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "REMARK", searchStr.length());
            keyList.add(searchStr);
        }  
        
        return cond.toString();
    }
    
    public void loadVehicleTable(int viewIndex, String plateNo) {
        DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();  
        model.setRowCount(0);
        // <editor-fold defaultstate="collapsed" desc="-- load vehicle list">                          
        Connection conn = null;
        PreparedStatement fetchVehicles = null;
        ResultSet rs = null;
        String excepMsg = "(registered vehicle list loading)";
        
        try {
            // <editor-fold defaultstate="collapsed" desc="-- construct SQL statement">  
            StringBuffer sb = new StringBuffer(); 
            sb.append("SELECT @ROWNUM := @ROWNUM + 1 recNo, TA.* ");
            sb.append("FROM ( ");
            sb.append(" select B.*, concat(BT.BLDG_NO, '-', U.UNIT_NO) building");
            sb.append(" from ");
            sb.append("   (select A.*, concat(L1.PARTY_NAME, '-', L2.PARTY_NAME) affiliation");
            sb.append("   from (select V.PLATE_NUMBER, C.SEQ_NO, C.NAME, C.CELLPHONE, ");
            sb.append("            C.PHONE, C.L2_NO, C.UNIT_SEQ_NO, V.NOTI_REQUESTED, ");
            sb.append("            V.WHOLE_REQUIRED, V.PERMITTED, V.REMARK, ");
            sb.append("            V.OTHER_INFO, V.CREATIONDATE, V.LASTMODIDATE");
            sb.append("        from vehicles V, cardriver C");
            sb.append("        where V.DRIVER_SEQ_NO = C.SEQ_NO");
            sb.append("        ) A");
            sb.append("        Left join L2_affiliation L2 on A.L2_NO = L2.L2_NO");
            sb.append("        Left join L1_affiliation L1 on L2.L1_NO = L1.L1_NO");
            sb.append("   ) B ");
            sb.append("   LEFT JOIN building_unit U ON UNIT_SEQ_NO = U.SEQ_NO");
            sb.append("   LEFT JOIN building_table BT ON BLDG_SEQ_NO = BT.SEQ_NO) TA,");
            sb.append("   (SELECT @rownum := 0) r ");

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
            sb.append(" ORDER BY PLATE_NUMBER");
            //</editor-fold>   
            
            conn = getConnection();
            fetchVehicles = conn.prepareStatement(sb.toString());
            int index = 1;
            for (String searchKey : currKeyList) {
                fetchVehicles.setString(index++, "%" + prependEscape(searchKey) + "%");                
            }
            
            rs = fetchVehicles.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                // <editor-fold defaultstate="collapsed" desc="-- make a vehicle row">                          
                model.addRow(new Object[] {
                    rs.getInt("recNo"), 
                    rs.getString("PLATE_NUMBER"), 
                    rs.getString("NAME"), 
                    rs.getString("AFFILIATION"), 
                    rs.getString("BUILDING"), 
                    rs.getString("OTHER_INFO"),
                    rs.getString("CELLPHONE"),
                    rs.getString("PHONE"),
                    rs.getInt("NOTI_REQUESTED"),
                    rs.getInt("WHOLE_REQUIRED"),
                    rs.getInt("PERMITTED"),
                    rs.getString("REMARK"),
                    rs.getString("CREATIONDATE"),
                    rs.getString("LASTMODIDATE"),
                    rs.getString("SEQ_NO")
                });
                if (viewIndex == -1 && plateNo.equals(rs.getString("PLATE_NUMBER"))) {
                    viewIndex = model.getRowCount() - 1;
                }
                //</editor-fold>
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, fetchVehicles, rs, excepMsg);
            Dimension tableDim = new Dimension(vehiclesTable.getSize().width, 
                    vehiclesTable.getRowHeight() * (vehiclesTable.getRowCount())); 
            vehiclesTable.setSize(tableDim);
            vehiclesTable.setPreferredSize(tableDim);
            countValue.setText(String.valueOf(vehiclesTable.getRowCount()));            
        }
        
        int numRows = model.getRowCount();
        if (numRows > 0) {
            if (viewIndex >= numRows)
                viewIndex = numRows - 1;
            showVehicleDetail(viewIndex);
            highlightTableRow(vehiclesTable, viewIndex); 
            vehiclesTable.requestFocus();
        } else {
            // clear left side panel vehicle details
            clearVehicleDetail();
        }
        searchButton.setEnabled(false);        
    }

    private void showVehicleDetail(int viewIndex) {
        TableModel vModel = vehiclesTable.getModel();

        Integer rowNo = (Integer)vModel.getValueAt(viewIndex, VehicleCol.RowNo.getNumVal());
        rowNumTextField.setText(rowNo.toString());
        carTagTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.PlateNumber.getNumVal()));
        driverTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Name.getNumVal()));
        cellTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.CellPhone.getNumVal()));
        phoneTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Phone.getNumVal()));
        
        int noti = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Notification.getNumVal());
        notiCheckBox.setSelected(noti == 1 ? true : false);
        
        int whole = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Whole.getNumVal());
        wholeCheckBox.setSelected(whole == 1 ? true : false);
        
        int perm = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Permitted.getNumVal());
        permitCheckBox.setSelected(perm == 1 ? false : true);
       
        reasonTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Causes.getNumVal()));
        otherInfoTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.OtherInfo.getNumVal()));
        
        creationTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Creation.getNumVal()));
        lastModiTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Modification.getNumVal()));  
    }

    private void attachEventListenerToVehicleTable() {
        vehiclesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
        {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) 
                    return;
                
                if (getFormMode() == FormMode.NormalMode) {
                    if (vehiclesTable.getSelectedRowCount() == 0) {
                        modiSave_Button.setEnabled(false);
                        deleteButton.setEnabled(false);
                    } else {
                        modiSave_Button.setEnabled(isManager);
                        deleteButton.setEnabled(isManager);
                    }                    
                }
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (vehiclesTable.getSelectedRow() >= 0) {
                            if (getFormMode() == FormMode.NormalMode) {
                                showVehicleDetail(vehiclesTable.convertRowIndexToModel(
                                        vehiclesTable.getSelectedRow()));  
                            } else {
                                String dialogMessage = "";
                                
                                switch (language) {
                                    case KOREAN:
                                        dialogMessage = "차량정보 " 
                                                + (formMode == FormMode.CreateMode ? "생성" : "변경") 
                                                + " 중입니다";
                                        break;
                                        
                                    case ENGLISH:
                                        dialogMessage = "Car information is being" 
                                                + (formMode == FormMode.CreateMode ? "created." : "modified.");
                                        break;
                                        
                                    default:
                                        break;
                                }
                                
                                JOptionPane.showConfirmDialog(null, dialogMessage,
                                        WORK_MODE_DIALOGTITLE.getContent(),
                                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                                
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * @return the formMode
     */
    public FormMode getFormMode() {
        return formMode;
    }

    private void setSearchEnabled(boolean flag) {
        vehiclesTable.setEnabled(flag);
        searchCarTag.setEnabled(flag);
        searchDriver.setEnabled(flag);
        searchAffiliCBox.setEnabled(flag);
        searchBldgCBox.setEnabled(flag);
        searchETC.setEnabled(flag);
        disallowReason.setEnabled(flag);
        closeFormButton.setEnabled(flag);
        cancel_Button.setEnabled(!flag);        
        
        if (flag && isManager) {
            if (vehiclesTable.getRowCount() > 0) {
                saveSheet_Button.setEnabled(true);
            } else {
                saveSheet_Button.setEnabled(false);
            }
        } else {
            saveSheet_Button.setEnabled(false);
        }        
    }

    /**
     * @param formMode the formMode to set
     */
    public void setFormMode(FormMode aFormMode) {
        
        // change mode value
        FormMode prevMode = this.formMode;
        this.formMode = aFormMode;
            
        // switch label for the users, change button label/functionality
        switch (aFormMode) {
            case CreateMode:
                formModeLabel.setText(CREATE_MODE_LABEL.getContent());
                setSearchEnabled(false);
                modiSave_Button.setEnabled(false);
                deleteButton.setEnabled(false);
                insertSave_Button.setText(SAVE_BTN.getContent());
                insertSave_Button.setMnemonic('s');
                makeVehicleInfoFieldsEditable(true);
                adminOperationEnabled(false, deleteAllVehicles, odsHelpButton, sampleButton, readSheet_Button);
                break;
                
            case UpdateMode:
                formModeLabel.setText(MODIFY_MODE_LABEL.getContent());
                setSearchEnabled(false);
                insertSave_Button.setEnabled(false);
                modiSave_Button.setText(SAVE_BTN.getContent());
                modiSave_Button.setMnemonic('s');
                deleteButton.setEnabled(false);
                makeVehicleInfoFieldsEditable(true);
                adminOperationEnabled(false, deleteAllVehicles, odsHelpButton, sampleButton, readSheet_Button);
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
                
                if (vehiclesTable.getSelectedRowCount() > 0) {
                    modiSave_Button.setEnabled(isManager);
                    deleteButton.setEnabled(isManager);
                }                
                makeVehicleInfoFieldsEditable(false);
                adminOperationEnabled(true, deleteAllVehicles, odsHelpButton, sampleButton, readSheet_Button);                
                break;
            default:
                formModeLabel.setText("");
                break;
        }
    }

    /**
     * Changes editable properties of various components of a vehicle management form.
     * @param b tells if the text and other fields are editable or not
     */
    private void makeVehicleInfoFieldsEditable(boolean b) {
        
        // As car tag number is the primary key of the vehicle table
        // it shouldn't be modified. It needs to be editable for insertion though.
        // and be put back to non-editable after the new vehicle inserted.
        if (getFormMode() == FormMode.CreateMode && b) {
            carTagTextField.setEditable(true);
            carTagTextField.setEnabled(true);
            isTagReqLabel.setEnabled(true);
        } else {
            carTagTextField.setEditable(false);
            carTagTextField.setEnabled(false);
            isTagReqLabel.setEnabled(false);
        }
        notiCheckBox.setEnabled(b);
        wholeCheckBox.setEnabled(b);
        permitCheckBox.setEnabled(b);
       
        reasonTextField.setEditable(b);
        otherInfoTextField.setEditable(b);
        
        // enable or disable related buttons
        selectDriverButton.setEnabled(b);      
        deleteAllVehicles.setEnabled(!b);
        readSheet_Button.setEnabled(!b);
    }
    
    public void setDriverInfo(String name, String cell, String phone, int seqNo) {
        driverTextField.setText(name);
        cellTextField.setText(cell);
        phoneTextField.setText(phone);
        driverObj = new DriverObj(name, seqNo);
    }

    private int insertNewVehicle(StringBuffer plateNo, StringBuffer vehicleProperties) {
        plateNo.append(carTagTextField.getText().trim());

        int result = insertOneVehicle(plateNo.toString(), driverObj.getSeqNo(), 
                notiCheckBox.isSelected() ? 1 : 0, wholeCheckBox.isSelected() ? 1 : 0,
                permitCheckBox.isSelected() ? 0 : 1, reasonTextField.getText().trim(),
                otherInfoTextField.getText().trim());
        
        if (result == 1) {
            vehicleProperties.append("Vehicle Creation Summary: " + System.lineSeparator());
            getVehicleProperties(vehicleProperties);
            logParkingOperation(OpLogLevel.UserCarChange, operationLogName);
        }
        
        return result;         
    }

    private void closeFrameGracefully() {
        if (formMode == FormMode.NormalMode) {
            disposeExit();
        } else {
            
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = (formMode == FormMode.CreateMode ? "생성" : "변경")
                            + " 중인 차량정보를 포기하겠습니까?";
                    break;
                    
                case ENGLISH:
                    dialogMessage = "Do you want to give up " +
                                (formMode == FormMode.CreateMode ? "registering " : "modifying ")
                                        + "a car?";
                    break;
                    
                default:
                    break;
            }
            
            int response = JOptionPane.showConfirmDialog(null, dialogMessage,
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) {
                disposeExit();
            }             
        }      
    }

    private void clearVehicleDetail() {
        
        rowNumTextField.setText("");
        carTagTextField.setText("");
        driverTextField.setText("");
        cellTextField.setText("");
        phoneTextField.setText("");

        notiCheckBox.setSelected(false);
        wholeCheckBox.setSelected(false);
        permitCheckBox.setSelected(true);

        reasonTextField.setText("");
        otherInfoTextField.setText("");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        creationTextField.setText(dateFormat.format(date));
        lastModiTextField.setText("");
    }

    private void deleteVehicles() {
        // delete a driver's record currently selected 
        int[] deleteIndice = vehiclesTable.getSelectedRows();
        if (deleteIndice.length == 0)
        {
            return;
        }
        
        int result = -1;
        String tagNumber = (String)vehiclesTable.getValueAt(deleteIndice[0], 1);
        int modal_Index = vehiclesTable.convertRowIndexToModel(deleteIndice[0]);
        
        if (deleteIndice.length == 1) {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 등록차량을 삭제합니까?" 
                            + System.getProperty("line.separator") + "차량번호: " + tagNumber;
                    break;
                    
                case ENGLISH:
                    dialogMessage = "Unregister following vehicle?" 
                        + System.getProperty("line.separator") + "Tag Number: " + tagNumber;
                    break;
                    
                default:
                    break;
            }            
            
            result = JOptionPane.showConfirmDialog(this, dialogMessage,
                        DELETE_DIALOGTITLE.getContent(),
                        JOptionPane.YES_NO_OPTION);
        } else {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 차량 및 총 " + deleteIndice.length + " 대 차량의 정보를 삭제합니까?" 
                            + System.getProperty("line.separator") + "차량번호: " + tagNumber;
                    break;
                    
                case ENGLISH:
                    dialogMessage ="Unregistered total " + deleteIndice.length + " vehicles including following?" 
                        + System.getProperty("line.separator") + "Tag number: " + tagNumber;
                    break;
                    
                default:
                    break;
            }

            result = JOptionPane.showConfirmDialog(this, dialogMessage,
                    DELETE_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION);
        }    
        
        if (result == JOptionPane.YES_OPTION) {
            // <editor-fold defaultstate="collapsed" desc="-- delete registered vehicles ">   
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = null;
            int totalDeletion = 0;
            
            result = -1;
            try {
                String sql = "Delete From vehicles Where PLATE_NUMBER = ?";
                conn = getConnection();
                createBuilding = conn.prepareStatement(sql);
                for (int indexNo : deleteIndice) {
                    modal_Index = vehiclesTable.convertRowIndexToModel(indexNo);
                    tagNumber = (String)vehiclesTable.getModel().getValueAt(modal_Index, 1);
                    excepMsg = "(deleting vehivle: " + tagNumber + ")";
                    createBuilding.setString(1, tagNumber);
                    result = createBuilding.executeUpdate();
                    totalDeletion += result;
                }
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);

                if (result == 1) {
                    loadVehicleTable(deleteIndice[0], ""); // pass row index of deleted vehicle
                    
                    String dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "차량 '" + tagNumber + "'의 기록이" + System.getProperty("line.separator")
                                    + "삭제되었습니다";
                            break;
                            
                        case ENGLISH:
                            dialogMessage ="Record of car '" + tagNumber + "'" + System.getProperty("line.separator")
                                    + "has been removed";
                            break;
                            
                        default:
                            break;
                    }                    
                    logParkingOperation(OpLogLevel.UserCarChange, 
                            ("* Vechcle deleted :" + dialogMessage + ")"));
                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }

    private int saveUpdatedVehicle(StringBuffer vehicleProperties) {
        int result = 0;
        
        Connection conn = null;
        PreparedStatement updateDriver = null;
        String excepMsg = "updating vehicle plate no: " + carTagTextField.getText();

        try {
            StringBuffer sb = new StringBuffer("Update Vehicles Set ");
            sb.append(" DRIVER_SEQ_NO = ?, NOTI_REQUESTED = ?,");
            sb.append(" WHOLE_REQUIRED = ?, PERMITTED = ?, Remark = ?,");
            sb.append(" OTHER_INFO = ?, LASTMODIDATE = current_timestamp");
            sb.append(" Where PLATE_NUMBER = ?");

            conn = getConnection();
            updateDriver = conn.prepareStatement(sb.toString());
            int seqNo = driverObj.getSeqNo();
            if (seqNo == 0) {
                seqNo = Integer.parseInt((String)vehiclesTable.getModel().getValueAt(
                    vehiclesTable.getSelectedRow(), VehicleCol.SeqNo.getNumVal()));        
            }
            updateDriver.setInt(1, seqNo);
            updateDriver.setInt(2, notiCheckBox.isSelected() ? 1 : 0);
            updateDriver.setInt(3, wholeCheckBox.isSelected() ? 1 : 0);
            updateDriver.setInt(4, permitCheckBox.isSelected() ? 0 : 1);
            updateDriver.setString(5, reasonTextField.getText().trim());
            updateDriver.setString(6, otherInfoTextField.getText().trim());
            updateDriver.setString(7, carTagTextField.getText().trim());
            
            vehicleProperties.append("Vehicle Update Summary: " + System.lineSeparator());
            getVehicleProperties(vehicleProperties);

            result = updateDriver.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, updateDriver, null, excepMsg);
        }

        return result;         
    }

    private void loadSearchBox() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String excepMsg = "(CBox Item Loading for : 2 affiliation columns combined";

        searchAffiliCBox.removeAllItems();
        searchAffiliCBox.addItem(new ConvComboBoxItem(new Integer(-1), 
                HIGHER_LOWER_CB_ITEM.getContent()));
        try {
            //<editor-fold defaultstate="collapsed" desc="-- load affiliation comboBox">                            
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();

            sb.append("SELECT concat(L1.PARTY_NAME, '-', L2.PARTY_NAME) ");
            sb.append("  as PARTY_NAME, L2_NO ");
            sb.append("FROM L2_affiliation L2, L1_affiliation L1 ");
            sb.append("Where L2.L1_NO = L1.L1_NO ");
            sb.append("Order by L1.PARTY_NAME, L2.party_name");

            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                ConvComboBoxItem item = new ConvComboBoxItem(rs.getInt("L2_NO"), rs.getString("PARTY_NAME"));
                searchAffiliCBox.addItem(item);
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }
        
        searchBldgCBox.removeAllItems();
        searchBldgCBox.addItem(new ConvComboBoxItem(new Integer(-1), 
                BUILDING_ROOM_CB_ITEM.getContent()));
        excepMsg = "(CBox Item Loading for : building-unit combined";
        try {
            //<editor-fold defaultstate="collapsed" desc="-- load affiliation comboBox">                            
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT concat(bd.BLDG_NO, '-', ut.UNIT_NO) ");
            sb.append(" as BLDG_UNIT, ut.SEQ_NO ");
            sb.append("FROM building_UNIT ut, building_table bd ");
            sb.append("Where ut.BLDG_SEQ_NO = bd.SEQ_NO ");
            sb.append("Order By bd.BLDG_NO, ut.UNIT_NO");
            
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                ConvComboBoxItem item = new ConvComboBoxItem(rs.getInt("SEQ_NO"), rs.getString("BLDG_UNIT"));
                searchBldgCBox.addItem(item);
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }        
    }
    
    private void attachEnterHandler(JComponent compo) {
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().getClass() == PComboBox.class) {
                    PComboBox cBox = (PComboBox)e.getSource();
                    if (cBox.isPopupVisible()) {
                        ConvComboBoxItem item = (ConvComboBoxItem)cBox.getHighlightedCbxItem();
                        cBox.setSelectedItem(item);
                        cBox.setPopupVisible(false);
                    } else
                        loadVehicleTable(0, "");
                    
                } else
                    loadVehicleTable(0, "");
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);
    }    

    private void getVehicleProperties(StringBuffer vehicleProperties) {
        vehicleProperties.append("  Tag number: " + carTagTextField.getText().trim() + System.lineSeparator());
        vehicleProperties.append("  Driver name: " + driverObj.getName() + System.lineSeparator());
        
        vehicleProperties.append("  Household notification: " + (notiCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());
        vehicleProperties.append("  Full tag comparision: " + (wholeCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());
        vehicleProperties.append("  Parking permitted: " + (permitCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());       
        
        vehicleProperties.append("  Detailed reason: " + reasonTextField.getText() + System.lineSeparator());
        vehicleProperties.append("  Other info': " + otherInfoTextField.getText() + System.lineSeparator());
    }

    private void fixControlDimensions() {
        setComponentSize(rowNumTextField, new Dimension(carTagWidth, 30));
        setComponentSize(carTagTextField, new Dimension(carTagWidth, 30));
    }

    private void showCarTagTip() {
        searchCarTag.setText(CAR_TAG_TF.getContent());
        carTagHintShown = true;
        searchCarTag.setForeground(tipColor);        
    }
    
    private boolean carTagHintShown = true;
    private boolean driverHintShown = true;
    private boolean etcHintShown = true;
    private boolean reasonHintShown = true;

    private void showDriverTip() {
        searchDriver.setText(DRIVER_TF.getContent());
        driverHintShown = true;
        searchDriver.setForeground(tipColor);
    }

    private void showEtcTip() {
        searchETC.setText(OTHER_INFO_TF.getContent()); 
        etcHintShown = true;
        searchETC.setForeground(tipColor);
    }

    private void showReasonTip() {
        disallowReason.setText(DIS_REASON_TF.getContent());
        reasonHintShown = true;
        disallowReason.setForeground(tipColor);
    }

    private void disposeExit() {
        if (mainForm != null) {
            mainForm.getTopForms()[ControlEnums.TopForms.Vehicle.ordinal()] = null;
        }
        dispose();
        if (isStandalone) {
            System.exit(0);
        } 
    }
}
