import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.io.PrintWriter;

public class Map extends JFrame implements WindowListener {
	protected JComboBox<String> namedOrDescribed;
	protected JTextField searchField;
	protected static ImagePanel ip = null;
	protected JFileChooser fileChooser = new JFileChooser(".");
	protected ArrayList<Place> registeredPlaces = new ArrayList<>();
	protected ArrayList<Place> none = new ArrayList<>();
	protected ArrayList<Place> tåg = new ArrayList<>();
	protected ArrayList<Place> tunnel = new ArrayList<>();
	protected ArrayList<Place> buss = new ArrayList<>();
	protected HashMap<String, ArrayList<Place>> registeredPlacesHashMap = new HashMap<>();	
	protected HashMap<Position, Place> placePositionList = new HashMap<>();
	protected HashMap<Category, ArrayList<Place> > placeMap = new  HashMap<>();
	protected ArrayList<Category> categoryList = new ArrayList<>();
	protected DefaultListModel<Category> dataModel = new DefaultListModel<>();
	protected JList<Category> jList = new JList<Category>(dataModel);
	protected static HashMap<Place, Integer> markingRectangle = new HashMap<>(); 
	protected NewLis newLis = new NewLis();
	protected MouseLis mouseLis = new MouseLis();
	protected NewPlaceLis newPlaceLis = new NewPlaceLis();
	protected PlacePressedLis pressedPlace = new PlacePressedLis();
	protected JButton hidePlacesButton;
	protected WhatIsHereMouseLis whatIsHere = new WhatIsHereMouseLis();
	private String fileNameSearchway = null;
	protected boolean changed = false;
	private int mkx, mky;
	private boolean WhatIsHereLisON = false;
	private MouseCoordinatesLis mcl = new MouseCoordinatesLis();
	protected String fileName;
	protected Category bus = new Category("Buss", Color.RED);
	protected Category train = new Category("Tåg", Color.GREEN);
	protected Category metro = new Category("Tunnelbana", Color.BLUE);
	protected Category noCategory = new Category("None", Color.BLACK);

	public Map() {
		super("Map of Järva");
		setLayout(new BorderLayout());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu archiveMenu = new JMenu("Archive");
		menuBar.add(archiveMenu);
		JMenuItem newMap = new JMenuItem("New Map");
		archiveMenu.add(newMap);
		newMap.addActionListener(new NewMapLis());
		JMenuItem loadPlaces = new JMenuItem("Load Places");
		archiveMenu.add(loadPlaces);
		loadPlaces.addActionListener(new LoadPlacesLis());
		JMenuItem save = new JMenuItem("Save");
		archiveMenu.add(save);
		save.addActionListener(new SaveLis());
		JMenuItem exit = new JMenuItem("Exit");
		archiveMenu.add(exit);
		exit.addActionListener(new ExitLis());

		JPanel north = new JPanel(new FlowLayout());
		add(north, BorderLayout.NORTH);
		JLabel newLabel = new JLabel("New: ");
		north.add(newLabel);
		String[] namedDescribed = {"Named", "Described"};
		namedOrDescribed = new JComboBox<String>(namedDescribed);
		north.add(namedOrDescribed);
		namedOrDescribed.addActionListener(newPlaceLis);
		searchField = new JTextField("Search", 15);	
		north.add(searchField);
		searchField.addFocusListener(new SearchFieldLis());
		searchField.addKeyListener(new SearchFieldLis());
		JButton searchButton = new JButton("Search");
		north.add(searchButton);
		searchButton.addActionListener(new SearchLis());
		hidePlacesButton = new JButton("Hide places");
		north.add(hidePlacesButton);
		hidePlacesButton.addActionListener(new HideplacesLis());
		JButton deletePlacesButton= new JButton("Delete places");
		north.add(deletePlacesButton);
		deletePlacesButton.addActionListener(new DeletePlacesLis());
		JButton whatIsHereButton = new JButton("What is here?");
		north.add(whatIsHereButton);
		whatIsHereButton.addActionListener(new WhatIsHereLis());


		JPanel east = new JPanel();
		add(east, BorderLayout.EAST);
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		JLabel categoryLabel = new JLabel("Catagories");
		east.add(categoryLabel);
		jList.setFixedCellWidth(120);
		east.add(new JScrollPane(jList));
		jList.setVisibleRowCount(25);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.addListSelectionListener(new ShowCategoryLis());
		jList.addMouseListener(new ShowCategoryLis());
		JButton hideCategoryButton = new JButton("Hide category");
		hideCategoryButton.addActionListener(new HideCategoryButtonLis());
		east.add(hideCategoryButton);

		dataModel.addElement(bus);
		dataModel.addElement(train);
		dataModel.addElement(metro);
		dataModel.addElement(noCategory);

		setVisible(true);
		//setSize(800, 450);
		pack();
		setLocationRelativeTo(null);
		this.addWindowListener(this);

	}

	class MyListModel extends DefaultListModel<String>{
		public void addSorted(String ny){
			int pos = 0;
			while (pos < size() && get(pos).compareTo(ny) < 0)
				pos++;
			add(pos, ny);
		}
	}

	class NewMapLis implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (changed) {
				int response = new JOptionPane().showConfirmDialog(Map.this, 
						"You have unsaved changes that will be lost if you don't save. Save?", "Warning!", 
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					saveMap();
				} 
			}
			int response = fileChooser.showOpenDialog(Map.this);
			if (response != JFileChooser.APPROVE_OPTION)
				return;

			System.out.println("\nNew session: ");
			if (!registeredPlaces.isEmpty()) 
				registeredPlaces.clear();

			if (!registeredPlacesHashMap.isEmpty()) 
				registeredPlacesHashMap.clear();

			if (!placePositionList.isEmpty()) 
				placePositionList.clear();

			if (!categoryList.isEmpty()) {
				categoryList.clear();
				dataModel.removeAllElements();
				jList.removeAll();
			}

			File file = fileChooser.getSelectedFile();
			String fileName = file.getAbsolutePath();
			System.out.println(file.getAbsolutePath());
			if (ip != null)
				remove(ip);
			ip = new ImagePanel(fileName);
			JScrollPane scrollPane = new JScrollPane(ip, 
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			add(scrollPane, BorderLayout.CENTER);

			pack();
			validate();
			repaint();
			setChanged(true);
		}
	}

	class LoadPlacesLis implements ActionListener {
		@SuppressWarnings("Unchecked")
		public void actionPerformed(ActionEvent ave) {
			int open = fileChooser.showOpenDialog(Map.this);
			if (open != JFileChooser.APPROVE_OPTION)
				return;
			File file = fileChooser.getSelectedFile();
			String fileName = file.getAbsolutePath();

			try{
				FileReader in = new FileReader(fileName);
				BufferedReader br = new BufferedReader(in);
				String line; 
				Place place;
				while ((line=br.readLine()) != null){ 
					String[] tokens = line.split(",");
					String instanceOf = tokens[0];
					String category = tokens[1];
					int x = Integer.parseInt(tokens[2]);
					int y = Integer.parseInt(tokens[3]);
					String name = tokens[4];

					if(instanceOf.equalsIgnoreCase("Named")){
						System.out.print("test");
						if(category.equalsIgnoreCase("Buss")){
							place = new NamedPlace (name, x - 2/4, y - 2/4, Color.RED, bus);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(bus, buss);
							buss.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("Tunnelbana")){
							place = new NamedPlace (name, x - 2/4, y - 2/4, Color.BLUE, metro);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(metro, tunnel);
							tunnel.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("Tåg")){
							place = new NamedPlace (name, x - 2/4, y -2/4, Color.GREEN, train);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(train, tåg);
							tåg.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("None")){
							place = new NamedPlace (name, x - 2/4, y - 2/4, Color.BLACK, noCategory);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(noCategory, none);
							none.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}

					}else{
						String description = tokens[5];
						if(category.equalsIgnoreCase("Buss")){
							place = new DescribedPlace (name, x - 2/4, y - 2/4, Color.RED, description, bus);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(bus, buss);
							buss.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("Tunnelbana")){
							place = new DescribedPlace (name, x - 2/4, y - 2/4, Color.BLUE, description, metro);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(metro, tunnel);
							tunnel.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("Tåg")){
							place = new DescribedPlace (name, x - 2/4, y - 2/4, Color.GREEN, description, train);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(train, tåg);
							tåg.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}
						if(category.equalsIgnoreCase("None")){
							place = new DescribedPlace (name, x - 2/4, y - 2/4, Color.BLACK, description, noCategory);
							ip.add(place);
							registeredPlaces.add(place);
							placePositionList.put(place.getPos(), place);
							placeMap.put(noCategory, none);
							none.add(place);
							
							if (registeredPlacesHashMap.containsKey(name)) {
								ArrayList<Place> existing = registeredPlacesHashMap.get(name);
								existing.add(place);
								registeredPlacesHashMap.put(name, existing);
							} else {
								ArrayList<Place> notExisting = new ArrayList<Place>();
								notExisting.add(place);
								registeredPlacesHashMap.put(name, notExisting);
							}
							
							place.addMouseListener(pressedPlace);
						}

					}
				} 
				br.close();
				in.close(); 

			}
			catch(FileNotFoundException e){ JOptionPane.showMessageDialog(Map.this,"Fel"); }
			catch(IOException e){ JOptionPane.showMessageDialog(Map.this,"Fel"); }

			pack();
			repaint();

		}

	}

	public class NewLis extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			Position position = new Position (mev.getX(), mev.getY()); 
			setChanged(true);
			switch ((String)namedOrDescribed.getSelectedItem()) {
			case "Named":
				try{
					NewNamedPlaceFormula namedPlaceFormula = new NewNamedPlaceFormula();
					int response = JOptionPane.showConfirmDialog(Map.this, namedPlaceFormula, 
							"Create new named place", JOptionPane.OK_CANCEL_OPTION);
					if (response != JOptionPane.OK_OPTION){
						return;
					}
					String name = namedPlaceFormula.getName();
					int x = mouseLis.getX();
					int y = mouseLis.getY();

					boolean found = false;
					int index = 0;

					Category category = jList.getSelectedValue();
					NamedPlace newNamedPlace;

					if(category == null){

						newNamedPlace = new NamedPlace(name, x, y, Color.BLACK, noCategory);
					}else{
						newNamedPlace = new NamedPlace(name, x, y, category.getColor(), category);
					}


					ip.setLayout(null);

					if(category == null || category.getName().equals("None")){
						none.add(newNamedPlace);
						placeMap.put(noCategory, none);
						System.out.println("None");
					}else if(category.getName().equals("Tunnelbana")){
						tunnel.add(newNamedPlace);
						placeMap.put(category, tunnel);
						System.out.println("T");
					}else if(category.getName().equals("Tåg")){
						tåg.add(newNamedPlace);
						placeMap.put(category, tåg);
						System.out.println("Tåg");
					}else if(category.getName().equals("Buss")){
						buss.add(newNamedPlace);
						placeMap.put(category, buss);
						System.out.println("bussssss");
					}

					registeredPlaces.add(newNamedPlace);
					if (registeredPlacesHashMap.containsKey(name)) {
						ArrayList<Place> existing = registeredPlacesHashMap.get(name);
						existing.add(newNamedPlace);
						registeredPlacesHashMap.put(name, existing);
					} else {
						ArrayList<Place> notExisting = new ArrayList<Place>();
						notExisting.add(newNamedPlace);
						registeredPlacesHashMap.put(name, notExisting);
					}
					placePositionList.put(position, newNamedPlace);


					for(Place p:registeredPlaces) {
						String s = p.toString();
						System.out.println(s);
					}


					System.out.println(newNamedPlace + " hashCode: " + newNamedPlace.pos.hashCode());

					ip.add(newNamedPlace);
					ip.repaint();	
					ip.validate();
					ip.removeMouseListener(mouseLis);
					ip.removeMouseListener(pressedPlace);
					ip.setCursor(Cursor.getDefaultCursor());

					newNamedPlace.addMouseListener(pressedPlace);

					System.out.println("- Class NewLis");

					performed = false;

				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(Map.this, "Error!");
				}
				break;
			case "Described":
				try{
					NewDescribedPlaceFormula describedPlaceFormula = new NewDescribedPlaceFormula();

					int response = JOptionPane.showConfirmDialog(Map.this, describedPlaceFormula, 
							"Create new DescribedPlace", JOptionPane.OK_CANCEL_OPTION);
					if (response != JOptionPane.OK_OPTION)
						return;
					String name = describedPlaceFormula.getName();
					int x = mouseLis.getX();
					int y = mouseLis.getY();

					boolean found = false;
					int index = 0;

					String description;
					Category category = jList.getSelectedValue();
					description = describedPlaceFormula.getDescription();
					DescribedPlace newDescribedPlace;

					if(category == null){
						newDescribedPlace = new DescribedPlace(name, x, y, Color.BLACK, description, noCategory);
					}else{
						newDescribedPlace = new DescribedPlace(name, x, y, category.getColor(), description, category);
					}

					ip.setLayout(null);

					if(category == null || category.getName().equals("None")){
						none.add(newDescribedPlace);
						placeMap.put(noCategory, none);
					}else if(category.getName().equals("Tunnelbana")){
						tunnel.add(newDescribedPlace);

						placeMap.put(category, tunnel);
					}else if(category.getName().equals("Tåg")){
						tåg.add(newDescribedPlace);
						placeMap.put(category, tåg);
					}else if(category.getName().equals("Buss")){
						buss.add(newDescribedPlace);
						placeMap.put(category, buss);
					}

					registeredPlaces.add(newDescribedPlace);
					if (registeredPlacesHashMap.containsKey(name)) {
						ArrayList<Place> existing = registeredPlacesHashMap.get(name);
						existing.add(newDescribedPlace);
						registeredPlacesHashMap.put(name, existing);
					} else {
						ArrayList<Place> notExisting = new ArrayList<Place>();
						notExisting.add(newDescribedPlace);
						registeredPlacesHashMap.put(name, notExisting);
					}
					placePositionList.put(position, newDescribedPlace);

					System.out.println(newDescribedPlace + " hashCode: " + newDescribedPlace.pos.hashCode());

					ip.add(newDescribedPlace);
					ip.repaint();	
					ip.validate();
					ip.removeMouseListener(mouseLis);
					ip.removeMouseListener(pressedPlace);
					ip.setCursor(Cursor.getDefaultCursor());

					newDescribedPlace.addMouseListener(pressedPlace);

					System.out.println("- Class NewLis - DescribePlace");

					performed = false;

				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(Map.this, "Error!");
				}
				break;
			}

			ip.removeMouseListener(newLis);
			namedOrDescribed.removeActionListener(newPlaceLis);
			namedOrDescribed.addActionListener(newPlaceLis);
		}

		public JList<Category> getJl() {
			return jList;
		}

	}

	class SaveLis implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (fileNameSearchway != null) {
				fastSaveMap();
			} else if (fileNameSearchway == null) {
				saveMap();
			} else {
				System.out.println("An error was encountered when trying to save!");
			}
		}
	}

	class ExitLis implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (changed) {
				int response = new JOptionPane().showConfirmDialog(Map.this, 
						"You have unsaved changes that will be lost if you don't save. Save?", "Warning!", 
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (response == JOptionPane.NO_OPTION) {
					System.exit(0);
					return;
				} else if (response == JOptionPane.YES_OPTION) {
					saveMap();
				} else if (response == JOptionPane.CANCEL_OPTION) {
					return;
				}
			} else {
				System.exit(0);
			}
		}
	}

	public void fastSaveMap() {
		try {
			FileOutputStream fos = new FileOutputStream(fileNameSearchway);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(ip);
			oos.writeObject(categoryList);

			setChanged(false);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e2) {
			JOptionPane.showMessageDialog(Map.this, "Cannot find the file!");
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(Map.this, "Error: " + e2.getMessage());
		}

	}

	public void saveMap() {
		int save = fileChooser.showSaveDialog(Map.this);
		if (save != JFileChooser.APPROVE_OPTION)
			return;
		File file = fileChooser.getSelectedFile();
		String fileName = file.getAbsolutePath();

		try {
			FileWriter fileOut = new FileWriter(fileName);
			PrintWriter out = new PrintWriter(fileOut);
			setChanged(false);
			for(Place places : placePositionList.values()){
				String s = places.toString();
				out.println(s);
			}
			out.close();

		}
		catch (FileNotFoundException fileException) {
			JOptionPane.showMessageDialog(Map.this, "File cannot be opened!", "Error!", JOptionPane.OK_OPTION);
		}
		catch (IOException IOException) {
			JOptionPane.showMessageDialog(Map.this, "Error! " + IOException.getMessage());
		}

	}

	class SearchFieldLis implements FocusListener, KeyListener {
		public void focusGained(FocusEvent e) {
			searchField.setText("");	
		}
		public void focusLost(FocusEvent e) {
			searchField.setText(searchField.getText());
		}
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				System.out.println("Entered");
				markSearchedClass();
			}
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}

	public void markSearchedClass() {

		for(Place p : registeredPlaces){
			if(p.isMarked() == true){
				p.setMarked(false);
			}
		}

		boolean found = false;
		String searchText = getSearchText();
		if (registeredPlacesHashMap.containsKey(searchText)) {
			try {
				ArrayList<Place> seek = registeredPlacesHashMap.get(searchText);
				for(Place p : seek){
					p.setVisible(true);
					p.setMarked(true);
					p.repaint();
					ip.repaint();

					found = true;
				}
			}catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(Map.this, "Error!");
			}
		}
		if (!found && !searchText.isEmpty()) {
			JOptionPane.showMessageDialog(Map.this, 
					"There are no places with the name "+ searchText.toString() + " registered.");
		}
	}

	class SearchLis implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			markSearchedClass();
		}
	}

	class HideplacesLis implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try {
				for (Place p : registeredPlaces) {
					if (p.marked) {
						p.setVisible(false);
						p.setHidden(true);
						p.setMarked(false);
					} 
				}
				repaint();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(Map.this, "Error with HidePlacesLis!");
			}
		}
	}

	class DeletePlacesLis implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try {
				int response = new JOptionPane().showConfirmDialog(Map.this, 
						"Are you sure you want to delete the marked places?", "Delete?", 
						JOptionPane.YES_NO_OPTION);
				if (response != JOptionPane.YES_OPTION)
					return;

				for(int j = registeredPlaces.size() - 1; j >= 0; j--){
					if(registeredPlaces.get(j).isMarked() == true){
						registeredPlaces.remove(j);
						ip.remove(j);
					}
				}

				for(Iterator<Entry<Position, Place>> it = placePositionList.entrySet().iterator(); it.hasNext(); ) {
					Entry<Position, Place> entry = it.next();
					if(entry.getValue().isMarked() == true) {
						it.remove();
					}
				}

				for(Iterator<Entry<Category, ArrayList<Place>>> it = placeMap.entrySet().iterator(); it.hasNext(); ) {
					Entry<Category, ArrayList<Place>> entry = it.next();
					ArrayList<Place> delete = entry.getValue();
					for(int j = delete.size() - 1; j >= 0; j--){
						if(delete.get(j).isMarked() == true) {
							delete.remove(j);
						}
					}

					if(delete.isEmpty()){
						it.remove();
					}
				}

				for(Iterator<Entry<String, ArrayList<Place>>> it = registeredPlacesHashMap.entrySet().iterator(); it.hasNext(); ) {
					Entry<String, ArrayList<Place>> entry = it.next();
					ArrayList<Place> delete = entry.getValue();
					for(int j = delete.size() - 1; j >= 0; j--){
						if(delete.get(j).isMarked() == true) {
							delete.remove(j);
						}
					}

					if(delete.isEmpty()){
						it.remove();
					}
				}

				System.out.println(registeredPlaces);
				System.out.println(placePositionList);
				System.out.println(placeMap);
				System.out.println(registeredPlacesHashMap);

				repaint();
				setChanged(true);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(Map.this, "Error with HidePlacesLis!");
			}

		}

	}

	class MouseCoordinatesLis implements MouseMotionListener {
		public void mouseDragged(MouseEvent arg0) {}
		public void mouseMoved(MouseEvent arg0) {
			mkx = arg0.getX()-11;
			mky = arg0.getY()-16;
		}
	}

	class WhatIsHereMouseLis implements MouseListener {
		private int x;
		private int y;

		public void mouseClicked(MouseEvent e) {
			this.x = e.getX();
			this.y = e.getY();
			System.out.println("Class WhatIsHereLis - mouseClicked " + x + " " + y);
			int xi = x-7;
			for (;xi <= x+7;xi++) {
				for (int yi = y-7; yi <= y+7;yi++) { 
					Position pos = new Position(xi, yi);
					if(placePositionList.containsKey(pos)){
						Place p = placePositionList.get(pos);
						p.setVisible(true);
					}

				}

			}
		}

		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	class WhatIsHereLis implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try{
				if (!WhatIsHereLisON) {
					System.out.println("WhatIsHereLis");
					ip.addMouseListener(whatIsHere);
					ip.addMouseMotionListener(mcl);
					WhatIsHereLisON = true;
				} else if (WhatIsHereLisON) {
					ip.removeMouseListener(whatIsHere);
					ip.removeMouseMotionListener(mcl);
					System.out.println("WhatIsHereLis-remove mouse and mouseMotion Listeners");
					WhatIsHereLisON = false;
				}
			}catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(Map.this, "Error!");
			}
		}
	}

	class HideCategoryButtonLis implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			hideCategory();
			ip.repaint();			
		}
	}

	public class MouseLis extends MouseAdapter {
		private int x;
		private int y;

		public void mousePressed(MouseEvent mp) {
			setChanged(true);
			this.x = mp.getX();
			this.y = mp.getY();
			System.out.println("Class MouseLis - mousePressed " + mp.getX() + " " + mp.getY());
		}

		public void mouseReleased(MouseEvent mr) {
			setChanged(true);
			ip.addMouseListener(newLis);
			ip.removeMouseListener(mouseLis);

		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	boolean performed = false;
	public class NewPlaceLis implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!performed) {
				setChanged(true);
				ip.addMouseListener(mouseLis);
				ip.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				performed = true;
			}
		}
	}

	public boolean mouseClicked = false;
	public class PlacePressedLis extends MouseAdapter implements MouseListener {	
		public void mouseClicked(MouseEvent mc) {
			Place p = (Place) mc.getSource();
			if (mc.getButton() == mc.BUTTON1 && !p.marked) {
				System.out.println(mc.getSource().toString());
				System.out.println("Class PlacePressedLis - mouseClicked - gets marked" + mc.getX() + " " + mc.getY() + "\n");
				p.setMarked(true);
				p.repaint();
			} else if (mc.getButton() == mc.BUTTON1 && p.marked) {
				System.out.println(mc.getSource().toString());
				System.out.println("Class PlacePressedLis - mouseClicked - gets unmarked " + mc.getX() + " " + mc.getY() + "\n");
				p.setMarked(false);
				p.repaint();
			}
			for (Place pil : registeredPlaces) {
				System.out.println(String.format("Place marked: %s (%s)", pil.marked, pil.getName()));
			}
			if (mc.getButton() == mc.BUTTON3 && !p.folded) {
				System.out.println("Folded");
				p.setFolded();
				System.out.println("" + p.getX()+22 + " " + p.getY()+57);

			} else if (mc.getButton() == mc.BUTTON3 && p.folded) {
				System.out.println("Fold");
				p.setFolded();
				System.out.println("" + p.getX()+22 + " " + p.getY()+57);
			} 

		}
	}

	public class ShowCategoryLis extends MouseAdapter implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			showCategory();
			ip.repaint();
			for (int i = 0; i < ip.getComponents().length; i++) {
				Place p = (Place) ip.getComponent(i);
				p.setMarked(false); 
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			showCategory();
			ip.repaint();

			for (int i = 0; i < ip.getComponents().length; i++) {
				Place p = (Place) ip.getComponent(i);
				p.setMarked(false); 
			}	
		}
	}

	public void showCategory() {
		Category category = jList.getSelectedValue();
		System.out.println(placeMap);
		System.out.println(category);
		if(placeMap.containsKey(category)){
			ArrayList <Place> p = placeMap.get(category);
			for (Place pp : p){
				pp.setVisible(true);
			}
		}

	}

	public void hideCategory() {
		Category category = jList.getSelectedValue();
		System.out.println(placeMap);
		System.out.println(category);
		if(placeMap.containsKey(category)){
			ArrayList <Place> p = placeMap.get(category);
			for (Place pp : p){
				pp.setVisible(false);
			}
		}

	}

	public String getSearchText() {
		return searchField.getText();
	}

	public ImagePanel getIp() {
		return ip;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean getChanged() {
		return changed;
	}

	public void unsavedWarning() {

	}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		if (changed) {
			int response = new JOptionPane().showConfirmDialog(Map.this, 
					"You have unsaved changes that will be lost if you don't save. Save?", "Warning!", 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (response == JOptionPane.NO_OPTION) {
				System.exit(0);
				return;
			} else if (response == JOptionPane.YES_OPTION) {
				saveMap();
			} else if (response == JOptionPane.CANCEL_OPTION) {
				return;
			}
		} else {
			System.out.println("windowClosing - method");
			System.exit(0);
		}
	}

	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	public static void main(String[] args) {
		Map map = new Map();
	}

}
