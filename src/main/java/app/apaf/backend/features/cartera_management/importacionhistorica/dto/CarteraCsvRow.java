package app.apaf.backend.features.cartera_management.importacionhistorica.dto;

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
