package local.escutadagua.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import local.escutadagua.model.EventoAgua;

@ApplicationScoped
public class EventoAguaRepository implements PanacheRepository<EventoAgua> {
    
}
