import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NewCategoryFormula extends JPanel{
	
	private JTextField nameField;
	private JColorChooser colorChooser;
	private Color color;
	
	public NewCategoryFormula() {
		setLayout(new BorderLayout());
		
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(panel1, BorderLayout.NORTH);
		
		panel1.add(new JLabel("Name: "));
		nameField = new JTextField(10);
		panel1.add(nameField);
		
		JPanel panel2 = new JPanel();
		add(panel2, BorderLayout.CENTER);
		
		colorChooser = new JColorChooser();
		panel2.add(colorChooser);
		
		colorChooser.getSelectionModel().addChangeListener(new cl());
		
	}
	
	public String getName() {
		return nameField.getText();
	}
	
	public Color getColor() {
		return color;
	}
	
	class cl implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			color = colorChooser.getColor();
		}
		
	}
	
	
}
