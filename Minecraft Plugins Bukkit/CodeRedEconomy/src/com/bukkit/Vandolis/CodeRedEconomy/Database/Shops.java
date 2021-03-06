/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class Shops {
	private int		ID;
	private int		balance				= EconomyProperties.getInfValue();
	private boolean	allItemsInfinite	= true;
	private Date	lastRestock			= null;
	private boolean	canRestock			= true;
	private boolean	allowBuying			= true;
	private boolean	allowSelling		= true;
	private boolean	hidden				= false;
	private String	name				= "";
	private boolean	newEntry			= false;
	
	/**
	 * Adds the given shop to SQL
	 * 
	 * @param name
	 *            of shop
	 */
	public Shops(String name) {
		this.name = name;
		newEntry = true;
		restock();
		update();
	}
	
	/**
	 * 
	 */
	private void restock() {
		if (canRestock) {
			try {
				Connection conn = DriverManager.getConnection(EconomyProperties.getDB());
				
				conn.close();
				
				lastRestock = EconomyProperties.getDate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			lastRestock = EconomyProperties.getDate();
		}
	}
	
	/**
	 * @param shopItemID
	 */
	public Shops(int shopID) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			ResultSet rs = stat.executeQuery("select * from Shops where ID = " + shopID + ";");
			ID = shopID;
			
			if (rs.next()) {
				balance = rs.getInt("Balance");
				allItemsInfinite = rs.getBoolean("AllItemsInfinite");
				lastRestock = rs.getDate("LastRestock");
				canRestock = rs.getBoolean("CanRestock");
				allowBuying = rs.getBoolean("AllowBuying");
				allowSelling = rs.getBoolean("AllowSelling");
				hidden = rs.getBoolean("Hidden");
				name = rs.getString("Name");
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param shopName
	 * @return shopID
	 */
	public static int getId(String shopName) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			PreparedStatement prep = EconomyProperties.getConn().prepareStatement("select Shops.ID from Shops where Name like ?;");
			prep.setString(1, shopName);
			ResultSet rs = prep.executeQuery();
			
			int ID = 0;
			
			if (rs.next()) {
				ID = rs.getInt("ID");
			}
			
			if (EconomyProperties.isDebug()) {
				System.out.println("Returning shopID: " + ID);
			}
			
			rs.close();
			stat.close();
			
			return ID;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 
	 */
	public void update() {
		if (EconomyProperties.isDebug()) {
			System.out.println("Updating " + name + " SQL entry.");
		}
		try {
			PreparedStatement prep;
			
			if (newEntry) {
				prep =
						EconomyProperties.getConn().prepareStatement(
								"insert into Shops (Balance, AllItemsInfinite, LastRestock, CanRestock, AllowBuying, AllowSelling, "
										+ "Hidden, Name) values (?, ?, ?, ?, ?, ?, ?, ?);");
				
				prep.setInt(1, balance);
				prep.setBoolean(2, allItemsInfinite);
				prep.setDate(3, lastRestock);
				prep.setBoolean(4, canRestock);
				prep.setBoolean(5, allowBuying);
				prep.setBoolean(6, allowSelling);
				prep.setBoolean(7, hidden);
				prep.setString(8, name);
				
				prep.execute();
				prep.close();
				
				prep = EconomyProperties.getConn().prepareStatement("select ID from Shops where Name = ?;");
				prep.setString(1, name);
				
				ResultSet rs = prep.executeQuery();
				
				ID = rs.getInt("ID");
				
				rs.close();
				
				newEntry = false;
			}
			else {
				prep =
						EconomyProperties.getConn().prepareStatement(
								"update Shops set Balance = ?, AllItemsInfinite = ?, LastRestock = ?, CanRestock = ?, AllowBuying = ?, "
										+ "AllowSelling = ?, Hidden = ?, Name = ? where ID = " + ID + ";");
				
				prep.setInt(1, balance);
				prep.setBoolean(2, allItemsInfinite);
				prep.setDate(3, lastRestock);
				prep.setBoolean(4, canRestock);
				prep.setBoolean(5, allowBuying);
				prep.setBoolean(6, allowSelling);
				prep.setBoolean(7, hidden);
				prep.setString(8, name);
				
				prep.execute();
			}
			
			prep.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}
	
	/**
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}
	
	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(int balance) {
		if (this.balance != EconomyProperties.getInfValue()) {
			this.balance = balance;
			update();
		}
	}
	
	/**
	 * @return the allItemsInfinite
	 */
	public boolean isAllItemsInfinite() {
		return allItemsInfinite;
	}
	
	/**
	 * @param allItemsInfinite
	 *            the allItemsInfinite to set
	 */
	public void setAllItemsInfinite(boolean allItemsInfinite) {
		this.allItemsInfinite = allItemsInfinite;
		update();
	}
	
	/**
	 * @return the lastRestock
	 */
	public Date getLastRestock() {
		return lastRestock;
	}
	
	/**
	 * @param lastRestock
	 *            the lastRestock to set
	 */
	public void setLastRestock(Date lastRestock) {
		this.lastRestock = lastRestock;
		update();
	}
	
	/**
	 * @return the canRestock
	 */
	public boolean isCanRestock() {
		return canRestock;
	}
	
	/**
	 * @param canRestock
	 *            the canRestock to set
	 */
	public void setCanRestock(boolean canRestock) {
		this.canRestock = canRestock;
		update();
	}
	
	/**
	 * @return the allowBuying
	 */
	public boolean isAllowBuying() {
		return allowBuying;
	}
	
	/**
	 * @param allowBuying
	 *            the allowBuying to set
	 */
	public void setAllowBuying(boolean allowBuying) {
		this.allowBuying = allowBuying;
		update();
	}
	
	/**
	 * @return the allowSelling
	 */
	public boolean isAllowSelling() {
		return allowSelling;
	}
	
	/**
	 * @param allowSelling
	 *            the allowSelling to set
	 */
	public void setAllowSelling(boolean allowSelling) {
		this.allowSelling = allowSelling;
		update();
	}
	
	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * @param hidden
	 *            the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		update();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
		update();
	}
	
	/**
	 * Removes the Shops from the table
	 */
	public void remove() {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from Shops where ID = " + ID + ";");
			
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param items
	 */
	public void addItem(Items items) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from ShopItems where ShopID = " + ID + ";");
			stat.close();
			
			PreparedStatement prep =
					EconomyProperties.getConn().prepareStatement(
							"insert into ShopItems (ShopID, ItemID, BuyPrice, SellPrice) values (?, ?, ?, ?);");
			
			prep.setInt(1, ID);
			prep.setInt(2, items.getID());
			prep.setDouble(3, items.getBuyPrice());
			prep.setDouble(4, items.getSellPrice());
			
			prep.execute();
			
			prep.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param items
	 */
	public void addItem(ShopItems items) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from ShopItems where ShopID = " + ID + ";");
			stat.close();
			
			PreparedStatement prep =
					EconomyProperties.getConn().prepareStatement(
							"insert into ShopItems (ShopID, ItemID, CurrentStock, IsInfinite, BuyPrice, SellPrice) "
									+ "values (?, ?, ?, ?, ?, ?);");
			
			prep.setInt(1, ID);
			prep.setInt(2, items.getItemID());
			prep.setInt(3, items.getCurrentStock());
			prep.setBoolean(4, items.isInfinite());
			prep.setDouble(5, items.getBuyPrice());
			prep.setDouble(6, items.getSellPrice());
			
			prep.execute();
			
			prep.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
