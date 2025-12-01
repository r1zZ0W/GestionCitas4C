package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreatePacienteDTO;
import mx.edu.utez.gestioncitas.services.PacienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los pacientes.
 * Proporciona endpoints para crear, leer, actualizar y eliminar pacientes.
 * Utiliza PacienteService para la lógica de negocio y CustomMap para las respuestas personalizadas.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Tilines Crew
 */
@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*")
public class PacienteController {

    // Servicio de Paciente inyectado
    private final PacienteService pacienteService;

    // Constructor para inyectar el servicio
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Obtiene todos los pacientes mediante una petición GET
     * Mediante un mapa personalizado se devuelve la lista de pacientes por la inyección del servicio
     * @return ResponseEntity con la lista de pacientes y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {

        CustomMap<String, Object> mapResponse = pacienteService.getAll();
        // Obtener el código de estado del mapa de respuesta
        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, getCode(code));

    }

    /**
     * Obtiene un paciente por su ID mediante una petición GET
     * @param id ID del paciente a buscar
     * @return ResponseEntity con el paciente encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.getById(id);
        // Obtener el código de estado del mapa de respuesta
        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, getCode(code));
    }

    /**
     * Crea un nuevo paciente mediante una petición POST
     * @param paciente Datos del paciente a crear
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreatePacienteDTO paciente) {

        CustomMap<String, Object> mapResponse = pacienteService.create(paciente);

        System.out.println(paciente);

        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, code == 201 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);

    }

    /**
     * Actualiza un paciente existente mediante una petición PUT
     * @param paciente Datos del paciente a actualizar
     * @param id ID del paciente a actualizar
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody CreatePacienteDTO paciente,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.update(id, paciente);
        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, getCode(code));
    }

    /**
     * Elimina un paciente por su ID mediante una petición DELETE
     * @param id ID del paciente a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.delete(id);
        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, getCode(code));

    }

    /**
     * Obtiene todos los pacientes ordenados por prioridad ascendente mediante una petición GET
     * @return ResponseEntity con la lista de pacientes ordenados y el estado HTTP
     */
    @GetMapping("/prioridad/asc")
    public ResponseEntity<Object> getPrioridadAsc() {

        CustomMap<String, Object> mapResponse = pacienteService.getAllPrioridadAsc();
        int code = (int) mapResponse.get("code");

        return new ResponseEntity<>(mapResponse, getCode(code));

    }

    /**
     * HELPER -
     * Convierte un código entero a HttpStatus rescatado del mapa de respuesta a un HttpStatus
     * @param code Código entero rescatado del mapa de respuesta
     * @return HttpStatus correspondiente al código entero
     */
    private HttpStatus getCode(int code) {

        if (code == 200) return HttpStatus.OK;
        if (code == 201) return HttpStatus.CREATED;
        if (code == 400) return HttpStatus.BAD_REQUEST;
        if (code == 404) return HttpStatus.NOT_FOUND;

        return HttpStatus.INTERNAL_SERVER_ERROR;

    }

}
