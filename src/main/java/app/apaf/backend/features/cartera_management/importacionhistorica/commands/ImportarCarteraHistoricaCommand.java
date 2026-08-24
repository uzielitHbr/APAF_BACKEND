package app.apaf.backend.features.cartera_management.importacionhistorica.commands;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.YearMonth;

public record ImportarCarteraHistoricaCommand(
                YearMonth mesCorte,
                Path archivo,
                String nombreArchivo,
                Charset charset,
                int batchSize,
                String idUsuarioCreacion) {
}
