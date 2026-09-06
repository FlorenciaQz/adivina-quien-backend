package adivinaquien.ui;

import adivinaquien.dominio.Personaje;
import java.util.Set;
import java.util.List;
import java.util.Scanner;

public class ConsolaUI implements InterfazUsuario {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ROJO = "\u001B[31m";
    private static final String ANSI_VERDE = "\u001B[32m";

    private final Scanner scanner = new Scanner(System.in);

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
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

    public void mostrarTablero(String titulo, List<Personaje> todos, Set<Integer> idsVigentes) {
        System.out.println(titulo);
        for (int i = 0; i < todos.size(); i++) {
            Personaje p = todos.get(i);
            boolean vigente = idsVigentes.contains(p.getId());
            String color = vigente ? ANSI_VERDE : ANSI_ROJO;
            String marca = vigente ? "[posible]  " : "[descartado]";
            System.out.println(color + "  " + marca + " #" + p.getId() + " "
                    + p.getNombre() + " - " + p.descripcionAtributos() + ANSI_RESET);
        }
    }

}
