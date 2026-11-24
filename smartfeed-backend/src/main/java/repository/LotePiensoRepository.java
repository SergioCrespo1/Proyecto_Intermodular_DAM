package repository;

import domain.model.LotePienso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotePiensoRepository extends JpaRepository<LotePienso, Integer> {

    // Buscar por el código de fábrica (único)
    Optional<LotePienso> findByCodigoLoteFabrica(String codigoLoteFabrica);

    /**
     * Consulta FEFO (First Expired, First Out):
     * Obtiene los lotes activos de un pienso concreto con sacos disponibles,
     * ordenados por fecha de caducidad ascendente (el más proximo a caducar se consume primero).
     */
    @Query("SELECT l FROM LotePienso l " +
            "WHERE l.pienso.idPienso = :idPienso " +
            "AND l.estadoLote = :estado " +
            "AND l.sacosDisponibles > :minSacos " +
            "ORDER BY l.fechaCaducidad ASC")
    List<LotePienso> findLotesDisponiblesFEFO(
            @Param("idPienso") Integer idPienso,
            @Param("estado") LotePienso.EstadoLote estado,
            @Param("minSacos") BigDecimal minSacos
    );
}
