package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.meeting.DateTime;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.NameMeeting;
import seedu.address.model.meeting.PersonToMeet;
import seedu.address.model.meeting.PhoneNum;
import seedu.address.model.meeting.Place;
import seedu.address.model.meeting.ReadOnlyMeeting;

/**
 * JAXB-friendly version of the Meeting.
 */
public class XmlAdaptedMeeting {

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String place;
    @XmlElement(required = true)
    private String date;
    @XmlElement(required = true)
    private String personToMeet;
    @XmlElement(required = true)
    private String phoneNum;

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs an XmlAdaptedMeeting.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedMeeting() {}


    /**
     * Converts a given Meeting into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedMeeting
     */
    public XmlAdaptedMeeting(ReadOnlyMeeting source) {
        name = source.getName().fullName;
        place = source.getPlace().value;
        date = source.getDate().toString();
        personToMeet = source.getPersonName().toString();
        phoneNum = source.getPersonPhone().toString();
    }


    /**
     * Converts this jaxb-friendly adapted meeting object into the model's Meeting object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted meeting
     */
    public Meeting toModelType() throws IllegalValueException {

        final NameMeeting name = new NameMeeting(this.name);
        final DateTime dateTime = new DateTime(this.date);
        final Place place = new Place(this.place);
        final PersonToMeet personToMeet = new PersonToMeet(this.personToMeet);
        final PhoneNum phoneNum = new PhoneNum(this.phoneNum);

        return new Meeting(name, dateTime, place, personToMeet, phoneNum);
    }

}
