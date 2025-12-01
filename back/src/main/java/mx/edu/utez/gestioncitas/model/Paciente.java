package mx.edu.utez.gestioncitas.model;

import jakarta.persistence.*;

import mx.edu.utez.gestioncitas.data_structs.ListaSimple;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Entidad que representa a un Paciente en el sistema de gestión de citas médicas.
 * Mapea la tabla "paciente" en la base de datos.
 * Contiene atributos como nombre, apellido, fecha de nacimiento, contacto, sexo y prioridad.
 * Además, mantiene una lista de citas asociadas al paciente.
 * @author Tilines Crew
 */
@Entity
@Table(name = "paciente")
public class Paciente {

    // Atributos de un Paciente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;

    @Column(length = 255)
    private String direccion;

    @Column(name = "correo_electronico", length = 100)
    private String correoElectronico;

    @Column(length = 1)
    private Character sexo; // por favor

    @Column
    private Integer prioridad; // 1 = Alta, 2 = Media, 3 = Baja

    @Column(name = "fecha_nacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formateador de fecha para compatibilidad con el front-end
    private LocalDate fechaNacimiento;

    @Transient // No se mapea en la base de datos
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
        return "\n--- DATOS DEL PACIENTE ---\n" +
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
