package adivinaquien.benchmark;

import adivinaquien.dominio.CargaPersonajes;
import adivinaquien.dominio.Personaje;
import adivinaquien.dominio.Tablero;
import java.util.ArrayList;
import java.util.List;

// Compara el MergeSort de Tablero contra dos ordenamientos cuadráticos
// (inserción y burbujeo) sobre la misma lista de 23 personajes y con el
// mismo criterio (género y, dentro del género, nombre).
public class BenchmarkOrdenamiento {

    private static final int CALENTAMIENTO = 20_000;
    private static final int REPETICIONES = 100_000;
    private static final int RONDAS = 5;

    public static void main(String[] args) {
        List<Personaje> base = CargaPersonajes.crearTodos();

        // Primera ejecución "en frío": es la que ocurre en el juego real (se ordena una sola vez).
        long t0 = System.nanoTime(); List<Personaje> m = new Tablero(base).personajes(); long frioMerge = System.nanoTime() - t0;
        t0 = System.nanoTime(); List<Personaje> ins = insercion(base); long frioIns = System.nanoTime() - t0;
        t0 = System.nanoTime(); List<Personaje> bur = burbujeo(base); long frioBur = System.nanoTime() - t0;
        if (!mismosIds(m, ins) || !mismosIds(m, bur)) throw new IllegalStateException("Los ordenamientos no coinciden");

        for (int i = 0; i < CALENTAMIENTO; i++) { new Tablero(base); insercion(base); burbujeo(base); }

        System.out.printf("Frio (1 ejecucion, ms): merge=%.4f insercion=%.4f burbujeo=%.4f%n",
                frioMerge / 1e6, frioIns / 1e6, frioBur / 1e6);
        for (int r = 1; r <= RONDAS; r++) {
            long a = System.nanoTime(); for (int i = 0; i < REPETICIONES; i++) new Tablero(base); long tm = System.nanoTime() - a;
            a = System.nanoTime(); for (int i = 0; i < REPETICIONES; i++) insercion(base); long ti = System.nanoTime() - a;
            a = System.nanoTime(); for (int i = 0; i < REPETICIONES; i++) burbujeo(base); long tb = System.nanoTime() - a;
            System.out.printf("Ronda %d (promedio por ordenamiento, ms): merge=%.6f insercion=%.6f burbujeo=%.6f%n",
                    r, tm / 1e6 / REPETICIONES, ti / 1e6 / REPETICIONES, tb / 1e6 / REPETICIONES);
        }
    }

    static List<Personaje> insercion(List<Personaje> entrada) {
        List<Personaje> l = new ArrayList<Personaje>(entrada);
        for (int i = 1; i < l.size(); i++) {
            Personaje actual = l.get(i);
            int j = i - 1;
            while (j >= 0 && esMayor(l.get(j), actual)) { l.set(j + 1, l.get(j)); j--; }
            l.set(j + 1, actual);
        }
        return l;
    }

    static List<Personaje> burbujeo(List<Personaje> entrada) {
        List<Personaje> l = new ArrayList<Personaje>(entrada);
        for (int i = 0; i < l.size() - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < l.size() - 1 - i; j++) {
                if (esMayor(l.get(j), l.get(j + 1))) {
                    Personaje tmp = l.get(j); l.set(j, l.get(j + 1)); l.set(j + 1, tmp); huboCambio = true;
                }
            }
            if (!huboCambio) break;
        }
        return l;
    }

    // Mismo criterio que Tablero.esMayor.
    private static boolean esMayor(Personaje a, Personaje b) {
        if (a.getGenero().ordinal() != b.getGenero().ordinal()) return a.getGenero().ordinal() > b.getGenero().ordinal();
        return a.getNombre().compareToIgnoreCase(b.getNombre()) > 0;
    }

    private static boolean mismosIds(List<Personaje> a, List<Personaje> b) {
        for (int i = 0; i < a.size(); i++) if (a.get(i).getId() != b.get(i).getId()) return false;
        return true;
    }
}
