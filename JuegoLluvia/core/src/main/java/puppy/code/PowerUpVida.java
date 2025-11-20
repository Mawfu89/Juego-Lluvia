package puppy.code;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * PowerUp que otorga una vida adicional al jugador (corazón)
 * Implementa el patrón Template Method
 */
public class PowerUpVida extends PowerUp {

    public PowerUpVida(float x, float y) {
        super("heart.png", x, y); // Usa la textura del corazón
    }

    /**
     * Implementación del hook method: prepara el efecto de parpadeo (alpha pulsante)
     */
    @Override
    protected void prepararDibujo(float deltaTime) {
        // El efecto de alpha se calcula en dibujarTextura usando tiempo actual
    }

    /**
     * Implementación del método primitivo: dibuja el corazón con efecto de parpadeo
     */
    @Override
    protected void dibujarTextura(SpriteBatch batch, float ancho, float alto) {
        // Efecto de parpadeo usando sin para alpha pulsante
        float alpha = 0.5f + 0.5f * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000f * 6.28f);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(textura, rect.x, rect.y, ancho, alto);
        batch.setColor(1f, 1f, 1f, 1f); // Restaurar color
    }

    @Override
    public void activar(Tarro tarro) {
        // Suma una vida extra al jugador
        tarro.sumarVida();
    }
}
