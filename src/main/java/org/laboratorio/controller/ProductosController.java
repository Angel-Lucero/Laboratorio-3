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
    @FXML private TableColumn colId, colNombre, colDescripcion, colMarca, 
            colModelo, colPrecioVenta, colStockMinimo, colCategoriaId, 
            colGarantiaMeses, colColor, colPeso, colDimensiones, 
            colActivo, colFechaCreacion;
    @FXML private TextField txtID, txtNombre, txtDescripcion, txtMarca, 
        txtModelo, txtPrecioVenta, txtStockMinimo, txtCategoriaId, 
        txtGarantiaMeses, txtColor, txtPeso, txtDimensiones, txtUrlImagen, txtBuscar;
    @FXML private DatePicker dpFechaCreacion;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));        
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colCategoriaId.setCellValueFactory(new PropertyValueFactory<>("categoriaId"));
        colGarantiaMeses.setCellValueFactory(new PropertyValueFactory<>("garantiaMeses"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colDimensiones.setCellValueFactory(new PropertyValueFactory<>("dimensiones"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
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
            txtNombre.setText(productoSeleccionado.getNombre());
            txtDescripcion.setText(productoSeleccionado.getDescripcion());
            txtMarca.setText(productoSeleccionado.getMarca());
            txtModelo.setText(productoSeleccionado.getModelo());
            txtPrecioVenta.setText(String.valueOf(productoSeleccionado.getPrecioVenta()));
            txtStockMinimo.setText(String.valueOf(productoSeleccionado.getStockMinimo()));
            txtCategoriaId.setText(String.valueOf(productoSeleccionado.getCategoriaId()));
            txtGarantiaMeses.setText(String.valueOf(productoSeleccionado.getGarantiaMeses()));
            txtColor.setText(productoSeleccionado.getColor());
            txtPeso.setText(String.valueOf(productoSeleccionado.getPeso()));
            txtDimensiones.setText(productoSeleccionado.getDimensiones());
            txtUrlImagen.setText(productoSeleccionado.getUrlImagen());
            dpFechaCreacion.setValue(productoSeleccionado.getFechaCreacion());
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
                productos.add(new Producto(
                    resultado.getInt(1),
                    resultado.getString(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    resultado.getString(5),
                    resultado.getFloat(6),
                    resultado.getInt(7),
                    resultado.getInt(8),
                    resultado.getInt(9),
                    resultado.getString(10),
                    resultado.getFloat(11),
                    resultado.getString(12),
                    resultado.getString(13),
                    resultado.getDate(14).toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.out.println("error al cargar: " + e.getMessage());
        }
        return productos;
    }
    
    private Producto getModeloProducto(){
        int idProducto = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();
        float precioVenta = Float.parseFloat(txtPrecioVenta.getText());
        int stockMinimo = Integer.parseInt(txtStockMinimo.getText());
        int categoriaId = Integer.parseInt(txtCategoriaId.getText());
        int garantiaMeses = Integer.parseInt(txtGarantiaMeses.getText());
        String color = txtColor.getText();
        float peso = Float.parseFloat(txtPeso.getText());
        String dimensiones = txtDimensiones.getText();
        String urlImagen = txtUrlImagen.getText();
        LocalDate fechaCreacion = dpFechaCreacion.getValue();

        return new Producto(
            idProducto, nombre, descripcion, marca, modelo, precioVenta,
            stockMinimo, categoriaId, garantiaMeses, color, peso, 
            dimensiones, urlImagen, fechaCreacion
        );
    }
    
    public void agregarProducto(){
        modeloProducto = getModeloProducto();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarProducto(?,?,?,?,?,?,?,?,?,?,?,?)}");
            enunciado.setString(1, modeloProducto.getNombre());
            enunciado.setString(2, modeloProducto.getDescripcion());
            enunciado.setString(3, modeloProducto.getMarca());
            enunciado.setString(4, modeloProducto.getModelo());
            enunciado.setFloat(5, modeloProducto.getPrecioVenta());
            enunciado.setInt(6, modeloProducto.getStockMinimo());
            enunciado.setInt(7, modeloProducto.getCategoriaId());
            enunciado.setInt(8, modeloProducto.getGarantiaMeses());
            enunciado.setString(9, modeloProducto.getColor());
            enunciado.setFloat(10, modeloProducto.getPeso());
            enunciado.setString(11, modeloProducto.getDimensiones());
            enunciado.setString(12, modeloProducto.getUrlImagen());
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
                .prepareCall("{call sp_ActualizarProducto(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloProducto.getIdProducto());
            enunciado.setString(2, modeloProducto.getNombre());
            enunciado.setString(3, modeloProducto.getDescripcion());
            enunciado.setString(4, modeloProducto.getMarca());
            enunciado.setString(5, modeloProducto.getModelo());
            enunciado.setFloat(6, modeloProducto.getPrecioVenta());
            enunciado.setInt(7, modeloProducto.getStockMinimo());
            enunciado.setInt(8, modeloProducto.getCategoriaId());
            enunciado.setInt(9, modeloProducto.getGarantiaMeses());
            enunciado.setString(10, modeloProducto.getColor());
            enunciado.setFloat(11, modeloProducto.getPeso());
            enunciado.setString(12, modeloProducto.getDimensiones());
            enunciado.setString(13, modeloProducto.getUrlImagen());
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
       txtNombre.clear();
       txtDescripcion.clear();
       txtMarca.clear();
       txtModelo.clear();
       txtPrecioVenta.clear();
       txtStockMinimo.clear();
       txtCategoriaId.clear();
       txtGarantiaMeses.clear();
       txtColor.clear();
       txtPeso.clear();
       txtDimensiones.clear();
       txtUrlImagen.clear();
       dpFechaCreacion.setValue(LocalDate.now());
    }
    
    @FXML
    private void cambiarNuevoProducto() {    
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
                    agregarProducto();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarProducto();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionProducto() {
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
    
    private void habilitarCampos() {
        txtNombre.setDisable(false);
        txtDescripcion.setDisable(false);
        txtMarca.setDisable(false);
        txtModelo.setDisable(false);
        txtPrecioVenta.setDisable(false);
        txtStockMinimo.setDisable(false);
        txtCategoriaId.setDisable(false);
        txtGarantiaMeses.setDisable(false);
        txtColor.setDisable(false);
        txtPeso.setDisable(false);
        txtDimensiones.setDisable(false);
        txtUrlImagen.setDisable(false);
        dpFechaCreacion.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProductos.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtNombre.setDisable(true);
        txtDescripcion.setDisable(true);
        txtMarca.setDisable(true);
        txtModelo.setDisable(true);
        txtPrecioVenta.setDisable(true);
        txtStockMinimo.setDisable(true);
        txtCategoriaId.setDisable(true);
        txtGarantiaMeses.setDisable(true);
        txtColor.setDisable(true);
        txtPeso.setDisable(true);
        txtDimensiones.setDisable(true);
        txtUrlImagen.setDisable(true);
        dpFechaCreacion.setDisable(true);

        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaProductos.setDisable(false);
    }

    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProductos.setDisable(true);
    }

    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaProductos.setDisable(false);
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
        txtNombre.setDisable(estado);
        txtDescripcion.setDisable(estado);
        txtMarca.setDisable(estado);
        txtModelo.setDisable(estado);
        txtPrecioVenta.setDisable(estado);
        txtStockMinimo.setDisable(estado);
        txtCategoriaId.setDisable(estado);
        txtGarantiaMeses.setDisable(estado);
        txtColor.setDisable(estado);
        txtPeso.setDisable(estado);
        txtDimensiones.setDisable(estado);
        txtUrlImagen.setDisable(estado);
        dpFechaCreacion.setDisable(estado);
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
        
        if (txtNombre.getText().isEmpty() || txtPrecioVenta.getText().isEmpty() || 
            txtStockMinimo.getText().isEmpty() || txtCategoriaId.getText().isEmpty() || 
            txtUrlImagen.getText().isEmpty() || dpFechaCreacion.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        
        try {
            float precioVenta = Float.parseFloat(txtPrecioVenta.getText());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText());
            int categoriaId = Integer.parseInt(txtCategoriaId.getText());
            
            if (precioVenta <= 0 || stockMinimo < 0 || categoriaId <= 0) {
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