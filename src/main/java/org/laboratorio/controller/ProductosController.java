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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.laboratorio.model.Categoria;

/**
 * FXML Controller class
 *
 * @author Lu0
 */
public class ProductosController implements Initializable {
    private Main principal;
    private Producto modeloProducto;
    private ObservableList<Producto> listarProductos;
    private ObservableList<Categoria> categoriasList = FXCollections.observableArrayList();
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
        txtModelo, txtPrecioVenta, txtStockMinimo,txtGarantiaMeses, txtColor,
            txtDimensiones, txtUrlImagen, txtBuscar;
    @FXML private Spinner<Double> spnPeso; 
    @FXML private ImageView imgProducto; 
    @FXML private DatePicker dpFechaCreacion;
    @FXML private ComboBox<Categoria> cbxCategoria;
        

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinner();
        setFormatoColumnaModelo();
        cargarDatos();
        cargarCategorias();
        tablaProductos.setOnMouseClicked(eventHandler -> getProductoTextField());
        deshabilitarCampos(); 
        txtUrlImagen.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarImagen(newValue);
        });
    }
      
    
    private void configurarSpinner() {
        SpinnerValueFactory<Double> valueFactory = 
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1000.0, 5.0, 0.1);
        spnPeso.setValueFactory(valueFactory);
    }
    
    private void cargarImagen(String nombreImagen) {
        if (nombreImagen != null && !nombreImagen.isEmpty()) {
            try {
                String rutaImagen = "/org/laboratorio/image/" + nombreImagen + ".png";
                Image image = new Image(getClass().getResourceAsStream(rutaImagen));
                imgProducto.setImage(image);
            } catch (Exception e) {
                cargarImagenPorDefecto();
            }
        } else {
            cargarImagenPorDefecto();
        }
    }
    
    private void cargarImagenPorDefecto() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream(
                "/org/laboratorio/image/default.png"));
            imgProducto.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen por defecto");
            imgProducto.setImage(null);
        }
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
            txtGarantiaMeses.setText(String.valueOf(productoSeleccionado.getGarantiaMeses()));
            txtColor.setText(productoSeleccionado.getColor());
            spnPeso.getValueFactory().setValue((double)productoSeleccionado.getPeso());
            txtDimensiones.setText(productoSeleccionado.getDimensiones());
            txtUrlImagen.setText(productoSeleccionado.getUrlImagen());
            dpFechaCreacion.setValue(productoSeleccionado.getFechaCreacion());
            
            if (productoSeleccionado.getCategoriaId() > 0) {
                cbxCategoria.getSelectionModel().clearSelection();
                for (Categoria cat : categoriasList) {
                    if (cat.getIdCategoria() == productoSeleccionado.getCategoriaId()) {
                        cbxCategoria.getSelectionModel().select(cat);
                        break;
                    }
                }
            }
            
            cargarImagen(productoSeleccionado.getUrlImagen());
        }
    }
    
    private void cargarCategorias() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarCategorias}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

            categoriasList.clear();
            while (rs.next()) {
                categoriasList.add(new Categoria(
                    rs.getInt("idCategoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("tipo")
                        
                ));
            }

            cbxCategoria.setItems(categoriasList);

            cbxCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
                @Override
                protected void updateItem(Categoria item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getNombre() + " - " + item.getTipo());
                }
            });

            cbxCategoria.setButtonCell(new ListCell<Categoria>() {
                @Override
                protected void updateItem(Categoria item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "Seleccione categoría" : item.getNombre() + " - " + item.getTipo());
                }
            });

        } catch (SQLException e) {
            System.out.println("Error al cargar categorías: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las categorías");
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
    
    private Producto getModeloProducto() throws NumberFormatException {
        int idProducto = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String marca = txtMarca.getText();
        String modelo = txtModelo.getText();

        float precioVenta;
        int stockMinimo, categoriaId, garantiaMeses;

        try {
            precioVenta = Float.parseFloat(txtPrecioVenta.getText());
            stockMinimo = Integer.parseInt(txtStockMinimo.getText());
            categoriaId = cbxCategoria.getValue() != null ? cbxCategoria.getValue().getIdCategoria() : 0;
            garantiaMeses = Integer.parseInt(txtGarantiaMeses.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Los campos numéricos deben contener solo números");
            throw e; 
        }

        String color = txtColor.getText();
        float peso = spnPeso.getValue().floatValue(); 
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
       cbxCategoria.getSelectionModel().clearSelection();
       txtGarantiaMeses.clear();
       txtColor.clear();
       txtDimensiones.clear();
       spnPeso.getValueFactory().setValue(0.0);
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
                try {
                    if(validarFormulario()){
                        agregarProducto();
                        cambiarOriginal();
                        deshabilitarCampos(); 
                        habilitarNavegacion(); 
                    }
                } catch (NumberFormatException e) {
                }
                break;
            case EDITAR:
                try {
                    if (validarFormulario()) {
                        editarProducto();
                        cambiarOriginal();
                        deshabilitarCampos(); 
                        habilitarNavegacion(); 
                    }
                } catch (NumberFormatException e) {
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
        cbxCategoria.setDisable(false);
        txtGarantiaMeses.setDisable(false);
        txtColor.setDisable(false);
        spnPeso.setDisable(false);
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
        cbxCategoria.setDisable(true);
        txtGarantiaMeses.setDisable(true);
        txtColor.setDisable(true);
        spnPeso.setDisable(true);
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
        cbxCategoria.setDisable(estado);
        txtGarantiaMeses.setDisable(estado);
        txtColor.setDisable(estado);
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
        if (cancelando) return true;

        if (txtNombre.getText().isEmpty() || 
            txtPrecioVenta.getText().isEmpty() || 
            txtStockMinimo.getText().isEmpty() || 
            cbxCategoria.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Complete todos los campos obligatorios (*)");
            return false;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El nombre del producto es obligatorio");
            return false;
        }

        if (txtPrecioVenta.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El precio de venta es obligatorio");
            return false;
        }

        if (txtStockMinimo.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El stock mínimo es obligatorio");
            return false;
        }

        if (cbxCategoria.getValue() == null) { 
            mostrarAlerta("Campo requerido", "Debe seleccionar una categoría");
            return false;
        }
 
        try {
            float precioVenta = Float.parseFloat(txtPrecioVenta.getText());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

            if (precioVenta <= 0) {
                mostrarAlerta("Valor inválido", "El precio debe ser mayor a 0");
                return false;
            }

            if (stockMinimo < 0) {
                mostrarAlerta("Valor inválido", "El stock mínimo no puede ser negativo");
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "Los campos numéricos deben contener solo números");
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