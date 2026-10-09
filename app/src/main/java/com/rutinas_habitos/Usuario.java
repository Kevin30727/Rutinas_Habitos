package com.rutinas_habitos;

public class Usuario {
    private long id;
    private String nombre;
    private String correo;
    private String password;
    private String fechaRegistro;

    public Usuario() {}

    public Usuario(long id, String nombre, String correo, String password, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
