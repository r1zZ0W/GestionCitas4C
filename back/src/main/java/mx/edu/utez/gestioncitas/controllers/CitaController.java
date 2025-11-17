package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.services.CitaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("") // Petición GET para obtener todas las citas
    public ResponseEntity<Object> getAll() {

        Map<String, Object> mapResponse = citaService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}") // Petición GET para obtener una cita por ID
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        Map<String, Object> mapResponse = citaService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("") // Petición POST para crear una nueva cita
    public ResponseEntity<Object> create(@RequestBody CreateCitaDTO cita) {

        Cita newCita = mapCita(cita);
        Map<String, Object> mapResponse = citaService.create(newCita);

        System.out.println(newCita);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}") // Petición PUT para actualizar una cita existente
    public ResponseEntity<Object> update(@RequestBody CreateCitaDTO cita,
                                         @PathVariable Integer id) {

        Cita newCita = mapCita(cita);

        Map<String, Object> mapResponse = citaService.update(id, newCita);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Petición DELETE para eliminar una cita por ID
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        Map<String, Object> mapResponse = citaService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    // Método privado para mapear CreateCitaDTO a Cita
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

}
