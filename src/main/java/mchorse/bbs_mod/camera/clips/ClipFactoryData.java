package mchorse.bbs_mod.camera.clips;

import mchorse.bbs_mod.camera.clips.converters.IClipConverter;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.utils.factory.UIFactoryData;

import java.util.HashMap;
import java.util.Map;

public class ClipFactoryData extends UIFactoryData /* TODO: <? extends UIClip> */
{
    public final Map<Link, IClipConverter<? extends Clip, ? extends Clip>> converters = new HashMap<>();
    public final Icon icon;

    public ClipFactoryData(Icon icon, int color, Class<?> panelUI)
    {
        super(color, panelUI);

        this.icon = icon;
    }

    public ClipFactoryData withConverter(Link to, IClipConverter<? extends Clip, ? extends Clip> converter)
    {
        this.converters.put(to, converter);

        return this;
    }
}