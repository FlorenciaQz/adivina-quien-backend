package adivinaquien.ui;

import adivinaquien.dominio.Personaje;
import java.util.List;
import java.util.Set;

public interface InterfazUsuario {
    void mostrar(String mensaje);
    void mostrarTablero(String titulo, List<Personaje> todos, Set<Integer> idsVigentes);
    String pedirTexto(String prompt);
    int pedirOpcion(String prompt, List<String> opciones);
}