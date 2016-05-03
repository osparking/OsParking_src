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

import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.logParkingException;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Open Source Parking Inc.
 */
public class JDBCMySQL {
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
    public static final String DB_URL = "jdbc:mysql://localhost/parkinglot?useUnicode=true&characterEncoding=utf-8";
    public static final String USER = "gate_1";
    public static final String PASSWORD = "1234";
    private static Logger logException = null;

    private JDBCMySQL() {
        try {
            Class.forName (DRIVER_CLASS);
        } catch (ClassNotFoundException  ex){
            System.exit(-1);
        }
    }
    
    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            logParkingException(Level.SEVERE, e, "failed in creating DB connection ");
            JOptionPane.showMessageDialog(null, "Connection creation Failure for" + System.lineSeparator() 
                    + "user ID: " + USER, "OsParking Failure", JOptionPane.ERROR_MESSAGE);
//            JOptionPane.showConfirmDialog(null, "Connection creation Failure for" + System.lineSeparator() 
//                    + "user ID: " + USER, "OsParking Failure", YES_OPTION, WARNING_MESSAGE);
            System.exit(-1);
        }
        return connection;        
    }
    
    public static Connection getConnection() {
        return (new JDBCMySQL()).createConnection();
    } 
    
    public static String getHashedPW(String passwd){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String pwHashed = null;
        
        try {
            conn = JDBCMySQL.getConnection();
            stmt = conn.prepareStatement("Select md5(?) as pwHashed from dual");
            stmt.setString(1, passwd);
            rs = stmt.executeQuery();
            rs.next();
            pwHashed = rs.getString("pwHashed");   
        } catch (SQLException se) {
            logParkingException(Level.SEVERE, se, "get hashed password using md5");
        } finally {
            closeDBstuff(conn, stmt, rs, "get hashed password using md5");
            return pwHashed;
        }        
    }    
}
