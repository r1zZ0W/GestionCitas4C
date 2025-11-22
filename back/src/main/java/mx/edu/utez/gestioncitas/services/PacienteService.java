package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Paciente;
import mx.edu.utez.gestioncitas.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

        List<Paciente> pacientes = pacienteRepository.findAll();
        ListaSimple<Paciente> listaPacientes = new ListaSimple<>();

        for (Paciente paciente : pacientes) {
            listaPacientes.add(paciente);
        }

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
    public CustomMap<String, Object> create(Paciente paciente) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (paciente == null) {
            mapResponse.put("error", "El paciente no puede ser nulo");
            return mapResponse;
        }

        // Validaciones básicas
        if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
            mapResponse.put("error", "El nombre del paciente es obligatorio");
            return mapResponse;
        }

        if (paciente.getApellido() == null || paciente.getApellido().trim().isEmpty()) {
            mapResponse.put("error", "El apellido del paciente es obligatorio");
            return mapResponse;
        }

        if (paciente.getCorreoElectronico() == null || paciente.getCorreoElectronico().trim().isEmpty()) {
            mapResponse.put("error", "El correo electrónico es obligatorio");
            return mapResponse;
        }

        // Validar formato de correo (básico)
        if (!paciente.getCorreoElectronico().contains("@")) {
            mapResponse.put("error", "El formato del correo electrónico no es válido");
            return mapResponse;
        }

        // Establecer prioridad por defecto si no viene
        if (paciente.getPrioridad() == null) {
            paciente.setPrioridad(3); // Baja por defecto
        }

        // Guardar en BD (JPA genera el ID automáticamente)
        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        mapResponse.put("paciente", pacienteGuardado);
        mapResponse.put("message", "Paciente creado exitosamente");
        mapResponse.put("id", pacienteGuardado.getId());

        return mapResponse;
    }

    /**
     * Actualiza un paciente existente
     */
    public CustomMap<String, Object> update(Integer id, Paciente paciente) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Paciente pacienteExistente = pacienteRepository.findById(id).orElse(null);

        if (pacienteExistente == null) {
            mapResponse.put("error", "Paciente no encontrado con ID: " + id);
            return mapResponse;
        }

        // Actualizar campos solo si vienen con datos
        if (paciente.getNombre() != null && !paciente.getNombre().trim().isEmpty()) {
            pacienteExistente.setNombre(paciente.getNombre());
        }

        if (paciente.getApellido() != null && !paciente.getApellido().trim().isEmpty()) {
            pacienteExistente.setApellido(paciente.getApellido());
        }

        if (paciente.getSexo() != null) {
            pacienteExistente.setSexo(paciente.getSexo());
        }

        if (paciente.getCorreoElectronico() != null && !paciente.getCorreoElectronico().trim().isEmpty()) {
            if (paciente.getCorreoElectronico().contains("@")) {
                pacienteExistente.setCorreoElectronico(paciente.getCorreoElectronico());
            }
        }

        if (paciente.getNumeroTelefono() != null && !paciente.getNumeroTelefono().trim().isEmpty()) {
            pacienteExistente.setNumeroTelefono(paciente.getNumeroTelefono());
        }

        if (paciente.getDireccion() != null && !paciente.getDireccion().trim().isEmpty()) {
            pacienteExistente.setDireccion(paciente.getDireccion());
        }

        if (paciente.getFechaNacimiento() != null) {
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        }

        if (paciente.getPrioridad() != null) {
            pacienteExistente.setPrioridad(paciente.getPrioridad());
        }

        // Guardar cambios en BD
        Paciente pacienteActualizado = pacienteRepository.save(pacienteExistente);

        mapResponse.put("paciente", pacienteActualizado);
        mapResponse.put("message", "Paciente actualizado exitosamente");

        return mapResponse;
    }

    /**
     * Elimina un paciente por su ID
     */
    public CustomMap<String, Object> delete(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        if (paciente == null) {
            mapResponse.put("error", "Paciente no encontrado con ID: " + id);
            return mapResponse;
        }

        // Verificar si tiene citas asignadas (opcional pero recomendado)
        // Si tienes la relación bidireccional:
        // if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {
        //     mapResponse.put("error", "No se puede eliminar un paciente con citas asignadas");
        //     return mapResponse;
        // }

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

        if (prioridad == null || prioridad < 1 || prioridad > 3) {
            mapResponse.put("error", "La prioridad debe ser 1 (Alta), 2 (Media) o 3 (Baja)");
            return mapResponse;
        }

        List<Paciente> pacientes = pacienteRepository.findAll();
        ListaSimple<Paciente> pacientesFiltrados = new ListaSimple<>();

        for (Paciente paciente : pacientes) {
            if (paciente.getPrioridad() != null && paciente.getPrioridad().equals(prioridad)) {
                pacientesFiltrados.add(paciente);
            }
        }

        if (pacientesFiltrados.isEmpty()) {
            mapResponse.put("message", "No se encontraron pacientes con esa prioridad");
            mapResponse.put("listPacientes", pacientesFiltrados);
            return mapResponse;
        }

        String prioridadTexto = switch (prioridad) {
            case 1 -> "Alta";
            case 2 -> "Media";
            case 3 -> "Baja";
            default -> "Desconocida";
        };

        mapResponse.put("listPacientes", pacientesFiltrados);
        mapResponse.put("total", pacientesFiltrados.size());
        mapResponse.put("prioridad", prioridadTexto);

        return mapResponse;
    }

    /**
     * Busca pacientes por nombre o apellido (búsqueda parcial)
     */
    public CustomMap<String, Object> buscarPorNombre(String termino) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (termino == null || termino.trim().isEmpty()) {
            mapResponse.put("error", "El término de búsqueda no puede estar vacío");
            return mapResponse;
        }

        List<Paciente> pacientes = pacienteRepository.findAll();
        ListaSimple<Paciente> pacientesEncontrados = new ListaSimple<>();
        String terminoBusqueda = termino.toLowerCase().trim();

        for (Paciente paciente : pacientes) {
            String nombreCompleto = (paciente.getNombre() + " " + paciente.getApellido()).toLowerCase();
            if (nombreCompleto.contains(terminoBusqueda)) {
                pacientesEncontrados.add(paciente);
            }
        }

        if (pacientesEncontrados.isEmpty()) {
            mapResponse.put("message", "No se encontraron pacientes con ese nombre");
            mapResponse.put("listPacientes", pacientesEncontrados);
            return mapResponse;
        }

        mapResponse.put("listPacientes", pacientesEncontrados);
        mapResponse.put("total", pacientesEncontrados.size());
        mapResponse.put("terminoBusqueda", termino);

        return mapResponse;
    }

    /**
     * Obtiene pacientes ordenados por prioridad (1-Alta, 2-Media, 3-Baja)
     */
    public CustomMap<String, Object> getAllOrdenadosPorPrioridad() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        List<Paciente> pacientes = pacienteRepository.findAll();

        // Separar por prioridad usando tus estructuras
        ListaSimple<Paciente> prioridadAlta = new ListaSimple<>();
        ListaSimple<Paciente> prioridadMedia = new ListaSimple<>();
        ListaSimple<Paciente> prioridadBaja = new ListaSimple<>();

        for (Paciente paciente : pacientes) {
            Integer prioridad = paciente.getPrioridad() != null ? paciente.getPrioridad() : 3;
            switch (prioridad) {
                case 1 -> prioridadAlta.add(paciente);
                case 2 -> prioridadMedia.add(paciente);
                default -> prioridadBaja.add(paciente);
            }
        }

        // Combinar en orden: Alta -> Media -> Baja
        ListaSimple<Paciente> listaOrdenada = new ListaSimple<>();

        for (Paciente p : prioridadAlta) listaOrdenada.add(p);
        for (Paciente p : prioridadMedia) listaOrdenada.add(p);
        for (Paciente p : prioridadBaja) listaOrdenada.add(p);

        mapResponse.put("listPacientes", listaOrdenada);
        mapResponse.put("total", listaOrdenada.size());
        mapResponse.put("prioridadAlta", prioridadAlta.size());
        mapResponse.put("prioridadMedia", prioridadMedia.size());
        mapResponse.put("prioridadBaja", prioridadBaja.size());

        return mapResponse;
    }
}