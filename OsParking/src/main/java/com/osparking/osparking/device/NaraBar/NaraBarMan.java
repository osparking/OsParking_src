/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.osparking.osparking.device.NaraBar;

import com.osparking.global.Globals;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.addMessageLine;
import static com.osparking.global.Globals.closeSocket;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.names.ControlEnums.DialogMessages.MISSING_PORT_DIALOG_1;
import static com.osparking.global.names.ControlEnums.DialogMessages.MISSING_PORT_DIALOG_2;
import static com.osparking.global.names.ControlEnums.DialogMessages.MISSING_PORT_DIALOG_3;
import static com.osparking.global.names.ControlEnums.DialogMessages.OsParking_FUNC_WARNING;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import com.osparking.global.names.DB_Access;
import static com.osparking.global.names.DB_Access.connectionType;
import static com.osparking.global.names.DB_Access.deviceComID;
import com.osparking.global.names.IDevice;
import com.osparking.global.names.OSP_enums;
import static com.osparking.global.names.OSP_enums.ConnectionType.RS_232;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.ParkingTimer;
import com.osparking.osparking.ControlGUI;
import com.osparking.osparking.device.NaraBar.NaraMessageQueue.NaraMsgItem;
import com.osparking.osparking.device.NaraBar.NaraEnums.Nara_MsgType;
import static com.osparking.osparking.device.NaraBar.NaraEnums.Nara_MsgType.*;
import com.osparking.osparking.device.NaraBar.NaraEnums.BarStatus;
import static com.osparking.osparking.device.NaraBar.NaraEnums.BarStatus.UNKNOWN;
import static com.osparking.osparking.device.NaraBar.NaraEnums.*;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * 나라 차단기와 연결된 통신 선로를 통하여 다음 두 가지 기능을 하는 쓰레드를 포함
 * 쓰레드 1(NaraBarMan): 차단기가 메시지를 전송할 때까지 기다리다가 메시지 별로 필요한 일처리 수행
 * 쓰레드 2(NaraBarWriter): 차단기로 보내는 메시지 큐에 항목이 입력되면 하나씩 차단기로 ACK 가 올 때까지 
 *                                 반복적으로 전송함.
 * @author Open Source Parking Inc.
 */
public class NaraBarMan extends Thread implements IDevice.IManager, IDevice.ISerial, IDevice.ISocket {
    private CommPortIdentifier portIdentifier;
    private CommPort commPort;
    
    ControlGUI mainGUI;
    byte deviceNo;
    private SerialPort serialPort = null;
    private Socket socket = null; // socket that connects with a e-board
    OutputStream oStrm;
    Thread msgSender = null;
    
    private Object msgArrived = new Object();
    private NaraEnums.Nara_MsgType msg;
    public long openOrderTmMs = 0;
    private BarStatus barState = UNKNOWN;    
    
    public NaraBarMan(final ControlGUI mainGUI, final byte deviceNo) {
        this.mainGUI = mainGUI;
        this.deviceNo = deviceNo;
        naraBarMessages = new NaraMessageQueue(getMsgQdoor(),
                                mainGUI.getPerfomStatistics()[GateBar.ordinal()][deviceNo]);

        naraBarMessages.add(new NaraMsgItem(Nara_MsgType.Status));
        naraBarMessages.add(new NaraMsgItem(Nara_MsgType.GateDown));        
        
        if (connectionType[GateBar.ordinal()][deviceNo] == OSP_enums.ConnectionType.RS_232.ordinal()) {
            String portNumStr = "COM" + deviceComID[GateBar.ordinal()][deviceNo];
            try {
                portIdentifier = CommPortIdentifier.getPortIdentifier(portNumStr);
            } catch (NoSuchPortException ex) {
                logParkingException(Level.SEVERE, ex, "NoSuchPortException as '" + portNumStr + "'", 
                        deviceNo);
                
                String msg = MISSING_PORT_DIALOG_1.getContent() + System.lineSeparator() +
                        MISSING_PORT_DIALOG_2.getContent() + deviceNo + System.lineSeparator() +
                        MISSING_PORT_DIALOG_3.getContent() + GateBar.getContent() + System.lineSeparator() +
                        System.lineSeparator() + OsParking_FUNC_WARNING.getContent();
                
                JOptionPane.showMessageDialog(mainGUI, msg,
                                ERROR_DIALOGTITLE.getContent() + " : " + portNumStr,
                                JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // 차단기 연결 소켓을 통하여 들어오는 메시지를 읽은 쓰레드 생성 및 가동
            SocketReader reader = new SocketReader(mainGUI, this, deviceNo);
            reader.start();
        }
        
        msgSender = new Thread("osp_NaraBarWriterThread")
        {
            public void run() {
                while (true) {
                    try {
                        //<editor-fold desc="-- Wait until it is connected">
                        synchronized (mainGUI.getSocketMutex()[GateBar.ordinal()][deviceNo]) {
                            if (!IDevice.isConnected(mainGUI.getDeviceManagers()[GateBar.ordinal()][deviceNo], 
                                    GateBar, deviceNo))
                            {
                                try {
                                    mainGUI.getSocketMutex()[GateBar.ordinal()][deviceNo].wait();
                                } catch (InterruptedException ex) {
                                    logParkingException(Level.SEVERE, ex, "waiting LEDnotice socket", deviceNo);
                                }
                            }
                        }
                        if (neverConnected) {
                            neverConnected = false;
                        }
                        //</editor-fold>
                        synchronized (getMsgQdoor()) {
                            if (getNaraBarMessages().peek() == null) {
                                getMsgQdoor().wait();
                            }
                        }
                        
                        while (!naraBarMessages.isEmpty()) {
                            //<editor-fold desc="-- Write message to LEDnotice if connected">
                            NaraMsgItem currItem = getNaraBarMessages().peek();
//                            System.out.println("curr Item : " + currItem.getType());
                            
                            try {
                                OutputStream oStream = null;
                                if (connectionType[GateBar.ordinal()][deviceNo] == RS_232.ordinal()) 
                                {
                                    if (serialPort != null) {
                                        oStream = serialPort.getOutputStream();
                                    }
                                } else {
                                    if (socket != null) {
                                        oStream = socket.getOutputStream();
                                    }
                                }
                                //<editor-fold desc="-- Handle Serial connection">
                                if (oStream != null) 
                                {
//                                    System.out.print("\t\t[Bar] ");
                                    switch (currItem.getType()) {
                                        
                                        case GateDown:
                                            if (barState == UNKNOWN || 
                                                    barState == BarStatus.OPENED || barState == BarStatus.OPENING) {
                                                oStream.write(getBarCommand(currItem.getType()));
                                                getNaraBarMessages().peek().incSendCount();
                                                if (DEBUG) 
                                                    System.out.println(currItem.getType().toString() + "~>");
                                            } else {
                                                currItem = getNaraBarMessages().remove();
                                            }
                                            break;

                                        case GateUp:
                                            if (barState == BarStatus.OPENED || barState == BarStatus.OPENING) {
                                                currItem = getNaraBarMessages().remove();
                                            } else {
                                                oStream.write(getBarCommand(currItem.getType()));
                                                getNaraBarMessages().peek().incSendCount();
                                                if (DEBUG) 
                                                    System.out.println(currItem.getType().toString() + "~>");
                                            }
                                            break;

                                        default:
                                            oStream.write(getBarCommand(currItem.getType()));
                                            getNaraBarMessages().peek().incSendCount();
                                            if (DEBUG) 
                                                System.out.println(currItem.getType().toString() + "~>");
                                            if (currItem.getType() == Nara_MsgType.Status) 
                                                currItem = getNaraBarMessages().remove();
                                            break;
                                    }
                                }
                                //</editor-fold>
                                
                            } catch (IOException ex) {
                                logParkingException(Level.SEVERE, ex, "writing LEDnotice serial port", deviceNo);
                            }
                            //</editor-fold>
                            
                            try {
                                //<editor-fold desc="-- Delay some milliseconds by message type">
                                switch (currItem.getType()) {
                                    default:
                                        Thread.sleep(60);
                                        break;
                                }
                                //</editor-fold>
                            } catch (InterruptedException ex) {
                                logParkingException(Level.SEVERE, ex, "taking a rest between two sending", deviceNo);
                                try {
                                    throw ex;
                                } catch (InterruptedException ex1) {
                                    logParkingException(Level.SEVERE, ex1, "rethrowing exception to outer", deviceNo);
                                }
                            }
                        }
                    } catch (InterruptedException ex) {
                        logParkingException(Level.SEVERE, ex, "Interrupted while waiting queue item added", deviceNo);
                        return;
                    }
                }
            }
        };
        msgSender.start();        
    }

    @Override
    public void run() {
        
        while (true) {
            synchronized(getMsgArrived()) {
                try {
                    // 포트에 메시지가 도달할 때까지 대기
                    getMsgArrived().wait();
                    mainGUI.tolerance[GateBar.ordinal()][deviceNo].assignMAX();
                } catch (InterruptedException ex) {
                    logParkingException(Level.SEVERE, ex, "closing serial port", deviceNo);
                }
            }

            switch (getMsg()) {
                //<editor-fold desc="-- 도착한 메시지를 처리">
                case GateUpAction:
                    // 메시지 큐 맨 앞에 개방 명령있으면 이를 제거함
                    if (getNaraBarMessages().peek().getType() == GateUp) {
                        
                        NaraMsgItem item = getNaraBarMessages().remove();
                        if (DB_Access.storePassingDelay) 
                        {
                            /**
                             * record vehicle processing performance if it is meaningful.
                             */
                            if (mainGUI.getPassingDelayStat()[deviceNo].isAccumulatable()) {
                                String msg = mainGUI.getPassingDelayStat()[deviceNo].recordBarACKspeed(deviceNo);
                                if (msg != null)
                                    addMessageLine(mainGUI.getMessageTextArea(), msg);                
                            }

                            if (Globals.DEBUG) {
                                // time the manager took until it gets ACK for an open command it issued
                                int ackDelay = (int)
                                        (System.currentTimeMillis() - mainGUI.openCommandIssuedMs[deviceNo]);

                                mainGUI.getPerfomStatistics()[GateBar.ordinal()][deviceNo]
                                        .addAckDelayStatistics(ackDelay, item.getSendCount());
                            }
                        }
                    }
                    setBarState(BarStatus.OPENING);
                    break;

                case GateUpOK:
                case GateState_UP_OK:
                    setBarState(BarStatus.OPENED);
                    break;
                    
                case GateDownAction:
                    // 메시지 큐 맨 앞에 폐쇄 명령있으면 이를 제거함
                    if (getNaraBarMessages().peek().getType() == GateDown) {
                        getNaraBarMessages().remove();
                    }
                    setBarState(BarStatus.CLOSING);
                    break;

                case GateDownOK:
                case GateState_DOWN_OK:
                    setBarState(BarStatus.CLOSED);
                    break;

                default:
                    break;
                //</editor-fold> 
            }
        }
    }

    /**
     * @return the naraBarMessages
     */
    public NaraMessageQueue getNaraBarMessages() {
        return naraBarMessages;
    }
    
//    @Override
    public void finishConnection(Exception e, String description, byte deviceNo) {
        
        synchronized(mainGUI.getSocketMutex()[GateBar.ordinal()][deviceNo]) {
        
            if (connectionType[GateBar.ordinal()][deviceNo] == OSP_enums.ConnectionType.RS_232.ordinal()) {
                try {
                    if (serialPort != null) {
                        serialPort.close();
                        serialPort = null;
                    }
                    oStrm.close();
                } catch (IOException ex) {
                    logParkingException(Level.SEVERE, ex, "closing serial port", deviceNo);
                }
            } else {
                if (Globals.isConnected(getSocket())) 
                {
                    String msg =  "Gate bar #" + deviceNo;

                    addMessageLine(mainGUI.getMessageTextArea(), "  ------" + msg + " disconnected");
                    logParkingException(Level.INFO, e, description + " " + msg);

                    long closeTm = System.currentTimeMillis();

                    mainGUI.getSockConnStat()[GateBar.ordinal()][deviceNo].recordSocketDisconnection(closeTm);

                    if (DEBUG) {
                        System.out.println("M9. Gate bar #" + deviceNo + " disconnected at: " + closeTm);                        
                    }
                    closeSocket(getSocket(), "while gate bar socket closing");
                    setSocket(null);
                }             
            }
        }
        
        if (mainGUI.getConnectDeviceTimer()[GateBar.ordinal()][deviceNo] != null) {
            if (!mainGUI.isSHUT_DOWN()) {
                mainGUI.getConnectDeviceTimer()[GateBar.ordinal()][deviceNo].reRunOnce();
                addMessageLine(mainGUI.getMessageTextArea(), "Trying to connect to Gate Bar #" + deviceNo);
            }
        }        
    }

    @Override
    public void stopOperation(String reason) {
//        aa
    }

    private boolean neverConnected = true;
    
    @Override
    public boolean isNeverConnected() {
        return neverConnected;
    }

    @Override
    public void setSerialPort(SerialPort serialPort) {
        try {
            if (serialPort != null) {
                serialPort.notifyOnDataAvailable(true);
                serialPort.addEventListener(new RS_232_Manager(this, serialPort));
            }
        } catch (TooManyListenersException ex) {
            logParkingException(Level.SEVERE, ex, "Serial port inputstream", deviceNo);
        }
        this.serialPort = serialPort;    
        try {
            oStrm = serialPort.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(NaraBarMan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public SerialPort getSerialPort() {
        return serialPort;
    }

    @Override
    public CommPortIdentifier getPortIdentifier() {
        return portIdentifier;
    }

    @Override
    public CommPort getCommPort() {
        return commPort;
    }

    @Override
    public void setCommPort(CommPort commPort) {
        this.commPort = commPort;
    }

    @Override
    public int getBaudRate() {
        return 9600;
    }
    

    /**
     * Create gate bar command(byte array) depending on the command type.
     * @param msgType command type (enum type)
     * @return message(command) byte array
     */
    public byte[] getBarCommand(Nara_MsgType msgType) {
        String msgCore = "";
        
        //<editor-fold desc="-- determine core message string">
        switch (msgType) {
            case GateUp:
                msgCore = gate_up;
                break;
            case GateDown:
                msgCore = gate_down;
                break;
            case Status:
                msgCore = status;
                break;
            case GateState_UpLOCK:
                msgCore = up_lock;
                break;
            case GateUnLOCK:
                msgCore = unlock;
                break;
            case SystemReset:
                msgCore = system_reset;
                break;
            default:
                break;
        }
        //</editor-fold>
        
        byte[] barCommand = new byte[msgCore.length() + 2]; 
        
        barCommand[0] = 0x02;
        System.arraycopy(msgCore.getBytes(), 0, barCommand, 1, msgCore.length());
        barCommand[msgCore.length() + 1] = 0x03;  
        
        return barCommand;
    }
        
    private Object msgQdoor = new Object();
    Object ackArrived = new Object();
    private NaraMessageQueue naraBarMessages;

    /**
     * @return the barState
     */
    public BarStatus getBarState() {
        return barState;
    }

    /**
     * @param barState the barState to set
     */
    public void setBarState(BarStatus barState) {
        this.barState = barState;
    }

    private ParkingTimer gateCloser = null;    
    
    public void scheduleGateCloseAction(NaraBarMan gateMan, int carPassingDelayMs) {
        // schedule to close the gate bar
        if (gateCloser == null) {
            gateCloser = new ParkingTimer("timerCloseBar_" + deviceNo, false, 
                    new GateCloser(gateMan), 0, carPassingDelayMs);
        } else {
            gateCloser.cancelTask();
        }
        gateCloser.reschedule(carPassingDelayMs);
    }

    @Override
    public void setSocket(Socket eBoardSocket) {
        this.socket = eBoardSocket;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    void setMsg(Nara_MsgType deliveredMessage) {
        msg = deliveredMessage;
    }

    /**
     * @return the msg
     */
    public NaraEnums.Nara_MsgType getMsg() {
        return msg;
    }

    /**
     * @return the msgQdoor
     */
    public Object getMsgQdoor() {
        return msgQdoor;
    }

    /**
     * @param msgQdoor the msgQdoor to set
     */
    public void setMsgQdoor(Object msgQdoor) {
        this.msgQdoor = msgQdoor;
    }

    /**
     * @return the msgArrived
     */
    public Object getMsgArrived() {
        return msgArrived;
    }

    /**
     * @param msgArrived the msgArrived to set
     */
    public void setMsgArrived(Object msgArrived) {
        this.msgArrived = msgArrived;
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeMessage(OSP_enums.MsgCode code, byte[] msgBytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class GateCloser extends TimerTask
    {
        NaraBarMan gateMan;

        public GateCloser (NaraBarMan gateMan)
        {
            this.gateMan = gateMan;
        }

        public void run()
        {
            gateMan.getNaraBarMessages().add(new NaraMsgItem(Nara_MsgType.GateDown));
        }
    }    
}

class RS_232_Manager implements SerialPortEventListener {
    NaraBarMan manager;
    InputStream inStream;
    
    RS_232_Manager(NaraBarMan manager, SerialPort port) {
        try {
            this.manager = manager;
            inStream = port.getInputStream();
        } catch (IOException ex) {
            logParkingException(Level.SEVERE, ex, "Serial port inputstream", manager.deviceNo);
        }
    }
    
    @Override
    public void serialEvent(SerialPortEvent e) {
        switch(e.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                // 자료를 포트에서 읽어서 바른 차단기 메시지이면 차단기 명령 수신 대기자를 깨움
                manager.setMsg(readDeliveredMessage(inStream));
                if (manager.getMsg() != Broken) {
                    System.out.println("\t\t  <~ " + manager.getMsg() + " [Bar]");
                    synchronized(manager.getMsgArrived()) {
                        manager.getMsgArrived().notify();
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * 시리얼 포트의 입력 스트림에서 바이트들을 읽어서 이로부터 메시지를 뽑아낸다
     * @param inStream 시리얼 포트에 연결된 입력 스트림
     * @return 도달한 메시지 유형(타입), 메시지가 깨지거나 EOF 등 예외상황이 발생하면 Broken 값을 반환한다.
     */
    public static Nara_MsgType readDeliveredMessage(InputStream inStream) {
        Nara_MsgType messageType = Broken;
        byte[] barMessage = new byte[100];
        int byteIndex = 0;
        byte posiETX;
        
        while ( true )
        {
            try {
                posiETX = (byte)(inStream.read());
                if (posiETX == -1) {
                    break;
                } 
                
                barMessage[byteIndex++] = posiETX;
                
                if (posiETX == 3) {
                    // 도착한 메시지 유형을 식별한다
                    messageType = getMessageType(barMessage, byteIndex - 1);
                    break;
                }
            }
            catch (IOException ex) {
                break;
            }
        }
        return messageType;
    }       

    /**
     * Identify message type out of byte array and its length.
     * @param barMessage message byte array 
     * @param length number of bytes in the message
     * @return Gate bar message type
     */
    public static Nara_MsgType getMessageType(byte[] barMessage, int length) {
        Nara_MsgType type = Broken;
        
        if (barMessage[0] == 2) {
            byte[] byteCore = Arrays.copyOfRange(barMessage, 1, length);
            
            String msgCore = new String(byteCore);
            if (msgCore.substring(0, 5).equals("GATE=")) {
                String statusCore = null;
                int commaLoc = msgCore.indexOf((int)',');
                
                if (commaLoc > -1) {
                    statusCore = msgCore.substring(5, commaLoc);
                    switch(statusCore) {
                        case up_lock:
                            type = GateUpLOCK;
                            break;
                        case up_ok:
                            type = GateState_UP_OK;
                            break;
                        case up_action:
                            type = GateState_UP_ACTION;
                            break;
                        case down_ok:
                            type = GateState_DOWN_OK;
                            break;
                        case down_action:
                            type = GateState_DOWN_ACTION;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                switch(new String(byteCore)) {
                    case gate_up_action:
                        type = GateUpAction;
                        break;
                    case gate_up_ok:
                        type = GateUpOK;
                        break;
                    case gate_down_action:
                        type = GateDownAction;
                        break;
                    case gate_down_ok:
                        type = GateDownOK;
                        break;
                    default:
                        break;
                }
            }
        }
        return type;
    }
}
