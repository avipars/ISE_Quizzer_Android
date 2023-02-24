package com.aviparshan.isequiz.Controller.Questions;


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
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuizViewHolder> {

   private List<QuizQuestion> quizQuestions;
   private List<QuizQuestion> filteredQuestions;
   private OnItemClickListener listener;

   public QuestionAdapter(List<QuizQuestion> quizQuestions) {
      this.quizQuestions = quizQuestions;
      this.filteredQuestions = quizQuestions;
   }

   @NonNull
   @Override
   public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
      return new QuizViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) { //, @NonNull List<Object> payloads
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
      QuizQuestion question = filteredQuestions.get(position);
      holder.questionTextView.setText(question.getQuestion());
      holder.answerTextView.setText(question.getCorrectAnswer());
      holder.weekTextView.setText(question.getWeekNum());
   }

   @Override
   public int getItemCount() {
      return filteredQuestions.size();
   }

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.listener = listener;
   }

   public interface OnItemClickListener {
      void onItemClick(QuizQuestion question);
   }

   public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      public TextView questionTextView;
      public TextView answerTextView;
      public TextView weekTextView;
      public QuizViewHolder(@NonNull View itemView) {
         super(itemView);
         questionTextView = itemView.findViewById(R.id.questionTextView);
         answerTextView = itemView.findViewById(R.id.answerTextView);
         weekTextView = itemView.findViewById(R.id.weekTextView);
         itemView.setOnClickListener(this);
      }

      @Override
      public void onClick(View v) {
         int position = getAdapterPosition();
         if (position != RecyclerView.NO_POSITION && listener != null) {
            QuizQuestion question = filteredQuestions.get(position);
            listener.onItemClick(question);
         }
      }

      public void bindQuestion(QuizQuestion question) {
         questionTextView.setText(question.getQuestion());
         answerTextView.setText(question.getCorrectAnswer());
         weekTextView.setText(question.getWeekNum());
      }
   }

   public void filter(String query) {
      List<QuizQuestion> newFilteredQuestions = new ArrayList<>();
      for (QuizQuestion question : quizQuestions) {
         if (question.getQuestion().toLowerCase().contains(query.toLowerCase())) {
            newFilteredQuestions.add(question);
         }
      }
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(filteredQuestions, newFilteredQuestions));
      filteredQuestions = newFilteredQuestions;
      diffResult.dispatchUpdatesTo(this);
   }

//   public void filter(String query) {
//      filteredQuestions = new ArrayList<>();
//      for (QuizQuestion question : quizQuestions) {
//         if (question.getQuestion().toLowerCase().contains(query.toLowerCase())) {
//            filteredQuestions.add(question);
//         }
//      }
//      notifyDataSetChanged();
//   }

//   public void sort() {
//      filteredQuestions.sort(new Comparator<QuizQuestion>() {
//         @Override
//         public int compare(QuizQuestion q1, QuizQuestion q2) {
//            return q1.getQuestion().compareTo(q2.getQuestion());
//         }
//      });
//      notifyDataSetChanged();
//   }

   public void sort() {
      List<QuizQuestion> newFilteredQuestions = new ArrayList<>(filteredQuestions);
      newFilteredQuestions.sort(new Comparator<QuizQuestion>() {
         @Override
         public int compare(QuizQuestion q1, QuizQuestion q2) {
            return q1.getQuestion().compareTo(q2.getQuestion());
         }
      });
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(filteredQuestions, newFilteredQuestions));
      filteredQuestions = newFilteredQuestions;
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
