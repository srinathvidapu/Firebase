package helloworld.com.firebasedemo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.IpPrefix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    String url = "https://fir-cc28d.firebaseio.com/fir-cc28d";
    String serverKey="AIzaSyAB8qt5_6GhD2UaxRzJXaXXo0QHG-0oYaA";
    String senderId="31135502916";
    Button b1,btadd;
    RecyclerView rv;
    DatabaseReference ref;
    FirebaseRecyclerAdapter<Chat, VH> firebaseRecyclerAdapter;

    FloatingActionButton fbadd;
    EditText etname,etmessage,etuid;
    Chat chat;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    String token="eFpsDevWaCw:APA91bFHxP_ShYzSa1mIGPtFC8iGOHu9aQblQEgMH3q9AhwRl5Jq8ZItebJweAQRzfGSQSidFYSqjKUPQ_I07uq75Z_LMe3lO1gwMxaZ2J9sG_VN7h6RhpmYKwZtDUzG2XyuJwfG-Hmd";

    OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);

        fbadd= (FloatingActionButton) findViewById(R.id.fbadd);
        fbadd.setOnClickListener(this);
        ref = FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, VH>(Chat.class,
                R.layout.chat_item, VH.class, ref) {


            @Override
            protected void populateViewHolder(VH viewHolder, Chat model, final int position) {


                viewHolder.tvname.setText(model.getName());
                viewHolder.tvname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        firebaseRecyclerAdapter.getRef(position).removeValue();
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                    }
                });

            }
        };
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(firebaseRecyclerAdapter);

    }


    public static class VH extends RecyclerView.ViewHolder {

        public TextView tvname;

        public VH(View itemView) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvname);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.b1:
             chat = new Chat("name", "1", "message");
            ref.push().setValue(chat);
            firebaseRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.fbadd:
                onDialog();
                break;

        }

    }

    private void onDialog() {

        builder=new AlertDialog.Builder(this);
        View v=getLayoutInflater().inflate(R.layout.chat_dialog_add,null);
          builder.setView(v);
          etname= (EditText) v.findViewById(R.id.etname);
          etmessage= (EditText) v.findViewById(R.id.etmessage);
          etuid= (EditText) v.findViewById(R.id.etuid);
          btadd= (Button) v.findViewById(R.id.btadd);

          btadd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 alertDialog.dismiss();
                 chat=new Chat(etname.getText().toString().trim(),etuid.getText().toString().trim(),
                         etmessage.getText().toString().trim());

                 ref.push().setValue(chat, new DatabaseReference.CompletionListener() {
                     @Override
                     public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                         firebaseRecyclerAdapter.notifyDataSetChanged();


                         gcmBYOKhttp();
                     }
                 });
             }
         });

        alertDialog=builder.create();
        alertDialog.show();
    }


    public static class Chat {
        private String name;
        private String text;
        private String uid;

        public Chat() {
        }

        public Chat(String name, String uid, String message) {
            this.name = name;
            this.text = message;
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public String getText() {
            return text;
        }
    }



    private void gcmBYOKhttp() {


        mOkHttpClient = new OkHttpClient();
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject.put("title", "sdcsdsd");
            jsonObject.put("text", "scsdcds");
            jsonObject1.put("notification",jsonObject);
            jsonObject1.put("to", "news");
            Map<String, Object> map = new HashMap<>();
            map.put("notification", jsonObject);
            //token or /topics/[a-zA-Z0-9-_.~%]+
           map.put("to", "news");

            Request mRequest = new Request
                    .Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key="+serverKey)
                    .addHeader("project_id",senderId)
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(RequestBody.create(MediaType.parse("raw"),jsonObject1.toString()))
                    .build();

            mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.e("error0", e.toString());

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.e("res0", response.body().string());

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postBody = "{ \n" +
                "    \n" +
                "    \"notification\": {\n" +
                "    \"title\": \"TITLEk\",\n" +
                "    \"text\": \"WORKING\"\n" +
                "  },\n" +
                "    \n" +
                "    \"data\": {\n" +
                "    \"score\": \"5x1\",\n" +
                "    \"time\": \"15:10\"\n" +
                "  },\n" +
                "  \"to\" :\""+token+"+\"\n" +
                "}";


        Log.e("postbody",postBody);
        Request mRequest = new Request
                .Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key="+serverKey)
                .addHeader("project_id",senderId)
                .url("https://fcm.googleapis.com/fcm/send")
                .post(RequestBody.create(MediaType.parse("raw"), postBody))
                .build();

        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("error1", e.toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.e("res1", response.body().string());

            }
        });


    }




}
