package testerComunicaciones;

import java.io.IOException;
import java.io.Serializable;

import Agentes.Aircraft;

import com.google.gson.Gson;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;



import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

public class Receptor extends Agent {


	
	private AID Emisor = new AID ("Emisor", AID.ISLOCALNAME);
	
	// El agente debe elegir el método de codificación
	 public Codec codec = new SLCodec(); 
	 // Y la ontologia que usará
	 public Ontology ontology = OntologiaCDMA.getInstance();
	 
	public void setup(){
		
		System.out.println( getAID().getLocalName()+ " operativo.");
		 
				 				
				 System.out.println(getLocalName() + " Esperando mensaje... ");
				
				ACLMessage msg = blockingReceive(); 
//					System.out.println(getLocalName() + " Mensaje recibido");
//					
//					System.out.println("Contenido del mensaje:");
//					System.out.println(" ");	
//					System.out.println(msg.toString());
//					
//					System.out.println(" ");	
//					System.out.println("Extraemos y guardamos el avion recibido...");
//					
//					
					FlightP avion = extraerInfo(msg);
	
					
					System.out.println(getLocalName() + ": acaba de recibir el siguiente mensaje: ");
					System.out.println("Avion " + avion.getId()+ " Status " + avion.getSTATUS());
					
					System.out.println("...Devolviendo el avion actualizado...");

					try {
						PublicarInfo(avion);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					

		
	}
	
	public FlightP extraerInfo(ACLMessage msg)
	{
		
		Gson gson = new Gson ();
		String jsonInput = msg.getContent();
		
		FlightP avion = new FlightP ();
		
		avion = gson.fromJson (jsonInput, avion.getClass());

		
		return avion;
	}
public void PublicarInfo(FlightP avion) throws IOException {
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);

		msg.addReceiver(Emisor);
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);

	}
}




