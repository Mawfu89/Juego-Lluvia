package puppy.code;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Gestiona el volumen del juego de forma centralizada.
 * Solo existe una instancia de esta clase (patron Singleton) para que
 * todo el juego use el mismo volumen maestro.
 */
public class GestorAudio {
    
    private static GestorAudio instancia;
    private float volumenMaestro = 0.8f;  // Volumen por defecto: 80%
    
    private GestorAudio() {
        // Constructor privado para evitar que se creen mas instancias
    }
    
    /**
     * Obtiene la unica instancia del gestor de audio.
     */
    public static synchronized GestorAudio getInstance() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }
    
    /**
     * Establece el volumen maestro del juego (entre 0.0 y 1.0).
     */
    public void setVolumenMaestro(float volumen) {
        this.volumenMaestro = Math.max(0.0f, Math.min(1.0f, volumen));
    }
    
    public float getVolumenMaestro() {
        return volumenMaestro;
    }
    
    /**
     * Aplica el volumen maestro a una musica de fondo.
     */
    public void aplicarVolumen(Music musica) {
        if (musica != null) {
            musica.setVolume(volumenMaestro);
        }
    }
    
    /**
     * Reproduce un sonido con el volumen maestro aplicado.
     */
    public void reproducirSonido(Sound sonido) {
        if (sonido != null) {
            sonido.play(volumenMaestro);
        }
    }
}

