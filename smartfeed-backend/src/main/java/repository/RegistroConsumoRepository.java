package repository;

import domain.model.RegistroConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroConsumoRepository extends JpaRepository<RegistroConsumo, Integer>{

    // Comprobar si una programación ya fue ejecutada
    Optional<RegistroConsumo> findByIdProgramacion(Integer idProgramacion);


}
