package com.tokens.nfc.nfctokens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

/**
 * Created by Kai on 1/8/2017.
 */

public class ListingFragment extends Fragment {

    ListingInterface mainActivity;
    ArchiveManager am;
    RecordArrayAdapter adapter;

    public void setArchiveManager(ArchiveManager am) {
        this.am = am;
    }

    public interface ListingInterface {
        void onListItemClick(Record f);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        mainActivity = (ListingInterface) ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listing_fragment, container, false);

        adapter = new RecordArrayAdapter((Context) mainActivity, R.layout.list_item, am.list());

        ListView listView = (ListView) rootView.findViewById(R.id.listing);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Record record = (Record) adapterView.getItemAtPosition(i);
                mainActivity.onListItemClick(record);
            }
        });

        SearchView searchView = (SearchView) rootView.findViewById(R.id.searchbox);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });

        return rootView;
    }
}
