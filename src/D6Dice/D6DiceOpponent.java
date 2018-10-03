package D6Dice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

class D6DiceOpponent extends JPanel implements D6DiceOpponentEvent, ActionListener, FocusListener
{
	public D6DiceOpponent()
	{
		m_textfieldOpponent = new JTextField[D6DiceOpponentModel._ALL];
		for (int iIndex = D6DiceOpponentModel.HEALTH; iIndex<D6DiceOpponentModel._ALL; iIndex++) m_textfieldOpponent[iIndex] = new JTextField(6);

		m_listActionListeners = new ArrayList<ActionListener>();
	}
	
	private D6DiceOpponentModel m_opponentData;
	
	private JTextField[] m_textfieldOpponent;
	
	JPanel createPanel(D6DiceOpponentChange opponentData, Color colourOpponent)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Border borderLine = BorderFactory.createLineBorder(colourOpponent);
		Border borderSpace = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border borderBoth = BorderFactory.createCompoundBorder(borderLine, borderSpace);
		setBorder(borderBoth);
		colourOpponent = colourOpponent.brighter();
		setBackground(colourOpponent);
		
		Dimension dimensionLabel = new Dimension(70, 20);
		Dimension dimensionText = new Dimension(50, 22);
		
		String[] ascLabels = {"HEALTH", "INIT", "HIT", "DAMAGE", "ARMOUR"};
		for (int iIndex = D6DiceOpponentModel.HEALTH; iIndex<D6DiceOpponentModel._ALL; iIndex++)
		{
			JPanel panelValue = new JPanel();
			panelValue.setLayout(new BoxLayout(panelValue, BoxLayout.LINE_AXIS));
			panelValue.setBackground(colourOpponent);
			
			JLabel labelValue = new JLabel(ascLabels[iIndex], 10);
			labelValue.setPreferredSize(dimensionLabel);
			panelValue.add(labelValue);		
			panelValue.add(Box.createRigidArea(new Dimension(10,0)));
			m_textfieldOpponent[iIndex].setMaximumSize(dimensionText);
			m_textfieldOpponent[iIndex].addActionListener(this);
			m_textfieldOpponent[iIndex].addFocusListener(this);
			panelValue.add(m_textfieldOpponent[iIndex]);
			panelValue.add(Box.createHorizontalGlue());

			add(panelValue);
			add(Box.createRigidArea(new Dimension(0, 10)));
		}
		
		add(Box.createVerticalGlue());
		
		// Set fields
		m_opponentData = opponentData;
		changedOpponent(opponentData);
		// Add as listener
		opponentData.listenEvent(this);
		
		return this;
	}
	
	public D6DiceOpponentModel getModel()
	{
		return m_opponentData;
	}
	
	@Override
	public void changedOpponent(D6DiceOpponentModel opponentData) 
	{
		m_textfieldOpponent[D6DiceOpponentModel.HEALTH].setText(Integer.toString(opponentData.getHealth()));
		m_textfieldOpponent[D6DiceOpponentModel.INIT].setText(Integer.toString(opponentData.getInit()));
		m_textfieldOpponent[D6DiceOpponentModel.HIT].setText(Integer.toString(opponentData.getHit()));
		m_textfieldOpponent[D6DiceOpponentModel.DAMAGE].setText(Integer.toString(opponentData.getDamage()));
		m_textfieldOpponent[D6DiceOpponentModel.ARMOUR].setText(Integer.toString(opponentData.getArmour()));		
	}
	
	private List<ActionListener> m_listActionListeners;
	
	void addActionListener(ActionListener listenAction)
	{
		m_listActionListeners.add(listenAction);
	}
	
	void removeActionListener(ActionListener listenAction)
	{
		m_listActionListeners.remove(listenAction);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		JTextField field = (JTextField)event.getSource();
		String scField = field.getText();
		int iValue = Integer.parseInt(scField);
				
		// Convert action event to field / value
		
		for (int iField = D6DiceOpponentModel.HEALTH; iField<D6DiceOpponentModel._ALL; iField++)
		{
			if (m_textfieldOpponent[iField]!=field) continue;			
			event = D6DiceOpponentControl.eventField(event, m_opponentData, iField, "FIELD", iValue);			
			break;
		}

		// Propagate action with field/value
		for (ActionListener listener : m_listActionListeners)
			listener.actionPerformed(event);
	}

	@Override
	public void focusGained(FocusEvent arg0) 
	{
		// Nothing to do on focus gain
		return;
	}

	@Override
	public void focusLost(FocusEvent eventFocus) 
	{
		// Generate action for field
		actionPerformed(new ActionEvent(eventFocus.getSource(), D6DiceOpponentModel._ALL, "FIELD"));
	}
	
	/**
	 * ID for swing
	 */
	private static final long serialVersionUID = 7193046513406951762L;

	public static void main(String[] args) 
	{
		int[] aiFields = {1, 2, 3, 4, 5};
		D6DiceOpponentChange opponentData = new D6DiceOpponentChange();
		opponentData.create(aiFields);
		
		D6DiceOpponent opponentView = new D6DiceOpponent();
		opponentView.createPanel(opponentData, new Color(0x90, 0xFF, 0x90));
		D6DiceHelper.frameView("Fill in the fields", opponentView);
	}
};
