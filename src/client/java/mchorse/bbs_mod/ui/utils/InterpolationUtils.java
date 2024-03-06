package mchorse.bbs_mod.ui.utils;

import mchorse.bbs_mod.camera.data.InterpolationType;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.utils.context.ContextAction;
import mchorse.bbs_mod.utils.keyframes.KeyframeEasing;
import mchorse.bbs_mod.utils.keyframes.KeyframeInterpolation;
import mchorse.bbs_mod.utils.math.IInterpolation;
import mchorse.bbs_mod.utils.math.Interpolation;
import org.lwjgl.glfw.GLFW;

public class InterpolationUtils
{
    public static void setupKeybind(Interpolation interp, ContextAction action, IKey category)
    {
        if (interp.key.endsWith("_in"))
        {
            action.key(category, interp.keybind, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        else if (interp.key.endsWith("_out"))
        {
            action.key(category, interp.keybind, GLFW.GLFW_KEY_LEFT_CONTROL);
        }
        else
        {
            action.key(category, interp.keybind);
        }
    }

    public static void setupKeybind(InterpolationType type, ContextAction action, IKey category)
    {
        if (type.function != null)
        {
            setupKeybind(type.function, action, category);
        }
        else
        {
            action.key(category, type.keybind);
        }
    }

    public static void setupKeybind(KeyframeInterpolation interp, ContextAction action, IKey category)
    {
        IInterpolation interpolation = interp.from(KeyframeEasing.IN);

        if (interpolation instanceof Interpolation i)
        {
            setupKeybind(i, action, category);
        }
        else if (interp == KeyframeInterpolation.CONST)
        {
            action.key(category, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        else if (interp == KeyframeInterpolation.HERMITE)
        {
            action.key(category, GLFW.GLFW_KEY_H, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        else if (interp == KeyframeInterpolation.BEZIER)
        {
            action.key(category, GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
    }

    public static IKey getName(IInterpolation interp)
    {
        return UIKeys.C_INTERPOLATION.get(interp.getKey());
    }

    public static IKey getTooltip(IInterpolation interp)
    {
        return UIKeys.C_INTERPOLATION_TIPS.get(interp.getKey());
    }

    public static IKey getName(InterpolationType type)
    {
        return UIKeys.C_INTERPOLATION.get(type.name);
    }

    public static IKey getName(KeyframeInterpolation interp)
    {
        return UIKeys.C_INTERPOLATION.get(interp.from(KeyframeEasing.IN).getKey());
    }
}