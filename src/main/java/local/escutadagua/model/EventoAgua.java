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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import local.escutadagua.model.enums.TipoEvento;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class EventoAgua extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    LocalDateTime dataHoraEvento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TipoEvento tipoEvento;
    
    @Column(nullable = false)
    private Double gastoAgua; // em litros 
    
    @Column(nullable = false)
    private Double duracaoEvento; // em minutos
}
