package puppy.code;

/**
 * Nivel de dificultad MEDIO
 * 
 * Características:
 * - Velocidad moderada
 * - Balance entre gotas buenas y malas
 * - Vidas estándar
 * - PowerUps con frecuencia normal
 * - Ideal para jugadores intermedios
 */
public class DificultadMedio implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Medio";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 200f; // Velocidad normal
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 180f; // Velocidad normal
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 1_000_000_000L; // 1 segundo - intervalo estándar
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.7f; // 70% gotas buenas, 30% malas
    }
    
    @Override
    public int getVidasIniciales() {
        return 3; // Vidas estándar
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 6_000_000_000L; // 6 segundos - frecuencia normal
    }
    
    @Override
    public String getDescripcion() {
        return "Velocidad moderada, balanceado, ideal para intermedios";
    }
}

