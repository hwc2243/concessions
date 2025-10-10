package com.concessions.local.ui.controller;

import static com.concessions.local.ui.action.AbstractAction.CANCEL_COMMAND;
import static com.concessions.local.ui.action.AbstractAction.OK_COMMAND;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.Application;
import com.concessions.local.rest.LocationRestService;
import com.concessions.local.rest.MenuRestService;
import com.concessions.local.rest.OrganizationRestService;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.model.SetupModel;
import com.concessions.local.ui.view.SetupDialog;

@Component
public class SetupController {
	
	@Autowired
	protected OrganizationRestService organizationRestService;
	
	@Autowired
	protected LocationRestService locationService;
	
	@Autowired
	protected MenuRestService menuService;
	
	@Autowired
	private ApplicationFrame applicationFrame;
	private SetupModel model;
	private SetupDialog view;
	
	public SetupController () {
		initializeController();
	}

	private void initializeController() {
		model = new SetupModel();
		view = new SetupDialog(model);
		
		view.addOrganizationSelectionListener(e -> {
			try
			{
				Application.selectedOrganization = view.getSelectedOrganization();
				System.out.println("Selected organization: " + Application.selectedOrganization);
				model.setLocations(locationService.findAll());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(applicationFrame, "Error fetching locations: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);

			}
		});
		view.addLocationSelectionListener(e -> {
			try
			{
				model.setMenus(menuService.findAll());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(applicationFrame, "Error fetching menus: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);

			}
		});
		view.addMenuSelectionListener(e -> {
			// Enable the Finish button
			view.setSetupEnabled(true);
		});
		view.addActionListener(e -> {
			switch (e.getActionCommand()) {
			case OK_COMMAND:
				System.out.println("organization: " + view.getSelectedOrganization());
				System.out.println("location: " + view.getSelectedLocation());
				System.out.println("menu: " + view.getSelectedMenu());
				break;
			case CANCEL_COMMAND:
				break;
			}
			view.setVisible(false);
		});
		
		view.setSetupEnabled(false);
	}
	
	public void execute () {
		try
		{
			model.setOrganizations(organizationRestService.findAll());
		} catch (Exception ex) {
			// HWC TODO handle this
			ex.printStackTrace();
		}
		
		SwingUtilities.invokeLater(() -> {
			view.setLocationRelativeTo(applicationFrame);
			view.pack();
			view.setVisible(true);
		});
	}
}
