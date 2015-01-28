package Agentes;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.swing.SwingConstants;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import Agentes.Aircraft;
import Agentes.Airport.RecepcionAvion;

import com.google.gson.Gson;
import java.awt.Window.Type;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class InterfazSimulatorInput extends JFrame {

	private JPanel contentPane;
	public JTextField rutaArchivoTextGuardar;


	boolean cargarresultados;
	int TaxiInTime;
	int TaxiOutTime;
	int VelocidadSimulacion;
	int NumeroAvionesSimulacion;
	int TiempoEstimadoSimulacion;
	String Aerolineas;
	String RUTA;
	String Aeropuerto = "...";
	JLabel lbl_Aeropuerto = new JLabel(Aeropuerto);
	JLabel lbl_Aerolineas = new JLabel("...");
	
	final JPanel panelDatosInput;
	final JLabel lbl_NumeroAviones;
	
	boolean cargado = false;
	boolean guardado = false;
	boolean parametrosDefinidos = false;
	boolean parametrosDDR2 = false;
	boolean inicioPrograma = false;
	
	JPanel panel;
	InterfazRetrasos1 interRetrasos;
	
	String parametros;
	Aircraft avionparametros;
	Aircraft avionDDR2 = new Aircraft();
	private JTextField txtINPUTtxt;
	private JTextField txtCusersivanworkspacesimuladorvresultstxt;
	private JPanel panel_Resultados;
	private JButton btnGuardar;
	private JLabel lblProcesandoDatosDdr;
	private JLabel lblQuieresAnalizarResultados;
	private JButton btnAnalizarResultados;
	private JSpinner spinnerTaxiIn;
	private JSpinner spinnerTaxiOut;
	private JSpinner spinnerVelSim;
	private JLabel lblNota;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazSimulatorInput frame = new InterfazSimulatorInput();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InterfazSimulatorInput() {
		setResizable(false);
		setBackground(Color.LIGHT_GRAY);
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Ivan\\workspace\\SimuladorV1.3-GRAFICO\\airport.bmp"));
		
		setTitle("A-CDM Simulator Input");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 485, 634);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		final JPanel panelDefinicionParametros = new JPanel();
		panelDefinicionParametros.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		panelDatosInput = new JPanel();
		
		ButtonGroup radiobuttons = new ButtonGroup();
		
		JLabel label_9 = new JLabel("Num Aviones: ");
		
		lbl_NumeroAviones = new JLabel("...");
		
		JLabel label_11 = new JLabel("Aerolineas: ");
		
		JLabel lblAeropuerto = new JLabel("Aeropuerto:");
		
		panelDatosInput.setVisible(false);
		

		GroupLayout gl_panelDatosInput = new GroupLayout(panelDatosInput);
		gl_panelDatosInput.setHorizontalGroup(
			gl_panelDatosInput.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDatosInput.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelDatosInput.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelDatosInput.createSequentialGroup()
							.addComponent(label_9)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_NumeroAviones, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
						.addGroup(gl_panelDatosInput.createSequentialGroup()
							.addComponent(label_11)
							.addGap(18)
							.addComponent(lbl_Aerolineas, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
						.addGroup(gl_panelDatosInput.createSequentialGroup()
							.addComponent(lblAeropuerto)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lbl_Aeropuerto, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panelDatosInput.setVerticalGroup(
			gl_panelDatosInput.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDatosInput.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelDatosInput.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_9)
						.addComponent(lbl_NumeroAviones, GroupLayout.PREFERRED_SIZE, 9, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDatosInput.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_11)
						.addComponent(lbl_Aerolineas))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDatosInput.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAeropuerto)
						.addComponent(lbl_Aeropuerto, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(32, Short.MAX_VALUE))
		);
		panelDatosInput.setLayout(gl_panelDatosInput);
		
		panel_Resultados = new JPanel();
		panel_Resultados.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_Resultados.setVisible(false);
		
		lblQuieresAnalizarResultados = new JLabel("\u00BFQuiere analizar resultados de una simulaci\u00F3n previa?");
		lblQuieresAnalizarResultados.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		btnAnalizarResultados = new JButton("Analizar Resultados");
		btnAnalizarResultados.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAnalizarResultados.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				panel_Resultados.setVisible(true);
				lblQuieresAnalizarResultados.setVisible(false);
				btnAnalizarResultados.setVisible(false);
				}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_Resultados, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblQuieresAnalizarResultados)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAnalizarResultados, GroupLayout.PREFERRED_SIZE, 117, Short.MAX_VALUE))
						.addComponent(panelDatosInput, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
						.addComponent(panelDefinicionParametros, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelDefinicionParametros, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelDatosInput, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblQuieresAnalizarResultados)
						.addComponent(btnAnalizarResultados))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_Resultados, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
					.addGap(34))
		);
		
		JLabel lblAnalizarResultados = new JLabel("Analizar Resultados:");
		lblAnalizarResultados.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblRutaArchivo = new JLabel("Ruta archivo:");
		
		txtCusersivanworkspacesimuladorvresultstxt = new JTextField();
		txtCusersivanworkspacesimuladorvresultstxt.setText("C:\\Users\\Ivan\\workspace\\SimuladorV1.7\\ResultadosPrueba\\ResultsFull.txt");
		txtCusersivanworkspacesimuladorvresultstxt.setColumns(10);
		
		JButton btnCargarResultados = new JButton("Cargar Resultados");
		btnCargarResultados.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				boolean valido=CargarArchivoresults (txtCusersivanworkspacesimuladorvresultstxt.getText());
				if(valido)
				{
					cargarresultados = true;
					dispose();
				}
			}
		});
		GroupLayout gl_panel_Resultados = new GroupLayout(panel_Resultados);
		gl_panel_Resultados.setHorizontalGroup(
			gl_panel_Resultados.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Resultados.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_Resultados.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Resultados.createSequentialGroup()
							.addComponent(lblAnalizarResultados)
							.addGap(139))
						.addGroup(gl_panel_Resultados.createSequentialGroup()
							.addGroup(gl_panel_Resultados.createParallelGroup(Alignment.LEADING)
								.addComponent(txtCusersivanworkspacesimuladorvresultstxt, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
								.addComponent(lblRutaArchivo)
								.addComponent(btnCargarResultados, Alignment.TRAILING))
							.addContainerGap())))
		);
		gl_panel_Resultados.setVerticalGroup(
			gl_panel_Resultados.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Resultados.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAnalizarResultados)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblRutaArchivo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtCusersivanworkspacesimuladorvresultstxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnCargarResultados)
					.addContainerGap(20, Short.MAX_VALUE))
		);
		panel_Resultados.setLayout(gl_panel_Resultados);
		
		JLabel lblParametros = new JLabel("Parametros Simulaci\u00F3n");
		lblParametros.setFont(new Font("Tahoma", Font.BOLD, 16));
		
		JLabel lblParametrosAeropuerto = new JLabel("Parametros Aeropuerto");
		lblParametrosAeropuerto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblTaxiInTime = new JLabel("Taxi In Time:");
		
		JLabel lblTaxiOutTime = new JLabel("Taxi Out Time:");
		
		JLabel lblParametrosPrograma = new JLabel("Parametros  Programa");
		lblParametrosPrograma.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblVelocidadSimulacin = new JLabel("Velocidad Simulaci\u00F3n");
		
		JLabel lblMin = new JLabel("min");
		
		JLabel lblMin_1 = new JLabel("min");
		
		JLabel lblRutaArchivoGenerado = new JLabel("Ruta Archivo Airport:");
		
		rutaArchivoTextGuardar = new JTextField();
		rutaArchivoTextGuardar.setColumns(10);
		rutaArchivoTextGuardar.setText("C:\\Users\\xavi\\workspace\\SimuladorV2.0\\Airport.txt");
		
		
		btnGuardar = new JButton("Cargar");
		btnGuardar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				
				rutatxtbox();
				boolean valido = CargarArchivoSO6(rutaArchivoTextGuardar.getText(),txtINPUTtxt.getText());
				String parametrosAeropuerto;
				if(valido)
					parametrosAeropuerto= CargarArchivoAirport(rutaArchivoTextGuardar.getText());
				else
					parametrosAeropuerto="error";
				if(parametrosAeropuerto.equals("error"))
				{
					//System.out.println("");
				}
				else
				{
					RUTA = txtINPUTtxt.getText()+" " + RUTA;
					System.out.println(RUTA);
					// Cargamos los datos del txt del aeropuerto
						
					avionDDR2.setIncidencias(parametrosAeropuerto);
					
					parametrosDDR2 = true;
					
					panel_Resultados.setVisible(false);
					
					btnGuardar.setVisible(false);
					lblProcesandoDatosDdr.setVisible(true);
					lblQuieresAnalizarResultados.setVisible(false);
					btnAnalizarResultados.setVisible(false);
					lblNota.setVisible(false);
					
					bloquearParametros();
				}
				
			}
		});
		
		JButton buttonIniciarGuardado = new JButton("Iniciar Simulaci\u00F3n");
		buttonIniciarGuardado.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(guardado){
					
					avionparametros = AvionParametros(parametros);
					System.out.println("Iniciando simulacion...");
					inicioPrograma = true;
					dispose();
					
					
				}
				else{
					JOptionPane.showMessageDialog(null,"Define y guarda los parametros para iniciar la simulación");
				}
			}
			
		});

		buttonIniciarGuardado.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblS = new JLabel("ms");
		
		JLabel lblNombreArchivoDdr = new JLabel("Nombre archivo DDR2: ");
		
		txtINPUTtxt = new JTextField();
		txtINPUTtxt.setText("SO6.txt");
		txtINPUTtxt.setColumns(10);
		
		panel = new JPanel();
		panel.setVisible(false);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		lblProcesandoDatosDdr = new JLabel("... PROCESANDO DATOS DDR2 ...");
		lblProcesandoDatosDdr.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblProcesandoDatosDdr.setVisible(false);
		
		spinnerTaxiIn = new JSpinner();
		spinnerTaxiIn.setModel(new SpinnerNumberModel(new Integer(15), new Integer(1), null, new Integer(1)));
		
		spinnerTaxiOut = new JSpinner();
		spinnerTaxiOut.setModel(new SpinnerNumberModel(new Integer(20), new Integer(1), null, new Integer(1)));
		
		spinnerVelSim = new JSpinner();
		spinnerVelSim.setModel(new SpinnerNumberModel(new Integer(500), new Integer(250), null, new Integer(100)));
		
		lblNota = new JLabel("Nota: Ambos archivos input deben compartir carpeta");
		lblNota.setFont(new Font("Tahoma", Font.PLAIN, 9));
		GroupLayout gl_panelDefinicionParametros = new GroupLayout(panelDefinicionParametros);
		gl_panelDefinicionParametros.setHorizontalGroup(
			gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addComponent(buttonIniciarGuardado, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panelDefinicionParametros.createSequentialGroup()
							.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
									.addGap(10)
									.addComponent(lblVelocidadSimulacin)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinnerVelSim, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblS))
								.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING, false)
									.addComponent(lblRutaArchivoGenerado)
									.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
										.addComponent(lblNombreArchivoDdr)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(txtINPUTtxt, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
									.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
										.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING, false)
											.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
												.addGap(10)
												.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
													.addComponent(lblTaxiOutTime)
													.addComponent(lblTaxiInTime))
												.addGap(18)
												.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.TRAILING)
													.addComponent(spinnerTaxiIn, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
													.addComponent(spinnerTaxiOut, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)))
											.addComponent(lblParametrosAeropuerto))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
											.addComponent(lblMin)
											.addComponent(lblMin_1)))
									.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
										.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.TRAILING)
											.addComponent(lblProcesandoDatosDdr)
											.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
												.addComponent(lblParametrosPrograma)
												.addGap(155))
											.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
													.addGap(10)
													.addComponent(lblNota))
												.addComponent(rutaArchivoTextGuardar, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnGuardar, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))))
							.addGap(0, 0, Short.MAX_VALUE))
						.addComponent(lblParametros))
					.addContainerGap())
		);
		gl_panelDefinicionParametros.setVerticalGroup(
			gl_panelDefinicionParametros.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDefinicionParametros.createSequentialGroup()
					.addGap(16)
					.addComponent(lblParametros)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblParametrosAeropuerto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTaxiInTime)
						.addComponent(lblMin)
						.addComponent(spinnerTaxiIn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTaxiOutTime)
						.addComponent(lblMin_1)
						.addComponent(spinnerTaxiOut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblParametrosPrograma)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblVelocidadSimulacin)
						.addComponent(spinnerVelSim, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblS))
					.addGap(18)
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNombreArchivoDdr)
						.addComponent(txtINPUTtxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblRutaArchivoGenerado)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelDefinicionParametros.createParallelGroup(Alignment.BASELINE)
						.addComponent(rutaArchivoTextGuardar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnGuardar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNota)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblProcesandoDatosDdr)
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
					.addComponent(buttonIniciarGuardado)
					.addContainerGap())
		);
		
		interRetrasos = new InterfazRetrasos1();
		
		JButton btnAdministrarRetrasos = new JButton("Administrar Retrasos");
		btnAdministrarRetrasos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				String param = avionparametros.getIncidencias();
				String[] listaparametros = param.split("\n");
				String ruta = valorparametro(listaparametros[3]);
				
				interRetrasos.setRuta(ruta);
				interRetrasos.setVisible(true);
			}
		});
		
		JLabel lblRetrasosEnLa = new JLabel("Retrasos en la Simulaci\u00F3n:");
		lblRetrasosEnLa.setFont(new Font("Tahoma", Font.BOLD, 11));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblRetrasosEnLa, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
					.addComponent(btnAdministrarRetrasos)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRetrasosEnLa)
						.addComponent(btnAdministrarRetrasos))
					.addContainerGap(22, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		panelDefinicionParametros.setLayout(gl_panelDefinicionParametros);
		contentPane.setLayout(gl_contentPane);
		
		panelDefinicionParametros.setVisible(true);
	}

	public String definirParametros(){	// Creación del String de parametros de entrada a partir de la interfaz	
		
		String taxiIn = "TaxiIntime" + "\t" + spinnerTaxiIn.getValue();
		String taxiOut = "TaxiOutTime"+ "\t" + spinnerTaxiOut.getValue();
		String velMilestones = "VelocidadMilestones" + "\t" + spinnerVelSim.getValue();
		VelocidadSimulacion = (int)spinnerVelSim.getValue();
		// Sacamos la ruta de la carpeta donde estan los archivos input
		String[] rutaarchivo = rutaArchivoTextGuardar.getText().split("/");
		
		String rutaar = rutaArchivoTextGuardar.getText();
		
		if(rutaarchivo.length == 1){ // Detectamos si se ha introducido la ruta con "/" o con "\"
			rutaarchivo = rutaArchivoTextGuardar.getText().split("\\\\");		
		}
			// Montamos la ruta a la carpeta
		String rutacarpeta = "";
		
		int i = 0;
		while(i < rutaarchivo.length-1){
			rutacarpeta = rutacarpeta + rutaarchivo[i]+"/";
			i++;
		}

		String rutaInput = "RutaInput" + "\t" + rutacarpeta;
		
		RUTA = rutacarpeta;
		
		String txt = taxiIn + "\n" + taxiOut + "\n" + velMilestones + "\n" + rutaInput + "\n";
				
		return txt;
	}
	
	public boolean CargarArchivoresults (String ruta) //Comprueba que existe el archivo con los resultados 
	{
		boolean valido = true;
	
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    	    
	    try{
	    	 fr = new FileReader(archivo);
		     br = new BufferedReader(fr);
		       
		     
		    // Contamos el numero de variables que tenemos
		    while (br.readLine() != null)
	        	{
	        	}
		    br.close();
		    }
		    
		catch(Exception filenptfoundexception)
		{
			System.out.println("El documento de resultados no existe");
		    JOptionPane.showMessageDialog(null,"Error: el documento de resultados no existe");
		    return false;
		}
		
		return valido;
		
		
	}
	
	public boolean CargarArchivoSO6 (String ruta, String So6)
	{
		boolean valido = true;
		// Abrimos el archivo con la información SO6 para assegurar que existe
		String[] rutas=ruta.split("\\\\");
		int carpetas = rutas.length;
		rutas[carpetas-1]=So6;
		int i=1;
		ruta=rutas[0];
		while(i<carpetas)
		{
			ruta=ruta+"\\\\" + rutas[i];
			i++;
		}
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    	    
	    try{
	    	 fr = new FileReader(archivo);
		     br = new BufferedReader(fr);
		       
		     
		    // Contamos el numero de variables que tenemos
		    while (br.readLine() != null)
	        	{
	        	}
		    br.close();
		    }
		    
		catch(Exception filenptfoundexception)
		{
			System.out.println("Error al cargar el archivo con la información del DDR2");
		    JOptionPane.showMessageDialog(null,"Error: el documento DDR2 no existe");
		    return false;
		}
		
		
		
		return valido;
		
		
	}
	
	public String CargarArchivoAirport(String ruta){
		
		// Abrimos el archivo con los parametros
		boolean existeArchivo = true;
		String parametros = "";		
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    int num = 0;
	    
	    try{
	    	 fr = new FileReader(archivo);
		     br = new BufferedReader(fr);
		       
		     
		    // Contamos el numero de variables que tenemos
		    while (br.readLine() != null)
	        	{
	        		num ++;
	        	}
		    br.close();
		    }
		    
		catch(Exception filenptfoundexception)
		{
			System.out.println("Error al cargar el archivo de Parametros del Aeropuerto");
		    JOptionPane.showMessageDialog(null,"Error al cargar el archivo de Parametros del Aeropuerto");
		    existeArchivo = false;
		    return "error";
		}
		if(num!=8)
		{
			JOptionPane.showMessageDialog(null,"el archivo Aeropuerto no es correcto");
		    existeArchivo = false;
		    return "error";
		}
	    // Lectura
	    if(existeArchivo)
	    {
		    try
		    {
		    	 fr = new FileReader(archivo);
			     br = new BufferedReader(fr);
			     
			     for (int i = 0; i < num; i++)
			        {
			            String line = br.readLine();
			            
			            parametros = parametros + line + "\n";
			            if(i == 0)
			            {
			            	lbl_Aeropuerto.setText(line);
			            }
			            if(i == 1)
			            {
			            	String[] aerolineas = line.split("\t");
			            	int cont = 0;
			            	String aero = "";
			            	while(cont < aerolineas.length)
			            	{
			            		if(cont == 0)
			            			aero = aerolineas[cont];
			            		else
			             			aero = aero +  "-" + aerolineas[cont];
			            	
			            		cont ++;
			            	}
			            	lbl_Aerolineas.setText(aero);
			            }
			        }		 
		    }
		    catch(Exception e){
		    	System.out.println("Error al leer los parametros");
		    	JOptionPane.showMessageDialog(null,"Error al leer los parametros \n" +
		    			"Error en el formato");	
		    	return "error";
		    }
	    }	    
		return parametros;
	}
	
	public int guardarArchivo(String ruta, String parametros) throws IOException{ //Guardado del String de la interfaz en un txt
		FileWriter fichero = new FileWriter(ruta);
		PrintWriter pw = new PrintWriter(fichero);
		
		String[] listaparametros = parametros.split("\n");
		
		try{
			int i = 0;
			while(i<listaparametros.length){
				
				pw.println(listaparametros[i]);
				i++;
			}
			
			pw.close();
		}
		catch(Exception e){
			
			return -1;
		}
		return 0;
	}
	
	public String cargarArchivo(String ruta){ // Cargamos los parametros de un txt a un string
		
		boolean existeArchivo = true;
		String parametros = "";
		
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    int num = 0;
	    
	    try{
	    	 fr = new FileReader(archivo);
		     br = new BufferedReader(fr);
		       
		    // Contamos el numero de variables que tenemos
		    while (br.readLine() != null)
	        	{
	        		num ++;
	        	}
		    br.close();
		    }
		    
		    catch(Exception filenptfoundexception){
		    	System.out.println("Error al cargar el archivo de Parametros de Simulación");
		    	JOptionPane.showMessageDialog(null,"Error al cargar el archivo de Parametros de Simulación");
		    	existeArchivo=false;
		    	
		    }
		    // Lectura
	    if(existeArchivo){  // Los unicos parametros que debemos cargar del archivo son los que se muestran en la interfaz, ya que los datos de la ruta y los paramtros del aeropuerto pueden ser diferentes al hacer la nueva simulación
		    try{
		    	 fr = new FileReader(archivo);
			     br = new BufferedReader(fr);
			     
			     for (int i = 0; i < 3; i++) // Los parametros que recojemos son los 3 primeros
			        {
			            String line = br.readLine();
			            
			            parametros = parametros + line + "\n";
			        }
		   
		    // Guardado de la ruta donde se encuentra este archivo (y los demas)
			    
			 // Sacamos la ruta de la carpeta donde estan los archivos input
				String[] rutaarchivo = ruta.split("/");
				
				if(rutaarchivo.length == 1){ // Detectamos si se ha introducido la ruta con "/" o con "\"
					// rutaarchivo = rutaArchivoTextGuardar.getText().split("\\");    // No funciona!!!
					
					rutaarchivo = ruta.split("\\\\");
					
				}
					// Montamos la ruta a la carpeta
				String rutacarpeta = "";
				
				int i = 0;
				while(i < rutaarchivo.length-1){
					rutacarpeta = rutacarpeta + rutaarchivo[i]+"/";
					i++;
				}
				
				parametros = parametros +  "ruta_archivos\t" + rutacarpeta + "\n";
				
				RUTA = rutacarpeta;
		    }
		    catch(Exception e){
		    	System.out.println("Error al leer los parametros");
		    	JOptionPane.showMessageDialog(null,"Error al leer los parametros \n" +
		    			"Error en el formato");		    	
		    }
	    }
		
	    //System.out.println(parametros);
	    return parametros;
		
	}
	
	public String valorparametro(String parametro){
		
		String[] parametroyvalor = parametro.split("\t");
		
		String valor = parametroyvalor[1];
		
		return valor;			
	}

	public Aircraft AvionParametros(String parametros){
		
		Aircraft avionparametros = new Aircraft();
		
		avionparametros.setId("Avion Parametros");
		
		avionparametros.setIncidencias(parametros);
		
		avionparametros.setOpcionMensaje(3); // Los agentes entenderan el mensaje como el de añadir los parametros de la simulacion.
		
		return avionparametros;
	}
	
	public void mensajeAviso(String aviso){
		
		JOptionPane.showMessageDialog(null,aviso);
		
	}
	
	public int avionesInput(String ruta){
		
		String rutaarchivo = ruta+"INPUT.txt";
		File archivo = new File(rutaarchivo);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    //Cadena de texto donde se guardara el contenido del archivo
	    int count = 0;
	    
	    ArrayList aviones = new ArrayList();

	    try
	    {
	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	        
        String linea = br.readLine();
       
        while (linea != null) //Recorrem el fitxer per saber quants avions tenim
        {
        	String[] l = linea.split("\t");
 
        	if(!aviones.contains(l[1]))
        	{	
        		aviones.add(l[1]);
        		count = count +1;
        	}
        	
            linea = br.readLine();
            
        }
        
        	br.close();
	    }
	    catch(Exception filenotfoundexception){
	    	System.out.println("No encuentro el archivo de Input");
	    	mensajeAviso("Error al leer el archivo de Input\n *Todos los archivos tienen que estar en la misma carpeta");
	    }
		
		return count;
	}

	// Funciones de Simulator Input y agentes --> Pruebas todo funcionando sin las comunicaciones
	
	public String rutaArchivos(Aircraft avion){ // Agente: SimulatorInput -> saca la rutade los archivos del avion parametros
		
		String[] incidencias = avion.getIncidencias().split("\n");
		String ubicacion = valorparametro(incidencias[3]);
		
		return ubicacion;
	}
	public String[] definirRutas(Aircraft avion){ // Agente: SimulatorInput -> define las rutas a los diferentes archivos input (no cambiar la del agente)
			// En el Agente SimulatorInput esta es una funcion VOID, aqui hago que devuelva un String[] porque no tiene definidas las variables (no cambiar la del agente)
		String ruta = rutaArchivos(avion);
		
		String rutaInput = ruta+"INPUT.txt";
		String rutaFP = ruta+"FlightPlans.txt";
		String rutaMilestones = ruta+"TriggerCDM.txt";
		String rutaAO1 = ruta+"AO1.txt";
		String rutaAO2 = ruta+"AO2.txt";
		String rutaAO = ruta+"VuelosAerolinea.txt";
		
		String[] rutas = {rutaInput, rutaFP, rutaMilestones, rutaAO1, rutaAO2, rutaAO};
		return rutas;
	}
		
		// Faltaria la función que crea y envia el mensaje con los parametros (Agente: Simulator Input)
	
	public String definirmensaje(Aircraft avion){ // Función que simulara el contenido de un mensaje (para test de las funciones de los agentes)
		String mensaje = "";
		
		Gson gson = new Gson();
		mensaje = gson.toJson(avion);
		
		return mensaje;
	}
	
	public String[] sacarParametros(Aircraft avion){ // Agente: CDM -> sacar una lista con los parametros y los valores del avionParametros recibido (no cambiar la del agente=
		String Parametros = avion.getIncidencias();
		
		String[] listaparametros = Parametros.split("\n");
		
		return listaparametros;
	}
	
	public void CargarParametrosInicio (){
		String parametrosGuardar = definirParametros();
		
		
		int[] errorguardado = {0,0};
		if(parametrosGuardar.equals("error"))
		{
			errorguardado[0] = 1;
		}
	
		parametros = parametrosGuardar;
		avionparametros = AvionParametros(parametros);  // Añadimos los parametros
		
		// Cargamos los datos del archivo de trafico aereo
		int avionesG = avionesInput(RUTA);
		TiempoEstimadoSimulacion=avionesG*VelocidadSimulacion*16;
		panelDatosInput.setVisible(true);
		lbl_NumeroAviones.setText(Integer.toString(avionesG));
		avionparametros.añadirIncidenciaLineaCompleta("Aviones\t" + avionesG);
		
		// Cargamos los datos del txt del aeropuerto
		String parametrosAeropuerto = CargarArchivoAirport(rutaArchivoTextGuardar.getText());
			
		avionparametros.añadirIncidenciaLineaCompleta(parametrosAeropuerto);
		
		//avionparametros.printFlight(12);
		
		parametrosGuardar = avionparametros.getIncidencias(); // Sacamos los datos que se guardaran en el .txt
		
		try {
			if(errorguardado[0] == 0 )
			{
				errorguardado[1] = guardarArchivo(RUTA+"ParametrosSimulacion.txt", parametrosGuardar);
			if(errorguardado[1] == 0)
			{
				lblProcesandoDatosDdr.setVisible(false);
				
				JOptionPane.showMessageDialog(null,"Parametros guardados \n" +
						"Archivo creado correctamente");
				
				String tiempo = mostrarTiempo(TiempoEstimadoSimulacion);
				JOptionPane.showMessageDialog(null, "Tiempo estimado de simulación \n " + tiempo);
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		guardado = true;
		parametrosDefinidos = true;
		
	}
	public void rutatxtbox()
	{

		String[] rutaarchivo = rutaArchivoTextGuardar.getText().split("/");
		
	// Detectamos si se ha introducido la ruta con "/" o con "\"
		if(rutaarchivo.length == 1){ 
			rutaarchivo = rutaArchivoTextGuardar.getText().split("\\\\");		
		}
			// Montamos la ruta a la carpeta
		String rutacarpeta = "";
		
		int i = 0;
		while(i < rutaarchivo.length-1){
			rutacarpeta = rutacarpeta + rutaarchivo[i]+"/";
			i++;
		}

		String rutaInput = "RutaInput" + "\t" + rutacarpeta;
		
		RUTA = rutacarpeta;
	}
	
	public String mostrarTiempo(int tiempo) // Escribe el tiempo en horas/min/sec (input en milisegundos)
	{
		String salida="";
		int hh = 0;
		int min = 0;
		int seg = 0;;
		while(tiempo >= 3600000)
		{
			hh++;
			tiempo = tiempo - 3600000;
		}
		if(hh!=0)
			salida = salida + hh + " horas ";
		while(tiempo>=60000)
		{
			min ++;
			tiempo = tiempo - 60000;
		}
		if(min!=0)
			salida = salida + min + " minutos ";
		while(tiempo >= 1000)
		{
			seg ++;
			tiempo = tiempo - 1000;
		}
		if(seg != 0)
		{
			salida = salida + seg + " segundos ";
		}
		salida = salida + "aproximadamente";
		return salida;
	}

	public boolean getHayRetrasos()
	{
		return interRetrasos.hayRetrasos;
	}
	public String getrutacargar()
	{
		return txtCusersivanworkspacesimuladorvresultstxt.getText();
	}
	public void MostrarErrorDDR2()
	{
		JOptionPane.showMessageDialog(null,"Archivo DDR2: Error en el formato");
		panel_Resultados.setVisible(false);
		btnGuardar.setVisible(true);
		lblProcesandoDatosDdr.setVisible(false);
		lblQuieresAnalizarResultados.setVisible(true);
		btnAnalizarResultados.setVisible(true);
		lblNota.setVisible(true);
	}
	public void MostrarErrorDDR2noaviones()
	{
		JOptionPane.showMessageDialog(null,"Error: En esta configuración se han encontrado 0 aviones para simular \n" +
				"Comprueba Aeropuerto y DDR2");
		panel_Resultados.setVisible(false);
		btnGuardar.setVisible(true);
		lblProcesandoDatosDdr.setVisible(false);
		lblQuieresAnalizarResultados.setVisible(true);
		btnAnalizarResultados.setVisible(true);
		lblNota.setVisible(true);
	}
	public void bloquearParametros()
	{
		rutaArchivoTextGuardar.setEditable(false);
		spinnerTaxiIn.setEnabled(false);
		spinnerTaxiOut.setEnabled(false);
		spinnerVelSim.setEnabled(false);
		txtINPUTtxt.setEditable(false);
	}
}
