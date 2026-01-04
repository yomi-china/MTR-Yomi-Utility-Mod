package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class MtryumSounds {
    public static final SoundEvent LIFT_ARRIVAL_SOUND_1 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_1")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_2 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_2")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_3 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_3")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_4 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_4")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_5 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_5")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_6 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_6")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_7 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_7")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_8 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_8")
    );
    public static final SoundEvent LIFT_ARRIVAL_SOUND_9 = SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_9")
    );

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_1"), LIFT_ARRIVAL_SOUND_1);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_2"), LIFT_ARRIVAL_SOUND_2);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_3"), LIFT_ARRIVAL_SOUND_3);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_4"), LIFT_ARRIVAL_SOUND_4);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_5"), LIFT_ARRIVAL_SOUND_5);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_6"), LIFT_ARRIVAL_SOUND_6);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_7"), LIFT_ARRIVAL_SOUND_7);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_8"), LIFT_ARRIVAL_SOUND_8);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_9"), LIFT_ARRIVAL_SOUND_9);
    }
}