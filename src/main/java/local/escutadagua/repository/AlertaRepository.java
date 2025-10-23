package local.escutadagua.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import local.escutadagua.model.Alerta;

@ApplicationScoped
public class AlertaRepository implements PanacheRepository<Alerta> {
    
}
