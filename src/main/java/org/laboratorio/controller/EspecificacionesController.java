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

public class EspecificacionesController implements Initializable {
    private Main principal;
    private Especificacion modeloEspecificacion;
    private ObservableList<Especificacion> listarEspecificaciones;
    private enum Acciones {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    private Acciones accion = Acciones.NINGUNA;
    private boolean cancelando = false;
    
    @FXML private Button btnAnterior, btnSiguiente, btnNuevo, btnEditar, btnEliminar, btnReporte;
    @FXML private TableView<Especificacion> tablaEspecificaciones;
    @FXML private TableColumn colId, colProductoId, colCaracteristica, colValor, colUnidad;
    @FXML private TextField txtID, txtProductoId, txtCaracteristica, txtValor, txtUnidad, txtBuscar;
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
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
    
    public void getEspecificacionTextField(){
        Especificacion especificacionSeleccionada = tablaEspecificaciones.getSelectionModel().getSelectedItem();
        if (especificacionSeleccionada != null) {
            txtID.setText(String.valueOf(especificacionSeleccionada.getIdEspecificacion()));
            txtProductoId.setText(String.valueOf(especificacionSeleccionada.getIdProducto()));
            txtCaracteristica.setText(especificacionSeleccionada.getCaracteristica());
            txtValor.setText(especificacionSeleccionada.getValor());
            txtUnidad.setText(especificacionSeleccionada.getUnidad());
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
    
    private Especificacion getModeloEspecificacion(){
        int idEspecificacion = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());
        int idProducto = Integer.parseInt(txtProductoId.getText());
        String caracteristica = txtCaracteristica.getText();
        String valor = txtValor.getText();
        String unidad = txtUnidad.getText();
        
        return new Especificacion(
            idEspecificacion, idProducto, caracteristica, valor, unidad
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
    
    public void limpiarTexto(){
        txtID.clear();
        txtProductoId.clear();
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
        txtProductoId.setDisable(false);
        txtCaracteristica.setDisable(false);
        txtValor.setDisable(false);
        txtUnidad.setDisable(false);

        btnSiguiente.setDisable(true);
        btnAnterior.setDisable(true);
        tablaEspecificaciones.setDisable(true);
    }

    private void deshabilitarCampos() {
        txtProductoId.setDisable(true);
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
        txtProductoId.setDisable(estado);
        txtCaracteristica.setDisable(estado);
        txtValor.setDisable(estado);
        txtUnidad.setDisable(estado);
    }
    
    private void habilitarDeshabilitarNodo(){
        boolean deshabilitado = txtProductoId.isDisable();
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
        if(cancelando) return true; 
        
        if (txtProductoId.getText().isEmpty() || txtCaracteristica.getText().isEmpty() || 
            txtValor.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete los campos obligatorios (Producto ID, Característica y Valor).");
            return false;
        }
        
        try {
            int productoId = Integer.parseInt(txtProductoId.getText());
            if (productoId <= 0) {
                mostrarAlerta("ID de Producto inválido", "El ID de Producto debe ser un número positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "El ID de Producto debe ser un número válido.");
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