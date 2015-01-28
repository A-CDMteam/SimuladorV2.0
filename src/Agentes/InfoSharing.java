package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


import com.google.gson.Gson;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import Agentes.Aircraft;
import Agentes.Airport.RecepcionAvion;

public class InfoSharing extends CDM {

	// Definimos la lista de los agentes a los que envia la información en cada
	// actualización.

	private AID[] CDMAgents;
	int muestralo = 1;
	boolean simulacionfinalizada=false;
	
	int avionesSimulacion;
	int GHamount;
	String[] AOforGH={""};
	
	ArrayList ListaMensajes = new ArrayList();
	
	String[] aerolineas = null;

	protected void setup() {
		System.out.println(getAID().getLocalName() + " operativo.");
		
		ParallelBehaviour p = new ParallelBehaviour (ParallelBehaviour.WHEN_ALL);
		p.addSubBehaviour(new RecepcionMessage());
		p.addSubBehaviour(new ActualizarTabla());

		
		addBehaviour (p);
	}
	class RecepcionMessage extends Behaviour 
	{
		private boolean fin = false;
		public void action() {

			ACLMessage mensaje = blockingReceive();

			if (mensaje != null) 
			{
				Aircraft avion = RecepcionMensaje(mensaje, 0);
				if(avion.getOpcionMensaje() == 3){
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					
					GHamount = avion.getAO().split("\t").length;
					AOforGH=avion.getAO().split("\t");
					
					CDMAgents = new AID[6];
					CDMAgents[0]= new AID("Interfaz", AID.ISLOCALNAME);
					CDMAgents[1]= new AID("CFMU", AID.ISLOCALNAME);
					CDMAgents[2]= new AID("ATC", AID.ISLOCALNAME);
					CDMAgents[3]= new AID("Airport", AID.ISLOCALNAME);
					CDMAgents[4]= new AID("Output", AID.ISLOCALNAME);
					
					
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
					
					aerolineas = listaaerolineas(parametros);
					
				}
				if(avion.getOpcionMensaje() != 3){

				SetMessages(mensaje);
				}
			}
		}
		
		public boolean done() 
		{
			return fin;
		}

	}
	class ActualizarTabla extends Behaviour 
	{

		private boolean fin = false;

		int Muestralo = 0;

		public void action() 
		{
			//ACLMessage mensaje = blockingReceive();
			ACLMessage mensaje=GetMessages();
			//SetMessages(mensaje, "tabla");
			if (mensaje != null && simulacionfinalizada==false) 
			{
				// Primero el agente recibe i descodifica el mensaje
				Aircraft avionmensaje = RecepcionMensaje(mensaje, Muestralo);
				
				//Miramos que no sea aviso de que se finaliza la simulacion.
				if(avionmensaje.getOpcionMensaje()==10 && avionmensaje.getMilestone()==17)
					simulacionfinalizada=true;
				else
				{
				
				
					// Actualizamos la tabla
				
					int[] posicionn = tabla.PosicionTabla(avionmensaje);
					int posicion = posicionn[0];
				
					Aircraft aviontabla = tabla.getAvion(posicion); // Recojemos el primer avion de la tablar para poder actualizarlo correctamente
				
					Aircraft AvionActualizado = ActualizarTabla(avionmensaje, aviontabla, Muestralo);
				
					///PROTECCIO ZEROS
				
					if(avionmensaje.getActualizadas()[0]==8 && (avionmensaje.getMilestone()!=1 && avionmensaje.getMilestone()!=0))
					{
						int[] novesACT=new int[2];
						if(AvionActualizado.getValorTiempos(3)==0)
							novesACT[0]=4;
						if(AvionActualizado.getValorTiempos(4)==0)
							novesACT[1]=5;
					
						AvionActualizado.AddActualizadas(AvionActualizado.getActualizadas(), novesACT);
					}
				
					tabla.setAvion(AvionActualizado,posicionn);
				
				
				
					/////////////////////////// FASE PUBLICAR INFORMACION ////////////////

					// Decidimos si es necesario hacer un Broadcast de la informacion:
					boolean enviamos = enviamosinfo_SIoNO(posicion);
					
					if(enviamos)
					{
						if(muestralo == 1)
						{
							System.out.println("");
							System.out.println("IS: BroadCastInformation");
							
							tabla.getAvion(posicion).printFlight(11);
						
							System.out.println("Incidencias:");
							tabla.getAvion(posicion).printFlight(12);
						
						}
				
						try 
						{
							DecidirGH(tabla.getAvion(posicion)); //Afegim a CDMAagents el AID del GH corresponent a cada cas
							BroadcastInfo(tabla.getAvion(posicion), Muestralo, CDMAgents);

					
						} 
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}	
				
					else
					{
						// System.out.println("Broadcast end");
					}
				}
			}
		}
	
		public boolean done() 
		{
			
			return fin;
		}

	}

	public void DecidirGH (Aircraft avion)
	//Out(AID[] CDMAgents) In(Aircraft avion, int GHamount, StringAOforGH)
	//Busca el agente GH que tiene el contrato con la aerolínea del avión de entrada.
	{
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
					CDMAgents[5] = new AID ("GH-"+paraules[0], AID.ISLOCALNAME);
					encontrado=true;
				}
				j++;
			}
			if(encontrado==false)
				CDMAgents[5] = new AID ("GH-GEN", AID.ISLOCALNAME);
			i++;
		}
		
		
	}
	public Aircraft ActualizarTabla(Aircraft mensaje, Aircraft avionTabla, int muestralo) { // La manera de actualizar la tabla de datos del IS es diferente a la de los otros agentes porque recibira mensajes muy juntos temporalmente
		//Out(Aircraft avionTabla) In(Aircraft mensaje, Aircraft avionTabla, int muestralo)
		//Actualiza el avionTabla según las actualizadas del mensaje.
		if(muestralo == 1) // Mostraremos por consolas las variables actualizadas que ha recibido
		{
			System.out.println("IS: Actualizacion entrante...");
			mensaje.printFlight(8);
		}
		
		for ( int i = 0; i < mensaje.getActualizadas().length; i++){
			int opcion = mensaje.getActualizadas()[i]; // Encontramos cual es la variable a actualizar en el avion

			
			if ( opcion == 1){
				avionTabla.setId(mensaje.getId());
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ID actualizado a: "+ avionTabla.getId());
				}
			}
			if ( opcion == 2){
				avionTabla.setSTATUS(mensaje.getSTATUS());
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", STATUS actualizado a: "+ avionTabla.getSTATUS());
				}
			}
			if ( opcion == 3){
				avionTabla.setValorTiempos(0, mensaje.getValorTiempos(0));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ETOT' actualizado a: "+ avionTabla.getValorTiempos(0));
				}
			}
			if ( opcion == 4){
				avionTabla.setValorTiempos(1, mensaje.getValorTiempos(1));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ELDT actualizado a: "+ avionTabla.getValorTiempos(1));
				}
			}
			if ( opcion == 5){
				avionTabla.setValorTiempos(2, mensaje.getValorTiempos(2));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ETOT actualizado a: "+ avionTabla.getValorTiempos(2));
				}
			}
			if ( opcion == 6){
				avionTabla.setValorTiempos(3, mensaje.getValorTiempos(3));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", EIBT actualizado a: "+ avionTabla.getValorTiempos(3));
				}
			}
			if ( opcion == 7 ){
				avionTabla.setValorTiempos(4, mensaje.getValorTiempos(4));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", EOBT actualizado a: "+ avionTabla.getValorTiempos(4));
				}
			}
			if ( opcion == 8){
				avionTabla.setMilestone(mensaje.getMilestone());
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", Milestone actualizado a: "+ avionTabla.getMilestone());
				}
			}
			if ( opcion == 14){
				avionTabla.setValorTiemposSecundarios(0, mensaje.getValorTiemposSecundarios(0));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ACGT actualizado a: "+ avionTabla.getValorTiemposSecundarios(0));
				}
			}
			if ( opcion == 15){
				avionTabla.setValorTiemposSecundarios(1, mensaje.getValorTiemposSecundarios(1));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ASBT actualizado a: "+ avionTabla.getValorTiemposSecundarios(1));
				}
			}
			if ( opcion == 16){
				avionTabla.setValorTiemposSecundarios(2, mensaje.getValorTiemposSecundarios(2));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ARDT actualizado a: "+ avionTabla.getValorTiemposSecundarios(2));
				}
			}
			if ( opcion == 17){
				avionTabla.setValorTiemposSecundarios(3, mensaje.getValorTiemposSecundarios(3));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", ASRT actualizado a: "+ avionTabla.getValorTiemposSecundarios(3));
				}
			}
			if ( opcion == 18){
				avionTabla.setValorTiemposSecundarios(4, mensaje.getValorTiemposSecundarios(4));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", TSAT actualizado a: "+ avionTabla.getValorTiemposSecundarios(4));
				}
			}
			if ( opcion == 19){
				String incidencias = actualizarIncidencias(avionTabla.getIncidencias(), mensaje.getIncidencias());
				
				avionTabla.setIncidencias(incidencias);
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", Incidencias actualizado: "+ avionTabla.getIncidencias());
				}
			}
			if ( opcion == 20){
				avionTabla.setValorTiemposVuelo(0,mensaje.getValorTiemposVuelo(0));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", TFIR actualizado a: "+ avionTabla.getValorTiemposVuelo(0));
				}
			}
			if ( opcion == 21){
				avionTabla.setValorTiemposVuelo(1,mensaje.getValorTiemposVuelo(1));
				avionTabla.AddActualizadas(mensaje.getActualizadas(),avionTabla.getActualizadas());
				if (muestralo == 1)
				{
					System.out.println("Actualizacion IS:");
					System.out.println("Avion: "+avionTabla.getId()+", TAPP actualizado a: "+ avionTabla.getValorTiemposVuelo(1));
				}
			}
			if (opcion == 99){ // Caso en el que se incia el Milestone, el CFMU enviara el avion.
				avionTabla = mensaje;
				int[] actualizadas = {1,2,3,4,20,21}; //Las variables que define el CFMU cuando inicia el Milestone
				avionTabla.setActualizadas(actualizadas);
				
				if (muestralo == 1)
				{
					System.out.println("Avion: "+avionTabla.getId()+", creado en la base de datos...");
					System.out.println("");
				}
			}	
		}
		avionTabla.setUltimoAgente(mensaje.getUltimoAgente());
		return avionTabla;
	}

	public void BroadcastInfo(Aircraft avion, int muestralo, AID[] Agentes) throws IOException {
		
		//Out() In(Aircraft avion, int muestralo, AID[] Agentes)
		//Envía un mensaje con la información del avion a los Agentes receptores
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		// Añadimos los receptores
		for ( int i = 0; i < Agentes.length; i++){
			
			msg.addReceiver(Agentes[i]);			
		}
			// Añadimos la aerolinia a la que pertenece el avion
		AID aerolinea = AerolineaAvion(avion);
		msg.addReceiver(aerolinea);
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
		
		if (muestralo == 1) {
			System.out.println(getLocalName()
					+ ": Broadcast Information of ");
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
	public AID AerolineaAvion(Aircraft avion)
	//Out(AID Aerolinea) In(Aircraft avion, String[] aerolineas)
	//Busca el agente AO que se ocupará del avión. Si no existe un agente AO para la aerolínea del avión será de AO-GEN
	{ // Añadimos el nombre del Agente Aerolinea que recibira el avion
		AID Aerolinea = null;
		boolean encontrado = false;
		
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
	public boolean enviamosinfo_SIoNO(int posicion)
	//Out(boolean enviamos) In(int posicion)
	//Mira si el avión de la tabla situado en posición y si ha actualizado alguna información realiza el broadcast
	{

		boolean enviamos = false;
		
		int[] actu = tabla.getAvion(posicion).getActualizadas();
		
		for (int i = 0; i < tabla.getAvion(posicion).getActualizadas().length; i++) {
			if(actu[i] != 0){
				
				enviamos = true;
				break;
			}
		}
		
		return enviamos;
		
	}

	public void SetMessages (ACLMessage text)
	//Out(ArrayList ListaMensajes) In(ACLMessage text, ArrayList ListaMensajes)
	//Añade un mensaje a la ListaMensajes de manera que entre comportamientos se puedan transferir informaciones.
	{
		ListaMensajes.add(text);
	}
	public ACLMessage GetMessages ()
	//Out(ACLMessage text, ArrayList ListaMensajes) In(ArrayList ListaMensajes)
	//Coge un mensaje a la ListaMensajes de manera que entre comportamientos se puedan transferir informaciones.
	{
		ACLMessage text = null;
		if(ListaMensajes.size()!=0)
		{
			text=(ACLMessage)ListaMensajes.get(0);
			ListaMensajes.remove(0);
			
		}
		return text;
	}
	public String actualizarIncidencias(String inTabla, String inMensaje)
	//Out(String incidencias) In(String inTabla, String inMensaje)
	//Añade las incidencias del mensaje a las de la Tabla, teniendo en cuenta que si una incidencia ya existía no la tiene de añadir.
	{
		
		String incidencias = inTabla;
		
		String[] vecInTabla = inTabla.split("\n");
		String[] vecInMensaje = inMensaje.split("\n");
		
		int i = 0;
		int c = 0;
		while(c<vecInTabla.length){
			while(i<vecInMensaje.length){
				if(vecInTabla[c].equals(vecInMensaje[i]) || vecInMensaje[i].equals(""))
				{
					vecInMensaje[i]="";
				}
				else
				{
					String[] palabrastabla=vecInTabla[c].split(" ");
					String[] palabrasmensaje=vecInMensaje[i].split(" ");
					if(palabrastabla[0].equals(palabrasmensaje[0]) && Integer.parseInt(palabrastabla[5])>Integer.parseInt(palabrasmensaje[3]))
					{
						palabrasmensaje[3]=palabrastabla[5];
						int k=1;
						vecInMensaje[i]=palabrasmensaje[0];
						while(k<palabrasmensaje.length)
						{
							vecInMensaje[i]=vecInMensaje[i]+" "+palabrasmensaje[k];
							k++;
						}
						if(Integer.parseInt(palabrasmensaje[3])>=Integer.parseInt(palabrasmensaje[5]))
						{
							vecInMensaje[i]="";
						}
					}
				}
				i++;
			}
			i=0;
			c++;
		}
		i=0;
		while(i<vecInMensaje.length)
		{
			if(false==vecInMensaje[i].equals(""))
			{
				incidencias = incidencias + vecInMensaje[i]+"\n";
			}
			i++;
		}
		return incidencias;
	}
}
