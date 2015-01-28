package Agentes;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;

import Agentes.GH.RecepcionAvion;

public class SimuladorGrafica extends CDM {

	Tabla tabla;
	int[] posicionn;
	int posicion;
	boolean publicarinfo;
	Interfaz inter;
	String[] datos;
	int avionesSimulacion;
	int p=0;
	
	boolean parado;
	boolean interfazAbierta = false;

	private AID IS = new AID("InfoSharing", AID.ISLOCALNAME);
	private AID Output = new AID("Output", AID.ISLOCALNAME);
	private AID MT = new AID("MilestoneTrigger", AID.ISLOCALNAME);
	
	protected void setup() {
		System.out.println(getAID().getLocalName() + " operativo.");
		
		publicarinfo = false;
		
		addBehaviour(new RecepcionAvion());
	}

	class RecepcionAvion extends Behaviour {

		private boolean fin = false;
		int Muestralo = 0;
		
		public void action() {

			ACLMessage mensaje = blockingReceive();

			if (mensaje != null) {
				if(interfazAbierta){
					parado = inter.parar;
				}			
				// Guarda la actualización en su tabla interna
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);

				if( avion.getOpcionMensaje() == 3)
				{
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
															
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
					
				}
				if(avion.getOpcionMensaje() == 1)
				{
					// Abrimos la interfaz, la simulación comienza
					
					System.out.println("Abro la interfaz");
					
					inter = new Interfaz();
					
					inter.setVisible(true);
					interfazAbierta = true;
				}

				{
					posicionn = PosicionTabla(avion);
					posicion = posicionn[0];
					tabla.setAvion(avion, posicionn);
					
					//Actualiza la interfaz grafica
					
					int[] actualizadas = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
					
					int actuamos = Actuamos(avion, actualizadas); //Comprobamos si se ha actualizado algún parametro
					
					if(actuamos == 1){
						
						inter.ActualizarTabla(avion, posicion);
					}
				}
				if(parado)
				{
					PararSimulacion();
				}
				
			}

		}

		public boolean done() {
			return fin;
		}

	}
	
	public int[] PosicionTabla(Aircraft avion){ // Busca la posición que tiene el avión en la tabla.
		int[] posicion = {-1,0};	//Primer valor indicara la posicion del avion que tratamos. el segundo indica si el avion es nuevo(-1)
		int i = 0;
		
		while(i<tabla.getnumero())
		{
			
			if (tabla.getAvion(i).getId().equals(avion.getId()))
			{
				posicion[0]=i;
				break;
			}
			i++;
		}
		
		if (posicion[0] == -1){
			
			posicion[0] = tabla.getnumero();
			posicion[1] = -1;
			}
		
		return posicion;
		
		
	}
	public void PararSimulacion() // Prepara y envia el avion que avisará de que se ha parado la simulación.
	{
		Aircraft AvionFinalSimulacion = new Aircraft();
		AID receptor[] = {Output,IS,MT};		// Añadimos el agente receptor del mensaje
		AvionFinalSimulacion.setMilestone(17);
		AvionFinalSimulacion.setNextMilestone(true);
		AvionFinalSimulacion.setOpcionMensaje(10);
		enviarTrigger(AvionFinalSimulacion, receptor);
	}

	public void enviarTrigger(Aircraft AvionFinalSimulacion, AID[] AgenteReceptor){
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		int i=0;
		while(i<AgenteReceptor.length)
		{
			msg.addReceiver(AgenteReceptor[i]);
			i++;
		}
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(AvionFinalSimulacion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
		
	}
}
