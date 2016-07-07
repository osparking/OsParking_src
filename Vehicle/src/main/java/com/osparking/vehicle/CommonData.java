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

import com.osparking.global.names.OSP_enums;
import static com.osparking.vehicle.driver.ManageDrivers.loadComboBoxItems;
import javax.swing.JComboBox;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class CommonData {
    /**
     * Vehicle table column width weight(=ratio) constants.
     */
    public static final int vRowNoWidth = 60; // row number(sequence number)
    public static final int vPlateNoWidth = 70; // 
    public static final int vDriverNmWidth = 65; // 
    public static final int vAffiliWidth = 100; // 
    public static final int vBuildingWidth = 75; // 
    public static final int vOtherWidth = 60; // 
    public static final int vCauseWidth = 80; // 
    
    public static final int DTC_MARGIN = 5; // Driver Table Column Margin
    public static final int DTCW_MAX = 3; // Driver Table Column Margin
    public static final int DTCW_RN = 80;
    public static final int DTCW_DN = 100;
    public static final int DTCW_CP = 130;
    public static final int DTCW_LL = 120;
    public static final int DTCW_L1 = 110;
    public static final int DTCW_L2 = 110;
    public static final int DTCW_BN = 110;
    public static final int DTCW_UN = 110;
    
    public static int refreshComboBox(JComboBox comboBox, 
            Object prompter, OSP_enums.DriverCol column, int parentkey) 
    {
        comboBox.removeAllItems();
        comboBox.addItem(prompter);
        loadComboBoxItems(comboBox, column, parentkey);
        
        return parentkey;
    }
}
