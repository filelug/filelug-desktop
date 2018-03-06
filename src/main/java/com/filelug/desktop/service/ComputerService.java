package com.filelug.desktop.service;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface ComputerService {

//    /**
//     * Return value:
//     * <ul>
//     * <li>400:使用者帳號與密碼至少一個未提供。或者提供的 computer group 值不合法。</li>
//     * <li>401:使用者密碼與既有的不相同。</li>
//     * <li>403:使用者帳號尚未註冊。</li>
//     * <li>409:電腦名稱已存在。</li>
//     * </ul>
//     */
//    int createComputer(ComputerModel computerModel) throws Exception;


//    boolean isComputerNameAvailable(ComputerModel computerModel) throws Exception;

//    /**
//     * Return value:
//     * <ul>
//     * <li>400:使用者帳號與密碼至少一個未提供。或者提供的 computer group 值不合法。</li>
//     * <li>401:使用者密碼與既有的不相同。</li>
//     * <li>403:使用者帳號尚未註冊。</li>
//     * <li>409:新電腦名稱已存在。</li>
//     * </ul>
//     */
//    int changeComputer(ChangeComputerModel changeComputerModel) throws Exception;

//    /**
//     * Return value:
//     * <ul>
//     * <li>400: 使用者帳號與密碼至少一個未提供、或者 computer group 值不合法。</li>
//     * <li>401: 使用者密碼與既有的不相同。</li>
//     * <li>403: 使用者帳號尚未註冊。</li>
//     * <li>404: 電腦名稱不存在，或者recoveryKey錯誤。</li>
//     * </ul>
//     */
//    int deleteComputer(ComputerModel computerModel) throws Exception;

//    /**
//     * Finds the lug server id to connect at current moment.
//     *
//     * @return The lug server id to connect to.
//     */
//    String dispatchConnection(ConnectModel connectModel) throws Exception;

//    /**
//     * Finds the lug server id to connect at current moment.
//     *
//     * @return The lug server id to connect to.
//     */
//    String dispatchConnection(String userId) throws Exception;

    /**
     * Check if the computer exists.
     */
    boolean checkComputerExisting() throws Exception;

//    void changeAdministrator(String oldAdminUserId, String oldAdminPasswd, String oldAdminNickname, String newAdminUserId, String newAdminPasswd) throws Exception;

    void removeAllUsersFromComputer(FutureCallback<HttpResponse> callback) throws Exception;
}
