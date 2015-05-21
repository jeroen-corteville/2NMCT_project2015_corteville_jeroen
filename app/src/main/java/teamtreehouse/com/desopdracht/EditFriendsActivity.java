package teamtreehouse.com.desopdracht;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = getFragmentManager();
        EditFriendsFragment list = new EditFriendsFragment();
        fm.beginTransaction().add(android.R.id.content, list).commit();
    }

    public static class EditFriendsFragment extends ListFragment {

        public static final String TAG = EditFriendsFragment.class.getSimpleName();

        protected List<ParseUser> mUsers;
        protected ParseRelation<ParseUser> mFriendsRelation;
        protected ParseUser mCurrentUser;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();
            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.orderByAscending(ParseConstants.KEY_USERNAME);
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null) {
                        //Success
                        mUsers = users;
                        String[] usernames = new String[mUsers.size()];
                        int i = 0;
                        for (ParseUser user : mUsers) {
                            usernames[i] = user.getUsername();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getListView().getContext(), android.R.layout.simple_list_item_checked,
                                usernames);
                        setListAdapter(adapter);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        addFriendCheckmarks();
                    } else {
                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }

        @Override
        public void onListItemClick (ListView l, View v, int position, long id){
            super.onListItemClick(l, v, position, id);

            if (getListView().isItemChecked(position)) {
                // add the friend
                mFriendsRelation.add(mUsers.get(position));
            } else {
                // remove the friend
                mFriendsRelation.remove(mUsers.get(position));
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }

        private void addFriendCheckmarks() {
            mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        //list returned - look for a match
                        for (int i = 0; i < mUsers.size(); i++) {
                            ParseUser user = mUsers.get(i);

                            for (ParseUser friend : friends) {
                                if (friend.getObjectId().equals(user.getObjectId())) {
                                    getListView().setItemChecked(i, true);
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }
}