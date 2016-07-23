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
package com.osparking.statistics;

import static com.osparking.global.Globals.language;
import static com.osparking.global.names.ControlEnums.LabelContent.ATTENDANT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.BAR_OP_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.GATE_NAME_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REGISTERED_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_BUILDING;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_PURPOSE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.VISIT_UNIT;
import com.osparking.global.names.ControlEnums.Languages;
import static com.osparking.global.names.ControlEnums.Languages.ENGLISH;
import static com.osparking.global.names.ControlEnums.Languages.KOREAN;
import static com.osparking.global.names.ControlEnums.TableTypes.HIGHER_HEADER;
import static com.osparking.global.names.ControlEnums.TableTypes.LOWER_HEADER;

/**
 *Extra (=additionally detailed) information on each car arrival.
 * @author Open Source Parking Inc.
 */
public enum CAExtra {
    Gate(GATE_NAME_LABEL.getContent(), GATE_NAME_LABEL.getContent()),
    BarOptn(BAR_OP_LABEL.getContent(), BAR_OP_LABEL.getContent()),
    Attendant(ATTENDANT_LABEL.getContent(), ATTENDANT_LABEL.getContent()),
    RegTag(REGISTERED_LABEL.getContent(), REGISTERED_LABEL.getContent()),
    BldgNum(VISIT_BUILDING.getContent(), VISIT_BUILDING.getContent()),
    RoomNum(VISIT_UNIT.getContent(), VISIT_UNIT.getContent()),
    L1name(HIGHER_HEADER.getContent(), HIGHER_HEADER.getContent()),
    L2name(LOWER_HEADER.getContent(), LOWER_HEADER.getContent()),
    VReason(VISIT_PURPOSE_LABEL.getContent(), VISIT_PURPOSE_LABEL.getContent());
    CAExtra(String korean, String english) {
        contents[KOREAN.ordinal()] = korean;
        contents[ENGLISH.ordinal()] = english;
    }

    private String[] contents = new String[Languages.values().length];

    public String getContent() {
        return contents[language.ordinal()];
    }    
}
