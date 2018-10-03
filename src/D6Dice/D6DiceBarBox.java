package D6Dice;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

class D6DiceBarBox extends JPanel implements D6DiceResultEvent 
{
	private D6DiceResultModel m_data;
	private Color[] m_acolorBar;
	private Color m_colorOutline;
	private Font m_fontDialog;
	
	public D6DiceBarBox()
	{
		m_fontDialog = new Font("Dialog", Font.BOLD, 14);
	}
	
	JPanel createPanel(D6DiceResultChange dataObserve)
	{
		setLayout(new BorderLayout());
		Border borderSpace = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		setBorder(borderSpace);
		
		m_data = dataObserve;
		
		Color colorForeground = getForeground();
		setColours(colorForeground);
		
		dataObserve.listenEvent(this);
		changedData(m_data);
		
		return this;
	}
	
	@Override
	public void setForeground(Color colorForeground)
	{
		if (null!=m_data) setColours(colorForeground);
		super.setForeground(colorForeground);
		return;
	}
		
	private void setColours(Color colorForeground)
	{
		int iNumber = m_data.getNumber();
		m_acolorBar = new Color[iNumber];
		colorForeground = colorForeground.darker();
		//colorForeground = darker(colorForeground);

		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			m_acolorBar[iIndex] = colorForeground;
			colorForeground = colorForeground.brighter();
			//colorForeground = brighter(colorForeground);
		}
		
		m_colorOutline = colorForeground;
		
		return;
	}

	@Override
	public Dimension getPreferredSize() 
	{
		int iNumber = 1;
		if (null!=m_data) iNumber = m_data.getNumber();		
        return new Dimension(iNumber*200, 25);
    }
	
	@Override
	public Dimension getMinimumSize() 
	{
		int iNumber = 1;
		if (null!=m_data) iNumber = m_data.getNumber();		
        return new Dimension(iNumber*100, 20);
    }
	
	@Override
	public void changedData(D6DiceResultModel data) 
	{
		// Force repaint
		invalidate();
		repaint();
		return;
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
		Dimension dimensionCurrent = getSize();		
		drawOne((Graphics2D)g, 0, dimensionCurrent.width, dimensionCurrent.height);
		return;
	}
	
	private void drawOne(Graphics2D g2D, int iOffset, int iWidth, int iHeight)
	{
		int iNumber = m_data.getNumber();
		int iTotal = m_data.getTotal();
		
		g2D.setStroke(new BasicStroke(2.0f));
		g2D.setFont(m_fontDialog);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		if (0==iTotal)
		{
			g2D.setColor(getBackground());
			g2D.fillRect(0, 0, iWidth, iHeight);
			g2D.setColor(m_colorOutline);
			g2D.drawRect(0, 0, iWidth, iHeight);
			
			g2D.setColor(Color.WHITE);
			g2D.drawString("<Unset>", iWidth, iHeight);
			
			return;
		}
		
		int iWidthOffset = 0;

		for (int iIndex = 0; iIndex<iNumber; iIndex++)
		{
			int iBar = m_data.getBar(iIndex);			
			int iWidthBar = iBar * iWidth / iTotal;
			
			g2D.setColor(m_acolorBar[iIndex]);
			g2D.fillRect(iWidthOffset, 0, iWidthBar, iHeight);
			g2D.setColor(m_colorOutline);
			g2D.drawRect(iWidthOffset, 0, iWidthBar, iHeight);
			
			g2D.setColor(Color.WHITE);
			float fBar = (float)iBar;
			float fPercent = 100 * fBar / iTotal;
			g2D.drawString(String.format("%3.3f", fPercent), iWidthOffset+7, iHeight-7);
			
			iWidthOffset += iWidthBar;
		}
		return;
	}
	
	/**
	 * ID for swing
	 */
	private static final long serialVersionUID = 4143804616257914120L;

	public static void main(String[] args) throws InterruptedException 
	{
		final int BARS = 4;
		D6DiceResultChange data = new D6DiceResultChange(BARS);
		
		D6DiceBarBox barbox = new D6DiceBarBoxPaint();
		barbox.createPanel(data);
		barbox.setForeground(new Color(0xE0, 0x80, 0x70));
		D6DiceHelper.frameView("Watch the values!", barbox);
		
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
				data.addBar(iBar, iChange, 0, 0);
			
			Thread.sleep(1000);
		}
	}
}

class D6DiceBarBoxPaint extends D6DiceBarBox 
{

	/**
	 * ID for Swing 
	 */
	private static final long serialVersionUID = 7753962629163041176L;
}
