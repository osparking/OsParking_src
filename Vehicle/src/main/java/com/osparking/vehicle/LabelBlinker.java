/*
 * Copyright (C) 2016 Open Source Parking Inc.(www.osparking.com)
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

import static com.osparking.global.CommonData.tipColor;
import static com.osparking.global.CommonData.tipColorTrans;
import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;

/**
 *
 * @author Open Source Parking Inc.(www.osparking.com)
 */
public class LabelBlinker {
    private static int counter = 0;

    /**
     * @param aCounter the counter to set
     */
    public static void setCounter(int aCounter) {
        counter = aCounter;
    }
    ScheduledExecutorService service;    
    
    public void displayHelpMessage(JLabel messageLabel, String message, boolean blinker) {

        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                if (counter++ >= 6) {
                    service.shutdown();
                    messageLabel.setForeground(Color.gray);
                    setCounter(0);
                } else {
                    if (counter % 2 == 1) {
                        messageLabel.setForeground(tipColor);
                    } else {
                        messageLabel.setForeground(tipColorTrans);
                    }
                }
            }
        };

        messageLabel.setText(message);
        messageLabel.setForeground(Color.gray);
        if (blinker) {
            if (service != null) {
                service.shutdown();
            }
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, 750, TimeUnit.MILLISECONDS);
        }
    } 
    
    public void displayHelpMessages(JLabel messageLabel, String message1, 
            String message2, boolean blinker) 
    {
        Runnable runnable = new Runnable(       ) {
            public void run() {
                // task to run goes here
                if (counter++ < 4) {
                    if (counter % 2 == 1) {
                        messageLabel.setForeground(tipColor);
                    } else {
                        messageLabel.setForeground(tipColorTrans);
                    }
                } else {
                    service.shutdown();
                    messageLabel.setText(message2);
                    messageLabel.setForeground(Color.gray);
                    setCounter(0); // init for the next column/control
                }
            }
        };

        messageLabel.setForeground(Color.gray);
        if (blinker) {
            messageLabel.setText(message1);
            if (service != null) {
                service.shutdown();
            }
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, 750, TimeUnit.MILLISECONDS);
        } else {
            if (service != null) {
                service.shutdown();
            }
            messageLabel.setText(message2);
        }
    }
}
