package app.apaf.backend.features.cartera_management.calculados.proyecciones;

import java.math.BigDecimal;

public record ProyeccionesRecuperacionResponse(
    BigDecimal carteraTotal,
    BigDecimal recuperacionEnElMesCapital,
    BigDecimal recuperacionEnElMesIntereses,
    Short convAbonosADias,
    Integer abonosRestantesMes1,
    BigDecimal importeCapitalProyectadoMes1,
    BigDecimal interesDevengadoProyectadoMes1,
    Integer abonosRestantesMes2,
    BigDecimal importeCapitalProyectadoMes2,
    BigDecimal interesDevengadoProyectadoMes2,
    Integer abonosRestantesMes3,
    BigDecimal importeCapitalProyectadoMes3,
    BigDecimal interesDevengadoProyectadoMes3
) {}
