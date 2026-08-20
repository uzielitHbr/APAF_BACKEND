package app.apaf.backend.features.cartera_management.importacionhistorica.events;

import org.springframework.context.ApplicationEvent;
import java.time.YearMonth;

public class CarteraImportadaEvent extends ApplicationEvent {
    private final YearMonth periodo;

    public CarteraImportadaEvent(Object source, YearMonth periodo) {
        super(source);
        this.periodo = periodo;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }
}
