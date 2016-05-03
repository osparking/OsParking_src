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
package com.osparking.camera.stat;

import static com.osparking.global.Globals.ERROR_RATE;
import static com.osparking.global.Globals.getFormattedRealNumber;

/**
 *
 * @author Open Source Parking Inc.
 */
public class CommandPerformance {
    
    /**
     * gate open command statistics
     */
    String commandName;
    int commandCount = 0;
    int commAckDelayTot = 0;
    int commAckDelayMax = 0;
    int commResendCntTot = 0;
    
    /**
     * socket connection break statistics
     */
    
    private long recentCloseTimeMs = 0L; 
    
    /**
     * 
     * @param commandName 
     */
    public CommandPerformance(String commandName) {
        this.commandName = commandName;
    }
    
    public void addCommandPerformance (int delayMs, int resendCnt) {
        commandCount++;
        commAckDelayTot += delayMs;
        if (commAckDelayMax < delayMs)
            commAckDelayMax = delayMs;
        commResendCntTot += resendCnt;
    }
    
    /**
     * Creates a description on the command execution.
     * 
     * @param errorInserted says if artificial error inserted or not
     * @return command execution performance description string
     */
    public String getPerformanceDescription(boolean errorInserted) {
        StringBuilder sb = new StringBuilder();
        
        if (commandCount == 0) {
            sb.append("no Open command statistics");
        } else {
            sb.append("    " + commandName);
            sb.append(" generated: ");
            sb.append(commandCount);

            if (errorInserted) {
                sb.append(" (error: ");
                sb.append(getFormattedRealNumber(ERROR_RATE, 2));
                sb.append(")");
            }
            sb.append(System.lineSeparator());

            sb.append("    ACK delay(ms)--avg: ");
            float countF = (float)commandCount;
            sb.append(getFormattedRealNumber(commAckDelayTot/countF, 1));
            sb.append(", max: " + commAckDelayMax);
            sb.append(System.lineSeparator());

            sb.append("    Re-transmission/" + commandName+  ": ");
            sb.append(getFormattedRealNumber(commResendCntTot/countF, 2));
            //sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
    
    /**
     * Check if at least one data is stored.
     * @return true, if one or more data is stored; false, otherwise
     */
    public boolean hasData() {
        if (commandCount > 0) 
            return true;
        else
            return false;
    }
}
