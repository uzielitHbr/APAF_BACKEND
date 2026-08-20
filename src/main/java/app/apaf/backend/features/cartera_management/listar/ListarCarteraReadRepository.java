package app.apaf.backend.features.cartera_management.listar;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ListarCarteraReadRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaginatedResponse<CarteraPreviewResponse> listar(int page, int size, String mesCorte, String searchTerm, String sucursal, String productoCredito) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        String mesFiltro = mesCorte;
        if (mesFiltro == null || mesFiltro.isBlank()) {
            mesFiltro = jdbcTemplate.queryForObject("SELECT TO_CHAR(MAX(mes_corte), 'YYYY-MM') FROM cartera_datos", params, String.class);
        }
        
        StringBuilder sql = new StringBuilder("FROM cartera_datos WHERE 1=1 ");
        
        if (mesFiltro != null) {
            sql.append("AND TO_CHAR(mes_corte, 'YYYY-MM') = :mesCorte ");
            params.addValue("mesCorte", mesFiltro);
        }
        
        if (searchTerm != null && !searchTerm.isBlank()) {
            sql.append("AND (LOWER(nombre_acreditado) LIKE :search OR LOWER(numero_socio) LIKE :search OR LOWER(numero_contrato) LIKE :search) ");
            params.addValue("search", "%" + searchTerm.toLowerCase() + "%");
        }
        
        if (sucursal != null && !sucursal.isBlank()) {
            sql.append("AND sucursal = :sucursal ");
            params.addValue("sucursal", sucursal);
        }
        
        if (productoCredito != null && !productoCredito.isBlank()) {
            sql.append("AND LOWER(producto_credito) = LOWER(:productoCredito) ");
            params.addValue("productoCredito", productoCredito);
        }
        
        String countSql = "SELECT COUNT(*) " + sql.toString();
        long totalElements = jdbcTemplate.queryForObject(countSql, params, Long.class);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        int offset = (page - 1) * size;
        String selectSql = "SELECT id_analisis_mensual, nombre_acreditado, numero_socio, numero_contrato, sucursal, producto_credito, capital_vigente " +
                           sql.toString() +
                           "ORDER BY numero_contrato ASC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", offset);
        
        List<CarteraPreviewResponse> content = jdbcTemplate.query(selectSql, params, (rs, rowNum) -> new CarteraPreviewResponse(
                UUID.fromString(rs.getString("id_analisis_mensual")),
                rs.getString("nombre_acreditado"),
                rs.getString("numero_socio"),
                rs.getString("numero_contrato"),
                rs.getString("sucursal"),
                rs.getString("producto_credito"),
                rs.getBigDecimal("capital_vigente")
        ));
        
        return new PaginatedResponse<>(content, page, size, totalElements, totalPages);
    }
}
