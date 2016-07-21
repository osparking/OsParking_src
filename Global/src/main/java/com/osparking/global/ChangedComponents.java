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
package com.osparking.global;

import java.awt.Component;
import java.util.HashSet;
import javax.swing.JButton;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class ChangedComponents {
    private HashSet<Component> changedControls = new HashSet<Component>();    
    
    JButton theButton;
    
    public ChangedComponents(JButton theButton) {
        this.theButton = theButton;
    }
    
    public boolean remove(Component compo) {
        if (changedControls.size() == 0) {
            theButton.setEnabled(false);
        }
        return changedControls.remove(compo);
    }
    
    public boolean add(Component compo) {
        theButton.setEnabled(true);
        return changedControls.add(compo);
    }
}
