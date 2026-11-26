package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "JAULA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jaula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jaula")
    private Integer idJaula;

    @NotBlank
    @Column(name = "codigo_jaula", nullable = false, unique = true, length = 20)
    private String codigoJaula;

    @NotNull
    @Column(name = "diametro_m", nullable = false, precision = 4, scale = 2)
    private BigDecimal diametroM;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_operativo", nullable = false)
    private EstadoOperativo estadoOperativo = EstadoOperativo.VACIA;

    public enum EstadoOperativo {
        ACTIVA,
        VACIA,
        MANTENIMIENTO
    }
}