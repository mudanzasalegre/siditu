package es.jlalegredev.siditu.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Turno {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int rueda;
	private int intervalo; // 1 si no está subdividido, 2 si está subdividido
	private LocalDate fecha;
	private char turno; // 'M' para Mañana, 'N' para Noche
	private LocalTime horaInicio; // Hora de inicio del turno del empleado
	private LocalTime horaFin; // Hora de fin del turno del empleado
	private boolean quirofano; // Indica si el empleado ha sido seleccionado para quirófano

	@ManyToOne
	@JoinColumn(name = "empleado_id", nullable = false)
	private Empleado empleado;

	@OneToMany(mappedBy = "turno", cascade = CascadeType.REMOVE) // Eliminación en cascada
	private List<Puntuacion> puntuaciones;

	// Constructor vacío
	public Turno() {
	}

	// Constructor completo
	public Turno(int rueda, int intervalo, LocalDate fecha, char turno, LocalTime horaInicio, LocalTime horaFin,
			Empleado empleado, boolean quirofano) {
		this.rueda = rueda;
		this.intervalo = intervalo;
		this.fecha = fecha;
		this.turno = turno;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.empleado = empleado;
		this.quirofano = quirofano;
	}

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRueda() {
		return rueda;
	}

	public void setRueda(int rueda) {
		this.rueda = rueda;
	}

	public int getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(int intervalo) {
		this.intervalo = intervalo;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public char getTurno() {
		return turno;
	}

	public void setTurno(char turno) {
		this.turno = turno;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public boolean isQuirofano() {
		return quirofano;
	}

	public void setQuirofano(boolean quirofano) {
		this.quirofano = quirofano;
	}

	public List<Puntuacion> getPuntuaciones() {
		return puntuaciones;
	}

	public void setPuntuaciones(List<Puntuacion> puntuaciones) {
		this.puntuaciones = puntuaciones;
	}
}
