package com.filelug.desktop.model;

import com.filelug.desktop.Utility;

/**
 * <code>AccountState</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class AccountState /*extends Observable*/ {

//    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AccountState.class.getSimpleName());
//
//    private static AccountState theInstance;
//
//    private final UserDao userDao;

    public enum State {
        UNKNOWN, ACTIVATED, INACTIVATED, ACTIVATING
    }

//    public static AccountState getInstance() {
//        if (theInstance == null) {
//            theInstance = new AccountState();
//        }
//
//        return theInstance;
//    }
//
//    private AccountState() {
//        userDao = new UserDao();
//    }

    public static String localizedDisplayNameForState(AccountState.State state) {
        String localizedString = Utility.localizedString(state.name());

        return localizedString != null ? localizedString : "";
    }

//    public static String localizedDisplayNameForState(String stateString) {
//        String localizedString = Utility.localizedString(stateString);
//
//        return localizedString != null ? localizedString : "";
//    }
//
//    public State getState(String userId) {
//        // Get the state directly from DB
//
//        String stateString = userDao.findUserStateById(userId);
//
//        return stateString != null ? State.valueOf(stateString) : State.UNKNOWN;
//    }
//
//    // The newState will save to db for this user, if it is different from the current one.
//    public void setState(String userId, State newState) {
////        String oldState = userDao.findUserStateById(userId);
////
////        if (oldState == null || State.valueOf(oldState) != newState) {
//            // Set the status to DB, so only the userId, instead of the whole AccountState used as the argument of notifyObservers(Object)
//
//            userDao.updateUserStateById(userId, newState);
//
////            LOGGER.debug("Account state changed from " + oldState + " to " + newState.name());
//
//            setChanged();
//            notifyObservers(userId);
////        }
//    }
}
