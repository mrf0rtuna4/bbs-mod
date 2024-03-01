package mchorse.bbs_mod.ui.film.clips.renderer;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.audio.SoundBuffer;
import mchorse.bbs_mod.camera.clips.misc.AudioClip;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.utils.Area;
import mchorse.bbs_mod.utils.colors.Colors;

public class UIAudioClipRenderer extends UIClipRenderer<AudioClip>
{
    @Override
    protected void renderBackground(UIContext context, int color, AudioClip clip, Area area, boolean selected, boolean current)
    {
        Link link = clip.audio.get();

        if (link != null)
        {
            SoundBuffer player = BBSModClient.getSounds().get(link, true);

            if (player != null)
            {
                context.batcher.box(area.x, area.y, area.ex(), area.ey(), Colors.mulRGB(color, 0.6F));
                player.getWaveform().render(context.batcher, Colors.WHITE, area.x, area.y, area.w, area.h, 0, clip.duration.get() / 20F);
            }
        }
        else
        {
            super.renderBackground(context, color, clip, area, selected, current);
        }
    }
}