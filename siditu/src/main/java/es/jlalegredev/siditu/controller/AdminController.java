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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jlalegredev.siditu.service.TurnoService;

@Controller
public class AdminController {

	// Aquí definimos una contraseña sencilla. En un entorno real, se recomienda
	// almacenar contraseñas de manera segura.
	private static final String ADMIN_PASSWORD = "admin123";

	@Autowired
	private TurnoService turnoService;

	@GetMapping("/admin")
	public String adminLogin() {
		return "admin-login"; // Esta es la página donde se introduce la contraseña
	}

	@PostMapping("/admin")
	public String adminAuthenticate(@RequestParam String password, Model model) {
		if (ADMIN_PASSWORD.equals(password)) {
			return "admin-dashboard"; // Redirige al panel de administración si la contraseña es correcta
		} else {
			model.addAttribute("error", "Contraseña incorrecta");
			return "admin-login";
		}
	}

	@PostMapping("/admin/reiniciarTurno")
	public String reiniciarTurno(Model model) {
		// Obtener el turno actual y la fecha
		LocalDate today = LocalDate.now();
		LocalTime time = LocalTime.now();
		char turnoActual = time.isBefore(LocalTime.of(8, 0)) ? 'N' : time.isBefore(LocalTime.of(20, 0)) ? 'M' : 'N';

		// Llamar al servicio para eliminar los turnos del día actual y turno actual
		turnoService.reiniciarTurno(today, turnoActual);

		model.addAttribute("mensaje", "El turno ha sido reiniciado. Ahora puedes generar un nuevo turno.");
		return "admin-dashboard";
	}

}
