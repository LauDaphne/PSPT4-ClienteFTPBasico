package es.studium.clienteFTPBasico;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.SystemColor;

public class ClienteFTPBasico extends JFrame {
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JLabel txtServidor = new JLabel();
	static JLabel txtUsuario = new JLabel();
	static JLabel txtDirectorioRaiz = new JLabel();
	// Campos de mensajes parte inferior
	private static JLabel txtArbolDirectoriosConstruido = new JLabel();
	private static JLabel txtActualizarArbol = new JLabel();
	// Botones
	JButton btnCreaCarpeta = new JButton("Crear carpeta");
	JButton btnEliminarCarpeta = new JButton("Eliminar carpeta");
	JButton btnRenombrarCarpeta = new JButton("Renombrar carpeta");
	JButton btnSubirFichero = new JButton("Subir fichero");
	JButton botonDescargarFichero = new JButton("Descargar fichero");
	JButton botonEliminarFichero = new JButton("Eliminar fichero");
	JButton btnRenombrarFichero = new JButton("Renombrar fichero");
	JButton botonSalir = new JButton("Salir");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container c = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "127.0.0.1";
	String user = "Laura";
	String pasw = "Studium2019;";
	boolean login;
	static String directorioInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String directorioActual = directorioInicial;
	static String carpetaSeleccionada = "";
	static String archivoSeleccionado = "";
	private final JLabel lblCarpetas = new JLabel("CARPETAS");
	private final JLabel lblFicheros = new JLabel("FICHEROS");
	private final JLabel lblDatos = new JLabel("DATOS");
	private final JSeparator separator_1 = new JSeparator();
	private final JSeparator separator_2 = new JSeparator();
	private final JSeparator separator_3 = new JSeparator();
	private final JSeparator separator_4 = new JSeparator();

	public static void main(String[] args) throws IOException {
		new ClienteFTPBasico();
	} // final del main

	public ClienteFTPBasico() throws IOException {
		super("CLIENTE BÁSICO FTP");
		getContentPane().setBackground(SystemColor.inactiveCaption);
		getContentPane().setForeground(SystemColor.desktop);
		setResizable(false);
		// para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor); // conexión al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		// Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(directorioInicial);
		// Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files);
		txtArbolDirectoriosConstruido.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtArbolDirectoriosConstruido.setBounds(11, 491, 716, 20);
		// Construyendo la lista de ficheros y directorios
		// del directorio de trabajo actual
		// preparar campos de pantalla
		txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
		txtServidor.setBounds(500, 348, 142, 20);
		txtServidor.setText("Servidor FTP: " + servidor);
		txtUsuario.setBounds(500, 410, 128, 20);
		txtUsuario.setText("Usuario: " + user);
		txtDirectorioRaiz.setBounds(500, 379, 137, 20);
		txtDirectorioRaiz.setText("DIRECTORIO RAÍZ: " + directorioInicial);
		getContentPane().setLayout(null);
		// Preparación de la lista
		// se configura el tipo de selección para que solo se pueda
		// seleccionar un elemento de la lista

		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(335, 420));
		barraDesplazamiento.setBounds(new Rectangle(11, 37, 353, 405));
		c.add(barraDesplazamiento);
		btnSubirFichero.setBounds(573, 112, 154, 23);
		c.add(btnSubirFichero);
		// final del botón Eliminar Carpeta

		c.add(txtServidor);
		c.add(txtUsuario);
		c.add(txtDirectorioRaiz);
		c.add(txtArbolDirectoriosConstruido);
		txtActualizarArbol.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtActualizarArbol.setBorder(null);
		txtActualizarArbol.setBounds(11, 460, 716, 20);
		c.add(txtActualizarArbol);
		btnCreaCarpeta.setBounds(374, 112, 154, 23);
		c.add(btnCreaCarpeta);
		btnEliminarCarpeta.setBounds(374, 156, 154, 23);
		c.add(btnEliminarCarpeta);
		botonDescargarFichero.setBounds(573, 156, 154, 23);
		c.add(botonDescargarFichero);
		botonEliminarFichero.setBounds(573, 200, 154, 23);
		c.add(botonEliminarFichero);
		botonSalir.setBounds(304, 522, 132, 36);
		c.add(botonSalir);
		c.setLayout(null);

		JLabel lblArbolDirectorios = new JLabel("ÁRBOL DE DIRECTORIOS");
		lblArbolDirectorios.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblArbolDirectorios.setBounds(11, 12, 226, 23);
		getContentPane().add(lblArbolDirectorios);

		JLabel lblAcciones = new JLabel("ACCIONES");
		lblAcciones.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAcciones.setBounds(516, 12, 78, 23);
		getContentPane().add(lblAcciones);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(Color.BLACK);
		separator.setBounds(549, 67, 9, 236);
		getContentPane().add(separator);
		lblCarpetas.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCarpetas.setBounds(422, 66, 78, 23);

		getContentPane().add(lblCarpetas);
		lblFicheros.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFicheros.setBounds(616, 67, 78, 23);

		getContentPane().add(lblFicheros);

		btnRenombrarCarpeta.setBounds(374, 200, 154, 23);
		getContentPane().add(btnRenombrarCarpeta);

		btnRenombrarFichero.setBounds(573, 247, 154, 23);
		getContentPane().add(btnRenombrarFichero);
		lblDatos.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblDatos.setBounds(526, 314, 78, 23);

		getContentPane().add(lblDatos);
		separator_1.setBackground(Color.BLACK);
		separator_1.setBounds(365, 65, 381, 13);

		getContentPane().add(separator_1);
		separator_2.setBackground(Color.BLACK);
		separator_2.setBounds(365, 304, 381, 13);

		getContentPane().add(separator_2);
		separator_3.setBackground(Color.BLACK);
		separator_3.setBounds(365, 88, 381, 13);

		getContentPane().add(separator_3);
		separator_4.setBackground(Color.BLACK);
		separator_4.setBounds(363, 440, 381, 13);

		getContentPane().add(separator_4);
		// se añaden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(748, 604);
		setVisible(true);

		/*
		 * ################################################### # Acciones al pulsar en
		 * la lista o en los botones #
		 * ###################################################
		 */

		/* LISTA DE ARCHIVOS */
		// SELECCIÓN
		listaDirec.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				if (lse.getValueIsAdjusting()) {
					try {
						archivoSeleccionado = listaDirec.getSelectedValue().toString();
						if (archivoSeleccionado.startsWith("(DIR)")) {
							carpetaSeleccionada = archivoSeleccionado.substring(6, archivoSeleccionado.length());
							txtArbolDirectoriosConstruido.setText("CARPETA SELECCIONADA: " + carpetaSeleccionada);
						} else if (archivoSeleccionado.equals("···")) {
							txtArbolDirectoriosConstruido.setText("SELECCIONADO EL DIRECTORIO PADRE");
						} else {
							txtArbolDirectoriosConstruido.setText("FICHERO SELECCIONADO: " + archivoSeleccionado);
						}
						txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + directorioActual);
						archivoSeleccionado = "";
						carpetaSeleccionada = "";
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Ha habido un error. Vuelva a intentarlo", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		// DOBLE CLICK
		listaDirec.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					try {
						archivoSeleccionado = listaDirec.getSelectedValue().toString();
						if (archivoSeleccionado.startsWith("(DIR)")) {
							try {
								carpetaSeleccionada = archivoSeleccionado.substring(6, archivoSeleccionado.length());
								if (directorioActual.equals(directorioInicial)) {
									directorioActual = directorioActual + carpetaSeleccionada;
									System.out.println(directorioActual);
								} else {
									directorioActual = directorioActual + "/" + carpetaSeleccionada;
									System.out.println(directorioActual);
								}
								// Se establece el directorio de trabajo actual
								cliente.changeWorkingDirectory(directorioActual);
								// Obteniendo ficheros y directorios del directorio actual
								FTPFile[] files = cliente.listFiles();
								llenarLista(files);
								txtArbolDirectoriosConstruido.setText("SE HA ACCEDIDO A UN NUEVO DIRECTORIO");
								txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + directorioActual);
								carpetaSeleccionada = "";
								archivoSeleccionado = "";
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								String[] partes = directorioActual.split("/");
								if (partes.length == 2) {
									directorioActual = directorioActual.substring(0,
											((directorioActual.length()) - (partes[partes.length - 1]).length()));
								} else {
									directorioActual = directorioActual.substring(0,
											((directorioActual.length()) - (partes[partes.length - 1]).length() - 1));
								}
								JOptionPane.showMessageDialog(null, "No se ha podido acceder al directorio.", "ERROR",
										JOptionPane.ERROR_MESSAGE);
								archivoSeleccionado = "";
								carpetaSeleccionada = "";
							}
						}
						if (archivoSeleccionado.equals("···")) {
							try {
								String[] partes = directorioActual.split("/");
								if (partes.length == 2) {
									directorioActual = directorioActual.substring(0,
											((directorioActual.length()) - (partes[partes.length - 1]).length()));
								} else {
									directorioActual = directorioActual.substring(0,
											((directorioActual.length()) - (partes[partes.length - 1]).length() - 1));
								}
								System.out.println(directorioActual);
								// Se establece el directorio de trabajo actual
								cliente.changeWorkingDirectory(directorioActual);
								// Obteniendo ficheros y directorios del directorio actual
								FTPFile[] files = cliente.listFiles();
								llenarLista(files);
								txtArbolDirectoriosConstruido.setText("SE HA ACCEDIDO AL DIRECTORIO PADRE");
								txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + directorioActual);
								carpetaSeleccionada = "";
								archivoSeleccionado = "";
							} catch (Exception e2) {
								if (directorioActual.equals(directorioInicial)) {
									JOptionPane.showMessageDialog(null,
											"No se ha podido acceder al directorio padre. Ya se encuentra en el directorio raíz.",
											"ERROR", JOptionPane.ERROR_MESSAGE);
								} else {
									JOptionPane.showMessageDialog(null, "No se ha podido acceder al directorio padre.",
											"ERROR", JOptionPane.ERROR_MESSAGE);
								}
								archivoSeleccionado = "";
								carpetaSeleccionada = "";
							}
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Ha habido un error. Vuelva a intentarlo", "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});

		/* CARPETAS */
		// CREAR CARPETAS
		btnCreaCarpeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio",
							"carpeta");
					if (!(nombreCarpeta == null)) {
						String directorio = directorioActual;
						if (!directorioActual.equals("/"))
							directorio = directorio + "/";
						// nombre del directorio a crear
						directorio += nombreCarpeta.trim();
						// quita blancos a derecha y a izquierda
						try {
							if (cliente.makeDirectory(directorio)) {
								String m = nombreCarpeta.trim() + " => Se ha creado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(directorioActual);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2);
							} else {
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido crear ...");
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
						}
					} // final del if
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Ha habido un error. Vuelva a intentarlo", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}); // final del botón CreaDir

		// ELIMINAR CARPETA
		btnEliminarCarpeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					archivoSeleccionado = listaDirec.getSelectedValue().toString();
					if (archivoSeleccionado.startsWith("(DIR)")) {
						carpetaSeleccionada = archivoSeleccionado.substring(6, archivoSeleccionado.length());
						int seleccion = JOptionPane.showConfirmDialog(null,
								"¿Desea eliminar la carpeta  \"" + carpetaSeleccionada + "\"?");
						if (seleccion == JOptionPane.OK_OPTION) {
							String directorio = directorioActual;
							if (!directorioActual.equals("/"))
								directorio = directorio + "/";
							// nombre del directorio a eliminar
							directorio += carpetaSeleccionada.trim(); // quita blancos a derecha y a izquierda
							try {
								if (cliente.removeDirectory(directorio)) {
									String m = carpetaSeleccionada.trim() + " => Se ha eliminado correctamente ...";
									JOptionPane.showMessageDialog(null, m);
									txtArbolDirectoriosConstruido.setText(m);
									// directorio de trabajo actual
									cliente.changeWorkingDirectory(directorioActual);
									FTPFile[] ff2 = null;
									// obtener ficheros del directorio actual
									ff2 = cliente.listFiles();
									// llenar la lista
									llenarLista(ff2);
								} else {
									JOptionPane.showMessageDialog(null,
											carpetaSeleccionada.trim() + " => No se ha podido eliminar ...");
								}
								archivoSeleccionado = "";
								carpetaSeleccionada = "";

							} catch (IOException e1) {
								archivoSeleccionado = "";
								carpetaSeleccionada = "";
								JOptionPane.showMessageDialog(null,
										carpetaSeleccionada.trim() + " => No se ha podido eliminar ...");
							}
						}
					} else {
						archivoSeleccionado = "";
						carpetaSeleccionada = "";
						JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna carpeta para eliminar ...");
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna carpeta para modificar ...");
				}
			}
		});
		// RENOMBRAR CARPETA
		btnRenombrarCarpeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String nombreNuevoCarpeta = "";
				try {
					archivoSeleccionado = listaDirec.getSelectedValue().toString();
					carpetaSeleccionada = archivoSeleccionado.substring(6, archivoSeleccionado.length());
					if (archivoSeleccionado.startsWith("(DIR)")) {
						nombreNuevoCarpeta = JOptionPane.showInputDialog(null,
								"Introduce el nombre nuevo que quieres ponerle a la carpeta \"" + carpetaSeleccionada
										+ "\"");

						if (!nombreNuevoCarpeta.trim().equals("")) {
							cliente.rename(carpetaSeleccionada, nombreNuevoCarpeta);
							String m = carpetaSeleccionada.trim() + " => Se ha modificado correctamente a ..."
									+ nombreNuevoCarpeta;
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(directorioActual);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2);
						} else {
							JOptionPane.showMessageDialog(null,
									nombreNuevoCarpeta.trim() + " => No se ha podido modificar ...");
						}
						archivoSeleccionado = "";
						carpetaSeleccionada = "";

					} else {
						JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna carpeta para modificar ...");
					}
				} catch (Exception e1) {
					nombreNuevoCarpeta="";
					if (nombreNuevoCarpeta.equals("") & !archivoSeleccionado.equals("")) {
						JOptionPane.showMessageDialog(null, "Modificación de carpeta cancelada ...");
					} else {
						JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna carpeta para modificar ...");
					}
					archivoSeleccionado = "";
					carpetaSeleccionada = "";
				}
			}
		});

		/* FICHEROS */
		// SUBIR FICHERO
		btnSubirFichero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser f;
					File file;
					f = new JFileChooser();
					// solo se pueden seleccionar ficheros
					f.setFileSelectionMode(JFileChooser.FILES_ONLY);
					// título de la ventana
					f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
					// se muestra la ventana
					int returnVal = f.showDialog(f, "Cargar");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						// fichero seleccionado
						file = f.getSelectedFile();
						// nombre completo del fichero
						String archivo = file.getAbsolutePath();
						// solo nombre del fichero
						String nombreArchivo = file.getName();
						try {
							SubirFichero(archivo, nombreArchivo);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "No se ha podido subir el fichero ...");
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Ha habido un error. Vuelva a intentarlo", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}); // Fin botón subir

		// DESCARGAR FICHERO
		botonDescargarFichero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					archivoSeleccionado = listaDirec.getSelectedValue().toString();
					if (!archivoSeleccionado.startsWith("(DIR)") & !archivoSeleccionado.equals("···")) {
						String directorio = directorioActual;
						if (!directorioActual.equals("/"))
							directorio = directorio + "/";
						if (!directorioActual.equals("")) {
							DescargarFichero(directorio + archivoSeleccionado, archivoSeleccionado);
						}
					} else {
						JOptionPane.showMessageDialog(null, "No ha seleccionado ningún fichero. Vuelva a intentarlo",
								"ERROR", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "No ha seleccionado ningún fichero. Vuelva a intentarlo",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}); // Fin botón descargar

		// ELIMINAR FICHERO
		botonEliminarFichero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String directorio = directorioActual;
					archivoSeleccionado = listaDirec.getSelectedValue().toString();
					if (!archivoSeleccionado.startsWith("(DIR)") & !archivoSeleccionado.equals("···")) {
						if (!directorioActual.equals("/"))
							directorio = directorio + "/";
						if (!directorioActual.equals("")) {
							BorrarFichero(directorio + archivoSeleccionado, archivoSeleccionado);
						}
					} else {
						JOptionPane.showMessageDialog(null, "No ha seleccionado ningún fichero. Vuelva a intentarlo",
								"ERROR", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Ha habido un error. Vuelva a intentarlo", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// RENOMBRAR FICHERO
		btnRenombrarFichero.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					archivoSeleccionado = listaDirec.getSelectedValue().toString();
					if (!archivoSeleccionado.startsWith("(DIR)") & !archivoSeleccionado.equals("···")) {
						String nombreNuevoFichero = JOptionPane.showInputDialog(null,
								"Introduce el nombre nuevo que quieres ponerle al fichero \"" + archivoSeleccionado
										+ "\"");

						if (!nombreNuevoFichero.trim().equals("")) {
							String[] partesFic = archivoSeleccionado.split("\\.");
							cliente.rename(archivoSeleccionado,
									(nombreNuevoFichero + "." + partesFic[partesFic.length - 1]));
							String m = archivoSeleccionado.trim() + " => Se ha modificado correctamente a ..."
									+ nombreNuevoFichero;
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(directorioActual);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2);
						} else {
							JOptionPane.showMessageDialog(null,
									nombreNuevoFichero.trim() + " => No se ha podido modificar ...");
						}

					} else {
						JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún fichero para modificar ...");
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún fichero para modificar ...");
				}
			}
		});

		/* SALIR */
		botonSalir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cliente.disconnect();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "No se ha podido desconectar el cliente correctamente");
				}
				System.exit(0);
			}
		});

	} // fin constructor

	private static void llenarLista(FTPFile[] files) {
		if (files == null)
			return;
		// se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		// se definen propiedades para la lista, color y tipo de fuente

		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		// se eliminan los elementos de la lista
		listaDirec.removeAll();
		// se recorre el array con los ficheros y directorios
		modeloLista.addElement("···");
		for (int i = 0; i < files.length; i++) {
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) {
				// nos saltamos los directorios . y ..
				// Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				// Si es directorio se añade al nombre (DIR)
				if (files[i].isDirectory())
					f = "(DIR) " + f;
				// se añade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			} // fin if
		} // fin for
		try {
			// se asigna el listmodel al JList,
			// se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		} catch (Exception n) {
			; // Se produce al cambiar de directorio
		}
	}// Fin llenarLista

	private boolean SubirFichero(String archivo, String soloNombre) throws IOException {
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		// directorio de trabajo actual
		cliente.changeWorkingDirectory(directorioActual);
		if (cliente.storeFile(soloNombre, in)) {
			String s = " " + soloNombre + " => Subido correctamente...";
			txtArbolDirectoriosConstruido.setText(s);
			txtActualizarArbol.setText("Se va a actualizar el árbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			// obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			// llenar la lista con los ficheros del directorio actual
			llenarLista(ff2);
			ok = true;
		} else
			txtArbolDirectoriosConstruido.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero

	private void DescargarFichero(String NombreCompleto, String nombreFichero) {
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();
		// solo se pueden seleccionar directorios
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// título de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = f.getSelectedFile();
			// obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			// construimos el nombre completo que se creará en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try {
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
				out.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
			}
		}
	} // Final de DescargarFichero

	private void BorrarFichero(String NombreCompleto, String nombreFichero) {
		// pide confirmación
		int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el fichero  \"" + nombreFichero + "\"?");
		if (seleccion == JOptionPane.OK_OPTION) {
			try {
				if (cliente.deleteFile(NombreCompleto)) {
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					txtArbolDirectoriosConstruido.setText(m);
					// directorio de trabajo actual
					cliente.changeWorkingDirectory(directorioActual);
					FTPFile[] ff2 = null;
					// obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					// llenar la lista con los ficheros del directorio actual
					llenarLista(ff2);
				} else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			}
		}
	}// Final de BorrarFichero
}// Final de la clase ClienteFTPBasico
