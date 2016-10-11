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

import com.osparking.global.ChangedComponentSave;
import static com.osparking.global.CommonData.TEXT_FIELD_HEIGHT;
import static com.osparking.global.CommonData.setKeyboardLanguage;
import static com.osparking.global.CommonData.tipColor;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CANCEL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_BTN;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.MS_NEO_GOTHIC;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.SANS_SERIF;
import static com.osparking.global.names.ControlEnums.DialogMessages.SAVE_SETTINGS_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.UPDATE_E_BOARD_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.VerbatimDialog;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BOTTOM_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DEFAULT_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.EFFECT_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.E_BOARD_SIM_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.SAVE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.SETTINGS_SAVE_RESULT;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.TOP_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.VEHICLE_TAB_TITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.LabelContent.COLOR_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DISPLAY_TYPE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.EFFECT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FIELD_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FLOW2L_CONF_0;
import static com.osparking.global.names.ControlEnums.LabelContent.FLOW2L_CONF_1;
import static com.osparking.global.names.ControlEnums.LabelContent.FLOW2L_CONF_2;
import static com.osparking.global.names.ControlEnums.LabelContent.FONT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LIMIT_DESCRIPTION;
import static com.osparking.global.names.ControlEnums.LabelContent.MESSAGE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PANEL_LABEL;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
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
import static com.osparking.global.names.EBD_DisplaySetting.EBD_LABEL_SZ;
import static com.osparking.global.names.EBD_DisplaySetting.E_BoardBotFont;
import static com.osparking.global.names.EBD_DisplaySetting.E_BoardTopFont;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.IDevice;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;

/**
 *
 * @author Open Source Parking Inc.
 */
//public class Settings_EBoard extends javax.swing.JFrame {
public class Settings_EBoard extends javax.swing.JFrame {
    public static ControlGUI mainForm = null;
    private HashMap<String,Component> componentMap = new HashMap<String,Component>();
    Settings_System parent = null;
    FormMode formMode = FormMode.NormalMode;
    static private ChangedComponentSave changedControls; 
    
    /**
     * Creates new form TestDisplay
     */
    public Settings_EBoard(ControlGUI mainForm, Settings_System parent) {
        initComponents();
        changedControls = new ChangedComponentSave(btn_Save, btn_Cancel, btn_Exit);
        this.mainForm = mainForm;
        this.parent = parent;
        setResizable(false);
        
        augmentComponentMap(this, componentMap);
        tuneComponentSize();
        
        addContentTypeItems();
        addDisplayEffectItems();
        addTextColorItems();
        addTextFontItems();
        
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            selectSpecificTab(usage);
        }
        
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
        titlePanel = new javax.swing.JPanel();
        attendantGUI_title = new javax.swing.JLabel();
        myMetaKeyLabel = new javax.swing.JLabel();
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
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(80, 25), new java.awt.Dimension(80, 25), new java.awt.Dimension(80, 25));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        btn_Save = new javax.swing.JButton();
        btn_Cancel = new javax.swing.JButton();
        btn_Exit = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        southPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(E_BOARD_SIM_TITLE.getContent());
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formClosing(evt);
            }
        });

        wholePanel.setMinimumSize(new java.awt.Dimension(550, 380));
        wholePanel.setPreferredSize(new java.awt.Dimension(600, 380));
        wholePanel.setLayout(new java.awt.BorderLayout());
        wholePanel.add(topFiller, java.awt.BorderLayout.NORTH);
        wholePanel.add(rightFiller, java.awt.BorderLayout.EAST);
        wholePanel.add(leftFiller, java.awt.BorderLayout.WEST);

        centerPanel.setMinimumSize(new java.awt.Dimension(0, 234));
        centerPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setMinimumSize(new java.awt.Dimension(0, 30));
        titlePanel.setPreferredSize(new java.awt.Dimension(470, 30));

        attendantGUI_title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        attendantGUI_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        attendantGUI_title.setText(E_BOARD_SIM_TITLE.getContent());
        attendantGUI_title.setMaximumSize(new java.awt.Dimension(120, 30));
        attendantGUI_title.setMinimumSize(new java.awt.Dimension(76, 30));
        attendantGUI_title.setPreferredSize(new java.awt.Dimension(120, 30));

        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        myMetaKeyLabel.setMaximumSize(new java.awt.Dimension(80, 30));
        myMetaKeyLabel.setMinimumSize(new java.awt.Dimension(80, 30));
        myMetaKeyLabel.setName(""); // NOI18N
        myMetaKeyLabel.setPreferredSize(new java.awt.Dimension(80, 30));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                .addContainerGap(154, Short.MAX_VALUE)
                .addComponent(attendantGUI_title, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(myMetaKeyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attendantGUI_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myMetaKeyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        centerPanel.add(titlePanel);

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
        eBoardPanel0.setLayout(new java.awt.GridBagLayout());

        label_MSG0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG0.setText(MESSAGE_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_MSG0, gridBagConstraints);

        tf_VerbatimContent0.setColumns(23);
        tf_VerbatimContent0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent0.setMaximumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        tf_VerbatimContent0.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_VerbatimContent0FocusGained(evt);
            }
        });
        tf_VerbatimContent0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent0KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent0KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 50);
        eBoardPanel0.add(tf_VerbatimContent0, gridBagConstraints);

        label_Effect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Effect0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Effect0.setText(EFFECT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        eBoardPanel0.add(label_Effect0, gridBagConstraints);

        label_Color0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color0.setText(COLOR_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_Color0, gridBagConstraints);

        label_Font0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font0.setText(FONT_LABEL.getContent());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(label_Font0, gridBagConstraints);

        combo_TextColor0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextColor0.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_TextColor0.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_TextColor0.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextColor0.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextColor0ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        eBoardPanel0.add(combo_TextColor0, gridBagConstraints);

        combo_TextFont0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_TextFont0.setMaximumSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.setMinimumSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_TextFont0.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextFont0ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
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
        combo_ContentType0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_ContentType0ItemStateChanged(evt);
            }
        });
        combo_ContentType0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_ContentType0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        eBoardPanel0.add(combo_ContentType0, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        eBoardPanel0.add(filler2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        combo_DisplayEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_DisplayEffect0.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect0.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_DisplayEffect0.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        combo_DisplayEffect0.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_DisplayEffect0ItemStateChanged(evt);
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
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
        tf_VerbatimContent1.setName("tf_VerbatimContent" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        tf_VerbatimContent1.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_VerbatimContent1FocusGained(evt);
            }
        });
        tf_VerbatimContent1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent1KeyTyped(evt);
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
        combo_TextColor1.setName("combo_TextColor" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_TextColor1.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextColor1ItemStateChanged(evt);
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
        combo_TextFont1.setName("combo_TextFont" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_TextFont1.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextFont1ItemStateChanged(evt);
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
        combo_ContentType1.setName("combo_ContentType" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_ContentType1.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_ContentType1ItemStateChanged(evt);
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
        combo_DisplayEffect1.setName("combo_DisplayEffect" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        combo_DisplayEffect1.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_DisplayEffect1ItemStateChanged(evt);
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
        tf_VerbatimContent2.setName("tf_VerbatimContent" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        tf_VerbatimContent2.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_VerbatimContent2FocusGained(evt);
            }
        });
        tf_VerbatimContent2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent2KeyTyped(evt);
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
        combo_TextColor2.setName("combo_TextColor" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        combo_TextColor2.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextColor2ItemStateChanged(evt);
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
        combo_TextFont2.setName("combo_TextFont" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        combo_TextFont2.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextFont2ItemStateChanged(evt);
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
        combo_ContentType2.setName("combo_ContentType" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        combo_ContentType2.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_ContentType2ItemStateChanged(evt);
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
        combo_DisplayEffect2.setName("combo_DisplayEffect" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        combo_DisplayEffect2.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_DisplayEffect2ItemStateChanged(evt);
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
        tf_VerbatimContent3.setName("tf_VerbatimContent" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        tf_VerbatimContent3.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_VerbatimContent3FocusGained(evt);
            }
        });
        tf_VerbatimContent3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent3KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent3KeyTyped(evt);
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
        combo_TextColor3.setName("combo_TextColor" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        combo_TextColor3.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_TextColor3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextColor3ItemStateChanged(evt);
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
        combo_TextFont3.setName("combo_TextFont" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        combo_TextFont3.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_TextFont3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_TextFont3ItemStateChanged(evt);
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
        combo_ContentType3.setName("combo_ContentType" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        combo_ContentType3.setPreferredSize(new java.awt.Dimension(150, 30));
        combo_ContentType3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_ContentType3ItemStateChanged(evt);
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
        combo_DisplayEffect3.setName("combo_DisplayEffect" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        combo_DisplayEffect3.setPreferredSize(new java.awt.Dimension(100, 30));
        combo_DisplayEffect3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_DisplayEffect3ItemStateChanged(evt);
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

        btn_Save.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save.setMnemonic('s');
        btn_Save.setText(SAVE_BTN.getContent());
        btn_Save.setInheritsPopupMenu(true);
        btn_Save.setMaximumSize(new java.awt.Dimension(90, 40));
        btn_Save.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Save.setName("btn_Save" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Save.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveActionPerformed(evt);
            }
        });

        btn_Cancel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel.setMnemonic('c');
        btn_Cancel.setText(CANCEL_BTN.getContent());
        btn_Cancel.setEnabled(false);
        btn_Cancel.setMaximumSize(new java.awt.Dimension(90, 40));
        btn_Cancel.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Cancel.setName("btn_Cancel" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Cancel.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CancelActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_Save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
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
            .addGap(0, 550, Short.MAX_VALUE)
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
                .addComponent(wholePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
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
        btn_Save.setEnabled(onOff);
        btn_Cancel.setEnabled(onOff);        
        btn_Exit.setEnabled(!onOff);
    }    
    
    private void btn_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveActionPerformed
        
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            if (!isContentGood(usage)) {
                return;
            }
        }
        confirmAndSaveSettings();
    }//GEN-LAST:event_btn_SaveActionPerformed

    private void btn_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CancelActionPerformed
        reloadE_BoardSettings();
        changeEnabled_of_SaveCancelButtons(false);        
    }//GEN-LAST:event_btn_CancelActionPerformed

    private void btn_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExitActionPerformed
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_btn_ExitActionPerformed

    private void formClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formClosing
        tryToCloseEBDSettingsForm();        
    }//GEN-LAST:event_formClosing

    private void tf_VerbatimContent0KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0KeyReleased
        changeButtonEnabled_IfVebarimChanged(0);
    }//GEN-LAST:event_tf_VerbatimContent0KeyReleased

    private void combo_ContentType0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType0ActionPerformed
        checkContentType(0);
    }//GEN-LAST:event_combo_ContentType0ActionPerformed

    private void tf_VerbatimContent1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1KeyReleased
        changeButtonEnabled_IfVebarimChanged(1);
    }//GEN-LAST:event_tf_VerbatimContent1KeyReleased

    private void combo_ContentType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType1ActionPerformed

    private void tf_VerbatimContent2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2KeyReleased
        changeButtonEnabled_IfVebarimChanged(2);
    }//GEN-LAST:event_tf_VerbatimContent2KeyReleased

    private void combo_ContentType2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType2ActionPerformed

    private void tf_VerbatimContent3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3KeyReleased
        changeButtonEnabled_IfVebarimChanged(3);
    }//GEN-LAST:event_tf_VerbatimContent3KeyReleased

    private void combo_ContentType3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_ContentType3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_ContentType3ActionPerformed

    private void combo_ContentType1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_ContentType1ItemStateChanged
        setButtonEnabledIfContentTypeChanged(1, evt);
    }//GEN-LAST:event_combo_ContentType1ItemStateChanged

    private void combo_ContentType0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_ContentType0ItemStateChanged
        setButtonEnabledIfContentTypeChanged(0, evt);
    }//GEN-LAST:event_combo_ContentType0ItemStateChanged

    private void combo_ContentType2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_ContentType2ItemStateChanged
        setButtonEnabledIfContentTypeChanged(2, evt);
    }//GEN-LAST:event_combo_ContentType2ItemStateChanged

    private void combo_ContentType3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_ContentType3ItemStateChanged
        setButtonEnabledIfContentTypeChanged(3, evt);
    }//GEN-LAST:event_combo_ContentType3ItemStateChanged

    private void combo_TextFont0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextFont0ItemStateChanged
        setButtonEnabledIfFontChanged(0, evt);
    }//GEN-LAST:event_combo_TextFont0ItemStateChanged

    private void combo_TextFont1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextFont1ItemStateChanged
        setButtonEnabledIfFontChanged(1, evt);
    }//GEN-LAST:event_combo_TextFont1ItemStateChanged

    private void combo_TextFont2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextFont2ItemStateChanged
        setButtonEnabledIfFontChanged(2, evt);
    }//GEN-LAST:event_combo_TextFont2ItemStateChanged

    private void combo_TextFont3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextFont3ItemStateChanged
        setButtonEnabledIfFontChanged(3, evt);
    }//GEN-LAST:event_combo_TextFont3ItemStateChanged

    private void combo_TextColor0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextColor0ItemStateChanged
        setButtonEnabledIfColorChanged(0, evt);
    }//GEN-LAST:event_combo_TextColor0ItemStateChanged

    private void combo_TextColor1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextColor1ItemStateChanged
        setButtonEnabledIfColorChanged(1, evt);
    }//GEN-LAST:event_combo_TextColor1ItemStateChanged

    private void combo_TextColor2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextColor2ItemStateChanged
        setButtonEnabledIfColorChanged(2, evt);
    }//GEN-LAST:event_combo_TextColor2ItemStateChanged

    private void combo_TextColor3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_TextColor3ItemStateChanged
        setButtonEnabledIfColorChanged(3, evt);
    }//GEN-LAST:event_combo_TextColor3ItemStateChanged

    private void combo_DisplayEffect0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_DisplayEffect0ItemStateChanged
        setButtonEnabledIfEffectChanged(0, evt);
    }//GEN-LAST:event_combo_DisplayEffect0ItemStateChanged

    private void combo_DisplayEffect1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_DisplayEffect1ItemStateChanged
        setButtonEnabledIfEffectChanged(1, evt);
    }//GEN-LAST:event_combo_DisplayEffect1ItemStateChanged

    private void combo_DisplayEffect2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_DisplayEffect2ItemStateChanged
        setButtonEnabledIfEffectChanged(2, evt);
    }//GEN-LAST:event_combo_DisplayEffect2ItemStateChanged

    private void combo_DisplayEffect3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_DisplayEffect3ItemStateChanged
        setButtonEnabledIfEffectChanged(3, evt);
    }//GEN-LAST:event_combo_DisplayEffect3ItemStateChanged

    private void tf_VerbatimContent0KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0KeyTyped
        checkVerbatimContentLength(0, evt);
    }//GEN-LAST:event_tf_VerbatimContent0KeyTyped

    private void tf_VerbatimContent1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1KeyTyped
        checkVerbatimContentLength(1, evt);
    }//GEN-LAST:event_tf_VerbatimContent1KeyTyped

    private void tf_VerbatimContent2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2KeyTyped
        checkVerbatimContentLength(2, evt);
    }//GEN-LAST:event_tf_VerbatimContent2KeyTyped

    private void tf_VerbatimContent3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3KeyTyped
        checkVerbatimContentLength(3, evt);
    }//GEN-LAST:event_tf_VerbatimContent3KeyTyped

    private void tf_VerbatimContent0FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0FocusGained
        setKeyboardLanguage(tf_VerbatimContent0, KOREAN);
    }//GEN-LAST:event_tf_VerbatimContent0FocusGained

    private void tf_VerbatimContent1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1FocusGained
        setKeyboardLanguage(tf_VerbatimContent1, KOREAN);
    }//GEN-LAST:event_tf_VerbatimContent1FocusGained

    private void tf_VerbatimContent2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2FocusGained
        setKeyboardLanguage(tf_VerbatimContent2, KOREAN);
    }//GEN-LAST:event_tf_VerbatimContent2FocusGained

    private void tf_VerbatimContent3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3FocusGained
        setKeyboardLanguage(tf_VerbatimContent3, KOREAN);
    }//GEN-LAST:event_tf_VerbatimContent3FocusGained

     // <editor-fold defaultstate="collapsed" desc="Generated Code">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attendantGUI_title;
    private javax.swing.JButton btn_Cancel;
    private javax.swing.JButton btn_Exit;
    private javax.swing.JButton btn_Save;
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
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
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
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.Box.Filler rightFiller;
    private javax.swing.JPanel southPanel;
    private javax.swing.JTextField tf_VerbatimContent0;
    private javax.swing.JTextField tf_VerbatimContent1;
    private javax.swing.JTextField tf_VerbatimContent2;
    private javax.swing.JTextField tf_VerbatimContent3;
    private javax.swing.JPanel titlePanel;
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
                parent.EBD_DisplaySettings[index].verbatimContent);
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
        
        if(contentType != parent.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal())
            result = true;
        if(!verbatimContent.equals(parent.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent))
            result = true;
        if(displayPattern != parent.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal())
            result = true;
        if(textFont != parent.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal())
            result = true;
        if(textColor != parent.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal()) 
            result = true;

        return result;
    }
    
    /**
     * When another panel doeeot select the property value to match the corresponding panel setting.
     * 
     * @param usage_row  Panel that is currently selected
     */
    public void selectSpecificTab(EBD_DisplayUsage usage_row){
        
        ((JComboBox) getComponentByName("combo_ContentType"+ usage_row.ordinal()))
                .setSelectedIndex(parent.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal());
        
        if(parent.EBD_DisplaySettings[usage_row.ordinal()].contentType.ordinal() 
                == OSP_enums.EBD_ContentType.VERBATIM.ordinal())
            ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal())).setEnabled(true);
        else
            ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal())).setEnabled(false);
        
        
        ((JTextField) getComponentByName("tf_VerbatimContent"+ usage_row.ordinal()))
                .setText(parent.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent);
        
        ((JComboBox)  getComponentByName("combo_DisplayEffect"+ usage_row.ordinal()))
                .setSelectedIndex(parent.EBD_DisplaySettings[usage_row.ordinal()].displayPattern.ordinal());
        
        ((JComboBox)  getComponentByName("combo_TextColor"+ usage_row.ordinal()))
                .setSelectedIndex(parent.EBD_DisplaySettings[usage_row.ordinal()].textColor.ordinal());
        
        ((JComboBox)  getComponentByName("combo_TextFont"+ usage_row.ordinal()))
                .setSelectedIndex(parent.EBD_DisplaySettings[usage_row.ordinal()].textFont.ordinal());
        
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
        
        //<editor-fold desc="-- Collect E-board simulator settings">
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
        //</editor-fold>
        int result = 0;
        try {
            StringBuilder sb = new StringBuilder("Update eboard_settings SET ");
            sb.append("verbatim_content = ?, content_type = ?, display_pattern = ?");
            sb.append(", text_color = ?, text_font = ? WHERE usage_row = ?");

            conn = JDBCMySQL.getConnection();
            updateSettings = conn.prepareStatement(sb.toString());

            int pIndex = 1;
            //<editor-fold desc="-- Provide actual parameter to update statement">
            updateSettings.setString(pIndex++, verbatimStr);
            updateSettings.setInt(pIndex++, typeItem.ordinal());
            //<editor-fold desc="-- Assign R-to-L flow to long messages">
            if (typeItem == EBD_ContentType.VERBATIM) {
                JComboBox effectBox = ((JComboBox) getComponentByName(
                        "combo_DisplayEffect"+ usage_row.ordinal()));   
                
                if (patternItem != EBD_Effects.RTOL_FLOW && 
                        strOverflowLabel(verbatimStr, usage_row)) { // verbatimStr length matters
                    // Ask user if force pattern to "Flow-R-to-L"
                    int response = JOptionPane.showConfirmDialog(null, 
                            FLOW2L_CONF_0.getContent() + usage_row.getPanelName() +
                                    System.lineSeparator() + System.lineSeparator() +
                                    FLOW2L_CONF_1.getContent() + System.lineSeparator() +
                                    FLOW2L_CONF_2.getContent(),
                            EFFECT_TITLE.getContent(),
                            JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE);                      

                    if (response == JOptionPane.YES_OPTION) {
                        effectBox.setSelectedIndex(EBD_Effects.RTOL_FLOW.ordinal());
                        patternItem = EBD_Effects.RTOL_FLOW;
                    }
                }
            }
            //</editor-fold>
            updateSettings.setInt(pIndex++, patternItem.ordinal());
            updateSettings.setInt(pIndex++, colorItem.ordinal());
            updateSettings.setInt(pIndex++, fontItem.ordinal());
            updateSettings.setInt(pIndex++, usage_row.getVal());
            //</editor-fold>
            result = updateSettings.executeUpdate();
             
        } catch (SQLException ex) {
            Globals.logParkingException(Level.SEVERE, ex, "while saving e-board settings");  
        } finally{
            closeDBstuff(conn, updateSettings, null, "e-board settings modification");
            if (result == 1) {
                //<editor-fold desc="-- Log system settings change if set to do so">
                String currVerbatimStr = parent.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent;
                if (typeItem == EBD_ContentType.VERBATIM && !currVerbatimStr.equals(verbatimStr)) {
                    logParkingOperation(OpLogLevel.SettingsChange,
                            "E-Board Settings Change, Verbatim Message: " + currVerbatimStr + " => " + verbatimStr);
                }
                //</editor-fold>
                //<editor-fold desc="-- Apply new settings to global variable">
                parent.EBD_DisplaySettings[usage_row.ordinal()].verbatimContent = verbatimStr;
                parent.EBD_DisplaySettings[usage_row.ordinal()].contentType = typeItem;
                parent.EBD_DisplaySettings[usage_row.ordinal()].displayPattern = patternItem;
                parent.EBD_DisplaySettings[usage_row.ordinal()].textColor = colorItem;
                parent.EBD_DisplaySettings[usage_row.ordinal()].textFont = fontItem;
                //</editor-fold>
            } else {
                JOptionPane.showMessageDialog(this, "This e-board settings update saving DB operation failed.",
                    "DB Update Operation Failure", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (mainForm != null) { // when settings frame invoked alone, main form is null
            if (usage_row.ordinal() == DEFAULT_TOP_ROW.ordinal() 
                    || usage_row.ordinal() == DEFAULT_BOTTOM_ROW.ordinal()) 
            {
                //<editor-fold desc="-- Apply new settings to the working e-board simulator">
                for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
                    if (IDevice.isConnected(mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo], E_Board, gateNo)) 
                    {
                        OSP_enums.EBD_Row row = OSP_enums.EBD_Row.BOTTOM;
                        
                        if (usage_row.ordinal() == DEFAULT_TOP_ROW.ordinal())
                            row = OSP_enums.EBD_Row.TOP;
                        
                        sendEBoardDefaultSetting(mainForm, gateNo, row);
                    }
                }
                //</editor-fold>
            }
        }
        return result;
    }
    
    private boolean isContentGood(EBD_DisplayUsage usage_row) {
        StringBuffer msgBF = new StringBuffer();
        
        if (!verbatimMessageOK(msgBF, usage_row)) {
            JOptionPane.showOptionDialog(
                    rootPane, 
                    msgBF.toString(), 
                    "Error", 
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    null,
                    null);
            selectErrorHavingTab(usage_row);
            return false;
        } 
        
        return true;
    }
    
    /**
     * verbatimContent input checking.
     * @param errorMsg      the contents of the error
     * @param usage_row     Panel that is currently selected
     * @return  errorMsg
     */
    private boolean verbatimMessageOK(StringBuffer errorMsgSB, EBD_DisplayUsage usage_row){
        boolean checkOK = true;
        
        JComboBox typeCBox = (JComboBox)getComponentByName("combo_ContentType" + usage_row.ordinal());
        
        if (typeCBox.getSelectedIndex() == OSP_enums.EBD_ContentType.VERBATIM.ordinal())
        {
            JTextField messageField = (JTextField) 
                    getComponentByName("tf_VerbatimContent" + usage_row.ordinal());
            String verbatimMessage = messageField.getText().trim();
            
            if (verbatimMessage.length() == 0) {
                checkOK = false;
                errorMsgSB.append(VerbatimDialog.getContent() + System.lineSeparator() +
                        System.lineSeparator() + 
                        PANEL_LABEL.getContent() + " : " +  usage_row.getPanelName() + 
                        System.lineSeparator() + 
                        FIELD_LABEL.getContent() + " : [" +  MESSAGE_LABEL.getContent() + "]" +
                        System.lineSeparator());
                messageField.requestFocus();
            }
        }
        return checkOK;
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

    private void setButtonEnabledIfContentTypeChanged(int index, java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            JComboBox typeCBox = (JComboBox) getComponentByName("combo_ContentType" + index);
            EBD_ContentType selectedType 
                    = (EBD_ContentType) ((ConvComboBoxItem)typeCBox.getSelectedItem()).getKeyValue();

            if (selectedType == parent.EBD_DisplaySettings[index].contentType) {
                changedControls.remove(typeCBox);            
            }
            else {
                changedControls.add(typeCBox);            
            }
            JTextField txtField = (JTextField) getComponentByName("tf_VerbatimContent" + index);
            if (typeCBox.getSelectedIndex() == EBD_ContentType.VERBATIM.ordinal()) {
                txtField.setEnabled(true);
            } else {
                txtField.setText("");
                txtField.setEnabled(false);
            }
        }
    }

    private void setButtonEnabledIfColorChanged(int index, java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            JComboBox colorCBox = (JComboBox) getComponentByName("combo_TextColor" + index);
            EBD_Colors selectedColor = (EBD_Colors) ((ConvComboBoxItem)colorCBox.getSelectedItem()).getKeyValue();

            if (selectedColor == parent.EBD_DisplaySettings[index].textColor) {
                changedControls.remove(colorCBox);            
            }
            else {
                changedControls.add(colorCBox);            
            }
        }
    }

    private void setButtonEnabledIfEffectChanged(int index, java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {        
            JComboBox effectCBox = (JComboBox) getComponentByName("combo_DisplayEffect" + index);
            EBD_Effects selectedEffect = (EBD_Effects) ((ConvComboBoxItem)effectCBox.getSelectedItem()).getKeyValue();

            if (selectedEffect == parent.EBD_DisplaySettings[index].displayPattern) {
                changedControls.remove(effectCBox);            
            } else {
                changedControls.add(effectCBox);            
            }
        }
    }

    private void setButtonEnabledIfFontChanged(int index, java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            JComboBox fontCBox = (JComboBox) getComponentByName("combo_TextFont" + index);
            EBD_Fonts selectedFont = (EBD_Fonts) ((ConvComboBoxItem)fontCBox.getSelectedItem()).getKeyValue();

            if (selectedFont == parent.EBD_DisplaySettings[index].textFont) {
                changedControls.remove(fontCBox);
            }
            else {
                changedControls.add(fontCBox);
            }
        }
    }

    private void changeButtonEnabled_IfVebarimChanged(int index) {
        JTextField verbatimContent = (JTextField) getComponentByName("tf_VerbatimContent" + index);
        String content = verbatimContent.getText().trim();
        
        if (content.equals(parent.EBD_DisplaySettings[index].verbatimContent)) {
            changedControls.remove(verbatimContent);
        } else {
            changedControls.add(verbatimContent);
        }
    }

    private void tryToCloseEBDSettingsForm() {
        if (formMode == FormMode.UpdateMode) {
            JOptionPane.showMessageDialog(this, 
                    "E-Board settings is being modified," + System.lineSeparator()
                            + "Either [Save] or [Cancel] current changes!"); 
        } else {
            dispose();
            parent.disposeEBoardDialog();
        }
    }

    private void tuneComponentSize() {
        /**
         * Fix combobox sizes.
         */
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            String compoName = "combo_TextColor" + usage.ordinal();
            JComponent compo = (JComponent)getComponentByName(compoName);
            
            setComponentSize(compo, new Dimension(100, 30));
            compoName = "combo_DisplayEffect" + usage.ordinal();
            compo = (JComponent)getComponentByName(compoName);
            setComponentSize(compo, new Dimension(100, 30));             
        }
    }

    private void selectErrorHavingTab(EBD_DisplayUsage usage_row) {
        eboardTabbedPanel.setSelectedIndex(usage_row.ordinal() < 2 ? 0 : 1);
        if(usage_row.ordinal() % 2 == 0) // top rows
            ((JTabbedPane) eboardTabbedPanel.getSelectedComponent())
                    .setSelectedIndex(OSP_enums.EBD_Row.TOP.ordinal());
        else
            ((JTabbedPane) eboardTabbedPanel.getSelectedComponent())
                    .setSelectedIndex(OSP_enums.EBD_Row.BOTTOM.ordinal());         
    }

    private void confirmAndSaveSettings() {
        int n = JOptionPane.showConfirmDialog(
                    rootPane, 
                    UPDATE_E_BOARD_DIALOG.getContent(), 
                    SAVE_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);

        if(n == JOptionPane.YES_OPTION)
        {
            int result = 0;
            for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
                result += saveDataBase(usage);
            }            
            if (result == 4) {
                JOptionPane.showMessageDialog(this, SAVE_SETTINGS_DIALOG.getContent(),
                    SETTINGS_SAVE_RESULT.getContent(), JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "E-board settings update failed.",
                    SETTINGS_SAVE_RESULT.getContent(), JOptionPane.ERROR_MESSAGE);
            } 
        }
        else if(n == JOptionPane.NO_OPTION) {
            reloadE_BoardSettings();
        }
        changeEnabled_of_SaveCancelButtons(false);
    }

    private void reloadE_BoardSettings() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            ((JComboBox) getComponentByName("combo_ContentType" + usage.ordinal()))
                    .setSelectedIndex(parent.EBD_DisplaySettings[usage.ordinal()].contentType.ordinal());
            ((JTextField) getComponentByName("tf_VerbatimContent" + usage.ordinal()))
                    .setText(parent.EBD_DisplaySettings[usage.ordinal()].verbatimContent);
            ((JComboBox) getComponentByName("combo_TextColor" + usage.ordinal()))
                    .setSelectedIndex(parent.EBD_DisplaySettings[usage.ordinal()].textColor.ordinal());
            ((JComboBox) getComponentByName("combo_DisplayEffect" + usage.ordinal()))
                    .setSelectedIndex(parent.EBD_DisplaySettings[usage.ordinal()].displayPattern.ordinal());
            ((JComboBox) getComponentByName("combo_TextFont" + usage.ordinal()))
                    .setSelectedIndex(parent.EBD_DisplaySettings[usage.ordinal()].textFont.ordinal());
        }         
    }

    private void checkVerbatimContentLength(int index, KeyEvent evt) {
        JTextField verbatimContent = (JTextField) getComponentByName("tf_VerbatimContent" + index);
        String pureMsg = verbatimContent.getText().trim();
        
        if (pureMsg.length() >= VERBATIM_CONTENT_LENGTH_MAX) 
        {
            getToolkit().beep();
            JOptionPane.showConfirmDialog(this, MESSAGE_LABEL.getContent() + " " +
                    LIMIT_DESCRIPTION.getContent() + " : " + VERBATIM_CONTENT_LENGTH_MAX,
                    ERROR_DIALOGTITLE.getContent(), JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);            
            evt.consume();
        }
    }

    private boolean strOverflowLabel(String pureMsg, EBD_DisplayUsage usage_row) {
        // Calculate message string width in pixels.
        JLabel tempLabel = new JLabel(pureMsg);
        Font eBdFont = null;
        
        if (usage_row == EBD_DisplayUsage.DEFAULT_TOP_ROW 
                || usage_row == EBD_DisplayUsage.CAR_ENTRY_TOP_ROW ) 
        {
            eBdFont = E_BoardTopFont;
        } else {
            eBdFont = E_BoardBotFont;
        }
        
        int currStyle = eBdFont.getStyle();
        int currSize = eBdFont.getSize();  

        JComboBox fontBox = (JComboBox) getComponentByName("combo_TextFont" + usage_row.ordinal());
        ConvComboBoxItem item = (ConvComboBoxItem)fontBox.getSelectedItem();
        String fontName = item.getLabel();
        
        tempLabel.setFont(new Font(fontName, currStyle, currSize));
        Dimension dim = tempLabel.getPreferredSize();
        
        if (dim.width + 20 > EBD_LABEL_SZ.width) {
            System.out.println("overflow");
            return true;
        } else {
            System.out.println("fits in");
            return false;
        }
    }
}
