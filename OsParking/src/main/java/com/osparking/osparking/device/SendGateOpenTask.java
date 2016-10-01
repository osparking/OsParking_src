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

import static com.osparking.global.Globals.gfinishConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.IDevice;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.Open;
import com.osparking.osparking.ControlGUI;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
//import parking.device.CameraReader.WriteMUTEX;

/**
 * send one specific open command to a gate bar.
 * 
 * @author Open Source Parking Inc.
 */
public class SendGateOpenTask implements Runnable {
    ControlGUI mainForm;
//    Socket socket;
    byte gateID;
    
    int openCmd_ID;
    byte[] msgCode = new byte[]{(byte)MsgCode.Open.ordinal()}; //length:1
    
    byte[] integerArr = null;
    private int[] resendCount = new int[4];
    byte[] openMsgArr = new byte[9]; // code(1 byte), cmd ID(4 bytes), delay(4 bytes)
    
    /**
     * Open Command Sender Task constructor.
     * 
     * @param managerForm manager program GUI
     * @param gateID ID of the gate being simulated
     * @param openCmd_ID ID of the open command to send*
     * @param passingDelay car passing delay in milliseconds
     */
    public SendGateOpenTask(ControlGUI managerForm, byte gateID, int openCmd_ID, int passingDelay) {
        
        this.mainForm = managerForm;
        this.gateID = gateID;
        this.openCmd_ID = openCmd_ID;

        openMsgArr[0] = (byte)Open.ordinal();
        
        integerArr = ByteBuffer.allocate(4).putInt(openCmd_ID).array();
        System.arraycopy(integerArr, 0, openMsgArr, 1, 4);
        
        integerArr = ByteBuffer.allocate(4).putInt(passingDelay).array();
        System.arraycopy(integerArr, 0, openMsgArr, 5, 4);
    }

    /**
     * send an open command using socket's output stream.
     */
    @Override
    public synchronized void run() {
        synchronized(mainForm.getSocketMutex()[GateBar.ordinal()][gateID]) 
        {
            if (! IDevice.isConnected(
                    mainForm.getDeviceManagers()[GateBar.ordinal()][gateID], GateBar, gateID))
            {
                try {
                    mainForm.getSocketMutex()[GateBar.ordinal()][gateID].wait();
                } catch (InterruptedException ex) {
                    logParkingException(Level.INFO, ex, "GateBar #" + gateID + " waits socket conn'");
                }
            }
            ++resendCount[gateID];
            IDevice.IManager gateMan = mainForm.getDeviceManagers()[GateBar.ordinal()][gateID];
            gateMan.writeMessage(Open, openMsgArr);
        }
    }

    /**
     * supplies this open command resent count.
     * used to check the system performance in case of network error/delay.
     * 
     * @return the resendCount
     */
    public int getResendCount() {
        if (resendCount[gateID] - 1 < 0) {
//            JOptionPane.showMessageDialog(null, "negative resend count-Gate");
            return 0;
        }
        return resendCount[gateID] - 1;  // first send shouldn't be counted
    }
}
