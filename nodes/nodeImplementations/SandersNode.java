package projects.murari_sanders87.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Comparator;
import java.util.PriorityQueue;

import sinalgo.configuration.Configuration;
import projects.murari_sanders87.nodes.messages.InquireVote;
import projects.murari_sanders87.nodes.messages.Relinquish;
import projects.murari_sanders87.nodes.messages.RequestCS;
import projects.murari_sanders87.nodes.messages.ReleaseCS;
import projects.murari_sanders87.nodes.messages.RequestCSComparator;
import projects.murari_sanders87.nodes.messages.VoteYes;
import projects.murari_sanders87.nodes.timers.LeaveCS;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.statistics.Distribution;


public class SandersNode extends Node {
	public boolean in_CS = false;
	public boolean requested_CS = false;
	public int current_TS = 0;
	public int my_TS = 0;

	public int yes_votes = 0;
	public boolean relinquish = false;
	public boolean has_voted = false;
	public Node cand;
	public int cand_TS = 0;
	public boolean inquired = false;
	public PriorityQueue<RequestCS> deferredQ;
	public int sent_messages = 0;
	private boolean ja_entrou = false;

	@Override
	public void preStep() {
		if (!in_CS && !requested_CS && wantToEnterCS() && !ja_entrou) {
			tryToEnterInCS();
			ja_entrou = true;
		}
		relinquish = false;
	}

	private boolean wantToEnterCS() {
		String namespace = "Sander87/CS/Enter";
		Distribution dist;
		double value;
        try {
	        dist = Distribution.getDistributionFromConfigFile(namespace + "/Size");
	        value = dist.nextSample();
        } catch (CorruptConfigurationEntryException e) {
        	value = 0;
	        e.printStackTrace();
        }
		try {
	        return value <= Configuration.getDoubleParameter(namespace + "/MaxCSValue");
        } catch (CorruptConfigurationEntryException e) {
	        e.printStackTrace();
	        return false;
        }
	}
	
	private void tryToEnterInCS() {
		requested_CS = true;
		my_TS = current_TS;
		broadcastAndCount(new RequestCS(this, my_TS));
		updateColor();
	}
		
	@Override
	public void handleMessages(Inbox inbox) {
		while (inbox.hasNext()) {
			Message msg = inbox.next();
			if (msg instanceof RequestCS) {
				receivedVoteRequest((RequestCS) msg);
			} else if (msg instanceof VoteYes) {
				receivedYes();
			} else if (msg instanceof ReleaseCS) {
				receivedRelease((ReleaseCS) msg);
			} else if (msg instanceof InquireVote) {
				receivedInquireVote((InquireVote) msg);
			} else if (msg instanceof Relinquish) {
				receivedRelinquish((Relinquish) msg);
			}
			updateColor();
		}
	}
	
	private void receivedVoteRequest(RequestCS msg) {
		if (!has_voted) {
			sendYesVote(msg);
			return;
		}
		
		deferredQ.add(msg.clone());
		if (inquired) {
			return;
		}
		
		if (msg.ts < cand_TS || (cand_TS == msg.ts && msg.req.ID < cand.ID)) {
			sendAndCount(new InquireVote(this, cand_TS), cand);
			inquired = true;
		}
	}
	
	private void sendYesVote(RequestCS msg) {
		sendAndCount(new VoteYes(), msg.req);
		cand = msg.req; 
		cand_TS = msg.ts;
		has_voted = true;
	}
	
	private void receivedYes() {
		yes_votes++;
		if (yes_votes == outgoingConnections.size()) {
			in_CS = true;
			requested_CS = false;
			LeaveCS timer = new LeaveCS(); 
			timer.startRelative(timeToLeaveCS(), this);
		}
	}

	private double timeToLeaveCS() {
		String namespace = "Sander87/CS/Leave";
		Distribution dist;
        try {
	        dist = Distribution.getDistributionFromConfigFile(namespace + "/Duration");
	        return dist.nextSample();
        } catch (CorruptConfigurationEntryException e) {
	        e.printStackTrace();
	        return 3;
        }
	}

	public void releaseVotes() {
		Message msg = new ReleaseCS(this);
		broadcastAndCount(msg);
		yes_votes = 0;
		in_CS = false;
		requested_CS = false;
		updateColor();
	}
	
	private void receivedRelease(ReleaseCS msg) {
		releaseVote();
	}

	private void releaseVote() {
		RequestCS msg = deferredQ.poll();
		if (msg == null) {
			has_voted = false;
		} else {
			sendYesVote(msg); 			
		}
		inquired = false;
	}

	private void receivedInquireVote(InquireVote msg) {
		if (!in_CS && requested_CS) {
			sendAndCount(new Relinquish(this, my_TS), msg.req);
			relinquish = true;
			yes_votes--;
		}
	}
	
	private void receivedRelinquish(Relinquish msg) {
		RequestCS requestCS = new RequestCS(msg.req, msg.ts);
		deferredQ.add(requestCS);
		releaseVote();
	}

	private void sendAndCount(Message msg, Node to) {
		sent_messages++;
		send(msg, to);
	}

	private void broadcastAndCount(Message msg) {
		sent_messages += outgoingConnections.size();
		broadcast(msg);
	}

	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		String text;
		text = Integer.toString(this.ID) + " - " + Integer.toString(this.yes_votes);
		super.drawNodeAsSquareWithText(g, pt, highlight, text, 25, Color.BLACK);
	}

	@Override
	public void init() {
		Comparator<RequestCS> comparator = new RequestCSComparator();
		this.deferredQ = new PriorityQueue<RequestCS>(10, comparator);
		updateColor();
	}

	@Override
	public void postStep() {
		current_TS++;
	}

	private void updateColor() {
		if (in_CS) {
			setColor(Color.RED);
		} else
		if (inquired) {
			setColor(Color.PINK);
		} else 
		if (has_voted) {
			setColor(Color.GRAY);
		} else
		if (requested_CS) {
			setColor(Color.YELLOW);
		} else {
			setColor(Color.GREEN);
		}
	}

	@Override
	public String toString() {
		String s = "Node: " + this.ID + " ";
		s += " ts: " + my_TS + "/" + current_TS;
		s += " is_cs: " + in_CS;
		s += " inq: " + inquired;
		s += " requested_CS: " + requested_CS;
		
		if (has_voted) {
			s += " cand: " + cand.ID;
		} else {
			s += " votes: " + yes_votes;
		}
		return s;
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {}

	@Override
	public void neighborhoodChange() { }
}
