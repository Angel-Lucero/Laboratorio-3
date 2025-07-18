
package org.laboratorio.model;

import java.time.LocalDate;


/**
 *
 * @author Lu0
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private String marca;
    private String modelo;
    private float precioVenta;
    private int stockMinimo;
    private int categoriaId;
    private int garantiaMeses;
    private String color;
    private float peso;
    private String dimensiones;
    private String UrlImagen;
    private LocalDate fechaCreacion;

    public Producto(int idProducto, String nombre, String descripcion, String marca, String modelo, float precioVenta, int stockMinimo, int categoriaId, int garantiaMeses, String color, float peso, String dimensiones, String UrlImagen, LocalDate fechaCreacion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.categoriaId = categoriaId;
        this.garantiaMeses = garantiaMeses;
        this.color = color;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.UrlImagen = UrlImagen;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
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

    public String getUrlImagen() {
        return UrlImagen;
    }

    public void setUrlImagen(String UrlImagen) {
        this.UrlImagen = UrlImagen;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return "ID: " + idProducto + ", Nombre: " + nombre + ", Marca: " + marca + ", Modelo: " + modelo;
    }   
}
