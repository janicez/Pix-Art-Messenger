package de.pixart.messenger.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.pixart.messenger.R;
import de.pixart.messenger.entities.Account;
import de.pixart.messenger.entities.Conversation;
import de.pixart.messenger.ui.adapter.AccountAdapter;
import de.pixart.messenger.xmpp.jid.InvalidJidException;
import de.pixart.messenger.xmpp.jid.Jid;

public class ShareViaAccountActivity extends XmppActivity {
    public static final String EXTRA_CONTACT = "contact";
    public static final String EXTRA_BODY = "body";

    protected final List<Account> accountList = new ArrayList<>();
    protected ListView accountListView;
    protected AccountAdapter mAccountAdapter;

    @Override
    protected void refreshUiReal() {
        synchronized (this.accountList) {
            accountList.clear();
            accountList.addAll(xmppConnectionService.getAccounts());
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(this.accountList.size() > 0);
            actionBar.setDisplayHomeAsUpEnabled(this.accountList.size() > 0);
        }
        mAccountAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_accounts);

        accountListView = findViewById(R.id.account_list);
        this.mAccountAdapter = new AccountAdapter(this, accountList);
        accountListView.setAdapter(this.mAccountAdapter);
        accountListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                final Account account = accountList.get(position);
                final String body = getIntent().getStringExtra(EXTRA_BODY);

                try {
                    final Jid contact = Jid.fromString(getIntent().getStringExtra(EXTRA_CONTACT));
                    final Conversation conversation = xmppConnectionService.findOrCreateConversation(
                            account, contact, false, false);
                    switchToConversation(conversation, body, false);
                } catch (InvalidJidException e) {
                    // ignore error
                }

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final int theme = findTheme();
        if (this.mTheme != theme) {
            recreate();
        }
    }

    @Override
    void onBackendConnected() {
        final int numAccounts = xmppConnectionService.getAccounts().size();

        if (numAccounts == 1) {
            final String body = getIntent().getStringExtra(EXTRA_BODY);
            final Account account = xmppConnectionService.getAccounts().get(0);

            try {
                final Jid contact = Jid.fromString(getIntent().getStringExtra(EXTRA_CONTACT));
                final Conversation conversation = xmppConnectionService.findOrCreateConversation(
                        account, contact, false, false);
                switchToConversation(conversation, body, false);
            } catch (InvalidJidException e) {
                // ignore error
            }

            finish();
        } else {
            refreshUiReal();
        }
    }
}