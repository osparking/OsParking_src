/*
 * Copyright (C) 2015 Open Source Parking Inc.
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

import com.osparking.global.Globals;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.augmentComponentMap;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.readSettings;
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
    OSP_enums.FormMode formMode = OSP_enums.FormMode.SEARCHING;

    private HashMap<String, Component> componentMap = new HashMap<String,Component>();
    
    boolean[] inDemoMode = new boolean[EBD_DisplayUsage.values().length];
    
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
    public Settings_LEDnotice(
            ControlGUI mainForm, Settings_System parent, LEDnoticeManager manager, int gateNo)
    {
        initComponents();
        this.mainForm = mainForm;
        this.parent = parent; 
        if (mainForm == null) {
            this.manager = manager;
        } else {
            this.manager = (LEDnoticeManager)mainForm.getDeviceManagers()[E_Board.ordinal()][gateNo];
        }
        this.gateNo = gateNo;
        setIconImages(OSPiconList);
        augmentComponentMap(this, componentMap);
        
        setLocation(1200, 350);       // delete after development completion
        initEffectComboBoxes();
        initTypeComboBox();
        initColorComboBox();
        initFontComboBox();    
        initE_BoardTypeCBox();      
        
        // init selected items using ledNoticeSettings
        selectItemsForSelectedTab(0, 0);
        
        for (EBD_DisplayUsage usage : EBD_DisplayUsage.values()) {
            inDemoMode[usage.ordinal()] = false;
        }
    }

    private void initE_BoardTypeCBox() {
        for (int gateNo = 1; gateNo < gateCount; gateNo++) {
            JComboBox ebdTypeBox = (JComboBox)componentMap.get("EBD" + gateNo + "_TypeComboBox");

            if (ebdTypeBox != null) {
                ebdTypeBox.removeAllItems();
                for (OSP_enums.E_BoardType type : OSP_enums.E_BoardType.values()) {
                    ebdTypeBox.addItem(type.getLabel());
                }
            }
        }
    }

    private void demoCurrentSetting(EBD_DisplayUsage usage) 
    {
        int gateNo = findGateNoUsingLEDnotice();
        
        cancelDemoIfRunning();
        inDemoMode[usage.ordinal()] = true;
        
        if (gateNo == -1) {
            JOptionPane.showMessageDialog(this, "설정된 LEDnotice 장치가 없습니다.");
            return;
        } else {
            if (mainForm == null) {
                JOptionPane.showMessageDialog(this, "메인 창이 열려있지 않습니다.");
            } else {
                LEDnoticeManager manager = (LEDnoticeManager)mainForm
                        .getDeviceManagers()[E_Board.ordinal()][gateNo];           
                LEDnoticeSettings aSetting = getLEDnoticeSetting(usage);
                manager.showCurrentEffect(usage, aSetting); 
            }
        }
    }

    private void finishAllEffectDemo(int index) {
        int gateNo = findGateNoUsingLEDnotice();
        
        LEDnoticeManager manager = (LEDnoticeManager)mainForm
                .getDeviceManagers()[E_Board.ordinal()][gateNo];
        manager.finishShowingDemoEffect(index);
    }

    private void demoAllEffects(int tabIndex) {
        int stopIndex = ((JComboBox)componentMap.get("combo_PauseTime" + tabIndex)).getSelectedIndex();
        int colorIdx = ((JComboBox)componentMap.get("charColor" + tabIndex)).getSelectedIndex();
        int fontIdx = ((JComboBox)componentMap.get("charFont" + tabIndex)).getSelectedIndex();
        int gateNo = findGateNoUsingLEDnotice();
    
        cancelDemoIfRunning();
        inDemoMode[tabIndex] = true;
        
        if (gateNo == -1) {
            JOptionPane.showMessageDialog(this, "설정된 LEDnotice 장치가 없습니다.");
            return;
        } else {
            if (mainForm == null) {
                JOptionPane.showMessageDialog(this, "메인 창이 열려있지 않습니다.");
            } else {
                LEDnoticeManager manager = (LEDnoticeManager)mainForm.getDeviceManagers()[
                        E_Board.ordinal()][gateNo];

                manager.showAllEffects(tabIndex, stopIndex, colorIdx, fontIdx);
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

            typeBox.removeAllItems();
            
            if (usage == CAR_ENTRY_TOP_ROW || usage == CAR_ENTRY_BOTTOM_ROW) {
                for (LEDnotice_enums.LEDnoticeVehicleContentType aFont : 
                        LEDnotice_enums.LEDnoticeVehicleContentType.values()) 
                {
                    typeBox.addItem(aFont.getLabel());
                }
            }
            
            for (LEDnotice_enums.LEDnoticeDefaultContentType aFont : 
                    LEDnotice_enums.LEDnoticeDefaultContentType.values()) 
            {
                typeBox.addItem(aFont.getLabel());
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
        ledNoticePanel = new javax.swing.JPanel();
        wholePanel1 = new javax.swing.JPanel();
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
        charFont0 = new javax.swing.JComboBox();
        combo_StartEffect0 = new javax.swing.JComboBox();
        combo_FinishEffect0 = new javax.swing.JComboBox();
        label_Color4 = new javax.swing.JLabel();
        label_Color5 = new javax.swing.JLabel();
        label_Color6 = new javax.swing.JLabel();
        label_Color7 = new javax.swing.JLabel();
        combo_PauseTime0 = new javax.swing.JComboBox();
        useLEDnoticeCkBox0 = new javax.swing.JCheckBox();
        demoButton0 = new javax.swing.JButton();
        demoFinishButton0 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        startEffectHelpButton0 = new javax.swing.JButton();
        demoCurrHelpButton0 = new javax.swing.JButton();
        demoAllHelpButton0 = new javax.swing.JButton();
        endEffectHelpButton0 = new javax.swing.JButton();
        demoAllButton0 = new javax.swing.JButton();
        btn_Save0 = new javax.swing.JButton();
        btn_Cancel0 = new javax.swing.JButton();
        pauseTimeHelpButton0 = new javax.swing.JButton();
        useCkBoxHelpButton0 = new javax.swing.JButton();
        ledNoticePanel1 = new javax.swing.JPanel();
        label_MSG1 = new javax.swing.JLabel();
        label_Color1 = new javax.swing.JLabel();
        label_Font1 = new javax.swing.JLabel();
        label_ContentType1 = new javax.swing.JLabel();
        contentTypeBox1 = new javax.swing.JComboBox();
        tf_VerbatimContent1 = new javax.swing.JTextField();
        charColor1 = new javax.swing.JComboBox();
        charFont1 = new javax.swing.JComboBox();
        combo_StartEffect1 = new javax.swing.JComboBox();
        combo_FinishEffect1 = new javax.swing.JComboBox();
        label_Color8 = new javax.swing.JLabel();
        label_Color9 = new javax.swing.JLabel();
        label_Color10 = new javax.swing.JLabel();
        label_Color11 = new javax.swing.JLabel();
        combo_PauseTime1 = new javax.swing.JComboBox();
        useLEDnoticeCkBox1 = new javax.swing.JCheckBox();
        demoButton1 = new javax.swing.JButton();
        demoFinishButton1 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        startEffectHelpButton1 = new javax.swing.JButton();
        demoCurrHelpButton1 = new javax.swing.JButton();
        demoAllHelpButton1 = new javax.swing.JButton();
        demoAllButton1 = new javax.swing.JButton();
        btn_Save1 = new javax.swing.JButton();
        btn_Cancel1 = new javax.swing.JButton();
        useCkBoxHelpButton1 = new javax.swing.JButton();
        ledNoticePanelVehicle = new javax.swing.JTabbedPane();
        ledNoticePanel2 = new javax.swing.JPanel();
        label_MSG4 = new javax.swing.JLabel();
        label_Color12 = new javax.swing.JLabel();
        label_Font4 = new javax.swing.JLabel();
        label_ContentType4 = new javax.swing.JLabel();
        contentTypeBox2 = new javax.swing.JComboBox();
        tf_VerbatimContent2 = new javax.swing.JTextField();
        charColor2 = new javax.swing.JComboBox();
        charFont2 = new javax.swing.JComboBox();
        combo_StartEffect2 = new javax.swing.JComboBox();
        combo_FinishEffect2 = new javax.swing.JComboBox();
        label_Color13 = new javax.swing.JLabel();
        label_Color14 = new javax.swing.JLabel();
        label_Color15 = new javax.swing.JLabel();
        label_Color16 = new javax.swing.JLabel();
        combo_PauseTime2 = new javax.swing.JComboBox();
        useLEDnoticeCkBox2 = new javax.swing.JCheckBox();
        demoButton2 = new javax.swing.JButton();
        demoFinishButton2 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        startEffectHelpButton2 = new javax.swing.JButton();
        demoCurrHelpButton2 = new javax.swing.JButton();
        demoAllHelpButton2 = new javax.swing.JButton();
        endEffectHelpButton1 = new javax.swing.JButton();
        demoAllButton2 = new javax.swing.JButton();
        btn_Save2 = new javax.swing.JButton();
        btn_Cancel2 = new javax.swing.JButton();
        pauseTimeHelpButton1 = new javax.swing.JButton();
        useCkBoxHelpButton2 = new javax.swing.JButton();
        ledNoticePanel3 = new javax.swing.JPanel();
        label_MSG5 = new javax.swing.JLabel();
        label_Color17 = new javax.swing.JLabel();
        label_Font5 = new javax.swing.JLabel();
        label_ContentType5 = new javax.swing.JLabel();
        contentTypeBox3 = new javax.swing.JComboBox();
        tf_VerbatimContent3 = new javax.swing.JTextField();
        charColor3 = new javax.swing.JComboBox();
        charFont3 = new javax.swing.JComboBox();
        combo_StartEffect3 = new javax.swing.JComboBox();
        combo_FinishEffect3 = new javax.swing.JComboBox();
        label_Color18 = new javax.swing.JLabel();
        label_Color19 = new javax.swing.JLabel();
        label_Color20 = new javax.swing.JLabel();
        label_Color21 = new javax.swing.JLabel();
        combo_PauseTime3 = new javax.swing.JComboBox();
        useLEDnoticeCkBox3 = new javax.swing.JCheckBox();
        demoButton3 = new javax.swing.JButton();
        demoFinishButton3 = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        startEffectHelpButton3 = new javax.swing.JButton();
        demoCurrHelpButton3 = new javax.swing.JButton();
        demoAllHelpButton3 = new javax.swing.JButton();
        endEffectHelpButton2 = new javax.swing.JButton();
        demoAllButton3 = new javax.swing.JButton();
        btn_Save3 = new javax.swing.JButton();
        btn_Cancel3 = new javax.swing.JButton();
        pauseTimeHelpButton2 = new javax.swing.JButton();
        useCkBoxHelpButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btn_Exit = new javax.swing.JButton();

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
        setTitle("LEDnotice 장치 설정");
        setPreferredSize(new java.awt.Dimension(516, 370));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ledNoticePanel.setPreferredSize(new java.awt.Dimension(516, 285));

        wholePanel1.setPreferredSize(new java.awt.Dimension(506, 283));
        wholePanel1.setLayout(new java.awt.BorderLayout());

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
        ledNoticePanel0.setLayout(new java.awt.GridBagLayout());

        label_MSG0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG0.setText("문자열");
        label_MSG0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel0.add(label_MSG0, gridBagConstraints);

        label_Color0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color0.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color0.setText("중간멈춤");
        label_Color0.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel0.add(label_Font0, gridBagConstraints);

        label_ContentType0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType0.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel0.add(label_ContentType0, gridBagConstraints);

        contentTypeBox0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "문구 자체", "주차장 이름" }));
        contentTypeBox0.setMinimumSize(new java.awt.Dimension(123, 25));
        contentTypeBox0.setName("contentTypeBox0"); // NOI18N
        contentTypeBox0.setPreferredSize(new java.awt.Dimension(123, 25));
        contentTypeBox0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                contentTypeBox0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
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
        ledNoticePanel0.add(contentTypeBox0, gridBagConstraints);

        tf_VerbatimContent0.setColumns(23);
        tf_VerbatimContent0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent0.setMinimumSize(new java.awt.Dimension(250, 23));
        tf_VerbatimContent0.setName("tf_VerbatimContent0"); // NOI18N
        tf_VerbatimContent0.setPreferredSize(new java.awt.Dimension(250, 23));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        ledNoticePanel0.add(tf_VerbatimContent0, gridBagConstraints);

        charColor0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor0.setMinimumSize(new java.awt.Dimension(70, 25));
        charColor0.setName("charColor0"); // NOI18N
        charColor0.setPreferredSize(new java.awt.Dimension(70, 25));
        charColor0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charColor0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel0.add(charColor0, gridBagConstraints);

        charFont0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "굴림체", "궁서체" }));
        charFont0.setMinimumSize(new java.awt.Dimension(70, 25));
        charFont0.setName("charFont0"); // NOI18N
        charFont0.setPreferredSize(new java.awt.Dimension(70, 25));
        charFont0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charFont0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel0.add(charFont0, gridBagConstraints);

        combo_StartEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect0.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_StartEffect0.setName("combo_StartEffect0"); // NOI18N
        combo_StartEffect0.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_StartEffect0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_StartEffect0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        combo_StartEffect0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_StartEffect0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel0.add(combo_StartEffect0, gridBagConstraints);

        combo_FinishEffect0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect0.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect0.setName("combo_FinishEffect0"); // NOI18N
        combo_FinishEffect0.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_FinishEffect0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel0.add(label_Color4, gridBagConstraints);

        label_Color5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color5.setText("마침효과");
        label_Color5.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel0.add(label_Color5, gridBagConstraints);

        label_Color6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color6.setText("시작효과");
        label_Color6.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel0.add(label_Color6, gridBagConstraints);

        label_Color7.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color7.setText("초");
        label_Color7.setPreferredSize(new java.awt.Dimension(25, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        ledNoticePanel0.add(label_Color7, gridBagConstraints);

        combo_PauseTime0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime0.setMinimumSize(new java.awt.Dimension(70, 25));
        combo_PauseTime0.setName("combo_PauseTime0"); // NOI18N
        combo_PauseTime0.setPreferredSize(new java.awt.Dimension(70, 25));
        combo_PauseTime0.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_PauseTime0PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel0.add(combo_PauseTime0, gridBagConstraints);

        useLEDnoticeCkBox0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox0.setSelected(true);
        useLEDnoticeCkBox0.setText("사용");
        useLEDnoticeCkBox0.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox0.setName("useLEDnoticeCkBox0"); // NOI18N
        useLEDnoticeCkBox0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        ledNoticePanel0.add(useLEDnoticeCkBox0, gridBagConstraints);

        demoButton0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton0.setText("현재");
        demoButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel0.add(demoButton0, gridBagConstraints);

        demoFinishButton0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton0.setText("그만");
        demoFinishButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        ledNoticePanel0.add(demoFinishButton0, gridBagConstraints);

        jLabel41.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel41.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
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
        demoAllButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel0.add(demoAllButton0, gridBagConstraints);

        btn_Save0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save0.setMnemonic('s');
        btn_Save0.setText("저장(S)");
        btn_Save0.setEnabled(false);
        btn_Save0.setInheritsPopupMenu(true);
        btn_Save0.setMaximumSize(new java.awt.Dimension(73, 35));
        btn_Save0.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Save0.setName("btn_Save" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Save0.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Save0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Save0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel0.add(btn_Save0, gridBagConstraints);

        btn_Cancel0.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel0.setMnemonic('c');
        btn_Cancel0.setText("취소(C)");
        btn_Cancel0.setEnabled(false);
        btn_Cancel0.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Cancel0.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Cancel0.setName("btn_Cancel" + EBD_DisplayUsage.DEFAULT_TOP_ROW.ordinal());
        btn_Cancel0.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Cancel0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Cancel0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel0.add(btn_Cancel0, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        ledNoticePanel0.add(useCkBoxHelpButton0, gridBagConstraints);

        ledNoticePanelDefault.addTab("상단", ledNoticePanel0);

        ledNoticePanel1.setName("eBoard" + EBD_DisplayUsage.DEFAULT_TOP_ROW.getVal());
        ledNoticePanel1.setLayout(new java.awt.GridBagLayout());

        label_MSG1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG1.setText("문자열");
        label_MSG1.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel1.add(label_MSG1, gridBagConstraints);

        label_Color1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color1.setText("중간멈춤");
        label_Color1.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel1.add(label_Color1, gridBagConstraints);

        label_Font1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font1.setText("폰트");
        label_Font1.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font1.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel1.add(label_Font1, gridBagConstraints);

        label_ContentType1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType1.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel1.add(label_ContentType1, gridBagConstraints);

        contentTypeBox1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "문구 자체", "주차장 이름" }));
        contentTypeBox1.setMinimumSize(new java.awt.Dimension(123, 25));
        contentTypeBox1.setName("contentTypeBox1"); // NOI18N
        contentTypeBox1.setPreferredSize(new java.awt.Dimension(123, 25));
        contentTypeBox1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                contentTypeBox1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        contentTypeBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        ledNoticePanel1.add(contentTypeBox1, gridBagConstraints);

        tf_VerbatimContent1.setColumns(23);
        tf_VerbatimContent1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent1.setMinimumSize(new java.awt.Dimension(250, 23));
        tf_VerbatimContent1.setName("tf_VerbatimContent1"); // NOI18N
        tf_VerbatimContent1.setPreferredSize(new java.awt.Dimension(250, 23));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        ledNoticePanel1.add(tf_VerbatimContent1, gridBagConstraints);

        charColor1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor1.setMinimumSize(new java.awt.Dimension(70, 25));
        charColor1.setName("charColor1"); // NOI18N
        charColor1.setPreferredSize(new java.awt.Dimension(70, 25));
        charColor1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charColor1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel1.add(charColor1, gridBagConstraints);

        charFont1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "굴림체", "궁서체" }));
        charFont1.setMinimumSize(new java.awt.Dimension(70, 25));
        charFont1.setName("charFont1"); // NOI18N
        charFont1.setPreferredSize(new java.awt.Dimension(70, 25));
        charFont1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charFont1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel1.add(charFont1, gridBagConstraints);

        combo_StartEffect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect1.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_StartEffect1.setName("combo_StartEffect1"); // NOI18N
        combo_StartEffect1.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_StartEffect1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_StartEffect1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel1.add(combo_StartEffect1, gridBagConstraints);

        combo_FinishEffect1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect1.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect1.setName("combo_FinishEffect1"); // NOI18N
        combo_FinishEffect1.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_FinishEffect1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel1.add(combo_FinishEffect1, gridBagConstraints);

        label_Color8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color8.setText("색상");
        label_Color8.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color8.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel1.add(label_Color8, gridBagConstraints);

        label_Color9.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color9.setText("마침효과");
        label_Color9.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel1.add(label_Color9, gridBagConstraints);

        label_Color10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color10.setText("시작효과");
        label_Color10.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel1.add(label_Color10, gridBagConstraints);

        label_Color11.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color11.setText("초");
        label_Color11.setPreferredSize(new java.awt.Dimension(25, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        ledNoticePanel1.add(label_Color11, gridBagConstraints);

        combo_PauseTime1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime1.setMinimumSize(new java.awt.Dimension(70, 25));
        combo_PauseTime1.setName("combo_PauseTime1"); // NOI18N
        combo_PauseTime1.setPreferredSize(new java.awt.Dimension(70, 25));
        combo_PauseTime1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_PauseTime1PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel1.add(combo_PauseTime1, gridBagConstraints);

        useLEDnoticeCkBox1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox1.setSelected(true);
        useLEDnoticeCkBox1.setText("사용");
        useLEDnoticeCkBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox1.setName("useLEDnoticeCkBox1"); // NOI18N
        useLEDnoticeCkBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useLEDnoticeCkBox1StateChanged(evt);
            }
        });
        useLEDnoticeCkBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox1ActionPerformed(evt);
            }
        });
        ledNoticePanel1.add(useLEDnoticeCkBox1, new java.awt.GridBagConstraints());

        demoButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton1.setText("현재");
        demoButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel1.add(demoButton1, gridBagConstraints);

        demoFinishButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton1.setText("그만");
        demoFinishButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 0);
        ledNoticePanel1.add(demoFinishButton1, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel42.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel1.add(jLabel42, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        ledNoticePanel1.add(demoAllHelpButton1, gridBagConstraints);

        demoAllButton1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton1.setText("전체");
        demoAllButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel1.add(demoAllButton1, gridBagConstraints);

        btn_Save1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save1.setMnemonic('s');
        btn_Save1.setText("저장(S)");
        btn_Save1.setEnabled(false);
        btn_Save1.setInheritsPopupMenu(true);
        btn_Save1.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Save1.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Save1.setName("btn_Save" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        btn_Save1.setPreferredSize(new java.awt.Dimension(85, 35));
        btn_Save1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Save1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel1.add(btn_Save1, gridBagConstraints);

        btn_Cancel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel1.setMnemonic('c');
        btn_Cancel1.setText("취소(C)");
        btn_Cancel1.setEnabled(false);
        btn_Cancel1.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Cancel1.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Cancel1.setName("btn_Cancel" + EBD_DisplayUsage.DEFAULT_BOTTOM_ROW.ordinal());
        btn_Cancel1.setPreferredSize(new java.awt.Dimension(85, 35));
        btn_Cancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Cancel1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel1.add(btn_Cancel1, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        ledNoticePanel1.add(useCkBoxHelpButton1, gridBagConstraints);

        ledNoticePanelDefault.addTab("하단", ledNoticePanel1);

        ledNoticeTabbedPane.addTab("기본", ledNoticePanelDefault);

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

        label_MSG4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG4.setText("문자열");
        label_MSG4.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel2.add(label_MSG4, gridBagConstraints);

        label_Color12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color12.setText("중간멈춤");
        label_Color12.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel2.add(label_Color12, gridBagConstraints);

        label_Font4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font4.setText("폰트");
        label_Font4.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font4.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel2.add(label_Font4, gridBagConstraints);

        label_ContentType4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType4.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel2.add(label_ContentType4, gridBagConstraints);

        contentTypeBox2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "문구 자체", "주차장 이름" }));
        contentTypeBox2.setMinimumSize(new java.awt.Dimension(123, 25));
        contentTypeBox2.setName("contentTypeBox2"); // NOI18N
        contentTypeBox2.setPreferredSize(new java.awt.Dimension(123, 25));
        contentTypeBox2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                contentTypeBox2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        contentTypeBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        ledNoticePanel2.add(contentTypeBox2, gridBagConstraints);

        tf_VerbatimContent2.setColumns(23);
        tf_VerbatimContent2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent2.setMinimumSize(new java.awt.Dimension(250, 23));
        tf_VerbatimContent2.setName("tf_VerbatimContent2"); // NOI18N
        tf_VerbatimContent2.setPreferredSize(new java.awt.Dimension(250, 23));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        ledNoticePanel2.add(tf_VerbatimContent2, gridBagConstraints);

        charColor2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor2.setMinimumSize(new java.awt.Dimension(70, 25));
        charColor2.setName("charColor2"); // NOI18N
        charColor2.setPreferredSize(new java.awt.Dimension(70, 25));
        charColor2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charColor2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel2.add(charColor2, gridBagConstraints);

        charFont2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "굴림체", "궁서체" }));
        charFont2.setMinimumSize(new java.awt.Dimension(70, 25));
        charFont2.setName("charFont2"); // NOI18N
        charFont2.setPreferredSize(new java.awt.Dimension(70, 25));
        charFont2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charFont2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel2.add(charFont2, gridBagConstraints);

        combo_StartEffect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect2.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_StartEffect2.setName("combo_StartEffect2"); // NOI18N
        combo_StartEffect2.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_StartEffect2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_StartEffect2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel2.add(combo_StartEffect2, gridBagConstraints);

        combo_FinishEffect2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect2.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect2.setName("combo_FinishEffect2"); // NOI18N
        combo_FinishEffect2.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_FinishEffect2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel2.add(combo_FinishEffect2, gridBagConstraints);

        label_Color13.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color13.setText("색상");
        label_Color13.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color13.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel2.add(label_Color13, gridBagConstraints);

        label_Color14.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color14.setText("마침효과");
        label_Color14.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel2.add(label_Color14, gridBagConstraints);

        label_Color15.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color15.setText("시작효과");
        label_Color15.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel2.add(label_Color15, gridBagConstraints);

        label_Color16.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color16.setText("초");
        label_Color16.setPreferredSize(new java.awt.Dimension(25, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        ledNoticePanel2.add(label_Color16, gridBagConstraints);

        combo_PauseTime2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime2.setMinimumSize(new java.awt.Dimension(70, 25));
        combo_PauseTime2.setName("combo_PauseTime2"); // NOI18N
        combo_PauseTime2.setPreferredSize(new java.awt.Dimension(70, 25));
        combo_PauseTime2.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_PauseTime2PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel2.add(combo_PauseTime2, gridBagConstraints);

        useLEDnoticeCkBox2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox2.setSelected(true);
        useLEDnoticeCkBox2.setText("사용");
        useLEDnoticeCkBox2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox2.setName("useLEDnoticeCkBox2"); // NOI18N
        useLEDnoticeCkBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        ledNoticePanel2.add(useLEDnoticeCkBox2, gridBagConstraints);

        demoButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton2.setText("현재");
        demoButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel2.add(demoButton2, gridBagConstraints);

        demoFinishButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton2.setText("그만");
        demoFinishButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 0);
        ledNoticePanel2.add(demoFinishButton2, gridBagConstraints);

        jLabel43.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel43.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel2.add(jLabel43, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        ledNoticePanel2.add(demoAllHelpButton2, gridBagConstraints);

        endEffectHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        endEffectHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        endEffectHelpButton1.setIcon(getQuest20_Icon());
        endEffectHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        endEffectHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton1.setOpaque(false);
        endEffectHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        endEffectHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endEffectHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel2.add(endEffectHelpButton1, gridBagConstraints);

        demoAllButton2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton2.setText("전체");
        demoAllButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel2.add(demoAllButton2, gridBagConstraints);

        btn_Save2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save2.setMnemonic('s');
        btn_Save2.setText("저장(S)");
        btn_Save2.setEnabled(false);
        btn_Save2.setInheritsPopupMenu(true);
        btn_Save2.setMaximumSize(new java.awt.Dimension(73, 35));
        btn_Save2.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Save2.setName("btn_Save" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        btn_Save2.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Save2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Save2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel2.add(btn_Save2, gridBagConstraints);

        btn_Cancel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel2.setMnemonic('c');
        btn_Cancel2.setText("취소(C)");
        btn_Cancel2.setEnabled(false);
        btn_Cancel2.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Cancel2.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Cancel2.setName("btn_Cancel" + EBD_DisplayUsage.CAR_ENTRY_TOP_ROW.ordinal());
        btn_Cancel2.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Cancel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Cancel2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel2.add(btn_Cancel2, gridBagConstraints);

        pauseTimeHelpButton1.setBackground(new java.awt.Color(153, 255, 153));
        pauseTimeHelpButton1.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        pauseTimeHelpButton1.setIcon(getQuest20_Icon());
        pauseTimeHelpButton1.setMargin(new java.awt.Insets(2, 4, 2, 4));
        pauseTimeHelpButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton1.setOpaque(false);
        pauseTimeHelpButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        pauseTimeHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseTimeHelpButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        ledNoticePanel2.add(pauseTimeHelpButton1, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        ledNoticePanel2.add(useCkBoxHelpButton2, gridBagConstraints);

        ledNoticePanelVehicle.addTab("상단", ledNoticePanel2);

        ledNoticePanel3.setName("eBoard" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.getVal());
        ledNoticePanel3.setLayout(new java.awt.GridBagLayout());

        label_MSG5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_MSG5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_MSG5.setText("문자열");
        label_MSG5.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel3.add(label_MSG5, gridBagConstraints);

        label_Color17.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color17.setText("중간멈춤");
        label_Color17.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel3.add(label_Color17, gridBagConstraints);

        label_Font5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Font5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Font5.setText("폰트");
        label_Font5.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Font5.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel3.add(label_Font5, gridBagConstraints);

        label_ContentType5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_ContentType5.setText("표시유형");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel3.add(label_ContentType5, gridBagConstraints);

        contentTypeBox3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        contentTypeBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "문구 자체", "주차장 이름" }));
        contentTypeBox3.setMinimumSize(new java.awt.Dimension(123, 25));
        contentTypeBox3.setName("contentTypeBox3"); // NOI18N
        contentTypeBox3.setPreferredSize(new java.awt.Dimension(123, 25));
        contentTypeBox3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                contentTypeBox3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        contentTypeBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentTypeBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        ledNoticePanel3.add(contentTypeBox3, gridBagConstraints);

        tf_VerbatimContent3.setColumns(23);
        tf_VerbatimContent3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        tf_VerbatimContent3.setMinimumSize(new java.awt.Dimension(250, 23));
        tf_VerbatimContent3.setName("tf_VerbatimContent3"); // NOI18N
        tf_VerbatimContent3.setPreferredSize(new java.awt.Dimension(250, 23));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        ledNoticePanel3.add(tf_VerbatimContent3, gridBagConstraints);

        charColor3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charColor3.setMinimumSize(new java.awt.Dimension(70, 25));
        charColor3.setName("charColor3"); // NOI18N
        charColor3.setPreferredSize(new java.awt.Dimension(70, 25));
        charColor3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charColor3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel3.add(charColor3, gridBagConstraints);

        charFont3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        charFont3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "굴림체", "궁서체" }));
        charFont3.setMinimumSize(new java.awt.Dimension(70, 25));
        charFont3.setName("charFont3"); // NOI18N
        charFont3.setPreferredSize(new java.awt.Dimension(70, 25));
        charFont3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                charFont3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel3.add(charFont3, gridBagConstraints);

        combo_StartEffect3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_StartEffect3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_StartEffect3.setEnabled(false);
        combo_StartEffect3.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_StartEffect3.setName("combo_StartEffect3"); // NOI18N
        combo_StartEffect3.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_StartEffect3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_StartEffect3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel3.add(combo_StartEffect3, gridBagConstraints);

        combo_FinishEffect3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_FinishEffect3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "효과없음", "왼쪽흐름", "오른쪽흐름", "위로흐름", "아래로흐름", "정지", "깜빡임", "반전", "플레싱", "블라인드", "레이저", "중앙이동", "펼침", "좌흐름적색깜빡임", "우흐름적색깜빡임", "좌흐름녹색깜빡임", "우흐름녹색깜빡임", "회전", "좌우열기", "좌우닫기", "상하열기", "상하닫기", "모듈별이동", "모듈별회전", "상하색분리", "좌우색분리", "테두리이동", "확대", "세로확대", "가로확대", "줄깜빡임", "가로쌓기", "흩뿌리기" }));
        combo_FinishEffect3.setEnabled(false);
        combo_FinishEffect3.setMinimumSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect3.setName("combo_FinishEffect3"); // NOI18N
        combo_FinishEffect3.setPreferredSize(new java.awt.Dimension(123, 25));
        combo_FinishEffect3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_FinishEffect3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel3.add(combo_FinishEffect3, gridBagConstraints);

        label_Color18.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color18.setText("색상");
        label_Color18.setMaximumSize(new java.awt.Dimension(100, 15));
        label_Color18.setPreferredSize(new java.awt.Dimension(38, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel3.add(label_Color18, gridBagConstraints);

        label_Color19.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color19.setText("마침효과");
        label_Color19.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel3.add(label_Color19, gridBagConstraints);

        label_Color20.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color20.setText("시작효과");
        label_Color20.setPreferredSize(new java.awt.Dimension(76, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        ledNoticePanel3.add(label_Color20, gridBagConstraints);

        label_Color21.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        label_Color21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_Color21.setText("초");
        label_Color21.setPreferredSize(new java.awt.Dimension(25, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        ledNoticePanel3.add(label_Color21, gridBagConstraints);

        combo_PauseTime3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        combo_PauseTime3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combo_PauseTime3.setEnabled(false);
        combo_PauseTime3.setMinimumSize(new java.awt.Dimension(70, 25));
        combo_PauseTime3.setName("combo_PauseTime3"); // NOI18N
        combo_PauseTime3.setPreferredSize(new java.awt.Dimension(70, 25));
        combo_PauseTime3.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_PauseTime3PopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ledNoticePanel3.add(combo_PauseTime3, gridBagConstraints);

        useLEDnoticeCkBox3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        useLEDnoticeCkBox3.setSelected(true);
        useLEDnoticeCkBox3.setText("사용");
        useLEDnoticeCkBox3.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        useLEDnoticeCkBox3.setName("useLEDnoticeCkBox3"); // NOI18N
        useLEDnoticeCkBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLEDnoticeCkBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        ledNoticePanel3.add(useLEDnoticeCkBox3, gridBagConstraints);

        demoButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoButton3.setText("현재");
        demoButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel3.add(demoButton3, gridBagConstraints);

        demoFinishButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoFinishButton3.setText("그만");
        demoFinishButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoFinishButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 0);
        ledNoticePanel3.add(demoFinishButton3, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel44.setText("시연보기");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel3.add(jLabel44, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        ledNoticePanel3.add(demoAllHelpButton3, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        ledNoticePanel3.add(endEffectHelpButton2, gridBagConstraints);

        demoAllButton3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        demoAllButton3.setText("전체");
        demoAllButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoAllButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        ledNoticePanel3.add(demoAllButton3, gridBagConstraints);

        btn_Save3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Save3.setMnemonic('s');
        btn_Save3.setText("저장(S)");
        btn_Save3.setEnabled(false);
        btn_Save3.setInheritsPopupMenu(true);
        btn_Save3.setMaximumSize(new java.awt.Dimension(73, 35));
        btn_Save3.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Save3.setName("btn_Save" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        btn_Save3.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Save3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Save3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel3.add(btn_Save3, gridBagConstraints);

        btn_Cancel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Cancel3.setMnemonic('c');
        btn_Cancel3.setText("취소(C)");
        btn_Cancel3.setEnabled(false);
        btn_Cancel3.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Cancel3.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Cancel3.setName("btn_Cancel" + EBD_DisplayUsage.CAR_ENTRY_BOTTOM_ROW.ordinal());
        btn_Cancel3.setPreferredSize(new java.awt.Dimension(85, 30));
        btn_Cancel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Cancel3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        ledNoticePanel3.add(btn_Cancel3, gridBagConstraints);

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
        ledNoticePanel3.add(pauseTimeHelpButton2, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        ledNoticePanel3.add(useCkBoxHelpButton3, gridBagConstraints);

        ledNoticePanelVehicle.addTab("하단", ledNoticePanel3);

        ledNoticeTabbedPane.addTab("차량", ledNoticePanelVehicle);

        wholePanel1.add(ledNoticeTabbedPane, java.awt.BorderLayout.PAGE_START);

        ledNoticePanel.add(wholePanel1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btn_Exit.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        btn_Exit.setMnemonic('c');
        btn_Exit.setText("닫기(C)");
        btn_Exit.setMaximumSize(new java.awt.Dimension(85, 35));
        btn_Exit.setMinimumSize(new java.awt.Dimension(85, 35));
        btn_Exit.setPreferredSize(new java.awt.Dimension(85, 35));
        btn_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ledNoticePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(285, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ledNoticePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 45, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combo_StartEffect0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_StartEffect0PopupMenuWillBecomeInvisible
        checkStartEffectComboBoxValueChange(0);
    }//GEN-LAST:event_combo_StartEffect0PopupMenuWillBecomeInvisible

    private void useLEDnoticeCkBox0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox0ActionPerformed
        checkLEDnoticeRowUsageChangeAndChangeButtonEnabled(DEFAULT_TOP_ROW);
        changeOtherComponentEnabled(DEFAULT_TOP_ROW, useLEDnoticeCkBox0.isSelected());
        
        if (!useLEDnoticeCkBox0.isSelected()) {
            useLEDnoticeCkBox1.setSelected(false);
        }
    }//GEN-LAST:event_useLEDnoticeCkBox0ActionPerformed

    private void demoButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton0ActionPerformed
        demoCurrentSetting(DEFAULT_TOP_ROW);
    }//GEN-LAST:event_demoButton0ActionPerformed

    private void demoFinishButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton0ActionPerformed
        finishAllEffectDemo(0);
    }//GEN-LAST:event_demoFinishButton0ActionPerformed

    private void startEffectHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton0ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton0ActionPerformed

    private void demoCurrHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this, "상, 하단 연계된 설정을 시연함" + System.lineSeparator()
            + "[그만] 버튼 사용으로 시연 종료!");
    }//GEN-LAST:event_demoCurrHelpButton0ActionPerformed

    private void demoAllHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton0ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton0ActionPerformed

    private void endEffectHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this,
            "하단 사용[V] 될 경우," + System.lineSeparator() + "자동으로 '효과 없음' 설정됨");
    }//GEN-LAST:event_endEffectHelpButton0ActionPerformed

    private void demoAllButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton0ActionPerformed
        demoAllEffects(0);
    }//GEN-LAST:event_demoAllButton0ActionPerformed

    private void btn_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ExitActionPerformed
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_btn_ExitActionPerformed

    private void btn_Save0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Save0ActionPerformed
        saveLEDnoticeSettingsTab(0);
    }//GEN-LAST:event_btn_Save0ActionPerformed

    private void btn_Cancel0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Cancel0ActionPerformed
        cancelModification(0);
    }//GEN-LAST:event_btn_Cancel0ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        tryToCloseEBDSettingsForm();
    }//GEN-LAST:event_formWindowClosing

    private void contentTypeBox0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox0ActionPerformed
        checkContentType(0);
    }//GEN-LAST:event_contentTypeBox0ActionPerformed

    private void contentTypeBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox1ActionPerformed
        checkContentType(1);
    }//GEN-LAST:event_contentTypeBox1ActionPerformed

    private void combo_StartEffect1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_StartEffect1PopupMenuWillBecomeInvisible
        checkStartEffectComboBoxValueChange(1);
    }//GEN-LAST:event_combo_StartEffect1PopupMenuWillBecomeInvisible

    private void useLEDnoticeCkBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox1ActionPerformed
        // 저장 버튼 활성화 설정 변경
        checkLEDnoticeRowUsageChangeAndChangeButtonEnabled(DEFAULT_BOTTOM_ROW);
        changeOtherComponentEnabled(DEFAULT_BOTTOM_ROW, useLEDnoticeCkBox1.isSelected());
        
        // 사용되는 경우, 
        //      - 상단 사용 박스 참 설정
        //      - 상단 설정 '중간 멈춤' 1초 설정
        //      - 상단 설정 '마침 효과'를 '효과 없음'으로 강제 변환
        if (useLEDnoticeCkBox1.isSelected()) {
            useLEDnoticeCkBox0.setSelected(true); // 상단 사용
            combo_PauseTime0.setSelectedIndex(0); // 1초 설정
            combo_FinishEffect0.setSelectedItem(EffectType.NONE.getLabel());
            combo_PauseTime0.setEnabled(false);
            combo_FinishEffect0.setEnabled(false);
        } else {
            combo_PauseTime0.setEnabled(true);
            combo_FinishEffect0.setEnabled(true);
        }
    }//GEN-LAST:event_useLEDnoticeCkBox1ActionPerformed

    private void demoButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton1ActionPerformed
        if (useLEDnoticeCkBox1.isSelected()) {
            demoCurrentSetting(DEFAULT_TOP_ROW);
            demoCurrentSetting(DEFAULT_BOTTOM_ROW);
        } else {
            JOptionPane.showMessageDialog(this, "먼저 '사용' 첵크 상자를 선택할 것!");
        }
    }//GEN-LAST:event_demoButton1ActionPerformed

    private void demoFinishButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton1ActionPerformed
        finishAllEffectDemo(1);
    }//GEN-LAST:event_demoFinishButton1ActionPerformed

    private void startEffectHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton1ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton1ActionPerformed

    private void demoCurrHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton1ActionPerformed
        JOptionPane.showMessageDialog(this, "상, 하단 연계된 설정을 시연함" + System.lineSeparator()
            + "[그만] 버튼 사용으로 시연 종료!");
    }//GEN-LAST:event_demoCurrHelpButton1ActionPerformed

    private void demoAllHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton1ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton1ActionPerformed

    private void demoAllButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton1ActionPerformed
        demoAllEffects(1);
    }//GEN-LAST:event_demoAllButton1ActionPerformed

    private void btn_Save1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Save1ActionPerformed
        if (useLEDnoticeCkBox1.isSelected()) {
            saveLEDnoticeSettingsTab(0);
        }
        saveLEDnoticeSettingsTab(1);
    }//GEN-LAST:event_btn_Save1ActionPerformed

    private void btn_Cancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Cancel1ActionPerformed
        cancelModification(1);
    }//GEN-LAST:event_btn_Cancel1ActionPerformed

    private void pauseTimeHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this,
            "하단 사용[V]의 경우," + System.lineSeparator() + "자동설정 값 '1' (초) 임!");
    }//GEN-LAST:event_pauseTimeHelpButton0ActionPerformed

    private void useCkBoxHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton1ActionPerformed
        JOptionPane.showMessageDialog(this,
            "사용[V]의 경우, 자동 변경되는 상단 행 설정" + System.lineSeparator() + 
                "      - 상단 행 사용[V] 설정하며," + System.lineSeparator() + 
                "      - 상단 행 '중간 멈춤' 1초로 변경하며," + System.lineSeparator() + 
                "      - 상단 행 '마침 효과'를 '효과 없음'으로 변환함.");
    }//GEN-LAST:event_useCkBoxHelpButton1ActionPerformed

    private void useCkBoxHelpButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton0ActionPerformed
        JOptionPane.showMessageDialog(this,
            "전광판 기본 표시 문구 행 사용 지침" + System.lineSeparator() + 
                "      - 상, 하단 행 모두 사용하거나," + System.lineSeparator() + 
                "      - 상단 행만 사용하거나," + System.lineSeparator() + 
                "      - 두 행 모두 사용하지 아니함.");
    }//GEN-LAST:event_useCkBoxHelpButton0ActionPerformed

    private void contentTypeBox0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_contentTypeBox0PopupMenuWillBecomeInvisible
        checkContentTypeBoxAndEnableButtons(0);
    }//GEN-LAST:event_contentTypeBox0PopupMenuWillBecomeInvisible

    private void contentTypeBox1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_contentTypeBox1PopupMenuWillBecomeInvisible
        checkContentTypeBoxAndEnableButtons(1);
    }//GEN-LAST:event_contentTypeBox1PopupMenuWillBecomeInvisible

    private void tf_VerbatimContent0KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent0KeyReleased
        checkVerbatimContentChangeButtonEnabled(0);
    }//GEN-LAST:event_tf_VerbatimContent0KeyReleased

    private void tf_VerbatimContent1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent1KeyReleased
        checkVerbatimContentChangeButtonEnabled(1);
    }//GEN-LAST:event_tf_VerbatimContent1KeyReleased

    private void useLEDnoticeCkBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox1StateChanged
        changeOtherComponentEnabled(DEFAULT_BOTTOM_ROW, useLEDnoticeCkBox1.isSelected());
    }//GEN-LAST:event_useLEDnoticeCkBox1StateChanged

    private void ledNoticePanelDefaultStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ledNoticePanelDefaultStateChanged
        if (componentMap == null)
            return;
        
        cancelDemoIfRunning();
        
        final int row = ledNoticePanelDefault.getSelectedIndex();
        int prevTabIdx = (row == 0 ? 1 : 0);
        JButton saveButton = (JButton)componentMap.get("btn_Save" + prevTabIdx);
        
        if (saveButton != null && saveButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, 
                    "LEDnotice 설정이 변경 중입니다.," + System.lineSeparator()
                            + "[저장] 혹은 [취소] 중 하나를 선택하십시오!");
            ledNoticePanelDefault.setSelectedIndex(prevTabIdx);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectItemsForSelectedTab(row, 0); // 0 : default column
            }
        });
    }//GEN-LAST:event_ledNoticePanelDefaultStateChanged

    private void combo_PauseTime0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_PauseTime0PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfPauseTimeChanged(0);
    }//GEN-LAST:event_combo_PauseTime0PopupMenuWillBecomeInvisible

    private void combo_FinishEffect0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_FinishEffect0PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFinishEffectChanged(0);
    }//GEN-LAST:event_combo_FinishEffect0PopupMenuWillBecomeInvisible

    private void charColor0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charColor0PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfColorChanged(0);
    }//GEN-LAST:event_charColor0PopupMenuWillBecomeInvisible

    private void charFont0PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charFont0PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFontChanged(0);
    }//GEN-LAST:event_charFont0PopupMenuWillBecomeInvisible

    private void combo_PauseTime1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_PauseTime1PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfPauseTimeChanged(1);
    }//GEN-LAST:event_combo_PauseTime1PopupMenuWillBecomeInvisible

    private void combo_FinishEffect1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_FinishEffect1PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFinishEffectChanged(1);
    }//GEN-LAST:event_combo_FinishEffect1PopupMenuWillBecomeInvisible

    private void charColor1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charColor1PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfColorChanged(1);
    }//GEN-LAST:event_charColor1PopupMenuWillBecomeInvisible

    private void charFont1PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charFont1PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFontChanged(1);
    }//GEN-LAST:event_charFont1PopupMenuWillBecomeInvisible

    private void contentTypeBox2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_contentTypeBox2PopupMenuWillBecomeInvisible
        checkContentTypeBoxAndEnableButtons(2);
    }//GEN-LAST:event_contentTypeBox2PopupMenuWillBecomeInvisible

    private void contentTypeBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox2ActionPerformed
        checkContentType(2);
    }//GEN-LAST:event_contentTypeBox2ActionPerformed

    private void tf_VerbatimContent2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent2KeyReleased
        checkVerbatimContentChangeButtonEnabled(2);
    }//GEN-LAST:event_tf_VerbatimContent2KeyReleased

    private void charColor2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charColor2PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfColorChanged(2);
    }//GEN-LAST:event_charColor2PopupMenuWillBecomeInvisible

    private void charFont2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charFont2PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFontChanged(2);
    }//GEN-LAST:event_charFont2PopupMenuWillBecomeInvisible

    private void combo_StartEffect2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_StartEffect2PopupMenuWillBecomeInvisible
        checkStartEffectComboBoxValueChange(2);
    }//GEN-LAST:event_combo_StartEffect2PopupMenuWillBecomeInvisible

    private void combo_FinishEffect2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_FinishEffect2PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFinishEffectChanged(2);
    }//GEN-LAST:event_combo_FinishEffect2PopupMenuWillBecomeInvisible

    private void combo_PauseTime2PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_PauseTime2PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfPauseTimeChanged(2);
    }//GEN-LAST:event_combo_PauseTime2PopupMenuWillBecomeInvisible

    private void useLEDnoticeCkBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox2ActionPerformed
        checkLEDnoticeRowUsageChangeAndChangeButtonEnabled(CAR_ENTRY_TOP_ROW);
        changeOtherComponentEnabled(CAR_ENTRY_TOP_ROW, useLEDnoticeCkBox2.isSelected());
        
        if (!useLEDnoticeCkBox2.isSelected()) {
            useLEDnoticeCkBox3.setSelected(false);
        }
    }//GEN-LAST:event_useLEDnoticeCkBox2ActionPerformed

    private void demoButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton2ActionPerformed
        demoCurrentInterruptSettings();
    }//GEN-LAST:event_demoButton2ActionPerformed

    private void demoFinishButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton2ActionPerformed
        finishAllEffectDemo(2);
    }//GEN-LAST:event_demoFinishButton2ActionPerformed

    private void startEffectHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton2ActionPerformed
        giveStartEffectAutoAdjustmentPossibility();
    }//GEN-LAST:event_startEffectHelpButton2ActionPerformed

    private void demoCurrHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton2ActionPerformed
        JOptionPane.showMessageDialog(this, "현재 설정 상태를 시연함" + System.lineSeparator()
                + "입차 표시 3초 후, 기본 표시로 복귀함" + System.lineSeparator()
                + "[그만] 버튼 사용으로 시연 종료!");        
    }//GEN-LAST:event_demoCurrHelpButton2ActionPerformed

    private void demoAllHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton2ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton2ActionPerformed

    private void endEffectHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton1ActionPerformed
        JOptionPane.showMessageDialog(this,
                "차량 도착의 경우, 기본 표시와 달리" + System.lineSeparator() +
                "마침 효과는 설정한 대로 적용됨.");
    }//GEN-LAST:event_endEffectHelpButton1ActionPerformed

    private void demoAllButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton2ActionPerformed
        demoAllEffects(2);
    }//GEN-LAST:event_demoAllButton2ActionPerformed

    private void btn_Save2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Save2ActionPerformed
        saveLEDnoticeSettingsTab(2);
    }//GEN-LAST:event_btn_Save2ActionPerformed

    private void btn_Cancel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Cancel2ActionPerformed
        cancelModification(2);
    }//GEN-LAST:event_btn_Cancel2ActionPerformed

    private void pauseTimeHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton1ActionPerformed
        JOptionPane.showMessageDialog(this,
                "차량 도착의 경우, 기본 표시와 달리" + System.lineSeparator() +
                "중간 멈춤 시간은 설정한 대로 적용됨.");        
    }//GEN-LAST:event_pauseTimeHelpButton1ActionPerformed

    private void useCkBoxHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton2ActionPerformed
        JOptionPane.showMessageDialog(this,
            "전광판 차량 표시 문구 행 사용 지침" + System.lineSeparator() + 
                "      - 상, 하단 행 모두 사용하거나," + System.lineSeparator() + 
                "      - 상단 행만 사용하거나," + System.lineSeparator() + 
                "      - 두 행 모두 사용하지 아니함.");        
    }//GEN-LAST:event_useCkBoxHelpButton2ActionPerformed

    private void contentTypeBox3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_contentTypeBox3PopupMenuWillBecomeInvisible
        checkContentTypeBoxAndEnableButtons(3);
    }//GEN-LAST:event_contentTypeBox3PopupMenuWillBecomeInvisible

    private void contentTypeBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentTypeBox3ActionPerformed
        checkContentType(3);        
    }//GEN-LAST:event_contentTypeBox3ActionPerformed

    private void tf_VerbatimContent3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_VerbatimContent3KeyReleased
        checkVerbatimContentChangeButtonEnabled(3);
    }//GEN-LAST:event_tf_VerbatimContent3KeyReleased

    private void charColor3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charColor3PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfColorChanged(3);
    }//GEN-LAST:event_charColor3PopupMenuWillBecomeInvisible

    private void charFont3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_charFont3PopupMenuWillBecomeInvisible
        changeButtonEnabled_IfFontChanged(3);
    }//GEN-LAST:event_charFont3PopupMenuWillBecomeInvisible

    private void combo_StartEffect3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_StartEffect3PopupMenuWillBecomeInvisible
    }//GEN-LAST:event_combo_StartEffect3PopupMenuWillBecomeInvisible

    private void combo_FinishEffect3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_FinishEffect3PopupMenuWillBecomeInvisible
    }//GEN-LAST:event_combo_FinishEffect3PopupMenuWillBecomeInvisible

    private void combo_PauseTime3PopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_PauseTime3PopupMenuWillBecomeInvisible
    }//GEN-LAST:event_combo_PauseTime3PopupMenuWillBecomeInvisible

    private void useLEDnoticeCkBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLEDnoticeCkBox3ActionPerformed
        checkLEDnoticeRowUsageChangeAndChangeButtonEnabled(CAR_ENTRY_BOTTOM_ROW);
        changeOtherComponentEnabled(CAR_ENTRY_BOTTOM_ROW, useLEDnoticeCkBox3.isSelected());
        
        if (useLEDnoticeCkBox3.isSelected()) {
            useLEDnoticeCkBox2.setSelected(true);
        }
    }//GEN-LAST:event_useLEDnoticeCkBox3ActionPerformed

    private void demoButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoButton3ActionPerformed
        demoCurrentInterruptSettings();
    }//GEN-LAST:event_demoButton3ActionPerformed

    private void demoFinishButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoFinishButton3ActionPerformed
        finishAllEffectDemo(3);
    }//GEN-LAST:event_demoFinishButton3ActionPerformed

    private void startEffectHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startEffectHelpButton3ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_startEffectHelpButton3ActionPerformed

    private void demoCurrHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoCurrHelpButton3ActionPerformed
        JOptionPane.showMessageDialog(this, "상, 하단 연계된 설정을 시연함" + System.lineSeparator()
            + "[그만] 버튼 사용으로 시연 종료!");
    }//GEN-LAST:event_demoCurrHelpButton3ActionPerformed

    private void demoAllHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllHelpButton3ActionPerformed
        showPopUpForDemoAllHelpButton();
    }//GEN-LAST:event_demoAllHelpButton3ActionPerformed

    private void endEffectHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endEffectHelpButton2ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_endEffectHelpButton2ActionPerformed

    private void demoAllButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoAllButton3ActionPerformed
        demoAllEffects(3);
    }//GEN-LAST:event_demoAllButton3ActionPerformed

    private void btn_Save3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Save3ActionPerformed
        saveLEDnoticeSettingsTab(3);
        
        if (useLEDnoticeCkBox3.isSelected()) {
            saveLEDnoticeSettingsTab(2);
        }
    }//GEN-LAST:event_btn_Save3ActionPerformed

    private void btn_Cancel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Cancel3ActionPerformed
        cancelModification(3);
    }//GEN-LAST:event_btn_Cancel3ActionPerformed

    private void pauseTimeHelpButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseTimeHelpButton2ActionPerformed
        giveWhyCarLowRowEffectNotUsed();
    }//GEN-LAST:event_pauseTimeHelpButton2ActionPerformed

    private void useCkBoxHelpButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCkBoxHelpButton3ActionPerformed
        JOptionPane.showMessageDialog(this,
            "사용[V]의 경우, 자동으로" + System.lineSeparator() 
                    + "상단 행을 사용[V]으로 설정함");
    }//GEN-LAST:event_useCkBoxHelpButton3ActionPerformed

    private void ledNoticeTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ledNoticeTabbedPaneStateChanged
        if (componentMap == null)
            return;
        
        cancelDemoIfRunning();
        
        final int column = ledNoticeTabbedPane.getSelectedIndex();
        int prevTabIdx = (column == 0 ? 1 : 0);
        JButton saveButtonTop = (JButton)componentMap.get("btn_Save" + (prevTabIdx * 2));
        JButton saveButtonBottom = (JButton)componentMap.get("btn_Save" + (prevTabIdx * 2 + 1));
        
        if ((saveButtonTop != null && saveButtonTop.isEnabled()) || 
                (saveButtonBottom != null && saveButtonBottom.isEnabled())) {
            JOptionPane.showMessageDialog(this, 
                    "LEDnotice 설정이 변경 중입니다.," + System.lineSeparator()
                            + "[저장] 혹은 [취소] 중 하나를 선택하십시오!");
            ledNoticeTabbedPane.setSelectedIndex(prevTabIdx);
        }
        final int row = ((JTabbedPane)ledNoticeTabbedPane.getSelectedComponent()).getSelectedIndex();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectItemsForSelectedTab(row, column);
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
                selectItemsForSelectedTab(row, 1); // 1 : vehicle column
            }
        });        
    }//GEN-LAST:event_ledNoticePanelVehicleStateChanged

    private void combo_StartEffect0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_StartEffect0ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_StartEffect0ActionPerformed

    /**
     *  Decide whether to use the verbatim text field after checking the content type.
     */
    public void checkContentType(int usage){
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
            java.util.logging.Logger.getLogger(Settings_LEDnotice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Settings_LEDnotice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Settings_LEDnotice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Settings_LEDnotice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        final byte gateNo = 1;
        
        initializeLoggers();
        checkOptions(args);
        readSettings();              
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @SuppressWarnings("ResultOfObjectAllocationIgnored")
            public void run() {
                ControlGUI controlGUI = new ControlGUI();
                Settings_LEDnotice settingsGUI = new Settings_LEDnotice(controlGUI, null, LEDmanager, gateNo);
                settingsGUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Cancel0;
    private javax.swing.JButton btn_Cancel1;
    private javax.swing.JButton btn_Cancel2;
    private javax.swing.JButton btn_Cancel3;
    private javax.swing.JButton btn_Exit;
    private javax.swing.JButton btn_Save0;
    private javax.swing.JButton btn_Save1;
    private javax.swing.JButton btn_Save2;
    private javax.swing.JButton btn_Save3;
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
    private javax.swing.JComboBox combo_PauseTime3;
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
    private javax.swing.JButton endEffectHelpButton1;
    private javax.swing.JButton endEffectHelpButton2;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label_Color0;
    private javax.swing.JLabel label_Color1;
    private javax.swing.JLabel label_Color10;
    private javax.swing.JLabel label_Color11;
    private javax.swing.JLabel label_Color12;
    private javax.swing.JLabel label_Color13;
    private javax.swing.JLabel label_Color14;
    private javax.swing.JLabel label_Color15;
    private javax.swing.JLabel label_Color16;
    private javax.swing.JLabel label_Color17;
    private javax.swing.JLabel label_Color18;
    private javax.swing.JLabel label_Color19;
    private javax.swing.JLabel label_Color20;
    private javax.swing.JLabel label_Color21;
    private javax.swing.JLabel label_Color4;
    private javax.swing.JLabel label_Color5;
    private javax.swing.JLabel label_Color6;
    private javax.swing.JLabel label_Color7;
    private javax.swing.JLabel label_Color8;
    private javax.swing.JLabel label_Color9;
    private javax.swing.JLabel label_ContentType0;
    private javax.swing.JLabel label_ContentType1;
    private javax.swing.JLabel label_ContentType4;
    private javax.swing.JLabel label_ContentType5;
    private javax.swing.JLabel label_Font0;
    private javax.swing.JLabel label_Font1;
    private javax.swing.JLabel label_Font4;
    private javax.swing.JLabel label_Font5;
    private javax.swing.JLabel label_MSG0;
    private javax.swing.JLabel label_MSG1;
    private javax.swing.JLabel label_MSG4;
    private javax.swing.JLabel label_MSG5;
    private javax.swing.JPanel ledNoticePanel;
    private javax.swing.JPanel ledNoticePanel0;
    private javax.swing.JPanel ledNoticePanel1;
    private javax.swing.JPanel ledNoticePanel2;
    private javax.swing.JPanel ledNoticePanel3;
    private javax.swing.JTabbedPane ledNoticePanelDefault;
    private javax.swing.JTabbedPane ledNoticePanelVehicle;
    private javax.swing.JTabbedPane ledNoticeTabbedPane;
    private javax.swing.JButton pauseTimeHelpButton0;
    private javax.swing.JButton pauseTimeHelpButton1;
    private javax.swing.JButton pauseTimeHelpButton2;
    private javax.swing.JButton startEffectHelpButton0;
    private javax.swing.JButton startEffectHelpButton1;
    private javax.swing.JButton startEffectHelpButton2;
    private javax.swing.JButton startEffectHelpButton3;
    private javax.swing.JTextField tf_VerbatimContent0;
    private javax.swing.JTextField tf_VerbatimContent1;
    private javax.swing.JTextField tf_VerbatimContent2;
    private javax.swing.JTextField tf_VerbatimContent3;
    private javax.swing.JButton useCkBoxHelpButton0;
    private javax.swing.JButton useCkBoxHelpButton1;
    private javax.swing.JButton useCkBoxHelpButton2;
    private javax.swing.JButton useCkBoxHelpButton3;
    private javax.swing.JCheckBox useLEDnoticeCkBox0;
    private javax.swing.JCheckBox useLEDnoticeCkBox1;
    private javax.swing.JCheckBox useLEDnoticeCkBox2;
    private javax.swing.JCheckBox useLEDnoticeCkBox3;
    private javax.swing.JPanel wholePanel1;
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
            changeEnabled_of_SaveCancelButtons(usage.ordinal(), true);
        } else {
            changeEnabled_of_SaveCancelButtons(usage.ordinal(), false);
        }    
    }    

    private void tryToCloseEBDSettingsForm() {
        if (formMode == OSP_enums.FormMode.MODIFICATION) {
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
                parent.setEBDsettings(null);
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
        ((JComboBox)componentMap.get("contentTypeBox" + usage.ordinal())).setEnabled(selected);
        ((JTextField)componentMap.get("tf_VerbatimContent" + usage.ordinal())).setEnabled(selected);
        
        ((JComboBox)componentMap.get("charColor" + usage.ordinal())).setEnabled(selected);
        ((JComboBox)componentMap.get("charFont" + usage.ordinal())).setEnabled(selected);
        
        if (usage == CAR_ENTRY_BOTTOM_ROW)
            return; 
        
        ((JComboBox)componentMap.get("combo_StartEffect" + usage.ordinal())).setEnabled(selected);
        ((JComboBox)componentMap.get("combo_PauseTime" + usage.ordinal())).setEnabled(selected);
        
        if (usage == EBD_DisplayUsage.DEFAULT_TOP_ROW) {
            if (ledNoticeSettings[DEFAULT_BOTTOM_ROW.ordinal()].isUsed) {
                combo_PauseTime0.setEnabled(false);
                combo_FinishEffect0.setEnabled(false);
            } else {
                combo_PauseTime0.setEnabled(true);
                combo_FinishEffect0.setEnabled(true);
            }
        } else {
            ((JComboBox)componentMap.get("combo_FinishEffect" + usage.ordinal())).setEnabled(selected);
        }
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
            changeEnabled_of_SaveCancelButtons(usage, false);
        } else {
            changeEnabled_of_SaveCancelButtons(usage, true);
        }
    }

    private void changeEnabled_of_SaveCancelButtons(int usage, boolean onOff) {
        JButton buton = (JButton) componentMap.get("btn_Save" + usage);
        buton.setEnabled(onOff);
        
        buton = (JButton) componentMap.get("btn_Cancel" + usage);
        buton.setEnabled(onOff);
        
        btn_Exit.setEnabled(!onOff);
    }

    private void checkVerbatimContentChangeButtonEnabled(int usage) {
        JTextField verbatimContent = (JTextField) getComponentByName("tf_VerbatimContent" + usage);
        String content = verbatimContent.getText().trim();
        
        if (content.equals(ledNoticeSettings[usage].verbatimContent)) {
            changeEnabled_of_SaveCancelButtons(usage, false);
        } else {
            changeEnabled_of_SaveCancelButtons(usage, true);
        }        
    }

    private void selectItemsForSelectedTab(int row, int column) {
        int ordinal = row + column * 2;
        
        JButton saveButton = (JButton)componentMap.get("btn_Save" + ordinal);
        if (saveButton != null && !saveButton.isEnabled()) {            
            JCheckBox usChkBox = (JCheckBox)componentMap.get("useLEDnoticeCkBox" + ordinal);
            usChkBox.setSelected(ledNoticeSettings[ordinal].isUsed);

            EBD_DisplayUsage usage = EBD_DisplayUsage.values()[ordinal];
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
        }
    }

    private void saveLEDnoticeSettingsTab(int usage) {
        JComboBox comboBox = null;
        Connection conn = null;
        PreparedStatement updateSettings = null;
        
        // 유형이 [문구 자체]인데, 문자열이 빈 문자열이면 경고 팝업창 띄움
        JTextField txtField = (JTextField) componentMap.get("tf_VerbatimContent" + usage);
        if (txtField.getText().trim().length() == 0) {
            JComboBox cmbBox = (JComboBox) componentMap.get("contentTypeBox" + usage);

            if (cmbBox.getSelectedIndex() == getVerbatimIndex(usage)) {
                JOptionPane.showMessageDialog(this, "[유형]이 '문구 자체'인 경우," + System.lineSeparator() +
                        "빈 [문자열]은 허용되지 않음!" );
                txtField.requestFocus();
                return; 
            }
        }
        
        forcedChangeOfStartEffectIfNeeded(usage);
        
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
            
            JCheckBox useCkBox = (JCheckBox)getComponentByName("useLEDnoticeCkBox" + usage);
            int isUsed = (useCkBox.isSelected() ? 1 : 0);
            updateSettings.setInt(pIndex++, isUsed);
            
            comboBox = (JComboBox)getComponentByName("contentTypeBox" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            String verbatimStr 
                    = ((JTextField) getComponentByName("tf_VerbatimContent" + usage)).getText().trim();
            updateSettings.setString(pIndex++, verbatimStr);
            
            comboBox = (JComboBox)getComponentByName("combo_StartEffect" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("combo_PauseTime" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("combo_FinishEffect" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("charColor" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            
            comboBox = (JComboBox)getComponentByName("charFont" + usage);
            updateSettings.setInt(pIndex++, comboBox.getSelectedIndex());
            updateSettings.setInt(pIndex++, usage);
            
            result = updateSettings.executeUpdate();
             
        } catch (SQLException ex) {
            Globals.logParkingException(Level.SEVERE, ex, "while saving LEDNotice settings");  
        } finally {
            closeDBstuff(conn, updateSettings, null, "LEDNotice settings modification");
            if (result == 1) {
                // reload global ledNotice settings variable
                readLEDnoticeSettings(ledNoticeSettings);
                changeEnabled_of_SaveCancelButtons(usage, false);
            } else {
                JOptionPane.showMessageDialog(this, "This LEDnotice settings update saving DB operation failed.",
                    "DB Update Operation Failure", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (mainForm != null) { // when settings frame invoked alone, main form is null
            if (usage == DEFAULT_TOP_ROW.ordinal() || usage == DEFAULT_BOTTOM_ROW.ordinal()) 
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

    private void cancelModification(int usage) {
        changeEnabled_of_SaveCancelButtons(usage, false);
        selectItemsForSelectedTab(usage % 2, usage / 2);
    }

    private void changeButtonEnabled_IfStartEffectChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_StartEffect" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].startEffectIdx) {
            changeEnabled_of_SaveCancelButtons(index, false);
        } else {
            changeEnabled_of_SaveCancelButtons(index, true);
        }        
    }

    private void changeButtonEnabled_IfPauseTimeChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_PauseTime" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].pauseTimeIdx) {
            changeEnabled_of_SaveCancelButtons(index, false);
        } else {
            changeEnabled_of_SaveCancelButtons(index, true);
        }        
    }

    private void changeButtonEnabled_IfFinishEffectChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("combo_FinishEffect" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].finishEffectIdx) {
            changeEnabled_of_SaveCancelButtons(index, false);
        } else {
            changeEnabled_of_SaveCancelButtons(index, true);
        }  
    }

    private void changeButtonEnabled_IfColorChanged(int index) {
        JComboBox comboBx = (JComboBox) getComponentByName("charColor" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].colorIdx) {
            changeEnabled_of_SaveCancelButtons(index, false);
        } else {
            changeEnabled_of_SaveCancelButtons(index, true);
        }  
    }

    private void changeButtonEnabled_IfFontChanged(int index) {
        
        JComboBox comboBx = (JComboBox) getComponentByName("charFont" + index);
        if (comboBx.getSelectedIndex() == ledNoticeSettings[index].fontIdx) {
            changeEnabled_of_SaveCancelButtons(index, false);
        } else {
            changeEnabled_of_SaveCancelButtons(index, true);
        }
    }

    private void checkStartEffectComboBoxValueChange(final int usage) {
        changeButtonEnabled_IfStartEffectChanged(usage);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                forcedChangeOfStartEffectIfNeeded(usage);
            }
        });     
    }

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
        JOptionPane.showMessageDialog(this, "차량 도착- 하단 행의 경우, " + System.lineSeparator()
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
}