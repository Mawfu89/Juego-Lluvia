package puppy.code;

/**
 * Configuracion de dificultad media.
 * Balance entre velocidad y frecuencia, ideal para jugadores intermedios.
 */
public class DificultadMedio implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Medio";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 200f;
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 180f;
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 1_000_000_000L;  // 1 segundo entre gotas
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.7f;  // 70% de probabilidad de gota buena
    }
    
    @Override
    public int getVidasIniciales() {
        return 3;
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 6_000_000_000L;  // PowerUp cada 6 segundos
    }
    
    @Override
    public String getDescripcion() {
        return "Velocidad moderada, balanceado, ideal para intermedios";
    }
}

