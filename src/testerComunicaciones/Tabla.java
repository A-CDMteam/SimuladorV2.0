package testerComunicaciones;

public class Tabla {

	private FlightP[] aviones = new FlightP[100];
	private int numero;
	
	public Tabla (int n)
	{
		 aviones = new FlightP[n];
		 numero = 0;
	}
	// Getters & Setters
	public FlightP[] getAviones() {
		return aviones;
	}
	public void setAviones(FlightP[] aviones) {
		this.aviones = aviones;
	}
	public int getnumero() {
		return numero;
	}
	public void setnumero(int i) {
		this.numero = i;
	}
	
	// Añadir y recojer un avion a la lista
	
	public void setAvion(FlightP a, int i){
		
		aviones[i] = a;
		numero ++ ;
	}
	public FlightP getAvion(int i){
		
		return aviones[i];
	}


}
