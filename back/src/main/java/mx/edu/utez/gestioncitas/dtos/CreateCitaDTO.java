package mx.edu.utez.gestioncitas.dtos;

import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Esta clase recibe los datos necesarios para crear una nueva cita en el sistema.
public class CreateCitaDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formateador de hora para compatibilidad en el frontend
    private LocalDate hora;

    private Paciente paciente;
    private Medico medicoAsignado;
    private String motivoConsulta;
    private Character estado; // 'P' = Programada, 'C' = Cancelada, 'F' = Finalizada, 'R' = Reagendada

    // Constructor vacío
    public CreateCitaDTO() {}

    // Getters y Setters
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
