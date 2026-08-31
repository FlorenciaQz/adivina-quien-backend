package adivinaquien.dominio;

public final class Personaje {

    private final int id;
    private final String nombre;
    private final Genero genero;
    private final ColorPelo pelo;
    private final boolean lentes;
    private final boolean humano;

    public Personaje(int id, String nombre, Genero genero,
                      ColorPelo pelo, boolean lentes, boolean humano) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.pelo = pelo;
        this.lentes = lentes;
        this.humano = humano;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Genero getGenero() {
        return genero;
    }

    public ColorPelo getPelo() {
        return pelo;
    }

    public boolean tieneLentes() {
        return lentes;
    }

    public boolean esHumano() {
        return humano;
    }

    public String descripcionAtributos() {
        String generoTexto = this.genero == Genero.M ? "hombre" : "mujer";
        String tipo = this.humano ? "humano" : "no humano";
        String lentesTexto = this.lentes ? "con lentes" : "sin lentes";
        return generoTexto + ", pelo " + this.pelo + ", " + lentesTexto + ", " + tipo;
    }
}
