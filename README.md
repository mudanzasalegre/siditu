# SIDITU - Sistema de Distribución de Turnos

**SIDITU** (Sistema de Distribución de Turnos) es una herramienta diseñada para facilitar la asignación de turnos de manera eficiente y equitativa en entornos hospitalarios. A través de una interfaz intuitiva, los usuarios pueden asignar y visualizar turnos, asegurando que se cumplan todas las necesidades operativas mientras se mantiene la motivación del personal.

## Introducción

**SIDITU** es una aplicación web que permite distribuir los turnos de manera eficiente, equitativa y con un toque de emoción. La aplicación está diseñada para usarse en entornos hospitalarios, donde es crucial que se mantenga un equilibrio en la distribución de turnos y que todos los puestos críticos, como el quirófano, estén cubiertos en todo momento.

## Características Principales

### 1. Vista Inicial y Selección del Turno
- La interfaz inicial muestra de inmediato el turno en el que se encuentran los usuarios (mañana o noche).
- Un reloj indicativo del turno actual ayuda a evitar confusiones y asegura que el usuario esté siempre al tanto del período de trabajo.

### 2. Selección del Número de Personas
- Los usuarios deben indicar el número de personas que participarán en la distribución del turno, con un mínimo de 5 personas.
- Es obligatorio que al menos una persona esté disponible para quirófano; de lo contrario, la aplicación no permitirá avanzar.

### 3. Selección del Personal para Quirófano
- Cada miembro del personal puede ser marcado como disponible para quirófano a través de un checkbox asociado a su nombre.
- La aplicación asegura que al menos una persona esté asignada al quirófano antes de proceder con la generación de turnos.

### 4. Opción de Subdivisión de Turnos
- SIDITU permite subdividir los turnos en mitades, ofreciendo la posibilidad de distribuir los turnos en dos rondas de trabajo, para evitar la fatiga del personal.

### 5. Generación de Turnos Aleatorios
- La aplicación permite generar turnos de manera aleatoria, asegurando una distribución equitativa y añadiendo un elemento de emoción para el personal.

### 6. Visualización de Turnos Generados
- Una vez configurados los turnos, la aplicación genera y muestra un resumen detallado de los turnos asignados.
- La visualización incluye una lista organizada de asignaciones, que se puede subdividir en "ruedas" según la opción seleccionada.

### 7. Limitación de Generación de Turnos
- Para evitar abusos, la aplicación permite generar los turnos solo una vez por período (mañana o noche), bloqueando nuevas generaciones hasta que se inicie un nuevo turno.

### 8. Panel de Administración
- El panel de administración permite a usuarios autorizados realizar ajustes o corregir errores en la asignación de turnos, asegurando flexibilidad en situaciones excepcionales.

## Beneficios y Ventajas

- **Simplicidad y Facilidad de Uso:** Interfaz intuitiva que permite a los usuarios generar y gestionar turnos sin necesidad de un entrenamiento extenso.
- **Flexibilidad en la Distribución de Turnos:** Las opciones de subdivisión y generación aleatoria permiten a los administradores adaptarse a diferentes situaciones operativas.
- **Prevención de Abusos:** El sistema de bloqueo de generación de turnos asegura una distribución justa y equitativa.
- **Herramientas de Administración:** El panel de administración ofrece control adicional para ajustar y corregir la asignación de turnos cuando sea necesario.

## Conclusión

**SIDITU** es una herramienta esencial para la gestión eficiente de turnos en entornos hospitalarios. Su combinación de simplicidad, flexibilidad y seguridad la convierte en una solución ideal para cualquier institución que busque mejorar la organización y motivación de su personal, asegurando que cada turno se asigne de manera justa y equitativa.

## Instalación

1. Clona este repositorio:
   ´´´bash
   git clone https://github.com/tu_usuario/siditu.git
   

2. Navega al directorio del proyecto:
   cd siditu

3. Configura tu entorno de desarrollo y base de datos (si es necesario).

4. Ejecuta la aplicación:
   ./mvnw spring-boot:run

## Contribuciones

Las contribuciones son bienvenidas. Por favor, sigue los lineamientos de contribución y asegúrate de que los cambios propuestos se alineen con la filosofía y los objetivos del proyecto.

## Licencia

Este proyecto está licenciado bajo la [MIT License](LICENSE).

## Contacto

Para más información o asistencia, puedes ponerte en contacto a través de [mudanzasalegre@hotmail.com](mailto:mudanzasalegre@hotmail.com).
