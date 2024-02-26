package mchorse.bbs_mod.settings.values;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.StringType;
import mchorse.bbs_mod.settings.values.base.BaseValueBasic;
import mchorse.bbs_mod.settings.values.base.IValueUIProvider;

public class ValueString extends BaseValueBasic<String> implements IValueUIProvider
{
    public ValueString(String id, String defaultValue)
    {
        super(id, defaultValue);
    }

//    @Override
//    public List<UIElement> getFields(UIElement ui)
//    {
//        UITextbox textbox = UIValueFactory.stringUI(this, null);
//
//        textbox.w(90);
//
//        return Arrays.asList(UIValueFactory.column(textbox, this));
//    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (BaseType.isString(data))
        {
            this.value = data.asString();
        }
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}