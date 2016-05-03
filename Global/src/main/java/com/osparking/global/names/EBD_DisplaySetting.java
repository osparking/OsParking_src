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
package com.osparking.global.names;

import com.osparking.global.names.OSP_enums.EBD_Colors;
import com.osparking.global.names.OSP_enums.EBD_ContentType;
import com.osparking.global.names.OSP_enums.EBD_Fonts;
import com.osparking.global.names.OSP_enums.EBD_Effects;

/**
 *
 * @author Open Source Parking Inc.
 */
public class EBD_DisplaySetting {
    public final static int MIN_FREQ = 32;      // minimum display refresh count per second for video effect
    public final static int MAX_PERIOD = 1000 / MIN_FREQ;      //electric board display period (unit : ms)     

    /**
     * calculated after displayCycle is entered while making text flow DELTA value integral.
     * since display pixed can not be divided and display margin should be a whole number.
     * this value should be 32 or less so long as MIN_FREQ is 32 as 1000/32 is roughly 32.
     * 
     */
    public static int EBD_PERIOD = MAX_PERIOD; 
    
    public String verbatimContent = null; // textual message to be displayed word by word
    public EBD_ContentType contentType; 
    public EBD_Effects displayPattern; // effect applied in displaying the content
    public EBD_Colors textColor;
    public EBD_Fonts textFont;
    
    public int displayCycle; // text blinking or (E-Board width long) text circulation time in milisecond (eg, 500, 8000)
            // cycleTime, blinkingTime, blinkTime
    
    public EBD_DisplaySetting(String verbatimContent, EBD_ContentType contentType, EBD_Effects displayPattern,
            EBD_Colors textColor, EBD_Fonts textFont, int displayCycle) 
    {
        this.verbatimContent = verbatimContent;            
        this.contentType = contentType;
        this.displayPattern = displayPattern;
        this.textColor = textColor;
        this.textFont = textFont;
        this.displayCycle = displayCycle;
    }
}
