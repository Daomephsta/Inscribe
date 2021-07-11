package io.github.daomephsta.mosaic.test.manual.flow;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.ThreadLocalRandom;

import io.github.daomephsta.mosaic.MosaicWidget;

public class TestWidget extends MosaicWidget implements Paintable
{
	private Color debugFill;

	public TestWidget()
	{
		float h = ThreadLocalRandom.current().nextFloat();
		float s = 1.0F;
		float b = 1.0F;
		this.debugFill = Color.getHSBColor(h, s, b);
	}

	@Override
	public void paint(Graphics g)
	{
		g.setColor(debugFill);
		g.fillRect(x(), y(), width(), height());
		g.setColor(Color.WHITE);
		g.drawRect(x(), y(), width() - 1, height() - 1);
	}

	public TestWidget setDebugFill(Color debugFill)
	{
		this.debugFill = debugFill;
		return this;
	}
}
