package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.time.YearMonth;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchivoCarteraConfig {
    private YearMonth period;
    private String name;
}
