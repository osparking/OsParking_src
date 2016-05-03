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
package com.osparking.global.names;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author Open Source Parking Inc.
 */
public abstract class GatePanel extends javax.swing.JPanel{
    public abstract JLabel[] getCarPicLabels();

    public abstract JPanel getPanel_Gate(int gateNo);
    public abstract void resizeComponents(Dimension panelSize);
    public abstract void displaySizes();
    public abstract JList getEntryList(int gateNo);
    public abstract DefaultListModel getDefaultListModel(int gateNo);
    public abstract BufferedImage[] getGateImages();
    public abstract void setGateImage(byte gateNo, BufferedImage gateImage);
}
