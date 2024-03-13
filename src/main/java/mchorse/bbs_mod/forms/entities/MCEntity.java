package mchorse.bbs_mod.forms.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MCEntity implements IEntity
{
    private Entity mcEntity;

    public MCEntity(Entity mcEntity)
    {
        this.mcEntity = mcEntity;
    }

    @Override
    public World getWorld()
    {
        return this.mcEntity.getWorld();
    }

    @Override
    public boolean isSneaking()
    {
        return this.mcEntity.isSneaking();
    }

    @Override
    public boolean isOnGround()
    {
        return this.mcEntity.isOnGround();
    }

    @Override
    public boolean isPunching()
    {
        return this.mcEntity instanceof LivingEntity ? ((LivingEntity) this.mcEntity).handSwingTicks >= 5 : false;
    }

    @Override
    public int getAge()
    {
        return this.mcEntity.age;
    }

    @Override
    public void setAge(int ticks)
    {
        this.mcEntity.age = ticks;
    }

    @Override
    public double getFallDistance()
    {
        return this.mcEntity.fallDistance;
    }

    @Override
    public double getX()
    {
        return this.mcEntity.getX();
    }

    @Override
    public double getY()
    {
        return this.mcEntity.getY();
    }

    @Override
    public double getZ()
    {
        return this.mcEntity.getZ();
    }

    @Override
    public Vec3d getVelocity()
    {
        return this.mcEntity.getVelocity();
    }

    @Override
    public float getHeadYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.getHeadYaw();
        }

        return this.mcEntity.getYaw();
    }

    @Override
    public float getPrevHeadYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.prevHeadYaw;
        }

        return this.mcEntity.prevYaw;
    }

    @Override
    public float getPitch()
    {
        return this.mcEntity.getPitch();
    }

    @Override
    public float getPrevPitch()
    {
        return this.mcEntity.prevPitch;
    }

    @Override
    public float getBodyYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.bodyYaw;
        }

        return this.getHeadYaw();
    }

    @Override
    public float getPrevBodyYaw()
    {
        if (this.mcEntity instanceof LivingEntity living)
        {
            return living.prevBodyYaw;
        }

        return this.getPrevHeadYaw();
    }
}