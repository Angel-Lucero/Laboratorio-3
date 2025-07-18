
package org.laboratorio.model;

/**
 *
 * @author Lu0
 */
public class Categoria {
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private String tipo;

    public Categoria(int idCategoria, String nombre, String descripcion, String tipo) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "ID:" + idCategoria + ", Nombre: " + nombre + ", Tipo: " + tipo;
    }
    
    
    
}
