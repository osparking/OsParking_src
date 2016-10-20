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
package com.osparking.vehicle;

import com.osparking.global.names.ControlEnums;
import com.osparking.global.names.ControlEnums.TableType;
import javax.swing.JTable;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class KorTable extends JTable {
    private ControlEnums.TableType tableType;
    private boolean cellEditable = false;
    
    public KorTable(TableType type)
    {
        tableType = type;
        getModel().addTableModelListener(this);        
    }    

    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        return cellEditable;
    }        
    
    /**
     * @return the tableType
     */
    public TableType getTableType() {
        return tableType;
    }

    /**
     * @param tableType the tableType to set
     */
    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    /**
     * @param cellEditable the cellEditable to set
     */
    public void setCellEditable(boolean cellEditable) {
        this.cellEditable = cellEditable;
    }    
}
