package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase abstracta base para todos los PowerUps del juego.
 * Implementa el movimiento, dibujo y detección de colisiones.
 * Las subclases definen su efecto al activarse.
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
        // 🔹 Aumentamos el tamaño de colisión (de 32x32 a 48x48)
        this.rect = new Rectangle(x, y, 48, 48);
    }

    // --- Movimiento vertical ---
    public void actualizar(float dt) {
        y -= velocidadY * dt;
        rect.setPosition(x, y);
    }

 // --- Dibujo animado (efecto visual + tamaño aumentado) ---
    public void dibujar(SpriteBatch batch) {
        float escala = 1.0f; 
        float ancho = 64;
        float alto = 64;

        if (this instanceof PowerUpPuntos) {
            rotacion += 120 * com.badlogic.gdx.Gdx.graphics.getDeltaTime(); 

            batch.setColor(1f, 1f, 0f, 1f);
            batch.draw(
                textura,
                rect.x + ancho / 2, rect.y + alto / 2, ancho / 2, alto / 2, ancho, alto, escala, escala,                   
                rotacion, 0, 0, (int) textura.getWidth(), (int) textura.getHeight(), false, false
            );
            batch.setColor(1f, 1f, 1f, 1f); 
        } 
        else if (this instanceof PowerUpVida) {

            float alpha = 0.5f + 0.5f * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000f * 6.28);
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(textura, rect.x, rect.y, ancho, alto);
            batch.setColor(1f, 1f, 1f, 1f);
        } 
        else {

            batch.draw(textura, rect.x, rect.y, ancho, alto);
        }
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
