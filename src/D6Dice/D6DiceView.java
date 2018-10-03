package D6Dice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

class D6DiceView extends JPanel implements ActionListener
{
	public D6DiceView()
	{
		m_listActionListeners = new ArrayList<ActionListener>();
	}

	private D6DiceOpponent[] m_aOpponent;
	private D6DiceResult m_panelResults;
	
	JPanel createPanel(D6DiceOpponentChange[] aOpponents, boolean zAdvantage, boolean zAdd, boolean zDefend, D6DiceResultChange dice)
	{
		setLayout(new BorderLayout());
		
		JButton buttonAdd = new JButton("Add");
		buttonAdd.setActionCommand("ADD");
		buttonAdd.addActionListener(this);
		
		JButton buttonRemove = new JButton("Remove");
		buttonRemove.setActionCommand("REMOVE");
		buttonRemove.addActionListener(this);
		
		JButton buttonGo = new JButton("Go!");		
		buttonGo.setActionCommand("GO");
		buttonGo.addActionListener(this);
		
		JButton buttonSim = new JButton("Simulate");		
		buttonSim.setActionCommand("SIMULATE");
		buttonSim.addActionListener(this);
		
		JCheckBox checkboxAdvantage = new JCheckBox("Add INIT to HIT?", zAdvantage);
		checkboxAdvantage.setToolTipText("For example for melee combat");
		checkboxAdvantage.setBackground((Color.GRAY).brighter());
		checkboxAdvantage.setActionCommand("MELEE");
		checkboxAdvantage.addActionListener(this);
		
		JCheckBox checkboxAdd = new JCheckBox("Add successes to DAMAGE?", zAdd);
		checkboxAdd.setToolTipText("For example for ranged combat");
		checkboxAdd.setBackground((Color.GRAY).brighter());
		checkboxAdd.setActionCommand("RANGED");
		checkboxAdd.addActionListener(this);
		
		JCheckBox checkboxDefend = new JCheckBox("Only defend?", zDefend);
		checkboxDefend.setToolTipText("No DAMAGE");
		checkboxDefend.setBackground((Color.GRAY).brighter());
		checkboxDefend.setActionCommand("DEFEND");
		checkboxDefend.addActionListener(this);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.LINE_AXIS));
		Border borderLine = BorderFactory.createLineBorder(Color.GRAY);
		Border borderSpace = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border borderBoth = BorderFactory.createCompoundBorder(borderLine, borderSpace);
		panelButtons.setBorder(borderBoth);
		panelButtons.setBackground((Color.GRAY).brighter());
		
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		panelButtons.add(buttonAdd);
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		panelButtons.add(buttonRemove);
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		panelButtons.add(buttonGo);
		panelButtons.add(Box.createRigidArea(new Dimension(20,0)));
		panelButtons.add(buttonSim);
		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(checkboxAdvantage);		
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		panelButtons.add(checkboxAdd);
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		panelButtons.add(checkboxDefend);
		panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
		
		add(panelButtons, BorderLayout.NORTH);
		
		m_aOpponent = new D6DiceOpponent[aOpponents.length];
		
		JPanel panelOpponents = new JPanel();
		panelOpponents.setLayout(new BoxLayout(panelOpponents, BoxLayout.LINE_AXIS));
		
		Color colourOpponent = new Color(0xA0, 0xFF, 0xA0);
		for (int iIndex = 0; iIndex<aOpponents.length; iIndex++)
		{
			m_aOpponent[iIndex] = new D6DiceOpponent();
			m_aOpponent[iIndex].createPanel(aOpponents[iIndex], colourOpponent);
			m_aOpponent[iIndex].addActionListener(this);
			
			panelOpponents.add(m_aOpponent[iIndex]);

			colourOpponent = new Color(0xFF, 0xA0, 0xA0);
		}
		
		add(panelOpponents, BorderLayout.CENTER);
		
		Color colourResults = new Color(0x80, 0x80, 0xFF);
		m_panelResults = createPanelResults(colourResults, dice);
		
		JPanel panelBorder = new JPanel();
		panelBorder.setLayout(new BorderLayout());
		Border borderLineResults = BorderFactory.createLineBorder(colourResults);
		Border borderSpaceResults = BorderFactory.createEmptyBorder(7, 7, 7, 7);
		Border borderResults = BorderFactory.createCompoundBorder(borderLineResults, borderSpaceResults);
		panelBorder.setBorder(borderResults);
		panelBorder.setBackground(colourResults.brighter());
		panelBorder.add(m_panelResults, BorderLayout.CENTER);
		
		add(panelBorder, BorderLayout.SOUTH);
		
		return this;
	}
	
	private D6DiceResult createPanelResults(Color colourResults, D6DiceResultChange dice)
	{
		D6DiceResult panelResults = new D6DiceResult();
		panelResults.createPanel(dice);
		
		Border borderLineResults = BorderFactory.createLineBorder(colourResults);
		Border borderTitle = BorderFactory.createTitledBorder(borderLineResults, "Results");
		
		Border borderSpace = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border borderBothResults = BorderFactory.createCompoundBorder(borderSpace, borderTitle);
		
		panelResults.setBorder(borderBothResults);
		panelResults.setBackground(colourResults.brighter());
		
		return panelResults;
	}
	
	private D6DiceResult createPanelResults(Color colourResults, D6DiceResultChange calculated, D6DiceResultChange simulated)
	{
		D6DiceResult panelResults = createPanelResults(colourResults, calculated);
		
		D6DiceBarBox barboxSimulated = new D6DiceBarBoxPaint();
		barboxSimulated.createPanel(simulated);
		
		JPanel panelBar = new JPanel();
		panelBar.setLayout(new BorderLayout());
		panelBar.add(new JLabel("Simulate %"), BorderLayout.NORTH);
		panelBar.add(barboxSimulated);

		panelResults.add(panelBar, BorderLayout.SOUTH);
		panelResults.setBackground(colourResults.brighter());
		
		return panelResults;
	}
	
	void removeOpponent(D6DiceOpponentModel opponentData, D6DiceResultChange dice)
	{
		for (int iIndex = 0; iIndex<m_aOpponent.length; iIndex++)
		{
			if (m_aOpponent[iIndex].getModel()!=opponentData) continue;
			removeOpponent(iIndex, dice);
			break;
		}
		return;
	}
	
	private void removeOpponent(int iRemove, D6DiceResultChange dice)
	{
		int iNumber = m_aOpponent.length;
		D6DiceOpponent[] aopponentView = new D6DiceOpponent[iNumber-1];
		
		int iIndex;
		
		for (iIndex = 0; iIndex<iRemove; iIndex++)
		{
			aopponentView[iIndex] = m_aOpponent[iIndex];
		}
		for (iIndex += 1; iIndex<iNumber; iIndex++)
		{
			aopponentView[iIndex-1] = m_aOpponent[iIndex];
		}
		m_aOpponent[iRemove].removeActionListener(this);
		
		Container containerPanel = m_aOpponent[iRemove].getParent();
		containerPanel.remove(m_aOpponent[iRemove]);
		
		Color colourResults = new Color(0x80, 0x80, 0xFF);
		D6DiceResult panelResults = createPanelResults(colourResults, dice);
		
		Container containerResults = m_panelResults.getParent();
		containerResults.remove(m_panelResults);
		containerResults.add(panelResults);
		
		m_aOpponent = aopponentView;
		m_panelResults = panelResults;
		
		validate();
		
		return;
	}
	
	void addOpponent(D6DiceOpponentChange opponentData, D6DiceResultChange dice)
	{
		int iNumber = m_aOpponent.length;
		Color colourOpponent = new Color(0xFF, 0xA0, 0xA0);
		D6DiceOpponent[] aopponentView = new D6DiceOpponent[iNumber+1];
		
		for (int iIndex = 0; iIndex<iNumber; iIndex++) aopponentView[iIndex] = m_aOpponent[iIndex];
		aopponentView[iNumber] = new D6DiceOpponent();
		aopponentView[iNumber].createPanel(opponentData, colourOpponent);
		aopponentView[iNumber].addActionListener(this);
		
		Container containerOpponents = m_aOpponent[0].getParent();
		containerOpponents.add(aopponentView[iNumber]);
		
		Color colourResults = new Color(0x80, 0x80, 0xFF);
		D6DiceResult panelResults = createPanelResults(colourResults, dice);
		
		Container containerResults = m_panelResults.getParent();
		containerResults.remove(m_panelResults);
		containerResults.add(panelResults);
		
		m_aOpponent = aopponentView;
		m_panelResults = panelResults;
		
		validate();
		
		return;
	}
	
	void addSimulation(D6DiceResultChange calculated, D6DiceResultChange simulated)
	{
		Color colourResults = new Color(0x80, 0x80, 0xFF);
		D6DiceResult panelResults = createPanelResults(colourResults, calculated, simulated);
		
		Container containerResults = m_panelResults.getParent();
		containerResults.remove(m_panelResults);
		m_panelResults = panelResults;
		containerResults.add(panelResults);

		validate();
		
		return;
	}
	
	private List<ActionListener> m_listActionListeners;
	
	void addActionListener(ActionListener listenAction)
	{
		m_listActionListeners.add(listenAction);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		Object eventSource = event.getSource();
		
		if (eventSource instanceof JCheckBox)
		{
			// Convert JCheckBox events to Boolean source
			JCheckBox checkbox = (JCheckBox)eventSource;
			Boolean zState = checkbox.isSelected();
			event.setSource(zState);
		}
		
		if (eventSource instanceof JButton)
		{
			// When button clicked then use this panel as the source
			event.setSource(this);
		}
		
		// Forward action events to listeners
		for (ActionListener listener : m_listActionListeners)
			listener.actionPerformed(event);
	}

	/**
	 * ID needed to save swing
	 */
	private static final long serialVersionUID = -7477466278904520075L;

	public static void main(String[] args) 
	{
		D6DiceOpponentChange opponent = new D6DiceOpponentChange();
		D6DiceOpponentChange[] opponents = {opponent, opponent, opponent};

		D6DiceResultChange data = new D6DiceResultChange(opponents.length);
		
		D6DiceView view = new D6DiceView();
		view.createPanel(opponents, true, false, false, data);
		D6DiceHelper.frameView("Fill in opponents and simulate", view);
	}
}
