package com.rutinas_habitos;

public class TonoAudio {
    private long id;
    private long usuarioId;
    private String nombre;
    private String rutaArchivo;
    private String duracion;
    private String fechaSubida;

    public TonoAudio() {}

    public TonoAudio(long id, long usuarioId, String nombre, String rutaArchivo, String duracion, String fechaSubida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.rutaArchivo = rutaArchivo;
        this.duracion = duracion;
        this.fechaSubida = fechaSubida;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(String fechaSubida) { this.fechaSubida = fechaSubida; }
}
