package projects.murari_sanders87.nodes.timers;

import projects.murari_sanders87.nodes.nodeImplementations.SandersNode;
import sinalgo.nodes.timers.Timer;

public class LeaveCS extends Timer {
	
	@Override
	public void fire() {
		((SandersNode) this.node).releaseVotes();
	}
}
