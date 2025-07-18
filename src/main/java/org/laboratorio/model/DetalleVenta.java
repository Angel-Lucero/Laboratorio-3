
package org.laboratorio.model;

/**
 *
 * @author informatica
 */
public class DetalleVenta {
    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private float precioUnitario;
    private float importe;

    public DetalleVenta(int idDetalle, int idVenta, int idProducto, int cantidad, float precioUnitario, float importe) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importe = importe;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    @Override
    public String toString() {
        return "ID: " + idDetalle + ", Venta: " + idVenta + ", Producto: " + idProducto + ", Precio: " + precioUnitario;
    }
    
    
}
