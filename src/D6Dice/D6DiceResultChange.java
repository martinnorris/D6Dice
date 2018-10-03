package D6Dice;

import java.util.ArrayList;
import java.util.List;

class D6DiceResultChange extends D6DiceResultModel 
{
	D6DiceResultChange(D6DiceResultModel dataSource)
	{
		super(dataSource);
		m_listListeners = new ArrayList<D6DiceResultEvent>();
	}
	
	D6DiceResultChange(int iNumber)
	{
		super(iNumber);
		m_listListeners = new ArrayList<D6DiceResultEvent>();
	}
	
	private List<D6DiceResultEvent> m_listListeners;
	
	void listenEvent(D6DiceResultEvent eventListener)
	{
		m_listListeners.add(eventListener);
	}
	
	void ignoreEvent(D6DiceResultEvent eventListener)
	{
		m_listListeners.remove(eventListener);
	}
	
	@Override
	void notifyListeners()
	{
		for (D6DiceResultEvent listener : m_listListeners)
			listener.changedData(this);
		return;
	}	
}
