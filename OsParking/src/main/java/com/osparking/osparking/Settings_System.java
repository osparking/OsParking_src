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

import com.osparking.global.names.JDBCMySQL;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTextField;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.EBD_blinkCycle;
import static com.osparking.global.names.DB_Access.EBD_flowCycle;
import static com.osparking.global.names.DB_Access.PIC_HEIGHT;
import static com.osparking.global.names.DB_Access.PIC_WIDTH;
import static com.osparking.global.names.DB_Access.deviceIP;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.gateNames;
import static com.osparking.global.names.DB_Access.localeIndex;
import static com.osparking.global.names.DB_Access.passingDelayCurrentTotalMs;
import static com.osparking.global.names.DB_Access.maxMaintainDate;
import static com.osparking.global.names.DB_Access.maxMessageLines;
import static com.osparking.global.names.DB_Access.opLoggingIndex;
import static com.osparking.global.names.DB_Access.storePassingDelay;
import static com.osparking.global.names.DB_Access.pwStrengthLevel;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.DB_Access.statCount;
import static com.osparking.global.names.DB_Access.passingCountCurrent;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*; 
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import static com.osparking.global.names.OSP_enums.OpLogLevel.LogAlways;
import static com.osparking.global.names.OSP_enums.OpLogLevel.SettingsChange;
import static com.osparking.global.names.OSP_enums.OpLogLevel.EBDsettingsChange;
import com.osparking.global.names.PasswordValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import com.osparking.attendant.PWHelpJDialog;
import com.osparking.global.names.ChangeSettings;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CANCEL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_BTN;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.REBOOT_MESSAGE;
import static com.osparking.global.names.ControlEnums.LabelContent.BLINGKING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CAMERA_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CYCLE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ELECTRONIC_DISPLAY_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.E_BOARD_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FLOWING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_BAR_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NUM_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.IMG_KEEP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LANGUAGE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOGGING_LEVEL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOT_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MAX_LINE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PASSWORD_LEVEL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.RECORD_PASSING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SECONDS_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.STATISTICS_SIZE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VEHICLE_IMG_HEIGHT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VEHICLE_IMG_SIZE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VEHICLE_IMG_WIDTH_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AVERAGE_WORDS;
import static com.osparking.global.names.ControlEnums.MsgContent.RECENT_WORD;
import static com.osparking.global.names.ControlEnums.TitleTypes.REBOOT_POPUP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.*;
import com.osparking.global.names.DB_Access;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.DB_Access.deviceComID;
import static com.osparking.global.names.DB_Access.devicePort;
import static com.osparking.global.names.DB_Access.deviceType;
import static com.osparking.global.names.DB_Access.parkingLotName;
import com.osparking.global.names.OSP_enums.CameraType;
import com.osparking.global.names.OSP_enums.ConnectionType;
import static com.osparking.global.names.OSP_enums.ConnectionType.RS_232;
import com.osparking.global.names.OSP_enums.DeviceType;
import com.osparking.global.names.OSP_enums.E_BoardType;
import com.osparking.global.names.OSP_enums.GateBarType;
import com.osparking.global.names.OSP_enums.PWStrengthLevel;
import static com.osparking.osparking.ControlGUI.EBD_DisplaySettings;
import com.osparking.osparking.device.LEDnotice.LEDnoticeManager;
import com.osparking.osparking.device.LEDnotice.Settings_LEDnotice;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * Provides GUI to set system parameters like image size(pixels), gate count, device IP address, etc.
 * For a stable operation, it is recommended to reboot this system(OS.Parking program) after any setting change.
 * @author Open Source Parking Inc.
 */
public class Settings_System extends javax.swing.JFrame {
    private boolean isStand_Alone = false;
    private static Logger logException = null;
    private static Logger logOperation = null;
    public static ControlGUI mainForm = null;
    private HashMap<String, Component> componentMap = new HashMap<String,Component>();
    public short maxArrivalCBoxIndex = 0;
    private HashSet<Component> changedControls = new HashSet<Component>();    
    
    /**
     * Initialize some controls on the system settings change GUI. 
     */
    public Settings_System(ControlGUI mainForm) {
        initComponents();
        setIconImages(OSPiconList);                
        augmentComponentMap(this, componentMap);
        
        this.mainForm = mainForm;
        if (mainForm == null)
            isStand_Alone = true;
        addPWStrengthItems();
        addMaxArrivalItems();
        maxArrivalCBoxIndex = findCBoxIndex(ImageDurationCBox, maxMaintainDate);
        addOperationLoggingLevelOptions();
        loadComponentValues();
        makeEnterActAsTab();
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

        wholePanel = new javax.swing.JPanel();
        parkinglotOptionPanel = new javax.swing.JPanel();
        PWStrengthChoiceComboBox = new javax.swing.JComboBox<ConvComboBoxItem>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        OptnLoggingLevelComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        MessageMaxLineComboBox = new javax.swing.JComboBox();
        LoggingLevelHelpButton = new javax.swing.JButton();
        PWHelpButton = new javax.swing.JButton();
        ImageDurationCBox = new javax.swing.JComboBox<ConvComboBoxItem>();
        ImageDurationLabel = new javax.swing.JLabel();
        GateCountComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        TextFieldPicWidth = new javax.swing.JTextField();
        TextFieldPicHeight = new javax.swing.JTextField();
        DateChooserLangCBox = new com.toedter.components.JLocaleChooser();
        LanguageSelectionlHelpButton = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        lotNameTextField = new javax.swing.JTextField();
        StatPopSizeTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        RecordPassingDelayCBox = new javax.swing.JCheckBox();
        gateSettingPanel = new javax.swing.JPanel();
        GatesTabbedPane = new javax.swing.JTabbedPane();
        gate1Panel = new javax.swing.JPanel();
        gate_name_p = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 32767));
        gateNameLabel1 = new javax.swing.JLabel();
        TextFieldGateName1 = new javax.swing.JTextField();
        topLabelsPanel = new javax.swing.JPanel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        device1_Label = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        cameraPan = new javax.swing.JPanel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        jLabel30 = new javax.swing.JLabel();
        Camera1_TypeCBox = new javax.swing.JComboBox();
        Camera1_connTypeCBox = new javax.swing.JComboBox();
        Camera1_IP_TextField = new javax.swing.JTextField();
        Camera1_Port_TextField = new javax.swing.JTextField();
        eBoardPan = new javax.swing.JPanel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        jLabel37 = new javax.swing.JLabel();
        E_Board1_TypeCBox = new javax.swing.JComboBox();
        E_Board1_connTypeCBox = new javax.swing.JComboBox();
        E_Board1_conn_detail_Pan = new javax.swing.JPanel();
        E_Board1_IP_TextField = new javax.swing.JTextField();
        E_Board1_Port_TextField = new javax.swing.JTextField();
        gateBarPan = new javax.swing.JPanel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        ebdLbl = new javax.swing.JLabel();
        GateBar1_TypeCBox = new javax.swing.JComboBox();
        GateBar1_connTypeCBox = new javax.swing.JComboBox();
        GateBar1_conn_detail_Pan = new javax.swing.JPanel();
        GateBar1_IP_TextField = new javax.swing.JTextField();
        GateBar1_Port_TextField = new javax.swing.JTextField();
        gate2Panel = new javax.swing.JPanel();
        gate_name_p1 = new javax.swing.JPanel();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 32767));
        gateNameLabel2 = new javax.swing.JLabel();
        TextFieldGateName2 = new javax.swing.JTextField();
        topLabelsPanel1 = new javax.swing.JPanel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        device1_Label1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        cameraPan1 = new javax.swing.JPanel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        jLabel44 = new javax.swing.JLabel();
        Camera2_TypeCBox = new javax.swing.JComboBox();
        Camera2_connTypeCBox = new javax.swing.JComboBox();
        Camera2_IP_TextField = new javax.swing.JTextField();
        Camera2_Port_TextField = new javax.swing.JTextField();
        eBoardPan1 = new javax.swing.JPanel();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        jLabel45 = new javax.swing.JLabel();
        E_Board2_TypeCBox = new javax.swing.JComboBox();
        E_Board2_connTypeCBox = new javax.swing.JComboBox();
        E_Board2_conn_detail_Pan = new javax.swing.JPanel();
        E_Board2_IP_TextField = new javax.swing.JTextField();
        E_Board2_Port_TextField = new javax.swing.JTextField();
        gateBarPan1 = new javax.swing.JPanel();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(50, 32767));
        ebdLbl1 = new javax.swing.JLabel();
        GateBar2_TypeCBox = new javax.swing.JComboBox();
        GateBar2_connTypeCBox = new javax.swing.JComboBox();
        GateBar2_conn_detail_Pan = new javax.swing.JPanel();
        GateBar2_IP_TextField = new javax.swing.JTextField();
        GateBar2_Port_TextField = new javax.swing.JTextField();
        gate3Panel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        TextFieldGateName3 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        Camera3_IP_TextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        GateBar3_IP_TextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        E_Board3_IP_TextField = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        Camera3_Port_TextField = new javax.swing.JTextField();
        GateBar3_Port_TextField = new javax.swing.JTextField();
        E_Board3_Port_TextField = new javax.swing.JTextField();
        gate4Panel = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        TextFieldGateName4 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        Camera4_IP_TextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        GateBar4_IP_TextField = new javax.swing.JTextField();
        E_Board4_IP_TextField = new javax.swing.JTextField();
        Camera4_Port_TextField = new javax.swing.JTextField();
        GateBar4_Port_TextField = new javax.swing.JTextField();
        E_Board4_Port_TextField = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        Camera4_Port_TextField1 = new javax.swing.JTextField();
        GateBar4_Port_TextField1 = new javax.swing.JTextField();
        E_Board4_Port_TextField1 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        eBoardSettingPanel = new javax.swing.JPanel();
        E_BoardSettingsButtonPanel = new javax.swing.JPanel();
        EBD_settings_label = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        EBD_settings = new javax.swing.JPanel();
        EBoardSettingsButton = new javax.swing.JButton();
        allCyclesPanel = new javax.swing.JPanel();
        cycleLabel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        twoCycles = new javax.swing.JPanel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        real2Pan = new javax.swing.JPanel();
        flowPanel = new javax.swing.JPanel();
        flowing = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(30, 32767));
        jLabel31 = new javax.swing.JLabel();
        cBoxPanel = new javax.swing.JPanel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(30, 32767));
        FlowingComboBox = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        blinkPanel = new javax.swing.JPanel();
        blinkingP = new javax.swing.JPanel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(30, 32767));
        blinkingL = new javax.swing.JLabel();
        cBoxPan = new javax.swing.JPanel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(30, 32767));
        BlinkingComboBox = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        SettingsSaveButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        SettingsCancelButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        SettingsCloseButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(10, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(10, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 20));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("System Settings -- OsParking");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(775, 800));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                finishSettingsForm(evt);
            }
        });

        wholePanel.setMinimumSize(new java.awt.Dimension(630, 738));
        wholePanel.setPreferredSize(new java.awt.Dimension(702, 800));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        parkinglotOptionPanel.setMinimumSize(new java.awt.Dimension(690, 300));
        parkinglotOptionPanel.setPreferredSize(new java.awt.Dimension(0, 300));
        parkinglotOptionPanel.setLayout(new java.awt.GridBagLayout());

        PWStrengthChoiceComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        PWStrengthChoiceComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        PWStrengthChoiceComboBox.setMinimumSize(new java.awt.Dimension(150, 23));
        PWStrengthChoiceComboBox.setName("PWStrengthChoiceComboBox"); // NOI18N
        PWStrengthChoiceComboBox.setPreferredSize(new java.awt.Dimension(150, 23));
        PWStrengthChoiceComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                PWStrengthChoiceComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        parkinglotOptionPanel.add(PWStrengthChoiceComboBox, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(PASSWORD_LEVEL_LABEL.getContent());
        jLabel1.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(LOGGING_LEVEL_LABEL.getContent());
        jLabel2.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel2, gridBagConstraints);

        OptnLoggingLevelComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        OptnLoggingLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        OptnLoggingLevelComboBox.setMinimumSize(new java.awt.Dimension(150, 23));
        OptnLoggingLevelComboBox.setName("OptnLoggingLevelComboBox"); // NOI18N
        OptnLoggingLevelComboBox.setPreferredSize(new java.awt.Dimension(150, 23));
        OptnLoggingLevelComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                OptnLoggingLevelComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        parkinglotOptionPanel.add(OptnLoggingLevelComboBox, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(LANGUAGE_LABEL.getContent());
        jLabel3.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel3, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(MAX_LINE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel5, gridBagConstraints);

        MessageMaxLineComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        MessageMaxLineComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100", "200", "300", "500", "1000" }));
        MessageMaxLineComboBox.setMinimumSize(new java.awt.Dimension(70, 23));
        MessageMaxLineComboBox.setName("MessageMaxLineComboBox"); // NOI18N
        MessageMaxLineComboBox.setPreferredSize(new java.awt.Dimension(70, 23));
        MessageMaxLineComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                MessageMaxLineComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        parkinglotOptionPanel.add(MessageMaxLineComboBox, gridBagConstraints);

        LoggingLevelHelpButton.setBackground(new java.awt.Color(153, 255, 153));
        LoggingLevelHelpButton.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        LoggingLevelHelpButton.setIcon(getQuest20_Icon());
        LoggingLevelHelpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        LoggingLevelHelpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        LoggingLevelHelpButton.setOpaque(false);
        LoggingLevelHelpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        LoggingLevelHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoggingLevelHelpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        parkinglotOptionPanel.add(LoggingLevelHelpButton, gridBagConstraints);

        PWHelpButton.setBackground(new java.awt.Color(153, 255, 153));
        PWHelpButton.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        PWHelpButton.setIcon(getQuest20_Icon());
        PWHelpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        PWHelpButton.setMaximumSize(new java.awt.Dimension(20, 20));
        PWHelpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        PWHelpButton.setOpaque(false);
        PWHelpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        PWHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWHelpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        parkinglotOptionPanel.add(PWHelpButton, gridBagConstraints);

        ImageDurationCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ImageDurationCBox.setToolTipText("");
        ImageDurationCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        ImageDurationCBox.setName("ImageDurationCBox"); // NOI18N
        ImageDurationCBox.setPreferredSize(new java.awt.Dimension(80, 23));
        ImageDurationCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                ImageDurationCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        parkinglotOptionPanel.add(ImageDurationCBox, gridBagConstraints);

        ImageDurationLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ImageDurationLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ImageDurationLabel.setText(IMG_KEEP_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(ImageDurationLabel, gridBagConstraints);

        GateCountComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateCountComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4" }));
        GateCountComboBox.setMinimumSize(new java.awt.Dimension(70, 23));
        GateCountComboBox.setName("GateCountComboBox"); // NOI18N
        GateCountComboBox.setPreferredSize(new java.awt.Dimension(70, 23));
        GateCountComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                GateCountComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        parkinglotOptionPanel.add(GateCountComboBox, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(GATE_NUM_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel6, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText(VEHICLE_IMG_SIZE_LABEL.getContent());
        jLabel19.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        parkinglotOptionPanel.add(jLabel19, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText(VEHICLE_IMG_WIDTH_LABEL.getContent());
        jLabel11.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        parkinglotOptionPanel.add(jLabel11, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("px");
        jLabel13.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        parkinglotOptionPanel.add(jLabel13, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText(VEHICLE_IMG_HEIGHT_LABEL.getContent());
        jLabel12.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        parkinglotOptionPanel.add(jLabel12, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("px");
        jLabel14.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 10, 0);
        parkinglotOptionPanel.add(jLabel14, gridBagConstraints);

        TextFieldPicWidth.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldPicWidth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        TextFieldPicWidth.setName("TextFieldPicWidth"); // NOI18N
        TextFieldPicWidth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldPicWidthKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextFieldPicWidthKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 20);
        parkinglotOptionPanel.add(TextFieldPicWidth, gridBagConstraints);

        TextFieldPicHeight.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldPicHeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        TextFieldPicHeight.setName("TextFieldPicHeight"); // NOI18N
        TextFieldPicHeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldPicHeightKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextFieldPicHeightKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 20);
        parkinglotOptionPanel.add(TextFieldPicHeight, gridBagConstraints);

        DateChooserLangCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        DateChooserLangCBox.setMinimumSize(new java.awt.Dimension(294, 23));
        DateChooserLangCBox.setName("DateChooserLangCBox"); // NOI18N
        DateChooserLangCBox.setPreferredSize(new java.awt.Dimension(280, 25));
        DateChooserLangCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                DateChooserLangCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        parkinglotOptionPanel.add(DateChooserLangCBox, gridBagConstraints);

        LanguageSelectionlHelpButton.setBackground(new java.awt.Color(153, 255, 153));
        LanguageSelectionlHelpButton.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        LanguageSelectionlHelpButton.setIcon(getQuest20_Icon());
        LanguageSelectionlHelpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        LanguageSelectionlHelpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        LanguageSelectionlHelpButton.setOpaque(false);
        LanguageSelectionlHelpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        LanguageSelectionlHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LanguageSelectionlHelpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        parkinglotOptionPanel.add(LanguageSelectionlHelpButton, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText(LOT_NAME_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel42, gridBagConstraints);

        lotNameTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lotNameTextField.setToolTipText("");
        lotNameTextField.setMinimumSize(new java.awt.Dimension(250, 27));
        lotNameTextField.setName("lotNameTextField"); // NOI18N
        lotNameTextField.setPreferredSize(new java.awt.Dimension(250, 27));
        lotNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lotNameTextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        parkinglotOptionPanel.add(lotNameTextField, gridBagConstraints);

        StatPopSizeTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        StatPopSizeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        StatPopSizeTextField.setText("100");
        StatPopSizeTextField.setToolTipText("");
        StatPopSizeTextField.setMinimumSize(new java.awt.Dimension(6, 25));
        StatPopSizeTextField.setName("StatPopSizeTextField"); // NOI18N
        StatPopSizeTextField.setPreferredSize(new java.awt.Dimension(70, 25));
        StatPopSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                StatPopSizeTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                StatPopSizeTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        parkinglotOptionPanel.add(StatPopSizeTextField, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(STATISTICS_SIZE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        parkinglotOptionPanel.add(jLabel4, gridBagConstraints);

        RecordPassingDelayCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        RecordPassingDelayCBox.setText(RECORD_PASSING_LABEL.getContent());
        RecordPassingDelayCBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        RecordPassingDelayCBox.setName("RecordPassingDelayCBox"); // NOI18N
        RecordPassingDelayCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecordPassingDelayCBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        parkinglotOptionPanel.add(RecordPassingDelayCBox, gridBagConstraints);

        wholePanel.add(parkinglotOptionPanel);

        gateSettingPanel.setMinimumSize(new java.awt.Dimension(430, 250));
        gateSettingPanel.setPreferredSize(new java.awt.Dimension(700, 250));

        GatesTabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        GatesTabbedPane.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GatesTabbedPane.setMinimumSize(new java.awt.Dimension(300, 250));
        GatesTabbedPane.setPreferredSize(new java.awt.Dimension(520, 250));

        gate1Panel.setEnabled(false);
        gate1Panel.setMinimumSize(new java.awt.Dimension(300, 115));
        gate1Panel.setPreferredSize(new java.awt.Dimension(518, 196));
        gate1Panel.setLayout(new javax.swing.BoxLayout(gate1Panel, javax.swing.BoxLayout.Y_AXIS));

        gate_name_p.setAlignmentX(1.0F);
        gate_name_p.setMinimumSize(new java.awt.Dimension(109, 20));
        gate_name_p.setPreferredSize(new java.awt.Dimension(100, 20));
        gate_name_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));
        gate_name_p.add(filler7);

        gateNameLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateNameLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gateNameLabel1.setText(GATE_NAME_LABEL.getContent());
        gateNameLabel1.setToolTipText("");
        gate_name_p.add(gateNameLabel1);

        TextFieldGateName1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldGateName1.setText("Front Gate");
        TextFieldGateName1.setToolTipText("");
        TextFieldGateName1.setMinimumSize(new java.awt.Dimension(30, 25));
        TextFieldGateName1.setName("TextFieldGateName1"); // NOI18N
        TextFieldGateName1.setPreferredSize(new java.awt.Dimension(120, 30));
        TextFieldGateName1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldGateName1KeyReleased(evt);
            }
        });
        gate_name_p.add(TextFieldGateName1);

        gate1Panel.add(gate_name_p);

        topLabelsPanel.setMinimumSize(new java.awt.Dimension(266, 20));
        topLabelsPanel.setPreferredSize(new java.awt.Dimension(100, 20));
        topLabelsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 15));
        topLabelsPanel.add(filler9);

        device1_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        device1_Label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        device1_Label.setText("Device");
        device1_Label.setPreferredSize(new java.awt.Dimension(60, 15));
        topLabelsPanel.add(device1_Label);

        jLabel26.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Type");
        jLabel26.setPreferredSize(new java.awt.Dimension(125, 15));
        topLabelsPanel.add(jLabel26);

        jLabel27.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Connection");
        jLabel27.setPreferredSize(new java.awt.Dimension(90, 15));
        topLabelsPanel.add(jLabel27);

        jLabel28.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("IP address");
        jLabel28.setPreferredSize(new java.awt.Dimension(125, 15));
        topLabelsPanel.add(jLabel28);

        jLabel29.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Port");
        jLabel29.setPreferredSize(new java.awt.Dimension(45, 15));
        topLabelsPanel.add(jLabel29);
        jLabel29.getAccessibleContext().setAccessibleName("PortLbl");

        gate1Panel.add(topLabelsPanel);

        cameraPan.setMinimumSize(new java.awt.Dimension(426, 25));
        cameraPan.setPreferredSize(new java.awt.Dimension(518, 25));
        cameraPan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        cameraPan.add(filler10);

        jLabel30.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel30.setText(CAMERA_LABEL.getContent());
        jLabel30.setMinimumSize(new java.awt.Dimension(60, 15));
        jLabel30.setPreferredSize(new java.awt.Dimension(70, 15));
        cameraPan.add(jLabel30);

        Camera1_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera1_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        Camera1_TypeCBox.setToolTipText("");
        Camera1_TypeCBox.setLightWeightPopupEnabled(false);
        Camera1_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        Camera1_TypeCBox.setName("Camera1_TypeCBox"); // NOI18N
        Camera1_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        Camera1_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                Camera1_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cameraPan.add(Camera1_TypeCBox);

        Camera1_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera1_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        Camera1_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        Camera1_connTypeCBox.setName("Camera1_connTypeCBox"); // NOI18N
        Camera1_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        Camera1_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                Camera1_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        Camera1_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Camera1_connTypeCBoxActionPerformed(evt);
            }
        });
        cameraPan.add(Camera1_connTypeCBox);

        Camera1_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera1_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Camera1_IP_TextField.setText("127.0.0.1");
        Camera1_IP_TextField.setToolTipText("");
        Camera1_IP_TextField.setMaximumSize(new java.awt.Dimension(32767, 32767));
        Camera1_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        Camera1_IP_TextField.setName("Camera1_IP_TextField"); // NOI18N
        Camera1_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        Camera1_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera1_IP_TextFieldKeyReleased(evt);
            }
        });
        cameraPan.add(Camera1_IP_TextField);

        Camera1_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera1_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Camera1_Port_TextField.setText("8080");
        Camera1_Port_TextField.setToolTipText("");
        Camera1_Port_TextField.setMaximumSize(new java.awt.Dimension(32767, 32767));
        Camera1_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        Camera1_Port_TextField.setName("Camera1_Port_TextField"); // NOI18N
        Camera1_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        Camera1_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera1_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Camera1_Port_TextFieldKeyTyped(evt);
            }
        });
        cameraPan.add(Camera1_Port_TextField);

        gate1Panel.add(cameraPan);

        eBoardPan.setMinimumSize(new java.awt.Dimension(426, 25));
        eBoardPan.setPreferredSize(new java.awt.Dimension(518, 25));
        eBoardPan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        eBoardPan.add(filler11);

        jLabel37.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel37.setText(E_BOARD_LABEL.getContent());
        jLabel37.setMinimumSize(new java.awt.Dimension(60, 15));
        jLabel37.setPreferredSize(new java.awt.Dimension(70, 15));
        eBoardPan.add(jLabel37);

        E_Board1_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board1_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        E_Board1_TypeCBox.setToolTipText("");
        E_Board1_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        E_Board1_TypeCBox.setName("E_Board1_TypeCBox"); // NOI18N
        E_Board1_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        E_Board1_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                E_Board1_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        E_Board1_TypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                E_Board1_TypeCBoxActionPerformed(evt);
            }
        });
        eBoardPan.add(E_Board1_TypeCBox);

        E_Board1_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board1_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        E_Board1_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        E_Board1_connTypeCBox.setName("E_Board1_connTypeCBox"); // NOI18N
        E_Board1_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        E_Board1_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                E_Board1_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        E_Board1_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                E_Board1_connTypeCBoxActionPerformed(evt);
            }
        });
        eBoardPan.add(E_Board1_connTypeCBox);

        E_Board1_conn_detail_Pan.setMinimumSize(new java.awt.Dimension(170, 25));
        E_Board1_conn_detail_Pan.setName("E_Board1_conn_detail_Pan"); // NOI18N
        E_Board1_conn_detail_Pan.setPreferredSize(new java.awt.Dimension(175, 29));
        E_Board1_conn_detail_Pan.setLayout(new java.awt.BorderLayout());

        E_Board1_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board1_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        E_Board1_IP_TextField.setText("127.0.0.1");
        E_Board1_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        E_Board1_IP_TextField.setName("E_Board1_IP_TextField"); // NOI18N
        E_Board1_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        E_Board1_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board1_IP_TextFieldKeyReleased(evt);
            }
        });
        E_Board1_conn_detail_Pan.add(E_Board1_IP_TextField, java.awt.BorderLayout.WEST);

        E_Board1_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board1_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        E_Board1_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        E_Board1_Port_TextField.setName("E_Board1_Port_TextField"); // NOI18N
        E_Board1_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        E_Board1_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board1_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                E_Board1_Port_TextFieldKeyTyped(evt);
            }
        });
        E_Board1_conn_detail_Pan.add(E_Board1_Port_TextField, java.awt.BorderLayout.EAST);

        eBoardPan.add(E_Board1_conn_detail_Pan);

        gate1Panel.add(eBoardPan);

        gateBarPan.setMinimumSize(new java.awt.Dimension(426, 25));
        gateBarPan.setPreferredSize(new java.awt.Dimension(518, 25));
        gateBarPan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        gateBarPan.add(filler12);

        ebdLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ebdLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ebdLbl.setText(GATE_BAR_LABEL.getContent());
        ebdLbl.setMinimumSize(new java.awt.Dimension(60, 15));
        ebdLbl.setPreferredSize(new java.awt.Dimension(70, 15));
        gateBarPan.add(ebdLbl);

        GateBar1_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar1_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        GateBar1_TypeCBox.setToolTipText("");
        GateBar1_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        GateBar1_TypeCBox.setName("GateBar1_TypeCBox"); // NOI18N
        GateBar1_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        GateBar1_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                GateBar1_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        GateBar1_TypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GateBar1_TypeCBoxActionPerformed(evt);
            }
        });
        gateBarPan.add(GateBar1_TypeCBox);

        GateBar1_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar1_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        GateBar1_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        GateBar1_connTypeCBox.setName("GateBar1_connTypeCBox"); // NOI18N
        GateBar1_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        GateBar1_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                GateBar1_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        GateBar1_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GateBar1_connTypeCBoxActionPerformed(evt);
            }
        });
        gateBarPan.add(GateBar1_connTypeCBox);

        GateBar1_conn_detail_Pan.setMinimumSize(new java.awt.Dimension(170, 25));
        GateBar1_conn_detail_Pan.setName("GateBar1_conn_detail_Pan"); // NOI18N
        GateBar1_conn_detail_Pan.setPreferredSize(new java.awt.Dimension(175, 29));
        GateBar1_conn_detail_Pan.setLayout(new java.awt.BorderLayout());

        GateBar1_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar1_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        GateBar1_IP_TextField.setText("127.0.0.1");
        GateBar1_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        GateBar1_IP_TextField.setName("GateBar1_IP_TextField"); // NOI18N
        GateBar1_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        GateBar1_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar1_IP_TextFieldKeyReleased(evt);
            }
        });
        GateBar1_conn_detail_Pan.add(GateBar1_IP_TextField, java.awt.BorderLayout.WEST);

        GateBar1_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar1_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        GateBar1_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        GateBar1_Port_TextField.setName("GateBar1_Port_TextField"); // NOI18N
        GateBar1_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        GateBar1_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar1_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GateBar1_Port_TextFieldKeyTyped(evt);
            }
        });
        GateBar1_conn_detail_Pan.add(GateBar1_Port_TextField, java.awt.BorderLayout.EAST);

        gateBarPan.add(GateBar1_conn_detail_Pan);

        gate1Panel.add(gateBarPan);

        GatesTabbedPane.addTab(GATE_LABEL.getContent() + "1", gate1Panel);

        gate2Panel.setEnabled(false);
        gate2Panel.setMinimumSize(new java.awt.Dimension(300, 115));
        gate2Panel.setPreferredSize(new java.awt.Dimension(518, 196));
        gate2Panel.setLayout(new javax.swing.BoxLayout(gate2Panel, javax.swing.BoxLayout.Y_AXIS));

        gate_name_p1.setAlignmentX(1.0F);
        gate_name_p1.setMinimumSize(new java.awt.Dimension(109, 20));
        gate_name_p1.setPreferredSize(new java.awt.Dimension(100, 20));
        gate_name_p1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));
        gate_name_p1.add(filler16);

        gateNameLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateNameLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gateNameLabel2.setText(GATE_NAME_LABEL.getContent());
        gateNameLabel2.setToolTipText("");
        gate_name_p1.add(gateNameLabel2);

        TextFieldGateName2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldGateName2.setText("Front Gate");
        TextFieldGateName2.setToolTipText("");
        TextFieldGateName2.setMinimumSize(new java.awt.Dimension(30, 25));
        TextFieldGateName2.setName("TextFieldGateName2"); // NOI18N
        TextFieldGateName2.setPreferredSize(new java.awt.Dimension(120, 30));
        TextFieldGateName2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldGateName2KeyReleased(evt);
            }
        });
        gate_name_p1.add(TextFieldGateName2);

        gate2Panel.add(gate_name_p1);

        topLabelsPanel1.setMinimumSize(new java.awt.Dimension(266, 20));
        topLabelsPanel1.setPreferredSize(new java.awt.Dimension(100, 20));
        topLabelsPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 15));
        topLabelsPanel1.add(filler19);

        device1_Label1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        device1_Label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        device1_Label1.setText("Device");
        device1_Label1.setPreferredSize(new java.awt.Dimension(60, 15));
        topLabelsPanel1.add(device1_Label1);

        jLabel34.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Type");
        jLabel34.setPreferredSize(new java.awt.Dimension(125, 15));
        topLabelsPanel1.add(jLabel34);

        jLabel40.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Connection");
        jLabel40.setPreferredSize(new java.awt.Dimension(90, 15));
        topLabelsPanel1.add(jLabel40);

        jLabel41.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("IP address");
        jLabel41.setPreferredSize(new java.awt.Dimension(125, 15));
        topLabelsPanel1.add(jLabel41);

        jLabel43.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Port");
        jLabel43.setPreferredSize(new java.awt.Dimension(45, 15));
        topLabelsPanel1.add(jLabel43);

        gate2Panel.add(topLabelsPanel1);

        cameraPan1.setAlignmentX(0.75F);
        cameraPan1.setMinimumSize(new java.awt.Dimension(426, 25));
        cameraPan1.setPreferredSize(new java.awt.Dimension(518, 25));
        cameraPan1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        cameraPan1.add(filler20);

        jLabel44.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel44.setText(CAMERA_LABEL.getContent());
        jLabel44.setMinimumSize(new java.awt.Dimension(60, 15));
        jLabel44.setPreferredSize(new java.awt.Dimension(70, 15));
        cameraPan1.add(jLabel44);

        Camera2_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera2_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        Camera2_TypeCBox.setToolTipText("");
        Camera2_TypeCBox.setLightWeightPopupEnabled(false);
        Camera2_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        Camera2_TypeCBox.setName("Camera2_TypeCBox"); // NOI18N
        Camera2_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        Camera2_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                Camera2_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cameraPan1.add(Camera2_TypeCBox);

        Camera2_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera2_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        Camera2_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        Camera2_connTypeCBox.setName("Camera2_connTypeCBox"); // NOI18N
        Camera2_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        Camera2_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                Camera2_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        Camera2_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Camera2_connTypeCBoxActionPerformed(evt);
            }
        });
        cameraPan1.add(Camera2_connTypeCBox);

        Camera2_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera2_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Camera2_IP_TextField.setText("127.0.0.1");
        Camera2_IP_TextField.setToolTipText("");
        Camera2_IP_TextField.setMaximumSize(new java.awt.Dimension(32767, 32767));
        Camera2_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        Camera2_IP_TextField.setName("Camera2_IP_TextField"); // NOI18N
        Camera2_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        Camera2_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera2_IP_TextFieldKeyReleased(evt);
            }
        });
        cameraPan1.add(Camera2_IP_TextField);

        Camera2_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera2_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Camera2_Port_TextField.setText("8080");
        Camera2_Port_TextField.setToolTipText("");
        Camera2_Port_TextField.setMaximumSize(new java.awt.Dimension(32767, 32767));
        Camera2_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        Camera2_Port_TextField.setName("Camera2_Port_TextField"); // NOI18N
        Camera2_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        Camera2_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera2_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Camera2_Port_TextFieldKeyTyped(evt);
            }
        });
        cameraPan1.add(Camera2_Port_TextField);

        gate2Panel.add(cameraPan1);

        eBoardPan1.setMinimumSize(new java.awt.Dimension(426, 25));
        eBoardPan1.setPreferredSize(new java.awt.Dimension(518, 25));
        eBoardPan1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        eBoardPan1.add(filler21);

        jLabel45.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel45.setText(E_Board.getContent());
        jLabel45.setMinimumSize(new java.awt.Dimension(60, 15));
        jLabel45.setPreferredSize(new java.awt.Dimension(70, 15));
        eBoardPan1.add(jLabel45);

        E_Board2_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board2_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        E_Board2_TypeCBox.setToolTipText("");
        E_Board2_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        E_Board2_TypeCBox.setName("E_Board2_TypeCBox"); // NOI18N
        E_Board2_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        E_Board2_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                E_Board2_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        E_Board2_TypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                E_Board2_TypeCBoxActionPerformed(evt);
            }
        });
        eBoardPan1.add(E_Board2_TypeCBox);

        E_Board2_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board2_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        E_Board2_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        E_Board2_connTypeCBox.setName("E_Board2_connTypeCBox"); // NOI18N
        E_Board2_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        E_Board2_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                E_Board2_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        E_Board2_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                E_Board2_connTypeCBoxActionPerformed(evt);
            }
        });
        eBoardPan1.add(E_Board2_connTypeCBox);

        E_Board2_conn_detail_Pan.setMinimumSize(new java.awt.Dimension(170, 25));
        E_Board2_conn_detail_Pan.setName("E_Board2_conn_detail_Pan"); // NOI18N
        E_Board2_conn_detail_Pan.setPreferredSize(new java.awt.Dimension(175, 29));
        E_Board2_conn_detail_Pan.setLayout(new java.awt.BorderLayout());

        E_Board2_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board2_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        E_Board2_IP_TextField.setText("127.0.0.1");
        E_Board2_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        E_Board2_IP_TextField.setName("E_Board2_IP_TextField"); // NOI18N
        E_Board2_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        E_Board2_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board2_IP_TextFieldKeyReleased(evt);
            }
        });
        E_Board2_conn_detail_Pan.add(E_Board2_IP_TextField, java.awt.BorderLayout.WEST);

        E_Board2_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board2_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        E_Board2_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        E_Board2_Port_TextField.setName("E_Board2_Port_TextField"); // NOI18N
        E_Board2_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        E_Board2_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board2_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                E_Board2_Port_TextFieldKeyTyped(evt);
            }
        });
        E_Board2_conn_detail_Pan.add(E_Board2_Port_TextField, java.awt.BorderLayout.EAST);

        eBoardPan1.add(E_Board2_conn_detail_Pan);

        gate2Panel.add(eBoardPan1);

        gateBarPan1.setMinimumSize(new java.awt.Dimension(426, 25));
        gateBarPan1.setPreferredSize(new java.awt.Dimension(518, 25));
        gateBarPan1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        gateBarPan1.add(filler22);

        ebdLbl1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ebdLbl1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ebdLbl1.setText(GateBar.getContent());
        ebdLbl1.setMinimumSize(new java.awt.Dimension(60, 15));
        ebdLbl1.setPreferredSize(new java.awt.Dimension(70, 15));
        gateBarPan1.add(ebdLbl1);

        GateBar2_TypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar2_TypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "list e-board types" }));
        GateBar2_TypeCBox.setToolTipText("");
        GateBar2_TypeCBox.setMinimumSize(new java.awt.Dimension(115, 25));
        GateBar2_TypeCBox.setName("GateBar2_TypeCBox"); // NOI18N
        GateBar2_TypeCBox.setPreferredSize(new java.awt.Dimension(115, 27));
        GateBar2_TypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                GateBar2_TypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        GateBar2_TypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GateBar2_TypeCBoxActionPerformed(evt);
            }
        });
        gateBarPan1.add(GateBar2_TypeCBox);

        GateBar2_connTypeCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar2_connTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP/IP", "RS-232" }));
        GateBar2_connTypeCBox.setMinimumSize(new java.awt.Dimension(80, 23));
        GateBar2_connTypeCBox.setName("GateBar2_connTypeCBox"); // NOI18N
        GateBar2_connTypeCBox.setPreferredSize(new java.awt.Dimension(90, 27));
        GateBar2_connTypeCBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                GateBar2_connTypeCBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        GateBar2_connTypeCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GateBar2_connTypeCBoxActionPerformed(evt);
            }
        });
        gateBarPan1.add(GateBar2_connTypeCBox);

        GateBar2_conn_detail_Pan.setMinimumSize(new java.awt.Dimension(170, 25));
        GateBar2_conn_detail_Pan.setName("GateBar2_conn_detail_Pan"); // NOI18N
        GateBar2_conn_detail_Pan.setPreferredSize(new java.awt.Dimension(175, 29));
        GateBar2_conn_detail_Pan.setLayout(new java.awt.BorderLayout());

        GateBar2_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar2_IP_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        GateBar2_IP_TextField.setText("127.0.0.1");
        GateBar2_IP_TextField.setMinimumSize(new java.awt.Dimension(125, 25));
        GateBar2_IP_TextField.setName("GateBar2_IP_TextField"); // NOI18N
        GateBar2_IP_TextField.setPreferredSize(new java.awt.Dimension(125, 27));
        GateBar2_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar2_IP_TextFieldKeyReleased(evt);
            }
        });
        GateBar2_conn_detail_Pan.add(GateBar2_IP_TextField, java.awt.BorderLayout.WEST);

        GateBar2_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar2_Port_TextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        GateBar2_Port_TextField.setMinimumSize(new java.awt.Dimension(40, 25));
        GateBar2_Port_TextField.setName("GateBar2_Port_TextField"); // NOI18N
        GateBar2_Port_TextField.setPreferredSize(new java.awt.Dimension(45, 27));
        GateBar2_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar2_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GateBar2_Port_TextFieldKeyTyped(evt);
            }
        });
        GateBar2_conn_detail_Pan.add(GateBar2_Port_TextField, java.awt.BorderLayout.EAST);

        gateBarPan1.add(GateBar2_conn_detail_Pan);

        gate2Panel.add(gateBarPan1);

        GatesTabbedPane.addTab(GATE_LABEL.getContent() + "2", gate2Panel);

        gate3Panel.setEnabled(false);
        gate3Panel.setMinimumSize(new java.awt.Dimension(300, 115));
        gate3Panel.setPreferredSize(new java.awt.Dimension(518, 196));
        gate3Panel.setLayout(new java.awt.GridBagLayout());

        jLabel16.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Gate Name");
        jLabel16.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(jLabel16, gridBagConstraints);

        TextFieldGateName3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldGateName3.setText("3rd Gate");
        TextFieldGateName3.setName("TextFieldGateName3"); // NOI18N
        TextFieldGateName3.setPreferredSize(new java.awt.Dimension(30, 23));
        TextFieldGateName3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldGateName3KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 105;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(TextFieldGateName3, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Camera IP Address");
        jLabel10.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(jLabel10, gridBagConstraints);

        Camera3_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera3_IP_TextField.setText("127.0.0.1");
        Camera3_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        Camera3_IP_TextField.setName("Camera3_IP_TextField"); // NOI18N
        Camera3_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        Camera3_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera3_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(Camera3_IP_TextField, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("GateBar IP Address");
        jLabel23.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(jLabel23, gridBagConstraints);

        GateBar3_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar3_IP_TextField.setText("127.0.0.1");
        GateBar3_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        GateBar3_IP_TextField.setName("GateBar3_IP_TextField"); // NOI18N
        GateBar3_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        GateBar3_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar3_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(GateBar3_IP_TextField, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("E-Board IP Address");
        jLabel24.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate3Panel.add(jLabel24, gridBagConstraints);

        E_Board3_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board3_IP_TextField.setText("127.0.0.1");
        E_Board3_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        E_Board3_IP_TextField.setName("E_Board3_IP_TextField"); // NOI18N
        E_Board3_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        E_Board3_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board3_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate3Panel.add(E_Board3_IP_TextField, gridBagConstraints);

        jLabel38.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel38.setText("Port No");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(jLabel38, gridBagConstraints);

        Camera3_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera3_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        Camera3_Port_TextField.setName("Camera3_Port_TextField"); // NOI18N
        Camera3_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        Camera3_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera3_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Camera3_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(Camera3_Port_TextField, gridBagConstraints);

        GateBar3_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar3_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        GateBar3_Port_TextField.setName("GateBar3_Port_TextField"); // NOI18N
        GateBar3_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        GateBar3_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar3_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GateBar3_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate3Panel.add(GateBar3_Port_TextField, gridBagConstraints);

        E_Board3_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board3_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        E_Board3_Port_TextField.setName("E_Board3_Port_TextField"); // NOI18N
        E_Board3_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        E_Board3_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board3_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                E_Board3_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate3Panel.add(E_Board3_Port_TextField, gridBagConstraints);

        GatesTabbedPane.addTab(GATE_LABEL.getContent() + "3", gate3Panel);

        gate4Panel.setEnabled(false);
        gate4Panel.setMinimumSize(new java.awt.Dimension(300, 115));
        gate4Panel.setPreferredSize(new java.awt.Dimension(518, 196));
        gate4Panel.setLayout(new java.awt.GridBagLayout());

        jLabel17.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Gate Name");
        jLabel17.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        gate4Panel.add(jLabel17, gridBagConstraints);

        TextFieldGateName4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        TextFieldGateName4.setText("4th Gate");
        TextFieldGateName4.setToolTipText("");
        TextFieldGateName4.setMinimumSize(new java.awt.Dimension(70, 21));
        TextFieldGateName4.setName("TextFieldGateName4"); // NOI18N
        TextFieldGateName4.setPreferredSize(new java.awt.Dimension(90, 23));
        TextFieldGateName4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextFieldGateName4KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        gate4Panel.add(TextFieldGateName4, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Camera IP Address");
        jLabel18.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(jLabel18, gridBagConstraints);

        Camera4_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera4_IP_TextField.setText("127.0.0.1");
        Camera4_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        Camera4_IP_TextField.setName("Camera4_IP_TextField"); // NOI18N
        Camera4_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        Camera4_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera4_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(Camera4_IP_TextField, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("GateBar IP Address");
        jLabel25.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(jLabel25, gridBagConstraints);

        GateBar4_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar4_IP_TextField.setText("127.0.0.1");
        GateBar4_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        GateBar4_IP_TextField.setName("GateBar4_IP_TextField"); // NOI18N
        GateBar4_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        GateBar4_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar4_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(GateBar4_IP_TextField, gridBagConstraints);

        E_Board4_IP_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board4_IP_TextField.setText("127.0.0.1");
        E_Board4_IP_TextField.setMinimumSize(new java.awt.Dimension(120, 27));
        E_Board4_IP_TextField.setName("E_Board4_IP_TextField"); // NOI18N
        E_Board4_IP_TextField.setPreferredSize(new java.awt.Dimension(90, 23));
        E_Board4_IP_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                E_Board4_IP_TextFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate4Panel.add(E_Board4_IP_TextField, gridBagConstraints);

        Camera4_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        Camera4_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        Camera4_Port_TextField.setName("Camera4_Port_TextField"); // NOI18N
        Camera4_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        Camera4_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Camera4_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Camera4_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(Camera4_Port_TextField, gridBagConstraints);

        GateBar4_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        GateBar4_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        GateBar4_Port_TextField.setName("GateBar4_Port_TextField"); // NOI18N
        GateBar4_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        GateBar4_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GateBar4_Port_TextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GateBar4_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(GateBar4_Port_TextField, gridBagConstraints);

        E_Board4_Port_TextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        E_Board4_Port_TextField.setMinimumSize(new java.awt.Dimension(50, 27));
        E_Board4_Port_TextField.setName("E_Board4_Port_TextField"); // NOI18N
        E_Board4_Port_TextField.setPreferredSize(new java.awt.Dimension(50, 27));
        E_Board4_Port_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                E_Board4_Port_TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate4Panel.add(E_Board4_Port_TextField, gridBagConstraints);

        jLabel35.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel35.setText("Port No");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(jLabel35, gridBagConstraints);

        Camera4_Port_TextField1.setMinimumSize(new java.awt.Dimension(40, 27));
        Camera4_Port_TextField1.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(Camera4_Port_TextField1, gridBagConstraints);

        GateBar4_Port_TextField1.setMinimumSize(new java.awt.Dimension(40, 27));
        GateBar4_Port_TextField1.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        gate4Panel.add(GateBar4_Port_TextField1, gridBagConstraints);

        E_Board4_Port_TextField1.setMinimumSize(new java.awt.Dimension(40, 27));
        E_Board4_Port_TextField1.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate4Panel.add(E_Board4_Port_TextField1, gridBagConstraints);

        jLabel39.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("E-Board IP Address");
        jLabel39.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 10);
        gate4Panel.add(jLabel39, gridBagConstraints);

        GatesTabbedPane.addTab(GATE_LABEL.getContent() + "4", gate4Panel);

        eBoardSettingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        eBoardSettingPanel.setMinimumSize(new java.awt.Dimension(180, 215));
        eBoardSettingPanel.setPreferredSize(new java.awt.Dimension(188, 250));

        E_BoardSettingsButtonPanel.setLayout(new javax.swing.BoxLayout(E_BoardSettingsButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel20.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText(ELECTRONIC_DISPLAY_LABEL.getContent());
        jLabel20.setToolTipText("");
        jLabel20.setMaximumSize(new java.awt.Dimension(300, 27));
        jLabel20.setPreferredSize(new java.awt.Dimension(150, 27));

        javax.swing.GroupLayout EBD_settings_labelLayout = new javax.swing.GroupLayout(EBD_settings_label);
        EBD_settings_label.setLayout(EBD_settings_labelLayout);
        EBD_settings_labelLayout.setHorizontalGroup(
            EBD_settings_labelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EBD_settings_labelLayout.createSequentialGroup()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        EBD_settings_labelLayout.setVerticalGroup(
            EBD_settings_labelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EBD_settings_labelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        E_BoardSettingsButtonPanel.add(EBD_settings_label);

        EBoardSettingsButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EBoardSettingsButton.setText("Settings");
        EBoardSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EBoardSettingsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EBD_settingsLayout = new javax.swing.GroupLayout(EBD_settings);
        EBD_settings.setLayout(EBD_settingsLayout);
        EBD_settingsLayout.setHorizontalGroup(
            EBD_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EBD_settingsLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(EBoardSettingsButton))
        );
        EBD_settingsLayout.setVerticalGroup(
            EBD_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EBD_settingsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(EBoardSettingsButton))
        );

        E_BoardSettingsButtonPanel.add(EBD_settings);

        allCyclesPanel.setLayout(new javax.swing.BoxLayout(allCyclesPanel, javax.swing.BoxLayout.Y_AXIS));

        cycleLabel.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText(CYCLE_LABEL.getContent());
        jLabel8.setMaximumSize(new java.awt.Dimension(80, 15));
        jLabel8.setMinimumSize(new java.awt.Dimension(80, 15));
        jLabel8.setPreferredSize(new java.awt.Dimension(150, 15));

        javax.swing.GroupLayout cycleLabelLayout = new javax.swing.GroupLayout(cycleLabel);
        cycleLabel.setLayout(cycleLabelLayout);
        cycleLabelLayout.setHorizontalGroup(
            cycleLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cycleLabelLayout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        cycleLabelLayout.setVerticalGroup(
            cycleLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cycleLabelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        allCyclesPanel.add(cycleLabel);

        twoCycles.setLayout(new javax.swing.BoxLayout(twoCycles, javax.swing.BoxLayout.LINE_AXIS));
        twoCycles.add(filler13);

        real2Pan.setLayout(new javax.swing.BoxLayout(real2Pan, javax.swing.BoxLayout.Y_AXIS));

        flowPanel.setLayout(new javax.swing.BoxLayout(flowPanel, javax.swing.BoxLayout.Y_AXIS));

        flowing.setLayout(new java.awt.BorderLayout());
        flowing.add(filler14, java.awt.BorderLayout.WEST);

        jLabel31.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText(FLOWING_LABEL.getContent());
        jLabel31.setToolTipText("");
        jLabel31.setMaximumSize(new java.awt.Dimension(80, 15));
        jLabel31.setPreferredSize(new java.awt.Dimension(50, 15));
        flowing.add(jLabel31, java.awt.BorderLayout.CENTER);

        flowPanel.add(flowing);

        cBoxPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        cBoxPanel.add(filler18);

        FlowingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        FlowingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "4,000", "6,000", "8,000", "10,000", "12,000" }));
        FlowingComboBox.setMinimumSize(new java.awt.Dimension(65, 25));
        FlowingComboBox.setName("FlowingComboBox"); // NOI18N
        FlowingComboBox.setPreferredSize(new java.awt.Dimension(80, 27));
        FlowingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                FlowingComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cBoxPanel.add(FlowingComboBox);

        jLabel33.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("ms");
        jLabel33.setToolTipText("");
        cBoxPanel.add(jLabel33);

        flowPanel.add(cBoxPanel);

        real2Pan.add(flowPanel);

        blinkPanel.setLayout(new javax.swing.BoxLayout(blinkPanel, javax.swing.BoxLayout.Y_AXIS));

        blinkingP.setLayout(new java.awt.BorderLayout());
        blinkingP.add(filler15, java.awt.BorderLayout.WEST);

        blinkingL.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        blinkingL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        blinkingL.setText(BLINGKING_LABEL.getContent());
        blinkingL.setToolTipText("");
        blinkingL.setMaximumSize(new java.awt.Dimension(80, 15));
        blinkingL.setPreferredSize(new java.awt.Dimension(50, 15));
        blinkingP.add(blinkingL, java.awt.BorderLayout.CENTER);

        blinkPanel.add(blinkingP);

        cBoxPan.setPreferredSize(new java.awt.Dimension(133, 33));
        cBoxPan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        cBoxPan.add(filler17);

        BlinkingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BlinkingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "500", "750", "1,000", "1,250", "1,500" }));
        BlinkingComboBox.setMinimumSize(new java.awt.Dimension(65, 25));
        BlinkingComboBox.setName("BlinkingComboBox"); // NOI18N
        BlinkingComboBox.setPreferredSize(new java.awt.Dimension(80, 27));
        BlinkingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                BlinkingComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cBoxPan.add(BlinkingComboBox);

        jLabel32.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("ms");
        jLabel32.setToolTipText("");
        cBoxPan.add(jLabel32);

        blinkPanel.add(cBoxPan);

        real2Pan.add(blinkPanel);

        twoCycles.add(real2Pan);

        allCyclesPanel.add(twoCycles);

        javax.swing.GroupLayout eBoardSettingPanelLayout = new javax.swing.GroupLayout(eBoardSettingPanel);
        eBoardSettingPanel.setLayout(eBoardSettingPanelLayout);
        eBoardSettingPanelLayout.setHorizontalGroup(
            eBoardSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eBoardSettingPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(eBoardSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(allCyclesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(eBoardSettingPanelLayout.createSequentialGroup()
                        .addComponent(E_BoardSettingsButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        eBoardSettingPanelLayout.setVerticalGroup(
            eBoardSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eBoardSettingPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(E_BoardSettingsButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allCyclesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout gateSettingPanelLayout = new javax.swing.GroupLayout(gateSettingPanel);
        gateSettingPanel.setLayout(gateSettingPanelLayout);
        gateSettingPanelLayout.setHorizontalGroup(
            gateSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gateSettingPanelLayout.createSequentialGroup()
                .addComponent(GatesTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(eBoardSettingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );
        gateSettingPanelLayout.setVerticalGroup(
            gateSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gateSettingPanelLayout.createSequentialGroup()
                .addGroup(gateSettingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(gateSettingPanelLayout.createSequentialGroup()
                        .addComponent(GatesTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(eBoardSettingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        GatesTabbedPane.getAccessibleContext().setAccessibleName("\"GATE_LABEL.getContent() + \\\"2\\\"\"");

        wholePanel.add(gateSettingPanel);

        bottomPanel.setMinimumSize(new java.awt.Dimension(275, 50));
        bottomPanel.setPreferredSize(new java.awt.Dimension(700, 50));

        SettingsSaveButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        SettingsSaveButton.setMnemonic('s');
        SettingsSaveButton.setText(SAVE_BTN.getContent());
        SettingsSaveButton.setToolTipText(SETTINGS_SAVE_TOOLTIP.getContent());
        SettingsSaveButton.setEnabled(false);
        SettingsSaveButton.setPreferredSize(new java.awt.Dimension(90, 40));
        SettingsSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsSaveButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(SettingsSaveButton);
        bottomPanel.add(filler5);

        SettingsCancelButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        SettingsCancelButton.setMnemonic('c');
        SettingsCancelButton.setText(CANCEL_BTN.getContent());
        SettingsCancelButton.setToolTipText(SETTINGS_CANCEL_TOOLTIP.getContent());
        SettingsCancelButton.setEnabled(false);
        SettingsCancelButton.setPreferredSize(new java.awt.Dimension(90, 40));
        SettingsCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsCancelButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(SettingsCancelButton);
        bottomPanel.add(filler8);

        SettingsCloseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        SettingsCloseButton.setMnemonic('c');
        SettingsCloseButton.setText(CLOSE_BTN.getContent());
        SettingsCloseButton.setToolTipText(CLOSE_BTN_TOOLTIP.getContent());
        SettingsCloseButton.setPreferredSize(new java.awt.Dimension(90, 40));
        SettingsCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsCloseButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(SettingsCloseButton);

        wholePanel.add(bottomPanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(filler2, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(filler4, java.awt.BorderLayout.LINE_START);
        getContentPane().add(filler6, java.awt.BorderLayout.LINE_END);
        getContentPane().add(filler3, java.awt.BorderLayout.PAGE_END);

        setSize(new java.awt.Dimension(786, 781));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void StatPopSizeTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatPopSizeTextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_StatPopSizeTextFieldKeyTyped

    private void LanguageSelectionlHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LanguageSelectionlHelpButtonActionPerformed
        // TODO add your handling code here:
        String helpText = "Date Input Panel GUI language selection .";

        JDialog helpDialog = new PWHelpJDialog(this, false,
            "Language Usage", helpText);
        locateAndShowHelpDialog(this, helpDialog, LoggingLevelHelpButton);
    }//GEN-LAST:event_LanguageSelectionlHelpButtonActionPerformed

    private void LoggingLevelHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoggingLevelHelpButtonActionPerformed

        String helpText = null;
        if (OptnLoggingLevelComboBox.getSelectedIndex() == OpLogLevel.LogAlways.ordinal()) {
            helpText = "At this level (No Logging)," + System.lineSeparator() 
                    + " Following 'Mandatory' items are logged :" + System.lineSeparator() 
                    + System.lineSeparator() 
                    + " - System start and stop time" + System.lineSeparator() 
                    + " - Number of deleted old records"+ System.lineSeparator() 
                    + " - Number of deleted old images"+ System.lineSeparator() 
                    + " - Number of deleted log folders and text files"+ System.lineSeparator() 
                    + " - File path of deleted log folders and text files"+ System.lineSeparator();
        } else
        if (OptnLoggingLevelComboBox.getSelectedIndex() == OpLogLevel.SettingsChange.ordinal()) {
            helpText = "'System Settings' Level Logged Items:" + System.lineSeparator() 
                    + System.lineSeparator() 
                    + " - Mandatory Items" + System.lineSeparator() 
                    + "   plus" + System.lineSeparator()
                    + " - System Settings Change" + System.lineSeparator() 
                    + " - Attendant/User Info Change" + System.lineSeparator() 
                    + " - Drivers Info Change" + System.lineSeparator() 
                    + " - Vehicles Info Change" + System.lineSeparator();
        } else
        if (OptnLoggingLevelComboBox.getSelectedIndex() == OpLogLevel.EBDsettingsChange.ordinal()) {
            helpText = "'E-Board Settings' Level Logged Items: " + System.lineSeparator() 
                    + System.lineSeparator() 
                    + " - System Settings Logged Items" + System.lineSeparator() 
                    + "   plus" + System.lineSeparator()
                    + " - E-Board Settings Change" + System.lineSeparator();
        }

        JDialog helpDialog = new PWHelpJDialog(this, false,
            "What is being LOGGED?", helpText);
        locateAndShowHelpDialog(this, helpDialog, LoggingLevelHelpButton);
    }//GEN-LAST:event_LoggingLevelHelpButtonActionPerformed

    private void PWHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PWHelpButtonActionPerformed
        PasswordValidator pwValidator = new PasswordValidator();
        short pwPowerLevel = (short)PWStrengthChoiceComboBox.getSelectedIndex();
        String helpText = pwValidator.getWrongPWFormatMsg(pwPowerLevel);

        JDialog helpDialog = new PWHelpJDialog(this, false, "Password Requirements", helpText);
        Point buttonPoint = new Point();
        PWHelpButton.getLocation(buttonPoint);

        Point framePoint = new Point(); 
        this.getLocation(framePoint);
        Point topLeft = new Point(framePoint.x + buttonPoint.x + 60, framePoint.y + buttonPoint.y - 60);
        helpDialog.setLocation(topLeft);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_PWHelpButtonActionPerformed

    private JFrame eBDsettings = null;
    
    private void SettingsSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsSaveButtonActionPerformed
        Connection conn = null;
        PreparedStatement updateSettings = null;
        int result = -1;
        boolean newStorePassingDelay = RecordPassingDelayCBox.isSelected();

        //<editor-fold desc="--check setting input errors">
        if (newStorePassingDelay) {
            if (!TextFieldNumericValueOK(StatPopSizeTextField, "Statistics Cycle Input Error")) {
                return;
            }
        }
        
        if (!TextFieldNumericValueOK(TextFieldPicWidth, "Photo Extent Typing Errors")) {
            return;
        }

        if (!TextFieldNumericValueOK(TextFieldPicHeight, "Photo Height Typing Errors")) {
            return;
        }

        if(Integer.parseInt(TextFieldPicHeight.getText().trim().replace(",","")) < 100){
            TextFieldPicHeight.requestFocusInWindow();
            JOptionPane.showConfirmDialog(this, "Please enter a height value of 100 or more",
                    "Picture Height Input Error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            return;
        }
        
        if(Integer.parseInt(TextFieldPicWidth.getText().trim().replace(",","")) < 100){
            TextFieldPicWidth.requestFocusInWindow();
            JOptionPane.showConfirmDialog(this, "Please enter a width value of 100 or more",
                    "Picture Width Input Error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            return;
        }
        
        if (someIPaddressWrong()) {
            return;
        }
        
        if (someCOMportIDsame()) {
            return;
        }
        //</editor-fold>

        String statCountStr = "";
        short pwLevel = -1;
        short optnLogLevel = -1;
        String maxLineStr = "";
        int imageKeepDuration = 0;
        int picWidth = Integer.parseInt(((String) TextFieldPicWidth.getText()).replace(",", ""));
        int picHeight = Integer.parseInt(((String) TextFieldPicHeight.getText()).replace(",", ""));
        int flowCycle = Integer.parseInt(((String) FlowingComboBox.getSelectedItem()).replace(",", ""));
        int blinkCycle = Integer.parseInt(((String) BlinkingComboBox.getSelectedItem()).replace(",", ""));
        try {
            StringBuffer sb = new StringBuffer("Update SettingsTable SET ");
            //<editor-fold desc="--create update statement">
            sb.append("Lot_Name = ?, ");
            sb.append("perfEvalNeeded = ?, PWStrengthLevel = ?, OptnLoggingLevel = ?, ");
            sb.append("languageCode = ?, countryCode = ?, localeIndex = ?, statCount =  ?, ");
            sb.append("MaxMessageLines = ?, GateCount = ?, ");
            sb.append("PictureWidth = ?, PictureHeight = ?, ");
            sb.append("EBD_flow_cycle = ?, EBD_blink_cycle = ?, ");
            sb.append("max_maintain_date = ? ");
            //</editor-fold>

            statCountStr = (StatPopSizeTextField.getText().trim());
            if (newStorePassingDelay) {
                for (int gateID = 1; gateID <= gateCount; gateID++) {
                    initPassingDelayStatIfNeeded(Integer.parseInt(statCountStr), gateID);
                }
            }
            conn = JDBCMySQL.getConnection();
            updateSettings = conn.prepareStatement (sb.toString());

            int pIndex = 1;

            // <editor-fold defaultstate="collapsed" desc="--Provide values to each parameters of the UPDATE statement">
            updateSettings.setString(pIndex++, lotNameTextField.getText().trim());
            if (newStorePassingDelay) {
                updateSettings.setInt(pIndex++, 1);
            } else {
                updateSettings.setInt(pIndex++, 0);
                if (DEBUG) {
                    // Give warning that in debug mode PassingDelay is always recorded.
                    JOptionPane.showMessageDialog(null, "In debug mode, Passing Delay is alway recorded");
                }
            }

            pwLevel = (short)(PWStrengthChoiceComboBox.getSelectedIndex());
            updateSettings.setShort(pIndex++, pwLevel);

            optnLogLevel = (short)(OptnLoggingLevelComboBox.getSelectedIndex());
            updateSettings.setShort(pIndex++, optnLogLevel);

            updateSettings.setString(pIndex++, DateChooserLangCBox.getLocale().getLanguage());
            updateSettings.setString(pIndex++, DateChooserLangCBox.getLocale().getCountry());
            updateSettings.setShort(pIndex++, (short)DateChooserLangCBox.getSelectedIndex());

            if (statCountStr.length() == 0) {
                // Handle the case of wrong value (zero) input.
                statCountStr = "100";
            }
            updateSettings.setInt(pIndex++, Integer.parseInt(statCountStr));
            
            maxLineStr = (String)MessageMaxLineComboBox.getSelectedItem();
            updateSettings.setShort(pIndex++, new Short(maxLineStr));
            updateSettings.setShort(pIndex++, new Short((String)GateCountComboBox.getSelectedItem()));

            updateSettings.setInt(pIndex++, picWidth);
            updateSettings.setInt(pIndex++, picHeight);
            updateSettings.setInt(pIndex++, flowCycle);
            updateSettings.setInt(pIndex++, blinkCycle);

            ConvComboBoxItem item = (ConvComboBoxItem)ImageDurationCBox.getSelectedItem();
            imageKeepDuration = (Integer)(item.getValue());
            updateSettings.setInt(pIndex++, imageKeepDuration);
            // </editor-fold>

            result = updateSettings.executeUpdate();
            if (index2Level(opLoggingIndex) != Level.OFF && index2Level(optnLogLevel) == Level.OFF) {
                Globals.isFinalWishLog = true;
            }
        } catch (SQLException se) {
            Globals.logParkingException(Level.SEVERE, se,
                    "(Save settings: " + (newStorePassingDelay ? "Y" : "N") + ")");
        } finally {
            // <editor-fold defaultstate="collapsed" desc="--Return resources and display the save result">
            closeDBstuff(conn, updateSettings, null, "(Save settings: " + (newStorePassingDelay ? "Y" : "N") + ")");

            if (result == 1) {
                //<editor-fold desc="-- Log system settings change if set to do so">
                if (statCount != Integer.parseInt(statCountStr)) 
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Statistics Population Size: " 
                            + statCount + " => " + Integer.parseInt(statCountStr));
                }
               
                if (storePassingDelay != newStorePassingDelay)
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Average Passing Delay: " 
                            + storePassingDelay + " => " + newStorePassingDelay);
                }
                
                if (pwStrengthLevel != pwLevel)
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Password Strength Level: " 
                            + PWStrengthChoiceComboBox.getItemAt(pwStrengthLevel) + " => " 
                            + PWStrengthChoiceComboBox.getItemAt(pwLevel));
                }
                
                if (opLoggingIndex != optnLogLevel)
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Gen' Operation Log Level: " 
                            + OptnLoggingLevelComboBox.getItemAt(opLoggingIndex) + " => " 
                            + OptnLoggingLevelComboBox.getItemAt(optnLogLevel));
                }
                
                if (localeIndex != (short)DateChooserLangCBox.getSelectedIndex())
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Date Chooser Lang': " 
                            + DateChooserLangCBox.getItemAt(localeIndex) + " => " 
                            + DateChooserLangCBox.getItemAt((short)DateChooserLangCBox.getSelectedIndex()));
                }
                
                if (maxMessageLines != new Short(maxLineStr))
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Recent Event Line Max: " 
                            + maxMessageLines + " => " + new Short(maxLineStr));
                }
                
                short newGateCount = new Short((String)GateCountComboBox.getSelectedItem());
                
                boolean gateCountChanged = gateCount != newGateCount;
                if (gateCountChanged)
                {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Number of Gates: " 
                            + gateCount + " => " + newGateCount);
                }
                
                if (maxMaintainDate != imageKeepDuration) {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Image Keep Duration: " 
                            + maxMaintainDate + " => " + imageKeepDuration);
                }
                
                if (PIC_WIDTH != picWidth) {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Image width: " 
                            + PIC_WIDTH + " => " + picWidth);
                }
                
                if (PIC_HEIGHT != picHeight) {
                    logParkingOperation(OpLogLevel.SettingsChange, "Settings Change, Image Height: " 
                            + PIC_HEIGHT + " => " + picHeight);
                }
                
                if (EBD_flowCycle != flowCycle)
                {
                    logParkingOperation(OpLogLevel.EBDsettingsChange, "E-Board Settings Change, Cycles--flowing: " 
                            + EBD_flowCycle + " => " + flowCycle);
                }                
                
                if (EBD_blinkCycle != blinkCycle)
                {
                    logParkingOperation(OpLogLevel.EBDsettingsChange, "E-Board Settings Change, Cycles--blinking: " 
                            + EBD_blinkCycle + " => " + blinkCycle);
                }    
                
                if (mainForm != null && gateCountChanged)
                {
                    JOptionPane.showMessageDialog(mainForm, REBOOT_MESSAGE.getContent(), 
                            REBOOT_POPUP.getContent(), WARNING_MESSAGE, 
                            new javax.swing.ImageIcon(mainForm.getClass().getResource("/restart.png")));
                    mainForm.askUserIntentionOnProgramStop(true);
                }                
                //</editor-fold>
                
                Globals.getOperationLog().setLevel(index2Level(opLoggingIndex));
            } else {
                JOptionPane.showMessageDialog(this, "The storage system settings failed.",
                    "Save Settings Results", JOptionPane.ERROR_MESSAGE);
            }
            // </editor-fold>
        }
        
        result += saveGateDevices();
        
        if (result == gateCount + 1) {
            readSettings();
            Globals.getOperationLog().setLevel(index2Level(opLoggingIndex));
            JOptionPane.showMessageDialog(this, "The system settings have been saved successfully.",
                "Save Settings Results", JOptionPane.PLAIN_MESSAGE);
            changedControls.clear();
            changeEnabled_of_SaveCancelButtons(false);
            ChangeSettings.changeEnabled_of_SaveCancelButtons(SettingsSaveButton, SettingsCancelButton, 
                    SettingsCloseButton, changedControls.size());            
        } else {
            JOptionPane.showMessageDialog(this, "The storage system settings failed.",
                "Save Settings Results", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_SettingsSaveButtonActionPerformed

    private void SettingsCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsCancelButtonActionPerformed
        loadComponentValues();
    }//GEN-LAST:event_SettingsCancelButtonActionPerformed

    private void SettingsCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsCloseButtonActionPerformed
        tryToCloseSettingsForm();
    }//GEN-LAST:event_SettingsCloseButtonActionPerformed

    private void TextFieldPicHeightKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldPicHeightKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_TextFieldPicHeightKeyTyped

    private void TextFieldPicWidthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldPicWidthKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_TextFieldPicWidthKeyTyped

    private void finishSettingsForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_finishSettingsForm
        tryToCloseSettingsForm();
    }//GEN-LAST:event_finishSettingsForm

    private void RecordPassingDelayCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecordPassingDelayCBoxActionPerformed
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), RecordPassingDelayCBox.isSelected(), storePassingDelay);
    }//GEN-LAST:event_RecordPassingDelayCBoxActionPerformed

    private void ImageDurationCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_ImageDurationCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), ImageDurationCBox.getSelectedIndex(), maxArrivalCBoxIndex);
        
    }//GEN-LAST:event_ImageDurationCBoxPopupMenuWillBecomeInvisible

    private void PWStrengthChoiceComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_PWStrengthChoiceComboBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), PWStrengthChoiceComboBox.getSelectedIndex(), pwStrengthLevel);
    }//GEN-LAST:event_PWStrengthChoiceComboBoxPopupMenuWillBecomeInvisible

    private void MessageMaxLineComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_MessageMaxLineComboBoxPopupMenuWillBecomeInvisible
        short lines = (short) Integer.parseInt((String)MessageMaxLineComboBox.getSelectedItem());
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), lines, maxMessageLines);        
    }//GEN-LAST:event_MessageMaxLineComboBoxPopupMenuWillBecomeInvisible

    private void OptnLoggingLevelComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_OptnLoggingLevelComboBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component)evt.getSource(),
                OptnLoggingLevelComboBox.getSelectedIndex(), opLoggingIndex);        
    }//GEN-LAST:event_OptnLoggingLevelComboBoxPopupMenuWillBecomeInvisible

    private void DateChooserLangCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_DateChooserLangCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), DateChooserLangCBox.getSelectedIndex(), localeIndex);        
    }//GEN-LAST:event_DateChooserLangCBoxPopupMenuWillBecomeInvisible

    private void GateCountComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_GateCountComboBoxPopupMenuWillBecomeInvisible
        String gateCountStr = (String)GateCountComboBox.getSelectedItem();
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(gateCountStr), gateCount);        
    }//GEN-LAST:event_GateCountComboBoxPopupMenuWillBecomeInvisible

    private void StatPopSizeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatPopSizeTextFieldKeyReleased
        String newStatPopStr = StatPopSizeTextField.getText().trim();
        if(newStatPopStr.length() == 0){
            newStatPopStr = "0";
         }
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(newStatPopStr), statCount);
    }//GEN-LAST:event_StatPopSizeTextFieldKeyReleased

    private void TextFieldPicWidthKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldPicWidthKeyReleased
        String newPicWidthStr = ((String) TextFieldPicWidth.getText()).replace(",", "");
        if(newPicWidthStr.length() == 0){
            newPicWidthStr = "0";
         }
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(newPicWidthStr), PIC_WIDTH);
    }//GEN-LAST:event_TextFieldPicWidthKeyReleased

    private void TextFieldPicHeightKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldPicHeightKeyReleased
        String newPicHeightStr = ((String) TextFieldPicHeight.getText()).replace(",", "");
        if(newPicHeightStr.length() == 0){
            newPicHeightStr = "0";
         }
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(newPicHeightStr), PIC_HEIGHT);
    }//GEN-LAST:event_TextFieldPicHeightKeyReleased

    LEDnoticeManager managerLEDnotice = null;    
    
    private void lotNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lotNameTextFieldKeyReleased
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), lotNameTextField.getText().trim(), parkingLotName);
    }//GEN-LAST:event_lotNameTextFieldKeyReleased

    private void BlinkingComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_BlinkingComboBoxPopupMenuWillBecomeInvisible
        String newBlinkCycleStr = ((String)BlinkingComboBox.getSelectedItem()).replace(",", "");
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(newBlinkCycleStr), EBD_blinkCycle);        
    }//GEN-LAST:event_BlinkingComboBoxPopupMenuWillBecomeInvisible

    private void FlowingComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_FlowingComboBoxPopupMenuWillBecomeInvisible
        String newFlowCycleStr =((String) FlowingComboBox.getSelectedItem()).replace(",", "");
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, (Component) evt.getSource(), Integer.parseInt(newFlowCycleStr), EBD_flowCycle);
    }//GEN-LAST:event_FlowingComboBoxPopupMenuWillBecomeInvisible

    private void EBoardSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EBoardSettingsButtonActionPerformed

        int tabIndex = GatesTabbedPane.getSelectedIndex();
        JComboBox typeCBox = (JComboBox)componentMap.get("E_Board" + (tabIndex + 1) + "_TypeCBox");

        if (eBDsettings == null) {
            if (typeCBox.getSelectedIndex() == E_BoardType.Simulator.ordinal()) {
                setEBDsettings(new Settings_EBoard(mainForm, this));
            } else if (typeCBox.getSelectedIndex() == E_BoardType.LEDnotice.ordinal()) {
                setEBDsettings(new Settings_LEDnotice(mainForm, this, null, tabIndex + 1));
            }
        }

        Point panelPoint = new Point();
        EBoardSettingsButton.getLocation(panelPoint);

        Point buttonPoint = new Point();
        eBoardSettingPanel.getLocation(buttonPoint);

        Point framePoint = new Point();
        this.getLocation(framePoint);
        Point topLeft = new Point(framePoint.x + buttonPoint.x  + panelPoint.x+ EBoardSettingsButton.getSize().width + 130,
            framePoint.y + buttonPoint.y + eBDsettings.getSize().height + 120);
        eBDsettings.setLocation(topLeft);
        eBDsettings.setVisible(true);
    }//GEN-LAST:event_EBoardSettingsButtonActionPerformed

    private void E_Board4_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board4_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_E_Board4_Port_TextFieldKeyTyped

    private void GateBar4_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar4_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_GateBar4_Port_TextFieldKeyTyped

    private void GateBar4_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar4_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(GateBar, 4, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar4_Port_TextFieldKeyReleased

    private void Camera4_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera4_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_Camera4_Port_TextFieldKeyTyped

    private void Camera4_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera4_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(Camera, 4, (Component) evt.getSource());
    }//GEN-LAST:event_Camera4_Port_TextFieldKeyReleased

    private void E_Board4_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board4_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(E_Board, 4, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board4_IP_TextFieldKeyReleased

    private void GateBar4_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar4_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(GateBar, 4, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar4_IP_TextFieldKeyReleased

    private void Camera4_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera4_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(Camera, 4, (Component) evt.getSource());
    }//GEN-LAST:event_Camera4_IP_TextFieldKeyReleased

    private void TextFieldGateName4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldGateName4KeyReleased
        checkGateNameChangeAndChangeEnabled(4, (Component)evt.getSource());
    }//GEN-LAST:event_TextFieldGateName4KeyReleased

    private void E_Board3_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board3_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_E_Board3_Port_TextFieldKeyTyped

    private void E_Board3_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board3_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(E_Board, 3, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board3_Port_TextFieldKeyReleased

    private void GateBar3_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar3_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_GateBar3_Port_TextFieldKeyTyped

    private void GateBar3_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar3_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(GateBar, 3, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar3_Port_TextFieldKeyReleased

    private void Camera3_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera3_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_Camera3_Port_TextFieldKeyTyped

    private void Camera3_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera3_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(Camera, 3, (Component) evt.getSource());
    }//GEN-LAST:event_Camera3_Port_TextFieldKeyReleased

    private void E_Board3_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board3_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(E_Board, 3, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board3_IP_TextFieldKeyReleased

    private void GateBar3_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar3_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(GateBar, 3, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar3_IP_TextFieldKeyReleased

    private void Camera3_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera3_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(Camera, 3, (Component) evt.getSource());
    }//GEN-LAST:event_Camera3_IP_TextFieldKeyReleased

    private void TextFieldGateName3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldGateName3KeyReleased
        checkGateNameChangeAndChangeEnabled(3, (Component)evt.getSource());
    }//GEN-LAST:event_TextFieldGateName3KeyReleased

    private void GateBar1_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar1_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_GateBar1_Port_TextFieldKeyTyped

    private void GateBar1_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar1_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(GateBar, 1, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar1_Port_TextFieldKeyReleased

    private void GateBar1_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar1_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(GateBar, 1, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar1_IP_TextFieldKeyReleased

    private void GateBar1_TypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GateBar1_TypeCBoxActionPerformed
        changeSaveEnabledForDeviceType(GateBar, 1);
    }//GEN-LAST:event_GateBar1_TypeCBoxActionPerformed

    private void E_Board1_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board1_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_E_Board1_Port_TextFieldKeyTyped

    private void E_Board1_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board1_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(E_Board, 1, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board1_Port_TextFieldKeyReleased

    private void E_Board1_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board1_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(E_Board, 1, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board1_IP_TextFieldKeyReleased

    private void E_Board1_TypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_E_Board1_TypeCBoxActionPerformed
        changeSaveEnabledForDeviceType(E_Board, 1);
    }//GEN-LAST:event_E_Board1_TypeCBoxActionPerformed

    private void Camera1_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera1_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_Camera1_Port_TextFieldKeyTyped

    private void Camera1_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera1_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(Camera, 1, (Component) evt.getSource());
    }//GEN-LAST:event_Camera1_Port_TextFieldKeyReleased

    private void Camera1_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera1_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(Camera, 1, (Component) evt.getSource());
    }//GEN-LAST:event_Camera1_IP_TextFieldKeyReleased

    private void TextFieldGateName1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldGateName1KeyReleased
        checkGateNameChangeAndChangeEnabled(1, (Component) evt.getSource());
    }//GEN-LAST:event_TextFieldGateName1KeyReleased

    private void E_Board1_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_E_Board1_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(E_Board, 1);
        changeConnDetailPanComponents(E_Board, 1);
    }//GEN-LAST:event_E_Board1_connTypeCBoxActionPerformed

    private void Camera1_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Camera1_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(Camera, 1);
    }//GEN-LAST:event_Camera1_connTypeCBoxActionPerformed

    private void GateBar1_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GateBar1_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(GateBar, 1);
        changeConnDetailPanComponents(GateBar, 1);        
    }//GEN-LAST:event_GateBar1_connTypeCBoxActionPerformed

    private void Camera1_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_Camera1_TypeCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(
                SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), Camera1_TypeCBox.getSelectedIndex(), 
                deviceType[Camera.ordinal()][1]);
    }//GEN-LAST:event_Camera1_TypeCBoxPopupMenuWillBecomeInvisible

    private void E_Board1_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_E_Board1_TypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(E_Board, 1);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), E_Board1_TypeCBox.getSelectedIndex(), 
                deviceType[E_Board.ordinal()][1]);
    }//GEN-LAST:event_E_Board1_TypeCBoxPopupMenuWillBecomeInvisible

    private void GateBar1_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_GateBar1_TypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(GateBar, 1);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), GateBar1_TypeCBox.getSelectedIndex(), 
                deviceType[GateBar.ordinal()][1]);
    }//GEN-LAST:event_GateBar1_TypeCBoxPopupMenuWillBecomeInvisible

    private void Camera1_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_Camera1_connTypeCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), Camera1_connTypeCBox.getSelectedIndex(), 
                connectionType[Camera.ordinal()][1]);
    }//GEN-LAST:event_Camera1_connTypeCBoxPopupMenuWillBecomeInvisible

    private void E_Board1_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_E_Board1_connTypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(E_Board, 1);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), E_Board1_connTypeCBox.getSelectedIndex(), 
                connectionType[E_Board.ordinal()][1]);
    }//GEN-LAST:event_E_Board1_connTypeCBoxPopupMenuWillBecomeInvisible

    private void GateBar1_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_GateBar1_connTypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(GateBar, 1);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), GateBar1_connTypeCBox.getSelectedIndex(), 
                connectionType[GateBar.ordinal()][1]);
    }//GEN-LAST:event_GateBar1_connTypeCBoxPopupMenuWillBecomeInvisible

    private void TextFieldGateName2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextFieldGateName2KeyReleased
        checkGateNameChangeAndChangeEnabled(2, (Component)evt.getSource());
    }//GEN-LAST:event_TextFieldGateName2KeyReleased

    private void Camera2_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_Camera2_TypeCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), Camera2_TypeCBox.getSelectedIndex(), 
                deviceType[Camera.ordinal()][2]);
    }//GEN-LAST:event_Camera2_TypeCBoxPopupMenuWillBecomeInvisible

    private void Camera2_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_Camera2_connTypeCBoxPopupMenuWillBecomeInvisible
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), Camera2_connTypeCBox.getSelectedIndex(), 
                connectionType[Camera.ordinal()][2]);
    }//GEN-LAST:event_Camera2_connTypeCBoxPopupMenuWillBecomeInvisible

    private void Camera2_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Camera2_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(Camera, 2);
    }//GEN-LAST:event_Camera2_connTypeCBoxActionPerformed

    private void Camera2_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera2_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(Camera, 2, (Component) evt.getSource());
    }//GEN-LAST:event_Camera2_IP_TextFieldKeyReleased

    private void Camera2_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera2_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(Camera, 2, (Component) evt.getSource());
    }//GEN-LAST:event_Camera2_Port_TextFieldKeyReleased

    private void Camera2_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Camera2_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);        
    }//GEN-LAST:event_Camera2_Port_TextFieldKeyTyped

    private void E_Board2_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_E_Board2_TypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(E_Board, 2);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), E_Board2_TypeCBox.getSelectedIndex(), 
                deviceType[E_Board.ordinal()][2]);
    }//GEN-LAST:event_E_Board2_TypeCBoxPopupMenuWillBecomeInvisible

    private void E_Board2_TypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_E_Board2_TypeCBoxActionPerformed
        changeSaveEnabledForDeviceType(E_Board, 2);
    }//GEN-LAST:event_E_Board2_TypeCBoxActionPerformed

    private void E_Board2_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_E_Board2_connTypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(E_Board, 2);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), E_Board2_connTypeCBox.getSelectedIndex(), 
                connectionType[E_Board.ordinal()][2]);
    }//GEN-LAST:event_E_Board2_connTypeCBoxPopupMenuWillBecomeInvisible

    private void E_Board2_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_E_Board2_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(E_Board, 2);
        changeConnDetailPanComponents(E_Board, 2);        
    }//GEN-LAST:event_E_Board2_connTypeCBoxActionPerformed

    private void E_Board2_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board2_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(E_Board, 2, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board2_IP_TextFieldKeyReleased

    private void E_Board2_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board2_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(E_Board, 2, (Component) evt.getSource());
    }//GEN-LAST:event_E_Board2_Port_TextFieldKeyReleased

    private void E_Board2_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_E_Board2_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_E_Board2_Port_TextFieldKeyTyped

    private void GateBar2_TypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_GateBar2_TypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(GateBar, 2);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), GateBar2_TypeCBox.getSelectedIndex(), 
                deviceType[GateBar.ordinal()][2]);
    }//GEN-LAST:event_GateBar2_TypeCBoxPopupMenuWillBecomeInvisible

    private void GateBar2_TypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GateBar2_TypeCBoxActionPerformed
        changeSaveEnabledForDeviceType(GateBar, 2);
    }//GEN-LAST:event_GateBar2_TypeCBoxActionPerformed

    private void GateBar2_connTypeCBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_GateBar2_connTypeCBoxPopupMenuWillBecomeInvisible
        enforceSimulatorTCP_IP(GateBar, 2);
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, ((Component) evt.getSource()), GateBar2_connTypeCBox.getSelectedIndex(), 
                connectionType[GateBar.ordinal()][2]);
    }//GEN-LAST:event_GateBar2_connTypeCBoxPopupMenuWillBecomeInvisible

    private void GateBar2_connTypeCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GateBar2_connTypeCBoxActionPerformed
        changeSaveEnabledForConnTypeChange(GateBar, 2);
        changeConnDetailPanComponents(GateBar, 2);
    }//GEN-LAST:event_GateBar2_connTypeCBoxActionPerformed

    private void GateBar2_IP_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar2_IP_TextFieldKeyReleased
        changeSaveEnabledForDeviceIpChange(GateBar, 2, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar2_IP_TextFieldKeyReleased

    private void GateBar2_Port_TextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar2_Port_TextFieldKeyReleased
        checkDevicePortChangeAndChangeButtonEnabledProperty(GateBar, 2, (Component) evt.getSource());
    }//GEN-LAST:event_GateBar2_Port_TextFieldKeyReleased

    private void GateBar2_Port_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GateBar2_Port_TextFieldKeyTyped
        rejectNonNumericKeys(evt);
    }//GEN-LAST:event_GateBar2_Port_TextFieldKeyTyped
                                              
    void closeSettingsForm() {
        if(mainForm != null)
            mainForm.setConfigureSettingsForm(null);
        if (eBDsettings != null) 
            eBDsettings.dispose();
        
        if (isStand_Alone) {
            this.setVisible(false);
            System.exit(0);
        } else {
            dispose();
        }
    }    
    
    /**
     * Initiate the configuration settings form of OS.Parking Program by itself.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc="-- Look and feel setting code (optional) ">
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
            java.util.logging.Logger.getLogger(Settings_System.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Settings_System.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Settings_System.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Settings_System.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        DB_Access.readEBoardSettings(EBD_DisplaySettings);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //ControlGUI mainForm = new ControlGUI();
                new Settings_System(null).setVisible(true);
            }
        });
    }

    //<editor-fold desc="-- Automatically generated form controls">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox BlinkingComboBox;
    private javax.swing.JTextField Camera1_IP_TextField;
    private javax.swing.JTextField Camera1_Port_TextField;
    private javax.swing.JComboBox Camera1_TypeCBox;
    private javax.swing.JComboBox Camera1_connTypeCBox;
    private javax.swing.JTextField Camera2_IP_TextField;
    private javax.swing.JTextField Camera2_Port_TextField;
    private javax.swing.JComboBox Camera2_TypeCBox;
    private javax.swing.JComboBox Camera2_connTypeCBox;
    private javax.swing.JTextField Camera3_IP_TextField;
    private javax.swing.JTextField Camera3_Port_TextField;
    private javax.swing.JTextField Camera4_IP_TextField;
    private javax.swing.JTextField Camera4_Port_TextField;
    private javax.swing.JTextField Camera4_Port_TextField1;
    private com.toedter.components.JLocaleChooser DateChooserLangCBox;
    private javax.swing.JPanel EBD_settings;
    private javax.swing.JPanel EBD_settings_label;
    private javax.swing.JButton EBoardSettingsButton;
    private javax.swing.JTextField E_Board1_IP_TextField;
    private javax.swing.JTextField E_Board1_Port_TextField;
    private javax.swing.JComboBox E_Board1_TypeCBox;
    private javax.swing.JComboBox E_Board1_connTypeCBox;
    private javax.swing.JPanel E_Board1_conn_detail_Pan;
    private javax.swing.JTextField E_Board2_IP_TextField;
    private javax.swing.JTextField E_Board2_Port_TextField;
    private javax.swing.JComboBox E_Board2_TypeCBox;
    private javax.swing.JComboBox E_Board2_connTypeCBox;
    private javax.swing.JPanel E_Board2_conn_detail_Pan;
    private javax.swing.JTextField E_Board3_IP_TextField;
    private javax.swing.JTextField E_Board3_Port_TextField;
    private javax.swing.JTextField E_Board4_IP_TextField;
    private javax.swing.JTextField E_Board4_Port_TextField;
    private javax.swing.JTextField E_Board4_Port_TextField1;
    private javax.swing.JPanel E_BoardSettingsButtonPanel;
    private javax.swing.JComboBox FlowingComboBox;
    private javax.swing.JTextField GateBar1_IP_TextField;
    private javax.swing.JTextField GateBar1_Port_TextField;
    private javax.swing.JComboBox GateBar1_TypeCBox;
    private javax.swing.JComboBox GateBar1_connTypeCBox;
    private javax.swing.JPanel GateBar1_conn_detail_Pan;
    private javax.swing.JTextField GateBar2_IP_TextField;
    private javax.swing.JTextField GateBar2_Port_TextField;
    private javax.swing.JComboBox GateBar2_TypeCBox;
    private javax.swing.JComboBox GateBar2_connTypeCBox;
    private javax.swing.JPanel GateBar2_conn_detail_Pan;
    private javax.swing.JTextField GateBar3_IP_TextField;
    private javax.swing.JTextField GateBar3_Port_TextField;
    private javax.swing.JTextField GateBar4_IP_TextField;
    private javax.swing.JTextField GateBar4_Port_TextField;
    private javax.swing.JTextField GateBar4_Port_TextField1;
    private javax.swing.JComboBox GateCountComboBox;
    public javax.swing.JTabbedPane GatesTabbedPane;
    private javax.swing.JComboBox ImageDurationCBox;
    private javax.swing.JLabel ImageDurationLabel;
    private javax.swing.JButton LanguageSelectionlHelpButton;
    private javax.swing.JButton LoggingLevelHelpButton;
    private javax.swing.JComboBox MessageMaxLineComboBox;
    private javax.swing.JComboBox OptnLoggingLevelComboBox;
    private javax.swing.JButton PWHelpButton;
    /*
    private javax.swing.JComboBox PWStrengthChoiceComboBox;
    */
    private javax.swing.JComboBox<ConvComboBoxItem> PWStrengthChoiceComboBox;
    private javax.swing.JCheckBox RecordPassingDelayCBox;
    private javax.swing.JButton SettingsCancelButton;
    private javax.swing.JButton SettingsCloseButton;
    private javax.swing.JButton SettingsSaveButton;
    private javax.swing.JTextField StatPopSizeTextField;
    private javax.swing.JTextField TextFieldGateName1;
    private javax.swing.JTextField TextFieldGateName2;
    private javax.swing.JTextField TextFieldGateName3;
    private javax.swing.JTextField TextFieldGateName4;
    private javax.swing.JTextField TextFieldPicHeight;
    private javax.swing.JTextField TextFieldPicWidth;
    private javax.swing.JPanel allCyclesPanel;
    private javax.swing.JPanel blinkPanel;
    private javax.swing.JLabel blinkingL;
    private javax.swing.JPanel blinkingP;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel cBoxPan;
    private javax.swing.JPanel cBoxPanel;
    private javax.swing.JPanel cameraPan;
    private javax.swing.JPanel cameraPan1;
    private javax.swing.JPanel cycleLabel;
    private javax.swing.JLabel device1_Label;
    private javax.swing.JLabel device1_Label1;
    private javax.swing.JPanel eBoardPan;
    private javax.swing.JPanel eBoardPan1;
    private javax.swing.JPanel eBoardSettingPanel;
    private javax.swing.JLabel ebdLbl;
    private javax.swing.JLabel ebdLbl1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel flowPanel;
    private javax.swing.JPanel flowing;
    private javax.swing.JPanel gate1Panel;
    private javax.swing.JPanel gate2Panel;
    private javax.swing.JPanel gate3Panel;
    private javax.swing.JPanel gate4Panel;
    private javax.swing.JPanel gateBarPan;
    private javax.swing.JPanel gateBarPan1;
    private javax.swing.JLabel gateNameLabel1;
    private javax.swing.JLabel gateNameLabel2;
    private javax.swing.JPanel gateSettingPanel;
    private javax.swing.JPanel gate_name_p;
    private javax.swing.JPanel gate_name_p1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField lotNameTextField;
    private javax.swing.JPanel parkinglotOptionPanel;
    private javax.swing.JPanel real2Pan;
    private javax.swing.JPanel topLabelsPanel;
    private javax.swing.JPanel topLabelsPanel1;
    private javax.swing.JPanel twoCycles;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    /**
     * Read settings values from the DB table and implant form component values using them.
     */
    private void loadComponentValues() {
        lotNameTextField.setText(parkingLotName);
        RecordPassingDelayCBox.setSelected(storePassingDelay);
        PWStrengthChoiceComboBox.setSelectedIndex(pwStrengthLevel);
        OptnLoggingLevelComboBox.setSelectedIndex(opLoggingIndex);
        if (localeIndex - 2 >=0 )
            DateChooserLangCBox.setSelectedIndex(localeIndex - 2);
        DateChooserLangCBox.setSelectedIndex(localeIndex);
        
        StatPopSizeTextField.setText(Integer.toString(statCount));
        MessageMaxLineComboBox.setSelectedItem(String.valueOf(maxMessageLines));
        GateCountComboBox.setSelectedIndex(gateCount - 1);
        ImageDurationCBox.setSelectedIndex(maxArrivalCBoxIndex);

        TextFieldPicWidth.setText(String.valueOf(new DecimalFormat("#,##0").format(PIC_WIDTH)));
        TextFieldPicHeight.setText(String.valueOf(new DecimalFormat("#,##0").format(PIC_HEIGHT)));
        BlinkingComboBox.setSelectedItem(String.valueOf(new DecimalFormat("#,##0").format(EBD_blinkCycle)));
        FlowingComboBox.setSelectedItem(String.valueOf(new DecimalFormat("#,##0").format(EBD_flowCycle)));
        
        for (int i = 0; i < 4; i++) {
            GatesTabbedPane.setEnabledAt(i, false);
        }
        
        for (int i = 0; i < gateCount; i++) {
            //<editor-fold desc="-- Init device IP address and Port numbers">
            GatesTabbedPane.setEnabledAt(i, true);

            // fill gate name textfields
            ((JTextField)getComponentByName("TextFieldGateName" +(i+1))).setText(gateNames[i+1]);
            
            // load 3 device IP address textfields
            ((JTextField)getComponentByName("Camera" +(i+1) + "_IP_TextField"))
                    .setText(deviceIP[Camera.ordinal() ][i+1]);
            
            ((JTextField)getComponentByName("GateBar" +(i+1) + "_IP_TextField"))
                    .setText(deviceIP[GateBar.ordinal() ][i+1]);
            
            ((JTextField)getComponentByName("E_Board" +(i+1) + "_IP_TextField"))
                    .setText(deviceIP[E_Board.ordinal() ][i+1]);
            
            // load 3 device port textfields
            ((JTextField)getComponentByName("Camera" +(i+1) + "_Port_TextField"))
                    .setText(devicePort[Camera.ordinal() ][i+1]);
            
            ((JTextField)getComponentByName("GateBar" +(i+1) + "_Port_TextField"))
                    .setText(devicePort[GateBar.ordinal() ][i+1]);
            
            ((JTextField)getComponentByName("E_Board" +(i+1) + "_Port_TextField"))
                    .setText(devicePort[E_Board.ordinal() ][i+1]);
            //</editor-fold>
        }
        changeEnabled_of_SaveCancelButtons(false);
        
        for (int gate = 1; gate <= gateCount; gate++) { 
            //<editor-fold desc="-- Combo Box item init for device and connection type of each gate">
            JComboBox comboBx = ((JComboBox)getComponentByName("Camera" +gate + "_TypeCBox"));
            if (comboBx != null) {
                comboBx.removeAllItems();
                for (CameraType type: CameraType.values()) {
                    comboBx.addItem(type.getLabel());
                }
                comboBx.setSelectedIndex(DB_Access.deviceType[Camera.ordinal()][gate]);
            }
            
            comboBx = ((JComboBox)getComponentByName("E_Board" +gate + "_TypeCBox"));
            if (comboBx != null) {
                comboBx.removeAllItems();
                for (E_BoardType type: E_BoardType.values()) {
                    comboBx.addItem(type.getLabel());
                }
                comboBx.setSelectedIndex(DB_Access.deviceType[E_Board.ordinal()][gate]);
            }
            
            comboBx = ((JComboBox)getComponentByName("GateBar" +gate + "_TypeCBox"));
            if (comboBx != null) {
                comboBx.removeAllItems();
                for (GateBarType type: GateBarType.values()) {
                    comboBx.addItem(type.getLabel());
                }
                comboBx.setSelectedIndex(DB_Access.deviceType[GateBar.ordinal()][gate]);
            }
            
            for (DeviceType devType: DeviceType.values()) {
                comboBx = ((JComboBox)getComponentByName(devType.name() +gate + "_connTypeCBox"));
                if (comboBx != null) {
                    comboBx.removeAllItems();
                    for (ConnectionType connType : ConnectionType.values()) {
                        if (devType != Camera || connType == ConnectionType.TCP_IP) 
                        {
                            comboBx.addItem(connType.getLabel());
                        }
                    }
                    comboBx.setSelectedIndex(DB_Access.connectionType[devType.ordinal()][gate]);
                }
            }
            //</editor-fold>
        }
    }
    
    /**
     * Load password complexity level selection combo box options.
     */
    private void addPWStrengthItems() {
        PWStrengthChoiceComboBox.removeAllItems();
        
        ConvComboBoxItem CBItem =  null; 

        for (PWStrengthLevel level : PWStrengthLevel.values()) {
            switch (level) {
                case FourDigit:
                    CBItem = new ConvComboBoxItem(level, "Four digits");
                    break;
                case SixDigit:
                    CBItem = new ConvComboBoxItem(level, "Six-digit or more alpha-numeric");
                    break;
                case Complex:
                    CBItem = new ConvComboBoxItem(level, "8 digit or more complex configuration");
                    break;
                default:
            }
            PWStrengthChoiceComboBox.addItem((ConvComboBoxItem) CBItem);
        }
    }
    
    @SuppressWarnings("unchecked")
    /**
     * Load normal operation logging level combo box options.
     */
    private void addOperationLoggingLevelOptions() {
        OptnLoggingLevelComboBox.removeAllItems();
        
        for (OpLogLevel level : OpLogLevel.values()) {
            switch (level) {
                case LogAlways:
                    OptnLoggingLevelComboBox.addItem("(no logging)");
                    break;
                case SettingsChange:
                    OptnLoggingLevelComboBox.addItem("System settings change");
                    break;
                case EBDsettingsChange:
                    OptnLoggingLevelComboBox.addItem("Log E-Board change too");
                    break;
            }
        }        
    }

    /**
     * Checks if natural number is entered and give warning when non-numeric entered.
     * @param textField field whose text is supposed to contain a number
     * @param dialogTitle title of the dialog box that will be shown when non-natural number string is entered
     * @return <b>true</b> when a natural number is in the <code>textField</code>, 
     * <b>false</b> otherwise
     */
    private boolean TextFieldNumericValueOK(JTextField textField, String dialogTitle) {
        // Check if empty string or numeric 0 were entered. 
        String input = textField.getText().trim();
        
        input = input.replace(",", "");
        
        if (input.length() == 0 || Integer.parseInt(input) == 0) {
            JOptionPane.showConfirmDialog(this, "Enter a value of 1 or more ..",
                    dialogTitle, JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            textField.requestFocusInWindow();
            textField.select(0, input.length());
            return false;
        }  else {
            return true;
        }      
    }

    private void initPassingDelayStatIfNeeded(int newStatCount, int gateID) {
        if (storePassingDelay) {
            /**
             * Passing Delay Statistics Population Size Changed while Statistics Are Gathered.
             */
            if (newStatCount !=statCount) { 
                if (passingCountCurrent[gateID] != 0) {
                    String secs = String.format("%.3f " + SECONDS_LABEL.getContent(), 
                            (float)passingDelayCurrentTotalMs[gateID]/passingCountCurrent[gateID]/1000f);
                    
                    String msg4GUI = "(#" + gateID + ")" + RECENT_WORD.getContent() + 
                            passingCountCurrent[gateID] + AVERAGE_WORDS.getContent() + secs;
                    if (DEBUG) {
                        logParkingOperation(OpLogLevel.LogAlways, 
                                msg4GUI + System.getProperty("line.separator"), GENERAL_DEVICE);
                    }
                    if (mainForm != null)
                        addMessageLine(mainForm.getMessageTextArea(), msg4GUI);
                    initializePassingDelayStatistics(gateID);
                }
            } 
        } else {
            initializePassingDelayStatistics(gateID);
        }
    }

    private boolean someIPaddressWrong() {
        InetAddressValidator validator = InetAddressValidator.getInstance();

        for (int i = 0; i < gateCount; i++) {
            JTextField txtField = (JTextField)getComponentByName("Camera" +(i+1) + "_IP_TextField");
            if (!validator.isValidInet4Address( txtField.getText())) {
                GatesTabbedPane.setSelectedIndex(i);
                txtField.requestFocusInWindow();
                JOptionPane.showConfirmDialog(this, "Camera #" + (i+1) + "in IP address format error.",
                        "IP address format error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                return true;
            }   
            
            txtField = (JTextField)getComponentByName("GateBar" +(i+1) + "_IP_TextField");
            if (!validator.isValidInet4Address( txtField.getText())) {
                GatesTabbedPane.setSelectedIndex(i);
                txtField.requestFocusInWindow();
                JOptionPane.showConfirmDialog(this, "GateBar #" + (i+1) + "in IP address format error.",
                        "IP address format error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                return true;
            }    
            
            txtField = (JTextField)getComponentByName("E_Board" +(i+1) + "_IP_TextField");
            if (!validator.isValidInet4Address( txtField.getText())) {
                GatesTabbedPane.setSelectedIndex(i);
                txtField.requestFocusInWindow();
                JOptionPane.showConfirmDialog(this, "E_Board #" + (i+1) + "in IP address format error.",
                        "IP address format error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                
                return true;
            }            
        }        
        
        return false;
    }
    
    public Component getComponentByName(String name) {
        if (componentMap.containsKey(name)) {
            return (Component) componentMap.get(name);
        }
        else 
            return null;
    }    

    @SuppressWarnings("unchecked")
    private void addMaxArrivalItems() {
        ImageDurationCBox.removeAllItems();
        
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(1), "1 day"));
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(7), "7 days"));
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(30), "30 days"));
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(60), "60 days"));
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(90), "90 days"));
        ImageDurationCBox.addItem(new ConvComboBoxItem(new Integer(120), "120 days"));
    }

    private short findCBoxIndex(JComboBox maxArrivalComboBox, int maxArrival) {
        short index = -1;

        Object item;
        for (short idx = 0; idx < maxArrivalComboBox.getItemCount(); idx++) {
            item = maxArrivalComboBox.getItemAt(idx);
            if (item.getClass() == ConvComboBoxItem.class 
                    && (Integer)((ConvComboBoxItem)item).getValue() == maxArrival) {
                index = idx;
                break;
            }
        }
        return index;
    }        

    /**
     * @param eBDsettings the eBDsettings to set
     */
    public void setEBDsettings(JFrame eBDsettings) {
        this.eBDsettings = eBDsettings;
    }

    private int saveGateDevices() {
        Connection conn = null;
        PreparedStatement updateSettings = null;
        int result = 0;
        int updateRowCount = 0;
        
        //<editor-fold desc="-- Create update statement">
        StringBuffer sb = new StringBuffer("Update gatedevices SET ");
        sb.append("  gatename = ? ");
        
        sb.append("  , cameraType = ?");
        sb.append("  , cameraIP = ? ");
        sb.append("  , cameraPort = ?");
        
        sb.append("  , e_boardType = ? ");
        sb.append("  , e_boardConnType = ? ");
        sb.append("  , e_boardCOM_ID = ? ");
        
        sb.append("  , e_boardIP = ? ");
        sb.append("  , e_boardPort = ? ");
        
        sb.append("  , gatebarType = ? ");
        sb.append("  , gatebarConnType = ? ");
        sb.append("  , gatebarCOM_ID = ? ");
        
        sb.append("  , gatebarIP = ? ");
        sb.append("  , gatebarPort = ? ");
        
        sb.append("WHERE GateID = ?");
        //</editor-fold>

        for (int gateID = 1; gateID <= gateCount; gateID++) {
            try 
            {
                conn = JDBCMySQL.getConnection();
                updateSettings = conn.prepareStatement (sb.toString());

                int pIndex = 1;
                JComboBox cBox;

                //<editor-fold defaultstate="collapsed" desc="--Provide actual values to the UPDATE">
                updateSettings.setString(pIndex++, 
                        ((JTextField) componentMap.get("TextFieldGateName" + gateID)).getText().trim());
                
                for (DeviceType type : DeviceType.values()) {
                    cBox = (JComboBox)componentMap.get(type.toString() + gateID + "_TypeCBox");
                    updateSettings.setInt(pIndex++, cBox == null ? 0 : cBox.getSelectedIndex());
                    if (type != Camera) {
                        cBox = (JComboBox)componentMap.get(type.toString() + gateID + "_connTypeCBox");
                        updateSettings.setInt(pIndex++, cBox == null ? 0 : cBox.getSelectedIndex());
                        
                        cBox = (JComboBox)componentMap.get(type.toString() + gateID + "_comID_CBox");
                        updateSettings.setString(pIndex++, cBox == null ? "" : (String)cBox.getSelectedItem());
                    }
                    updateSettings.setString(pIndex++, 
                            ((JTextField) componentMap.get(type.toString() + gateID + "_IP_TextField")).getText().trim());
                    updateSettings.setString(pIndex++, 
                            ((JTextField) componentMap.get(type.toString() + gateID + "_Port_TextField")).getText().trim());
                }
                updateSettings.setInt(pIndex++, gateID);
                // </editor-fold>

                result = updateSettings.executeUpdate();
                
            } catch (SQLException se) {
                Globals.logParkingException(Level.SEVERE, se, "(Save gate & device settings)");
            } finally {
                // <editor-fold defaultstate="collapsed" desc="--Return resources and display the save result">
                closeDBstuff(conn, updateSettings, null, "(Save gate & device settings)");

                if (result == 1) {
                    updateRowCount++;
                }
                // </editor-fold>
            }  
        }
        return updateRowCount;
    }

    private void initializePassingDelayStatistics(int gateID) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pStmt = null;    
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE gatedevices ");
        sql.append("SET passingCountCurrent = 0, ");
        sql.append("  passingDelayCurrentTotalMs = 0 ");
        sql.append("WHERE gateid = ?");
        
        try 
        {
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(sql.toString());
            
            int loc = 1;
            pStmt.setInt(loc++, gateID);
            result = pStmt.executeUpdate();
        } catch(Exception e) {
            logParkingException(Level.SEVERE, e, "(init passing delay statistics)");
        } finally {
            if (result != 1)
                logParkingException(Level.SEVERE, null, "(failed initialization of passing delay statistics)");
                
            closeDBstuff(conn, pStmt, null, "(init passing delay statistics)");
        }  
    }
    
    private void changeEnabled_of_SaveCancelButtons(boolean onOff) {
        SettingsSaveButton.setEnabled(onOff);
        SettingsCancelButton.setEnabled(onOff);        
        SettingsCloseButton.setEnabled(!onOff);
    } 

    private void tryToCloseSettingsForm() {
        if (SettingsSaveButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Settings Changed.\n \n"
                    + "Either [Save] or [Cancel], please.",
                "Confirm Request", JOptionPane.WARNING_MESSAGE);
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        } else {
            closeSettingsForm();
        }     
    }

    private void checkGateNameChangeAndChangeEnabled(int gateNo, Component comp) {
        JTextField gateNameField = (JTextField)componentMap.get("TextFieldGateName" + gateNo);
        
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, comp, gateNameField.getText().trim(), gateNames[gateNo]);
    }

    private void changeSaveEnabledForDeviceIpChange(DeviceType device, int gateNo, Component comp) {
        JTextField deviceIpField = (JTextField)componentMap.get(device.toString() + gateNo + "_IP_TextField");
        
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, comp, deviceIpField.getText().trim(), deviceIP[device.ordinal()][gateNo]);
    }

    private void checkDevicePortChangeAndChangeButtonEnabledProperty(DeviceType device, int gateNo, Component comp) {
        JTextField devicePortField = (JTextField)componentMap.get(device.toString() + gateNo + "_Port_TextField");
        
        ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                changedControls, comp, devicePortField.getText().trim(), devicePort[device.ordinal()][gateNo]);
    }

    private void makeEnterActAsTab() {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK);
        Set<KeyStroke> keys = new HashSet<>();
        keys.add(enter);
        keys.add(tab);
        keys.add(ctrlTab);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);    
    }

    private void rejectNonNumericKeys(KeyEvent evt) {
        char c = evt.getKeyChar();
        if ( !(
                (c >= '0') && (c <= '9') ||
                (c == KeyEvent.VK_BACK_SPACE) ||
                (c == KeyEvent.VK_DELETE) ||
                (c == KeyEvent.VK_ENTER)
                ))
        {
            getToolkit().beep();
            evt.consume();
        }    
    }

    private void reflectE_BoardTypeChange(int gateNo) {
        JComboBox typeCBox = (JComboBox)componentMap.get("E_Board" + gateNo + "_TypeCBox");
        
        if (typeCBox.getSelectedIndex() == E_BoardType.Simulator.ordinal()) { 
            EBoardSettingsButton.setText("Simulator");
            changeCycleCBoxEnabled(true);
        } else if (typeCBox.getSelectedIndex() == E_BoardType.LEDnotice.ordinal()) {
            EBoardSettingsButton.setText("LEDnotice");
            changeCycleCBoxEnabled(false);
        }
        
        if (typeCBox.getSelectedIndex() == deviceType[E_Board.ordinal()][gateNo]) {
            changeEnabled_of_SaveCancelButtons(false);
        } else {
            changeEnabled_of_SaveCancelButtons(true);
        }        
    }

    private void changeCycleCBoxEnabled(boolean b) {
        BlinkingComboBox.setEnabled(b);
        FlowingComboBox.setEnabled(b);
    }

    private void changeSaveEnabledForDeviceType(DeviceType currDevType, int gateNo) {
        JComboBox cBox = (JComboBox)componentMap.get("" + currDevType + gateNo + "_TypeCBox");
        
        if (cBox.getSelectedIndex() == deviceType[currDevType.ordinal()][gateNo]) {
            changeEnabled_of_SaveCancelButtons(false);
        } else {
            changeEnabled_of_SaveCancelButtons(true);
        }    
    }

    private void changeSaveEnabledForConnTypeChange(DeviceType currDevType, int gateNo) {
        JComboBox cBox = (JComboBox)componentMap.get("" + currDevType + gateNo + "_connTypeCBox");
        
        if (cBox.getSelectedIndex() == connectionType[currDevType.ordinal()][gateNo]) {
            changeEnabled_of_SaveCancelButtons(false);
        } else {
            changeEnabled_of_SaveCancelButtons(true);
        }   
        
        JTextField txtField;
        if (cBox.getSelectedIndex() == RS_232.ordinal()) {
            txtField = (JTextField)componentMap.get(currDevType.toString() + gateNo + "_IP_TextField");
            txtField.setEnabled(false);
            txtField = (JTextField)componentMap.get(currDevType.toString() + gateNo + "_Port_TextField");
            txtField.setEnabled(false);
        } else {
            txtField = (JTextField)componentMap.get(currDevType.toString() + gateNo + "_IP_TextField");
            txtField.setEnabled(true);
            txtField = (JTextField)componentMap.get(currDevType.toString() + gateNo + "_Port_TextField");
            txtField.setEnabled(true);
        }
    }

    private void changeConnDetailPanComponents(DeviceType deviceType, int gateNo) {
        JComboBox connTypeCBox 
                = (JComboBox)componentMap.get (deviceType.toString() + gateNo + "_connTypeCBox");
        
        if (connTypeCBox.getSelectedIndex() == -1 )
            return;
        
        if (connTypeCBox.getSelectedItem().toString().equals("RS-232")) {
            showSerialConnectionDetail(deviceType, gateNo);
        } else {
            showSocketConnectionDetail(deviceType, gateNo);
        }
    }

    private void showSerialConnectionDetail(final DeviceType deviceType, final int gateNo) {
        JPanel detailPan = (JPanel)componentMap.get (deviceType.toString() + gateNo + "_conn_detail_Pan");
        detailPan.removeAll();

        JLabel comLabel = new javax.swing.JLabel();
        //<editor-fold desc="-- COM 포트 레이블 생성 및 추가">
        comLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        comLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        comLabel.setText("COM port");
        comLabel.setPreferredSize(new java.awt.Dimension(90, 15));

        comLabel.getAccessibleContext().setAccessibleName("COM_Lbl");
        //</editor-fold>

        final JComboBox comPortID_CBox = new javax.swing.JComboBox();
        //<editor-fold desc="-- 시리얼 콤포트 번호 선택 콤보박스 생성 및 추가">
        comPortID_CBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        comPortID_CBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"3", "4", "5", "6"}));
        comPortID_CBox.setMinimumSize(new java.awt.Dimension(50, 23));
        comPortID_CBox.setName(deviceType.toString() + gateNo + "_comID_CBox"); // NOI18N
        comPortID_CBox.setPreferredSize(new java.awt.Dimension(50, 23));
        comPortID_CBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                ChangeSettings.changeStatus_Manager(SettingsSaveButton, SettingsCancelButton, SettingsCloseButton, 
                        changedControls, ((Component) evt.getSource()), 
                        (String) comPortID_CBox.getSelectedItem(), 
                        deviceComID[deviceType.ordinal()][gateNo]);                
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        comPortID_CBox.setSelectedItem(deviceComID[deviceType.ordinal()][gateNo]);
        //</editor-fold>

        detailPan.setLayout (new java.awt.FlowLayout(FlowLayout.CENTER, 5, 3));
        detailPan.add(comLabel);
        detailPan.add(comPortID_CBox);
        
        String name = ((JComboBox)comPortID_CBox).getName();

        if (name != null && name.length() > 0) {
            componentMap.put(name, comPortID_CBox);
        }
    }

    private void showSocketConnectionDetail(DeviceType deviceType, int gateNo) {
        String controlPrefix = deviceType.toString() + gateNo;
        JPanel detailPan = (JPanel)componentMap.get (controlPrefix + "_conn_detail_Pan");
        detailPan.removeAll();
        detailPan.setLayout(new java.awt.BorderLayout());
        
        Object obj = componentMap.get(controlPrefix + "_IP_TextField");
        detailPan.add((JTextField)obj, java.awt.BorderLayout.WEST);
        
        obj = componentMap.get(controlPrefix + "_Port_TextField"); 
        detailPan.add((JTextField)obj, java.awt.BorderLayout.EAST);
    }

    private boolean someCOMportIDsame() {
        ArrayList<COM_ID_Usage> com_ID_usageList = new ArrayList<COM_ID_Usage>();
        
        int gateID;
        
        for (gateID = 1; gateID <= gateCount; gateID++) 
        {
            if (someCOMportIDsame(gateID, E_Board, com_ID_usageList) ||
                someCOMportIDsame(gateID, GateBar, com_ID_usageList)) 
            {
                return true;
            }
            
        }
        return false;    
    }

    private boolean someCOMportIDsame
        (int gateNo, DeviceType deviceType, ArrayList<COM_ID_Usage> com_ID_usageList) 
    {
        JComboBox cBox = (JComboBox)componentMap.get(deviceType.toString() + gateNo + "_comID_CBox");
        if (cBox == null)
            return false;
        
        String currCOM_ID = (String)cBox.getSelectedItem();
        
        for (COM_ID_Usage usage : com_ID_usageList) {
            if (usage.COM_ID.equals(currCOM_ID)) {
                String msg = 
                        usage.gateNo + "번 입구 " + usage.deviceType + "와" + System.lineSeparator() +
                        gateNo + "번 입구 " + deviceType + "가" + System.lineSeparator() +
                        "둘 다 COM"+ usage.COM_ID + "를 사용하고 있습니다." + System.lineSeparator() +
                        "그래도 저장하겠습니까?";
                int response = JOptionPane.showConfirmDialog(this, msg, "COM포트 중복사용", 
                        YES_NO_OPTION, ERROR_MESSAGE);
                if (response == YES_OPTION) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        com_ID_usageList.add(new COM_ID_Usage(gateNo, deviceType, currCOM_ID));
        return false;
    }

    /**
     * 모의 장치가 연결 유형이 TCP/IP 가 아닌 경우 경고 창을 띄우고 연결 유형을
     * 자동으로 TCP/IP 유형으로 설정함.
     * 
     * @param deviceType
     * @param gateNo 
     */
    private void enforceSimulatorTCP_IP(final DeviceType deviceType, final int gateNo) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String prefix = deviceType.name() + gateNo;

                JComboBox typeCBox = (JComboBox)componentMap.get(prefix + "_TypeCBox");
                JComboBox connTypeCBox = (JComboBox)componentMap.get(prefix + "_connTypeCBox");

                if (typeCBox.getSelectedIndex() == GateBarType.Simulator.ordinal() 
                        && connTypeCBox.getSelectedIndex() != ConnectionType.TCP_IP.ordinal()) 
                {
                    JOptionPane.showMessageDialog(null, "모의 장치는 연결이 TCP-IP 라야 됨!");
                    connTypeCBox.setSelectedIndex(ConnectionType.TCP_IP.ordinal());
                }    
            }
        });        
    }

    private static class COM_ID_Usage {
        int gateNo;
        DeviceType deviceType;
        String COM_ID;

        private COM_ID_Usage(int gateNo, DeviceType deviceType, String COM_ID) {
            this.gateNo = gateNo;
            this.deviceType = deviceType;
            this.COM_ID = COM_ID;
        }
        
        boolean equals(COM_ID_Usage idUsage) {
            if (COM_ID == idUsage.COM_ID) 
                return true;
            else 
                return false;
        }
    }
}
