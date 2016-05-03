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

/**
 * This class is defined for the items of combo boxes when that have different 
 * face values(of a String type) from their associated value(of an Object type).
 * @author Open Source Parking Inc.
 */

// <editor-fold defaultstate="collapsed" desc="-- ComboBox Item Class Definition ">                           
public class ConvComboBoxItem { // Conv stands for Conventional 
    private Object value;
    private String label;
    
    public ConvComboBoxItem(Object value, String label) {
        this.value= value;
        this.label = label;
    }
    
    @Override
    public String toString() {
        return getLabel();
    }
    
    @Override
    public boolean equals(Object rhs) {
        boolean result = false;
        try {
            if (rhs != null && getLabel().equals(((ConvComboBoxItem)rhs).getLabel())) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println("exception occurred");
        } finally {
            return result;
        }
    }

    @Override
    public int hashCode()
    {
        return label.hashCode();
    }
    
    /**
     * Returns the key value associated with this item
     * @return the keyValue
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param keyValue the keyValue to set
     */
    public void setKeyValue(int keyValue) {
        this.value = keyValue;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param title the label to set
     */
    public void setTitle(String title) {
        this.label = title;
    }
}
//</editor-fold>
