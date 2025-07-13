
package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.laboratorio.database.Conexion;
import org.laboratorio.model.Usuario;
import org.laboratorio.system.Main;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class NuevoUsuarioController implements Initializable {
    private Main principal;
    private Usuario modeloUsuario;
    
    @FXML private Button BtnVolver, BtnCrear;
    @FXML private TextField TxtUsuario;
    @FXML private PasswordField PassContraseña;
    @FXML private Label LabMensaje;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
    
    public void VolverOnActionEvent(ActionEvent e){
        principal.getLoginView();
    }
    
    public void validarNuevoUsuario(){
     if (TxtUsuario.getText().isBlank() == false && PassContraseña.getText().isBlank() == false) {
            LabMensaje.setText("Usuario creado Correctamente");
        } else if (modeloUsuario.getNombreUsuario() == null || modeloUsuario.getNombreUsuario().trim().isEmpty()) {
            LabMensaje.setText("El nombre de usuario no puede estar vacío.");
        
        } else if (modeloUsuario.getContraseñaUsuario() == null || modeloUsuario.getContraseñaUsuario().trim().isEmpty()){
            LabMensaje.setText("La contraseña no puede estar vacía.");
        }
         else {
            LabMensaje.setText("Por favor ingresa un Usuario y contraseña");
        }  
    }

    private Usuario getModeloUsuario (){
        String nombre = TxtUsuario.getText();
        String contra = PassContraseña.getText();

        return new Usuario (nombre,contra);
    }

    public void agregarUsuario(){
        modeloUsuario = getModeloUsuario();
        validarNuevoUsuario();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarUsuarios(?,?)}");
            enunciado.setString(1, modeloUsuario.getNombreUsuario());
            enunciado.setString(2, modeloUsuario.getContraseñaUsuario());
            enunciado.execute();
        } catch (SQLException ex) {
            System.out.println("Error al agregar: " + ex.getSQLState());
            ex.printStackTrace();
        }
    }
 
}
