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
package com.osparking.deviceglobal;

import com.osparking.global.Globals;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Open Source Parking Inc.
 */


public class DeviceGlobals {
    public static void setIconList(String[] iconFilenames, List<Image> iconList) {
         
        for (String iconPath: iconFilenames) {
            URL iconURL = new Globals().getClass().getResource(iconPath);
            if (iconURL == null) {
                JOptionPane.showMessageDialog(null,
                    "Can't find icon file below." + System.lineSeparator() + "File: " + iconPath, "File Not Found",
                    JOptionPane.ERROR_MESSAGE);                
            } else {
                iconList.add(new ImageIcon(iconURL).getImage());
            }
        }         
    }    
}
