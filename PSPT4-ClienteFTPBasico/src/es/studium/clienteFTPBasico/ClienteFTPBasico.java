package es.studium.clienteFTPBasico;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTPBasico extends JFrame 
{
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JTextField txtServidor = new JTextField();
	static JTextField txtUsuario = new JTextField();
	static JTextField txtDirectorioRaiz = new JTextField();
	// Campos de mensajes parte inferior
	private static JTextField txtArbolDirectoriosConstruido = new JTextField();
	private static JTextField txtActualizarArbol = new JTextField();
	// Botones
	JButton botonCargar = new JButton("Subir fichero");
	JButton botonDescargar = new JButton("Descargar fichero");
	JButton botonBorrar = new JButton("Eliminar fichero");
	JButton botonCreaDir = new JButton("Crear carpeta");
	JButton botonDelDir = new JButton("Eliminar carpeta");
	JButton botonSalir = new JButton("Salir");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container c = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "127.0.0.1";
	String user = "jorge";
	String pasw = "Studium2019;";
	boolean login;
	static String direcInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec = direcInicial;
	static String ficheroSelec = "";
	public static void main(String[] args) throws IOException 
	{
		new ClienteFTPBasico();
	} // final del main

	public ClienteFTPBasico() throws IOException
	{
		super("CLIENTE BÁSICO FTP");
		//para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter (System.out)));
		cliente.connect(servidor); //conexión al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		//Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcInicial);
		//Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files,direcInicial);
		//Construyendo la lista de ficheros y directorios
		//del directorio de trabajo actual		
		//preparar campos de pantalla
		txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
		txtServidor.setText("Servidor FTP: "+servidor);
		txtUsuario.setText("Usuario: "+user);
		txtDirectorioRaiz.setText("DIRECTORIO RAIZ: "+direcInicial);
		//Preparación de la lista
		//se configura el tipo de selección para que solo se pueda
		//seleccionar un elemento de la lista

		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(335,420));
		barraDesplazamiento.setBounds(new Rectangle(5,65,335,420));
		c.add(barraDesplazamiento);
		c.add(txtServidor);
		c.add(txtUsuario);
		c.add(txtDirectorioRaiz);
		c.add(txtArbolDirectoriosConstruido);
		c.add(txtActualizarArbol);
		c.add(botonCargar);
		c.add(botonCreaDir);
		c.add(botonDelDir);
		c.add(botonDescargar);
		c.add(botonBorrar);
		c.add(botonSalir);
		c.setLayout(null);
		//se añaden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(510,600);
		setVisible(true);
		//Acciones al pulsar en la lista o en los botones
		listaDirec.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent lse)
			{
				// TODO Auto-generated method stub
				String fic = "";
				if (lse.getValueIsAdjusting()) 
				{
					ficheroSelec ="";
					//elemento que se ha seleccionado de la lista
					fic =listaDirec.getSelectedValue().toString();
					//Se trata de un fichero
					ficheroSelec = direcSelec;
					txtArbolDirectoriosConstruido.setText("FICHERO SELECCIONADO: " + ficheroSelec);
					ficheroSelec = fic;//nos quedamos con el nombre
					txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + direcSelec);
				}
			}
		});
		botonSalir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					cliente.disconnect();
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		botonCreaDir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio","carpeta");
				if (!(nombreCarpeta==null)) 
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					//nombre del directorio a crear
					directorio += nombreCarpeta.trim(); 
					//quita blancos a derecha y a izquierda
					try 
					{
						if (cliente.makeDirectory(directorio))
						{
							String m = nombreCarpeta.trim()+ " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							//directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							//obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							//llenar la lista
							llenarLista(ff2, direcSelec);
						}
						else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del botón CreaDir
		botonDelDir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null,"Introduce el nombre del directorio a eliminar","carpeta");
				if (!(nombreCarpeta==null)) 
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					//nombre del directorio a eliminar
					directorio += nombreCarpeta.trim(); //quita blancos a derecha y a izquierda
					try 
					{
						if(cliente.removeDirectory(directorio)) 
						{
							String m = nombreCarpeta.trim()+" => Se ha eliminado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							//directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							//obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							//llenar la lista
							llenarLista(ff2, direcSelec);
						}
						else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido eliminar ...");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} 
				// final del if
			}
		}); 
		//final del botón Eliminar Carpeta
		botonCargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser f;
				File file;
				f = new JFileChooser();
				//solo se pueden seleccionar ficheros
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//t�tulo de la ventana
				f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				//se muestra la ventana
				int returnVal = f.showDialog(f, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
					//fichero seleccionado
					file = f.getSelectedFile();
					//nombre completo del fichero
					String archivo = file.getAbsolutePath();
					//solo nombre del fichero
					String nombreArchivo = file.getName();
					try 
					{
						SubirFichero(archivo, nombreArchivo);
					}
					catch (IOException e1) 
					{
						e1.printStackTrace(); 
					}
				}
			}
		}); //Fin botón subir
		botonDescargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals("")) 
				{
					DescargarFichero(directorio + ficheroSelec, ficheroSelec);
				}
			}
		}); // Fin botón descargar
		botonBorrar.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals("")) 
				{
					BorrarFichero(directorio + ficheroSelec,ficheroSelec);
				}
			}
		});
	} // fin constructor
	
	private static void llenarLista(FTPFile[] files,String direc2) 
	{
		if (files == null)
			return;
		//se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		//se definen propiedades para la lista, color y tipo de fuente

		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		//se eliminan los elementos de la lista
		listaDirec.removeAll();
		try 
		{
			//se establece el directorio de trabajo actual
			cliente.changeWorkingDirectory(direc2);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		direcSelec = direc2; //directorio actual
		//se añade el directorio de trabajo al listmodel, primerelementomodeloLista.addElement(direc2);
		//se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++) 
		{
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) 
			{
				//nos saltamos los directorios . y ..
				//Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				//Si es directorio se añade al nombre (DIR)
				if (files[i].isDirectory()) f = "(DIR) " + f;
				//se añade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			}//fin if
		}//fin for
		try 
		{
			//se asigna el listmodel al JList,
			//se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		}
		catch (NullPointerException n) 
		{
			; //Se produce al cambiar de directorio
		}
	}//Fin llenarLista
	
	private boolean SubirFichero(String archivo, String soloNombre) throws IOException 
	{
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		//directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in)) 
		{
			String s = " " + soloNombre + " => Subido correctamente...";
			txtArbolDirectoriosConstruido.setText(s);
			txtActualizarArbol.setText("Se va a actualizar el árbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			//obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			//llenar la lista con los ficheros del directorio actual
			llenarLista(ff2,direcSelec);
			ok = true;
		}
		else
			txtArbolDirectoriosConstruido.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero
	
	private void DescargarFichero(String NombreCompleto, String nombreFichero) 
	{
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();
		//solo se pueden seleccionar directorios
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//título de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{
			file = f.getSelectedFile();
			//obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			//construimos el nombre completo que se creará en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try 
			{
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null,	nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null,	nombreFichero + " => No se ha podido descargar ...");
				out.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero
	private void BorrarFichero(String NombreCompleto, String nombreFichero) 
	{
		//pide confirmaci�n
		int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el fichero seleccionado?");
		if (seleccion == JOptionPane.OK_OPTION) 
		{
			try 
			{
				if (cliente.deleteFile(NombreCompleto)) 
				{
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					txtArbolDirectoriosConstruido.setText(m);
					//directorio de trabajo actual
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2 = null;
					//obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					//llenar la lista con los ficheros del directorio actual
					llenarLista(ff2, direcSelec);
				}
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}// Final de BorrarFichero
}// Final de la clase ClienteFTPBasico