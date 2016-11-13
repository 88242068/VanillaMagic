package seia.vanillamagic.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import seia.vanillamagic.api.tileentity.ICustomTileEntity;
import seia.vanillamagic.core.VanillaMagic;
import seia.vanillamagic.util.NBTHelper;

public abstract class CustomTileEntity extends TileEntity implements ICustomTileEntity
{
	protected Ticket chunkTicket;
	
	public void init(World world, BlockPos pos)
	{
		this.worldObj = world;
		this.pos = pos;
	}
	
	public TileEntity getTileEntity()
	{
		return this;
	}
	
	public void setWorld(World world)
	{
		this.worldObj = world;
	}
	
	public void validate() 
	{
		super.validate();
		if((!this.worldObj.isRemote) && (this.chunkTicket == null)) 
		{
			Ticket ticket = ForgeChunkManager.requestTicket(VanillaMagic.INSTANCE, this.worldObj, ForgeChunkManager.Type.NORMAL);
			if(ticket != null) 
			{
				forceChunkLoading(ticket);
			}
		}
	}
	
	public void invalidate() 
	{
		super.invalidate();
		stopChunkLoading();
	}
	
	public void stopChunkLoading() 
	{
		if(this.chunkTicket != null) 
		{
			ForgeChunkManager.releaseTicket(this.chunkTicket);
			this.chunkTicket = null;
		}
	}
	
	public void forceChunkLoading(Ticket ticket)
	{
		if(chunkTicket == null)
		{
			chunkTicket = ticket;
		}
		ChunkPos tilePos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		ForgeChunkManager.forceChunk(ticket, tilePos);
	}
	
	/**
	 * Try to override {@link #deserializeNBT(NBTTagCompound)} instead of this method.
	 */
	@Deprecated
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTHelper.readFromINBTSerializable(this, tag);
	}
	
	public void deserializeNBT(NBTTagCompound tag)
    {
//    	this.readFromNBT(tag);
    }
	
	/**
	 * Try to override {@link #serializeNBT()} instead of this method.
	 */
	@Deprecated
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag = NBTHelper.writeToINBTSerializable(this, tag);
		return tag;
	}
	
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		return tag;//this.writeToNBT(tag);
	}
	
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), -999, nbt);
	}
    
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
		this.readFromNBT(pkt.getNbtCompound());
	}
	
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}
	
	public List<String> getAdditionalInfo()
	{
		List<String> list = new ArrayList<String>();
		list.add("CustomTileEntity name: " + getClass().getSimpleName());
		list.add("CustomTileEntity position: X=" + pos.getX() + ", Y=" + pos.getY() + ", Z=" + pos.getZ());
		return list;
	}
	
	public Ticket getChunkTicket()
	{
		return chunkTicket;
	}
}