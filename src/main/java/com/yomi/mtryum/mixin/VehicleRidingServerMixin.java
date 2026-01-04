// Mixin部分全是AI写的
package com.yomi.mtryum.mixin;

import mtr.data.VehicleRidingServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VehicleRidingServer.class)
public class VehicleRidingServerMixin {

    @Inject(
            method = "mountRider",
            at = @At("HEAD"),
            remap = false
    )
    private static void onMountRider(CallbackInfo ci) {

    }
}