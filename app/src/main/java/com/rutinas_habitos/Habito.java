package com.rutinas_habitos;

public class Habito {
    private long id;
    private long usuarioId;
    private String nombre;
    private String descripcion;
    private String icono;
    private String color;
    private String areaVida;
    private String tipoFrecuencia;
    private String diasSemana;
    private String tipoMeta;
    private int metaCantidad;
    private String unidad;
    private String horaRecordatorio;
    private boolean recordatorioActivo;
    private String tipoSonidoNotificacion;
    private int duracionSonidoSegundos;
    private int rachaDias;
    private boolean completadoHoy;
    private int progresoActual;
    private String fechaCreacion;

    public Habito() {
        this.icono = "meditacion";
        this.color = "#B8E0D2";
        this.areaVida = "Salud";
        this.tipoFrecuencia = "Diario";
        this.diasSemana = "L,M,M,J,V,S,D";
        this.tipoMeta = "simple";
        this.metaCantidad = 1;
        this.unidad = "veces";
        this.horaRecordatorio = "08:00 AM";
        this.recordatorioActivo = true;
        this.tipoSonidoNotificacion = "default";
        this.duracionSonidoSegundos = 30;
        this.rachaDias = 0;
        this.completadoHoy = false;
        this.progresoActual = 0;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getAreaVida() { return areaVida; }
    public void setAreaVida(String areaVida) { this.areaVida = areaVida; }

    public String getTipoFrecuencia() { return tipoFrecuencia; }
    public void setTipoFrecuencia(String tipoFrecuencia) { this.tipoFrecuencia = tipoFrecuencia; }

    public String getDiasSemana() { return diasSemana; }
    public void setDiasSemana(String diasSemana) { this.diasSemana = diasSemana; }

    public String getTipoMeta() { return tipoMeta; }
    public void setTipoMeta(String tipoMeta) { this.tipoMeta = tipoMeta; }

    public int getMetaCantidad() { return metaCantidad; }
    public void setMetaCantidad(int metaCantidad) { this.metaCantidad = metaCantidad; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getHoraRecordatorio() { return horaRecordatorio; }
    public void setHoraRecordatorio(String horaRecordatorio) { this.horaRecordatorio = horaRecordatorio; }

    public boolean isRecordatorioActivo() { return recordatorioActivo; }
    public void setRecordatorioActivo(boolean recordatorioActivo) { this.recordatorioActivo = recordatorioActivo; }

    public int getRachaDias() { return rachaDias; }
    public void setRachaDias(int rachaDias) { this.rachaDias = rachaDias; }

    public boolean isCompletadoHoy() { return completadoHoy; }
    public void setCompletadoHoy(boolean completadoHoy) { this.completadoHoy = completadoHoy; }

    public int getProgresoActual() { return progresoActual; }
    public void setProgresoActual(int progresoActual) { this.progresoActual = progresoActual; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoSonidoNotificacion() {
        return tipoSonidoNotificacion != null ? tipoSonidoNotificacion : "default";
    }
    public void setTipoSonidoNotificacion(String tipoSonidoNotificacion) {
        this.tipoSonidoNotificacion = tipoSonidoNotificacion;
    }

    public int getDuracionSonidoSegundos() {
        return duracionSonidoSegundos > 0 ? duracionSonidoSegundos : 30;
    }
    public void setDuracionSonidoSegundos(int duracionSonidoSegundos) {
        this.duracionSonidoSegundos = duracionSonidoSegundos;
    }
}
