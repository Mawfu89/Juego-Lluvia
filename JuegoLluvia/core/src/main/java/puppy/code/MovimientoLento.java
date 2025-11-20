package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Estrategia de movimiento lento: movimiento vertical más pausado
 */
public class MovimientoLento implements EstrategiaMovimiento {
    private float velocidad;
    
    public MovimientoLento(float velocidad) {
        this.velocidad = velocidad * 0.7f; // 30% más lento
    }
    
    @Override
    public void mover(Rectangle gota, float deltaTime) {
        gota.y -= velocidad * deltaTime;
    }
}

