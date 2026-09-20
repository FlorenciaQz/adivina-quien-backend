package adivinaquien.ui.swing;

import adivinaquien.dominio.Personaje;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class VentanaJuego extends JFrame {

    private JPanel panel1;
    private JTextArea log;
    private JPanel grilla;
    private JPanel panelAccion;
    private JScrollPane logScroll;
    private JScrollPane grillaScroll;

    private final CargadorAvatares avatares = new CargadorAvatares();
    private final Map<Integer, TarjetaPersonajePanel> tarjetasPorId = new LinkedHashMap<>();
    private final JLabel promptLabel = new JLabel(" ");

    public VentanaJuego() {
        super("Adivina Quién");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(1050, 900);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(log.getFont().deriveFont(13f));
        logScroll.setPreferredSize(new Dimension(1000, 140));
        logScroll.setBorder(BorderFactory.createTitledBorder("Partida"));

        grilla.setLayout(new GridLayout(0, 8, 6, 6));
        grilla.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panelAccion.setLayout(new BorderLayout(8, 8));
        promptLabel.setFont(promptLabel.getFont().deriveFont(Font.BOLD, 14f));
        panelAccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Tu turno"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panelAccion.setPreferredSize(new Dimension(1000, 260));
        panelAccion.add(promptLabel, BorderLayout.NORTH);
    }

    public void agregarLog(String mensaje) {
        log.append(mensaje + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    public void limpiarLog() {
        log.setText("");
    }

    // Construye la grilla la primera vez que se necesita (el orden de 'todos' es
    // siempre el mismo entre llamadas, porque sale de Tablero, que ya está ordenado).
    public void actualizarTablero(List<Personaje> todos, Set<Integer> idsVigentes) {
        if (tarjetasPorId.isEmpty()) {
            construirGrilla(todos);
        }
        for (Personaje p : todos) {
            TarjetaPersonajePanel tarjeta = tarjetasPorId.get(p.getId());
            tarjeta.setVigente(idsVigentes.contains(p.getId()));
            tarjeta.setSeleccionable(false);
        }
        grilla.revalidate();
        grilla.repaint();
    }

    private void construirGrilla(List<Personaje> todos) {
        for (Personaje p : todos) {
            TarjetaPersonajePanel tarjeta = new TarjetaPersonajePanel(p, avatares.generar(p));
            tarjetasPorId.put(p.getId(), tarjeta);
            grilla.add(tarjeta);
        }
    }

    public void modoTexto(String prompt, Consumer<String> alConfirmar) {
        panelAccion.removeAll();
        promptLabel.setText(prompt);

        JTextField campo = new JTextField();
        JButton confirmar = new JButton("Confirmar");
        Runnable enviar = () -> {
            campo.setEnabled(false);
            confirmar.setEnabled(false);
            alConfirmar.accept(campo.getText());
        };
        confirmar.addActionListener(e -> enviar.run());
        campo.addActionListener(e -> enviar.run());

        panelAccion.add(promptLabel, BorderLayout.NORTH);
        panelAccion.add(campo, BorderLayout.CENTER);
        panelAccion.add(confirmar, BorderLayout.EAST);
        deshabilitarSeleccionDeTarjetas();
        revalidarAccion();
        campo.requestFocusInWindow();
    }

    public void modoOpciones(String prompt, List<String> opciones, Consumer<Integer> alElegir) {
        panelAccion.removeAll();
        promptLabel.setText(prompt);

        JPanel botones = new JPanel();
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        List<JButton> todosLosBotones = new ArrayList<>();
        for (int i = 0; i < opciones.size(); i++) {
            int indice = i;
            JButton boton = new JButton(opciones.get(i));
            boton.setFont(boton.getFont().deriveFont(13f));
            boton.setAlignmentX(Component.LEFT_ALIGNMENT);
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, boton.getPreferredSize().height + 10));
            boton.addActionListener(e -> {
                for (JButton b : todosLosBotones) {
                    b.setEnabled(false);
                }
                alElegir.accept(indice);
            });
            todosLosBotones.add(boton);
            botones.add(boton);
            botones.add(Box.createVerticalStrut(6));
        }

        JScrollPane scroll = new JScrollPane(botones);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panelAccion.add(promptLabel, BorderLayout.NORTH);
        panelAccion.add(scroll, BorderLayout.CENTER);
        deshabilitarSeleccionDeTarjetas();
        revalidarAccion();
    }

    public void modoSeleccionPersonaje(String prompt, List<Personaje> elegibles, Consumer<Personaje> alElegir) {
        panelAccion.removeAll();
        promptLabel.setText(prompt + " — hacé click en una tarjeta del tablero");
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelAccion.add(promptLabel, BorderLayout.NORTH);
        revalidarAccion();

        deshabilitarSeleccionDeTarjetas();
        for (Personaje p : elegibles) {
            TarjetaPersonajePanel tarjeta = tarjetasPorId.get(p.getId());
            if (tarjeta != null) {
                tarjeta.setOnClick(alElegir);
                tarjeta.setSeleccionable(true);
            }
        }
    }

    private void deshabilitarSeleccionDeTarjetas() {
        for (TarjetaPersonajePanel tarjeta : tarjetasPorId.values()) {
            tarjeta.setSeleccionable(false);
        }
    }

    private void revalidarAccion() {
        panelAccion.revalidate();
        panelAccion.repaint();
    }
}
