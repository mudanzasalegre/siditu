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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlalegredev.siditu.model.Turno;
import es.jlalegredev.siditu.repository.PuntuacionRepository;
import es.jlalegredev.siditu.repository.TurnoRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;

@Service
public class TurnoService {

	@Autowired
	private TurnoRepository turnoRepository;

	@Autowired
	private PuntuacionRepository puntuacionRepository;

	private static final Logger logger = LoggerFactory.getLogger(TurnoService.class);

	public Turno obtenerTurnoPorId(Long id) {
		Optional<Turno> turno = turnoRepository.findById(id);
		return turno.orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + id));
	}

	@Transactional
	public List<Turno> obtenerTurnosDelDia(LocalDate fecha, char turno) {
		try {
			return turnoRepository.findByFechaAndTurno(fecha, turno);
		} catch (PessimisticLockException | LockTimeoutException e) {
			// Manejar el timeout del lock
			throw new RuntimeException("No se pudo adquirir el bloqueo para generar los turnos. Intente nuevamente más tarde.",
					e);
		}
	}

	public List<Turno> obtenerTurnosPorEmpleado(Long empleadoId) {
		return turnoRepository.findByEmpleadoId(empleadoId);
	}

	@Transactional
	public void reiniciarTurno(LocalDate fecha, char turno) {
		try {
			// Obtener todos los turnos del día y turno actual
			List<Turno> turnos = turnoRepository.findByFechaAndTurno(fecha, turno);
			logger.debug("Encontrados {} turnos para la fecha {} y turno {}", turnos.size(), fecha, turno);

			for (Turno turnoActual : turnos) {
				// Eliminar todas las puntuaciones asociadas a cada turno
				logger.debug("Eliminando puntuaciones para el turno con ID {}", turnoActual.getId());
				puntuacionRepository.deleteByTurnoId(turnoActual.getId());

				// Ahora eliminar el turno
				logger.debug("Eliminando turno con ID {}", turnoActual.getId());
				turnoRepository.delete(turnoActual);
			}

			logger.debug("Eliminación de turnos y puntuaciones completada para la fecha {} y turno {}", fecha, turno);
		} catch (Exception e) {
			logger.error("Error al intentar reiniciar el turno", e);
			throw new RuntimeException("Error al intentar reiniciar el turno.", e);
		}
	}

	@Transactional
	public Turno guardarTurno(Turno turno) {
		return turnoRepository.save(turno);
	}

	@Transactional
	public void guardarTurnos(List<Turno> turnos) {
		turnoRepository.saveAll(turnos);
	}

	public List<Turno> obtenerTurnosPorIntervalo(int intervalo) {
		return turnoRepository.findByIntervalo(intervalo);
	}

}
