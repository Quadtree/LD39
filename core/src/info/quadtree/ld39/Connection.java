package info.quadtree.ld39;

public class Connection implements Comparable<Connection> {
	public final double retained;
	public final Building sink;
	public final Building source;

	public Connection(double retained, Building source, Building sink) {
		super();
		this.retained = retained;
		this.source = source;
		this.sink = sink;
	}

	@Override
	public int compareTo(Connection o) {
		return (int) (this.retained * 10000 - o.retained * 10000);
	}

	@Override
	public String toString() {
		return "Connection [retained=" + retained + ", sink=" + sink + ", source=" + source + "]";
	}

}
