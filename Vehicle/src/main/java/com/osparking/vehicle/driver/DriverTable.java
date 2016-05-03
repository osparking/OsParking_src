/* 
 * Copyright (C) 2015 Open Source Parking Inc.(www.osparking.com)
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

import static com.osparking.vehicle.driver.ManageDrivers.driverTable;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.Globals.emptyLastRowPossible;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.MODIFY_DAILOGTITLE;
import com.osparking.global.names.InnoComboBoxItem;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.OSP_enums.FormMode;
import com.osparking.global.names.PComboBox;
import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JDialog;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DriverTable extends JTable {

    /**
     * table model row index for the driver currently being modified
     * 
     */
    static int modifyingRowM = -1;    
    
    ManageDrivers parent = null;
    
    public DriverTable(Object[][] rowData, Object[] columnNames, ManageDrivers parent) {
        this.parent = parent;
        setModel(new DriverTableModel(rowData, columnNames, parent));
    }   
    
    public boolean isCellEditable(int row, int column) {
        if ((parent.getFormMode() == FormMode.CREATION ||
                parent.getFormMode() == FormMode.MODIFICATION)
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
        modifyingRowM = rowM;
        return true;
    } 
    
    public int askUserOnUpdate(String name, int vCount) {
        
        String optionMessage = "";
        
        switch(language){
            case KOREAN :
                optionMessage = "해당 운전자 정보를 수정하시겟습니까?"+ System.getProperty("line.separator") + 
                        " - 운전자 이름 : "+ name + "(소유 차량 : " + vCount + "대)";
                break;
                
            case ENGLISH:
                optionMessage = "Do you want to Modify driver information?" + System.getProperty("line.separator") + 
                        " - Driver name: " + name + " (owns " + vCount + " cars)";
                break;
                
            default :
                break;
        }        
        
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
//        int response = (Integer) optionPane.getValue();           
        return (Integer) optionPane.getValue();
    }

    public TableCellEditor getCellEditor(int row, int column) {   
        int modRow = ManageDrivers.driverTable.convertRowIndexToModel(row);
        int modCol = ManageDrivers.driverTable.convertColumnIndexToModel(column);
        
        Object keyObj = ManageDrivers.driverTable.getModel().getValueAt(modRow, 
                DriverCol.SEQ_NO.getNumVal());
        
        // if new driver is being created and the cell is outside the new driver, then quit
        int tabSize = driverTable.getRowCount();
        if (parent.getFormMode() == FormMode.MODIFICATION && row != modifyingRowM 
                ||
                parent.getFormMode() == FormMode.CREATION && row != tabSize -1)
        {
            return null;
        }


        TableCellEditor cellEditor = null; 
        
        // <editor-fold defaultstate="collapsed" desc="-- Make ComboBox for the Cell">                        
        if (column == DriverCol.AffiliationL2.getNumVal()) {
            Object itemObj = getValueAt(row, DriverCol.AffiliationL1.getNumVal() );
            int L1_NO = (Integer)(((ConvComboBoxItem)itemObj).getValue());
            
            TableColumn comboCol = driverTable.getColumnModel().getColumn(column);        
            PComboBox<InnoComboBoxItem> comboBox = (PComboBox<InnoComboBoxItem>)
                    ((DefaultCellEditor)comboCol.getCellEditor()).getComponent();
            
            comboBox.removeAllItems();
            comboBox.addItem( (InnoComboBoxItem) 
                    ManageDrivers.getPrompter (DriverCol.AffiliationL2, driverTable.getValueAt(
                            driverTable.getSelectedRow(), AffiliationL1.getNumVal()) ) );
            parent.loadComboBoxItems(comboBox, DriverCol.AffiliationL2, L1_NO);
            comboBox.setEditable(true);
            Object item = driverTable.getValueAt(driverTable.getSelectedRow(), column);
            comboBox.setSelectedItem((InnoComboBoxItem)item);
            
            cellEditor = new DefaultCellEditor(comboBox); 
        } else if (column == DriverCol.UnitNo.getNumVal()) {
            Object itemObj = getValueAt(row, DriverCol.BuildingNo.getNumVal());
            int bldgSeqNo = (Integer)(((ConvComboBoxItem)itemObj).getValue());
            
            TableColumn comboCol = driverTable.getColumnModel().getColumn(column);        
            PComboBox<InnoComboBoxItem> comboBox = (PComboBox<InnoComboBoxItem>)
                    ((DefaultCellEditor)comboCol.getCellEditor()).getComponent();

            comboBox.removeAllItems();
            comboBox.addItem( (InnoComboBoxItem) 
                    ManageDrivers.getPrompter (DriverCol.UnitNo, driverTable.getValueAt(
                            driverTable.getSelectedRow(), BuildingNo.getNumVal()) ) );    
            comboBox.setEditable(true);
            Object item = driverTable.getValueAt(driverTable.getSelectedRow(), column);
            comboBox.setSelectedItem((InnoComboBoxItem)item);            
            
            parent.loadComboBoxItems(comboBox, DriverCol.UnitNo, bldgSeqNo);
            
            cellEditor = new DefaultCellEditor(comboBox);
        } else if (column == DriverCol.AffiliationL1.getNumVal()) {
            
            cellEditor = new DefaultCellEditor(ManageDrivers.affiliationL1CBox); 
            cellEditor.addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {                     
                            int rIdx = driverTable.getSelectedRow();
                            
                            driverTable.setValueAt(ManageDrivers.getPrompter(AffiliationL2, 
                                    driverTable.getValueAt(rIdx, AffiliationL1.getNumVal())),
                                    rIdx, AffiliationL2.getNumVal());   
                        }
                    });
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                }
            });

        } else if (column == DriverCol.BuildingNo.getNumVal()) {
            
            cellEditor = new DefaultCellEditor(ManageDrivers.buildingCBox); 
            cellEditor.addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {                     
                            int rIdx = driverTable.getSelectedRow();
                            
                            driverTable.setValueAt(ManageDrivers.getPrompter(UnitNo,
                                    driverTable.getValueAt(rIdx, BuildingNo.getNumVal())),
                                    rIdx, UnitNo.getNumVal());                               
                        }
                    });
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                }
            });
        } else {
            cellEditor = super.getCellEditor(row, column);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="-- Enter Key Handler ">            
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                
                if (e.getSource().getClass() == PComboBox.class) {
                    PComboBox comboBox = (PComboBox)e.getSource();
                    if (comboBox.isPopupVisible()) {
                        comboBox.setSelectedItem(comboBox.getHighlightedCbxItem());
                    } else {
                        // finalize update or insert operation here
                        int curRow = driverTable.getSelectedRow();
                        if (curRow < driverTable.getRowCount() - 1)
                            driverTable.setRowSelectionInterval(curRow + 1, curRow + 1);
                        finalizeDataEntry(parent);
                    }
                } else {
                    // finalize update or insert operation here
                    finalizeDataEntry(parent);
                }                           
            }
        };
        
        JComponent compo = (JComponent)((DefaultCellEditor)cellEditor).getComponent();
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);  
        // </editor-fold>
        
        if (column >= DriverCol.AffiliationL1.getNumVal()
            && column <= DriverCol.UnitNo.getNumVal())
        {
            Component comp = ((DefaultCellEditor)cellEditor).getComponent();
            comp.addMouseListener(new MouseAdapter() {
                public void mousePressed (MouseEvent me) { 
                   
                    if (me.getClickCount() == 2 && !me.isConsumed()) {
                        me.consume();
                    
                        if (emptyLastRowPossible(parent.createDriver_Button, driverTable))
                        {
                            removeEmptyRow(parent.createDriver_Button, driverTable);
                        }                        

                        if (parent.getFormMode() != FormMode.MODIFICATION) {
                            Point cBoxLoc = ((PComboBox)me.getSource()).getLocation();
                            int rowV = driverTable.rowAtPoint(cBoxLoc);                                    
                            int colV = driverTable.columnAtPoint(cBoxLoc);
                            ((DriverTable)driverTable).userWantsToUpdateRow(rowV, colV);
                        }
                    }
                }
            });
        }
        
        return cellEditor;
    }
        
    public static void finalizeDataEntry(ManageDrivers parent){
        System.out.println("finalizeDataEntry called");
        if (parent.getFormMode() == FormMode.CREATION) {
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
                parent.finalizeDriverCreation();
            }
            parent.setFormMode(FormMode.SEARCHING);
        } else if (parent.getFormMode() == FormMode.MODIFICATION) {
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
                int rowV = driverTable.getSelectedRow();
                parent.finalizeDriverUpdate(rowV);
            }
            parent.setFormMode(FormMode.SEARCHING);
        }        
    }
}