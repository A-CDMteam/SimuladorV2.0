package testerComunicaciones;

import com.google.gson.Gson;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class ReceptorBucle extends Agent {
    
	int i = 0;
	Tabla datos = new Tabla(4);
	
	public class RecepcionAvion extends SimpleBehaviour
    {
            private boolean fin = false;
            public void action()
            {
                System.out.println(" Preparandose para recibir");
 
            //Obtiene el primer mensaje de la cola de mensajes
                ACLMessage mensaje = blockingReceive();
                if (mensaje!= null)
                {
        			System.out.println(getLocalName() + " Mensaje recibido");
							
					FlightP avion = extraerInfo(mensaje);
	
					datos.setAvion(avion, i);
					
					System.out.println(getLocalName() + ": acaba de recibir el siguiente mensaje: ");
					
					System.out.println("Avion " + datos.getAvion(i).getId()+ " Status " + datos.getAvion(i).getSTATUS()+ ". En la tabla es el avion numero " + datos.getnumero());
					
					i = i+1;
					
                }
            }
            
            public boolean done()
            {
                return fin;
            }
    }
    protected void setup()
    {
        addBehaviour(new RecepcionAvion());
        System.out.println(getLocalName() + "operativo");
        
        
    }
	public FlightP extraerInfo(ACLMessage msg)
	{
		
		Gson gson = new Gson ();
		String jsonInput = msg.getContent();
		
		FlightP avion = new FlightP ();
		
		avion = gson.fromJson (jsonInput, avion.getClass());

		
		return avion;
	}
}
