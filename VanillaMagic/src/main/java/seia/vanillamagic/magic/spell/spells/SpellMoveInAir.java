package seia.vanillamagic.magic.spell.spells;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import seia.vanillamagic.magic.spell.Spell;
import seia.vanillamagic.magic.wand.IWand;

public class SpellMoveInAir extends Spell 
{
	public SpellMoveInAir(int spellID, String spellName, String spellUniqueName, IWand wand, 
			ItemStack itemOffHand) 
	{
		super(spellID, spellName, spellUniqueName, wand, itemOffHand);
	}

	public boolean castSpell(EntityPlayer caster, BlockPos pos, EnumFacing face, Vec3d hitVec) 
	{
		World world = caster.world;
		double distance = 10;
		Vec3d casterLookVec = caster.getLookVec();
		Potion potionSpeed = Potion.getPotionById(1);
		if(caster.isPotionActive(potionSpeed)) // speed potion
		{
			int amplifier = caster.getActivePotionEffect(potionSpeed).getAmplifier();
			distance += (1 + amplifier) * (0.35);
		}
		// will teleport caster to the farthest blockPos between casterPos and 'distance'
		for(double i = distance; i > 0; i -= 1.0D)
		{
			double newPosX = caster.posX + casterLookVec.xCoord * i;
			double newPosY = caster.posY + casterLookVec.yCoord * i;
			double newPosZ = caster.posZ + casterLookVec.zCoord * i;
			BlockPos newPos = new BlockPos(newPosX, newPosY, newPosZ);
			BlockPos newPosHead = new BlockPos(newPosX, newPosY + 1, newPosZ);
			if(newPosY > 0)
			{
				if(world.isAirBlock(newPos) && 
						world.isAirBlock(newPosHead))
				{
					caster.setPositionAndUpdate(newPosX, newPosY, newPosZ);
					caster.fallDistance = 0.0F;
					return true;
				}
			}
		}
		return false;
	}
}