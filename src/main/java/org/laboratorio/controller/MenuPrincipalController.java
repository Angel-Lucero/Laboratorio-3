
package org.laboratorio.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.laboratorio.system.Main;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class MenuPrincipalController implements Initializable {
    private Main principal;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @FXML private Button BtnProveedores, BtnProductos, BtnEspecificaciones,
            BtnProductoProveedor, BtnClientes, BtnVentas, BtnDetalleVenta,
            BtnMovimientos ;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    @FXML 
    public void btnCambioDeCategoria (ActionEvent evento){
        if (evento.getSource()==BtnProveedores) {
            principal.getProveedoresView();
        }else if (evento.getSource()==BtnProductos){    
            principal.getProductosView();
        }else if (evento.getSource()==BtnEspecificaciones){    
            principal.getEspecificacionesView();
        }else if (evento.getSource()==BtnProductoProveedor){    
            principal.getProductoProveedorView();
        }else if (evento.getSource()==BtnClientes){    
            principal.getClientesView();
        }else if (evento.getSource()==BtnVentas){    
            principal.getVentasView();
        }else if (evento.getSource()==BtnDetalleVenta){    
            principal.getDetalleVentaView();
        }else if (evento.getSource()==BtnMovimientos){    
            principal.getMovimientosInventarioView();
        }    
    }       
}
