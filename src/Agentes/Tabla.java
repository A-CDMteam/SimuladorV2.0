package Agentes;

import java.util.Arrays;

public class Tabla {

	private int numero;
	
	private Aircraft[] aviones;
	
	
	public Tabla (int n)
	{
		 aviones = new Aircraft[n];
		 numero = 0;
	}
	
	// Getters & Setters
	public Aircraft[] getAviones() {
		return aviones;
	}
	public void setAviones(Aircraft[] aviones) {
		this.aviones = aviones;
	}
	
	public int getnumero() {
		return numero;
	}
	public void setnumero(int i) {
		this.numero = i;
	}
	
	// A�adir y recojer un avion a la lista
	
	public void setAvion(Aircraft a, int[] i){ //mirar la i, no se usa
		int posicion = i[1];
		
		if(posicion == -1){

			aviones[i[0]] = a;
			numero ++ ;	
		}
		else{
			aviones[i[0]] = a;
		}
	}
	public Aircraft getAvion(int i){
		
		return aviones[i];
	}
	
	// Metodos �tiles
	public void printTabla(int opcion)
	//Out() In(int opcion)
	//Pinta todos los aviones de la tabla en pantalla. 
	//Seg�n el valor de opcion mostrar� m�s o menos informaci�n de cada avi�n.
	{
		for(int i = 0; i<numero; i++)
		{
			System.out.println("Avion " +i);
			this.getAvion(i).printFlight(opcion);
		}
		
	}
	
	public int[] PosicionTabla(Aircraft avion)
	//Out(int[] posicion) In(Aircraft avion)
	//Busca un avi�n en la tabla y devuelve su posici�n,
	//si ese avi�n no aparece en la tabla lo indica y devuelve la posici�n que ocupar�a si se a�adiese en ese momento a la tabla.
	{
		int[] posicion = {-1,0};	//Primer valor indicara la posicion del avion que tratamos. el segundo indica si el avion es nuevo(-1)
		int i = 0;
		
		while(i<this.getnumero())
		{
			
			if (this.getAvion(i).getId().equals(avion.getId()))
			{
				posicion[0]=i;
				break;
			}
			i++;
		}
		if (posicion[0] == -1){
			
			posicion[0] = this.getnumero();
			posicion[1]=-1;
			}
		return posicion;
	}
	public int GetPositionAvion(String id)
	//Out(int posicion) In(String id)
	//Busca un id en la tabla, si lo encuentra devuelve el indice de su posici�n en la tabla.
	{
		int posicion=-1;
		int i=0;
		while(i<numero)
		{
			if(id.equals(this.getAvion(i).getId()))
			{
				posicion=i;
				break;
			}
			i++;
		}
		return posicion;
	}
	public int contarAviones()
	//Out(int num) In()
	//Cuenta la cantidad de aviones existentes en la tabla y devuelve el n�mero, a la vez que actualiza el par�metro n�mero de la tabla
	{
		int num = 0;
		
		int c = 0;
		
		Aircraft a = aviones[c];
		try
		{
			while(c < aviones.length)
			{
				String p = aviones[c].getId();
				num++;
				c++;
			}
			numero=num;
		}
		catch(Exception NullPointerException)
		{
			
		}
		return num;
	}
}
