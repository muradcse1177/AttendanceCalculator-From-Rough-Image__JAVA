package com.attendence;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.utility.*;
import com.exports.PdfMaker;
import com.exports.TxtMaker;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.utility.ImageUtility;

public class Main extends JFrame {

	
	BufferedImage oldImage = null;
	private static final long serialVersionUID = 1L;
	private int imageWidth = 900;
	private int imageHeight = 1400;
	public final static int OUTPUT_IMAGE_WIDTH = 2000;
	public final static int OUTPUT_IMAGE_HEIGHT = 3200;
	private int windowHeight = 600;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem closeMenuItem;
	private JMenuItem mntmExit;
	private JMenu exportMenu;
	private JMenuItem exportAsPdf;
	private JMenuItem exportAsPlainText;
	private BufferedImage bufferedImage;
	private JMenu mnOperations;
	private JMenuItem mntmProcess;
	private ImagePanel imagePanel;
	public static final int FIRST_Y_COORDINATE = 0;
	public static final int HEIGHT_OF_EACH_ROW = 106;
	private static double HEIGHT_ADJUST = 0.2f;
	public static final int UPPER_LEFT_CORNER_Y = 127;

	public BufferedImage sheetHeader;

	private ArrayList<StudentInfo> studentInfo = new ArrayList<>();

	public Main() throws IOException {
		
        //matrix input A....................
		
		double arr[][] = new double[784][100];
		Scanner input = new Scanner(new File("data/matA.txt"));
		for (int i = 0; i < 784; i++) {
			for (int j = 0; j < 100; j++) {
				arr[i][j] = input.nextDouble();
			}
		}

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(imageWidth + 20, windowHeight);
		setResizable(false);
		setLocationRelativeTo(null);

		setJMenu();

		imagePanel = new ImagePanel(this, null, imageWidth, imageHeight);
		JScrollPane scPane = new JScrollPane(imagePanel);

		getContentPane().add(scPane);

		setVisible(true);
	}

	public void setJMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");

		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser("Choose Roll Sheet");
				chooser.showOpenDialog(Main.this);
				File file = chooser.getSelectedFile();
				
				//jpg or png selector....................
				
				if (file != null && file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
					try {

						bufferedImage = ImageIO.read(file);
						oldImage = bufferedImage;
						boolean mat[][] = ImageUtility.getThresholdMatrix(bufferedImage, 70); //thresholding
						BlobExtraction bl = new BlobExtraction(mat);
						boolean[][] n = bl.getExtractedMatrix(800); //rectangle remove
						bufferedImage = ImageUtility.getImage(n);
						imagePanel.setImage(bufferedImage, oldImage);
						


					} catch (IOException e1) {
						e1.printStackTrace();
					}


				} else {
					JOptionPane.showMessageDialog(Main.this, "Please Select JPG or PNG image.");
				}
			}
		});
		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imagePanel.setImage(null, null);
			}
		});

		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);

		menuBar.add(fileMenu);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(mntmExit);

		setJMenuBar(menuBar);

		mnOperations = new JMenu("Operations");
		menuBar.add(mnOperations);

		mntmProcess = new JMenuItem("Process");
		mntmProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					long startTime = System.nanoTime();
					processImage(); //process image..................

					long stopTime = System.nanoTime();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		mnOperations.add(mntmProcess);
		

		// Export Menu...................
		
		exportMenu = new JMenu("Export");
		exportAsPdf = new JMenuItem("As PDF");
		exportAsPdf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				
				//PDF maker..................
				
				if (studentInfo.size() == 0) {
					JOptionPane.showMessageDialog(Main.this, "Please Process First.");

				} else {
					JFileChooser chooser = new JFileChooser();
					chooser.showSaveDialog(Main.this);
					File file = chooser.getSelectedFile();
					if (file != null) {

						try {
							PdfMaker maker = new PdfMaker(studentInfo, sheetHeader, file.getAbsolutePath() + ".pdf");
							maker.makePdf();
						} catch (DocumentException es) {
							es.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

					} else {
						JOptionPane.showMessageDialog(Main.this, "Please Select A File.");
					}

				}
			}
		});

		exportAsPlainText = new JMenuItem("As Plain Text");
		exportAsPlainText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				//Text maker.....................

				if (studentInfo.size() == 0) {
					JOptionPane.showMessageDialog(Main.this, "Please Process First.");

				} else {
					JFileChooser chooser = new JFileChooser();
					chooser.showSaveDialog(Main.this);
					File file = chooser.getSelectedFile();

					if (file != null) {
						TxtMaker maker = new TxtMaker(studentInfo, file.getAbsolutePath() + ".txt");
						maker.makeTxtFile();

					} else {
						JOptionPane.showMessageDialog(Main.this, "Please Select A File.");
					}
				}
			}
		});

		exportMenu.add(exportAsPdf);
		exportMenu.addSeparator();
		exportMenu.add(exportAsPlainText);

		menuBar.add(exportMenu);

	}
	
	
	//process image..........................

	public void processImage() throws IOException {

		if (bufferedImage == null) {
			JOptionPane.showMessageDialog(Main.this, "Please Add Attendance Sheet");
		} else {
			bufferedImage = imagePanel.getCropedImage();

			if (bufferedImage == null) {
				JOptionPane.showMessageDialog(Main.this, "Please Select Four Coorner Point.");

			} else {
			
				
				
				Rectangle corrInfoRect = new Rectangle(0, UPPER_LEFT_CORNER_Y, bufferedImage.getWidth(),
						bufferedImage.getHeight() - UPPER_LEFT_CORNER_Y); //remove the 1st rectangle......................
				
				
				
				bufferedImage = ImageUtility.cropImage(bufferedImage, corrInfoRect); //crop image

				bufferedImage = ImageUtility.getScaledImage(OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT, bufferedImage); //scale image

				ImageIO.write(bufferedImage, "jpg", new File("finalImage.jpg"));

				splitStudentInfo();
				imagePanel.setImage(bufferedImage, oldImage);
				imagePanel.setProcessed();

			}

		}

	}

	
	//row segmentation...............
	
	private void splitStudentInfo() {
		double pos = 0;
		for (int i = 0; i < 30; i++) {

			BufferedImage bf = new BufferedImage(bufferedImage.getWidth(), HEIGHT_OF_EACH_ROW, bufferedImage.getType());

			Rectangle r = new Rectangle(0, (int) pos, bufferedImage.getWidth(),
					(int) (Math.min(HEIGHT_OF_EACH_ROW, bufferedImage.getHeight() - pos)));

			bf = ImageUtility.cropImage(bufferedImage, r);
			studentInfo.add(new StudentInfo(bf, i));
			pos += (double) HEIGHT_OF_EACH_ROW + HEIGHT_ADJUST;
		}
	}

	public static void main(String[] args) throws IOException {
		new Main();
	}

}
