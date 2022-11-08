package conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/premiacion?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
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

    public static Connection conMySQL() {

        //Cargo el driver de MySQL
        //y controlo las excepciones
        try {
            Class.forName(getDriver());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver");
            System.out.println(e.getMessage());
        }
        // Establezco la conexión con la base de datos MySQL
        // y controlo las excepciones
        Connection con = null;
        try {
            //Guardo la conexión en la variable con
            con = DriverManager.getConnection(getURL(), getUser(), getPassword());
            System.out.println("Conexión establecida con MySQL");
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión");
            System.out.println(e.getMessage());
        }
        return con;
    }
}

