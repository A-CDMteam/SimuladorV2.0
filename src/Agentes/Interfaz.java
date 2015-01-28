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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import java.awt.Font;

public class Interfaz extends JFrame {

	private JPanel contentPane;
	public JTable table;
	DefaultTableModel model;
	int[] aviones = {0,0,0}; // Numero de aviones Ariborne y aviones Airport
	
	JLabel lblAirborneNumber;
	JLabel lblOnAirportNumber;
	JLabel lblTotalAircraftsNumber;
	
	boolean parar = false;
	
	
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

	public Interfaz() {
		
		setTitle("A-CDM Simulator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 590);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setVisible(true);
		
		JScrollPane scrollPane = new JScrollPane();
		DefaultTableModel modelo = new DefaultTableModel(); // Crear la tabla
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
		
		JLabel lblAirborne = new JLabel("Airborne: ");
		
		JLabel lblOnAirport = new JLabel("On Airport: ");
		
		JLabel lblTotalAircrafts = new JLabel("Total aircrafts on A-CDM: ");
		
		lblAirborneNumber = new JLabel("" +aviones[1]);
		
		lblOnAirportNumber = new JLabel("" +aviones[2]);
				
		lblTotalAircraftsNumber = new JLabel(""+aviones[0]);
		
		JButton btnTerminarSimulacin = new JButton("Terminar Simulaci\u00F3n");
		btnTerminarSimulacin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				parar = true;
				dispose();
			}
		});
		btnTerminarSimulacin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblAirborne)
										.addComponent(lblOnAirport))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblOnAirportNumber)
										.addComponent(lblAirborneNumber)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblTotalAircrafts)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblTotalAircraftsNumber, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 401, Short.MAX_VALUE)
									.addComponent(btnTerminarSimulacin)))
							.addGap(10))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(47)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAirborne)
						.addComponent(lblAirborneNumber))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOnAirport)
						.addComponent(lblOnAirportNumber))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTotalAircrafts)
						.addComponent(lblTotalAircraftsNumber))
					.addGap(18)
					.addComponent(btnTerminarSimulacin, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
			
		
		
		scrollPane.setViewportView(table);
		contentPane.setLayout(gl_contentPane);
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
			AircraftsNumber();
		}
		else{
			ActualizarValores(avion, fila);	
			AircraftsNumber();
		}
	}
	
	public void ActualizarValores(Aircraft avion, Object[] datos){ // El avion ya esta en la tabla, ha cambiado algun valor
		
		int numerofila = posicionenlatabla(datos);
		
		if(numerofila < model.getRowCount())
		{
			model.removeRow(numerofila);
		}
		
		model.insertRow(numerofila, datos);
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
			this.lblAirborneNumber.setText(""+aviones[1]);
			this.lblOnAirportNumber.setText(""+aviones[2]);
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
}
