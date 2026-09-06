package adivinaquien.juego;

import adivinaquien.algoritmos.Pregunta;
import adivinaquien.dominio.Personaje;
import adivinaquien.ui.InterfazUsuario;
import java.util.List;
import java.util.Set;

// Traduce lo que pasa en la partida a los textos que ve el jugador. Es el unico
// lugar donde se arman mensajes: el motor dice que paso, no como se escribe.
// No conoce el marcador ni el tablero: recibe todo lo que necesita por parametro.
public final class PresentadorJuego {

    private final InterfazUsuario ui;

    public PresentadorJuego(InterfazUsuario ui) {
        this.ui = ui;
    }

    public void tableroDeCandidatos(List<Personaje> todos, Set<Integer> idsVigentes) {
        ui.mostrarTablero("Candidatos para el secreto del rival:", todos, idsVigentes);
    }

    public void tableroParaElegirSecreto(List<Personaje> todos, Set<Integer> idsVigentes) {
        ui.mostrarTablero("Elegí tu personaje secreto. Estos son los disponibles:", todos, idsVigentes);
    }

    public void record(String nombreHumano, int victorias) {
        ui.mostrar("¡Hola " + nombreHumano + "! Llevás " + victorias + " "
                + plural(victorias, "victoria registrada", "victorias registradas") + ".");
    }

    public void inicioMaquinaVsMaquina(String nombreM1) {
        ui.mostrar("Comienza Máquina vs Máquina. Arranca " + nombreM1 + ".");
    }

    // En maquina vs maquina se muestra como parte la pregunta antes de hacerla,
    // porque es lo que deja ver la heuristica en accion.
    public void preguntaElegida(String nombre, Pregunta pregunta, int si, int no) {
        ui.mostrar(nombre + " pregunta: \"" + pregunta.texto()
                + "\" (divide en " + si + " sí / " + no + " no).");
    }

    public void resultadoDeFiltro(boolean verdad, int restantes) {
        ui.mostrar("  -> " + (verdad ? "Sí" : "No") + ". Quedan " + restantes + " "
                + plural(restantes, "candidato", "candidatos") + ".");
    }

    public void preguntaConResultado(String nombre, Pregunta pregunta, boolean verdad, int restantes) {
        ui.mostrar(nombre + " preguntó: \"" + pregunta.texto() + "\" -> "
                + (verdad ? "Sí" : "No") + ". Quedan " + restantes + " "
                + plural(restantes, "candidato", "candidatos") + ".");
    }

    // Una sola linea por jugada: antes en maquina vs maquina salian dos.
    public void adivinanza(String nombre, Personaje sospecha, boolean acierto, int candidatosPrevios) {
        String contexto = " (le " + plural(candidatosPrevios, "quedaba", "quedaban") + " "
                + candidatosPrevios + " " + plural(candidatosPrevios, "candidato", "candidatos") + ")";
        if (acierto) {
            ui.mostrar(nombre + " adivinó: " + sospecha.getNombre() + contexto + ". ¡" + nombre + " gana!");
        } else {
            ui.mostrar(nombre + " arriesgó con " + sospecha.getNombre() + contexto
                    + " y no era. Se descarta y sigue el juego.");
        }
    }

    public void victoriaDelHumano(String nombreHumano, Personaje sospecha, int candidatosPrevios, int totalVictorias) {
        adivinanza(nombreHumano, sospecha, true, candidatosPrevios);
        ui.mostrar("Llevás " + totalVictorias + " " + plural(totalVictorias, "victoria", "victorias") + ".");
    }

    public void victoriasSinCambios(int totalVictorias) {
        if (totalVictorias == 0) {
            ui.mostrar("Seguís sin victorias registradas.");
        } else if (totalVictorias == 1) {
            ui.mostrar("Se mantiene tu única victoria.");
        } else {
            ui.mostrar("Se mantienen tus " + totalVictorias + " victorias.");
        }
    }

    public void transicionAlSegundoRival(String nombreM1, String nombreM2) {
        ui.mostrar("Le ganaste a " + nombreM1 + ". Ahora jugás contra " + nombreM2
                + ", que ya sabe lo que " + nombreM1 + " averiguó sobre tu personaje.");
    }

    public void respuestaRechazada() {
        ui.mostrar("Eso no es cierto sobre tu personaje.");
    }

    // Devuelve texto en vez de imprimirlo: es el prompt que usa EntradaJugador.
    public String promptAntiMentira(Pregunta pregunta) {
        return "La máquina pregunta: \"" + pregunta.texto() + "\". ¿Es cierto sobre tu personaje?";
    }

    private String plural(int cantidad, String singular, String plural) {
        return cantidad == 1 ? singular : plural;
    }
}