package mchorse.bbs_mod.graphics.text.format;

import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.graphics.text.FontRenderer;
import mchorse.bbs_mod.graphics.text.FontRendererContext;

public abstract class BaseFontFormat implements IFontFormat
{
    protected char control;

    public BaseFontFormat()
    {}

    public BaseFontFormat(char control)
    {
        this.control = control;
    }

    @Override
    public char getControlCharacter()
    {
        return this.control;
    }

    @Override
    public void setControlCharacter(String string)
    {
        if (!string.isEmpty())
        {
            this.control = string.charAt(0);
        }
    }

    @Override
    public void apply(FontRendererContext context)
    {
        context.activeFormats.add(this);
    }

    @Override
    public void process(FontRendererContext context)
    {}

    @Override
    public String toString()
    {
        return FontRenderer.FORMATTING_STRING + this.control;
    }

    @Override
    public void fromData(MapType data)
    {}

    @Override
    public void toData(MapType data)
    {}
}