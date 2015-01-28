package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
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
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import Agentes.Aircraft;
import Agentes.Airport.RecepcionAvion;

public class ATC extends CDM {
	
	Tabla tabla;
	boolean publicarinfo;
	
	int avionesSimulacion;
	
	private AID IS = new AID("InfoSharing", AID.ISLOCALNAME);
	private AID MT = new AID("MilestoneTrigger", AID.ISLOCALNAME);

	ArrayList TakeOffSequence = new ArrayList();
	ArrayList LandingSequence = new ArrayList();
	int EOBTtoTSAT = 5; // Minutos encender motores e iniciar txi (Sacado del pdf del Master de ITAerea)
	int tiempoMil9 = 5; // Minutos entre el EIBT y el milestone 9 (confirmación EOBT) (tiempo en hacer la pre-departure sequence)
	int tiempoMil10 = 3; // Minutos entre el milestone 9 y el 10 (definicion TSAT)
	int[] SeparationTimein = {2,3,4,4,2,2,3,3,2,2,2,3,2,2,2,2}; //minutos en funcion de la Categoria del avion de delante y el de atras en llegadas
	int[] SeparationTimeout = {2,3,4,4,2,2,3,3,2,2,2,3,2,2,2,2}; //minutos en funcion de la Categoria del avion de delante y el de atras en salidas
	ArrayList SeparationsTimeReducted = new ArrayList(); //Per posar les restriccions de reduccio de capacitat de pista, cada linia te: 'Hinici' 'Hfinal' 'Minuts de separacio'
	ArrayList Retrasoavion=new ArrayList();
	ArrayList Retrasotiempo=new ArrayList();
	ArrayList Retrasopista=new ArrayList();
	int politicaCongestio = 1; //1=l'avio es retrasa fins el primer slot. 2=Mantenim ordre d'arribades, retrasem tots els avions necessaris.
	
	boolean variacionesnormales=false;
	int VariacioNormalVuelo = 15;
	int VariacioNormalTFIR=0;
	int VariacioNormalTAPP=0;
	int VariacioNormalTaxiIn = 3;
	int VariacioNormalACGT=0;
	int VariacioNormalEOBT=0;
	int VariacioNormalTaxiOut = 4;
	
	int ophINnominal;
	int ophOUTnominal;
	
	protected void setup()
    {	
		System.out.println( getAID().getLocalName()+ " operativo.");
		
		
		publicarinfo = false;
	    addBehaviour(new RecepcionAvion());	   
    }


	class RecepcionAvion extends Behaviour
	{
    
	private boolean fin = false;
	
	
	// Podemos canviar la opcion "Muestralo" para que la recepcion muestre por consola si recibe el mensaje o no:
	// Muestralo = 0 --> No se muestra por pantalla ninguna informacion;
	// Muestralo = 1 --> Se muestra por pantalla "Se ha recibido el avion X";
	// Muestralo = 2 --> Se muestra por pantalla el contendio del mensaje (el contenido del avion enviado);
	
    int Muestralo = 0;

	public void action(){
		
		ACLMessage mensaje = blockingReceive();
		
		String[] actuMilestone = {" ", " ", " ", " ", " "}; // Actualizaciones de milestone, vector con todas las que se calculan en este agente
		boolean milestoneActualizado = false;
	
		if (mensaje != null)
		{
			Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
			if(avion.getOpcionMensaje() == 3){
				
				System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
				
				// Obtenemos los parametros iniciales que nos interesan:
				String[] parametros = sacarParametros(avion);
				
				avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
				ophINnominal = Integer.parseInt(valorparametro(parametros[7]));
				ophOUTnominal = Integer.parseInt(valorparametro(parametros[8]));
				ActualizarSeparationTimes();
				
				
				// Ya podemos inicializar la tabla de aviones
				tabla = new Tabla(avionesSimulacion+1);
				
			}
			if(avion.getOpcionMensaje()==4 || avion.getOpcionMensaje()==5 || avion.getOpcionMensaje()==7)
			{
				if(avion.getOpcionMensaje()==7 && Retrasopista.isEmpty()==false)
				{
					String ReduccionCapacidad = TrobarRetraso(avion.getIncidencias(),Retrasopista);
					if (ReduccionCapacidad.equals("")==false)
					{
						boolean[] aplicar=AddSeparationTime (ReduccionCapacidad);
						
						if(aplicar[0])
						PosarSeparacionsReduccioCapacitat (ReduccionCapacidad,1,avion.getIncidencias());//1 per les arribades
						if(aplicar[1])
						PosarSeparacionsReduccioCapacitat (ReduccionCapacidad,0,avion.getIncidencias());//0 per les sortides
					}
										
				}
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
				if(posicionn[1]==-1)
				{
					tabla.setAvion(avion, posicionn);
					posicionn[1]=0;
			}
			if(avion.getNextMilestone() == true && avion.getOpcionMensaje() == 0)
				{ 	
					int[] act = {8, 2};
					tabla.getAvion(posicion).setActualizadas(act);
					if(avion.getMilestone() == 3) //Caso inicio tercer Milestone (Despegue en origen)
					{
						System.out.println("ATC informa: El avion "+avion.getId()+" esta despegando en su aeropuerto de origen");
						System.out.println("....Inicio del Milestone 3 para el avion "+avion.getId()+" ....");
						System.out.println("");
						
						int DelayVuelo=AplicarNormal(VariacioNormalVuelo,variacionesnormales);
						if(DelayVuelo!=0)
						{
							tabla.getAvion(posicion).setValorTiemposVuelo(0, Calculotiempo(tabla.getAvion(posicion).getValorTiemposVuelo(0),DelayVuelo,1));
							int[] actu = {20};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(1); //Cambiamos el STATUS del avion a AIRBORNE
						tabla.getAvion(posicion).setMilestone(3);
						publicarinfo = true;
					}
					if(avion.getMilestone() == 4) //Caso inicio cuarto Milestone (Entrando en la FIR)
					{
						System.out.println("ATC informa: El avion "+avion.getId()+" esta entrando en la FIR");
						System.out.println("....Inicio del Milestone 4 para el avion "+avion.getId()+" ....");
						System.out.println("");
					
						int DelayFIR=AplicarNormal(VariacioNormalTFIR,variacionesnormales);
						if(DelayFIR!=0)
						{
							tabla.getAvion(posicion).setValorTiemposVuelo(1, Calculotiempo(tabla.getAvion(posicion).getValorTiemposVuelo(1),DelayFIR,1));
							int[] actu = {21};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(2);
						tabla.getAvion(posicion).setMilestone(4);
					
						publicarinfo = true;
					}
					if(avion.getMilestone() == 5) //Caso inicio quinto Milestone (Empezando la final approach)
					{
						System.out.println("ATC informa: El avion "+avion.getId()+" esta empezando la Final Approach");
						System.out.println("....Inicio del Milestone 5 para el avion "+avion.getId()+" ....");
						System.out.println("");
						
						int DelayTAPP=AplicarNormal(VariacioNormalTAPP,variacionesnormales);
						if(DelayTAPP!=0)
						{
							tabla.getAvion(posicion).setValorTiempos(1, Calculotiempo(tabla.getAvion(posicion).getValorTiempos(1),DelayTAPP,1));
							int[] actu = {4};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(3);
						tabla.getAvion(posicion).setMilestone(5);
					
						publicarinfo = true;
					}
					if(avion.getMilestone() == 6) //Caso inicio sexto Milestone (Aterrizaje)
					{
						System.out.println("ATC informa: El avion "+avion.getId()+" ha aterrizado");
						System.out.println("....Inicio del Milestone 6 para el avion "+avion.getId()+" ....");
						System.out.println("");
				
						int DelayTaxiIn=AplicarNormal(VariacioNormalTaxiIn,variacionesnormales);
						if(DelayTaxiIn!=0)
						{
							tabla.getAvion(posicion).setValorTiempos(3, Calculotiempo(tabla.getAvion(posicion).getValorTiempos(3),DelayTaxiIn,1));
							int[] actu = {6};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(4);
						tabla.getAvion(posicion).setMilestone(6);
					
						publicarinfo = true;
					}
			
					if(avion.getMilestone() == 7) //Caso inicio septimo Milestone (llegada a plataforma)
					{
						System.out.println("ATC informa: El avion "+avion.getId()+" ha llegado a Gate");
						System.out.println("....Inicio del Milestone 7 para el avion "+avion.getId()+" ....");
						System.out.println("");
						
						int DelayACGT=AplicarNormal(VariacioNormalACGT,variacionesnormales);
						if(DelayACGT!=0)
						{
							tabla.getAvion(posicion).setValorTiemposSecundarios(0, Calculotiempo(tabla.getAvion(posicion).getValorTiemposSecundarios(0),DelayACGT,1));
							int[] actu = {14};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(5);
						tabla.getAvion(posicion).setMilestone(7);
	
						publicarinfo = true;
					}
					if(avion.getMilestone() == 9) //Caso inicio noveno Milestone (Confirmacion TOBT)
					{
						System.out.println("ATC informa: Confirmado el TOBT para el avion "+avion.getId()+". "); //TOBT = "+avion.getValorTiempos(4)
						System.out.println("....Inicio del Milestone 9 para el avion "+avion.getId()+" ....");
						System.out.println("");
					
						
						
						tabla.getAvion(posicion).newStatus(5);
						tabla.getAvion(posicion).setMilestone(9);
						
						publicarinfo = true;
					}
					if(avion.getMilestone() == 10) //Caso inicio decimo Milestone (Definición TSAT)
					{
						System.out.println("ATC informa: Definido el TSAT para el avion "+avion.getId()+". ");// TSAT = "+avion.getValorTiemposSecundarios(4))
						System.out.println("....Inicio del Milestone 10 para el avion "+avion.getId()+" ....");
						System.out.println("");
					
						tabla.getAvion(posicion).newStatus(6);
						tabla.getAvion(posicion).setMilestone(10);
						
						publicarinfo = true;
					}
					if(avion.getMilestone() == 14) //Caso inicio Milestone 14 (StartUp Approved)
					{
						System.out.println("ATC informa: Avion "+avion.getId()+": Start Up Approved." );
						System.out.println("....Inicio del Milestone 14 para el avion "+avion.getId()+" ....");
						System.out.println("");
						
						int DelayEOBT=AplicarNormal(VariacioNormalEOBT,variacionesnormales);
						if(DelayEOBT!=0)
						{
							tabla.getAvion(posicion).setValorTiempos(4, Calculotiempo(tabla.getAvion(posicion).getValorTiempos(4),DelayEOBT,1));
							int[] actu = {7};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(8);
						tabla.getAvion(posicion).setMilestone(14);
						
						publicarinfo = true;
					}
					if(avion.getMilestone() == 15) //Caso inicio Milestone 15 (Off-Block)
					{
						System.out.println("ATC informa: Avion "+avion.getId()+": Inicio Taxi-Out ");
						System.out.println("....Inicio del Milestone 15 para el avion "+avion.getId()+" ....");
						System.out.println("");
					
						int DelayTaxiOut=AplicarNormal(VariacioNormalTaxiOut,variacionesnormales);
						if(DelayTaxiOut!=0)
						{
							tabla.getAvion(posicion).setValorTiempos(2, Calculotiempo(tabla.getAvion(posicion).getValorTiempos(2),DelayTaxiOut,1));
							int[] actu = {5};
							tabla.getAvion(posicion).AddActualizadas(tabla.getAvion(posicion).getActualizadas(),actu);
							tabla.getAvion(posicion).setOpcionMensaje(2);
						}
						
						tabla.getAvion(posicion).newStatus(9);
						tabla.getAvion(posicion).setMilestone(15);
						
						publicarinfo = true;
					}
					if(avion.getMilestone() == 16) //Caso inicio Milestone 16 (Despegue)
					{
						System.out.println("ATC informa: Avion "+avion.getId()+": ha despegado");
						System.out.println("....Inicio del Milestone 16 para el avion "+avion.getId()+" ....");
						System.out.println("");
					
						
						tabla.getAvion(posicion).newStatus(10);
						tabla.getAvion(posicion).setMilestone(16);
						
						publicarinfo = true;
					}
				
				}
				if(avion.getNextMilestone() == false)
				{
					// Comprobamos si es necesario hacer el calculo de TSAT	
					int[] actuTSAT = {7};
					boolean tiempoTSAT = comprobarActualizadas(avion, actuTSAT);
					if(avion.getMilestone()>=14)
						tiempoTSAT=false;
					
					// Comprobamos si es necesario definir el Milestone 9 y 10
					int[] actuMilestone9y10 = {6};
					boolean defMilestone9y10 = comprobarActualizadas(avion, actuMilestone9y10);
					if(avion.getMilestone()>=10 || avion.getValorTiempos(2)==0) //Si es solo llegada no ponemos este tiempo
						defMilestone9y10=false;
								
					// Comprobamos si es necesario definir el Milestone 16 
					//Primero miraremos que el ETOT sea valido
					int[] actuMilestone16 = {5};
					boolean defMilestone16 = comprobarActualizadas(avion, actuMilestone16);
					if(avion.getMilestone()>=16)
						defMilestone16=false;
					
					// Miramos si el nuevo ELDT es valido
					int[] ComprovarELDT = {4};
					boolean mirarELDT = comprobarActualizadas(avion, ComprovarELDT);
					if(avion.getMilestone()>=6)
						mirarELDT=false;
					
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
					int[] act = {0,0,0,0,0,0}; //TSAT,ETOT,ELDT,INCIDENCIA,mil9,mil10
					
					if(tiempoTSAT)
					{
						int TSAT = Calculotiempo(avion.getValorTiempos(4), EOBTtoTSAT,0);
						
						if(avion.getValorTiemposSecundarios(4) < TSAT || (avion.getValorTiemposSecundarios(4)==0 && avion.getMilestone()==10))
						{
							act[0] = 18;
							
							if(avion.getValorTiemposSecundarios(4) != 0)
							{
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiemposSecundarios(4), TSAT, "TSAT", getAID().getLocalName(),h);
								act[3] = 19;
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiemposSecundarios(4, TSAT);
							
							publicarinfo = true;
							
							// Mensaje definir Milestone 14:
							String milestone14 = "14 "+TSAT;
							actuMilestone[0] = milestone14;
							milestoneActualizado = true;
							
							// System.out.println("Avion: "+avion.getId()+" TSAT: "+TSAT);
							
						}
						
					}
					if(defMilestone9y10)
					{
						int tiempoMilestone9 = Calculotiempo(avion.getValorTiempos(3), tiempoMil9,1);
						// Mensaje definir Milestone 9:
						String milestone9 = "9 "+tiempoMilestone9;
						actuMilestone[1] = milestone9;
						
						int tiempoMilestone10 = Calculotiempo(tiempoMilestone9, tiempoMil10,1);
						// Mensaje definir Milestone 10:
						String milestone10 = "10 "+tiempoMilestone10;
						actuMilestone[2] = milestone10;
						
						avion.setOpcionMensaje(2);
						
						act[4]=22;
						act[5]=23;
						avion.setActualizadas(act);
						milestoneActualizado = true;
	
					}

					if(defMilestone16)
					{
						int nuevoETOT = avion.getValorTiempos(2);
						int nuevoETOTcongestion = Pistaocupada(nuevoETOT,avion,false);
						if(nuevoETOT < nuevoETOTcongestion && nuevoETOT!=0)
						{
							int h=GetTempsActualMil(avion,avion.getMilestone());
							avion.añadirIncidencia(nuevoETOT, nuevoETOTcongestion, "ETOT", "ATC (por congestion de pista)",h);
							avion.setValorTiempos(2,nuevoETOTcongestion);
							publicarinfo = true;
							act[3] = 19;
							avion.setActualizadas(act);
							act[1] = 5;
							
							String milestone16 = "16 "+nuevoETOTcongestion;
							actuMilestone[3] = milestone16;
							
							milestoneActualizado = true;
							avion.setOpcionMensaje(2);
						}
					}
					if(mirarELDT)
					{
						int nuevoELDT = avion.getValorTiempos(1);
						int nuevoELDTcongestion = Pistaocupada(nuevoELDT,avion,true);
						if(nuevoELDT < nuevoELDTcongestion)
						{
							int h=GetTempsActualMil(avion,avion.getMilestone());
							avion.añadirIncidencia(nuevoELDT, nuevoELDTcongestion, "ELDT", "ATC (por congestion de pista)",h);
							avion.setValorTiempos(1,nuevoELDTcongestion);
							publicarinfo = true;
							act[3] = 19;
							avion.setActualizadas(act);
							act[2] = 4;
							
							String milestone6 = "6 "+nuevoELDTcongestion;
							actuMilestone[4] = milestone6;
							
							milestoneActualizado = true;
							avion.setOpcionMensaje(2);
						}
					}
					avion.setActualizadas(act);
					if(RetrasoDetectado)
					{
						AplicarRetras((RetrasoAvion[0]).split(" "),"0");
						//************************************
						//Poner el retraso que sea i si publicar info...!!
					}
					tabla.setAvion(avion, posicionn);
					if(avion.getValorTiempos(1)!=0)
					{
						AddSequence(avion.getId(),avion.getValorTiempos(1),avion.getCat(),true);
					}
					if(avion.getValorTiempos(2)!=0)
					{
						AddSequence(avion.getId(),avion.getValorTiempos(2),avion.getCat(),false);
					}
				}
			
			
			
				//////////// FASE Publicar Informacion ///////
			
				if ( publicarinfo || milestoneActualizado )
				{
					try {
						if(publicarinfo){
							PublicarInformacion(tabla.getAvion(posicion), Muestralo);
					
							publicarinfo = false;
						}
						if(milestoneActualizado){ // Publicar la informacion de nuevos milestones [Mensajes al MilestoneTrigger]
							mensajeMilestone(tabla.getAvion(posicion), actuMilestone,0);
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
			}
		}
		
	}
	
	public void AddSequence (String Id, int tiempo,int categoria, boolean landing)
	//Out(ArrayList LadingSequence, ArrayList TakeOffSequence) In(String Id, int tiempo,int categoria, boolean landing, ArrayList LadingSequence, ArrayList TakeOffSequence)
	//Añade a la secuencia el avión con Id  de entrada con su tiempo de salida o llegada. Si ese avión ya estaba dentro de la secuencia modifica su tiempo.
	{
		int i=1;
		if(landing)
		{
			int numero;
			boolean first = LandingSequence.isEmpty();
			if(first)
			{
				numero=0;
			}
			else
			{
				numero = LandingSequence.size();
			}
				
			int posicion=numero;
			while(i<=numero)
			{
				String infoavion = (String) LandingSequence.get(i-1);
				if(posicion==numero && Integer.parseInt((infoavion.split(" "))[1])>=tiempo)
					posicion=i-1;
				if(Id.equals((infoavion.split(" "))[0]))
				{
					LandingSequence.remove(i-1);
					if(posicion==numero)
					{
						posicion=posicion-1;
					}
					numero=numero-1;
				}
				else
					i++;
			}
			LandingSequence.add(posicion,Id + " " + String.valueOf(tiempo) + " " + String.valueOf(categoria));		
		}
		else
		{
			int numero;
			boolean first = TakeOffSequence.isEmpty();
			if(first)
			{
				numero=0;
			}
			else
			{
				numero = TakeOffSequence.size();
			}
					
			int posicion=numero;
			while(i<=numero)
			{
				String infoavion = (String) TakeOffSequence.get(i-1);
				if(posicion==numero && Integer.parseInt((infoavion.split(" "))[1])>=tiempo)
					posicion=i-1;
				if(Id.equals((infoavion.split(" "))[0]))
				{
					TakeOffSequence.remove(i-1);
					if(posicion==numero)
					{
						posicion=posicion-1;
					}
					numero=numero-1;
				}
				else
					i++;
			}
			TakeOffSequence.add(posicion,Id + " " + String.valueOf(tiempo) + " " + String.valueOf(categoria));			
		}
	}
	public int GetPositionSequence(String Id, boolean landing)
	//Out(int valor) In(String Id, boolean landing, ArrayList LandingSequence, ArrayList TakeOffSequence)
	//Le llega un Id de un avión y te devuelve un entero que indica en qué posición realiza el aterrizaje o el despegue.
	{
		int valor=-1;
			if(landing)
			{
				int i=0;
				while(i<LandingSequence.size())
				{
					if(Id.equals(((String) LandingSequence.get(i)).split(" ")[0]))
					{
						valor=i;
						break;
					}
					i++;
				}
			}
			else
			{
				int i=0;
				while(i<TakeOffSequence.size())
				{
					if(Id.equals(((String) TakeOffSequence.get(i)).split(" ")[0]))
					{
						valor=i;
						break;
					}
					i++;
				}
			}
		return valor;
	}
	public int GetTimeSequence (int position, boolean landing)
	//Out(int time) In(int position, boolean landing, ArrayList LandingSequence, ArrayList TakeOffSequence)
	//Devuelve el tiempo de despegue o aterrizaje del avion en la position según la secuencia de takeoff y de landing
	{
		int time=-1;
		if(landing)
		{
			if(position<LandingSequence.size())
				time=Integer.parseInt(((String) LandingSequence.get(position)).split(" ")[1]);
		}
		else
		{
			if(position<TakeOffSequence.size())
				time=Integer.parseInt(((String) TakeOffSequence.get(position)).split(" ")[1]);
		}
		return time;
	}
	public int GetCategoriaSequence ( int position, boolean landing)
	//Out(int cat) In(int position, boolean landing, ArrayList LandingSequence, ArrayList TakeOffSequence)
	//Devuelve la categoria del avión que despega o aterriza en la position según la secuencia de takeoff y de landing
	{
		int cat=-1;
		if(landing)
		{
			if(position<LandingSequence.size())
				cat=Integer.parseInt(((String) LandingSequence.get(position)).split(" ")[2]);
		}
		else
		{
			if(position<TakeOffSequence.size())
				cat=Integer.parseInt(((String) TakeOffSequence.get(position)).split(" ")[2]);
		}
		
		return cat;
	}
	public String GetIdSequence ( int position, boolean landing)
	//Out(String Id) In(int position, boolean landing, ArrayList LandingSequence, ArrayList TakeOffSequence)
	//Devuelve el Id del avión que despega o aterriza en la position según la secuencia de takeoff y de landing
	{
		String Id="";
		if(landing)
		{
			if(position<LandingSequence.size())
				Id=((String) LandingSequence.get(position)).split(" ")[0];
		}
		else
		{
			if(position<TakeOffSequence.size())
				Id=((String) TakeOffSequence.get(position)).split(" ")[0];
		}
		
		return Id;
	}
	
		
	public int Pistaocupada(int nuevoT, Aircraft avion,boolean landing ) //L'aprofitem per ELDT i ETOT
	//Out(int nuevoT) In(int nuevoT, Aircraft avion, boolean landing, ArrayList LandingSequence, ArrayList TakeOffSequence, int politicacongestio)
	//Mira si para un tiempo ELDT o ETOT la pista está ocupada, en caso afirmativo calcula el nuevo ETOT o ELDT del vuelo, o aplica retrasos a los
	//aviones que sea necesario en función de la políticacongestio
	{
		AddSequence (avion.getId(), nuevoT,avion.getCat(), landing);
		int posicio = GetPositionSequence(avion.getId(),landing);
		int num;
		int arr;
		if(landing)
		{
			num=LandingSequence.size();
			arr=0;
		}
		else
		{
			num = TakeOffSequence.size();
			arr=1;
		}
		int i=1;
		if(posicio!=0)
			i=posicio-1;
		
		while(i<num)
		{
			if(i!=posicio)//no comparem amb la seva propia hora, no te sentit
			{
				int Tvols=GetTimeSequence (i,landing);
				int diferenciatemps = restatemps(Tvols, nuevoT);
				int SeparationTimeDeparture = GetSeparationTime(GetCategoriaSequence(1,landing),avion.getCat(),diferenciatemps,landing);
				int SeparationTimeReduccioPista = GetSeparation(nuevoT,arr);
				if(SeparationTimeReduccioPista>SeparationTimeDeparture)
				{
					SeparationTimeDeparture=SeparationTimeReduccioPista;
				}
				
				if(diferenciatemps<SeparationTimeDeparture && diferenciatemps>(SeparationTimeDeparture*-1))
				{
					if(politicaCongestio==1)
					{
						if(diferenciatemps<=0)
							nuevoT= sumatemps(Tvols, SeparationTimeDeparture);
						else
							if(GetSeparationTime(tabla.getAvion(i).getCat(),avion.getCat(),0,landing)>SeparationTimeDeparture) //Mirem si tenim reducció de capacitat de pista
								nuevoT= sumatemps(Tvols, GetSeparationTime(tabla.getAvion(i).getCat(),avion.getCat(),0,landing));
							else
								nuevoT= sumatemps(Tvols, SeparationTimeDeparture);
						
					}
					if(politicaCongestio==2)
					{
						if(diferenciatemps<=0)
						{
							nuevoT= sumatemps(Tvols, SeparationTimeDeparture);
						}
						else
						{
							//Cas de que tinguem de moure l'altre avio!
							int nuevoTotro = sumatemps(nuevoT,SeparationTimeDeparture);
							String id = GetIdSequence(i, landing);
							int posicioavio=tabla.GetPositionAvion(id);
							Aircraft avionotro = tabla.getAvion(posicioavio);
							String[] ActuMil= new String[1];
							if(landing)
							{
								int antiguoT = avionotro.getValorTiempos(1);
								avionotro.setValorTiempos(1, nuevoTotro);
								int[] act = {4,19};
								avionotro.setActualizadas(act);
								int h=GetTempsActualMil(avionotro,avionotro.getMilestone());
								avionotro.añadirIncidencia(antiguoT, nuevoTotro, "ELDT", "ATC (por congestion de pista)",h);
								ActuMil[0]="4 "+ nuevoTotro;
								}
							else
							{
								int antiguoT = avionotro.getValorTiempos(2);
								avionotro.setValorTiempos(2, nuevoTotro);
								int[] act = {5,19};
								avionotro.setActualizadas(act);
								avionotro.setIncidencias("");
								int h=GetTempsActualMil(avionotro,avionotro.getMilestone());
								avionotro.añadirIncidencia(antiguoT, nuevoTotro, "ETOT", "ATC (por congestion de pista)",h);
								ActuMil[0]="16 "+ nuevoTotro;
							}
							int[] posicionn = {posicioavio,0};
							tabla.setAvion(avionotro, posicionn);
							try {
								PublicarInformacion(tabla.getAvion(posicioavio), Muestralo);
								mensajeMilestone(tabla.getAvion(posicioavio), ActuMil,0);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				else
				{
					if(i>posicio)
						break;
				}
			}
			i++;
		}
		
		return nuevoT;
	}
	
	public boolean done()
    {
        return fin;
    }
	
}
	public void PonerRetraso(String Retraso)
	//Out(String[]Retrasotiempo, String[]Retrasoavion, String[]Retrasopista) In(String Retraso)
	//Le llega un retraso y lo guarda en la lista Retrasotiempo(tipo 1), Retrasoavion (tipo 2 y 3)o Retrasopista (tipo 4)
		
	{
		String[] palabras=Retraso.split(" ");
		String comentario="";
		if(Integer.parseInt(palabras[0])==1) //Retraso tipo 1
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
		if(Integer.parseInt(palabras[0])==2)//Retraso tipo 2
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
		if(Integer.parseInt(palabras[0])==3) //Retraso tipo 3
		{
			int i=6;
			while(i<palabras.length)
			{
				comentario=comentario + " " +palabras[i];
				i++;
			}
			Retrasoavion.add(palabras[1]+" "+palabras[2]+" "+palabras[3]+" "+palabras[4]+comentario);
		}
		if (Integer.parseInt(palabras[0])==4) //Retraso tipo 4
		{
			int i=2;
			while(i<palabras.length)
			{
				comentario=comentario + " " +palabras[i];
				i++;
			}
			Retrasopista.add(palabras[1]+comentario);
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
	public boolean[] AddSeparationTime (String reduccio) //devolvera 00 01 10 o 11. take/land  el 0 indica que no se tiene de aplicar reduccion de la capacidad de pista, el 1 que si
	//Out(boolean[] aplicar, SeparationsTimeReducted) In(String reduccio)
	//Añade la información sobre la separación de los aviones debido a una reducción de la capacidad de pista.
	{
		boolean[] aplicar=new boolean[2];
		String[] paraules = reduccio.split(" ");
		int capacitatland=Integer.parseInt(paraules[2]); 
		double capacitat_land=capacitatland;//necesito tots els valors amb double, per poder trobar els minuts de separacio
		double minutsprevisland = (60/capacitat_land);  // 
		int minutsland = (int) Math.ceil(minutsprevisland);
		if(minutsland<=2)
			aplicar[1]=false;
		else
			aplicar[1]=true;
		
		int capacitattake=Integer.parseInt(paraules[3]);
		double capacitat_take=capacitattake;//necesito tots els valors amb double, per poder trobar els minuts de separacio
		double minutsprevistake = (60/capacitat_take); 
		int minutstake = (int) Math.ceil(minutsprevistake);
		if(minutstake<=2)
			aplicar[0]=false;
		else
			aplicar[0]=true;
		String frase=paraules[0]+" "+paraules[1]+" "+minutsland+" "+minutstake;
		SeparationsTimeReducted.add(frase);
		return aplicar;
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
		
		int TempsActual = TreureTemps(avion,Informacio[2]);
		if (TempsActual!=0)
		{
			int[] Actualizada = TreureActualizada(Informacio[2]);
			int TempsNou= sumatemps(TempsActual,Integer.parseInt(Informacio[1]));
			//Posem indicació de quin temps s'actualiza
			avion.setActualizadas(Actualizada);
			avion.setOpcionMensaje(2);
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
	public void PosarSeparacionsReduccioCapacitat (String reduccio, int arribada, String HoraActual)
	//Out() In(String reduccio, int arribada, String HoraActual)
	//Assegurar que todos los aviones de llegada o salida mantengan las distancias de seguridad necesarias teniendo en cuenta las capacidades reducidas
	{
		String[] paraules = reduccio.split(" ");
		int p=4;
		String comentari="";
		while(p<paraules.length)
		{
			comentari = comentari+" "+ paraules[p];
			p++;
		}
		int Hinici=Integer.parseInt(paraules[0]);
		int Hfinal= Integer.parseInt(paraules[1]);
		ArrayList NuevosTiempos = new ArrayList(); //3 valors / Primer 1 o 0 segons si es arribada o sortida / Matricula / temps antic
		ArrayList Ids = new ArrayList(); //Per saber respecte quins avions s'han mirat els retrasos
		ArrayList Idsretrasados = new ArrayList(); //Per saber quins avions han patit algun retras
		int i;
		int k=0;
		int tempsminim=9999;
		String Idminim="";
		int pos=2;
		String variable ="ETOT";
		int postablaminim=-1;
		int[] actualizadas =  {5,19};
		boolean encontrado = false; //Si el retard deixa de propagar-se no fa falta seguir mirant
		boolean landing =false;
		if(arribada==1)
		{
			actualizadas[0] = 4;
			pos=1;
			variable = "ELDT";
			landing =true;
		}
		
		
		//Buscamos el primer avion en actuar
		//Asseguramos que mantenga la nueva separación mínima con los otros aviones
		//Miramos(una vez aplicados los nuevos retrasos) qual es el segundo avion en actuar
		//Asseguramos que mantenga la nueva separación mínima con los otros aviones
		//asi hasta el final
		//Una vez el problema con la capacidad de pista se ha solucionado
		//Seguimos realizando las mismas comprovaciones, pero ahora el tiempo entre aviones puede ser menor
		while(k<tabla.getnumero())
		{
			if(encontrado==true)
				break;
			i=0;
			tempsminim=9999;
			Idminim="";
			postablaminim=-1;
			while(i<tabla.getnumero()) //Busquem el seguent avio amb menys temps de arribada o sortida
			{
				try
				{
					if(Ids.contains(tabla.getAvion(i).getId())==false)
					{
						if (tempsminim>tabla.getAvion(i).getValorTiempos(pos) && tabla.getAvion(i).getValorTiempos(pos)!=0)
						{
							tempsminim=tabla.getAvion(i).getValorTiempos(pos);
							Idminim=tabla.getAvion(i).getId();
							postablaminim=i;
						}
						else if (tempsminim==tabla.getAvion(i).getValorTiempos(pos))
						{
							String IDnoucandidat=tabla.getAvion(i).getId();
							int tempsminimnoucandidat=tempsminim;
							int tempsminimanticcandidat=tempsminim;
							boolean find1=false;
							boolean find2=false;
							int q=0;
							while(q< Idsretrasados.size() && (find1==false || find2 == false))
							{
								if(IDnoucandidat.equals(Idsretrasados.get(q)) && find1==false)
								{
									String phrase = (String) NuevosTiempos.get(k);
									String[] words = phrase.split(" ");
									tempsminimnoucandidat=Integer.parseInt(words[2]);
									find1=true;
								}
								if(Idminim.equals(Idsretrasados.get(q)) && find2==false)
								{
									String phrase = (String) NuevosTiempos.get(k);
									String[] words = phrase.split(" ");
									tempsminimanticcandidat=Integer.parseInt(words[2]);
									find2=true;
								}
								q++;
							}
							if(tempsminimnoucandidat<tempsminimanticcandidat)
							{
								tempsminim=tabla.getAvion(i).getValorTiempos(pos);
								Idminim=tabla.getAvion(i).getId();
								postablaminim=i;
							}
						}
					}
				}
				catch (Exception SourcenotfoundException)
				{
					
				}
				i++;
			}
			if(Idminim=="")
				break;
			Ids.add(Idminim);
			i=0;
			//Ara anem a corretgir tots els avions respecte aquest
			if(tempsminim>Hinici && tempsminim!=9999)
			{
				int minutsseparacio = GetSeparation(tempsminim, arribada );
				if (tempsminim > Hfinal)
				{
					encontrado=true;
				}
				while(i<tabla.getnumero())
				{
					int realseparation=minutsseparacio;
					if(minutsseparacio==3)
					{
						int realseparation_=GetSeparationTime(tabla.getAvion(postablaminim).getCat(),tabla.getAvion(i).getCat(),-1,landing);  //El -1 es per indicar que el postablaminim aterra primer
						if(realseparation_>realseparation)
							realseparation=realseparation_;
					}
					if (minutsseparacio==2)
					{
						realseparation=GetSeparationTime(tabla.getAvion(postablaminim).getCat(),tabla.getAvion(i).getCat(),-1,landing);  //El -1 es per indicar que el postablaminim aterra primer
					}
					
					
						
					if(Math.abs(restatemps(tempsminim,tabla.getAvion(i).getValorTiempos(pos)))<realseparation && Idminim.equals(tabla.getAvion(i).getId())==false)
					{
						int noutemps=sumatemps(tempsminim,realseparation);
						if(Idsretrasados.contains(tabla.getAvion(i).getId())==false)
						{
							Idsretrasados.add(tabla.getAvion(i).getId());
							NuevosTiempos.add(arribada+" "+tabla.getAvion(i).getId()+" "+tabla.getAvion(i).getValorTiempos(pos)+ " "+ Idminim + " " + tempsminim);
						}
						tabla.getAvion(i).setValorTiempos(pos, noutemps);
						encontrado=false;
					}
					i++;
	
				}
			}
			k++;	
		}
		
		//Ara enviem la informacio dels avions que han estat modificats
		
		i=0;
		while(i<tabla.getnumero() && Idsretrasados.isEmpty()==false)
		{
			int tiempoAntiguo;
			String id = tabla.getAvion(i).getId();
			if (Idsretrasados.contains(id))
			{
				k=0;
				encontrado=false;
				while(k<Idsretrasados.size() && encontrado==false)
				{
					String frase = (String) NuevosTiempos.get(k);
					String[] palabras = frase.split(" ");
					String ID =(String) Idsretrasados.get(k);
					if(ID.equals(tabla.getAvion(i).getId()))
					{
						tiempoAntiguo=Integer.parseInt(palabras[2]);
						encontrado=true;
						Idsretrasados.remove(k);
						NuevosTiempos.remove(k);
						Aircraft avion = tabla.getAvion(i);
						avion.setOpcionMensaje(2);
						avion.setActualizadas(actualizadas);
						avion.añadirIncidencia(tiempoAntiguo, avion.getValorTiempos(pos), variable, "ATC ("+comentari+" culpa de"+palabras[3] + " " + palabras[4]+" )", Integer.parseInt(HoraActual));
						try {
							PublicarInformacion(avion,0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					k++;
				}
				
			}
			i++;
		}
			
		
		
	}
	public int GetSeparationTime (int A, int B, int Diferenciatemps, boolean Landing)
	//Out(int Separation) In(int A, int B, int Diferenciatemps, boolean  Landing, ArrayList SeparationTimein, ArrayList SeparationTimeout)
	//Busca la separación mínima necesaria entre 2 aviones
	{
		if(A==0)
			A=4;
		if(B==0)
			B=4;
		int Separation;
		if(Landing)
		{
			if(Diferenciatemps>0) //Si es positiu el B aterra primer
			{
				Separation=SeparationTimein[(B-1)*4+(A-1)];
			}
			else{ //El A aterra primer
				Separation=SeparationTimein[(A-1)*4+(B-1)];
			}
		}
		else
		{
			if(Diferenciatemps>0) //Si es positiu el B aterra primer
			{
				Separation=SeparationTimeout[(B-1)*4+(A-1)];
			}
			else{ //El A aterra primer
				Separation=SeparationTimeout[(A-1)*4+(B-1)];
			}
	}
		return Separation;
	}
	public int GetSeparation (int Hora, int arr)
	//Out (int minutos) In(int Hora, int arr, ArrayList SeparationsTimeReducted)
	//Mira si a la hora tenemos una reducción de la capacidad de la pista, en caso afirmativo devuelve el mínimo tiempo necesario entre 2 aviones
	{
		int minutos=2;
		int i=0;
		while(i<SeparationsTimeReducted.size())
		{
			String[] paraules = ((String) SeparationsTimeReducted.get(i)).split(" ");
			if(Hora>=Integer.parseInt(paraules[0]) && Hora<= Integer.parseInt(paraules[1]))
				{
				if(arr==1 && minutos<Integer.parseInt(paraules[2]))
					minutos=Integer.parseInt(paraules[3]);
				else if(minutos<Integer.parseInt(paraules[3]))
					minutos=Integer.parseInt(paraules[2]);
				}
			i++;
		}
		return minutos;
	}
	
	public void ActualizarSeparationTimes()
	//Out(int[]SeparationTimein, int[]SeparationTimeout) In(int ophINnominal, int ophOUTnominal)
	//Varia los valores de separación entre aviones si el documento airport.txt marca unos valores de operaciones por hora inferior al nominal(30)
	{
		if(ophINnominal<30)
		{

			double ophIN_nominal=ophINnominal;//necesito tots els valors amb double, per poder trobar els minuts de separacio
			double minutsprevisland = (60/ophIN_nominal);  // 
			int minutsland = (int) Math.ceil(minutsprevisland);
			int i=0;
			while(i<SeparationTimein.length)
			{
				if(SeparationTimein[i]<minutsland)
					SeparationTimein[i]=minutsland;
				i++;
			}
		}
		if(ophOUTnominal<30)
		{
			double ophOUT_nominal=ophOUTnominal;//necesito tots els valors amb double, per poder trobar els minuts de separacio
			double minutsprevistake = (60/ophOUT_nominal);  // 
			int minutstake = (int) Math.ceil(minutsprevistake);
			int i=0;
			while(i<SeparationTimeout.length)
			{
				if(SeparationTimeout[i]<minutstake)
					SeparationTimeout[i]=minutstake;
				i++;
			}
		}
	}
}

