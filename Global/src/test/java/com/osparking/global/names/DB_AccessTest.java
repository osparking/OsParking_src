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

import static com.osparking.global.names.OSP_enums.EBD_CycleType.EBD_FLOW_CYCLE;
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
    }

    /**
     * Test of passwordMatched method, of class DB_Access.
     */
    @Test
    public void testPasswordMatched() {
        System.out.println("passwordMatched");
        String userID = "admin";
        String passwd = "1234";
        boolean expResult = true;
        boolean result = DB_Access.passwordMatched(userID, passwd);
        assertEquals(expResult, result);
    }

    /**
     * Test of readSettings method, of class DB_Access.
     */
    @Test
    public void testReadSettings() {
        System.out.println("readSettings");
        DB_Access.readSettings();
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
    }

    /**
     * Test of enteranceAllowed method, of class DB_Access.
     */
    @Test
    public void testEnteranceAllowed() {
        System.out.println("enteranceAllowed");
        String tagRecognized = "26누8648";
        StringBuffer tagEnteredAs = new StringBuffer();
        StringBuffer remark = null;
        OSP_enums.PermissionType expResult = OSP_enums.PermissionType.ALLOWED;
        OSP_enums.PermissionType result = DB_Access.enteranceAllowed(tagRecognized, tagEnteredAs, remark);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCycleFromDB method, of class DB_Access.
     */
    @Test
    public void testGetCycleFromDB() {
        System.out.println("getCycleFromDB");
        OSP_enums.EBD_CycleType cycleType = EBD_FLOW_CYCLE;
        int expResult = 8000;
        int result = DB_Access.getCycleFromDB(cycleType);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRecordCount method, of class DB_Access.
     */
    @Test
    public void testGetRecordCount_String_int() {
        System.out.println("getRecordCount");
        String tableName = "cardriver";
        int CD_SEQ_NO = -1;
        int expResult = 1000;
        int result = DB_Access.getRecordCount(tableName, CD_SEQ_NO);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRecordCount method, of class DB_Access.
     */
    @Test
    public void testGetRecordCount_3args() {
        System.out.println("getRecordCount");
        String tableName = "users_osp";
        String columnName = "id";
        String columnValue = "admin";
        int expResult = 1;
        int result = DB_Access.getRecordCount(tableName, columnName, columnValue);
        assertEquals(expResult, result);
    }

    /**
     * Test of insertOneVehicle method, of class DB_Access.
     */
    @Test
    public void testInsertOneVehicle() {
        System.out.println("insertOneVehicle");
        String plateNo = "12거3456";
        int seqNo = 541;
        int notification = 0;
        int wholeTag = 0;
        int parkPermit = 0;
        String reasonTxt = "";
        String otherTxt = "";
        int expResult = 0;
        int result = DB_Access.insertOneVehicle(plateNo, seqNo, notification, wholeTag, parkPermit, reasonTxt, otherTxt);
        assertEquals(expResult, result);
    }
    
}
