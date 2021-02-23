package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.PlayerGoals;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				if(!idMap.containsKey(player.getPlayerID())) {
					result.add(player);
					idMap.put(player.getPlayerID(), player);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertex(Map<Integer, Player> idMap, double goals){
		List<Player> result = new ArrayList<>();
		String sql = "select distinct a.`PlayerID`as id, avg(a.`Goals`) as media "
				+ "from actions as a "
				+ "group by id"
				+ " having media > ?";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, goals);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player p = idMap.get(res.getInt("id"));
				
				if(p != null)
					result.add(p);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Adiacenza> getEdges(Map<Integer, Player> idMap){
		List<Adiacenza> result = new ArrayList<>();
		String sql = "select a1.`PlayerID`as id1, a2.`PlayerID` as id2, "
				+ "sum(a1.`TimePlayed`) as sum1, sum(a2.`TimePlayed`) as sum2 "
				+ "from actions as a1, actions as a2 "
				+ "where a1.`PlayerID` > a2.`PlayerID` "
				+ "and a1.`TeamID` != a2.`TeamID` "
				+ "and a1.`MatchID` = a2.`MatchID` "
				+ "and a1.`Starts` = 1 and a2.`Starts`=1 "
				+ "group by id1, id2 "
				+ "having (sum1 - sum2) != 0 ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player p1 = idMap.get(res.getInt("id1"));
				Player p2 = idMap.get(res.getInt("id2"));
				int sum1 = res.getInt("sum1");
				int sum2 = res.getInt("sum2");
				if(p1 != null && p2 != null)
					result.add(new Adiacenza(p1, p2, sum1, sum2));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
