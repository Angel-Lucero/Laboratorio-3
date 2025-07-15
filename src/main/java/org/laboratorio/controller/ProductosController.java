
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.laboratorio.database.Conexion;
import org.laboratorio.model.Producto;
import org.laboratorio.system.Main;
import static javafx.scene.control.Alert.AlertType.WARNING;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProductosController implements Initializable {
    private Main principal;
    private Producto modeloProducto;
    private ObservableList<Producto> listarProductos;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn colId, colCodigoBarras, colNombre, colDescripcion, colMarca, 
            colModelo, colPrecioVenta, colPrecioCompra, colExistencia, colStockMinimo,
            colCategoriaId, colGarantiaMeses, colColor, colPeso, colDimensiones, 
            colActivo, colFechaCreacion, colFechaActualizacion;
    @FXML private TextField txtID, txtCodigoBarras, txtNombre, txtDescripcion, txtMarca, 
            txtModelo, txtPrecioVenta, txtPrecioCompra, txtExistencia, txtStockMinimo,
            txtCategoriaId, txtGarantiaMeses, txtColor, txtPeso, txtDimensiones, 
            txtActivo, txtBuscar;
    @FXML private DatePicker dpFechaCreacion, dpFechaActualizacion;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaProductos.setOnMouseClicked(eventHandler -> getProductoTextField());
        deshabilitarCampos();
    }    
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("IdProducto"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<Producto, String>("CodigoBarras"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Producto, String>("Nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Producto, String>("Descripcion"));
        colMarca.setCellValueFactory(new PropertyValueFactory<Producto, String>("Marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<Producto, String>("Modelo"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<Producto, Float>("PrecioVenta"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<Producto, Float>("precioCompra"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("Existencia"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("StockMinimo"));
        colCategoriaId.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("CategoriaId"));
        colGarantiaMeses.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("GarantiaMeses"));
        colColor.setCellValueFactory(new PropertyValueFactory<Producto, String>("Color"));
        colPeso.setCellValueFactory(new PropertyValueFactory<Producto, Float>("Peso"));
        colDimensiones.setCellValueFactory(new PropertyValueFactory<Producto, String>("Dimensiones"));
        colActivo.setCellValueFactory(new PropertyValueFactory<Producto, String>("Activo"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<Producto, Date>("FechaCreacion"));
        colFechaActualizacion.setCellValueFactory(new PropertyValueFactory<Producto, Date>("FechaActualizacion"));
    }
    
    public void cargarDatos(){
        ArrayList<Producto> productos = listarProducto();
        listarProductos = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(listarProductos);
        tablaProductos.getSelectionModel().selectFirst();
        getProductoTextField();
    }
    
    public void getProductoTextField(){
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtID.setText(String.valueOf(productoSeleccionado.getIdProducto()));
            txtCodigoBarras.setText(productoSeleccionado.getCodigoBarras());
            txtNombre.setText(productoSeleccionado.getNombre());
            txtDescripcion.setText(productoSeleccionado.getDescripcion());
            txtMarca.setText(productoSeleccionado.getMarca());
            txtModelo.setText(productoSeleccionado.getModelo());
            txtPrecioVenta.setText(String.valueOf(productoSeleccionado.getPrecioVenta()));
            txtPrecioCompra.setText(String.valueOf(productoSeleccionado.getPrecioCompra()));
            txtExistencia.setText(String.valueOf(productoSeleccionado.getExistencia()));
            txtStockMinimo.setText(String.valueOf(productoSeleccionado.getStockMinimo()));
            txtCategoriaId.setText(String.valueOf(productoSeleccionado.getCategoriaId()));
            txtGarantiaMeses.setText(String.valueOf(productoSeleccionado.getGarantiaMeses()));
            txtColor.setText(productoSeleccionado.getColor());
            txtPeso.setText(String.valueOf(productoSeleccionado.getPeso()));
            txtDimensiones.setText(productoSeleccionado.getDimensiones());
            txtActivo.setText(productoSeleccionado.getActivo());
            dpFechaCreacion.setValue(productoSeleccionado.getFechaCreacion());
            dpFechaActualizacion.setValue(productoSeleccionado.getFechaActualizacion());
        }
    }
    
    public ArrayList<Producto> listarProducto(){
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarProductos}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                LocalDate fechaCreacion = resultado.getDate(17) != null ? 
                resultado.getDate(17).toLocalDate() : null;
            LocalDate fechaActualizacion = resultado.getDate(18) != null ? 
                resultado.getDate(18).toLocalDate() : null;
            
                productos.add(new Producto(
                    resultado.getInt(1),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    resultado.getString(5),
                    resultado.getString(6),
                    resultado.getFloat(7),
                    resultado.getFloat(8),
                    resultado.getInt(9),
                    resultado.getInt(10),
                    resultado.getInt(11),
                    resultado.getInt(12),
                    resultado.getString(13),
                    resultado.getFloat(14),
                    resultado.getString(15),
                    resultado.getString(16),
                    fechaCreacion,
                    fechaActualizacion
                ));
            }
        } catch (SQLException e) {
            System.out.println("error al cargar: " + e.getMessage());
        }
        return productos;
    }
    
    private Producto getModeloProducto(){
        int idProducto = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        String codigoBarras = txtCodigoBarras.getText();
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        float precioVenta = Float.parseFloat(txtPrecioVenta.getText());
        float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
        int existencia = Integer.parseInt(txtExistencia.getText());
        int stockMinimo = Integer.parseInt(txtStockMinimo.getText());
        int categoriaId = Integer.parseInt(txtCategoriaId.getText());
        int garantiaMeses = Integer.parseInt(txtGarantiaMeses.getText());
        String color = txtColor.getText();
        float peso = Float.parseFloat(txtPeso.getText());
        String dimensiones = txtDimensiones.getText();
        String activo = txtActivo.getText();
        LocalDate fechaCreacion = dpFechaCreacion.getValue();
        LocalDate fechaActualizacion = dpFechaActualizacion.getValue();
        
        return new Producto(
            idProducto, codigoBarras, nombre, descripcion, marca, modelo,
            precioVenta, precioCompra, existencia, stockMinimo, categoriaId,
            garantiaMeses, color, peso, dimensiones, activo, fechaCreacion,
            fechaActualizacion
        );
    }
    
    public void agregarProducto(){
        modeloProducto = getModeloProducto();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarProducto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            enunciado.setString(1, modeloProducto.getCodigoBarras());
            enunciado.setString(2, modeloProducto.getNombre());
            enunciado.setString(3, modeloProducto.getDescripcion());
            enunciado.setString(4, modeloProducto.getMarca());
            enunciado.setString(5, modeloProducto.getModelo());
            enunciado.setFloat(6, modeloProducto.getPrecioVenta());
            enunciado.setFloat(7, modeloProducto.getPrecioCompra());
            enunciado.setInt(8, modeloProducto.getExistencia());
            enunciado.setInt(9, modeloProducto.getStockMinimo());
            enunciado.setInt(10, modeloProducto.getCategoriaId());
            enunciado.setInt(11, modeloProducto.getGarantiaMeses());
            enunciado.setString(12, modeloProducto.getColor());
            enunciado.setFloat(13, modeloProducto.getPeso());
            enunciado.setString(14, modeloProducto.getDimensiones());
            enunciado.setString(15, modeloProducto.getActivo());
            enunciado.setDate(16, modeloProducto.getFechaCreacion() != null ? 
                 java.sql.Date.valueOf(modeloProducto.getFechaCreacion()) : null);
            enunciado.setDate(17, modeloProducto.getFechaActualizacion() != null ? 
                java.sql.Date.valueOf(modeloProducto.getFechaActualizacion()) : null);
            enunciado.execute();
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarProducto(){
        modeloProducto = getModeloProducto();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarProducto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloProducto.getIdProducto());
            enunciado.setString(2, modeloProducto.getCodigoBarras());
            enunciado.setString(3, modeloProducto.getNombre());
            enunciado.setString(4, modeloProducto.getDescripcion());
            enunciado.setString(5, modeloProducto.getMarca());
            enunciado.setString(6, modeloProducto.getModelo());
            enunciado.setFloat(7, modeloProducto.getPrecioVenta());
            enunciado.setFloat(8, modeloProducto.getPrecioCompra());
            enunciado.setInt(9, modeloProducto.getExistencia());
            enunciado.setInt(10, modeloProducto.getStockMinimo());
            enunciado.setInt(11, modeloProducto.getCategoriaId());
            enunciado.setInt(12, modeloProducto.getGarantiaMeses());
            enunciado.setString(13, modeloProducto.getColor());
            enunciado.setFloat(14, modeloProducto.getPeso());
            enunciado.setString(15, modeloProducto.getDimensiones());
            enunciado.setString(16, modeloProducto.getActivo());
            enunciado.setDate(16, modeloProducto.getFechaCreacion() != null ? 
                java.sql.Date.valueOf(modeloProducto.getFechaCreacion()) : null);
            enunciado.setDate(17, modeloProducto.getFechaActualizacion() != null ? 
                java.sql.Date.valueOf(modeloProducto.getFechaActualizacion()) : null);
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " +  e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarProducto(){
        modeloProducto = getModeloProducto();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarProducto(?)}");
            enunciado.setInt(1, modeloProducto.getIdProducto());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar. " + e.getMessage());
        }
    }
    
    public void limpiarTexto(){
        txtID.clear();
        txtCodigoBarras.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtMarca.clear();
        txtModelo.clear();
        txtPrecioVenta.clear();
        txtPrecioCompra.clear();
        txtExistencia.clear();
        txtStockMinimo.clear();
        txtCategoriaId.clear();
        txtGarantiaMeses.clear();
        txtColor.clear();
        txtPeso.clear();
        txtDimensiones.clear();
        txtActivo.clear();
        dpFechaCreacion.setValue(null);
        dpFechaActualizacion.setValue(null);
    }
    
    private void deshabilitarCampos() {
        txtCodigoBarras.setDisable(true);
        txtNombre.setDisable(true);
        txtDescripcion.setDisable(true);
        txtMarca.setDisable(true);
        txtModelo.setDisable(true);
        txtPrecioVenta.setDisable(true);
        txtPrecioCompra.setDisable(true);
        txtExistencia.setDisable(true);
        txtStockMinimo.setDisable(true);
        txtCategoriaId.setDisable(true);
        txtGarantiaMeses.setDisable(true);
        txtColor.setDisable(true);
        txtPeso.setDisable(true);
        txtDimensiones.setDisable(true);
        txtActivo.setDisable(true);
        dpFechaCreacion.setDisable(true);
        dpFechaActualizacion.setDisable(true);
    }
    
    @FXML
    private void cambiarNuevoProducto(){    
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
                    agregarProducto();
                    cambiarOriginal();
                    habilitarDeshabilitarNodo();
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    System.out.println("Accion del metodo editar");
                    editarProducto();
                    cambiarOriginal();
                    habilitarDeshabilitarNodo();
                }
                break;
        }
    }
    
    @FXML
    private void cambiarEdicionProducto(){
        cambiarGuardarEditar();
        accion = Acciones.EDITAR;
        habilitarDeshabilitarNodo();
    }
    
    @FXML
    private void cambiarCancelarEliminar(){
        if(accion == Acciones.NINGUNA) {
            eliminarProducto();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            habilitarDeshabilitarNodo();

            if(estabaEditando) {  
                getProductoTextField();
            } else {
                tablaProductos.getSelectionModel().selectFirst();
            }

            cancelando = false;
        }    
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice < listarProductos.size()-1) {
            tablaProductos.getSelectionModel().select(indice+1);
            getProductoTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaProductos.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaProductos.getSelectionModel().select(indice-1);
            getProductoTextField();
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
        txtCodigoBarras.setDisable(estado);
        txtNombre.setDisable(estado);
        txtDescripcion.setDisable(estado);
        txtMarca.setDisable(estado);
        txtModelo.setDisable(estado);
        txtPrecioVenta.setDisable(estado);
        txtPrecioCompra.setDisable(estado);
        txtExistencia.setDisable(estado);
        txtStockMinimo.setDisable(estado);
        txtCategoriaId.setDisable(estado);
        txtGarantiaMeses.setDisable(estado);
        txtColor.setDisable(estado);
        txtPeso.setDisable(estado);
        txtDimensiones.setDisable(estado);
        txtActivo.setDisable(estado);
        dpFechaCreacion.setDisable(estado);
        dpFechaActualizacion.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = txtNombre.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaProductos.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorNombre(){
        ArrayList<Producto> resultadoBusqueda = new ArrayList<>();
        String nombreBuscado = txtBuscar.getText();
        for(Producto producto: listarProductos) {
            if(producto.getNombre().toLowerCase().contains(nombreBuscado.toLowerCase())) {
                resultadoBusqueda.add(producto);
            }
        }
        tablaProductos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if(cancelando) return true; 
        
        // Validar campos obligatorios
        if (txtCodigoBarras.getText().isEmpty() || txtNombre.getText().isEmpty() || 
            txtPrecioVenta.getText().isEmpty() || txtPrecioCompra.getText().isEmpty() || 
            txtExistencia.getText().isEmpty() || txtStockMinimo.getText().isEmpty() || 
            txtCategoriaId.getText().isEmpty() || txtActivo.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        
        // Validar que los valores numéricos sean correctos
        try {
            float precioVenta = Float.parseFloat(txtPrecioVenta.getText());
            float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
            int existencia = Integer.parseInt(txtExistencia.getText());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText());
            int categoriaId = Integer.parseInt(txtCategoriaId.getText());
            
            if (precioVenta <= 0 || precioCompra <= 0 || existencia < 0 || stockMinimo < 0 || categoriaId <= 0) {
                mostrarAlerta("Valores inválidos", "Los valores numéricos deben ser positivos.");
                return false;
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "Por favor, ingrese valores numéricos válidos.");
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
