package mchorse.bbs_mod.settings.values;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ByteType;
import mchorse.bbs_mod.settings.values.base.BaseValueBasic;
import mchorse.bbs_mod.settings.values.base.IValueUIProvider;

public class ValueBoolean extends BaseValueBasic<Boolean> implements IValueUIProvider
{
    public ValueBoolean(String id)
    {
        this(id, false);
    }

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id, defaultValue);
    }

//    @Override
//    public List<UIElement> getFields(UIElement ui)
//    {
//        UIToggle toggle = UIValueFactory.booleanUI(this, null);
//
//        toggle.resetFlex();
//
//        return Arrays.asList(toggle);
//    }

    @Override
    public BaseType toData()
    {
        return new ByteType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.value = data.asNumeric().boolValue();
        }
    }

    @Override
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
