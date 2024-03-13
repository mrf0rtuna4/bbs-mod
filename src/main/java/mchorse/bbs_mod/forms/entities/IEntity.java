package mchorse.bbs_mod.forms.entities;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Interface that provides access to an "Entity" within forms for rendering
 * and updating.
 */
public interface IEntity
{
    public World getWorld();

    public boolean isSneaking();

    public boolean isOnGround();

    public boolean isPunching();

    public int getAge();

    public void setAge(int ticks);

    double getFallDistance();

    public double getX();

    public double getY();

    public double getZ();

    public Vec3d getVelocity();

    public float getHeadYaw();

    public float getPrevHeadYaw();


    public float getPitch();

    public float getPrevPitch();

    public float getBodyYaw();

    public float getPrevBodyYaw();
}