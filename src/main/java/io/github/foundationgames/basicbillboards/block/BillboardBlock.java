package io.github.foundationgames.basicbillboards.block;

import com.google.common.collect.ImmutableSet;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BillboardBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape NORTH_SHAPE = createCuboidShape(0, 0, 8, 16, 16, 16);
    public static final VoxelShape SOUTH_SHAPE = createCuboidShape(0, 0, 0, 16, 16, 8);
    public static final VoxelShape WEST_SHAPE = createCuboidShape(8, 0, 0, 16, 16, 16);
    public static final VoxelShape EAST_SHAPE = createCuboidShape(0, 0, 0, 8, 16, 16);

    public BillboardBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if(ImmutableSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST).contains(ctx.getSide())) return getDefaultState().with(FACING, ctx.getSide());
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof BillboardBlockEntity) player.openHandledScreen((BillboardBlockEntity)world.getBlockEntity(pos));
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)) {
            default: return NORTH_SHAPE;
            case SOUTH: return SOUTH_SHAPE;
            case EAST: return EAST_SHAPE;
            case WEST: return WEST_SHAPE;
        }
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BillboardBlockEntity();
    }
}
