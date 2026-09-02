package adivinaquien.ui;

import adivinaquien.dominio.Personaje;
import java.util.List;
import java.util.Scanner;

public class ConsolaUI implements InterfazUsuario {

    private static final String ANSI_LIMPIAR_PANTALLA = "\u001B[H\u001B[2J";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ROJO = "\u001B[31m";
    private static final String ANSI_VERDE = "\u001B[32m";

    private final Scanner scanner;

    public ConsolaUI() {
        this(new Scanner(System.in));
    }

    public ConsolaUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrar(String mensaje) {
        System.out.println(colorear(mensaje));
    }

    public void limpiarYMostrar(String mensaje) {
        System.out.print(ANSI_LIMPIAR_PANTALLA);
        mostrar(mensaje);
    }

    public String pedirTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int pedirOpcion(String prompt, List<String> opciones) {
        System.out.println(prompt);
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        while (true) {
            System.out.print("Elegí una opción: ");
            String linea = scanner.nextLine();
            try {
                int numero = Integer.parseInt(linea.trim());
                if (numero >= 1 && numero <= opciones.size()) {
                    return numero - 1;
                }
            } catch (NumberFormatException e) {
                // se ignora, se vuelve a pedir
            }
            System.out.println("Opción inválida. Ingresá un número entre 1 y " + opciones.size() + ".");
        }
    }

    // Muestra el tablero completo marcando cuáles siguen siendo candidatos posibles y cuáles ya se descartaron.
    public void mostrarTablero(String titulo, List<Personaje> todos, List<Personaje> vigentes) {
        limpiarYMostrar(titulo);
        for (int i = 0; i < todos.size(); i++) {
            Personaje p = todos.get(i);
            String marca = esVigente(p, vigentes) ? "[posible]  " : "[descartado]";
            mostrar("  " + marca + " #" + p.getId() + " " + p.getNombre() + " - " + p.descripcionAtributos());
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

    public Personaje pedirPersonaje(String prompt, List<Personaje> elegibles) {
        while (true) {
            String texto = pedirTexto(prompt);
            try {
                int id = Integer.parseInt(texto.trim());
                for (int i = 0; i < elegibles.size(); i++) {
                    if (elegibles.get(i).getId() == id) {
                        return elegibles.get(i);
                    }
                }
            } catch (NumberFormatException e) {
                // se ignora, se vuelve a pedir
            }
            mostrar("No existe ese personaje. Probá de nuevo.");
        }
    }

    // Colorea las tarjetas de personajes: rojo si están descartadas, verde si siguen posibles.
    private String colorear(String texto) {
        if (texto.contains("[descartado]")) {
            return ANSI_ROJO + texto + ANSI_RESET;
        }
        if (texto.contains("[posible]")) {
            return ANSI_VERDE + texto + ANSI_RESET;
        }
        return texto;
    }
}
