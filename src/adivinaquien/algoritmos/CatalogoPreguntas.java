package adivinaquien.algoritmos;

import adivinaquien.dominio.ColorPelo;
import java.util.ArrayList;
import java.util.List;

public class CatalogoPreguntas {

    // Único lugar acoplado a los atributos concretos.
    public List<Pregunta> todas() {
        List<Pregunta> preguntas = new ArrayList<Pregunta>();
        preguntas.add(new Pregunta(Pregunta.Criterio.ES_HOMBRE, null, "¿Es hombre?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.USA_LENTES, null, "¿Usa lentes?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.ES_HUMANO, null, "¿Es humano?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.CALVO, "¿Es calvo?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.COLORADO, "¿Tiene el pelo colorado?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.NEGRO, "¿Tiene el pelo negro?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.AMARILLO, "¿Tiene el pelo amarillo?"));
        preguntas.add(new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.BLANCO, "¿Tiene el pelo blanco?"));
        return preguntas;
    }
}
