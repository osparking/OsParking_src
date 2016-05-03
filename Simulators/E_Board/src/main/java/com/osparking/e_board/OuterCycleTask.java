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

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import com.osparking.global.names.EBD_DisplaySetting;
import static com.osparking.global.names.EBD_DisplaySetting.EBD_PERIOD;
import static com.osparking.global.names.EBD_DisplaySetting.MAX_PERIOD;
import javax.swing.JTextField;
import static com.osparking.global.Globals.*;
import com.osparking.global.names.OSP_enums.DisplayArea;
import static com.osparking.global.names.OSP_enums.DisplayArea.TOP_ROW;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.VERBATIM;
import static com.osparking.global.names.OSP_enums.EBD_Effects.*;
import static javax.swing.text.html.HTML.Tag.HEAD;

/**
 *
 * @author Song, YongSeok <Song, YongSeok at Open Source Parking Inc.>
 */
public class OuterCycleTask implements Runnable{
    A_EBD_GUI mainform;
    private DisplayArea row;
    EBD_DisplaySetting rowSetting;
    
    JTextField eBoardTextField;
    int textWidth, flowDelta;
    int count = 1;
    private boolean isTextShowing = false;
        
    AffineTransform affinetransform = new AffineTransform();     
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    
    /**
     * 
     * @param mainform
     * @param row
     * @param rowSetting 
     */
    public OuterCycleTask(A_EBD_GUI mainform, DisplayArea row, EBD_DisplaySetting rowSetting)
    {
        this.mainform = mainform;
        this.row = row;
        this.rowSetting = rowSetting;
               
        eBoardTextField = (row == TOP_ROW ? mainform.topTextField : mainform.botTextField);
        
        flowDelta = Math.round(MAX_PERIOD * eBoardTextField.getWidth() / (float)rowSetting.displayCycle);
        flowDelta = (flowDelta == 0) ?  1 : flowDelta;
        
        if (rowSetting.displayPattern == RTOL_FLOW || rowSetting.displayPattern == LTOR_FLOW)
            EBD_PERIOD = rowSetting.displayCycle * flowDelta / eBoardTextField.getWidth();
    }
    
    public void run(){
        String renderedContent = null;

        if (rowSetting.contentType == VERBATIM)
            renderedContent = rowSetting.verbatimContent;
        else
            renderedContent = getRenderedContent(rowSetting.contentType, mainform.getID());
        
        if (rowSetting.displayPattern == BLINKING) {
            if (isTextShowing) {
                if(row == TOP_ROW) {
//                    mainform.topTextField.setText("");
                    if (mainform == null) {
                        System.out.println("mainform is null");
                    } else {
                        if (mainform.topTextField == null) {
                            System.out.println("mainform.topTextField is null");
                        } else {
                            mainform.topTextField.setText("");
                        }
                    }                    
                } else {
//                    mainform.botTextField.setText("");
                    if (mainform == null) {
                        System.out.println("mainform is null");
                    } else {
                        if (mainform.botTextField == null) {
                            System.out.println("mainform.botTextField is null");
                        } else {
                            mainform.botTextField.setText("");
                        }
                    }
                }
            } else {
                if(row == TOP_ROW)
                    mainform.topTextField.setText(renderedContent);
                else
                    mainform.botTextField.setText(renderedContent);
            }
            isTextShowing = ! isTextShowing;
        } else {
            textWidth = (int)(eBoardTextField.getFont().getStringBounds(renderedContent, frc).getWidth());   
            if(row == TOP_ROW)
                mainform.topTextField.setText(renderedContent);
            else
                mainform.botTextField.setText(renderedContent);
            
            InnerCycleTask innerCycleTask = new InnerCycleTask(mainform, row, rowSetting, textWidth, flowDelta, 
                    row == TOP_ROW ? mainform.topTextField : mainform.botTextField);
            mainform.parking_Display_InnerTimer[row.ordinal()].reschedule(innerCycleTask, 0, EBD_PERIOD);
        }
    }
}