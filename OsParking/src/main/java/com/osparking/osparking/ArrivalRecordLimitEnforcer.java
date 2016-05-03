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
package com.osparking.osparking;

import com.osparking.global.Globals;
import static com.osparking.global.Globals.addMessageLine;
import com.osparking.global.names.JDBCMySQL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;
import static com.osparking.global.names.DB_Access.maxMaintainDate;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.logParkingOperation;
import static com.osparking.global.names.ControlEnums.TextType.DELETE_LOG_MSG;
import com.osparking.global.names.OSP_enums;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JTextArea;

/**
 * Deletes old car arrival records (main target is car images) to save hard disck(or SSD) space.
 * Car arrival records that were made more than <code>maxMaintainDate</code> dates ago are removed.
 * <code>maxMaintainDate</code> value is stored in <code>systemsettings</code> table. Default value of
 * <code>maxMaintainDate</code> is 60.
 * (<code>MAX_MAINTAIN_DATE</code> column)
 * @author Open Source Parking Inc.
 */
class ArrivalRecordLimitEnforcer extends TimerTask {
    int numDeletedFolders;
    int numDeletedFiles;
    JTextArea messageArea;
    static int maxMaintainDates = 366;
    ArrivalRecordLimitEnforcer(JTextArea controlGUI) {
        this.messageArea = controlGUI;
    }
    @Override
    public void run() {
        int numRecords = 0;
        Connection conn = null;
        Statement stmt = null;
        
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        //<editor-fold desc="-- Delete parking records older than a year">
        /**
         * Delete parking records older than one year (365 days)
         */
        String sql = "Delete From car_arrival where ((to_days(now()) - to_days(arrivalTime)) >= " 
                + maxMaintainDates + ")";
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            numRecords = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Globals.logParkingException(Level.SEVERE, e, "DB space saving operation failure");
        } finally {
            String message = numRecords + " car arrival records more than " + maxMaintainDate
                    + " days old are deleted";
            closeDBstuff(conn, stmt, null, message);
            if (numRecords > 0)
                logParkingOperation(OSP_enums.OpLogLevel.LogAlways, message);
        }
        //</editor-fold>
        
        //<editor-fold desc="-- Delete login records older than a year">
        /**
         * Delete login records older than one year (365 days)
         */
        sql = "Delete From loginrecord Where  (to_days(now()) - to_days(loginTS)) >= " + maxMaintainDates +
                " or (to_days(now()) - to_days(loginTS)) >= " + maxMaintainDates;
        
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            numRecords = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Globals.logParkingException(Level.SEVERE, e, "Login record deletion failure");
        } finally {
            String message = numRecords + " login records more than " + maxMaintainDate
                    + " days old are deleted";
            closeDBstuff(conn, stmt, null, message);
            if (numRecords > 0)
                logParkingOperation(OSP_enums.OpLogLevel.LogAlways, message);
        }
        //</editor-fold>        
        
        //<editor-fold desc="-- Delete booting records older than a year">
        /**
         * Delete booting records older than one year (365 days)
         */
        sql = "Delete From systemrun Where  (to_days(now()) - to_days(startTm)) >= " + maxMaintainDates +
                " or (to_days(now()) - to_days(stopTm)) >= " + maxMaintainDates;
        
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            numRecords = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Globals.logParkingException(Level.SEVERE, e, "Booting record deletion failure");
        } finally {
            String message = numRecords + " Booting records more than " + maxMaintainDate
                    + " days old are deleted";
            closeDBstuff(conn, stmt, null, message);
            if (numRecords > 0)
                logParkingOperation(OSP_enums.OpLogLevel.LogAlways, message);
        }
        //</editor-fold>        
        
        //<editor-fold desc="-- Update parking records older than limit">
        /**
         * Update parking records by removing images older than a set limit (30 days or so)
         */
        sql = "Update car_arrival Set ImageBlob = null Where ImageBlob IS NOT NULL " 
                + "AND ((to_days(now()) - to_days(arrivalTime)) > " + maxMaintainDate + ")";

        int numImages = 0;
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.createStatement();
            numImages = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Globals.logParkingException(Level.SEVERE, e, "DB space saving operation failure");
        } finally {
            String message = numImages + " car arrival images more than " + maxMaintainDate
                    + " days old are deleted";
            closeDBstuff(conn, stmt, null, message);
            if (numImages > 0)
                logParkingOperation(OSP_enums.OpLogLevel.LogAlways, message);
        }
        //</editor-fold>            
        
        //<editor-fold desc="-- Delete log folders older than set limit">
        /**
         * Delete text log file folders older than a set limit
         */
        numDeletedFolders = 0;
        numDeletedFiles = 0; 
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, maxMaintainDate  * -1);
        Date dateLimitDaysAgo = cal.getTime(); 
        cal.setTime(dateLimitDaysAgo);
        int agoYear = cal.get(Calendar.YEAR);
        int agoMonth = cal.get(Calendar.MONTH);            

        String path = System.getProperty("user.dir") + File.separator + "log" + File.separator;

        File yearFolder = deleteOldLogFolders(new File(path + "exception"), agoYear);
        deleteOldLogFolders(yearFolder, agoMonth);

        yearFolder = deleteOldLogFolders(new File(path + "operation"), agoYear);
        deleteOldLogFolders(yearFolder, agoMonth);
        if (numDeletedFolders != 0 || numDeletedFiles != 0) {
            String logMsg = DELETE_LOG_MSG.getContent() + numDeletedFolders + 
                    DELETE_LOG_MSG.getContent() + numDeletedFiles;
            logParkingOperation(OSP_enums.OpLogLevel.LogAlways, "Log deleted: " +  logMsg);        
            addMessageLine(messageArea, logMsg);
        }
            
        //</editor-fold>            
    }

    private File deleteOldLogFolders(File fileOrFolder, int agoTimeUnitValue) {
        File agoFolder = null;
        if (fileOrFolder != null && fileOrFolder.isDirectory()) {
            File[] fList = fileOrFolder.listFiles();
            for (File oneFolder : fList) {
                if (oneFolder.isDirectory()) {
                    if (Integer.parseInt(oneFolder.getName()) < agoTimeUnitValue) {
                        deleteWithForce(oneFolder);
                    }
                    else {
                        if (Integer.parseInt(oneFolder.getName()) == agoTimeUnitValue)
                            agoFolder = oneFolder;
                    }
                }
            }
        }
        return agoFolder;
    }    

    /**
     * Deletes an item (file or folder either empty or not) in the file system 
     * @param oneItem 
     */
    private void deleteWithForce(File oneItem) {
        if (oneItem.isDirectory()) {
            File[] files = oneItem.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    deleteWithForce(aFile);
                }
            }
            deleteItemWithLogging(oneItem);
        } else {
            deleteItemWithLogging(oneItem);
        }        
    }

    private void deleteItemWithLogging(File oneItem) {
        String itemPath = oneItem.getPath();
        if (oneItem.isDirectory())
            numDeletedFolders++;
        else 
            numDeletedFiles++;
        oneItem.delete();
        logParkingOperation(OSP_enums.OpLogLevel.LogAlways, "Log deleted: " +  itemPath);        
    }
}
