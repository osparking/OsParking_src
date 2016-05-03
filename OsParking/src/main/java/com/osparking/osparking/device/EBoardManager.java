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

import com.osparking.global.Globals;
import com.osparking.global.names.IDevice;
import com.osparking.osparking.ControlGUI;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import com.osparking.global.names.ParkingTimer;
import static com.osparking.global.names.DB_Access.gateCount;
import static com.osparking.global.names.DB_Access.gateNames;
import com.osparking.global.names.EBD_DisplaySetting;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.GATE_NAME;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.VERBATIM;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.EBD_DisplayUsage.DEFAULT_TOP_ROW;
import com.osparking.global.names.OSP_enums.EBD_Row;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_DEFAULT1;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_DEFAULT2;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_ID_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT1;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT2;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import static com.osparking.osparking.ControlGUI.EBD_DisplaySettings;
import gnu.io.SerialPort;

import java.nio.ByteBuffer;

/**
 * Manages a gate bar via a socket communication while current socket connection is valid.
 * To do that, gateManager uses a socket reader(SockReader) Thread instance and a Runnable class
 * (AreYouThereSender) instance which is created by the SockReader object.
 * 
 * @author Open Source Parking Inc.
 */
public class EBoardManager extends Thread implements
        IDevice.IManager, IDevice.ISocket,  IDevice.IE_Board {

    private static byte[] getEBDSimulatorDefaultMessage(byte deviceNo, EBD_Row row, int msgSN) {
        EBD_DisplaySetting setting = null;
        String displayText = null;
        
        setting = EBD_DisplaySettings
                [row == EBD_Row.TOP ? DEFAULT_TOP_ROW.ordinal() : DEFAULT_BOTTOM_ROW.ordinal()];
            
        //<editor-fold desc="-- determind display text using e-board settings(contentType, type, etc.)">
        switch (setting.contentType) {
            case VERBATIM:
                displayText = setting.verbatimContent;
                break;
                
            case GATE_NAME:
                displayText = gateNames[deviceNo];
                break;
                
            default:
                displayText = "";
                break;
        }
        //</editor-fold>       
        
        byte[] displayTextBytes = displayText.getBytes();
        int displayTextLength = displayTextBytes .length;
        
        // <code:1><length:2><row:1><text:?><type:1><color:1><font:1><pattern:1><cycle:4><check:2>
        byte code = (byte)(row == EBD_Row.TOP ? EBD_DEFAULT1.ordinal() : EBD_DEFAULT2.ordinal());
        short wholeMessageLen // length of 9 fields from <length> to <check>
                = (short)(displayTextLength + 17); // 13 == sum of 8 fields == 9 fields except <text>
        byte[] lenBytes //  {--Len[1], --Len[0]}
                = {(byte)((wholeMessageLen >> 8) & 0xff), (byte)(wholeMessageLen & 0xff)}; 
        byte[] wholeMessageBytes = new byte[wholeMessageLen + 1]; // 1 is for the very first <code>
        
        formMessageExceptCheckShort(code, lenBytes, row, msgSN, displayTextBytes, setting, 0,
                wholeMessageBytes);        
        
        //<editor-fold desc="complete making message byte array by assigning 2 check bytes">
        // calculate 2 check bytes by adding all bytes in the of 9 fields: from <code:1> to <delay:4>
        byte[] check = new byte[2];
        addUpBytes(wholeMessageBytes, check);
        
        int idx = wholeMessageBytes.length - 2;
        wholeMessageBytes[idx++] = check[0];
        wholeMessageBytes[idx++] = check[1];
        //</editor-fold>
        
        return wholeMessageBytes;             
    }

    private static void formMessageExceptCheckShort(byte code, byte[] lenBytes, EBD_Row row, int msgSN, 
            byte[] coreMsg, EBD_DisplaySetting setting, int delay, byte[] wholeMessageBytes) 
    {
        int idx = 0;
        
        wholeMessageBytes[idx++] = code;
        wholeMessageBytes[idx++] = lenBytes[0];
        wholeMessageBytes[idx++] = lenBytes[1];
        wholeMessageBytes[idx++] = (byte)row.ordinal();

        for (byte dByte : ByteBuffer.allocate(4).putInt(msgSN).array()) {
            wholeMessageBytes[idx++] = dByte;
        }        
        
        if (coreMsg != null) {
            for (byte aByte: coreMsg) {
                wholeMessageBytes[idx++] = aByte;
            }
        }
        wholeMessageBytes[idx++] = (byte)setting.contentType.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.textColor.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.textFont.ordinal();
        wholeMessageBytes[idx++] = (byte)setting.displayPattern.ordinal();

        if (code == EBD_INTERRUPT1.ordinal() || code == EBD_INTERRUPT2.ordinal()) {
            for (byte cByte : ByteBuffer.allocate(4).putInt(setting.displayCycle).array()) {
                wholeMessageBytes[idx++] = cByte;
            }
        }
        
        for (byte dByte : ByteBuffer.allocate(4).putInt(delay).array()) {
            wholeMessageBytes[idx++] = dByte;
        }
    }        
    
    //<editor-fold desc="--class variables">
    private byte deviceNo = 0; // ID of the gate bar being served by this manager. A valid ID starts from 1.
    private ControlGUI mainForm; // main form of the gate bar simulator.
    
    /**
     * socket for the communication with the gate bar.
     */
    private Socket socket = null; // socket that connects with a e-board
    
    private SerialPort serialPort = null;
    
    /**
     * a timer employed to send Open commands to the designated gate bar for sure.
     */
    ParkingTimer timerSendOpenCmd = null;
    
    //</editor-fold>    

    byte [] cmdIDarr = new byte[4]; // open command ID
    byte [] fiveByteArr =new byte[5]; // storage for (code + ID)
    
    boolean justBooted = true;
    private boolean neverConnected = true;

    /**
     * 
     * @param mainForm main GUI form of the whole manager program
     * @param deviceNo ID of the E-Board to manage
     */
    public EBoardManager(ControlGUI mainForm, byte deviceNo)
    {
        super("osp_EBD_" + deviceNo + "_Manager");
        this.mainForm = mainForm; 
        this.deviceNo = deviceNo;
    }    

    public void run()
    {   
        while (true) // infinite communication with an e-board
        {
            if (mainForm.isSHUT_DOWN()) {
                return;
            }
            
            int msgCode = -2;
            // read device message as long as connection is good
            
            try {
                synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo])  
                {
                    //<editor-fold desc="-- Wait connection, send default settings, read message code">
                    if (! Globals.isConnected(getSocket())) 
                    {
                        mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo].wait();
                        neverConnected = false;
                    
                        if (justBooted) {
                            justBooted = false;
                            showDefaultMessage();
                        } 
                    }
                    //</editor-fold>
                } 
                // SocketTimeoutException will arise when no data on the socket during 1 second
                msgCode = socket.getInputStream().read(); // waits for PULSE_PERIOD miliseconds                        

                //<editor-fold defaultstate="collapsed" desc="-- Reject irrelevant message code">
                if (msgCode == -1) {
                    // 'End of stream' means other party closed socket. So, I need to close it from my side.
                    gfinishConnection(E_Board, null,  
                            "End of stream reached, gate #" + deviceNo, 
                            deviceNo,
                            mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                            socket,
                            mainForm.getMessageTextArea(), 
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                            mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                            mainForm.isSHUT_DOWN()
                    );   
                
                    continue;
                } else if (msgCode < -1 || MsgCode.values().length <= msgCode) {
                    gfinishConnection(E_Board, null,  
                            "Wrong message code: "+ msgCode, 
                            deviceNo,
                            mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                            socket,
                            mainForm.getMessageTextArea(), 
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                            mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                            mainForm.isSHUT_DOWN()
                    );                       
                    continue;
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="-- Process message from e-board">
                synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo]) 
                {
                    switch (MsgCode.values()[msgCode]) 
                    {      
                        case EBD_ID_ACK: // e-board heartbeat
                            //<editor-fold defaultstate="collapsed" desc="--handle gate bar heartbeat">
                            if (noArtificialErrorInserted(mainForm.errorCheckBox))
                            {
                                /**
                                 * Maximize tolerance value for the E-Board.
                                 * manager will consider this bar connected for the next MAX_TOLERANCE
                                 * LED blinking cycles.
                                 */
                                mainForm.tolerance[E_Board.ordinal()][deviceNo].assignMAX();
                            }
                            break;
                            //</editor-fold>

                        case EBD_ACK: // gate acknowledges an e-board display message reception.
                            //<editor-fold defaultstate="collapsed" desc="--ACK for display interrupt or default display change">
                            byte[] restOfMessage = new byte[3];

                            if (Globals.isConnected(socket))
                                socket.getInputStream().read(restOfMessage);
                            else
                                continue;

//                            System.out.println("4. message ACD arrived at: " + System.currentTimeMillis() % 10000);

                            int codeAcked = restOfMessage[0];
                            short checkTSC = (short)(msgCode + codeAcked); // TCS: This Site Calculation

                            if (restOfMessage[1] == (byte)((checkTSC >> 8) & 0xff)
                                    && restOfMessage[2] == (byte)(checkTSC & 0xff))
                            {
                                //<editor-fold desc="-- Handle ACK response from E-Board">
                                EBD_Row row;

                                //<editor-fold desc="-- Calculate row number(0 or 1)">
                                if (codeAcked == EBD_INTERRUPT1.ordinal()
                                        || codeAcked == EBD_DEFAULT1.ordinal()) {
                                        row = EBD_Row.TOP;
                                } else if (codeAcked == EBD_INTERRUPT2.ordinal()
                                        || codeAcked == EBD_DEFAULT2.ordinal()) {
                                        row = EBD_Row.BOTTOM;
                                } else {
                                    logParkingException(Level.SEVERE, null, "wrong row number", deviceNo);
                                    break;
                                }
                                //</editor-fold>

                                ParkingTimer msgSendingTimer 
                                        = mainForm.getSendEBDmsgTimer()[deviceNo][row.ordinal()];
                                if (msgSendingTimer.hasTask()) {
                                    //<editor-fold desc="-- Save debugging info">
                                    if (codeAcked == EBD_INTERRUPT1.ordinal() || 
                                            codeAcked == EBD_INTERRUPT2.ordinal() ) 
                                    {
                                        if (DEBUG) {
                                            long currTmMs = System.currentTimeMillis();
                                            long ackDelay 
                                                    = (int)(currTmMs - mainForm.eBoardMsgSentMs[deviceNo][row.ordinal()]);
                                            int resendCnt = ( (SendEBDMessageTask)msgSendingTimer.getParkingTask() )
                                                    .getResendCount();

                                            mainForm.getPerfomStatistics()[E_Board.ordinal()][deviceNo]
                                                    .addAckDelayStatistics((int)ackDelay, resendCnt);
                                        }
                                    }
                                    //</editor-fold>

                                    msgSendingTimer.cancelTask();
                                }
                                //</editor-fold>
                            }
                            break;
                            //</editor-fold>

                        case JustBooted:
                            //<editor-fold defaultstate="collapsed" desc="-- First connection after device booting">
                            // reset recent device disconnection time 
//                            System.out.println("just booted arrived");
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo].resetStatChangeTm();
                            break;
                            //</editor-fold>
                                                  
                        default:
                            throw new Exception("Unhandled message code " + MsgCode.values()[msgCode] 
                                + " from E-Board #" + deviceNo);
                    }
                }
                //</editor-fold>
                
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ex) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingException(Level.INFO, ex, "E-Board manager #" + deviceNo + " waits socket conn'");
                    gfinishConnection(E_Board, ex,  
                            "E-Board manager #" + deviceNo + " waits socket conn'", 
                            deviceNo,
                            mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                            socket,
                            mainForm.getMessageTextArea(), 
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                            mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                            mainForm.isSHUT_DOWN()
                    );                       
                }
            } catch (IOException e) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingExceptionStatus(Level.SEVERE, e, "IOEx- closed socket, E-board #" + deviceNo,
                            mainForm.getStatusTextField(), deviceNo);
                    gfinishConnection(E_Board, e,  
                            "server closed socket for ", 
                            deviceNo,
                            mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                            socket,
                            mainForm.getMessageTextArea(), 
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                            mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                            mainForm.isSHUT_DOWN()
                    );                       
                }
            } catch (Exception e2) {
                logParkingExceptionStatus(Level.SEVERE, e2, 
                        e2.getMessage() + "server- closed socket forE-Board #" + deviceNo,
                        mainForm.getStatusTextField(), deviceNo);
                gfinishConnection(E_Board, e2,  
                        "E-Board manager Excp", 
                        deviceNo,
                        mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                        socket,
                        mainForm.getMessageTextArea(), 
                        mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                        mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                        mainForm.isSHUT_DOWN()
                );                   
            }
            //</editor-fold>

            if (mainForm.tolerance[E_Board.ordinal()][deviceNo].getLevel() <= 0) {
                gfinishConnection(E_Board, null,  
                        "LED: tolerance depleted for", 
                        deviceNo,
                        mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                        socket,
                        mainForm.getMessageTextArea(), 
                        mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                        mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                        mainForm.isSHUT_DOWN()
                );                   
            }
        }
    }

    /**
     * stops serving a gate bar.
     */
    @Override
    public void stopOperation(String reason) {
        gfinishConnection(E_Board, null,  
                reason, 
                deviceNo,
                mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo],
                socket,
                mainForm.getMessageTextArea(), 
                mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo],
                mainForm.getConnectDeviceTimer()[E_Board.ordinal()][deviceNo],
                mainForm.isSHUT_DOWN()
        );           
        interrupt();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * closes socket connection to a gate bar.
     * 
     * before closing the socket, it cancels any existing relevant tasks.
     */
//    @Override
//    public void finishConnection(Exception e, String description, byte gateNo) 
//    {
//        synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][gateNo]) 
//        {
//            if (0 < gateNo && gateNo <= gateCount) 
//            {
//                if (isConnected(socket)) 
//                {   
//                    String msg =  "E-Board #" + gateNo;
//
//                    addMessageLine(mainForm.getMessageTextArea(), "  ------" +  msg + " disconnected");
//                    logParkingException(Level.INFO, e, description + " " + msg);
//
//                    mainForm.getSockConnStat()[E_Board.ordinal()][gateNo].
//                            recordSocketDisconnection(System.currentTimeMillis());
//                    closeSocket(getSocket(), "while gate bar socket closing");
//                    socket = null;
//                }
//            } else {
//                System.out.println("this never ever gateNo");
//            }
//        }
//            
//        if (mainForm.getConnectDeviceTimer()[E_Board.ordinal()][gateNo] != null) {
//            if (!mainForm.isSHUT_DOWN()) {
//                mainForm.getConnectDeviceTimer()[E_Board.ordinal()][gateNo].reRunOnce();
//                addMessageLine(mainForm.getMessageTextArea(), "Trying to connect to Camera #" + gateNo);
//            }
//        }
//    }

    public static void sendEBoardDefaultSetting(ControlGUI mainForm, byte deviceNo, EBD_Row row) {
        if (! mainForm.getSendEBDmsgTimer()[deviceNo][row.ordinal()].hasTask())
        {
            mainForm.getSendEBDmsgTimer()[deviceNo][row.ordinal()].reschedule(
                        new SendEBDMessageTask(
                            mainForm, deviceNo, row, 
                            getEBDSimulatorDefaultMessage(deviceNo, row, --mainForm.msgSNs[deviceNo]),
//                            mainForm.getDefaultMessage(
//                                    deviceNo, row,
//                                    --mainForm.msgSNs[deviceNo]), 
                            mainForm.msgSNs[deviceNo]));
        }    
    }

    @Override
    public boolean isNeverConnected() {
        return neverConnected;
    }

    @Override
    public void showDefaultMessage() {
        sendEBoardDefaultSetting(mainForm, deviceNo, EBD_Row.TOP);
        sendEBoardDefaultSetting(mainForm, deviceNo, EBD_Row.BOTTOM);
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
