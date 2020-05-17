import java.util.HashSet;
import java.util.Set;

public class Tile {
    private Set<Person> people = new HashSet<>();
    private final Location location;

    public Tile(Location location) {
        this.location = location;
    }

    public Set<Person> getPeople() {
        return new HashSet<>(people);
    }

    public boolean empty() {
        for (Person person : people) {
            if (person instanceof Cop || person instanceof Agent &&
                ((Agent) person).getJailTerm() == 0) {
                return false;
            }
        }
        return true;
    }

    public Location getLocation() {
        return location;
    }

    public void addPerson(Person person) {
        this.people.add(person);
        person.setTile(this);
    }

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
