package mx.edu.utez.gestioncitas.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un Médico en el sistema de gestión de citas médicas.
 * Contiene atributos como nombre, apellido, especialidad y número de consultorio.
 * Mapea a la tabla "medico" en la base de datos.
 * @author Tilines Crew
 */
@Entity
@Table(name = "medico")
public class Medico {

    // Atributos de un Médico
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false)
    private Integer numeroConsultorio;

    @Column(nullable = false)
    private Boolean ocupado = false;

    // Constructor vacío
    public Medico() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getNumeroConsultorio() {
        return numeroConsultorio;
    }

    public void setNumeroConsultorio(Integer numeroConsultorio) {
        this.numeroConsultorio = numeroConsultorio;
    }

    public Boolean getOcupado() {
        return ocupado;
    }

    public void setOcupado(Boolean ocupado) {
        this.ocupado = ocupado;
    }

    @Override
    public String toString() {
        return "\n--- DATOS DEL MÉDICO ---\n" +
                "  ID: " + id + "\n" +
                "  Nombre: " + nombre + "\n" +
                "  Apellido: " + apellido + "\n" +
                "  Especialidad: " + especialidad + "\n" +
                "  Número de Consultorio: " + numeroConsultorio + "\n" +
                "  Estado: " + (ocupado ? "Ocupado" : "Libre")+ "\n" +
                "--------------------------";
    }

}
