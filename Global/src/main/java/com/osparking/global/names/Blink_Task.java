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

import java.awt.Font;
import java.util.TimerTask;
import javax.swing.JTextField;

/**
 *
 * @author Open Source Parking Inc.
 */
/**
 * Display the gate bar GUI status field in a blinking fashion
 * @author Open Source Parking Inc.
 */
public class Blink_Task extends TimerTask {

    JTextField criticalInfoTextField = null;
    
    /**
     * used to give blinking effect to the text label of camera socket connection status 
     */
    boolean setHalfTransparent = false;
    String defaultFont ;
    int fontSize;
    
    /**
     * initializes this task with the main frame and a socket array
     * @param guiMain connection status is to be displayed bottom left corner on this frame
     * @param connectionLED sockets whose connection status to be displayed
     */
    public Blink_Task(JTextField theField, String msg) {
        this.criticalInfoTextField = theField;
        
        this.criticalInfoTextField.setText(msg);
        defaultFont = this.criticalInfoTextField.getFont().getFontName();
        fontSize = this.criticalInfoTextField.getFont().getSize();
    }
    
    /**
     * Periodically displays the connection status of each hardware component on 
     * each gate. 
     * One row of LED labels represents components for a gate.
     * To give Las Vegas sign effect, rotates transparency degree between adjacent rows.
     */
    public void run() {
        synchronized (criticalInfoTextField) {
            if (setHalfTransparent) {
                criticalInfoTextField.setFont(new Font(defaultFont, Font.PLAIN, fontSize));
            } else {
                criticalInfoTextField.setFont(new Font(defaultFont, Font.BOLD, fontSize));
            }
        }
        setHalfTransparent  =  ! setHalfTransparent;
    }
}