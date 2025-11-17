package mx.edu.utez.gestioncitas.dtos;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Esta clase recibe los datos necesarios para crear un nuevo paciente en el sistema.
public class CreatePacienteDTO {

    // Atributos del paciente
    private String nombre;
    private String apellido;
    private String numeroTelefono;
    private String direccion;
    private String correoElectronico;
    private Character sexo;
    private Integer prioridad;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formateador de fecha para compatibilidad en el frontend
    private LocalDate fechaNacimiento;

    // Constructor vacío
    public CreatePacienteDTO() {}

    // Getters y Setters
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public Character getSexo() {
        return sexo;
    }

    public void setSexo(Character sexo) {
        this.sexo = sexo;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

}
