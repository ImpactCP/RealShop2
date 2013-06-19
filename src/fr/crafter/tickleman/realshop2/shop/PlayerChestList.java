package fr.crafter.tickleman.realshop2.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealChest;
import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################# PlayerChestList
public class PlayerChestList
{

	RealShop2Plugin plugin;

	/**
	 * Players selected chest list : Player name => RealChest
	 * 
	 * Using the player object as key of this map is not reliable, as it changes once the player reconnects.
	 * It's nothing major, but over time the map will fill up with non-used entries
	 */
	private Map<String, RealChest> chests = new HashMap<String, RealChest>();

	//------------------------------------------------------------------------------- PlayerChestList
	public PlayerChestList(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------- isInChest
	public boolean hasSelectedChest(Player player)
	{
		return (selectedChest(player) != null);
	}

	//----------------------------------------------------------------------------------- selectChest
	public void selectChest(Player player, RealChest chest)
	{
		chests.put(player.getName().toLowerCase(), chest);
	}

	//----------------------------------------------------------------------------------- playerChest
	public RealChest selectedChest(Player player)
	{
		return chests.get(player.getName().toLowerCase());
	}

	//--------------------------------------------------------------------------------- unselectChest
	public void unselectChest(Player player)
	{
		chests.remove(player.getName().toLowerCase());
	}

}
