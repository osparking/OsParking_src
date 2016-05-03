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

import com.osparking.global.Globals;
import static com.osparking.global.Globals.logParkingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ParkingTimer extends Timer {
    String name;
    int rescheduleCallCount = 0;
    private Runnable parkingTask = null;
    private TimerTask timerTask = null;
    long startAt = 0;
    long period = 0;
    
    public ParkingTimer(String name, boolean isDaemon) {
        super(name, isDaemon);
        this.name = name;
    }
    
    /**
     * Parking Lot specific reschedulable timer.
     * 
     * @param name name of the timer for program analysis
     * @param isDaemon tells if this should be a daemon thread
     * @param parkingTask the job to be done via this timer
     * @param startAt the first execution start time
     * @param period duration between successive execution of the given task
     */
    public ParkingTimer(String name, boolean isDaemon, final Runnable parkingTask,
            long startAt, long period) {
        super(name, isDaemon);
        this.name = name;
        this.parkingTask = parkingTask;
        this.startAt = startAt;
        this.period = period;        
    }

    public void schedule(final Runnable parkingTask, long startAt, long period) {
        this.parkingTask = parkingTask;
        this.startAt = startAt;
        this.period = period;
        
        timerTask =  new TimerTask() { public void run() { parkingTask.run(); }; };        
        super.schedule(timerTask, startAt, period);
    }

    public void reschedule() {
        cancelTask();
        setSchedule(startAt);
    }
    
    public void cancelTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
            purge();
            //logParkingException(Level.INFO, null, name + "'s task canceled at: " + incAndGetSeqValue());
        }
    }

    public void reschedule(int startAt) {
        setSchedule(startAt);
    }

    private void setSchedule(long startAt) {
        timerTask =  new TimerTask() { public void run() { getParkingTask().run(); }; };        
        super.schedule(timerTask, startAt, period);
    }

    private void setSchedule(long startAt, long period) {
        timerTask =  new TimerTask() { public void run() { getParkingTask().run(); }; };     
        try {
            super.schedule(timerTask, startAt, period);
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "1. setSchedule exception", Globals.GENERAL_DEVICE);
            logParkingException(Level.SEVERE, e, "2. msg: " + e.getMessage(), Globals.GENERAL_DEVICE);
            logParkingException(Level.SEVERE, e, "3. strin " + e.toString(), Globals.GENERAL_DEVICE);
        }
    }
    
    private void setScheduleAtFixedRate(long startAt, long period) {
        timerTask =  new TimerTask() { public void run() { getParkingTask().run(); }; };        
        super.scheduleAtFixedRate(timerTask, startAt, period);
    }
    
    public void reschedule(Runnable parkingTask) {
        cancelTask();
        this.parkingTask = parkingTask;
        setSchedule(startAt);
    }

    public void reRunOnce(TimerTask timerTask, long delay) {
        cancelTask();
        this.timerTask = timerTask;
        super.schedule(timerTask, delay);
    }
    
    public void runOnce(final Runnable parkingTask) {
        this.parkingTask = parkingTask;
        timerTask =  new TimerTask() { public void run() { parkingTask.run(); }; };        
        try {
            super.schedule(timerTask, 0);
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "timer task scheduling error", Globals.GENERAL_DEVICE);
        }
    }    
    
    public void reRunOnce() {
        timerTask =  new TimerTask() { public void run() { parkingTask.run(); }; };        
        super.schedule(timerTask, 0);
    }
    
    public void reschedule(Runnable parkingTask, long startAt, long period) {
        cancelTask();
        this.parkingTask = parkingTask;
        this.period = period;
        try {
            setSchedule(startAt, period);
        } catch (Exception e) {
            logParkingException(Level.SEVERE, e, "task rescheduling error", Globals.GENERAL_DEVICE);
        }
    }
    
    public void rescheduleAtFixedRate(Runnable parkingTask, long startAt, long period) {
        cancelTask();
        this.parkingTask = parkingTask;
        this.period = period;
        setScheduleAtFixedRate(startAt, period);
    }    
    
    /**
     * @return the parkingTask
     */
    public Runnable getParkingTask() {
        return parkingTask;
    }
    
    public boolean hasTask() {
        if (timerTask == null)
            return false;
        else
            return true;
    }
}
