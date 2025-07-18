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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import org.laboratorio.model.MovimientoInventario;
import org.laboratorio.model.Producto;
import org.laboratorio.model.Proveedor;
import org.laboratorio.model.Venta;
import static javafx.scene.control.Alert.AlertType.WARNING;
import org.laboratorio.database.Conexion;
import org.laboratorio.system.Main;
/**
 *
 * @author Lu0
 */
public class MovimientosInventarioController implements Initializable {
    private Main principal;
    private MovimientoInventario modeloMovimiento;
    private ObservableList<MovimientoInventario> listaMovimientos;
    private ObservableList<Producto> listaProductos;
    private ObservableList<Proveedor> listaProveedores;
    private ObservableList<Venta> listaVentas;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<MovimientoInventario> tablaMovimientos;
    @FXML private TableColumn<MovimientoInventario, Integer> colId, colCantidad, colProductoId, colVentaId, colProveedorId;
    @FXML private TableColumn<MovimientoInventario, String> colTipo, colComentario;
    @FXML private TableColumn<MovimientoInventario, LocalDate> colFecha;
    @FXML private TextField txtID, txtCantidad, txtComentario, txtBuscar;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Producto> cbProducto;
    @FXML private ComboBox<Venta> cbVenta;
    @FXML private ComboBox<Proveedor> cbProveedor;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipo.getItems().addAll("Entrada", "Salida", "Ajuste", "Devolución");
        
        configurarColumnas();
        cargarComboboxes();
        cargarDatos();
        tablaMovimientos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> mostrarMovimientoSeleccionado(newSelection)
        );
        deshabilitarCampos();
    }
    
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMovimiento"));
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colVentaId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colProveedorId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
    }
    
    private void cargarComboboxes() {
        cargarProductos();
        cargarProveedores();
        cargarVentas();
    }
    
    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarProductos()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("IdProducto"),
                    rs.getString("Nombre"),
                    rs.getString("Descripcion"),
                    rs.getString("Marca"),
                    rs.getString("Modelo"),
                    rs.getFloat("PrecioVenta"),
                    rs.getInt("StockMinimo"),
                    rs.getInt("CategoriaId"),
                    rs.getInt("GarantiaMeses"),
                    rs.getString("Color"),
                    rs.getFloat("PesoKg"),  
                    rs.getString("Dimensiones"),
                    rs.getString("urlImagen"),  
                    rs.getDate("FechaCreacion").toLocalDate()
                );
                listaProductos.add(producto);
            }
            cbProducto.setItems(listaProductos);
        } catch (SQLException e) {
            mostrarError("Error al cargar productos", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cargarProveedores() {
        listaProveedores = FXCollections.observableArrayList();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarProveedores()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                    rs.getInt("idProveedor"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getString("especialidad")
                );
                listaProveedores.add(proveedor);
            }
            cbProveedor.setItems(listaProveedores);
        } catch (SQLException e) {
            mostrarError("Error al cargar proveedores", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cargarVentas() {
        listaVentas = FXCollections.observableArrayList();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarVentas()}");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Venta venta = new Venta(
                    rs.getInt("IdVenta"),
                    rs.getInt("ClienteId"), 
                    rs.getString("FechaVenta"),
                    rs.getFloat("Subtotal"),
                    rs.getFloat("Iva"),
                    rs.getFloat("Total"),
                    rs.getString("MetodoPago"),
                    rs.getInt("UsuarioId"),
                    rs.getString("Facturada"),
                    rs.getString("FolioFactura")
                );
                listaVentas.add(venta);
            }
            cbVenta.setItems(listaVentas);
        } catch (SQLException e) {
            mostrarError("Error al cargar ventas", e.getMessage());
            e.printStackTrace();
       }
    }
    
    private void cargarDatos() {
        try {
            listaMovimientos = FXCollections.observableArrayList(obtenerMovimientosBD());
            tablaMovimientos.setItems(listaMovimientos);
            if (!listaMovimientos.isEmpty()) {
                tablaMovimientos.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar movimientos", e.getMessage());
        }
    }
    
    private ArrayList<MovimientoInventario> obtenerMovimientosBD() throws SQLException {
        ArrayList<MovimientoInventario> movimientos = new ArrayList<>();
        Connection conexion = Conexion.getInstancia().getConexion();
        try (CallableStatement stmt = conexion.prepareCall("{call sp_ListarMovimientosInventario()}")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MovimientoInventario movimiento = new MovimientoInventario();
                movimiento.setIdMovimiento(rs.getInt("IdMovimiento"));
                movimiento.setIdProducto(rs.getInt("ProductoId"));
                movimiento.setTipo(rs.getString("TipoMovimiento"));
                movimiento.setCantidad(rs.getInt("Cantidad"));
                movimiento.setFecha(rs.getDate("FechaMovimiento").toLocalDate());
                movimiento.setIdVenta(rs.getInt("VentaId"));
                movimiento.setIdProveedor(rs.getInt("ProveedorId"));
                movimiento.setComentario(rs.getString("Comentario"));
                movimientos.add(movimiento);
            }
        }
        return movimientos;
    }
    
    private void mostrarMovimientoSeleccionado(MovimientoInventario movimiento) {
        if (movimiento != null) {
            txtID.setText(String.valueOf(movimiento.getIdMovimiento()));
            cbTipo.setValue(movimiento.getTipo());
            txtCantidad.setText(String.valueOf(movimiento.getCantidad()));
            dpFecha.setValue(movimiento.getFecha());
            txtComentario.setText(movimiento.getComentario());

            for (Producto producto : cbProducto.getItems()) {
                if (producto.getIdProducto() == movimiento.getIdProducto()) {
                    cbProducto.setValue(producto);
                    break;
                }
            }

            if (movimiento.getIdVenta() != 0) {
                for (Venta venta : cbVenta.getItems()) {
                    if (venta.getIdVenta() == movimiento.getIdVenta()) {
                        cbVenta.setValue(venta);
                        break;
                    }
                }
            } else {
                cbVenta.getSelectionModel().clearSelection();
            }

            if (movimiento.getIdProveedor() != 0) {
                for (Proveedor proveedor : cbProveedor.getItems()) {
                    if (proveedor.getIdProveedor() == movimiento.getIdProveedor()) {
                        cbProveedor.setValue(proveedor);
                        break;
                    }
                }
            } else {
                cbProveedor.getSelectionModel().clearSelection();
            }
        }
    }
    
    private MovimientoInventario obtenerModeloDesdeFormulario() {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setIdMovimiento(txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText()));
        movimiento.setTipo(cbTipo.getValue());
        movimiento.setCantidad(Integer.parseInt(txtCantidad.getText()));
        movimiento.setFecha(dpFecha.getValue());
        movimiento.setComentario(txtComentario.getText());

        if (cbProducto.getValue() != null) {
            movimiento.setIdProducto(cbProducto.getValue().getIdProducto());
        }

        if (cbVenta.getValue() != null) {
            movimiento.setIdVenta(cbVenta.getValue().getIdVenta());
        } else {
            movimiento.setIdVenta(0);
        }

        if (cbProveedor.getValue() != null) {
            movimiento.setIdProveedor(cbProveedor.getValue().getIdProveedor());
        } else {
            movimiento.setIdProveedor(0);
        }

        return movimiento;
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
                    agregarMovimiento();
                    restaurarEstadoOriginal();
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    actualizarMovimiento();
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
    
    private void agregarMovimiento() {
        modeloMovimiento = obtenerModeloDesdeFormulario();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement stmt = conexion.prepareCall("{call sp_agregarmovimientoinventario(?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, modeloMovimiento.getIdProducto());
            stmt.setString(2, modeloMovimiento.getTipo());
            stmt.setInt(3, modeloMovimiento.getCantidad());
            stmt.setDate(4, java.sql.Date.valueOf(modeloMovimiento.getFecha()));
            
            if (modeloMovimiento.getIdVenta() == 0) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(5, modeloMovimiento.getIdVenta());
            }
            
            if (modeloMovimiento.getIdProveedor() == 0) {
                stmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(6, modeloMovimiento.getIdProveedor());
            }
            
            stmt.setString(7, modeloMovimiento.getComentario());

            stmt.registerOutParameter(8, java.sql.Types.INTEGER);

            stmt.execute();

            int nuevoId = stmt.getInt(8);
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al agregar movimiento", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarMovimiento() {
        modeloMovimiento = obtenerModeloDesdeFormulario();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarMovimientoInventario(?,?,?,?,?,?,?,?)}");
            stmt.setInt(1, modeloMovimiento.getIdMovimiento());
            stmt.setInt(2, modeloMovimiento.getIdProducto());
            stmt.setString(3, modeloMovimiento.getTipo());
            stmt.setInt(4, modeloMovimiento.getCantidad());
            stmt.setDate(5, java.sql.Date.valueOf(modeloMovimiento.getFecha()));
            
            if (modeloMovimiento.getIdVenta() == 0) {
                stmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(6, modeloMovimiento.getIdVenta());
            }
            
            if (modeloMovimiento.getIdProveedor() == 0) {
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(7, modeloMovimiento.getIdProveedor());
            }
            
            stmt.setString(8, modeloMovimiento.getComentario());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al actualizar movimiento", e.getMessage());
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
            eliminarMovimiento();
        } else {
            cancelarAccion();
        }
    }
    
    private void eliminarMovimiento() {
        modeloMovimiento = obtenerModeloDesdeFormulario();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarMovimientosInventario(?)}");
            stmt.setInt(1, modeloMovimiento.getIdMovimiento());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarError("Error al eliminar movimiento", e.getMessage());
        }
    }
    
    private void cancelarAccion() {
        cancelando = true;
        boolean estabaEditando = (accion == Acciones.EDITAR);
        restaurarEstadoOriginal();
        
        if (estabaEditando) {
            mostrarMovimientoSeleccionado(tablaMovimientos.getSelectionModel().getSelectedItem());
        } else if (!listaMovimientos.isEmpty()) {
            tablaMovimientos.getSelectionModel().selectFirst();
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
        cbTipo.getSelectionModel().clearSelection();
        txtCantidad.clear();
        dpFecha.setValue(LocalDate.now());
        txtComentario.clear();
        cbProducto.getSelectionModel().clearSelection();
        cbVenta.getSelectionModel().clearSelection();
        cbProveedor.getSelectionModel().clearSelection();
    }
    
    private void habilitarCampos() {
        cbTipo.setDisable(false);
        txtCantidad.setDisable(false);
        dpFecha.setDisable(false);
        txtComentario.setDisable(false);
        cbProducto.setDisable(false);
        cbVenta.setDisable(false);
        cbProveedor.setDisable(false);
        deshabilitarNavegacion();
    }
    
    private void deshabilitarCampos() {
        cbTipo.setDisable(true);
        txtCantidad.setDisable(true);
        dpFecha.setDisable(true);
        txtComentario.setDisable(true);
        cbProducto.setDisable(true);
        cbVenta.setDisable(true);
        cbProveedor.setDisable(true);
        habilitarNavegacion();
    }
    
    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaMovimientos.setDisable(true);
    }
    
    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaMovimientos.setDisable(false);
    }
    
    @FXML
    private void navegarSiguiente() {
        int indice = tablaMovimientos.getSelectionModel().getSelectedIndex();
        if (indice < listaMovimientos.size() - 1) {
            tablaMovimientos.getSelectionModel().select(indice + 1);
        }
    }
    
    @FXML
    private void navegarAnterior() {
        int indice = tablaMovimientos.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaMovimientos.getSelectionModel().select(indice - 1);
        }
    }
    
    @FXML
    private void buscarMovimientos() {
        String criterio = txtBuscar.getText().toLowerCase();
        if (criterio.isEmpty()) {
            tablaMovimientos.setItems(listaMovimientos);
        } else {
            ObservableList<MovimientoInventario> filtrados = listaMovimientos.filtered(movimiento ->
                String.valueOf(movimiento.getIdMovimiento()).contains(criterio) ||
                (cbProducto.getValue() != null && cbProducto.getValue().getNombre().toLowerCase().contains(criterio)) ||
                movimiento.getTipo().toLowerCase().contains(criterio) ||
                String.valueOf(movimiento.getCantidad()).contains(criterio)
            );
            tablaMovimientos.setItems(filtrados);
        }
    }
    
    private boolean validarFormulario() {
        if (cancelando) return true;
        
        if (cbTipo.getValue() == null || txtCantidad.getText().isEmpty() || 
            dpFecha.getValue() == null || cbProducto.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Los campos obligatorios son: Producto, Tipo, Cantidad y Fecha");
            return false;
        }
        
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            
            if (cantidad <= 0) {
                mostrarAlerta("Valor inválido", "La cantidad debe ser mayor a cero");
                return false;
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "Ingrese un valor numérico válido para la cantidad");
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