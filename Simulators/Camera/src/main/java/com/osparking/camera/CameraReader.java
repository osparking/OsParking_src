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
package com.osparking.camera;

import static com.osparking.global.Globals.addMessageLine;
import static com.osparking.global.Globals.closeSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import com.osparking.global.names.DeviceReader;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.noArtificialErrorInserted;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.AreYouThere;
import static com.osparking.global.names.OSP_enums.MsgCode.IAmHere;
import static com.osparking.global.names.OSP_enums.MsgCode.Img_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import java.util.logging.Level;

/**
 * thread dedicated to read the socket input stream from the manager.
 * 
 * @author Open Source Parking Inc.
 */
public class CameraReader extends Thread implements DeviceReader{
    CameraGUI cameraGUI = null;
    private Socket managerSocket = null; // socket that connects to the manager program
    private boolean SHUT_DOWN = false;
    byte [] cmdIDarr = new byte[4];
    byte [] fiveByteArr =new byte[5];   
    
    int prevCommandID = 0;
    int seq = 0;
    static boolean justBooted = true;
    
    public CameraReader(CameraGUI cameraGUI) {
        setName("Camera" + cameraGUI.getID()+ "_Reader");
        this.cameraGUI = cameraGUI;     
    }

    public void run() {

        while (true)
        {
            try
            {
                if (isSHUT_DOWN()) {
                    if (cameraGUI.imageID_logFile != null) {
                        //<editor-fold desc="-- Close log file">
                        try {
                            cameraGUI.imageID_logFile.close();
                            cameraGUI.imageID_logFile = null;
                        } catch (IOException e) {}
                        //</editor-fold>
                    }                
                    return;
                }            

                int msgCode = -2;
                //<editor-fold desc="-- Read data from the connected socket">
                synchronized(cameraGUI.getSocketMUTEX()) 
                {
                    if (! isConnected(getManagerSocket())) {
                        cameraGUI.getSocketMUTEX().wait();
                    }
                }
                
                if (justBooted) {
                    managerSocket.getOutputStream().write(JustBooted.ordinal());
                    justBooted = false;
                }
                        
                msgCode = getManagerSocket().getInputStream().read();

                if (msgCode == -1) {
                    disconnectSocket(null, "End of stream reached");
                    continue;
                } else if (msgCode < -1 || MsgCode.values().length <= msgCode) {
                    disconnectSocket(null, "Code out of range");
                    continue;  
                }
                //</editor-fold>

                synchronized(cameraGUI.getSocketMUTEX()) 
                {
                    //<editor-fold desc="-- Read rest bytes of message and process them">
                    switch (MsgCode.values()[msgCode]) 
                    {
                        case AreYouThere:
                            //<editor-fold defaultstate="collapsed" desc="--Handle by sending IamHere">                        
                            if (noArtificialErrorInserted(cameraGUI.errorCheckBox)) 
                            {
                                cameraGUI.getTolerance().assignMAX();
                                if (isConnected(getManagerSocket())) {
                                    getManagerSocket().getOutputStream().write(IAmHere.ordinal());
                                } 
                            }
                            //</editor-fold>
                            break;

                        case Img_ACK:
                            //<editor-fold defaultstate="collapsed" desc="-- Image arrival confirmed">                        
                            byte [] ackedIDarr = new byte[4];

                            if (isConnected(getManagerSocket())) {
                                getManagerSocket().getInputStream().read(ackedIDarr);  
                            } else {
                                System.out.println("unexpected/rare execution path 12");
                            }

                            if (noArtificialErrorInserted(cameraGUI.errorCheckBox))
                            {
                                // read image ID from the socket and compare it with the local image ID
                                int ackedID = ByteBuffer.wrap(ackedIDarr).getInt();                                     

                                // time the manager taken until it gets ACK for an open command issued                                    
                                int ackDelay = (int)(System.currentTimeMillis() - cameraGUI.imageGenerationTimeMs);

                                int resendCnt = ((ImageTransmissionTask) 
                                        cameraGUI.getImageTransmissionTimer().getParkingTask()).getResendCount();

                                // Handle Img_ACK message properly
                                synchronized ( cameraGUI.getImageTransmissionTimer()) 
                                {
                                    if (! cameraGUI.imageID_Acked && ackedID == cameraGUI.generationSN)
                                    {
                                        cameraGUI.imagePerf.addCommandPerformance(ackDelay, resendCnt);
                                        /**
                                         * manager acked the reception of an image currently a timer keeps sending.
                                         * so, let the timer task stop what it is doing now.
                                         */
                                        cameraGUI.imageID_Acked = true;
                                        cameraGUI.getImageTransmissionTimer().cancelTask();
                                    }
                                }
                            }
                            //</editor-fold>
                            break;

                        default:
                            cameraGUI.getCriticalInfoTextField().setText("unexpected message code");
                            throw new Exception ("unexpected message code: " + MsgCode.values()[msgCode]); 
                    }
                    //</editor-fold>
                }
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ie) {
                disconnectSocket(ie, "while waiting socket connection");
            } catch(IOException e) {
                disconnectSocket(e,  "camera reader saw IO-Exception"); 
            } catch (Exception e) {
                disconnectSocket(e,  "camera reader saw Exception"); 
            }
            //</editor-fold>
                    
            if (isConnected(getManagerSocket()) && cameraGUI.getTolerance().getLevel() < 0 ) {
                disconnectSocket(null, "Manager isn't reaching at " + Camera + " #" + cameraGUI.getID());
            }
        }
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

    @Override
    public void stopOperation(String cause) {
        disconnectSocket(null, cause);
        setSHUT_DOWN(true);        
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
    
//    public synchronized void disconnectSocket(Exception e, String reason) {
    public void disconnectSocket(Exception e, String reason) {
        
        if (getManagerSocket() == null)
            return;
        logParkingException(Level.INFO, e, reason, cameraGUI.getID());
        synchronized(cameraGUI.getSocketMUTEX()) {
            addMessageLine(cameraGUI.getMessageTextArea(), 
                    "manager disconnected" + System.lineSeparator());
            closeSocket(getManagerSocket(), "manager socket closing");
            setManagerSocket(null);
        }
        
        if (! cameraGUI.isSHUT_DOWN())
            cameraGUI.getAcceptManagerTimer().reRunOnce();
    }        
}
