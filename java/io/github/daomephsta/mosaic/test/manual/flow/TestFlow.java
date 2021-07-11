package io.github.daomephsta.mosaic.test.manual.flow;

import java.awt.Graphics;

import io.github.daomephsta.mosaic.MosaicWidget;
import io.github.daomephsta.mosaic.flow.Flow;

public class TestFlow extends Flow<MosaicWidget> implements Paintable
{
	public TestFlow(Direction direction)
	{
		super(direction);
	}

	@Override
	public void paint(Graphics g)
	{
		for (MosaicWidget child : getChildren())
		{
			if (child instanceof Paintable)
				((Paintable) child).paint(g);
			else
				throw new IllegalStateException("Cannot paint " + child);
		}
	}
}
