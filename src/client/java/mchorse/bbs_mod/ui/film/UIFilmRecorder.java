package mchorse.bbs_mod.ui.film;

import mchorse.bbs_mod.graphics.Framebuffer;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import org.lwjgl.glfw.GLFW;

public class UIFilmRecorder extends UIElement
{
    public UIFilmPanel editor;

    private UIExit exit = new UIExit(this);
    private int end;

    public UIFilmRecorder(UIFilmPanel editor)
    {
        super();

        this.editor = editor;

        this.noCulling();
    }

    public boolean isRecording()
    {
        return false; // TODO: this.getRecorder().isRecording();
    }

    private UIContext getUIContext()
    {
        return this.editor.getContext();
    }

    private Object getRecorder()
    {
        return null; // TODO: this.getUIContext().menu.bridge.get(IBridgeVideoScreenshot.class).getVideoRecorder();
    }

    private boolean isRunning()
    {
        return this.editor.isRunning();
    }

    public void openMovies()
    {
        // TODO: UIUtils.openFolder(this.getRecorder().movies);
    }

    public void startRecording(int duration, Framebuffer framebuffer)
    {
        /* TODO: VideoRecorder recorder = this.getRecorder();
        UIContext context = this.getUIContext();

        if (this.isRunning() || recorder.isRecording() || duration <= 0)
        {
            return;
        }

        this.end = duration;

        try
        {
            recorder.startRecording(framebuffer.getMainTexture());
        }
        catch (Exception e)
        {
            UIOverlay.addOverlay(context, new UIMessageOverlayPanel(UIKeys.GENERAL_ERROR, IKey.raw(e.getMessage())));

            return;
        }

        this.editor.setCursor(0);
        this.editor.togglePlayback();
        context.menu.main.setEnabled(false);
        context.menu.overlay.add(this);
        context.menu.getRoot().add(this.exit); */
    }

    public void stop()
    {
        UIContext context = this.getUIContext();

        /* TODO: context.render.postRunnable(this.exit::removeFromParent);

        if (this.getRecorder().isRecording())
        {
            try
            {
                this.getRecorder().stopRecording();
            }
            catch (Exception e) {}

            if (this.isRunning())
            {
                this.editor.togglePlayback();
            }

            context.menu.main.setEnabled(true);
            context.render.postRunnable(this::removeFromParent);
        } */
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        int ticks = this.editor.getCursor();

        if (!this.isRecording())
        {
            return;
        }

        if (!this.isRunning() || ticks >= this.end)
        {
            this.stop();
        }
    }

    public static class UIExit extends UIElement
    {
        private UIFilmRecorder recorder;

        public UIExit(UIFilmRecorder recorder)
        {
            this.recorder = recorder;
        }

        @Override
        protected boolean subKeyPressed(UIContext context)
        {
            if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                this.recorder.stop();

                return true;
            }

            return super.subKeyPressed(context);
        }
    }
}