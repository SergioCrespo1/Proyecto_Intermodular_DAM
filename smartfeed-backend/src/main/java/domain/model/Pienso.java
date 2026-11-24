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

@Entity
@Table(name = "PIENSO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Pienso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pienso")
    private Integer idPienso;

    @NotBlank
    @Column(name = "nombre_comercial",  nullable = false, length = 100)
    private String nombreComercial;

    @NotBlank
    @Column(name = "fabricante", nullable = false, length = 100)
    private String fabricante;

    @NotNull
    @DecimalMin(value = "0,1")
    @Column(name = "tamano_pellet_mm", nullable = false, precision = 3, scale = 1)
    private BigDecimal tamañoPelletMn;

    @NotNull
    @DecimalMin(value = "0,0")
    @Column(name = "peso_pez_min_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoPezMinG;

    @NotNull
    @DecimalMin(value = "0,0")
    @Column(name = "peso_pez_max_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoPezMaxG;

    @NotNull
    @DecimalMin(value = "1.0")
    @Column(name = "peso_saco_kg", nullable = false, precision = 4, scale = 1)
    private BigDecimal pesoSacoKg = new BigDecimal("25.0");

    @NotNull
    @Column(name = "es medicado", nullable = false)
    private Boolean esMedicado = false;

    @Column(name = "medicamento_activo", length = 100)
    private String medicamentoActivo;
}