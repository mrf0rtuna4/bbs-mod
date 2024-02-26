package mchorse.bbs_mod.utils.keyframes.generic.factories;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.utils.math.IInterpolation;

public interface IGenericKeyframeFactory <T>
{
    public T fromData(BaseType data);

    public BaseType toData(T value);

    public T copy(T value);

    public T interpolate(T a, T b, IInterpolation interpolation, float x);

    // TODO: public UIKeyframeFactory<T> createUI(GenericKeyframe<T> keyframe, UIPropertyEditor editor);
}