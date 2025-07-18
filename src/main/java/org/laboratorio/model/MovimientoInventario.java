
package org.laboratorio.model;

import java.time.LocalDate;

/**
 *
 * @author Lu0
 */
public class MovimientoInventario {
    private int idMovimiento;
    private int idProducto;
    private String tipo; 
    private int cantidad;
    private LocalDate fecha;
    private int idVenta;
    private int idProveedor;
    private String comentario;

    public MovimientoInventario() {
    }
    
    public MovimientoInventario(int idMovimiento, int idProducto, String tipo, int cantidad, LocalDate fecha, int idVenta, int idProveedor, String comentario) {
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idVenta = idVenta;
        this.idProveedor = idProveedor;
        this.comentario = comentario;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "ID: " + idMovimiento + ", Tipo: " + tipo + ", Cantidad: " + cantidad;
    }
}
