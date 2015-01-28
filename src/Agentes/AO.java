package Agentes;

import java.io.*;

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
import Agentes.Airport.RecepcionAvion;

public class AO extends CDM {
	

	String rutaVuelosAerolinea = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.4/VuelosAerolinea.txt";
	
	boolean publicarinfo;
	
	int avionesSimulacion;
	
	String[] contenido;
	ArrayList Retrasoavion = new ArrayList();
	ArrayList Retrasotiempo = new ArrayList();
	
	String[][] avionesCompañia;

	int EOBTtoASBT = 27; // Tiempo entre el incio del embarque y el EOBT
	int TSATtoARDT = 2; // Tiempo entre Aircraft Ready y TSAT
	int ARDTtoASRT = 1; // Tiempo entre ARDT y ASRT
	int ASBTtoARDT = 20; //Tiempo entre ASBT y ARDT
	int ASRTtoTSAT = 1; //Tiempo entre ASRT y TSAT
	
	String[] aerolineas;
	
	boolean variacionesnormales = false;
	int VariacioNormalACGT = 0;
	int VariacioNormalASRT = 0;
	int VariacioNormalTSAT = 0;
	
	public void setup() {

		System.out.println(getAID().getLocalName() + " operativo.");

		publicarinfo = false;
		
		addBehaviour(new RecepcionAvion());

	}

	class RecepcionAvion extends Behaviour {

		private boolean fin = false;

		int Muestralo = 0;

		public void action() {

			ACLMessage mensaje = blockingReceive();
			
			String[] actuMilestone = {" ", " ", " ", " ", " "}; // Actualizaciones de milestone, vector con todas las que se calculan en este agente   ASBT,ARDT,ASRT,TSAT, ETOT
			boolean milestoneActualizado = false;
			
			if (mensaje != null) {
				
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
				
				if(avion.getOpcionMensaje() == 3){
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					aerolineas = listaaerolineas(parametros);
					rutaVuelosAerolinea = valorparametro(parametros[3])  + "VuelosAerolinea.txt";
					
										
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
	
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
										AplicarRetras((UnRetras[k]).split(" "),avion.getIncidencias());
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
				else
				{
					int[] posicionn = tabla.PosicionTabla(avion);
					int posicion=posicionn[0];
				
					if (avion.getOpcionMensaje() == 1){ // Ya esta preparado el archivo de AvionesCompañia a partir del Input
					
						cargarAvionesCompañia();
						publicarinfo = false;
						
					
					}
					if(avion.getNextMilestone() && avion.getOpcionMensaje() == 0)
					{
						int[] actualizadas = {2,8};
						if(avion.getMilestone() == 11) //Caso inicio Milestone 11 (Inicio Embarque: ASBT)
						{
							System.out.println(getLocalName()+" informa: Inicio embarque para el avion "+avion.getId()+".");
							System.out.println("....Inicio del Milestone 11 para el avion "+avion.getId()+" ....");
							System.out.println("");
							

							int DelayARDT=AplicarNormal(VariacioNormalACGT,variacionesnormales);
							if(DelayARDT!=0)
							{
								tabla.getAvion(posicion).setValorTiemposSecundarios(2, Calculotiempo(tabla.getAvion(posicion).getValorTiemposSecundarios(2),DelayARDT,1));
								actualizadas = new int[] {8, 2,16};
								tabla.getAvion(posicion).setOpcionMensaje(2);
							}
							
							tabla.getAvion(posicion).newStatus(7);
							tabla.getAvion(posicion).setMilestone(11);
						
							publicarinfo = true;
						}
						if(avion.getMilestone() == 12) //Caso inicio Milestone 12 (Aircraft Ready: ARDT)
						{
							System.out.println(getLocalName()+" informa: Aircraft "+avion.getId()+" ready. ");
							System.out.println("....Inicio del Milestone 12 para el avion "+avion.getId()+" ....");
							System.out.println("");
							
							int DelayASRT=AplicarNormal(VariacioNormalASRT,variacionesnormales);
							if(DelayASRT!=0)
							{
								tabla.getAvion(posicion).setValorTiemposSecundarios(3, Calculotiempo(tabla.getAvion(posicion).getValorTiemposSecundarios(3),DelayASRT,1));
								actualizadas = new int[] {8, 2,17};
								tabla.getAvion(posicion).setOpcionMensaje(2);
							}
							
							tabla.getAvion(posicion).newStatus(8);
							tabla.getAvion(posicion).setMilestone(12);
						
							publicarinfo = true;
						}
						if(avion.getMilestone() == 13) //Caso inicio Milestone 13 (Start Up Approval Request: ARDT)
						{
							System.out.println(getLocalName()+" informa: Avion "+avion.getId()+": StartUp Request. ");
							System.out.println("....Inicio del Milestone 13 para el avion "+avion.getId()+" ....");
							System.out.println("");
							
							int DelayTSAT=AplicarNormal(VariacioNormalTSAT,variacionesnormales);
							if(DelayTSAT!=0)
							{
								tabla.getAvion(posicion).setValorTiemposSecundarios(2, Calculotiempo(tabla.getAvion(posicion).getValorTiemposSecundarios(2),DelayTSAT,1));
								actualizadas = new int[] {8, 2,18};
								tabla.getAvion(posicion).setOpcionMensaje(2);
							}
							
							tabla.getAvion(posicion).newStatus(8);
							tabla.getAvion(posicion).setMilestone(13);
							
							publicarinfo = true;
						}
					
						// Despues de iniciar el milestone se hace un broadcast con el cambio de milestone y de STATUS
						
						tabla.getAvion(posicion).setActualizadas(actualizadas);
					
					}
					else
					{			

						int[] actuAO = {0,0,0,0,0,0};	//ETOT, ASBT, ARDT ,ASRT, TSAT, incidencia
					
						// Comprobamos si es necesario cargar el tiempo de ETOT del avion
						int[] actETOT = { 1 }; // Se buscara el tiempo ETOT en los aviones que acaben de iniciar el CDM (Han actualizado el ID)
						boolean definirETOT = comprobarActualizadas(avion, actETOT);
					
						// Comprobamos si es necesario calcular el ASBT
						int[] actASBT = {7};
						boolean tiempoASBT = comprobarActualizadas(avion, actASBT);
						if(avion.getMilestone()>=11)
							tiempoASBT=false;
					
						// Comprobamos si es necesario calcular el ARDT
						int[] actARDT = {18};
						boolean tiempoARDT = comprobarActualizadas(avion, actARDT);
						if(avion.getMilestone()>=12)
							tiempoARDT=false;
						// Comprobamos si es necesario calcular el ASRT
						int[] actASRT = {16};
						boolean tiempoASRT = comprobarActualizadas(avion, actASRT);
						if(avion.getMilestone()>=13)
							tiempoASRT=false;
						
						// Comprobamos si es necesario calcular el ARDT por actualizada de ASBT
						int[] actARDT2 = {15};
						boolean tiempoARDT2 = comprobarActualizadas(avion, actARDT2);
						if(avion.getMilestone()>=12)
							tiempoARDT2=false;
						
						// Comprobamos si es necesario calcular el TSAT por actualizada de ASRT
						int[] actTSAT = {17};
						boolean tiempoTSAT = comprobarActualizadas(avion, actTSAT);
						if(avion.getMilestone()>=14)
							tiempoTSAT=false;
						
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
					
						// Calculos:
						if (definirETOT) 
						{ // Calcular ETOT
							tabla.setAvion(avion, posicionn);
							boolean solollega=relacionarFP(posicion); // Obtenemos los datos de la aerolinia sobre el avion deseado.

							if(!solollega)
							{
								actuAO[0] = 5; // Actualizamos el vector de variables actualizadas.
						
								publicarinfo = true;
							
								// Mensaje definir Milestone 16:
								String milestone16 = "16 "+avion.getValorTiempos(2);
								actuMilestone[4] = milestone16;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							}
						}
						if(tiempoASBT)
						{
							int ASBT = Calculotiempo(avion.getValorTiempos(4), EOBTtoASBT,0);
						
							if(avion.getValorTiemposSecundarios(1) < ASBT || (avion.getValorTiemposSecundarios(1)==0 && avion.getMilestone()==10))
							{
								if(avion.getValorTiemposSecundarios(1) != 0)
								{
									int h=GetTempsActualMil(avion,avion.getMilestone());
									avion.añadirIncidencia(avion.getValorTiemposSecundarios(1), ASBT, "ASBT", getAID().getLocalName(),h);
									actuAO[5] = 19;
								}
								avion.setValorTiemposSecundarios(1, ASBT);
							
								actuAO[1] = 15;
								publicarinfo= true;
							
								// Mensaje definir Milestone 11:
								String milestone11 = "11 "+avion.getValorTiemposSecundarios(1);
								actuMilestone[0] = milestone11;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							
							}
						}
						if(tiempoARDT)
						{
							int ARDT = Calculotiempo(avion.getValorTiemposSecundarios(4), TSATtoARDT,0);
						
							if(avion.getValorTiemposSecundarios(2) < ARDT || (avion.getValorTiemposSecundarios(2)==0 && avion.getMilestone()==10))
							{
								if(avion.getValorTiemposSecundarios(2) != 0)
								{
									int h=GetTempsActualMil(avion,avion.getMilestone());
									avion.añadirIncidencia(avion.getValorTiemposSecundarios(2), ARDT, "ARDT", getAID().getLocalName(),h);
									actuAO[5] = 19;
								}
								avion.setValorTiemposSecundarios(2, ARDT);
							
								actuAO[2] = 16;
								publicarinfo= true;
							
								// Mensaje definir Milestone 12:
								String milestone12 = "12 "+avion.getValorTiemposSecundarios(2);
								actuMilestone[1] = milestone12;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							

							}
						}
						if(tiempoASRT)
						{
							int ASRT = Calculotiempo(avion.getValorTiemposSecundarios(2), ARDTtoASRT,1);
							
							if(avion.getValorTiemposSecundarios(3) < ASRT || (avion.getValorTiemposSecundarios(3)==0 && avion.getMilestone()==10))
							{
								if(avion.getValorTiemposSecundarios(3) != 0)
								{
									int h=GetTempsActualMil(avion,avion.getMilestone());
									avion.añadirIncidencia(avion.getValorTiemposSecundarios(3), ASRT, "ASRT", getAID().getLocalName(),h);
									actuAO[5] = 19;
								}
								avion.setValorTiemposSecundarios(3, ASRT);
							
								actuAO[3] = 17;
								publicarinfo= true;
							
								// Mensaje definir Milestone 13:
								String milestone13 = "13 "+avion.getValorTiemposSecundarios(3);
								actuMilestone[2] = milestone13;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);
							}
						}
						if(tiempoARDT2)
						{
							int ARDT = Calculotiempo(avion.getValorTiemposSecundarios(1), ASBTtoARDT,1);
						
							if(avion.getValorTiemposSecundarios(2) < ARDT || (avion.getValorTiemposSecundarios(2)==0 && avion.getMilestone()==10))
							{
								if(avion.getValorTiemposSecundarios(2) != 0)
								{
									int h=GetTempsActualMil(avion,avion.getMilestone());
									avion.añadirIncidencia(avion.getValorTiemposSecundarios(2), ARDT, "ARDT", getAID().getLocalName(),h);
									actuAO[5] = 19;
								}
								avion.setValorTiemposSecundarios(2, ARDT);
							
								actuAO[2] = 16;
								publicarinfo= true;
							
								// Mensaje definir Milestone 12:
								String milestone12 = "12 "+avion.getValorTiemposSecundarios(2);
								actuMilestone[1] = milestone12;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);

							}
						}
						if(tiempoTSAT)
						{
							int TSAT = Calculotiempo(avion.getValorTiemposSecundarios(3), ASRTtoTSAT,1);
						
							if(avion.getValorTiemposSecundarios(4) < TSAT || (avion.getValorTiemposSecundarios(4)==0 && avion.getMilestone()==10))
							{
								if(avion.getValorTiemposSecundarios(4) != 0)
								{
									int h=GetTempsActualMil(avion,avion.getMilestone());
									avion.añadirIncidencia(avion.getValorTiemposSecundarios(4), TSAT, "TSAT", getAID().getLocalName(),h);
									actuAO[5] = 19;
								}
								avion.setValorTiemposSecundarios(4, TSAT);
							
								actuAO[4] = 18;
								publicarinfo= true;
							
								// Mensaje definir Milestone 12:
								String milestone14 = "14 "+avion.getValorTiemposSecundarios(4);
								actuMilestone[3] = milestone14;
								milestoneActualizado = true;
								avion.setOpcionMensaje(2);

							}
						}
						
						if(RetrasoDetectado)
						{
							AplicarRetras((RetrasoAvion[0]).split(" "),"0");
						}
						if(avion.getMilestone()!=0)
						{
							tabla.setAvion(avion, posicionn);
							tabla.getAvion(posicion).setActualizadas(actuAO);
						}
					}
				
				
				
					//////////// FASE Publicar Informacion ///////
				
					if ( (publicarinfo) )
					{
						try 
						{
						
							tabla.getAvion(posicion).setNextMilestone(false);
							
							PublicarInformacion(tabla.getAvion(posicion), Muestralo);
						
							if(milestoneActualizado){ // Publicar la informacion de nuevos milestones [Mensajes al MilestoneTrigger]
								mensajeMilestone(tabla.getAvion(posicion), actuMilestone,0);
							}
						
							publicarinfo = false;
						}
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}		
				}
			}
		}

		public boolean done() {

			return fin;
		}

	}

	public void cargarAvionesCompañia(){
		//In() Out(String[][] avionesCompañia)
		//Función que llama a la función que leerá el documento VuelosAerolinea.txt
		
		avionesCompañia = leerArchivoAO(rutaVuelosAerolinea);
		System.out.println(getAID().getLocalName()+": Archivo de vuelos cargado correctamente");
	
	}
	public String[][] leerArchivoAO(String ubicacion){
		//In(String ubicacion) Out(String[][] avionesCompañia)
		//función que lee el documento VuelosAerolinea.txt
		
		//Y generamos el objeto respecto a la ruta del archivo
		File archivo = new File(ubicacion);
		
		FileReader fr = null;
	    BufferedReader br = null;
		
	    String[] datosVuelo;
		
		String[] NombreAgente = getAID().getLocalName().split(""); // Solo se cargara los aviones de su propia Aerolinia;
		
	    String AO = NombreAgente[4]+NombreAgente[5]+NombreAgente[6]; //AO que estudiem
	    
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
	        	
	            contenido = linea;
	            datosVuelo = contenido.split("\t");
	            if(AO.equals("GEN"))
	            {	           
	            	boolean generic = aerolineagenerica (datosVuelo,aerolineas);
	            	if(generic==true)
	            	{
	            		numeroaviones++;
	            	}
	            }
	            
	            else
	            {
	            	if(datosVuelo[1].equals(AO))
	            	{	            	
	            		numeroaviones++;
	            	}
	            }

	            
	        }
	 
	    	
	    }
	    catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    
	    String[][] AvionesAO = new String[numeroaviones][4];
	    
	    try // Creamos la matriz de datos de los aviones de la compañia
	    {	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea; //Obtenemos el contenido del archivo linea por linea
	        
	        int c = 0;	// Contador de aviones en la memoria del agente AO
	        
	        while( ( linea = br.readLine() ) != null )
	        {
	        	
	            contenido = linea;
	            datosVuelo = contenido.split("\t");
	            
	            if(datosVuelo[1].equals(AO)){
	            	
	            	AvionesAO[c] = datosVuelo;
	            	c++;
	            }
	            if(AO.equals("GEN"))
	            {
	            	boolean generic = aerolineagenerica (datosVuelo,aerolineas);
	            	if (generic)
	            	{
	            		AvionesAO[c]=datosVuelo;
	            		c++;
	            	}
	            }
	        }
	 
	    }catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    
	    return AvionesAO;
	}
	public boolean relacionarFP(int posicion) { // Devuelve el vector con los datos
												// del avion de la base de datos
												// de la compañia
		//Out(boolean solollega)  In(int posicion, String[][] avionesCompañia)
		//Mira si el avión que se encuentra en posición es de solo llegada o no.
		
		String IDavion = this.tabla.getAvion(posicion).getId();
		boolean solollega=false;
		// Encontramos el avion del mensaje en la base de datos de la aerolínia para poder relacionar los FP
		
		for (int i = 0; i < avionesCompañia.length; i++) { 

			String IDcompañia = avionesCompañia[i][0];
			
			if (IDcompañia.equals(IDavion)) 
			{
				String ETOT = avionesCompañia[i][4];
				if(Integer.parseInt(ETOT)!=9999)
				{
				// Añadimos el valor del ETOT al avion.
				tabla.getAvion(posicion).setValorTiempos(2,Integer.parseInt(ETOT));
				}
				else
				{
					solollega=true;
				}
				break;
			}
			else
			{
				 // System.out.println("este no");
			}
		}
		return solollega;
	}
	public boolean aerolineagenerica(String[] datos, String[] aerolineas)
	//Out(boolean generic) In(String[] datos, String[] aerolineas)
	//Mira si el avión de datos pertenece a la aerolínea genérica o pertenece a una aerolínea con agente AO propio
	{
		boolean generic=true;
		int i=0;
		while(i< aerolineas.length && generic ==true)
		{
			if (aerolineas[i].equals(datos[1]))
			{
				generic=false;
			}
			i++;
		}
		return generic;
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
	public void AplicarRetras(String[] Informacio,String hora)
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
}
