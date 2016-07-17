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

import static com.osparking.global.CommonData.bigButtonDim;
import static com.osparking.global.Globals.CONTENT_INC;
import static com.osparking.global.Globals.LABEL_INC;
import com.osparking.vehicle.driver.ManageDrivers;
import static com.osparking.vehicle.driver.ManageDrivers.loadComboBoxItems;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.osparking.global.names.OSP_enums.BarOperation;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.getTopLeftPointToPutThisFrameAtScreenCenter;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.names.ControlEnums.LabelContent.DISALLOW_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.OPEN_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_OVERVIEW;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_REASON_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_TARGET_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_TIME_LABEL;
import static com.osparking.global.names.ControlEnums.TableTypes.BUILDING_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.CAR_TAG_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOWER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ROOM_HEADER;
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
import static com.osparking.vehicle.CommonData.refreshComboBox;
import static com.osparking.vehicle.driver.ManageDrivers.getPrompter;
import static com.osparking.vehicle.driver.ManageDrivers.mayChangeChildPrompter;
import static com.osparking.vehicle.driver.ManageDrivers.mayPropagateBackward;
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
    
    private int[] prevParentKey = new int[OSP_enums.DriverCol.values().length];    
    
    /**
     * Creates new form VisitingCar
     */
    public VisitingCar(ControlGUI parent, String tagRecognized, Date arrivalTime, 
            byte gateNo, int imageSN, BufferedImage bImg, int delay) 
    {
        initComponents();
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
        visitTimeTextField.setText("'" + new SimpleDateFormat ("a hh:mm:ss").
                format(arrivalTime));
        
//        initSearchComboBox(highLevelComboBox, lowLevelComboBox, 
//                buildingComboBox, unitComboBox);
        visitL1ComboBox.addItem(getPrompter(AffiliationL1, null));
        visitL2ComboBox.addItem(getPrompter(AffiliationL2, visitL1ComboBox));
        visitBuildingComboBox.addItem(getPrompter(BuildingNo, null));
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
        jLabel7 = new javax.swing.JLabel();
        overviewPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        gateNameTextField = new javax.swing.JTextField();
        overview2Panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        recogTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        visitTimeTextField = new javax.swing.JTextField();
        purposeTitle = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        purposePanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitL1ComboBox = new PComboBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitL2ComboBox = new PComboBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitBuildingComboBox = new PComboBox();
        jPanel14 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        visitUnitComboBox = new PComboBox();
        reasonTitle = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        reasonPanel = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(80, 0), new java.awt.Dimension(80, 0), new java.awt.Dimension(80, 32767));
        visitReasonTextField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        openBarButton = new javax.swing.JButton();
        notAllowButton = new javax.swing.JButton();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler25 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(VISITING_CAR_FRAME_TITLE.getContent());
        setMaximumSize(new java.awt.Dimension(900, 700));
        setMinimumSize(new java.awt.Dimension(900, 700));
        setPreferredSize(new java.awt.Dimension(900, 700));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wholePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        wholePanel.setLayout(new java.awt.GridLayout(0, 1, 0, 10));

        overviewTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jLabel7.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel7.setForeground(new java.awt.Color(18, 22, 113));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText(VISIT_OVERVIEW.getContent());
        jLabel7.setMaximumSize(new java.awt.Dimension(130, 40));
        jLabel7.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabel7.setPreferredSize(new java.awt.Dimension(130, 40));
        overviewTitle.add(jLabel7);

        wholePanel.add(overviewTitle);

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
        gateNameTextField.setMaximumSize(new java.awt.Dimension(180, 50));
        gateNameTextField.setMinimumSize(new java.awt.Dimension(180, 50));
        gateNameTextField.setPreferredSize(new java.awt.Dimension(180, 50));
        gateNameTextField.setRequestFocusEnabled(false);
        overviewPanel.add(gateNameTextField);

        wholePanel.add(overviewPanel);

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
        recogTextField.setMaximumSize(new java.awt.Dimension(180, 50));
        recogTextField.setMinimumSize(new java.awt.Dimension(180, 50));
        recogTextField.setPreferredSize(new java.awt.Dimension(180, 50));
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
        visitTimeTextField.setMaximumSize(new java.awt.Dimension(210, 50));
        visitTimeTextField.setMinimumSize(new java.awt.Dimension(210, 50));
        visitTimeTextField.setPreferredSize(new java.awt.Dimension(210, 50));
        overview2Panel.add(visitTimeTextField);

        wholePanel.add(overview2Panel);

        purposeTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel2.setForeground(new java.awt.Color(18, 22, 113));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(VISIT_TARGET_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(180, 50));
        jLabel2.setMinimumSize(new java.awt.Dimension(180, 50));
        jLabel2.setPreferredSize(new java.awt.Dimension(180, 50));
        purposeTitle.add(jLabel2);
        purposeTitle.add(filler1);

        wholePanel.add(purposeTitle);

        purposePanel.setMaximumSize(new java.awt.Dimension(65676, 130));
        purposePanel.setPreferredSize(new java.awt.Dimension(1001, 130));
        purposePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jPanel2.setPreferredSize(new java.awt.Dimension(300, 99));

        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(HIGHER_HEADER.getContent());
        jLabel3.setMinimumSize(new java.awt.Dimension(160, 50));
        jLabel3.setPreferredSize(new java.awt.Dimension(160, 50));
        jPanel11.add(jLabel3);
        jPanel11.add(filler18);

        visitL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        visitL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitL1ComboBox.setMaximumSize(new java.awt.Dimension(210, 50));
        visitL1ComboBox.setMinimumSize(new java.awt.Dimension(210, 50));
        visitL1ComboBox.setPreferredSize(new java.awt.Dimension(210, 50));
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
        jPanel11.add(visitL1ComboBox);

        jPanel2.add(jPanel11);

        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.LINE_AXIS));

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText(LOWER_HEADER.getContent());
        jLabel5.setMinimumSize(new java.awt.Dimension(160, 50));
        jLabel5.setPreferredSize(new java.awt.Dimension(160, 50));
        jPanel12.add(jLabel5);
        jPanel12.add(filler19);

        visitL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        visitL2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitL2ComboBox.setMaximumSize(new java.awt.Dimension(210, 50));
        visitL2ComboBox.setMinimumSize(new java.awt.Dimension(210, 50));
        visitL2ComboBox.setPreferredSize(new java.awt.Dimension(210, 50));
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
        jPanel12.add(visitL2ComboBox);

        jPanel2.add(jPanel12);

        purposePanel.add(jPanel2);

        jPanel3.setPreferredSize(new java.awt.Dimension(161, 99));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText(BUILDING_HEADER.getContent());
        jLabel9.setMinimumSize(new java.awt.Dimension(130, 50));
        jLabel9.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel13.add(jLabel9);
        jPanel13.add(filler20);

        visitBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        visitBuildingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitBuildingComboBox.setMaximumSize(new java.awt.Dimension(210, 50));
        visitBuildingComboBox.setMinimumSize(new java.awt.Dimension(210, 50));
        visitBuildingComboBox.setPreferredSize(new java.awt.Dimension(210, 50));
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

        jPanel3.add(jPanel13);

        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText(ROOM_HEADER.getContent());
        jLabel10.setMinimumSize(new java.awt.Dimension(130, 50));
        jLabel10.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel14.add(jLabel10);
        jPanel14.add(filler21);

        visitUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        visitUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        visitUnitComboBox.setMaximumSize(new java.awt.Dimension(210, 50));
        visitUnitComboBox.setMinimumSize(new java.awt.Dimension(210, 50));
        visitUnitComboBox.setPreferredSize(new java.awt.Dimension(210, 50));
        visitUnitComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                visitUnitComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        jPanel14.add(visitUnitComboBox);

        jPanel3.add(jPanel14);

        purposePanel.add(jPanel3);

        wholePanel.add(purposePanel);

        reasonTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 0));

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size + LABEL_INC));
        jLabel4.setForeground(new java.awt.Color(18, 22, 113));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText(VISIT_REASON_LABEL.getContent());
        jLabel4.setMaximumSize(new java.awt.Dimension(130, 40));
        jLabel4.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabel4.setPreferredSize(new java.awt.Dimension(130, 40));
        reasonTitle.add(jLabel4);

        wholePanel.add(reasonTitle);

        reasonPanel.setMaximumSize(new java.awt.Dimension(2147483647, 50));
        reasonPanel.setMinimumSize(new java.awt.Dimension(86, 40));
        reasonPanel.setPreferredSize(new java.awt.Dimension(1001, 40));
        reasonPanel.setLayout(new javax.swing.BoxLayout(reasonPanel, javax.swing.BoxLayout.LINE_AXIS));
        reasonPanel.add(filler3);

        visitReasonTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size + CONTENT_INC));
        visitReasonTextField.setText("Visit coffee shop \"Star Coffee Bean\"");
        visitReasonTextField.setMaximumSize(null);
        visitReasonTextField.setMinimumSize(new java.awt.Dimension(6, 40));
        visitReasonTextField.setPreferredSize(new java.awt.Dimension(207, 40));
        reasonPanel.add(visitReasonTextField);

        wholePanel.add(reasonPanel);

        openBarButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size+6));
        openBarButton.setMnemonic('O');
        openBarButton.setText(OPEN_LABEL.getContent());
        openBarButton.setMaximumSize(bigButtonDim);
        openBarButton.setMinimumSize(bigButtonDim);
        openBarButton.setPreferredSize(bigButtonDim);
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
        notAllowButton.setMaximumSize(bigButtonDim);
        notAllowButton.setMinimumSize(bigButtonDim);
        notAllowButton.setPreferredSize(bigButtonDim);
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

        wholePanel.add(buttonPanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(filler22, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(filler23, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(filler24, java.awt.BorderLayout.EAST);
        getContentPane().add(filler25, java.awt.BorderLayout.WEST);

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
        mayChangeChildPrompter(visitL1ComboBox, visitL2ComboBox, AffiliationL2);
//        if (visitL1ComboBox.isPopupVisible()) {
//            MutableComboBoxModel model = (MutableComboBoxModel)visitL2ComboBox.getModel();
//            model.removeElementAt(0);
//            model.insertElementAt(getPrompter(AffiliationL2, visitL1ComboBox), 0);
//            visitL2ComboBox.setSelectedIndex(0);            
//        }        
    }//GEN-LAST:event_visitL1ComboBoxActionPerformed

    @SuppressWarnings("unchecked") 
    private void visitL1ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitL1ComboBoxPopupMenuWillBecomeVisible
        Object selItem = visitL1ComboBox.getSelectedItem();

        visitL1ComboBox.removeAllItems();
        visitL1ComboBox.addItem(ManageDrivers.getPrompter(AffiliationL1, visitL1ComboBox));     
        loadComboBoxItems(visitL1ComboBox, AffiliationL1, -1);
        visitL1ComboBox.setSelectedItem(selItem);         
    }//GEN-LAST:event_visitL1ComboBoxPopupMenuWillBecomeVisible

    @SuppressWarnings("unchecked") 
    private void visitL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitL2ComboBoxPopupMenuWillBecomeVisible
        Object selItem = visitL2ComboBox.getSelectedItem();
        
        ConvComboBoxItem l1Item = (ConvComboBoxItem)visitL1ComboBox.getSelectedItem(); 
        int L1No = (Integer) l1Item.getKeyValue();        // normalize child combobox item 
        
        visitL2ComboBox.removeAllItems();
        visitL2ComboBox.addItem(ManageDrivers.getPrompter(AffiliationL2, visitL1ComboBox));     
        loadComboBoxItems(visitL2ComboBox, DriverCol.AffiliationL2, L1No);   
        
        visitL2ComboBox.setSelectedItem(selItem);           
    }//GEN-LAST:event_visitL2ComboBoxPopupMenuWillBecomeVisible

    @SuppressWarnings("unchecked") 
    private void visitBuildingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitBuildingComboBoxActionPerformed
        mayChangeChildPrompter(visitBuildingComboBox, visitUnitComboBox, UnitNo);
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
        Object selItem = visitBuildingComboBox.getSelectedItem();
        
        visitBuildingComboBox.removeAllItems();
        visitBuildingComboBox.addItem(ManageDrivers.getPrompter(BuildingNo, null));     
        loadComboBoxItems(visitBuildingComboBox, BuildingNo, -1);
        visitBuildingComboBox.setSelectedItem(selItem);         
    }//GEN-LAST:event_visitBuildingComboBoxPopupMenuWillBecomeVisible

    private void visitUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_visitUnitComboBoxPopupMenuWillBecomeVisible
//        loadUnitComboBox(visitL1ComboBox, visitBuildingComboBox, visitUnitComboBox);   
        Object selItem = visitUnitComboBox.getSelectedItem();
        ConvComboBoxItem bldgItem = (ConvComboBoxItem)visitBuildingComboBox.getSelectedItem(); 
        int bldgNo = (Integer) bldgItem.getKeyValue();
        
        refreshComboBox(visitUnitComboBox, getPrompter(UnitNo, visitBuildingComboBox), 
                UnitNo, bldgNo, getPrevParentKey());
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
    private javax.swing.JPanel buttonPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler25;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JTextField gateNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton notAllowButton;
    private javax.swing.JButton openBarButton;
    private javax.swing.JPanel overview2Panel;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JPanel overviewTitle;
    private javax.swing.JPanel purposePanel;
    private javax.swing.JPanel purposeTitle;
    private javax.swing.JPanel reasonPanel;
    private javax.swing.JPanel reasonTitle;
    private javax.swing.JTextField recogTextField;
    private javax.swing.JComboBox visitBuildingComboBox;
    private javax.swing.JComboBox visitL1ComboBox;
    private javax.swing.JComboBox visitL2ComboBox;
    private javax.swing.JTextField visitReasonTextField;
    private javax.swing.JTextField visitTimeTextField;
    private javax.swing.JComboBox visitUnitComboBox;
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
    public int[] getPrevParentKey() {
        return prevParentKey;
    }
}
