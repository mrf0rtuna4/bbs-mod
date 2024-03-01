package mchorse.bbs_mod.graphics.text.format;

import mchorse.bbs_mod.data.IMapSerializable;
import mchorse.bbs_mod.graphics.text.FontRendererContext;

public interface IFontFormat extends IMapSerializable
{
    public char getControlCharacter();

    public void setControlCharacter(String string);

    public void reset();

    public void apply(FontRendererContext context);

    public void process(FontRendererContext context);
}