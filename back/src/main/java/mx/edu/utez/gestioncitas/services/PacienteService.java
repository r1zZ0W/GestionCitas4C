package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.data_structs.MergeSort;
import mx.edu.utez.gestioncitas.dtos.CreatePacienteDTO;
import mx.edu.utez.gestioncitas.model.Paciente;
import mx.edu.utez.gestioncitas.repository.PacienteRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los pacientes.
 * Proporciona métodos para crear, leer, actualizar y eliminar pacientes.
 * Utiliza PacienteRepository para interactuar con la base de datos
 * y CustomMap para las respuestas personalizadas.
 * @author Tilines Crew
 */
@Service
public class PacienteService {

    // Repositorio de Paciente inyectado
    private final PacienteRepository pacienteRepository;

    // Constructor para inyección de dependencias
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Obtiene todos los pacientes desde la base de datos
     * @return Mapa con la lista de pacientes y un mensaje de éxito
     */
    public CustomMap<String, Object> getAll() {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Paciente> listaPacientes = new ListaSimple<>();

        listaPacientes.addAll(pacienteRepository.findAll());

        mapResponse.put("message", "Lista de pacientes obtenida exitosamente");
        mapResponse.put("listPacientes", listaPacientes);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente a buscar
     * @return Mapa con el paciente encontrado o un mensaje de error
     */
    public CustomMap<String, Object> getById(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Paciente> optPaciente = pacienteRepository.findById(id);

        if (optPaciente.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el paciente con ID: " + id);
            mapResponse.put("paciente", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        Paciente paciente = optPaciente.get();

        mapResponse.put("message", "Paciente encontrado exitosamente");
        mapResponse.put("paciente", paciente);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Crea un nuevo paciente en la base de datos
     * @param paciente DTO con los datos del paciente a crear
     * @return Mapa con el paciente creado o un mensaje de error
     */
    public CustomMap<String, Object> create(CreatePacienteDTO paciente) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (paciente == null) {

            mapResponse.put("error", "El paciente no puede ser nulo");
            mapResponse.put("paciente", null);
            mapResponse.put("code", 400);
            return mapResponse;

        }

        // Establecer prioridad por defecto si no viene
        if (paciente.getPrioridad() == null)
            paciente.setPrioridad(3); // Baja por defecto

        // Mapear DTO a entidad Paciente para guardar en BD
        Paciente nuevoPaciente = mapPaciente(paciente);

        // Guardar en BD
        pacienteRepository.save(nuevoPaciente);

        mapResponse.put("message", "Paciente creado exitosamente");
        mapResponse.put("paciente", nuevoPaciente);
        mapResponse.put("code", 201);

        return mapResponse;

    }

    /**
     * Actualiza un paciente existente en la base de datos
     * @param id ID del paciente a actualizar
     * @param paciente DTO con los datos del paciente a actualizar
     * @return Mapa con el paciente actualizado o un mensaje de error
     */
    public CustomMap<String, Object> update(Integer id, CreatePacienteDTO paciente) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        Optional<Paciente> optPaciente = pacienteRepository.findById(id); // Buscar paciente por ID, y regresa un Optional

        // Verificar si el paciente existe
        if (optPaciente.isEmpty()) {

            mapResponse.put("error", "Paciente no encontrado con ID: " + id);
            mapResponse.put("paciente", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        Paciente pacienteExistente = optPaciente.get();

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

        if (paciente.getEnAtencion() != null)
            pacienteExistente.setEnAtencion(paciente.getEnAtencion());

        // Guardar cambios en BD
        pacienteRepository.save(pacienteExistente);

        // Se envía los datos del paciente actualizado, junto con un código 200
        mapResponse.put("message", "Paciente actualizado exitosamente");
        mapResponse.put("paciente", pacienteExistente);
        mapResponse.put("code", 200);

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
        newPaciente.setEnAtencion(paciente.getEnAtencion());

        return newPaciente;

    }

    /**
     * Elimina un paciente por su ID si no tiene citas asociadas
     * @param id ID del paciente a eliminar
     * @return Mapa con el resultado de la eliminación o un mensaje de error
     */
    public CustomMap<String, Object> delete(Integer id) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Paciente> optPaciente = pacienteRepository.findById(id);

        if (optPaciente.isEmpty()) {

            mapResponse.put("error", "Paciente no encontrado con ID: " + id);
            mapResponse.put("paciente", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        Paciente paciente = optPaciente.get();

        if (paciente.getCitas() != null && !paciente.getCitas().isEmpty()) {
            mapResponse.put("error", "No se puede eliminar el paciente con ID: " + id + " porque tiene citas asociadas");
            return mapResponse;
        }

        pacienteRepository.delete(paciente);

        mapResponse.put("message", "Paciente eliminado correctamente");
        mapResponse.put("pacienteEliminado", paciente);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Busca pacientes por prioridad
     * @param prioridad Prioridad a buscar: 1 - Alta, 2 - Media, 3 - Baja
     * @return Mapa con la lista de pacientes encontrados y el total
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
     * @param nombre Término de búsqueda
     * @return Mapa con la lista de pacientes encontrados o un mensaje de error
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
     * Ordena la lista de pacientes dada por prioridad de forma ascendente
     * utilizando el algoritmo de {@code MergeSort}.
     * Filtra los pacientes que NO están en atención.
     * @return Una nueva lista simple de pacientes ordenada por prioridad (solo disponibles).
     */
    public CustomMap<String, Object> getAllPrioridadAsc() {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Paciente> listaSimple = new ListaSimple<>();

        // Obtener todos los pacientes y filtrar los que NO están en atención
        ListaSimple<Paciente> pacientesDisponibles = new ListaSimple<>();
        for (Paciente p : pacienteRepository.findAll()) {
            if (p.getEnAtencion() == null || !p.getEnAtencion()) {
                pacientesDisponibles.add(p);
            }
        }

        // Ordenar por prioridad usando MergeSort
        ListaSimple<Paciente> listaPrioridad = MergeSort.sortByPrioridadAsc(pacientesDisponibles);

        // Verificar si la lista está vacía
        if (listaPrioridad.isEmpty()) {

            mapResponse.put("message", "No se encontraron pacientes disponibles");
            mapResponse.put("listPacientes", listaPrioridad);
            mapResponse.put("code", 200);

        } else {

            mapResponse.put("message", "Pacientes ordenados por prioridad (solo disponibles)");
            mapResponse.put("listPacientes", listaPrioridad);
            mapResponse.put("code", 200);
        }

        return mapResponse;
    }

}