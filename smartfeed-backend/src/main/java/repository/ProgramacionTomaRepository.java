package repository;

import domain.model.EstadoOrden;
import domain.model.LotePeces;
import domain.model.ProgramacionToma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgramacionTomaRepository extends JpaRepository<ProgramacionToma, Integer> {

    /**
     * Consulta principal para la app móvil.
     * Recupera las tomas de hoy pendientes de suministro.
     */
    List<ProgramacionToma> findByFechaProgramadaAndEstadoOrden(LocalDate fechaProgramada, EstadoOrden estadoOrden);

    /**
     * Recupera las órdenes de alimentación asignadas a un lote en una fecha concreta.
     */
    List<ProgramacionToma> findByLotePecesAndFechaProgramada(LotePeces lotePeces, LocalDate fechaProgramada);
}
