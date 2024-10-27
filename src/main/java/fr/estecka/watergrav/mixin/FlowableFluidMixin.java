package fr.estecka.watergrav.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;


@Mixin(FlowableFluid.class)
public class FlowableFluidMixin
{

	@Shadow static private boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState){ throw new AssertionError(); }

	@WrapOperation( method="onScheduledTick", at=@At(value="INVOKE", target="net/minecraft/fluid/FlowableFluid.tryFlow(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)V") )
	private void TryApplyGravity(FlowableFluid fluid, ServerWorld world, BlockPos topPos, BlockState topState, FluidState topFluidState, Operation<Void> original) {
		if (!TryApplyGravity(fluid, world, topPos, topState, topFluidState))
			original.call(fluid, world, topPos, topState, topFluidState);
	}

	@Unique
	private boolean TryApplyGravity(FlowableFluid fluidType, ServerWorld world, BlockPos topPos, BlockState topState, FluidState topFluidState) {
		BlockPos bottomPos = topPos.down();
		BlockState bottomState = world.getBlockState(bottomPos);
		FluidState bottomFluid = bottomState.getFluidState();

		if (!topFluidState.isStill()
		|| !(topState.getBlock() instanceof FluidDrainable topDrainable)
		|| bottomFluid.isStill()
		|| !(bottomFluid.getFluid().matchesType(fluidType) || bottomFluid.canBeReplacedWith(world, bottomPos, fluidType, Direction.DOWN))
		|| !receivesFlow(Direction.DOWN, world, topPos, topState, bottomPos, bottomState) // Checks there is an open connection between the two blocks.
		){
			return false;
		}

		if ((bottomState.isAir() || bottomState.getBlock() instanceof FluidBlock)
		&& null != topDrainable.tryDrainFluid(null, world, topPos, topState)
		){
			world.setBlockState(bottomPos, topFluidState.getBlockState(), Block.NOTIFY_ALL);
			return true;
		}
		else if (bottomState.getBlock() instanceof FluidFillable bottomFillable
		&& bottomFillable.canFillWithFluid(null, world, bottomPos, bottomState, fluidType)
		&& null != topDrainable.tryDrainFluid(null, world, topPos, topState)
		){
			bottomFillable.tryFillWithFluid(world, bottomPos, bottomState, topFluidState);
			return true;
		}
		else
			return false;
	}

}
