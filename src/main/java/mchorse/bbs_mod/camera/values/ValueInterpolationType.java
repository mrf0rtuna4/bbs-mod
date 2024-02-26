package mchorse.bbs_mod.camera.values;

import mchorse.bbs_mod.camera.data.InterpolationType;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.StringType;
import mchorse.bbs_mod.settings.values.base.BaseValueBasic;

public class ValueInterpolationType extends BaseValueBasic<InterpolationType>
{
    public ValueInterpolationType(String id)
    {
        super(id, InterpolationType.HERMITE);
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value.name);
    }

    @Override
    public void fromData(BaseType base)
    {
        String key = base.asString();

        for (InterpolationType type : InterpolationType.values())
        {
            if (type.name.equals(key))
            {
                this.value = type;

                break;
            }
        }
    }
}