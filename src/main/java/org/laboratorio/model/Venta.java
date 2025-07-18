
package org.laboratorio.model;

/**
 *
 * @author Lu0
 */
public class Venta {
    private int idVenta;
    private int idCliente;
    private String fechaVenta;
    private float subtotal;
    private float iva;
    private float total;
    private String metodoPago;
    private int usuarioId;
    private String facturada;
    private String folioFactura;

    public Venta(int idVenta, int idCliente, String fechaVenta, float subtotal, float iva, float total, String metodoPago, int usuarioId, String facturada, String folioFactura) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fechaVenta = fechaVenta;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.metodoPago = metodoPago;
        this.usuarioId = usuarioId;
        this.facturada = facturada;
        this.folioFactura = folioFactura;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getFacturada() {
        return facturada;
    }

    public void setFacturada(String facturada) {
        this.facturada = facturada;
    }

    public String getFolioFactura() {
        return folioFactura;
    }

    public void setFolioFactura(String folioFactura) {
        this.folioFactura = folioFactura;
    }   

    @Override
    public String toString() {
        return "ID: " + idVenta + ", Cliente: " + idCliente + ", Total: " + total + ", Metodo: " + metodoPago + ", Factuaración: " + facturada;
    }
    
    
}
