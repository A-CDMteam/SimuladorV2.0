package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

public class TurnAround extends CDM {
	
	int avionesSimulacion;
	boolean leido=false;
	String ruta = "";
	
	ArrayList Modelos = new ArrayList();
	ArrayList Tiempos = new ArrayList();
	ArrayList Aerolineas = new ArrayList();
	
	int GHamount=0;
	String AOforGH[]={""};
	
	private AID Receptor;

	protected void setup() {
		String pepe="mkjd";
		
		System.out.println(getAID().getLocalName() + " operativo.");
		
		addBehaviour(new BuscarTurnAround());
	}

	class BuscarTurnAround extends Behaviour {

		private boolean fin = false;

		int Muestralo = 0;

		public void action() {

			ACLMessage mensaje = blockingReceive();
			int TurnAroundTime;

			boolean milestoneActualizado = false;
			
			if (mensaje != null) 
			{
				
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
				if(avion.getOpcionMensaje() == 3) //Mensaje de inicialización del programa
				{
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					if(avion.getAO().equals("")==false)
					{
						GHamount = avion.getAO().split("\t").length;	
						AOforGH=avion.getAO().split("\t");
					}
					
					ruta = valorparametro(parametros[3])  + "Turn-Around-Time.txt";
					try {
						LlegirText();//llegira la ruta, si ho fa correctament leido =true, sino seguira fals.
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					if(avion.getModelo().equals("") || avion.getAO().equals(""))
					{
						TurnAroundTime=0;
					}
					else
					{
						String Model=avion.getModelo();
						String AO=avion.getAO();
						int cat = avion.getCat();
						TurnAroundTime=GetTurnAroundTime(Model,cat,AO);
						Receptor = GetReceptor(AO);
					}
					MandarInfoGH(avion,TurnAroundTime);
				}
				
			}
		}


		public boolean done() {
			return fin;
		}
	}
	

	public void LlegirText() throws IOException 
	//In (String ruta), Out (boolean leido, ArrayList Modelos, ArrayList Tiempos)
	//Leemos el documento de Turn-Around-Tiempos.txt y guardamos los modelos en la ArrayList Modelos,
	//y los tiempos en la ArrayList tiempos. leido indica si se ha leido correctamente el documento
	{
		File archivoresults = new File(ruta);
		
		FileReader fresults=null;
		BufferedReader brresults = null;
		try{
			fresults=new FileReader(archivoresults);
			brresults = new BufferedReader(fresults);
			
			String linea; //Obtenemos el contenido del archivo linea por linea
	    	while( ( linea = brresults.readLine() ) != null )
	    	{
	    		 		String[] palabras=linea.split("\t");
	    		 		
	    		 		Tiempos.add(palabras[1]);
	    		 		if (palabras.length==3) //Si el turn araund es para un modelo y aerolinea guardamos los dos datos en Modelos
	    		 		{
	    		 			Modelos.add(palabras[0]+" "+palabras[2]);
	    		 		}
	    		 		else
	    		 			Modelos.add(palabras[0]);
	    	}
			leido=true;
		}
	    catch(Exception FileNotFoundException)
	    {
	    	System.out.println("No encuentro el archivo de resultados");
	    }
	}
	
	public int GetTurnAroundTime(String Modelo, int cat, String AO)
	//In(String Modelo, int cat, boolean leido) Out (int TurnAroundTime)
	//organiza el proceso para obtener el turn around time del avión solicitado en cada caso.
	{
		int TurnAroundTime=0;
		if(leido)
		{
			int[] info=BuscarTAT(Modelo,AO);
			if(info[1]==0)
				TurnAroundTime=info[0];
			else
				TurnAroundTime=TATcasgeneral(info, cat);
		}
		else
		{
			int[] info = {0, 2};
			TurnAroundTime=TATcasgeneral(info, cat);
		}
		if(TurnAroundTime<35)
			TurnAroundTime=35;
		
		
		return TurnAroundTime;
		
		
	}
	public int[] BuscarTAT(String Modelo,String AO)
	//In(String Modelo, ArrayList Modelos, ArrayList Tiempos) Out (int[2] info)
	//Busca el TAT para e la lista para el modelo de entrada. Si el modelo no aparece en la lista, 
	//mira si aparece en la ArrayList el caso GEN
	{
		int[] info=new int[2]; //[temps,val] val=0 Model existent a la taula. val=1 Model del cas GEN. val=2 Informacio no trobada
		if(Modelos.contains(Modelo+" "+AO))
		{
			int index = Modelos.indexOf(Modelo+" "+AO);
			String valor = (String)Tiempos.get(index);
			info[0] = Integer.parseInt(valor) ;
			info[1] = 0;
		}
		else if(Modelos.contains(Modelo))
		{
			int index = Modelos.indexOf(Modelo);
			String valor = (String)Tiempos.get(index);
			info[0] = Integer.parseInt(valor) ;
			info[1] = 0;
		}
		else if(Modelos.contains("GEN"))
		{
			int index = Modelos.indexOf("GEN");
			String valor = (String)Tiempos.get(index);
			info[0] = Integer.parseInt(valor) ;
			info[1] = 1;
		}
		else
		{
			info[0] = 0;
			info[1] = 2;
		}
		
		return info;
	}
	public int TATcasgeneral(int[] info, int cat)
	//In (int[2] info int cat) Out(int TurnAroundTime)
	//Añade una constante de tiempo al TurnAroundTime en
	//función de la categoría del avión. Solo si el documento Turn-Around-Times.txt 
	//no contiene información de nuestro modelo en concreto. 
	{
		int TurnAroundTime=35;
		if(info[1]==1)
			TurnAroundTime=info[0];
		if(cat==1)
			TurnAroundTime=TurnAroundTime+15;
		if(cat==2)
			TurnAroundTime=TurnAroundTime+10;
		if(cat==3)
			TurnAroundTime=TurnAroundTime+5;
		if(cat==4 || cat==0)
			TurnAroundTime=TurnAroundTime;
		
		return TurnAroundTime;
	}
	
	public AID GetReceptor (String AO)
	//In(String AO, int GHamount, String AOforGH) Out (AID GHAID)
	//Sabiendo los contratos de las empresas de GH y la aerolínea del avión que estamos estudiando,
	//buscará cual es la empresa a la que tiene de mandar la información con el turn around time.
	{
		AID GHAID = new AID();
		
		boolean encontrado=false;
		int i=0;
		while(i<GHamount && encontrado==false)
		{
			String paraules[]=AOforGH[i].split("-");
			int j=1;
			while(j<paraules.length && encontrado==false)
			{
				if(AO.equals(paraules[j]))
				{
					GHAID = new AID ("GH-"+paraules[0], AID.ISLOCALNAME);
					encontrado=true;
				}
				j++;
			}
			
			i++;
		}
		if(encontrado==false)
			GHAID = new AID ("GH-GEN", AID.ISLOCALNAME);
		
		return GHAID;
		
	}
	
	public void MandarInfoGH(Aircraft avion, int TurnAroundTime)
	//In (Aircraft avion, int TurnAroundTime, AID Receptor) Out ()
	//Compila el mensaje con los json y lo manda al GH.
	{
		avion.setIncidencias(TurnAroundTime+"");
		avion.setOpcionMensaje(8);
		
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.addReceiver(Receptor);
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
					
		// Enviamos el avion
		msg.setContent(jsonOutput);
					
		send(msg);
		
	}
	
}
