package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Define como se mueven las gotas en el juego.
 * Permite tener diferentes tipos de movimiento (normal, rapido, lento, etc.)
 * sin modificar la clase que las usa.
 */
public interface EstrategiaMovimiento {
    /**
     * Mueve una gota segun la estrategia implementada.
     */
    void mover(Rectangle gota, float deltaTime);
}

