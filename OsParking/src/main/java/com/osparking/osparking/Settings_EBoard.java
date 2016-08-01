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
package com.osparking.osparking;

import static com.osparking.global.CommonData.TEXT_FIELD_HEIGHT;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static com.osparking.global.names.DB_Access.readEBoardSettings;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CANCEL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_BTN;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.MS_NEO_GOTHIC;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.SANS_SERIF;
import static com.osparking.global.names.ControlEnums.DialogMessages.SAVE_SETTINGS_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BOTTOM_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DEFAULT_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.SETTINGS_SAVE_RESULT;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.TOP_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.VEHICLE_TAB_TITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.LabelContent.COLOR_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DISPLAY_TYPE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.EFFECT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FONT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MESSAGE_LABEL;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CLOSE_BTN_TOOLTIP;
import com.osparking.global.names.ConvComboBoxItem;
import com.osparking.global.names.JDBCMySQL;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.global.names.OSP_enums.EBD_Colors;
import com.osparking.global.names.OSP_enums.EBD_ContentType;
import com.osparking.global.names.OSP_enums.EBD_DisplayUsage;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.*;
import com.osparking.global.names.OSP_enums.EBD_Fonts;
import com.osparking.global.names.OSP_enums.EBD_Effects;
import static com.osparking.osparking.device.EBoardManager.sendEBoardDefaultSetting;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.IDevice;
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author YongSeok
 */
//public class Settings_EBoard extends javax.swing.JFrame {
public class Settings_EBoard extends javax.swing.JFrame {
    public static ControlGUI mainForm = null;
    private HashMap<String,Component> componentMap = new HashMap<String,Component>();
    private EBD_DisplayUsage currentTab = DEFAULT_TOP_ROW, previousTab = DEFAULT_TOP_ROW;
    Settings_System parent = null;
    FormMode formMode = FormMode.NormalMode;
    /**
     * Creates new form TestDisplay
     */
    public Settings_EBoard(ControlGUI mainForm, Settings_System parent) {
        initComponents();
        this.mainForm = mainForm;
        this.parent = parent;
        setResizable(false);
        
        augmentComponentMap(this, componentMap);
        tuneComponentSize();
        
        addContentTypeItems();
        addDisplayEffectItems();
        addTextColorItems();
        addTextFontItems();
        
        selectSpecificTab(currentTab);
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                previousTab = currentTab;
                int index =  (eboardTabbedPanel.getSelectedIndex() * 2) 
                    + ((JTabbedPane) eboardTabbedPanel.getSelectedComponent()).getSelectedIndex();
                currentTab = EBD_DisplayUsage.values()[index];
                selectSpecificTab(currentTab);
                showDialog(previousTab);
            }
        };
        eboardTabbedPanel.addChangeListener(changeListener);
        eBoardTabPane1.addChangeListener(changeListener);
        eBoardTabPane2.addChangeListener(changeListener);
        
        /**
         * Set icon for the simulated camera program
         */
        setIconImages(OSPiconList);                  
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
        topFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        rightFiller = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        leftFiller = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        centerPanel = new javax.swing.JPanel();
        eboardTabbedPanel = new javax.swing.JTabbedPane();
        eBoardTabPane1 = new javax.swing.JTabbedPane();
        eBoardPanel0 = new javax.swing.JPanel();
        label_MSG0 = new javax.swing.JLabel();
        tf_VerbatimContent0 = new javax.swing.JTextField();
        label_Effect0 = new javax.swing.JLabel();
        label_Color0 = new javax.swing.JLabel();
        label_Font0 = new javax.swing.JLabel();
        combo_TextColor0 = new javax.swing.JComboBox();
        combo_TextFont0 = new javax.swing.JComboBox();
        label_ContentType0 = new javax.swing.JLabel();
        combo_ContentType0 = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel1 = new javax.swing.JPanel();
        combo_DisplayEffect0 = new javax.swing.JComboBox();
        eBoardPanel1 = new javax.swing.JPanel();
        label_MSG4 = new javax.swing.JLabel();
        tf_VerbatimContent1 = new javax.swing.JTextField();
        label_Effect4 = new javax.swing.JLabel();
        label_Color4 = new javax.swing.JLabel();
        label_Font4 = new javax.swing.JLabel();
        combo_TextColor1 = new javax.swing.JComboBox();
        combo_TextFont1 = new javax.swing.JComboBox();
        label_ContentType4 = new javax.swing.JLabel();
        combo_ContentType1 = new javax.swing.JComboBox();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel2 = new javax.swing.JPanel();
        combo_DisplayEffect1 = new javax.swing.JComboBox();
        eBoardTabPane2 = new javax.swing.JTabbedPane();
        eBoardPanel2 = new javax.swing.JPanel();
        label_MSG1 = new javax.swing.JLabel();
        tf_VerbatimContent2 = new javax.swing.JTextField();
        label_Effect1 = new javax.swing.JLabel();
        label_Color1 = new javax.swing.JLabel();
        label_Font1 = new javax.swing.JLabel();
        combo_TextColor2 = new javax.swing.JComboBox();
        combo_TextFont2 = new javax.swing.JComboBox();
        label_ContentType1 = new javax.swing.JLabel();
        combo_ContentType2 = new javax.swing.JComboBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel3 = new javax.swing.JPanel();
        combo_DisplayEffect2 = new javax.swing.JComboBox();
        eBoardPanel3 = new javax.swing.JPanel();
        label_MSG2 = new javax.swing.JLabel();
        tf_VerbatimContent3 = new javax.swing.JTextField();
        label_Effect2 = new javax.swing.JLabel();
        label_Color2 = new javax.swing.JLabel();
        label_Font2 = new javax.swing.JLabel();
        combo_TextColor3 = new javax.swing.JComboBox();
        combo_TextFont3 = new javax.swing.JComboBox();
        label_ContentType2 = new javax.swing.JLabel();
        combo_ContentType3 = new javax.swing.JComboBox();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel4 = new javax.swing.JPanel();
        combo_DisplayEffect3 = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        buttonPanel = new javax.swing.JPanel();
        btn_Exit = new javax.swing.JButton();
        btn_Save0 = new javax.swing.JButton();
        btn_Cancel0 = new javax.swing.JButton();
        southPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Electronic Display Settings");
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(600, 350));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formClosing(evt);
            }
        });

        wholePanel.setMinimumSize(new java.awt.Dimension(550, 314));
        wholePanel.setPreferredSize(new java.awt.Dimension(600, 350));
        wholePanel.setLayout(new java.awt.BorderLayout());
        wholePanel.add(topFiller, java.awt.BorderLayout.NORTH);
        wholePanel.add(rightFiller, java.awt.BorderLayout.EAST);
        wholePanel.add(leftFiller, java.awt.BorderLayout.WEST);

        centerPanel.setMinimumSize(new java.awt.Dimension(0, 234));
        centerPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));

        eboardTabbedPanel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        eboardTabbedPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        eboardTabbedPanel.setName("eboardTabbedPanel"); // NOI18N
        eboardTabbedPanel.setPreferredSize(new java.awt.Dimension(0, 231));

        eBoardTabPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, null));
        eBoardTabPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        eBoardTabPane1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        eBoardTabPane1.setMinimumSize(new java.awt.Dimension(300, 198));
        eBoardTabPane1.setName("Default_Panel"); // NOI18N
        eBoardTabPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        eBoardPanel0.setName("eBoard" + EBD_DisplayUsage.DEFAULT_TOP_ROW.getVal());
        java.awt.GridBagLayout eBoardPanel0Layout = new java.awt.GridBagLayout();
        eBoardPanel0Layout.columnWidths = new int[] {0, 2, 0, 2, 0, 2, 0};
        eBoardPanel0Layout.rowHeights = new int[] {0, 2, 0, 2, 0, 2, 0};
        eBoardPanel0.setLayout(eBoardPanel0Layout);

        label_MSG0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG0.setText(MESSAGE_LABEL.getContent());
        label_MSG0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_MSG0, gridBagConstraints);

        tf_VerbatimContent0.setColumns(23);
        tf_VerbatimContent0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent0.setMaximumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        tf_VerbatimContent0.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent0KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 50);
        eBoardPanel0.add(tf_VerbatimContent0, gridBagConstraints);

        label_Effect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Effect0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Effect0.setText(EFFECT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        eBoardPanel0.add(label_Effect0, gridBagConstraints);

        label_Color0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color0.setText(COLOR_LABEL.getContent());
        label_Color0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_Color0, gridBagConstraints);

        label_Font0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font0.setText(FONT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_Font0, gridBagConstraints);

        combo_TextColor0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextColor0.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_TextColor0.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_TextColor0.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextColor0.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextColor0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(combo_TextColor0, gridBagConstraints);

        combo_TextFont0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextFont0.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextFont0.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextFont0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel0.add(combo_TextFont0, gridBagConstraints);

        label_ContentType0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType0.setText(DISPLAY_TYPE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_ContentType0, gridBagConstraints);

        combo_ContentType0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_ContentType0.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_ContentType0.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_ContentType0.setName("combo_ContentType" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_ContentType0.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_ContentType0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_ContentType0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_ContentType0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel0.add(combo_ContentType0, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        eBoardPanel0.add(filler2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        combo_DisplayEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_DisplayEffect0.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect0.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect0.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_DisplayEffect0.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_DisplayEffect0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel1.add(combo_DisplayEffect0, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        eBoardPanel0.add(jPanel1, gridBagConstraints);

        eBoardTabPane1.addTab(TOP_TAB_TITLE.getContent(), eBoardPanel0);

        eBoardPanel1.setName("eBoard" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.getVal());
        eBoardPanel1.setLayout(new java.awt.GridBagLayout());

        label_MSG4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG4.setText(MESSAGE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel1.add(label_MSG4, gridBagConstraints);

        tf_VerbatimContent1.setColumns(23);
        tf_VerbatimContent1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent1.setMaximumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        tf_VerbatimContent1.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent1KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 50);
        eBoardPanel1.add(tf_VerbatimContent1, gridBagConstraints);

        label_Effect4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Effect4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Effect4.setText(EFFECT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        eBoardPanel1.add(label_Effect4, gridBagConstraints);

        label_Color4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color4.setText(COLOR_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel1.add(label_Color4, gridBagConstraints);

        label_Font4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font4.setText(FONT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel1.add(label_Font4, gridBagConstraints);

        combo_TextColor1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextColor1.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_TextColor1.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_TextColor1.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextColor1.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextColor1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel1.add(combo_TextColor1, gridBagConstraints);

        combo_TextFont1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextFont1.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_TextFont1.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_TextFont1.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextFont1.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextFont1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel1.add(combo_TextFont1, gridBagConstraints);

        label_ContentType4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType4.setText(DISPLAY_TYPE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel1.add(label_ContentType4, gridBagConstraints);

        combo_ContentType1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_ContentType1.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_ContentType1.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_ContentType1.setName("combo_ContentType" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_ContentType1.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_ContentType1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_ContentType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_ContentType1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel1.add(combo_ContentType1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        eBoardPanel1.add(filler3, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        combo_DisplayEffect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_DisplayEffect1.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect1.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect1.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_DisplayEffect1.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_DisplayEffect1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel2.add(combo_DisplayEffect1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        eBoardPanel1.add(jPanel2, gridBagConstraints);

        eBoardTabPane1.addTab(BOTTOM_TAB_TITLE.getContent(), eBoardPanel1);

        eboardTabbedPanel.addTab(DEFAULT_TAB_TITLE.getContent(), eBoardTabPane1);

        eBoardTabPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, null));
        eBoardTabPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        eBoardTabPane2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        eBoardTabPane2.setName("Vehicle_Panel"); // NOI18N

        eBoardPanel2.setName("eBoard" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.getVal());
        eBoardPanel2.setLayout(new java.awt.GridBagLayout());

        label_MSG1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG1.setText(MESSAGE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel2.add(label_MSG1, gridBagConstraints);

        tf_VerbatimContent2.setColumns(23);
        tf_VerbatimContent2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent2.setMaximumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        tf_VerbatimContent2.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent2KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 50);
        eBoardPanel2.add(tf_VerbatimContent2, gridBagConstraints);

        label_Effect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Effect1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Effect1.setText(EFFECT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        eBoardPanel2.add(label_Effect1, gridBagConstraints);

        label_Color1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color1.setText(COLOR_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel2.add(label_Color1, gridBagConstraints);

        label_Font1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font1.setText(FONT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel2.add(label_Font1, gridBagConstraints);

        combo_TextColor2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextColor2.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_TextColor2.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_TextColor2.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextColor2.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextColor2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel2.add(combo_TextColor2, gridBagConstraints);

        combo_TextFont2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextFont2.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_TextFont2.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_TextFont2.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextFont2.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextFont2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel2.add(combo_TextFont2, gridBagConstraints);

        label_ContentType1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType1.setText(DISPLAY_TYPE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel2.add(label_ContentType1, gridBagConstraints);

        combo_ContentType2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_ContentType2.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_ContentType2.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_ContentType2.setName("combo_ContentType" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_ContentType2.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_ContentType2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_ContentType2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_ContentType2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel2.add(combo_ContentType2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        eBoardPanel2.add(filler4, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        combo_DisplayEffect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_DisplayEffect2.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect2.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect2.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_DisplayEffect2.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_DisplayEffect2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel3.add(combo_DisplayEffect2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        eBoardPanel2.add(jPanel3, gridBagConstraints);

        eBoardTabPane2.addTab(TOP_TAB_TITLE.getContent(), eBoardPanel2);

        eBoardPanel3.setName("eBoard" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.getVal());
        eBoardPanel3.setLayout(new java.awt.GridBagLayout());

        label_MSG2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG2.setText(MESSAGE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel3.add(label_MSG2, gridBagConstraints);

        tf_VerbatimContent3.setColumns(23);
        tf_VerbatimContent3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent3.setMaximumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        tf_VerbatimContent3.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent3KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 50);
        eBoardPanel3.add(tf_VerbatimContent3, gridBagConstraints);

        label_Effect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Effect2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Effect2.setText(EFFECT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        eBoardPanel3.add(label_Effect2, gridBagConstraints);

        label_Color2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color2.setText(COLOR_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel3.add(label_Color2, gridBagConstraints);

        label_Font2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font2.setText(FONT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel3.add(label_Font2, gridBagConstraints);

        combo_TextColor3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextColor3.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_TextColor3.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_TextColor3.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_TextColor3.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextColor3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel3.add(combo_TextColor3, gridBagConstraints);

        combo_TextFont3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextFont3.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_TextFont3.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_TextFont3.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_TextFont3.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_TextFont3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel3.add(combo_TextFont3, gridBagConstraints);

        label_ContentType2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType2.setText(DISPLAY_TYPE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel3.add(label_ContentType2, gridBagConstraints);

        combo_ContentType3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_ContentType3.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_ContentType3.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_ContentType3.setName("combo_ContentType" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_ContentType3.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_ContentType3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_ContentType3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_ContentType3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel3.add(combo_ContentType3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        eBoardPanel3.add(filler5, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        combo_DisplayEffect3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_DisplayEffect3.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect3.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect3.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_DisplayEffect3.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_DisplayEffect3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel4.add(combo_DisplayEffect3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        eBoardPanel3.add(jPanel4, gridBagConstraints);

        eBoardTabPane2.addTab(BOTTOM_TAB_TITLE.getContent(), eBoardPanel3);

        eboardTabbedPanel.addTab(VEHICLE_TAB_TITLE.getContent(), eBoardTabPane2);

        centerPanel.add(eboardTabbedPanel);
        centerPanel.add(filler1);

        buttonPanel.setMinimumSize(new java.awt.Dimension(0, 40));
        buttonPanel.setPreferredSize(new java.awt.Dimension(0, 40));

        btn_Exit.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Exit.setMnemonic('c');
        btn_Exit.setText(CLOSE_BTN.getContent());
        btn_Exit.setToolTipText(CLOSE_BTN_TOOLTIP.getContent());
        btn_Exit.setMaximumSize(new java.awt.Dimension(90, 40));
        btn_Exit.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Exit.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExitActionPerformed(evt);
            }
        });

        btn_Save0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save0.setMnemonic('s');
        btn_Save0.setText(SAVE_BTN.getContent());
        btn_Save0.setEnabled(false);
        btn_Save0.setInheritsPopupMenu(true);
        btn_Save0.setMaximumSize(new java.awt.Dimension(90, 40));
        btn_Save0.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Save0.setName("btn_Save" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Save0.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Save0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Save0ActionPerformed(evt);
            }
        });

        btn_Cancel0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel0.setMnemonic('c');
        btn_Cancel0.setText(CANCEL_BTN.getContent());
        btn_Cancel0.setEnabled(false);
        btn_Cancel0.setMaximumSize(new java.awt.Dimension(90, 40));
        btn_Cancel0.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Cancel0.setName("btn_Cancel" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Cancel0.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Cancel0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Cancel0ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .addComponent(btn_Save0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Cancel0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_Save0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_Cancel0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        centerPanel.add(buttonPanel);

        wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(545, 40));

        javax.swing.GroupLayout southPanelLayout = new javax.swing.GroupLayout(southPanel);
        southPanel.setLayout(southPanelLayout);
        southPanelLayout.setHorizontalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        southPanelLayout.setVerticalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        wholePanel.add(southPanel, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(wholePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(wholePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void changeEnabled_of_SaveCancelButtons(boolean onOff) {
        if (onOff) {
            formMode = FormMode.UpdateMode;
        } else {
            formMode = FormMode.NormalMode;
        }
        ((JButton) getComponentByName("btn_Save" + currentTab.ordinal())).setEnabled(onOff);
        ((JButton) getComponentByName("btn_Cancel" + currentTab.ordinal())).setEnabled(onOff);        
        btn_Exit.setEnabled(!onOff);
    }    
    
    private void btn_Save0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Save0ActionPerformed
        int result = 0;
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            result += saveDataBase(usage);
        }
        
        if (result == 4) {
//            Globals.getOperationLog().setLevel(index2Level(opLoggingIndex));
            JOptionPane.showMessageDialog(this, SAVE_SETTINGS_DIALOG.getContent(),
                SETTINGS_SAVE_RESULT.getContent(), JOptionPane.PLAIN_MESSAGE);
//            enableSaveCancelButtons(false);
            changeEnabled_of_SaveCancelButtons(false);
//            ChangeSettings.changeEnabled_of_SaveCancelButtons(SettingsSaveButton, SettingsCancelButton, 
//                    SettingsCloseButton, changedControls.size());            
        } else {
            JOptionPane.showMessageDialog(this, "E-board settings saving failed.",
                SETTINGS_SAVE_RESULT.getContent(), JOptionPane.ERROR_MESSAGE);
        }            
//        JOptionPane.showOptionDialog(
//            rootPane, 
//            "Saved", 
//            "Confirm", 
//            JOptionPane.DEFAULT_OPTION,
//            JOptionPane.PLAIN_MESSAGE,
//            null,
//            save_Options,
//            save_Options[0]);
    }//GEN-LAST:event_btn_Save0ActionPerformed

    private void btn_Cancel0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Cancel0ActionPerformed
        cancelBtnClick();
    }//GEN-LAST:event_btn_Cancel0ActionPerformed

    private void btn_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExitActionPerformed
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_btn_ExitActionPerformed

    private void formClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formClosing
        tryToCloseEBDSettingsForm();        
    }//GEN-LAST:event_formClosing

    private void combo_ContentType0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_ContentType0PopupMenuWillBecomeInvisible
        setButtonEnabledIfContentTypeChanged(EBD_DisplayUsage.values()[0]);
    }//GEN-LAST:event_combo_ContentType0PopupMenuWillBecomeInvisible

    private void combo_TextColor0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextColor0PopupMenuWillBecomeInvisible
        setButtonEnabledIfColorChanged(EBD_DisplayUsage.values()[0]);
    }//GEN-LAST:event_combo_TextColor0PopupMenuWillBecomeInvisible

    private void combo_DisplayEffect0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_DisplayEffect0PopupMenuWillBecomeInvisible
        setButtonEnabledIfEffectChanged(EBD_DisplayUsage.values()[0]);
    }//GEN-LAST:event_combo_DisplayEffect0PopupMenuWillBecomeInvisible

    private void combo_TextFont0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextFont0PopupMenuWillBecomeInvisible
        setButtonEnabledIfFontChanged(EBD_DisplayUsage.values()[0]);
    }//GEN-LAST:event_combo_TextFont0PopupMenuWillBecomeInvisible

    private void tf_VerbatimContent0KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0KeyReleased
        changeButtonEnabled_IfVebarimChanged(0);
    }//GEN-LAST:event_tf_VerbatimContent0KeyReleased

    private void combo_ContentType0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType0ActionPerformed
        checkContentType(0);
    }//GEN-LAST:event_combo_ContentType0ActionPerformed

    private void tf_VerbatimContent1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_VerbatimContent1KeyReleased

    private void combo_TextColor1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextColor1PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextColor1PopupMenuWillBecomeInvisible

    private void combo_TextFont1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextFont1PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextFont1PopupMenuWillBecomeInvisible

    private void combo_ContentType1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_ContentType1PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType1PopupMenuWillBecomeInvisible

    private void combo_ContentType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType1ActionPerformed

    private void combo_DisplayEffect1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_DisplayEffect1PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_DisplayEffect1PopupMenuWillBecomeInvisible

    private void tf_VerbatimContent2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_VerbatimContent2KeyReleased

    private void combo_TextColor2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextColor2PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextColor2PopupMenuWillBecomeInvisible

    private void combo_TextFont2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextFont2PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextFont2PopupMenuWillBecomeInvisible

    private void combo_ContentType2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_ContentType2PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType2PopupMenuWillBecomeInvisible

    private void combo_ContentType2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType2ActionPerformed

    private void combo_DisplayEffect2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_DisplayEffect2PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_DisplayEffect2PopupMenuWillBecomeInvisible

    private void tf_VerbatimContent3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_VerbatimContent3KeyReleased

    private void combo_TextColor3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextColor3PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextColor3PopupMenuWillBecomeInvisible

    private void combo_TextFont3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_TextFont3PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_TextFont3PopupMenuWillBecomeInvisible

    private void combo_ContentType3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_ContentType3PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType3PopupMenuWillBecomeInvisible

    private void combo_ContentType3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType3ActionPerformed

    private void combo_DisplayEffect3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_DisplayEffect3PopupMenuWillBecomeInvisible
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_DisplayEffect3PopupMenuWillBecomeInvisible

     // <editor-fold defaultstate="collapsed" desc="Generated Code">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Cancel0;
    private javax.swing.JButton btn_Exit;
    private javax.swing.JButton btn_Save0;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox combo_ContentType0;
    private javax.swing.JComboBox combo_ContentType1;
    private javax.swing.JComboBox combo_ContentType2;
    private javax.swing.JComboBox combo_ContentType3;
    private javax.swing.JComboBox combo_DisplayEffect0;
    private javax.swing.JComboBox combo_DisplayEffect1;
    private javax.swing.JComboBox combo_DisplayEffect2;
    private javax.swing.JComboBox combo_DisplayEffect3;
    private javax.swing.JComboBox combo_TextColor0;
    private javax.swing.JComboBox combo_TextColor1;
    private javax.swing.JComboBox combo_TextColor2;
    private javax.swing.JComboBox combo_TextColor3;
    private javax.swing.JComboBox combo_TextFont0;
    private javax.swing.JComboBox combo_TextFont1;
    private javax.swing.JComboBox combo_TextFont2;
    private javax.swing.JComboBox combo_TextFont3;
    private javax.swing.JPanel eBoardPanel0;
    private javax.swing.JPanel eBoardPanel1;
    private javax.swing.JPanel eBoardPanel2;
    private javax.swing.JPanel eBoardPanel3;
    private javax.swing.JTabbedPane eBoardTabPane1;
    private javax.swing.JTabbedPane eBoardTabPane2;
    private javax.swing.JTabbedPane eboardTabbedPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel label_Color0;
    private javax.swing.JLabel label_Color1;
    private javax.swing.JLabel label_Color2;
    private javax.swing.JLabel label_Color4;
    private javax.swing.JLabel label_ContentType0;
    private javax.swing.JLabel label_ContentType1;
    private javax.swing.JLabel label_ContentType2;
    private javax.swing.JLabel label_ContentType4;
    private javax.swing.JLabel label_Effect0;
    private javax.swing.JLabel label_Effect1;
    private javax.swing.JLabel label_Effect2;
    private javax.swing.JLabel label_Effect4;
    private javax.swing.JLabel label_Font0;
    private javax.swing.JLabel label_Font1;
    private javax.swing.JLabel label_Font2;
    private javax.swing.JLabel label_Font4;
    private javax.swing.JLabel label_MSG0;
    private javax.swing.JLabel label_MSG1;
    private javax.swing.JLabel label_MSG2;
    private javax.swing.JLabel label_MSG4;
    private javax.swing.Box.Filler leftFiller;
    private javax.swing.Box.Filler rightFiller;
    private javax.swing.JPanel southPanel;
    private javax.swing.JTextField tf_VerbatimContent0;
    private javax.swing.JTextField tf_VerbatimContent1;
    private javax.swing.JTextField tf_VerbatimContent2;
    private javax.swing.JTextField tf_VerbatimContent3;
    private javax.swing.Box.Filler topFiller;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
   // </editor-fold>
    
    /**
     *  Decide whether to use the verbatim text field after checking the content type.
     */
    public void checkContentType(int index){
        if (((JComboBox)getComponentByName("combo_ContentType" + index)).getSelectedIndex() 
                == EBD_ContentType.VERBATIM.ordinal())
        {
            ((JTextField) getComponentByName("tf_VerbatimContent" + index)).setEnabled(true);
            ((JTextField) getComponentByName("tf_VerbatimContent" + index)).setText(
                ControlGUI.EBD_DisplaySettings[index].verbatimContent);
        }
        else
        {
            ((JTextField) getComponentByName("tf_VerbatimContent" + index)).setEnabled(false);
            ((JTextField) getComponentByName("tf_VerbatimContent" + index)).setText(null);
        }
    }
    
    /**
     * Check the error of the panel.
     * 
     * @param usage_row  Panel that is currently selected
     * @return  <b>true</b> When there is no error in the data inputted to the  <code>panel</code>, 
     * <b>false</b> otherwise
     */
    public boolean checkPanel(EBD_DisplayUsage usage_row){
        boolean result = false;
        
        byte contentType = (byte) ((JComboBox)  getComponentByName(
                "combo_ContentType"+ usage_row.ordinal())).getSelectedIndex();
        
        String verbatimContent = ((JTextField) getComponentByName(
                "tf_VerbatimContent" + usage_row.ordinal())).getText().trim();
        
        byte displayPattern = (byte) ((JComboBox)  getComponentByName(
                "combo_DisplayEffect" + usage_row.ordinal())).getSelectedIndex();

        byte textFont = (byte) ((JComboBox)  getComponentByName(
                "combo_TextFont" + usage_row.ordinal())).getSelectedIndex();
        
        
        byte textColor = (byte) ((JComboBox)  getComponentByName(
                "combo_TextColor" + usage_row.ordinal())).getSelectedIndex();

        
        if(contentType != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal())
            result = true;
        if(!verbatimContent.equals(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent))
            result = true;
        if(displayPattern != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal())
            result = true;
        if(textFont != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal())
            result = true;
        if(textColor != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal()) 
            result = true;

        return result;
    }
    
    /**
     * Enter the panel has been selected as the contents that were stored in the Database.
     * 
     * @param currentTab  Panel that is currently selected
     */
    public void cancelBtnClick(){
        ((JComboBox) getComponentByName("combo_ContentType" + currentTab.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].contentType.ordinal());
        ((JTextField) getComponentByName("tf_VerbatimContent" + currentTab.ordinal()))
                .setText(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].verbatimContent);
        ((JComboBox) getComponentByName("combo_TextColor" + currentTab.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].textColor.ordinal());
        ((JComboBox) getComponentByName("combo_DisplayEffect" + currentTab.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].displayPattern.ordinal());
        ((JComboBox) getComponentByName("combo_TextFont" + currentTab.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].textFont.ordinal());
        
        changeEnabled_of_SaveCancelButtons(false);
    }
    
    /**
     * When another panel doeeot select the property value to match the corresponding panel setting.
     * 
     * @param usage_row  Panel that is currently selected
     */
    public void selectSpecificTab(EBD_DisplayUsage usage_row){
        
        ((JComboBox) getComponentByName("combo_ContentType"+ usage_row.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal());
        
        if(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal() 
                == OSP_enums.EBD_ContentType.VERBATIM.ordinal())
            ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal())).setEnabled(true);
        else
            ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal())).setEnabled(false);
        
        
        ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal()))
                .setText(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent);
        
        ((JComboBox)  getComponentByName("combo_DisplayEffect"+ usage_row.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal());
        
        ((JComboBox)  getComponentByName("combo_TextColor"+ usage_row.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal());
        
        ((JComboBox)  getComponentByName("combo_TextFont"+ usage_row.ordinal()))
                .setSelectedIndex(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal());
        
        changeEnabled_of_SaveCancelButtons(false);
    }
    
    /**
     *  
     * 
     * @param name
     * @return 
     */
    private Component getComponentByName(String name) {
        if (componentMap.containsKey(name)) 
            return (Component) componentMap.get(name);
        else 
            return null;
    }  
    
    /**
     * Stored in the database.
     * 
     * @param usage_row Panel that is currently selected
     */
    public int saveDataBase(EBD_DisplayUsage usage_row){
        JComboBox comboBox = null;
        Connection conn = null;
        PreparedStatement updateSettings = null;
        
        String verbatimStr 
                = ((JTextField) getComponentByName("tf_VerbatimContent" + usage_row.ordinal())).getText().trim();
        
        comboBox = (JComboBox)getComponentByName("combo_ContentType" + usage_row.ordinal());
        
        EBD_ContentType typeItem = (EBD_ContentType)
                (((ConvComboBoxItem)comboBox.getSelectedItem()).getKeyValue());
        
        comboBox = (JComboBox) getComponentByName("combo_DisplayEffect" + usage_row.ordinal());
        EBD_Effects patternItem = (EBD_Effects)
                (((ConvComboBoxItem)comboBox.getSelectedItem()).getKeyValue());
        
        comboBox = (JComboBox) getComponentByName("combo_TextColor" + usage_row.ordinal());
        EBD_Colors colorItem = (EBD_Colors)
                (((ConvComboBoxItem)comboBox.getSelectedItem()).getKeyValue());
        
        comboBox = (JComboBox) getComponentByName("combo_TextFont" + usage_row.ordinal());
        EBD_Fonts fontItem = (EBD_Fonts)
                (((ConvComboBoxItem)comboBox.getSelectedItem()).getKeyValue());
        
        int result = 0;
        try {
            StringBuilder sb = new StringBuilder("Update eboard_settings SET ");
            sb.append("verbatim_content = ?, content_type = ?, display_pattern = ?");
            sb.append(", text_color = ?, text_font = ? WHERE usage_row = ?");

            conn = JDBCMySQL.getConnection();
            updateSettings = conn.prepareStatement(sb.toString());

            int pIndex = 1;
            updateSettings.setString(pIndex++, verbatimStr);
            updateSettings.setInt(pIndex++, typeItem.ordinal());
            updateSettings.setInt(pIndex++, patternItem.ordinal());
            updateSettings.setInt(pIndex++, colorItem.ordinal());
            updateSettings.setInt(pIndex++, fontItem.ordinal());
            updateSettings.setInt(pIndex++, usage_row.getVal());

            result = updateSettings.executeUpdate();
             
        } catch (SQLException ex) {
            Globals.logParkingException(Level.SEVERE, ex, "while saving e-board settings");  
        } finally{
            closeDBstuff(conn, updateSettings, null, "e-board settings modification");
            if (result == 1) {
                //<editor-fold desc="-- Log system settings change if set to do so">
                String currVerbatimStr = ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent;
                if (typeItem == EBD_ContentType.VERBATIM && !currVerbatimStr.equals(verbatimStr)) {
                    logParkingOperation(OpLogLevel.EBDsettingsChange,
                            "E-Board Settings Change, Verbatim Message: " + currVerbatimStr + " => " + verbatimStr);
                }
                //</editor-fold>
                ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent = verbatimStr;
                ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].contentType = typeItem;
                ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].displayPattern = patternItem;
                ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textColor = colorItem;
                ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textFont = fontItem;
            } else {
                JOptionPane.showMessageDialog(this, "This e-board settings update saving DB operation failed.",
                    "DB Update Operation Failure", JOptionPane.ERROR_MESSAGE);
            }
        }

        readEBoardSettings(ControlGUI.EBD_DisplaySettings);

        if (mainForm != null) { // when settings frame invoked alone, main form is null
            if (usage_row.ordinal() == DEFAULT_TOP_ROW.ordinal() 
                    || usage_row.ordinal() == DEFAULT_BOTTOM_ROW.ordinal()) 
            {
                for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
                    if (IDevice.isConnected(mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo], E_Board, gateNo)) 
                    {
                        OSP_enums.EBD_Row row = OSP_enums.EBD_Row.BOTTOM;
                        
                        if (usage_row.ordinal() == DEFAULT_TOP_ROW.ordinal())
                            row = OSP_enums.EBD_Row.TOP;
                        
                        sendEBoardDefaultSetting(mainForm, gateNo, row);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Generating the notification window.
     * @param usage_row information on usage(normal time vs. car arrival) and E-Board row number.
     */
    public void showDialog(EBD_DisplayUsage usage_row){
        String[] message = new String[1];

        if (textFieldCheck(message, usage_row))
        {
            if (!overlapCheck(message, usage_row))
            {
                Object[] options = {"Save", "Cancel"};
                Object[] save_Options = {"Confirm"};
                int n = JOptionPane.showOptionDialog(
                            rootPane, 
                            message, 
                            "Confirmation", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, 
                            null, 
                            options, 
                            options[0]);

                if(n == JOptionPane.YES_OPTION)
                {
                    saveDataBase(usage_row);
                    JOptionPane.showOptionDialog(
                        rootPane, 
                        "Saved", 
                        "Confirm", 
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        save_Options,
                        save_Options[0]);
                    changeEnabled_of_SaveCancelButtons(false);
                }
                else if(n == JOptionPane.NO_OPTION) {
                    eboardTabbedPanel.setSelectedIndex(usage_row.ordinal() < 2 ? 0 : 1);
                    if (usage_row.ordinal() % 2 == 0)
                    {
                        ((JTabbedPane) eboardTabbedPanel
                                .getSelectedComponent()).setSelectedIndex(OSP_enums.EBD_Row.TOP.getValue());
                    }
                    else
                    {
                        ((JTabbedPane) eboardTabbedPanel
                                .getSelectedComponent()).setSelectedIndex(OSP_enums.EBD_Row.BOTTOM.getValue());
                    }
                }
            }
        }
        else
        {
            JOptionPane.showOptionDialog(
                    rootPane, 
                    message[0], 
                    "Error", 
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    null,
                    null);
            eboardTabbedPanel.setSelectedIndex(usage_row.ordinal() < 2 ? 0 : 1);
            if(usage_row.ordinal() % 2 == 0) // top rows
                ((JTabbedPane) eboardTabbedPanel.getSelectedComponent())
                        .setSelectedIndex(OSP_enums.EBD_Row.TOP.ordinal());
            else
                ((JTabbedPane) eboardTabbedPanel.getSelectedComponent())
                        .setSelectedIndex(OSP_enums.EBD_Row.BOTTOM.ordinal());
        }
    }
    
    /**
     * Checks if any field of E-Board settings panel changed and accumulates changed fields' contents.
     * 
     * @param errorMsg  the contents of the error             
     * @param usage_row Panel that is currently selected
     * @return  true when 
     */
    private boolean overlapCheck(String[] errorMsg, EBD_DisplayUsage usage_row){
        boolean result;
        StringBuilder wrongFields = new StringBuilder();
        
        byte contentType = (byte) ((JComboBox)  getComponentByName(
                "combo_ContentType"+ usage_row.ordinal())).getSelectedIndex();
        
        String verbatimContent = ((JTextField) getComponentByName(
                "tf_VerbatimContent" + usage_row.ordinal())).getText();
        
        byte displayPattern = (byte) ((JComboBox)  getComponentByName(
                "combo_DisplayEffect" + usage_row.ordinal())).getSelectedIndex();

        byte textFont = (byte) ((JComboBox)  getComponentByName(
                "combo_TextFont" + usage_row.ordinal())).getSelectedIndex();
        
        
        byte textColor = (byte) ((JComboBox)  getComponentByName(
                "combo_TextColor" + usage_row.ordinal())).getSelectedIndex();

        String wrongText = " Changes \n\n";
        
        if (contentType != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal()) 
        {
             wrongFields.append(" * Content Type \n"); 
             wrongFields.append("    - Current   : " 
                     + ((JComboBox)  getComponentByName("combo_ContentType" + usage_row.ordinal()))
                             .getItemAt(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()]
                                     .contentType.ordinal()) +"\n");
             wrongFields.append("    - Modified : " 
                     + ((JComboBox)  getComponentByName("combo_ContentType" + usage_row.ordinal()))
                             .getItemAt(contentType) +"\n");
             wrongFields.append("\n");
        }
        
        if(!verbatimContent.equals(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent)
                && contentType == OSP_enums.EBD_ContentType.VERBATIM.ordinal()) 
        {
             wrongFields.append(" * Message \n"); 
             wrongFields.append("    - Current   : ")
                     .append(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent).append("\n");
             wrongFields.append("    - Modified : ").append(verbatimContent).append("\n");
             wrongFields.append("\n");
        }

        if(displayPattern != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal())
        {
             wrongFields.append(" * Effect \n"); 
             wrongFields.append("    - Current   : " 
                    + ((JComboBox)  getComponentByName("combo_DisplayEffect"
                            + usage_row.ordinal())).getItemAt(
                                    ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal()) +"\n");
             wrongFields.append("    - Modified : " 
                    + ((JComboBox)  getComponentByName("combo_DisplayEffect" + usage_row.ordinal()))
                            .getItemAt(displayPattern) +"\n");
            wrongFields.append("\n");
        }

        if(textFont != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal())
        {
             wrongFields.append(" * Font \n"); 
             wrongFields.append("    - Current   : " 
                    + ((JComboBox)  getComponentByName("combo_TextFont" + usage_row.ordinal()))
                            .getItemAt(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal()) +"\n");
             wrongFields.append("    - Modified : " 
                    + ((JComboBox)  getComponentByName("combo_TextFont" + usage_row.ordinal()))
                            .getItemAt(textFont) +"\n");
            wrongFields.append("\n");
        }

        if(textColor != ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal())
        {
             wrongFields.append(" * Color \n"); 
             wrongFields.append("    - Current   : " 
                    + ((JComboBox)  getComponentByName("combo_TextColor" + usage_row.ordinal()))
                            .getItemAt(ControlGUI.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal()) +"\n");
             wrongFields.append("    - Modified : " 
                    + ((JComboBox)  getComponentByName("combo_TextColor" + usage_row.ordinal()))
                            .getItemAt(textColor) +"\n");
            wrongFields.append("\n");
        }
        
        if (wrongFields.length() == 0)
        {
            result = true;
        } 
        else 
        {
            result = false;
            wrongFields = wrongFields.insert(0, wrongText);
        }
        errorMsg[0] = wrongFields.toString();
        return result;
    }
    
    /**
     * verbatimContent input checking.
     * @param errorMsg      the contents of the error
     * @param usage_row     Panel that is currently selected
     * @return  errorMsg
     */
    private boolean textFieldCheck(String[] errorMsg, EBD_DisplayUsage usage_row){
        boolean result = false;
        
        
        StringBuilder wrongFields = new StringBuilder();
        
        if(((JComboBox) getComponentByName("combo_ContentType" + usage_row.ordinal()))
                .getSelectedIndex() == OSP_enums.EBD_ContentType.VERBATIM.ordinal()){
            String message = ((JTextField) getComponentByName(
                    "tf_VerbatimContent" +  usage_row.ordinal())).getText().trim();
            if (message.length() <= 0)
            {
                wrongFields.append("  - Please enter a message\n");
                ((JTextField) getComponentByName(
                    "tf_VerbatimContent" +  usage_row.ordinal())).requestFocus();
            }
        }
        if (wrongFields.length() == 0) 
        {
            result = true;
        } 
        errorMsg[0] = wrongFields.toString();
        return result;
    }    

    private void addContentTypeItems() {
        for (EBD_DisplayUsage usage_row : EBD_DisplayUsage.values()) {
            JComboBox comboBox = ((JComboBox) getComponentByName(
                    "combo_ContentType"+ usage_row.ordinal()));
            
            comboBox.removeAllItems();
            for (EBD_ContentType aType : EBD_ContentType.values()) {
                comboBox.addItem(new ConvComboBoxItem(aType, aType.getLabel()));
            }
        }
    }

    private void addTextColorItems() {
        for (EBD_DisplayUsage usage_row : EBD_DisplayUsage.values()) {
            JComboBox comboBox = ((JComboBox) getComponentByName(
                    "combo_TextColor"+ usage_row.ordinal()));
            
            comboBox.removeAllItems();
            for (EBD_Colors aColor : EBD_Colors.values()) {                                
                comboBox.addItem(new ConvComboBoxItem(aColor, aColor.getLabel()));
            }
        }
    }

    private void addDisplayEffectItems() {
        for (EBD_DisplayUsage usage_row : EBD_DisplayUsage.values()) {
            JComboBox comboBox = ((JComboBox) getComponentByName(
                    "combo_DisplayEffect"+ usage_row.ordinal()));
            
            comboBox.removeAllItems();
            for (EBD_Effects anEffect : EBD_Effects.values()) {
                comboBox.addItem(new ConvComboBoxItem(anEffect, anEffect.getLabel()));
            }
        }
    }

    private void addTextFontItems() {
        for (EBD_DisplayUsage usage_row : EBD_DisplayUsage.values()) {
            JComboBox comboBox = ((JComboBox) getComponentByName(
                    "combo_TextFont"+ usage_row.ordinal()));
            
            comboBox.removeAllItems();
            for (EBD_Fonts aFont : EBD_Fonts.values()) {
                
                //<editor-fold desc="-- determine label for each item value">
                String label = "";
                
                switch (aFont) {
                    case Dialog:
                        label = "Dialog";
                        break;
                        
                    case DialogInput:
                        label = "Dialog Input";
                        break;
                        
                    case Microsoft_NeoGothic:
                        label = MS_NEO_GOTHIC.getContent();
                        break;
                        
                    case Monospaced:
                        label = "Monospaced";
                        break;
                        
                    case Sans_Serif:
                        label = SANS_SERIF.getContent() ;
                        break;
                        
                    default:
                        label = "";
                        break;
                }
                //</editor-fold>                
                comboBox.addItem(new ConvComboBoxItem(aFont, label));
            }
        }
    }

    private void setButtonEnabledIfContentTypeChanged(EBD_DisplayUsage usage) {
        JComboBox typeCBox = (JComboBox) getComponentByName("combo_ContentType" + usage.ordinal());
        EBD_ContentType selectedType 
                = (EBD_ContentType) ((ConvComboBoxItem)typeCBox.getSelectedItem()).getKeyValue();
        if (selectedType == ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].contentType) {
            changeEnabled_of_SaveCancelButtons(false);
        }
        else {
            changeEnabled_of_SaveCancelButtons(true);
        }
    }

    private void setButtonEnabledIfColorChanged(EBD_DisplayUsage usage) {
        JComboBox colorCBox = (JComboBox) getComponentByName("combo_TextColor" + usage.ordinal());
        EBD_Colors selectedColor = (EBD_Colors) ((ConvComboBoxItem)colorCBox.getSelectedItem()).getKeyValue();
        
        if (selectedColor == ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].textColor) {
            changeEnabled_of_SaveCancelButtons(false);
        }
        else {
            changeEnabled_of_SaveCancelButtons(true);
        }
    }

    private void setButtonEnabledIfEffectChanged(EBD_DisplayUsage usage) {
        JComboBox effectCBox = (JComboBox) getComponentByName("combo_DisplayEffect" + usage.ordinal());
        EBD_Effects selectedEffect = (EBD_Effects) ((ConvComboBoxItem)effectCBox.getSelectedItem()).getKeyValue();
        
        if (selectedEffect == ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].displayPattern) {
            changeEnabled_of_SaveCancelButtons(false);
        }
        else {
            changeEnabled_of_SaveCancelButtons(true);
        }
    }

    private void setButtonEnabledIfFontChanged(EBD_DisplayUsage usage) {
        JComboBox fontCBox = (JComboBox) getComponentByName("combo_TextFont" + usage.ordinal());
        EBD_Fonts selectedFont = (EBD_Fonts) ((ConvComboBoxItem)fontCBox.getSelectedItem()).getKeyValue();
        
        if (selectedFont == ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].textFont) {
            changeEnabled_of_SaveCancelButtons(false);
        }
        else {
            changeEnabled_of_SaveCancelButtons(true);
        }
    }

    private void changeButtonEnabled_IfVebarimChanged(int index) {
        JTextField verbatimContent = (JTextField) getComponentByName("tf_VerbatimContent" + index);
        String content = verbatimContent.getText().trim();
        if (content.equals(ControlGUI.EBD_DisplaySettings[currentTab.ordinal()].verbatimContent)) {
            changeEnabled_of_SaveCancelButtons(false);
        } else {
            changeEnabled_of_SaveCancelButtons(true);
        }
    }

    private void tryToCloseEBDSettingsForm() {
        if (formMode == FormMode.UpdateMode) {
            JOptionPane.showMessageDialog(this, 
                    "E-Board settings is being modified," + System.lineSeparator()
                            + "Either [Save] or [Cancel] current changes!"); 
        } else {
            parent.setEBDsettings(null); 
            this.dispose();
        }
    }

    private void tuneComponentSize() {
        /**
         * Fix combobox sizes.
         */
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            if (usage != DEFAULT_TOP_ROW && usage != DEFAULT_BOTTOM_ROW)
                continue;
            String compoName = "combo_TextColor" + usage.ordinal();
            JComponent compo = (JComponent)getComponentByName(compoName);
            setComponentSize(compo, new Dimension(100, 30));

            compoName = "combo_DisplayEffect" + usage.ordinal();
            compo = (JComponent)getComponentByName(compoName);
            setComponentSize(compo, new Dimension(100, 30));             
        }
    }
}
