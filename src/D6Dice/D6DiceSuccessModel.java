package D6Dice;

class D6DiceSuccessModel
{
	private int m_iSuccesses;
	private float m_fChance;
	
	public D6DiceSuccessModel(int iSuccesses, float fChance)
	{
		m_iSuccesses = iSuccesses;
		m_fChance = fChance;
	}
	
	public int getSuccesses()
	{
		return m_iSuccesses;
	}
	
	public float getChance()
	{
		return m_fChance;
	}
	
	public D6DiceSuccessModel addSuccess(D6DiceSuccessModel dice)
	{
		return new D6DiceSuccessModel(m_iSuccesses + dice.m_iSuccesses, m_fChance * dice.m_fChance);
	}
	
	public D6DiceSuccessModel addSuccessTo(D6DiceSuccessModel dice)
	{
		int iSuccesses = dice.m_iSuccesses;
		if (0==m_iSuccesses) iSuccesses = 0;
		return new D6DiceSuccessModel(iSuccesses, m_fChance * dice.m_fChance);
	}
	
	public D6DiceSuccessModel addSuccessZero(D6DiceSuccessModel dice)
	{
		int iSuccesses = m_iSuccesses + dice.m_iSuccesses;
		if (0==m_iSuccesses) iSuccesses = 0;
		return new D6DiceSuccessModel(iSuccesses, m_fChance * dice.m_fChance);
	}
	
	public float addChance(float fChance)
	{
		m_fChance += fChance;
		return m_fChance;
	}
	
	public D6DiceSuccessModel reduceSuccess(D6DiceSuccessModel dice)
	{
		return new D6DiceSuccessModel(m_iSuccesses - dice.m_iSuccesses, m_fChance * dice.m_fChance);
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(m_iSuccesses));
		sb.append(":");
		sb.append(String.format("%3.3f", m_fChance));
		sb.append(" ");
		return sb.toString();
	}
}