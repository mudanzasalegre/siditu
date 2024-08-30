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

package es.jlalegredev.siditu.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlalegredev.siditu.model.Turno;
import es.jlalegredev.siditu.repository.TurnoRepository;

@Service
public class TurnoService {

	@Autowired
	private TurnoRepository turnoRepository;

	public List<Turno> obtenerTurnosDelDia(LocalDate fecha, char turno) {
		return turnoRepository.findByFechaAndTurno(fecha, turno);
	}

	public List<Turno> obtenerTurnosPorEmpleado(Long empleadoId) {
		return turnoRepository.findByEmpleadoId(empleadoId);
	}

	@Transactional
	public void reiniciarTurno(LocalDate fecha, char turno) {
		turnoRepository.deleteByFechaAndTurno(fecha, turno);
	}

	public Turno guardarTurno(Turno turno) {
		return turnoRepository.save(turno);
	}

	public void guardarTurnos(List<Turno> turnos) {
		turnoRepository.saveAll(turnos);
	}

	public List<Turno> obtenerTurnosPorIntervalo(int intervalo) {
		return turnoRepository.findByIntervalo(intervalo);
	}

}
