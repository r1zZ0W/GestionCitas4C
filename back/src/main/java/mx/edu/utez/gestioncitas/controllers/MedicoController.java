package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreateMedicoDTO;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.services.MedicoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("") // Petición GET para obtener todos los médicos
    public ResponseEntity<Object> getAll() {

        CustomMap<String, Object> mapResponse = medicoService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}") // Petición GET para obtener un médico por ID
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("") // Petición POST para crear un nuevo médico
    public ResponseEntity<Object> create(@RequestBody CreateMedicoDTO medico) {

        CustomMap<String, Object> mapResponse = medicoService.create(medico);

        System.out.println(medico);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}") // Petición PUT para actualizar un médico existente
    public ResponseEntity<Object> update(@RequestBody CreateMedicoDTO medico,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.update(id, medico);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Petición DELETE para eliminar un médico por ID
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = medicoService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

}
