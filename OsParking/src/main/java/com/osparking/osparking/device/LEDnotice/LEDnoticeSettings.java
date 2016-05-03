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
package com.osparking.osparking.device.LEDnotice;

/**
 *
 * @author Open Source Parking Inc.
 */
public class LEDnoticeSettings {
    public boolean isUsed = true;
    public int contentTypeIdx = 0;
    public String verbatimContent = "";
    public int startEffectIdx = 0;
    public int pauseTimeIdx = 0;
    public int finishEffectIdx = 0;
    public int colorIdx = 0;
    public int fontIdx = 0;
  
    public LEDnoticeSettings
        (int used, int type, String content, int startEffect, int pauseTime, int finishEffect, int color, int font) 
    {
        isUsed= (used == 1 ? true : false);
        contentTypeIdx = type;
        verbatimContent = content;
        startEffectIdx = startEffect;
        pauseTimeIdx = pauseTime;
        finishEffectIdx = finishEffect;
        colorIdx = color;
        fontIdx = font;        
    }
}
