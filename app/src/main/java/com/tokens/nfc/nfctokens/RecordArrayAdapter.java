package com.tokens.nfc.nfctokens;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.filter;
import static android.R.id.list;
import static com.tokens.nfc.nfctokens.ArchiveManager.readFile;

/**
 * Created by Kai on 1/8/2017.
 */
public class RecordArrayAdapter extends ArrayAdapter<Record> {

    protected Filter filter;
    protected ArrayList<Record> original;

    RecordArrayAdapter(Context context, int resource, ArrayList<Record> files) {
        super(context, resource, files);
        this.original = new ArrayList<>(files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewItem = convertView;
        if (viewItem == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            viewItem = li.inflate(R.layout.list_item, null);
        }

        Record record = getItem(position);

        TextView tvTitle = (TextView) viewItem.findViewById(R.id.tv_title_list_item);
        tvTitle.setText(record.data.getName());

        boolean verified = (record.publicKey==null) ? false : true;
        setLockIcon(viewItem, verified);

        TextView tvContent = (TextView) viewItem.findViewById(R.id.tv_content_list_item);
        String content = new String(ArchiveManager.readFile(record.data));
        if (content.length() > 80) {
            content = content.substring(0, 80);
        }
        tvContent.setText(content);

        return viewItem;
    }

    public void setLockIcon(View view, boolean verified) {
        ImageView ivLock = (ImageView) view.findViewById(R.id.img_lock);
        if (verified) {
            ivLock.setImageResource(R.drawable.ic_lock_black_24dp);
            ivLock.setColorFilter(Color.argb(255, 50, 210, 30));
        } else {
            ivLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
            ivLock.setColorFilter(Color.argb(255, 230, 50, 20));
        }
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new RecordFilter();
        }

        return filter;
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();
            String keyword = constraint.toString().toLowerCase();
            if (keyword == null || keyword.length() == 0){
                results.values = original;
                results.count = original.size();
            }else{
                ArrayList<Record> filtered = new ArrayList<>();
                for (Record f : original) {
                    if (f.data.getName().toLowerCase().contains(keyword)) {
                        filtered.add(f);
                        continue;
                    }
                    String content = f.getData();
                    if (content.toLowerCase().contains(keyword)) {
                        filtered.add(f);
                    }
                }

                results.values = filtered;
                results.count = filtered.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            ArrayList<Record> files = (ArrayList<Record>) results.values;
            for (Record f : files) {
                add(f);
            }
            notifyDataSetChanged();
        }
    }
}
