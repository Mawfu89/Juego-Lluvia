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
 * sus movimientos, colisiones, sonidos y los PowerUps.
 * 
 * Utiliza el patrón Strategy para el movimiento de gotas y
 * el sistema de niveles de dificultad para ajustar la dificultad del juego.
 */
public class Lluvia {

    // Texturas y sonidos
    private Texture texturaGotaBuena;
    private Texture texturaGotaMala;
    private Sound sonidoGota;
    private Music musicaLluvia;

    // Arrays de gotas
    private Array<Rectangle> gotasBuenas;
    private Array<Rectangle> gotasMalas;
    private long ultimoTiempoGota;
    
    // Estrategias de movimiento (Patrón Strategy - GM2.3)
    private EstrategiaMovimiento estrategiaBuena;
    private EstrategiaMovimiento estrategiaMala;

    // PowerUps (usando Template Method - GM2.2)
    private Array<PowerUp> powerUps;
    private long ultimoTiempoPowerUp;
    
    // Nivel de dificultad (Patrón Strategy - GM2.3)
    private NivelDificultad nivelDificultad;

    /**
     * Constructor de la clase Lluvia
     * @param gotaBuena Textura de las gotas buenas
     * @param gotaMala Textura de las gotas malas
     * @param sonidoGota Sonido al atrapar una gota buena
     * @param musicaLluvia Música de fondo del juego
     */
    public Lluvia(Texture gotaBuena, Texture gotaMala, Sound sonidoGota, Music musicaLluvia) {
        this.texturaGotaBuena = gotaBuena;
        this.texturaGotaMala = gotaMala;
        this.sonidoGota = sonidoGota;
        this.musicaLluvia = musicaLluvia;
        // Por defecto, dificultad media
        this.nivelDificultad = new DificultadMedio();
    }

    /**
     * Inicializa el sistema de lluvia
     * Configura las estrategias de movimiento según el nivel de dificultad
     */
    public void crear() {
        gotasBuenas = new Array<>();
        gotasMalas = new Array<>();
        powerUps = new Array<>();

        // Inicializar estrategias de movimiento según la dificultad (Patrón Strategy)
        float velocidadBuena = nivelDificultad.getVelocidadGotasBuenas();
        float velocidadMala = nivelDificultad.getVelocidadGotasMalas();
        estrategiaBuena = new MovimientoNormal(velocidadBuena);
        estrategiaMala = new MovimientoNormal(velocidadMala);

        // Reproducir música ambiente usando GestorAudio (Patrón Singleton - GM2.1)
        if (musicaLluvia != null) {
            musicaLluvia.setLooping(true);
            GestorAudio.getInstance().aplicarVolumen(musicaLluvia);
            musicaLluvia.play();
        }

        // Crear gotas iniciales
        crearGotaBuena();
        crearGotaMala();
        ultimoTiempoPowerUp = TimeUtils.nanoTime();
    }
    
    /**
     * Establece el nivel de dificultad del juego
     * @param nivel Nivel de dificultad a aplicar
     */
    public void setNivelDificultad(NivelDificultad nivel) {
        this.nivelDificultad = nivel;
        // Actualizar estrategias con las nuevas velocidades
        if (estrategiaBuena != null && estrategiaMala != null) {
            estrategiaBuena = new MovimientoNormal(nivel.getVelocidadGotasBuenas());
            estrategiaMala = new MovimientoNormal(nivel.getVelocidadGotasMalas());
        }
    }
    
    /**
     * Obtiene el nivel de dificultad actual
     * @return Nivel de dificultad actual
     */
    public NivelDificultad getNivelDificultad() {
        return nivelDificultad;
    }

    // --- Crear gotas ---
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

    // --- Crear PowerUp (con probabilidades balanceadas) ---
    private void crearPowerUp() {
        float x = MathUtils.random(0, 800 - 48);
        float y = 480;

        // 50% estrella, 50% corazón
        if (MathUtils.randomBoolean(0.5f)) {
            powerUps.add(new PowerUpVida(x, y));     // Corazón = vida
        } else {
            powerUps.add(new PowerUpPuntos(x, y));   // Estrella = puntos
        }
    }

    /**
     * Actualiza el movimiento de todas las gotas y PowerUps
     * Utiliza las estrategias de movimiento y el nivel de dificultad
     * @param tarro El jugador (tarro) para detectar colisiones
     */
    public void actualizarMovimiento(Tarro tarro) {
        // Crear nuevas gotas según el intervalo de la dificultad
        long intervalo = nivelDificultad.getIntervaloCreacionGotas();
        if (TimeUtils.nanoTime() - ultimoTiempoGota > intervalo) {
            float probabilidad = nivelDificultad.getProbabilidadGotaBuena();
            if (MathUtils.randomBoolean(probabilidad))
                crearGotaBuena();
            else
                crearGotaMala();
        }

        // === Gotas buenas (usando Strategy) ===
        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (int i = gotasBuenas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasBuenas.get(i);
            estrategiaBuena.mover(gota, deltaTime); // Usa la estrategia de movimiento

            if (gota.y + 64 < 0) {
                gotasBuenas.removeIndex(i);
                continue;
            }

            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.sumarPunto();
                reproducirSonido();
                gotasBuenas.removeIndex(i);
            }
        }

        // === Gotas malas (usando Strategy) ===
        for (int i = gotasMalas.size - 1; i >= 0; i--) {
            Rectangle gota = gotasMalas.get(i);
            estrategiaMala.mover(gota, deltaTime); // Usa la estrategia de movimiento

            if (gota.y + 64 < 0) {
                gotasMalas.removeIndex(i);
                continue;
            }

            if (gota.overlaps(tarro.getRectangulo())) {
                tarro.restarVida();
                gotasMalas.removeIndex(i);
            }
        }

        // === Crear nuevo PowerUp según el intervalo de la dificultad ===
        long intervaloPowerUp = nivelDificultad.getIntervaloPowerUps();
        if (TimeUtils.nanoTime() - ultimoTiempoPowerUp > intervaloPowerUp) {
            crearPowerUp();
            ultimoTiempoPowerUp = TimeUtils.nanoTime();
        }

        // === Actualizar PowerUps (usando Template Method) ===
        float dt = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            // Actualizar movimiento usando el template method
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

    // --- Dibujo ---
    public void actualizarDibujoLluvia(SpriteBatch batch) {
        // Dibujar gotas con tamaño fijo 64x64 para que coincida con los rectángulos de colisión
        for (Rectangle gota : gotasBuenas)
            batch.draw(texturaGotaBuena, gota.x, gota.y, gota.width, gota.height);
        for (Rectangle gota : gotasMalas)
            batch.draw(texturaGotaMala, gota.x, gota.y, gota.width, gota.height);

        // === Dibujar PowerUps ===
        for (PowerUp p : powerUps)
            p.dibujar(batch);
    }

    // --- Sonido (usando GestorAudio Singleton) ---
    private void reproducirSonido() {
        GestorAudio.getInstance().reproducirSonido(sonidoGota);
    }

    // --- Liberar recursos ---
    public void destruir() {
        if (texturaGotaBuena != null) texturaGotaBuena.dispose();
        if (texturaGotaMala != null) texturaGotaMala.dispose();
        if (sonidoGota != null) sonidoGota.dispose();
        if (musicaLluvia != null) musicaLluvia.dispose();
        for (PowerUp p : powerUps) p.dispose();
    }

    // --- Volumen global (usando GestorAudio Singleton) ---
    public void setVolumen(float nuevoVolumen) {
        GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
        GestorAudio.getInstance().aplicarVolumen(musicaLluvia);
    }
}
