package local.escutadagua.service.usuario;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import local.escutadagua.dto.usuario.UsuarioResponseDTO;

@ApplicationScoped
public class JwtService {
    private static final Duration EXPIRATION_TIME = Duration.ofHours(24);

    public String generateJwt(UsuarioResponseDTO dto){
        Instant now = Instant.now();
            Instant expiryDate = now.plus(EXPIRATION_TIME);
    
            Set<String> roles = new HashSet<>();
            roles.add("User");
    
            return Jwt.issuer("banco-de-horas-jwt")
            .subject(dto.email())
            .groups(roles)
            .expiresAt(expiryDate)
            .sign();
        }    
}

