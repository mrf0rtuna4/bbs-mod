package mchorse.bbs_mod.forms.forms;

import mchorse.bbs_mod.forms.properties.LinkProperty;
import mchorse.bbs_mod.resources.Link;

public class ExtrudedForm extends Form
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);

    public ExtrudedForm()
    {
        super();

        this.register(this.texture);
    }

    @Override
    public String getDefaultDisplayName()
    {
        Link link = this.texture.get();

        return link == null ? "none" : link.toString();
    }
}