package Agentes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import testerComunicaciones.FlightP;
import testerComunicaciones.ReceptorBucle.RecepcionAvion;

import com.google.gson.Gson;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import Agentes.Aircraft;

public class CDM extends Agent {

	/**
	 * @param args
	 */

	public static Tabla tabla;
	private AID IS = new AID("InfoSharing", AID.ISLOCALNAME);
	private AID MT = new AID("MilestoneTrigger", AID.ISLOCALNAME);

	// Inicio del agente, como en cada agente variara el metodo "setup()", he
	// optado por tenerlo comentado en el agente "padre" y asi en el momento de
	// programar los hijos solo hay que copiarlo y cambiar los parametros
	// necesarios

//	protected void setup() {
//		System.out.println(getAID().getLocalName() + " operativo.");
//
//		tabla = new Tabla(NumeroAvionesSimulacion);
//
//		addBehaviour(new RecepcionAvion());
//	}
//
//	class RecepcionAvion extends Behaviour {
//
//		private boolean fin = false;
//
//		// Podemos canviar la opcion "Muestralo" para que la recepcion muestre
//		// por consola si recibe el mensaje o no:
//		// Muestralo = 0 --> No se muestra por pantalla ninguna informacion;
//		// Muestralo = 1 --> Se muestra por pantalla
//		// "Se ha recibido el avion X";
//		// Muestralo = 2 --> Se muestra por pantalla el contendio del mensaje
//		// (el contenido del avion enviado);
//
//		int Muestralo = 0;
//
//		public void action() {
//
//			ACLMessage mensaje = blockingReceive();
//
//			if (mensaje != null) {
//				Flight avion = RecepcionMensaje(mensaje, Muestralo);
//				int posicion = PosicionTabla(avion);
//				tabla.setAvion(avion, posicion);
//				
//				// Trabajamos con los protocolos de cada uno de los agentes del CDM
//
//			}
//
//		}
//
//		public boolean done() {
//			return fin;
//		}
//
//	}

	public int Actuamos(Aircraft avion, int[] opcion) {
		//In(Aircraft Avion, int[] opcion) Out (int)
		// Significado returns:
		// Return 1: La función ha detectado que un dato de "opcion" ha sido
		// actualizado. Hace de trigger para que el agente actue.
		// Return 0: La función no ha detectado que ningun dato de "opcion" ha
		// sido actualizado. Return 2: La función ha detectado que es un mensaje de NEXTMILESTONE.

		if (avion.getNextMilestone() == false) {
			boolean Actuar;

			// this.tabla.setAvion(avion,posicion);

			Actuar = comprobarActualizadas(avion, opcion);

			if (Actuar == false) {
				return 0;
			} else {
				return 1;
			}
		} else {

			avion.setNextMilestone(false);
			return 2;
		}
	}

	public void PublicarInformacion(Aircraft avion, int muestralo) throws IOException 
	//In(Aircraft avion, int muestralo) Out ()
	//Es la función a la que recurriremos cada vez que queramos avisar al IS de alguna actualización. 
	//Con el valor muéstralo podemos decidir si queremos mostrar alguna información por pantalla.
	{
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(IS);
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String.   (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
		
		if (muestralo == 1) {
			System.out.println(getLocalName()
					+ ": Publica la informacion del vuelo: ");
			System.out.println("Avion" + avion.getId());
		}
		if (muestralo == 2) {

			System.out.println(getLocalName()
					+ ": Publica la informacion del vuelo: ");
			System.out.println("Avion" + avion.getId());
			
			// Mostramos la informacion en el mensaje:
			
			System.out.println(getLocalName() + ": Informacion publicada");

			System.out.println("Contenido del mensaje:");
			System.out.println(" ");
			System.out.println(msg.getContent().toString());

			
		}
		if (muestralo == 0) {
			// No se muestra nada por consola;

		}

	}

	public boolean comprobarActualizadas(Aircraft avion, int[] opcion) {
		//In(Aircraft Avion, int[] opcion) Out (boolean bool)
		// Significado bool:
		// bool true: La función ha detectado que un dato de "opcion" ha sido
		// actualizado. Hace de trigger para que el agente actue.
		// bool false: La función no ha detectado que ningun dato de "opcion" ha
		// sido actualizado. Return 2: La función ha detectado que es un mensaje de NEXTMILESTONE.
		boolean bool = false;

		if (avion.getActualizadas() != null) {
			for (int i = 0; i < avion.getActualizadas().length; i++) {

				for (int c = 0; c < opcion.length; c++) {
					int e = avion.getActualizadas()[i];
					if (e == opcion[c])
						bool = true;
				}
			}
		}
		return bool;

	}

	public Aircraft RecepcionMensaje(ACLMessage msg, int muestralo) 
	//In(ACLMessage msg, int muestralo) Out (Aircraft avion)
	//Extrae la informacion de ACLMessage a una classe Aircraft
	{

		//Extraemos la información del mensaje de entrada, y la guard
		Aircraft avion = extraerInfo(msg);

		if (muestralo == 1) {
			System.out.println(getLocalName()
					+ ": acaba de recibir el siguiente mensaje: ");
			System.out.println("Avion " + avion.getId());
		}
		if (muestralo == 2) {

			System.out.println(getLocalName() + " Mensaje recibido");

			System.out.println("Contenido del mensaje:");
			System.out.println(" ");
			System.out.println(msg.getContent().toString());

			System.out.println(getLocalName()
					+ ": acaba de recibir el siguiente mensaje: ");
			System.out.println("Avion" + avion.getId());
		}
		if (muestralo == 0) {
			// No se muestra nada por consola;

		}

		return avion;
	}
	
	public int restatemps (int A, int B)
	//In(int A, int B) out (int C)
	//resta 2 tiempos teniendo en cuenta hhmm
	{
		int hA = A/100;
		int minA = A%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		int hB = B/100;
		int minB = B%100;
		int C=hA*60+minA-hB*60-minB;
		
		return C;
	}
	
	public int sumatemps (int A, int min)
	//In(int A, int min) out (int C)
	//suma A y los minutos min tiempos teniendo en cuenta hhmm
	{
		int hA = A/100;
		int minA = A%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		
		int C=hA*60+minA+min;
		int horafinal =C/60;
		horafinal=horafinal*100+C-horafinal*60;
		
		return horafinal;
	}
	
	public String TempsQueRetrasem(int X)
	{
		//In(int X) Out(String TiempoVar)
		//Para cada valor de entrada X encuentra cual es el tiempo que se retrasará en los retrasos.
		//X equivale al valor de “Situación” en los retrasos tipo 1 y tipo 2
		
		//Miramos el tiempo que se retrasara:
		//{ETOT', ELDT, ETOT, EIBT, EOBT};
		//{ACGT, ASBT, ARDT, ASRT, TSAT};
		//{TFIR, TAPP};
		
		String TiempoVar=null;
		if(X==0)
		{
			TiempoVar="TAPP";
		}
		if(X==1)
		{
			TiempoVar="ACGT";
		}
		if(X==2)
		{
			TiempoVar="ETOT";
		}
		if(X==3)
		{
			TiempoVar="EOBT";
		}
		
		return TiempoVar;
	}
	
	public boolean MirarmodeliAO(Aircraft Avion, String AOs, String Modelos)
	//In(Aircraft Avion, String AOs, String Modelos) Out(boolean correctos)
	//Comprueba si el modelo y la AO del avión está dentro de la lista de modelos y AOs de entrada a la función
	{
		boolean correctos=false;
		boolean AOcorrecto=false;
		boolean Modelocorrecto=false;
		
		String AO=Avion.getAO();
		String Modelo=Avion.getModelo();
		
		int i=0;
		String[] AOslist=AOs.split("-");
		String[] Modeloslist=Modelos.split("-");
		if(AOs.equals("-"))
			AOcorrecto=true;
		if(Modelos.equals("-"))
			Modelocorrecto=true;
		
		while(i<AOslist.length && AOcorrecto==false)
		{
			if(AO.equals(AOslist[i]))
				AOcorrecto=true;
			i++;
		}
		i=0;
		while(i<Modeloslist.length && Modelocorrecto==false)
		{
			if(Modelo.equals(Modeloslist[i]))
				Modelocorrecto=true;
			i++;
		}
		if(Modelocorrecto && AOcorrecto)
			correctos=true;
		
		return correctos;
	}
	
	public String[] AlgunRestraso(String Id, int Milestone,ArrayList Retrasoavion)
	//In(String Id, int Milestone, ArrayList Retrasoavion)
	//Busca si la lista retraso avión tiene guardado una información 
	//correspondiente al avión con callsign=Id en el momento de Milestone.
	{
		String[] Retraso=new String[2];
		Retraso[0]="Ninguno";
		Retraso[1]=String.valueOf(0);
		int i=0;
		while(i<Retrasoavion.size())
		{
			String[] palabras=((String) Retrasoavion.get(i)).split(" ");
			if(Id.equals(palabras[0]) && Milestone==Integer.parseInt(palabras[1]))
			{
				int k=4;
				String Comment="";
				while(k<palabras.length)
				{
					Comment=Comment + " "+palabras[k];
					k=k+1;
				}
				Retraso[0]=Id + " " + palabras[2]+" "+palabras[3]+Comment;
				Retraso[1]=String.valueOf(i);
				break;
			}
			i++;
		}
		
		
		
		return Retraso;
	}	

	public String DetectarRetrasoTiempo(String hora,ArrayList Retrasotiempo)
	//In(String hora, ArrayList Retrasotiempo) Out (retraso)
	//Busca si la lista Retrasotiempo tiene guardado una información correspondiente a un retraso
	//para la hora en la que nos encontramos
	{
		String retraso="";
		int HoraActual=Integer.parseInt(hora);
		int i=0;
		boolean encontrado=false;
		while(i<Retrasotiempo.size() && encontrado==false)
		{
			int HoraRetraso=Integer.parseInt(((String)Retrasotiempo.get(i)).split(" ")[2]);
			if(HoraRetraso==HoraActual)
			{
				retraso=retraso+String.valueOf(i);
				encontrado = true;
			}
			i++;
		}
		return retraso;  //Retorna un numero, que indica que tenim d'aplicar el retras, es la posicio dins l'arrayList del retras a aplicar
	}
	
	public String TrobarRetraso(String Hora,ArrayList Retrasopista)
	//In(String Hora, ArrayList Restrasopista) Out (String retraso)
	//Busca si la lista Retrasopista tiene guardado una información correspondiente 
	//a un retraso para la hora en la que nos encontramos
	{
		String retraso="";
		int hora = Integer.parseInt(Hora);
		int i =0;
		boolean encontrado=false;
		while(i<Retrasopista.size() && encontrado==false)
		{
			int HoraRetraso=Integer.parseInt(((String)Retrasopista.get(i)).split(" ")[2]);
			if(HoraRetraso==hora)
			{
				retraso=retraso+((String)Retrasopista.get(i)).split(" ")[0]+" "+((String)Retrasopista.get(i)).split(" ")[1]+" "+((String)Retrasopista.get(i)).split(" ")[3];
				encontrado=true;
			}
			i++;
		}
		if (encontrado==true)
		{
			String[] palabras = ((String)Retrasopista.get(i-1)).split(" ");
			i=4;
			while(i<palabras.length)
			{
				retraso=retraso+" "+palabras[i];
				i++;
			}
		}
		return retraso;
	}
		
	public int GetTempsPreviMil(int a)
	//In(int a) Out (int Tiempo)
	//Recibe el valor de un milestone e indica cual es el tiempo previo
	{
		//		{ETOT', ELDT, ETOT, EIBT, EOBT}
		//Return: 1 	 2		3	 4		5
		//		{ACGT, ASBT, ARDT, ASRT, TSAT}
		//Return: 6		7	   8	9	  10
		//		{TFIR, TAPP}
		//Return: 11    12
		int Tiempo=-1;
		if(a==1)
			Tiempo=1;
		if(a==2)
			Tiempo=1;
		if(a==3)
			Tiempo=1;
		if(a==4)
			Tiempo=11;
		if(a==5)
			Tiempo=12;
		if(a==6)
			Tiempo=2;
		if(a==7)
			Tiempo=4;
		if(a==8)
			Tiempo=6;
		if(a==9)
			Tiempo=6;
		if(a==10)
			Tiempo=6;
		if(a==11)
			Tiempo=7;
		if(a==12)
			Tiempo=8;
		if(a==13)
			Tiempo=9;
		if(a==14)
			Tiempo=10;
		if(a==15)
			Tiempo=5;
		return Tiempo;
	}
	
	public int GetTempsPostMil(int a)
	//In(int a) Out (int Tiempo)
	//Recibe el valor de un milestone e indica cual es el tiempo posterior
	{
		//		{ETOT', ELDT, ETOT, EIBT, EOBT}
		//Return: 1 	 2		3	 4		5
		//		{ACGT, ASBT, ARDT, ASRT, TSAT}
		//Return: 6		7	   8	9	  10
		//		{TFIR, TAPP}
		//Return: 11    12
		int Tiempo=-1;
		if(a==1)
			Tiempo=2;
		if(a==2)
			Tiempo=2;
		if(a==3)
			Tiempo=2;
		if(a==4)
			Tiempo=12;
		if(a==5)
			Tiempo=2;
		if(a==6)
			Tiempo=4;
		if(a==7)
			Tiempo=6;
		if(a==8)
			Tiempo=7;
		if(a==9)
			Tiempo=7;
		if(a==10)
			Tiempo=7;
		if(a==11)
			Tiempo=8;
		if(a==12)
			Tiempo=9;
		if(a==13)
			Tiempo=10;
		if(a==14)
			Tiempo=5;
		if(a==15)
			Tiempo=3;
		return Tiempo;
		
	}
	
	public int GetTempsActualMil(Aircraft avion,int a)
	//In(Aircraft avion, int a) Out (int Tiempo)
	//Recibe el valor de un milestone correspondiente a un avión. 
	//Busca la hora en la que sucede ese milestone para ese avión.
	{
		int Tiempo=9999;
		if(a==1)
			Tiempo=Calculotiempo(avion.getValorTiempos(0),300,0);
		if(a==2)
			Tiempo=Calculotiempo(avion.getValorTiempos(0),200,0);
		if(a==3)
			Tiempo=avion.getValorTiempos(0);
		if(a==4)
			Tiempo=avion.getValorTiemposVuelo(0);
		if(a==5)
			Tiempo=avion.getValorTiemposVuelo(1);
		if(a==6)
			Tiempo=avion.getValorTiempos(1);
		if(a==7)
			Tiempo=avion.getValorTiempos(3);
		if(a==8)
			Tiempo=avion.getValorTiemposSecundarios(0);
		if(a==9)
			Tiempo=avion.getValorTiemposSecundarios(0);
		if(a==10)
			Tiempo=avion.getValorTiemposSecundarios(0);
		if(a==11)
			Tiempo=avion.getValorTiemposSecundarios(1);
		if(a==12)
			Tiempo=avion.getValorTiemposSecundarios(2);
		if(a==13)
			Tiempo=avion.getValorTiemposSecundarios(3);
		if(a==14)
			Tiempo=avion.getValorTiemposSecundarios(4);
		if(a==15)
			Tiempo=avion.getValorTiempos(4);
		if(a==16)
			Tiempo=avion.getValorTiempos(2);
		return Tiempo;
		
	}
	
	public int TreureTemps(Aircraft a,String Temps)
	//In(Aircraft a, String Temps) Out (int valor)
	//Recibe un avión y la información del tiempo que tiene de extraer del avión.
	{
		int valor=0;
		//		{ETOT', ELDT, ETOT, EIBT, EOBT}
		//		{ACGT, ASBT, ARDT, ASRT, TSAT}
		//		{TFIR, TAPP}
		if(Temps.equals("ETOT'"))
			valor=a.getValorTiempos(0);
		if(Temps.equals("ELDT"))
			valor=a.getValorTiempos(1);
		if(Temps.equals("ETOT"))
			valor=a.getValorTiempos(2);
		if(Temps.equals("EIBT"))
			valor=a.getValorTiempos(3);
		if(Temps.equals("EOBT"))
			valor=a.getValorTiempos(4);
		if(Temps.equals("ACGT"))
			valor=a.getValorTiemposSecundarios(0);
		if(Temps.equals("ASBT"))
			valor=a.getValorTiemposSecundarios(1);
		if(Temps.equals("ARDT"))
			valor=a.getValorTiemposSecundarios(2);
		if(Temps.equals("ASRT"))
			valor=a.getValorTiemposSecundarios(3);
		if(Temps.equals("TSAT"))
			valor=a.getValorTiemposSecundarios(4);
		if(Temps.equals("TFIR"))
			valor=a.getValorTiemposVuelo(0);
		if(Temps.equals("TAPP"))
			valor=a.getValorTiemposVuelo(1);
		
		return valor;
	}
	
	public int[] TreureActualizada(String Temps)
	//In(String Temps) Out (int[] a)
	//Recibe un tiempo, y busca cual es el numero para el vector actualizadas
	{
		// Referencias vector Actualizadas: { ID, STATUS, ETOT', ELDT, ETOT, EIBT, EOBT, Milestone, AO, ADEP, ADES, Modelo, Matricula, ACGT,  ASBT, ARDT, ASRT, TSAT, Incidencias, TFIR, TAPP};
		//									{  1     2      3     4     5     6     7        8 	     9   10    11      12      13       14     15    16    17    18 	   19	    20     21}	
		
		int[] a=new int[2];
		a[1]=19;//Si entrem per aqui la incidencia sempre s'ha actualitzat
		if(Temps.equals("ELDT"))
			a[0]=4;
		if(Temps.equals("ETOT"))
			a[0]=5;
		if(Temps.equals("EIBT"))
			a[0]=6;
		if(Temps.equals("EOBT"))
			a[0]=7;
		if(Temps.equals("ACGT"))
			a[0]=14;
		if(Temps.equals("ASBT"))
			a[0]=15;
		if(Temps.equals("ARDT"))
			a[0]=16;
		if(Temps.equals("ASRT"))
			a[0]=17;
		if(Temps.equals("TSAT"))
			a[0]=18;
		if(Temps.equals("TFIR"))
			a[0]=20;
		if(Temps.equals("TAPP"))
			a[0]=21;
		return a;
	}
	
	public Aircraft extraerInfo(ACLMessage msg) 
	//In(ACLMessage msg) Out (Aircraft avion)
	//Extrae la informacion de ACLMessage a una classe Aircraft
	{

		Gson gson = new Gson();
		String jsonInput = msg.getContent();

		Aircraft avion = new Aircraft();

		avion = gson.fromJson(jsonInput, avion.getClass());

		return avion;
	}
	
	public Aircraft extraerInfoResults (String msg)//msg=jsonInput de los otros casos
	//In(String msg) Out (Aircraft avion)
	//Extrae la informacion de un ACLMessage escrito en un String a una classe Aircraft
	{ 
		
		Gson gson = new Gson();
		Aircraft avion = new Aircraft();

		avion = gson.fromJson(msg, avion.getClass());

		return avion;
	}

	protected void takeDown() 
	{ // Muestra un mensaje cuando se hace doDelete();
		System.out.println("Agente " + getAID().getLocalName() + " se cierra.");
		
		maydaymayday(); //Enviamos un mensaje cuando cerramos el agente, para poder saber en que momento se cerró (en el informa de mensajes del sniffer)
		
		super.takeDown();
	}
	
	public void maydaymayday()
	{ // Envia un mensaje cuando se cierra, así se puede ver en el momento en el que falla
		
		ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
		
		AID error = new AID ("error", AID.ISLOCALNAME);

		msg.addReceiver(error);
		
		// Enviamos el mensaje
		msg.setContent("Houston, tenemos un problema...");
		
		send(msg);
	} 
	
	public void mensajeMilestone(Aircraft avion, String[] milestone, int opcion)
	//In (Aircraft avion, String[] milestone, int opcion) Out()
	//Envía un mensaje al MilestoneTrigger para avisar de que se ha actualizado algún milestone de la lista	
	{
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				msg.addReceiver(MT);
				
				// Preparamos los datos del avion para que el MT lo entienda como un nuevo milestone
				avion.setOpcionMensaje(2);
				avion.setActuMilestone(milestone);
				
				// Envio
				Gson gson = new Gson();
				String jsonOutput = gson.toJson(avion);
				
				msg.setContent(jsonOutput);
				
				send(msg);
				
				// Mostrar por consola el milestone actualizado
				if(opcion == 1)
				{
					System.out.println("Avion: "+avion.getId()+" - Milestone actualizado: "+Arrays.toString(avion.getActuMilestone())+ " (Agente "+this.getLocalName()+")");
				}
			}
	
	public int Calculotiempo(int tiempo, int delay, int operacion)
	//In(int tiempo, int delay, int operacion) Out(result)
	//Suma o resta los minutos delay al valor tiempo, teniendo en cuenta hhmm
	{ // Operaciones con los tiempos (opcion == 1: sumar, opcion == 0: restar)
		
		// Separamos el tiempo en horas y minutos
		int ht = tiempo/100;
		int mint = tiempo%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		int hd = delay/100;
		int mind = delay%100;
		int result;
		
		if(operacion==1)
		{
			int finaltime=ht*60+hd*60+mint+mind;
		
			result = finaltime/60;
			result=result*100+finaltime-result*60;
		}
		else
		{
			int finaltime=ht*60-hd*60+mint-mind; //si delay es mayor a tiempo tendremos problemas
			
			result = finaltime/60;
			result=result*100+finaltime-result*60;
		}
		return result;
	}

	public int AplicarNormal (int variacio,boolean variacionesnormales)
	//In(int variacio, boolean variacionesnormales) Out(int Delay)
	//Calculara el valor del delay a añadir al tiempo siguiendo una probabilidad normal
	{ 
		if(variacionesnormales==false)
			return 0;
		else
		{
			Random r = new Random();
			
			double val = r.nextGaussian()*variacio;
			int Delay = (int) Math.round(val);
		
			if(Delay<=0)
				return 0;
			else
				return Delay;
		}
	}

	public String valorparametro(String parametro)
	//In(String parametro) Out (String valor)
	// Recoje el valor del parametro del avionParametros (INPUT: linea parametro, ejemplo "Aviones 12")
	{ 
		String[] parametroyvalor = parametro.split("\t");
		
		String valor = parametroyvalor[1];
		
		return valor;			
	}

	public String[] sacarParametros(Aircraft avion)
	//In(Aircraft avion) Out(String[] listaparametros)
	//Saca los parámetros de las incidencias de avion y los devuelve en forma de vector de Strings
	
	{ 		// Parametros: 
//		[0]: taxiIn 
//		[1]: taxiOut 
//		[2]: VelocidadSimulación
//		[3]: rutaArchivos
//		[4]: NumeroAviones
//		[5]: Aeropuerto
//		[6]: Aerolineas
//		[7]: op/hora llegada
//		[8]: op/h salida 
//		[9]: Categorias
//		[10]: WayPoints FIR
//		[11]: WayPoints APP
//		[12]: Empresas de GH i Aerolineas con las que trabaja cada una
		
		String Parametros = avion.getIncidencias();
		
		String[] listaparametros = Parametros.split("\n");
		
		return listaparametros;
	}
	public String sacarParametros(Aircraft avion, int posicion)
	//In(Aircraft avion) Out(String)
	//Saca los parámetros de las incidencias de avion y devuelve el que se encuentra en el indice posicion
	{ 		// Parametros: 
//		[0]: taxiIn 
//		[1]: taxiOut 
//		[2]: VelocidadSimulación
//		[3]: rutaArchivos
//		[4]: NumeroAviones
//		[5]: Aeropuerto
//		[6]: Aerolineas
//		[7]: op/hora llegada
//		[8]: op/h salida 
//		[9]: Categorias
//		[10]: WayPoints FIR
//		[11]: WayPoints APP
//		[12]: Empresas de GH i Aerolineas con las que trabaja cada una(GH1-AO1-AO2 \t GH2-AO3-AO4...)
		
		String Parametros = avion.getIncidencias();
		
		String[] listaparametros = Parametros.split("\n");
		try
		{
			return listaparametros[posicion];
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			return ("");
		}
	}

	public String[] listaaerolineas(String[] parametros)
	//In(String[] parametros) Out(String[] aerolineas)
	//Devuelve un lista de Strings, en cada palabra contiene el nombre de un agente AO.
	{
		String[] al = parametros[6].split("\t");
		String[] aerolineas = new String[al.length + 1];
		int i = 0;
		while(i < al.length)
		{
			aerolineas[i] = "AO-"+al[i];
			i++;
		}
		aerolineas[i] = "AO-GEN";
		
    return aerolineas;
	}
}