/*
 * Copyright (C) 2015 Open Source Parking Inc.
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
package com.osparking.osparking.device.LEDnotice;

import com.osparking.global.names.IDevice.MessageQueue;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType;
import com.osparking.osparking.device.LEDnotice.LEDnoticeMessageQueue.MsgItem;
import com.osparking.osparking.statistics.DeviceCommand;
import java.util.LinkedList;

/**
 *
 * @author Open Source Parking Inc.
 */
public class LEDnoticeMessageQueue extends LinkedList<MsgItem> implements MessageQueue {
    Object msgQdoor = new Object();
    DeviceCommand devCmd;
    
    long delayTotal;
//    int delayCount;

//    long recentTotal;
//    int recentCount;

    LEDnoticeMessageQueue(Object msgQdoor, DeviceCommand devCmd) {
        this.msgQdoor = msgQdoor;
        this.devCmd = devCmd;
    }

    @Override
    public boolean add(MsgItem item) {
        item.timeAddedMs = System.currentTimeMillis();
        super.add(item);
        synchronized(msgQdoor) {
            msgQdoor.notify(); // wake up someone sleeping on this queue
        }
        return true;
    }

    public MsgItem remove() {
        if (peek().getType() == LED_MsgType.SAVE_INTR) 
        {
            int currDelay = (int)(System.currentTimeMillis() - peek().timeAddedMs);
            System.out.println("interrupt stat added");
            devCmd.addAckDelayStatistics(currDelay, peek().getSendCount());
        }
        return super.remove();
    }

    @Override
    public String getAckStatistics() {
//        if (ledNoticeMessages.recentCount == 0)
//            return "avg not accumed yet";
//        else 
//            return " AckAvg:" + (ledNoticeMessages.recentTotal / ledNoticeMessages.recentCount) + "ms/cnt:" 
//                    + ledNoticeMessages.recentCount + System.lineSeparator(); 
        return "dum stat";
    }
    
    public static class MsgItem {
        private LED_MsgType type;
        private String hexStr;
        private byte[] message;
        private int sendCount = 0;
        private long timeAddedMs;
        static public LedProtocol ledNoticeProtocol = new LedProtocol(); 
        
        
        public MsgItem(LED_MsgType type, String hexStr) {
            this.type = type;
            this.hexStr = hexStr;
            message = ledNoticeProtocol.hexToByteArray(hexStr);
        }
        
        /**
         * @return the message
         */
        public byte[] getMessage() {
            return message;
        }

        /**
         * @return the type
         */
        public LED_MsgType getType() {
            return type;
        }

        /**
         * @return the hexStr
         */
        public String getHexStr() {
            return hexStr;
        }
        
        public void incSendCount() {
            sendCount++;
        }

        /**
         * @return the sendCount
         */
        public int getSendCount() {
            return sendCount;
        }
    }
}
    