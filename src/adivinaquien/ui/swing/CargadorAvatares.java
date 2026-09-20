package adivinaquien.ui.swing;

import adivinaquien.dominio.Personaje;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;

// Devuelve el avatar de un personaje: busca resources/personajes/<id>.png o .jpg.
// Si todavía no está cargado, muestra un placeholder mínimo en vez de romper la
// grilla. Cachea por id para no releer el archivo en cada repintado.
public class CargadorAvatares {

    // Resolución "maestra" con la que se cachea cada avatar. TarjetaPersonajePanel la
    // redibuja escalada al tamaño real de cada tarjeta, así que esto solo define el
    // techo de calidad cuando la ventana (y las tarjetas) crecen.
    private static final int TAMANIO = 220;
    private static final String CARPETA = "resources/personajes";

    private final Map<Integer, BufferedImage> cache = new HashMap<>();

    public BufferedImage generar(Personaje personaje) {
        return cache.computeIfAbsent(personaje.getId(), id -> cargarArchivo(id).orElseGet(this::placeholder));
    }

    private Optional<BufferedImage> cargarArchivo(int id) {
        for (String extension : new String[] {"png", "jpg"}) {
            File archivo = new File(CARPETA, id + "." + extension);
            if (archivo.exists()) {
                try {
                    BufferedImage original = ImageIO.read(archivo);
                    return original != null ? Optional.of(escalar(original)) : Optional.empty();
                } catch (Exception e) {
                    // archivo corrupto o ilegible: se ignora y se cae al placeholder
                }
            }
        }
        return Optional.empty();
    }

    // Escala cualquier imagen (sea cual sea su resolución original) a un cuadrado de
    // TAMANIO x TAMANIO, recortando al centro en vez de deformarla — mismo efecto que
    // "object-fit: cover" en CSS. Así todas las tarjetas quedan del mismo tamaño sin
    // depender de que las imágenes que carguen ya vengan cuadradas.
    private BufferedImage escalar(BufferedImage original) {
        BufferedImage resultado = new BufferedImage(TAMANIO, TAMANIO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        double escala = Math.max(TAMANIO / (double) original.getWidth(), TAMANIO / (double) original.getHeight());
        int anchoEscalado = (int) Math.ceil(original.getWidth() * escala);
        int altoEscalado = (int) Math.ceil(original.getHeight() * escala);
        int x = (TAMANIO - anchoEscalado) / 2;
        int y = (TAMANIO - altoEscalado) / 2;
        g.drawImage(original, x, y, anchoEscalado, altoEscalado, null);
        g.dispose();
        return resultado;
    }

    private BufferedImage placeholder() {
        BufferedImage img = new BufferedImage(TAMANIO, TAMANIO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xEE, 0xEE, 0xEE));
        g.fillRoundRect(0, 0, TAMANIO, TAMANIO, 16, 16);
        g.setColor(new Color(0xAA, 0xAA, 0xAA));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String texto = "sin imagen";
        int ancho = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, (TAMANIO - ancho) / 2, TAMANIO / 2);
        g.dispose();
        return img;
    }
}
