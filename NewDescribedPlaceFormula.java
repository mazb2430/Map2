import javax.swing.*;

class NewDescribedPlaceFormula extends JPanel {
	
    private JTextField nameField;
    private JTextArea textArea;

    NewDescribedPlaceFormula() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel row1 = new JPanel();
		add(row1);
		row1.add(new JLabel("Name:"));
		nameField = new JTextField(10);
		row1.add(nameField);
	
		JPanel row2 = new JPanel();
		add(row2);
		row2.add(new JLabel("Description:"));
		textArea = new JTextArea(5, 20);
		row2.add(new JScrollPane(textArea));
		
    }

    public String getName(){
    	return nameField.getText();
    }
    
    public String getDescription() {
    	String s = textArea.getText();
    	return s;
    }
    
    
}
