package testerComunicaciones;



import jade.core.Agent;
import java.io.*;

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
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPANames;
import jade.domain.DFService;
import jade.domain.FIPAException;
 
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.Ontology;



public class Emisor extends Agent {
	
	private AID Receptor = new AID ("MilestoneTrigger", AID.ISLOCALNAME);

	// El agente debe elegir el método de codificación
	private Codec codec = new SLCodec(); 
		 // Y la ontologia que usará
	private Ontology ontology = OntologiaCDMA.getInstance();
		 
	public void setup(){
		
		System.out.println( getAID().getLocalName()+ " operativo.");
		
		// Registramos el método de codificacion y la ontología
		 getContentManager().registerLanguage(codec); 
		 getContentManager().registerOntology(ontology);

		// Pequeña interfaz por consola para retrasar el inicio del CDM y que asi nos de tiempo de ver los mensajes que se envian.
		 
		InputStreamReader isr = new InputStreamReader(System.in);  
		BufferedReader br = new BufferedReader(isr);
	
			
			try // Tener en cuenta excepciones
			{
					//System.out.println ("Escribe la matricula del avion"); //Pedimos al usuario (mostramos por pantalla)
					
					System.out.println("");
					System.out.println("Presione ENTER para iniciar el simulador.");
					
					String id = br.readLine();	// Leemos una linea del teclado, la convertimos a numero y la guardamos en la variable id;	
//	
//					int[] tiempos = {1100, 1200,};
//	
					Aircraft avionprueba = new Aircraft();
					
//					int[] tiempos = {900, 1200, 1300, 0, 0};
//					int[] act = {99};	
//					int mil = 1;
					avionprueba.setOpcionMensaje(1);
//					avionprueba.setId(id);
//					avionprueba.setActualizadas(act);
//					avionprueba.setTiempos(tiempos);
//					avionprueba.newStatus(1);
//					avionprueba.setMilestone(mil);
					avionprueba.setNextMilestone(true);
					
//					// Pruebas MostrarAvion
//					avionprueba.printFlight(1);
//					avionprueba.printFlight(2);
//					avionprueba.printFlight(3);
//					avionprueba.printFlight(4);
//					avionprueba.printFlight(5);
//					avionprueba.printFlight(6);
//					
					
					
					// Enviamos el avion:	
					this.EnviarInformacionPrueba(avionprueba);
						
					// System.out.println("Avion " +avionprueba.getId()+ " enviado.");
					System.out.println("...Iniciando Simulador v1.0...");
					System.out.println("");
				
				
			}
			catch(Exception e) //Exception e = cualquier excepcion
			{
				System.out.println("Error" + e.getMessage());
			}		
	
	}
	
	public void EnviarInformacionPrueba(Aircraft avion) throws IOException, CodecException, OntologyException{
		
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF); 
		 
		 // Añado receptor y defino codificacion y ontolgia

		msg.addReceiver(Receptor);
		
		Gson gson = new Gson ();
		String jsonOutput = gson.toJson (avion);
		msg.setContent(jsonOutput);
		
		
		send(msg); 
		 
	
	}

}




