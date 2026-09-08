package adivinaquien.algoritmos;

import adivinaquien.dominio.ColorPelo;
import java.util.List;

public class CatalogoPreguntas {

    // Único lugar acoplado a los atributos concretos. Se arma una sola vez y se
    // comparte: las preguntas son fijas y Pregunta es inmutable (campos final, sin
    // setters), asi que no hay riesgo en que todos usen las mismas instancias.
    // La lista es inmutable a proposito: si alguien intentara modificarla estaria
    // afectando a todos los que la comparten.
    private final List<Pregunta> preguntas = List.of(
            new Pregunta(Pregunta.Criterio.ES_HOMBRE, null, "¿Es hombre?"),
            new Pregunta(Pregunta.Criterio.USA_LENTES, null, "¿Usa lentes?"),
            new Pregunta(Pregunta.Criterio.ES_HUMANO, null, "¿Es humano?"),
            new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.CALVO, "¿Es calvo?"),
            new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.COLORADO, "¿Tiene el pelo colorado?"),
            new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.NEGRO, "¿Tiene el pelo negro?"),
            new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.AMARILLO, "¿Tiene el pelo amarillo?"),
            new Pregunta(Pregunta.Criterio.COLOR_PELO, ColorPelo.BLANCO, "¿Tiene el pelo blanco?"));

    public List<Pregunta> todas() {
        return preguntas;
    }
}