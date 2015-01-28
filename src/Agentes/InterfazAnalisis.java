package Agentes;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.BevelBorder;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import java.awt.Canvas;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.math.plot.Plot2DPanel;

import org.math.plot.*;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class InterfazAnalisis extends JFrame {

	public Color graph1 = new Color(255,0,0);
	public Color graph2 = new Color(0,0,255);
	private JPanel contentPane;
	boolean rutas2 = false;
	String Ruta = ""; //"C:/Users/xavi/workspace/SimuladorV1.6/Analisis-";
	String Rutareal;
	
	// Atributos necesarios para el análisis
	Tabla tabla1;
	Tabla tabla2;

	int minutostotalessimulacion;
	int minutostotalessimulacion1;
	int minutostotalessimulacion2;
	int minutostotalessimulacionventana;
	
	int[] avion1maximo = new int[3]; // index avion en tabla, numero de minutos totales, minuto en el que llega al tiempo
	int[] avion2maximo = new int[3]; // index avion en tabla, numero de minutos totales, minuto en el que llega al tiempo
	double[] average1 = new double[2]; // num de aviones retrasados por minutos, num de minutos retrasados por avion
	double[] average2 = new double[2]; // num de aviones retrasados por minutos, num de minutos retrasados por avion
	int[] avion1maximoventana = new int[3]; // index avion en tabla, numero de minutos totales, minuto en el que llega al tiempo
	int[] avion2maximoventana = new int[3]; // index avion en tabla, numero de minutos totales, minuto en el que llega al tiempo
	double[] average1ventana = new double[2]; // num de aviones retrasados por minutos, num de minutos retrasados por avion
	double[] average2ventana = new double[2]; // num de aviones retrasados por minutos, num de minutos retrasados por avion
	String parametro = "ELDT";
	
	double[] vectorX;
	int[] analisisTabla1;
	int[] analisisTabla2;
	int minutostotales1; //suma de todos los minutos retrasados
	int minutostotales2;
	int starttime = 9999;
	int finishtime = 0;
	int starttime1;
	int finishtime1;
	int starttime2 = 9999;
	int finishtime2 = 0;
	
	double[]vectorXventana;
	int[] analisisTabla1ventana;
	int[] analisisTabla2ventana;
	int minutostotalesventana1;
	int minutostotalesventana2;
	int starttimeVentana;
	int finishtimeVentana;

	ArrayList MinutosRetraso1=new ArrayList();
	ArrayList MinutosRetraso2=new ArrayList();
	
		// Plot
	double[] vectorTabla1; // Vector de la tabla1 que saldrá en el plot
	double[] vectorTabla2; // Vector de la tabla2 que saldrá en el plot
	
		//plot ventana
	double[] vectorTablaventana1; // Vector de la tabla1 que saldrá en el plot
	double[] vectorTablaventana2; // Vector de la tabla2 que saldrá en el plot
	
		//Lista aerolineas y modelos que podremos escojer en el filtro
	ArrayList aerolineas = new ArrayList();
	ArrayList modelos = new ArrayList();
		
	// Filtro
	private JPanel panelFiltro;
	String aerolineasFiltro = "";
	String modelosFiltro = "";
	int numaviones1;
	int numaviones2;
	int numaviones1ventana;  //No fet
	int numaviones2ventana;  //No fet
	int cM;
	int cA;
	
	boolean hayventana = false;
	
	private JTextField textFieldAerolineas;
	private JTextField textFieldModelos;
	private JComboBox comboBox_Aerolineas;
	private JComboBox comboBox_Modelos;
	private JComboBox comboBoxParametro;
	private JCheckBox chckbxSinFiltro;
	private Plot2DPanel plot2DPanel;
	private JLabel lblTabla_2;
	private JLabel lblNumAviones2;
	private JLabel lblNumAviones1;
	private JLabel lblVentanaAnlisis;
	private JPanel panelVentanaTiempo;
	private JSpinner spinner_HoraInicio;
	private JSpinner spinner_MinInicio;
	private JSpinner spinner_HoraFin;
	private JSpinner spinner_MinFin;
	private JCheckBox chckbxAnlisisTiempoCompleto;
	private JTextField txt_HoraInicioVentana;
	private JTextField txt_HoraFinVentana;
	private JTextField txt_MinTotal;
	private JTextField txt_NumA1;
	private JTextField txt_MinDelay1;
	private JTextField txt_Avmin1;
	private JTextField txt_MinAv1;
	private JTextField txt_AvMax1;
	private JTextField txt_DelayMax1;
	private JTextField txt_Tparam1;
	private JTextField txt_NumA2;
	private JTextField txt_MinDelay2;
	private JTextField txt_Avmin2;
	private JTextField txt_MinAv2;
	private JTextField txt_DelayMax2;
	private JTextField txt_Tparam2;
	private JLabel lblParametro;
	private JTextField txt_AvMax2;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazAnalisis frame = new InterfazAnalisis();
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
	public InterfazAnalisis() {
		setTitle("ACDM - An\u00E1lisis");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1189, 745);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panelControles = new JPanel();
		panelControles.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JPanel panelEstadisticas = new JPanel();
		panelEstadisticas.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		plot2DPanel = new Plot2DPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelControles, GroupLayout.PREFERRED_SIZE, 385, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(plot2DPanel, GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
						.addComponent(panelEstadisticas, GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelControles, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(plot2DPanel, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelEstadisticas, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		JLabel lblTiempoSimulado = new JLabel("Tiempo Simulado:");
		lblTiempoSimulado.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblHoraInicio = new JLabel("Hora Inicio");
		lblHoraInicio.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txt_HoraInicioVentana = new JTextField();
		txt_HoraInicioVentana.setEditable(false);
		txt_HoraInicioVentana.setColumns(10);
		
		JLabel lblHoraFinal_1 = new JLabel("Hora Final");
		lblHoraFinal_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txt_HoraFinVentana = new JTextField();
		txt_HoraFinVentana.setEditable(false);
		txt_HoraFinVentana.setColumns(10);
		
		JLabel lblMinutosTotales = new JLabel("Minutos Totales");
		
		txt_MinTotal = new JTextField();
		txt_MinTotal.setEditable(false);
		txt_MinTotal.setColumns(10);
		
		JLabel lblDatosAnlisis = new JLabel("Datos An\u00E1lisis: ");
		lblDatosAnlisis.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		JLabel lblNumaviones = new JLabel("Num.Aviones");
		
		JLabel lblTotalMinutosDelay = new JLabel("Total Minutos Delay");
		
		JLabel lblAverageAvionesmin = new JLabel("Average Aviones/min");
		
		JLabel lblAverageMinutosDelay = new JLabel("Average minutos delay/avi\u00F3n");
		
		txt_NumA1 = new JTextField();
		txt_NumA1.setEditable(false);
		txt_NumA1.setColumns(10);
		
		txt_MinDelay1 = new JTextField();
		txt_MinDelay1.setEditable(false);
		txt_MinDelay1.setColumns(10);
		
		txt_Avmin1 = new JTextField();
		txt_Avmin1.setEditable(false);
		txt_Avmin1.setColumns(10);
		
		txt_MinAv1 = new JTextField();
		txt_MinAv1.setEditable(false);
		txt_MinAv1.setColumns(10);
		
		JLabel lblAvinMaxDelay = new JLabel("Avi\u00F3n max. delay");
		lblAvinMaxDelay.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblAvin = new JLabel("Avi\u00F3n");
		
		JLabel lblDelay = new JLabel("Delay");
		
		lblParametro = new JLabel("XXXX a las");
		
		txt_AvMax1 = new JTextField();
		txt_AvMax1.setText("");
		txt_AvMax1.setEditable(false);
		txt_AvMax1.setColumns(10);
		
		txt_DelayMax1 = new JTextField();
		txt_DelayMax1.setEditable(false);
		txt_DelayMax1.setColumns(10);
		
		txt_Tparam1 = new JTextField();
		txt_Tparam1.setEditable(false);
		txt_Tparam1.setColumns(10);
		
		JLabel lblTabla_1 = new JLabel("Tabla 1");
		lblTabla_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblTabla_3 = new JLabel("Tabla 2");
		lblTabla_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		txt_NumA2 = new JTextField();
		txt_NumA2.setEditable(false);
		txt_NumA2.setColumns(10);
		
		txt_MinDelay2 = new JTextField();
		txt_MinDelay2.setEditable(false);
		txt_MinDelay2.setColumns(10);
		
		txt_Avmin2 = new JTextField();
		txt_Avmin2.setEditable(false);
		txt_Avmin2.setColumns(10);
		
		txt_MinAv2 = new JTextField();
		txt_MinAv2.setEditable(false);
		txt_MinAv2.setColumns(10);
		
		txt_DelayMax2 = new JTextField();
		txt_DelayMax2.setEditable(false);
		txt_DelayMax2.setColumns(10);
		
		txt_Tparam2 = new JTextField();
		txt_Tparam2.setEditable(false);
		txt_Tparam2.setColumns(10);
		
		JButton btnClear_1 = new JButton("Clear");
		btnClear_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				plot2DPanel.removeAllPlots();
				resetPanelDatos();
			}
		});
		
		txt_AvMax2 = new JTextField();
		txt_AvMax2.setText("");
		txt_AvMax2.setEditable(false);
		txt_AvMax2.setColumns(10);

		GroupLayout gl_panelEstadisticas = new GroupLayout(panelEstadisticas);
		gl_panelEstadisticas.setHorizontalGroup(
			gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEstadisticas.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_panelEstadisticas.createSequentialGroup()
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelEstadisticas.createSequentialGroup()
									.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
										.addComponent(lblTiempoSimulado)
										.addGroup(gl_panelEstadisticas.createSequentialGroup()
											.addGap(10)
											.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING, false)
												.addComponent(lblMinutosTotales, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblHoraFinal_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblHoraInicio, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING, false)
												.addComponent(txt_HoraFinVentana, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
												.addComponent(txt_MinTotal, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
												.addComponent(txt_HoraInicioVentana, 0, 0, Short.MAX_VALUE))))
									.addGap(64)
									.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panelEstadisticas.createSequentialGroup()
											.addComponent(lblDatosAnlisis)
											.addGap(18))
										.addGroup(gl_panelEstadisticas.createSequentialGroup()
											.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblTotalMinutosDelay)
												.addComponent(lblAverageAvionesmin)
												.addComponent(lblNumaviones))
											.addGap(9))))
								.addGroup(gl_panelEstadisticas.createSequentialGroup()
									.addComponent(lblParametro)
									.addGap(18))
								.addGroup(gl_panelEstadisticas.createSequentialGroup()
									.addComponent(lblDelay)
									.addGap(18))
								.addGroup(gl_panelEstadisticas.createSequentialGroup()
									.addComponent(lblAverageMinutosDelay)
									.addPreferredGap(ComponentPlacement.UNRELATED)))
							.addGap(8)
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
								.addComponent(txt_MinDelay1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(txt_NumA1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(txt_Avmin1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(txt_MinAv1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(txt_DelayMax1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(txt_Tparam1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTabla_1)))
						.addGroup(gl_panelEstadisticas.createSequentialGroup()
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblAvinMaxDelay)
								.addGroup(gl_panelEstadisticas.createSequentialGroup()
									.addComponent(btnClear_1)
									.addPreferredGap(ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
									.addComponent(lblAvin)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txt_AvMax1, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)))
					.addGap(28)
					.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
							.addComponent(lblTabla_3, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGap(1)
								.addComponent(txt_NumA2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGap(1)
								.addComponent(txt_MinDelay2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGap(1)
								.addComponent(txt_Avmin2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGap(1)
								.addComponent(txt_MinAv2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGap(1)
								.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.TRAILING)
									.addComponent(txt_Tparam2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
									.addComponent(txt_DelayMax2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))))
						.addComponent(txt_AvMax2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
					.addGap(286))
		);
		gl_panelEstadisticas.setVerticalGroup(
			gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelEstadisticas.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addComponent(lblTabla_3, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(txt_NumA2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(txt_MinDelay2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(txt_Avmin2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addComponent(txt_MinAv2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(57)
								.addComponent(txt_DelayMax2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelEstadisticas.createSequentialGroup()
								.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.LEADING)
									.addComponent(lblDatosAnlisis)
									.addGroup(gl_panelEstadisticas.createSequentialGroup()
										.addComponent(lblTabla_1)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
											.addComponent(txt_NumA1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblNumaviones))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
											.addComponent(txt_MinDelay1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblTotalMinutosDelay))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
											.addComponent(txt_Avmin1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblAverageAvionesmin))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
											.addComponent(txt_MinAv1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblAverageMinutosDelay))))
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblAvinMaxDelay)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
									.addComponent(txt_AvMax1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblAvin)
									.addComponent(btnClear_1)
									.addComponent(txt_AvMax2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
									.addComponent(txt_DelayMax1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblDelay))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
									.addComponent(txt_Tparam1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblParametro)
									.addComponent(txt_Tparam2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_panelEstadisticas.createSequentialGroup()
							.addComponent(lblTiempoSimulado)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
								.addComponent(txt_HoraInicioVentana, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblHoraInicio))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblHoraFinal_1)
								.addComponent(txt_HoraFinVentana, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelEstadisticas.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMinutosTotales)
								.addComponent(txt_MinTotal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		panelEstadisticas.setLayout(gl_panelEstadisticas);
		
		JLabel label = new JLabel("Filtro:");
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		panelFiltro = new JPanel();
		panelFiltro.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelFiltro.setVisible(false);
		JLabel label_1 = new JLabel("Aerolineas:");
		
		JLabel label_2 = new JLabel("Modelos:");
		
		comboBox_Modelos = new JComboBox();
		comboBox_Modelos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
			
			añadirAlFiltro("Modelo");
			actualizarFiltro();
			int[] num = contarAvionesTablas();
			lblNumAviones1.setText(num[0] + "");
			lblNumAviones1.setText(num[1] + "");
			
				
			}
		}
		);
		
		comboBox_Aerolineas = new JComboBox();
		comboBox_Aerolineas.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
			
			añadirAlFiltro("Aerolinea");
			actualizarFiltro();
			int[] num = contarAvionesTablas();
			lblNumAviones1.setText(num[0] + "");
			lblNumAviones2.setText(num[1] + "");
			}
		}
		);
		
		textFieldAerolineas = new JTextField();
		textFieldAerolineas.setEditable(false);
		textFieldAerolineas.setColumns(10);
		
		textFieldModelos = new JTextField();
		textFieldModelos.setEditable(false);
		textFieldModelos.setColumns(10);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textFieldModelos.setText("");
				textFieldAerolineas.setText("");
				
				actualizarFiltro();
				cM = 0;
				cA = 0;
				
				int[] num = contarAvionesTablas();
				lblNumAviones1.setText(num[0] + "");
				lblNumAviones2.setText(num[1] + "");
			}
		});
		JLabel lblAvionesAnalizados = new JLabel("Aviones Analizados:");
		
		JLabel lblTabla = new JLabel("Tabla 1");
		
		lblNumAviones1 = new JLabel("...");
		
		lblTabla_2 = new JLabel("Tabla 2");
		lblTabla_2.setVisible(false);
		
		lblNumAviones2 = new JLabel("...");
		lblNumAviones2.setVisible(false);
		GroupLayout gl_panelFiltro = new GroupLayout(panelFiltro);
		gl_panelFiltro.setHorizontalGroup(
			gl_panelFiltro.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFiltro.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelFiltro.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelFiltro.createSequentialGroup()
									.addComponent(lblTabla_2)
									.addGap(18)
									.addComponent(lblNumAviones2))
								.addGroup(gl_panelFiltro.createSequentialGroup()
									.addComponent(lblTabla)
									.addGap(18)
									.addComponent(lblNumAviones1, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_panelFiltro.createSequentialGroup()
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.LEADING)
								.addComponent(comboBox_Modelos, 0, 125, Short.MAX_VALUE)
								.addComponent(label_1)
								.addComponent(label_2)
								.addComponent(comboBox_Aerolineas, 0, 125, Short.MAX_VALUE))
							.addGap(14)
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(textFieldAerolineas)
								.addComponent(textFieldModelos, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)))
						.addGroup(Alignment.TRAILING, gl_panelFiltro.createSequentialGroup()
							.addComponent(lblAvionesAnalizados, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
							.addComponent(btnClear)))
					.addContainerGap())
		);
		gl_panelFiltro.setVerticalGroup(
			gl_panelFiltro.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFiltro.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_1)
					.addGap(4)
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_Aerolineas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldAerolineas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_Modelos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldModelos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelFiltro.createSequentialGroup()
							.addComponent(btnClear)
							.addGap(31))
						.addGroup(gl_panelFiltro.createSequentialGroup()
							.addComponent(lblAvionesAnalizados)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblTabla)
								.addComponent(lblNumAviones1))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblTabla_2)
								.addComponent(lblNumAviones2))
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addContainerGap(37, Short.MAX_VALUE))
		);
		panelFiltro.setLayout(gl_panelFiltro);
		
		JLabel lblTiempoAnalizado = new JLabel("Parametro Analizado:");
		lblTiempoAnalizado.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		comboBoxParametro = new JComboBox();
		comboBoxParametro.setModel(new DefaultComboBoxModel(new String[] {"ELDT [Estimated Landing Time]", "ETOT [Estimated Take Off Time]", "EIBT [Estimated IN-Block Time]", "EOBT [Estimated OFF-Block Time]", "TFIR [FIR Entrance Time]", "TAPP [Final Approach Time]", "ACGT [Actual Commence of Gound Handling Time]", "ASBT [Actual Start Boarding Time]", "ARDT [Aircraft Ready Time]", "ASRT [Actual Start-Up Request Time]", "TSAT [Target Start-Up Aproval Time]"}));
		comboBoxParametro.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
			
				String opcionCombo = (String)comboBoxParametro.getSelectedItem();
				parametro  = sacarParametro(opcionCombo);
				System.out.println("Parametro a analizar: " + parametro);
				
			}
		}
		);
		
		JButton btnAnalizar = new JButton("Analizar");
		btnAnalizar.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAnalizar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				ResetValores();
				
				if(chckbxAnlisisTiempoCompleto.isSelected() == false)
				{
					actualizarVentana();
					hayventana = true;
					
				}
				gettimessimualcion(parametro);
				Rutareal = Ruta + "Analisis-" + parametro + ".txt";
				vectorX = new double[restatemps(finishtime,starttime)];
				minutostotalessimulacion = restatemps(finishtime,starttime);
				minutostotalessimulacion1 = restatemps(finishtime1,starttime1);
				if(rutas2)
					minutostotalessimulacion2=restatemps(finishtime2,starttime2);
				int i=starttime;
				int p=restatemps(0,starttime);
				int contador=0;
				while(i<finishtime)
				{
					vectorX[contador]=p;
					i=sumatemps(i,1);
					p++;
					contador++;
				}
				analisisTabla1 = new int[restatemps(finishtime,starttime)];
				if(rutas2)
					analisisTabla2 = new int[restatemps(finishtime,starttime)];
				
				EscanearMirandotiempo(parametro);
				
				if(hayventana)
				{
					calculosventana(parametro);
				}
				// create your PlotPanel (you can use it as a JPanel)
				
				// add a line plot to the PlotPanel

				plot2DPanel.setLegendOrientation("SOUTH");
				vectorTabla1 = new double[analisisTabla1.length];
				i=0;
				while(i<analisisTabla1.length)
				{
					vectorTabla1[i] = analisisTabla1[i];
					i++;
				}
				String leyenda1 = "Tabla 1: "+parametro;
				plot2DPanel.addLinePlot(leyenda1,graph1,vectorX ,vectorTabla1);
				
				if(rutas2)
				{
					vectorTabla2 = new double[analisisTabla2.length];
					i=0;
					while(i<analisisTabla2.length)
					{
						vectorTabla2[i] = analisisTabla2[i];
						i++;
					}
					String leyenda2 = "Tabla 2: "+parametro;
					plot2DPanel.addLinePlot(leyenda2,graph2,vectorX ,vectorTabla2);
				}
				plot2DPanel.setAxisLabel(0, "Tiempo de simulación [min]");
				plot2DPanel.setAxisLabel(1, "Aviones Afectados [num]");

				EscribirResultados();
				
				// AnalisisPorConsola();
				
				rellenarPanelDatos();
				
				JOptionPane.showMessageDialog(null, "Archivo Analisis-"+parametro+" guardado.\n" +
						"Ruta: " + Rutareal);
				
				
			}
		});
		
		chckbxSinFiltro = new JCheckBox("Sin Filtro");
		chckbxSinFiltro.setSelected(true);
		chckbxSinFiltro.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Hacemos un reset al filtro
				textFieldModelos.setText("");
				textFieldAerolineas.setText("");
				
				actualizarFiltro();
				cM = 0;
				cA = 0;
				
				// Mostramos o no el panel con las opciones del filtro
				Boolean visible = true;
				if(chckbxSinFiltro.isSelected())
				{
					visible = false;
				}
				panelFiltro.setVisible(visible);
				

			}
		});
		
		panelVentanaTiempo = new JPanel();
		panelVentanaTiempo.setVisible(false);
		panelVentanaTiempo.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		lblVentanaAnlisis = new JLabel("Ventana Tiempo An\u00E1lisis:");
		lblVentanaAnlisis.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		chckbxAnlisisTiempoCompleto = new JCheckBox("An\u00E1lisis Completo");
		chckbxAnlisisTiempoCompleto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Mostramos o no el panel con las opciones de la ventana
				Boolean visible = true;
				if(chckbxAnlisisTiempoCompleto.isSelected())
				{
					visible = false;
				}
				panelVentanaTiempo.setVisible(visible);
			}
		});
		chckbxAnlisisTiempoCompleto.setSelected(true);
		GroupLayout gl_panelControles = new GroupLayout(panelControles);
		gl_panelControles.setHorizontalGroup(
			gl_panelControles.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelControles.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelControles.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panelControles.createSequentialGroup()
							.addComponent(btnAnalizar, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panelControles.createSequentialGroup()
							.addGroup(gl_panelControles.createParallelGroup(Alignment.TRAILING)
								.addComponent(panelFiltro, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, gl_panelControles.createSequentialGroup()
									.addComponent(label, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
									.addGap(255)
									.addComponent(chckbxSinFiltro, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(Alignment.LEADING, gl_panelControles.createSequentialGroup()
									.addComponent(lblTiempoAnalizado, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBoxParametro, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
								.addGroup(Alignment.LEADING, gl_panelControles.createSequentialGroup()
									.addComponent(lblVentanaAnlisis, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
									.addComponent(chckbxAnlisisTiempoCompleto))
								.addComponent(panelVentanaTiempo, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
							.addGap(16))))
		);
		gl_panelControles.setVerticalGroup(
			gl_panelControles.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelControles.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelControles.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTiempoAnalizado, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxParametro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(34)
					.addGroup(gl_panelControles.createParallelGroup(Alignment.BASELINE)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxSinFiltro))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelFiltro, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelControles.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblVentanaAnlisis, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxAnlisisTiempoCompleto))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelVentanaTiempo, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
					.addGap(226)
					.addComponent(btnAnalizar)
					.addContainerGap())
		);
		
		JLabel lblInicioTiempoAnalizado = new JLabel("Tiempo Analizado:");
		
		spinner_HoraInicio = new JSpinner();
		spinner_HoraInicio.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblHora = new JLabel("Hora Inicio:");
		
		JLabel lblh = new JLabel("h");
		
		spinner_MinInicio = new JSpinner();
		spinner_MinInicio.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin = new JLabel("min");
		
		JLabel lblHoraFinal = new JLabel("Hora Final:");
		
		spinner_HoraFin = new JSpinner();
		spinner_HoraFin.setModel(new SpinnerNumberModel(23, 0, 23, 1));
		
		JLabel label_4 = new JLabel("h");
		
		spinner_MinFin = new JSpinner();
		spinner_MinFin.setModel(new SpinnerNumberModel(59, 0, 59, 1));
		
		JLabel label_5 = new JLabel("min");
		GroupLayout gl_panelVentanaTiempo = new GroupLayout(panelVentanaTiempo);
		gl_panelVentanaTiempo.setHorizontalGroup(
			gl_panelVentanaTiempo.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelVentanaTiempo.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelVentanaTiempo.createParallelGroup(Alignment.LEADING)
						.addComponent(lblInicioTiempoAnalizado)
						.addGroup(gl_panelVentanaTiempo.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_panelVentanaTiempo.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_panelVentanaTiempo.createSequentialGroup()
									.addComponent(lblHoraFinal, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinner_HoraFin, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinner_MinFin, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(label_5, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panelVentanaTiempo.createSequentialGroup()
									.addComponent(lblHora, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinner_HoraInicio, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblh, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinner_MinInicio, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblMin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
					.addContainerGap(133, Short.MAX_VALUE))
		);
		gl_panelVentanaTiempo.setVerticalGroup(
			gl_panelVentanaTiempo.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelVentanaTiempo.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInicioTiempoAnalizado)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelVentanaTiempo.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinner_HoraInicio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblh)
						.addComponent(spinner_MinInicio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMin)
						.addComponent(lblHora))
					.addGap(9)
					.addGroup(gl_panelVentanaTiempo.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraFinal)
						.addComponent(spinner_HoraFin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4)
						.addComponent(spinner_MinFin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_5))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		panelVentanaTiempo.setLayout(gl_panelVentanaTiempo);
		panelControles.setLayout(gl_panelControles);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void AnalisisPorConsola()
	{
		System.out.println("Mostrar por pantalla las variables que tienen de salir en labels:");
		System.out.println("Tabla 1 Sin Ventana:");
		System.out.println("Tiempo estudiado: " + minutostotalessimulacion + " minutos");
		System.out.println("Minutoinicio: " + starttime +". Minuto final: " + finishtime);
		System.out.println("Numero de aviones: " + numaviones1);
		System.out.println("Avión con más retraso: "+ avion1maximo[0] + ". Retrasado: "+ avion1maximo[1] + " minutos. Realiza " + parametro + " en el minuto: "+ avion1maximo[2]);
		System.out.println("Average de aviones retrasados a cada minuto: "+average1[0]);
		System.out.println("Average de minutos de retraso que recibe cada avion: "+average1[1]);
		System.out.println("Cantidad de minutos de retraso aplicados: "+minutostotales1);
		System.out.println("");
		
		System.out.println("Tabla 2 Sin Ventana:");
		System.out.println("Tiempo estudiado: " + minutostotalessimulacion + " minutos");
		System.out.println("Minutoinicio: " + starttime +". Minuto final: " + finishtime);
		System.out.println("Numero de aviones: " + numaviones2);
		System.out.println("Avión con más retraso: "+ avion2maximo[0] + ". Retrasado: "+ avion2maximo[1] + " minutos. Realiza " + parametro + " en el minuto: "+ avion2maximo[2]);
		System.out.println("Average de aviones retrasados a cada minuto: "+average2[0]);
		System.out.println("Average de minutos de retraso que recibe cada avion: "+average2[1]);
		System.out.println("Cantidad de minutos de retraso aplicados: "+minutostotales2);
		System.out.println("");
		
		System.out.println("Tabla 1 Con Ventana:");
		System.out.println("Tiempo estudiado: " + minutostotalessimulacionventana+ " minutos");
		System.out.println("Minutoinicio: " + starttimeVentana +". Minuto final: " + finishtimeVentana);
		System.out.println("Numero de aviones: " + numaviones1ventana);
		System.out.println("Avión con más retraso: "+ avion1maximoventana[0] + ". Retrasado: "+ avion1maximoventana[1] + " minutos. Realiza " + parametro + " en el minuto: "+ avion1maximoventana[2]);
		System.out.println("Average de aviones retrasados a cada minuto: "+average1ventana[0]);
		System.out.println("Average de minutos de retraso que recibe cada avion: "+average1ventana[1]);
		System.out.println("Cantidad de minutos de retraso aplicados: "+minutostotalesventana1);
		System.out.println("");
		
		System.out.println("Tabla 2 Con Ventana:");
		System.out.println("Tiempo estudiado: " + minutostotalessimulacionventana + " minutos");
		System.out.println("Minutoinicio: " + starttimeVentana +". Minuto final: " + finishtimeVentana);
		System.out.println("Numero de aviones: " + numaviones2ventana);
		System.out.println("Avión con más retraso: "+ avion2maximoventana[0] + ". Retrasado: "+ avion2maximoventana[1] + " minutos. Realiza " + parametro + " en el minuto: "+ avion2maximoventana[2]);
		System.out.println("Average de aviones retrasados a cada minuto: "+average2ventana[0]);
		System.out.println("Average de minutos de retraso que recibe cada avion: "+average2ventana[1]);
		System.out.println("Cantidad de minutos de retraso aplicados: "+minutostotalesventana2);
		System.out.println("");
		
	}
	
	public void ResetValores()
	{
		starttime = 9999;
		finishtime = 0;
		starttime2 = 9999;
		finishtime2 = 0;
		
		avion1maximo[0] = 0;
		avion1maximo[1] = 0;
		avion1maximo[2] = 0;
		avion2maximo[0] = 0;
		avion2maximo[1] = 0;
		avion2maximo[2] = 0;
	}
	
	public void EscribirResultados()
	{
		 FileWriter ficheroAnalisis;
			try {
				ficheroAnalisis = new FileWriter(Rutareal);
		        PrintWriter pwMilestones = new PrintWriter(ficheroAnalisis);
		        pwMilestones.println("Analisis tiempo de " + parametro);
	        	pwMilestones.println("");
		        if(aerolineasFiltro.equals("") && modelosFiltro.equals(""))
		        	pwMilestones.println("Sin filtros");
		        else
		        {
		        	if(aerolineasFiltro.equals("")==false)
		        	{
		        		String[] palabras=aerolineasFiltro.split("-");
			        	int i=1;
			        	String filtroaerolineas = "Filtro de aerolineas: "+palabras[0];
			        	while(i<palabras.length)
			        	{
			        		filtroaerolineas=filtroaerolineas + "-" + palabras[i];
			        		i++;
			        	}
			        	pwMilestones.println(filtroaerolineas);
		        	}
		        	if(modelosFiltro.equals("")==false)
		        	{
		        		String[] palabras=modelosFiltro.split("-");
			        	int i=1;
			        	String filtromodelos = "Filtro de modelos: "+palabras[0];
			        	while(i<modelos.size())
			        	{
			        		filtromodelos=filtromodelos + "-" + palabras[i];
			        		i++;
			        	}
			        	pwMilestones.println(filtromodelos);
		        	}
		        }
	        	pwMilestones.println("");
		        pwMilestones.println("Documento 1: Gráfica");
		        int i=0;
		        String paraula ="";
		        while(i<vectorTabla1.length)
		        {
		        	paraula = paraula + vectorTabla1[i]+" ";
		        	i++;
		        }
		        pwMilestones.println(paraula);
		        i=0;
		        paraula ="";
		        while(i<vectorX.length)
		        {
		        	paraula = paraula + vectorX[i]+" ";
		        	i++;
		        }
		        pwMilestones.println(paraula);
		        if(rutas2)
		        {
		        	pwMilestones.println("");
			        pwMilestones.println("Documento 2: Gráfica");
			        i=0;
			        paraula="";
			        while(i<vectorTabla2.length)
			        {
			        	paraula = paraula + vectorTabla2[i]+" ";
			        	i++;
			        }
			        pwMilestones.println(paraula);
			        i=0;
			        paraula ="";
			        while(i<vectorX.length)
			        {
			        	paraula = paraula + vectorX[i]+" ";
			        	i++;
			        }
			        pwMilestones.println(paraula);
		        }
		        if(hayventana)
		        {
		        	pwMilestones.println("");
			        pwMilestones.println("Documento 1 ventana: Gráfica");
			        i=0;
			        paraula="";
			        while(i<vectorTablaventana1.length)
			        {
			        	paraula = paraula + vectorTablaventana1[i]+" ";
			        	i++;
			        }
			        pwMilestones.println(paraula);
			        i=0;
			        paraula ="";
			        while(i<vectorXventana.length)
			        {
			        	paraula = paraula + vectorXventana[i]+" ";
			        	i++;
			        }
			        pwMilestones.println(paraula);
			        if(rutas2)
			        {
			        	pwMilestones.println("");
				        pwMilestones.println("Documento 2 ventana: Gráfica");
				        i=0;
				        paraula="";
				        while(i<vectorTablaventana2.length)
				        {
				        	paraula = paraula + vectorTablaventana2[i]+" ";
				        	i++;
				        }
				        pwMilestones.println(paraula);
				        i=0;
				        paraula ="";
				        while(i<vectorXventana.length)
				        {
				        	paraula = paraula + vectorXventana[i]+" ";
				        	i++;
				        }
				        pwMilestones.println(paraula);
			        }
		        }
	        	pwMilestones.println("");
		        pwMilestones.println("Resultados Documento 1:");
		        pwMilestones.println("Tiempo estudiado: " + minutostotalessimulacion);
		        pwMilestones.println("Minutoinicio: " + starttime +". Minuto final: " + finishtime);
		        pwMilestones.println("Numero de aviones: " + numaviones1);
		        pwMilestones.println("Avión con más retraso: "+ avion1maximo[0] + ". Retrasado: "+ avion1maximo[1] + "minutos. Realiza " + parametro + " en el minuto: "+ avion1maximo[2]);
		        pwMilestones.println("Average de aviones retrasados a cada minuto: "+average1[0]);
		        pwMilestones.println("Average de minutos de retraso que recibe cada avion: "+average1[1]);
		        pwMilestones.println("Cantidad de minutos de retraso aplicados: "+minutostotales1);
		        if(rutas2)
		        {
		        	pwMilestones.println("");
			        pwMilestones.println("Resultados Documento 2:");
			        pwMilestones.println("Tiempo estudiado: " + minutostotalessimulacion);
			        pwMilestones.println("Minutoinicio: " + starttime +". Minuto final: " + finishtime);
			        pwMilestones.println("Numero de aviones: " + numaviones2);
			        pwMilestones.println("Avión con más retraso: "+ avion2maximo[0] + ". Retrasado: "+ avion2maximo[1] + "minutos. Realiza " + parametro + " en el minuto: "+ avion2maximo[2]);
			        pwMilestones.println("Average de aviones retrasados a cada minuto: "+average2[0]);
			        pwMilestones.println("Average de minutos de retraso que recibe cada avion: "+average2[1]);
			        pwMilestones.println("Cantidad de minutos de retraso aplicados: "+minutostotales2);
		        }
		        if(hayventana)
		        {
		        	pwMilestones.println("");
			        pwMilestones.println("Resultados Documento 1 en la VENTANA:");
			        pwMilestones.println("Tiempo estudiado: " + minutostotalessimulacionventana);
			        pwMilestones.println("Minutoinicio: " + starttimeVentana +". Minuto final: " + finishtimeVentana);
			        pwMilestones.println("Numero de aviones: " + numaviones1ventana);
			        pwMilestones.println("Avión con más retraso: "+ avion1maximoventana[0] + ". Retrasado: "+ avion1maximoventana[1] + "minutos. Realiza " + parametro + " en el minuto: "+ avion1maximoventana[2]);
			        pwMilestones.println("Average de aviones retrasados a cada minuto: "+average1ventana[0]);
			        pwMilestones.println("Average de minutos de retraso que recibe cada avion: "+average1ventana[1]);
			        pwMilestones.println("Cantidad de minutos de retraso aplicados: "+minutostotalesventana1);
			        if(rutas2)
			        {
			        	pwMilestones.println("");
				        pwMilestones.println("Resultados Documento 2 en la VENTANA:");
				        pwMilestones.println("Tiempo estudiado: " + minutostotalessimulacionventana);
				        pwMilestones.println("Minutoinicio: " + starttimeVentana +". Minuto final: " + finishtimeVentana);
				        pwMilestones.println("Numero de aviones: " + numaviones2ventana);
				        pwMilestones.println("Avión con más retraso: "+ avion2maximoventana[0] + ". Retrasado: "+ avion2maximoventana[1] + "minutos. Realiza " + parametro + " en el minuto: "+ avion2maximoventana[2]);
				        pwMilestones.println("Average de aviones retrasados a cada minuto: "+average2ventana[0]);
				        pwMilestones.println("Average de minutos de retraso que recibe cada avion: "+average2ventana[1]);
				        pwMilestones.println("Cantidad de minutos de retraso aplicados: "+minutostotalesventana2);
			        }
		        }
		        pwMilestones.println("");
		        pwMilestones.println("Tabla informativa Documento 1:");
		        i=0;
		        while(i<MinutosRetraso1.size())
		        {
		        	String p=((String) MinutosRetraso1.get(i))+" "+((String) MinutosRetraso1.get(i+1))+" "+((String) MinutosRetraso1.get(i+2));
		        	pwMilestones.println(p);
		        	i=i+3;
		        }
		        if(rutas2)
		        {
			        pwMilestones.println("");
			        pwMilestones.println("Tabla informativa Documento 2:");
			        i=0;
			        while(i<MinutosRetraso2.size())
			        {
			        	String p=((String) MinutosRetraso2.get(i))+" "+((String) MinutosRetraso2.get(i+1))+" "+((String) MinutosRetraso2.get(i+2));
			        	pwMilestones.println(p);
			        	i=i+3;
			        }
		        }
		        ficheroAnalisis.close();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void setTabla(Tabla t, int opcion) // Añadir una tabla de aviones a la tabla interna de la interna de la interfaz
	{
		if(opcion == 1)
		{
			int num = t.contarAviones();
		
			tabla1 = new Tabla(num);
		
			tabla1.setAviones(t.getAviones());
			
			tabla1.contarAviones();
			
			ModelosyAO(tabla1);
		}
		
		if(opcion == 2)
		{
			int num = t.contarAviones();
			
			tabla2 = new Tabla(num);
		
			tabla2.setAviones(t.getAviones());
			
			tabla2.contarAviones();
			
			rutas2=true;
			
			ModelosyAO(tabla2);
			
			lblTabla_2.setVisible(true);
			lblNumAviones2.setVisible(true);
		}
		
		CargarComboBoxes();
	}

	public void ModelosyAO(Tabla tabla) //busca todos los modelos y aerolineas de las tablas y genera las listas.
	{
		int i=0;
		while(i<tabla.getnumero())
		{
			if(aerolineas.contains(tabla.getAvion(i).getAO())==false)
				aerolineas.add(tabla.getAvion(i).getAO());
			if(modelos.contains(tabla.getAvion(i).getModelo())==false)
				modelos.add(tabla.getAvion(i).getModelo());
			i++;
		}
	}
	
	public void CargarComboBoxes()
	{
		// Cargamos los comboBox del panel de los retrasos tipo 2
		Object[] modelosCombo = modelos.toArray();
		comboBox_Modelos.setModel(new DefaultComboBoxModel(modelosCombo));
		
		Object[] aerolineasCombo = aerolineas.toArray();
		comboBox_Aerolineas.setModel(new DefaultComboBoxModel(aerolineasCombo));
		
	}
	
	public void añadirAlFiltro(String tipo) // Añade una aerolinea o modelo al filtro.
	{
		if(tipo.equals("Modelo"))
		{
			String modelo = (String)comboBox_Modelos.getSelectedItem();
			String modelosfiltro = textFieldModelos.getText();
			if(cM == 0)
			{
				textFieldModelos.setText(modelo);
				cM++;
			}
			else
				textFieldModelos.setText(modelosfiltro + "-" + modelo);		
		}
		
		if(tipo.equals("Aerolinea"))
		{
			String aerolinea = (String)comboBox_Aerolineas.getSelectedItem();
			String aerolineasfiltro = textFieldAerolineas.getText();
			if(cA == 0)
			{
				textFieldAerolineas.setText(aerolinea);
				cA++;
			}
			else
				textFieldAerolineas.setText(aerolineasfiltro + "-" + aerolinea);		
		}
		
	}
	
	public void actualizarFiltro() // Guarda los parametros del filtro en la memoria
	{
		aerolineasFiltro = (String)textFieldAerolineas.getText();
		modelosFiltro = (String)textFieldModelos.getText();
		
		System.out.println("Filtro Aerolineas: " + aerolineasFiltro);
		System.out.println("Filtro Modelos: " + modelosFiltro);
	}

	public String sacarParametro(String combo) // Obtiene el parametro del analisis de manera que el programa lo entiende y trabaj con el
	{
		String[] paramm = combo.split(" ");
		String parametro = paramm[0];
		
		return parametro;
	}
	
	public void gettimessimualcion(String tiempos)
	{
		int i = 0;
		while(i < tabla1.getnumero())
		{
			int[] tiempo = gettemps(tabla1.getAvion(i),tiempos); //Los ETOT
			if(tiempo[1]==1)
			{
				if(tiempo[0] < starttime)
					starttime = tiempo[0];
				if(tiempo[0] > finishtime)
					finishtime = tiempo[0];
			}
			i++;
		}
		starttime1=starttime;
		finishtime1=finishtime;
		i=0;
		while(rutas2 && i < tabla2.getnumero())
		{
			int[] tiempo = gettemps(tabla2.getAvion(i),tiempos); //Los ETOT
			if(tiempo[1]==1)
			{
				if(tiempo[0] < starttime)
					starttime = tiempo[0];
				if(tiempo[0] > finishtime)
					finishtime = tiempo[0];
				if(tiempo[0] < starttime2)
					starttime2 = tiempo[0];
				if(tiempo[0] > finishtime2)
					finishtime2 = tiempo[0];
			}
			i++;
		}
	}
	
	public int restatemps (int A, int B)
	//In(int A, int B) out (int C)
	//resta 2 tiempos teniendo en cuenta hhmm
	{
		int hA = A/100;
		int minA = A%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		int hB = B/100;
		int minB = B%100;
		int C=hA*60+minA-hB*60-minB;
		
		return C;
	}
	
	public int sumatemps (int A, int min)
	//In(int A, int min) out (int C)
	//suma A y los minutos min tiempos teniendo en cuenta hhmm
	{
		int hA = A/100;
		int minA = A%100; // El operador % hace la division y se queda con el cociente (p.e: 5%2 = 1)
		
		int C=hA*60+minA+min;
		int horafinal =C/60;
		horafinal=horafinal*100+C-horafinal*60;
		
		return horafinal;
	}
	
	public int[] gettemps (Aircraft avion, String tiempo)
	{
		int[] valor=new int[2];
		valor[0]=0;
		valor[1]=1; //si es un zero, el avion solo sale o solo llega, y se pide info de un tiempo que no tiene por solo salir o solo llegar
		if(tiempo.equals("ETOT'"))
		{
			valor[0]=avion.getValorTiempos(0);
			if (avion.getValorTiempos(1)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ELDT"))
		{
			valor[0]=avion.getValorTiempos(1);
			if (avion.getValorTiempos(0)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ETOT"))
		{
			valor[0]=avion.getValorTiempos(4);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		if(tiempo.equals("EIBT"))
		{
			valor[0]=avion.getValorTiempos(3);
			if (avion.getValorTiempos(1)==0)
				valor[1]=0;
		}
		if(tiempo.equals("EOBT"))
		{
			valor[0]=avion.getValorTiempos(4);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		if(tiempo.equals("TFIR"))
		{
			valor[0]=avion.getValorTiemposVuelo(0);
			if (avion.getValorTiempos(1)==0)
				valor[1]=0;
		}
		if(tiempo.equals("TAPP"))
		{
			valor[0]=avion.getValorTiemposVuelo(1);
			if (avion.getValorTiempos(1)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ACGT"))
		{
			valor[0]=avion.getValorTiemposSecundarios(0);
			if (avion.getValorTiempos(1)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ASBT"))
		{
			valor[0]=avion.getValorTiemposSecundarios(1);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ARDT"))
		{
			valor[0]=avion.getValorTiemposSecundarios(2);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		if(tiempo.equals("ASRT"))
		{
			valor[0]=avion.getValorTiemposSecundarios(3);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		if(tiempo.equals("TSAT"))
		{
			valor[0]=avion.getValorTiemposSecundarios(4);
			if (avion.getValorTiempos(2)==0)
				valor[1]=0;
		}
		
		return valor;
		
		
	}
	
	public void calculosventana(String tiempos)
	{
		//Asseguramos que la ventana esta bien puesta, dentro de los limites 
		if(finishtimeVentana<starttimeVentana)
		{
			int p=starttimeVentana;
			starttimeVentana=finishtimeVentana;
			finishtimeVentana=p;
		}
		if(starttimeVentana<starttime)
			starttimeVentana=starttime;
		if(finishtimeVentana>finishtime)
			finishtimeVentana=finishtime;
		if(finishtimeVentana<starttime)
			hayventana=false;
		
		if(hayventana)
		{
			int iniciventana=restatemps(starttimeVentana,starttime);  //Mirar linea a linea que amb starttime negatiu tot va be
			int finalventana=restatemps(finishtimeVentana,starttime);
			minutostotalessimulacionventana=finalventana-iniciventana;
			analisisTabla1ventana = new int[finalventana-iniciventana];
			vectorXventana = new double[finalventana-iniciventana];
			if(rutas2)
				analisisTabla2ventana = new int[finalventana-iniciventana];
			
			int i=0;
			while(iniciventana<finalventana)
			{
				vectorXventana[i]=iniciventana;
				iniciventana++;
				i++;
			}
			
			//Escaneado
			EscanearMirandotiempo(tiempos,starttimeVentana,finishtimeVentana);
		}
	}
	
	public void EscanearMirandotiempo(String tiempos, int inicio, int fin) 
	//Coger tiempo, filtros por modelo i aerolinea.
	{
		int i=0;
		numaviones1ventana=0;
		minutostotalesventana1=0;
		boolean avionnoactua = false;
		while(i<tabla1.getnumero())
		{
			avionnoactua=false;
			if(gettemps(tabla1.getAvion(i),tiempos)[1]==0)
			{
				avionnoactua=true;
			}
			if(avionnoactua==false)
			{
				int sumaretrasos=0;
				boolean miramosmodelo=true;
				boolean miramosAO=true;
				boolean miramos=false;
				if(aerolineasFiltro.equals("")==false)
				{
					miramosAO=false;
					int p=0;
					String[]palabras=aerolineasFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla1.getAvion(i).getAO().equals(palabras[p]))
						{
							miramosAO=true;
						}
						p++;
					}
				}
				if(modelosFiltro.equals("")==false)
				{
					miramosmodelo=false;
					int p=0;
					String[]palabras=modelosFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla1.getAvion(i).getModelo().equals(palabras[p]))
						{
							miramosmodelo=true;
						}
						p++;
					}
				}
					
				if(miramosmodelo && miramosAO)
				{
					miramos=true;
				}
				boolean encontrado=false;
				if(tabla1.getAvion(i).getIncidencias().equals("")==false && miramos) //Si no te incidencias no interesa
				{
					String[] incidencias = tabla1.getAvion(i).getIncidencias().split("\n");
					int k=0;
					while(k<incidencias.length && encontrado==false) //Mentres tinguem incidencias per mirar seguim. Busquem una de ETOT
					{
						String[] palabras = incidencias[k].split(" ");
						if(palabras[0].equals(tiempos))
						{
							boolean dentroventana=false; //avisa si el avio esta dins la finestra de temps
							int tempsprevist = Integer.parseInt(palabras[3]);
							int tempsreal = gettemps(tabla1.getAvion(i),tiempos)[0];
	
							
							if(tempsprevist<=inicio && tempsreal>=fin)
							{
								tempsprevist=inicio;
								tempsreal=fin;
								dentroventana=true;
								numaviones1ventana++;
							}
							else if(tempsprevist<=inicio && tempsreal>=inicio && tempsreal<=fin)
							{
								tempsprevist=inicio;
								dentroventana=true;
								numaviones1ventana++;
							}
							else if(tempsprevist>=inicio && tempsreal<=fin)
							{
								dentroventana=true;
								numaviones1ventana++;
							}
							else if(tempsprevist>=inicio &&tempsprevist<=fin && tempsreal>=fin)
							{
								tempsreal=fin;
								dentroventana=true;
								numaviones1ventana++;
							}
								
							if(dentroventana)
							{
								int indexiniciretras =  restatemps (tempsprevist, inicio);
								int indexfinalretras=  restatemps (tempsreal, inicio);
								sumaretrasos=sumaretrasos+indexfinalretras-indexiniciretras;
								while(indexiniciretras<indexfinalretras)
								{
									analisisTabla1ventana[indexiniciretras]=analisisTabla1ventana[indexiniciretras]+1;
									indexiniciretras++;
									minutostotalesventana1++;
								}
								if(sumaretrasos>avion1maximoventana[1])
								{
									avion1maximoventana[1]=sumaretrasos;
									avion1maximoventana[0]=i;
									avion1maximoventana[2]=gettemps(tabla1.getAvion(i),tiempos)[0];
								}
							}
							encontrado=true;
						}
						k++;
					}
				}
				if(!encontrado)//Miramos si el avion esta en la ventana
				{
					int tempsreal = gettemps(tabla1.getAvion(i),tiempos)[0];
					tempsreal = restatemps(tempsreal,starttime);
					if (tempsreal>=inicio && tempsreal<=fin)
						numaviones1ventana++;
				}
			}
			i++;
		}
		average1[0]=minutostotalesventana1/minutostotalessimulacionventana;
		if(numaviones1ventana==0)
			average1[1]=0;
		else
			average1[1]=minutostotalesventana1/numaviones1ventana;
		i=0;
		numaviones2ventana=0;
		minutostotalesventana2=0;
		avionnoactua = false;
		while(rutas2 && i<tabla2.getnumero())
		{
			avionnoactua=false;
			if(gettemps(tabla2.getAvion(i),tiempos)[1]==0)
			{
			
				avionnoactua=true;
			}
			if(avionnoactua==false)
			{
				int sumaretrasos=0;
				boolean miramosmodelo=true;
				boolean miramosAO=true;
				boolean miramos=false;
				if(aerolineasFiltro.equals("")==false)
				{
					miramosAO=false;
					int p=0;
					String[]palabras=aerolineasFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla2.getAvion(i).getAO().equals(palabras[p]))
						{
							miramosAO=true;
						}
						p++;
					}
				}
				if(modelosFiltro.equals("")==false)
				{
					miramosmodelo=false;
					int p=0;
					String[]palabras=modelosFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla2.getAvion(i).getModelo().equals(palabras[p]))
						{
							miramosmodelo=true;
						}
						p++;
					}
				}
				if(miramosmodelo && miramosAO)
				{
					miramos=true;
				}
				boolean encontrado=false;
				if(tabla2.getAvion(i).getIncidencias().equals("")==false && miramos) //Si no te incidencias no interesa
				{
					String[] incidencias = tabla2.getAvion(i).getIncidencias().split("\n");
					int k=0;
					while(k<incidencias.length && encontrado==false) //Mentres tinguem incidencias per mirar seguim. Busquem una de ETOT
					{
						String[] palabras = incidencias[k].split(" ");
						if(palabras[0].equals(tiempos))
						{
							boolean dentroventana=false; //avisa si el avio esta dins la finestra de temps
							int tempsprevist = Integer.parseInt(palabras[3]);
							int tempsreal = gettemps(tabla2.getAvion(i),tiempos)[0];
	
							
							if(tempsprevist<=inicio && tempsreal>=fin)
							{
								tempsprevist=inicio;
								tempsreal=fin;
								dentroventana=true;
								numaviones2ventana++;
							}
							else if(tempsprevist<=inicio && tempsreal>=inicio && tempsreal<=fin)
							{
								tempsprevist=inicio;
								dentroventana=true;
								numaviones2ventana++;
							}
							else if(tempsprevist>=inicio && tempsreal<=fin)
							{
								dentroventana=true;
								numaviones2ventana++;
							}
							else if(tempsprevist>=inicio &&tempsprevist<=fin && tempsreal>=fin)
							{
								tempsreal=fin;
								dentroventana=true;
								numaviones2ventana++;
							}
								
							if(dentroventana)
							{
								int indexiniciretras =  restatemps (tempsprevist, inicio);
								int indexfinalretras=  restatemps (tempsreal, inicio);
								sumaretrasos=indexfinalretras-indexiniciretras;
								while(indexiniciretras<indexfinalretras)
								{
									analisisTabla2ventana[indexiniciretras]=analisisTabla2ventana[indexiniciretras]+1;
									indexiniciretras++;
									minutostotalesventana2++;
								}
								if(sumaretrasos>avion2maximoventana[1])
								{
									avion2maximoventana[1]=sumaretrasos;
									avion2maximoventana[0]=i;
									avion2maximoventana[2]=gettemps(tabla2.getAvion(i),tiempos)[0];
								}
							}
							encontrado=true;
						}
						k++;
					}
				}
				if(!encontrado)//Miramos si el avion esta en la ventana
				{
					int tempsreal = gettemps(tabla2.getAvion(i),tiempos)[0];
					tempsreal = restatemps(tempsreal,starttime);
					if (tempsreal>=inicio && tempsreal<=fin)
						numaviones2ventana++;
				}
			}
			i++;
		}
		average2[0]=minutostotalesventana2/minutostotalessimulacionventana;
		if(numaviones2ventana==0)
			average2[1]=0;
		else
			average2[1]=minutostotalesventana2/numaviones2ventana;
		
		
		
		vectorTablaventana1 = new double[analisisTabla1ventana.length];
		i=0;
		while(i<analisisTabla1ventana.length)
		{
			vectorTablaventana1[i]=analisisTabla1ventana[i];
			i++;
		}

		if(rutas2)
		{
			vectorTablaventana2 = new double[analisisTabla2ventana.length];
			i=0;
			while(i<analisisTabla2ventana.length)
			{
				vectorTablaventana2[i]=analisisTabla2ventana[i];
				i++;
			}
		}
	}
	
	public void EscanearMirandotiempo(String tiempos)
	//Coger tiempo, filtros por modelo i aerolinea.
	{
		String[] frase= {"avion", "Retraso de "+tiempos, "Tiempo que ocurre"};
		MinutosRetraso1.add(frase[0]);
		MinutosRetraso1.add(frase[1]);
		MinutosRetraso1.add(frase[2]);
		if(rutas2)
			MinutosRetraso2.add(frase[0]);
			MinutosRetraso2.add(frase[1]);
			MinutosRetraso2.add(frase[2]);
		int i=0;
		numaviones1=0;
		minutostotales1=0;
		boolean avionnoactua = false;
		while(i<tabla1.getnumero())
		{
			avionnoactua=false;
			if(gettemps(tabla1.getAvion(i),tiempos)[1]==0)
			{
				avionnoactua=true;
			}
			if(avionnoactua==false)
			{
				frase = new String[3];
				frase[0]=tabla1.getAvion(i).getId();
				frase[1]="0";
				frase[2]=gettemps(tabla1.getAvion(i),tiempos)[0]+"";
				int sumaretrasos=0;
				boolean miramosmodelo=true;
				boolean miramosAO=true;
				boolean miramos=false;
				if(aerolineasFiltro.equals("")==false)
				{
					miramosAO=false;
					int p=0;
					String[]palabras=aerolineasFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla1.getAvion(i).getAO().equals(palabras[p]))
						{
							miramosAO=true;
						}
						p++;
					}
				}
				if(modelosFiltro.equals("")==false)
				{
					miramosmodelo=false;
					int p=0;
					String[]palabras=modelosFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla1.getAvion(i).getModelo().equals(palabras[p]))
						{
							miramosmodelo=true;
						}
						p++;
					}
				}
					
				if(miramosmodelo && miramosAO)
				{
					miramos=true;
					numaviones1++;
				}
					
				if(tabla1.getAvion(i).getIncidencias().equals("")==false && miramos) //Si no te incidencias no interesa
				{
					String[] incidencias = tabla1.getAvion(i).getIncidencias().split("\n");
					boolean encontrado=false;
					int k=0;
					while(k<incidencias.length && encontrado==false) //Mentres tinguem incidencias per mirar seguim. Busquem una de ETOT
					{
						String[] palabras = incidencias[k].split(" ");
						if(palabras[0].equals(tiempos))
						{
							int tempsprevist = Integer.parseInt(palabras[3]);
							int tempsreal = gettemps(tabla1.getAvion(i),tiempos)[0];
							int indexiniciretras =  restatemps (tempsprevist, starttime);
							int indexfinalretras=  restatemps (tempsreal, starttime);
							sumaretrasos=sumaretrasos+indexfinalretras-indexiniciretras;
							while(indexiniciretras<indexfinalretras)
							{
								analisisTabla1[indexiniciretras]=analisisTabla1[indexiniciretras]+1;
								indexiniciretras++;
								minutostotales1++;
								int valor =Integer.parseInt(frase[1])+1;
								frase[1]=valor+"";
							}
							if(sumaretrasos>avion1maximo[1])
							{
								avion1maximo[1]=sumaretrasos;
								avion1maximo[0]=i;
								avion1maximo[2]=gettemps(tabla1.getAvion(i),tiempos)[0];
							}
							encontrado=true;
						}
						k++;
					}
				}
				MinutosRetraso1.add(frase[0]);
				MinutosRetraso1.add(frase[1]);
				MinutosRetraso1.add(frase[2]);
			}
			i++;
		}
		average1[0]=minutostotales1/minutostotalessimulacion1;
		if(numaviones1==0)
			average1[1]=0;
		else
			average1[1]=minutostotales1/numaviones1;
		i=0;
		numaviones2=0;
		minutostotales2=0;
		while(rutas2 && i<tabla2.getnumero())
		{
			avionnoactua=false;
			if(gettemps(tabla2.getAvion(i),tiempos)[1]==0)
			{
				avionnoactua=true;
			}
			if(avionnoactua==false)
			{
				frase = new String[3];
				frase[0]=tabla2.getAvion(i).getId();
				frase[1]="0";
				frase[2]=gettemps(tabla2.getAvion(i),tiempos)[0]+"";
				int sumaretrasos=0;
				boolean miramosmodelo=true;
				boolean miramosAO=true;
				boolean miramos=false;
				if(aerolineasFiltro.equals("")==false)
				{
					miramosAO=false;
					int p=0;
					String[]palabras=aerolineasFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla2.getAvion(i).getAO().equals(palabras[p]))
						{
							miramosAO=true;
						}
						p++;
					}
				}
				if(modelosFiltro.equals("")==false)
				{
					miramosmodelo=false;
					int p=0;
					String[]palabras=modelosFiltro.split("-");
					while(p<palabras.length)
					{
						if(tabla2.getAvion(i).getModelo().equals(palabras[p]))
						{
							miramosmodelo=true;
						}
						p++;
					}
				}
				if(miramosmodelo && miramosAO)
				{
					miramos=true;
					numaviones2++;
				}
				if(tabla2.getAvion(i).getIncidencias().equals("") == false && miramos) //Si no te incidencias no interesa
				{
					String[] incidencias = tabla2.getAvion(i).getIncidencias().split("\n");
					boolean encontrado=false;
					int k=0;
					while(k<incidencias.length && encontrado==false) //Mentres tinguem incidencias per mirar seguim. Busquem una de ETOT
					{
						String[] palabras = incidencias[k].split(" ");
						if(palabras[0].equals(tiempos))
						{
							int tempsprevist = Integer.parseInt(palabras[3]);
							int tempsreal = gettemps(tabla2.getAvion(i),tiempos)[0];
							int indexiniciretras =  restatemps (tempsprevist, starttime);
							int indexfinalretras=  restatemps (tempsreal, starttime);
							sumaretrasos=indexfinalretras-indexiniciretras;
							while(indexiniciretras<indexfinalretras)
							{
								analisisTabla2[indexiniciretras]=analisisTabla2[indexiniciretras]+1;
								indexiniciretras++;
								minutostotales2++;
								int valor =Integer.parseInt(frase[1])+1;
								frase[1]=valor+"";
							}
							if(sumaretrasos>avion2maximo[1])
							{
								avion2maximo[1]=sumaretrasos;
								avion2maximo[0]=i;
								avion2maximo[2]=gettemps(tabla2.getAvion(i),tiempos)[0];
							}
							encontrado=true;
						}
						k++;
					}
				}
				MinutosRetraso2.add(frase[0]);
				MinutosRetraso2.add(frase[1]);
				MinutosRetraso2.add(frase[2]);
			}
			i++;
		}
		if(rutas2)
		{
			average2[0]=minutostotales2/minutostotalessimulacion2;
			if(numaviones2==0)
				average2[1]=0;
			else
				average2[1]=minutostotales2/numaviones2;
		}
	}
	
	public void setRuta(String r)
	{
		Ruta = r;
		
		String[] rr = r.split("\\.");
		if(rr[rr.length-1].equals("txt"))
			Ruta = cortarRuta(r);
	}
	
	public int[] contarAvionesTablas()// Calcula el numero de aviones que habrá en cada una de las listas, a causa de el filtro
	{
		int[] numaviones=new int[2];
		int i=0;
		while(i<tabla1.getnumero())
		{
			boolean miramosmodelo=true;
			boolean miramosAO=true;
			if(aerolineasFiltro.equals("")==false)
			{
				miramosAO=false;
				int p=0;
				String[]palabras=aerolineasFiltro.split("-");
				while(p<palabras.length)
				{
					if(tabla1.getAvion(i).getAO().equals(palabras[p]))
					{
						miramosAO=true;
					}
					p++;
				}
			}
			if(modelosFiltro.equals("")==false)
			{
				miramosmodelo=false;
				int p=0;
				String[]palabras=modelosFiltro.split("-");
				while(p<palabras.length)
				{
					if(tabla1.getAvion(i).getModelo().equals(palabras[p]))
					{
						miramosmodelo=true;
					}
					p++;
				}
			}
				
			if(miramosmodelo && miramosAO)
			{
				numaviones[0]++;
			}
			i++;
		}
		
		i=0;
		while( rutas2 && i<tabla2.getnumero())
		{
			boolean miramosmodelo=true;
			boolean miramosAO=true;
			if(aerolineasFiltro.equals("")==false)
			{
				miramosAO=false;
				int p=0;
				String[]palabras=aerolineasFiltro.split("-");
				while(p<palabras.length)
				{
					if(tabla2.getAvion(i).getAO().equals(palabras[p]))
					{
						miramosAO=true;
					}
					p++;
				}
			}
			if(modelosFiltro.equals("")==false)
			{
				miramosmodelo=false;
				int p=0;
				String[]palabras=modelosFiltro.split("-");
				while(p<palabras.length)
				{
					if(tabla2.getAvion(i).getModelo().equals(palabras[p]))
					{
						miramosmodelo=true;
					}
					p++;
				}
			}
				
			if(miramosmodelo && miramosAO)
			{
				numaviones[1]++;
			}
			i++;
		}
		return numaviones;
		
	}

	public void actualizarVentana() //Guarda los parametros de la ventana en la memoria
	{
		int horaInicioVentana = (int)spinner_HoraInicio.getValue();
		int minutoInicioVentana = (int)spinner_MinInicio.getValue();
		int horaFinalVentana = (int)spinner_HoraFin.getValue();
		int minutoFinalVentana = (int)spinner_MinFin.getValue();
		
		starttimeVentana = horaInicioVentana*100 + minutoInicioVentana;
		finishtimeVentana = horaFinalVentana*100 + minutoFinalVentana;
		
		System.out.println("Hora Inicio Ventana: " + starttimeVentana);
		System.out.println("Hora Final Ventana: " + finishtimeVentana);
	}

	public void rellenarPanelDatos()
	{
		resetPanelDatos();
		
		if(hayventana) // Se ha definido una ventana de tiempo
		{
			txt_HoraInicioVentana.setText(starttimeVentana + "");
			txt_HoraFinVentana.setText(finishtimeVentana + "");
			txt_MinTotal.setText(minutostotalessimulacionventana + "");
			
			txt_NumA1.setText(numaviones1ventana + "");
			txt_MinDelay1.setText(minutostotalesventana1 + "");
			txt_Avmin1.setText(average1ventana[0] + "");
			txt_MinAv1.setText(average1ventana[1] + "");
			if(avion1maximoventana[1] == 0)
				txt_AvMax1.setText(" - ");
			else
				txt_AvMax1.setText(tabla1.getAvion(avion1maximoventana[0]).getId() + "");
			txt_DelayMax1.setText(avion1maximoventana[1] + "");
			txt_Tparam1.setText(avion1maximoventana[2] + "");
			
			txt_NumA2.setText(numaviones2ventana + "");
			txt_MinDelay2.setText(minutostotalesventana2 + "");
			txt_Avmin2.setText(average2ventana[0] + "");
			txt_MinAv2.setText(average2ventana[1] + "");
			if(avion2maximoventana[1] == 0)
				txt_AvMax2.setText(" - ");
			else
				txt_AvMax2.setText(tabla2.getAvion(avion2maximoventana[0]).getId() + "");
			txt_DelayMax2.setText(avion2maximoventana[1] + "");
			txt_Tparam2.setText(avion2maximoventana[2] + "");
			
			lblParametro.setText(parametro + " a las");
		}
		
		if(!hayventana) // No hay ventana, se analizan la totalidad de los vuelos
		{
			txt_HoraInicioVentana.setText(starttime + "");
			txt_HoraFinVentana.setText(finishtime + "");
			txt_MinTotal.setText(minutostotalessimulacion + "");
			
			txt_NumA1.setText(numaviones1 + "");
			txt_MinDelay1.setText(minutostotales1 + "");
			txt_Avmin1.setText(average1[0] + "");
			txt_MinAv1.setText(average1[1] + "");
			if(avion1maximo[1] == 0)
				txt_AvMax1.setText(" - ");
			else
				txt_AvMax1.setText(tabla1.getAvion(avion1maximo[0]).getId() + "");
			txt_DelayMax1.setText(avion1maximo[1] + "");
			txt_Tparam1.setText(avion1maximo[2] + "");
			
			txt_NumA2.setText(numaviones2 + "");
			txt_MinDelay2.setText(minutostotales2 + "");
			txt_Avmin2.setText(average2[0] + "");
			txt_MinAv2.setText(average2[1] + "");
			if(avion2maximo[1] == 0)
				txt_AvMax2.setText(" - ");
			else
				txt_AvMax2.setText(tabla2.getAvion(avion2maximo[0]).getId() + "");
			txt_DelayMax2.setText(avion2maximo[1] + "");
			txt_Tparam2.setText(avion2maximo[2] + "");
			
			lblParametro.setText(parametro + " a las");
		}
	}

	public void resetPanelDatos()
	{
		txt_HoraInicioVentana.setText("");
		txt_HoraFinVentana.setText("");
		txt_MinTotal.setText("");
		
		txt_NumA1.setText("");
		txt_MinDelay1.setText("");
		txt_Avmin1.setText("");
		txt_MinAv1.setText("");
		txt_AvMax1.setText("");
		txt_DelayMax1.setText("");
		txt_Tparam1.setText("");
		
		txt_NumA2.setText("");
		txt_MinDelay2.setText("");
		txt_Avmin2.setText("");
		txt_MinAv2.setText("");
		txt_AvMax2.setText("");
		txt_DelayMax2.setText("");
		txt_Tparam2.setText("");
		
		lblParametro.setText("XXXX"+ " a las");
		
	}

	public String cortarRuta(String r)
	{
		String[] rutaarchivo = r.split("/");
		
	// Detectamos si se ha introducido la ruta con "/" o con "\"
		if(rutaarchivo.length == 1){ 
			rutaarchivo = r.split("\\\\");		
		}
			// Montamos la ruta a la carpeta
		String rutacarpeta = "";
		
		int i = 0;
		while(i < rutaarchivo.length-1){
			rutacarpeta = rutacarpeta + rutaarchivo[i]+"/";
			i++;
		}
		
		return rutacarpeta;
		
	}
}
