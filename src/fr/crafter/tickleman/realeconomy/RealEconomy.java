package fr.crafter.tickleman.realeconomy;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.Method;

import fr.crafter.tickleman.realplugin.RealPlugin;

//##################################################################################### RealEconomy
public class RealEconomy
{

	private RealAccounts      accounts;
	private RealEconomyConfig config;
	private RealPlugin        plugin;
	private String            economyPlugin;
	private Method            paymentMethod;
  private Economy           vaultEconomy = null;

	//----------------------------------------------------------------------------------- RealEconomy
	public RealEconomy(RealPlugin plugin)
	{
		this.plugin = plugin;
		this.economyPlugin = "RealEconomy";
		accounts = new RealAccounts(plugin);
		config = new RealEconomyConfig(plugin);
		config.load();
	}

	//---------------------------------------------------------------------------------------- format
	public String format(Double amount)
	{
		String result;
		result = amount.toString() + " " + getCurrency();
		return result.replace(".00 ", "").replace(".0 ", "");
	}

	//------------------------------------------------------------------------------------ getBalance
	public double getBalance(String playerName)
	{
		Double balance = 0.0;
		if (
			(this.paymentMethod == null)
			|| economyPlugin.equals("none")
			|| economyPlugin.equals("RealEconomy")
		) {
			balance = accounts.getBalance(playerName);
			if (balance == null) {
				balance = config.initialBalance;
			}
		} else if ((vaultEconomy != null) && vaultEconomy.isEnabled()) {
			balance = vaultEconomy.getBalance(playerName);
		} else {
			balance = paymentMethod.getAccount(playerName).balance();
		}
		return Math.round(balance * 100.0) / 100.0;
	}

	//------------------------------------------------------------------------------------ getBalance
	public String getBalance(String playerName, boolean withCurrency)
	{
		Double balance = getBalance(playerName);
		if (withCurrency) {
			return format(balance);
		} else {
			return balance.toString();
		}
	}

	//------------------------------------------------------------------------------------ hasAccount
	public boolean hasAccount(String playerName)
	{
		if (
			(this.paymentMethod == null)
			|| economyPlugin.equals("none")
			|| economyPlugin.equals("RealEconomy")
		) {
			return (accounts.getBalance(playerName) != null);
		} else if ((vaultEconomy != null) && vaultEconomy.isEnabled()) {
			return vaultEconomy.getBalance(playerName) > 0;
		} else {
			return paymentMethod.hasAccount(playerName);
		}
	}

	//----------------------------------------------------------------------------------- getCurrency
	public String getCurrency()
	{
		return config.currency;
	}

	//------------------------------------------------------------------------------ getEconomyPlugin
	public String getEconomyPlugin()
	{
		return economyPlugin;
	}

	//------------------------------------------------------------------------------------- initVault
	public void initVault()
	{
		if ((vaultEconomy == null) && economyPlugin.equals("Vault")) {
			try {
				RegisteredServiceProvider<Economy> economyProvider
					= plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
				if (economyProvider != null) {
					vaultEconomy = economyProvider.getProvider();
				}
			} catch (Exception e) {
				vaultEconomy = null;
			}
		}
	}

	//------------------------------------------------------------------------------------ setBalance
	public void setBalance(String playerName, double balance)
	{
		if (
			(this.paymentMethod == null)
			|| economyPlugin.equals("none")
			|| economyPlugin.equals("RealEconomy")
		) {
			accounts.setBalance(playerName, balance);
		} else if ((vaultEconomy != null) && vaultEconomy.isEnabled()) {
			if (balance > vaultEconomy.getBalance(playerName)) {
				vaultEconomy.depositPlayer(playerName, balance - vaultEconomy.getBalance(playerName));
			} else {
				vaultEconomy.withdrawPlayer(playerName, vaultEconomy.getBalance(playerName) - balance);
			}
		} else {
			paymentMethod.getAccount(playerName).set(balance);
		}
	}

	//------------------------------------------------------------------------------ setPaymentMethod
	public void setPaymentMethod(Method paymentMethod)
	{
		economyPlugin = "Register";
		this.paymentMethod = paymentMethod;
	}

	//-------------------------------------------------------------------------------------- transfer
	public void transfer(String playerNameFrom, String playerNameTo, double balance)
	{
		setBalance(playerNameFrom, getBalance(playerNameFrom) - balance);
		setBalance(playerNameTo,   getBalance(playerNameTo)   + balance);
	}

}
