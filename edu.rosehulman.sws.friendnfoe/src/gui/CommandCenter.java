/*
 * CommandCenter.java
 * Oct 30, 2012
 *
 * FriendnFoe Attacker
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.*;

import foe.BruteForceAttack;
import foe.DOSAttack;
import foe.DOSListener;
import foe.SynFloodAttack;
import friend.CrawlListener;
import friend.Crawler;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public class CommandCenter extends JFrame implements CrawlListener, DOSListener {
	private static final long serialVersionUID = -7306164544041589737L;
	
	///// GUI Stuffs /////
	private JTextField txtUrl;
	
	private JTextField txtThreadPool;
	private JTextField txtTaskPerSecond;
	
	private JButton butStartSynFlooding;
	private JButton butStopSynFlooding;
	private JLabel lblFloodSize;
	
	private JButton butCrawl;
	private JButton butClearCrawl;
	private JButton butStartBruteForce;
	private JButton butStopBruteForce;
	private JLabel lblServiceRate;
	
	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JTextArea txtExceptions;
	private JButton butClearExceptions;
	
	
	//////////////////// Non-GUI Stuffs ////////////////////
	private DOSAttack attacker;
	private Crawler crawler;
	
	private int synFloodSize;
	private double serviceRate;
	
	private String host;
	private int port;
	private String path;	
	private int threadPool;
	private int taskPerSecond;
	
	public CommandCenter() {
		super("Friend-n-Foe DOS Attack Application");
		JPanel contentPane = (JPanel)this.getContentPane();
		
		BoxLayout layout = new BoxLayout(contentPane, BoxLayout.PAGE_AXIS);
		contentPane.setLayout(layout);
		contentPane.add(this.getUrlPanel());
		contentPane.add(this.getSynCommandPanel());
		contentPane.add(this.getBruteForcePanel());
		contentPane.add(this.getExceptionsPanel());
		
		// Setup of listeners
		this.setupUrlPanelListeners();
		this.setupSynPanelListeners();
		this.setupBruteForceListeners();
		this.setupExceptionsListener();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/////////////////////// Code for Crawl and DOSAttack Listeners ///////////////////////
	@Override
	public void rateUpdated(Object source, String type, String value) {
		if(source instanceof SynFloodAttack) {
			try {
				this.synFloodSize = Integer.parseInt(value);
			}
			catch(Exception e){}
		}
		else if(source instanceof BruteForceAttack) {
			try {
				this.serviceRate = Double.parseDouble(value);
			}
			catch(Exception e){}
		}
	}

	@Override
	public void directoryAdded(Object source, String directory) {
		this.listModel.addElement(directory);
	}

	@Override
	public void exceptionDetected(Object source, Exception e) {
		this.showMessage(e.getMessage());
	}
	
	public void showMessage(String message) {
		this.txtExceptions.append(message + "\n");
		this.txtExceptions.setCaretPosition(this.txtExceptions.getDocument().getLength());		
	}
	
	//////////////////// GUI ActionListeners ////////////////////
	private class ServiceUpdater implements Runnable {
		private boolean stop;
		
		public ServiceUpdater() {
			stop = false;
		}
		
		public void stop() {
			this.stop = true;
		}
		
		@Override
		public void run() {
			while(!stop) {
				CommandCenter.this.lblFloodSize.setText("" + CommandCenter.this.synFloodSize);
				CommandCenter.this.lblServiceRate.setText("" + CommandCenter.this.serviceRate);
				try {
					Thread.sleep(1000);
				}
				catch(Exception e){}
			}
		}
	}
	
	private ServiceUpdater updater;
	public void startUpdater() {
		if(updater != null)
			return;
		
		updater = new ServiceUpdater();
		Thread worker = new Thread(updater);
		worker.start();
	}
	
	public void stopUpdater() {
		if(updater != null)
			updater.stop();
		this.lblFloodSize.setText("Unknown");
		this.lblServiceRate.setText("Unknown");
		updater = null;
	}

	//////////////////// GUI ActionListeners ////////////////////
	private void setupUrlPanelListeners() {
		this.txtTaskPerSecond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(attacker != null) {
					int taskPerSecond = -1;
					try {
						taskPerSecond = Integer.parseInt(txtTaskPerSecond.getText());
					}
					catch(Exception ex) {}
					
					if(taskPerSecond >= 0) {
						attacker.setTaskPerSecond(taskPerSecond);
					}
					else {
						JOptionPane.showMessageDialog(CommandCenter.this, "Invalid Input!", "Invalid Input! Please try again!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
	
	private boolean extractInputInfo() {
		try {
			URL url = new URL(txtUrl.getText());
			host = url.getHost();
			port = url.getPort();
			path = url.getPath();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(CommandCenter.this, "Invalid URL!", "Please enter correct URL and try again!", JOptionPane.ERROR_MESSAGE);					
			return false;				
		}
		

		int threadPool = -1;
		try {
			threadPool = Integer.parseInt(this.txtThreadPool.getText());
		}
		catch(Exception e) {}
		
		if(threadPool > 0) {
			this.threadPool = threadPool;
		}
		else {
			JOptionPane.showMessageDialog(CommandCenter.this, "Invalid Thread Pool Size!", "Please enter a correct thread pool size and try again!", JOptionPane.ERROR_MESSAGE);					
			return false;				
		}
		
		int taskPerSecond = -1;
		try {
			taskPerSecond = Integer.parseInt(this.txtTaskPerSecond.getText());
		}
		catch(Exception e) {}
		
		if(taskPerSecond > 0) {
			this.taskPerSecond = taskPerSecond;
		}
		else {
			JOptionPane.showMessageDialog(CommandCenter.this, "Invalid Task Per Second Value!", "Please enter a correct rate and try again!", JOptionPane.ERROR_MESSAGE);					
			return false;				
		}
		return true;
	}
	
	private void setupSynPanelListeners() {
		this.butStartSynFlooding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMessage("Commensing suicide misson ...");
				if(attacker != null) {
					JOptionPane.showMessageDialog(CommandCenter.this, "Another Attack In Progress!", "Please stop previous attack and try again!", JOptionPane.ERROR_MESSAGE);					
					return;
				}
				
				if(!extractInputInfo())
					return;

				Thread mainWorker = new Thread() {
					@Override
					public void run() {
						Object[] uris = CommandCenter.this.listModel.toArray();
						String[] strUris = new String[uris.length];
						for(int i = 0; i < uris.length; ++i) {
							strUris[i] = uris[i].toString();
						}
						attacker = new SynFloodAttack(host, port, strUris, threadPool, taskPerSecond);
						attacker.addDOSListener(CommandCenter.this);
						Thread worker = new Thread(attacker);
						worker.start();						
					}
				};
				mainWorker.start();				
				
				showMessage("Syn Flooding Attack Started!");
				startUpdater();
			}
		});
		
		this.butStopSynFlooding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(attacker instanceof SynFloodAttack) {
					attacker.stop();
					stopUpdater();
					attacker = null;
					JOptionPane.showMessageDialog(CommandCenter.this, "Warning - Resource Leak", "Please restart this application to release resources used in SYN flooding!", JOptionPane.WARNING_MESSAGE);					
					showMessage("Syn Flodding Attack Stopped!");
				}
				else {
					JOptionPane.showMessageDialog(CommandCenter.this, "Syn Flood Cannot Stop", "Another attack in progress. Syn flood attack is not running!", JOptionPane.ERROR_MESSAGE);					
				}
			}
		});
	}
	
	private void setupBruteForceListeners() {
		this.butCrawl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(crawler != null) {
					JOptionPane.showMessageDialog(CommandCenter.this, "Another Crawler in Progress!", "Another crawler is already running, please try again later!", JOptionPane.ERROR_MESSAGE);					
					return;
				}
				
				if(!extractInputInfo())
					return;
				
				crawler = new Crawler(host, port, path, threadPool);
				crawler.addCrawlListener(CommandCenter.this);
				Thread worker = new Thread() {
					@Override
					public void run() {
						try {
							crawler.crawl();
							crawler = null;
						}
						catch(Exception e) {
							CommandCenter.this.exceptionDetected(crawler, e);
						}
					}
				};
				worker.start();
				showMessage("Crawler Started!");
			}
		});
		
		this.butClearCrawl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommandCenter.this.listModel.clear();
			}
		});
		
		this.butStartBruteForce.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(attacker != null) {
					JOptionPane.showMessageDialog(CommandCenter.this, "Another Attack In Progress!", "Please stop previous attack and try again!", JOptionPane.ERROR_MESSAGE);					
					return;
				}
				
				if(!extractInputInfo())
					return;
				
				showMessage("Commensing Brute-Force Attack ...");

				Thread mainWorker = new Thread() {
					@Override
					public void run() {
						Object[] uris = CommandCenter.this.listModel.toArray();
						String[] strUris = new String[uris.length];
						for(int i = 0; i < uris.length; ++i) {
							strUris[i] = uris[i].toString();
						}
						attacker = new BruteForceAttack(host, port, strUris, threadPool, taskPerSecond);
						attacker.addDOSListener(CommandCenter.this);
						Thread worker = new Thread(attacker);
						worker.start();
						
					}
				};
				mainWorker.start();
				showMessage("Brute-Force Attack Started!");
				startUpdater();
			}
		});
		
		this.butStopBruteForce.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(attacker instanceof BruteForceAttack) {
					attacker.stop();
					stopUpdater();
					attacker = null;
					showMessage("Brute-Force Attack Stopped!");
				}
				else {
					JOptionPane.showMessageDialog(CommandCenter.this, "Brute-Force Attack Cannot Stop", "Another attack in progress. Brute-Force attack is not running!", JOptionPane.ERROR_MESSAGE);					
				}
			}
		});
	}
	
	private void setupExceptionsListener() {
		this.butClearExceptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtExceptions.setText("");
			}
		});
	}
	
	/////////////////////// Code for GUI ///////////////////////
	private JPanel getUrlPanel() {
		JPanel panel = new JPanel(new SpringLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Input Panel"));

		JLabel label = new JLabel("Enter Server URL");
		this.txtUrl = new JTextField("http://localhost:8080/");
		panel.add(label);
		panel.add(this.txtUrl);
		
		panel.add(new JLabel("Threadpool Size (Not used)"));
		this.txtThreadPool = new JTextField("" + Runtime.getRuntime().availableProcessors());
		panel.add(this.txtThreadPool);
		
		panel.add(new JLabel("Task/Second (Modifiable)"));
		this.txtTaskPerSecond = new JTextField("" + 1);
		this.txtTaskPerSecond.setToolTipText("You can update this value while running an attack. Change and hit return!");
		panel.add(this.txtTaskPerSecond);
		
		// Compact the grid
		SpringUtilities.makeCompactGrid(panel, 3, 2, 5, 5, 5, 5);
		return panel;
	}
	
	private JPanel getSynCommandPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createTitledBorder("SYN Attack Command Panel (Suicide Bombing Mission)"));
		
		this.butStartSynFlooding = new JButton("Start SYN Flooding Attack");
		this.butStopSynFlooding = new JButton("Stop SYN Flooding Attack");
		panel.add(this.butStartSynFlooding);
		panel.add(this.butStopSynFlooding);
		panel.add(new JLabel("Flood Size: "));
		this.lblFloodSize = new JLabel("Unknown");
		panel.add(this.lblFloodSize);

		return panel;
	}
	
	private JPanel getBruteForcePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Brute-Force Attack Panel"));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.butCrawl = new JButton("Start Crawler");
		this.butClearCrawl = new JButton("Clear Crawler URIs");
		this.butStartBruteForce = new JButton("Start Brute-Force Attack");
		this.butStopBruteForce = new JButton("Stop Brute-Force Attack");
		topPanel.add(this.butCrawl);
		topPanel.add(this.butClearCrawl);
		topPanel.add(this.butStartBruteForce);
		topPanel.add(this.butStopBruteForce);
		topPanel.add(new JLabel("Service Rate: "));
		this.lblServiceRate = new JLabel("Unknown");
		topPanel.add(this.lblServiceRate);
		
		panel.add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JLabel topLabel = new JLabel("Crawled URI");
		topLabel.setHorizontalTextPosition(JLabel.LEFT);
		centerPanel.add(topLabel, BorderLayout.NORTH);
		
		this.listModel = new DefaultListModel<String>();
		this.list = new JList<String>(this.listModel);
		this.list.setLayoutOrientation(JList.VERTICAL);
		this.list.setVisibleRowCount(-1);
		
		JScrollPane listPane = new JScrollPane(this.list);
		listPane.setPreferredSize(new Dimension(250, 100));
		
		centerPanel.add(listPane, BorderLayout.CENTER);
		panel.add(centerPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel getExceptionsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Exceptions Log Panel"));
		
		this.txtExceptions = new JTextArea();
		this.txtExceptions.setEditable(false);
		JScrollPane txtPane = new JScrollPane(this.txtExceptions);
		txtPane.setPreferredSize(new Dimension(250, 100));
		panel.add(txtPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		this.butClearExceptions = new JButton("Clear Exceptions");
		bottomPanel.add(this.butClearExceptions);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		return panel;
	}
	
	/////////////////////// Code for Entry Point ///////////////////////
	public static void main(String[] args) {
		CommandCenter cmdCenter = new CommandCenter();
		cmdCenter.pack();
		cmdCenter.setVisible(true);
	}
}
