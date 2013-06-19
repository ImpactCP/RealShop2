package fr.crafter.tickleman.realshop2.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realshop2.RealShop2Plugin;

//################################################################################## PlayerShopList
public class PlayerShopList
{

	RealShop2Plugin plugin;

	/**
	 * Selected shop list : Player name => Shop
	 * 
	 * Using the player object as key of this map is not reliable, as it changes once the player reconnects.
	 * It's nothing major, but over time the map will fill up with non-used entries
	 */
	private Map<String, Shop> selectShop = new HashMap<String, Shop>();

	/**
	 * Inside shop players list : Player name => Shop
	 */
	private Map<String, Shop> insideShop = new HashMap<String, Shop>();

	//-------------------------------------------------------------------------------- PlayerShopList
	public PlayerShopList(RealShop2Plugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------------- enterShop
	/**
	 * Player enters a shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void enterShop(Player player, Shop shop)
	{
		selectShop(player, shop);
		insideShop.put(player.getName().toLowerCase(), shop);
	}

	//-------------------------------------------------------------------------------------- exitShop
	/**
	 * Player exits a shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void exitShop(Player player)
	{
		insideShop.remove(player.getName().toLowerCase());
	}

	//------------------------------------------------------------------------------- hasSelectedShop
	/**
	 * Return true if player has selected a shop
	 *
	 * @param Player player
	 * @return boolean
	 */
	public boolean hasSelectedShop(Player player)
	{
		return (selectedShop(player) != null);
	}

	//------------------------------------------------------------------------------------ insideShop
	/**
	 * Return shop inside which player is
	 *
	 * @param Player player
	 * @return Shop shop
	 */
	public Shop insideShop(Player player)
	{
		return insideShop.get(player.getName().toLowerCase());
	}

	//-------------------------------------------------------------------------------------- isInShop
	/**
	 * Return true if player is inside a shop
	 *
	 * @param Player player
	 * @return boolean
	 */
	public boolean isInShop(Player player)
	{
		return (insideShop(player) != null);
	}

	//------------------------------------------------------------------------------------ playerShop
	/**
	 * Return last shop player selected
	 *
	 * @param Player player
	 * @return Shop shop
	 */
	public Shop selectedShop(Player player)
	{
		return selectShop.get(player.getName().toLowerCase());
	}

	//------------------------------------------------------------------------------------ selectShop
	/**
	 * Set player's last selected shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void selectShop(Player player, Shop shop)
	{
		selectShop.put(player.getName().toLowerCase(), shop);
	}

	//---------------------------------------------------------------------------------- unselectShop
	/**
	 * Unselect shop
	 *
	 * @param Player player
	 * @param Shop shop
	 */
	public void unselectShop(Player player)
	{
		selectShop.remove(player.getName().toLowerCase());
	}

}
