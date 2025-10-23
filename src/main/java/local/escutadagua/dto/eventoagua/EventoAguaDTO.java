package local.escutadagua.dto.eventoagua;

import java.time.LocalDateTime;


public record EventoAguaDTO(
    Long idUsuario,
    LocalDateTime dataHoraEvento,
    String tipoEvento,
    Double gastoAgua, // em litros 
    Double duracaoEvento // em minutos
) {
    
}
