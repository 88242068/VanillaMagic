package seia.vanillamagic.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import seia.vanillamagic.api.tileentity.ICustomTileEntity;
import seia.vanillamagic.handler.customtileentity.CustomTileEntityHandler;
import seia.vanillamagic.tileentity.CustomTileEntity;
import seia.vanillamagic.util.WorldHelper;

public class ChunkLoadingHandler implements OrderedLoadingCallback
{
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		for(Ticket ticket : tickets)
		{
			NBTTagCompound modData = ticket.getModData();
			int posX = modData.getInteger("x");
			int posY = modData.getInteger("y");
			int posZ = modData.getInteger("z");
			BlockPos pos = new BlockPos(posX, posY, posZ);
			ICustomTileEntity customTile = CustomTileEntityHandler.INSTANCE.getCustomTileEntity(pos, WorldHelper.getDimensionID(world));
			if(customTile != null)
			{
				((ICustomTileEntity) customTile).forceChunkLoading(ticket);
			}
			else
			{
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof CustomTileEntity)
				{
					((CustomTileEntity) tile).forceChunkLoading(ticket);
				}
			}
		}
	}
	
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
	{
		List<Ticket> validTickets = new ArrayList<Ticket>();
		for(Ticket ticket : tickets)
		{
			NBTTagCompound modData = ticket.getModData();
			int posX = modData.getInteger("x");
			int posY = modData.getInteger("y");
			int posZ = modData.getInteger("z");
			BlockPos pos = new BlockPos(posX, posY, posZ);
			ICustomTileEntity customTile = CustomTileEntityHandler.INSTANCE.getCustomTileEntity(pos, WorldHelper.getDimensionID(world));
			if(customTile != null)
			{
				validTickets.add(ticket);
			}
			else
			{
				TileEntity tile = world.getTileEntity(pos);
				if(tile instanceof CustomTileEntity)
				{
					validTickets.add(ticket);
				}
			}
		}
		return validTickets;
	}
}