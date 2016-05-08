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
import static com.osparking.global.Globals.gateDeviceTypes;
import static com.osparking.global.Globals.gfinishConnection;
import static com.osparking.global.Globals.logParkingException;
import java.awt.Color;
import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import static com.osparking.global.Globals.logParkingExceptionStatus;
import com.osparking.global.names.OSP_enums.DeviceType;
import static com.osparking.global.names.OSP_enums.DeviceType.E_Board;
import com.osparking.osparking.ControlGUI;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.MsgCode.AreYouThere;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_GetID;
import com.osparking.global.names.IDevice.ISocket;
import static com.osparking.global.names.OSP_enums.E_BoardType.LEDnotice;
import com.osparking.osparking.device.BlackFly.BlackFlyManager;
import com.osparking.osparking.device.LEDnotice.LEDnoticeManager;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType;
import com.osparking.osparking.device.LEDnotice.LedProtocol;
import com.osparking.osparking.device.LEDnotice.LEDnoticeMessageQueue.MsgItem;
import com.osparking.osparking.device.NaraBar.NaraMessageQueue.NaraMsgItem;
import com.osparking.osparking.device.NaraBar.NaraBarMan;
import com.osparking.osparking.device.NaraBar.NaraEnums;
import com.osparking.osparking.device.NaraBar.NaraEnums.Nara_MsgType;
import com.osparking.osparking.device.NaraBar.NaraMessageQueue;
import java.io.OutputStream;
import java.nio.ByteBuffer;

//<editor-fold desc="-- Class LED_Task">
/**
 * Display the camera connection status with the Manager program
 * 
 * @author Open Source Parking Inc.
 */
public class LED_Task extends TimerTask {

    ControlGUI controlGUI = null;
    IDevice.IManager[][] deviceManagers = null;
    /**
     * used to give blinking effect to the status text label on the device socket connection
     */
    boolean setHalfTransparent = false;

    LedProtocol ledNoticeProtocol; 
    byte[] ledNoticeGetIDmsg;
    String getID_HexString;
    
    /**
     * Initializes this task with the main GUI and a managerLEDnotice array.
     * 
     * @param guiMain GUI form frame on which it's device connection status is to be displayed
     * @param gateManagerArr managerLEDnotice array through which their sockets are accessed 
     */
    public LED_Task(ControlGUI guiMain, IDevice.IManager[][] deviceManagers) {
        this.controlGUI = guiMain;
        this.deviceManagers = deviceManagers;
        ledNoticeProtocol = new LedProtocol();
        
        // Initialize some repeatedly used messages.
        getID_HexString = ledNoticeProtocol.getId();
        ledNoticeGetIDmsg = ledNoticeProtocol.hexToByteArray(getID_HexString);
    }
    
    /**
     * Displays connection status to each hardware device of each gate periodically. 
     * 
     * One row of LED labels represents device components at an enterance gate.
     * To give blinking Las Vegas sign effect, assigns different transparency degrees for each neighboring rows.
     */
    public void run() {
        byte typeNo;
        float opaqueDegree; // change alpha value to give blinking effect

        for (DeviceType devType : DeviceType.values()) {
            typeNo = (byte)devType.ordinal();
            
            for (byte gateNo = 1; gateNo <= gateCount; gateNo++) // gn : gate number
            {
                try {
                    if (setHalfTransparent && gateNo % 2 == 0 ||
                            !setHalfTransparent && gateNo % 2 != 0) {
                        opaqueDegree = 1.0f; // completely opaque
                    } else {
                        opaqueDegree = 0.5f; // half opaque
                    }

                    //<editor-fold desc="--decrease alpha value of odd row LED label">
                    if (deviceManagers[typeNo][gateNo] != null && 
                            IDevice.isConnected(deviceManagers[typeNo][gateNo], devType, gateNo))
                    {
                        sendHeartBeat(devType, gateNo);
                        controlGUI.tolerance[devType.ordinal()][gateNo].decrease();

                        if (gateNo % 2 == 0 )
                            controlGUI.getDeviceConnectionLEDs()[typeNo][gateNo]
                                    .setForeground(new Color( 0.0f, 1.0f, 0.0f, opaqueDegree));
                        else
                            controlGUI.getDeviceConnectionLEDs()[typeNo][gateNo]
                                    .setForeground(new Color( 0.0f, 1.0f, 0.0f, opaqueDegree));
                    } else {
                        if (gateNo % 2 == 0 ) // for blinking effect between adjacent rows
                            controlGUI.getDeviceConnectionLEDs()[typeNo][gateNo]
                                    .setForeground(new Color( 1.0f, 0.0f, 0.0f, opaqueDegree));
                        else
                            controlGUI.getDeviceConnectionLEDs()[typeNo][gateNo]
                                    .setForeground(new Color( 1.0f, 0.0f, 0.0f, opaqueDegree));
                        if (devType == DeviceType.Camera && 
                                gateDeviceTypes[gateNo].cameraType == OSP_enums.CameraType.Blackfly)
                        {
                            BlackFlyManager bfMan = (BlackFlyManager)deviceManagers[typeNo][gateNo];
                            if (bfMan.findCamera() > 0) {
                                bfMan.initBusanANPR();
                            }
                        }
                    }
                    //</editor-fold>
                } catch (Exception e) {
                    logParkingExceptionStatus(Level.SEVERE, e, "LED task finishing: ", 
                            controlGUI.getStatusTextField(), gateNo);            
                }
            }
        }
        setHalfTransparent  =  ! setHalfTransparent;
    }
    
    private void sendHeartBeat(DeviceType type, byte gateNo) {
        OutputStream outStream;
        byte[] msgBytes = null;
        
        if (type == DeviceType.E_Board) {
            switch (gateDeviceTypes[gateNo].eBoardType) {
                case LEDnotice:
                    msgBytes = ledNoticeGetIDmsg;
                    break;

                default:
                    msgBytes = ByteBuffer.allocate(1).put((byte)EBD_GetID.ordinal()).array();
                    break;
            }
        } else {
            if (type == DeviceType.Camera && 
                    gateDeviceTypes[gateNo].cameraType == OSP_enums.CameraType.Blackfly) 
            {
                ; // Do not send heartbeat in case of Blackfly camera.
                // reset heartbeat timer, instead.
            } else {
                try {
                    msgBytes = ByteBuffer.allocate(1).put((byte)AreYouThere.ordinal()).array();
                } catch (Exception exc) {
                    System.out.println("exc");
                }
            }
        }
        
        try {
            if (type == DeviceType.E_Board && 
                    gateDeviceTypes[gateNo].eBoardType == OSP_enums.E_BoardType.LEDnotice) 
            {
                LEDnoticeManager manager = (LEDnoticeManager)deviceManagers[type.ordinal()][gateNo];
                if (manager.getLedNoticeMessages().size() == 0) 
                {
                    manager.getLedNoticeMessages().add(new MsgItem(LED_MsgType.GET_ID, getID_HexString));
                }
            } else if (type == DeviceType.GateBar &&
                    gateDeviceTypes[gateNo].gateBarType == OSP_enums.GateBarType.NaraBar) 
            {
                // send status message to the gate bar firmware
                NaraBarMan manager = (NaraBarMan)deviceManagers[type.ordinal()][gateNo];
                if (manager.getNaraBarMessages().size() == 0) 
                {
                    manager.getNaraBarMessages().add(new NaraMsgItem(Nara_MsgType.Status));
                }
            } else {
                if (type == DeviceType.Camera && 
                        gateDeviceTypes[gateNo].cameraType == OSP_enums.CameraType.Blackfly) 
                {       
                    ; // Do not send heartbeat in case of Blackfly camera.
                } else {     
                    // send to simulators
                    outStream = ((ISocket)deviceManagers[type.ordinal()][gateNo])
                            .getSocket().getOutputStream();
                    outStream.write(msgBytes);
                }
            }
        } catch (IOException e) {
            gfinishConnection(type, null,  
                    "while sending heartbeat", 
                    gateNo,
                    controlGUI.getSocketMutex()[type.ordinal()][gateNo],
                    ((ISocket)deviceManagers[type.ordinal()][gateNo]).getSocket(),
                    controlGUI.getMessageTextArea(), 
                    controlGUI.getSockConnStat()[type.ordinal()][gateNo],
                    controlGUI.getConnectDeviceTimer()[type.ordinal()][gateNo],
                    controlGUI.isSHUT_DOWN()
                    );                 
        }
    }
}
