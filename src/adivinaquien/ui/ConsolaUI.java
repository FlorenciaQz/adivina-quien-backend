package adivinaquien.ui;

import java.util.List;
import java.util.Scanner;

public class ConsolaUI implements InterfazUsuario {

    // Convención de texto: si un mensaje arranca con este carácter (form feed,
    // el "nueva página" clásico de terminal), ConsolaUI limpia la pantalla antes
    // de mostrarlo. Es solo una señal en el texto: el resto de las capas no sabe
    // nada de ANSI ni de terminales.
    private static final char SEÑAL_LIMPIAR = '\f';

    private static final String ANSI_LIMPIAR_PANTALLA = "\u001B[H\u001B[2J";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ROJO = "\u001B[31m";
    private static final String ANSI_VERDE = "\u001B[32m";

    private final Scanner scanner = new Scanner(System.in);

    public void mostrar(String mensaje) {
        String texto = mensaje;
        if (texto.length() > 0 && texto.charAt(0) == SEÑAL_LIMPIAR) {
            System.out.print(ANSI_LIMPIAR_PANTALLA);
            texto = texto.substring(1);
        }
        System.out.println(colorear(texto));
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
