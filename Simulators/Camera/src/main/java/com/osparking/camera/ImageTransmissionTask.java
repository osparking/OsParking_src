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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import java.util.logging.Level;
import static com.osparking.global.Globals.isConnected;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.CarImage;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ImageTransmissionTask implements Runnable {
    CameraGUI cameraGUI;
//    Socket managerSocket;
    int genSeqNo;
    
    byte[] msgCode = new byte[]{(byte)MsgCode.CarImage.ordinal()}; //length:1
    byte[] imgIDarr = null;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] imgSizeArr = null;
    private int sendCount = 0;
    CameraReader reader = null;
    byte[] imageMessageBytes = null;

    
    public ImageTransmissionTask(CameraGUI cameraGUI, int genSeqNo, String filename) {
        this.cameraGUI = cameraGUI;
        this.reader = (CameraReader) cameraGUI.getReader();
        this.genSeqNo = genSeqNo;
        
        imgIDarr = ByteBuffer.allocate(4).putInt(genSeqNo).array();
        
        try {
            // reads the image file and create a byte array for the image to send
            BufferedImage image = ImageIO.
                    read(getClass().getResourceAsStream("/" + filename));

            ImageIO.write(image, "jpg", byteArrayOutputStream);
            image = null;
        } catch (IOException e) {
            logParkingException(Level.SEVERE, e, "Image reading failure from SSD", cameraGUI.getID());
            cameraGUI.getImageTransmissionTimer().cancelTask();
        }

        imgSizeArr = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        int imageSZ = byteArrayOutputStream.size();
        imageMessageBytes = new byte[9 + imageSZ];
        
        System.out.println("file: " + filename + ", size: " + imageSZ);
        
        imageMessageBytes[0] = (byte)CarImage.ordinal();
        System.arraycopy(imgIDarr, 0, imageMessageBytes, 1, 4);
        System.arraycopy(imgSizeArr, 0, imageMessageBytes, 5, 4);
        System.arraycopy(byteArrayOutputStream.toByteArray(), 0, imageMessageBytes, 9, imageSZ);
    }
    
    @Override
    public void run() { 
        synchronized(cameraGUI.getSocketMUTEX()) {
            if (! isConnected(reader.getManagerSocket())) {
                try {
                    System.out.println("before image trans");
                    cameraGUI.getSocketMUTEX().wait();
                    System.out.println("before image trans");
                } catch (InterruptedException ex) {
                    logParkingException(Level.SEVERE, ex, "wait socket connection", cameraGUI.getID());
                }
            }
            try {
                synchronized(cameraGUI.getImageTransmissionTimer()) {
                    reader.getManagerSocket().getOutputStream().write(imageMessageBytes);
                    ++sendCount;
                }
            } catch (IOException e) {
                if (cameraGUI.getReader().getManagerSocket() != null) {
                    cameraGUI.getReader().disconnectSocket(e, "while sending car image"); 
                }
            }
        }
    }
    
    /**
     * supplies this open command resent count.
     * used to check the system performance in case of network error/delay.
     * 
     * @return the sendCount
     */
    public int getResendCount() {
        return sendCount - 1;  // first send shouldn't be counted
    }    
}