package mx.edu.utez.gestioncitas.model;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class Paciente {

    // Atributos de un Paciente
    private Integer id;
    private String nombre;
    private String apellido;
    private String numeroTelefono;
    private String direccion;
    private String correoElectronico;
    private Character sexo; // por favor
    private Integer prioridad; // 1 = Alta, 2 = Media, 3 = Baja

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formateador de fecha para compatibilidad con el front-end
    private LocalDate fechaNacimiento;
    private ListaSimple<Cita> citas = new ListaSimple<>();

    // Constructor vacío
    public Paciente() {}

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

    public ListaSimple<Cita> getCitas() {
        return citas;
    }

    public void setCitas(ListaSimple<Cita> citas) {
        this.citas = citas;
    }

    // Metodo toString para pruebas xd
    @Override
    public String toString() {
        return "--- DATOS DEL PACIENTE ---\n" +
                "  ID: " + id + "\n" +
                "  Nombre: " + nombre + "\n" +
                "  Apellido: " + apellido + "\n" +
                "  Fecha de Nacimiento: " + fechaNacimiento + "\n" +
                "  Teléfono: " + numeroTelefono + "\n" +
                "  Dirección: " + direccion + "\n" +
                "  Correo Electrónico: " + correoElectronico + "\n" +
                "  Sexo: " + sexo + "\n" +
                "  Prioridad: " + prioridad + "\n" +
                "--------------------------";
    }
}
