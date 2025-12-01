package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreateMedicoDTO;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.services.MedicoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar las operaciones CRUD de los médicos.
 * Proporciona endpoints para crear, leer, actualizar y eliminar médicos.
 * Utiliza MedicoService para la lógica de negocio.
 * Responde con objetos ResponseEntity que contienen un CustomMap con los resultados.
 * Soporta solicitudes CORS desde cualquier origen.
 * @author Tilines Crew
 */
@RestController
@RequestMapping("/api/medico")
@CrossOrigin(origins = "*")
public class MedicoController {

    // Servicio de Médico inyectado
    private final MedicoService medicoService;

    // Constructor para inyectar el servicio
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    /**
     * Obtiene todos los médicos desde la base de datos.
     * @return ResponseEntity con un CustomMap que contiene la lista de médicos y el estado HTTP 200 OK.
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {

        CustomMap<String, Object> mapResponse = medicoService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    /**
     * Obtiene un médico por su ID.
     * @param id ID del médico a obtener.
     * @return ResponseEntity con un CustomMap que contiene el médico encontrado y el estado HTTP 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Crea un nuevo médico en la base de datos.
     * @param medico DTO con los datos del médico a crear.
     * @return ResponseEntity con un CustomMap que contiene el médico creado y el estado HTTP 201 Created.
     */
    @PostMapping("") // Petición POST para crear un nuevo médico
    public ResponseEntity<Object> create(@RequestBody CreateMedicoDTO medico) {

        CustomMap<String, Object> mapResponse = medicoService.create(medico);

        System.out.println(medico);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    /**
     * Actualiza un médico existente en la base de datos.
     * @param medico DTO con los datos del médico a actualizar.
     * @param id ID del médico a actualizar.
     * @return ResponseEntity con un CustomMap que contiene el médico actualizado y el estado HTTP 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody CreateMedicoDTO medico,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.update(id, medico);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    /**
     * Elimina un médico por su ID.
     * @param id ID del médico a eliminar.
     * @return ResponseEntity con un CustomMap que contiene el resultado de la eliminación y el estado HTTP 200 OK.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

}
