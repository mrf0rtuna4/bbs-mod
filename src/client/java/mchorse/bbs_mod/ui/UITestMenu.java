package mchorse.bbs_mod.ui;

import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class UITestMenu extends UIBaseMenu
{
    public UITextbox textbox;
    public UIButton submit;
    public UIButton insert;

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

        UIElement column = UI.column(this.textbox, UI.row(this.submit, this.insert));

        column.relative(this.viewport).xy(0.5F, 0.5F).w(200).anchor(0.5F, 0.5F);

        this.getRoot().add(column);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("test");
    }
}