package mchorse.bbs_mod.utils.keyframes.generic.factories;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.FloatType;
import mchorse.bbs_mod.utils.math.IInterpolation;

public class FloatKeyframeFactory implements IGenericKeyframeFactory<Float>
{
    @Override
    public Float fromData(BaseType data)
    {
        return data.isNumeric() ? data.asNumeric().floatValue() : 0F;
    }

    @Override
    public BaseType toData(Float value)
    {
        return new FloatType(value);
    }

    @Override
    public Float copy(Float value)
    {
        return value;
    }

    @Override
    public Float interpolate(Float a, Float b, IInterpolation interpolation, float x)
    {
        return interpolation.interpolate(a, b, x);
    }
}