package Agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

import java.io.*;

import Agentes.Airport.RecepcionAvion;

public class CFMU extends CDM {

	Tabla tabla;
	String rutaTXT = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.4//FlightPlans.txt";
	String rutaTXTS = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.4//FlightPlansSalidas.txt";
	
	
	boolean publicarinfo;
	int avionesSimulacion;
	String[][] FlightPlans;
	String[][] FlightPlansSalidas;
	
	ArrayList Retrasoavion=new ArrayList();
	ArrayList Retrasotiempo=new ArrayList();
	ArrayList ModelosCat = new ArrayList();
	ArrayList NumCat = new ArrayList();
	
	public void setup(){
	
		System.out.println( getAID().getLocalName()+ " operativo.");
		
		
		publicarinfo = false;
		
	    addBehaviour(new RecepcionAvion());
	
	}
	
	class RecepcionAvion extends Behaviour{
        
		private boolean fin = false;
		

		// Podemos canviar la opcion "Muestralo" para que la recepcion muestre por consola si recibe el mensaje o no:
		// Muestralo = 0 --> No se muestra por pantalla ninguna informacion;
		// Muestralo = 1 --> Se muestra por pantalla "Se ha recibido el avion X";
		// Muestralo = 2 --> Se muestra por pantalla el contendio del mensaje (el contenido del avion enviado);
		
        int Muestralo = 0;

		public void action(){
			
			ACLMessage mensaje = blockingReceive();
			String[] actuMilestone = {" ", " ", " "}; // Actualizaciones de milestone, vector con todas las que se calculan en este agente
			
			if (mensaje != null)
			{
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
								
				if(avion.getOpcionMensaje() == 3){
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					rutaTXT = valorparametro(parametros[3])  + "FlightPlans.txt";
					rutaTXTS = valorparametro(parametros[3]) + "FlightPlansSalidas.txt";
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					CargarCat(parametros);
					
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
						

				}
				if(	avion.getOpcionMensaje() == 1) // Ya esta preparado el archivo de Planes de Vuelo (FP) a partir del Input
				{
					cargarFP(); // Creamos la tabla todos los planes de vuelo en la memoria del Agente
					System.out.println("CFMU: Flight Plans y Flight Plans Salidas cargados correctamente");
					publicarinfo = false;
				}
				if(avion.getOpcionMensaje() == 6)
				{
					Aircraft flightCDM = IniciarCDMSalidas(avion.getId(), FlightPlansSalidas); // Sacamos el avion recibido de la tabla de FP y lo creamos como avion
					
					int[] posicionn = tabla.PosicionTabla(avion);
					int posicion=posicionn[0];
					
					tabla.setAvion(flightCDM, posicionn);
					tabla.getAvion(posicion).setNextMilestone(false);

					// El agente informa por consola del incio del CDM
					//System.out.println("CFMU informa: 3 horas para el despegue del avion "+tabla.getAvion(posicion).getId()+" ");
					//System.out.println("....Inicio del Milestone 1 para el avion " +tabla.getAvion(posicion).getId()+" ....");
					System.out.println("CFMU informa: 3 horas para el despegue del avion "+avion.getId()+" ");
					System.out.println("....Inicio del Milestone 1 para el avion " +avion.getId()+" ....");
				
					System.out.println("");
				
					try {
						PublicarInformacion(tabla.getAvion(posicion), Muestralo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(avion.getOpcionMensaje()==4 || avion.getOpcionMensaje()==5 || avion.getOpcionMensaje()==7)
				{
					if(avion.getOpcionMensaje()==5 && Retrasotiempo.isEmpty()==false)
					{
						String RetrasoPosicion = DetectarRetrasoTiempo(avion.getIncidencias(),Retrasotiempo);
						if(RetrasoPosicion.equals("")==false)
						{
							String[] posiciones=RetrasoPosicion.split("");
							int i=1;//Al fer split "" el primer valor estara vacio, los otros tendran los numeros de las posiciones
							while(i<posiciones.length)
							{
								String LlistaRetrasos= BuscarRetrasosTiempos(posiciones[i],i-1);
								String[] UnRetras = LlistaRetrasos.split("\n");
								int k=1;
								while(k<UnRetras.length)
								{
									if(UnRetras[k].equals("")==false)
									{
										AplicarRetras((UnRetras[k]).split(" "),avion.getId());
									}
									k++;
								}
								i++;
							}
						
						}
					}
					if(avion.getOpcionMensaje()==4)
					{
						String Retraso = avion.getIncidencias();
						PonerRetraso(Retraso);
					}
				}
				else if(avion.getOpcionMensaje()!=3 && avion.getOpcionMensaje()!=1)
				{
					int[] posicionn = tabla.PosicionTabla(avion);
					int posicion=posicionn[0];
					
					boolean milestoneActualizado=false;
				
					if(avion.getMilestone() == 1 && avion.getNextMilestone() == true) //Recibe el mensaje de inicio del CDM
					{
						Aircraft flightCDM = IniciarCDM(avion.getId(), FlightPlans); // Sacamos el avion recibido de la tabla de FP y lo creamos como avion
					
						tabla.setAvion(flightCDM, posicionn);
						tabla.getAvion(posicion).setMilestone(1);
						tabla.getAvion(posicion).setNextMilestone(false);

						// El agente informa por consola del incio del CDM
						//System.out.println("CFMU informa: 3 horas para el despegue del avion "+tabla.getAvion(posicion).getId()+" ");
						//System.out.println("....Inicio del Milestone 1 para el avion " +tabla.getAvion(posicion).getId()+" ....");
						System.out.println("CFMU informa: 3 horas para el despegue del avion "+avion.getId()+" ");
						System.out.println("....Inicio del Milestone 1 para el avion " +avion.getId()+" ....");
					
						System.out.println("");
					
						publicarinfo = true;
					}
					if(avion.getMilestone() == 2 && avion.getNextMilestone() == true) //Recibe el mensaje de inicio del Milestone 2
					{
						 // El agente informa por consola del cambio de milestone
						System.out.println("CFMU informa: 2 horas para el despegue del avion "+tabla.getAvion(posicion).getId()+" ");
						System.out.println("....Inicio del Milestone 2 para el avion " +tabla.getAvion(posicion).getId()+" ....");
						System.out.println("");
						
						int[] act = {8}; // Actualizamos el milestone del avion
						tabla.getAvion(posicion).setActualizadas(act);
						tabla.getAvion(posicion).setMilestone(2);
						
						
						publicarinfo = true;
					
					}
					
					if(avion.getMilestone() != 1 && avion.getMilestone() != 2 && avion.getOpcionMensaje()!=1)
					{
						tabla.setAvion(avion, posicionn);
						publicarinfo = false;
					}
					
					if(avion.getNextMilestone()==false)
					{
						int[] actualizadasCFMU = new int[4];
						//Miramos si se tiene de actualizar TFIR
						int[] opcionTFIR = {3}; //Se actua si se ha actualizado el ETOT';
						int calcularTFIR = Actuamos(avion, opcionTFIR);
						if(avion.getMilestone()>=3)
							calcularTFIR=0;
						
						//Miramos si se tiene de actualizar TAPP
						int[] opcionTAPP = {20}; //Se actua si se ha actualizado el TFIR;
						int calcularTAPP = Actuamos(avion, opcionTAPP);
						if(avion.getMilestone()>=4)
							calcularTAPP=0;
						
						//Miramos si se tiene de actualizar ELDT
						int[] opcionELDT = {21}; //Se actua si se ha actualizado el TAPP
						int calcularELDT = Actuamos(avion, opcionELDT);
						if(avion.getMilestone()>=5)
							calcularELDT=0;
						
						//Miramos si tenemos de aplicar algun retraso a un avion en concreto
						boolean RetrasoDetectado=false;
						String[] RetrasoAvion=null;
						if(Retrasoavion.isEmpty()==false)
						{
							RetrasoAvion=AlgunRestraso(avion.getId(),avion.getMilestone(),Retrasoavion);
							if(RetrasoAvion[0].equals("Ninguno")==false)
							{
								Retrasoavion.remove(Integer.parseInt(RetrasoAvion[1]));
								RetrasoDetectado=true;
							}
						}
						
						if(calcularTFIR==1)
						{
							int TFIR=avion.getValorTiemposVuelo(0);
							int TFIRnuevo=sumatemps(avion.getValorTiempos(0),avion.getinfoVuelo()[0]);
							if(TFIRnuevo>TFIR)
							{
								avion.setValorTiemposVuelo(0, TFIRnuevo);
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(TFIR, TFIRnuevo, "TFIR", "CFMU",h);
								actualizadasCFMU[0]=20;
								actualizadasCFMU[3]=19;
								avion.setActualizadas(actualizadasCFMU);
								publicarinfo= true;
								
								// Mensaje definir Milestone 4:
								String milestone4 = "4 "+TFIRnuevo;
								actuMilestone[0] = milestone4;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							}
						}
						
						if(calcularTAPP==1)
						{
							int TAPP=avion.getValorTiemposVuelo(1);
							int TAPPnuevo=sumatemps(avion.getValorTiemposVuelo(0),avion.getinfoVuelo()[1]);
							if(TAPPnuevo>TAPP)
							{
								avion.setValorTiemposVuelo(1, TAPPnuevo);
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(TAPP, TAPPnuevo, "TAPP", "CFMU",h);
								actualizadasCFMU[1]=21;
								actualizadasCFMU[3]=19;
								avion.setActualizadas(actualizadasCFMU);
								publicarinfo= true;
							
								// Mensaje definir Milestone 5:
								String milestone5 = "5 "+TAPPnuevo;
								actuMilestone[1] = milestone5;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							}
						}
						
						if(calcularELDT==1)
						{
							int ELDT=avion.getValorTiempos(1);
							int ELDTnuevo=sumatemps(avion.getValorTiemposVuelo(1),avion.getinfoVuelo()[2]);
							if(ELDTnuevo>ELDT)
							{
								avion.setValorTiempos(1, ELDTnuevo);
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(ELDT, ELDTnuevo, "ELDT", "CFMU",h);
								actualizadasCFMU[2]=4;
								actualizadasCFMU[3]=19;
								avion.setActualizadas(actualizadasCFMU);
								publicarinfo= true;
							
								// Mensaje definir Milestone 6:
								String milestone6 = "6 "+ELDTnuevo;
								actuMilestone[2] = milestone6;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							}
						}
						
						
						
						if(RetrasoDetectado)
						{
							AplicarRetras((RetrasoAvion[0]).split(" "),"0");
							//************************************
							//Poner el retraso que sea i si publicar info...!!
						}
						
					}
							
					//////////// FASE Publicar Informacion ///////
						
					if ( (publicarinfo) )
					{
						try {
						
							PublicarInformacion(tabla.getAvion(posicion), Muestralo);
						
							if(milestoneActualizado){ // Publicar la informacion de nuevos milestones [Mensajes al MilestoneTrigger]
								mensajeMilestone(tabla.getAvion(posicion), actuMilestone,0);
							}
							
							publicarinfo = false;
					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}		
				}
			}
			
		}
		public boolean done()
        {
            return fin;
        }
		
	}
	
	public String[][] leerArchivoCFMUSalidas(String ubicacion)
	//Out (String[][] FP) In (String ubicacion)
	//Leer el documento FlightPlansSalidas.txt y devuelve la información en formato String[ ][ ]
	{

		
		//Y generamos el objeto respecto a la ruta del archivo
		File archivo = new File(ubicacion);
		String[] datosVuelo;
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    
	    //Cadena de texto donde se guardara el contenido del archivo
	    String contenido = "";
	    
	    int numeroaviones = 0;
	    
	    try // Contamos el numero de aiones de la compañia en el archivo
	    {
	    	fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea; //Obtenemos el contenido del archivo linea por linea
        
	        while( ( linea = br.readLine() ) != null )
	        {
	        	numeroaviones++;
	        }
	    }
	    catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    
	    String[][] FP = new String[numeroaviones][6];
	    try // Guardamos los Planes de vuelo en la base de datos
	    {	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea;
	        //Obtenemos el contenido del archivo linea por linea
	        int i = 0;
	        while( ( linea = br.readLine() ) != null )
	        {
	            contenido = linea;
	            datosVuelo = contenido.split("\t");
	            
	            FP[i] = datosVuelo;
	            i=i+1;
	            
	        }
	 
	    }catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    return FP;
	}
	public String[][] leerArchivoCFMU(String ubicacion)
	//Out (String[][] FP) In (String ubicacion)
	//Leer el documento FlightPlans.txt y devuelve la información en formato String[ ][ ]
	{

		
		//Y generamos el objeto respecto a la ruta del archivo
		File archivo = new File(ubicacion);
		String[] datosVuelo;
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    
	    //Cadena de texto donde se guardara el contenido del archivo
	    String contenido = "";
	    
	    int numeroaviones = 0;
	    
	    try // Contamos el numero de aiones de la compañia en el archivo
	    {
	    	fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea; //Obtenemos el contenido del archivo linea por linea
	        
	        while( ( linea = br.readLine() ) != null )
	        {
	        	numeroaviones++;
	        }
	    }
	    catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    
	    String[][] FP = new String[numeroaviones][9];
	    
	    try // Guardamos los Planes de vuelo en la base de datos
	    {	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea;
	        //Obtenemos el contenido del archivo linea por linea
	        int i = 0;
	        while( ( linea = br.readLine() ) != null )
	        {
	            contenido = linea;
	            datosVuelo = contenido.split("\t");
	            
	            FP[i] = datosVuelo;
	            i=i+1;
	            
	        }
	 
	    }catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    return FP;
	}
	public void cargarFP()
    //Out(String[][] FlightPlans, String[][]FlightPlansSalidas) In(String rutaTXT, String rutaTXTS)
	//Creamos la tabla con todos los aviones de los planes de vuelo en la memoria del agente
	{
		
		
		FlightPlans = leerArchivoCFMU(rutaTXT);
		FlightPlansSalidas = leerArchivoCFMUSalidas(rutaTXTS);
		// Formato FP:
		// ID AO MODELO ADEP ADES ATOT' ALDT
		
		
	}
	public Aircraft IniciarCDMSalidas(String id, String[][] FP)
	//Out(Aircraft avionCDM) In(String id, String[][] FP)
	//Busca en FP un avión con una id concreta y crea el objeto Aircraft
	{

		Aircraft avionCDM = new Aircraft();
		int i = 0;
		
		// Definimos las variables que extraeremos de los datos para definir el avion
		String ID = null;
		int ETOT = 0;
		int[] infoVuelo = new int[3];
		String Aerolinea = null;
		String ADEP = null;
		String ADES = null;
		String Modelo = null;
		
		while  ( i < FP.length){
			if(id.equals(FP[i][0])){ // Buscamos el avion en los datos de FP
				
				ID = FP[i][0];
				Aerolinea = FP[i][1];
				Modelo = FP[i][2];
				ADEP = FP[i][3];
				ADES = FP[i][4];
				ETOT  = Integer.parseInt(FP[i][5]);
				break;
			}
			else{
				i++;
			}
		}
		// Rellenamos el avion
		int[] tiempos = {0, 0, ETOT, 0, 0};
		int[] tiemposSecundarios = {0, 0, 0, 0, 0};
		int[] tiemposVuelo = {0,0};
		String incidencias = "";
		
		avionCDM.setId(ID);
		avionCDM.setModelo(Modelo);
		avionCDM.setCat(CategoriaAvion(avionCDM));
		avionCDM.setADEP(ADEP);
		avionCDM.setADES(ADES);
		avionCDM.setAO(Aerolinea);
		avionCDM.newStatus(0);
		avionCDM.setTiempos(tiempos);
		avionCDM.setTiemposSecundarios(tiemposSecundarios);
		avionCDM.setIncidencias(incidencias);
		avionCDM.setTiemposVuelo(tiemposVuelo);
		avionCDM.setinfoVuelo(infoVuelo);
		avionCDM.setMilestone(10);
		
		int[] act = {99, 5}; // Codigo para que el IS sepa que se esta creando por primera vez el avion
		avionCDM.setActualizadas(act);
		
		int categoria = CategoriaAvion(avionCDM);
		avionCDM.setCat(categoria);
		
		
		return avionCDM;
	}

	public Aircraft IniciarCDM(String id, String[][] FP)
	//Out(Aircraft avionCDM) In(String id, String[][] FP)
	//Busca en FP un avión con una id concreta y crea el objeto Aircraft
	{

		Aircraft avionCDM = new Aircraft();
		int i = 0;
		
		// Definimos las variables que extraeremos de los datos para definir el avion
		String ID = null;
		int ETOTprima = 0;
		int ELDT = 0;
		int TFIR = 0;
		int TAPP = 0;
		int[] infoVuelo = new int[3];
		String Aerolinea = null;
		String ADEP = null;
		String ADES = null;
		String Modelo = null;
		
		while  ( i < FP.length){
			if(id.equals(FP[i][0])){ // Buscamos el avion en los datos de FP
				
				ID = FP[i][0];
				Aerolinea = FP[i][1];
				Modelo = FP[i][2];
				ADEP = FP[i][3];
				ADES = FP[i][4];
				ETOTprima  = Integer.parseInt(FP[i][5]);
				ELDT = Integer.parseInt(FP[i][6]);
				TFIR = Integer.parseInt(FP[i][7]);
				TAPP = Integer.parseInt(FP[i][8]);
				infoVuelo[0]=restatemps(TFIR,ETOTprima);
				infoVuelo[1]=restatemps(TAPP,TFIR);
				infoVuelo[2]=restatemps(ELDT,TAPP);
				break;
			}
			else{
				i++;
			}
		}
		// Rellenamos el avion
		int[] tiempos = {ETOTprima, ELDT, 0, 0, 0};
		int[] tiemposSecundarios = {0, 0, 0, 0, 0};
		int[] tiemposVuelo = {TFIR,TAPP};
		String incidencias = "";
		
		avionCDM.setId(ID);
		avionCDM.setModelo(Modelo);
		avionCDM.setCat(CategoriaAvion(avionCDM));
		avionCDM.setADEP(ADEP);
		avionCDM.setADES(ADES);
		avionCDM.setAO(Aerolinea);
		avionCDM.newStatus(0);
		avionCDM.setTiempos(tiempos);
		avionCDM.setTiemposSecundarios(tiemposSecundarios);
		avionCDM.setIncidencias(incidencias);
		avionCDM.setTiemposVuelo(tiemposVuelo);
		avionCDM.setinfoVuelo(infoVuelo);
		
		int[] act = { 99 }; // Codigo para que el IS sepa que se esta creando por primera vez el avion
		avionCDM.setActualizadas(act);
		
		int categoria = CategoriaAvion(avionCDM);
		avionCDM.setCat(categoria);
		
		return avionCDM;
	}

	public void PonerRetraso(String Retraso)
	//Out(String[]Retrasotiempo String[]Retrasoavion) In(String Retraso)
	//Le llega un retraso y lo guarda en la lista Retrasotiempo(tipo 1) o Retrasoavion (tipo 2 y 3)
	{
		String[] palabras=Retraso.split(" ");
		String comentario="";
		if(Integer.parseInt(palabras[0])==1)
		{
			int i=6;
			while(i<palabras.length)
			{
				comentario=comentario + " " +palabras[i];
				i++;
			}
			String TiempoVar = TempsQueRetrasem(Integer.parseInt(palabras[5]));
			Retrasotiempo.add(palabras[1]+" "+palabras[2]+ " "+palabras[3]+ " "+palabras[4]+ " "+TiempoVar+comentario);
		}
		if(Integer.parseInt(palabras[0])==2)
		{
			int i=5;
			while(i<palabras.length)
			{
				comentario=comentario + " " +palabras[i];
				i++;
			}
			String TiempoVar = TempsQueRetrasem(Integer.parseInt(palabras[4]));
			Retrasoavion.add(palabras[1]+" "+palabras[2]+" "+palabras[3]+" "+TiempoVar+comentario);
		}
		if(Integer.parseInt(palabras[0])==3)
		{
			int i=6;
			while(i<palabras.length)
			{
				comentario=comentario + " " +palabras[i];
				i++;
			}
			Retrasoavion.add(palabras[1]+" "+palabras[2]+" "+palabras[3]+" "+palabras[4]+comentario);
		}
	}
	public String BuscarRetrasosTiempos(String posicio, int encontrados)
	//Out(String LlistaRetrasos, ArrayList Retrasotiempo) In(String posicio, int encontrados, ArrayList Retrasotiempo)
	//Encuentra todos los aviones afectados por un retraso de tipo 1, y escribe los retrasos como tipo 3
	{
		String LlistaRetrasos="";
		int i=0;
		String[] RetrasoAplicado=((String) Retrasotiempo.get(Integer.parseInt(posicio)-encontrados)).split(" ");
		Retrasotiempo.remove(Integer.parseInt(posicio)-encontrados);
		int TempsPrevi=GetTempsPreviMil(Integer.parseInt(RetrasoAplicado[5]));
		int TempsPost=GetTempsPostMil(Integer.parseInt(RetrasoAplicado[5]));
		while(i<tabla.getnumero())
		{
			Aircraft avion=tabla.getAvion(i);
			int ValorPrev=0;
			int ValorPost=0;
			if(TempsPrevi>=1 && TempsPrevi<=5)
				ValorPrev=avion.getValorTiempos(TempsPrevi-1);
			if(TempsPrevi>=6 && TempsPrevi<=10)
				ValorPrev=avion.getValorTiemposSecundarios(TempsPrevi-6);
			if(TempsPrevi>=11 && TempsPrevi<=12)
			ValorPrev=avion.getValorTiemposVuelo(TempsPrevi-11);
			if(TempsPost>=1 && TempsPost<=5)
				ValorPost=avion.getValorTiempos(TempsPost-1);
			if(TempsPost>=6 && TempsPost<=10)
				ValorPost=avion.getValorTiemposSecundarios(TempsPost-6);
			if(TempsPost>=11 && TempsPost<=12)
				ValorPost=avion.getValorTiemposVuelo(TempsPost-11);
			
			int PasA=restatemps(ValorPrev,Integer.parseInt(RetrasoAplicado[0]));
			int PasB=restatemps(ValorPost,Integer.parseInt(RetrasoAplicado[0]));
			int PasC=restatemps(ValorPrev,Integer.parseInt(RetrasoAplicado[1]));
			int PasD=restatemps(ValorPost,Integer.parseInt(RetrasoAplicado[1]));
			
			boolean ModeliAO= MirarmodeliAO(avion, RetrasoAplicado[6],RetrasoAplicado[7]);
			if ((PasA * PasB <= 0 || PasC * PasD <= 0) && ModeliAO)// Significa que el nostre temps esta aqui dintre
			{
				int k=6;
				String Comment="";
				while(k<RetrasoAplicado.length)
				{
					Comment=Comment + " "+RetrasoAplicado[k];
					k=k+1;
				}
				LlistaRetrasos=LlistaRetrasos+"\n"+avion.getId()+" "+RetrasoAplicado[3] + " " + RetrasoAplicado[4]+Comment;
			}
			i++;
			
		}
		
		
		return LlistaRetrasos;
	}
	public void AplicarRetras(String[] Informacio, String hora)
	//Out() In (String[] Informacio, String hora)
	//Aplica un retraso de tipo 3 a cualquier avión
	{
		Aircraft avion=new Aircraft();
		avion.setId(Informacio[0]);
		int[] posicionn = tabla.PosicionTabla(avion);
		int posicion=posicionn[0];
		avion=tabla.getAvion(posicion);
		avion.setOpcionMensaje(2);
		
		int TempsActual = TreureTemps(avion,Informacio[2]);
		if (TempsActual!=0)
		{
			int[] Actualizada = TreureActualizada(Informacio[2]);
			int TempsNou= sumatemps(TempsActual,Integer.parseInt(Informacio[1]));
			//Posem indicació de quin temps s'actualiza
			avion.setActualizadas(Actualizada);
			
			//Actualitzem el temps
			if(Actualizada[0]>=4 && Actualizada[0]<=7)
				avion.setValorTiempos(Actualizada[0]-3, TempsNou);
			if(Actualizada[0]>=14 && Actualizada[0]<=18)
				avion.setValorTiemposSecundarios(Actualizada[0]-14, TempsNou);
			if(Actualizada[0]>=20 && Actualizada[0]<=21)
				avion.setValorTiemposVuelo(Actualizada[0]-20, TempsNou);
			
			
			//Posem la Incidencia
			int k=3;
			String Comment="";
			while(k<Informacio.length)
			{
				Comment=Comment + " "+Informacio[k];
				k=k+1;
			}
			if(Comment.equals("")==false)
			{
				if(hora.equals("0"))
					avion.añadirIncidencia(TempsActual,TempsNou, Informacio[2], "Airport (" +Comment +")",TempsActual);
				else
					avion.añadirIncidencia(TempsActual,TempsNou, Informacio[2], "Airport (" +Comment +")",Integer.parseInt(hora));
			}
			else
			{
				if(hora.equals("0"))
					avion.añadirIncidencia(TempsActual, TempsNou, Informacio[2], "Airport",TempsActual);
				else
					avion.añadirIncidencia(TempsActual, TempsNou, Informacio[2], "Airport",Integer.parseInt(hora));
			}
		
			try {
				if((Integer.parseInt(hora)>TempsActual))
					PublicarInformacion(tabla.getAvion(posicion),0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
	}
	public void CargarCat(String[] parametros)
	//Out(ArrayList ModelosCat, ArrayList NumCat) In(String[] parametros)
	//Escribe la lista ModelosCat i NumCat a partir de los datos de entrada (leídos del documento airport.txt)
	{		
		String[] a = parametros[9].split(" ");
		String[] cat;
		int i = 0;
		while(i<a.length)
		{
			cat = a[i].split("-");
			ModelosCat.add(cat[0]);
			NumCat.add(cat[1]);
			
			i++;
		}
	}
	public int CategoriaAvion(Aircraft avion)
	//Out(int categoria) In(Aircraft avion, ArrayList ModelosCat, ArrayList NumCat)
	//Mira el modelo del avión, y busca su categoría en las listas ModelosCat y NumCat, 
	//si no encuentra su categoría le assigna un valor de 4.
	{
		String modelo = avion.getModelo();
		
		int posicion = ModelosCat.indexOf(modelo);
		
		int categoria;
		
		if(posicion != -1)
		{
			categoria = Integer.parseInt((String)NumCat.get(posicion));
			return categoria;
		}
		else
		{
			int catGen = 4;
			return catGen; // Consideremos de categoria 4 a los aviones que no estan en la lista de categorias
		}
	}
	
}
