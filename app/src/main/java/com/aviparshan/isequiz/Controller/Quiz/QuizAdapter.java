package com.aviparshan.isequiz.Controller.Quiz;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.Controller.Questions.QuestionAdapter;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ISE Quiz Adapter (outer view with week and menu)
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final List<Quiz> quizzes;
    // Define listener member variable
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public QuizAdapter(List<Quiz> q) {
        quizzes = q;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Define long listener member variable
    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public QuizAdapter.QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quiz_item, viewGroup, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz q = quizzes.get(position);
        holder.weekTV.setText(q.getWeek());
        holder.subjectTV.setText(q.getSubject());
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define interface for long click events
    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        private final TextView weekTV, subjectTV;
//        private final TextView urlTV;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            }
        };
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longListener.onItemLongClick(itemView, position);
                        return true;
                    }
                }
                return false;
            }
        };

        public QuizViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            weekTV = itemView.findViewById(R.id.weekTextView);
            subjectTV = itemView.findViewById(R.id.subjectTextView);
            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(longClickListener);
        }
    }
}