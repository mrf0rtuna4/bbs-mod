package mchorse.bbs_mod.forms.properties;

import mchorse.bbs_mod.cubic.animation.ActionsConfig;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.Form;

public class ActionsConfigProperty extends BaseProperty<ActionsConfig>
{
    public ActionsConfigProperty(Form form, String key, ActionsConfig value)
    {
        super(form, key, value);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.value.fromData(data.getMap(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), this.value.toData());
    }
}