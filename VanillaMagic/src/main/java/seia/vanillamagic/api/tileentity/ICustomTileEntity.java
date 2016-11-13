package seia.vanillamagic.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.INBTSerializable;
import seia.vanillamagic.api.util.IAdditionalInfoProvider;

/**
 * This is the base definition for CustomTileEntity.<br>
 * Each CustomTileEntity is self-chunkloading.
 */
public interface ICustomTileEntity extends 
		ITickable, IAdditionalInfoProvider, INBTSerializable<NBTTagCompound>, 
		ITileEntityWrapper
{
	/**
	 * This initialization will be used for placing the ICustomTileEntity FOR THE FIRST TIME on the right position.<br>
	 * Loading / Saving will be done by {@link INBTSerializable}<br>
	 * Any variables that should be saved must be read / write by {@link INBTSerializable}'s methods.
	 */
	void init(World world, BlockPos pos);
	
	/**
	 * Forcing chunks to be loaded on this {@link Ticket}.
	 */
	void forceChunkLoading(Ticket ticket);
	
	SPacketUpdateTileEntity getUpdatePacket();
	
	void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt);
	
	NBTTagCompound getUpdateTag();
	
	/**
	 * Returns the {@link Ticket} for this CustomTileEntity.
	 */
	Ticket getChunkTicket();
}