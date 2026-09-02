package adivinaquien.dominio;

import java.util.ArrayList;
import java.util.List;

public class CargaPersonajes {

    public static List<Personaje> crearTodos() {
        List<Personaje> personajes = new ArrayList<Personaje>();
        int id = 1;

        personajes.add(new Personaje(id++, "Naruto Uzumaki", Genero.M, ColorPelo.AMARILLO, false, true));
        personajes.add(new Personaje(id++, "Tsukishima Kei", Genero.M, ColorPelo.AMARILLO, true, true));
        personajes.add(new Personaje(id++, "Sasuke Uchiha", Genero.M, ColorPelo.NEGRO, false, true));
        personajes.add(new Personaje(id++, "Leorio", Genero.M, ColorPelo.NEGRO, true, true));
        personajes.add(new Personaje(id++, "Gaara", Genero.M, ColorPelo.COLORADO, false, true));
        personajes.add(new Personaje(id++, "Kakashi", Genero.M, ColorPelo.BLANCO, false, true));
        personajes.add(new Personaje(id++, "Gojo Satoru", Genero.M, ColorPelo.BLANCO, true, true));
        personajes.add(new Personaje(id++, "Saitama", Genero.M, ColorPelo.CALVO, false, true));
        personajes.add(new Personaje(id++, "Maestro Roshi", Genero.M, ColorPelo.CALVO, true, true));
        personajes.add(new Personaje(id++, "Goku", Genero.M, ColorPelo.NEGRO, false, false));
        personajes.add(new Personaje(id++, "Meruem", Genero.M, ColorPelo.CALVO, false, false));
        personajes.add(new Personaje(id++, "Shanks", Genero.M, ColorPelo.COLORADO, false, true));
        personajes.add(new Personaje(id++, "Douma", Genero.M, ColorPelo.BLANCO, false, false));
        personajes.add(new Personaje(id++, "Tsunade", Genero.F, ColorPelo.AMARILLO, false, true));
        personajes.add(new Personaje(id++, "Kalifa", Genero.F, ColorPelo.AMARILLO, true, true));
        personajes.add(new Personaje(id++, "Nico Robin", Genero.F, ColorPelo.NEGRO, false, true));
        personajes.add(new Personaje(id++, "Maki Zenin", Genero.F, ColorPelo.NEGRO, true, true));
        personajes.add(new Personaje(id++, "Nami", Genero.F, ColorPelo.COLORADO, false, true));
        personajes.add(new Personaje(id++, "Karin", Genero.F, ColorPelo.COLORADO, true, true));
        personajes.add(new Personaje(id++, "Nezuko", Genero.F, ColorPelo.NEGRO, false, false));
        personajes.add(new Personaje(id++, "Neferpitou", Genero.F, ColorPelo.BLANCO, false, false));
        personajes.add(new Personaje(id++, "Android 18", Genero.F, ColorPelo.AMARILLO, false, false));
        personajes.add(new Personaje(id++, "Big Mom", Genero.F, ColorPelo.COLORADO, false, true));

        return personajes;
    }
}
