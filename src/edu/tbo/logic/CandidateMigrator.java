package edu.tbo.logic;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tbo.data.CandidateDAO;
import edu.tbo.data.DatabaseManager;
import edu.tbo.web.models.CandidateModel;

@Component
public class CandidateMigrator {
	
	private final int NUM_ROWS_PER_CONN = 1;
	private List<String> output = Collections.synchronizedList(new ArrayList<>());
	private List<MigratorThread> threads = new ArrayList<>();
	
	@Autowired
	DatabaseManager database;
	
	@Autowired
	CandidateDAO dao;
	
	public enum STATE {
		STOPPED,
		RUNNING
	}
	
	private STATE state = STATE.STOPPED;

	public STATE getState() {
		return state;
	}
	
	public int getNumLoops() {
		int total = 0;
		for(MigratorThread thread : threads) {
			total += thread.loops;
		}
		return total;
	}
	
	public int getNumMigrated() {
		int total = 0;
		for(MigratorThread thread : threads) {
			total += thread.migrated;
		}
		return total;
	}
	
	public String getHtmlOut() {
		List<String> limited = output.stream().sorted(Collections.reverseOrder()).limit(50).collect(Collectors.toList());
		return StringUtils.trimToEmpty(String.join("<br />", limited));
	}
	
	public void start() throws InterruptedException {
		if(this.state != STATE.RUNNING) {
			stop();
			threads = new ArrayList<>();
			for(int i = 0; i < 10; i++) {
				MigratorThread thread = new MigratorThread(i);
				thread.setDaemon(false);
				thread.start();
				threads.add(thread);
			}
		}
		checkThreads();
	}
	
	void checkThreads() {
		boolean isRunning = false;
		for(MigratorThread thread : threads) {
			if(thread.isAlive() && !thread.cancelled) {
				isRunning = true;
			}
		}
		this.state = isRunning ? STATE.RUNNING : STATE.STOPPED;
	}
	
	public void stop() throws InterruptedException {
		checkThreads();
		if(this.state != STATE.STOPPED) {
			for(MigratorThread thread : threads) {
				thread.cancelled = true;
				thread.join(5000);
			}
		}
	}
	
	private class MigratorThread extends Thread {
		
		boolean cancelled;
		int clause;
		int loops;
		int migrated;
		
		MigratorThread(int clause) {
			this.setName("outlaw-candidate-migrator");
			this.cancelled = false;
			this.clause = clause;
			this.loops = 0;
			this.migrated = 0;
		}
		
		@Override
		public void run() {
			output.add(LocalDateTime.now() + " :: Starting Migrator["+clause+"]");
			checkThreads();
			
			try {
				while(!cancelled && !this.isInterrupted()) {
					Connection conn = null;
					try {
						conn = database.getConnection();
						
						List<CandidateModel> toCheck = dao.findCandidates(clause, NUM_ROWS_PER_CONN, conn);
						List<CandidateModel> toMigrate = new ArrayList<>();
						
						if(toCheck.isEmpty()) {
							this.cancelled = true;
							output.add(LocalDateTime.now() + " :: Migrator["+clause+"] nothing left to migrate");
						}
						
						for(CandidateModel model : toCheck) {
							if(true) {
								model.setCriteria("Uninhabited Gas Giant");
								toMigrate.add(model);
								
								this.migrated++;
								output.add(LocalDateTime.now() + " :: " + model.getDisplayName() + "[" + model.getBodyId() + "]");
							}
						}
						
						if(toMigrate.size() > 0) {
							dao.addCandidate(toMigrate, conn);
						}
					}
					finally {
						if (conn != null) conn.close();
					}
					
					this.loops++;
					Thread.sleep(1000);
				}
			}
			catch(Throwable t) {
				output.add(LocalDateTime.now() + " :: " + t);
			}
			finally {
				output.add(LocalDateTime.now() + " :: Stopping Migrator["+clause+"]");
				checkThreads();
			}
		}
	}
}
