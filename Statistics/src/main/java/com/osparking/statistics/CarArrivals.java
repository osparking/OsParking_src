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
package com.osparking.statistics;

import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.bigButtonHeight;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.normGUIwidth;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.tipColor;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.SEARCH_PERIOD;
import static com.osparking.global.names.DB_Access.parkingLotLocale;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLEAR_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.FIX_IT_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SEARCH_BTN;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMessages.DATE_INPUT_CHECK_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.DATE_INPUT_ERROR_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.WARING_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ARRIVAL_TIME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ARR_TM_LEGEND;
import static com.osparking.global.names.ControlEnums.LabelContent.ATTENDANT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BAR_OP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BUILDING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CAR_TAG_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DURATION_SET_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LAST_1HOUR_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LAST_24HOURS_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_TARGET_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NON_REGI_TAG2_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.RECOGNIZED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REGISTERED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ROOM_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_PURPOSE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.WHERE_TO_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.ControlEnums.TitleTypes.*;
import static com.osparking.global.names.ControlEnums.TableTypes.ARRIVAL_TIME_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.CAR_TAG_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TextType.LOG_OUT_TF;
import static com.osparking.global.names.ControlEnums.TextType.NOT_APPLICABLE_TF;
import static com.osparking.global.names.ControlEnums.TextType.UNKNOWN_TF;
import static com.osparking.global.names.ControlEnums.TextType.UNREGISTERED_TF;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CAR_TAG_TF_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CLEAR_BTN_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.FIX_IT_BTN_TOOLTIP;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.gateNames;
import com.osparking.global.names.ImageDisplay;
import com.osparking.global.names.InnoComboBoxItem;
import com.osparking.global.names.JDBCMySQL;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.BarOperation;
import static com.osparking.global.names.OSP_enums.BarOperation.REGISTERED_CAR_OPENED;
import static com.osparking.global.names.OSP_enums.BarOperation.AUTO_OPENED;
import static com.osparking.global.names.OSP_enums.BarOperation.MANUAL;
import static com.osparking.global.names.OSP_enums.BarOperation.REMAIN_CLOSED;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.OSP_enums.SearchPeriod;
import static com.osparking.vehicle.CommonData.CABH_NORM;
import static com.osparking.vehicle.CommonData.CABW_NORM;
import static com.osparking.vehicle.CommonData.CABW_WIDE;
import static com.osparking.vehicle.driver.ManageDrivers.mayPropagateBackward;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Open Source Parking Inc.
 */
public class CarArrivals extends javax.swing.JFrame {
    BufferedImage originalImg = null;
    ListSelectionListener valueChangeListener = null; 
    
    /**
     * Keys of parent combobox items for which their children combobox 
     * item list is formed -- comboboxes in the search panel area.
     */
    private int[] prevParentSKey = new int[OSP_enums.DriverCol.values().length];    
    
    /**
     * Creates new form CarArrivals
     */
    public CarArrivals() {
        initComponents();
        
        BeginDateChooser.setLocale(parkingLotLocale);
        EndDateChooser.setLocale(parkingLotLocale);    
        loadSearchControls();
        setIconImages(OSPiconList);
            
        if (SEARCH_PERIOD == SearchPeriod.OneHour.ordinal())
            oneHourRadioButton.setSelected(true);
        else if (SEARCH_PERIOD == SearchPeriod.OneDay.ordinal())
            oneDayRadioButton.setSelected(true);
        else {
            periodRadioButton.setSelected(true);
            periodRadioButtonActionPerformed(null);
        }
            
        detailTuneTableProperties();
        loadArrivalsListTable(false);
        affiliationRadioButtonActionPerformed(null);
        carTagTF.requestFocus();
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

        periodOptionGroup = new javax.swing.ButtonGroup();
        affiliationGroup = new javax.swing.ButtonGroup();
        wholeTop = new javax.swing.JPanel();
        wholeEast = new javax.swing.JPanel();
        wholePanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        titleLabel = new javax.swing.JLabel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        myMetaKeyLabel = new javax.swing.JLabel();
        filler_h10_18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        topPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        criteriaPanel = new javax.swing.JPanel();
        searchTop = new javax.swing.JPanel();
        topVehicle = new javax.swing.JPanel();
        carTagPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        carTagTF = new javax.swing.JTextField();
        carPlus = new javax.swing.JPanel();
        gatePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        gateCB = new javax.swing.JComboBox();
        attendPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        attendantCB = new javax.swing.JComboBox();
        barOptnPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        gateBarCB = new javax.swing.JComboBox();
        topDriver = new javax.swing.JPanel();
        affiliPanel = new javax.swing.JPanel();
        affiliationRadioButton = new javax.swing.JRadioButton();
        searchL1ComboBox = new PComboBox();
        searchL2ComboBox = new PComboBox<InnoComboBoxItem>();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        buildingPanel = new javax.swing.JPanel();
        buildingRadioButton = new javax.swing.JRadioButton();
        searchBuildingComboBox = new PComboBox();
        searchUnitComboBox = new PComboBox();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        clearPanel = new javax.swing.JPanel();
        clearSearchPropertiesButton = new javax.swing.JButton();
        searchBottom = new javax.swing.JPanel();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        oneHourRadioButton = new javax.swing.JRadioButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        oneDayRadioButton = new javax.swing.JRadioButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        periodRadioButton = new javax.swing.JRadioButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        BeginDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        EndDateChooser = new com.toedter.calendar.JDateChooser();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        fixPanel = new javax.swing.JPanel();
        setSearchPeriodOptionButton = new javax.swing.JButton();
        searchButtonPanel = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        filler_h10_19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        bottomPanel = new javax.swing.JPanel();
        detailWhole = new javax.swing.JPanel();
        detailTop = new javax.swing.JPanel();
        gateNoPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        rowNumTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        gateNameTF = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        barOptnTF = new javax.swing.JTextField();
        dateTmPanel = new javax.swing.JPanel();
        colHead1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        arrivalTmTF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        attendantTF = new javax.swing.JTextField();
        tagNoPanel = new javax.swing.JPanel();
        colHead2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        recognizedTF = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        registeredTF = new javax.swing.JTextField();
        detailBottom = new javax.swing.JPanel();
        imagePanel = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        targetPanel = new javax.swing.JPanel();
        whereToLabel = new javax.swing.JLabel();
        buildingLabel = new javax.swing.JLabel();
        buildingTF = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        unitTF = new javax.swing.JTextField();
        affiliPan = new javax.swing.JPanel();
        affiliationLabel = new javax.swing.JLabel();
        affiliationTF = new javax.swing.JTextField();
        targetDescrp = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        visitPurposeTF = new javax.swing.JTextField();
        bottomRightPanel = new javax.swing.JPanel();
        arrivalListPanel = new javax.swing.JPanel();
        arrivalsScroPan = new javax.swing.JScrollPane();
        arrivalsList = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        countPanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveSheet_Button = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        wholeBottom = new javax.swing.JPanel();
        wholeWest = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(CAR_ARRIVALS_FRAME_TITLE.getContent());
        setFocusCycleRoot(false);
        setMinimumSize(new Dimension(normGUIwidth, normGUIheight));
        setPreferredSize(new Dimension(normGUIwidth, normGUIheight));
        setSize(new Dimension(normGUIwidth, normGUIheight));

        wholeTop.setMaximumSize(new java.awt.Dimension(32767, 40));
        wholeTop.setMinimumSize(new java.awt.Dimension(0, 40));
        wholeTop.setPreferredSize(new java.awt.Dimension(0, 40));

        javax.swing.GroupLayout wholeTopLayout = new javax.swing.GroupLayout(wholeTop);
        wholeTop.setLayout(wholeTopLayout);
        wholeTopLayout.setHorizontalGroup(
            wholeTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        wholeTopLayout.setVerticalGroup(
            wholeTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        getContentPane().add(wholeTop, java.awt.BorderLayout.NORTH);

        wholeEast.setMaximumSize(new java.awt.Dimension(40, 32767));
        wholeEast.setMinimumSize(new java.awt.Dimension(40, 0));
        wholeEast.setPreferredSize(new java.awt.Dimension(40, 0));

        javax.swing.GroupLayout wholeEastLayout = new javax.swing.GroupLayout(wholeEast);
        wholeEast.setLayout(wholeEastLayout);
        wholeEastLayout.setHorizontalGroup(
            wholeEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        wholeEastLayout.setVerticalGroup(
            wholeEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(wholeEast, java.awt.BorderLayout.EAST);

        wholePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        titlePanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        titlePanel.setMinimumSize(new java.awt.Dimension(110, 40));
        titlePanel.setPreferredSize(new java.awt.Dimension(210, 40));
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        seeLicenseButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setPreferredSize(new java.awt.Dimension(90, 40));
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        titlePanel.add(seeLicenseButton);
        titlePanel.add(filler16);

        titleLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(CAR_ARRIVALS_FRAME_TITLE.getContent());
        titleLabel.setMaximumSize(new java.awt.Dimension(200, 28));
        titleLabel.setMinimumSize(new java.awt.Dimension(200, 28));
        titleLabel.setPreferredSize(new java.awt.Dimension(200, 28));
        titlePanel.add(titleLabel);
        titlePanel.add(filler17);

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setPreferredSize(new java.awt.Dimension(90, 40));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        titlePanel.add(myMetaKeyLabel);

        wholePanel.add(titlePanel);
        wholePanel.add(filler_h10_18);

        topPanel.setMaximumSize(new java.awt.Dimension(2147483647, 270));
        topPanel.setMinimumSize(new java.awt.Dimension(810, 290));
        topPanel.setPreferredSize(new java.awt.Dimension(0, 290));
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchPanel.setBackground(new java.awt.Color(243, 243, 243));
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, SEARCH_CRITERIA_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchPanel.setAlignmentX(0.0F);
        searchPanel.setMinimumSize(new java.awt.Dimension(800, 280));
        searchPanel.setPreferredSize(new java.awt.Dimension(900, 280));

        criteriaPanel.setBackground(new java.awt.Color(243, 243, 243));
        criteriaPanel.setMinimumSize(new java.awt.Dimension(800, 255));
        criteriaPanel.setPreferredSize(new java.awt.Dimension(800, 255));

        searchTop.setBackground(new java.awt.Color(243, 243, 243));
        searchTop.setBorder(javax.swing.BorderFactory.createTitledBorder(null, ARRIVAL_PROPERTIES_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchTop.setForeground(new java.awt.Color(255, 255, 255));
        searchTop.setAlignmentX(0.0F);
        searchTop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        searchTop.setMaximumSize(new java.awt.Dimension(32767, 145));
        searchTop.setMinimumSize(new java.awt.Dimension(800, 145));
        searchTop.setPreferredSize(new java.awt.Dimension(800, 145));

        topVehicle.setMinimumSize(new java.awt.Dimension(340, 94));
        topVehicle.setPreferredSize(new java.awt.Dimension(340, 94));
        topVehicle.setLayout(new javax.swing.BoxLayout(topVehicle, javax.swing.BoxLayout.Y_AXIS));

        carTagPanel.setBackground(new java.awt.Color(243, 243, 243));
        carTagPanel.setPreferredSize(new java.awt.Dimension(122, 39));
        carTagPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel2.setBackground(new java.awt.Color(243, 243, 243));
        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setLabelFor(carTagTF);
        jLabel2.setText(CAR_TAG_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(32767, 32767));
        carTagPanel.add(jLabel2);

        carTagTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        carTagTF.setToolTipText(CAR_TAG_TF_TOOLTIP.getContent());
        carTagTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        carTagTF.setMinimumSize(new java.awt.Dimension(110, 28));
        carTagTF.setPreferredSize(new java.awt.Dimension(110, 28));
        carTagPanel.add(carTagTF);

        topVehicle.add(carTagPanel);

        carPlus.setBackground(new java.awt.Color(243, 243, 243));
        carPlus.setMinimumSize(new java.awt.Dimension(340, 50));
        carPlus.setPreferredSize(new java.awt.Dimension(320, 55));
        carPlus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        gatePanel.setBackground(new java.awt.Color(243, 243, 243));
        gatePanel.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        gatePanel.setPreferredSize(new java.awt.Dimension(82, 50));
        gatePanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setBackground(new java.awt.Color(243, 243, 243));
        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setLabelFor(gateCB);
        jLabel1.setText(GATE_NAME_LABEL.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(32767, 32767));
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gatePanel.add(jLabel1, java.awt.BorderLayout.NORTH);

        gateCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        gateCB.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        gateCB.setPreferredSize(new Dimension(CABW_NORM, CABH_NORM));
        gatePanel.add(gateCB, java.awt.BorderLayout.CENTER);

        carPlus.add(gatePanel);

        attendPanel.setBackground(new java.awt.Color(243, 243, 243));
        attendPanel.setPreferredSize(new java.awt.Dimension(122, 50));
        attendPanel.setLayout(new java.awt.BorderLayout());

        jLabel15.setBackground(new java.awt.Color(243, 243, 243));
        jLabel15.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setLabelFor(attendantCB);
        jLabel15.setText(ATTENDANT_LABEL.getContent());
        jLabel15.setMaximumSize(new java.awt.Dimension(32767, 32767));
        jLabel15.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        attendPanel.add(jLabel15, java.awt.BorderLayout.NORTH);

        attendantCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        attendantCB.setModel(new javax.swing.DefaultComboBoxModel(new Object[] {
            new ConvComboBoxItem("", ATTENDANT_CB_ITEM.getContent())
        }));
        attendantCB.setMinimumSize(new Dimension(CABW_WIDE, CABH_NORM));
        attendantCB.setPreferredSize(new Dimension(CABW_WIDE, CABH_NORM));
        attendantCB.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                attendantCBPopupMenuWillBecomeVisible(evt);
            }
        });
        attendPanel.add(attendantCB, java.awt.BorderLayout.CENTER);

        carPlus.add(attendPanel);

        barOptnPanel.setBackground(new java.awt.Color(243, 243, 243));
        barOptnPanel.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        barOptnPanel.setPreferredSize(new java.awt.Dimension(90, 50));
        barOptnPanel.setLayout(new java.awt.BorderLayout(10, 0));

        jLabel20.setBackground(new java.awt.Color(243, 243, 243));
        jLabel20.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText(BAR_OP_LABEL.getContent());
        jLabel20.setMaximumSize(new java.awt.Dimension(32767, 32767));
        jLabel20.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        barOptnPanel.add(jLabel20, java.awt.BorderLayout.NORTH);

        gateBarCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateBarCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        gateBarCB.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        gateBarCB.setPreferredSize(new Dimension(CABW_NORM, CABH_NORM));
        barOptnPanel.add(gateBarCB, java.awt.BorderLayout.CENTER);

        carPlus.add(barOptnPanel);

        topVehicle.add(carPlus);

        topDriver.setBackground(new java.awt.Color(243, 243, 243));
        topDriver.setDoubleBuffered(false);
        topDriver.setMaximumSize(new java.awt.Dimension(1000, 120));
        topDriver.setMinimumSize(new java.awt.Dimension(280, 94));
        topDriver.setPreferredSize(new java.awt.Dimension(280, 94));

        affiliPanel.setBackground(new java.awt.Color(243, 243, 243));
        affiliPanel.setMinimumSize(new java.awt.Dimension(120, 94));
        affiliPanel.setPreferredSize(new java.awt.Dimension(120, 94));
        affiliPanel.setRequestFocusEnabled(false);

        affiliationRadioButton.setBackground(new java.awt.Color(243, 243, 243));
        affiliationGroup.add(affiliationRadioButton);
        affiliationRadioButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        affiliationRadioButton.setText(AFFILIATION_LABEL.getContent());
        affiliationRadioButton.setAlignmentX(0.5F);
        affiliationRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        affiliationRadioButton.setMargin(new java.awt.Insets(2, 20, 2, 2));
        affiliationRadioButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        affiliationRadioButton.setMinimumSize(new java.awt.Dimension(100, 23));
        affiliationRadioButton.setPreferredSize(new java.awt.Dimension(100, 23));
        affiliationRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                affiliationRadioButtonActionPerformed(evt);
            }
        });

        searchL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchL1ComboBox.setMinimumSize(new Dimension(CABW_WIDE, CABH_NORM));
        searchL1ComboBox.setPreferredSize(new Dimension(CABW_WIDE, CABH_NORM));
        searchL1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchL1ComboBoxActionPerformed(evt);
            }
        });
        searchL1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchL1ComboBoxActionPerformed(evt);
            }
        });

        searchL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL2ComboBox.setModel(    new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchL2ComboBox.setMinimumSize(new Dimension(CABW_WIDE, CABH_NORM));
        searchL2ComboBox.setPreferredSize(new Dimension(CABW_WIDE, CABH_NORM));
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
        searchL2ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchL2ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout affiliPanelLayout = new javax.swing.GroupLayout(affiliPanel);
        affiliPanel.setLayout(affiliPanelLayout);
        affiliPanelLayout.setHorizontalGroup(
            affiliPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(affiliationRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(affiliPanelLayout.createSequentialGroup()
                .addGroup(affiliPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchL1ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchL2ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        affiliPanelLayout.setVerticalGroup(
            affiliPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(affiliPanelLayout.createSequentialGroup()
                .addComponent(affiliationRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchL1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchL2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buildingPanel.setBackground(new java.awt.Color(243, 243, 243));
        buildingPanel.setMinimumSize(new java.awt.Dimension(120, 94));
        buildingPanel.setPreferredSize(new java.awt.Dimension(120, 94));

        buildingRadioButton.setBackground(new java.awt.Color(243, 243, 243));
        affiliationGroup.add(buildingRadioButton);
        buildingRadioButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        buildingRadioButton.setText(BUILDING_LABEL.getContent());
        buildingRadioButton.setAlignmentX(0.5F);
        buildingRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buildingRadioButton.setMargin(new java.awt.Insets(2, 20, 2, 2));
        buildingRadioButton.setMaximumSize(new java.awt.Dimension(32767, 32767));
        buildingRadioButton.setMinimumSize(new java.awt.Dimension(80, 21));
        buildingRadioButton.setPreferredSize(new java.awt.Dimension(80, 23));
        buildingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildingRadioButtonActionPerformed(evt);
            }
        });

        searchBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBuildingComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchBuildingComboBox.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        searchBuildingComboBox.setPreferredSize(new Dimension(CABW_NORM, CABH_NORM));
        searchBuildingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBuildingComboBoxActionPerformed(evt);
            }
        });

        searchUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchUnitComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchUnitComboBox.setMinimumSize(new Dimension(CABW_NORM, CABH_NORM));
        searchUnitComboBox.setPreferredSize(new Dimension(CABW_NORM, CABH_NORM));
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
        searchUnitComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchUnitComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buildingPanelLayout = new javax.swing.GroupLayout(buildingPanel);
        buildingPanel.setLayout(buildingPanelLayout);
        buildingPanelLayout.setHorizontalGroup(
            buildingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buildingRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(searchBuildingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(searchUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        buildingPanelLayout.setVerticalGroup(
            buildingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buildingPanelLayout.createSequentialGroup()
                .addComponent(buildingRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchBuildingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout topDriverLayout = new javax.swing.GroupLayout(topDriver);
        topDriver.setLayout(topDriverLayout);
        topDriverLayout.setHorizontalGroup(
            topDriverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topDriverLayout.createSequentialGroup()
                .addComponent(affiliPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(filler19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(buildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        topDriverLayout.setVerticalGroup(
            topDriverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(affiliPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(filler19, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        clearPanel.setBackground(new java.awt.Color(243, 243, 243));
        clearPanel.setMaximumSize(new java.awt.Dimension(111, 120));
        clearPanel.setMinimumSize(new java.awt.Dimension(100, 94));
        clearPanel.setPreferredSize(new java.awt.Dimension(100, 94));
        clearPanel.setLayout(new javax.swing.BoxLayout(clearPanel, javax.swing.BoxLayout.LINE_AXIS));

        clearSearchPropertiesButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearSearchPropertiesButton.setMnemonic('l');
        clearSearchPropertiesButton.setText(CLEAR_BTN.getContent());
        clearSearchPropertiesButton.setToolTipText(CLEAR_BTN_TOOLTIP.getContent());
        clearSearchPropertiesButton.setMaximumSize(new java.awt.Dimension(100, 35));
        clearSearchPropertiesButton.setMinimumSize(new java.awt.Dimension(100, 35));
        clearSearchPropertiesButton.setPreferredSize(new java.awt.Dimension(100, 35));
        clearSearchPropertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSearchPropertiesButtonActionPerformed(evt);
            }
        });
        clearPanel.add(clearSearchPropertiesButton);

        javax.swing.GroupLayout searchTopLayout = new javax.swing.GroupLayout(searchTop);
        searchTop.setLayout(searchTopLayout);
        searchTopLayout.setHorizontalGroup(
            searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchTopLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(topVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(topDriver, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(filler18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(clearPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        searchTopLayout.setVerticalGroup(
            searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchTopLayout.createSequentialGroup()
                .addGroup(searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(topVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(topDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(filler18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(clearPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        searchBottom.setBackground(new java.awt.Color(244, 244, 244));
        searchBottom.setBorder(javax.swing.BorderFactory.createTitledBorder(null, ARRIVAL_TIME_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchBottom.setForeground(new java.awt.Color(153, 153, 153));
        searchBottom.setAlignmentX(0.0F);
        searchBottom.setAlignmentY(0.0F);
        searchBottom.setFocusTraversalPolicyProvider(true);
        searchBottom.setMaximumSize(new java.awt.Dimension(32767, 90));
        searchBottom.setMinimumSize(new java.awt.Dimension(800, 90));
        searchBottom.setPreferredSize(new java.awt.Dimension(800, 90));

        periodOptionGroup.add(oneHourRadioButton);
        oneHourRadioButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        oneHourRadioButton.setSelected(true);
        oneHourRadioButton.setText(LAST_1HOUR_LABEL.getContent());
        oneHourRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneHourRadioButtonActionPerformed(evt);
            }
        });

        periodOptionGroup.add(oneDayRadioButton);
        oneDayRadioButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        oneDayRadioButton.setText(LAST_24HOURS_LABEL.getContent());
        oneDayRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneDayRadioButtonActionPerformed(evt);
            }
        });

        periodOptionGroup.add(periodRadioButton);
        periodRadioButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        periodRadioButton.setText(DURATION_SET_LABEL.getContent());
        periodRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                periodRadioButtonActionPerformed(evt);
            }
        });

        BeginDateChooser.setBackground(new java.awt.Color(243, 243, 243));
        BeginDateChooser.setAlignmentY(0.0F);
        BeginDateChooser.setAutoscrolls(true);
        BeginDateChooser.setEnabled(false);
        BeginDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BeginDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        BeginDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));

        jLabel9.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("~");
        jLabel9.setFocusable(false);
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        EndDateChooser.setBackground(new java.awt.Color(243, 243, 243));
        EndDateChooser.setAlignmentY(0.0F);
        EndDateChooser.setAutoscrolls(true);
        EndDateChooser.setEnabled(false);
        EndDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EndDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        EndDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));

        fixPanel.setBackground(new java.awt.Color(243, 243, 243));
        fixPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        fixPanel.setPreferredSize(new java.awt.Dimension(100, 40));

        setSearchPeriodOptionButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        setSearchPeriodOptionButton.setMnemonic('f');
        setSearchPeriodOptionButton.setText(FIX_IT_BTN.getContent());
        setSearchPeriodOptionButton.setToolTipText(FIX_IT_BTN_TOOLTIP.getContent());
        setSearchPeriodOptionButton.setMaximumSize(new java.awt.Dimension(100, 35));
        setSearchPeriodOptionButton.setMinimumSize(new java.awt.Dimension(100, 35));
        setSearchPeriodOptionButton.setPreferredSize(new java.awt.Dimension(100, 35));
        setSearchPeriodOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSearchPeriodOptionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fixPanelLayout = new javax.swing.GroupLayout(fixPanel);
        fixPanel.setLayout(fixPanelLayout);
        fixPanelLayout.setHorizontalGroup(
            fixPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(setSearchPeriodOptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        fixPanelLayout.setVerticalGroup(
            fixPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(setSearchPeriodOptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout searchBottomLayout = new javax.swing.GroupLayout(searchBottom);
        searchBottom.setLayout(searchBottomLayout);
        searchBottomLayout.setHorizontalGroup(
            searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(filler21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(oneHourRadioButton)
                .addGap(5, 5, 5)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(oneDayRadioButton)
                .addGap(5, 5, 5)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(periodRadioButton)
                .addGap(5, 5, 5)
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(BeginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel9)
                .addGap(5, 5, 5)
                .addComponent(EndDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(filler20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(fixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );
        searchBottomLayout.setVerticalGroup(
            searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(oneHourRadioButton))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(oneDayRadioButton))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(periodRadioButton))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(BeginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel9))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(EndDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(fixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout criteriaPanelLayout = new javax.swing.GroupLayout(criteriaPanel);
        criteriaPanel.setLayout(criteriaPanelLayout);
        criteriaPanelLayout.setHorizontalGroup(
            criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(criteriaPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchTop, javax.swing.GroupLayout.DEFAULT_SIZE, 811, Short.MAX_VALUE)
                    .addComponent(searchBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 811, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        criteriaPanelLayout.setVerticalGroup(
            criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(criteriaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        searchButtonPanel.setBackground(new java.awt.Color(243, 243, 243));
        searchButtonPanel.setMaximumSize(new java.awt.Dimension(90, 250));
        searchButtonPanel.setMinimumSize(new java.awt.Dimension(90, 250));
        searchButtonPanel.setPreferredSize(new java.awt.Dimension(90, 250));
        searchButtonPanel.setLayout(new javax.swing.BoxLayout(searchButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        searchButton.setMaximumSize(new java.awt.Dimension(90, 60));
        searchButton.setMinimumSize(new java.awt.Dimension(90, 60));
        searchButton.setPreferredSize(new java.awt.Dimension(90, 60));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        searchButtonPanel.add(searchButton);

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(criteriaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 811, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(criteriaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        topPanel.add(searchPanel);

        wholePanel.add(topPanel);
        wholePanel.add(filler_h10_19);

        bottomPanel.setPreferredSize(new java.awt.Dimension(965, 400));
        bottomPanel.setLayout(new javax.swing.BoxLayout(bottomPanel, javax.swing.BoxLayout.LINE_AXIS));

        detailWhole.setBorder(javax.swing.BorderFactory.createTitledBorder(null, VEHICLE_ARIIVAL_DETAILS_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        detailWhole.setMaximumSize(new java.awt.Dimension(32767, 163835));
        detailWhole.setMinimumSize(new java.awt.Dimension(400, 195));
        detailWhole.setPreferredSize(new java.awt.Dimension(650, 445));

        detailTop.setPreferredSize(new java.awt.Dimension(536, 125));
        detailTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        gateNoPanel.setMaximumSize(new java.awt.Dimension(196602, 35));
        gateNoPanel.setMinimumSize(new java.awt.Dimension(175, 120));
        gateNoPanel.setPreferredSize(new java.awt.Dimension(175, 120));
        gateNoPanel.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(ORDER_LABEL.getContent());
        jLabel3.setMaximumSize(null);
        jLabel3.setMinimumSize(new java.awt.Dimension(65, 28));
        jLabel3.setPreferredSize(new java.awt.Dimension(65, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 3, 0);
        gateNoPanel.add(jLabel3, gridBagConstraints);

        rowNumTF.setEditable(false);
        rowNumTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rowNumTF.setMaximumSize(new java.awt.Dimension(50, 35));
        rowNumTF.setMinimumSize(new java.awt.Dimension(100, 28));
        rowNumTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 3, 10);
        gateNoPanel.add(rowNumTF, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(GATE_NAME_LABEL.getContent());
        jLabel4.setMaximumSize(null);
        jLabel4.setMinimumSize(new java.awt.Dimension(65, 28));
        jLabel4.setPreferredSize(new java.awt.Dimension(65, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        gateNoPanel.add(jLabel4, gridBagConstraints);

        gateNameTF.setEditable(false);
        gateNameTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateNameTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        gateNameTF.setMinimumSize(new java.awt.Dimension(100, 28));
        gateNameTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 10);
        gateNoPanel.add(gateNameTF, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText(BAR_OP_LABEL.getContent());
        jLabel12.setMaximumSize(null);
        jLabel12.setMinimumSize(new java.awt.Dimension(65, 28));
        jLabel12.setPreferredSize(new java.awt.Dimension(65, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        gateNoPanel.add(jLabel12, gridBagConstraints);

        barOptnTF.setEditable(false);
        barOptnTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        barOptnTF.setMaximumSize(new java.awt.Dimension(120, 35));
        barOptnTF.setMinimumSize(new java.awt.Dimension(100, 28));
        barOptnTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 10);
        gateNoPanel.add(barOptnTF, gridBagConstraints);

        detailTop.add(gateNoPanel);

        dateTmPanel.setMinimumSize(new java.awt.Dimension(165, 120));
        dateTmPanel.setPreferredSize(new java.awt.Dimension(165, 120));
        dateTmPanel.setLayout(new java.awt.GridBagLayout());

        colHead1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        colHead1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colHead1.setText("M.D H:M");
        colHead1.setMaximumSize(new java.awt.Dimension(80, 28));
        colHead1.setMinimumSize(new java.awt.Dimension(80, 28));
        colHead1.setPreferredSize(new java.awt.Dimension(80, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        dateTmPanel.add(colHead1, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(ARRIVAL_TIME_LABEL.getContent());
        jLabel5.setMaximumSize(null);
        jLabel5.setMinimumSize(new java.awt.Dimension(55, 28));
        jLabel5.setPreferredSize(new java.awt.Dimension(55, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        dateTmPanel.add(jLabel5, gridBagConstraints);

        arrivalTmTF.setEditable(false);
        arrivalTmTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        arrivalTmTF.setToolTipText("");
        arrivalTmTF.setMaximumSize(new java.awt.Dimension(100, 35));
        arrivalTmTF.setMinimumSize(new java.awt.Dimension(100, 28));
        arrivalTmTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        dateTmPanel.add(arrivalTmTF, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(ATTENDANT_LABEL.getContent());
        jLabel6.setMaximumSize(null);
        jLabel6.setMinimumSize(new java.awt.Dimension(55, 28));
        jLabel6.setPreferredSize(new java.awt.Dimension(55, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        dateTmPanel.add(jLabel6, gridBagConstraints);

        attendantTF.setEditable(false);
        attendantTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        attendantTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        attendantTF.setMinimumSize(new java.awt.Dimension(100, 28));
        attendantTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        dateTmPanel.add(attendantTF, gridBagConstraints);

        detailTop.add(dateTmPanel);

        tagNoPanel.setMinimumSize(new java.awt.Dimension(165, 120));
        tagNoPanel.setPreferredSize(new java.awt.Dimension(165, 120));
        tagNoPanel.setLayout(new java.awt.GridBagLayout());

        colHead2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        colHead2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colHead2.setText("Tag No.");
        colHead2.setMaximumSize(new java.awt.Dimension(80, 28));
        colHead2.setMinimumSize(new java.awt.Dimension(80, 28));
        colHead2.setPreferredSize(new java.awt.Dimension(80, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        tagNoPanel.add(colHead2, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(RECOGNIZED_LABEL.getContent());
        jLabel8.setMaximumSize(new java.awt.Dimension(65, 28));
        jLabel8.setMinimumSize(new java.awt.Dimension(55, 28));
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        tagNoPanel.add(jLabel8, gridBagConstraints);

        recognizedTF.setEditable(false);
        recognizedTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        recognizedTF.setText("서울32가5555");
        recognizedTF.setMaximumSize(new java.awt.Dimension(130, 35));
        recognizedTF.setMinimumSize(new java.awt.Dimension(100, 28));
        recognizedTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        tagNoPanel.add(recognizedTF, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(REGISTERED_LABEL.getContent());
        jLabel7.setMaximumSize(new java.awt.Dimension(65, 28));
        jLabel7.setMinimumSize(new java.awt.Dimension(55, 28));
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        tagNoPanel.add(jLabel7, gridBagConstraints);

        registeredTF.setEditable(false);
        registeredTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        registeredTF.setText("서울32가5555");
        registeredTF.setMaximumSize(new java.awt.Dimension(130, 35));
        registeredTF.setMinimumSize(new java.awt.Dimension(100, 28));
        registeredTF.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
        tagNoPanel.add(registeredTF, gridBagConstraints);

        detailTop.add(tagNoPanel);

        detailBottom.setMinimumSize(new java.awt.Dimension(525, 140));
        detailBottom.setPreferredSize(new java.awt.Dimension(536, 150));

        imagePanel.setMinimumSize(new java.awt.Dimension(300, 240));
        imagePanel.setPreferredSize(new java.awt.Dimension(300, 240));

        imageLabel.setBackground(new java.awt.Color(0, 0, 204));
        imageLabel.setForeground(new java.awt.Color(255, 0, 153));
        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        imageLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        imageLabel.setMinimumSize(new java.awt.Dimension(300, 240));
        imageLabel.setPreferredSize(new java.awt.Dimension(300, 240));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanelLayout.createSequentialGroup()
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        targetPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        targetPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        targetPanel.setLayout(new java.awt.GridBagLayout());

        whereToLabel.setText(WHERE_TO_LABEL.getContent());
        whereToLabel.setMaximumSize(new java.awt.Dimension(110, 28));
        whereToLabel.setMinimumSize(new java.awt.Dimension(110, 28));
        whereToLabel.setPreferredSize(new java.awt.Dimension(110, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        targetPanel.add(whereToLabel, gridBagConstraints);

        buildingLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        buildingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buildingLabel.setText(BUILDING_LABEL.getContent());
        buildingLabel.setMaximumSize(new java.awt.Dimension(130, 35));
        buildingLabel.setMinimumSize(new java.awt.Dimension(130, 35));
        buildingLabel.setPreferredSize(new java.awt.Dimension(130, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        targetPanel.add(buildingLabel, gridBagConstraints);

        buildingTF.setEditable(false);
        buildingTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        buildingTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        buildingTF.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        targetPanel.add(buildingTF, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(ROOM_LABEL.getContent());
        jLabel10.setMaximumSize(new java.awt.Dimension(80, 35));
        jLabel10.setMinimumSize(new java.awt.Dimension(80, 35));
        jLabel10.setPreferredSize(new java.awt.Dimension(80, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        targetPanel.add(jLabel10, gridBagConstraints);

        unitTF.setEditable(false);
        unitTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        unitTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        unitTF.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        targetPanel.add(unitTF, gridBagConstraints);

        affiliPan.setMaximumSize(new java.awt.Dimension(196602, 35));
        affiliPan.setPreferredSize(new java.awt.Dimension(140, 35));
        affiliPan.setLayout(new javax.swing.BoxLayout(affiliPan, javax.swing.BoxLayout.LINE_AXIS));

        affiliationLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        affiliationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        affiliationLabel.setText(AFFILIATION_LABEL.getContent());
        affiliationLabel.setMaximumSize(new java.awt.Dimension(130, 35));
        affiliationLabel.setMinimumSize(new java.awt.Dimension(130, 35));
        affiliationLabel.setPreferredSize(new java.awt.Dimension(130, 35));
        affiliPan.add(affiliationLabel);

        affiliationTF.setEditable(false);
        affiliationTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        affiliationTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        affiliationTF.setPreferredSize(new java.awt.Dimension(140, 25));
        affiliPan.add(affiliationTF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        targetPanel.add(affiliPan, gridBagConstraints);

        targetDescrp.setMaximumSize(new java.awt.Dimension(196602, 35));
        targetDescrp.setPreferredSize(new java.awt.Dimension(140, 35));
        targetDescrp.setLayout(new javax.swing.BoxLayout(targetDescrp, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(VISIT_PURPOSE_LABEL.getContent());
        jLabel14.setMaximumSize(new java.awt.Dimension(110, 28));
        jLabel14.setMinimumSize(new java.awt.Dimension(110, 28));
        jLabel14.setPreferredSize(new java.awt.Dimension(110, 28));
        targetDescrp.add(jLabel14);

        visitPurposeTF.setEditable(false);
        visitPurposeTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        visitPurposeTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        visitPurposeTF.setPreferredSize(new java.awt.Dimension(140, 25));
        targetDescrp.add(visitPurposeTF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        targetPanel.add(targetDescrp, gridBagConstraints);

        javax.swing.GroupLayout detailBottomLayout = new javax.swing.GroupLayout(detailBottom);
        detailBottom.setLayout(detailBottomLayout);
        detailBottomLayout.setHorizontalGroup(
            detailBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailBottomLayout.createSequentialGroup()
                .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(targetPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        detailBottomLayout.setVerticalGroup(
            detailBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailBottomLayout.createSequentialGroup()
                .addComponent(targetPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
            .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout detailWholeLayout = new javax.swing.GroupLayout(detailWhole);
        detailWhole.setLayout(detailWholeLayout);
        detailWholeLayout.setHorizontalGroup(
            detailWholeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(detailTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(detailBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        detailWholeLayout.setVerticalGroup(
            detailWholeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailWholeLayout.createSequentialGroup()
                .addComponent(detailTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(detailBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        bottomPanel.add(detailWhole);

        bottomRightPanel.setMaximumSize(new java.awt.Dimension(32779, 33293));
        bottomRightPanel.setPreferredSize(new java.awt.Dimension(400, 300));

        arrivalListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(VEHICLE_ARRIVAL_LIST_PANEL_TITLE.getContent()));
        arrivalListPanel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        arrivalListPanel.setPreferredSize(new java.awt.Dimension(323, 0));

        arrivalsScroPan.setMinimumSize(new java.awt.Dimension(23, 27));

        JTableHeader header = arrivalsList.getTableHeader();
        header.setDefaultRenderer(
            new HeaderCellRenderer(header.getDefaultRenderer()));
        arrivalsList.setFont(new java.awt.Font(font_Type, 0, 14));
        arrivalsList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String[] {
                ORDER_HEADER.getContent(),
                ARRIVAL_TIME_HEADER.getContent(),
                CAR_TAG_HEADER.getContent(),
                "arrSeqNo"
            }

        )
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        }
    );
    arrivalsList.setRowHeight(22);
    arrivalsScroPan.setViewportView(arrivalsList);

    javax.swing.GroupLayout arrivalListPanelLayout = new javax.swing.GroupLayout(arrivalListPanel);
    arrivalListPanel.setLayout(arrivalListPanelLayout);
    arrivalListPanelLayout.setHorizontalGroup(
        arrivalListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
        .addGroup(arrivalListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arrivalsScroPan, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
    );
    arrivalListPanelLayout.setVerticalGroup(
        arrivalListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 239, Short.MAX_VALUE)
        .addGroup(arrivalListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arrivalsScroPan, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
    );

    countPanel.setMinimumSize(new java.awt.Dimension(100, 25));
    countPanel.setPreferredSize(new java.awt.Dimension(100, 25));

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
            .addGap(5, 5, 5)
            .addComponent(countValue)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    countPanelLayout.setVerticalGroup(
        countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(countPanelLayout.createSequentialGroup()
            .addGroup(countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(countValue)
                .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    saveSheet_Button.setMnemonic('A');
    saveSheet_Button.setText("ods저장(A)");
    saveSheet_Button.setEnabled(false);
    saveSheet_Button.setMargin(new java.awt.Insets(0, 0, 0, 0));
    saveSheet_Button.setMaximumSize(new java.awt.Dimension(110, 40));
    saveSheet_Button.setMinimumSize(new java.awt.Dimension(110, 40));
    saveSheet_Button.setPreferredSize(new java.awt.Dimension(110, 40));
    saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveSheet_ButtonActionPerformed(evt);
        }
    });

    closeButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    closeButton.setMnemonic('c');
    closeButton.setText(CLOSE_BTN.getContent());
    closeButton.setMaximumSize(new java.awt.Dimension(90, 40));
    closeButton.setMinimumSize(new java.awt.Dimension(90, 40));
    closeButton.setPreferredSize(new java.awt.Dimension(90, 40));
    closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            closeButtonActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanelLayout.setHorizontalGroup(
        buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(buttonPanelLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(countPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addComponent(filler23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(5, 5, 5)
            .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10))
    );
    buttonPanelLayout.setVerticalGroup(
        buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(buttonPanelLayout.createSequentialGroup()
            .addGap(0, 0, 0)
            .addComponent(countPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(buttonPanelLayout.createSequentialGroup()
            .addGap(25, 25, 25)
            .addComponent(filler23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(buttonPanelLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(buttonPanelLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    javax.swing.GroupLayout bottomRightPanelLayout = new javax.swing.GroupLayout(bottomRightPanel);
    bottomRightPanel.setLayout(bottomRightPanelLayout);
    bottomRightPanelLayout.setHorizontalGroup(
        bottomRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(arrivalListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    bottomRightPanelLayout.setVerticalGroup(
        bottomRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(bottomRightPanelLayout.createSequentialGroup()
            .addGap(0, 0, 0)
            .addComponent(arrivalListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
            .addGap(10, 10, 10)
            .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0))
    );

    bottomPanel.add(bottomRightPanel);

    wholePanel.add(bottomPanel);

    getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

    wholeBottom.setMaximumSize(new java.awt.Dimension(32767, 40));
    wholeBottom.setMinimumSize(new java.awt.Dimension(0, 40));
    wholeBottom.setPreferredSize(new java.awt.Dimension(0, 40));

    javax.swing.GroupLayout wholeBottomLayout = new javax.swing.GroupLayout(wholeBottom);
    wholeBottom.setLayout(wholeBottomLayout);
    wholeBottomLayout.setHorizontalGroup(
        wholeBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
    );
    wholeBottomLayout.setVerticalGroup(
        wholeBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 40, Short.MAX_VALUE)
    );

    getContentPane().add(wholeBottom, java.awt.BorderLayout.SOUTH);

    wholeWest.setMaximumSize(new java.awt.Dimension(40, 32767));
    wholeWest.setMinimumSize(new java.awt.Dimension(40, 0));
    wholeWest.setPreferredSize(new java.awt.Dimension(40, 0));

    javax.swing.GroupLayout wholeWestLayout = new javax.swing.GroupLayout(wholeWest);
    wholeWest.setLayout(wholeWestLayout);
    wholeWestLayout.setHorizontalGroup(
        wholeWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
    );
    wholeWestLayout.setVerticalGroup(
        wholeWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
    );

    getContentPane().add(wholeWest, java.awt.BorderLayout.WEST);

    setBounds(0, 0, 1016, 780);
    }// </editor-fold>//GEN-END:initComponents

    private void imageLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLabelMouseClicked
        // TODO add your handling code here:
        if (originalImg != null)
        {
            ImageDisplay bigImage = new ImageDisplay(originalImg, "100% " + FULL_SIZE_IMAGE_FRAME_TITLE.getContent());
            bigImage.setVisible(true);               
        }        
    }//GEN-LAST:event_imageLabelMouseClicked

    private void attendantCBPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_attendantCBPopupMenuWillBecomeVisible
        if (attendantCB.getItemCount() > 1)
            return;
        
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;         
        
        try {
            //<editor-fold defaultstate="collapsed" desc="-- load affiliation comboBox"> 
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();

            sb.append("Select id, name From users_osp ");
            sb.append("Order by name");

            rs = stmt.executeQuery(sb.toString());
            attendantCB.addItem(new ConvComboBoxItem(null, ATTENDANT_LOGOUT_ITEM.getContent()));
            while (rs.next()) {
                attendantCB.addItem(new ConvComboBoxItem(rs.getString("id"), rs.getString("name")));
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "(CBox Item Loading for : attendants" );
        } finally {
            closeDBstuff(conn, stmt, rs, "attendants list pop up menu");
        }        
    }//GEN-LAST:event_attendantCBPopupMenuWillBecomeVisible

    private void clearSearchPropertiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSearchPropertiesButtonActionPerformed
        gateCB.setSelectedIndex(0);
        carTagTF.setText("");
        attendantCB.setSelectedIndex(0);
        searchL1ComboBox.setSelectedIndex(0);
        searchL2ComboBox.setSelectedIndex(0);
        searchBuildingComboBox.setSelectedIndex(0);
        searchUnitComboBox.setSelectedIndex(0);
        gateBarCB.setSelectedIndex(0);
        affiliationRadioButtonActionPerformed(null);        
    }//GEN-LAST:event_clearSearchPropertiesButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadArrivalsListTable(true);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void setSearchPeriodOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSearchPeriodOptionButtonActionPerformed
        Connection conn = null;
        PreparedStatement pStmt = null;

        try
        {
            int option = -1;
            if (oneHourRadioButton.isSelected())
            option = SearchPeriod.OneHour.ordinal();
            else if (oneDayRadioButton.isSelected())
            option = SearchPeriod.OneDay.ordinal();
            else
            option = SearchPeriod.GivenPeriod.ordinal();

            conn = getConnection();
            String sql = "update settingsTable set SearchPeriod = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, option);
            pStmt.executeUpdate();
        }
        catch(Exception e)
        {
            logParkingException(Level.SEVERE, e, "(arrival record search option setting change)");
        }
        finally {
            closeDBstuff(conn, pStmt, null, "(arrival record search option setting change)");
        }
    }//GEN-LAST:event_setSearchPeriodOptionButtonActionPerformed

    private void periodRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_periodRadioButtonActionPerformed
        if (periodRadioButton.isSelected()) {
            BeginDateChooser.setEnabled(true);
            EndDateChooser.setEnabled(true);
        }
    }//GEN-LAST:event_periodRadioButtonActionPerformed

    private void oneDayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneDayRadioButtonActionPerformed
        disablePeriodChooser();
    }//GEN-LAST:event_oneDayRadioButtonActionPerformed

    private void oneHourRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneHourRadioButtonActionPerformed
        disablePeriodChooser();
    }//GEN-LAST:event_oneHourRadioButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void searchUnitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchUnitComboBoxActionPerformed
        buildingRadioButton.setEnabled(true);
    }//GEN-LAST:event_searchUnitComboBoxActionPerformed

    private void searchUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeVisible
        int bldgNo = (Integer)
                ((ConvComboBoxItem)searchBuildingComboBox.getSelectedItem()).getKeyValue();
        
        if (searchUnitComboBox.getItemCount() == 1 || 
                getPrevParentSKey()[UnitNo.getNumVal()] != bldgNo) 
        {
            Object selItem = searchUnitComboBox.getSelectedItem();
            refreshComboBox(searchUnitComboBox, getPrompter(UnitNo, searchBuildingComboBox), 
                UnitNo, bldgNo, getPrevParentSKey());
            searchUnitComboBox.setSelectedItem(selItem); 
        }        
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeVisible

    private void searchL2ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchL2ComboBoxActionPerformed
        affiliationRadioButton.setSelected(true);
    }//GEN-LAST:event_searchL2ComboBoxActionPerformed
    
    private void searchL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeVisible
//        int L1No = (Integer)((ConvComboBoxItem)searchL1ComboBox.getSelectedItem()).getKeyValue();
//        
//        if (searchL2ComboBox.getItemCount() == 1 || 
//                getPrevParentSKey()[AffiliationL2.getNumVal()] != L1No) 
//        {
//            Object selItem = searchL2ComboBox.getSelectedItem();
//            refreshComboBox(searchL2ComboBox, getPrompter(AffiliationL2, searchL1ComboBox),
//                    AffiliationL2, L1No, getPrevParentSKey());        
//            searchL2ComboBox.setSelectedItem(selItem);
//        }       
        
        mayRefreshLowerCBox(searchL1ComboBox, searchL2ComboBox, AffiliationL2, 
                getPrevParentSKey());
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeVisible

    private void searchBuildingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxActionPerformed
        if (searchBuildingComboBox.isPopupVisible()) {
            buildingRadioButton.setEnabled(true);
            MutableComboBoxModel model
            = (MutableComboBoxModel)searchUnitComboBox.getModel();
            model.removeElementAt(0);
            model.insertElementAt(getPrompter(UnitNo,
                searchBuildingComboBox), 0);
            searchUnitComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_searchBuildingComboBoxActionPerformed

    private void searchL1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchL1ComboBoxActionPerformed
        if (searchL1ComboBox.isPopupVisible()) {
            affiliationRadioButton.setSelected(true);
            MutableComboBoxModel model
            = (MutableComboBoxModel)searchL2ComboBox.getModel();
            model.removeElementAt(0);
            model.insertElementAt(getPrompter(AffiliationL2, searchL1ComboBox), 0);
            searchL2ComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_searchL1ComboBoxActionPerformed

    private void buildingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildingRadioButtonActionPerformed
        if (buildingRadioButton.isSelected()) {
            searchL1ComboBox.setEnabled(false);
            searchL2ComboBox.setEnabled(false);
            searchBuildingComboBox.setEnabled(true);
            searchUnitComboBox.setEnabled(true);
        }
    }//GEN-LAST:event_buildingRadioButtonActionPerformed

    private void affiliationRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_affiliationRadioButtonActionPerformed
        if (affiliationRadioButton.isSelected()) {
            searchBuildingComboBox.setEnabled(false);
            searchUnitComboBox.setEnabled(false);
            searchL1ComboBox.setEnabled(true);
            searchL2ComboBox.setEnabled(true);
        }
    }//GEN-LAST:event_affiliationRadioButtonActionPerformed

    private void searchL2ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible
        mayPropagateBackward(searchL2ComboBox, searchL1ComboBox);
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible

    private void searchUnitComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible
        mayPropagateBackward(searchUnitComboBox, searchBuildingComboBox);
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
//        saveODSfile(this, driverTable, saveFileChooser, DRIVER_SAVE_ODS_FAIL_DIALOG.getContent());
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    // <editor-fold defaultstate="collapsed" desc="-- Variables defined via GUI creation">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser BeginDateChooser;
    private com.toedter.calendar.JDateChooser EndDateChooser;
    private javax.swing.JPanel affiliPan;
    private javax.swing.JPanel affiliPanel;
    private javax.swing.ButtonGroup affiliationGroup;
    private javax.swing.JLabel affiliationLabel;
    private javax.swing.JRadioButton affiliationRadioButton;
    private javax.swing.JTextField affiliationTF;
    private javax.swing.JPanel arrivalListPanel;
    private javax.swing.JTextField arrivalTmTF;
    private javax.swing.JTable arrivalsList;
    private javax.swing.JScrollPane arrivalsScroPan;
    private javax.swing.JPanel attendPanel;
    private javax.swing.JComboBox attendantCB;
    private javax.swing.JTextField attendantTF;
    private javax.swing.JPanel barOptnPanel;
    private javax.swing.JTextField barOptnTF;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel bottomRightPanel;
    private javax.swing.JLabel buildingLabel;
    private javax.swing.JPanel buildingPanel;
    private javax.swing.JRadioButton buildingRadioButton;
    private javax.swing.JTextField buildingTF;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel carPlus;
    private javax.swing.JPanel carTagPanel;
    private javax.swing.JTextField carTagTF;
    private javax.swing.JPanel clearPanel;
    private javax.swing.JButton clearSearchPropertiesButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel colHead1;
    private javax.swing.JLabel colHead2;
    private javax.swing.JLabel countLbl;
    private javax.swing.JPanel countPanel;
    private javax.swing.JLabel countValue;
    private javax.swing.JPanel criteriaPanel;
    private javax.swing.JPanel dateTmPanel;
    private javax.swing.JPanel detailBottom;
    private javax.swing.JPanel detailTop;
    private javax.swing.JPanel detailWhole;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler_h10_18;
    private javax.swing.Box.Filler filler_h10_19;
    private javax.swing.JPanel fixPanel;
    private javax.swing.JComboBox gateBarCB;
    private javax.swing.JComboBox gateCB;
    private javax.swing.JTextField gateNameTF;
    private javax.swing.JPanel gateNoPanel;
    private javax.swing.JPanel gatePanel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JRadioButton oneDayRadioButton;
    private javax.swing.JRadioButton oneHourRadioButton;
    private javax.swing.ButtonGroup periodOptionGroup;
    private javax.swing.JRadioButton periodRadioButton;
    private javax.swing.JTextField recognizedTF;
    private javax.swing.JTextField registeredTF;
    private javax.swing.JTextField rowNumTF;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JPanel searchBottom;
    private javax.swing.JComboBox searchBuildingComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchButtonPanel;
    private javax.swing.JComboBox searchL1ComboBox;
    private javax.swing.JComboBox searchL2ComboBox;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchTop;
    private javax.swing.JComboBox searchUnitComboBox;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JButton setSearchPeriodOptionButton;
    private javax.swing.JPanel tagNoPanel;
    private javax.swing.JPanel targetDescrp;
    private javax.swing.JPanel targetPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel topDriver;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel topVehicle;
    private javax.swing.JTextField unitTF;
    private javax.swing.JTextField visitPurposeTF;
    private javax.swing.JLabel whereToLabel;
    private javax.swing.JPanel wholeBottom;
    private javax.swing.JPanel wholeEast;
    private javax.swing.JPanel wholePanel;
    private javax.swing.JPanel wholeTop;
    private javax.swing.JPanel wholeWest;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    private void loadArrivalsListTable(boolean selectTop) {
        DefaultTableModel model = (DefaultTableModel) arrivalsList.getModel();  
        
        // <editor-fold defaultstate="collapsed" desc="-- construct SQL statement">  
        StringBuffer cond = new StringBuffer();
        StringBuffer sb = new StringBuffer(); 
        
        if (affiliationRadioButton.isSelected() && searchL2ComboBox.getSelectedIndex() > 0) {
            // <editor-fold defaultstate="collapsed" desc="-- specific affiliation is selected">  
            createConditionWithCA(cond);

            InnoComboBoxItem item = (InnoComboBoxItem)searchL2ComboBox.getSelectedItem();
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo ");
            sb.append("From car_arrival CA, vehicles VH, cardriver CD ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.TagEnteredAs = VH.PLATE_NUMBER ");
            sb.append("  and VH.DRIVER_SEQ_NO = CD.SEQ_NO ");
            sb.append("  and CD.L2_NO = " + item.getKeys()[0] + " ");
            sb.append(") Union ");
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo  ");
            sb.append("From car_arrival CA ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.L2_NO = " + item.getKeys()[0] + ") ");
            sb.append("Order by arrSeqNo desc");
            //</editor-fold>                   
        } else if (affiliationRadioButton.isSelected() && searchL1ComboBox.getSelectedIndex() > 0) {
            // <editor-fold defaultstate="collapsed" desc="-- only higher level affiliation is selected">  
            createConditionWithCA(cond);

            ConvComboBoxItem item = (ConvComboBoxItem)searchL1ComboBox.getSelectedItem();
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo "); 
            sb.append("From car_arrival CA, vehicles VH, cardriver CD, l2_affiliation L2 "); 
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.TagEnteredAs = VH.PLATE_NUMBER "); 
            sb.append("  and VH.DRIVER_SEQ_NO = CD.SEQ_NO "); 
            sb.append("  and CD.L2_NO = L2.L2_NO  "); 
            sb.append("  and L2.L1_NO = " + item.getKeyValue() + ") "); 
            sb.append("Union "); 
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo  "); 
            sb.append("From car_arrival CA, l2_affiliation L2 "); 
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.L2_NO = L2.L2_NO  "); 
            sb.append("  and L2.L1_NO =  " + item.getKeyValue() + ") "); 
            sb.append("Order by arrSeqNo desc "); 
            //</editor-fold>   
        } else if (buildingRadioButton.isSelected() && searchUnitComboBox.getSelectedIndex() > 0) {
            // <editor-fold defaultstate="collapsed" desc="-- specific building unit is selected">  
            createConditionWithCA(cond);

            InnoComboBoxItem item = (InnoComboBoxItem)searchUnitComboBox.getSelectedItem();
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo ");
            sb.append("From car_arrival CA, vehicles VH, cardriver CD ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.TagEnteredAs = VH.PLATE_NUMBER ");
            sb.append("  and VH.DRIVER_SEQ_NO = CD.SEQ_NO ");
            sb.append("  and CD.UNIT_SEQ_NO = " + item.getKeys()[0] + ") ");
            sb.append("Union ");
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo  ");
            sb.append("From car_arrival CA ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.unitSeqNo = " + item.getKeys()[0] + ") ");
            sb.append("Order by arrSeqNo desc");
            //</editor-fold>                               
        } else if (buildingRadioButton.isSelected() && searchBuildingComboBox.getSelectedIndex() > 0) {
            // <editor-fold defaultstate="collapsed" desc="-- only a building is selected">  
            createConditionWithCA(cond);

            ConvComboBoxItem item = (ConvComboBoxItem)searchBuildingComboBox.getSelectedItem();
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo ");
            sb.append("From car_arrival CA, vehicles VH, cardriver CD, building_unit UT ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.TagEnteredAs = VH.PLATE_NUMBER ");
            sb.append("  and VH.DRIVER_SEQ_NO = CD.SEQ_NO ");
            sb.append("  and CD.unit_seq_no = UT.SEQ_NO ");
            sb.append("  and UT.BLDG_SEQ_NO = " + item.getKeyValue() + ") ");
            sb.append("Union ");
            sb.append("(Select ArrivalTime, tagRecognized, arrSeqNo ");
            sb.append("From car_arrival CA, building_unit UT ");
            sb.append("Where " + (cond.length() > 0 ? cond + " and " : ""));
            sb.append("  CA.unitSeqNo = UT.SEQ_NO ");
            sb.append("  and UT.BLDG_SEQ_NO = " + item.getKeyValue() + ") ");
            sb.append("Order by arrSeqNo desc");
            //</editor-fold>               
        } else {
            // <editor-fold defaultstate="collapsed" desc="-- without affiliation or building condition">  
            attachTimeCondition(null, cond);

            Object keyObj =((ConvComboBoxItem)gateCB.getSelectedItem()).getKeyValue();
            attachIntCondition(cond, "GateNo", (Integer) keyObj);   

            attachCondition(cond, "tagRecognized", carTagTF.getText().trim());

            Object selValue = ((ConvComboBoxItem)attendantCB.getSelectedItem()).getKeyValue();
            if (selValue == null) {
                cond.append("and attendantID is null");
            } else {
                attachCondition(cond, "attendantID", (String)selValue);
            }

            if (gateBarCB.getSelectedIndex() != 0) {
                ConvComboBoxItem item = (ConvComboBoxItem)gateBarCB.getSelectedItem();
                attachIntCondition(cond, "BarOperation", ((BarOperation)(item.getKeyValue())).ordinal()) ;
            }

            sb.append("Select ArrivalTime, tagRecognized, arrSeqNo "); 
            sb.append("From car_arrival ");
            sb.append((cond.length() > 0 ? "Where " + cond : ""));
            sb.append(" Order by arrSeqNo desc");
            //</editor-fold>   
        }
        //</editor-fold>   
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        
        try {
            // <editor-fold defaultstate="collapsed" desc="-- load vehicle list">                          
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            
            addSelectionChangeListener(false);
            model.setRowCount(0);
            
            int rowNum = 0;
            while (rs.next()) {
                // <editor-fold defaultstate="collapsed" desc="-- make a vehicle row">     
                SimpleDateFormat timeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");   
                Timestamp arrivalTS = rs.getTimestamp("ArrivalTime");
                StringBuffer arrivalTime  = new StringBuffer("'");
                arrivalTime.append(timeFormat.format(arrivalTS)); 
                model.addRow(new Object[] {
                    ++rowNum, 
                    arrivalTime.toString(),
                    rs.getString("tagRecognized"), 
                    rs.getString("arrSeqNo")
                });
                //</editor-fold>
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, "(registered vehicle list loading)");
        } finally {
            closeDBstuff(conn, selectStmt, rs, "(registered vehicle list loading)");
            addSelectionChangeListener(true);
        }
        
        int numRows = model.getRowCount();
        
        countValue.setText(Integer.toString(numRows));
        if (numRows == 0) {
            clearArrivalDetail();
        } else {
            if (selectTop) {
                arrivalsList.setRowSelectionInterval(0, 0);
                arrivalsList.requestFocus();
            }
        }
    }

    private void fineTuneColumnWidth() {
        TableColumnModel tcm = arrivalsList.getColumnModel();
       
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(0), 50, 80, 200); // 0: row number
        SetAColumnWidth(tcm.getColumn(1), 100, 160, 400); // 1: arrival date and time
        SetAColumnWidth(tcm.getColumn(2), 75, 120, 300); // 2: recognized vehicle tag
    }

    private void detailTuneTableProperties() {
        tuneTitlePanelSize();
        changeLabelText();
        fineTuneColumnWidth();
        TableColumnModel arrivalsTableModel = arrivalsList.getColumnModel();
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment( SwingConstants.RIGHT );

        arrivalsTableModel.getColumn(0).setCellRenderer( rightRenderer );        
        arrivalsTableModel.removeColumn(arrivalsTableModel.getColumn(3));
        
        valueChangeListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        int idx = arrivalsList.getSelectedRow();
                        
                        if (idx == -1) {
                            clearArrivalDetail();
                            return;
                        }
                        rowNumTF.setText(arrivalsList.getModel().getValueAt(idx, 0).toString());
                        arrivalTmTF.setText(((String)arrivalsList.getModel().getValueAt(idx, 1)).substring(10));
                        recognizedTF.setText((String)arrivalsList.getModel().getValueAt(idx, 2));
                        String seqNo = (String)arrivalsList.getModel().getValueAt(idx, 3);
                        displayArrivalDetail(seqNo);   
                    }
                });
            }
          
            private void displayArrivalDetail(String seqNo) {
                Connection conn = null;
                Statement selectStmt = null;
                ResultSet rs = null;

                // <editor-fold defaultstate="collapsed" desc="-- form select statement for detailed info'">
                StringBuffer sb = new StringBuffer(); 
                sb.append("Select AV.*, CD.L2_NO as regisL2No, CD.UNIT_SEQ_NO as regisUnitSN "); 
                sb.append("from ( "); 
                sb.append("  Select gateNo, attendantID, tagEnteredAs, L2_No as visitL2No,  "); 
                sb.append("        UnitSeqNo as visitUnitSN, visitReason, DRIVER_SEQ_NO, barOperation, "); 
                sb.append("        LENGTH(ImageBlob) imgBytes, ImageBlob "); 
                sb.append("  From car_arrival "); 
                sb.append("  Left Join vehicles "); 
                sb.append("  On car_arrival.TagEnteredAs = vehicles.PLATE_NUMBER "); 
                sb.append("  Where arrSeqNo = " + seqNo +" ) AV "); 
                sb.append("left join cardriver CD "); 
                sb.append("On AV.DRIVER_SEQ_NO = CD.SEQ_NO "); 
                //</editor-fold>
                
                try {
                    conn = JDBCMySQL.getConnection();
                    selectStmt = conn.createStatement();
                    rs = selectStmt.executeQuery(sb.toString());
                    if (rs.next()) {
                        gateNameTF.setText(gateNames[rs.getInt("gateNo")]);
                        
                        //<editor-fold defaultstate="collapsed" desc="-- show attendant ID">
                        String attID = rs.getString("attendantID");
                        if (attID == null || attID.length() == 0)
                            attendantTF.setText(LOG_OUT_TF.getContent());
                        else
                            attendantTF.setText(attID);
                        //</editor-fold>
                        
                        // <editor-fold defaultstate="collapsed" desc="-- show registered tag number">
                        String regiTag = rs.getString("tagEnteredAs");
                        int l2No = 0, unitSN = 0;
                                                
                        if (regiTag == null) {
                            // handle unregistered vehicle
                            registeredTF.setText(UNREGISTERED_TF.getContent());
                            buildingLabel.setText(VISIT_TARGET_LABEL.getContent());
                            affiliationLabel.setText(NON_REGI_TAG2_LABEL.getContent());
                            l2No = rs.getInt("visitL2No");
                            unitSN = rs.getInt("visitUnitSN");
                        }
                        else {
                            // for registered vehicles
                            registeredTF.setText(regiTag);
                            buildingLabel.setText(BUILDING_LABEL.getContent());
                            affiliationLabel.setText(AFFILIATION_LABEL.getContent());
                            l2No = rs.getInt("regisL2No");
                            unitSN = rs.getInt("regisUnitSN");
                        }
                        
                        buildingTF.setText(UNKNOWN_TF.getContent());
                        unitTF.setText(UNKNOWN_TF.getContent());
                        affiliationTF.setText(UNKNOWN_TF.getContent());
                        //</editor-fold>                        
                                
                        if (l2No > 0 || unitSN > 0) {
                            showPlaceDetail(l2No, unitSN);
                        }   
                        
                        // <editor-fold defaultstate="collapsed" desc="-- show visit reason">
                        String purpose = rs.getString("visitReason");
                        if (purpose == null || purpose.length() == 0) {
                            if (regiTag == null)
                                visitPurposeTF.setText(UNKNOWN_TF.getContent()); 
                            else
                                visitPurposeTF.setText(NOT_APPLICABLE_TF.getContent()); 
                        }
                        else
                            visitPurposeTF.setText(rs.getString("visitReason")); 
                        
                        int ordinalValue = (Integer)(rs.getInt("barOperation"));
                        barOptnTF.setText(getBarOperationLabel(BarOperation.values()[ordinalValue])); 
                        //</editor-fold>                        

                        // <editor-fold defaultstate="collapsed" desc="-- display car arrival image">
                        
                        InputStream imageIS = rs.getBinaryStream("ImageBlob");
                        try {
                            if(imageIS == null){
                                imageIS = this.getClass().getResourceAsStream("/deletedPicture.png");
                            }
                            originalImg = ImageIO.read(imageIS);
                            ImageIcon iIcon = createStretchedIcon(imageLabel.getSize(), originalImg,
                                    false);   
                            imageLabel.setIcon(iIcon); 
                        } catch (IOException ex) {
                            logParkingException(Level.SEVERE, ex, "(image loading from DB)");
                        }
                        //</editor-fold>                        
                    }
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, "(arrived vehicle detail loading)");
                } finally {
                    closeDBstuff(conn, selectStmt, rs, "(arrived vehicle detail loading)");
                }
            }

            private void showPlaceDetail(int l2No, int unitSN) {
                Connection conn = null;
                Statement selectStmt = null;
                ResultSet rs = null;                
                StringBuffer sb = new StringBuffer(); 
                
                sb.append("Select bt.BLDG_NO, ut.UNIT_NO ");
                sb.append("From building_table bt, building_unit ut ");
                sb.append("Where ut.SEQ_NO = " + unitSN + " and ut.BLDG_SEQ_NO = bt.SEQ_NO");
                
                try {
                    // <editor-fold defaultstate="collapsed" desc="-- read building and unit info">    
                    conn = JDBCMySQL.getConnection();
                    selectStmt = conn.createStatement();
                    rs = selectStmt.executeQuery(sb.toString());
                    if (rs.next()) {
                        buildingTF.setText(rs.getString("BLDG_NO"));
                        unitTF.setText(rs.getString("UNIT_NO"));
                    }
                    //</editor-fold>
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, "(fetching building and unit)");
                } finally {
                    closeDBstuff(conn, selectStmt, rs, "(arrived vehicle detail loading)");
                }
                
                sb = new StringBuffer(); 
                sb.append("Select concat(L1.Party_name, '-', L2.Party_name) as affiliation ");
                sb.append("From l1_affiliation L1, l2_affiliation L2 ");
                sb.append("Where L2.L2_NO = " + l2No + " and L2.L1_NO = L1.L1_NO");
                
                try {
                    // <editor-fold defaultstate="collapsed" desc="-- read affiliation info">                          
                    conn = JDBCMySQL.getConnection();
                    selectStmt = conn.createStatement();
                    rs = selectStmt.executeQuery(sb.toString());
                    if (rs.next()) {
                        affiliationTF.setText(rs.getString("affiliation"));
                    }
                    //</editor-fold>
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, "(fetching affiliation)");
                } finally {
                    closeDBstuff(conn, selectStmt, rs, "(arrived vehicle detail loading)");
                }                               
            }
        };        
    }

    private void clearArrivalDetail() {
        rowNumTF.setText("");
        arrivalTmTF.setText("");
        recognizedTF.setText("");    
        gateNameTF.setText("");    
        attendantTF.setText(""); 
        registeredTF.setText("");
        buildingLabel.setText(VISIT_TARGET_LABEL.getContent());
        affiliationLabel.setText(NON_REGI_TAG2_LABEL.getContent());
        buildingTF.setText("");
        unitTF.setText("");
        affiliationTF.setText("");    
        visitPurposeTF.setText(""); 
        barOptnTF.setText("");
        try {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image Exists");
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "(while reading no image picture)");
        }                        
    }

    private void loadSearchControls() {
        attachEnterHandler(gateCB);
        attachEnterHandler(carTagTF);
        attachEnterHandler(attendantCB);
        attachEnterHandler(searchL1ComboBox);
        attachEnterHandler(searchL2ComboBox);
        attachEnterHandler(searchBuildingComboBox);
        attachEnterHandler(searchUnitComboBox);
        attachEnterHandler(gateBarCB);
        
        // load gate number combobox 
        gateCB.addItem(new ConvComboBoxItem(new Integer(-1), GATE_CB_ITEM.getContent()));
        for (int i = 1; i <= gateCount; i++) {
            gateCB.addItem(new ConvComboBoxItem(new Integer(i), gateNames[i]));
        }
        
        refreshComboBox(searchL1ComboBox, getPrompter(AffiliationL1, searchL1ComboBox),
                AffiliationL1, -1, getPrevParentSKey());
        searchL2ComboBox.addItem(getPrompter(AffiliationL2, searchL1ComboBox));
        
        refreshComboBox(searchBuildingComboBox, getPrompter(BuildingNo, searchBuildingComboBox),
                BuildingNo, -1, getPrevParentSKey());
        searchUnitComboBox.addItem(getPrompter(UnitNo, searchBuildingComboBox));
        
        addBarOperationItems();
        Calendar today = Calendar.getInstance();
        EndDateChooser.setDate(today.getTime());
        today.add(Calendar.DATE, -7);
        BeginDateChooser.setDate(today.getTime());
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
                        loadArrivalsListTable(true);
                } else
                    loadArrivalsListTable(true);
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);        
    }

    private void createConditionWithCA(StringBuffer cond) {
        Object keyObj =((ConvComboBoxItem)gateCB.getSelectedItem()).getKeyValue();
        attachIntConditionCA(cond, "GateNo", (Integer) keyObj);   

        attachConditionCA(cond, "tagRecognized", carTagTF.getText().trim());

        Object selValue = ((ConvComboBoxItem)attendantCB.getSelectedItem()).getKeyValue();
        if (selValue == null) {
            cond.append(" attendantID is null");
        } else {
            attachConditionCA(cond, "attendantID", (String)selValue);
        }

        keyObj =((ConvComboBoxItem)gateBarCB.getSelectedItem()).getKeyValue();
        attachIntConditionCA(cond, "BarOperation", (Integer) keyObj);           
    }

    private void attachTimeCondition(String prefix, StringBuffer cond) {
        if (cond.length() > 0)
            cond.append(" and ");
        if (oneHourRadioButton.isSelected()) {
            cond.append("ArrivalTime >= SUBTIME(now(), '1:0:0')");
        } else if (oneDayRadioButton.isSelected()) {
            cond.append("ArrivalTime >= SUBTIME(now(), '24:0:0')");
        } else {
            Date beginDate = BeginDateChooser.getDate();
            Date endDate = EndDateChooser.getDate();

            // Check if starting date and ending date both entered
            if (beginDate == null || endDate == null) {
                JOptionPane.showConfirmDialog(this, DATE_INPUT_CHECK_DIALOG.getContent(),
                        WARING_DIALOGTITLE.getContent(),
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE); 
            } else {
                // Check if starting date were later than ending date which is illogical.
                if (beginDate.after(endDate)) {
                    JOptionPane.showConfirmDialog(this, DATE_INPUT_ERROR_DIALOG.getContent(),
                            WARING_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);             
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String startDate = dateFormat.format(beginDate);
                    String stopDate = dateFormat.format(endDate);                    
                    cond.append("'" + startDate + "' <= date(ArrivalTime) && date(ArrivalTime) <= '" 
                            + stopDate + "'");
                }
            }
        }
    }

    private void disablePeriodChooser() {
        BeginDateChooser.setEnabled(false);
        EndDateChooser.setEnabled(false);
    }

    private void addBarOperationItems() {
        gateBarCB.removeAllItems();
        
        gateBarCB.addItem(new ConvComboBoxItem(new Integer(-1), BAR_CB_ITEM.getContent()));

        for (BarOperation barOperation : BarOperation.values()) {
            gateBarCB.addItem(new ConvComboBoxItem(barOperation, 
                    getBarOperationLabel(barOperation)));
        }
    }

    public static String getBarOperationLabel(BarOperation barOperation) {
        String label;
        switch (barOperation) {
            case REGISTERED_CAR_OPENED:
                label = BAR_ALLOWED_CB_ITEM.getContent();
                break;

            case AUTO_OPENED:
                label = BAR_LAZY_ATT_CB_ITEM.getContent();
                break;

            case MANUAL:
                label = BAR_MANUAL_CB_ITEM.getContent();
                break;

            case REMAIN_CLOSED:
                label = BAR_REMAIN_CLOSED_ATT_CB_ITEM.getContent();
                break;

            default:
                label = "(none)";
                break;
        }        
        return label;
    }

    private void addSelectionChangeListener(boolean listenerNeeded) {
        if (listenerNeeded) {
            arrivalsList.getSelectionModel().addListSelectionListener(valueChangeListener);
        } else {
            arrivalsList.getSelectionModel().removeListSelectionListener(valueChangeListener);
        }
    }

    /**
     * @return the prevParentSKey
     */
    public int[] getPrevParentSKey() {
        return prevParentSKey;
    }

    private void mayRefreshLowerCBox(JComboBox higherCBox, JComboBox lowerCBox, 
            OSP_enums.DriverCol lowerCol, int[] prevHigherSKey) 
    {
        int higherSKey = (Integer)((ConvComboBoxItem)higherCBox.getSelectedItem()).getKeyValue();
        
        if (lowerCBox.getItemCount() == 1 || 
                prevHigherSKey[lowerCol.getNumVal()] != higherSKey) 
        {
            Object selectedLowerItem = lowerCBox.getSelectedItem();
            refreshComboBox(lowerCBox, getPrompter(lowerCol, higherCBox),
                    lowerCol, higherSKey, prevHigherSKey);        
            lowerCBox.setSelectedItem(selectedLowerItem);
        }    
    }

    private void tuneTitlePanelSize() {
        setComponentSize(seeLicenseButton, new Dimension(buttonWidthNorm, buttonHeightNorm));
        setComponentSize(myMetaKeyLabel, new Dimension(buttonWidthNorm, buttonHeightNorm));
        setComponentSize(carTagTF, new Dimension(CABW_NORM, CABH_NORM));
        setComponentSize(gateCB, new Dimension(CABW_NORM, CABH_NORM));
        setComponentSize(attendantCB, new Dimension(CABW_WIDE, CABH_NORM));
        setComponentSize(gateBarCB, new Dimension(CABW_NORM, CABH_NORM));
        setComponentSize(carPlus, new Dimension(360, 55));
        
        setComponentSize(searchL1ComboBox, new Dimension(CABW_WIDE, CABH_NORM));
        setComponentSize(searchL2ComboBox, new Dimension(CABW_WIDE, CABH_NORM));
        setComponentSize(affiliPanel, new Dimension(CABW_WIDE, CABH_NORM * 3));
        
        setComponentSize(searchBuildingComboBox, new Dimension(CABW_NORM, CABH_NORM));
        setComponentSize(searchUnitComboBox, new Dimension(CABW_NORM, CABH_NORM));
        setComponentSize(buildingPanel, new Dimension(CABW_NORM, CABH_NORM * 3));
        
        setComponentSize(clearSearchPropertiesButton, new Dimension(buttonWidthNorm, buttonHeightNorm));
        setComponentSize(clearPanel, new Dimension(buttonWidthNorm, 94));
        
        setComponentSize(setSearchPeriodOptionButton, new Dimension(buttonWidthNorm, buttonHeightNorm));
        setComponentSize(searchBottom, new Dimension(800, 90));
        
        setComponentSize(searchButton, new Dimension(buttonWidthNorm, bigButtonHeight));
        setComponentSize(saveSheet_Button, new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        setComponentSize(closeButton, new Dimension(CommonData.buttonWidthNorm, buttonHeightNorm));
    }

    private void changeLabelText() {
        colHead1.setText(ARR_TM_LEGEND.getContent());
        colHead2.setText(CAR_TAG_HEADER.getContent());
    }
}

class HeaderCellRenderer implements TableCellRenderer {
 
  private final TableCellRenderer wrappedRenderer;
  private final JLabel label;
 
  public HeaderCellRenderer(TableCellRenderer wrappedRenderer) {
    if (!(wrappedRenderer instanceof JLabel)) {
      throw new IllegalArgumentException("The supplied renderer must inherit from JLabel");
    }
    this.wrappedRenderer = wrappedRenderer;
    this.label = (JLabel) wrappedRenderer;
  }
 
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
    wrappedRenderer.getTableCellRendererComponent(table, value,
            isSelected, hasFocus, row, column);
    label.setHorizontalAlignment(column == 0 ? JLabel.RIGHT : JLabel.LEFT);
    return label;
  }
}        
