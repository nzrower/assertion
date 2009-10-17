package com.googlecode.assertion.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import com.googlecode.assertion.Message;
import com.googlecode.assertion.MessageArchive;
import com.googlecode.assertion.utility.Utility;

public class SequenceDiagram extends Composite implements PaintListener, MouseListener, SelectionListener {

	private static final int P4 = 4;
	private static final int P10 = 10;
	private static final int P12 = 12;
	private static final int P20 = 20;
	private static final int P30 = 30;
	private static final int P40 = 40;
	private static final int P80 = 80;
	private static final int P100 = 100;

	private MessageArchive messageArchive;

	private final Listener listener;

	private Message selection = null;

	private final Map<Message, Rectangle> boundingMap = new HashMap<Message, Rectangle>();

	private int pageSize = 0;

	public SequenceDiagram(final Composite parent, final int style, final Listener listener) {
		super(parent, style);
		this.listener = listener;
		addPaintListener(this);
		addMouseListener(this);
		getVerticalBar().addSelectionListener(this);
	}

	public void setMessageCount(final int size) {
		final ScrollBar verticalBar = getVerticalBar();
		verticalBar.setIncrement(1);
		verticalBar.setMinimum(0);
		verticalBar.setMaximum(size);
	}

	public Listener getListener() {
		return listener;
	}

	public void setMessageArchive(final MessageArchive messageArchive) {
		this.messageArchive = messageArchive;
	}

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public void paintControl(final PaintEvent event) {
		boundingMap.clear();
		final GC gc = event.gc;
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setTextAntialias(SWT.ON);
		final Font font = new Font(gc.getDevice(), "Courier", 9, SWT.NONE);
		gc.setFont(font);
		final Color black = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		final Color green = gc.getDevice().getSystemColor(SWT.COLOR_GREEN);
		gc.setBackground(black);
		final Rectangle clientArea = getClientArea();
		gc.fillRectangle(clientArea);
		gc.setForeground(green);
		gc.drawRectangle(0, 0, clientArea.width - 1, clientArea.height - 1);

		int yOffset = SequenceDiagram.P20;

		final String range = String.format("%d messages", messageArchive.getMessageCount());
		final Point rangeExtent = gc.textExtent(range);
		gc.drawText(range, clientArea.width - SequenceDiagram.P20 - rangeExtent.x, SequenceDiagram.P20 + rangeExtent.y);
		//
		// Draw the lifeline label and the lifeline running from top to bottom
		//
		for (int i = 1; i < 7; i++) {
			final String label = String.format("#%s", i);
			final Point extent = gc.textExtent(label);
			final int x = SequenceDiagram.P100 + i * SequenceDiagram.P100;
			final int halfWidth = extent.x / 2;
			gc.drawText(label, x - halfWidth, yOffset);
			gc.drawRectangle(x - halfWidth - SequenceDiagram.P4, yOffset - SequenceDiagram.P4, extent.x
					+ SequenceDiagram.P4 + SequenceDiagram.P4, extent.y + SequenceDiagram.P4 + SequenceDiagram.P4);
			gc.drawLine(x, yOffset + extent.y + SequenceDiagram.P4, x, clientArea.height - SequenceDiagram.P10);
		}

		yOffset += SequenceDiagram.P30;

		//
		// Draw each message left/right with arrows
		//
		final Iterator<Message> iterator = messageArchive.getIterator(getVerticalBar().getSelection());

		long lastTime = 0L;
		int displayCount = 0;
		while (iterator.hasNext()) {
			displayCount++;
			final Message message = iterator.next();
			final int x1 = SequenceDiagram.P100 + message.getSource() * SequenceDiagram.P100;
			final int x2 = SequenceDiagram.P100 + message.getTarget() * SequenceDiagram.P100;
			yOffset += SequenceDiagram.P10;

			final String time;
			if (lastTime == 0) {
				lastTime = message.getDate().getTime();
				time = Utility.LONG_DATE_TIME.get().format(message.getDate());
			} else {
				final long elapsed = message.getDate().getTime() - lastTime;
				lastTime = message.getDate().getTime();
				time = "+" + Utility.NUMBER.get().format(elapsed);
			}
			final Point timeExtent = gc.textExtent(time);
			gc.drawText(time, SequenceDiagram.P100 + SequenceDiagram.P80 - timeExtent.x, yOffset - timeExtent.y / 2);

			final Rectangle rectangle = new Rectangle(clientArea.x, yOffset - SequenceDiagram.P12, clientArea.width,
					SequenceDiagram.P12 + SequenceDiagram.P12 + SequenceDiagram.P4);
			if (selection != null && selection.equals(message)) {
				gc.setAlpha(32);
				gc.setBackground(green);
				gc.fillRectangle(rectangle);
				gc.setBackground(black);
				gc.setAlpha(255);
			}
			boundingMap.put(message, rectangle);

			gc.drawLine(x1, yOffset, x2, yOffset);
			final String displayName = message.getDisplayName();
			if (message.getSource() < message.getTarget()) {
				gc.drawLine(x2 - SequenceDiagram.P10, yOffset - SequenceDiagram.P4, x2, yOffset);
				gc.drawLine(x2 - SequenceDiagram.P10, yOffset + SequenceDiagram.P4, x2, yOffset);
				gc.drawLine(x2 - SequenceDiagram.P10, yOffset - SequenceDiagram.P4, x2 - SequenceDiagram.P10, yOffset
						+ SequenceDiagram.P4);
				gc.drawText(displayName, x1 + SequenceDiagram.P4, yOffset, true);
			} else {
				gc.drawLine(x2 + SequenceDiagram.P10, yOffset - SequenceDiagram.P4, x2, yOffset);
				gc.drawLine(x2 + SequenceDiagram.P10, yOffset + SequenceDiagram.P4, x2, yOffset);
				gc.drawLine(x2 + SequenceDiagram.P10, yOffset - SequenceDiagram.P4, x2 + SequenceDiagram.P10, yOffset
						+ SequenceDiagram.P4);
				final Point textExtent = gc.textExtent(displayName);
				gc.drawText(displayName, x1 - SequenceDiagram.P4 - textExtent.x, yOffset, true);
			}

			yOffset += SequenceDiagram.P20;
			if (yOffset + SequenceDiagram.P10 + SequenceDiagram.P20 >= clientArea.height - SequenceDiagram.P10) {
				break;
			}
		}
		pageSize = displayCount;

		final String pageSizeString = String.format("%d page size", pageSize);
		final Point pageSizeExtent = gc.textExtent(pageSizeString);
		gc.drawText(pageSizeString, clientArea.width - SequenceDiagram.P20 - pageSizeExtent.x, SequenceDiagram.P40
				+ pageSizeExtent.y);

		font.dispose(); // TODO
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		// ignored
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		// ignored
	}

	@Override
	public void mouseUp(final MouseEvent event) {
		final int x = event.x;
		final int y = event.y;
		for (final Map.Entry<Message, Rectangle> entry : boundingMap.entrySet()) {
			final Rectangle bounds = entry.getValue();
			if (bounds.contains(x, y)) {
				selection = entry.getKey();
				listener.updated();
				return;
			}
		}
	}

	public Message getSelection() {
		return selection;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		getVerticalBar().setMaximum(messageArchive.getMessageCount());
		listener.updated();
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		listener.updated();
	}

}
