package es.jlalegredev.siditu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Empleado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@Column(name = "puede_ir_a_quirofano")
	private boolean puedeIrAQuirofano;

	@Column(name = "equipo")
	private Integer equipo; // Número del equipo (1 a 6), puede ser NULL

	@OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Turno> turnos;

	public Empleado() {
	}

	public Empleado(String nombre, boolean puedeIrAQuirofano, Integer equipo) {
		this.nombre = nombre;
		this.puedeIrAQuirofano = puedeIrAQuirofano;
		this.equipo = equipo;
	}

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

	public Integer getEquipo() {
		return equipo;
	}

	public void setEquipo(Integer equipo) {
		this.equipo = equipo;
	}

	public List<Turno> getTurnos() {
		return turnos;
	}

	public void setTurnos(List<Turno> turnos) {
		this.turnos = turnos;
	}
}
