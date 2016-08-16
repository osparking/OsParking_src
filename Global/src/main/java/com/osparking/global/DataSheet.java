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

import static com.osparking.global.CommonData.ODS_FILE_DIR;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.names.ControlEnums.DialogMessages.OVERWRITE_WARNING_DIALOG;
import static com.osparking.global.names.ControlEnums.DialogMessages.OVERWRITE_WARNING_TITLE;
import static com.osparking.global.names.ControlEnums.DialogMessages.USER_DELETE_CONF_3;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ODS_SAVE_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_1;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_2;
import static com.osparking.global.names.ControlEnums.LabelContent.ODS_SAVE_DIALOG_3;
import com.osparking.global.names.OdsFileOnly;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Open Office spread sheet file (extension 'ods') related routines.
 * @author Open Source Parking Inc.(www.osparking.com)
 */
public class DataSheet {
    
    /**
     * Verify if the file has extension of 'ods'.
     * 
     * @param saveFileChooser
     * @param file 
     */
    public static String verifyOdsExtension(JFileChooser saveFileChooser, File[] file) {
        String ext = "";
        String pathname = null;
        try {
            pathname = file[0].getAbsolutePath();
            String extension = saveFileChooser.getFileFilter().getDescription();
            if (extension.indexOf("*.ods") >= 0) {
                // Finish making ods file name from the name supplied from the user
                int start = pathname.length() - 4;
                // Java doesn't have endsWithIgnoreCase. So, ...
                if (start < 0 || !pathname.substring(start).equalsIgnoreCase(".ods")) {
                    //<editor-fold defaultstate="collapsed" desc="// In case pathname doesn't have ".ods" suffix">
                    // Give it the ".ods" extension, automatically.
                    // pure file name(except extension name) has no ".ods" suffix
                    // So, to make it a ods file, append ".ods" extension to the filename.
                    //</editor-fold>
                    ext = ".ods";
                    pathname += ext;
                    file[0] = new File(pathname);
                } else {
                    // pathname already has ".ods" as its suffix
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(File: " + pathname + ")");
        } finally {
            return ext;
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
    public static void saveODSfile(JFrame aFrame, JTable tableToSave,
            JFileChooser saveFileChooser, String emptyTableMsg)
    {
        // Check the size of the list and if empty just return saying "noting to save"
//        saveFileChooser.setFileFilter(new OdsFileOnly());

        String currDir = saveFileChooser.getCurrentDirectory().getAbsolutePath();
        makeSurePathExists(currDir);
        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String dirPath = saveFileChooser.getCurrentDirectory().getAbsolutePath();
            String filename = saveFileChooser.getSelectedFile().getName();

            filePath += File.separator + filename;
            saveOrNotWithFixedName(saveFileChooser, filePath, tableToSave, dirPath, filename);
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
        // Check the size of the list and if empty just return saying "noting to save"
//        saveFileChooser.setFileFilter(new OdsFileOnly());
        
        String dirPath = System.getProperty("user.home") + File.separator + ODS_FILE_DIR;
        
        // Make sure the 'ods' folder exists in the user home directory.
        makeSurePathExists(dirPath);

        String filePath = dirPath +  File.separator + filename;
        File defFile = new File(filePath);
        saveFileChooser.setSelectedFile(defFile);
        
        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String selPath = saveFileChooser.getSelectedFile().getAbsolutePath();
            String selCore = saveFileChooser.getSelectedFile().getName();
            saveOrNotWithFixedName(saveFileChooser, selPath, tableToSave, dirPath, selCore);
        }
    }

    private static void saveOrNotWithFixedName(JFileChooser saveFileChooser, 
            String filePath, JTable tableToSave, String dirPath, String coreName) 
    {
        File[] file = new File[1];
        String filename = coreName;
        FileFilter filter = saveFileChooser.getFileFilter();
        
        file[0] = saveFileChooser.getSelectedFile();
        if (filter instanceof OdsFileOnly) {
            filePath += ".ods";
            filename = coreName + ".ods";
        } 
        verifyOdsExtension(saveFileChooser, file);

        //<editor-fold desc="-- Determine if to overwrite existing file.">
        if (file[0].exists() && !file[0].isDirectory()) { 
            String msg = OVERWRITE_WARNING_DIALOG.getContent() + System.lineSeparator()
                    + System.lineSeparator()
                    + ODS_SAVE_DIALOG_3.getContent() + filePath + System.lineSeparator()
                    + System.lineSeparator()
                    + USER_DELETE_CONF_3.getContent();
            int response = JOptionPane.showConfirmDialog(null, msg, OVERWRITE_WARNING_TITLE.getContent(),
                    OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); 
            if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        //</editor-fold>
            
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

        TableModel model = new DefaultTableModel(data, columns);
        
        try {
            SpreadSheet.createEmpty(model).saveAs(file[0]);
            //<editor-fold desc="-- Display ods file saving result.">
            String message = ODS_SAVE_DIALOG_1.getContent() + System.lineSeparator() + 
                    System.lineSeparator() +
                    ODS_SAVE_DIALOG_2.getContent() + dirPath + System.lineSeparator() + 
                    ODS_SAVE_DIALOG_3.getContent() + filename + System.lineSeparator();
            JOptionPane.showMessageDialog(saveFileChooser, message, 
                    ODS_SAVE_TITLE.getContent(), JOptionPane.INFORMATION_MESSAGE);
            OOUtils.open(file[0]);
            //</editor-fold>
        } catch (IOException ex) {
            System.out.println("File save exception: " + ex.getMessage());
        }                
    }

    private static void makeSurePathExists(String dirPath) {
        File dirFile = new File(dirPath);
        Boolean result = dirFile.mkdirs();
    }
}