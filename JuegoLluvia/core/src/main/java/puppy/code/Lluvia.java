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
 * Gestiona las gotas que caen, los PowerUps y la musica de fondo.
 * Se encarga de crear gotas, moverlas, detectar colisiones y generar PowerUps.
 */
public class Lluvia {

    private Texture texturaGotaBuena;
    private Texture texturaGotaMala;
    private Sound sonidoGota;
    private Music musicaLluvia;

    // Listas de gotas activas en pantalla
    private Array<Rectangle> gotasBuenas;
    private Array<Rectangle> gotasMalas;
    private long ultimoTiempoGota;  // Controla cuando crear la proxima gota
    
    // Estrategias de movimiento para las gotas (patron Strategy)
    private EstrategiaMovimiento estrategiaBuena;
    private EstrategiaMovimiento estrategiaMala;

    // PowerUps que aparecen durante el juego
    private Array<PowerUp> powerUps;
    private long ultimoTiempoPowerUp;  // Controla cuando crear el proximo PowerUp
    
    // Configuracion de dificultad actual
    private NivelDificultad nivelDificultad;

    public Lluvia(Texture gotaBuena, Texture gotaMala, Sound sonidoGota, Music musicaLluvia) {
        this.texturaGotaBuena = gotaBuena;
        this.texturaGotaMala = gotaMala;
        this.sonidoGota = sonidoGota;
        this.musicaLluvia = musicaLluvia;
        this.nivelDificultad = new DificultadMedio();
    }

    /**
     * Inicializa el sistema de lluvia y comienza a reproducir la musica.
     */
    public void crear() {
        gotasBuenas = new Array<>();
        gotasMalas = new Array<>();
        powerUps = new Array<>();

        // Configurar velocidades segun la dificultad
        float velocidadBuena = nivelDificultad.getVelocidadGotasBuenas();
        float velocidadMala = nivelDificultad.getVelocidadGotasMalas();
        estrategiaBuena = new MovimientoNormal(velocidadBuena);
        estrategiaMala = new MovimientoNormal(velocidadMala);

        // Iniciar musica de fondo
        if (musicaLluvia != null) {
            musicaLluvia.setLooping(true);
            GestorAudio.getInstance().aplicarVolumen(musicaLluvia);
            musicaLluvia.play();
        }

        // Crear las primeras gotas para empezar el juego
        crearGotaBuena();
        crearGotaMala();
        ultimoTiempoPowerUp = TimeUtils.nanoTime();
    }
    
    /**
     * Cambia la dificultad del juego y actualiza las velocidades de las gotas.
     */
    public void setNivelDificultad(NivelDificultad nivel) {
        this.nivelDificultad = nivel;
        if (estrategiaBuena != null && estrategiaMala != null) {
            estrategiaBuena = new MovimientoNormal(nivel.getVelocidadGotasBuenas());
            estrategiaMala = new MovimientoNormal(nivel.getVelocidadGotasMalas());
        }
    }
    
    public NivelDificultad getNivelDificultad() {
        return nivelDificultad;
    }

    /**
     * Crea una nueva gota buena en una posicion aleatoria en la parte superior.
     */
    private void crearGotaBuena() {
        Rectangle gota = new Rectangle();
        float ancho = texturaGotaBuena.getWidth();
        float alto = texturaGotaBuena.getHeight();
        gota.x = MathUtils.random(0, 800 - ancho);
        gota.y = 480;
        gota.width = ancho;
        gota.height = alto;
        gotasBuenas.add(gota);
        ultimoTiempoGota = TimeUtils.nanoTime();
    }

    /**
     * Crea una nueva gota mala en una posicion aleatoria en la parte superior.
     */
    private void crearGotaMala() {
        Rectangle gota = new Rectangle();
        float ancho = texturaGotaMala.getWidth();
        float alto = texturaGotaMala.getHeight();
        gota.x = MathUtils.random(0, 800 - ancho);
        gota.y = 480;
        gota.width = ancho;
        gota.height = alto;
        gotasMalas.add(gota);
    }

    /**
     * Crea un PowerUp aleatorio (vida o puntos) en la parte superior.
     */
    private void crearPowerUp() {
        float x = MathUtils.random(0, 800 - 48);
        float y = 480;

        // Probabilidad 50/50 entre vida y puntos
        if (MathUtils.randomBoolean(0.5f)) {
            powerUps.add(new PowerUpVida(x, y));
        } else {
            powerUps.add(new PowerUpPuntos(x, y));
        }
    }

    /**
     * Actualiza todas las gotas y PowerUps: las mueve, detecta colisiones y crea nuevas.
     * Este metodo se llama cada frame durante el juego.
     */
    public void actualizarMovimiento(Tarro tarro) {
        // Crear nuevas gotas segun el intervalo configurado por la dificultad
        long intervalo = nivelDificultad.getIntervaloCreacionGotas();
        if (TimeUtils.nanoTime() - ultimoTiempoGota > intervalo) {
            float probabilidad = nivelDificultad.getProbabilidadGotaBuena();
            if (MathUtils.randomBoolean(probabilidad))
                crearGotaBuena();
            else
                crearGotaMala();
        }

        // Actualizar gotas buenas
        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (int i = gotasBuenas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasBuenas.get(i);
            estrategiaBuena.mover(gota, deltaTime);

            // Eliminar gotas que salieron de la pantalla
            if (gota.y + gota.height < 0) {
                gotasBuenas.removeIndex(i);
                continue;
            }

            // Detectar colision con el tarro
            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.sumarPunto();
                reproducirSonido();
                gotasBuenas.removeIndex(i);
            }
        }

        // Actualizar gotas malas
        for (int i = gotasMalas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasMalas.get(i);
            estrategiaMala.mover(gota, deltaTime);

            if (gota.y + gota.height < 0) {
                gotasMalas.removeIndex(i);
                continue;
            }

            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.restarVida();
                gotasMalas.removeIndex(i);
            }
        }

        // Crear PowerUps segun el intervalo de la dificultad
        long intervaloPowerUp = nivelDificultad.getIntervaloPowerUps();
        if (TimeUtils.nanoTime() - ultimoTiempoPowerUp > intervaloPowerUp) {
            crearPowerUp();
            ultimoTiempoPowerUp = TimeUtils.nanoTime();
        }

        // Actualizar PowerUps
        float dt = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.actualizar(dt);

            if (p.estaFueraPantalla()) {
                powerUps.removeIndex(i);
                continue;
            }

            if (p.colisionaCon(tarro)) {
                p.activar(tarro);
                powerUps.removeIndex(i);
            }
        }
    }

    /**
     * Dibuja todas las gotas y PowerUps en pantalla.
     */
    public void actualizarDibujoLluvia(SpriteBatch batch) {
        for (Rectangle gota : gotasBuenas)
            batch.draw(texturaGotaBuena, gota.x, gota.y, gota.width, gota.height);
        for (Rectangle gota : gotasMalas)
            batch.draw(texturaGotaMala, gota.x, gota.y, gota.width, gota.height);

        for (PowerUp p : powerUps)
            p.dibujar(batch);
    }

    private void reproducirSonido() {
        GestorAudio.getInstance().reproducirSonido(sonidoGota);
    }

    /**
     * Libera todos los recursos al cerrar el juego.
     */
    public void destruir() {
        if (texturaGotaBuena != null) texturaGotaBuena.dispose();
        if (texturaGotaMala != null) texturaGotaMala.dispose();
        if (sonidoGota != null) sonidoGota.dispose();
        if (musicaLluvia != null) musicaLluvia.dispose();
        for (PowerUp p : powerUps) p.dispose();
    }

    public void setVolumen(float nuevoVolumen) {
        GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
        GestorAudio.getInstance().aplicarVolumen(musicaLluvia);
    }
}
