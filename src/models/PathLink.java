package models;

import java.util.Objects;

public class PathLink{
	private int fromID;
	private int toID;

	public PathLink(int fromID, int toID) {
		this.fromID = fromID;
		this.toID = toID;
	}

	public int getFromID() {
		return fromID;
	}

	public void setFromID(int fromID) {
		this.fromID = fromID;
	}

	public int getToID() {
		return toID;
	}

	public void setToID(int toID) {
		this.toID = toID;
	}

	@Override
	public String toString(){
		return "from: " + fromID + " to: " + toID;
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof PathLink)) {
            return false;
        }
        PathLink pathLink = (PathLink) o;
        return fromID == pathLink.fromID && toID == pathLink.toID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromID, toID);
    }
}
