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
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.RTOL_CB_ITEM;
import static com.osparking.global.names.ControlEnums.LabelContent.LANGUAGE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.TYPE_LABEL;
import static com.osparking.global.names.ControlEnums.Languages.ENGLISH;
import static com.osparking.global.names.ControlEnums.Languages.KOREAN;
import static com.osparking.global.names.ControlEnums.MsgContent.OS_PARKINGLOT;
import static com.osparking.global.names.ControlEnums.TitleTypes.ARRIVAL_TIME_PANEL_TITLE;
import static com.osparking.global.names.DB_Access.gateNames;
import static com.osparking.global.names.OSP_enums.CameraType.CarButton;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ControlEnums {
  
    public enum Languages{
        KOREAN, ENGLISH
    }

    public enum TableType {  
        L1_TABLE("상위 소속 ", "High Affili'"),
        L2_TABLE("부서 목록", "Low Affili'"),
        Building("건물(동)", "Building"),
        UnitTab("호실 목록", "Unit(Rm)"),
        Vehicles("등록 차량 목록", "Registerd Cars");
        
        TableType(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        private String[] contents = new String[Languages.values().length];
        public String getContent() {
                return contents[language.ordinal()];
        }
    }    
    
    public enum TitleTypes {
        CHECK_IP_TITLE("장치 IP 주소 확인 필요", "Check Device IP Address"),
        CAR_ARRIVALS_TITLE("최근 차량 도착(시간 역순)", "Recent Car Arrivals(Recent First)"),
        SETTINGS_TITLE("시스템 설정", "System Settings"),
        LOGIN_DIALOG_TITLE("오즈파킹 로그인", "OsParking Login"),
        LOGIN_FRAME_TITLE("주차관리 로그인", "Attendant Login"),
        ATTLIST_FRAME_TITLE("사용자 정보관리", "Login ID Management"),
        AFFILI_BUILD_FRAME_TITLE("소속 및 건물", "Affiliation and Building"),
        VEHICLESFORM_FRAME_TITLE("차량 등록 관리", "Vehicle Registration"),
        DRIVER_SELECTION_FRAME_TITLE("운전자 선택", "Select Driver"),
        DRIVER_LIST_FRAME_TITLE("운전자 목록", "Driver List"),
        CAR_ARRIVALS_FRAME_TITLE("차량 도착 목록", "Vehicle Arrival List"),
        SEARCH_CRITERIA_PANEL_TITLE("검색 기준", "Search Criteria"),
        SEARCH_RESULT_TITLE("검색 결과", "Search Result"),
        ARRIVAL_PROPERTIES_PANEL_TITLE("도착 속성", "Arrival Properties"),
        ARRIVAL_TIME_PANEL_TITLE("도착 기간", "Arrival Period"),
        VEHICLE_ARIIVAL_DETAILS_PANEL_TITLE("도착차량 상세정보", "Vehicle Arrival Details"),
        VEHICLE_ARRIVAL_LIST_PANEL_TITLE("도착차량 목록", "Vehicle Arrival List"),
        FULL_SIZE_IMAGE_FRAME_TITLE("입차 사진", "car arrival image"),
        DISALLOWED_CAR_FRAME_TITLE("허가되지 않은 차량", "DisAllowed Car"),
        VISITING_CAR_FRAME_TITLE("방문자 환영", "Welcome Visiter"),
        LOGIN_RECORD_FRAME_TITLE("사용자 로그인 기록", "User Login Record"),
        RUN_RECORD_FRAME_TITLE("오즈파킹 실행 기록", "OsParking Run Record"),
        GATE_PANEL_TITLE("최근 도착 차량", "Recent car Arrivals"),
        SYSTEM_SETTINGS_FRAME_TITLE("시스템 설정", "System Settings"),
        E_BOARD_SETTINGS_FRAME_TITLE("전광판 설정", "E-board Settings"),
        DEFAULT_PANEL_TITLE("평시", "Default"),
        VEHICLE_PANEL_TITLE("차량", "Vehicle"),
        TOP_PANEL_TITLE("상단", "TOP"), 
        REBOOT_POPUP("자동 종료 안내", "Reboot System"), 
        SELECT_BTN("선택(T)", "Select"),        
        BOTTOM_PANEL_TITLE("하단", "BOTTOM"),
        DisallowedTitle("임시 불허 차량", "Temporarily Disallowed Car");
        
        TitleTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
            return contents[language.ordinal()];
        }        
    }
    
    public enum LabelContent {
        EMPTY_REASON("(기록 없음)", "(No Record)"), 
        CLOSED_LABEL("(폐쇄)", "(closed)"), 
        STOPPED_LABEL("(일단정지)", "(stopped)"), 
        RATE_LABEL("비율 : ", "Rate : "), 
        COM_PORT_ID_LABEL("COM 포트 ID : ", "COM Port ID : "), 
        LEVEL_LABEL("레벨 : ", "Level : "), 
        DEVICE_LABEL("장치", "Device"), 
        TYPE_LABEL("유형", "Type"), 
        CONN_LABEL("연결 방식", "Connection"), 
        IP_ADDR_LABEL("IP 주소", "IP address"), 
        PORT_LABEL("포트", "Port"), 
        levelSelectionNotice("허용되는 연산의 범위를 결정하기 위한 선택!", 
                "Selection that determines allowed operations!"), 
        NO_IMAGE_MESSAGE("사진이 없습니다.", "No Image Exists"), 
        RecogedTagLabel("식별된 차량번호", "Recognized Tag No."), 
        RegisteredTagLabel("등록된 차량번호", "Registered Tag No."), 
        BlinkNotPermitted("주차 한시적 불허 차량", "Parking Temporarily Not Permitted"),
        DisallowReason("불허 사유", "Why?"), 
        VISIT_REASON_LABEL("상세 목적", "Detailed Reason"), 
        LOGIN_ID_LABEL("아이디", "User ID"), 
        PANEL_LABEL("패널", "Panel"), 
        FIELD_LABEL("요소", "Field"), 
        TAB_LABEL("탭", "Tab"), 
        ID_LABEL("아이디 : ", "User ID : "), 
        PW_LABEL("비밀번호",  "Password"), 
        MODE_LABEL("작업 상태 : ", "Form Mode : "), 
        MODIFY_COND("\u203B 정보 수정 조건", "\u203B Legend"), 
        CREATE_COND("\u203B 정보 생성 조건", "\u203B Legend"), 
        DATA_COND("\u203B 필수 선택 구분", "\u203B Legend"), 
        DATA_REQUIRED("\u203B 자료 입력 구분", "\u203B Required Item"), 
        REQUIRED1_LABEL(": 필수", ": Required"), 
        REQUIRED2_LABEL(": 최소 1 개", ": 1 or more"),
        USER_ID_LABEL("사용자 ID: ", "My ID: "),
        NAME_LABEL("이름",  "Name"),
        NONE_EXIST("(없음)", "(none)"),
        CELL_PHONE_LABEL("휴대전화", "Cell Phone"),
        PHONE_LABEL("유선전화",  "Phone"),
        EMAIL_LABEL("이메일",  "E-Mail"),
        CHANGE_PW_LABEL("비밀번호 변경",  "Change Password"),
        NEW_PW_LABLE("새 비밀번호",  "New Password"),
        REPEAT_PW_LABEL("새 비밀번호 반복",  "Repeat Password"),
        PW_LABLE("비밀번호",  "Password"),
        REPEAT_PW("비밀번호 반복",  "Repeat Password"),
        MY_PW_LABEL("비밀번호",  "Password"),
        CREATION_LABEL("자료 생성일",  "Creation Date"),
        AFFILIATION_LIST_LABEL("상위 소속 목록",  "Higher Affiliations"),
        LOWER_LIST_LABEL("소속 부서 목록",  "Lower Affiliations"),
        BUILDING_LIST_LABEL("건물(동) 목록",  "Building Numbers"), 
        ROOM_LIST_LABEL("호실 목록",  "Rooms of Building"), 
        HELP_AFFIL_LABEL("소속 명칭 ods 파일 형식",  "Affiliation name list ods file content"), 
        HELP_BUILDING_LABEL("건물 호실 ods 파일 형식", "Building room number list ods file content"), 
        HELP_DRIVER_TITLE("운전자 ods 파일 형식", "Driver list ods file content"), 
        HELP_VEHICLE_ODS("등록차량 ods 파일 형식", "Vehicle list ods file format"), 
        AFFILI_MODE_STRING("소속, 부서", "Affiliations"), 
        BUILDING_MODE_STRING("건물, 호실", "Building, Units"), 
        WORK_PANEL_LABEL("작업 대상 : ",  "Work Panel :"), 
        SEARCH_MODE_LABEL("검색",  "Searching"), 
        CREATE_MODE_LABEL("생성",  "Creation"), 
        MODIFY_MODE_LABEL("수정",  "Modification"), 
        SEARCH_LABEL("검색키",  "S'Key"), 
        ORDER_LABEL("순번",  "Seq No"), 
        CAR_TAG_LABEL("<html>차량번호(<u>N</u>)</html>",  "<html>Tag (<u>N</u>)o.</html>"), 
        MODI_DATE_LABEL("최근변경",  "Modi' Date"),
        NOTIFICATION_LABEL("세대통보",  "Notification"),
        EXACT_COMP_LABEL ("전체비교",  "Exact Comp'"),
        PARK_ALLOWED_LABEL("주차허용",  "Park Allowed"),
        REASON_LABEL ("불허사유",  "Reason"),
        OTHER_INFO_LABEL  ("기타정보",  "Other Info'"),
        REGI_DATE_LABEL ("최초등록",  "Regi' Date"),
        COUNT_LABEL ("자료 수 :",  "Count : "),
        REQUIRED3_LABEL ("*: 필수 항목",  "*: required field"),
        GATE_NAME_LABEL ("입구",  "Gate"),
        WHERE_TO_LABEL ("방문 대상",  "Visit Target"),
        ATTENDANT_LABEL ("직원",  "Clerk"),
        BAR_OP_LABEL ("차단기",  "Bar Operation"),
        ARRIVAL_TIME_LABEL("도착",  "Date"),       
        RECOGNIZED_LABEL ("인식",  "Recog'"),
        REGISTERED_LABEL("등록",  "Regi'"),
        ARR_TM_LEGEND("월-일 시:분",  "Mo-D H:Mi"),
        AFFILIATION_LABEL ("소속부서",  "Affiliations"),
        BUILDING_LABEL ("소속건물",  "Building"),
        VISIT_BUILDING ("건물(동)",  "Bldg."),
        VISIT_UNIT ("방(호)",  "Room"),
        ROOM_LABEL("호실",  "Room No."),
        VISIT_PURPOSE_LABEL("방문 목적",  "V' Reason"),
        VISIT_TIME_LABEL ("방문 시각",  "Visit time"),
        VISIT_WHERE_LABEL ("방문 하는 곳",  "Where to go"),
        FILE_SIZE_LABEL ("파일 크기",  "File Size"),
        RECORD_COUNT_LABEL("차량 합계",  "Record Count"),
        LAST_1HOUR_LABEL ("최근 1시간",  "Last 1 hour"),
        LAST_24HOURS_LABEL ("최근 24시간",  "Last 24 hour"),
        DURATION_SET_LABEL("기간지정",  "Duration"),
        VISIT_TARGET_LABEL("방문 대상",  "Visit Place"),
        OPEN_LABEL("<HTML>입구 개방(<U>P</U>)</HTML>",  "OPen Gate"),
        
        VISIT_OVERVIEW("방문 요약",  "Overview"),
        NON_REGI_TAG2_LABEL("방문부서",  "Visit Departments"),
        DISALLOW_LABEL("입차 불허(C)",  "Close Gate"),
        STATUS_LABEL ("상황 게시",  "Recent Events"),
        GATE_LABEL ("입구",  "Gate"),
        CAMERA_LABEL ("카메라",  "Camera"),
        E_BOARD_LABEL ("전광판",  "E-Board"),
        GATE_BAR_LABEL ("차단기",  "G-Bar"),
        DISALLOWED_LABEL("일시적으로 허용되지 않은 차량입니다.", "Car Temporarily Not Permitted"),
        STATISTICS_SIZE_LABEL("성능 통계 모집단 크기",  "Statistics Population Size"),
        RECORD_PASSING_LABEL("통과 성능 기록",  "Record Passing Delay"),
        PASSWORD_LEVEL_LABEL("비밀번호 난이도",  "Password Complexity Level"),
        HOWTO_CHANGE_PW_STRENGTH("\u203B 비밀번호 난이도 설정 단계:",
                "\u203B Setting Password Complexity Level:"),
        LOGGING_LEVEL_LABEL("일반연산 로깅 레벨",  "General Operation Logging Level"),
        LANGUAGE_LABEL("표시 언어",  "Language Chooser"),
        MAX_LINE_LABEL("메인 상황게시 최대 행수",  "Recent Event Line Max"),
        GATE_NUM_LABEL("입구 수",  "Number of Gates"),
        IMG_KEEP_LABEL("사진 저장 기간",  "Image Keeping Duration"),
        VEHICLE_IMG_SIZE_LABEL("차량 사진 크기",  "Vehicle Image Size"),
        VEHICLE_IMG_WIDTH_LABEL("너비",  "Width"),
        VEHICLE_IMG_HEIGHT_LABEL("높이",  "Height"),
        CAMERA_IP_ADDRESS_LABEL("카메라 IP 주소",  "Camera IP Address"),
        GATE_BAR_IP_ADDRESS_LABEL("차단기 IP 주소",  "GateBar IP Address"),
        E_BOARD_IP_ADDRESS_LABEL("전광판 IP 주소",  "E-Board IP Address"),
        CYCLE_LABEL("전광판 표시 주기",  "E-board Display Cycle"),
        BLINGKING_LABEL("깜빡임",  "Blinking"),
        FLOWING_LABEL("흐름",  "Flowing"),
        DISPLAY_TYPE_LABEL("표시 유형", "Display Type"), 
        MESSAGE_LABEL ("문자열",  "Message"),
        COLOR_LABEL ("색상",  "Color"),
        FONT_LABEL ("폰트",  "Font"),
        EFFECT_LABEL ("효과",  "Effect"),
        SEARCH_PERIOD_LABEL("검색 기간",  "Search Period"),
        SECONDS_LABEL ("초",  "seconds"),
        VEHICLES_LABEL (" 대",  " vehicles"),
        LOT_NAME_LABEL("주차장 이름",  "Parking Lot Name"),
        LOWER_LABEL(" 부서 목록", "Lower affiliations of"),
        IP_ADDR_ERROR_1("[" + IP_ADDR_LABEL.getContent() + "] 값 오류",
                "[" + IP_ADDR_LABEL.getContent() + "] format error"),
        IP_ADDR_ERROR_2("바른 형식 : ", "Right format : "),
        LANGUAGE_HELP_1(" - 오즈파킹 각종 GUI 언어", " - OsParking GUI Language"),
        LANGUAGE_HELP_2(" - 날짜 입력 GUI 언어", " - Date Input Panel GUI Language"),
        FLOW2L_CONF_0("전광판 행 : ", "E-Board Row : "),
        FLOW2L_CONF_1("위 행의 '" + MESSAGE_LABEL.getContent() + "'이 전광판을 초과합니다.", 
                "Above row's '" + MESSAGE_LABEL.getContent() + " exceeds E-board width."),
        FLOW2L_CONF_2("'" + EFFECT_LABEL.getContent() + "'" + "를 '" + 
                RTOL_CB_ITEM.getContent() + "'으로 바꾸시겠습니까?", 
                "Want to change '" + EFFECT_LABEL.getContent() + "' to '" + RTOL_CB_ITEM.getContent() + "'?"),

        WRONG_LANG_DIALOG_1("현재 선택된 언어는 아직 지원되지 않습니다.",
                "New language selection is not supported yet."),
        WRONG_LANG_DIALOG_2("- 현 선택 언어 : ", "- Current selection : "),
        WRONG_LANG_DIALOG_3("아래 지원되는 언어 중에서 선택하십시오!",
                "Select among from the supported languages!"),
        WRONG_LANG_DIALOG_4("※지원되는 언어 : ", "- Supported language : "),
        
        CHECK_IP_DIALOG_1("아래 실제 장치의 IP 주소가 127.0.0.1 입니다.", 
                "Following real hardware device IP is 127.0.0.1"),
        CHECK_IP_DIALOG_4("이는 설정 오류일 수 있으니 확인하십시오.", 
                "It could be an error, so ckeck it again."),
        DEV_CONN_ERROR_1("다음 장치가 연결되지 않았습니다.", "Below device is not connected"),
        DEV_CONN_ERROR_2("위치 : ", "Location : "),
        DEV_CONN_ERROR_3("장치 : ", "Device : "),
        DEV_CONN_ERROR_4("따라서, 이 장치에 대한 검검이 필요합니다.", "So, you need to check this device."),
        
        CHOOSE_PANEL_DIALOG("사용할 패널(제목, 목록, 분홍 버튼)을 선택하세요!", 
                "Click the panel(title, table, pink button) to use!"),
        SELECT_DRIVER_HELP("운전자 행 클릭 후 [선택(T)] 버튼 사용",
                "Click a driver row, then use [Select] button!"), 
        CREATE_SAVE_HELP("자료 생성 후 [엔터] 키로 마감(=저장)할 것!",
                "Press enter after the data creation!"),
        REQUIRE_FIELD_NOTE("필수 입력 항목임!", "It's a Required field!"),
        FOCUS_MOVE_NOTE(
                "열 이동: 탭, Shift+탭, 마우스", 
                "Col' move: Tab, Sh+Tab, Mouse"),
        PERIOD_SETTING_RESULT(
                ARRIVAL_TIME_PANEL_TITLE.getContent() + " 옵션 선택이" + System.lineSeparator() +
                        "시스템에 저장되었습니다.", 
                ARRIVAL_TIME_PANEL_TITLE.getContent() + " option selection" + System.lineSeparator() +
                        "recorded to OsParking."),
        
        UPDATE_SAVE_HELP("자료 갱신 후 [엔터] 키로 마감(=저장)할 것!",
                "Press enter after the data modification!"),
        DEFAULT_USER_LINE1("[생성된 기본 아이디]", "[Created Default User ID]"),
        DEFAULT_USER_LINE2("- 아이디 목록: ", "- ID List: "),
        OVERLAPPED_PORT_DIALOG_1("아래 두 장치가 같은 포트를", "Two devices are set to use the same "),
        OVERLAPPED_PORT_DIALOG_2("사용하게 잘못 설정되었습니다.", "COM port. It is an error."),
        OVERLAPPED_PORT_DIALOG_3("그래도 설정을 저장하겠습니까?", "Do you still want to save the settings?"),
        OVERLAPPED_PORT_DIALOG_4("사용 포트 : ", "Used Port : "),
        
        LOGGIND_DIALOG_1("다음 '필수 항목'만 기록됨 :", "Following 'Mandatory Items' are logged :"),
        LOGGIND_DIALOG_2(" - 오즈파킹 가동 및 정지 시각", " - System start and stop time"),
        LOGGIND_DIALOG_3(" - 오래되어 삭제된 레코드 수", " - Number of deleted old records"),
        LOGGIND_DIALOG_4(" - 오래되어 삭제된 영상 수", " - Number of deleted old images"),
        LOGGIND_DIALOG_5(" - 삭제된 로그 폴더 및 파일 수", " - Number of deleted log folders and text files"),
        LOGGIND_DIALOG_6(" - 삭제된 로그 폴더 및 파일 경로", " - File path of deleted log folders and text files"),
        LOGGIND_DIALOG_A("추가적으로 기록되는 항목 : ", "Additionally logged items : "),
        LOGGIND_DIALOG_B(" - 시스템 설정 변경", " - System Settings Change"),
        LOGGIND_DIALOG_C(" - 사용자 정보 변경", " - Attendant/User Info Change"),
        LOGGIND_DIALOG_D(" - 운전자 정보 변경", " - Drivers Info Change"),
        LOGGIND_DIALOG_E(" - 등록차량 정보 변경", " - Vehicles Info Change"),
        LOGGIND_DIALOG_F(" - 전광판 설정 변경", " - E-Board Settings Change"),
        LIMIT_DESCRIPTION("길이 한계", "Length Limit"),
        
        ARTI_CURR_ERR_LIMIT_1("현재 인공 오류 비율(=", "Current error rate(="),
        ARTI_CURR_ERR_LIMIT_2(") 이 최대치임!", ") is max!"),
        ARTI_CURR_ERR_LIMIT_b(") 이 최소치임!", ") is min!"),
        ODS_SAVE_DIALOG_1("ods 파일이 아래와 같이 생성되었습니다.", "ods file created as follows."),
        ODS_SAVE_DIALOG_2("- 폴더명 : ", "- Folder : "),
        ODS_SAVE_DIALOG_3("- 파일명 : ", "- File name : "),
        NUMBER_LIMIT_DESCRIPTION("번호 길이 한계", "Number Length Limit")
        ;
        
        LabelContent(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
            return contents[language.ordinal()];
        }
    }
    
    public enum ButtonTypes {
        LOGIN_BTN("로그인(L)", "Login"),
        SAMPLE_BTN("샘플(P)", "Sample"),
        SAMPLE2_BTN("샘플(S)", "Sample"),
        CLOSE_BTN("닫기(C)", "Close"),
        SAVE_BTN("저장(S)", "Save"),
        CREATE_BTN("생성(R)", "Create"),
        DELETE_BTN("삭제(D)", "Delete"),
        MODIFY_BTN("수정(M)", "Modify"),
        CANCEL_BTN("취소(C)", "Cancel"),
        SEARCH_BTN("검색(S)", "Search"),
        SAVE_AS_BTN("파일저장(A)", "Save As"),
        ID_CHECK_BTN("<HTML>중복검사(<U>K</U>)</HTML>", "<HTML>Check(<U>K</U>)</HTML>"),
        EMAIL_CHECK_BTN("<HTML>중복검사(<U>L</U>)</HTML>", "<HTML>Check(<U>L</U>)</HTML>"),
        CLEAR_BTN("초기화(L)", "Clear"),
        DELETE_ALL_BTN("전체삭제(E)", "dElete All"),
        READ_ODS_BTN("ods읽기", "read ods"),
        SAVE_ODS_BTN("ods저장(A)", "SAve Ods"),
        DELETE_ALL_NO_SHORT_BTN("전체삭제", "Delete All"),
        READ_ODS_NO_SHORT_BTN("시트읽기", "Read ods"),
        SAVE_NO_SHORT_BTN("저장", "Save"),
        CANCEL_NO_SHORT_BTN("취소", "Cancel"),
        OWNER_BTN("운전자(D)", "Driver"),
        MANAGE_BTN("운전자 관리(M)", "Manage Drivers"),
        FIX_IT_BTN("설정(F)", "Fix It"),
        SET_BUTTON("설정(S)", "Settings"),
        ARRIVALS_BTN("<HTML>도착기록(<U>A</U>)</HTML>", "<HTML><U>A</U>rrivals</HTML>"),
        VEHICLES_BTN("<HTML>차량관리(<U>V</U>)</HTML>", "<HTML><U>V</U>ehicles</HTML>"),
        USERS_BTN("<HTML>사용자(<U>U</U>)</HTML>", "<HTML><U>U</U>sers</HTML>"),
        CAR_ARRIVAL_BTN("입차버튼(C)", "CarEntry"),
        STATISTICS_BTN("통계(T)", "Statistics"),
        GATE_BAR_OPEN_BTN("차단기 열림(O)", "Open Bar"),
        GATE_BAR_CLOSE_BTN("차단기 닫힘(C)", "Close Bar"),
        E_BOARD_SETTINGS_BTN("세부 설정", "Content Settings");
        
        ButtonTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }        
    }

    public enum FormModeString {  
        SEARCH("검색 ", "Searching"),
        MODIFY("수정", "Modifying"),
        CREATE("생성", "Creating");
        
        FormModeString(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        private String[] contents = new String[Languages.values().length];
        public String getContent() {
                return contents[language.ordinal()];
        }
    }
        
    public enum MsgContent {
        OS_PARKINGLOT("오즈 주차장", "OS Parking Lot"), 
        RESEND("재전송율: ", "resend/open: "), 
        NO_STAT("통계 자료 없슴", "No command statistics"), 
        ACK_MAX(", 최대: ", ", max: "), 
        ACK_AVG("회신 지연--평균", "ACK delay--avg"), 
        OPEN_CMD("개방수", "Opens"), 
        DISPLAY_CMD("표시수", "Displays"), 
        CAMERA_SIM("모의카메라", "Camera Sim'"), 
        E_BOARD_SIM("모의전광판", "E-board Sim'"), 
        GATEBAR_SIM("모의차단기", "Gate bar Sim'"), 
        P_DELAY_DEF_1("* 통과 지연 정의 : ", "*Passing Delay Definition:"), 
        P_DELAY_DEF_2("차량 영상 첫 바이트 도달", "first byte arrival of car image"), 
        P_DELAY_DEF_3("차단기 개방 지시 인지 회신", "gate bar open ACK arrival"), 
        AFFILI_DIAG_L1("다음 상위 소속 명칭을 변경합니까?", 
                "Want to change the following higher affiliation?"),
        AFFILI_DIAG_L2(" - 상위 소속 : ", 
                " - Higher Affiliation : "),
        AFFILI_DIAG_L3(" - 하위 부서 수 : ", 
                " - Number of lower affiliations : "),
        AFFILI2_DIAG_L1("다음 하위 부서를 변경합니까?", 
                "Change below lower affiliation?"),
        AFFILI2_DIAG_L2(" - 하위 부서 : ", " - Lower affiliation : "),
        AFFILI2_DIAG_L3(" - 소속 상위 부서 : ", " - Higher affiliation : "),
        AFFILI_DEL_L1("다음 소속 및 그 하위 소속을 삭제합니까?", 
                "Want to delete below higher and lower affiliations?"),
        
        AFFILI_ODS_DIAG_1("소속 자료 식별 건수입니다. 로딩을 계속합니까?", 
                "Recognized Affiliation Names Count. Continue to Load?"),
        AFFILI_ODS_DIAG_2(" -상위 소속 건수 : ", " -Higher Affiliation count : "),
        AFFILI_ODS_DIAG_3(" -하위 소속 건수 : ", " -Lower Affiliation count : "),
        
        
        BLDG_ODS_DIAG_1("건물과 호실 식별 건수입니다. 로딩을 계속합니까?", 
                "Recognized Building/Unit Count. Continue to Load?"),
        BLDG_ODS_DIAG_2(" -건물 번호 건수 : ", " -Building number count : "),
        BLDG_ODS_DIAG_3(" -호실 번호 건수 : ", " -Unit number count : "),
        
        BLDG_DIAG_L1("다음 건물 번호를 변경합니까?", "Want to change below building number?"),
        BLDG_DIAG_L2("건물 번호 : ", "Building No.: "),
        BLDG_DIAG_L3("(소속 호실 수: ", "(Number of rooms : "),
        
        UNIT_DIAG_L1("다음 호실 번호를 변경합니까?", "Change below room number?"),
        UNIT_DIAG_L2(" - 호실 번호 : ", " - Room number : "),
        UNIT_DIAG_L3(" - 소속 건물 : ", " - Of building : "),
        
        BLDG_DELETE_L1("다음 건물 및 소속 호실들을 삭제합니까?", 
                "Want to delete below building and its rooms?"),
        BLDG_DELETE_L3(" (소속 호실 수: ", " (Number of Rooms : "),
        
        AFFILI_DEL_RESULT("소속 삭제 성공!", "Successful affiliation deletion!"),
        BLDG_DEL_RESULT("건물 삭제 성공!", "Successful building deletion!"),
        
        ASK_LOGOUT(", 로그아웃 원하십니까?", ", do you want to log out?"),
        AVERAGE_WORDS(" 대 평균 통과시간: ", " car passing delay average: "),
        LOG_IN("로그인", "Logged In"),
        LOG_OUT("로그아웃", "Logged Out"),
        FILE_PATH("파일경로: ", "File path: "),
        MISSING_ADMIN("admin 아이디가 ParkingLot DB에 없음!!", 
                "admin is not in the ParkingLot DB"),
        RECENT_WORD("최근 ", "Recent "),
        SYSTEM_START("오즈파킹 가동 시작", "OsParking started"),
        SYSTEM_STOP("오즈파킹 가동 중지", "OsParking stopped");
        
        MsgContent(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }
        
        public String toString() {
            return contents[language.ordinal()];
        }
    }
    
    public enum ToolTipContent {
        CAR_ENTRY_TOOLTIP("디버깅 모드에서 " + gateNames[1] + " [" +
                Camera.getContent() + "]의 [" + TYPE_LABEL.getContent() + "]을 [" + 
                CarButton + "]으로 설정하여 활성화!", 
                "Enabled if " + gateNames[1] + "'s [" + Camera.getContent() +
                        "] [" + TYPE_LABEL.getContent() + "] set to [" + CarButton + "]"),
        ENABLING_CONDITION("활성화하려면 기간을 변경하십시오.", "To enable, change period!"),
        SEARCH_BTN_TOOLTIP("검색 조건 바꾸면 활성화 됨", "To enable, change search condition."),
        CTRL_F_TOOLTIP("(Ctrl+F)", "(Ctrl+F)"),
        DRIVER_ODS_UPLOAD_SAMPLE_PNG("ods 표본 그림 보기", "Sample ods file image"),
        DRIVER_ODS_UPLOAD_SAMPLE_DOWNLOAD("ods 표본 내려받기", "Download sample file"),
        EMAIL_TOOLTIP("이메일 형태 : ", "Email format : "),
        INSERT_TOOLTIP("(입력 후 엔터 키)", "(Type and Enter)"),
        SEARCH_TOOLTIP("포커스 이동: Control+F", "To focus Click Ctrl+F"),
        LOGIN_BTN_TOOLTIP("관리자 로그인", "Click to Login"),
        CLOSE_BTN_TOOLTIP("현재 창을 닫기", "Close Window"),
        SAVE_AS_TOOLTIP("파일로 저장", "Save as file"),
        CHK_DUP_ID_TIP("이미 등록된 ID 인지 검사", "Check if ID is occupied"),
        CHK_E_MAIL_TIP("주소 형식 및 기 등록 여부 검사", "Check syntax and if already registered"),        
        ID_INPUT_TOOLTIP("영문숫자 최대 20자!", "Up to 20 alphanumeric characters"),
        NAME_INPUT_TOOLTIP("두 자 이상 입력하세요", "Please enter at least two characters"),
        PW_INPUT_TOOTLTIP("현 사용자 비번 입력!", "Current User Password!"),
        CELL_INPUT_TOOLTIP("숫자 10 혹은 11자리 입력하세요", "Enter the 10 or 11 digit number"),
        PHONE_INPUT_TOOLTIP("숫자 4자리 이상 입력하세요", "Please enter at least a four-digit number"),
        CELL_PHONE_TOOLTIP("전화 한대 이상 입력하세요", "Please enter at least one phone number"),
        NUMBER_FORMAT_ERROR_MSG("번호 값이므로 숫자만 입력 가능합니다.",
                "You many input only digits(0-9)"),
        REPEAT_PW_INPUT_TOOLTIP("한번 더 입력하세요", "Please enter it again."),
        SEARCH_INPUT_TOOLTIP("속성 값 입력", "Enter Search Key."),
        PW_FOURDIGIT_TOOLTIP("네 자리 숫자 입력 (자세한 내용: ? 클릭)", 
                "Enter a 4 digit number(for details click '?')"),
        PW_SIXDIGIT_TOOLTIP("6 자리 이상 영자 및 숫자 입력 (자세한 내용: ? 클릭)", 
                "Enter 6 or more digits of alpha-numeric(for details click '?'"),
        PW_COMPLEX_TOOLTIP("8 자리 이상 영자, 숫자 및 특수문자 입력 (자세한 내용: ? 클릭)", 
                "Enter 8 or more digits of alphabet, number, and special character(for details click '?')"),
        CAR_TAG_INPUT_TOOLTIP("챠량번호로 검색", "Search CarTag"),
        DRIVER_INPUT_TOOLTIP("운전자로 검색", "Search Driver"),
        AFFILIATION_TOOLTIP("소속으로 검색", "Search Affiliation"),
        BUILDING_TOOLTIP("건물로 검색", "Search Building"),
        OTHER_TOOLTIP("기타정보로 검색", "Search Other Info"),
        CELL_PHONE_INPUT_TOOLTIP("휴대전화로 검색", "Search Cell Phone"),
        LANDLINE_INPUT_TOOLTIP("유선전화로 검색", "Search LandLine"),
        HIGHER_TOOLTIP("상위 소속으로 검색", "Search Higher Affiliation"),
        LOWER_TOOLTIP("하위 소속로 검색", "Search Lower Affiliation"),
        UNIT_TOOLTIP("호실번호로 검색", "Search Unit"),
        CLEAR_BTN_TOOLTIP("영역 초기화", "Clears Criteria"),
        FIX_IT_BTN_TOOLTIP("선택된 라디오 버튼을 적용", "Remember Radio Button Selection"),
        CAR_TAG_TF_TOOLTIP("입력 후 [엔터]", "Type and [Enter]"),
        INC_BTN_TOOLTIP("0,1 씩 증가", "Inc by 0.1"),
        DEC_BTN_TOOLTIP("0,1 씩 감소", "Dec by 0.1"),
        SETTINGS_SAVE_TOOLTIP("변경된 내용 저장하기", "Save your changes"),
        SETTINGS_CANCEL_TOOLTIP("변경된 내용을 취소하기", "Desert changes made");
        
        ToolTipContent(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }
    }
    
    public enum ColumnHeader {
        OPTN_START("가동 시작", "Starting"),
        OPTN_STOP("가동 중지", "Shutdown"),
        STOP_DURATION("중지 기간(시:분.초)", "Stop Period(H:M.S)");
        
        ColumnHeader(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }        
    }
    
    public enum TableTypes {
        REQUIRED("필수", "Req'"),
        USER_ID_HEADER("아이디", "User ID"),
        NAME_HEADER("이름", "Name"),
        MANAGER_HEADER("매니저", "Manager"),
        CELL_PHONE_HEADER("휴대전화", "Cell Phone"),
        PHONE_HEADER("유선전화", "Phone"),
        EMAIL_HEADER("이메일", "E-mail"),
        CREATED_HEADER("생성날짜", "Created"),
        MODIFIED_HEADER("수정날짜", "Modified"),
        ORDER_HEADER("순번", "Order"),
        HIGHER_HEADER("상위 소속", "Higher Affili'"),
        LOWER_HEADER("하위 소속", "Lower Affili'"),
        BUILDING_HEADER("건물 번호", "Building"),
        ROOM_HEADER("호실 번호", "Room No."),
        HIGHER_HEADER_SC("<html>상위 소속(<u>H</u>)</html>", "Higher Affili'"),
        LOWER_HEADER_SC("<html>하위 소속(<u>L</u>)</html>", "Lower Affili'"),
        BUILDING_HEADER_SC("<html>건물 번호(<u>B</u>)</html>", "Building"),
        ROOM_HEADER_SC("<html>호실 번호(<u>R</u>)</html>", "Room No."),
        CAR_TAG_HEADER("차량번호", "Tag No."),
        DRIVER_HEADER("운전자", "Driver"),
        HIGH_LOW_HEADER("상위-하위 소속", "Higher-Lower"),
        BUILD_ROOM_HEADER("건물-호실", "Building-Room"),
        OTHER_INFO_HEDER("기타정보", "Other Info'"),
        REASON_HEADER("불허 사유", "Reason"),
        ARRIVAL_TIME_HEADER("도착일시", "Arrival Date"),
        LOGIN_TIME_HEADER( "로그인 시간",  "Login Time"),
        LOGOUT_TIME_HEADER( "로그아웃 시간",  "Logout Time"),
        DURATION_HEADER( "경과시간(시:분:초)",  "Duration(hh:mm:ss)"),
        WORK_PERIOD( "근무시간(시:분:초)",  "Work Time(hh:mm:ss)"),
        SHUTDOWN_HEADER( "시스템 종료",  "Shutdown"),
        START_HEADER("시스템 시작",  "Start Up");
        
        TableTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }        
    }
    
    public enum ATTLIST_ComboBoxTypes {
        NAME, ID
    }

    public enum DialogMessages {
        WELCOMING_OS(OS_PARKINGLOT.getContent() + " 방문을 환영합니다.",
                "Welcome to " + OS_PARKINGLOT.getContent()),
        FIRST_RUN_MSG("오즈파킹 최초 가동!", "Very First Run of OsParking!"),
        TIMED_OUT(" 시간 초과 ", " timed out "),
        CONN_REFUSED(" 연결 거부 ", " refused connection "),
        CONN_REFUSED_1(" 회", " times"),
        OsParking_FUNC_WARNING("※오즈파킹이 오작동할 수 있습니다.", "※OsParking could function improperly"),
        MISSING_PORT_DIALOG_1("장치 포트 부재 내역 : ", "Missing device port details : "),
        MISSING_PORT_DIALOG_2("  - 입구 번호 : ", "  - Gate No. :"),
        MISSING_PORT_DIALOG_3("  - 장치 종류 : ", "  - Device Type : "),
        USER_UPDATE_1("아래 사용자 정보가 갱신되었습니다.", "Below user information modified."),
        USER_UPDATE_2("- 사용자 아이디 : ", "- User ID : "),
        USER_UPDATE_A("아래 사용자 정보 갱신에 실패하였습니다.", "User information update failure Notice."),
        NAME_ADMIN("관리자", "Administrator"),
        NAME_MANAGER("운영자", "Operator"),
        NAME_GUEST("김손님", "Guest Kim"),
        PASSING_DELAY_AVG("통과 지연 평균 : ", "Passing Delay Average"),
        ARTIF_ERROR_RATE("인공 오류율 : ", "Artificial error rate: "),
        CONN_TRIAL("연결시도 장치 : ", "Device to connect : "),
        VerbatimDialog("다음 패널의 요소가 누락되었습니다.", 
                "Panel Field is missing as follows."),
        USER_DELETE_CONF_1("이 작업은 다음 두 기록도 전부 삭제합니다.", 
                "This operation also deletes following records, too."),
        USER_DELETE_CONF_2("- 기록 : 사용자 로그인, 차량 도착", "- Record : user login, car arrival"),
        USER_DELETE_CONF_3("그래도 진행하시겠습니까?", "Still want to continue?"),
        OVERWRITE_WARNING_DIALOG("이미 존재하는 아래 파일이 덧 쓰여지게 됩니다.", 
                "Existing following file will be overwritten!"),
        USER_DELETE_CONF_TITLE("많은 삭제 경고", "Much Delete Warning"),
        OVERWRITE_WARNING_TITLE("파일 덧쓰기 경고", "File Overwrite Warning"),
        PopSizeHelp1("차량 통과 지연 시간 계산을", "Determines the population"),
        PopSizeHelp2("위한 통계처리에 있어서", "size to calculate average"),
        PopSizeHelp3("모집단의 크기를 정한다.", "vehicle passing delay."),
        PopSizeHelp4("즉, 최근 통과시킨 몇 대의", "In other words, the number of"),
        PopSizeHelp5("차량에 대한 평균 처리 시간을", "cars passed recently and"),
        PopSizeHelp6("계산할지 결정한다.", "added for the average delay."),
        
        PERIOD_ERROR_DIALOG1("시작일이 종료일보다 늦을 수 없습니다. ", "End date can't precede start date!"),
        PERIOD_ERROR_DIALOG2("검색 기간을 수정하십시오!", "Please correct search range!"),
        DRIVER_ODS_READ_RESULT1("운전자 읽어들인 결과", "Driver Upload Result"),
        DRIVER_ODS_READ_RESULT2(" - 성공적으로 읽힌 운전자: ", " - Uploaded drivers: "),
        DRIVER_ODS_READ_RESULT3(" - 중복으로 제외된 운전자: ", " - Rejected duplicates: "),
        DRIVER_ODS_READ_RESULT4(" - 총 거부된 운전자 수: ", " - Total rejected drivers: "),
        
        DEV_TYPE_ERROR_MSG1("예상 밖 메시지 코드입니다.", "Unexpected message code arrived."),
        DEV_TYPE_ERROR_MSG2("메시지 코드 : ", "Message code : "),
        DEV_TYPE_ERROR_MSG3("을 확인하십시오.", " needs to be checked."),
        DEV_TYPE_ERROR_MSG4("확인 위치 : ", "Checking Steps : "),
                
        READ_DRIVER_ODS_CONF_1("ods 파일에서 다음 운전자 정보가 식별되었습니다.",
                "Following drivers exists in the ods file."),
        READ_DRIVER_ODS_CONF_2(" - 운전자 항목 수 : ", " - Driver count : "),
        READ_DRIVER_ODS_CONF_3("이들 정보를 읽어 옵니까?",
                "Want to read this information?"),
        
        DELETE_ALL_DRIVER_P1 ("아래 운전자 및 차량 정보를 삭제합니까?", "Want to delete below drivers and vehicles?"),
        DELETE_ALL_DRIVER_P2 (" - 운전자 정보 건수 : ", " - Driver record count : "),
        DELETE_ALL_DRIVER_P3 (" - 소유 차량 건수 : ",    " - Owned vehicle count : "),
        DRIVER_UPDATE_QUEST_P1 ("이 운전자를 수정하시겠습니까?", "Do you want to modify this driver?"),
        DRIVER_UPDATE_QUEST_P2 (" - 운전자 이름 : ", " - Driver name: "),
        DRIVER_UPDATE_QUEST_P3 ("(소유 차량 대수 : ", "(owning car count : "),
        HOWTO_CLOSE_WINDOW_DIALOG ("(창단기: 엔터키)", "(To close window: Enter Key)."),
        ID_CHECK_DIALOG ("아이디 중복검사가 필요합니다.", "Need to check if 'id' is usable(unoccupied)."),
        EMAIL_CHECK_DIALOG("이메일 중복검사가 필요합니다.", "Need to check if 'E-Mail' is usable(unoccupied)."),
        ATT_NAME_CHECK_DIALOG("  - 관리원 이름 두 글자 이상", "  - Name should be longer than 1 character"),
        ATT_CELL_CHECK_DIALOG("  - 휴대폰 번호 입력 오류", "  - Wrong cell phone number"),
        PHONE_CHECK_DIALOG("  - 전화 번호 입력 오류", "  - Phone number error"),
        NO_USER_DIALOG("사용자 검색 건수: 0", "No user found"),
        CELL_PHONE_CHECK_DIALOG(
                "  - 전화번호 나 휴대폰 중 하나 입력", "  - Either phone, cell-phone or both is needed"),
        PASSWORD_CHECK_DIALOG(
                "  - 비밀번호 형식이 요구조건에 부합하지 않음.", "  - Password doesn't meet syntax requirements"),
        REPEAT_PW_CHECK_ERROR("  - 새 비밀번호 반복입력 불일치", "  - New passwords don't match"),
        ADMIN_PW_CHECK_DIALOG(" 비밀번호 틀림!", "'s Password is Wrong!"),
        DELETE_FAIL_DAILOG("비밀번호가 올바르지 않습니다.", "Password is wrong!"),
        ID_LENGTH_CHECK_DIALOG("아이디는 두 자 이상이어야 합니다.", "ID should consists of at least 2 characters."),
        ID_FIRST_CHAR_CHECK_DIALOG(
                "- 첫 글자가 영문자가 아닙니다!", "- First character isn't an English alphabet!" ), 
        ID_CHAR_CHECK_DIALOG(
                "- 아이디에 내포 가능한 문자: " + System.lineSeparator() + "   영숫자, 공백 혹은 점(.)",
                "- Characters allowed inside ID string: " + System.lineSeparator() + "   Alphanumeric, blank, period(.)" ),
        ID_END_CHAR_CHECK_DIALOG("- 끝 글자가 영숫자가 아닙니다!", "Last character isn't an alpha-numeric!" ),
        ATT_DELETE_DIALOG("아래 계정을 삭제합니까?", "Want to delete below user?" ),
        AFFILIATION_DELETE_ALL_DAILOG(
                "모든 상위 및 하위 소속들을 삭제합니까?", "Want to delete all of higher and lower affiliations?"),
        AFFILIATION_DELETE_ALL_RESULT_DAILOG(
                "모든 소속들이 성공적으로 삭제되었습니다", "Every affiliations are deleted successfully." ),
        BUILDING_DELETE_ALL_DAILOG(
                "모든 건물 및 소속 호실들을 삭제합니까?", "Want to delete all buildings and rooms?" ),
        BUILDING_DELETE_ALL_RESULT_DAILOG(
                "모든 건물 및 소속 호실들이 성공적으로 삭제되었습니다", "Every Buildings and Rooms are deleted."),
        CAR_TAG_DIALOG("차량번호가 누락되었습니다.", "Car Tag Number is missing."),
        DRIVER_DIALOG("운전자 정보가 누락되었습니다.", "Car Owner/Driver is missing."),
        VEHICLE_CREATION_FAIL_DIALOG("차량 등록을 실패하였습니다.", "failed vehicle creation "),
        VEHICLE_CREATE_CANCEL_DIALOG(
                "작성 중인 차량정보를 포기하겠습니까?", "Want to desert car information created so far?"),
        VEHICLE_MODIFY_CANCEL_DAILOG(
                "변경 중인 차량정보를 포기하겠습니까?", "Want to desert car information updated so far?"),
        VEHICLE_DELETE_ALL_DAILOG(
                "모든 등록 차량을 삭제합니까?", "Want do delete all vehicle information?"),
        VEHICLE_DELETE_ALL_RESULT_DAILOG(
                "모든 차량 정보가 삭제되었습니다.", "All Vehicles are Deleted"),
        VEHICLE_SAVE_ODS_FAIL_DIALOG(
                "저장할 차량 목록이 없습니다.", "No vehicle list to save!"),
        DRIVER_SAVE_ODS_FAIL_DIALOG(
                "저장할 운전자 목록이 없습니다.", "Empty driver list can't be saved!"),
        ARRIVAL_SAVE_ODS_FAIL_DIALOG(
                "저장할 도착 목록이 없습니다.", "Empty arrival list can't be saved!"),
        USER_SAVE_ODS_FAIL_DIALOG(
                "저장할 사용자 목록이 없습니다!", "No user list to save!"),
        DRIVER_CREATRION_FAIL_DIALOG("운전자 정보 생성 실패.", "Failed to create a owner/driver record."), 
        DRIVER_DELETE_ALL_RESULT_DAILOG("모든 운전자가 삭제되었고" + System.getProperty("line.separator")
                + "그들에게 등록된 모든 차량도 삭제되었습니다.", 
                "All drivers are deleted" + System.getProperty("line.separator")
                + "and all vehicles registered are deleted, too."),
        DRIVER_MODIFY_FAIL_DAILOG(
                "하나의 운전자만 선택하여 변경하십시오.", "Please narrow down to a single driver to update!"),
        DRIVER_CREATE_CANCEL_DIALOG("정말 운전자 생성을 취소합니까?", "Really cancel driver creation?"),
        DRIVER_MODIFY_CANCEL_DAILOG("정말 운전자 변경을 취소합니까?", "Really cancel driver update?"),
        
        MISSING_CELL_HANDLING("\"휴대전화\" 번호는 최소 10 자리입니다." 
                            + System.getProperty("line.separator") 
                            + "[예] => 바른 휴대전화 번호 입력" 
                            + System.getProperty("line.separator") 
                            + "[아니오] => 운전자 ", 
                "\"Cell phone number\" is at least 10 digits." 
                            + System.getProperty("line.separator") 
                            + "[Yes] => to edit cell phone number" 
                            + System.getProperty("line.separator") 
                            + "[No] => to give up driver "),
        
        MISSING_PHONE_HANDLING("\"전화\" 번호는 최소 4 자리입니다." 
                            + System.getProperty("line.separator") 
                            + "[예] => 바른 전화번호 입력" 
                            + System.getProperty("line.separator") 
                            + "[아니오] => 운전자 ", 
                "\"Phone number\" is at least 4 digits." 
                            + System.getProperty("line.separator") 
                            + "[Yes] => to edit phone number" 
                            + System.getProperty("line.separator") 
                            + "[No] => to give up driver "),

        MISSING_NAME_HANDLING("\"운전자 이름\"은 두 자를 넘어야 합니다." + System.getProperty("line.separator") 
                            + "[예] => 바른 이름 입력" + System.getProperty("line.separator") 
                            + "[아니오] => 운전자 ", 
                "\"Name\" should be longer than 1 character." + System.getProperty("line.separator") 
                            + "[Yes] => to enter right name" + System.getProperty("line.separator") 
                            + "[No] => to give up driver "),
        MISSING_NAME_2(" 포기!", ""),
        USER_RIGHTS_DESCRIPTION("1. 관리자(ID: admin) : 상위" + System.getProperty("line.separator") +
                "    - 매니저 및 일반에 대한 모든 권한" + System.getProperty("line.separator") +
                "      (모든 권한: 삽입, 갱신, 삭제)" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "2. 매니저(첵크된 계정) : 중위" + System.getProperty("line.separator") +
                "    - 일반에 대한 모든 권한" + System.getProperty("line.separator") +
                "      (일반에 매니저 권한 부여 불가)" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "3. 일반 : 하위" +System.getProperty("line.separator") +
                "    - 자기 계정에 대한 갱신 권한" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "4. 사용자 전체" +System.getProperty("line.separator") +
                "    - 모두 자기 계정 속성 변경 가능" + System.getProperty("line.separator") +
                "      (단, 매니저 속성은 변경 불가)" + System.getProperty("line.separator") +
                "    - 누구도 자기 계정 삭제 불가"
                ,
                "1. admin(: user ID) : high level" + System.getProperty("line.separator") +
                "    - All rights for the managers and the ordinaries" + System.getProperty("line.separator") +
                "      (rights: insert, update, delete)" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "2. Managers : middle level" + System.getProperty("line.separator") +
                "    - All the rights for the ordinaries " + System.getProperty("line.separator") +
                "      (manager check box disabled)" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "3. Ordinaries : low level" + System.getProperty("line.separator") +
                "    - Update own account information" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "4. Every user" +System.getProperty("line.separator") +                        
                "    -  Can update own information" + System.getProperty("line.separator") +
                "       (but manager property is an exception)" + System.getProperty("line.separator") +
                "    -  Can not delete own account" + System.getProperty("line.separator")
        ),
        DRIVER_CLOSE_FORM_DIALOG(
                " 중인 운전자 정보를 버리겠습니까?", " will be discarded, is it OK?"),
        
        DATE_INPUT_CHECK_DIALOG("시작일과 종료일을 모두 입력하십시오", "Enter starting and ending date both"),
        
        DATE_INPUT_ERROR_DIALOG( "종료일이 시작일보다 앞설 수 없습니다\n" 
                + "날짜를 변경하여 입력하십시오.", 
                "Ending date can't precede starting date\n" 
                        + "Please, correct search range."),
         
        STOP_RUNNING_DIALOG("실행중인 프로그램을 종료합니다.", "Program stops running."),
        SHUT_DOWN_CONFIRM_DIALOG("프로그램을 종료하시겠습니까?", "Do you want to stop the system?"),
        
        LOGALWAYS_DAILOG("현제 단계 (로깅하지 않음.)," + System.lineSeparator() 
                + " 다음과 같은 '필수적인' 항목들만 로깅 :" + System.lineSeparator() 
                + System.lineSeparator() 
                + " - 시스템 시작시간과 종료시간" + System.lineSeparator() 
                + " - 삭제 된 레코드 수"+ System.lineSeparator() 
                + " - 삭제 된 이미지의 수"+ System.lineSeparator() 
                + " - 삭제 된 로그 폴더 및 텍스트 파일의 개수"+ System.lineSeparator() 
                + " - 삭제 된 로그 폴더 및 텍스트 파일의 경로", 
                "At this level (No Logging)," + System.lineSeparator() 
                + " Following 'Mandatory' items are logged :" + System.lineSeparator() 
                + System.lineSeparator() 
                + " - System start and stop time" + System.lineSeparator() 
                + " - Number of deleted old records"+ System.lineSeparator() 
                + " - Number of deleted old images"+ System.lineSeparator() 
                + " - Number of deleted log folders and text files"+ System.lineSeparator() 
                + " - File path of deleted log folders and text files"),
        
        SETTINGSCHANGE_DAILOG(" '시스템 설정 변경' 단계 로깅 항목 :" + System.lineSeparator() 
                + System.lineSeparator() 
                + " - 필수 항목" + System.lineSeparator() 
                + "   추가" + System.lineSeparator()
                + " - 시스템 설정 변경"+ System.lineSeparator() 
                + " - 관리원/사용자 정보 변경"+ System.lineSeparator() 
                + " - 운전자 정보 변경"+ System.lineSeparator() 
                + " - 차량정보 변경", 
                "'System Settings' Level Logged Items:" + System.lineSeparator() 
                + System.lineSeparator() 
                + " - Mandatory Items" + System.lineSeparator() 
                + "   plus" + System.lineSeparator()
                + " - System Settings Change" + System.lineSeparator() 
                + " - Attendant/User Info Change" + System.lineSeparator() 
                + " - Drivers Info Change" + System.lineSeparator() 
                + " - Vehicles Info Change" + System.lineSeparator()),
        
        EBDSETTINGSCHANGE_DIALOG(" '시스템, 사용자, 차량' 단계 로깅 항목 :" + System.lineSeparator() 
                + System.lineSeparator() 
                + " - 시스템 설정 변경 항목" + System.lineSeparator() 
                + "   추가"+ System.lineSeparator() 
                + " - 전광판 설정 변경"+ System.lineSeparator() ,
                "'E-Board Settings' Level Logged Items: " + System.lineSeparator() 
                + System.lineSeparator() 
                + " - System Settings Logged Items" + System.lineSeparator() 
                + "   plus" + System.lineSeparator()
                + " - E-Board Settings Change" + System.lineSeparator()),

        LANGUAGE_USE_DIALOG("GUI 언어 선택", "GUI language selection."),
        PHOTO_SIZE_INPUT_ERROR_DIALOG(
                "사진크기를 100이상으로 입력해주시기 바랍니다.", "Please enter a picture size value of 100 or more."),
        
        CHANGE_GATE_DAILOG("입구 갯수를 변경할 경우," + System.lineSeparator() 
                    + "'OsParking' 이 자동으로 종료됩니다." + System.lineSeparator() 
                    + "'OsParking' 을 다시 실행시켜주시기 바랍니다.",
            "After Gate count change," + System.lineSeparator() 
                    + "'OSParking' shuts down by itself." + System.lineSeparator() 
                    + "So, you need to start OSParking again."),
        
        FAIL_SAVE_SETTINGS_DIALOG("시스템 설정 저장에 실패하였습니다.", "The system settings saving failed."),
        SAVE_SETTINGS_DIALOG("설정 값이 저장 되었습니다.", "Settings saved successfully."),
        INPUT_ERROR_DAILOG("1 이상의 값을 입력하세요.", "Enter a value of 1 or more .."),
        
        SAVE_OR_CANEL_DIALOG("설정이 변경되었습니다..\n \n"
                    + "[저장] 또는 [취소]버튼을 선택해 주세요.", 
            "Settings Changed.\n \n"
                    + "Either [Save] or [Cancel], please."),
        
        E_BOARD_SAVE_FAIL_DIALOG(
                "전광판 설정을 실패하였습니다.", "This e-board settings update saving DB operation failed."),
        E_BOARD_NO_INPUT_MSG_DIALOG("  - 문구를 입력해주십시오.", "  - Please enter a message"),
        CHANGE_DIALOG("변경사항", "Changes"),
        CURRENT_DIALOG("변경 전", "Current"),
        MODIFY_DIALOG("변경 후", "Modified"),
        SAVE_DIALOG("저장되었습니다.", "Saved"),
        LOGIN_WRONG_DIALOG("아이디 혹은 비밀번호가 틀립니다!", "ID or Password is wrong!"),
        INPUT_ID_DIALOG("\"아이디\"를 입력하세요.", "Enter \'Login ID\'!"),
        INPUT_PW_DIALOG("\"비밀번호\"를 입력하세요.", "Enter \'Password\'"),
        
        SUGGEST_SUPPLY_L2("\"하위소속\"을 선택 하시겠습니까?.\n"
                + "[예] => 하위소속 선택\n"
                + "[아니오] => 전체 작업 취소\n", 
                " Want to select \'Lower Affiliation\'?\n"
                + "[Yes] => to select Lower Affiliation\n"
                + "[No] => to cancel whole task"),
        
        SUGGEST_SUPPLY_UNIT("\"호실번호\"를 선택 하시겠습니까?\n"
                + "[예] => 호실번호 선택\n"
                + "[아니오] => 전체 작업 취소\n",
                "Want to select \'Room\'?\n"
                + "[Yes] => to select Room(number)\n"
                + "[No] => to cancel whole task"),
        
        PW_FOURDIGIT_DIALOG("  - 정확히 네 자리 숫자", "  - Exact four digit number"),
        
        PW_SIXDIGIT_DIALOG("  - 6 ~ 40자로 구성\n"
                + "  - 영문 문자(a-z,A~Z)를 한 글자 이상 포함\n"
                + "  - 숫자(0-9)를 한 글자 이상 포함",
                "  - consists of 6 to 40 characters\n"
                + "  - contains at least one English alphabet (a-z,A~Z)\n"
                + "  - includes more than one number key(0-9)"),
        
        PW_COMPLEX_DIALOG(
                "  - 8 ~ 40자로 구성\n"
                + "  - 영문 소문자(a~z) 한 글자 이상 포함\n"
                + "  - 영문 대문자(A~Z) 한 글자 이상 포함\n"
                + "  - 숫자(0-9) 한 자 이상 포함\n"
                + "  - 다음 특수 문자 중 한 글자 이상 포함",
                "  - consists of 8 to 40 characters\n"
                + "  - contains at least one lower case alphabet (a-z)\n"
                + "  - contains at least one upper case alphabet (A-Z)\n"
                + "  - includes more than one number key(0-9)\n"
                + "  - includes at least one special character shown below"
                ),
        
        BUILDING_IN_DIALOG("건물 번호", "Building number"),
        ROOM_IN_DIALOG("호실 번호", "Room number"),
        LEVEL1_NAME_DIALOG("상위 소속", "Higher Affiliation"),
        LEVEL2_NAME_DIALOG("하위 소속", "Lower Affiliation"),
        VEHICLE_MODIFY_FAIL_DAILOG("정보 갱신 실패 차량: ", "modification failed vehicle: "),
        UPDATE_E_BOARD_DIALOG("전광판 변경 설정을 저장합니까?", "Want to save E-board update?"),
        READ_ODS_FAIL_DIALOG("잘못된 형식의 자료가 포함되어 있습니다.", "Cells that include wrong formatted data"),
        
        CREATION_SUCCESS_DIALOG(
                "운전자 생성 성공" + System.getProperty("line.separator") + "운전자 이름: ", 
                "Driver Creation Succeeded" + System.getProperty("line.separator") + "Driver Name: "),
        
        USER_UPDATE_SUCCESS_DIALOG(
                "수정 중인 운전자 정보를 저장하겠습니까?" + System.getProperty("line.separator") + " - 운전자 이름: ",
                "Do you want to save the modified driver?" +  System.getProperty("line.separator") + " - Driver Name: "
        ),
        
        USER_CREATE_SUCCESS_DIALOG(
                "생성하신 운전자 정보를 저장하시겠습니까?" 
                        + System.getProperty("line.separator") + " - 운전자 이름: ",
                "Do you want to save created driver?" 
                        +  System.getProperty("line.separator") + " - Driver Name: "
        ),
        CHECK_BUILDING_ODS_DIALOG(
                "건물 및 호실 번호 종료마크(아래)가 누락됨" 
                        + System.getProperty("line.separator") 
                        +  "종료마크: 첫 열이 -1인 행",
                "List of Building and Room lacks End Mark" 
                        + System.getProperty("line.separator") 
                        + "End Mark: a row having -1 at its first cell/column"),
        
        READ_FAIL_AFFILIATION_ODS_DIALOG(
                "아래 하위소속이 속한 상위소속을 알 수 없습니다." 
                        + System.getProperty("line.separator") 
                        + " - 하위 소속: ", 
                "Can't tell to what higher affiliation do following lower affiliation belong" 
                        + System.getProperty("line.separator") 
                        + " - Lower Affiliation: "),
        SAVE_AS_EXIST_DIALOG(
                "아래 파일이 이미 존재합니다.", "A folder(=directory) of same name exists"),
        
        SAVE_OVERWRITE_DIALOG(
                "이 파일에 덮어 쓰겠습니까?", "Do you want to overwrite it?"),
        AUTO_LOGOUT("이 자동 로그아웃 됨!", " is forced to log out!"),
        EMPTY_HIGH_AFFILI("상위 소속 명칭이 빈 문자열입니다.", "Empty high level affiliation error."),
        EMPTY_LOW_AFFILI("하위 부서 명칭이 빈 문자열입니다.", "Empty low level affiliation error."),
        DUPLICATE_HIGH_AFFILI("중복되는 상위 소속 : ", "Duplicate high level affiliation : "),
        DUPLICATE_LOW_AFFILI("중복되는 하위 소속 : ", "Duplicate low level affiliation : "),
        DUPLICATE_BUILDING("중복되는 건물 번호 : ", "Duplicate building number : "),
        DUPLICATE_UNIT("중복되는 호실 번호 : ", "Duplicate unit number : "),
         
        OSPARKING_STOPS("오즈파킹 가동이 종료됨.", "OsParking operation finishes."),
        REBOOT_MESSAGE("중요 설정이 변경되어," + System.getProperty("line.separator") +
                "오즈파킹이 자동 종료됨."  + System.getProperty("line.separator") +
                "따라서, (필요하면)"  + System.getProperty("line.separator") +
                "오즈파킹을 재가동 할 것!", 
                "As Gate count changed," + System.getProperty("line.separator") +
                "OsParking shuts down by itself." + System.getProperty("line.separator") +
                "If you need, restart OsParking!"
        ),
        RECORD_DELAY_DEBUG("디버깅 모드에서는 통과 지연 시간이 항상 기록됩니다.", 
                "In debug mode, Passing Delay is alway recorded");
        
        DialogMessages(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }        
    }
    
    public static enum DialogTitleTypes {
        LANGUAGE_ERROR_TITLE("언어 선택 오류", "Language Selection Error"),
        EFFECT_TITLE("효과" + " 변경 확인", "Effect" + " Change Confirm"),
        ODS_HELP_TITLE("ods 파일 도움말", "ods File Help"),
        ODS_SAVE_TITLE("ods 파일 생성 위치", "ods File Location"),
        DEFAULT_USER_TITLE("사용자 생성", "User Creation"),
        E_BOARD_SIM_TITLE("모의 전광판 설정", "E-Board Simulator"),
        OVERLAPPED_PORT_TITLE("COM 포트 중복 사용", "PORT USAGE ERROR"),
        IP_ERROR_TITLE("IP 주소 오류", "IP Address Error"),
        DEFAULT_TAB_TITLE("평소", "Default"),
        VEHICLE_TAB_TITLE("입차", "Vehicle"),
        TOP_TAB_TITLE("제 1행", "1st Row"),
        BOTTOM_TAB_TITLE("제 2행", "2nd Row"),
        SETTINGS_SAVE_RESULT("설정 저장 결과", "Settings Save Result"),
        UserLevelSelectionGUItitle("로그인 사용자 수준 결정 폼", "Login User Level Selection GUI"),
        ATT_SAVE_AS_FAIL_DIALOGTITLE("파일명 변경 필요성 알림", "Choose Different File Name"), 
        ATT_EMAIL_DUP_DIALOGTITLE("이메일 중복 검사 결과", "Duplicate Check Result"), 
        ATT_EMAIL_SYNTAX_CHECK_DIALOG("이메일 주소 검사 결과", "Syntax Check Result"),            
        ATT_ID_DUP_CHCEK_DIALOGTITLE("아이디 검사 결과", "ID Check Result"),
        ATT_USER_UPDATE_DIALOGTITLE("관리원 정보 수정 결과", "User Info Change Result"),       
        ATT_SFAVE_AS_SUCCESS_DIALOGTITLE("텍스트(.txt) 파일 생성", "Text File(*.txt) Creation"), 
        ATT_HELP_DIALOGTITLE("비밀번호 요구조건", "Password Requirements"),
        RIGHTS_DIALOGTITLE("사용자 권한 3 단계", "User 3 level rights"),
        CONFIRM_LOGOUT("로그아웃 확인", "Confirm Logout"),                
        ODS_CHECK_RESULT_TITLE("ods 분석 결과", "ods check result"),                
        ODS_READ_RESULT_TITLE("ods 읽은 결과", "ods read result"),                
        READ_ODS_FAIL_DIALOGTITLE("차트 형식 오류", "Sheet Cell Data Format Error"),       
        PERIOD_ERROR_TITLE("기간 지정 오류", "PERIOD ERROR"), 
        AFFILIATION_MODIFY_DIALOGTITLE("상위 소속 변경", "Higher Affiliation Change"),  
        BUILDING_MODIFY_DIALOGTITLE("건물 변경 확인", "Building Change Confirm'"),
        LOWER_MODIFY_DIALOGTITLE("하위 변경 확인", "Low Affili' Change"),
        UNIT_MODIFY_DIALOGTITLE("호실 변경 확인", "Confirm Room Change"),
        REJECT_USER_DIALOGTITLE("중복 값 입력 오류", "Duplicate Data Error'"),
        VEHICLE_CHECK_DIALOGTITLE("차량 필수 자료 오류", "Required Field Missing"),
        VEHICLE_MODIFY_FAIL_DIALOGTITLE("차량 수정 실패", "Vehicle Modification Failure"),
        WARING_DIALOGTITLE("경고", "WARNING"),
        NOTICE_DIALOGTITLE("알림", "Notice"),
        LOW_AFFILI_MISSING("하위 소속 부재 ", "Lower Affiliation "),
        LOW_UNIT_MISSING("호실 비 선택 ", "Missing Unit "),
        ERROR_DIALOGTITLE("오류", "Error"),
        CONN_ERROR_TITLE("연결 오류", "Connection Error"),
        WORK_MODE_DIALOGTITLE("작업 모드 환기", "Current Work Mode"),
        CREATION_RESULT_DIALOGTITLE("생성 결과", "Creation Result"),
        USER_FIELD_CHECK_RESULT("항목 검사 결과", "Field Check Result"),
        CREATTION_FAIL_DIALOGTITLE("생성 실패", "Creation Error" ),               
        DELETE_ALL_DAILOGTITLE("전체 삭제 확인", "All Record Deletion Confirmation"),
        DELETE_ALL_RESULT_DIALOGTITLE("전체 삭제 결과", "All Record Deletion Result"),
        DELETE_DIALOGTITLE("삭제 확인", "Deletion Confirmation"),
        DELETE_RESULT_DIALOGTITLE("삭제 결과", "Deletion Result"),
        DELETE_FAIL_DAILOGTITLE("삭제 실패", "Deletion Failure"),
        MODIFY_DAILOGTITLE("수정 확인", "Modification Confirm"),
        MODIFY_FAIL_DIALOGTITLE("수정 실패", "Modification Failure"),
        MISSING_FILE_DIALOGTITLE("파일 부재 오류", "File Not Found"),
        CANCEL_DIALOGTITLE("취소 확인", "Cancel Confirmation"),
        SAVE_DIALOGTITLE("저장 확인", "Save Confirmation"),
        CONFIRM_DIALOGTITLE("확인", "Confirm"),
        LOGGING_DIALOGTITLE("어떤 것들이 로깅되는가?", "What is being LOGGED?"),
        PASSWORD_REQUIR_DIALOGTITLE("비밀번호 요구사항", "Password Requirements"),
        LANGUAGE_SELECT_DIALOGTITLE(LANGUAGE_LABEL.getContent() + " 용도", 
                LANGUAGE_LABEL.getContent() + " Usage"),
        STATISTICS_INPUT_ERROR_DIALOGTITLE("통계 주기 입력 오류", "Statistics Cycle Input Error"),
        ATTENDANT_ODS_TITLE("사용자 목록 ods 파일로 저장하기", "Saving User List into an ods File"),
        PHOTO_SIZE_INPUT_ERROR_DIALOGTITLE("사진 크기 입력 오류", "Picture Size Input Error"),
        SYSTEM_SHUTDOWN_CONFIRM("종료 의사 확인", "Shutdown Confirmation"),
        MAIN_GUI_TITLE("㈜오픈소스파킹", "Open Source Parking Inc.");

        DialogTitleTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }          
        
    }
    
    public enum TextType{
        CAR_TAG_TF("(차량번호)", "(Tag No.)"),
        DRIVER_TF("(운전자)", "(Driver)"),
        OTHER_INFO_TF("(기타정보)", "(Other Info)"),
        DIS_REASON_TF("(불허 사유)", "(Why disallowed)"),
        CELL_PHONE_TF("(휴대전화)", "(Cell Phone)"),
        LANDLINE_TF("(유선전화)", "(LandLine)"),
        LOG_OUT_TF("(로그아웃)", "(Log Out)"),
        UNKNOWN_TF("(자료 없음)", "(unknown)"),
        UNKNOWN_TF_SHORT("(없음)", "(NA)"),
        UNREGISTERED_TF("(미등록)", "(Non-Registered)"),
        NOT_APPLICABLE_TF("(해당 없음)", "(Not Applicable)"),
        STATUS_TF("<중요 상태 정보>", "<Critical Status Information>"),
        START_MSG("시스템 시작", "System started"),
        STOP_MSG("시스템 종료", "System stopped"),
        FIRST_RUN_MSG("OsParking 의 첫 번째 실행",
                "Very First Run of OsParking!"),
        NO_MSG("없음", "N/A"),
        ERROR_RATE_MSG ("인공 에러율: ", "Artificial error rate: "),
        NO_APP_MSG("인공 오류 없음", "No artificial error"),
        NO_SOCKET_DISCON_MSG(": 소켓 단절 없음.", ": no socket disconn'"),
        NO_COMMAND_MSG("개방 명령 없음.", "no Open command statistics"),
        OPEN_MSG("개방", "Open"),
        INTERRUPT_MSG("입차", "Interrupt"),
        DISCONN_MSG("단절됨", "disconnected"),
        CONN_MSG("연결됨", "connected"),
        ON_ARTIFI_ERROR_MSG("인공 오류 삽입", "Artificial error is on"),
        ERROR_RATE_MSG2("에러율 : ", "prob of error: "),
        ERROR_MSG("에러 ", "error"),
        ERROR_CHECK_BOX_MSG("에러 체크 박스를 먼저 선택해주세요. ",
                "First, select error check box, OK?"),
        LETEST_MSG("최근 ", "latest"),
        PASSING_MSG(" 대 평균 통과 시간 ", " car average passing delay is "),
        SECOND_MSG("초", "seconds"),
        DELETE_LOG_MSG("삭제된 기록--폴더: ", "Deleted logs--directory: "),
        DELETE_FILE_MSG(", 파일: ", "files: "),
        
        HELP_TA( 
                "\u203B 오픈오피스 스프레드시트(OpenOffice Calc) 를 사용하여 만들 수 있습니다"
                        + System.getProperty("line.separator")
                        + "\u203B MS엑셀에서 엑셀파일을 다음 절차로 'ods' 파일로 저장할 수 있습니다"
                        + System.getProperty("line.separator")
                        + "     [파일] \u21D2 [다른 이름으로 저장] \u21D2 파일 형식: 'OpenDocu...' 선택",
                "\u203B Default file type of a office SW 'OpenOffice Calc'"
                        + System.getProperty("line.separator")
                        + "\u203B Creatable using MS Excel, OpenOffice Calc, etc."
                        + System.getProperty("line.separator")
                        + "\u203B In MS Excel, 'ods' can be created by --"
                        + System.getProperty("line.separator")
                        + "     [File] \u21D2 [Save As...] \u21D2 [File Type: (choose) 'OpenDocu...'");
                
        TextType(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
            return contents[language.ordinal()];
        }
    }
    
    public enum ComboBoxItemTypes {
        SANS_SERIF("고딕체", "Sans Serif"),
        MS_NEO_GOTHIC("네오고딕", "Microsoft Neo Gothic"),
        DAY_SUFFIX("일", "days"),
        LOWER_HIGHER_CB_ITEM("(하위-상위)", "(Lower-Higher)"),
        HIGHER_LOWER_CB_ITEM("(상위-하위)", "(Higher-Lower)"),
        ROOM_BUILDING_CB_ITEM("(호실-건물)", "(Room-Bldg)"),
        BUILDING_ROOM_CB_ITEM("(건물-호실)", "(Bldg-Room)"),
        HIGHER_CB_ITEM("(상위 소속)", "(Higher Group)"),
        LOWER_CB_ITEM("(하위 소속)", "(Lower Group)"),
        BUILDING_CB_ITEM("(건물 번호)", "(Building No,)"),
        ROOM_CB_ITEM("(호실 번호)", "(Unit No.)"),
        GATE_CB_ITEM("(입구)", "(Gate)"),
        ATTENDANT_CB_ITEM("(관리원)", "(Attendant)"),
        BAR_CB_ITEM("(차단기)", "(Unselected)"),
        ATTENDANT_LOGOUT_ITEM("(로그아웃)", "(logged out)"),
        USER_CB_ITEM("(모두)", "(ALL)"),
        FOUR_DIGIT_CB_ITEM("4 자리 수", "Four digits"),
        SIX_DIGIT_CB_ITEM("6+ 자리 영숫자", "Six+ alpha-num"),
        COMPLEX_CB_ITEM("8+ 자리 복합구성", "8+ complex config'"),
        NO_LOGGING_CB_ITEM("(로깅 안함)", "(No Logging)"),
        SETTINGS_LOGGING_CB_ITEM("시스템 설정 변경", "Settings Change"),
        E_BOARD_LOGGING_CB_ITEM("사용자 및 차량 변경 추가", "E-board Change Added"),
        DAY_CB_ITEM("일", "day"),
        DAYS_CB_ITEM("일", "days"),
        VERBATIM_CB_ITEM("문구 자체", "Exact Words"),
        VEHICLE_TAG_CB_ITEM("차량번호", "Plate Number"),
        REGISTRATION_STAT_CB_ITEM("등록 상태", "Registration Status"),
        GATE_NAME_CB_ITEM("입구 명칭", "Gate Name"),
        CURRENT_DATE_CB_ITEM("현재 날짜", "Current Date"),
        CURRENT_TIME_CB_ITEM("현재 시간", "Current Time"),
        CURRENT_DATE_TIME_CB_ITEM("현재 날짜-시간", "Current Date and Time"),
        RED_COLOR_CB_ITEM("빨강", "RED"),
        ORANGE_COLOR_CB_ITEM("주황", "ORANGE"),
        GREEN_COLOR_CB_ITEM("초록", "GREEN"),
        BLACK_COLOR_CB_ITEM("검정", "BLACK"),
        BLUE_COLOR_CB_ITEM("파랑", "BLUE"),
        BLINKING_CB_ITEM("깜빡임", "Blinking"),
        LTOR_CB_ITEM("우로 흐름", "L to R Flow"),
        RTOL_CB_ITEM("좌로 흐름", "R to L Flow"),
        STILL_FRAME_CB_ITEM("정지", "Still Frame");
        
        ComboBoxItemTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }          
    }
    
    
    public enum OPTN_LOGGING {
        NO_LOGGING_CB_ITEM("(로깅 안함)", "(No Logging)"),
        SETTINGS_LOGGING_CB_ITEM("시스템 설정 변경", "Settings Change"),
        E_BOARD_LOGGING_CB_ITEM("사용자 및 차량 변경 추가", "E-board Change Added");
        
        OPTN_LOGGING(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }          
    }
    
    
    public enum BarOperation {
        /**
         * Gate bar opened as a result of an arrival of a vehicle allowed to 
         * park legally.
         *//**
         * Gate bar opened as a result of an arrival of a vehicle allowed to 
         * park legally.
         *//**
         * Gate bar opened as a result of an arrival of a vehicle allowed to 
         * park legally.
         *//**
         * Gate bar opened as a result of an arrival of a vehicle allowed to 
         * park legally.
         */
        REGISTERED_CAR_OPENED("자격개방", "Legal Open"), 
        /**
         * Attendant in charge isn't able to take care the gate at the moment. 
         * He/She set the gate on automatic open mode as soon as the tag 
         * is recognized.
         */
        AUTO_OPENED("불문개방", "Auto' Open"),
        /**
         * Attendant opened the gate manually. It could be done for a visitor,
         * in case of LPR malfunction, or for a disallowed car, etc.
         */
        MANUAL("수동개방", "Manual Open"), 
        /**
         * Gate remained closed since the attendant disallowed a car to enter.
         */
        REMAIN_CLOSED("회차조치", "Not allowed"),
        /**
         * Temporarily stopped for a checkup
         */
        OPENED_UP("일단정지", "Stopped");
        
        BarOperation(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
                return contents[language.ordinal()];
        }
    }
    
    public enum MenuITemTypes{
        RECORD_MENU("각종기록", "Records"),
        ARRIVAL_MENU_ITEM("도착기록", "Arrival"),
        BOOTING_MENU_ITEM("실행기록", "Booting"),
        LOGIN_RECORD_MENU_ITEM("로그인기록", "Login"),
        VEHICLE_MENU("<HTML>차량\u22C5차주</HTML>", "<HTML>Car\u22C5Owner</HTML>"),
        VEHICLE_MANAGE_MENU_ITEM("차량관리", "Vehicle"),
        DRIVERS_MENU_ITEM("차주관리", "Driver"),
        AFFILIATION_MENU("건물소속", "Affiliation"),
        AFFILIATION_ITEM("목록관리", "Manage List"),
        USERS_ITEM("사용자", "Users"),
        SYSTEM_MENU("시스템", "System"),
        SETTING_MENU_ITEM("설정", "Settings"),
        SETTING_MENU_ITEM_SC("설정(S)", "Settings"),
        QUIT_MENU_ITEM("종료", "Quit"),
        QUIT_MENU_ITEM_SC("종료(Q)", "Quit"),
        LOGIN_MENU("<HTML>로그인(<U>I</U>)</HTML>", "<HTML>Log <U>I</U>n</HTML>"),
        LOGOUT_MENU("<HTML>로그아웃(<U>O</U>)</HTML>", "<HTML>Log <U>O</U>ut</HTML>"),
        LOGIN_MENU_ITEM("로그인", "Log In"),
        LOGOUT_MENU_ITEM("로그아웃", "Log Out"),
        MANAGER_MANU("매니저 :", "Manager :"),
        META_KEY_LABEL("메타 키 : Alt", "Meta Key : Alt"),
        ID_LABEL_STR("아이디 :", "ID :"),
        ID_DEFAULT("(없음)", "(N/A)");
        
        MenuITemTypes(String korean, String english) {
            contents[KOREAN.ordinal()] = korean;
            contents[ENGLISH.ordinal()] = english;
        }
        
        private String[] contents = new String[Languages.values().length];
        
        public String getContent() {
            return contents[language.ordinal()];
        }            
    }
    
    public enum MessageTypes{
        LOGIN_MSG, LOGOUT_MSG, PASSING_DELAY_MSG, 
    }
    
    public enum FormMode {
        NormalMode,
        CreateMode,
        UpdateMode
    }    
}
