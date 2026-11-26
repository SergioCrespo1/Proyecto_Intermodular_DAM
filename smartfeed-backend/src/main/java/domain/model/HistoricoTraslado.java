package domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORICO_TRASLADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoTraslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_traslado")
    private Integer idTraslado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote_peces", nullable = false)
    private LotePeces lotePeces;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jaula_origen", nullable = false)
    private Jaula jaulaOrigen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jaula_destino", nullable = false)
    private Jaula jaulaDestino;

    @NotNull
    @Min(0)
    @Column(name = "cantidad_peces_trasladados", nullable = false)
    private Integer cantidadPecesTrasladados;

    @NotNull
    @Column(name = "fecha_hora_traslado", nullable = false)
    private LocalDateTime fechaHoraTraslado;

    @NotBlank
    @Column(name = "motivo", nullable = false, length = 150)
    private String motivo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}