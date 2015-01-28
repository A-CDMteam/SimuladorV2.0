package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Arrays;


public class Aircraft implements Serializable {
	

	
	private String id;
	private String STATUS;
	private int[] tiempos = new int[5];  //Variables:{ETOT', ELDT, ETOT, EIBT, EOBT};
	private int[] actualizadas;
	private int milestone;	
	private int[] tiemposVuelo= new int[2]; //Variables: {TFIR,TAPP}; (TFIR = Tiempo de entrada en la FIR; TAPP = Tiempo de inicio de la Aproximacion)
	private int[] tiemposSecundarios= new int[5]; // Varibles: {ACGT, ASBT, ARDT, ASRT, TSAT};    ( ACGT = Inicio GH; ASBT = Inicio embarque; ARDT = Aircraft ready; ASRT = Peticion encender motores; TSAT = Autorizacion encender motores;) *siglas y tiempos definidos por el manual de EUROCONTROL
	private int[] infoVuelo; //Valores Constantes {Tiempo Vuelo, Tiempo en FIR, Tiempo en APP};
	
	private boolean nextMilestone; // Se utilizara para diferenciar los tipos de mensajes.
									// NextMilestone = false => Mensaje "Actualizar tiempos" : mensaje con un avion, de o para el InformationSharing
									// NextMilestone = true => Mensaje "nextMilestone": Mensaje del agente AvionMilestones a un agente para iniciar un Milestone (trigger)
	
	private int opcionMensaje; // La utilizaremos para definir que tipo de mensaje enviamos	
									// Opcion = -1 => Aviso de DDR2 leido
									// Opcion = 0 => Por defecto
									// Opcion = 1 => Mensaje inicio lectura .txt (receptores = AO y CFMU)
									// Opcion = 2 => Actualizacion de algun Milestone
									// Opcion = 3 => Definición parametros iniciales simulación.
									// Opcion = 4 => Mensaje que indica un retraso
									// Opcion = 5 => Mensaje que indica la hora actual, para retrassos de tipo 1
									// Opcion = 6 => Mensaje que avisa de que faltan 3 horas para la salida de un avion que ha estado en el aeropuerto desde el incio(sin llegar durante la simulacion)
									// Opcion = 7 => Mensaje que indica la hora actual, para retrassos de tipo 2??????(o 4??)
									// Opcion = 8 => Mensaje que avisa al GH del TurnAroundTime de un avion en concreto
									// Opcion = 9 => Mensaje que indica de que vamos a leer algun documento de Results.
	private String UltimoAgente; // Guardaremos el nombre del ultimo agente que modifico los datos del vuelo;
	
	private String[] ActuMilestone; // Milestone y tiempo en el que ocurrira el nuevo milestone (para las actualizaciones del MilestoneTrigger formato: [numeroMilestone nuevaHora] (un solo String separado por espacio
	

	private String ADEP; 	// Aeropuerto de Departure
	private String ADES; 	// Aeropuerto de Destino
	private String Modelo; 	// Modelo Aeronave
	private String AO=""; 		// Aerolinia que opera la aeronave
	private String Matricula; // Se utilitza como CALLSIGN2 ////////////////////////////// Cambiar "matricula" por "callsign2"
	
	
	private int Categoria; // 1==Super  2==Heavy  3==Medium  4==Low, si no sabem considerarem low
	
	
	private boolean boleano; //Boleao que se utiliza para las funciones que seria necesario devolver un avion y un bool. Con este boleano conseguimos meterlo dentro del avion y asi solo tener que devolver un return en la funcion
	private String Incidencias; // String utilizado para guardar una descripcion de cada una de las incidencias de los aviones (ejemplo: EOBT retrasado a XXXX por Airport)
	
	// Referencias vector Actualizadas: { ID, STATUS, ETOT', ELDT, ETOT, EIBT, EOBT, Milestone, AO, ADEP, ADES, Modelo, Matricula, ACGT,  ASBT, ARDT, ASRT, TSAT, Incidencias , TFIR , TAPP, mil 9 , mil10};
	//									{  1     2      3     4     5     6     7        8 	     9   10    11      12      13       14     15    16    17    18 	   19	      20     21   22       23}	
	
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
	public void setSTATUS(String i) {
		STATUS = i;
	}
	public int[] getTiempos() {
		return tiempos;
	}
	public void setTiempos(int[] tiempos) {
		this.tiempos = tiempos;
	}
	public int[] getActualizadas() {
		return actualizadas;
	}
	public void setActualizadas(int[] actualizada) 
	{
		this.actualizadas=actualizada;
	}
	public void AddActualizadas(int[] actu1, int[] actu2)
	//Out(int[] actualizadas) In(Int[] actu1, int[ ] actu2)
	//Crea un vector de actualizadas que contiene todos los valores de los 2 de entrada, si un valor aparece actualizado en ambas listas, el solo lo considera 1 vez.
	{
		int i=0;
		int[]SumaActualizadas = new int[actu1.length+actu2.length];
		while(i<actu1.length)
		{
			SumaActualizadas[i]=actu1[i];
			i++;
		}
		while(i<(actu1.length+actu2.length))
		{
			boolean encontrado=false;
			int p=0;
			while(p<actu1.length)
			{
				if(actu2[i-actu1.length]==actu1[p])
				{
					encontrado=true;
				}
				p++;
			}
			if(!encontrado)
			{
				SumaActualizadas[i]=actu2[i-actu1.length];
			}
			i++;
		}	
		this.actualizadas=SumaActualizadas;
	}
	public int getMilestone() {
		return milestone;
	}
	public void setMilestone(int milestone) {
		this.milestone = milestone;
	}
	public boolean getNextMilestone() {
		return nextMilestone;
	}
	public void setNextMilestone(boolean nextMilestone) {
		this.nextMilestone = nextMilestone;
	}
	public int getOpcionMensaje() {
		return opcionMensaje;
	}
	public void setOpcionMensaje(int opcionMensaje) {
		this.opcionMensaje = opcionMensaje;
	}
	public String getUltimoAgente() {
		return UltimoAgente;
	}
	public void setUltimoAgente(String ultimoAgente) {
		UltimoAgente = ultimoAgente;
	}
	public String getADEP() {
		return ADEP;
	}
	public void setADEP(String aDEP) {
		ADEP = aDEP;
	}
	public String getADES() {
		return ADES;
	}
	public void setADES(String aDES) {
		ADES = aDES;
	}
	public String getModelo() {
		return Modelo;
	}
	public void setModelo(String modelo) {
		
		Modelo = modelo;
	}
	public String getAO() {
		return AO;
	}
	public void setAO(String aO) {
		AO = aO;
	}
	public String getMatricula() {
		return Matricula;
	}
	public void setMatricula(String matricula) {
		Matricula = matricula;
	}
	public String[] getActuMilestone() {
		return ActuMilestone;
	}
	public void setActuMilestone(String[] actuMilestone) {
		ActuMilestone = actuMilestone;
	}
	public int[] getTiemposVuelo() {
		return tiemposVuelo;
	}
	public void setTiemposVuelo(int[] tiemposVuelo) {
		this.tiemposVuelo = tiemposVuelo;
	}
	public int[] getTiemposSecundarios() {
		return tiemposSecundarios;
	}
	public void setTiemposSecundarios(int[] tiemposSecundarios) {
		this.tiemposSecundarios = tiemposSecundarios;
	}
	public int[] getinfoVuelo() {
		return infoVuelo;
	}
	public void setinfoVuelo(int[] infoVuelo) {
		this.infoVuelo = infoVuelo;
	}
	public boolean getBoleano() {
		return boleano;
	}
	public void setBoleano(boolean boleano) {
		this.boleano = boleano;
	}
	public String getIncidencias() {
		return Incidencias;
	}
	public void setIncidencias(String incidencias) {
		Incidencias = incidencias;
	}
	public int getCat() {
		return Categoria;
	}
	public void setCat(int Cat) {
		this.Categoria = Cat;
	}
	
	// Constructors
	//Primer constructor
	public Aircraft() 
	{
		super();
		this.nextMilestone = false;
	}
	
	//Segundo constructor
	public Aircraft(String id, String sTATUS, int[] tiempos) 
	{
		super();
		this.id = id;
		STATUS = sTATUS;
		this.tiempos = tiempos;
		this.nextMilestone = false;

	}
	
	
	// Funciones útiles para trabajar con Flight
	public void setValorTiempos(int posicion, int valor)
	{
		this.tiempos[posicion] = valor;
	}
	public int getValorTiempos(int posicion)
	{
		return this.tiempos[posicion];
	}
	public void setValorTiemposVuelo(int posicion, int valor)
	{
		this.tiemposVuelo[posicion]=valor;
	}
	public int getValorTiemposVuelo(int posicion){
		
		return this.tiemposVuelo[posicion];
	}
	public void setValorTiemposSecundarios(int posicion, int valor)
	{
		this.tiemposSecundarios[posicion] = valor;
	}
	public int getValorTiemposSecundarios(int posicion){
		
		return this.tiemposSecundarios[posicion];
	}
	
	public void añadirIncidencia(int tiempoAntiguo, int tiempoNuevo, String variable, String nombreAgente,int hora){
		//Out(String NuevaIncidencia) In(int tiempoAntiguo, int tiempoNuevo, String variable, String nombreAgente, int hora)
		//Añade una incidencia a la lista de incidencias de un avión siempre que este cumpla con una serie de restricciones,
		//que esa incidencia concreta no se ha añadido anteriormente, que el tiempo antiguo coincide con el que realmente era 
		//el tiempo antiguo, y que el tiempoNuevo no se ha adelantado respecto el tiempo real del avion estudiado.
		boolean existe=false;
		String Incidencias = getIncidencias();
		//Miramos si queremos añadir esta incidencia(no repetida) tiempos correctos.
		if(Incidencias==null)
		{
			String NuevaIncidencia = (variable+" retrasado de "+tiempoAntiguo+" a "+tiempoNuevo+" por "+nombreAgente+" ("+ hora+")\n");
			this.setIncidencias(NuevaIncidencia);
		}
		else
		{
			String[] frase= Incidencias.split("\n"); 
			int i=0;
			while(i<frase.length)
			{
				if((variable+" retrasado de "+tiempoAntiguo+" a "+tiempoNuevo+" por "+nombreAgente + "( "+ hora+" )").equals(frase[i]))
				{
					existe=true;
					break;
				}
				i++;
			}
			if(!existe)
			{
				i=0;
				while(i<frase.length)	
				{
					String[] palabras = frase[i].split(" ");
					if(variable.equals(palabras[0]) && tiempoAntiguo<Integer.parseInt(palabras[5]))
					{
						tiempoAntiguo=Integer.parseInt(palabras[3]);
					}
					i++;
				}
				if(tiempoAntiguo>=tiempoNuevo)
				{
					existe=true;
				}
			}
			if(!existe)
			{
				String NuevaIncidencia = (variable+" retrasado de "+tiempoAntiguo+" a "+tiempoNuevo+" por "+nombreAgente+" ("+ hora+")\n");
				this.setIncidencias(Incidencias+ "" +NuevaIncidencia);
			}
		}
	}
	public void añadirIncidenciaLineaCompleta(String linea)
	//Out() In(String linea, String Incidencias)
	//Añade una incidencia a la lista de incidencias de un avión sin realizar ninguna comprobación.
	{
		Incidencias = Incidencias + linea + "\n";
	}
	public void newStatus(int opcion)
	//OUT() IN(int opcion)
	//Varia el Status de un avión.
	{
		String[] Status = {"INITIATED", "AIRBORNE", "FIR", "FINAL", "LANDED", "INBLOCK", "SEQUENCED", "BOARDING", "READY", "OFFBLOCK", "DEPARTED"};
		
		this.STATUS = Status[opcion];
		
	}
	
	public void printFlight(int opcion){ 
		//Out() In(int opcion)
		// Muestra por pantalla la información relacionada con el avión. Según el número de opcion se mostrará más o menos información.
// 					Sacamos los diferentes parametros del avion para 
				
// 					Opciones:
//					1: Se muestra solo el nombre del avion.
//					2: Se muestra el nombre del avion, su STATUS y el milestone.
//					3: Se muestra el nombre del avion y el parametro NextMilestone (true: mensaje nuevo milestone, false: mensaje actualizar tiempos)
//					4: Se muestra el nombre del avion y sus tiempos.
//					5: Se muestra el nombre del avion y las variables actualizadas.
//					6: Se muestran todos los datos del avion (solo tiempos principales).
//					7: Opcion 6 + actualizadas.
//					8: Opcion 5 + ultimo agente que actualiza.
//					9: Opcion 6 + tiempos secundarios + tiempos vuelo.
//					10: Se muestra el nombre del avion y sus tiempos secundarios + tiempos vuelo.
//					11: Opcion 9 - Actualizadas
//					12: Se muestra el nombre del avion y sus incidencias
				
				if(opcion == 1)
				{
					String id = this.getId();
					
					System.out.println("Avion: "+id);
					System.out.println("");
				}
				if(opcion == 2)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int milestone = this.getMilestone();
					
					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Milestone: " +milestone);
					System.out.println("");
				}
				if(opcion == 3)
				{
					String id = this.getId();
					boolean nextM = this.getNextMilestone();
					
					System.out.println("Avion: "+id);
					System.out.println("  NextMilestone: "+nextM);
					if(nextM){
					System.out.println("  (Mensaje siguiente milestone)");
					System.out.println("");}
					else{
					System.out.println("  (Mensaje actualizacion de tiempos)");
					System.out.println("");}
				}
				if(opcion == 4)
				{
					String id = this.getId();
					int ETOTprima = this.getValorTiempos(0);
					int ELDT = this.getValorTiempos(1);
					int ETOT = this.getValorTiempos(2);
					int EIBT = this.getValorTiempos(3);
					int EOBT = this.getValorTiempos(4);
					
					System.out.println("Avion: "+id);
					System.out.println("   Tiempos: ");
					System.out.println(" 	 ETOT' = " +ETOTprima);
					System.out.println(" 	 ELDT = " +ELDT);
					System.out.println(" 	 ETOT = " +ETOT);
					System.out.println(" 	 EIBT = " +EIBT);
					System.out.println(" 	 EOBT = " +EOBT);
					System.out.println("");
					
				}
				if(opcion == 5)
				{
					String id = this.getId();
					String act = Arrays.toString(this.getActualizadas());
					
					System.out.println("Avion: "+id);
					System.out.println("   Actualizadas: "+act);
					System.out.println("");
					
				}
				if(opcion == 6)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int ETOTprima = this.getValorTiempos(0);
					int ELDT = this.getValorTiempos(1);
					int ETOT = this.getValorTiempos(2);
					int EIBT = this.getValorTiempos(3);
					int EOBT = this.getValorTiempos(4);
					int milestone = this.getMilestone();

					
					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Tiempos: ");
					System.out.println(" 	ETOT' = " +ETOTprima);
					System.out.println(" 	ELDT = " +ELDT);
					System.out.println(" 	ETOT = " +ETOT);
					System.out.println(" 	EIBT = " +EIBT);
					System.out.println(" 	EOBT = " +EOBT);
					System.out.println("  Milestone: "+milestone);
					System.out.println("");
				}
				if(opcion == 7)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int ETOTprima = this.getValorTiempos(0);
					int ELDT = this.getValorTiempos(1);
					int ETOT = this.getValorTiempos(2);
					int EIBT = this.getValorTiempos(3);
					int EOBT = this.getValorTiempos(4);
					String act = Arrays.toString(this.getActualizadas());
					int milestone = this.getMilestone();

					
					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Tiempos: ");
					System.out.println(" 	ETOT' = " +ETOTprima);
					System.out.println(" 	ELDT = " +ELDT);
					System.out.println(" 	ETOT = " +ETOT);
					System.out.println(" 	EIBT = " +EIBT);
					System.out.println(" 	EOBT = " +EOBT);
					System.out.println("   Actualizadas: "+act);
					System.out.println("  Milestone: "+milestone);
					System.out.println("");
				}
				if(opcion == 8)
				{
					String id = this.getId();
					String act = Arrays.toString(this.getActualizadas());
					String ultimoagente = this.getUltimoAgente();
					
					System.out.println("Avion: "+id);
					System.out.println("   Actualizadas: "+act);
					System.out.println("   Agente: "+ultimoagente);
					System.out.println("");
				}
				if(opcion == 9)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int ETOTprima = this.getValorTiempos(0);
					int ELDT = this.getValorTiempos(1);
					int ETOT = this.getValorTiempos(2);
					int EIBT = this.getValorTiempos(3);
					int EOBT = this.getValorTiempos(4);
					String act = Arrays.toString(this.getActualizadas());
					int milestone = this.getMilestone();
					int ACGT = this.getValorTiemposSecundarios(0);
					int ASBT = this.getValorTiemposSecundarios(1);
					int ARDT = this.getValorTiemposSecundarios(2);
					int ASRT = this.getValorTiemposSecundarios(3);
					int TSAT = this.getValorTiemposSecundarios(4);
					int TFIR = this.getValorTiemposVuelo(0);
					int TAPP = this.getValorTiemposVuelo(1);
					
					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Tiempos: ");
					System.out.println(" 	ETOT' = " +ETOTprima);
					System.out.println(" 	ELDT = " +ELDT);
					System.out.println(" 	ETOT = " +ETOT);
					System.out.println(" 	EIBT = " +EIBT);
					System.out.println(" 	EOBT = " +EOBT);
					
					System.out.println("  Actualizadas: "+act);
					System.out.println("  Milestone: "+milestone);
					System.out.println("  Tiempos Secundarios: ");
					System.out.println("     ACGT: "+ACGT);
					System.out.println("     ASBT: "+ASBT);
					System.out.println("     ARDT: "+ARDT);
					System.out.println("     ASRT: "+ASRT);
					System.out.println("     TSAT: "+TSAT);
					System.out.println("	 TFIR: "+TFIR);
					System.out.println("     TAPP: "+TAPP);
					System.out.println("");
				}
				if(opcion == 10)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int ACGT = this.getValorTiemposSecundarios(0);
					int ASBT = this.getValorTiemposSecundarios(1);
					int ARDT = this.getValorTiemposSecundarios(2);
					int ASRT = this.getValorTiemposSecundarios(3);
					int TSAT = this.getValorTiemposSecundarios(4);
					int TFIR = this.getValorTiemposVuelo(0);
					int TAPP = this.getValorTiemposVuelo(1);

					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Tiempos secundarios: ");
					System.out.println("     ACGT: "+ACGT);
					System.out.println("     ASBT: "+ASBT);
					System.out.println("     ARDT: "+ARDT);
					System.out.println("     ASRT: "+ASRT);
					System.out.println("     TSAT: "+TSAT);
					System.out.println("	 TFIR: "+TFIR);
					System.out.println("     TAPP: "+TAPP);
					System.out.println("");
				}
				if(opcion == 11)
				{
					String id = this.getId();
					String status = this.getSTATUS();
					int ETOTprima = this.getValorTiempos(0);
					int ELDT = this.getValorTiempos(1);
					int ETOT = this.getValorTiempos(2);
					int EIBT = this.getValorTiempos(3);
					int EOBT = this.getValorTiempos(4);
					int milestone = this.getMilestone();
					int ACGT = this.getValorTiemposSecundarios(0);
					int ASBT = this.getValorTiemposSecundarios(1);
					int ARDT = this.getValorTiemposSecundarios(2);
					int ASRT = this.getValorTiemposSecundarios(3);
					int TSAT = this.getValorTiemposSecundarios(4);
					int TFIR = this.getValorTiemposVuelo(0);
					int TAPP = this.getValorTiemposVuelo(1);
					
					System.out.println("Avion: "+id);
					System.out.println("  STATUS: "+status);
					System.out.println("  Tiempos: ");
					System.out.println(" 	ETOT' = " +ETOTprima);
					System.out.println(" 	ELDT = " +ELDT);
					System.out.println(" 	ETOT = " +ETOT);
					System.out.println(" 	EIBT = " +EIBT);
					System.out.println(" 	EOBT = " +EOBT);
					
					System.out.println("  Milestone: "+milestone);
					System.out.println("  Tiempos Secundarios: ");
					System.out.println("     ACGT: "+ACGT);
					System.out.println("     ASBT: "+ASBT);
					System.out.println("     ARDT: "+ARDT);
					System.out.println("     ASRT: "+ASRT);
					System.out.println("     TSAT: "+TSAT);
					System.out.println("     TFIR: "+TFIR);
					System.out.println("     TAPP: "+TAPP);
					System.out.println("");
				}
				if(opcion == 12)
				{
					String id = this.getId();
					String incidencias = this.getIncidencias();
					
					System.out.println("Avion: "+id);
					System.out.println("Incidencias: ");
					System.out.println(incidencias);
					System.out.println("");
				}
		}
	}