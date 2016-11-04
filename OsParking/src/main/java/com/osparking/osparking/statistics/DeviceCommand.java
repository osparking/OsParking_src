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
package com.osparking.osparking.statistics;

import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.getPercentString;
import static com.osparking.global.names.ControlEnums.MsgContent.ACK_AVG;
import static com.osparking.global.names.ControlEnums.MsgContent.ACK_MAX;
import static com.osparking.global.names.ControlEnums.MsgContent.NO_STAT;
import static com.osparking.global.names.ControlEnums.MsgContent.RESEND;
import static com.osparking.global.names.DB_Access.statCount;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DeviceCommand {
    
    /**
     * gate open command statistics
     */
    String commandName;
    int commandCount = 0;
    int commandCountRunning = 0;
    
    int commAckDelayTot = 0;
    int commAckDelayTotRunning = 0;
    
    int commAckDelayMax = 0;
    int commAckDelayMaxRunning = 0;
    
    int commResendCntTot = 0;
    int commResendCntTotRunning = 0;
    
    private int DECENT_LIMIT = 100; 
    
    
    /**
     * socket connection break statistics
     */
    
    private long recentCloseTimeMs = 0L; 
    
    /**
     * 
     * @param commandName 
     */
    public DeviceCommand(String commandName) {
        this.commandName = commandName;
    }
    
    public void addAckDelayStatistics (int delayMs, int resendCnt) {
        if (++commandCountRunning == statCount) {
            commandCount = commandCountRunning;
            commAckDelayTot = commAckDelayTotRunning;
            commAckDelayMax = commAckDelayMaxRunning;
            commResendCntTot = commResendCntTotRunning;
            
            commandCountRunning = 0;
            commAckDelayTotRunning = 0;
            commAckDelayMaxRunning = 0;
            commResendCntTotRunning = 0;
        };
        commAckDelayTotRunning += delayMs;
        if (commAckDelayMaxRunning < delayMs)
            commAckDelayMaxRunning = delayMs;
        commResendCntTotRunning += resendCnt;
        /**
         * save large disconnection period and occurrence time for debugging.
         */
//        if (delayMs > DECENT_LIMIT)
//            logParkingOperation(OpLogLevel.LogAlways, "Large ACK delay: " + delayMs, GENERAL_DEVICE);
    }
    
    /**
     * Creates a description on the command execution.
     * 
     * @param errorInserted says if artificial error inserted or not
     * @return command execution performance description string
     */
    public String getPerformanceDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (commandCount == 0) {
            sb.append("\t" + NO_STAT.getContent());
        } else {
            sb.append(commandName);
            sb.append(": ");
            sb.append(commandCount);
            sb.append(System.lineSeparator());

            sb.append("      " + ACK_AVG.getContent() + "(ms): ");
            sb.append(getFormattedRealNumber(commAckDelayTot/(float)commandCount, 1) + ACK_MAX.getContent());
            sb.append(commAckDelayMax);
            sb.append(System.lineSeparator());

            sb.append("      " + RESEND.getContent());
            sb.append(getPercentString(commResendCntTot/(float)commandCount));
            sb.append(System.lineSeparator());
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
