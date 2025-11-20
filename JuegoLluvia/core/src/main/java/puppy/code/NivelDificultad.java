package puppy.code;

/**
 * Define los parametros de configuracion para cada nivel de dificultad.
 * Cada dificultad (Facil, Medio, Dificil) implementa esta interfaz con valores diferentes.
 */
public interface NivelDificultad {
    
    String getNombre();
    
    /**
     * Velocidad a la que caen las gotas buenas (en pixeles por segundo).
     */
    float getVelocidadGotasBuenas();
    
    /**
     * Velocidad a la que caen las gotas malas (en pixeles por segundo).
     */
    float getVelocidadGotasMalas();
    
    /**
     * Tiempo que debe pasar antes de crear una nueva gota (en nanosegundos).
     */
    long getIntervaloCreacionGotas();
    
    /**
     * Probabilidad de que aparezca una gota buena (0.0 = siempre mala, 1.0 = siempre buena).
     */
    float getProbabilidadGotaBuena();
    
    /**
     * Numero de vidas con las que empieza el jugador.
     */
    int getVidasIniciales();
    
    /**
     * Tiempo que debe pasar antes de crear un nuevo PowerUp (en nanosegundos).
     */
    long getIntervaloPowerUps();
    
    /**
     * Descripcion breve del nivel de dificultad para mostrar en el menu.
     */
    String getDescripcion();
}

