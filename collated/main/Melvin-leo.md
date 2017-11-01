# Melvin-leo
###### \java\seedu\address\logic\commands\AddMeetingCommand.java
``` java
    /**
     * Creates an AddMeetingCommand to add the specified {@code ReadOnlyMeeting}
     */
    public AddMeetingCommand (NameMeeting name, DateTime date, Place location, Index index) {
        this.index = index;
        this.name = name;
        this.date = date;
        this.location = location;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToAdd = lastShownList.get(index.getZeroBased());
        PersonToMeet personName = new PersonToMeet(personToAdd.getName().toString());
        PhoneNum phoneNum = new PhoneNum(personToAdd.getPhone().toString());

        toAdd = new Meeting(name, date, location, personName, phoneNum);
        try {
            model.addMeeting(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateMeetingException e) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        } catch (MeetingBeforeCurrDateException mde) {
            throw new CommandException(MESSAGE_OVERDUE_MEETING);
        } catch (MeetingClashException mce) {
            throw new CommandException(MESSAGE_MEETING_CLASH);
        }
    }
```
###### \java\seedu\address\logic\commands\ListMeetingCommand.java
``` java
/**
 * Lists all meetings in the address book to the user.
 */

public class ListMeetingCommand extends Command {

    public static final String COMMAND_WORD = "listmeeting";
    public static final String COMMAND_ALIAS = "lm";

    public static final String MESSAGE_SUCCESS = "Listed all meetings";


    @Override
    public CommandResult execute() {
        model.updateFilteredMeetingList(PREDICATE_SHOW_ALL_MEETINGS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### \java\seedu\address\model\AddressBook.java
``` java
    /**
     * Adds a meeting to the address book.
     * Also checks the new person's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the person to point to those in {@link #tags}.
     *
     * @throws DuplicateMeetingException if an equivalent meeting of the same date and time already exists.
     */
    public void addMeeting(ReadOnlyMeeting m) throws DuplicateMeetingException, MeetingBeforeCurrDateException,
            MeetingClashException {
        Meeting newMeeting = new Meeting(m);
        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime currDate = LocalDateTime.now();
        LocalDateTime meetDate = LocalDateTime.parse(newMeeting.getDate().toString(), formatter);
        if (meetDate.isAfter((currDate))) {
            meetings.add(newMeeting);
        } else {
            throw new MeetingBeforeCurrDateException();
        }
    }
```
###### \java\seedu\address\model\meeting\DateTime.java
``` java
/**
 * consist the date and time of an existing meeting in the address book.
 */
public class DateTime {

    public static final String MESSAGE_DATETIME_CONSTRAINTS =
            "Date and time should only contain numeric characters, colon and spaces, and it should not be blank."
                    + " Date and time should be an actual date and time, with the format dd-MM-yyyy";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String DATETIME_VALIDATION_REGEX = "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}";

    public final String value;

    /**
     * Validates given date and time.
     *
     * @throws IllegalValueException if given datetime string is invalid.
     */
    public DateTime(String date) throws IllegalValueException {
        requireNonNull(date);
        if (!isValidDateTime(date)) {
            throw new IllegalValueException(MESSAGE_DATETIME_CONSTRAINTS);
        }
        this.value = date;
    }

    /**
     * Returns true if a given string is a valid dateTime.
     */
    public static boolean isValidDateTime(String test) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(test, formatter);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof seedu.address.model.meeting.DateTime // instanceof handles nulls
                && this.value.equals(((seedu.address.model.meeting.DateTime) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
```
###### \java\seedu\address\model\meeting\exceptions\DuplicateMeetingException.java
``` java
/**
 * Signals that the operation will result in duplicate Meeting objects.
 */
public class DuplicateMeetingException extends DuplicateDataException {

    public DuplicateMeetingException() {
            super("Operation would result in duplicate meetings");
    }
}
```
###### \java\seedu\address\model\meeting\exceptions\MeetingBeforeCurrDateException.java
``` java
/**
 * Signals that the operation is unable to add meeting due to Date and time before log in time.
 */
public class MeetingBeforeCurrDateException extends Exception {

    public MeetingBeforeCurrDateException() {
        super("Operation would result in invalid meetings");
    }
}
```
###### \java\seedu\address\model\meeting\Meeting.java
``` java
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
```
###### \java\seedu\address\model\meeting\NameMeeting.java
``` java
/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class NameMeeting {

    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Meeting type should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String NAME_VALIDATION_REGEX = "[^\\s].*";

    public final String fullName;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public NameMeeting(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!isValidName(trimmedName)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.fullName = trimmedName;
    }

    /**
     * Returns true if a given string is a valid person name.
     */
    public static boolean isValidName(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NameMeeting // instanceof handles nulls
                && this.fullName.equals(((NameMeeting) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
```
###### \java\seedu\address\model\meeting\PersonToMeet.java
``` java
/**
 * Store the person who user is meeting in Meeting class
 */
public class PersonToMeet {
    public final String fullName;

    public PersonToMeet(String name) {
        this.fullName = name;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PersonToMeet // instanceof handles nulls
                && this.fullName.equals(((PersonToMeet) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
```
###### \java\seedu\address\model\meeting\PhoneNum.java
``` java
/**
 * Store phonenumber of Person so that user can easily contact him/her for meeting
 */
public class PhoneNum {
    public final String phone;

    public PhoneNum(String num) {
        this.phone = num;
    }

    @Override
    public String toString() {
        return phone;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PhoneNum // instanceof handles nulls
                && this.phone.equals(((PhoneNum) other).phone)); // state check
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
}
```
###### \java\seedu\address\model\meeting\Place.java
``` java
/**
 * Represents a Meeting's place in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Place {

    public static final String MESSAGE_ADDRESS_CONSTRAINTS =
            "Places can take any values, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String ADDRESS_VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Validates given address.
     *
     * @throws IllegalValueException if given address string is invalid.
     */
    public Place(String address) throws IllegalValueException {
        requireNonNull(address);
        if (!isValidAddress(address)) {
            throw new IllegalValueException(MESSAGE_ADDRESS_CONSTRAINTS);
        }
        this.value = address;
    }

    /**
     * Returns true if a given string is a valid person email.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(ADDRESS_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof seedu.address.model.meeting.Place // instanceof handles nulls
                && this.value.equals(((seedu.address.model.meeting.Place) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\meeting\ReadOnlyMeeting.java
``` java
/**
 * A read-only immutable interface for a Meeting in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyMeeting {
    ObjectProperty<NameMeeting> nameProperty();
    NameMeeting getName();
    ObjectProperty<DateTime> dateProperty();
    DateTime getDate();
    ObjectProperty<Place> placeProperty();
    Place getPlace();
    ObjectProperty<PersonToMeet> personMeetProperty();
    PersonToMeet getPersonName();
    ObjectProperty<PhoneNum> phoneMeetProperty();
    PhoneNum getPersonPhone();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyMeeting other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getDate().equals(this.getDate())
                && other.getPersonName().equals(this.getPersonName())
                && other.getPersonPhone().equals(this.getPersonPhone())
                && other.getPlace().equals(this.getPlace()));
    }

    /**
     * Formats the person as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append("\nMeeting with: ")
                .append(getPersonName())
                .append("\nContact Number: ")
                .append(getPersonPhone())
                .append("\nDate and Time: ")
                .append(getDate())
                .append("\nLocation: ")
                .append(getPlace());
        return builder.toString();
    }
}
```
###### \java\seedu\address\model\meeting\UniqueMeetingList.java
``` java
    /**
     * Adds a meeting to the list.
     *
     * @throws DuplicateMeetingException if the meeting to add is a duplicate of an existing meeting in the list.
     */
    public void add(ReadOnlyMeeting toAdd) throws DuplicateMeetingException, MeetingClashException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateMeetingException();
        } else if (diffNameOfMeeting(toAdd)) {
            throw new MeetingClashException();
        } else if (diffLocationOfMeeting(toAdd)) {
            throw new MeetingClashException();
        }
        internalMeetingList.add(new Meeting(toAdd));
        internalMeetingList.sort((m1, m2) -> m1.getActualDate(m1.getDate().toString())
                .compareTo(m2.getActualDate(m2.getDate().toString())));
    }

    /**
     * Replaces the meeting {@code target} in the list with {@code editedMeeting}.
     *
     * @throws DuplicateMeetingException if the replacement is equivalent to another existing meeting in the list.
     * @throws MeetingNotFoundException if {@code target} could not be found in the list.
     */
    public void setMeeting(ReadOnlyMeeting target, ReadOnlyMeeting editedMeeting)
            throws DuplicateMeetingException, MeetingNotFoundException, MeetingClashException {
        requireNonNull(editedMeeting);

        int index = internalMeetingList.indexOf(target);
        if (index == -1) {
            throw new MeetingNotFoundException();
        }
        if (!target.equals(editedMeeting) && internalMeetingList.contains(editedMeeting)) {
            throw new DuplicateMeetingException();
        } else if (diffNameOfMeeting(editedMeeting)) {
            throw new MeetingClashException();
        } else if (diffLocationOfMeeting(editedMeeting)) {
            throw new MeetingClashException();
        }

        internalMeetingList.set(index, new Meeting(editedMeeting));
        internalMeetingList.sort((m1, m2)-> m1.getActualDate(m1.getDate().toString())
                .compareTo(m2.getActualDate(m2.getDate().toString())));
    }
```
###### \java\seedu\address\storage\XmlAdaptedMeeting.java
``` java
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
```
###### \java\seedu\address\ui\MeetingAlert.java
``` java
/**
 * To have a pop up window to remind user about the meetings they have today
 */
public class MeetingAlert extends UiPart<Region> {

    private static final Logger logger = LogsCenter.getLogger(MeetingAlert.class);
    private static final String ICON = "/images/alert_icon.png";
    private static final String FXML = "MeetingAlert.fxml";
    private static final String TITLE = "REMINDER!!";
    private static final String MESSAGE = "Your Next Meeting is : ";

    private final Stage dialogStage;

    @FXML
    private Label warningMessage;

    @FXML
    private Label firstMeeting;

    @FXML
    private Label nameMeeting;


    public MeetingAlert(ObservableList<ReadOnlyMeeting> list) {
        super(FXML);
        Scene scene = new Scene(getRoot());
        //Null passed as the parent stage to make it non-modal.
        dialogStage = createDialogStage(TITLE, null, scene);
        dialogStage.setMaxHeight(600);
        dialogStage.setMaxWidth(1000);
        dialogStage.setX(475);
        dialogStage.setY(300);
        FxViewUtil.setStageIcon(dialogStage, ICON);
        warningMessage.setText(MESSAGE);
        if (isGroupMeeting(list)) {
            int indexDate = list.get(0).getDate().toString().indexOf(' ');
            firstMeeting.setText("Group Meeting with " + list.get(0).getPersonName().toString()
                    + " at " + list.get(0).getDate().toString().substring(indexDate + 1) + " for");
            nameMeeting.setText(list.get(0).getName().toString());
        } else {
            int indexDate = list.get(0).getDate().toString().indexOf(' ');
            firstMeeting.setText("Meeting with " + list.get(0).getPersonName().toString()
                    + " at " + list.get(0).getDate().toString().substring(indexDate + 1) + " for");
            nameMeeting.setText(list.get(0).getName().toString());
        }
    }
    /**
     * Shows the Alert window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine("Showing alert page about the application.");
        dialogStage.showAndWait();
    }
    /**
    *Get the number of individual meetings to be shown to user
     */
    private boolean isGroupMeeting(ObservableList<ReadOnlyMeeting> list) {
        int numMeet = 0;
        for (int i = 0; i < list.size(); i++) {
            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime currDate = LocalDateTime.now();
            LocalDateTime meetDate = LocalDateTime.parse(list.get(i).getDate().toString(), formatter);
            long daysBet = ChronoUnit.DAYS.between(currDate, meetDate);
            if (daysBet == 0) {
                int j = i + 1;
                while (list.get(i).getDate().equals(list.get(j).getDate())) {
                    numMeet++;
                    j++;
                }

            }
        }
        if (numMeet > 0) {
            return true;
        }
        return false;
    }
    @FXML
    private void handleExit() {
        dialogStage.close();
    }
}
```
###### \java\seedu\address\ui\MeetingCard.java
``` java
/**
 * An UI component that displays information of a {@code Meeting}.
 */
public class MeetingCard extends UiPart<Region> {

    private static final String FXML = "MeetingListCard.fxml";
    private static String[] colors = { "darkRed", "red", "orangeRed", "grey" };

    public final ReadOnlyMeeting meeting;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label date;
    @FXML
    private Label place;
    @FXML
    private Label person;
    @FXML
    private Label phoneNum;


    public MeetingCard(ReadOnlyMeeting meeting, int displayedIndex) {
        super(FXML);
        this.meeting = meeting;
        id.setText(displayedIndex + ". ");
        bindListeners(meeting);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Meeting} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyMeeting meeting) {
        name.textProperty().bind(Bindings.convert(meeting.nameProperty()));
        date.textProperty().bind(Bindings.convert(meeting.dateProperty()));
        place.textProperty().bind(Bindings.convert(meeting.placeProperty()));
        person.textProperty().bind(Bindings.convert(meeting.personMeetProperty()));
        phoneNum.textProperty().bind(Bindings.convert(meeting.phoneMeetProperty()));
        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime meetingDate = LocalDateTime.parse(meeting.getDate().toString(), formatter);
        LocalDateTime currDate = LocalDateTime.now();
        long daysBet = ChronoUnit.DAYS.between(currDate, meetingDate);
        if (daysBet == 0) {
            initMeeting(meeting, colors[0]);
        } else if (daysBet == 1) {
            initMeeting(meeting, colors[1]);
        } else if (daysBet == 2) {
            initMeeting(meeting, colors[2]);
        }

    }

    /**
     * set colours to Meeting
     * @param meeting
     */
    private void initMeeting(ReadOnlyMeeting meeting, String color) {
        cardPane.setStyle("-fx-background-color: " + color);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MeetingCard)) {
            return false;
        }

        // state check
        MeetingCard card = (MeetingCard) other;
        return id.getText().equals(card.id.getText())
                && meeting.equals(card.meeting);
    }
}
```
###### \java\seedu\address\ui\MeetingListPanel.java
``` java
/**
 * Panel containing the list of meetings.
 */
public class MeetingListPanel extends UiPart<Region> {
    private static final String FXML = "MeetingListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(MeetingListPanel.class);

    @FXML
    private ListView<MeetingCard> meetingListView;

    public MeetingListPanel(ObservableList<ReadOnlyMeeting> meetingList) {
        super(FXML);
        setConnections(meetingList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<ReadOnlyMeeting> meetingList) {
        ObservableList<MeetingCard> mappedMeetingList = EasyBind.map(
                meetingList, (meeting) -> new MeetingCard(meeting, meetingList.indexOf(meeting) + 1));
        meetingListView.setItems(mappedMeetingList);
        meetingListView.setCellFactory(listView -> new MeetingListPanel.MeetingListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        meetingListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in meeting list panel changed to : '" + newValue + "'");
                        raise(new MeetingPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code MeetingCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            meetingListView.scrollTo(index);
            meetingListView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code MeetingCard}.
     */
    class MeetingListViewCell extends ListCell<MeetingCard> {

        @Override
        protected void updateItem(MeetingCard meeting, boolean empty) {
            super.updateItem(meeting, empty);

            if (empty || meeting == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(meeting.getRoot());
            }
        }
    }
}
```
###### \java\seedu\address\ui\UiManager.java
``` java
    /**
     * To check if there is a meeting on the day of logging in, only shows reminder if there is a meeting
     * @param meetingList
     * @return
     */
    private boolean meetingToday (ObservableList<ReadOnlyMeeting> meetingList) {
        for (ReadOnlyMeeting meeting : meetingList) {
            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime currDate = LocalDateTime.now();
            LocalDateTime meetDate = LocalDateTime.parse(meeting.getDate().toString(), formatter);
            long daysBet = ChronoUnit.DAYS.between(currDate, meetDate);
            if (daysBet == 0) {
                return true;
            }
        }
        return false;
    }
```
###### \resources\view\MeetingAlert.fxml
``` fxml
<StackPane fx:id="helpWindowRoot" styleClass="background" stylesheets="@DarkTheme.css" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
   <children>
      <StackPane prefHeight="600.0" prefWidth="1000.0" styleClass="background" stylesheets="@DarkTheme.css">
         <children>
            <Button layoutX="750.0" layoutY="500.0" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" mnemonicParsing="false" onKeyPressed="#handleExit" onMouseClicked="#handleExit" prefHeight="40.0" prefWidth="90.0" stylesheets="@DarkTheme.css" text="Got it!" StackPane.alignment="BOTTOM_CENTER">
               <StackPane.margin>
                  <Insets bottom="20.0" />
               </StackPane.margin>
            </Button>
            <Label fx:id="WarningMessage" alignment="CENTER" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="100.0" prefWidth="700.0" styleClass="label-warning" stylesheets="@DarkTheme.css" text="\$Warning" StackPane.alignment="TOP_CENTER">
               <StackPane.margin>
                  <Insets top="60.0" />
               </StackPane.margin></Label>
            <Label fx:id="FirstMeeting" alignment="CENTER" maxHeight="-Infinity" maxWidth="1.7976931348623157E308" minHeight="-Infinity" minWidth="-Infinity" prefHeight="100.0" prefWidth="800.0" styleClass="label-warning" stylesheets="@DarkTheme.css" text="\$PersonName" StackPane.alignment="CENTER">
               <StackPane.margin>
                  <Insets bottom="80.0" />
               </StackPane.margin>
            </Label>
            <Label fx:id="NameMeeting" alignment="CENTER" maxHeight="-Infinity" maxWidth="1.7976931348623157E308" minHeight="-Infinity" minWidth="-Infinity" prefHeight="70.0" prefWidth="700.0" styleClass="label-warning" stylesheets="@DarkTheme.css" text="\$Meeting" StackPane.alignment="CENTER">
               <StackPane.margin>
                  <Insets top="100.0" />
               </StackPane.margin>
            </Label>
         </children>
      </StackPane>
   </children>
</StackPane>
```
###### \resources\view\MeetingListCard.fxml
``` fxml
<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="105" GridPane.columnIndex="0">
            <padding>
                <Insets bottom="5" left="15" right="5" top="5" />
            </padding>
            <HBox alignment="CENTER_LEFT" spacing="5">
                <Label fx:id="id" styleClass="cell_big_label">
                    <minWidth>
                        <!-- Ensures that the label text is never truncated -->
                        <Region fx:constant="USE_PREF_SIZE" />
                    </minWidth>
                </Label>
                <Label fx:id="name" styleClass="cell_big_label" text="\$first" />
            </HBox>
            <FlowPane fx:id="tags" />
            <Label fx:id="date" text="\$date" />
         <Label fx:id="person" text="\$person" />
         <Label fx:id="phoneNum" styleClass="cell_small_label" text="\$phoneNum" />
            <Label fx:id="place" styleClass="cell_small_label" text="\$place" />
        </VBox>
      <rowConstraints>
         <RowConstraints />
      </rowConstraints>
    </GridPane>
</HBox>
```
###### \resources\view\MeetingListPanel.fxml
``` fxml
<VBox xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
   <Label stylesheets="@DarkTheme.css" text="                   MEETINGS" />
    <ListView fx:id="meetingListView" VBox.vgrow="ALWAYS" />
</VBox>
```