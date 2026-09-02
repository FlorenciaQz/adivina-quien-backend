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
import java.util.function.Consumer;
import javax.swing.BorderFactory;
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

    private final CargadorAvatares avatares = new CargadorAvatares();
    private final Map<Integer, TarjetaPersonajePanel> tarjetasPorId = new LinkedHashMap<>();

    private final JTextArea log = new JTextArea();
    private final JPanel grilla = new JPanel(new GridLayout(0, 8, 6, 6));
    private final JPanel panelAccion = new JPanel(new BorderLayout(8, 8));
    private final JLabel promptLabel = new JLabel(" ");

    public VentanaJuego() {
        super("Adivina Quién");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setSize(1050, 900);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(log.getFont().deriveFont(13f));
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setPreferredSize(new Dimension(1000, 140));
        logScroll.setBorder(BorderFactory.createTitledBorder("Partida"));

        grilla.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // Pensada para que las 23 tarjetas entren sin scroll (8 columnas x 3 filas);
        // el JScrollPane queda solo como red de seguridad si la fuente del sistema
        // es más grande de lo esperado.
        JScrollPane grillaScroll = new JScrollPane(grilla);

        promptLabel.setFont(promptLabel.getFont().deriveFont(Font.BOLD, 14f));
        panelAccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Tu turno"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panelAccion.setPreferredSize(new Dimension(1000, 260));
        panelAccion.add(promptLabel, BorderLayout.NORTH);

        add(logScroll, BorderLayout.NORTH);
        add(grillaScroll, BorderLayout.CENTER);
        add(panelAccion, BorderLayout.SOUTH);
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
    public void actualizarTablero(List<Personaje> todos, List<Personaje> vigentes) {
        if (tarjetasPorId.isEmpty()) {
            construirGrilla(todos);
        }
        for (Personaje p : todos) {
            TarjetaPersonajePanel tarjeta = tarjetasPorId.get(p.getId());
            boolean esVigente = vigentes.stream().anyMatch(v -> v.getId() == p.getId());
            tarjeta.setVigente(esVigente);
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
            botones.add(javax.swing.Box.createVerticalStrut(6));
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
