/****Flash Card Viewer/Editor Program****
****David Gardner, 2012-2013****
*
*To-Do:
*-Possibly add built-in timer
*-Fix glitch with Revisiting the last card in a deck
*-Optimize size of Frame, lock size
*-Figure out how to center frame
*-Make Flip Deck a button
*-Change title when add/remove cards (on navigate between perspectives???)
*/
package flashCards;



import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

public class CardMain extends JFrame {

	//JPanel screen = new CardViewer();
	private boolean isViewer = false;
	private boolean isEditor = false;
	private CardEditor editor;
	private CardViewer viewer;
	private static String title = "FlashCards Beta";
	//private JPanel mainPanel;
	Deck mainDeck;
	JFileChooser fileChooser = new JFileChooser();
	
	private String help = "Flash Cards Viewer/Editor\nDeveloped by David Gardner 2012-2013\n\n"
			+"To create a new deck of Flash Cards, press File>New Deck.\n"
			+"When you are finished editing, press File>Save then View>Viewer to return to the viewer\n"
			+"To load a previously created deck, press File>Load Deck" 
			+"and select the file you wish to view/edit\n"
			+"Once you have loaded a deck, use the View>Viewer\n" +
			" and the View>Editor options to switch between viewing and editing perspectives.\n" +
			"When viewing a deck, use the Flip Button to flip over a card, the Next button to \n" +
			"transition to the next card, Revisit to move a card to the back of the deck \n" +
			"(this will not count for the Card Total), the Previous button to move to the previous card,\n" +
			"and the Shuffle button to shuffle the deck (this returns to the first card \n" +
			"and resets the Card Total.\nTo start with the other side of each card, press Flip Deck.\n" +
			"Note: While this program does not include options to transfer cards from one deck to another, \n" +
			"the decks are stored as plain text in the file system so the user could easily do this manually\n" +
			"(at their own risk of accidently corrupting the data).";
	
	public CardMain() {
		super(title);
		setSize(700, 410);
		add(createGUI(), BorderLayout.NORTH);
		//mainPanel = loadViewer();
		//isViewer = true;
		viewer = new CardViewer();
		editor = new CardEditor(true);
		loadViewer();
		//add(mainPanel, BorderLayout.CENTER);
		//pack();
		//setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setTitle(String title) {
		super.setTitle(title);
	}
	public void updateTitle() {
		setTitle(mainDeck.toString() + " - " + title);
	}
	public void refresh() {
		super.setVisible(true);
	}
	
	public JMenuBar createGUI() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		//file.setMnemonic('F');
		JMenu view = new JMenu("View");
		//view.setMnemonic('V');
		JMenuItem loadItem = new JMenuItem("Load Deck");
		loadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//JFileChooser
				//String fileName = JOptionPane.showInputDialog("Enter fileName");
				int retVal = fileChooser.showOpenDialog(CardMain.this);
				if(retVal == JFileChooser.APPROVE_OPTION) {
					mainDeck = loadDeck(fileChooser.getSelectedFile());
					if (mainDeck != null) {
						viewer.setDeck(mainDeck);
						editor.setDeck(mainDeck);
						setTitle(mainDeck.toString()+ " - " + title);
					}
				}
				//mainDeck = loadDeck(fileName.trim());
				//System.out.println(mainDeck.toString());
			}
		});
		file.add(loadItem);
		JMenuItem newItem = new JMenuItem("New Deck");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadEditorNew();
			}
		});
		file.add(newItem);
		JMenuItem editItem = new JMenuItem("Editor");
		editItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadEditor();
			}
		});
		view.add(editItem);
		JMenuItem viewItem = new JMenuItem("Viewer");
		viewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadViewer();
			}
		});
		view.add(viewItem);
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(CardMain.this, help, "Help", JOptionPane.PLAIN_MESSAGE);
			}
		});
		view.add(helpItem);
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retVal = fileChooser.showSaveDialog(CardMain.this);
				if(retVal == JFileChooser.APPROVE_OPTION) {
					File dir = fileChooser.getSelectedFile();
					//String path = dir.getAbsolutePath();
					File file = new File(mainDeck.getFullPath(dir.getAbsolutePath()));
					System.out.println(file.getAbsolutePath());
					System.out.println(mainDeck);
					saveDeck(mainDeck, file);
				}
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
		});
		file.add(saveItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				System.exit(0);
			}
		});
		file.add(exitItem);
		
		bar.add(file);
		bar.add(view);
		return bar;
	}
	
	public void loadViewer() {
		if (isEditor) {
			editor.cleanUp();
			mainDeck = editor.getDeck();
			remove(editor); 
			updateTitle();
		}
		if (mainDeck != null) {
			viewer = new CardViewer(mainDeck);
		}
		else viewer = new CardViewer();
		
		add(viewer, BorderLayout.CENTER);
		isViewer = true;
		isEditor = false;
		setVisible(true);
		//return new CardViewer();
	}
	
	public void loadEditor() {
		if (mainDeck!=null) {
			editor = new CardEditor(mainDeck);
		}
		else {editor = new CardEditor();}
		if(isViewer) {
			remove(viewer);
		}
		else {
			remove(editor); 
		}
		add(editor, BorderLayout.CENTER);
		isEditor = true;
		isViewer = false;
		//return new CardEditor();
		this.setVisible(true);
	}
	public void loadEditorNew() {
		editor = new CardEditor();
		if(isViewer) {
			remove(viewer); 
		}
		add(editor, BorderLayout.CENTER);
		isViewer = false;
		isEditor = true;
		setVisible(true);
	}
/*	public JPanel loadEditor(Deck deck) {
		isEditor = true;
		isViewer = false;
		return new CardEditor(deck);
	}*/
	
	public static Deck loadDeck(String fileName) {
		return loadDeck(new File(fileName));
	}
	public static Deck loadDeck(File file) {
		Deck deck;
		int i=0;
		ArrayList<String> input = new ArrayList<String>();
		//String input[] = new String[Deck.DECKMAX];
		//File file = new File(fileName);
		Scanner fileIn;
		try {
			fileIn = new Scanner(file);
			//deck = new Deck(fileIn.nextLine());
			while(fileIn.hasNextLine()) {
				input.add(fileIn.nextLine().trim());
				System.out.println(i+": "+input.get(i));
				i++;
				//deck.addCard(new Card(fileIn.nextLine(), fileIn.nextLine()));
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find requested file.");
			JOptionPane.showMessageDialog(null, "Cannot find requested file", "IO Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
			} 
		String[] array = new String[input.size()];
		array = input.toArray(array);
		deck = Deck.constructFromSaveState(array);
		//System.out.println(deck.toString());
		return deck;
	}
	public static void saveDeck(Deck deck, File file) {
		String[] saveState = deck.toSaveState();
		try {
			FileWriter writer = new FileWriter(file, false);
			for(int i=0; i<saveState.length; i++) {
				System.out.println(i + ": " + saveState[i]);
				writer.write(saveState[i]);
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("Cannot open requested file");
			JOptionPane.showMessageDialog(null, "IO Error", "IO Error: Cannot open file",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		new CardMain();

	}

}
