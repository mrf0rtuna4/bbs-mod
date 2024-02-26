package mchorse.bbs_mod.audio;

import mchorse.bbs_mod.audio.ogg.VorbisReader;
import mchorse.bbs_mod.audio.wav.WaveReader;
import mchorse.bbs_mod.resources.AssetProvider;
import mchorse.bbs_mod.resources.Link;

public class AudioReader
{
    public static Wave read(AssetProvider provider, Link link) throws Exception
    {
        if (link.path.endsWith(".wav"))
        {
            return new WaveReader().read(provider.getAsset(link));
        }
        else if (link.path.endsWith(".ogg"))
        {
            return VorbisReader.read(link, provider.getAsset(link));
        }

        throw new IllegalStateException("Given link " + link + " isn't a Wave or a Vorbis file!");
    }
}