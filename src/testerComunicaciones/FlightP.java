package testerComunicaciones;
import java.io.Serializable;

import jade.content.Concept;


public class FlightP implements Serializable {
	
	private String id;
	private String STATUS;
//	private int[] tiempos;
//	private int[] actualizadas;
	private int milestone;
	
	public FlightP() {
		super();
	}
	// Getters and Setters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
//	public int[] getTiempos() {
//		return tiempos;
//	}
//	public void setTiempos(int[] tiempos) {
//		this.tiempos = tiempos;
//	}
//	public int[] getActualizadas() {
//		return actualizadas;
//	}
//	public void setActualizadas(int[] actualizadas) {
//		this.actualizadas = actualizadas;
//	}
	public int getMilestone() {
		return milestone;
	}
	public void setMilestone(int milestone) {
		this.milestone = milestone;
	}
	
	// Constructor
	
	/*public Flight(String id, String sTATUS, int[] tiempos) {
		super();
		this.id = id;
		STATUS = sTATUS;
		this.tiempos = tiempos;

	}*/
	public FlightP(String id, String sTATUS, int mil) {
		super();
		this.id = id;
		this.STATUS = sTATUS;
		this.milestone = mil;
	}



}
