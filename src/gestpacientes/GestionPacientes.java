package gestpacientes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import util.ES;

/**
 * Clase que representa un objeto de la clase GestionPacientes con algunos métodos 
 * de su utilidad
 * 
 * Contiene los siguientes atributos:
 * 
 * - menu: String con el diseño del menú
 * - con: conexión con la base de dato
 * - PacientesDAO: objeto de la clase PacientesDAO
 * @author Adrián Romero Ramírez
 */
public class GestionPacientes {
    
    // String con el diseño del menú usando un metodo de la clase Aplication
    private final String menu=Aplicacion.loadResourceAsString(Aplicacion.APP_MENU);
    // Conexión con la base de datos, se inicializa mas tarde
    private Connection con=null;
    // Objeto de la clase PacientesDAO, se inicializa dentro del constructor
    private static PacientesDAO pacientesDAO;
    
    /**
     * Constructor con parametro de la clase
     * @param con Conexión con la base de datos
     * @throws IllegalArgumentException Por si la conexión con la base de datos da error
     */
    public GestionPacientes(Connection con) throws IllegalArgumentException {        
        
        try {
            // Si la conexión y el tiempo de respuesta es valido, se pasa el parametro 
            // introducido al atributo "con" del objeto
            if (con!=null && con.isValid(10))
            {
                this.con=con;
            }
        }
        catch (SQLException e) // Por si necesita capturar una excepción
        {
            this.con=null;
        }
        
        // Si no se ha podido iniciar la conexión y el atributo "con" sigue null
        // se lanza una excepción
        if (this.con==null)
        {
            throw new IllegalArgumentException("Conexión a la base de datos no válida.");
        }
        
        // Si tido sale bien, se crea un objeto PacienteDAO y se guarda en el atributo correspondiente
        pacientesDAO=new PacientesDAO(con);
    }     
    
    /**
     * Método que muestra el menú por pantalla
     */
    public void mostrarMenu()
    {
        Arrays.asList(menu.split("(\n|\r\n)")).forEach(ES::msgln);        
    }

    /**
     * Método que entra en un loop mostrando el menú y ejecutando metodos conforme 
     * se le valla pidiendo hasta que el usuario pulse 6
     */
    public void loop() {
        String opcion="";  
        // Mientras que opcion sea distinto a 5, se reitera el bucle
        while (!opcion.equalsIgnoreCase("6"))
        {
            // Se muestra el menú con el método mostrarMenu()
            mostrarMenu();
            // Con un switch hacemos que ejecute una parte del código u otra dependiendo
            // de la opción introducida
            switch(opcion=ES.preguntaOpcion("Introduzca una de las opciones:", true, false, "1","2","3","4","5","6"))
            {
                case "1": //Añadir paciente
                    añadirPacientes();
                    break;
                case "2": //Listar pacientes
                    listarPacientes();                      
                    break;
                case "3": //Borrar paciente
                    borrarPaciente();                       
                    break;
                case "4": //Mostrar detalles
                    mostrarDetalles();
                    break;
                case "5": //Incrementar nhosp
                    sumarNhosp();
                    break;
            }             
            
        }
    }
        
    /**
     * Método que pide al usuario todos los datos del paciente y los comprueba para
     * luego crear al paciente y llamar al metodo insert() de la clase PacientesDAO
     */
    public static void añadirPacientes(){
        
        boolean dniOk = false; // Comprobación del dni
        boolean fechaOk = false; // Comprobación del formato de fecha
        String dni = null; // Dni del paciente
        String fechaNacimiento = null; // Fecha de nacimiento del paciente
    
        // Mientras el dni no este en un formato correcto no sale del bucle
        while (!dniOk){
            dni = ES.leeCadena("Introduce el DNI:"); // Se lee el dni

            // Variables para la validación del dni
            String letrasValidas = "TRWAGMYFPDXBNJZSQVHLCKET";
            String patternNif = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$";
            String patternNie = "^[XYZ][0-9]{7}[TRWAGMYFPDXBNJZSQVHLCKET]$";

            // Si coincide con un dni o nif valido ntra en el if
            if(dni.matches(patternNif) || dni.matches(patternNie)){
                // Si coincide en concreto con un nif, se cambia la primera letra
                if(dni.matches(patternNie)){
                    dni = dni.replaceFirst("X", "0");
                    dni = dni.replaceFirst("Y", "1");
                    dni = dni.replaceFirst("Z", "2");
                }

                // Se divide el dni por partes
                int numero = Integer.parseInt(dni.substring(0, 8));
                char letra = dni.charAt(8);
                int resto = numero%23;

                // Se comprueba y se introduce el resultado en la variable
                dniOk = letra==letrasValidas.charAt(resto);
            }
            if(!dniOk){
                // Si no es correcto se muestra un mensaje
                System.out.println("--> ERROR: El dni introducido no es correcto.");
            }
        }
        
        String nombre = ES.leeCadena("Introduzca el nombre:"); // Se lee el nombre
        String appelidos = ES.leeCadena("Introduzca los apellidos:"); // Se lee los apellidos
        
        // mientras el formato de fecha no sea correcto no sale del bucle
        while (!fechaOk){
            // Se lee la fecha
            fechaNacimiento = ES.leeCadena("Introduce la fecha ( Ejemplo: 2019-04-22):");
            // Se comprueba con un metodo de la clase Aplicacion
            fechaOk = Aplicacion.verifyDate(fechaNacimiento);
            if (!fechaOk){
                // Si no es correcto se muestra un mensaje
                System.out.println("--> ERROR: La fecha debe tener un formato como el siguiente: 2019-04-22.");
            }
        }
        
        // Se pregunta si quiere continuar con la operación
        String respuesta = ES.preguntarSiONo("¿Seguro que desea continuar con la operación? (S o N) ");
        // Si es afirmativo se crea el paciente y se llama al metodo insert()
        if("S".equals(respuesta)){
            Paciente p = new Paciente(dni, nombre, appelidos, fechaNacimiento);
            if(pacientesDAO.insert(p)){
                System.out.println("Paciente guardado correctamente!");
            }
        }
        
        // PD: Me hubiera gustado hacer métodos aparte para las comprobaciones pero
        // al pedir explicitamente la tarea que se haga dentro del mismo método me ha quedado mas "sucio"
    }
    
    /**
     * Método que imprime por pantalla los datos Dni, nombre y apellidos de todos los pacientes
     */
    public static void listarPacientes(){
        // Creo una lista y meto todos los pacientes con el metodo findAll de la clase PacientesDAO
        List<Paciente> lista = pacientesDAO.findAll();
        
        // Por cada paciente imprimo por pantalla sus datos
        for (Paciente p : lista){
            System.out.println("["+p.getDni()+"] "+p.getNombre()+" "+p.getApellidos());
        }
    }

    /**
     * Método que llama a un metodo de la clase PacientesDAO para borrar un paciente
     * pasandole por parametro el dni que se le pide al usuario.
     */
    public static void borrarPaciente(){
        String dni = ES.leeCadena("Introduce el DNI del paciente a borrar (o nada para cancelar):", true);
        // Si la cadena no está vacia, se llama al codigo deleteById y muestra por pantalla
        // los registros eliminados
        if(dni!=null){ 
            int numDel = pacientesDAO.deleteById(dni);
            System.out.println("Se han eliminado "+numDel+" registros.");
        }
    }
    
    /**
     * Metodo que pide al usuario que introduzca el dni del paciente del que quiere
     * saber los detalles y los muestra por pantalla
     */
    public static void mostrarDetalles(){
        // Pide al usuario el dni del paciente
        String dni = ES.leeCadena("Introduce el DNI del paciente a mostrar detalles (o nada para cancelar):", true);
        // Si la cadena no está vacia, se llama al metodo detallesPaciente y muestra
        // los datos por pantalla
        if(dni!=null){ 
            Paciente p = pacientesDAO.detallesPaciente(dni);
            if(p!=null){
            System.out.println("["+p.getDni()+"] "+p.getNombre()+" "+p.getApellidos()+" "
                    + "\nFecha de nacimiento: "+p.getFechaNacimiento()+" "
                            + "\nNúmero de hospitalizaciones: "+p.getNhosp());
            }else{
                System.out.println("Paciente no encontrado");
            }
        }
    }
    
    /**
     * Método que suma 1 el número de hospitalizaciones del paciente cuyo dni 
     * introduce el usuario y avisa por pantalla del resultado
     */
    public static void sumarNhosp(){
        // Pide al usuario el dni del paciente
        String dni = ES.leeCadena("Introduce el DNI del paciente a incrementar el número de hospitalizaciones (o nada para cancelar):", true);
        if(pacientesDAO.incrementarNhosp(dni)){
            System.out.println("Número de hospitalizaciones incrementadas en 1.");
        } else {
            System.out.println("Paciente no encontrado");
        }
    }
}
