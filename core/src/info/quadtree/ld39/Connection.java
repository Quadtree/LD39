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
		return (int) (o.retained * 10000 - this.retained * 10000);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Connection)
			return (int) (this.retained * 10000) == (int) (((Connection) (obj)).retained * 10000);
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Connection [retained=" + retained + ", sink=" + sink + ", source=" + source + "]";
	}

}
