package mchorse.bbs_mod.ui;

import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIRenderingContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.Direction;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class UITestMenu extends UIBaseMenu
{
    public UITestMenu()
    {
        UITextbox textbox = new UITextbox();
        UIButton submit = new UIButton(IKey.raw("Send"), (a) ->
        {
            MinecraftClient.getInstance().player.sendMessage(Text.literal(textbox.getText()));
        });
        UIButton insert = new UIButton(IKey.raw("Insert"), (a) ->
        {
            textbox.setText(MinecraftClient.getInstance().keyboard.getClipboard());
        });

        insert.tooltip(IKey.raw("Hello"), Direction.BOTTOM);
        insert.context((menu) ->
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

        UIColor color = new UIColor((c) -> System.out.println(c));

        color.withAlpha();

        UIElement column = UI.column(textbox, UI.row(submit, insert), color);

        column.relative(this.viewport).xy(0.5F, 0.5F).w(200).anchor(0.5F, 0.5F);
        column.setVisible(false);

        UIKeyframesEditor editor = new UIKeyframesEditor()
        {
            @Override
            protected UIKeyframes createElement()
            {
                return new UIKeyframes(this::fillData);
            }
        };

        editor.keyframes.sheets.add(new UISheet("id", IKey.raw("Hello"), Colors.RED, new KeyframeChannel()));
        editor.full().relative(this.viewport);

        this.main.add(column, editor);
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