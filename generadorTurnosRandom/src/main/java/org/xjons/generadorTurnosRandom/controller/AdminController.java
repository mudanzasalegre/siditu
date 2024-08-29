package org.xjons.generadorTurnosRandom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

	// Aquí definimos una contraseña sencilla. En un entorno real, se recomienda
	// almacenar contraseñas de manera segura.
	private static final String ADMIN_PASSWORD = "admin123";

	private final TurnoController turnoController;

	public AdminController(TurnoController turnoController) {
		this.turnoController = turnoController;
	}

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
		turnoController.reiniciarUltimoTurno();
		model.addAttribute("mensaje", "El último turno ha sido reiniciado. Ahora puedes generar un nuevo turno.");
		return "admin-dashboard";
	}

}
