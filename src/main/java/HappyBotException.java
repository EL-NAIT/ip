/**
 * Mainly used for handling invalid user inputs into HappyBot
 */
public class HappyBotException extends Exception {

    /**
     * Creates an exception with a message that explains the invalid command.
     *
     * @param message the explanation of the input error
     */
    public HappyBotException(String message) {
        super(message);
    }
}
