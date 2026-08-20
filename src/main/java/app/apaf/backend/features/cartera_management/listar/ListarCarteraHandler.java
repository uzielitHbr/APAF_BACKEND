package app.apaf.backend.features.cartera_management.listar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarCarteraHandler {
    
    private final ListarCarteraReadRepository repository;

    public PaginatedResponse<CarteraPreviewResponse> handle(int page, int size, String mesCorte, String searchTerm, String sucursal, String producto) {
        return repository.listar(page, size, mesCorte, searchTerm, sucursal, producto);
    }
}
