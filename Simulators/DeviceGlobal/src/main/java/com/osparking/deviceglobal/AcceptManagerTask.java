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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.DB_Access.devicePort;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;

/**
 * Gate Bar ServerSocket Listener thread.
 * 
 * Accepts each gate bar socket connection request and creates a manager thread for the socket.
 * @author Open Source Parking Inc.
 */
public class AcceptManagerTask implements Runnable {
//public class AcceptManagerTask extends Thread {
    DeviceGUI deviceGUI = null;
    DeviceType devType;
    
    /**
     * manager threads for gate bars. Manager index is the same as the bar ID number.
     * (index 0 is unused)
     */
    ServerSocket serverSocket = null;
    
    /**
     * text file used to store open command IDs acked from the gate bar.
     */
    
    Socket managerSocket = null;   
    boolean holdOperation = false;
    
    /**
     * drives gate bar socket connection status label blinker module.
     */
    Timer LED_Timer = null;
    
    public AcceptManagerTask(DeviceGUI deviceGUI, DeviceType devType) {
        this.deviceGUI = deviceGUI;
        this.devType = devType;

        // create server socket by passing either a development or a test-run version port number
        try {
            serverSocket = new ServerSocket(getGateDevicePortNo(devType, deviceGUI.getID()));
        } catch (IOException e) {
            logParkingExceptionStatus(Level.INFO, null, "serverSocket creation error",
                    deviceGUI.getCriticalInfoTextField(), 0);
        }  
        
        /** 
         * Start socket connection status display timer and schedule it.
         */
        LED_Timer = new Timer(devType + Integer.toString(deviceGUI.getID()) + "_LEDtimer", true);
        LED_Timer.schedule(new LED_Task(deviceGUI, devType), 0, LED_PERIOD);            
    }
    
    /**
     * Accepts camera connection request and starts connection status LED display. ??
     * It periodically wakes up and checks if some camera is disconnected. ??
     * If yes, then it listens for a camera request for a conenction. ??
     */
    public void run() {
        deviceGUI.getCriticalInfoTextField().setText("waiting for manager to request socket connection");
        System.out.println(devType + "#" + deviceGUI.getID() + " waits manager to connect");

        try {
            while (true) 
            {
                //<editor-fold defaultstate="collapsed" desc="-- Accepts connection request from a device">
                try {
                    if (isConnected(deviceGUI.getReader().getManagerSocket()))
                        return;
                    
                    managerSocket = serverSocket.accept();
                    
                    deviceGUI.getTolerance().assignMAX();
                    if (DEBUG) {
                        System.out.println(devType + "#" + deviceGUI.getID() + " accepted manager"); 
                    }
                    // created device socket is stored in a device manager array that is updated by the DeviceManager
                    // instance (specifically, by its socket reader)
                    managerSocket.setSoTimeout(PULSE_PERIOD);
                    managerSocket.setTcpNoDelay(true);

                    synchronized(deviceGUI.getSocketMUTEX()) {
                        String display = "manager connected";
                        if (devType == Camera) {
                            deviceGUI.getCriticalInfoTextField().setText("");
                            addMessageLine(deviceGUI.getMessageTextArea(), display);
                        } else {
                            deviceGUI.getCriticalInfoTextField().setText(timeFormat.format(new Date()) + "--" + display);
                        }

                        deviceGUI.getReader().setManagerSocket(managerSocket);
                        deviceGUI.getSocketMUTEX().notifyAll(); // for camera: reader and image transmetter waits
                    }                      
                    //managerSocket = null;
                    if (holdOperation) 
                        break;
                } catch (SocketTimeoutException e) {
                    // gate bar is not connecting, proceed to the beginning of the loop body
                } catch (IOException e) {
                    if (!deviceGUI.isSHUT_DOWN()) {
                        logParkingExceptionStatus(Level.INFO, e, devType + " Socket creation error",
                                deviceGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
                    }else{
                        
                        System.out.println("device Closed");
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("excep");
                }
                //</editor-fold>  
            }
        } catch (Exception e) {
            logParkingExceptionStatus(Level.SEVERE, e, "SS listener finishing" + devType,
                    deviceGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
        }
    }
    
    /**
     * Closes device sockets and stop LED blinking.
     */
    public void terminate() {

        try {
            if (deviceGUI.getReader() != null && deviceGUI.getReader().getManagerSocket()!= null)
                deviceGUI.getReader().getManagerSocket().close();
        } catch (IOException ioe) {
            logParkingExceptionStatus(Level.SEVERE, ioe, "camera socket close error",
                    deviceGUI.getCriticalInfoTextField(), 0);
        }
        
        for (int i = 1; i <= gateCount; i++) 
            deviceGUI.getConnectionLED().setForeground(new Color( 1.0f, 0.0f, 0.0f, 1.0f));
        
        LED_Timer.cancel();
        LED_Timer.purge();
        
        holdOperation = true;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {}
        }
    }
}
