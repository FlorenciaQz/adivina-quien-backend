package adivinaquien.algoritmos;

import adivinaquien.dominio.Personaje;
import java.util.List;

// Greedy "desbalanceado": el mismo algoritmo que EstrategiaGreedy (recorre las
// preguntas, mira cómo dividen a los candidatos), pero con el objetivo
// invertido: en vez de buscar la pregunta que divide MÁS PAREJO, busca la que
// divide MÁS DESPAREJO. La apuesta es aislar rápido un grupo chico de
// sospechosos en vez de repartir la información pareja entre las dos ramas.
// Mismo costo O(P·N) por turno que EstrategiaGreedy; lo único que cambia es
// qué se maximiza.
public class EstrategiaDesbalanceada implements EstrategiaPreguntas {

    private final CatalogoPreguntas catalogo;

    public EstrategiaDesbalanceada(CatalogoPreguntas catalogo) {
        this.catalogo = catalogo;
    }

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
            int puntaje = Math.abs(si - no); // a diferencia de EstrategiaGreedy, acá se MAXIMIZA la diferencia
            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = q;
            }
        }
        return mejor;
    }
}
