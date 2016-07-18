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
package com.osparking.global;

import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import com.osparking.global.names.OSP_enums;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/** new Dimension(CommonData.bigButtonWidth, bigButtonHeight)
 *
 * @author Open Source Parking, Inc.
 */
public class CommonData { // new Dimension(carTagWidth, 30)
    /**
     * Combobox selected item index when not selected.
     */
        public static final String ADMIN_ID = "admin";
    public static final int NOT_SELECTED = -1; 
    public static final int NOT_LISTED = -1; 
    public static final int PROMPTER_KEY = 0; 
    
    public static final int carTagWidth = 125; // Normal Width
    public static final int buttonWidthNorm = 90; // Normal Width
    public static final int buttonWidthWide = 110; // Wide Width
    public static final int buttonHeightNorm = 40;
    public static final int buttonHeightShort = 30; 
    public static final int normGUIwidth = 1027; 
    public static final int normGUIheight = 720; 
    public static final int tableRowHeight = 25; 
    public static final int bigButtonWidth = 160;
    public static final int bigButtonHeight = 60;
    public static final Dimension bigButtonDim = 
            new Dimension(CommonData.bigButtonWidth, bigButtonHeight);
    
    public static JLabel metaKeyLabel = new JLabel(META_KEY_LABEL.getContent());  
    public static final Color tipColor = new java.awt.Color(0xff, 0x85, 0x33);
    public static final Color tipColorTrans = new java.awt.Color(0xff, 0x85, 0x33, 127);
    static {
        metaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        metaKeyLabel.setForeground(tipColor);
    }
    public static final Color pointColor = new java.awt.Color(255, 51, 51);
    
    public static final DefaultTableCellRenderer putCellCenter = new DefaultTableCellRenderer();
    static {
        putCellCenter.setHorizontalAlignment(JLabel.CENTER);    
    }

    public static DefaultTableCellRenderer numberCellRenderer = new DefaultTableCellRenderer() {
        Border padding = BorderFactory.createEmptyBorder(0, 15, 0, 15);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) 
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
            setHorizontalAlignment(JLabel.RIGHT);
            return this;            
        }
    };    
}
