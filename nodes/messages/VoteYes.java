package projects.murari_sanders87.nodes.messages;

import sinalgo.nodes.messages.Message;

public class VoteYes extends Message {

	public VoteYes() {
	}

	@Override
	public VoteYes clone() {
		return new VoteYes();
	}

}
