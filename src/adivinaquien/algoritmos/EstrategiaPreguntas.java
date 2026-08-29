package adivinaquien.algoritmos;

import adivinaquien.dominio.Personaje;
import java.util.List;

// Dos implementaciones reales: EstrategiaGreedy (busca la pregunta que divide
// más parejo) y EstrategiaDesbalanceada (busca la que divide más desparejo).
// Permite que cada Maquina juegue con una heurística distinta y se puedan
// comparar.
public interface EstrategiaPreguntas {
    Pregunta mejorPregunta(List<Personaje> candidatos);
}
