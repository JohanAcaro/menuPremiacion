package conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionPSQL {
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/premiacion";
    private static final String USER = "postgres";
    private static final String PASSWORD = "curso";

    public static String getDriver() {
        return DRIVER;
    }
    public static String getURL() {
        return URL;
    }
    public static String getUser() {
        return USER;
    }
    public static String getPassword() {
        return PASSWORD;
    }

    public static Connection conPSQL() {
        //Cargo el driver de PostgreSQL
        //y controlo las excepciones
        try {
            Class.forName(getDriver());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver");
            System.out.println(e.getMessage());
        }
        // Establezco la conexión con la base de datos PostgreSQL
        // y controlo las excepciones
        Connection con = null;
        try {
            //Guardo la conexión en la variable con
            con = DriverManager.getConnection(getURL(), getUser(), getPassword());
            System.out.println("Conexión establecida con PostgreSQL");
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión");
            System.out.println(e.getMessage());
        }
        return con;
    }
}
