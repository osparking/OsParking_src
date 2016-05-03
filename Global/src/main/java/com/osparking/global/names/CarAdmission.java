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

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Open Source Parking Inc.
 */
public class CarAdmission {
    private String msgLine;
    private long arrSeqNo;

    public CarAdmission(String msgLine,  long arrSeqNo)
    {
        this.msgLine = msgLine;
        this.arrSeqNo = arrSeqNo;
    }
    
    @Override
    public String toString()
    {
        return getEntryLine();
    }

    /**
     * @return the msgLine
     */
    public String getEntryLine() {
        return msgLine;
    }

    /**
     * @param entryLine the msgLine to set
     */
    public void setEntryLine(String entryLine) {
        this.msgLine = entryLine;
    }

    /**
     * @return the arrSseqNo
     */
    public long getArrSeqNo() {
        return arrSeqNo;
    }

    /**
     * @param arrSeqNo the arrSseqNo to set
     */
    public void setArrSeqNo(long arrSeqNo) {
        this.arrSeqNo = arrSeqNo;
    }
}
