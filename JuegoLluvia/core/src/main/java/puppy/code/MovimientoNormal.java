package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Movimiento vertical constante hacia abajo.
 * Es el movimiento estandar que usan la mayoria de las gotas.
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

