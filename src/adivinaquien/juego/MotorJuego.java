package adivinaquien.juego;

import adivinaquien.algoritmos.CatalogoPreguntas;
import adivinaquien.algoritmos.Pregunta;
import adivinaquien.dominio.Personaje;
import adivinaquien.dominio.Tablero;
import adivinaquien.persistencia.MarcadorPartidas;
import adivinaquien.ui.InterfazUsuario;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MotorJuego {

    private final Tablero tablero;
    private final InterfazUsuario ui;
    private final CatalogoPreguntas catalogo;
    private final MarcadorPartidas marcador;
    private final Random random = new Random();

    public MotorJuego(Tablero tablero, InterfazUsuario ui, CatalogoPreguntas catalogo, MarcadorPartidas marcador) {
        this.tablero = tablero;
        this.ui = ui;
        this.catalogo = catalogo;
        this.marcador = marcador;
    }

    public void jugarMaquinaVsMaquina(Maquina m1, Maquina m2) {
        List<Personaje> personajes = tablero.personajes();
        Personaje secretoM1 = elegirSecreto(personajes);
        Personaje secretoM2 = elegirSecreto(personajes);

        ui.mostrar("Comienza Máquina vs Máquina. Arranca " + m1.getNombre() + ".");

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
            if (pregunta == null || decideArriesgar(cantidad, turno.getRiesgo())) {
                ui.mostrar(turno.getNombre() + " arriesga y adivina: " + candidatosDelTurno.get(0).getNombre()
                    + " (tenía " + cantidad + " candidatos posibles).");
                if (arriesgarYAdivinar(turno.getNombre(), candidatosDelTurno, secretoRival)) {
                    return;
                }
            } else {
                int si = contarCoincidencias(candidatosDelTurno, pregunta, true);
                int no = cantidad - si;
                ui.mostrar(turno.getNombre() + " pregunta: \"" + pregunta.texto()
                    + "\" (divide en " + si + " sí / " + no + " no).");

                boolean verdad = pregunta.evaluar(secretoRival);
                filtrarCandidatos(candidatosDelTurno, pregunta, verdad);
                ui.mostrar("  -> " + (verdad ? "Sí" : "No") + ". Quedan " + candidatosDelTurno.size() + " candidatos.");
            }

            turnoM1 = !turnoM1;
        }
    }

    // flujo completo: humano vs M1; si el humano gana, humano vs M2.
    // El humano conserva su mismo personaje secreto en ambas partidas. M2 arranca con los
    // candidatos que M1 ya habia logrado filtrar (no repite lo que M1 ya averiguo).
    public void jugarFlujoCompleto(String nombreHumano, Maquina m1, Maquina m2) {
        mostrarRecord(nombreHumano);

        Personaje secretoHumano = pedirSecretoHumano();
        List<Personaje> personajes = tablero.personajes();

        Personaje secretoM1 = elegirSecreto(personajes);
        List<Personaje> candidatosM1 = tablero.personajes();
        boolean humanoGanoAM1 = jugarUnaPartidaHumanoVsMaquina(nombreHumano, secretoHumano, m1, secretoM1, candidatosM1);
        if (!humanoGanoAM1) {
            return;
        }

        ui.mostrar("Le ganaste a " + m1.getNombre() + ". Ahora jugás contra " + m2.getNombre()
            + ", que ya sabe lo que " + m1.getNombre() + " averiguó sobre tu personaje.");

        Personaje secretoM2 = elegirSecreto(personajes);
        jugarUnaPartidaHumanoVsMaquina(nombreHumano, secretoHumano, m2, secretoM2, candidatosM1);
    }

    // Muestra cuántas victorias tiene ya registradas este jugador, antes de arrancar.
    private void mostrarRecord(String nombreHumano) {
        int victoriasPrevias = marcador.victoriasDe(nombreHumano);
        String resumen = victoriasPrevias == 1 ? "1 victoria registrada" : victoriasPrevias + " victorias registradas";
        ui.mostrar("¡Hola " + nombreHumano + "! Llevás " + resumen + ".");
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
                mostrarTablero("Candidatos para el secreto del rival:", tablero.personajes(), candidatosHumano);
                List<String> opciones = List.of("Hacer una pregunta", "Adivinar un personaje");
                int opcion = ui.pedirOpcion("Es tu turno. ¿Qué querés hacer?", opciones);

                if (opcion == 1) {
                    Personaje sospecha = pedirPersonajePorId("¿A quién adivinás? (número): ", tablero.personajes());
                    if (sospecha.getId() == secretoMaquina.getId()) {
                        // Se registra antes de armar el mensaje para que el conteo ya incluya esta victoria.
                        marcador.registrarVictoria(nombreHumano);
                        int totalVictorias = marcador.victoriasDe(nombreHumano);
                        String resumen = totalVictorias == 1 ? "1 victoria" : totalVictorias + " victorias";
                        ui.mostrar(nombreHumano + " adivinó: " + sospecha.getNombre() + ". ¡" + nombreHumano
                            + " gana! Llevás " + resumen + ".");
                        return true;
                    }
                    ui.mostrar(nombreHumano + " arriesgó con " + sospecha.getNombre() + " y no era. Se descarta y sigue el juego.");
                    descartarCandidato(candidatosHumano, sospecha);
                } else {
                    Pregunta pregunta = pedirPreguntaHumano();
                    boolean verdad = pregunta.evaluar(secretoMaquina);
                    filtrarCandidatos(candidatosHumano, pregunta, verdad);
                    ui.mostrar(nombreHumano + " preguntó: \"" + pregunta.texto() + "\" -> "
                        + (verdad ? "Sí" : "No") + ". Quedan " + candidatosHumano.size() + " candidatos.");
                }

            } else {
                int cantidad = candidatosMaquina.size();
                // Igual que en jugarMaquinaVsMaquina: si no hay ninguna pregunta que separe
                // a los candidatos restantes (perfiles idénticos), mejorPregunta da null y
                // no queda otra que arriesgar.
                Pregunta pregunta = cantidad == 1 ? null : maquina.getEstrategia().mejorPregunta(candidatosMaquina);
                if (pregunta == null || decideArriesgar(cantidad, maquina.getRiesgo())) {
                    if (arriesgarYAdivinar(maquina.getNombre(), candidatosMaquina, secretoHumano)) {
                        mostrarVictoriasSinCambios(nombreHumano);
                        return false;
                    }
                } else {
                    boolean verdad = obtenerRespuestaConAntiMentira(pregunta, secretoHumano);
                    filtrarCandidatos(candidatosMaquina, pregunta, verdad);
                    ui.mostrar(maquina.getNombre() + " preguntó: \"" + pregunta.texto() + "\" -> "
                        + (verdad ? "Sí" : "No") + ". Quedan " + candidatosMaquina.size() + " candidatos.");
                }
            }

            turnoHumano = !turnoHumano;
        }
    }

    // arriesga si la probabilidad de acertar de una ya supera el umbral que marca el riesgo (0..100)
    private boolean decideArriesgar(int cantidadCandidatos, int riesgo) {
        double probAcierto = 1.0 / cantidadCandidatos;
        double umbral = 1.0 - (riesgo / 100.0);
        return probAcierto >= umbral;
    }

    // Cuando el humano pierde la partida, el marcador no cambia: se lo aclara para que
    // no parezca que se reseteó.
    private void mostrarVictoriasSinCambios(String nombreHumano) {
        int totalVictorias = marcador.victoriasDe(nombreHumano);
        String resumen = totalVictorias == 1 ? "1 victoria" : totalVictorias + " victorias";
        ui.mostrar("Se mantienen tus " + resumen + ".");
    }

    // imprime el resultado de una adivinanza y devuelve si acertó. Errar no termina
    // la partida: solo descarta ese personaje (lo hace el llamador) y sigue el juego.
    private boolean anunciarAdivinanza(String nombreAdivinador, Personaje sospecha, Personaje secretoReal) {
        boolean acierto = sospecha.getId() == secretoReal.getId();
        if (acierto) {
            ui.mostrar(nombreAdivinador + " adivinó: " + sospecha.getNombre() + ". ¡" + nombreAdivinador + " gana!");
        } else {
            ui.mostrar(nombreAdivinador + " arriesgó con " + sospecha.getNombre() + " y no era. Se descarta y sigue el juego.");
        }
        return acierto;
    }

    // Arriesga: adivina el primer candidato restante. Si acierta, el llamador corta el
    // juego; si falla, se descarta ese candidato y el turno sigue. Devuelve si acertó.
    private boolean arriesgarYAdivinar(String nombreAdivinador, List<Personaje> candidatos, Personaje secretoReal) {
        Personaje sospecha = candidatos.get(0);
        boolean acierto = anunciarAdivinanza(nombreAdivinador, sospecha, secretoReal);
        if (!acierto) {
            descartarCandidato(candidatos, sospecha);
        }
        return acierto;
    }

    private void descartarCandidato(List<Personaje> candidatos, Personaje aDescartar) {
        Iterator<Personaje> it = candidatos.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == aDescartar.getId()) {
                it.remove();
                return;
            }
        }
    }

    // La maquina le pregunta al humano: no puede mentir, si contesta mal se rechaza y se vuelve a pedir.
    private boolean obtenerRespuestaConAntiMentira(Pregunta pregunta, Personaje secretoHumano) {
        boolean verdad = pregunta.evaluar(secretoHumano);
        while (true) {
            int opcion = ui.pedirOpcion(
                "La máquina pregunta: \"" + pregunta.texto() + "\". ¿Es cierto sobre tu personaje?", List.of("Sí", "No"));
            boolean respuestaHumano = (opcion == 0);
            if (respuestaHumano == verdad) {
                return verdad;
            }
            ui.mostrar("Eso no es cierto sobre tu personaje.");
        }
    }

    private void filtrarCandidatos(List<Personaje> candidatos, Pregunta pregunta, boolean verdad) {
        Iterator<Personaje> it = candidatos.iterator();
        while (it.hasNext()) {
            Personaje p = it.next();
            if (pregunta.evaluar(p) != verdad) {
                it.remove();
            }
        }
    }

    private int contarCoincidencias(List<Personaje> candidatos, Pregunta pregunta, boolean valor) {
        int cantidad = 0;
        for (int i = 0; i < candidatos.size(); i++) {
            if (pregunta.evaluar(candidatos.get(i)) == valor) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private Personaje pedirSecretoHumano() {
        List<Personaje> todos = tablero.personajes();
        mostrarTablero("Elegí tu personaje secreto. Estos son los disponibles:", todos, todos);
        return pedirPersonajePorId("Elegí tu personaje secreto por su número (no lo vas a poder cambiar): ", todos);
    }

    // Muestra el tablero completo (como tarjetas) marcando cuáles siguen siendo
    // candidatos posibles y cuáles ya se descartaron, para no depender de la memoria.
    private void mostrarTablero(String titulo, List<Personaje> todos, List<Personaje> vigentes) {
        ui.limpiarYMostrar(titulo);
        for (int i = 0; i < todos.size(); i++) {
            Personaje p = todos.get(i);
            String marca = esVigente(p, vigentes) ? "[posible]  " : "[descartado]";
            ui.mostrar("  " + marca + " #" + p.getId() + " " + p.getNombre() + " - " + p.descripcionAtributos());
        }
    }

    private boolean esVigente(Personaje p, List<Personaje> vigentes) {
        for (int i = 0; i < vigentes.size(); i++) {
            if (vigentes.get(i).getId() == p.getId()) {
                return true;
            }
        }
        return false;
    }

    private Pregunta pedirPreguntaHumano() {
        List<Pregunta> todas = catalogo.todas();
        List<String> textos = new ArrayList<String>();
        for (int i = 0; i < todas.size(); i++) {
            textos.add(todas.get(i).texto());
        }
        int opcion = ui.pedirOpcion("Elegí una pregunta:", textos);
        return todas.get(opcion);
    }

    private Personaje pedirPersonajePorId(String prompt, List<Personaje> lista) {
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

    // Sorteo independiente entre los 23: no hace falta excluir a nadie (ni el secreto
    // del humano, ni el de la otra máquina). Que coincida con uno ya usado no le da
    // ninguna ventaja al humano: si arriesga con ese nombre tiene la misma probabilidad
    // que con cualquier otro, y si falla no pierde, solo se descarta como cualquier otro.
    private Personaje elegirSecreto(List<Personaje> personajes) {
        return personajes.get(random.nextInt(personajes.size()));
    }
}
