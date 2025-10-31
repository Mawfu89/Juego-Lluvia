package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Tutorial interactivo por fases:
 *  - MOVER: desplaza el tarro hasta la zona amarilla.
 *  - ATRAPAR_BUENA: cae una gota azul; debes atraparla.
 *  - EVITAR_MALA: cae una gota roja; debes dejarla caer.
 *  - RESUMEN: tips + pasar a jugar (ESPACIO/ENTER) o volver al menú (ESC).
 */
public class Tutorial {

    private enum Fase { MOVER, ATRAPAR_BUENA, EVITAR_MALA, RESUMEN }

    // Mundo base
    private static final float ANCHO = 800, ALTO = 480;

    // Texturas (reutiliza los mismos assets del juego)
    private final Texture texTarro  = new Texture("bucket.png");
    private final Texture texBuena  = new Texture("drop.png");
    private final Texture texMala   = new Texture("dropBad.png");

    // Entidades
    private Rectangle tarro = new Rectangle(ANCHO / 2f - 32, 32, 64, 64);
    private final Rectangle zonaObjetivo = new Rectangle(ANCHO * 0.15f, 32, 120, 64);
    private float velocidadTarro = 300f;

    // Gota actual
    private Texture texGotaActual = null;
    private boolean esBuena = false;
    private float gx, gy, gvy;

    // Estado del tutorial
    private Fase fase = Fase.MOVER;
    private float tiempo = 0f;
    private boolean pedirMenu = false;
    private boolean pedirJugar = false;

    // ==============================================================
    //                       CICLO DE VIDA
    // ==============================================================
    public void reiniciar() {
        fase = Fase.MOVER;
        tiempo = 0f;
        pedirMenu = false;
        pedirJugar = false;
        tarro.setPosition(ANCHO / 2f - 32, 32);
        texGotaActual = null;
    }

    public void actualizar(float dt, OrthographicCamera cam, SpriteBatch batch, BitmapFont fuente) {
        tiempo += dt;

        // --- Entrada de usuario ---
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            tarro.x -= velocidadTarro * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            tarro.x += velocidadTarro * dt;

        tarro.x = Math.max(0, Math.min(ANCHO - tarro.width, tarro.x));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) pedirMenu = true;

        // --- Lógica de fases ---
        switch (fase) {
            case MOVER:
                if (tarro.overlaps(zonaObjetivo) || Math.abs(tarro.x - zonaObjetivo.x) < 8) {
                    if (tiempo > 0.6f) pasarFase(Fase.ATRAPAR_BUENA);
                }
                break;

            case ATRAPAR_BUENA:
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
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    pedirJugar = true;
                }
                break;
        }

        // --- Dibujo ---
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        // Zona objetivo (solo en fase 1)
        if (fase == Fase.MOVER) {
            batch.setColor(Color.YELLOW);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y, zonaObjetivo.width, 3);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y + zonaObjetivo.height - 3, zonaObjetivo.width, 3);
            batch.draw(texTarro, zonaObjetivo.x, zonaObjetivo.y, 3, zonaObjetivo.height);
            batch.draw(texTarro, zonaObjetivo.x + zonaObjetivo.width - 3, zonaObjetivo.y, 3, zonaObjetivo.height);
            batch.setColor(Color.WHITE);
        }

        // Dibujo de gota activa
        if (texGotaActual != null)
            batch.draw(texGotaActual, gx, gy, 32, 32);

        // Tarro
        batch.draw(texTarro, tarro.x, tarro.y, tarro.width, tarro.height);

        // Mensajes por fase
        float y = ALTO - 20;
        switch (fase) {
            case MOVER:
                fuente.draw(batch, "Fase 1/3: MUEVE el tarro con ← → (A/D) hasta el rectángulo amarillo", 20, y);
                fuente.draw(batch, "ESC: Menú", 20, y - 22);
                break;

            case ATRAPAR_BUENA:
                fuente.setColor(Color.CYAN);
                fuente.draw(batch, "Fase 2/3: ATRAPA la gota azul 💧 para sumar puntos", 20, y);
                fuente.setColor(Color.WHITE);
                break;

            case EVITAR_MALA:
                fuente.setColor(Color.SALMON);
                fuente.draw(batch, "Fase 3/3: EVITA la gota roja ❌ (déjala caer)", 20, y);
                fuente.setColor(Color.WHITE);
                break;

            case RESUMEN:
                fuente.setColor(Color.GOLD);
                fuente.draw(batch, "Resumen: ← → (A/D) para mover. Azul = +1 | Roja = -1 vida", 20, y);
                fuente.setColor(Color.WHITE);
                fuente.draw(batch, "Presiona ESPACIO para JUGAR o ESC para volver al Menú", 20, y - 22);
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
            if (fase == Fase.EVITAR_MALA && !esBuena)
                pasarFase(Fase.RESUMEN);
            texGotaActual = null;
        }
    }

    private void reiniciarFase() {
        tiempo = 0f;
        texGotaActual = null;
    }

    private void pasarFase(Fase siguiente) {
        tiempo = 0f;
        texGotaActual = null;
        fase = siguiente;
    }

    // ==============================================================
    //                       GETTERS Y UTILIDADES
    // ==============================================================
    public boolean solicitaVolverMenu() { return pedirMenu; }
    public boolean solicitaJugar() { return pedirJugar; }

    public void dispose() {
        texTarro.dispose();
        texBuena.dispose();
        texMala.dispose();
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
