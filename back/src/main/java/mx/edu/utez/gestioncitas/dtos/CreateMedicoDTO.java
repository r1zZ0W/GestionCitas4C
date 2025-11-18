package mx.edu.utez.gestioncitas.dtos;

// Esta clase recibe los datos necesarios para crear un nuevo médico en el sistema.
public class CreateMedicoDTO {

    // Atributos del médico
    private String nombre;
    private String apellido;
    private String especialidad;
    private Integer numeroConsultorio;

    // Constructor vacío
    public CreateMedicoDTO() {}

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

}
