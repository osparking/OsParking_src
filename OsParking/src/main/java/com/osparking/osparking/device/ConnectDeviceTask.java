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
package com.osparking.osparking.device;

import com.osparking.global.names.IDevice;
import java.awt.Font;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.PULSE_PERIOD;
import static com.osparking.global.Globals.gateDeviceTypes;
import static com.osparking.global.Globals.getGateDevicePortNo;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.OSP_enums.DeviceType;
import com.osparking.osparking.ControlGUI;
import static com.osparking.global.names.DB_Access.deviceIP;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.OSP_enums.ConnectionType.TCP_IP;
import com.osparking.global.names.IDevice.ISocket;
import com.osparking.global.names.OSP_enums;
import com.osparking.osparking.device.BlackFly.BlackFlyManager;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * 
 * @author Open Source Parking Inc.
 */
public class ConnectDeviceTask implements Runnable {
    ControlGUI managerGUI;
    DeviceType deviceType;
    byte deviceID = 0;
    int seq = 0;
    
    public ConnectDeviceTask(ControlGUI managerGUI, DeviceType deviceType, byte deviceID) {
        this.managerGUI = managerGUI;
        this.deviceType = deviceType;
        this.deviceID = deviceID;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                //<editor-fold desc="-- establish socket connection">
                int portNo = getGateDevicePortNo(deviceType, deviceID);
                
                if (DEBUG && (seq == 0 || seq % 20 == 1)) {
                    managerGUI.getStatusTextField().setText(
                            "Socket() IP: " + deviceIP[deviceType.ordinal()][deviceID] + ", port: " + 
                                    portNo + " (" + seq + "-th)");      
                }
                if (managerGUI.isSHUT_DOWN()) {
                    break;
                }
                synchronized (managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID]) 
                {
                    IDevice.IManager manager = managerGUI.getDeviceManagers()[deviceType.ordinal()][deviceID];
                    
                    boolean isBFcamConnected = true;
                    if (connectionType[deviceType.ordinal()][deviceID] == TCP_IP.ordinal()) 
                    {
                        if (deviceType == DeviceType.Camera &&
                                gateDeviceTypes[deviceID].cameraType == OSP_enums.CameraType.Blackfly) 
                        {
                            ; // do nothing- connection re-establishment is done by LED_Task
                        } else {
                            //<editor-fold desc="-- Request socket connection">
                            Socket deviceSocket = new Socket();
                            deviceSocket.connect(new InetSocketAddress(deviceIP[deviceType.ordinal()][deviceID], portNo),
                                    PULSE_PERIOD);

                            deviceSocket.setTcpNoDelay(true);
                            deviceSocket.setSoTimeout(PULSE_PERIOD);
                            ((ISocket)manager).setSocket(deviceSocket);
                            //</editor-fold>
                        }
                    } else { // serial port 
                        //<editor-fold desc="-- Open serial port">
                        IDevice.ISerial serialMan = (IDevice.ISerial)manager;
                        
                        if (serialMan.getPortIdentifier().isCurrentlyOwned()) {
                            JOptionPane.showMessageDialog(managerGUI, "Gate #" + deviceID +" " 
                                    + deviceType + " serial port is currently OWNed");
                        } else {
                            serialMan.setCommPort(serialMan.getPortIdentifier().open(this.getClass().getName(), 1000));
                            if (serialMan.getCommPort() instanceof SerialPort) {
                                SerialPort serialPort = (SerialPort) serialMan.getCommPort();
                                serialPort.setSerialPortParams(serialMan.getBaudRate(), // 통신속도
                                                SerialPort.DATABITS_8,                   // 데이터 비트
                                                SerialPort.STOPBITS_1,                    // stop 비트
                                                SerialPort.PARITY_NONE);                // 패리티
                                if (manager == null) {
                                    logParkingException(Level.INFO, null, "null manager", deviceID);
                                } else {
                                    serialMan.setSerialPort(serialPort);
                                }
                            } else {
                                logParkingException(Level.INFO, null, "Only serial ports are handled", deviceID);
                            }
                        }
                        //</editor-fold>
                    }
                    managerGUI.getSockConnStat()[deviceType.ordinal()][deviceID].recordSocketConnection(
                            System.currentTimeMillis());
                    managerGUI.getStatusTextField().setFont(new Font(
                            managerGUI.getStatusTextField().getFont().getFontName(), Font.PLAIN, 
                            managerGUI.getStatusTextField().getFont().getSize()));  

                    boolean isCamera = deviceType == DeviceType.Camera;
                    boolean isBlkFlyCam = gateDeviceTypes[deviceID].cameraType == OSP_enums.CameraType.Blackfly;
                    if (!isCamera || !isBlkFlyCam)
                    {
                        managerGUI.tolerance[deviceType.ordinal()][deviceID].assignMAX();  
                        managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID].notifyAll();                        
                    }
//                    System.out.println("after notify all");
                }
                //</editor-fold>
                return;
            } catch (SocketTimeoutException ex) {
            } catch (IOException e) {
                //<editor-fold desc="--handle ioexception">
                if (e.getMessage().indexOf("refused") >= 0) {
                    String msg = deviceType + " #"  + deviceID + " refused connection: " + (++seq) + " times";

                    managerGUI.getStatusTextField().setText(msg); 
                    if (seq % 20 == 1) {
                        logParkingException(Level.INFO, null, msg + System.lineSeparator(), deviceID);
                    }
                    managerGUI.getStatusTextField().setText(msg);

                } else {
                    String tip = "";
                    if (e.getMessage().indexOf("timed out") >= 0) {
                    } else {
                        logParkingException(Level.SEVERE, e, "IOEx during socket connection", deviceID);
                    }
                }
                //</editor-fold>
            } catch (PortInUseException ex) {
                logParkingException(Level.SEVERE, ex, "IOEx getting serial port", deviceID);
            } catch (UnsupportedCommOperationException ex) {
                logParkingException(Level.SEVERE, ex, "IOEx getting serial port", deviceID);
            }
        }
    }
}
