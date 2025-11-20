# Juego Lluvia

Juego desarrollado con LibGDX donde el objetivo es atrapar gotas azules y evitar las rojas.

## Características

- Sistema de dificultades (Fácil, Medio, Difícil)
- Tutorial interactivo
- Power-ups (estrellas para puntos, corazones para vidas)
- Menús completos (Principal, Opciones, Pausa, Game Over)
- Sistema de audio con gestión centralizada

## Patrones de Diseño Implementados

- **Singleton (GM2.1)**: GestorAudio para gestión centralizada de audio
- **Template Method (GM2.2)**: PowerUp para ciclo de vida estructurado
- **Strategy (GM2.3)**: EstrategiaMovimiento y NivelDificultad

## Controles

- **A/D** o **Flechas Izquierda/Derecha**: Mover el tarro
- **P** o **ESC**: Pausar/Reanudar
- **F11**: Pantalla completa

## Requisitos

- Java 8 o superior
- Gradle

## Compilación

```bash
./gradlew build
```

## Ejecución

```bash
./gradlew lwjgl3:run
```


