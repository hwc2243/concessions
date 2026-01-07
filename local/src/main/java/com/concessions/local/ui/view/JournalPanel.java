package com.concessions.local.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.concessions.dto.StatusType;
import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.ui.CurrencyRenderer;
import com.concessions.local.ui.JournalNotifier.JournalListener;
import com.concessions.local.ui.LocalDateTimeRenderer;
import com.concessions.local.ui.controller.JournalController;

public class JournalPanel extends JPanel implements JournalListener {

	private JournalController controller;
    private final JournalTableModel tableModel;
    private final JPopupMenu popupMenu;
	private final JTable journalTable;
    
	public JournalPanel (JournalController controller) {
		this(controller, new ArrayList<>());
	}

	public JournalPanel (JournalController controller, List<JournalDTO> journals) {
		this.controller = controller;
		//controller.addJournalListener(this);
		
		setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize the table model
        this.tableModel = new JournalTableModel(journals);
        this.journalTable = new JTable(tableModel);
        
     // Apply custom renderers for better data visualization
        journalTable.setDefaultRenderer(LocalDateTime.class, new LocalDateTimeRenderer());
        journalTable.setDefaultRenderer(BigDecimal.class, new CurrencyRenderer());
        
        // Center the header text
        ((DefaultTableCellRenderer)journalTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Make the table look nicer
        journalTable.setRowHeight(25);
        journalTable.setFillsViewportHeight(true);
        journalTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        // Initialize and attach the popup menu
        this.popupMenu = createPopupMenu();
        this.setComponentPopupMenu(popupMenu);
        attachPopupMenuListener();

        // Add the table to a JScrollPane for scrolling and headers
        JScrollPane scrollPane = new JScrollPane(journalTable);
        
        // Add a title
        JLabel titleLabel = new JLabel("Journal History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
	}
	
	private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem viewDetailsItem = new JMenuItem("View Orders");
        viewDetailsItem.addActionListener(e -> {
        	JournalDTO journal = getSelectedJournal();
        	if (journal != null) {
        		controller.viewOrders(journal);
        	}
        });
        menu.add(viewDetailsItem);
        
        JMenuItem openJournalItem = new JMenuItem("Open");
        openJournalItem.addActionListener(e -> {
        	JournalDTO journal = getSelectedJournal();
        	if (journal != null) {
        		controller.open(journal);
        	}
        });
        menu.add(openJournalItem);
        
        JMenuItem suspendJournalItem = new JMenuItem("Suspend");
        suspendJournalItem.addActionListener(e -> {
        	JournalDTO journal = getSelectedJournal();
        	if (journal != null) {
        		controller.suspend(journal);
        	}
        });
        menu.add(suspendJournalItem);
        
        JMenuItem closeJournalItem = new JMenuItem("Close");
        closeJournalItem.addActionListener(e -> {
        	JournalDTO journal = getSelectedJournal();
        	if (journal != null) {
        		controller.close(journal);
        	}
        	// tableModel.setJournalAt(journalTable.getSelectedRow(), journal);
        });
        menu.add(closeJournalItem);
        
        JMenuItem syncJournalItem = new JMenuItem("Sync");
        syncJournalItem.addActionListener(e -> {
        	JournalDTO journal = getSelectedJournal();
        	if (journal != null) {
        		controller.sync(journal);
        	}
        });
        menu.add(syncJournalItem);
        
        return menu;
	}
	
	private void attachPopupMenuListener() {
        journalTable.addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed (MouseEvent e) {
            	showPopup(e);
            }
        	
            @Override
            public void mouseReleased(MouseEvent e) {
            	showPopup(e);
            }
        });
    }
	
	private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int r = journalTable.rowAtPoint(e.getPoint());
            if (r >= 0 && r < journalTable.getRowCount()) {
                // Select the row
                journalTable.setRowSelectionInterval(r, r);
                
                // Convert view row to model row index
                int modelRow = journalTable.convertRowIndexToModel(r);
                JournalDTO journal = tableModel.getJournalAt(modelRow);
                
                // Dynamically enable/disable "Close Journal"
                for (Component comp : popupMenu.getComponents()) {
                    if (comp instanceof JMenuItem menuItem) {
                        if (menuItem.getText().equals("Open")) {
                            boolean canOpen = journal.getStatus() == StatusType.NEW || journal.getStatus() == StatusType.SUSPEND; 
                        	menuItem.setEnabled(canOpen);
                        } else if (menuItem.getText().equals("Close")) {
                            boolean canClose = journal.getStatus() == StatusType.OPEN || journal.getStatus() == StatusType.SUSPEND; 
                            menuItem.setEnabled(canClose);
                        } else if (menuItem.getText().equals("Suspend")) {
                        	boolean canSuspend = journal.getStatus() == StatusType.OPEN;
                        	menuItem.setEnabled(canSuspend);
                        } else if (menuItem.getText().equals("Sync")) {
                        	boolean canSync = journal.getStatus() == StatusType.CLOSE;
                        	menuItem.setEnabled(canSync);
                        }
                    }
                }
                
                // Show the menu
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            } else {
                // If click is outside table rows, clear selection (optional, but good UX)
                journalTable.clearSelection();
            }
        }
    }
	
	private JournalDTO getSelectedJournal () {
		JournalDTO journal = null;
		int selectedRow = journalTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = journalTable.convertRowIndexToModel(selectedRow);
            journal = tableModel.getJournalAt(modelRow);
        }
        return journal;
	}
	
	class JournalTableModel extends AbstractTableModel {
	    private final List<JournalDTO> data;
	    private final String[] columnNames = {
	        "Status", "Order Count", "Sales Total", "Start Time", "End Time", "Sync Time"
	    };

	    public JournalTableModel(List<JournalDTO> data) {
	        this.data = data;
	    }

	    @Override
	    public int getRowCount() {
	        return data.size();
	    }

	    @Override
	    public int getColumnCount() {
	        return columnNames.length;
	    }

	    @Override
	    public String getColumnName(int columnIndex) {
	        return columnNames[columnIndex];
	    }

	    @Override
	    public Object getValueAt(int rowIndex, int columnIndex) {
	        JournalDTO journal = data.get(rowIndex);
	        return switch (columnIndex) {
	        	case 0 -> journal.getStatus();
	        	case 1 -> journal.getOrderCount();
	        	case 2 -> journal.getSalesTotal();
	            case 3 -> journal.getStartTs();
	            case 4 -> journal.getEndTs();
	            case 5 -> journal.getSyncTs();
	            default -> null;
	        };
	    }

	    @Override
	    public Class<?> getColumnClass(int columnIndex) {
	        return switch (columnIndex) {
	            case 0 -> StatusType.class; // Status Type
	            case 1 -> Long.class; // Order count
	            case 2 -> BigDecimal.class; // Sales Total
	            case 3, 4, 5 -> LocalDateTime.class; // Timestamps
	            default -> Object.class;
	        };
	    }
	    
	    public JournalDTO getJournalAt (int rowIndex) {
	    	return data.get(rowIndex);
	    }

	    public void journalAdded (JournalDTO journal) {
	    	data.add(journal);
	    	
	    	this.fireTableRowsInserted(data.size() - 1, data.size() - 1);
	    }
	    
	    public void journalUpdated (JournalDTO journal) {
	    	int rowIndex = data.indexOf(journal);
	    	if (rowIndex >= 0) {
	    		data.set(rowIndex, journal);
	    		fireTableRowsUpdated(rowIndex, rowIndex);
	    	}
	    }
	}

	@Override
	public void journalClosed (JournalDTO journal) {
		tableModel.journalUpdated(journal);
		
	}

	@Override
	public void journalChanged (JournalDTO journal) {
		tableModel.journalUpdated(journal);
	}
	
	@Override
	public void journalOpened(JournalDTO journal) {
		tableModel.journalUpdated(journal);
	}

	@Override
	public void journalStarted(JournalDTO journal) {
		tableModel.journalAdded(journal);
	}

	@Override
	public void journalSuspended(JournalDTO journal) {
		tableModel.journalUpdated(journal);
	}

	@Override
	public void journalSynced(JournalDTO journal) {
		tableModel.journalUpdated(journal);
	}
}
