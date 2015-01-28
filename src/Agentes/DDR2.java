package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

public class DDR2 extends CDM {
	//String name="xavi";
	
	
	String rutaSO6= ""; //C:/Users/"+name+"/workspace/SimuladorV1.3/FULLSO6.txt";
	
	String rutaInput = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/INPUT2.txt";
	
	String[] WPFIR; //= {"PEPE1","PEPE2","PEPE3","PEPE4"};
	String[] WPAPP; //= {"PEPE5","PEPE6"};
	String Airport = ""; // "LEBL";
	int leido = -1;
	
	ArrayList Llegadas=new ArrayList();
	ArrayList Salidas=new ArrayList();

	ArrayList Callsigntodos=new ArrayList();
	
	protected void setup() {
		System.out.println(getAID().getLocalName() + " operativo.");

		addBehaviour(new SO6in());
	}

	class SO6in extends Behaviour {

		private boolean fin = false;

		//Llega el mensaje de inicializacion, leemos el documento SO6, y escribimos el FP en el fichero.
		//Mandamos un mensaje al SimulatorInput para que sepa que puede continuar con sus tareas,
		//o avisandole de que tenemos algun problema con el DDR2(SO6)

			
		
		
		public void action() {

			//Bloquea el comportamiento hasta el momento en que el agente reciba un mensaje
			ACLMessage mensaje = blockingReceive();

			if (mensaje != null) 
			{
				Aircraft avion = RecepcionMensaje(mensaje, 0);
				String[] rutas=avion.getIncidencias().split(" ");
				try{
				
					
					rutaSO6=rutas[1]+rutas[0];
					rutaInput=rutas[1]+"Input.txt";
					String wpfir=rutas[2];
					WPFIR=wpfir.split("\t");
					String wpapp=rutas[3];
					WPAPP=wpapp.split("\t");
					Airport=rutas[4];
					leido = -1;
					leerSO6();
					System.out.println("ja");
					MandarInfo();
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					leido = -1;
					MandarInfo();
				}				
			}
		}

		public boolean done() {
			return fin;
		}
	}
	public void leerSO6()
	//in (-String rutaSO6, String Airport, String[ ] WPFIR, String[ ] WPAPP) Out (Input.txt)
	//Esta función realiza casi todo el trabajo del agente DDR2, es la encargada de procesar 
	//el documento de entrada DDR2 para sacar por escrito el documento Input.txt
	{
		File archivoSO6 = new File(rutaSO6);
		FileReader frSO6 = null;
	    BufferedReader brSO6 = null;
	    try
	    {
	    	frSO6 = new FileReader( archivoSO6 );
	    	brSO6 = new BufferedReader( frSO6 );
	    
	   		String lineaSO6; //Obtenemos el contenido del archivo linea por linea
	   		
	   		
	   		
	   		ArrayList Llegadasreduit = new ArrayList();//Per trobar relacions entre arribades i sortides
	   		ArrayList Salidasreduit = new ArrayList();
	   		
	   		lineaSO6 = brSO6.readLine();
	   		
	   		int borrar=0;
	   		System.out.println("leemos DDR2");
	    	while( lineaSO6 != null )
	    	{
	    		
	    		String[] palabras = lineaSO6.split(" ");
	    		
	    		if(borrar==15)
	    			break;
	    		if(palabras[1].equals(Airport) && palabras[17].equals("1")) //Tenim una sortida (teoricament palabras[17]==1)
	    		{
	    			//borrar++;
	    			palabras[17]="0"; //Aixó ens facilita saber quan comença el seguent avio, just quan palabras[17]==1;
	    			String[] ConstruccioSortida = new String[6];
	    			ConstruccioSortida[0]="2";
	    			String callsign=palabras[9];
	    			boolean valido=false;
	    			int num=0;
	    			while(valido==false)
	    			{
	    				if(Callsigntodos.contains(callsign))
	    				{
	    					callsign=callsign+"/";
	    					num++;
	    				}
	    				else
	    					valido=true;
	    			}
	    			Callsigntodos.add(callsign);
	    			ConstruccioSortida[1]=callsign;// Callsign
	    			ConstruccioSortida[2]=palabras[3];// Modelo
	    			ConstruccioSortida[3]=palabras[1];// Aeropuerto de Salida(el nuestro)
	    			ConstruccioSortida[4]=palabras[2];// Aeropuerto de Destino
	    			String[] Hora = palabras[4].split(""); //Hora de sortida hhmmss
	    			ConstruccioSortida[5]=Hora[1]+Hora[2]+Hora[3]+Hora[4]; //Hora de sortida hhmm (ss eliminats)
	    			Salidas.add(ConstruccioSortida[0]+"\t"+ConstruccioSortida[1]+"\t"+ConstruccioSortida[2]+"\t"+ConstruccioSortida[3]+"\t"+ConstruccioSortida[4]+"\t"+ConstruccioSortida[5]);  //Sortida afegida a la llista
	    			Salidasreduit.add(Salidasreduit.size()+" "+GetAO(ConstruccioSortida[1])+" "+ConstruccioSortida[2]+" "+ConstruccioSortida[5]);
	    			lineaSO6 = brSO6.readLine();  //Saltem les lineas fins el seguent
	    			palabras = lineaSO6.split(" ");
	    			String actcallsign=palabras[9];
	    			int i=0;
	    			while(i<num)
	    			{
	    				actcallsign=actcallsign+"/";
	    				i++;
	    			}
	    			while(palabras[1].equals(Airport) && ConstruccioSortida[1].equals(actcallsign))
	    			{
	    				lineaSO6 = brSO6.readLine();  //Saltem les lineas fins el seguent
	    				if(lineaSO6==null)
	    					break;
	    				palabras = lineaSO6.split(" ");
	    				actcallsign=palabras[9];
		    			i=0;
		    			while(i<num)
		    			{
		    				actcallsign=actcallsign+"/";
		    				i++;
		    			}
	    			}
	    		}
	    		else if(palabras[2].equals(Airport) && palabras[17].equals("1")) //Tenim una arribada (teoricament palabras[17]==1)
	    		{
	    			palabras[17]="0"; //Aixó ens facilita saber quan comença el seguent avio, just quan palabras[17]==1;
	    			String[] ConstruccioArribada = new String[9];
	    			
	    			ConstruccioArribada[0]="1";
	    			String callsign=palabras[9];
	    			boolean valido=false;
	    			int num=0;
	    			while(valido==false)
	    			{
	    				if(Callsigntodos.contains(callsign))
	    				{
	    					callsign=callsign+"/";
	    					num++;
	    				}
	    				else
	    					valido=true;
	    			}
	    			Callsigntodos.add(callsign);
	    			ConstruccioArribada[1]=callsign;//Callsign
	    			ConstruccioArribada[2]=palabras[3];//Modelo
	    			ConstruccioArribada[3]=palabras[1];// Aeropuerto de Salida
	    			ConstruccioArribada[4]=palabras[2];// Aeropuerto de Destino(el nuestro)
	    			String[] Hora = palabras[4].split(""); //Hora de sortida hhmmss
	    			ConstruccioArribada[5]=Hora[1]+Hora[2]+Hora[3]+Hora[4];//Hora de sortida otro aeropuerto hhmm (ss eliminats)

	    			lineaSO6 = brSO6.readLine();  //Saltem al segon segment, esta clar que en el primer no estarem a la FIR ni a la APP
	    			palabras = lineaSO6.split(" ");
	    			boolean TempsFIR = false;
	    			boolean TempsAPP=false;
	    			String actcallsign=palabras[9];
	    			int i=0;
	    			while(i<num)
	    			{
	    				actcallsign=actcallsign+"/";
	    				i++;
	    			}
	    			while(palabras[17].equals("1")==false && ConstruccioArribada[1].equals(actcallsign))
	    			{
	    				String[] HoraArribada_= palabras[5].split("");
		    			ConstruccioArribada[8]=HoraArribada_[1]+HoraArribada_[2]+HoraArribada_[3]+HoraArribada_[4];//Possible Hora de arribada hhmm (ss eliminats)
	    				if(TempsFIR==false)
	    				{
	    					TempsFIR = MirarWP (WPFIR,palabras[0]);
	    					if (TempsFIR)
	    						ConstruccioArribada[6]=ConstruccioArribada[8];
	    				}
	    				if(TempsAPP==false)
	    				{
	    					TempsAPP = MirarWP (WPAPP,palabras[0]);
	    					if (TempsAPP)
	    						ConstruccioArribada[7]=ConstruccioArribada[8];
	    				}
	    				lineaSO6 = brSO6.readLine();
	    				if(lineaSO6==null)
	    					break;
	    				palabras = lineaSO6.split(" ");
	    				actcallsign=palabras[9];
		    			i=0;
		    			while(i<num)
		    			{
		    				actcallsign=actcallsign+"/";
		    				i++;
		    			}
	    			}
	    			if(TempsFIR==false)
	    			{
	    				//Proteccio en cas de que no pasem pels nostres punts de la fir, (exemple dir que son sempre 10 min abans del ELDT)
	    				int FIR =Calculotiempo(Integer.parseInt(ConstruccioArribada[8]),10,0);
	    				if(FIR<10)
	    					ConstruccioArribada[6]="000"+FIR;
	    				else if(FIR<100)
	    					ConstruccioArribada[6]="00"+FIR;
	    				else if(FIR<1000)
	    					ConstruccioArribada[6]="0"+FIR;
	    				else
	    					ConstruccioArribada[6]=""+FIR;
	    			}
	    			if(TempsFIR==false && TempsAPP==true)
	    			{
	    				//Proteccio en cas de que no pasem pels nostres punts de la fir, (exemple dir que son sempre 10 min abans del ELDT)
	    				int FIR =Calculotiempo(Integer.parseInt(ConstruccioArribada[7]),5,0);
	    				if(FIR<10)
	    					ConstruccioArribada[6]="000"+FIR;
	    				else if(FIR<100)
	    					ConstruccioArribada[6]="00"+FIR;
	    				else if(FIR<1000)
	    					ConstruccioArribada[6]="0"+FIR;
	    				else
	    					ConstruccioArribada[6]=""+FIR;
	    			}
	    			if(TempsAPP==false)
	    			{
	    				//Proteccio en cas de que no pasem pels nostres punts de la APP, (exemple dir que son sempre 5 min abans del ELDT)
	    				int APP =Calculotiempo(Integer.parseInt(ConstruccioArribada[8]),0015,0);

	    				if(Integer.parseInt(ConstruccioArribada[6])>APP)
	    				{
	    					APP=Integer.parseInt(ConstruccioArribada[6]);
	    				}
	    				
	    				if(APP<10)
	    					ConstruccioArribada[7]="000"+APP;
	    				else if(APP<100)
	    					ConstruccioArribada[7]="00"+APP;
	    				else if(APP<1000)
	    					ConstruccioArribada[7]="0"+APP;
	    				else
	    					ConstruccioArribada[7]=""+APP;
	    				
	    			}
	    			
	    			//Canvi de dia entre sortida i arribada(per ara sumare 24h)
	    			if(Integer.parseInt(ConstruccioArribada[5])>Integer.parseInt(ConstruccioArribada[6]))
	    			{
	    				int value=Integer.parseInt(ConstruccioArribada[6]);
	    				value=value+2400;
	    				ConstruccioArribada[6]=value+"";
	    			}
	    			if(Integer.parseInt(ConstruccioArribada[5])>Integer.parseInt(ConstruccioArribada[7]))
	    			{
	    				int value=Integer.parseInt(ConstruccioArribada[7]);
	    				value=value+2400;
	    				ConstruccioArribada[7]=value+"";
	    			}
	    			if(Integer.parseInt(ConstruccioArribada[5])>Integer.parseInt(ConstruccioArribada[8]))
	    			{
	    				int value=Integer.parseInt(ConstruccioArribada[8]);
	    				value=value+2400;
	    				ConstruccioArribada[8]=value+"";
	    			}
	    			Llegadas.add(ConstruccioArribada[0]+"\t"+ConstruccioArribada[1]+"\t"+ConstruccioArribada[2]+"\t"+ConstruccioArribada[3]+"\t"+ConstruccioArribada[4]+"\t"+ConstruccioArribada[5]+"\t"+ConstruccioArribada[6]+"\t"+ConstruccioArribada[7]+"\t"+ConstruccioArribada[8]);  //Arribada afegida a la llista
	    			Llegadasreduit.add(Llegadasreduit.size()+" "+GetAO(ConstruccioArribada[1])+" "+ConstruccioArribada[2]+" "+ConstruccioArribada[8]);
	    		}
	    		else
	    		{
	    			lineaSO6 = brSO6.readLine();
    				if(lineaSO6==null)
    					break;
	    			palabras = lineaSO6.split(" ");
	    		}
	    	}
	    	
	    	////  BUSCAR RELACIONS ARRIBADES/SORTIDES
	    	
	    	//Proposta, mirar que la companyia i el model d'avio siguin el mateix, i que com a minim el avio porti 30min aterrat al aeroport
	    	//Si tot aixó es compleix considerarem que es el mateix avio, i posarem com a nom de ID el que tenia el avio quan arribava al aeroport
	    	//ID=Callsign
	    	
	    	RelacionarAvions(Salidasreduit,Llegadasreduit);
	    	
	    	
	    	FileWriter ficheroInput = new FileWriter(rutaInput);
	    	PrintWriter pwInput = new PrintWriter(ficheroInput);
	    	int i=0;
	    	while(i<Llegadas.size())
	    	{
	    		pwInput.println ((String) Llegadas.get(i));
	    		i++;
	    	}
	    	int p=0;
	    	while(p<Salidas.size())
	    	{
	    		pwInput.println ((String) Salidas.get(p));
	    		p++;
	    	}
	    	ficheroInput.close();
	    	leido = 1;
	    	if(p+i==0)
	    	{
	    		System.out.println("No encontramos ningun avion en el documento SO6.txt que realize ninguna acción en el aeropuerto dado.");
	    		leido=-1;
	    	}
	    }
	    catch(Exception FileNotFoundException)
	    {
	    	System.out.println("No encuentro el archivo de las aerolineas");
	    	leido=0;
	    }
	}
	public boolean	MirarWP(String[] WaypointsZona, String SegmentWP)
	//In(String[] WayPointsZona, String SegmentWP) Out (boolean encontrado)
	//Mira si el WayPoint de inicio de un segmento de vuelo corresponde con
	//alguno de los de la lista de entrada (WPFIR o WPAPP)
	{
		boolean encontrado=false;
		String[] Waypoints = SegmentWP.split("_");
		int i=0;
		while(i<WaypointsZona.length && encontrado==false)
		{
			if(WaypointsZona[i].equals(Waypoints[0]))
				encontrado=true;
			i++;

		}
		return encontrado;
	}
	
	public void MandarInfo()
	//In(boolean leido) Out()
	//Completa el avión que se mandara al SimulatorInput para informarle de cómo a ido la lectura del documento
	{
		Aircraft Avion = new Aircraft();
		Avion.setOpcionMensaje(-1);
		if(leido==1)
			Avion.setIncidencias("DDR2 leido");
		else if(leido==0)
			Avion.setIncidencias("Error");
		else if(leido==-1)
			Avion.setIncidencias("Sin aviones");
		try 
		{
			BroadcastInfo(Avion);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void BroadcastInfo(Aircraft avion) throws IOException 
	//In(Aircraft avion) Out ()
	//mandara un mensaje al SimulatorInput para informarle de cómo 
	//a ido la lectura del documento y si ya se ha escrito el documento input.txt
	{
		avion.setOpcionMensaje(-1);
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		// Añadimos el receptor
		AID receptor = new AID("SimulatorInput", AID.ISLOCALNAME);
		
		msg.addReceiver(receptor);
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
	}
	public int Calculotiempo(int tiempo, int delay, int operacion)
	//In (int tiempo, int delay, int operacion) Out ( int result)
	//Suma o resta los dos valores de entrada. Tiene en cuenta posibles cambios de hora
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
	public void RelacionarAvions(ArrayList Out, ArrayList In)
	//In (ArrayList Out, ArrayList In, ArrayList Salidas, ArrayList Llegadas) Out(ArrayList Salidas)
	//Encuentra los casos en los que es posible que el avión que realiza una salida sea el mismo que 
	//ha llegado al aeropuerto anteriormente.
	{
		Out=OrdenarLlista(Out);
		int i=0;
		while(i<Out.size())
		{
			String fraseout=(String)Out.get(i);
			String[] paraulesout = fraseout.split(" ");
			int index=Integer.parseInt(paraulesout[0]);
						
			int k=0;
			int diftemps=0;
			int posicioreal=-1;
			int posicioin=-1;
			while(k<In.size())
			{
				String frasein=(String)In.get(k);
				String[] paraulesin = frasein.split(" ");
				if(paraulesin[1].equals(paraulesout[1]) && paraulesin[2].equals(paraulesout[2]))
				{
					int restatemps= Calculotiempo(Integer.parseInt(paraulesout[3]),Integer.parseInt(paraulesin[3]),0);
					if(restatemps>=35 && restatemps>diftemps)
					{
						diftemps=restatemps;
						posicioreal=Integer.parseInt(paraulesin[0]);
						posicioin=k;
					}
				}
				k++;
			}
			String Callsign2;
			if(posicioreal!=-1)
			{
				In.remove(posicioin);
				String frasellegada=(String)Llegadas.get(posicioreal);
				String[] paraulesllegada = frasellegada.split("\t");
				Callsign2=paraulesllegada[1];
			}
			else
				Callsign2="-";
			String frasesalida=(String)Salidas.get(index);
			String[] palabrassalida = frasesalida.split("\t");
					
			Salidas.remove(index);
			
			if(Callsign2.equals("-"))
				Salidas.add(index,palabrassalida[0]+ "\t"  + palabrassalida[1]+ "\t" + palabrassalida[2]+ "\t" + palabrassalida[3]+ "\t" + palabrassalida[4]+ "\t" + palabrassalida[5] + "\t" + Callsign2);
			else
				Salidas.add(index,palabrassalida[0]+ "\t" + Callsign2 + "\t" + palabrassalida[2]+ "\t" + palabrassalida[3]+ "\t" + palabrassalida[4]+ "\t" + palabrassalida[5] + "\t" + palabrassalida[1]);
				
			
			
			i++;
		}
	}

	public ArrayList OrdenarLlista(ArrayList Llista)
	//In (ArrayList Llista) Out (ArrayList Out)
	//Ordena temporalmente todos los elementos de la lista.
	{
		ArrayList Out=new ArrayList();
   		while(Llista.size()!=0)
   		{
   			String frase=(String)Llista.get(0);
			String[] paraules = frase.split(" ");
			int minim=Integer.parseInt(paraules[3]);
   			
   			int valor=0;
   			int k=1;
   			while(k<Llista.size())
   			{
   				frase=(String)Llista.get(k);
   				paraules = frase.split(" ");
   				if(Integer.parseInt(paraules[3])<minim)
   				{
   					minim=Integer.parseInt(paraules[3]);
   					valor=k;
   				}
   				k++;
   			}
   			Out.add((String) Llista.get(valor));
   			Llista.remove(valor);
   		}
		return Out;
	}
	public String GetAO (String ID)
	//In(String ID) Out (String AO)
	//A partir del ID de un vuelo encuentra las siglas de la aerolinea(3 primeras letras del ID
	{
		char lletra1 = ID.charAt(0);
		char lletra2 = ID.charAt(1);
		char lletra3 = ID.charAt(2);
		String AO=Character.toString(lletra1)+Character.toString(lletra2)+Character.toString(lletra3);
		return AO;
	}
}
