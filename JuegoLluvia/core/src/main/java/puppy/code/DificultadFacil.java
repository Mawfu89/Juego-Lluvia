package puppy.code;

/**
 * Configuracion de dificultad facil.
 * Gotas mas lentas, mas vidas y mas PowerUps para facilitar el juego.
 */
public class DificultadFacil implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Facil";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 150f;
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 130f;
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 1_500_000_000L;  // 1.5 segundos entre gotas
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.8f;  // 80% de probabilidad de gota buena
    }
    
    @Override
    public int getVidasIniciales() {
        return 5;
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 4_000_000_000L;  // PowerUp cada 4 segundos
    }
    
    @Override
    public String getDescripcion() {
        return "Gotas lentas, mas vidas, ideal para principiantes";
    }
}

