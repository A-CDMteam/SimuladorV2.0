package Agentes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import Agentes.CFMU.RecepcionAvion;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

import java.awt.Toolkit;

public class SimulatorInput extends CDM {
	
	String rutaInput = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/INPUT.txt";
	String rutaFP =  ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/FlightPlans.txt";
	String rutaFPS = ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/FlightPlansSalidas.txt";
	String rutaMilestones =  ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/TriggerCDM.txt";
	String rutaAO1 =  ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/AO1.txt";
	String rutaAO2 =  ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/AO2.txt";
	String rutaAO =  ""; //"C:/Users/"+name+"/workspace/SimuladorV1.3/VuelosAerolinea.txt";
	String rutaRetrasos = ""; // "C:/Users/"+name+"/workspace/SimuladorV1.3/Retrasos.txt";
	
	AID MilestoneTrigger = new AID("MilestoneTrigger", AID.ISLOCALNAME);
	AID CFMU = new AID("CFMU", AID.ISLOCALNAME);
	AID DDR2 = new AID("DDR2", AID.ISLOCALNAME);
	AID Output = new AID("Output", AID.ISLOCALNAME);
	AID Interfaz = new AID("Interfaz", AID.ISLOCALNAME);
	
	AID[] AgentesAO;
	String[] Aerolineas;
	
	InterfazSimulatorInput interfaz;
			
	boolean inicio;
	boolean parametros;
	boolean parametrosdefinidos = false;
	boolean DDR2valido=false;
	boolean DDR2leido=false;
	boolean MensajeMostrado = false;
	
	boolean errorretrasoactual = false;
	int[] errorLectura = {0,0,0,0};
	
	private AID[] Agentes;
	
	String AOforGH = "";
	
	int countArr;
	int countDep;
	int[] tiempos;
    int[] tiemposdep;
    int[] tiemposmil;
    String[][] valuesArr;
    String[][] valuesDep;
	
	ArrayList TiemposRetrasoMilestone= new ArrayList();
	ArrayList TiemposSoloSalidas= new ArrayList();
	String[] Retrasos;
	int numRetrasos;
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	
	protected void setup()
	{

		
		interfaz = new InterfazSimulatorInput();
		interfaz.setVisible(true);
		//interfaz.setIconImage(Toolkit.getDefaultToolkit().getImage("J:\\TFC-CDM\\SimuladorV1.3-GRAFICO\\airport.bmp")); /////////////////////////////////////////////
		
		System.out.println("Interfaz cargada");

		inicio = interfaz.inicioPrograma; // Parametros para saber cuando se cargan los parametros o se inicia la simulaciñon en la interfaz
		parametros = interfaz.parametrosDefinidos;
		
		addBehaviour(new RecepcionAvion());
	}
		class RecepcionAvion extends Behaviour{
	        
			private boolean fin = false;
			
	        int Muestralo = 0;
	        
	       
			public void action(){

				Aircraft Avion = new Aircraft();
				int i=-1000000000;
				boolean parametrosDDR2=interfaz.parametrosDDR2;
				boolean cargarresultados = interfaz.cargarresultados;
				String Airport="-";
				while(parametrosDDR2==false && cargarresultados==false)
				{
					i++;
					if(i==1000000000)
					{
						cargarresultados=interfaz.cargarresultados;
						i=-1000000000;
						parametrosDDR2=interfaz.parametrosDDR2;
					}
					if(parametrosDDR2)
					{
						Airport=(getAirport(interfaz.rutaArchivoTextGuardar.getText()));
						if(Airport.equals("-"))
						{
							parametrosDDR2=false;
							interfaz.parametrosDDR2=false;
						}
					}
					if(cargarresultados==true && parametrosDDR2==false)
					{
						System.out.println("Le has dado a cargar---> CARGAR");
						String Localitzacio = interfaz.getrutacargar();
						LlegirResults(Localitzacio);
						cargarresultados=false;
						interfaz.cargarresultados=false;
						
					}
						
					//No le dejamos continuar por ahora.
				}
				
				
				
				// 5 y 6 perque no es el avionparametros, es el avion DDR2, que te menys informacio(tindria de ser 10 i 11 sino)
				Avion.setIncidencias(interfaz.RUTA+" "+sacarParametros(interfaz.avionDDR2,5)+ " "+sacarParametros(interfaz.avionDDR2,6)+ " " + Airport); 
				AOforGH=sacarParametros(interfaz.avionDDR2,7);
				EscanejarAOforGH();
				AvisarDDR2(Avion);
				ACLMessage mensaje = blockingReceive();
				if (mensaje != null)
				{
					
					Aircraft avion = RecepcionMensaje(mensaje, Muestralo);
					if(avion.getOpcionMensaje()==-1)
					{
						if (avion.getIncidencias().equals("DDR2 leido"))
						{
							DDR2leido=true;
							DDR2valido=true;
						}
						else if(avion.getIncidencias().equals("Error"))
						{
							interfaz.parametrosDDR2=false; //Lo vuelvo a falso, porque el DDR2 no se ha leido correctamente
							interfaz.MostrarErrorDDR2();
						}
						else
						{
							interfaz.parametrosDDR2=false; //Lo vuelvo a falso, porque el DDR2 no se ha leido correctamente
							interfaz.MostrarErrorDDR2noaviones();
						}
						if(DDR2valido)
						{
							if(MensajeMostrado==false)
							{
								interfaz.CargarParametrosInicio();
								MensajeMostrado=true;
							}
							while(!parametrosdefinidos || !inicio) // El agente espera a que se definan los parametros para empezar el programa.
							{
								// Se ejecutara cuando se guarden o carguen los parametros en la interfaz
								if(interfaz.parametrosDefinidos && !parametrosdefinidos)
								{
									actualizarAOforGH(); //Actualitzem en avionparametros la linea de AOforGH per la nova linea.
									
									System.out.println("Avion Parametros: ");
									interfaz.avionparametros.printFlight(12);
						
									listaAerolineas(); // Definimos los agentes aerolinea que hay en la simulación
									// CargarAgentesAO(interfaz.avionparametros.getIncidencias());  BORRAR!
							
									// Broadcast de los parametros
									try {
										definirRutas(interfaz.avionparametros);
										CargarArchivos(interfaz.avionparametros);
										BroadcastParametros(interfaz.avionparametros);
										
										interfaz.panel.setVisible(true);
										
										
									} catch (IOException e) {
										// TODO Auto-generated catch block
										System.out.println("No se ha podido repartir la información de los parametros");
									}
									parametrosdefinidos = true;
								}
								
								//Parar el programa en caso de que pulse el boton de ponerretrasos hasta que se pulse aceptar o cancelar
					
								
								
								if(interfaz.inicioPrograma && !inicio)
								{
									System.out.println(interfaz.getHayRetrasos());
									RetrasosYMilestones(interfaz.getHayRetrasos());
									//System.out.println("He llegado");
									InicioPrograma(interfaz.avionparametros);
							
									inicio = true;
								}
								
							}
						}
						else
						{
							System.out.println("DDR2 no existe o esta modificado y no se puede leer");
						}
					}
				}
			}
			

			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return fin;
			}
		}
		
		
	private void actualizarAOforGH()
	
	//Actualitzem en avionparametros la linea de AOforGH per la nova linea.
	{
		String[] Incidenciasnoactualizado = interfaz.avionparametros.getIncidencias().split("\n");
		Incidenciasnoactualizado[Incidenciasnoactualizado.length-1]=AOforGH;
		int i=1;
		String actuIncidencias=Incidenciasnoactualizado[0];
		while(i<Incidenciasnoactualizado.length)
		{
			actuIncidencias=actuIncidencias+"\n"+Incidenciasnoactualizado[i];
			i++;
		}
		interfaz.avionparametros.setIncidencias(actuIncidencias);
	}
	private void EscanejarAOforGH()
	{
		String[] comp = AOforGH.split("\t");
		AOforGH="";
		int numcomp=comp.length;
		//Mirem que no en tinguem 2 que es diuen igual:
		int i=0;
		int j=1;
		while(i<numcomp)
		{
			String Comp1=comp[i].split("-")[0];
			j=i+1;
			while(j<numcomp)
			{
				String Comp2=comp[j].split("-")[0];
				if(Comp1.equals(Comp2) && Comp1.equals("")==false)
				{
					comp[i]=comp[i]+comp[j].substring(Comp2.length(), comp[j].length());
					comp[j]="";
				}
				j++;
			}
			i++;
		}
		//Mirem que 2 companyies no tinguin la mateixa AO en carreg
		ArrayList AOfets = new ArrayList();
		i=0;
		while(i<numcomp)
		{
			String[] AOscomp=comp[i].split("-");
			comp[i]=AOscomp[0];
			j=1;
			while(j<AOscomp.length)
			{
				if(AOfets.contains(AOscomp[j]))
				{
					AOscomp[j]="";
				}
				else
				{
					AOfets.add(AOscomp[j]);
					comp[i]=comp[i]+"-"+AOscomp[j];
				}
				j++;
			}
			if(comp[i].split("-").length==1)
				comp[i]="";
			else
				AOforGH=AOforGH+comp[i]+"\t";
			i++;
		}
		AOforGH=AOforGH.substring(0, AOforGH.length()-1);
		CargarAgentesAO(AOforGH);
	}
	private void CargarAgentesAO(String Incidencias)
	{
		String[] grupos = AOforGH.split("\t");
		int i=0;
		int valids=0;
		while(i<grupos.length)
		{
			if(grupos[i].equals("")==false)
			{
				valids++;
			}
				
			i++;
		}
		Agentes = new AID[8+valids] ;
		Agentes[0] = new AID("Interfaz", AID.ISLOCALNAME);
		Agentes[1] = new AID("CFMU", AID.ISLOCALNAME);
		Agentes[2] = new AID("ATC", AID.ISLOCALNAME);
		Agentes[3] = new AID("Airport", AID.ISLOCALNAME);
		Agentes[4] = new AID("MilestoneTrigger", AID.ISLOCALNAME);
		Agentes[5] = new AID("InfoSharing", AID.ISLOCALNAME);
		Agentes[6] = new AID("Output", AID.ISLOCALNAME);
		Agentes[7] = new AID("GH-GEN", AID.ISLOCALNAME);
		
//		i=1;
//		while(i<=valids)
//		{
//			Agentes[7+i]= new AID("GH"+i, AID.ISLOCALNAME);
//			i++;
//		}	
		String[] companyies=AOforGH.split("\t");
		i=1;
		while(i<=valids)
		{
			String nom=companyies[i-1].split("-")[0];
			Agentes[7+i]= new AID("GH-"+nom, AID.ISLOCALNAME);
			i++;
		}	
	}
	
	private void CargarArchivos(Aircraft a)
	{
		int[] resultados = {0,0,0,0,0,0};
		try {
			cargarAerolineas(interfaz.avionparametros);
			resultados = leerInput();
			

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		} // Cargamos el input de vuelos al Aeropuerto
		
		// Mensajes de funciona/error al crear los diferentes txts 
		if(resultados[0] == 1){
			System.out.println("Ordenes Simulador cargadas correctamente...");
		}
		if(resultados[1] == 1){
			System.out.println("Archivo Flight Plan creado correctamente...");
		}
		if(resultados[2] == 1){
			System.out.println("Archivo Llegadas por aerolinea creado correctamente...");
		}
//		if(resultados[3] == 1){
//			System.out.println("Archivo Salidas por aerolinea creado correctamente...");
//		}
//		if(resultados[4] == 1){
//			System.out.println("Archivo de Retrasos Cargado correctamente...");
//		}
//		if(resultados[5] == 1){
//			System.out.println("Retrasos notificados a todos los agentes...");
//		}
		
		if(resultados[0] == 0){
			System.out.println("No se ha podido cargar las Ordenes Simulador...");
		}
		if(resultados[1] == 0){
			System.out.println("No se ha podido crear el archivo de Flight Plans...");
		}		
		if(resultados[2] == 0){
			System.out.println("No se ha podido crear el archivo de Llegadas por aerolinea...");
		}		
//		if(resultados[3] == 0){
//			System.out.println("No se ha podido crear el archivo de Salidas por aerolinea...");
//		}
//		if (resultados[4] == 0){
//			System.out.println("No se ha podido cargar el archivo de Retrasos...");
//			System.out.println("Consideramos que no hay archivo de retrasos");
//		}
//		if(resultados[5] == 0){
//			System.out.println("No se ha podido informar a los agentes de los retrasos de la simulación...");
//		}
		
		
		// Preparamos el archivo de Aerolineas (uniendo Llegadas y Salidas)
		int resAO = ArchivoAO();
		if(resAO == 1){
			System.out.println("Archivo Aviones Aerolinea creado correctamente");
		}
		if(resAO == 0){
			System.out.println("No se ha podido crear el archivo Aviones Aerolinea...");
		}

	}
	
	private int ArchivoAO() {
		
		///Parte de la union de los 2 documentos:
		File fileAO1 = new File(rutaAO1);
		File fileAO2 = new File(rutaAO2);
		
		FileReader fr1 = null;
	    BufferedReader landings = null;
	    FileReader fr2 = null;
	    BufferedReader departures = null;
	    
	    int numdep=0;
		int numland=0;
	    
		try{
	    	 fr1 = new FileReader( fileAO1);
		     landings = new BufferedReader(fr1);
		     
		     fr2 = new FileReader( fileAO2);
		     departures = new BufferedReader(fr2);
		    
		     
		     
		     //Contamos cuantos llegadas y salidas tenemos
        while (landings.readLine() != null)
        	{
        		numland = numland + 1;
        	}
        while (departures.readLine() != null)
        	{
            	numdep = numdep + 1;
        	}
        landings.close();
        departures.close();
	    }
	    
	    catch(Exception filenptfoundexception){
	    	System.out.println("No encuentro los archivos AO1 y AO2");
	    	return 0;
	    }
	    
	    try{
	    	 fr1 = new FileReader( fileAO1); // Lectura
		     landings = new BufferedReader(fr1);
		     
		     fr2 = new FileReader( fileAO2);
		     departures = new BufferedReader(fr2);
		     
		     FileWriter ficheroAO = new FileWriter(rutaAO); // Escritura
		     PrintWriter pwAO = new PrintWriter(ficheroAO);		     
		    
		     
		     String[][] IDdep = new String[numdep][4];
		     
		     for (int i = 0; i < numdep; i++)
		        {
		            String line = departures.readLine();
		            String[] l = line.split("\t"); //Separem per espais una linea
		            IDdep[i][0] = l[0];
		            IDdep[i][1] = l[1];
		            IDdep[i][2] = l[2];
		            IDdep[i][3] = l[3];
		        }
		        for (int i = 0; i < numland; i++)
		        {
		            String line = landings.readLine();
		            String[] l = line.split("\t"); //Separem per espais una linea
		            boolean encontrado = false;
		            int p = 0;
		            while (encontrado == false && p < numdep)
		            {
		                if(l[0].equals(IDdep[p][0]))
		                {
		                    encontrado=true;
		                }
		                p = p + 1;
		            }
		            if (encontrado == true)
		            {
		                pwAO.println(line + "\t" + IDdep[p - 1][3]);
		                IDdep[p - 1][3] =  Integer.toString(9999);
		            }
		            else
		               pwAO.println(line + "\t" + 9999);
		        }
		        for (int i = 0; i < numdep; i++)
		        {
		            if(Integer.parseInt(IDdep[i][3]) != 9999)
		                pwAO.println(IDdep[i][0] + "\t" + IDdep[i][1] + "\t" + IDdep[i][2] + "\t" + 9999 + "\t" + IDdep[i][3]);
		        }
		        
		        pwAO.close();
		        return 1;

	    }
	    catch(Exception filenptfoundexception){
	    	System.out.println("No encuentro los archivos AO1 y AO2");
	    	return 0;
	    	
	    }
	            
		
	}

	public void cargarAerolineas(Aircraft avion)
	{
		String[] inc = avion.getIncidencias().split("\n");
		
		String[] ao = inc[6].split("\t");
		
		Aerolineas = new String[ao.length];
		Aerolineas = ao;
	}
	
	public int[] leerInput() throws IOException{ // Leera los archivos i devolvera 1 o 0 dependiendo de si ha podido crear
											// cada uno de los 3 .txt (Trigger, CFMU, AO)
		File archivo = new File(rutaInput);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    //Cadena de texto donde se guardara el contenido del archivo
	    String contenido = "";
	    int count = 0;
        countArr = 0;
        countDep = 0;
	    try
	    {
	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	        
	        
        String linea = br.readLine();
       
        while (linea != null) //Recorrem el fitxer per saber quants avions tenim
        {
            String[] l = linea.split("\t"); //Separem per espais una linea
            if (l.length == 9)
            {
                countArr = countArr + 1;
            }
            else
            {
                countDep = countDep + 1;
            }
            count = count + 1;
            linea = br.readLine();
        }
        
        br.close();
	    }
	    catch(Exception filenotfoundexception){
	    	System.out.println("No encuentro el archivo de Input");
	    }
        
        // Leemos el archivo
        
        fr = new FileReader( archivo );
        br = new BufferedReader( fr );
   
        tiempos = new int[countArr];
        tiemposdep = new int[countDep];
        tiemposmil = new int[countArr*6];
        
        valuesArr = new String[countArr][7];
        valuesDep = new String[countDep][5];
        
        int pos = 0;
        int dep = 0;
        
        for (int i = 0; i < count; i++)
        {
            String line = br.readLine();
            String[] l = line.split("\t"); //Separem per espais una linea
            if (l.length == 9)
            {
                valuesArr[pos][0] = l[1];//ID
                valuesArr[pos][1] = l[2];//modelo
                valuesArr[pos][2] = l[3];//ADEP
                valuesArr[pos][3] = l[4];//ADES
                valuesArr[pos][4] = l[5];//ATOT
                valuesArr[pos][5] = l[6];//TFIR
                valuesArr[pos][6] = l[7];//TAPP
                
                tiempos[pos] = Integer.parseInt(l[8]);
                tiemposmil[pos * 6] = Integer.parseInt(l[5]) - 300;
                tiemposmil[pos * 6 + 1] = Integer.parseInt(l[5]) - 200;
                tiemposmil[pos * 6 + 2] = Integer.parseInt(l[5]);
                tiemposmil[pos * 6 + 3] = Integer.parseInt(l[6]);
                tiemposmil[pos * 6 + 4] = Integer.parseInt(l[7]);
                tiemposmil[pos * 6 + 5] = Integer.parseInt(l[8]);
                pos = pos + 1;
            }
            else
            {
                valuesDep[dep][0] = l[1];
                valuesDep[dep][1] = l[2];
                valuesDep[dep][2] = l[3];
                valuesDep[dep][3] = l[4];
                
                if(l.length == 7)
                {
                	valuesDep[dep][4] = l[6];
                }
                else
                {
                	valuesDep[dep][4] = "-";
                }
                tiemposdep[dep]= Integer.parseInt(l[5]);
                dep = dep+1;
            }
        }
        br.close();

	    
        // Escritura
		
        FileWriter ficheroFP = new FileWriter(rutaFP);
        FileWriter ficheroAO1 = new FileWriter(rutaAO1);
        FileWriter ficheroAO2 = new FileWriter(rutaAO2);
        FileWriter ficheroFPS = new FileWriter(rutaFPS);
        
        PrintWriter pwFP = new PrintWriter(ficheroFP);
        PrintWriter pwAO1 = new PrintWriter(ficheroAO1);
        PrintWriter pwAO2 = new PrintWriter(ficheroAO2);
        PrintWriter pwFPS = new PrintWriter(ficheroFPS);
        

        //Escribimos el milestone de activacion de los aviones que solo salen del aeropuerto(no llegan)
        int k=0;
        while(k<valuesDep.length)
        {
        	int p=0;
        	boolean encontrado=false;
        	while(p<valuesArr.length && encontrado==false)
        	{
        		if(valuesDep[k][0].equals(valuesArr[p][0]))
        		{
        			encontrado=true;
        		}
        		p=p+1;
        	}
        	if(!encontrado)
        	{
        		int valor=tiemposdep[k]-300;
        		TiemposSoloSalidas.add(valuesDep[k][0]+"\t"+ String.valueOf(-1) +"\t"+valor);
        	}
        	
        	k=k+1;
        }
        // Escribimos el fichero de AO2
        for (int i = 0;i< countDep ;i++)
        {
        	int[] valores = valores(tiemposdep);
            pwAO2.println(valuesDep[valores[1]][0] + "\t" + valuesDep[valores[1]][0].charAt(0) + valuesDep[valores[1]][0].charAt(1) + valuesDep[valores[1]][0].charAt(2) +"\t" +valuesDep[valores[1]][1] +"\t" + tiemposdep[valores[1]] + "\t" + valuesDep[valores[1]][4]);
            pwFPS.println(valuesDep[valores[1]][0] + "\t" + valuesDep[valores[1]][0].charAt(0) + valuesDep[valores[1]][0].charAt(1) + valuesDep[valores[1]][0].charAt(2) +"\t" +valuesDep[valores[1]][1] +"\t" + valuesDep[valores[1]][2]+"\t"+valuesDep[valores[1]][3]+"\t"+tiemposdep[valores[1]]);
            tiemposdep[valores[1]] = 9999;        	
        }
        ficheroAO2.close();
        ficheroFPS.close();
        int escrituraAO2 = 1;
		
        // Esribimos el fichero AO1 y FP
        for (int i = 0; i < countArr; i++)
        {
        	int[] valores = valores(tiempos);
        	
            pwFP.println(valuesArr[valores[1]][0] + "\t" + valuesArr[valores[1]][0].charAt(0) + valuesArr[valores[1]][0].charAt(1) + valuesArr[valores[1]][0].charAt(2) + "\t" + valuesArr[valores[1]][1] + "\t" + valuesArr[valores[1]][2] + "\t" + valuesArr[valores[1]][3] + "\t" + valuesArr[valores[1]][4] + "\t" + valores[0]+"\t"+valuesArr[valores[1]][5]+"\t"+valuesArr[valores[1]][6]);
            pwAO1.println(valuesArr[valores[1]][0] + "\t" + valuesArr[valores[1]][0].charAt(0) + valuesArr[valores[1]][0].charAt(1) + valuesArr[valores[1]][0].charAt(2) + "\t" + valuesArr[valores[1]][1] + "\t" + valores[0]);

            tiempos[valores[1]] = 9999;
        }
        ficheroAO1.close();
        ficheroFP.close();
        int escrituraAO1 = 1;
        int escrituraFP = 1;
        
//        //********************************************************************
//        // Cargamos el documento de retrasos
//        int lecturaRetrasos = leerInputretrasos();
//        
//        // Informamos a los agentes de los retrasos
//        int CargarRetrasos = MandarRetrasos();
//        
//        //Ordenar TiemposRetrasoMilestone y TiempoSoloSalidas. Sino no sale bien en ciertos casos
//        TiemposRetrasoMilestone=OrdenarLlista(TiemposRetrasoMilestone);
//        TiemposSoloSalidas=OrdenarLlista(TiemposSoloSalidas);
//        
//        
//        // Escribimos el fichero Milestones
//        for (int i = 0; i < countArr * 6; i++)
//        {
//        	int[] valores = valores(tiemposmil);
//        	
//        	int term = (int) Math.ceil((valores[1])/6);
//            int milestone = (int) (valores[1]+1 - ((Math.ceil((valores[1] + 1) / 6))) * 6);
//            if ( milestone==0)
//            {
//            	milestone=6;
//            }
//            k=0;
//            
//           
//            while(k<TiemposRetrasoMilestone.size() && k < TiemposSoloSalidas.size())
//            {            
//            	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<valores[0] && Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<valores[0] )
//            	{
//            		if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2]))
//            		{
//                    	pwMilestones.println(TiemposSoloSalidas.get(k));
//                    	TiemposSoloSalidas.remove(k);
//            		}
//            		else
//            		{
//            			pwMilestones.println(TiemposRetrasoMilestone.get(k));
//            			TiemposRetrasoMilestone.remove(k);
//            		}
//            	}
//            	else
//            	{
//            		break;
//            	}
//            }
//            while(k<TiemposRetrasoMilestone.size())
//            {
//            	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<valores[0])
//            	{
//            		pwMilestones.println(TiemposRetrasoMilestone.get(k));
//            		TiemposRetrasoMilestone.remove(k);
//            	}
//            	else
//            	{
//            		break;
//            	}
//            }
//            while(k<TiemposSoloSalidas.size())
//            {
//            	if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<valores[0])
//            	{
//            		pwMilestones.println(TiemposSoloSalidas.get(k));
//            		TiemposSoloSalidas.remove(k);
//            	}
//            	else
//            	{
//            		break;
//            	}
//            }
//            
//            pwMilestones.println( valuesArr[term][0] + "\t" + milestone + "\t" + valores[0]);
//
//            tiemposmil[valores[1]] = 9999;
//        }
//        if(TiemposRetrasoMilestone.isEmpty()==false)
//        {
//        	pwMilestones.println(TiemposRetrasoMilestone.get(0));
//			TiemposRetrasoMilestone.remove(0);
//        }
//			
//        
//        ficheroMilestones.close();
//        int escrituraMil = 1;
//        //***************************************************************
        
        

       
        
        int[] resultado = {escrituraFP, escrituraAO1, escrituraAO2};
        
        return resultado;
        
        

	}
	public void RetrasosYMilestones(boolean retrasos)
	{
		
        FileWriter ficheroMilestones;
		try {
			ficheroMilestones = new FileWriter(rutaMilestones);

	        PrintWriter pwMilestones = new PrintWriter(ficheroMilestones);
	        if(retrasos)
	        {
				//********************************************************************
		        // Cargamos el documento de retrasos
		        int lecturaRetrasos = leerInputretrasos();
		        
		        // Informamos a los agentes de los retrasos
		        int CargarRetrasos = MandarRetrasos();
	        }
	        //Ordenar TiemposRetrasoMilestone y TiempoSoloSalidas. Sino no sale bien en ciertos casos
	        TiemposRetrasoMilestone=OrdenarLlista(TiemposRetrasoMilestone);
	        TiemposSoloSalidas=OrdenarLlista(TiemposSoloSalidas);
	        
	        
	        // Escribimos el fichero Milestones
	        for (int i = 0; i < countArr * 6; i++)
	        {
	        	int[] valores = valores(tiemposmil);
	        	
	        	int term = (int) Math.ceil((valores[1])/6);
	            int milestone = (int) (valores[1]+1 - ((Math.ceil((valores[1] + 1) / 6))) * 6);
	            if ( milestone==0)
	            {
	            	milestone=6;
	            }
	            int k=0;
	            
	           
	            while(k<TiemposRetrasoMilestone.size() && k < TiemposSoloSalidas.size())
	            {            
	            	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<valores[0] && Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<valores[0] )
	            	{
	            		if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2]))
	            		{
	                    	pwMilestones.println(TiemposSoloSalidas.get(k));
	                    	TiemposSoloSalidas.remove(k);
	            		}
	            		else
	            		{
	            			pwMilestones.println(TiemposRetrasoMilestone.get(k));
	            			TiemposRetrasoMilestone.remove(k);
	            		}
	            	}
	            	else
	            	{
	            		break;
	            	}
	            }
	            while(k<TiemposRetrasoMilestone.size())
	            {
	            	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<valores[0])
	            	{
	            		pwMilestones.println(TiemposRetrasoMilestone.get(k));
	            		TiemposRetrasoMilestone.remove(k);
	            	}
	            	else
	            	{
	            		break;
	            	}
	            }
	            while(k<TiemposSoloSalidas.size())
	            {
	            	if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<valores[0])
	            	{
	            		pwMilestones.println(TiemposSoloSalidas.get(k));
	            		TiemposSoloSalidas.remove(k);
	            	}
	            	else
	            	{
	            		break;
	            	}
	            }
	            
	            pwMilestones.println( valuesArr[term][0] + "\t" + milestone + "\t" + valores[0]);
	
	            tiemposmil[valores[1]] = 9999;
	        }
	        
	        
	        while(TiemposRetrasoMilestone.isEmpty()==false && TiemposSoloSalidas.isEmpty()==false)
	        {
	        	int k=1;
	        	int[] MinimoTiempo = new int[2];// [indice, valor]
	        	MinimoTiempo[1]=Integer.parseInt(((String) TiemposRetrasoMilestone.get(0)).split("\t")[2]);
	        	
	        	while(k<(TiemposRetrasoMilestone.size()))
	        	{
		        	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<MinimoTiempo[1])
		            {
		            	MinimoTiempo[1]=Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2]);
		            	MinimoTiempo[0]=k;
		            }
		        	k++;
	        	}
	        	while(k<(TiemposSoloSalidas.size()))
	        	{
		        	if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<MinimoTiempo[1])
		            {
		            	MinimoTiempo[1]=Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2]);
		            	MinimoTiempo[0]=k;
		            }
		        	k++;
	        	}
	        	k=MinimoTiempo[0];
	        	if(k<TiemposRetrasoMilestone.size())
	        	{
	        		pwMilestones.println(TiemposRetrasoMilestone.get(k));
	    			TiemposRetrasoMilestone.remove(k);
	        	}
	        	else
	        	{
	        		k=k-TiemposRetrasoMilestone.size();
	        		
	        		pwMilestones.println(TiemposSoloSalidas.get(k));
	            	TiemposSoloSalidas.remove(k);
	        	}
	        }
	        
	        
	        while(TiemposRetrasoMilestone.isEmpty()==false)
	        {
	        	int k=1;
	        	int[] MinimoTiempo = new int[2];// [indice, valor]
	        	MinimoTiempo[1]=Integer.parseInt(((String) TiemposRetrasoMilestone.get(0)).split("\t")[2]);
	        	while(k<(TiemposRetrasoMilestone.size()))
	        	{
		        	if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2])<MinimoTiempo[1])
		            {
		            	MinimoTiempo[1]=Integer.parseInt(((String) TiemposRetrasoMilestone.get(k)).split("\t")[2]);
		            	MinimoTiempo[0]=k;
		            }
		        	k++;
	        	}
	        	k=MinimoTiempo[0];
	        	pwMilestones.println(TiemposRetrasoMilestone.get(k));
				TiemposRetrasoMilestone.remove(k);
	        }
	        while(TiemposSoloSalidas.isEmpty()==false)
	        {
	        	int k=1;
	        	int[] MinimoTiempo = new int[2];// [indice, valor]
	        	MinimoTiempo[1]=Integer.parseInt(((String) TiemposSoloSalidas.get(0)).split("\t")[2]);
	        	while(k<(TiemposSoloSalidas.size()))
	        	{
		        	if(Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2])<MinimoTiempo[1])
		            {
		            	MinimoTiempo[1]=Integer.parseInt(((String) TiemposSoloSalidas.get(k)).split("\t")[2]);
		            	MinimoTiempo[0]=k;
		            }
		        	k++;
	        	}
	        	k=MinimoTiempo[0];
	        	pwMilestones.println(TiemposSoloSalidas.get(k));
	        	TiemposSoloSalidas.remove(k);
	        }

	        ficheroMilestones.close();
	        int escrituraMil = 1;
	        //***************************************************************
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void InicioPrograma(Aircraft avionparametros){
		
		// Para iniciar el programa se envian los mensajes al MilestoneTrigger, AO i CFMU diciendo que ya puede leer sus archivos de input
		
		Aircraft mensaje = new Aircraft(); // Creamos el avionmensaje que se enviara
		mensaje.setId("AvionMensajeInicioCDM");
		mensaje.setNextMilestone(true); // Damos los valores que los agentes entenderan como la orden de iniciar el CDM
		mensaje.setOpcionMensaje(1);
		mensaje.setIncidencias(avionparametros.getIncidencias()); // Añadimos los parametros de simulación porque algunos agentes los necesitan en este punto
		
		// Enviamos
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		
		msg.addReceiver(MilestoneTrigger); // Añadimos los agentes que recibiran el mensaje
		msg.addReceiver(CFMU);
		msg.addReceiver(Interfaz);
		// Añadimos las aerolineas a los receptores del mensaje
	
		for ( int i = 0; i < AgentesAO.length; i++){
			
			msg.addReceiver(AgentesAO[i]);			
		}
		
		Gson gson = new Gson(); // Preparamos el contenido del mensaje para poder enviarlo
		String jsonOutput = gson.toJson(mensaje);
		
		msg.setContent(jsonOutput);
		
		send(msg); //Enviamos
		
	}
	
	public int[] valores(int[] vector){
		
		int[] valores = {0,0};
		int min = 9999;
		int indice = 0;
		 //Calculo del minimo y posicion
		for(int i = 0; i<vector.length; i++){
			 if(vector[i] < min){
				min = vector[i];
			 	indice = i;
			 }
		}
		
		valores[0] = min;
		valores[1] = indice;

		return valores;
	}

	public void AvisarDDR2 (Aircraft Avion)
	{
		if(!DDR2leido)
		{
			Avion.setOpcionMensaje(-1);
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
	
			msg.addReceiver(DDR2);			
			
			// Creamos el contenido del mensaje
			Gson gson = new Gson();
			String jsonOutput = gson.toJson(Avion);
			
			msg.setContent(jsonOutput);
			
			// Enviamos
			send(msg);
		}
		
	}
	
	public String[] listaaerolineas(String ubicacion){
		File archivoaero = new File(ubicacion);
		
		FileReader fraero = null;
	    BufferedReader braero = null;
	    String[] aerolineas = null;
	    try
	    {
	    	fraero = new FileReader( archivoaero );
	    	braero = new BufferedReader( fraero );
	    
	   		int numaerolineas = 0;
	    	String lineaaero; //Obtenemos el contenido del archivo linea por linea
	    	while( ( lineaaero = braero.readLine() ) != null )
	    	{
	    		numaerolineas++;
	    	}
	    	aerolineas = new String[numaerolineas];
			fraero = new FileReader( archivoaero );
	    	braero = new BufferedReader( fraero );
	    	int i=0;
	    	while( ( lineaaero = braero.readLine() ) != null )
	    	{
	    		aerolineas[i]="AO-" + lineaaero;
	    		i++;
	    	}
	    }
	    catch(Exception FileNotFoundException)
	    {
	    	System.out.println("No encuentro el archivo de las aerolineas");
	    }
	    return aerolineas;
	}
	
	public AID[] AgentesAO(Aircraft avion){

		String ubicacion = rutaArchivos(avion) + "/Aerolineas.txt";
		
		String[] aerolineas = listaaerolineas(ubicacion);
			// Creamos el vector de AIDs de agentes a partir de las aerolineas en el txt.
		
		AID[] Aerolineas = null;
		
	    int i =0;
	    while(i < aerolineas.length)
	    {
	    	Aerolineas[i] = new AID(aerolineas[i], AID.ISLOCALNAME);
	    	i++;
	    }
	    	Aerolineas[i] = new AID("AO-GEN", AID.ISLOCALNAME);
	    	
	    return Aerolineas;	
	}
	
	public String valorparametro(String parametro){
		String[] parametroyvalor = parametro.split("\t");
		
		String valor = parametroyvalor[1];
		
		return valor;			
	}
	
	public String rutaArchivos(Aircraft avion){
		
		String[] incidencias = avion.getIncidencias().split("\n");
		String ubicacion = valorparametro(incidencias[3]);
		
		return ubicacion;
	}
	
	public void definirRutas(Aircraft avion){
		
		String ruta = rutaArchivos(avion);
		
		rutaInput = ruta + "Input.txt"; 
		rutaFP = ruta + "FlightPlans.txt";
		rutaMilestones = ruta + "TriggerCDM.txt";
		rutaAO1 = ruta + "AO1.txt";
		rutaAO2 = ruta + "AO2.txt";
		rutaAO = ruta + "VuelosAerolinea.txt";
		rutaFPS = ruta + "FlightPlansSalidas.txt";
		rutaRetrasos = ruta + "Retrasos.txt";
		
	}
	
	public void BroadcastParametros(Aircraft avion) throws IOException {
		
		avion.setAO(AOforGH);
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		// Añadimos los receptores
		for ( int i = 0; i < Agentes.length; i++){
			
			msg.addReceiver(Agentes[i]);			
		}
			 //Añadimos las aerolineas de la lista
		for (int i = 0; i < AgentesAO.length; i++){
			
			msg.addReceiver(AgentesAO[i]);			
		}
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(avion);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);

	}

	public void listaAerolineas() // Utilizamos el avionparametros para declarar los agentes Aerolinea que habran en la simulación
	{
		String[] parametros = interfaz.avionparametros.getIncidencias().split("\n");
		
		String[] nombres = parametros[6].split("\t"); // Obtenemos la lista de las aerolineas
		
		AgentesAO = new AID[nombres.length + 1]; // Definimos el numero de aerolineas que hay (las del documento + la general)
		
    	for (int i = 0; i < nombres.length; i++) // Listamos los identificadores de los agentes aerolinea
    	{
    		AgentesAO[i] = new AID("AO-"+nombres[i], AID.ISLOCALNAME);
    	}
    	
    	// Añadimos la aerolinea general
    	int ulti = AgentesAO.length-1;
    	
    	AgentesAO[ulti] = new AID("AO-GEN", AID.ISLOCALNAME);
	}

	public int leerInputretrasos()
	{
		numRetrasos=0;
		File archivo = new File(rutaRetrasos);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    try
	    {
	    	fr = new FileReader( archivo );
        	br = new BufferedReader( fr );
	    	String linea;
	    	
	    	while((linea = br.readLine())!=null)
	    	{
        		numRetrasos++;
	    	}
	    	Retrasos = new String[numRetrasos];
    		fr = new FileReader( archivo );
        	br = new BufferedReader( fr );
        	int i=0;
        	while((linea = br.readLine())!=null)
        	{
        		Retrasos[i]= linea;
        		i++;
        	}
        	return 1;
	    }
	    catch(Exception FileNotFoundException)
	    {
//	    	System.out.println("No encuentro el archivo con los retrasos. Supondremos que no existen restrasos");
	    	Retrasos = new String[1];
	    	Retrasos[0]="";
	    	return 0;
	    }
	}
	public int MandarRetrasos()
	{
		int r = 0;
		int i=0;
		while(i<numRetrasos)
		{
			Aircraft Avion = new Aircraft();
			Avion.setIncidencias(Retrasos[i]);
			try 
			{
				BroadcastInfoRetraso(Avion);
				r = 1;
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				r = 0;
			}
			i++;
		}
		return r;
	}
	public void BroadcastInfoRetraso(Aircraft avion) throws IOException {
		avion.setOpcionMensaje(4);
		// Definimos el tipo de mensaje FIPA que enviaremos (tiene que coincidir con el filtro de recepcion)
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		// Añadimos el receptor
		AID[] receptor = GetReceptor(avion);
		if(!errorretrasoactual)
		{
			int i=0;
			while(i<receptor.length)
			{
				msg.addReceiver(receptor[i]);
				i++;
			}
			
			// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
			Gson gson = new Gson();
			String jsonOutput = gson.toJson(avion);
			
			// Enviamos el avion
			msg.setContent(jsonOutput);
			
			send(msg);
		}
	}
	
	private AID[] GetReceptor (Aircraft Avion) //Pillem el receptor de l'avio:
	{
		errorretrasoactual=false;
		AID[] receptor = null;
		String Missatge = Avion.getIncidencias();
		String[] paraules = Missatge.split(" ");
		if(paraules.length==1)
			paraules=Missatge.split("\t");
		int ModoRetraso = Integer.parseInt(paraules[0]);
		if(ModoRetraso == 4)
		{
			if(Integer.parseInt(paraules[2])<=Integer.parseInt(paraules[1]) ||
					Integer.parseInt(paraules[3])<=Integer.parseInt(paraules[2]) ||
					((Integer.parseInt(paraules[4])==30 || paraules[4].equals("-")) &&
							(Integer.parseInt(paraules[5])==30 || paraules[5].equals("-"))))
			{
				errorretrasoactual=true;
			}
			receptor = new AID[1];
			receptor[0] = new AID("ATC", AID.ISLOCALNAME);
			
			if((TiemposRetrasoMilestone.contains("Retrasopista\t0\t"+ paraules[3]))==false)
			{
				int i=0;
				while(i<TiemposRetrasoMilestone.size())
				{
					if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(i)).split("\t")[2])>Integer.parseInt(paraules[3]))
					{
						TiemposRetrasoMilestone.add(i,"Retrasopista\t0\t"+ paraules[3]);
						break;
					}
					i++;
				}
				if(i==TiemposRetrasoMilestone.size())
					TiemposRetrasoMilestone.add("Retrasopista\t0\t"+ paraules[3]);
			}
		}
		
		else if(ModoRetraso==3)
		{
			receptor = new AID[1];
			if(paraules[5].equals("AO"))
			{
				receptor[0] = GetAOAID(paraules[1]);
			}
			else if(paraules[5].equals("GH"))
			{
				receptor[0] = GetGHAID(paraules[1]);
			}
			else
			{
				receptor[0] = new AID(paraules[5], AID.ISLOCALNAME);
			}
		}
		else
		{
			int X;
			if(ModoRetraso==2)
			{
				X=Integer.parseInt(paraules[4]);
			}
			else//(ModoRetraso==1)
			{
				if(Integer.parseInt(paraules[3])>Integer.parseInt(paraules[1]) || Integer.parseInt(paraules[1])>Integer.parseInt(paraules[2]))
				{
					errorretrasoactual=true;
				}
				if((TiemposRetrasoMilestone.contains("Retraso\t0\t"+ paraules[3]))==false)
				{
					int i=0;
					while(i<TiemposRetrasoMilestone.size())
					{
						if(Integer.parseInt(((String) TiemposRetrasoMilestone.get(i)).split("\t")[2])>Integer.parseInt(paraules[3]))
						{
							TiemposRetrasoMilestone.add(i,"Retraso\t0\t"+ paraules[3]);
							break;
						}
						i++;
					}
					if(i==TiemposRetrasoMilestone.size())
						TiemposRetrasoMilestone.add("Retraso\t0\t"+ paraules[3]);
				}
				X=Integer.parseInt(paraules[5]);
			}
			
			
			//Efectos de X ************************************
			if(X==0)
			{
				receptor = new AID[1];
				receptor[0]=new AID("CFMU", AID.ISLOCALNAME);
			}
			if(X==1 || X==2)
			{
				receptor = new AID[1];
				receptor[0]=new AID("Airport", AID.ISLOCALNAME);
			}
			if(X==3)
			{
				
				if(ModoRetraso==2)
				{
					receptor = new AID[1];
					receptor[0]=GetGHAID(paraules[1]);
				}
				else if(AOforGH.equals("")) //ModoRetraso==1
				{
					receptor = new AID[1];
					receptor[0]=new AID("GH-GEN", AID.ISLOCALNAME);
				}
				else //ModoRetraso==1 && AOforGH!=""
				{
					String[] GHs=AOforGH.split("\t");
					receptor = new AID[1+GHs.length];
					receptor[0]=new AID("GH-GEN", AID.ISLOCALNAME);
					int i=0;
					while(i<GHs.length)
					{
						receptor[1+i]=new AID("GH-"+GHs[i].split("-")[0], AID.ISLOCALNAME);
						i++;
					}	
				}
			}
		}
		
		return receptor;
	}
	private AID GetGHAID (String ID) //Pillem el receptor de l'avio:
	{
		AID GHAID = null;
		char lletra1 = ID.charAt(0);
		char lletra2 = ID.charAt(1);
		char lletra3 = ID.charAt(2);
		String AO=Character.toString(lletra1)+Character.toString(lletra2)+Character.toString(lletra3);
		
		String[] GHs=AOforGH.split("\t");
		int GHamount=GHs.length;
		if(AOforGH.equals(""))
			GHamount=0;
		
		boolean encontrado=false;
		int i=0;
		while(i<GHamount && encontrado==false)
		{
			String paraules[]=GHs[i].split("-");
			int j=1;
			
			while(j<paraules.length && encontrado==false)
			{
				if(paraules[j].equals(AO))
				{
					GHAID = new AID ("GH-"+paraules[0], AID.ISLOCALNAME);
					encontrado=true;
				}
				j++;
			}
			if(encontrado==false)
				GHAID = new AID ("GH-GEN", AID.ISLOCALNAME);
			i++;
		}
		
		return GHAID;
	}
	private AID GetAOAID (String ID) //Pillem el receptor de l'avio:
	{
		AID Aerolinea = null;
		char lletra1 = ID.charAt(0);
		char lletra2 = ID.charAt(1);
		char lletra3 = ID.charAt(2);
		String AO=Character.toString(lletra1)+Character.toString(lletra2)+Character.toString(lletra3);
		int i=0;
		boolean encontrado=false;
		while(i<Aerolineas.length)
		{
			if(AO.equals(Aerolineas[i]))
			{
				encontrado=true;
				break;
			}
			i++;
		}
		if(encontrado==false)
			AO="GEN";
		Aerolinea = new AID("AO-"+AO, AID.ISLOCALNAME);
		return Aerolinea;
	}
	public ArrayList OrdenarLlista(ArrayList Llista)
	{
		ArrayList Out=new ArrayList();
   		while(Llista.size()!=0)
   		{
   			String frase=(String)Llista.get(0);
			String[] paraules = frase.split("\t");
			int minim=Integer.parseInt(paraules[2]);
   			
   			int valor=0;
   			int k=1;
   			while(k<Llista.size())
   			{
   				frase=(String)Llista.get(k);
   				paraules = frase.split("\t");
   				if(Integer.parseInt(paraules[2])<minim)
   				{
   					minim=Integer.parseInt(paraules[2]);
   					valor=k;
   				}
   				k++;
   			}
   			Out.add((String) Llista.get(valor));
   			Llista.remove(valor);
   		}
		return Out;
	}
	public void LlegirResults(String Localitzacio)
	{
		Aircraft A= new Aircraft();
		A.setOpcionMensaje(9);
		A.setIncidencias(Localitzacio);
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		msg.addReceiver(Output);
		
		// Para enviar el avion en un mensaje, utilizamos las librerias Gson para convertir el objeto avion en un String. (Simplificamos el envio de datos)
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(A);
		
		// Enviamos el avion
		msg.setContent(jsonOutput);
		
		send(msg);
	}
	public String getAirport(String ruta)
	{
		String Airport = "-";
		
		File archivo = new File(ruta);
	    
	    try
	    {
	        
	    	FileReader fr = new FileReader( archivo );
	    	BufferedReader br = new BufferedReader( fr );
	        
	        
	        Airport = br.readLine(); //Aiport esta en la primera linea del documento Airport
	        br.close();
	        if(Airport!= null)
	        	return Airport;
	        else
	        {
		    	System.out.println("El archivo aeropuerto no esta escrito correctamente");
		    	Airport= "-";
		    	return Airport;
	        }
	    }
	    
	    catch(Exception filenotfoundexception)
	    {
	    	System.out.println("No encuentro el archivo del Aeropuerto");
	    	return Airport;
	    }
		
		
	}
	
}