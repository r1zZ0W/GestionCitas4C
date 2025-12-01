package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Cita.
 * Proporciona métodos CRUD para gestionar las citas en la base de datos.
 * @author Tilines Crew
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
}
