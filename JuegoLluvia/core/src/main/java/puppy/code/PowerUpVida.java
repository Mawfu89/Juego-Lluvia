package puppy.code;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * PowerUp que otorga una vida adicional al jugador.
 * Se representa como un corazon que parpadea para llamar la atencion.
 */
public class PowerUpVida extends PowerUp {

    public PowerUpVida(float x, float y) {
        super("heart.png", x, y);
    }

    @Override
    protected void prepararDibujo(float deltaTime) {
        // El efecto de parpadeo se calcula directamente en dibujarTextura
    }

    /**
     * Dibuja el corazon con un efecto de parpadeo suave.
     */
    @Override
    protected void dibujarTextura(SpriteBatch batch, float ancho, float alto) {
        // Calcula un valor de transparencia que oscila entre 0.5 y 1.0
        float alpha = 0.5f + 0.5f * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000f * 6.28f);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(textura, rect.x, rect.y, ancho, alto);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void activar(Tarro tarro) {
        tarro.sumarVida();
    }
}
