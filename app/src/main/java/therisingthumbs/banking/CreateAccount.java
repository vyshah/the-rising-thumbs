package therisingthumbs.banking;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;



public class CreateAccount extends Activity {

    static Spinner spinner;
    static User u; //current user
    static int screenWidth, screenHeight; //vars to hold the size of the screen
    static boolean isPortrait; //bool used to check if in portrait or landscape

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        u = ((myApplication) this.getApplication()).getUser();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        if (screenWidth <= screenHeight)
        {
            isPortrait = true;
        }
        else
            isPortrait = false;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);

            /**
             * Create our cool spinner that doesn't spin
             */
            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    rootView.getContext(),
                    R.array.create_spinner,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            TextView text = (TextView) rootView.findViewById(R.id.welcome);
            /**
             * Set the objects on the screen based on the layout of the screen
             */
            if( isPortrait )
                text.setPadding( 0, screenHeight/5, 0, 0);
            else
                text.setPadding( 0, screenHeight/7, 0, 0);

            return rootView;

        }
    }

    private void uniqueID (){String uniqueKey = UUID.randomUUID().toString(); }

    public void randomKey() {
        long key = 0;
        Random randomGenerator = new Random();
        // Change size to number of users
        for (int i = 0; i< 100000000; i++) {
            // calling unique key
            key = randomGenerator.nextLong();
            System.err.println("The value of key is: " + key );
        }
    }

    /**
     * Respond to create button being clicked
     */
    public void createAccount(View view) {

        final Intent intent = new Intent(this, ManageAccount.class);

        ///need this for interest
        DateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //long createAt = parseObject.getCreatedAt().getTime();
        final Calendar cal = Calendar.getInstance();

        System.err.println(date.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH,30);
        //Date curDate = cal.getTime();
        System.err.println(date.format(cal.getTime()));

        ////////////////////////////////////////////////////////////

        System.err.println("Create button clicked");

        System.err.println("Spinner: " + spinner.getSelectedItem().toString());

        final EditText  title = (EditText) findViewById(R.id.title);
        final String type = spinner.getSelectedItem().toString();

        /**
         * Make sure edittext field are not empty
         */
        if(!isEmpty(title)) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Account");
            query.whereEqualTo("email", u.email);
            query.whereEqualTo("type", type);
            query.whereEqualTo("title", title.getText().toString());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                      // object exists
                        System.err.println("Account exists");
                    } else {
                      // does not exists, create
                        System.err.println("Creating account");
                        ParseObject account = new ParseObject("Account");
                        account.put("email", u.email);
                        account.put("title", title.getText().toString());
                        account.put("type", type);
                        account.put("balance", 0);
                        account.put("interestDate",cal.getTime());
                        account.saveInBackground();

                        u.accountType = type;
                        u.accountTitle = title.getText().toString();

                        startActivity(intent);
                    }
                }
            });
        }
        else {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, "Missing information!\nAll fields required.", duration);
            toast.show();
        }
    }

    protected boolean isEmpty(EditText t)
    {
        if(t.getText().toString().trim().length() == 0)
            return true;
        return false;

    }
}
