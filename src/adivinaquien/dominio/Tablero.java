package adivinaquien.dominio;

import java.util.ArrayList;
import java.util.List;

public class Tablero {

    private final List<Personaje> personajes;

    public Tablero(List<Personaje> personajes) {
        this.personajes = mergeSort(new ArrayList<Personaje>(personajes));
    }

    // copia defensiva de la lista ordenada.
    public List<Personaje> personajes() {
        return new ArrayList<Personaje>(personajes);
    }

    // D&C + recursión: parte la lista al medio (divide), ordena cada mitad
    // por separado (conquista), mezcla las dos mitades ya ordenadas (combina).
    // Caso base: lista de 0 o 1 elementos, ya está ordenada.
    private static List<Personaje> mergeSort(List<Personaje> lista) {
        if (lista.size() <= 1) {
            return lista;
        }
        int medio = lista.size() / 2;
        List<Personaje> izquierda = mergeSort(new ArrayList<Personaje>(lista.subList(0, medio)));
        List<Personaje> derecha = mergeSort(new ArrayList<Personaje>(lista.subList(medio, lista.size())));
        return mezclar(izquierda, derecha);
    }

    private static List<Personaje> mezclar(List<Personaje> izquierda, List<Personaje> derecha) {
        List<Personaje> resultado = new ArrayList<Personaje>();
        int i = 0;
        int j = 0;
        while (i < izquierda.size() && j < derecha.size()) {
            if (esMayor(izquierda.get(i), derecha.get(j))) {
                resultado.add(derecha.get(j));
                j++;
            } else {
                resultado.add(izquierda.get(i));
                i++;
            }
        }
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i));
            i++;
        }
        while (j < derecha.size()) {
            resultado.add(derecha.get(j));
            j++;
        }
        return resultado;
    }

    // orden del tablero: por género y, dentro del mismo género, alfabético.
    // true si a va DESPUÉS de b.
    private static boolean esMayor(Personaje a, Personaje b) {
        if (a.getGenero().ordinal() != b.getGenero().ordinal()) {
            return a.getGenero().ordinal() > b.getGenero().ordinal();
        }
        return a.getNombre().compareToIgnoreCase(b.getNombre()) > 0;
    }
}
