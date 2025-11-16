package mx.edu.utez.gestioncitas.dtos;

import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;

import java.time.LocalDate;

public class CreateCitaDTO {

    private LocalDate fecha;
    private LocalDate hora;
    private Paciente paciente;
    private Medico medicoAsignado;
    private String motivoConsulta;
    private Character estado;

    public CreateCitaDTO() {}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getHora() {
        return hora;
    }

    public void setHora(LocalDate hora) {
        this.hora = hora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedicoAsignado() {
        return medicoAsignado;
    }

    public void setMedicoAsignado(Medico medicoAsignado) {
        this.medicoAsignado = medicoAsignado;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public Character getEstado() {
        return estado;
    }

    public void setEstado(Character estado) {
        this.estado = estado;
    }
}
