package adivinaquien.juego;

import adivinaquien.algoritmos.EstrategiaPreguntas;

public final class Maquina {

    private final String nombre;
    private final int riesgo;
    private final EstrategiaPreguntas estrategia;

    public Maquina(String nombre, int riesgo, EstrategiaPreguntas estrategia) {
        this.nombre = nombre;
        this.riesgo = riesgo;
        this.estrategia = estrategia;
    }

    public String getNombre() {
        return nombre;
    }

    // arriesga si la probabilidad de acertar de una ya supera el umbral que marca su riesgo (0..100)
    public boolean decideArriesgar(int cantidadCandidatos) {
        double probAcierto = 1.0 / cantidadCandidatos;
        double umbral = 1.0 - (riesgo / 100.0);
        return probAcierto >= umbral;
    }

    public EstrategiaPreguntas getEstrategia() {
        return estrategia;
    }
}
