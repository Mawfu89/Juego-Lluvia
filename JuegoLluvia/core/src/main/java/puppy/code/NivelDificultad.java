package puppy.code;

/**
 * GM2.3 - PATRÓN STRATEGY (ESTRATEGIA) - Niveles de Dificultad
 * 
 * Esta interfaz define el contrato para diferentes niveles de dificultad del juego.
 * Cada nivel implementa una estrategia diferente que modifica el comportamiento
 * del juego (velocidad de gotas, frecuencia de aparición, etc.).
 * 
 * PROBLEMA:
 * El juego tenía una dificultad fija, sin opciones para jugadores de diferentes
 * niveles de habilidad. No había forma de ajustar la dificultad dinámicamente.
 * 
 * CONTEXTO:
 * - Diferentes jugadores tienen diferentes niveles de habilidad
 * - Se necesita flexibilidad para ajustar la dificultad
 * - La dificultad debe afectar múltiples aspectos del juego
 * - Debe ser fácil agregar nuevos niveles de dificultad
 * 
 * SOLUCIÓN:
 * Implementación del patrón Strategy para encapsular diferentes configuraciones
 * de dificultad. Cada nivel de dificultad es una estrategia que define parámetros
 * específicos del juego.
 */
public interface NivelDificultad {
    
    /**
     * Obtiene el nombre del nivel de dificultad
     * @return Nombre descriptivo del nivel
     */
    String getNombre();
    
    /**
     * Obtiene la velocidad base de las gotas buenas
     * @return Velocidad en píxeles por segundo
     */
    float getVelocidadGotasBuenas();
    
    /**
     * Obtiene la velocidad base de las gotas malas
     * @return Velocidad en píxeles por segundo
     */
    float getVelocidadGotasMalas();
    
    /**
     * Obtiene el intervalo de tiempo entre creación de gotas (en nanosegundos)
     * @return Intervalo en nanosegundos
     */
    long getIntervaloCreacionGotas();
    
    /**
     * Obtiene la probabilidad de que aparezca una gota buena (0.0 a 1.0)
     * @return Probabilidad entre 0.0 y 1.0
     */
    float getProbabilidadGotaBuena();
    
    /**
     * Obtiene el número inicial de vidas del jugador
     * @return Número de vidas
     */
    int getVidasIniciales();
    
    /**
     * Obtiene el intervalo de tiempo entre creación de PowerUps (en nanosegundos)
     * @return Intervalo en nanosegundos
     */
    long getIntervaloPowerUps();
    
    /**
     * Obtiene la descripción del nivel de dificultad
     * @return Descripción del nivel
     */
    String getDescripcion();
}

