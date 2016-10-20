/*
 * Copyright (C) 2016 Open Source Parking, Inc.(www.osparking.com)
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

import static com.osparking.global.CommonData.ADMIN_ID;
import static com.osparking.global.CommonData.adminOperationEnabled;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.buttonWidthWide;
import static com.osparking.global.CommonData.deleteTable;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.PopUpBackground;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.findLoginIdentity;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getQuest20_Icon;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.isManager;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.loginID;
import com.osparking.global.IMainGUI;
import com.osparking.global.names.ControlEnums;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.DELETE_ALL_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.MODIFY_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.READ_ODS_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAMPLE2_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SAVE_ODS_BTN;
import static com.osparking.global.names.ControlEnums.DialogMessages.AFFILIATION_DELETE_ALL_DAILOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.AFFILIATION_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_ALL_DAILOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.LOWER_MODIFY_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.FormMode.CreateMode;
import static com.osparking.global.names.ControlEnums.FormMode.NormalMode;
import static com.osparking.global.names.ControlEnums.FormMode.UpdateMode;
import static com.osparking.global.names.ControlEnums.FormModeString.CREATE;
import static com.osparking.global.names.ControlEnums.FormModeString.MODIFY;
import static com.osparking.global.names.ControlEnums.FormModeString.SEARCH;
import static com.osparking.global.names.ControlEnums.LabelContent.AFFILIATION_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CHOOSE_PANEL_DIALOG;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_SAVE_HELP;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_AFFIL_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.HELP_BUILDING_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.LOWER_LIST_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.UPDATE_SAVE_HELP;
import static com.osparking.global.names.ControlEnums.LabelContent.WORK_PANEL_LABEL;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI2_DIAG_L3;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L1;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L2;
import static com.osparking.global.names.ControlEnums.MsgContent.AFFILI_DIAG_L3;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import static com.osparking.global.names.ControlEnums.OsPaTable.L1_affiliation;
import com.osparking.global.names.ControlEnums.TableType;
import static com.osparking.global.names.ControlEnums.TableType.L1_TABLE;
import static com.osparking.global.names.ControlEnums.TableType.L2_TABLE;
import static com.osparking.global.names.ControlEnums.TitleTypes.L1_AFFILI_ROW;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums.ODS_TYPE;
import static com.osparking.global.names.OSP_enums.ODS_TYPE.AFFILIATION;
import static com.osparking.global.names.OSP_enums.ODS_TYPE.BUILDING;
import static com.osparking.vehicle.CommonData.setHelpDialogLoc;
import java.awt.Color;
import static java.awt.Color.black;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class AffiliationGUI extends javax.swing.JFrame {
    private boolean affiliL1_Editable = false;
    IMainGUI mainForm = null;
    private boolean isStand_Alone = false;
    private ControlEnums.FormMode formMode = NormalMode;

    static Object prevL1Name = null;
    static Object prevL2Name = null;    
    
    /** Used to make context sensitive help message only once after
     *  this GUI displayed.
     */
    boolean createBlinked = false;
    boolean normalBlinked = false;
    boolean updateBlinked = false;    
    
    /**
     * Creates new form AffiliationGUI
     */
    public AffiliationGUI(IMainGUI mainForm) {
        initComponents();
        this.mainForm = mainForm;
        if (mainForm == null) {
            isStand_Alone = true;
        }
        affiL1_Control.setSelected(true);     
        
        /**
         * Set icon for the simulated camera program
         */
        setIconImages(OSPiconList);
        this.getContentPane().setBackground(PopUpBackground);       
        adjustTables();
        adminOperationEnabled(true, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);        
        
        /**
         * Set default fonts for Korean columns.
         */
        TableCellEditorKor editor = new TableCellEditorKor(KOREAN, L1_Affiliation.getFont());
        System.out.println("Editor 1: " + editor);
        
        L1_Affiliation.getColumnModel().getColumn(1).setCellEditor(editor);  
        editor = new TableCellEditorKor(KOREAN, L2_Affiliation.getFont());
        System.out.println("Editor 1': " + editor);
        L2_Affiliation.getColumnModel().getColumn(1).setCellEditor(editor);        
        
        /**
         * Add table model listener to process 'enter key' for affiliation tables.
         */
        L1_Affiliation.getModel().addTableModelListener(new TableModelListener() {
           @Override
            public void tableChanged(TableModelEvent e) {
//                if (formMode == UpdateMode) {
//                    updateAffiliation(L1_Affiliation, prevL1Name, "L1_NO", cancelL1_Button, 
//                            DUPLICATE_HIGH_AFFILI, L1_TABLE);
//                } else if (formMode == CreateMode) {
//                    insertAffiliation(L1_Affiliation, cancelL1_Button, 
//                            DUPLICATE_HIGH_AFFILI, L1_TABLE);
//                }
            }
        });
        L2_Affiliation.getModel().addTableModelListener(new TableModelListener() {
           @Override
            public void tableChanged(TableModelEvent e) {
                if (!affiL2_Control.isSelected()) {
                    return;
                }
//                if (formMode == UpdateMode) {
//                    updateAffiliation(L2_Affiliation, prevL2Name, "L2_NO", cancelL2_Button, 
//                            DUPLICATE_LOW_AFFILI, L2_TABLE);
//                } else if (formMode == CreateMode) {
//                    insertAffiliation(L2_Affiliation, cancelL2_Button, 
//                            DUPLICATE_LOW_AFFILI, L2_TABLE);                    
//                }
            }            
        });
        
        loadL1_Affiliation(0, "");
        setFormMode(NormalMode);        
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

        all_L1_Panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        L1_Affiliation = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return affiliL1_Editable;
            }
        }
        ;
        jPanel2 = new javax.swing.JPanel();
        affiL1_Control = new javax.swing.JRadioButton();
        insertL1_Button = new javax.swing.JButton();
        modifyL1_Button = new javax.swing.JButton();
        deleteL1_Button = new javax.swing.JButton();
        cancelL1_Button = new javax.swing.JButton();
        L1_Title = new javax.swing.JLabel();
        all_L2_Panel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        L2_Affiliation = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return affiliL1_Editable;
            }
        }
        ;
        jPanel4 = new javax.swing.JPanel();
        affiL2_Control = new javax.swing.JRadioButton();
        L2_Title = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        workPanel = new javax.swing.JLabel();
        workPanelName = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel2 = new javax.swing.JLabel();
        modeString = new javax.swing.JLabel();
        csHelpLabel = new javax.swing.JLabel();
        closePanel = new javax.swing.JPanel();
        leftButtons = new javax.swing.JPanel();
        deleteAll_Affiliation = new javax.swing.JButton();
        readSheet = new javax.swing.JButton();
        saveSheet_Button = new javax.swing.JButton();
        ODSAffiliHelp = new javax.swing.JButton();
        sampleButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        L1_Affiliation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        L1_Affiliation.setRowHeight(tableRowHeight);
        jScrollPane1.setViewportView(L1_Affiliation);

        jPanel2.setLayout(new java.awt.GridLayout(5, 1, 0, 10));

        affiL1_Control.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(affiL1_Control);

        insertL1_Button.setText("jButton1");
        jPanel2.add(insertL1_Button);

        modifyL1_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modifyL1_Button.setFont(new Font(font_Type, font_Style, font_Size));
        modifyL1_Button.setMnemonic('M');
        modifyL1_Button.setText(MODIFY_BTN.getContent());
        modifyL1_Button.setEnabled(false);
        modifyL1_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyL1_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyL1_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyL1_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyL1_ButtonActionPerformed(evt);
            }
        });
        jPanel2.add(modifyL1_Button);

        deleteL1_Button.setText("jButton3");
        jPanel2.add(deleteL1_Button);

        cancelL1_Button.setText("jButton4");
        jPanel2.add(cancelL1_Button);

        L1_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        L1_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        L1_Title.setText(AFFILIATION_LIST_LABEL.getContent());
        L1_Title.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                L1_TitleMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout all_L1_PanelLayout = new javax.swing.GroupLayout(all_L1_Panel);
        all_L1_Panel.setLayout(all_L1_PanelLayout);
        all_L1_PanelLayout.setHorizontalGroup(
            all_L1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, all_L1_PanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(all_L1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(L1_Title, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(all_L1_PanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        all_L1_PanelLayout.setVerticalGroup(
            all_L1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(all_L1_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L1_Title)
                .addGap(16, 16, 16)
                .addGroup(all_L1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        L2_Affiliation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane2.setViewportView(L2_Affiliation);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(affiL2_Control)
                .addGap(0, 75, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(affiL2_Control)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        L2_Title.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        L2_Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        L2_Title.setText(LOWER_LIST_LABEL.getContent());
        L2_Title.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                L2_TitleMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout all_L2_PanelLayout = new javax.swing.GroupLayout(all_L2_Panel);
        all_L2_Panel.setLayout(all_L2_PanelLayout);
        all_L2_PanelLayout.setHorizontalGroup(
            all_L2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, all_L2_PanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(all_L2_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L2_Title, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        all_L2_PanelLayout.setVerticalGroup(
            all_L2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(all_L2_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L2_Title)
                .addGap(14, 14, 14)
                .addGroup(all_L2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setMinimumSize(new java.awt.Dimension(100, 40));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 40));

        workPanel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        workPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        workPanel.setText(WORK_PANEL_LABEL.getContent());
        workPanel.setMaximumSize(new java.awt.Dimension(140, 28));
        workPanel.setMinimumSize(new java.awt.Dimension(50, 26));
        workPanel.setPreferredSize(new java.awt.Dimension(90, 26));
        jPanel5.add(workPanel);

        workPanelName.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        workPanelName.setText("소속, 부서");
        workPanelName.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        workPanelName.setMaximumSize(new java.awt.Dimension(200, 28));
        workPanelName.setMinimumSize(new java.awt.Dimension(51, 26));
        workPanelName.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel5.add(workPanelName);
        jPanel5.add(filler3);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(MODE_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(120, 28));
        jLabel2.setMinimumSize(new java.awt.Dimension(50, 26));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel5.add(jLabel2);

        modeString.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modeString.setText(SEARCH.getContent());
        modeString.setMaximumSize(new java.awt.Dimension(120, 28));
        modeString.setMinimumSize(new java.awt.Dimension(34, 26));
        modeString.setPreferredSize(new java.awt.Dimension(80, 26));
        jPanel5.add(modeString);

        csHelpLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        csHelpLabel.setForeground(Color.gray);
        csHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        csHelpLabel.setText("자료 입력 후 탭 혹은 엔터 키로 저장/적용할 것!");
        csHelpLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        csHelpLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        csHelpLabel.setMaximumSize(new java.awt.Dimension(230, 30));
        csHelpLabel.setPreferredSize(new java.awt.Dimension(230, 28));
        csHelpLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        closePanel.setMaximumSize(new Dimension(4000, 70));
        closePanel.setMinimumSize(new Dimension(150, 70));
        closePanel.setPreferredSize(new Dimension(40, 70));
        closePanel.setLayout(new javax.swing.BoxLayout(closePanel, javax.swing.BoxLayout.X_AXIS));

        leftButtons.setMaximumSize(new java.awt.Dimension(370, 70));
        leftButtons.setMinimumSize(new java.awt.Dimension(370, 70));
        leftButtons.setPreferredSize(new java.awt.Dimension(370, 70));
        leftButtons.setLayout(new java.awt.GridBagLayout());

        deleteAll_Affiliation.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteAll_Affiliation.setMnemonic('E');
        deleteAll_Affiliation.setText(DELETE_ALL_BTN.getContent());
        deleteAll_Affiliation.setEnabled(false);
        deleteAll_Affiliation.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        deleteAll_Affiliation.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        deleteAll_Affiliation.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        deleteAll_Affiliation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAll_AffiliationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        leftButtons.add(deleteAll_Affiliation, gridBagConstraints);

        readSheet.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet.setMnemonic('O');
        readSheet.setText(READ_ODS_BTN.getContent());
        readSheet.setToolTipText("");
        readSheet.setEnabled(false);
        readSheet.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        readSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        leftButtons.add(readSheet, gridBagConstraints);

        saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveSheet_Button.setMnemonic('A');
        saveSheet_Button.setText(SAVE_ODS_BTN.getContent());
        saveSheet_Button.setEnabled(false);
        saveSheet_Button.setMaximumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setMinimumSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.setPreferredSize(new Dimension(buttonWidthWide, buttonHeightNorm));
        saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSheet_ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        leftButtons.add(saveSheet_Button, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        leftButtons.add(ODSAffiliHelp, gridBagConstraints);

        sampleButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        sampleButton.setMnemonic('S');
        sampleButton.setText(SAMPLE2_BTN.getContent());
        sampleButton.setToolTipText(DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD.getContent());
        sampleButton.setEnabled(false);
        sampleButton.setMaximumSize(new java.awt.Dimension(80, 30));
        sampleButton.setMinimumSize(new java.awt.Dimension(80, 30));
        sampleButton.setPreferredSize(new java.awt.Dimension(80, 30));
        sampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        leftButtons.add(sampleButton, gridBagConstraints);

        closePanel.add(leftButtons);
        closePanel.add(filler8);

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
        closePanel.add(closeFormButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(all_L1_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(all_L2_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(csHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(closePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(csHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(all_L1_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(all_L2_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private ODS_TYPE chosenPanelFor() {
        if (affiL1_Control.isSelected() || affiL2_Control.isSelected()) {
            return AFFILIATION;
        } else {
            return BUILDING;
        }
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

        saveSheet_Button.setEnabled(false);
        
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
            if (isManager) {
                saveSheet_Button.setEnabled(true);
            }
        }
        else
        {
            modifyL1_Button.setEnabled(false);                
            deleteL1_Button.setEnabled(false);                      
//            loadL2_Affiliation(null, 0, "");
        }
        //</editor-fold>
    }    
    
    private void deleteAll_AffiliationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAll_AffiliationActionPerformed
        int result = JOptionPane.showConfirmDialog(this,
            AFFILIATION_DELETE_ALL_DAILOG.getContent(),
            DELETE_ALL_DAILOGTITLE.getContent(),
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            deleteTable(this, L1_affiliation, null, L1_AFFILI_ROW.getContent());
            loadL1_Affiliation(0, "");
        }
    }//GEN-LAST:event_deleteAll_AffiliationActionPerformed

    private void readSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheetActionPerformed
//        if (currTable ==  L1_TABLE || currTable == L2_TABLE)
//        {
//            loadAffiliationsFromODS();
//        } else {
//            loadBuildingsFromODS();
//        }
    }//GEN-LAST:event_readSheetActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
//        if (affiL1_Control.isSelected()) {
//            saveODSfileName(this, L1_Affiliation, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), L1_TABLE.getContent());
//        } else if (affiL2_Control.isSelected()) {
//            saveODSfileName(this, L2_Affiliation, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), affiBotTitle.getText());
//        } else if (buildingControl.isSelected()) {
//            saveODSfileName(this, BuildingTable, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), Building.getContent());
//        } else if (unitControl.isSelected()) {
//            saveODSfileName(this, UnitTable, odsFileChooser,
//                USER_SAVE_ODS_FAIL_DIALOG.getContent(), UnitLabel.getText());
//        }
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void ODSAffiliHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ODSAffiliHelpActionPerformed
        JDialog helpDialog;

        if (chosenPanelFor() == AFFILIATION) {
            helpDialog = new ODS_HelpJDialog(this, false,
                HELP_AFFIL_LABEL.getContent(),
                ODS_TYPE.AFFILIATION);
        } else {
            helpDialog = new ODS_HelpJDialog(this, false,
                HELP_BUILDING_LABEL.getContent(),
                ODS_TYPE.BUILDING);
        }
        setHelpDialogLoc(ODSAffiliHelp, helpDialog);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_ODSAffiliHelpActionPerformed

    private void sampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleButtonActionPerformed
        String sampleFile = "";

        if (chosenPanelFor() == AFFILIATION) {
            sampleFile = "/affiliations";
        } else {
            sampleFile = "/buildings";
        }

        // Ask user the name and location for the ods file to save
        StringBuffer odsFullPath = new StringBuffer();

//        if (wantToSaveFile(this, saveFileChooser, odsFullPath, sampleFile)) {
//            // Read sample ods resource file
//            String extension = saveFileChooser.getFileFilter().getDescription();
//
//            if (extension.indexOf("*.ods") >= 0 && !odsFullPath.toString().endsWith(".ods")) {
//                odsFullPath.append(".ods");
//            }
//
//            InputStream sampleIn = getClass().getResourceAsStream(sampleFile + ".ods");
//
//            downloadSample(odsFullPath.toString(), sampleIn, sampleFile);
//            if (sampleIn != null) {
//                try {
//                    sampleIn.close();
//                } catch (IOException e) {
//                    logParkingException(Level.SEVERE, e, sampleFile + " istrm close error");
//                }
//            }
//        }
    }//GEN-LAST:event_sampleButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        tryToCloseSettingsForm();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void L1_TitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_L1_TitleMouseClicked
        affiL1_Control.setSelected(true);
    }//GEN-LAST:event_L1_TitleMouseClicked

    private void L2_TitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_L2_TitleMouseClicked
        affiL2_Control.setSelected(true);
    }//GEN-LAST:event_L2_TitleMouseClicked

    private void modifyL1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyL1_ButtonActionPerformed
        prepareModifyingAffiBldg(L1_Affiliation, modifyL1_Button, cancelL1_Button);
    }//GEN-LAST:event_modifyL1_ButtonActionPerformed

    private void tryToCloseSettingsForm() {
        if (mainForm != null) {
            mainForm.getTopForms()[ControlEnums.TopForms.Settings.ordinal()] = null;
        }

        if (isStand_Alone) {
            this.setVisible(false);
            System.exit(0);
        } else {
            dispose();
        }  
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
            java.util.logging.Logger.getLogger(AffiliationGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AffiliationGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AffiliationGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AffiliationGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (loginID != null ||
                        loginID == null && findLoginIdentity() != null) 
                {                
                    AffiliationGUI runForm = new AffiliationGUI(null);
                    runForm.setVisible(true);
                    runForm.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable L1_Affiliation;
    private javax.swing.JLabel L1_Title;
    private javax.swing.JTable L2_Affiliation;
    private javax.swing.JLabel L2_Title;
    private javax.swing.JButton ODSAffiliHelp;
    private javax.swing.JRadioButton affiL1_Control;
    private javax.swing.JRadioButton affiL2_Control;
    private javax.swing.JPanel all_L1_Panel;
    private javax.swing.JPanel all_L2_Panel;
    private javax.swing.JButton cancelL1_Button;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JLabel csHelpLabel;
    private javax.swing.JButton deleteAll_Affiliation;
    private javax.swing.JButton deleteL1_Button;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton insertL1_Button;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel leftButtons;
    private javax.swing.JLabel modeString;
    private javax.swing.JButton modifyL1_Button;
    private javax.swing.JButton readSheet;
    private javax.swing.JButton sampleButton;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JLabel workPanel;
    private javax.swing.JLabel workPanelName;
    // End of variables declaration//GEN-END:variables

    /**
     * @param affiliL1_Editable the affiliL1_Editable to set
     */
    public void setAffiliL1_Editable(boolean affiliL1_Editable) {
        this.affiliL1_Editable = affiliL1_Editable;
    }

    private void adjustTables() {
        adjustAffiliationTable(L1_Affiliation);
        adjustAffiliationTable(L2_Affiliation);
        addAffiliationSelectionListener();      
        
        /**
         * Make table column heading click select that table's radio button.
         */
        L1_Affiliation.getTableHeader().addMouseListener(new HdrMouseListener(L1_Affiliation));
        L2_Affiliation.getTableHeader().addMouseListener(new HdrMouseListener(L2_Affiliation));
        
        /**
         * Make table cell 
         * click select that table's radio button,
         * double click initiate cell modification.
         */        
        addClickEventListener(L1_Affiliation);
        addClickEventListener(L2_Affiliation);
    }
    
    private void addClickEventListener(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable)me.getSource();
                Point p = me.getPoint();
                TableType tabType = (table == L1_Affiliation ? L1_TABLE : L2_TABLE);
                
                if (table.columnAtPoint(p) == 0) {
                    setRadioButtonFor(table);
                } else {
                    if (me.getClickCount() == 1) {
                        setRadioButtonFor(table);
                    } else 
                    if (me.getClickCount() == 2) {
                        if (table == L1_Affiliation) {
                            prepareModifyingAffiBldg(table, modifyL1_Button, cancelL1_Button);                        
                        } else {
//                            prepareModifyingAffiBldg(table, modifyL2_Button, cancelL2_Button); 
                        }
                    }
                }
            }
        });
    }    
    
    /**
     * @param formMode the formMode to set
     */
    public void setFormMode(ControlEnums.FormMode formMode) {
        this.formMode = formMode;
        
        switch (formMode) {
            case CreateMode:
                modeString.setText(CREATE.getContent());
                changeRadioButtonsEnabled(false);
                changeBottomButtonsEnbled(false);   
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
                        CREATE_SAVE_HELP.getContent(), !createBlinked);  
                createBlinked = true;
                adminOperationEnabled(false, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;   
                
            case NormalMode:
                modeString.setText(SEARCH.getContent());
                changeRadioButtonsEnabled(true);
                changeBottomButtonsEnbled(true);
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, 
                        CHOOSE_PANEL_DIALOG.getContent(), !normalBlinked); 
                normalBlinked = true;
                adminOperationEnabled(true, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            case UpdateMode:
                modeString.setText(MODIFY.getContent());
                changeRadioButtonsEnabled(false);
                changeBottomButtonsEnbled(false);   
                (new LabelBlinker()).displayHelpMessage(csHelpLabel, UPDATE_SAVE_HELP.getContent(), 
                        !updateBlinked);                  
                updateBlinked = true;
                adminOperationEnabled(false, deleteAll_Affiliation, ODSAffiliHelp, sampleButton, readSheet);                
                break;
                
            default:
                break;
        } 
    }  
    
    private void changeRadioButtonsEnabled(boolean b) {
        affiL1_Control.setEnabled(b);
        changeControlEnabledForTable(L1_TABLE);
        affiL2_Control.setEnabled(b);
        changeControlEnabledForTable(L2_TABLE);
    }    
    
    
    private void changeControlEnabledForTable(TableType table) {
        switch (table) {
            case L1_TABLE: 
                changeItemsEnabled(L1_Affiliation, affiL1_Control.isSelected(),
                        insertL1_Button, modifyL1_Button, deleteL1_Button);
                break;
            case L2_TABLE: 
//                changeItemsEnabled(L2_Affiliation, affiL2_Control.isSelected(),
//                        insertL2_Button, modifyL2_Button, deleteL2_Button);
//                if (!affiL2_Control.isSelected()) {
//                    addDummyFirstRow((DefaultTableModel)L2_Affiliation.getModel());
//                }
                break;
            default:
                break;
        }
    }   

    private boolean firstRowIsDummy(JTable table) {
	if (table.getRowCount() == 1) {
            Object lineNo = table.getModel().getValueAt(0, 0); // Line number column value
            
            if (lineNo == null) {
                return false;
            } else {
                if (lineNo.getClass() == String.class) {
                    if (((String)lineNo).length() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private int followAndGetTrueIndex(JTable theTable) {
        theTable.scrollRectToVisible(
                new Rectangle(theTable.getCellRect(theTable.getSelectedRow(), 0, true)));
        if (theTable.getSelectedRow() == -1)
            return -1;
        else
            return theTable.convertRowIndexToModel(theTable.getSelectedRow());                
    }
        
    
    private void changeItemsEnabled(JTable table, boolean selected,
            JButton insertButton, JButton modifyButton, JButton deleteButton)
    {
        if (firstRowIsDummy(table)) {
            ((DefaultTableModel)table.getModel()).setRowCount(0);
        }
        
        int idx = followAndGetTrueIndex(table);    
        
        //<editor-fold desc="-- Change selected panel border">
        if (selected) {
            if (table == L1_Affiliation) {
                all_L1_Panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                L1_Title.setForeground(pointColor);
            } else {
                all_L2_Panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
                L2_Title.setForeground(pointColor);
            }
        } else {
            if (table == L1_Affiliation) {
                all_L1_Panel.setBorder(null);
                L1_Title.setForeground(black);
            } else {
                all_L2_Panel.setBorder(null);
                L2_Title.setForeground(black);
            }
        }
        //</editor-fold>
        if (selected) {
            if (idx == -1 && table.getRowCount() > 0) {
                idx = 0;
                table.setRowSelectionInterval(idx, idx);
            }
        }
        
        if (idx >= 0)
        {
            Object L1_no = table.getModel().getValueAt(idx, 2);
            boolean enable = L1_no != null 
                    && selected 
                    && formMode == ControlEnums.FormMode.NormalMode;
            if (isManager) {
                modifyButton.setEnabled(enable);
                deleteButton.setEnabled(enable);
            } else {
                modifyButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        }
        
        if (isManager) {
            insertButton.setEnabled(formMode == ControlEnums.FormMode.NormalMode && selected);
        } else {
            insertButton.setEnabled(false);
        }
        table.setEnabled(selected);
        if (selected) {
//            workPanelName.setText(((KorTable)table).getTableType().getContent());
            workPanelName.setText("dontknow");
        }
    }    
    
    private void prepareModifyingAffiBldg(JTable table, JButton modifyBtn, JButton cancelBtn) {
        int rowIndex = table.getSelectedRow();
        int model_index = table.convertRowIndexToModel(rowIndex);
        
        TableModel model = table.getModel();
        int result = JOptionPane.NO_OPTION;
        
        if (table == L1_Affiliation) {
            prevL1Name = model.getValueAt(model_index, 1);
            result = getUserConfirmationL1(model_index);
        } else {
            prevL2Name = model.getValueAt(model_index, 1);
            result = getUserConfirmationL2(model_index);
        }
        
        if (result == JOptionPane.YES_OPTION) {
            setAffiliL1_Editable(true);
            TableCellEditorKor editor = new TableCellEditorKor(KOREAN, L1_Affiliation.getFont());
            System.out.println("Editor 1: " + editor);
        
            L1_Affiliation.getColumnModel().getColumn(1).setCellEditor(editor);              
            
            TableCellEditor editor2 = table.getCellEditor(model_index, 1);
            System.out.println("Editor 2: " + editor2);
            if (table.editCellAt(model_index, 1)) 
            {
                table.getEditorComponent().requestFocus();
                setFormMode(ControlEnums.FormMode.UpdateMode);
                modifyBtn.setEnabled(false);
                cancelBtn.setEnabled(true);    
            }
        }
    }  

    private int getUserConfirmationL2(int model_index) {
        String nameL1 = L1_Affiliation.getModel().getValueAt(model_index, 1).toString();
        String dialog = AFFILI2_DIAG_L1.getContent() + System.getProperty("line.separator") 
                + AFFILI2_DIAG_L2.getContent() + prevL2Name + System.getProperty("line.separator") 
                + AFFILI2_DIAG_L3.getContent() + nameL1;
                        
        return JOptionPane.showConfirmDialog(this, dialog,
                LOWER_MODIFY_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION);     
    }    
    
    private void addAffiliationSelectionListener() {
        ListSelectionModel cellSelectionModel = L1_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
//                            int index1 = followAndGetTrueIndex(L1_Affiliation);
//
//                            if (index1 >= 0) 
//                            {
//                                Object L1_no = L1_Affiliation.getModel().getValueAt(index1, 2);
//                                modifyL1_Button.setEnabled(L1_no != null && isManager ? true : false);
//                                deleteL1_Button.setEnabled(L1_no != null && isManager ? true : false);
//                                loadL2_Affiliation(L1_no, 0, "");
//                            }
//                            else
//                            {
//                                // loadL2_Affiliation(null, 0, ""); abcd
//                            }
//
//                            // clear L2List selection
//                            L2_Affiliation.removeEditor();
//                            L2_Affiliation.getSelectionModel().clearSelection();  
//
//                            // Delete an empty row if existed
//                            if (emptyLastRowPossible(insertL1_Button, L1_Affiliation))
//                            {
//                                removeEmptyRow(insertL1_Button, L1_Affiliation);                    
//                            }
                        }
                    }
                });                 
            }
        }); 
        
        cellSelectionModel = L2_Affiliation.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            public void valueChanged(ListSelectionEvent  e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (!e.getValueIsAdjusting())
                        {
//                            int index2 = followAndGetTrueIndex(L2_Affiliation);
//                            if (index2 >= 0)
//                            {
//                                Object L2_no = L2_Affiliation.getModel().getValueAt(index2, 2);
//                                deleteL2_Button.setEnabled(L2_no != null && isManager ? true : false); 
//                                modifyL2_Button.setEnabled(L2_no != null && isManager ? true : false);
//                            }
//                            else
//                            {
//                                deleteL2_Button.setEnabled(false);     
//                                modifyL2_Button.setEnabled(false);     
//                            }
//
//                            // Delete an empty row if existed
//                            if (emptyLastRowPossible(insertL2_Button, L2_Affiliation))
//                            {
//                                removeEmptyRow(insertL2_Button, L2_Affiliation);
//                            }
                        }
                    }
                });
            }
        });
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

    private int getUserConfirmationL1(int model_index) {
        TableModel model = L1_Affiliation.getModel();
        int L1_no = (int)model.getValueAt(model_index, 2);
        String dialog = AFFILI_DIAG_L1.getContent() + System.getProperty("line.separator") 
                + AFFILI_DIAG_L2.getContent() + prevL1Name + System.getProperty("line.separator") 
                + AFFILI_DIAG_L3.getContent() + getL2RecordCount(L1_no); 

        return JOptionPane.showConfirmDialog(this, dialog,
                AFFILIATION_MODIFY_DIALOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION); 
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

    private void changeBottomButtonsEnbled(boolean b) {
        if (loginID.equals(ADMIN_ID)) {
            deleteAll_Affiliation.setEnabled(b);
        } else {
            deleteAll_Affiliation.setEnabled(false);
        }
        if (isManager) {
            saveSheet_Button.setEnabled(b);
            readSheet.setEnabled(b);
        } else {
            saveSheet_Button.setEnabled(false);
            readSheet.setEnabled(false);
        }
        closeFormButton.setEnabled(b);
    }
    
    private class HdrMouseListener extends MouseAdapter {
        JTable table = null;
        private HdrMouseListener(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent evt) {
            setRadioButtonFor(table);
        }

    }    
    
    private void setRadioButtonFor(JTable table) {
        if (table == L1_Affiliation) {
            affiL1_Control.setSelected(true);
        } else if (table == L2_Affiliation) {
            affiL2_Control.setSelected(true);
        }
    }
}
