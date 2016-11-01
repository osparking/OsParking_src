/*
 * Copyright (C) 2016 Open Source Parking Inc.(www.osparking.com)
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
package com.osparking.global;

import static com.osparking.global.CommonData.ODS_FILEPATH;
import static com.osparking.global.names.ControlEnums.DialogMessages.OVERWRITE_WARNING_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.OVERWRITE_WARNING_TITLE;
import static com.osparking.global.names.ControlEnums.DialogMessages.USER_DELETE_CONF_3;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ODS_SAVE_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_1;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_2;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_3;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Open Office spread sheet file (extension 'ods') related routines.
 * @author Open Source Parking Inc.(www.osparking.com)
 */
public class DataSheet {
    
//    /**
//     * Verify if the file has extension of 'ods'.
//     * 
//     * @param saveFileChooser
//     * @param file 
//     */
//    public static String verifyOdsExtension(JFileChooser saveFileChooser, File[] file) {
//        String ext = "";
//        String pathname = null;
//        try {
//            pathname = file[0].getAbsolutePath();
//            String extension = saveFileChooser.getFileFilter().getDescription();
//            if (extension.indexOf("*.ods") >= 0) {
//                // Finish making ods file name from the name supplied from the user
//                int start = pathname.length() - 4;
//                // Java doesn't have endsWithIgnoreCase. So, ...
//                if (start < 0 || !pathname.substring(start).equalsIgnoreCase(".ods")) {
//                    //<editor-fold defaultstate="collapsed" desc="// In case pathname doesn't have ".ods" suffix">
//                    // Give it the ".ods" extension, automatically.
//                    // pure file name(except extension name) has no ".ods" suffix
//                    // So, to make it a ods file, append ".ods" extension to the filename.
//                    //</editor-fold>
//                    ext = ".ods";
//                    pathname += ext;
//                    file[0] = new File(pathname);
//                } else {
//                    // pathname already has ".ods" as its suffix
//                }
//            }
//        } catch (Exception ex) {
//            logParkingException(Level.SEVERE, ex, "(File: " + pathname + ")");
//        } finally {
//            return ext;
//        }
//    }    

    /**
     * Saves data in a Java Table into an Open Office Calc file. 
     * Note that Open Office Calc has its file extension of 'ods'.
     * 
     * @param aFrame the Java frame from which this saveODSfile is called
     * @param tableToSave the Java table containing data to save
     * @param saveFileChooser a file chooser dialog object
     * @param emptyTableMsg warning message to show when the Java table is empty
     */
    public static void saveODSfile(JFrame aFrame, JTable tableToSave,
            JFileChooser saveFileChooser, String emptyTableMsg)
    {
        // Check the size of the list and if empty just return saying "noting to save"
        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String dirPath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String filename = saveFileChooser.getSelectedFile().getName();

            filePath += File.separator + filename;
            
            TableModel model = getSaveModel(tableToSave);
            saveOrNotWithFixedName(saveFileChooser, filePath, model, dirPath, filename);
        }        
    }
    
    public static void saveODSfile(JFrame aFrame, Object[][] data, String[] columns,
            JFileChooser saveFileChooser) {
        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            String filePath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String dirPath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String filename = saveFileChooser.getSelectedFile().getName();

            filePath += File.separator + filename;
            TableModel model = new DefaultTableModel(data, columns);
            saveOrNotWithFixedName(saveFileChooser, filePath, model, dirPath, filename);   
        }
    }
    
    /**
     * Saves data in a Java Table into an Open Office Calc file. 
     * Note that Open Office Calc has its file extension of 'ods'.
     * 
     * @param aFrame the Java frame from which this saveODSfile is called
     * @param tableToSave the Java table containing data to save
     * @param saveFileChooser a file chooser dialog object
     * @param emptyTableMsg warning message to show when the Java table is empty
     */
    public static void saveODSfileName(JFrame aFrame, JTable tableToSave,
            JFileChooser saveFileChooser, String emptyTableMsg, String filename)
    {
        String filePath = ODS_FILEPATH +  File.separator + filename;
        File defFile = new File(filePath);
        saveFileChooser.setSelectedFile(defFile);
        
        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String selPath = saveFileChooser.getSelectedFile().getAbsolutePath();
            String selCore = saveFileChooser.getSelectedFile().getName();
            
            TableModel model = getSaveModel(tableToSave);
            
            saveOrNotWithFixedName(saveFileChooser, selPath, model, ODS_FILEPATH, selCore);
        }
    }

    public static void saveOrNotWithFixedName(JFileChooser saveFileChooser, 
            String filePath, TableModel model, String dirPath, String coreName) 
    {
        File file = null;
        String filename = coreName;
        
        file = saveFileChooser.getSelectedFile();
        if (!coreName.toLowerCase().endsWith(".ods")) 
        {
            filePath += ".ods";
            filename += ".ods";
            file = new File(filePath);
        } 

        if (noOverwritePossibleExistingSameFile(file, filePath)) {
            return;
        }
        
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
            //<editor-fold desc="-- Display result.">
            String message = ODS_SAVE_DIALOG_1.getContent() + System.lineSeparator() + 
                    System.lineSeparator() +
                    ODS_SAVE_DIALOG_2.getContent() + dirPath + System.lineSeparator() + 
                    ODS_SAVE_DIALOG_3.getContent() + filename + System.lineSeparator();
            JOptionPane.showMessageDialog(saveFileChooser, message, 
                    ODS_SAVE_TITLE.getContent(), JOptionPane.INFORMATION_MESSAGE);
            
            OOUtils.open(file);
            //</editor-fold>
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, 
                    "ods file writing failure : " + System.lineSeparator() +
                            ex.getMessage(), ERROR_DIALOGTITLE.getContent(), 
                    JOptionPane.WARNING_MESSAGE);             
        }                
    }

    public static boolean noOverwritePossibleExistingSameFile(File file, String filePath) {
        if (file.exists() && !file.isDirectory()) { 
            String msg = OVERWRITE_WARNING_DIALOG.getContent() + System.lineSeparator()
                    + System.lineSeparator()
                    + ODS_SAVE_DIALOG_3.getContent() + filePath + System.lineSeparator()
                    + System.lineSeparator()
                    + USER_DELETE_CONF_3.getContent();
            int response = JOptionPane.showConfirmDialog(null, msg, OVERWRITE_WARNING_TITLE.getContent(),
                    OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
            if (response == JOptionPane.CANCEL_OPTION) {
                return true;
            }
        }
        return false;
    }

    private static TableModel getSaveModel(JTable tableToSave) {
        final Object[][] data =
                new Object[tableToSave.getModel().getRowCount()][tableToSave.getColumnCount()];

        for (int row = 0; row < tableToSave.getModel().getRowCount(); row ++) {
            int rowM = tableToSave.convertRowIndexToModel(row);

            for (int col = 0; col < tableToSave.getColumnCount(); col++) {
                data[rowM][col] = tableToSave.getValueAt(rowM, col);
            }
        }

        String[] columns = new String[tableToSave.getColumnCount()];
        for (int col = 0; col < tableToSave.getColumnCount(); col++) {
            columns[col] = (String)tableToSave.getColumnModel().getColumn(col).getHeaderValue();
        }

        return new DefaultTableModel(data, columns);
    }
}