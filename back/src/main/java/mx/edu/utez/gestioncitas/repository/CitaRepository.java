package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
}
