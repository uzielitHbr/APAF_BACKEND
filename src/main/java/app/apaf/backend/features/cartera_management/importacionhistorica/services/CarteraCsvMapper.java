package app.apaf.backend.features.cartera_management.importacionhistorica.services;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import app.apaf.backend.domain.cartera.entity.CarteraDatos;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarteraCsvMapper {

    private final CsvValueParser valueParser;

    public CarteraDatos map(CarteraCsvRow row, YearMonth periodo, UUID idImportacion) {
        int ln = row.getLineNumber();
        LocalDate mesCorte = periodo.atDay(1);
        LocalDate fechaCorte = periodo.atEndOfMonth();

        CarteraDatos entity = new CarteraDatos();
        entity.setMesCorte(mesCorte);
        entity.setFechaCorte(fechaCorte);
        entity.setIdImportacion(idImportacion);
        
        entity.setNombreAcreditado(row.getColumn(0));
        entity.setNumeroSocio(row.getColumn(1));
        entity.setNumeroContrato(row.getColumn(2));
        entity.setSucursal(row.getColumn(3));
        entity.setClasificacionCredito(row.getColumn(4));
        entity.setProductoCredito(row.getColumn(5));
        entity.setModalidadPago(row.getColumn(6));
        entity.setFechaOtorgamiento(valueParser.parseLocalDate(row.getColumn(7), "fechaOtorgamiento", ln));
        entity.setMontoOriginal(valueParser.parseBigDecimal(row.getColumn(8), "montoOriginal", ln));
        entity.setFechaVencimiento(valueParser.parseLocalDate(row.getColumn(9), "fechaVencimiento", ln));
        entity.setTasaOrdinariaNominalAnual(valueParser.parseBigDecimal(row.getColumn(10), "tasaOrdinariaNominalAnual", ln));
        entity.setTasaMoratoriaNominalAnual(valueParser.parseBigDecimal(row.getColumn(11), "tasaMoratoriaNominalAnual", ln));
        entity.setPlazoCreditoMeses(valueParser.parseInteger(row.getColumn(12), "plazoCreditoMeses", ln));
        entity.setFrecuenciaPagoCapital(row.getColumn(13));
        entity.setFrecuenciaPagoIntereses(row.getColumn(14));
        entity.setDiasMora(valueParser.parseInteger(row.getColumn(15), "diasMora", ln));
        entity.setCapitalVigente(valueParser.parseBigDecimal(row.getColumn(16), "capitalVigente", ln));
        entity.setCapitalVencido(valueParser.parseBigDecimal(row.getColumn(17), "capitalVencido", ln));
        entity.setIntDevNoCobradosVigentes(valueParser.parseBigDecimal(row.getColumn(18), "intDevNoCobradosVigentes", ln));
        entity.setIntDevNoCobradosVencidos(valueParser.parseBigDecimal(row.getColumn(19), "intDevNoCobradosVencidos", ln));
        entity.setIntDevNoCobradosCtasOrden(valueParser.parseBigDecimal(row.getColumn(20), "intDevNoCobradosCtasOrden", ln));
        entity.setFechaUltimoPagoCapital(valueParser.parseLocalDate(row.getColumn(21), "fechaUltimoPagoCapital", ln));
        entity.setMontoUltimoPagoCapital(valueParser.parseBigDecimal(row.getColumn(22), "montoUltimoPagoCapital", ln));
        entity.setFechaUltimoPagoIntereses(valueParser.parseLocalDate(row.getColumn(23), "fechaUltimoPagoIntereses", ln));
        entity.setMontoUltimoPagoIntereses(valueParser.parseBigDecimal(row.getColumn(24), "montoUltimoPagoIntereses", ln));
        entity.setRenovadoReestructuradoNormal(row.getColumn(25));
        entity.setEmproblemado(row.getColumn(26));
        entity.setVigenteOVencido(row.getColumn(27));
        entity.setCargoAcreditadoParteRelacionada(row.getColumn(28));
        entity.setMontoGarantiaLiquida(valueParser.parseBigDecimal(row.getColumn(29), "montoGarantiaLiquida", ln));
        entity.setCuentaGarantiaLiquida(row.getColumn(30));
        entity.setMontoGarantiaPrendaria(valueParser.parseBigDecimal(row.getColumn(31), "montoGarantiaPrendaria", ln));
        entity.setMontoGarantiaHipotecaria(valueParser.parseBigDecimal(row.getColumn(32), "montoGarantiaHipotecaria", ln));
        entity.setEprcContableParteCubierta(valueParser.parseBigDecimal(row.getColumn(33), "eprcContableParteCubierta", ln));
        entity.setEprcContableParteExpuesta(valueParser.parseBigDecimal(row.getColumn(34), "eprcContableParteExpuesta", ln));
        entity.setEprcContableXInteresesCee(valueParser.parseBigDecimal(row.getColumn(35), "eprcContableXInteresesCee", ln));
        entity.setImporteEstimacionAdicional(valueParser.parseBigDecimal(row.getColumn(36), "importeEstimacionAdicional", ln));
        entity.setLocalidad(row.getColumn(37));
        entity.setEstado(row.getColumn(38));
        entity.setOcupacion(row.getColumn(39));
        entity.setMunicipio(row.getColumn(40));
        entity.setGenero(row.getColumn(41));
        entity.setFechaNacimiento(valueParser.parseLocalDate(row.getColumn(42), "fechaNacimiento", ln));
        entity.setEdad(valueParser.parseShort(row.getColumn(43), "edad", ln));
        entity.setTipoCarteraCalificacion(row.getColumn(44));
        entity.setFinalidadCredito(row.getColumn(45));
        entity.setCce(row.getColumn(46));

        return entity;
    }
}
