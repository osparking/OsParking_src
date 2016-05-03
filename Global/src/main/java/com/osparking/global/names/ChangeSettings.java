/*
<<<<<<< HEAD
 * Copyright (C) 2015 Open Source Parking Inc.
=======
 * Copyright (C) 2015 YongSeok
>>>>>>> osparking/master
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

import java.util.Set;
import java.awt.Component;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ChangeSettings {
    public static void changeEnabled_of_SaveCancelButtons
        (JButton saveBtn, JButton cancelBtn, JButton closeBtn, int size) 
    {
        if (size > 0){
            saveBtn.setEnabled(true);
            cancelBtn.setEnabled(true);        
            closeBtn.setEnabled(false);
        }else{
            saveBtn.setEnabled(false);
            cancelBtn.setEnabled(false);        
            closeBtn.setEnabled(true);
        }  
    } 
    
    public static void changeStatus_Manager
        (final JButton saveBtn, final JButton cancelBtn, final JButton closeBtn,
            final HashSet<Component> changedControls, final Component compo, 
            final boolean current, final boolean original)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (current == original) {
                    // remove from changedControls set
                    changedControls.remove(compo);
                } else {
                    // add to the changedControls set
                    changedControls.add(compo);
                }
                changeEnabled_of_SaveCancelButtons(saveBtn, cancelBtn, closeBtn, changedControls.size());
            }
        });
    }
    
    public static void changeStatus_Manager
        (final JButton saveBtn, final JButton cancelBtn, final JButton closeBtn,
            final HashSet<Component> changedControls, final Component compo, 
            final int current, final int original)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (current == original) {
                    // remove from changedControls set
                    changedControls.remove(compo);
                } else {
                    // add to the changedControls set
                    changedControls.add(compo);
                }
                changeEnabled_of_SaveCancelButtons(saveBtn, cancelBtn, closeBtn, changedControls.size());
            }
        });
    }
    
    public static void changeStatus_Manager(final JButton saveBtn, final JButton cancelBtn, final JButton closeBtn,
            final HashSet<Component> changedControls, final Component name, final String current, final String original){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (current.equals(original)) {
                    // remove from changedControls set
                    changedControls.remove(name);
                } else {
                    // add to the changedControls set
                    changedControls.add(name);
                }
                changeEnabled_of_SaveCancelButtons(saveBtn, cancelBtn, closeBtn, changedControls.size());
            }
        });
    }
}
