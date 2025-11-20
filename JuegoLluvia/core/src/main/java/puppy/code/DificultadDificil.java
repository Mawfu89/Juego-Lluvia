package puppy.code;

/**
 * Nivel de dificultad DIFÍCIL
 * 
 * Características:
 * - Gotas muy rápidas
 * - Más gotas malas que buenas
 * - Menos vidas iniciales
 * - PowerUps menos frecuentes
 * - Ideal para jugadores expertos
 */
public class DificultadDificil implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Difícil";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 280f; // Muy rápido
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 260f; // Muy rápido
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 600_000_000L; // 0.6 segundos - gotas muy frecuentes
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.5f; // 50% gotas buenas, 50% malas - más difícil
    }
    
    @Override
    public int getVidasIniciales() {
        return 2; // Menos vidas
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 8_000_000_000L; // 8 segundos - PowerUps menos frecuentes
    }
    
    @Override
    public String getDescripcion() {
        return "Gotas rápidas, menos vidas, solo para expertos";
    }
}

