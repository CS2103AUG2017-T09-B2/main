package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a Meeting in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Meeting implements ReadOnlyMeeting {
    private ObjectProperty<NameMeeting> name;
    private ObjectProperty<DateTime> date;
    private ObjectProperty<Place> place;
    private ObjectProperty<PersonToMeet> personMeet;
    private ObjectProperty<PhoneNum> phoneMeet;

    public Meeting(NameMeeting name, DateTime date, Place place, PersonToMeet personMeet, PhoneNum phoneMeet) {
        requireAllNonNull(name, date, place);
        this.name = new SimpleObjectProperty<>(name);
        this.date = new SimpleObjectProperty<>(date);
        this.place = new SimpleObjectProperty<>(place);
        this.personMeet = new SimpleObjectProperty<>(personMeet);
        this.phoneMeet = new SimpleObjectProperty<>(phoneMeet);
        // protect internal tags from changes in the arg list
    }

    /**
     * Creates a copy of the given ReadOnlyMeeting.
     */
    public Meeting(ReadOnlyMeeting source) {
        this(source.getName(), source.getDate(), source.getPlace(), source.getPersonName(), source.getPersonPhone());
    }

    public void setName(NameMeeting name) {
        this.name.set(requireNonNull(name));
    }

    @Override
    public ObjectProperty<NameMeeting> nameProperty() {
        return name;
    }

    @Override
    public NameMeeting getName() {
        return name.get();
    }

    public void setDateTime(DateTime date) {
        this.date.set(requireNonNull(date));
    }

    @Override
    public ObjectProperty<DateTime> dateProperty() {
        return date;
    }

    @Override
    public DateTime getDate() {
        return date.get();
    }

    public LocalDateTime getActualDate(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return localDateTime;
    }

    public void setPlace(Place place) {
        this.place.set(requireNonNull(place));
    }

    @Override
    public ObjectProperty<Place> placeProperty() {
        return place;
    }

    @Override
    public Place getPlace() {
        return place.get();
    }

    public void setPersonName(PersonToMeet person) {
        this.personMeet.set(requireNonNull(person));
    }

    @Override
    public ObjectProperty<PersonToMeet> personMeetProperty() {
        return personMeet;
    }

    @Override
    public PersonToMeet getPersonName() {
        return personMeet.get();
    }

    public void setPhoneNum(PhoneNum num) {
        this.phoneMeet.set(requireNonNull(num));
    }

    @Override
    public ObjectProperty<PhoneNum> phoneMeetProperty() {
        return phoneMeet;
    }

    @Override
    public PhoneNum getPersonPhone() {
        return phoneMeet.get();
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyMeeting // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyMeeting) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, place);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}
