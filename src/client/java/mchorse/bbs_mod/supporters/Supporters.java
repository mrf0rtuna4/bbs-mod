package mchorse.bbs_mod.supporters;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.utils.resources.LinkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Supporters
{
    private List<Supporter> supporters = new ArrayList<>();

    public void setup(MapType data)
    {
        ListType supporters = data.getList("supporters");

        for (BaseType s : supporters)
        {
            MapType supporter = s.asMap();

            this.supporters.add(new Supporter(
                supporter.getString("name"),
                supporter.getString("link"),
                LinkUtils.create(supporter.get("banner"))
            ));
        }
    }

    public List<Supporter> getBBSEarlyAccessSupporters()
    {
        return this.supporters.stream().filter(Supporter::hasOnlyName).collect(Collectors.toList());
    }

    public List<Supporter> getSuperSupporters()
    {
        return this.supporters.stream().filter(Supporter::hasNoBanner).collect(Collectors.toList());
    }

    public List<Supporter> getCCSupporters()
    {
        return this.supporters.stream().filter(Supporter::hasBanner).collect(Collectors.toList());
    }
}