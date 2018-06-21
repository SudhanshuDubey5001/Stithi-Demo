package stithi.my.com.stithi.WalletTransaction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;
import stithi.my.com.stithi.activities.WalletActivity;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewholder>{

    private static ArrayList<User> allUsers = new ArrayList<>();

    public void addUsers(User user){
        if(!user.getEmail().equals(ConstantClass.LOGGEDIN_USER.getEmail())){
            allUsers.add(user);
        }
    }

    public void clearArray(){
        allUsers.clear();
    }

    public String getEmailOfClicked(int pos){
        return allUsers.get(pos).getEmail();
    }

    @NonNull
    @Override
    public AllUserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_view_wallet, parent, false);
        return new AllUserAdapter.AllUserViewholder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserViewholder holder, int position) {
        holder.user_name.setText(allUsers.get(position).getName());
        holder.user_email.setText(allUsers.get(position).getEmail());
        holder.pos=position;
    }

    @Override
    public int getItemCount() {
        return allUsers.size();
    }

    class AllUserViewholder extends RecyclerView.ViewHolder{

        Context context;
        TextView user_name;
        TextView user_email;
        int pos;

        AllUserViewholder(View itemView,Context context) {
            super(itemView);
            this.context=context;
            user_name=itemView.findViewById(R.id.user_name_wallet);
            user_email=itemView.findViewById(R.id.user_email_wallet);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WalletActivity activity = (WalletActivity)AllUserViewholder.this.context;
                    if(activity.getInputCredit()>-1){
                        int inputAmount= activity.getInputCredit();
                        String email=AllUserAdapter.this.getEmailOfClicked(AllUserViewholder.this.pos);
                        FirebaseOperations operations = new FirebaseOperations(AllUserViewholder.this.context);
                        operations.performTransaction(inputAmount,email);
                    }
                }
            });
        }
    }
}
