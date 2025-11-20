package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase base para los PowerUps del juego (estrellas y corazones).
 * Define el ciclo de vida comun: movimiento, dibujo y activacion.
 * Cada tipo de PowerUp implementa su propio dibujo y efecto al activarse.
 */
public abstract class PowerUp implements Activable {

    protected Texture textura;
    protected float x, y;
    protected float velocidadY = 120f;  // Velocidad de caida
    protected Rectangle rect;  // Area de colision
    protected float rotacion = 0f;  // Para animaciones de rotacion

    public PowerUp(String nombreTextura, float x, float y) {
        this.textura = new Texture(nombreTextura);
        this.x = x;
        this.y = y;
        this.rect = new Rectangle(x, y, 48, 48);
        inicializar();
    }

    /**
     * Ciclo de vida completo del PowerUp: actualiza, prepara y dibuja.
     */
    public final void cicloVidaCompleto(float deltaTime, SpriteBatch batch) {
        actualizarMovimiento(deltaTime);
        prepararDibujo(deltaTime);
        dibujar(batch);
    }

    /**
     * Permite a las subclases hacer inicializaciones especificas.
     */
    protected void inicializar() {
        // Las subclases pueden sobrescribir este metodo
    }

    /**
     * Mueve el PowerUp hacia abajo en la pantalla.
     */
    protected void actualizarMovimiento(float dt) {
        y -= velocidadY * dt;
        rect.setPosition(x, y);
    }

    /**
     * Permite a las subclases preparar animaciones antes de dibujar.
     */
    protected void prepararDibujo(float deltaTime) {
        // Las subclases pueden sobrescribir este metodo
    }

    /**
     * Dibuja el PowerUp delegando el dibujo especifico a las subclases.
     */
    public final void dibujar(SpriteBatch batch) {
        float ancho = 64;
        float alto = 64;
        dibujarTextura(batch, ancho, alto);
    }

    /**
     * Cada subclase implementa como dibujar su textura.
     */
    protected abstract void dibujarTextura(SpriteBatch batch, float ancho, float alto);

    public void actualizar(float dt) {
        actualizarMovimiento(dt);
    }

    public boolean colisionaCon(Tarro tarro) {
        return rect.overlaps(tarro.getRectangulo());
    }

    public boolean estaFueraPantalla() {
        return y + rect.height < 0;
    }

    public void dispose() {
        if (textura != null)
            textura.dispose();
    }

    /**
     * Cada subclase define que efecto tiene al ser recogido.
     */
    @Override
    public abstract void activar(Tarro tarro);
}
