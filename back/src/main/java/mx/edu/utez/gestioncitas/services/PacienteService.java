package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Paciente;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class PacienteService {

    private final ListaSimple<Paciente> listaPacientes = new ListaSimple<>();
    private int nextId = 1;

    public PacienteService() {

        LocalDate ld = LocalDate.now();
        Paciente p = new Paciente();

        p.setId(1);
        p.setNombre("Juan");
        p.setApellido("Lopez");
        p.setSexo('H');
        p.setCorreoElectronico("2@gmail.com");
        p.setNumeroTelefono("777 458 6499");
        p.setDireccion("Calle Paciente");
        p.setFechaNacimiento(ld);
        p.setPrioridad(2);

        listaPacientes.append(p);

    }

    public Map<String, Object> getAll() {

        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("listPacientes", listaPacientes.toList());

        return mapResponse;

    }

    public Map<String, Object> getById(int id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Paciente paciente = listaPacientes.findById(id, Paciente::getId);

        if (paciente == null) {
            mapResponse.put("error", "No se pudo encontrar el paciente");
            return mapResponse;
        }

        mapResponse.put("paciente", paciente);
        return mapResponse;
    }

    public Map<String, Object> create(Paciente paciente) {

        Map<String, Object> mapResponse = new HashMap<>();

        if(paciente == null) {
            mapResponse.put("error", "Paciente no puede ser nulo");
            return mapResponse;
        }

        paciente.setId(nextId += 1);
        listaPacientes.append(paciente);

        mapResponse.put("paciente", paciente);
        return mapResponse;
    }


    public Map<String, Object> update(int id, Paciente paciente) {
        Map<String, Object> mapResponse = new HashMap<>();

            Paciente pacienteUpdate = listaPacientes.findById(id, Paciente::getId);

        if(pacienteUpdate == null) {
            mapResponse.put("error", "Paciente no encontrado");
            return mapResponse;
        }

        pacienteUpdate.setNombre(paciente.getNombre());
        pacienteUpdate.setApellido(paciente.getApellido());
        pacienteUpdate.setSexo(paciente.getSexo());
        pacienteUpdate.setCorreoElectronico(paciente.getCorreoElectronico());
        pacienteUpdate.setNumeroTelefono(paciente.getNumeroTelefono());
        pacienteUpdate.setDireccion(paciente.getDireccion());
        pacienteUpdate.setFechaNacimiento(paciente.getFechaNacimiento());

        mapResponse.put("paciente", pacienteUpdate);

        return mapResponse;

    }

    public Map<String, Object> delete(int id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Paciente pacienteDelete = listaPacientes.findById(id, Paciente::getId);

        if(pacienteDelete == null) {
            mapResponse.put("error", "Paciente no encontrado");
            return mapResponse;
        }

        listaPacientes.delete(pacienteDelete);

        mapResponse.put("paciente", "Paciente eliminado correctamente");

        return mapResponse;
    }
}
