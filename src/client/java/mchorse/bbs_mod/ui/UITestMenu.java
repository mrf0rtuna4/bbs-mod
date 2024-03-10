package mchorse.bbs_mod.ui;

import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.UIRenderingContext;
import mchorse.bbs_mod.ui.framework.elements.utils.UIModelRenderer;

public class UITestMenu extends UIBaseMenu
{
    public UIModelRenderer modelRenderer;

    public UITestMenu()
    {
        super();

        this.modelRenderer = new UIModelRenderer() {
            @Override
            protected void renderUserModel(UIContext context)
            {

            }
        };

        this.modelRenderer.relative(this.viewport).full();

        this.main.add(this.modelRenderer);
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();
    }
}