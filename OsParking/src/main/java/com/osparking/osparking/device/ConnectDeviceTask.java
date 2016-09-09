/* 
 * Copyright (C) 2015, 2016  Open Source Parking, Inc.(www.osparking.com)
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
import java.util.logging.Level;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.PULSE_PERIOD;
import static com.osparking.global.Globals.gateDeviceTypes;
import static com.osparking.global.Globals.getGateDevicePortNo;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.timeFormat;
import static com.osparking.global.names.ControlEnums.DialogMessages.CONN_REFUSED;
import static com.osparking.global.names.ControlEnums.DialogMessages.CONN_REFUSED_1;
import static com.osparking.global.names.ControlEnums.DialogMessages.TIMED_OUT;
import static com.osparking.global.names.ControlEnums.LabelContent.PORT_LABEL;
import com.osparking.global.names.OSP_enums.DeviceType;
import com.osparking.osparking.ControlGUI;
import static com.osparking.global.names.DB_Access.deviceIP;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.OSP_enums.ConnectionType.TCP_IP;
import com.osparking.global.names.IDevice.ISocket;
import com.osparking.global.names.OSP_enums;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.util.Date;
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
                    String msg = "D "+ deviceType.getContent() + " #"  + deviceID +
                            ", IP: " + deviceIP[deviceType.ordinal()][deviceID] + ", " + 
                                    PORT_LABEL.getContent() + ": " + 
                                    portNo + " (" + seq + "-th)";
                    managerGUI.displayStatus(msg);
//                    managerGUI.getStatusTextField().setText(
//                            "D "+ deviceType.getContent() + " #"  + deviceID +
//                            ", IP: " + deviceIP[deviceType.ordinal()][deviceID] + ", " + 
//                                    PORT_LABEL.getContent() + ": " + 
//                                    portNo + " (" + seq + "-th)");      
                }
                if (managerGUI.isSHUT_DOWN()) {
                    break;
                }
                synchronized (managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID]) 
                {
                    IDevice.IManager manager = managerGUI.getDeviceManagers()[deviceType.ordinal()][deviceID];
                    boolean isCamera = (deviceType == DeviceType.Camera);
                    boolean isBlkFlyCam = gateDeviceTypes[deviceID].cameraType == OSP_enums.CameraType.Blackfly;
                    
                    if (connectionType[deviceType.ordinal()][deviceID] == TCP_IP.ordinal()) 
                    {
                        if (isCamera && isBlkFlyCam) 
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
                        
                        CommPortIdentifier portID = serialMan.getPortIdentifier();
                        if (portID == null) {
                            continue;
                        }
                        if (portID.isCurrentlyOwned()) {
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

                    if (!isCamera || !isBlkFlyCam)
                    {
                        managerGUI.tolerance[deviceType.ordinal()][deviceID].assignMAX();  
                        managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID].notifyAll();                        
                    }
                }
                //</editor-fold>
                return;
            } catch (IOException e) {
                //<editor-fold desc="--handle ioexception">
                if (e.getMessage().contains( "refused")) {
                    String msg = timeFormat.format(new Date()) + "-- " + deviceType.getContent() +
                            " #"  + deviceID + CONN_REFUSED.getContent() + 
                            (++seq) + CONN_REFUSED_1.getContent();

                    managerGUI.displayStatus(msg);
                    if (seq % 20 == 1) {
                        logParkingException(Level.INFO, null, msg + System.lineSeparator(), deviceID);
                    }
                } else {
                    if (e.getMessage().contains("timed out")) {
                        String msg = timeFormat.format(new Date()) + "-- " + deviceType.getContent() +
                                " #"  + deviceID + TIMED_OUT.getContent() + 
                                (++seq) + CONN_REFUSED_1.getContent();
                        managerGUI.displayStatus(msg);
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
