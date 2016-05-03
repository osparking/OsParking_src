/*
 * Copyright (C) 2015 Open Source Parking Inc.
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
package com.osparking.osparking.device.LEDnotice;

import static com.osparking.global.Globals.isConnected;
import com.osparking.global.names.OSP_enums.DeviceType;
import com.osparking.osparking.ControlGUI;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.Broken;
import static com.osparking.osparking.device.LEDnotice.RS_232_Manager.readDeliveredMessage;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 *
 * @author Open Source Parking Inc.
 */
public class SocketReader  extends Thread {
    ControlGUI mainGUI;
    private LEDnoticeManager ledNoticeManager; // main form of the gate bar simulator.
    private byte gateID = 0; // ID of the gate bar being served by this manager. A valid ID starts from 1.
    
    public SocketReader(ControlGUI mainGUI, LEDnoticeManager ledNoticeManager, byte gateID)
    {
        super("osp_GateBar_" + gateID + "_Manager");        
        this.mainGUI = mainGUI; 
        this.ledNoticeManager = ledNoticeManager; 
        this.gateID = gateID; 
    }       
    
    public void run()
    {   
        while (true) // infinite reading of a gate bar socket 
        {
            synchronized (mainGUI.getSocketMutex()[DeviceType.E_Board.ordinal()][gateID]) 
            {
                if (!isConnected(ledNoticeManager.getSocket())) {
                    try {
                        mainGUI.getSocketMutex()[DeviceType.E_Board.ordinal()][gateID].wait();
                    } catch (InterruptedException ex) {
                        System.out.println("intred excp");
                    }
                }
            }   
            
            LED_MsgType messageArrived = null;
            try {
                messageArrived= readDeliveredMessage(ledNoticeManager.getSocket().getInputStream());
                System.out.println("LED message type came: " + messageArrived);
                
                if (messageArrived != Broken) {
                    synchronized(ledNoticeManager.getMsgArrived()) {
                        ledNoticeManager.setMsg(messageArrived);
                        ledNoticeManager.getMsgArrived().notify();
                    }
                }              
            } catch (SocketTimeoutException ex) {
                System.out.println("time out");
            } catch (IOException ex) {
                System.out.println("IO excep");
                ledNoticeManager.finishConnection(null, "IO excep", gateID);
            }
            
        }
    }
}
