package mx.edu.utez.gestioncitas.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una Cita en el sistema de gestión de citas médicas.
 * Contiene información sobre la fecha, hora, paciente, médico asignado,
 * motivo de consulta y estado de la cita.
 * @author Tilines Crew
 */
@Entity
@Table(name = "cita")
public class Cita {

    // Atributos de una Cita
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    private LocalDate fecha;

    @DateTimeFormat(pattern = "HH:mm") // formateadores de fecha pa compatibilidad con el front-end
    @Column(name = "hora", nullable = false, columnDefinition = "TIME")
    private LocalTime hora;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medicoAsignado;

    @Column(length = 255)
    private String motivoConsulta;

    @Column(length = 1)
    private Character estado; // 'P' = Programada, 'C' = Cancelada, 'F' = Finalizada, 'R' = Reagendada, 'E' = En Atención

    //Constructor vacío
    public Cita() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
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

    // toString para pruebas y que se vean los datos vaya
    @Override
    public String toString() {

        String estadoDescripcion = switch (estado) {
            case 'P' -> "Programada";
            case 'C' -> "Cancelada";
            case 'F' -> "Finalizada";
            case 'R' -> "Reagendada";
            case 'E' -> "En Atención";
            default -> "Desconocido";
        };

        return "\n--- DATOS DE LA CITA ---\n" +
                "  ID: " + id + "\n" +
                "  Fecha: " + fecha + "\n" +
                "  Hora: " + hora + "\n" +
                "  Paciente: " + paciente.getNombre() + " " + paciente.getApellido() + " (ID: " + paciente.getId() + ")\n" +
                "  Médico Asignado: " + medicoAsignado.getNombre() + " " + medicoAsignado.getApellido() + " (" + medicoAsignado.getEspecialidad() + ")\n" +
                "  Motivo de Consulta: " + motivoConsulta + "\n" +
                "  Estado: " + estadoDescripcion + " (" + estado + ")\n" +
                "--------------------------";
    }

}
