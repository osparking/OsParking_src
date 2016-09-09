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
import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.noArtificialErrorInserted;
import static com.osparking.global.names.ControlEnums.DialogMessages.DEV_TYPE_ERROR_MSG1;
import static com.osparking.global.names.ControlEnums.DialogMessages.DEV_TYPE_ERROR_MSG2;
import static com.osparking.global.names.ControlEnums.DialogMessages.DEV_TYPE_ERROR_MSG3;
import static com.osparking.global.names.ControlEnums.DialogMessages.DEV_TYPE_ERROR_MSG4;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.CAMERA_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.TYPE_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.SETTING_MENU_ITEM;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.SYSTEM_MENU;
import static com.osparking.global.names.OSP_enums.MsgCode.IAmHere;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTextField;
import org.jsoup.Jsoup;

/**
 *
 * @author Open Source Parking Inc.
 */


public class DeviceGlobals {
    
    public static void showCheckDeviceTypeDialog(String device, int ebdID, int msgCode) {
        JOptionPane.showConfirmDialog(null, 
                DEV_TYPE_ERROR_MSG1.getContent() + System.lineSeparator() 
                        + System.lineSeparator() + 
                        (DEV_TYPE_ERROR_MSG2.getContent() + msgCode) + 
                        System.lineSeparator() + System.lineSeparator() +
                        GATE_LABEL.getContent() + ebdID + " " +
                        device + " " + TYPE_LABEL.getContent() + 
                        DEV_TYPE_ERROR_MSG3.getContent() + System.lineSeparator() +
                        DEV_TYPE_ERROR_MSG4.getContent() + 
                        Jsoup.parse(SYSTEM_MENU.getContent()).text() + " > " +
                        SETTING_MENU_ITEM.getContent() + " > " + 
                        GATE_LABEL.getContent() + ebdID,
                device + ebdID +" " + ERROR_DIALOGTITLE.getContent(), 
                JOptionPane.PLAIN_MESSAGE, 
                WARNING_MESSAGE);        
    }
    
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
    
    public static void displayErrorRate(JTextField displayField, float rate) {
        displayField.setText("Artificial error rate : " + getFormattedRealNumber(rate, 2));
    }    

    static Toolkit toolkit = null;
    
    public static void displayRateLimit(JTextField displayField, float rate, boolean isMax) {
        if (toolkit == null) {
            toolkit = Toolkit.getDefaultToolkit();
        }
        toolkit.beep();
        displayField.setText("Current error rate(=" + getFormattedRealNumber(rate, 2)
                + ") is " + (isMax ? "max!" : "min!"));
    } 
    
    public static void sayIamHere(DeviceGUI deviceGUI) {
        if (noArtificialErrorInserted(deviceGUI.getErrorCheckBox())) 
        {
            try {
                deviceGUI.getReader().getManagerSocket().getOutputStream().write(IAmHere.ordinal());
            } catch (IOException ex) {
                deviceGUI.getReader().disconnectSocket(ex,  "while saying I'am here.");                 
            }
            deviceGUI.getTolerance().assignMAX();
        }
    }    
}
