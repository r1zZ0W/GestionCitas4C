package mx.edu.utez.gestioncitas.dtos;

/**
 * DTO para crear un nuevo médico.
 * Contiene los atributos necesarios para la creación de un médico.
 * Proporciona métodos getters y setters para cada atributo.
 * Incluye un método toString para representar el objeto como una cadena.
 * @author Tilines Crew
 */
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

    @Override
    public String toString() {
        return "\n--- DATOS DEL MÉDICO ---\n" +
                "  Nombre: " + nombre + "\n" +
                "  Apellido: " + apellido + "\n" +
                "  Especialidad: " + especialidad + "\n" +
                "  Número de Consultorio: " + numeroConsultorio + "\n" +
                "--------------------------";
    }

}
