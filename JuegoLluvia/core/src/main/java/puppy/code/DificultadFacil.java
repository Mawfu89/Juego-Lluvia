package puppy.code;

/**
 * Nivel de dificultad FÁCIL
 * 
 * Características:
 * - Gotas más lentas
 * - Más gotas buenas que malas
 * - Más vidas iniciales
 * - PowerUps más frecuentes
 * - Ideal para principiantes
 */
public class DificultadFacil implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Fácil";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 150f; // Más lento
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 130f; // Más lento
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 1_500_000_000L; // 1.5 segundos - más tiempo entre gotas
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.8f; // 80% gotas buenas, 20% malas
    }
    
    @Override
    public int getVidasIniciales() {
        return 5; // Más vidas
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 4_000_000_000L; // 4 segundos - PowerUps más frecuentes
    }
    
    @Override
    public String getDescripcion() {
        return "Gotas lentas, más vidas, ideal para principiantes";
    }
}

