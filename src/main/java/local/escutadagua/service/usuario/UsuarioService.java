package local.escutadagua.service.usuario;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import local.escutadagua.dto.usuario.UsuarioDTO;
import local.escutadagua.dto.usuario.UsuarioResponseDTO;
import local.escutadagua.model.Usuario;
import local.escutadagua.repository.UsuarioRepository;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    HashService hash;

    public UsuarioResponseDTO autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        String senhaVerificada = hash.getHashSenha(senha);
        if (usuario != null && senhaVerificada.equals(usuario.getSenha())) {
            return UsuarioResponseDTO.valueOf(usuario);
        }
        return null;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(usuarioDTO.nomeCompleto());
        usuario.setEmail(usuarioDTO.email());
        usuario.setSenha(hash.getHashSenha(usuarioDTO.senha()));
        
        usuario.persist();
        return UsuarioResponseDTO.valueOf(usuario);
    }

    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        return usuario != null ? UsuarioResponseDTO.valueOf(usuario) : null;
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        return usuario != null ? UsuarioResponseDTO.valueOf(usuario) : null;
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        usuario.setNomeCompleto(usuarioDTO.nomeCompleto());
        usuario.setEmail(usuarioDTO.email());
        if (usuarioDTO.senha() != null && !usuarioDTO.senha().isEmpty()) {
            usuario.setSenha(hash.getHashSenha(usuarioDTO.senha()));
        }
        usuario.persist();
        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario.deleteById(id);
    }
}