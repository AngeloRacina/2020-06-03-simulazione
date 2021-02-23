package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {

	private Graph<Player, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao ;
	private Map<Integer, Player> idMap;
	
	private int titolarita;
	private List<Player> best;
	public Model() {
		this.idMap = new HashMap<Integer, Player>();
		this.dao = new PremierLeagueDAO();
		this.dao.listAllPlayers(idMap);
		
	}
	
	public void creaGrafo(double goal) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		List<Player> vertices = new ArrayList<>();
		vertices = this.dao.getVertex(idMap, goal);
		
		Graphs.addAllVertices(this.grafo, vertices);
		
		for(Adiacenza a : this.dao.getEdges(idMap)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if(this.grafo.getEdge(a.getP1(), a.getP2()) == null) {
					if(a.getMin1()-a.getMin2()>0) {
						Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), (a.getMin1()-a.getMin2()));
					}else {
						Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), (a.getMin2()-a.getMin1()));
					}	
				}
			}
		}
	}
	
	public List<Player> doDreamTeam(int k) {
		
		this.best = new ArrayList<>();
		titolarita = 0;
		List<Player> parziale = new ArrayList<>();

		this.ricorsiva(parziale, k, new ArrayList<Player>(this.grafo.vertexSet()));
			
			return best;
	}
	
	private void ricorsiva(List<Player> parziale, int k, List<Player> players) {
		// TODO Auto-generated method stub
		if(parziale.size() == k) {
			// stop
			// try partial as best
			int peso = this.getDegree(parziale);
			if(peso > this.titolarita) {
				best = new ArrayList<Player>(parziale);
				titolarita = peso;
			}
			return;
					}else {
			for(Player p : players) {
				if(!parziale.contains(p)) {
					parziale.add(p);
					//remove remaining 
					List<Player> restanti = new ArrayList<Player>(players);
					restanti.removeAll(Graphs.successorListOf(this.grafo, p));
					ricorsiva(parziale, k, restanti);
					//backtraching
					parziale.remove(p);
					
				}
			}
		}
	}

	private int getDegree(List<Player> parziale) {
		// TODO Auto-generated method stub
		int sum = 0;
		for(Player p : parziale) {
			sum +=(this.getPesoArchiOut(p)-this.getPesoArchiIn(p));
		}
		return sum;
	}

	public int getPesoArchiOut(Player p) {
		
		int somma = 0;
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
			somma += this.grafo.getEdgeWeight(e);
		}
		
		return somma;
	}
	
	public int getPesoArchiIn(Player p) {
		
		int somma = 0;
		for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(p)) {
			somma += this.grafo.getEdgeWeight(e);
		}
		return somma;
	}
	
	public Player getTopPlayer() {
		
		if(this.grafo== null) {
			return null;
		}
		int max = 0;
		Player top = null;
		
		for(Player p : this.grafo.vertexSet()) {
			if(grafo.outDegreeOf(p)> max) {
				max = this.grafo.outDegreeOf(p);
				top = p;
			}
			
		}
		
		return top;
	}
	
	public List<Connessione> getConnessioni(Player top){
		
		List<Connessione> result = new ArrayList<Connessione>();
		
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(top)) {
			result.add(new Connessione( this.grafo.getEdgeTarget(e), (int) this.grafo.getEdgeWeight(e)));
		}
		Collections.sort(result);
		return result; 
	}
	
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	
	public int nEdges() {
		return this.grafo.edgeSet().size();
	}
}
