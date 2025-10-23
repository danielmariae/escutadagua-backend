package local.escutadagua.dto.usuario;

public record UsuarioDTO(
    String nomeCompleto,
    String email,
    String senha,
    Double metaDiaria
) {
    
}
