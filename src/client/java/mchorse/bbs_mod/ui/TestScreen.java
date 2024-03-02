package mchorse.bbs_mod.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestScreen extends Screen
{
    private TextFieldWidget textBox;
    private ButtonWidget submit;
    private ButtonWidget insert;

    private List<UIRect> rectangles = new ArrayList<>();
    private long lastTime = System.currentTimeMillis();

    public TestScreen(Text title)
    {
        super(title);
    }

    @Override
    protected void init()
    {
        super.init();

        int w = 200;
        int x = (this.width - w) / 2;
        int y = this.height / 2;

        this.textBox = new TextFieldWidget(this.client.advanceValidatingTextRenderer, x, y - 25, w, 20, Text.literal("Test"));
        this.submit = ButtonWidget.builder(Text.literal("Send"), (a) ->
        {
            this.client.player.sendMessage(Text.literal(this.textBox.getText()));
        }).dimensions(x, y, w / 2 - 2, 20).build();
        this.insert = ButtonWidget.builder(Text.literal("Insert"), (a) ->
        {
            this.textBox.setText(this.client.keyboard.getClipboard());
        }).dimensions(x + w / 2 + 2, y, w / 2 - 2, 20).build();

        this.addSelectableChild(this.textBox);
        this.addSelectableChild(this.submit);
        this.addSelectableChild(this.insert);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == 0)
        {
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            int random = ColorHelper.Argb.getArgb(255, red, green, blue);

            this.rectangles.add(new UIRect((int) mouseX, (int) mouseY, random));

            // return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        System.out.println("Draggin your " + mouseX + " " + mouseX);

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        long time = System.currentTimeMillis();
        float diff = (time - this.lastTime) / 1000F;

        super.render(context, mouseX, mouseY, delta);

        int width = this.client.textRenderer.getWidth("Title");

        context.drawText(this.client.textRenderer, "Title", (this.width - width) / 2, 20, 0xffffffff, true);

        this.textBox.render(context, mouseX, mouseY, delta);
        this.submit.render(context, mouseX, mouseY, delta);
        this.insert.render(context, mouseX, mouseY, delta);

        Iterator<UIRect> it = this.rectangles.iterator();

        VertexConsumer buffer = context.getVertexConsumers().getBuffer(RenderLayer.getDebugLineStrip(5F));

        while (it.hasNext())
        {
            UIRect next = it.next();

            next.time -= diff;

            if (next.time < 0)
            {
                it.remove();
            }
            else
            {
                buffer.vertex(next.pos.x, next.pos.y, 100).color(next.color).normal(0F, 0F, 1F).next();
            }
        }

        context.draw();

        for (UIRect rect : this.rectangles)
        {
            context.fill(rect.pos.x - 10, rect.pos.y - 10, rect.pos.x + 10, rect.pos.y + 10, rect.color);
        }

        context.drawTexture(new Identifier("bbs:icon.png"), 0, 0, 0, 0, 40, 40, 40, 40);

        this.lastTime = time;
    }

    public static class UIRect
    {
        public Vector2i pos = new Vector2i();
        public int color;
        public float time = 10;

        public UIRect(int x, int y, int color)
        {
            this.pos.set(x, y);
            this.color = color;
        }
    }
}