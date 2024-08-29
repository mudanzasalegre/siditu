package org.xjons.generadorTurnosRandom.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.xjons.generadorTurnosRandom.model.UltimoTurno;

@Controller
public class TurnoController {

	private UltimoTurno ultimoTurnoGenerado;

	@GetMapping("/")
	public String index(Model model) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		LocalTime time = now.toLocalTime();

		String turnoActual;
		LocalDate fechaTurno;

		if (time.isBefore(LocalTime.of(8, 0))) {
			turnoActual = "N";
			fechaTurno = today.minusDays(1);
		} else if (time.isBefore(LocalTime.of(20, 0))) {
			turnoActual = "M";
			fechaTurno = today;
		} else {
			turnoActual = "N";
			fechaTurno = today;
		}

		String formattedDate = fechaTurno.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		model.addAttribute("turnoActual", turnoActual);
		model.addAttribute("formattedDate", formattedDate);

		return "index";
	}

	@PostMapping("/generarTurnos")
	public String generarTurnos(@RequestParam int numeroDePersonas, @RequestParam String turno,
			@RequestParam List<String> nombres, @RequestParam(required = false) List<String> puedeIrAQuirofano,
			@RequestParam(required = false) boolean subdividir, @RequestParam(required = false) boolean aleatorio, Model model) {

		List<Boolean> puedeIrAQuirofanoList = new ArrayList<>(Collections.nCopies(numeroDePersonas, Boolean.FALSE));
		if (puedeIrAQuirofano != null) {
			for (String index : puedeIrAQuirofano) {
				int i = Integer.parseInt(index);
				puedeIrAQuirofanoList.set(i, Boolean.TRUE);
			}
		}

//Verificar si ya se ha generado un turno en este intervalo
		if (ultimoTurnoGenerado != null && ultimoTurnoGenerado.getTurno().equals(turno)) {
			model.addAttribute("mensaje",
					"ERROR: Ya se ha generado un turno para este intervalo. Aquí están los datos del turno generado anteriormente.");
			model.addAttribute("turno", turno.equals("M") ? "Mañana" : "Noche");
			model.addAttribute("horarioInicio", ultimoTurnoGenerado.getHorarioInicio());
			model.addAttribute("horarioFin", ultimoTurnoGenerado.getHorarioFin());
			model.addAttribute("empleadoQuirofano", ultimoTurnoGenerado.getEmpleadoQuirofano());
			model.addAttribute("asignaciones", ultimoTurnoGenerado.getAsignaciones());
			return "turnos";
		}

		// Validación de que al menos haya 5 empleados y uno para quirófano
		if (numeroDePersonas < 5 || puedeIrAQuirofanoList.stream().noneMatch(Boolean::booleanValue)) {
			model.addAttribute("turnoActual", turno);
			model.addAttribute("formattedDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			model.addAttribute("nombres", nombres);
			model.addAttribute("puedeIrAQuirofanoList", puedeIrAQuirofanoList);
			model.addAttribute("subdividir", subdividir);
			model.addAttribute("aleatorio", aleatorio);

			// Mostrar una alerta personalizada desde el servidor
			model.addAttribute("validationError", true);

			return "index"; // Regresar al formulario
		}

		// Resto de la lógica si no hay errores de validación
		if (aleatorio) {
			return generarTurnosAleatorios(numeroDePersonas, turno, nombres, puedeIrAQuirofanoList, subdividir, model);
		} else {
			return generarTurnosNormales(numeroDePersonas, turno, nombres, puedeIrAQuirofanoList, subdividir, model);
		}
	}

	public String generarTurnosAleatorios(int numeroDePersonas, String turno, List<String> nombres,
			List<Boolean> puedeIrAQuirofano, boolean subdividir, Model model) {
		LocalTime startTime = turno.equals("M") ? LocalTime.of(8, 0) : LocalTime.of(20, 0);
		int totalMinutos = 12 * 60;
		List<String> asignaciones = new ArrayList<>();
		List<Integer> empleados = new ArrayList<>();

// Agregar todos los empleados a la lista de rotación
		for (int i = 0; i < numeroDePersonas; i++) {
			empleados.add(i);
		}

// Aleatorizar la lista de empleados
		Collections.shuffle(empleados, new Random());

// Seleccionar el empleado para quirófano de manera aleatoria entre los disponibles
		String empleadoQuirofano = "No hay empleados disponibles para quirófano";
		List<Integer> empleadosDisponibles = new ArrayList<>();
		for (int i = 0; i < numeroDePersonas; i++) {
			if (puedeIrAQuirofano.get(i)) {
				empleadosDisponibles.add(i);
			}
		}

		if (!empleadosDisponibles.isEmpty()) {
			int indiceQuirofano = new Random().nextInt(empleadosDisponibles.size());
			empleadoQuirofano = "Empleado " + nombres.get(empleadosDisponibles.get(indiceQuirofano)) + " asignado al quirófano";
		}
		model.addAttribute("empleadoQuirofano", empleadoQuirofano);

// Calcular el tiempo por persona
		int minutosPorPersona = totalMinutos / numeroDePersonas;
		if (subdividir) {
			minutosPorPersona /= 2;
		}
		int repeticiones = subdividir ? 2 : 1;

// Generar las asignaciones en base a la lista aleatorizada
		LocalTime slotStartTime = startTime;
		for (int rep = 0; rep < repeticiones; rep++) {
			for (int empleado : empleados) { // Usar la lista aleatorizada de empleados
				LocalTime slotEndTime = slotStartTime.plusMinutes(minutosPorPersona);

				String asignacion = String.format("Rueda %d - Empleado %s - de %s a %s en CAR", rep + 1, nombres.get(empleado),
						slotStartTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
						slotEndTime.format(DateTimeFormatter.ofPattern("hh:mm a")));

				asignaciones.add(asignacion);
				slotStartTime = slotEndTime;
			}
			slotStartTime = startTime.plusMinutes((rep + 1) * minutosPorPersona * numeroDePersonas);
		}

		model.addAttribute("asignaciones", asignaciones);
		model.addAttribute("turno", turno.equals("M") ? "Mañana" : "Noche");
		model.addAttribute("horarioInicio", startTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
		model.addAttribute("horarioFin", startTime.plusHours(12).format(DateTimeFormatter.ofPattern("hh:mm a")));

		ultimoTurnoGenerado = new UltimoTurno();
		ultimoTurnoGenerado.setTurno(turno);
		ultimoTurnoGenerado.setAsignaciones(asignaciones);
		ultimoTurnoGenerado.setEmpleadoQuirofano(empleadoQuirofano);
		ultimoTurnoGenerado.setHorarioInicio(startTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
		ultimoTurnoGenerado.setHorarioFin(startTime.plusHours(12).format(DateTimeFormatter.ofPattern("hh:mm a")));

		return "turnos";
	}

	public void reiniciarUltimoTurno() {
		ultimoTurnoGenerado = null; // Reiniciar el último turno
	}

	public String generarTurnosNormales(int numeroDePersonas, String turno, List<String> nombres,
			List<Boolean> puedeIrAQuirofano, boolean subdividir, Model model) {
		LocalTime startTime = turno.equals("M") ? LocalTime.of(8, 0) : LocalTime.of(20, 0);
		int totalMinutos = 12 * 60;
		List<String> asignaciones = new ArrayList<>();
		List<Integer> empleados = new ArrayList<>();

		for (int i = 0; i < numeroDePersonas; i++) {
			empleados.add(i);
		}

		Collections.shuffle(empleados, new Random());

		String empleadoQuirofano = "No hay empleados disponibles para quirófano";
		List<Integer> empleadosDisponibles = new ArrayList<>();

		for (int i = 0; i < numeroDePersonas; i++) {
			if (puedeIrAQuirofano.get(i)) {
				empleadosDisponibles.add(i);
			}
		}

		if (!empleadosDisponibles.isEmpty()) {
			int indiceQuirofano = new Random().nextInt(empleadosDisponibles.size());
			empleadoQuirofano = "Empleado " + nombres.get(empleadosDisponibles.get(indiceQuirofano)) + " asignado al quirófano";
		}

		model.addAttribute("empleadoQuirofano", empleadoQuirofano);

		int minutosPorPersona = totalMinutos / numeroDePersonas;
		if (subdividir) {
			minutosPorPersona /= 2;
		}
		int repeticiones = subdividir ? 2 : 1;

		LocalTime slotStartTime = startTime;
		for (int rep = 0; rep < repeticiones; rep++) {
			for (int i = 0; i < numeroDePersonas; i++) {
				LocalTime slotEndTime = slotStartTime.plusMinutes(minutosPorPersona);

				String asignacion = String.format("Rueda %d - Empleado %s - de %s a %s en CAR", rep + 1, nombres.get(i),
						slotStartTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
						slotEndTime.format(DateTimeFormatter.ofPattern("hh:mm a")));

				asignaciones.add(asignacion);
				slotStartTime = slotEndTime;
			}
			slotStartTime = startTime.plusMinutes((rep + 1) * minutosPorPersona * numeroDePersonas);
		}

		model.addAttribute("asignaciones", asignaciones);
		model.addAttribute("turno", turno.equals("M") ? "Mañana" : "Noche");
		model.addAttribute("horarioInicio", startTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
		model.addAttribute("horarioFin", startTime.plusHours(12).format(DateTimeFormatter.ofPattern("hh:mm a")));

		ultimoTurnoGenerado = new UltimoTurno();
		ultimoTurnoGenerado.setTurno(turno);
		ultimoTurnoGenerado.setAsignaciones(asignaciones);
		ultimoTurnoGenerado.setEmpleadoQuirofano(empleadoQuirofano);
		ultimoTurnoGenerado.setHorarioInicio(startTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
		ultimoTurnoGenerado.setHorarioFin(startTime.plusHours(12).format(DateTimeFormatter.ofPattern("hh:mm a")));

		return "turnos";
	}
}
