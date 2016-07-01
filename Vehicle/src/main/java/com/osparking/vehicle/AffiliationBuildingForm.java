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

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_NO;
import static com.mysql.jdbc.MysqlErrorNumbers.ER_YES;
import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.buttonWidthWide;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.DataSheet.saveODSfile;
import static com.osparking.global.Globals.BLDG_TAB_WIDTH;
import static com.osparking.global.Globals.PopUpBackground;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.emptyLastRowPossible;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.getTopLeftPointToPutThisFrameAtScreenCenter;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.insertBuilding;
import static com.osparking.global.Globals.insertBuildingUnit;
import static com.osparking.global.Globals.insertLevel1Affiliation;
import static com.osparking.global.Globals.insertLevel2Affiliation;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.rejectEmptyInput;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.AFFILIATION_DELETE_ALL_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.AFFILIATION_DELETE_ALL_RESULT_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_DELETE_ALL_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_DELETE_ALL_RESULT_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_IN_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DUPLICATE_BUILDING;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DUPLICATE_HIGH_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DUPLICATE_LOW_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DUPLICATE_UNIT;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.EMPTY_HIGH_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.EMPTY_LOW_AFFILI;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.USER_SAVE_ODS_FAIL_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.AFFILIATION_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BUILDING_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_ALL_DAILOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_ALL_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.LOWER_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.READ_ODS_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.READ_ODS_FAIL_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.REJECT_USER_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.UNIT_MODIFY_DIALOGTITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.FormMode.CreateMode;
import static com.osparking.global.names.ControlEnums.FormMode.NormalMode;
import static com.osparking.global.names.ControlEnums.FormMode.UpdateMode;
import static com.osparking.global.names.ControlEnums.FormModeString.CREATE;
import static com.osparking.global.names.ControlEnums.FormModeString.MODIFY;
import static com.osparking.global.names.ControlEnums.FormModeString.SEARCH;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BUILDING_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CHOOSE_PANEL_DIALOG;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_SAVE_HELP;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_AFFIL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_BUILDING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NONE_EXIST;
import static com.osparking.global.names.ControlEnums.LabelContent.ROOM_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.UPDATE_SAVE_HELP;
import static com.osparking.global.names.ControlEnums.LabelContent.WORK_PANEL_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DEL_RESULT;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DELETE_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DELETE_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DEL_RESULT;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.BLDG_DIAG_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.UNIT_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.UNIT_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.UNIT_DIAG_L3;
import com.osparking.global.names.ControlEnums.TableType;
import static com.osparking.global.names.ControlEnums.TableType.Building;
import static com.osparking.global.names.ControlEnums.TableType.L1_TABLE;
import static com.osparking.global.names.ControlEnums.TableType.L2_TABLE;
import static com.osparking.global.names.ControlEnums.TableType.UnitTab;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILDING_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOWER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ROOM_HEADER;
import static com.osparking.global.names.ControlEnums.TitleTypes.AFFILI_BUILD_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.ToolTipContent.INSERT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.NUMBER_FORMAT_ERROR_MSG;
import static com.osparking.global.names.DB_Access.parkingLotLocale;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import static com.osparking.global.names.OSP_enums.ODS_TYPE.AFFILIATION;
import static com.osparking.global.names.OSP_enums.ODS_TYPE.BUILDING;
//import static com.osparking.global.names.OSP_enums.VehicleCol.Building;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.WrappedInt;
import com.osparking.vehicle.driver.ODSReader;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
import java.awt.Color;
import static java.awt.Color.black;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
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
 * @author Open Source Parking Inc.
 */
public class AffiliationBuildingForm extends javax.swing.JFrame {
    private FormMode formMode = NormalMode;
    /**
     * Creates new form BuildingManageFrame
     */
    public AffiliationBuildingForm() {
        initComponents();
        affiL1_Control.setSelected(true);
        
        /**
         * Set icon for the simulated camera program
         */
        setIconImages(OSPiconList);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(PopUpBackground);       
        adjustTables();
        loadL1_Affiliation(0, "");
        loadBuilding(0, 0);
        setFormMode(NormalMode);
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

        odsFileChooser = new javax.swing.JFileChooser();
        fourPanels = new javax.swing.ButtonGroup();
        wholePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        workPanel = new javax.swing.JLabel();
        workPanelName = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel2 = new javax.swing.JLabel();
        modeString = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        BigMidPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(30, 32767));
        centerHelpPanel = new javax.swing.JPanel();
        helpPanel = new javax.swing.JPanel();
        csHelpLabel = new javax.swing.JLabel();
        centerPanel = new javax.swing.JPanel();
        affiliationPanel = new javax.swing.JPanel();
        topLeft = new javax.swing.JPanel();
        affiTopTitle = new javax.swing.JLabel();
        scrollTopLeft = new javax.swing.JScrollPane();
        L1_Affiliation = new RXTable(L1_TABLE);
        affiliTopRight = new javax.swing.JPanel();
        radioPanel1 = new javax.swing.JPanel();
        affiL1_Control = new javax.swing.JRadioButton();
        insertL1_Button = new javax.swing.JButton();
        modifyL1_Button = new javax.swing.JButton();
        deleteL1_Button = new javax.swing.JButton();
        cancelL1_Button = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        botLeft = new javax.swing.JPanel();
        affiBotTitle = new javax.swing.JLabel();
        scrollBotLeft = new javax.swing.JScrollPane();
        L2_Affiliation = new RXTable(L2_TABLE);
        affiliBotRight = new javax.swing.JPanel();
        radioPanel2 = new javax.swing.JPanel();
        affiL2_Control = new javax.swing.JRadioButton();
        insertL2_Button = new javax.swing.JButton();
        modifyL2_Button = new javax.swing.JButton();
        deleteL2_Button = new javax.swing.JButton();
        cancelL2_Button = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 32767));
        jSeparator1 = new javax.swing.JSeparator();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 32767));
        buildingPanel = new javax.swing.JPanel();
        topRight = new javax.swing.JPanel();
        bldgTopTitle = new javax.swing.JLabel();
        scrollTopRight = new javax.swing.JScrollPane();
        BuildingTable = new RXTable(Building);
        bldgTopRight = new javax.swing.JPanel();
        radioPanel3 = new javax.swing.JPanel();
        buildingControl = new javax.swing.JRadioButton();
        insertBuilding_Button = new javax.swing.JButton();
        modifyBuilding_Button = new javax.swing.JButton();
        deleteBuilding_Button = new javax.swing.JButton();
        cancelBuilding_Button = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        botRight = new javax.swing.JPanel();
        UnitLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        UnitTable = new RXTable(UnitTab);
        jPanel12 = new javax.swing.JPanel();
        radioPanel4 = new javax.swing.JPanel();
        unitControl = new javax.swing.JRadioButton();
        insertUnit_Button = new javax.swing.JButton();
        modifyUnit_Button = new javax.swing.JButton();
        deleteUnit_Button = new javax.swing.JButton();
        cancelUnit_Button = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(30, 32767));
        bottomPanel = new javax.swing.JPanel();
        h30_5 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 30), new java.awt.Dimension(40, 30), new java.awt.Dimension(40, 30));
        closePanel = new javax.swing.JPanel();
        deleteAll_Affiliation = new javax.swing.JButton();
        readODSpan = new javax.swing.JPanel();
        readSheet = new javax.swing.JButton();
        ODSAffiliHelp = new javax.swing.JButton();
        saveSheet = new javax.swing.JButton();
        closeFormButton = new javax.swing.JButton();
        bottomGap = new javax.swing.JPanel();

        odsFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AFFILI_BUILD_FRAME_TITLE.getContent());
        setBackground(PopUpBackground);
        setMinimumSize(new Dimension(750, normGUIheight));
        setPreferredSize(new Dimension(750, normGUIheight));

        wholePanel.setLayout(new java.awt.BorderLayout());

        topPanel.setMinimumSize(new java.awt.Dimension(0, 54));
        topPanel.setPreferredSize(new java.awt.Dimension(0, 54));
        topPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 40));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 40));

        workPanel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        workPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        workPanel.setText(WORK_PANEL_LABEL.getContent());
        workPanel.setMaximumSize(new java.awt.Dimension(140, 28));
        workPanel.setMinimumSize(new java.awt.Dimension(50, 26));
        workPanel.setPreferredSize(new java.awt.Dimension(90, 26));
        jPanel1.add(workPanel);

        workPanelName.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        workPanelName.setForeground(pointColor);
        workPanelName.setText("소속, 부서");
        workPanelName.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        workPanelName.setMaximumSize(new java.awt.Dimension(200, 28));
        workPanelName.setMinimumSize(new java.awt.Dimension(51, 26));
        workPanelName.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel1.add(workPanelName);
        jPanel1.add(filler3);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(MODE_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(120, 28));
        jLabel2.setMinimumSize(new java.awt.Dimension(50, 26));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel1.add(jLabel2);

        modeString.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modeString.setForeground(CommonData.tipColor);
        modeString.setText(SEARCH.getContent());
        modeString.setMaximumSize(new java.awt.Dimension(120, 28));
        modeString.setMinimumSize(new java.awt.Dimension(34, 26));
        modeString.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel1.add(modeString);

        topPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setMinimumSize(new java.awt.Dimension(0, 14));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 14));

        jSeparator2.setOpaque(true);
        jSeparator2.setPreferredSize(new java.awt.Dimension(200, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 4, Short.MAX_VALUE)
                .addContainerGap())
        );

        topPanel.add(jPanel2, java.awt.BorderLayout.SOUTH);

        wholePanel.add(topPanel, java.awt.BorderLayout.NORTH);

        BigMidPanel.setMinimumSize(new java.awt.Dimension(380, 320));
        BigMidPanel.setLayout(new javax.swing.BoxLayout(BigMidPanel, javax.swing.BoxLayout.X_AXIS));
        BigMidPanel.add(filler1);

        centerHelpPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        centerHelpPanel.setLayout(new java.awt.BorderLayout());

        helpPanel.setMaximumSize(new java.awt.Dimension(32767, 30));
        helpPanel.setMinimumSize(new java.awt.Dimension(0, 26));
        helpPanel.setPreferredSize(new java.awt.Dimension(300, 30));

        csHelpLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        csHelpLabel.setForeground(Color.gray);
        csHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        csHelpLabel.setText("자료 입력 후 탭 혹은 엔터 키로 저장/적용할 것!");
        csHelpLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        csHelpLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        csHelpLabel.setMaximumSize(new java.awt.Dimension(230, 30));
        csHelpLabel.setPreferredSize(new java.awt.Dimension(230, 28));
        csHelpLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(csHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addComponent(csHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addContainerGap())
        );

        centerHelpPanel.add(helpPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        centerPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.X_AXIS));

        affiliationPanel.setMinimumSize(new java.awt.Dimension(300, 320));
        affiliationPanel.setPreferredSize(new Dimension(BLDG_TAB_WIDTH+50, 500));
        affiliationPanel.setLayout(new javax.swing.BoxLayout(affiliationPanel, javax.swing.BoxLayout.Y_AXIS));

        topLeft.setMinimumSize(new java.awt.Dimension(83, 160));
        topLeft.setPreferredSize(new java.awt.Dimension(588, 168));
        topLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                topLeftMouseClicked(evt);
            }
        });
        topLeft.setLayout(new java.awt.BorderLayout(10, 0));

        affiTopTitle.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        affiTopTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        affiTopTitle.setText(AFFILIATION_LIST_LABEL.getContent());
        affiTopTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                affiTopTitleMouseClicked(evt);
            }
        });
        topLeft.add(affiTopTitle, java.awt.BorderLayout.NORTH);
        affiTopTitle.getAccessibleContext().setAccessibleName("");

        scrollTopLeft.setMaximumSize(new java.awt.Dimension(32767, 300));
        scrollTopLeft.setMinimumSize(new java.awt.Dimension(24, 140));
        scrollTopLeft.setPreferredSize(new java.awt.Dimension(200, 140));

        ((RXTable)L1_Affiliation).setSelectAllForEdit(true);
        L1_Affiliation.setAutoCreateRowSorter(true);
        L1_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        L1_Affiliation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {1, "Janitor's Office", 2},
                {2, "Engineering Bldg", 1}
            },
            new String[]{
                ORDER_HEADER.getContent(),
                HIGHER_HEADER.getContent(),
                "L1_NO"
            }
        )
        {
            public boolean isCellEditable(int row, int column)
            {
                if (column == 0)
                return false;
                else
                return true;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                return Integer.class;
                else
                return String.class;
            }
        }

    );
    L1_Affiliation.setDoubleBuffered(true);
    L1_Affiliation.setRowHeight(tableRowHeight);
    L1_Affiliation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    L1_Affiliation.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            L1_AffiliationFocusLost(evt);
        }
    });
    L1_Affiliation.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            L1_AffiliationKeyReleased(evt);
        }
    });
    scrollTopLeft.setViewportView(L1_Affiliation);

    topLeft.add(scrollTopLeft, java.awt.BorderLayout.CENTER);

    affiliTopRight.setMinimumSize(new Dimension(buttonWidthNorm, L1_Affiliation.getSize().height));
    affiliTopRight.setPreferredSize(new Dimension(buttonWidthNorm, L1_Affiliation.getSize().height));
    affiliTopRight.setLayout(new java.awt.GridBagLayout());

    radioPanel1.setBackground(new java.awt.Color(255, 204, 255));
    radioPanel1.setMaximumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel1.setMinimumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel1.setPreferredSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            radioPanel1MouseClicked(evt);
        }
    });

    fourPanels.add(affiL1_Control);
    affiL1_Control.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    affiL1_Control.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            affiL1_ControlItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout radioPanel1Layout = new javax.swing.GroupLayout(radioPanel1);
    radioPanel1.setLayout(radioPanel1Layout);
    radioPanel1Layout.setHorizontalGroup(
        radioPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 10, Short.MAX_VALUE)
        .addGroup(radioPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(affiL1_Control)
                .addGap(0, 0, Short.MAX_VALUE)))
    );
    radioPanel1Layout.setVerticalGroup(
        radioPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 21, Short.MAX_VALUE)
        .addGroup(radioPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(affiL1_Control)
                .addGap(0, 0, Short.MAX_VALUE)))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    affiliTopRight.add(radioPanel1, gridBagConstraints);

    insertL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertL1_Button.setMnemonic('R');
    insertL1_Button.setText(CREATE_BTN.getContent());
    insertL1_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL1_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL1_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertL1_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    affiliTopRight.add(insertL1_Button, gridBagConstraints);

    modifyL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyL1_Button.setMnemonic('M');
    modifyL1_Button.setText(MODIFY_BTN.getContent());
    modifyL1_Button.setEnabled(false);
    modifyL1_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL1_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL1_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyL1_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    affiliTopRight.add(modifyL1_Button, gridBagConstraints);

    deleteL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteL1_Button.setMnemonic('D');
    deleteL1_Button.setText(DELETE_BTN.getContent());
    deleteL1_Button.setEnabled(false);
    deleteL1_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL1_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL1_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteL1_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    affiliTopRight.add(deleteL1_Button, gridBagConstraints);

    cancelL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    cancelL1_Button.setMnemonic('C');
    cancelL1_Button.setText(CANCEL_BTN.getContent());
    cancelL1_Button.setEnabled(false);
    cancelL1_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL1_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL1_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelL1_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    affiliTopRight.add(cancelL1_Button, gridBagConstraints);

    topLeft.add(affiliTopRight, java.awt.BorderLayout.EAST);

    affiliationPanel.add(topLeft);
    affiliationPanel.add(filler6);

    botLeft.setMinimumSize(new java.awt.Dimension(83, 160));
    botLeft.setPreferredSize(new java.awt.Dimension(588, 168));
    botLeft.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            botLeftMouseClicked(evt);
        }
    });
    botLeft.setLayout(new java.awt.BorderLayout(10, 0));

    affiBotTitle.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
    affiBotTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    affiBotTitle.setText(LOWER_LIST_LABEL.getContent());
    affiBotTitle.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            affiBotTitleMouseClicked(evt);
        }
    });
    botLeft.add(affiBotTitle, java.awt.BorderLayout.NORTH);

    scrollBotLeft.setMaximumSize(new java.awt.Dimension(32767, 300));
    scrollBotLeft.setMinimumSize(new java.awt.Dimension(24, 140));
    scrollBotLeft.setPreferredSize(new java.awt.Dimension(200, 140));

    ((RXTable)L2_Affiliation).setSelectAllForEdit(true);
    L2_Affiliation.setAutoCreateRowSorter(true);
    L2_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    L2_Affiliation.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {1, "Group 1", 3},
            {2, "Group 2", 4}
        },
        new String [] {
            ORDER_HEADER.getContent(),
            LOWER_HEADER.getContent(),
            "PARTY_NO"
        }
    )
    {
        public boolean isCellEditable(int row, int column)
        {
            if (column == 0)
            return false;
            else
            return true;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
            return Integer.class;
            else
            return String.class;
        }
    }
    );
    L2_Affiliation.setDoubleBuffered(true);
    L2_Affiliation.setEnabled(false);
    L2_Affiliation.setRowHeight(tableRowHeight);
    L2_Affiliation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    L2_Affiliation.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            L2_AffiliationFocusLost(evt);
        }
    });
    L2_Affiliation.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            L2_AffiliationKeyReleased(evt);
        }
    });
    scrollBotLeft.setViewportView(L2_Affiliation);

    botLeft.add(scrollBotLeft, java.awt.BorderLayout.CENTER);

    affiliBotRight.setMinimumSize(new Dimension(buttonWidthNorm, L2_Affiliation.getSize().height));
    affiliBotRight.setPreferredSize(new Dimension(buttonWidthNorm, L2_Affiliation.getSize().height));
    affiliBotRight.setLayout(new java.awt.GridBagLayout());

    radioPanel2.setBackground(new java.awt.Color(255, 204, 255));
    radioPanel2.setMaximumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel2.setMinimumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel2.setPreferredSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            radioPanel2MouseClicked(evt);
        }
    });

    fourPanels.add(affiL2_Control);
    affiL2_Control.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            affiL2_ControlItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout radioPanel2Layout = new javax.swing.GroupLayout(radioPanel2);
    radioPanel2.setLayout(radioPanel2Layout);
    radioPanel2Layout.setHorizontalGroup(
        radioPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 10, Short.MAX_VALUE)
        .addGroup(radioPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(affiL2_Control)
                .addGap(0, 0, Short.MAX_VALUE)))
    );
    radioPanel2Layout.setVerticalGroup(
        radioPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 21, Short.MAX_VALUE)
        .addGroup(radioPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(affiL2_Control)
                .addGap(0, 0, Short.MAX_VALUE)))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    affiliBotRight.add(radioPanel2, gridBagConstraints);

    insertL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertL2_Button.setMnemonic('R');
    insertL2_Button.setText(CREATE_BTN.getContent());
    insertL2_Button.setEnabled(false);
    insertL2_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL2_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL2_Button.setName(""); // NOI18N
    insertL2_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertL2_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    affiliBotRight.add(insertL2_Button, gridBagConstraints);

    modifyL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyL2_Button.setMnemonic('M');
    modifyL2_Button.setText(MODIFY_BTN.getContent());
    modifyL2_Button.setEnabled(false);
    modifyL2_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL2_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL2_Button.setName(""); // NOI18N
    modifyL2_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyL2_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    affiliBotRight.add(modifyL2_Button, gridBagConstraints);

    deleteL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteL2_Button.setMnemonic('D');
    deleteL2_Button.setText(DELETE_BTN.getContent());
    deleteL2_Button.setEnabled(false);
    deleteL2_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL2_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL2_Button.setName(""); // NOI18N
    deleteL2_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteL2_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    affiliBotRight.add(deleteL2_Button, gridBagConstraints);

    cancelL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    cancelL2_Button.setMnemonic('C');
    cancelL2_Button.setText(CANCEL_BTN.getContent());
    cancelL2_Button.setEnabled(false);
    cancelL2_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL2_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL2_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelL2_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    affiliBotRight.add(cancelL2_Button, gridBagConstraints);

    botLeft.add(affiliBotRight, java.awt.BorderLayout.EAST);

    affiliationPanel.add(botLeft);

    centerPanel.add(affiliationPanel);
    centerPanel.add(filler4);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator1.setOpaque(true);
    centerPanel.add(jSeparator1);
    centerPanel.add(filler5);

    buildingPanel.setMinimumSize(new java.awt.Dimension(0, 223));
    buildingPanel.setPreferredSize(new Dimension(BLDG_TAB_WIDTH, 500));
    buildingPanel.setLayout(new javax.swing.BoxLayout(buildingPanel, javax.swing.BoxLayout.Y_AXIS));

    topRight.setMinimumSize(new java.awt.Dimension(83, 85));
    topRight.setPreferredSize(new java.awt.Dimension(400, 168));
    topRight.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            topRightMouseClicked(evt);
        }
    });
    topRight.setLayout(new java.awt.BorderLayout(10, 0));

    bldgTopTitle.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
    bldgTopTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    bldgTopTitle.setText(BUILDING_LIST_LABEL.getContent());
    bldgTopTitle.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            bldgTopTitleMouseClicked(evt);
        }
    });
    topRight.add(bldgTopTitle, java.awt.BorderLayout.NORTH);

    scrollTopRight.setMaximumSize(new java.awt.Dimension(200, 300));
    scrollTopRight.setMinimumSize(new java.awt.Dimension(100, 120));
    scrollTopRight.setPreferredSize(new java.awt.Dimension(100, 120));

    ((RXTable)BuildingTable).setSelectAllForEdit(true);
    BuildingTable.setAutoCreateRowSorter(true);
    BuildingTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    BuildingTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] { {1, 101, 5}, {2, 102, 6} },
        new String [] {
            ORDER_HEADER.getContent(),
            BUILDING_HEADER.getContent(),
            "SEQ_NO"})
    {
        public boolean isCellEditable(int row, int column)
        {
            if (column == 0)
            return false;
            else
            return true;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
        }
    }
    );
    ((DefaultTableCellRenderer)BuildingTable.getTableHeader().getDefaultRenderer())
    .setHorizontalAlignment(JLabel.CENTER);
    BuildingTable.getColumnModel().getColumn(1).setCellRenderer(bnoCellRenderer);
    BuildingTable.setDoubleBuffered(true);
    BuildingTable.setEnabled(false);
    BuildingTable.setRowHeight(tableRowHeight);
    L1_Affiliation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    BuildingTable.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            BuildingTableFocusLost(evt);
        }
    });
    BuildingTable.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            BuildingTableKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            BuildingTableKeyReleased(evt);
        }
        public void keyTyped(java.awt.event.KeyEvent evt) {
            BuildingTableKeyTyped(evt);
        }
    });
    scrollTopRight.setViewportView(BuildingTable);

    topRight.add(scrollTopRight, java.awt.BorderLayout.CENTER);

    bldgTopRight.setMinimumSize(new Dimension(buttonWidthNorm, BuildingTable.getSize().height));
    bldgTopRight.setPreferredSize(new Dimension(buttonWidthNorm, BuildingTable.getSize().height));
    bldgTopRight.setLayout(new java.awt.GridBagLayout());

    radioPanel3.setBackground(new java.awt.Color(255, 204, 255));
    radioPanel3.setMaximumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel3.setMinimumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel3.setPreferredSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            radioPanel3MouseClicked(evt);
        }
    });

    fourPanels.add(buildingControl);
    buildingControl.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            buildingControlItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout radioPanel3Layout = new javax.swing.GroupLayout(radioPanel3);
    radioPanel3.setLayout(radioPanel3Layout);
    radioPanel3Layout.setHorizontalGroup(
        radioPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 10, Short.MAX_VALUE)
        .addGroup(radioPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(buildingControl)
                .addGap(0, 0, Short.MAX_VALUE)))
    );
    radioPanel3Layout.setVerticalGroup(
        radioPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 21, Short.MAX_VALUE)
        .addGroup(radioPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(buildingControl)
                .addGap(0, 0, Short.MAX_VALUE)))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    bldgTopRight.add(radioPanel3, gridBagConstraints);

    insertBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertBuilding_Button.setMnemonic('R');
    insertBuilding_Button.setText(CREATE_BTN.getContent());
    insertBuilding_Button.setEnabled(false);
    insertBuilding_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertBuilding_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertBuilding_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertBuilding_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    bldgTopRight.add(insertBuilding_Button, gridBagConstraints);

    modifyBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyBuilding_Button.setMnemonic('M');
    modifyBuilding_Button.setText(MODIFY_BTN.getContent());
    modifyBuilding_Button.setEnabled(false);
    modifyBuilding_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyBuilding_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyBuilding_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyBuilding_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    bldgTopRight.add(modifyBuilding_Button, gridBagConstraints);

    deleteBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteBuilding_Button.setMnemonic('D');
    deleteBuilding_Button.setText(DELETE_BTN.getContent());
    deleteBuilding_Button.setEnabled(false);
    deleteBuilding_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteBuilding_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteBuilding_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteBuilding_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    bldgTopRight.add(deleteBuilding_Button, gridBagConstraints);

    cancelBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    cancelBuilding_Button.setMnemonic('C');
    cancelBuilding_Button.setText(CANCEL_BTN.getContent());
    cancelBuilding_Button.setEnabled(false);
    cancelBuilding_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelBuilding_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelBuilding_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelBuilding_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    bldgTopRight.add(cancelBuilding_Button, gridBagConstraints);

    topRight.add(bldgTopRight, java.awt.BorderLayout.EAST);

    buildingPanel.add(topRight);
    buildingPanel.add(filler7);

    botRight.setMinimumSize(new java.awt.Dimension(83, 85));
    botRight.setPreferredSize(new java.awt.Dimension(400, 168));
    botRight.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            botRightMouseClicked(evt);
        }
    });
    botRight.setLayout(new java.awt.BorderLayout(10, 0));

    UnitLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
    UnitLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    UnitLabel.setText(ROOM_LIST_LABEL.getContent());
    UnitLabel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            UnitLabelMouseClicked(evt);
        }
    });
    botRight.add(UnitLabel, java.awt.BorderLayout.NORTH);

    jScrollPane4.setMaximumSize(new java.awt.Dimension(32767, 300));
    jScrollPane4.setMinimumSize(new java.awt.Dimension(24, 120));
    jScrollPane4.setPreferredSize(new java.awt.Dimension(454, 120));

    ((RXTable)UnitTable).setSelectAllForEdit(true);
    UnitTable.setAutoCreateRowSorter(true);
    UnitTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    UnitTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {{1, 803, 1}, {2, 805, 2}},
        new String [] {
            ORDER_HEADER.getContent(),
            ROOM_HEADER.getContent(),
            "SEQ_NO"})
    {
        public boolean isCellEditable(int row, int column)
        {
            if (column == 0)
            return false;
            else
            return true;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
        }
    }
    );
    ((DefaultTableCellRenderer)UnitTable.getTableHeader().getDefaultRenderer())
    .setHorizontalAlignment(JLabel.CENTER);
    UnitTable.getColumnModel().getColumn(1).setCellRenderer(bnoCellRenderer);
    UnitTable.setDoubleBuffered(true);
    UnitTable.setRowHeight(tableRowHeight);
    L2_Affiliation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    UnitTable.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            UnitTableFocusLost(evt);
        }
    });
    UnitTable.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            UnitTableKeyReleased(evt);
        }
    });
    jScrollPane4.setViewportView(UnitTable);

    botRight.add(jScrollPane4, java.awt.BorderLayout.CENTER);

    jPanel12.setMinimumSize(new Dimension(buttonWidthNorm, UnitTable.getSize().height));
    jPanel12.setPreferredSize(new Dimension(buttonWidthNorm, UnitTable.getSize().height));
    jPanel12.setLayout(new java.awt.GridBagLayout());

    radioPanel4.setBackground(new java.awt.Color(255, 204, 255));
    radioPanel4.setMaximumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel4.setMinimumSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel4.setPreferredSize(new Dimension(buttonWidthNorm - 10, buttonHeightNorm - 10));
    radioPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            radioPanel4MouseClicked(evt);
        }
    });

    fourPanels.add(unitControl);
    unitControl.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            unitControlItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout radioPanel4Layout = new javax.swing.GroupLayout(radioPanel4);
    radioPanel4.setLayout(radioPanel4Layout);
    radioPanel4Layout.setHorizontalGroup(
        radioPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 10, Short.MAX_VALUE)
        .addGroup(radioPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(unitControl)
                .addGap(0, 0, Short.MAX_VALUE)))
    );
    radioPanel4Layout.setVerticalGroup(
        radioPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 21, Short.MAX_VALUE)
        .addGroup(radioPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(unitControl)
                .addGap(0, 0, Short.MAX_VALUE)))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    jPanel12.add(radioPanel4, gridBagConstraints);

    insertUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertUnit_Button.setMnemonic('R');
    insertUnit_Button.setText(CREATE_BTN.getContent());
    insertUnit_Button.setEnabled(false);
    insertUnit_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertUnit_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertUnit_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    insertUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertUnit_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    jPanel12.add(insertUnit_Button, gridBagConstraints);

    modifyUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyUnit_Button.setMnemonic('M');
    modifyUnit_Button.setText(MODIFY_BTN.getContent());
    modifyUnit_Button.setEnabled(false);
    modifyUnit_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyUnit_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyUnit_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    modifyUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyUnit_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    jPanel12.add(modifyUnit_Button, gridBagConstraints);

    deleteUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteUnit_Button.setMnemonic('D');
    deleteUnit_Button.setText(DELETE_BTN.getContent());
    deleteUnit_Button.setEnabled(false);
    deleteUnit_Button.setMargin(new java.awt.Insets(2, 4, 2, 4));
    deleteUnit_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteUnit_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteUnit_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    deleteUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteUnit_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    jPanel12.add(deleteUnit_Button, gridBagConstraints);

    cancelUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    cancelUnit_Button.setMnemonic('C');
    cancelUnit_Button.setText(CANCEL_BTN.getContent());
    cancelUnit_Button.setEnabled(false);
    cancelUnit_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelUnit_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelUnit_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
    cancelUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelUnit_ButtonActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
    jPanel12.add(cancelUnit_Button, gridBagConstraints);

    botRight.add(jPanel12, java.awt.BorderLayout.EAST);

    buildingPanel.add(botRight);

    centerPanel.add(buildingPanel);

    centerHelpPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

    BigMidPanel.add(centerHelpPanel);
    BigMidPanel.add(filler2);

    wholePanel.add(BigMidPanel, java.awt.BorderLayout.CENTER);

    bottomPanel.setMinimumSize(new java.awt.Dimension(200, 108));
    bottomPanel.setPreferredSize(new java.awt.Dimension(200, 108));
    bottomPanel.setLayout(new java.awt.BorderLayout());
    bottomPanel.add(h30_5, java.awt.BorderLayout.NORTH);

    closePanel.setMaximumSize(new Dimension(4000, buttonHeightNorm));
    closePanel.setMinimumSize(new Dimension(150, buttonHeightNorm + 2));
    closePanel.setPreferredSize(new Dimension(40, buttonHeightNorm + 2));

    deleteAll_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteAll_Affiliation.setText(DELETE_ALL_BTN.getContent());
    deleteAll_Affiliation.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    deleteAll_Affiliation.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    deleteAll_Affiliation.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    deleteAll_Affiliation.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteAll_AffiliationActionPerformed(evt);
        }
    });

    readODSpan.setMinimumSize(new Dimension(130, buttonHeightNorm));
    readODSpan.setPreferredSize(new Dimension(140, buttonHeightNorm));

    readSheet.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    readSheet.setMnemonic('O');
    readSheet.setText(READ_ODS_BTN.getContent());
    readSheet.setToolTipText("");
    readSheet.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    readSheet.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    readSheet.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    readSheet.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            readSheetActionPerformed(evt);
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

    javax.swing.GroupLayout readODSpanLayout = new javax.swing.GroupLayout(readODSpan);
    readODSpan.setLayout(readODSpanLayout);
    readODSpanLayout.setHorizontalGroup(
        readODSpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(readODSpanLayout.createSequentialGroup()
            .addGap(2, 2, 2)
            .addComponent(readSheet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(ODSAffiliHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    readODSpanLayout.setVerticalGroup(
        readODSpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, readODSpanLayout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(ODSAffiliHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGroup(readODSpanLayout.createSequentialGroup()
            .addComponent(readSheet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );

    saveSheet.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    saveSheet.setText(SAVE_ODS_BTN.getContent());
    saveSheet.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    saveSheet.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    saveSheet.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
    saveSheet.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveSheetActionPerformed(evt);
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

    javax.swing.GroupLayout closePanelLayout = new javax.swing.GroupLayout(closePanel);
    closePanel.setLayout(closePanelLayout);
    closePanelLayout.setHorizontalGroup(
        closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(closePanelLayout.createSequentialGroup()
            .addGap(40, 40, 40)
            .addComponent(deleteAll_Affiliation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10)
            .addComponent(readODSpan, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(saveSheet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
            .addComponent(closeFormButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(40, 40, 40))
    );
    closePanelLayout.setVerticalGroup(
        closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(closePanelLayout.createSequentialGroup()
            .addGroup(closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(readODSpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addComponent(deleteAll_Affiliation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveSheet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeFormButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(0, 0, 0))
    );

    bottomPanel.add(closePanel, java.awt.BorderLayout.CENTER);

    bottomGap.setMinimumSize(new java.awt.Dimension(10, 36));
    bottomGap.setPreferredSize(new java.awt.Dimension(100, 36));
    bottomPanel.add(bottomGap, java.awt.BorderLayout.SOUTH);

    wholePanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

    getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

    pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void addAffiliationSelectionListener() {
        ListSelectionModel cellSelectionModel = L1_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
                            int index1 = followAndGetTrueIndex(L1_Affiliation);

                            if (index1 >= 0) 
                            {
                                Object L1_no = L1_Affiliation.getModel().getValueAt(index1, 2);
                                modifyL1_Button.setEnabled(L1_no == null ? false : true);
                                deleteL1_Button.setEnabled(L1_no == null ? false : true);
                                loadL2_Affiliation(L1_no, 0, "");
                            }
                            else
                            {
                                loadL2_Affiliation(null, 0, "");
                            }

                            // clear L2List selection
                            L2_Affiliation.removeEditor();
                            L2_Affiliation.getSelectionModel().clearSelection();  

                            // Delete an empty row if existed
                            if (emptyLastRowPossible(insertL1_Button, L1_Affiliation))
                            {
                                removeEmptyRow(insertL1_Button, L1_Affiliation);                    
                            }
                        }
                    }
                });                 
            }
        }); 
        
        cellSelectionModel = L2_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
                            int index2 = followAndGetTrueIndex(L2_Affiliation);
                            if (index2 >= 0)
                            {
                                Object L2_no = L2_Affiliation.getModel().getValueAt(index2, 2);
                                deleteL2_Button.setEnabled(L2_no == null ? false : true);               
                                modifyL2_Button.setEnabled(L2_no == null ? false : true);    
                            }
                            else
                            {
                                deleteL2_Button.setEnabled(false);     
                                modifyL2_Button.setEnabled(false);     
                            }

                            // Delete an empty row if existed
                            if (emptyLastRowPossible(insertL2_Button, L2_Affiliation))
                            {
                                removeEmptyRow(insertL2_Button, L2_Affiliation);
                            }
                        }
                    }
                });
            }
        });
    }

    private int followAndGetTrueIndex(JTable theTable) {
        theTable.scrollRectToVisible(
                new Rectangle(theTable.getCellRect(theTable.getSelectedRow(), 0, true)));
        if (theTable.getSelectedRow() == -1)
            return -1;
        else
            return theTable.convertRowIndexToModel(theTable.getSelectedRow());                
    }
    
    private void L1_AffiliationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_L1_AffiliationKeyReleased
        int rowIndex = L1_Affiliation.convertRowIndexToModel (L1_Affiliation.getSelectedRow());
        TableModel model = L1_Affiliation.getModel();
        String L1Name = ((String)model.getValueAt(rowIndex, 1)).trim();
        
        // Conditions to make this a new higher affiliation: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        { 
            // <editor-fold defaultstate="collapsed" desc="-- Create a high affiliation">                  
            if (L1Name == null 
                    || L1Name.isEmpty()
                    || L1Name.equals(INSERT_TOOLTIP.getContent())) 
            {
                abortCreation(L1_TABLE);
                return;
            } else {
                // Cond 2. Name field has string of meaningful affiliation name
                // <editor-fold defaultstate="collapsed" desc="-- Insert New Higher name and Refresh the List">               
                int result = insertLevel1Affiliation(L1Name);
                
                if (result == ER_NO) {
                    loadL1_Affiliation(-1, L1Name); // Refresh the list
                } else if (result == ER_DUP_ENTRY) {
                    String msg = DUPLICATE_HIGH_AFFILI.getContent() + L1Name;
                    JOptionPane.showConfirmDialog(null, msg,
                            ERROR_DIALOGTITLE.getContent(),
                            JOptionPane.WARNING_MESSAGE, WARNING_MESSAGE);
                    abortCreation(L1_TABLE);
                }
                // </editor-fold>
            }
            //</editor-fold>
        }
        else 
        {
            // <editor-fold defaultstate="collapsed" desc="-- Handle higher affiliation name update">
            if (L1Name.isEmpty()) {
                String msg = EMPTY_HIGH_AFFILI.getContent() + L1Name;  
                abortModification(msg, rowIndex, L1_Affiliation);                      
                return;                
            } else {
                // <editor-fold defaultstate="collapsed" desc="-- Update high affiliation name">
                Object L1No = model.getValueAt(rowIndex, 2);
                int result = 0;
                Connection conn = null;
                PreparedStatement modifyAffiliation = null;
                String excepMsg = "(Original L1 Affili': " + prevL1Name + ")";

                try {
                    String sql = "Update L1_affiliation Set PARTY_NAME = ? Where L1_NO = ?";
                    
                    conn = getConnection();
                    modifyAffiliation = conn.prepareStatement(sql);
                    modifyAffiliation.setString(1, L1Name);
                    modifyAffiliation.setInt(2, (Integer)L1No);
                    result = modifyAffiliation.executeUpdate();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY) {
                        String msg = DUPLICATE_HIGH_AFFILI.getContent() + L1Name;  
                        abortModification(msg, rowIndex, L1_Affiliation);                      
                        return;
                    }
                } finally {
                    closeDBstuff(conn, modifyAffiliation, null, excepMsg);
                }    
                if (result == 1) {
                    loadL1_Affiliation(-1, L1Name); // Refresh higher affiliation list
                } 
                //</editor-fold>   
            } 
            //</editor-fold>                             
        }
        cancelL1_Button.setEnabled(false);
        setFormMode(FormMode.NormalMode);
        changeControlEnabledForTable(L1_TABLE);            
    }//GEN-LAST:event_L1_AffiliationKeyReleased

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
    
    private void deleteL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteL1_ButtonActionPerformed
        // Delete currently selected higher affiliation
        int viewIndex = L1_Affiliation.getSelectedRow();
        if (viewIndex == -1)
        {
            return;
        }
        String affiliation = (String)L1_Affiliation.getValueAt(viewIndex, 1);
        int modal_Index = L1_Affiliation.convertRowIndexToModel(viewIndex);
        int L1_no = (int)L1_Affiliation.getModel().getValueAt(modal_Index, 2);
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
                loadL1_Affiliation(viewIndex, ""); // Deliver the index of deleted row

                dialogMessage = AFFILI_DEL_RESULT.getContent() +
                        System.getProperty("line.separator") + 
                        AFFILI_DIAG_L2.getContent() + affiliation;

                JOptionPane.showConfirmDialog(this, dialogMessage,
                        DELETE_RESULT_DIALOGTITLE.getContent(),
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_deleteL1_ButtonActionPerformed

    private void insertL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertL1_ButtonActionPerformed
        prepareInsertion(L1_Affiliation, insertL1_Button, cancelL1_Button);
    }//GEN-LAST:event_insertL1_ButtonActionPerformed

    private void L2_AffiliationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_L2_AffiliationKeyReleased
        int rowIndex = L2_Affiliation.convertRowIndexToModel (L2_Affiliation.getSelectedRow());
        TableModel model = L2_Affiliation.getModel();        
        final String L2Name = ((String)model.getValueAt(rowIndex, 1)).trim();
        
        // Conditions to make this a new lower affiliation: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        {
            // <editor-fold defaultstate="collapsed" desc="-- Create a low affiliation">   
            if (L2Name == null 
                    || L2Name.isEmpty()
                    || L2Name.equals(INSERT_TOOLTIP.getContent())) 
            {
                abortCreation(L2_TABLE);
                return;                
            } else {
                // Cond 2. Name field has string of meaningful low affiliation name
                int index = L1_Affiliation.convertRowIndexToModel(L1_Affiliation.getSelectedRow());                
                Object parentKey = L1_Affiliation.getModel().getValueAt(index, 2);

                // <editor-fold defaultstate="collapsed" desc="-- Insert New Lower name and Refresh the List"> 
                int result = insertLevel2Affiliation((Integer)parentKey, L2Name);
                if (result == 1)
                {
                    loadL2_Affiliation(parentKey, -1, L2Name); // Refresh the list
                } else if (result == 2) {
                    String msg = DUPLICATE_LOW_AFFILI.getContent() + L2Name;                   
                    JOptionPane.showConfirmDialog(null, msg,
                            ERROR_DIALOGTITLE.getContent(), 
                            JOptionPane.WARNING_MESSAGE, WARNING_MESSAGE);                       
                    abortCreation(L2_TABLE);
                }                
                //</editor-fold>
            }
            //</editor-fold>            
        }
        else 
        {
            //<editor-fold desc="-- Handle building number update">
            if (L2Name.trim().isEmpty()) {
                String msg = EMPTY_LOW_AFFILI.getContent();
                abortModification(msg, rowIndex, L2_Affiliation);                      
                return;                   
            } else {
                int index1 = L1_Affiliation.convertRowIndexToView(
                        L1_Affiliation.getSelectedRow());
                Object L1_no = L1_Affiliation.getModel().getValueAt(index1, 2);
                Object L2_no = model.getValueAt(rowIndex, 2);

                int result = 0;
                // <editor-fold defaultstate="collapsed" desc="-- update lower level affiliation">            
                Connection conn = null;
                PreparedStatement updateL2name = null;
                String excepMsg = "lower level affiliation name(before update): " + prevL2Name;

                try {
                    String sql = "Update L2_Affiliation Set PARTY_NAME = ? Where L2_NO = ?";
                    conn = getConnection();
                    updateL2name = conn.prepareStatement(sql);
                    updateL2name.setString(1, L2Name);
                    updateL2name.setInt(2, (Integer)L2_no);

                    result = updateL2name.executeUpdate();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY) {
                        String msg = DUPLICATE_LOW_AFFILI.getContent() + L2Name;
                        abortModification(msg, rowIndex, L2_Affiliation); 
                        return;
                    }
                } finally {
                    closeDBstuff(conn, updateL2name, null, excepMsg);
                }                 
                //</editor-fold>            
                if (result == 1)
                {
                    loadL2_Affiliation(L1_no, -1, L2Name); // Refresh lower affiliation list
                }
            }
            //</editor-fold>
        }
        cancelL2_Button.setEnabled(false);
        setFormMode(FormMode.NormalMode);
        changeControlEnabledForTable(L2_TABLE);            
    }//GEN-LAST:event_L2_AffiliationKeyReleased

    private void insertL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertL2_ButtonActionPerformed
        prepareInsertion(L2_Affiliation, insertL2_Button, cancelL2_Button);      
    }//GEN-LAST:event_insertL2_ButtonActionPerformed

    private void deleteL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteL2_ButtonActionPerformed
        // Delete currently selected lower affiliation
        int index2 = L2_Affiliation.getSelectedRow();
        if (index2 == -1)
        {
            return;
        }
        String BelongName = (String)L2_Affiliation.getValueAt(index2, 1);
        int modal_Index = L2_Affiliation.convertRowIndexToModel(index2);        
        int L2_no = (int)L2_Affiliation.getModel().getValueAt(modal_Index, 2);
        
        String dialogMessage = "";
                
        switch (parkingLotLocale.getLanguage()) {
            case "ko":
                dialogMessage = "다음 하위 소속을 삭제합니까?" + System.getProperty("line.separator") 
                + " -하위 소속명: " + BelongName;
                break;
            default:
                dialogMessage = "Want to delete the following lower affiliation?" + System.getProperty("line.separator") 
                + " - Affiliation name: " + BelongName;
                break;
            }
            
        int result = JOptionPane.showConfirmDialog(this, dialogMessage,
                DELETE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
            // <editor-fold defaultstate="collapsed" desc="-- deletion of a lower level affiliation">               
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = "(Deletion of Lower Party No: " + L2_no + ")";

            result = 0;
            try {
                String sql = "Delete From L2_Affiliation Where L2_no = ?";

                conn = getConnection();
                createBuilding = conn.prepareStatement(sql);
                createBuilding.setInt(1, L2_no);

                result = createBuilding.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);

                if (result == 1) {
                    int index1 = L1_Affiliation.convertRowIndexToView(L1_Affiliation.getSelectedRow());
                    Object L1_No = L1_Affiliation.getModel().getValueAt(index1, 2);                    
                    loadL2_Affiliation((Integer)L1_No, index2, ""); // Deliver the deleted L2 affiliation name
                    
                    String dialog = "";

                    switch (parkingLotLocale.getLanguage()) {
                        case "ko":
                            dialog = "하위 소속 '" + BelongName + 
                                "'이 성공적으로 삭제되었습니다";
                            break;
                        default:
                            dialog = "Lower Affiliation '" + BelongName + "' has been successfully deleted";
                            break;
                    }
                    
                    JOptionPane.showConfirmDialog(this, dialog,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            }
            //</editor-fold>            
        }
    }//GEN-LAST:event_deleteL2_ButtonActionPerformed

    private void BuildingTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BuildingTableKeyReleased
        int rowIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
        TableModel model = BuildingTable.getModel();
        Object inputObj = model.getValueAt(rowIndex, 1);
        Integer bnoInteger = null;
        
        if (inputObj.getClass() == String.class) {
            JOptionPane.showConfirmDialog(this, 
                    NUMBER_FORMAT_ERROR_MSG.getContent(),
                    ERROR_DIALOGTITLE.getContent(),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);   
            if (BuildingTable.editCellAt(rowIndex, 1)) {
                BuildingTable.getEditorComponent().requestFocus();
            }
            return;
        } else {
            if (inputObj.getClass() == Integer.class) {
                bnoInteger = (Integer)inputObj;
            }
        }
                
        // Conditions to make this a new building number: Cond 1 and cond 2        
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        { 
            // <editor-fold defaultstate="collapsed" desc="-- Create a building">            
            if (bnoInteger == null)
            {
                abortCreation(TableType.Building);
                return;
            } else {
                // Cond 2: Building Number has a good decimal number string
                // <editor-fold defaultstate="collapsed" desc="-- Actual creation of a new building(number)">            
                int result = insertBuilding(bnoInteger);;
                
                if (result == ER_NO) {
                    loadBuilding(-1, bnoInteger); // Refresh building number list
                } else {
                    if (result == ER_DUP_ENTRY) {
                        String msg = DUPLICATE_HIGH_AFFILI.getContent() + bnoInteger;                   
                        JOptionPane.showConfirmDialog(null, msg,
                                ERROR_DIALOGTITLE.getContent(), 
                                JOptionPane.WARNING_MESSAGE, WARNING_MESSAGE);                       
                        abortCreation(Building);
                    }
                }                  
                //</editor-fold>            
            }   
            //</editor-fold>            
        }
        else {
            // <editor-fold defaultstate="collapsed" desc="-- Handle building number update">                          
            if (bnoInteger == null) {
                rejectEmptyInput(BuildingTable, rowIndex, "Can't use empty string as a building number"); 
            } else {
                // <editor-fold defaultstate="collapsed" desc="-- Actual building number update">
                String sql = "Update building_table Set BLDG_NO = ? Where SEQ_NO = ?";
                int seqNo = (Integer)(model.getValueAt(rowIndex, 2));
                String excepMsg = "(Original building no: " + prevBldgNo + ")";
                int result = updateBuildingUnit(rowIndex, bnoInteger, seqNo, sql, 
                        excepMsg, BuildingTable, BUILDING_IN_DIALOG.getContent());
                
                if (result == ER_NO) {
                    loadBuilding(-1, bnoInteger); // Refresh building number list after update
                } else if (result == ER_DUP_ENTRY) {
                    String msg = DUPLICATE_BUILDING.getContent() + bnoInteger;                   
                    abortModification(msg, rowIndex, BuildingTable);
                    return;
                }
                //</editor-fold>            
            }
            //</editor-fold>            
        }
        cancelBuilding_Button.setEnabled(false);
        setFormMode(FormMode.NormalMode);
        changeControlEnabledForTable(TableType.Building);           
    }//GEN-LAST:event_BuildingTableKeyReleased

    private void insertUnit_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertUnit_ButtonActionPerformed
        prepareInsertion(UnitTable, insertUnit_Button, cancelUnit_Button);
    }//GEN-LAST:event_insertUnit_ButtonActionPerformed
    
    private void UnitTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UnitTableKeyReleased
        TableModel bModel = BuildingTable.getModel();
        int bIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
        int bldgNo = (Integer)bModel.getValueAt(bIndex, 1);
        Object bldgSeqNoObj = bModel.getValueAt(bIndex, 2);
                
        int rowIndex = UnitTable.convertRowIndexToModel (UnitTable.getSelectedRow());
        TableModel model = UnitTable.getModel();
        Object inputObj = model.getValueAt(rowIndex, 1);
        Integer unoInteger = null;
        
        if (inputObj.getClass() == String.class) {
            JOptionPane.showConfirmDialog(this, 
                    NUMBER_FORMAT_ERROR_MSG.getContent(),
                    ERROR_DIALOGTITLE.getContent(),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);   
            if (UnitTable.editCellAt(rowIndex, 1)) {
                UnitTable.getEditorComponent().requestFocus();
            }
            return;
        } else {
            if (inputObj.getClass() == Integer.class) {
                unoInteger = (Integer)inputObj;
            }
        }        
        
        // Conditions to make this a new room number: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        {
            // <editor-fold defaultstate="collapsed" desc="-- Create a new Room number"> 
            if (unoInteger == null) // Cond 2. Number field has some string
            {
                abortCreation(UnitTab);
                return;
            } else {
                // Cond 2: Unit Number has a good decimal number string
                // <editor-fold defaultstate="collapsed" desc="-- Actual creation of a new room number"> 
                int unit_no = (Integer)unoInteger; 
                int result = insertBuildingUnit(unit_no, (Integer)bldgSeqNoObj);
                
                if (result == ER_NO) {
                    loadUnitNumberTable(bldgNo, (Integer)bldgSeqNoObj, -1, unit_no); // Refresh the list
                } else if (result == ER_DUP_ENTRY) {
                    String msg = DUPLICATE_UNIT.getContent() + unit_no;                   
                    JOptionPane.showConfirmDialog(null, msg,
                            ERROR_DIALOGTITLE.getContent(), 
                            JOptionPane.WARNING_MESSAGE, WARNING_MESSAGE);                       
                    abortCreation(UnitTab);
                }    
                //</editor-fold>                
            }
            //</editor-fold>
        } 
        else 
        {
            // <editor-fold defaultstate="collapsed" desc="-- Handle unit/room number update">      
            if (unoInteger == null) {
                rejectEmptyInput(UnitTable, rowIndex, "Can't use empty string as a room unit number"); 
            } else {
                // <editor-fold defaultstate="collapsed" desc="-- Actual room/unit number update">
                String sql = "Update BUILDING_UNIT Set UNIT_NO = ? Where SEQ_NO = ?";
                int seqNo = (Integer)model.getValueAt(rowIndex, 2);
                String excepMsg = "(Oiriginal UNIT No: " + prevUnitNo + ")";
                int result = updateBuildingUnit(rowIndex, unoInteger, seqNo,
                        sql, excepMsg, BuildingTable, BUILDING_IN_DIALOG.getContent());
                if (result == ER_NO) {
                    // Refresh room number list table after a room number update
                    loadUnitNumberTable((Integer)bldgNo, bModel.getValueAt(bIndex, 2), -1, unoInteger); 
                } else if (result == ER_DUP_ENTRY) {
                    String msg = DUPLICATE_UNIT.getContent() + unoInteger;  
                    abortModification(msg, rowIndex, UnitTable);
                    return;
                }
                //</editor-fold>
            }
            //</editor-fold>            
        }
        cancelUnit_Button.setEnabled(false);
        setFormMode(FormMode.NormalMode);
        changeControlEnabledForTable(UnitTab);              
    }//GEN-LAST:event_UnitTableKeyReleased

    private void insertBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertBuilding_ButtonActionPerformed
        prepareInsertion(BuildingTable, insertBuilding_Button, cancelBuilding_Button);
    }//GEN-LAST:event_insertBuilding_ButtonActionPerformed

    private void deleteBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBuilding_ButtonActionPerformed
        // Delete currently selected building row

        int viewIndex = BuildingTable.getSelectedRow();
        if (viewIndex == -1)
        {
            return;
        } else {
            int modal_Index = BuildingTable.convertRowIndexToModel(viewIndex);        
            int bldg_no = (Integer)BuildingTable.getModel().getValueAt(modal_Index, 1);
            int bldg_seq_no = (Integer)BuildingTable.getModel().getValueAt(modal_Index, 2);        
            int count = getUnitCount(bldg_seq_no);

            String message = BLDG_DELETE_L1.getContent() + System.getProperty("line.separator") 
                    + BLDG_DIAG_L2.getContent() + bldg_no + BLDG_DELETE_L3.getContent() 
                    + count + ")";
            
            int result = JOptionPane.showConfirmDialog(this, message,
                    DELETE_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 
        
            if (result == JOptionPane.YES_OPTION) {
                String excepMsg = "(while deleting building No: " + bldg_no + ")";            
                String sql = "Delete From BUILDING_TABLE Where SEQ_NO = ?";                

                result = deleteHigherRow(excepMsg, sql, bldg_seq_no);
                
                if (result == 1) {
                    loadBuilding(viewIndex, 0); // Deliver the index of deleted row
                    message = BLDG_DEL_RESULT.getContent()
                            +  System.getProperty("line.separator") 
                            + BLDG_DIAG_L2.getContent() + bldg_no;

                    JOptionPane.showConfirmDialog(this, message,
                                DELETE_RESULT_DIALOGTITLE.getContent(),
                                JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                
                }
            }
        }
    }//GEN-LAST:event_deleteBuilding_ButtonActionPerformed

    /**
     * Delete a room (unit) number currently selected.
     * @param evt 
     */
    private void deleteUnit_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUnit_ButtonActionPerformed
        int uIndex = UnitTable.getSelectedRow();
        if (uIndex == -1)
        {
            return;
        }
        int unitNo = (Integer)UnitTable.getValueAt(uIndex, 1);
        int modal_Index = UnitTable.convertRowIndexToModel(uIndex);                
        int seqNo = (Integer)UnitTable.getModel().getValueAt(modal_Index, 2);
        
        String dialog = "";
        
        switch (language) {
            case KOREAN:
                dialog = "다음 호실(번호)을 삭제합니까?" + System.getProperty("line.separator") 
                + " -호실번호: " + unitNo;
                break;
                
            case ENGLISH:
                dialog = "Want to delete the following roon number?" + System.getProperty("line.separator") 
                + " -Room No.: " + unitNo;
                break;
                
            default:
                break;
        }        
        
        int result = JOptionPane.showConfirmDialog(this, dialog,
                DELETE_DIALOGTITLE.getContent(), 
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
            // <editor-fold defaultstate="collapsed" desc="-- Deletion of a room number">
            Connection conn = null;
            PreparedStatement deleteUnit = null;
            String excepMsg = "(failed deletion of unit no: " + unitNo + ")";

            result = 0;
            try {
                String sql = "Delete From BUILDING_UNIT Where SEQ_NO = ?";

                conn = getConnection();
                deleteUnit = conn.prepareStatement(sql);
                deleteUnit.setInt(1, seqNo);

                result = deleteUnit.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, deleteUnit, null, excepMsg);
            }
            //</editor-fold>
            
            if (result == 1) 
            {
                int bIndex = BuildingTable.convertRowIndexToView(
                        BuildingTable.getSelectedRow());
                int bldgNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 1));                
                int bldgSeqNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 2));                
                
                /**
                 * Refresh room number list after a room has been deleted.
                 */
                loadUnitNumberTable(bldgNo, bldgSeqNo, uIndex, unitNo); 
                
                switch (language) {
                    case KOREAN:
                        dialog = "다음 호실이 삭제되었습니다" 
                        + System.getProperty("line.separator") + " -호실번호: " + unitNo;
                        break;
                        
                    case ENGLISH:
                        dialog = "Followind Data Item Has Been Deleted" 
                        + System.getProperty("line.separator") + " - Room No.: " + unitNo;
                        break;
                        
                    default:
                        break;
                }
                
                JOptionPane.showConfirmDialog(this, dialog,
                        DELETE_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            } else {
                
                switch (parkingLotLocale.getLanguage()) {
                    case "ko":
                        dialog = "호실번호 '" + unitNo + 
                        "' 삭제에 실패하였습니다";
                        break;
                    default:
                        dialog = "Room No.: '" + unitNo + 
                        "' Deletion Failure";
                        break;
                }                
                
                JOptionPane.showConfirmDialog(this, dialog, 
                        DELETE_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                
            }
        }
    }//GEN-LAST:event_deleteUnit_ButtonActionPerformed
    
    private void modifyUnit_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyUnit_ButtonActionPerformed
        prepareModification(UnitTable, modifyUnit_Button, cancelUnit_Button);
    }//GEN-LAST:event_modifyUnit_ButtonActionPerformed

    private void modifyBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyBuilding_ButtonActionPerformed
        // Get confirmation from the user on a building number update.
        prepareModification(BuildingTable, modifyBuilding_Button, cancelBuilding_Button);
    }//GEN-LAST:event_modifyBuilding_ButtonActionPerformed

    private void modifyL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyL1_ButtonActionPerformed
        // Get confirmation from the user on a 1st level affiliation update.
        prepareModification(L1_Affiliation, modifyL1_Button, cancelL1_Button);
    }//GEN-LAST:event_modifyL1_ButtonActionPerformed

    private void L1_AffiliationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_L1_AffiliationFocusLost
        if (emptyLastRowPossible(insertL1_Button, L1_Affiliation))
        {
            removeEmptyRow(insertL1_Button, L1_Affiliation);
        }
    }//GEN-LAST:event_L1_AffiliationFocusLost

    private void L2_AffiliationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_L2_AffiliationFocusLost
        if (emptyLastRowPossible(insertL2_Button, L2_Affiliation))
        {
            removeEmptyRow(insertL2_Button, L2_Affiliation);
        }
    }//GEN-LAST:event_L2_AffiliationFocusLost

    private void BuildingTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BuildingTableFocusLost
        if (emptyLastRowPossible(insertBuilding_Button, BuildingTable))
        {
            removeEmptyRow(insertBuilding_Button, BuildingTable);
        }
    }//GEN-LAST:event_BuildingTableFocusLost

    private void UnitTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UnitTableFocusLost
        if (emptyLastRowPossible(insertUnit_Button, UnitTable))
        {
            removeEmptyRow(insertUnit_Button, UnitTable);
        }
    }//GEN-LAST:event_UnitTableFocusLost

    private void saveSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheetActionPerformed
        if (affiL1_Control.isSelected()) {
            saveODSfile(this, L1_Affiliation, odsFileChooser, USER_SAVE_ODS_FAIL_DIALOG.getContent());
        } else if (affiL2_Control.isSelected()) {
            saveODSfile(this, L2_Affiliation, odsFileChooser, USER_SAVE_ODS_FAIL_DIALOG.getContent());
        } else if (buildingControl.isSelected()) {
            saveODSfile(this, BuildingTable, odsFileChooser, USER_SAVE_ODS_FAIL_DIALOG.getContent());
        } else if (unitControl.isSelected()) {
            saveODSfile(this, UnitTable, odsFileChooser, USER_SAVE_ODS_FAIL_DIALOG.getContent());
        }
    }//GEN-LAST:event_saveSheetActionPerformed

    private void deleteAll_AffiliationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAll_AffiliationActionPerformed

        if (chosenPanelFor() == AFFILIATION) {
            int result = JOptionPane.showConfirmDialog(this, AFFILIATION_DELETE_ALL_DAILOG.getContent(),
                    DELETE_ALL_DAILOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.YES_OPTION) {
                //<editor-fold desc="-- Delete every affiliation information">
                Connection conn = null;
                PreparedStatement deleteAffiliation = null;
                String excepMsg = "(All Affiliation Deletion)";

                result = 0;
                try {
                    String sql = "Delete From L1_Affiliation";

                    conn = getConnection();
                    deleteAffiliation = conn.prepareStatement(sql);
                    result = deleteAffiliation.executeUpdate();
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                } finally {
                    closeDBstuff(conn, deleteAffiliation, null, excepMsg);
                }    

                if (result >= 1) {
                    loadL1_Affiliation(0, "");
                    JOptionPane.showConfirmDialog(this, AFFILIATION_DELETE_ALL_RESULT_DAILOG.getContent(),
                            DELETE_ALL_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }  
                //</editor-fold>
            }
        } else {
            int result = JOptionPane.showConfirmDialog(this, BUILDING_DELETE_ALL_DAILOG.getContent(), 
                    DELETE_ALL_RESULT_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.YES_OPTION) {
                //<editor-fold desc="-- Delete every affiliation information">
                Connection conn = null;
                PreparedStatement createBuilding = null;
                String excepMsg = "(Deletion of whole building";
                result = 0;

                try {
                    String sql = "Delete From BUILDING_TABLE";
                    conn = getConnection();
                    createBuilding = conn.prepareStatement(sql);
                    result = createBuilding.executeUpdate();
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                } finally {
                    closeDBstuff(conn, createBuilding, null, excepMsg);
                }

                if (result >= 1) {
                    loadBuilding(0, 0); 
                    JOptionPane.showConfirmDialog(this, BUILDING_DELETE_ALL_RESULT_DAILOG.getContent(),
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                           JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
                //</editor-fold>
            }            
        }
    }//GEN-LAST:event_deleteAll_AffiliationActionPerformed

    private void readSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheetActionPerformed
        if (currTable ==  L1_TABLE || currTable == L2_TABLE)
        {
            loadAffiliationsFromODS();
        } else {
            loadBuildingsFromODS();
        }         
    }//GEN-LAST:event_readSheetActionPerformed

    private void ODSAffiliHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSAffiliHelpActionPerformed

        if (chosenPanelFor() == AFFILIATION) {
            JDialog helpDialog = new ODS_HelpJDialog(this, false, 
                    HELP_AFFIL_LABEL.getContent(),
                    ODS_TYPE.AFFILIATION);
            Point buttonPoint = new Point();
            ODSAffiliHelp.getLocation(buttonPoint);
            Point framePoint = new Point();
            this.getLocation(framePoint);
            int leftMargin = filler1.getSize().width + affiliationPanel.getSize().width;
            helpDialog.setLocation(framePoint.x + leftMargin, framePoint.y + 30);
            helpDialog.setVisible(true);
        } else {
            JDialog helpDialog = new ODS_HelpJDialog(this, false, 
                    HELP_BUILDING_LABEL.getContent(),
                    ODS_TYPE.BUILDING);
            Point buttonPoint = new Point();
            ODSAffiliHelp.getLocation(buttonPoint);
            Point framePoint = new Point();
            this.getLocation(framePoint);
            int rightWidth = getSize().width - 
                    (helpDialog.getSize().width + filler5.getSize().width + 
                    buildingPanel.getWidth() + filler2.getSize().width);
            helpDialog.setLocation(framePoint.x + rightWidth, framePoint.y + 30);                
            helpDialog.setVisible(true);    
        }
    }//GEN-LAST:event_ODSAffiliHelpActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private boolean firstRowIsDummy(JTable table) {
	if (table.getRowCount() == 1) {
            Object lineNo = table.getModel().getValueAt(0, 0); // Line number column value
            
            if (lineNo == null) {
                return false;
            } else {
                if (lineNo.getClass() == String.class) {
                    if (((String)lineNo).length() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    TableType currTable = null;
    
    private void changeItemsEnabled(JTable table, boolean selected,
            JButton insertButton, JButton modifyButton, JButton deleteButton)
    {
        if (firstRowIsDummy(table)) {
            ((DefaultTableModel)table.getModel()).setRowCount(0);
        }
        
        int idx = followAndGetTrueIndex(table);    
        
        //<editor-fold desc="-- Change selected panel border">
        if (selected) {
            currTable = ((RXTable)table).getTableType();
            switch (((RXTable)table).getTableType()) {
                case L1_TABLE:
                    topLeft.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                    affiTopTitle.setForeground(pointColor);
                    break;
                case L2_TABLE:
                    botLeft.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                    affiBotTitle.setForeground(pointColor);
                    break;
                case Building:
                    topRight.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                    bldgTopTitle.setForeground(pointColor);
                    break;
                case UnitTab:
                    botRight.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                    UnitLabel.setForeground(pointColor);
                    break;
                default:
                    break;
            }
        } else {
            switch (((RXTable)table).getTableType()) {
                case L1_TABLE:
                    topLeft.setBorder(null);
                    affiTopTitle.setForeground(black);
                    break;
                case L2_TABLE:
                    botLeft.setBorder(null);
                    affiBotTitle.setForeground(black);
                    break;
                case Building:
                    topRight.setBorder(null);
                    bldgTopTitle.setForeground(black);
                    break;
                case UnitTab:
                    botRight.setBorder(null);
                    UnitLabel.setForeground(black);
                    break;
                default:
                    break;
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

            modifyButton.setEnabled(enable);
            deleteButton.setEnabled(enable);
        }
        
        insertButton.setEnabled(formMode == FormMode.NormalMode && selected);
        table.setEnabled(selected);
        if (selected) {
            workPanelName.setText(((RXTable)table).getTableType().getContent());
        }
    }
    
    private void changeControlEnabledForTable(TableType table) {
        switch (table) {
            case L1_TABLE: 
                changeItemsEnabled(L1_Affiliation, affiL1_Control.isSelected(),
                        insertL1_Button, modifyL1_Button, deleteL1_Button);
                break;
            case L2_TABLE: 
                changeItemsEnabled(L2_Affiliation, affiL2_Control.isSelected(),
                        insertL2_Button, modifyL2_Button, deleteL2_Button);
                if (!affiL2_Control.isSelected()) {
                    addDummyFirstRow((DefaultTableModel)L2_Affiliation.getModel());
                }
                break;
            case Building: 
                changeItemsEnabled(BuildingTable, buildingControl.isSelected(),
                        insertBuilding_Button, modifyBuilding_Button, deleteBuilding_Button);
                break;
            case UnitTab: 
                changeItemsEnabled(UnitTable, unitControl.isSelected(),
                        insertUnit_Button, modifyUnit_Button, deleteUnit_Button);
                if (!unitControl.isSelected()) {
                    addDummyFirstRow((DefaultTableModel)UnitTable.getModel());
                }                
                break;
            default:
                break;
        }
    }
    
    private void modifyL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyL2_ButtonActionPerformed
        prepareModification(L2_Affiliation, modifyL2_Button, cancelL2_Button);
    }//GEN-LAST:event_modifyL2_ButtonActionPerformed

    private void cancelL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelL1_ButtonActionPerformed
        // Remove current L1 new row content.
        if (formMode == FormMode.CreateMode) {
            abortCreation(L1_TABLE);
        } else if (formMode == FormMode.UpdateMode) {
            L1_Affiliation.getCellEditor().stopCellEditing();
            setFormMode(FormMode.NormalMode);
            cancelL1_Button.setEnabled(false);
            loadL1_Affiliation(L1_Affiliation.getSelectedRow(), "");
        }
    }//GEN-LAST:event_cancelL1_ButtonActionPerformed

    private void cancelL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelL2_ButtonActionPerformed
        // Remove current L2 new row content.
        if (formMode == FormMode.CreateMode) {
            abortCreation(L2_TABLE);
        } else if (formMode == FormMode.UpdateMode) {
            L2_Affiliation.getCellEditor().stopCellEditing();
            setFormMode(FormMode.NormalMode);  
            cancelL2_Button.setEnabled(false);
            
            int index1 = followAndGetTrueIndex(L1_Affiliation);
            Object L1_no = L1_Affiliation.getModel().getValueAt(index1, 2);
                    
            loadL2_Affiliation(L1_no, L2_Affiliation.getSelectedRow(), "");
        }
    }//GEN-LAST:event_cancelL2_ButtonActionPerformed

    private void cancelBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBuilding_ButtonActionPerformed
        // Remove current L1 new row content.
        if (formMode == FormMode.CreateMode) {
            abortCreation(TableType.Building);
        } else if (formMode == FormMode.UpdateMode) {
            BuildingTable.getCellEditor().stopCellEditing();
            setFormMode(FormMode.NormalMode);  
            cancelBuilding_Button.setEnabled(false);
            loadBuilding(BuildingTable.getSelectedRow(), 0);            
        }
    }//GEN-LAST:event_cancelBuilding_ButtonActionPerformed

    private void cancelUnit_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelUnit_ButtonActionPerformed
        // Remove current L1 new row content.
        if (formMode == FormMode.CreateMode) {
            abortCreation(UnitTab);
        } else if (formMode == FormMode.UpdateMode) {
            UnitTable.getCellEditor().stopCellEditing();
            setFormMode(FormMode.NormalMode);  
            cancelUnit_Button.setEnabled(false);

            int bIndex = BuildingTable.convertRowIndexToView(BuildingTable.getSelectedRow());
            int bldgNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 1));    
            int bldgSeqNo = (Integer)(BuildingTable.getModel().getValueAt(bIndex, 2));  
                
            loadUnitNumberTable(bldgNo, bldgSeqNo, UnitTable.getSelectedRow(), 0);
        }
    }//GEN-LAST:event_cancelUnit_ButtonActionPerformed

    private void BuildingTableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BuildingTableKeyTyped
        System.out.println("Typed: '" + evt.getKeyChar() + "'");
        // TODO add your handling code here:
    }//GEN-LAST:event_BuildingTableKeyTyped

    private void BuildingTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BuildingTableKeyPressed
        System.out.println("Pressed: '" + evt.getKeyChar() + "'");
        // TODO add your handling code here:
    }//GEN-LAST:event_BuildingTableKeyPressed

    private void affiL2_ControlItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_affiL2_ControlItemStateChanged
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                changeControlEnabledForTable(L2_TABLE);
            }
        }); 
    }//GEN-LAST:event_affiL2_ControlItemStateChanged

    private void affiL1_ControlItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_affiL1_ControlItemStateChanged
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                changeControlEnabledForTable(L1_TABLE);
            }
        });
    }//GEN-LAST:event_affiL1_ControlItemStateChanged

    private void unitControlItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_unitControlItemStateChanged
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                changeControlEnabledForTable(UnitTab);                             
            }
        }); 
    }//GEN-LAST:event_unitControlItemStateChanged

    private void buildingControlItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_buildingControlItemStateChanged
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                changeControlEnabledForTable(Building);                
            }
        });  
    }//GEN-LAST:event_buildingControlItemStateChanged

    private void radioPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radioPanel1MouseClicked
        affiL1_Control.setSelected(true);
    }//GEN-LAST:event_radioPanel1MouseClicked

    private void radioPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radioPanel2MouseClicked
        affiL2_Control.setSelected(true);
    }//GEN-LAST:event_radioPanel2MouseClicked

    private void radioPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radioPanel3MouseClicked
        buildingControl.setSelected(true);
    }//GEN-LAST:event_radioPanel3MouseClicked

    private void radioPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radioPanel4MouseClicked
        unitControl.setSelected(true);
    }//GEN-LAST:event_radioPanel4MouseClicked

    private void affiTopTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_affiTopTitleMouseClicked
        affiL1_Control.setSelected(true);
    }//GEN-LAST:event_affiTopTitleMouseClicked

    private void bldgTopTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bldgTopTitleMouseClicked
        buildingControl.setSelected(true);
    }//GEN-LAST:event_bldgTopTitleMouseClicked

    private void affiBotTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_affiBotTitleMouseClicked
        affiL2_Control.setSelected(true);
    }//GEN-LAST:event_affiBotTitleMouseClicked

    private void UnitLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UnitLabelMouseClicked
        unitControl.setSelected(true);
    }//GEN-LAST:event_UnitLabelMouseClicked

    private void topLeftMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topLeftMouseClicked
        affiL1_Control.setSelected(true);
    }//GEN-LAST:event_topLeftMouseClicked

    private void topRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topRightMouseClicked
        buildingControl.setSelected(true);
    }//GEN-LAST:event_topRightMouseClicked

    private void botLeftMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botLeftMouseClicked
        affiL2_Control.setSelected(true);
    }//GEN-LAST:event_botLeftMouseClicked

    private void botRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botRightMouseClicked
        unitControl.setSelected(true);
    }//GEN-LAST:event_botRightMouseClicked

    private void adjustTables() {
        adjustAffiliationTable(L1_Affiliation);
        adjustAffiliationTable(L2_Affiliation);
        addAffiliationSelectionListener();      
        
        adjustNumberTable(BuildingTable);
        adjustNumberTable(UnitTable);
        addBuildingAndUnitSelectionListener(); 
        
        /**
         * Make table column heading click select that table's radio button.
         */
        L1_Affiliation.getTableHeader().addMouseListener(new HdrMouseListener(L1_Affiliation));
        L2_Affiliation.getTableHeader().addMouseListener(new HdrMouseListener(L2_Affiliation));
        BuildingTable.getTableHeader().addMouseListener(new HdrMouseListener(BuildingTable));
        UnitTable.getTableHeader().addMouseListener(new HdrMouseListener(UnitTable));
        
        /**
         * Make table cell 
         * click select that table's radio button,
         * double click initiate cell modification.
         */        
        addClickEventListener(L1_Affiliation);
        addClickEventListener(L2_Affiliation);
        addClickEventListener(BuildingTable);
        addClickEventListener(UnitTable);
    }
    
    private void adjustAffiliationTable(JTable AffiliationTable) {
        
        // Hide affiliation number field which is used internally.
        TableColumnModel BelongModel = AffiliationTable.getColumnModel();
        BelongModel.removeColumn(BelongModel.getColumn(2));
        
        // Decrease the first column width
        TableColumn column = AffiliationTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50); //row number column is narrow
        column.setMinWidth(50); //row number column is narrow
        column.setMaxWidth(50); //row number column is narrow
    }    
    
    public void loadL1_Affiliation(int viewIndexToHighlight, String insertedLevelTwoName) {

        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(load upper and lower level affiliations)";

        DefaultTableModel model = (DefaultTableModel) L1_Affiliation.getModel();
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
                    if (insertedLevelTwoName.equals(rs.getString("PARTY_NAME")))
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

        // <editor-fold defaultstate="collapsed" desc="-- Selection of a higher affiliation and loading of its lower affil'">
        int numRows = model.getRowCount();
        if (numRows > 0)
        {
            if (viewIndexToHighlight == -1) // loading right after a new affiliation is created
            {
                viewIndexToHighlight = L1_Affiliation.convertRowIndexToView(model_Index);
            } else if (viewIndexToHighlight == numRows)
            {
                // "number of remaining rows == deleted row index" means the row deleted was the last row
                // In this case, highlight the previous row
                viewIndexToHighlight--;
            }
            highlightTableRow(L1_Affiliation, viewIndexToHighlight);
//            modifyL1_Button.setEnabled(true);                
//            deleteL1_Button.setEnabled(true);                
        }
        else
        {
            modifyL1_Button.setEnabled(false);                
            deleteL1_Button.setEnabled(false);                      
            loadL2_Affiliation(null, 0, "");
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

    private void loadL2_Affiliation(Object L1_no, int viewIndex, String l2Name) {
        if (L1_no == null)
        {
            affiBotTitle.setText(LOWER_LIST_LABEL.getContent());
            insertL2_Button.setEnabled(false);
            ((DefaultTableModel) L2_Affiliation.getModel()).setRowCount(0);
        }
        else 
        {
            int L1_index = L1_Affiliation.
                    convertRowIndexToModel(L1_Affiliation.getSelectedRow());
            String L1_Affil = L1_Affiliation.getModel().getValueAt(L1_index, 1).toString();
            
            String label = "";
            switch (language) {
                case KOREAN:
                    label = L1_Affil + LOWER_LABEL.getContent();
                    break;
                case ENGLISH:
                    label = LOWER_LABEL.getContent() + L1_Affil; 
                    break;
                default:
                    break;
            }  
            affiBotTitle.setText(label);
            
            Connection conn = null;
            Statement selectStmt = null;
            ResultSet rs = null;
            String excepMsg = "change selected L2 name to " + l2Name + " for L1 no: " + L1_no;

            DefaultTableModel model = (DefaultTableModel) L2_Affiliation.getModel();
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
                    if (viewIndex == -1) { // After creation/update, first find the new/modified affiliation name
                        if (l2Name.equals(rs.getString("PARTY_NAME"))) {
                            model_index = model.getRowCount();
                        }
                    }                       
                    model.addRow(new Object[] {
                         rs.getInt("recNo"),  rs.getString("PARTY_NAME"), rs.getInt("L2_NO")
                    });
                }
                if (!affiL2_Control.isSelected()) {
                    addDummyFirstRow(model);
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
                    viewIndex = L2_Affiliation.convertRowIndexToView(model_index);
                } else {
                    // "number of remaining rows == deleted row index" means the row deleted was the last row
                    // In this case, highlight the previous row                    
                    if (viewIndex == numRows)
                    { 
                        viewIndex--; 
                    }
                }
                highlightTableRow(L2_Affiliation, viewIndex);  
                modifyL2_Button.setEnabled(true);                
                deleteL2_Button.setEnabled(true);                      
            }                                
        }            
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BigMidPanel;
    private javax.swing.JTable BuildingTable;
    private javax.swing.JTable L1_Affiliation;
    private javax.swing.JTable L2_Affiliation;
    private javax.swing.JButton ODSAffiliHelp;
    private javax.swing.JLabel UnitLabel;
    private javax.swing.JTable UnitTable;
    private javax.swing.JLabel affiBotTitle;
    private javax.swing.JRadioButton affiL1_Control;
    private javax.swing.JRadioButton affiL2_Control;
    private javax.swing.JLabel affiTopTitle;
    private javax.swing.JPanel affiliBotRight;
    private javax.swing.JPanel affiliTopRight;
    private javax.swing.JPanel affiliationPanel;
    private javax.swing.JPanel bldgTopRight;
    private javax.swing.JLabel bldgTopTitle;
    private javax.swing.JPanel botLeft;
    private javax.swing.JPanel botRight;
    private javax.swing.JPanel bottomGap;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JRadioButton buildingControl;
    private javax.swing.JPanel buildingPanel;
    private javax.swing.JButton cancelBuilding_Button;
    private javax.swing.JButton cancelL1_Button;
    private javax.swing.JButton cancelL2_Button;
    private javax.swing.JButton cancelUnit_Button;
    private javax.swing.JPanel centerHelpPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JLabel csHelpLabel;
    private javax.swing.JButton deleteAll_Affiliation;
    private javax.swing.JButton deleteBuilding_Button;
    private javax.swing.JButton deleteL1_Button;
    private javax.swing.JButton deleteL2_Button;
    private javax.swing.JButton deleteUnit_Button;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.ButtonGroup fourPanels;
    private javax.swing.Box.Filler h30_5;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JButton insertBuilding_Button;
    private javax.swing.JButton insertL1_Button;
    private javax.swing.JButton insertL2_Button;
    private javax.swing.JButton insertUnit_Button;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel modeString;
    private javax.swing.JButton modifyBuilding_Button;
    private javax.swing.JButton modifyL1_Button;
    private javax.swing.JButton modifyL2_Button;
    private javax.swing.JButton modifyUnit_Button;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JPanel radioPanel1;
    private javax.swing.JPanel radioPanel2;
    private javax.swing.JPanel radioPanel3;
    private javax.swing.JPanel radioPanel4;
    private javax.swing.JPanel readODSpan;
    private javax.swing.JButton readSheet;
    private javax.swing.JButton saveSheet;
    private javax.swing.JScrollPane scrollBotLeft;
    private javax.swing.JScrollPane scrollTopLeft;
    private javax.swing.JScrollPane scrollTopRight;
    private javax.swing.JPanel topLeft;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel topRight;
    private javax.swing.JRadioButton unitControl;
    private javax.swing.JPanel wholePanel;
    private javax.swing.JLabel workPanel;
    private javax.swing.JLabel workPanelName;
    // End of variables declaration//GEN-END:variables
    
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
                if (viewIndex == -1) // the case of a new building creation
                {
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
            if (viewIndex == -1) // handle the case of a new building creation
            {
                viewIndex = BuildingTable.convertRowIndexToView(model_Index);
            } else if (viewIndex == numRows)
            {
                // "number of remaining rows == deleted row index" means the row deleted was the last row
                // In this case, highlight the previous row      
                viewIndex--;
            }
            highlightTableRow(BuildingTable, viewIndex);
//            deleteBuilding_Button.setEnabled(true);                
//            modifyBuilding_Button.setEnabled(true);  
        }
        else
        {
//            deleteBuilding_Button.setEnabled(false);                
//            modifyBuilding_Button.setEnabled(false);                         
            loadUnitNumberTable(0, null, 0, 0);
        }
        //</editor-fold>
    }

    private void loadUnitNumberTable(int bldgNo, Object bldg_seq_no, int viewIndex, int unitNo) 
    {
        if (bldg_seq_no == null)
        {
            UnitLabel.setText(ROOM_LIST_LABEL.getContent());
            insertUnit_Button.setEnabled(false);
            ((DefaultTableModel) UnitTable.getModel()).setRowCount(0);
        }
        else 
        {
            String label = "";
            
            switch (language) {
                case KOREAN:
                    label = bldgNo + "동 호실 목록";
                    break;
                    
                case ENGLISH:
                    label = "Rooms of Building" + bldgNo;
                    break;
                    
                default:
                    break;
            }   
            
            UnitLabel.setText(label); 
            
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
                    if (viewIndex == -1) // Handle the case of a new room creation
                    {
                        if (unitNo == rs.getInt("UNIT_NO"))
                        {
                            model_index = model.getRowCount();
                        }
                    }                    
                    model.addRow(new Object[] 
                        {rs.getInt("recNo"),  rs.getInt("UNIT_NO"), rs.getInt("SEQ_NO")});
                }
                if (!unitControl.isSelected()) {
                    addDummyFirstRow(model);
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
                if (viewIndex == -1) // handle the case of new room creation
                {
                    viewIndex = UnitTable.convertRowIndexToView(model_index);
                } else                    
                    if (viewIndex == numRows)
                {
                    viewIndex--;
                }
                highlightTableRow(UnitTable, viewIndex);
                modifyUnit_Button.setEnabled(true);                
                deleteUnit_Button.setEnabled(true);                       
            }
        }          
    }

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

    private void addBuildingAndUnitSelectionListener() {
        ListSelectionModel cellSelectionModel = BuildingTable.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                if (!e.getValueIsAdjusting())
                {
                    int bIndex = followAndGetTrueIndex(BuildingTable);                 
                    if (bIndex >= 0)
                    {
                        Object bldg_seq_no = BuildingTable.getModel().getValueAt(bIndex, 2);
                        boolean enable = bldg_seq_no != null && buildingControl.isSelected();
                        deleteBuilding_Button.setEnabled(enable);
                        modifyBuilding_Button.setEnabled(enable);
                        
                        if (BuildingTable.getModel().getValueAt(bIndex, 0) == null) 
                        {
                            loadUnitNumberTable(0, bldg_seq_no, 0, 0);
                        } else {
                            loadUnitNumberTable(
                                    (Integer)BuildingTable.getModel().getValueAt(bIndex, 1),
                                    bldg_seq_no, 0, 0);
                        }
                    }
                    else
                    {
                        loadUnitNumberTable(0, null, 0, 0);
                    }

                    // Deselect a highlighted row from the room table
                    UnitTable.removeEditor();
                    UnitTable.getSelectionModel().clearSelection();  
                    
                    if (emptyLastRowPossible(insertBuilding_Button, BuildingTable))
                    {
                        removeEmptyRow(insertBuilding_Button, BuildingTable);                    
                    }	                    
                }
            }
        }); 
        
        cellSelectionModel = UnitTable.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                if (!e.getValueIsAdjusting())
                {
                    int uIndex = followAndGetTrueIndex(UnitTable);                 
                    if (uIndex == -1)
                    {
                        deleteUnit_Button.setEnabled(false);    
                        modifyUnit_Button.setEnabled(false);                            
                    }
                    else
                    {
                        Object L2_no = UnitTable.getModel().getValueAt(uIndex, 1);
                        deleteUnit_Button.setEnabled(L2_no == null ? false : true);               
                        modifyUnit_Button.setEnabled(L2_no == null ? false : true);               
                    }
                    
                    if (emptyLastRowPossible(insertUnit_Button, UnitTable))
                    {
                        removeEmptyRow(insertUnit_Button, UnitTable);                    
                    }	                    
                }
            }
        });  
        
        BuildingTable.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getFirstRow() >= 0 && e.getColumn() >= 0)
                {
                    System.out.println("new value: " + BuildingTable.getValueAt(e.getFirstRow(), e.getColumn()));
                }
            }
        });
    }

    private void adjustNumberTable(JTable NumberTable) {
        // Hide building or unit number field which is used internally.
        TableColumnModel NumberTableModel = NumberTable.getColumnModel();
        NumberTableModel.removeColumn(NumberTableModel.getColumn(2));        
        
        // Decrease the first column width
        TableColumn column = NumberTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50); //row number column is narrow
        column.setMinWidth(50); //row number column is narrow
        column.setMaxWidth(50); //row number column is narrow    
    }

    private void addClickEventListener(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                RXTable table =(RXTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    selectRadioButtonFor(table.getTableType());
                } else {
                    if (me.getClickCount() == 1) {
                        selectRadioButtonFor(table.getTableType());
                    } else 
                    if (me.getClickCount() == 2) {
                        switch (table.getTableType()) {
                            case L1_TABLE:
                                prepareModification(table, modifyL1_Button, cancelL1_Button);                        
                                break;
                            case L2_TABLE:
                                prepareModification(table, modifyL2_Button, cancelL2_Button);                        
                                break;
                            case Building:
                                prepareModification(table, modifyBuilding_Button, cancelBuilding_Button);                        
                                break;
                            case UnitTab:
                                prepareModification(table, modifyUnit_Button, cancelUnit_Button);                        
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void selectRadioButtonFor(TableType tableType) {
        switch (tableType) {
            case L1_TABLE:
                affiL1_Control.setSelected(true);
                break;
            case L2_TABLE:
                affiL2_Control.setSelected(true);
                break;
            case Building:
                buildingControl.setSelected(true);
                break;
            case UnitTab:
                unitControl.setSelected(true);
                break;
            default:
                break;
        }                
    }    
            
    static Object prevUnitNo = null;
    static Object prevBldgNo = null;
    static Object prevL1Name = null;
    static Object prevL2Name = null;
    
    private void processL1NameChangeTrial(int rowIndex) {
        int model_index = L1_Affiliation.convertRowIndexToModel(rowIndex);
        TableModel model1 = L1_Affiliation.getModel();
        
        prevL1Name = model1.getValueAt(model_index, 1);    
        if (model1.getValueAt(model_index, 0) != null) 
        {
            int L1_no = (int)model1.getValueAt(model_index, 2);
            String dialog = "";
            
            //<editor-fold desc="-- Make confirm message.">
            switch (language) {
                case KOREAN:
                    dialog = "다음 상위 소속 이름을 변경합니까?" 
                            + System.getProperty("line.separator") 
                            + " - 상위 소속: " + prevL1Name 
                            + " (관련 하위 소속: " + getL2RecordCount(L1_no) + " 개)"; 
                    break;
                    
                case ENGLISH:
                    dialog =  "Want to change the following higher affiliation?"
                            + System.getProperty("line.separator") 
                            + " - Higher Affiliation: " + prevL1Name 
                            + " (number of lower affiliations: " + getL2RecordCount(L1_no) + ")";
                    break;
                    
                default:
                    break;
            }
            //</editor-fold>
            
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    AFFILIATION_MODIFY_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.NO_OPTION) { 
                L1_Affiliation.getCellEditor().stopCellEditing();
            } else {
                setFormMode(FormMode.UpdateMode);
                modifyL1_Button.setEnabled(false);
                cancelL1_Button.setEnabled(true);
            }
        }
    }

    private void prepareInsertion(JTable listTable, 
            JButton insertButton, JButton cancelButton) 
    {    
        DefaultTableModel model = (DefaultTableModel)listTable.getModel();
        model.setRowCount(listTable.getRowCount() + 1);
        int rowIndex = listTable.getRowCount() - 1;
        
        if (listTable.getValueAt(rowIndex, 1) != null) 
        {
            rowIndex = 0;
        }
        listTable.setRowSelectionInterval(rowIndex, rowIndex);
        
        ((DefaultTableModel)listTable.getModel()).
                setValueAt(INSERT_TOOLTIP.getContent(), rowIndex, 1);

        if (listTable.editCellAt(rowIndex, 1))
        {
            listTable.getEditorComponent().requestFocus();
            listTable.scrollRectToVisible(
                    new Rectangle(listTable.getCellRect(rowIndex, 0, true)));
        }
        setFormMode(FormMode.CreateMode);
        insertButton.setEnabled(false);
        cancelButton.setEnabled(true);
    }

    private void rejectUserInput(JTable thisTable, int rowIndex, String groupName) {
        if (thisTable.editCellAt(rowIndex, 1))
        {
            thisTable.getEditorComponent().requestFocus();
        }
        
        String dialog = "";
        
        switch (language) {
            case KOREAN:
                dialog = "이미 존재하는 " + groupName + "입니다";
                break;
                
            case ENGLISH:
                dialog =  "It already exists in " + groupName + " table";
                break;
                
            default:
                break;
        }
        
        showMessageDialog(null, dialog,
                REJECT_USER_DIALOGTITLE.getContent(),
                JOptionPane.INFORMATION_MESSAGE);     
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
            java.util.logging.Logger.getLogger(AffiliationBuildingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AffiliationBuildingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AffiliationBuildingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AffiliationBuildingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AffiliationBuildingForm runForm = new AffiliationBuildingForm();
                Point screenCenter = getTopLeftPointToPutThisFrameAtScreenCenter(runForm);
                runForm.setLocation(screenCenter);
                runForm.setVisible(true);
                runForm.setDefaultCloseOperation(
                        javax.swing.WindowConstants.EXIT_ON_CLOSE);
            }
        });
    }

    private ODS_TYPE chosenPanelFor() {
        if (affiL1_Control.isSelected() || affiL2_Control.isSelected()) {
            return AFFILIATION;
        } else {
            return BUILDING;
        }
    }

    private void changeRadioButtonsEnabled(boolean b) {
        affiL1_Control.setEnabled(b);
        changeControlEnabledForTable(L1_TABLE);
        affiL2_Control.setEnabled(b);
        changeControlEnabledForTable(L2_TABLE);
        buildingControl.setEnabled(b);
        changeControlEnabledForTable(Building);
        unitControl.setEnabled(b);
        changeControlEnabledForTable(UnitTab);
    }

    private void changeBottomButtonsEnbled(boolean b) {
        deleteAll_Affiliation.setEnabled(b);
        readSheet.setEnabled(b);
        saveSheet.setEnabled(b);
        closeFormButton.setEnabled(b);
    }

    /**
     * @return the formMode
     */
    public FormMode getFormMode() {
        return formMode;
    }

    /** Used to make context sensitive help message only once after
     *  this GUI displayed.
     */
    boolean createBlinked = false;
    boolean normalBlinked = false;
    boolean updateBlinked = false;
    /**
     * @param formMode the formMode to set
     */
    public void setFormMode(FormMode formMode) {
        this.formMode = formMode;
        
        switch (formMode) {
            case CreateMode:
                modeString.setText(CREATE.getContent());
                changeRadioButtonsEnabled(false);
                changeBottomButtonsEnbled(false);   
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
                        CREATE_SAVE_HELP.getContent(), !createBlinked);  
                createBlinked = true;
                break;   
                
            case NormalMode:
                modeString.setText(SEARCH.getContent());
                changeRadioButtonsEnabled(true);
                changeBottomButtonsEnbled(true);
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
                        CHOOSE_PANEL_DIALOG.getContent(), !normalBlinked); 
                normalBlinked = true;
                break;
                
            case UpdateMode:
                modeString.setText(MODIFY.getContent());
                changeRadioButtonsEnabled(false);
                changeBottomButtonsEnbled(false);   
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
                        UPDATE_SAVE_HELP.getContent(), !updateBlinked);                  
                updateBlinked = true;
                break;
                
            default:
                break;
        } 
    }    

    private void abortCreation(TableType tableType) {
        switch (tableType) {
            case L1_TABLE: 
                removeAndReturn(L1_Affiliation, cancelL1_Button);
                break;
            case L2_TABLE: 
                removeAndReturn(L2_Affiliation, cancelL2_Button);                
                break;
            case Building: 
                removeAndReturn(BuildingTable, cancelBuilding_Button);
                break;
            case UnitTab: 
                removeAndReturn(UnitTable, cancelUnit_Button);
                break;
            default:
                break;
        }  
        changeControlEnabledForTable(tableType);
    }

    private void removeAndReturn(JTable table, JButton cancelButton) 
    {
        ((DefaultTableModel)table.getModel()).setRowCount(table.getRowCount() - 1);
        
        // Return to read mode as if [Enter] key pressed.
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        setFormMode(FormMode.NormalMode);  
        cancelButton.setEnabled(false);
    }

    private void prepareModification(JTable table, JButton modifyButton, JButton cancelButton) 
    {
        int rowIndex = table.getSelectedRow();
        
        if (table.editCellAt(rowIndex, 1))
        {
            table.getEditorComponent().requestFocus();        
            
            int model_index = table.convertRowIndexToModel(rowIndex);
            TableModel model = table.getModel();

            //<editor-fold desc="-- Save previous record field value">
            switch(((RXTable)table).getTableType()) {
                case L1_TABLE:
                    prevL1Name = model.getValueAt(model_index, 1);
                    break;
                case L2_TABLE:
                    prevL2Name = model.getValueAt(model_index, 1);
                    break;
                case Building:
                    prevBldgNo = model.getValueAt(model_index, 1);
                    break;
                case UnitTab:
                    prevUnitNo = model.getValueAt(model_index, 1);
                    break;
                default:
                    break;
            }
            //</editor-fold>
            
            if (model.getValueAt(model_index, 0) != null)
            {
                String dialogTitle = null;
                String dialog = "";
                TableType tableType = ((RXTable)table).getTableType();

                //<editor-fold desc="-- Make dialog title and content">
                switch (tableType) {
                        
                    case UnitTab:
                        int bIndex = BuildingTable.convertRowIndexToModel(
                                BuildingTable.getSelectedRow());
                        int bldgNo = (Integer)BuildingTable.getModel().getValueAt(bIndex, 1);
                        
                        dialogTitle = UNIT_MODIFY_DIALOGTITLE.getContent();
                        dialog = UNIT_DIAG_L1.getContent() + System.getProperty("line.separator") 
                                + UNIT_DIAG_L2.getContent() + prevUnitNo + System.getProperty("line.separator") 
                                + UNIT_DIAG_L3.getContent() + bldgNo;
                        break;
                        
                    case Building:
                        int modal_Index = BuildingTable.convertRowIndexToModel(rowIndex);        
                        int bno = (Integer)BuildingTable.getModel().getValueAt(modal_Index, 2);        
                        int count = getUnitCount(bno);                        
                        
                        dialogTitle = BUILDING_MODIFY_DIALOGTITLE.getContent();
                        dialog = BLDG_DIAG_L1.getContent() + System.getProperty("line.separator") 
                                + BLDG_DIAG_L2.getContent() + prevBldgNo + " " 
                                + BLDG_DIAG_L3.getContent() + count + ")";
                        break;
                    
                    case L2_TABLE:
                        int indexL1 = L1_Affiliation.convertRowIndexToModel(L1_Affiliation.getSelectedRow());
                        String nameL1 = L1_Affiliation.getModel().getValueAt(indexL1, 1).toString();
                        
                        dialogTitle = LOWER_MODIFY_DIALOGTITLE.getContent();
                        dialog = AFFILI2_DIAG_L1.getContent() + System.getProperty("line.separator") 
                                + AFFILI2_DIAG_L2.getContent() + prevL2Name + System.getProperty("line.separator") 
                                + AFFILI2_DIAG_L3.getContent() + nameL1;
                        break;

                    case L1_TABLE:
                        int L1_no = (int)model.getValueAt(model_index, 2);
                        
                        dialogTitle = AFFILIATION_MODIFY_DIALOGTITLE.getContent();
                        dialog = AFFILI_DIAG_L1.getContent() + System.getProperty("line.separator") 
                                + AFFILI_DIAG_L2.getContent() + prevL1Name + System.getProperty("line.separator") 
                                + AFFILI_DIAG_L3.getContent() + getL2RecordCount(L1_no); 
                        break;

                    default:
                        break;
                }
                //</editor-fold>
                
                int result = JOptionPane.showConfirmDialog(this, dialog,
                        dialogTitle, JOptionPane.YES_NO_OPTION); 

                if (result == JOptionPane.NO_OPTION) { 
                    table.getCellEditor().stopCellEditing();
                } else {
                    setFormMode(FormMode.UpdateMode);
                    modifyButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                }
            }
        }
    }

    DefaultTableCellRenderer bnoCellRenderer = new DefaultTableCellRenderer() {
        Border padding = BorderFactory.createEmptyBorder(0, 15, 0, 15);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) 
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
            setHorizontalAlignment(JLabel.RIGHT);
            return this;            
        }
    };

    private void addDummyFirstRow(DefaultTableModel model) {
        if (model.getRowCount() == 0) {
            model.addRow(new Object[] {"", NONE_EXIST.getContent(), ""
            });
        }
    }

    /**
     * 
     * @param rowIndex
     * @param data
     * @param seqNo
     * @param sql
     * @param excepMsg
     * @param BuildingTable
     * @param content
     * @return 
     */
    private int updateBuildingUnit(int rowIndex, int data, int seqNo,
            String sql, String excepMsg, JTable BuildingTable, String content) 
    {
        Connection conn = null;
        PreparedStatement updateStmt = null;
        int result = ER_YES;
        
        try {
            conn = getConnection();
            updateStmt = conn.prepareStatement(sql);
            updateStmt.setInt(1, data); 
            updateStmt.setInt(2, seqNo);
            if (updateStmt.executeUpdate() == 1)  {
                result = ER_NO;
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                result = ER_DUP_ENTRY;
                logParkingException(Level.SEVERE, ex, data + " already existing building");
            }
            else {
                logParkingException(Level.SEVERE, ex, "(Insertion failed building : " + data);
            }
        } finally {
            closeDBstuff(conn, updateStmt, null, excepMsg);
            return result;
        }    
    }

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

                        switch (language) {
                            case KOREAN:
                                sb.append("아래 자료가 식별되었습니다. 로딩을 계속합니까?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -자료: 상위소속 " + level1_total.getValue());
                                sb.append("건, 하위소속 " + level2_total.getValue() + "건");
                                break;
                                
                            case ENGLISH:
                                sb.append("Below Data Recognized. Want to continue loading?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -Data: Higher Affiliation count: " + level1_total.getValue());
                                sb.append(", Lower Affiliation count: " + level2_total.getValue());
                                break;
                                
                            default:
                                break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                READ_ODS_DIALOGTITLE.getContent(), 
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

    private void loadBuildingsFromODS() {
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
                
                    ArrayList<Point> wrongCells = new ArrayList<Point>();
                    WrappedInt buildingTotal = new WrappedInt();
                    WrappedInt unitTotal = new WrappedInt();

                    if (objODSReader.checkODS(sheet, wrongCells, buildingTotal, unitTotal))
                    {
                        StringBuilder sb = new StringBuilder();

                        switch (language) {
                            case KOREAN:
                                sb.append("아래 자료가 식별되었습니다. 로딩을 계속합니까?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -자료: 건물 번호 " + buildingTotal.getValue());
                                sb.append("건, 호실 번호 " + unitTotal.getValue() + "건");
                                break;
                                
                            case ENGLISH:
                                sb.append("Below Data Recognized. Want to continue loading?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" - Data: Buildings count: " + buildingTotal.getValue());
                                sb.append(", Room count: " + unitTotal.getValue());
                                break;
                                
                            default:
                                break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                READ_ODS_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readODS(sheet, this);
                            //loadBuilding(0, 0);
                        }
                    } else {
                        // display wrong cell points if existed
                        if (wrongCells.size() > 0) {
                            String dialog = "";
                            
                            switch (language) {
                                case KOREAN:
                                    dialog = "다음 셀에서 숫자 이외의 자료가 탐지됨" 
                                                + System.getProperty("line.separator") 
                                                + getWrongCellPointString(wrongCells);
                                    break;
                                    
                                case ENGLISH:
                                    dialog = "Cells containing data other than numbers" 
                                                + System.getProperty("line.separator") 
                                                + getWrongCellPointString(wrongCells);
                                    break;
                                    
                                default:
                                    break;
                            } 
                            
                            JOptionPane.showConfirmDialog(null, dialog,
                                    READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                      
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(User action: read user list ods file sheet)");
        }            
    }

    private void abortModification(String msg, int row, JTable table) {
        JOptionPane.showConfirmDialog(null, msg,
                ERROR_DIALOGTITLE.getContent(), 
                JOptionPane.WARNING_MESSAGE, WARNING_MESSAGE);                       
        if (table.editCellAt(row, 1)) {
            table.getEditorComponent().requestFocus();
        }      
    }

    private class HdrMouseListener extends MouseAdapter {
        JTable table = null;
        private HdrMouseListener(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent evt) {
            selectRadioButtonFor(((RXTable) table).getTableType());
        }
    }
}
