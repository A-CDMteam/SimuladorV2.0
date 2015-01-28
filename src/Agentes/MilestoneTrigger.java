package Agentes;

import jade.core.Agent;

import java.io.*;
import java.util.*;
import java.util.Timer;


import testerComunicaciones.OntologiaCDMA;
import Agentes.Aircraft;

import com.google.gson.Gson;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.content.ContentElement;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.util.leap.ArrayList;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPANames;
import jade.domain.DFService;
import jade.domain.FIPAException;
 
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.Ontology;

public class MilestoneTrigger extends CDM 
{	
	boolean simulacionfinalizada=false;
	
	// Receptores de los mensajes trigger
	private AID CFMU = new AID ("CFMU", AID.ISLOCALNAME);
	private AID ATC = new AID ("ATC", AID.ISLOCALNAME);
	private AID Airport = new AID ("Airport", AID.ISLOCALNAME);

	
	int avionesSimulacion;
	boolean encontrado=false;
	private AID Output = new AID("Output", AID.ISLOCALNAME);
	private AID InfoSharing = new AID("InfoSharing", AID.ISLOCALNAME);

	
	int GHamount=0;
	String AOforGH[]={""};
	
	
	// Diferentes Aerolineas que entraran del Aeropuerto de Barcelona (Dependera del Input)
	private AID AO = new AID("AO-GEN", AID.ISLOCALNAME); // Agente predeterminado para los aviones sin aerolinea
	
	String ruta = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.5//TriggerCDM.txt"; //Ruta donde buscara el archivo con las ordenes
	String rutaMilestones = ""; //C:/Users/xavi/workspace/SimuladorV1.5//OldMilestones.txt";  //Ruta donde escribiremos las ordenes que manda el MT por orden
	
	String[] aerolineas;
	
	int velocidadSimulacion;
	int Muestralo = 0;
	int muestratodo = 0;
	boolean simula;
	boolean finsimulacion=false;
	
	boolean SoloPrimerMilestone = false; //Para hacer pruebas, solo lanza el primer milestone del primer avion del INPUT
	
	ACLMessage MensajeInicio = null;

    ArrayList Milestoneslist = new ArrayList();
    ArrayList OldMilestonesList = new ArrayList();
	int contador = 0;
	
	public void setup()
	{
		
		System.out.println( getAID().getLocalName()+ " operativo.");
		simula = false;
			
		ParallelBehaviour p = new ParallelBehaviour (ParallelBehaviour.WHEN_ALL);
		p.addSubBehaviour(new OrdenarMilestones());
		p.addSubBehaviour(new IniciarSimulacion());


		addBehaviour (p);
	}
	
	class OrdenarMilestones extends Behaviour 
	{
		private boolean fin = false;
		public void action() 
		{				
			ACLMessage mensaje = blockingReceive();
			
			if (mensaje != null || simulacionfinalizada) 
			{
				Aircraft avion = RecepcionMensaje(mensaje, 0);
				
				if(avion.getOpcionMensaje() == 3){
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					velocidadSimulacion = Integer.parseInt(valorparametro(parametros[2]));
					aerolineas = listaaerolineas(parametros);
					ruta = valorparametro(parametros[3])  + "TriggerCDM.txt";
					rutaMilestones = valorparametro(parametros[3])  + "MilestonesResultado.txt";
					
					if(avion.getAO().equals("")==false)
					{
						GHamount = avion.getAO().split("\t").length;	
						AOforGH=avion.getAO().split("\t");
					}
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion);
				}
				else if(avion.getOpcionMensaje() == 1)
				{
					SetMensaje(mensaje);
					simula = true;
					
				}
				else if(avion.getOpcionMensaje() == 10)
				{
					if(avion.getMilestone()==17)
					{
						finsimulacion=true;
					}
				}
				else if(avion.getOpcionMensaje() == 2)
				{
					String[] variaciones=avion.getActuMilestone();
					int p=0;
					int cont=0;
					while(p<variaciones.length)
					{
						if(!variaciones[p].equals(" "))
						{
							cont++;
						}
						p++;
					}
					String[] variacion = new String[cont];
					p=0;
					cont=0;
					while(p<variaciones.length)
					{
						if(!variaciones[p].equals(" "))
						{
							variacion[cont] = avion.getId() + " " +variaciones[p];
							if(Integer.parseInt((variaciones[p].split(" "))[0])==0)
							{
								variacion[cont] = "Retraso " +variaciones[p];
							}
							cont++;
						}
						p++;
					}
											
					
					ArrayList Milestones = getMilestoneslist();
					ArrayList TodasVariaciones=variaciones(variacion);
					
					while(TodasVariaciones.size()!=0)
					{
						Milestones=variante(Milestones,(String)TodasVariaciones.get(0));
						TodasVariaciones.remove(0);
					}
					setMilestoneslist(Milestones);
					if(Muestralo==1)
					{
						System.out.println("Actualizado milestone: " + Arrays.toString(variacion));
						int i = 0;
						while(i<Milestones.size()){
								System.out.println((String) Milestones.get(i));
								i++;
						}
						
					}
					
					contador ++;
				}
			}
		}
		public ArrayList variaciones(String[] text)
		//Out(ArrayList lista) In(String[] text)
		//Escribe un String[ ] a formato ArrayList
		
		{
			ArrayList lista=new ArrayList();
			int p=0;
			while(p<text.length)
			{
				lista.add(text[p]);
				p++;
			}
			return lista;
		}
		
		public ArrayList variante(ArrayList Arr,String text)
		//Out(ArrayList Arr) In(ArrayList Arr, String text)
		//Añade un milestone a la lista de milestones (Arr). 
		//Si este existía y solo se le ha cambiado el tiempo al milestone, se borra el antiguo de la lista
		{
			String[] parts = text.split(" ");
			String part1 = parts[0]+ " " + parts[1];
			String part2 = parts[2];
			boolean borrado=false;
			boolean noborrar=false;
			if (Integer.parseInt(parts[1])==0) //Si es un milestone de aviso para aplicar retrasos no se borra
			{
				noborrar=true;
			}
			boolean insertado=false;
			int i=0;
			while((borrado==false || insertado==false)&& i<Arr.size())
			{
				String nombre=(String) Arr.get(i);
				String[] partsarr = nombre.split(" ");
				String partarr1 = partsarr[0]+ " " + partsarr[1];
				String partarr2 = partsarr[2];
				if(part1.equals(partarr1) && noborrar==false)
				{
					Arr.remove(i);
					i--;
					borrado=true;
					if(insertado==false && Integer.parseInt(part2)<Integer.parseInt(partarr2))
					{
						Arr.add(i+1,text);
						insertado=true;
					}
				}
				if(insertado==false && Integer.parseInt(part2)<Integer.parseInt(partarr2))
				{
					Arr.add(i,text);
					insertado=true;
				}
				i++;
			}
			if(insertado==false)
			{
			Arr.add(text);
			}
			
			return Arr;
		
		}
		
		
		public boolean done() {
			return fin;
		}
	}
	class IniciarSimulacion extends Behaviour {

		final Timer timer = new Timer();
		private boolean fin = false;
			public void action() {
			
			ACLMessage mensaje = GetMensaje();

				
			if (mensaje != null && simula) 
			{
				Aircraft avion = RecepcionMensaje(mensaje, 0);
				if (avion.getOpcionMensaje() == 1)
				{ // Ya esta preparado el archivo de AvionesCompañia a partir del Input
					Milestoneslist = this.cargarCDM();
					TimerTask timerTask = new TimerTask()
					{
						int i=0;
						public void run() 
						{
							
							ArrayList Milestones=getMilestoneslist();
							if(finsimulacion)
							{
								OldMilestonesList.add("Simulacion Parada");
								FileWriter ficheroMilestones;
								try 
								{
								ficheroMilestones = new FileWriter(rutaMilestones);
						        PrintWriter pwMilestones = new PrintWriter(ficheroMilestones);
						        int j=0;
						        while(j<OldMilestonesList.size())
						        {
						        	pwMilestones.println(OldMilestonesList.get(j));
						        	j++;
						        }
						        ficheroMilestones.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								timer.cancel();
							}
							if(i==Milestones.size())
							{
								TriggerMilestone("Fin_Simulacion 17 17".split(" "));
								OldMilestonesList.add("Fin_Simulacion 17 17");
								simulacionfinalizada=true;
							}
							else
							{
								String nombre=(String)Milestones.get(i);
								TriggerMilestone(nombre.split(" "));
								OldMilestonesList.add(nombre);
							}
							i++;
							if(muestratodo == 1)
							{
								int p=0;
								while(p<Milestones.size())
								{
									System.out.println((String)Milestones.get(p));
									p++;
								}
							}
							
							if(SoloPrimerMilestone)
							{
								timer.cancel(); // para hacer pruebas, hace que solo se mande el primer milestone
							}
							if(i==(Milestones.size()+1)) //Entra quando lee el ultimo milestone
							{
								finsimulacion=true;
								System.out.println("..............................");
								System.out.println("Se han acabado los Milestones");
								System.out.println("..............................");
						        FileWriter ficheroMilestones;
								try 
								{
								ficheroMilestones = new FileWriter(rutaMilestones);
						        PrintWriter pwMilestones = new PrintWriter(ficheroMilestones);
						        int j=0;
						        while(j<OldMilestonesList.size())
						        {
						        	pwMilestones.println(OldMilestonesList.get(j));
						        	j++;
						        }
						        ficheroMilestones.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								timer.cancel();
								
								
							}
						}
					
				
					};
				
						timer.schedule(timerTask, 0, velocidadSimulacion);
				}
			}

		}
	
		private  ArrayList cargarCDM() 
		//Out(ArrayList CDM) In(String ruta)
		//Llama a la función que leerá el documento de TriggerCDM.txt
		
		{
			ArrayList CDM = leerArchivoCDM(ruta);
			return CDM;
		}
			public boolean done() {
			return fin;
		}

	}
	public void SetMensaje(ACLMessage mensaje)
	//Out(ACLMessage MensajeInicio) In(ACLMessage mensaje)
	//Copia el mensaje de entrada en MensajeInicio, para que el comportamiento IniciarSimulacion pueda leerlo
	{
		MensajeInicio=mensaje;
	}
	
	public ACLMessage GetMensaje ()
	//Out(ACLMessage Mensaje) In(ACLMessage MensajeInicio)
	//Copia el mensaje de MensajeInicio, para que el comportamiento IniciarSimulacion pueda usarlo
	{
		ACLMessage mensaje = MensajeInicio;
		MensajeInicio=null;
		return mensaje;
	}
	
	public void setMilestoneslist(ArrayList Milestones)
	//Out(ArrayList Milestoneslist) In(ArrayList Milestones)
	//Actualitza la llista de milestones
	{
		Milestoneslist=Milestones;
	}
	public ArrayList getMilestoneslist()
	//Out(ArrayList Milestoneslist) In(ArrayList Milestoneslist)
	//Solicita la lista de milestones
	{
		return Milestoneslist;
	}
	
	public  ArrayList leerArchivoCDM(String ubicacion)
	//Out(ArrayList arrlist) In(String ubicacion)
	//función que leerá el documento de TriggerCDM.txt
	
	{
		//Y generamos el objeto respecto a la ruta del archivo
		File archivo = new File(ubicacion);
		FileReader fr = null;
	    BufferedReader br = null;
	    String[] CDM = null;
	    ArrayList arrlist = new ArrayList();
	    
	    //Cadena de texto donde se guardara el contenido del archivo
	    String contenido = "";
	    
	    try
	    {	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	  
	        String linea;
	        //Obtenemos el contenido del archivo linea por linea
	        int i = 0;
	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	        while( ( linea = br.readLine() ) != null )
	        {
	            contenido = linea;
	            CDM = contenido.split("\t");
	            arrlist.add(CDM[0]+" "+CDM[1]+ " " + CDM[2]);
	            i=i+1;
	            	            
	        }
	 
	    }catch( Exception filenotfound ){
	    	System.out.println("No encuentro el archivo");
	    }
	    
	    return arrlist;
	}
	
	public void TriggerMilestone(String[] trigger)
	//Out() In(String[] trigger, int GHamount, String AOforGH, String aerolineas)
	//Crea el avión con la información que quiere mandar en cada caso. 
	//En función del milestone y de la aerolínea del avión del que quiere avisar, los receptores del mensaje serán unos o otros.
	//A continuación procederá a llamar a una función que se encargara de mandar el mensaje
	{
		Aircraft avion = new Aircraft(); // Creamos el avion que enviaremos al programa
		
		if(trigger[1].equals("-1"))
		{
			avion.setId(trigger[0]);
			avion.setIncidencias(trigger[2]);
			avion.setOpcionMensaje(6);
			AID[] receptor = new AID[1];
			receptor[0] = CFMU;
			enviarTrigger(avion, receptor);
		}
		if(trigger[1].equals("0"))
		{
			avion.setId(trigger[0]);
			avion.setIncidencias(trigger[2]);
			if(trigger[0].equals("Retrasopista"))
				avion.setOpcionMensaje(7);
			else
				avion.setOpcionMensaje(5);
			AID[] receptor = new AID[5+GHamount+aerolineas.length];
			receptor[0] = CFMU;
			receptor[1] = ATC;
			receptor[2] = Airport;
			receptor[3] = AO;
			receptor[4] = new AID ("GH-GEN", AID.ISLOCALNAME);
			int i=0;
			while(i<GHamount)
			{
				String paraules[]=AOforGH[i].split("-");
				receptor[5+i] = new AID ("GH-"+paraules[0], AID.ISLOCALNAME);
				i++;
			}
			i=0;
			while(i<aerolineas.length)
			{
				receptor[5+GHamount+i]=new AID(aerolineas[i], AID.ISLOCALNAME);
				i++;
			}
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("1")){ // Milestone = 1: El avion inicia el CDM (3h antes del ETOT')
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {CFMU};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(1);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("2")){ // Milestone = 3: 2h antes del ETOT'
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {CFMU};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(2);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("3")){ // Milestone = 3: Despegue en Aeropuerto de Origen
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(3);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if(trigger[1].equals("4")){ // Milestone = 4: Entrada en la FIR
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(4);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("5")){ // Milestone = 5: Inicio Aproximacion Final
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(5);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("6")){ // Milestone = 6: Aterrizaje
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(6);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("7")){ // Milestone = 7: Llegada a plataforma
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(7);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("8")){ // Milestone = 8: Inicio Ground Handling
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = new AID[1];// Añadimos el agente receptor del mensaje (dependera del milestone)
			
			String[] id = avion.getId().split("");
			avion.setAO(id[1]+id[2]+id[3]);
			
			
			boolean encontrado=false;
			int i=0;
			while(i<GHamount && encontrado==false)
			{
				String paraules[]=AOforGH[i].split("-");
				int j=1;
				while(j<paraules.length && encontrado==false)
				{
					if(paraules[j].equals(avion.getAO()))
					{
						receptor[0] = new AID ("GH-"+paraules[0], AID.ISLOCALNAME);
						encontrado=true;
					}
					j++;
				}
				if(encontrado==false)
					receptor[0] = new AID ("GH-GEN", AID.ISLOCALNAME);
				i++;
			}
			
			avion.setMilestone(8);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("9")){ // Milestone = 9: Confirmación final TOBT
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(9);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("10")){ // Milestone = 10: Definición TSAT (Target StartUp Approval Time)
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(10);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("11")){ // Milestone = 11: Inicio Embarque. ASBT (Actual Start Boarding Time)
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {AerolineaAvion(avion)};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(11);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
			}
		if( trigger[1].equals("12")){ // Milestone = 12: Aircraft Ready. ARDT (Aircraft Ready Time)
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {AerolineaAvion(avion)};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(12);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("13")){ // Milestone = 13: ASRT (Actual StartUp Approval Request Time)
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {AerolineaAvion(avion)};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(13);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("14")){ // Milestone = 14: Encender motores. TSAT
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(14);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("15")){ // Milestone = 15: Off-Block TOBT (Target Off Block Time)
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(15);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		if( trigger[1].equals("16")){ // Milestone = 16: Despegue
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {ATC};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(16);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
		
		//Prova per el Output
		if(trigger[1].equals("17")){
			avion.setId(trigger[0]); // Añadimos el nombre del avion
			AID receptor[] = {Output,InfoSharing};		// Añadimos el agente receptor del mensaje (dependera del milestone)
			avion.setMilestone(17);
			avion.setOpcionMensaje(10);
			avion.setNextMilestone(true);
	
			enviarTrigger(avion, receptor);
		}
	}
	public void enviarTrigger(Aircraft avion, AID[] AgenteReceptor)
	//Out() In(Aircraft avion, AID[] AgenteReceptor)
	//Envía un mensaje con la información del avion a los receptores 
	{
			
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
		
		int i=0;
		while(i<AgenteReceptor.length)
		{
			msg.addReceiver(AgenteReceptor[i]);
			i++;
		}
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
		
	}
	
	public AID AerolineaAvion(Aircraft avion)
	//Out(AID Aerolinea) In(Aircraft avion, String aerolineas)
	// Busca el nombre del Agente Aerolinea que recibira el mensaje con la información del avion
	{ 
		AID Aerolinea = null;
		boolean encontrado = false;
		
		String[] id = avion.getId().split("");
		avion.setAO(id[1]+id[2]+id[3]);
	    int i =0;
	    while(i < aerolineas.length && encontrado==false)
	    {
	    	if(("AO-"+avion.getAO()).equals(aerolineas[i]))
	    	{
	    		encontrado=true;
	    		Aerolinea = new AID(aerolineas[i], AID.ISLOCALNAME);
	    	}
	    	i++;
	    }
	    if (encontrado==false) //Si el avion no es de ninguna de las anteriores, se envia a un agente AO predeterminado
	    {
	    	Aerolinea = new AID("AO-GEN", AID.ISLOCALNAME);
	    }
	
		return Aerolinea;
		
	}
}
