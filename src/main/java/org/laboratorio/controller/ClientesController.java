package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import org.laboratorio.model.Cliente;

import static javafx.scene.control.Alert.AlertType.WARNING;
import org.laboratorio.database.Conexion;
import org.laboratorio.system.Main;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ClientesController implements Initializable {
    private Main principal;
    private Cliente modeloCliente;
    private ObservableList<Cliente> listarClientes;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn colId, colNombre, colApellido, colTelefono, colEmail,
            colDireccion, colFechaRegistro;
    @FXML private TextField txtID, txtNombre, txtApellido, txtTelefono, txtEmail, txtDireccion, txtBuscar;
    @FXML private DatePicker dpFechaRegistro;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaClientes.setOnMouseClicked(eventHandler -> getClienteTextField());
        deshabilitarCampos(); 
    }  
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<Cliente, Integer>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<Cliente, String>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefono"));        
        colEmail.setCellValueFactory(new PropertyValueFactory<Cliente, String>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Cliente, String>("direccion"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Cliente, LocalDate>("fechaRegistro"));
    }
    
    public void cargarDatos(){
        ArrayList<Cliente> clientes = listarCliente();
        listarClientes = FXCollections.observableArrayList(clientes);
        tablaClientes.setItems(listarClientes);
        tablaClientes.getSelectionModel().selectFirst();
        getClienteTextField();
    }
    
    public void getClienteTextField(){
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            txtID.setText(String.valueOf(clienteSeleccionado.getIdCliente()));
            txtNombre.setText(clienteSeleccionado.getNombre());
            txtApellido.setText(clienteSeleccionado.getApellidos());
            txtTelefono.setText(clienteSeleccionado.getTelefono());
            txtEmail.setText(clienteSeleccionado.getEmail());
            txtDireccion.setText(clienteSeleccionado.getDireccion());
            dpFechaRegistro.setValue(clienteSeleccionado.getFechaRegistro());
        }
    }
    
    public ArrayList<Cliente> listarCliente(){
        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarClientes}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                clientes.add(new Cliente(
                    resultado.getInt(1),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    resultado.getString(5),
                    resultado.getString(6),
                    resultado.getDate(7).toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.out.println("error al cargar: " + e.getMessage());
        }
        return clientes;
    }
    
    private Cliente getModeloCliente(){
        int idCliente = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        String nombre = txtNombre.getText();
        String apellidos = txtApellido.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        LocalDate fechaRegistro = dpFechaRegistro.getValue();
        
        return new Cliente(
            idCliente, nombre, apellidos, telefono, email, direccion, fechaRegistro
        );
    }
    
    public void agregarCliente(){
        modeloCliente = getModeloCliente();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarCliente(?,?,?,?,?,?)}");
            enunciado.setString(1, modeloCliente.getNombre());
            enunciado.setString(2, modeloCliente.getApellidos());
            enunciado.setString(3, modeloCliente.getTelefono());
            enunciado.setString(4, modeloCliente.getEmail());
            enunciado.setString(5, modeloCliente.getDireccion());
            enunciado.setDate(6, java.sql.Date.valueOf(modeloCliente.getFechaRegistro()));
            enunciado.execute();
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarCliente(){
        modeloCliente = getModeloCliente();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarCliente(?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloCliente.getIdCliente());
            enunciado.setString(2, modeloCliente.getNombre());
            enunciado.setString(3, modeloCliente.getApellidos());
            enunciado.setString(4, modeloCliente.getTelefono());
            enunciado.setString(5, modeloCliente.getEmail());
            enunciado.setString(6, modeloCliente.getDireccion());
            enunciado.setDate(7, java.sql.Date.valueOf(modeloCliente.getFechaRegistro()));
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " +  e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarCliente(){
        modeloCliente = getModeloCliente();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarCliente(?)}");
            enunciado.setInt(1, modeloCliente.getIdCliente());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar. " + e.getMessage());
        }
    }
    
    public void limpiarTexto(){
        txtID.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        dpFechaRegistro.setValue(LocalDate.now()); 
    }
    

    
    @FXML
    private void cambiarNuevoCliente() {    
        if (cancelando) return; 

        switch (accion) {
            case NINGUNA:
                cambiarGuardarEditar();
                accion = Acciones.AGREGAR;
                limpiarTexto();
                habilitarCampos(); 
                deshabilitarNavegacion(); 
                break;
            case AGREGAR:
                if(validarFormulario()){
                    agregarCliente();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarCliente();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionCliente() {
        if (accion == Acciones.NINGUNA) {
            cambiarGuardarEditar();
            accion = Acciones.EDITAR;
            habilitarCampos(); 
            deshabilitarNavegacion(); 
        }
    }
    
    @FXML
    private void cambiarCancelarEliminar(){
        if(accion == Acciones.NINGUNA) {
            eliminarCliente();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            habilitarDeshabilitarNodo();

            if(estabaEditando) {  
                getClienteTextField();
            } else {
                tablaClientes.getSelectionModel().selectFirst();
            }

            cancelando = false;
        }    
    }
    
    private void habilitarCampos() {
        txtNombre.setDisable(false);
        txtApellido.setDisable(false);
        txtTelefono.setDisable(false);
        txtEmail.setDisable(false);
        txtDireccion.setDisable(false);
        dpFechaRegistro.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaClientes.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtTelefono.setDisable(true);
        txtEmail.setDisable(true);
        txtDireccion.setDisable(true);
        dpFechaRegistro.setDisable(true);

        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaClientes.setDisable(false);
    }

    private void deshabilitarNavegacion() {
    btnSiguiente.setDisable(true);
    btnAnterior.setDisable(true);
    tablaClientes.setDisable(true);
    }

    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaClientes.setDisable(false);
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaClientes.getSelectionModel().getSelectedIndex();
        if (indice < listarClientes.size()-1) {
            tablaClientes.getSelectionModel().select(indice+1);
            getClienteTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaClientes.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaClientes.getSelectionModel().select(indice-1);
            getClienteTextField();
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
    
    private void cambiarEstado(boolean estado) {
        txtNombre.setDisable(estado);
        txtApellido.setDisable(estado);
        txtTelefono.setDisable(estado);
        txtEmail.setDisable(estado);
        txtDireccion.setDisable(estado);
        dpFechaRegistro.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = txtNombre.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaClientes.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorNombre(){
        ArrayList<Cliente> resultadoBusqueda = new ArrayList<>();
        String nombreBuscado = txtBuscar.getText();
        for(Cliente cliente: listarClientes) {
            if(cliente.getNombre().toLowerCase().contains(nombreBuscado.toLowerCase())) {
                resultadoBusqueda.add(cliente);
            }
        }
        tablaClientes.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaClientes.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if(cancelando) return true; 
        
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        LocalDate fechaRegistro = dpFechaRegistro.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty() || fechaRegistro == null) {
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