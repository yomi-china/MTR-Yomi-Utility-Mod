package com.yomi.mtryum.mixin;

import com.yomi.mtryum.registry.MtryumItems;
import mtr.data.Train;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Train.class)
public class TrainMixin {

    @Inject(method = "isHoldingKey", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectCustomKeyCheck(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && player.isHolding(MtryumItems.ONBOARD_TOOL)) {
            cir.setReturnValue(true);
        }
    }
}