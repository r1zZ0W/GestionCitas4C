package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {
}
