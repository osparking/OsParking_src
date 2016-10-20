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

import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.ControlEnums;
import static com.osparking.global.names.ControlEnums.OsPaLang.KOREAN;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.im.InputContext;
import java.util.logging.Level;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class TableCellEditorKor extends AbstractCellEditor implements TableCellEditor {

    JComponent component = new JTextField();
    ControlEnums.OsPaLang language;

    public TableCellEditorKor(ControlEnums.OsPaLang language, Font font) {
        this.language = language;
        component.setFont(font);
        component.setForeground(Color.BLUE);    
    }

    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int col) 
    {
        if (value == null) {
            ((JTextField)component).setText("");
        } else if (value.getClass() == Integer.class) {
            ((JTextField)component).setText(Integer.toString((Integer)value));
        } else {
            ((JTextField)component).setText((String)value);
        }
      
        try {
            InputContext inCtx  =  table.getInputContext();
        
            if (inCtx != null) {
                Character.Subset[] subset = new Character.Subset[1];

                if (language == KOREAN) {
                    subset[0] = Character.UnicodeBlock.HANGUL_SYLLABLES;
                } else {
                    subset = null;
                }

                inCtx.setCharacterSubsets(subset);
            }
        } catch(Exception e) {
            logParkingException(Level.SEVERE, e, "while setting language ");            
        }
        return component;
    }

    public Object getCellEditorValue() {
        return ((JTextField) component).getText();
    }
}