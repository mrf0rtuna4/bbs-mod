package mchorse.bbs_mod.graphics.text.format;

import mchorse.bbs_mod.graphics.text.FontRendererContext;

public class BoldFontFormat extends BaseFontFormat
{
    public BoldFontFormat()
    {
        super();
    }

    public BoldFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void reset()
    {}

    @Override
    public void apply(FontRendererContext context)
    {
        super.apply(context);

        context.bold = true;
    }
}