package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service // Servicio de cita el cual maneja la lógica de negocio
public class CitaService {

    // Lista simple para almacenar las citas
    ListaSimple<Cita> listaCitas = new ListaSimple<>();
    private Integer nextId = 1; // Variable para manejar el siguiente ID disponible

    // Constructor que inicializa el servicio con una cita de ejemplo
    public CitaService() {

        LocalDate ld = LocalDate.now();
        Cita c = new Cita();
        Paciente p = new Paciente();
        Medico m = new Medico();

        // Crear un paciente de ejemplo
        p.setId(1);
        p.setNombre("Juan");
        p.setApellido("Lopez");
        p.setSexo('H');
        p.setCorreoElectronico("2@gmail.com");
        p.setNumeroTelefono("777 458 6499");
        p.setDireccion("Calle Paciente");
        p.setFechaNacimiento(ld);
        p.setPrioridad(2);

        // Crear un médico de ejemplo
        m.setId(1);
        m.setNombre("Dr. Pedro");
        m.setApellido("Gomez");
        m.setEspecialidad("Medicina General");
        m.setNumeroConsultorio(101);

        // Crear una cita de ejemplo
        c.setId(1);
        c.setFecha(ld);
        c.setHora(ld);
        c.setPaciente(p);
        c.setMotivoConsulta("Consulta general");
        c.setEstado('A');
        c.setMedicoAsignado(m);

        // Agregar la cita de ejemplo a la lista
        listaCitas.append(c);

    }

    // Método para obtener todas las citas disponibles
    public Map<String, Object> getAll() {

        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("listCitas", listaCitas.toList());

        return mapResponse;

    }

    // Método para obtener una cita por su ID
    public Map<String, Object> getById(Integer id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Cita cita = listaCitas.findById(id, Cita::getId);

        if (cita == null) {
            mapResponse.put("error", "No se pudo encontrar la cita");
            return mapResponse;
        }

        mapResponse.put("cita", cita);
        return mapResponse;
    }

    // Método para crear una nueva cita
    public Map<String, Object> create(Cita cita) {

        Map<String, Object> mapResponse = new HashMap<>();

        if(cita == null) {
            mapResponse.put("error", "Cita no puede ser nulo");
            return mapResponse;
        }

        cita.setId(nextId += 1);
        listaCitas.append(cita);

        mapResponse.put("cita", cita);
        return mapResponse;
    }

    // Método para actualizar una cita existente
    public Map<String, Object> update(Integer id, Cita cita) {
        Map<String, Object> mapResponse = new HashMap<>();

        Cita citaUpdate = listaCitas.findById(id, Cita::getId);

        if(citaUpdate == null) {
            mapResponse.put("error", "Cita no encontrada");
            return mapResponse;
        }

        citaUpdate.setId(cita.getId());
        citaUpdate.setFecha(cita.getFecha());
        citaUpdate.setHora(cita.getHora());
        citaUpdate.setPaciente(cita.getPaciente());
        citaUpdate.setMedicoAsignado(cita.getMedicoAsignado());
        citaUpdate.setMotivoConsulta(cita.getMotivoConsulta());
        citaUpdate.setEstado(cita.getEstado());

        mapResponse.put("cita", citaUpdate);
        return mapResponse;

    }

    // Método para eliminar una cita por su ID
    public Map<String, Object> delete(Integer id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Cita citaDelete = listaCitas.findById(id, Cita::getId);

        if(citaDelete == null) {
            mapResponse.put("error", "Paciente no encontrado");
            return mapResponse;
        }

        listaCitas.delete(citaDelete);

        mapResponse.put("cita", "Cita eliminada correctamente");

        return mapResponse;
    }

}
