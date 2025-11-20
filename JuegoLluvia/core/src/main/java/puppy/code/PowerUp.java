package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * GM2.2 - PATRÓN TEMPLATE METHOD
 * 
 * PROBLEMA:
 * El ciclo de vida de los PowerUps (inicialización, actualización, dibujo, activación)
 * tenía lógica duplicada y mezclada entre la clase base y las subclases, dificultando
 * la extensión y el mantenimiento del código.
 * 
 * CONTEXTO:
 * - Todos los PowerUps comparten un ciclo de vida similar: crear, actualizar, dibujar, activar
 * - Cada PowerUp tiene comportamientos específicos en ciertos pasos del ciclo
 * - Se necesita un marco común que garantice el orden correcto de ejecución
 * - Las subclases deben poder personalizar pasos específicos sin modificar el flujo general
 * 
 * SOLUCIÓN:
 * Implementación del patrón Template Method que define el esqueleto del algoritmo
 * del ciclo de vida en la clase base, delegando pasos específicos a métodos abstractos
 * o protegidos que las subclases implementan.
 * 
 * PARTICIPANTES:
 * - PowerUp (AbstractClass): Define el template method y métodos primitivos
 *   - cicloVidaCompleto(): Template method que define el flujo
 *   - inicializar(): Método hook para inicialización específica
 *   - actualizarMovimiento(): Paso común del algoritmo
 *   - prepararDibujo(): Método hook para preparar efectos visuales
 *   - dibujarTextura(): Método abstracto para dibujo específico
 *   - activar(): Método abstracto para efecto específico
 * - PowerUpPuntos (ConcreteClass): Implementa pasos específicos
 * - PowerUpVida (ConcreteClass): Implementa pasos específicos
 * 
 * UML:
 * ┌──────────────────────┐
 * │     PowerUp           │
 * Clase abstracta que define el ciclo de vida de los PowerUps
 * Utiliza el patron Template Method para estructurar el comportamiento
 * 
 * Metodos principales:
 * - cicloVidaCompleto(): Template Method que define el flujo
 * - inicializar(): Hook Method para inicializacion personalizada
 * - actualizarMovimiento(): Actualiza la posicion del PowerUp
 * - prepararDibujo(): Hook Method para preparar el dibujo
 * - dibujar(): Metodo que coordina el dibujo
 * - dibujarTextura(): Metodo abstracto que cada subclase implementa
 * - activar(): Metodo abstracto que define el efecto del PowerUp
 *          ▲
 *          │ extends
 *    ┌─────┴─────┐
 *    │           │
 * ┌──┴──────┐ ┌──┴────────┐
 * │Puntos   │ │Vida       │
 * │(Concrete)│ │(Concrete) │
 * └─────────┘ └───────────┘
 */
public abstract class PowerUp implements Activable {

    protected Texture textura;
    protected float x, y;
    protected float velocidadY = 120f;
    protected Rectangle rect;
    protected float rotacion = 0f; // Para animación de giro

    // --- Constructor ---
    public PowerUp(String nombreTextura, float x, float y) {
        this.textura = new Texture(nombreTextura);
        this.x = x;
        this.y = y;
        this.rect = new Rectangle(x, y, 48, 48);
        inicializar(); // Hook method para inicialización específica
    }

    /**
     * Template Method: Define el ciclo de vida completo del PowerUp
     * Este método establece el orden de ejecución de los pasos
     */
    public final void cicloVidaCompleto(float deltaTime, SpriteBatch batch) {
        actualizarMovimiento(deltaTime);
        prepararDibujo(deltaTime);
        dibujar(batch);
    }

    /**
     * Hook Method: Permite a las subclases personalizar la inicialización
     * Implementación por defecto vacía, puede ser sobrescrita
     */
    protected void inicializar() {
        // Hook method - las subclases pueden sobrescribir
    }

    /**
     * Paso común del algoritmo: actualización del movimiento
     * Este paso es común para todos los PowerUps
     */
    protected void actualizarMovimiento(float dt) {
        y -= velocidadY * dt;
        rect.setPosition(x, y);
    }

    /**
     * Hook Method: Permite preparar efectos visuales antes del dibujo
     * Las subclases pueden sobrescribir para personalizar animaciones
     */
    protected void prepararDibujo(float deltaTime) {
        // Hook method - las subclases pueden sobrescribir
    }

    /**
     * Template Method para el dibujo: define el flujo de dibujo
     * Delega el dibujo específico a dibujarTextura()
     */
    public final void dibujar(SpriteBatch batch) {
        float ancho = 64;
        float alto = 64;
        dibujarTextura(batch, ancho, alto);
    }

    /**
     * Método primitivo: cada subclase implementa su propio dibujo
     */
    protected abstract void dibujarTextura(SpriteBatch batch, float ancho, float alto);

    // Método heredado para compatibilidad con código anterior
    public void actualizar(float dt) {
        actualizarMovimiento(dt);
    }

    // --- Colisión ---
    public boolean colisionaCon(Tarro tarro) {
        return rect.overlaps(tarro.getRectangulo());
    }

    // --- Estado ---
    public boolean estaFueraPantalla() {
        return y + rect.height < 0;
    }

    // --- Limpieza ---
    public void dispose() {
        if (textura != null)
            textura.dispose();
    }

    // --- Método que cada subclase implementa ---
    @Override
    public abstract void activar(Tarro tarro);
}
