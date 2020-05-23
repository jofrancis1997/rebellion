public class Location {
    private int xPos;
    private int yPos;

    /**
     * Initialise a location with an x and y position.
     *
     * @param xPos x position of the tile
     * @param yPos y position of the tile
     */
    public Location(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Retrieve the x position of the location.
     *
     * @return the x position of the location
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Retrieve the y position of the location.
     *
     * @return the y position of the location
     */
    public int getYPos() {
        return yPos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + xPos;
        result = prime * result + yPos;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (xPos != other.xPos)
            return false;
        if (yPos != other.yPos)
            return false;
        return true;
    }
}
