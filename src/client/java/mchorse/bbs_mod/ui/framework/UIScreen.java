package mchorse.bbs_mod.ui.framework;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class UIScreen extends Screen
{
    private UIBaseMenu menu;
    private UIRenderingContext context;

    public UIScreen(Text title, UIBaseMenu menu)
    {
        super(title);

        MinecraftClient mc = MinecraftClient.getInstance();

        this.menu = menu;
        this.context = new UIRenderingContext(new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers()));

        this.menu.context.setup(this.context);
    }

    public void update()
    {
        this.menu.update();
    }

    @Override
    public void removed()
    {
        super.removed();

        this.menu.onClose(null);
    }

    @Override
    public void onDisplayed()
    {
        super.onDisplayed();

        this.menu.onOpen(null);
    }

    @Override
    public boolean shouldPause()
    {
        return this.menu.canPause();
    }

    @Override
    protected void init()
    {
        super.init();

        this.menu.resize(this.width, this.height);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height)
    {
        super.resize(client, width, height);

        this.menu.resize(width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return this.menu.mouseClicked((int) mouseX, (int) mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        return this.menu.mouseScrolled((int) mouseX, (int) mouseY, (int) verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return this.menu.mouseReleased((int) mouseX, (int) mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.menu.handleKey(keyCode, scanCode, GLFW.GLFW_PRESS, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return this.menu.handleKey(keyCode, scanCode, GLFW.GLFW_RELEASE, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        this.menu.handleTextInput(chr);

        return true;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta)
    {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        this.menu.renderMenu(this.context, mouseX, mouseY);
    }
}