package local.escutadagua.dto.consumoagua;

import java.time.LocalDateTime;
import java.util.List;

import local.escutadagua.dto.eventoagua.EventoAguaResponseDTO;
import local.escutadagua.model.ConsumoAgua;

public record ConsumoAguaResponseDTO(
    Long id,    
    Long idUsuario,
    LocalDateTime diaConsumo,
    Double totalConsumo, // Em litros
    List<EventoAguaResponseDTO> eventosDia
) {
    public static ConsumoAguaResponseDTO valueOf(ConsumoAgua c){
        List<EventoAguaResponseDTO> eventosDTO = c.getEventosDia().stream()
            .map(e -> EventoAguaResponseDTO.valueOf(e))
            .toList();

        return new ConsumoAguaResponseDTO(
            c.getId(),
            c.getUsuario().getId(),
            c.getDiaConsumo(),
            c.getTotalConsumo(),
            eventosDTO
        );
    }
}
