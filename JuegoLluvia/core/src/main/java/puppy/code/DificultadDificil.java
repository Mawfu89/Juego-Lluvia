package puppy.code;

/**
 * Configuracion de dificultad dificil.
 * Gotas muy rapidas, menos vidas y menos PowerUps para un desafio mayor.
 */
public class DificultadDificil implements NivelDificultad {
    
    @Override
    public String getNombre() {
        return "Dificil";
    }
    
    @Override
    public float getVelocidadGotasBuenas() {
        return 280f;
    }
    
    @Override
    public float getVelocidadGotasMalas() {
        return 260f;
    }
    
    @Override
    public long getIntervaloCreacionGotas() {
        return 600_000_000L;  // 0.6 segundos entre gotas
    }
    
    @Override
    public float getProbabilidadGotaBuena() {
        return 0.5f;  // 50% de probabilidad de gota buena (mas dificil)
    }
    
    @Override
    public int getVidasIniciales() {
        return 2;
    }
    
    @Override
    public long getIntervaloPowerUps() {
        return 8_000_000_000L;  // PowerUp cada 8 segundos
    }
    
    @Override
    public String getDescripcion() {
        return "Gotas rapidas, menos vidas, solo para expertos";
    }
}

