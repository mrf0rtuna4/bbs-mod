package mchorse.bbs_mod.forms.entities;

public class StubEntity implements IEntity
{
    private int age;

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
}