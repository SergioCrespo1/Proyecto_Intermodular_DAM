package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "LINEA_CONSUMO_PIENSO",
        indexes = {
                @Index(name = "idx_linea_consumo_lote", columnList = "id_lote_pienso")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LineaConsumoPienso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea_consumo")
    private Integer idLineaConsumo;

    // Relación con la cabecera (REGISTRO_CONSUMO)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_consumo", nullable = false)
    private RegistroConsumo registroConsumo;

    // Relación con el lote físico del que se descuenta el stock (LOTE_PIENSO)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote_pienso", nullable = false)
    private LotePienso lotePienso;

    // Cantidad consumida (permite fracciones de sacos como 0.50)
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "sacos_gastados", nullable = false, precision = 5, scale = 2)
    private BigDecimal sacosGastados;
}

