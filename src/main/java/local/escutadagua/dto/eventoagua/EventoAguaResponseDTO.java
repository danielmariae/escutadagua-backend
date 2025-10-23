package local.escutadagua.dto.eventoagua;

import java.time.LocalDateTime;


public record EventoAguaResponseDTO(
    Long id,
    Long idUsuario,
    LocalDateTime dataHoraEvento,
    String tipoEvento,
    Double gastoAgua, // em litros 
    Double duracaoEvento // em minutos
) {
    public static EventoAguaResponseDTO valueOf(local.escutadagua.model.EventoAgua e){
        return new EventoAguaResponseDTO(
            e.getId(),
            e.getUsuario().getId(),
            e.getDataHoraEvento(),
            e.getTipoEvento().toString(),
            e.getGastoAgua(),
            e.getDuracaoEvento()
        );
    }
}
