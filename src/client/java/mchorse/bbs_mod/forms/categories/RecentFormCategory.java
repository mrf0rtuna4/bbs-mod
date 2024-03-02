package mchorse.bbs_mod.forms.categories;

import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.UIFormList;
import mchorse.bbs_mod.ui.forms.categories.UIFormCategory;
import mchorse.bbs_mod.ui.forms.categories.UIRecentFormCategory;

public class RecentFormCategory extends FormCategory
{
    public RecentFormCategory()
    {
        super(UIKeys.FORMS_CATEGORIES_RECENT);
    }

    @Override
    public boolean canModify(Form form)
    {
        return true;
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIRecentFormCategory(this, list);
    }
}