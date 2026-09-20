package adivinaquien.ui.swing;

import adivinaquien.dominio.Personaje;
import adivinaquien.ui.InterfazUsuario;
import java.util.List;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import javax.swing.SwingUtilities;

// El motor corre en otro hilo (ver Main); acá se bloquea hasta que llega la
// respuesta desde el hilo de Swing.
public class InterfazGrafica implements InterfazUsuario {

    private final VentanaJuego ventana = new VentanaJuego();
    private final SynchronousQueue<Object> respuestas = new SynchronousQueue<>();

    public InterfazGrafica() {
        SwingUtilities.invokeLater(() -> ventana.setVisible(true));
    }

    public void mostrar(String mensaje) {
        SwingUtilities.invokeLater(() -> ventana.agregarLog(mensaje));
    }

    public void mostrarTablero(String titulo, List<Personaje> todos, Set<Integer> idsVigentes) {
        SwingUtilities.invokeLater(() -> {
            ventana.agregarLog(titulo);
            ventana.actualizarTablero(todos, idsVigentes);
        });
    }

    public String pedirTexto(String prompt) {
        SwingUtilities.invokeLater(() -> ventana.modoTexto(prompt, this::responder));
        return (String) esperarRespuesta();
    }

    public int pedirOpcion(String prompt, List<String> opciones) {
        SwingUtilities.invokeLater(() -> ventana.modoOpciones(prompt, opciones, this::responder));
        return (Integer) esperarRespuesta();
    }

    public Personaje pedirPersonaje(String prompt, List<Personaje> elegibles) {
        SwingUtilities.invokeLater(() -> ventana.modoSeleccionPersonaje(prompt, elegibles, this::responder));
        return (Personaje) esperarRespuesta();
    }

    private void responder(Object valor) {
        try {
            respuestas.put(valor);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Object esperarRespuesta() {
        try {
            return respuestas.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrumpido esperando una respuesta de la ventana", e);
        }
    }
}
