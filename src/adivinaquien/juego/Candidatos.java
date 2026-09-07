package adivinaquien.juego;

import adivinaquien.algoritmos.Pregunta;
import adivinaquien.dominio.Personaje;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


// Operaciones sobre listas de candidatos.
//Nota: filtrar y descartar modifican la lista recibida in-place, no devuelven una
//copia. De eso depende que Máquina 2 herede lo que filtró Máquina 1.
public final class Candidatos {

    private Candidatos() {
    }

    public static void filtrar(List<Personaje> candidatos, Pregunta pregunta, boolean verdad) {
        Iterator<Personaje> it = candidatos.iterator();
        while (it.hasNext()) {
            Personaje p = it.next();
            if (pregunta.evaluar(p) != verdad) {
                it.remove();
            }
        }
    }

    public static int contar(List<Personaje> candidatos, Pregunta pregunta, boolean valor) {
        int cantidad = 0;
        for (int i = 0; i < candidatos.size(); i++) {
            if (pregunta.evaluar(candidatos.get(i)) == valor) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public static void descartar(List<Personaje> candidatos, Personaje aDescartar) {
        Iterator<Personaje> it = candidatos.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == aDescartar.getId()) {
                it.remove();
                return;
            }
        }
    }

    public static Set<Integer> ids(List<Personaje> candidatos) {
        Set<Integer> resultado = new HashSet<Integer>();
        for (int i = 0; i < candidatos.size(); i++) {
            resultado.add(candidatos.get(i).getId());
        }
        return resultado;
    }
}