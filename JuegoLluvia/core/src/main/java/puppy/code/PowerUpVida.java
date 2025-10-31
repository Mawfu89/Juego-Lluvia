package puppy.code;

/**
 * PowerUp que otorga una vida adicional al jugador (corazón ❤️)
 */
public class PowerUpVida extends PowerUp {

    public PowerUpVida(float x, float y) {
        super("heart.png", x, y); // Usa la textura del corazón
    }

    @Override
    public void activar(Tarro tarro) {
        // ❤️ Suma una vida extra al jugador
        tarro.sumarVida();
    }
}
