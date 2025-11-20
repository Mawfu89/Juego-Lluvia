package puppy.code;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * PowerUp que otorga puntos extra al jugador.
 * Se representa como una estrella dorada que rota continuamente.
 */
public class PowerUpPuntos extends PowerUp {

    public PowerUpPuntos(float x, float y) {
        super("star.png", x, y);
    }

    /**
     * Actualiza la rotacion de la estrella cada frame.
     */
    @Override
    protected void prepararDibujo(float deltaTime) {
        rotacion += 120 * deltaTime;
    }

    /**
     * Dibuja la estrella rotando y con color dorado.
     */
    @Override
    protected void dibujarTextura(SpriteBatch batch, float ancho, float alto) {
        float escala = 1.0f;
        batch.setColor(1f, 1f, 0f, 1f);  // Color dorado
        batch.draw(
            textura,
            rect.x + ancho / 2, rect.y + alto / 2, ancho / 2, alto / 2,
            ancho, alto, escala, escala,
            rotacion, 0, 0, (int) textura.getWidth(), (int) textura.getHeight(),
            false, false
        );
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void activar(Tarro tarro) {
        tarro.sumarPuntos(5);
    }
}
