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
package com.osparking.osparking.device.NaraBar;

import com.osparking.osparking.device.LEDnotice.*;
import com.osparking.global.names.IDevice.MessageQueue;
import com.osparking.osparking.device.NaraBar.NaraMessageQueue.NaraMsgItem;
import com.osparking.osparking.device.NaraBar.NaraEnums.Nara_MsgType;
import com.osparking.osparking.statistics.DeviceCommand;
import java.util.LinkedList;

/**
 *
 * @author Open Source Parking Inc.
 */
public class NaraMessageQueue extends LinkedList<NaraMsgItem> implements MessageQueue {
    Object msgQdoor = new Object();
    DeviceCommand devCmd;

    public NaraMessageQueue(Object msgQdoor, DeviceCommand devCmd) {
        this.msgQdoor = msgQdoor;
        this.devCmd = devCmd;        
    }

    @Override
    public boolean add(NaraMsgItem item) {
        item.timeAddedMs = System.currentTimeMillis();
        super.add(item);
        synchronized(msgQdoor) {
            msgQdoor.notify(); // wake up someone sleeping on this queue
        }
        return true;
    }

    public NaraMsgItem remove() {
        if (peek().getType() == Nara_MsgType.GateUp) {
            int currDelay = (int)(System.currentTimeMillis() - peek().timeAddedMs);
            System.out.println("interrupt stat added");
            devCmd.addAckDelayStatistics(currDelay, peek().getSendCount());            
//            delayTotal += currDelay;
//            delayCount++;
//            if (delayCount == DB_Access.statCount) {
//                recentTotal = delayTotal;
//                recentCount = DB_Access.statCount;
//            }
        }
        return super.remove();
    }

    @Override
    public String getAckStatistics() {
        return "";
    }
    
    public static class NaraMsgItem {
        private Nara_MsgType type;
        private String hexStr;
        private byte[] message;
        private int sendCount = 0;
        private long timeAddedMs;
        public LedProtocol ledNoticeProtocol = new LedProtocol(); 
        
        
        public NaraMsgItem(Nara_MsgType type) {
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
        public Nara_MsgType getType() {
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