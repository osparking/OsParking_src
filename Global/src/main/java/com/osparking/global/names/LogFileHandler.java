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
package com.osparking.global.names;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 *
 * @author Open Source Parking Inc.
 */
public class LogFileHandler extends FileHandler {
    String _LogFilePath;
    
    public LogFileHandler(String FilePath, boolean isAppend) throws IOException {
        super(FilePath, isAppend);
        _LogFilePath = FilePath;
    }
    
    public String getLogFilePath() {
        return _LogFilePath;
    }
}
