package local.escutadagua.dto.alerta;

import java.time.LocalDateTime;

public record AlertaDTO(
    Long idUsuario,
    String tipoAlerta,
    String titulo,
    String mensagem,
    LocalDateTime notificadoEm,
    Boolean lido
) {
    
}
