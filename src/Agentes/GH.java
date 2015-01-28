package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Agentes.DDR2.SO6in;
import Agentes.MilestoneTrigger.IniciarSimulacion;
import Agentes.MilestoneTrigger.OrdenarMilestones;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

public class GH extends CDM {

	Tabla tabla;
	boolean publicarinfo;
	int avionesSimulacion;
	int muestraIncidencia = 0;
	int EIBTtoACGT = 3; // Minutos entre llegar a gate e iniciar el
								// Handling (Sacado del pdf del Master de
								// ITAerea)
	int ACGTtoASBT = 1; //Minutos entre  ACGT y ASBT
	ArrayList Retrasoavion = new ArrayList();
	ArrayList Retrasotiempo = new ArrayList();


	boolean variacionesnormales = false;
	int VariacioNormalACGT = 0;

	private AID Receptor = new AID("InfoSharing", AID.ISLOCALNAME);
	private AID AIDTURNAROUND = new AID("TurnAround", AID.ISLOCALNAME);
	
	
	ArrayList AvionesInicio = new ArrayList();


	protected void setup() {
		System.out.println(getAID().getLocalName() + " operativo.");

		// BORRAR
		// if(getAID().getLocalName().equals("GH"))
		// {
		// GHnumber=0;
		// }
		// else
		// GHnumber=-1;

		publicarinfo = false;

		//addBehaviour(new RecepcionAvion());
				
		addBehaviour(new RecepcionAvion());
	}

	class RecepcionAvion extends Behaviour {

		private boolean fin = false;

		// Podemos canviar la opcion "Muestralo" para que la recepcion muestre
		// por consola si recibe el mensaje o no:
		// Muestralo = 0 --> No se muestra por pantalla ninguna informacion;
		// Muestralo = 1 --> Se muestra por pantalla
		// "Se ha recibido el avion X";
		// Muestralo = 2 --> Se muestra por pantalla el contendio del mensaje
		// (el contenido del avion enviado);

		int Muestralo = 0;

		public void action() 
		{			
			Aircraft avion = new Aircraft();
			if(AvionesInicio.size()>0)
			{
				avion = getAvionesInicio();
			}
			else
			{
				boolean mensajein=false;
				while(mensajein==false)
				{
					ACLMessage mensaje = blockingReceive();
					if(mensaje!=null)
					{
						mensajein=true;
						avion = RecepcionMensaje(mensaje, 0);
					}
				}
			}
			
			
			String[] actuMilestone = { " ", " ", " " }; // Actualizaciones de
													// milestone, vector con
													// todas las que se calculan
													// en este agente
			boolean milestoneActualizado = false;

			if (avion.getOpcionMensaje() == 3) 
			{

				System.out.println("Agente " + getLocalName()+ ": he recibido los parametros");

				// Obtenemos los parametros iniciales que nos interesan:
				String[] parametros = sacarParametros(avion);

				// BorrarsacarAOs(avion);

				avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));

				// Ya podemos inicializar la tabla de aviones
				tabla = new Tabla(avionesSimulacion + 1);
					
				if(getLocalName().equals("GH-GEN"))
				{
					InformarTurnAround(avion);
						
				}
			}

			else if (avion.getOpcionMensaje() == 4 || avion.getOpcionMensaje() == 5 || avion.getOpcionMensaje() == 7) 
			{
				if (avion.getOpcionMensaje() == 5 && Retrasotiempo.isEmpty() == false) 
				{
					String RetrasoPosicion = DetectarRetrasoTiempo(avion.getIncidencias(), Retrasotiempo);
					if (RetrasoPosicion.equals("") == false) 
					{
						String[] posiciones = RetrasoPosicion.split("");
						int i = 1;// Al fer split "" el primer valor estara
									// vacio, los otros tendran los numeros
									// de las posiciones
						while (i < posiciones.length) 
						{
							String LlistaRetrasos = BuscarRetrasosTiempos(posiciones[i], i - 1);
							String[] UnRetras = LlistaRetrasos.split("\n");
							int k = 1;
							while (k < UnRetras.length) {
								if (UnRetras[k].equals("") == false) {
									AplicarRetras((UnRetras[k]).split(" "),avion.getIncidencias());
								}
								k++;
							}
							i++;
						}
					}
				}
				if (avion.getOpcionMensaje() == 4) {
					String Retraso = avion.getIncidencias();
					PonerRetraso(Retraso);
				}
			} 
			else 
			{
				int[] posicionn = tabla.PosicionTabla(avion);
				int posicion = posicionn[0];

				if (avion.getNextMilestone()) 
				{
					if (avion.getMilestone() == 8) // Caso inicio sexto
														// Milestone
													// (Aterrizaje)
					{
						System.out
								.println("GH informa: Inicio Handling para el avion "
										+ avion.getId() + ".");
						System.out
								.println("....Inicio del Milestone 8 para el avion "
										+ avion.getId() + " ....");
						System.out.println("");
						int[] act = { 8, 2 };
						int DelayARDT = AplicarNormal(VariacioNormalACGT,
								variacionesnormales);
						if (DelayARDT != 0) {
							tabla.getAvion(posicion).setValorTiemposSecundarios(2,Calculotiempo(tabla.getAvion(posicion).getValorTiemposSecundarios(2),DelayARDT, 1));
							act = new int[] { 8, 2, 16 };
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						tabla.getAvion(posicion).newStatus(5);
						tabla.getAvion(posicion).setMilestone(8);

						tabla.getAvion(posicion).setActualizadas(act);

						publicarinfo = true;
					}
				}
				else 
				{
					// Comprobamos si es necesario hacer los calculos de
					// turnaround
					int[] actualizadasrotacion = { 6, 7 };
					boolean rotacio = comprobarActualizadas(avion,actualizadasrotacion);
					if (avion.getMilestone() > 15)
						rotacio = false;
					// Comprobamos si es necesario calcular el ACGT
					int[] actACGT = { 6 };
					boolean tiempoACGT = comprobarActualizadas(avion,actACGT);
					if (avion.getMilestone() > 8)
						tiempoACGT = false;
					
					// Comprobamos si es necesario calcular el ASBT
					int[] actASBT = { 14 };
					boolean tiempoASBT = comprobarActualizadas(avion,actASBT);
					if (avion.getMilestone() > 11 || avion.getValorTiempos(2)==0) //Proteccion de en caso de solollegadas, que no aplique esta relación
						tiempoASBT = false;

					// Miramos si tenemos de aplicar algun retraso a un
					// avion en concreto
					boolean RetrasoDetectado = false;
					String[] RetrasoAvion = null;
					if (Retrasoavion.isEmpty() == false) {
						RetrasoAvion = AlgunRestraso(avion.getId(),
								avion.getMilestone(), Retrasoavion);
						if (RetrasoAvion[0].equals("Ninguno") == false) {
							Retrasoavion.remove(Integer
									.parseInt(RetrasoAvion[1]));
							RetrasoDetectado = true;
						}
					}

					int[] actualizadas = { 0, 0, 0 ,0};

					// Calculos:
					if (rotacio) // Turnaround
					{
						// estudi de si seguim el minim permesde temps de
						// rotacio.
						// Si el temps de rotacio proposat es menor al
						// permes, no s'accepta.
						// Si varia la hora estimada d'arribada,
						// actualizadas=4 poder també tindra de variar el
						// actualizadas=5
						int[] retorna = estudioRotacion(avion);
						if (retorna[1] == 1
								&& avion.getValorTiempos(3) != 0) {
							if (avion.getValorTiempos(4) != 0) {
								int h = GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiempos(4), retorna[0],"EOBT", getAID().getLocalName(), h);
								actualizadas[2] = 19;
								avion.setOpcionMensaje(2);
							}
							actualizadas[0] = 7;
							avion.setValorTiempos(4, retorna[0]);
							publicarinfo = true;

							// Mensaje definir Milestone 15:
							String milestone15 = "15 " + retorna[0];
							actuMilestone[0] = milestone15;
							milestoneActualizado = true;
							if (muestraIncidencia == 2)
							{
								System.out.println("Retraso del EOBT para el avion "+ avion.getId());
							}

						}
					}

					if (tiempoACGT) {
						int ACGT = Calculotiempo(avion.getValorTiempos(3),EIBTtoACGT, 1);

						if (avion.getValorTiemposSecundarios(0) < ACGT) 
						{
							if (avion.getValorTiemposSecundarios(0) != 0) 
							{
								int h = GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiemposSecundarios(0),ACGT, "ACGT", getAID().getLocalName(), h);
								actualizadas[2] = 19;
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiemposSecundarios(0, ACGT); // Añadimos
																			// el
																			// nuevo
																			// tiempo

							actualizadas[1] = 14;
							publicarinfo = true;

							// Mensaje definir Milestone 8:
							String milestone8 = "8 " + ACGT;
							actuMilestone[1] = milestone8;
							milestoneActualizado = true;

						}

					}

					if (tiempoASBT) {
						int ASBT = Calculotiempo(avion.getValorTiemposSecundarios(0),
								ACGTtoASBT, 1);

						if (avion.getValorTiemposSecundarios(1) < ASBT) {
							if (avion.getValorTiemposSecundarios(1) != 0) {
								int h = GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiemposSecundarios(1),ASBT, "ASBT", getAID().getLocalName(), h);
								actualizadas[2] = 19;
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiemposSecundarios(1, ASBT); // Añadimos
																			// el
																			// nuevo
																			// tiempo

							actualizadas[3] = 15;
							publicarinfo = true;

							// Mensaje definir Milestone 8:
							String milestone11 = "11 " + ASBT;
							actuMilestone[2] = milestone11;
							milestoneActualizado = true;

						}

					}
					if (RetrasoDetectado) 
					{
						AplicarRetras((RetrasoAvion[0]).split(" "),"0");
						// ************************************
						// Poner el retraso que sea!!
					}

					avion.setActualizadas(actualizadas);

					// Guardar avion despues de hacer (o no) los calculos
					// necesarios

					tabla.setAvion(avion, posicionn);
				}

				// ////////// FASE Publicar Informacion ///////

				if (publicarinfo) 
				{
					try 
					{
						PublicarInformacion(tabla.getAvion(posicion),
								Muestralo);

						publicarinfo = false;

						if (milestoneActualizado) {
							// Publicar la informacion de nuevos milestones [Mensajes al MilestoneTrigger]
							mensajeMilestone(tabla.getAvion(posicion),actuMilestone, 0);
						}

					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public boolean done() {
			return fin;
		}

		public int[] estudioRotacion(Aircraft Avio)
		//Out (int[] retorna) In(Aircraft Avio)
		//Asegura que el tiempo entre EIBT i EOBT sea igual o mayor al mínimo tiempo necesario de turn around de cada aeronave
		{
			// Cas 1. actualizada EIBT, pero no existe aun EOBT (no fem re).
			// Cas 2. actualizada EIBT, mirar que EOBT sea correcto.
			// Cas 3. actualizadaEOBT, mirar que sea valido.
			int EIBT = Avio.getValorTiempos(3);
			int EOBT = Avio.getValorTiempos(4);
			int[] retorna = { EOBT, 0 }; // [EOBT, X] X=0 no se actualiza el
										 // EOBT, X=1 si se actualiza el EOBT
			if (EOBT != 0) 
			{
				AgenteExternoTurnAround(Avio);
				boolean TurnAroundin=false;
				int TurnAround=0;
				while(TurnAroundin==false)
				{
					ACLMessage mensaje2 = blockingReceive();
					if(mensaje2!=null)
					{
						Aircraft avion2 = RecepcionMensaje(mensaje2, 0);
						if(avion2.getOpcionMensaje()==8)
						{
							String valor = avion2.getIncidencias();
							TurnAround=Integer.parseInt(valor);
							TurnAroundin=true;
						}
						else
						{
							ListaInicio(avion2);
						}
					}
				}
				if (Calculotiempo(EIBT,TurnAround, 1) > EOBT) 
				{
					retorna[0] = Calculotiempo(EIBT,TurnAround, 1);
					retorna[1] = 1;
				}
				// Codi de proteccio
			}
			return retorna;
		}

	}

	public void ListaInicio(Aircraft avion)
	{
		//Out(ArrayList AvionesInicio) In(ArrayList AvionesInicio, Aircraft avion)
		//Si el agente recibe un mensaje, pero está ocupado procesando otro, guarda ese mensaje en AvionesInicio 
		AvionesInicio.add(avion);
	}
	
	
	public void PonerRetraso(String Retraso) {
		//Out(String[]Retrasotiempo String[]Retrasoavion) In(String Retraso)
		//Le llega un retraso y lo guarda en la lista Retrasotiempo(tipo 1) o Retrasoavion (tipo 2 y 3)
		
		String[] palabras = Retraso.split(" ");
		if(palabras.length==1)
			palabras=Retraso.split("\t");
		String comentario = "";
		if (Integer.parseInt(palabras[0]) == 1) {//Retraso tipo 1
			int i = 6;
			while (i < palabras.length) {
				comentario = comentario + " " + palabras[i];
				i++;
			}
			String TiempoVar = TempsQueRetrasem(Integer.parseInt(palabras[5]));
			Retrasotiempo.add(palabras[1] + " " + palabras[2] + " "
					+ palabras[3] + " " + palabras[4] + " " + TiempoVar
					+ comentario);
		}
		if (Integer.parseInt(palabras[0]) == 2) {//Retraso tipo 2
			int i = 5;
			while (i < palabras.length) {
				comentario = comentario + " " + palabras[i];
				i++;
			}
			String TiempoVar = TempsQueRetrasem(Integer.parseInt(palabras[4]));
			Retrasoavion.add(palabras[1] + " " + palabras[2] + " "
					+ palabras[3] + " " + TiempoVar + comentario);
		}
		if (Integer.parseInt(palabras[0]) == 3) {//Retraso tipo 3
			int i = 6;
			while (i < palabras.length) {
				comentario = comentario + " " + palabras[i];
				i++;
			}
			Retrasoavion.add(palabras[1] + " " + palabras[2] + " "
					+ palabras[3] + " " + palabras[4] + comentario);
		}
	}

	public String BuscarRetrasosTiempos(String posicio, int encontrados) 
	//Out(String LlistaRetrasos, ArrayList Retrasotiempo) In(String posicio, int encontrados, ArrayList Retrasotiempo)
	//Encuentra todos los aviones afectados por un retraso de tipo 1, y escribe los retrasos como tipo 3
	{
		String LlistaRetrasos = "";
		int i = 0;
		String[] RetrasoAplicado = ((String) Retrasotiempo.get(Integer
				.parseInt(posicio) - encontrados)).split(" ");
		Retrasotiempo.remove(Integer.parseInt(posicio) - encontrados);
		int TempsPrevi = GetTempsPreviMil(Integer.parseInt(RetrasoAplicado[5]));
		int TempsPost = GetTempsPostMil(Integer.parseInt(RetrasoAplicado[5]));
		while (i < tabla.getnumero()) {
			Aircraft avion = tabla.getAvion(i);
			int ValorPrev = 0;
			int ValorPost = 0;
			if (TempsPrevi >= 1 && TempsPrevi <= 5)
				ValorPrev = avion.getValorTiempos(TempsPrevi - 1);
			if (TempsPrevi >= 6 && TempsPrevi <= 10)
				ValorPrev = avion.getValorTiemposSecundarios(TempsPrevi - 6);
			if (TempsPrevi >= 11 && TempsPrevi <= 12)
				ValorPrev = avion.getValorTiemposVuelo(TempsPrevi - 11);
			if (TempsPost >= 1 && TempsPost <= 5)
				ValorPost = avion.getValorTiempos(TempsPost - 1);
			if (TempsPost >= 6 && TempsPost <= 10)
				ValorPost = avion.getValorTiemposSecundarios(TempsPost - 6);
			if (TempsPost >= 11 && TempsPost <= 12)
				ValorPost = avion.getValorTiemposVuelo(TempsPost - 11);

			int PasA = restatemps(ValorPrev,
					Integer.parseInt(RetrasoAplicado[0]));
			int PasB = restatemps(ValorPost,
					Integer.parseInt(RetrasoAplicado[0]));
			int PasC = restatemps(ValorPrev,
					Integer.parseInt(RetrasoAplicado[1]));
			int PasD = restatemps(ValorPost,
					Integer.parseInt(RetrasoAplicado[1]));

			boolean ModeliAO= MirarmodeliAO(avion, RetrasoAplicado[6],RetrasoAplicado[7]);
			if ((PasA * PasB <= 0 || PasC * PasD <= 0) && ModeliAO)// Significa que el nostre temps esta aqui dintre
			{
				int k = 8;
				String Comment = "";
				while (k < RetrasoAplicado.length) {
					Comment = Comment + " " + RetrasoAplicado[k];
					k = k + 1;
				}
				LlistaRetrasos = LlistaRetrasos + "\n" + avion.getId() + " "
						+ RetrasoAplicado[3] + " " + RetrasoAplicado[4]
						+ Comment;
			}
			i++;

		}

		return LlistaRetrasos;
	}

	public void AplicarRetras(String[] Informacio,String hora) 
	//Out() In (String[] Informacio, String hora)
	//Aplica un retraso de tipo 3 a cualquier avión
	{
		Aircraft avion = new Aircraft();
		avion.setId(Informacio[0]);
		int[] posicionn = tabla.PosicionTabla(avion);
		int posicion = posicionn[0];
		avion = tabla.getAvion(posicion);
		avion.setOpcionMensaje(2);
		
		int TempsActual = TreureTemps(avion, Informacio[2]);
		if (TempsActual!=0)
		{
			int[] Actualizada = TreureActualizada(Informacio[2]);
			int TempsNou = sumatemps(TempsActual, Integer.parseInt(Informacio[1]));
			// Posem indicació de quin temps s'actualiza
			avion.setActualizadas(Actualizada);
	
			// Actualitzem el temps
			if (Actualizada[0] >= 4 && Actualizada[0] <= 7)
				avion.setValorTiempos(Actualizada[0] - 3, TempsNou);
			if (Actualizada[0] >= 14 && Actualizada[0] <= 18)
				avion.setValorTiemposSecundarios(Actualizada[0] - 14, TempsNou);
			if (Actualizada[0] >= 20 && Actualizada[0] <= 21)
				avion.setValorTiemposVuelo(Actualizada[0] - 20, TempsNou);
	
			// Posem la Incidencia
			int k = 3;
			String Comment = "";
			while (k < Informacio.length) {
				Comment = Comment + " " + Informacio[k];
				k = k + 1;
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
	
	public void AgenteExternoTurnAround(Aircraft avion)
	{
		//Out() In(Aircraft Avion)
		//Manda un mensaje al agente TurnAround pidiendo información sobre el tiempo de turn around de un avión.
		Aircraft avionTA = avion;
		avionTA.setOpcionMensaje(8);
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
				
		msg.addReceiver(AIDTURNAROUND);
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avionTA);
							
		// Enviamos el avion
		msg.setContent(jsonOutput);
							
		send(msg);
		
	}
	
	public void InformarTurnAround(Aircraft avion)
	{
		//Out() In(Aircraft Avion)
		//Manda un mensaje al agente TurnAround para informarle de los parámetros de simulación. Este mensaje solo lo manda GH-GEN
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		msg.addReceiver(AIDTURNAROUND);
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
					
		// Enviamos el avion
		msg.setContent(jsonOutput);
					
		send(msg);
		
	}
	public Aircraft getAvionesInicio()
	{
		//Out(Aircraft a, ArrayList AvionesInicio) In(ArrayList AvionesInicio)
		//Mira si tenemos mensajes por procesar en la lista AvionesInicio, si no están todos procesados, coge uno y lo procesa
		Aircraft a = new Aircraft();
		a=(Aircraft) AvionesInicio.get(0);
		AvionesInicio.remove(0);
		return a;
	}

	
}
