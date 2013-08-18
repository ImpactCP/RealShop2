package fr.crafter.tickleman.realshop2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.crafter.tickleman.realplugin.RealColor;
import fr.crafter.tickleman.realshop2.price.PlayerPriceAction;
import fr.crafter.tickleman.realshop2.shop.Shop;

//########################################################################## RealShopPlayerListener
/**
 * Handle events for all Player related events
 * @author tickleman
 */
public class RealShopPlayerListener implements Listener
{

	private final RealShop2Plugin plugin;

	//------------------------------------------------------------------------ RealShopPlayerListener
	public RealShopPlayerListener(RealShop2Plugin instance)
	{
		super();
		plugin = instance;
	}

	//---------------------------------------------------------------------------------- onPlayerChat
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (PlayerPriceAction.isChangingPrice(player)) {
			if (new PlayerPriceAction(plugin, player).chatChangePriceChat(
				event.getPlayer(), event.getMessage()
			)) {
				event.setCancelled(true);
			}
		}
	}

	//------------------------------------------------------------------------------ onPlayerInteract

	/*
	 * A high priority handler is required here, to manage overlapping actions with other plugins
	 *
	 * Note: Eventhandlers are processed in this oder: Lowest, Low, Normal, High, Highest, Monitor
	 */

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// check if player right-clicked a chest

		/*
		 *  Making sure that event hasn't been canceled by another plugin yet.
		 *  In conjunction with the corrent eventhandler priority, this prevents entering a shop
		 *  when right-clicking it with the worldedit wand, the worldguard string (checking for build rights) or
		 *  when the chest is locked by some plugin (SimpleChestLock for example)
		 *
		 *  Note: It's important that those plugins set the event to canceled if they handle it, 
		 *  in order for this to work properly
		 */
		if(event.isCancelled()) {
			return;
		}
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.isCancelled()) {
			Block block = event.getClickedBlock();
			if (block.getType().equals(Material.CHEST)) {
				Shop shop = plugin.getShopList().shopAt(block.getLocation());
				Player player = event.getPlayer();
				if(shop != null && player != null) {
					if (!plugin.hasPermission(player, "realshop.shop")) {
						// players must have "realshop.shop" permission to enter a shop
						player.sendMessage(RealColor.cancel + plugin.tr("You don't have the permission to shop"));
						event.setCancelled(true);
					} else if (!player.getName().equalsIgnoreCase(shop.getPlayerName()) && !shop.isOpened() && !plugin.hasPermission(player, "realshop.op")) {
						player.sendMessage(RealColor.cancel
								+ plugin.tr("+owner's shop +name is closed, please come later")
								.replace("+name", RealColor.shop + shop.getName() + RealColor.cancel)
								.replace("+owner", RealColor.player + shop.getPlayerName() + RealColor.cancel));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	//--------------------------------------------------------------------------------- onPlayerLogin
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());

		// The selected Shop should be cleared, too.
		plugin.getPlayerShopList().unselectShop(event.getPlayer());
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.getPlayerChestList().unselectChest(event.getPlayer());
		plugin.getPlayerShopList().exitShop(event.getPlayer());

		// The selected Shop should be cleared, too.
		plugin.getPlayerShopList().unselectShop(event.getPlayer());
	}

}
