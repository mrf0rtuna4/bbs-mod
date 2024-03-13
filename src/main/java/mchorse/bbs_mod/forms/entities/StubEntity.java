package mchorse.bbs_mod.forms.entities;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StubEntity implements IEntity
{
    private World world;
    private int age;

    public StubEntity(World world)
    {
        this.world = world;
    }

    public StubEntity()
    {}

    @Override
    public World getWorld()
    {
        return this.world;
    }

    @Override
    public boolean isSneaking()
    {
        return false;
    }

    @Override
    public boolean isOnGround()
    {
        return true;
    }

    @Override
    public boolean isPunching()
    {
        return false;
    }

    @Override
    public int getAge()
    {
        return this.age;
    }

    @Override
    public void setAge(int ticks)
    {
        this.age = ticks;
    }

    @Override
    public double getFallDistance()
    {
        return 0;
    }

    @Override
    public double getX()
    {
        return 0;
    }

    @Override
    public double getY()
    {
        return 0;
    }

    @Override
    public double getZ()
    {
        return 0;
    }

    @Override
    public Vec3d getVelocity()
    {
        return Vec3d.ZERO;
    }

    @Override
    public float getHeadYaw()
    {
        return 0;
    }

    @Override
    public float getPrevHeadYaw()
    {
        return 0;
    }

    @Override
    public float getPitch()
    {
        return 0;
    }

    @Override
    public float getPrevPitch()
    {
        return 0;
    }

    @Override
    public float getBodyYaw()
    {
        return 0;
    }

    @Override
    public float getPrevBodyYaw()
    {
        return 0;
    }
}