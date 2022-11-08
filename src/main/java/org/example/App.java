package org.example;

import conexiones.ConexionMySQL;
import conexiones.ConexionPSQL;

import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static int opcion;
    private static int entrada;
    private static Connection con;

    public static void main(String[] args ) throws SQLException, IOException, InputMismatchException {

        //Instancio la clase Scanner para leer datos desde el teclado
        Scanner lector = new Scanner(System.in);
        //El delimitador de los datos es el salto de línea
        lector.useDelimiter("\n");

        //Bucle para mostrar el menú hasta que el usuario elija una de las 3 opciones
        do {
            //Menú de opciones
            System.out.println("\n-----------MENU - SISTEMAS DE GESTIÓN DE BASES DE DATOS-----------");
            System.out.println("1. MySQL");
            System.out.println("2. PostgreSQL");
            System.out.println("3. Salir");
            System.out.println("¿Qué base de datos desea utilizar?");
            //Leo la opción del usuario

            opcion = lector.nextInt();

            //Creo el switch para seleccionar la base de datos
            switch (opcion) {
                case 1 -> {
                    //Si el usuario eligió MySQL
                    //asigno a la variable con la conexión
                    con = ConexionMySQL.conMySQL();
                }
                case 2 -> {
                    //Si el usuario eligió PostgreSQL
                    //asigno a la variable con la conexión
                    con = ConexionPSQL.conPSQL();

                }
                case 3 -> {
                    //Si el usuario eligió salir
                    //cierro la conexión
                    System.out.println("Hasta luego!");
                    System.exit(0);
                }
                //Si el usuario no eligió ninguna opción válida
                default -> System.out.println("Opción no válida");
            }

        }while (opcion != 1 && opcion != 2 && opcion != 3);



        //Interactuo con la BD

        //Controlo que se realizan correctamente las sentencias
        try {

            //Creo la variable sentencia
            //createStatement() crea un objeto Statement para enviar instrucciones SQL a la base de datos
            assert con != null;
            Statement sentencia = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //Bucle do while para que el usuario pueda elegir entre las opciones del menú

            do {
                //Menú de opciones
                System.out.println("-----------MENU - BALÓN DE ORO-----------");
                System.out.println("1. Añadir nuevo jugador");
                System.out.println("2. Clasificación");
                System.out.println("3. Modificar un jugador");
                System.out.println("4. Borrar un jugador");
                System.out.println("5. Importar CSV");
                System.out.println("6. Exportar CSV");
                System.out.println("7. Terminar");
                System.out.println("¿Que quieres hacer?");
                //Leo la entrada del usuario
                try {
                    entrada = lector.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Debe ingresar un número");

                }
                //Creo el switch para seleccionar la opción del menú - balón de oro
                switch (entrada) {
                    case 1 -> {
                        caso1(lector, sentencia);
                    }
                    case 2 -> {
                        caso2(sentencia);
                    }
                    case 3 -> {
                        caso3(lector, sentencia, con);
                    }
                    case 4 -> {
                        caso4(lector, sentencia, con);
                    }
                    case 5 -> {
                        caso5(lector);
                    }
                    case 6 -> {
                        caso6(lector, con);
                    }
                    case 7 -> {
                        limpiar(15);
                        System.out.println("7. Terminar");
                    }
                    default -> System.out.println("Opción no válida");
                }

            } while (entrada != 7);


        } catch (SQLException e) {
            System.out.println("Error al realizar el listado de jugadores");
            System.out.println(e.getMessage());
        }


        //Cierro la conexión y
        //controlo que se cierre la conexión con la base de datos
        try {
            System.out.println("Hasta luego!");
            System.out.println("Cerrando conexión...");
            con.close();
        } catch (SQLException e) {
            System.out.println("No se ha podido cerrar la conexión con la BD");
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Se ha cerrado la base de datos");

    }

    //1. Añadir nuevo jugador
    private static void caso1(Scanner lector, Statement sentencia) throws SQLException {
        ResultSet rs;
        ResultSet rs2;

        //Añadir nuevo jugador
        limpiar(15);

        System.out.println("1. Añadir nuevo jugador");
        System.out.println("Introduce el nombre del jugador");

        //Leo el nombre del jugador
        String nombre = lector.next();

        //Muestro los equipos disponibles
        System.out.println("\n--------------EQUIPOS--------------");
        //Creo la sentencia SQL
        rs = sentencia.executeQuery("SELECT * FROM EQUIPOS");
        //Llamada al método mostrarEquipos
        mostrarEquipos(rs);
        System.out.println("\nIntroduce el codigo del equipo");
        System.out.println("Si no existe el equipo, introduce 0");

        //Leo el código del equipo
        String codigoEquipo = lector.next();

        //Si el código del equipo es 0
        //se crea un nuevo equipo
        //y se guarda el código del equipo
        //Llamada al método solicitarNuevoEquipo
        if (codigoEquipo.equals("0")) {
            codigoEquipo=solicitarNuevoEquipo(lector, sentencia);
        }

        //Vuelvo a la ejecución de añadir jugador
        System.out.println("Introduce el número de votos");
        //Leo el número de votos
        int votos = lector.nextInt();

        //Creo la sentencia SQL
        rs2 = sentencia.executeQuery("SELECT * FROM balon_oro");
        //Llamada al método insertarJugador
        insertarJugador(rs2, nombre, codigoEquipo, votos);
    }

    //2. Clasificación
    private static void caso2(Statement sentencia) throws SQLException {
        ResultSet rs2;

        //Clasificación
        limpiar(15);

        System.out.println("2. Clasificación");
        //Creo la sentencia SQL
        rs2 = sentencia.executeQuery("SELECT * FROM balon_oro ORDER BY id ASC");
        //Llamada al método mostrarClasificacion
        mostrarClasificacion(rs2);
    }

    //3. Modificar un jugador
    private static void caso3(Scanner lector, Statement sentencia, Connection con) throws SQLException {
        //Modificar un jugador
        ResultSet rs;
        ResultSet rs2;
        ResultSet rs3;
        PreparedStatement ps;

        limpiar(15);
        System.out.println("3. Modificar un jugador");
        //Creo la sentencia SQL
        rs2 = sentencia.executeQuery("SELECT * FROM balon_oro ORDER BY id ASC");
        //Llamada al método mostrarClasificacion
        mostrarClasificacion(rs2);
        System.out.println("Introduce el id del jugador que quieres modificar");
        //Leo el ID del jugador
        int id = lector.nextInt();
        //Creo la sentencia SQL
        rs3 = sentencia.executeQuery("SELECT * FROM balon_oro WHERE id = " + id);

        //compruebo que el jugador existe
        boolean existe = rs3.next();
        if (existe) {
            //Pregunto los datos
            System.out.println("Introduce el nuevo nombre del jugador");
            //Leo el nombre del jugador
            String actualizaJugador = lector.next();

            //Muestro los equipos disponibles
            System.out.println("\n--------------EQUIPOS--------------");
            //Creo la sentencia SQL
            rs = sentencia.executeQuery("SELECT * FROM EQUIPOS");
            //Llamada al método mostrarEquipos
            mostrarEquipos(rs);


            System.out.println("\nIntroduce el codigo del equipo");
            System.out.println("Si no existe el equipo, introduce 0");
            //Leo el código del equipo
            String actualizaCodigoEquipo = lector.next();
            //Si el código del equipo es 0
            //se crea un nuevo equipo
            //y se guarda el código del equipo
            //Llamada al método solicitarNuevoEquipo
            if (actualizaCodigoEquipo.equals("0")) {
                solicitarNuevoEquipo(lector, sentencia);
            }

            System.out.println("Introduce el número de votos");
            int actualizaVotos = lector.nextInt();
            ps = con.prepareStatement("UPDATE balon_oro SET nombre = ?, cod_equipo = ?, votos = ? WHERE id = " + id);

            modificarJugador(ps, actualizaJugador, actualizaCodigoEquipo, actualizaVotos);
        } else {
            System.out.println("No existe el jugador");
        }
    }

    //4. Eliminar un jugador
    private static void caso4(Scanner lector, Statement sentencia, Connection con) throws SQLException {
        //declaración de variables
        ResultSet rs;
        ResultSet rs2;
        ResultSet rs3;
        PreparedStatement ps;

        //Eliminar un jugador
        limpiar(15);
        System.out.println("4. Borrar un jugador");
        rs2 = sentencia.executeQuery("SELECT * FROM balon_oro ORDER BY id ASC");
        mostrarClasificacion(rs2);
        //Pregunto el ID del jugador
        System.out.println("Introduce el id del jugador que quieres borrar");
        int idBorrar = lector.nextInt();
        rs3 = sentencia.executeQuery("SELECT * FROM balon_oro WHERE id = " + idBorrar);

        //compruebo que el jugador existe
        boolean existeBorrar = rs3.next();
        if (existeBorrar) {
            //cercioro que el usuario quiere borrar el jugador
            System.out.println("¿Estas seguro de que quieres borrar el jugador? (S/N)");
            String respuesta = lector.next();
            //si la respuesta es S
            if (respuesta.equalsIgnoreCase("S")) {
                //borro el jugador
                ps = con.prepareStatement("DELETE FROM balon_oro WHERE id = " + idBorrar);
                borrarJugador(ps);
            }
            //si la respuesta es N
            else if (respuesta.equalsIgnoreCase("N")) {
                //no borro el jugador
                System.out.println("No se ha borrado el jugador");
            }
            //si la respuesta es distinta de S o N
            else {
                //no borro el jugador
                System.out.println("No se ha borrado el jugador");
            }
        //si el jugador no existe
        } else {
            System.out.println("No existe el jugador");
        }
    }

    //5. Importar CSV
    private static void caso5(Scanner lector){
        // Importar CSV
        boolean probar;
        do {
            limpiar(15);
            System.out.println("5. Importar CSV");

            //Muestro los archivos disponibles en la carpeta archivosCSV
            String sDirectorio = "target/archivosCSV";
            File f = new File(sDirectorio);
            File[] ficheros = f.listFiles();

            //recorre los archivos de la carpeta
            for (int x = 0; x < Objects.requireNonNull(ficheros).length; x++) {
                System.out.println(ficheros[x].getName());
            }

            //Pregunto el nombre de los archivos
            System.out.println("Introduce el nombre del archivo(.csv) para la tabla balon_oro");
            String nombreArchivo = lector.next();

            System.out.println("Introduce el nombre del archivo(.csv) para la tabla equipos");
            String nombreArchivo2 = lector.next();

            //Si el nombre del archivo es correcto
            if (nombreArchivo.contains(".csv") && nombreArchivo2.contains(".csv")) {
                //llamo al método importarCSV
                importarCSV(nombreArchivo, nombreArchivo2);
                probar = true;
            }
            //Si el nombre del archivo es incorrecto
            else {
                //muestro un mensaje de error
                System.out.println("El nombre del archivo no es correcto");
                probar = false;
            }

        }
        //Si el nombre del archivo es incorrecto te vuelve a preguntar
        while (!probar);
    }

    //6. Exportar CSV
    private static void caso6(Scanner lector, Connection con) throws SQLException {
        boolean probar2;
        do {
            // Exportar CSV
            limpiar(15);
            //añado la dirección de la carpeta
            String sDirectorio2 = "target/archivosCSV";
            File f2 = new File(sDirectorio2);
            File[] ficheros2 = f2.listFiles();

            System.out.println("6. Exportar CSV");
            //Muestro los archivos disponibles en la carpeta archivosCSV
            for (int x = 0; x < Objects.requireNonNull(ficheros2).length; x++) {
                System.out.println(ficheros2[x].getName());
            }
            //Pregunto el nombre de los archivos
            System.out.println("(Recuerde que si los archivos ya existen se sobreescribirán)");
            System.out.println("Introduce el nombre del archivo(.csv) para la tabla balon_oro");
            String nombreArchivo3 = lector.next();

            System.out.println("Introduce el nombre del archivo(.csv) para la tabla equipos");
            String nombreArchivo4 = lector.next();

            //Si el nombre del archivo es correcto
            if (nombreArchivo3.contains(".csv") && nombreArchivo4.contains(".csv")) {
                //si los archivos son iguales
                if (nombreArchivo3.equals(nombreArchivo4)) {
                    System.out.println("Los nombres de los archivos no pueden ser iguales");
                    probar2 = false;
                }
                //si los archivos son diferentes
                else {
                    //llamo al método exportarCSV
                    exportarCSV(nombreArchivo3, nombreArchivo4, con);
                    probar2 = true;
                }
            }
            //Si el nombre del archivo es incorrecto
            else {
                //muestro un mensaje de error
                System.out.println("El nombre del archivo no es correcto");
                probar2 = false;
            }
        }
        //Si el nombre del archivo es incorrecto te vuelve a preguntar
        while (!probar2);
    }

    //Funcion para limpiar la pantalla de la consola
    //Recibe como párametro el número de líneas que queremos limpiar
    public static void limpiar(int lineas)
    {
        for (int i=0; i < lineas; i++)
        {
            System.out.println("");
        }
    }


    //Función para solicitar un nuevo equipo
    private static String solicitarNuevoEquipo(Scanner lector, Statement sentencia) throws SQLException {
        //Creo la variable
        ResultSet rs;

        limpiar(15);
        System.out.println("Introduce el código del nuevo equipo");
        //Leo el código del nuevo equipo
        String codigoNuevoEquipo = lector.next();

        System.out.println("Introduce el nombre del nuevo equipo");
        //Leo el nombre del nuevo equipo
        String nombreNuevoEquipo = lector.next();

        System.out.println("Introduce la liga del nuevo equipo");
        //Leo la liga del nuevo equipo
        String ligaNuevoEquipo = lector.next();

        //Creo la sentencia SQL
        rs= sentencia.executeQuery("SELECT * FROM EQUIPOS");

        //Llamada al método insertarEquipo
        insertarEquipo(rs, codigoNuevoEquipo, nombreNuevoEquipo, ligaNuevoEquipo);

        return codigoNuevoEquipo;

    }

    //Función para mostrar los equipos
    private static void mostrarEquipos(ResultSet rs) {
        //Controlo que se muestran los datos correctamente
        try {
            //Recorro el ResultSet
            //mientras haya datos
            //muestra los datos
            while (rs.next()) {
                System.out.print(rs.getString("cod_equipo"));
                System.out.print(" - ");
                System.out.print(rs.getString("nombre"));
                System.out.print(" - ");
                System.out.print(rs.getString("liga"));
                System.out.println();
            }
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("Error al mostrar los equipos");
            System.out.println(e.getMessage());
        }
    }

    //Función para añadir un equipo
    private static void insertarEquipo(ResultSet rs, String codigoNuevoEquipo, String nombreNuevoEquipo, String ligaNuevoEquipo) {
        //Controlo el error de que no se añada bien el equipo
        try {
            //Inserto las variables recibidas en la tabla equipos
            rs.moveToInsertRow();
            rs.updateString("cod_equipo", codigoNuevoEquipo);
            rs.updateString("nombre", nombreNuevoEquipo);
            rs.updateString("liga", ligaNuevoEquipo);
            rs.insertRow();
            rs.moveToCurrentRow();
            System.out.println("Equipo añadido correctamente\n");
        } catch (SQLException e) {
            System.out.println("Error al añadir el equipo");
            System.out.println(e.getMessage());
        }
    }

    //Función para añadir un jugador
    private static void insertarJugador(ResultSet rs2, String nombre, String codEquipo,int votos) {
        //Controlo el error de que no se añada bien el jugador
        try{
            //Inserto las variables recibidas en la tabla balon_oro
            rs2.moveToInsertRow();
            rs2.updateString("nombre", nombre);
            rs2.updateString("cod_equipo", codEquipo);
            rs2.updateInt("votos", votos);
            rs2.insertRow();
            System.out.println("Jugador añadido correctamente\n");
        } catch (SQLException e) {
            System.out.println("Error al añadir el jugador");
            System.out.println(e.getMessage());
        }
    }

    //Función para mostrar los jugadores
    private static void mostrarClasificacion(ResultSet rs2) {
        //Controlo que no se haya llegado al final del ResultSet
        try {
            //Recorro el ResultSet
            //mientras haya datos
            //muestra los datos
            while (rs2.next()) {
                //Muestro los datos de los jugadores
                System.out.print(rs2.getString("id"));
                System.out.print(" - ");
                System.out.print(rs2.getString("nombre"));
                System.out.print(" - ");
                System.out.print(rs2.getString("cod_equipo"));
                System.out.print(" - ");
                System.out.print(rs2.getString("votos"));
                System.out.println();
            }
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("Error al mostrar los equipos");
            System.out.println(e.getMessage());
        }

    }

    //Función para modificar un jugador
    private static void modificarJugador(PreparedStatement ps, String actualizaJugador, String actualizaCodigoEquipo, int actualizaVotos) {
        //Controlo el posible error al actualizar el jugador
        try{
            //Actualizo los datos del jugador
            ps.setString(1, actualizaJugador);
            ps.setString(2, actualizaCodigoEquipo);
            ps.setInt(3, actualizaVotos);
            //Ejecuto la actualización
            ps.executeUpdate();
            System.out.println("Jugador actualizado correctamente\n");
        } catch (SQLException e) {
            System.out.println("Error al actualizar el jugador");
            System.out.println(e.getMessage());
        }
    }

    //Función para eliminar un jugador
    private static void borrarJugador(PreparedStatement ps) {
        //Controlo el posible error al borrar el jugador
        try{
            //Ejecuto la sentencia sql para borrar el jugador
            ps.executeUpdate();
            System.out.println("Jugador borrado correctamente\n");
        } catch (SQLException e) {
            System.out.println("Error al borrar el jugador");
            System.out.println(e.getMessage());
        }
    }

    //Función para importar un archivo csv
    private static void importarCSV(String nombreArchivo, String nombreArchivo2) {
        //Controlo los posibles errores de la lectura del archivo con un try / catch
        try {
            //Creo un objeto de la clase File para leer el archivo pasado por parámetro
            BufferedReader br = new BufferedReader(new FileReader("target/archivosCSV/"+nombreArchivo));
            String linea;
            System.out.println("Importando archivos CSV");
            System.out.println("\n---------Clasificación----------");
            //Leo el archivo linea a linea
            while ((linea = br.readLine()) != null) {
                //Divido la línea en un array de String
                //El separador es el punto y coma
                String[] campos = linea.split(";");
                int id = Integer.parseInt(campos[0]);
                String nombre = campos[1];
                String codEquipo = campos[2];
                int votos = Integer.parseInt(campos[3]);
                //Muestro los datos de la línea
                System.out.println(id + " - " + nombre + " - " + codEquipo + " - " + votos);
            }
            //Cierro el archivo
            br.close();

            //Repetimos el proceso para el segundo archivo
            BufferedReader br2 = new BufferedReader(new FileReader("target/archivosCSV/"+nombreArchivo2));
            String linea2;
            System.out.println("\n---------Equipos----------");
            while ((linea2 = br2.readLine()) != null) {
                String[] campos2 = linea2.split(";");
                String codEquipo = campos2[0];
                String nombre = campos2[1];
                String liga = campos2[2];
                System.out.println(codEquipo + " - " + nombre + " - " + liga);
            }
            //Cierro el archivo
            br2.close();
            System.out.println("Archivos importados correctamente\n");
        } catch (FileNotFoundException e) {
            System.out.println("No se ha encontrado el archivo");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
            System.out.println(e.getMessage());
        }
    }

    //Función para exportar un archivo csv
    private static void exportarCSV(String nombreArchivo3, String nombreArchivo4,Connection con) {
        //Controlo los posibles errores de la escritura del archivo con un try / catch
        try {
            //Creo el archivo de salida con el nombre pasado por parámetro
            BufferedWriter bw = new BufferedWriter(new FileWriter("target/archivosCSV/"+nombreArchivo3));
            String linea;
            //Creo la consulta
            PreparedStatement st2 = con.prepareStatement("SELECT * FROM balon_oro ORDER BY votos ASC");
            //Ejecuto la consulta
            ResultSet rs2 = st2.executeQuery();
            //Recorro el resultado de la consulta
            while (rs2.next()) {
                //Guardo los datos de la consulta en variables
                int id = rs2.getInt("id");
                String nombre = rs2.getString("nombre");
                String codEquipo = rs2.getString("cod_equipo");
                int votos = rs2.getInt("votos");
                //Escribo en el archivo
                linea = id + ";" + nombre + ";" + codEquipo + ";" + votos;
                bw.write(linea);
                bw.newLine();
            }
            //Cierro el archivo
            bw.close();

            //Repito el mismo proceso para el segundo archivo
            BufferedWriter bw2 = new BufferedWriter(new FileWriter("target/archivosCSV/"+nombreArchivo4));
            String linea2;
            PreparedStatement st3 = con.prepareStatement("SELECT * FROM equipos");
            ResultSet rs3 = st3.executeQuery();
            while (rs3.next()) {
                String codEquipo = rs3.getString("cod_equipo");
                String nombre = rs3.getString("nombre");
                String liga = rs3.getString("liga");
                linea2 = codEquipo + ";" + nombre + ";" + liga;
                bw2.write(linea2);
                bw2.newLine();
            }
            //Cierro el archivo
            bw2.close();
            System.out.println("Archivos exportados correctamente\n");


        } catch (IOException e) {
            System.out.println("Error al escribir el archivo");
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta");
            System.out.println(e.getMessage());
        }

    }

}
