package projects.murari_sanders87.nodes.messages;

import java.util.Comparator;

public class RequestCSComparator implements
        Comparator<RequestCS> {

	@Override
    public int compare(RequestCS r1, RequestCS r2) {
		if (r1.ts < r2.ts) {
			return -1;
		} else if (r1.ts > r2.ts) {
			return 1;
		} else if (r1.req.ID < r2.req.ID) {
			return -1;
		} else if (r1.req.ID > r2.req.ID) {
			return 1;
		} else {
			return 0;
		}
    }
}
