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
import org.laboratorio.model.Especificacion;
import org.laboratorio.system.Main;

import static javafx.scene.control.Alert.AlertType.WARNING;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import org.laboratorio.model.Producto;

/**
 *
 * @author Lu0
 */
public class EspecificacionesController implements Initializable {
    private Main principal;
    private Especificacion modeloEspecificacion;
    private ObservableList<Especificacion> listarEspecificaciones;
    private ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Especificacion> tablaEspecificaciones;
    @FXML private TableColumn colId, colProductoId, colCaracteristica, colValor, colUnidad;
    @FXML private TextField txtID, txtCaracteristica, txtValor, txtUnidad, txtBuscar;
    @FXML private ComboBox<Producto> cbxProducto;
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarProductos();
        cargarDatos();
        configurarEventos();
        tablaEspecificaciones.setOnMouseClicked(eventHandler -> getEspecificacionTextField());
        deshabilitarCampos(); 
    }  
    
    public void setFormatoColumnaModelo(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idEspecificacion"));
        colProductoId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCaracteristica.setCellValueFactory(new PropertyValueFactory<>("caracteristica"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));        
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
    }
    
    public void cargarDatos(){
        ArrayList<Especificacion> especificaciones = listarEspecificaciones();
        listarEspecificaciones = FXCollections.observableArrayList(especificaciones);
        tablaEspecificaciones.setItems(listarEspecificaciones);
        tablaEspecificaciones.getSelectionModel().selectFirst();
        getEspecificacionTextField();
    }
    
    public void getEspecificacionTextField() {
        Especificacion especificacion = tablaEspecificaciones.getSelectionModel().getSelectedItem();
        if (especificacion != null) {
            txtID.setText(String.valueOf(especificacion.getIdEspecificacion()));

            if (productosList != null) {
                productosList.stream()
                    .filter(p -> p.getIdProducto() == especificacion.getIdProducto())
                    .findFirst()
                    .ifPresent(p -> cbxProducto.getSelectionModel().select(p));
            }

            txtCaracteristica.setText(especificacion.getCaracteristica());
            txtValor.setText(especificacion.getValor());
            txtUnidad.setText(especificacion.getUnidad());
        }
    }
    
    public ArrayList<Especificacion> listarEspecificaciones(){
        ArrayList<Especificacion> especificaciones = new ArrayList<>();
        try {
            Connection conexionv = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarEspecificacionesProducto}";
            CallableStatement enunciado = conexionv.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()){
                especificaciones.add(new Especificacion(
                    resultado.getInt(1),
                    resultado.getInt(2),
                    resultado.getString(3),
                    resultado.getString(4),
                    resultado.getString(5)
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
        return especificaciones;
    }
    
    private Especificacion getModeloEspecificacion() {
        int idProducto = cbxProducto.getValue() != null ? 
                        cbxProducto.getValue().getIdProducto() : 0;

        return new Especificacion(
            txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText()),
            idProducto,
            txtCaracteristica.getText(),
            txtValor.getText(),
            txtUnidad.getText()
        );
    }
    
    public void agregarEspecificacion(){
        modeloEspecificacion = getModeloEspecificacion();
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_AgregarEspecificacion(?,?,?,?)}");
            enunciado.setInt(1, modeloEspecificacion.getIdProducto());
            enunciado.setString(2, modeloEspecificacion.getCaracteristica());
            enunciado.setString(3, modeloEspecificacion.getValor());
            enunciado.setString(4, modeloEspecificacion.getUnidad());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException ex) {
            System.out.println("Error al agregar:" + ex.getSQLState());
            ex.printStackTrace();
        }
    }
    
    public void editarEspecificacion(){
        modeloEspecificacion = getModeloEspecificacion();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_ActualizarEspecificacion(?,?,?,?,?)}");
            enunciado.setInt(1, modeloEspecificacion.getIdEspecificacion());
            enunciado.setInt(2, modeloEspecificacion.getIdProducto());
            enunciado.setString(3, modeloEspecificacion.getCaracteristica());
            enunciado.setString(4, modeloEspecificacion.getValor());
            enunciado.setString(5, modeloEspecificacion.getUnidad());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void eliminarEspecificacion(){
        modeloEspecificacion = getModeloEspecificacion();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                .prepareCall("{call sp_EliminarEspecificacion(?)}");
            enunciado.setInt(1, modeloEspecificacion.getIdEspecificacion());
            enunciado.execute();
            cargarDatos();
        } catch (SQLException e) {
            System.out.println("Error al eliminar. " + e.getMessage());
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
                    rs.getTimestamp("FechaCreacion").toLocalDateTime().toLocalDate()
                );
                productosList.add(producto);
            }
        
            cbxProducto.setItems(productosList);
        
            cbxProducto.setCellFactory(lv -> new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%s - %s %s", 
                            item.getNombre(), 
                            item.getMarca(), 
                            item.getModelo()));
                    }
                }
            });
        
            cbxProducto.setButtonCell(new ListCell<Producto>() {
                @Override
                protected void updateItem(Producto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Seleccione un producto");
                    } else {
                        setText(item.getNombre());
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar los productos");
        }
    }
    
    private void configurarEventos() {
        tablaEspecificaciones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                getEspecificacionTextField();
            }
        });
    }
    
    public void limpiarTexto(){
        txtID.clear();
        cbxProducto.getSelectionModel().clearSelection();
        txtCaracteristica.clear();
        txtValor.clear();
        txtUnidad.clear();
    }
    
    @FXML
    private void cambiarNuevoEspecificacion() {    
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
                    agregarEspecificacion();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
            case EDITAR:
                if (validarFormulario()) {
                    editarEspecificacion();
                    cambiarOriginal();
                    deshabilitarCampos(); 
                    habilitarNavegacion(); 
                }
                break;
        }
    }

    @FXML
    private void cambiarEdicionEspecificacion() {
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
            eliminarEspecificacion();
        } else if (accion == Acciones.AGREGAR || accion == Acciones.EDITAR) {
            cancelando = true;
            boolean estabaEditando = (accion == Acciones.EDITAR); 
            cambiarOriginal();
            habilitarDeshabilitarNodo();

            if(estabaEditando) {  
                getEspecificacionTextField();
            } else {
                tablaEspecificaciones.getSelectionModel().selectFirst();
            }

            cancelando = false;
        }    
    }
    
    private void habilitarCampos() {
        cbxProducto.setDisable(false);
        txtCaracteristica.setDisable(false);
        txtValor.setDisable(false);
        txtUnidad.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaEspecificaciones.setDisable(true);
    }

    private void deshabilitarCampos() {
        cbxProducto.setDisable(true);
        txtCaracteristica.setDisable(true);
        txtValor.setDisable(true);
        txtUnidad.setDisable(true);

        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaEspecificaciones.setDisable(false);
    }

    private void deshabilitarNavegacion() {
        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaEspecificaciones.setDisable(true);
    }

    private void habilitarNavegacion() {
        btnSiguiente.setDisable(false);
        btnAnterior.setDisable(false);
        tablaEspecificaciones.setDisable(false);
    }
    
    @FXML
    private void btnSiguienteAccion(){
        int indice = tablaEspecificaciones.getSelectionModel().getSelectedIndex();
        if (indice < listarEspecificaciones.size()-1) {
            tablaEspecificaciones.getSelectionModel().select(indice+1);
            getEspecificacionTextField();
        } 
    }
    
    @FXML
    private void btnAnteriorAccion(){
        int indice = tablaEspecificaciones.getSelectionModel().getSelectedIndex();
        if (indice > 0) {
            tablaEspecificaciones.getSelectionModel().select(indice-1);
            getEspecificacionTextField();
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
        txtCaracteristica.setDisable(estado);
        txtValor.setDisable(estado);
        txtUnidad.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = cbxProducto.isDisable();
        cambiarEstado(!deshabilitado);
        btnSiguiente.setDisable(deshabilitado);
        btnAnterior.setDisable(deshabilitado);
        tablaEspecificaciones.setDisable(deshabilitado);
    }
    
    @FXML
    private void btnBuscarPorCaracteristica(){
        ArrayList<Especificacion> resultadoBusqueda = new ArrayList<>();
        String caracteristicaBuscada = txtBuscar.getText();
        for(Especificacion especificacion: listarEspecificaciones) {
            if(especificacion.getCaracteristica().toLowerCase().contains(caracteristicaBuscada.toLowerCase())) {
                resultadoBusqueda.add(especificacion);
            }
        }
        tablaEspecificaciones.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (resultadoBusqueda.isEmpty()) {
            tablaEspecificaciones.getSelectionModel().selectFirst();
        }
    }
    
    private boolean validarFormulario() {
        if (cbxProducto.getValue() == null) {
            mostrarAlerta("Validación", "Debe seleccionar un producto");
            cbxProducto.requestFocus();
            return false;
        }
        if (txtCaracteristica.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "La característica es requerida");
            txtCaracteristica.requestFocus();
            return false;
        }
        if (txtValor.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El valor es requerido");
            txtValor.requestFocus();
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