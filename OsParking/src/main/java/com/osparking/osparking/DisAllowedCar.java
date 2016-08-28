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

import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.Globals.CONTENT_INC;
import static com.osparking.global.Globals.LABEL_INC;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import static com.osparking.global.Globals.getTopLeftPointToPutThisFrameAtScreenCenter;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.initializeLoggers;
import com.osparking.global.names.ControlEnums.BarOperation;
import static com.osparking.global.names.ControlEnums.LabelContent.BlinkNotPermitted;
import static com.osparking.global.names.ControlEnums.LabelContent.DISALLOW_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DisallowReason;
import static com.osparking.global.names.ControlEnums.LabelContent.EMPTY_REASON;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.OPEN_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.RecogedTagLabel;
import static com.osparking.global.names.ControlEnums.LabelContent.RegisteredTagLabel;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import com.osparking.global.names.ControlEnums.TitleTypes;
import static com.osparking.global.names.DB_Access.gateNames;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DisAllowedCar extends javax.swing.JFrame {

    Toolkit toolkit;
    Timer timer;
    ControlGUI parent = null;
    byte gateNo;
    int imageSN;
    String tagRecognized;
    Date arrivalTm;
    String tagEnteredAs;
    String filename = null;
    BufferedImage bImg = null;
    int delay;
    
    /**
     * Constructor of a window frame for a registered vehicle but parking is not allowed.
     * 
     * @param parent parent window that opened this window
     * @param tagRecognized tag number recognized by a image processing software
     * @param arrivalTm vehicle arrival time at the gate
     * @param tagEnteredAs tag number stored in the registered vehicle database table
     * @param remark description why the car is disallowed to park
     * @param gateNo gate ID where the car arrived
     * @param filename 
     * @param filenameModified
     * @param bImg
     * @param delay 
     */
    public DisAllowedCar(ControlGUI parent, String tagRecognized, Date arrivalTm, 
            String tagEnteredAs, String remark, byte gateNo, int imageSN, 
            BufferedImage bImg, int delay) 
    {
        initComponents();
        this.parent = parent;
        this.tagRecognized = tagRecognized;
        this.arrivalTm = arrivalTm;
        this.tagEnteredAs = tagEnteredAs;
        this.gateNo = gateNo;
        this.imageSN = imageSN; 
        this.bImg = bImg;
        this.delay = delay;
        
        setIconImages(OSPiconList);
        Point screenCenter = getTopLeftPointToPutThisFrameAtScreenCenter(this);
        setLocation(screenCenter);        
        
        recogTextField.setText(tagRecognized);
        regisTextField.setText(tagEnteredAs);
        if (remark.length() == 0) {
            disAllowReasonTextField.setForeground(tipColor);
            disAllowReasonTextField.setText(EMPTY_REASON.getContent());
        } else {
            disAllowReasonTextField.setForeground(new Color(0, 0, 0));
            disAllowReasonTextField.setText(remark);
        }
        toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        timer.schedule(new RemindTask(), 0, //initial delay
        1 * 1000);   
        
        gateNameTextField.setText(gateNames[gateNo]);
        addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                openBarButton.requestFocus();
            }
        });  
    }
    
    class RemindTask extends TimerTask {
        public void run() {
            toolkit.beep();
            if (WarningSignTBox.getForeground() == Color.red)
                WarningSignTBox.setForeground(WarningSignTBox.getBackground());
            else 
                WarningSignTBox.setForeground(Color.red);
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

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(30, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(30, 32767));
        wholePanel = new javax.swing.JPanel();
        firstPanel = new javax.swing.JPanel();
        metaPanel = new javax.swing.JPanel();
        myMetaKeyLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        gateNameTextField = new javax.swing.JTextField();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        tag_Reco_Panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        recogTextField = new javax.swing.JTextField();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        tag_Regi_Panel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        regisTextField = new javax.swing.JTextField();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        warnningPanel = new javax.swing.JPanel();
        WarningSignTBox = new javax.swing.JLabel();
        reasonPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        disallowReasonPanel = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        disAllowReasonTextField = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        buttonPanel = new javax.swing.JPanel();
        openBarButton = new javax.swing.JButton();
        closeGateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TitleTypes.DisallowedTitle.getContent());
        setMaximumSize(new java.awt.Dimension(2147483647, 485));
        setMinimumSize(new java.awt.Dimension(500, 485));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().add(filler1, java.awt.BorderLayout.NORTH);
        getContentPane().add(filler2, java.awt.BorderLayout.SOUTH);
        getContentPane().add(filler3, java.awt.BorderLayout.EAST);
        getContentPane().add(filler4, java.awt.BorderLayout.WEST);

        wholePanel.setMinimumSize(new java.awt.Dimension(420, 405));
        wholePanel.setPreferredSize(new java.awt.Dimension(420, 405));
        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        firstPanel.setLayout(new javax.swing.BoxLayout(firstPanel, javax.swing.BoxLayout.PAGE_AXIS));

        metaPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        metaPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        metaPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        metaPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setPreferredSize(new java.awt.Dimension(90, 40));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        metaPanel.add(myMetaKeyLabel);

        firstPanel.add(metaPanel);

        jPanel1.setMaximumSize(new java.awt.Dimension(33127, 60));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(GATE_NAME_LABEL.getContent());
        jLabel3.setMaximumSize(new java.awt.Dimension(190, 40));
        jLabel3.setMinimumSize(new java.awt.Dimension(190, 40));
        jLabel3.setPreferredSize(new java.awt.Dimension(190, 40));
        jPanel1.add(jLabel3);
        jPanel1.add(filler18);

        gateNameTextField.setEditable(false);
        gateNameTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        gateNameTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gateNameTextField.setText("jTextField1");
        gateNameTextField.setMaximumSize(new java.awt.Dimension(160, 40));
        gateNameTextField.setMinimumSize(new java.awt.Dimension(160, 40));
        gateNameTextField.setPreferredSize(new java.awt.Dimension(160, 40));
        jPanel1.add(gateNameTextField);
        jPanel1.add(filler26);

        firstPanel.add(jPanel1);

        tag_Reco_Panel.setMaximumSize(new java.awt.Dimension(33127, 60));
        tag_Reco_Panel.setLayout(new javax.swing.BoxLayout(tag_Reco_Panel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText(RecogedTagLabel.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(190, 40));
        jLabel1.setMinimumSize(new java.awt.Dimension(190, 40));
        jLabel1.setPreferredSize(new java.awt.Dimension(190, 40));
        tag_Reco_Panel.add(jLabel1);
        tag_Reco_Panel.add(filler11);

        recogTextField.setEditable(false);
        recogTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        recogTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        recogTextField.setText("30모8186");
        recogTextField.setMaximumSize(new java.awt.Dimension(160, 40));
        recogTextField.setMinimumSize(new java.awt.Dimension(160, 40));
        recogTextField.setPreferredSize(new java.awt.Dimension(160, 40));
        tag_Reco_Panel.add(recogTextField);
        tag_Reco_Panel.add(filler19);

        firstPanel.add(tag_Reco_Panel);

        tag_Regi_Panel.setMaximumSize(new java.awt.Dimension(33127, 60));
        tag_Regi_Panel.setLayout(new javax.swing.BoxLayout(tag_Regi_Panel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel2.setForeground(java.awt.Color.gray);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText(RegisteredTagLabel.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(190, 40));
        jLabel2.setMinimumSize(new java.awt.Dimension(190, 40));
        jLabel2.setPreferredSize(new java.awt.Dimension(190, 40));
        tag_Regi_Panel.add(jLabel2);
        tag_Regi_Panel.add(filler12);

        regisTextField.setEditable(false);
        regisTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        regisTextField.setForeground(java.awt.Color.gray);
        regisTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        regisTextField.setText("30모8186");
        regisTextField.setMaximumSize(new java.awt.Dimension(160, 40));
        regisTextField.setMinimumSize(new java.awt.Dimension(160, 40));
        regisTextField.setPreferredSize(new java.awt.Dimension(160, 40));
        tag_Regi_Panel.add(regisTextField);
        tag_Regi_Panel.add(filler22);

        firstPanel.add(tag_Regi_Panel);

        warnningPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        warnningPanel.setMinimumSize(new java.awt.Dimension(90, 40));
        warnningPanel.setPreferredSize(new java.awt.Dimension(133, 40));
        warnningPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        WarningSignTBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        WarningSignTBox.setForeground(new java.awt.Color(255, 0, 0));
        WarningSignTBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WarningSignTBox.setText(BlinkNotPermitted.getContent());
        WarningSignTBox.setMaximumSize(new java.awt.Dimension(2147483647, 50));
        WarningSignTBox.setMinimumSize(new java.awt.Dimension(90, 40));
        WarningSignTBox.setPreferredSize(new java.awt.Dimension(400, 40));
        warnningPanel.add(WarningSignTBox);

        firstPanel.add(warnningPanel);

        reasonPanel.setMaximumSize(new java.awt.Dimension(400, 40));
        reasonPanel.setMinimumSize(new java.awt.Dimension(400, 40));
        reasonPanel.setPreferredSize(new java.awt.Dimension(400, 40));
        reasonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText(DisallowReason.getContent());
        jLabel4.setMaximumSize(new java.awt.Dimension(400, 40));
        jLabel4.setMinimumSize(new java.awt.Dimension(400, 40));
        jLabel4.setPreferredSize(new java.awt.Dimension(400, 40));
        reasonPanel.add(jLabel4);

        firstPanel.add(reasonPanel);

        disallowReasonPanel.setMaximumSize(new java.awt.Dimension(400, 40));
        disallowReasonPanel.setMinimumSize(new java.awt.Dimension(400, 40));
        disallowReasonPanel.setPreferredSize(new java.awt.Dimension(400, 40));

        disAllowReasonTextField.setEditable(false);
        disAllowReasonTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        disAllowReasonTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        disAllowReasonTextField.setText("012345678901234567890123456789");
        disAllowReasonTextField.setMargin(new java.awt.Insets(0, 10, 0, 0));
        disAllowReasonTextField.setMaximumSize(new java.awt.Dimension(340, 40));
        disAllowReasonTextField.setMinimumSize(new java.awt.Dimension(340, 40));
        disAllowReasonTextField.setPreferredSize(new java.awt.Dimension(340, 40));

        javax.swing.GroupLayout disallowReasonPanelLayout = new javax.swing.GroupLayout(disallowReasonPanel);
        disallowReasonPanel.setLayout(disallowReasonPanelLayout);
        disallowReasonPanelLayout.setHorizontalGroup(
            disallowReasonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(disallowReasonPanelLayout.createSequentialGroup()
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disAllowReasonTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
        );
        disallowReasonPanelLayout.setVerticalGroup(
            disallowReasonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(disallowReasonPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(filler6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(disAllowReasonTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        firstPanel.add(disallowReasonPanel);
        firstPanel.add(filler5);

        buttonPanel.setMaximumSize(new java.awt.Dimension(450, 60));
        buttonPanel.setMinimumSize(new java.awt.Dimension(450, 60));
        buttonPanel.setPreferredSize(new java.awt.Dimension(450, 60));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        openBarButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        openBarButton.setMnemonic('P');
        openBarButton.setText(OPEN_LABEL.getContent());
        openBarButton.setMaximumSize(new java.awt.Dimension(160, 60));
        openBarButton.setMinimumSize(new java.awt.Dimension(160, 60));
        openBarButton.setNextFocusableComponent(closeGateButton);
        openBarButton.setPreferredSize(new java.awt.Dimension(160, 60));
        openBarButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                openBarButtonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                openBarButtonFocusLost(evt);
            }
        });
        openBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBarButtonActionPerformed(evt);
            }
        });
        openBarButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                openBarButtonKeyTyped(evt);
            }
        });
        buttonPanel.add(openBarButton);

        closeGateButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        closeGateButton.setMnemonic('c');
        closeGateButton.setText(DISALLOW_LABEL.getContent());
        closeGateButton.setMaximumSize(new java.awt.Dimension(160, 60));
        closeGateButton.setMinimumSize(new java.awt.Dimension(160, 60));
        closeGateButton.setNextFocusableComponent(openBarButton);
        closeGateButton.setPreferredSize(new java.awt.Dimension(160, 60));
        closeGateButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                closeGateButtonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                closeGateButtonFocusLost(evt);
            }
        });
        closeGateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeGateButtonActionPerformed(evt);
            }
        });
        closeGateButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                closeGateButtonKeyTyped(evt);
            }
        });
        buttonPanel.add(closeGateButton);

        firstPanel.add(buttonPanel);

        wholePanel.add(firstPanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(517, 531));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void openBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBarButtonActionPerformed
        if(parent != null){
            parent.raiseGateBar(gateNo, imageSN, delay);
                
//            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTm, tagRecognized, tagEnteredAs,
//                    bImg, -1, -1, null, BarOperation.MANUAL);
//            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, BarOperation.MANUAL);
            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTm, tagRecognized, tagEnteredAs,
                    bImg, -1, -1, null, BarOperation.MANUAL);
            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, BarOperation.STOPPED);
            parent.isGateBusy[gateNo] = false;
        }
        timer.cancel();
        timer.purge();
        dispose();
    }//GEN-LAST:event_openBarButtonActionPerformed

    private void closeGateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeGateButtonActionPerformed
        if(parent != null){  
            
            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTm, tagRecognized, tagEnteredAs,
                    bImg,  -1, -1, null, BarOperation.REMAIN_CLOSED);        
            parent.isGateBusy[gateNo] = false;
            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, BarOperation.REMAIN_CLOSED);
        }
        timer.cancel();
        timer.purge();
        dispose();
    }//GEN-LAST:event_closeGateButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(parent != null){
            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTm, tagRecognized, tagEnteredAs,
                    bImg, -1, -1, null, BarOperation.REMAIN_CLOSED);   
            parent.isGateBusy[gateNo] = false;
            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, BarOperation.REMAIN_CLOSED);        
        }
        timer.cancel();
        timer.purge();
    }//GEN-LAST:event_formWindowClosing

    private void closeGateButtonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_closeGateButtonFocusGained
        closeGateButton.setBackground((new java.awt.Color(102, 255, 102)));
    }//GEN-LAST:event_closeGateButtonFocusGained

    private void openBarButtonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_openBarButtonFocusGained
        openBarButton.setBackground((new java.awt.Color(102, 255, 102)));
    }//GEN-LAST:event_openBarButtonFocusGained

    private void openBarButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_openBarButtonFocusLost
        openBarButton.setBackground((new java.awt.Color(240, 240, 240)));
    }//GEN-LAST:event_openBarButtonFocusLost

    private void closeGateButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_closeGateButtonFocusLost
        closeGateButton.setBackground((new java.awt.Color(240, 240, 240)));
    }//GEN-LAST:event_closeGateButtonFocusLost

    private void openBarButtonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_openBarButtonKeyTyped
        if(evt.getKeyChar() == KeyEvent.VK_ENTER)
        {
            openBarButtonActionPerformed(null);
        }
    }//GEN-LAST:event_openBarButtonKeyTyped

    private void closeGateButtonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeGateButtonKeyTyped
        if(evt.getKeyChar() == KeyEvent.VK_ENTER)
        {
            closeGateButtonActionPerformed(null);
        }
    }//GEN-LAST:event_closeGateButtonKeyTyped

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
            java.util.logging.Logger.getLogger(DisAllowedCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DisAllowedCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DisAllowedCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DisAllowedCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DisAllowedCar(null, "30모8186", new Date(), "30모8186", "testing",
                    (byte)1, 1000000, null, 8000).setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel WarningSignTBox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeGateButton;
    private javax.swing.JTextField disAllowReasonTextField;
    private javax.swing.JPanel disallowReasonPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler26;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JPanel firstPanel;
    private javax.swing.JTextField gateNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel metaPanel;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JButton openBarButton;
    private javax.swing.JPanel reasonPanel;
    private javax.swing.JTextField recogTextField;
    private javax.swing.JTextField regisTextField;
    private javax.swing.JPanel tag_Reco_Panel;
    private javax.swing.JPanel tag_Regi_Panel;
    private javax.swing.JPanel warnningPanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
}
