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

import static com.osparking.global.Globals.getNumericDigitCount;

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
    public static final int CABW_NORM = 90; // Car Arrival Box Width Normal
    public static final int CABW_WIDE = 120; // Car Arrival Box Width Wide
    public static final int CABH_NORM = 28; // Car Arrival Box Height Normal
        
    static int count = 0;
    
    public static boolean invalidName(String name) {
        if (name.length() <= 1) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean invalidCell(String cell) {
        if (0 < getNumericDigitCount(cell) && getNumericDigitCount(cell) < 10) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean invalidPhone(String phone) {
        if (0 < getNumericDigitCount(phone) && getNumericDigitCount(phone) < 4) {
            return true;
        } else {
            return false;
        }
    }
}
