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

    public int getRiesgo() {
        return riesgo;
    }

    public EstrategiaPreguntas getEstrategia() {
        return estrategia;
    }
}
