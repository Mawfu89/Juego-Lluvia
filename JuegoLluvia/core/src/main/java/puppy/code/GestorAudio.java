package puppy.code;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * GM2.1 - PATRÓN SINGLETON
 * 
 * PROBLEMA:
 * El volumen maestro del juego se gestionaba de forma dispersa en múltiples clases
 * (CangriMain, Lluvia, Tarro), lo que generaba duplicación de código y dificultaba
 * mantener un estado consistente del volumen en toda la aplicación.
 * 
 * CONTEXTO:
 * - El juego necesita un volumen maestro único y centralizado
 * - Múltiples componentes (música, sonidos) deben responder al mismo volumen
 * - El volumen debe poder modificarse desde cualquier parte del juego
 * - Solo debe existir una instancia del gestor de audio en toda la aplicación
 * 
 * SOLUCIÓN:
 * Implementación del patrón Singleton para crear un único punto de acceso
 * al gestor de audio. Garantiza que solo exista una instancia y proporciona
 * un acceso global controlado al volumen maestro.
 * 
 * PARTICIPANTES:
 * - GestorAudio (Singleton): Clase única que gestiona el volumen maestro
 *   - getInstance(): Método estático que retorna la única instancia
 *   - setVolumenMaestro(float): Establece el volumen (0.0 a 1.0)
 *   - getVolumenMaestro(): Obtiene el volumen actual
 *   - aplicarVolumen(Music): Aplica el volumen a música
 *   - reproducirSonido(Sound): Reproduce sonido con volumen maestro
 */
public class GestorAudio {
    
    // Instancia única del Singleton
    private static GestorAudio instancia;
    
    // Volumen maestro (0.0 a 1.0)
    private float volumenMaestro = 0.8f;
    
    /**
     * Constructor privado para prevenir instanciación externa
     */
    private GestorAudio() {
        // Constructor privado para garantizar Singleton
    }
    
    /**
     * Obtiene la única instancia del GestorAudio (Singleton)
     * @return La instancia única del gestor de audio
     */
    public static synchronized GestorAudio getInstance() {
        if (instancia == null) {
            instancia = new GestorAudio();
        }
        return instancia;
    }
    
    /**
     * Establece el volumen maestro del juego
     * @param volumen Valor entre 0.0 (silencio) y 1.0 (máximo)
     */
    public void setVolumenMaestro(float volumen) {
        this.volumenMaestro = Math.max(0.0f, Math.min(1.0f, volumen));
    }
    
    /**
     * Obtiene el volumen maestro actual
     * @return Volumen entre 0.0 y 1.0
     */
    public float getVolumenMaestro() {
        return volumenMaestro;
    }
    
    /**
     * Aplica el volumen maestro a una música
     * @param musica Música a la que aplicar el volumen
     */
    public void aplicarVolumen(Music musica) {
        if (musica != null) {
            musica.setVolume(volumenMaestro);
        }
    }
    
    /**
     * Reproduce un sonido con el volumen maestro aplicado
     * @param sonido Sonido a reproducir
     */
    public void reproducirSonido(Sound sonido) {
        if (sonido != null) {
            sonido.play(volumenMaestro);
        }
    }
}

