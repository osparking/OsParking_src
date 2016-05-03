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
package com.osparking.attendant;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.validator.routines.EmailValidator;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import static com.osparking.global.Globals.*;
import com.osparking.global.names.PasswordValidator;

import static javax.swing.JOptionPane.showMessageDialog;
import static com.osparking.global.names.DB_Access.*;
import com.osparking.global.Globals;
import com.osparking.global.names.ControlEnums.ATTLIST_ComboBoxTypes;
import static com.osparking.global.names.ControlEnums.ButtonContent.CHECK_BTN;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.*;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_EMAIL_DUP_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_EMAIL_SYNTAX_CHECK_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_HELP_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_ID_DUP_CHCEK_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_SAVE_AS_FAIL_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_SFAVE_AS_SUCCESS_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ATT_USER_UPDATE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.CONFIRM_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.CREATION_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.CREATTION_FAIL_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_FAIL_DAILOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DELETE_RESULT_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.*;
import static com.osparking.global.names.ControlEnums.TableTypes.CELL_PHONE_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.EMAIL_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.MANAGER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.MODIFIED_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.NAME_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.PHONE_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.USER_ID_HEADER;
import static com.osparking.global.names.ControlEnums.TitleTypes.*;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CELL_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.ID_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.NAME_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.PHONE_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.PW_INPUT_TOOTLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.REPEAT_PW_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.SAVE_AS_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.SEARCH_INPUT_TOOLTIP;
import com.osparking.global.names.DB_Access;
import com.osparking.global.names.JDBCMySQL;
import static com.osparking.global.names.JDBCMySQL.getHashedPW;
import com.osparking.global.names.JTextFieldLimit;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.global.names.ParentGUI;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Open Source Parking Inc.
 */
//test git song
public class AttListForm extends javax.swing.JFrame {
    FormMode formMode = FormMode.NormalMode;
    int selectedRowIndex = 0;  
    String loginID = null;
    String loginPW = null;
    boolean isManager = false;
    boolean ID_usable = false;
    String usableID = null;
    boolean Email_usable = true;
    String usableEmail = null;
    String searchCondition = "";
    private static Logger logException = null;
    private static Logger logOperation = null;
    private PasswordValidator pwValidator; 
    ParentGUI mainGUI;
    
    /**
     * Creates new form AttListForm
     */
    public AttListForm(ParentGUI mainGUI, String loginID, String loginPW, boolean isManager) {
        // Mark the first row as selected in default
        this.mainGUI = mainGUI;
        try {
            initComponents();
            setIconImages(OSPiconList);

            
            this.loginID = loginID;
            this.loginPW = loginPW;
            this.isManager = isManager;
            initComponentsUser();
            
            // limit maximun allowed length of user ID
            userIDText.setDocument(new JTextFieldLimit(20));

            RefreshTableContents();
            // usersTable.changeSelection(selectedRowIndex, 0, false, false);
            ShowAttendantDetail(selectedRowIndex); 

            usersTable.getRowSorter().addRowSorterListener(new RowSorterListener() {
                @Override  
                public void sorterChanged (final RowSorterEvent e) {  
                    SwingUtilities.invokeLater(new Runnable()  
                    {  
                        public void run()  
                        {  
                            if (e.getType () == RowSorterEvent.Type.SORTED)  
                            {  
                                if (usersTable.getSelectedRow () != -1) {  
                                    usersTable.scrollRectToVisible 
                                        (usersTable.getCellRect 
                                            (usersTable.getSelectedRow(), 0, false));
                                }  
                            }  
                        }  
                    }); 
                }
            });  
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(AttListForm Constructor ID: " + loginID + ")");
        }
        
        JComponent pane = (JComponent) this.getContentPane();
        pane.getInputMap().put(null, MUTEX_DEBUG_SEQ_VALUE);
        
        addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                searchText.requestFocus();
            }
        });           
        attachEnterHandler(searchText);
        adminAuth2CheckBox.setSelected(isManager);
    }
    
    private void attachEnterHandler(JComponent compo) {
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                searchButtonActionPerformed(null);
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);
    }        
    
    private void initComponentsUser()
    {
        userID2Label.setText(USER_ID_LABEL.getContent() + loginID);
        adminAuth2CheckBox.setSelected(isManager);
        saveFileName.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        legendLLabel.setText("\u203B" + REQUIRED_LABEL.getContent() + "\u2212");
        legendMLabel.setText("\u25CF");
        legendRLabel.setText(REQUIRED1_LABEL.getContent());
        legendMLabel2.setText("\uu25B2");
        legendRLabel2.setText(REQUIRED2_LABEL.getContent());
        isIDreqLabel.setText("\u25CF");
        nameReqLabel.setText("\u25CF");
        cellReqLabel.setText("\u25B2");
        phoneReqLabel.setText("\u25B2");
        userPWReqLabel.setText("\u25CF");
        SetTableColumnWidth();
        setModificationState(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveFileName = new javax.swing.JFileChooser();
        wholePanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        topInPanel = new javax.swing.JPanel();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        seeLicenseButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        adminAuth2CheckBox = new javax.swing.JCheckBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        userID2Label = new javax.swing.JLabel();
        filler46 = new javax.swing.Box.Filler(new java.awt.Dimension(60, 0), new java.awt.Dimension(60, 0), new java.awt.Dimension(60, 32767));
        westPanel = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 40), new java.awt.Dimension(0, 40), new java.awt.Dimension(32767, 40));
        RequiredPanel1 = new javax.swing.JPanel();
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        legendLLabel = new javax.swing.JLabel();
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        legendMLabel = new javax.swing.JLabel();
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(3, 32767));
        legendRLabel = new javax.swing.JLabel();
        legendMLabel2 = new javax.swing.JLabel();
        filler78 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(3, 32767));
        legendRLabel2 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        loginPanel = new javax.swing.JPanel();
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userIDLabel = new javax.swing.JLabel();
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        isIDreqLabel = new javax.swing.JLabel();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userIDText = new javax.swing.JTextField();
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 40), new java.awt.Dimension(40, 40), new java.awt.Dimension(40, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        idCheckPanel = new javax.swing.JPanel();
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        checkIDButton = new javax.swing.JButton();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        adminPanel = new javax.swing.JPanel();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        adminAuthLabel = new javax.swing.JLabel();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(60, 32767));
        managerAuthCheckBox = new javax.swing.JCheckBox();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        namePanel = new javax.swing.JPanel();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        nameLabel = new javax.swing.JLabel();
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        nameReqLabel = new javax.swing.JLabel();
        filler45 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userNameText = new javax.swing.JTextField();
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        cellPhonePanel = new javax.swing.JPanel();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler47 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        cellReqLabel = new javax.swing.JLabel();
        filler48 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        cellPhoneText = new javax.swing.JTextField();
        filler49 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        phonePanel = new javax.swing.JPanel();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler62 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        phoneReqLabel = new javax.swing.JLabel();
        filler63 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        phoneText = new javax.swing.JTextField();
        filler61 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        emailPanel = new javax.swing.JPanel();
        filler51 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler67 = new javax.swing.Box.Filler(new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 32767));
        emailAddrText = new javax.swing.JTextField();
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        emailCheckPanel = new javax.swing.JPanel();
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        checkEmailButton = new javax.swing.JButton();
        filler69 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        changePWD_Panel = new javax.swing.JPanel();
        filler52 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        changePWLabel = new javax.swing.JLabel();
        filler80 = new javax.swing.Box.Filler(new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 32767));
        changePWCheckBox = new javax.swing.JCheckBox();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        PWD_Panel = new javax.swing.JPanel();
        filler53 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        newPW1Label = new javax.swing.JLabel();
        filler74 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        newPW1ReqLabel = new javax.swing.JLabel();
        filler75 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        new1Password = new javax.swing.JPasswordField();
        filler84 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(10, 10), new java.awt.Dimension(20, 32767));
        PWHelpButton = new javax.swing.JButton();
        filler77 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(10, 10), new java.awt.Dimension(20, 32767));
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        repeatPWD_Panel = new javax.swing.JPanel();
        filler58 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        newPW2Label = new javax.swing.JLabel();
        filler59 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        newPW2ReqLabel = new javax.swing.JLabel();
        filler60 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        new2Password = new javax.swing.JPasswordField();
        filler57 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        currentPWD_Panel = new javax.swing.JPanel();
        filler64 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userPWLabel = new javax.swing.JLabel();
        filler54 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userPWReqLabel = new javax.swing.JLabel();
        filler55 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userPassword = new javax.swing.JPasswordField();
        filler56 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        datePanel = new javax.swing.JPanel();
        filler65 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        userPWLabel1 = new javax.swing.JLabel();
        filler68 = new javax.swing.Box.Filler(new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 0), new java.awt.Dimension(55, 32767));
        creationDateText = new javax.swing.JTextField();
        filler50 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 40), new java.awt.Dimension(20, 32767));
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        centerPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        southPanel = new javax.swing.JPanel();
        spacePanel1 = new javax.swing.JPanel();
        btnPanel = new javax.swing.JPanel();
        filler70 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        createButton = new javax.swing.JButton();
        filler71 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        deleteButton = new javax.swing.JButton();
        filler72 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        multiFuncButton = new javax.swing.JButton();
        filler73 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        cancelButton = new javax.swing.JButton();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(90, 0), new java.awt.Dimension(90, 0), new java.awt.Dimension(32767, 32767));
        searchCriteriaComboBox = new javax.swing.JComboBox();
        filler85 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        searchText = new javax.swing.JTextField();
        filler76 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        searchButton = new javax.swing.JButton();
        filler81 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        saveTextFileButton = new javax.swing.JButton();
        filler82 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 10), new java.awt.Dimension(10, 32767));
        closeFormButton = new javax.swing.JButton();
        filler83 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(20, 32767));
        spacePanel2 = new javax.swing.JPanel();

        saveFileName.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileName.setApproveButtonText(SAVE_BTN.getContent());
        saveFileName.setFileFilter(new TextFileOnly());
        saveFileName.setToolTipText("");
        saveFileName.setEnabled(false);
        saveFileName.setName(""); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(ATTLIST_FRAME_TITLE.getContent()
        );
        setMinimumSize(new java.awt.Dimension(1100, 710));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wholePanel.setMinimumSize(new java.awt.Dimension(400, 400));
        wholePanel.setPreferredSize(new java.awt.Dimension(400, 400));
        wholePanel.setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.PAGE_AXIS));
        topPanel.add(filler23);

        topInPanel.setLayout(new javax.swing.BoxLayout(topInPanel, javax.swing.BoxLayout.LINE_AXIS));
        topInPanel.add(filler27);

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.setFocusable(false);
        seeLicenseButton.setRequestFocusEnabled(false);
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        topInPanel.add(seeLicenseButton);
        topInPanel.add(filler1);
        topInPanel.add(filler2);

        adminAuth2CheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        adminAuth2CheckBox.setText(ADMIN_LABEL.getContent());
        adminAuth2CheckBox.setToolTipText("");
        adminAuth2CheckBox.setEnabled(false);
        adminAuth2CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        adminAuth2CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        topInPanel.add(adminAuth2CheckBox);
        topInPanel.add(filler4);

        userID2Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userID2Label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        userID2Label.setText(LOGIN_ID_LABEL.getContent() + ": " +loginID);
        topInPanel.add(userID2Label);
        topInPanel.add(filler46);

        topPanel.add(topInPanel);

        wholePanel.add(topPanel, java.awt.BorderLayout.NORTH);

        westPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        westPanel.setMinimumSize(new java.awt.Dimension(400, 355));
        westPanel.setPreferredSize(new java.awt.Dimension(400, 537));
        westPanel.setLayout(new javax.swing.BoxLayout(westPanel, javax.swing.BoxLayout.PAGE_AXIS));
        westPanel.add(filler5);

        RequiredPanel1.setMaximumSize(new java.awt.Dimension(32877, 28));
        RequiredPanel1.setMinimumSize(new java.awt.Dimension(300, 26));
        RequiredPanel1.setPreferredSize(new java.awt.Dimension(300, 26));
        RequiredPanel1.setLayout(new javax.swing.BoxLayout(RequiredPanel1, javax.swing.BoxLayout.LINE_AXIS));
        RequiredPanel1.add(filler30);

        legendLLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendLLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        legendLLabel.setText("Required");
        legendLLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        legendLLabel.setMaximumSize(new java.awt.Dimension(140, 21));
        legendLLabel.setMinimumSize(new java.awt.Dimension(140, 21));
        legendLLabel.setName(""); // NOI18N
        legendLLabel.setPreferredSize(new java.awt.Dimension(140, 21));
        RequiredPanel1.add(legendLLabel);
        RequiredPanel1.add(filler41);

        legendMLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendMLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        legendMLabel.setText("M");
        legendMLabel.setToolTipText("");
        legendMLabel.setPreferredSize(new java.awt.Dimension(14, 14));
        RequiredPanel1.add(legendMLabel);
        RequiredPanel1.add(filler42);

        legendRLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendRLabel.setText(": Required");
        legendRLabel.setToolTipText("");
        legendRLabel.setMaximumSize(new java.awt.Dimension(80, 21));
        legendRLabel.setMinimumSize(new java.awt.Dimension(80, 21));
        legendRLabel.setName(""); // NOI18N
        legendRLabel.setPreferredSize(new java.awt.Dimension(80, 21));
        RequiredPanel1.add(legendRLabel);

        legendMLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendMLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        legendMLabel2.setText("w");
        legendMLabel2.setToolTipText("");
        legendMLabel2.setPreferredSize(new java.awt.Dimension(14, 14));
        RequiredPanel1.add(legendMLabel2);
        RequiredPanel1.add(filler78);

        legendRLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        legendRLabel2.setText(": choose1");
        legendRLabel2.setToolTipText("");
        legendRLabel2.setMaximumSize(new java.awt.Dimension(80, 21));
        legendRLabel2.setMinimumSize(new java.awt.Dimension(80, 21));
        legendRLabel2.setPreferredSize(new java.awt.Dimension(80, 21));
        RequiredPanel1.add(legendRLabel2);

        westPanel.add(RequiredPanel1);
        westPanel.add(filler7);

        loginPanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        loginPanel.setMinimumSize(new java.awt.Dimension(300, 26));
        loginPanel.setPreferredSize(new java.awt.Dimension(300, 26));
        loginPanel.setLayout(new javax.swing.BoxLayout(loginPanel, javax.swing.BoxLayout.LINE_AXIS));
        loginPanel.add(filler31);

        userIDLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userIDLabel.setText(LOGIN_ID_LABEL.getContent());
        userIDLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        userIDLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        userIDLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        loginPanel.add(userIDLabel);
        loginPanel.add(filler40);

        isIDreqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        isIDreqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        isIDreqLabel.setText("M");
        isIDreqLabel.setToolTipText("");
        isIDreqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        isIDreqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        isIDreqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        loginPanel.add(isIDreqLabel);
        loginPanel.add(filler43);

        userIDText.setEditable(false);
        userIDText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userIDText.setToolTipText(ID_INPUT_TOOLTIP.getContent());
        userIDText.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        userIDText.setEnabled(false);
        userIDText.setMaximumSize(new java.awt.Dimension(32767, 30));
        userIDText.setMinimumSize(new java.awt.Dimension(80, 21));
        userIDText.setPreferredSize(new java.awt.Dimension(80, 21));
        userIDText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newIDtyped(evt);
            }
        });
        loginPanel.add(userIDText);
        loginPanel.add(filler33);

        westPanel.add(loginPanel);
        westPanel.add(filler6);

        idCheckPanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        idCheckPanel.setMinimumSize(new java.awt.Dimension(300, 26));
        idCheckPanel.setPreferredSize(new java.awt.Dimension(300, 26));
        idCheckPanel.setLayout(new javax.swing.BoxLayout(idCheckPanel, javax.swing.BoxLayout.LINE_AXIS));
        idCheckPanel.add(filler28);

        checkIDButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        checkIDButton.setText(CHECK_BTN.getContent());
        checkIDButton.setEnabled(false);
        checkIDButton.setMaximumSize(new java.awt.Dimension(90, 23));
        checkIDButton.setMinimumSize(new java.awt.Dimension(90, 23));
        checkIDButton.setPreferredSize(new java.awt.Dimension(90, 23));
        checkIDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkIDButtonActionPerformed(evt);
            }
        });
        idCheckPanel.add(checkIDButton);
        idCheckPanel.add(filler34);

        westPanel.add(idCheckPanel);
        westPanel.add(filler8);

        adminPanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        adminPanel.setMinimumSize(new java.awt.Dimension(300, 26));
        adminPanel.setPreferredSize(new java.awt.Dimension(300, 26));
        adminPanel.setLayout(new javax.swing.BoxLayout(adminPanel, javax.swing.BoxLayout.LINE_AXIS));
        adminPanel.add(filler32);

        adminAuthLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        adminAuthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        adminAuthLabel.setText(ADMIN_LABEL.getContent());
        adminAuthLabel.setToolTipText("");
        adminAuthLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        adminAuthLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        adminAuthLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        adminPanel.add(adminAuthLabel);
        adminPanel.add(filler37);

        managerAuthCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        managerAuthCheckBox.setEnabled(false);
        managerAuthCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        adminPanel.add(managerAuthCheckBox);

        westPanel.add(adminPanel);
        westPanel.add(filler9);

        namePanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        namePanel.setMinimumSize(new java.awt.Dimension(300, 26));
        namePanel.setPreferredSize(new java.awt.Dimension(300, 26));
        namePanel.setLayout(new javax.swing.BoxLayout(namePanel, javax.swing.BoxLayout.LINE_AXIS));
        namePanel.add(filler35);

        nameLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel.setText(NAME_LABEL.getContent());
        nameLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        nameLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        nameLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        namePanel.add(nameLabel);
        namePanel.add(filler44);

        nameReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        nameReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameReqLabel.setText("M");
        nameReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        nameReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        nameReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        namePanel.add(nameReqLabel);
        namePanel.add(filler45);

        userNameText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userNameText.setToolTipText(NAME_INPUT_TOOLTIP.getContent());
        userNameText.setEnabled(false);
        userNameText.setMaximumSize(new java.awt.Dimension(32767, 30));
        userNameText.setMinimumSize(new java.awt.Dimension(80, 21));
        userNameText.setName(""); // NOI18N
        userNameText.setPreferredSize(new java.awt.Dimension(80, 21));
        namePanel.add(userNameText);
        namePanel.add(filler38);

        westPanel.add(namePanel);
        westPanel.add(filler10);

        cellPhonePanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        cellPhonePanel.setMinimumSize(new java.awt.Dimension(300, 26));
        cellPhonePanel.setPreferredSize(new java.awt.Dimension(300, 26));
        cellPhonePanel.setLayout(new javax.swing.BoxLayout(cellPhonePanel, javax.swing.BoxLayout.LINE_AXIS));
        cellPhonePanel.add(filler36);

        jLabel5.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(CELL_PHONE_LABEL.getContent()   );
        jLabel5.setMaximumSize(new java.awt.Dimension(130, 21));
        jLabel5.setMinimumSize(new java.awt.Dimension(130, 21));
        jLabel5.setPreferredSize(new java.awt.Dimension(130, 21));
        cellPhonePanel.add(jLabel5);
        cellPhonePanel.add(filler47);

        cellReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cellReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cellReqLabel.setText("w");
        cellReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        cellReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        cellReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        cellPhonePanel.add(cellReqLabel);
        cellPhonePanel.add(filler48);

        cellPhoneText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cellPhoneText.setToolTipText(CELL_INPUT_TOOLTIP.getContent());
        cellPhoneText.setEnabled(false);
        cellPhoneText.setMaximumSize(new java.awt.Dimension(32767, 30));
        cellPhoneText.setMinimumSize(new java.awt.Dimension(80, 21));
        cellPhoneText.setPreferredSize(new java.awt.Dimension(80, 21));
        cellPhonePanel.add(cellPhoneText);
        cellPhonePanel.add(filler49);

        westPanel.add(cellPhonePanel);
        westPanel.add(filler11);

        phonePanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        phonePanel.setMinimumSize(new java.awt.Dimension(300, 26));
        phonePanel.setPreferredSize(new java.awt.Dimension(300, 26));
        phonePanel.setLayout(new javax.swing.BoxLayout(phonePanel, javax.swing.BoxLayout.LINE_AXIS));
        phonePanel.add(filler39);

        jLabel6.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(PHONE_LABEL.getContent());
        jLabel6.setMaximumSize(new java.awt.Dimension(130, 21));
        jLabel6.setMinimumSize(new java.awt.Dimension(130, 21));
        jLabel6.setPreferredSize(new java.awt.Dimension(130, 21));
        phonePanel.add(jLabel6);
        phonePanel.add(filler62);

        phoneReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        phoneReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        phoneReqLabel.setText("w");
        phoneReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        phoneReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        phoneReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        phonePanel.add(phoneReqLabel);
        phonePanel.add(filler63);

        phoneText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        phoneText.setToolTipText(PHONE_INPUT_TOOLTIP.getContent());
        phoneText.setEnabled(false);
        phoneText.setMaximumSize(new java.awt.Dimension(32767, 30));
        phonePanel.add(phoneText);
        phonePanel.add(filler61);

        westPanel.add(phonePanel);
        westPanel.add(filler12);

        emailPanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        emailPanel.setMinimumSize(new java.awt.Dimension(300, 26));
        emailPanel.setPreferredSize(new java.awt.Dimension(300, 26));
        emailPanel.setLayout(new javax.swing.BoxLayout(emailPanel, javax.swing.BoxLayout.LINE_AXIS));
        emailPanel.add(filler51);

        jLabel4.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(EMAIL_LABEL.getContent());
        jLabel4.setMaximumSize(new java.awt.Dimension(130, 21));
        jLabel4.setMinimumSize(new java.awt.Dimension(130, 21));
        jLabel4.setPreferredSize(new java.awt.Dimension(130, 21));
        jLabel4.setRequestFocusEnabled(false);
        emailPanel.add(jLabel4);
        emailPanel.add(filler67);

        emailAddrText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        emailAddrText.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        emailAddrText.setEnabled(false);
        emailAddrText.setMaximumSize(new java.awt.Dimension(32767, 30));
        emailAddrText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                EmailTypedHandler(evt);
            }
        });
        emailPanel.add(emailAddrText);
        emailPanel.add(filler66);

        westPanel.add(emailPanel);
        westPanel.add(filler13);

        emailCheckPanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        emailCheckPanel.setMinimumSize(new java.awt.Dimension(300, 26));
        emailCheckPanel.setPreferredSize(new java.awt.Dimension(300, 26));
        emailCheckPanel.setLayout(new javax.swing.BoxLayout(emailCheckPanel, javax.swing.BoxLayout.LINE_AXIS));
        emailCheckPanel.add(filler29);

        checkEmailButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        checkEmailButton.setText(CHECK_BTN.getContent());
        checkEmailButton.setEnabled(false);
        checkEmailButton.setMaximumSize(new java.awt.Dimension(90, 23));
        checkEmailButton.setMinimumSize(new java.awt.Dimension(90, 23));
        checkEmailButton.setPreferredSize(new java.awt.Dimension(90, 23));
        checkEmailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkEmailButtonActionPerformed(evt);
            }
        });
        emailCheckPanel.add(checkEmailButton);
        emailCheckPanel.add(filler69);

        westPanel.add(emailCheckPanel);
        westPanel.add(filler14);

        changePWD_Panel.setMaximumSize(new java.awt.Dimension(32877, 28));
        changePWD_Panel.setMinimumSize(new java.awt.Dimension(300, 26));
        changePWD_Panel.setPreferredSize(new java.awt.Dimension(300, 26));
        changePWD_Panel.setLayout(new javax.swing.BoxLayout(changePWD_Panel, javax.swing.BoxLayout.LINE_AXIS));
        changePWD_Panel.add(filler52);

        changePWLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        changePWLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        changePWLabel.setText(CHANGE_PW_LABEL.getContent());
        changePWLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        changePWLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        changePWLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        changePWLabel.setRequestFocusEnabled(false);
        changePWD_Panel.add(changePWLabel);
        changePWD_Panel.add(filler80);

        changePWCheckBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        changePWCheckBox.setEnabled(false);
        changePWCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePWCheckBoxActionPerformed(evt);
            }
        });
        changePWD_Panel.add(changePWCheckBox);

        westPanel.add(changePWD_Panel);
        westPanel.add(filler15);

        PWD_Panel.setMaximumSize(new java.awt.Dimension(32877, 28));
        PWD_Panel.setMinimumSize(new java.awt.Dimension(300, 26));
        PWD_Panel.setPreferredSize(new java.awt.Dimension(300, 26));
        PWD_Panel.setLayout(new javax.swing.BoxLayout(PWD_Panel, javax.swing.BoxLayout.LINE_AXIS));
        PWD_Panel.add(filler53);

        newPW1Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        newPW1Label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        newPW1Label.setText(NEW_PW_LABLE.getContent());
        newPW1Label.setMaximumSize(new java.awt.Dimension(130, 21));
        newPW1Label.setMinimumSize(new java.awt.Dimension(130, 21));
        newPW1Label.setPreferredSize(new java.awt.Dimension(130, 21));
        PWD_Panel.add(newPW1Label);
        PWD_Panel.add(filler74);

        newPW1ReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        newPW1ReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newPW1ReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        newPW1ReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        newPW1ReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        PWD_Panel.add(newPW1ReqLabel);
        PWD_Panel.add(filler75);

        new1Password.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        new1Password.setToolTipText(showPasswordRequirement());
        new1Password.setEnabled(false);
        new1Password.setMaximumSize(new java.awt.Dimension(32767, 30));
        new1Password.setNextFocusableComponent(new2Password);
        PWD_Panel.add(new1Password);
        PWD_Panel.add(filler84);

        PWHelpButton.setBackground(new java.awt.Color(153, 255, 153));
        PWHelpButton.setFont(new java.awt.Font("Dotum", 1, 14)); // NOI18N
        PWHelpButton.setIcon(getQuest20_Icon());
        PWHelpButton.setEnabled(false);
        PWHelpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        PWHelpButton.setMaximumSize(new java.awt.Dimension(20, 20));
        PWHelpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        PWHelpButton.setOpaque(false);
        PWHelpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        PWHelpButton.setRequestFocusEnabled(false);
        PWHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWHelpButtonActionPerformed(evt);
            }
        });
        PWD_Panel.add(PWHelpButton);
        PWD_Panel.add(filler77);

        westPanel.add(PWD_Panel);
        westPanel.add(filler16);

        repeatPWD_Panel.setMaximumSize(new java.awt.Dimension(32877, 28));
        repeatPWD_Panel.setMinimumSize(new java.awt.Dimension(300, 26));
        repeatPWD_Panel.setPreferredSize(new java.awt.Dimension(300, 26));
        repeatPWD_Panel.setLayout(new javax.swing.BoxLayout(repeatPWD_Panel, javax.swing.BoxLayout.LINE_AXIS));
        repeatPWD_Panel.add(filler58);

        newPW2Label.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        newPW2Label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        newPW2Label.setText(REPEAT_PW_LABEL.getContent());
        newPW2Label.setMaximumSize(new java.awt.Dimension(130, 21));
        newPW2Label.setMinimumSize(new java.awt.Dimension(130, 21));
        newPW2Label.setPreferredSize(new java.awt.Dimension(130, 21));
        repeatPWD_Panel.add(newPW2Label);
        repeatPWD_Panel.add(filler59);

        newPW2ReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        newPW2ReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newPW2ReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        newPW2ReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        newPW2ReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        repeatPWD_Panel.add(newPW2ReqLabel);
        repeatPWD_Panel.add(filler60);

        new2Password.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        new2Password.setToolTipText(REPEAT_PW_INPUT_TOOLTIP.getContent());
        new2Password.setEnabled(false);
        new2Password.setMaximumSize(new java.awt.Dimension(32767, 30));
        repeatPWD_Panel.add(new2Password);
        repeatPWD_Panel.add(filler57);

        westPanel.add(repeatPWD_Panel);
        westPanel.add(filler17);

        currentPWD_Panel.setMaximumSize(new java.awt.Dimension(32877, 28));
        currentPWD_Panel.setMinimumSize(new java.awt.Dimension(300, 26));
        currentPWD_Panel.setPreferredSize(new java.awt.Dimension(300, 26));
        currentPWD_Panel.setLayout(new javax.swing.BoxLayout(currentPWD_Panel, javax.swing.BoxLayout.LINE_AXIS));
        currentPWD_Panel.add(filler64);

        userPWLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userPWLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userPWLabel.setText(MY_PW_LABEL.getContent());
        userPWLabel.setMaximumSize(new java.awt.Dimension(130, 21));
        userPWLabel.setMinimumSize(new java.awt.Dimension(130, 21));
        userPWLabel.setPreferredSize(new java.awt.Dimension(130, 21));
        currentPWD_Panel.add(userPWLabel);
        currentPWD_Panel.add(filler54);

        userPWReqLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userPWReqLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userPWReqLabel.setText("M");
        userPWReqLabel.setMaximumSize(new java.awt.Dimension(15, 21));
        userPWReqLabel.setMinimumSize(new java.awt.Dimension(15, 21));
        userPWReqLabel.setPreferredSize(new java.awt.Dimension(15, 21));
        currentPWD_Panel.add(userPWReqLabel);
        currentPWD_Panel.add(filler55);

        userPassword.setEditable(false);
        userPassword.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userPassword.setToolTipText("");
        userPassword.setMaximumSize(new java.awt.Dimension(32767, 30));
        currentPWD_Panel.add(userPassword);
        currentPWD_Panel.add(filler56);

        westPanel.add(currentPWD_Panel);
        westPanel.add(filler19);

        datePanel.setMaximumSize(new java.awt.Dimension(32877, 28));
        datePanel.setMinimumSize(new java.awt.Dimension(300, 26));
        datePanel.setPreferredSize(new java.awt.Dimension(300, 26));
        datePanel.setLayout(new javax.swing.BoxLayout(datePanel, javax.swing.BoxLayout.LINE_AXIS));
        datePanel.add(filler65);

        userPWLabel1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        userPWLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userPWLabel1.setText(CREATION_LABEL.getContent());
        userPWLabel1.setMaximumSize(new java.awt.Dimension(130, 21));
        userPWLabel1.setMinimumSize(new java.awt.Dimension(130, 21));
        userPWLabel1.setPreferredSize(new java.awt.Dimension(130, 21));
        datePanel.add(userPWLabel1);
        datePanel.add(filler68);

        creationDateText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        creationDateText.setToolTipText("");
        creationDateText.setEnabled(false);
        creationDateText.setMaximumSize(new java.awt.Dimension(32767, 30));
        datePanel.add(creationDateText);
        datePanel.add(filler50);

        westPanel.add(datePanel);
        westPanel.add(filler18);

        wholePanel.add(westPanel, java.awt.BorderLayout.WEST);

        centerPanel.setMinimumSize(new java.awt.Dimension(600, 474));
        centerPanel.setPreferredSize(new java.awt.Dimension(600, 474));
        centerPanel.setLayout(new java.awt.BorderLayout());

        jLabel11.setFont(new java.awt.Font(font_Type, font_Style, 16));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText(ATTLIST_FRAME_TITLE.getContent());
        centerPanel.add(jLabel11, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(600, 474));
        jScrollPane1.setName(""); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 474));

        usersTable.setAutoCreateRowSorter(true);
        usersTable.setFont(new java.awt.Font(font_Type, 0, font_Size));
        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
            },
            new String[]{
                USER_ID_HEADER.getContent(),
                NAME_HEADER.getContent(),
                MANAGER_HEADER.getContent(),
                CELL_PHONE_HEADER.getContent(),
                PHONE_HEADER.getContent(),
                EMAIL_HEADER.getContent(),
                MODIFIED_HEADER.getContent()
            }
        ));
        usersTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        usersTable.setFillsViewportHeight(true);
        usersTable.setFocusCycleRoot(true);
        usersTable.setFocusTraversalPolicy(null);
        usersTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
        usersTable.setMinimumSize(new java.awt.Dimension(600, 474));
        usersTable.setName(""); // NOI18N
        usersTable.setNextFocusableComponent(userNameText);
        usersTable.setPreferredSize(new java.awt.Dimension(600, 5000));
        usersTable.setRowHeight(22);
        usersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersTableMouseClicked(evt);
            }
        });
        usersTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                usersTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(usersTable);

        centerPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        wholePanel.add(centerPanel, java.awt.BorderLayout.CENTER);
        wholePanel.add(filler3, java.awt.BorderLayout.LINE_END);

        southPanel.setMinimumSize(new java.awt.Dimension(920, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(761, 80));
        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout spacePanel1Layout = new javax.swing.GroupLayout(spacePanel1);
        spacePanel1.setLayout(spacePanel1Layout);
        spacePanel1Layout.setHorizontalGroup(
            spacePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        spacePanel1Layout.setVerticalGroup(
            spacePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        southPanel.add(spacePanel1);

        btnPanel.setMaximumSize(new java.awt.Dimension(33747, 40));
        btnPanel.setMinimumSize(new java.awt.Dimension(950, 40));
        btnPanel.setLayout(new javax.swing.BoxLayout(btnPanel, javax.swing.BoxLayout.LINE_AXIS));
        btnPanel.add(filler70);

        createButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        createButton.setMnemonic('r');
        createButton.setText(CREATE_BTN.getContent());
        createButton.setEnabled(false);
        createButton.setMaximumSize(new java.awt.Dimension(80, 60));
        createButton.setMinimumSize(new java.awt.Dimension(80, 40));
        createButton.setPreferredSize(new java.awt.Dimension(90, 40));
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        btnPanel.add(createButton);
        btnPanel.add(filler71);

        deleteButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteButton.setMnemonic('d');
        deleteButton.setText(DELETE_BTN.getContent());
        deleteButton.setEnabled(false);
        deleteButton.setMaximumSize(new java.awt.Dimension(80, 60));
        deleteButton.setMinimumSize(new java.awt.Dimension(80, 60));
        deleteButton.setPreferredSize(new java.awt.Dimension(90, 40));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        btnPanel.add(deleteButton);
        btnPanel.add(filler72);

        multiFuncButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        multiFuncButton.setMnemonic('m');
        multiFuncButton.setText(MODIFY_BTN.getContent());
        multiFuncButton.setEnabled(false);
        multiFuncButton.setMaximumSize(new java.awt.Dimension(80, 60));
        multiFuncButton.setMinimumSize(new java.awt.Dimension(80, 60));
        multiFuncButton.setPreferredSize(new java.awt.Dimension(90, 40));
        multiFuncButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiFuncButtonActionPerformed(evt);
            }
        });
        btnPanel.add(multiFuncButton);
        btnPanel.add(filler73);

        cancelButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        cancelButton.setMnemonic('c');
        cancelButton.setText(CANCEL_BTN.getContent());
        cancelButton.setEnabled(false);
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 60));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 60));
        cancelButton.setPreferredSize(new java.awt.Dimension(90, 40));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        btnPanel.add(cancelButton);
        btnPanel.add(filler22);

        searchCriteriaComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCriteriaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{
                NAME_LABEL.getContent(),
                LOGIN_ID_LABEL.getContent()
            }
        ));
        searchCriteriaComboBox.setMaximumSize(new java.awt.Dimension(100, 30));
        searchCriteriaComboBox.setMinimumSize(new java.awt.Dimension(100, 30));
        searchCriteriaComboBox.setPreferredSize(new java.awt.Dimension(100, 30));
        btnPanel.add(searchCriteriaComboBox);
        btnPanel.add(filler85);

        searchText.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchText.setToolTipText(SEARCH_INPUT_TOOLTIP.getContent());
        searchText.setMaximumSize(new java.awt.Dimension(200, 30));
        searchText.setMinimumSize(new java.awt.Dimension(80, 30));
        searchText.setPreferredSize(new java.awt.Dimension(120, 30));
        btnPanel.add(searchText);
        btnPanel.add(filler76);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setMaximumSize(new java.awt.Dimension(80, 60));
        searchButton.setMinimumSize(new java.awt.Dimension(80, 60));
        searchButton.setPreferredSize(new java.awt.Dimension(90, 40));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        btnPanel.add(searchButton);
        btnPanel.add(filler81);

        saveTextFileButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        saveTextFileButton.setText(SAVE_AS_BTN.getContent());
        saveTextFileButton.setToolTipText(SAVE_AS_TOOLTIP.getContent());
        saveTextFileButton.setAutoscrolls(true);
        saveTextFileButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveTextFileButton.setMaximumSize(new java.awt.Dimension(80, 40));
        saveTextFileButton.setMinimumSize(new java.awt.Dimension(80, 40));
        saveTextFileButton.setPreferredSize(new java.awt.Dimension(100, 40));
        saveTextFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTextFileButtonActionPerformed(evt);
            }
        });
        btnPanel.add(saveTextFileButton);
        btnPanel.add(filler82);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText(CLOSE_BTN.getContent());
        closeFormButton.setMaximumSize(new java.awt.Dimension(80, 60));
        closeFormButton.setMinimumSize(new java.awt.Dimension(80, 60));
        closeFormButton.setPreferredSize(new java.awt.Dimension(90, 40));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        btnPanel.add(closeFormButton);
        btnPanel.add(filler83);

        southPanel.add(btnPanel);

        javax.swing.GroupLayout spacePanel2Layout = new javax.swing.GroupLayout(spacePanel2);
        spacePanel2.setLayout(spacePanel2Layout);
        spacePanel2Layout.setHorizontalGroup(
            spacePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        spacePanel2Layout.setVerticalGroup(
            spacePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        southPanel.add(spacePanel2);

        wholePanel.add(southPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(1054, 696));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void usersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersTableMouseClicked

        try {
            // When a table is sorted on some key, a row of the table can have two index values.
            // They are one for the display ordering and the other is  for the location in the model.
            // So, in the source code, these two index values need to be translated back and forth.
            if (formMode != FormMode.UpdateMode) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        selectedRowIndex = usersTable.getSelectedRow();
                        if (selectedRowIndex != -1) {
                            ShowAttendantDetail(usersTable.convertRowIndexToModel(selectedRowIndex));
                        }
                    }
                });
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User action: user list table mouse click, index: " + selectedRowIndex + ")");
        }
    }//GEN-LAST:event_usersTableMouseClicked

    private void ChangeNewPasswordEnabled(boolean flag)
    {
        new1Password.setEnabled(flag);
        PWHelpButton.setEnabled(flag);
        new2Password.setEnabled(flag);  
        if (!flag)
        {
            new1Password.setText("");
            new2Password.setText("");
        }
    }
    
    private void multiFuncButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiFuncButtonActionPerformed
        String[] errorMsg = new String[1];
        
        try {
            switch (formMode) {
                case NormalMode:
                    // <editor-fold defaultstate="collapsed" desc="-- Prepare to change user information">
                    formMode = FormMode.UpdateMode;
                    multiFuncButton.setText(SAVE_BTN.getContent());
                    multiFuncButton.setMnemonic('s');
                    setModificationState(true); // change to modification mode
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);  

                    break;
                    // </editor-fold>                
                case UpdateMode:
                    // <editor-fold defaultstate="collapsed" desc="-- save modified user information ">
                    if (allFieldsAreGood(errorMsg)) {
                        // each field satisfies data requirements
                        formMode = FormMode.NormalMode;
                        multiFuncButton.setText(MODIFY_BTN.getContent());
                        multiFuncButton.setMnemonic('m');
                        setModificationState(false);
                        int result = saveUpdatedRecord();
                        doAfterUpdateOperations(result);
                        usersTableMouseClicked(null);
                    } else {
                        if (errorMsg[0].length() > 0) {
                            showMessageDialog(null, errorMsg[0]);
                        }
                    }
                    break;
                    // </editor-fold>
                case CreateMode:
                    // <editor-fold defaultstate="collapsed" desc="-- Complete user creation operation">
                    if (!ID_usable) {
                        JOptionPane.showConfirmDialog(this, 
                                ID_CHECK_DIALOG.getContent(),
                                CREATTION_FAIL_DIALOGTITLE.getContent(),
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE); 
                        return;
                    }
                    if (!Email_usable) {
                        JOptionPane.showConfirmDialog(this, 
                                EMAIL_CHECK_DIALOG.getContent(),
                                CREATTION_FAIL_DIALOGTITLE.getContent(),
                                JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE); 
                        return;
                    }
                    if (allFieldsAreGood(errorMsg)) {
                        String newUserID = userIDText.getText().trim();
                        int result = saveCreatedRecord();
                        String dialogText = "";
                        
                        if (result == 1) {
                            revokeCreationMode(true);
                            switch (language) {
                                case KOREAN:
                                    dialogText ="사용자(ID: " + newUserID + ") 정보가\n성공적으로 생성되었습니다.";
                                    break;
                                    
                                case ENGLISH:
                                    dialogText = "Successful Creation of a user" + System.lineSeparator() 
                                            + "User ID: " + newUserID + "";
                                    break;
                                default:
                                    break;
                            }
                            
                            JOptionPane.showMessageDialog(this, 
                                    dialogText,
                                    CREATION_RESULT_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE);  
                        } else {
                            switch (language) {
                                case KOREAN:
                                    dialogText = "정보 생성에 실패하였습니다!\n  ID: " +   newUserID;
                                    break;
                                    
                                case ENGLISH:
                                    dialogText = "User Creation Failure!" + System.lineSeparator() 
                                            + " Failed ID: " + newUserID + "";
                                    break;
                                default:
                                    break;
                            }                            
                            
                            JOptionPane.showMessageDialog(this, 
                                    dialogText,
                                    CREATION_RESULT_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE);            
                        }
                    } else {
                        if (errorMsg[0].length() > 0) {
                            showMessageDialog(null, errorMsg[0]);
                        }
                    }   
                    break;
                    // </editor-fold>
                default:
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                        "(User requested command: " + evt.getActionCommand() + ")");             
        }
    }//GEN-LAST:event_multiFuncButtonActionPerformed

    private int saveUpdatedRecord() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("Update users_osp SET name = ?");
        if (changePWCheckBox.isSelected()) {
            sBuilder.append(", password = md5(?)");
        }
        sBuilder.append(", email = ?, isManager = ?, cellphone = ?, phone = ? WHERE id = ?");
        String sql = sBuilder.toString();
        Connection conn = null;
        PreparedStatement updateAttendant = null;
        ResultSet rs = null;
        int result = -1;
        try {
            int pIndex = 1;
            conn = JDBCMySQL.getConnection();            
            updateAttendant = conn.prepareStatement(sql);

            // <editor-fold defaultstate="collapsed" desc="-- Supply actual values to the prepared statement">
            updateAttendant.setString(pIndex++, userNameText.getText().trim());
            if (changePWCheckBox.isSelected()) {
                updateAttendant.setString(pIndex++, new String(new1Password.getPassword()));
            }    
            String email = emailAddrText.getText().trim();
            if (email.length() == 0) {
                updateAttendant.setString(pIndex++, null);
            } else {
                updateAttendant.setString(pIndex++, email);
            }
            if (managerAuthCheckBox.isSelected()) {
                updateAttendant.setInt(pIndex++, 1);
            } else {
                updateAttendant.setInt(pIndex++, 0);
            }
            updateAttendant.setString(pIndex++, cellPhoneText.getText().trim());
            updateAttendant.setString(pIndex++, phoneText.getText().trim());
            updateAttendant.setString(pIndex++, userIDText.getText());
            // </editor-fold>
            
            result = updateAttendant.executeUpdate();
        } catch (SQLException se) {
            logParkingException(Level.SEVERE, se, 
                    "(ID: " + userIDText.getText() + 
                    ", name: " + userNameText.getText().trim() + 
                    ", email: " + emailAddrText.getText().trim() + 
                    ", cell: " + cellPhoneText.getText().trim() + 
                    ", phone: " + phoneText.getText().trim() + ")");  
        } finally {
            closeDBstuff(conn, updateAttendant, rs, "update attendant " + userIDText.getText() + " " 
                    + userNameText.getText().trim());
            return result;
        }
    }
    
    private boolean allFieldsAreGood(String[] errorMsg) {

        //    ID is unchangable. So, check other fields if their format is good to use. Other fields include
        //     name, cell phone number, phone number, new password, etc.
        // <editor-fold defaultstate="collapsed" desc="-- Syntax check of each field">      
        
        StringBuilder wrongFields = new StringBuilder();
        // name should be longer than 2 characters
        if (userNameText.getText().trim().length() <= 1) {
            wrongFields.append(ATT_NAME_CHECK_DIALOG.getContent() + System.lineSeparator());
            userNameText.requestFocus();
        }

        // given cellphone number exists, it should be longer than 10
        String cellPhone = cellPhoneText.getText().trim();
        int cellDigCount = getNumericDigitCount(cellPhone);
        if (cellPhone.length() > 0 && cellDigCount != 11) {
            if (wrongFields.toString().length() == 0)
                cellPhoneText.requestFocus();
            wrongFields.append(ATT_CELL_CHECK_DIALOG.getContent()+ System.lineSeparator());
        }      
        
        // given phone number exists, it should be longer than 4
        // some phone number is just the extension number of a company
        String phoneNumber = phoneText.getText().trim();
        int phDigCount = getNumericDigitCount(phoneNumber);
        if (phoneNumber.length() > 0 && phDigCount < 4) {
            if (wrongFields.toString().length() == 0)
                phoneText.requestFocus();            
            wrongFields.append(PHONE_CHECK_DIALOG.getContent()+ System.lineSeparator());
        }        
        
        // one of cell or phone should be supplied
        if ((cellDigCount == 0) && (phDigCount == 0)) {
            if (wrongFields.toString().length() == 0)
                cellPhoneText.requestFocus();                
            wrongFields.append(CELL_PHONE_CHECK_DIALOG.getContent()+ System.lineSeparator());
        }
        
        // when password is to be updated
        // supply new password and identical confirmation of it
        if (changePWCheckBox.isSelected()) {
            pwValidator = new PasswordValidator();
            if (pwValidator.isInValidForm(new String(new1Password.getPassword()))) {
                String pass1 = new String(new1Password.getPassword());
                String pass2 = new String(new2Password.getPassword());
                if (!pass1.equals(pass2)) {
                    if (wrongFields.toString().length() == 0)
                        new1Password.requestFocus();                            
                    wrongFields.append(REPEAT_PW_CHECK_ERROR.getContent() + System.lineSeparator());
                }
            } else {
                if (wrongFields.toString().length() == 0)
                    new1Password.requestFocus();         
                wrongFields.append(PASSWORD_CHECK_DIALOG.getContent() + System.lineSeparator());
            }
        }
        // </editor-fold>

        boolean result = false;
        
        // Check if current password matched DB stored one
        if (passwordMatched(loginID, new String(userPassword.getPassword()))) {
            if (wrongFields.length() == 0) {
                result = true;
            } else {
                result = false;
            }
        } else {
            if (wrongFields.toString().length() == 0)
                userPassword.requestFocus();                    
            wrongFields.append(ADMIN_PW_CHECK_DIALOG.getContent() + System.lineSeparator());
            result = false;
        }        
        errorMsg[0] = wrongFields.toString();
        return result;
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        try {
            if (formMode == FormMode.CreateMode) {
                // reverse steps done before the creation
                revokeCreationMode(false);
            } else if (formMode == FormMode.UpdateMode ) {
                setModificationState(false);
                multiFuncButton.setMnemonic('s');
                formMode = FormMode.NormalMode;
                multiFuncButton.setText(MODIFY_BTN.getContent());
                multiFuncButton.setMnemonic('m');
                createButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
            usersTableMouseClicked(null);
        } catch (Exception ex) {
            String mode = (formMode == FormMode.CreateMode ? "Create" : "Modify");
            logParkingException(Level.SEVERE, ex, "(User action: user cancelled mode " + mode);
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        if (mainGUI != null)
            mainGUI.clearAttendantManageForm();
        dispose();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void saveTextFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTextFileButtonActionPerformed
        try {
            saveFileName.setSelectedFile(new File("User List"));
            int returnVal = saveFileName.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = saveFileName.getSelectedFile();
                String pathname = null;
                try {
                    // What to do with the file, e.g. display it in a TextArea
                    pathname = file.getAbsolutePath();
                    String extension = saveFileName.getFileFilter().getDescription();
                    if (extension.indexOf("*.txt") >= 0) {
                        // Create a text file from the attendants list
                        int start = pathname.length() - 4;
                        // Java doesn't have endsWithIgnoreCase. So, ...
                        if (start < 0 || !pathname.substring(start).equalsIgnoreCase(".txt")) {
                            //<editor-fold defaultstate="collapsed" desc="// In case pathname doesn't have ".txt" suffix">
                            // Give it the ".txt" extension, automatically.
                            // pure file name(except extension name) has no ".txt" suffix
                            // So, to make it a text file, append ".txt" extension to the filename.
                            //</editor-fold>
                            pathname += ".txt";
                        } else {
                            // pathname already has ".txt" as its suffix
                        }
                    }
                    // <editor-fold defaultstate="collapsed" desc="-- Close resources(rs, stmt)">
                    File f = new File(pathname);
                    if(f.exists()) {
                        if (f.isDirectory()) {
                            String dialogText = "";
                            
                            switch (language) {
                                case KOREAN:
                                    dialogText = "같은 이름의 폴더(=디렉토리)가 존재합니다.\n " + 
                                            pathname + "\n파일의 이름을 다른 것으로 변경하십시오.";
                                    break;

                                case ENGLISH:
                                    dialogText = "A folder(=directory) of same name exists" +
                                            System.lineSeparator() + 
                                            pathname + "\nChange file name to a different one";
                                    break;
                                default:
                                    break;
                            }                            
                            
                            JOptionPane.showConfirmDialog(this,
                                    dialogText,
                                    ATT_SAVE_AS_FAIL_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE,
                                    WARNING_MESSAGE);
                            return;
                        } else {
                            String message = 
                                    SAVE_AS_EXIST_DIALOG.getContent() + System.lineSeparator() +
                                    pathname + System.lineSeparator() +
                                    SAVE_OVERWRITE_DIALOG.getContent();
                            
                            int result = JOptionPane.showConfirmDialog(this, message,
                                    CONFIRM_DIALOGTITLE.getContent(),
                                    JOptionPane.YES_NO_OPTION);
                            
                            if (result != YES_OPTION) {
                                return;
                            }
                        }
                    }
                    // </editor-fold>                 
                    CreateAttendantListTextFile(pathname);
                } catch (Exception ex) {
                    logParkingException(Level.SEVERE, ex, 
                            "(File: " + pathname + ")");
                } finally {
                }
            } else {
                System.out.println("Text File creation cancelled by user.");
            } 
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(User action: Save user list as a text file)");      
        }
    }//GEN-LAST:event_saveTextFileButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        try {
            // When deleting a user account, to compare user password (stored at a login time)
            // with the password entered in the delete form, dual table in the MySQL is needed.
            String pwHashed = getHashedPW(new String(userPassword.getPassword()));
            String dialogText = "";
            if (loginPW.equals(pwHashed) ) {
                // Get a confirmation from the user for the deletion.
                switch (language) {
                    case KOREAN:
                        dialogText = "아래 계정을 삭제합니까?" + System.lineSeparator()  
                                + "계정 ID: " + userIDText.getText();
                        break;
                    case ENGLISH:
                        dialogText = "Do you want to delete this user?" + System.lineSeparator() 
                                + "User ID: " + userIDText.getText();
                        break;
                    default:
                        break;
                }                
                
                int result = JOptionPane.showConfirmDialog(null, dialogText, 
                        DELETE_DIALOGTITLE.getContent(), 
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {            
                    deleteAttendant();
                } else {
                    // Clear password entered
                    userPassword.setText("");
                }
            } else {
                showMessageDialog(null, 
                        DELETE_FAIL_DAILOG.getContent() + System.lineSeparator(),
                        DELETE_FAIL_DAILOGTITLE.getContent(), 
                        JOptionPane.INFORMATION_MESSAGE);             
            }

            //throw new Exception("dummy");
        }
        catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(User Action: user account deletion)");
        }
    }
                
    private void deleteAttendant()
    {
        /**
         * Check if the user/attendant were deletable (not referenced from other tuple)
         * This checking involves two tables: Car_Arrival, LoginRecord
         */
        Connection conn = null;
        PreparedStatement updateStmt = null;
        String deleteID = userIDText.getText();
        
        int relatedRecordCount = DB_Access.getRecordCount("car_arrival", "AttendantID", deleteID);
        if (relatedRecordCount > 0) {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 사용자는 삭제할 수 없습니다" + System.lineSeparator() +
                    " - 아이디: " + userIDText.getText() + System.lineSeparator() + 
                    " - 이  유: 차량 도착기록이 존재합니다.(" + relatedRecordCount + ")" +
                    System.lineSeparator() + " * 자동차 도착 기록은 1 년간 저장됩니다.";
                    break;
                case ENGLISH:
                    dialogMessage = "Following user can't be deleted" + System.lineSeparator() +
                    " - User ID: " + userIDText.getText() + System.lineSeparator() + 
                    " - Reason: has nonzero(" + relatedRecordCount + ") Car Arrival Records" +
                    System.lineSeparator() + " * Car Arrival Records are stored for 1 year.";
                    break;
                default:
                    break;
            }
            
            JOptionPane.showMessageDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.WARNING_MESSAGE);   
            return; 
        }
        
        relatedRecordCount = DB_Access.getRecordCount("loginrecord", "UserID", deleteID);
        if (relatedRecordCount > 0) {
            String dialogMessage = "";

            switch (language) {
                case KOREAN:
                    dialogMessage = "다음 사용자는 삭제할 수 없습니다" + System.lineSeparator() +
                    " - 아이디 : " + userIDText.getText() + System.lineSeparator() + 
                    " - 이  유 : 로그인기록이 존재합니다.(" + relatedRecordCount + ") " +
                    System.lineSeparator() + " * 사용자 로그인 기록은 1 년간 저장됩니다.";
                    break;
                    
                case ENGLISH:
                    dialogMessage = "Following user can't be deleted" + System.lineSeparator() +
                    " - User ID: " + userIDText.getText() + System.lineSeparator() + 
                    " - Reason: has nonzero(" + relatedRecordCount + ") Login Records" +
                    System.lineSeparator() + " * User Login Records are stored for 1 year.";
                    break;
                    
                default:
                    break;
            }
            
            JOptionPane.showMessageDialog(this, dialogMessage,
                            DELETE_RESULT_DIALOGTITLE.getContent(), 
                            JOptionPane.WARNING_MESSAGE);   
            return; 
        }
        
        String sql = "Delete From users_osp WHERE id = ?";
        try {
            conn = JDBCMySQL.getConnection();
            updateStmt = conn.prepareStatement(sql);
            updateStmt.setString(1, deleteID);
            int result = updateStmt.executeUpdate();
            if (result == 1) {
                List sortKeys = usersTable.getRowSorter().getSortKeys();                
                RefreshTableContents(); 
                usersTable.getRowSorter().setSortKeys(sortKeys);
                if (selectedRowIndex == usersTable.getRowCount()) {
                    selectedRowIndex--;
                }
                usersTable.changeSelection(selectedRowIndex, 0, false, false); 
                usersTable.requestFocus();
                
                int realRow = usersTable.convertRowIndexToModel(selectedRowIndex);
                ShowAttendantDetail(realRow);  
                logParkingOperation(OpLogLevel.SettingsChange, 
                        ("* User deleted (ID:" + deleteID + ")"));
                
                String dialogMessage = "";
                
                switch (language) {
                    case KOREAN:
                        dialogMessage = "사용자(아이디: " + userIDText.getText() + ") 기록이" + 
                                System.lineSeparator() + "성공적으로 삭제되었습니다.";
                        break;
                        
                    case ENGLISH:
                        dialogMessage = "User(ID: " + userIDText.getText() + ") record" + 
                                System.lineSeparator() + "deleted successfully.";
                        break;
                        
                    default:
                        break;
                }                
                
                JOptionPane.showMessageDialog(this, dialogMessage,
                        DELETE_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE);  
                clearPasswordFields();
            } else {
                String dialogMessage = "";
                
                switch (language) {
                    case KOREAN:
                        dialogMessage = "사용자 삭제에 실패하였습니다!\n  아이디 : " + userIDText.getText();
                        break;
                        
                    case ENGLISH:
                        dialogMessage = "Failed Deletion of a User Account!" + 
                                System.lineSeparator() + " ID : " + userIDText.getText();
                        break;
                        
                    default:
                        break;
                }                
                
                JOptionPane.showMessageDialog(this, dialogMessage,
                        DELETE_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE);            
            }
        } catch (Exception se) {
            logParkingException(Level.SEVERE, se, "(ID: " + deleteID + ")");
        }  finally {
            closeDBstuff(conn, updateStmt, null, "(ID: " + deleteID + ")");
        }     
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void usersTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usersTableKeyPressed
        
        int keyCode = evt.getKeyCode();
        try {
            if (keyCode == KeyEvent.VK_ENTER || 
                    keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN ||
                    keyCode == KeyEvent.VK_PAGE_UP || keyCode == KeyEvent.VK_PAGE_DOWN)
            {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        int selRow = usersTable.getSelectedRow();
                        int realRow = usersTable.convertRowIndexToModel(selRow);
                        ShowAttendantDetail(realRow);               
                    }
                }); 
            }
        } catch (Exception ex) {
            String key = null;
            switch (keyCode) {
                case KeyEvent.VK_UP: key = "Up Arrow"; break;
                case KeyEvent.VK_DOWN: key = "Down Arrow"; break;
                case KeyEvent.VK_PAGE_UP: key = "Next Page Arrow"; break;
                case KeyEvent.VK_PAGE_DOWN: key = "Prev Page Arrow"; break;
            }
            logParkingException(Level.SEVERE, ex, "(User Action: " + key + " key entered while surfing user list)");
        }
    }//GEN-LAST:event_usersTableKeyPressed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // Prepare to accept property values for a new user to be created.
        try {
            formMode = FormMode.CreateMode;
            usersTable.setEnabled(false);
            ID_usable = false;
            legendLLabel.setText("\u203B"+GUIDELINE_LABEL.getContent()+"\u2212");
            // <editor-fold defaultstate="collapsed" desc="-- Enable data input fields">
            userIDText.setEnabled(true);
            userIDText.setText("");
            userIDText.setEditable(true);
            userIDText.requestFocusInWindow();

            checkIDButton.setEnabled(true);
            if (loginID.equals("admin"))
                managerAuthCheckBox.setEnabled(true);        
            managerAuthCheckBox.setSelected(false);

            changeEnabledProperty(true);
            userNameText.setText("");
            cellPhoneText.setText("");
            phoneText.setText("");
            emailAddrText.setText("");
            // </editor-fold>     

            // <editor-fold defaultstate="collapsed" desc="-- Enable password fields">           
            changePWCheckBox.setSelected(true);
            new1Password.setEnabled(true);
            PWHelpButton.setEnabled(true);
            new2Password.setEnabled(true);
            // </editor-fold>     

            changeBoxEditability(true);

            // <editor-fold defaultstate="collapsed" desc="-- Change visibility lower buttons">
            createButton.setEnabled(false);
            multiFuncButton.setText(SAVE_BTN.getContent());
            multiFuncButton.setMnemonic('s');
            changeButtonEnabled(false);
            multiFuncButton.setEnabled(true);
            deleteButton.setEnabled(false);
            
            // </editor-fold>    

            // <editor-fold defaultstate="collapsed" desc="-- Change labels of password fields">
            newPW1ReqLabel.setText("\u25CF");
            newPW2ReqLabel.setText("\u25CF");
            changePWLabel.setEnabled(false);
            newPW1Label.setText(NEW_PW_LABLE.getContent());
            newPW2Label.setText(REPEAT_PW_LABEL.getContent());
            userPWLabel.setText(MY_PW_LABEL.getContent());
            // </editor-fold>   
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(User Action: Clicked Create New User Button)");         
        }
    }//GEN-LAST:event_createButtonActionPerformed

    private boolean validateEmail(String email) {
        //Create InternetAddress object and validated the email address.
        final EmailValidator emailValidator = EmailValidator.getInstance();
        
        return emailValidator.isValid(email);
    }    
    
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        try {
            if (searchCriteriaComboBox.getSelectedIndex() == ATTLIST_ComboBoxTypes.ID.ordinal()) {
                searchCondition = " where id like '" + "%" + searchText.getText() + "%';";
            } else {
                searchCondition = " where name like '" + "%" + searchText.getText() + "%';";
            }
            List sortKeys = usersTable.getRowSorter().getSortKeys();                
            RefreshTableContents(); 
            usersTable.changeSelection(selectedRowIndex, 0, false, false);
            usersTable.requestFocus();
            ShowAttendantDetail(selectedRowIndex);
            
            usersTable.getRowSorter().setSortKeys(sortKeys);  
            if(usersTable.getRowCount()==0){
                multiFuncButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }else{
                multiFuncButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User action: user list search)");         
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on User(=Attendant) Project");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void PWHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PWHelpButtonActionPerformed
        pwValidator = new PasswordValidator();
        String helpText = pwValidator.getWrongPWFormatMsg(pwStrengthLevel);

        JDialog helpDialog = new PWHelpJDialog(this, false, 
                ATT_HELP_DIALOGTITLE.getContent(), helpText);
        Point buttonPoint = new Point();
        PWHelpButton.getLocation(buttonPoint);

        Point framePoint = new Point();
        this.getLocation(framePoint);
        Point topLeft = new Point(framePoint.x + buttonPoint.x + 60, framePoint.y + buttonPoint.y - 60);
        helpDialog.setLocation(topLeft);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_PWHelpButtonActionPerformed

    private void changePWCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePWCheckBoxActionPerformed
        ChangeNewPasswordEnabled(changePWCheckBox.isSelected());
    }//GEN-LAST:event_changePWCheckBoxActionPerformed

    private void checkEmailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkEmailButtonActionPerformed
        // Check syntax first, and then check if it's preoccupied, if everything were OK, then use the e-mail.
        // Otherwise, just return
        // Use avax.mail.jar library.
        // Refer http://examples.javacodegeeks.com/enterprise-java/mail/validate-email-address-with-java-mail-api/
        // Download: https://java.net/projects/javamail/pages/Home#Download_JavaMail_1.5.2_Release
        String emailEntered = emailAddrText.getText().trim();

        try {
            boolean checkResult = validateEmail(emailEntered);
            if (checkResult) {
                String sql = "Select count(*) as dataCount From users_osp Where email = ?";
                if (dataExistsInDB(sql, emailEntered)) {
                    // Access DB and find if entered e-mail is already registered to the system.
                    String dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "이메일 '" + emailAddrText.getText().trim() + "' 는 사용 중 입니다.\n"
                                + "다른 이메일을 입력하십시오.";
                            break;
                            
                        case ENGLISH:
                            dialogMessage = "E-mail '" + emailAddrText.getText().trim() + "' is in use"
                            + System.lineSeparator() + "Choose a different one.";
                            break;
                            
                        default:
                            break;
                    }                    
                    
                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            ATT_EMAIL_DUP_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                    emailAddrText.requestFocus();
                } else {
                    Email_usable = true;
                    usableEmail = emailEntered;
                    checkEmailButton.setEnabled(false);
                    
                    String dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "이메일 '" + emailAddrText.getText().trim() + 
                                    "' 는 사용가능합니다." + System.lineSeparator();
                            break;
                            
                        case ENGLISH:
                            dialogMessage = "E-mail '" + emailAddrText.getText().trim() + 
                                    "' could be used." ;
                            break;
                            
                        default:
                            break;
                    }                    
                    
                    JOptionPane.showConfirmDialog(this, dialogMessage,
                            ATT_EMAIL_DUP_DIALOGTITLE.getContent(),
                            JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                }
            } else {
                String dialogMessage = "";
                
                switch (language) {
                    case KOREAN:
                        dialogMessage = "이메일 주소 '" + emailAddrText.getText().trim() + 
                                "'는 구문이 바르지 않습니다.\n";
                        break;
                        
                    case ENGLISH:
                        dialogMessage = "E-mail address '" + emailAddrText.getText().trim() + 
                                "' has wrong syntax." + System.lineSeparator();
                        break;
                        
                    default:
                        break;
                }
                
                JOptionPane.showConfirmDialog(this, dialogMessage, 
                        ATT_EMAIL_SYNTAX_CHECK_DIALOG.getContent(),
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                emailAddrText.requestFocus();
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex,
                "(User action: new E-mail address('" + emailEntered + "') check button)");
        }
    }//GEN-LAST:event_checkEmailButtonActionPerformed

    /**
     * Checks E-mail address whenever its content is modified.
     * Even after an E-mail is once verified to be good, for each typing, e-mail check button needs to be 
     * considered to be enabled.
     * @param evt event
     */
    private void EmailTypedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EmailTypedHandler

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String tempEmail = emailAddrText.getText().trim();
                if (tempEmail.length() == 0) {
                    // when e-mail address isn't entered, check button is to be disabled
                    Email_usable = true;
                    checkEmailButton.setEnabled(false);
                } else {
                    if (Email_usable || !checkEmailButton.isEnabled()) {
                        if (!(tempEmail.equals(usableEmail))) {
                            Email_usable = false;
                            usableEmail = null;
                            checkEmailButton.setEnabled(true);
                        }
                    }
                }
            }
        });
    }//GEN-LAST:event_EmailTypedHandler

    private void checkIDButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkIDButtonActionPerformed
        String idEntered = userIDText.getText().trim();
        String sql = "Select count(*) as dataCount From users_osp Where id = ?";

        try {
            if (idEntered.length() < 2) {
                // Reject if ID were shorter than 2 characters
                JOptionPane.showConfirmDialog(this, ID_LENGTH_CHECK_DIALOG.getContent(),
                    ATT_ID_DUP_CHCEK_DIALOGTITLE.getContent(), 
                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                userIDText.requestFocusInWindow();
                return;
            } else if (dataExistsInDB(sql, idEntered)) {
                // Same ID is being used by other user, let the user know about this.
                
                String dialogMessage = "";
                
                switch (language) {
                    case KOREAN:
                        dialogMessage = "아이디 '" + userIDText.getText().trim() + 
                            "' 는 사용 중 입니다." + System.lineSeparator() 
                            + "다른 아이디를 입력하십시오.";
                        break;
                        
                    case ENGLISH:
                        dialogMessage = "ID '" + userIDText.getText().trim() + 
                            "' is preoccupied by someone else." + System.lineSeparator() 
                            + "Choose a different ID";
                        break;
                        
                    default:
                        break;
                }                
                
                JOptionPane.showConfirmDialog(this, dialogMessage,
                        ATT_ID_DUP_CHCEK_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                userIDText.requestFocusInWindow();
            } else {
                String checkResult = IDSyntaxCheckResult(idEntered);
                if (checkResult.length() == 0) {
                    ID_usable = true;
                    usableID = idEntered;
                    checkIDButton.setEnabled(false);
                    
                    String dialogMessage = "";
                    
                    switch (language) {
                        case KOREAN:
                            dialogMessage = "아이디 '" + userIDText.getText().trim() + 
                                    "' 는 사용가능합니다." + System.lineSeparator() ;
                            break;
                            
                        case ENGLISH:
                            dialogMessage = "ID '" + userIDText.getText().trim() + 
                                    "' is usable." + System.lineSeparator() ;
                            break;
                            
                        default:
                            break;
                    }                    
                    
                    JOptionPane.showConfirmDialog(this, dialogMessage,
//                            getTextFor(ID_CHECK_GOOD_DIALOG, userIDText.getText().trim()),
                            ATT_ID_DUP_CHCEK_DIALOGTITLE.getContent(), 
                             JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showConfirmDialog(this, 
                            checkResult,
                            ATT_ID_DUP_CHCEK_DIALOGTITLE.getContent(), 
                            JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex,
                "(User Action: new user ID('" + idEntered + "') Check Button is Being Processed)");
        }
    }//GEN-LAST:event_checkIDButtonActionPerformed

    private void newIDtyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newIDtyped
        if (ID_usable) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    String tempID = userIDText.getText().trim();
                    if (!(tempID.equals(usableID))) {
                        ID_usable = false;
                        usableID = null;
                        checkIDButton.setEnabled(true);
                    }
                }
            });
        }
    }//GEN-LAST:event_newIDtyped

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (mainGUI != null)
            mainGUI.clearAttendantManageForm();
    }//GEN-LAST:event_formWindowClosing

    private void CreateAttendantListTextFile(String filename) {
        //FileWriter writer = null;
        BufferedWriter bw = null;
        StringBuilder sb  = new StringBuilder();

        try {
            //writer = new FileWriter(filename);
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"));
            
            // Write table heading
            int rowFrame;
            int colCount = usersTable.getModel().getColumnCount();
            writeColumnHeadings(bw, colCount);
            
            for (rowFrame = 0; rowFrame < usersTable.getModel().getRowCount(); rowFrame++)
            {
                // Get the number of row shown at this row frame.
                int row = usersTable.convertRowIndexToModel(rowFrame);
                
                // Read the table row by row
                for (int colFrame = 0; colFrame < colCount; colFrame++)
                {
                    // Read the line element by element 
                    // Get the column index shown at this table frame column
                    int col = usersTable.convertColumnIndexToModel(colFrame);
                    Object itemObj = usersTable.getModel().getValueAt(row, col);
                    if (itemObj == null) {
                        // for an empty(null) element, write a blank string as a place holder.
                        bw.write("");
                    } else {
                        bw.write(itemObj.toString());
                    }
                    if (colFrame < colCount -1) {
                        // Use tab as the element separator within a line
                        bw.write("\t");
                    }
                }
                // Add CRLF at the end of each line and make it readable from notepad
                bw.write(System.getProperty("line.separator"));
            }
            // Show the result in a ack popup box
            StringBuilder dialogBuilder = new StringBuilder();
            
            int lastSlash = filename.lastIndexOf((int)'\\');
            switch (language) {
                case KOREAN:
                    dialogBuilder.append("생성 완료!\n  -파일이름: ");
                    dialogBuilder.append(filename.substring(lastSlash + 1));
                    dialogBuilder.append(" (총 ");
                    dialogBuilder.append(rowFrame);
                    dialogBuilder.append("행)");
                    dialogBuilder.append("\n  -파일위치: ");
                    dialogBuilder.append(filename.substring(0, lastSlash + 1));
                    break;
                    
                case ENGLISH:
                    dialogBuilder.append("File Created!" + System.lineSeparator() + "  - File name: ");
                    dialogBuilder.append(filename.substring(lastSlash + 1));
                    dialogBuilder.append(" (total ");
                    dialogBuilder.append(rowFrame);
                    dialogBuilder.append("lines)");
                    dialogBuilder.append(System.lineSeparator() + "  - File Location: ");
                    dialogBuilder.append(filename.substring(0, lastSlash + 1));
                    break;
                    
                default:
                    break;
            }            
            
            JOptionPane.showMessageDialog(this, dialogBuilder.toString(),
                    ATT_SFAVE_AS_SUCCESS_DIALOGTITLE.getContent(), 
                    JOptionPane.PLAIN_MESSAGE);            
        } catch (Exception ex) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                logParkingException(Level.SEVERE, ex, "(finally-filename: " + filename + ")");
            }
        }
    }

    private void writeColumnHeadings(BufferedWriter bw, int colCount) {
        String colHeading = null;
        try {
            for (int colFrame=0; colFrame < colCount; colFrame++)
            {
                int col = usersTable.convertColumnIndexToModel(colFrame);            
                colHeading = usersTable.getModel().getColumnName(col);
                bw.write(colHeading);
                if (col < colCount -1)
                {
                    // except the last element of each row, write a 'tab' seperator
                    bw.write("\t");
                }
            }
            bw.write(System.getProperty("line.separator"));
        } catch (IOException ex) {
            logParkingException(Level.SEVERE, ex, "(colHeading: " + colHeading + ")");
        }
    }


    private void clearPasswordFields() {
        userPassword.setText("");
        new1Password.setText("");
        new2Password.setText("");
    }

    private int getNumericDigitCount(String cellPhone) {
        int numCount = 0;
        
        if (cellPhone == null || cellPhone.length() == 0)
        {
            return 0;
        }
        for (char aChar : cellPhone.toCharArray()) {
            if (aChar >= '0' && aChar <= '9') {
                numCount++;
            }
        }
        return numCount;
    }

    private void changeEnabledProperty(boolean b) {
        userNameText.setEnabled(b);
        cellPhoneText.setEnabled(b);
        phoneText.setEnabled(b);
        emailAddrText.setEnabled(b);
        userPassword.setEnabled(b);
    }

    private void ChangeFieldEditableForManager(String rowID, boolean rowIsForA_Manager) {
        createButton.setEnabled(true);            
        if (rowID.equals("admin")) {
            changeDeletableProperty(false);
            if (loginID.equals("admin")) {
                // admin manages self information
                changeEnabledProperty(true);
                multiFuncButton.setEnabled(true);  
                //CurrentPassword.setEnabled(false);
            } else {
                // non-admin administrator attendant manages admin's information
                // (cf. average attendant can be an administrator but not an 'admin'.
                // 'admin' is a special ID which is unique in the OsParking system.
                changeEnabledProperty(false);
                multiFuncButton.setEnabled(false);            
            }
        } else if (rowID.equals(loginID)){
            // administrator's self information management
            // can't delete his/her own record
            changeDeletableProperty(false);
            changeEnabledProperty(true);
            multiFuncButton.setEnabled(true);         
        } else if (!rowIsForA_Manager || loginID.equals("admin")){
            changeDeletableProperty(true);
            changeEnabledProperty(true);
            multiFuncButton.setEnabled(true);         
        } else {
            // non-admin attendant's information management by administrator
            // : update and deletion are possible
            changeDeletableProperty(false);
            changeEnabledProperty(false);
            multiFuncButton.setEnabled(false);         
        }
    }

    private void ChangeFieldPropForAttendants(String rowID) {
        managerAuthCheckBox.setEnabled(false);
        createButton.setEnabled(false);            
        changeDeletableProperty(false);
        if (loginID.equals(rowID)) { 
            // user manages self information
            changeEnabledProperty(true);
            multiFuncButton.setEnabled(true);  
        } else {
            // a user can't manage other user's information
            changeEnabledProperty(false);
            multiFuncButton.setEnabled(false);              
        }
    }

    private void revokeCreationMode(boolean created) {
        // Reset properties set for a new user as it was.
        // <editor-fold defaultstate="collapsed" desc="-- Reset Password LabelsText">
        newPW1ReqLabel.setText("");
        newPW2ReqLabel.setText("");
        changePWLabel.setEnabled(true);
        newPW1Label.setText(NEW_PW_LABLE.getContent());
        newPW2Label.setText(REPEAT_PW_LABEL.getContent());
        userPWLabel.setText(MY_PW_LABEL.getContent());
        // </editor-fold> 
        
        // <editor-fold defaultstate="collapsed" desc="-- Enable two buttons back again">        
        createButton.setEnabled(true);
        multiFuncButton.setText(MODIFY_BTN.getContent());
        changeButtonEnabled(true);
        multiFuncButton.setMnemonic('m');
        // </editor-fold>   
        
        changeBoxEditability(false);
        clearPasswordFields();        

        // <editor-fold defaultstate="collapsed" desc="-- Disable password related controls in search mode">        
        changePWCheckBox.setSelected(false);
        new1Password.setEnabled(false);
        PWHelpButton.setEnabled(false);
        new2Password.setEnabled(false);  
        // </editor-fold>   

        if (created) {
            // <editor-fold defaultstate="collapsed" desc="-- Refresh user list while maintaining sort order">            
            List sortKeys = usersTable.getRowSorter().getSortKeys();
            RefreshTableContents(); 
            usersTable.getRowSorter().setSortKeys(sortKeys);
            String newUserID = userIDText.getText().trim();
            selectedRowIndex = searchRow(newUserID);
            usersTable.changeSelection(selectedRowIndex, 0, false, false); 
            usersTable.requestFocus();
            // </editor-fold>   
        }
        
        usersTable.setEnabled(true);
        formMode = FormMode.NormalMode;
        if(selectedRowIndex < 0)
            selectedRowIndex = 0;
        ShowAttendantDetail(usersTable.convertRowIndexToModel(selectedRowIndex));     
        
        // <editor-fold defaultstate="collapsed" desc="-- Disable login ID related controls in search mode">            
        managerAuthCheckBox.setEnabled(false);        
        userIDText.setEnabled(false); 
        userIDText.setEditable(false);       
        checkIDButton.setEnabled(false); 
        // </editor-fold>   

        legendLLabel.setText("\u203B" + REQUIRED_LABEL.getContent() + "\u2212");       
    }

    private boolean dataExistsInDB(String sql, String dataEntered) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement pstmt = null;    
        ResultSet rs = null;
        try 
        {
            conn = JDBCMySQL.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dataEntered);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                result = false;
            } else {
                result = true;
            }
        }
        catch(Exception ex)
        {
            logParkingException(Level.SEVERE, ex, 
                    "(sql: " + sql + ", dataEntered: " + dataEntered +")");
        }
        finally {
            closeDBstuff(conn, pstmt, rs, "(finally-sql: " + sql + ", dataEntered: " + dataEntered +")");
        }
        return result;
    } 

    private String IDSyntaxCheckResult(String idEntered) {
        // ID syntax requirement:
        // Allowed characters: alphabet, number digit(0-9), nested space or dot(.)
        // Additionally, ID should begin with an alphabet, ends by alphabet or number digit.
        // E.g., "Henry14", "John K. Park"
        
        StringBuilder tempStr = new StringBuilder();
        int idLen = idEntered.length();
        
        if (!(Character.isLetter(idEntered.charAt(0)))) {
            tempStr.append(ID_FIRST_CHAR_CHECK_DIALOG.getContent() + System.lineSeparator());
        } 
        for (int i = 0; i < idLen - 1; i++) {
            char ch = idEntered.charAt(i);
            
            if (!(Character.isLetter(ch)) && !(Character.isDigit(ch)) && ch != ' ' && ch != '.')
            {
                tempStr.append(ID_CHAR_CHECK_DIALOG.getContent()  + System.lineSeparator());
            }
        }
        char lastCh = idEntered.charAt(idLen - 1);
        
        if (!(Character.isLetter(lastCh)) && !(Character.isDigit(lastCh))) 
        {
            tempStr.append(ID_END_CHAR_CHECK_DIALOG.getContent() + System.lineSeparator());
//                    ((String[])Globals.DialogMSGList.get(ID_END_CHAR_CHECK_DIALOG.getContent()
        }
        if (tempStr.length() >= 1) {
            tempStr.deleteCharAt(idLen - 1); // remove last newline character
        }
        return tempStr.toString();
    }

    private int saveCreatedRecord() {
        Connection conn = null;        
        PreparedStatement createAttendant = null;
        String sql = "Insert Into  users_osp (id, name, password, email, isManager, " + 
                "cellphone, phone) values (?, ?, md5(?), ?, ?, ?, ?)";
        int result = -1;
        try {
            int pIndex = 1;
            conn = JDBCMySQL.getConnection();
            createAttendant = conn.prepareStatement(sql);
            
            // <editor-fold defaultstate="collapsed" desc="-- Provide actual value to each field">
            createAttendant.setString(pIndex++, userIDText.getText().trim());
            createAttendant.setString(pIndex++, userNameText.getText().trim());
            createAttendant.setString(pIndex++, new String(new1Password.getPassword()));
            String email = emailAddrText.getText().trim();
            if (email.length() == 0) {
                createAttendant.setString(pIndex++, null);
            } else {
                createAttendant.setString(pIndex++, email);
            }
            if (managerAuthCheckBox.isSelected()) {
                createAttendant.setInt(pIndex++, 1);
            } else {
                createAttendant.setInt(pIndex++, 0);
            }
            createAttendant.setString(pIndex++, cellPhoneText.getText().trim());
            createAttendant.setString(pIndex++, phoneText.getText().trim());
            // </editor-fold>

            result = createAttendant.executeUpdate();

        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(ID: " + userIDText.getText() + 
                    ", name: " + userNameText.getText().trim() + 
                    ", email: " + emailAddrText.getText().trim() + 
                    ", cell: " + cellPhoneText.getText().trim() + 
                    ", phone: " + phoneText.getText().trim() + ")");                    
        } finally {
            closeDBstuff(conn, createAttendant, null, "(ID: " + userIDText.getText() + ")");
            return result;
        }
    }

    private int searchRow(String userID) {
        for(int row = 0; row < usersTable.getRowCount(); row++) { 
            String next = (String)usersTable.getValueAt(row, 0);  
            
            if(next.equals(userID))  
            {  
                return row;  
            }  
        }  
        return -1;
    }

    private void doAfterUpdateOperations(int result) {
        if (result == 1) {
            List sortKeys = usersTable.getRowSorter().getSortKeys();
            RefreshTableContents(); 
            usersTable.getRowSorter().setSortKeys(sortKeys);    
            logParkingOperation(OpLogLevel.SettingsChange, getModifiedUserInfo());       
            
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "사용자(ID: " + userIDText.getText().trim() + 
                            ") 정보가\n성공적으로 수정되었습니다.";
                    break;

                case ENGLISH:
                    dialogMessage ="A user(ID: " + userIDText.getText().trim() + ") information" + 
                            System.lineSeparator() + "successfully modified.";
                    break;
                    
                default:
                    break;
            }
            
            JOptionPane.showMessageDialog(this, dialogMessage,
                    ATT_USER_UPDATE_DIALOGTITLE.getContent(), 
                    JOptionPane.PLAIN_MESSAGE);  
            clearPasswordFields();
        } else {
            String dialogMessage = "";
            
            switch (language) {
                case KOREAN:
                    dialogMessage = "정보 수정에 실패하였습니다!\n  "
                            + "ID: " +  userIDText.getText().trim();
                    break;
                    
                case ENGLISH:
                    dialogMessage = "User Info Change Failure!" + System.lineSeparator() + 
                            "ID: " +  userIDText.getText().trim();
                    break;
                    
                default:
                    break;
            }
                
            JOptionPane.showMessageDialog(this, dialogMessage,
                    ATT_USER_UPDATE_DIALOGTITLE.getContent(), 
                    JOptionPane.PLAIN_MESSAGE);            
        }
        selectedRowIndex = searchRow(userIDText.getText());
        usersTable.changeSelection(selectedRowIndex, 0, false, false); 
        usersTable.requestFocus();
        
        ShowAttendantDetail(selectedRowIndex);
    }

    private void setModificationState(boolean flag) {
        boolean isAdminRerocd = (loginID.equals("admin"));
        boolean notOwnRecord = !(loginID.equals(userIDText.getText()));
        if (!isAdminRerocd && notOwnRecord) {
            // No user can change self 'admin' property
            // No user with admin right can change admin property of user 'admin'
            if (loginID.equals("admin")) {
                managerAuthCheckBox.setEnabled(flag);
                if (flag) {
                    managerAuthCheckBox.requestFocusInWindow();
                }
            }
        } else {
            if (flag) {
                userNameText.requestFocusInWindow();
            } else {
                usersTable.requestFocus();
            }
        }
        changePWCheckBox.setEnabled(flag);
        if(isAdminRerocd && notOwnRecord)
            managerAuthCheckBox.setEnabled(flag);
        changeBoxEditability(flag);
        changeButtonEnabled(!flag);        
    }

    private void changeBoxEditability(boolean bln) {
        userNameText.setEditable(bln);
        cellPhoneText.setEditable(bln);
        phoneText.setEditable(bln);
        emailAddrText.setEditable(bln);        
        new1Password.setEditable(bln);
        new2Password.setEditable(bln);
        userPassword.setEditable(bln);
    }

    private void changeDeletableProperty(boolean b) {
        deleteButton.setEnabled(b);                  
        userPassword.setEditable(b);
    }

    private void changeButtonEnabled(boolean flag) {
        cancelButton.setEnabled(!flag);
        searchText.setEnabled(flag);
        searchCriteriaComboBox. setEnabled(flag);
        searchButton.setEnabled(flag);
        saveTextFileButton. setEnabled(flag);
        closeFormButton.setEnabled(flag); 
    }

    private String showPasswordRequirement() {
        pwValidator = new PasswordValidator();
        return pwValidator.getPasswordTooltip();
    }

    private String getModifiedUserInfo() {
        StringBuilder sBuild = new StringBuilder();

        sBuild.append("User Info Modification Summany=>" + System.lineSeparator());
        sBuild.append("\tModified User ID: " + userIDText.getText());
        sBuild.append(",\tIs Admin: " + (managerAuthCheckBox.isSelected() ? "Yes" : "No"));
        sBuild.append(System.lineSeparator());
        
        sBuild.append("\tName: " + userNameText.getText().trim());
        sBuild.append(",\tCell: " + cellPhoneText.getText().trim() + System.lineSeparator());
        
        sBuild.append("\tPhone: " + phoneText.getText().trim());
        sBuild.append(",\tE-mail: " + emailAddrText.getText().trim() + System.lineSeparator());
                
        sBuild.append("\tPassword: " + (changePWCheckBox.isSelected() ? "changed" : "no change"));
        sBuild.append(", \tModifying User ID: " + loginID + System.lineSeparator());

        return sBuild.toString();
    }

    class TextFileOnly extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Text documents (*.txt)";
        }
    }        
    
    class OdsFileOnly extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".ods");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "ods documents (*.ods)";
        }
    }  
    
    // <editor-fold defaultstate="collapsed" desc="-- System Generated Code Section">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PWD_Panel;
    private javax.swing.JButton PWHelpButton;
    private javax.swing.JPanel RequiredPanel1;
    private javax.swing.JCheckBox adminAuth2CheckBox;
    private javax.swing.JLabel adminAuthLabel;
    private javax.swing.JPanel adminPanel;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel cellPhonePanel;
    private javax.swing.JTextField cellPhoneText;
    private javax.swing.JLabel cellReqLabel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JCheckBox changePWCheckBox;
    private javax.swing.JPanel changePWD_Panel;
    private javax.swing.JLabel changePWLabel;
    private javax.swing.JButton checkEmailButton;
    private javax.swing.JButton checkIDButton;
    private javax.swing.JButton closeFormButton;
    private javax.swing.JButton createButton;
    private javax.swing.JTextField creationDateText;
    private javax.swing.JPanel currentPWD_Panel;
    private javax.swing.JPanel datePanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField emailAddrText;
    private javax.swing.JPanel emailCheckPanel;
    private javax.swing.JPanel emailPanel;
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
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
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
    private javax.swing.Box.Filler filler52;
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
    private javax.swing.Box.Filler filler75;
    private javax.swing.Box.Filler filler76;
    private javax.swing.Box.Filler filler77;
    private javax.swing.Box.Filler filler78;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler80;
    private javax.swing.Box.Filler filler81;
    private javax.swing.Box.Filler filler82;
    private javax.swing.Box.Filler filler83;
    private javax.swing.Box.Filler filler84;
    private javax.swing.Box.Filler filler85;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel idCheckPanel;
    private javax.swing.JLabel isIDreqLabel;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel legendLLabel;
    private javax.swing.JLabel legendMLabel;
    private javax.swing.JLabel legendMLabel2;
    private javax.swing.JLabel legendRLabel;
    private javax.swing.JLabel legendRLabel2;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JCheckBox managerAuthCheckBox;
    private javax.swing.JButton multiFuncButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel namePanel;
    private javax.swing.JLabel nameReqLabel;
    private javax.swing.JPasswordField new1Password;
    private javax.swing.JPasswordField new2Password;
    private javax.swing.JLabel newPW1Label;
    private javax.swing.JLabel newPW1ReqLabel;
    private javax.swing.JLabel newPW2Label;
    private javax.swing.JLabel newPW2ReqLabel;
    private javax.swing.JPanel phonePanel;
    private javax.swing.JLabel phoneReqLabel;
    private javax.swing.JTextField phoneText;
    private javax.swing.JPanel repeatPWD_Panel;
    private javax.swing.JFileChooser saveFileName;
    private javax.swing.JButton saveTextFileButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchCriteriaComboBox;
    private javax.swing.JTextField searchText;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel spacePanel1;
    private javax.swing.JPanel spacePanel2;
    private javax.swing.JPanel topInPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel userID2Label;
    private javax.swing.JLabel userIDLabel;
    private javax.swing.JTextField userIDText;
    private javax.swing.JTextField userNameText;
    private javax.swing.JLabel userPWLabel;
    private javax.swing.JLabel userPWLabel1;
    private javax.swing.JLabel userPWReqLabel;
    private javax.swing.JPasswordField userPassword;
    private javax.swing.JTable usersTable;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    
    private void RefreshTableContents() {
        DefaultTableModel model =  (DefaultTableModel) usersTable.getModel();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Select id as 'User ID', name as 'Name', ");
        sb.append("if(isManager, 'Y', 'N') as 'Manager', ");
        sb.append("cellphone as 'Cell', phone as 'Phone', email as 'E-mail', ");
        sb.append("date_format(creationTime, '%Y-%m-%d') as 'Created', ");
        sb.append("date_format(lastModiTime, '%Y-%m-%d %H:%i') as 'Modified' ");
        sb.append("from users_osp");
        sb.append(searchCondition);
        try {
            conn = JDBCMySQL.getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            rs = pstmt.executeQuery();
	    /*
            usersTable.setModel(DbUtils.resultSetToTableModel(rs));
            int dimX = usersTable.getPreferredSize().width;
            int rowHeight = usersTable.getRowHeight();
            usersTable.setPreferredSize(new Dimension(dimX, 
                    rowHeight * usersTable.getRowCount()));
            SetTableColumnWidth();
	    */

            model.setRowCount(0);
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getString("User ID"),
                    rs.getString("Name"),
                    rs.getString("Manager"),
                    rs.getString("Cell"),
                    rs.getString("Phone"),
                    rs.getString("E-mail"),
                    rs.getString("Created"),
                    rs.getString("Modified")
                });
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(refresh user list displaying table)");
        } finally {
            closeDBstuff(conn, pstmt, rs, "(refresh user list displaying table)");
        }
    }

    private void ShowAttendantDetail(int clickedRow) {
        TableModel attModel = usersTable.getModel();
        if ( attModel.getRowCount() == 0)
        {
            return;
        }

        Object field = null;

        if (formMode != FormMode.CreateMode) {
            // <editor-fold defaultstate="collapsed" desc="-- Display ID and E-mail, initialize password">
            String tableRowID = attModel.getValueAt(clickedRow, 0).toString();
            boolean isManager = (attModel.getValueAt(clickedRow, 2).toString().equals("Y") ? true : false);
        
            userIDText.setText(tableRowID);
            changeFieldAndButtonProperties(tableRowID, isManager);
        
            field = attModel.getValueAt(clickedRow, 5);
            if (field == null) {
                emailAddrText.setText("");
            }
            else{
                emailAddrText.setText(field.toString());
            }            
            changePWCheckBox.setSelected(false);
            ChangeNewPasswordEnabled(false);
            userPassword.setText("");   
            creationDateText.setText(attModel.getValueAt(clickedRow, 6).toString());
            // </editor-fold>
        }
        userNameText.setText(attModel.getValueAt(clickedRow, 1).toString());
        String adminAuthority = attModel.getValueAt(clickedRow, 2).toString();
        managerAuthCheckBox.setSelected(adminAuthority.equals("Y"));
        
        // <editor-fold defaultstate="collapsed" desc="-- 2 Display or copy phone number">            
        field = attModel.getValueAt(clickedRow, 3);
        if (field == null) {
            cellPhoneText.setText("");
        }
        else{
            cellPhoneText.setText(field.toString());
        }
        
        field = attModel.getValueAt(clickedRow, 4);
        if (field == null) {
            phoneText.setText("");
        }
        else{
            phoneText.setText(field.toString());
        }
        // </editor-fold>

        if (isManager)
        {
            userPassword.setToolTipText(PW_INPUT_TOOTLTIP.getContent());
            userPWLabel.setToolTipText((PW_INPUT_TOOTLTIP.getContent()));
        }
    }

    private void changeFieldAndButtonProperties(String rowID, boolean rowManager) {
        if (loginID == null) {
            return;
        }
        if (isManager) {            
            ChangeFieldEditableForManager(rowID, rowManager);
        } else {
            ChangeFieldPropForAttendants(rowID);
        }
    }

    private void SetTableColumnWidth() {
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel tcm = usersTable.getColumnModel();
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Adjust column width one by one
        // Adjust email address column width
        tcm.getColumn(2).setCellRenderer(centerRenderer);

        SetAColumnWidth(tcm.getColumn(0), 10, 80, 500); // 0: User ID
        SetAColumnWidth(tcm.getColumn(1), 10, 80, 500); // 1: User name
        SetAColumnWidth(tcm.getColumn(2), 10, 60, 500); // 2: Is Administrator or not
        SetAColumnWidth(tcm.getColumn(3), 10, 100, 32767); // 2: Cell Phone 
        SetAColumnWidth(tcm.getColumn(4), 10, 100, 32767); // 3: Phone number
        SetAColumnWidth(tcm.getColumn(5), 10, 120, 32767); // 5: E-mail address
        //SetAColumnWidth(tcm.getColumn(7), 10, 120, 32767); // 7: Latest modification date and time
        SetAColumnWidth(tcm.getColumn(6), 10, 120, 32767); // 6: Latest modification date and time
    }
    
//    private String getTextFor(ControlEnums.DialogMSGTypes msgType, String str) {
//    private String getTextFor(AttListDialogType dialogType, String str) {
//        String label = null;
//        
//        switch (dialogType) {
//           
//            case SAVE_AS_FILE_FAILURE_DIALOG:
//            switch (parkingLotLocale.getLanguage()) {
//                case "ko":
//                    label = SAVE_AS_EXIST_DIALOG.getContent() + System.lineSeparator() +
//                            str + System.lineSeparator() + 
//                            SAVE_OVERWRITE_DIALOG.getContent();
//                    break;
//                default:
//                    label = "A folder(=directory) of same name exists" +
//                            System.lineSeparator() + 
//                            str + "\nChange file name to a different one";
//                    break;
//                }
//                break;                        
//
//            default :
//                break;
//            
//        
//        }
//        return label;
//    }
    
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
            logParkingException(Level.SEVERE, ex, "(exception class: ClassNotFoundException)");
        } catch (InstantiationException ex) {
            logParkingException(Level.SEVERE, ex, "(exception class: InstantiationException)");
        } catch (IllegalAccessException ex) {
            logParkingException(Level.SEVERE, ex, "(exception class: IllegalAccessException)");
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logParkingException(Level.SEVERE, ex, "(exception class: UnsupportedLookAndFeelException)");
        }
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    storePassingDelay = true;
                    LoginForm loginForm = new LoginForm();
                    loginForm.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    loginForm.setVisible(true);
                    loginForm.addLoginEventListener(new LoginEventListener() {
                        public void loginEventOccurred(LoginWindowEvent w) {

                            Globals.loginID = w.getUID();
                            Globals.loginPW = w.getPW();
                            Globals.isManager = w.getIsManager();
                            final AttListForm attendantsForm = new AttListForm(null,
                                    Globals.loginID, Globals.loginPW, Globals.isManager);
                            attendantsForm.setDefaultCloseOperation(EXIT_ON_CLOSE);
                            attendantsForm.addWindowListener( new WindowAdapter() {
                                public void windowOpened( WindowEvent e ){
                                    attendantsForm.searchText.requestFocus();
                                }
                            });                            
                            
                            attendantsForm.setVisible(true); 
                        }
                    });
                    shortLicenseDialog(loginForm, "Attendant Program", "upper left");
                } catch (Exception ex) {
                }
            }
        });
    }
}

enum FormMode {
    NormalMode,
    CreateMode,
    UpdateMode
}
