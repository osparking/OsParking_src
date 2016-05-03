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

import com.osparking.global.names.OSP_enums.DisplayArea;
import static com.osparking.global.names.OSP_enums.DisplayArea.BOTTOM_ROW;
import static com.osparking.global.names.OSP_enums.DisplayArea.TOP_ROW;
import static com.osparking.global.names.OSP_enums.DisplayArea.WHOLE_AREA;
import static com.osparking.osparking.device.LEDnotice.LEDnoticeManager.ledNoticeBlankString;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.ColorFont;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.EffectType;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.GROUP_TYPE;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.IntOnType;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.DEL_GROUP;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.DEL_TEXT_ALL;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.DEL_TEXT_ONE;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.GET_ID;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.GET_VERSION;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.INTR_TXT_OFF;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.INTR_TXT_ON;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_INTR;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_RAM;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SAVE_TEXT;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SET_CLOCK;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SET_COMM_SPD;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SET_ID;
import static com.osparking.osparking.device.LEDnotice.LEDnotice_enums.LED_MsgType.SET_MONITOR;
import com.osparking.osparking.device.LEDnotice.LEDnotice_enums.RoomType;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Protocol for the followind device is defined in this class file
 * Device name: LEDnotice
 * 
 * @author Open Source Parking Inc.
 */
public class LedProtocol {
    final public static int LED_COLUMN_CNT = 6; // LEDnotice 제품의 열 개수.
    final public static int STOP_TIME_MIN = 1;
    
    final static String STX = "02";                   //데이터 전송 시작 코드
    final static int intSTX = Integer.parseInt(STX);
    
    final static String ETX = "03";                   //데이터 전송 마침 코드 
    final static int intETX = Integer.parseInt(ETX);
    
    final static String R5  = "0000000000";           // 5자리 예약 공간 표시
    final static String R6  = "076B033C0000";         // 6자리 예약 공간 표시
    final static String R9  = "000000000000000000";   // 9자리 예약 공간 표시
    final static String type2 = "41";
    
    static final int ROOM_START = 0x30;
    
    final static int SUCCESS = 0x31;                   // success: 0x31
    final static int FAILURE = 0x30;                   // failure: 0x30
    
    final static int MAX_SPEED = 31;                 // 가장 빠른 시작 및 종료 효과 속도
    final static int MIN_SPEED = 1;                 // 가장 빠른 시작 및 종료 효과 속도
    final static int MIN_PAUSE = 1;                 // 가장 빠른 시작 및 종료 효과 속도

    public static int byteToUint(byte b) {
        return b & 0xFF;
    }    
    
    /**
     * Interrupt Text Display Settings.
     * 
     * @param indexInterruptTextRoomNo 인터럽트 텍스트를 저장할 방 번호
     * @param pos
     * @param se
     * @param ss
     * @param st
     * @param ee
     * @param es
     * @param re
     * @param colorFont
     * @param text
     * @return 
     */
    public String interruptText(int indexInterruptTextRoomNo, DisplayArea pos, 
            LEDnotice_enums.EffectType se, int ss, int st, 
            LEDnotice_enums.EffectType ee, int es, int re, ColorFont colorFont, String text)   
    {
        String dataVariable = null;
        try {
            String index = String.format("%02X", indexInterruptTextRoomNo);
            String Coordinate = setCoordinate(pos);
            String effect = setEffect(se, ss, st, ee, es, re);
            String stringData = (byteArrayToHex(text.getBytes("x-windows-949"))).toUpperCase();
            dataVariable = index + R6 + R6 + R5 + Coordinate + R9 + type2 + effect + 
                    Integer.toHexString(colorFont.getValue()) + stringData;
            System.out.println("");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
       return getStringToTransmit(SAVE_INTR, dataVariable);     
    }     
    
    /**
     * 
     * @param roomNumber interrupt text room number (0~4)
     * @param area display area (top, bottom, whole area)
     * @param startEffect starting effect of the interrupt message 
     * @param ss 
     * @param st
     * @param ee
     * @param es
     * @param re
     * @param topFontColor
     * @param topMessage
     * @param bottomFontColor
     * @param bottomMessage
     * @return 
     */
    public String interruptBothRows(int roomNumber, DisplayArea area, LEDnotice_enums.EffectType startEffect, 
            int ss, int st, LEDnotice_enums.EffectType ee, int es, int re, ColorFont topFontColor, String topMessage, 
            ColorFont bottomFontColor, String bottomMessage)  {
        String dataVariable = null;
                
        try {
            String index = String.format("%02X", roomNumber);
            String Coordinate = setCoordinate(area);
            String effect = setEffect(startEffect, ss, st, ee, es, re);
            String stringData = (byteArrayToHex(topMessage.getBytes("x-windows-949"))).toUpperCase();
            String stringData2 = (byteArrayToHex(bottomMessage.getBytes("x-windows-949"))).toUpperCase();
            
            dataVariable = index + R6 + R6 + R5 + Coordinate + R9 + type2 + effect + 
                    topFontColor.getHexStr() + stringData + bottomFontColor.getHexStr(true) + stringData2;
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getStringToTransmit(SAVE_INTR, dataVariable);
    }     
    
    /**
     * hex String을 Byte Array로 변환하기 위한 코드
     * @param hex
     * @return 
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            try {
                ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
            } catch (Exception ex ) {
                System.out.println("");
            }
        }
        return ba;
    }

    /**
     * Byte Array을 hex String로 변환하기 위한 코드.
     * 
     * @param ba    byteArray
     * @return 
     */
    public String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }
    
    /**
     * 좌표 설정 함수, 2단 6열의 상단 x =0, y =0, w = 12, h = 16 
     * 2단 6열의 하단 x =0, y =16, w = 12, h = 32 
     * 2단 6열의 상하단 x = 0,y = 0, wposh = 32
     * 
     * @param p     position
     * @return 
     */
    public String setCoordinate(DisplayArea pos) {    
        String position = null;
        String X, Y, W, H;

        switch (pos) {
            case TOP_ROW:
                X = String.format("%02X", 0);
                Y = String.format("%02X", 0);
                W = String.format("%02X", 12);
                H = String.format("%02X", 16);
                position = X + Y + W + H;
                break;

            case BOTTOM_ROW:
                X = String.format("%02X", 0);
                Y = String.format("%02X", 16);
                W = String.format("%02X", 12);
                H = String.format("%02X", 32);
                position = X + Y + W + H;
                break;

            case WHOLE_AREA:
                X = String.format("%02X", 0);
                Y = String.format("%02X", 0);
                W = String.format("%02X", 12);
                H = String.format("%02X", 32);
                position = X + Y + W + H;
                break;
                
            default:
                break;
        }
        return position;
    }

    /**
     * 효과설정 함수 시작 효과, 시작 속도, 정지 시간, 마침 효과, 마침 속도, 반복횟수
     * 
     * @param startEffect   광고 문구의 시작효과  1 Byte - 0x40~0x60까지 (64~96)
     * @param startSpeed    광고 문구의 시작효과 속도  1 Byte - 0x41~0x5f까지 (65~95)
     * @param stopTime      광고 문구의 정지시간  1 Byte - 0x40~0x4a까지 (64~74)
     * @param endEffect     광고 문구의 마침효과  1 Byte - 0x3f~0x60까지 (63~96)
     * @param endSpeed      광고 문구의 마침효과 속도  1 Byte - 0x41~0x5f까지 (65~95)
     * @param repeat        광고 문구의 반복횟수  1 Byte - 0x41~0x4a까지 (65~74)
     * @return              위의 효과를 합친 String 값.
     */
    public String setEffect(LEDnotice_enums.EffectType startEffect, int startSpeed, int stopTime,
            LEDnotice_enums.EffectType endEffect, int endSpeed, int repeat) {

        String se = Integer.toHexString(startEffect.getValue());
        String ss = Integer.toHexString(startSpeed+64);
        String st = Integer.toHexString(stopTime+63);
        String ee = Integer.toHexString(endEffect.getValue());
        String es = Integer.toHexString(endSpeed+64);
        String re = Integer.toHexString(repeat+64);
        String reserve = "0000";

        return (se+ss+st+ee+es+re+reserve);

    }

    /**
     * 글씨체 설정
     * set = 1 빨강색 굴림체
     * set = 2 초록색 굴림체
     * set = 3 노란색 굴림체
     * set = 4 빨강색 궁서체
     * set = 5 초록색 궁서체
     * set = 6 노란색 궁서체
     * 
     * @param font     색상 및 폰트 설정   
     * @return          색상 및 폰트 설정 된 16진수
     */
    public String setFonts(int font) {
        String setF = null;
        switch (font){
            case 1:
                setF = "1B60";
                break; //빨강색 굴림체
            case 2:
                setF = "1BA0";
                break; //초록색 굴림체
            case 3:
                setF = "1BE0";
                break; //노란색 굴림체
            case 4:
                setF = "1B50";
                break; //빨간색 궁서체
            case 5:
                setF = "1B90";
                break; //초록색 궁서체
            case 6:
                setF = "1BD0";
                break; //노란색 궁서체
        }
        return setF; 
    }

    /**
     * checksum 계산용 함수 
     * 
     * @param dataLength
     * @param textType
     * @param data
     * @return  
     */
    public static String checkSum(String dataLength, String textType, String data) {

        int length = Integer.parseInt(dataLength, 16);
        int type = Integer.parseInt(textType, 16);
        int variable = 0;
        int L = data.length();

        for (int n=0; n < L; n = n + 2) {
            String D = data.substring(n, n+2);
            if (D.substring(0, 1).equals(" "))
                System.out.println("");
            variable = variable + Integer.parseInt(D, 16);    
        }

        return String.format("%04X", length + type +variable);
    }

    public String sendMSG(LED_MsgType msgType, String dataVariable){
        String typeANDdata = Integer.toHexString(msgType.getValue()) + dataVariable; // get data length string
        String msgLen = String.format("%04X", typeANDdata.length() / 2);
        String ckSum = checkSum(msgLen, Integer.toHexString(msgType.getValue()), dataVariable);
        String typeText = msgLen + typeANDdata + ckSum;

        String dataOut = STX + removeSpecialCharacter(typeText) + ETX;
        
        return dataOut;
    } 

    /**
     * 텍스트 Display 설정
     * 
     * @param roomNum   
     * @param pos
     * @param se
     * @param ss
     * @param st
     * @param ee
     * @param es
     * @param re
     * @param setF
     * @param text
     * @return
     */
    public String textType(int roomNum, DisplayArea pos, EffectType startEffect, int ss, int st, 
        EffectType endingEffect, int es, int re, String colorFont, String displayCore, boolean fullDemo)  
    {
        
        String blankString = ledNoticeBlankString.toString();
    
        int contentWidth = getViewWidth(displayCore);
        int padBlankCount = LedProtocol.LED_COLUMN_CNT * 2 - contentWidth;
        
        String displayReal = displayCore + blankString.substring(0, (padBlankCount < 0 ? 0 : padBlankCount));
        
        if (contentWidth > LedProtocol.LED_COLUMN_CNT * 2) {
            startEffect = EffectType.FLOW_RtoL;
        }
                
        // Display text storage room number
        String index = String.format("%02X", roomNum + ROOM_START); // (0~31) starts with 0x30
        String Coordinate = setCoordinate(pos);
        String effect = setEffect(startEffect, ss, st, endingEffect, es, re);
        byte [] message = displayReal.getBytes();
        String stringData = (byteArrayToHex(message)).toUpperCase();
        String dataVariable = null;
        dataVariable = index + R6 + R6 + R5 + Coordinate + R9 + type2 + effect +
                colorFont + stringData;

        return getStringToTransmit(SAVE_TEXT, dataVariable);   
    }
        
    public static int getViewWidth(String effectLabel) {
        int length = effectLabel.length();
        
        for (int i = 0; i < effectLabel.length(); i++) {
            if (isHangulCh(effectLabel.charAt(i)))
                length++;
        }
        return length;
    }  
    
    private static boolean isHangulCh(char c){
        String uncodeBlock = Character.UnicodeBlock.of(c).toString();
        if(uncodeBlock.equals("HANGUL_SYLLABLES") ||
                uncodeBlock.equals("HANGUL_JAMO") ||
                uncodeBlock.equals("Hangul_Compatibility_Jamo") ||
                uncodeBlock.equals("HANGUL_COMPATILITY_JAMO"))
            return true;
        else
            return false;
    }        
    
    /**
     * Interrupt Text Display Settings.
     * 
     * @param indexnumber
     * @param pos
     * @param se
     * @param ss
     * @param st
     * @param ee
     * @param es
     * @param re
     * @param setF
     * @param text
     * @return 
     */
    public String interruptText(int indexnumber, int pos, int se, int ss, int st, int ee, int es, int re, int setF, String text)   {
        String dataVariable = null;
        try {
            String index = String.format("%02X", indexnumber+80);
            String font = setFonts(setF);
            byte [] message = text.getBytes("x-windows-949");
            String stringData = (byteArrayToHex(message)).toUpperCase();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
       return sendMSG(SAVE_INTR, dataVariable);     
    } 
    
    public String interruptText2(int indexnumber, int p, int se, int ss, int st, int ee, int es, int re, int setF, String text, String text2)  {
        String dataVariable = null;
                
        try {
            String index = String.format("%02X", indexnumber+80);
//            String Coordinate = setCoordinate(p);
//            String effect = setEffect(se, ss, st, ee, es, re);
            String font = setFonts(setF);
            byte [] message = text.getBytes("x-windows-949");
            byte [] message2 = text2.getBytes("x-windows-949");
            String stringData = (byteArrayToHex(message)).toUpperCase();
            String stringData2 = (byteArrayToHex(message2)).toUpperCase();
//            dataVariable = index + R6 + R6 + R5 + Coordinate + R9 + type2 + effect + font + stringData + "1bE1" + stringData2;
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sendMSG(SAVE_INTR, dataVariable);
                
    } 
    
    public String interruptText3(int indexnumber, int p, int se, int ss, int st, int ee, int es, int re, int setF, String text, 
            int se2, int ss2, int st2, int ee2, int es2, int re2, int setF2, String text2)  {
        String dataVariable = null;
                
        try {
            String index = String.format("%02X", indexnumber+80);
            String font = setFonts(setF);
            String font2 = setFonts(setF2);
            byte [] message = text.getBytes("x-windows-949");
            byte [] message2 = text2.getBytes("x-windows-949");
            String stringData = (byteArrayToHex(message)).toUpperCase();
            String stringData2 = (byteArrayToHex(message2)).toUpperCase();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sendMSG(SAVE_INTR, dataVariable);
                
    } 

    /**
     * 
     * @param indexnumber
     * @param p
     * @param se
     * @param ss
     * @param st
     * @param ee
     * @param es
     * @param re
     * @param setF
     * @param text
     * @return 
     */
    public String ramText(int indexnumber, int p, int se, int ss, int st, int ee, int es, int re, int setF, String text)  {
        String dataVariable = null;
        try {
            String index = String.format("%02X", indexnumber+48);
            String font = setFonts(setF);
            byte [] message = text.getBytes("x-windows-949");
            String stringData = (byteArrayToHex(message)).toUpperCase();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LedProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
            return sendMSG(SAVE_RAM, dataVariable);
    }

    public String getScreenSetString(int i, int w, int h) {

        String I = String.format("%04X", i); //스토리 리스트 개수 ??
        String W = String.format("%02X", w); //화면의 가로모듈 수
        String H = String.format("%02X", h); //화면의 세로모듈 수
        String dataVariable = I + W + H + "0000000000"; // "3030300000"; 

        return getStringToTransmit(SET_MONITOR, dataVariable);
    }

    public String getStringToTransmit(LED_MsgType msgType, String dataVariable){
        String forlength = Integer.toHexString(msgType.getValue()) + dataVariable; // get data length string
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, Integer.toHexString(msgType.getValue()), dataVariable);
        String typeText = dLength + forlength + ckSum;

        String dataOut = STX + removeSpecialCharacter(typeText) + ETX;
        
        return dataOut;
    } 
    
    //0x10, 0x02, 0x03을 검사하여 치환
    /**
     * SpecialCharater를 구분하기위한 코드.
     * 
     * STX와 ETX를 제외한 곳에서 Special Character (STX(0x02), ETX(0x03), DLE(0x10)) 
     * 이 있으면 Special Character 앞에 DLE(0x10)을 추가하고 
     * SpecialCharacter에 SP(0x20)을 더하여 전송한다.
     * 이때 0x10 및 0x20을 추가 및 더하여도 전체 LENGTH 및 CHECKSUM에는 아무런 변화가 없다.
     * 
     * @param sendData
     * @return 
     */
    public static String removeSpecialCharacter(String sendData) {    
        int data_length = sendData.length();

        String data;
        String alter_Data = "";

        for (int n=0; n < data_length; n = n + 2) {

            data = sendData.substring(n, n+2);
            if ("10".equals(data))
            {data = "1030";}
            else if ("02".equals(data))
            {data = "1022";}
            else if ("03".equals(data))
            {data = "1023";}
            alter_Data += data;
        }
        return alter_Data.toUpperCase();    
    }    
    
    public String setTime() {
       long now = System.currentTimeMillis();


        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat date = new SimpleDateFormat("dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        SimpleDateFormat second = new SimpleDateFormat("ss");
        SimpleDateFormat millisecond = new SimpleDateFormat("SSS");

        String y = year.format(new Date(now));
        String m = month.format(new Date(now));
        String d = date.format(new Date(now));
        String h = hour.format(new Date(now));
        String mm = minute.format(new Date(now));
        String s = second.format(new Date(now));
        String ms = millisecond.format(new Date(now));


        String y1 = String.format("%04X", Integer.parseInt(y));
        String m1 = String.format("%02X", Integer.parseInt(m));
        String d1 = String.format("%02X", Integer.parseInt(d));
        String h1 = String.format("%02X", Integer.parseInt(h));
        String mm1 = String.format("%02X", Integer.parseInt(mm));
        String s1 = String.format("%02X", Integer.parseInt(s));
        String ms1 = String.format("%04X", Integer.parseInt(ms));

        String dataVariable = y1+m1+d1+h1+mm1+s1+ms1;

        String forlength = "61" + dataVariable; //datalength를 구함 61은 시간 설정 type 코드임
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, "61", dataVariable);
        String ScreenArea = dLength + forlength + ckSum;
        String dataOut = STX + removeSpecialCharacter(ScreenArea) + ETX;   

        return dataOut;
    }

    /**
     * 개별 텍스트 삭제
     * 
     * @param roomType 일반텍스트, Ram, Interrupt Text 
     * @param roomNo 0 ~ 31
     * @return 
     */
    public String delData(RoomType roomType, int roomNo) {

        String dataVariable = String.format("%02X", roomType.getValue()) + String.format("%02X", roomNo);
        String forlength = Integer.toHexString(DEL_TEXT_ONE.getValue()) + dataVariable;
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, Integer.toHexString(DEL_TEXT_ONE.getValue()), dataVariable);
        String delData = dLength + forlength + ckSum;
        String dataOut = STX + removeSpecialCharacter(delData) + ETX;   

        return dataOut;
    }
    
    // a = 1 광고그룹 삭제, a = 5 인터럽트 텍스트 삭제, a = 6 RAM 삭제
    public String delGroup(GROUP_TYPE group)  { 

        String data = String.format("%02X", group.getValue()) + "00";
        String types = Integer.toHexString(DEL_GROUP.getValue());
        String forlength = types + data;
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, types, data);
        String delData = dLength + forlength + ckSum;
        String dataOut = STX + removeSpecialCharacter(delData) + ETX;   

        return dataOut;
    }

    public String delAll() {
        String dataVariable = "3030";
        String forlength = Integer.toHexString(DEL_TEXT_ALL.getValue()) + dataVariable;
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, Integer.toHexString(DEL_TEXT_ALL.getValue()), dataVariable);
        String delData = dLength + forlength + ckSum;
        String dataOut = STX + removeSpecialCharacter(delData) + ETX;   
    
        return dataOut;     
    }

    public String setOnOffTime(int startHour, int startMinute, int startSecond, int endHour, int endMinute, int endSecond)  {
        String startH = String.format("%02X", startHour);    
        String StartM = String.format("%02X", startMinute);
        String startS = String.format("%02X", startSecond);
        String endH = String.format("%02X", endHour);
        String endM = String.format("%02X", endMinute);
        String endS = String.format("%02X", endSecond);

        String dataVariable = startH+StartM+startS+endH+endM+endS;

        String typesT = "3B";

        String forlength = typesT + dataVariable;
        String dLength = String.format("%04X", forlength.length() / 2);
        String ckSum = checkSum(dLength, typesT, dataVariable);
        String delData = dLength + forlength + ckSum;
        String dataOut = STX + removeSpecialCharacter(delData) + ETX;   

        return dataOut;
    }

    /**
     * 
     * @param type either "One Shot" or "Unlimited Loop"
     * @param index 인터럽트 텍스트를 저장할 방 번호 // index = 0 ~ 4,
     * @param count 인터럽트 텍스트의 디스플레이할 방의 개수 //  count 0~31 
     * @return 
     */
    public String intOn(IntOnType type, int index, int count) {
        String id = String.format("%02X", index);
        String ct = String.format("%02X", count);
        String dataVariable = type.getHexStr() + id + ct;

        return getStringToTransmit(INTR_TXT_ON, dataVariable);    
    }

    public String intOff() {
        String dataVariable = "417031";
        return sendMSG(INTR_TXT_OFF, dataVariable);    
    }

    public String setCom(int com0, int com1, int ack, int rs) {
        String comeZero = String.format("%02X", com0);
        String comeOne = String.format("%02X", com1); 
        String ackSet = String.format("%02X", ack);
        String rsSet = String.format("%02X", rs);
        String dataVariable = comeZero + comeOne + "00" + ackSet + rsSet + "0000";

        return sendMSG(SET_COMM_SPD, dataVariable);    
    }

    //TCP/IP 맥어드래스 명명과 같이 한다. 
    public String setId(int id) {
        return sendMSG(SET_ID, String.format("%012X", id));
    }

    public String getId() {
        return sendMSG(GET_ID, "000000000000");
    }

    public String getVersion() {
        String dataVariable = "0EF2F0FA0000";

        return sendMSG(GET_VERSION, dataVariable);
    }

    public String setClock() {
        LocalDateTime currTm = LocalDateTime.now();
        String dataVariable = String.format("%04x%02x%02x%02x%02x%02x%04x", currTm.getYear(), 
                currTm.getMonthValue(), currTm.getDayOfMonth(), currTm.getHour(), currTm.getMinute(), 
                currTm.getSecond(), (int)(currTm.getNano()/1000000));
//        String dataVariable = "07DF 0C 1D 0C 14 00 0000"; // 년(2), 월(1), 일(1), 시(1), 분(1), 초(1), 밀리(2)
                                    // 2015 12 29 12 20(분) 00(초) 0000
        return sendMSG(SET_CLOCK, dataVariable);    
    }

    public static boolean isRtoLFlowType(EffectType startEffect) {
        if (startEffect == EffectType.FLOW_RtoL 
                || startEffect == EffectType.BLINK_RtoL_GREEN 
                || startEffect == EffectType.BLINK_RtoL_RED)
            return true;
        else
            return false;
    }
    
    public static boolean isLtoRFlowType(EffectType startEffect) {
        if ( startEffect == EffectType.FLOW_LtoR 
                || startEffect == EffectType.BLINK_LtoR_GREEN 
                || startEffect == EffectType.BLINK_LtoR_RED)
            return true;
        else
            return false;
    }
}
