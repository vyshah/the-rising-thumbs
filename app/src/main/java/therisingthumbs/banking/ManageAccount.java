package therisingthumbs.banking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class ManageAccount extends Activity {

    static User u;
    int screenWidth,screenHeight;
    static int portraitH = 0;
    ParseObject temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        u = ((myApplication) this.getApplication()).getUser();

        screenOrientation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (u.isTeller)
            getMenuInflater().inflate(R.menu.manage_account, menu);

        return true;
    }

    public void deleteAccount()
    {
        //to delete account, find the account on parse
        //and delete it
        final Intent intent = new Intent(this, HomePage.class);
        ParseQuery<ParseObject> toDelete = ParseQuery.getQuery("Account");
        toDelete.whereEqualTo("title", u.accountTitle);
        toDelete.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // account exists
                    if(Double.parseDouble(parseObject.get("balance").toString()) <= 0) {
                        parseObject.deleteInBackground();
                        try {
                            Thread.sleep((long) 1000.0, 0);
                        } catch (InterruptedException a) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                    else {
                        displayToast("Account has funds cannot delete");
                    }

                } else {
                    // YOU FUCKED UP, this shouldn't happen
                    System.out.println("YOU FUCKED UP DEVELOPER");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean del = false;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        if(u.isTeller) {
            if (id == R.id.delete_Accont) {
                //final Intent intent = new Intent(this, HomePage.class);
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        deleteAccount();
                                    /*pause program to allow it to delete account
                                    * from parse and get the new update of accounts*/
                                        /*try {
                                            Thread.sleep((long) 1000.0, 0);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }*/
                                        //startActivity(intent);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }//end delete account if
        }
        // TODO
        // Make logout functionality here


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
            final View rootView = inflater.inflate(R.layout.fragment_manage_account, container, false);

            TextView title = (TextView) rootView.findViewById(R.id.account_title);
            title.setText("Account Title: " +u.accountTitle);

            TextView type = (TextView) rootView.findViewById(R.id.account_type);
            type.setText("Account Type: " + u.accountType);

            TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabHost);

            tabHost.setup();
            TabHost.TabSpec tabSpec = tabHost.newTabSpec("Manage");
            tabSpec.setContent(R.id.tabManage);
            tabSpec.setIndicator("Manage Account");
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("History");
            tabSpec.setContent(R.id.tabHistory);
            tabSpec.setIndicator("Account History");
            tabHost.addTab(tabSpec);

            if (!u.isTeller)
            {
                //title.setPadding(0,150,0,0);
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Account");
                query.whereEqualTo("email", u.email);
                query.whereEqualTo("type", u.accountType);
                query.whereEqualTo("title", u.accountTitle);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            // object exists
                            TextView balance = (TextView) rootView.findViewById(R.id.balance);
                            balance.setText("Current balance: $" + parseObject.get("balance"));
                        } else {
                            // does not exists, create
                            System.err.println("FUCK");
                        }
                    }
                });

            // if user is not the teller, hide withdraw and transfer ImageButtons
            if(!u.isTeller) {
                //hide EditText field
                EditText edit = (EditText) rootView.findViewById(R.id.enter_amount);
                edit.setVisibility(View.INVISIBLE);

                //hide ImageButtons
                ImageButton with = (ImageButton) rootView.findViewById(R.id.withdraw);
                with.setVisibility(View.INVISIBLE);
                ImageButton depo = (ImageButton) rootView.findViewById(R.id.deposit);
                depo.setVisibility(View.INVISIBLE);

                //hide TextView objects below buttons
                TextView withText = (TextView) rootView.findViewById(R.id.textView);
                withText.setVisibility(View.INVISIBLE);
                TextView depoText = (TextView) rootView.findViewById(R.id.textView2);
                depoText.setVisibility(View.INVISIBLE);

                // Move current balance field closer to center of screen and make larger
                TextView currBal = (TextView) rootView.findViewById(R.id.balance);
                currBal.setTextSize(20);
                currBal.setPadding(0,50,0,0);
            }

            return rootView;
        }
    }

    public void screenOrientation()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point   size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        if(portraitH < screenHeight)
        {
            portraitH = screenHeight;
        }
        System.err.println(screenHeight +" " +screenWidth+" "+portraitH);
    }

    public void deposit(View view) {
        EditText e = (EditText) findViewById(R.id.enter_amount);
        TextView a = (TextView) findViewById(R.id.balance);

        if (!isEmpty(e)) {
            u.deposit(Double.parseDouble(e.getText().toString()), this, e, a);

        }
    }

    public void withdraw(View view) {
        EditText e = (EditText) findViewById(R.id.enter_amount);
        TextView b = (TextView) findViewById(R.id.balance);
        if(!isEmpty(e)) {
                u.withdraw(Double.parseDouble(e.getText().toString()), this, e, b);

        }
    }

    public void displayToast(String message)
    {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context,message, duration);
        toast.show();

    }

    //have a pop up window display
    //where the user will enter there the amount to transfer
    // and the account to transfer to
    //all is done with much magic
    public void transferPopUp(View view)
    {

        if(u.userBalance <= 0)
        {
            displayToast("You have no balance.\nCan't transfer money");
            return;
        }

        final Intent intent = new Intent(this, HomePage.class);

        //create the inflater for the popup window
        LayoutInflater popupInflater =
                (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        //load the layout to use
        View popupView = popupInflater.inflate(R.layout.transfer_layout,null);

        //create the pop up window
        final PopupWindow popUpTran =
                new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);




        //get where the email of is stored
        final EditText emailTrans = (EditText) popupView.findViewById(R.id.accountHolder);
        final EditText titleTrans = (EditText) popupView.findViewById(R.id.accountToTransfer);
        final EditText amountTrans = (EditText) popupView.findViewById(R.id.amountToTransfer);

        final Button transfer = (Button)popupView.findViewById(R.id.transferMoney);
        transfer.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(isEmpty(emailTrans)||isEmpty(titleTrans)||isEmpty(amountTrans))
                {
                    displayToast("Please fill out\nall the fields");
                    return;
                }


                if(u.userBalance < Double.parseDouble(amountTrans.getText().toString()))
                {
                    displayToast("You don't have sufficient funds.");

                    return;
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Account");
                //search for email
                query.whereEqualTo("email", emailTrans.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> accList, ParseException a)
                    {
                        System.err.println("looking for user "+emailTrans.getText().toString() );
                        System.err.println("SIZE:"+accList.size());
                        if (a == null && accList.size() > 0)
                        {
                            System.err.println("in IF");
                            //email exists, match account title
                            boolean canTrans = false;

                            //create iterator for account list
                            ParseObject curr = accList.get(0);

                            //traverse the list to find matching account
                            for(int i = 0; i< accList.size();i++)
                            {

                                String otherTitle = accList.get(i).get("title").toString();
                                curr = accList.get(i);
                                System.err.println(otherTitle + " " + titleTrans.getText().toString());

                                if(otherTitle.equals(titleTrans.getText().toString()))
                                {

                                    canTrans = true;
                                    System.err.println("found Match "+curr.get("balance").toString());
                                    break;
                                }
                            }
                            if (canTrans)
                            {
                                System.err.println("Found title");
                                //get the current balance of the other user
                                double otherBal = Double.parseDouble(curr.get("balance").toString());
                                System.err.println("Balance" + otherBal);
                                double newBal = otherBal + Double.parseDouble(amountTrans.getText().toString());

                                curr.put("balance", newBal);
                                curr.saveInBackground();

                                //update the balance
                                ParseQuery<ParseObject> user = ParseQuery.getQuery("Account");
                                user.whereEqualTo("email", u.email);
                                user.whereEqualTo("type", u.accountType);
                                user.whereEqualTo("title", u.accountTitle);
                                user.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            // object exists
                                           double userBal = Double.parseDouble(parseObject.get("balance").toString());

                                           double balUpdate = userBal - Double.parseDouble(amountTrans.getText().toString());

                                           parseObject.put("balance",balUpdate);
                                           parseObject.saveInBackground();

                                        } else {
                                            // YOU FUCKED UP HARD

                                        }
                                    }
                                });

                                try {
                                    Thread.sleep((long) 1000.0,0);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }

                                displayToast("Transfer Successful");

                                startActivity(intent);

                            }
                            else // account does not exist
                            {
                                displayToast("This account does not exists.");
                            }
                        }
                        else //email does not exist
                        {
                            System.err.println("No user");
                            //TODO inform user view toast
                            displayToast("This user is not registered with this bank.");
                        }

                    }

                });

            }//end onClick transfer button

        });//end onClickListener transfer


        final Button cancel = (Button)popupView.findViewById(R.id.cancelTrans);
        cancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                popUpTran.dismiss();
            }
        });

        //set the focus on the pop up window
        popUpTran.setFocusable(true);

        //
        popUpTran.setHeight(portraitH/2);
        popUpTran.setWidth(screenWidth);

        popUpTran.showAtLocation(popupView, Gravity.NO_GRAVITY,0,0);

    }

    protected boolean isEmpty(EditText t)
    {
        if(t.getText().toString().trim().length() == 0)
            return true;
        return false;

    }
}
