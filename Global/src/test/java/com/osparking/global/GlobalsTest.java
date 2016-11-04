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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class GlobalsTest {
    
    public GlobalsTest() {
    }
    
    /**
     * Test of getFormattedRealNumber method, of class Globals.
     */
    @Test
    public void testGetPercentString() {
        System.out.println("getFormattedRealNumber");
        double realNumber = 0.01;
        String expResult = "1%";
        String result = Globals.getPercentString(realNumber);
        assertEquals(expResult, result);
        
        realNumber = 0.21;
        expResult = "21%";
        result = Globals.getPercentString(realNumber);
        assertEquals(expResult, result);
        
        realNumber = 0.91;
        expResult = "91%";
        result = Globals.getPercentString(realNumber);
        assertEquals(expResult, result);
    }   
}
