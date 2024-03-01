package mchorse.bbs_mod;

import mchorse.bbs_mod.film.FilmManager;
import mchorse.bbs_mod.particles.ParticleManager;

import java.io.File;

public class BBSData
{
    private static ParticleManager particles;
    private static FilmManager films;

    public static ParticleManager getParticles()
    {
        return particles;
    }

    public static FilmManager getFilms()
    {
        return films;
    }

    public static void load(File folder)
    {
        particles = new ParticleManager(new File(folder, "particles"));
        films = new FilmManager(new File(folder, "films"));
    }

    public static void delete()
    {
        particles = null;
        films = null;
    }
}