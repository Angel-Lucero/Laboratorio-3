
package org.laboratorio.model;

import java.time.LocalDate;
import java.util.Date;


/**
 *
 * @author Lu0
 */
public class Producto {
    private int idProducto;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private String marca;
    private String modelo;
    private float precioVenta;
    private float precioCompra;
    private int existencia;
    private int stockMinimo;
    private int categoriaId;
    private int garantiaMeses;
    private String color;
    private float peso;
    private String dimensiones;
    private String activo;
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;

    public Producto(int idProducto, String codigoBarras, String nombre, String descripcion, String marca, String modelo, float precioVenta, float precioCompra, int existencia, int stockMinimo, int categoriaId, int garantiaMeses, String color, float peso, String dimensiones, String activo, LocalDate fechaCreacion, LocalDate fechaActualizacion) {
        this.idProducto = idProducto;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.precioVenta = precioVenta;
        this.precioCompra = precioCompra;
        this.existencia = existencia;
        this.stockMinimo = stockMinimo;
        this.categoriaId = categoriaId;
        this.garantiaMeses = garantiaMeses;
        this.color = color;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public float getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(float precioVenta) {
        this.precioVenta = precioVenta;
    }

    public float getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(float precioCompra) {
        this.precioCompra = precioCompra;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public int getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(int garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

   
}
