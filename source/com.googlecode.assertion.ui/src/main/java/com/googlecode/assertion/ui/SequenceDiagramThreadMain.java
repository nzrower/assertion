package com.googlecode.assertion.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.googlecode.assertion.Message;
import com.googlecode.assertion.MessageArchive;
import com.googlecode.assertion.MessageArchive.Mode;
import com.googlecode.assertion.utility.BaseThread;
import com.googlecode.assertion.utility.Utility;

//TODO show range of messages in top right corner on ui
//TODO user interface tail - YAGNI
//TODO refactor / cleanup
//TODO unit tests / coverage
//TODO document
//TODO lifelines file
//TODO specification
//TODO try and break it
//TODO rename message to interaction?
//TODO store and show message sequence #

/**
 * {@link SequenceDiagramThreadMain} renders the contents of a Message Archive (.index and .data Files) as a Sequential
 * Interaction Diagram.
 */
public class SequenceDiagramThreadMain extends BaseThread implements Listener {

	/**
	 * The number of pixels margin to indent the main window.
	 */
	private static final int PADDING = 30;

	private Shell shell;

	private SequenceDiagram sequenceDiagram;

	private Text messageContents;

	private MessageArchive messages;

	// private TailThread tailThread;

	/**
	 * The index file name as passed in as argument to the process.
	 */
	private final String indexFileName;

	public static void main(final String[] args) {
		final SequenceDiagramThreadMain viewer = new SequenceDiagramThreadMain(Utility.join(args, ' '));
		viewer.start();
	}

	public SequenceDiagramThreadMain(final String indexFileName) {
		super(SequenceDiagramThreadMain.class.getName());
		this.indexFileName = indexFileName;
	}

	@Override
	public void mainLoop() {
		info("SWT Version = '%s'", SWT.getVersion());
		final Display display = new Display();
		shell = new Shell(display);
		messages = new MessageArchive(new File(indexFileName), Mode.READ);
		initUserInterface();
		// tailThread = new TailThread(messages, this);
		// tailThread.start();
		updateViewState();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		if (messages != null) {
			messages.close();
		}
	}

	private void initUserInterface() {
		shell.setText("Assertion Sequence Diagram");
		final Rectangle clientArea = shell.getDisplay().getPrimaryMonitor().getClientArea();
		shell.setLocation(clientArea.x + SequenceDiagramThreadMain.PADDING, clientArea.y
				+ SequenceDiagramThreadMain.PADDING);
		final int width = clientArea.width - clientArea.x - SequenceDiagramThreadMain.PADDING
				- SequenceDiagramThreadMain.PADDING;
		final int height = clientArea.height - clientArea.y - SequenceDiagramThreadMain.PADDING
				- SequenceDiagramThreadMain.PADDING;
		shell.setSize(width, height);

		shell.setLayout(new GridLayout(1, false));
		GridData gridData;

		sequenceDiagram = new SequenceDiagram(shell, SWT.V_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED, this);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		sequenceDiagram.setLayoutData(gridData);
		sequenceDiagram.setMessageArchive(messages);
		sequenceDiagram.setMessageCount(messages.getMessageCount());

		messageContents = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.heightHint = 200;
		messageContents.setLayoutData(gridData);
	}

	private void updateViewState() {
		sequenceDiagram.redraw();
		final Message selection = sequenceDiagram.getSelection();
		if (selection == null) {
			messageContents.setText("");
		} else {
			messageContents.setText(selection.toString());
		}
		// if (messages.getMessageCount() == sequenceDiagram.getVerticalBar().getSelection()
		// + sequenceDiagram.getPageSize()) {
		// // System.out.println("Scrolling on");
		// tailThread.setActive(true);
		// } else {
		// // System.out.println(String.format("Scrolling off %d %d", messages.getMessageCount(), sequenceDiagram
		// // .getVerticalBar().getSelection()));
		// tailThread.setActive(false);
		// }
	}

	@Override
	public void updated() {
		updateViewState();
	}

	@Override
	public void info(final String format, final Object... objects) {
		super.logToSystemOut(format, objects);
	}

}