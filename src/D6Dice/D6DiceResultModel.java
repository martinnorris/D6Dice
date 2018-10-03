package D6Dice;

class D6DiceResultModel 
{
	D6DiceResultModel(int iNumber)
	{
		m_iNumber = iNumber;
		m_iTotalBars = 0;
		m_aiBars = new int[iNumber];

		m_afRounds = new float[iNumber];
		m_afWounds = new float[iNumber];
	}
	
	D6DiceResultModel(D6DiceResultModel dataSource)
	{
		m_iNumber = dataSource.m_iNumber;
		m_iTotalBars = dataSource.m_iTotalBars;
		m_aiBars = dataSource.m_aiBars;

		m_afRounds = dataSource.m_afRounds;
		m_afWounds = dataSource.m_afWounds;		
	}
	
	private int m_iNumber;
	private int m_iTotalBars;
	private int[] m_aiBars;

	private float[] m_afRounds;
	private float[] m_afWounds;

	int getNumber() {return m_iNumber;}
	int getTotal() {return m_iTotalBars;}
	int getBar(int iBar) {return m_aiBars[iBar];}
	
	float getRounds(int iBar) {return m_afRounds[iBar];}
	float getWounds(int iBar) {return m_afWounds[iBar];}
	
	int reset(int iNumber)
	{
		m_iNumber = iNumber;
		m_iTotalBars = 0;
		m_aiBars = new int[iNumber];

		m_afRounds = new float[iNumber];
		m_afWounds = new float[iNumber];
		
		notifyListeners();
		
		return m_iNumber;
	}
	
	int addBar(int iBar, int iValue, float fRounds, float fWounds)
	{
		m_iTotalBars += iValue;
		m_aiBars[iBar] += iValue;
		
		m_afRounds[iBar] = fRounds;
		m_afWounds[iBar] = fWounds;
		
		notifyListeners();
		
		return m_aiBars[iBar];
	}

	void notifyListeners()
	{
		return;
	}	
}
