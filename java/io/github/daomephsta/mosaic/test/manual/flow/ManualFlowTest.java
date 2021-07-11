package io.github.daomephsta.mosaic.test.manual.flow;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import io.github.daomephsta.mosaic.SizeConstraint;
import io.github.daomephsta.mosaic.flow.Flow;
import io.github.daomephsta.mosaic.flow.Flow.Direction;

public class ManualFlowTest
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(ManualFlowTest::createAndShowGUI);
	}

	private static void createAndShowGUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(600, 600));
		TestFlow rootFlow = createRootFlow();
		frame.add(new MosaicSwingAdapter(rootFlow));
		frame.pack();
		frame.setVisible(true);
	}

	private static TestFlow createRootFlow()
	{
	    TestFlow root = new TestFlow(Direction.VERTICAL);
	    Flow<?> row1 = new TestFlow(Direction.HORIZONTAL)
	        .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(33.33)))
	        .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(66.66)));
	    root.add(row1, d -> d.setPreferredSize(SizeConstraint.percentage(33.33)));
	    Flow<?> row2 = new TestFlow(Direction.HORIZONTAL)
            .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(66.66)))
            .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(33.33)));
	    root.add(row2, d -> d.setPreferredSize(SizeConstraint.percentage(33.33)));
	    Flow<?> row3 = new TestFlow(Direction.HORIZONTAL)
            .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(33.33)))
            .add(new TestWidget(), d -> d.setPreferredSize(SizeConstraint.percentage(66.66)));
	    root.add(row3, d -> d.setPreferredSize(SizeConstraint.percentage(33.33)));
        return root;
	}

	private static class MosaicSwingAdapter extends JComponent
	{
		private final TestFlow rootFlow;

		public MosaicSwingAdapter(TestFlow rootFlow)
		{
			this.rootFlow = rootFlow;
		}

		@Override
		public void paint(Graphics g)
		{
			rootFlow.paint(g);
		}

		@Override
		public void setBounds(int x, int y, int width, int height)
		{
			super.setBounds(x, y, width, height);
			rootFlow.setLayoutParameters(x, y, width, height);
			rootFlow.layoutChildren();
		}
	}
}
