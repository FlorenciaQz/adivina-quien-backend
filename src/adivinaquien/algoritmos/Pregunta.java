package adivinaquien.algoritmos;

import adivinaquien.dominio.ColorPelo;
import adivinaquien.dominio.Genero;
import adivinaquien.dominio.Personaje;

public class Pregunta {

    public enum Criterio { ES_HOMBRE, USA_LENTES, ES_HUMANO, COLOR_PELO }

    private final Criterio criterio;
    private final ColorPelo color;
    private final String texto;

    // 'color' solo se usa cuando criterio == COLOR_PELO (si no, null)
    public Pregunta(Criterio criterio, ColorPelo color, String texto) {
        this.criterio = criterio;
        this.color = color;
        this.texto = texto;
    }

    public String texto() {
        return texto;
    }

    public boolean evaluar(Personaje p) {
        switch (criterio) {
            case ES_HOMBRE:  return p.getGenero() == Genero.M;
            case USA_LENTES: return p.tieneLentes();
            case ES_HUMANO:  return p.esHumano();
            case COLOR_PELO: return p.getPelo() == color;
            default:         return false;
        }
    }
}
