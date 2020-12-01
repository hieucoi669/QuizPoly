package vn.poly.quiz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import vn.poly.quiz.activities.PlayerDetailActivity;
import vn.poly.quiz.dao.UserDAO;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder>{

    private final List<Quiz> fullList;
    private List<Quiz> searchList;
    private final Context context;
    private final UserDAO userDAO;

    private final SimpleDateFormat sdf;

    public RecAdapter(List<Quiz> list, Context context) {
        this.fullList = list;
        this.context = context;
        userDAO = new UserDAO(context);
        Locale.setDefault(new Locale("vi", "VN"));
        sdf = new SimpleDateFormat("mm:ss:SS", Locale.getDefault());
    }

    public RecAdapter(List<Quiz> list, Context context, List<Quiz> searchList) {
        this.fullList = list;
        this.context = context;
        userDAO = new UserDAO(context);
        this.searchList = searchList;
        Locale.setDefault(new Locale("vi", "VN"));
        sdf = new SimpleDateFormat("mm:ss:SS", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        int rank = position;

        List<Quiz> list;
        if(searchList != null){
            list = searchList;

            for(int i=0; i<fullList.size(); i++){
                if(fullList.get(i).getQuizID().equals(list.get(position).getQuizID())){
                    rank = i;
                    break;
                }
            }
        }else{
            list = fullList;
        }

        if(rank == 0){
            holder.backGround.setBackgroundResource(R.drawable.bg_gold);
        }else if(rank == 1){
            holder.backGround.setBackgroundResource(R.drawable.bg_silver);
        }else if(rank == 2){
            holder.backGround.setBackgroundResource(R.drawable.bg_bronze);
        }

        String username = list.get(position).getUsername();
        User u = userDAO.checkUserExist(username, "username=?");

        if(u.getStringUri() != null){
            Glide.with(context).load(u.getStringUri()).into(holder.ivAvatar);
        }
        holder.tvDisplayName.setText(u.getDisplayName());
        holder.tvScore.setText(context.getString(R.string.list_item_count_correct_ans,
                list.get(position).getNumCorrectAnswer()));
        int time = list.get(position).getTime();

        String formatted = sdf.format(time);
        holder.tvPlayTime.setText(formatted);
        holder.ivDetail.setOnClickListener(view -> {
            Intent i = new Intent(context, PlayerDetailActivity.class);
            i.putExtra("username", u.getUsername());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        if(searchList != null){
            return searchList.size();
        }
        return fullList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        final ImageView ivAvatar;
        final ImageView ivDetail;
        final TextView tvDisplayName;
        final TextView tvPlayTime;
        final TextView tvScore;
        final LinearLayout backGround;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatarLB);
            ivDetail = itemView.findViewById(R.id.ivDetailLB);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayNameLB);
            tvPlayTime = itemView.findViewById(R.id.tvPlayTimeLB);
            tvScore = itemView.findViewById(R.id.tvScoreLB);
            backGround = itemView.findViewById(R.id.backGroundLayout);
        }
    }
}
