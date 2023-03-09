package com.aviparshan.isequiz.Controller.Questions;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.Controller.Utils;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>  {

    private static final int currentPos = 0;
    private List<QuizQuestion> quizQuestions, filteredQuestions;
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

//    public QuestionAdapter() {
////        super(DIFF_CALLBACK); //call the diffCallback function
//        quizQuestions = new ArrayList<>();
//        filteredQuestions = quizQuestions;
//
//    }
//
//    public QuestionAdapter(Context context) {
////        super(DIFF_CALLBACK); //call the diffCallback function
//        quizQuestions = new ArrayList<>();
//        filteredQuestions = quizQuestions;
//    }

    public QuestionAdapter(List<QuizQuestion> q) {
//        super();
//        fix the issue with arguments
        this.quizQuestions = q;
        this.filteredQuestions = q;
    }

    public List<QuizQuestion> getQuizQuestions() {
        return filteredQuestions;
    }

    public void submitList(List<QuizQuestion> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, newList));
        quizQuestions = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    public void addItems(List<QuizQuestion> dataList) {
//        quizQuestions.addAll(dataList);
//        filteredQuestions = quizQuestions;
        submitList(dataList);
        //   diff result to update the adapter

    }

    /**
     * Define the listener interface
     */
    public void updateModel(List<QuizQuestion> newQ) {
        this.quizQuestions.clear(); //clear the old list
        this.quizQuestions.addAll(newQ); //add the new list
        this.filteredQuestions = quizQuestions;
        //notify that the whole list has changed, so diffUtil doesn't really matter
        DiffUtil.DiffResult DiffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, newQ));
        DiffResult.dispatchUpdatesTo(this);

//        notifyDataSetChanged();
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
        QuizQuestion question = filteredQuestions.get(position);
        holder.bindQuestion(question);
    }

    @Override
    public int getItemCount() {
        return filteredQuestions.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Define long listener member variable
    public void setOnItemLongClickListener(QuestionAdapter.OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

//    @Override
//    public Filter getFilter() {
//        //    uses the filter method filter()
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charString = constraint.toString();
//                if (charString.isEmpty()) {
//                    filteredQuestions = quizQuestions;
//                } else {
//                    List<QuizQuestion> filteredList = new ArrayList<>();
//                    for (QuizQuestion row : quizQuestions) {
//                        // name match condition. this might differ depending on your requirement
//                        // here we are looking for name or phone number match
//                        if (row.getQuestion().toLowerCase().contains(charString.toLowerCase())
//                                || row.getCorrectAnswer().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(row);
//                        }
//                    }
//                    filteredQuestions = filteredList;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredQuestions;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                //use DiffUtil to calculate the difference between the old and new list
////                use the DiffCallback Class I made
//                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizQuestions, filteredQuestions));
//                quizQuestions = filteredQuestions;
//                diffResult.dispatchUpdatesTo(QuestionAdapter.this);
//            }
//        };
//    }

    public void setAllAnswers(boolean show) {
        for (QuizQuestion q : filteredQuestions) {
            q.setShowAnswer(show);
            notifyItemChanged(filteredQuestions.indexOf(q),q);
        }
//        notifyDataSetChanged();
    }

    //get the question at a specific position and set to toggle what is shown
    public void toggleAnswer(int position, QuizQuestion qz) {


        //if it was shown, hide it , else show it
        int pos = filteredQuestions.indexOf(qz) == position ? position : filteredQuestions.indexOf(qz);

        filteredQuestions.get(pos).setShowAnswer(!filteredQuestions.get(pos).getShowAnswer());
        notifyItemChanged(position, qz);
    }

    //getWeekNum
    public int getWeekNum() {
        return filteredQuestions.get(0).getWeekNum();
    }

    //invalidate the list of questions
    public void invalidateQuestions() {
        filteredQuestions = null;
        //notifyDataSetChanged();
        //   use DiffUtil to calculate the difference between the old and new list of questions
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(filteredQuestions, null));
        filteredQuestions = null;
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
        filteredQuestions = quizQuestions;
        diffResult.dispatchUpdatesTo(this);
    }

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

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    // Define interface for long click events
    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void filter(String query) {
        List<QuizQuestion> newFilteredQuestions = new ArrayList<>();
        for (QuizQuestion question : quizQuestions) {
            if (question.getQuestion().toLowerCase().contains(query.toLowerCase())
                    || question.getCorrectAnswer().toLowerCase().contains(query.toLowerCase())) {
                newFilteredQuestions.add(question);
            }
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(filteredQuestions, newFilteredQuestions));
        filteredQuestions = newFilteredQuestions;
        diffResult.dispatchUpdatesTo(this);
    }

    private static class QuizDiffCallback extends DiffUtil.Callback {

        private final List<QuizQuestion> oldList, newList;

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
            return oldQuestion.getQuestion().equals(newQuestion.getQuestion()) &&
                    oldQuestion.getCorrectAnswer().equals(newQuestion.getCorrectAnswer());
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
            if(oldQuestion.getShowAnswer() != newQuestion.getShowAnswer()) {
                payload.putBoolean("showAnswer", newQuestion.getShowAnswer());
            }
            if(!oldQuestion.getPossibleAnswers().equals(newQuestion.getPossibleAnswers())) {
                payload.putStringArrayList("answers", (ArrayList<String>) newQuestion.getPossibleAnswers());
            }
            if (payload.size() == 0) {
                return null;
            }
            return payload;
        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final TextView questionTextView;
        private final TextView answerTextView;
        private final TextView allAnswersTextView;

        //public TextView weekTextView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
            allAnswersTextView = itemView.findViewById(R.id.allAnswerTextView);
            //weekTextView = itemView.findViewById(R.id.weekTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                QuizQuestion question = filteredQuestions.get(position);
                Log.e("QuestionViewHolder", "onClick: " + question.getPossibleAnswers().toString());
                listener.onItemClick(itemView, position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && longListener != null) {
                longListener.onItemLongClick(itemView, position);
                return true;
            }
            return false;
        }

        public void bindQuestion(QuizQuestion question) {
            questionTextView.setText(question.getQuestion());
            //weekTextView.setText("Week: " + question.gele tWeekNum());

            if (allAnswersTextView.getText().length() <= 0) { // only append once and don't allow it to be changed
//                get the string from resource
                if(question.getqType() == Utils.OPEN_ANSWER){
                    allAnswersTextView.setText(itemView.getContext().getString(R.string.open_answer));
                    allAnswersTextView.append("\n" + question.getPossibleAnswers().toString());
                }
                else if(question.getqType() == Utils.TRUE_FALSE){
//                    get the string from the string resource file
                    allAnswersTextView.setText(itemView.getContext().getString(R.string.true_false));
                    allAnswersTextView.append("\n" + question.getPossibleAnswers().toString());

                }
                else if(question.getqType() == Utils.MULTIPLE_CHOICE){
                    for (int i = 0; i < question.getPossibleAnswers().size(); ++i) {
                        allAnswersTextView.append(i+1 + ". " + question.getPossibleAnswers().get(i)); //last one
                        if(i != question.getPossibleAnswers().size() - 1){ //not last
                            allAnswersTextView.append("\n");
                        }
                    }
//                    allAnswersTextView.setText(question.getPossibleAnswers().toString());

                }
                else{
                    allAnswersTextView.setText("unknown" + question.getqType());
                }
                //get all answers as a list and display them in the possible answers text view, with a newline between each answer
            }
            answerTextView.setVisibility(View.VISIBLE);
            answerTextView.setText(question.getCorrectAnswer());
            allAnswersTextView.setVisibility(View.VISIBLE);
//            if (question.getShowAnswer()) { //hide unless clicked
//                answerTextView.setVisibility(View.VISIBLE);
//                answerTextView.setText(question.getCorrectAnswer());
//                allAnswersTextView.setVisibility(View.GONE);
//            } else {
//                answerTextView.setVisibility(View.GONE);
//                answerTextView.setText("");
//                allAnswersTextView.setVisibility(View.VISIBLE);
//            }


        }

    }
}