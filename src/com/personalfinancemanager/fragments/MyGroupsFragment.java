package com.personalfinancemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.personalfinancemanager.activities.MainActivity;
import com.personalfinancemanager.model.Group;

public class MyGroupsFragment extends android.support.v4.app.ListFragment {

	private String ref = MainActivity.firebaseRef;
	Firebase userRef = new Firebase(ref);

	MyGroupsRowAdapter adapter;

	MainActivity parentActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();
		parentActivity = (MainActivity) getActivity();

		adapter = new MyGroupsRowAdapter(parentActivity);

		View formView = inflater.inflate(R.layout.fragment_mygroups, null);

		formView.findViewById(R.id.footer_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				makeGroup();
			}
		});

		return formView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Firebase thisUserRef = userRef.child("users").child(parentActivity.getUserFirebaseID())
				.child("groups");

		thisUserRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				Group temp = snapshot.getValue(Group.class);

				adapter.add(temp);

			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {

				Group temp = arg0.getValue(Group.class);
				adapter.remove(temp);
			}
		});

		setListAdapter(adapter);
	}

	private void makeGroup() {

		parentActivity.switchContent(new NewGroupFragment());

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		GroupFragment selectedGroup = new GroupFragment();
		Bundle info = new Bundle();
		info.putString("id", adapter.getItem(position).getId());
		selectedGroup.setArguments(info);

		parentActivity.switchContent(selectedGroup);
	}

	public class MyGroupsRowAdapter extends ArrayAdapter<Group> {

		public MyGroupsRowAdapter(Context context) {
			super(context, 0);

		}

		@Override
		public void remove(Group object) {

			Group removeThis = null;
			for (int i = 0; i < getCount(); i++) {

				if (getItem(i).getId().equals(object.getId())) {
					removeThis = getItem(i);
				}
			}
			super.remove(removeThis);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {

				convertView = LayoutInflater.from(getContext())
						.inflate(R.layout.two_line_row, null);
			}

			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).getName());

			// TextView subtext = (TextView)
			// convertView.findViewById(R.id.row_subtext);
			// subtext.setText(getItem(position).calculateBalance().toString());

			convertView.setTag(getItem(position).getId());

			return convertView;
		}

	}

}
