package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.BubbleSort;
import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.dtos.CreateMedicoDTO;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.repository.MedicoRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    // Constructor para inyección de dependencias
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    /**
     * Obtiene todos los médicos desde la base de datos
     * @return Mapa con la lista de médicos y el total de registros
     */
    public CustomMap<String, Object> getAll() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Medico> listaMedicos = new ListaSimple<>();

        listaMedicos.addAll(medicoRepository.findAll());

        mapResponse.put("message", "Lista de médicos obtenida exitosamente");
        mapResponse.put("listMedicos", listaMedicos);

        return mapResponse;
    }

    /**
     * Obtiene todos los médicos ordenados por nombre alfabéticamente usando BubbleSort
     * @return Mapa con la lista de médicos ordenada por nombre
     */
    public CustomMap<String, Object> getAllOrdenadosPorNombre() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Medico> listaMedicos = new ListaSimple<>();
        listaMedicos.addAll(medicoRepository.findAll());

        // Ordenar usando BubbleSort
        ListaSimple<Medico> medicosOrdenados = BubbleSort.sortByNombreAsc(listaMedicos);

        mapResponse.put("message", "Lista de médicos ordenada por nombre (BubbleSort)");
        mapResponse.put("listMedicos", medicosOrdenados);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Obtiene un médico por su ID
     * @param id ID del médico a buscar
     * @return Mapa con la respuesta de la búsqueda
     */
    public CustomMap<String, Object> getById(Integer id) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Medico> optMedico = medicoRepository.findById(id);

        if (optMedico.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + id);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Medico medico = optMedico.get();

        mapResponse.put("medico", medico);
        mapResponse.put("code", 200);

        return mapResponse;

    }

    /**
     * Crea un nuevo médico en la base de datos
     * @param medico DTO con los datos del médico a crear
     * @return Mapa con la respuesta de la creación
     */
    public CustomMap<String, Object> create(CreateMedicoDTO medico) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (medico == null) {
            mapResponse.put("error", "El médico no puede ser nulo");
            return mapResponse;
        }

        // Validaciones básicas
        if (medico.getNombre() == null || medico.getNombre().trim().isEmpty()) {
            mapResponse.put("error", "El nombre del médico es obligatorio");
            return mapResponse;
        }

        if (medico.getApellido() == null || medico.getApellido().trim().isEmpty()) {
            mapResponse.put("error", "El apellido del médico es obligatorio");
            return mapResponse;
        }

        if (medico.getEspecialidad() == null || medico.getEspecialidad().trim().isEmpty()) {
            mapResponse.put("error", "La especialidad es obligatoria");
            return mapResponse;
        }

        // Se hace el mapeo de DTO a entidad Médico para guardarlo en BD
        Medico nuevoMedico = mapMedico(medico);

        // Guardar en BD el nuevo médico
        Medico medicoGuardado = medicoRepository.save(nuevoMedico);

        mapResponse.put("message", "Médico creado exitosamente");
        mapResponse.put("medico", medicoGuardado);
        mapResponse.put("code", 201);
        return mapResponse;
    }

    /**
     * Actualiza un médico existente
     * @param id ID del médico a actualizar
     * @param medico DTO con los datos actualizados del médico
     * @return Mapa con la respuesta de la actualización
     */
    public CustomMap<String, Object> update(Integer id, CreateMedicoDTO medico) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Medico> optMedico = medicoRepository.findById(id);

        if (optMedico.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + id);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Medico medicoExistente = getMedicoExistente(medico, optMedico);

        // Guardar cambios en BD
        Medico medicoActualizado = medicoRepository.save(medicoExistente);

        mapResponse.put("medico", medicoActualizado);
        mapResponse.put("message", "Médico actualizado exitosamente");

        return mapResponse;
    }

    /**
     * Actualiza los campos de un médico existente con los datos proporcionados en el DTO
     * @param medico DTO con los datos actualizados del médico
     * @param optMedico Médico existente en la base de datos
     * @return Médico con los datos actualizados
     */
    private static Medico getMedicoExistente(CreateMedicoDTO medico, Optional<Medico> optMedico) {
        Medico medicoExistente = optMedico.get();

        // Actualizar campos solo si vienen con datos
        if (medico.getNombre() != null && !medico.getNombre().trim().isEmpty())
            medicoExistente.setNombre(medico.getNombre());

        if (medico.getApellido() != null && !medico.getApellido().trim().isEmpty())
            medicoExistente.setApellido(medico.getApellido());

        if (medico.getEspecialidad() != null && !medico.getEspecialidad().trim().isEmpty())
            medicoExistente.setEspecialidad(medico.getEspecialidad());

        if (medico.getNumeroConsultorio() != null)
            medicoExistente.setNumeroConsultorio(medico.getNumeroConsultorio());

        if (medico.getOcupado() != null)
            medicoExistente.setOcupado(medico.getOcupado());

        return medicoExistente;
    }

    /**
     * Elimina un médico por su ID
     * @param id ID del médico a eliminar
     * @return Mapa con la respuesta de la eliminación
     */
    public CustomMap<String, Object> delete(Integer id) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Medico> optMedico = medicoRepository.findById(id);

        if (optMedico.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + id);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Medico medico = optMedico.get();

        medicoRepository.delete(medico);

        mapResponse.put("message", "Médico eliminado correctamente");
        mapResponse.put("medicoEliminado", medico);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Mapea un CreateMedicoDTO a una entidad Médico para que pueda ser guardada en la base de datos
     * @param medico DTO con los datos del médico a crear
     * @return Entidad Médico mapeada
     */
    private Medico mapMedico(CreateMedicoDTO medico) {

        Medico newMedico = new Medico();

        newMedico.setNombre(medico.getNombre());
        newMedico.setApellido(medico.getApellido());
        newMedico.setEspecialidad(medico.getEspecialidad());
        newMedico.setNumeroConsultorio(medico.getNumeroConsultorio());
        newMedico.setOcupado(medico.getOcupado());

        return newMedico;

    }

    /**
     * Busca médicos por especialidad
     * @param especialidad Especialidad a buscar
     * @return Mapa con la respuesta de la búsqueda
     */
    public CustomMap<String, Object> getByEspecialidad(String especialidad) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (especialidad == null || especialidad.trim().isEmpty()) {

            mapResponse.put("error", "La especialidad no puede estar vacía");
            mapResponse.put("listMedicos", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        ListaSimple<Medico> medicosFiltrados = new ListaSimple<>();

        for (Medico medico : medicoRepository.findAll()) {
            if (medico.getEspecialidad() != null &&
                    medico.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase())) {
                medicosFiltrados.add(medico);
            }
        }

        if (medicosFiltrados.isEmpty()) {

            mapResponse.put("message", "No se encontraron médicos con esa especialidad");
            mapResponse.put("listMedicos", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        mapResponse.put("message", "Se encontraron médicos con la especialidad: " + especialidad);
        mapResponse.put("listMedicos", medicosFiltrados);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Busca médicos por número de consultorio
     * @param numeroConsultorio Número de consultorio a buscar
     * @return Mapa con la respuesta de la búsqueda
     */
    public CustomMap<String, Object> getByConsultorio(Integer numeroConsultorio) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (numeroConsultorio == null) {

            mapResponse.put("error", "El número de consultorio no puede ser nulo");
            mapResponse.put("listMedicos", null);
            mapResponse.put("code", 404);

            return mapResponse;
        }

        Medico medicoEncontrado = null;

        for (Medico medico : medicoRepository.findAll()) {
            if (medico.getNumeroConsultorio() != null &&
                    medico.getNumeroConsultorio().equals(numeroConsultorio)) {
                medicoEncontrado = medico;
                break;
            }
        }

        if (medicoEncontrado == null) {
            mapResponse.put("error", "No se encontró médico en el consultorio: " + numeroConsultorio);
            return mapResponse;
        }

        mapResponse.put("medico", medicoEncontrado);

        return mapResponse;
    }

    /**
     * Obtiene la lista de médicos disponibles (no ocupados)
     * @return Mapa con la lista de médicos disponibles
     */
    public CustomMap<String, Object> getMedicosDisponibles() {

        CustomMap<String, Object> mapResponse = new CustomMap<>();
        ListaSimple<Medico> medicosDisponibles = new ListaSimple<>();

        for (Medico medico : medicoRepository.findAll())
            if (medico.getOcupado() != null && !medico.getOcupado()) {
                System.out.println("Medico ID: " + medico.getId() +
                        " - Ocupado: " + medico.getOcupado());
                medicosDisponibles.add(medico);
            }
        if (medicosDisponibles.isEmpty()) {

            mapResponse.put("message", "No hay médicos disponibles en este momento");
            mapResponse.put("code", 404);

            return mapResponse;

        }

        mapResponse.put("message", "Médicos disponibles");
        mapResponse.put("listMedicos", medicosDisponibles);
        mapResponse.put("code", 200);

        return mapResponse;

    }

    /**
     * Marca a un médico como ocupado
     * @param idMedico ID del médico a marcar como ocupado
     * @return Mapa con la respuesta de la operación
     */
    public CustomMap<String, Object> marcarOcupado(Integer idMedico) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Medico> optMedico = medicoRepository.findById(idMedico);

        if (optMedico.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + idMedico);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Medico medico = optMedico.get();
        medico.setOcupado(true);

        medicoRepository.save(medico);

        mapResponse.put("medico", medico);
        mapResponse.put("message", "Estado de ocupación actualizado exitosamente");
        mapResponse.put("code", 200);

        return mapResponse;

    }

    /**
     * Marca a un médico como disponible
     * @param idMedico ID del médico a marcar como disponible
     * @return Mapa con la respuesta de la operación
     */
    public CustomMap<String, Object> marcarDisponible(Integer idMedico) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Medico> optMedico = medicoRepository.findById(idMedico);

        if (optMedico.isEmpty()) {

            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + idMedico);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Medico medico = optMedico.get();
        medico.setOcupado(false);

        medicoRepository.save(medico);

        mapResponse.put("medico", medico);
        mapResponse.put("message", "Estado de ocupación actualizado exitosamente");
        mapResponse.put("code", 200);

        return mapResponse;

    }

}