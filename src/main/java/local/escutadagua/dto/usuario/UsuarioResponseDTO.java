package local.escutadagua.dto.usuario;

import local.escutadagua.model.Usuario;

public record UsuarioResponseDTO(
    Long id,
    String nomeCompleto,
    String email,
    Double metaDiaria
    ) {
    public static UsuarioResponseDTO valueOf(Usuario u){
        return new UsuarioResponseDTO(u.getId(),
                                      u.getNomeCompleto(),
                                      u.getEmail(),
                                      u.getMetaDiaria());
    }
}
