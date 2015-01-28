package Agentes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class Airport extends CDM {

	int TaxiInTime = 15;
	int TaxiOutTime = 20;
	int avionesSimulacion;
	Tabla tabla;
	boolean publicarinfo;
	int[] actuAirport = new int[4];
	
	ArrayList Retrasoavion=new ArrayList();
	ArrayList Retrasotiempo=new ArrayList();
	int TSATtoEOBT = 5; //Tiempo entre TSAT y EOBT
		
		
	protected void setup()
	    {	
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
        int muestraIncidencia = 0;

		public void action(){
			
			ACLMessage mensaje = blockingReceive();
			
			boolean milestoneActualizado = false;
			String[] actuMilestone = {" ", " ", " "}; // Actualizaciones de milestone, vector con todas las que se calculan en este agente
			
			if (mensaje != null)
			{
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
				if(avion.getOpcionMensaje() == 3){
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					TaxiInTime = Integer.parseInt(valorparametro(parametros[0]));
					TaxiOutTime = Integer.parseInt(valorparametro(parametros[1]));
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
					
					
				}
				else if(avion.getOpcionMensaje()==4 ||avion.getOpcionMensaje()==5 || avion.getOpcionMensaje()==7)
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
				
					// Comprobamos si hay que hacer el calculo de EOBT
					int[] opcionEOBT = {5}; //Se actua si se ha actualizado el ETOT;
					int calcularEOBT = Actuamos(avion, opcionEOBT);
					if(avion.getMilestone()>=15)
						calcularEOBT=0;
				
					// Comprobamos si hay que haver el calculo de EOTB por actualizacion de TSAT
					int[] opcionEOBT2 = {18}; //Se actua si se ha actualizado el ETOT;
					int calcularEOBT2 = Actuamos(avion, opcionEOBT2);
					if(avion.getMilestone()>=15)
						calcularEOBT2=0;
					
					// Comprobamos si hay que hacer el calculo de EIBT
					int [] opcionEIBT = {4};
					int calcularEIBT = Actuamos(avion, opcionEIBT);
					if(avion.getMilestone()>=7)
						calcularEIBT=0;
				
				
					//Comprobamos si se ha actualizado el EOBT (para retrasar el ETOT)
					int [] opcionETOT = {7};
					int calcularETOT = Actuamos(avion, opcionETOT);
					if(avion.getMilestone()>=16)
						calcularETOT=0;
					
					//Miramos si tenemos de aplicar algun retraso a un avion en concreto
					boolean RetrasoDetectado=false;
					String[] RetrasoAvion=null;
					if(Retrasoavion.isEmpty()==false && avion.getNextMilestone()==false)
					{
						RetrasoAvion=AlgunRestraso(avion.getId(),avion.getMilestone(),Retrasoavion);
						if(RetrasoAvion[0].equals("Ninguno")==false)
						{
							Retrasoavion.remove(Integer.parseInt(RetrasoAvion[1]));
							RetrasoDetectado=true;
						}
					}
				
					// Variable para actualizar el vector "actualizadas" del avion.
					actuAirport[0] = 0; //EIBT
					actuAirport[1] = 0;	//EOBT
					actuAirport[2] = 0; //ETOT
					actuAirport[3] = 0; //Incidencia
				
					// Realizamos los calculos necesarios
				
					if (calcularEIBT == 1)
					{
						avion = CalculoEIBT(avion);
					
						if(avion.getBoleano()){
							
							publicarinfo=true;
							// Mensaje definir Milestone 7:
							String milestone7 = "7 "+ avion.getValorTiempos(3);
							actuMilestone[0] = milestone7;
							milestoneActualizado = true;
							avion.setOpcionMensaje(2);
						}
					
					}
					if (calcularEOBT == 1) //ETOT->EOBT
					{
						int EOBT = Calculotiempo(avion.getValorTiempos(2), TaxiOutTime,0);
						
						if(avion.getValorTiempos(4) < EOBT || (avion.getValorTiempos(4)==0 && avion.getMilestone()==10))
						{
							if(avion.getValorTiempos(4) != 0)
							{
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiempos(4), EOBT, "EOBT", "Airport",h);
								actuAirport[3] = 19;
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiempos(4, EOBT);
						
							actuAirport[1] = 7;
							publicarinfo= true;
						
							// Mensaje definir Milestone 15:
							String milestone15 = "15 "+EOBT;
							actuMilestone[1] = milestone15;
							milestoneActualizado = true;
						}								
					}
					if (calcularEOBT2 == 1) //TSAT -> EOBT
					{
						int EOBT = Calculotiempo(avion.getValorTiemposSecundarios(4), TSATtoEOBT,1);
						
						if(avion.getValorTiempos(4) < EOBT)
						{
							if(avion.getValorTiempos(4) != 0)
							{
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiempos(4), EOBT, "EOBT", "Airport",h);
								actuAirport[3] = 19;
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiempos(4, EOBT);
						
							actuAirport[1] = 7;
							publicarinfo= true;
						
							// Mensaje definir Milestone 15:
							String milestone15 = "15 "+EOBT;
							actuMilestone[1] = milestone15;
							milestoneActualizado = true;
						}							
								
					}
					if(calcularETOT == 1)
					{
						int ETOT = Calculotiempo(avion.getValorTiempos(4), TaxiOutTime,1);
						
						if(avion.getValorTiempos(2) < ETOT)
						{
							if(avion.getValorTiempos(2) != 0)
							{
								int h=GetTempsActualMil(avion,avion.getMilestone());
								avion.añadirIncidencia(avion.getValorTiempos(2), ETOT, "ETOT", "Airport",h);
								actuAirport[3] = 19;
								
								if(muestraIncidencia == 1){
									System.out.println("El avion "+avion.getId()+" ha retrasado su hora de despegue a "+ETOT);
								}
								avion.setOpcionMensaje(2);
							}
							avion.setValorTiempos(2, ETOT);
						
							actuAirport[2] = 5;
							publicarinfo= true;
						
							// Mensaje definir Milestone 15:
							String milestone16 = "16 "+ETOT;
							actuMilestone[2] = milestone16;
							milestoneActualizado = true;
						}			
					
											

					}
				
					if(RetrasoDetectado)
					{
						AplicarRetras((RetrasoAvion[0]).split(" "),"0");
						//************************************
						//Poner el retraso que sea!!
					}
					// Actualizamos el vector de actualizadas
				
				
				
					avion.setActualizadas(actuAirport);
					avion.setUltimoAgente(getAID().getLocalName());
				
					tabla.setAvion(avion, posicionn);
				
					//////////// FASE Publicar Informacion ///////
				
					if ( publicarinfo )
					{
						try 
						{
							PublicarInformacion(tabla.getAvion(posicion), Muestralo);
						
							publicarinfo = false;
						
							if(milestoneActualizado){ // Publicar la informacion de nuevos milestones [Mensajes al MilestoneTrigger]
								mensajeMilestone(tabla.getAvion(posicion), actuMilestone,0);
							}
					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}	
				
//					// Mostramos en pantalla que todo funciona
//					int EOBT = tabla.getAvion(posicion).getValorTiempos(4);
//					int EIBT = tabla.getAvion(posicion).getValorTiempos(3);
//				
//					System.out.println("Avion " +tabla.getAvion(posicion).getId()+ ". Se han calculado los tiempos EIBT = "+EIBT+" y EOBT = "+EOBT+" .");
				}
			}
		}
		public boolean done()
        {
            return fin;
        }
		
	}

	public Aircraft CalculoEIBT(Aircraft a)
	//Out(Aircraft a) In(Aircraft a, int TaxiInTime)
	//Calcula el EIBT de un avión en función de su ELDT y del tiempo de taxi- in
	{
		
		boolean bool = false;
		
		// Separamos el tiempo en horas y minutos
		int h = a.getTiempos()[1]/100;
		int min = a.getTiempos()[1]%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		
		// Calculamos el tiempo de EIBT
		if((60-min) <= this.TaxiInTime){
			h = h+1;
			min = min + this.TaxiInTime - 60;
		}
		else{
			min = min + this.TaxiInTime;
		}
		
		int EIBT = min + (h*100);
		int EIBTantiguo = a.getValorTiempos(3); 
		
		if( EIBTantiguo < EIBT)
		{
			if(EIBTantiguo !=0)
			{
				int t=GetTempsActualMil(a,a.getMilestone());
				a.añadirIncidencia(EIBTantiguo, EIBT, "EIBT", "Airport",t);
				actuAirport[3] = 19;
				
			}
			a.setValorTiempos(3, EIBT);// Añadimos el EIBT al avion
			actuAirport[0]=6;
			bool = true;
		}
		a.setBoleano(bool);
		
		return a;
		
	}
	
	public void PonerRetraso(String Retraso)
	{
		//Out(String[]Retrasotiempo String[]Retrasoavion) In(String Retraso)
		//Le llega un retraso y lo guarda en la lista Retrasotiempo(tipo 1) o Retrasoavion (tipo 2 y 3)
				
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
	{
		//Out(String LlistaRetrasos, ArrayList Retrasotiempo) In(String posicio, int encontrados, ArrayList Retrasotiempo)
		//Encuentra todos los aviones afectados por un retraso de tipo 1, y escribe los retrasos como tipo 3
		String LlistaRetrasos="";
		int i=0;
		String[] RetrasoAplicado=((String) Retrasotiempo.get(Integer.parseInt(posicio)-encontrados)).split(" ");
		Retrasotiempo.remove(Integer.parseInt(posicio)-encontrados);
		int TempsPrevi=GetTempsPreviMil(Integer.parseInt(RetrasoAplicado[5])); //no es un valor de temps, indica quin es el temps. EX: TFIR, TAPP...
		int TempsPost=GetTempsPostMil(Integer.parseInt(RetrasoAplicado[5]));  //no es un valor de temps, indica quin es el temps. EX: TFIR, TAPP...
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
				int k=8;
				String Comment="";
				while(k<RetrasoAplicado.length)
				{
					Comment=Comment + " "+RetrasoAplicado[k];
					k=k+1;
				}
				boolean encontradoAO=false;
				boolean encontradomodelo=false;
				if(RetrasoAplicado[6].equals("-"))
					encontradoAO=true;
				else
				{
					String[] AOs=RetrasoAplicado[6].split("-");
					int h=0;
					while(h<AOs.length && encontradoAO==false)
					{
						if(AOs[h].equals(avion.getAO()))
							encontradoAO=true;
						h++;
					}
					
				}

				
				if(RetrasoAplicado[7].equals("-"))
					encontradomodelo=true;
				else
				{
					String[] Modelos=RetrasoAplicado[7].split("-");
					int h=0;
					while(h<Modelos.length && encontradomodelo==false)
					{
						if(Modelos[h].equals(avion.getModelo()))
							encontradomodelo=true;
						h++;
					}
					
				}
				if(encontradoAO && encontradomodelo)
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
		if(TempsActual!=0)  //No posem retards  a temps no definits
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
