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
package com.osparking.global.names;

import com.osparking.global.Globals;
import static com.osparking.global.Globals.gateDeviceTypes;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.OSP_enums.CameraType.Blackfly;
import static com.osparking.global.names.OSP_enums.ConnectionType.RS_232;
import com.osparking.global.names.OSP_enums.DeviceType;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.net.Socket;

/**
 *
 * @author Open Source Parking Inc.
 */
public class IDevice {
    
    public static interface IManager {
//        void finishConnection(Exception e, String description, byte deviceNo);
        void setPriority(int newPriority);
        void start();
        public void stopOperation(String reason);
        boolean isNeverConnected();

        public boolean isConnected();
    }
    
    public static interface IE_Board {
        public void showDefaultMessage();
    }
    
    public static interface ISocket {
        public void setSocket(Socket s);
        Socket getSocket();
    }
    
    public static interface ISerial {
        public void setSerialPort(SerialPort serialPort);
        public SerialPort getSerialPort();    
        public CommPortIdentifier getPortIdentifier();
        public CommPort getCommPort();
        public void setCommPort(CommPort open);
        public int getBaudRate();
    }   
    
    public static interface MessageQueue {
        public String getAckStatistics();
    }    

    /**
     * Check if a device manager is connected to the device.
     * The manager program processes a given device(of a type) at a gate(with a number).
     * 
     * @param manager Device manager program.
     * @param devType Type of the device handled by the device.
     * @param gateNo ID number of the gate where device is located.
     * @return true if device is connected, false otherwise.
     */
    public static boolean isConnected(IManager manager, DeviceType devType, byte gateNo) 
    {
        if (manager == null) 
            return false;
        
        if (connectionType[devType.ordinal()][gateNo] == RS_232.ordinal())
        {
            if (((ISerial)manager).getSerialPort() == null)
                return false;
            else 
                return true;
        } else {
            if (devType == DeviceType.Camera) {
                if (gateDeviceTypes[gateNo].cameraType == Blackfly) {
                    return manager.isConnected();
                }
            }
            return Globals.isConnected(((ISocket)manager).getSocket());
        }
    }
}
