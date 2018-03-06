package com.filelug.desktop.service;

import com.filelug.desktop.model.UserConnectionEvent;
import com.filelug.desktop.model.UserConnectionModel;

import java.util.*;

/**
 * <code>UserConnectionService</code> handles <code>UserConnectionModel</code>s to manage the connection status of all users.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserConnectionService extends Observable {

    private static List<UserConnectionModel> userConnectionModels;

    public UserConnectionService() {
        if (userConnectionModels == null) {
            List<UserConnectionModel> models = new ArrayList<>();

            // make is a synchronized List
            userConnectionModels = Collections.synchronizedList(models);
        }
    }

    public List<UserConnectionModel> getUserConnectionModels() {
        return userConnectionModels;
    }

    public void updateUserConnectionModel(UserConnectionModel userConnectionModel) {
        if (userConnectionModel != null) {
            String userId = userConnectionModel.getUserId();

            UserConnectionModel foundUserConnectionModel = findUserConnectionModelByUserId(userId, false);

            if (foundUserConnectionModel != null) {
                // update

                UserConnectionModel oldCopy = foundUserConnectionModel.copy();

                UserConnectionModel newCopy = userConnectionModel.copy();

                setChanged();
                notifyObservers(new UserConnectionEvent(UserConnectionEvent.ChangeType.USER_CONNECTION_UPDATED, oldCopy, newCopy));
            }
        }
    }

    public void addOrUpdateUserConnectionModel(UserConnectionModel userConnectionModel) {
        if (userConnectionModel != null) {
            String userId = userConnectionModel.getUserId();

            UserConnectionModel foundUserConnectionModel = findUserConnectionModelByUserId(userId, false);

            if (foundUserConnectionModel == null) {
                // add

                userConnectionModels.add(userConnectionModel);

                UserConnectionModel copy = userConnectionModel.copy();

                setChanged();
                notifyObservers(new UserConnectionEvent(UserConnectionEvent.ChangeType.USER_CONNECTION_ADDED, null, copy));
            } else {
                // update

                UserConnectionModel oldCopy = foundUserConnectionModel.copy();

                foundUserConnectionModel.setConnectionState(userConnectionModel.getConnectionState());
                foundUserConnectionModel.setNickname(userConnectionModel.getNickname());

                UserConnectionModel newCopy = foundUserConnectionModel.copy();

                setChanged();
                notifyObservers(new UserConnectionEvent(UserConnectionEvent.ChangeType.USER_CONNECTION_UPDATED, oldCopy, newCopy));
            }
        }
    }

    // a copied UserConnectionModel returned if found.
    public UserConnectionModel findUserConnectionModelByUserId(String userId, boolean returnCopied) {
        UserConnectionModel found = null;

        for (UserConnectionModel userConnectionModel : userConnectionModels) {
            if (userConnectionModel.getUserId().equals(userId)) {
                if (returnCopied) {
                    found = userConnectionModel.copy();
                } else {
                    found = userConnectionModel;
                }

                break;
            }
        }

        return found;
    }

    public int indexOfUserConnectionModelsByUserId(String userId) {
        int found = -1;

        int index = -1;

        for (UserConnectionModel userConnectionModel : userConnectionModels) {
            index++;

            if (userConnectionModel.getUserId().equals(userId)) {
                found = index;

                break;
            }
        }

        return found;
    }

    // the instance of UserConnectionModel(not copied) returned if found.
    public UserConnectionModel findUserConnectionModelByIndex(int index) {
        UserConnectionModel found = null;

        int count = userConnectionModels.size();

        if (index > -1 && index < count) {
            found = userConnectionModels.get(index);
        }

        return found;
    }

    public void removeUserConnectionModelByUserId(String userId) {
        for (Iterator<UserConnectionModel> iterator = userConnectionModels.listIterator(); iterator.hasNext(); ) {
            UserConnectionModel model = iterator.next();

            if (model.getUserId().equals(userId)) {
//                UserConnectionModel copy = model.copy();

                iterator.remove();

//                setChanged();
//                notifyObservers(new UserConnectionEvent(UserConnectionEvent.ChangeType.USER_CONNECTION_REMOVED, copy, null));

                break;
            }
        }
    }
}
