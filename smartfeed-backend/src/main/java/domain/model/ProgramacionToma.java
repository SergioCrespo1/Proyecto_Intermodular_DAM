package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PROGRAMACION_TOMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProgramacionToma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programacion")
    private Integer idProgramacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote_peces", nullable = false)
    private LotePeces lotePeces;

    @NotNull
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @NotNull
    @Min(value = 1)
    @Column(name = "numero_toma", nullable = false)
    private Integer numeroToma = 1;

    @NotNull
    @Min(value = 1)
    @Column(name = "total_tomas_dia", nullable = false)
    private Integer totalTomasDia = 1;

    // Auditoría zootécnica: congelan los valores del cálculo matemático
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "biomasa_calculo_kg", nullable = false, precision = 9, scale = 2)
    private BigDecimal biomasaCalculoKg;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "peso_medio_calculo_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoMedioCalculoG;

    @NotNull
    @Column(name = "temperatura_calculo_c", nullable = false, precision = 4, scale = 2)
    private BigDecimal temperaturaCalculoC;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "tasa_sfr_porc", nullable = false, precision = 4, scale = 2)
    private BigDecimal tasaSfrPorc;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "racion_teorica_kg", nullable = false, precision = 7, scale = 2)
    private BigDecimal racionTeoricaKg;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "sacos_programados", nullable = false, precision = 5, scale = 2)
    private BigDecimal sacosProgramados;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_orden", nullable = false, length = 20)
    private EstadoOrden estadoOrden = EstadoOrden.PENDIENTE;

    // Constructor de conveniencia adaptado con el objeto LotePeces real
    public ProgramacionToma(
            LotePeces lotePeces,
            LocalDate fechaProgramada,
            Integer numeroToma,
            Integer totalTomasDia,
            BigDecimal biomasaCalculoKg,
            BigDecimal pesoMedioCalculoG,
            BigDecimal temperaturaCalculoC,
            BigDecimal tasaSfrPorc,
            BigDecimal racionTeoricaKg,
            BigDecimal sacosProgramados) {

        this.lotePeces = lotePeces;
        this.fechaProgramada = fechaProgramada;
        this.numeroToma = numeroToma;
        this.totalTomasDia = totalTomasDia;
        this.biomasaCalculoKg = biomasaCalculoKg;
        this.pesoMedioCalculoG = pesoMedioCalculoG;
        this.temperaturaCalculoC = temperaturaCalculoC;
        this.tasaSfrPorc = tasaSfrPorc;
        this.racionTeoricaKg = racionTeoricaKg;
        this.sacosProgramados = sacosProgramados;
        this.estadoOrden = EstadoOrden.PENDIENTE;
    }
}
