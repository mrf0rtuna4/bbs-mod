package mchorse.bbs_mod.ui.forms.editors.forms;

import mchorse.bbs_mod.forms.forms.ExtrudedForm;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.forms.editors.panels.UIExtrudedFormPanel;
import mchorse.bbs_mod.ui.utils.icons.Icons;

public class UIExtrudedForm extends UIForm<ExtrudedForm>
{
    public UIExtrudedForm()
    {
        super();

        this.defaultPanel = new UIExtrudedFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_EXTRUDED_TITLE, Icons.MATERIAL);
        this.registerDefaultPanels();
    }
}