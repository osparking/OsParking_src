/**
 * Copyright (C) 2016 Open Source Parking, Inc.(www.osparking.com)
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
package com.osparking.osparking.device.BlackFly;

import static com.osparking.global.CommonData.ImgHeight;
import static com.osparking.global.CommonData.ImgWidth;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.gfinishConnection;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingOperation;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.ERROR_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.MISSING_FILE_DIALOGTITLE;
import static com.osparking.global.names.ControlEnums.MsgContent.FILE_PATH;
import com.osparking.global.names.IDevice;
import static com.osparking.global.names.OSP_enums.DeviceType.Camera;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.osparking.ControlGUI;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.bytedeco.javacpp.FlyCapture2;
import org.bytedeco.javacpp.FlyCapture2.BusManager;
import org.bytedeco.javacpp.FlyCapture2.CameraInfo;
import static org.bytedeco.javacpp.FlyCapture2.PGRERROR_OK;
import org.bytedeco.javacpp.FlyCapture2.PGRGuid;
import org.bytedeco.javacpp.FlyCapture2.Error;
import org.bytedeco.javacpp.FlyCapture2.GigECamera;
import static org.bytedeco.javacpp.FlyCapture2.PIXEL_FORMAT_BGR;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class BlackFlyManager extends Thread implements 
        IDevice.IManager, IDevice.ISocket 
{  
    private byte cameraID = 0; // ID of the gate bar being served by this manager. A valid ID starts from 1.
    private ControlGUI mainForm; // main form of the gate bar simulator.

    private Socket socket = null;
    
    FileWriter fw;
    
    PGRGuid guid = new PGRGuid(); //It is used to uniquely identify a camera. 
    GigECamera camera = new GigECamera();
    static BusManager busMgr = new BusManager(); 
    static int[] numCameras = new int[1];
    boolean neverBeenConnected = true;
    public Object takePicture = new Object();
    public Object processTag = new Object();
    public String carTagNumber = new String("");
    
    public FlyCapture2.Image rawImage;

    static {
        busMgr.GetNumOfCameras(numCameras);
    }
    
    LPR_DLL recognizer;
    
    private Object disconnected = new Object();
    int currImgSN = 0; // the ID of the most recently processed car entry image
    
    /**
     * New design plate tag, width is 250 pixels.
     */
    static final int SMALL_PlATE = 1;
    
    /**
     * New design plate tag, width is 300 pixels.
     */
    static final int NORMAL_PlATE = 2;
    
    /**
     * License Plate Recognition timeout (unit: millisecond).
     * Value range in general : 1500 ~ 2000
     */
    static final int LPR_TIMEOUT = 1500;
    
    /**
     * Vertical offset from where a plate can be found.
     */
    static final int V_OFFSET_TOP = (int)(ImgWidth * 0.25);

    /**
     * Vertical offset to where a plate can be found.
     */    
    static final int V_OFFSET_BOTTOM = (int)(ImgWidth * 0.75);  
    
    /**
     * Horizontal tilt angle compensating angle in radian.
     * Possible value range: -20.0 ~ +20.0
     */
    static final double H_TILT_ANGLE = 0.0;
    
    /**
     * Horizontal tilt angle compensating angle in radian.
     * Possible value range: -20.0 ~ +20.0
     */
    static final double V_TILT_ANGLE = 0.0;   
    
    static final String LPR_FOLDER = 
            System.getProperty("user.dir") + File.separator + "LIB";
    
    public BlackFlyManager(ControlGUI mainForm, byte cameraID) {
        super("osp_Camera_" + cameraID + "_Manager");
        this.mainForm = mainForm;
        this.cameraID = cameraID;
        
        fw = mainForm.getIDLogFile()[Camera.ordinal()][cameraID];
        String dllName = "";
        if (Platform.isWindows())
            dllName = "ANPRS_OCR";
        else
            dllName = "ANPRS_OCR_Linux";
        
        String dllPathExt = LPR_FOLDER + File.separator + dllName + ".dll";
        File dF = new File(dllPathExt);
        
        if (dF.exists() && !dF.isDirectory()) { 
            try {
                if (DEBUG) {
                    System.setProperty("jna.debug_load", "true");
                    System.setProperty("jna.debug_load.jna", "true");
                }
//                System.setProperty("jna.platform.library.path", LPR_FOLDER);
                System.setProperty("jna.library.path", LPR_FOLDER);
                if (DEBUG) {
                    System.setProperty("java.library.path", LPR_FOLDER);
//                    System.loadLibrary(dllName);
                }
                recognizer = (LPR_DLL) Native.loadLibrary(dllName, LPR_DLL.class);
                if (this.cameraID <= numCameras[0]) {
                    if (findCamera() > 0) {
                        initBusanANPR();
                    }
                } else {
                    logParkingException(Level.WARNING, null, 
                            "Connected camera is not enough", cameraID);
                    String msg = "Connected camera is not enough" + System.lineSeparator() +
                            System.lineSeparator() + "※OsParking has trouble in starting";
                    JOptionPane.showMessageDialog(mainForm, msg,
                            ERROR_DIALOGTITLE.getContent(), JOptionPane.WARNING_MESSAGE);                    
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainForm, "LPR, excep: " + ex.getMessage());
            }
        } else {
            String msg = FILE_PATH.getContent() + dllPathExt + System.lineSeparator() +
                    System.lineSeparator() + "※LPR would not function properly";
            JOptionPane.showMessageDialog(mainForm, msg,
                            MISSING_FILE_DIALOGTITLE.getContent(),
                            JOptionPane.WARNING_MESSAGE);
        }
    }

    public void run() {
        Error error = camera.StartCapture();
        
        if (error.equals(PGRERROR_OK)) {
        } else {
            logParkingException(Level.WARNING, null, 
                    "Start capture: " + error.GetDescription().getString(), 
                    cameraID);
        }
        
        while (true) {
            try {
                synchronized(takePicture) {
                    takePicture.wait();
                }
                rawImage = new FlyCapture2.Image();
                camera.RetrieveBuffer(rawImage); 
                
                carTagNumber = getCarNumber(rawImage);
                synchronized(processTag) {
                    processTag.notify();
                }
            } catch (InterruptedException ex) {
                logParkingException(Level.WARNING, ex, "while waiting take picture command", 
                        cameraID);
            }
        }
    }
    
    public void emitDisconnected() {
        getDisconnected().notifyAll();
    }

    @Override
    public boolean isConnected() {
        Error result;

        if (camera.IsConnected() || neverBeenConnected) 
        {
            result = busMgr.RescanBus();

            if (result.equals(PGRERROR_OK)) {
                result = busMgr.GetNumOfCameras(numCameras);
                if (result.equals(PGRERROR_OK)) 
                {
                    if (numCameras[0] == 1) 
                    {
                        if (camera.IsConnected()) 
                        {
                            if (!mainForm.getSockConnStat()[Camera.ordinal()][cameraID].
                                    isConnected())
                            {
                                mainForm.getSockConnStat()[Camera.ordinal()][cameraID].
                                        recordSocketConnection(System.currentTimeMillis());
                            }
                            return true;
                        } else {
                            disConnCount = 0;
                            camera.Disconnect();
                        }
                    }
                }
            }
        }
        if (mainForm.getSockConnStat()[Camera.ordinal()][cameraID].isConnected()) 
        {
            mainForm.getSockConnStat()[Camera.ordinal()][cameraID].recordSocketDisconnection(
                    System.currentTimeMillis());
        }
        return false;
    }
    
    static int disConnCount = 0;
    /**
     * Find (ID - 1)-th camera and connect it to the class variable 'camera'.
     */
    public void connectCamera(byte cameraID) {
        Error error = busMgr.GetCameraFromIndex(cameraID - 1, guid);
        
        if (error.equals(PGRERROR_OK)) {
            error = camera.Connect(guid);
            if (error.equals(PGRERROR_OK)) {
                if (camera.IsConnected()) {
                    neverBeenConnected = false;
                    logParkingOperation(OpLogLevel.LogAlways, 
                            "카메라에 연결되었습니다.");
                } else {
                    logParkingException(Level.WARNING, null, "Camera not connected", cameraID);
                }
            } else {
                if (disConnCount++ % 10 == 0) {
                    String errorMsg = "Connection Error: " + error.GetDescription().getString();
                    logParkingException(Level.WARNING, null, errorMsg, cameraID);
                }
            }
        } else {
            String errorMsg = "Camera ID init error: " + error.GetDescription().getString();
            logParkingException(Level.WARNING, null, errorMsg, cameraID);
        }        
    }

    @Override
    public void stopOperation(String reason) {
        gfinishConnection(Camera, null,  
                reason, 
                cameraID,
                mainForm.getSocketMutex()[Camera.ordinal()][cameraID],
                socket,
                mainForm.getMessageTextArea(), 
                mainForm.getSockConnStat()[Camera.ordinal()][cameraID],
                mainForm.getConnectDeviceTimer()[Camera.ordinal()][cameraID],
                mainForm.isSHUT_DOWN()
                );        
        camera.StopCapture();
        interrupt();        
    }

    @Override
    public boolean isNeverConnected() {
        return neverBeenConnected;
    }

    @Override
    public void setSocket(Socket s) {
        socket = s;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    public void initBusanANPR() {
        final int SY_H_ANGLE = 1; // horizontal angle
        final int SY_V_ANGLE = 2; // vertical angle
        boolean r1 = false, r2 = false, r3 = false, r4 = false;

        String datPathExt = LPR_FOLDER + File.separator + "ANPRS.dat";
        File dF = new File(datPathExt);
        
        if (dF.exists() && !dF.isDirectory()) 
        {         
            if (recognizer.syInit_ANPRS(LPR_FOLDER)) {
                // set timeout value
                r1 = recognizer.sySetImageMode(SMALL_PlATE, LPR_TIMEOUT);

                //  인식할 영상의 상하 영역 설정
                r2 = recognizer.sySetOcrZone(V_OFFSET_TOP, V_OFFSET_BOTTOM);

                // set camera vs tag image picture taken angle
                r3 = recognizer.sySetParameter_ANPRS(SY_H_ANGLE, 
                        H_TILT_ANGLE * Math.PI / 180 );

                r4 = recognizer.sySetParameter_ANPRS(SY_V_ANGLE, 
                        V_TILT_ANGLE * Math.PI / 180 );
                byte[] engVer = new byte[100];
                byte[] datVer = new byte[100];
                recognizer.syGetVersion_ANPRS(engVer, datVer);

                System.out.println("Eng ver: " + convert2string(engVer) + ", Data ver: " +
                        convert2string(datVer));

                if (r1 && r2 && r3 && r4) {
                    logParkingOperation(OpLogLevel.LogAlways, "LPR module init success");
                } else {
                    logParkingException(Level.SEVERE, null, "LPR module init failure", cameraID);
                }
            } else {
                if (datFileMissingWarningPopupCount++ < 2) {
                    String msg = "ANPRS Initialization Failure" + System.lineSeparator() +
                            System.lineSeparator() + "※LPR would not function properly";
                    JOptionPane.showMessageDialog(mainForm, msg,
                            ERROR_DIALOGTITLE.getContent(), JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            if (datFileMissingWarningPopupCount++ < 2) {
                String msg = FILE_PATH.getContent() + datPathExt + System.lineSeparator() +
                        System.lineSeparator() + "※LPR would not function properly";
                JOptionPane.showMessageDialog(mainForm, msg,
                        MISSING_FILE_DIALOGTITLE.getContent(), JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    static int datFileMissingWarningPopupCount = 0;

    /**
     * @return the disconnected
     */
    public Object getDisconnected() {
        return disconnected;
    }

    public void getUniqueCameraID() {

    }

    /**
     * @return the takePicture
     */
    public Object getTakePicture() {
        return takePicture;
    }

    public int findCamera() {
        int[] numCameras = new int[1];
        busMgr.GetNumOfCameras(numCameras);
        int numOfCameras = numCameras[0];

        if (numOfCameras == 0) {
            logParkingException(Level.WARNING, null, "카메라가 보이지 않습니다.", cameraID);
        } else {
            for (int i = 0; i < numCameras[0]; i++) {
                connectCamera(cameraID);
                CameraInfo camInfo = new CameraInfo();

                Error error = camera.GetCameraInfo(camInfo); //카메라 정보를 가져온다
                if (error.equals(PGRERROR_OK)) {
                    if (error.equals(PGRERROR_OK)) {
                        logParkingOperation(OpLogLevel.LogAlways, 
                                camInfo.modelName().getString() + 
                                " 카메라 검색 완료."); 
                    } else {
                        logParkingException(Level.WARNING, null, 
                                error.GetDescription().getString(), cameraID);  
                    }
                } else {
                    logParkingException(Level.WARNING, null, 
                            error.GetDescription().getString(), cameraID);                                
                }                
            }
        }
        return numOfCameras;
    }

    private String convert2string(byte[] carTagArr) {
        int len = 0;
        for ( ; carTagArr[len] != 0; len++)
            ;
        byte[] carTagArrPure = Arrays.copyOf(carTagArr, len);

        String carTagNumber = null;
        try {
            carTagNumber = new String(carTagArrPure, "ksc5601");
        }
        catch (UnsupportedEncodingException uee) {}
        return carTagNumber;    
    }

    /**
     * @return the carTagNumber
     */
    public String getCarTagNumber() {
        return carTagNumber;
    }

    /**
     * @param carTagNumber the carTagNumber to set
     */
    public void setCarTagNumber(String carTagNumber) {
        this.carTagNumber = carTagNumber;
    }

    public interface LPR_DLL extends Library {

        boolean syInit_ANPRS(String szExeDirName);

        boolean sySetImageMode(int iTypeMode, int iTimeOutMs);

        boolean sySetOcrZone(int iTop, int iBottom);

        boolean syExec_ANPRS(String szImageFileName, byte[] szOcrCode,
                byte[] iPlateType, byte[] iLeft, byte[] iTop, byte[] iRight, byte[] iBottom);

        /**
         * 
         * @param imageBuffer memory pointer for the image
         * @param width image width
         * @param height image height
         * @param szOcrCode recognition result
         * @param iPlateType car type based on the plate recognition
         * @param iLeft location of the number plate in the image
         * @param iTop same as above
         * @param iRight same as above
         * @param iBottom same as above
         * @return 
         */
        boolean syExec_ANPRSm(byte[] imageBuffer, 
                int width, int height, byte[] szOcrCode, 
                byte[] iPlateType, byte[] iLeft, byte[] iTop, byte[] iRight, byte[] iBottom); 

        boolean sySetParameter_ANPRS(int iParaType, double dParaValue); 
        boolean syGetVersion_ANPRS(byte[] szEngineVersion, byte[] szDataVersion); 
        boolean syDeInit_ANPRS(); 
    }    
    
    private String getCarNumber(FlyCapture2.Image rawImage) {
        byte[] carTagArr = new byte[100];
        byte[] iPlateType = new byte[4];
        byte[] iLeft = new byte[4];
        byte[] iTop = new byte[4];
        byte[] iRight = new byte[4];
        byte[] iBottom = new byte[4];     
        
        FlyCapture2.Image bgrImage = new FlyCapture2.Image();
        byte[] imageBytes = new byte[ImgWidth * ImgHeight * 3];  
        byte[] imageBytesRvd = new byte[ImgWidth * ImgHeight * 3];
                
        rawImage.Convert(PIXEL_FORMAT_BGR , bgrImage);
        bgrImage.GetData().get(imageBytes);
        for (int i=0; i < imageBytes.length; i++) {
            imageBytesRvd[i] = imageBytes[imageBytes.length - 1 - i];
        }

        // record start time here
        recognizer.syExec_ANPRSm(imageBytesRvd, ImgWidth, ImgHeight, 
                carTagArr, iPlateType, iLeft, iTop, iRight, iBottom);

        // determine the length of pure car tag number
        return convert2string(carTagArr);

//        ByteBuffer wrapped = ByteBuffer.wrap(iPlateType);
//        int plateType = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
//        appendLine(logArea, "tag number : " + carTagNumber + 
//                ", LPR time : " + recogTime + "ms, vehicle tag type: " + 
//                getPlateTypeStr(plateType), true);
    }
}
