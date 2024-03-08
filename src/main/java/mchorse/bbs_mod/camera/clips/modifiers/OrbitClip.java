package mchorse.bbs_mod.camera.clips.modifiers;

import mchorse.bbs_mod.camera.data.Angle;
import mchorse.bbs_mod.camera.data.Point;
import mchorse.bbs_mod.camera.data.Position;
import mchorse.bbs_mod.settings.values.ValueBoolean;
import mchorse.bbs_mod.settings.values.ValueFloat;
import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.utils.clips.ClipContext;
import mchorse.bbs_mod.utils.joml.Matrices;
import mchorse.bbs_mod.utils.math.Interpolations;
import mchorse.bbs_mod.utils.math.MathUtils;
import net.minecraft.entity.Entity;
import org.joml.Vector3f;

/**
 * Orbit modifier
 * 
 * This modifier is responsible for making the camera orbit around
 * the given entity with given yaw and pitch.
 */
public class OrbitClip extends EntityClip
{
    /**
     * In addition, copy yaw and pitch from entity
     */
    public final ValueBoolean copy = new ValueBoolean("copy");

    /**
     * How far away to orbit from the entity
     */
    public final ValueFloat distance = new ValueFloat("distance", 0F);

    /**
     * Yaw to be added to orbit
     */
    public final ValueFloat yaw = new ValueFloat("yaw", 0F);

    /**
     * Pitch to be added to orbit
     */
    public final ValueFloat pitch = new ValueFloat("pitch", 0F);

    public OrbitClip()
    {
        super();

        this.add(this.copy);
        this.add(this.distance);
        this.add(this.yaw);
        this.add(this.pitch);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        if (this.checkForDead())
        {
            this.tryFindingEntity(context.world);
        }

        if (this.entities == null)
        {
            return;
        }

        if (!context.applyUnderneath(this.tick.get(), 0, this.position))
        {
            this.position.copy(position);
        }

        float yaw = this.yaw.get() + (position.angle.yaw - this.position.angle.yaw);
        float pitch = this.pitch.get() + (position.angle.pitch - this.position.angle.pitch);
        float distance = this.distance.get() + (float) (position.point.z - this.position.point.z);
        Entity entity = this.entities.get(0);
        Vector3f vector = Matrices.rotation(MathUtils.toRad(pitch), MathUtils.toRad(-yaw));

        if (this.copy.get())
        {
            float entityYaw = MathUtils.toDeg(Interpolations.lerp(entity.prevYaw, entity.getYaw(), context.transition));
            float entityPitch = MathUtils.toDeg(Interpolations.lerp(entity.prevPitch, entity.getPitch(), context.transition));

            Matrices.rotate(vector, MathUtils.toRad(-entityPitch), MathUtils.toRad(-entityYaw));
        }

        Point offset = this.offset.get();
        double x = Interpolations.lerp(entity.prevX, entity.getX(), context.transition) + offset.x;
        double y = Interpolations.lerp(entity.prevY, entity.getY(), context.transition) + offset.y;
        double z = Interpolations.lerp(entity.prevZ, entity.getZ(), context.transition) + offset.z;

        vector.mul(distance);

        double fX = x + vector.x;
        double fY = y + vector.y;
        double fZ = z + vector.z;
        Angle angle = Angle.angle(x - fX, y - fY, z - fZ);

        position.point.set(fX, fY, fZ);
        position.angle.set(angle.yaw, angle.pitch);
    }

    @Override
    public Clip create()
    {
        return new OrbitClip();
    }
}