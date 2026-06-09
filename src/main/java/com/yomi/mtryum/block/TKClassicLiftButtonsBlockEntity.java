package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TKClassicLiftButtonsBlockEntity extends AbstractLiftButtonsBlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("TKStyleLiftButtons");

    public TKClassicLiftButtonsBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY;
    }
}