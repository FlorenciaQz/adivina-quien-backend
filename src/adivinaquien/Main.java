package adivinaquien;

import adivinaquien.algoritmos.CatalogoPreguntas;
import adivinaquien.algoritmos.EstrategiaDesbalanceada;
import adivinaquien.algoritmos.EstrategiaGreedy;
import adivinaquien.dominio.CargaPersonajes;
import adivinaquien.dominio.Personaje;
import adivinaquien.dominio.Tablero;
import adivinaquien.juego.Maquina;
import adivinaquien.juego.MotorJuego;
import adivinaquien.ui.ConsolaUI;
import adivinaquien.ui.InterfazUsuario;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Personaje> personajes = CargaPersonajes.crearTodos();
        Tablero tablero = new Tablero(personajes);

        InterfazUsuario ui = new ConsolaUI();
        CatalogoPreguntas catalogo = new CatalogoPreguntas();

        // Dos heurísticas greedy distintas para poder comparar:
        // M1 busca la pregunta que divide más parejo a los candidatos,
        // M2 busca la que divide más desparejo
        Maquina m1 = new Maquina("Máquina 1", 70, new EstrategiaGreedy(catalogo));
        Maquina m2 = new Maquina("Máquina 2", 30, new EstrategiaDesbalanceada(catalogo));

        MotorJuego motor = new MotorJuego(tablero, ui, catalogo);

        List<String> modos = List.of(
            "Humano vs Máquina (jugás contra M1 y, si ganás, contra M2)",
            "Máquina vs Máquina (mirá a la IA jugar sola)");
        int opcion = ui.pedirOpcion("Elegí un modo de juego:", modos);

        if (opcion == 0) {
            String nombreHumano = ui.pedirTexto("¿Cómo te llamás?: ");
            motor.jugarFlujoCompleto(nombreHumano, m1, m2);
        } else {
            motor.jugarMaquinaVsMaquina(m1, m2);
        }
    }
}
