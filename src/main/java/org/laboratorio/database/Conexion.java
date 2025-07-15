
package org.laboratorio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Lu0
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/DBlaboratorio?useSSL=false";
    private static final String USER = "quintov";
    private static final String PASSWORD = "admin";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    
    public Conexion(){
        conectar(); 
    }
   
    public void conectar(){      
        try {
           Class.forName(DRIVER).newInstance();      
          conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException|SQLException ex) {
            
        } 
    }
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (Exception e) {
            System.out.println("error al conectar: " + e.getMessage()); 
            conectar();
        }
        return conexion;
    }
 
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

