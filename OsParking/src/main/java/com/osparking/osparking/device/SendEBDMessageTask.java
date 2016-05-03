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

import static com.osparking.global.Globals.gfinishConnection;
import java.io.IOException;
import java.util.logging.Level;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.IDevice;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.osparking.ControlGUI;
import javax.swing.JOptionPane;

/**
 *
 * @author Open Source Parking Inc.
 */
public class SendEBDMessageTask implements Runnable {
    ControlGUI mainGUI;
    byte deviceNo;
    byte[] message;
    OSP_enums.EBD_Row rowNo;
    private int sendCount = 0;
    int msgSN = 0; // message sequence number

    public SendEBDMessageTask(ControlGUI mainGUI, 
            int deviceNo, OSP_enums.EBD_Row row, byte[] message, int msgSN) 
    {
        this.mainGUI = mainGUI;
        this.deviceNo = (byte) deviceNo;
        this.message = message;
        this.rowNo = row;
        this.msgSN = msgSN;
    }

    @Override
    public synchronized void run() {
        IDevice.ISocket ebdMan = (IDevice.ISocket) mainGUI.getDeviceManagers()[E_Board.ordinal()][deviceNo];
        
        try {
            synchronized(mainGUI.getSocketMutex()[E_Board.ordinal()][deviceNo]) 
            {
                if (! isConnected(ebdMan.getSocket())) 
                {
                    mainGUI.getSocketMutex()[E_Board.ordinal()][deviceNo].wait();
                }
            }
            ++sendCount;
            ebdMan.getSocket().getOutputStream()
                    .write(message);
            
        } catch (IOException e) {
            gfinishConnection(E_Board, null,  
                    "EBD message sent", 
                    deviceNo,
                    mainGUI.getSocketMutex()[E_Board.ordinal()][deviceNo],
                    ebdMan.getSocket(),
                    mainGUI.getMessageTextArea(), 
                    mainGUI.getSockConnStat()[E_Board.ordinal()][deviceNo],
                    mainGUI.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                    mainGUI.isSHUT_DOWN()
            );
        } catch (InterruptedException ex) {
            logParkingException(Level.SEVERE, ex, "E-Board #" + deviceNo + " message sender wait socket conn'");
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
            JOptionPane.showMessageDialog(null, "negative resend count");
        }        
        return sendCount - 1;  // first send shouldn't be counted
    }        
}
