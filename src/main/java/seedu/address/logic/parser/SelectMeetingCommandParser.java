package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.SelectMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author nelsonqyj
/**
 * Parses input arguments and creates a new SelectMeetingCommand object
 */
public class SelectMeetingCommandParser implements Parser<SelectMeetingCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SelectMeetingCommand
     * and returns an SelectMeetingCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SelectMeetingCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new SelectMeetingCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectMeetingCommand.MESSAGE_USAGE));
        }
    }

}
