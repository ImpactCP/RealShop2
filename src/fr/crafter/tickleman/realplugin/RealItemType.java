package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Method;

import net.minecraft.server.Block;
import net.minecraft.server.Item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

//#################################################################################### RealItemType
public class RealItemType
{

	/**
	 * Minecraft type identifier of item
	 */
	private int typeId;

	/**
	 * Variant code of item, for items than can have variants
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is null for non-applicable items
	 */
	private short variant;

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(ItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(RealItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(net.minecraft.server.ItemStack itemStack)
	{
		this(itemStack.id, (short)itemStack.getData());
	}

	//------------------------------------------------------------------------------------------ Item
	public RealItemType(net.minecraft.server.Item item)
	{
		this(item.id, (short)0);
	}

	//------------------------------------------------------------------------------------------ Item
	public RealItemType(net.minecraft.server.Item item, short variant)
	{
		this(item.id, variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material)
	{
		this(material.getId(), (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material, short variant)
	{
		this(material.getId(), variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(int typeId)
	{
		this(typeId, (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(int typeId, short variant)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------- getNameOf
	private static String getNameOf(Object object)
	{
		String name = null;
		if (object != null) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if ((method.getParameterTypes().length == 0)) {
					if (method.getReturnType().getName().equals("java.lang.String")) {
						try {
							name = (String) method.invoke(object);
							break;
						} catch (Exception e) {
						}
					} else if (
						method.getName().equals("getParent")
						&& (method.getParameterTypes().length == 0)
					) {
						try {
							Object object2 = method.invoke(object);
							name = getNameOf(object2);
							if ((name != null) && (name.length() > 0)) {
								break;
							}
						} catch (Exception e) {
						}
					}
				}
			}
			if ((name == null) || (name.length() == 0)) {
				name = object.getClass().getName();
			}
			while (name.contains(".")) {
				name = name.substring(name.indexOf(".") + 1);
			}
			if (name.length() > 5) {
				if (name.substring(0, 5).equalsIgnoreCase("block")) {
					name = name.substring(5);
				} else if (name.substring(0, 4).equalsIgnoreCase("item")) {
					name = name.substring(4);
				}
			}
		}
		return (name == null) ? "" : name;
	}

	//--------------------------------------------------------------------------------------- getName
	public String getName()
	{
		return getName(typeId);
	}

	//--------------------------------------------------------------------------------------- getName
	public static String getName(int typeId)
	{
		Object object = ((typeId < 256) ? Block.byId[typeId] : Item.byId[typeId]);
		String name = getNameOf(object);
		for (int i = 0; i < name.length(); i ++) {
			if ((name.charAt(i) >= 'A') && (name.charAt(i) <= 'Z')) {
				if (i == 0) {
					name = (char)(name.charAt(i) - 'A' + 'a') + name.substring(i + 1);
				} else if (name.charAt(i - 1) == ' ') {
					name = name.substring(0, i) + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				} else {
					name = name.substring(0, i) + " " + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				}
			}
		}
		return name;
	}

	//------------------------------------------------------------------------------------- getTypeId
	public int getTypeId()
	{
		return typeId;
	}

	//------------------------------------------------------------------------------------ getVariant
	public short getVariant()
	{
		return variant;
	}

	//--------------------------------------------------------------------------------- parseItemType
	public static RealItemType parseItemType(String typeIdVariant)
	{
		if (typeIdVariant.contains(":")) {
			String[] split = typeIdVariant.split(":");
			return new RealItemType(Integer.parseInt(split[0]), Short.parseShort(split[1]));
		} else {
			return new RealItemType(Integer.parseInt(typeIdVariant));
		}
	}

	//------------------------------------------------------------------------- parseItemTypeKeywords
	public static RealItemType parseItemTypeKeywords(String[] keyWords)
	{
		try {
			return RealItemType.parseItemType(keyWords[0]);
		} catch (Exception e) {
			return RealItemType.parseItemType("0");
		}
	}

	//------------------------------------------------------------------------------------- setTypeId
	public void setTypeId(int typeId)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------ isSameItem
	public boolean isSameItem(RealItemType itemType)
	{
		return (itemType.getTypeId() == getTypeId()) && (itemType.getVariant() == getVariant());
	}

	//------------------------------------------------------------------------------ setTypeIdVariant
	public void setTypeIdVariant(int typeId, short variant)
	{
		this.typeId = typeId;
		setVariant(variant);
	}

	//------------------------------------------------------------------------------------ setVariant
	public void setVariant(short variant)
	{
		if (typeIdHasVariant(typeId)) {
			this.variant = ((variant < 0) ? 0 : variant);
		} else {
			this.variant = 0;
		}
	}

	//--------------------------------------------------------------------------------- toNamedString
	public String toNamedString()
	{
		return getName() + ((getVariant() != 0) ? " : " + getVariant() : "");
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return getTypeId() + ((getVariant() != 0) ? ":" + getVariant() : "");
	}

	//------------------------------------------------------------------------------- typeIdHasDamage
	public static Boolean typeIdHasDamage(int typeId)
	{
		return !typeIdHasVariant(typeId);
	}

	//------------------------------------------------------------------------------ typeIdHasVariant
	public static Boolean typeIdHasVariant(int typeId)
	{
		return
			// those codes have variant : durability is an item variant instead of damage
			(typeId == Material.LOG.getId())
			|| (typeId == Material.LEAVES.getId())
			|| (typeId == Material.MONSTER_EGGS.getId())
			|| (typeId == Material.WOOL.getId())
			|| (typeId == Material.DOUBLE_STEP.getId())
			|| (typeId == Material.STEP.getId())
			|| (typeId == Material.COAL.getId())
			|| (typeId == Item.INK_SACK.id)
			|| (typeId == Item.POTION.id)
		;
	}

	//------------------------------------------------------------------------------- typeIdMaxDamage
	public static short typeIdMaxDamage(int typeId)
	{
		if (typeIdHasVariant(typeId)) {
			return 0;
		} else if (typeId < 256) {
			return (short)Block.byId[typeId].c();
		} else {
			return (short)Item.byId[typeId].getMaxDurability();
		}
	}

}
