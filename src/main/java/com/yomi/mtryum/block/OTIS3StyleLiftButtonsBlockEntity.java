package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OTIS3StyleLiftButtonsBlockEntity extends AbstractLiftButtonsBlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("OTIS3StyleLiftButtons");

    public OTIS3StyleLiftButtonsBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    }
}