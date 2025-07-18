package org.laboratorio.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import org.laboratorio.model.Producto;
import org.laboratorio.model.Proveedor;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProductoProveedorController implements Initializable {
    private Main principal;
    private ProductoProveedor modeloProductoProveedor;
    private ObservableList<ProductoProveedor> listarRelaciones;
    private ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private ObservableList<Proveedor> proveedoresList = FXCollections.observableArrayList();
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<ProductoProveedor> tablaProductoProveedor;
    @FXML private TableColumn colId, colProductoId, colProveedorId, 
            colPrecioCompra, colTiempoEntrega;
    @FXML private TextField txtID, txtProductoId, txtProveedorId, txtPrecioCompra,
            txtTiempoEntrega, txtBuscar;
    @FXML private ComboBox<Producto> cbxProducto;
    @FXML private ComboBox<Proveedor> cbxProveedor;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();       
        cargarProductos();
        cargarProveedores();
        cargarDatos();
        tablaProductoProveedor.setOnMouseClicked(eventHandler -> getProductoProveedorTextField());
        deshabilitarCampos(); 
    }  
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idRelacion"));
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProveedorId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
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
    
    public void getProductoProveedorTextField() {
        ProductoProveedor relacionSeleccionada = tablaProductoProveedor.getSelectionModel().getSelectedItem();
        if (relacionSeleccionada != null) {
            txtID.setText(String.valueOf(relacionSeleccionada.getIdRelacion()));

            productosList.stream()
                .filter(p -> p.getIdProducto() == relacionSeleccionada.getIdProducto())
                .findFirst()
                .ifPresent(cbxProducto::setValue);

            proveedoresList.stream()
                .filter(prov -> prov.getIdProveedor() == relacionSeleccionada.getIdProveedor())
                .findFirst()
                .ifPresent(cbxProveedor::setValue);

            txtPrecioCompra.setText(String.valueOf(relacionSeleccionada.getPrecioCompra()));
            txtTiempoEntrega.setText(String.valueOf(relacionSeleccionada.getDiasEntrega()));
        }
    }
    
    private void cargarProductos() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarProductos()}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

            productosList.clear();

            while (rs.next()) {
                productosList.add(new Producto(
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
                ));
            }

            configurarComboBoxProducto();

        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los productos");
        }
    }
    
    private void configurarComboBoxProducto() {
        cbxProducto.setItems(productosList);

        cbxProducto.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });

        cbxProducto.setButtonCell(new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Seleccione un producto" : item.getNombre());
            }
        });
    }
    
    private void configurarComboBoxProveedor() {
        cbxProveedor.setItems(proveedoresList);

        cbxProveedor.setCellFactory(lv -> new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });

        cbxProveedor.setButtonCell(new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Seleccione un proveedor" : item.getNombre());
            }
        });
    }
    
    
    private void cargarProveedores() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarProveedores()}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

            proveedoresList.clear();

            while (rs.next()) {
                proveedoresList.add(new Proveedor(
                    rs.getInt("IdProveedor"),
                    rs.getString("Nombre"),
                    rs.getString("Telefono"),
                    rs.getString("Email"),
                    rs.getString("Direccion"),
                    rs.getString("Especialidad")
                ));
            }

            configurarComboBoxProveedor();

        } catch (SQLException e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los proveedores");
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
                    resultado.getFloat(4),
                    resultado.getInt(5)
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
        return relaciones;
    }
    
    private ProductoProveedor getModeloProductoProveedor() {
        int idRelacion = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        int idProducto = cbxProducto.getValue() != null ? cbxProducto.getValue().getIdProducto() : 0;
        int idProveedor = cbxProveedor.getValue() != null ? cbxProveedor.getValue().getIdProveedor() : 0;
        float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
        int tiempoEntrega = Integer.parseInt(txtTiempoEntrega.getText());

        return new ProductoProveedor(
            idRelacion, idProducto, idProveedor, precioCompra, tiempoEntrega
        );
    }
    
    public void agregarRelacion(){
        modeloProductoProveedor = getModeloProductoProveedor();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarProductoProveedor(?,?,?,?,?)}");
            enunciado.setInt(1, modeloProductoProveedor.getIdProducto());
            enunciado.setInt(2, modeloProductoProveedor.getIdProveedor());
            enunciado.setFloat(3, modeloProductoProveedor.getPrecioCompra());
            enunciado.setInt(4, modeloProductoProveedor.getDiasEntrega());
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
            enunciado.setFloat(4, modeloProductoProveedor.getPrecioCompra());
            enunciado.setInt(5, modeloProductoProveedor.getDiasEntrega());
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
    
    public void limpiarTexto() {
        txtID.clear();
        cbxProducto.getSelectionModel().clearSelection();
        cbxProveedor.getSelectionModel().clearSelection();
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
        cbxProducto.setDisable(false);
        cbxProveedor.setDisable(false);
        txtPrecioCompra.setDisable(false);
        txtTiempoEntrega.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaProductoProveedor.setDisable(true);
    }

    private void deshabilitarCampos() {
        cbxProducto.setDisable(true);
        cbxProveedor.setDisable(true);
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
        cbxProducto.setDisable(estado);
        cbxProveedor.setDisable(estado);
        txtPrecioCompra.setDisable(estado);
        txtTiempoEntrega.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo() {
        boolean deshabilitado = cbxProducto.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaProductoProveedor.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorRelacion(){
        ArrayList<ProductoProveedor> resultadoBusqueda = new ArrayList<>();
        String textoBuscado = txtBuscar.getText().toLowerCase();

        for(ProductoProveedor relacion: listarRelaciones) {
            boolean matchProducto = productosList.stream()
                .filter(p -> p.getIdProducto() == relacion.getIdProducto())
                .anyMatch(p -> p.getNombre().toLowerCase().contains(textoBuscado));

            boolean matchProveedor = proveedoresList.stream()
                .filter(p -> p.getIdProveedor() == relacion.getIdProveedor())
                .anyMatch(p -> p.getNombre().toLowerCase().contains(textoBuscado));

            if(matchProducto || matchProveedor) {
                resultadoBusqueda.add(relacion);
            }
        }

        tablaProductoProveedor.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            mostrarAlerta("Búsqueda", "No se encontraron coincidencias");
            tablaProductoProveedor.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if (cancelando) return true;
        if (cbxProducto.getValue() == null) {
            mostrarAlerta("Validación", "Debe seleccionar un producto");
            cbxProducto.requestFocus();
            return false;
        }
        if (cbxProveedor.getValue() == null) {
            mostrarAlerta("Validación", "Debe seleccionar un proveedor");
            cbxProveedor.requestFocus();
            return false;
        }
        try {
            float precioCompra = Float.parseFloat(txtPrecioCompra.getText());
            int tiempoEntrega = Integer.parseInt(txtTiempoEntrega.getText());

            if (precioCompra <= 0 || tiempoEntrega <= 0) {
                mostrarAlerta("Validación", "Precio y tiempo de entrega deben ser mayores a cero");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y tiempo de entrega deben ser valores numéricos");
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