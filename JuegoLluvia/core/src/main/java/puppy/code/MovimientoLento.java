package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Movimiento lento: las gotas caen 30% mas lento que lo normal.
 */
public class MovimientoLento implements EstrategiaMovimiento {
    private float velocidad;
    
    public MovimientoLento(float velocidad) {
        this.velocidad = velocidad * 0.7f;
    }
    
    @Override
    public void mover(Rectangle gota, float deltaTime) {
        gota.y -= velocidad * deltaTime;
    }
}

