
package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
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
import static javafx.scene.control.Alert.AlertType.WARNING;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.laboratorio.database.Conexion;
import org.laboratorio.model.Cliente;
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
            colDireccion, colRfc, colFechaRegistro, colActivo;
    @FXML private TextField txtID, txtNombre, txtApellido, txtTelefono, txtEmail, txtDireccion, txtRFC, txtActivo, txtBuscar;
    @FXML private DatePicker dpFechaRegistro;

    
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    setFormatoColumnaModelo();
        cargarDatos();
        tablaClientes.setOnMouseClicked(eventHandler -> getClienteTextField());
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtTelefono.setDisable(true);
        txtDireccion.setDisable(true);
        txtEmail.setDisable(true);
        dpFechaRegistro.setDisable(true);    
        txtRFC.setDisable(true);                  
    }    
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<Cliente,Integer>("IdCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Telefono"));        
        colEmail.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Direccion"));
        colRfc.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Rfc"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Cliente,LocalDate>("FechaRegistro"));    
        colActivo.setCellValueFactory(new PropertyValueFactory<Cliente,String>("Activo"));
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
            txtDireccion.setText(clienteSeleccionado.getDireccion());
            txtEmail.setText(clienteSeleccionado.getEmail());
            txtRFC.setText(clienteSeleccionado.getRfc());
            dpFechaRegistro.setValue(clienteSeleccionado.getFechaRegistro());
            txtActivo.setText(clienteSeleccionado.getActivo());
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
                resultado.getString(7),
                resultado.getDate(8).toLocalDate(),
                resultado.getString(9)));
            
            }
        } catch (SQLException e) {
            System.out.println("error al cargar: " + e.getMessage());
        }
    
        return clientes;
    }
    
    private Cliente getModeloCliente(){
        int codigoCliente;
        if ( txtID.getText().isEmpty()) {
            codigoCliente=1;
        } else {
            codigoCliente = Integer.parseInt(txtID.getText());

        }
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        String rfc = txtRFC.getText();
        LocalDate fechaRegistro = dpFechaRegistro.getValue();
        String activo = txtActivo.getText();
        Cliente cliente = new Cliente(codigoCliente, nombre, apellido, telefono, email, direccion, rfc, fechaRegistro, activo);
        
        return cliente;
    }
    
    public void agregarCliente(){
        modeloCliente = getModeloCliente();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCliente(?,?,?,?,?,?,?,?)}");
            enunciado.setString(1, modeloCliente.getNombre());
            enunciado.setString(2, modeloCliente.getApellidos());
            enunciado.setString(3, modeloCliente.getTelefono());
            enunciado.setString(4, modeloCliente.getEmail());
            enunciado.setString(5, modeloCliente.getDireccion());
            enunciado.setString(6, modeloCliente.getRfc());
            enunciado.setDate(7, Date.valueOf(modeloCliente.getFechaRegistro()));
            enunciado.setString(8, modeloCliente.getActivo());
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
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarCliente(?,?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloCliente.getIdCliente());
            enunciado.setString(2, modeloCliente.getNombre());
            enunciado.setString(3, modeloCliente.getApellidos());
            enunciado.setString(4, modeloCliente.getTelefono());
            enunciado.setString(5, modeloCliente.getEmail());
            enunciado.setString(6, modeloCliente.getDireccion());
            enunciado.setString(7, modeloCliente.getRfc());      
            enunciado.setDate(8, Date.valueOf(modeloCliente.getFechaRegistro()));
            enunciado.setString(9, modeloCliente.getActivo());
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
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCliente(?)}");
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
        txtDireccion.clear();
        txtEmail.clear();
        txtRFC.clear();
        dpFechaRegistro.setValue(null);
        txtActivo.clear();
    }
    
    @FXML
    private void cambiarNuevoCliente(){    
        if (cancelando) return; 

        switch (accion) {
            case NINGUNA:
                cambiarGuardarEditar();
                accion = Acciones.AGREGAR;
                limpiarTexto();
                habilitarDeshabilitarNodo();
                break;
            case AGREGAR:
                if(validarFormulario()){
                    System.out.println("Accion de agregar");
                    agregarCliente();
                    cambiarOriginal();
                    habilitarDeshabilitarNodo();
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    System.out.println("Accion del metodo editar");
                    editarCliente();
                    cambiarOriginal();
                    habilitarDeshabilitarNodo();
                }
                break;
        }
    }
    
    @FXML
    private void cambiarEdicionCliente(){
            cambiarGuardarEditar();
            accion = Acciones.EDITAR;
            habilitarDeshabilitarNodo();
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
        txtDireccion.setDisable(estado);
        txtEmail.setDisable(estado);
        txtRFC.setDisable(estado);
        dpFechaRegistro.setDisable(estado);
        txtActivo.setDisable(estado);
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
        ArrayList <Cliente> resultadoBusqueda = new ArrayList<>();
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
        String direccion = txtDireccion.getText();
        String email = txtEmail.getText();
        LocalDate fecha = dpFechaRegistro.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || email.isEmpty() || fecha == null) {
            mostrarAlerta("Campos vacios", "Por favor, llene todos los campos.");
            return false;
        }

        if (!telefono.matches("[0-9]+")) {
            mostrarAlerta("NO.telefono no valido", "El numero de telefono solo debe contener deigitos");
            return false;
        }

        if (!email.contains("@") || !email.endsWith(".com")) {
            mostrarAlerta("Correo no valido", "El correo debe contener '@' y terminar en '.com'");
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
   
    public void VolverOnActionEvent(ActionEvent e){
        principal.getMenuPrincipalView();
    }    
}
