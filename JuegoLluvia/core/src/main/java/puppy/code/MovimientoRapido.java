package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * Estrategia de movimiento rápido: movimiento vertical acelerado
 */
public class MovimientoRapido implements EstrategiaMovimiento {
    private float velocidad;
    
    public MovimientoRapido(float velocidad) {
        this.velocidad = velocidad * 1.5f; // 50% más rápido
    }
    
    @Override
    public void mover(Rectangle gota, float deltaTime) {
        gota.y -= velocidad * deltaTime;
    }
}

