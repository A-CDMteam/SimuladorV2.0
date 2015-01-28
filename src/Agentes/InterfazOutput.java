package Agentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import com.google.gson.Gson;
import javax.swing.JLayeredPane;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.border.BevelBorder;

public class InterfazOutput extends JFrame {

	private JPanel contentPane;
	public JTable table;
	DefaultTableModel model;
	DefaultTableModel model1;
	DefaultTableModel model2;
	
	InterfazAnalisis interAnalisis;
	int[] aviones = {0,0,0}; // Numero de aviones Ariborne y aviones Airport
	Tabla tablaAviones;
	Tabla tablaCargada;
	JLabel lblTotalAircraftsNumber;
	//String ruta = "C:/Users/xavi/workspace/SimuladorV1.5/ResultsFull.txt";
	String Ruta1="";
	String Ruta2="";
	String RutaPrograma;
	private JTable table_1;
	private JTable table_2;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JLabel lblTotalAircraftsCargados;
	private JLabel lblNumAvionesCargados;
	private JTextField textFieldCargar;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new Interfaz();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public InterfazOutput() {
		
		setTitle("A-CDM Resultados");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 868, 566);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setVisible(true);
		DefaultTableModel modelo = new DefaultTableModel();

		
		JLabel lblTotalAircrafts = new JLabel("Total aircrafts on A-CDM: ");
				
		lblTotalAircraftsNumber = new JLabel(""+aviones[0]);
		
		JLayeredPane layeredPane = new JLayeredPane();
		
		JButton btnAnalizar = new JButton("Analizar");
		btnAnalizar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				interAnalisis = new InterfazAnalisis();
				if(Ruta2.equals("")==false)
				{
					interAnalisis.setTabla(tablaCargada,2);
				}
				interAnalisis.setTabla(tablaAviones,1);
				interAnalisis.setRuta(RutaPrograma);
				interAnalisis.setVisible(true);
			}
		});
		
		lblTotalAircraftsCargados = new JLabel("Total aircrafts cargados: ");
		lblTotalAircraftsCargados.setVisible(false);
		
		lblNumAvionesCargados = new JLabel("0");
		lblNumAvionesCargados.setVisible(false);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(layeredPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTotalAircrafts)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTotalAircraftsNumber, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
							.addGap(269)
							.addComponent(lblTotalAircraftsCargados)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNumAvionesCargados)
							.addPreferredGap(ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
							.addComponent(btnAnalizar))
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(layeredPane, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAnalizar)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblTotalAircrafts)
							.addComponent(lblTotalAircraftsNumber)
							.addComponent(lblNumAvionesCargados)
							.addComponent(lblTotalAircraftsCargados)))
					.addGap(18)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
					.addGap(24))
		);
		
		JButton btnCargar = new JButton("Cargar");
		btnCargar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				scrollPane.setVisible(false);
				panel.setVisible(true);
				
				String r = textFieldCargar.getText(); 
						//"C:/Users/Ivan/workspace/SimuladorV1.6/ResultadosPrueba/ResultsFull.txt";
				
				clearTablaCargada();
				
				try {
					cargardeTXT(r);
					Ruta2=r;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,"No se han podido cargar los resultados");
				}
			}
		});
		
		JLabel lblCargarResultadosDesde = new JLabel("Comparar Resultados:");
		lblCargarResultadosDesde.setFont(new Font("Verdana", Font.BOLD, 11));
		
		JLabel lblRuta = new JLabel("Cargar desde archivo. Ruta:");
		
		textFieldCargar = new JTextField();
		textFieldCargar.setText("C:/Users/xavi/workspace/SimuladorV2.0/ResultadosPrueba/ResultsFull2.txt");
		textFieldCargar.setColumns(10);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblRuta)
						.addComponent(textFieldCargar, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
						.addComponent(lblCargarResultadosDesde, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCargar, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(lblCargarResultadosDesde, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblRuta)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldCargar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnCargar)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setVisible(false);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JLabel lblResultados = new JLabel("Resultados Simulaci\u00F3n: ");
		lblResultados.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblResultadosCargados = new JLabel("Resultados Cargados: ");
		lblResultadosCargados.setFont(new Font("Tahoma", Font.BOLD, 12));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(56)
							.addComponent(lblResultados))
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(70)
							.addComponent(lblResultadosCargados)
							.addContainerGap())))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblResultados)
						.addComponent(lblResultadosCargados))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)))
		);
		
		table_2 = new JTable();
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"N", "CALLSIGN", "STATUS", "MODEL", "ETOT'", "ELDT", "EIBT", "EOBT", "ETOT", "Milestone"
			}
		));
		scrollPane_2.setViewportView(table_2);
		model2 = (DefaultTableModel) table_2.getModel();
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"N", "CALLSIGN", "STATUS", "MODEL", "ETOT'", "ELDT", "EIBT", "EOBT", "ETOT", "Milestone"
			}
		));
		model1 = (DefaultTableModel) table_1.getModel();
		
		scrollPane_1.setViewportView(table_1);
		panel.setLayout(gl_panel);
		
		scrollPane = new JScrollPane();
		table = new JTable(modelo);
		table.setEnabled(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);		
		
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"N", "CALLSIGN", "STATUS", "MODEL", "ETOT'", "ELDT", "EIBT", "EOBT", "ETOT", "Milestone"
			}
		));
		table.getColumnModel().getColumn(1).setPreferredWidth(85);
		table.getColumnModel().getColumn(2).setPreferredWidth(102);
		table.getColumnModel().getColumn(9).setPreferredWidth(60);
		
		model = (DefaultTableModel) table.getModel();
		
		
		scrollPane.setViewportView(table);
		GroupLayout gl_layeredPane = new GroupLayout(layeredPane);
		gl_layeredPane.setHorizontalGroup(
			gl_layeredPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
		);
		gl_layeredPane.setVerticalGroup(
			gl_layeredPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
		);
		layeredPane.setLayout(gl_layeredPane);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void setruta1(String ruta)
	{
		Ruta1 = ruta;
	}
	
	public void setTabla(Tabla t)
	{	
		tablaAviones = new Tabla(t.getAviones().length);
		
		tablaAviones.setAviones(t.getAviones());		
		
		int i = 0;
		
		int getaviones = tablaAviones.getAviones().length;
		int contaraviones = tablaAviones.contarAviones();
		
		while(i < contaraviones)
		{
			ActualizarTabla(tablaAviones.getAvion(i), i);
			
			i++;
		}
		
		AircraftsNumber();
	}
	
	public String[] datosAvion(Aircraft avion){ // Traduce el objeto aircraft a un String[] con el que trabaja la interfaz
		
		String[] datos = new String[17];
		
		 // Obtenemos los datos del avion:
		String id = avion.getId();
		String status = avion.getSTATUS();
		int ETOTprima = avion.getValorTiempos(0);
		int ELDT = avion.getValorTiempos(1);
		int ETOT = avion.getValorTiempos(2);
		int EIBT = avion.getValorTiempos(3);
		int EOBT = avion.getValorTiempos(4);
		int milestone = avion.getMilestone();
		boolean nextM = avion.getNextMilestone();
		String AO = avion.getAO();
		String ADEP = avion.getADEP();
		String ADES = avion.getADES();
		String modelo = avion.getModelo();
		int ASBT = avion.getValorTiemposSecundarios(0);
		int ACGT = avion.getValorTiemposSecundarios(1);
		int ARDT = avion.getValorTiemposSecundarios(2);
		int ASRT = avion.getValorTiemposSecundarios(3);
		int TSAT = avion.getValorTiemposSecundarios(4);
		
		// Pulimos algunos valores para aplicar uniformidad a los tiempos (todos los tiempos con 4 cifras)
		String etotprima = arreglarMuestra(String.valueOf(ETOTprima));
		String eldt = arreglarMuestra(String.valueOf(ELDT));
		String etot = arreglarMuestra(String.valueOf(ETOT));
		String eibt = arreglarMuestra(String.valueOf(EIBT));
		String eobt = arreglarMuestra(String.valueOf(EOBT));
		
		// Rellenamos el String con los datos del avion;
		datos[0] = id;
		datos[1] = status;
		datos[2] = etotprima;
		datos[3] = eldt;
		datos[4] = etot;
		datos[5] = eibt;
		datos[6] = eobt;
		datos[7] = String.valueOf(milestone);
		datos[8] = AO;
		datos[9] = ADEP;
		datos[10] = ADES;
		datos[11] = modelo;
//		datos[12] = String.valueOf(ASBT);
//		datos[13] = String.valueOf(ACGT);
//		datos[14] = String.valueOf(ARDT);
//		datos[15] = String.valueOf(ASRT);
//		datos[16] = String.valueOf(TSAT);
		
		
		return datos;
	}
		
	public void ActualizarTabla(Aircraft avion, int posicion){
		
		// Recojemos los datos del avion que se pondran en la tabla
		String[] datos = null;
		datos = datosAvion(avion);
		
		// Creamos la fila que añadiremos a la tabla
		Object [] fila = new Object[15];
		fila[0] = posicion + 1;
		fila[1] = datos[0];		// ID
		fila[2] = datos[1];		// STATUS
		fila[3] = datos[11];	// Modelo
		fila[4] = datos[2];		// ETOTprima
		fila[5] = datos[3];		// ELDT	
		fila[6] = datos[5];		// EIBT
		fila[7] = datos[6];		// EOBT
		fila[8] = datos[4];		// ETOT
		fila[9] = datos[7];		// Milestone
		fila[10] = datos[12];	// ASBT
		fila[11] = datos[13];	// ACGT
		fila[12] = datos[14];	// ARDT
		fila[13] = datos[15];	// ASRT
		fila[14] = datos[16];	// TSAT

		// Añadimos la fila a la tabla
		if(posicion == model.getRowCount()){ // Es la primera vez que aparece este avion en el CDM
			model.addRow(fila);
			model1.addRow(fila);
			AircraftsNumber();
		}
		else{
			ActualizarValores(avion, fila);	
			AircraftsNumber();
		}
	}
	
	public void ActualizarTablaCargada(Aircraft avion, int posicion)
	{

		// Recojemos los datos del avion que se pondran en la tabla
		String[] datos = null;
		datos = datosAvion(avion);
		
		// Creamos la fila que añadiremos a la tabla
		Object [] fila = new Object[15];
		fila[0] = posicion + 1;
		fila[1] = datos[0];		// ID
		fila[2] = datos[1];		// STATUS
		fila[3] = datos[11];	// Modelo
		fila[4] = datos[2];		// ETOTprima
		fila[5] = datos[3];		// ELDT	
		fila[6] = datos[5];		// EIBT
		fila[7] = datos[6];		// EOBT
		fila[8] = datos[4];		// ETOT
		fila[9] = datos[7];		// Milestone
		fila[10] = datos[12];	// ASBT
		fila[11] = datos[13];	// ACGT
		fila[12] = datos[14];	// ARDT
		fila[13] = datos[15];	// ASRT
		fila[14] = datos[16];	// TSAT

		// Añadimos la fila a la tabla
		if(posicion == model2.getRowCount()){ // Es la primera vez que aparece este avion en el CDM
			model2.addRow(fila);
			AircraftsNumber();
		}
		else{
			ActualizarValoresCargada(avion, fila);	
			AircraftsNumber();
		}
		
	}
	
	public void ActualizarValores(Aircraft avion, Object[] datos){ // El avion ya esta en la tabla, ha cambiado algun valor
		
		int numerofila = posicionenlatabla(datos);
		
		if(numerofila < model.getRowCount())
		{
			model.removeRow(numerofila);
			model1.removeRow(numerofila);
		}
		
		model.insertRow(numerofila, datos);
		model1.insertRow(numerofila, datos);
	}
	
	public void ActualizarValoresCargada(Aircraft avion, Object[] datos)
	{
		int numerofila = posicionenlatabla(datos);
		
		if(numerofila < model.getRowCount())
		{
			model2.removeRow(numerofila);
			
		}
		model2.insertRow(numerofila, datos);
		
	}
	
	public int posicionenlatabla(Object[] datos){
		
		int i = 0;
		int ee = model.getRowCount();
		
		while(i < model.getRowCount()){
			String ID = (String) datos[1];
			String IDn = (String) model.getValueAt(i, 1);
			
			if(ID.equals(IDn) ){
				break;
			}
			else{
				i++;
			}
		}
		return i;
		
	}

	public void AircraftsNumber(){ //Busca en la tabla cuantos aviones estan AIRBORNE y cuantos ON AIRPORT
		
		aviones[0] = 0;
		aviones[1] = 0;
		aviones[2] = 0;
				
		int i = 0;
		
		while(i < table.getModel().getRowCount())
		{
			int milestone = Integer.parseInt(table.getModel().getValueAt(i,9).toString());
			
			if(milestone >= 6){
				aviones[2]++;				
			}
			if(milestone>=3 && milestone<6){
				aviones[1]++;
			}
			if(milestone == 16)
			{
				aviones[2] --;
			}
			aviones[0]++;
			i++;
			

		}
			//this.lblAirborneNumber.setText(""+aviones[1]);
			//this.lblOnAirportNumber.setText(""+aviones[2]);
			this.lblTotalAircraftsNumber.setText(""+aviones[0]);
	}

	public String arreglarMuestra(String t) // Cambia el tiempo para dejarlo adecuado a como se verá en la tabla (añade un "0" si tiene 3 cifras y pone un "-" si es solo un 0)
	{
		String newt = t;
		String[] tvec = t.split("");
		
		if(!tvec[1].equals("-"))
		{
			if(t.length() == 3)
			{
				newt = "0" + t;
			}
			if(t.length() == 2)
			{			
				newt = "00"+t;
			}
			if(t.length() == 1)
			{
				newt = "000" + t;
			}
			if(t.equals("0"))
			{
				newt = " - ";
			}
		}
		if(tvec[1].equals("-"))
		{
			if(t.length() == 4)
			{
				newt = "-0" + tvec[2] + tvec[3] + tvec[4];
			}
			if(t.length() == 3)
			{			
				newt = "-00" + tvec[2] + tvec[3];
			}
			if(t.length() == 2)
			{
				newt = "-000" + tvec[2];
			}
		}
		
		return newt;
	}
	
	public void cargardeTXT(String ruta) throws IOException
	{
		tablaCargada = leerTabla(ruta);
		
		int i = 0;

		int contaraviones = tablaCargada.contarAviones();
		
		while(i < contaraviones)
		{
			ActualizarTablaCargada(tablaCargada.getAvion(i), i);
			
			i++;
		}
		
		lblTotalAircraftsCargados.setVisible(true);
		lblNumAvionesCargados.setVisible(true);
		lblNumAvionesCargados.setText(Integer.toString(contaraviones));
		
	}
	
	public Tabla leerTabla(String r) throws IOException{
		
		int Muestralo = 0;
		
		File archivoresults = new File(r);
		
		Tabla tablavacia = new Tabla(1);
		
		
		FileReader fresults=null;
		BufferedReader brresults = null;
		try{
			fresults=new FileReader(archivoresults);
			brresults = new BufferedReader(fresults);
			int num = 0;
			int numAvions=0;
			String linea; //Obtenemos el contenido del archivo linea por linea
			while( (linea = brresults.readLine()) != null)
			{
				num++;
			}
			
			Tabla tablacargada = new Tabla(num);
			
			brresults.close();
			
			fresults=new FileReader(archivoresults);
			brresults = new BufferedReader(fresults);
	    	while( ( linea = brresults.readLine() ) != null )
	    	{
	    		int[] posicionn = {numAvions,-1};
	    		Gson gson = new Gson();
	    		Aircraft avion = new Aircraft();
	    		avion = gson.fromJson(linea, avion.getClass());
	    		tablacargada.setAvion(avion, posicionn);
	    		numAvions++;
	    	}
	    	
	    	return tablacargada;
			
		}
	    catch(Exception FileNotFoundException)
	    {
	    	System.out.println("No encuentro el archivo de resultados");
	    }
		
		return tablavacia;
		
	}
	
	public void clearTablaCargada() // Borra las lineas de la tablaCargada
	{
		int filas = model2.getRowCount();
		int i = 0;
		while(i<filas)
		{
			model2.removeRow(0);
			i++;
		}
	}
	
	public void setRutaGen(String r)
	{
		RutaPrograma = r;
		
		String[] ruta = r.split("\\.");
		int l = ruta.length;
		
		if(ruta[l-1].equals("txt") == false)
			textFieldCargar.setText(RutaPrograma + "ResultadosPrueba/ResultsFull.txt");
		
	}

}
