package adivinaquien.juego;

import adivinaquien.algoritmos.Pregunta;
import adivinaquien.dominio.Personaje;
import adivinaquien.dominio.Tablero;
import adivinaquien.persistencia.MarcadorPartidas;
import adivinaquien.ui.InterfazUsuario;
import java.util.List;
import java.util.Random;

public class MotorJuego {

    private final Tablero tablero;
    private final InterfazUsuario ui;
    private final MarcadorPartidas marcador;
    private final Random random;
    private final EntradaJugador entrada;
    private final PresentadorJuego presentador;

    public MotorJuego(Tablero tablero, InterfazUsuario ui, MarcadorPartidas marcador,
                      Random random, EntradaJugador entrada, PresentadorJuego presentador) {
        this.tablero = tablero;
        this.ui = ui;
        this.marcador = marcador;
        this.random = random;
        this.entrada = entrada;
        this.presentador = presentador;
    }

    public void jugarMaquinaVsMaquina(Maquina m1, Maquina m2) {
        List<Personaje> personajes = tablero.personajes();
        Personaje secretoM1 = elegirSecreto(personajes);
        Personaje secretoM2 = elegirSecreto(personajes);

        presentador.inicioMaquinaVsMaquina(m1.getNombre());

        List<Personaje> candidatosM1 = tablero.personajes();
        List<Personaje> candidatosM2 = tablero.personajes();
        boolean turnoM1 = true;

        while (true) {
            Maquina turno = turnoM1 ? m1 : m2;
            List<Personaje> candidatosDelTurno = turnoM1 ? candidatosM1 : candidatosM2;
            Personaje secretoRival = turnoM1 ? secretoM2 : secretoM1;

            int cantidad = candidatosDelTurno.size();
            // Si quedan perfiles idénticos entre sí, puede no haber ninguna pregunta que
            // los distinga (mejorPregunta devuelve null): ahí no hay más remedio que
            // arriesgar, igual que con 1 solo candidato.
            Pregunta pregunta = cantidad == 1 ? null : turno.getEstrategia().mejorPregunta(candidatosDelTurno);
            if (pregunta == null || turno.decideArriesgar(cantidad)) {
                if (arriesgarYAdivinar(turno.getNombre(), candidatosDelTurno, secretoRival)) {
                    return;
                }
            } else {
                int si = Candidatos.contar(candidatosDelTurno, pregunta, true);
                presentador.preguntaElegida(turno.getNombre(), pregunta, si, cantidad - si);
                boolean verdad = pregunta.evaluar(secretoRival);
                Candidatos.filtrar(candidatosDelTurno, pregunta, verdad);
                presentador.resultadoDeFiltro(verdad, candidatosDelTurno.size());
            }

            turnoM1 = !turnoM1;
        }
    }

    // flujo completo: humano vs M1; si el humano gana, humano vs M2.
    // El humano conserva su mismo personaje secreto en ambas partidas. M2 arranca con los
    // candidatos que M1 ya habia logrado filtrar (no repite lo que M1 ya averiguo).
    public void jugarFlujoCompleto(String nombreHumano, Maquina m1, Maquina m2) {
        presentador.record(nombreHumano, marcador.victoriasDe(nombreHumano));

        Personaje secretoHumano = pedirSecretoHumano();
        List<Personaje> personajes = tablero.personajes();

        Personaje secretoM1 = elegirSecreto(personajes);
        List<Personaje> candidatosM1 = tablero.personajes();
        boolean humanoGanoAM1 = jugarUnaPartidaHumanoVsMaquina(nombreHumano, secretoHumano, m1, secretoM1, candidatosM1);
        if (!humanoGanoAM1) {
            return;
        }

        presentador.transicionAlSegundoRival(m1.getNombre(), m2.getNombre());

        Personaje secretoM2 = elegirSecreto(personajes);
        jugarUnaPartidaHumanoVsMaquina(nombreHumano, secretoHumano, m2, secretoM2, candidatosM1);
    }

    // Corre el loop de turnos de una partida humano-vs-maquina. candidatosMaquina se
    // pasa por referencia y se va filtrando in-place: al terminar, el que llamó a este
    // metodo puede seguir usando esa misma lista (asi es como M2 hereda lo de M1).
    // Devuelve true si ganó el humano.
    private boolean jugarUnaPartidaHumanoVsMaquina(String nombreHumano, Personaje secretoHumano, Maquina maquina,
                                                   Personaje secretoMaquina, List<Personaje> candidatosMaquina) {
        List<Personaje> candidatosHumano = tablero.personajes();
        boolean turnoHumano = true;

        while (true) {
            if (turnoHumano) {
                ui.mostrarTablero("Candidatos para el secreto del rival:",
                        tablero.personajes(), Candidatos.ids(candidatosHumano));
                List<String> opciones = List.of("Hacer una pregunta", "Adivinar un personaje");
                int opcion = ui.pedirOpcion("Es tu turno. ¿Qué querés hacer?", opciones);

                if (opcion == 1) {
                    // Se captura antes de descartar, para que el conteo del mensaje sea el
                    // que tenía el jugador al momento de arriesgar.
                    int candidatosPrevios = candidatosHumano.size();
                    Personaje sospecha = entrada.pedirPersonajePorId("¿A quién adivinás? (número): ", tablero.personajes());
                    if (sospecha.getId() == secretoMaquina.getId()) {
                        // Se registra antes de armar el mensaje para que el conteo ya incluya esta victoria.
                        marcador.registrarVictoria(nombreHumano);
                        presentador.victoriaDelHumano(nombreHumano, sospecha, candidatosPrevios,
                                marcador.victoriasDe(nombreHumano));
                        return true;
                    }
                    presentador.adivinanza(nombreHumano, sospecha, false, candidatosPrevios);
                    Candidatos.descartar(candidatosHumano, sospecha);
                } else {
                    Pregunta pregunta = entrada.pedirPregunta();
                    boolean verdad = pregunta.evaluar(secretoMaquina);
                    Candidatos.filtrar(candidatosHumano, pregunta, verdad);
                    presentador.preguntaConResultado(nombreHumano, pregunta, verdad, candidatosHumano.size());
                }

            } else {
                int cantidad = candidatosMaquina.size();
                // Igual que en jugarMaquinaVsMaquina: si no hay ninguna pregunta que separe
                // a los candidatos restantes (perfiles idénticos), mejorPregunta da null y
                // no queda otra que arriesgar.
                Pregunta pregunta = cantidad == 1 ? null : maquina.getEstrategia().mejorPregunta(candidatosMaquina);
                if (pregunta == null || maquina.decideArriesgar(cantidad)) {
                    if (arriesgarYAdivinar(maquina.getNombre(), candidatosMaquina, secretoHumano)) {
                        presentador.victoriasSinCambios(marcador.victoriasDe(nombreHumano));
                        return false;
                    }
                } else {
                    boolean verdad = obtenerRespuestaConAntiMentira(pregunta, secretoHumano);
                    Candidatos.filtrar(candidatosMaquina, pregunta, verdad);
                    presentador.preguntaConResultado(maquina.getNombre(), pregunta, verdad, candidatosMaquina.size());
                }
            }

            turnoHumano = !turnoHumano;
        }
    }

    // Arriesga: adivina el primer candidato restante. Si acierta, el llamador corta el
    // juego; si falla, se descarta ese candidato y el turno sigue. Devuelve si acertó.
    private boolean arriesgarYAdivinar(String nombreAdivinador, List<Personaje> candidatos, Personaje secretoReal) {
        Personaje sospecha = candidatos.get(0);
        int candidatosPrevios = candidatos.size();
        boolean acierto = sospecha.getId() == secretoReal.getId();
        presentador.adivinanza(nombreAdivinador, sospecha, acierto, candidatosPrevios);
        if (!acierto) {
            Candidatos.descartar(candidatos, sospecha);
        }
        return acierto;
    }

    // La maquina le pregunta al humano: no puede mentir, si contesta mal se rechaza y se vuelve a pedir.
    private boolean obtenerRespuestaConAntiMentira(Pregunta pregunta, Personaje secretoHumano) {
        boolean verdad = pregunta.evaluar(secretoHumano);
        while (true) {
            boolean respuestaHumano = entrada.pedirConfirmacion(presentador.promptAntiMentira(pregunta));
            if (respuestaHumano == verdad) {
                return verdad;
            }
            presentador.respuestaRechazada();
        }
    }

    private Personaje pedirSecretoHumano() {
        List<Personaje> todos = tablero.personajes();
        ui.mostrarTablero("Elegí tu personaje secreto. Estos son los disponibles:",
                todos, Candidatos.ids(todos));
        return entrada.pedirPersonajePorId(
                "Elegí tu personaje secreto por su número (no lo vas a poder cambiar): ", todos);
    }

    // Sorteo independiente entre los 23: no hace falta excluir a nadie (ni el secreto
    // del humano, ni el de la otra máquina). Que coincida con uno ya usado no le da
    // ninguna ventaja al humano: si arriesga con ese nombre tiene la misma probabilidad
    // que con cualquier otro, y si falla no pierde, solo se descarta como cualquier otro.
    private Personaje elegirSecreto(List<Personaje> personajes) {
        return personajes.get(random.nextInt(personajes.size()));
    }
}