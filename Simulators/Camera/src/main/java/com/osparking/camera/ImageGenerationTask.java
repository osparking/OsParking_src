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
package com.osparking.camera;

import static com.osparking.global.CommonData.appendOdsLine;
import static com.osparking.global.CommonData.checkOdsExistance;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import static com.osparking.global.Globals.*;
import static com.osparking.global.Globals.PULSE_PERIOD;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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

    /**
     * Bed on which camera sleeps and never sends car image to the manager while socket is disconnected.
     * ManagerSocketReader wakes the camera up when it sees ID_Ack sent from the manager.
     */
    public Object ID_confirmed = new Object(); // ID_confirmed is a portable bed brand.
    Object managerConnection = new Object();
    
    String[] columns = new String[] {"Image ID", ""};
    TableModel model = new DefaultTableModel(null, columns);       
    File[] odsFile = new File[1];
    
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
            
            if (cameraGUI.isCameraRunning() // pause button not pressed
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
                    while (cameraGUI.isOsBusy()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                    // choose a car (image) ID randomly
                    cameraGUI.setOsBusy(true);
                    imageFileNo = getNextCarNum(rand, imageFileNo);
                    cameraGUI.sendCarImage(imageFileNo, ++cameraGUI.generationSN);
                    if (DEBUG_FLAG) {
                        // prepare a text file to log normal operation like uniquely creationed image ID
                        checkOdsExistance("_Image_ID_", cameraID, " Camera", "Generated Image IDs",
                                cameraGUI.getCriticalInfoTextField(), odsFile, model);                        
                        appendOdsLine(odsFile[0], 
                                Integer.toString(cameraGUI.generationSN),
                                cameraGUI.getCriticalInfoTextField());
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


}
