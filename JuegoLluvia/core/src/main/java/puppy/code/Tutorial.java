package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Tutorial interactivo que ensena las mecanicas basicas del juego.
 * 
 * El tutorial tiene 4 fases:
 * 1. MOVER: Aprender a mover el tarro con las teclas
 * 2. ATRAPAR_BUENA: Atrapar una gota buena para sumar puntos
 * 3. EVITAR_MALA: Evitar una gota mala para no perder vidas
 * 4. RESUMEN: Resumen final con controles y mecanicas
 */
public class Tutorial {

    /**
     * Fases del tutorial.
     */
    private enum Fase { 
        MOVER,
        ATRAPAR_BUENA,
        EVITAR_MALA,
        RESUMEN
    }

    // Mundo base
    private static final float ANCHO = 800, ALTO = 480;

    // Texturas (reutiliza los mismos assets del juego)
    private final Texture texTarro  = new Texture("bucket.png");
    private final Texture texBuena  = new Texture("drop.png");
    private final Texture texMala   = new Texture("dropBad.png");
    private final Texture texEstrella = new Texture("star.png");
    private final Texture texCorazon = new Texture("heart.png");
    
    // Fondo del tutorial (usa el mismo que nivel fácil)
    private final Texture fondoFacil = new Texture("Facil.png");
    
    // Flechas para indicar dirección de movimiento
    private final Texture flechaIzquierda = new Texture("flechaizquierda.png");
    private final Texture flechaDerecha = new Texture("flechaDerecha.png");

    // Entidades
    private Rectangle tarro = new Rectangle(ANCHO / 2f - 32, 32, 64, 64);
    private final Rectangle zonaObjetivo = new Rectangle(ANCHO * 0.15f, 32, 120, 64);
    private float velocidadTarro = 300f;

    // Gota actual
    private Texture texGotaActual = null;
    private boolean esBuena = false;
    private float gx, gy, gvy;
    
    // PowerUp ya no se usa (tutorial de 3 fases)

    // Estado del tutorial
    private Fase fase = Fase.MOVER;
    private float tiempo = 0f;
    private boolean pedirMenu = false;
    private boolean pedirJugar = false;

    /**
     * Reinicia el tutorial al estado inicial cuando el jugador entra.
     */
    public void reiniciar() {
        fase = Fase.MOVER;
        tiempo = 0f;
        pedirMenu = false;
        pedirJugar = false;
        tarro.setPosition(ANCHO / 2f - 32, 32);
        texGotaActual = null;
    }

    /**
     * Actualiza la logica del tutorial y lo dibuja en pantalla.
     */
    public void actualizar(float dt, OrthographicCamera cam, SpriteBatch batch, BitmapFont fuente) {
        tiempo += dt;

        // Procesar entrada del jugador para mover el tarro
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            tarro.x -= velocidadTarro * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            tarro.x += velocidadTarro * dt;

        tarro.x = Math.max(0, Math.min(ANCHO - tarro.width, tarro.x));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) pedirMenu = true;

        // Logica especifica de cada fase
        switch (fase) {
            case MOVER:
                // Avanzar cuando el tarro llega a la zona objetivo
                if (tarro.overlaps(zonaObjetivo) || Math.abs(tarro.x - zonaObjetivo.x) < 8) {
                    if (tiempo > 0.6f) pasarFase(Fase.ATRAPAR_BUENA);
                }
                break;

            case ATRAPAR_BUENA:
                // Crear una gota buena para que el jugador la atrape
                if (texGotaActual == null && tiempo > 0.2f) {
                    texGotaActual = texBuena;
                    esBuena = true;
                    gx = clamp(tarro.x + tarro.width * 0.5f - 16 + (float)Math.random() * 60f - 30f, 0, ANCHO - 32);
                    gy = ALTO - 32;
                    gvy = -180f;
                }
                actualizarGota(dt);
                break;

            case EVITAR_MALA:
                // Crear una gota mala para que el jugador la evite
                if (texGotaActual == null && tiempo > 0.2f) {
                    texGotaActual = texMala;
                    esBuena = false;
                    gx = clamp(tarro.x + tarro.width * 0.5f - 16, 0, ANCHO - 32);
                    gy = ALTO - 32;
                    gvy = -200f;
                }
                actualizarGota(dt);
                break;
                
            case RESUMEN:
                // Permitir continuar al juego presionando espacio o enter
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    pedirJugar = true;
                }
                break;
        }

        // --- Dibujo ---
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        
        // Dibujar fondo del nivel fácil
        if (fondoFacil != null) {
            batch.draw(fondoFacil, 0, 0, ANCHO, ALTO);
        }

        // Zona objetivo y flechas indicadoras (solo en fase 1)
        if (fase == Fase.MOVER) {
            // Dibujar rectángulo objetivo en amarillo
            batch.setColor(Color.YELLOW);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y, zonaObjetivo.width, 3);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y + zonaObjetivo.height - 3, zonaObjetivo.width, 3);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y, 3, zonaObjetivo.height);
            batch.draw(texTarro, zonaObjetivo.x + zonaObjetivo.width - 3, zonaObjetivo.y, 3, zonaObjetivo.height);
            batch.setColor(Color.WHITE);
            
            // Dibujar flechas indicadoras de movimiento con efecto de pulso
            // Efecto de pulso usando tiempo para animación suave
            float pulso = 0.8f + 0.2f * (float)Math.sin(tiempo * 4f);
            float flechaSize = 40f * pulso; // Tamaño con efecto de pulso
            float flechaY = tarro.y + tarro.height / 2f - flechaSize / 2f;
            
            // Flecha izquierda (a la izquierda del tarro)
            float flechaIzqX = tarro.x - flechaSize - 20f;
            if (flechaIzqX > 10) {
                // Efecto de alpha pulsante para mejor visibilidad
                batch.setColor(1f, 1f, 1f, 0.7f + 0.3f * pulso);
                batch.draw(flechaIzquierda, flechaIzqX, flechaY, flechaSize, flechaSize);
            }
            
            // Flecha derecha (a la derecha del tarro)
            float flechaDerX = tarro.x + tarro.width + 20f;
            if (flechaDerX + flechaSize < ANCHO - 10) {
                batch.draw(flechaDerecha, flechaDerX, flechaY, flechaSize, flechaSize);
            }
            
            batch.setColor(Color.WHITE); // Restaurar color
        }

        // Dibujo de gota activa
        if (texGotaActual != null)
            batch.draw(texGotaActual, gx, gy, 32, 32);

        // Tarro
        batch.draw(texTarro, tarro.x, tarro.y, tarro.width, tarro.height);

        // PowerUp removido (tutorial de 3 fases)

        // Mensajes por fase con mejor formato
        float y = ALTO - 20;
        fuente.setColor(Color.WHITE);
        
        // Barra de progreso
        int faseActual = fase.ordinal() + 1;
        int totalFases = Fase.values().length;
        fuente.draw(batch, "Tutorial - Fase " + faseActual + "/" + totalFases, 20, ALTO - 5);
        
        switch (fase) {
            case MOVER:
                // Dibujar flechas como imágenes arriba del texto de instrucción
                float flechaSizeTexto = 28f;
                float textoInicioX = 20f;
                // Calcular posición X donde dice "con" para colocar las flechas
                GlyphLayout layoutCon = new GlyphLayout(fuente, "Fase 1: MUEVE el tarro con ");
                float xFlechas = textoInicioX + layoutCon.width;
                float flechaYTexto = y + 35f; // Arriba del texto principal
                
                // Dibujar flecha izquierda
                batch.draw(flechaIzquierda, xFlechas, flechaYTexto, flechaSizeTexto, flechaSizeTexto);
                
                // Dibujar flecha derecha (al lado de la izquierda)
                batch.draw(flechaDerecha, xFlechas + flechaSizeTexto + 6f, flechaYTexto, flechaSizeTexto, flechaSizeTexto);
                
                // Texto sin los caracteres de flecha, usando las imágenes en su lugar
                fuente.setColor(Color.YELLOW);
                fuente.draw(batch, "Fase 1: MUEVE el tarro con (A/D o Flechas) hasta el rectángulo amarillo", 20, y);
                fuente.setColor(Color.WHITE);
                fuente.draw(batch, "Presiona cualquier tecla de movimiento para comenzar", 20, y - 22);
                fuente.draw(batch, "ESC: Volver al Menú", 20, y - 44);
                break;

            case ATRAPAR_BUENA:
                fuente.setColor(Color.CYAN);
                fuente.draw(batch, "Fase 2: ATRAPA la gota azul para sumar puntos", 20, y);
                fuente.setColor(Color.WHITE);
                fuente.draw(batch, "Las gotas azules te dan +1 punto", 20, y - 22);
                break;

            case EVITAR_MALA:
                fuente.setColor(Color.SALMON);
                fuente.draw(batch, "Fase 3: EVITA la gota roja (dejala caer)", 20, y);
                fuente.setColor(Color.WHITE);
                fuente.draw(batch, "Las gotas rojas te quitan -1 vida. Dejalas caer!", 20, y - 22);
                break;
                
            case RESUMEN:
                fuente.setColor(Color.GOLD);
                fuente.draw(batch, "Fase 4: Tutorial Completado!", 20, y);
                fuente.setColor(Color.WHITE);
                fuente.draw(batch, "Controles: <- -> (A/D o Flechas) para mover", 20, y - 22);
                fuente.draw(batch, "Azul = +1 punto  |  Roja = -1 vida", 20, y - 44);
                fuente.draw(batch, "Estrella = +5 puntos  |  Corazon = +1 vida", 20, y - 66);
                fuente.setColor(Color.GREEN);
                fuente.draw(batch, "Presiona ESPACIO para JUGAR o ESC para volver al Menu", 20, y - 88);
                break;
        }

        batch.end();
    }

    // ==============================================================
    //                       LÓGICA INTERNA
    // ==============================================================
    private void actualizarGota(float dt) {
        if (texGotaActual == null) return;

        gy += gvy * dt;
        Rectangle rGota = new Rectangle(gx, gy, 32, 32);

        if (rGota.overlaps(tarro)) {
            if (fase == Fase.ATRAPAR_BUENA && esBuena)
                pasarFase(Fase.EVITAR_MALA);
            else if (fase == Fase.EVITAR_MALA && !esBuena)
                reiniciarFase(); // atrapó mala: reintenta fase
            texGotaActual = null;
        } else if (gy < -32) {
            // Si evitó la gota mala, avanzar a resumen
            if (fase == Fase.EVITAR_MALA && !esBuena) {
                pasarFase(Fase.RESUMEN);
            }
            texGotaActual = null;
        }
    }

    private void reiniciarFase() {
        tiempo = 0f;
        texGotaActual = null;
    }

    /**
     * Avanza a la siguiente fase del tutorial
     * @param siguiente La siguiente fase a mostrar
     */
    private void pasarFase(Fase siguiente) {
        tiempo = 0f;
        texGotaActual = null;
        fase = siguiente;
    }
    
    // Método actualizarPowerUp removido (tutorial de 3 fases)

    // ==============================================================
    //                       GETTERS Y UTILIDADES
    // ==============================================================
    public boolean solicitaVolverMenu() { return pedirMenu; }
    public boolean solicitaJugar() { return pedirJugar; }

    /**
     * Libera los recursos del tutorial
     * Debe llamarse al finalizar el juego
     */
    public void dispose() {
        if (texTarro != null) texTarro.dispose();
        if (texBuena != null) texBuena.dispose();
        if (texMala != null) texMala.dispose();
        if (texEstrella != null) texEstrella.dispose();
        if (texCorazon != null) texCorazon.dispose();
        if (fondoFacil != null) fondoFacil.dispose();
        if (flechaIzquierda != null) flechaIzquierda.dispose();
        if (flechaDerecha != null) flechaDerecha.dispose();
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
