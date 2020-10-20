package gestpacientes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Clase que representa un objeto PacienteDAO con algunos metodos de utilidad
 * 
 * Contiene los siguientes atributos:
 * 
 * - con: Conexión con la base de datos
 * @author Adrián Romero Ramírez
 */
public class PacientesDAO {

    // Conexión con la base de datos
    private Connection con;
    
    /**
     * Constructor con parametros
     * @param con Conexión con la base de datos
     */
    public PacientesDAO(Connection con) {
        this.con=con;
    }
    
    /**
     * Método que inserta un paciente pasado por parametro en la base de datos
     * @param p paciente que se quiere insertar
     * @return true o false dependiendo si se ha insertado correctamente
     */
    public static boolean insert(Paciente p){
        boolean introducido = false;
        try {
            // Se comprueba el dni para ver si ya existe
            if(!comprobarDni(p.getDni())){
                Connection con = null;
                con = Aplicacion.conectar(con);
                // se crea una conexión
                // Se obtienen todoas los atributos del paciente
                String dni = p.getDni();
                String nombre = p.getNombre();
                String apellidos = p.getApellidos();
                String fechaNacimiento = Aplicacion.localDateToString(p.getFechaNacimiento());
                // Preparamos la consulta y la ejecutamos
                Statement s = con.createStatement();
                s.executeUpdate("INSERT INTO PACIENTES "
                        + "(dni, nombre, apellidos, fecha_nacimiento) values "
                        + "('"+dni+"', '"+nombre+"', '"+apellidos+"', '"+fechaNacimiento+"')");
                introducido = true; // Actualizamos introducido a true
            }else{
                // Si existe un paciento con el mismo dni sale este error
                System.out.println("ERROR --> No se pudo ejecutar la inserción del paciente. Ya existe un paciente con el mismo DNI.");
            }
        } catch (SQLException ex) {
            // Si existe algun problema con la conexión o la sentencia sale este error
            System.out.println("A surgido un error respecto a la base de datos.");
        }
        return introducido;
    }
    
    /**
     * Método que comprueba si el dni ya existe en la base de datos
     * @param dni dni que se quiere comprobar
     * @return true o false dependiendo si existe o no el dni
     */
    public static boolean comprobarDni(String dni){
        boolean existe = false;
        try {
            // Inicio una conexión con la base de datos y hago una consulta
            Connection con = null;
            con = Aplicacion.conectar(con);
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT dni FROM PACIENTES");
            
            // Compruebo el resultado de la consulta con el dni pasado por parametro
            while (rs.next()){
                if(rs.getString(1).equals(dni)){
                    // Si encuentra coincidencias, existe pasa a true
                    existe = true; 
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error en la consulta.");
        }
        return existe;
    }
    
    
    
    
    /**
     * Método que busca a todos los pacientes y los mete dentro de una List
     * @return la lista con todos los objetos pacientes
     */
    public static List<Paciente> findAll(){
        List<Paciente> lista = new LinkedList(); // Creo la lista
        try{
            // Establezco la conexión y ejecuto la consulta
            Connection con = null;
            con = Aplicacion.conectar(con);
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT dni, nombre, apellidos, fecha_nacimiento FROM PACIENTES");
            
            // Creo un bucle que reitere por cada resultado de la busqueda
            while (rs.next()){
                // Guardo los datos que me interesan en variables
                String dniRs = rs.getString(1);
                String nombreRs = rs.getString(2);
                String apellidosRs = rs.getString(3);
                String fechaNacimientoRs = rs.getString(4);
                // Añado en la lista pacientes creados con los datos de la consulta
                lista.add(new Paciente(dniRs, nombreRs, apellidosRs, fechaNacimientoRs));
            }
        } catch (SQLException ex) {
            // Si existe algun problema con la conexión o la sentencia sale este error
            System.out.println("A surgido un error respecto a la base de datos.");
        }
        return lista;
    }
    
    /**
     * Método que borra un paciente de la base de datos si coincide su dni con el dni pasado por parametro
     * @param dni del paciente que se quiere borrar
     * @return la cantidad de registros alterados
     */
    public static int deleteById(String dni){
        int numReg = 0;
        try{
            // Establezco la conexión y ejecuto la sentencia
            Connection con = null;
            con = Aplicacion.conectar(con);
            Statement s = con.createStatement();
            numReg = s.executeUpdate("DELETE FROM PACIENTES WHERE dni = '"+dni+"'");
            
        } catch (SQLException ex) {
            // Si existe algun problema con la conexión o la sentencia sale este error
            System.out.println("A surgido un error respecto a la base de datos.");
        }
        return numReg;
    }
    
    /**
     * Método que muestra todos los datos de un paciente mediante su dni
     * @param dni dni del paciente que queremos conocer los detalles
     * @return el paciente 
     */
    public static Paciente detallesPaciente(String dni){
        Paciente p = null;
        try{
            // Establezco la conexión y ejecuto la consulta
            Connection con = null;
            con = Aplicacion.conectar(con);
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("SELECT dni, nombre, apellidos, fecha_nacimiento, nhosp FROM PACIENTES WHERE dni = '"+dni+"'");
            
            while (rs.next()){
                // Guardo los datos que me interesan en variables
                String dniRs = rs.getString(1);
                String nombreRs = rs.getString(2);
                String apellidosRs = rs.getString(3);
                String fechaNacimientoRs = rs.getString(4);
                int nhospRs = rs.getInt(5);
                // Creo un paciente con los datos de la consulta
                p = new Paciente(dniRs, nombreRs, apellidosRs, fechaNacimientoRs);
                // Modifico el atributo nhosp ya que el constructor por defecto lo deja en 0
                p.setNhosp(nhospRs);
            }
        } catch (SQLException ex) {
            // Si existe algun problema con la conexión o la sentencia sale este error
            System.out.println("A surgido un error respecto a la base de datos.");
        }
        return p;
    }
    
    /**
     * Método que incrementa en 1 el número de hospitalizaciones del paciente que
     * se pasa el dni como parametro
     * @param dni del paciente que se quiere incrementar el nhosp
     * @return true o false si se ha podido incrementar el nhosp o no
     */
    public static boolean incrementarNhosp(String dni){
        boolean incrementado = false;
        try{
            // Establezco la conexión y ejecuto la sentencia
            Connection con = null;
            con = Aplicacion.conectar(con);
            Statement s = con.createStatement();
            int numReg = s.executeUpdate("UPDATE PACIENTES SET nhosp = nhosp+1 WHERE dni = '"+dni+"'");
            // Solo si se ha modificado algún registro "incrementado" cambia a true
            if(numReg!=0){
                incrementado = true;
            }
        } catch (SQLException ex) {
            // Si existe algun problema con la conexión o la sentencia sale este error
            System.out.println("A surgido un error respecto a la base de datos.");
        }
        return incrementado;
    }
}
