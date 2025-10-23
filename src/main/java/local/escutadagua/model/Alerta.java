package local.escutadagua.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import local.escutadagua.model.enums.TipoAlerta;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class Alerta extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Usuario usuario;

    @Enumerated(EnumType.STRING)
    TipoAlerta tipoAlerta;

    @Column(nullable = false)
    String titulo;

    @Column(nullable = false)
    String mensagem;
    
    LocalDateTime notificadoEm;
    Boolean lido;
}
