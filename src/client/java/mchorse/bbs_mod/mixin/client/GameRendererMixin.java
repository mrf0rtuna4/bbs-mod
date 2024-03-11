package mchorse.bbs_mod.mixin.client;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.camera.controller.CameraController;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    /**
     * This injection cancels bobbing when camera controller takes over
     */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void onBob(CallbackInfo ci)
    {
        if (BBSModClient.getCameraController().getCurrent() != null)
        {
            ci.cancel();
        }
    }

    /**
     * This injection replaces the FOV when camera controller takes over
     */
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    public void onGetFov(CallbackInfoReturnable<Double> info)
    {
        CameraController controller = BBSModClient.getCameraController();

        if (controller.getCurrent() != null)
        {
            info.setReturnValue(controller.getFOV());
        }
    }
}