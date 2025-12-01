package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.dtos.CreateMedicoDTO;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.repository.MedicoRepository;

import org.springframework.stereotype.Service;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    // Constructor para inyección de dependencias
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    /**
     * Obtiene todos los médicos desde la base de datos
     */
    public CustomMap<String, Object> getAll() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Medico> listaMedicos = new ListaSimple<>();

        listaMedicos.addAll(medicoRepository.findAll());

        mapResponse.put("listMedicos", listaMedicos);
        mapResponse.put("total", listaMedicos.size());

        return mapResponse;
    }

    /**
     * Obtiene un médico por su ID
     */
    public CustomMap<String, Object> getById(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Medico medico = medicoRepository.findById(id).orElse(null);

        if (medico == null) {
            mapResponse.put("error", "No se pudo encontrar el médico con ID: " + id);
            return mapResponse;
        }

        mapResponse.put("medico", medico);
        return mapResponse;
    }

    /**
     * Crea un nuevo médico en la base de datos
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

        // Se have el mapeo de DTO a entidad Médico para guardarlo en BD
        Medico nuevoMedico = mapMedico(medico);

        // Guardar en BD el nuevo médico
        Medico medicoGuardado = medicoRepository.save(nuevoMedico);

        mapResponse.put("medico", medicoGuardado);
        mapResponse.put("message", "Médico creado exitosamente");

        return mapResponse;
    }

    /**
     * Actualiza un médico existente
     */
    public CustomMap<String, Object> update(Integer id, CreateMedicoDTO medico) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Medico medicoExistente = medicoRepository.findById(id).orElse(null);

        if (medicoExistente == null) {
            mapResponse.put("error", "Médico no encontrado con ID: " + id);
            return mapResponse;
        }

        // Actualizar campos solo si vienen con datos
        if (medico.getNombre() != null && !medico.getNombre().trim().isEmpty()) {
            medicoExistente.setNombre(medico.getNombre());
        }

        if (medico.getApellido() != null && !medico.getApellido().trim().isEmpty()) {
            medicoExistente.setApellido(medico.getApellido());
        }

        if (medico.getEspecialidad() != null && !medico.getEspecialidad().trim().isEmpty()) {
            medicoExistente.setEspecialidad(medico.getEspecialidad());
        }

        if (medico.getNumeroConsultorio() != null) {
            medicoExistente.setNumeroConsultorio(medico.getNumeroConsultorio());
        }

        // Guardar cambios en BD
        Medico medicoActualizado = medicoRepository.save(medicoExistente);

        mapResponse.put("medico", medicoActualizado);
        mapResponse.put("message", "Médico actualizado exitosamente");

        return mapResponse;
    }

    /**
     * Elimina un médico por su ID
     */
    public CustomMap<String, Object> delete(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Medico medico = medicoRepository.findById(id).orElse(null);

        if (medico == null) {
            mapResponse.put("error", "Médico no encontrado con ID: " + id);
            return mapResponse;
        }

        medicoRepository.deleteById(id);

        mapResponse.put("message", "Médico eliminado correctamente");
        mapResponse.put("medicoEliminado", medico);

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

        return newMedico;

    }

    /**
     * Busca médicos por especialidad
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
}