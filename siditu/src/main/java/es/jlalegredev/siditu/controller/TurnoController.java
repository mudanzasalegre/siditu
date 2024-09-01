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
import es.jlalegredev.siditu.model.Puntuacion;
import es.jlalegredev.siditu.model.Turno;
import es.jlalegredev.siditu.service.EmpleadoService;
import es.jlalegredev.siditu.service.PuntuacionService;
import es.jlalegredev.siditu.service.TurnoService;

@Controller
public class TurnoController {

	@Autowired
	private TurnoService turnoService;

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private PuntuacionService puntuacionService;

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
			model.addAttribute("mensajeError", "No se ha generado un turno aún.");
			return "error";
		}

		// Obtener los empleados ordenados por grupo y nombre
		List<Empleado> empleadosOrdenados = empleadoService.obtenerEmpleadosOrdenadosPorGrupoYNombre();

		// Pasar los empleados ordenados al modelo
		model.addAttribute("empleadosOrdenados", empleadosOrdenados);

		// Buscar el empleado que está marcado para quirófano en el turno actual
		Empleado empleadoQuirofano = turnos.stream().filter(turno -> turno.isQuirofano()) // Filtrar por el campo quirófano
				.map(Turno::getEmpleado).findFirst().orElse(null);

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

		try {
			ZoneId zoneId = ZoneId.of("Europe/Madrid");
			ZonedDateTime now = ZonedDateTime.now(zoneId);
			LocalDate today = now.toLocalDate();
			LocalTime time = now.toLocalTime();

			// Determinar el turno actual
			char turnoActual = time.isBefore(LocalTime.of(8, 0)) ? 'N' : time.isBefore(LocalTime.of(20, 0)) ? 'M' : 'N';

			// Ajustar la fecha para turnos de noche
			if (turnoActual == 'N' && time.isAfter(LocalTime.of(20, 0))) {
				today = now.toLocalDate();
			} else if (turnoActual == 'N' && time.isBefore(LocalTime.of(8, 0))) {
				today = today.minusDays(1);
			}

			// Verifica si ya se ha generado el turno
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
					empleado = new Empleado(nombre, puedeIrAQuirofano, null);
					empleadoService.guardarEmpleado(empleado);
				} else {
					empleado = encontrados.get(0);
					empleado.setPuedeIrAQuirofano(puedeIrAQuirofano);
					empleadoService.guardarEmpleado(empleado);
				}

				empleadosTurno.add(empleado);
			}

			// Orden aleatorio si se seleccionó la opción
			if (aleatorio) {
				Collections.shuffle(empleadosTurno);
			}

			// Seleccionar un empleado para quirófano
			Empleado empleadoQuirofano = seleccionarEmpleadoQuirofano(empleadosTurno);

			// Generar asignaciones de turno
// Generar asignaciones de turno
			List<Turno> turnos = generarAsignacionesDeTurno(empleadosTurno, today, turnoActual, subdividir, empleadoQuirofano);

			// Guardar turnos en la base de datos
			turnoService.guardarTurnos(turnos);

			// Para evitar puntos duplicados, utilizamos un Set para los IDs de los
			// empleados ya puntuados
			Set<Long> empleadosPuntuados = new HashSet<>();

			// Generar y guardar las puntuaciones
			for (Turno turno : turnos) {
				Long empleadoId = turno.getEmpleado().getId();

				// Verificamos si ya se ha puntuado al empleado en este turno
				if (!empleadosPuntuados.contains(empleadoId)) {
					// Puntuación por preselección
					if (turno.getEmpleado().isPuedeIrAQuirofano()) {
						Puntuacion preseleccion = new Puntuacion(turno, "preseleccion");
						puntuacionService.guardarPuntuacion(preseleccion);
					}

					// Puntuación adicional si fue elegido para quirófano
					if (turno.getEmpleado().equals(empleadoQuirofano)) {
						Puntuacion elegido = new Puntuacion(turno, "elegido");
						puntuacionService.guardarPuntuacion(elegido);
					}

					// Marcar este empleado como ya puntuado para evitar duplicados
					empleadosPuntuados.add(empleadoId);
				}
			}

			// Pasar los datos al modelo para su visualización
			model.addAttribute("turnos", turnos);
			model.addAttribute("empleadoQuirofano", empleadoQuirofano);
			model.addAttribute("turno", turnoActual == 'M' ? "Mañana" : "Noche");
		} catch (RuntimeException e) {
			model.addAttribute("mensajeError",
					"ERROR: No se pudo generar el turno debido a un conflicto de concurrencia. Por favor, intente nuevamente más tarde.");
			return "error";
		}

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
			boolean subdividir, Empleado empleadoQuirofano) {

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

				boolean esQuirofano = empleado.equals(empleadoQuirofano);

				Turno turno = new Turno();
				turno.setEmpleado(empleado);
				turno.setFecha(today);
				turno.setTurno(turnoActual);
				turno.setIntervalo(intervalo);
				turno.setRueda(rep + 1); // Asignar equipo basado en el ciclo actual
				turno.setHoraInicio(slotStartTime);
				turno.setHoraFin(slotEndTime);
				turno.setQuirofano(esQuirofano);

				turnos.add(turno);
				slotStartTime = slotEndTime;
			}
		}

		return turnos;
	}

}
