package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "apaf.cartera.importacion")
public class CarteraImportacionProperties {
    private boolean enabled;
    private ModoImportacion mode;
    private String directory;
    private String reportDirectory;
    private String defaultCharset;
    private int batchSize;
    private boolean stopOnError;
    private List<String> selectedPeriods;
    private List<ArchivoCarteraConfig> files;
}
