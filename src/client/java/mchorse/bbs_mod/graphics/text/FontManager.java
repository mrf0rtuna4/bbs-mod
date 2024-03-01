package mchorse.bbs_mod.graphics.text;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.data.DataToString;
import mchorse.bbs_mod.resources.AssetProvider;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.IOUtils;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.watchdog.IWatchDogListener;
import mchorse.bbs_mod.utils.watchdog.WatchDogEvent;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FontManager implements IWatchDogListener
{
    public final Map<Link, FontRenderer> fontRenderers = new HashMap<>();

    private AssetProvider provider;

    public FontManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    public Set<Link> getFontSet()
    {
        Set<Link> linkSet = new HashSet<>();
        File fonts = BBSMod.getAssetsPath("fonts");

        linkSet.add(Link.assets("fonts/bbs_round.json"));
        linkSet.add(Link.assets("fonts/bbs_round_mono.json"));
        linkSet.add(Link.assets("fonts/bbs_square.json"));

        fonts.mkdirs();

        for (File file : fonts.listFiles())
        {
            if (file.getName().endsWith(".json"))
            {
                linkSet.add(Link.assets("fonts/" + file.getName()));
            }
        }

        return linkSet;
    }

    public FontRenderer getRenderer(Link link)
    {
        if (!this.fontRenderers.containsKey(link))
        {
            this.fontRenderers.put(link, this.loadFont(link));
        }

        return this.fontRenderers.get(link);
    }

    private FontRenderer loadFont(Link link)
    {
        try
        {
            InputStream fontData = this.provider.getAsset(link);
            Font font = Font.fromMap(DataToString.mapFromString(IOUtils.readText(fontData)));
            Link texture = new Link(link.source, StringUtils.replaceExtension(link.path, "png"));

            System.out.println("Font \"" + link + "\" was loaded!");

            return new FontRenderer(texture, font);
        }
        catch (Exception e)
        {
            System.err.println("Failed to load font renderer \"" + link + "\"!");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBSMod.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        FontRenderer fontRenderer = this.fontRenderers.get(link);

        if (fontRenderer != null)
        {
            FontRenderer newFontRenderer = this.loadFont(link);

            if (newFontRenderer != null)
            {
                fontRenderer.update(newFontRenderer);
            }
        }
        else
        {
            this.fontRenderers.remove(link);
        }
    }
}