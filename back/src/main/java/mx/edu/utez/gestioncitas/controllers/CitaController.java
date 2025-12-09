package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.services.CitaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar las operaciones relacionadas con las citas.
 * Proporciona endpoints para crear, leer, actualizar y eliminar citas.
 * Utiliza CitaService para la lógica de negocio y CustomMap para las respuestas personalizadas.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Tilines Crew
 */
@RestController
@RequestMapping("/api/cita")
@CrossOrigin(origins = "*")
public class CitaController {

    // Servicio de Cita inyectado
    private final CitaService citaService;

    // Constructor para inyectar el servicio
    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /**
     * Obtiene todas las citas mediante una petición GET
     * Mediante un mapa personalizado se devuelve la lista de citas por la inyección del servicio
     * @return ResponseEntity con la lista de citas y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {

        CustomMap<String, Object> mapResponse = citaService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    /**
     * Obtiene una cita por su ID mediante una petición GET
     * @param id ID de la cita a buscar
     * @return ResponseEntity con la cita encontrada y el estado HTTP
     */
    @GetMapping("/{id}") // Petición GET para obtener una cita por ID
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Crea una nueva cita mediante una petición POST
     * @param cita DTO con los datos de la nueva cita
     * @return ResponseEntity con la respuesta de la creación y el estado HTTP
     */
    @PostMapping("") // Petición POST para crear una nueva cita
    public ResponseEntity<Object> create(@RequestBody CreateCitaDTO cita) {

        CustomMap<String, Object> mapResponse = citaService.create(cita);

        System.out.println(cita);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    /**
     * Actualiza una cita existente mediante una petición PUT
     * @param cita DTO con los datos actualizados de la cita
     * @param id ID de la cita a actualizar
     * @return ResponseEntity con la respuesta de la actualización y el estado HTTP
     */
    @PutMapping("/{id}") // Petición PUT para actualizar una cita existente
    public ResponseEntity<Object> update(@RequestBody CreateCitaDTO cita,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.update(id, cita);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Elimina una cita por su ID mediante una petición DELETE
     * @param id ID de la cita a eliminar
     * @return ResponseEntity con la respuesta de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}") // Petición DELETE para eliminar una cita por ID
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }


    /**
     * Obtiene la cola de citas pendientes
     * @return ResponseEntity con la cola de citas pendientes y el estado HTTP
     */
    @GetMapping("/cola/pendientes")
    public ResponseEntity<Object> getColaCitasPendientes() {
        CustomMap<String, Object> mapResponse = citaService.getColaCitasPendientes();
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Obtiene la siguiente cita pendiente en la cola
     * @return ResponseEntity con la siguiente cita pendiente y el estado HTTP
     */
    @GetMapping("/cola/siguiente")
    public ResponseEntity<Object> getSiguienteCitaPendiente() {
        CustomMap<String, Object> mapResponse = citaService.getSiguienteCitaPendiente();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Atiende la siguiente cita en la cola
     * @return ResponseEntity con la cita atendida y el estado HTTP
     */
    @PostMapping("/cola/atender")
    public ResponseEntity<Object> atenderSiguienteCita() {
        CustomMap<String, Object> mapResponse = citaService.atenderSiguienteCita();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Agrega una cita a la cola de pendientes
     * @param id ID de la cita a agregar
     * @return ResponseEntity con la respuesta de la operación y el estado HTTP
     */
    @PostMapping("/cola/agregar/{id}")
    public ResponseEntity<Object> agregarCitaACola(@PathVariable Integer id) {
        CustomMap<String, Object> mapResponse = citaService.agregarCitaACola(id);
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Obtiene el historial de citas atendidas
     * @return ResponseEntity con el historial de citas y el estado HTTP
     */
    @GetMapping("/pila/historial")
    public ResponseEntity<Object> getHistorialCitas() {
        CustomMap<String, Object> mapResponse = citaService.getHistorialCitas();
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Obtiene la última cita procesada
     * @return ResponseEntity con la última cita procesada y el estado HTTP
     */
    @GetMapping("/pila/ultima")
    public ResponseEntity<Object> getUltimaCitaProcesada() {
        CustomMap<String, Object> mapResponse = citaService.getUltimaCitaProcesada();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Atiende un paciente por prioridad (lo marca como en atención y lo quita después de un tiempo)
     * @return ResponseEntity con la cita creada y el estado HTTP
     */
    @PostMapping("/atender/prioridad")
    public ResponseEntity<Object> atenderPacientePorPrioridad() {
        CustomMap<String, Object> mapResponse = citaService.atenderPacientePorPrioridad();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Busca una cita en el historial por ID usando BinaryTree
     * @param id ID de la cita a buscar
     * @return ResponseEntity con la cita encontrada y el estado HTTP
     */
    @GetMapping("/historial/buscar/{id}")
    public ResponseEntity<Object> buscarCitaEnHistorial(@PathVariable Integer id) {
        CustomMap<String, Object> mapResponse = citaService.buscarCitaEnHistorial(id);
        
        if (mapResponse.containsKey("error")) {
            HttpStatus status = (Integer) mapResponse.get("code") == 404 ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(mapResponse, status);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Busca citas en el historial por nombre de paciente
     * @param nombre Nombre del paciente a buscar
     * @return ResponseEntity con las citas encontradas y el estado HTTP
     */
    @GetMapping("/historial/buscar/paciente")
    public ResponseEntity<Object> buscarCitasPorPaciente(@RequestParam String nombre) {
        CustomMap<String, Object> mapResponse = citaService.buscarCitasPorPaciente(nombre);
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }
}
