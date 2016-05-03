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

import java.awt.image.BufferedImage;

/**
 *
 * @author Open Source Parking Inc.
 */
public class CameraMessage {
    private String imagePath;
    private String carNumber;
    private BufferedImage  bufferedImg;
    
    public CameraMessage(String imagePath,  String carNumber, BufferedImage bufferedImg)
    {
        this.imagePath = imagePath;
        this.carNumber = carNumber;
        this.bufferedImg = bufferedImg;
    }

    /**
     * @return the imagePath
     */
    public String getFilename() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the carNumber
     */
    public String getCarNumber() {
        return carNumber;
    }

    /**
     * @param carNumber the carNumber to set
     */
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    /**
     * @return the bufferedImg
     */
    public BufferedImage getBufferedImg() {
        return bufferedImg;
    }

    /**
     * @param bufferedImg the bufferedImg to set
     */
    public void setBufferedImg(BufferedImage bufferedImg) {
        this.bufferedImg = bufferedImg;
    }
}
