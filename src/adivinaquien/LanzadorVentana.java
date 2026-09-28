package adivinaquien;

import adivinaquien.ui.swing.InterfazGrafica;

// Punto de entrada del .jar: abre directamente la ventana, sin el menú por consola.
public class LanzadorVentana {

    public static void main(String[] args) {
        Main.iniciar(new InterfazGrafica());
    }
}