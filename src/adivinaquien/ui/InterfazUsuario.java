package adivinaquien.ui;

import adivinaquien.dominio.Personaje;
import java.util.List;

public interface InterfazUsuario {
    void mostrar(String mensaje);
    void limpiarYMostrar(String mensaje);
    String pedirTexto(String prompt);
    int pedirOpcion(String prompt, List<String> opciones);

    // Muestra el tablero completo marcando cuáles de 'todos' siguen siendo candidatos posibles y cuáles ya se descartaron
    void mostrarTablero(String titulo, List<Personaje> todos, List<Personaje> vigentes);

    // Pide que se elija uno de 'elegibles'
    Personaje pedirPersonaje(String prompt, List<Personaje> elegibles);
}
