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

    public Personaje pedirPersonajePorId(String prompt, List<Personaje> lista) {
        while (true) {
            String texto = ui.pedirTexto(prompt);
            try {
                int id = Integer.parseInt(texto.trim());
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getId() == id) {
                        return lista.get(i);
                    }
                }
            } catch (NumberFormatException e) {
                // se ignora, se vuelve a pedir
            }
            ui.mostrar("No existe ese personaje. Probá de nuevo.");
        }
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