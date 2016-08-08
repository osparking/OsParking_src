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
package com.osparking.osparking.device.LEDnotice;

import com.osparking.global.ChangedComponentSave;
import static com.osparking.global.CommonData.TEXT_FIELD_HEIGHT;
import static com.osparking.global.CommonData.tipColor;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.augmentComponentMap;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BOTTOM_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DEFAULT_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.TOP_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.VEHICLE_TAB_TITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.names.IDevice;
import com.osparking.global.names.IDevice.ISerial;
import com.osparking.global.names.IDevice.ISocket;
import com.osparking.global.names.JDBCMySQL;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.ConnectionType.TCP_IP;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.global.names.OSP_enums.EBD_DisplayUsage;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.CAR_ENTRY_TOP_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_TOP_ROW;
import com.osparking.global.names.OSP_enums.PermissionType;
import com.osparking.osparking.ControlGUI;
import com.osparking.osparking.Settings_System;
import static com.osparking.osparking.device.LEDnotice.LEDnoticeManager.ledNoticeSettings;
import static com.osparking.osparking.device.LEDnotice.LEDnoticeManager.readLEDnoticeSettings;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.EffectType;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.EffectType.NONE;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LEDnoticeDefaultContentType.Verbatim;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LEDnoticeVehicleContentType;
import static com.osparking.osparking.device.LEDnotice.LedProtocol.getViewWidth;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author Open Source Parking Inc.
 */
public class Settings_LEDnotice extends javax.swing.JFrame { 
    static LEDnoticeManager LEDmanager = null;    
    ControlGUI mainForm = null;
    Settings_System parent = null;
    LEDnoticeManager manager;
    int gateNo;
    FormMode formMode = FormMode.NormalMode;
    static private ChangedComponentSave changedControls; 

    private HashMap<String, Component> componentMap = new HashMap<String,Component>();
    
    boolean[] inDemoMode = new boolean[EBD_DisplayUsage.values().length];
    ControlGUI dummyMainForm = null;
    
    void cancelDemoIfRunning() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            if (inDemoMode[usage.ordinal()]) {
                finishAllEffectDemo(usage.ordinal());
                inDemoMode[usage.ordinal()] = false;
            }
        }
    }
    
    /**
     * Creates new form NewJFrame
     */
    public Settings_LEDnotice(ControlGUI mainForm, Settings_System parent, int gateNo)
    {
        initComponents();
        changedControls = new ChangedComponentSave(btn_Save, btn_Cancel, btn_Exit);
        
        this.mainForm = mainForm;
        this.parent = parent; 
        if (mainForm == null) {
            mainForm = new ControlGUI(true);
        }
        manager = (LEDnoticeManager)mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo];
        
        this.gateNo = gateNo;
        setIconImages(OSPiconList);
        String parentDialogTitle = parent.getE_BoardDialog().getTitle();
        setTitle(parentDialogTitle);
        ledNoticeSettingsGUI_title.setText(parentDialogTitle);
        augmentComponentMap(this, componentMap);
        
        initTypeComboBox();
        initEffectComboBoxes();
        initColorComboBox();
        initFontComboBox();    
        initE_BoardTypeCBox();      
        
        // init selected items using ledNoticeSettings
        
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            selectItemsForSelectedTab(usage);
            inDemoMode[usage.ordinal()] = false;
        }
        changedControls.clear();
    }

    private void initE_BoardTypeCBox() {
        for (int gateNo = 1; gateNo < gateCount; gateNo++) {
            JComboBox ebdTypeBox = (JComboBox)componentMap.get("EBD" + gateNo + "_TypeComboBox");

            if (ebdTypeBox != null) {
                ebdTypeBox.removeAllItems();
                for (OSP_enums.E_BoardType type : OSP_enums.E_BoardType.values()) {
                    ebdTypeBox.addItem(type);
                }
            }
        }
    }

    private void demoCurrentSetting(EBD_DisplayUsage usage) 
    {
        int gateNo = findGateNoUsingLEDnotice();
        
        cancelDemoIfRunning();
        
        if (gateNo == -1) {
            JOptionPane.showMessageDialog(this, "설정된 LEDnotice 장치가 없습니다.");
            return;
        } else {
            LEDnoticeSettings aSetting = getLEDnoticeSetting(usage);
            
            inDemoMode[usage.ordinal()] = true;
            if (mainForm == null) {
                manager.showCurrentEffect(usage, aSetting); 
                enableFinishButton(usage.ordinal(), true);
            } else {
                manager.showCurrentEffect(usage, aSetting); 
                enableFinishButton(usage.ordinal(), true);
            }
        }
    }

    private void finishAllEffectDemo(int index) {
        manager.finishShowingDemoEffect(index);
    }

    private void demoAllEffects(int tabIndex) {
        int gateNo = findGateNoUsingLEDnotice();
    
        cancelDemoIfRunning();
        
        if (gateNo == -1) {
            JOptionPane.showMessageDialog(this, "설정된 LEDnotice 장치가 없습니다.");
            return;
        } else {
            if (mainForm == null) {
                JOptionPane.showMessageDialog(this, "먼저 오즈파킹을 가동하십시오.");
            } else {
                int stopIndex = ((JComboBox)componentMap.get("combo_PauseTime" + tabIndex)).getSelectedIndex();
                int colorIdx = ((JComboBox)componentMap.get("charColor" + tabIndex)).getSelectedIndex();
                int fontIdx = ((JComboBox)componentMap.get("charFont" + tabIndex)).getSelectedIndex();
                inDemoMode[tabIndex] = true;
                LEDnoticeManager manager = (LEDnoticeManager)mainForm.getDeviceManagers()[
                        E_Board.ordinal()][gateNo];

                manager.showAllEffects(tabIndex, stopIndex, colorIdx, fontIdx);
                enableFinishButton(tabIndex, true);    
            }
        }    
    }
    
    private void initColorComboBox() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            JComboBox colorBox = (JComboBox)componentMap.get("charColor" + usage.ordinal());
            
            if (colorBox == null)
                continue;
            colorBox.removeAllItems();
            for (LEDnotice_enums.ColorBox aColor : LEDnotice_enums.ColorBox.values()) {
                colorBox.addItem(aColor.getLabel());
            }  
        }
    }
    
    private void initFontComboBox() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            JComboBox fontBox = (JComboBox)componentMap.get("charFont" + usage.ordinal());
            
            if (fontBox == null)
                continue;
            fontBox.removeAllItems();
            for (LEDnotice_enums.FontBox aFont : LEDnotice_enums.FontBox.values()) {
                fontBox.addItem(aFont.getLabel());
            }  
        }
    }    

    private void initTypeComboBox() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            JComboBox typeBox = (JComboBox)componentMap.get("contentTypeBox" + usage.ordinal());
            
            if (usage == CAR_ENTRY_TOP_ROW || usage == CAR_ENTRY_BOTTOM_ROW) {
                for (LEDnotice_enums.LEDnoticeVehicleContentType aType : 
                        LEDnotice_enums.LEDnoticeVehicleContentType.values()) 
                {
                    typeBox.addItem(aType.getLabel());
                }
            }
            
            for (LEDnotice_enums.LEDnoticeDefaultContentType aType : 
                    LEDnotice_enums.LEDnoticeDefaultContentType.values()) 
            {
                typeBox.addItem(aType.getLabel());
            }  
        }
    }    
    
    private void initEffectComboBoxes() {
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            JComboBox effectBox = (JComboBox)componentMap.get("combo_StartEffect" + usage.ordinal());

            if (effectBox == null)
                continue;
            effectBox.removeAllItems();
            for (EffectType effect : EffectType.values()) {
                if (effect == NONE) 
                    continue;
                effectBox.addItem(effect.getLabel());
            }  
            
            effectBox = (JComboBox)componentMap.get("combo_FinishEffect" + usage.ordinal());

            if (effectBox == null)
                continue;
            effectBox.removeAllItems();
            for (EffectType effect : EffectType.values()) {
                effectBox.addItem(effect.getLabel());
            } 
            
            if (usage == EBD_DisplayUsage.DEFAULT_TOP_ROW || 
                    usage == EBD_DisplayUsage.CAR_ENTRY_TOP_ROW)
            {
                if (ledNoticeSettings[usage.ordinal()].isUsed) {
                    effectBox.setEnabled(false);
                }
            }
        }
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

        jPanel2 = new javax.swing.JPanel();
        topFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        leftFiller = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        rightFiller = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        centerPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        ledNoticeSettingsGUI_title = new javax.swing.JLabel();
        ledNoticeTabbedPane = new javax.swing.JTabbedPane();
        ledNoticePanelDefault = new javax.swing.JTabbedPane();
        ledNoticePanel0 = new javax.swing.JPanel();
        label_MSG0 = new javax.swing.JLabel();
        label_Color0 = new javax.swing.JLabel();
        label_Font0 = new javax.swing.JLabel();
        label_ContentType0 = new javax.swing.JLabel();
        contentTypeBox0 = new javax.swing.JComboBox();
        tf_VerbatimContent0 = new javax.swing.JTextField();
        charColor0 = new javax.swing.JComboBox();
        charFont0 = new javax.swing.JComboBox(LEDnotice_enums.FontBox.values());
        combo_StartEffect0 = new javax.swing.JComboBox();
        combo_FinishEffect0 = new javax.swing.JComboBox();
        label_Color4 = new javax.swing.JLabel();
        label_Color5 = new javax.swing.JLabel();
        label_Color6 = new javax.swing.JLabel();
        midStopPanel0 = new javax.swing.JPanel();
        combo_PauseTime0 = new javax.swing.JComboBox();
        label_Color7 = new javax.swing.JLabel();
        useLEDnoticeCkBox0 = new javax.swing.JCheckBox();
        demoButton0 = new javax.swing.JButton();
        demoFinishButton0 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        startEffectHelpButton0 = new javax.swing.JButton();
        demoCurrHelpButton0 = new javax.swing.JButton();
        demoAllHelpButton0 = new javax.swing.JButton();
        endEffectHelpButton0 = new javax.swing.JButton();
        demoAllButton0 = new javax.swing.JButton();
        pauseTimeHelpButton0 = new javax.swing.JButton();
        useCkBoxHelpButton0 = new javax.swing.JButton();
        ledNoticePanel1 = new javax.swing.JPanel();
        label_MSG2 = new javax.swing.JLabel();
        label_Color2 = new javax.swing.JLabel();
        label_Font2 = new javax.swing.JLabel();
        label_ContentType2 = new javax.swing.JLabel();
        contentTypeBox1 = new javax.swing.JComboBox();
        tf_VerbatimContent1 = new javax.swing.JTextField();
        charColor1 = new javax.swing.JComboBox();
        charFont1 = new javax.swing.JComboBox(LEDnotice_enums.FontBox.values());
        combo_StartEffect1 = new javax.swing.JComboBox();
        combo_FinishEffect1 = new javax.swing.JComboBox();
        label_Color22 = new javax.swing.JLabel();
        label_Color23 = new javax.swing.JLabel();
        label_Color24 = new javax.swing.JLabel();
        midStopPanel1 = new javax.swing.JPanel();
        combo_PauseTime1 = new javax.swing.JComboBox();
        label_Color25 = new javax.swing.JLabel();
        useLEDnoticeCkBox1 = new javax.swing.JCheckBox();
        demoButton1 = new javax.swing.JButton();
        demoFinishButton1 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        startEffectHelpButton1 = new javax.swing.JButton();
        demoCurrHelpButton1 = new javax.swing.JButton();
        demoAllHelpButton1 = new javax.swing.JButton();
        demoAllButton1 = new javax.swing.JButton();
        useCkBoxHelpButton1 = new javax.swing.JButton();
        ledNoticePanelVehicle = new javax.swing.JTabbedPane();
        ledNoticePanel2 = new javax.swing.JPanel();
        label_MSG1 = new javax.swing.JLabel();
        label_Color1 = new javax.swing.JLabel();
        label_Font1 = new javax.swing.JLabel();
        label_ContentType1 = new javax.swing.JLabel();
        contentTypeBox2 = new javax.swing.JComboBox();
        tf_VerbatimContent2 = new javax.swing.JTextField();
        charColor2 = new javax.swing.JComboBox();
        charFont2 = new javax.swing.JComboBox(LEDnotice_enums.FontBox.values());
        combo_StartEffect2 = new javax.swing.JComboBox();
        combo_FinishEffect2 = new javax.swing.JComboBox();
        label_Color8 = new javax.swing.JLabel();
        label_Color9 = new javax.swing.JLabel();
        label_Color10 = new javax.swing.JLabel();
        midStopPanel2 = new javax.swing.JPanel();
        combo_PauseTime2 = new javax.swing.JComboBox();
        label_Color11 = new javax.swing.JLabel();
        useLEDnoticeCkBox2 = new javax.swing.JCheckBox();
        demoButton2 = new javax.swing.JButton();
        demoFinishButton2 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        startEffectHelpButton2 = new javax.swing.JButton();
        demoCurrHelpButton2 = new javax.swing.JButton();
        demoAllHelpButton2 = new javax.swing.JButton();
        endEffectHelpButton2 = new javax.swing.JButton();
        demoAllButton2 = new javax.swing.JButton();
        pauseTimeHelpButton2 = new javax.swing.JButton();
        useCkBoxHelpButton2 = new javax.swing.JButton();
        ledNoticePanel3 = new javax.swing.JPanel();
        label_MSG3 = new javax.swing.JLabel();
        label_Color3 = new javax.swing.JLabel();
        label_Font3 = new javax.swing.JLabel();
        label_ContentType3 = new javax.swing.JLabel();
        contentTypeBox3 = new javax.swing.JComboBox();
        tf_VerbatimContent3 = new javax.swing.JTextField();
        charColor3 = new javax.swing.JComboBox();
        charFont3 = new javax.swing.JComboBox(LEDnotice_enums.FontBox.values());
        combo_StartEffect3 = new javax.swing.JComboBox();
        combo_FinishEffect3 = new javax.swing.JComboBox();
        label_Color26 = new javax.swing.JLabel();
        label_Color27 = new javax.swing.JLabel();
        label_Color28 = new javax.swing.JLabel();
        midStopPanel3 = new javax.swing.JPanel();
        combo_PauseTime5 = new javax.swing.JComboBox();
        label_Color29 = new javax.swing.JLabel();
        useLEDnoticeCkBox3 = new javax.swing.JCheckBox();
        demoButton3 = new javax.swing.JButton();
        demoFinishButton3 = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        startEffectHelpButton3 = new javax.swing.JButton();
        demoCurrHelpButton3 = new javax.swing.JButton();
        demoAllHelpButton3 = new javax.swing.JButton();
        endEffectHelpButton3 = new javax.swing.JButton();
        demoAllButton3 = new javax.swing.JButton();
        pauseTimeHelpButton3 = new javax.swing.JButton();
        useCkBoxHelpButton3 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        buttonPanel = new javax.swing.JPanel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(80, 25), new java.awt.Dimension(80, 25), new java.awt.Dimension(80, 25));
        btn_Save = new javax.swing.JButton();
        btn_Cancel = new javax.swing.JButton();
        btn_Exit = new javax.swing.JButton();
        myMetaKeyLabel = new javax.swing.JLabel();
        southPanel = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(601, 462));
        setPreferredSize(new java.awt.Dimension(635, 490));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().add(topFiller, java.awt.BorderLayout.NORTH);
        getContentPane().add(leftFiller, java.awt.BorderLayout.WEST);
        getContentPane().add(rightFiller, java.awt.BorderLayout.EAST);

        centerPanel.setPreferredSize(new java.awt.Dimension(516, 285));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setMinimumSize(new java.awt.Dimension(0, 30));
        titlePanel.setPreferredSize(new java.awt.Dimension(470, 30));
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        ledNoticeSettingsGUI_title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        ledNoticeSettingsGUI_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ledNoticeSettingsGUI_title.setText(getTitle());
        ledNoticeSettingsGUI_title.setMaximumSize(new java.awt.Dimension(120, 30));
        ledNoticeSettingsGUI_title.setMinimumSize(new java.awt.Dimension(76, 30));
        ledNoticeSettingsGUI_title.setPreferredSize(new java.awt.Dimension(200, 30));
        titlePanel.add(ledNoticeSettingsGUI_title);

        centerPanel.add(titlePanel);

        ledNoticeTabbedPane.setToolTipText("");
        ledNoticeTabbedPane.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ledNoticeTabbedPane.setMinimumSize(new java.awt.Dimension(521, 268));
        ledNoticeTabbedPane.setName("ledNoticeTabbedPane"); // NOI18N
        ledNoticeTabbedPane.setPreferredSize(new java.awt.Dimension(506, 280));
        ledNoticeTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ledNoticeTabbedPaneStateChanged(evt);
            }
        });

        ledNoticePanelDefault.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, null));
        ledNoticePanelDefault.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        ledNoticePanelDefault.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ledNoticePanelDefault.setMinimumSize(new java.awt.Dimension(300, 260));
        ledNoticePanelDefault.setName("Default_Panel"); // NOI18N
        ledNoticePanelDefault.setPreferredSize(new java.awt.Dimension(516, 280));
        ledNoticePanelDefault.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ledNoticePanelDefaultStateChanged(evt);
            }
        });

        ledNoticePanel0.setMinimumSize(new java.awt.Dimension(433, 260));
        ledNoticePanel0.setName("eBoard" + EBD_DisplayUsage.DEFAULT_TOP_ROW.getVal());
        ledNoticePanel0.setPreferredSize(new java.awt.Dimension(469, 280));
        ledNoticePanel0.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ledNoticePanel0FocusGained(evt);
            }
        });
        ledNoticePanel0.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                ledNoticePanel0ComponentShown(evt);
            }
        });
        ledNoticePanel0.setLayout(new java.awt.GridBagLayout());

        label_MSG0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG0.setText("문자열");
        label_MSG0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel0.add(label_MSG0, gridBagConstraints);

        label_Color0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color0.setText("중간멈춤");
        label_Color0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel0.add(label_Color0, gridBagConstraints);

        label_Font0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font0.setText("폰트");
        label_Font0.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font0.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel0.add(label_Font0, gridBagConstraints);

        label_ContentType0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType0.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel0.add(label_ContentType0, gridBagConstraints);

        contentTypeBox0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox0.setMinimumSize(new java.awt.Dimension(123, 30));
        contentTypeBox0.setName("contentTypeBox0"); // NOI18N
        contentTypeBox0.setPreferredSize(new java.awt.Dimension(123, 30));
        contentTypeBox0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                contentTypeBox0ItemStateChanged(evt);
            }
        });
        contentTypeBox0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        ledNoticePanel0.add(contentTypeBox0, gridBagConstraints);

        tf_VerbatimContent0.setColumns(23);
        tf_VerbatimContent0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent0.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.setName("tf_VerbatimContent0"); // NOI18N
        tf_VerbatimContent0.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent0KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        ledNoticePanel0.add(tf_VerbatimContent0, gridBagConstraints);

        charColor0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor0.setMinimumSize(new java.awt.Dimension(70, 30));
        charColor0.setName("charColor0"); // NOI18N
        charColor0.setPreferredSize(new java.awt.Dimension(70, 30));
        charColor0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charColor0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel0.add(charColor0, gridBagConstraints);

        charFont0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont0.setMinimumSize(new java.awt.Dimension(70, 30));
        charFont0.setName("charFont0"); // NOI18N
        charFont0.setPreferredSize(new java.awt.Dimension(70, 30));
        charFont0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charFont0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel0.add(charFont0, gridBagConstraints);

        combo_StartEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect0.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_StartEffect0.setName("combo_StartEffect0"); // NOI18N
        combo_StartEffect0.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_StartEffect0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_StartEffect0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel0.add(combo_StartEffect0, gridBagConstraints);

        combo_FinishEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect0.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect0.setName("combo_FinishEffect0"); // NOI18N
        combo_FinishEffect0.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_FinishEffect0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        ledNoticePanel0.add(combo_FinishEffect0, gridBagConstraints);

        label_Color4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color4.setText("색상");
        label_Color4.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color4.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel0.add(label_Color4, gridBagConstraints);

        label_Color5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color5.setText("마침효과");
        label_Color5.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 5);
        ledNoticePanel0.add(label_Color5, gridBagConstraints);

        label_Color6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color6.setText("시작효과");
        label_Color6.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        ledNoticePanel0.add(label_Color6, gridBagConstraints);

        midStopPanel0.setMaximumSize(new java.awt.Dimension(95, 30));
        midStopPanel0.setMinimumSize(new java.awt.Dimension(95, 30));
        midStopPanel0.setPreferredSize(new java.awt.Dimension(100, 30));
        midStopPanel0.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        combo_PauseTime0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime0.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime0.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime0.setName("combo_PauseTime0"); // NOI18N
        combo_PauseTime0.setPreferredSize(new java.awt.Dimension(70, 30));
        combo_PauseTime0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_PauseTime0ActionPerformed(evt);
            }
        });
        midStopPanel0.add(combo_PauseTime0);

        label_Color7.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_Color7.setText("초");
        label_Color7.setMaximumSize(new java.awt.Dimension(25, 15));
        label_Color7.setMinimumSize(new java.awt.Dimension(25, 15));
        label_Color7.setPreferredSize(new java.awt.Dimension(25, 15));
        midStopPanel0.add(label_Color7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel0.add(midStopPanel0, gridBagConstraints);

        useLEDnoticeCkBox0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox0.setText("사용");
        useLEDnoticeCkBox0.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox0.setName("useLEDnoticeCkBox0"); // NOI18N
        useLEDnoticeCkBox0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useLEDnoticeCkBox0ItemStateChanged(evt);
            }
        });
        useLEDnoticeCkBox0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        ledNoticePanel0.add(useLEDnoticeCkBox0, gridBagConstraints);

        demoButton0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton0.setText("현재");
        demoButton0.setEnabled(false);
        demoButton0.setMaximumSize(new java.awt.Dimension(59, 30));
        demoButton0.setMinimumSize(new java.awt.Dimension(59, 30));
        demoButton0.setName("demoButton0"); // NOI18N
        demoButton0.setPreferredSize(new java.awt.Dimension(59, 30));
        demoButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel0.add(demoButton0, gridBagConstraints);

        demoFinishButton0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton0.setText("그만");
        demoFinishButton0.setEnabled(false);
        demoFinishButton0.setMaximumSize(new java.awt.Dimension(59, 30));
        demoFinishButton0.setMinimumSize(new java.awt.Dimension(59, 30));
        demoFinishButton0.setName("demoFinishButton0"); // NOI18N
        demoFinishButton0.setPreferredSize(new java.awt.Dimension(59, 30));
        demoFinishButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 0);
        ledNoticePanel0.add(demoFinishButton0, gridBagConstraints);

        jLabel41.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel41.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        ledNoticePanel0.add(jLabel41, gridBagConstraints);

        startEffectHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        startEffectHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        startEffectHelpButton0.setIcon(getQuest20_Icon());
        startEffectHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        startEffectHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton0.setOpaque(false);
        startEffectHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startEffectHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel0.add(startEffectHelpButton0, gridBagConstraints);

        demoCurrHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        demoCurrHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoCurrHelpButton0.setIcon(getQuest20_Icon());
        demoCurrHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoCurrHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton0.setOpaque(false);
        demoCurrHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoCurrHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel0.add(demoCurrHelpButton0, gridBagConstraints);

        demoAllHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        demoAllHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoAllHelpButton0.setIcon(getQuest20_Icon());
        demoAllHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoAllHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton0.setOpaque(false);
        demoAllHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel0.add(demoAllHelpButton0, gridBagConstraints);

        endEffectHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        endEffectHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        endEffectHelpButton0.setIcon(getQuest20_Icon());
        endEffectHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        endEffectHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton0.setOpaque(false);
        endEffectHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endEffectHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel0.add(endEffectHelpButton0, gridBagConstraints);

        demoAllButton0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton0.setText("전체");
        demoAllButton0.setMaximumSize(new java.awt.Dimension(59, 30));
        demoAllButton0.setMinimumSize(new java.awt.Dimension(59, 30));
        demoAllButton0.setName("demoAllButton0"); // NOI18N
        demoAllButton0.setPreferredSize(new java.awt.Dimension(59, 30));
        demoAllButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel0.add(demoAllButton0, gridBagConstraints);

        pauseTimeHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        pauseTimeHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        pauseTimeHelpButton0.setIcon(getQuest20_Icon());
        pauseTimeHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pauseTimeHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton0.setOpaque(false);
        pauseTimeHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseTimeHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel0.add(pauseTimeHelpButton0, gridBagConstraints);

        useCkBoxHelpButton0.setBackground(new java.awt.Color(153, 255, 153));
        useCkBoxHelpButton0.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        useCkBoxHelpButton0.setIcon(getQuest20_Icon());
        useCkBoxHelpButton0.setMargin(new java.awt.Insets(2, 4, 2, 4));
        useCkBoxHelpButton0.setMinimumSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton0.setOpaque(false);
        useCkBoxHelpButton0.setPreferredSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCkBoxHelpButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        ledNoticePanel0.add(useCkBoxHelpButton0, gridBagConstraints);

        ledNoticePanelDefault.addTab(TOP_TAB_TITLE.getContent(), ledNoticePanel0);

        ledNoticePanel1.setName("eBoard" + EBD_DisplayUsage.DEFAULT_TOP_ROW.getVal());
        ledNoticePanel1.setLayout(new java.awt.GridBagLayout());

        label_MSG2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG2.setText("문자열");
        label_MSG2.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel1.add(label_MSG2, gridBagConstraints);

        label_Color2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color2.setText("중간멈춤");
        label_Color2.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel1.add(label_Color2, gridBagConstraints);

        label_Font2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font2.setText("폰트");
        label_Font2.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font2.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel1.add(label_Font2, gridBagConstraints);

        label_ContentType2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType2.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel1.add(label_ContentType2, gridBagConstraints);

        contentTypeBox1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox1.setMinimumSize(new java.awt.Dimension(123, 30));
        contentTypeBox1.setName("contentTypeBox1"); // NOI18N
        contentTypeBox1.setPreferredSize(new java.awt.Dimension(123, 30));
        contentTypeBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        ledNoticePanel1.add(contentTypeBox1, gridBagConstraints);

        tf_VerbatimContent1.setColumns(23);
        tf_VerbatimContent1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent1.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.setName("tf_VerbatimContent1"); // NOI18N
        tf_VerbatimContent1.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent1KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        ledNoticePanel1.add(tf_VerbatimContent1, gridBagConstraints);

        charColor1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor1.setMinimumSize(new java.awt.Dimension(70, 30));
        charColor1.setName("charColor1"); // NOI18N
        charColor1.setPreferredSize(new java.awt.Dimension(70, 30));
        charColor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charColor1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel1.add(charColor1, gridBagConstraints);

        charFont1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont1.setMinimumSize(new java.awt.Dimension(70, 30));
        charFont1.setName("charFont1"); // NOI18N
        charFont1.setPreferredSize(new java.awt.Dimension(70, 30));
        charFont1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charFont1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel1.add(charFont1, gridBagConstraints);

        combo_StartEffect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect1.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_StartEffect1.setName("combo_StartEffect1"); // NOI18N
        combo_StartEffect1.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_StartEffect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_StartEffect1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel1.add(combo_StartEffect1, gridBagConstraints);

        combo_FinishEffect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect1.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect1.setName("combo_FinishEffect1"); // NOI18N
        combo_FinishEffect1.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_FinishEffect1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        ledNoticePanel1.add(combo_FinishEffect1, gridBagConstraints);

        label_Color22.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color22.setText("색상");
        label_Color22.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color22.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel1.add(label_Color22, gridBagConstraints);

        label_Color23.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color23.setText("마침효과");
        label_Color23.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 5);
        ledNoticePanel1.add(label_Color23, gridBagConstraints);

        label_Color24.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color24.setText("시작효과");
        label_Color24.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        ledNoticePanel1.add(label_Color24, gridBagConstraints);

        midStopPanel1.setMaximumSize(new java.awt.Dimension(95, 30));
        midStopPanel1.setPreferredSize(new java.awt.Dimension(100, 30));
        midStopPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        combo_PauseTime1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime1.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime1.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime1.setName("combo_PauseTime1"); // NOI18N
        combo_PauseTime1.setPreferredSize(new java.awt.Dimension(70, 30));
        combo_PauseTime1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_PauseTime1ActionPerformed(evt);
            }
        });
        midStopPanel1.add(combo_PauseTime1);

        label_Color25.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_Color25.setText("초");
        label_Color25.setMaximumSize(new java.awt.Dimension(25, 15));
        label_Color25.setMinimumSize(new java.awt.Dimension(25, 15));
        label_Color25.setPreferredSize(new java.awt.Dimension(25, 15));
        midStopPanel1.add(label_Color25);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel1.add(midStopPanel1, gridBagConstraints);

        useLEDnoticeCkBox1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox1.setText("사용");
        useLEDnoticeCkBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox1.setName("useLEDnoticeCkBox1"); // NOI18N
        useLEDnoticeCkBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useLEDnoticeCkBox1ItemStateChanged(evt);
            }
        });
        useLEDnoticeCkBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        ledNoticePanel1.add(useLEDnoticeCkBox1, gridBagConstraints);

        demoButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton1.setText("현재");
        demoButton1.setEnabled(false);
        demoButton1.setMaximumSize(new java.awt.Dimension(59, 30));
        demoButton1.setMinimumSize(new java.awt.Dimension(59, 30));
        demoButton1.setName("demoButton1"); // NOI18N
        demoButton1.setPreferredSize(new java.awt.Dimension(59, 30));
        demoButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel1.add(demoButton1, gridBagConstraints);

        demoFinishButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton1.setText("그만");
        demoFinishButton1.setEnabled(false);
        demoFinishButton1.setMaximumSize(new java.awt.Dimension(59, 30));
        demoFinishButton1.setMinimumSize(new java.awt.Dimension(59, 30));
        demoFinishButton1.setName("demoFinishButton1"); // NOI18N
        demoFinishButton1.setPreferredSize(new java.awt.Dimension(59, 30));
        demoFinishButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 0);
        ledNoticePanel1.add(demoFinishButton1, gridBagConstraints);

        jLabel45.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel45.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        ledNoticePanel1.add(jLabel45, gridBagConstraints);

        startEffectHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        startEffectHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        startEffectHelpButton1.setIcon(getQuest20_Icon());
        startEffectHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        startEffectHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton1.setOpaque(false);
        startEffectHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startEffectHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel1.add(startEffectHelpButton1, gridBagConstraints);

        demoCurrHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        demoCurrHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoCurrHelpButton1.setIcon(getQuest20_Icon());
        demoCurrHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoCurrHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton1.setOpaque(false);
        demoCurrHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoCurrHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel1.add(demoCurrHelpButton1, gridBagConstraints);

        demoAllHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        demoAllHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoAllHelpButton1.setIcon(getQuest20_Icon());
        demoAllHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoAllHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton1.setOpaque(false);
        demoAllHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel1.add(demoAllHelpButton1, gridBagConstraints);

        demoAllButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton1.setText("전체");
        demoAllButton1.setMaximumSize(new java.awt.Dimension(59, 30));
        demoAllButton1.setMinimumSize(new java.awt.Dimension(59, 30));
        demoAllButton1.setName("demoAllButton1"); // NOI18N
        demoAllButton1.setPreferredSize(new java.awt.Dimension(59, 30));
        demoAllButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel1.add(demoAllButton1, gridBagConstraints);

        useCkBoxHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        useCkBoxHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        useCkBoxHelpButton1.setIcon(getQuest20_Icon());
        useCkBoxHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        useCkBoxHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton1.setOpaque(false);
        useCkBoxHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCkBoxHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        ledNoticePanel1.add(useCkBoxHelpButton1, gridBagConstraints);

        ledNoticePanelDefault.addTab(BOTTOM_TAB_TITLE.getContent(), ledNoticePanel1);

        ledNoticeTabbedPane.addTab(DEFAULT_TAB_TITLE.getContent(), ledNoticePanelDefault);

        ledNoticePanelVehicle.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, null));
        ledNoticePanelVehicle.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        ledNoticePanelVehicle.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ledNoticePanelVehicle.setName("Vehicle_Panel"); // NOI18N
        ledNoticePanelVehicle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ledNoticePanelVehicleStateChanged(evt);
            }
        });

        ledNoticePanel2.setName("eBoard" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.getVal());
        ledNoticePanel2.setLayout(new java.awt.GridBagLayout());

        label_MSG1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG1.setText("문자열");
        label_MSG1.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel2.add(label_MSG1, gridBagConstraints);

        label_Color1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color1.setText("중간멈춤");
        label_Color1.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel2.add(label_Color1, gridBagConstraints);

        label_Font1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font1.setText("폰트");
        label_Font1.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font1.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel2.add(label_Font1, gridBagConstraints);

        label_ContentType1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType1.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel2.add(label_ContentType1, gridBagConstraints);

        contentTypeBox2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox2.setMinimumSize(new java.awt.Dimension(123, 30));
        contentTypeBox2.setName("contentTypeBox2"); // NOI18N
        contentTypeBox2.setPreferredSize(new java.awt.Dimension(123, 30));
        contentTypeBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        ledNoticePanel2.add(contentTypeBox2, gridBagConstraints);

        tf_VerbatimContent2.setColumns(23);
        tf_VerbatimContent2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent2.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.setName("tf_VerbatimContent2"); // NOI18N
        tf_VerbatimContent2.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent2KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        ledNoticePanel2.add(tf_VerbatimContent2, gridBagConstraints);

        charColor2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor2.setMinimumSize(new java.awt.Dimension(70, 30));
        charColor2.setName("charColor2"); // NOI18N
        charColor2.setPreferredSize(new java.awt.Dimension(70, 30));
        charColor2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charColor2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel2.add(charColor2, gridBagConstraints);

        charFont2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont2.setMinimumSize(new java.awt.Dimension(70, 30));
        charFont2.setName("charFont2"); // NOI18N
        charFont2.setPreferredSize(new java.awt.Dimension(70, 30));
        charFont2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charFont2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel2.add(charFont2, gridBagConstraints);

        combo_StartEffect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect2.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_StartEffect2.setName("combo_StartEffect2"); // NOI18N
        combo_StartEffect2.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_StartEffect2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_StartEffect2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel2.add(combo_StartEffect2, gridBagConstraints);

        combo_FinishEffect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect2.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect2.setName("combo_FinishEffect2"); // NOI18N
        combo_FinishEffect2.setPreferredSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_FinishEffect2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        ledNoticePanel2.add(combo_FinishEffect2, gridBagConstraints);

        label_Color8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color8.setText("색상");
        label_Color8.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color8.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel2.add(label_Color8, gridBagConstraints);

        label_Color9.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color9.setText("마침효과");
        label_Color9.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 5);
        ledNoticePanel2.add(label_Color9, gridBagConstraints);

        label_Color10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color10.setText("시작효과");
        label_Color10.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        ledNoticePanel2.add(label_Color10, gridBagConstraints);

        midStopPanel2.setMaximumSize(new java.awt.Dimension(95, 30));
        midStopPanel2.setPreferredSize(new java.awt.Dimension(100, 30));
        midStopPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        combo_PauseTime2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime2.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime2.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime2.setName("combo_PauseTime2"); // NOI18N
        combo_PauseTime2.setPreferredSize(new java.awt.Dimension(70, 30));
        combo_PauseTime2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_PauseTime2ActionPerformed(evt);
            }
        });
        midStopPanel2.add(combo_PauseTime2);

        label_Color11.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_Color11.setText("초");
        label_Color11.setMaximumSize(new java.awt.Dimension(25, 15));
        label_Color11.setMinimumSize(new java.awt.Dimension(25, 15));
        label_Color11.setPreferredSize(new java.awt.Dimension(25, 15));
        midStopPanel2.add(label_Color11);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel2.add(midStopPanel2, gridBagConstraints);

        useLEDnoticeCkBox2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox2.setText("사용");
        useLEDnoticeCkBox2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox2.setName("useLEDnoticeCkBox2"); // NOI18N
        useLEDnoticeCkBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useLEDnoticeCkBox2ItemStateChanged(evt);
            }
        });
        useLEDnoticeCkBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        ledNoticePanel2.add(useLEDnoticeCkBox2, gridBagConstraints);

        demoButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton2.setText("현재");
        demoButton2.setEnabled(false);
        demoButton2.setMaximumSize(new java.awt.Dimension(59, 30));
        demoButton2.setMinimumSize(new java.awt.Dimension(59, 30));
        demoButton2.setName("demoButton2"); // NOI18N
        demoButton2.setPreferredSize(new java.awt.Dimension(59, 30));
        demoButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel2.add(demoButton2, gridBagConstraints);

        demoFinishButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton2.setText("그만");
        demoFinishButton2.setEnabled(false);
        demoFinishButton2.setMaximumSize(new java.awt.Dimension(59, 30));
        demoFinishButton2.setMinimumSize(new java.awt.Dimension(59, 30));
        demoFinishButton2.setName("demoFinishButton2"); // NOI18N
        demoFinishButton2.setPreferredSize(new java.awt.Dimension(59, 30));
        demoFinishButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 0);
        ledNoticePanel2.add(demoFinishButton2, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel42.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        ledNoticePanel2.add(jLabel42, gridBagConstraints);

        startEffectHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        startEffectHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        startEffectHelpButton2.setIcon(getQuest20_Icon());
        startEffectHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        startEffectHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton2.setOpaque(false);
        startEffectHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startEffectHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel2.add(startEffectHelpButton2, gridBagConstraints);

        demoCurrHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        demoCurrHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoCurrHelpButton2.setIcon(getQuest20_Icon());
        demoCurrHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoCurrHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton2.setOpaque(false);
        demoCurrHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoCurrHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel2.add(demoCurrHelpButton2, gridBagConstraints);

        demoAllHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        demoAllHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoAllHelpButton2.setIcon(getQuest20_Icon());
        demoAllHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoAllHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton2.setOpaque(false);
        demoAllHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel2.add(demoAllHelpButton2, gridBagConstraints);

        endEffectHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        endEffectHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        endEffectHelpButton2.setIcon(getQuest20_Icon());
        endEffectHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        endEffectHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton2.setOpaque(false);
        endEffectHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endEffectHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel2.add(endEffectHelpButton2, gridBagConstraints);

        demoAllButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton2.setText("전체");
        demoAllButton2.setMaximumSize(new java.awt.Dimension(59, 30));
        demoAllButton2.setMinimumSize(new java.awt.Dimension(59, 30));
        demoAllButton2.setName("demoAllButton2"); // NOI18N
        demoAllButton2.setPreferredSize(new java.awt.Dimension(59, 30));
        demoAllButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel2.add(demoAllButton2, gridBagConstraints);

        pauseTimeHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        pauseTimeHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        pauseTimeHelpButton2.setIcon(getQuest20_Icon());
        pauseTimeHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pauseTimeHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton2.setOpaque(false);
        pauseTimeHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseTimeHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel2.add(pauseTimeHelpButton2, gridBagConstraints);

        useCkBoxHelpButton2.setBackground(new java.awt.Color(153, 255, 153));
        useCkBoxHelpButton2.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        useCkBoxHelpButton2.setIcon(getQuest20_Icon());
        useCkBoxHelpButton2.setMargin(new java.awt.Insets(2, 4, 2, 4));
        useCkBoxHelpButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton2.setOpaque(false);
        useCkBoxHelpButton2.setPreferredSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCkBoxHelpButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        ledNoticePanel2.add(useCkBoxHelpButton2, gridBagConstraints);

        ledNoticePanelVehicle.addTab(TOP_TAB_TITLE.getContent(), ledNoticePanel2);

        ledNoticePanel3.setName("eBoard" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.getVal());
        ledNoticePanel3.setLayout(new java.awt.GridBagLayout());

        label_MSG3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG3.setText("문자열");
        label_MSG3.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel3.add(label_MSG3, gridBagConstraints);

        label_Color3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color3.setText("중간멈춤");
        label_Color3.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel3.add(label_Color3, gridBagConstraints);

        label_Font3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font3.setText("폰트");
        label_Font3.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font3.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel3.add(label_Font3, gridBagConstraints);

        label_ContentType3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType3.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 5);
        ledNoticePanel3.add(label_ContentType3, gridBagConstraints);

        contentTypeBox3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox3.setMinimumSize(new java.awt.Dimension(123, 30));
        contentTypeBox3.setName("contentTypeBox3"); // NOI18N
        contentTypeBox3.setPreferredSize(new java.awt.Dimension(123, 30));
        contentTypeBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        ledNoticePanel3.add(contentTypeBox3, gridBagConstraints);

        tf_VerbatimContent3.setColumns(23);
        tf_VerbatimContent3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent3.setMinimumSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.setName("tf_VerbatimContent3"); // NOI18N
        tf_VerbatimContent3.setPreferredSize(new Dimension(250, TEXT_FIELD_HEIGHT));
        tf_VerbatimContent3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tf_VerbatimContent3KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        ledNoticePanel3.add(tf_VerbatimContent3, gridBagConstraints);

        charColor3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor3.setMinimumSize(new java.awt.Dimension(70, 30));
        charColor3.setName("charColor3"); // NOI18N
        charColor3.setPreferredSize(new java.awt.Dimension(70, 30));
        charColor3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charColor3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel3.add(charColor3, gridBagConstraints);

        charFont3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont3.setMinimumSize(new java.awt.Dimension(70, 30));
        charFont3.setName("charFont3"); // NOI18N
        charFont3.setPreferredSize(new java.awt.Dimension(70, 30));
        charFont3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charFont3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel3.add(charFont3, gridBagConstraints);

        combo_StartEffect3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect3.setEnabled(false);
        combo_StartEffect3.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_StartEffect3.setName("combo_StartEffect3"); // NOI18N
        combo_StartEffect3.setPreferredSize(new java.awt.Dimension(123, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel3.add(combo_StartEffect3, gridBagConstraints);

        combo_FinishEffect3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect3.setEnabled(false);
        combo_FinishEffect3.setMinimumSize(new java.awt.Dimension(123, 30));
        combo_FinishEffect3.setName("combo_FinishEffect3"); // NOI18N
        combo_FinishEffect3.setPreferredSize(new java.awt.Dimension(123, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        ledNoticePanel3.add(combo_FinishEffect3, gridBagConstraints);

        label_Color26.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color26.setText("색상");
        label_Color26.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color26.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 5);
        ledNoticePanel3.add(label_Color26, gridBagConstraints);

        label_Color27.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color27.setText("마침효과");
        label_Color27.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 5);
        ledNoticePanel3.add(label_Color27, gridBagConstraints);

        label_Color28.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color28.setText("시작효과");
        label_Color28.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        ledNoticePanel3.add(label_Color28, gridBagConstraints);

        midStopPanel3.setMaximumSize(new java.awt.Dimension(95, 30));
        midStopPanel3.setPreferredSize(new java.awt.Dimension(100, 30));
        midStopPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        combo_PauseTime5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime5.setEnabled(false);
        combo_PauseTime5.setMaximumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime5.setMinimumSize(new java.awt.Dimension(70, 30));
        combo_PauseTime5.setName("combo_PauseTime3"); // NOI18N
        combo_PauseTime5.setPreferredSize(new java.awt.Dimension(70, 30));
        midStopPanel3.add(combo_PauseTime5);

        label_Color29.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_Color29.setText("초");
        label_Color29.setMaximumSize(new java.awt.Dimension(25, 15));
        label_Color29.setMinimumSize(new java.awt.Dimension(25, 15));
        label_Color29.setPreferredSize(new java.awt.Dimension(25, 15));
        midStopPanel3.add(label_Color29);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        ledNoticePanel3.add(midStopPanel3, gridBagConstraints);

        useLEDnoticeCkBox3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox3.setText("사용");
        useLEDnoticeCkBox3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox3.setName("useLEDnoticeCkBox3"); // NOI18N
        useLEDnoticeCkBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useLEDnoticeCkBox3ItemStateChanged(evt);
            }
        });
        useLEDnoticeCkBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 5);
        ledNoticePanel3.add(useLEDnoticeCkBox3, gridBagConstraints);

        demoButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton3.setText("현재");
        demoButton3.setEnabled(false);
        demoButton3.setMaximumSize(new java.awt.Dimension(59, 30));
        demoButton3.setMinimumSize(new java.awt.Dimension(59, 30));
        demoButton3.setName("demoButton3"); // NOI18N
        demoButton3.setPreferredSize(new java.awt.Dimension(59, 30));
        demoButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel3.add(demoButton3, gridBagConstraints);

        demoFinishButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton3.setText("그만");
        demoFinishButton3.setEnabled(false);
        demoFinishButton3.setMaximumSize(new java.awt.Dimension(59, 30));
        demoFinishButton3.setMinimumSize(new java.awt.Dimension(59, 30));
        demoFinishButton3.setName("demoFinishButton3"); // NOI18N
        demoFinishButton3.setPreferredSize(new java.awt.Dimension(59, 30));
        demoFinishButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 10, 0);
        ledNoticePanel3.add(demoFinishButton3, gridBagConstraints);

        jLabel46.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel46.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        ledNoticePanel3.add(jLabel46, gridBagConstraints);

        startEffectHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        startEffectHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        startEffectHelpButton3.setIcon(getQuest20_Icon());
        startEffectHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        startEffectHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton3.setOpaque(false);
        startEffectHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        startEffectHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startEffectHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel3.add(startEffectHelpButton3, gridBagConstraints);

        demoCurrHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        demoCurrHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoCurrHelpButton3.setIcon(getQuest20_Icon());
        demoCurrHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoCurrHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton3.setOpaque(false);
        demoCurrHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        demoCurrHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoCurrHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel3.add(demoCurrHelpButton3, gridBagConstraints);

        demoAllHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        demoAllHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        demoAllHelpButton3.setIcon(getQuest20_Icon());
        demoAllHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        demoAllHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton3.setOpaque(false);
        demoAllHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        demoAllHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        ledNoticePanel3.add(demoAllHelpButton3, gridBagConstraints);

        endEffectHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        endEffectHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        endEffectHelpButton3.setIcon(getQuest20_Icon());
        endEffectHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        endEffectHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton3.setOpaque(false);
        endEffectHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endEffectHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel3.add(endEffectHelpButton3, gridBagConstraints);

        demoAllButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton3.setText("전체");
        demoAllButton3.setMaximumSize(new java.awt.Dimension(59, 30));
        demoAllButton3.setMinimumSize(new java.awt.Dimension(59, 30));
        demoAllButton3.setName("demoAllButton3"); // NOI18N
        demoAllButton3.setPreferredSize(new java.awt.Dimension(59, 30));
        demoAllButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 0);
        ledNoticePanel3.add(demoAllButton3, gridBagConstraints);

        pauseTimeHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        pauseTimeHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        pauseTimeHelpButton3.setIcon(getQuest20_Icon());
        pauseTimeHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pauseTimeHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton3.setOpaque(false);
        pauseTimeHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseTimeHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        ledNoticePanel3.add(pauseTimeHelpButton3, gridBagConstraints);

        useCkBoxHelpButton3.setBackground(new java.awt.Color(153, 255, 153));
        useCkBoxHelpButton3.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        useCkBoxHelpButton3.setIcon(getQuest20_Icon());
        useCkBoxHelpButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        useCkBoxHelpButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton3.setOpaque(false);
        useCkBoxHelpButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        useCkBoxHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCkBoxHelpButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        ledNoticePanel3.add(useCkBoxHelpButton3, gridBagConstraints);

        ledNoticePanelVehicle.addTab(BOTTOM_TAB_TITLE.getContent(), ledNoticePanel3);

        ledNoticeTabbedPane.addTab(VEHICLE_TAB_TITLE.getContent(), ledNoticePanelVehicle);

        centerPanel.add(ledNoticeTabbedPane);
        centerPanel.add(filler1);

        buttonPanel.setMinimumSize(new java.awt.Dimension(0, 40));

        btn_Save.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save.setMnemonic('s');
        btn_Save.setText("저장(S)");
        btn_Save.setEnabled(false);
        btn_Save.setInheritsPopupMenu(true);
        btn_Save.setMaximumSize(new java.awt.Dimension(73, 35));
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
        btn_Cancel.setText("취소(C)");
        btn_Cancel.setEnabled(false);
        btn_Cancel.setMaximumSize(new java.awt.Dimension(85, 35));
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
        btn_Exit.setText("닫기(C)");
        btn_Exit.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Exit.setMinimumSize(new java.awt.Dimension(90, 40));
        btn_Exit.setPreferredSize(new java.awt.Dimension(90, 40));
        btn_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExitActionPerformed(evt);
            }
        });

        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        myMetaKeyLabel.setMaximumSize(new java.awt.Dimension(80, 25));
        myMetaKeyLabel.setMinimumSize(new java.awt.Dimension(80, 25));
        myMetaKeyLabel.setName(""); // NOI18N
        myMetaKeyLabel.setPreferredSize(new java.awt.Dimension(80, 25));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(btn_Save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(myMetaKeyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn_Save, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btn_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myMetaKeyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        centerPanel.add(buttonPanel);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(545, 40));

        javax.swing.GroupLayout southPanelLayout = new javax.swing.GroupLayout(southPanel);
        southPanel.setLayout(southPanelLayout);
        southPanelLayout.setHorizontalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 654, Short.MAX_VALUE)
        );
        southPanelLayout.setVerticalGroup(
            southPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void useLEDnoticeCkBox0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox0ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!useLEDnoticeCkBox0.isSelected()) {
                    useLEDnoticeCkBox1.setSelected(false);
                }
                changeOtherComponentEnabled(DEFAULT_TOP_ROW, useLEDnoticeCkBox0.isSelected());
            }
        }); 
    }//GEN-LAST:event_useLEDnoticeCkBox0ActionPerformed

    private void demoButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton0ActionPerformed
        demoCurrentSetting(DEFAULT_TOP_ROW);
    }//GEN-LAST:event_demoButton0ActionPerformed

    private void demoFinishButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton0ActionPerformed
        finishAllEffectDemo(0);
        enableFinishButton(0, false);                
    }//GEN-LAST:event_demoFinishButton0ActionPerformed

    private void startEffectHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton0ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton0ActionPerformed

    private void demoCurrHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this, 
                "현재 설정 상태가 지속적으로" + System.lineSeparator()
                        + "시연되므로, 관찰이 끝나면, 아래" + System.lineSeparator()
                        + "[그만]버튼을 사용하여 중단 할 것!");
    }//GEN-LAST:event_demoCurrHelpButton0ActionPerformed

    private void demoAllHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton0ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton0ActionPerformed

    private void endEffectHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this, "[" + BOTTOM_TAB_TITLE.getContent() + 
                "]이 사용[V] 될 경우," + System.lineSeparator() + "자동으로 '효과 없음' 설정됨");
    }//GEN-LAST:event_endEffectHelpButton0ActionPerformed

    private void demoAllButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton0ActionPerformed
        demoAllEffects(0);
    }//GEN-LAST:event_demoAllButton0ActionPerformed

    private void btn_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExitActionPerformed
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_btn_ExitActionPerformed

    private void btn_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveActionPerformed
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            saveLEDnoticeSettingsTab(usage);
        }
    }//GEN-LAST:event_btn_SaveActionPerformed

    private void btn_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CancelActionPerformed

        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            cancelModification(usage);
        }
    }//GEN-LAST:event_btn_CancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_formWindowClosing

    private void contentTypeBox0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox0ActionPerformed
//        setButtonEnabledIf_ContentTypeChanged(0);    
//        reflectSelectionToVerbatimContent(0);
    }//GEN-LAST:event_contentTypeBox0ActionPerformed

    private void pauseTimeHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this,"[" + BOTTOM_TAB_TITLE.getContent() + 
                "]이 사용[V]의 경우," + System.lineSeparator() + "자동설정 값 '1' (초) 임!");
    }//GEN-LAST:event_pauseTimeHelpButton0ActionPerformed

    private void useCkBoxHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton0ActionPerformed
        showHowToUseRows();
    }//GEN-LAST:event_useCkBoxHelpButton0ActionPerformed

    private void tf_VerbatimContent0KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0KeyReleased
        changeButtonEnabled_IfVerbatimChanged(0);
    }//GEN-LAST:event_tf_VerbatimContent0KeyReleased

    private void ledNoticePanelDefaultStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ledNoticePanelDefaultStateChanged
        if (componentMap == null)
            return;
        
        cancelDemoIfRunning();
        
        final int row = ledNoticePanelDefault.getSelectedIndex();
        int prevTabIdx = (row == 0 ? 1 : 0);
        JButton saveButton = (JButton)componentMap.get("btn_Save" + prevTabIdx);
        
//        if (saveButton != null && saveButton.isEnabled()) {
//            JOptionPane.showMessageDialog(this, 
//                    "LEDnotice 설정이 변경 중입니다.," + System.lineSeparator()
//                            + "[저장] 혹은 [취소] 중 하나를 선택하십시오!");
//            ledNoticePanelDefault.setSelectedIndex(prevTabIdx);
//        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                selectItemsForSelectedTab(row, 0); // 0 : default column
            }
        });
    }//GEN-LAST:event_ledNoticePanelDefaultStateChanged

    private void ledNoticeTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ledNoticeTabbedPaneStateChanged
        if (componentMap == null)
            return;
        
        cancelDemoIfRunning();
        
        final int column = ledNoticeTabbedPane.getSelectedIndex();
        int prevTabIdx = (column == 0 ? 1 : 0);
        JButton saveButtonTop = (JButton)componentMap.get("btn_Save" + (prevTabIdx * 2));
        JButton saveButtonBottom = (JButton)componentMap.get("btn_Save" + (prevTabIdx * 2 + 1));
        
//        if ((saveButtonTop != null && saveButtonTop.isEnabled()) || 
//                (saveButtonBottom != null && saveButtonBottom.isEnabled())) {
//            JOptionPane.showMessageDialog(this, 
//                    "LEDnotice 설정이 변경 중입니다.," + System.lineSeparator()
//                            + "[저장] 혹은 [취소] 중 하나를 선택하십시오!");
//            ledNoticeTabbedPane.setSelectedIndex(prevTabIdx);
//        }
        final int row = ((JTabbedPane)ledNoticeTabbedPane.getSelectedComponent()).getSelectedIndex();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                selectItemsForSelectedTab(row, column);
            }
        });
    }//GEN-LAST:event_ledNoticeTabbedPaneStateChanged

    private void ledNoticePanelVehicleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ledNoticePanelVehicleStateChanged
        if (componentMap == null)
            return;
        
        cancelDemoIfRunning();
        
        final int row = ledNoticePanelVehicle.getSelectedIndex();
        int prevTabIdx = (row == 0 ? 1 : 0);
        JButton saveButton = (JButton)componentMap.get("btn_Save" + (2 + prevTabIdx));
        
        if (saveButton != null && saveButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, 
                    "LEDnotice 설정이 변경 중입니다.," + System.lineSeparator()
                            + "[저장] 혹은 [취소] 중 하나를 선택하십시오!");
            ledNoticePanelVehicle.setSelectedIndex(prevTabIdx);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                selectItemsForSelectedTab(row, 1); // 1 : vehicle column
            }
        });        
    }//GEN-LAST:event_ledNoticePanelVehicleStateChanged

    private void combo_StartEffect0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_StartEffect0ActionPerformed
        changeButtonEnabled_IfStartEffectChanged(0);
    }//GEN-LAST:event_combo_StartEffect0ActionPerformed

    private void charColor0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charColor0ActionPerformed
        changeButtonEnabled_IfColorChanged(0);        
    }//GEN-LAST:event_charColor0ActionPerformed

    private void combo_PauseTime0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_PauseTime0ActionPerformed
        changeButtonEnabled_IfPauseTimeChanged(0);
    }//GEN-LAST:event_combo_PauseTime0ActionPerformed

    private void charFont0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charFont0ActionPerformed
        changeButtonEnabled_IfFontChanged(0);
    }//GEN-LAST:event_charFont0ActionPerformed

    private void combo_FinishEffect0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_FinishEffect0ActionPerformed
        changeButtonEnabled_IfFinishEffectChanged(0);
    }//GEN-LAST:event_combo_FinishEffect0ActionPerformed

    private void contentTypeBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox1ActionPerformed
        setButtonEnabledIf_ContentTypeChanged(1);    
        reflectSelectionToVerbatimContent(1);

    }//GEN-LAST:event_contentTypeBox1ActionPerformed

    private void tf_VerbatimContent1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1KeyReleased
        changeButtonEnabled_IfVerbatimChanged(1);
    }//GEN-LAST:event_tf_VerbatimContent1KeyReleased

    private void charColor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charColor1ActionPerformed
        changeButtonEnabled_IfColorChanged(1);
    }//GEN-LAST:event_charColor1ActionPerformed

    private void charFont1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charFont1ActionPerformed
        changeButtonEnabled_IfFontChanged(1);
    }//GEN-LAST:event_charFont1ActionPerformed

    private void combo_StartEffect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_StartEffect1ActionPerformed
        changeButtonEnabled_IfStartEffectChanged(1);
    }//GEN-LAST:event_combo_StartEffect1ActionPerformed

    private void combo_FinishEffect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_FinishEffect1ActionPerformed
        changeButtonEnabled_IfFinishEffectChanged(1);
    }//GEN-LAST:event_combo_FinishEffect1ActionPerformed

    private void combo_PauseTime1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_PauseTime1ActionPerformed
        changeButtonEnabled_IfPauseTimeChanged(1);
    }//GEN-LAST:event_combo_PauseTime1ActionPerformed

    private void useLEDnoticeCkBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox1ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (useLEDnoticeCkBox1.isSelected()) {
                    useLEDnoticeCkBox0.setSelected(true);
                }      
            }
        });  
    }//GEN-LAST:event_useLEDnoticeCkBox1ActionPerformed

    private void demoButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton1ActionPerformed
        demoCurrentSetting(DEFAULT_BOTTOM_ROW);
//        enableFinishButton(1, true);
    }//GEN-LAST:event_demoButton1ActionPerformed

    private void demoFinishButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton1ActionPerformed
        finishAllEffectDemo(1);
        enableFinishButton(1, false);
    }//GEN-LAST:event_demoFinishButton1ActionPerformed

    private void startEffectHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton1ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton1ActionPerformed

    private void demoCurrHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton1ActionPerformed
        current2ndRowDemoHelp();
    }//GEN-LAST:event_demoCurrHelpButton1ActionPerformed

    private void demoAllHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton1ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton1ActionPerformed

    private void demoAllButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton1ActionPerformed
        demoAllEffects(1);
    }//GEN-LAST:event_demoAllButton1ActionPerformed

    private void useCkBoxHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton1ActionPerformed
        String rowName = TOP_TAB_TITLE.getContent();
        JOptionPane.showMessageDialog(this,
            "사용[V]되는 경우, 아래 같이 " + rowName + " 설정이 강제 변경됨!" + System.lineSeparator() + 
                "    - " + rowName + " 사용[V] 설정하며," + System.lineSeparator() + 
                "    - " + rowName + " '중간 멈춤' 1초로 변경하며," + System.lineSeparator() + 
                "    - " + rowName + " '마침 효과'를 '효과 없음'으로 변환함.");
    }//GEN-LAST:event_useCkBoxHelpButton1ActionPerformed

    private void useLEDnoticeCkBox0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox0ItemStateChanged
        setButtonEnabledIf_UseCkBoxChanged(0);
    }//GEN-LAST:event_useLEDnoticeCkBox0ItemStateChanged

    private void useLEDnoticeCkBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox1ItemStateChanged
        setButtonEnabledIf_UseCkBoxChanged(1);
        changeOtherComponentEnabled(DEFAULT_BOTTOM_ROW, useLEDnoticeCkBox1.isSelected());
    }//GEN-LAST:event_useLEDnoticeCkBox1ItemStateChanged

    private void ledNoticePanel0FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ledNoticePanel0FocusGained
        System.out.println("0 gained");
    }//GEN-LAST:event_ledNoticePanel0FocusGained

    private void ledNoticePanel0ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ledNoticePanel0ComponentShown
        System.out.println("0 shown");
        changeDependantPropEnabled();
    }//GEN-LAST:event_ledNoticePanel0ComponentShown

    private void contentTypeBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contentTypeBox2ActionPerformed

    private void tf_VerbatimContent2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_VerbatimContent2KeyReleased

    private void charColor2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charColor2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_charColor2ActionPerformed

    private void charFont2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charFont2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_charFont2ActionPerformed

    private void combo_StartEffect2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_StartEffect2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_StartEffect2ActionPerformed

    private void combo_FinishEffect2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_FinishEffect2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_FinishEffect2ActionPerformed

    private void combo_PauseTime2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_PauseTime2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_PauseTime2ActionPerformed

    private void useLEDnoticeCkBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox2ItemStateChanged
        setButtonEnabledIf_UseCkBoxChanged(2);
        changeOtherComponentEnabled(CAR_ENTRY_TOP_ROW, useLEDnoticeCkBox2.isSelected());
    }//GEN-LAST:event_useLEDnoticeCkBox2ItemStateChanged

    private void useLEDnoticeCkBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox2ActionPerformed
        if (!useLEDnoticeCkBox2.isSelected()) {
            useLEDnoticeCkBox3.setSelected(false);
        }
    }//GEN-LAST:event_useLEDnoticeCkBox2ActionPerformed

    private void demoButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton2ActionPerformed
        demoCurrentInterruptSettings();
    }//GEN-LAST:event_demoButton2ActionPerformed

    private void demoFinishButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_demoFinishButton2ActionPerformed

    private void startEffectHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton2ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton2ActionPerformed

    private void demoCurrHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton2ActionPerformed
        current1stRowDemoHelp();
    }//GEN-LAST:event_demoCurrHelpButton2ActionPerformed

    private void demoAllHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_demoAllHelpButton2ActionPerformed

    private void endEffectHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton2ActionPerformed

    }//GEN-LAST:event_endEffectHelpButton2ActionPerformed

    private void demoAllButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton2ActionPerformed
        demoAllEffects(2);
    }//GEN-LAST:event_demoAllButton2ActionPerformed

    private void pauseTimeHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton2ActionPerformed
//        showOneSecondForcedHelpDialog();
    }//GEN-LAST:event_pauseTimeHelpButton2ActionPerformed

    private void useCkBoxHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton2ActionPerformed
        showHowToUseRows();
    }//GEN-LAST:event_useCkBoxHelpButton2ActionPerformed

    private void contentTypeBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contentTypeBox3ActionPerformed

    private void tf_VerbatimContent3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_VerbatimContent3KeyReleased

    private void charColor3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charColor3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_charColor3ActionPerformed

    private void charFont3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charFont3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_charFont3ActionPerformed

    private void useLEDnoticeCkBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox3ItemStateChanged
        setButtonEnabledIf_UseCkBoxChanged(3);
        changeOtherComponentEnabled(CAR_ENTRY_BOTTOM_ROW, useLEDnoticeCkBox3.isSelected());
    }//GEN-LAST:event_useLEDnoticeCkBox3ItemStateChanged

    private void useLEDnoticeCkBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox3ActionPerformed
//        if (useLEDnoticeCkBox3.isSelected()) {
//            useLEDnoticeCkBox2.setSelected(true);
//        } 
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (useLEDnoticeCkBox3.isSelected()) {
                    useLEDnoticeCkBox2.setSelected(true);
                }      
            }
        });          
    }//GEN-LAST:event_useLEDnoticeCkBox3ActionPerformed

    private void demoButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton3ActionPerformed
        demoCurrentInterruptSettings();
    }//GEN-LAST:event_demoButton3ActionPerformed

    private void demoFinishButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_demoFinishButton3ActionPerformed

    private void startEffectHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton3ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_startEffectHelpButton3ActionPerformed

    private void demoCurrHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton3ActionPerformed
        current2ndRowDemoHelp();
    }//GEN-LAST:event_demoCurrHelpButton3ActionPerformed

    private void demoAllHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton3ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton3ActionPerformed

    private void endEffectHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton3ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_endEffectHelpButton3ActionPerformed

    private void demoAllButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_demoAllButton3ActionPerformed

    private void pauseTimeHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton3ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_pauseTimeHelpButton3ActionPerformed

    private void useCkBoxHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useCkBoxHelpButton3ActionPerformed

    private void contentTypeBox0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_contentTypeBox0ItemStateChanged
        setButtonEnabledIf_ContentTypeChanged(0);    
        reflectSelectionToVerbatimContent(0);
    }//GEN-LAST:event_contentTypeBox0ItemStateChanged

    /**
     *  Decide whether to use the verbatim text field after checking the content type.
     */
    public void reflectSelectionToVerbatimContent(int usage){
        JTextField txtField = ((JTextField) getComponentByName("tf_VerbatimContent" + usage));
        
        if (((JComboBox)getComponentByName("contentTypeBox" + usage)).getSelectedIndex() 
                == getVerbatimIndex(usage))
        {
            txtField.setEnabled(true);
            txtField.setText(ledNoticeSettings[usage].verbatimContent);
        }
        else
        {
            txtField.setEnabled(false);
            txtField.setText("");
        }
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Cancel;
    private javax.swing.JButton btn_Exit;
    private javax.swing.JButton btn_Save;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox charColor0;
    private javax.swing.JComboBox charColor1;
    private javax.swing.JComboBox charColor2;
    private javax.swing.JComboBox charColor3;
    private javax.swing.JComboBox charFont0;
    private javax.swing.JComboBox charFont1;
    private javax.swing.JComboBox charFont2;
    private javax.swing.JComboBox charFont3;
    private javax.swing.JComboBox combo_FinishEffect0;
    private javax.swing.JComboBox combo_FinishEffect1;
    private javax.swing.JComboBox combo_FinishEffect2;
    private javax.swing.JComboBox combo_FinishEffect3;
    private javax.swing.JComboBox combo_PauseTime0;
    private javax.swing.JComboBox combo_PauseTime1;
    private javax.swing.JComboBox combo_PauseTime2;
    private javax.swing.JComboBox combo_PauseTime5;
    private javax.swing.JComboBox combo_StartEffect0;
    private javax.swing.JComboBox combo_StartEffect1;
    private javax.swing.JComboBox combo_StartEffect2;
    private javax.swing.JComboBox combo_StartEffect3;
    private javax.swing.JComboBox contentTypeBox0;
    private javax.swing.JComboBox contentTypeBox1;
    private javax.swing.JComboBox contentTypeBox2;
    private javax.swing.JComboBox contentTypeBox3;
    private javax.swing.JButton demoAllButton0;
    private javax.swing.JButton demoAllButton1;
    private javax.swing.JButton demoAllButton2;
    private javax.swing.JButton demoAllButton3;
    private javax.swing.JButton demoAllHelpButton0;
    private javax.swing.JButton demoAllHelpButton1;
    private javax.swing.JButton demoAllHelpButton2;
    private javax.swing.JButton demoAllHelpButton3;
    private javax.swing.JButton demoButton0;
    private javax.swing.JButton demoButton1;
    private javax.swing.JButton demoButton2;
    private javax.swing.JButton demoButton3;
    private javax.swing.JButton demoCurrHelpButton0;
    private javax.swing.JButton demoCurrHelpButton1;
    private javax.swing.JButton demoCurrHelpButton2;
    private javax.swing.JButton demoCurrHelpButton3;
    private javax.swing.JButton demoFinishButton0;
    private javax.swing.JButton demoFinishButton1;
    private javax.swing.JButton demoFinishButton2;
    private javax.swing.JButton demoFinishButton3;
    private javax.swing.JButton endEffectHelpButton0;
    private javax.swing.JButton endEffectHelpButton2;
    private javax.swing.JButton endEffectHelpButton3;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label_Color0;
    private javax.swing.JLabel label_Color1;
    private javax.swing.JLabel label_Color10;
    private javax.swing.JLabel label_Color11;
    private javax.swing.JLabel label_Color2;
    private javax.swing.JLabel label_Color22;
    private javax.swing.JLabel label_Color23;
    private javax.swing.JLabel label_Color24;
    private javax.swing.JLabel label_Color25;
    private javax.swing.JLabel label_Color26;
    private javax.swing.JLabel label_Color27;
    private javax.swing.JLabel label_Color28;
    private javax.swing.JLabel label_Color29;
    private javax.swing.JLabel label_Color3;
    private javax.swing.JLabel label_Color4;
    private javax.swing.JLabel label_Color5;
    private javax.swing.JLabel label_Color6;
    private javax.swing.JLabel label_Color7;
    private javax.swing.JLabel label_Color8;
    private javax.swing.JLabel label_Color9;
    private javax.swing.JLabel label_ContentType0;
    private javax.swing.JLabel label_ContentType1;
    private javax.swing.JLabel label_ContentType2;
    private javax.swing.JLabel label_ContentType3;
    private javax.swing.JLabel label_Font0;
    private javax.swing.JLabel label_Font1;
    private javax.swing.JLabel label_Font2;
    private javax.swing.JLabel label_Font3;
    private javax.swing.JLabel label_MSG0;
    private javax.swing.JLabel label_MSG1;
    private javax.swing.JLabel label_MSG2;
    private javax.swing.JLabel label_MSG3;
    private javax.swing.JPanel ledNoticePanel0;
    private javax.swing.JPanel ledNoticePanel1;
    private javax.swing.JPanel ledNoticePanel2;
    private javax.swing.JPanel ledNoticePanel3;
    private javax.swing.JTabbedPane ledNoticePanelDefault;
    private javax.swing.JTabbedPane ledNoticePanelVehicle;
    private javax.swing.JLabel ledNoticeSettingsGUI_title;
    private javax.swing.JTabbedPane ledNoticeTabbedPane;
    private javax.swing.Box.Filler leftFiller;
    private javax.swing.JPanel midStopPanel0;
    private javax.swing.JPanel midStopPanel1;
    private javax.swing.JPanel midStopPanel2;
    private javax.swing.JPanel midStopPanel3;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JButton pauseTimeHelpButton0;
    private javax.swing.JButton pauseTimeHelpButton2;
    private javax.swing.JButton pauseTimeHelpButton3;
    private javax.swing.Box.Filler rightFiller;
    private javax.swing.JPanel southPanel;
    private javax.swing.JButton startEffectHelpButton0;
    private javax.swing.JButton startEffectHelpButton1;
    private javax.swing.JButton startEffectHelpButton2;
    private javax.swing.JButton startEffectHelpButton3;
    private javax.swing.JTextField tf_VerbatimContent0;
    private javax.swing.JTextField tf_VerbatimContent1;
    private javax.swing.JTextField tf_VerbatimContent2;
    private javax.swing.JTextField tf_VerbatimContent3;
    private javax.swing.JPanel titlePanel;
    private javax.swing.Box.Filler topFiller;
    private javax.swing.JButton useCkBoxHelpButton0;
    private javax.swing.JButton useCkBoxHelpButton1;
    private javax.swing.JButton useCkBoxHelpButton2;
    private javax.swing.JButton useCkBoxHelpButton3;
    private javax.swing.JCheckBox useLEDnoticeCkBox0;
    private javax.swing.JCheckBox useLEDnoticeCkBox1;
    private javax.swing.JCheckBox useLEDnoticeCkBox2;
    private javax.swing.JCheckBox useLEDnoticeCkBox3;
    // End of variables declaration//GEN-END:variables

    private int findGateNoUsingLEDnotice() {
        int index = -1;
        
        for (int gateNo = 1; gateNo <= gateCount; gateNo++) {
            if (Globals.gateDeviceTypes[gateNo].eBoardType == OSP_enums.E_BoardType.LEDnotice) {
                index = gateNo;
            }
        }
        return index;
    }    
    
    private void checkLEDnoticeRowUsageChangeAndChangeButtonEnabled(EBD_DisplayUsage usage ) {
        JCheckBox useChkBox = (JCheckBox)componentMap.get("useLEDnoticeCkBox" + usage.ordinal());
        
        // 전역변수 값과 현재 설정되고 있는 값을 비교!
        if (LEDnoticeManager.ledNoticeSettings[usage.ordinal()].isUsed && !useChkBox.isSelected() ||
                !LEDnoticeManager.ledNoticeSettings[usage.ordinal()].isUsed && useChkBox.isSelected())
        {
            changedControls.add(useChkBox);            
        } else {
            changedControls.remove(useChkBox);            
        }    
    }    

    private void tryToCloseEBDSettingsForm() {
        if (formMode == FormMode.UpdateMode) {
            JOptionPane.showMessageDialog(this, 
                    "E-Board settings is being modified," + System.lineSeparator()
                            + "Either [Save] or [Cancel] current changes!"); 
        } else {
            cancelDemoIfRunning();
            if (parent == null) {
                if (connectionType[E_Board.ordinal()][gateNo] == TCP_IP.ordinal()) {
                    ((ISocket) mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo]).setSocket(null);
                } else {
                    ((ISerial)mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo]).setSerialPort(null);
                }
                mainForm.stopRunningTheProgram();

                manager.interrupt();
                mainForm.dispose();                
            } else {
//                parent.setEBDsettings(null);
                parent.disposeEBoardDialog();
            }
            this.dispose();    
        }    
    }

    private void forcedChangeOfStartEffectIfNeeded(int usage) {
        
        JComboBox cBox;
        
        if (usage == EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal()) {
            cBox = (JComboBox) componentMap.get("combo_StartEffect" + CAR_ENTRY_TOP_ROW.ordinal());
        } else {
            cBox = (JComboBox) componentMap.get("combo_StartEffect" + usage);
        }
        // 길이가 길면 좌로 흐름으로 설정하고 알림창을 띄운다
        // 그리고 항목 선택도 자동 변경한다.
        // 만일 값이 변경된 경우 Save 버튼을 활성화 한다.
        
        JTextField contentField = (JTextField)componentMap.get("tf_VerbatimContent" + usage);
        JTextField carBtmRowField = (JTextField)componentMap.get("tf_VerbatimContent" + 
                EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        
        boolean longContent = (
                  ((getViewWidth(contentField.getText().trim()) > LedProtocol.LED_COLUMN_CNT * 2) && 
                  usage != CAR_ENTRY_TOP_ROW.ordinal())
                ||
                ((getViewWidth(contentField.getText().trim()) > LedProtocol.LED_COLUMN_CNT * 2) ||
                  (getViewWidth(carBtmRowField.getText().trim()) > LedProtocol.LED_COLUMN_CNT * 2)) && 
                  usage == CAR_ENTRY_TOP_ROW.ordinal()
                );
        
        if (longContent) {
            EffectType type = EffectType.values()[cBox.getSelectedIndex() + 1];
            
            if ( !LedProtocol.isLtoRFlowType(type) && !LedProtocol.isRtoLFlowType(type)) {
                JOptionPane.showMessageDialog(this, "문자열이 한글 " + LedProtocol.LED_COLUMN_CNT + 
                        "(영숫자 " + LedProtocol.LED_COLUMN_CNT * 2 + ") 자 이상이고," +
                    System.lineSeparator() + "시작 효과가 '--흐름--' 종류가 아니므로," +
                    System.lineSeparator() + "'좌로 흐름'으로 자동 설정되었음!");
                cBox.setSelectedIndex(EffectType.FLOW_RtoL.ordinal() - 1);
            }
        }    
    }

    private void changeOtherComponentEnabled(EBD_DisplayUsage usage, boolean selected) {
        JComboBox contTypeCBox = (JComboBox)componentMap.get("contentTypeBox" + usage.ordinal());
        
        contTypeCBox.setEnabled(selected);
        String typeName = (String)contTypeCBox.getSelectedItem();
        JTextField verbatimField = (JTextField)
                componentMap.get("tf_VerbatimContent" + usage.ordinal());
        
        if (selected && typeName.equals(Verbatim.getLabel())) {
            verbatimField.setEnabled(true);
        } else {
            verbatimField.setEnabled(false);
        }
        
        ((JComboBox)componentMap.get("charColor" + usage.ordinal())).setEnabled(selected);
        ((JComboBox)componentMap.get("charFont" + usage.ordinal())).setEnabled(selected);
        
        if (usage == CAR_ENTRY_BOTTOM_ROW)
            return; 
        
        ((JComboBox)componentMap.get("combo_StartEffect" + usage.ordinal())).setEnabled(selected);
        ((JComboBox)componentMap.get("combo_PauseTime" + usage.ordinal())).setEnabled(selected);
        
        if (usage == DEFAULT_TOP_ROW) {
            changeDependantPropEnabled();
        } else {
            ((JComboBox)componentMap.get("combo_FinishEffect" + usage.ordinal())).setEnabled(selected);
        }
        System.out.println("name: " + ("demoButton" + usage.ordinal()));
        ((JButton)componentMap.get("demoButton" + usage.ordinal())).setEnabled(selected);
    }

    private void showPopUpForDemoAllHelpButton() {
        int count = EffectType.values().length;
        JOptionPane.showMessageDialog(this, "시작효과, 중간멈춤, 색상 및 효과를" + System.lineSeparator() +
                "적용하여 총 '" + count + "' 개의 효과를" + System.lineSeparator()
            + "효과명을 사용하여 상단 행에 시연." + System.lineSeparator()
            + "[그만] 버튼 사용으로 시연 종료!");    
    }

    private void checkContentTypeBoxAndEnableButtons(final int usage) {
        JComboBox cBox = (JComboBox)componentMap.get("contentTypeBox" + usage);
        
        if (ledNoticeSettings[usage].contentTypeIdx == cBox.getSelectedIndex()) {
            changedControls.remove(cBox);            
        } else {
            changedControls.add(cBox);            
        }
    }

    private void changeEnabled_of_SaveCancelButtons(boolean onOff) {
//        JButton buton = (JButton) componentMap.get("btn_Save");
        btn_Save.setEnabled(onOff);
        
//        buton = (JButton) componentMap.get("btn_Cancel");
        btn_Cancel.setEnabled(onOff);
        
        btn_Exit.setEnabled(!onOff);
    }

    private void changeButtonEnabled_IfVerbatimChanged(int usage) {
        JTextField verbatimContent = (JTextField) getComponentByName("tf_VerbatimContent" + usage);
        String content = verbatimContent.getText().trim();
        
        if (content.equals(ledNoticeSettings[usage].verbatimContent)) {
            changedControls.remove(verbatimContent);            
        } else {
            changedControls.add(verbatimContent);            
        }        
    }

    private void selectItemsForSelectedTab(EBD_DisplayUsage usage) {
        int ordinal = usage.ordinal();

        changeOtherComponentEnabled(usage, ledNoticeSettings[ordinal].isUsed);

        JComboBox cmboBox = (JComboBox)componentMap.get("contentTypeBox" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].contentTypeIdx);

        JTextField textField = (JTextField)componentMap.get("tf_VerbatimContent" + ordinal);
        textField.setText(ledNoticeSettings[ordinal].verbatimContent);

        cmboBox = (JComboBox)componentMap.get("combo_StartEffect" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].startEffectIdx);

        cmboBox = (JComboBox)componentMap.get("combo_PauseTime" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].pauseTimeIdx);

        cmboBox = (JComboBox)componentMap.get("combo_FinishEffect" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].finishEffectIdx);

        cmboBox = (JComboBox)componentMap.get("charColor" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].colorIdx);

        cmboBox = (JComboBox)componentMap.get("charFont" + ordinal);
        cmboBox.setSelectedIndex(ledNoticeSettings[ordinal].fontIdx);

        JCheckBox usChkBox = (JCheckBox)componentMap.get("useLEDnoticeCkBox" + ordinal);
        usChkBox.setSelected(ledNoticeSettings[ordinal].isUsed);            
    }

    private void saveLEDnoticeSettingsTab(EBD_DisplayUsage usage) {
        
        if (usage == CAR_ENTRY_BOTTOM_ROW || usage == CAR_ENTRY_TOP_ROW)
            return;
        
        int index = usage.ordinal();
        JComboBox comboBox = null;
        Connection conn = null;
        PreparedStatement updateSettings = null;
        
        // 유형이 [문구 자체]인데, 문자열이 빈 문자열이면 경고 팝업창 띄움
        JTextField txtField = (JTextField) componentMap.get("tf_VerbatimContent" + index);
        if (txtField.getText().trim().length() == 0) {
            JComboBox cmbBox = (JComboBox) componentMap.get("contentTypeBox" + index);

            if (cmbBox.getSelectedIndex() == getVerbatimIndex(index)) {
                JOptionPane.showMessageDialog(this, "[유형]이 '문구 자체'인 경우," + System.lineSeparator() +
                        "빈 [문자열]은 허용되지 않음!" );
                txtField.requestFocus();
                return; 
            }
        }
        
        forcedChangeOfStartEffectIfNeeded(index);
        
        int result = 0;
        try {
            StringBuilder sb = new StringBuilder("Update eboard_lednotice ");
            sb.append("set row_used = ?, display_type = ?, verbatim_content = ?, ");
            sb.append("  start_effect = ?, pause_time = ?, ");
            sb.append("  finish_effect = ?, text_color = ?, text_font = ? ");
            sb.append("where usage_row = ?");
            
            conn = JDBCMySQL.getConnection();
            updateSettings = conn.prepareStatement(sb.toString());

            int pIndex = 1;
            
            JCheckBox useCkBox = (JCheckBox)getComponentByName("useLEDnoticeCkBox" + index);
            int isUsed = (useCkBox.isSelected() ? 1 : 0);
            updateSettings.setInt(pIndex++, isUsed);
            
            comboBox = (JComboBox)getComponentByName("contentTypeBox" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            String verbatimStr 
                    = ((JTextField) getComponentByName("tf_VerbatimContent" + index)).getText().trim();
            updateSettings.setString(pIndex++, verbatimStr);
            
            comboBox = (JComboBox)getComponentByName("combo_StartEffect" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("combo_PauseTime" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("combo_FinishEffect" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("charColor" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("charFont" + index);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            updateSettings.setInt(pIndex++, index);
            
            result = updateSettings.executeUpdate();
             
        } catch (SQLException ex) {
            Globals.logParkingException(Level.SEVERE, ex, "while saving LEDNotice settings");  
        } finally {
            closeDBstuff(conn, updateSettings, null, "LEDNotice settings modification");
            if (result == 1) {
                // reload global ledNotice settings variable
                readLEDnoticeSettings(ledNoticeSettings);
                changeEnabled_of_SaveCancelButtons(false);
            } else {
                JOptionPane.showMessageDialog(this, "This LEDnotice settings update saving DB operation failed.",
                    "DB Update Operation Failure", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (mainForm != null) { // when settings frame invoked alone, main form is null
            if (index == DEFAULT_TOP_ROW.ordinal() || index == DEFAULT_BOTTOM_ROW.ordinal()) 
            {
                for (byte gateNo = 1; gateNo <= gateCount; gateNo++) {
                    //if (isConnected(mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo].getSocket())) {
                    if (IDevice.isConnected(mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo], E_Board, gateNo)) {
                        manager.showDefaultMessage();
                    }
                }
            }
        }
    }

    private void cancelModification(EBD_DisplayUsage usage) {
        if (usage == CAR_ENTRY_BOTTOM_ROW || usage == CAR_ENTRY_TOP_ROW)
            return;
        
        int index = usage.ordinal();
        
        changeEnabled_of_SaveCancelButtons(false);
        selectItemsForSelectedTab(usage);
//        selectItemsForSelectedTab(index % 2, index / 2);
    }

    private void changeButtonEnabled_IfStartEffectChanged(final int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_StartEffect" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].startEffectIdx) {
            changedControls.remove(comboBx);            
        } else {
            changedControls.add(comboBx);            
        }  
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                forcedChangeOfStartEffectIfNeeded(index);
            }
        });  
    }

    private void changeButtonEnabled_IfPauseTimeChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_PauseTime" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].pauseTimeIdx) {
            changedControls.remove(comboBx);            
        } else {
            changedControls.add(comboBx);            
        }        
    }

    private void changeButtonEnabled_IfFinishEffectChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_FinishEffect" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].finishEffectIdx) {
            changedControls.remove(comboBx);            
        } else {
            changedControls.add(comboBx);            
        }  
    }

    private void changeButtonEnabled_IfColorChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("charColor" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].colorIdx) {
            changedControls.remove(comboBx);            
        } else {
            changedControls.add(comboBx);            
        }  
    }

    private void changeButtonEnabled_IfFontChanged(int index) {
        
        JComboBox comboBx = (JComboBox) getComponentByName("charFont" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].fontIdx) {
            changedControls.remove(comboBx);            
        } else {
            changedControls.add(comboBx);            
        }
    }

//    private void changeButtonEnabled_IfStartEffectChanged(final int usage) {
//        changeButtonEnabled_IfStartEffectChanged(usage);
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                forcedChangeOfStartEffectIfNeeded(usage);
//            }
//        });     
//    }

    private void checkNonsenseEmptyContent(final int usage) {
        // 유형이 [문구 자체]인데, 문자열이 빈 문자열이면 경고 팝업창 띄움
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                forcedChangeOfStartEffectIfNeeded(usage);
            }
        });
    }

    private void giveStartEffectAutoAdjustmentPossibility() {
        JOptionPane.showMessageDialog(this,
            "한글 6 (영숫자 12) 자 이상이고," + System.lineSeparator() +
            "좌/우 흐름 유형이 아니면," + System.lineSeparator() + "자동으로 '좌로 흐름'이 설정됨!");    
    }

    private int getVerbatimIndex(int usage) {
        int verbatimIndex = Verbatim.ordinal();

        if (usage == EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal() || 
                usage == EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal()) 
        {
            verbatimIndex += LEDnoticeVehicleContentType.values().length;
        }    
        return verbatimIndex;
    }

    private void giveWhyCarLowRowEffectNotUsed() {
        JOptionPane.showMessageDialog(this, VEHICLE_TAB_TITLE.getContent() + "-[" 
                + BOTTOM_TAB_TITLE.getContent() + "]의 경우, " + System.lineSeparator()
            + "시작효과, 중간멈춤, 마침효과는 사용되지 않음." + System.lineSeparator()
            + "상단 행과 같이 하나의 동작으로 처리되기 때문임.");          
    }

    private LEDnoticeSettings getLEDnoticeSetting(EBD_DisplayUsage ebd_DisplayUsage) {
        return new LEDnoticeSettings(
                ((JCheckBox)componentMap.get("useLEDnoticeCkBox" + ebd_DisplayUsage.ordinal())).isSelected() 
                        ? 1 : 0,
                ((JComboBox)componentMap.get("contentTypeBox" + ebd_DisplayUsage.ordinal())).getSelectedIndex(),
                ((JTextField)componentMap.get("tf_VerbatimContent" + ebd_DisplayUsage.ordinal())).getText().trim(),
                ((JComboBox)componentMap.get("combo_StartEffect" + ebd_DisplayUsage.ordinal())).getSelectedIndex(),
                ((JComboBox)componentMap.get("combo_PauseTime" + ebd_DisplayUsage.ordinal())).getSelectedIndex(),
                ((JComboBox)componentMap.get("combo_FinishEffect" + ebd_DisplayUsage.ordinal())).getSelectedIndex(),
                ((JComboBox)componentMap.get("charColor" + ebd_DisplayUsage.ordinal())).getSelectedIndex(),
                ((JComboBox)componentMap.get("charFont" + ebd_DisplayUsage.ordinal())).getSelectedIndex()
        );
    }

    private void demoCurrentInterruptSettings() {
        LEDnoticeSettings topSetting = getLEDnoticeSetting(CAR_ENTRY_TOP_ROW);
        LEDnoticeSettings bottomSetting = getLEDnoticeSetting(CAR_ENTRY_BOTTOM_ROW);
        
        cancelDemoIfRunning();
        inDemoMode[CAR_ENTRY_TOP_ROW.ordinal()] = true;
        
        manager.sendCarArrival_interruptMessage(topSetting, bottomSetting, (byte)gateNo, "서울32가1234", 
                PermissionType.DISALLOWED, "노상주차경고", 3000);
    }

    private void setButtonEnabledIf_UseCkBoxChanged(int index) {
        JCheckBox useChkBox = (JCheckBox)componentMap.get("useLEDnoticeCkBox" + index);
        
        // Compare original selection to new selection status.
        if (LEDnoticeManager.ledNoticeSettings[index].isUsed == useChkBox.isSelected()) {
            changedControls.remove(useChkBox);            
        } else {
            changedControls.add(useChkBox);            
        }
    }

    private void setButtonEnabledIf_ContentTypeChanged(int index) {
        JComboBox cBox = (JComboBox)componentMap.get("contentTypeBox" + index);
        
        if (ledNoticeSettings[index].contentTypeIdx == cBox.getSelectedIndex()) {
            changedControls.remove(cBox);            
        } else {
            changedControls.add(cBox);            
        }
    }

    private void enableFinishButton(int index, boolean flag) {
        JButton button = (JButton)componentMap.get("demoFinishButton" + index);
        button.setEnabled(flag);
        
        button = (JButton)componentMap.get("demoButton" + index);
        button.setEnabled(!flag);
        button = (JButton)componentMap.get("demoAllButton" + index);
        button.setEnabled(!flag);
    }

    private void changeDependantPropEnabled() {
        JCheckBox usBotChkBox = (JCheckBox)componentMap.get("useLEDnoticeCkBox" + DEFAULT_BOTTOM_ROW.ordinal());

        if (usBotChkBox.isSelected()) {
            useLEDnoticeCkBox0.setSelected(true); // 상단 사용
            combo_PauseTime0.setSelectedIndex(0); // 1초 설정
            combo_FinishEffect0.setSelectedItem(EffectType.NONE.getLabel());            
            combo_PauseTime0.setEnabled(false);
            combo_FinishEffect0.setEnabled(false);
        } else {
            boolean enableThis = useLEDnoticeCkBox0.isSelected();
            combo_PauseTime0.setEnabled(enableThis);
            combo_FinishEffect0.setEnabled(enableThis);
        }
    }

    private void showHowToUseRows() {
        JOptionPane.showMessageDialog(this,
            "전광판 표시 행 사용 지침" + System.lineSeparator() + System.lineSeparator() + 
                "   - " + TOP_TAB_TITLE.getContent() + ", " +
                    BOTTOM_TAB_TITLE.getContent() + " 모두 사용하거나," + System.lineSeparator() + 
                "   - " + TOP_TAB_TITLE.getContent() + "만 사용하거나," + System.lineSeparator() + 
                "   - 두 행 모두 사용하지 아니함.");
    }

    private void current2ndRowDemoHelp() {
        JOptionPane.showMessageDialog(this, "상, 하단 연계된 설정을 시연함" + System.lineSeparator()
            + "[그만] 버튼 사용으로 시연 종료!");
    }

    private void current1stRowDemoHelp() {
        JOptionPane.showMessageDialog(this, 
                "현재 설정 상태를 시연함" + System.lineSeparator()
                        + "입차 표시 3초 후, 기본 표시로 복귀함" + System.lineSeparator()
                        + "[그만] 버튼 사용으로 시연 종료!");
    }
}