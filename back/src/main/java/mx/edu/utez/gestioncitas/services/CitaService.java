package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.Cola;
import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.data_structs.Pila;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final Cola<Cita> colaCitasPendientes = new Cola<>();
    private final Pila<Cita> pilaHistorialCitas = new Pila<>();

    // Constructor para inyección de dependencias
    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
        cargarCitasPendientesDesdeDB();
    }

    /**
     * Carga las citas activas desde la BD a la cola al iniciar el servicio
     */
    private void cargarCitasPendientesDesdeDB() {
        List<Cita> citasActivas = citaRepository.findAll();
        for (Cita cita : citasActivas) {
            if (cita.getEstado() == 'A' || cita.getEstado() == 'P') {
                colaCitasPendientes.enqueue(cita);
            }
        }
    }

    /**
     * Obtiene todas las citas desde la base de datos
     */
    public CustomMap<String, Object> getAll() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        List<Cita> citas = citaRepository.findAll();
        ListaSimple<Cita> listaCitas = new ListaSimple<>();

        for (Cita cita : citas) {
            listaCitas.add(cita);
        }

        mapResponse.put("listCitas", listaCitas);
        mapResponse.put("total", listaCitas.size());

        return mapResponse;
    }

    /**
     * Obtiene una cita por su ID
     */
    public CustomMap<String, Object> getById(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Cita cita = citaRepository.findById(id).orElse(null);

        if (cita == null) {
            mapResponse.put("error", "No se pudo encontrar la cita con ID: " + id);
            return mapResponse;
        }

        mapResponse.put("cita", cita);
        return mapResponse;
    }

    /**
     * Crea una nueva cita en la BD y la agrega a la cola de pendientes
     */
    public CustomMap<String, Object> create(Cita cita) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (cita == null) {
            mapResponse.put("error", "La cita no puede ser nula");
            return mapResponse;
        }

        // Validaciones básicas
        if (cita.getFecha() == null || cita.getHora() == null) {
            mapResponse.put("error", "La fecha y hora son obligatorias");
            return mapResponse;
        }

        if (cita.getPaciente() == null || cita.getMedicoAsignado() == null) {
            mapResponse.put("error", "El paciente y médico son obligatorios");
            return mapResponse;
        }

        // Establecer estado inicial si no se proporcionó
        if (cita.getEstado() == null) {
            cita.setEstado('P'); // P = Programada
        }

        // Guardar en BD (JPA genera el ID automáticamente)
        Cita citaGuardada = citaRepository.save(cita);

        // Agregar a la cola si está activa o programada
        if (citaGuardada.getEstado() == 'A' || citaGuardada.getEstado() == 'P') {
            colaCitasPendientes.enqueue(citaGuardada);
        }

        mapResponse.put("cita", citaGuardada);
        mapResponse.put("message", "Cita creada y agregada a la cola de pendientes");
        mapResponse.put("id", citaGuardada.getId());

        return mapResponse;
    }

    /**
     * Actualiza una cita existente
     */
    public CustomMap<String, Object> update(Integer id, Cita cita) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Cita citaExistente = citaRepository.findById(id).orElse(null);

        if (citaExistente == null) {
            mapResponse.put("error", "Cita no encontrada con ID: " + id);
            return mapResponse;
        }

        // Actualizar campos
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

        // Guardar cambios en BD
        Cita citaActualizada = citaRepository.save(citaExistente);

        mapResponse.put("cita", citaActualizada);
        mapResponse.put("message", "Cita actualizada exitosamente");

        return mapResponse;
    }

    /**
     * Elimina una cita por su ID
     */
    public CustomMap<String, Object> delete(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Cita cita = citaRepository.findById(id).orElse(null);

        if (cita == null) {
            mapResponse.put("error", "Cita no encontrada con ID: " + id);
            return mapResponse;
        }

        citaRepository.deleteById(id);

        mapResponse.put("message", "Cita eliminada correctamente");
        mapResponse.put("citaEliminada", cita);

        return mapResponse;
    }

    /**
     * Obtiene todas las citas pendientes en la cola
     */
    public CustomMap<String, Object> getColaCitasPendientes() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Cita> listaPendientes = new ListaSimple<>();
        for (Cita cita : colaCitasPendientes.toList()) {
            listaPendientes.add(cita);
        }

        mapResponse.put("colaCitasPendientes", listaPendientes);
        mapResponse.put("tamaño", colaCitasPendientes.size());
        mapResponse.put("isEmpty", colaCitasPendientes.isEmpty());

        return mapResponse;
    }

    /**
     * Obtiene la siguiente cita pendiente sin sacarla de la cola
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
     * Atiende la siguiente cita (la saca de la cola y la marca como completada)
     */
    public CustomMap<String, Object> atenderSiguienteCita() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (colaCitasPendientes.isEmpty()) {
            mapResponse.put("error", "No hay citas pendientes para atender");
            return mapResponse;
        }

        Cita citaAtendida = colaCitasPendientes.dequeue();
        citaAtendida.setEstado('F'); // F = Finalizada

        // Actualizar en BD
        citaRepository.save(citaAtendida);

        // Agregar al historial
        pilaHistorialCitas.push(citaAtendida);

        mapResponse.put("citaAtendida", citaAtendida);
        mapResponse.put("message", "Cita atendida exitosamente. Movida de la cola al historial.");
        mapResponse.put("citasPendientesRestantes", colaCitasPendientes.size());

        return mapResponse;
    }

    /**
     * Agrega una cita existente a la cola de pendientes
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

        colaCitasPendientes.enqueue(cita);

        mapResponse.put("cita", cita);
        mapResponse.put("message", "Cita agregada a la cola de pendientes");
        mapResponse.put("posicionEnCola", colaCitasPendientes.size());

        return mapResponse;
    }

    /**
     * Obtiene el historial de citas procesadas (LIFO)
     */
    public CustomMap<String, Object> getHistorialCitas() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        ListaSimple<Cita> listaHistorial = new ListaSimple<>();
        for (Cita cita : pilaHistorialCitas.toList()) {
            listaHistorial.add(cita);
        }

        mapResponse.put("historialCitas", listaHistorial);
        mapResponse.put("tamaño", pilaHistorialCitas.size());
        mapResponse.put("isEmpty", pilaHistorialCitas.isEmpty());
        mapResponse.put("message", "Historial ordenado LIFO: la última cita procesada aparece primero");

        return mapResponse;
    }

    /**
     * Obtiene la última cita procesada sin sacarla del historial
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
        colaCitasPendientes.enqueue(citaRevertida);

        mapResponse.put("citaRevertida", citaRevertida);
        mapResponse.put("message", "Cita revertida exitosamente. Movida del historial a la cola de pendientes.");

        return mapResponse;
    }
}