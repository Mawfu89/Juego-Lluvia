package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/**
 * GM2.3 - PATRÓN STRATEGY (ESTRATEGIA)
 * 
 * PROBLEMA:
 * Las gotas del juego tenían velocidades fijas y comportamiento de movimiento
 * hardcodeado. Esto dificultaba la extensión del juego con nuevos tipos de gotas
 * o comportamientos de movimiento dinámicos (rápidas, lentas, zigzag, etc.).
 * 
 * CONTEXTO:
 * - El juego necesita diferentes tipos de gotas con comportamientos de movimiento variados
 * - Las gotas buenas y malas tienen velocidades diferentes
 * - Se requiere flexibilidad para agregar nuevos tipos de movimiento sin modificar
 *   la clase Lluvia
 * - El movimiento debe ser intercambiable en tiempo de ejecución
 * 
 * SOLUCIÓN:
 * Implementación del patrón Strategy para encapsular algoritmos de movimiento
 * en clases separadas. Permite cambiar el comportamiento de movimiento de las
 * gotas sin modificar su estructura, facilitando la extensión y mantenimiento.
 * 
 * PARTICIPANTES:
 * - EstrategiaMovimiento (Strategy): Interfaz que define el contrato de movimiento
 *   - mover(Rectangle, float): Mueve la gota según la estrategia
 * - MovimientoNormal (ConcreteStrategy): Movimiento vertical constante
 * - MovimientoRapido (ConcreteStrategy): Movimiento vertical acelerado
 * - MovimientoLento (ConcreteStrategy): Movimiento vertical lento
 * - Lluvia (Context): Usa la estrategia para mover las gotas
 * 
 * UML:
 * ┌──────────────────────┐
 * │EstrategiaMovimiento  │
 * ├──────────────────────┤
 * │ + mover()            │
 * └──────────────────────┘
 *          ▲
 *          │ implements
 *          │
 *    ┌─────┴─────┬─────────────┬──────────────┐
 *    │           │             │              │
 * ┌──┴──┐  ┌─────┴────┐  ┌─────┴─────┐  ┌─────┴─────┐
 * │Normal│  │Rapido   │  │Lento     │  │Zigzag    │
 * └──────┘  └─────────┘  └──────────┘  └──────────┘
 */
public interface EstrategiaMovimiento {
    /**
     * Mueve una gota según la estrategia de movimiento implementada
     * @param gota Rectángulo que representa la gota
     * @param deltaTime Tiempo transcurrido desde el último frame
     */
    void mover(Rectangle gota, float deltaTime);
}

