package therisingthumbs.banking;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Julio on 11/1/2014.
 */
public class accountAdapter extends BaseAdapter{

    static User u;

    private LayoutInflater accInflater;
    private static List<ParseObject> accs;

    public accountAdapter(Context context, List<ParseObject> accounts) {
       // super(context, R.layout.account_list_layout, bal);
        //System.err.println("Adapter constructor");
        u = ((myApplication) context.getApplicationContext()).getUser();
        //store the accounts passed
        accs = accounts;
        //System.err.println("Account size " + bal.size());

        //set the inflater needed for ListView
        accInflater = LayoutInflater.from(context);
        //System.err.println("End ad construct");

    }

    @Override
    public int getCount() {

        return accs.size();
    }

    @Override
    public ParseObject getItem(int i) {
        return accs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //System.err.println("In the View");

        View accView = accInflater.inflate(R.layout.account_list_layout,null);

        //get the type, title, and balance of the account
        String accountTitle = accs.get(position).getString("title");
        String accountType  = accs.get(position).getString("type");
        Number balance = accs.get(position).getNumber("balance");
        String email = accs.get(position).getString("email");

        //System.err.println("Account Type "+ accountType);
        //System.err.println("Balance "+ balance);

        //find the TextViews to display the data in
        TextView accTextView = (TextView) accView.findViewById(R.id.accountTitle);
        TextView typeTextView = (TextView) accView.findViewById(R.id.accountType);
        TextView balTextView = (TextView) accView.findViewById(R.id.accountBalance);
        TextView emailTextView = (TextView) accView.findViewById(R.id.accountEmail);

        //set the text in each TextView
        accTextView.setText("Title: " + accountTitle);
        typeTextView.setText("Type: " + accountType);
        balTextView.setText("Balance: $" + balance.toString());


        if(u.isTeller)
        {
            emailTextView.setText(email);
        }
        else
        {
            emailTextView.setVisibility(View.GONE);
        }

        return accView;

    }
}
