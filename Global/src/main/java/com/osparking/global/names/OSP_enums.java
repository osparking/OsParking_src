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
package com.osparking.global.names;

import static com.osparking.global.Globals.language;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.BOTTOM_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.DEFAULT_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.TOP_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.VEHICLE_TAB_TITLE;
import static com.osparking.global.names.ControlEnums.LabelContent.TAB_LABEL;
import com.osparking.global.names.ControlEnums.Languages;
import static com.osparking.global.names.ControlEnums.Languages.ENGLISH;
import static com.osparking.global.names.ControlEnums.Languages.KOREAN;

/**
 *
 * @author Open Source Parking Inc.S
 */
public class OSP_enums {
    
    public enum ConnectionType {
        TCP_IP("TCP/IP"), 
        RS_232("RS-232");
        
        private String label;
        
        ConnectionType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }          
    }
    
    public enum EBD_Row {
        TOP(1), 
        BOTTOM(2),
        BOTH(3);
        
        private int value;
        
        EBD_Row(int numVal) {
            this.value = numVal;
        }

        public int getValue() {
            return value;
        }            
    }
    
    public enum E_BoardType {
        Simulator("모의장치", "Simulator"), 
        LEDnotice("LED노티스", "LEDnotice");
        
        private String[] label = new String[Languages.values().length];        
        
        E_BoardType(String korean, String english) {
            label[KOREAN.ordinal()] = korean;
            label[ENGLISH.ordinal()] = english;
        }

        public String toString() {
            return label[language.ordinal()];
        }          
    }
    
    public enum GateBarType {
        Simulator("모의장치", "Simulator"), 
        NaraBar("나라바", "LEDnotice");
        
        private String[] label = new String[Languages.values().length];        
        
        GateBarType(String korean, String english) {
            label[KOREAN.ordinal()] = korean;
            label[ENGLISH.ordinal()] = english;
        }

        public String toString() {
            return label[language.ordinal()];
        }         
    }
    
    public enum CameraType {
        Simulator("모의장치", "Simulator"), 
        Blackfly("블랙플라이", "Blackfly");
        
        CameraType(String korean, String english) {
            label[KOREAN.ordinal()] = korean;
            label[ENGLISH.ordinal()] = english;
        }
        
        private String[] label = new String[Languages.values().length];        

        public String toString() {
            return label[language.ordinal()];
        }    
    }

    public enum VehicleOds {
        PlateNumber,
        DriverSN,
        Notification,
        Whole,
        Permitted,
        Causes,
        OtherInfo
    }   
    
    public enum VehicleCol {
        RowNo(0),
        PlateNumber(1),
        Name(2),
        Affiliation(3),
        Building(4),
        OtherInfo(5),
        CellPhone(6),
        Phone(7),
        Notification(8),
        Whole(9),
        Permitted(10),
        Causes(11),
        Creation(12),
        Modification(13),
        SeqNo(14); // driver's sequence number

        private int numVal;

        VehicleCol(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }    
    }    

    public enum PWStrengthLevel {
        FourDigit,
        SixDigit,
        Complex
    }    
    
    public enum SearchPeriod {
        OneHour,
        OneDay,
        GivenPeriod
    }    
    
    public enum ODS_TYPE {
        AFFILIATION("소속", "AFFILI'"),
        BUILDING("건물", "Bldg"),
        DRIVER("운전자", "Driver"),
        VEHICLE("등록차량", "Vehicle");
        
        ODS_TYPE(String korean, String english) {
            label[KOREAN.ordinal()] = korean;
            label[ENGLISH.ordinal()] = english;
        }

        private String[] label = new String[Languages.values().length];
        
        public String toString() {
            return label[language.ordinal()];
        }        
    }    
    
    public enum DriverCol {
        RowNo(0),
        DriverName(1),
        CellPhone(2),
        LandLine(3),
        AffiliationL1(4),
        AffiliationL2(5),
        BuildingNo(6),
        UnitNo(7),
        SEQ_NO(8);

        private int numVal;

        DriverCol(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }      
    
    public enum OPType {
        START, STOP        
    }
    
    public enum MsgCode {
        AreYouThere, IAmHere, DeviceID, ID_Ack, CarImage, Img_ACK,
        Open, Open_ACK, EBD_Default, 

        /**
         * Interrupt E-Board upper row display and display current message temporarily.
         */
        EBD_INTERRUPT1, 

        /**
         * Interrupt E-Board lower row display and display current message temporarily.
         */
        EBD_INTERRUPT2, 

        /**
         * Change default display of the E-Board higher row permanently.
         */
        EBD_DEFAULT1,

        /**
         * Change default display of the E-Board lower row permanently.
         */
        EBD_DEFAULT2,

        EBD_GetID, EBD_ACK, EBD_ID_ACK, JustBooted
    }

    public enum E_Board_Status {
        NORMAL, 
        CarIN
    }

    public enum VersionType {
        DEVELOP, TESTRUN, RELEASE
    }    
    
    /**
     *  General System Operation Logging Level
     */ 
    public enum OpLogLevel {
        LogAlways, // log this operation always
        SettingsChange, // log system settings change
        UserCarChange // log system settings change plus E-board settings change
    }    
    
    public enum DeviceType {
        Camera ("카메라",  "Camera"),
        E_Board ("전광판",  "E-Board"),
        GateBar ("차단기",  "Gate Bar");
        DeviceType(String korean, String english) {
            label[KOREAN.ordinal()] = korean;
            label[ENGLISH.ordinal()] = english;
        }
        
        private String[] label = new String[Languages.values().length];
        
        public String getContent() {
            return label[language.ordinal()];
        }        
    }    
    
    public enum EBD_CycleType {
        EBD_FLOW_CYCLE,
        EBD_BLINK_CYCLE
    }
    
    public enum PermissionType {
        /**
         * Car at the entry gate is allowed to park at this parking lot. So, the gate was opened
         * to allow the car to come on in.
         */
        ALLOWED, DISALLOWED, UNREGISTERED, BADTAGFORMAT       
    }

    public enum EBD_DisplayUsage {        
        DEFAULT_TOP_ROW(1, 
                DEFAULT_TAB_TITLE.getContent() + " " + TAB_LABEL.getContent() +
                        ", " + TOP_TAB_TITLE.getContent()),      // used for the top row when no vehicle arrives
        DEFAULT_BOTTOM_ROW(2,
                DEFAULT_TAB_TITLE.getContent() + " " + TAB_LABEL.getContent() +
                        ", " + BOTTOM_TAB_TITLE.getContent()),   // used for the bottom row when no vehicle arrives 
        CAR_ENTRY_TOP_ROW(3,
                VEHICLE_TAB_TITLE.getContent() + " " + TAB_LABEL.getContent() +
                        ", " + TOP_TAB_TITLE.getContent()),      // used for the top row when vehicle arrived at the gate entry
        CAR_ENTRY_BOTTOM_ROW(4,
                VEHICLE_TAB_TITLE.getContent() + " " + TAB_LABEL.getContent() +
                        ", " + BOTTOM_TAB_TITLE.getContent());   // used for the bottom row when vehicle arrived at the gate entry
        private final int val;
        private final String panelName;
        private EBD_DisplayUsage(int v, String name) { 
            val = v; 
            panelName = name;
        }
        
        /**
         * A unique integer number assigned for each usage row.
         * 
         * @return integer value starting 1
         */
        public int getVal() { return val; }        
        public String getPanelName() { return panelName; }        
    }

    public enum EBD_ContentType {
        VERBATIM(VERBATIM_CB_ITEM.getContent()),           // display as it is (character by character)
        VEHICLE_TAG(VEHICLE_TAG_CB_ITEM.getContent()),        // car license tag number
        REGISTRATION_STAT(REGISTRATION_STAT_CB_ITEM.getContent()),      // registered, un-registered, parking-restricted
        GATE_NAME(GATE_NAME_CB_ITEM.getContent()),        // gate name which is stored in the system settings already 
        CURRENT_DATE(CURRENT_DATE_CB_ITEM.getContent()),     // format: 20XX-12-31(Mon.)
        CURRENT_TIME(CURRENT_TIME_CB_ITEM.getContent()),     // format: AM/PM HH:MM:SS
        CURRENT_DATE_TIME(CURRENT_DATE_TIME_CB_ITEM.getContent()); // format: 20XX-12-31(Mon.) HH:MM:SS AM/PM
        
        private String label;
        
        EBD_ContentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }           
    }

    public enum EBD_Effects {
        BLINKING(BLINKING_CB_ITEM.getContent()),
        LTOR_FLOW(LTOR_CB_ITEM.getContent()),
        RTOL_FLOW(RTOL_CB_ITEM.getContent()),
        STILL_FRAME(STILL_FRAME_CB_ITEM.getContent());
        private String label;
        
        EBD_Effects(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }        
    }
    
    public enum EBD_Fonts {
        Sans_Serif(SANS_SERIF.getContent()),
        Microsoft_NeoGothic(MS_NEO_GOTHIC.getContent()),
        Dialog("Dialog"),
        DialogInput("Dialog Input"),
        Monospaced("Monospaced");
        
        private String label;
        
        EBD_Fonts(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }            
    }   
    
    public enum EBD_Colors {
        BLACK(BLACK_COLOR_CB_ITEM.getContent()),
        RED(RED_COLOR_CB_ITEM.getContent()),
        ORANGE(ORANGE_COLOR_CB_ITEM.getContent()),
        GREEN(GREEN_COLOR_CB_ITEM.getContent()),
        BLUE(BLUE_COLOR_CB_ITEM.getContent());
        
        private String label;
        
        EBD_Colors(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }    
    }
    
    public enum DisplayArea {
        TOP_ROW(1), 
        BOTTOM_ROW(2), 
        WHOLE_AREA(3);
        
        int value;
        DisplayArea(int value) {
            this.value = value;
        }
        
        int getValue() {
            return value;
        }
    }
}
