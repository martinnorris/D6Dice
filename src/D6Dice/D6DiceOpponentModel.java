package D6Dice;

class D6DiceOpponentModel 
{
	static final int HEALTH = 0;
	static final int INIT = 1;
	static final int HIT = 2;
	static final int DAMAGE = 3;
	static final int ARMOUR = 4;
	static final int _ALL = 5;
	
	public D6DiceOpponentModel()
	{
	}
	
	protected int m_iHEALTH;
	protected int m_iINIT;
	protected int m_iHIT;
	protected int m_iDAMAGE;
	protected int m_iARMOUR;
	
	D6DiceOpponentModel(D6DiceOpponentModel dataSource)
	{
		int[] aiFields = {dataSource.m_iHEALTH, dataSource.m_iINIT, dataSource.m_iHIT, dataSource.m_iDAMAGE, dataSource.m_iARMOUR}; 
		create(aiFields);
	}
	
	void create(int[] aiFields)
	{
		m_iHEALTH = aiFields[HEALTH];
		m_iINIT = aiFields[INIT];
		m_iHIT = aiFields[HIT];
		m_iDAMAGE = aiFields[DAMAGE];
		m_iARMOUR = aiFields[ARMOUR];
	}
	
	public int getHealth() {return m_iHEALTH;}
	public int getInit() {return m_iINIT;}
	public int getHit() {return m_iHIT;}
	public int getDamage() {return m_iDAMAGE;}
	public int getArmour() {return m_iARMOUR;}
	
	public void setHealth(int iValue)
	{
		m_iHEALTH = iValue;
		notifyListeners();
	}
	
	public void setInit(int iValue)
	{
		m_iINIT = iValue;
		notifyListeners();
	}
	
	public void setHit(int iValue)
	{
		m_iHIT = iValue;
		notifyListeners();
	}
	
	public void setDamage(int iValue)
	{
		m_iDAMAGE = iValue;
		notifyListeners();
	}
	
	public void setArmour(int iValue)
	{
		m_iARMOUR = iValue;
		notifyListeners();
	}
	
	public void notifyListeners()
	{
		return;
	}
	
	@Override
	public String toString()
	{
		return String.format("[%d, %d, %d, %d, %d] ", m_iHEALTH, m_iINIT, m_iHIT, m_iDAMAGE, m_iARMOUR);
	}
}