package edu.tbo.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tbo.data.CandidateDAO;
import edu.tbo.web.models.CandidateModel;

@Component
public class CandidateMigrator {
	
	private static List<String> output = new ArrayList<>();
	
	@Autowired
	CandidateDAO dao;
	
	MigratorThread thread;
	
	public enum STATE {
		STOPPED,
		RUNNING
	}
	
	private STATE state = STATE.STOPPED;

	public STATE getState() {
		return state;
	}
	
	public int getNumLoops() {
		if(thread != null) {
			return thread.loops;
		}
		
		return 0;
	}
	
	public String getHtmlOut() {
		return StringUtils.trimToEmpty(String.join("<br />", output));
	}
	
	public void start() {
		if(this.state != STATE.RUNNING) {
			thread = new MigratorThread();
			thread.setDaemon(false);
			thread.start();
		}
	}
	
	public void stop() throws InterruptedException {
		if(this.state != STATE.STOPPED && thread != null) {
			thread.cancelled = true;
			thread.interrupt();
			thread.join(5000);
		}
	}
	
	private class MigratorThread extends Thread {
		
		boolean cancelled = false;
		int loops = 0;
		
		MigratorThread() {
			this.setName("outlaw-candidate-migrator");
			this.cancelled = false;
			this.loops = 0;
		}
		
		@Override
		public void run() {
			output.add(LocalDateTime.now() + " :: Starting Migrator");
			state = STATE.RUNNING;
			
			try {
				while(!cancelled && !this.isInterrupted()) {
					List<CandidateModel> found = dao.findCandidates(1);
					for(CandidateModel model : found) {
						dao.addCandidate(model);
						output.add(LocalDateTime.now() + " :: " + model.getDisplayName() + "[" + model.getBodyId() + "]");
					}
					
					
					this.loops++;
					Thread.sleep(1000);
				}
			}
			catch(Throwable t) {
				output.add(LocalDateTime.now() + " :: " + t);
			}
			finally {
				output.add(LocalDateTime.now() + " :: Stopping Migrator");
				state = STATE.STOPPED;
			}
		}
	}
}
