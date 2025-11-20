package puppy.code;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Clase principal del juego "Lluvia".
 * 
 * Esta clase controla todo el flujo del juego:
 * - Menús principales y de opciones
 * - Sistema de selección de dificultad
 * - Tutorial interactivo
 * - Lógica del juego
 * - Pantalla de Game Over
 * 
 * Utiliza los siguientes patrones de diseño:
 * - Singleton (GM2.1): GestorAudio para gestión centralizada de audio
 * - Template Method (GM2.2): PowerUp para ciclo de vida estructurado
 * - Strategy (GM2.3): EstrategiaMovimiento y NivelDificultad
 * 
 * REQUISITOS CUMPLIDOS:
 * - GM1.4: Clase abstracta PowerUp con subclases PowerUpPuntos y PowerUpVida
 * - GM1.5: Interfaz Activable implementada por PowerUp
 * - GM2.1: Patrón Singleton en GestorAudio
 * - GM2.2: Patrón Template Method en PowerUp
 * - GM2.3: Patrón Strategy en EstrategiaMovimiento y NivelDificultad
 */
public class CangriMain extends ApplicationAdapter {

    /**
     * Estados posibles de la pantalla del juego
     */
    private enum EstadoPantalla { 
        MENU,              // Menú principal
        SELECCION_DIFICULTAD, // Selección de nivel de dificultad
        OPCIONES,          // Menú de opciones
        TUTORIAL,          // Tutorial interactivo
        JUEGO,             // Juego en curso
        PAUSA,             // Menú de pausa
        GAME_OVER          // Pantalla de fin de juego
    }
    
    private EstadoPantalla estado = EstadoPantalla.MENU;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Lluvia lluvia;
    
    // Texturas de fondos para menús
    private Texture fondoMenuPrincipal;
    private Texture fondoOpciones;
    private Texture fondoPausa;
    private Texture fondoGameOver;
    private Texture fondoFacil;
    private Texture fondoMedio;
    private Texture fondoDificil;
    
    // Texturas reutilizables
    private Texture texBlanco; // Para overlay de pausa
    private Texture texSlider; // Para slider de opciones
    private Texture texBucket; // Para reutilizar en inicializarJuego

    private Stage escMenu;
    private Skin skinMenu;
    private FitViewport vpMenu;

    private Stage escOpciones;
    private Skin skinOpc;
    private FitViewport vpOpc;
    private Label lblVolumen;

    private Stage escGameOver;
    private Skin skinGameOver;
    private FitViewport vpGameOver;
    private Label lblPuntajeFinal;
    private Label lblDificultadFinal;
    private Label lblMejorPuntaje;

    // Menú de selección de dificultad
    private Stage escDificultad;
    private Skin skinDificultad;
    private FitViewport vpDificultad;
    
    // Menú de pausa
    private Stage escPausa;
    private Skin skinPausa;
    private FitViewport vpPausa;
    private Label lblInfoPausa; // Label para mostrar información de la partida
    
    // Nivel de dificultad actual
    private NivelDificultad dificultadActual;
    
    // Mejor puntaje alcanzado
    private int mejorPuntaje = 0;
    
    // Animación del menú principal
    private float tiempoAnimacion = 0f; // Para animación sutil del subtítulo

    private Tutorial tutorial;

    /**
     * Método de inicialización del juego
     * Se ejecuta una vez al iniciar la aplicación
     */
    @Override
    public void create() {
        // Inicializar componentes gráficos básicos
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        // Cargar recursos de audio y texturas
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        texBucket = new Texture(Gdx.files.internal("bucket.png"));
        tarro = new Tarro(texBucket, hurtSound);
        Texture gota = new Texture(Gdx.files.internal("drop.png"));
        Texture gotaMala = new Texture(Gdx.files.internal("dropBad.png"));
        Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        lluvia = new Lluvia(gota, gotaMala, dropSound, rainMusic);
        
        // Cargar fondos de menús y niveles
        fondoMenuPrincipal = new Texture(Gdx.files.internal("Menu.png"));
        fondoOpciones = new Texture(Gdx.files.internal("Opciones.png"));
        fondoPausa = new Texture(Gdx.files.internal("Pausa.png"));
        fondoGameOver = new Texture(Gdx.files.internal("GameOver.png"));
        fondoFacil = new Texture(Gdx.files.internal("Facil.png"));
        fondoMedio = new Texture(Gdx.files.internal("Medio.png"));
        fondoDificil = new Texture(Gdx.files.internal("Dificil.png"));
        
        // Cargar texturas reutilizables
        texBlanco = new Texture(Gdx.files.internal("white.png"));
        texSlider = new Texture(Gdx.files.internal("white.png"));
        
        // Inicializar GestorAudio (Singleton - GM2.1) con volumen por defecto
        GestorAudio.getInstance().setVolumenMaestro(0.8f);
        
        // Establecer dificultad por defecto (medio)
        dificultadActual = new DificultadMedio();
        lluvia.setNivelDificultad(dificultadActual);
        
        // Inicializar entidades del juego
        inicializarJuego();

        // Crear todos los menús
        crearMenuPrincipal();
        crearMenuSeleccionDificultad();
        crearMenuOpciones();
        crearMenuPausa();
        crearMenuGameOver();
    }
    
    /**
     * Inicializa o reinicia el juego con la dificultad actual
     */
    private void inicializarJuego() {
        // Configurar tarro con vidas según la dificultad
        // Reutilizar textura del bucket en lugar de crear una nueva
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        tarro = new Tarro(texBucket, hurtSound);
        tarro.crear();
        // Establecer vidas iniciales según la dificultad
        tarro.setVidasIniciales(dificultadActual.getVidasIniciales());
        
        // Configurar lluvia con la dificultad actual
        lluvia.setNivelDificultad(dificultadActual);
        lluvia.crear();
        aplicarVolumen();
    }

    // ============================================================
    // MENÚ PRINCIPAL 
    // ============================================================
    private void crearMenuPrincipal() {
        vpMenu = new FitViewport(800, 480);
        escMenu = new Stage(vpMenu, batch);
        Gdx.input.setInputProcessor(escMenu);

        skinMenu = new Skin();
        skinMenu.add("fuente", font);

        // Estilos de botones mejorados con colores
        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinMenu.add("default", estiloBtn);
        
        TextButton.TextButtonStyle estiloBtnJugar = new TextButton.TextButtonStyle();
        estiloBtnJugar.font = font;
        estiloBtnJugar.fontColor = Color.GREEN;
        skinMenu.add("jugar", estiloBtnJugar);

        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        skinMenu.add("lbl", estiloLbl);

        Table tMenu = new Table();
        tMenu.setFillParent(true);
        tMenu.center();
        // Botones adaptables: ancho mínimo pero se expanden si es necesario
        tMenu.defaults().pad(8).minWidth(240).prefWidth(260).maxWidth(300).height(48).center();
        tMenu.padTop(180f); // Espacio para título animado
        escMenu.addActor(tMenu);

        // El título y subtítulo se dibujan en renderMenu() con animaciones
        // No los agregamos aquí para poder animarlos dinámicamente

        // Botones con estilos mejorados (sin emojis)
        TextButton btnJugar = new TextButton("JUGAR", skinMenu.get("jugar", TextButton.TextButtonStyle.class));
        TextButton btnTutorial = new TextButton("Tutorial", skinMenu);
        TextButton btnOpciones = new TextButton("Opciones", skinMenu);
        TextButton btnSalir = new TextButton("Salir", skinMenu);

        tMenu.add(btnJugar).center().row();
        tMenu.add(btnTutorial).center().row();
        tMenu.add(btnOpciones).center().row();
        tMenu.add(btnSalir).center().row();

        // === Botones ===
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Ir a selección de dificultad antes de jugar
                estado = EstadoPantalla.SELECCION_DIFICULTAD;
                Gdx.input.setInputProcessor(escDificultad);
            }
        });

        btnTutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tutorial == null) tutorial = new Tutorial();
                tutorial.reiniciar();
                estado = EstadoPantalla.TUTORIAL;
            }
        });

        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.OPCIONES;
                Gdx.input.setInputProcessor(escOpciones);
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    // ============================================================
    // MENÚ SELECCIÓN DE DIFICULTAD
    // ============================================================
    /**
     * Crea el menú de selección de dificultad
     * Permite al jugador elegir entre Fácil, Medio y Difícil
     */
    private void crearMenuSeleccionDificultad() {
        vpDificultad = new FitViewport(800, 480);
        escDificultad = new Stage(vpDificultad, batch);
        skinDificultad = new Skin();
        skinDificultad.add("fuente", font);

        // Estilos de botones con colores mejorados
        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinDificultad.add("default", estiloBtn);
        
        TextButton.TextButtonStyle estiloBtnFacil = new TextButton.TextButtonStyle();
        estiloBtnFacil.font = font;
        estiloBtnFacil.fontColor = Color.GREEN;
        skinDificultad.add("facil", estiloBtnFacil);
        
        TextButton.TextButtonStyle estiloBtnMedio = new TextButton.TextButtonStyle();
        estiloBtnMedio.font = font;
        estiloBtnMedio.fontColor = Color.YELLOW;
        skinDificultad.add("medio", estiloBtnMedio);
        
        TextButton.TextButtonStyle estiloBtnDificil = new TextButton.TextButtonStyle();
        estiloBtnDificil.font = font;
        estiloBtnDificil.fontColor = Color.RED;
        skinDificultad.add("dificil", estiloBtnDificil);

        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.CYAN);
        skinDificultad.add("lbl", estiloLbl);
        skinDificultad.add("titulo", estiloLblTitulo);

        Table tDificultad = new Table();
        tDificultad.setFillParent(true);
        tDificultad.center();
        // Botones adaptables con ancho flexible
        tDificultad.defaults().pad(6).minWidth(250).prefWidth(300).maxWidth(350).height(42).center();
        tDificultad.padTop(15f);
        escDificultad.addActor(tDificultad);

        // Título centrado
        Label titulo = new Label("Selecciona Dificultad", estiloLblTitulo);
        tDificultad.add(titulo).center().padBottom(25).row();

        // ===== OPCIÓN FÁCIL =====
        // Instancia de dificultad fácil (Strategy Pattern)
        DificultadFacil facil = new DificultadFacil();
        // Botón en verde para indicar facilidad/seguridad
        TextButton btnFacil = new TextButton("FACIL", skinDificultad.get("facil", TextButton.TextButtonStyle.class));
        tDificultad.add(btnFacil).center().padBottom(6).row();
        // Descripción informativa con wrap para adaptación de texto
        Label descFacil = new Label(facil.getDescripcion(), estiloLbl);
        descFacil.setWrap(true);
        tDificultad.add(descFacil).center().width(360).padBottom(15).row();
        
        btnFacil.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dificultadActual = facil;
                iniciarJuego();
            }
        });

        // ===== OPCIÓN MEDIO =====
        // Instancia de dificultad media (Strategy Pattern)
        DificultadMedio medio = new DificultadMedio();
        // Botón en amarillo para indicar equilibrio
        TextButton btnMedio = new TextButton("MEDIO", skinDificultad.get("medio", TextButton.TextButtonStyle.class));
        tDificultad.add(btnMedio).center().padBottom(6).row();
        // Descripción informativa
        Label descMedio = new Label(medio.getDescripcion(), estiloLbl);
        descMedio.setWrap(true);
        tDificultad.add(descMedio).center().width(360).padBottom(15).row();
        
        btnMedio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dificultadActual = medio;
                iniciarJuego();
            }
        });

        // ===== OPCIÓN DIFÍCIL =====
        // Instancia de dificultad difícil (Strategy Pattern)
        DificultadDificil dificil = new DificultadDificil();
        // Botón en rojo para indicar desafío/peligro
        TextButton btnDificil = new TextButton("DIFICIL", skinDificultad.get("dificil", TextButton.TextButtonStyle.class));
        tDificultad.add(btnDificil).center().padBottom(6).row();
        // Descripción informativa
        Label descDificil = new Label(dificil.getDescripcion(), estiloLbl);
        descDificil.setWrap(true);
        tDificultad.add(descDificil).center().width(360).padBottom(15).row();
        
        btnDificil.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dificultadActual = dificil;
                iniciarJuego();
            }
        });

        // ===== BOTÓN DE NAVEGACIÓN =====
        // Botón para volver al menú principal
        TextButton btnVolver = new TextButton("Volver al Menú", skinDificultad);
        tDificultad.add(btnVolver).center().padTop(10).row();
        
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }
    
    /**
     * Inicia el juego con la dificultad seleccionada
     */
    private void iniciarJuego() {
        inicializarJuego();
        estado = EstadoPantalla.JUEGO;
    }

    // ============================================================
    // MENÚ DE PAUSA - DISEÑO PROFESIONAL
    // ============================================================
    /**
     * Construye el menú de pausa con diseño centrado y estilos consistentes.
     * 
     * Características:
     * - Usa FitViewport para mantener proporciones en diferentes resoluciones
     * - Sistema de estilos centralizado para consistencia visual
     * - Layout con Table para centrado perfecto
     * - Título en amarillo, información en cian, botón principal en verde
     */
    private void crearMenuPausa() {
        // Inicialización del viewport y stage para el menú de pausa
        vpPausa = new FitViewport(800, 480);
        escPausa = new Stage(vpPausa, batch);
        skinPausa = new Skin();
        skinPausa.add("fuente", font);

        // ===== SISTEMA DE ESTILOS =====
        // Estilo base para botones secundarios (blanco neutro)
        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinPausa.add("default", estiloBtn);
        
        // Estilo para botón primario (verde - acción principal)
        // El verde indica acción positiva y es estándar en UI de juegos
        TextButton.TextButtonStyle estiloBtnReanudar = new TextButton.TextButtonStyle();
        estiloBtnReanudar.font = font;
        estiloBtnReanudar.fontColor = Color.GREEN;
        skinPausa.add("reanudar", estiloBtnReanudar);

        // Estilos para labels: texto normal y título destacado
        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.YELLOW);
        skinPausa.add("lbl", estiloLbl);
        skinPausa.add("titulo", estiloLblTitulo);

        // ===== LAYOUT PRINCIPAL =====
        // Table con fillParent para ocupar toda la pantalla
        // center() asegura centrado horizontal y vertical perfecto
        Table tPausa = new Table();
        tPausa.setFillParent(true);
        tPausa.center();
        
        // Configuración de botones: tamaños adaptables para diferentes resoluciones
        // minWidth/prefWidth/maxWidth permiten flexibilidad sin romper el diseño
        // center() en defaults() asegura que todos los elementos estén centrados
        tPausa.defaults().pad(8).minWidth(260).prefWidth(300).maxWidth(340).height(45).center();
        // Ajustar padding para centrado vertical mejorado
        tPausa.padBottom(60); // Espacio inferior para las instrucciones
        escPausa.addActor(tPausa);

        // ===== ELEMENTOS VISUALES =====
        // Título principal: "PAUSA" en amarillo para máxima visibilidad
        Label tituloPausa = new Label("PAUSA", estiloLblTitulo);
        tituloPausa.setAlignment(Align.center);
        tPausa.add(tituloPausa).center().padBottom(15).row();
        
        // Información de partida: se actualiza dinámicamente durante el render
        // Color cian para diferenciación visual y wrap para adaptación de texto
        Label.LabelStyle estiloInfo = new Label.LabelStyle(font, Color.CYAN);
        lblInfoPausa = new Label("", estiloInfo);
        lblInfoPausa.setWrap(true);
        lblInfoPausa.setAlignment(Align.center);
        tPausa.add(lblInfoPausa).center().width(380).padBottom(20).row();

        // ===== BOTONES DE ACCIÓN =====
        // Botón primario: Reanudar (verde, con indicador visual ">")
        TextButton btnReanudar = new TextButton("> Reanudar", skinPausa.get("reanudar", TextButton.TextButtonStyle.class));
        tPausa.add(btnReanudar).center().padBottom(10).row();
        btnReanudar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.JUEGO;
            }
        });

        // Botón secundario: Reiniciar partida
        TextButton btnReiniciar = new TextButton("Reiniciar Partida", skinPausa);
        tPausa.add(btnReiniciar).center().padBottom(10).row();
        btnReiniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inicializarJuego();
                estado = EstadoPantalla.JUEGO;
            }
        });

        // Botón de navegación: Opciones
        TextButton btnOpciones = new TextButton("Opciones", skinPausa);
        tPausa.add(btnOpciones).center().padBottom(10).row();
        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.OPCIONES;
                Gdx.input.setInputProcessor(escOpciones);
            }
        });

        // Botón de navegación: Menú Principal
        TextButton btnMenu = new TextButton("Menú Principal", skinPausa);
        tPausa.add(btnMenu).center().padBottom(10).row();
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }

    // ============================================================
    // MENÚ OPCIONES - DISEÑO PROFESIONAL
    // ============================================================
    /**
     * Construye el menú de opciones con diseño limpio y centrado.
     * 
     * Características:
     * - Sistema de configuración centralizado usando GestorAudio
     * - Slider interactivo con feedback visual inmediato
     * - Layout vertical centrado para fácil navegación
     * - Control de volumen con label dinámico que muestra porcentaje
     * - Botones de acción secundaria (pantalla completa, volver)
     * - Espaciado consistente para jerarquía visual clara
     * 
     * @implNote El slider actualiza el volumen en tiempo real durante el arrastre
     */
    private void crearMenuOpciones() {
        // Inicialización del viewport y stage
        vpOpc = new FitViewport(800, 480);
        escOpciones = new Stage(vpOpc, batch);
        skinOpc = new Skin();
        skinOpc.add("fuente", font);

        // ===== SISTEMA DE ESTILOS =====
        // Estilo base para botones
        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinOpc.add("default", estiloBtn);

        // Estilo para labels de texto
        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        skinOpc.add("lbl", estiloLbl);

        // ===== LAYOUT PRINCIPAL =====
        Table tOpc = new Table();
        tOpc.setFillParent(true);
        tOpc.center();
        // Configuración de elementos: tamaños consistentes y centrados
        tOpc.defaults().pad(12).minWidth(260).prefWidth(300).maxWidth(340).height(50).center();
        escOpciones.addActor(tOpc);

        // ===== ELEMENTOS VISUALES =====
        // Título del menú: centrado y con espaciado adecuado
        Label tituloOpc = new Label("Opciones", estiloLbl);
        tOpc.add(tituloOpc).center().padBottom(30).row();

        // Label de volumen: muestra porcentaje actual, se actualiza dinámicamente
        lblVolumen = new Label("Volumen: " + (int)(GestorAudio.getInstance().getVolumenMaestro() * 100) + "%", estiloLbl);
        tOpc.add(lblVolumen).center().padBottom(12).row();

        // ===== CONTROL DE VOLUMEN =====
        // Estilo del slider: fondo y knob personalizados
        // Usa textura blanca para crear slider minimalista y profesional
        // Reutiliza texSlider en lugar de crear nuevas texturas
        Slider.SliderStyle estiloSlider = new Slider.SliderStyle();
        estiloSlider.background = new TextureRegionDrawable(new TextureRegion(texSlider));
        estiloSlider.knob = new TextureRegionDrawable(new TextureRegion(texSlider));
        estiloSlider.background.setMinHeight(6); // Altura del track del slider
        estiloSlider.knob.setMinWidth(18); // Ancho del knob para fácil interacción
        estiloSlider.knob.setMinHeight(28); // Altura del knob
        skinOpc.add("default-horizontal", estiloSlider);

        // Slider de volumen: rango 0.0-1.0, incremento 0.01 para precisión
        // Color gris claro para visibilidad sin ser intrusivo
        final Slider sliderVolumen = new Slider(0f, 1f, 0.01f, false, skinOpc);
        sliderVolumen.setValue(GestorAudio.getInstance().getVolumenMaestro());
        sliderVolumen.setColor(Color.LIGHT_GRAY);
        tOpc.add(sliderVolumen).center().width(320).height(45).padBottom(25).row();

        // Listener del slider: actualiza volumen en tiempo real durante el arrastre
        // Solo actualiza cuando el usuario está arrastrando para evitar actualizaciones innecesarias
        sliderVolumen.addListener(event -> {
            if (!sliderVolumen.isDragging()) return false;
            float nuevoVolumen = sliderVolumen.getValue();
            GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
            lblVolumen.setText("Volumen: " + (int)(nuevoVolumen * 100) + "%");
            aplicarVolumen();
            return true;
        });

        // ===== BOTONES DE ACCIÓN =====
        // Botón de pantalla completa: toggle de modo pantalla completa
        // Muestra atajo de teclado (F11) para mejor UX
        TextButton btnPantalla = new TextButton("Pantalla completa (F11)", skinOpc);
        tOpc.add(btnPantalla).center().padBottom(20).row();
        btnPantalla.addListener(new ClickListener() {
            @Override 
            public void clicked(InputEvent event, float x, float y) {
                togglePantallaCompleta();
            }
        });

        // Botón de navegación: volver al menú principal
        TextButton btnVolver = new TextButton("Volver al Menú", skinOpc);
        tOpc.add(btnVolver).center().row();
        btnVolver.addListener(new ClickListener() {
            @Override 
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }

    // ============================================================
    // MENÚ GAME OVER - DISEÑO PROFESIONAL
    // ============================================================
    /**
     * Construye la pantalla de Game Over con diseño impactante y profesional.
     * 
     * Características:
     * - Muestra estadísticas finales de la partida
     * - Compara puntaje actual con mejor puntaje histórico
     * - Permite reiniciar el juego o volver al menú
     * - Título en rojo, puntaje en blanco, mejor puntaje en dorado
     * - Layout centrado para máxima legibilidad
     * 
     * Nota: Los labels se actualizan dinámicamente en renderGameOver()
     */
 private void crearMenuGameOver() {
        // Inicialización del viewport y stage
     vpGameOver = new FitViewport(800, 480);
     escGameOver = new Stage(vpGameOver, batch);
     skinGameOver = new Skin();
     skinGameOver.add("fuente", font);

        // ===== SISTEMA DE ESTILOS =====
        // Estilo base para botones
     TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
     estiloBtn.font = font;
     estiloBtn.fontColor = Color.WHITE;
     skinGameOver.add("default", estiloBtn);

        // Estilos para labels: título impactante y texto informativo
     Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.RED);
     Label.LabelStyle estiloLblTexto = new Label.LabelStyle(font, Color.WHITE);

        // ===== LAYOUT PRINCIPAL =====
     Table tOver = new Table();
     tOver.setFillParent(true);
     tOver.center();              
        // Configuración de elementos: tamaños consistentes y centrados
        tOver.defaults().pad(12).minWidth(260).prefWidth(300).maxWidth(340).height(50).center();
     escGameOver.addActor(tOver);

        // ===== ELEMENTOS VISUALES =====
        // Título principal: "GAME OVER" en rojo para máximo impacto
        Label lblGameOver = new Label("GAME OVER", estiloLblTitulo);
        lblGameOver.setAlignment(Align.center);
        tOver.add(lblGameOver).center().padBottom(35).row();
        
        // Separador visual para jerarquía
        // Usa guiones simples en lugar de caracteres especiales
        Label separador = new Label("----------------------------------------", estiloLblTexto);
        separador.setColor(0.5f, 0.5f, 0.5f, 0.6f);
        separador.setAlignment(Align.center);
        tOver.add(separador).center().padBottom(25).row();

        // ===== ESTADÍSTICAS DE PARTIDA =====
        // Puntaje obtenido: se actualiza dinámicamente al finalizar
     lblPuntajeFinal = new Label("", estiloLblTexto);
        lblPuntajeFinal.setAlignment(Align.center);
        tOver.add(lblPuntajeFinal).center().padBottom(12).row();

        // Dificultad jugada: contexto de la partida
        lblDificultadFinal = new Label("", estiloLblTexto);
        lblDificultadFinal.setAlignment(Align.center);
        tOver.add(lblDificultadFinal).center().padBottom(12).row();
        
        // Mejor puntaje: destacado en dorado para mostrar logro
        Label.LabelStyle estiloLblMejor = new Label.LabelStyle(font, Color.GOLD);
        lblMejorPuntaje = new Label("Mejor Puntaje: 0", estiloLblMejor);
        lblMejorPuntaje.setAlignment(Align.center);
        tOver.add(lblMejorPuntaje).center().padBottom(35).row();

        // ===== BOTONES DE ACCIÓN =====
        // Botón primario: Volver a jugar (acción más común)
        TextButton btnReintentar = new TextButton("Volver a jugar", skinGameOver);
        tOver.add(btnReintentar).center().padBottom(12).row();
     btnReintentar.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
                // Reinicia el juego manteniendo la dificultad actual
                inicializarJuego();
             estado = EstadoPantalla.JUEGO;
         }
     });

        // Botón de navegación: Menú principal
        TextButton btnMenu = new TextButton("Menú principal", skinGameOver);
        tOver.add(btnMenu).center().padBottom(12).row();
     btnMenu.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
             estado = EstadoPantalla.MENU;
             Gdx.input.setInputProcessor(escMenu);
         }
     });

        // Botón de salida: Salir del juego
        TextButton btnSalir = new TextButton("Salir", skinGameOver);
        tOver.add(btnSalir).center().row();
     btnSalir.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
             Gdx.app.exit();
         }
     });
 }


    // ============================================================
    // RENDER LOOP
    // ============================================================
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) togglePantallaCompleta();

        switch (estado) {
            case MENU: renderMenu(); break;
            case SELECCION_DIFICULTAD: renderSeleccionDificultad(); break;
            case OPCIONES: renderOpciones(); break;
            case TUTORIAL: renderTutorial(); break;
            case JUEGO: renderJuego(); break;
            case PAUSA: renderPausa(); break;
            case GAME_OVER: renderGameOver(); break;
        }
    }

    // ============================================================
    // RENDER MENÚS
    // ============================================================
    /**
     * Renderiza la pantalla del menú principal con diseño limpio y elegante
     */
    private void renderMenu() {
        // Actualizar tiempo para animación sutil del subtítulo
        tiempoAnimacion += Gdx.graphics.getDeltaTime();
        
    	vpMenu.apply(true); 

        // Dibujar fondo del menú principal
        batch.setProjectionMatrix(escMenu.getCamera().combined);
        batch.begin();
        if (fondoMenuPrincipal != null) {
            batch.draw(fondoMenuPrincipal, 0, 0, vpMenu.getWorldWidth(), vpMenu.getWorldHeight());
        }
        batch.end();

        escMenu.act(Gdx.graphics.getDeltaTime());
        escMenu.draw();

        batch.setProjectionMatrix(escMenu.getCamera().combined);
        batch.begin(); 
        
        // ===== TÍTULO CON CONTRASTE ELEGANTE =====
        // Título sin animación de escala, con sombra simple y elegante
        String tituloTexto = "JUEGO LLUVIA";
        GlyphLayout layoutTitulo = new GlyphLayout(font, tituloTexto);
        float anchoTitulo = layoutTitulo.width;
        float xTitulo = (vpMenu.getWorldWidth() - anchoTitulo) / 2f;
        float yTitulo = 380;
        
        // Sombra simple y elegante (una sola capa)
        font.setColor(new Color(0, 0, 0, 0.8f));
        font.draw(batch, tituloTexto, xTitulo + 3, yTitulo - 3);
        
        // Título principal con color brillante
        font.setColor(new Color(0.4f, 0.9f, 1f, 1f)); // Cyan brillante
        font.draw(batch, tituloTexto, xTitulo, yTitulo);
        
        // ===== SUBTÍTULO CON CONTRASTE ELEGANTE =====
        // Subtítulo con animación sutil de fade
        float alpha = 0.85f + 0.15f * (float)Math.sin(tiempoAnimacion * 2f);
        String subtitulo = "Atrapa gotas azules, evita las rojas";
        GlyphLayout layoutSubtitulo = new GlyphLayout(font, subtitulo);
        float xSubtitulo = (vpMenu.getWorldWidth() - layoutSubtitulo.width) / 2f;
        
        // Sombra del subtítulo
        font.setColor(new Color(0, 0, 0, 0.7f));
        font.draw(batch, subtitulo, xSubtitulo + 2, 338);
        
        // Subtítulo principal
        font.setColor(new Color(1f, 1f, 0.95f, alpha)); // Blanco ligeramente amarillento
        font.draw(batch, subtitulo, xSubtitulo, 340);
        
        // ===== ATAJOS CON CONTRASTE ELEGANTE =====
        // Atajos con sombra simple
        String texto = "Atajos: [J] Jugar  [T] Tutorial  [O] Opciones  [ESC] Salir";
        GlyphLayout layout = new GlyphLayout(font, texto);
        float xTexto = (vpMenu.getWorldWidth() - layout.width) / 2f;
        
        // Sombra de atajos
        font.setColor(new Color(0, 0, 0, 0.6f));
        font.draw(batch, texto, xTexto + 2, 472);
        
        // Atajos principales
        font.setColor(new Color(0.95f, 0.95f, 0.95f, 1f)); // Blanco casi puro
        font.draw(batch, texto, xTexto, 470);
        
        batch.end();

        // Atajos de teclado
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            estado = EstadoPantalla.SELECCION_DIFICULTAD;
            Gdx.input.setInputProcessor(escDificultad);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            if (tutorial == null) tutorial = new Tutorial();
            tutorial.reiniciar();
            estado = EstadoPantalla.TUTORIAL;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            estado = EstadoPantalla.OPCIONES;
            Gdx.input.setInputProcessor(escOpciones);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }


    /**
     * Renderiza la pantalla de selección de dificultad
     * Usa el mismo fondo que el menú principal
     */
    private void renderSeleccionDificultad() {
        vpDificultad.apply(true);
        
        // Dibujar fondo (mismo que menú principal)
        batch.setProjectionMatrix(escDificultad.getCamera().combined);
        batch.begin();
        if (fondoMenuPrincipal != null) {
            batch.draw(fondoMenuPrincipal, 0, 0, vpDificultad.getWorldWidth(), vpDificultad.getWorldHeight());
        }
        batch.end();
        
        escDificultad.act(Gdx.graphics.getDeltaTime());
        escDificultad.draw();

        // Atajos de teclado
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estado = EstadoPantalla.MENU;
            Gdx.input.setInputProcessor(escMenu);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            dificultadActual = new DificultadFacil();
            iniciarJuego();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            dificultadActual = new DificultadMedio();
            iniciarJuego();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            dificultadActual = new DificultadDificil();
            iniciarJuego();
        }
    }

    /**
     * Renderiza el menú de opciones con su fondo específico
     */
    private void renderOpciones() {
        vpOpc.apply(true);
        
        // Dibujar fondo del menú de opciones
        batch.setProjectionMatrix(escOpciones.getCamera().combined);
        batch.begin();
        if (fondoOpciones != null) {
            batch.draw(fondoOpciones, 0, 0, vpOpc.getWorldWidth(), vpOpc.getWorldHeight());
        }
        batch.end();
        
        escOpciones.act(Gdx.graphics.getDeltaTime());
        escOpciones.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estado = EstadoPantalla.MENU;
            Gdx.input.setInputProcessor(escMenu);
        }
    }

    private void renderTutorial() {
        float dt = Gdx.graphics.getDeltaTime();
        if (tutorial == null) tutorial = new Tutorial();
        tutorial.actualizar(dt, camera, batch, font);
        if (tutorial.solicitaVolverMenu()) {
            estado = EstadoPantalla.MENU;
            Gdx.input.setInputProcessor(escMenu);
        } else if (tutorial.solicitaJugar()) {
            estado = EstadoPantalla.JUEGO;
        }
    }

    /**
     * Renderiza el menú de pausa con diseño centrado.
     * 
     * Orden de renderizado:
     * 1. Dibuja el fondo del menú de pausa
     * 2. Aplica overlay oscuro semitransparente para legibilidad
     * 3. Renderiza el menú centrado
     * 4. Muestra instrucciones en la parte inferior
     */
    private void renderPausa() {
        // ===== CAPA 1: FONDO DEL MENÚ DE PAUSA =====
        // Dibuja el fondo específico del menú de pausa
        vpPausa.apply(true);
        batch.setProjectionMatrix(vpPausa.getCamera().combined);
        batch.begin();
        if (fondoPausa != null) {
            batch.draw(fondoPausa, 0, 0, vpPausa.getWorldWidth(), vpPausa.getWorldHeight());
        }
        batch.end();
        
        // ===== CAPA 2: OVERLAY OSCURO PARA LEGIBILIDAD =====
        // Overlay semitransparente para mejorar contraste del texto sobre el fondo
        // Reutiliza texBlanco en lugar de crear una nueva textura cada frame
        batch.begin();
        batch.setColor(0, 0, 0, 0.5f); // 50% opacidad para mantener visibilidad del fondo
        batch.draw(texBlanco, 0, 0, vpPausa.getWorldWidth(), vpPausa.getWorldHeight());
        batch.setColor(1, 1, 1, 1); // Restaurar color
        batch.end();
        
        // ===== ACTUALIZACIÓN DE UI =====
        // Actualizar información de partida antes de renderizar UI
        if (lblInfoPausa != null) {
            String info = "Puntaje: " + tarro.getPuntos() + " | Vidas: " + tarro.getVidas() + " | " + dificultadActual.getNombre();
            lblInfoPausa.setText(info);
        }
        
        // ===== RENDERIZADO DE MENÚ =====
        // Aplicar viewport del menú y actualizar stage
        // El menú ya está perfectamente centrado gracias al Table con setFillParent(true) y center()
        vpPausa.apply(true);
        escPausa.act(Gdx.graphics.getDeltaTime());
        escPausa.draw();
        
        // ===== INSTRUCCIONES DE TECLADO =====
        // Mostrar instrucciones centradas en la parte inferior
        // Usar un margen más grande para evitar sobreposición con el menú
        batch.setProjectionMatrix(vpPausa.getCamera().combined);
        batch.begin();
        font.setColor(Color.LIGHT_GRAY);
        String textoPausa = "Presiona [P] o [ESC] para reanudar";
        GlyphLayout layoutPausa = new GlyphLayout(font, textoPausa);
        float xPausa = (vpPausa.getWorldWidth() - layoutPausa.width) / 2f;
        // Aumentar el margen inferior para evitar sobreposición
        float yPausa = 30f;
        font.draw(batch, textoPausa, xPausa, yPausa);
        batch.end();
        
        // Atajos de teclado para reanudar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            estado = EstadoPantalla.JUEGO;
        }
    }

    /**
     * Renderiza la pantalla de juego principal
     * Muestra puntaje, vidas, dificultad y actualiza todas las entidades
     * Usa fondos específicos según la dificultad seleccionada
     */
    private void renderJuego() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Determinar y dibujar fondo según dificultad
        batch.begin();
        Texture fondoActual = null;
        if (dificultadActual instanceof DificultadFacil) {
            fondoActual = fondoFacil;
        } else if (dificultadActual instanceof DificultadDificil) {
            fondoActual = fondoDificil;
        } else {
            fondoActual = fondoMedio;
        }
        
        // Dibujar fondo si existe, si no usar color sólido como fallback
        if (fondoActual != null) {
            batch.draw(fondoActual, 0, 0, 800, 480);
        } else {
            // Fallback a colores sólidos si no hay fondo
            batch.end();
            if (dificultadActual instanceof DificultadFacil) {
                ScreenUtils.clear(0, 0.1f, 0.2f, 1);
            } else if (dificultadActual instanceof DificultadDificil) {
                ScreenUtils.clear(0.2f, 0, 0, 1);
            } else {
                ScreenUtils.clear(0, 0, 0.2f, 1);
            }
            batch.begin();
        }
        // Información del juego con mejor formato
        font.setColor(Color.WHITE);
        font.draw(batch, "Puntaje: " + tarro.getPuntos(), 10, 475);
        
        // Vidas con color según cantidad
        if (tarro.getVidas() > 2) {
            font.setColor(Color.GREEN);
        } else if (tarro.getVidas() > 1) {
            font.setColor(Color.YELLOW);
        } else {
            font.setColor(Color.RED);
        }
        font.draw(batch, "Vidas: " + tarro.getVidas(), 720, 475);
        
        // Mostrar dificultad actual
        font.setColor(Color.CYAN);
        font.draw(batch, "Dificultad: " + dificultadActual.getNombre(), 10, 455);
        
        // Actualizar y dibujar entidades
        tarro.actualizarMovimiento();
        lluvia.actualizarMovimiento(tarro);
        tarro.dibujar(batch);
        lluvia.actualizarDibujoLluvia(batch);
        batch.end();

        // Verificar fin del juego
        if (tarro.getVidas() <= 0) {
            // Actualizar mejor puntaje
            if (tarro.getPuntos() > mejorPuntaje) {
                mejorPuntaje = tarro.getPuntos();
            }
            lblPuntajeFinal.setText("Puntaje Obtenido: " + tarro.getPuntos());
            lblDificultadFinal.setText("Dificultad: " + dificultadActual.getNombre());
            lblMejorPuntaje.setText("Mejor Puntaje: " + mejorPuntaje);
            estado = EstadoPantalla.GAME_OVER;
            Gdx.input.setInputProcessor(escGameOver);
        }

        // Pausa con ESC o P (solo cuando está jugando)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            estado = EstadoPantalla.PAUSA;
            Gdx.input.setInputProcessor(escPausa);
        }
    }
    
    /**
     * Renderiza la pantalla de Game Over con su fondo específico.
     * 
     * Dibuja el fondo de Game Over y renderiza el menú centrado.
     * Los labels se actualizan en renderJuego() antes de cambiar a este estado.
     */
    private void renderGameOver() {
        vpGameOver.apply(true);
        
        // Dibujar fondo de Game Over
        batch.setProjectionMatrix(escGameOver.getCamera().combined);
        batch.begin();
        if (fondoGameOver != null) {
            batch.draw(fondoGameOver, 0, 0, vpGameOver.getWorldWidth(), vpGameOver.getWorldHeight());
        }
        batch.end();
        
        escGameOver.act(Gdx.graphics.getDeltaTime());
        escGameOver.draw();
    }

    // ============================================================
    // AJUSTE DE TAMAÑO Y LIBERACIÓN
    // ============================================================
    @Override
    public void resize(int width, int height) {
        if (vpMenu != null) vpMenu.update(width, height, true);
        if (vpDificultad != null) vpDificultad.update(width, height, true);
        if (vpOpc != null) vpOpc.update(width, height, true);
        if (vpPausa != null) vpPausa.update(width, height, true);
        if (vpGameOver != null) vpGameOver.update(width, height, true);
        if (camera != null) { camera.setToOrtho(false, 800, 480); camera.update(); }
    }

    @Override
    public void dispose() {
        // Liberar recursos de entidades del juego
        if (tarro != null) tarro.destruir();
        if (lluvia != null) lluvia.destruir();
        
        // Liberar recursos gráficos
        batch.dispose();
        font.dispose();
        
        // Liberar recursos de menús
        if (escMenu != null) escMenu.dispose();
        if (skinMenu != null) skinMenu.dispose();
        if (escDificultad != null) escDificultad.dispose();
        if (skinDificultad != null) skinDificultad.dispose();
        if (escOpciones != null) escOpciones.dispose();
        if (skinOpc != null) skinOpc.dispose();
        if (escPausa != null) escPausa.dispose();
        if (skinPausa != null) skinPausa.dispose();
        if (escGameOver != null) escGameOver.dispose();
        if (skinGameOver != null) skinGameOver.dispose();
        if (tutorial != null) tutorial.dispose();
        
        // Liberar texturas de fondos
        if (fondoMenuPrincipal != null) fondoMenuPrincipal.dispose();
        if (fondoOpciones != null) fondoOpciones.dispose();
        if (fondoPausa != null) fondoPausa.dispose();
        if (fondoGameOver != null) fondoGameOver.dispose();
        if (fondoFacil != null) fondoFacil.dispose();
        if (fondoMedio != null) fondoMedio.dispose();
        if (fondoDificil != null) fondoDificil.dispose();
        
        // Liberar texturas reutilizables
        if (texBlanco != null) texBlanco.dispose();
        if (texSlider != null) texSlider.dispose();
        if (texBucket != null) texBucket.dispose();
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private void aplicarVolumen() {
        // Usa GestorAudio (Singleton) para aplicar volumen a todos los componentes
        if (lluvia != null) lluvia.setVolumen(GestorAudio.getInstance().getVolumenMaestro());
        if (tarro != null) tarro.setVolumen(GestorAudio.getInstance().getVolumenMaestro());
    }

    private void togglePantallaCompleta() {
        if (Gdx.graphics.isFullscreen())
            Gdx.graphics.setWindowedMode(800, 480);
        else
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }
}