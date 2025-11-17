package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Medico;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service // Servicio de médico el cual maneja la lógica de negocio
public class MedicoService {

    // Lista simple para almacenar los médicos
    ListaSimple<Medico> listaMedicos = new ListaSimple<>();
    private Integer nextId = 1; // Variable para manejar el siguiente ID disponible

    // Constructor que inicializa el servicio con un médico de ejemplo
    public MedicoService() {

        Medico m = new Medico();

        // Crear un médico de ejemplo
        m.setId(1);
        m.setNombre("Dr. Pedro");
        m.setApellido("Gomez");
        m.setEspecialidad("Medicina General");
        m.setNumeroConsultorio(101);

        // Agregar el médico de ejemplo a la lista
        listaMedicos.append(m);

    }

    // Método para obtener todos los médicos disponibles
    public Map<String, Object> getAll() {

        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("listMedicos", listaMedicos.toList());

        return mapResponse;

    }

    // Método para obtener un médico por su ID
    public Map<String, Object> getById(Integer id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Medico medico = listaMedicos.findById(id, Medico::getId);

        if (medico == null) {
            mapResponse.put("error", "No se pudo encontrar el paciente");
            return mapResponse;
        }

        mapResponse.put("medico", medico);
        return mapResponse;
    }

    // Método para crear un nuevo médico
    public Map<String, Object> create(Medico medico) {

        Map<String, Object> mapResponse = new HashMap<>();

        if(medico == null) {
            mapResponse.put("error", "Medico no puede ser nulo");
            return mapResponse;
        }

        medico.setId(nextId += 1);
        listaMedicos.append(medico);

        mapResponse.put("medico", medico);
        return mapResponse;
    }

    // Método para actualizar un médico existente
    public Map<String, Object> update(Integer id, Medico medico) {
        Map<String, Object> mapResponse = new HashMap<>();

        Medico medicoUpdate = listaMedicos.findById(id, Medico::getId);

        if(medicoUpdate == null) {
            mapResponse.put("error", "Medico no encontrado");
            return mapResponse;
        }

        medicoUpdate.setId(medico.getId());
        medicoUpdate.setNombre(medico.getNombre());
        medicoUpdate.setApellido(medico.getApellido());
        medicoUpdate.setEspecialidad(medico.getEspecialidad());
        medicoUpdate.setNumeroConsultorio(medico.getNumeroConsultorio());

        mapResponse.put("medico", medicoUpdate);

        return mapResponse;

    }

    // Método para eliminar un médico por su ID
    public Map<String, Object> delete(Integer id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Medico medicoDelete = listaMedicos.findById(id, Medico::getId);

        if(medicoDelete == null) {
            mapResponse.put("error", "Medico no encontrado");
            return mapResponse;
        }

        listaMedicos.delete(medicoDelete);

        mapResponse.put("medico", "Medico eliminado correctamente");

        return mapResponse;
    }

}
