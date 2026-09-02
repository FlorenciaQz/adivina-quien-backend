package adivinaquien.ui.swing;

import adivinaquien.dominio.Personaje;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class TarjetaPersonajePanel extends JPanel {

    private static final Color BORDE_POSIBLE = new Color(0x2E, 0x86, 0xAB);
    private static final Color BORDE_DESCARTADO = new Color(0xCC, 0xCC, 0xCC);
    private static final Color BORDE_SELECCIONABLE = new Color(0xE0, 0x7A, 0x5F);
    private static final Color FONDO_DESCARTADO = new Color(0xF2, 0xF2, 0xF2);

    private final Personaje personaje;
    private final AvatarPanel avatarPanel;
    private final JLabel nombreLabel;

    private boolean vigente = true;
    private boolean seleccionable = false;
    private Consumer<Personaje> onClick;

    public TarjetaPersonajePanel(Personaje personaje, BufferedImage avatar) {
        this.personaje = personaje;
        setLayout(new BorderLayout(2, 2));
        setOpaque(true);

        avatarPanel = new AvatarPanel(avatar);

        nombreLabel = new JLabel(personaje.getNombre());
        nombreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nombreLabel.setFont(nombreLabel.getFont().deriveFont(Font.BOLD, 10f));

        add(avatarPanel, BorderLayout.CENTER);
        add(nombreLabel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (seleccionable && onClick != null) {
                    Consumer<Personaje> callback = onClick;
                    setSeleccionable(false);
                    callback.accept(TarjetaPersonajePanel.this.personaje);
                }
            }
        });

        actualizarEstilo();
    }

    // Solo anima la vuelta cuando realmente pasa de posible a descartado. Si vuelve a
    // ser posible (arranca una partida nueva y se reusan las mismas tarjetas) o si ya
    // estaba en ese estado (mostrarTablero se llama en cada turno), no hay animación.
    public void setVigente(boolean nuevoVigente) {
        if (this.vigente == nuevoVigente) {
            return;
        }
        boolean pasaADescartado = this.vigente && !nuevoVigente;
        this.vigente = nuevoVigente;
        actualizarEstilo();
        if (pasaADescartado) {
            avatarPanel.iniciarFlip(true);
        } else {
            avatarPanel.mostrarDescartada(false);
        }
    }

    public void setSeleccionable(boolean seleccionable) {
        this.seleccionable = seleccionable;
        setCursor(seleccionable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        actualizarEstilo();
    }

    public void setOnClick(Consumer<Personaje> onClick) {
        this.onClick = onClick;
    }

    private void actualizarEstilo() {
        Color borde = !vigente ? BORDE_DESCARTADO : (seleccionable ? BORDE_SELECCIONABLE : BORDE_POSIBLE);
        int grosor = seleccionable ? 3 : 2;
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borde, grosor),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        setBackground(vigente ? Color.WHITE : FONDO_DESCARTADO);
        nombreLabel.setForeground(vigente ? Color.BLACK : Color.GRAY);
    }

    // Dibuja el avatar escalado al espacio disponible (crece/achica con la ventana) y
    // sabe "darse vuelta": una animación de Timer que encoge el ancho hasta 0 y lo
    // vuelve a crecer, cambiando de cara (normal <-> descartada) justo en el punto
    // donde el ancho es 0 — el mismo efecto visual que girar una carta física.
    private static class AvatarPanel extends JPanel {
        private final BufferedImage imagenNormal;
        private final BufferedImage imagenDescartada;

        private boolean descartada = false;
        private double escalaX = 1.0;
        private Timer timerFlip;

        AvatarPanel(BufferedImage imagen) {
            this.imagenNormal = imagen;
            this.imagenDescartada = imagen != null ? aDescartada(imagen) : null;
            setOpaque(false);
        }

        // Cambio instantáneo, sin animación (para cuando arranca una partida nueva).
        void mostrarDescartada(boolean descartada) {
            this.descartada = descartada;
            this.escalaX = 1.0;
            repaint();
        }

        void iniciarFlip(boolean aDescartadaFinal) {
            if (timerFlip != null && timerFlip.isRunning()) {
                timerFlip.stop();
            }
            final int pasos = 14;
            final int[] paso = {0};
            timerFlip = new Timer(14, null);
            timerFlip.addActionListener(e -> {
                paso[0]++;
                double t = paso[0] / (double) pasos;
                if (t >= 0.5 && descartada != aDescartadaFinal) {
                    descartada = aDescartadaFinal; // se cambia de cara en el punto de ancho ~0, no se nota el salto
                }
                escalaX = Math.abs(Math.cos(Math.PI * t));
                repaint();
                if (paso[0] >= pasos) {
                    timerFlip.stop();
                    escalaX = 1.0;
                    descartada = aDescartadaFinal;
                    repaint();
                }
            });
            timerFlip.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage imagen = descartada ? imagenDescartada : imagenNormal;
            if (imagen == null) {
                return;
            }
            int lado = Math.min(getWidth(), getHeight());
            if (lado <= 0) {
                return;
            }
            int x = (getWidth() - lado) / 2;
            int y = (getHeight() - lado) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int centroX = getWidth() / 2;
            g2.translate(centroX, 0);
            g2.scale(escalaX, 1.0);
            g2.translate(-centroX, 0);

            g2.drawImage(imagen, x, y, lado, lado, null);
            g2.dispose();
        }

        // Misma imagen pero oscurecida y con una tacha roja, para que "descartado" se
        // note de verdad y no solo con un fondo gris apenas distinto.
        private static BufferedImage aDescartada(BufferedImage original) {
            int w = original.getWidth();
            int h = original.getHeight();
            BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resultado.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(original, 0, 0, null);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g.setColor(new Color(0x33, 0x33, 0x33));
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver);

            g.setColor(new Color(0xD6, 0x3B, 0x3B));
            g.setStroke(new BasicStroke(Math.max(3f, w * 0.05f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) (w * 0.12), (int) (h * 0.12), (int) (w * 0.88), (int) (h * 0.88));
            g.drawLine((int) (w * 0.88), (int) (h * 0.12), (int) (w * 0.12), (int) (h * 0.88));

            g.dispose();
            return resultado;
        }
    }
}
