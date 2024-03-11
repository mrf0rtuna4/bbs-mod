package mchorse.bbs_mod.forms.categories;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.UIFormList;
import mchorse.bbs_mod.ui.forms.categories.UIFormCategory;
import mchorse.bbs_mod.ui.utils.UIUtils;
import mchorse.bbs_mod.ui.utils.icons.Icons;

import java.io.File;

public class ModelFormCategory extends FormCategory
{
    public ModelFormCategory()
    {
        super(UIKeys.FORMS_CATEGORIES_MODELS);
    }

    @Override
    public void update()
    {
        super.update();

        this.forms.clear();

        File folder = BBSMod.getAssetsPath("models");

        folder.mkdirs();

        File[] files = folder.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isDirectory())
            {
                ModelForm form = new ModelForm();

                form.model.set(file.getName());
                this.forms.add(form);
            }
        }
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        UIFormCategory category = super.createUI(list);

        category.context((menu) ->
        {
            menu.action(Icons.FOLDER, UIKeys.FORMS_CATEGORIES_CONTEXT_OPEN_MODEL_FOLDER, () ->
            {
                ModelForm form = (ModelForm) category.selected;

                UIUtils.openFolder(BBSMod.getAssetsPath("models/" + form.model.get() + "/"));
            });
        });

        return category;
    }
}