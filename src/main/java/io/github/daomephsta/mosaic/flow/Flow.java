package io.github.daomephsta.mosaic.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import io.github.daomephsta.mosaic.MosaicWidget;
import io.github.daomephsta.mosaic.ParentWidget;

public class Flow<E extends MosaicWidget> extends MosaicWidget implements ParentWidget
{
    private final Direction direction;
    private final List<E> children = new ArrayList<>();
    private final Map<E, FlowLayoutData> childLayoutData = new HashMap<>();

    public Flow(Direction direction)
    {
        this.direction = direction;
    }

    public Flow<E> add(E mosaicWidget, Consumer<FlowLayoutData> layoutDataConfig)
    {
        FlowLayoutData layoutData = new FlowLayoutData();
        direction.applyHints(mosaicWidget, layoutData);
        layoutDataConfig.accept(layoutData);
        return add(mosaicWidget, layoutData);
    }

    public Flow<E> add(E mosaicWidget)
    {
        FlowLayoutData layoutData = new FlowLayoutData();
        direction.applyHints(mosaicWidget, layoutData);
        return add(mosaicWidget, layoutData);
    }

    public Flow<E> add(E mosaicWidget, FlowLayoutData layoutData)
    {
        children.add(mosaicWidget);
        childLayoutData.put(mosaicWidget, layoutData);
        return this;
    }

    public Iterable<E> getChildren()
    {
        return children;
    }

    @Override
    public void layoutChildren()
    {
        if (children.size() == 0)
            return;
        int availableSpace = direction.getAvailableSpace(this);
        int nextChildCoord = direction.getStartCoord(this);
        for (MosaicWidget child : children)
        {
            int childSize = childLayoutData.get(child).computeSize(availableSpace);
            nextChildCoord += direction.getLeadingMargin(child);
            direction.setLayoutParameters(child, direction.getFixedCoord(this), direction.getFixedDimension(this),
                nextChildCoord, childSize);
            nextChildCoord += childSize + direction.getTrailingMargin(child);
            if (child instanceof ParentWidget)
                ((ParentWidget) child).layoutChildren();
        }
    }

    @Override
    public int hintHeight()
    {
        return direction.hintHeight(children);
    }

    @Override
    public int hintWidth()
    {
        return direction.hintWidth(children);
    }

    public enum Direction
    {
        HORIZONTAL
        {
            @Override
            void setLayoutParameters(MosaicWidget widget, int fixedCoord, int fixedDimension,
                int variableCoord, int variableDimension)
            {
                widget.setLayoutParameters(variableCoord, fixedCoord, variableDimension, fixedDimension);
            }

            @Override
            int getAvailableSpace(Flow<?> flow)
            { return flow.width() - flow.padding().left() - flow.padding().right(); }

            @Override
            int getFixedCoord(Flow<?> flow)
            { return flow.y() + flow.padding().top(); }

            @Override
            int getFixedDimension(Flow<?> flow)
            { return flow.height() - flow.padding().top() - flow.padding().bottom(); }

            @Override
            int getStartCoord(Flow<?> flow)
            { return flow.x() + flow.padding().left(); }

            @Override
            int getLeadingMargin(MosaicWidget widget)
            { return widget.margin().left(); }

            @Override
            int getTrailingMargin(MosaicWidget widget)
            { return widget.margin().right(); }

            @Override
            int hintWidth(List<? extends MosaicWidget> children)
            { return children.stream().mapToInt(MosaicWidget::hintHeight).sum(); }

            @Override
            int hintHeight(List<? extends MosaicWidget> children)
            { return children.stream().mapToInt(MosaicWidget::hintHeight).max().orElse(0); }

            @Override
            void applyHints(MosaicWidget mosaicWidget, FlowLayoutData layoutData)
            { layoutData.setDefaultMinSize(mosaicWidget.hintWidth()); }
        },
        VERTICAL
        {
            @Override
            void setLayoutParameters(MosaicWidget widget, int fixedCoord, int fixedDimension,
                int variableCoord, int variableDimension)
            {
                widget.setLayoutParameters(fixedCoord, variableCoord, fixedDimension, variableDimension);
            }

            @Override
            int getAvailableSpace(Flow<?> flow)
            { return flow.height() - flow.padding().top() - flow.padding().bottom(); }

            @Override
            int getFixedCoord(Flow<?> flow)
            { return flow.x() + flow.padding().left(); }

            @Override
            int getFixedDimension(Flow<?> flow)
            { return flow.width() - flow.padding().left() - flow.padding().right(); }

            @Override
            int getStartCoord(Flow<?> flow)
            { return flow.y() + flow.padding().top(); }

            @Override
            int getLeadingMargin(MosaicWidget widget)
            { return widget.margin().top(); }

            @Override
            int getTrailingMargin(MosaicWidget widget)
            { return widget.margin().bottom(); }

            @Override
            int hintWidth(List<? extends MosaicWidget> children)
            { return children.stream().mapToInt(MosaicWidget::hintHeight).max().orElse(0); }

            @Override
            int hintHeight(List<? extends MosaicWidget> children)
            { return children.stream().mapToInt(MosaicWidget::hintHeight).sum(); }

            @Override
            void applyHints(MosaicWidget mosaicWidget, FlowLayoutData layoutData)
            { layoutData.setDefaultMinSize(mosaicWidget.hintHeight()); }
        };

        abstract void setLayoutParameters(MosaicWidget widget, int fixedCoord, int fixedDimension,
            int variableCoord, int variableDimension);
        abstract int getAvailableSpace(Flow<?> flow);
        abstract int getStartCoord(Flow<?> flow);
        abstract int getFixedCoord(Flow<?> flow);
        abstract int getFixedDimension(Flow<?> flow);
        abstract int getLeadingMargin(MosaicWidget widget);
        abstract int getTrailingMargin(MosaicWidget widget);
        abstract int hintWidth(List<? extends MosaicWidget> children);
        abstract int hintHeight(List<? extends MosaicWidget> children);
        abstract void applyHints(MosaicWidget mosaicWidget, FlowLayoutData layoutData);
    }
}
