package org.xjons.generadorTurnosRandom.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
	public String handleNotFoundError(NoHandlerFoundException ex, Model model) {
		// Aquí podrías agregar atributos al modelo si necesitas mostrar información
		// adicional en la página de error
		return "error"; // Retorna la página de error personalizada
	}
}
