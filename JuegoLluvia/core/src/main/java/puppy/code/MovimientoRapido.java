package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Movimiento rapido: las gotas caen 50% mas rapido que lo normal.
 */
public class MovimientoRapido implements EstrategiaMovimiento {
    private float velocidad;
    
    public MovimientoRapido(float velocidad) {
        this.velocidad = velocidad * 1.5f;
    }
    
    @Override
    public void mover(Rectangle gota, float deltaTime) {
        gota.y -= velocidad * deltaTime;
    }
}

