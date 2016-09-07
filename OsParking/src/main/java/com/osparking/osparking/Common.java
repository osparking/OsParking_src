/*
 * Copyright (C) 2016 Open Source Parking, Inc.(www.osparking.com)
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
import static com.osparking.global.names.OSP_enums.MsgCode.Os_Free;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import javax.imageio.ImageIO;

/**
 *
 * @author Open Source Parking, Inc.
 */
public class Common {
    public static final int gateInset = 2;
    public static final int imageH_inset = 24;
    public static final int entrySPV_inset = 28; // Car Entry ScrollPane Vertical Insets 
    public static final int RECENT_ROW_HEIGHT = 27; // Car Entry ScrollPane Vertical Insets 
    
    public static final byte[] os_FreeBytes = ByteBuffer.allocate(1).put((byte)Os_Free.ordinal()).array();
    
    /**
     * Adjust Dimension of Panel for a specific Gate
     * @param gatePanel panel for every gates monitored
     * @param gatesPanelSize dimension of the panel containing all gates
     */
    public static void fixPanelDimemsion(GatePanel gatePanel, Dimension gatesPanelSize, int maxHeight) 
    {
        int picWidth = 0, picHeight = 0;

        // <editor-fold defaultstate="collapsed" desc="-- Adjust sizes of panels for each gate">                          
        // Limit picture label width to max pixel count of image.
        int gateWidth = gatesPanelSize.width/gateCount - gateInset;
        
        if (gateWidth > PIC_WIDTH + imageH_inset) {
            gateWidth = PIC_WIDTH + imageH_inset;
        } 

        picWidth = gateWidth - entrySPV_inset;
        picHeight = picWidth * PIC_HEIGHT / PIC_WIDTH;
        
        // When car entry list box height isn't enough, reduce picture height and 
        // propagate the change to the width.
        int maxPicHeight = gatesPanelSize.height - entrySPV_inset - LIST_HEIGHT_MIN;
        if (picHeight > maxPicHeight) {
            picHeight = maxPicHeight;
            picWidth =  picHeight * PIC_WIDTH / PIC_HEIGHT;
            gateWidth = picWidth + imageH_inset;
        }
        
        for (int gate = 1; gate <= gateCount; gate++) {
            // A tall panel for one gate which includes car image and arrival list
            setComponentSize(gatePanel.getPanel_Gate(gate), 
                    new Dimension(gateWidth, gatesPanelSize.height));
            // Label for one car image display
            setComponentSize(gatePanel.getCarPicLabels()[gate], 
                    new Dimension(picWidth, picHeight));
        }
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="-- Adjust sizes of right margin panel">                                  
        // Calculate right margin area width
        int width = gatesPanelSize.width - gateWidth * gateCount;
        
        try {
            if (width >= 32) {
                setComponentSize(gatePanel.getMarginLabel(), 
                        new Dimension(width - 2, gatePanel.getSize().height));
                BufferedImage marginImage 
                        = ImageIO.read(gatePanel.getClass().getResourceAsStream(restAreaImage));
                int w = gatePanel.getMarginLabel().getSize().width;
                int h = gatePanel.getMarginLabel().getSize().height;
                int side = (w > h ? h / 2 : w / 2);
                if (side > maxHeight) {
                    side = maxHeight;
                }
                if (side >= 32) {
                    gatePanel.getMarginLabel().setIcon(
                            createStretchedIcon(new Dimension(side, side), marginImage, true));
                    gatePanel.add(gatePanel.getMarginLabel());
                }
            } else {
                setComponentSize(gatePanel.getMarginLabel(), 
                        new Dimension(width, gatePanel.getSize().height));
                gatePanel.getMarginLabel().setIcon(null);
            }
            gatePanel.getMarginLabel().revalidate();
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "(Margin area updater)");
        }
        //</editor-fold>
    }     
}