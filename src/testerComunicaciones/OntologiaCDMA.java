package testerComunicaciones;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

/*public class OntoligiaCDMA extends Ontology {
	
	
	// Definimos las palabras que se usarán para construir los mensajes de texto para las comunicaciones
	 // The name identifying this ontology
	 public static final String ONTOLOGY_NAME = "ontologia-libros"; 
	 // VOCABULARY 
	 public static final String LIBRO = "libro"; 
	 public static final String LIBRO_TITULO = "titulo"; 
	 public static final String LIBRO_AUTOR = "autor"; 
	 public static final String LIBRO_PRECIO = "precio"; 
	
	 // Atencion porque las palabras que se pongan aqui tienen que coincidir con los nombres
	 // de los campos. Si ponemos "owner" entonces tiene que hacer un método getOwner
	 public static final String TIENES = "tienes"; 
	 public static final String TIENES_TIENDA = "owner"; 
	 public static final String TIENES_LIBRO = "libro"; 
	 
	 public static final String COMPRO = "compro"; 
	 public static final String COMPRO_LIBRO = "libro"; 
	 
	 // The singleton instance of this ontology
	 private static Ontology theInstance = new OntologiaLibros(); 
	 // This is the method to access the singleton ontology object 
	 public static Ontology getInstance() { 
		 return theInstance; 
	 }
	 
	 
	 // Private constructor 
	 private OntoligiaCDMA() { 
	
		 super(ONTOLOGY_NAME, BasicOntology.getInstance()) ;
		 // Incorporamos a la ontologia las definiciones de conceptos, predicados y acciones
		 try 
		 { 
		 	add(new ConceptSchema(LIBRO), Libro.class);
	 		add(new PredicateSchema(TIENES), Tienes.class); 
	 		add(new AgentActionSchema(COMPRO), Compro.class); 
	 		
	 		
	 		// Estructura del esquema para Libro
	 		ConceptSchema cs = (ConceptSchema) getSchema(LIBRO); 
	 		cs.add(LIBRO_TITULO, (PrimitiveSchema) getSchema(BasicOntology.STRING));
	 		cs.add(LIBRO_AUTOR , (PrimitiveSchema) getSchema(BasicOntology.STRING));
	 		cs.add(LIBRO_PRECIO , (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
	 		
	 
	 		// Estructura del esquema para el predicado Tienes
	 		PredicateSchema ps = (PredicateSchema) getSchema(TIENES); 
	 		ps.add(TIENES_TIENDA, (ConceptSchema) getSchema(BasicOntology.AID)); 
	 		ps.add(TIENES_LIBRO, (ConceptSchema) getSchema(LIBRO)); 
	 		
	 		
	 		
	 		
	 		// Estructura para la accion compra
	 		AgentActionSchema as = (AgentActionSchema) getSchema(COMPRO); 
	 		as.add(COMPRO_LIBRO, (ConceptSchema) getSchema(LIBRO)); 
	
		 } 
	 
		 catch (OntologyException oe) { 
			 oe.printStackTrace(); 
		 } 
	 }

}
*/


public class OntologiaCDMA extends Ontology {

	// Definimos las palabras que se usarán para construir los mensajes de texto para las comunicaciones
			// The name identifying this ontology
			 public static final String ONTOLOGY_NAME = "Ontologia-CDMA"; 
			 // VOCABULARY 
			 public static final String FLIGHT = "flight"; 
			 public static final String FLIGHT_ID = "ID"; 
			 public static final String FLIGHT_STATUS = "status"; 
	/*		 public static final String FLIGHT_TABLA = "flight_tabla"; 
			 public static final String FLIGHT_ACTU = "flight_actu";*/
			 public static final String FLIGHT_MILESTONE = "milestone";
		
			 // Atencion porque las palabras que se pongan aqui tienen que coincidir con los nombres
			 // de los campos. Si ponemos "avion" entonces tiene que hacer un método getAvion
			 public static final String ENVIARINFO = "EnviarInfo"; 
			 public static final String ENVIARINFO_FLIGHT = "Avion";
			 
			 // The singleton instance of this ontology
			 private static Ontology theInstance = new OntologiaCDMA(); 
			 // This is the method to access the singleton ontology object 
			
			 public static Ontology getInstance() 
			 {
				 return theInstance; 
			 }
			 

			 //Constructor
			 
			 private OntologiaCDMA() { 
					
				super(ONTOLOGY_NAME, BasicOntology.getInstance());

	              // Incorporamos a la ontologia las definiciones de conceptos, predicados y acciones
				 try 
				 { 
				 	add(new ConceptSchema(FLIGHT), FlightP.class);
			 		add(new PredicateSchema(ENVIARINFO), EnviarInfo.class); 
			 		
			 		
			 		// Estructura del esquema para Avion
			 		ConceptSchema cs = (ConceptSchema) getSchema(FLIGHT); 
			 		
			 		cs.add (FLIGHT_ID, (PrimitiveSchema) getSchema (BasicOntology.STRING));
			 		cs.add (FLIGHT_STATUS, (PrimitiveSchema) getSchema (BasicOntology.STRING));
		/*	 		cs.add (FLIGHT_TABLA, (PrimitiveSchema) getSchema (BasicOntology.INTEGER));
			 		cs.add (FLIGHT_ACTU, (PrimitiveSchema) getSchema (BasicOntology.INTEGER));*/
			 		cs.add(FLIGHT_MILESTONE, (PrimitiveSchema) getSchema (BasicOntology.INTEGER));
			 	
			 				 		
			 		// Estructura para la accion enviar información
			 		PredicateSchema as = (PredicateSchema) getSchema(ENVIARINFO); 
			 		as.add(ENVIARINFO_FLIGHT , (ConceptSchema) getSchema(FLIGHT)); 
			
				 } 
			 
				 catch (OntologyException oe) { 
					 oe.printStackTrace(); 
				 } 
			 
				 
				 catch (Exception e) {
					 e.printStackTrace();
				 }
			 }
			 
		
}





