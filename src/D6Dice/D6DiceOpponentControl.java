package D6Dice;

import java.awt.event.ActionEvent;

class D6DiceOpponentControl extends ActionEvent
{
	private int[] m_aiFields;
	protected int m_iValue; // NO_UCD (use private) because events created in eventField then do not compile 
	
	public D6DiceOpponentControl(Object oSource, int iID, String scCommand, int[] aiFields)
	{
		super(oSource, iID, scCommand);
		m_aiFields = aiFields;
	}
	
	private D6DiceOpponentControl(Object oSource, int iID, String scCommand, int iValue)
	{
		super(oSource, iID, scCommand);
		m_iValue = iValue;
	}
	
	public void performAction()
	{
		D6DiceOpponentModel opponentData = (D6DiceOpponentModel)getSource();
		opponentData.create(m_aiFields);
		return;
	}
	
	@SuppressWarnings("serial")
	public static ActionEvent eventField(ActionEvent event, Object oSource, int iField, String scCommand, int iValue)
	{
		if (iField==D6DiceOpponentModel.HEALTH) return new D6DiceOpponentControl(oSource, iField, scCommand, iValue)
		{
			@Override
			public void performAction()
			{
				D6DiceOpponentModel dataOpponent = (D6DiceOpponentModel)getSource();
				dataOpponent.setHealth(m_iValue);
				return;
			}							
		};
		
		if (iField==D6DiceOpponentModel.INIT) return new D6DiceOpponentControl(oSource, iField, scCommand, iValue)
		{
			@Override
			public void performAction()
			{
				D6DiceOpponentModel dataOpponent = (D6DiceOpponentModel)getSource();
				dataOpponent.setInit(m_iValue);
				return;
			}							
		};
		
		if (iField==D6DiceOpponentModel.HIT) return new D6DiceOpponentControl(oSource, iField, scCommand, iValue)
		{
			@Override
			public void performAction()
			{
				D6DiceOpponentModel dataOpponent = (D6DiceOpponentModel)getSource();
				dataOpponent.setHit(m_iValue);
				return;
			}							
		};
		
		if (iField==D6DiceOpponentModel.DAMAGE) return new D6DiceOpponentControl(oSource, iField, scCommand, iValue)
		{
			@Override
			public void performAction()
			{
				D6DiceOpponentModel dataOpponent = (D6DiceOpponentModel)getSource();
				dataOpponent.setDamage(m_iValue);
				return;
			}							
		};
		
		if (iField==D6DiceOpponentModel.ARMOUR) return new D6DiceOpponentControl(oSource, iField, scCommand, iValue)
		{
			@Override
			public void performAction()
			{
				D6DiceOpponentModel dataOpponent = (D6DiceOpponentModel)getSource();
				dataOpponent.setArmour(m_iValue);
				return;
			}							
		};
		
		return event;
	}
	
	
	/**
	 * ID for Swing
	 */
	private static final long serialVersionUID = -5363100972121436395L;
}