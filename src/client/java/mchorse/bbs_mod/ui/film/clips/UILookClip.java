package mchorse.bbs_mod.ui.film.clips;

import mchorse.bbs_mod.bridge.IBridgeWorld;
import mchorse.bbs_mod.camera.Camera;
import mchorse.bbs_mod.camera.clips.modifiers.LookClip;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.film.clips.modules.UIPointModule;
import mchorse.bbs_mod.ui.film.utils.UITextboxHelp;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.voxel.raytracing.RayTraceResult;
import mchorse.bbs_mod.voxel.raytracing.RayTraceType;
import mchorse.bbs_mod.voxel.raytracing.RayTracer;
import mchorse.bbs_mod.world.World;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class UILookClip extends UIClip<LookClip>
{
    /* TODO: Aperture */
    public static final String SELECTOR_HELP = "";

    public UITextboxHelp selector;
    public UIToggle relative;
    public UIPointModule offset;
    public UIToggle atBlock;
    public UIPointModule block;
    public UIToggle forward;

    public UIElement row;

    public UILookClip(LookClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.selector = new UITextboxHelp(500, (str) ->
        {
            this.clip.selector.set(str);
            this.clip.tryFindingEntity(MinecraftClient.getInstance().world);
        });
        this.selector.link(SELECTOR_HELP).tooltip(UIKeys.CAMERA_PANELS_SELECTOR_TOOLTIP);

        this.block = new UIPointModule(editor, UIKeys.CAMERA_PANELS_BLOCK).contextMenu();
        this.block.context((menu) ->
        {
            menu.action(Icons.VISIBLE, UIKeys.CAMERA_PANELS_CONTEXT_LOOK_COORDS, () -> this.rayTrace(false));
            menu.action(Icons.BLOCK, UIKeys.CAMERA_PANELS_CONTEXT_LOOK_BLOCK, () -> this.rayTrace(true));
        });
        this.offset = new UIPointModule(editor, UIKeys.CAMERA_PANELS_OFFSET).contextMenu();

        this.relative = new UIToggle(UIKeys.CAMERA_PANELS_RELATIVE, false, (b) -> this.clip.relative.set(b.getValue()));
        this.relative.tooltip(UIKeys.CAMERA_PANELS_RELATIVE_TOOLTIP);

        this.atBlock = new UIToggle(UIKeys.CAMERA_PANELS_AT_BLOCK, false, (b) -> this.clip.atBlock.set(b.getValue()));

        this.forward = new UIToggle(UIKeys.CAMERA_PANELS_FORWARD, false, (b) -> this.clip.forward.set(b.getValue()));
        this.forward.tooltip(UIKeys.CAMERA_PANELS_FORWARD_TOOLTIP);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_SELECTOR).marginTop(12), this.selector);
        this.panels.add(this.relative);
        this.panels.add(this.offset.marginTop(6));
        this.panels.add(this.atBlock.marginTop(6));
        this.panels.add(this.block.marginTop(6));
        this.panels.add(this.forward);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.selector.setText(this.clip.selector.get());
        this.block.fill(this.clip.block);
        this.offset.fill(this.clip.offset);
        this.relative.setValue(this.clip.relative.get());
        this.atBlock.setValue(this.clip.atBlock.get());
        this.forward.setValue(this.clip.forward.get());
    }

    private void rayTrace(boolean center)
    {
        Camera camera = this.editor.getCamera();
        World world = MinecraftClient.getInstance().world;
        RayTraceResult result = new RayTraceResult();

        RayTracer.traceEntity(result, world, camera.position, camera.getLookDirection(), 128);

        if (center && result.type == RayTraceType.BLOCK)
        {
            Vector3i pos = result.block;

            BaseValue.edit(this.clip.block, (block) -> block.get().set(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5));
            this.fillData();
        }
        else if (!center && !result.type.isMissed())
        {
            Vector3d vec = result.hit;

            BaseValue.edit(this.clip.block, (block) -> block.get().set(vec.x, vec.y, vec.z));
            this.fillData();
        }
    }
}