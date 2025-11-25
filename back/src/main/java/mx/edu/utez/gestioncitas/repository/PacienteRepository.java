package mx.edu.utez.gestioncitas.repository;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Paciente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Paciente en el que se definen métodos personalizados ocupando JPA
 */
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    /**
     * Busca pacientes por prioridad
     *
     * @param prioridad La prioridad del paciente
     * @return Una lista simple de pacientes con la prioridad especificada
     */
    ListaSimple<Paciente> findByPrioridad(Integer prioridad);

    /**
     * Busca todos los pacientes ordenados por prioridad ascendente
     *
     * @return Una lista simple de pacientes ordenados por prioridad ascendente
     */
    ListaSimple<Paciente> findAllByOrderByPrioridadAsc();

    /**
     * Busca pacientes por nombre o apellido utilizando una consulta personalizada
     *
     * @param termino El término de búsqueda para el nombre o apellido
     * @return Una lista simple de pacientes que coinciden con el término de búsqueda
     */
    @Query("SELECT p FROM Paciente p" +
            " WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR" +
            " LOWER(p.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    ListaSimple<Paciente> buscarPorNombreOApellido(@Param("termino") String termino);

}
