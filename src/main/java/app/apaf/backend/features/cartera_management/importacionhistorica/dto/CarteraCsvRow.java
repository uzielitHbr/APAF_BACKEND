package app.apaf.backend.features.cartera_management.importacionhistorica.dto;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarteraCsvRow {
    private final int lineNumber;
    private final List<String> columns;

    public String getColumn(int index) {
        if (index >= 0 && index < columns.size()) {
            String val = columns.get(index);
            return val != null ? val.trim() : null;
        }
        return null;
    }
    
    public String getRawColumn(int index) {
         if (index >= 0 && index < columns.size()) {
             return columns.get(index);
         }
         return null;
    }
}
