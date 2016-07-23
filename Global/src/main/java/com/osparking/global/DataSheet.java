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

import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.OdsFileOnly;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
    
    /**
     * Verify if the file has extension of 'ods'.
     * 
     * @param saveFileChooser
     * @param file 
     */
    public static void verifyOdsExtension(JFileChooser saveFileChooser, File[] file) {
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
                    pathname += ".ods";
                    file[0] = new File(pathname);
                } else {
                    // pathname already has ".ods" as its suffix
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(File: " + pathname + ")");
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
        saveFileChooser.setFileFilter(new OdsFileOnly());

        int returnVal = saveFileChooser.showSaveDialog(aFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] file = new File[1];
            
            file[0] = saveFileChooser.getSelectedFile();
            verifyOdsExtension(saveFileChooser, file);            
            
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
                OOUtils.open(file[0]);
            } catch (IOException ex) {
                System.out.println("File save exception: " + ex.getMessage());
            }                
        }        
    }
}