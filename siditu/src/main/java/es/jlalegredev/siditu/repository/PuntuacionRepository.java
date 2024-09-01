package es.jlalegredev.siditu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.jlalegredev.siditu.model.Puntuacion;

public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {

	@Query("SELECT p FROM Puntuacion p WHERE YEAR(p.turno.fecha) = :year")
	List<Puntuacion> findAllByYear(@Param("year") int year);

	@Query("SELECT COALESCE(SUM(1), 0) FROM Puntuacion p WHERE p.turno.empleado.id = :empleadoId AND YEAR(p.turno.fecha) = :year")
	int getPuntosTotalesEmpleado(@Param("empleadoId") Long empleadoId, @Param("year") int year);

	@Query("SELECT COALESCE(SUM(1), 0) FROM Puntuacion p WHERE p.turno.empleado.equipo = :equipo AND YEAR(p.turno.fecha) = :year")
	int getPuntosTotalesEquipo(@Param("equipo") int equipo, @Param("year") int year);

	@Modifying
 @Transactional
 @Query("DELETE FROM Puntuacion p WHERE p.turno.id = :turnoId")
 void deleteByTurnoId(@Param("turnoId") Long turnoId);

}
