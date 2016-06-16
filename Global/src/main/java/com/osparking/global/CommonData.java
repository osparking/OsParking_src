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
import java.awt.Color;
import javax.swing.JLabel;

/**
 *
 * @author Open Source Parking, Inc.
 */
public class CommonData {
    public static final int buttonWidthNorm = 90; // Normal Width
    public static final int buttonWidthWide = 110; // Wide Width
    public static final int buttonHeightNorm = 40;
    public static final int buttonHeightShort = 30;
    public static JLabel metaKeyLabel = new JLabel(META_KEY_LABEL.getContent());  
    public static final Color tipColor = new java.awt.Color(0xff, 0x85, 0x33);
    static {
        metaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        metaKeyLabel.setForeground(tipColor);
    }
    public static final Color pointColor = new java.awt.Color(255, 51, 51);
}
