package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "LOTE_PIENSO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LotePienso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote_pienso")
    private Integer idLotePienso;

    @NotBlank
    @Column(name = "codigo_lote_fabrica", nullable = false, unique = true, length = 50)
    private String codigoLoteFabrica;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pienso", nullable = false)
    private Pienso pienso;

    @NotNull
    @Column(name = "fecha_fabricacion", nullable = false)
    private LocalDate fechaFabricacion;

    @NotNull
    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    @NotNull
    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "sacos_recibidos", nullable = false, precision = 7, scale = 2)
    private BigDecimal sacosRecibidos;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "sacos_disponibles", nullable = false, precision = 7, scale = 2)
    private BigDecimal sacosDisponibles;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_lote", nullable = false)
    private EstadoLote estadoLote = EstadoLote.ACTIVO;

    public enum EstadoLote {
        ACTIVO,
        AGOTADO,
        RETIRADO
    }
}
