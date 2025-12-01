package mx.edu.utez.gestioncitas.dtos;

import mx.edu.utez.gestioncitas.model.Medico;
import mx.edu.utez.gestioncitas.model.Paciente;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para crear una nueva cita médica.
 * Contiene los datos necesarios para programar una cita, incluyendo fecha, hora,
 * paciente, médico asignado, motivo de consulta y estado de la cita.
 * Utiliza anotaciones de formateo de fecha y hora para asegurar la compatibilidad
 * con el frontend.
 * @author Tilines Crew
 */
public class CreateCitaDTO {

    /**
     * Atributos de la clase CreateCitaDTO
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    @DateTimeFormat(pattern = "HH:mm") // Formateador de hora para compatibilidad en el frontend
    private java.time.LocalTime hora;

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

    public java.time.LocalTime getHora() {
        return hora;
    }

    public void setHora(java.time.LocalTime hora) {
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

    @Override
    public String toString() {

        String estadoDescripcion = switch (estado) {
            case 'P' -> "Programada";
            case 'C' -> "Cancelada";
            case 'F' -> "Finalizada";
            case 'R' -> "Reagendada";
            default -> "Desconocido";
        };

        return "\n--- DATOS DE LA CITA ---\n" +
                "  Fecha: " + fecha + "\n" +
                "  Hora: " + hora + "\n" +
                "  Paciente: " + paciente.getNombre() + " " + paciente.getApellido() + " (ID: " + paciente.getId() + ")\n" +
                "  Médico Asignado: " + medicoAsignado.getNombre() + " " + medicoAsignado.getApellido() + " (" + medicoAsignado.getEspecialidad() + ")\n" +
                "  Motivo de Consulta: " + motivoConsulta + "\n" +
                "  Estado: " + estadoDescripcion + " (" + estado + ")\n" +
                "--------------------------";
    }

}
