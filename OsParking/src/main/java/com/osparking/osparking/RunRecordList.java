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
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.showCount;
import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.DataSheet.saveODSfile;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.SetAColumnWidth;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.getDateFromGivenDate;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.setComponentSize;
import static com.osparking.global.names.ControlEnums.ButtonTypes.CLOSE_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.SEARCH_BTN;
import static com.osparking.global.names.ControlEnums.ColumnHeader.OPTN_START;
import static com.osparking.global.names.ControlEnums.ColumnHeader.OPTN_STOP;
import static com.osparking.global.names.ControlEnums.ColumnHeader.STOP_DURATION;
import static com.osparking.global.names.ControlEnums.DialogMessages.PERIOD_ERROR_DIALOG1;
import static com.osparking.global.names.ControlEnums.DialogMessages.PERIOD_ERROR_DIALOG2;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.PERIOD_ERROR_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_PERIOD_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.ControlEnums.TitleTypes.RUN_RECORD_FRAME_TITLE;
import static com.osparking.global.names.DB_Access.parkingLotLocale;
import static com.osparking.global.names.DB_Access.readSettings;
import com.osparking.global.names.JDBCMySQL;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author YongSeok
 */
public class RunRecordList extends javax.swing.JFrame {
    private String prevSearchCondition = null;    
    private String currSearchCondition = ""; 
    
    /**
     * Creates new form RunRecordList2
     */
    public RunRecordList() {
        initComponents();
        
        setIconImages(OSPiconList);
        detailTuneTableProperties();
        
        BeginDateChooser.setLocale(parkingLotLocale);
        EndDateChooser.setLocale(parkingLotLocale);
        Date today = new Date();
        BeginDateChooser.setDate(getDateFromGivenDate(today, -7));
        EndDateChooser.setDate(today);
        
        changeSearchButtonEnabled();
        RunRecordTable.setAutoCreateRowSorter(true);
        
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
        loadRunRecordTable();  
    }
    
    private void changeSearchButtonEnabled() {
        currSearchCondition = formSearchCondition();
        if (currSearchCondition.equals(prevSearchCondition)) {
            searchButton.setEnabled(false);
        } else {
            searchButton.setEnabled(true);
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

        saveFileChooser = new javax.swing.JFileChooser();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0));
        wholePanel = new javax.swing.JPanel();
        LoginRecordListTopPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        leftSideLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        myMetaKeyLabel = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        datePanel = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(91, 0), new java.awt.Dimension(91, 0), new java.awt.Dimension(91, 0));
        horiGlueL = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        horiGlueR = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        periodPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        BeginDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        EndDateChooser = new com.toedter.calendar.JDateChooser();
        searchButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        RunRecordTable = new javax.swing.JTable();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        closePanel = new javax.swing.JPanel();
        countPanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveSheet_Button = new javax.swing.JButton();
        CloseFormButton = new javax.swing.JButton();

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(RUN_RECORD_FRAME_TITLE.getContent());
        setMaximumSize(new Dimension(800, normGUIheight));
        setMinimumSize(new Dimension(800, normGUIheight));
        setPreferredSize(new Dimension(800, normGUIheight));
        setResizable(false);
        getContentPane().add(filler9, java.awt.BorderLayout.SOUTH);
        getContentPane().add(filler8, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(filler11, java.awt.BorderLayout.LINE_START);
        getContentPane().add(filler10, java.awt.BorderLayout.LINE_END);

        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        LoginRecordListTopPanel.setLayout(new javax.swing.BoxLayout(LoginRecordListTopPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        leftSideLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        leftSideLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        leftSideLabel.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        titlePanel.add(leftSideLabel);

        titleLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(RUN_RECORD_FRAME_TITLE.getContent());
        titleLabel.setFocusable(false);
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        titleLabel.setMaximumSize(new java.awt.Dimension(32767, 40));
        titleLabel.setMinimumSize(new java.awt.Dimension(76, 40));
        titleLabel.setPreferredSize(new java.awt.Dimension(76, 40));
        titlePanel.add(titleLabel);

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        titlePanel.add(myMetaKeyLabel);

        LoginRecordListTopPanel.add(titlePanel);
        LoginRecordListTopPanel.add(filler4);

        datePanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        datePanel.setMinimumSize(new java.awt.Dimension(0, 40));
        datePanel.setPreferredSize(new java.awt.Dimension(638, 40));

        periodPanel.setMaximumSize(new java.awt.Dimension(420, 40));
        periodPanel.setMinimumSize(new java.awt.Dimension(420, 40));
        periodPanel.setPreferredSize(new java.awt.Dimension(420, 40));
        periodPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setText(SEARCH_PERIOD_LABEL.getContent());
        periodPanel.add(jLabel1);

        BeginDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        BeginDateChooser.setMaximumSize(new java.awt.Dimension(32767, 28));
        BeginDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        BeginDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));
        periodPanel.add(BeginDateChooser);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("~");
        jLabel2.setFocusable(false);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        periodPanel.add(jLabel2);

        EndDateChooser.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        EndDateChooser.setMaximumSize(new java.awt.Dimension(32767, 28));
        EndDateChooser.setMinimumSize(new java.awt.Dimension(130, 33));
        EndDateChooser.setPreferredSize(new java.awt.Dimension(130, 33));
        periodPanel.add(EndDateChooser);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setMaximumSize(new java.awt.Dimension(90, 40));
        searchButton.setMinimumSize(new java.awt.Dimension(90, 40));
        searchButton.setPreferredSize(new java.awt.Dimension(90, 40));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setHorizontalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(periodPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(horiGlueR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
                    .addContainerGap(550, Short.MAX_VALUE)
                    .addComponent(horiGlueL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(88, 88, 88)))
        );
        datePanelLayout.setVerticalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(datePanelLayout.createSequentialGroup()
                .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(datePanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(horiGlueR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
            .addComponent(searchButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(periodPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, datePanelLayout.createSequentialGroup()
                    .addContainerGap(36, Short.MAX_VALUE)
                    .addComponent(horiGlueL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)))
        );

        LoginRecordListTopPanel.add(datePanel);

        wholePanel.add(LoginRecordListTopPanel);

        RunRecordTable.setAutoCreateRowSorter(true);
        RunRecordTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        RunRecordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                ORDER_LABEL.getContent(), OPTN_STOP.getContent(),
                OPTN_START.getContent(), STOP_DURATION.getContent()
            }
        ));
        RunRecordTable.setEnabled(false);
        RunRecordTable.setRowHeight(28);
        RunRecordTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jScrollPane1.setViewportView(RunRecordTable);

        wholePanel.add(jScrollPane1);
        wholePanel.add(filler2);

        closePanel.setMaximumSize(new java.awt.Dimension(32767, 40));

        countPanel.setMinimumSize(new java.awt.Dimension(100, 25));

        countLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        countLbl.setText(COUNT_LABEL.getContent());
        JLabel tempLabel = new JLabel(COUNT_LABEL.getContent());
        tempLabel.setFont(countLbl.getFont());
        Dimension dim = tempLabel.getPreferredSize();
        countLbl.setMaximumSize(new java.awt.Dimension(110, 27));
        countLbl.setMinimumSize(new java.awt.Dimension(90, 27));
        countLbl.setPreferredSize(new Dimension(dim.width + 1, dim.height));

        countValue.setForeground(pointColor);
        countValue.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countValue.setText("count");

        javax.swing.GroupLayout countPanelLayout = new javax.swing.GroupLayout(countPanel);
        countPanel.setLayout(countPanelLayout);
        countPanelLayout.setHorizontalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(countValue)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        countPanelLayout.setVerticalGroup(
            countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(countPanelLayout.createSequentialGroup()
                .addGroup(countPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(countValue)
                    .addComponent(countLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

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

        javax.swing.GroupLayout closePanelLayout = new javax.swing.GroupLayout(closePanel);
        closePanel.setLayout(closePanelLayout);
        closePanelLayout.setHorizontalGroup(
            closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(closePanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(countPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(CloseFormButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        closePanelLayout.setVerticalGroup(
            closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(closePanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(countPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(closePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(saveSheet_Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(CloseFormButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        wholePanel.add(closePanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseFormButtonActionPerformed
        dispose();
    }//GEN-LAST:event_CloseFormButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
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
                loadRunRecordTable();
            }
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
        saveODSfile(this, RunRecordTable, saveFileChooser, "");
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

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
            java.util.logging.Logger.getLogger(RunRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RunRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RunRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RunRecordList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RunRecordList().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser BeginDateChooser;
    private javax.swing.JButton CloseFormButton;
    private com.toedter.calendar.JDateChooser EndDateChooser;
    private javax.swing.JPanel LoginRecordListTopPanel;
    private javax.swing.JTable RunRecordTable;
    private javax.swing.JPanel closePanel;
    private javax.swing.JLabel countLbl;
    private javax.swing.JPanel countPanel;
    private javax.swing.JLabel countValue;
    private javax.swing.JPanel datePanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.Box.Filler horiGlueL;
    private javax.swing.Box.Filler horiGlueR;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel leftSideLabel;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JPanel periodPanel;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    private void loadRunRecordTable() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null; 
        StringBuffer sb = new StringBuffer();
        
        sb.append("Select recNo as '" + ORDER_LABEL.getContent() + "', ");        
        sb.append(" concat(date_format(stopTm, '%Y-%m-%d '), ");
        sb.append(" if(date_format(stopTm, '%p') ='AM', 'AM', 'PM'),");
        sb.append(" date_format(stopTm, ' %h:%i:%s')) as '" + OPTN_STOP.getContent() + "', ");
        sb.append(" concat(date_format(startTm, '%Y-%m-%d '), ");
        sb.append(" if(date_format(startTm, '%p') ='AM', 'AM', 'PM'),");
        sb.append(" date_format(startTm, ' %h:%i:%s')) as '" + OPTN_START.getContent() + "', ");
        
        sb.append(" concat( ");
        sb.append("   lpad(timestampdiff(HOUR, stopTm, startTm),");
        sb.append(    " if (timestampdiff(HOUR, stopTm, startTm) > 9999, 5,");
        sb.append(    " if (timestampdiff(HOUR, stopTm, startTm) > 999, 4,");
        sb.append(    " if (timestampdiff(HOUR, stopTm, startTm) > 99, 3, 2))), '0'), ':',");        
        sb.append("   lpad(mod(timestampdiff(MINUTE, stopTm, startTm), 60), 2, '0'), '.', ");
        sb.append("   lpad(mod(timestampdiff(SECOND, stopTm, startTm), 60), 2, '0')) as '" +
                STOP_DURATION.getContent() + "' ");
        sb.append("FROM SystemRun Where " + currSearchCondition);
        sb.append(" order by recNo desc");
        
        DefaultTableModel model = (DefaultTableModel) RunRecordTable.getModel();  
        
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getObject(1), rs.getObject(2), 
                    rs.getObject(3), rs.getObject(4)
                });
            }   
            prevSearchCondition = currSearchCondition;
            searchButton.setEnabled(false);            
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(System Operation Record List: Content Refresh Module)");
        } finally {
            closeDBstuff(conn, stmt, rs, 
                    "(System Operation Record List: Content Refresh Module)");
            RunRecordTable.setPreferredSize(
                    new Dimension(RunRecordTable.getPreferredSize().width, 
                            RunRecordTable.getRowHeight() * RunRecordTable.getRowCount()));            
        }
        
        /**
         * Sets a correct <code>comparator</code> method for the first <code>id</code>column
         * Without it, the <code>id</code>(an integral type identifier) field of the base table
         * (<code>RunRecordTable</code>) is sorted as strings where a row with '2' 
         * being greater (appears later in the ascending order sorting) than a row with '10'.
         */
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(RunRecordTable.getModel());
        sorter.setComparator(0, com.osparking.global.Globals.comparator);
        RunRecordTable.setRowSorter(sorter);   
        
        showCount(RunRecordTable, saveSheet_Button, countValue);     
    }

    private void detailTuneTableProperties() {
        setComponentSize(searchButton, 
                new Dimension(buttonWidthNorm, buttonHeightNorm));
        setComponentSize(CloseFormButton, 
                new Dimension(buttonWidthNorm, buttonHeightNorm));
        
        TableColumnModel tcm = RunRecordTable.getColumnModel();
        
        ((DefaultTableCellRenderer)RunRecordTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);        
        
        tcm.getColumn(0).setCellRenderer(numberCellRenderer); // order : right alignment 
 
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tcm.getColumn(1).setCellRenderer(centerRenderer); // dates
        tcm.getColumn(2).setCellRenderer(centerRenderer);  
        tcm.getColumn(3).setCellRenderer(centerRenderer);  
        
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(0), 90, 90, 90); // line number        
    }

    private String formSearchCondition() {
        if (BeginDateChooser.getDate() == null) {
            return "";
        } else {        
            StringBuffer sb = new StringBuffer();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");        
            String dateFromStr = dateFormat.format(BeginDateChooser.getDate());
            String dateToStr = dateFormat.format(EndDateChooser.getDate());

            sb.append("(('");
            sb.append(dateFromStr);
            sb.append("' <= date(stopTm) and date(stopTm) <= '" + dateToStr + "') or ('");
            sb.append(dateFromStr);
            sb.append("' <= date(startTm) and date(startTm) <= '" + dateToStr + "'))");
            
            return sb.toString();
        }
    }
}
