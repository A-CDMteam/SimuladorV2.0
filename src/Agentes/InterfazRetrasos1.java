package Agentes;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class InterfazRetrasos1 extends JFrame {

	private JPanel contentPane;
	private JTextField txtCusersxaviworkspacesimuladorvretrasostxt;
	
	ArrayList listaRetrasos = new ArrayList();
	
	JButton botonCargar;
	JButton btnAceptar;
	public boolean hayRetrasos = false;
	
	String ruta;// "C:/Users/Ivan/workspace/SimuladorV1.5/VuelosAerolinea.txt";
	
	InterfazGuardarRetrasos1 interGuardar;
	InterfazFormularioRetrasos1 interFormulario;
	private JTable tablaTipo1;
	private JTable tablaTipo2;
	private JTable tablaTipo3;
	private JTable tablaTipo4;
	DefaultTableModel modelo1;
	DefaultTableModel modelo2;
	DefaultTableModel modelo3;
	DefaultTableModel modelo4;
	JSpinner spinnerBorrar;
	
	int ct1 = -1; // Indica la fila selecionada en la tabla 1
	int ct2 = -1;
	int ct3 = -1;
	int ct4 = -1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazRetrasos1 frame = new InterfazRetrasos1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InterfazRetrasos1() {
		setTitle("Retrasos Simulaci\u00F3n");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1040, 657);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JPanel panel_2 = new JPanel();
		
		interGuardar = new InterfazGuardarRetrasos1();
		interGuardar.botonGuardar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
//				JOptionPane.showMessageDialog(null,"Se utilizarán los retrasos definidos");
				
				GuardarRetrasosTXT(interGuardar.textField.getText());
				
				dispose();
				
			}
		});
		btnAceptar = new JButton("Aceptar");
		btnAceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				JOptionPane.showMessageDialog(null,"Se utilizarán los retrasos definidos");
				
				interGuardar.setRuta(ruta);
				
				interGuardar.setVisible(true);
				
				GuardarRetrasosTXT(ruta + "Retrasos.txt");
				
				hayRetrasos = true;
				
				dispose();
				
			}
		});
		btnAceptar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JOptionPane.showMessageDialog(null,"No se cargarán los retrasos en la simulación");
				hayRetrasos = false;
				dispose();
				
			}
		});
		
		btnCancelar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap(669, Short.MAX_VALUE)
					.addComponent(btnAceptar, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addGap(2))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap(39, Short.MAX_VALUE)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAceptar))
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel label = new JLabel("Cargar desde archivo:");
		label.setFont(new Font("Verdana", Font.BOLD, 11));
		
		txtCusersxaviworkspacesimuladorvretrasostxt = new JTextField();
		txtCusersxaviworkspacesimuladorvretrasostxt.setText(" ");
		txtCusersxaviworkspacesimuladorvretrasostxt.setColumns(10);
		
		JLabel label_1 = new JLabel("Ruta:");
		
		botonCargar = new JButton("Cargar");
		botonCargar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				boolean correcto = cargarTXT(txtCusersxaviworkspacesimuladorvretrasostxt.getText());
				
				if(correcto)
				{
				botonCargar.setVisible(false);
				}
			}
		});

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 299, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
						.addComponent(txtCusersxaviworkspacesimuladorvretrasostxt, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
						.addComponent(label_1)
						.addComponent(botonCargar, Alignment.TRAILING))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 106, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addGap(1)
					.addComponent(label_1)
					.addGap(3)
					.addComponent(txtCusersxaviworkspacesimuladorvretrasostxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(botonCargar)
					.addContainerGap(12, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel label_2 = new JLabel("Administrar Retrasos:");
		label_2.setFont(new Font("Verdana", Font.BOLD, 14));
		
		JButton btnNuevo = new JButton("Nuevo");
		btnNuevo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					
				interFormulario.SetRuta(ruta);
				
				interFormulario.setVisible(true);
				
			}
		});
		btnNuevo.setFont(new Font("Verdana", Font.PLAIN, 12));
		
		interFormulario = new InterfazFormularioRetrasos1();
		interFormulario.btnAceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//System.out.println("LA INTERFAZ FORMULARIO HA APRETADO EL BOTÓN ACEPTAR");
				boolean hayretraso = interFormulario.definido;
				if(hayretraso)
				{
					String[] nuevoRetraso = (String[])interFormulario.getRetrasoDef();
					//Quitar "-" de los elementos vacios.
					if(nuevoRetraso[0].equals("1"))
					{
						int i=10;
						while(i<nuevoRetraso.length)
						{
							nuevoRetraso[i]="";
							i++;
						}
					}
					if(nuevoRetraso[0].equals("2"))
					{
						int i=6;
						while(i<nuevoRetraso.length)
						{
							nuevoRetraso[i]="";
							i++;
						}
					}
					if(nuevoRetraso[0].equals("3"))
					{
						int i=7;
						while(i<nuevoRetraso.length)
						{
							nuevoRetraso[i]="";
							i++;
						}
					}
					if(nuevoRetraso[0].equals("4"))
					{
						int i=7;
						while(i<nuevoRetraso.length)
						{
							nuevoRetraso[i]="";
							i++;
						}
					}
					
					
					setRetraso(nuevoRetraso);
					actualizarTablas(nuevoRetraso);
				}
			}
		});
		JLabel lblTabla = new JLabel("Tabla?");
		
		spinnerBorrar = new JSpinner();
		spinnerBorrar.setModel(new SpinnerNumberModel(1, 1, 4, 1));
		
		JButton btnBorrar = new JButton("Borrar");
		btnBorrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				int tabla = (int)spinnerBorrar.getValue();
				int ct = 0;
				if(tabla == 1)
				{
					ct = ct1;
				}
				if(tabla == 2)
				{
					ct = ct2;
				}
				if(tabla == 3)
				{
					ct = ct3;
				}
				if(tabla == 4)
				{
					ct = ct4;
				}
				
				BorrarLinea(tabla, ct);
				if(tabla == 1)
				{
					ct1 = -1;
				}
				if(tabla == 2)
				{
					ct2 = -1;
				}
				if(tabla == 3)
				{
					ct3 = -1;
				}
				if(tabla == 4)
				{
					ct4 = -1;
				}
				
			}
		});
		btnBorrar.setFont(new Font("Verdana", Font.PLAIN, 12));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 299, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(211, Short.MAX_VALUE)
					.addComponent(btnNuevo, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(211, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(10)
							.addComponent(lblTabla)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerBorrar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnBorrar, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addGap(32)
					.addComponent(btnNuevo)
					.addGap(130)
					.addComponent(btnBorrar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTabla)
						.addComponent(spinnerBorrar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JScrollPane scrollPane_3 = new JScrollPane();
		
		JLabel lblRetrasos = new JLabel("Retrasos:");
		lblRetrasos.setFont(new Font("Verdana", Font.BOLD, 14));
		
		JLabel lblTiporetrasoMultiavin = new JLabel("Tipo1 (Retraso MultiAvi\u00F3n -  situaci\u00F3n concreta)");
		
		JLabel lblTiporetrasoAvin = new JLabel("Tipo2 (Retraso Avi\u00F3n concreto - situaci\u00F3n concreta)");
		
		JLabel lblTiporetrasoAvin_1 = new JLabel("Tipo3 (Retraso Avi\u00F3n Concreto - Tiempo concreto)");
		
		JLabel lblTiporeduccin = new JLabel("Tipo 4 (Reducci\u00F3n Capacidad de pista)");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 1004, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(14)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
								.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
								.addComponent(scrollPane_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
								.addComponent(scrollPane_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
								.addComponent(lblRetrasos, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTiporeduccin, GroupLayout.PREFERRED_SIZE, 514, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTiporetrasoAvin_1, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTiporetrasoAvin, GroupLayout.PREFERRED_SIZE, 456, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTiporetrasoMultiavin, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(6)
							.addComponent(lblRetrasos, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
							.addGap(1)
							.addComponent(lblTiporetrasoMultiavin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTiporetrasoAvin)
							.addGap(1)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTiporetrasoAvin_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTiporeduccin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		tablaTipo4 = new JTable();
		tablaTipo4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ct4 = tablaTipo4.getSelectedRow(); //Guardamos el valor de la fila selecionada en la tabla
			}
		});
		modelo4 = new DefaultTableModel();
		tablaTipo4.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Hora Inicio", "Hora Final", "Hora Conocimiento", "Departures (op/h)", "Arribals (op/h)", "Comentarios"
			}
		) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		modelo4 = (DefaultTableModel) tablaTipo4.getModel();
		scrollPane_3.setViewportView(tablaTipo4);
		
		tablaTipo3 = new JTable();
		tablaTipo3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ct3 = tablaTipo3.getSelectedRow(); //Guardamos el valor de la fila selecionada en la tabla
			}
		});
		tablaTipo3.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID Avi\u00F3n", "Milestone Conocimiento", "Minutos Retraso", "Parametro (tiempo)", "Agente", "Comentarios"
			}
				) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		modelo3 = (DefaultTableModel) tablaTipo3.getModel();
		scrollPane_2.setViewportView(tablaTipo3);
		
		tablaTipo2 = new JTable();
		tablaTipo2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ct2 = tablaTipo2.getSelectedRow(); //Guardamos el valor de la fila selecionada en la tabla
			}
		});
		tablaTipo2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID Avi\u00F3n", "Milestone Conocimiento", "Minutos Retraso", "Situaci\u00F3n", "Comentarios"
			}
			) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		modelo2 = (DefaultTableModel) tablaTipo2.getModel();
		scrollPane_1.setViewportView(tablaTipo2);
		
		tablaTipo1 = new JTable();
		tablaTipo1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				ct1 = tablaTipo1.getSelectedRow(); //Guardamos el valor de la fila selecionada en la tabla
				
			}
		});
		tablaTipo1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaTipo1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Hora Inicio", "Hora Final", "H. Conocimiento", "Minutos Retraso", "Situaci\u00F3n", "Milestone", "AO Filtro", "Modelos Filtro", "Comentarios"
			}
		) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		modelo1 = (DefaultTableModel) tablaTipo1.getModel();
		scrollPane.setViewportView(tablaTipo1);
		
		contentPane.setLayout(gl_contentPane);
	}
	
	public void setRuta(String r) // Se define la ruta desde otra interfaz
	{
		ruta = r;
		
		txtCusersxaviworkspacesimuladorvretrasostxt.setText(r + "Retrasos.txt");
		
	}
	
	private void setRetraso(String[] retraso){
		
		int posicion = listaRetrasos.indexOf(retraso);
		
		if(posicion == -1)
			listaRetrasos.add(retraso);
	}
	
	private String[] getRetraso(int posicion)
	{
		String[] retraso = (String[]) listaRetrasos.get(posicion);
		
		return retraso;
		
	}

	public Object[] pasarALinea(String[] re)
	{
		// Definimos la longitud de la linea según el tipo de retraso
		int len = 0;
		if(re[0].equals("1"))
			len = 9;
		if(re[0].equals("2"))
			len = 5;
		if(re[0].equals("3"))
			len = 6;
		if(re[0].equals("4"))
			len = 6;
		
		// Definimos el objeto y lo rellenamos
		Object[] fila = new Object[len];
		
		int c = 1;
		while(c<=len)
		{
			fila[c-1] = re[c];
			
			c++;
		}
				
		return fila;
	}

	public void actualizarTablas(String[] re)
	{
		// Pasamos el retraso a fila
		Object[] fila = pasarALinea(re);
		
		// Decidimos a que tabla va el retraso y lo añadimos a la tabla
		if(re[0].equals("1"))
		{
			modelo1.addRow(fila);
		}
		if(re[0].equals("2"))
		{
			modelo2.addRow(fila);
		}
		if(re[0].equals("3"))
		{
			modelo3.addRow(fila);
		}
		if(re[0].equals("4"))
		{
			modelo4.addRow(fila);
		}
	}

	public boolean cargarTXT(String ruta)
	{
		boolean correcto;
		ArrayList listaTipo1 = new ArrayList();
		ArrayList listaTipo2 = new ArrayList();
		ArrayList listaTipo3 = new ArrayList();
		ArrayList listaTipo4 = new ArrayList();
		
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    try
	    {
	    	fr = new FileReader( archivo );
        	br = new BufferedReader( fr );
        	String linea;
        	while((linea = br.readLine())!=null)
	    	{
        		String[] Retraso = linea.split(" ");
        		String[] AddRetraso;
        		String Coment = "";
        		if(Retraso[0].equals("1"))
        		{
        			AddRetraso=new String[10];
        			int i=0;
        			while(i<9)
        			{
        				AddRetraso[i]=Retraso[i];
        				i++;
        			}
        			while(i<Retraso.length)
        			{
        				Coment=Coment + Retraso[i]+" ";
        				i++;
        			}
        			AddRetraso[9]=Coment;
        			listaTipo1.add(AddRetraso);
        		}
        		if(Retraso[0].equals("2"))
        		{
        			AddRetraso=new String[6];
        			int i=0;
        			while(i<5)
        			{
        				AddRetraso[i]=Retraso[i];
        				i++;
        			}
        			while(i<Retraso.length)
        			{
        				Coment=Coment + Retraso[i]+" ";
        				i++;
        			}
        			AddRetraso[5]=Coment;
        			listaTipo2.add(AddRetraso);
        		}
        		if(Retraso[0].equals("3"))
        		{
        			AddRetraso=new String[7];
        			int i=0;
        			while(i<6)
        			{
        				AddRetraso[i]=Retraso[i];
        				i++;
        			}
        			while(i<Retraso.length)
        			{
        				Coment=Coment + Retraso[i]+" ";
        				i++;
        			}
        			AddRetraso[6]=Coment;
        			listaTipo3.add(AddRetraso);
        		}
        		if(Retraso[0].equals("4"))
        		{
        			AddRetraso=new String[7];
        			int i=0;
        			while(i<6)
        			{
        				AddRetraso[i]=Retraso[i];
        				i++;
        			}
        			while(i<Retraso.length)
        			{
        				Coment=Coment + Retraso[i]+" ";
        				i++;
        			}
        			AddRetraso[6]=Coment;
        			listaTipo4.add(AddRetraso);
        		}
	    	}        	
        	br.close();
        	
        	ArrayList listaCargados = new ArrayList(); // lista de retrasos que luego se añadira a las tablas
        	
        	// Añadimos los retrasos a la lista de retrasos de la interfaz
        	
        		// Para añadir los retrasos a la lista hay que pasarlos a String[] de 10 posiciones
        	int c = 0;
        	
        	while(c < listaTipo1.size())
        	{
        		String[] re  = adaptarRetrasoLista((String[])listaTipo1.get(c));
        		
        		listaRetrasos.add(re);
        		listaCargados.add(re);
        		c++;
        	}
        	c=0;
        	while(c < listaTipo2.size())
        	{
        		String[] re  = adaptarRetrasoLista((String[])listaTipo2.get(c));
        		
        		listaRetrasos.add(re);
        		listaCargados.add(re);
        		c++;
        	}
        	c=0;
        	while(c < listaTipo3.size())
        	{
        		String[] re  = adaptarRetrasoLista((String[])listaTipo3.get(c));
        		
        		listaRetrasos.add(re);
        		listaCargados.add(re);
        		c++;
        	}
        	c=0;
        	while(c < listaTipo4.size())
        	{
        		String[] re  = adaptarRetrasoLista((String[])listaTipo4.get(c));
        		
        		listaRetrasos.add(re);
        		listaCargados.add(re);
        		c++;
        	}
        	
        	pintarTablas(listaCargados);
        	correcto = true;
        	
	    }
	    catch(Exception FileNotFoundException)
	    {
	    	JOptionPane.showMessageDialog(null,"No encuentro el archivo con los retrasos. Supondremos que no existen restrasos");
	    	correcto = false;
	    }
	    return correcto;
	}

	public void pintarTablas(ArrayList lista) //Pinta todos los retrasos (listaRetrasos entera) en las diferentes tablas.
	{
		int i = 0;
		int len = lista.size();
		String[] linea;
		
		while(i<len)
		{
			linea = (String[]) lista.get(i);
			actualizarTablas(linea);
			i++;
		}
	}
	
	private String[] adaptarRetrasoLista(String[] re)
	{
		/* Vectores de tipo de retraso:
		 * 
		 * Tipo 1:
		 * [tipo, H.Inicio, H.Final, H.Conocimiento, MinRetraso, X, Milestone En VentanaTiempo, AO filtro, Modelo Filtro, Comentarios]
		 * Tipo 2:
		 * [tipo, ID Avion, MilestoneConocimiento, MinRetraso, X, Comentaris]
		 * Tipo 3:
		 * [tipo, IDAvion, MilestoneConocimiento, MinRetraso, Parametro, Agente, Comentarios]
		 * Tipo 4:
		 * [tipo, H.Inicio, H.Final, H.Coneixement, Departures, Arribals, Comentarios]
		 * */
		
		// Queremos pasarlos todos a un vector de Strings con lenght determinado (para poder usarlos en la misma arraylist)

		// Utilizaremos un String con un lenght de 13. De esta manera todos los tipos de retrasos podran guardar sus variables. 
				//*Las variables que un tipo de retraso no tenga se dejaran como "-" en la posición que toque del vector.

			String[] retraso = new String[10];
			
			retraso = generarVec(10); // Generamos el vector con las "-" en las posiciones que no tendrán valor
			
			String tipo = re[0];
			int c = 0;

			while(c<re.length)
			{
				retraso[c] = re[c];
				c++;
			}
			
		return retraso;
			 
	}
	
	public String[] generarVec(int l) // Genera un vector de Strings con "-" en todas las posiciones.
	{
		String[] r = new String[l];
		
		int c = 0;
		while(c<l)
		{
			r[c] = "-";
			c++;
		}
		return r;
	}

	public void GuardarRetrasosTXT(String ruta)
	{
		FileWriter ficheroRe;
		try {
			ficheroRe = new FileWriter(ruta);        
			
			PrintWriter pwr = new PrintWriter(ficheroRe);
			
			String fila = "";
			int c = 0;
			while(c<listaRetrasos.size())
			{
				fila = pasarAString((String[])listaRetrasos.get(c));
				pwr.println(fila);
				c++;
			}
			
			ficheroRe.close();
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error al guardar los retrasos en la ruta especificada");
		}
	}
	public String pasarAString(String[] vec)
	{
		if(vec[0].equals("1"))
		{
			int i=10;
			while(vec.length>i)
			{
				vec[i]="";
				i++;
			}
		}
		if(vec[0].equals("2"))
		{
			int i=6;
			while(vec.length>i)
			{
				vec[i]="";
				i++;
			}
		}
		if(vec[0].equals("3"))
		{
			int i=7;
			while(vec.length>i)
			{
				vec[i]="";
				i++;
			}
		}
		if(vec[0].equals("4"))
		{
			int i=7;
			while(vec.length>i)
			{
				vec[i]="";
				i++;
			}
		}
		int c = 0;
		String s = "";
		while(c < vec.length)
		{
			s = s + vec[c] + " ";
			c++;
		}
		
		return s;
	}
	public void BorrarLinea(int tabla, int linea)
	{
		if(linea == -1)
		{
			JOptionPane.showMessageDialog(null,"No has selecionado nada en la tabla " + tabla);
		}
		else
		{
			if(tabla == 1)
			{
				modelo1.removeRow(linea);
			}
			if(tabla == 2)
			{
				modelo2.removeRow(linea);
			}
			if(tabla == 3)
			{
				modelo3.removeRow(linea);
			}
			if(tabla == 4)
			{
				modelo4.removeRow(linea);
			}
			
		}
	}
}

