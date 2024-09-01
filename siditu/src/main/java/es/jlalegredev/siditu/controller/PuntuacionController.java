package es.jlalegredev.siditu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jlalegredev.siditu.model.Empleado;
import es.jlalegredev.siditu.model.Puntuacion;
import es.jlalegredev.siditu.model.Turno;
import es.jlalegredev.siditu.service.PuntuacionService;
import es.jlalegredev.siditu.service.TurnoService;

@Controller
@RequestMapping("/puntuaciones")
public class PuntuacionController {

	@Autowired
	private PuntuacionService puntuacionService;

	@Autowired
	private TurnoService turnoService;

	@PostMapping("/guardar")
	public ResponseEntity<String> guardarPuntuacion(@RequestParam Long turnoId, @RequestParam String motivo) {
		try {
			Turno turno = turnoService.obtenerTurnoPorId(turnoId);
			Puntuacion puntuacion = new Puntuacion(turno, motivo);
			puntuacionService.guardarPuntuacion(puntuacion);
			return ResponseEntity.ok("Puntuación guardada exitosamente");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error al guardar la puntuación");
		}
	}

	@GetMapping("/puntuaciones")
	public String mostrarPuntuaciones(Model model) {
		// Obtener las clasificaciones
		int currentYear = java.time.Year.now().getValue(); // Obtener el año actual
		Map<Empleado, Integer> clasificacionIndividual = puntuacionService.getClasificacionGeneralIndividual(currentYear);
		Map<Integer, Integer> clasificacionPorEquipos = puntuacionService.getClasificacionPorEquipos(currentYear);

		// Pasar las clasificaciones al modelo
		model.addAttribute("clasificacionIndividual", clasificacionIndividual);
		model.addAttribute("clasificacionPorEquipos", clasificacionPorEquipos);

		return "puntuaciones";
	}

	// Método adicional para redirigir de /puntuaciones a /puntuaciones/puntuaciones
//Ensure that requests to /puntuaciones are handled correctly
	@GetMapping("")
	public String redirectToPuntuaciones() {
		return "redirect:/puntuaciones/puntuaciones";
	}

}
