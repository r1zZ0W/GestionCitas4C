package mx.edu.utez.gestioncitas.controllers;


import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;
import mx.edu.utez.gestioncitas.dtos.CreateMedicoDTO;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.services.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/medico")
@CrossOrigin(origins = "*")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getAll() {

        Map<String, Object> mapResponse = medicoService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        Map<String, Object> mapResponse = medicoService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateMedicoDTO medico) {

        Medico newMedico = mapMedico(medico);
        Map<String, Object> mapResponse = medicoService.create(newMedico);

        System.out.println(newMedico);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody CreateMedicoDTO medico,
                                         @PathVariable Integer id) {

        Medico newMedico = mapMedico(medico);

        Map<String, Object> mapResponse = medicoService.update(id, newMedico);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        Map<String, Object> mapResponse = medicoService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    private Medico mapMedico(CreateMedicoDTO medico) {

        Medico newMedico = new Medico();

        newMedico.setNombre(medico.getNombre());
        newMedico.setApellido(medico.getApellido());
        newMedico.setEspecialidad(medico.getEspecialidad());
        newMedico.setNumeroConsultorio(medico.getNumeroConsultorio());

        return newMedico;

    }
}
