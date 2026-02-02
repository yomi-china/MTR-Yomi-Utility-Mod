// Mixin部分全是AI写的
package com.yomi.mtryum.mixin;

import com.yomi.mtryum.registry.MtryumItems;
import mtr.data.TrainServer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(TrainServer.class)
public class TrainServerMixin {

    @ModifyArg(
            method = "simulateCar",
            at = @At(
                    value = "INVOKE",
                    target = "Lmtr/data/VehicleRidingServer;mountRider(Lnet/minecraft/world/level/Level;Ljava/util/Set;JJDDDDDFFZZILnet/minecraft/resources/ResourceLocation;Ljava/util/function/Function;Ljava/util/function/Consumer;)V"
            ),
            index = 11,
            remap = false
    )
    private boolean modifyCanMount(boolean originalCanMount) {
        return true;
    }

    @ModifyArg(
            method = "simulateCar",
            at = @At(
                    value = "INVOKE",
                    target = "Lmtr/data/VehicleRidingServer;mountRider(Lnet/minecraft/world/level/Level;Ljava/util/Set;JJDDDDDFFZZILnet/minecraft/resources/ResourceLocation;Ljava/util/function/Function;Ljava/util/function/Consumer;)V"
            ),
            index = 14,
            remap = false
    )
    private Function<Player, Boolean> modifyCanRide(Function<Player, Boolean> original) {
        return player -> {
            return player != null && player.isHolding(MtryumItems.ONBOARD_TOOL);
        };
    }
}