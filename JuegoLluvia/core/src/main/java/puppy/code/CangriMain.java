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
 * Clase principal del juego Lluvia.
 * 
 * Gestiona todos los estados del juego: menus, tutorial, juego en curso, pausa y game over.
 * Se encarga de cargar recursos, crear interfaces de usuario y coordinar la logica principal.
 */
public class CangriMain extends ApplicationAdapter {

    /**
     * Estados del juego que determinan que pantalla se muestra.
     */
    private enum EstadoPantalla { 
        MENU,
        SELECCION_DIFICULTAD,
        OPCIONES,
        TUTORIAL,
        JUEGO,
        PAUSA,
        GAME_OVER
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
     * Se ejecuta al iniciar el juego. Carga todos los recursos y crea los menus.
     */
    @Override
    public void create() {
        // Configurar componentes graficos basicos
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        // Cargar texturas y sonidos del juego
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        texBucket = new Texture(Gdx.files.internal("bucket.png"));
        tarro = new Tarro(texBucket, hurtSound);
        Texture gota = new Texture(Gdx.files.internal("drop.png"));
        Texture gotaMala = new Texture(Gdx.files.internal("dropBad.png"));
        Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        lluvia = new Lluvia(gota, gotaMala, dropSound, rainMusic);
        
        // Cargar fondos para los diferentes menus y niveles
        fondoMenuPrincipal = new Texture(Gdx.files.internal("Menu.png"));
        fondoOpciones = new Texture(Gdx.files.internal("Opciones.png"));
        fondoPausa = new Texture(Gdx.files.internal("Pausa.png"));
        fondoGameOver = new Texture(Gdx.files.internal("GameOver.png"));
        fondoFacil = new Texture(Gdx.files.internal("Facil.png"));
        fondoMedio = new Texture(Gdx.files.internal("Medio.png"));
        fondoDificil = new Texture(Gdx.files.internal("Dificil.png"));
        
        // Texturas que se reutilizan en varios lugares
        texBlanco = new Texture(Gdx.files.internal("white.png"));
        texSlider = new Texture(Gdx.files.internal("white.png"));
        
        // Configurar volumen inicial
        GestorAudio.getInstance().setVolumenMaestro(0.8f);
        
        // Establecer dificultad por defecto
        dificultadActual = new DificultadMedio();
        lluvia.setNivelDificultad(dificultadActual);
        
        // Preparar el juego para empezar
        inicializarJuego();

        // Construir todas las interfaces de usuario
        crearMenuPrincipal();
        crearMenuSeleccionDificultad();
        crearMenuOpciones();
        crearMenuPausa();
        crearMenuGameOver();
    }
    
    /**
     * Reinicia el juego con la dificultad actual seleccionada.
     * Se llama al empezar una nueva partida o al reiniciar.
     */
    private void inicializarJuego() {
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        tarro = new Tarro(texBucket, hurtSound);
        tarro.crear();
        tarro.setVidasIniciales(dificultadActual.getVidasIniciales());
        
        lluvia.setNivelDificultad(dificultadActual);
        lluvia.crear();
        aplicarVolumen();
    }

    /**
     * Crea el menu principal con sus botones y estilos.
     */
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

    /**
     * Crea el menu de seleccion de dificultad.
     * Muestra tres opciones: Facil, Medio y Dificil, cada una con su descripcion.
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

        // Opcion Facil
        DificultadFacil facil = new DificultadFacil();
        TextButton btnFacil = new TextButton("FACIL", skinDificultad.get("facil", TextButton.TextButtonStyle.class));
        tDificultad.add(btnFacil).center().padBottom(6).row();
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

        // Opcion Medio
        DificultadMedio medio = new DificultadMedio();
        TextButton btnMedio = new TextButton("MEDIO", skinDificultad.get("medio", TextButton.TextButtonStyle.class));
        tDificultad.add(btnMedio).center().padBottom(6).row();
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

        // Opcion Dificil
        DificultadDificil dificil = new DificultadDificil();
        TextButton btnDificil = new TextButton("DIFICIL", skinDificultad.get("dificil", TextButton.TextButtonStyle.class));
        tDificultad.add(btnDificil).center().padBottom(6).row();
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

        // Boton para volver al menu principal
        TextButton btnVolver = new TextButton("Volver al Menu", skinDificultad);
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
     * Inicia una nueva partida con la dificultad seleccionada.
     */
    private void iniciarJuego() {
        inicializarJuego();
        estado = EstadoPantalla.JUEGO;
    }

    /**
     * Crea el menu de pausa que aparece cuando el jugador pausa el juego.
     * Muestra informacion de la partida y opciones para continuar o salir.
     */
    private void crearMenuPausa() {
        vpPausa = new FitViewport(800, 480);
        escPausa = new Stage(vpPausa, batch);
        skinPausa = new Skin();
        skinPausa.add("fuente", font);

        // Estilos para los botones y labels
        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinPausa.add("default", estiloBtn);
        
        TextButton.TextButtonStyle estiloBtnReanudar = new TextButton.TextButtonStyle();
        estiloBtnReanudar.font = font;
        estiloBtnReanudar.fontColor = Color.GREEN;
        skinPausa.add("reanudar", estiloBtnReanudar);

        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.YELLOW);
        skinPausa.add("lbl", estiloLbl);
        skinPausa.add("titulo", estiloLblTitulo);

        // Layout principal centrado
        Table tPausa = new Table();
        tPausa.setFillParent(true);
        tPausa.center();
        tPausa.defaults().pad(8).minWidth(260).prefWidth(300).maxWidth(340).height(45).center();
        tPausa.padBottom(60);
        escPausa.addActor(tPausa);

        // Titulo del menu
        Label tituloPausa = new Label("PAUSA", estiloLblTitulo);
        tituloPausa.setAlignment(Align.center);
        tPausa.add(tituloPausa).center().padBottom(15).row();
        
        // Label que muestra puntaje, vidas y dificultad (se actualiza en render)
        Label.LabelStyle estiloInfo = new Label.LabelStyle(font, Color.CYAN);
        lblInfoPausa = new Label("", estiloInfo);
        lblInfoPausa.setWrap(true);
        lblInfoPausa.setAlignment(Align.center);
        tPausa.add(lblInfoPausa).center().width(380).padBottom(20).row();

        // Boton para reanudar el juego
        TextButton btnReanudar = new TextButton("> Reanudar", skinPausa.get("reanudar", TextButton.TextButtonStyle.class));
        tPausa.add(btnReanudar).center().padBottom(10).row();
        btnReanudar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.JUEGO;
            }
        });

        // Boton para reiniciar la partida desde cero
        TextButton btnReiniciar = new TextButton("Reiniciar Partida", skinPausa);
        tPausa.add(btnReiniciar).center().padBottom(10).row();
        btnReiniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inicializarJuego();
                estado = EstadoPantalla.JUEGO;
            }
        });

        // Boton para ir a opciones
        TextButton btnOpciones = new TextButton("Opciones", skinPausa);
        tPausa.add(btnOpciones).center().padBottom(10).row();
        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.OPCIONES;
                Gdx.input.setInputProcessor(escOpciones);
            }
        });

        // Boton para volver al menu principal
        TextButton btnMenu = new TextButton("Menu Principal", skinPausa);
        tPausa.add(btnMenu).center().padBottom(10).row();
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }

    /**
     * Crea el menu de opciones donde se puede ajustar el volumen y cambiar a pantalla completa.
     */
    private void crearMenuOpciones() {
        vpOpc = new FitViewport(800, 480);
        escOpciones = new Stage(vpOpc, batch);
        skinOpc = new Skin();
        skinOpc.add("fuente", font);

        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinOpc.add("default", estiloBtn);

        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        skinOpc.add("lbl", estiloLbl);

        Table tOpc = new Table();
        tOpc.setFillParent(true);
        tOpc.center();
        tOpc.defaults().pad(12).minWidth(260).prefWidth(300).maxWidth(340).height(50).center();
        escOpciones.addActor(tOpc);

        Label tituloOpc = new Label("Opciones", estiloLbl);
        tOpc.add(tituloOpc).center().padBottom(30).row();

        // Label que muestra el porcentaje de volumen actual
        lblVolumen = new Label("Volumen: " + (int)(GestorAudio.getInstance().getVolumenMaestro() * 100) + "%", estiloLbl);
        tOpc.add(lblVolumen).center().padBottom(12).row();

        // Slider para ajustar el volumen
        Slider.SliderStyle estiloSlider = new Slider.SliderStyle();
        estiloSlider.background = new TextureRegionDrawable(new TextureRegion(texSlider));
        estiloSlider.knob = new TextureRegionDrawable(new TextureRegion(texSlider));
        estiloSlider.background.setMinHeight(6);
        estiloSlider.knob.setMinWidth(18);
        estiloSlider.knob.setMinHeight(28);
        skinOpc.add("default-horizontal", estiloSlider);

        final Slider sliderVolumen = new Slider(0f, 1f, 0.01f, false, skinOpc);
        sliderVolumen.setValue(GestorAudio.getInstance().getVolumenMaestro());
        sliderVolumen.setColor(Color.LIGHT_GRAY);
        tOpc.add(sliderVolumen).center().width(320).height(45).padBottom(25).row();

        // Actualizar volumen mientras se arrastra el slider
        sliderVolumen.addListener(event -> {
            if (!sliderVolumen.isDragging()) return false;
            float nuevoVolumen = sliderVolumen.getValue();
            GestorAudio.getInstance().setVolumenMaestro(nuevoVolumen);
            lblVolumen.setText("Volumen: " + (int)(nuevoVolumen * 100) + "%");
            aplicarVolumen();
            return true;
        });

        // Boton para cambiar entre pantalla completa y ventana
        TextButton btnPantalla = new TextButton("Pantalla completa (F11)", skinOpc);
        tOpc.add(btnPantalla).center().padBottom(20).row();
        btnPantalla.addListener(new ClickListener() {
            @Override 
            public void clicked(InputEvent event, float x, float y) {
                togglePantallaCompleta();
            }
        });

        // Boton para volver al menu principal
        TextButton btnVolver = new TextButton("Volver al Menu", skinOpc);
        tOpc.add(btnVolver).center().row();
        btnVolver.addListener(new ClickListener() {
            @Override 
            public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }

    /**
     * Crea la pantalla de Game Over que se muestra cuando el jugador pierde todas las vidas.
     * Muestra el puntaje final, la dificultad jugada y el mejor puntaje alcanzado.
     */
 private void crearMenuGameOver() {
     vpGameOver = new FitViewport(800, 480);
     escGameOver = new Stage(vpGameOver, batch);
     skinGameOver = new Skin();
     skinGameOver.add("fuente", font);

     TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
     estiloBtn.font = font;
     estiloBtn.fontColor = Color.WHITE;
     skinGameOver.add("default", estiloBtn);

     Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.RED);
     Label.LabelStyle estiloLblTexto = new Label.LabelStyle(font, Color.WHITE);

     Table tOver = new Table();
     tOver.setFillParent(true);
     tOver.center();
     tOver.defaults().pad(12).minWidth(260).prefWidth(300).maxWidth(340).height(50).center();
     escGameOver.addActor(tOver);

        // Titulo principal
        Label lblGameOver = new Label("GAME OVER", estiloLblTitulo);
        lblGameOver.setAlignment(Align.center);
        tOver.add(lblGameOver).center().padBottom(35).row();
        
        // Separador visual
        Label separador = new Label("----------------------------------------", estiloLblTexto);
        separador.setColor(0.5f, 0.5f, 0.5f, 0.6f);
        separador.setAlignment(Align.center);
        tOver.add(separador).center().padBottom(25).row();

        // Labels que se actualizan cuando termina el juego
     lblPuntajeFinal = new Label("", estiloLblTexto);
        lblPuntajeFinal.setAlignment(Align.center);
        tOver.add(lblPuntajeFinal).center().padBottom(12).row();

        lblDificultadFinal = new Label("", estiloLblTexto);
        lblDificultadFinal.setAlignment(Align.center);
        tOver.add(lblDificultadFinal).center().padBottom(12).row();
        
        // Mejor puntaje destacado en dorado
        Label.LabelStyle estiloLblMejor = new Label.LabelStyle(font, Color.GOLD);
        lblMejorPuntaje = new Label("Mejor Puntaje: 0", estiloLblMejor);
        lblMejorPuntaje.setAlignment(Align.center);
        tOver.add(lblMejorPuntaje).center().padBottom(35).row();

        // Boton para volver a jugar
        TextButton btnReintentar = new TextButton("Volver a jugar", skinGameOver);
        tOver.add(btnReintentar).center().padBottom(12).row();
     btnReintentar.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
                inicializarJuego();
             estado = EstadoPantalla.JUEGO;
         }
     });

        // Boton para volver al menu principal
        TextButton btnMenu = new TextButton("Menu principal", skinGameOver);
        tOver.add(btnMenu).center().padBottom(12).row();
     btnMenu.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
             estado = EstadoPantalla.MENU;
             Gdx.input.setInputProcessor(escMenu);
         }
     });

        // Boton para salir del juego
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
     * Dibuja el menu de pausa con fondo, overlay oscuro y opciones.
     */
    private void renderPausa() {
        // Dibujar fondo del menu de pausa
        vpPausa.apply(true);
        batch.setProjectionMatrix(vpPausa.getCamera().combined);
        batch.begin();
        if (fondoPausa != null) {
            batch.draw(fondoPausa, 0, 0, vpPausa.getWorldWidth(), vpPausa.getWorldHeight());
        }
        batch.end();
        
        // Overlay oscuro semitransparente para mejorar legibilidad del texto
        batch.begin();
        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(texBlanco, 0, 0, vpPausa.getWorldWidth(), vpPausa.getWorldHeight());
        batch.setColor(1, 1, 1, 1);
        batch.end();
        
        // Actualizar informacion de la partida (puntaje, vidas, dificultad)
        if (lblInfoPausa != null) {
            String info = "Puntaje: " + tarro.getPuntos() + " | Vidas: " + tarro.getVidas() + " | " + dificultadActual.getNombre();
            lblInfoPausa.setText(info);
        }
        
        // Dibujar el menu
        vpPausa.apply(true);
        escPausa.act(Gdx.graphics.getDeltaTime());
        escPausa.draw();
        
        // Mostrar instrucciones en la parte inferior
        batch.setProjectionMatrix(vpPausa.getCamera().combined);
        batch.begin();
        font.setColor(Color.LIGHT_GRAY);
        String textoPausa = "Presiona [P] o [ESC] para reanudar";
        GlyphLayout layoutPausa = new GlyphLayout(font, textoPausa);
        float xPausa = (vpPausa.getWorldWidth() - layoutPausa.width) / 2f;
        float yPausa = 30f;
        font.draw(batch, textoPausa, xPausa, yPausa);
        batch.end();
        
        // Detectar teclas para reanudar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            estado = EstadoPantalla.JUEGO;
        }
    }

    /**
     * Dibuja la pantalla principal del juego.
     * Actualiza todas las entidades, muestra informacion y maneja la pausa.
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