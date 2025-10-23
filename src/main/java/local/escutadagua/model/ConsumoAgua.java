package local.escutadagua.model;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.JoinColumn;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class ConsumoAgua extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime diaConsumo;

    @Column(nullable = false)
    private Double totalConsumo; // em litros

    @OneToMany()
    @JoinTable(
        name = "consumo_dia_eventos",
        joinColumns = @JoinColumn(name = "consumo_id"),
        inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    private List<EventoAgua> eventosDia;
}
