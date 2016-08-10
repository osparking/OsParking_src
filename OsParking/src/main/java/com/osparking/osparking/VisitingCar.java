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

import static com.osparking.global.CommonData.NOT_LISTED;
import static com.osparking.global.CommonData.bigButtonHeight;
import static com.osparking.global.CommonData.bigButtonWidth;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.tipColor;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.CONTENT_INC;
import static com.osparking.global.Globals.LABEL_INC;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.getTopLeftPointToPutThisFrameAtScreenCenter;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getPrompter;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.loadComboBoxItems;
import static com.osparking.global.Globals.refreshComboBox;
import com.osparking.global.names.ControlEnums.BarOperation;
import static com.osparking.global.names.ControlEnums.LabelContent.DISALLOW_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.DisallowReason;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.OPEN_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_OVERVIEW;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_REASON_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_TARGET_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_TIME_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILDING_HEADER_SC;
import static com.osparking.global.names.ControlEnums.TableTypes.CAR_TAG_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER_SC;
import static com.osparking.global.names.ControlEnums.TableTypes.LOWER_HEADER_SC;
import static com.osparking.global.names.ControlEnums.TableTypes.ROOM_HEADER_SC;
import static com.osparking.global.names.ControlEnums.TitleTypes.VISITING_CAR_FRAME_TITLE;
import static com.osparking.global.names.DB_Access.gateNames;
import com.osparking.global.names.InnoComboBoxItem;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.PComboBox;
import static com.osparking.vehicle.driver.ManageDrivers.mayPropagateBackward;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author Open Source Parking Inc.
 */
public class VisitingCar extends javax.swing.JFrame {
    ControlGUI parent = null;
    String tagRecognized;
    Date arrivalTime;
    byte gateNo;
    int imageSN;
    String filename;
    String filenameModified;
    BufferedImage bImg;
    int delay;
    
    /**
     * Key of parent combobox item for which (this) child combobox item 
     * list is formed.
     */
    private int[] prevListParentKey = new int[OSP_enums.DriverCol.values().length];
    
    private int[] prevItemParentKey = new int[OSP_enums.DriverCol.values().length];    
    
    /**
     * Creates new form VisitingCar
     */
    public VisitingCar(ControlGUI parent, String tagRecognized, Date arrivalTime, 
            byte gateNo, int imageSN, BufferedImage bImg, int delay) 
    {
        initComponents();
        applyUserCode();
        setIconImages(OSPiconList);
        Point screenCenter = getTopLeftPointToPutThisFrameAtScreenCenter(this);
        setLocation(screenCenter);    
        
        this.parent = parent;
        this.tagRecognized = tagRecognized; 
        this.arrivalTime = arrivalTime; 
        this.gateNo = gateNo; 
        this.imageSN = imageSN; 
        this.filename = filename; 
        this.bImg = bImg;
        this.delay = delay;

        recogTextField.setText(tagRecognized);
        visitTimeTextField.setText(new SimpleDateFormat ("a hh:mm:ss").
                format(arrivalTime));
        
        getPrevListParentKey()[AffiliationL2.getNumVal()] = NOT_LISTED;
        getPrevListParentKey()[UnitNo.getNumVal()] = NOT_LISTED;
        
        refreshComboBox(visitL1ComboBox, getPrompter(AffiliationL1, visitL1ComboBox),
                AffiliationL1, -1, getPrevListParentKey());        
        visitL2ComboBox.addItem(getPrompter(AffiliationL2, visitL1ComboBox));
        
        refreshComboBox(visitBuildingComboBox, getPrompter(BuildingNo, visitBuildingComboBox),
                BuildingNo, -1, getPrevListParentKey());        
        visitUnitComboBox.addItem(getPrompter(UnitNo, visitBuildingComboBox));           
        
        visitReasonTextField.setText("");
        gateNameTextField.setText(gateNames[gateNo]);
        addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                openBarButton.requestFocus();
            }
        }); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wholePanel = new javax.swing.JPanel();
        overviewTitle = new javax.swing.JPanel();
        summaryTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        myMetaKeyLabel = new javax.swing.JLabel();
        overviewPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        gateNameTextField = new javax.swing.JTextField();
        overview2Panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        recogTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        visitTimeTextField = new javax.swing.JTextField();
        whereToTitle = new javax.swing.JPanel();
        whereTo = new javax.swing.JLabel();
        whereToPanel = new javax.swing.JPanel();
        affiliationPan = new javax.swing.JPanel();
        L1Panel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitL1ComboBox = new PComboBox();
        L2Panel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitL2ComboBox = new PComboBox();
        buildingPanel = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitBuildingComboBox = new PComboBox();
        jPanel14 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitUnitComboBox = new PComboBox();
        detailedTitle = new javax.swing.JPanel();
        detailedReason = new javax.swing.JLabel();
        reasonPanel = new javax.swing.JPanel();
        visitReasonTextField = new javax.swing.JTextField();
        expandingPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        openBarButton = new javax.swing.JButton();
        notAllowButton = new javax.swing.JButton();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler25 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        bttomPanel = new javax.swing.JPanel();
        openL1Button = new javax.swing.JButton();
        openL2Button = new javax.swing.JButton();
        openBldgButton = new javax.swing.JButton();
        openRoomButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(VISITING_CAR_FRAME_TITLE.getContent());
        setMinimumSize(new java.awt.Dimension(760, 610));
        setPreferredSize(new java.awt.Dimension(760, 610));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wholePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        wholePanel.setMinimumSize(new java.awt.Dimension(680, 467));
        wholePanel.setPreferredSize(new java.awt.Dimension(680, 467));

        overviewTitle.setMaximumSize(new java.awt.Dimension(32767, 40));

        summaryTitle.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        summaryTitle.setForeground(new java.awt.Color(18, 22, 113));
        summaryTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        summaryTitle.setText("Overview");
        summaryTitle.setMaximumSize(new java.awt.Dimension(130, 40));
        summaryTitle.setMinimumSize(new java.awt.Dimension(130, 40));
        summaryTitle.setPreferredSize(new java.awt.Dimension(130, 40));

        jPanel1.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        jPanel1.add(myMetaKeyLabel);

        javax.swing.GroupLayout overviewTitleLayout = new javax.swing.GroupLayout(overviewTitle);
        overviewTitle.setLayout(overviewTitleLayout);
        overviewTitleLayout.setHorizontalGroup(
            overviewTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewTitleLayout.createSequentialGroup()
                .addComponent(summaryTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        overviewTitleLayout.setVerticalGroup(
            overviewTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(summaryTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(overviewTitleLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        overviewPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        overviewPanel.setMinimumSize(new java.awt.Dimension(360, 40));
        overviewPanel.setPreferredSize(new java.awt.Dimension(360, 40));
        overviewPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(GATE_NAME_LABEL.getContent());
        jLabel6.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel6.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel6.setPreferredSize(new java.awt.Dimension(120, 40));
        overviewPanel.add(jLabel6);

        gateNameTextField.setEditable(false);
        gateNameTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        gateNameTextField.setText("gateNameTextField");
        gateNameTextField.setMaximumSize(new java.awt.Dimension(160, 40));
        gateNameTextField.setMinimumSize(new java.awt.Dimension(160, 40));
        gateNameTextField.setPreferredSize(new java.awt.Dimension(160, 40));
        gateNameTextField.setRequestFocusEnabled(false);
        overviewPanel.add(gateNameTextField);

        overview2Panel.setMaximumSize(new java.awt.Dimension(32767, 40));
        overview2Panel.setMinimumSize(new java.awt.Dimension(730, 40));
        overview2Panel.setPreferredSize(new java.awt.Dimension(730, 40));
        overview2Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(CAR_TAG_HEADER.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel1.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 40));
        overview2Panel.add(jLabel1);

        recogTextField.setEditable(false);
        recogTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        recogTextField.setText("12가3456");
        recogTextField.setMaximumSize(new java.awt.Dimension(160, 40));
        recogTextField.setMinimumSize(new java.awt.Dimension(160, 40));
        recogTextField.setPreferredSize(new java.awt.Dimension(160, 40));
        overview2Panel.add(recogTextField);

        jLabel8.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(VISIT_TIME_LABEL.getContent());
        jLabel8.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel8.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel8.setPreferredSize(new java.awt.Dimension(120, 40));
        overview2Panel.add(jLabel8);

        visitTimeTextField.setEditable(false);
        visitTimeTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        visitTimeTextField.setText("'15.02.05 13:27:04");
        visitTimeTextField.setMaximumSize(new java.awt.Dimension(150, 40));
        visitTimeTextField.setMinimumSize(new java.awt.Dimension(150, 40));
        visitTimeTextField.setPreferredSize(new java.awt.Dimension(150, 40));
        overview2Panel.add(visitTimeTextField);

        whereToTitle.setMaximumSize(new java.awt.Dimension(32767, 40));
        whereToTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        whereTo.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        whereTo.setForeground(new java.awt.Color(18, 22, 113));
        whereTo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        whereTo.setText("Where to go");
        whereTo.setMaximumSize(new java.awt.Dimension(180, 40));
        whereTo.setMinimumSize(new java.awt.Dimension(180, 40));
        whereTo.setPreferredSize(new java.awt.Dimension(180, 40));
        whereToTitle.add(whereTo);

        whereToPanel.setMaximumSize(new java.awt.Dimension(700, 110));
        whereToPanel.setMinimumSize(new java.awt.Dimension(620, 99));
        whereToPanel.setPreferredSize(new java.awt.Dimension(620, 99));

        affiliationPan.setMaximumSize(new java.awt.Dimension(32767, 99));
        affiliationPan.setMinimumSize(new java.awt.Dimension(300, 99));
        affiliationPan.setPreferredSize(new java.awt.Dimension(300, 99));

        L1Panel.setMaximumSize(new java.awt.Dimension(380, 40));
        L1Panel.setMinimumSize(new java.awt.Dimension(380, 40));
        L1Panel.setPreferredSize(new java.awt.Dimension(380, 40));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(HIGHER_HEADER_SC.getContent());
        jLabel3.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel3.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel3.setPreferredSize(new java.awt.Dimension(120, 40));

        visitL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+CONTENT_INC));
        visitL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitL1ComboBox.setMaximumSize(new java.awt.Dimension(170, 40));
        visitL1ComboBox.setMinimumSize(new java.awt.Dimension(170, 40));
        visitL1ComboBox.setPreferredSize(new java.awt.Dimension(170, 40));
        visitL1ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                visitL1ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        visitL1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitL1ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout L1PanelLayout = new javax.swing.GroupLayout(L1Panel);
        L1Panel.setLayout(L1PanelLayout);
        L1PanelLayout.setHorizontalGroup(
            L1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(L1PanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(visitL1ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        L1PanelLayout.setVerticalGroup(
            L1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(filler18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(visitL1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        L2Panel.setMaximumSize(new java.awt.Dimension(310, 40));
        L2Panel.setMinimumSize(new java.awt.Dimension(380, 40));
        L2Panel.setPreferredSize(new java.awt.Dimension(380, 40));

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText(LOWER_HEADER_SC.getContent());
        jLabel5.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel5.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 40));

        visitL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+CONTENT_INC));
        visitL2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitL2ComboBox.setMaximumSize(new java.awt.Dimension(170, 40));
        visitL2ComboBox.setMinimumSize(new java.awt.Dimension(170, 40));
        visitL2ComboBox.setPreferredSize(new java.awt.Dimension(170, 40));
        visitL2ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                visitL2ComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                visitL2ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        visitL2ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitL2ComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout L2PanelLayout = new javax.swing.GroupLayout(L2Panel);
        L2Panel.setLayout(L2PanelLayout);
        L2PanelLayout.setHorizontalGroup(
            L2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(L2PanelLayout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(visitL2ComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        L2PanelLayout.setVerticalGroup(
            L2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(filler19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(visitL2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout affiliationPanLayout = new javax.swing.GroupLayout(affiliationPan);
        affiliationPan.setLayout(affiliationPanLayout);
        affiliationPanLayout.setHorizontalGroup(
            affiliationPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(affiliationPanLayout.createSequentialGroup()
                .addGroup(affiliationPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(L1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(L2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        affiliationPanLayout.setVerticalGroup(
            affiliationPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(affiliationPanLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(L1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(L2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buildingPanel.setMinimumSize(new java.awt.Dimension(288, 99));
        buildingPanel.setPreferredSize(new java.awt.Dimension(288, 99));
        buildingPanel.setLayout(new javax.swing.BoxLayout(buildingPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel13.setMaximumSize(new java.awt.Dimension(400, 32767));
        jPanel13.setMinimumSize(new java.awt.Dimension(288, 40));
        jPanel13.setName(""); // NOI18N
        jPanel13.setPreferredSize(new java.awt.Dimension(288, 40));
        jPanel13.setRequestFocusEnabled(false);
        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText(BUILDING_HEADER_SC.getContent());
        jLabel9.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel9.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel9.setPreferredSize(new java.awt.Dimension(120, 40));
        jPanel13.add(jLabel9);
        jPanel13.add(filler20);

        visitBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+CONTENT_INC));
        visitBuildingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitBuildingComboBox.setMaximumSize(new java.awt.Dimension(150, 40));
        visitBuildingComboBox.setMinimumSize(new java.awt.Dimension(150, 40));
        visitBuildingComboBox.setPreferredSize(new java.awt.Dimension(150, 40));
        visitBuildingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                visitBuildingComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        visitBuildingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitBuildingComboBoxActionPerformed(evt);
            }
        });
        jPanel13.add(visitBuildingComboBox);

        buildingPanel.add(jPanel13);

        jPanel14.setMaximumSize(new java.awt.Dimension(400, 32767));
        jPanel14.setMinimumSize(new java.awt.Dimension(288, 40));
        jPanel14.setPreferredSize(new java.awt.Dimension(288, 40));
        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText(ROOM_HEADER_SC.getContent());
        jLabel10.setMaximumSize(new java.awt.Dimension(120, 40));
        jLabel10.setMinimumSize(new java.awt.Dimension(120, 40));
        jLabel10.setPreferredSize(new java.awt.Dimension(120, 40));
        jPanel14.add(jLabel10);
        jPanel14.add(filler21);

        visitUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+CONTENT_INC));
        visitUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitUnitComboBox.setMaximumSize(new java.awt.Dimension(150, 40));
        visitUnitComboBox.setMinimumSize(new java.awt.Dimension(150, 40));
        visitUnitComboBox.setPreferredSize(new java.awt.Dimension(150, 40));
        visitUnitComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                visitUnitComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                visitUnitComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        jPanel14.add(visitUnitComboBox);

        buildingPanel.add(jPanel14);

        javax.swing.GroupLayout whereToPanelLayout = new javax.swing.GroupLayout(whereToPanel);
        whereToPanel.setLayout(whereToPanelLayout);
        whereToPanelLayout.setHorizontalGroup(
            whereToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(whereToPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(affiliationPan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(buildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(112, 112, 112))
        );
        whereToPanelLayout.setVerticalGroup(
            whereToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(affiliationPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buildingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        detailedTitle.setMaximumSize(new java.awt.Dimension(32767, 40));
        detailedTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        detailedReason.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        detailedReason.setForeground(new java.awt.Color(18, 22, 113));
        detailedReason.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        detailedReason.setText(DisallowReason.getContent());
        detailedReason.setMaximumSize(new java.awt.Dimension(130, 40));
        detailedReason.setMinimumSize(new java.awt.Dimension(130, 40));
        detailedReason.setPreferredSize(new java.awt.Dimension(130, 40));
        detailedTitle.add(detailedReason);

        reasonPanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        reasonPanel.setMinimumSize(new java.awt.Dimension(86, 40));
        reasonPanel.setPreferredSize(new java.awt.Dimension(0, 40));

        visitReasonTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size+CONTENT_INC));
        visitReasonTextField.setText("Visit coffee shop \"Star Coffee Bean\"");
        visitReasonTextField.setMaximumSize(null);
        visitReasonTextField.setMinimumSize(new java.awt.Dimension(6, 40));
        visitReasonTextField.setPreferredSize(new java.awt.Dimension(700, 40));

        javax.swing.GroupLayout reasonPanelLayout = new javax.swing.GroupLayout(reasonPanel);
        reasonPanel.setLayout(reasonPanelLayout);
        reasonPanelLayout.setHorizontalGroup(
            reasonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reasonPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(visitReasonTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
        );
        reasonPanelLayout.setVerticalGroup(
            reasonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visitReasonTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        expandingPanel.setMaximumSize(new java.awt.Dimension(100, 32767));
        expandingPanel.setMinimumSize(new java.awt.Dimension(100, 10));
        expandingPanel.setPreferredSize(new java.awt.Dimension(100, 10));

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        openBarButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        openBarButton.setMnemonic('P');
        openBarButton.setText(OPEN_LABEL.getContent());
        openBarButton.setMaximumSize(new java.awt.Dimension(160, 60));
        openBarButton.setMinimumSize(new java.awt.Dimension(160, 60));
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

        notAllowButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        notAllowButton.setMnemonic('C');
        notAllowButton.setText(DISALLOW_LABEL.getContent());
        notAllowButton.setMaximumSize(new java.awt.Dimension(160, 60));
        notAllowButton.setMinimumSize(new java.awt.Dimension(160, 60));
        notAllowButton.setPreferredSize(new java.awt.Dimension(160, 60));
        notAllowButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notAllowButtonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                notAllowButtonFocusLost(evt);
            }
        });
        notAllowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notAllowButtonActionPerformed(evt);
            }
        });
        notAllowButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                notAllowButtonKeyTyped(evt);
            }
        });
        buttonPanel.add(notAllowButton);

        javax.swing.GroupLayout wholePanelLayout = new javax.swing.GroupLayout(wholePanel);
        wholePanel.setLayout(wholePanelLayout);
        wholePanelLayout.setHorizontalGroup(
            wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(reasonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
            .addComponent(overviewTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(overviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(overview2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(whereToTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(detailedTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(whereToPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wholePanelLayout.createSequentialGroup()
                    .addContainerGap(206, Short.MAX_VALUE)
                    .addComponent(expandingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(374, Short.MAX_VALUE)))
        );
        wholePanelLayout.setVerticalGroup(
            wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wholePanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(overviewTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(overviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(overview2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(whereToTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(whereToPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(detailedTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(reasonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(wholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wholePanelLayout.createSequentialGroup()
                    .addContainerGap(370, Short.MAX_VALUE)
                    .addComponent(expandingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(88, Short.MAX_VALUE)))
        );

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(filler22, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(filler24, java.awt.BorderLayout.EAST);
        getContentPane().add(filler25, java.awt.BorderLayout.WEST);

        bttomPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        bttomPanel.setMinimumSize(new java.awt.Dimension(100, 40));
        bttomPanel.setPreferredSize(new java.awt.Dimension(100, 40));

        openL1Button.setMnemonic('H');
        openL1Button.setPreferredSize(new java.awt.Dimension(0, 0));
        openL1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openL1ButtonActionPerformed(evt);
            }
        });

        openL2Button.setMnemonic('L');
        openL2Button.setPreferredSize(new java.awt.Dimension(0, 0));
        openL2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openL2ButtonActionPerformed(evt);
            }
        });

        openBldgButton.setMnemonic('B');
        openBldgButton.setPreferredSize(new java.awt.Dimension(0, 0));
        openBldgButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBldgButtonActionPerformed(evt);
            }
        });

        openRoomButton.setMnemonic('R');
        openRoomButton.setPreferredSize(new java.awt.Dimension(0, 0));
        openRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRoomButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bttomPanelLayout = new javax.swing.GroupLayout(bttomPanel);
        bttomPanel.setLayout(bttomPanelLayout);
        bttomPanelLayout.setHorizontalGroup(
            bttomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bttomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openL1Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openL2Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openBldgButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(722, Short.MAX_VALUE))
        );
        bttomPanelLayout.setVerticalGroup(
            bttomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bttomPanelLayout.createSequentialGroup()
                .addGap(0, 40, Short.MAX_VALUE)
                .addGroup(bttomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openL1Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openL2Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openBldgButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        getContentPane().add(bttomPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBarButtonActionPerformed
        welcomeVisitor(true);
    }//GEN-LAST:event_openBarButtonActionPerformed

    private void notAllowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notAllowButtonActionPerformed
        welcomeVisitor(false);
    }//GEN-LAST:event_notAllowButtonActionPerformed

    @SuppressWarnings("unchecked") 
    private void visitL1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitL1ComboBoxActionPerformed
        Globals.mayChangeLowerCBoxPrompt(visitL1ComboBox, visitL2ComboBox, 
                AffiliationL2, getPrevItemParentKey());
//        if (visitL1ComboBox.isPopupVisible()) {
//            MutableComboBoxModel model = (MutableComboBoxModel)visitL2ComboBox.getModel();
//            model.removeElementAt(0);
//            model.insertElementAt(getPrompter(AffiliationL2, visitL1ComboBox), 0);
//            visitL2ComboBox.setSelectedIndex(0);            
//        }        
    }//GEN-LAST:event_visitL1ComboBoxActionPerformed

    @SuppressWarnings("unchecked") 
    private void visitL1ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitL1ComboBoxPopupMenuWillBecomeVisible
//        Object selItem = L1CBox.getSelectedItem();
//
//        L1CBox.removeAllItems();
//        L1CBox.addItem(getPrompter(AffiliationL1, L1CBox));     
//        loadComboBoxItems(L1CBox, AffiliationL1, -1);
//        L1CBox.setSelectedItem(selItem);         
    }//GEN-LAST:event_visitL1ComboBoxPopupMenuWillBecomeVisible

    @SuppressWarnings("unchecked") 
    private void visitL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitL2ComboBoxPopupMenuWillBecomeVisible
        Object selItem = visitL2ComboBox.getSelectedItem();
        
        ConvComboBoxItem l1Item = (ConvComboBoxItem)visitL1ComboBox.getSelectedItem(); 
        int L1No = (Integer) l1Item.getKeyValue();        // normalize child combobox item 
        
        visitL2ComboBox.removeAllItems();
        visitL2ComboBox.addItem(getPrompter(AffiliationL2, visitL1ComboBox));     
        loadComboBoxItems(visitL2ComboBox, DriverCol.AffiliationL2, L1No);   
        
        visitL2ComboBox.setSelectedItem(selItem);           
    }//GEN-LAST:event_visitL2ComboBoxPopupMenuWillBecomeVisible

    @SuppressWarnings("unchecked") 
    private void visitBuildingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitBuildingComboBoxActionPerformed
        Globals.mayChangeLowerCBoxPrompt(visitBuildingComboBox, visitUnitComboBox, 
                UnitNo, getPrevItemParentKey());
//        if (visitBuildingComboBox.isPopupVisible()) {
//            MutableComboBoxModel model 
//                    = (MutableComboBoxModel)visitUnitComboBox.getModel();
//            model.removeElementAt(0);
//            model.insertElementAt(ManageDrivers.getPrompter(UnitNo, visitBuildingComboBox), 0);
//            visitUnitComboBox.setSelectedIndex(0);            
//        }
    }//GEN-LAST:event_visitBuildingComboBoxActionPerformed

    @SuppressWarnings("unchecked") 
    private void visitBuildingComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitBuildingComboBoxPopupMenuWillBecomeVisible
//        Object selItem = visitBuildingComboBox.getSelectedItem();
//        
//        visitBuildingComboBox.removeAllItems();
//        visitBuildingComboBox.addItem(getPrompter(BuildingNo, null));     
//        loadComboBoxItems(visitBuildingComboBox, BuildingNo, -1);
//        visitBuildingComboBox.setSelectedItem(selItem);         
    }//GEN-LAST:event_visitBuildingComboBoxPopupMenuWillBecomeVisible

    private void visitUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitUnitComboBoxPopupMenuWillBecomeVisible
//        loadUnitComboBox(visitL1ComboBox, visitBuildingComboBox, visitUnitComboBox);   
        Object selItem = visitUnitComboBox.getSelectedItem();
        
        ConvComboBoxItem bldgItem = (ConvComboBoxItem)visitBuildingComboBox.getSelectedItem(); 
        int bldgNo = (Integer) bldgItem.getKeyValue();
        
        refreshComboBox(visitUnitComboBox, getPrompter(UnitNo, visitBuildingComboBox), 
                UnitNo, bldgNo, getPrevItemParentKey());
        visitUnitComboBox.setSelectedItem(selItem);         
    }//GEN-LAST:event_visitUnitComboBoxPopupMenuWillBecomeVisible

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(parent != null){
            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTime, tagRecognized, null,
                bImg, -1, -1, null, BarOperation.REMAIN_CLOSED);
            parent.isGateBusy[gateNo] = false;        
            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, BarOperation.REMAIN_CLOSED);
        }
        dispose();        
    }//GEN-LAST:event_formWindowClosing

    private void openBarButtonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_openBarButtonFocusGained
        // TODO add your handling code here:
        openBarButton.setBackground((new java.awt.Color(102, 255, 102)));
    }//GEN-LAST:event_openBarButtonFocusGained

    private void openBarButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_openBarButtonFocusLost
        // TODO add your handling code here:
        openBarButton.setBackground((new java.awt.Color(240, 240, 240)));
    }//GEN-LAST:event_openBarButtonFocusLost

    private void openBarButtonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_openBarButtonKeyTyped
        // TODO add your handling code here:
        if(evt.getKeyChar() == KeyEvent.VK_ENTER)
        {
            openBarButtonActionPerformed(null);
        }
    }//GEN-LAST:event_openBarButtonKeyTyped

    private void notAllowButtonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notAllowButtonFocusGained
        // TODO add your handling code here:
        notAllowButton.setBackground((new java.awt.Color(102, 255, 102)));
    }//GEN-LAST:event_notAllowButtonFocusGained

    private void notAllowButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notAllowButtonFocusLost
        // TODO add your handling code here:
        notAllowButton.setBackground((new java.awt.Color(240, 240, 240)));
    }//GEN-LAST:event_notAllowButtonFocusLost

    private void notAllowButtonKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_notAllowButtonKeyTyped
        // TODO add your handling code here:
        if(evt.getKeyChar() == KeyEvent.VK_ENTER)
        {
            notAllowButtonActionPerformed(null);
        }
    }//GEN-LAST:event_notAllowButtonKeyTyped

    private void visitL2ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitL2ComboBoxPopupMenuWillBecomeInvisible
        mayPropagateBackward(visitL2ComboBox, visitL1ComboBox);
    }//GEN-LAST:event_visitL2ComboBoxPopupMenuWillBecomeInvisible

    private void visitUnitComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitUnitComboBoxPopupMenuWillBecomeInvisible
        mayPropagateBackward(visitUnitComboBox, visitBuildingComboBox);
    }//GEN-LAST:event_visitUnitComboBoxPopupMenuWillBecomeInvisible

    private void openL1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openL1ButtonActionPerformed
        visitL1ComboBox.requestFocus();
        visitL1ComboBox.showPopup();
    }//GEN-LAST:event_openL1ButtonActionPerformed

    private void visitL2ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitL2ComboBoxActionPerformed

    }//GEN-LAST:event_visitL2ComboBoxActionPerformed

    private void openL2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openL2ButtonActionPerformed
        visitL2ComboBox.requestFocus();
        visitL2ComboBox.showPopup();
    }//GEN-LAST:event_openL2ButtonActionPerformed

    private void openBldgButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBldgButtonActionPerformed
        visitBuildingComboBox.requestFocus();
        visitBuildingComboBox.showPopup();
    }//GEN-LAST:event_openBldgButtonActionPerformed

    private void openRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRoomButtonActionPerformed
        visitUnitComboBox.requestFocus();
        visitUnitComboBox.showPopup();
    }//GEN-LAST:event_openRoomButtonActionPerformed

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
            java.util.logging.Logger.getLogger(VisitingCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VisitingCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VisitingCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VisitingCar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VisitingCar(null, "12가3456", new Date(), (byte)1, 1, 
                        null, 8000).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel L1Panel;
    private javax.swing.JPanel L2Panel;
    private javax.swing.JPanel affiliationPan;
    private javax.swing.JPanel bttomPanel;
    private javax.swing.JPanel buildingPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel detailedReason;
    private javax.swing.JPanel detailedTitle;
    private javax.swing.JPanel expandingPanel;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler25;
    private javax.swing.JTextField gateNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JButton notAllowButton;
    private javax.swing.JButton openBarButton;
    private javax.swing.JButton openBldgButton;
    private javax.swing.JButton openL1Button;
    private javax.swing.JButton openL2Button;
    private javax.swing.JButton openRoomButton;
    private javax.swing.JPanel overview2Panel;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JPanel overviewTitle;
    private javax.swing.JPanel reasonPanel;
    private javax.swing.JTextField recogTextField;
    private javax.swing.JLabel summaryTitle;
    private javax.swing.JComboBox visitBuildingComboBox;
    private javax.swing.JComboBox visitL1ComboBox;
    private javax.swing.JComboBox visitL2ComboBox;
    private javax.swing.JTextField visitReasonTextField;
    private javax.swing.JTextField visitTimeTextField;
    private javax.swing.JComboBox visitUnitComboBox;
    private javax.swing.JLabel whereTo;
    private javax.swing.JPanel whereToPanel;
    private javax.swing.JPanel whereToTitle;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Operate the gate bar if ordered so.
     * Store the visitor arrival record into the database.
     * @param openGate when true, open the gate bar to welcome the visitor.
     *                                  otherwise, let the bar remain closed and disallow the visitor to enter.
     */
    private void welcomeVisitor(boolean openGate) {
        int unitSeqNo;
        int l2No;
        if (parent != null) {
            if (openGate) {
                parent.raiseGateBar(gateNo, imageSN, delay);
            }

            if (visitL2ComboBox.getSelectedIndex() == -1) {
                l2No = -1;
            } else {
                l2No = (Integer)
                        ((InnoComboBoxItem)visitL2ComboBox.getSelectedItem()).getKeys()[0];
            }
            if (visitUnitComboBox.getSelectedIndex() == -1) {
                unitSeqNo = -1;
            } else {
                unitSeqNo = (Integer)
                        ((InnoComboBoxItem)visitUnitComboBox.getSelectedItem()).getKeys()[0];
            }
            BarOperation barOperation = BarOperation.MANUAL;
            if (!openGate) {
                barOperation = BarOperation.REMAIN_CLOSED;
            }
            String reason = visitReasonTextField.getText();
            long arrSeqNo = parent.insertDBrecord(gateNo, arrivalTime, tagRecognized, null,
                    bImg, unitSeqNo, l2No, reason.length() == 0 ? null : reason , barOperation);
            parent.updateMainForm(gateNo, tagRecognized, arrSeqNo, barOperation);        
            parent.isGateBusy[gateNo] = false; 
        }
        dispose();
    }

    /**
     * @return the prevParentKey
     */
    public int[] getPrevItemParentKey() {
        return prevItemParentKey;
    }

    private void applyUserCode() {
        summaryTitle.setText(VISIT_OVERVIEW.getContent());
        whereTo.setText(VISIT_TARGET_LABEL.getContent());
        detailedReason.setText(VISIT_REASON_LABEL.getContent());
        Globals.setComponentSize(openBarButton, new Dimension(bigButtonWidth, bigButtonHeight));
        Globals.setComponentSize(notAllowButton, new Dimension(bigButtonWidth, bigButtonHeight));
    }

    /**
     * @return the prevListParentKey
     */
    public int[] getPrevListParentKey() {
        return prevListParentKey;
    }
}
