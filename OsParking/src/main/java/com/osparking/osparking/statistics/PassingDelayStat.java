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
package com.osparking.osparking.statistics;

import static com.mysql.jdbc.NonRegisteringDriver.DEBUG;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.osparking.global.names.DB_Access.recordPerformance;
import static com.osparking.global.names.DB_Access.statCount;
import static com.osparking.global.Globals.GENERAL_DEVICE;
import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.logParkingOperation;
import static com.osparking.global.Globals.timeFormat;
import com.osparking.global.names.OSP_enums.OpLogLevel;

/**
 *
 * @author Open Source Parking Inc.
 */
public class PassingDelayStat {

    /**
     * Image Code (very first byte of the car image message) arrival time.
     */
    private long ICodeArrivalTime; 
    
    /**
     * Tells if the latest vehicle processing performance is meaningful to accumulate.
     */
    private boolean accumulatable; // Registered Vehicle flag
    
    /**
     * Vehicle Passing delay total (unit: millisecond).
     */
    long VPdelayTotal;
    
    /**
     * Maximum value of a car passing delay
     */
    
    int runningDelayMax = 0;
    
    /**
     * Passed Vehicle count.  Increases from 0 to statCount and then goes back to 0 repeatedly.
     */
    int PVcount; 
    
    /**
     * Latest Vehicle Passing Average
     */
    private float passingDelayPreviousAverageMs = -1.0f; 
    int LVPdelayMax = 0;
    
    /**
     * Date(time) when the latest vehicle passing average(passingDelayPreviousAverageMs) was calculated.
     */
    private Date passingDelayCalculationTime; 
    
    /**
     *  Population size for the calculation the latest vehicle passing average(passingDelayPreviousAverageMs). 
     *  It is needed since statCount (: system settings variable) can change all the time.
     */
    private int passingDelayPreviousPopulation; 
    
    /**
     * Accumulates a vehicle processing performance regarding the present time(ms) as a gate bar 
     * acknowledgment time for an <code>open</code> command.
     */
    public String recordBarACKspeed(int gateID) {
        String msg;
        
        long delay = System.currentTimeMillis() - getICarrivalTime();
        VPdelayTotal += delay;
        if (runningDelayMax < delay) 
            runningDelayMax = (int)delay;
        PVcount++;

        if (PVcount == statCount) {
            setPassingDelayPreviousAverageMs((float)VPdelayTotal / statCount);
            LVPdelayMax = runningDelayMax;
            setPassingDelayCalculationTime(new Date());
            setPassingDelayPreviousPopulation(statCount);

            // Preperation for the next cycle of statistics gathering
            PVcount = 0;
            runningDelayMax = 0;
            VPdelayTotal = 0;
        }
        msg = recordPerformance(gateID, delay);
        if (msg != null && DEBUG)
            logParkingOperation(OpLogLevel.LogAlways ,  
                    msg + ", max: " + LVPdelayMax + System.getProperty("line.separator"), 
                    GENERAL_DEVICE);    
        
        return msg;
    }
    
    /**
     * Supplies a vehicle passing delay average time(ms) (for a specific gate).
     * @return a average time with the time unit suffix (ms for milliseconds) (eg, "25.5ms")
     */
    public String getPassingDelayAvg() {
        String resultString = null;

        if (passingDelayPreviousAverageMs < 0) {
            String rate = getFormattedRealNumber(((float)PVcount/statCount), 2);
            resultString = "(wait: " + rate + ")";
        }
        else 
            resultString = getFormattedRealNumber(passingDelayPreviousAverageMs, 1) + "ms on " 
                    + new SimpleDateFormat("yyyy-MM-dd ").format(passingDelayCalculationTime) 
                    + timeFormat.format(passingDelayCalculationTime) 
                    + " (" + passingDelayPreviousPopulation + " vehicles)";
        
        return resultString;
    }
    
    public int getPassingDelayMax() {
        return LVPdelayMax;
    }

    /**
     * @return the accumulatable
     */
    public boolean isAccumulatable() {
        return accumulatable;
    }

    /**
     * Sets the flag which tells if the latest vehicle processing performance is accumulatable.
     * @param accumulatable <code>true</code> if the performance is accumulatable,
     * <code>false</code> otherwise
     */
    public void setAccumulatable(boolean accumulatable) {
        this.accumulatable = accumulatable;
    }

    /**
     * @return the ICodeArrivalTime
     */
    public long getICarrivalTime() {
        return ICodeArrivalTime;
    }

    /**
     * Save the arrival time of the first byte of a vehicle image sent from the camera.
     * @param ICarrivalTime the read time of the code byte of the image message in millisecond
     */
    public void setICodeArrivalTime(long ICarrivalTime) {
        ICodeArrivalTime = ICarrivalTime;
    }

    /**
     * @param passingDelayPreviousAverageMs the passingDelayPreviousAverageMs to set
     */
    public void setPassingDelayPreviousAverageMs(float passingDelayPreviousAverageMs) {
        this.passingDelayPreviousAverageMs = passingDelayPreviousAverageMs;
    }

    /**
     * @param passingDelayPreviousPopulation the passingDelayPreviousPopulation to set
     */
    public void setPassingDelayPreviousPopulation(int passingDelayPreviousPopulation) {
        this.passingDelayPreviousPopulation = passingDelayPreviousPopulation;
    }

    /**
     * @param passingDelayCalculationTime the passingDelayCalculationTime to set
     */
    public void setPassingDelayCalculationTime(Date passingDelayCalculationTime) {
        this.passingDelayCalculationTime = passingDelayCalculationTime;
    }
    
}
