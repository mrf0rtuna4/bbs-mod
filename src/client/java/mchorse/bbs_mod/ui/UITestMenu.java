package mchorse.bbs_mod.ui;

import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIRenderingContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.Direction;
import mchorse.bbs_mod.utils.colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class UITestMenu extends UIBaseMenu
{
    public UITextbox textbox;
    public UIButton submit;
    public UIButton insert;
    public UIColor color;

    public UITestMenu()
    {
        this.textbox = new UITextbox();
        this.submit = new UIButton(IKey.raw("Send"), (a) ->
        {
            MinecraftClient.getInstance().player.sendMessage(Text.literal(this.textbox.getText()));
        });
        this.insert = new UIButton(IKey.raw("Insert"), (a) ->
        {
            this.textbox.setText(MinecraftClient.getInstance().keyboard.getClipboard());
        });

        this.insert.tooltip(IKey.raw("Hello"), Direction.BOTTOM);
        this.insert.context((menu) ->
        {
            menu.action(Icons.ADD, IKey.raw("Hello"), () -> {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
                    IKey.raw("What's up!"),
                    IKey.raw("Hello, please input some text into me, my man...")
                );

                UIOverlay.addOverlay(this.context, panel);
            });
            menu.action(Icons.REMOVE, IKey.raw("Red hello"), Colors.RED, () -> {});
        });

        this.color = new UIColor((c) -> System.out.println(c));
        this.color.withAlpha();

        UIElement column = UI.column(this.textbox, UI.row(this.submit, this.insert), this.color);

        column.relative(this.viewport).xy(0.5F, 0.5F).w(200).anchor(0.5F, 0.5F);

        this.main.add(column);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("test");
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();

        context.batcher.gradientVBox(0, 0, this.width, 50, Colors.A50, 0);
        context.batcher.icon(Icons.CAMERA, 20, 20);
    }
}