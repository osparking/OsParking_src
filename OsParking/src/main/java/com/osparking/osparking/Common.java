/*
 * Copyright (C) 2016 Jongbum Park
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
package com.osparking.osparking;

import static com.osparking.global.Globals.LIST_HEIGHT_MIN;
import static com.osparking.global.Globals.createStretchedIcon;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.restAreaImage;
import static com.osparking.global.Globals.setComponentSize;
import static com.osparking.global.names.DB_Access.PIC_HEIGHT;
import static com.osparking.global.names.DB_Access.PIC_WIDTH;
import static com.osparking.global.names.DB_Access.gateCount;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.imageio.ImageIO;

/**
 *
 * @author Jongbum Park
 */
public class Common {
    public static final int panelMargin = 3;
    public static final int imageMargin = 28;    
    
    /**
     * Adjust Dimension of Panel for a specific Gate
     * @param gatePanel panel for every gates monitored
     * @param gatesPanelSize dimension of the panel containing all gates
     */
    public static void fixPanelDimemsion(GatePanel gatePanel, Dimension gatesPanelSize) {
        int picWidthNew = 0, picHeightNew = 0;

        // <editor-fold defaultstate="collapsed" desc="-- Adjust sizes of panels for each gate">                          
        // Limit picture label width to max pixel count of image.
        int gateWidth = (gatesPanelSize.width - panelMargin * gateCount)/gateCount;
        
        if (gateWidth > PIC_WIDTH + imageMargin) {
            gateWidth = PIC_WIDTH + imageMargin;
        } 

        picWidthNew = gateWidth - imageMargin;
        picHeightNew = picWidthNew * PIC_HEIGHT / PIC_WIDTH;
        
        // When car entry list box height isn't enough, reduce picture height and 
        // propagate the change to the width.
        if (gatesPanelSize.height - picHeightNew - panelMargin - imageMargin < LIST_HEIGHT_MIN) {
            picHeightNew = gatesPanelSize.height - LIST_HEIGHT_MIN - panelMargin - imageMargin;
            picWidthNew =  picHeightNew * PIC_WIDTH / PIC_HEIGHT;
            gateWidth = picWidthNew + imageMargin;
        }
        
        for (int gate = 1; gate <= gateCount; gate++) {
            // A tall panel for one gate which includes car image and arrival list
            setComponentSize(gatePanel.getPanel_Gate(gate), 
                    new Dimension(gateWidth, gatesPanelSize.height));
            // Label for one car image display
            setComponentSize(gatePanel.getCarPicLabels()[gate], 
                    new Dimension(picWidthNew, picHeightNew));
            System.out.println("WIDTH panel: " + gateWidth
                    + ", picture: " + picWidthNew);
        }
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="-- Adjust sizes of right margin panel">                                  
        // Calculate right margin area width
        int panWidth = gatePanel.getPanel_Gate(1).getWidth();
        int width = gatesPanelSize.width - (panWidth + panelMargin) * gateCount;
        System.out.print("Total: " + gatesPanelSize.width + ", gatePan: " + panWidth);
        
        try {
            System.out.println(", margin: " + width);
            if (width >= 0) {
                setComponentSize(gatePanel.getMarginLabel(), 
                        new Dimension(width, gatePanel.getSize().height));
                BufferedImage marginImage 
                        = ImageIO.read(gatePanel.getClass().getResourceAsStream(restAreaImage));
                gatePanel.getMarginLabel().setIcon(
                        createStretchedIcon(gatePanel.getMarginLabel().getSize(), marginImage, true));
                gatePanel.getMarginLabel().revalidate();
                gatePanel.add(gatePanel.getMarginLabel());
                System.out.println("yes margin image");
            } else {
                width = 0;
                setComponentSize(gatePanel.getMarginLabel(), 
                        new Dimension(width, gatePanel.getSize().height));
                gatePanel.getMarginLabel().setIcon(null);
                System.out.println("no margin image");
            }
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "(Margin area updater)");
        }
        //</editor-fold>
    }    
    

    /**
     * Adjust Rest Area Picture Size
     * @param gatesPanelSize 
     */
    public static void resizeComponents(GatePanel gPanel, Dimension gatesPanelSize) {
        setComponentSize(gPanel, gatesPanelSize); // Whole gates panel      
        gPanel.revalidate();
        gatesPanelSize = gPanel.getSize();        
        fixPanelDimemsion(gPanel, gatesPanelSize);
    }    
}