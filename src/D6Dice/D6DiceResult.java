package D6Dice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class D6DiceResult extends JPanel implements D6DiceResultEvent
{
	private JPanel m_panelStats;	
	private JTextField[] m_atextboxRounds;
	private JTextField[] m_atextboxWounds;
	
	JPanel createPanel(D6DiceResultChange dataObserve)
	{
		setLayout(new BorderLayout());
		
		D6DiceBarBox barbox = new D6DiceBarBoxPaint();
		barbox.createPanel(dataObserve);
		
		int iNumber = dataObserve.getNumber();
		m_atextboxRounds = new JTextField[iNumber];
		m_atextboxWounds = new JTextField[iNumber];
		
		m_panelStats = new JPanel();		
		m_panelStats.setLayout(new BoxLayout(m_panelStats, BoxLayout.LINE_AXIS));
		
		Dimension dimensionText = new Dimension(100,50);
		Dimension dimensionSpacing = new Dimension(10,0);
		
		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			JPanel panelWounds = new JPanel();
			panelWounds.setLayout(new BorderLayout());
			panelWounds.add(new JLabel("Average wounds/round"), BorderLayout.NORTH);
			m_atextboxWounds[iIndex] = new JTextField("0");
			panelWounds.add(m_atextboxWounds[iIndex], BorderLayout.CENTER);
			panelWounds.setMaximumSize(dimensionText);
			
			JPanel panelRounds = new JPanel();
			panelRounds.setLayout(new BorderLayout());
			panelRounds.add(new JLabel("Average rounds"), BorderLayout.NORTH);
			m_atextboxRounds[iIndex] = new JTextField("0");
			panelRounds.add(m_atextboxRounds[iIndex], BorderLayout.CENTER);
			panelRounds.setMaximumSize(dimensionText);

			m_panelStats.add(Box.createRigidArea(dimensionSpacing));		
			m_panelStats.add(panelWounds);
			m_panelStats.add(Box.createRigidArea(dimensionSpacing));
			m_panelStats.add(panelRounds);
			m_panelStats.add(Box.createRigidArea(dimensionSpacing));
			m_panelStats.add(Box.createHorizontalGlue());
		}
		
		JPanel panelBar = new JPanel();
		panelBar.setLayout(new BorderLayout());
		panelBar.add(new JLabel("Winning %"), BorderLayout.NORTH);
		panelBar.add(barbox);
		
		add(panelBar, BorderLayout.NORTH);		
		add(m_panelStats, BorderLayout.CENTER);
		
		// Attach to data and update fields
		changedData(dataObserve);
		dataObserve.listenEvent(this);
		
		return this;
	}
	
	@Override
	public void setBackground(Color colourBackground)
	{
		//super.setBackground(colourBackground);
		setBackgroundAll(this, colourBackground);
		return;
	}
	
	private void setBackgroundAll(JComponent componentParent, Color colourBackground)
	{
		for (Component componentContained : componentParent.getComponents())
		{
			if (componentContained instanceof JComponent)
			{
				JComponent componentBackgound = (JComponent)componentContained;
				componentBackgound.setBackground(colourBackground);
				setBackgroundAll(componentBackgound, colourBackground);
			}
		}
	}
	
	@Override
	public void changedData(D6DiceResultModel data) 
	{
		int iNumber = data.getNumber();
		
		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			String scRounds = String.format("%3.3f", data.getRounds(iIndex));
			m_atextboxRounds[iIndex].setText(scRounds);
			String scWounds = String.format("%3.3f", data.getWounds(iIndex));
			m_atextboxWounds[iIndex].setText(scWounds);
		}
		
		return;
	}
	
	/**
	 * ID for Swing
	 */
	private static final long serialVersionUID = 8665090339985299409L;

	public static void main(String[] args) throws InterruptedException 
	{
		final int BARS = 3;
		D6DiceResultChange data = new D6DiceResultChange(BARS);
		
		D6DiceResult result = new D6DiceResult();
		result.createPanel(data);
		result.setBackground(new Color(0xEE, 0xEE, 0xFF));
		
		D6DiceHelper.frameView("Watch the values", result);
		
		Random random = new Random();
		int iChange = 1;
		
		while (true)
		{
			if (data.getTotal()>255) iChange = -1;
			if (data.getTotal()<2) iChange = 1;
				
			int iBar = random.nextInt(BARS);
			int iNow = data.getBar(iBar);
			
			if (0==iNow)
				data.addBar(iBar, 1, 0, 0);
			else
				data.addBar(iBar, iChange, iChange*random.nextInt(5), iChange*random.nextInt(3));
			
			Thread.sleep(1000);
		}
	}
}
