import java.util.HashSet;
import java.util.Set;

public class Tile {
    private Set<Person> people = new HashSet<>();
    private final Location location;

    /**
     * Initialise a tile.
     *
     * @param location the location of the tile.
     */
    public Tile(Location location) {
        this.location = location;
    }

    /**
     * Retrieve the people currently occupying the tile.
     *
     * @return the currently occupying the tile
     */
    public Set<Person> getPeople() {
        return new HashSet<>(people);
    }

    /**
     * Determine if the tile is empty.
     *
     * @return true if the tile is empty, false otherwise
     */
    public boolean empty() {
        for (Person person : people) {
            if (person instanceof Cop || person instanceof Agent &&
                ((Agent) person).getJailTerm() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieve the location of the tile.
     *
     * @return the location of the tile
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Add a person to the tile.
     *
     * @param person a person to add to the tile
     */
    public void addPerson(Person person) {
        this.people.add(person);
        person.setTile(this);
    }

    /**
     * Remove a person from the tile.
     *
     * @param person a person to remove from the tile
     */
    public void removePerson(Person person) {
        this.people.remove(person);
        person.setTile(null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result +
            ((location == null) ? 0 : location.hashCode());
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
        Tile other = (Tile) obj;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }
}
