package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REGISTRO_CONSUMO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RegistroConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_consumo")
    private Integer idRegistroConsumo;


    // Relación 1 a 1 con la programación de la toma (uq_consumo_programacion)
    @NotNull
    @Column(name = "id_programacion", nullable = false, unique = true)
    private Integer idProgramacion;

    /*
     * Nota: Si mapeas la entidad ProgramacionToma, se sustituye por:
     *
     * @OneToOne(fetch = FetchType.LAZY)
     * @JoinColumn(name = "id_programacion", nullable = false, unique = true)
     * private ProgramacionToma programacion;
     */

    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin;

    // Apetito / voracidad observada (CHECK entre 1 y 4)
    @NotNull
    @Min(1)
    @Max(4)
    @Column(name = "respuesta_alimentaria", nullable = false)
    // RM - Cambiado de Integer a Byte para coincidir con el tipo TINYINT de schema.sql en la validación de Hibernate
    private Byte respuestaAlimentaria;

    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    /* todo
     * Nota: Al mapear la entidad Usuario, se sustituye por:
     *
     * @ManyToOne(fetch = FetchType.LAZY)
     * @JoinColumn(name = "id_usuario", nullable = false)
     * private Usuario usuario;
     */

    // NULL mientras está en la tablet offline se rellena al sincronizar con el backend
    @Column(name = "fecha_hora_sincronizacion")
    private LocalDateTime fechaHoraSincronizacion;

    // Relación bidireccional maestro-detalle: un consumo tiene varias líneas de pienso
    @OneToMany(
            mappedBy = "registroConsumo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LineaConsumoPienso> detalles = new ArrayList<>();


    public void addLinea(LineaConsumoPienso linea) {
        detalles.add(linea);
        linea.setRegistroConsumo(this); }



}
