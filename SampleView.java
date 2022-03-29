package etsappl.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import java.util.HashMap;
import javax.inject.Inject;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "etsappl.views.SampleView";

	@Inject IWorkbench workbench;
	
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	
	private HashMap<String, String> JSTLSyntax = new HashMap<String, String>();
	private HashMap<String, String> JSTLUrl= new HashMap<String, String>();

	private void initializeSyntaxData() {
		JSTLSyntax.put("[Short Hand IF...ELSE]","variable = (condition) ? expressionTrue :  expressionFalse;");
		JSTLSyntax.put("[Switch]", "switch(expression) {\n"
				+ "  case x:\n"
				+ "    // code block\n"
				+ "    break;\n"
				+ "  case y:\n"
				+ "    // code block\n"
				+ "    break;\n"
				+ "  default:\n"
				+ "    // code block\n"
				+ "}");
		JSTLSyntax.put("[While]", "while (condition) {\n"
				+ "  // code block to be executed\n"
				+ "}");
		JSTLSyntax.put("[For]", "for (statement 1; statement 2; statement 3) {\n"
				+ "  // code block to be executed\n"
				+ "}");
		JSTLSyntax.put("[ForEach]", "for (type variableName : arrayName) {\n"
				+ "  // code block to be executed\n"
				+ "}");
	}
	
	private void initializeUrlData() {
		JSTLUrl.put("[Short Hand IF...ELSE]", "https://www.w3schools.com/java/java_conditions_shorthand.asp");
		JSTLUrl.put("[Switch]", "https://www.w3schools.com/java/java_switch.asp");
		JSTLUrl.put("[While]", "https://www.w3schools.com/java/java_while_loop.asp");
		JSTLUrl.put("[For]", "https://www.w3schools.com/java/java_for_loop.asp");
		JSTLUrl.put("[ForEach]", "https://www.w3schools.com/java/java_foreach_loop.asp");
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		initializeSyntaxData();
		initializeUrlData();
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(new String[] { 
				"[Short Hand IF...ELSE]",
				"[Switch]",
				"[While]",
				"[For]",
				"[ForEach]"
			});
	viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "etsappl.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private String getSelectedItemString() {
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object obj = selection.getFirstElement();
		
		return obj.toString();
	}
	
	private void openBrowserLink(String url) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		    try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				String item = getSelectedItemString();
				openBrowserLink(JSTLUrl.get(item));
			}
		};
		action1.setText("Open Use Case");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				String item = getSelectedItemString();
				showMessage(item + " syntax is:\n\n" + JSTLSyntax.get(item));
			}
		};
		action2.setText("See Syntax");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				String item = getSelectedItemString();
				showMessage(item + " syntax is:\n\n" + JSTLSyntax.get(item));
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
