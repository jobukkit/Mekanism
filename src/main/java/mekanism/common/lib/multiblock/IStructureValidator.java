package mekanism.common.lib.multiblock;

import mekanism.common.lib.math.voxel.IShape;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public interface IStructureValidator<T extends MultiblockData> {

    void init(World world, MultiblockManager<T> manager, Structure structure);

    boolean precheck();

    FormationResult validate(FormationProtocol<T> ctx);

    FormationResult postcheck(T structure, Set<BlockPos> innerNodes);

    IShape getShape();
}
