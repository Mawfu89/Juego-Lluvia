package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa al tarro (jugador principal) del juego.
 * Controla movimiento, vidas, puntos, estado de invulnerabilidad
 * y reacciones a PowerUps.
 */
public class Tarro {

    // --- Atributos ---
    private Texture textura;
    private Sound sonidoDano;
    private Rectangle rectangulo;

    private int puntos;
    private int vidas;

    private boolean herido;
    private float tiempoHerido;

    // --- Constructor ---
    public Tarro(Texture textura, Sound sonidoDano) {
        this.textura = textura;
        this.sonidoDano = sonidoDano;
        this.rectangulo = new Rectangle();
        this.puntos = 0;
        this.vidas = 3;
        this.herido = false;
        this.tiempoHerido = 0;
    }

    // --- Inicialización ---
    public void crear() {
        rectangulo.x = 800 / 2f - 64 / 2f;
        rectangulo.y = 20;
        rectangulo.width = 64;
        rectangulo.height = 64;
    }
    
    /**
     * Establece el número de vidas iniciales
     * @param vidasIniciales Número de vidas a establecer
     */
    public void setVidasIniciales(int vidasIniciales) {
        this.vidas = vidasIniciales;
    }

    public void actualizarMovimiento() {
        float velocidad = 400 * Gdx.graphics.getDeltaTime();

        // Movimiento lateral (A/D o flechas)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            rectangulo.x -= velocidad;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            rectangulo.x += velocidad;

        // Limitar movimiento dentro de la pantalla
        if (rectangulo.x < 0) rectangulo.x = 0;
        if (rectangulo.x > 800 - rectangulo.width) rectangulo.x = 800 - rectangulo.width;

        // Actualiza invulnerabilidad si está herido
        if (herido) actualizarInvulnerabilidad();
    }

    // --- Control de invulnerabilidad ---
    private void actualizarInvulnerabilidad() {
        tiempoHerido += Gdx.graphics.getDeltaTime();
        if (tiempoHerido > 1.5f) { // 1.5 segundos de invulnerabilidad
            herido = false;
            tiempoHerido = 0;
        }
    }

    // --- Dibujo ---
    public void dibujar(SpriteBatch batch) {
        // Efecto visual: parpadea si está herido
        // Dibujar con tamaño fijo 64x64 para que coincida con el rectángulo de colisión
        if (herido) {
            int frame = (int) (tiempoHerido * 20);
            if (frame % 4 < 2) batch.draw(textura, rectangulo.x, rectangulo.y, rectangulo.width, rectangulo.height);
        } else {
            batch.draw(textura, rectangulo.x, rectangulo.y, rectangulo.width, rectangulo.height);
        }
    }

    // --- Colisiones / puntuación ---
    public Rectangle getRectangulo() {
        return rectangulo;
    }

    public void sumarPunto() {
        puntos++;
    }

    public void restarVida() {
        if (!herido) { // Solo recibe daño si no está invulnerable
            vidas--;
            herido = true;
            reproducirDano();
        }
    }

 // --- Power-Up: sumar vida ---
    public void sumarVida() {
        if (vidas < 5) { // límite opcional (por ejemplo, 5 vidas máximo)
            vidas++;
        }
    }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
    }

    private void reproducirDano() {
        GestorAudio.getInstance().reproducirSonido(sonidoDano);
    }

    // --- Getters ---
    public boolean estaHerido() {
        return herido;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getVidas() {
        return vidas;
    }

    // --- Control de volumen global (usando GestorAudio Singleton) ---
    public void setVolumen(float nuevoVolumen) {
        GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
    }

    // --- Liberar recursos ---
    public void destruir() {
        if (textura != null) textura.dispose();
        if (sonidoDano != null) sonidoDano.dispose();
    }
}
