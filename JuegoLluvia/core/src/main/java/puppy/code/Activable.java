package puppy.code;

/**
 * Interfaz para objetos que pueden activarse cuando el jugador los toca.
 * Los PowerUps implementan esta interfaz para definir su efecto.
 */
public interface Activable {
    void activar(Tarro tarro);
}
