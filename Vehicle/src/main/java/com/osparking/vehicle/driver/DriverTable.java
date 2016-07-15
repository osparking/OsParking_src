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

import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.vehicle.driver.ManageDrivers.driverTable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.ControlEnums.DialogMessages.DRIVER_UPDATE_QUEST_P1;
import static com.osparking.global.names.ControlEnums.DialogMessages.DRIVER_UPDATE_QUEST_P2;
import static com.osparking.global.names.ControlEnums.DialogMessages.DRIVER_UPDATE_QUEST_P3;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.MODIFY_DAILOGTITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import com.osparking.global.names.InnoComboBoxItem;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.PComboBox;
import com.osparking.vehicle.CommonData;
import static com.osparking.vehicle.CommonData.refreshComboBox;
import static com.osparking.vehicle.driver.ManageDrivers.getPrompter;
import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DriverTable extends JTable {

    /**
     * table model modRow index for the driver currently being modified
     * 
     */
    static int updateRow = -1;    
    
    ManageDrivers parent = null;
    
    public DriverTable(Object[][] rowData, Object[] columnNames, ManageDrivers parent) {
        this.parent = parent;
        setModel(new DriverTableModel(rowData, columnNames, parent));
        
        ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);        
        
        for (DriverCol col : DriverCol.values()) {
            if (col == DriverCol.RowNo ||
                    col == DriverCol.BuildingNo ||
                    col == DriverCol.UnitNo) 
            {
                getColumnModel().getColumn(col.getNumVal()).setCellRenderer(numberCellRenderer);
            } else if (col != DriverCol.SEQ_NO) {
                getColumnModel().getColumn(col.getNumVal()).setCellRenderer(centerRenderer);
            }
        }
        
        setRowHeight(tableRowHeight);        
    }
    
    public boolean isCellEditable(int modRow, int column) {
        if ((parent.getFormMode() == FormMode.CreateMode ||
                parent.getFormMode() == FormMode.UpdateMode)
                && column != DriverCol.RowNo.getNumVal()) 
        {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean userWantsToUpdateRow(final int rowV, final int colV) {
        int rowM = driverTable.convertRowIndexToModel(rowV);

        parent.setUpdateMode(true);
        updateRow = rowM;
        return true;
    } 
    
    public int askUserOnUpdate(String name, int vCount) {
        
        String optionMessage = DRIVER_UPDATE_QUEST_P1.getContent() + System.getProperty("line.separator") 
                + DRIVER_UPDATE_QUEST_P2.getContent() + name 
                + DRIVER_UPDATE_QUEST_P3.getContent() + vCount + ")";
        JOptionPane optionPane = new JOptionPane(optionMessage,
                JOptionPane.QUESTION_MESSAGE, 
                JOptionPane.YES_NO_OPTION, null, null, null); // options[0]);
        JDialog dialog = optionPane.createDialog(MODIFY_DAILOGTITLE.getContent());
        
        dialog.addKeyListener(new KeyListener () {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("key pressed");
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        Set<AWTKeyStroke> focusTraversalKeys = new HashSet<AWTKeyStroke>(dialog.getFocusTraversalKeys(0));
        focusTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_UNDEFINED));
        focusTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_UNDEFINED));
        dialog.setFocusTraversalKeys(0, focusTraversalKeys);
        dialog.setVisible(true);
        dialog.dispose();
        return (Integer) optionPane.getValue();
    }

   /**
    * Returns the cell editor for each cell in the vehicle driver list table.
    * @param row row index of the cell to edit.
    * @param column column index of the cell to edit.
    * @return the editor usable for the cell to edit.
    */
    public TableCellEditor getCellEditor(int row, int column) {   
        /** 
         * row index translation for the table model
         */
        int modRow = ManageDrivers.driverTable.convertRowIndexToModel(row);
        /** 
         * column index translation for the table model
         */        
        int modCol = ManageDrivers.driverTable.convertColumnIndexToModel(column);
        
        /**
         * If a driver is being created or updated and the row index differs from it
         * then return null editor.
         */
        int tabSize = driverTable.getRowCount();
        if (parent.getFormMode() == FormMode.UpdateMode && modRow != updateRow 
                ||
                parent.getFormMode() == FormMode.CreateMode && modRow != tabSize -1)
        {
            return null;
        }

        /**
         * The cell editor to return.
         */
        TableCellEditor cellEditor = null; 
        
        // <editor-fold defaultstate="collapsed" desc="-- ComboBox Cell Editor">        
        if (modCol == AffiliationL2.getNumVal() || modCol == UnitNo.getNumVal()) {
            //<editor-fold desc="-- Two lower level comboboxes">
            DriverCol thisCol;
            Object parentObj;
            
            if (modCol == AffiliationL2.getNumVal()) {
                parentObj = getValueAt(modRow, AffiliationL1.getNumVal() );
                thisCol = AffiliationL2;
            } else {
                parentObj = getValueAt(modRow, BuildingNo.getNumVal() );
                thisCol = UnitNo;
            }

            int parentKey = (Integer)(((ConvComboBoxItem)parentObj).getKeyValue());            
            System.out.println("parentKey: " + parentKey);
            
            TableColumn cBoxCol = driverTable.getColumnModel().getColumn(modCol);        
            PComboBox<InnoComboBoxItem> comboBox = (PComboBox<InnoComboBoxItem>)
                    ((DefaultCellEditor)cBoxCol.getCellEditor()).getComponent();

            // Construct combo box item list only when needed.
            int len = comboBox.getItemCount();
            System.out.println("len: " + len + ", CommonData.getPrevParentKey(thisCol): " 
                    + CommonData.getPrevParentKey(thisCol) + ", parentKey: " + parentKey);
            if (len == 0 || CommonData.getPrevParentKey(thisCol) != parentKey) {
                System.out.println(thisCol.toString() + " refreshing................");
                Object thisObj = driverTable.getValueAt(modRow, modCol);
                Object prompter = getPrompter(thisCol, parentObj);
                
                refreshComboBox(comboBox, prompter, thisCol, parentKey);
                comboBox.setSelectedItem((InnoComboBoxItem)thisObj);
                cellEditor = new DefaultCellEditor(comboBox);
            } else {
                cellEditor = new DefaultCellEditor(comboBox);
            }
            //</editor-fold>
        } else if (modCol == DriverCol.AffiliationL1.getNumVal()) {
            cellEditor = new DefaultCellEditor(ManageDrivers.affiliationL1CBox); 
        } else if (modCol == DriverCol.BuildingNo.getNumVal()) {
            cellEditor = new DefaultCellEditor(ManageDrivers.buildingCBox); 
        } else {
            cellEditor = super.getCellEditor(modRow, modCol);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="-- Enter Key Handler ">            
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        Object itemSource = e.getSource();

                        if (itemSource instanceof PComboBox) {
                            Object cBoxItem = ((PComboBox)e.getSource()).getHighlightedCbxItem();               
                            ((PComboBox)itemSource).setSelectedItem(cBoxItem);
                            
                        }
                        int currCol = driverTable.getSelectedColumn(), nextCol;
                        
                        nextCol = (currCol == UnitNo.getNumVal()) ? 1 : currCol + 1;
                        if (driverTable.editCellAt(driverTable.getSelectedRow(), nextCol))
                        {
                            parent.startEditingCell(driverTable.getSelectedRow(), nextCol);
                        }                          
                    }
                });                  
            }
        };
        JComponent compo = (JComponent)((DefaultCellEditor)cellEditor).getComponent();
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);  
        // </editor-fold>
        return cellEditor;
    }

    public static void finalizeDataEntry(ManageDrivers parent){
      
        if (driverTable.getCellEditor() != null) {
            driverTable.getCellEditor().stopCellEditing();
        }
        
        if (parent.getFormMode() == FormMode.CreateMode) {
            parent.finalizeDriverCreation();
        } else {
            int rowV = driverTable.getSelectedRow();
            parent.finalizeDriverUpdate(rowV);
        }
    }
}