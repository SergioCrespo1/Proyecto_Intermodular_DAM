package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGISTRO_TEMPERATURA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTemperatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_temperatura")
    private Integer idTemperatura;

    @NotNull
    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "40.0")
    @Column(name = "temperatura_c", nullable = false, precision = 4, scale = 2)
    private BigDecimal temperaturaC;

    @NotNull
    @Column(name = "fecha_hora_registro", nullable = false)
    private LocalDateTime fechaHoraRegistro;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}