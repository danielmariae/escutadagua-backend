package local.escutadagua.dto.alerta;

import java.time.LocalDateTime;

import local.escutadagua.model.Alerta;

public record AlertaResponseDTO(
    Long id,
    Long idUsuario,
    String tipoAlerta,
    String titulo,
    String mensagem,
    LocalDateTime notificadoEm,
    Boolean lido
) {
    public static AlertaResponseDTO valueOf(Alerta a){
        return new AlertaResponseDTO(
            a.getId(),
            a.getUsuario().getId(),
            a.getTipoAlerta().toString(),
            a.getTitulo(),
            a.getMensagem(),
            a.getNotificadoEm(),
            a.getLido()
        );
    }
}
