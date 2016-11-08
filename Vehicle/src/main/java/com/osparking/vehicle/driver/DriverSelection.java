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
package com.osparking.vehicle.driver;

import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.centerCellRenderer;
import static com.osparking.global.CommonData.leftCellRenderer;
import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.putCellCenter;
import static com.osparking.global.CommonData.rejectNonNumericKeys;
import static com.osparking.global.CommonData.setKeyboardLanguage;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
import com.osparking.vehicle.VehiclesForm;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.sql.Connection;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.SetAColumnWidth;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.findLoginIdentity;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.loginID;
import com.osparking.global.IMainGUI;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SELECT_DRIVER_HELP;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.TitleTypes.DRIVER_SELECTION_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.*;
import static com.osparking.global.names.ControlEnums.TextType.*;
import static com.osparking.global.names.ControlEnums.TitleTypes.SELECT_BTN;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CELL_PHONE_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_INPUT_TOOLTIP;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import static com.osparking.vehicle.CommonData.prependEscape;
import static com.osparking.vehicle.CommonData.attachLikeCondition;
import com.osparking.vehicle.LabelBlinker;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DriverSelection extends javax.swing.JFrame {
    final static int NAME = 1;
    final static int CELL = 2;
    final static int PHONE = 3;
    final static int SEQ_NO = 4;
    
    VehiclesForm vehicleForm = null;
    int seqNo = 0;
    
    private boolean nameHintShown = true;
    private boolean cellHintShown = true;    
    private String prevSearchString = null;    
    private String currSearchString = null;       
    private List<String> prevKeyList = new ArrayList<String>();
    private List<String> currKeyList = new ArrayList<String>();    
    IMainGUI mainForm = null;
    
    /**
     * Creates new form DriverSelection
     */
    public DriverSelection(VehiclesForm vehicleForm, IMainGUI mainForm, int seqNo) {
        this.vehicleForm = vehicleForm;
        this.mainForm = mainForm;
        if (vehicleForm != null)
            vehicleForm.setEnabled(false);
        this.seqNo = seqNo;
        initComponents();
        setIconImages(OSPiconList);
        
        adjustSkinnyTable();
        currSearchString = formSearchString(currKeyList);
        fixColumnAlignment();
        loadSkinnyDriverTable(seqNo);
        attachEnterHandler(searchName);
        attachEnterHandler(searchCell); 
        skinnyDriverTable.getSelectionModel().addListSelectionListener(
                new DriverSelectionListener());
        (new LabelBlinker()).displayHelpMessage(csHelpText, 
                SELECT_DRIVER_HELP.getContent(), true);  
        
        searchName.getInputContext().selectInputMethod(Locale.KOREA);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        westPanel = new javax.swing.JPanel();
        northPanel = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        titlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        csHelpPanel = new javax.swing.JPanel();
        csHelpText = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        topButtonPanel = new javax.swing.JPanel();
        fixDriverButton = new javax.swing.JButton();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        searchButton = new javax.swing.JButton();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        searchPanel = new javax.swing.JPanel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        jLabel3 = new javax.swing.JLabel();
        searchName = new javax.swing.JTextField();
        searchCell = new javax.swing.JTextField();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        driversPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        skinnyDriverTable = new javax.swing.JTable();
        eastPanel = new javax.swing.JPanel();
        southPanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        bottomButtonPanel = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        manageDriversButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(DRIVER_SELECTION_FRAME_TITLE.getContent());
        setMaximumSize(new java.awt.Dimension(550, 571));
        setMinimumSize(new java.awt.Dimension(410, 571));
        setPreferredSize(new java.awt.Dimension(410, 571));
        setResizable(false);
        setSize(new java.awt.Dimension(410, 571));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        westPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        westPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

        northPanel.setMaximumSize(new java.awt.Dimension(550, 211));
        northPanel.setMinimumSize(new java.awt.Dimension(400, 211));
        northPanel.setPreferredSize(new java.awt.Dimension(400, 211));
        northPanel.setLayout(new javax.swing.BoxLayout(northPanel, javax.swing.BoxLayout.Y_AXIS));
        northPanel.add(filler5);

        titlePanel.setMaximumSize(new java.awt.Dimension(32767, 30));
        titlePanel.setMinimumSize(new java.awt.Dimension(120, 30));
        titlePanel.setPreferredSize(new java.awt.Dimension(320, 30));
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(DRIVER_SELECTION_FRAME_TITLE.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(120, 28));
        jLabel1.setMinimumSize(new java.awt.Dimension(120, 28));
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 28));
        titlePanel.add(jLabel1);

        northPanel.add(titlePanel);

        csHelpPanel.setPreferredSize(new java.awt.Dimension(320, 40));

        csHelpText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        csHelpText.setForeground(Color.gray);
        csHelpText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        csHelpText.setText("OWNER_SELECTION_HELP_MSG.getContent()");
        csHelpText.setPreferredSize(new java.awt.Dimension(46, 28));

        javax.swing.GroupLayout csHelpPanelLayout = new javax.swing.GroupLayout(csHelpPanel);
        csHelpPanel.setLayout(csHelpPanelLayout);
        csHelpPanelLayout.setHorizontalGroup(
            csHelpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(csHelpText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );
        csHelpPanelLayout.setVerticalGroup(
            csHelpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(csHelpPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(csHelpText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        northPanel.add(csHelpPanel);
        northPanel.add(filler3);

        topButtonPanel.setMaximumSize(new java.awt.Dimension(550, 45));
        topButtonPanel.setMinimumSize(new java.awt.Dimension(400, 45));
        topButtonPanel.setPreferredSize(new java.awt.Dimension(400, 45));
        topButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        fixDriverButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        fixDriverButton.setMnemonic('t');
        fixDriverButton.setText(SELECT_BTN.getContent());
        fixDriverButton.setEnabled(false);
        fixDriverButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        fixDriverButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        fixDriverButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        fixDriverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixDriverButtonActionPerformed(evt);
            }
        });
        fixDriverButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fixDriverButtonKeyReleased(evt);
            }
        });
        topButtonPanel.add(fixDriverButton);
        topButtonPanel.add(filler22);

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
        searchButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchButtonKeyReleased(evt);
            }
        });
        topButtonPanel.add(searchButton);
        topButtonPanel.add(filler23);

        northPanel.add(topButtonPanel);
        northPanel.add(filler4);

        searchPanel.setMaximumSize(new java.awt.Dimension(550, 28));
        searchPanel.setMinimumSize(new java.awt.Dimension(400, 28));
        searchPanel.setPreferredSize(new java.awt.Dimension(400, 28));
        searchPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        searchPanel.add(filler8);

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(SEARCH_LABEL.getContent());
        jLabel3.setMaximumSize(new Dimension(rnMax, 28));
        jLabel3.setMinimumSize(new Dimension(rnPref, 28));
        jLabel3.setPreferredSize(new Dimension(rnPref, 28));
        searchPanel.add(jLabel3);

        searchName.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchName.setForeground(tipColor);
        searchName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        searchName.setText(DRIVER_TF.getContent());
        searchName.setToolTipText(DRIVER_INPUT_TOOLTIP.getContent());
        searchName.setMaximumSize(new Dimension(nmMax, 28));
        searchName.setMinimumSize(new Dimension(nmPref, 28));
        searchName.setPreferredSize(new Dimension(nmPref, 28));
        searchName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchNameFocusLost(evt);
            }
        });
        searchName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchNameMousePressed(evt);
            }
        });
        searchName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchNameKeyReleased(evt);
            }
        });
        searchPanel.add(searchName);

        searchCell.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCell.setForeground(tipColor);
        searchCell.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        searchCell.setText(CELL_PHONE_TF.getContent());
        searchCell.setToolTipText(CELL_PHONE_INPUT_TOOLTIP.getContent());
        searchCell.setMaximumSize(new Dimension(celMax, 28));
        searchCell.setMinimumSize(new Dimension(celPref, 28));
        searchCell.setPreferredSize(new Dimension(celPref-7, 28));
        searchCell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchCellFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchCellFocusLost(evt);
            }
        });
        searchCell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchCellMousePressed(evt);
            }
        });
        searchCell.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchCellKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchCellKeyTyped(evt);
            }
        });
        searchPanel.add(searchCell);
        searchPanel.add(filler9);

        northPanel.add(searchPanel);

        getContentPane().add(northPanel, java.awt.BorderLayout.NORTH);

        driversPanel.setMaximumSize(new java.awt.Dimension(1470, 32767));
        driversPanel.setMinimumSize(new java.awt.Dimension(320, 300));
        driversPanel.setPreferredSize(new java.awt.Dimension(320, 300));
        driversPanel.setLayout(new javax.swing.BoxLayout(driversPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setMaximumSize(new java.awt.Dimension(470, 1000));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(320, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(320, 300));

        skinnyDriverTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        skinnyDriverTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        skinnyDriverTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                ORDER_HEADER.getContent(),
                NAME_HEADER.getContent(),
                CELL_PHONE_HEADER.getContent(),
                "Phone No.", "SEQ_NO"
            })
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            }
        );
        ((DefaultTableCellRenderer)skinnyDriverTable.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
        skinnyDriverTable.setRowHeight(tableRowHeight);
        skinnyDriverTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                skinnyDriverTableMouseClicked(evt);
            }
        });
        skinnyDriverTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                skinnyDriverTableKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(skinnyDriverTable);

        driversPanel.add(jScrollPane1);

        getContentPane().add(driversPanel, java.awt.BorderLayout.CENTER);

        eastPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        eastPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.Y_AXIS));
        southPanel.add(filler2);

        bottomButtonPanel.setMinimumSize(new java.awt.Dimension(302, 25));
        bottomButtonPanel.setPreferredSize(new java.awt.Dimension(320, 25));
        bottomButtonPanel.setLayout(new javax.swing.BoxLayout(bottomButtonPanel, javax.swing.BoxLayout.X_AXIS));
        bottomButtonPanel.add(filler6);

        manageDriversButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        manageDriversButton.setMnemonic('m');
        manageDriversButton.setText(MANAGE_BTN.getContent());
        manageDriversButton.setMaximumSize(new Dimension(buttonWidthNorm + 45, buttonHeightNorm));
        manageDriversButton.setMinimumSize(new Dimension(buttonWidthNorm + 30, buttonHeightNorm));
        manageDriversButton.setPreferredSize(new Dimension(buttonWidthNorm + 45, buttonHeightNorm));
        manageDriversButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageDriversButtonActionPerformed(evt);
            }
        });
        manageDriversButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                manageDriversButtonKeyReleased(evt);
            }
        });
        bottomButtonPanel.add(manageDriversButton);
        bottomButtonPanel.add(filler1);

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
        closeFormButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                closeFormButtonKeyReleased(evt);
            }
        });
        bottomButtonPanel.add(closeFormButton);
        bottomButtonPanel.add(filler7);

        southPanel.add(bottomButtonPanel);
        southPanel.add(filler10);

        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadSkinnyDriverTable(seqNo);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void skinnyDriverTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_skinnyDriverTableMouseClicked
        if (evt.getClickCount() == 2) {
            // change parent form and close this form itself
            assignDriverToVehicle();
        }
    }//GEN-LAST:event_skinnyDriverTableMouseClicked

    private void skinnyDriverTableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_skinnyDriverTableKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            assignDriverToVehicle();
        }
    }//GEN-LAST:event_skinnyDriverTableKeyTyped

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (vehicleForm != null){
            vehicleForm.setEnabled(true);
            vehicleForm.requestFocus();
        }
    }//GEN-LAST:event_formWindowClosing

    private void manageDriversButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageDriversButtonActionPerformed
        if (loginID != null ||
                loginID == null && findLoginIdentity() != null) 
        {
            CarOwners driverManageForm = new CarOwners(mainForm, this);
            driverManageForm.setVisible(true);
        }
    }//GEN-LAST:event_manageDriversButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        if (vehicleForm != null){
            vehicleForm.setEnabled(true);
            vehicleForm.requestFocus();
        }
        dispose();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void fixDriverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixDriverButtonActionPerformed
        assignDriverToVehicle();
    }//GEN-LAST:event_fixDriverButtonActionPerformed

    private void manageDriversButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_manageDriversButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            CarOwners driverManageForm = new CarOwners(mainForm, this);
            driverManageForm.setVisible(true);
        }
    }//GEN-LAST:event_manageDriversButtonKeyReleased

    private void searchButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            loadSkinnyDriverTable(seqNo);        
        }
    }//GEN-LAST:event_searchButtonKeyReleased

    private void closeFormButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeFormButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            if (vehicleForm != null){
                vehicleForm.setEnabled(true);
                vehicleForm.requestFocus();
            }
            dispose();            
        }
    }//GEN-LAST:event_closeFormButtonKeyReleased

    private void fixDriverButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fixDriverButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            assignDriverToVehicle();
        }        
    }//GEN-LAST:event_fixDriverButtonKeyReleased

    private void searchNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusLost
        if (searchName.getText().trim().length() == 0) {
            searchName.setText(DRIVER_TF.getContent());
            nameHintShown = true;
            searchName.setForeground(tipColor);
        }          
    }//GEN-LAST:event_searchNameFocusLost

    private void searchCellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusLost
        if (searchCell.getText().trim().length() == 0) {
            searchCell.setText(CELL_PHONE_TF.getContent());
            cellHintShown = true;
            searchCell.setForeground(tipColor);
        }           
    }//GEN-LAST:event_searchCellFocusLost

    private void searchNameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchNameMousePressed
        searchName.selectAll();
    }//GEN-LAST:event_searchNameMousePressed

    private void searchCellMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchCellMousePressed
        searchCell.selectAll();
    }//GEN-LAST:event_searchCellMousePressed

    private void searchNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusGained
        setKeyboardLanguage(searchName, KOREAN);

        if (nameHintShown) {
            searchName.setText("");
            nameHintShown = false;            
            searchName.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchNameFocusGained

    private void searchCellFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusGained
        if (searchCell.getText().equals(CELL_PHONE_TF.getContent())) {
            searchCell.setText("");
            cellHintShown = false;            
            searchCell.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchCellFocusGained

    private void searchCellKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCellKeyTyped
        rejectNonNumericKeys(evt);       
    }//GEN-LAST:event_searchCellKeyTyped

    /**
     * Used to detect a partial input of Korean alphabet and allow the "searchButton" enabled.
     * Without it, Koreans might be ansious about the delay of the button becoming usable.
     */
    private void searchNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNameKeyReleased
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });        
    }//GEN-LAST:event_searchNameKeyReleased

    private void searchCellKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCellKeyReleased
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        }); 
    }//GEN-LAST:event_searchCellKeyReleased

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
            java.util.logging.Logger.getLogger(DriverSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DriverSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DriverSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DriverSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DriverSelection(null, null, 0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomButtonPanel;
    public javax.swing.JButton closeFormButton;
    private javax.swing.JPanel csHelpPanel;
    private javax.swing.JLabel csHelpText;
    private javax.swing.JPanel driversPanel;
    private javax.swing.JPanel eastPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    public javax.swing.JButton fixDriverButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton manageDriversButton;
    private javax.swing.JPanel northPanel;
    public javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCell;
    private javax.swing.JTextField searchName;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTable skinnyDriverTable;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel topButtonPanel;
    private javax.swing.JPanel westPanel;
    // End of variables declaration//GEN-END:variables

    public void loadSkinnyDriverTable(int seqNo) {
        
        Connection conn = null;
        PreparedStatement fetchDrivers = null;
        ResultSet rs = null;
        DefaultTableModel model = null;
        
        String excepMsg = "(While loading vehicle driver list)";
        int highlightRow = -1;
        int currentRow = 0;   
            
        try {
            // <editor-fold defaultstate="collapsed" desc="-- load car driver list">                                      
            StringBuilder sb = new StringBuilder(); 
            
            sb.append("SELECT @recNo := @recNo + 1 recNo, A.* ");
            sb.append("From (select name, CELLPHONE, Phone, SEQ_NO ");
            sb.append("  from cardriver order by NAME, CELLPHONE) A, ");
            sb.append("  (SELECT @recNo := 0) tmp ");
            
            prevSearchString = currSearchString;
            prevKeyList = currKeyList;            
            if (currSearchString.length() > 0) {
                sb.append(" Where " + currSearchString);
            } 
            
            conn = getConnection();
            fetchDrivers = conn.prepareStatement(sb.toString());   

            int index = 1;
            for (String searchKey : currKeyList) {
                fetchDrivers.setString(index++, "%" + prependEscape(searchKey) + "%");  
            }            
            
            rs = fetchDrivers.executeQuery();
            
            model = (DefaultTableModel) skinnyDriverTable.getModel();  
            model.setRowCount(0);
            while (rs.next()) {               
                //<editor-fold defaultstate="collapsed" desc="-- construct a skinny driver info'"> 
                model.addRow(new Object[] {
                     rs.getInt("recNo"),   rs.getString("name"), rs.getString("CELLPHONE"),
                     rs.getString("PHONE"), rs.getInt("SEQ_NO")
                });
                // non-zero seqNo means highlight the driver with the sequence number
                if (seqNo != 0 && seqNo == (Integer)rs.getInt("SEQ_NO")) {
                    highlightRow = currentRow;
                }
                currentRow++;
                //</editor-fold>
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, fetchDrivers, rs, excepMsg);
        }
        if (highlightRow != -1 && model.getRowCount() > 0) {
            skinnyDriverTable.setRowSelectionInterval(highlightRow, highlightRow);
            skinnyDriverTable.requestFocus();
            fixDriverButton.setEnabled(true);
        } else {
            fixDriverButton.setEnabled(false);
        }
        searchButton.setEnabled(false);
    }

    final int rnPref = 70;
    final int rnMax = (int)(rnPref * 1.5);
    
    final int nmPref = 90;
    final int nmMax = (int)(nmPref * 1.5);
    
    final int celPref = 170;
    final int celMax = (int)(celPref * 1.5);
    
    private void adjustSkinnyTable() {
        // Hide drivers table sequence number which is used by only inside the code
        TableColumnModel skinnyModel = skinnyDriverTable.getColumnModel();
        
        skinnyModel.getColumn(0).setCellRenderer(putCellCenter);

        // <editor-fold defaultstate="collapsezd" desc="-- Adjust Column Width ">                    
        SetAColumnWidth(skinnyModel.getColumn(0), rnPref, rnPref, rnMax); // 0: row number
        SetAColumnWidth(skinnyModel.getColumn(NAME), nmPref, nmPref, nmMax); // 1: driver name
        SetAColumnWidth(skinnyModel.getColumn(CELL), celPref, celPref, celMax); // 2: cell phone
        //</editor-fold>        
        // <editor-fold defaultstate="collapsed" desc="-- Hide Some Columns">                         
        skinnyModel.removeColumn(skinnyModel.getColumn(SEQ_NO));
        skinnyModel.removeColumn(skinnyModel.getColumn(PHONE));
        //</editor-fold>        
        skinnyDriverTable.setSelectionMode(
                ListSelectionModel.SINGLE_INTERVAL_SELECTION);  
        
        // <editor-fold defaultstate="collapsed" desc="-- Adjust Column Width ">        
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                assignDriverToVehicle();
            }
        };
        JComponent compo = (JComponent) skinnyDriverTable;
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);        
        //</editor-fold>                
    }

    private void attachEnterHandler(JComponent compo) {
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                loadSkinnyDriverTable(seqNo);
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);
    }    
    
    private String formSearchString(List<String> keyList) {
        StringBuffer cond = new StringBuffer();
        String searchStr = searchName.getText().trim();

        if (!nameHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "name", searchStr.length());
            keyList.add(searchStr);            
        }
        
        searchStr = searchCell.getText().trim();
        if (!cellHintShown && searchStr.length() > 0) {
            attachLikeCondition(cond, "cellphone", searchStr.length());
            keyList.add(searchStr);            
        }
        
        return cond.toString();
    }

    private void assignDriverToVehicle() {
        setVisible(false);
        dispose();    
        
        if (vehicleForm != null) {
            int row = skinnyDriverTable.getSelectedRow();
            String cell = (String) skinnyDriverTable.getModel().getValueAt(row, CELL);
            String phone = (String) skinnyDriverTable.getModel().getValueAt(row, PHONE);
            int seqNo = (Integer) skinnyDriverTable.getModel().getValueAt(row, SEQ_NO);
            String name = (String)skinnyDriverTable.getModel().getValueAt(row, NAME);
            vehicleForm.setDriverInfo(name, cell, phone, seqNo);
            vehicleForm.setEnabled(true);
            vehicleForm.requestFocus();
        }
    }

    private void changeSearchButtonEnabled() {
        currKeyList = new ArrayList<String>();
        currSearchString = formSearchString(currKeyList);
        if (currSearchString.equals(prevSearchString)
                && currKeyList.equals(prevKeyList)) 
        {
            searchButton.setEnabled(false);
        } else {
            searchButton.setEnabled(true);
        }        
    }

    private void fixColumnAlignment() {
        TableColumnModel colModel = skinnyDriverTable.getColumnModel();
        colModel.getColumn(0).setCellRenderer(numberCellRenderer);
        colModel.getColumn(1).setCellRenderer(centerCellRenderer);
        colModel.getColumn(2).setCellRenderer(leftCellRenderer);
    }

    private class DriverSelectionListener implements ListSelectionListener {

        public DriverSelectionListener() {
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            } else {
                int index = skinnyDriverTable.getSelectedRow();
                if (index >= 0) {
                    fixDriverButton.setEnabled(true);
                }
            }
        }
    }
}
