package adivinaquien.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

// Guarda cuántas veces le ganó cada jugador humano a las máquinas, en un archivo de
// texto plano (marcador.properties, en la raíz del proyecto) para que sobreviva entre
// corridas. No depende de InterfazUsuario a propósito: es pura persistencia, no le
// importa cómo se muestra la info.
public class MarcadorPartidas {

    private static final String ARCHIVO = "marcador.properties";

    private final Properties victorias = new Properties();

    public MarcadorPartidas() {
        cargar();
    }

    public int victoriasDe(String nombreHumano) {
        String valor = victorias.getProperty(clave(nombreHumano), "0");
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0; // el archivo se editó/corrompió a mano: se ignora y arranca de 0
        }
    }

    public void registrarVictoria(String nombreHumano) {
        int nuevoTotal = victoriasDe(nombreHumano) + 1;
        victorias.setProperty(clave(nombreHumano), String.valueOf(nuevoTotal));
        guardar();
    }

    private String clave(String nombreHumano) {
        return nombreHumano.trim();
    }

    private void cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return; // primera corrida: todavía no hay nada que cargar
        }
        try (FileInputStream in = new FileInputStream(archivo)) {
            victorias.load(in);
        } catch (IOException e) {
            // no se pudo leer el marcador: se arranca vacío en vez de romper el juego
        }
    }

    private void guardar() {
        try (FileOutputStream out = new FileOutputStream(ARCHIVO)) {
            victorias.store(out, "Marcador de victorias de Adivina Quien");
        } catch (IOException e) {
            // no se pudo guardar: se ignora, no vale la pena interrumpir el juego por esto
        }
    }
}
