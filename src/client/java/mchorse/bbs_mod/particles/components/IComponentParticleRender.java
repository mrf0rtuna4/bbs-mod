package mchorse.bbs_mod.particles.components;

import mchorse.bbs_mod.particles.emitter.Particle;
import mchorse.bbs_mod.particles.emitter.ParticleEmitter;
import net.minecraft.client.render.BufferBuilder;

public interface IComponentParticleRender extends IComponentBase
{
    public void preRender(ParticleEmitter emitter, float transition);

    public void render(ParticleEmitter emitter, Particle particle, BufferBuilder builder, float transition);

    public void renderUI(Particle particle, BufferBuilder builder, float transition);

    public void postRender(ParticleEmitter emitter, float transition);
}