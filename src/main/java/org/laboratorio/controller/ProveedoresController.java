package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.laboratorio.model.Proveedor;

import static javafx.scene.control.Alert.AlertType.WARNING;
import org.laboratorio.database.Conexion;
import org.laboratorio.system.Main;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProveedoresController implements Initializable {
    private Main principal;
    private Proveedor modeloProveedor;
    private ObservableList<Proveedor> listarProveedores;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn colId, colNombre, colTelefono, colEmail, colDireccion, colEspecialidad;
    @FXML private TextField txtID, txtNombre, txtTelefono, txtEmail, txtDireccion, txtEspecialidad, txtBuscar;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaProveedores.setOnMouseClicked(eventHandler -> getProveedorTextField());
        deshabilitarCampos();
    }    
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<Proveedor, Integer>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("telefono"));        
        colEmail.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("direccion"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("especialidad"));
    }
    
    public void cargarDatos(){
        ArrayList<Proveedor> proveedores = listarProveedor();
        listarProveedores = FXCollections.observableArrayList(proveedores);
        tablaProveedores.setItems(listarProveedores);
        tablaProveedores.getSelectionModel().selectFirst();
        getProveedorTextField();
    }
    
    public void getProveedorTextField(){
        Proveedor proveedorSeleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (proveedorSeleccionado != null) {
            txtID.setText(String.valueOf(proveedorSeleccionado.getIdProveedor()));
            txtNombre.setText(proveedorSeleccionado.getNombre());
            txtTelefono.setText(proveedorSeleccionado.getTelefono());
            txtEmail.setText(proveedorSeleccionado.getEmail());
            txtDireccion.setText(proveedorSeleccionado.getDireccion());
            txtEspecialidad.setText(proveedorSeleccionado.getEspecialidad());
        }
    }
    
    public ArrayList<Proveedor> listarProveedor(){
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarProveedores}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                proveedores.add(new Proveedor(
                    resultado.getInt(1),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    resultado.getString(5),
                    resultado.getString(6)
                ));
            }
        } catch (SQLException e) {
            System.out.println("error al cargar: " + e.getMessage());
        }
        return proveedores;
    }
    
    private Proveedor getModeloProveedor(){
        int idProveedor = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        String especialidad = txtEspecialidad.getText();
        
        return new Proveedor(
            idProveedor, nombre, telefono, email, direccion, especialidad
        );
    }
    
    public void agregarProveedor(){
        modeloProveedor = getModeloProveedor();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarProveedor(?,?,?,?,?)}");
            enunciado.setString(1, modeloProveedor.getNombre());
            enunciado.setString(2, modeloProveedor.getTelefono());
            enunciado.setString(3, modeloProveedor.getEmail());
            enunciado.setString(4, modeloProveedor.getDireccion());
            enunciado.setString(5, modeloProveedor.getEspecialidad());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarProveedor(){
        modeloProveedor = getModeloProveedor();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarProveedor(?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloProveedor.getIdProveedor());
            enunciado.setString(2, modeloProveedor.getNombre());
            enunciado.setString(3, modeloProveedor.getTelefono());
            enunciado.setString(4, modeloProveedor.getEmail());
            enunciado.setString(5, modeloProveedor.getDireccion());
            enunciado.setString(6, modeloProveedor.getEspecialidad());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " +  e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarProveedor(){
        modeloProveedor = getModeloProveedor();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarProveedor(?)}");
            enunciado.setInt(1, modeloProveedor.getIdProveedor());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar. " + e.getMessage());
        }
    }
    
    public void limpiarTexto(){
        txtID.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtEspecialidad.clear();
    }
    
    private void habilitarCampos() {
        txtNombre.setDisable(false);
        txtTelefono.setDisable(false);
        txtEmail.setDisable(false);
        txtDireccion.setDisable(false);
        txtEspecialidad.setDisable(false);
        
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProveedores.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtNombre.setDisable(true);
        txtTelefono.setDisable(true);
        txtEmail.setDisable(true);
        txtDireccion.setDisable(true);
        txtEspecialidad.setDisable(true);
        
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaProveedores.setDisable(false);
    }
    
    @FXML
    private void cambiarNuevoProveedor() {    
        if (cancelando) return; 

        switch (accion) {
            case NINGUNA:
                cambiarGuardarEditar();
                accion = Acciones.AGREGAR;
                limpiarTexto();
                habilitarCampos();
                break;
            case AGREGAR:
                if(validarFormulario()){
                    agregarProveedor();
                    cambiarOriginal();
                    deshabilitarCampos();
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarProveedor();
                    cambiarOriginal();
                    deshabilitarCampos();
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionProveedor() {
        if (accion == Acciones.NINGUNA) {
            cambiarGuardarEditar();
            accion = Acciones.EDITAR;
            habilitarCampos();
        }
    }
    
    @FXML
    private void cambiarCancelarEliminar(){
        if(accion == Acciones.NINGUNA) {
            eliminarProveedor();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            deshabilitarCampos();

            if(estabaEditando) {  
                getProveedorTextField();
            } else {
                tablaProveedores.getSelectionModel().selectFirst();
            }

            cancelando = false;
        }    
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaProveedores.getSelectionModel().getSelectedIndex();
        if (indice < listarProveedores.size()-1) {
            tablaProveedores.getSelectionModel().select(indice+1);
            getProveedorTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaProveedores.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaProveedores.getSelectionModel().select(indice-1);
            getProveedorTextField();
        }
    }
    
    public void cambiarGuardarEditar(){
        btnNuevo.setText("Guardar");
        btnEliminar.setText("Cancelar");
        btnEditar.setDisable(true);
    }
    
    public void cambiarOriginal(){
        btnNuevo.setText("Nuevo");
        btnEliminar.setText("Eliminar");
        btnEditar.setDisable(false);
        accion = Acciones.NINGUNA;
    }
    
    @FXML
    private void btnBuscarPorNombre(){
        ArrayList<Proveedor> resultadoBusqueda = new ArrayList<>();
        String nombreBuscado = txtBuscar.getText();
        for(Proveedor proveedor: listarProveedores) {
            if(proveedor.getNombre().toLowerCase().contains(nombreBuscado.toLowerCase())) {
                resultadoBusqueda.add(proveedor);
            }
        }
        tablaProveedores.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaProveedores.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if(cancelando) return true; 
        
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        String especialidad = txtEspecialidad.getText();

        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty() || especialidad.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos.");
            return false;
        }

        if (!telefono.matches("[0-9]+")) {
            mostrarAlerta("Teléfono no válido", "El número de teléfono solo debe contener dígitos");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            mostrarAlerta("Correo no válido", "El correo debe contener '@' y un dominio válido");
            return false;
        }

        return true;
    }
    
    private void mostrarAlerta(String titulo, String razon) {
        Alert mensaje = new Alert(WARNING);
        mensaje.setTitle("Advertencia");
        mensaje.setHeaderText(titulo);
        mensaje.setContentText(razon);
        mensaje.showAndWait();
    }
   
    public void VolverOnActionEvent(ActionEvent e) {
        principal.getMenuPrincipalView();
    }    
}