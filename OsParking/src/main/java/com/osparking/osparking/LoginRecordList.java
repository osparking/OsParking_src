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

import static com.osparking.global.CommonData.ODS_DIRECTORY;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.showCount;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.DataSheet.saveODSfile;
import com.osparking.global.names.JDBCMySQL;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import static com.osparking.global.names.DB_Access.locale;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SEARCH_BTN;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.USER_CB_ITEM;
import static com.osparking.global.names.ControlEnums.DialogMessages.PERIOD_ERROR_DIALOG1;
import static com.osparking.global.names.ControlEnums.DialogMessages.PERIOD_ERROR_DIALOG2;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.PERIOD_ERROR_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.ID_LABEL_STR;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.ControlEnums.TableTypes.LOGIN_TIME_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOGOUT_TIME_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.USER_ID_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.WORK_PERIOD;
import static com.osparking.global.names.ControlEnums.TitleTypes.LOGIN_RECORD_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.ToolTipContent.ENABLING_CONDITION;
import com.osparking.global.names.OdsFileOnly;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Open Source Parking Inc.
 */
public class LoginRecordList extends javax.swing.JFrame {
    private static Logger logException = null;
    private String prevSearchCondition = null;    
    private String currSearchCondition = "";   
    /**
     * Creates new form LoginRecordList
     */
    public LoginRecordList() {
        initComponents();
        setIconImages(OSPiconList);
        
        BeginDateChooser.setLocale(locale);
        EndDateChooser.setLocale(locale);
        Date today = new Date();
        BeginDateChooser.setDate(getDateFromGivenDate(today, -7));
        EndDateChooser.setDate(today);
        
        initUserIDComboBox();
        detailTuneTableProperties();
        LoginRecordTable.setAutoCreateRowSorter(true);
        RefreshTableContents(); 
        
        PropertyChangeListener dateChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("date")) {
                    changeSearchButtonEnabled();
                }
            }
        };
        
        BeginDateChooser.getDateEditor().addPropertyChangeListener(dateChangeListener);
        EndDateChooser.getDateEditor().addPropertyChangeListener(dateChangeListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveFileChooser = new javax.swing.JFileChooser();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0));
        wholePanel = new javax.swing.JPanel();
        LoginRecordListTopPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        datePanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(70, 0));
        ID_Label = new javax.swing.JLabel();
        UserIDComboBox = new javax.swing.JComboBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0));
        BeginDateChooser = new com.toedter.calendar.JDateChooser();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0));
        jLabel2 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0));
        EndDateChooser = new com.toedter.calendar.JDateChooser();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(70, 0));
        searchButton = new javax.swing.JButton();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jScrollPane1 = new javax.swing.JScrollPane();
        LoginRecordTable = new javax.swing.JTable();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel1 = new javax.swing.JPanel();
        countPanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        saveSheet_Button = new javax.swing.JButton();
        CloseFormButton = new javax.swing.JButton();
        southPanel = new javax.swing.JPanel();

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setCurrentDirectory(ODS_DIRECTORY);
        saveFileChooser.setFileFilter(new OdsFileOnly());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(LOGIN_RECORD_FRAME_TITLE.getContent());
        setMinimumSize(new Dimension(825, normGUIheight));
        setPreferredSize(new Dimension(825, normGUIheight));
        setResizable(false);
        getContentPane().add(filler10, java.awt.BorderLayout.LINE_END);
        getContentPane().add(filler8, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(filler11, java.awt.BorderLayout.LINE_START);

        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.Y_AXIS));

        LoginRecordListTopPanel.setLayout(new javax.swing.BoxLayout(LoginRecordListTopPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(LOGIN_RECORD_FRAME_TITLE.getContent());
        jLabel3.setFocusable(false);
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.setMaximumSize(new java.awt.Dimension(32767, 40));
        jLabel3.setMinimumSize(new java.awt.Dimension(105, 40));
        jLabel3.setPreferredSize(new java.awt.Dimension(105, 40));
        titlePanel.add(jLabel3);

        LoginRecordListTopPanel.add(titlePanel);
        LoginRecordListTopPanel.add(filler13);

        datePanel.setMinimumSize(new java.awt.Dimension(700, 40));
        datePanel.setPreferredSize(new java.awt.Dimension(700, 40));
        datePanel.setLayout(new javax.swing.BoxLayout(datePanel, javax.swing.BoxLayout.LINE_AXIS));
        datePanel.add(filler2);

        ID_Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        ID_Label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ID_Label.setText("아이디");
        ID_Label.setMaximumSize(new java.awt.Dimension(90, 40));
        ID_Label.setMinimumSize(new java.awt.Dimension(90, 40));
        ID_Label.setPreferredSize(new java.awt.Dimension(90, 40));
        datePanel.add(ID_Label);

        UserIDComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        UserIDComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        UserIDComboBox.setMaximumSize(new java.awt.Dimension(115, 28));
        UserIDComboBox.setMinimumSize(new java.awt.Dimension(115, 28));
        UserIDComboBox.setPreferredSize(new java.awt.Dimension(115, 28));
        UserIDComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserIDComboBoxActionPerformed(evt);
            }
        });
        UserIDComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                UserIDComboBoxKeyTyped(evt);
            }
        });
        datePanel.add(UserIDComboBox);
        datePanel.add(filler4);

        BeginDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BeginDateChooser.setMaximumSize(new java.awt.Dimension(130, 33));
        BeginDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        BeginDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));
        BeginDateChooser.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                BeginDateChooserComponentHidden(evt);
            }
        });
        datePanel.add(BeginDateChooser);
        datePanel.add(filler5);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("~");
        jLabel2.setFocusable(false);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        datePanel.add(jLabel2);
        datePanel.add(filler6);

        EndDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EndDateChooser.setMaximumSize(new java.awt.Dimension(130, 33));
        EndDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        EndDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));
        datePanel.add(EndDateChooser);
        datePanel.add(filler1);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setToolTipText(ENABLING_CONDITION.getContent());
        searchButton.setMaximumSize(new java.awt.Dimension(90, 40));
        searchButton.setMinimumSize(new java.awt.Dimension(90, 40));
        searchButton.setPreferredSize(new java.awt.Dimension(90, 40));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        datePanel.add(searchButton);

        LoginRecordListTopPanel.add(datePanel);

        wholePanel.add(LoginRecordListTopPanel);
        wholePanel.add(filler12);

        LoginRecordTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LoginRecordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
            },
            new String [] {
                ORDER_LABEL.getContent(), USER_ID_HEADER.getContent(),
                LOGIN_TIME_HEADER.getContent(), LOGOUT_TIME_HEADER.getContent(),
                WORK_PERIOD.getContent()
            }
        ));
        LoginRecordTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LoginRecordTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        LoginRecordTable.setEnabled(false);
        LoginRecordTable.setRowHeight(28);
        jScrollPane1.setViewportView(LoginRecordTable);

        wholePanel.add(jScrollPane1);
        wholePanel.add(filler9);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 40));
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 40));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 40));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        countPanel.setMinimumSize(new java.awt.Dimension(100, 25));

        countLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        countLbl.setText(COUNT_LABEL.getContent());
        JLabel tempLabel = new JLabel(COUNT_LABEL.getContent());
        tempLabel.setFont(countLbl.getFont());
        Dimension dim = tempLabel.getPreferredSize();
        countLbl.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        countLbl.setMaximumSize(new java.awt.Dimension(110, 27));
        countLbl.setMinimumSize(new java.awt.Dimension(90, 27));
        countLbl.setPreferredSize(new Dimension(dim.width + 1, dim.height));

        countValue.setForeground(pointColor);
        countValue.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countValue.setText("count");
        countValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveSheet_Button.setMnemonic('A');
        saveSheet_Button.setText("ods저장(A)");
        saveSheet_Button.setEnabled(false);
        saveSheet_Button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveSheet_Button.setMaximumSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.setMinimumSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.setPreferredSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSheet_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout countPanelLayout = new javax.swing.GroupLayout(countPanel);
        countPanel.setLayout(countPanelLayout);
        countPanelLayout.setHorizontalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(countValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 378, Short.MAX_VALUE)
                .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        countPanelLayout.setVerticalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGroup(countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(countValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel1.add(countPanel);

        CloseFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        CloseFormButton.setMnemonic('c');
        CloseFormButton.setText(CLOSE_BTN.getContent());
        CloseFormButton.setMaximumSize(new java.awt.Dimension(90, 40));
        CloseFormButton.setMinimumSize(new java.awt.Dimension(90, 40));
        CloseFormButton.setPreferredSize(new java.awt.Dimension(90, 40));
        CloseFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseFormButtonActionPerformed(evt);
            }
        });
        jPanel1.add(CloseFormButton);

        wholePanel.add(jPanel1);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        getDatesRefreshTable();     
    }//GEN-LAST:event_searchButtonActionPerformed

    private void CloseFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseFormButtonActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_CloseFormButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
        saveODSfile(this, LoginRecordTable, saveFileChooser, "");
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void UserIDComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UserIDComboBoxKeyTyped
        changeSearchButtonEnabled();
    }//GEN-LAST:event_UserIDComboBoxKeyTyped

    private void UserIDComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserIDComboBoxActionPerformed
        changeSearchButtonEnabled();
    }//GEN-LAST:event_UserIDComboBoxActionPerformed

    private void BeginDateChooserComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_BeginDateChooserComponentHidden
        System.out.println("Begin");
        // TODO add your handling code here:
    }//GEN-LAST:event_BeginDateChooserComponentHidden

    private void changeSearchButtonEnabled() {
        currSearchCondition = formSearchCondition();
        if (currSearchCondition.equals(prevSearchCondition)) {
            searchButton.setEnabled(false);
        } else {
            searchButton.setEnabled(true);
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
            java.util.logging.Logger.getLogger(LoginRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        if (findLoginIdentity() != null) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new LoginRecordList().setVisible(true);
                }
            });
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser BeginDateChooser;
    private javax.swing.JButton CloseFormButton;
    private com.toedter.calendar.JDateChooser EndDateChooser;
    private javax.swing.JLabel ID_Label;
    private javax.swing.JPanel LoginRecordListTopPanel;
    private javax.swing.JTable LoginRecordTable;
    private javax.swing.JComboBox UserIDComboBox;
    private javax.swing.JLabel countLbl;
    private javax.swing.JPanel countPanel;
    private javax.swing.JLabel countValue;
    private javax.swing.JPanel datePanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables

    private void RefreshTableContents() {
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;        
        StringBuffer sb = new StringBuffer();
        
        //<editor-fold desc="--create select statement">
        sb.append("select recNo as 'Order', userID as 'User ID', ");
        
        sb.append(" concat(date_format(loginTS, '%Y-%m-%d '), ");
        sb.append(" if(date_format(loginTS, '%p') ='AM', 'AM', 'PM'),");
        sb.append(" date_format(loginTS, ' %h:%i:%s')) as 'Login Time', ");
        
        sb.append(" concat(date_format(logoutTS, '%Y-%m-%d '), ");
        sb.append(" if(date_format(logoutTS, '%p') ='AM', 'AM', 'PM'),");
        sb.append(" date_format(logoutTS, ' %h:%i:%s')) as 'Logout Time', ");

        sb.append(" concat( ");
        sb.append(  " lpad(timestampdiff(HOUR, loginTS, logoutTS),");
        sb.append(    " if (timestampdiff(HOUR, loginTS, logoutTS) > 9999, 5,");
        sb.append(    " if (timestampdiff(HOUR, loginTS, logoutTS) > 999, 4,");
        sb.append(    " if (timestampdiff(HOUR, loginTS, logoutTS) > 99, 3, 2))), '0'), ':',");
        sb.append(  " lpad(mod(timestampdiff(MINUTE, loginTS, logoutTS), 60), 2, '0'), ':',");
        sb.append(  " lpad(mod(timestampdiff(SECOND, loginTS, logoutTS), 60), 2, '0')) as 'Duration(hh:mm:ss)' ");
        
        sb.append(" from loginrecord" + currSearchCondition);
        sb.append(" order by recNo desc");
        //</editor-fold>

        DefaultTableModel model = (DefaultTableModel) LoginRecordTable.getModel();  
        
        try {
            conn = JDBCMySQL.getConnection();
            pStmt = conn.prepareStatement(sb.toString());
            rs = pStmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getObject(1), rs.getObject(2), 
                    rs.getObject(3), rs.getObject(4), rs.getObject(5)
                });
            } 
            prevSearchCondition = currSearchCondition;
            searchButton.setEnabled(false);
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(user login record display table: content refreshing)");
        } finally {
            closeDBstuff(conn, pStmt, rs, "accessing login record from DB");
            LoginRecordTable.setPreferredSize(new Dimension(
                    LoginRecordTable.getPreferredSize().width, 
                    LoginRecordTable.getRowHeight() * LoginRecordTable.getRowCount()));
        }
        
        /**
         * Sets a correct <code>comparator</code> method for the first <code>recNo</code>column
         * Without it, the <code>recNo</code>(an integral type primary key) field of the base table
         * (<code>LoginRecordTable</code>) is sorted as strings where a row with '2' 
         * being greater (appears later in the ascending order sorting) than a row with '10'.
         */        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(LoginRecordTable.getModel());       
        sorter.setComparator(0, com.osparking.global.Globals.comparator);
        LoginRecordTable.setRowSorter(sorter);        
        
        showCount(LoginRecordTable, saveSheet_Button, countValue);     
    }       

    @SuppressWarnings("unchecked") 
    private void initUserIDComboBox() {
        UserIDComboBox.removeAllItems();
        UserIDComboBox.addItem(USER_CB_ITEM.getContent());
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = JDBCMySQL.getConnection();            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select id from users_osp order by id");
            while (rs.next()) {
                UserIDComboBox.addItem(rs.getString("id"));
            }
            UserIDComboBox.setSelectedIndex(0);
        } catch(Exception ex) {
            logParkingException(Level.SEVERE, ex, "ID combobox data retrieval");
        } finally {
            closeDBstuff(conn, stmt, rs, "ID combobox data retrieval");
        }
    }

    public void getDatesRefreshTable() {
        Date beginDate = BeginDateChooser.getDate();
        Date endDate = EndDateChooser.getDate();
        
        // Check if both starting and ending dates are entered
        if (beginDate == null || endDate == null) {
            JOptionPane.showConfirmDialog(this, "Please, enter both starting and ending dates!",
                    "Search Range Error", JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);             
        } else {
            // Check if dates are chronologically wrong.
            if (beginDate.after(endDate)) {
                JOptionPane.showConfirmDialog(this, PERIOD_ERROR_DIALOG1.getContent() +
                        System.lineSeparator() + PERIOD_ERROR_DIALOG2.getContent(), 
                PERIOD_ERROR_TITLE.getContent(), JOptionPane.PLAIN_MESSAGE, 
                WARNING_MESSAGE);             
            } else {
                RefreshTableContents();
            }
        }           
    }

    private void detailTuneTableProperties() {
        TableColumnModel tcm = LoginRecordTable.getColumnModel();
        
        SetAColumnWidth(tcm.getColumn(0), 90, 90, 90);
        SetAColumnWidth(tcm.getColumn(1), 110, 110, 110);
        SetAColumnWidth(tcm.getColumn(4), 140, 140, 140);
        
        ((DefaultTableCellRenderer)LoginRecordTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);        
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tcm.getColumn(0).setCellRenderer(numberCellRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tcm.getColumn(1).setCellRenderer(centerRenderer);  
        tcm.getColumn(2).setCellRenderer(centerRenderer);  
        tcm.getColumn(3).setCellRenderer(centerRenderer);  
        tcm.getColumn(4).setCellRenderer(centerRenderer);  
        setComponentSize(ID_Label, new Dimension(90, 40));
        ID_Label.setText(ID_LABEL_STR.getContent());
        
    }

    private String formSearchCondition() {
        if (BeginDateChooser.getDate() == null 
                || UserIDComboBox.getSelectedIndex() == -1) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateFormat.format(BeginDateChooser.getDate());
            String stopDate = dateFormat.format(EndDateChooser.getDate()); 

            String result = " where '" + startDate + 
                    "' <= date(loginTS) and date(loginTS) <= '" + stopDate + "'";

            String user = (String)UserIDComboBox.getSelectedItem();
            if (!user.equals(USER_CB_ITEM.getContent())) {
                result += " and userID = '" + user + "'";
            }
            return result;
        }
    }
}
