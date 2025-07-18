package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.laboratorio.database.Conexion;
import org.laboratorio.model.Venta;
import org.laboratorio.model.Cliente;
import org.laboratorio.model.Usuario;
import org.laboratorio.system.Main;

import static javafx.scene.control.Alert.AlertType.WARNING;
/**
 *
 * @author Lu0
 */
public class VentasController implements Initializable {
    private Main principal;
    private Venta modeloVenta;
    private ObservableList<Venta> listarVentas;
    private ObservableList<Cliente> listaClientes;
    private ObservableList<Usuario> listaUsuarios;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn colId, colClienteId, colFechaVenta, colSubtotal, 
            colIva, colTotal, colMetodoPago, colFacturada, colFolioFactura;
    @FXML private TextField txtID, txtSubtotal, txtIva, txtTotal, txtFolioFactura, txtBuscar;
    @FXML private ComboBox<String> cbxMetodoPago, cbxFacturada;
    @FXML private ComboBox<Cliente> cbxCliente;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbxMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Transferencia", "Meses sin intereses");
        cbxFacturada.getItems().addAll("Facturada", "No Facturada");
        
        cargarClientes();
        cargarUsuarios();
        setFormatoColumnaModelo();
        cargarDatos();
        tablaVentas.setOnMouseClicked(eventHandler -> getVentaTextField());
        deshabilitarCampos(); 
    }
    
    private void cargarClientes() {
        listaClientes = FXCollections.observableArrayList();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement stmt = conexion.prepareCall("{call sp_ListarClientes()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("IdCliente"),
                    rs.getString("Nombre"),
                    rs.getString("Apellidos"),
                    rs.getString("Telefono"),
                    rs.getString("Email"),
                    rs.getString("Direccion"),
                    rs.getDate("FechaRegistro").toLocalDate()
                );
                listaClientes.add(cliente);
            }
            cbxCliente.setItems(listaClientes);
        } catch (SQLException e) {
            mostrarError("Error al cargar clientes", e.getMessage());
        }
    }
    
    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement stmt = conexion.prepareCall("{call sp_ListarUsuarios()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getString("nombre"),
                    rs.getString("contrasena")
                );
                listaUsuarios.add(usuario);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colClienteId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));        
        colIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colFacturada.setCellValueFactory(new PropertyValueFactory<>("facturada"));
        colFolioFactura.setCellValueFactory(new PropertyValueFactory<>("folioFactura"));
    }
    
    public void cargarDatos(){
        ArrayList<Venta> ventas = listarVentas();
        listarVentas = FXCollections.observableArrayList(ventas);
        tablaVentas.setItems(listarVentas);
        if (!listarVentas.isEmpty()) {
            tablaVentas.getSelectionModel().selectFirst();
            getVentaTextField();
        }
    }
    
    public void getVentaTextField(){
        Venta ventaSeleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada != null) {
            txtID.setText(String.valueOf(ventaSeleccionada.getIdVenta()));
            
            // Buscar y seleccionar el cliente correspondiente
            for (Cliente cliente : cbxCliente.getItems()) {
                if (cliente.getIdCliente() == ventaSeleccionada.getIdCliente()) {
                    cbxCliente.setValue(cliente);
                    break;
                }
            }
            
            txtSubtotal.setText(String.valueOf(ventaSeleccionada.getSubtotal()));
            txtIva.setText(String.valueOf(ventaSeleccionada.getIva()));
            txtTotal.setText(String.valueOf(ventaSeleccionada.getTotal()));
            cbxMetodoPago.setValue(ventaSeleccionada.getMetodoPago());
            cbxFacturada.setValue(ventaSeleccionada.getFacturada());
            txtFolioFactura.setText(ventaSeleccionada.getFolioFactura());
        }
    }
    
    public ArrayList<Venta> listarVentas(){
        ArrayList<Venta> ventas = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarVentas()}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                ventas.add(new Venta(
                    resultado.getInt("IdVenta"),        
                    resultado.getInt("ClienteId"),        
                    resultado.getTimestamp("FechaVenta").toLocalDateTime().toString(), 
                    resultado.getFloat("Subtotal"),       
                    resultado.getFloat("Iva"),          
                    resultado.getFloat("Total"),       
                    resultado.getString("MetodoPago"),   
                    resultado.getString("Facturada"),    
                    resultado.getString("FolioFactura") 
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
            e.printStackTrace(); // Para más detalles del error
        }
        return ventas;
    }
    
    private Venta getModeloVenta(){
        int idVenta = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        int idCliente = cbxCliente.getValue().getIdCliente();
        String fechaVenta = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        float subtotal = Float.parseFloat(txtSubtotal.getText());
        float iva = Float.parseFloat(txtIva.getText());
        float total = Float.parseFloat(txtTotal.getText());
        String metodoPago = cbxMetodoPago.getValue();
        String facturada = cbxFacturada.getValue(); 

        return new Venta(
            idVenta, 
            idCliente, 
            fechaVenta, 
            subtotal, 
            iva, 
            total, 
            metodoPago, 
            facturada, 
            txtFolioFactura.getText()
        );
    }
    
    public void agregarVenta(){
        modeloVenta = getModeloVenta();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarVenta(?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloVenta.getIdCliente());
            enunciado.setFloat(2, modeloVenta.getSubtotal());
            enunciado.setFloat(3, modeloVenta.getIva());
            enunciado.setFloat(4, modeloVenta.getTotal());
            enunciado.setString(5, modeloVenta.getMetodoPago());
            enunciado.setString(6, modeloVenta.getFacturada());
            enunciado.setString(7, modeloVenta.getFolioFactura());
            enunciado.registerOutParameter(8, java.sql.Types.INTEGER);
            enunciado.execute();
            
            int nuevoId = enunciado.getInt(8);
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar venta:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarVenta(){
        modeloVenta = getModeloVenta();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarVenta(?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloVenta.getIdVenta());
            enunciado.setInt(2, modeloVenta.getIdCliente());
            enunciado.setFloat(3, modeloVenta.getSubtotal());
            enunciado.setFloat(4, modeloVenta.getIva());
            enunciado.setFloat(5, modeloVenta.getTotal());
            enunciado.setString(6, modeloVenta.getMetodoPago());
            enunciado.setString(7, modeloVenta.getFacturada());
            enunciado.setString(8, modeloVenta.getFolioFactura());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarVenta(){
        modeloVenta = getModeloVenta();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarVenta(?)}");
            enunciado.setInt(1, modeloVenta.getIdVenta());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar venta. " + e.getMessage());
        }
    }
    
    public void limpiarTexto(){
        txtID.clear();
        cbxCliente.getSelectionModel().clearSelection();
        txtSubtotal.clear();
        txtIva.clear();
        txtTotal.clear();
        cbxMetodoPago.getSelectionModel().clearSelection();
        cbxFacturada.getSelectionModel().clearSelection();
        txtFolioFactura.clear();
    }
    
    @FXML
    private void cambiarNuevaVenta() {    
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
                    agregarVenta();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarVenta();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionVenta() {
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
            eliminarVenta();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            habilitarDeshabilitarNodo();

            if(estabaEditando) {  
                getVentaTextField();
            } else {
                if (!listarVentas.isEmpty()) {
                    tablaVentas.getSelectionModel().selectFirst();
                }
            }

            cancelando = false;
        }    
    }
    
    private void habilitarCampos() {
        cbxCliente.setDisable(false);
        txtSubtotal.setDisable(false);
        txtIva.setDisable(false);
        txtTotal.setDisable(false);
        cbxMetodoPago.setDisable(false);
        cbxFacturada.setDisable(false);
        txtFolioFactura.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaVentas.setDisable(true);
    }

    private void deshabilitarCampos() {
        cbxCliente.setDisable(true);
        txtSubtotal.setDisable(true);
        txtIva.setDisable(true);
        txtTotal.setDisable(true);
        cbxMetodoPago.setDisable(true);
        cbxFacturada.setDisable(true);
        txtFolioFactura.setDisable(true);

        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaVentas.setDisable(false);
    }

    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaVentas.setDisable(true);
    }

    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaVentas.setDisable(false);
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaVentas.getSelectionModel().getSelectedIndex();
        if (indice < listarVentas.size()-1) {
            tablaVentas.getSelectionModel().select(indice+1);
            getVentaTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaVentas.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaVentas.getSelectionModel().select(indice-1);
            getVentaTextField();
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
        cbxCliente.setDisable(estado);
        txtSubtotal.setDisable(estado);
        txtIva.setDisable(estado);
        txtTotal.setDisable(estado);
        cbxMetodoPago.setDisable(estado);
        cbxFacturada.setDisable(estado);
        txtFolioFactura.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = cbxCliente.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaVentas.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorCliente(){
        ArrayList<Venta> resultadoBusqueda = new ArrayList<>();
        String clienteBuscado = txtBuscar.getText();
        for(Venta venta: listarVentas) {
            if(String.valueOf(venta.getIdCliente()).contains(clienteBuscado)) {
                resultadoBusqueda.add(venta);
            }
        }
        tablaVentas.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaVentas.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if(cancelando) return true; 
        
        if (cbxCliente.getValue() == null || txtSubtotal.getText().isEmpty() || 
            txtIva.getText().isEmpty() || txtTotal.getText().isEmpty() || 
            cbxMetodoPago.getValue() == null || cbxFacturada.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        
        try {
            float subtotal = Float.parseFloat(txtSubtotal.getText());
            float iva = Float.parseFloat(txtIva.getText());
            float total = Float.parseFloat(txtTotal.getText());
            
            if (subtotal <= 0 || iva < 0 || total <= 0) {
                mostrarAlerta("Valores inválidos", "Los valores monetarios deben ser positivos.");
                return false;
            }
            
            float calculado = subtotal + iva;
            if (Math.abs(calculado - total) > 0.01) {
                mostrarAlerta("Error en cálculos", "El total debe ser igual a Subtotal + IVA");
                return false;
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "Por favor, ingrese valores numéricos válidos.");
            return false;
        }
        
        if (cbxFacturada.getValue().equals("Facturada") && txtFolioFactura.getText().isEmpty()) {
            mostrarAlerta("Folio requerido", "Debe ingresar un folio de factura para ventas facturadas");
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
    
    private void mostrarError(String titulo, String mensaje) {
        System.err.println(titulo + ": " + mensaje);
        mostrarAlerta(titulo, mensaje);
    }
   
    public void VolverOnActionEvent(ActionEvent e) {
        principal.getMenuPrincipalView();
    }    
}