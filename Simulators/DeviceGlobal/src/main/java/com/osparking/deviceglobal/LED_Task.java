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

import java.awt.Color;
import java.net.Socket;
import java.util.TimerTask;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.isConnected;
import com.osparking.global.names.OSP_enums.DeviceType;

/**
 * Display the camera connection status with the Manager program
 * @author Open Source Parking Inc.
 */
public class LED_Task extends TimerTask {

    DeviceGUI deviceGUI = null;
    DeviceType deviceType;
    /**
     * used to give blinking effect to the text label of camera socket connection status 
     */
    boolean setHalfTransparent = false;

    /**
     * initializes this task with the main frame and a socket array
     * @param deviceMain connection status is to be displayed bottom left corner on this frame
     */
    public LED_Task(DeviceGUI deviceMain, DeviceType deviceType) {
        this.deviceGUI = deviceMain;
        this.deviceType = deviceType;
    }
    
    /**
     * Periodically displays the connection status of each hardware component on 
     * each gate. 
     * One row of LED labels represents components for a gate.
     * To give Las Vegas sign effect, rotates transparency degree between adjacent rows.
     */
    public void run() {
        Socket connSockLED = null;
        DeviceReader reader = deviceGUI.getReader();
        if (reader != null) {
            connSockLED = reader.getManagerSocket();
        }
        
        if (setHalfTransparent) {
            // decrease alpha value of LED label
            if (isConnected(connSockLED)) {
                deviceGUI.getTolerance().decrease();   
                deviceGUI.getConnectionLED().setText("O");
                deviceGUI.getConnectionLED().setForeground(new Color( 0.0f, 1.0f, 0.0f, 0.5f));
            } else {
                deviceGUI.getConnectionLED().setText("X");
                deviceGUI.getConnectionLED().setForeground(new Color( 1.0f, 0.0f, 0.0f, 0.5f));
            }
        } else {
            if (isConnected(connSockLED)) {
                deviceGUI.getTolerance().decrease();   
                deviceGUI.getConnectionLED().setText("O");
                deviceGUI.getConnectionLED().setForeground(new Color( 0.0f, 1.0f, 0.0f, 1.0f));
            } else {
                deviceGUI.getConnectionLED().setText("X");
                deviceGUI.getConnectionLED().setForeground(new Color( 1.0f, 0.0f, 0.0f, 1.0f));
            }
        }
        setHalfTransparent  =  ! setHalfTransparent;
    }
}