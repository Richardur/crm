package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aiva.aivacrm.R;
import com.aiva.aivacrm.home.TasksTab;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Action;
import model.Task;

public class AdapterTasks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static List<Task> items = new ArrayList<>();
    private Context ctx;
    public List<Integer> date;
    private int animation_type = 0;
    public int elements = 0;

    Timestamp t1, t2;

    public AdapterTasks(TasksTab context, List<Task> items, int animation_type, Timestamp a, Timestamp b) {

        t1=a;
        t2=b;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public View lyt_parent;
        public TextView laikas;
        public TextView klientas;
        public TextView darbas;
        public TextView komentaras;
        public Button atlikta;


        public OriginalViewHolder(View v) {
            super(v);
            laikas = (TextView) v.findViewById(R.id.laikas);
            klientas = (TextView) v.findViewById(R.id.klientas);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            darbas = (TextView) v.findViewById(R.id.darbas);
            komentaras = (TextView) v.findViewById(R.id.komentaras);
            atlikta = (Button) v.findViewById(R.id.atlikta);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        Type collectionType = new TypeToken<List<Action>>(){}.getType();
        List<Action> actions = actionList();

        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Task t = items.get(position);
            //if(t.AtlikData.after(t1)) {
                for (Action action : actions) {
                    if (action.id == t.VeiksmoID) view.darbas.setText(action.action);
                }

                view.laikas.setText(t.getTime(t.AtlikData));
                //view.klientas.setText(t.KlientasID);
                //view.darbas.setText(t.VeiksmoID);
                view.komentaras.setText(t.Komentaras);
            //}

            setAnimation(view.itemView, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public static void setItems(List<Task> data) {


        items.clear();
        items.addAll(data);
    }
    public List<Action> actionList(){
        List<Action> a = new ArrayList<>();

        a.add(new Action(16,"Paruošti projektą"));
        a.add(new Action(17,"Pasidomėti toliau"));
        a.add(new Action(14,"Parašyti el.laišką"));
        a.add(new Action(13,"Paruošti sutartį"));
        a.add(new Action(18,"žsakyti pas tiekėjus"));
        a.add(new Action(6,"Išsiųsti sąskaitą"));
        a.add(new Action(5,"Pataisyti pasiūlymą"));
        a.add(new Action(4,"Paruošti pasiūlymą"));
        a.add(new Action(3,"Paskambinti"));
        a.add(new Action(2,"Atvyksta į įmonę"));
        a.add(new Action(1,"Nuvykti pas klientą"));

        return a;
    }


}