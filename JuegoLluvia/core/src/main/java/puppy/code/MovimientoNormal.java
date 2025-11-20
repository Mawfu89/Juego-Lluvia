package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Estrategia de movimiento normal: movimiento vertical constante
 */
public class MovimientoNormal implements EstrategiaMovimiento {
    private float velocidad;
    
    public MovimientoNormal(float velocidad) {
        this.velocidad = velocidad;
    }
    
    @Override
    public void mover(Rectangle gota, float deltaTime) {
        gota.y -= velocidad * deltaTime;
    }
}

