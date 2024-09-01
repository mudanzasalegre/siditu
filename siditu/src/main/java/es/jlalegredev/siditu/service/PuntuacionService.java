package es.jlalegredev.siditu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.jlalegredev.siditu.model.Empleado;
import es.jlalegredev.siditu.model.Puntuacion;
import es.jlalegredev.siditu.repository.PuntuacionRepository;
import es.jlalegredev.siditu.repository.EmpleadoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PuntuacionService {

	@Autowired
	private PuntuacionRepository puntuacionRepository;

	@Autowired
	private EmpleadoRepository empleadoRepository;

	public Puntuacion guardarPuntuacion(Puntuacion puntuacion) {
		return puntuacionRepository.save(puntuacion);
	}

	public List<Puntuacion> obtenerPuntuacionesPorTurno(Long turnoId) {
		return puntuacionRepository.findAll(); // Aquí puedes filtrar por turno si es necesario
	}

	public List<Puntuacion> obtenerTodasLasPuntuaciones() {
		return puntuacionRepository.findAll();
	}

	public Map<Empleado, Integer> getClasificacionGeneralIndividual(int year) {
		List<Empleado> empleados = empleadoRepository.findAll();
		Map<Empleado, Integer> clasificacion = new HashMap<>();

		for (Empleado empleado : empleados) {
			int puntosTotales = puntuacionRepository.getPuntosTotalesEmpleado(empleado.getId(), year);
			clasificacion.put(empleado, puntosTotales);
		}

		// Ordenar por puntos de mayor a menor
		return clasificacion.entrySet().stream().sorted(Map.Entry.<Empleado, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public Map<Integer, Integer> getClasificacionPorEquipos(int year) {
		Map<Integer, Integer> clasificacion = new HashMap<>();

		// Incluimos 0 como clave para representar "Sin equipo"
		clasificacion.put(0, 0);

		for (int equipo = 1; equipo <= 6; equipo++) {
			int puntosTotales = puntuacionRepository.getPuntosTotalesEquipo(equipo, year);
			clasificacion.put(equipo, puntosTotales);
		}

		// Sumamos los puntos de empleados "Sin equipo"
		for (Empleado empleado : empleadoRepository.findAll()) {
			if (empleado.getEquipo() == null) {
				int puntosSinEquipo = clasificacion.get(0) + puntuacionRepository.getPuntosTotalesEmpleado(empleado.getId(), year);
				clasificacion.put(0, puntosSinEquipo);
			}
		}

		// Ordenar por puntos de mayor a menor
		return clasificacion.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
}
