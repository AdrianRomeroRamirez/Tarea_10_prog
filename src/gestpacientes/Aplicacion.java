package gestpacientes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.h2.tools.Server;
import util.ES;

/**
 * Clase principal del proyecto que establece conexión con la base de datos y 
 * la gestiona mediante métodos de esta clase y de otras clases del proyecto.
 * @author Adrián Romero Ramírez
 */
public class Aplicacion {

    private static final String dbname="pacientes.h2db";
    private static final String connectionURL="jdbc:h2:./"+dbname;
    private static final String driver="org.h2.Driver";
    private static final String cuParams=";MODE=MySQL;AUTO_RECONNECT=TRUE;COLLATION=SPANISH_SPAIN";
    
    
    // Dirección del archivo con la estructura para crear la tabla pacientes
    public final static String ESTRUCTURA_DB="/resources/structure.sql";
    // Dirección del el archivo donde está el diseño del logo
    public final static String APP_LOGO="/resources/logo.txt";
    // Dirección del archivo donde está el diseño del menú
    public final static String APP_MENU="/resources/menu.txt";
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // Comprobamos que se puede cargar correctamente el driver para poder conectarse
        // a la base de datos
        boolean driverCargado=false;
        try {
            Class.forName(driver);
            driverCargado=true;
        }
        catch (ClassNotFoundException e)
        {
            // En caso de error, nos muestra un mensaje
            System.err.printf("No se encuentra el driver de la base de datos (%s)\n",driver);
        }
        
        // Si se ha cargado correctamente, continua la ejecución
        if (driverCargado) {            
            String[] wsArgs={"-baseDir",System.getProperty("user.dir"),"-browser"};
            // Obtenemos la conexión
            try (Connection con = DriverManager.getConnection(connectionURL+cuParams,"","")) {
                // Se crea una conexión a un servidor web
                Server sr=Server.createWebServer(wsArgs);
                
                // Se inicia la conexión        
                sr.start();
                
                // Se muestra un mensaje por pantalla
                ES.msgln("¡¡Atención!!");                
                ES.msgln();
                ES.msgln("Mientras tu aplicación se esté ejecutando \n"
                        + "puedes acceder a la consola de la base de datos \n"
                        + "a través del navegador web.");    
                ES.msgln();
                ES.msgln("Página local: " + sr.getURL());     
                ES.msgln();
                ES.msgln("Datos de acceso");                
                ES.msgln("---------------");
                ES.msgln("Controlador: "+driver);
                ES.msgln("URL JDBC: "+connectionURL);
                ES.msgln("Usuario: (no indicar nada)");                
                ES.msgln("Password: (no indicar nada)");                
                
                // Se evalua si la creación de la tabla a sido correcta, y si es así
                // se continua dentro del if
                if (createTables(con)) {
                    // Se muestra por pantalla el logo
                    System.out.println(loadResourceAsString(APP_LOGO));
                    // Se crea un objeto de la clase gestionPacientes pasando por parametro
                    // la conexión a la base de datos
                    GestionPacientes gp=new GestionPacientes(con);
                    // Se mete dentro del metodo loop() de su clase hasta que se pulsa 4
                    gp.loop();                    
                } else {
                    // Si no se puede crear la tabla, da error
                    ES.msgln("Problema creando las tablas.");
                }
                
                /*
                * Se establece que no se muestre  el prefijo de obligatoriedad u
                * opcionalidad de una entrada de datos usando un metodo de la libreria "utilidades"
                */
                ES.setShowPromptPrefix(false);
                ES.leeCadena("Pulsa cualquier tecla para terminar (servidor web de la base de datos se cerrará)", true);
                // Se para la conexión con la base de datos
                sr.stop();
                sr.shutdown();
                
            } catch (SQLException ex) {
                // Si sucede algún error a la hora de conectar con la base de datos,
                // se muestra un mensaje
                System.err.printf("No se pudo conectar a la base de datos (%s)\n", dbname);
                ex.printStackTrace();
            }
        }

    }
    
    /**
     * Método que recibe como parametro la dirección de un archivo y los convierte en String
     * @param resourceName Dirección del archivo a convertir
     * @return String del archivo
     */
    public static String loadResourceAsString(String resourceName)
    {
        String resource=null;
        try (InputStream is=Aplicacion.class.getResourceAsStream(resourceName);
             InputStreamReader isr=new InputStreamReader(is);
             BufferedReader br=new BufferedReader(isr);)
        {
            resource=br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            System.err.printf("Problema leyendo el recurso como cadena: %S\n ",resourceName);
        }
        return resource;
    }
    
    /**
     * Método para crear la tabla pacientes en la base de datos
     * @param con Base de datos donde se debe crear la tabla
     * @return Verdadero o falso si se ha podido crear la tabla o no 
     */
    public static boolean createTables (Connection con)
    {
        boolean ok=false;
        
        try (Statement st=con.createStatement()) // Se prepara la consulta
        {                   
            // Se mete en un String la estructura de la tabla en sql
            String code=loadResourceAsString(ESTRUCTURA_DB);
            st.execute(code); // Se ejecuta la sentencia anterior           
            st.close(); // Se cierra la conexión
            ok=true; // ok pasa a true
        }
        catch (SQLException ex) {
            // Si la creación de la tabla da error, muestra un mensjae por pantalla
            // y ok se mantiene en false
            System.err.printf("Problema creando la estructura de la base de datos.");
            ex.printStackTrace();
        }
        return ok;        
    }        
            
    /**
     * Método que recibe como parametro una fecha en String y la devuelve en LocalDate
     * @param date Fecha que quieres convertir
     * @return Fecha convertida en LocalDate
     */   
    public static LocalDate stringToLocalDate(String date)
    {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Método que recibe como parametro una fecha en LocalDate y la devuelve en String
     * @param date Fecha que quieres convertir
     * @return Fecha convertida en String
     */
    public static String localDateToString(LocalDate date)
    {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Método que verifica que una fecha en String cuple con el formato deseado
     * @param date Fecha que se quiere verificar
     * @return true o false si la fecha es correcta o no
     */
    public static boolean verifyDate (String date)
    {
        DateTimeFormatter dtf=DateTimeFormatter.ISO_LOCAL_DATE;
        boolean res;
        try {
            dtf.parse(date);
            res=true;
        } catch (Exception e)
        {
            res=false;
        }
        return res;
    }
    
    /**
     * He creado un método para conectarse con la base de datos sin tener que
     * escribir de nuevo el código con la dirección url y demás
     * @param con conexión que se quiere realizar
     * @return la conexión ya realizada
     */
    public static Connection conectar(Connection con){
        try {
            con = DriverManager.getConnection(connectionURL+cuParams,"","");
        } catch (SQLException ex) {
            System.out.println("No se ha podido realizar la conexión.");
        }
        return con;
    }
}
