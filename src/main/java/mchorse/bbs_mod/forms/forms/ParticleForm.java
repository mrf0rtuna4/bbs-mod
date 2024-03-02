package mchorse.bbs_mod.forms.forms;

import mchorse.bbs_mod.forms.properties.BooleanProperty;
import mchorse.bbs_mod.forms.properties.LinkProperty;
import mchorse.bbs_mod.forms.properties.StringProperty;

public class ParticleForm extends Form
{
    public StringProperty effect = new StringProperty(this, "effect", null);
    public BooleanProperty paused = new BooleanProperty(this, "paused", false);
    public LinkProperty texture = new LinkProperty(this, "texture", null);

    public ParticleForm()
    {
        super();

        this.effect.cantAnimate();

        this.register(this.effect);
        this.register(this.paused);
        this.register(this.texture);
    }

    @Override
    public String getDefaultDisplayName()
    {
        String effect = this.effect.get();

        return effect == null || effect.isEmpty() ? "none" : effect.toString();
    }
}