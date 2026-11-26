package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "LOTE_PECES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotePeces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote_peces")
    private Integer idLotePeces;

    @NotBlank
    @Column(name = "codigo_lote", nullable = false, unique = true, length = 50)
    private String codigoLote;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", nullable = false)
    private Especie especie;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jaula_actual", nullable = false)
    private Jaula jaulaActual;

    @NotNull
    @Column(name = "fecha_siembra", nullable = false)
    private LocalDate fechaSiembra;

    @NotNull
    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @NotNull
    @Min(0)
    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @NotNull
    @Column(name = "peso_medio_inicial_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoMedioInicialG;

    @NotNull
    @Column(name = "peso_medio_actual_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoMedioActualG;

    @Column(name = "fecha_ultimo_muestreo")
    private LocalDate fechaUltimoMuestreo;

    @NotNull
    @Column(name = "biomasa_actual_kg", nullable = false, precision = 9, scale = 2)
    private BigDecimal biomasaActualKg;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_lote", nullable = false)
    private EstadoLote estadoLote = EstadoLote.EN_ENGORDE;

    public enum EstadoLote {
        EN_ENGORDE,
        COSECHADO,
        FUSIONADO,
        TRASLADADO
    }
}