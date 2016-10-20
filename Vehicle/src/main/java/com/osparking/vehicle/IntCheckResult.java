/*
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
package com.osparking.vehicle;

/**
 *
 * @author Open Source Parking, Inc.(www.osparking.com)
 */
public class IntCheckResult {
    private boolean badFormat = false;
    private String abortMsg = null;
    Integer value;
    IntCheckResult(boolean isFormatBad, String abortMsg, Integer value) {
        this.badFormat = isFormatBad;
        this.abortMsg = abortMsg;
        this.value = value;
    }

    /**
     * @return the badFormat
     */
    public boolean isBadFormat() {
        return badFormat;
    }

    /**
     * @return the abortMsg
     */
    public String getAbortMsg() {
        return abortMsg;
    }
}
