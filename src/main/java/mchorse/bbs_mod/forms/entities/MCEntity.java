package mchorse.bbs_mod.forms.entities;

import net.minecraft.entity.Entity;

public class MCEntity implements IEntity
{
    private Entity mcEntity;

    public MCEntity(Entity mcEntity)
    {
        this.mcEntity = mcEntity;
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
}