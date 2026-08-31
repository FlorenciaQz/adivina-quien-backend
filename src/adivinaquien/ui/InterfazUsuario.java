package adivinaquien.ui;

import java.util.List;

public interface InterfazUsuario {
    void mostrar(String mensaje);
    void limpiarYMostrar(String mensaje);
    String pedirTexto(String prompt);
    int pedirOpcion(String prompt, List<String> opciones);
}
