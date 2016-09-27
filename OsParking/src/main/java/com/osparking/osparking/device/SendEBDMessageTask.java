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

import java.util.logging.Level;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.IDevice;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.*;
import com.osparking.osparking.ControlGUI;
import javax.swing.JOptionPane;

/**
 *
 * @author Open Source Parking Inc.
 */
public class SendEBDMessageTask implements Runnable {
    ControlGUI mainGUI;
    byte deviceNo;
    MsgCode msgCode;
    byte[] message;
    private int sendCount = 0;
    int msgSN = 0; // message sequence number

    public SendEBDMessageTask(ControlGUI mainGUI, 
            int deviceNo, MsgCode msgCode, byte[] message, int msgSN) 
    {
        this.mainGUI = mainGUI;
        this.deviceNo = (byte) deviceNo;
        this.msgCode = msgCode;
        this.message = message;
        this.msgSN = msgSN;
    }

    @Override
    public synchronized void run() {
        IDevice.IManager devMan = mainGUI.getDeviceManagers()[E_Board.ordinal()][deviceNo];
        IDevice.ISocket devSock = (IDevice.ISocket) devMan;
        
        try {
            synchronized(mainGUI.getSocketMutex()[E_Board.ordinal()][deviceNo]) 
            {
                if (! isConnected(devSock.getSocket())) 
                {
                    mainGUI.getSocketMutex()[E_Board.ordinal()][deviceNo].wait();
                }
            }
            ++sendCount;
            devMan.writeMessage(msgCode, message);
        } catch (InterruptedException ex) {
            logParkingException(Level.SEVERE, ex, 
                    "E-Board #" + deviceNo + " message sender wait socket conn'");
        }          
    }
    
    /**
     * supplies this open command resent count.
     * used to check the system performance in case of network error/delay.
     * 
     * @return the sendCount
     */
    public int getResendCount() {
        if (sendCount - 1 < 0) {
            JOptionPane.showMessageDialog(null, "negative resend count-EBD");
        }        
        return sendCount - 1;  // first send shouldn't be counted
    }        
}
