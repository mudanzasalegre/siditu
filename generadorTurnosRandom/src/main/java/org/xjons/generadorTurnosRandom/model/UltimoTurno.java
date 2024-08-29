package org.xjons.generadorTurnosRandom.model;

import java.util.List;

public class UltimoTurno {
    private String turno;
    private List<String> asignaciones;
    private String empleadoQuirofano;
    private String horarioInicio;
    private String horarioFin;

    // Getters y Setters

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public List<String> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<String> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String getEmpleadoQuirofano() {
        return empleadoQuirofano;
    }

    public void setEmpleadoQuirofano(String empleadoQuirofano) {
        this.empleadoQuirofano = empleadoQuirofano;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(String horarioFin) {
        this.horarioFin = horarioFin;
    }
}
