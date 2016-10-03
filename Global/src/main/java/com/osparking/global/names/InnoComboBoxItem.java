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
package com.osparking.global.names;

/**
 * Korea(ROK) Patent Registered(No: 1016595460000)Technology Implementing 
 * Class Data Type for Combo Box Items.
 * Patent Registration Details:
 *      -Name: User Apparatus for displaying item using combobox and item display method
 *      -Registration Date: 2016. 09. 19
 *      -Patent no.: 1016595460000 (South Korea)
 * 
 * It can have multiple keys and labels in two arrays which are of same length.
 * @author Open Source Parking Inc.
 */
public class InnoComboBoxItem { // Inno stands for Innovative 
    private int[] keys;
    private String[] labels;
    
    public InnoComboBoxItem(int[] keys, String[] labels) {
        this.keys= keys;
        this.labels = labels;
    }
    
    @Override
    public String toString() {
        if (labels ==  null) 
            return null;
        else {
            StringBuilder sbuf = new StringBuilder(labels[0]);

            for (int idx = 1; idx < labels.length; idx++)
            {
                sbuf.append("-");
                sbuf.append(labels[idx]);
            }        

            return sbuf.toString();
        }
    }
    
    /**
     * Check the equality of this item with the given item.
     * @param rhs the given item to compare with this item
     * @return true when the two items have the same size and contents.
     */
    @Override
    public boolean equals(Object rhs) {
        
        if (rhs instanceof InnoComboBoxItem) {
            InnoComboBoxItem rightOne = (InnoComboBoxItem)rhs;
            if (rightOne.keys == null || rightOne.labels == null) {
                return false;
            } else {
                if (rightOne.keys.length == this.keys.length 
                        && rightOne.labels.length == this.labels.length) 
                {
                    for (int idx = 0; idx < this.keys.length; idx++) {
                        if (rightOne.keys[idx] != this.keys[idx]) {
                            return false;
                        }
                    }
                    for (int idx = 0; idx < this.labels.length; idx++) {
                        if (!rightOne.labels[idx].equals(this.labels[idx])) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return labels.hashCode();
    }
    
    /**
     * Returns the key value associated with this item
     * @return the keyValue
     */
    public int[] getKeys() {
        return keys;
    }

    /**
     * @param keyValue the keyValue to set
     */
    public void setKeys(int[] keys) {
        this.keys = keys;
    }

    /**
     * @return the label
     */
    public String[] getLabels() {
        return labels;
    }

    /**
     * @param title the label to set
     */
    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
