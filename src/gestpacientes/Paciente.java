package gestpacientes;

import java.time.LocalDate;

/**
 * Clase que representa un objeto de la clase Paciente con algunos metodos para
 * obtener y cambiar sus atributos.
 * 
 * Contiene los siguientes atributos.
 * 
 * - dni: DNI del paciente
 * - nombre: Nombre del paciente
 * - appelidos: Apellidos del paciente
 * - fechaNaciemiento: Fecha de naciemiento del paciente
 * - nhosp: Número de hospitalizaciones
 * @author Adrián Romero Ramírez
 */
public class Paciente {
    
    // DNI del paciente
    private String dni; //    VARCHAR(10) NOT NULL PRIMARY KEY,
    // Nombre del paciente
    private String nombre; // VARCHAR(50) NOT NULL,
    // Apellidos del paciente
    private String apellidos; // VARCHAR(100) NOT NULL,
    // Fecha de naciemiento del paciente
    private LocalDate fechaNacimiento; // DATE NOT NULL, /* formato yyyy-MM-dd */
    // Número de hospitalizaciones
    private int nhosp; // INT DEFAULT 0, 
    
    /**
     * Constructor con parametros de la clase Paciente, nhosp no se introduce como parametro
     * y se pone por defecto a 0.
     * @param dni DNI del paciente
     * @param nombre Nombre del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de naciemiento del paciente con el tipo de dato "LocalDate"
     */
    public Paciente (String dni, String nombre, String apellidos, LocalDate fechaNacimiento)
    {
        this.dni=dni;
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.fechaNacimiento=fechaNacimiento;
        this.nhosp=0;
    }
    
    /**
     * Constructor con parametros de la clase Paciente, nhosp no se introduce como parametro
     * y se pone por defecto a 0.
     * @param dni DNI del paciente
     * @param nombre Nombre del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de naciemiento del paciente con el tipo de dato "String"
     */
    public Paciente (String dni, String nombre, String apellidos, String fechaNacimiento)
    {
        // Usa el constructor anterior pasando la fechaNacimiento de String a LocalDate
        this(dni, nombre,apellidos,Aplicacion.stringToLocalDate(fechaNacimiento));
    }

    /**
     * Método para obtener el dni del paciente
     * @return dni del paciente
     */
    public String getDni() {
        return dni;
    }

    /**
     * Método para obtener el nombre del paciente
     * @return Nombre del paciente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método para obtener los apellidos del paciente
     * @return Apellidos del paciente
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Método para obtener la fecha de nacimiento del paciente 
     * @return Fecha de nacimiento del paciente
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Método para obtener el número de hospitalizaciones del paciente
     * @return Número de hospitalizaciones del paciente
     */
    public int getNhosp() {
        return nhosp;
    }

    /**
     * Método para cambiar el nombre del paciente
     * @param nombre Nombre nuevo que se quiere poner
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método para cambiar los apellidos del paciente
     * @param apellidos Apellidos nuevos que se quieren poner
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Método para cambiar la fecha de nacimiento del paciente
     * @param fechaNacimiento Fecha nueva que se quiere poner
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Método para cambiar el número de hospitalizaciones del paciente
     * @param nhosp Número de hospitalizaciones nueva que se quiere poner
     */
    public void setNhosp(int nhosp) {
        this.nhosp = nhosp;
    }
    
    /**
     * Metodo que crea un String especifico para paciente
     * @return String especifico de paciente
     */
    @Override
    public String toString()
    {
        return "["+dni+"] "+nombre+" "+apellidos;
    }
    
}
