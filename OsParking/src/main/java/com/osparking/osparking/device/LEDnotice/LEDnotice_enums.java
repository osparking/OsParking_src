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
package com.osparking.osparking.device.LEDnotice;

import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.ColorBox.Green;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.ColorBox.Orange;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.ColorBox.Red;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.FontBox.Gothic;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.FontBox.Ming;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_INTR;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_RAM;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_TEXT;

/**
 *
 * @author Open Source Parking Inc.
 */
public class LEDnotice_enums {

    public enum LEDnoticeDefaultContentType {
        Verbatim("문구 자체"), // 문구를 그대로 표시
        ParkingLotName("주차장 명"), // 주차장/아파트-단지 이름
        GateName("정문 명칭"), // 출입구 명칭
        ParkingLot_GateName("주차장-정문"), // 주차장이름-정문 명칭
        CurrentDate("날짜(요일)"),     // 형태: 20XX-12-31(Mon.)
        CurrentTime("시:분:초"),     // 형태: AM/PM HH:MM:SS
        CurrentDateTime("날짜-시:분:초"); // 형태: 20XX-12-31(Mon) AM/PM HH:MM:SS
        
        private String label;

        LEDnoticeDefaultContentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }          
    };
    
    public enum LEDnoticeVehicleContentType {
        VehicleTag("차량번호"),
        RegistrationStat("등록상태"),
        VehicleRemark("불허사유");
        
        private String label;

        LEDnoticeVehicleContentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };
    
    public enum GROUP_TYPE {
        TEXT_GROUP (SAVE_TEXT.getValue()), // 일반(기본) 텍스트
        INTR_GROUP (SAVE_INTR.getValue()), // 인터럽트 텍스트
        RAM_GROUP (SAVE_RAM.getValue()); // 램 텍스트
        
        private int value;

        GROUP_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }           
    }
    
    public enum IntOnType {
        OneShot(0x41), 
        Unlimited(0x42);
        
        private int value;

        IntOnType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }       

        public String getHexStr() {
            return Integer.toHexString(value);
        }       
    }      
        
    public enum ColorBox {
        Red   (0b01, "빨강"), 
        Green(0b10, "초록"), 
        Orange(0b11, "주황");
        
        private int value;
        private String label;

        ColorBox(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue() {
            return value * (int)Math.pow(2, 6);
        }       

        public String getLabel() {
            return label;
        }       

        public String getHexStr() {
            return Integer.toHexString(getValue());
        }       
    }      
    
    public enum FontBox {
        Ming(0b01, "명조"),
        Gothic(0b10, "고딕");
        
        private int value;
        private String label;

        FontBox(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue() {
            return value * (int)Math.pow(2, 4);
        }       

        public String getLabel() {
            return label;
        }       

        public String getHexStr() {
            return Integer.toHexString(getValue());
        }       
    }      
        
    public enum ColorFont {
        RedGothic(0x1B00 + (Red.getValue() | Gothic.getValue())), // 빨강색 굴림체
        GreenGothic(0x1B00 + (Green.getValue() | Gothic.getValue())), // 초록색 굴림체
        OrangeGothic(0x1B00 + (Orange.getValue() | Gothic.getValue())), // 주황색 굴림체
        RedMing(0x1B00 + (Red.getValue() | Ming.getValue())), // 빨강색 명조체(Ming font)
        GreenMing(0x1B00 + (Green.getValue() | Ming.getValue())), // 빨강색 명조체(Ming font)
        OrangeMing(0x1B00 + (Orange.getValue() | Ming.getValue())); // 빨강색 명조체(Ming font)
        
        private int value;

        ColorFont(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }  
        
        public String getHexStr() {
            return Integer.toHexString(value);
        }             
        
        public String getHexStr(boolean withLF) {
            if (withLF)
                return Integer.toHexString(value + 1);
            else
                return Integer.toHexString(value);
        }             
    }                
    
    public enum RoomType {
        GENERAL_TEXT(0x30), // 일반 텍스트
        RAM(0x31), // Ram
        INTERRUPT_TEXT(0x32); // 인터럽트 텍스트
        
        private int value;

        RoomType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }       
    }            
    
    public enum EffectType {
        NONE (0x3f, "효과 없음"),
        
        FLOW_RtoL (0x40, "좌로 흐름"),
        FLOW_LtoR (0x41, "우로 흐름"),
        FLOW_UP (0x42, "위로 흐름"),
        FLOW_DOWN (0x43, "아래로 흐름"),
        
        STOP_MOVING (0x44, "멈춤 상태"),
        BLINKING (0x45, "깜박임"),
        REVERSE (0x46, "화려한 반전"),
        
        FLASHING (0x47, "플레싱"),
        BLIND (0x48, "블라인드"),
        RASER (0x49, "레이저 빔"),
        CENTERING (0x4a, "중앙 이동"),
        EXTEND (0x4b, "펼침"),
        BLINK_RtoL_RED (0x4c, "좌로적색깜박"),
        BLINK_LtoR_RED (0x4d, "우로적색깜박"),
        BLINK_RtoL_GREEN (0x4e, "좌로녹색깜박"),
        BLINK_LtoR_GREEN (0x4f, "우로녹색깜박"),
        
        CIRCLING (0x50, "회전"),
        OPENING_L_R (0x51, "좌우열기"),
        CLOSING_L_R (0x52, "좌우닫기"),
        OPENING_UP_DN (0x53, "상하열기"),
        CLOSING_UP_DN (0x54, "상하닫기"),
        
        MODULE_MOVE (0x55, "모듈별 이동"),
        MODULE_CIRCLE (0x56, "모듈별 회전"),
        COLOR_UP_DN (0x57, "상하색 분리"),
        COLOR_L_R (0x58, "좌우색 분리"),
        MOVE_EDEG (0x59, "테두리 이동"),
        
        EXPAND (0x5a, "확대"),
        EXPAND_VERTICAL (0x5b, "세로 확대"),
        EXPAND_HORIZONTAL (0x5c, "가로 확대"),
        
        BLINK_ROW (0x5d, "줄 깜박임"),
        VERTICAL_ADD (0x5e, "세로 쌓기"),
        SPRINKLE (0x5f, "흩뿌리기");
        
        private int value;
        private String label;

        EffectType(int value, String label) {
            this.value = value;
            this.label = label;            
        }

        public int getValue() {
            return value;
        }   
        
        public String getLabel() {
            return label;
        }        
    }    
    
    final static int delTextOne = 0x31;
    final static int delGroup = 0x32;
    final static int delTextAll = 0x33;
    final static int intrTxtOn = 0x3c;
    final static int intrTxtOff = 0x3d;
    final static int setCommSpd = 0x40;
    final static int setID = 0x42;
    final static int getID = 0x43;
    final static int getVersion = 0x44;
    final static int saveFlash = 0x53;
    final static int setClock = 0x61;
    final static int saveText = 0x71;
    final static int saveIntr = 0x75;
    final static int saveRAM = 0x76;
    final static int setMonitor = 0X82;
    
    public enum LED_MsgType {
        Broken(0),
        DEL_TEXT_ONE (delTextOne), // delete individual text
        DEL_GROUP (delGroup), // 그룹 메모리 삭제
        DEL_TEXT_ALL (delTextAll),  // delete whole text
        INTR_TXT_ON (intrTxtOn),  // set interrupt text on
        INTR_TXT_OFF (intrTxtOff),  // set interrupt text off
        
        SET_COMM_SPD (setCommSpd), // set communication speed
        SET_ID (setID), // set device ID
        GET_ID (getID),  // get device ID
        GET_VERSION (getVersion), // get version string
        
        SAVE_FLASH (saveFlash), // store into flash memory
        SAVE_TEXT (saveText), // store display text
        SET_CLOCK (setClock), // set E-board clock
        SAVE_INTR (saveIntr), // store interrupt text
        SAVE_RAM (saveRAM), // store RAM text 
        
        SET_MONITOR(setMonitor); // set monitor size
        
        final private int value;

        LED_MsgType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }       
    }
}
