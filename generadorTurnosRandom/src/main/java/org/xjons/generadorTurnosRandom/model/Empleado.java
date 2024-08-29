package org.xjons.generadorTurnosRandom.model;

public class Empleado {

	private Long id;

	private String nombre;
	private boolean puedeIrAQuirofano; // Nuevo campo

	// Constructor vacío necesario para JPA
	public Empleado() {
	}

	// Constructor para inicialización rápida
	public Empleado(String nombre, boolean puedeIrAQuirofano) {
		this.nombre = nombre;
		this.puedeIrAQuirofano = puedeIrAQuirofano;
	}

	// Getters y setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isPuedeIrAQuirofano() {
		return puedeIrAQuirofano;
	}

	public void setPuedeIrAQuirofano(boolean puedeIrAQuirofano) {
		this.puedeIrAQuirofano = puedeIrAQuirofano;
	}
}
