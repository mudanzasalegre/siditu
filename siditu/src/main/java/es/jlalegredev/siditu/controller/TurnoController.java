/*
 * Sistema de Distribución de Turnos SIDITU - Propiedad Intelectual
 * Derechos de autor (c) - 2024 - mudanzasalegre
 * 
 * Este software y la documentación asociada son propiedad de JL Alegre (el "Autor").
 * 
 * Permiso de uso:
 * Se concede permiso para usar este software y la documentación asociada para fines internos dentro de [Nombre de la Institución] únicamente.
 * 
 * Restricciones:
 * 1. No se permite la copia, modificación, distribución, venta, sublicencia o transferencia de este software sin el permiso expreso y por escrito del Autor.
 * 2. Este software no puede ser usado para fines comerciales sin el consentimiento previo por escrito del Autor.
 * 
 * Propiedad Intelectual:
 * Este software es y seguirá siendo propiedad intelectual del Autor.
 * 
 * Garantía y Responsabilidad:
 * Este software se proporciona "tal cual", sin garantía de ningún tipo, expresa o implícita, incluyendo pero no limitándose a las garantías de comerciabilidad, idoneidad para un propósito particular y no infracción. En ningún caso el Autor será responsable por cualquier reclamo, daño o responsabilidad, ya sea en una acción de contrato, agravio o de otro tipo, que surja de o en conexión con el software o el uso u otros tratos en el software.
 * 
 * Contacto:
 * Para solicitar permiso o información adicional, por favor contacta a mudanzasalegre@hotmail.com
 */

package es.jlalegredev.siditu.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jlalegredev.siditu.model.Empleado;
import es.jlalegredev.siditu.model.Turno;
import es.jlalegredev.siditu.service.EmpleadoService;
import es.jlalegredev.siditu.service.TurnoService;

@Controller
public class TurnoController {

	@Autowired
	private TurnoService turnoService;

	@Autowired
	private EmpleadoService empleadoService;

	@GetMapping("/turnos")
	public String mostrarTurnos(Model model) {
		ZoneId zoneId = ZoneId.of("Europe/Madrid");
		ZonedDateTime now = ZonedDateTime.now(zoneId);

		// Determinar el turno actual basado en la hora
		char turnoActual = now.getHour() < 8 || now.getHour() >= 20 ? 'N' : 'M';

		// Ajustar la fecha para turnos de noche (entre 20:00 y las 8:00)
		LocalDate today = now.toLocalDate();
		if (turnoActual == 'N' && now.getHour() < 8) {
			today = today.minusDays(1);
		}

		// Obtener los turnos del día y turno actual
		List<Turno> turnos = turnoService.obtenerTurnosDelDia(today, turnoActual);
		if (turnos.isEmpty()) {
			model.addAttribute("mensaje", "No se ha generado un turno aún.");
			return "index";
		}

		// Buscar el empleado que puede ir a quirófano
		Empleado empleadoQuirofano = turnos.stream().filter(turno -> turno.getEmpleado().isPuedeIrAQuirofano()).findFirst()
				.map(Turno::getEmpleado).orElse(null);

		// Agregar los datos al modelo para la vista
		model.addAttribute("turnos", turnos);
		model.addAttribute("turno", turnoActual == 'M' ? "Mañana" : "Noche");
		model.addAttribute("empleadoQuirofano", empleadoQuirofano);

		return "turnos";
	}

	@PostMapping("/generarTurnos")
	public String generarTurnos(@RequestParam int numeroDePersonas,
			@RequestParam(defaultValue = "false") boolean subdividir, @RequestParam(defaultValue = "false") boolean aleatorio,
			@RequestParam Map<String, String> empleados, Model model) {

		ZoneId zoneId = ZoneId.of("Europe/Madrid");
		ZonedDateTime now = ZonedDateTime.now(zoneId);
		LocalDate today = now.toLocalDate();
		LocalTime time = now.toLocalTime();

		// Determinar el turno actual
		char turnoActual = time.isBefore(LocalTime.of(8, 0)) ? 'N' : time.isBefore(LocalTime.of(20, 0)) ? 'M' : 'N';

		// Ajustar la fecha para turnos de noche (entre 20:00 y las 8:00)
		if (turnoActual == 'N' && time.isAfter(LocalTime.of(20, 0))) {
			today = now.toLocalDate(); // Ya es después de las 20:00, usar la fecha actual
		} else if (turnoActual == 'N' && time.isBefore(LocalTime.of(8, 0))) {
			today = today.minusDays(1); // Es después de la medianoche pero antes de las 8:00, usar la fecha anterior
		}

		if (!turnoService.obtenerTurnosDelDia(today, turnoActual).isEmpty()) {
			model.addAttribute("mensaje", "ERROR: Ya se ha generado un turno para este intervalo.");
			return "index";
		}

		Set<String> uniqueNombres = new HashSet<>();
		List<Empleado> empleadosTurno = new ArrayList<>();

		for (int i = 0; i < numeroDePersonas; i++) {
			String nombre = empleados.get("empleados[" + i + "].nombre").trim();
			boolean puedeIrAQuirofano = empleados.containsKey("empleados[" + i + "].puedeIrAQuirofano");

			if (uniqueNombres.contains(nombre)) {
				model.addAttribute("mensaje", "ERROR: El empleado " + nombre + " está duplicado.");
				return "index";
			}

			uniqueNombres.add(nombre);

			List<Empleado> encontrados = empleadoService.buscarEmpleadosPorNombre(nombre);
			Empleado empleado;

			if (encontrados.isEmpty()) {
				empleado = new Empleado(nombre, puedeIrAQuirofano);
				empleadoService.guardarEmpleado(empleado);
			} else {
				empleado = encontrados.get(0);
				empleado.setPuedeIrAQuirofano(puedeIrAQuirofano);
				empleadoService.guardarEmpleado(empleado);
			}

			empleadosTurno.add(empleado);
		}

		// Aplicar el orden aleatorio si se seleccionó la opción
		if (aleatorio) {
			Collections.shuffle(empleadosTurno);
		}

		// Asignar un empleado a quirófano de manera aleatoria entre los que pueden
		Empleado empleadoQuirofano = seleccionarEmpleadoQuirofano(empleadosTurno);

		// Generar los turnos según la configuración
		List<Turno> turnos = generarAsignacionesDeTurno(empleadosTurno, today, turnoActual, subdividir);

		// Guardar los turnos en la base de datos
		turnoService.guardarTurnos(turnos);

		// Pasar los datos al modelo para su visualización
		model.addAttribute("turnos", turnos);
		model.addAttribute("empleadoQuirofano", empleadoQuirofano);
		model.addAttribute("turno", turnoActual == 'M' ? "Mañana" : "Noche");

		return "turnos";
	}

	private Empleado seleccionarEmpleadoQuirofano(List<Empleado> empleadosTurno) {
		List<Empleado> empleadosDisponibles = new ArrayList<>();

		for (Empleado empleado : empleadosTurno) {
			if (empleado.isPuedeIrAQuirofano()) {
				empleadosDisponibles.add(empleado);
			}
		}

		if (!empleadosDisponibles.isEmpty()) {
			return empleadosDisponibles.get(new Random().nextInt(empleadosDisponibles.size()));
		}

		return null;
	}

	private List<Turno> generarAsignacionesDeTurno(List<Empleado> empleadosTurno, LocalDate today, char turnoActual,
			boolean subdividir) {

		int numeroDePersonas = empleadosTurno.size();
		int intervalo = subdividir ? 2 : 1;
		int totalMinutos = 12 * 60; // 12 horas convertidas a minutos
		int minutosPorPersona = totalMinutos / (numeroDePersonas * intervalo);

		LocalTime startTime = turnoActual == 'M' ? LocalTime.of(8, 0) : LocalTime.of(20, 0);
		List<Turno> turnos = new ArrayList<>();

		for (int rep = 0; rep < intervalo; rep++) {
			LocalTime slotStartTime = startTime.plusMinutes(rep * totalMinutos / intervalo);

			for (Empleado empleado : empleadosTurno) {
				LocalTime slotEndTime = slotStartTime.plusMinutes(minutosPorPersona);

				Turno turno = new Turno();
				turno.setEmpleado(empleado);
				turno.setFecha(today);
				turno.setTurno(turnoActual);
				turno.setIntervalo(intervalo);
				turno.setEquipo(rep + 1); // Asignar equipo basado en el ciclo actual
				turno.setHoraInicio(slotStartTime);
				turno.setHoraFin(slotEndTime);

				turnos.add(turno);
				slotStartTime = slotEndTime;
			}
		}

		return turnos;
	}
}
