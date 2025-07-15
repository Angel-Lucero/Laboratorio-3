
package org.laboratorio.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import org.laboratorio.system.Main;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProductoProveedorController implements Initializable {
    private Main principal;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void VolverOnActionEvent(ActionEvent e){
        principal.getMenuPrincipalView();
    }  
    
    
}
