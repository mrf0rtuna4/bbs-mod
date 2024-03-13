package mchorse.bbs_mod.supporters;

import mchorse.bbs_mod.resources.Link;

public class Supporter
{
    public final String name;
    public final String link;
    public final Link banner;

    public Supporter(String name)
    {
        this(name, "...");
    }

    public Supporter(String name, String link)
    {
        this(name, link, "...");
    }

    public Supporter(String name, String link, String banner)
    {
        this(name, link, Link.create(banner));
    }

    public Supporter(String name, String link, Link banner)
    {
        this.name = name;
        this.link = link;
        this.banner = banner;
    }

    public boolean hasOnlyName()
    {
        return this.link.isEmpty() && this.banner == null;
    }

    public boolean hasNoBanner()
    {
        return !this.link.isEmpty() && this.banner == null;
    }

    public boolean hasBanner()
    {
        return this.banner != null;
    }
}