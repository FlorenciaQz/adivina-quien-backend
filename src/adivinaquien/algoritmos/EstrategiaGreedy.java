package adivinaquien.algoritmos;

import adivinaquien.dominio.Personaje;
import java.util.List;

public class EstrategiaGreedy implements EstrategiaPreguntas {

    private final CatalogoPreguntas catalogo;

    public EstrategiaGreedy(CatalogoPreguntas catalogo) {
        this.catalogo = catalogo;
    }

    // greedy por ganancia de informacion: la pregunta que divide mas parejo a los candidatos
    public Pregunta mejorPregunta(List<Personaje> candidatos) {
        Pregunta mejor = null;
        int mejorPuntaje = Integer.MIN_VALUE;
        List<Pregunta> todas = catalogo.todas();
        for (int i = 0; i < todas.size(); i++) {
            Pregunta q = todas.get(i);
            int si = 0;
            for (int j = 0; j < candidatos.size(); j++) {
                if (q.evaluar(candidatos.get(j))) {
                    si++;
                }
            }
            int no = candidatos.size() - si;
            if (si == 0 || no == 0) {
                continue;
            }
            int puntaje = -Math.abs(si - no);
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = q;
            }
        }
        return mejor;
    }
}
