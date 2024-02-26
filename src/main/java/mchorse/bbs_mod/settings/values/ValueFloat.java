package mchorse.bbs_mod.settings.values;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.FloatType;
import mchorse.bbs_mod.settings.values.base.BaseValueNumber;
import mchorse.bbs_mod.settings.values.base.IValueUIProvider;
import mchorse.bbs_mod.utils.math.MathUtils;

public class ValueFloat extends BaseValueNumber<Float> implements IValueUIProvider
{
    public ValueFloat(String id, Float defaultValue)
    {
        this(id, defaultValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, Float defaultValue, Float min, Float max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    protected Float clamp(Float value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

//    @Override
//    public List<UIElement> getFields(UIElement ui)
//    {
//        UITrackpad trackpad = UIValueFactory.floatUI(this, null);
//
//        trackpad.w(90);
//
//        return Arrays.asList(UIValueFactory.column(trackpad, this));
//    }

    @Override
    public BaseType toData()
    {
        return new FloatType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.value = data.asNumeric().floatValue();
        }
    }

    @Override
    public String toString()
    {
        return Float.toString(this.value);
    }
}