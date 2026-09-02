package adivinaquien;

import adivinaquien.algoritmos.CatalogoPreguntas;
import adivinaquien.algoritmos.EstrategiaDesbalanceada;
import adivinaquien.algoritmos.EstrategiaGreedy;
import adivinaquien.dominio.CargaPersonajes;
import adivinaquien.dominio.Personaje;
import adivinaquien.dominio.Tablero;
import adivinaquien.juego.Maquina;
import adivinaquien.juego.MotorJuego;
import adivinaquien.persistencia.MarcadorPartidas;
import adivinaquien.ui.ConsolaUI;
import adivinaquien.ui.InterfazUsuario;
import adivinaquien.ui.swing.InterfazGrafica;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<Personaje> personajes = CargaPersonajes.crearTodos();
        Tablero tablero = new Tablero(personajes);

        InterfazUsuario ui = elegirInterfaz();
        CatalogoPreguntas catalogo = new CatalogoPreguntas();
        MarcadorPartidas marcador = new MarcadorPartidas();

        // Dos heurísticas greedy distintas para poder comparar:
        // M1 busca la pregunta que divide más parejo a los candidatos,
        // M2 busca la que divide más desparejo
        Maquina m1 = new Maquina("Máquina 1", 70, new EstrategiaGreedy(catalogo));
        Maquina m2 = new Maquina("Máquina 2", 30, new EstrategiaDesbalanceada(catalogo));

        MotorJuego motor = new MotorJuego(tablero, ui, catalogo, marcador);

        Runnable juego = () -> {
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
        };

        if (ui instanceof InterfazGrafica) {
            Thread hiloJuego = new Thread(juego, "motor-juego");
            hiloJuego.setDaemon(true);
            hiloJuego.start();
        } else {
            juego.run();
        }
    }

    private static InterfazUsuario elegirInterfaz() {
        System.out.println("¿Cómo querés jugar?");
        System.out.println("  1. Consola");
        System.out.println("  2. Ventana");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Elegí una opción: ");
            String linea = scanner.nextLine().trim();
            if (linea.equals("2")) {
                return new InterfazGrafica();
            }
            if (linea.equals("1") || linea.isEmpty()) {
                return new ConsolaUI(scanner);
            }
            System.out.println("Opción inválida.");
        }
    }
}
