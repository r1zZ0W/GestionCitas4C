package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.dtos.CreatePacienteDTO;
import mx.edu.utez.gestioncitas.model.Paciente;
import mx.edu.utez.gestioncitas.repository.PacienteRepository;

import org.springframework.stereotype.Service;


@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    // Constructor para inyección de dependencias
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Obtiene todos los pacientes desde la base de datos
     */
    public CustomMap<String, Object> getAll() {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Paciente> listaPacientes = new ListaSimple<>();

        listaPacientes.addAll(pacienteRepository.findAll());

        mapResponse.put("listPacientes", listaPacientes);
        mapResponse.put("total", listaPacientes.size());

        return mapResponse;
    }

    /**
     * Obtiene un paciente por su ID
     */
    public CustomMap<String, Object> getById(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        if (paciente == null) {
            mapResponse.put("error", "No se pudo encontrar el paciente con ID: " + id);
            return mapResponse;
        }

        mapResponse.put("paciente", paciente);
        return mapResponse;
    }

    /**
     * Crea un nuevo paciente en la base de datos
     */
    public CustomMap<String, Object> create(CreatePacienteDTO paciente) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (paciente == null) {
            mapResponse.put("error", "El paciente no puede ser nulo");
            return mapResponse;
        }

        // Establecer prioridad por defecto si no viene
        if (paciente.getPrioridad() == null) {
            paciente.setPrioridad(3); // Baja por defecto
        }

        // Mapear DTO a entidad Paciente para guardar en BD
        Paciente nuevoPaciente = mapPaciente(paciente);

        // Guardar en BD
        Paciente pacienteGuardado = pacienteRepository.save(nuevoPaciente);

        mapResponse.put("paciente", pacienteGuardado);
        mapResponse.put("message", "Paciente creado exitosamente");
        mapResponse.put("id", pacienteGuardado.getId());

        return mapResponse;

    }

    /**
     * Actualiza un paciente existente
     */
    public CustomMap<String, Object> update(Integer id, CreatePacienteDTO paciente) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Paciente pacienteExistente = pacienteRepository.findById(id).orElse(null);

        if (pacienteExistente == null) {
            mapResponse.put("error", "Paciente no encontrado con ID: " + id);
            return mapResponse;
        }

        // Actualizar campos solo si vienen con datos
        if (paciente.getNombre() != null && !paciente.getNombre().trim().isEmpty())
            pacienteExistente.setNombre(paciente.getNombre());

        if (paciente.getApellido() != null && !paciente.getApellido().trim().isEmpty())
            pacienteExistente.setApellido(paciente.getApellido());

        if (paciente.getSexo() != null)
            pacienteExistente.setSexo(paciente.getSexo());

        if (paciente.getCorreoElectronico() != null && !paciente.getCorreoElectronico().trim().isEmpty())
            if (paciente.getCorreoElectronico().contains("@"))
                pacienteExistente.setCorreoElectronico(paciente.getCorreoElectronico());

        if (paciente.getNumeroTelefono() != null && !paciente.getNumeroTelefono().trim().isEmpty())
            pacienteExistente.setNumeroTelefono(paciente.getNumeroTelefono());

        if (paciente.getDireccion() != null && !paciente.getDireccion().trim().isEmpty())
            pacienteExistente.setDireccion(paciente.getDireccion());

        if (paciente.getFechaNacimiento() != null)
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());

        if (paciente.getPrioridad() != null)
            pacienteExistente.setPrioridad(paciente.getPrioridad());

        // Guardar cambios en BD
        Paciente pacienteActualizado = pacienteRepository.save(pacienteExistente);

        mapResponse.put("paciente", pacienteActualizado);
        mapResponse.put("message", "Paciente actualizado exitosamente");

        return mapResponse;
    }

    /**
     * Mapea un CreatePacienteDTO a una entidad Paciente para guardarlo en BD
     *
     * @param paciente DTO con datos del paciente
     * @return Entidad Paciente mapeada
     */
    private Paciente mapPaciente(CreatePacienteDTO paciente) {

        Paciente newPaciente = new Paciente();

        newPaciente.setNombre(paciente.getNombre());
        newPaciente.setApellido(paciente.getApellido());
        newPaciente.setSexo(paciente.getSexo());
        newPaciente.setCorreoElectronico(paciente.getCorreoElectronico());
        newPaciente.setNumeroTelefono(paciente.getNumeroTelefono());
        newPaciente.setDireccion(paciente.getDireccion());
        newPaciente.setFechaNacimiento(paciente.getFechaNacimiento());
        newPaciente.setPrioridad(paciente.getPrioridad());

        return newPaciente;

    }

    /**
     * Elimina un paciente por su ID
     */
    public CustomMap<String, Object> delete(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        if (paciente == null) {
            mapResponse.put("error", "Paciente no encontrado");
            return mapResponse;
        }

        if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {
            mapResponse.put("error", "No se puede eliminar el paciente con ID: " + id + " porque tiene citas asociadas");
            return mapResponse;
        }

        pacienteRepository.deleteById(id);

        mapResponse.put("message", "Paciente eliminado correctamente");
        mapResponse.put("pacienteEliminado", paciente);

        return mapResponse;
    }

    /**
     * Busca pacientes por prioridad
     */
    public CustomMap<String, Object> getByPrioridad(Integer prioridad) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Paciente> listaJPA = pacienteRepository.findByPrioridad(prioridad);

        if (listaJPA.isEmpty())
            mapResponse.put("message", "No se encontraron pacientes con esa prioridad");

        ListaSimple<Paciente> listaSimple = new ListaSimple<>();
        listaSimple.addAll(listaJPA);

        mapResponse.put("listPacientes", listaSimple);
        mapResponse.put("total", listaSimple.size());

        return mapResponse;
    }

    /**
     * Busca por nombre o apellido
     */
    public CustomMap<String, Object> buscarPorNombre(String nombre) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (nombre == null || nombre.trim().isEmpty()) {
            mapResponse.put("error", "El término no puede estar vacío");
            return mapResponse;
        }

        ListaSimple<Paciente> listaJPA = pacienteRepository.buscarPorNombreOApellido(nombre);

        if (listaJPA.isEmpty())
            mapResponse.put("message", "No hay coincidencias");


        ListaSimple<Paciente> listaSimple = new ListaSimple<>();
        listaSimple.addAll(listaJPA);

        mapResponse.put("listPacientes", listaSimple);
        return mapResponse;
    }

    /**
     * Obtiene ordenados por prioridad desde la BD
     */
    public CustomMap<String, Object> getAllOrdenadosPorPrioridad() {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Paciente> listaJPA = pacienteRepository.findAllByOrderByPrioridadAsc();
        ListaSimple<Paciente> listaSimple = new ListaSimple<>();

        listaSimple.addAll(listaJPA);

        mapResponse.put("listPacientes", listaSimple);
        mapResponse.put("total", listaSimple.size());

        return mapResponse;
    }
}