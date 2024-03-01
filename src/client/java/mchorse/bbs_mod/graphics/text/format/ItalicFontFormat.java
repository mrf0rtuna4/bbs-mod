package mchorse.bbs_mod.graphics.text.format;

import mchorse.bbs_mod.graphics.text.FontRendererContext;

public class ItalicFontFormat extends AmountFontFormat
{
    public ItalicFontFormat()
    {}

    public ItalicFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void apply(FontRendererContext context)
    {
        super.apply(context);

        context.skew = this.amount;
    }
}