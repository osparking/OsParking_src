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

import com.osparking.global.Globals;
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
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DATE_INPUT_CHECK_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DATE_INPUT_ERROR_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.WARING_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ARRIVAL_TIME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ATTENDANT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BAR_OP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BUILDING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CAR_TAG_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DURATION_SET_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FILE_SIZE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LAST_1HOUR_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LAST_24HOURS_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NON_REGI_TAG1_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NON_REGI_TAG2_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.RECOGNIZED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.RECORD_COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REGISTERED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ROOM_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_PURPOSE_LABEL;
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
import com.osparking.global.names.OSP_enums.BarOperation;
import static com.osparking.global.names.OSP_enums.BarOperation.REGISTERED_CAR_OPENED;
import static com.osparking.global.names.OSP_enums.BarOperation.AUTO_OPENED;
import static com.osparking.global.names.OSP_enums.BarOperation.MANUAL;
import static com.osparking.global.names.OSP_enums.BarOperation.REMAIN_CLOSED;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.OSP_enums.SearchPeriod;
import static com.osparking.vehicle.driver.ManageDrivers.getPrompter;
import static com.osparking.vehicle.driver.ManageDrivers.initSearchComboBox;
import static com.osparking.vehicle.driver.ManageDrivers.loadComboBoxItems;
import static com.osparking.vehicle.driver.ManageDrivers.loadUnitComboBox;
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

        periodOptionGroup = new javax.swing.ButtonGroup();
        affiliationGroup = new javax.swing.ButtonGroup();
        wholePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        criteriaPanel = new javax.swing.JPanel();
        searchTop = new javax.swing.JPanel();
        gatePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        gateCB = new javax.swing.JComboBox();
        carTagPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        carTagTF = new javax.swing.JTextField();
        attendPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        attendantCB = new javax.swing.JComboBox();
        barOptnPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        gateBarCB = new javax.swing.JComboBox();
        affiliationBuildingPanel = new javax.swing.JPanel();
        affiliPanel = new javax.swing.JPanel();
        affiliationRadioButton = new javax.swing.JRadioButton();
        searchL1ComboBox = new PComboBox();
        searchL2ComboBox = new PComboBox<InnoComboBoxItem>();
        buildingPanel = new javax.swing.JPanel();
        buildingRadioButton = new javax.swing.JRadioButton();
        searchBuildingComboBox = new PComboBox();
        searchUnitComboBox = new PComboBox();
        clearSearchPropertiesButton = new javax.swing.JButton();
        searchBottom = new javax.swing.JPanel();
        oneHourRadioButton = new javax.swing.JRadioButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        oneDayRadioButton = new javax.swing.JRadioButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        periodRadioButton = new javax.swing.JRadioButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        jLabel9 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(32767, 0));
        setSearchPeriodOptionButton = new javax.swing.JButton();
        BeginDateChooser = new com.toedter.calendar.JDateChooser();
        EndDateChooser = new com.toedter.calendar.JDateChooser();
        searchButton = new javax.swing.JButton();
        closePanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        bottomLeftPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        rowNumTF = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel5 = new javax.swing.JLabel();
        arrivalTmTF = new javax.swing.JTextField();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel8 = new javax.swing.JLabel();
        recognizedTF = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        gateNameTF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        attendantTF = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        registeredTF = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        buildingLabel = new javax.swing.JLabel();
        buildingTF = new javax.swing.JTextField();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel10 = new javax.swing.JLabel();
        unitTF = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        affiliationLabel = new javax.swing.JLabel();
        affiliationTF = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        jLabel14 = new javax.swing.JLabel();
        visitPurposeTF = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel13 = new javax.swing.JLabel();
        imgSizeTF = new javax.swing.JTextField();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel12 = new javax.swing.JLabel();
        barOptnTF = new javax.swing.JTextField();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel16 = new javax.swing.JLabel();
        rsCountTF = new javax.swing.JTextField();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 2));
        jPanel7 = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        bottomRightPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        arrivalsList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(CAR_ARRIVALS_FRAME_TITLE.getContent());
        setFocusCycleRoot(false);
        setMinimumSize(new java.awt.Dimension(1200, 850));
        setPreferredSize(new java.awt.Dimension(1150, 850));

        wholePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        topPanel.setMaximumSize(new java.awt.Dimension(2147483647, 210));
        topPanel.setPreferredSize(new java.awt.Dimension(910, 280));

        searchPanel.setBackground(new java.awt.Color(243, 243, 243));
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, SEARCH_CRITERIA_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchPanel.setAlignmentX(0.0F);
        searchPanel.setMinimumSize(new java.awt.Dimension(800, 113));
        searchPanel.setPreferredSize(new java.awt.Dimension(900, 400));

        criteriaPanel.setMinimumSize(new java.awt.Dimension(800, 80));
        criteriaPanel.setPreferredSize(new java.awt.Dimension(882, 230));

        searchTop.setBackground(new java.awt.Color(243, 243, 243));
        searchTop.setBorder(javax.swing.BorderFactory.createTitledBorder(null, ARRIVAL_PROPERTIES_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchTop.setForeground(new java.awt.Color(255, 255, 255));
        searchTop.setAlignmentX(0.0F);
        searchTop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        searchTop.setMaximumSize(new java.awt.Dimension(32767, 120));
        searchTop.setMinimumSize(new java.awt.Dimension(800, 70));
        searchTop.setPreferredSize(new java.awt.Dimension(880, 120));

        gatePanel.setBackground(new java.awt.Color(243, 243, 243));
        gatePanel.setPreferredSize(new java.awt.Dimension(82, 39));
        gatePanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setBackground(new java.awt.Color(243, 243, 243));
        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setLabelFor(gateCB);
        jLabel1.setText(GATE_NAME_LABEL.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(32767, 32767));
        gatePanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        gateCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        gateCB.setPreferredSize(new java.awt.Dimension(82, 23));
        gatePanel.add(gateCB, java.awt.BorderLayout.PAGE_END);

        carTagPanel.setBackground(new java.awt.Color(243, 243, 243));
        carTagPanel.setPreferredSize(new java.awt.Dimension(122, 39));
        carTagPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setBackground(new java.awt.Color(243, 243, 243));
        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setLabelFor(carTagTF);
        jLabel2.setText(CAR_TAG_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(32767, 32767));
        carTagPanel.add(jLabel2, java.awt.BorderLayout.CENTER);

        carTagTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        carTagTF.setToolTipText(CAR_TAG_TF_TOOLTIP.getContent());
        carTagTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        carTagTF.setPreferredSize(new java.awt.Dimension(120, 23));
        carTagPanel.add(carTagTF, java.awt.BorderLayout.PAGE_END);

        attendPanel.setBackground(new java.awt.Color(243, 243, 243));
        attendPanel.setPreferredSize(new java.awt.Dimension(122, 39));
        attendPanel.setLayout(new java.awt.BorderLayout());

        jLabel15.setBackground(new java.awt.Color(243, 243, 243));
        jLabel15.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setLabelFor(attendantCB);
        jLabel15.setText(ATTENDANT_LABEL.getContent());
        jLabel15.setMaximumSize(new java.awt.Dimension(32767, 32767));
        attendPanel.add(jLabel15, java.awt.BorderLayout.CENTER);

        attendantCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        attendantCB.setModel(new javax.swing.DefaultComboBoxModel(new Object[] {
            new ConvComboBoxItem("", ATTENDANT_CB_ITEM.getContent())
        }));
        attendantCB.setMinimumSize(new java.awt.Dimension(120, 23));
        attendantCB.setPreferredSize(new java.awt.Dimension(120, 23));
        attendantCB.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                attendantCBPopupMenuWillBecomeVisible(evt);
            }
        });
        attendPanel.add(attendantCB, java.awt.BorderLayout.PAGE_END);

        barOptnPanel.setBackground(new java.awt.Color(243, 243, 243));
        barOptnPanel.setPreferredSize(new java.awt.Dimension(130, 39));
        barOptnPanel.setLayout(new java.awt.BorderLayout());

        jLabel20.setBackground(new java.awt.Color(243, 243, 243));
        jLabel20.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText(BAR_OP_LABEL.getContent());
        jLabel20.setMaximumSize(new java.awt.Dimension(32767, 32767));
        barOptnPanel.add(jLabel20, java.awt.BorderLayout.CENTER);

        gateBarCB.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateBarCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        gateBarCB.setPreferredSize(new java.awt.Dimension(120, 23));
        barOptnPanel.add(gateBarCB, java.awt.BorderLayout.PAGE_END);

        affiliationBuildingPanel.setBackground(new java.awt.Color(243, 243, 243));
        affiliationBuildingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        affiliationBuildingPanel.setDoubleBuffered(false);
        affiliationBuildingPanel.setMaximumSize(new java.awt.Dimension(1000, 71));
        affiliationBuildingPanel.setMinimumSize(new java.awt.Dimension(300, 71));
        affiliationBuildingPanel.setPreferredSize(new java.awt.Dimension(300, 71));

        affiliPanel.setBackground(new java.awt.Color(243, 243, 243));
        affiliPanel.setMinimumSize(new java.awt.Dimension(150, 69));
        affiliPanel.setPreferredSize(new java.awt.Dimension(150, 69));
        affiliPanel.setRequestFocusEnabled(false);
        affiliPanel.setLayout(new javax.swing.BoxLayout(affiliPanel, javax.swing.BoxLayout.Y_AXIS));

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
        affiliPanel.add(affiliationRadioButton);

        searchL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchL1ComboBox.setMinimumSize(new java.awt.Dimension(100, 23));
        searchL1ComboBox.setPreferredSize(new java.awt.Dimension(100, 23));
        searchL1ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
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
        searchL1ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
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
        affiliPanel.add(searchL1ComboBox);

        searchL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL2ComboBox.setModel(    new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchL2ComboBox.setMinimumSize(new java.awt.Dimension(100, 23));
        searchL2ComboBox.setPreferredSize(new java.awt.Dimension(100, 23));
        searchL2ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
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
        affiliPanel.add(searchL2ComboBox);

        buildingPanel.setBackground(new java.awt.Color(243, 243, 243));
        buildingPanel.setLayout(new javax.swing.BoxLayout(buildingPanel, javax.swing.BoxLayout.PAGE_AXIS));

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
        buildingPanel.add(buildingRadioButton);

        searchBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBuildingComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchBuildingComboBox.setMinimumSize(new java.awt.Dimension(80, 21));
        searchBuildingComboBox.setPreferredSize(new java.awt.Dimension(80, 23));
        searchBuildingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchBuildingComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchBuildingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBuildingComboBoxActionPerformed(evt);
            }
        });
        buildingPanel.add(searchBuildingComboBox);

        searchUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchUnitComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[]{}));
        searchUnitComboBox.setMinimumSize(new java.awt.Dimension(80, 21));
        searchUnitComboBox.setPreferredSize(new java.awt.Dimension(80, 23));
        searchUnitComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
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
        buildingPanel.add(searchUnitComboBox);

        javax.swing.GroupLayout affiliationBuildingPanelLayout = new javax.swing.GroupLayout(affiliationBuildingPanel);
        affiliationBuildingPanel.setLayout(affiliationBuildingPanelLayout);
        affiliationBuildingPanelLayout.setHorizontalGroup(
            affiliationBuildingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(affiliationBuildingPanelLayout.createSequentialGroup()
                .addComponent(affiliPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(buildingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
        );
        affiliationBuildingPanelLayout.setVerticalGroup(
            affiliationBuildingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(affiliPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        clearSearchPropertiesButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearSearchPropertiesButton.setMnemonic('l');
        clearSearchPropertiesButton.setText(CLEAR_BTN.getContent());
        clearSearchPropertiesButton.setToolTipText(CLEAR_BTN_TOOLTIP.getContent());
        clearSearchPropertiesButton.setPreferredSize(new java.awt.Dimension(100, 35));
        clearSearchPropertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSearchPropertiesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchTopLayout = new javax.swing.GroupLayout(searchTop);
        searchTop.setLayout(searchTopLayout);
        searchTopLayout.setHorizontalGroup(
            searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchTopLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(gatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(carTagPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(attendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(barOptnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(affiliationBuildingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(clearSearchPropertiesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        searchTopLayout.setVerticalGroup(
            searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchTopLayout.createSequentialGroup()
                .addGroup(searchTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(gatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(carTagPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(attendPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(barOptnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(affiliationBuildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchTopLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(clearSearchPropertiesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        searchBottom.setBackground(new java.awt.Color(244, 244, 244));
        searchBottom.setBorder(javax.swing.BorderFactory.createTitledBorder(null, ARRIVAL_TIME_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        searchBottom.setForeground(new java.awt.Color(153, 153, 153));
        searchBottom.setAlignmentX(0.0F);
        searchBottom.setFocusTraversalPolicyProvider(true);
        searchBottom.setMaximumSize(new java.awt.Dimension(32767, 90));
        searchBottom.setMinimumSize(new java.awt.Dimension(800, 70));
        searchBottom.setPreferredSize(new java.awt.Dimension(880, 90));

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

        jLabel9.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("~");
        jLabel9.setFocusable(false);
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        setSearchPeriodOptionButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        setSearchPeriodOptionButton.setMnemonic('f');
        setSearchPeriodOptionButton.setText(FIX_IT_BTN.getContent());
        setSearchPeriodOptionButton.setToolTipText(FIX_IT_BTN_TOOLTIP.getContent());
        setSearchPeriodOptionButton.setPreferredSize(new java.awt.Dimension(77, 35));
        setSearchPeriodOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSearchPeriodOptionButtonActionPerformed(evt);
            }
        });

        BeginDateChooser.setAlignmentY(0.0F);
        BeginDateChooser.setAutoscrolls(true);
        BeginDateChooser.setEnabled(false);
        BeginDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BeginDateChooser.setPreferredSize(new java.awt.Dimension(150, 33));

        EndDateChooser.setAlignmentY(0.0F);
        EndDateChooser.setAutoscrolls(true);
        EndDateChooser.setEnabled(false);
        EndDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EndDateChooser.setPreferredSize(new java.awt.Dimension(150, 33));

        javax.swing.GroupLayout searchBottomLayout = new javax.swing.GroupLayout(searchBottom);
        searchBottom.setLayout(searchBottomLayout);
        searchBottomLayout.setHorizontalGroup(
            searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGap(87, 87, 87)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BeginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EndDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(setSearchPeriodOptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        searchBottomLayout.setVerticalGroup(
            searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchBottomLayout.createSequentialGroup()
                .addGroup(searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(oneHourRadioButton))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(oneDayRadioButton))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(periodRadioButton))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel9))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(searchBottomLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(searchBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BeginDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(setSearchPeriodOptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(EndDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout criteriaPanelLayout = new javax.swing.GroupLayout(criteriaPanel);
        criteriaPanel.setLayout(criteriaPanelLayout);
        criteriaPanelLayout.setHorizontalGroup(
            criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(criteriaPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchTop, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
                    .addComponent(searchBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        criteriaPanelLayout.setVerticalGroup(
            criteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(criteriaPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(searchTop, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(searchBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
        );

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        searchButton.setMaximumSize(new java.awt.Dimension(80, 80));
        searchButton.setMinimumSize(new java.awt.Dimension(80, 80));
        searchButton.setPreferredSize(new java.awt.Dimension(80, 80));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(criteriaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(criteriaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        closePanel.setMaximumSize(new java.awt.Dimension(87, 32767));

        closeButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeButton.setMnemonic('c');
        closeButton.setText(CLOSE_BTN.getContent());
        closeButton.setMaximumSize(new java.awt.Dimension(77, 52));
        closeButton.setMinimumSize(new java.awt.Dimension(77, 52));
        closeButton.setPreferredSize(new java.awt.Dimension(85, 52));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closePanel.add(closeButton);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 993, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(closePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(closePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        wholePanel.add(topPanel);

        bottomPanel.setPreferredSize(new java.awt.Dimension(965, 400));
        bottomPanel.setLayout(new javax.swing.BoxLayout(bottomPanel, javax.swing.BoxLayout.LINE_AXIS));

        bottomLeftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, VEHICLE_ARIIVAL_DETAILS_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        bottomLeftPanel.setMaximumSize(new java.awt.Dimension(32767, 163835));
        bottomLeftPanel.setMinimumSize(new java.awt.Dimension(400, 195));
        bottomLeftPanel.setPreferredSize(new java.awt.Dimension(650, 445));
        bottomLeftPanel.setLayout(new javax.swing.BoxLayout(bottomLeftPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setMinimumSize(new java.awt.Dimension(570, 198));
        jPanel4.setPreferredSize(new java.awt.Dimension(590, 300));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel1.setPreferredSize(new java.awt.Dimension(185, 25));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(ORDER_LABEL.getContent());
        jLabel3.setMaximumSize(new java.awt.Dimension(80, 35));
        jLabel3.setMinimumSize(new java.awt.Dimension(80, 35));
        jLabel3.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel1.add(jLabel3);

        rowNumTF.setEditable(false);
        rowNumTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rowNumTF.setMaximumSize(new java.awt.Dimension(50, 35));
        rowNumTF.setPreferredSize(new java.awt.Dimension(60, 25));
        jPanel1.add(rowNumTF);
        jPanel1.add(filler1);

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(ARRIVAL_TIME_LABEL.getContent());
        jLabel5.setMaximumSize(new java.awt.Dimension(100, 35));
        jLabel5.setMinimumSize(new java.awt.Dimension(100, 35));
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanel1.add(jLabel5);

        arrivalTmTF.setEditable(false);
        arrivalTmTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        arrivalTmTF.setMaximumSize(new java.awt.Dimension(100, 35));
        arrivalTmTF.setMinimumSize(new java.awt.Dimension(100, 35));
        arrivalTmTF.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanel1.add(arrivalTmTF);
        jPanel1.add(filler7);

        jLabel8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(RECOGNIZED_LABEL.getContent());
        jLabel8.setMaximumSize(new java.awt.Dimension(100, 35));
        jLabel8.setMinimumSize(new java.awt.Dimension(100, 35));
        jLabel8.setPreferredSize(new java.awt.Dimension(120, 35));
        jPanel1.add(jLabel8);

        recognizedTF.setEditable(false);
        recognizedTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        recognizedTF.setMaximumSize(new java.awt.Dimension(130, 35));
        recognizedTF.setMinimumSize(new java.awt.Dimension(130, 35));
        recognizedTF.setPreferredSize(new java.awt.Dimension(150, 35));
        jPanel1.add(recognizedTF);

        jPanel4.add(jPanel1);

        jPanel3.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(GATE_NAME_LABEL.getContent());
        jLabel4.setMaximumSize(new java.awt.Dimension(80, 35));
        jLabel4.setMinimumSize(new java.awt.Dimension(80, 35));
        jLabel4.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel3.add(jLabel4);

        gateNameTF.setEditable(false);
        gateNameTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        gateNameTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        gateNameTF.setPreferredSize(new java.awt.Dimension(59, 25));
        jPanel3.add(gateNameTF);

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText(ATTENDANT_LABEL.getContent());
        jLabel6.setMaximumSize(new java.awt.Dimension(100, 35));
        jLabel6.setMinimumSize(new java.awt.Dimension(100, 35));
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanel3.add(jLabel6);

        attendantTF.setEditable(false);
        attendantTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        attendantTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        attendantTF.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel3.add(attendantTF);

        jLabel7.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(REGISTERED_LABEL.getContent());
        jLabel7.setMaximumSize(new java.awt.Dimension(100, 35));
        jLabel7.setMinimumSize(new java.awt.Dimension(100, 35));
        jLabel7.setPreferredSize(new java.awt.Dimension(120, 35));
        jPanel3.add(jLabel7);

        registeredTF.setEditable(false);
        registeredTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        registeredTF.setMaximumSize(new java.awt.Dimension(130, 35));
        registeredTF.setMinimumSize(new java.awt.Dimension(130, 35));
        registeredTF.setPreferredSize(new java.awt.Dimension(150, 35));
        jPanel3.add(registeredTF);

        jPanel4.add(jPanel3);

        jPanel5.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));
        jPanel5.add(filler13);

        buildingLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        buildingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buildingLabel.setText(BUILDING_LABEL.getContent());
        buildingLabel.setMaximumSize(new java.awt.Dimension(130, 35));
        buildingLabel.setMinimumSize(new java.awt.Dimension(130, 35));
        buildingLabel.setPreferredSize(new java.awt.Dimension(130, 35));
        jPanel5.add(buildingLabel);

        buildingTF.setEditable(false);
        buildingTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        buildingTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        buildingTF.setPreferredSize(new java.awt.Dimension(80, 25));
        jPanel5.add(buildingTF);
        jPanel5.add(filler12);

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(ROOM_LABEL.getContent());
        jLabel10.setMaximumSize(new java.awt.Dimension(80, 35));
        jLabel10.setMinimumSize(new java.awt.Dimension(80, 35));
        jLabel10.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel5.add(jLabel10);

        unitTF.setEditable(false);
        unitTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        unitTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        unitTF.setPreferredSize(new java.awt.Dimension(80, 25));
        jPanel5.add(unitTF);

        jPanel4.add(jPanel5);

        jPanel8.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jPanel8.add(filler15);

        affiliationLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        affiliationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        affiliationLabel.setText(AFFILIATION_LABEL.getContent());
        affiliationLabel.setMaximumSize(new java.awt.Dimension(130, 35));
        affiliationLabel.setMinimumSize(new java.awt.Dimension(130, 35));
        affiliationLabel.setPreferredSize(new java.awt.Dimension(130, 35));
        jPanel8.add(affiliationLabel);

        affiliationTF.setEditable(false);
        affiliationTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        affiliationTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        affiliationTF.setPreferredSize(new java.awt.Dimension(340, 25));
        jPanel8.add(affiliationTF);

        jPanel4.add(jPanel8);

        jPanel6.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));
        jPanel6.add(filler14);

        jLabel14.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(VISIT_PURPOSE_LABEL.getContent());
        jLabel14.setMaximumSize(new java.awt.Dimension(130, 35));
        jLabel14.setMinimumSize(new java.awt.Dimension(130, 35));
        jLabel14.setPreferredSize(new java.awt.Dimension(130, 35));
        jPanel6.add(jLabel14);

        visitPurposeTF.setEditable(false);
        visitPurposeTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        visitPurposeTF.setMaximumSize(new java.awt.Dimension(32767, 32767));
        visitPurposeTF.setPreferredSize(new java.awt.Dimension(340, 25));
        jPanel6.add(visitPurposeTF);

        jPanel4.add(jPanel6);

        jPanel2.setMaximumSize(new java.awt.Dimension(196602, 35));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(filler10);

        jLabel13.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(FILE_SIZE_LABEL.getContent());
        jLabel13.setMaximumSize(new java.awt.Dimension(80, 35));
        jLabel13.setMinimumSize(new java.awt.Dimension(80, 35));
        jLabel13.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel2.add(jLabel13);

        imgSizeTF.setEditable(false);
        imgSizeTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        imgSizeTF.setMaximumSize(new java.awt.Dimension(80, 35));
        imgSizeTF.setMinimumSize(new java.awt.Dimension(80, 35));
        imgSizeTF.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel2.add(imgSizeTF);
        jPanel2.add(filler8);

        jLabel12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(BAR_OP_LABEL.getContent());
        jLabel12.setMaximumSize(new java.awt.Dimension(120, 35));
        jLabel12.setMinimumSize(new java.awt.Dimension(120, 35));
        jLabel12.setPreferredSize(new java.awt.Dimension(120, 35));
        jPanel2.add(jLabel12);

        barOptnTF.setEditable(false);
        barOptnTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        barOptnTF.setMaximumSize(new java.awt.Dimension(120, 35));
        barOptnTF.setMinimumSize(new java.awt.Dimension(120, 35));
        barOptnTF.setPreferredSize(new java.awt.Dimension(120, 35));
        jPanel2.add(barOptnTF);
        jPanel2.add(filler9);

        jLabel16.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText(RECORD_COUNT_LABEL.getContent());
        jLabel16.setMaximumSize(new java.awt.Dimension(120, 35));
        jLabel16.setMinimumSize(new java.awt.Dimension(120, 35));
        jLabel16.setPreferredSize(new java.awt.Dimension(120, 35));
        jPanel2.add(jLabel16);

        rsCountTF.setEditable(false);
        rsCountTF.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rsCountTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rsCountTF.setMaximumSize(new java.awt.Dimension(80, 35));
        rsCountTF.setMinimumSize(new java.awt.Dimension(80, 35));
        rsCountTF.setPreferredSize(new java.awt.Dimension(80, 35));
        jPanel2.add(rsCountTF);
        jPanel2.add(filler11);

        jPanel4.add(jPanel2);

        bottomLeftPanel.add(jPanel4);
        bottomLeftPanel.add(filler2);

        jPanel7.setPreferredSize(new java.awt.Dimension(450, 240));

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        imageLabel.setPreferredSize(new java.awt.Dimension(339, 220));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageLabelMouseClicked(evt);
            }
        });
        jPanel7.add(imageLabel);

        bottomLeftPanel.add(jPanel7);

        bottomPanel.add(bottomLeftPanel);

        bottomRightPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, VEHICLE_ARRIVAL_LIST_PANEL_TITLE.getContent(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(font_Type, font_Style, font_Size)));
        bottomRightPanel.setMaximumSize(new java.awt.Dimension(32779, 33293));
        bottomRightPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        bottomRightPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 27));

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
    jScrollPane1.setViewportView(arrivalsList);

    bottomRightPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    bottomPanel.add(bottomRightPanel);

    wholePanel.add(bottomPanel);

    getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

    setBounds(0, 0, 1102, 726);
    }// </editor-fold>//GEN-END:initComponents

    private void imageLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLabelMouseClicked
        // TODO add your handling code here:
        if (originalImg != null)
        {
//            ImageDisplay bigImage = new ImageDisplay(originalImg, "100% " + ((String[])Globals.TitleList.get(FULL_SIZE_IMAGE_FRAME_TITLE.ordinal()))[ourLang]);
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
        // TODO add your handling code here:
        disablePeriodChooser();
    }//GEN-LAST:event_oneDayRadioButtonActionPerformed

    private void oneHourRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneHourRadioButtonActionPerformed
        disablePeriodChooser();
    }//GEN-LAST:event_oneHourRadioButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // TODO add your handling code here:
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void searchUnitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchUnitComboBoxActionPerformed
        buildingRadioButton.setEnabled(true);
    }//GEN-LAST:event_searchUnitComboBoxActionPerformed

    private void searchUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
        loadUnitComboBox(searchL1ComboBox, searchBuildingComboBox, searchUnitComboBox);
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeVisible

    private void searchL2ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchL2ComboBoxActionPerformed
        affiliationRadioButton.setSelected(true);
    }//GEN-LAST:event_searchL2ComboBoxActionPerformed
    
    private void searchL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeVisible
        Object selItem = searchL2ComboBox.getSelectedItem();

        ConvComboBoxItem l1Item = (ConvComboBoxItem)searchL1ComboBox.getSelectedItem();
        int L1No = (Integer) l1Item.getKeyValue();        // normalize child combobox item
        searchL2ComboBox.removeAllItems();
        searchL2ComboBox.addItem(getPrompter(AffiliationL2, searchL1ComboBox));
        loadComboBoxItems(searchL2ComboBox, DriverCol.AffiliationL2, L1No);
        searchL2ComboBox.setSelectedItem(selItem);
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

    private void searchBuildingComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxPopupMenuWillBecomeVisible
        Object selItem = searchBuildingComboBox.getSelectedItem();

        searchBuildingComboBox.removeAllItems();
        searchBuildingComboBox.addItem(getPrompter(BuildingNo, null
        ));
        loadComboBoxItems(searchBuildingComboBox, BuildingNo, -1);
        searchBuildingComboBox.setSelectedItem(selItem);
    }//GEN-LAST:event_searchBuildingComboBoxPopupMenuWillBecomeVisible

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

    private void searchL1ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL1ComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
        Object selItem = searchL1ComboBox.getSelectedItem();

        searchL1ComboBox.removeAllItems();
        searchL1ComboBox.addItem(getPrompter(AffiliationL1, null));
        loadComboBoxItems(searchL1ComboBox, AffiliationL1, -1);
        searchL1ComboBox.setSelectedItem(selItem);
    }//GEN-LAST:event_searchL1ComboBoxPopupMenuWillBecomeVisible

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

    // <editor-fold defaultstate="collapsed" desc="-- Variables defined via GUI creation">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser BeginDateChooser;
    private com.toedter.calendar.JDateChooser EndDateChooser;
    private javax.swing.JPanel affiliPanel;
    private javax.swing.JPanel affiliationBuildingPanel;
    private javax.swing.ButtonGroup affiliationGroup;
    private javax.swing.JLabel affiliationLabel;
    private javax.swing.JRadioButton affiliationRadioButton;
    private javax.swing.JTextField affiliationTF;
    private javax.swing.JTextField arrivalTmTF;
    private javax.swing.JTable arrivalsList;
    private javax.swing.JPanel attendPanel;
    private javax.swing.JComboBox attendantCB;
    private javax.swing.JTextField attendantTF;
    private javax.swing.JPanel barOptnPanel;
    private javax.swing.JTextField barOptnTF;
    private javax.swing.JPanel bottomLeftPanel;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel bottomRightPanel;
    private javax.swing.JLabel buildingLabel;
    private javax.swing.JPanel buildingPanel;
    private javax.swing.JRadioButton buildingRadioButton;
    private javax.swing.JTextField buildingTF;
    private javax.swing.JPanel carTagPanel;
    private javax.swing.JTextField carTagTF;
    private javax.swing.JButton clearSearchPropertiesButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JPanel criteriaPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JComboBox gateBarCB;
    private javax.swing.JComboBox gateCB;
    private javax.swing.JTextField gateNameTF;
    private javax.swing.JPanel gatePanel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JTextField imgSizeTF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton oneDayRadioButton;
    private javax.swing.JRadioButton oneHourRadioButton;
    private javax.swing.ButtonGroup periodOptionGroup;
    private javax.swing.JRadioButton periodRadioButton;
    private javax.swing.JTextField recognizedTF;
    private javax.swing.JTextField registeredTF;
    private javax.swing.JTextField rowNumTF;
    private javax.swing.JTextField rsCountTF;
    private javax.swing.JPanel searchBottom;
    private javax.swing.JComboBox searchBuildingComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchL1ComboBox;
    private javax.swing.JComboBox searchL2ComboBox;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchTop;
    private javax.swing.JComboBox searchUnitComboBox;
    private javax.swing.JButton setSearchPeriodOptionButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField unitTF;
    private javax.swing.JTextField visitPurposeTF;
    private javax.swing.JPanel wholePanel;
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
        
        rsCountTF.setText(Integer.toString(numRows));
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
                            buildingLabel.setText(NON_REGI_TAG1_LABEL.getContent());
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
                        int numBytes = (Integer)rs.getInt("imgBytes");
                        imgSizeTF.setText(getFilesizeStr(numBytes)); 
                        
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
        buildingLabel.setText(NON_REGI_TAG1_LABEL.getContent());
        affiliationLabel.setText(NON_REGI_TAG2_LABEL.getContent());
        buildingTF.setText("");
        unitTF.setText("");
        affiliationTF.setText("");    
        visitPurposeTF.setText(""); 
        barOptnTF.setText("");
        imgSizeTF.setText("");
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
        
        initSearchComboBox(searchL1ComboBox, searchL2ComboBox,
                searchBuildingComboBox, searchUnitComboBox);        
        
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
            //<editor-fold desc="-- determine label for each item value">
            String label = "";

            label = getBarOperationLabel(barOperation);

            //</editor-fold>                
            gateBarCB.addItem(new ConvComboBoxItem(barOperation, label));
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
        
