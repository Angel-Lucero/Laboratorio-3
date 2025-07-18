package org.laboratorio.system;

import java.io.InputStream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.laboratorio.controller.*;

/**
 *
 * @author Lu0
 */
public class Main extends Application {
    private Stage escenarioPrincipal;
    private Scene siguienteEscena;
    private static String URL = "/view/";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
          this.escenarioPrincipal = stage;
          getLoginView();
          stage.show();         
    }
    
    public Initializable cambiarEscena(String fxml,double ancho, double alto) throws Exception{ 
        Initializable interfazDeCambio = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivoFXML = Main.class.getResourceAsStream(URL+fxml); 
        
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL+fxml));
        
        siguienteEscena = new Scene(cargadorFXML.load(archivoFXML),ancho,alto);
        escenarioPrincipal.setScene(siguienteEscena);
        escenarioPrincipal.sizeToScene();
        
        interfazDeCambio = cargadorFXML.getController();
        
        return interfazDeCambio;  
    }
    
    public void getLoginView(){
        try {
            LoginController control =
                        (LoginController) cambiarEscena("LoginView.fxml",520,400);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a Inicio"+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getMenuPrincipalView(){
        try {
            MenuPrincipalController control =
                    (MenuPrincipalController) cambiarEscena("MenuPrincipalView.fxml",1000,800);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a Inicio"+ex.getMessage());
            ex.printStackTrace();
        }
    }      
    
    public void getNuevoUsuarioView(){
        try {
            NuevoUsuarioController control =
                    (NuevoUsuarioController) cambiarEscena("NuevoUsuarioView.fxml",520,400);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a Inicio"+ex.getMessage());
            ex.printStackTrace();
        }
    }     
    
    public void getProveedoresView(){
    try {
        ProveedoresController control =
                (ProveedoresController) cambiarEscena("ProveedoresView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a Proveedores"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getProductosView(){
    try {
        ProductosController control =
                (ProductosController) cambiarEscena("ProductosView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a Productos"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getEspecificacionesView(){
    try {
        EspecificacionesController control =
                (EspecificacionesController) cambiarEscena("EspecificacionesView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a Especificaciones"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getProductoProveedorView(){
    try {
        ProductoProveedorController control =
                (ProductoProveedorController) cambiarEscena("ProductoProveedorView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a ProductoProveedor"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getClientesView(){
    try {
        ClientesController control =
                (ClientesController) cambiarEscena("ClientesView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a Clientes"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getVentasView(){
    try {
        VentasController control =
                (VentasController) cambiarEscena("VentasView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a Ventas"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getDetalleVentaView(){
    try {
        DetalleVentaController control =
                (DetalleVentaController) cambiarEscena("DetalleVentaView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a DetalleVenta"+ex.getMessage());
        ex.printStackTrace();
    }
}

public void getMovimientosInventarioView(){
    try {
        MovimientosInventarioController control =
                (MovimientosInventarioController) cambiarEscena("MovimientosInventarioView.fxml",1000,800);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a MovimientosInventario"+ex.getMessage());
        ex.printStackTrace();
    }
}
    
    
}