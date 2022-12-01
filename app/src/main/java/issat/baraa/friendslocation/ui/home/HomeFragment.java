package issat.baraa.friendslocation.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import issat.baraa.friendslocation.JSONParser;
import issat.baraa.friendslocation.R;
import issat.baraa.friendslocation.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ArrayList<MyLocation>data=new ArrayList<MyLocation>();
    ArrayAdapter ad;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
          
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ad = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,data);
        binding.lv.setAdapter(ad);
        binding.btnDownload.setOnClickListener(view -> {
             new Telechargement(getActivity()).execute();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    class Telechargement extends AsyncTask {
        Context con;
        AlertDialog alert;
        public Telechargement(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //UIThread
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("telechargement");
            builder.setMessage("Vueillez patientez...");
            alert=builder.create();
            alert.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            JSONParser parser = new JSONParser();//pour executer le fichier php
            //String ip ="192.168.4.36";
            String ip ="192.168.43.247";
            /**
             * avd:10.0.2.2
             * LAN:ipv4:192.168
             * internet:site
             */
            String url = "http://"+ip+"/servicephp/getAll.php";
            JSONObject response = parser.makeHttpRequest(url,"GET",null);

            //reccupération des résultats.
            try {
                int success = response.getInt("success");
                if (success==0){
                    String msg = response.getString("messsage");
                }else {
                    JSONArray tableau = response.getJSONArray("Ami");
                    for (int i=0;i<tableau.length();i++){
                        JSONObject ligne = tableau.getJSONObject(i);
                        String nom =ligne.getString("nom");
                        String numero =ligne.getString("numero");
                        String longitude =ligne.getString("longitude");
                        String latitude =ligne.getString("latitude");
                        data.add(new MyLocation(nom,numero,longitude,latitude));
                    }
                }

            }catch (JSONException e){
                e.printStackTrace();}


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //UIThread
            ad.notifyDataSetChanged();
            alert.dismiss();

        }
    }
}