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

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.jlalegredev.siditu.service.TurnoService;

@Controller
public class HomeController {

	@Autowired
	private TurnoService turnoService;

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/")
	public String index(Model model) {

		ZoneId zonaMadrid = ZoneId.of("Europe/Madrid");
		ZonedDateTime now = ZonedDateTime.now(zonaMadrid);

		// Ajuste de fecha para turnos de noche (entre medianoche y las 8 AM)
		if (now.toLocalTime().isBefore(LocalTime.of(8, 0))) {
			now = now.minusDays(1);
		}

		char turnoActual = now.getHour() < 8 || now.getHour() >= 20 ? 'N' : 'M';

		// Log para verificar el valor de turnoActual
		logger.info("Turno actual: " + turnoActual + " para el día " + now.toLocalDate());

		boolean turnoGenerado = !turnoService.obtenerTurnosDelDia(now.toLocalDate(), turnoActual).isEmpty();

		// Formatear la fecha y la hora en español
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
		String formattedDateTime = now.format(formatter);

		model.addAttribute("turnoActual", turnoActual);
		model.addAttribute("formattedDate", formattedDateTime);
		model.addAttribute("turnoGenerado", turnoGenerado);

		return "index";
	}

}
