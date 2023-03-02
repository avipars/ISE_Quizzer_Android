package com.aviparshan.isequiz.Controller.Questions;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

   private List<QuizQuestion> quizQuestions;
   //private List<QuizQuestion> filteredQuestions;
   private OnItemClickListener listener;
   private OnItemLongClickListener longListener;

   private static int currentPos = 0;
   private Context context;
   public QuestionAdapter() {
      this.quizQuestions = new ArrayList<>();
   }

   public QuestionAdapter(Context context) {
      super();
      this.context = context;
      this.quizQuestions = new ArrayList<QuizQuestion>();
   }
   public QuestionAdapter(List<QuizQuestion> q) {
      super();

      this.quizQuestions = q;
      //this.filteredQuestions = quizQuestions;
   }

   public void addItems(List<QuizQuestion> dataList){
      quizQuestions.addAll(dataList);

   //   diff result to update the adapter
      DiffUtil.DiffResult DiffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
         @Override
         public int getOldListSize() {
            return quizQuestions.size();
         }

         @Override
         public int getNewListSize() {
            return dataList.size();
         }

         @Override
         public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return quizQuestions.get(oldItemPosition).getQuestion().equals(dataList.get(newItemPosition).getQuestion());
         }

         @Override
         public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return quizQuestions.get(oldItemPosition).equals(dataList.get(newItemPosition));
         }
      });
        DiffResult.dispatchUpdatesTo(this);


   }
   /**
    * Define the listener interface
    */
    public void updateModel(List<QuizQuestion> newQ) {
        this.quizQuestions.clear();
        this.quizQuestions.addAll(newQ);
        //notify that the whole list has changed
        notifyDataSetChanged();
    }

   @NonNull
   @Override
   public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
      return new QuestionViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) { //, @NonNull List<Object> payloads
//      if (payloads.isEmpty()) {
//         super.onBindViewHolder(holder, position, payloads);
//      }
//      else {
//         Bundle bundle = (Bundle) payloads.get(0);
//         for (String key : bundle.keySet()) {
//            switch (key) {
//               case "question":
//                  holder.questionTextView.setText(bundle.getString(key));
//                  break;
//               case "answer":
//                  holder.answerTextView.setText(bundle.getString(key));
//                  break;
//            }
//         }
//      }
      QuizQuestion question = quizQuestions.get(position);
//      holder.questionTextView.setText(question.getQuestion());
//      holder.answerTextView.setText(question.getCorrectAnswer());
//      holder.weekTextView.setText(question.getWeekNum());
      holder.bindQuestion(question);
   }

   @Override
   public int getItemCount() {
      return quizQuestions.size();
   }

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.listener = listener;
   }

   // Define long listener member variable
   public void setOnItemLongClickListener(QuestionAdapter.OnItemLongClickListener longListener) {
      this.longListener = longListener;
   }
   public interface OnItemClickListener {
      void onItemClick(QuizQuestion question);
   }

   // Define interface for long click events
   public interface OnItemLongClickListener {
      void onItemLongClick(View itemView, int position);
   }
   public class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

      public TextView questionTextView;
      public TextView answerTextView;
      public TextView weekTextView;
      public QuestionViewHolder(@NonNull View itemView) {
         super(itemView);
         questionTextView = itemView.findViewById(R.id.questionTextView);
         answerTextView = itemView.findViewById(R.id.answerTextView);
         weekTextView = itemView.findViewById(R.id.weekTextView);
         itemView.setOnClickListener(this);
         itemView.setOnLongClickListener(this);
      }

      @Override
      public void onClick(View v) {
         int position = getAdapterPosition();
         if (position != RecyclerView.NO_POSITION && listener != null) {
            QuizQuestion question = quizQuestions.get(position);
            listener.onItemClick(question);
         }
      }

      @Override
      public boolean onLongClick(View v) {
         if(longListener != null){
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
               longListener.onItemLongClick(itemView, position);
               return true;
            }
         }
         return false;
      }

      public void bindQuestion(QuizQuestion question) {
         questionTextView.setText(question.getQuestion());
         answerTextView.setText(question.getCorrectAnswer());
         weekTextView.setText("Week: " + question.getWeekNum());
      }
   }

   //getWeekNum
    public int getWeekNum() {
        return quizQuestions.get(0).getWeekNum();
    }
   //invalidate the list of questions
    public void invalidateQuestions() {
        quizQuestions = null;
        //notifyDataSetChanged();
    //   use DiffUtil to calculate the difference between the old and new list of questions
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, null));
        quizQuestions = null;
        diffResult.dispatchUpdatesTo(this);
    }

   // insert a question at a specific position
    public void insertQuestion(int position, QuizQuestion question) {
        quizQuestions.add(position, question);
        notifyItemInserted(position);
    }


   //update the list of questions
    public void updateQuestions(List<QuizQuestion> questions) {
        this.quizQuestions = questions;
    //    use DiffUtil to calculate the difference between the old and new list of questions
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, questions));
        quizQuestions = questions;
        diffResult.dispatchUpdatesTo(this);
    }

   //public void filter(String query) {
   //   List<QuizQuestion> newFilteredQuestions = new ArrayList<>();
   //   for (QuizQuestion question : quizQuestions) {
   //      if (question.getQuestion().toLowerCase().contains(query.toLowerCase())) {
   //         newFilteredQuestions.add(question);
   //      }
   //   }
   //   DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(filteredQuestions, newFilteredQuestions));
   //   filteredQuestions = newFilteredQuestions;
   //   diffResult.dispatchUpdatesTo(this);
   //}
   //

   //public void sort() {
   //   quizQuestions.sort(new Comparator<QuizQuestion>() {
   //      @Override
   //      public int compare(QuizQuestion q1, QuizQuestion q2) {
   //         return q1.getQuestion().compareTo(q2.getQuestion());
   //      }
   //   });
   //   notifyDataSetChanged();
   //}

   public void sort() {
      List<QuizQuestion> newFilteredQuestions = new ArrayList<>(quizQuestions);
      newFilteredQuestions.sort(new Comparator<QuizQuestion>() {
         @Override
         public int compare(QuizQuestion q1, QuizQuestion q2) {
            return q1.getQuestion().compareTo(q2.getQuestion());
         }
      });
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, newFilteredQuestions));
      quizQuestions = newFilteredQuestions;
      diffResult.dispatchUpdatesTo(this);
   }

   private static class QuizDiffCallback extends DiffUtil.Callback {

      private List<QuizQuestion> oldList;
      private List<QuizQuestion> newList;

      public QuizDiffCallback(List<QuizQuestion> oldList, List<QuizQuestion> newList) {
         this.oldList = oldList;
         this.newList = newList;
      }

      @Override
      public int getOldListSize() {
         return oldList.size();
      }

      @Override
      public int getNewListSize() {
         return newList.size();
      }

      @Override
      public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
         return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
      }

      @Override
      public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
         QuizQuestion oldQuestion = oldList.get(oldItemPosition);
         QuizQuestion newQuestion = newList.get(newItemPosition);
         return oldQuestion.getQuestion().equals(newQuestion.getQuestion())
                 && oldQuestion.getCorrectAnswer().equals(newQuestion.getCorrectAnswer());
      }

      @Nullable
      @Override
      public Object getChangePayload(int oldItemPosition, int newItemPosition) {
         QuizQuestion oldQuestion = oldList.get(oldItemPosition);
         QuizQuestion newQuestion = newList.get(newItemPosition);
         Bundle payload = new Bundle();
         if (!oldQuestion.getQuestion().equals(newQuestion.getQuestion())) {
            payload.putString("question", newQuestion.getQuestion());
         }
         if (!oldQuestion.getCorrectAnswer().equals(newQuestion.getCorrectAnswer())) {
            payload.putString("answer", newQuestion.getCorrectAnswer());
         }
         if (payload.size() == 0) {
            return null;
         }
         return payload;
      }
   }
}
