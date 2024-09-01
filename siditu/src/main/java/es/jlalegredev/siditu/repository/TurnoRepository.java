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

package es.jlalegredev.siditu.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.jlalegredev.siditu.model.Turno;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

	Optional<Turno> findById(Long id);

	// Obtener todos los turnos de un día específico y turno (Mañana o Noche)
	// List<Turno> findByFechaAndTurno(LocalDate fecha, char turno);

	// Obtener todos los turnos de un empleado específico
	List<Turno> findByEmpleadoId(Long empleadoId);

	// Obtener todos los turnos en un intervalo específico
	List<Turno> findByIntervalo(int intervalo);

	@Modifying
	@Transactional
	@Query("DELETE FROM Turno t WHERE t.fecha = :fecha AND t.turno = :turno")
	void deleteTurnos(@Param("fecha") LocalDate fecha, @Param("turno") char turno);

	@Modifying
	@Transactional
	@Query("DELETE FROM Turno t WHERE t.fecha = :fecha AND t.turno = :turno")
	void deleteByFechaAndTurno(@Param("fecha") LocalDate fecha, @Param("turno") char turno);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT t FROM Turno t WHERE t.fecha = :fecha AND t.turno = :turno")
	@QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "10000") })
	List<Turno> findByFechaAndTurno(@Param("fecha") LocalDate fecha, @Param("turno") char turno);
}
