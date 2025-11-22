package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
}
