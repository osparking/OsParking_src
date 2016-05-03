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
package com.osparking.vehicle;

import com.osparking.vehicle.driver.DriverSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.*;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.*;
import static com.osparking.global.names.ControlEnums.LabelContent.CAR_TAG_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CELL_PHONE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.EXACT_COMP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.FORM_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODIFY_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODI_DATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.NOTIFICATION_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.ORDER_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.OTHER_INFO_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PARK_ALLOWED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.PHONE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REASON_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REGI_DATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.TitleTypes.VEHICLESFORM_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.CAR_TAG_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.DRIVER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOW_HIGH_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ORDER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.OTHER_INFO_HEDER;
import static com.osparking.global.names.ControlEnums.TableTypes.REASON_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.ROOM_BUILD_HEADER;
import static com.osparking.global.names.ControlEnums.TextType.*;
import static com.osparking.global.names.ControlEnums.ToolTipContent.AFFILIATION_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.BUILDING_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CAR_TAG_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.OTHER_TOOLTIP;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.JTextFieldLimit;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.FormMode;
import com.osparking.global.names.OSP_enums.VehicleCol;
import com.osparking.global.names.OdsFileOnly;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.WrappedInt;
import com.osparking.vehicle.driver.ODSReader;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.logging.Logger;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTable;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
/**
 *
 * @author Open Source Parking Inc.
 */
public class VehiclesForm extends javax.swing.JFrame {

    static VehiclesForm mySelf = null;
    private FormMode formMode; 
    private DriverObj driverObj = new DriverObj("", 0);
    final int UNKNOWN = -1;
    
    /**
     * Creates new form Vehicles
     */
    public VehiclesForm() {
        initComponents();
        
        setIconImages(OSPiconList);
        
        attachEnterHandler(searchCarTag);
        attachEnterHandler(searchDriver);
        attachEnterHandler(searchAffiliCBox);
        attachEnterHandler(searchBldgCBox);
        attachEnterHandler(searchETC);
        
        setFormMode(FormMode.SEARCHING);
        loadSearchBox();
        attachEventListenerToVehicleTable();
        loadVehicleTable(0, "");
        driverTextField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (driverTextField.getText().trim().length() == 0) {
                    openDriverSelectionForm(mySelf);
                } else {    
                    cellTextField.requestFocus();
                }
            }

        });  
        mySelf = this;
        
        // limit the number of characters that can be entered to the (dis)allow reason field
        reasonTextField.setDocument(new JTextFieldLimit(20));
    }

    private void openDriverSelectionForm(VehiclesForm mySelf) {
        int seqNo = 0; 
        if (formMode == formMode.MODIFICATION)
            seqNo = Integer.parseInt((String)vehiclesTable.getModel().getValueAt(
                    vehiclesTable.getSelectedRow(), VehicleCol.SeqNo.getNumVal()));
        new DriverSelection(mySelf, seqNo).setVisible(true);
    }
            
    private void fineTuneColumnWidth() {
        TableColumnModel tcm = vehiclesTable.getColumnModel();
       
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(VehicleCol.RowNo.getNumVal()), 85, 85, 85); // 0: row number
        SetAColumnWidth(tcm.getColumn(VehicleCol.PlateNumber.getNumVal()), 120, 120, 200); // 1: vehicle tag number
        SetAColumnWidth(tcm.getColumn(VehicleCol.Name.getNumVal()), 95, 95, 32767); // 2: driver name
        SetAColumnWidth(tcm.getColumn(VehicleCol.Affiliation.getNumVal()), 10, 120, 32767); // 3: affiliation level 1, 2
        SetAColumnWidth(tcm.getColumn(VehicleCol.Building.getNumVal()), 10, 120, 200); // 4: building and unit no
        SetAColumnWidth(tcm.getColumn(VehicleCol.OtherInfo.getNumVal()), 10, 120, 32767); // 5: etc info
        SetAColumnWidth(tcm.getColumn(VehicleCol.CellPhone.getNumVal()), 0, 0, 0); // 6: cellphone
        SetAColumnWidth(tcm.getColumn(VehicleCol.Phone.getNumVal()), 0, 0, 0); // 7: LandLine
        SetAColumnWidth(tcm.getColumn(VehicleCol.Notification.getNumVal()), 0, 0, 0); // 8: NOTification
        SetAColumnWidth(tcm.getColumn(VehicleCol.Whole.getNumVal()), 0, 0, 0); // 9: extra comp`
        SetAColumnWidth(tcm.getColumn(VehicleCol.Permitted.getNumVal()), 0, 0, 0); // 10: Alloed
        SetAColumnWidth(tcm.getColumn(VehicleCol.Causes.getNumVal()), 10, 150, 32767); // 11: reason
        SetAColumnWidth(tcm.getColumn(VehicleCol.Creation.getNumVal()), 0, 0, 0); // 12: registered On
        SetAColumnWidth(tcm.getColumn(VehicleCol.Modification.getNumVal()), 0, 0, 0); // 13: Modify On
        SetAColumnWidth(tcm.getColumn(VehicleCol.SeqNo.getNumVal()), 0, 0, 0); // 14: drvseqNo
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
        odsFileChooser = new javax.swing.JFileChooser();
        wholePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler62 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel3 = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler61 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeFormButton = new javax.swing.JButton();
        filler63 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(10, 32767));
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        leftPanel = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 80), new java.awt.Dimension(0, 80), new java.awt.Dimension(32767, 80));
        jPanel8 = new javax.swing.JPanel();
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        rowNumTextField = new javax.swing.JTextField();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel11 = new javax.swing.JPanel();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        carTagTextField = new javax.swing.JTextField();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel12 = new javax.swing.JPanel();
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        selectDriverButton = new javax.swing.JButton();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        driverTextField = new javax.swing.JTextField();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel23 = new javax.swing.JPanel();
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        cellTextField = new javax.swing.JTextField();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel24 = new javax.swing.JPanel();
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        phoneTextField = new javax.swing.JTextField();
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel30 = new javax.swing.JPanel();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel10 = new javax.swing.JLabel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        lastModiTextField = new javax.swing.JTextField();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel25 = new javax.swing.JPanel();
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jLabel7 = new javax.swing.JLabel();
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        notiCheckBox = new javax.swing.JCheckBox();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel26 = new javax.swing.JPanel();
        filler45 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jLabel8 = new javax.swing.JLabel();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        wholeCheckBox = new javax.swing.JCheckBox();
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel27 = new javax.swing.JPanel();
        filler46 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jLabel13 = new javax.swing.JLabel();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        permitCheckBox = new javax.swing.JCheckBox();
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel28 = new javax.swing.JPanel();
        filler47 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel14 = new javax.swing.JLabel();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        reasonTextField = new javax.swing.JTextField();
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel29 = new javax.swing.JPanel();
        filler48 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel15 = new javax.swing.JLabel();
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        otherInfoTextField = new javax.swing.JTextField();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel31 = new javax.swing.JPanel();
        filler49 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        jLabel9 = new javax.swing.JLabel();
        filler25 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        creationTextField = new javax.swing.JTextField();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        filler72 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel1 = new javax.swing.JPanel();
        filler74 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler73 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        countLabel = new javax.swing.JLabel();
        filler54 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        centerPanel = new javax.swing.JPanel();
        centerFirstPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel12 = new javax.swing.JLabel();
        filler50 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        formModeLabel = new javax.swing.JLabel();
        filler51 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        searchPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        filler64 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 32767));
        searchCarTag = new javax.swing.JTextField();
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchDriver = new javax.swing.JTextField();
        filler67 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchAffiliCBox = new PComboBox();
        filler68 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchBldgCBox = new PComboBox();
        filler69 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchETC = new javax.swing.JTextField();
        filler71 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(1, 0), new java.awt.Dimension(32767, 0));
        clearButton = new javax.swing.JButton();
        filler70 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        vehiclesTable = new javax.swing.JTable();
        filler65 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        centerThridPanel = new javax.swing.JPanel();
        filler53 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveSheet_Button = new javax.swing.JButton();
        filler55 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        readSheet_Button = new javax.swing.JButton();
        filler56 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        deleteAllVehicles = new javax.swing.JButton();
        filler57 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        deleteCancel_Button = new javax.swing.JButton();
        filler58 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        modiSave_Button = new javax.swing.JButton();
        filler59 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        insertSave_Button = new javax.swing.JButton();
        filler60 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(VEHICLESFORM_FRAME_TITLE.getContent());
        setMinimumSize(new java.awt.Dimension(1350, 750));
        setPreferredSize(new java.awt.Dimension(1250, 750));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wholePanel.setMinimumSize(new java.awt.Dimension(1190, 1100));
        wholePanel.setPreferredSize(new java.awt.Dimension(1190, 1100));
        wholePanel.setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(filler62, java.awt.BorderLayout.PAGE_END);

        topPanel.add(jPanel2);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        jPanel3.add(seeLicenseButton);
        jPanel3.add(filler61);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText(CLOSE_BTN.getContent());
        closeFormButton.setMaximumSize(new java.awt.Dimension(90, 40));
        closeFormButton.setMinimumSize(new java.awt.Dimension(90, 40));
        closeFormButton.setPreferredSize(new java.awt.Dimension(90, 40));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        jPanel3.add(closeFormButton);
        jPanel3.add(filler63);

        topPanel.add(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(VEHICLESFORM_FRAME_TITLE.getContent());
        jLabel1.setMaximumSize(new java.awt.Dimension(113, 30));
        jLabel1.setMinimumSize(new java.awt.Dimension(113, 30));
        jLabel1.setPreferredSize(new java.awt.Dimension(113, 30));
        jPanel4.add(jLabel1, java.awt.BorderLayout.CENTER);

        topPanel.add(jPanel4);

        wholePanel.add(topPanel, java.awt.BorderLayout.NORTH);

        leftPanel.setMaximumSize(new java.awt.Dimension(393204, 32767));
        leftPanel.setPreferredSize(new java.awt.Dimension(320, 362));
        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.PAGE_AXIS));
        leftPanel.add(filler14);

        jPanel8.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jPanel8.add(filler38);

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText(ORDER_LABEL.getContent());
        jLabel2.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel2.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel2.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel8.add(jLabel2);
        jPanel8.add(filler2);

        rowNumTextField.setEditable(false);
        rowNumTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        rowNumTextField.setEnabled(false);
        rowNumTextField.setFocusable(false);
        jPanel8.add(rowNumTextField);
        jPanel8.add(filler39);

        leftPanel.add(jPanel8);
        leftPanel.add(filler3);

        jPanel11.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));
        jPanel11.add(filler37);

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(CAR_TAG_LABEL.getContent());
        jLabel3.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel3.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel3.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel11.add(jLabel3);
        jPanel11.add(filler15);

        carTagTextField.setEditable(false);
        carTagTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jPanel11.add(carTagTextField);
        jPanel11.add(filler36);

        leftPanel.add(jPanel11);
        leftPanel.add(filler4);

        jPanel12.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.LINE_AXIS));
        jPanel12.add(filler40);

        selectDriverButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        selectDriverButton.setText(OWNER_BTN.getContent());
        selectDriverButton.setEnabled(false);
        selectDriverButton.setFocusable(false);
        selectDriverButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectDriverButton.setMargin(new java.awt.Insets(0, 7, 0, 7));
        selectDriverButton.setMaximumSize(new java.awt.Dimension(90, 27));
        selectDriverButton.setMinimumSize(new java.awt.Dimension(90, 27));
        selectDriverButton.setPreferredSize(new java.awt.Dimension(90, 27));
        selectDriverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectDriverButtonActionPerformed(evt);
            }
        });
        selectDriverButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectDriverButtonKeyReleased(evt);
            }
        });
        jPanel12.add(selectDriverButton);
        jPanel12.add(filler16);

        driverTextField.setEditable(false);
        driverTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                driverTextFieldMouseClicked(evt);
            }
        });
        jPanel12.add(driverTextField);
        jPanel12.add(filler35);

        leftPanel.add(jPanel12);
        leftPanel.add(filler5);

        jPanel23.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel23.setLayout(new javax.swing.BoxLayout(jPanel23, javax.swing.BoxLayout.LINE_AXIS));
        jPanel23.add(filler41);

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText(CELL_PHONE_LABEL.getContent());
        jLabel5.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel5.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel5.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel23.add(jLabel5);
        jPanel23.add(filler17);

        cellTextField.setEditable(false);
        cellTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cellTextField.setEnabled(false);
        cellTextField.setFocusable(false);
        jPanel23.add(cellTextField);
        jPanel23.add(filler34);

        leftPanel.add(jPanel23);
        leftPanel.add(filler6);

        jPanel24.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel24.setLayout(new javax.swing.BoxLayout(jPanel24, javax.swing.BoxLayout.LINE_AXIS));
        jPanel24.add(filler42);

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText(PHONE_LABEL.getContent());
        jLabel6.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel6.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel24.add(jLabel6);
        jPanel24.add(filler18);

        phoneTextField.setEditable(false);
        phoneTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        phoneTextField.setEnabled(false);
        phoneTextField.setFocusable(false);
        jPanel24.add(phoneTextField);
        jPanel24.add(filler33);

        leftPanel.add(jPanel24);
        leftPanel.add(filler8);

        jPanel30.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel30.setLayout(new javax.swing.BoxLayout(jPanel30, javax.swing.BoxLayout.LINE_AXIS));
        jPanel30.add(filler43);

        jLabel10.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText(MODI_DATE_LABEL.getContent());
        jLabel10.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel10.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel10.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel30.add(jLabel10);
        jPanel30.add(filler19);

        lastModiTextField.setEditable(false);
        lastModiTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        lastModiTextField.setEnabled(false);
        lastModiTextField.setFocusable(false);
        jPanel30.add(lastModiTextField);
        jPanel30.add(filler32);

        leftPanel.add(jPanel30);
        leftPanel.add(filler9);

        jPanel25.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel25.setLayout(new javax.swing.BoxLayout(jPanel25, javax.swing.BoxLayout.LINE_AXIS));
        jPanel25.add(filler44);

        jLabel7.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText(NOTIFICATION_LABEL.getContent());
        jLabel7.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel7.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel7.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel25.add(jLabel7);
        jPanel25.add(filler20);

        notiCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        notiCheckBox.setEnabled(false);
        notiCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        notiCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        jPanel25.add(notiCheckBox);

        leftPanel.add(jPanel25);
        leftPanel.add(filler7);

        jPanel26.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel26.setLayout(new javax.swing.BoxLayout(jPanel26, javax.swing.BoxLayout.LINE_AXIS));
        jPanel26.add(filler45);

        jLabel8.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText(EXACT_COMP_LABEL.getContent());
        jLabel8.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel8.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel8.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel26.add(jLabel8);
        jPanel26.add(filler21);

        wholeCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        wholeCheckBox.setEnabled(false);
        wholeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wholeCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        jPanel26.add(wholeCheckBox);
        jPanel26.add(filler30);

        leftPanel.add(jPanel26);
        leftPanel.add(filler10);

        jPanel27.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel27.setLayout(new javax.swing.BoxLayout(jPanel27, javax.swing.BoxLayout.LINE_AXIS));
        jPanel27.add(filler46);

        jLabel13.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText(PARK_ALLOWED_LABEL.getContent());
        jLabel13.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel13.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel13.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel27.add(jLabel13);
        jPanel27.add(filler22);

        permitCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        permitCheckBox.setEnabled(false);
        permitCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        permitCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
        jPanel27.add(permitCheckBox);
        jPanel27.add(filler29);

        leftPanel.add(jPanel27);
        leftPanel.add(filler11);

        jPanel28.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel28.setLayout(new javax.swing.BoxLayout(jPanel28, javax.swing.BoxLayout.LINE_AXIS));
        jPanel28.add(filler47);

        jLabel14.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText(REASON_LABEL.getContent());
        jLabel14.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel14.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel14.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel28.add(jLabel14);
        jPanel28.add(filler23);

        reasonTextField.setEditable(false);
        reasonTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jPanel28.add(reasonTextField);
        jPanel28.add(filler28);

        leftPanel.add(jPanel28);
        leftPanel.add(filler12);

        jPanel29.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel29.setLayout(new javax.swing.BoxLayout(jPanel29, javax.swing.BoxLayout.LINE_AXIS));
        jPanel29.add(filler48);

        jLabel15.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText(OTHER_INFO_LABEL.getContent());
        jLabel15.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel15.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel15.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel29.add(jLabel15);
        jPanel29.add(filler24);

        otherInfoTextField.setEditable(false);
        otherInfoTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jPanel29.add(otherInfoTextField);
        jPanel29.add(filler26);

        leftPanel.add(jPanel29);
        leftPanel.add(filler13);

        jPanel31.setMaximumSize(new java.awt.Dimension(32877, 28));
        jPanel31.setLayout(new javax.swing.BoxLayout(jPanel31, javax.swing.BoxLayout.LINE_AXIS));
        jPanel31.add(filler49);

        jLabel9.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText(REGI_DATE_LABEL.getContent());
        jLabel9.setMaximumSize(new java.awt.Dimension(90, 27));
        jLabel9.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel9.setPreferredSize(new java.awt.Dimension(90, 27));
        jPanel31.add(jLabel9);
        jPanel31.add(filler25);

        creationTextField.setEditable(false);
        creationTextField.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        creationTextField.setEnabled(false);
        creationTextField.setFocusable(false);
        jPanel31.add(creationTextField);
        jPanel31.add(filler27);

        leftPanel.add(jPanel31);
        leftPanel.add(filler72);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 28));
        jPanel1.setPreferredSize(new java.awt.Dimension(166, 27));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(filler74);

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(COUNT_LABEL.getContent());
        jLabel4.setMaximumSize(new java.awt.Dimension(110, 27));
        jLabel4.setMinimumSize(new java.awt.Dimension(90, 27));
        jLabel4.setPreferredSize(new java.awt.Dimension(110, 27));
        jPanel1.add(jLabel4);
        jPanel1.add(filler73);

        countLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLabel.setText("count");
        jPanel1.add(countLabel);

        leftPanel.add(jPanel1);

        wholePanel.add(leftPanel, java.awt.BorderLayout.WEST);
        wholePanel.add(filler54, java.awt.BorderLayout.PAGE_END);

        centerPanel.setPreferredSize(new java.awt.Dimension(850, 480));
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.PAGE_AXIS));

        centerFirstPanel.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        centerFirstPanel.setPreferredSize(new java.awt.Dimension(850, 80));
        centerFirstPanel.setLayout(new javax.swing.BoxLayout(centerFirstPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(33058, 40));
        jPanel5.setPreferredSize(new java.awt.Dimension(767, 40));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));
        jPanel5.add(filler31);

        jLabel12.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel12.setText(FORM_MODE_LABEL.getContent());
        jLabel12.setMaximumSize(new java.awt.Dimension(95, 27));
        jLabel12.setMinimumSize(new java.awt.Dimension(95, 27));
        jLabel12.setPreferredSize(new java.awt.Dimension(95, 27));
        jPanel5.add(jLabel12);
        jPanel5.add(filler50);

        formModeLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        formModeLabel.setText(SEARCH_MODE_LABEL.getContent());
        formModeLabel.setMaximumSize(new java.awt.Dimension(86, 27));
        formModeLabel.setMinimumSize(new java.awt.Dimension(86, 27));
        jPanel5.add(formModeLabel);
        jPanel5.add(filler51);

        centerFirstPanel.add(jPanel5);

        searchPanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        searchPanel.setMinimumSize(new java.awt.Dimension(850, 40));
        searchPanel.setPreferredSize(new java.awt.Dimension(850, 40));
        searchPanel.setLayout(new javax.swing.BoxLayout(searchPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel11.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText(SEARCH_LABEL.getContent());
        jLabel11.setMaximumSize(new java.awt.Dimension(80, 27));
        jLabel11.setMinimumSize(new java.awt.Dimension(80, 27));
        jLabel11.setPreferredSize(new java.awt.Dimension(80, 27));
        searchPanel.add(jLabel11);
        searchPanel.add(filler64);

        searchCarTag.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCarTag.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchCarTag.setText(CAR_TAG_TF.getContent());
        searchCarTag.setToolTipText(CAR_TAG_INPUT_TOOLTIP.getContent());
        searchCarTag.setMaximumSize(new java.awt.Dimension(120, 28));
        searchCarTag.setMinimumSize(new java.awt.Dimension(120, 28));
        searchCarTag.setPreferredSize(new java.awt.Dimension(120, 28));
        searchCarTag.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchCarTagFocusLost(evt);
            }
        });
        searchCarTag.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchCarTagMousePressed(evt);
            }
        });
        searchPanel.add(searchCarTag);
        searchPanel.add(filler66);

        searchDriver.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchDriver.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchDriver.setText(DRIVER_TF.getContent());
        searchDriver.setToolTipText(DRIVER_INPUT_TOOLTIP.getContent());
        searchDriver.setMaximumSize(new java.awt.Dimension(32767, 28));
        searchDriver.setMinimumSize(new java.awt.Dimension(110, 28));
        searchDriver.setPreferredSize(new java.awt.Dimension(110, 28));
        searchDriver.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchDriverFocusLost(evt);
            }
        });
        searchDriver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchDriverMousePressed(evt);
            }
        });
        searchPanel.add(searchDriver);
        searchPanel.add(filler67);

        searchAffiliCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchAffiliCBox.setToolTipText(AFFILIATION_TOOLTIP.getContent());
        searchAffiliCBox.setMaximumSize(new java.awt.Dimension(32767, 30));
        searchAffiliCBox.setMinimumSize(new java.awt.Dimension(125, 30));
        searchAffiliCBox.setPreferredSize(new java.awt.Dimension(125, 30));
        searchPanel.add(searchAffiliCBox);
        searchPanel.add(filler68);

        searchBldgCBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBldgCBox.setToolTipText(BUILDING_TOOLTIP.getContent());
        searchBldgCBox.setMaximumSize(new java.awt.Dimension(150, 30));
        searchBldgCBox.setMinimumSize(new java.awt.Dimension(110, 30));
        searchBldgCBox.setPreferredSize(new java.awt.Dimension(110, 30));
        searchPanel.add(searchBldgCBox);
        searchPanel.add(filler69);

        searchETC.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchETC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchETC.setText(OTHER_INFO_TF.getContent());
        searchETC.setToolTipText(OTHER_TOOLTIP.getContent());
        searchETC.setMaximumSize(new java.awt.Dimension(32767, 28));
        searchETC.setMinimumSize(new java.awt.Dimension(120, 20));
        searchETC.setPreferredSize(new java.awt.Dimension(120, 20));
        searchETC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchETCFocusLost(evt);
            }
        });
        searchETC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchETCMousePressed(evt);
            }
        });
        searchPanel.add(searchETC);
        searchPanel.add(filler71);

        clearButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearButton.setMnemonic('l');
        clearButton.setText(CLEAR_BTN.getContent());
        clearButton.setMaximumSize(new java.awt.Dimension(90, 40));
        clearButton.setMinimumSize(new java.awt.Dimension(100, 40));
        clearButton.setPreferredSize(new java.awt.Dimension(100, 40));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        searchPanel.add(clearButton);
        searchPanel.add(filler70);

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
        searchPanel.add(searchButton);

        centerFirstPanel.add(searchPanel);

        centerPanel.add(centerFirstPanel);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 370));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 410));

        vehiclesTable.setAutoCreateRowSorter(true);
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null}
            },
            new String [] {
                ORDER_HEADER.getContent(),
                CAR_TAG_HEADER.getContent(),
                DRIVER_HEADER.getContent(),
                LOW_HIGH_HEADER.getContent(),
                ROOM_BUILD_HEADER.getContent(),
                OTHER_INFO_HEDER.getContent(),
                "Cell Phone", "Land Line",
                "Notif'", "Exact", "Allowed",
                REASON_HEADER.getContent(),
                "Registered On",
                "Modified On", "drvSeqNo"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    return Integer.class;
                    default:
                    return String.class;
                }
            }
        };
        vehiclesTable.setAutoCreateRowSorter(true);
        vehiclesTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        vehiclesTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        vehiclesTable.setModel(tableModel);
        //hideSomeColumns();
        fineTuneColumnWidth();
        vehiclesTable.setMinimumSize(new java.awt.Dimension(600, 400));
        jScrollPane1.setViewportView(vehiclesTable);

        centerPanel.add(jScrollPane1);
        centerPanel.add(filler65);

        centerThridPanel.setMaximumSize(new java.awt.Dimension(33397, 40));
        centerThridPanel.setLayout(new javax.swing.BoxLayout(centerThridPanel, javax.swing.BoxLayout.LINE_AXIS));
        centerThridPanel.add(filler53);

        saveSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveSheet_Button.setMnemonic('a');
        saveSheet_Button.setText(SAVE_ODS_BTN.getContent());
        saveSheet_Button.setMaximumSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.setMinimumSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.setPreferredSize(new java.awt.Dimension(110, 40));
        saveSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSheet_ButtonActionPerformed(evt);
            }
        });
        centerThridPanel.add(saveSheet_Button);
        centerThridPanel.add(filler55);

        readSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet_Button.setMnemonic('o');
        readSheet_Button.setText(READ_ODS_BTN.getContent());
        readSheet_Button.setMaximumSize(new java.awt.Dimension(110, 40));
        readSheet_Button.setMinimumSize(new java.awt.Dimension(110, 40));
        readSheet_Button.setPreferredSize(new java.awt.Dimension(110, 40));
        readSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheet_ButtonActionPerformed(evt);
            }
        });
        centerThridPanel.add(readSheet_Button);
        centerThridPanel.add(filler56);

        deleteAllVehicles.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteAllVehicles.setMnemonic('e');
        deleteAllVehicles.setText(DELETE_ALL_BTN.getContent());
        deleteAllVehicles.setMaximumSize(new java.awt.Dimension(110, 40));
        deleteAllVehicles.setMinimumSize(new java.awt.Dimension(110, 40));
        deleteAllVehicles.setPreferredSize(new java.awt.Dimension(110, 40));
        deleteAllVehicles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllVehiclesActionPerformed(evt);
            }
        });
        centerThridPanel.add(deleteAllVehicles);
        centerThridPanel.add(filler57);

        deleteCancel_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteCancel_Button.setMnemonic('d');
        deleteCancel_Button.setText(DELETE_BTN.getContent());
        deleteCancel_Button.setEnabled(false);
        deleteCancel_Button.setMaximumSize(new java.awt.Dimension(90, 40));
        deleteCancel_Button.setMinimumSize(new java.awt.Dimension(90, 40));
        deleteCancel_Button.setPreferredSize(new java.awt.Dimension(90, 40));
        deleteCancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCancel_ButtonActionPerformed(evt);
            }
        });
        centerThridPanel.add(deleteCancel_Button);
        centerThridPanel.add(filler58);

        modiSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modiSave_Button.setMnemonic('m');
        modiSave_Button.setText(MODIFY_BTN.getContent());
        modiSave_Button.setEnabled(false);
        modiSave_Button.setMaximumSize(new java.awt.Dimension(90, 40));
        modiSave_Button.setMinimumSize(new java.awt.Dimension(90, 40));
        modiSave_Button.setPreferredSize(new java.awt.Dimension(90, 40));
        modiSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modiSave_ButtonActionPerformed(evt);
            }
        });
        centerThridPanel.add(modiSave_Button);
        centerThridPanel.add(filler59);

        insertSave_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        insertSave_Button.setMnemonic('r');
        insertSave_Button.setText(CREATE_BTN.getContent());
        insertSave_Button.setMaximumSize(new java.awt.Dimension(90, 40));
        insertSave_Button.setMinimumSize(new java.awt.Dimension(90, 40));
        insertSave_Button.setPreferredSize(new java.awt.Dimension(90, 40));
        insertSave_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSave_ButtonActionPerformed(evt);
            }
        });
        centerThridPanel.add(insertSave_Button);
        centerThridPanel.add(filler60);

        centerPanel.add(centerThridPanel);

        wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);
        wholePanel.add(filler1, java.awt.BorderLayout.EAST);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void insertSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSave_ButtonActionPerformed
        
        if (getFormMode() == FormMode.SEARCHING) {
            //<editor-fold defaultstate="collapsed" desc="--change to insertion mode ">
            setFormMode(FormMode.CREATION);
            // clear vehicle detail text fields on the left side panel
            clearVehicleDetail();

            // move focus to the car tag number input field
            carTagTextField.requestFocus();
            //</editor-fold>
        } else if (getFormMode() == FormMode.CREATION) {
            //<editor-fold defaultstate="collapsed" desc="--check and save vehicle info' ">
            // check if all the required fields are supplied
            if (carTagTextField.getText().trim().length() == 0) {
                 JOptionPane.showConfirmDialog(null, CAR_TAG_DIALOG.getContent(),
                                VEHICLE_CHECK_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);  
                carTagTextField.requestFocus();
            } else if (driverTextField.getText().trim().length() == 0) {
                JOptionPane.showConfirmDialog(null, DRIVER_DIALOG.getContent(),
                                VEHICLE_CHECK_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);  
                selectDriverButton.requestFocus();

            } else {
                // insert new vehicle information.
                int result = 0;
                StringBuffer plateNo = new StringBuffer();
                StringBuffer vehicleContents = new StringBuffer();
                
                result = insertNewVehicle(plateNo, vehicleContents);
                if (result == 1) {
                    setFormMode(FormMode.SEARCHING);
                    loadVehicleTable(-1, plateNo.toString()); 
                } else {
                    JOptionPane.showConfirmDialog(null, VEHICLE_CREATION_FAIL_DIALOG.getContent(),
                            CREATION_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, 
                        WARNING_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_insertSave_ButtonActionPerformed

    private void driverTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_driverTextFieldMouseClicked
        if (evt.isConsumed() || formMode == FormMode.SEARCHING)
            return;
        else
            evt.consume();
        openDriverSelectionForm(this);
    }//GEN-LAST:event_driverTextFieldMouseClicked

    /**
     * Depending on the mode of the form, it performs one of the following functions
     * 1. modify the detail info of currently selected vehicle
     * 2. save current modification of the vehicle
     * @param evt 
     */
    private void modiSave_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modiSave_ButtonActionPerformed
        if (getFormMode() == FormMode.SEARCHING) {
            //<editor-fold defaultstate="collapsed" desc="--change to insertion mode ">
            setFormMode(FormMode.MODIFICATION);
            
            // move focus to the car tag number input field
            highlightTableRow(vehiclesTable, vehiclesTable.getSelectedRow());
            selectDriverButton.requestFocus();
            //</editor-fold>
        } else if (getFormMode() == FormMode.MODIFICATION) {
            //<editor-fold defaultstate="collapsed" desc="--check and save updated vehicle">
            // save updated vehicle information
            StringBuffer vehicleModification = new StringBuffer();
            
            if (saveUpdatedVehicle(vehicleModification) == 1)
            {
                setFormMode(FormMode.SEARCHING);
                loadVehicleTable(-1, carTagTextField.getText()); 
                logParkingOperation(OSP_enums.OpLogLevel.SettingsChange, vehicleModification.toString());
            } else {
                JOptionPane.showConfirmDialog(null, VEHICLE_MODIFY_FAIL_DAILOG.getContent(),
                                VEHICLE_MODIFY_FAIL_DIALOGTITLE.getContent(), 
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_modiSave_ButtonActionPerformed

    private void deleteCancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCancel_ButtonActionPerformed
        // depending on the mode of the form, it performs one of the following functions
        // 1. cancel current insertion,  2.  cancel current update
        // 3. delete current row(vehicle)
        if (formMode == FormMode.CREATION) {
            //<editor-fold defaultstate="collapsed" desc="--handle cancelling insertion">
            int response = JOptionPane.showConfirmDialog(null, VEHICLE_CREATE_CANCEL_DIALOG.getContent(),
//                                ((String[])Globals.DialogMSGList.get(VEHICLE_CREATE_CANCEL_DIALOG.ordinal()))[ourLang], 
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                setFormMode(FormMode.SEARCHING);
                insertSave_Button.setText(CREATE_BTN.getContent());
                int selRow = vehiclesTable.getSelectedRow();
                if (selRow >= 0)
                    showVehicleDetail(selRow);  
                else
                    clearVehicleDetail();
            } 
            //</editor-fold>
            
        } else if (formMode == FormMode.MODIFICATION) {
            //<editor-fold defaultstate="collapsed" desc="--handle cancelling update">
            int response = JOptionPane.showConfirmDialog(null, VEHICLE_MODIFY_CANCEL_DAILOG.getContent(),
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                setFormMode(FormMode.SEARCHING);
                modiSave_Button.setText(MODIFY_BTN.getContent());
                int selRow = vehiclesTable.getSelectedRow();
                if (selRow >= 0)
                    showVehicleDetail(selRow);  
                else
                    clearVehicleDetail();
                driverObj = new DriverObj("", 0);           
            } 
            //</editor-fold>
        }
        else if (formMode == FormMode.SEARCHING) {
            int selRow = vehiclesTable.getSelectedRow();
            highlightTableRow(vehiclesTable, selRow);
            // stop processing if no row selected currently
            if (selRow == -1) 
                return;
            else {
                deleteVehicles();
            }
        }
    }//GEN-LAST:event_deleteCancel_ButtonActionPerformed

    private void selectDriverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDriverButtonActionPerformed
        if (formMode == FormMode.SEARCHING)
            return;
        openDriverSelectionForm(this);
    }//GEN-LAST:event_selectDriverButtonActionPerformed

    private void selectDriverButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectDriverButtonKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            openDriverSelectionForm(this);
        }
    }//GEN-LAST:event_selectDriverButtonKeyReleased

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        closeFrameGracefully();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeFrameGracefully();
    }//GEN-LAST:event_formWindowClosing

    private void deleteAllVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllVehiclesActionPerformed

        int result = JOptionPane.showConfirmDialog(this, VEHICLE_DELETE_ALL_DAILOG.getContent(),
//                        ((String[])Globals.DialogMSGList.get(VEHICLE_DELETE_ALL_DAILOG.getContent(),
                        DELETE_ALL_DAILOGTITLE.getContent(), 
                        JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement deleteVehicles = null;
            String excepMsg = "(All Vehicle Deletion)";
            result = 0;

            try {
                conn = getConnection();
                deleteVehicles = conn.prepareStatement("Delete From vehicles");
                result = deleteVehicles.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, deleteVehicles, null, excepMsg);
            }

            if (result >= 1) {
                loadVehicleTable(0, "");
                
                JOptionPane.showConfirmDialog(this, VEHICLE_DELETE_ALL_RESULT_DAILOG.getContent(),
                    DELETE_ALL_RESULT_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            }
        }        
    }//GEN-LAST:event_deleteAllVehiclesActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadVehicleTable(0, "");
    }//GEN-LAST:event_searchButtonActionPerformed

    private void readSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_ButtonActionPerformed

        try{
            int returnVal = odsFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {      
                File file = odsFileChooser.getSelectedFile();
                ArrayList<Point> wrongCells = new ArrayList<Point>();

                Sheet sheet = null;
                try {
                    sheet = SpreadSheet.createFromFile(file).getSheet(0);
                } catch (IOException ex) {
                    Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (sheet != null)
                {
                    ODSReader objODSReader = new ODSReader();

                    WrappedInt driverTotal = new WrappedInt();

                    if (objODSReader.chekcVehiclesODS(sheet, wrongCells, driverTotal))
                    {
                        StringBuilder sb = new StringBuilder();
                        
                        switch (language) {
                            case KOREAN:
                                sb.append("자료가 인식되었습니다. 자료를 불러오시겠습니까?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -자료 갯수: 차량 기록" + driverTotal.getValue() + " 개");
                                break;
                                
                            case ENGLISH:
                                sb.append("Following data has been recognized. Want to load these data?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -Data content: vehicle records " + driverTotal.getValue() + " rows");
                                break;
                                
                            default:
                                break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
                                        READ_ODS_DIALOGTITLE.getContent(), 
                                        JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readVehiclesODS(sheet, this);
                        }
                        
                    } else {
                        // display wrong cell points if existed
                        if (wrongCells.size() > 0) {
                            JOptionPane.showConfirmDialog(null, READ_ODS_FAIL_DIALOG.getContent() +
                                    System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                                    READ_ODS_FAIL_DIALOGTITLE.getContent(), 
                                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);     
                        }
                    }
                }
            }
        }catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User Operation: loading drivers records from an ods file)");
        }
    }//GEN-LAST:event_readSheet_ButtonActionPerformed

    private void saveSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSheet_ButtonActionPerformed
        // Check the size of the list and if empty just return saying "noting to save"
        if (vehiclesTable.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, VEHICLE_SAVE_ODS_FAIL_DIALOG.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_OPTION );
            return;
        }
        saveFileChooser.setFileFilter(new OdsFileOnly());

        int returnVal = saveFileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {                
            File file = saveFileChooser.getSelectedFile();
            //<editor-fold desc="Make sure file extension is 'ods'">
            String pathname = null;
            try {
                pathname = file.getAbsolutePath();
                String extension = saveFileChooser.getFileFilter().getDescription();
                if (extension.indexOf("*.ods") >= 0) {
                    // Create a text file from the attendants list
                    int start = pathname.length() - 4;
                    // Java doesn't have endsWithIgnoreCase. So, ...
                    if (start < 0 || !pathname.substring(start).equalsIgnoreCase(".ods")) {
                        //<editor-fold defaultstate="collapsed" desc="// In case pathname doesn't have ".ods" suffix">
                        // Give it the ".ods" extension, automatically.
                        // pure file name(except extension name) has no ".ods" suffix
                        // So, to make it a ods file, append ".ods" extension to the filename.
                        //</editor-fold>
                        pathname += ".ods";
                        file = new File(pathname);
                    } else {
                        // pathname already has ".ods" as its suffix
                    }
                }
            } catch (Exception ex) {
                logParkingException(Level.SEVERE, ex, "(File: " + pathname + ")");
            }     
            //</editor-fold>
            
            
            final Object[][] data = new Object[vehiclesTable.getModel().getRowCount()][vehiclesTable.getColumnCount()];
            
            for (int row = 0; row < vehiclesTable.getModel().getRowCount(); row ++) {
                int rowM = vehiclesTable.convertRowIndexToModel(row);
                
                for (int col = 0; col < vehiclesTable.getColumnCount(); col++) {
                    data[rowM][col] = vehiclesTable.getValueAt(rowM, col);
                }
            }
            
            String[] columns = new String[vehiclesTable.getColumnCount()];
            for (int col = 0; col < vehiclesTable.getColumnCount(); col++) {
                columns[col] = (String)vehiclesTable.getColumnModel().getColumn(col).getHeaderValue();
            }
            
            TableModel model = new DefaultTableModel(data, columns);
            try {
                SpreadSheet.createEmpty(model).saveAs(file);
                OOUtils.open(file);
            } catch (IOException ex) {
                System.out.println("File save exception: " + ex.getMessage());
            }                
        }
    }//GEN-LAST:event_saveSheet_ButtonActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void searchCarTagMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchCarTagMousePressed
        boolean b1 = searchDriver.isEditable();
        boolean b2 = searchDriver.isEnabled();
        searchCarTag.selectAll();
    }//GEN-LAST:event_searchCarTagMousePressed

    private void searchCarTagFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCarTagFocusLost
        if(searchCarTag.getText().trim().equals(""))
            searchCarTag.setText(CAR_TAG_TF.getContent());
    }//GEN-LAST:event_searchCarTagFocusLost

    private void searchDriverMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchDriverMousePressed
        boolean b1 = searchDriver.isEditable();
        boolean b2 = searchDriver.isEnabled();
        searchDriver.selectAll();
    }//GEN-LAST:event_searchDriverMousePressed

    private void searchDriverFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchDriverFocusLost
        if(searchDriver.getText().trim().equals(""))
            searchDriver.setText(DRIVER_TF.getContent());
    }//GEN-LAST:event_searchDriverFocusLost

    private void searchETCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchETCMousePressed

        searchETC.selectAll();
    }//GEN-LAST:event_searchETCMousePressed

    private void searchETCFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchETCFocusLost

        if(searchETC.getText().trim().equals(""))
            searchETC.setText(OTHER_INFO_TF.getContent());
    }//GEN-LAST:event_searchETCFocusLost

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        searchCarTag.setText(CAR_TAG_TF.getContent());
        searchDriver.setText(DRIVER_TF.getContent());
        searchAffiliCBox.setSelectedIndex(0);
        searchBldgCBox.setSelectedIndex(0);
        searchETC.setText(OTHER_INFO_TF.getContent());
        vehiclesTable.requestFocus();
    }//GEN-LAST:event_clearButtonActionPerformed

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
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VehiclesForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VehiclesForm mainForm = new VehiclesForm();
                mainForm.setVisible(true);
                shortLicenseDialog(mainForm, "Vehicle Manager Program", "upper left");                
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="-- automaticically defined variables">                              
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField carTagTextField;
    private javax.swing.JTextField cellTextField;
    private javax.swing.JPanel centerFirstPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel centerThridPanel;
    private javax.swing.JButton clearButton;
    public javax.swing.JButton closeFormButton;
    private javax.swing.JLabel countLabel;
    private javax.swing.JTextField creationTextField;
    private javax.swing.JButton deleteAllVehicles;
    private javax.swing.JButton deleteCancel_Button;
    private javax.swing.JTextField driverTextField;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler25;
    private javax.swing.Box.Filler filler26;
    private javax.swing.Box.Filler filler27;
    private javax.swing.Box.Filler filler28;
    private javax.swing.Box.Filler filler29;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler30;
    private javax.swing.Box.Filler filler31;
    private javax.swing.Box.Filler filler32;
    private javax.swing.Box.Filler filler33;
    private javax.swing.Box.Filler filler34;
    private javax.swing.Box.Filler filler35;
    private javax.swing.Box.Filler filler36;
    private javax.swing.Box.Filler filler37;
    private javax.swing.Box.Filler filler38;
    private javax.swing.Box.Filler filler39;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler40;
    private javax.swing.Box.Filler filler41;
    private javax.swing.Box.Filler filler42;
    private javax.swing.Box.Filler filler43;
    private javax.swing.Box.Filler filler44;
    private javax.swing.Box.Filler filler45;
    private javax.swing.Box.Filler filler46;
    private javax.swing.Box.Filler filler47;
    private javax.swing.Box.Filler filler48;
    private javax.swing.Box.Filler filler49;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler50;
    private javax.swing.Box.Filler filler51;
    private javax.swing.Box.Filler filler53;
    private javax.swing.Box.Filler filler54;
    private javax.swing.Box.Filler filler55;
    private javax.swing.Box.Filler filler56;
    private javax.swing.Box.Filler filler57;
    private javax.swing.Box.Filler filler58;
    private javax.swing.Box.Filler filler59;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler60;
    private javax.swing.Box.Filler filler61;
    private javax.swing.Box.Filler filler62;
    private javax.swing.Box.Filler filler63;
    private javax.swing.Box.Filler filler64;
    private javax.swing.Box.Filler filler65;
    private javax.swing.Box.Filler filler66;
    private javax.swing.Box.Filler filler67;
    private javax.swing.Box.Filler filler68;
    private javax.swing.Box.Filler filler69;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler70;
    private javax.swing.Box.Filler filler71;
    private javax.swing.Box.Filler filler72;
    private javax.swing.Box.Filler filler73;
    private javax.swing.Box.Filler filler74;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel formModeLabel;
    public javax.swing.JButton insertSave_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastModiTextField;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JButton modiSave_Button;
    private javax.swing.JCheckBox notiCheckBox;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JTextField otherInfoTextField;
    private javax.swing.JCheckBox permitCheckBox;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JButton readSheet_Button;
    private javax.swing.JTextField reasonTextField;
    private javax.swing.JTextField rowNumTextField;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JButton saveSheet_Button;
    private javax.swing.JComboBox searchAffiliCBox;
    private javax.swing.JComboBox searchBldgCBox;
    public javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCarTag;
    private javax.swing.JTextField searchDriver;
    private javax.swing.JTextField searchETC;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JButton selectDriverButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTable vehiclesTable;
    private javax.swing.JCheckBox wholeCheckBox;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    public void loadVehicleTable(int viewIndex, String plateNo) {
        DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();  
        model.setRowCount(0);
        int listNum = 1;
        // <editor-fold defaultstate="collapsed" desc="-- load vehicle list">                          
        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(registered vehicle list loading)";
        
        try {
            // <editor-fold defaultstate="collapsed" desc="-- construct SQL statement">  
            StringBuffer cond = new StringBuffer();
            if(!searchCarTag.getText().trim().equals(CAR_TAG_TF.getContent()))
                attachCondition(cond, "PLATE_NUMBER", searchCarTag.getText().trim());
            if(!searchDriver.getText().trim().equals(DRIVER_TF.getContent()))
                attachCondition(cond, "NAME", searchDriver.getText().trim());

            Object keyObj =((ConvComboBoxItem)searchAffiliCBox.getSelectedItem()).getValue();
            attachIntCondition(cond, "L2_NO", (Integer) keyObj); 

            keyObj =((ConvComboBoxItem)searchBldgCBox.getSelectedItem()).getValue();
            attachIntCondition(cond, "UNIT_SEQ_NO", (Integer)keyObj);

            if(!searchETC.getText().trim().equals(OTHER_INFO_TF.getContent()))
                attachCondition(cond, "OTHER_INFO", searchETC.getText().trim());

            StringBuffer sb = new StringBuffer(); 
            sb.append("SELECT @ROWNUM := @ROWNUM + 1 recNo, TA.* ");
            sb.append("FROM ( ");
            sb.append(" select B.*, concat(BT.BLDG_NO, '-', U.UNIT_NO) building");
            sb.append(" from ");
            sb.append("   (select A.*, concat(L1.PARTY_NAME, '-', L2.PARTY_NAME) affiliation");
            sb.append("   from (select V.PLATE_NUMBER, C.SEQ_NO, C.NAME, C.CELLPHONE, ");
            sb.append("            C.PHONE, C.L2_NO, C.UNIT_SEQ_NO, V.NOTI_REQUESTED, ");
            sb.append("            V.WHOLE_REQUIRED, V.PERMITTED, V.REMARK, ");
            sb.append("            V.OTHER_INFO, V.CREATIONDATE, V.LASTMODIDATE");
            sb.append("        from vehicles V, cardriver C");
            sb.append("        where V.DRIVER_SEQ_NO = C.SEQ_NO");
            sb.append("        ) A");
            sb.append("        Left join L2_affiliation L2 on A.L2_NO = L2.L2_NO");
            sb.append("        Left join L1_affiliation L1 on L2.L1_NO = L1.L1_NO");
            sb.append("   ) B ");
            sb.append("   LEFT JOIN building_unit U ON UNIT_SEQ_NO = U.SEQ_NO");
            sb.append("   LEFT JOIN building_table BT ON BLDG_SEQ_NO = BT.SEQ_NO) TA,");
            sb.append("   (SELECT @rownum := 0) r ");
            sb.append((cond.length() > 0 ? "Where " + cond : ""));
            sb.append(" ORDER BY PLATE_NUMBER");
            //</editor-fold>   
            
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            model.setRowCount(0);
            while (rs.next()) {
                // <editor-fold defaultstate="collapsed" desc="-- make a vehicle row">                          
                model.addRow(new Object[] {
                    rs.getInt("recNo"), 
                    rs.getString("PLATE_NUMBER"), 
                    rs.getString("NAME"), 
                    rs.getString("AFFILIATION"), 
                    rs.getString("BUILDING"), 
                    rs.getString("OTHER_INFO"),
                    rs.getString("CELLPHONE"),
                    rs.getString("PHONE"),
                    rs.getInt("NOTI_REQUESTED"),
                    rs.getInt("WHOLE_REQUIRED"),
                    rs.getInt("PERMITTED"),
                    rs.getString("REMARK"),
                    rs.getString("CREATIONDATE"),
                    rs.getString("LASTMODIDATE"),
                    rs.getString("SEQ_NO")
                });
                if (viewIndex == -1 && plateNo.equals(rs.getString("PLATE_NUMBER"))) {
                    viewIndex = model.getRowCount() - 1;
                }
                //</editor-fold>
            }
            countLabel.setText(""+vehiclesTable.getRowCount()+"");
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, selectStmt, rs, excepMsg);
        }
        
        int numRows = model.getRowCount();
        if (numRows > 0) {
            if (viewIndex >= numRows)
                viewIndex = numRows - 1;
            showVehicleDetail(viewIndex);
            highlightTableRow(vehiclesTable, viewIndex); 
            vehiclesTable.requestFocus();
            deleteCancel_Button.setEnabled(true);
            modiSave_Button.setEnabled((true));
        } else {
            // clear left side panel vehicle details
            clearVehicleDetail();

            deleteCancel_Button.setEnabled(false);    
            modiSave_Button.setEnabled(false);            
        }
    }

    private void hideSomeColumns() {
        TableColumnModel NumberTableModel = vehiclesTable.getColumnModel();
        
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.SeqNo.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Modification.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Creation.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Permitted.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Whole.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Notification.getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.Phone .getNumVal()));
        NumberTableModel.removeColumn(
                NumberTableModel.getColumn(VehicleCol.CellPhone.getNumVal()));
    }

    private void showVehicleDetail(int viewIndex) {
        TableModel vModel = vehiclesTable.getModel();

        Integer rowNo = (Integer)vModel.getValueAt(viewIndex, VehicleCol.RowNo.getNumVal());
        rowNumTextField.setText(rowNo.toString());
        carTagTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.PlateNumber.getNumVal()));
        driverTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Name.getNumVal()));
        cellTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.CellPhone.getNumVal()));
        phoneTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Phone.getNumVal()));
        
        int noti = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Notification.getNumVal());
        notiCheckBox.setSelected(noti == 1 ? true : false);
        
        int whole = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Whole.getNumVal());
        wholeCheckBox.setSelected(whole == 1 ? true : false);
        
        int perm = (Integer)vModel.getValueAt(viewIndex, VehicleCol.Permitted.getNumVal());
        permitCheckBox.setSelected(perm == 1 ? false : true);
       
        reasonTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Causes.getNumVal()));
        otherInfoTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.OtherInfo.getNumVal()));
        
        creationTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Creation.getNumVal()));
        lastModiTextField.setText((String)
                vModel.getValueAt(viewIndex, VehicleCol.Modification.getNumVal()));  
        
        deleteCancel_Button.setEnabled(true);
    }

    private void attachEventListenerToVehicleTable() {
        vehiclesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
        {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (vehiclesTable.getSelectedRow() >= 0) {
                            if (getFormMode() == FormMode.SEARCHING) {
                                showVehicleDetail(vehiclesTable.convertRowIndexToModel(
                                        vehiclesTable.getSelectedRow()));  
                            } else {
                                String dialogMessage = "";
                                
                                switch (language) {
                                    case KOREAN:
                                        dialogMessage = "차량정보 " 
                                                + (formMode == FormMode.CREATION ? "생성" : "변경") 
                                                + " 중입니다";
                                        break;
                                        
                                    case ENGLISH:
                                        dialogMessage = "Car information is being" 
                                                + (formMode == FormMode.CREATION ? "created." : "modified.");
                                        break;
                                        
                                    default:
                                        break;
                                }
                                
                                JOptionPane.showConfirmDialog(null, dialogMessage,
                                        WORK_MODE_DIALOGTITLE.getContent(),
                                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);                                
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * @return the formMode
     */
    public FormMode getFormMode() {
        return formMode;
    }

    /**
     * @param formMode the formMode to set
     */
    public void setFormMode(FormMode aFormMode) {
        
        // change mode value
        this.formMode = aFormMode;
            
        // switch label for the users, change button label/functionality
        switch (aFormMode) {
            case CREATION:
                searchButton.setEnabled(false);
                clearButton.setEnabled(false);
                closeFormButton.setEnabled(false);
                
                formModeLabel.setText(CREATE_MODE_LABEL.getContent());
                insertSave_Button.setText(SAVE_BTN.getContent());
                insertSave_Button.setMnemonic('s');
                deleteCancel_Button.setEnabled(true);    
                deleteCancel_Button.setText(CANCEL_BTN.getContent());
                deleteCancel_Button.setMnemonic('c');
                
                saveSheet_Button.setEnabled(false);
                vehiclesTable.setEnabled(false);
                
                makeVehicleInfoFieldsEditable(true);
                
                searchCarTag.setEnabled(false);
                searchDriver.setEnabled(false);
                searchAffiliCBox.setEnabled(false);
                searchBldgCBox.setEnabled(false);
                searchETC.setEnabled(false);
                break;
            case MODIFICATION:
                searchButton.setEnabled(false);
                clearButton.setEnabled(false);
                closeFormButton.setEnabled(false);
                
                formModeLabel.setText(MODIFY_MODE_LABEL.getContent());
                modiSave_Button.setText(SAVE_BTN.getContent());
                modiSave_Button.setMnemonic('s');
                deleteCancel_Button.setEnabled(true);    
                deleteCancel_Button.setText(CANCEL_BTN.getContent());
                deleteCancel_Button.setMnemonic('c');
                
                saveSheet_Button.setEnabled(false);
                vehiclesTable.setEnabled(false);
                
                makeVehicleInfoFieldsEditable(true);
                
                searchCarTag.setEnabled(false);
                searchDriver.setEnabled(false);
                searchAffiliCBox.setEnabled(false);
                searchBldgCBox.setEnabled(false);
                searchETC.setEnabled(false);
                break;
            case SEARCHING:
                searchButton.setEnabled(true);
                searchButton.setMnemonic('s');
                closeFormButton.setEnabled(true);
                closeFormButton.setMnemonic('c');
                formModeLabel.setText(SEARCH_MODE_LABEL.getContent());
                
                insertSave_Button.setText(CREATE_BTN.getContent());
                insertSave_Button.setMnemonic('r');
                modiSave_Button.setText(MODIFY_BTN.getContent());
                modiSave_Button.setMnemonic('m');
                deleteCancel_Button.setText(DELETE_BTN.getContent());
                deleteCancel_Button.setMnemonic('d');
                clearButton.setEnabled(true);
                
                saveSheet_Button.setEnabled(true);
                vehiclesTable.setEnabled(true);
                
                makeVehicleInfoFieldsEditable(false);
                
                searchCarTag.setEnabled(true);
                searchDriver.setEnabled(true);
                searchAffiliCBox.setEnabled(true);
                searchBldgCBox.setEnabled(true);
                searchETC.setEnabled(true);
                break;
            default:
                formModeLabel.setText("");
                break;
        }
    }

    /**
     * Changes editable properties of various components of a vehicle management form.
     * @param b tells if the text and other fields are editable or not
     */
    private void makeVehicleInfoFieldsEditable(boolean b) {
        
        // As car tag number is the primary key of the vehicle table
        // it shouldn't be modified. It needs to be editable for insertion though.
        // and be put back to non-editable after the new vehicle inserted.
        if (getFormMode() != FormMode.MODIFICATION)
            carTagTextField.setEditable(b);
        notiCheckBox.setEnabled(b);
        wholeCheckBox.setEnabled(b);
        permitCheckBox.setEnabled(b);
       
        reasonTextField.setEditable(b);
        otherInfoTextField.setEditable(b);
        
        // enable or disable related buttons
        selectDriverButton.setEnabled(b);      
        deleteAllVehicles.setEnabled(!b);
        readSheet_Button.setEnabled(!b);
        
        if (getFormMode() == FormMode.CREATION)
            modiSave_Button.setEnabled(false);
        else if (getFormMode() == FormMode.MODIFICATION)
            insertSave_Button.setEnabled(false);
        else {
            modiSave_Button.setEnabled(true);
            insertSave_Button.setEnabled(true);
        }
    }
    
    public void setDriverInfo(String name, String cell, String phone, int seqNo) {
        driverTextField.setText(name);
        cellTextField.setText(cell);
        phoneTextField.setText(phone);
        driverObj = new DriverObj(name, seqNo);
    }

    private int insertNewVehicle(StringBuffer plateNo, StringBuffer vehicleProperties) {
        Connection conn = null;
        PreparedStatement createDriver = null;
        String excepMsg = "in creation of a car with tag number: " + carTagTextField.getText().trim();

        int result = 0;
        try {
            StringBuffer sb = new StringBuffer("Insert Into Vehicles (");
            sb.append(" PLATE_NUMBER, DRIVER_SEQ_NO, NOTI_REQUESTED,");
            sb.append(" WHOLE_REQUIRED, PERMITTED, Remark, OTHER_INFO, ");
            sb.append(" CREATIONDATE) Values (?, ?, ?, ?, ?, ?, ?, current_timestamp)");

            conn = getConnection();
            createDriver = conn.prepareStatement(sb.toString());
            plateNo.append(carTagTextField.getText().trim());
            int loc = 1;
            
            createDriver.setString(loc++, plateNo.toString());
            createDriver.setInt(loc++, driverObj.getSeqNo());
            createDriver.setInt(loc++, notiCheckBox.isSelected() ? 1 : 0);
            createDriver.setInt(loc++, wholeCheckBox.isSelected() ? 1 : 0);
            createDriver.setInt(loc++, permitCheckBox.isSelected() ? 0 : 1);
            createDriver.setString(loc++, reasonTextField.getText().trim());
            createDriver.setString(loc++, otherInfoTextField.getText().trim());

            vehicleProperties.append("Vehicle Creation Summary: " + System.lineSeparator());
            getVehicleProperties(vehicleProperties);            
            
            result = createDriver.executeUpdate();
        } catch (SQLException e) {
            logParkingException(Level.SEVERE, e, excepMsg);
        } finally {
            closeDBstuff(conn, createDriver, null, excepMsg);
        }
        
        return result;         
    }

    private void closeFrameGracefully() {
        if (formMode == FormMode.SEARCHING) {
            dispose();
        } else {
            
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = (formMode == FormMode.CREATION ? "생성" : "변경")
                            + " 중인 차량정보를 포기하겠습니까?";
                    break;
                    
                case ENGLISH:
                    dialogMessage = "Do you want to give up " +
                                (formMode == FormMode.CREATION ? "registering " : "modifying ")
                                        + "a car?";
                    break;
                    
                default:
                    break;
            }
            
            int response = JOptionPane.showConfirmDialog(null, dialogMessage,
                                WARING_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                dispose();
            }             
        }      
    }

    private void clearVehicleDetail() {
        
        rowNumTextField.setText("");
        carTagTextField.setText("");
        driverTextField.setText("");
        cellTextField.setText("");
        phoneTextField.setText("");

        notiCheckBox.setSelected(false);
        wholeCheckBox.setSelected(false);
        permitCheckBox.setSelected(true);

        reasonTextField.setText("");
        otherInfoTextField.setText("");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        creationTextField.setText(dateFormat.format(date));
        lastModiTextField.setText("");
    }

    private void deleteVehicles() {
        // delete a driver's record currently selected 
        int[] deleteIndice = vehiclesTable.getSelectedRows();
        if (deleteIndice.length == 0)
        {
            return;
        }
        
        int result = -1;
        String tagNumber = (String)vehiclesTable.getValueAt(deleteIndice[0], 1);
        int modal_Index = vehiclesTable.convertRowIndexToModel(deleteIndice[0]);
        
        if (deleteIndice.length == 1) {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 등록차량을 삭제합니까?" 
                            + System.getProperty("line.separator") + "차량번호: " + tagNumber;
                    break;
                    
                case ENGLISH:
                    dialogMessage = "Unregister following vehicle?" 
                        + System.getProperty("line.separator") + "Tag Number: " + tagNumber;
                    break;
                    
                default:
                    break;
            }            
            
            result = JOptionPane.showConfirmDialog(this, dialogMessage,
                        DELETE_DIALOGTITLE.getContent(),
                        JOptionPane.YES_NO_OPTION);
        } else {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 차량 및 총 " + deleteIndice.length + " 대 차량의 정보를 삭제합니까?" 
                            + System.getProperty("line.separator") + "차량번호: " + tagNumber;
                    break;
                    
                case ENGLISH:
                    dialogMessage ="Unregistered total " + deleteIndice.length + " vehicles including following?" 
                        + System.getProperty("line.separator") + "Tag number: " + tagNumber;
                    break;
                    
                default:
                    break;
            }

            result = JOptionPane.showConfirmDialog(this, dialogMessage,
                    DELETE_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION);
        }    
        
        if (result == JOptionPane.YES_OPTION) {
            // <editor-fold defaultstate="collapsed" desc="-- delete registered vehicles ">   
            Connection conn = null;
            PreparedStatement createBuilding = null;
            String excepMsg = null;
            int totalDeletion = 0;
            
            result = -1;
            try {
                String sql = "Delete From vehicles Where PLATE_NUMBER = ?";
                conn = getConnection();
                createBuilding = conn.prepareStatement(sql);
                for (int indexNo : deleteIndice) {
                    modal_Index = vehiclesTable.convertRowIndexToModel(indexNo);
                    tagNumber = (String)vehiclesTable.getModel().getValueAt(modal_Index, 1);
                    excepMsg = "(deleting vehivle: " + tagNumber + ")";
                    createBuilding.setString(1, tagNumber);
                    result = createBuilding.executeUpdate();
                    totalDeletion += result;
                }
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, excepMsg);
            } finally {
                closeDBstuff(conn, createBuilding, null, excepMsg);

                if (result == 1) {
                    loadVehicleTable(deleteIndice[0], ""); // pass row index of deleted vehicle
                    
                    String dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "차량 '" + tagNumber + "'의 기록이" + System.getProperty("line.separator")
                                    + "삭제되었습니다";
                            break;
                            
                        case ENGLISH:
                            dialogMessage ="Record of car '" + tagNumber + "'" + System.getProperty("line.separator")
                                    + "has been removed";
                            break;
                            
                        default:
                            break;
                    }                    
                    
                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            }
            //</editor-fold>
        }
    }

    private int saveUpdatedVehicle(StringBuffer vehicleProperties) {
        int result = 0;
        
        Connection conn = null;
        PreparedStatement updateDriver = null;
        String excepMsg = "updating vehicle plate no: " + carTagTextField.getText();

        try {
            StringBuffer sb = new StringBuffer("Update Vehicles Set ");
            sb.append(" DRIVER_SEQ_NO = ?, NOTI_REQUESTED = ?,");
            sb.append(" WHOLE_REQUIRED = ?, PERMITTED = ?, Remark = ?,");
            sb.append(" OTHER_INFO = ?, LASTMODIDATE = current_timestamp");
            sb.append(" Where PLATE_NUMBER = ?");

            conn = getConnection();
            updateDriver = conn.prepareStatement(sb.toString());
            int seqNo = driverObj.getSeqNo();
            if (seqNo == 0) {
                seqNo = Integer.parseInt((String)vehiclesTable.getModel().getValueAt(
                    vehiclesTable.getSelectedRow(), VehicleCol.SeqNo.getNumVal()));        
            }
            updateDriver.setInt(1, seqNo);
            updateDriver.setInt(2, notiCheckBox.isSelected() ? 1 : 0);
            updateDriver.setInt(3, wholeCheckBox.isSelected() ? 1 : 0);
            updateDriver.setInt(4, permitCheckBox.isSelected() ? 0 : 1);
            updateDriver.setString(5, reasonTextField.getText().trim());
            updateDriver.setString(6, otherInfoTextField.getText().trim());
            updateDriver.setString(7, carTagTextField.getText().trim());
            
            vehicleProperties.append("Vehicle Update Summary: " + System.lineSeparator());
            getVehicleProperties(vehicleProperties);

            result = updateDriver.executeUpdate();
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, updateDriver, null, excepMsg);
        }

        return result;         
    }

    private void loadSearchBox() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String excepMsg = "(CBox Item Loading for : 2 affiliation columns combined";

        searchAffiliCBox.removeAllItems();
        searchAffiliCBox.addItem(new ConvComboBoxItem(new Integer(-1), 
                LOWER_HIGHER_CB_ITEM.getContent()));
        try {
            //<editor-fold defaultstate="collapsed" desc="-- load affiliation comboBox">                            
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();

            sb.append("SELECT concat(L1.PARTY_NAME, '-', L2.PARTY_NAME) ");
            sb.append("  as PARTY_NAME, L2_NO ");
            sb.append("FROM L2_affiliation L2, L1_affiliation L1 ");
            sb.append("Where L2.L1_NO = L1.L1_NO ");
            sb.append("Order by L1.PARTY_NAME, L2.party_name");

            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                ConvComboBoxItem item = new ConvComboBoxItem(rs.getInt("L2_NO"), rs.getString("PARTY_NAME"));
                searchAffiliCBox.addItem(item);
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }
        
        searchBldgCBox.removeAllItems();
        searchBldgCBox.addItem(new ConvComboBoxItem(new Integer(-1), 
                ROOM_BUILDING_CB_ITEM.getContent()));
        excepMsg = "(CBox Item Loading for : building-unit combined";
        try {
            //<editor-fold defaultstate="collapsed" desc="-- load affiliation comboBox">                            
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT concat(bd.BLDG_NO, '-', ut.UNIT_NO) ");
            sb.append(" as BLDG_UNIT, ut.SEQ_NO ");
            sb.append("FROM building_UNIT ut, building_table bd ");
            sb.append("Where ut.BLDG_SEQ_NO = bd.SEQ_NO ");
            sb.append("Order By bd.BLDG_NO, ut.UNIT_NO");
            
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                ConvComboBoxItem item = new ConvComboBoxItem(rs.getInt("SEQ_NO"), rs.getString("BLDG_UNIT"));
                searchBldgCBox.addItem(item);
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }        
    }
    
    private void attachEnterHandler(JComponent compo) {
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().getClass() == PComboBox.class) {
                    PComboBox cBox = (PComboBox)e.getSource();
                    if (cBox.isPopupVisible()) {
                        ConvComboBoxItem item = (ConvComboBoxItem)cBox.getHighlightedCbxItem();
                        cBox.setSelectedItem(item);
                        cBox.setPopupVisible(false);
                    } else
                        loadVehicleTable(0, "");
                    
                } else
                    loadVehicleTable(0, "");
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);
    }    

    private void getVehicleProperties(StringBuffer vehicleProperties) {
        vehicleProperties.append("  Tag number: " + carTagTextField.getText().trim() + System.lineSeparator());
        vehicleProperties.append("  Driver name: " + driverObj.getName() + System.lineSeparator());
        
        vehicleProperties.append("  Household notification: " + (notiCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());
        vehicleProperties.append("  Full tag comparision: " + (wholeCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());
        vehicleProperties.append("  Parking permitted: " + (permitCheckBox.isSelected() ? "Yes" : "No")
                + System.lineSeparator());       
        
        vehicleProperties.append("  Detailed reason: " + reasonTextField.getText() + System.lineSeparator());
        vehicleProperties.append("  Other info': " + otherInfoTextField.getText() + System.lineSeparator());
    }
    
    private static void highlightTableRow(JTable table, int rowIndex) {
        table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        if(rowIndex + 15 > table.getRowCount())
            table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 2, true))); 
        else if( 0 > table.getSelectedRow() -15)
            table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 2, true))); 
        else     
            table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex-15, 2, true))); 
        
        
    }  
}
