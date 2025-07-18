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
import org.laboratorio.model.DetalleVenta;
import static javafx.scene.control.Alert.AlertType.WARNING;
import org.laboratorio.database.Conexion;
import org.laboratorio.system.Main;

public class DetalleVentaController implements Initializable {
    private Main principal;
    private DetalleVenta modeloDetalleVenta;
    private ObservableList<DetalleVenta> listaDetallesVenta;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<DetalleVenta> tablaDetallesVenta;
    @FXML private TableColumn<DetalleVenta, Integer> colId, colVentaId, colProductoId, colCantidad;
    @FXML private TableColumn<DetalleVenta, Float> colPrecioUnitario, colImporte;
    @FXML private TextField txtID, txtVentaId, txtProductoId, txtCantidad, txtPrecioUnitario, txtImporte, txtBuscar;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        tablaDetallesVenta.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> mostrarDetalleSeleccionado(newSelection)
        );
        deshabilitarCampos();
    }
    
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDetalle"));
        colVentaId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
    }
    
    private void cargarDatos() {
        try {
            listaDetallesVenta = FXCollections.observableArrayList(obtenerDetallesVentaBD());
            tablaDetallesVenta.setItems(listaDetallesVenta);
            if (!listaDetallesVenta.isEmpty()) {
                tablaDetallesVenta.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar detalles de venta", e.getMessage());
        }
    }
    
    private ArrayList<DetalleVenta> obtenerDetallesVentaBD() throws SQLException {
        ArrayList<DetalleVenta> detalles = new ArrayList<>();
        Connection conexion = Conexion.getInstancia().getConexion();
        try (CallableStatement stmt = conexion.prepareCall("{call sp_ListarDetalleVenta()}")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                detalles.add(new DetalleVenta(
                    rs.getInt("IdDetalle"),
                    rs.getInt("VentaId"),
                    rs.getInt("ProductoId"),
                    rs.getInt("Cantidad"),
                    rs.getFloat("PrecioUnitario"),
                    rs.getFloat("Importe")
                ));
            }
        }
        return detalles;
    }
    
    private void mostrarDetalleSeleccionado(DetalleVenta detalle) {
        if (detalle != null) {
            txtID.setText(String.valueOf(detalle.getIdDetalle()));
            txtVentaId.setText(String.valueOf(detalle.getIdVenta()));
            txtProductoId.setText(String.valueOf(detalle.getIdProducto()));
            txtCantidad.setText(String.valueOf(detalle.getCantidad()));
            txtPrecioUnitario.setText(String.valueOf(detalle.getPrecioUnitario()));
            txtImporte.setText(String.valueOf(detalle.getImporte()));
        }
    }
    
    private DetalleVenta obtenerModeloDesdeFormulario() {
        return new DetalleVenta(
            txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText()),
            Integer.parseInt(txtVentaId.getText()),
            Integer.parseInt(txtProductoId.getText()),
            Integer.parseInt(txtCantidad.getText()),
            Float.parseFloat(txtPrecioUnitario.getText()),
            Float.parseFloat(txtImporte.getText())
        );
    }
    
    @FXML
    private void accionNuevo() {
        if (cancelando) return;
        
        switch (accion) {
            case NINGUNA:
                prepararFormularioParaNuevo();
                break;
            case AGREGAR:
                if (validarFormulario()) {
                    agregarDetalleVenta();
                    restaurarEstadoOriginal();
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    actualizarDetalleVenta();
                    restaurarEstadoOriginal();
                }
                break;
        }
    }
    
    private void prepararFormularioParaNuevo() {
        cambiarEstadoBotones(true);
        accion = Acciones.AGREGAR;
        limpiarFormulario();
        habilitarCampos();
    }
    
    private void agregarDetalleVenta() {
        modeloDetalleVenta = obtenerModeloDesdeFormulario();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarDetalleVenta(?,?,?,?,?)}");
            stmt.setInt(1, modeloDetalleVenta.getIdVenta());
            stmt.setInt(2, modeloDetalleVenta.getIdProducto());
            stmt.setInt(3, modeloDetalleVenta.getCantidad());
            stmt.setFloat(4, modeloDetalleVenta.getPrecioUnitario());
            stmt.setFloat(5, modeloDetalleVenta.getImporte());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al agregar detalle de venta", e.getMessage());
        }
    }
    
    private void actualizarDetalleVenta() {
        modeloDetalleVenta = obtenerModeloDesdeFormulario();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarDetalleVenta(?,?,?,?,?,?)}");
            stmt.setInt(1, modeloDetalleVenta.getIdDetalle());
            stmt.setInt(2, modeloDetalleVenta.getIdVenta());
            stmt.setInt(3, modeloDetalleVenta.getIdProducto());
            stmt.setInt(4, modeloDetalleVenta.getCantidad());
            stmt.setFloat(5, modeloDetalleVenta.getPrecioUnitario());
            stmt.setFloat(6, modeloDetalleVenta.getImporte());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al actualizar detalle de venta", e.getMessage());
        }
    }
    
    @FXML
    private void accionEditar() {
        if (accion == Acciones.NINGUNA) {
            cambiarEstadoBotones(true);
            accion = Acciones.EDITAR;
            habilitarCampos();
        }
    }
    
    @FXML
    private void accionEliminarCancelar() {
        if (accion == Acciones.NINGUNA) {
            eliminarDetalleVenta();
        } else {
            cancelarAccion();
        }
    }
    
    private void eliminarDetalleVenta() {
        modeloDetalleVenta = obtenerModeloDesdeFormulario();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarDetalleVenta(?)}");
            stmt.setInt(1, modeloDetalleVenta.getIdDetalle());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al eliminar detalle de venta", e.getMessage());
        }
    }
    
    private void cancelarAccion() {
        cancelando = true;
        boolean estabaEditando = (accion == Acciones.EDITAR);
        restaurarEstadoOriginal();
        
        if (estabaEditando) {
            mostrarDetalleSeleccionado(tablaDetallesVenta.getSelectionModel().getSelectedItem());
        } else if (!listaDetallesVenta.isEmpty()) {
            tablaDetallesVenta.getSelectionModel().selectFirst();
        }
        
        cancelando = false;
    }
    
    private void restaurarEstadoOriginal() {
        cambiarEstadoBotones(false);
        accion = Acciones.NINGUNA;
        deshabilitarCampos();
    }
    
    private void cambiarEstadoBotones(boolean enEdicion) {
        btnNuevo.setText(enEdicion ? "Guardar" : "Nuevo");
        btnEliminar.setText(enEdicion ? "Cancelar" : "Eliminar");
        btnEditar.setDisable(enEdicion);
    }
    
    private void limpiarFormulario() {
        txtID.clear();
        txtVentaId.clear();
        txtProductoId.clear();
        txtCantidad.clear();
        txtPrecioUnitario.clear();
        txtImporte.clear();
    }
    
    private void habilitarCampos() {
        txtVentaId.setDisable(false);
        txtProductoId.setDisable(false);
        txtCantidad.setDisable(false);
        txtPrecioUnitario.setDisable(false);
        txtImporte.setDisable(false);
        deshabilitarNavegacion();
    }
    
    private void deshabilitarCampos() {
        txtVentaId.setDisable(true);
        txtProductoId.setDisable(true);
        txtCantidad.setDisable(true);
        txtPrecioUnitario.setDisable(true);
        txtImporte.setDisable(true);
        habilitarNavegacion();
    }
    
    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaDetallesVenta.setDisable(true);
    }
    
    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaDetallesVenta.setDisable(false);
    }
    
    @FXML
    private void navegarSiguiente() {
        int indice = tablaDetallesVenta.getSelectionModel().getSelectedIndex();
        if (indice < listaDetallesVenta.size() - 1) {
            tablaDetallesVenta.getSelectionModel().select(indice + 1);
        }
    }
    
    @FXML
    private void navegarAnterior() {
        int indice = tablaDetallesVenta.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaDetallesVenta.getSelectionModel().select(indice - 1);
        }
    }
    
    @FXML
    private void buscarDetallesVenta() {
        String criterio = txtBuscar.getText().toLowerCase();
        if (criterio.isEmpty()) {
            tablaDetallesVenta.setItems(listaDetallesVenta);
        } else {
            ObservableList<DetalleVenta> filtrados = listaDetallesVenta.filtered(detalle ->
                String.valueOf(detalle.getIdVenta()).contains(criterio) ||
                String.valueOf(detalle.getIdProducto()).contains(criterio) ||
                String.valueOf(detalle.getCantidad()).contains(criterio)
            );
            tablaDetallesVenta.setItems(filtrados);
        }
    }
    
    private boolean validarFormulario() {
        if (cancelando) return true;
        
        if (txtVentaId.getText().isEmpty() || txtProductoId.getText().isEmpty() ||
            txtCantidad.getText().isEmpty() || txtPrecioUnitario.getText().isEmpty() ||
            txtImporte.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Todos los campos son obligatorios");
            return false;
        }
        
        try {
            int ventaId = Integer.parseInt(txtVentaId.getText());
            int productoId = Integer.parseInt(txtProductoId.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());
            float precioUnitario = Float.parseFloat(txtPrecioUnitario.getText());
            float importe = Float.parseFloat(txtImporte.getText());
            
            if (ventaId <= 0 || productoId <= 0 || cantidad <= 0 || precioUnitario <= 0 || importe <= 0) {
                mostrarAlerta("Valores inválidos", "Todos los valores deben ser mayores a cero");
                return false;
            }
            
            // Validar que importe = cantidad * precioUnitario
            float importeCalculado = cantidad * precioUnitario;
            if (Math.abs(importeCalculado - importe) > 0.01) {
                mostrarAlerta("Error en cálculo", "El importe debe ser igual a Cantidad × Precio Unitario");
                return false;
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "Ingrese valores numéricos válidos");
            return false;
        }
        
        return true;
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        System.err.println(titulo + ": " + mensaje);
        mostrarAlerta(titulo, mensaje);
    }
    
    public void volverMenuPrincipal(ActionEvent e) {
        principal.getMenuPrincipalView();
    }
}