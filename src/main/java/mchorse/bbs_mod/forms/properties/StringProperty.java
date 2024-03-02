package mchorse.bbs_mod.forms.properties;

import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.generic.factories.KeyframeFactories;

public class StringProperty extends BaseProperty<String>
{
    public StringProperty(Form form, String key, String value)
    {
        super(form, key, value);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getString(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putString(this.getKey(), this.value);
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel(String key)
    {
        return new GenericKeyframeChannel(key, KeyframeFactories.STRING);
    }
}