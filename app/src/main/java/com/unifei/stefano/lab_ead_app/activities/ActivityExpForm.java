package com.unifei.stefano.lab_ead_app.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.unifei.stefano.lab_ead_app.Controller;
import com.unifei.stefano.lab_ead_app.R;
import com.unifei.stefano.lab_ead_app.operations.IniciarOperacao;
import com.unifei.stefano.lab_ead_app.operations.OperationGetExpStatus;
import com.unifei.stefano.lab_ead_app.operations.OperationSendReport;

import java.util.ArrayList;
import java.util.Arrays;


public class ActivityExpForm extends Activity {

    private ArrayList<String> arrHint;
    private ArrayList<String> reportFieldNames;
    private String[] reportValues;
    private String mExpKey;

    private ListView listView;
    private VideoView videoView;
    private View expForm;
    private ImageView snapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_form);

        Controller.setmTelaExpForm(this);

        TextView mExpNameViewForm = (TextView) findViewById(R.id.exp_nameForm);
        Bundle b = getIntent().getExtras();
        mExpNameViewForm.setText(b.getString("expName"));
        mExpKey = b.getString("expID");
        reportFieldNames = b.getStringArrayList("expFormCampos");
        arrHint = b.getStringArrayList("expFormHints");
        reportValues = new String[reportFieldNames.size()];

        videoView = (VideoView) findViewById(R.id.videoView);
        snapshot = (ImageView) findViewById(R.id.imageView);
        expForm = findViewById(R.id.exp_form);

        listView = (ListView) findViewById(R.id.campos_list);
        listView.setAdapter(new MyListAdapter());

        //hides report form and video
        expForm.setVisibility(View.GONE);

        //starts 'fake streaming'
        Object[] params = {mExpKey, 1, this};
        IniciarOperacao.iniciar(OperationGetExpStatus.class, params);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Controller.setmTelaExpForm(this);
    }

    private boolean validateInput(){

        boolean cancel = false;

        for (String value : reportValues) {
            if (TextUtils.isEmpty(value)) {
                cancel = true;
            }
        }

        if (cancel){
            // There was an error; don't attempt loginRequest and focus the first
            // form field with an error.
            Toast.makeText(ActivityExpForm.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }

        return !cancel;
    }

    private void attemptSend() {

        ArrayList<String> reportValuesList = new ArrayList<> (Arrays.asList(reportValues));

        IniciarOperacao.iniciar(OperationSendReport.class, new Object[]{mExpKey, reportFieldNames, reportValuesList, this});
    }

    public void startVideo(View v) {
        videoView.start();
    }

    public void sendReport(View view) {

        if (validateInput()) {
            attemptSend();
            //Toast.makeText(ActivityExpForm.this, "Teste", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void updateSnapshot(Bitmap image){
        snapshot.setImageBitmap(image);
    }

    public void finishFakeStreaming(String filePath){

        //hide fakeStreaming view, shows form view
        expForm.setVisibility(View.VISIBLE);
        snapshot.setVisibility(View.GONE);
        justifyListViewHeightBasedOnChildren(listView);
        expForm.invalidate();

        //displays video, if available
        if(filePath != null){
            videoView.setVideoPath(filePath);
            videoView.setTag(filePath);
        }
    }

    private void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if(reportFieldNames != null && reportFieldNames.size() != 0){
                return reportFieldNames.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {

            return reportFieldNames.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = ActivityExpForm.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.itens, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.editText1 = (EditText) convertView.findViewById(R.id.editText1);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.textView1.setText(reportFieldNames.get(position));
            holder.editText1.setText(reportValues[position]);
            holder.editText1.setHint(arrHint.get(position));
            holder.editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

                //saves typed value into 'arrTemp'
                @Override
                public void afterTextChanged(Editable arg0) {
                    reportValues[holder.ref] = arg0.toString();
                }
            });

            holder.editText1.clearFocus();

            return convertView;
        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }
    }
}
