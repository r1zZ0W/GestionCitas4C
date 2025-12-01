package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Medico.
 * Proporciona métodos CRUD para gestionar los médicos en la base de datos.
 * @author Tilines Crew
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
}
