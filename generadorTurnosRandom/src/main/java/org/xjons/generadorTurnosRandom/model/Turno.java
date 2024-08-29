package org.xjons.generadorTurnosRandom.model;

public class Turno {

    private Long id;

    private int equipo;
    private String intervalo;

    private Empleado empleado;

    // Constructor vacío necesario para JPA
    public Turno() {
    }

    // Constructor para inicialización rápida
    public Turno(int equipo, String intervalo, Empleado empleado) {
        this.equipo = equipo;
        this.intervalo = intervalo;
        this.empleado = empleado;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEquipo() {
        return equipo;
    }

    public void setEquipo(int equipo) {
        this.equipo = equipo;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}