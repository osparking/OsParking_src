/*
 * Copyright (C) 2015 Open Source Parking Inc.
 *
 * This program inStream free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program inStream distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.osparking.osparking.device.LEDnotice;

import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.Broken;
import static com.osparking.osparking.device.LEDnotice.LedProtocol.SUCCESS;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Open Source Parking Inc.
 */
public class RS_232_Manager implements SerialPortEventListener {

    private static boolean validMessage(byte[] preMsg, byte possibleETX) {
        if (preMsg[0] == LedProtocol.intSTX 
                && possibleETX == LedProtocol.intETX
                && preMsg[1] == SUCCESS) 
        {
            return true;
        } else {
            return false;
        }    
    }

    private static LED_MsgType getLED_MsgType(int uintMsgType) {
        for (LED_MsgType type : LED_MsgType.values()) {
            if (type.getValue() == uintMsgType) 
                return type;
        }
        return Broken;
    }

    LEDnoticeManager manager;
    InputStream inStream;
    RS_232_Manager(LEDnoticeManager manager, SerialPort port) {
        try {
            this.manager = manager;
            inStream = port.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(RS_232_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void serialEvent(SerialPortEvent e) {
        try {
            byte[] preMsg = new byte[3];
            int msgLength = -2;
            byte typeInt;
            int typeUint = 0;
            byte posiETX;
            byte[] MsgPost = new byte[100];
            int byteIndex = 0;
            
            //
            // Discriminate handling according to event type
            //
            switch(e.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    
                    msgLength = inStream.read(preMsg); // waits for PULSE_PERIOD miliseconds 
                    typeInt = preMsg[2];
                    typeUint = LedProtocol.byteToUint(typeInt);

                    while ( true )
                    {
                        posiETX = (byte)(inStream.read());
                        if (posiETX == -1) {
//                            finishConnection(null, "LEDnotice closed socket", 1);
                            break;
                        } else 
                        if (posiETX == 3) {
                            MsgPost[byteIndex++] = posiETX;
                            break;
                        } else {
                            if (posiETX == 0x10) {
                                byte aByte = (byte)inStream.read();
                                posiETX = (byte)(aByte - 0x20);
                            }
                            MsgPost[byteIndex++] = posiETX;
                        }
                    }                    
                    
                    if (msgLength != -1) {
                        if (byteIndex > 0 && manager.validMessage(preMsg, MsgPost[byteIndex - 1])) 
                        {
                            manager.processValidMessage(preMsg, byteIndex, MsgPost, typeUint);
                        }
                    } else {
                        // 'End of stream' means other party closed socket. So, I need to close it from my side.
//                        finishConnection(null,  "End of stream reached, gate #" + deviceNo, deviceNo);
                    }                    
                    break;
                    
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(RS_232_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static LED_MsgType readDeliveredMessage(InputStream inStream) throws IOException {
        LED_MsgType msgFromLED = Broken;
        byte[] preMsg = new byte[3];
        byte posiETX;        
        byte[] MsgPost = new byte[100];
        
        //<editor-fold desc="-- Read arriving message from LEDnotice">
        // SocketTimeoutException will arise when no data on the socket during 1 second
        int byteIndex = 0;
        int msgLength = inStream.read(preMsg); // waits for PULSE_PERIOD miliseconds 

        while ( true )
        {
            posiETX = (byte)(inStream.read());
            if (posiETX == -1) {
                throw new SocketException("Socket Closed by LEDnotice");
            } else 
            if (posiETX == 3) {
                MsgPost[byteIndex++] = posiETX;
                break;
            } else {
                if (posiETX == 0x10) {
                    byte aByte = (byte)inStream.read();
                    posiETX = (byte)(aByte - 0x20);
                }
                MsgPost[byteIndex++] = posiETX;
            }
        }
        
        if (msgLength != -1 && byteIndex > 0 && validMessage(preMsg, MsgPost[byteIndex - 1])) {
            System.out.println("msg code uint: " + preMsg[2]);
            msgFromLED = getLED_MsgType(LedProtocol.byteToUint(preMsg[2]));
        }
        return msgFromLED;
    }
}
