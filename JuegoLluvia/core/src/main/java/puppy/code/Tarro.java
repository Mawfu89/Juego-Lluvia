package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Representa al jugador (tarro) del juego.
 * Se encarga del movimiento, las vidas, los puntos y las colisiones.
 */
public class Tarro {

    private Texture textura;
    private Sound sonidoDano;
    private Rectangle rectangulo;  // Area de colision del tarro

    private int puntos;
    private int vidas;

    // Sistema de invulnerabilidad temporal despues de recibir dano
    private boolean herido;
    private float tiempoHerido;

    public Tarro(Texture textura, Sound sonidoDano) {
        this.textura = textura;
        this.sonidoDano = sonidoDano;
        this.rectangulo = new Rectangle();
        this.puntos = 0;
        this.vidas = 3;
        this.herido = false;
        this.tiempoHerido = 0;
    }

    /**
     * Inicializa la posicion y tamano del tarro.
     * Usa las dimensiones reales de la textura para que coincidan con la imagen.
     */
    public void crear() {
        float ancho = textura.getWidth();
        float alto = textura.getHeight();
        rectangulo.x = 800 / 2f - ancho / 2f;  // Centrado horizontalmente
        rectangulo.y = 20;  // Posicion inicial en la parte inferior
        rectangulo.width = ancho;
        rectangulo.height = alto;
    }
    
    /**
     * Establece el numero de vidas iniciales segun la dificultad.
     */
    public void setVidasIniciales(int vidasIniciales) {
        this.vidas = vidasIniciales;
    }

    /**
     * Actualiza el movimiento del tarro segun las teclas presionadas.
     * Se puede mover con A/D o las flechas izquierda/derecha.
     */
    public void actualizarMovimiento() {
        float velocidad = 400 * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            rectangulo.x -= velocidad;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            rectangulo.x += velocidad;

        // Evitar que el tarro se salga de la pantalla
        if (rectangulo.x < 0) rectangulo.x = 0;
        if (rectangulo.x > 800 - rectangulo.width) rectangulo.x = 800 - rectangulo.width;

        if (herido) actualizarInvulnerabilidad();
    }

    /**
     * Controla el tiempo de invulnerabilidad despues de recibir dano.
     * Durante 1.5 segundos el tarro no puede recibir mas dano.
     */
    private void actualizarInvulnerabilidad() {
        tiempoHerido += Gdx.graphics.getDeltaTime();
        if (tiempoHerido > 1.5f) {
            herido = false;
            tiempoHerido = 0;
        }
    }

    /**
     * Dibuja el tarro en pantalla.
     * Si esta herido, parpadea para indicar invulnerabilidad.
     */
    public void dibujar(SpriteBatch batch) {
        if (herido) {
            // Efecto de parpadeo: se dibuja solo en algunos frames
            int frame = (int) (tiempoHerido * 20);
            if (frame % 4 < 2) 
                batch.draw(textura, rectangulo.x, rectangulo.y, rectangulo.width, rectangulo.height);
        } else {
            batch.draw(textura, rectangulo.x, rectangulo.y, rectangulo.width, rectangulo.height);
        }
    }

    public Rectangle getRectangulo() {
        return rectangulo;
    }

    public void sumarPunto() {
        puntos++;
    }

    /**
     * Resta una vida al tarro si no esta en periodo de invulnerabilidad.
     */
    public void restarVida() {
        if (!herido) {
            vidas--;
            herido = true;
            reproducirDano();
        }
    }

    /**
     * Suma una vida cuando se recoge un PowerUp de vida.
     * Tiene un limite maximo de 5 vidas.
     */
    public void sumarVida() {
        if (vidas < 5) {
            vidas++;
        }
    }

    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
    }

    private void reproducirDano() {
        GestorAudio.getInstance().reproducirSonido(sonidoDano);
    }

    public boolean estaHerido() {
        return herido;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getVidas() {
        return vidas;
    }

    public void setVolumen(float nuevoVolumen) {
        GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
    }

    /**
     * Libera los recursos del tarro al cerrar el juego.
     */
    public void destruir() {
        if (textura != null) textura.dispose();
        if (sonidoDano != null) sonidoDano.dispose();
    }
}
