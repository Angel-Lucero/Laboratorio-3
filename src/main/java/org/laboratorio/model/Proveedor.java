
package org.laboratorio.model;

/**
 *
 * @author Lu0
 */
public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private String especialidad;

    public Proveedor(int idProveedor, String nombre, String telefono, String email, String direccion, String especialidad) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.especialidad = especialidad;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "ID: " + idProveedor + ", Nombre: " + nombre + ", Telefono: " + telefono;
    }
    
    
}
