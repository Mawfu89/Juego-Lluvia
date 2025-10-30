package puppy.code;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Clase que gestiona la lógica de las gotas (buenas y malas),
 * su movimiento, colisiones y sonidos.
 */
public class Lluvia {

    private Texture texturaGotaBuena;
    private Texture texturaGotaMala;
    private Sound sonidoGota;
    private Music musicaLluvia;

    private Array<Rectangle> gotasBuenas;
    private Array<Rectangle> gotasMalas;
    private long ultimoTiempoGota;

    private float volumen = 1f;

    // --- Constructor ---
    public Lluvia(Texture gotaBuena, Texture gotaMala, Sound sonidoGota, Music musicaLluvia) {
        this.texturaGotaBuena = gotaBuena;
        this.texturaGotaMala = gotaMala;
        this.sonidoGota = sonidoGota;
        this.musicaLluvia = musicaLluvia;
    }

    // --- Inicialización del sistema de lluvia ---
    public void crear() {
        gotasBuenas = new Array<>();
        gotasMalas = new Array<>();

        // Reproducir música ambiente
        if (musicaLluvia != null) {
            musicaLluvia.setLooping(true);
            musicaLluvia.setVolume(volumen);
            musicaLluvia.play();
        }

        crearGotaBuena();
        crearGotaMala();
    }

    // --- Crear gotas individuales ---
    private void crearGotaBuena() {
        Rectangle gota = new Rectangle();
        gota.x = MathUtils.random(0, 800 - 64);
        gota.y = 480;
        gota.width = 64;
        gota.height = 64;
        gotasBuenas.add(gota);
        ultimoTiempoGota = TimeUtils.nanoTime();
    }

    private void crearGotaMala() {
        Rectangle gota = new Rectangle();
        gota.x = MathUtils.random(0, 800 - 64);
        gota.y = 480;
        gota.width = 64;
        gota.height = 64;
        gotasMalas.add(gota);
    }

    // --- Actualizar posición y colisiones ---
    public void actualizarMovimiento(Tarro tarro) {

        // Crear nuevas gotas cada 1 segundo
        if (TimeUtils.nanoTime() - ultimoTiempoGota > 1000000000) {
            if (MathUtils.randomBoolean(0.7f))
                crearGotaBuena();
            else
                crearGotaMala();
        }

        // === Gotas buenas ===
        for (int i = gotasBuenas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasBuenas.get(i);
            gota.y -= 200 * com.badlogic.gdx.Gdx.graphics.getDeltaTime();

            // Si sale de la pantalla
            if (gota.y + 64 < 0) {
                gotasBuenas.removeIndex(i);
                continue;
            }

            // Si colisiona con el tarro
            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.sumarPunto();
                reproducirSonido();
                gotasBuenas.removeIndex(i);
            }
        }

        // === Gotas malas ===
        // Corregido: iterar de atrás hacia adelante para evitar bloqueos
        for (int i = gotasMalas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasMalas.get(i);
            gota.y -= 180 * com.badlogic.gdx.Gdx.graphics.getDeltaTime();

            // Si sale de la pantalla
            if (gota.y + 64 < 0) {
                gotasMalas.removeIndex(i);
                continue;
            }

            // Si colisiona con el tarro
            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.restarVida();
                gotasMalas.removeIndex(i);
            }
        }
    }

    // --- Dibujo ---
    public void actualizarDibujoLluvia(SpriteBatch batch) {
        for (Rectangle gota : gotasBuenas)
            batch.draw(texturaGotaBuena, gota.x, gota.y);
        for (Rectangle gota : gotasMalas)
            batch.draw(texturaGotaMala, gota.x, gota.y);
    }

    // --- Sonido ---
    private void reproducirSonido() {
        if (sonidoGota != null)
            sonidoGota.play(volumen);
    }

    // --- Liberar recursos ---
    public void destruir() {
        if (texturaGotaBuena != null) texturaGotaBuena.dispose();
        if (texturaGotaMala != null) texturaGotaMala.dispose();
        if (sonidoGota != null) sonidoGota.dispose();
        if (musicaLluvia != null) musicaLluvia.dispose();
    }

    // --- Control de volumen global ---
    public void setVolumen(float nuevoVolumen) {
        this.volumen = Math.max(0f, Math.min(1f, nuevoVolumen));
        if (musicaLluvia != null)
            musicaLluvia.setVolume(volumen);
    }
}
