package ru.turikhay.tlauncher.ui.login;

import net.minecraft.launcher.versions.ReleaseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.turikhay.tlauncher.TLauncher;
import ru.turikhay.tlauncher.managers.ProfileManager;
import ru.turikhay.tlauncher.managers.ProfileManagerListener;
import ru.turikhay.tlauncher.minecraft.auth.Account;
import ru.turikhay.tlauncher.minecraft.auth.Authenticator;
import ru.turikhay.tlauncher.minecraft.auth.AuthenticatorDatabase;
import ru.turikhay.tlauncher.minecraft.auth.AuthenticatorListener;
import ru.turikhay.tlauncher.ui.alert.Alert;
import ru.turikhay.tlauncher.ui.block.Blockable;
import ru.turikhay.tlauncher.ui.listener.AuthUIListener;
import ru.turikhay.tlauncher.ui.loc.LocalizableComponent;
import ru.turikhay.tlauncher.ui.swing.AccountCellRenderer;
import ru.turikhay.tlauncher.ui.swing.SimpleComboBoxModel;
import ru.turikhay.tlauncher.ui.swing.extended.ExtendedComboBox;
import ru.turikhay.tlauncher.user.InvalidCredentialsException;
import ru.turikhay.util.U;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class AccountComboBox extends ExtendedComboBox<Account> implements Blockable, LoginForm.LoginProcessListener, ProfileManagerListener, LocalizableComponent {
    private static final Logger LOGGER = LogManager.getLogger(AccountComboBox.class);

    private static final long serialVersionUID = 6618039863712810645L;
    private static final Account EMPTY;
    private static final Account MANAGE;
    private final ProfileManager manager;
    private final LoginForm loginForm;
    private final AuthenticatorListener listener;
    private final SimpleComboBoxModel<Account> model;
    private Account selectedAccount;
    boolean refreshing;

    static {
        EMPTY = AccountCellRenderer.EMPTY;
        MANAGE = AccountCellRenderer.MANAGE;
    }

    AccountComboBox(LoginForm lf) {
        super(new AccountCellRenderer());
        loginForm = lf;
        model = getSimpleModel();
        manager = TLauncher.getInstance().getProfileManager();
        manager.addListener(this);
        listener = new AuthUIListener(lf) {
            @Override
            public void onAuthPassingError(Authenticator auth, Throwable e) {
                if(e instanceof InvalidCredentialsException) {
                    //TLauncher.getInstance().getProfileManager().getAccountManager().getUserSet().remove(selectedAccount.getUser());
                    loginForm.pane.openAccountEditor();
                    loginForm.pane.accountManager.get().multipane.showTip("add-account-" +
                            (selectedAccount.getType() == Account.AccountType.ELY_LEGACY? "ely" :
                            selectedAccount.getType().name().toLowerCase())
                    );
                }
                super.onAuthPassingError(auth, e);
            }
        };
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Account selected = (Account) getSelectedItem();
                if (selected != null && selected != AccountComboBox.EMPTY) {
                    if (selected == AccountComboBox.MANAGE) {
                        if(selectedAccount != null && loginForm.pane.accountManager.isLoaded()) {
                            loginForm.pane.accountManager.get().list.select(selectedAccount);
                        }
                        loginForm.pane.openAccountEditor();
                        setSelectedIndex(0);
                    } else {
                        selectedAccount = selected;
                        TLauncher.getInstance().getProfileManager().getAccountManager().getUserSet().select(selectedAccount == null? null : selectedAccount.getUser(), false);
                        try {
                            TLauncher.getInstance().getProfileManager().saveProfiles();
                        } catch (IOException e1) {
                            LOGGER.warn("Could not save profiles", e1);
                        }
                    }
                }
                updateAccount();
            }
        });
    }

    private void updateAccount() {
        Account.AccountType type = Account.AccountType.PLAIN;
        if (!refreshing) {
            if(selectedAccount != null) {
                if (selectedAccount.getType() == Account.AccountType.ELY ||
                        selectedAccount.getType() == Account.AccountType.ELY_LEGACY ||
                        selectedAccount.getType() == Account.AccountType.MCLEAKS) {
                    if (!loginForm.tlauncher.getLibraryManager().isRefreshing()) {
                        loginForm.tlauncher.getLibraryManager().asyncRefresh();
                    }
                }
                type = selectedAccount.getType();
            }
        }
        VersionComboBox.showVersionForType = type;
    }

    public Account getAccount() {
        Account value = (Account) getSelectedItem();
        return value != null && !value.equals(EMPTY) ? value : null;
    }

    public void setAccount(Account account) {
        if (account != null) {
            if (!account.equals(getAccount())) {
                setSelectedItem(account);
            }
        }
    }

    public void logginingIn() throws LoginException {
        if(loginForm.versions.getVersion() != null &&
                loginForm.versions.getVersion().getAvailableVersion().getReleaseType() == ReleaseType.LAUNCHER) {
            return;
        }
        final Account account = getAccount();
        if (account == null) {
            loginForm.pane.openAccountEditor();
            Alert.showLocError("account.empty.error");
            throw new LoginException("Account list is empty!");
        } else if (!account.isFree()) {
            throw new LoginWaitException("Waiting for auth...", new LoginWaitException.LoginWaitTask() {
                public void runTask() {
                    Authenticator.instanceFor(account).pass(listener);
                }
            });
        }
    }

    public void loginFailed() {
    }

    public void loginSucceed() {
    }

    public void refreshAccounts(AuthenticatorDatabase db, Account select) {
        removeAllItems();
        selectedAccount = null;
        Collection list1 = db.getAccounts();
        if (list1.isEmpty()) {
            addItem(EMPTY);
        } else {
            refreshing = true;
            model.addElements(list1);
            Iterator var5 = list1.iterator();

            while (var5.hasNext()) {
                Account account1 = (Account) var5.next();
                if (select != null && select.equals(account1)) {
                    setSelectedItem(account1);
                    break;
                }
            }

            refreshing = false;
        }
        addItem(MANAGE);
        updateAccount();
    }

    public void updateLocale() {
        refreshAccounts(manager.getAuthDatabase(), null);
    }

    public void onAccountsRefreshed(AuthenticatorDatabase db) {
        refreshAccounts(db, null);
    }

    public void onProfilesRefreshed(ProfileManager pm) {
        refreshAccounts(pm.getAuthDatabase(), pm.getAccountManager().getUserSet().getSelected() == null? null : new Account(pm.getAccountManager().getUserSet().getSelected()));
    }

    public void onProfileManagerChanged(ProfileManager pm) {
        refreshAccounts(pm.getAuthDatabase(), pm.getAccountManager().getUserSet().getSelected() == null? null : new Account(pm.getAccountManager().getUserSet().getSelected()));
    }

    public void block(Object reason) {
        setEnabled(false);
    }

    public void unblock(Object reason) {
        setEnabled(true);
    }
}
