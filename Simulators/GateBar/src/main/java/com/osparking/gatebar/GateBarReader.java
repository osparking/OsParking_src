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

import static com.osparking.deviceglobal.DeviceGlobals.sayIamHere;
import static com.osparking.deviceglobal.DeviceGlobals.showCheckDeviceTypeDialog;
import static com.osparking.global.CommonData.appendOdsLine;
import static com.osparking.global.CommonData.checkOdsExistance;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.DEBUG_FLAG;
import static com.osparking.global.Globals.closeSocket;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.noArtificialErrorInserted;
import static com.osparking.global.Globals.timeFormat;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_BAR_LABEL;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.AreYouThere;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import static com.osparking.global.names.OSP_enums.MsgCode.Open;
import static com.osparking.global.names.OSP_enums.MsgCode.Open_ACK;

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
    int barID;

    public GateBarReader(GateBarGUI gateBarGUI) {
        this.gateBarGUI = gateBarGUI;
        barID = gateBarGUI.getID();
        
        if (DEBUG_FLAG) {
            checkOdsExistance("_GateOpen_", barID, 
                    " Gate Bar", "Received Open Command IDs", 
                    this.gateBarGUI.getCriticalInfoTextField(),
                    this.gateBarGUI.odsFile, this.gateBarGUI.model);
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
                            sayIamHere(gateBarGUI);
                            break;

                        case Open:
                            //<editor-fold defaultstate="collapsed" desc="--Open the gate bar">  
                            byte[] messageOpenAck = new byte[5];

                            getManagerSocket().getInputStream().read(restMsg);
                            if (noArtificialErrorInserted(gateBarGUI.getErrorCheckBox())) {
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
                                    gateBarGUI.orderOpenGate(delayMS);
                                    if (DEBUG_FLAG) {
                                        checkOdsExistance("_GateOpen_", barID, 
                                                " Gate Bar", "Received Open Command IDs", 
                                                gateBarGUI.getCriticalInfoTextField(),
                                                gateBarGUI.odsFile, gateBarGUI.model);
                                        appendOdsLine(gateBarGUI.odsFile[0], 
                                                Integer.toString(cmdID), 
                                                Integer.toString(gateBarGUI.prevCommandID),
                                                gateBarGUI.getCriticalInfoTextField());
                                    }
                                    gateBarGUI.prevCommandID = cmdID;
                                }
                                //</editor-fold>                                        
                            }
                            //</editor-fold>
                            break;

                        default:
                            showCheckDeviceTypeDialog(GATE_BAR_LABEL.getContent(), barID, msgCode);
                            throw new Exception ("unexpected message code: " + MsgCode.values()[msgCode]); 
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
                disconnectSocket(null, "Manager isn't reaching at " + GateBar + " #" + barID);
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

    public void disconnectSocket(Exception e, String reason) {
        logParkingException(Level.INFO, e, reason, barID);
        synchronized(gateBarGUI.getSocketMUTEX()) {
            closeSocket(getManagerSocket(), "manager socket closing");
            setManagerSocket(null);
        }       
        gateBarGUI.getCriticalInfoTextField().setText(timeFormat.format(new Date()) + "--OsParking disconnected");
        if (! gateBarGUI.isSHUT_DOWN())
            gateBarGUI.getAcceptManagerTimer().reRunOnce();
    }        

    @Override
    public void stopOperation(String cause) {
        disconnectSocket(null, cause);
        setSHUT_DOWN(true);        
    }
}
