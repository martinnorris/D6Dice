package D6Dice;

import java.util.ArrayList;
import java.util.List;

class D6DiceOpponentChange extends D6DiceOpponentModel 
{
	public D6DiceOpponentChange()
	{
		m_listListeners = new ArrayList<D6DiceOpponentEvent>();		
	}
	
	D6DiceOpponentChange(D6DiceOpponentModel dataSource)
	{
		super(dataSource);
		m_listListeners = new ArrayList<D6DiceOpponentEvent>();
	}
	
	private List<D6DiceOpponentEvent> m_listListeners;
	
	void listenEvent(D6DiceOpponentEvent eventListener)
	{
		m_listListeners.add(eventListener);
	}
	
	void ignoreEvent(D6DiceOpponentEvent eventListener)
	{
		m_listListeners.remove(eventListener);
	}
	
	@Override
	public void notifyListeners()
	{
		for (D6DiceOpponentEvent listener : m_listListeners)
			listener.changedOpponent(this);
		return;
	}	
}
