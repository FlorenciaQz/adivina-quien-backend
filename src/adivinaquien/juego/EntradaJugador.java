package adivinaquien.juego;

import adivinaquien.algoritmos.CatalogoPreguntas;
import adivinaquien.algoritmos.Pregunta;
import adivinaquien.dominio.Personaje;
import adivinaquien.ui.InterfazUsuario;
import java.util.ArrayList;
import java.util.List;

// Le pide datos al jugador y los devuelve ya validados: parsea, verifica que existan
// y vuelve a preguntar si no. No conoce las reglas del juego ni el personaje secreto
// de nadie.
public final class EntradaJugador {

    private final InterfazUsuario ui;
    private final CatalogoPreguntas catalogo;

    public EntradaJugador(InterfazUsuario ui, CatalogoPreguntas catalogo) {
        this.ui = ui;
        this.catalogo = catalogo;
    }

    // El jugador elige entre preguntar o adivinar. Devuelve true si quiere adivinar,
    // asi el motor no tiene que saber en que posicion de la lista quedo cada opcion.
    public boolean pedirSiAdivina() {
        return ui.pedirOpcion("Es tu turno. ¿Qué querés hacer?",
                List.of("Hacer una pregunta", "Adivinar un personaje")) == 1;
    }

    public Personaje pedirPersonajeAAdivinar(List<Personaje> lista) {
        return ui.pedirPersonaje("¿A quién adivinás? (número): ", lista);
    }

    public Personaje pedirSecreto(List<Personaje> lista) {
        return ui.pedirPersonaje(
                "Elegí tu personaje secreto por su número (no lo vas a poder cambiar): ", lista);
    }

    public Pregunta pedirPregunta() {
        List<Pregunta> todas = catalogo.todas();
        List<String> textos = new ArrayList<String>();
        for (int i = 0; i < todas.size(); i++) {
            textos.add(todas.get(i).texto());
        }
        int opcion = ui.pedirOpcion("Elegí una pregunta:", textos);
        return todas.get(opcion);
    }

    // Devuelve lo que el jugador contestó, sin juzgar si es cierto: esa comparación es
    // una regla del juego y la hace el motor.
    public boolean pedirConfirmacion(String prompt) {
        return ui.pedirOpcion(prompt, List.of("Sí", "No")) == 0;
    }
}