package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ESPECIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Especie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEspecie;

    @NotBlank
    @Column(name = "nombre_comun", nullable = false, length = 50)
    private String nombreComun;

    @NotBlank
    @Column(name = "nombre_cientifico", nullable = false, length = 100)
    private String nombreCientifico;

    @NotNull
    @DecimalMin(value = "0.50")
    @DecimalMax(value = "5.00")
    @Column(name = "fcr_referencia_medio", nullable = false, precision = 3, scale = 2)
    private BigDecimal fcrReferenciaMedio;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "temp_min_operativa", nullable = false, precision = 4, scale = 2)
    private BigDecimal tempMinOperativa;

    @NotNull
    @DecimalMax(value = "40.00")
    @Column(name = "temp_max_operativa", nullable = false, precision = 4, scale = 2)
    private BigDecimal tempMaxOperativa;

    // Constructor de conveniencia sin ID (para inserciones y tests)
    public Especie(
            String nombreComun,
            String nombreCientifico,
            BigDecimal fcrReferenciaMedio,
            BigDecimal tempMinOperativa,
            BigDecimal tempMaxOperativa) {

        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.fcrReferenciaMedio = fcrReferenciaMedio;
        this.tempMinOperativa = tempMinOperativa;
        this.tempMaxOperativa = tempMaxOperativa;
    }

}
