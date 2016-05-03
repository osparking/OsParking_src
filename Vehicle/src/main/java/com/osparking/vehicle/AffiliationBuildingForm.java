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
package com.osparking.vehicle;

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
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
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.insertNewBuilding;
import static com.osparking.global.Globals.insertNewBuildingUnit;
import static com.osparking.global.Globals.insertNewLevel1Affiliation;
import static com.osparking.global.Globals.insertNewLevel2Affiliation;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.rejectEmptyInput;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.Globals.shortLicenseDialog;
import static com.osparking.global.Globals.showLicensePanel;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.AFFILIATION_DELETE_ALL_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.AFFILIATION_DELETE_ALL_RESULT_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_DELETE_ALL_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_DELETE_ALL_RESULT_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.BUILDING_IN_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.LEVEL1_NAME_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.LEVEL2_NAME_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.ROOM_IN_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.AFFILIATION_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BUILDING_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_ALL_DAILOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_ALL_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.LOWER_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.READ_ODS_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.READ_ODS_FAIL_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.REJECT_USER_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.UNIT_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BUILDING_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_AFFIL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_BUILDING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ROOM_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.TableTypes.*;
import static com.osparking.global.names.ControlEnums.TitleTypes.AFFILI_BUILD_FRAME_TITLE;
import static com.osparking.global.names.DB_Access.parkingLotLocale;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.FormMode;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.WrappedInt;
import com.osparking.vehicle.driver.ODSReader;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
    FormMode formMode = FormMode.SEARCHING;
    /**
     * Creates new form BuildingManageFrame
     */
    public AffiliationBuildingForm() {
        initComponents();

        /**
         * Set icon for the simulated camera program
         */
        setIconImages(OSPiconList);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(PopUpBackground);       
        adjustTables();
        loadL1_Affiliation(0, "");
        loadBuilding(0, 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        odsFileChooser = new javax.swing.JFileChooser();
        wholePanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        topPanel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        L1_Affiliation = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        insertL1_Button = new javax.swing.JButton();
        modifyL1_Button = new javax.swing.JButton();
        deleteL1_Button = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel2 = new javax.swing.JPanel();
        L2AffilLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        L2_Affiliation = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        insertL2_Button = new javax.swing.JButton();
        modifyL2_Button = new javax.swing.JButton();
        deleteL2_Button = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel3 = new javax.swing.JPanel();
        deleteAll_Affiliation = new javax.swing.JButton();
        readSheet_Affiliation = new javax.swing.JButton();
        ODSAffiliHelp = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jPanel9 = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        BuildingTable = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        insertBuilding_Button = new javax.swing.JButton();
        modifyBuilding_Button = new javax.swing.JButton();
        deleteBuilding_Button = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel5 = new javax.swing.JPanel();
        UnitLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        UnitTable = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        insertUnit_Button = new javax.swing.JButton();
        modifyUnit_Button = new javax.swing.JButton();
        deleteUnit_Button = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel6 = new javax.swing.JPanel();
        deleteAll_Button = new javax.swing.JButton();
        readSheet_Button = new javax.swing.JButton();
        ODSBuildPWHelp = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));

        odsFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AFFILI_BUILD_FRAME_TITLE.getContent());
        setBackground(PopUpBackground);
        setMinimumSize(new java.awt.Dimension(740, 550));

        wholePanel.setLayout(new java.awt.BorderLayout());
        wholePanel.add(filler1, java.awt.BorderLayout.WEST);

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        topPanel.add(seeLicenseButton);
        topPanel.add(filler11);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText(CLOSE_BTN.getContent());
        closeFormButton.setMaximumSize(new java.awt.Dimension(90, 40));
        closeFormButton.setMinimumSize(new java.awt.Dimension(90, 40));
        closeFormButton.setPreferredSize(new java.awt.Dimension(90, 40));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        topPanel.add(closeFormButton);

        wholePanel.add(topPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setPreferredSize(new java.awt.Dimension(500, 489));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.LINE_AXIS));

        jPanel8.setMinimumSize(new java.awt.Dimension(300, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(700, 489));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.Y_AXIS));
        jPanel8.add(filler5);

        jPanel1.setMinimumSize(new java.awt.Dimension(83, 85));
        jPanel1.setPreferredSize(new java.awt.Dimension(588, 168));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setText(AFFILIATION_LIST_LABEL.getContent());
        jPanel1.add(jLabel1, java.awt.BorderLayout.PAGE_START);
        jLabel1.getAccessibleContext().setAccessibleName("");

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
    jScrollPane1.setViewportView(L1_Affiliation);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    jPanel7.setPreferredSize(new java.awt.Dimension(90, 120));
    jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

    insertL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertL1_Button.setText(CREATE_NO_SHORT_BTN.getContent());
    insertL1_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    insertL1_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    insertL1_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    insertL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertL1_ButtonActionPerformed(evt);
        }
    });
    jPanel7.add(insertL1_Button);

    modifyL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyL1_Button.setText(MODIFY_NO_SHORT_BTN.getContent());
    modifyL1_Button.setEnabled(false);
    modifyL1_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    modifyL1_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    modifyL1_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    modifyL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyL1_ButtonActionPerformed(evt);
        }
    });
    jPanel7.add(modifyL1_Button);

    deleteL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteL1_Button.setText(DELETE_NO_SHORT_BTN.getContent());
    deleteL1_Button.setEnabled(false);
    deleteL1_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteL1_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    deleteL1_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    deleteL1_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteL1_ButtonActionPerformed(evt);
        }
    });
    jPanel7.add(deleteL1_Button);

    jPanel1.add(jPanel7, java.awt.BorderLayout.EAST);
    jPanel1.add(filler8, java.awt.BorderLayout.PAGE_END);

    jPanel8.add(jPanel1);

    jPanel2.setMinimumSize(new java.awt.Dimension(83, 85));
    jPanel2.setPreferredSize(new java.awt.Dimension(588, 168));
    jPanel2.setLayout(new java.awt.BorderLayout());

    L2AffilLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    L2AffilLabel.setText(LOWER_LIST_LABEL.getContent());
    jPanel2.add(L2AffilLabel, java.awt.BorderLayout.NORTH);

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
    jScrollPane2.setViewportView(L2_Affiliation);

    jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.Y_AXIS));

    insertL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertL2_Button.setText(CREATE_NO_SHORT_BTN.getContent());
    insertL2_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    insertL2_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    insertL2_Button.setName(""); // NOI18N
    insertL2_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    insertL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertL2_ButtonActionPerformed(evt);
        }
    });
    jPanel10.add(insertL2_Button);

    modifyL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyL2_Button.setText(MODIFY_NO_SHORT_BTN.getContent());
    modifyL2_Button.setEnabled(false);
    modifyL2_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    modifyL2_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    modifyL2_Button.setName(""); // NOI18N
    modifyL2_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    modifyL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyL2_ButtonActionPerformed(evt);
        }
    });
    jPanel10.add(modifyL2_Button);

    deleteL2_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteL2_Button.setText(DELETE_NO_SHORT_BTN.getContent());
    deleteL2_Button.setEnabled(false);
    deleteL2_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteL2_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    deleteL2_Button.setName(""); // NOI18N
    deleteL2_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    deleteL2_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteL2_ButtonActionPerformed(evt);
        }
    });
    jPanel10.add(deleteL2_Button);

    jPanel2.add(jPanel10, java.awt.BorderLayout.EAST);
    jPanel2.add(filler7, java.awt.BorderLayout.PAGE_END);

    jPanel8.add(jPanel2);

    jPanel3.setMinimumSize(new java.awt.Dimension(212, 33));
    jPanel3.setPreferredSize(new java.awt.Dimension(318, 50));

    deleteAll_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteAll_Affiliation.setText(DELETE_ALL_BTN.getContent());
    deleteAll_Affiliation.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteAll_Affiliation.setPreferredSize(new java.awt.Dimension(110, 40));
    deleteAll_Affiliation.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteAll_AffiliationActionPerformed(evt);
        }
    });
    jPanel3.add(deleteAll_Affiliation);

    readSheet_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    readSheet_Affiliation.setText(READ_ODS_BTN.getContent());
    readSheet_Affiliation.setMaximumSize(new java.awt.Dimension(90, 40));
    readSheet_Affiliation.setPreferredSize(new java.awt.Dimension(110, 40));
    readSheet_Affiliation.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            readSheet_AffiliationActionPerformed(evt);
        }
    });
    jPanel3.add(readSheet_Affiliation);

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
    jPanel3.add(ODSAffiliHelp);

    jPanel8.add(jPanel3);

    centerPanel.add(jPanel8);
    centerPanel.add(filler4);

    jPanel9.setPreferredSize(new java.awt.Dimension(550, 406));
    jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.Y_AXIS));
    jPanel9.add(filler6);

    jPanel4.setMinimumSize(new java.awt.Dimension(83, 85));
    jPanel4.setPreferredSize(new java.awt.Dimension(588, 168));
    jPanel4.setLayout(new java.awt.BorderLayout());

    jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    jLabel4.setText(BUILDING_LIST_LABEL.getContent());
    jPanel4.add(jLabel4, java.awt.BorderLayout.NORTH);

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
    BuildingTable.setDoubleBuffered(true);
    L1_Affiliation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    BuildingTable.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            BuildingTableFocusLost(evt);
        }
    });
    BuildingTable.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            BuildingTableKeyReleased(evt);
        }
    });
    jScrollPane3.setViewportView(BuildingTable);

    jPanel4.add(jScrollPane3, java.awt.BorderLayout.CENTER);

    jPanel11.setPreferredSize(new java.awt.Dimension(90, 120));
    jPanel11.setRequestFocusEnabled(false);
    jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.Y_AXIS));

    insertBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertBuilding_Button.setText(CREATE_NO_SHORT_BTN.getContent());
    insertBuilding_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    insertBuilding_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    insertBuilding_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    insertBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertBuilding_ButtonActionPerformed(evt);
        }
    });
    jPanel11.add(insertBuilding_Button);

    modifyBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyBuilding_Button.setText(MODIFY_NO_SHORT_BTN.getContent());
    modifyBuilding_Button.setEnabled(false);
    modifyBuilding_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    modifyBuilding_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    modifyBuilding_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    modifyBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyBuilding_ButtonActionPerformed(evt);
        }
    });
    jPanel11.add(modifyBuilding_Button);

    deleteBuilding_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteBuilding_Button.setText(DELETE_NO_SHORT_BTN.getContent());
    deleteBuilding_Button.setEnabled(false);
    deleteBuilding_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteBuilding_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    deleteBuilding_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    deleteBuilding_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteBuilding_ButtonActionPerformed(evt);
        }
    });
    jPanel11.add(deleteBuilding_Button);

    jPanel4.add(jPanel11, java.awt.BorderLayout.EAST);
    jPanel4.add(filler9, java.awt.BorderLayout.PAGE_END);

    jPanel9.add(jPanel4);

    jPanel5.setMinimumSize(new java.awt.Dimension(83, 85));
    jPanel5.setPreferredSize(new java.awt.Dimension(588, 168));
    jPanel5.setLayout(new java.awt.BorderLayout());

    UnitLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    UnitLabel.setText(ROOM_LIST_LABEL.getContent());
    jPanel5.add(UnitLabel, java.awt.BorderLayout.NORTH);

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
    UnitTable.setDoubleBuffered(true);
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

    jPanel5.add(jScrollPane4, java.awt.BorderLayout.CENTER);

    jPanel12.setPreferredSize(new java.awt.Dimension(90, 120));
    jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.Y_AXIS));

    insertUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    insertUnit_Button.setText(CREATE_NO_SHORT_BTN.getContent());
    insertUnit_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    insertUnit_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    insertUnit_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    insertUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertUnit_ButtonActionPerformed(evt);
        }
    });
    jPanel12.add(insertUnit_Button);

    modifyUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    modifyUnit_Button.setText(MODIFY_NO_SHORT_BTN.getContent());
    modifyUnit_Button.setEnabled(false);
    modifyUnit_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    modifyUnit_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    modifyUnit_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    modifyUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modifyUnit_ButtonActionPerformed(evt);
        }
    });
    jPanel12.add(modifyUnit_Button);

    deleteUnit_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteUnit_Button.setText(DELETE_NO_SHORT_BTN.getContent());
    deleteUnit_Button.setEnabled(false);
    deleteUnit_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteUnit_Button.setMinimumSize(new java.awt.Dimension(90, 40));
    deleteUnit_Button.setPreferredSize(new java.awt.Dimension(90, 40));
    deleteUnit_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteUnit_ButtonActionPerformed(evt);
        }
    });
    jPanel12.add(deleteUnit_Button);

    jPanel5.add(jPanel12, java.awt.BorderLayout.EAST);
    jPanel5.add(filler10, java.awt.BorderLayout.PAGE_END);

    jPanel9.add(jPanel5);

    jPanel6.setMinimumSize(new java.awt.Dimension(212, 33));
    jPanel6.setPreferredSize(new java.awt.Dimension(318, 50));

    deleteAll_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    deleteAll_Button.setText(DELETE_ALL_BTN.getContent());
    deleteAll_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    deleteAll_Button.setPreferredSize(new java.awt.Dimension(110, 40));
    deleteAll_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteAll_ButtonActionPerformed(evt);
        }
    });
    jPanel6.add(deleteAll_Button);

    readSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
    readSheet_Button.setText(READ_ODS_BTN.getContent());
    readSheet_Button.setMaximumSize(new java.awt.Dimension(90, 40));
    readSheet_Button.setPreferredSize(new java.awt.Dimension(110, 40));
    readSheet_Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            readSheet_ButtonActionPerformed(evt);
        }
    });
    jPanel6.add(readSheet_Button);

    ODSBuildPWHelp.setBackground(new java.awt.Color(153, 255, 153));
    ODSBuildPWHelp.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
    ODSBuildPWHelp.setIcon(getQuest20_Icon());
    ODSBuildPWHelp.setAlignmentY(0.0F);
    ODSBuildPWHelp.setMargin(new java.awt.Insets(2, 4, 2, 4));
    ODSBuildPWHelp.setMinimumSize(new java.awt.Dimension(20, 20));
    ODSBuildPWHelp.setOpaque(false);
    ODSBuildPWHelp.setPreferredSize(new java.awt.Dimension(25, 25));
    ODSBuildPWHelp.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            ODSBuildPWHelpActionPerformed(evt);
        }
    });
    jPanel6.add(ODSBuildPWHelp);

    jPanel9.add(jPanel6);

    centerPanel.add(jPanel9);

    wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);
    wholePanel.add(filler2, java.awt.BorderLayout.EAST);
    wholePanel.add(filler3, java.awt.BorderLayout.PAGE_END);

    getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

    pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void addAffiliationSelectionListener() {
        ListSelectionModel cellSelectionModel = L1_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                if (!e.getValueIsAdjusting())
                {
                    System.out.println("valueChanged");
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
        
        cellSelectionModel = L2_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
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
        String L1Name = (String)L1_Affiliation.getModel().getValueAt(rowIndex, 1);
        
        L1Name = L1Name.trim();
        // Conditions to make this a new higher affiliation: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        { 
            // <editor-fold defaultstate="collapsed" desc="-- Create a Higher Affiliation">                  
            if (L1Name != null && !L1Name.isEmpty()) // Cond 2. Name field has some string
            { 
                int result = 0;
                // <editor-fold defaultstate="collapsed" desc="-- Insert New Higher name and Refresh the List">               
                try {
                    result = insertNewLevel1Affiliation(L1Name);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY) {
                        rejectUserInput(L1_Affiliation, rowIndex, LEVEL1_NAME_DIALOG.getContent());
                    }
                    else {
                        logParkingException(Level.SEVERE, ex, 
                                "(insertion tried level1 name: " + L1Name + ")");
                    }
                }                           
                if (result == 1)
                {
                    loadL1_Affiliation(-1, L1Name); // Refresh the list
                    insertL1_Button.setEnabled(true);
                }
                // </editor-fold>
            }
            else
            {
                removeEmptyRow(insertL1_Button, L1_Affiliation);                    
            } 
            // </editor-fold>
        }
        else 
        {
            // <editor-fold defaultstate="collapsed" desc="-- Handle higher affiliation name update">
            if (L1Name.isEmpty()) {
                rejectEmptyInput(L1_Affiliation, rowIndex, "Can't save empty string as a name"); 
            } else {
                Object L1No = model.getValueAt(rowIndex, 2);

                int result = 0;
                
                // <editor-fold defaultstate="collapsed" desc="-- Actual update of a higher affiliation name">
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
                        rejectUserInput(L1_Affiliation, rowIndex, LEVEL1_NAME_DIALOG.getContent());
                    }
                    else {
                        logParkingException(Level.SEVERE, ex, excepMsg);
                    }
                } finally {
                    closeDBstuff(conn, modifyAffiliation, null, excepMsg);
                }    
                //</editor-fold>   
                if (result == 1)
                {
                    loadL1_Affiliation(-1, L1Name); // Refresh higher affiliation list
                } 
            } 
            //</editor-fold>                             
        }
    }//GEN-LAST:event_L1_AffiliationKeyReleased

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
        
        String dialogMessage = "";
        
        switch (language) {
            case KOREAN:
                dialogMessage = "다음 소속 및 그 하위 소속을 삭제합니까?" + System.getProperty("line.separator") 
                + "소속명: '" + affiliation + "' (하위소속: " + count + " 건)";
                break;
                
            case ENGLISH:
                dialogMessage = "Want to delete the following affiliation and its lower affiliations?" 
                + System.getProperty("line.separator") 
                + " - Affiliation name: '" + affiliation 
                + "' (lower affiliations count: " + count + ")";
                break;
                
            default:
                break;
        }
            
        int result = JOptionPane.showConfirmDialog(this, dialogMessage,
                DELETE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
            //<editor-fold desc="delete upper level affliation (unit name)">
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = "(In deletion of: " + affiliation + ")";

            result = -1;
            try {
                conn = getConnection();
                createBuilding = conn.prepareStatement("Delete From L1_Affiliation Where L1_no = ?");
                createBuilding.setInt(1, L1_no);

                result = createBuilding.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);

                if (result == 1) {
                    loadL1_Affiliation(viewIndex, ""); // Deliver the index of deleted row
                    
                    dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "소속 '" + affiliation + 
                                "'이 성공적으로 삭제되었습니다";
                            break;
                            
                        case ENGLISH:
                            dialogMessage = "Affiliation '" + affiliation + 
                                "' has been successfully deleted";
                            break;
                            
                        default:
                            break;
                    }                    
                    
                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_deleteL1_ButtonActionPerformed

    private void insertL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertL1_ButtonActionPerformed
        prepareRecordInsertion(L1_Affiliation, insertL1_Button);
    }//GEN-LAST:event_insertL1_ButtonActionPerformed

    private void L2_AffiliationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_L2_AffiliationKeyReleased
        int rowIndex = L2_Affiliation.convertRowIndexToModel (L2_Affiliation.getSelectedRow());
        TableModel model = L2_Affiliation.getModel();        
        final String L2Name = (String)model.getValueAt(rowIndex, 1);
        
        // Conditions to make this a new lower affiliation: Cond 1 and cond 2
        if (model.getValueAt(rowIndex, 0) == null) // Cond 1. Row number field is null
        {
            // <editor-fold defaultstate="collapsed" desc="-- Create a Lower Affiliation">   
            if (L2Name != null && !L2Name.trim().isEmpty()) // Cond 2. Name field has some string
            {
                String name2 = L2Name.trim();
                int L1_index = L1_Affiliation.convertRowIndexToModel(L1_Affiliation.getSelectedRow());                
                Object L1_No = L1_Affiliation.getModel().getValueAt(L1_index, 2);

                int result = 0;
                // <editor-fold defaultstate="collapsed" desc="-- Insert New Lower name and Refresh the List"> 
                try {
                    result = insertNewLevel2Affiliation((Integer)L1_No, name2);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY)
                    {
                        rejectUserInput(L2_Affiliation, rowIndex, LEVEL2_NAME_DIALOG.getContent());
                    }
                    else
                    {
                        logParkingException(Level.SEVERE, ex, 
                                "(insertion tried L2 name: " + name2 + ")");
                    }
                }                  
                       
                if (result == 1)
                {
                    loadL2_Affiliation(L1_No, -1, name2); // Refresh the list
                    insertL2_Button.setEnabled(true);
                }
                //</editor-fold>
            }
            else
            {
                removeEmptyRow(insertL2_Button, L2_Affiliation);    
            }
            //</editor-fold>            
        }
        else // handle the case of modification
        {
            if (L2Name.trim().isEmpty()) {
                rejectEmptyInput(L2_Affiliation, rowIndex, "Can't save empty string as a name");
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
                        rejectUserInput(L2_Affiliation, rowIndex, LEVEL2_NAME_DIALOG.getContent());
                    }
                    else {
                        logParkingException(Level.SEVERE, ex, excepMsg);
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
        }
    }//GEN-LAST:event_L2_AffiliationKeyReleased

    private void insertL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertL2_ButtonActionPerformed
        prepareRecordInsertion(L2_Affiliation, insertL2_Button);      
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
        Object bldgNoObj = model.getValueAt(rowIndex, 1);
        
        // Check if it were a new building number being created
        // - Cond 1: current building number is null, Cond 2: Building Number is not null
        if (model.getValueAt(rowIndex, 0) == null) {
            // <editor-fold defaultstate="collapsed" desc="-- Create a new building number">            
            if (bldgNoObj != null) 
            {
                int result = 0;
                int bldgNo = (Integer)bldgNoObj; 

                // <editor-fold defaultstate="collapsed" desc="-- Actual creation of a new building(number)">            
                try {
                    result = insertNewBuilding(bldgNo);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY)
                    {
                        rejectUserInput(BuildingTable, rowIndex, BUILDING_IN_DIALOG.getContent());
                    }
                    else
                    {
                        logParkingException(Level.SEVERE, ex, "(inserted building: " + bldgNo + ")");
                    }
                }                    
                //</editor-fold>            
                
                if (result == 1)
                {
                    loadBuilding(-1, bldgNo); // Refresh building number list
                    insertBuilding_Button.setEnabled(true);
                }
            }   
            else
            {
                removeEmptyRow(insertBuilding_Button, BuildingTable);                
            }
            //</editor-fold>            
        }
        else // Handle building number update case
        {
            // <editor-fold defaultstate="collapsed" desc="-- Handle building number update">                          
            if (bldgNoObj == null) {
                rejectEmptyInput(BuildingTable, rowIndex, "Can't use empty string as a building number"); 
            }
            else {
                Object bldgSeqNo = model.getValueAt(rowIndex, 2);
                
                // <editor-fold defaultstate="collapsed" desc="-- Actual building number update">
                Connection conn = null;
                PreparedStatement modifyBuilding = null;
                String excepMsg = "(Original building no: " + prevBldgNo + ")";

                int result = 0;
                try {
                    String sql = "Update building_table Set BLDG_NO = ? Where SEQ_NO = ?";

                    conn = getConnection();
                    modifyBuilding = conn.prepareStatement(sql);
                    modifyBuilding.setInt(1, (Integer)bldgNoObj); 
                    modifyBuilding.setInt(2, (Integer)bldgSeqNo);

                    result = modifyBuilding.executeUpdate();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY) {
                        rejectUserInput(BuildingTable, rowIndex, BUILDING_IN_DIALOG.getContent());
                    }
                    else {
                        logParkingException(Level.SEVERE, ex, excepMsg);
                    }
                } finally {
                    closeDBstuff(conn, modifyBuilding, null, excepMsg);
                }    
                //</editor-fold>            
                
                if (result == 1) {
                    loadBuilding(-1, (Integer)bldgNoObj); // Refresh building number list after a number update
                } 
            }
            //</editor-fold>            
        }
    }//GEN-LAST:event_BuildingTableKeyReleased

    private void insertUnit_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertUnit_ButtonActionPerformed
        prepareRecordInsertion(UnitTable, insertUnit_Button);
    }//GEN-LAST:event_insertUnit_ButtonActionPerformed
    
    private void UnitTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UnitTableKeyReleased
        TableModel bModel = BuildingTable.getModel();
        int bIndex = BuildingTable.convertRowIndexToModel(BuildingTable.getSelectedRow());
        int bldgNo = (Integer)bModel.getValueAt(bIndex, 1);
        Object bldgSeqNoObj = bModel.getValueAt(bIndex, 2);
                
        int uIndex = UnitTable.convertRowIndexToModel (UnitTable.getSelectedRow());
        TableModel uModel = UnitTable.getModel();
        Object unitNoObj = uModel.getValueAt(uIndex, 1);
        
        // Conditions to make this a new room number: Cond 1 and cond 2
        if (uModel.getValueAt(uIndex, 0) == null) // Cond 1. Row number field is null
        {
            // <editor-fold defaultstate="collapsed" desc="-- Create a new Room number"> 
            if (unitNoObj != null) // Cond 2. Number field has some string
            {
                int unit_no = (Integer)unitNoObj; 
                int result = 0;

                // <editor-fold defaultstate="collapsed" desc="-- Actual creation of a new room number"> 
                try {
                    result = insertNewBuildingUnit(unit_no, (Integer)bldgSeqNoObj);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY)
                    {
                        rejectUserInput(UnitTable, uIndex, ROOM_IN_DIALOG.getContent());
                    }
                    else
                    {
                        logParkingException(Level.SEVERE, ex, "(inserted UNIT: " + unit_no + ")");
                    }
                }
                //</editor-fold>                

                if (result == 1)
                {
                    loadUnitNumberTable(bldgNo, (Integer)bldgSeqNoObj, -1, unit_no); // Refresh the list
                    insertUnit_Button.setEnabled(true);
                }
            }
            else
            {
                removeEmptyRow(insertUnit_Button, UnitTable);                
            }
            //</editor-fold>
        } 
        else 
        {
            if (unitNoObj == null) {
                rejectEmptyInput(UnitTable, uIndex, "Can't use empty string as a room unit number"); 
            } else {
                // <editor-fold defaultstate="collapsed" desc="-- Actual room number update">
                int unit_no = (Integer)unitNoObj; 
                
                Connection conn = null;
                PreparedStatement updateUnit = null;
                String excepMsg = "(Oiriginal UNIT No: " + prevUnitNo + ")";

                int result = 0;
                try {
                    String sql = "Update BUILDING_UNIT Set UNIT_NO = ? Where SEQ_NO = ?";
                    conn = getConnection();
                    updateUnit = conn.prepareStatement(sql);
                    updateUnit.setInt(1, unit_no);
                    updateUnit.setInt(2, (Integer)uModel.getValueAt(uIndex, 2));

                    result = updateUnit.executeUpdate();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == ER_DUP_ENTRY) {
                        rejectUserInput(UnitTable, uIndex, ROOM_IN_DIALOG.getContent());
                    }
                    else {
                        logParkingException(Level.SEVERE, ex, excepMsg);
                    }
                } finally {
                    closeDBstuff(conn, updateUnit, null, excepMsg);
                }                  
                //</editor-fold>
                
                if (result == 1) {
                    // Refresh room number list table after a room number update
                    loadUnitNumberTable((Integer)bldgNo, bModel.getValueAt(bIndex, 2), -1, unit_no); 
                } 
            }
        }
    }//GEN-LAST:event_UnitTableKeyReleased

    private void insertBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertBuilding_ButtonActionPerformed
        prepareRecordInsertion(BuildingTable, insertBuilding_Button);
    }//GEN-LAST:event_insertBuilding_ButtonActionPerformed

    private void deleteBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBuilding_ButtonActionPerformed
        // Delete currently selected building row
        int viewIndex = BuildingTable.getSelectedRow();
        if (viewIndex == -1)
        {
            return;
        }
        int modal_Index = BuildingTable.convertRowIndexToModel(viewIndex);        
        int bldg_no = (Integer)BuildingTable.getModel().getValueAt(modal_Index, 1);
        int bldg_seq_no = (Integer)BuildingTable.getModel().getValueAt(modal_Index, 2);        
        int count = getUnitCount(bldg_seq_no);
        
        String dialog = "";
        
        switch (parkingLotLocale.getLanguage()) {
                case "ko":
                    dialog = "다음 건물 및 그의 호실들을 삭제합니까?" + System.getProperty("line.separator") 
                    + "건물번호: " + bldg_no + " (소속 호실: " + count + " 개)";
                    break;
                default:
                    dialog = "Want to delete the following building and its rooms?" + System.getProperty("line.separator") 
                    + "Building No.: " + bldg_no + " (Number of Rooms: " + count + ")";
                    break;
            }        
        
        int result = JOptionPane.showConfirmDialog(this, dialog,
                DELETE_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
            //<editor-fold desc="actual delete of a building(its number)">
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = "(Deletion Belong No: " + bldg_no + ")";

            result = 0;
            try {
                String sql = "Delete From BUILDING_TABLE Where SEQ_NO = ?";

                conn = getConnection();
                createBuilding = conn.prepareStatement(sql);
                createBuilding.setInt(1, bldg_seq_no);

                result = createBuilding.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);
            }    
            //</editor-fold>
            
            if (result == 1) {
                loadBuilding(viewIndex, 0); // Deliver the index of deleted row
                
                switch (language) {
                    case KOREAN:
                        dialog = "다음 건물 이 성공적으로 삭제되었습니다" 
                        +  System.getProperty("line.separator") 
                        + "건물 번호: " + bldg_no;
                        break;
                        
                    case ENGLISH:
                        dialog = "Building No. " + bldg_no + 
                            " has been successfully deleted";
                        break;
                        
                    default:
                        break;
                }
                
                JOptionPane.showConfirmDialog(this, dialog,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                
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
        // Get confirmation from the user on a room number update.
        int rowIndex = UnitTable.getSelectedRow();
        if (UnitTable.editCellAt(rowIndex, 1))
        {
            UnitTable.getEditorComponent().requestFocus();        
            processUnitNoChangeTrial(UnitTable.getSelectedRow());
        }
    }//GEN-LAST:event_modifyUnit_ButtonActionPerformed

    private void modifyL2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyL2_ButtonActionPerformed
        // Get confirmation from the user on a 2nd level affiliation update.
        int rowIndex = L2_Affiliation.getSelectedRow();
        if (L2_Affiliation.editCellAt(rowIndex, 1))
        {
            L2_Affiliation.getEditorComponent().requestFocus();        
            processL2NameChangeTrial(rowIndex);
        }
    }//GEN-LAST:event_modifyL2_ButtonActionPerformed

    private void modifyBuilding_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyBuilding_ButtonActionPerformed
        // Get confirmation from the user on a building number update.
        int bIndex = BuildingTable.getSelectedRow();
        
        if (BuildingTable.editCellAt(bIndex, 1))
        {
            BuildingTable.getEditorComponent().requestFocus();        
            processBuildingChangeTrial(bIndex);
        }
    }//GEN-LAST:event_modifyBuilding_ButtonActionPerformed

    private void modifyL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyL1_ButtonActionPerformed
        // Get confirmation from the user on a 1st level affiliation update.
        int rowIndex = L1_Affiliation.getSelectedRow();
        if (L1_Affiliation.editCellAt(rowIndex, 1))
        {
            L1_Affiliation.getEditorComponent().requestFocus();        
            processL1NameChangeTrial(rowIndex);
        }
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

    private void deleteAll_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAll_ButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(this, BUILDING_DELETE_ALL_DAILOG.getContent(), 
                DELETE_ALL_RESULT_DIALOGTITLE.getContent(), 
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
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
        }
    }//GEN-LAST:event_deleteAll_ButtonActionPerformed

    private void readSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_ButtonActionPerformed
        try {
            int returnVal = odsFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {                
                File file = odsFileChooser.getSelectedFile();
                ODSReader objODSReader = new ODSReader();

                Sheet sheet = null;
                try {
                    sheet = SpreadSheet.createFromFile(file).getSheet(0);
                } catch (IOException ex) {
                    Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (sheet != null)
                {
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
    }//GEN-LAST:event_readSheet_ButtonActionPerformed

    private void deleteAll_AffiliationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAll_AffiliationActionPerformed
        // TODO add your handling code here:
        
        int result = JOptionPane.showConfirmDialog(this, AFFILIATION_DELETE_ALL_DAILOG.getContent(),
                DELETE_ALL_DAILOGTITLE.getContent(), 
                JOptionPane.YES_NO_OPTION); 
        
        if (result == JOptionPane.YES_OPTION) {
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
        }
    }//GEN-LAST:event_deleteAll_AffiliationActionPerformed

    private void readSheet_AffiliationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_AffiliationActionPerformed
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
                        sb.append("Below Data Recognized. Want to continue loading?");
                        sb.append(System.getProperty("line.separator"));
                        sb.append(" -Data: Higher Affiliation count: " + level1_total.getValue());
                        sb.append(", Lower Affiliation count: " + level2_total.getValue());

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
    }//GEN-LAST:event_readSheet_AffiliationActionPerformed

    private void ODSAffiliHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSAffiliHelpActionPerformed

        JDialog helpDialog = new ODS_HelpJDialog(this, false, 
                HELP_AFFIL_LABEL.getContent(),
                ODS_TYPE.AFFILIATION);
        Point buttonPoint = new Point();
        ODSAffiliHelp.getLocation(buttonPoint);
        Point framePoint = new Point();
        this.getLocation(framePoint);
        helpDialog.setLocation(framePoint.x + buttonPoint.x + 50, framePoint.y + 30);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_ODSAffiliHelpActionPerformed

    private void ODSBuildPWHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSBuildPWHelpActionPerformed

        JDialog helpDialog = new ODS_HelpJDialog(this, false, 
                HELP_BUILDING_LABEL.getContent(),
                ODS_TYPE.BUILDING);
        Point buttonPoint = new Point();
        ODSBuildPWHelp.getLocation(buttonPoint);
        Point framePoint = new Point();
        this.getLocation(framePoint);
        helpDialog.setLocation(framePoint.x - 130, framePoint.y + 30);                
        helpDialog.setVisible(true);
    }//GEN-LAST:event_ODSBuildPWHelpActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void adjustTables() {
        adjustAffiliationTable(L1_Affiliation);
        adjustAffiliationTable(L2_Affiliation);
        addAffiliationSelectionListener();      
        
        adjustNumberTable(BuildingTable);
        adjustNumberTable(UnitTable);
        addBuildingAndUnitSelectionListener(); 
        
        // Register mouse double click event to allow fast modification
        addDoubleClickEventListener();
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
            modifyL1_Button.setEnabled(true);                
            deleteL1_Button.setEnabled(true);                
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
            L2AffilLabel.setText(LOWER_LIST_LABEL.getContent());
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
            L2AffilLabel.setText(label);
            
            insertL2_Button.setEnabled(true);
            
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
    private javax.swing.JTable BuildingTable;
    private javax.swing.JTable L1_Affiliation;
    private javax.swing.JLabel L2AffilLabel;
    private javax.swing.JTable L2_Affiliation;
    private javax.swing.JButton ODSAffiliHelp;
    private javax.swing.JButton ODSBuildPWHelp;
    private javax.swing.JLabel UnitLabel;
    private javax.swing.JTable UnitTable;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JButton deleteAll_Affiliation;
    private javax.swing.JButton deleteAll_Button;
    private javax.swing.JButton deleteBuilding_Button;
    private javax.swing.JButton deleteL1_Button;
    private javax.swing.JButton deleteL2_Button;
    private javax.swing.JButton deleteUnit_Button;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton insertBuilding_Button;
    private javax.swing.JButton insertL1_Button;
    private javax.swing.JButton insertL2_Button;
    private javax.swing.JButton insertUnit_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton modifyBuilding_Button;
    private javax.swing.JButton modifyL1_Button;
    private javax.swing.JButton modifyL2_Button;
    private javax.swing.JButton modifyUnit_Button;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JButton readSheet_Affiliation;
    private javax.swing.JButton readSheet_Button;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel wholePanel;
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
            deleteBuilding_Button.setEnabled(true);                
            modifyBuilding_Button.setEnabled(true);  
        }
        else
        {
            deleteBuilding_Button.setEnabled(false);                
            modifyBuilding_Button.setEnabled(false);                         
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
            
            insertUnit_Button.setEnabled(true);
            
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
                        deleteBuilding_Button.setEnabled(bldg_seq_no == null ? false : true);
                        modifyBuilding_Button.setEnabled(bldg_seq_no == null ? false : true);
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

    private void addDoubleClickEventListener() {
        BuildingTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    return;
                }
                else
                {
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        processBuildingChangeTrial(row);
                    }
                }
            }
        });
        
        UnitTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    return;
                }
                else
                {
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        processUnitNoChangeTrial(row);
                    }
                }                
            }
        });

        L1_Affiliation.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    return;
                }
                else
                {
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        processL1NameChangeTrial(row);
                    }
                }                
            }
        });        

        L2_Affiliation.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    return;
                }
                else
                {
                    int row = table.rowAtPoint(p);
                    if (me.getClickCount() == 2) {
                        processL2NameChangeTrial(row);
                    }
                }                
            }
        });        
    }

    static Object prevUnitNo = null;
    static Object prevBldgNo = null;
    static Object prevL1Name = null;
    static Object prevL2Name = null;
    
    private void processUnitNoChangeTrial(int rowIndex) {
        int model_index = UnitTable.convertRowIndexToModel(rowIndex);        
        Object lineNo = UnitTable.getModel().getValueAt(model_index, 0);
        prevUnitNo = UnitTable.getModel().getValueAt(model_index, 1);    
        if (lineNo != null) 
        {
            int bIndex = BuildingTable.convertRowIndexToModel(
                    BuildingTable.getSelectedRow());
            int bldgNo = (Integer)BuildingTable.getModel().getValueAt(bIndex, 1);
            
            String dialog = "";
            switch (language) {
                case KOREAN:
                    dialog = "다음 건물의 호실 번호를 변경합니까?" + System.getProperty("line.separator") 
                            + " - 건물 번호: " + bldgNo + System.getProperty("line.separator") 
                            + " - 호실 번호: " + prevUnitNo;
                    break;
                    
                case ENGLISH:
                    dialog =  "Waht to change following room number?" + System.getProperty("line.separator") 
                            + " - Building number: " + bldgNo + System.getProperty("line.separator") 
                            + " - Room number: " + prevUnitNo;
                    break;
                    
                default:
                    break;
                }            
            
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    UNIT_MODIFY_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.NO_OPTION) { 
                UnitTable.getCellEditor().stopCellEditing();
            }
        }
    }    

    private void processL2NameChangeTrial(int rowIndex) {
        int model_index = L2_Affiliation.convertRowIndexToModel(rowIndex);        
        Object lineNo = L2_Affiliation.getModel().getValueAt(model_index, 0);
        prevL2Name = L2_Affiliation.getModel().getValueAt(model_index, 1);    
        if (lineNo != null) 
        {
            int index1 = L1_Affiliation.convertRowIndexToModel(
                    L1_Affiliation.getSelectedRow());
            String nameL1 = L1_Affiliation.getModel().getValueAt(index1, 1).toString();
            
            String dialog = "";
            
            switch (parkingLotLocale.getLanguage()) {
                case "ko":
                    dialog = "다음 하위 소속 이름을 변경합니까?" + System.getProperty("line.separator") 
                            + " - 상위 소속: " + nameL1 + System.getProperty("line.separator") 
                            + " - 하위 소속: " + prevL2Name;
                    break;
                default:
                    dialog =  "Waht to change following lower affiliation?" + System.getProperty("line.separator") 
                            + " - Higher Affiliation: " + nameL1 + System.getProperty("line.separator") 
                            + " - Lower Affiliation: " + prevL2Name;
                    break;
            }            
            
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    LOWER_MODIFY_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.NO_OPTION) { 
                L2_Affiliation.getCellEditor().stopCellEditing();
            }
        }
    }

    private void processBuildingChangeTrial(int rowIndex) {
        int model_index = BuildingTable.convertRowIndexToModel(rowIndex);  
        TableModel modelB = BuildingTable.getModel();
        
        prevBldgNo = modelB.getValueAt(model_index, 1);    
        if (modelB.getValueAt(model_index, 0) != null) 
        {
            int bldg_seq_no = (Integer)modelB.getValueAt(model_index, 2);
            
            String dialog = "";
            
            switch (language) {
                case KOREAN:
                    dialog = "다음 건물의 번호를 변경합니까?" + System.getProperty("line.separator") 
                            + " - 건물번호: " + prevBldgNo 
                            + " (관련 호실: " + getUnitCount(bldg_seq_no) + " 개)";
                    break;
                    
                case ENGLISH:
                    dialog =  "Want to change the following building number?" + System.getProperty("line.separator") 
                            + " - Building no.: " + prevBldgNo 
                            + " (number of rooms: " + getUnitCount(bldg_seq_no) + ")";
                    break;
                    
                default:
                    break;
            }            
            
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    BUILDING_MODIFY_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.NO_OPTION) { 
                BuildingTable.getCellEditor().stopCellEditing();
            }
        }    
    }

    private void processL1NameChangeTrial(int rowIndex) {
        int model_index = L1_Affiliation.convertRowIndexToModel(rowIndex);
        TableModel model1 = L1_Affiliation.getModel();
        
        prevL1Name = model1.getValueAt(model_index, 1);    
        if (model1.getValueAt(model_index, 0) != null) 
        {
            int L1_no = (int)model1.getValueAt(model_index, 2);
            String dialog = "";
            
            switch (language) {
                case KOREAN:
                    dialog = "다음 상위 소속 이름을 변경합니까?" + System.getProperty("line.separator") 
                            + " - 상위 소속: " + prevL1Name 
                            + " (관련 하위 소속: " + getL2RecordCount(L1_no) + " 개)"; 
                    break;
                    
                case ENGLISH:
                    dialog =  "Want to change the following higher affil'?" + System.getProperty("line.separator") 
                            + " - Higher Affiliation: " + prevL1Name 
                            + " (number of lower affiliations: " + getL2RecordCount(L1_no) + ")";
                    break;
                    
                default:
                    break;
                }            
            
            int result = JOptionPane.showConfirmDialog(this, dialog,
                    AFFILIATION_MODIFY_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION); 

            if (result == JOptionPane.NO_OPTION) { 
                L1_Affiliation.getCellEditor().stopCellEditing();
            }
        }
    }

    private void prepareRecordInsertion(JTable listTable, JButton insertButton) {    
        
        DefaultTableModel model = (DefaultTableModel)listTable.getModel();
        model.setRowCount(listTable.getRowCount() + 1);
        int rowIndex = listTable.getRowCount() - 1;
        
        if (listTable.getValueAt(rowIndex, 1) != null) 
        {
            rowIndex = 0;
        }
        listTable.setRowSelectionInterval(rowIndex, rowIndex);
        if (listTable.editCellAt(rowIndex, 1))
        {
            listTable.getEditorComponent().requestFocus();
            listTable.scrollRectToVisible(
                    new Rectangle(listTable.getCellRect(rowIndex, 0, true)));
        }
        insertButton.setEnabled(false);      
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
        //</editor-fold>
        //</editor-fold>
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
                shortLicenseDialog(runForm, "Attendant Program", "upper left");
            }
        });
    }
}
