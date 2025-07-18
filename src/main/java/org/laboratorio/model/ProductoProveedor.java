
package org.laboratorio.model;

/**
 *
 * @author Lu0
 */
public class ProductoProveedor {
    private int idRelacion;
    private int idProducto;
    private int idProveedor;
    private String codigoProveedor;
    private float precioCompra ;
    private int diasEntrega;

    public ProductoProveedor(int idRelacion, int idProducto, int idProveedor, String codigoProveedor, float precioCompra, int diasEntrega) {
        this.idRelacion = idRelacion;
        this.idProducto = idProducto;
        this.idProveedor = idProveedor;
        this.codigoProveedor = codigoProveedor;
        this.precioCompra = precioCompra;
        this.diasEntrega = diasEntrega;
    }

    public int getIdRelacion() {
        return idRelacion;
    }

    public void setIdRelacion(int idRelacion) {
        this.idRelacion = idRelacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public float getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(float precioCompra) {
        this.precioCompra = precioCompra;
    }

    public int getDiasEntrega() {
        return diasEntrega;
    }

    public void setDiasEntrega(int diasEntrega) {
        this.diasEntrega = diasEntrega;
    }

    @Override
    public String toString() {
        return "ID: " + ", Relacion: " + idRelacion + ", Precio: " + precioCompra + ", Entrega en:" + diasEntrega + "dias";
    }

    
}
