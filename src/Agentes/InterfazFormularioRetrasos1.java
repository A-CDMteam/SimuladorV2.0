package Agentes;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.*;

import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class InterfazFormularioRetrasos1 extends JFrame {

	private JPanel contentPane;
	JComboBox comboBox;
	String RUTA;// = "C:/Users/Ivan/workspace/SimuladorV1.5/VuelosAerolinea.txt";
	JButton btnAceptar;
	int c = 0;
	int cM = 0;

	int numAviones;
	String[] RetrasoDef = new String[10];
	
	String[][] datos;
	ArrayList aerolineas = new ArrayList();
	ArrayList modelos = new ArrayList();
	ArrayList aviones = new ArrayList();

	JComboBox comboBox_AvionesTipo2;
	JComboBox comboBox_AerolineasTipo2;
	JComboBox comboBox_ModelosTipo2;
	JLabel label_numAerolineasTipo2;
	JLabel label_numModelosTipo2;
	JLabel label_NumAvionesTipo2;
	JLabel label_NumAerolineasTipo1;
	JLabel label_NumModelosTipo1;
	JComboBox comboBox_MilestoneT1;
	JComboBox comboBox_MilestoneTipo1;
	JSpinner spinnerHInicio_h;
	JSpinner spinnerHInicio_min;
	JSpinner SpinnerMinRT1;
	JSpinner spinnerHFinal_h;
	JSpinner spinnerHFinal_min;
	JSpinner spinnerHCon_h;
	JSpinner spinnerHCon_min;
	
	
	Boolean definido = false;
	Boolean Aceptar = false;

	// Panel Retrasos tipo 2
	JTabbedPane panel_Tipos1_2;
	JPanel panel_Tipo4;
	JPanel panel_Tipo3;
	private JTextField textSituaciónTipo2;
	private JTextField textFieldComentariosTipo2;
	private JTable tablaAvionSelectTipo2;
	DefaultTableModel modeloAvSelectTipo2;
	JSpinner minRetrasoTipo2;
	JComboBox comboBox_MilestoneTipo2;
	JComboBox comboBox_AerolineasT1;
	JComboBox comboBox_ModelosT1;
	JTextField textField_AerolineasT1;
	JTextField textField_ModelosT1;
	JTextField textField_SituacionT1;
	JTextField textField_ComentariosT1;
	
	// Labels de la Vista Previa:
	JLabel lbltipo;
	JLabel labelSituacion;
	JLabel labelID;
	JLabel labelMinR;
	JLabel labelParametro;
	JLabel labelAgente;
	JLabel labelMilestone;
	JLabel labelHCon;
	JLabel labelHInicio;
	JLabel labelHFinal;
	JLabel labelDep;
	JLabel labelArr;
	JLabel labelComent;
	private JTextField textField_ComentariosT4;
	private JSpinner spinnerInh;
	private JSpinner spinnerInmin;
	private JSpinner spinnerFinh;
	private JSpinner spinnerFinmin;
	private JSpinner spinnerConh;
	private JSpinner spinnerConmin;
	private JCheckBox checkBoxDepartures;
	private JCheckBox checkBoxArrivals;
	private JSpinner spinnerDep;
	private JSpinner spinnerArr;
	private JComboBox comboBox_AvionesT3;
	private JComboBox comboBox_AerolineasT3;
	private JComboBox comboBox_ModelosT3;
	private JLabel label_numAvionesT3;
	private JLabel label_numAerolineasT3;
	private JLabel label_numModelosT3;
	private JTable tableAvionT3;
	DefaultTableModel modeloAvSelectT3;
	private JTextField textField_ComentariosT3;
	private JComboBox comboBox_TiempoT3;
	private JComboBox comboBox_MilT3;
	private JComboBox comboBox_AgenteT3;
	private JSpinner spinnerMinRT3;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazFormularioRetrasos1 frame = new InterfazFormularioRetrasos1();
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
	public InterfazFormularioRetrasos1() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1044, 678);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panelTipoRetrasos = new JPanel();
		panelTipoRetrasos.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"[tipo1-2] Problemas meteo FIR", "[tipo1-2] Problemas Taxiway IN", "[tipo1-2] Problemas Taxiway OUT", "[tipo1-2] Problemas GH", "[tipo3] Retraso un avi\u00F3n", "[tipo4] Reducci\u00F3n Capacidad de Pista"}));
		comboBox.setToolTipText("");
		
		comboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
				
				try {
					lecturaArchivo();
					CargarComboBoxes();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null,"No se encuentra el archivo con el trafico aereo");
				}
				
				String tipo = (String)comboBox.getSelectedItem();
				int opcion = (int)comboBox.getSelectedIndex();
				
				resetVistaPrevia();

				activarPaneles(opcion);  
			}
		}
		);
		
		JLabel lblTipoRetraso = new JLabel("Tipo Retraso:");
		lblTipoRetraso.setFont(new Font("Verdana", Font.BOLD, 11));
		GroupLayout gl_panelTipoRetrasos = new GroupLayout(panelTipoRetrasos);
		gl_panelTipoRetrasos.setHorizontalGroup(
			gl_panelTipoRetrasos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTipoRetrasos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelTipoRetrasos.createParallelGroup(Alignment.LEADING)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTipoRetraso, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(376, Short.MAX_VALUE))
		);
		gl_panelTipoRetrasos.setVerticalGroup(
			gl_panelTipoRetrasos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTipoRetrasos.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTipoRetraso, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(16, Short.MAX_VALUE))
		);
		panelTipoRetrasos.setLayout(gl_panelTipoRetrasos);
		
		JPanel panel = new JPanel();
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JLayeredPane layeredPane = new JLayeredPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(layeredPane, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
								.addComponent(panelTipoRetrasos, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
							.addGap(10))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelTipoRetrasos, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
					.addGap(8)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
						.addComponent(layeredPane, GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
		);
		
		panel_Tipo4 = new JPanel();
		panel_Tipo4.setBounds(0, 0, 717, 484);
		panel_Tipo4.setVisible(false);
		
		panel_Tipo3 = new JPanel();
		panel_Tipo3.setBounds(0, 0, 717, 484);
		panel_Tipo3.setVisible(false);
		
		comboBox_ModelosTipo2 = new JComboBox();
		comboBox_AerolineasTipo2 = new JComboBox();
		comboBox_AvionesTipo2 = new JComboBox();
		
		label_numModelosTipo2 = new JLabel("...");
		label_numAerolineasTipo2 = new JLabel("...");
		label_NumAvionesTipo2 = new JLabel("...");
		
		
		panel_Tipos1_2 = new JTabbedPane(JTabbedPane.TOP);
		panel_Tipos1_2.setBounds(0, 0, 717, 484);
		layeredPane.add(panel_Tipos1_2);
		
		panel_Tipos1_2.setVisible(false);
		
		JPanel panel_Multiavion = new JPanel();
		panel_Tipos1_2.addTab("[Tipo 1] Multi Avión", null, panel_Multiavion, null);
		
		JLabel lblFiltro_1 = new JLabel("Filtro:");
		lblFiltro_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel lblAerolineasSeleccionadas = new JLabel("Aerolineas Seleccionadas:");
		
		JLabel lblModelosSeleccionados = new JLabel("Modelos Seleccionados:");
		
		textField_AerolineasT1 = new JTextField();
		textField_AerolineasT1.setText("-");
		textField_AerolineasT1.setEditable(false);
		textField_AerolineasT1.setColumns(10);
		
		textField_ModelosT1 = new JTextField();
		textField_ModelosT1.setText("-");
		textField_ModelosT1.setEditable(false);
		textField_ModelosT1.setColumns(10);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				textField_AerolineasT1.setText("-");
				textField_ModelosT1.setText("-");
				c = 0;
				cM = 0;
			}
		});
		
		JLabel label = new JLabel("Retraso:");
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel label_1 = new JLabel("Minutos Retraso");
		
		SpinnerMinRT1 = new JSpinner();
		SpinnerMinRT1.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		
		JLabel lblMilestone = new JLabel("Milestone:");
		comboBox_MilestoneTipo1= new JComboBox();
		comboBox_MilestoneTipo1.setModel(new DefaultComboBoxModel(new String[] {"3 [TakeOff aeropuerto origen]", "4 [Entrada en FIR]", "5 [Inicio Final Approach]", "6 [Landing]", "7 [IN Block]", "8 [Inico Ground Handling]", "9 [Confirmaci\u00F3n TOBT]", "10 [Definici\u00F3n TSAT]", "11 [Embarque]", "12 [Aircraft Ready]", "13 [Start Up Approval Requested]", "14 [Start Up Approved]", "15 [OFF Block]"}));
		
		JLabel label_3 = new JLabel("Situaci\u00F3n:");
		
		textField_SituacionT1 = new JTextField();
		textField_SituacionT1.setEditable(false);
		textField_SituacionT1.setColumns(10);
		
		textField_ComentariosT1 = new JTextField();
		textField_ComentariosT1.setColumns(10);
		
		JLabel label_4 = new JLabel("Comentarios:");
		
		JButton btnVistaPreviaT1 = new JButton("Vista Previa");
		btnVistaPreviaT1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			
				definirRetrasoTipo1();
				definido = true;
			}
		});
		
		JLabel lblAplicacinDelRetraso = new JLabel("Aplicaci\u00F3n del Retraso:");
		
		JLabel lblHoraInicio = new JLabel("Hora Inicio:");
		
		spinnerHInicio_h = new JSpinner();
		spinnerHInicio_h.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH = new JLabel("h");
		
		spinnerHInicio_min = new JSpinner();
		spinnerHInicio_min.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin = new JLabel("min");
		
		JLabel lblHoraFinal = new JLabel("Hora Final: ");
		
		spinnerHFinal_h = new JSpinner();
		spinnerHFinal_h.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH_1 = new JLabel("h");
		
		spinnerHFinal_min = new JSpinner();
		spinnerHFinal_min.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin_1 = new JLabel("min");
		
		JLabel lblHoraConocimiento_1 = new JLabel("Hora Conocimiento:");
		
		spinnerHCon_h = new JSpinner();
		spinnerHCon_h.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH_2 = new JLabel("h");
		
		spinnerHCon_min = new JSpinner();
		spinnerHCon_min.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin_2 = new JLabel("min");
		
		GroupLayout gl_panel_Multiavion = new GroupLayout(panel_Multiavion);
		gl_panel_Multiavion.setHorizontalGroup(
			gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Multiavion.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_Multiavion.createSequentialGroup()
									.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING, false)
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(lblModelosSeleccionados)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(textField_ModelosT1))
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(lblAerolineasSeleccionadas)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(textField_AerolineasT1, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)))
									.addGap(6)
									.addComponent(btnClear))
								.addComponent(lblFiltro_1)
								.addGroup(gl_panel_Multiavion.createSequentialGroup()
									.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField_ComentariosT1, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)))
							.addGap(69))
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addComponent(lblAplicacinDelRetraso)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_Multiavion.createSequentialGroup()
									.addComponent(lblHoraConocimiento_1)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinnerHCon_h, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblH_2)
									.addPreferredGap(ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
									.addComponent(spinnerHCon_min, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblMin_2)
									.addGap(359))
								.addGroup(gl_panel_Multiavion.createSequentialGroup()
									.addComponent(lblMilestone)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBox_MilestoneTipo1, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
								.addGroup(gl_panel_Multiavion.createSequentialGroup()
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING, false)
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(lblHoraFinal)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerHFinal_h, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(lblHoraInicio)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerHInicio_h, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblH)
										.addComponent(lblH_1))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING, false)
										.addComponent(spinnerHFinal_min, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(spinnerHInicio_min, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
										.addComponent(lblMin)
										.addComponent(lblMin_1))
									.addGap(96)
									.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(textField_SituacionT1, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panel_Multiavion.createSequentialGroup()
											.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(SpinnerMinRT1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
									.addGap(104))))
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addComponent(btnVistaPreviaT1)
							.addContainerGap(613, Short.MAX_VALUE))))
		);
		gl_panel_Multiavion.setVerticalGroup(
			gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Multiavion.createSequentialGroup()
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addGap(32)
							.addComponent(lblFiltro_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addGap(65)
							.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblAerolineasSeleccionadas)
								.addComponent(textField_AerolineasT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblModelosSeleccionados)
								.addComponent(textField_ModelosT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_Multiavion.createSequentialGroup()
							.addGap(82)
							.addComponent(btnClear)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAplicacinDelRetraso)
						.addComponent(lblHoraInicio)
						.addComponent(spinnerHInicio_h, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblH)
						.addComponent(spinnerHInicio_min, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMin)
						.addComponent(label_3)
						.addComponent(textField_SituacionT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraFinal)
						.addComponent(spinnerHFinal_h, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblH_1)
						.addComponent(spinnerHFinal_min, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMin_1)
						.addComponent(label_1)
						.addComponent(SpinnerMinRT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraConocimiento_1)
						.addComponent(spinnerHCon_h, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblH_2)
						.addComponent(spinnerHCon_min, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMin_2))
					.addGap(21)
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMilestone)
						.addComponent(comboBox_MilestoneTipo1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_Multiavion.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_4)
						.addComponent(textField_ComentariosT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(7)
					.addComponent(btnVistaPreviaT1)
					.addContainerGap())
		);
		
		comboBox_AerolineasT1 = new JComboBox();
		comboBox_AerolineasT1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String selection = (String)comboBox_AerolineasT1.getSelectedItem();
				String selected = (String)textField_AerolineasT1.getText();
				
				if(c == 0)
				{
					textField_AerolineasT1.setText(selection);
					c++;
				}
				else
					textField_AerolineasT1.setText(selected + "-" + selection);
				
			}
		}
		);
		
		JLabel lblAerolineas_1 = new JLabel("Aerolineas:");
		
		label_NumAerolineasTipo1 = new JLabel("...");
		
		JLabel lblModelos_1 = new JLabel("Modelos:");
		
		comboBox_ModelosT1 = new JComboBox();
		comboBox_ModelosT1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String selection = (String)comboBox_ModelosT1.getSelectedItem();
				String selected = (String)textField_ModelosT1.getText();
				
				if(cM == 0)
				{
					textField_ModelosT1.setText(selection);
					cM++;
				}
				else
					textField_ModelosT1.setText(selected + "-" + selection);
				
			}
		}
		);
		
		label_NumModelosTipo1 = new JLabel("...");
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAerolineas_1)
						.addComponent(lblModelos_1)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(comboBox_ModelosT1, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(comboBox_AerolineasT1, Alignment.LEADING, 0, 128, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
								.addComponent(label_NumModelosTipo1)
								.addComponent(label_NumAerolineasTipo1))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAerolineas_1)
					.addGap(4)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_AerolineasT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_NumAerolineasTipo1))
					.addGap(18)
					.addComponent(lblModelos_1)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_ModelosT1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_NumModelosTipo1))
					.addContainerGap(41, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		panel_Multiavion.setLayout(gl_panel_Multiavion);
		
		JPanel panel_AvionConcreto = new JPanel();
		panel_Tipos1_2.addTab("[Tipo 2] Avión Concreto", null, panel_AvionConcreto, null);
		
		JLabel lblAvin = new JLabel("Avi\u00F3n:");
		lblAvin.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JPanel panelFiltro = new JPanel();
		panelFiltro.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel lblFiltro = new JLabel("Filtro: ");
		
		JLabel lblRetraso = new JLabel("Retraso:");
		lblRetraso.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		minRetrasoTipo2 = new JSpinner();
		minRetrasoTipo2.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		
		JLabel lblMinutosRetraso = new JLabel("Minutos Retraso");
		
		JLabel lblTipo = new JLabel("Situaci\u00F3n:");
		
		textSituaciónTipo2 = new JTextField();
		textSituaciónTipo2.setEditable(false);
		textSituaciónTipo2.setColumns(10);
		
		JLabel lblComentaris = new JLabel("Comentarios:");
		
		JLabel lblMilestoneConeixement = new JLabel("Milestone Conocimiento:");
		
		textFieldComentariosTipo2 = new JTextField();
		textFieldComentariosTipo2.setColumns(10);
		
		JButton btnVistaPreviaTipo2 = new JButton("Vista Previa");
		btnVistaPreviaTipo2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				definirRetrasoTipo2();
			}
		});
		
		JLabel lblAvinSeleccionado = new JLabel("Avi\u00F3n seleccionado");
		
		JScrollPane scrollPane = new JScrollPane();
		
		comboBox_MilestoneTipo2= new JComboBox();
		comboBox_MilestoneTipo2.setModel(new DefaultComboBoxModel(new String[] {"3 [TakeOff aeropuerto origen]", "4 [Entrada en FIR]", "5 [Inicio Final Approach]", "6 [Landing]", "7 [IN Block]", "8 [Inico Ground Handling]", "9 [Confirmaci\u00F3n TOBT]", "10 [Definici\u00F3n TSAT]", "11 [Embarque]", "12 [Aircraft Ready]", "13 [Start Up Approval Requested]", "14 [Start Up Approved]", "15 [OFF Block]"}));
		
		GroupLayout gl_panel_AvionConcreto = new GroupLayout(panel_AvionConcreto);
		gl_panel_AvionConcreto.setHorizontalGroup(
			gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAvin)
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addComponent(lblMinutosRetraso)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(minRetrasoTipo2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblRetraso)
						.addComponent(btnVistaPreviaTipo2)
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addComponent(lblMilestoneConeixement)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBox_MilestoneTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblTipo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textSituaciónTipo2, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addComponent(lblComentaris, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldComentariosTipo2, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addGap(30)
							.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBox_AvionesTipo2, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(label_NumAvionesTipo2, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.TRAILING, false)
									.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
										.addComponent(lblFiltro)
										.addGap(153))
									.addComponent(panelFiltro, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)))
							.addGap(39)
							.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING, false)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAvinSeleccionado))))
					.addGap(59))
		);
		gl_panel_AvionConcreto.setVerticalGroup(
			gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
					.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblAvin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.BASELINE)
								.addComponent(label_NumAvionesTipo2)
								.addComponent(comboBox_AvionesTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(28)
							.addComponent(lblFiltro)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelFiltro, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblRetraso)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.BASELINE)
								.addComponent(minRetrasoTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMinutosRetraso)))
						.addGroup(gl_panel_AvionConcreto.createSequentialGroup()
							.addGap(34)
							.addComponent(lblAvinSeleccionado)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)))
					.addGap(18)
					.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMilestoneConeixement)
						.addComponent(comboBox_MilestoneTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTipo)
						.addComponent(textSituaciónTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_AvionConcreto.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComentaris)
						.addComponent(textFieldComentariosTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnVistaPreviaTipo2)
					.addGap(9))
		);
		
		tablaAvionSelectTipo2 = new JTable();
		tablaAvionSelectTipo2.setRowSelectionAllowed(false);
		tablaAvionSelectTipo2.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null},
			},
			new String[] {
				"ID Avi\u00F3n", "Aerol\u00EDnea", "Modelo", "ELDT", "ETOT"
			}
		));
		modeloAvSelectTipo2 = (DefaultTableModel) tablaAvionSelectTipo2.getModel();
		
		scrollPane.setViewportView(tablaAvionSelectTipo2);
		
		JLabel lblAerolineas = new JLabel("Aerolineas:");
		JLabel lblModelos = new JLabel("Modelos: ");
		
		comboBox_AvionesTipo2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
				
				String id = (String)comboBox_AvionesTipo2.getSelectedItem();
				String[] av = getdatos(id);
				
				actualizarTablaAvionSelect(av, 2);
				
				
				
			}
		}
		);
		
		comboBox_AerolineasTipo2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String filtroAerolinea = (String)comboBox_AerolineasTipo2.getSelectedItem();
				
				String filtroModelo = (String) comboBox_ModelosTipo2.getSelectedItem();
				
				ArrayList nuevaLista = CargarAvionesFiltrado(filtroAerolinea, filtroModelo);
				
				actualizarComboBox(comboBox_AvionesTipo2, nuevaLista);
				
			}
		}
		);
		
		comboBox_ModelosTipo2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String filtroAerolinea = (String)comboBox_AerolineasTipo2.getSelectedItem();
				
				String filtroModelo = (String) comboBox_ModelosTipo2.getSelectedItem();
				
				ArrayList nuevaLista = CargarAvionesFiltrado(filtroAerolinea, filtroModelo);
				
				actualizarComboBox(comboBox_AvionesTipo2, nuevaLista);
				
			}
		}
		);
		
		GroupLayout gl_panelFiltro = new GroupLayout(panelFiltro);
		gl_panelFiltro.setHorizontalGroup(
			gl_panelFiltro.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFiltro.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAerolineas)
						.addComponent(lblModelos)
						.addGroup(gl_panelFiltro.createSequentialGroup()
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(comboBox_ModelosTipo2, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(comboBox_AerolineasTipo2, Alignment.LEADING, 0, 124, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelFiltro.createParallelGroup(Alignment.LEADING)
								.addComponent(label_numModelosTipo2)
								.addComponent(label_numAerolineasTipo2))))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		gl_panelFiltro.setVerticalGroup(
			gl_panelFiltro.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFiltro.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAerolineas)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_AerolineasTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_numAerolineasTipo2))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblModelos)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelFiltro.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_ModelosTipo2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_numModelosTipo2))
					.addContainerGap(29, Short.MAX_VALUE))
		);
		panelFiltro.setLayout(gl_panelFiltro);
		panel_AvionConcreto.setLayout(gl_panel_AvionConcreto);
		layeredPane.add(panel_Tipo3);
		
		JLabel label_2 = new JLabel("Avi\u00F3n:");
		label_2.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		comboBox_AvionesT3 = new JComboBox();
		comboBox_AvionesT3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){
				
				String id = (String)comboBox_AvionesT3.getSelectedItem();
				String[] av = getdatos(id);
				
				actualizarTablaAvionSelect(av, 3);
			}
		}
		);
		
		
		label_numAvionesT3 = new JLabel("...");
		
		JLabel label_6 = new JLabel("Filtro: ");
		
		JPanel panel_ComboT3 = new JPanel();
		panel_ComboT3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel label_7 = new JLabel("Aerolineas:");
		
		JLabel label_8 = new JLabel("Modelos: ");
		
		comboBox_ModelosT3 = new JComboBox();
		comboBox_ModelosT3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String filtroAerolinea = (String)comboBox_AerolineasT3.getSelectedItem();
				
				String filtroModelo = (String) comboBox_ModelosT3.getSelectedItem();
				
				ArrayList nuevaLista = CargarAvionesFiltrado(filtroAerolinea, filtroModelo);
				
				actualizarComboBox(comboBox_AvionesT3, nuevaLista);
				
			}
		}
		);
		
		comboBox_AerolineasT3 = new JComboBox();
		comboBox_AerolineasT3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

				String filtroAerolinea = (String)comboBox_AerolineasT3.getSelectedItem();
				
				String filtroModelo = (String) comboBox_ModelosT3.getSelectedItem();
				
				ArrayList nuevaLista = CargarAvionesFiltrado(filtroAerolinea, filtroModelo);
				
				actualizarComboBox(comboBox_AvionesT3, nuevaLista);
				
			}
		}
		);
		
		label_numModelosT3 = new JLabel("...");
		
		label_numAerolineasT3 = new JLabel("...");
		GroupLayout gl_panel_ComboT3 = new GroupLayout(panel_ComboT3);
		gl_panel_ComboT3.setHorizontalGroup(
			gl_panel_ComboT3.createParallelGroup(Alignment.LEADING)
				.addGap(0, 184, Short.MAX_VALUE)
				.addGroup(gl_panel_ComboT3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_ComboT3.createParallelGroup(Alignment.LEADING)
						.addComponent(label_7)
						.addComponent(label_8)
						.addGroup(gl_panel_ComboT3.createSequentialGroup()
							.addGroup(gl_panel_ComboT3.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(comboBox_ModelosT3, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(comboBox_AerolineasT3, Alignment.LEADING, 0, 124, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_ComboT3.createParallelGroup(Alignment.LEADING)
								.addComponent(label_numModelosT3)
								.addComponent(label_numAerolineasT3))))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		gl_panel_ComboT3.setVerticalGroup(
			gl_panel_ComboT3.createParallelGroup(Alignment.LEADING)
				.addGap(0, 129, Short.MAX_VALUE)
				.addGroup(gl_panel_ComboT3.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_7)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ComboT3.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_AerolineasT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_numAerolineasT3))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(label_8)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_ComboT3.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_ModelosT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_numModelosT3))
					.addContainerGap(18, Short.MAX_VALUE))
		);
		panel_ComboT3.setLayout(gl_panel_ComboT3);
		
		JLabel label_11 = new JLabel("Avi\u00F3n seleccionado");
		label_11.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblRetraso_2 = new JLabel("Retraso:");
		lblRetraso_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblMilestoneConocimiento_1 = new JLabel("Milestone Conocimiento:");
		
		comboBox_MilT3 = new JComboBox();
		comboBox_MilT3.setModel(new DefaultComboBoxModel(new String[] {"3 [TakeOff aeropuerto origen]", "4 [Entrada en FIR]", "5 [Inicio Final Approach]", "6 [Landing]", "7 [IN Block]", "8 [Inico Ground Handling]", "9 [Confirmaci\u00F3n TOBT]", "10 [Definici\u00F3n TSAT]", "11 [Embarque]", "12 [Aircraft Ready]", "13 [Start Up Approval Requested]", "14 [Start Up Approved]", "15 [OFF Block]"}));
		
		
		JLabel lblTiempo = new JLabel("Tiempo:");
		
		comboBox_TiempoT3 = new JComboBox();
		comboBox_TiempoT3.setModel(new DefaultComboBoxModel(new String[] {"ELDT [Estimated Landing Time]", "ETOT [Estimated Take Off Time]", "EIBT [Estimated IN-Block Time]", "EOBT [Estimated OFF-Block Time]", "TFIR [FIR Entrance Time]", "TAPP [Final Approach Time]", "ACGT [Actual Commence of Gound Handling Time]", "ASBT [Actual Start Boarding Time]", "ARDT [Aircraft Ready Time]", "ASRT [Actual Start-Up Request Time]", "TSAT [Target Start-Up Aproval Time]"}));
		
		JLabel lblMinutosRetraso_2 = new JLabel("Minutos Retraso:");
		
		spinnerMinRT3 = new JSpinner();
		spinnerMinRT3.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		
		JLabel lblAgente_1 = new JLabel("Agente:");
		
		comboBox_AgenteT3 = new JComboBox();
		comboBox_AgenteT3.setModel(new DefaultComboBoxModel(new String[] {"CFMU", "ATC", "Airport", "AO", "GH"}));
		
		JLabel lblComentarios_2 = new JLabel("Comentarios:");
		
		textField_ComentariosT3 = new JTextField();
		textField_ComentariosT3.setColumns(10);
		
		JButton btnVistaPrevia_T3 = new JButton("Vista Previa");
		btnVistaPrevia_T3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				definirRetrasoTipo3();
			}
		});
		GroupLayout gl_panel_Tipo3 = new GroupLayout(panel_Tipo3);
		gl_panel_Tipo3.setHorizontalGroup(
			gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Tipo3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Tipo3.createSequentialGroup()
							.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_Tipo3.createSequentialGroup()
									.addGap(30)
									.addComponent(comboBox_AvionesT3, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(label_numAvionesT3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
									.addGap(68)
									.addComponent(label_11, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel_Tipo3.createSequentialGroup()
									.addGap(30)
									.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
										.addComponent(panel_ComboT3, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
										.addComponent(label_6, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
									.addGap(39)
									.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE))
								.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblRetraso_2)
								.addGroup(gl_panel_Tipo3.createSequentialGroup()
									.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.TRAILING, false)
										.addGroup(gl_panel_Tipo3.createSequentialGroup()
											.addComponent(lblAgente_1)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_AgenteT3, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(lblMilestoneConocimiento_1, Alignment.LEADING))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBox_MilT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(Alignment.TRAILING, gl_panel_Tipo3.createSequentialGroup()
									.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panel_Tipo3.createSequentialGroup()
											.addComponent(lblTiempo)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_TiempoT3, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
											.addComponent(lblMinutosRetraso_2)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerMinRT3, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panel_Tipo3.createSequentialGroup()
											.addComponent(lblComentarios_2)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(textField_ComentariosT3, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)))
									.addGap(157)))
							.addGap(96))
						.addGroup(gl_panel_Tipo3.createSequentialGroup()
							.addComponent(btnVistaPrevia_T3)
							.addContainerGap(618, Short.MAX_VALUE))))
		);
		gl_panel_Tipo3.setVerticalGroup(
			gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Tipo3.createSequentialGroup()
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Tipo3.createSequentialGroup()
							.addGap(34)
							.addComponent(label_11))
						.addGroup(gl_panel_Tipo3.createSequentialGroup()
							.addContainerGap()
							.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_Tipo3.createSequentialGroup()
									.addGap(3)
									.addComponent(label_numAvionesT3))
								.addComponent(comboBox_AvionesT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addGap(6)
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Tipo3.createSequentialGroup()
							.addGap(22)
							.addComponent(label_6)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_ComboT3, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblRetraso_2)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTiempo)
						.addComponent(comboBox_TiempoT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(spinnerMinRT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMinutosRetraso_2))
					.addGap(18)
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMilestoneConocimiento_1)
						.addComponent(comboBox_MilT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAgente_1)
						.addComponent(comboBox_AgenteT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_Tipo3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComentarios_2)
						.addComponent(textField_ComentariosT3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					.addComponent(btnVistaPrevia_T3)
					.addGap(23))
		);
		
		tableAvionT3 = new JTable();
		tableAvionT3.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null},
			},
			new String[] {
				"ID Avi\u00F3n", "Aerol\u00EDnea", "Modelo", "ELDT", "ETOT"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		modeloAvSelectT3 = (DefaultTableModel) tableAvionT3.getModel();
		
		
		scrollPane_1.setViewportView(tableAvionT3);
		panel_Tipo3.setLayout(gl_panel_Tipo3);
		comboBox_ModelosT3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent eee){

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
			}
		}
		);
		layeredPane.add(panel_Tipo4);
		
		JLabel lblTipo_2 = new JLabel("Reducci\u00F3n de capacidad de pista");
		lblTipo_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JLabel lblAplicacinRetraso = new JLabel("Aplicaci\u00F3n Retraso:");
		lblAplicacinRetraso.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JLabel lblHoraInicio_1 = new JLabel("Hora Inicio:");
		
		spinnerInh = new JSpinner();
		spinnerInh.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH_3 = new JLabel("h");
		
		spinnerInmin = new JSpinner();
		spinnerInmin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin_3 = new JLabel("min");
		
		JLabel lblHoraFinal_1 = new JLabel("Hora Final:");
		
		spinnerFinh = new JSpinner();
		spinnerFinh.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH_4 = new JLabel("h");
		
		spinnerFinmin = new JSpinner();
		spinnerFinmin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin_4 = new JLabel("min");
		
		JLabel lblHoraConocimiento_2 = new JLabel("Hora Conocimiento:");
		
		spinnerConh = new JSpinner();
		spinnerConh.setModel(new SpinnerNumberModel(0, 0, 23, 1));
		
		JLabel lblH_5 = new JLabel("h");
		
		spinnerConmin = new JSpinner();
		spinnerConmin.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		
		JLabel lblMin_5 = new JLabel("min");
		
		JLabel lblCapacidadesReducidas = new JLabel("Capacidades Reducidas:");
		lblCapacidadesReducidas.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		checkBoxDepartures = new JCheckBox("");
		checkBoxDepartures.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(checkBoxDepartures.isSelected())
					spinnerDep.setEnabled(true);
				if(!checkBoxDepartures.isSelected())
					spinnerDep.setEnabled(false);
					
			}
		});
		
		JLabel lblDepartures_1 = new JLabel("Departures:");
		lblDepartures_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				boolean check = checkBoxDepartures.isSelected(); 
				if(check)
				{
					checkBoxDepartures.setSelected(false);
					spinnerDep.setEnabled(false);
				}
				if(!check)
				{
					checkBoxDepartures.setSelected(true);
					spinnerDep.setEnabled(true);
			
				}
			}
		});
		lblDepartures_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		checkBoxArrivals = new JCheckBox("");
		checkBoxArrivals.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(checkBoxArrivals.isSelected())
					spinnerArr.setEnabled(true);
				if(!checkBoxArrivals.isSelected())
					spinnerArr.setEnabled(false);
			}
		});
		
		JLabel lblArrivals = new JLabel("Arrivals:");
		lblArrivals.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				boolean check = checkBoxArrivals.isSelected();
				if(check)
				{
					checkBoxArrivals.setSelected(false);
					spinnerArr.setEnabled(false);
				}
				if(!check)
				{
					checkBoxArrivals.setSelected(true);
					spinnerArr.setEnabled(true);
				}
			}
		});
		lblArrivals.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		spinnerDep = new JSpinner();
		spinnerDep.setEnabled(false);
		spinnerDep.setModel(new SpinnerNumberModel(30, 0, 30, 1));
		
		spinnerArr = new JSpinner();
		spinnerArr.setEnabled(false);
		spinnerArr.setModel(new SpinnerNumberModel(30, 1, 30, 1));
		
		JLabel lblComentarios_1 = new JLabel("Comentarios:");
		lblComentarios_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		textField_ComentariosT4 = new JTextField();
		textField_ComentariosT4.setColumns(10);
		
		JButton btnVistaPrevia = new JButton("Vista Previa");
		btnVistaPrevia.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				definirRetrasoTipo4();
				definido = true;
			}
		});
		
		JLabel lblOph = new JLabel("op/h");
		
		JLabel lblOph_1 = new JLabel("op/h");
		GroupLayout gl_panel_Tipo4 = new GroupLayout(panel_Tipo4);
		gl_panel_Tipo4.setHorizontalGroup(
			gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Tipo4.createSequentialGroup()
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_Tipo4.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTipo_2)
								.addComponent(lblAplicacinRetraso)
								.addGroup(gl_panel_Tipo4.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblHoraInicio_1)
										.addComponent(lblHoraConocimiento_2)
										.addComponent(lblHoraFinal_1))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING, false)
										.addComponent(spinnerConh)
										.addComponent(spinnerFinh)
										.addComponent(spinnerInh, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING, false)
										.addGroup(gl_panel_Tipo4.createSequentialGroup()
											.addComponent(lblH_5)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(spinnerConmin))
										.addGroup(gl_panel_Tipo4.createSequentialGroup()
											.addComponent(lblH_3)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(spinnerInmin, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_panel_Tipo4.createSequentialGroup()
											.addComponent(lblH_4)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(spinnerFinmin)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
										.addComponent(lblMin_5)
										.addComponent(lblMin_4)
										.addComponent(lblMin_3)))))
						.addGroup(gl_panel_Tipo4.createSequentialGroup()
							.addGap(53)
							.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_Tipo4.createSequentialGroup()
									.addComponent(checkBoxDepartures)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblDepartures_1))
								.addGroup(gl_panel_Tipo4.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(checkBoxArrivals, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblArrivals)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING, false)
								.addComponent(spinnerArr)
								.addComponent(spinnerDep, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
								.addComponent(lblOph_1)
								.addComponent(lblOph)))
						.addGroup(gl_panel_Tipo4.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblCapacidadesReducidas))
						.addGroup(gl_panel_Tipo4.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblComentarios_1)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textField_ComentariosT4, GroupLayout.PREFERRED_SIZE, 391, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_Tipo4.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnVistaPrevia)))
					.addContainerGap(203, Short.MAX_VALUE))
		);
		gl_panel_Tipo4.setVerticalGroup(
			gl_panel_Tipo4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Tipo4.createSequentialGroup()
					.addGap(54)
					.addComponent(lblTipo_2)
					.addGap(18)
					.addComponent(lblAplicacinRetraso)
					.addGap(14)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerInh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblH_3)
						.addComponent(spinnerInmin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMin_3)
						.addComponent(lblHoraInicio_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblHoraFinal_1)
						.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
							.addComponent(spinnerFinh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblH_4)
							.addComponent(spinnerFinmin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMin_4)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblHoraConocimiento_2)
						.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
							.addComponent(spinnerConh, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblH_5)
							.addComponent(spinnerConmin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMin_5)))
					.addGap(30)
					.addComponent(lblCapacidadesReducidas)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.LEADING, false)
						.addComponent(checkBoxDepartures, 0, 0, Short.MAX_VALUE)
						.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblDepartures_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(spinnerDep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblOph)))
					.addGap(16)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
							.addComponent(spinnerArr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblArrivals)
							.addComponent(lblOph_1))
						.addComponent(checkBoxArrivals, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel_Tipo4.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComentarios_1)
						.addComponent(textField_ComentariosT4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(43)
					.addComponent(btnVistaPrevia)
					.addContainerGap(45, Short.MAX_VALUE))
		);
		panel_Tipo4.setLayout(gl_panel_Tipo4);
		
		JLabel lblRetraso_1 = new JLabel("Retraso:");
		lblRetraso_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JLabel lblTipoRetraso_1 = new JLabel("Tipo Retraso: ");
		
		JLabel lblSituacin = new JLabel("Situaci\u00F3n:");
		
		JLabel lblIdAvin = new JLabel("ID Avi\u00F3n:");
		
		JLabel lblMinutosRetraso_1 = new JLabel("Minutos Retraso:");
		
		JLabel lblParametro = new JLabel("Parametro:");
		
		JLabel lblAgente = new JLabel("Agente:");
		
		JLabel lblMilestoneConocimiento = new JLabel("Milestone:");
		
		JLabel lblHoraConocimiento = new JLabel("Hora Conocimiento:");
		
		JLabel lblHoraInicioVentana = new JLabel("Hora Inicio Ventana:");
		
		JLabel lblHoraFinalVentana = new JLabel("Hora Final Ventana:");
		
		JLabel lblDepartures = new JLabel("Departures [op/h]:");
		
		JLabel lblArribals = new JLabel("Arribals [op/h]:");
		
		JLabel lblComentarios = new JLabel("Comentarios:");
		
		lbltipo = new JLabel("-");
		
		labelSituacion = new JLabel("-");
		
		labelID = new JLabel("-");
		
		labelMinR = new JLabel("-");
		
		labelParametro = new JLabel("-");
		
		labelAgente = new JLabel("-");
		
		labelMilestone = new JLabel("-");
		
		labelHCon = new JLabel("-");
		
		labelHInicio = new JLabel("-");
		
		labelHFinal = new JLabel("-");
		
		labelDep = new JLabel("-");
		
		labelArr = new JLabel("-");
		
		labelComent = new JLabel("-");
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblRetraso_1)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblTipoRetraso_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbltipo))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblSituacin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelSituacion))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblIdAvin)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelID))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblMinutosRetraso_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelMinR))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblParametro)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelParametro))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblAgente)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelAgente))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblMilestoneConocimiento)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelMilestone))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblComentarios)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelComent))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblHoraConocimiento)
								.addComponent(lblHoraInicioVentana))
							.addGap(2)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(labelHInicio)
								.addComponent(labelHCon)))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblHoraFinalVentana)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(lblDepartures)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(labelDep))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(lblArribals)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(labelArr)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(labelHFinal)))
					.addContainerGap(136, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblRetraso_1)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTipoRetraso_1)
						.addComponent(lbltipo))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSituacin)
						.addComponent(labelSituacion))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIdAvin)
						.addComponent(labelID))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMinutosRetraso_1)
						.addComponent(labelMinR))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblParametro)
						.addComponent(labelParametro))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAgente)
						.addComponent(labelAgente))
					.addGap(18)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMilestoneConocimiento)
						.addComponent(labelMilestone))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraConocimiento)
						.addComponent(labelHCon))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraInicioVentana)
						.addComponent(labelHInicio))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHoraFinalVentana)
						.addComponent(labelHFinal))
					.addGap(18)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDepartures)
						.addComponent(labelDep))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblArribals)
						.addComponent(labelArr))
					.addGap(26)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComentarios)
						.addComponent(labelComent))
					.addContainerGap(88, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			
				dispose();
			}
		});
		
		btnAceptar = new JButton("Aceptar");
		btnAceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!definido)
				{
					JOptionPane.showMessageDialog(null,"Define un retraso y presiona Vista Previa para guardarlo");
				}
				if(definido)
				{
					Aceptar = true;
					
					dispose();
				}
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(554, Short.MAX_VALUE)
					.addComponent(btnAceptar)
					.addGap(18)
					.addComponent(btnCancelar)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancelar)
						.addComponent(btnAceptar))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		comboBox_MilestoneT1 = new JComboBox();
		contentPane.setLayout(gl_contentPane);
	
	
	
		
	}
	
	public void activarPaneles(int num)
	{
		panel_Tipos1_2.setVisible(false);
		panel_Tipo3.setVisible(false);
		panel_Tipo4.setVisible(false);
		
		AerolineasModelosAviones(numAviones);
		CargarComboBoxes(); // Reiniciamos los comboBox del panel
		
		definido = false;
		
		if(num <= 3)
		{
			panel_Tipos1_2.setVisible(true);
			
			String situacion = nombreSituacion(comboBox.getSelectedIndex());
			textSituaciónTipo2.setText(situacion);
			textField_SituacionT1.setText(situacion);
		}
		if(num == 4)
		{
			panel_Tipo3.setVisible(true);
		}
		if(num == 5)
		{
			panel_Tipo4.setVisible(true);
		}
		
	}

	public void lecturaArchivo() throws FileNotFoundException
	{
		numAviones = contarAvionesArchivo(RUTA);
		
		datos = new String[numAviones][5]; // Definimos los datos con el numero de aviones que hay en la simulacion
		
		cargarDatos(RUTA, numAviones); // Cargamos los datos de los aviones
		
		AerolineasModelosAviones(numAviones);
		
	}
	
	public int contarAvionesArchivo(String ruta)
	{
		File archivo = new File(ruta);
		
		FileReader fr = null;
	    BufferedReader br = null;
	    //Cadena de texto donde se guardara el contenido del archivo
	    int count = 0;

	    try
	    {
	        
	        fr = new FileReader( archivo );
	        br = new BufferedReader( fr );
	        
        String linea = br.readLine();
       
        while (linea != null) //Recorrem el fitxer per saber quants avions tenim
        {
            count = count + 1;
            linea = br.readLine();
        }
        
        br.close();
	    }
	    catch(Exception filenotfoundexception){
	    	System.out.println("No encuentro el archivo de AvionesAerolinea");
	    }
	    
	    return count;
	
	}

	public void cargarDatos(String ruta, int numero) throws FileNotFoundException
	{
		File archivo = new File(ruta);
		
	    //Cadena de texto donde se guardara el contenido del archivo
	    String[] contenido = new String[4];
	    
	    FileReader fr = null;
	    BufferedReader br = null;

	    try
	    { 
	      fr = new FileReader( archivo );
	      br = new BufferedReader( fr );
	        
        String linea = br.readLine();
        int c = 0;
        
        while (linea != null) //Recorrem el fitxer per saber quants avions tenim
        {
        	contenido = linea.split("\t");
        	datos[c][0] = contenido[0]; //Guardamos el ID del avión
        	datos[c][1] = contenido[1]; //Guardamos la Aerolinea del avión;
        	datos[c][2] = contenido[2]; //Guardamos el Modelo del avión
        	datos[c][3] = contenido[3]; //Guardamos el ELDT del avión
        	datos[c][4] = contenido[4]; //Guardamos el ETOT del avión
        	
            linea = br.readLine(); //Leemos la siguiente linea
        	c++;
        }
        
        br.close();
	    }
	    catch(Exception filenotfoundexception){
	    	System.out.println("No encuentro el archivo de AvionesAerolinea");
	    }
		
	}
	
	public void AerolineasModelosAviones(int numero) //Saca de los datos de las aerolineas y los modelos
	{
		aerolineas = new ArrayList(); // Limpiamos la lista antes de volver a guardarla.
		modelos = new ArrayList();
		aviones = new ArrayList();
		
		int i = 0;
		int c = 0;
		aerolineas.add("- Cualquiera -"); // Añadimos la opción de no filtrar por aerolineas
		modelos.add("- Cualquiera -"); // Añadimos la opción de no filtrar por modelos
		while(i < numero)
		{
			if(aviones.indexOf(datos[i][0]) == -1) //Comprobamos si ya se ha guardado la aerolinea
			{
				aviones.add(datos[i][0]);
			}
			
			if(aerolineas.indexOf(datos[i][1]) == -1) //Comprobamos si ya se ha guardado la aerolinea
			{
				aerolineas.add(datos[i][1]);
			}
			if(modelos.indexOf(datos[i][2]) == -1) //Comprobamos si ya se ha guardado la aerolinea
			{
				modelos.add(datos[i][2]);
			}
			
			i++;
		}
		
	}

	public void SetRuta(String r)
	{
		RUTA = r + "VuelosAerolinea.txt";
	}

	public void CargarComboBoxes()
	{
		// Cargamos los comboBox del panel de los retrasos tipo 2
		Object[] modelosCombo = modelos.toArray();
		comboBox_ModelosTipo2.setModel(new DefaultComboBoxModel(modelosCombo));
		label_numModelosTipo2.setText((modelos.size()-1) +"");
		
		Object[] avionesCombo = aviones.toArray();
		comboBox_AvionesTipo2.setModel(new DefaultComboBoxModel(avionesCombo));
		label_NumAvionesTipo2.setText((aviones.size() + ""));
		
		Object[] aerolineasCombo = aerolineas.toArray();
		comboBox_AerolineasTipo2.setModel(new DefaultComboBoxModel(aerolineasCombo));
		label_numAerolineasTipo2.setText((aerolineas.size()-1)+"");
		
		// Cargamos los comboBox del panel de los retrasos tipo 1
		ArrayList modelosSinCualquiera = modelos;
		modelosSinCualquiera.remove(0);
		Object[] modelosComboT1 = modelosSinCualquiera.toArray();
		comboBox_ModelosT1.setModel(new DefaultComboBoxModel(modelosComboT1));
		label_NumModelosTipo1.setText((modelos.size()-1) +"");
		
		ArrayList AerolineasSinCualquiera = aerolineas;
		AerolineasSinCualquiera.remove(0);
		Object[] aerolineasComboT1 = AerolineasSinCualquiera.toArray();
		comboBox_AerolineasT1.setModel(new DefaultComboBoxModel(aerolineasComboT1));
		label_NumAerolineasTipo1.setText((aerolineas.size()-1)+"");
		
		// Cargar comboBox del panel de los retrasos tipo 3
		comboBox_ModelosT3.setModel(new DefaultComboBoxModel(modelosCombo));
		label_numModelosT3.setText((modelos.size()-1) +"");
		
		comboBox_AvionesT3.setModel(new DefaultComboBoxModel(avionesCombo));
		label_numAvionesT3.setText((aviones.size() + ""));

		comboBox_AerolineasT3.setModel(new DefaultComboBoxModel(aerolineasCombo));
		label_numAerolineasT3.setText((aerolineas.size()-1)+"");
	}

	public ArrayList CargarAvionesFiltrado(String aerolinea, String modelo)
	{
		ArrayList newAviones = new ArrayList();
		int length = datos.length;
		int cont = 0;
		
		boolean ao = false;
		boolean mod = false;
		
		if(aerolinea != "- Cualquiera -")
		{
			ao = true;
		}
		if(modelo != "- Cualquiera -")
		{
			mod = true;
		}		
		
		// Habra 4 casos: 1- No hay filtros , 2- Filtramos solo por Aerolinea, 3- Filtramos solo por Modelo, 4- Filtramos por Aerolinea y modelo
		
		if(!ao && !mod) // Caso 1: Sin filtros
		{
			while(cont<length)
			{	
				newAviones.add(datos[cont][0]);
				
				cont++;
			}
		}
		if(ao && !mod) // Caso 2: Filtro por Aerolinea
		{
			while(cont<length)
			{
				if(datos[cont][1].equals(aerolinea))
				{
					newAviones.add(datos[cont][0]);
				}
				cont++;
			}
		}
		if(!ao && mod) // Caso 3: Filtro por Modelo
		{
			while(cont<length)
			{
				if(datos[cont][2].equals(modelo))
				{
					newAviones.add(datos[cont][0]);
				}
				cont++;
			}
		}
		if(ao && mod) // Caso 4: Filtro por Aerolinea y por Modelo
		{
			while(cont<length)
			{
				if(datos[cont][1].equals(aerolinea) && datos[cont][2].equals(modelo))
				{
					newAviones.add(datos[cont][0]);
				}
				cont++;
			}
		}
		
		return newAviones;
	}

	public void actualizarComboBox(ArrayList nuevaLista, int combobox)
	{
		if(combobox == 1) // Se actualiza la lista del comboBox Aviones
		{
			Object[] avionesCombo = nuevaLista.toArray();
			comboBox_AvionesTipo2.setModel(new DefaultComboBoxModel(avionesCombo));
			label_NumAvionesTipo2.setText(nuevaLista.size() + "");
		}
	}

	public void actualizarComboBox(JComboBox combo, ArrayList lista)
	{
		Object[] modelosCombo = lista.toArray();
		combo.setModel(new DefaultComboBoxModel(modelosCombo));
		label_NumAvionesTipo2.setText(lista.size() + "");
		label_numAvionesT3.setText(lista.size() + "");
		
	}

	public String nombreSituacion(int opcion)
	{
		String nombre = " ";
		if(opcion == 0)
		{
			nombre = "Problemas meteo FIR";
		}
		if(opcion == 1)
		{
			nombre = "Problemas Taxiway IN";
		}
		if(opcion == 2)
		{
			nombre = "Problemas Taxiway OUT";
		}
		if(opcion == 3)
		{
			nombre = "Problemas GH";
		}
		
		return nombre;
	}

	public String getX(String situacion) // Devuelve el parametro "X" a partir del nombre que tiene la situación
	{
		if(situacion.equals("Problemas meteo FIR"))
			return "1";
		if(situacion.equals("Problemas Taxiway IN"))
			return "2";
		if(situacion.equals("Problemas Taxiway OUT"))
			return "3";
		if(situacion.equals("Problemas GH"))
			return "4";
		
		return "Error";
	}

	private String[] definirRetrasoVistaPrevia(String[] re)
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
		
		/*Vector retraso Vista Previa:
		 * [0]: Tipo de retraso
		 * [1]: Situación ("X" para los tipos 1 y 2)
		 * [2]: ID Avión
		 * [3]: Minutos Retraso
		 * [4]: Parametro
		 * [5]: Agente
		 * [6]: Milestone
		 * [7]: Hora Conocimiento (Milestone conocimiento en tipos 2 y 3)
		 * [8]: Hora Inicio
		 * [9]: Hora Final
		 * [10]: Departures
		 * [11]: Arribals
		 * [12]: Comentarios
		 */
			String[] retraso = new String[13];
			
			retraso = generarVec(13); // Generamos el vector con las "-" en las posiciones que no tendrán valor
			
			String tipo = re[0];
			
			if(tipo == "1") // Si el retraso es de tipo 1
			{
				retraso[0] = re[0]; // Tipo
				retraso[8] = re[1]; // H.Inicio
				retraso[9] = re[2]; // H.Final
				retraso[7] = re[3]; // H.Coneixement
				retraso[3] = re[4]; // Minutos Retraso
				String situación = getSituacion(Integer.parseInt(re[5]));
				retraso[1] = situación; // "X" (Situación)
				retraso[6] = re[6]; // Milestone aplicación
				
				retraso[2] = "Filtro: Aerolineas ["+re[7]+"] / Modelos ["+re[8]+"]";
				retraso[12] = re[9]; // Comentaris
			} 
			if(tipo == "2") // Si el retraso es de tipo 2
			{
				retraso[0] = re[0]; // Tipo
				retraso[2] = re[1]; // ID Avión
				retraso[7] = "Milestone " + re[2]; // Milestone coneixement
				retraso[3] = re[3]; // Minutos Retraso
				String situación = getSituacion(Integer.parseInt(re[4]));
				retraso[1] = situación; // "X" (Situación)
				retraso[12] = re[5]; // Comentarios
			} 
			if(tipo == "3") // Si el retraso es de tipo 3
			{
				retraso[0] = re[0]; // Tipo
				retraso[2] = re[1]; // ID Avión
				retraso[7] = "Milestone " + re[2]; // Milestone coneixement
				retraso[3] = re[3]; // Minutos Retraso
				retraso[4] = re[4]; // Parametros/tiempo retrasado
				retraso[5] = re[5]; // Agente
				retraso[12] = re[6];	// Comentarios
			}
			if(tipo == "4") // Si el retraso es de tipo 4
			{
				retraso[0] = re[0]; // Tipo
				retraso[8] = re[1]; // H.Inicio
				retraso[9] = re[2]; // H.Final
				retraso[7] = re[3]; // H.Coneixement
				//retraso[3] = re[4]; // Minutos Retraso
				retraso[1] = re[5]; // "X" (Opción)
				retraso[10] = re[4]; // Departures
				retraso[11] = re[5]; // Arribals
				retraso[12] = re[6]; // Comentarios
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
	
	public String getSituacion(int opcion)
	{
		String nombre = " ";
		if(opcion == 1)
		{
			nombre = "Problemas meteo FIR";
		}
		if(opcion == 2)
		{
			nombre = "Problemas Taxiway IN";
		}
		if(opcion == 3)
		{
			nombre = "Problemas Taxiway OUT";
		}
		if(opcion == 4)
		{
			nombre = "Problemas GH";
		}
		
		return nombre;
	}
	
	public String[] getdatos(String ID) // Devuelve los datos de un avión concreto
	{
		String[] d = new String[5];
		int c = 0;
		while(c<datos.length)
		{
			if(datos[c][0].equals(ID))
			{
				d[0] = datos[c][0];
				d[1] = datos[c][1];
				d[2] = datos[c][2];
				d[3] = datos[c][3];
				d[4] = datos[c][4];
				
				break;
			}
			c++;
		}
	return d;
	}
	
	public void actualizarTablaAvionSelect(String[] dd, int tipo) // Actualiza la tabla del avión seleccionado
	{
		Object [] fila = new Object[5]; // Creamos el Object que se añadirá a la tabla.
		int c = 0;
		while(c<5)
		{
			fila[c] = dd[c];
			c++;
		}
		if(tipo == 2)
		{
			modeloAvSelectTipo2.removeRow(0); // Borramos la linea actual (solo puede haver un avion)
			modeloAvSelectTipo2.addRow(fila); // Añadimos el nuevo avion a la tabla 
		}
		if(tipo == 3)
		{
			modeloAvSelectT3.removeRow(0); // Borramos la linea actual (solo puede haver un avion)
			modeloAvSelectT3.addRow(fila); // Añadimos el nuevo avion a la tabla 
		}
	}

	public void definirRetrasoTipo2() // Guarda el retraso definido y lo muestra en el apartado de vista previa
	{
		String milestoneConocimiento = (String)comboBox_MilestoneTipo2.getSelectedItem();
		String milestone = (String)textSituaciónTipo2.getText();
		boolean coherente = comprobarMilestoneCoherente(milestone, milestoneConocimiento, 2);
		
		if(!coherente)
		{
			JOptionPane.showMessageDialog(null,"Revisa la coherencia de los Milestones.\n (Milestone conocimiento < Milestone Retraso)");
		}
		else
		{
			String[] retraso = new String[6];
			
			retraso[0] = "2";
			retraso[1] = (String)comboBox_AvionesTipo2.getSelectedItem();
			retraso[2] = sacarMilestone((String)comboBox_MilestoneTipo2.getSelectedItem());
			retraso[3] = Integer.toString((int)minRetrasoTipo2.getValue());
			retraso[4] = getX((String)textSituaciónTipo2.getText());
			retraso[5] = textFieldComentariosTipo2.getText();
			
			setRetrasoDef(retraso);
			String[] reVP = definirRetrasoVistaPrevia(retraso);
			
			VistaPreviaRetraso(reVP);
			
			definido = true;
		}
		
		
	}
	
	public void definirRetrasoTipo1() // Guarda el retraso definido y lo muestra en el apartado de vista previa
	{
		String Hinicio = sacarTiempo((int)spinnerHInicio_h.getValue())+ sacarTiempo((int)spinnerHInicio_min.getValue());
		String Hfinal = sacarTiempo((int)spinnerHFinal_h.getValue())+ sacarTiempo((int)spinnerHFinal_min.getValue());
		String Hcon = sacarTiempo((int)spinnerHCon_h.getValue())+ sacarTiempo((int)spinnerHCon_min.getValue());
		
		boolean coherente = comprobarHorasCoherentes(Hcon,Hinicio,Hfinal);
		
		if(!coherente)
		{
			JOptionPane.showMessageDialog(null,"Revisa la coherencia de los tiempos.\n  (H.con < H.inicio < H.final)");
		}
		else
		{
			String[] retraso = new String[10];
			
			retraso[0] = "1";
			retraso[1] = Hinicio;
			retraso[2] = Hfinal;
			retraso[3] = Hcon;
			retraso[4] = Integer.toString((int)SpinnerMinRT1.getValue());
			retraso[5] = getX((String)textField_SituacionT1.getText());
			String qq = (String)comboBox_MilestoneT1.getSelectedItem();
			retraso[6] = sacarMilestone((String)comboBox_MilestoneTipo1.getSelectedItem());
			retraso[7] = (String)textField_AerolineasT1.getText();
			retraso[8] = (String)textField_ModelosT1.getText();
			retraso[9] = (String)textField_ComentariosT1.getText();
			
			
			setRetrasoDef(retraso);
			String[] reVP = definirRetrasoVistaPrevia(retraso);
			
			VistaPreviaRetraso(reVP);
			
			definido = true;
		}
	}
	
	public void definirRetrasoTipo4() // Guarda el retraso definido y lo muestra en el apartado de vista previa
	{
		String Hinicio = sacarTiempo((int)spinnerInh.getValue())+ sacarTiempo((int)spinnerInmin.getValue());
		String Hfinal = sacarTiempo((int)spinnerFinh.getValue())+ sacarTiempo((int)spinnerFinmin.getValue());
		String Hcon = sacarTiempo((int)spinnerConh.getValue())+ sacarTiempo((int)spinnerConmin.getValue());
		
		boolean coherente = comprobarHorasCoherentes(Hcon,Hinicio,Hfinal);
		
		if(!coherente)
		{
			JOptionPane.showMessageDialog(null,"Revisa la coherencia de los tiempos.\n  (H.con < H.inicio < H.final)");
		}
		else
		{
			String[] retraso = new String[7];
			
			retraso[0] = "4";
			retraso[1] = Hinicio;
			retraso[2] = Hfinal;
			retraso[3] = Hcon;
			retraso[4] = "-";
			retraso[5] = "-";
			if(checkBoxDepartures.isSelected())
				retraso[4] = Integer.toString((int)spinnerDep.getValue());
			if(checkBoxArrivals.isSelected())
				retraso[5] = Integer.toString((int)spinnerArr.getValue());
			retraso[6] = (String)textField_ComentariosT4.getText();
			
			setRetrasoDef(retraso);
			String[] reVP = definirRetrasoVistaPrevia(retraso);
			
			VistaPreviaRetraso(reVP);
			
			definido = true;
		}
	}
	
	public void definirRetrasoTipo3() // Guarda el retraso definido y lo muestra en el apartado de vista previa
	{
		
		String milestoneConocimiento = (String)comboBox_MilT3.getSelectedItem();
		String milestone = (String)comboBox_TiempoT3.getSelectedItem();
		boolean coherente = comprobarMilestoneCoherente(milestone, milestoneConocimiento, 3);
		
		if(!coherente)
		{
			JOptionPane.showMessageDialog(null,"Revisa la coherencia de los Milestones.\n (Milestone conocimiento < Milestone Retraso)");
		}
		else
		{
			String[] retraso = new String[7];
			
			retraso[0] = "3";
			retraso[1] = (String)comboBox_AvionesT3.getSelectedItem();
			retraso[2] = sacarMilestone((String)comboBox_MilT3.getSelectedItem());;
			retraso[3] = Integer.toString((int)spinnerMinRT3.getValue());;
			retraso[4] = sacarMilestone((String)comboBox_TiempoT3.getSelectedItem());
			retraso[5] = (String)comboBox_AgenteT3.getSelectedItem();
			retraso[6] = (String)textField_ComentariosT3.getText();
			
			setRetrasoDef(retraso);
			String[] reVP = definirRetrasoVistaPrevia(retraso);
			
			VistaPreviaRetraso(reVP);
			
			definido = true;
		}
	}
	
	public String sacarMilestone(String m) // Sacamos únicamente el numero de milestone de un string del comboBox de milestones
	{
		String[] mm = m.split(" ");
		
		return mm[0];
	}

	public void VistaPreviaRetraso(String[] re) // Asigna los valores de los parametros del retraso a los labels de la vista previa
	{
		lbltipo.setText(re[0]);
		labelSituacion.setText(re[1]);
		labelID.setText(re[2]);
		labelMinR.setText(re[3]);
		labelParametro.setText(re[4]);
		labelAgente.setText(re[5]);
		labelMilestone.setText(re[6]);
		labelHCon.setText(re[7]);
		labelHInicio.setText(re[8]);
		labelHFinal.setText(re[9]);
		labelDep.setText(re[10]);
		labelArr.setText(re[11]);
		labelComent.setText(re[12]);


	}

	public void setRetrasoDef(String[] re)
	{
		String[] retrasoGuardar = generarVec(10); // Generamos un vector con todo "-"
		
		// Generamos el vector de 10 posiciones que se guardará y se enviara a la InterfazRetrasos
		String tipo = re[0];
		if(tipo.equals("1"))
		{
			retrasoGuardar[0] = re[0];
			retrasoGuardar[1] = re[1];
			retrasoGuardar[2] = re[2];
			retrasoGuardar[3] = re[3];
			retrasoGuardar[4] = re[4];
			retrasoGuardar[5] = re[5];
			retrasoGuardar[6] = re[6];
			retrasoGuardar[7] = re[7];
			retrasoGuardar[8] = re[8];
			retrasoGuardar[9] = re[9];
			
		}
		if(tipo.equals("2"))
		{
			retrasoGuardar[0] = re[0];
			retrasoGuardar[1] = re[1];
			retrasoGuardar[2] = re[2];
			retrasoGuardar[3] = re[3];
			retrasoGuardar[4] = re[4];
			retrasoGuardar[5] = re[5];
		}
		if(tipo.equals("3"))
		{
			retrasoGuardar[0] = re[0];
			retrasoGuardar[1] = re[1];
			retrasoGuardar[2] = re[2];
			retrasoGuardar[3] = re[3];
			retrasoGuardar[4] = re[4];
			retrasoGuardar[5] = re[5];
			retrasoGuardar[6] = re[6];
			
		}
		if(tipo.equals("4"))
		{
			retrasoGuardar[0] = re[0];
			retrasoGuardar[1] = re[1];
			retrasoGuardar[2] = re[2];
			retrasoGuardar[3] = re[3];
			retrasoGuardar[4] = re[4];
			retrasoGuardar[5] = re[5];
			retrasoGuardar[6] = re[6];
		}
		
		//Guardamos el retraso definido en el vector que se enviará a InterfazRetrasos
		RetrasoDef = retrasoGuardar;
		
	}

	public String[] getRetrasoDef()
	{
		return RetrasoDef;
	}

	public Boolean getAceptar()
	{
		return Aceptar;
	}

	public String sacarTiempo(int t) // Sacamos el tiempo en dos cifras (3h = 03h) para no tener problemas al escribir
	{
		String valor = Integer.toString(t);
		if(t<10)
		{
			valor = "0"+valor;
		}
		
		return valor;
	}

	public void resetVistaPrevia() // Las variables de vista previa vuelven a zero
	{
		lbltipo.setText("-");
		labelSituacion.setText("-");
		labelID.setText("-");
		labelMinR.setText("-");
		labelParametro.setText("-");
		labelAgente.setText("-");
		labelMilestone.setText("-");
		labelHCon.setText("-");
		labelHInicio.setText("-");
		labelHFinal.setText("-");
		labelDep.setText("-");
		labelArr.setText("-");
		labelComent.setText("-");
		
	}

	public boolean comprobarHorasCoherentes(String con, String inicio, String fin) // Comprueba que se cumpla la relacion Hcon<HInicio<Hfinal
	{
		int Hcon = Integer.parseInt(con);
		int Hinicio = Integer.parseInt(inicio);
		int Hfinal = Integer.parseInt(fin);
		boolean coherente = true;
		
		// Comprobacion 1 (Hcon<=Hin)
		int con_in = restatemps(Hcon,Hinicio);
		if(con_in > 0)
		{
			coherente = false;
		}
		
		// Comprobacion 2(Hin<Hfin)
		int in_fin = restatemps(Hinicio, Hfinal);
		if(in_fin >= 0)
		{
			coherente = false;
		}
		
		return coherente;
	
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

	public boolean comprobarMilestoneCoherente(String situacion, String MilestoneCon, int opcion)
	{
		int milestoneCon = Integer.parseInt(sacarMilestone(MilestoneCon));
		
		if(opcion == 2) // Estamos en un retraso tipo 2, String situación será una de las situaciones propuestas7
		{
			int X = Integer.parseInt(getX(situacion));
			
			if(X == 1) // Caso "problemasFIR". Se retrasa milestone 5
			{
				int milestoneDelay = 5;
				
				if(milestoneDelay <= milestoneCon)
					return false;
				
			}
			if(X == 2) // Caso "problemasTaxiIN". Se retrasa milestone 7
			{
				int milestoneDelay = 7;
				
				if(milestoneDelay <= milestoneCon)
					return false;
				
			}
			if(X == 3) // Caso "problemasTaxiOUT". Se retrasa milestone 16
			{
				int milestoneDelay = 16;
				
				if(milestoneDelay <= milestoneCon)
					return false;
				
			}
			if(X == 4) // Caso "problemasGH". Se retrasa milestone 15
			{
				int milestoneDelay = 15;
				
				if(milestoneDelay <= milestoneCon)
					return false;
				
			}
			
		}
		if(opcion == 3) // Estamos en un retraso tipo 3, String situación será el milestone que se retrasa
		{
			int milestoneDelay = sacarMilestoneParametro(situacion);
			if(milestoneDelay <= milestoneCon)
				return false;
		}
		
		return true;
	}

	public int sacarMilestoneParametro(String parametro)
	{
		String tiempo  = sacarMilestone(parametro);
		
		if(tiempo.equals("ELDT"))
			return 6;
		if(tiempo.equals("ETOT"))
			return 16;
		if(tiempo.equals("EIBT"))
			return 7;
		if(tiempo.equals("EOBT"))
			return 15;
		if(tiempo.equals("TFIR"))
			return 4;
		if(tiempo.equals("TAPP"))
			return 5;
		if(tiempo.equals("ACGT"))
			return 8;
		if(tiempo.equals("ASBT"))
			return 11;
		if(tiempo.equals("ARDT"))
			return 12;
		if(tiempo.equals("ASRT"))
			return 13;
		if(tiempo.equals("TSAT"))
			return 14;
		
		return 0;
		
	}

}
