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
package com.osparking.e_board;

import com.osparking.global.names.EBD_DisplaySetting;
import com.osparking.global.names.OSP_enums.DisplayArea;
import static com.osparking.global.names.OSP_enums.DisplayArea.TOP_ROW;
import java.awt.Insets;
import javax.swing.JTextField;
import com.osparking.global.names.OSP_enums.EBD_Effects;
import static com.osparking.global.names.OSP_enums.EBD_Effects.BLINKING;
import static com.osparking.global.names.OSP_enums.EBD_Effects.LTOR_FLOW;
import static com.osparking.global.names.OSP_enums.EBD_Effects.RTOL_FLOW;
import static javax.swing.text.html.HTML.Tag.HEAD;

/**
 *
 * @author Song, YongSeok <Song, YongSeok at Open Source Parking Inc.>
 */
public class InnerCycleTask implements Runnable {
    A_EBD_GUI mainform;
//<<<<<<< HEAD
    private DisplayArea row;
//=======
//    private byte row;
//>>>>>>> osparking/master
    EBD_DisplaySetting rowSetting;
    int textWidth, flowDelta;    
    
    private JTextField eBoardRow;
    private int topMG, leftMG, botMG, rightMG;

    int top_count=  1;
    int bot_count = 1;
    int count = 1;
    
//<<<<<<< HEAD
    public InnerCycleTask(A_EBD_GUI mainform, DisplayArea row, EBD_DisplaySetting rowSetting, int textWidth, 
//=======
//    public InnerCycleTask(A_EBD_GUI mainform, byte row, EBD_DisplaySetting rowSetting, int textWidth, 
//>>>>>>> osparking/master
            int flowDelta, JTextField eBoardRow){
        this.mainform = mainform;
        this.row = row;
        this.rowSetting = rowSetting;
        
        this.textWidth = textWidth;
        this.flowDelta = flowDelta;
        
        this.eBoardRow = eBoardRow;
        initMargin(eBoardRow, rowSetting.displayPattern);
    }
    
    public void run(){
        
        switch(rowSetting.displayPattern) {
            case RTOL_FLOW : 
                if(leftMG > -textWidth){
                    if (row == TOP_ROW) 
                        mainform.topTextField.setMargin(new Insets(topMG, leftMG -= flowDelta, botMG, rightMG));
                    else
                        mainform.botTextField.setMargin(new Insets(topMG, leftMG -= flowDelta, botMG, rightMG));
                        
                }else{
//<<<<<<< HEAD
                    mainform.parking_Display_InnerTimer[row.ordinal()].cancelTask();
                    mainform.parking_Display_OuterTimer[row.ordinal()].reRunOnce();
//=======
//                    mainform.parking_Display_InnerTimer[row].cancelTask();
//                    mainform.parking_Display_OuterTimer[row].reRunOnce();
//>>>>>>> osparking/master
                }
                break;
                
            case LTOR_FLOW : 
                if(rightMG > -textWidth){
                    if (row == TOP_ROW) {
                        mainform.topTextField.setMargin(new Insets(topMG, leftMG, botMG, rightMG -= flowDelta));
                    } else {
                        mainform.botTextField.setMargin(new Insets(topMG, leftMG, botMG, rightMG -= flowDelta));
                    }                    
                }else{
//<<<<<<< HEAD
                    mainform.parking_Display_InnerTimer[row.ordinal()].cancelTask();
                    mainform.parking_Display_OuterTimer[row.ordinal()].reRunOnce();                    
//=======
//                    mainform.parking_Display_InnerTimer[row].cancelTask();
//                    mainform.parking_Display_OuterTimer[row].reRunOnce();                    
//>>>>>>> osparking/master
                }
                break;
                
            default:
                break;                
        }        
        
        eBoardRow.repaint();
    }
    
    public void initMargin(JTextField eboard, EBD_Effects pattern){
        switch (pattern) {
            case RTOL_FLOW:
                eboard.setHorizontalAlignment(javax.swing.JTextField.LEFT);
                topMG = eboard.getMargin().top;
                leftMG = eboard.getWidth();
                botMG = eboard.getMargin().bottom;
                rightMG = -textWidth;
                break;

            case LTOR_FLOW:
                eboard.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                topMG = eboard.getMargin().top;
                leftMG = -textWidth;
                botMG = eboard.getMargin().bottom;
                rightMG = eboard.getWidth();
                break;
                
            case STILL_FRAME:
                eboard.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                eboard.setMargin(new Insets(2, 2, 2, 2) );
                break;
                
            case BLINKING:
                eboard.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                eboard.setMargin(new Insets(2, 2, 2, 2) );
                break;
                
            default:
                break;
        }
    }
}
    
