package mchorse.bbs_mod.graphics.text.builders;

import mchorse.bbs_mod.utils.colors.Color;

public abstract class BaseColoredTextBuilder implements ITextBuilder
{
    protected boolean multiply;
    protected Color color = new Color();

    @Override
    public void setMultiplicative(boolean multiplicative)
    {
        this.multiply = multiplicative;
    }
}
