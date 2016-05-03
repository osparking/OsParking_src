/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osparking.osparking.device.NaraBar;

/**
 *
 * @author Open Source Parking Inc.
 */
public class NaraEnums {
    
    public enum BarStatus {
        UNKNOWN, 
        OPENED, CLOSED, OPENING, CLOSING
    }
    
    final static String status = "STATUS";
    
    final static String gate_up = "GATE UP";
    final static String gate_up_action = "GATE UP ACTION";
    final static String gate_up_ok = "GATE UP OK";
    
    final static String gate_down = "GATE DOWN";
    final static String gate_down_action = "GATE DOWN ACTION";
    final static String gate_down_ok = "GATE DOWN OK";
    
    final static String unlock = "GATE UNLOCK";
    final static String up_lock = "GATE UPLOCK";
    final static String system_reset = "SYSTEM RESET";
    
    final static String system_init = "SYSTEM INIT";
    final static String up_ok = "UP OK";
    final static String up_action = "UP ACTION";
    final static String down_ok = "DOWN OK";
    final static String down_action = "DOWN ACTION";
    
    public enum Nara_MsgType {
        Broken("", ""),
        Status(status, "상태검사"), 
        GateUp(gate_up, "개방지시"), 
        GateUpAction(gate_up_action, "개방지시 접수"), 
        GateUpOK(gate_up_ok, "개방 완료"),
        GateDown(gate_down, "폐쇄지시"), 
        GateDownAction(gate_down_action, "폐쇄지시 접수"), 
        GateDownOK(gate_down_ok, "폐쇄 완료"),
        GateUnLOCK(unlock, "잠금해제"),
        GateUpLOCK(up_lock, "개방&잠금"),
        SystemReset(system_reset, "시스템 재설정"),
        
        GateState_UpLOCK(up_lock, "개방&잠금"),
        GateState_SYSTEM_INIT(system_init, "시스템 초기화"),
        GateState_UP_OK(up_ok, "개방 상태"),
        GateState_UP_ACTION(up_action, "바 개방 중"),
        GateState_DOWN_OK(down_ok, "폐쇄 상태"),
        GateState_DOWN_ACTION(down_action, "바 폐쇄 중");
        
        private String message;
        private String messageUF;

        Nara_MsgType(String message, String messageUF) {
            this.message = message;
            this.messageUF = messageUF;
        } 

        public String getMessage() {
            return message;
        }
        
        /**
         * 사용자 친화적인(User Friendly) 메시지를 반환한다
         * @return 사용자 친숙한(User Friendly) 메시지
         */
        public String getMessageUF() {
            return messageUF;
        }         
    }    
}
