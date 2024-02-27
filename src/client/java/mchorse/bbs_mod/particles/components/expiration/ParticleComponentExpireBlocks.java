package mchorse.bbs_mod.particles.components.expiration;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.math.molang.MolangException;
import mchorse.bbs_mod.math.molang.MolangParser;
import mchorse.bbs_mod.particles.components.ParticleComponentBase;
import mchorse.bbs_mod.particles.emitter.Particle;
import mchorse.bbs_mod.particles.emitter.ParticleEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleComponentExpireBlocks extends ParticleComponentBase
{
    public List<String> blocks = new ArrayList<>();

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (String block : this.blocks)
        {
            list.addString(block);
        }

        return list;
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isList())
        {
            return super.fromData(data, parser);
        }

        for (BaseType value : data.asList())
        {
            try
            {
                this.blocks.add(value.asString());
            }
            catch (Exception e)
            {}
        }

        return super.fromData(data, parser);
    }

    public BlockState getBlock(ParticleEmitter emitter, Particle particle)
    {
        if (emitter.world == null)
        {
            return null;
        }

        Vector3d position = particle.getGlobalPosition(emitter);

        return emitter.world.getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z));
    }
}