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
package com.osparking.osparking;

import static com.osparking.osparking.ControlGUI.getGatePanel;
import static com.osparking.osparking.ControlGUI.showImage;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Open Source Parking Inc.
 */
class KeyPressedEventHandler extends KeyAdapter {
    int gateNo = 0; 
    
    public KeyPressedEventHandler(int gateNo) {
        this.gateNo = gateNo;    
    }
    
    public void keyReleased(KeyEvent ke){
        // when the key is either keyDown, keyUp, keyPgDn, keyPgUp
        // and current row is valid, then change image label for it.
        int currIndex = getGatePanel().getEntryList(gateNo).getSelectedIndex() ;
        
        if ((ke.getKeyCode() == KeyEvent.VK_DOWN ||
                ke.getKeyCode() == KeyEvent.VK_UP ||
                ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                ke.getKeyCode() == KeyEvent.VK_PAGE_UP) 
                && currIndex >= 0
                ) 
        {
            ke.consume();
            showImage( gateNo);      
        }
    }      
}
