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
package com.osparking.global.names;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class DB_AccessTest {
    
    public DB_AccessTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of makeSureBasicUserExistance method, of class DB_Access.
     */
    @Test
    public void testMakeSureBasicUserExistance() {
        System.out.println("makeSureBasicUserExistance");
        DB_Access.makeSureBasicUserExistance();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of passwordMatched method, of class DB_Access.
     */
    @Test
    public void testPasswordMatched() {
        System.out.println("passwordMatched");
        String userID = "";
        String passwd = "";
        boolean expResult = false;
        boolean result = DB_Access.passwordMatched(userID, passwd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of recordPerformance method, of class DB_Access.
     */
    @Test
    public void testRecordPerformance() {
        System.out.println("recordPerformance");
        int gateID = 0;
        long miliSeconds = 0L;
        String expResult = "";
        String result = DB_Access.recordPerformance(gateID, miliSeconds);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readSettings method, of class DB_Access.
     */
    @Test
    public void testReadSettings() {
        System.out.println("readSettings");
        DB_Access.readSettings();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readEBoardSettings method, of class DB_Access.
     */
    @Test
    public void testReadEBoardSettings() {
        System.out.println("readEBoardSettings");
        EBD_DisplaySetting[] expResult = null;
        EBD_DisplaySetting[] result = DB_Access.readEBoardSettings();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readEBoardUsageSettings method, of class DB_Access.
     */
    @Test
    public void testReadEBoardUsageSettings() {
        System.out.println("readEBoardUsageSettings");
        OSP_enums.EBD_DisplayUsage usageRow = null;
        EBD_DisplaySetting expResult = null;
        EBD_DisplaySetting result = DB_Access.readEBoardUsageSettings(usageRow);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parkingPermitted method, of class DB_Access.
     */
    @Test
    public void testParkingPermitted() {
        System.out.println("parkingPermitted");
//        String tagEnteredAs = "26누8648"; 
        String tagEnteredAs = "27자7691"; 
        boolean expResult = false;
        boolean result = DB_Access.parkingPermitted(tagEnteredAs);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of enteranceAllowed method, of class DB_Access.
     */
    @Test
    public void testEnteranceAllowed() {
        System.out.println("enteranceAllowed");
        String tagRecognized = "";
        StringBuffer tagEnteredAs = null;
        StringBuffer remark = null;
        OSP_enums.PermissionType expResult = null;
        OSP_enums.PermissionType result = DB_Access.enteranceAllowed(tagRecognized, tagEnteredAs, remark);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCycleFromDB method, of class DB_Access.
     */
    @Test
    public void testGetCycleFromDB() {
        System.out.println("getCycleFromDB");
        OSP_enums.EBD_CycleType cycleType = null;
        int expResult = 0;
        int result = DB_Access.getCycleFromDB(cycleType);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecordCount method, of class DB_Access.
     */
    @Test
    public void testGetRecordCount_String_int() {
        System.out.println("getRecordCount");
        String tableName = "";
        int CD_SEQ_NO = 0;
        int expResult = 0;
        int result = DB_Access.getRecordCount(tableName, CD_SEQ_NO);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecordCount method, of class DB_Access.
     */
    @Test
    public void testGetRecordCount_3args() {
        System.out.println("getRecordCount");
        String tableName = "";
        String columnName = "";
        String columnValue = "";
        int expResult = 0;
        int result = DB_Access.getRecordCount(tableName, columnName, columnValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insertOneVehicle method, of class DB_Access.
     */
    @Test
    public void testInsertOneVehicle() {
        System.out.println("insertOneVehicle");
        String plateNo = "";
        int seqNo = 0;
        int notification = 0;
        int wholeTag = 0;
        int parkPermit = 0;
        String reasonTxt = "";
        String otherTxt = "";
        int expResult = 0;
        int result = DB_Access.insertOneVehicle(plateNo, seqNo, notification, wholeTag, parkPermit, reasonTxt, otherTxt);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
