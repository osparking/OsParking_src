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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import static com.osparking.global.Globals.PULSE_PERIOD;
import com.osparking.global.names.OSP_enums.OpLogLevel;

/**
 * Simulates an LPR(License Plate Recognition) camera shutter.
 * LPR camera pictures tags of cars entering parking lot.
 * It randomly determines whether a car appeared at the gate and selects one car image out of 6 sample
 * images.
 * @author Open Source Parking Inc.
 */

public class ImageGenerationTask extends TimerTask {
    //<editor-fold defaultstate="collapsed" desc="-- class variables">
    private CameraGUI cameraGUI;
    Random rand = null;
    
    /**
     * ID of this camera, determined by the camera GUI module.
     */
    public byte cameraID = 0;
    
    public boolean IP_ID_Verified = false;
    /**
     * ensures a ordered usage of the managerSocket which is used by two tasks simultaneously.
     */
    public static Object SocketMUTEX = new Object();
//    boolean SHUT_DOWN = false;

    /**
     * Bed on which camera sleeps and never sends car image to the manager while socket is disconnected.
     * ManagerSocketReader wakes the camera up when it sees ID_Ack sent from the manager.
     */
    public Object ID_confirmed = new Object(); // ID_confirmed is a portable bed brand.
    
    Object managerConnection = new Object();
        
    //</editor-fold>

    /**
     * A simulated LPR camera obect.
     * @param cameraGUI window frame by which this camera is represented
     * @param cameraID camera identifier which is usually same as the gate number
     */
    ImageGenerationTask(CameraGUI cameraGUI, byte cameraID) 
    {
        this.cameraGUI = cameraGUI;
        this.cameraID = cameraID;

        long seed = (new Date()).getTime();
        rand = new Random(seed + 157 + cameraID);
        
        // prepare a text file to log normal operation like uniquely creationed image ID
        if (DEBUG) {
            //<editor-fold desc="--prepare logger file to save car image ID">
            StringBuilder pathname = new StringBuilder();
            StringBuilder daySB = new StringBuilder();

            getPathAndDay("operation", pathname, daySB);

            // full path name of the today's text file for Open command ID logging
            String imageIDLogFilePathname = pathname + File.separator 
                    + daySB.toString() + "_Image_ID_" + cameraGUI.getID() + ".txt";
            try {
                cameraGUI.imageID_logFile = new FileWriter(imageIDLogFilePathname, false);
                cameraGUI.imageID_logFile.write("#" + cameraGUI.getID() + " Camera Generated Image IDs" 
                        + System.lineSeparator());
            } catch (IOException ex) {
                logParkingExceptionStatus(Level.SEVERE, ex, "image ID logging file preparation", 
                        cameraGUI.getCriticalInfoTextField(), 0);
            }
            //</editor-fold>
        }            
    }
    
    /**
     * main module of camera which sends an image of car arriving.
     * for a camera be able to send car image to the manager through a socket a few conditions need to be met.
     * They are:
     * 1. the socket(managerSocket) is connected.
     * 2. the camera is not selected to pause by the user using the pause button(pauseButton)
     * 3. Image_Sender timertask doing nothing at the moment(is idle) since camera can't handle more than
     *      one car simultaneously.
     * 4. finally, a car arrived by chance.
     */
    public void run()
    {    
        byte imageFileNo = 0;

        while (true) {

            if (cameraGUI.isSHUT_DOWN())
                return;
            
            if (!cameraGUI.isCameraPausing() // pause button not pressed
                    && ! cameraGUI.imageTransmissionTimer.hasTask()
                    && cameraGUI.imageID_Acked
                    && (rand.nextFloat() < 0.5f)
                    && cameraGUI.getReader() != null)
            {
                if (isConnected(cameraGUI.getReader().getManagerSocket())) {
                    try {
                        Thread.sleep(1000 * (rand.nextInt(18) + 2)); // 2 to 20 seconds of wait time
                    } catch (InterruptedException ie) {
                    }
                    // choose a car (image) ID randomly
                    imageFileNo = getNextCarNum(rand, imageFileNo);
                    cameraGUI.sendCarImage(imageFileNo, ++cameraGUI.generationSN);
                    if (DEBUG) {
                        Globals.logParkingOperation(OpLogLevel.EBDsettingsChange, 
                            "Generated image ID : " + Integer.toString(cameraGUI.generationSN), cameraGUI.getID());
                        saveImageIDsent(cameraGUI.generationSN);
                    }                    
                }
            }
            
            try {
                Thread.sleep(PULSE_PERIOD);
            } catch (InterruptedException ie) {
            }                    
        }
    }

    /**
     * @return the cameraGUI
     */
    public CameraGUI getCameraGUI() {
        return cameraGUI;
    }

    /**
     * @param cameraGUI the cameraGUI to set
     */
    public void setCameraGUI(CameraGUI cameraGUI) {
        this.cameraGUI = cameraGUI;
    }

    private Timer sendID_forSure = null;
    
    /**
     * @return the sendID_forSure
     */
    public Timer getSendID_forSure() {
        return sendID_forSure;
    }

    /**
     * @param sendID_forSure the sendID_forSure to set
     */
    public void setSendID_forSure(Timer sendID_forSure) {
        this.sendID_forSure = sendID_forSure;
    }              
    //</editor-fold>

    private void saveImageIDsent(int ackedImgID) {
        try {
            cameraGUI.imageID_logFile.write(Integer.toString(ackedImgID) + System.lineSeparator());
            cameraGUI.imageID_logFile.flush();
        } catch (FileNotFoundException ex1) {
            logParkingExceptionStatus(Level.SEVERE, ex1, "open command ID logging module",
                    cameraGUI.getCriticalInfoTextField(), 0);
        } catch (IOException ex2) {
            logParkingExceptionStatus(Level.SEVERE, ex2, "open command ID logging module",
                    cameraGUI.getCriticalInfoTextField(), 0);
        }
    }    
}
