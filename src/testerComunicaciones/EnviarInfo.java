package testerComunicaciones;

import jade.content.AgentAction;

public class EnviarInfo implements AgentAction{
	
	private FlightP avion;
	
	public FlightP getAvion(){
		return avion;
	}
	public void setAvion(FlightP a){
		
		avion = a;
	}
}
