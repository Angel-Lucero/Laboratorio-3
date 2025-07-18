
package org.laboratorio.model;

/**
 *
 * @author Lu0
 */
public class Especificacion {
    private int idEspecificacion;
    private int idProducto;
    private String caracteristica;
    private String valor;
    private String unidad;

    public Especificacion(int idEspecificacion, int idProducto, String caracteristica, String valor, String unidad) {
        this.idEspecificacion = idEspecificacion;
        this.idProducto = idProducto;
        this.caracteristica = caracteristica;
        this.valor = valor;
        this.unidad = unidad;
    }

    public int getIdEspecificacion() {
        return idEspecificacion;
    }

    public void setIdEspecificacion(int idEspecificacion) {
        this.idEspecificacion = idEspecificacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(String caracteristica) {
        this.caracteristica = caracteristica;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    @Override
    public String toString() {
        return "ID: " + idEspecificacion + ", Producto: " + idProducto + ", Valor: " + valor ;
    }

    
    

}
