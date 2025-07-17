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
import org.laboratorio.database.Conexion;
import org.laboratorio.model.ProductoProveedor;
import org.laboratorio.system.Main;

import static javafx.scene.control.Alert.AlertType.WARNING;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProductoProveedorController implements Initializable {
    private Main principal;
    private ProductoProveedor modeloProductoProveedor;
    private ObservableList<ProductoProveedor> listarRelaciones;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<ProductoProveedor> tablaProductoProveedor;
    @FXML private TableColumn colId, colProductoId, colProveedorId, colCodigoProveedor, 
            colPrecioCompra, colTiempoEntrega;
    @FXML private TextField txtID, txtProductoId, txtProveedorId, txtCodigoProveedor, 
            txtPrecioCompra, txtTiempoEntrega, txtBuscar;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaProductoProveedor.setOnMouseClicked(eventHandler -> getProductoProveedorTextField());
        deshabilitarCampos(); 
    }  
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idRelacion"));
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProveedorId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<>("codigoProveedor"));        
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colTiempoEntrega.setCellValueFactory(new PropertyValueFactory<>("diasEntrega"));
    }
    
    public void cargarDatos(){
        ArrayList<ProductoProveedor> relaciones = listarRelaciones();
        listarRelaciones = FXCollections.observableArrayList(relaciones);
        tablaProductoProveedor.setItems(listarRelaciones);
        tablaProductoProveedor.getSelectionModel().selectFirst();
        getProductoProveedorTextField();
    }
    
    public void getProductoProveedorTextField(){
        ProductoProveedor relacionSeleccionada = tablaProductoProveedor.getSelectionModel().getSelectedItem();
        if (relacionSeleccionada != null) {
            txtID.setText(String.valueOf(relacionSeleccionada.getIdRelacion()));
            txtProductoId.setText(String.valueOf(relacionSeleccionada.getIdProducto()));
            txtProveedorId.setText(String.valueOf(relacionSeleccionada.getIdProveedor()));
            txtCodigoProveedor.setText(relacionSeleccionada.getCodigoProveedor());
            txtPrecioCompra.setText(String.valueOf(relacionSeleccionada.getPrecioCompra()));
            txtTiempoEntrega.setText(String.valueOf(relacionSeleccionada.getDiasEntrega()));
        }
    }
    
    public ArrayList<ProductoProveedor> listarRelaciones(){
        ArrayList<ProductoProveedor> relaciones = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarProductoProveedor()}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                relaciones.add(new ProductoProveedor(
                    resultado.getInt(1),
                    resultado.getInt(2),
                    resultado.getInt(3),
                    resultado.getString(4),
                    resultado.getFloat(5),
                    resultado.getInt(6)
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
        return relaciones;
    }
    
    private ProductoProveedor getModeloProductoProveedor(){
        int idRelacion = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        int idProducto = Integer.parseInt(txtProductoId.getText());
        int idProveedor = Integer.parseInt(txtProveedorId.getText());
        String codigoProveedor = txtCodigoProveedor.getText();
        float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
        int tiempoEntrega = Integer.parseInt(txtTiempoEntrega.getText());
        
        return new ProductoProveedor(
            idRelacion, idProducto, idProveedor, codigoProveedor, precioCompra, tiempoEntrega
        );
    }
    
    public void agregarRelacion(){
        modeloProductoProveedor = getModeloProductoProveedor();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarProductoProveedor(?,?,?,?,?)}");
            enunciado.setInt(1, modeloProductoProveedor.getIdProducto());
            enunciado.setInt(2, modeloProductoProveedor.getIdProveedor());
            enunciado.setString(3, modeloProductoProveedor.getCodigoProveedor());
            enunciado.setFloat(4, modeloProductoProveedor.getPrecioCompra());
            enunciado.setInt(5, modeloProductoProveedor.getDiasEntrega());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarRelacion(){
        modeloProductoProveedor = getModeloProductoProveedor();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarProductoProveedor(?,?,?,?,?,?)}");
            enunciado.setInt(1, modeloProductoProveedor.getIdRelacion());
            enunciado.setInt(2, modeloProductoProveedor.getIdProducto());
            enunciado.setInt(3, modeloProductoProveedor.getIdProveedor());
            enunciado.setString(4, modeloProductoProveedor.getCodigoProveedor());
            enunciado.setFloat(5, modeloProductoProveedor.getPrecioCompra());
            enunciado.setInt(6, modeloProductoProveedor.getDiasEntrega());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarRelacion(){
        modeloProductoProveedor = getModeloProductoProveedor();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarProductoProveedor(?)}");
            enunciado.setInt(1, modeloProductoProveedor.getIdRelacion());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar. " + e.getMessage());
        }
    }
    
    public void limpiarTexto(){
        txtID.clear();
        txtProductoId.clear();
        txtProveedorId.clear();
        txtCodigoProveedor.clear();
        txtPrecioCompra.clear();
        txtTiempoEntrega.clear();
    }
    
    @FXML
    private void cambiarNuevaRelacion() {    
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
                    agregarRelacion();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarRelacion();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionRelacion() {
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
            eliminarRelacion();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            habilitarDeshabilitarNodo();

            if(estabaEditando) {  
                getProductoProveedorTextField();
            } else {
                tablaProductoProveedor.getSelectionModel().selectFirst();
            }

            cancelando = false;
        }    
    }
    
    private void habilitarCampos() {
        txtProductoId.setDisable(false);
        txtProveedorId.setDisable(false);
        txtCodigoProveedor.setDisable(false);
        txtPrecioCompra.setDisable(false);
        txtTiempoEntrega.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProductoProveedor.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtProductoId.setDisable(true);
        txtProveedorId.setDisable(true);
        txtCodigoProveedor.setDisable(true);
        txtPrecioCompra.setDisable(true);
        txtTiempoEntrega.setDisable(true);

        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaProductoProveedor.setDisable(false);
    }

    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProductoProveedor.setDisable(true);
    }

    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaProductoProveedor.setDisable(false);
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaProductoProveedor.getSelectionModel().getSelectedIndex();
        if (indice < listarRelaciones.size()-1) {
            tablaProductoProveedor.getSelectionModel().select(indice+1);
            getProductoProveedorTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaProductoProveedor.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaProductoProveedor.getSelectionModel().select(indice-1);
            getProductoProveedorTextField();
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
        txtProductoId.setDisable(estado);
        txtProveedorId.setDisable(estado);
        txtCodigoProveedor.setDisable(estado);
        txtPrecioCompra.setDisable(estado);
        txtTiempoEntrega.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = txtProductoId.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaProductoProveedor.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorCodigo(){
        ArrayList<ProductoProveedor> resultadoBusqueda = new ArrayList<>();
        String codigoBuscado = txtBuscar.getText();
        for(ProductoProveedor relacion: listarRelaciones) {
            if(relacion.getCodigoProveedor().toLowerCase().contains(codigoBuscado.toLowerCase())) {
                resultadoBusqueda.add(relacion);
            }
        }
        tablaProductoProveedor.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaProductoProveedor.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if(cancelando) return true; 
        
        if (txtProductoId.getText().isEmpty() || txtProveedorId.getText().isEmpty() || 
            txtPrecioCompra.getText().isEmpty() || txtTiempoEntrega.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete los campos obligatorios (ID Producto, ID Proveedor, Precio y Tiempo de Entrega).");
            return false;
        }
        
        try {
            int productoId = Integer.parseInt(txtProductoId.getText());
            int proveedorId = Integer.parseInt(txtProveedorId.getText());
            float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
            int tiempoEntrega = Integer.parseInt(txtTiempoEntrega.getText());
            
            if (productoId <= 0 || proveedorId <= 0 || precioCompra <= 0 || tiempoEntrega <= 0) {
                mostrarAlerta("Valores inválidos", "Los IDs, precio y tiempo de entrega deben ser valores positivos.");
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