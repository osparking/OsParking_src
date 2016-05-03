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
package com.osparking.gatebar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import com.osparking.global.names.DeviceReader;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.GENERAL_DEVICE;
import static com.osparking.global.Globals.closeSocket;
import static com.osparking.global.Globals.getPathAndDay;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingExceptionStatus;
import static com.osparking.global.Globals.noArtificialErrorInserted;
import static com.osparking.global.Globals.timeFormat;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.AreYouThere;
import static com.osparking.global.names.OSP_enums.MsgCode.IAmHere;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import static com.osparking.global.names.OSP_enums.MsgCode.Open;
import static com.osparking.global.names.OSP_enums.MsgCode.Open_ACK;
import com.osparking.global.names.OSP_enums.OpLogLevel;

/**
 *
 * @author Open Source Parking Inc.
 */
public class GateBarReader extends Thread implements DeviceReader {
    GateBarGUI gateBarGUI = null;
    private Socket managerSocket = null; // socket that connects to the manager program
    private boolean SHUT_DOWN = false;
    byte [] restMsg = new byte[8];
    
    int seq = 0;
    public FileWriter logFileWriter = null; 
    static boolean justBooted = true;

    public GateBarReader(GateBarGUI gateBarGUI) {
        this.gateBarGUI = gateBarGUI;

        if (DEBUG) {
            //<editor-fold desc="-- Create file for 'E-Board display interrupt' message Sequence Number logging">
            StringBuilder pathname = new StringBuilder();
            StringBuilder daySB = new StringBuilder();

            getPathAndDay("operation", pathname, daySB);

            // full path name of the today's text file for gate Open command ID logging
            String operationLogFilePathname = pathname + File.separator 
                    + daySB.toString() + "_GateOpen_" + gateBarGUI.getID() + ".txt";
            try {
                logFileWriter = new FileWriter(operationLogFilePathname, false); 
                logFileWriter.write("#" + gateBarGUI.getID() + " Gate Bar Received Open Command IDs"
                        + System.lineSeparator());
                logFileWriter.write("<current> < previous>" + System.lineSeparator());
                logFileWriter.flush();
            } catch (FileNotFoundException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "while preparing log file", 
                        gateBarGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
            } catch (IOException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "while preparing log text file", 
                        gateBarGUI.getCriticalInfoTextField(), GENERAL_DEVICE);
            }  
            //</editor-fold>
        }        
    }
        
    public void run() {

        while (true)
        {
            try
            {
                if (isSHUT_DOWN()) {
                    //<editor-fold desc="-- Close Open command ID log file">
                    if (logFileWriter != null) {
                        try {
                            logFileWriter.close();
                            logFileWriter = null;
                        } catch (IOException e) {}
                    }     
                    //</editor-fold>
                    return;
                }    
                
                int msgCode = -2;
                //<editor-fold desc="-- Read first byte(message code) from the socket">
                synchronized(gateBarGUI.getSocketMUTEX()) 
                {
                    if (! isConnected(getManagerSocket())) {
                        gateBarGUI.getSocketMUTEX().wait();
                    }
                }
                
                if (justBooted) {
                    managerSocket.getOutputStream().write(JustBooted.ordinal());
                    justBooted = false;
                }                
                
                /**
                 * SocketTimeoutException will arise when no data on the socket during 1 second
                 */
                msgCode = getManagerSocket().getInputStream().read(); // waits for PULSE_PERIOD miliseconds

                if (msgCode == -1) {
                    disconnectSocket(null, "End of stream reached");
                    continue;
                } else if (msgCode < 0 || MsgCode.values().length <= msgCode) {
                    disconnectSocket(null, "Code out of range");
                    continue;
                }
                //</editor-fold>
                
                //<editor-fold desc="-- Process arrived messages">
                synchronized(gateBarGUI.getSocketMUTEX()) 
                {
                    switch (MsgCode.values()[msgCode]) 
                    {
                        case AreYouThere:
                            if (noArtificialErrorInserted(gateBarGUI.errorCheckBox)) 
                            {
                                getManagerSocket().getOutputStream().write(IAmHere.ordinal());
                                gateBarGUI.tolerance.assignMAX();
                            }
                            break;

                        case Open:
                            //<editor-fold defaultstate="collapsed" desc="--Open the gate bar">  
                            byte[] messageOpenAck = new byte[5];

                            getManagerSocket().getInputStream().read(restMsg);
                            if (noArtificialErrorInserted(gateBarGUI.errorCheckBox)) {
                                //<editor-fold desc="-- write Open_ack on the socket">
                                int cmdID = ByteBuffer.wrap(Arrays.copyOfRange(restMsg, 0, 4)).getInt();
                                int delayMS = ByteBuffer.wrap(Arrays.copyOfRange(restMsg, 4, 8)).getInt();

                                messageOpenAck[0] = (byte)Open_ACK.ordinal();
                                System.arraycopy(restMsg, 0, messageOpenAck, 1, 4);
                                
                                while (! isConnected(getManagerSocket())) {
                                    gateBarGUI.getSocketMUTEX().wait();
                                }                                   
                                getManagerSocket().getOutputStream().write(messageOpenAck);

                                if (cmdID != gateBarGUI.prevCommandID) {
                                    System.out.println("open gate cmd ID: " + cmdID);
                                    gateBarGUI.orderOpenGate(delayMS);
                                    if (DEBUG) {
                                        saveOpenCommandID(cmdID, gateBarGUI.prevCommandID);
                                    }
                                    gateBarGUI.prevCommandID = cmdID;
                                }
                                //</editor-fold>                                        
                            }
                            //</editor-fold>
                            break;

                        default:
                            gateBarGUI.criticalInfoTextField.setText("no planned message code");
                            throw new Exception ("unplanned message code: " + MsgCode.values()[msgCode]); 
                    }
                }
                //</editor-fold>
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ie) {
                disconnectSocket(ie, "while waiting socket connection");
            } catch(IOException e) {
                disconnectSocket(e,  "while reading manager socket"); 
            } catch(Exception e) {
                disconnectSocket(e,  "while handling manager message");
            }
            //</editor-fold>

            if (isConnected(managerSocket) && gateBarGUI.getTolerance().getLevel() < 0 ) {
                disconnectSocket(null, "Manager isn't reaching at " + GateBar + " #" + gateBarGUI.getID());
            }
        }
    }        

    /**
     * @return the SHUT_DOWN
     */
    public boolean isSHUT_DOWN() {
        return SHUT_DOWN;
    }

    /**
     * @param SHUT_DOWN the SHUT_DOWN to set
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

    private void saveOpenCommandID(int cmdID, int prevCommandID) throws IOException {

        String message = cmdID + " " + prevCommandID + System.lineSeparator();
        Globals.logParkingOperation(OpLogLevel.LogAlways, message, gateBarGUI.getID());
        
        try 
        {
            logFileWriter.write(message);
            logFileWriter.flush();
        } catch (FileNotFoundException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "while saving open command ID", 
                    gateBarGUI.criticalInfoTextField, gateBarGUI.getID());
        }
    }

    public void disconnectSocket(Exception e, String reason) {
        logParkingException(Level.INFO, e, reason, gateBarGUI.getID());
        synchronized(gateBarGUI.getSocketMUTEX()) {
            closeSocket(getManagerSocket(), "manager socket closing");
            setManagerSocket(null);
        }       
        gateBarGUI.getCriticalInfoTextField().setText(timeFormat.format(new Date()) + "--manager disconnected");
        if (! gateBarGUI.isSHUT_DOWN())
            gateBarGUI.getAcceptManagerTimer().reRunOnce();
    }        

    @Override
    public void stopOperation(String cause) {
        disconnectSocket(null, cause);
        setSHUT_DOWN(true);        
    }
}
