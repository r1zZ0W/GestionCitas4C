package mx.edu.utez.gestioncitas.controllers;

import mx.edu.utez.gestioncitas.data_structs.CustomMap;
import mx.edu.utez.gestioncitas.dtos.CreateCitaDTO;
import mx.edu.utez.gestioncitas.model.Cita;
import mx.edu.utez.gestioncitas.services.CitaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        CustomMap<String, Object> mapResponse = citaService.getAll();

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}") // Petición GET para obtener una cita por ID
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.getById(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("") // Petición POST para crear una nueva cita
    public ResponseEntity<Object> create(@RequestBody CreateCitaDTO cita) {

        CustomMap<String, Object> mapResponse = citaService.create(cita);

        System.out.println(cita);

        return new ResponseEntity<>(mapResponse, HttpStatus.CREATED);

    }

    @PutMapping("/{id}") // Petición PUT para actualizar una cita existente
    public ResponseEntity<Object> update(@RequestBody CreateCitaDTO cita,
                                         @PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.update(id, cita);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Petición DELETE para eliminar una cita por ID
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        CustomMap<String, Object> mapResponse = citaService.delete(id);

        return new ResponseEntity<>(mapResponse, HttpStatus.OK);

    }

    @GetMapping("/cola/pendientes")
    public ResponseEntity<Object> getColaCitasPendientes() {
        CustomMap<String, Object> mapResponse = citaService.getColaCitasPendientes();
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @GetMapping("/cola/siguiente")
    public ResponseEntity<Object> getSiguienteCitaPendiente() {
        CustomMap<String, Object> mapResponse = citaService.getSiguienteCitaPendiente();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("/cola/atender")
    public ResponseEntity<Object> atenderSiguienteCita() {
        CustomMap<String, Object> mapResponse = citaService.atenderSiguienteCita();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("/cola/agregar/{id}")
    public ResponseEntity<Object> agregarCitaACola(@PathVariable Integer id) {
        CustomMap<String, Object> mapResponse = citaService.agregarCitaACola(id);
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @GetMapping("/pila/historial")
    public ResponseEntity<Object> getHistorialCitas() {
        CustomMap<String, Object> mapResponse = citaService.getHistorialCitas();
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @GetMapping("/pila/ultima")
    public ResponseEntity<Object> getUltimaCitaProcesada() {
        CustomMap<String, Object> mapResponse = citaService.getUltimaCitaProcesada();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

    @PostMapping("/pila/revertir")
    public ResponseEntity<Object> revertirUltimaCita() {
        CustomMap<String, Object> mapResponse = citaService.revertirUltimaCita();
        
        if (mapResponse.containsKey("error")) {
            return new ResponseEntity<>(mapResponse, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(mapResponse, HttpStatus.OK);
    }

}
