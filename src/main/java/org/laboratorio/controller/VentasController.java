package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import org.laboratorio.system.Main;

import static javafx.scene.control.Alert.AlertType.WARNING;
/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class VentasController implements Initializable {
    private Main principal;
    private Venta modeloVenta;
    private ObservableList<Venta> listarVentas;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn colId, colClienteId, colFechaVenta, colSubtotal, 
            colIva, colTotal, colMetodoPago, colUsuarioId, colFacturada, colFolioFactura;
    @FXML private TextField txtID, txtClienteId, txtSubtotal, txtIva, txtTotal, 
            txtUsuarioId, txtFolioFactura, txtBuscar;
    @FXML private ComboBox<String> cbMetodoPago, cbFacturada;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar comboboxes
        cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Transferencia", "Meses sin intereses");
        cbFacturada.getItems().addAll("Facturada", "No Facturada");
        
        setFormatoColumnaModelo();
        cargarDatos();
        tablaVentas.setOnMouseClicked(eventHandler -> getVentaTextField());
        deshabilitarCampos(); 
    }  
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colClienteId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));        
        colIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
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
            txtClienteId.setText(String.valueOf(ventaSeleccionada.getIdCliente()));
            txtSubtotal.setText(String.valueOf(ventaSeleccionada.getSubtotal()));
            txtIva.setText(String.valueOf(ventaSeleccionada.getIva()));
            txtTotal.setText(String.valueOf(ventaSeleccionada.getTotal()));
            cbMetodoPago.setValue(ventaSeleccionada.getMetodoPago());
            txtUsuarioId.setText(String.valueOf(ventaSeleccionada.getUsuarioId()));
            cbFacturada.setValue(ventaSeleccionada.getFacturada());
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
                    resultado.getInt(1),
                    resultado.getInt(2),
                    resultado.getTimestamp(3).toLocalDateTime().toString(),
                    resultado.getFloat(4),
                    resultado.getFloat(5),
                    resultado.getFloat(6),
                    resultado.getString(7),
                    resultado.getInt(8),
                    resultado.getString(9),
                    resultado.getString(10)
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
        }
        return ventas;
    }
    
    private Venta getModeloVenta(){
        int idVenta = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        int idCliente = Integer.parseInt(txtClienteId.getText());
        String fechaVenta = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        float subtotal = Float.parseFloat(txtSubtotal.getText());
        float iva = Float.parseFloat(txtIva.getText());
        float total = Float.parseFloat(txtTotal.getText());
        String metodoPago = cbMetodoPago.getValue();
        int usuarioId = Integer.parseInt(txtUsuarioId.getText());
        String facturada = cbFacturada.getValue();
        String folioFactura = txtFolioFactura.getText();
        
        return new Venta(
            idVenta, idCliente, fechaVenta, subtotal, iva, total, 
            metodoPago, usuarioId, facturada, folioFactura
        );
    }
    
    public void agregarVenta(){
        modeloVenta = getModeloVenta();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarVenta(?,?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloVenta.getIdCliente());
            enunciado.setFloat(2, modeloVenta.getSubtotal());
            enunciado.setFloat(3, modeloVenta.getIva());
            enunciado.setFloat(4, modeloVenta.getTotal());
            enunciado.setString(5, modeloVenta.getMetodoPago());
            enunciado.setInt(6, modeloVenta.getUsuarioId());
            enunciado.setString(7, modeloVenta.getFacturada());
            enunciado.setString(8, modeloVenta.getFolioFactura());
            enunciado.registerOutParameter(9, java.sql.Types.INTEGER);
            enunciado.execute();
            
            // Obtener el ID de la venta recién creada
            int nuevoId = enunciado.getInt(9);
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
                .prepareCall("{call sp_ActualizarVenta(?,?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloVenta.getIdVenta());
            enunciado.setInt(2, modeloVenta.getIdCliente());
            enunciado.setFloat(3, modeloVenta.getSubtotal());
            enunciado.setFloat(4, modeloVenta.getIva());
            enunciado.setFloat(5, modeloVenta.getTotal());
            enunciado.setString(6, modeloVenta.getMetodoPago());
            enunciado.setInt(7, modeloVenta.getUsuarioId());
            enunciado.setString(8, modeloVenta.getFacturada());
            enunciado.setString(9, modeloVenta.getFolioFactura());
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
        txtClienteId.clear();
        txtSubtotal.clear();
        txtIva.clear();
        txtTotal.clear();
        cbMetodoPago.getSelectionModel().clearSelection();
        txtUsuarioId.clear();
        cbFacturada.getSelectionModel().clearSelection();
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
        txtClienteId.setDisable(false);
        txtSubtotal.setDisable(false);
        txtIva.setDisable(false);
        txtTotal.setDisable(false);
        cbMetodoPago.setDisable(false);
        txtUsuarioId.setDisable(false);
        cbFacturada.setDisable(false);
        txtFolioFactura.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaVentas.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtClienteId.setDisable(true);
        txtSubtotal.setDisable(true);
        txtIva.setDisable(true);
        txtTotal.setDisable(true);
        cbMetodoPago.setDisable(true);
        txtUsuarioId.setDisable(true);
        cbFacturada.setDisable(true);
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
        txtClienteId.setDisable(estado);
        txtSubtotal.setDisable(estado);
        txtIva.setDisable(estado);
        txtTotal.setDisable(estado);
        cbMetodoPago.setDisable(estado);
        txtUsuarioId.setDisable(estado);
        cbFacturada.setDisable(estado);
        txtFolioFactura.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = txtClienteId.isDisable();
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
        
        if (txtClienteId.getText().isEmpty() || txtSubtotal.getText().isEmpty() || 
            txtIva.getText().isEmpty() || txtTotal.getText().isEmpty() || 
            cbMetodoPago.getValue() == null || txtUsuarioId.getText().isEmpty() || 
            cbFacturada.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        
        try {
            int clienteId = Integer.parseInt(txtClienteId.getText());
            float subtotal = Float.parseFloat(txtSubtotal.getText());
            float iva = Float.parseFloat(txtIva.getText());
            float total = Float.parseFloat(txtTotal.getText());
            int usuarioId = Integer.parseInt(txtUsuarioId.getText());
            
            if (clienteId <= 0 || usuarioId <= 0 || subtotal <= 0 || iva < 0 || total <= 0) {
                mostrarAlerta("Valores inválidos", "Los IDs y valores monetarios deben ser positivos.");
                return false;
            }
            
            // Validar que el total sea igual a subtotal + iva
            float calculado = subtotal + iva;
            if (Math.abs(calculado - total) > 0.01) {
                mostrarAlerta("Error en cálculos", "El total debe ser igual a Subtotal + IVA");
                return false;
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "Por favor, ingrese valores numéricos válidos.");
            return false;
        }
        
        // Validación adicional para folio factura si está facturada
        if (cbFacturada.getValue().equals("Facturada") && txtFolioFactura.getText().isEmpty()) {
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
   
    public void VolverOnActionEvent(ActionEvent e) {
        principal.getMenuPrincipalView();
    }    
}