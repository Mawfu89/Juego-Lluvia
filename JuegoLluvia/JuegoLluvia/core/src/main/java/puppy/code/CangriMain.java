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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Clase principal del juego "Lluvia".
 * Controla los menús, opciones, tutorial, juego y pantalla de Game Over.
 */
public class CangriMain extends ApplicationAdapter {

    private enum EstadoPantalla { MENU, OPCIONES, TUTORIAL, JUEGO, GAME_OVER }
    private EstadoPantalla estado = EstadoPantalla.MENU;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Lluvia lluvia;

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

    private Tutorial tutorial;
    private float volumenMaestro = 0.8f;

    @Override
    public void create() {
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        tarro = new Tarro(new Texture(Gdx.files.internal("bucket.png")), hurtSound);
        Texture gota = new Texture(Gdx.files.internal("drop.png"));
        Texture gotaMala = new Texture(Gdx.files.internal("dropBad.png"));
        Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        lluvia = new Lluvia(gota, gotaMala, dropSound, rainMusic);
        tarro.crear();
        lluvia.crear();
        aplicarVolumen();

        crearMenuPrincipal();
        crearMenuOpciones();
        crearMenuGameOver();
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

        TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
        estiloBtn.font = font;
        estiloBtn.fontColor = Color.WHITE;
        skinMenu.add("default", estiloBtn);

        Label.LabelStyle estiloLbl = new Label.LabelStyle(font, Color.WHITE);
        skinMenu.add("lbl", estiloLbl);

        Table tMenu = new Table();
        tMenu.setFillParent(true);
        tMenu.center();
        tMenu.defaults().pad(10).width(260).height(52).center();
        tMenu.padTop(40f);
        escMenu.addActor(tMenu);

        Label titulo = new Label("Juego Lluvia", estiloLbl);
        tMenu.add(titulo).padLeft(160f).center().padBottom(24).row();

        TextButton btnJugar = new TextButton("Jugar", skinMenu);
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
                estado = EstadoPantalla.JUEGO; // 🔹 No quitamos el InputProcessor
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
    // MENÚ OPCIONES
    // ============================================================
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
        tOpc.defaults().pad(10).width(260).height(52).center();
        escOpciones.addActor(tOpc);

        Label tituloOpc = new Label("Opciones", estiloLbl);
        tOpc.add(tituloOpc).padBottom(24).row();

        lblVolumen = new Label("Volumen: " + (int)(volumenMaestro * 100) + "%", estiloLbl);
        tOpc.add(lblVolumen).padBottom(10).row();

        Slider.SliderStyle estiloSlider = new Slider.SliderStyle();
        estiloSlider.background = new TextureRegionDrawable(new TextureRegion(new Texture("white.png")));
        estiloSlider.knob = new TextureRegionDrawable(new TextureRegion(new Texture("white.png")));
        estiloSlider.background.setMinHeight(5);
        estiloSlider.knob.setMinWidth(15);
        estiloSlider.knob.setMinHeight(25);
        skinOpc.add("default-horizontal", estiloSlider);

        final Slider sliderVolumen = new Slider(0f, 1f, 0.01f, false, skinOpc);
        sliderVolumen.setValue(volumenMaestro);
        sliderVolumen.setColor(Color.LIGHT_GRAY);
        tOpc.add(sliderVolumen).width(300).height(40).padBottom(20).row();

        sliderVolumen.addListener(event -> {
            if (!sliderVolumen.isDragging()) return false;
            volumenMaestro = sliderVolumen.getValue();
            lblVolumen.setText("Volumen: " + (int)(volumenMaestro * 100) + "%");
            aplicarVolumen();
            return true;
        });

        TextButton btnPantalla = new TextButton("Pantalla completa (F11)", skinOpc);
        tOpc.add(btnPantalla).padBottom(20).row();

        TextButton btnVolver = new TextButton("Volver al Menú", skinOpc);
        tOpc.add(btnVolver).row();

        btnPantalla.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                togglePantallaCompleta();
            }
        });
        btnVolver.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                estado = EstadoPantalla.MENU;
                Gdx.input.setInputProcessor(escMenu);
            }
        });
    }

    // ============================================================
    // GAME OVER
    // ============================================================

 private void crearMenuGameOver() {
     vpGameOver = new FitViewport(800, 480);
     escGameOver = new Stage(vpGameOver, batch);
     skinGameOver = new Skin();
     skinGameOver.add("fuente", font);

     // === Estilos ===
     TextButton.TextButtonStyle estiloBtn = new TextButton.TextButtonStyle();
     estiloBtn.font = font;
     estiloBtn.fontColor = Color.WHITE;
     skinGameOver.add("default", estiloBtn);

     Label.LabelStyle estiloLblTitulo = new Label.LabelStyle(font, Color.RED);
     Label.LabelStyle estiloLblTexto = new Label.LabelStyle(font, Color.WHITE);

     // === Tabla principal ===
     Table tOver = new Table();
     tOver.setFillParent(true);
     tOver.center();                                
     tOver.defaults().pad(12).width(260).height(52).center();
     escGameOver.addActor(tOver);

     // === Título ===
     Label lblGameOver = new Label("¡GAME OVER!", estiloLblTitulo);
     tOver.add(lblGameOver).padLeft(170f).center().padBottom(30).row();

     // === Puntaje final ===
     lblPuntajeFinal = new Label("", estiloLblTexto);
     tOver.add(lblPuntajeFinal).padLeft(70f).center().padBottom(50).row();

     // === Botones ===
     TextButton btnReintentar = new TextButton("Volver a jugar", skinGameOver);
     TextButton btnMenu = new TextButton("Menú principal", skinGameOver);
     TextButton btnSalir = new TextButton("Salir", skinGameOver);

     // 🔹 Espaciado visual consistente
     tOver.add(btnReintentar).center().padBottom(15).row();
     tOver.add(btnMenu).center().padBottom(15).row();
     tOver.add(btnSalir).center().row();

     // 🔹 Validación de layout
     tOver.pack();
     tOver.validate();

     // === Listeners ===
     btnReintentar.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
             tarro = new Tarro(new Texture(Gdx.files.internal("bucket.png")),
                               Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")));
             tarro.crear();
             lluvia.crear();
             aplicarVolumen();
             estado = EstadoPantalla.JUEGO;
         }
     });

     btnMenu.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
             estado = EstadoPantalla.MENU;
             Gdx.input.setInputProcessor(escMenu);
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
            case OPCIONES: renderOpciones(); break;
            case TUTORIAL: renderTutorial(); break;
            case JUEGO: renderJuego(); break;
            case GAME_OVER: renderGameOver(); break;
        }
    }

    // ============================================================
    // RENDER MENÚS
    // ============================================================
    private void renderMenu() {
    	vpMenu.apply(true); 

        escMenu.act(Gdx.graphics.getDeltaTime());
        escMenu.draw();

        batch.setProjectionMatrix(escMenu.getCamera().combined);
        batch.begin(); 
        String texto = "Atajos: [J] Jugar   [T] Tutorial   [O] Opciones   [ESC] Salir";
        GlyphLayout layout = new GlyphLayout(font, texto);

        font.draw(batch, texto, (vpMenu.getWorldWidth() - layout.width) / 2f, 470);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            estado = EstadoPantalla.JUEGO;
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


    private void renderOpciones() {
        vpOpc.apply();
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

    private void renderJuego() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Puntaje: " + tarro.getPuntos(), 5, 475);
        font.draw(batch, "Vidas: " + tarro.getVidas(), 720, 475);
        tarro.actualizarMovimiento();
        lluvia.actualizarMovimiento(tarro);
        tarro.dibujar(batch);
        lluvia.actualizarDibujoLluvia(batch);
        batch.end();

        if (tarro.getVidas() <= 0) {
            lblPuntajeFinal.setText("Puntaje obtenido: " + tarro.getPuntos());
            estado = EstadoPantalla.GAME_OVER;
            Gdx.input.setInputProcessor(escGameOver);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estado = EstadoPantalla.MENU;
            Gdx.input.setInputProcessor(escMenu);
        }
    }
    
    private void renderGameOver() {
        vpGameOver.apply(true);
        escGameOver.act(Gdx.graphics.getDeltaTime());
        escGameOver.draw();
    }

    // ============================================================
    // AJUSTE DE TAMAÑO Y LIBERACIÓN
    // ============================================================
    @Override
    public void resize(int width, int height) {
        if (vpMenu != null) vpMenu.update(width, height, true);
        if (vpOpc != null) vpOpc.update(width, height, true);
        if (vpGameOver != null) vpGameOver.update(width, height, true);
        if (camera != null) { camera.setToOrtho(false, 800, 480); camera.update(); }
    }

    @Override
    public void dispose() {
        tarro.destruir();
        lluvia.destruir();
        batch.dispose();
        font.dispose();
        if (escMenu != null) escMenu.dispose();
        if (skinMenu != null) skinMenu.dispose();
        if (escOpciones != null) escOpciones.dispose();
        if (skinOpc != null) skinOpc.dispose();
        if (escGameOver != null) escGameOver.dispose();
        if (skinGameOver != null) skinGameOver.dispose();
        if (tutorial != null) tutorial.dispose();
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private void aplicarVolumen() {
        if (lluvia != null) lluvia.setVolumen(volumenMaestro);
        if (tarro != null) tarro.setVolumen(volumenMaestro);
    }

    private void togglePantallaCompleta() {
        if (Gdx.graphics.isFullscreen())
            Gdx.graphics.setWindowedMode(800, 480);
        else
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }
}