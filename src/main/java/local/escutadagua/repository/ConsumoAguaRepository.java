package local.escutadagua.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import local.escutadagua.model.ConsumoAgua;

@ApplicationScoped
public class ConsumoAguaRepository implements PanacheRepository<ConsumoAgua> {

    /**
     * Busca o registro de ConsumoAgua para um usuário em um dia específico.
     * @param idUsuario ID do usuário
     * @param diaConsumo Data a ser buscada
     * @return ConsumoAgua ou null
     */
    public ConsumoAgua findByUsuarioAndDia(Long idUsuario, LocalDate diaConsumo) {
        // O ConsumoAgua.diaConsumo é um LocalDateTime (início do dia: 00:00:00)
        LocalDateTime startOfDay = diaConsumo.atStartOfDay();
        
        return find("usuario.id = ?1 and diaConsumo = ?2", idUsuario, startOfDay)
                .firstResult();
    }
}