package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.dtos.CreatePacienteDTO;
import mx.edu.utez.gestioncitas.model.Paciente;
import mx.edu.utez.gestioncitas.services.PacienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getAll() {

        Map<String, Object> mapResponse = pacienteService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        Map<String, Object> mapResponse = pacienteService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreatePacienteDTO paciente) {

        Paciente newPaciente = mapPaciente(paciente);
        Map<String, Object> mapResponse = pacienteService.create(newPaciente);

        System.out.println(newPaciente);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody CreatePacienteDTO paciente,
                                         @PathVariable Integer id) {

        Paciente newPaciente = mapPaciente(paciente);

        Map<String, Object> mapResponse = pacienteService.update(id, newPaciente);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        Map<String, Object> mapResponse = pacienteService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    private Paciente mapPaciente(CreatePacienteDTO paciente) {

        Paciente newPaciente = new Paciente();

        newPaciente.setNombre(paciente.getNombre());
        newPaciente.setApellido(paciente.getApellido());
        newPaciente.setSexo(paciente.getSexo());
        newPaciente.setCorreoElectronico(paciente.getCorreoElectronico());
        newPaciente.setNumeroTelefono(paciente.getNumeroTelefono());
        newPaciente.setDireccion(paciente.getDireccion());
        newPaciente.setFechaNacimiento(paciente.getFechaNacimiento());

        return newPaciente;

    }

}