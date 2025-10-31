package puppy.code;

/**
 * PowerUp que otorga puntos extra al jugador (estrella dorada ⭐)
 */
public class PowerUpPuntos extends PowerUp {

    public PowerUpPuntos(float x, float y) {
        super("star.png", x, y);
    }

    @Override
    public void activar(Tarro tarro) {
        // ⭐ Otorga puntos adicionales (por ejemplo, +5)
        tarro.sumarPuntos(5);
    }
}
