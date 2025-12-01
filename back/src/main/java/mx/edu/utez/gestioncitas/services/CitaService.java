package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.Cola;
import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.data_structs.Pila;

import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;

import mx.edu.utez.gestioncitas.model.Cita;

import mx.edu.utez.gestioncitas.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CitaService {

    /**
     * Repositorio JPA para operaciones CRUD en la entidad Cita al igual que la gestión de
     * la cola de citas pendientes y el historial de citas atendidas utilizando una pila.
     */
    private final CitaRepository citaRepository;
    private final Cola<Cita> colaCitasPendientes = new Cola<>();
    private final Pila<Cita> pilaHistorialCitas = new Pila<>();

    /**
     *Constructor para inyección de dependencias y carga inicial de citas pendientes
     * @param citaRepository Repositorio JPA para la entidad Cita
     */
    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
        cargarCitasPendientes();
    }

    /**
     * Carga las citas activas desde la BD a la cola al iniciar el servicio
     */
    private void cargarCitasPendientes() {

        for (Cita cita : citaRepository.findAll())
            if (cita.getEstado() == 'A' || cita.getEstado() == 'P')
                colaCitasPendientes.offer(cita);

    }

    /**
     * Obtiene todas las citas desde la base de datos
     * @return CustomMap con la lista de todas las citas
     */
    public CustomMap<String, Object> getAll() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        // Usar ArrayList para la serialización JSON (ListaSimple causa problemas con Jackson)
        ListaSimple<Cita> listaCitas = new ListaSimple<>();
        listaCitas.addAll(citaRepository.findAll());
        mapResponse.put("listCitas", listaCitas);
        return mapResponse;
    }

    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita a buscar
     * @return mapa con la cita encontrada o error si no existe
     */
    public CustomMap<String, Object> getById(Integer id) {
        
        CustomMap<String, Object> mapResponse = new CustomMap<>();
        Optional<Cita> optCita = citaRepository.findById(id);
        
        if (optCita.isPresent()) {
            
            Cita cita = optCita.get();
            mapResponse.put("message", "Cita encontrada");
            mapResponse.put("cita", cita);
            mapResponse.put("code", 200);
        
        } else {
            
            mapResponse.put("message", "Cita no encontrada con ID: " + id);
            mapResponse.put("cita", null);
            mapResponse.put("code", 404);
            
            return mapResponse;
        
        }
        
        return mapResponse;
    }

    /**
     * Crea una nueva cita en la BD y la agrega a la cola de pendientes
     * @param cita DTO con los datos de la nueva cita
     * @return mapa con la cita creada con error si faltan datos
     */
    public CustomMap<String, Object> create(CreateCitaDTO cita) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (cita == null) {

            mapResponse.put("error", "La cita no puede ser nula");
            mapResponse.put("cita", null);
            mapResponse.put("code", 400);

            return mapResponse;
        }

        if (cita.getFecha() == null || cita.getHora() == null) {

            mapResponse.put("error", "La fecha y hora son obligatorias");
            mapResponse.put("cita", cita.getFecha());
            mapResponse.put("hora", cita.getHora());
            mapResponse.put("code", 400);

            return mapResponse;
        }

        if (cita.getPaciente() == null || cita.getMedicoAsignado() == null) {

            mapResponse.put("error", "El paciente y médico son obligatorios");
            mapResponse.put("paciente", cita.getPaciente());
            mapResponse.put("medicoAsignado", cita.getMedicoAsignado());
            mapResponse.put("code", 400);

            return mapResponse;
        }

        if (cita.getEstado() == null)
            cita.setEstado('P'); // P = Programada

        Cita nuevaCita = mapCita(cita);
        citaRepository.save(nuevaCita);

        if (nuevaCita.getEstado() == 'A' || nuevaCita.getEstado() == 'P')
            colaCitasPendientes.offer(nuevaCita);


        mapResponse.put("message", "Cita creada y agregada a la cola de pendientes");
        mapResponse.put("cita", nuevaCita);
        mapResponse.put("code", 201);

        return mapResponse;
    }

    /**
     * Actualiza una cita existente por su ID.
     * @param id ID de la cita a actualizar
     * @param cita DTO con los datos a actualizar
     * @return mapa con la cita actualizada o error si no se encuentra
     */
    public CustomMap<String, Object> update(Integer id, CreateCitaDTO cita) {

        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Cita> optCita = citaRepository.findById(id);

        if (optCita.isEmpty()) {
            
            mapResponse.put("error", "Cita no encontrada");
            mapResponse.put("cita", null);
            mapResponse.put("code", 404);
                    
            return mapResponse;
        }

        Cita citaExistente = getExistente(cita, optCita);
        citaRepository.save(citaExistente);

        mapResponse.put("message", "Cita actualizada exitosamente");
        mapResponse.put("cita", citaExistente);
        mapResponse.put("code", 200);

        return mapResponse;
    }
    
    /**
     * Actualiza solo los campos no nulos de la cita existente
     * @param cita DTO con los datos a actualizar
     * @param optCita cita existente en la BD
     * @return cita existente con los campos actualizados
     */
    private static Cita getExistente(CreateCitaDTO cita, Optional<Cita> optCita) {
        
        Cita citaExistente = optCita.get();

        if (cita.getFecha() != null) {
            citaExistente.setFecha(cita.getFecha());
        }
        if (cita.getHora() != null) {
            citaExistente.setHora(cita.getHora());
        }
        if (cita.getPaciente() != null) {
            citaExistente.setPaciente(cita.getPaciente());
        }
        if (cita.getMedicoAsignado() != null) {
            citaExistente.setMedicoAsignado(cita.getMedicoAsignado());
        }
        if (cita.getMotivoConsulta() != null) {
            citaExistente.setMotivoConsulta(cita.getMotivoConsulta());
        }
        if (cita.getEstado() != null) {
            citaExistente.setEstado(cita.getEstado());
        }
        return citaExistente;
    }

    /**
     * Elimina una cita por su ID
     * @param id ID de la cita a eliminar
     * @return mapa con la cita eliminada o error si no se encuentra
     */
    public CustomMap<String, Object> delete(Integer id) {

        CustomMap<String, Object> mapResponse = new CustomMap<>(3);
        Optional<Cita> cita = citaRepository.findById(id);
        
        if (cita.isEmpty()) {
            
            mapResponse.put("error", "Cita no encontrada");
            mapResponse.put("cita", null);
            mapResponse.put("code", 404);
            
            return mapResponse;
        }
        
        Cita citaExistente = cita.get();

        citaRepository.delete(citaExistente);

        mapResponse.put("message", "Cita eliminada correctamente");
        mapResponse.put("citaEliminada", citaExistente);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Mapea un CreateCitaDTO a una entidad Cita para que JPA la pueda guardar
     * @param cita DTO con los datos de la cita
     * @return entidad Cita mapeada
     */
    private Cita mapCita(CreateCitaDTO cita) {
        Cita newCita = new Cita();
        newCita.setFecha(cita.getFecha());
        newCita.setHora(cita.getHora());
        newCita.setPaciente(cita.getPaciente());
        newCita.setMedicoAsignado(cita.getMedicoAsignado());
        newCita.setMotivoConsulta(cita.getMotivoConsulta());
        newCita.setEstado(cita.getEstado());
        return newCita;
    }

    /**
     * Obtiene todas las citas pendientes en la cola
     * @return mapa con la lista de citas pendientes
     */
    public CustomMap<String, Object> getColaCitasPendientes() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Cita> listaPendientes = new ListaSimple<>();
        listaPendientes.addAll(colaCitasPendientes);

        mapResponse.put("colaCitasPendientes", listaPendientes);
        mapResponse.put("tamaño", colaCitasPendientes.size());
        mapResponse.put("isEmpty", colaCitasPendientes.isEmpty());

        return mapResponse;
    }

    /**
     * Obtiene la siguiente cita pendiente sin sacarla de la cola
     * @return mapa con la siguiente cita o error si no hay citas pendientes
     */
    public CustomMap<String, Object> getSiguienteCitaPendiente() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (colaCitasPendientes.isEmpty()) {
            mapResponse.put("error", "No hay citas pendientes en la cola");
            return mapResponse;
        }

        Cita siguienteCita = colaCitasPendientes.peek();
        mapResponse.put("siguienteCita", siguienteCita);
        mapResponse.put("message", "Esta es la siguiente cita a atender");

        return mapResponse;
    }

    /**
     * Atiende la siguiente cita (la saca de la cola y la marca como completada
     * @return mapa con la cita atendida o error si no hay citas pendientes
     */
    public CustomMap<String, Object> atenderSiguienteCita() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (colaCitasPendientes.isEmpty()) {
            mapResponse.put("error", "No hay citas pendientes para atender");
            return mapResponse;
        }

        Cita citaAtendida = colaCitasPendientes.poll();
        citaAtendida.setEstado('F'); // F = Finalizada

        // Actualizar en BD
        citaRepository.save(citaAtendida);

        // Agregar al historial
        pilaHistorialCitas.push(citaAtendida);

        mapResponse.put("message", "Cita atendida exitosamente. Movida de la cola al historial.");
        mapResponse.put("citaAtendida", citaAtendida);
        mapResponse.put("citasPendientesRestantes", colaCitasPendientes.size());

        return mapResponse;
    }

    /**
     * Agrega una cita existente a la cola de pendientes
     * @param id ID de la cita a agregar
     * @return mapa con la cita agregada o error si no se encuentra o no es válida
     */
    public CustomMap<String, Object> agregarCitaACola(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Cita cita = citaRepository.findById(id).orElse(null);

        if (cita == null) {
            mapResponse.put("error", "Cita no encontrada con ID: " + id);
            return mapResponse;
        }

        if (cita.getEstado() != 'A' && cita.getEstado() != 'P') {
            mapResponse.put("error", "Solo se pueden agregar citas activas (A) o programadas (P) a la cola");
            return mapResponse;
        }

        colaCitasPendientes.offer(cita);

        mapResponse.put("cita", cita);
        mapResponse.put("message", "Cita agregada a la cola de pendientes");
        mapResponse.put("posicionEnCola", colaCitasPendientes.size());

        return mapResponse;
    }

    /**
     * Obtiene el historial de citas procesadas (LIFO)
     * @return mapa con la lista de citas en el historial
     */
    public CustomMap<String, Object> getHistorialCitas() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Cita> listaHistorial = new ListaSimple<>();
        listaHistorial.addAll(pilaHistorialCitas.toList());

        mapResponse.put("historialCitas", listaHistorial);
        mapResponse.put("tamaño", pilaHistorialCitas.size());
        mapResponse.put("isEmpty", pilaHistorialCitas.isEmpty());
        mapResponse.put("message", "Historial ordenado LIFO: la última cita procesada aparece primero");

        return mapResponse;
    }

    /**
     * Obtiene la última cita procesada sin sacarla del historial
     * @return mapa con la última cita procesada o error si el historial está vacío
     */
    public CustomMap<String, Object> getUltimaCitaProcesada() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (pilaHistorialCitas.isEmpty()) {
            mapResponse.put("error", "No hay citas en el historial");
            return mapResponse;
        }

        Cita ultimaCita = pilaHistorialCitas.peek();
        mapResponse.put("ultimaCita", ultimaCita);
        mapResponse.put("message", "Esta es la última cita procesada (LIFO)");

        return mapResponse;
    }

    /**
     * Revierte la última cita procesada (la saca del historial y la vuelve a la cola)
     * @return mapa con la cita revertida o error si el historial está vacío
     */
    public CustomMap<String, Object> revertirUltimaCita() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (pilaHistorialCitas.isEmpty()) {
            mapResponse.put("error", "No hay citas en el historial para revertir");
            return mapResponse;
        }

        Cita citaRevertida = pilaHistorialCitas.pop();
        citaRevertida.setEstado('P'); // P = Programada nuevamente

        // Actualizar en BD
        citaRepository.save(citaRevertida);

        // Volver a la cola
        colaCitasPendientes.offer(citaRevertida);

        mapResponse.put("citaRevertida", citaRevertida);
        mapResponse.put("message", "Cita revertida exitosamente. Movida del historial a la cola de pendientes.");

        return mapResponse;
    }
}