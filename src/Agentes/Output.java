package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

public class Output extends CDM {
	//Per treure els resultats finals en txt
	Tabla tabla;	
	Tabla tablaDoc;
	boolean publicarinfo;
	int muestraIncidencia = 0;
	int avionesSimulacion;
	InterfazOutput inter;
	boolean primeravez = true;
	String ruta = "";//"C:/Users/Ivan/workspace/SimuladorV1.4/Results.txt";
	
	private AID Receptor = new AID("InfoSharing", AID.ISLOCALNAME);

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void setup() {
		System.out.println(getAID().getLocalName() + " operativo.");
		
		publicarinfo = false;
		
		addBehaviour(new AvionEnTabla());
	}

	class AvionEnTabla extends Behaviour {

		private boolean fin = false;

		int Muestralo = 0;

		public void action() {

			ACLMessage mensaje = blockingReceive();

			boolean milestoneActualizado = false;
			
			if (mensaje != null) 
			{
				
				Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
				if(avion.getOpcionMensaje()==9)
				{
					ruta = avion.getIncidencias();
					File archivo = new File(ruta);
					
					FileReader fr = null;
				    BufferedReader br = null;
				    
				    try
				    {
						fr = new FileReader( archivo );
						br = new BufferedReader( fr );
						String linea = br.readLine();
					    int num=0;
					    while (linea != null) //Recorrem el fitxer per saber quants avions tenim
					    {
					    	num++;
					    	linea = br.readLine();
					    }
					    br.close();
					    
					    fr = new FileReader( archivo );
						br = new BufferedReader( fr );
						
						tablaDoc = new Tabla(num);
						linea = br.readLine();
						while (linea != null) //Recorrem el fitxer per saber quants avions tenim
					    {
					    	
					    	Aircraft A = extraerInfoResults(linea);
							int[] posicionn = tablaDoc.PosicionTabla(A);
							tablaDoc.setAvion(A, posicionn);
							linea = br.readLine();
					    }
					    br.close();
				    
						if(primeravez)
						{
							inter = new InterfazOutput();
							inter.setruta1(ruta);
							inter.setRutaGen(ruta);
							inter.setTabla(tablaDoc);
							inter.setVisible(true);
							primeravez=false;
						}

				    
				    } catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
			        
				    
				}
				else if(avion.getOpcionMensaje() == 3)
				{
					
					System.out.println("Agente "+ getLocalName() +": he recibido los parametros");
					// Obtenemos los parametros iniciales que nos interesan:
					String[] parametros = sacarParametros(avion);
					
					avionesSimulacion = Integer.parseInt(valorparametro(parametros[4]));
					ruta = valorparametro(parametros[3])  + "Results.txt";
					
					// Ya podemos inicializar la tabla de aviones
					tabla = new Tabla(avionesSimulacion+1);
					
				}
				else if(avion.getOpcionMensaje() == 2 || avion.getOpcionMensaje()==0)
				{
					int[] posicionn = tabla.PosicionTabla(avion);
					tabla.setAvion(avion, posicionn);
				}
				else if (avion.getOpcionMensaje() == 10)
				{
					try {
						if(primeravez)
						{
							EscribirText();
							inter = new InterfazOutput();
							inter.setTabla(tabla);
							inter.setruta1(ruta);
							inter.setRutaGen(ruta);
							inter.setVisible(true);
							primeravez=false;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public boolean done() {
			return fin;
		}
	}
	public void EscribirText() throws IOException
	//Out(tabla tabla) In(String ruta)
	//Escribe en el documento que se encuentra en ruta la información relacionada con los aviones guardados en tabla
	
	{
		int i=0;
		FileWriter ficheroResults = new FileWriter(ruta);
		PrintWriter pwResults = new PrintWriter(ficheroResults);
		
		while(i<tabla.getnumero())
		{
			Aircraft Avion = new Aircraft();
			Avion=tabla.getAvion(i);
			Gson gson = new Gson();
			String jsonOutput = gson.toJson(Avion);
			pwResults.println(jsonOutput);
			
			i=i+1;
		}
		ficheroResults.close();
	}
}
