import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class NewNamedPlaceFormula extends JPanel {
    private JTextField nameField;
    
    NewNamedPlaceFormula() {
		setLayout(new BorderLayout());
		
		JPanel row1 = new JPanel();
		add(row1, BorderLayout.NORTH);

		row1.add(new JLabel("Name:"));
		nameField = new JTextField(10);
		row1.add(nameField);

    }

    public String getName() {
    	return nameField.getText();
    }
   
}