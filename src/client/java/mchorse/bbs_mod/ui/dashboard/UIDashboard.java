package mchorse.bbs_mod.ui.dashboard;

import mchorse.bbs_mod.BBS;
import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.bridge.IBridge;
import mchorse.bbs_mod.bridge.IBridgeCamera;
import mchorse.bbs_mod.bridge.IBridgeWorld;
import mchorse.bbs_mod.camera.OrbitCamera;
import mchorse.bbs_mod.camera.controller.OrbitCameraController;
import mchorse.bbs_mod.events.register.RegisterDashboardPanels;
import mchorse.bbs_mod.graphics.Draw;
import mchorse.bbs_mod.graphics.GLStates;
import mchorse.bbs_mod.graphics.MatrixStack;
import mchorse.bbs_mod.graphics.RenderingContext;
import mchorse.bbs_mod.graphics.shaders.CommonShaderAccess;
import mchorse.bbs_mod.graphics.shaders.Shader;
import mchorse.bbs_mod.graphics.text.FontRenderer;
import mchorse.bbs_mod.graphics.text.builders.ColoredTextBuilder3D;
import mchorse.bbs_mod.graphics.text.builders.ITextBuilder;
import mchorse.bbs_mod.graphics.vao.VAO;
import mchorse.bbs_mod.graphics.vao.VAOBuilder;
import mchorse.bbs_mod.graphics.vao.VBOAttributes;
import mchorse.bbs_mod.graphics.window.Window;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.settings.ui.UISettingsOverlayPanel;
import mchorse.bbs_mod.ui.Keys;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs_mod.ui.dashboard.panels.UIDashboardPanels;
import mchorse.bbs_mod.ui.dashboard.textures.UITextureManagerPanel;
import mchorse.bbs_mod.ui.dashboard.utils.UIGraphPanel;
import mchorse.bbs_mod.ui.dashboard.utils.UIOrbitCamera;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.font.UIFontPanel;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIRenderingContext;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.particles.UIParticleSchemePanel;
import mchorse.bbs_mod.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs_mod.ui.utils.UIChalkboard;
import mchorse.bbs_mod.ui.utils.UIUtils;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.ui.world.UIWorldEditorPanel;
import mchorse.bbs_mod.ui.world.entities.UIEntitiesPanel;
import mchorse.bbs_mod.ui.world.objects.UIWorldObjectsPanel;
import mchorse.bbs_mod.ui.world.settings.UIWorldSettingsOverlayPanel;
import mchorse.bbs_mod.ui.world.worlds.UIWorldsOverlayPanel;
import mchorse.bbs_mod.utils.Direction;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.joml.Matrices;
import mchorse.bbs_mod.utils.math.Interpolations;
import mchorse.bbs_mod.utils.math.MathUtils;
import mchorse.bbs_mod.world.World;
import mchorse.bbs_mod.world.entities.Entity;
import mchorse.bbs_mod.world.entities.architect.EntityArchitect;
import mchorse.bbs_mod.world.entities.components.BasicComponent;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class UIDashboard extends UIBaseMenu
{
    private UIDashboardPanels panels;

    public UIIcon settings;
    public UIIcon worlds;
    public UIIcon worldSettings;

    /* Camera data */
    public final UIOrbitCamera orbitUI = new UIOrbitCamera();
    public final OrbitCamera orbit = this.orbitUI.orbit;
    public final OrbitCameraController camera = new OrbitCameraController(this.orbit, 5);

    private Entity walker;
    private boolean displayAxes = true;

    private UISettingsOverlayPanel settingsPanel;

    public UIDashboard(IBridge bridge)
    {
        super(bridge);

        World world = bridge.get(IBridgeWorld.class).getWorld();

        this.orbitUI.setControl(true);
        this.orbit.position.set(world.settings.cameraPosition);
        this.orbit.rotation.set(world.settings.cameraRotation);

        /* Setup panels */
        this.panels = new UIDashboardPanels();
        this.panels.getEvents().register(UIDashboardPanels.PanelEvent.class, (e) ->
        {
            this.orbitUI.setControl(this.panels.isFlightSupported());

            if (e.lastPanel instanceof UIFilmPanel)
            {
                this.orbit.setup(this.bridge.get(IBridgeCamera.class).getCamera());
            }
        });
        this.panels.relative(this.viewport).full();
        this.registerPanels();

        BBS.events.post(new RegisterDashboardPanels(this));

        this.main.add(this.panels);

        this.settingsPanel = new UISettingsOverlayPanel();

        this.settings = new UIIcon(Icons.SETTINGS, (b) ->
        {
            UIOverlay.addOverlayRight(this.context, this.settingsPanel, 240);
        });
        this.settings.tooltip(UIKeys.CONFIG_TITLE, Direction.TOP);
        this.worlds = new UIIcon(Icons.GLOBE, (b) ->
        {
            UIOverlay.addOverlay(this.context, new UIWorldsOverlayPanel(this.bridge));
        });
        this.worlds.tooltip(UIKeys.WORLD_WORLDS, Direction.TOP);
        this.worldSettings = new UIIcon(Icons.GEAR, (b) ->
        {
            UIWorldSettingsOverlayPanel settings = new UIWorldSettingsOverlayPanel(this.bridge.get(IBridgeWorld.class).getWorld().settings);
            UIOverlay overlay = UIOverlay.addOverlayRight(this.context, settings, 200);

            overlay.noBackground();
        });

        this.panels.pinned.add(this.settings, this.worlds, this.worldSettings);
        this.getRoot().prepend(this.orbitUI);

        /* Register keys */
        IKey category = UIKeys.DASHBOARD_CATEGORY;

        this.overlay.keys().register(Keys.TOGGLE_VISIBILITY, this.main::toggleVisible).category(category);
        this.overlay.keys().register(Keys.WORLD_SAVE, this::saveWorld).category(category);
        this.overlay.keys().register(Keys.WORLD_TOGGLE_WALK, this::toggleWalkMode).category(category);
        this.overlay.keys().register(Keys.WORLD_TOGGLE_AXES, () -> this.displayAxes = !this.displayAxes).category(category);
        this.overlay.keys().register(Keys.WORLD_CYCLE_PANELS, this::cyclePanels).category(category);
        this.overlay.keys().register(Keys.DASHBOARD_WORLD_EDITOR, () -> this.panels.setPanel(this.panels.getPanel(UIWorldEditorPanel.class))).category(category);

        UIChalkboard chalkboard = new UIChalkboard();

        chalkboard.relative(this.viewport).full();
        chalkboard.setVisible(false);

        this.getRoot().add(chalkboard);
        this.getRoot().keys().register(Keys.CHALKBOARD_TOGGLE, chalkboard::toggleVisible);
    }

    private void saveWorld()
    {
        this.bridge.get(IBridgeWorld.class).getWorld().saveAll(false);
        UIUtils.playClick();
    }

    public boolean isWalkMode()
    {
        return this.walker != null;
    }

    public void toggleWalkMode()
    {
        if (this.walker == null)
        {
            Vector3d finalPosition = this.orbit.getFinalPosition();

            this.walker = EntityArchitect.createDummy();
            this.walker.basic.setHitboxSize(0.5F, 1.2F);
            this.walker.basic.sneakMultiplier = 0.75F;
            this.walker.basic.setPosition(finalPosition.x, finalPosition.y - this.walker.basic.getEyeHeight(), finalPosition.z);
            this.walker.basic.prevPosition.set(this.walker.basic.position);
            this.orbit.position.set(finalPosition);
        }
        else
        {
            this.walker = null;
        }

        this.orbit.distance = 0F;
    }

    private void cyclePanels()
    {
        List<UIDashboardPanel> panels = this.panels.panels;

        int direction = Window.isShiftPressed() ? -1 : 1;
        int index = panels.indexOf(this.panels.panel);
        int newIndex = MathUtils.cycler(index + direction, 0, panels.size() - 1);

        this.setPanel(panels.get(newIndex));
        UIUtils.playClick();
    }

    public UIDashboardPanels getPanels()
    {
        return this.panels;
    }

    public void reloadWorld(World world)
    {
        for (UIDashboardPanel panel : this.panels.panels)
        {
            panel.reloadWorld();
        }

        this.orbitUI.setControl(true);
        this.orbit.position.set(world.settings.cameraPosition);
        this.orbit.rotation.set(world.settings.cameraRotation);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("dashboard");
    }

    @Override
    public boolean canPause()
    {
        return this.panels.panel != null && this.panels.panel.canPause();
    }

    @Override
    public boolean canRefresh()
    {
        return this.panels.panel != null && this.panels.panel.canRefresh();
    }

    @Override
    public void onOpen(UIBaseMenu oldMenu)
    {
        super.onOpen(oldMenu);

        if (oldMenu != this)
        {
            this.panels.open();
            this.setPanel(this.panels.panel);
        }

        this.bridge.get(IBridgeCamera.class).getCameraController().add(this.camera);
    }

    @Override
    public void onClose(UIBaseMenu nextMenu)
    {
        super.onClose(nextMenu);

        if (nextMenu != this)
        {
            this.panels.close();
        }

        this.orbit.reset();
        this.bridge.get(IBridgeCamera.class).getCameraController().remove(this.camera);
    }

    @Override
    protected void closeMenu()
    {
        if (!this.main.isVisible())
        {
            this.main.setVisible(true);
        }
    }

    protected void registerPanels()
    {
        this.panels.registerPanel(new UIFilmPanel(this), UIKeys.FILM_TITLE, Icons.FILM);

        this.panels.registerPanel(new UIWorldEditorPanel(this), UIKeys.WORLD_WORLD_EDITOR, Icons.BLOCK).marginLeft(10);
        this.panels.registerPanel(new UIWorldObjectsPanel(this), UIKeys.WORLD_OBJECT_EDITOR, Icons.SPHERE);
        this.panels.registerPanel(new UIEntitiesPanel(this), UIKeys.WORLD_ENTITY_EDITOR, Icons.POSE);
        this.panels.registerPanel(new UITileSetEditorPanel(this), UIKeys.TILE_SET_TITLE, Icons.STAIR);

        this.panels.registerPanel(new UIParticleSchemePanel(this), UIKeys.PANELS_PARTICLES, Icons.PARTICLE).marginLeft(10);
        this.panels.registerPanel(new UIFontPanel(this), UIKeys.FONT_EDITOR_TITLE, Icons.FONT);
        this.panels.registerPanel(new UITextureManagerPanel(this), UIKeys.TEXTURES_TOOLTIP, Icons.MATERIAL);
        this.panels.registerPanel(new UIGraphPanel(this), UIKeys.GRAPH_TOOLTIP, Icons.GRAPH);

        this.setPanel(this.getPanel(UIWorldEditorPanel.class));
    }

    public <T> T getPanel(Class<T> clazz)
    {
        return this.panels.getPanel(clazz);
    }

    public void setPanel(UIDashboardPanel panel)
    {
        this.panels.setPanel(panel);
    }

    @Override
    public boolean handleKey(int key, int scanCode, int action, int mods)
    {
        if (this.panels.isFlightSupported() && this.walker != null)
        {
            if (key == GLFW.GLFW_KEY_SPACE && action == GLFW.GLFW_PRESS)
            {
                float factor = Window.isCtrlPressed() ? 1F : 0.4F;

                this.walker.basic.velocity.y += factor;
                this.walker.basic.grounded = false;
            }
            else if (key == GLFW.GLFW_KEY_LEFT_SHIFT)
            {
                if (action == GLFW.GLFW_PRESS)
                {
                    this.walker.basic.sneak = true;
                }
                else if (action == GLFW.GLFW_RELEASE)
                {
                    this.walker.basic.sneak = false;
                }
            }
        }

        return super.handleKey(key, scanCode, action, mods);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.panels.panel != null)
        {
            this.panels.panel.update();
        }

        if (this.panels.isFlightSupported() && this.walker != null)
        {
            this.walker.setWorld(this.bridge.get(IBridgeWorld.class).getWorld());
            this.walker.update();

            Vector3i position = this.orbit.getVelocityPosition();
            Vector3f direction = new Vector3f();
            float factor = Window.isCtrlPressed() ? 3F : Window.isAltPressed() ? 0.2F : 0.5F;

            factor *= this.walker.basic.grounded ? 1 / 0.7F : 1 / 0.95F;

            direction.x = position.x * -factor;
            direction.z = position.z * -factor;

            Matrices.rotate(direction, 0, -this.orbit.rotation.y);

            BasicComponent basic = this.walker.basic;

            basic.velocity.x = Interpolations.lerp(basic.velocity.x, direction.x, 0.25F);
            basic.velocity.z = Interpolations.lerp(basic.velocity.z, direction.z, 0.25F);
        }
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        if (!this.main.isVisible())
        {
            return;
        }

        if (this.panels.panel != null && this.panels.panel.needsBackground())
        {
            this.background(context);
        }
        else
        {
            context.batcher.gradientVBox(0, 0, this.width, this.height / 8, Colors.A25, 0);
            context.batcher.gradientVBox(0, this.height - this.height / 8, this.width, this.height, 0, Colors.A25);
        }
    }

    private void background(UIRenderingContext context)
    {
        Link background = BBSSettings.backgroundImage.get();
        int color = BBSSettings.backgroundColor.get();

        if (background == null)
        {
            context.batcher.box(0, 0, this.width, this.height, color);
        }
        else
        {
            context.batcher.texturedBox(context.getTextures().getTexture(background), color, 0, 0, this.width, this.height, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    @Override
    public void renderMenu(UIRenderingContext context, int mouseX, int mouseY)
    {
        super.renderMenu(context, mouseX, mouseY);

        if (this.orbitUI.canControl() && this.walker != null)
        {
            BasicComponent basic = this.walker.basic;
            Vector3d position = new Vector3d(basic.prevPosition).lerp(basic.position, context.getTransition());

            this.orbit.position.set(position).add(0, basic.getEyeHeight(), 0);
        }
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        super.renderInWorld(context);

        if (this.panels.panel != null)
        {
            this.panels.panel.renderInWorld(context);
        }

        if (this.main.isVisible() && this.orbit.distance > 0.1F && this.displayAxes && this.panels.isFlightSupported())
        {
            this.renderWorldAxes(context);
        }
    }

    public void renderWorldAxes(RenderingContext context)
    {
        Vector3f relative = context.getCamera().getRelative(this.orbit.position);

        final float axisSize = 0.75F;
        final float axisOffset = 0.045F;
        final float outlineSize = axisSize + 0.015F;
        final float outlineOffset = axisOffset + 0.015F;
        final float labelOffset = axisSize - 0.075F;

        context.stack.push();
        context.stack.translate(relative);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);

        CommonShaderAccess.setModelView(shader, context.stack);

        context.stack.pop();

        /* Draw axes */
        VAOBuilder builder = context.getVAO().setup(shader);

        GLStates.depthMask(false);

        builder.begin();

        Draw.fillBox(builder, 0, -outlineOffset, -outlineOffset, outlineSize, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineSize, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, 0, -outlineOffset, outlineOffset, outlineSize, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineSize, -outlineOffset, outlineOffset, 0, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, outlineSize, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, -outlineSize, outlineOffset, outlineOffset, 0, 0, 0, 0);

        builder.render();

        GLStates.depthMask(true);

        builder.begin();

        Draw.fillBox(builder, 0, -axisOffset, -axisOffset, axisSize, axisOffset, axisOffset, 1, 0, 0);
        Draw.fillBox(builder, -axisSize, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, 0.75F, 0, 0.25F);
        Draw.fillBox(builder, -axisOffset, 0, -axisOffset, axisOffset, axisSize, axisOffset, 0, 1, 0);
        Draw.fillBox(builder, -axisOffset, -axisSize, -axisOffset, axisOffset, 0, axisOffset, 0, 0.75F, 0.25F);
        Draw.fillBox(builder, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, axisSize, 0, 0, 1);
        Draw.fillBox(builder, -axisOffset, -axisOffset, -axisSize, axisOffset, axisOffset, 0, 0.25F, 0, 0.75F);

        builder.render();

        if (this.orbit.distance < 8)
        {
            GLStates.depthTest(false);
            GLStates.cullFaces(false);

            this.renderWorldAxisLabel(context, "-X", -labelOffset, 0, 0, relative);
            this.renderWorldAxisLabel(context, "+X", labelOffset, 0, 0, relative);
            this.renderWorldAxisLabel(context, "+Y", 0, labelOffset, 0, relative);
            this.renderWorldAxisLabel(context, "-Y", 0, -labelOffset, 0, relative);
            this.renderWorldAxisLabel(context, "-Z", 0, 0, -labelOffset, relative);
            this.renderWorldAxisLabel(context, "+Z", 0, 0, labelOffset, relative);

            GLStates.depthTest(true);
            GLStates.cullFaces(true);
        }
    }

    private void renderWorldAxisLabel(RenderingContext context, String label, float x, float y, float z, Vector3f relative)
    {
        final float scale = 0.125F / 16F;
        MatrixStack stack = context.stack;
        ColoredTextBuilder3D textBuilder = ITextBuilder.colored3D;

        stack.push();
        stack.translate(relative);
        stack.translate(x, y, z);
        stack.scale(scale, -scale, scale);
        stack.rotateY(-this.orbit.rotation.y);
        stack.rotateX(this.orbit.rotation.x);

        Shader shader = context.getShaders().get(textBuilder.getAttributes());

        CommonShaderAccess.setModelView(shader, stack);

        stack.pop();

        FontRenderer font = context.getFont();

        font.bindTexture(context);

        VAOBuilder builder = context.getVAO().setup(shader, VAO.INDICES);

        builder.begin();
        font.buildVAO(-font.getWidth(label) / 2, -font.getHeight() / 2, label, builder, textBuilder.setup(Colors.A100));
        builder.render();
    }
}