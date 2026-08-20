package app.apaf.backend.features.cartera_management.totales;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import app.apaf.backend.core.security.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@WebMvcTest(controllers = ObtenerTotalesCarteraController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@org.springframework.test.context.ActiveProfiles("test")
class ObtenerTotalesCarteraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObtenerTotalesCarteraHandler handler;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private app.apaf.backend.domain.users.repository.UserRepository userRepository;

    @MockBean
    private app.apaf.backend.domain.users.repository.RoleRepository roleRepository;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void debeResolverLaRutaTotalesCorrectamente() throws Exception {
        CarteraTotalesResponse response = new CarteraTotalesResponse(
                LocalDate.of(2026, 3, 31),
                new ResumenGeneralResponse(BigDecimal.TEN, BigDecimal.TEN, 10L, 10L, 0L),
                new SaldosDevengadosResponse(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN),
                new FlujosRecuperacionResponse(BigDecimal.TEN, BigDecimal.TEN),
                new RiesgoYRegulatorioResponse(10L, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN)
        );

        when(handler.handle("2026-03")).thenReturn(response);

        mockMvc.perform(get("/api/v1/cartera/totales")
                        .param("mesCorte", "2026-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaCorte").value("2026-03-31"));
    }
}
