package adivinaquien.persistencia;

// Abstraccion del marcador: MotorJuego depende de esto y no de como se guardan
// los datos. Hoy la unica implementacion escribe en un archivo .properties, pero
// se podria cambiar por una en memoria o en base de datos sin tocar el juego.
public interface IMarcador {
    int victoriasDe(String nombreHumano);
    void registrarVictoria(String nombreHumano);
}