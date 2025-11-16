package mx.edu.utez.gestioncitas.services;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MedicoService {

    ListaSimple<Medico> listaMedicos = new ListaSimple<>();
    private int nextId = 1;

    public MedicoService() {

        Medico m = new Medico();



        listaMedicos.append(m);

    }

    public Map<String, Object> getAll() {

        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("listMedicos", listaMedicos.toList());

        return mapResponse;

    }

    public Map<String, Object> getById(int id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Medico medico = listaMedicos.findById(id, Medico::getId);

        if (medico == null) {
            mapResponse.put("error", "No se pudo encontrar el paciente");
            return mapResponse;
        }

        mapResponse.put("medico", medico);
        return mapResponse;
    }

    public Map<String, Object> create(Medico medico) {

        Map<String, Object> mapResponse = new HashMap<>();

        if(medico == null) {
            mapResponse.put("error", "Medico no puede ser nulo");
            return mapResponse;
        }

        medico.setId(nextId += 1);
        listaMedicos.append(medico);

        mapResponse.put("medico", medico);
        return mapResponse;
    }


    public Map<String, Object> update(int id, Medico medico) {
        Map<String, Object> mapResponse = new HashMap<>();

        Medico medicoUpdate = listaMedicos.findById(id, Medico::getId);

        if(medicoUpdate == null) {
            mapResponse.put("error", "Medico no encontrado");
            return mapResponse;
        }

        medicoUpdate.setId(medico.getId());
        medicoUpdate.setNombre(medico.getNombre());
        medicoUpdate.setApellido(medico.getApellido());
        medicoUpdate.setEspecialidad(medico.getEspecialidad());
        medicoUpdate.setNumeroConsultorio(medico.getNumeroConsultorio());

        mapResponse.put("medico", medicoUpdate);

        return mapResponse;

    }

    public Map<String, Object> delete(int id) {

        Map<String, Object> mapResponse = new HashMap<>();

        Medico medicoDelete = listaMedicos.findById(id, Medico::getId);

        if(medicoDelete == null) {
            mapResponse.put("error", "Medico no encontrado");
            return mapResponse;
        }

        listaMedicos.delete(medicoDelete);

        mapResponse.put("medico", "Medico eliminado correctamente");

        return mapResponse;
    }

}
