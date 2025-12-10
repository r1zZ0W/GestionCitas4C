package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.BinaryTree;
import mx.edu.utez.gestioncitas.data_structs.Cola;
import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.data_structs.MergeSort;
import mx.edu.utez.gestioncitas.data_structs.Pila;

import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;

import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;

import mx.edu.utez.gestioncitas.repository.CitaRepository;
import mx.edu.utez.gestioncitas.repository.MedicoRepository;
import mx.edu.utez.gestioncitas.repository.PacienteRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CitaService {

    /**
     * Repositorio JPA para operaciones CRUD en la entidad Cita al igual que la gestión de
     * la cola de citas pendientes y el historial de citas atendidas utilizando una Pila.
     * BinaryTree se usa para búsqueda eficiente en el historial.
     */
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final Cola<Cita> colaCitasPendientes = new Cola<>();
    private final Pila<Cita> pilaHistorialCitas = new Pila<>();
    private final BinaryTree<Cita> arbolBusquedaHistorial; // Para búsqueda eficiente
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final CustomMap<Integer, ScheduledFuture<?>> tareasProgramadas = new CustomMap<>();


    private final Random random = new Random();

    /**
     * Constructor para inyección de dependencias y carga inicial de citas pendientes
     * @param citaRepository Repositorio JPA para la entidad Cita
     * @param pacienteRepository Repositorio JPA para la entidad Paciente
     * @param medicoRepository Repositorio JPA para la entidad Medico
     */
    public CitaService(CitaRepository citaRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        
        // Crear BinaryTree para búsqueda por ID (más eficiente)
        this.arbolBusquedaHistorial = new BinaryTree<>((c1, c2) -> {
            Integer id1 = c1.getId() != null ? c1.getId() : 0;
            Integer id2 = c2.getId() != null ? c2.getId() : 0;
            return id1.compareTo(id2);
        });
        
        cargarCitasPendientes();
        cargarHistorialCitas();
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
     * Carga el historial de citas finalizadas desde la BD a la pila y al árbol de búsqueda al iniciar el servicio
     */
    private void cargarHistorialCitas() {
        for (Cita cita : citaRepository.findAll()) {
            if (cita.getEstado() != null && cita.getEstado() == 'F') { // F = Finalizada
                pilaHistorialCitas.push(cita);
                arbolBusquedaHistorial.insert(cita); // Para búsqueda eficiente
            }
        }
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
        
        // La prioridad se asigna al paciente cuando se crea la cita
        // Esto permite que la prioridad esté asociada a la cita, no solo al paciente
        if (nuevaCita.getPaciente() != null) {
            Paciente paciente = nuevaCita.getPaciente();
            // Si el paciente viene con prioridad desde el formulario, actualizarla
            if (paciente.getPrioridad() != null) {
                // Actualizar la prioridad del paciente en BD
                Optional<Paciente> optPaciente = pacienteRepository.findById(paciente.getId());
                if (optPaciente.isPresent()) {
                    Paciente pacienteBD = optPaciente.get();
                    pacienteBD.setPrioridad(paciente.getPrioridad());
                    pacienteRepository.save(pacienteBD);
                    // Actualizar referencia en la cita
                    nuevaCita.setPaciente(pacienteBD);
                }
            }
        }
        
        citaRepository.save(nuevaCita);

        // Agregar a la cola si está Programada, Reagendada, o en estado A
        // Las citas reagendadas vuelven a la cola para ser atendidas
        if (nuevaCita.getEstado() == 'P' || nuevaCita.getEstado() == 'R') {
            colaCitasPendientes.offer(nuevaCita);
            mapResponse.put("message", "Cita creada y agregada a la cola de pendientes");
        } else {
            mapResponse.put("message", "Cita creada exitosamente");
        }
        
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
        Character estadoAnterior = optCita.get().getEstado();
        Character estadoNuevo = citaExistente.getEstado();

        // Si se finaliza o cancela, liberar recursos
        if (estadoNuevo == 'F' || estadoNuevo == 'C') {
            if (citaExistente.getMedicoAsignado() != null) {
                citaExistente.getMedicoAsignado().setOcupado(false);
                medicoRepository.save(citaExistente.getMedicoAsignado());
            }
            if (citaExistente.getPaciente() != null) {
                citaExistente.getPaciente().setEnAtencion(false);
                pacienteRepository.save(citaExistente.getPaciente());
            }
        }

        citaRepository.save(citaExistente);

        // Si se reagenda (R), agregar a la cola usando estructura manual (Cola)
        if (estadoNuevo == 'R' && estadoAnterior != 'R') {
            colaCitasPendientes.offer(citaExistente);
            mapResponse.put("message", "Cita reagendada y agregada a la cola de pendientes");
        } else if (estadoNuevo == 'P' && estadoAnterior != 'P') {
            // Si cambia a Programada, también agregar a la cola
            colaCitasPendientes.offer(citaExistente);
            mapResponse.put("message", "Cita actualizada y agregada a la cola de pendientes");
        } else {
            mapResponse.put("message", "Cita actualizada exitosamente");
        }
        
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

        if (cita.getFecha() != null)
            citaExistente.setFecha(cita.getFecha());

        if (cita.getHora() != null)
            citaExistente.setHora(cita.getHora());

        if (cita.getPaciente() != null)
            citaExistente.setPaciente(cita.getPaciente());

        if (cita.getMedicoAsignado() != null)
            citaExistente.setMedicoAsignado(cita.getMedicoAsignado());

        if (cita.getMotivoConsulta() != null)
            citaExistente.setMotivoConsulta(cita.getMotivoConsulta());

        if (cita.getEstado() != null)
            citaExistente.setEstado(cita.getEstado());

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

        ScheduledFuture<?> tarea = tareasProgramadas.get(id);
        if (tarea != null && !tarea.isDone()) {
            tarea.cancel(false); // Cancelar sin interrumpir si ya está ejecutándose
            tareasProgramadas.remove(id);
            System.out.println("Tarea programada cancelada para cita ID: " + id);
        }

        // Liberar al médico
        if (citaExistente.getMedicoAsignado() != null) {

            citaExistente.getMedicoAsignado().setOcupado(false);
            medicoRepository.save(citaExistente.getMedicoAsignado());

        }

        // Marcar al paciente como no en atención
        if (citaExistente.getPaciente() != null) {

            citaExistente.getPaciente().setEnAtencion(false);
            pacienteRepository.save(citaExistente.getPaciente());

        }
        
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

        // Agregar al historial (Pila) y al árbol de búsqueda
        pilaHistorialCitas.push(citaAtendida);
        arbolBusquedaHistorial.insert(citaAtendida);

        mapResponse.put("message", "Cita atendida exitosamente. Movida de la cola al historial.");
        mapResponse.put("citaAtendida", citaAtendida);
        mapResponse.put("citasPendientesRestantes", colaCitasPendientes.size());

        return mapResponse;
    }

    /**
     * Atiende un paciente por prioridad desde las citas programadas en la cola.
     * Lo marca como "en atención" y después de 6 segundos lo quita de la lista y finaliza la cita.
     * @return mapa con la cita creada o error si no hay pacientes disponibles
     */
    public CustomMap<String, Object> atenderPacientePorPrioridad() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        // Verificar que hay médicos disponibles
        ListaSimple<Medico> medicosDisponibles = new ListaSimple<>();
        for (Medico medico : medicoRepository.findAll()) {
            if (medico.getOcupado() == null || !medico.getOcupado()) {
                medicosDisponibles.add(medico);
            }
        }

        if (medicosDisponibles.isEmpty()) {
            mapResponse.put("error", "No hay médicos disponibles para atender");
            mapResponse.put("code", 404);
            return mapResponse;
        }

        // Obtener citas programadas (P) o reagendadas (R) de la cola para hoy
        LocalDate fechaHoy = java.time.LocalDate.now();
        ListaSimple<Cita> citasDisponibles = new ListaSimple<>();
        
        // Obtener todas las citas programadas o reagendadas para hoy
        for (Cita cita : citaRepository.findAll()) {
            if (cita.getFecha() != null && 
                cita.getFecha().equals(fechaHoy) &&
                (cita.getEstado() == 'P' || cita.getEstado() == 'R') &&
                cita.getPaciente() != null) {
                
                // Verificar que el paciente no esté en atención
                Paciente paciente = cita.getPaciente();
                if (paciente.getEnAtencion() == null || !paciente.getEnAtencion()) {
                    citasDisponibles.add(cita);
                }
            }
        }

        if (citasDisponibles.isEmpty()) {
            mapResponse.put("error", "No hay citas programadas disponibles para atender");
            mapResponse.put("code", 404);
            return mapResponse;
        }

        // Obtener pacientes de las citas disponibles
        ListaSimple<Paciente> pacientesDeCitas = new ListaSimple<>();
        CustomMap<Integer, Cita> citasPorPaciente = new CustomMap<>(); // Para asociar cita con paciente
        
        for (Cita cita : citasDisponibles) {
            Paciente paciente = cita.getPaciente();
            pacientesDeCitas.add(paciente);
            citasPorPaciente.put(paciente.getId(), cita);
        }

        // Ordenar pacientes por prioridad usando MergeSort (menor número = mayor prioridad)
        ListaSimple<Paciente> pacientesOrdenados = MergeSort.sortByPrioridadAsc(pacientesDeCitas);

        if (pacientesOrdenados.isEmpty()) {
            mapResponse.put("error", "No hay pacientes disponibles para atender");
            mapResponse.put("code", 404);
            return mapResponse;
        }

        Paciente pacienteAAtender = pacientesOrdenados.get(0);
        Cita citaAAtender = citasPorPaciente.get(pacienteAAtender.getId());
        
        if (citaAAtender == null) {
            mapResponse.put("error", "No se encontró la cita asociada al paciente");
            mapResponse.put("code", 404);
            return mapResponse;
        }
        
        // Verificar que el paciente existe
        Optional<Paciente> optPaciente = pacienteRepository.findById(pacienteAAtender.getId());
        if (optPaciente.isEmpty()) {
            mapResponse.put("error", "El paciente seleccionado no existe en la base de datos");
            mapResponse.put("code", 404);
            return mapResponse;
        }
        
        pacienteAAtender = optPaciente.get();

        // Obtener médico de la cita o asignar uno disponible
        Medico medicoAsignado = citaAAtender.getMedicoAsignado();
        if (medicoAsignado == null || (medicoAsignado.getOcupado() != null && medicoAsignado.getOcupado())) {
            // Si el médico de la cita está ocupado o no tiene médico, asignar uno disponible
            medicoAsignado = medicosDisponibles.get(0);
        }
        
        medicoAsignado.setOcupado(true);
        medicoRepository.save(medicoAsignado);

        // Marcar paciente como en atención
        pacienteAAtender.setEnAtencion(true);
        pacienteRepository.save(pacienteAAtender);

        // Actualizar la cita existente a estado 'E' (En Atención)
        citaAAtender.setEstado('E'); // E = En Atención
        citaAAtender.setMedicoAsignado(medicoAsignado);
        if (citaAAtender.getMotivoConsulta() == null || citaAAtender.getMotivoConsulta().isEmpty()) {
            citaAAtender.setMotivoConsulta("Atención por prioridad: " + 
                (pacienteAAtender.getPrioridad() == 1 ? "Alta" : 
                 pacienteAAtender.getPrioridad() == 2 ? "Media" : "Baja"));
        }

        // Guardar cita actualizada en BD
        citaRepository.save(citaAAtender);

        // Crear variables finales para usar en el lambda
        final Integer pacienteId = pacienteAAtender.getId();
        final Integer citaId = citaAAtender.getId();
        final LocalDate fechaHoyFinal = fechaHoy;
        final Integer medicoId = medicoAsignado.getId();

        // Tiempo de espera fijo de 6 segundos
        int tiempoEspera = 6; // 6 segundos

        // Programar tarea para quitar al paciente de la lista después del timeout
        ScheduledFuture<?> tareaFutura = scheduler.schedule(() -> {
            try {
                // Obtener el paciente actualizado de la BD
                Optional<Paciente> optPacienteTimeout = pacienteRepository.findById(pacienteId);
                if (optPacienteTimeout.isEmpty()) {
                    tareasProgramadas.remove(citaId);
                    return;
                }
                
                Paciente paciente = optPacienteTimeout.get();

                // Verificar que la cita todavía existe y está en estado 'E'
                Optional<Cita> optCita = citaRepository.findById(citaId);
                if (optCita.isEmpty() || optCita.get().getEstado() != 'E') {
                    // La cita fue eliminada o ya finalizada, no hacer nada
                    tareasProgramadas.remove(citaId);
                    return;
                }

                Cita cita = optCita.get();
                
                // Finalizar la cita
                cita.setEstado('F'); // F = Finalizada
                citaRepository.save(cita);

                // Liberar al médico
                if (cita.getMedicoAsignado() != null) {
                    Optional<Medico> optMedico = medicoRepository.findById(medicoId);
                    if (optMedico.isPresent()) {
                        Medico medico = optMedico.get();
                        medico.setOcupado(false);
                        medicoRepository.save(medico);
                    }
                }

                // Verificar si el paciente tiene otras citas activas antes de marcarlo como disponible
                boolean tieneOtrasCitasActivas = false;
                for (Cita otraCita : citaRepository.findAll()) {
                    if (otraCita.getPaciente() != null && 
                        otraCita.getPaciente().getId().equals(pacienteId) &&
                        !otraCita.getId().equals(citaId)) {
                        Character estado = otraCita.getEstado();
                        LocalDate fechaCita = otraCita.getFecha();
                        // Verificar si tiene otra cita activa (P o E) para hoy
                        if ((estado == 'P' || estado == 'E') && 
                            fechaCita != null && fechaCita.equals(fechaHoyFinal)) {
                            tieneOtrasCitasActivas = true;
                            break;
                        }
                    }
                }

                // Solo marcar como no en atención si no tiene otras citas activas
                if (!tieneOtrasCitasActivas) {
                    paciente.setEnAtencion(false);
                    pacienteRepository.save(paciente);
                }

                // Agregar al historial
                pilaHistorialCitas.push(cita);
                arbolBusquedaHistorial.insert(cita);

                // Remover tarea del mapa
                tareasProgramadas.remove(citaId);
            } catch (Exception e) {
                System.err.println("Error al procesar timeout de atención: " + e.getMessage());
                e.printStackTrace();
                tareasProgramadas.remove(citaId);
            }
        }, tiempoEspera, TimeUnit.SECONDS);
        
        // Guardar la tarea programada
        tareasProgramadas.put(citaAAtender.getId(), tareaFutura);

        mapResponse.put("message", "Paciente en atención. Será removido automáticamente en " + tiempoEspera + " segundos.");
        mapResponse.put("cita", citaAAtender);
        mapResponse.put("paciente", pacienteAAtender);
        mapResponse.put("tiempoEspera", tiempoEspera);
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Agrega una cita existente a la cola de pendientes
     * @param id ID de la cita a agregar
     * @return mapa con la cita agregada o error si no se encuentra o no es válida
     */
    public CustomMap<String, Object> agregarCitaACola(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        Optional<Cita> optCita = citaRepository.findById(id);

        if (optCita.isEmpty()) {

            mapResponse.put("error", "Cita no encontrada con ID: " + id);
            mapResponse.put("code", 404);

            return mapResponse;

        }

        Cita cita = optCita.get();

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
     * Obtiene el historial de citas procesadas (LIFO - Pila)
     * @return mapa con la lista de citas en el historial
     */
    public CustomMap<String, Object> getHistorialCitas() {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        // Sincronizar la pila con la BD (por si hay citas finalizadas que no están en la pila)
        sincronizarHistorialConBD();

        // Obtener lista de la pila (LIFO - última en entrar, primera en salir)
        ListaSimple<Cita> listaHistorial = pilaHistorialCitas.toList();

        mapResponse.put("historialCitas", listaHistorial);
        mapResponse.put("tamaño", pilaHistorialCitas.size());
        mapResponse.put("isEmpty", pilaHistorialCitas.isEmpty());
        mapResponse.put("message", "Historial ordenado LIFO (Pila): la última cita procesada aparece primero");

        return mapResponse;
    }

    /**
     * Sincroniza la pila y el árbol de búsqueda con las citas finalizadas de la BD
     */
    private void sincronizarHistorialConBD() {
        // Limpiar y reconstruir desde la BD para asegurar que esté sincronizado
        pilaHistorialCitas.clear();
        arbolBusquedaHistorial.clear();
        
        // Obtener todas las citas finalizadas de la BD y agregarlas
        for (Cita cita : citaRepository.findAll()) {
            if (cita.getEstado() != null && cita.getEstado() == 'F') { // F = Finalizada
                pilaHistorialCitas.push(cita);
                arbolBusquedaHistorial.insert(cita);
            }
        }
        
        System.out.println("Historial sincronizado: " + pilaHistorialCitas.size() + " citas finalizadas");
    }

    /**
     * Obtiene la última cita procesada (la más reciente - LIFO)
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
     * Busca una cita en el historial por ID usando BinaryTree (búsqueda eficiente)
     * @param id ID de la cita a buscar
     * @return mapa con la cita encontrada o error si no existe
     */
    public CustomMap<String, Object> buscarCitaEnHistorial(Integer id) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (id == null) {
            mapResponse.put("error", "El ID no puede ser nulo");
            mapResponse.put("code", 400);
            return mapResponse;
        }

        // Sincronizar primero
        sincronizarHistorialConBD();

        // Buscar en el árbol (más eficiente que recorrer la pila)
        Cita citaEncontrada = arbolBusquedaHistorial.findById(id, Cita::getId);

        if (citaEncontrada == null) {
            mapResponse.put("error", "Cita no encontrada en el historial con ID: " + id);
            mapResponse.put("code", 404);
            return mapResponse;
        }

        mapResponse.put("cita", citaEncontrada);
        mapResponse.put("message", "Cita encontrada en el historial");
        mapResponse.put("code", 200);

        return mapResponse;
    }

    /**
     * Busca citas en el historial por nombre de paciente usando BinaryTree
     * El árbol binario se usa para obtener todas las citas ordenadas, luego se filtran por nombre
     * @param nombrePaciente Nombre o parte del nombre del paciente
     * @return mapa con las citas encontradas
     */
    public CustomMap<String, Object> buscarCitasPorPaciente(String nombrePaciente) {
        CustomMap<String, Object> mapResponse = new CustomMap<>();

        if (nombrePaciente == null || nombrePaciente.trim().isEmpty()) {
            mapResponse.put("error", "El nombre del paciente no puede estar vacío");
            mapResponse.put("code", 400);
            return mapResponse;
        }

        // Sincronizar primero
        sincronizarHistorialConBD();

        // Buscar en todas las citas del historial usando el árbol binario
        // El árbol binario ordena las citas por ID, luego filtramos por nombre
        ListaSimple<Cita> citasEncontradas = new ListaSimple<>();
        String nombreBusqueda = nombrePaciente.toLowerCase().trim();

        // Obtener todas las citas del árbol binario (más eficiente que recorrer la pila)
        ListaSimple<Cita> todasLasCitas = arbolBusquedaHistorial.toList();
        
        // Filtrar por nombre de paciente
        for (int i = 0; i < todasLasCitas.size(); i++) {
            Cita cita = todasLasCitas.get(i);
            if (cita.getPaciente() != null) {
                String nombreCompleto = (cita.getPaciente().getNombre() + " " + 
                                         cita.getPaciente().getApellido()).toLowerCase();
                if (nombreCompleto.contains(nombreBusqueda)) {
                    citasEncontradas.add(cita);
                }
            }
        }

        mapResponse.put("citasEncontradas", citasEncontradas);
        mapResponse.put("tamaño", citasEncontradas.size());
        mapResponse.put("terminoBusqueda", nombrePaciente);
        mapResponse.put("code", 200);

        return mapResponse;
    }
}