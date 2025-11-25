package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreatePacienteDTO;
import mx.edu.utez.gestioncitas.model.Paciente;
import mx.edu.utez.gestioncitas.services.PacienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("") // Petición GET para obtener todos los pacientes
    public ResponseEntity<Object> getAll() {

        CustomMap<String, Object> mapResponse = pacienteService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}") // Petición GET para obtener un paciente por ID
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("") // Petición POST para crear un nuevo paciente
    public ResponseEntity<Object> create(@RequestBody CreatePacienteDTO paciente) {

        CustomMap<String, Object> mapResponse = pacienteService.create(paciente);

        System.out.println(paciente);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}") // Petición PUT para actualizar un paciente existente
    public ResponseEntity<Object> update(@RequestBody CreatePacienteDTO paciente,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.update(id, paciente);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Petición DELETE para eliminar un paciente por ID
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = pacienteService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

}