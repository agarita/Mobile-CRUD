package com.example.mobile_crud;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;


    //defining views
    EditText editTextPersonId, editTextName, editTextLast_name;
    ListView listView;
    ProgressBar progressBar;
    Button buttonAddUpdate;


    //we will use this list to display hero in listview
    List<Person> personList;

    //as the same button is used for create and update
    //we need to track whether it is an update or create operation
    //for this we have this boolean
    boolean isUpdating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPersonId = (EditText) findViewById(R.id.editTextPersonId);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLast_name = (EditText) findViewById(R.id.editTextLast_name);

        buttonAddUpdate = (Button) findViewById(R.id.buttonAddUpdate);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listViewPersons);

        personList = new ArrayList<>();


        buttonAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                if (isUpdating) {
                    //calling the method update hero
                    //method is commented becuase it is not yet created
                    updatePerson();
                } else {
                    //if it is not updating
                    //that means it is creating
                    //so calling the method create hero
                    createPerson();
                }
            }
        });

        //calling the method read heroes to read existing heros from the database
        //method is commented because it is not yet created
        readPersons();
    }

    private void createPerson() {
        String name = editTextName.getText().toString().trim();
        String last_name = editTextLast_name.getText().toString().trim();

        //validating the inputs
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(last_name)) {
            editTextLast_name.setError("Please enter last name");
            editTextLast_name.requestFocus();
            return;
        }

        //if validation passes

        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("last_name", last_name);


        //Calling the create hero API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_PERSON, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void readPersons() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_PERSONS, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updatePerson() {
        String id = editTextPersonId.getText().toString();
        String name = editTextName.getText().toString().trim();
        String last_name = editTextLast_name.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(last_name)) {
            editTextLast_name.setError("Please enter last name");
            editTextLast_name.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        params.put("last_name", last_name);


        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_PERSON, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Add");

        editTextName.setText("");
        editTextLast_name.setText("");

        isUpdating = false;
    }

    private void deletePerson(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_PERSON + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshPersonList(JSONArray persons) throws JSONException {
        //clearing previous heroes
        personList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < persons.length(); i++) {
            //getting each hero object
            JSONObject obj = persons.getJSONObject(i);

            //adding the hero to the list
            personList.add(new Person(
                    obj.getInt("id"),
                    obj.getString("name"),
                    obj.getString("last_name")
            ));
        }

        //creating the adapter and setting it to the listview
        PersonAdapter adapter = new PersonAdapter(personList);
        listView.setAdapter(adapter);
    }

    class PersonAdapter extends ArrayAdapter<Person> {

        //our hero list
        List<Person> personList;

        //constructor to get the list
        public PersonAdapter(List<Person> personList) {
            super(MainActivity.this, R.layout.layout_person_list, personList);
            this.personList = personList;
        }

        //method returning list item
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_person_list, null, true);

            //getting the textview for displaying name
            TextView textViewName = listViewItem.findViewById(R.id.TextViewName);

            //the update and delete textview
            TextView textViewUpdate = listViewItem.findViewById(R.id.TextViewUpdate);
            TextView textViewDelete = listViewItem.findViewById(R.id.TextViewDelete);

            final Person person = personList.get(position);

            textViewName.setText(person.getName() + " " + person.getLast_name());

            //attaching click listener to update
            textViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //so when it is updating we will
                    //make the isUpdating as true
                    isUpdating = true;

                    //we will set the selected hero to the UI elements
                    editTextPersonId.setText(String.valueOf(person.getId()));
                    editTextName.setText(person.getName());
                    editTextLast_name.setText(person.getLast_name());

                    //we will also make the button text to Update
                    buttonAddUpdate.setText("Update");
                }
            });

            //when the user selected delete
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // we will display a confirmation dialog before deleting
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Delete " + person.getName())
                            .setMessage("Are you sure you want to delete it?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //if the choice is yes we will delete the hero
                                    //method is commented because it is not yet created
                                    deletePerson(person.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });

            return listViewItem;
        }
    }

    //inner class to perform network request extending an AsyncTask
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    refreshPersonList(object.getJSONArray("persons"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}