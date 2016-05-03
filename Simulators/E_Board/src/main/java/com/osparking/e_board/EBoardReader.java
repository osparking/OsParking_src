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
package com.osparking.e_board;

import java.io.File;
import com.osparking.global.names.EBD_DisplaySetting;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import static com.osparking.global.names.DB_Access.EBD_flowCycle;
import static com.osparking.global.names.DB_Access.readEBoardUsageSettings;
import com.osparking.global.names.DeviceReader;
import com.osparking.global.Globals;
import com.osparking.global.names.OSP_enums.*;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.GENERAL_DEVICE;
import static com.osparking.global.Globals.closeSocket;
import static com.osparking.global.Globals.getPathAndDay;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingExceptionStatus;
import static com.osparking.global.Globals.noArtificialErrorInserted;
import static com.osparking.global.Globals.stringLengthInPixels;
import static com.osparking.global.Globals.timeFormat;
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import static com.osparking.global.names.OSP_enums.DisplayArea.BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.DisplayArea.TOP_ROW;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.*;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_TOP_ROW;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import java.net.SocketTimeoutException;
import static javax.swing.text.html.HTML.Tag.HEAD;

/**
 *
 * @author Open Source Parking Inc.
 */
public class EBoardReader extends Thread implements DeviceReader {
    A_EBD_GUI eBoardGUI = null;
    private Socket managerSocket = null; // socket that connects to the manager program
    private boolean SHUT_DOWN = false;
    byte [] lenByteArr = new byte[2];
    static Random rand = new Random();
    
    int seq = 0;
    public FileWriter logFileWriter = null; 
    static boolean justBooted = true;

    public EBoardReader(A_EBD_GUI gateBarGUI) {
        super("E_Board" + gateBarGUI.getID() + "_Reader");
        this.eBoardGUI = gateBarGUI;
        
        if (DEBUG) {
            //<editor-fold desc="-- Prepare log file to store 'E-Board display interrupt' message Sequence Number">
            StringBuilder pathname = new StringBuilder();
            StringBuilder daySB = new StringBuilder();

            getPathAndDay("operation", pathname, daySB);

            // full path name of the today's text file for display interrupt command SN logging
            String operationLogFilePathname = pathname + File.separator 
                    + daySB.toString() + "_E_Board_" + eBoardGUI.getID() + ".txt";
            try {
                logFileWriter = new FileWriter(operationLogFilePathname, false); 
                logFileWriter.write("#" + eBoardGUI.getID() + " Electronic Display Received Interrupt IDs" 
                        + System.lineSeparator());
                logFileWriter.write("<current> <previous>" + System.lineSeparator());
                logFileWriter.flush();
            } catch (FileNotFoundException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "prepare logging file", 
                        eBoardGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
            } catch (IOException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "prepare logging file", 
                        eBoardGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
            }  
            //</editor-fold>
        }
    }
        
    public void run() {

        while (true)
        {
            //<editor-fold desc="-- Read and process message if available">
            int msgCode = -2;
            try 
            {
                if (isSHUT_DOWN()) {
                    closeIDLogFile();  
                    return;
                }
                
                //<editor-fold desc="-- Read first byte(message code) from the socket">
                synchronized(eBoardGUI.getSocketMUTEX()) 
                {
                    if (! isConnected(getManagerSocket())) {
                        eBoardGUI.getSocketMUTEX().wait();
                        System.out.println("B1. EBD reader woke up");
                    }
                }
                
                if (justBooted) {
                    managerSocket.getOutputStream().write(JustBooted.ordinal());
                    justBooted = false;
                }
                
                msgCode = getManagerSocket().getInputStream().read(); // waits for PULSE_PERIOD miliseconds
//                System.out.println("3-1. message code delivered at: " + System.currentTimeMillis() % 10000);

                if (msgCode == -1) {
                    disconnectSocket(null, "End of stream reached");
                    continue;
                } else if (msgCode < 0 || MsgCode.values().length <= msgCode) {
                    disconnectSocket(null, "Code out of range");
                    continue;                    
                }
                //</editor-fold>
                
                int msgLength = 0;
                byte[] restOfMessage = null;
                byte [] checkShort = null;
                int len = 0;
                byte[] coreBytes = null;
                short checkACK = 0;

                synchronized(eBoardGUI.getSocketMUTEX()) 
                {
                    //<editor-fold desc="-- Read and process rest bytes of the arrived message">
                    switch (MsgCode.values()[msgCode]) 
                    {
                        case EBD_GetID:
                            //<editor-fold desc="-- Handle heartbeat: respond with ACK message">
                            if (noArtificialErrorInserted(eBoardGUI.errorCheckBox)) 
                            {
                                eBoardGUI.getTolerance().assignMAX();
                                getManagerSocket().getOutputStream().write(MsgCode.EBD_ID_ACK.ordinal());
                            }   
                            //</editor-fold>
                            break;

                        case EBD_DEFAULT1:
                        case EBD_DEFAULT2:
                            //<editor-fold defaultstate="collapsed" desc="-- Change E-Board display temporarily">  
                            if (isConnected(getManagerSocket()))
                                getManagerSocket().getInputStream().read(lenByteArr);
                            else 
                                continue;
                            msgLength = ByteBuffer.wrap(lenByteArr).getShort();

                            // Read rest of the message whose structure(protocol) is :
                            //  <row:1><msgSN:4><text:?><type:1><color:1><font:1><pattern:1><cycle:4><check:2>
                            restOfMessage = new byte[msgLength - 2]; // subtract 2 from whole length
                                // since it was already read just now when length was needed first hand.
                            
                            if (isConnected(getManagerSocket()))
                                getManagerSocket().getInputStream().read(restOfMessage);
                            else 
                                continue;

                            // verify check bytes
                            checkShort = new byte[2]; // function: checking, size: 2 byte short
                            len = restOfMessage.length;
                            coreBytes = Arrays.copyOfRange(restOfMessage, 0,  len - 2);
                            
                            System.out.println("code: " + MsgCode.values()[msgCode]);

                            addUpBytes((byte)msgCode, lenByteArr, coreBytes, checkShort);
                            if (checkShort[1] == restOfMessage[len - 1] 
                                    && checkShort[0] == restOfMessage[len - 2]
                                    && noArtificialErrorInserted(eBoardGUI.errorCheckBox)) 
                            {
                                // Check if this message isn't a duplicate one.
                                int msgSN = ByteBuffer.wrap(Arrays.copyOfRange(restOfMessage, 1, 5)).getInt();
                                System.out.println("curr SN: " + msgSN + ", prev: " + eBoardGUI.prevMsgSN[coreBytes[0]]);
                                if (msgSN != eBoardGUI.prevMsgSN[coreBytes[0]]) {
                                    //<editor-fold desc="-- Decode message, update settings, change display">
                                    // decode message field by field, and apply the result to global variables(Settings)
                                    // and call _Display method
                                    // in case of real hardware, code snippet below should really decode coreBytes
                                    // refer interruptCurrentDisplay method which perform a similar task.
//<<<<<<< HEAD
                                    if (coreBytes[0] == TOP_ROW.ordinal()) {
                                        eBoardGUI.defaultDisplaySettings[TOP_ROW.ordinal()] =
                                                readEBoardUsageSettings(DEFAULT_TOP_ROW);
                                        eBoardGUI.changeE_BoardDisplay(TOP_ROW,
                                                eBoardGUI.defaultDisplaySettings[TOP_ROW.ordinal()]); 
                                    } else {
                                        eBoardGUI.defaultDisplaySettings[BOTTOM_ROW.ordinal()] = 
                                                readEBoardUsageSettings(DEFAULT_BOTTOM_ROW);        
                                        eBoardGUI.changeE_BoardDisplay(BOTTOM_ROW, 
                                                eBoardGUI.defaultDisplaySettings[BOTTOM_ROW.ordinal()]);
//=======
//                                    if (coreBytes[0] == TOP_ROW) {
//                                        eBoardGUI.defaultDisplaySettings[TOP_ROW] =
//                                                readEBoardUsageSettings(DEFAULT_TOP_ROW);
//                                        eBoardGUI.changeE_BoardDisplay(
//                                                TOP_ROW, eBoardGUI.defaultDisplaySettings[TOP_ROW]); 
//                                    } else {
//                                        eBoardGUI.defaultDisplaySettings[BOTTOM_ROW] = 
//                                                readEBoardUsageSettings(DEFAULT_BOTTOM_ROW);        
//                                        eBoardGUI.changeE_BoardDisplay(
//                                                BOTTOM_ROW, eBoardGUI.defaultDisplaySettings[BOTTOM_ROW]);
//>>>>>>> osparking/master
                                    }
                                    //</editor-fold>
//                                    if (DEBUG) 
//                                        saveMsgSN(msgSN, eBoardGUI.prevMsgSN[coreBytes[0]]);
                                    eBoardGUI.prevMsgSN[coreBytes[0]] = msgSN;
                                }

                                //<editor-fold desc="--Build ack message and send it to the manager">
                                byte[] ackMessage = {(byte)EBD_ACK.ordinal(), (byte)msgCode, 0, 0};
                                checkACK = (short)(ackMessage[0] + ackMessage[1]);
                                ackMessage[2] = (byte)((checkACK >> 8) & 0xff);
                                ackMessage[3] = (byte)(checkACK & 0xff);   
    
                                while (! isConnected(getManagerSocket())) {
                                    System.out.println("before defa ack");
                                    eBoardGUI.getSocketMUTEX().wait();
                                    System.out.println("after defa ack");
                                }
                                getManagerSocket().getOutputStream().write(ackMessage);  
                                //</editor-fold>
                            }
                            //</editor-fold>                                
                            break;

                        case EBD_INTERRUPT1:
                        case EBD_INTERRUPT2:
                            //<editor-fold defaultstate="collapsed" desc="-- Change E-Board display temporarily">  
                            if (isConnected(getManagerSocket()))
                                getManagerSocket().getInputStream().read(lenByteArr);
                            else
                                continue;
                            msgLength = ByteBuffer.wrap(lenByteArr).getShort();

                            // read rest of the whole message
                            //  : <row:1><msgSN:4><text:?><type:1><color:1><font:1><pattern:1><cycle:4>
                            //    <delay:4><check:2>
                            restOfMessage = new byte[msgLength - 2]; // subtract 2 from whole length
                                // since it was already read just above when length was needed first hand.
                            if (isConnected(getManagerSocket()))
                                getManagerSocket().getInputStream().read(restOfMessage);
                            else
                                continue;

                            System.out.println("3-2. message body delivered at: " + System.currentTimeMillis() % 10000);
                            
                            // verify check bytes
                            checkShort = new byte[2]; // function: checking, size: short(2 bytes)
                            len = restOfMessage.length;
                            coreBytes = Arrays.copyOfRange(restOfMessage, 0,  len - 2);

                            addUpBytes((byte)msgCode, lenByteArr, coreBytes, checkShort);
                            if (checkShort[1] == restOfMessage[len - 1] 
                                    && checkShort[0] == restOfMessage[len - 2]
                                    && noArtificialErrorInserted(eBoardGUI.errorCheckBox)) 
                            {
                                //<editor-fold desc="-- Process a valid message from the manager">
                                // check if this message has already been processed(not a duplicate one?)
                                int msgSN = ByteBuffer.wrap(Arrays.copyOfRange(restOfMessage, 1, 5)).getInt();
                                if (msgSN != eBoardGUI.prevMsgSN[coreBytes[0]]) {
                                    // decode message field by field and apply the result to display
                                    System.out.println("display Intr msg ID: " + msgSN);
                                    interruptCurrentDisplay(coreBytes);
                                    if (DEBUG) {
                                        saveMsgSN(msgSN, eBoardGUI.prevMsgSN[coreBytes[0]]);
                                    }
                                    eBoardGUI.prevMsgSN[coreBytes[0]] = msgSN;
                                }
                                // build ack message and send it to the manager
                                byte[] ackMessage = {(byte)EBD_ACK.ordinal(), (byte)msgCode, 0, 0};
                                checkACK = (short)(ackMessage[0] + ackMessage[1]);
                                ackMessage[2] = (byte)((checkACK >> 8) & 0xff);
                                ackMessage[3] = (byte)(checkACK & 0xff);   
                                
                                while (! isConnected(getManagerSocket())) {
                                    System.out.println("before intr ack");
                                    eBoardGUI.getSocketMUTEX().wait();
                                    System.out.println("after intr ack");
                                }                                
                                getManagerSocket().getOutputStream().write(ackMessage); 
                                System.out.println("3-3. wrote ack at: " + System.currentTimeMillis() % 10000);
                                
                                //</editor-fold>
                            }
                            //</editor-fold>
                            break;

                        default:
                            eBoardGUI.criticalInfoTextField.setText("good but un-planned code");
                            throw new Exception ("unplanned message code: " + MsgCode.values()[msgCode]); 
                            //break;
                    }
                    //</editor-fold>
                }
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ie) {
                disconnectSocket(ie, "while waiting socket connection");
            } catch(IOException e) {
                disconnectSocket(e, "reader processing message code");
            } catch(Exception e) {                  
                disconnectSocket(e, "reader handling message code: " + msgCode);
            }
            //</editor-fold>

            if (isConnected(getManagerSocket()) && eBoardGUI.getTolerance().getLevel() < 0 ) {
                disconnectSocket(null, "Manager isn't reaching at " + E_Board + " #" + eBoardGUI.getID());
            }
        }
    }
    
    private void closeIDLogFile() {
        if (logFileWriter != null) {
            try {
                logFileWriter.close();
                logFileWriter = null;
            } catch (IOException e) {}
        }
    }    

    /**
     * @return the finishingOperation
     */
    public boolean isSHUT_DOWN() {
        return SHUT_DOWN;
    }

    /**
     * @param SHUT_DOWN the finishingOperation to set
     */
    public void setSHUT_DOWN(boolean SHUT_DOWN) {
        this.SHUT_DOWN = SHUT_DOWN;
    }

    /**
     * @return the managerSocket
     */
    public Socket getManagerSocket() {
        return managerSocket;
    }

    /**
     * @param managerSocket the managerSocket to set
     */
    public void setManagerSocket(Socket managerSocket) {
        this.managerSocket = managerSocket;
    }     

    private void addUpBytes(byte code, byte[] lenBytes, byte[] restOfMessage, byte[] checkShort) {
        
        int total = code;
        total += lenBytes[0]; // msgLen[1]
        total += lenBytes[1]; // msgLen[0]
        for (byte aByte : restOfMessage) {
            total += aByte;
        }
        
        short result = (short) (total % Math.pow(2, 16));
        checkShort[0] = (byte)((result >> 8) & 0xff);
        checkShort[1] = (byte)(result & 0xff);        
    }

    private void interruptCurrentDisplay(byte[] coreInfoBytes) {
        // Message protocol: <row:1><text:varies><type:1><color:1><font:1><pattern:1><cycle:4><delay:4>
//<<<<<<< HEAD
        final byte rowNo = coreInfoBytes[0];
        final DisplayArea row = (rowNo == 0 ? TOP_ROW : BOTTOM_ROW);
//=======
//        final byte row = coreInfoBytes[0];
//>>>>>>> osparking/master
        int index = 12; // 12 == total length of 6 fields (<type> ~ <delay>)
        
        String displayText = ":UnsupportedEncodingException: ";
        int len = coreInfoBytes.length;
        try {
            displayText = new String(Arrays.copyOfRange(coreInfoBytes, 5,  len - index), "UTF-8");
        } catch (IOException ex) {
            logParkingException(Level.SEVERE, ex, "logging display text");
        }
        
        byte typeIndex = coreInfoBytes[len - index--];
        byte colorIndex = coreInfoBytes[len - index--];
        byte fontIndex = coreInfoBytes[len - index--];
        byte patternIndex = coreInfoBytes[len - index--];
        int cycle = ByteBuffer.wrap(Arrays.copyOfRange(coreInfoBytes, len - 8,  len - 4)).getInt();
        int delay = ByteBuffer.wrap(Arrays.copyOfRange(coreInfoBytes, len - 4,  len)).getInt();
        
        EBD_ContentType adjustedContentType = EBD_ContentType.values()[typeIndex];
        if (displayText.length() > 0) 
            adjustedContentType = VERBATIM;
        
        if (stringLengthInPixels(displayText, eBoardGUI.topTextField) > eBoardGUI.topTextField.getWidth()) {
            /// do smthing
            patternIndex = (byte) (EBD_Effects.RTOL_FLOW.ordinal());
            System.out.println("adjust cycle: " + cycle);
            cycle = EBD_flowCycle;
        }        
        
        eBoardGUI.getCriticalInfoTextField().setText(
                                    timeFormat.format(new Date()) + "-- Vehicle Entered");
        eBoardGUI.changeE_BoardDisplay(row, 
                new EBD_DisplaySetting(displayText, adjustedContentType,
                        EBD_Effects.values()[patternIndex], EBD_Colors.values()[colorIndex], 
                        EBD_Fonts.values()[fontIndex], cycle));
        
        //<editor-fold desc="-- print debug message">
        if (DEBUG) {
//<<<<<<< HEAD
            System.out.println("row: " + rowNo);
//=======
//            System.out.println("row: " + row);
//>>>>>>> osparking/master
            System.out.println("msg : " + displayText);
            if (displayText.length() == 0)
                System.out.println("msg length is zero : ");
            System.out.print("type: " + EBD_ContentType.values()[typeIndex]);
            System.out.print(", color: " + EBD_Colors.values()[colorIndex]);
            System.out.println(", index : " + colorIndex);
            System.out.println("font: " + EBD_Fonts.values()[fontIndex]); 
            System.out.println("pattern: " + EBD_Effects.values()[patternIndex]);
            System.out.println("cycle: " + cycle);
            System.out.println("delay: " + delay);
        }
        //</editor-fold>
        
        //<editor-fold desc="-- reserve default message display event">
        if (delay == -1) {
//<<<<<<< HEAD
            eBoardGUI.prevMsgSN[rowNo] = 0;
        } else {
            try {
                eBoardGUI.getDisplayRestoreTimer()[rowNo].reRunOnce(
                        new TimerTask() {
                            @Override
                            public void run() {
                                eBoardGUI.changeE_BoardDisplay(row, eBoardGUI.getDefaultDisplaySettings()[rowNo]);
//=======
//            eBoardGUI.prevMsgSN[row] = 0;
//        } else {
//            try {
//                eBoardGUI.getDisplayRestoreTimer()[row].reRunOnce(
//                        new TimerTask() {
//                            @Override
//                            public void run() {
//                                eBoardGUI.changeE_BoardDisplay(row, eBoardGUI.getDefaultDisplaySettings()[row]);
//>>>>>>> osparking/master
                                eBoardGUI.getCriticalInfoTextField().setText(
                                        timeFormat.format(new Date()) + "-- Vehicle left gate");
                            }
                        },
                        delay
                );
            } catch (Exception e) {
//<<<<<<< HEAD
                logParkingException(Level.SEVERE, e, "Return to default display for row #" + rowNo, eBoardGUI.getID());  
//=======
//                logParkingException(Level.SEVERE, e, "Return to default display for row #" + row, eBoardGUI.getID());  
//>>>>>>> osparking/master
            }  
        }
        //</editor-fold>
    }

    private synchronized void saveMsgSN(int currMsgSN, int prevMsgSN) {
        String message = currMsgSN + " " + prevMsgSN + System.lineSeparator();
        
        Globals.logParkingOperation(OpLogLevel.LogAlways, message, eBoardGUI.getID());
        
        try {
            logFileWriter.write(message);
            logFileWriter.flush();
        } catch (FileNotFoundException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "display msg SN logging module", 
                    eBoardGUI.getCriticalInfoTextField(), eBoardGUI.getID());
        } catch (IOException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "EBD msg SN logging module", 
                    eBoardGUI.getCriticalInfoTextField(), eBoardGUI.getID());
        }             
    }

//    public synchronized void disconnectSocket(Exception e, String reason) {
    public void disconnectSocket(Exception e, String reason) {
        
        logParkingException(Level.INFO, e, reason, eBoardGUI.getID());
        
        if (DEBUG) {
            System.out.println("B1. E-Board #" + eBoardGUI.getID() + " close socket at: " +
                    System.currentTimeMillis());
        }
        synchronized(eBoardGUI.getSocketMUTEX()) {
            closeSocket(getManagerSocket(), "manager socket closing");
            setManagerSocket(null);
        }
        eBoardGUI.getCriticalInfoTextField().setText(timeFormat.format(new Date()) + "--manager disconnected");
        if (! eBoardGUI.isSHUT_DOWN())
            eBoardGUI.getAcceptManagerTimer().reRunOnce();
    }       

    @Override
    public void stopOperation(String cause) {
        disconnectSocket(null, cause);
        setSHUT_DOWN(true);
    }
}
