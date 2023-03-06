package com.aviparshan.isequiz.Controller;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.aviparshan.isequiz.Models.QuizQuestion;

import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 3/6/2023 on com.aviparshan.isequiz.Controller
 */
//Comparing data between lists
public class DiffCallback extends DiffUtil.Callback {

    private final List<QuizQuestion> mOldList, mNewList;

    public DiffCallback(List<QuizQuestion> oldList, List<QuizQuestion> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        if (mOldList == null) return 0; //null = not initialized = empty
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        if (mNewList == null) return 0; //null = not initialized = empty
        return mNewList.size();
    }


    //   avi's implementation
    public static boolean areItemsSame(QuizQuestion oldCountry, QuizQuestion newCountry) {
        return oldCountry.getHash() == newCountry.getHash();
    }

    //    same ID
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // add a unique ID property on Country and expose a getId() method
        return areItemsSame(mOldList.get(oldItemPosition), mOldList.get(newItemPosition));
//        return mOldList.get(oldItemPosition).getID() == mNewList.get(newItemPosition).getID();
    }

    //   avi's implementation
    public static boolean areContentsSame(QuizQuestion oldCountry, QuizQuestion newCountry) {
//        if (!oldCountry.getName().equalsIgnoreCase(newCountry.getName())) return false;
//        if (!oldCountry.getIsoCode().equalsIgnoreCase(newCountry.getIsoCode())) return false;
//        if (!oldCountry.getFlagCode().equalsIgnoreCase(newCountry.getFlagCode())) return false;
//        if (!oldCountry.getDialCode().equalsIgnoreCase(newCountry.getDialCode())) return false;
//        if (oldCountry.isAvailable() != newCountry.isAvailable()) return false;
//        return true;

        return oldCountry.equals(newCountry);

    }

    //    same data?
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

//        final Country oldItem = mOldList.get(oldItemPosition);
//        final Country newItem = mNewList.get(newItemPosition);
//        return oldItem.getName().equalsIgnoreCase(newItem.getName());
        return areContentsSame(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    public static final DiffUtil.ItemCallback<QuizQuestion> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<QuizQuestion>() {
                @Override
                public boolean areItemsTheSame(@NonNull QuizQuestion oldItem, @NonNull QuizQuestion newItem) {
                    return areItemsSame(oldItem, newItem);
//                    return oldItem.getID() == newItem.getID();
                }

                @Override
                public boolean areContentsTheSame(@NonNull QuizQuestion oldItem, @NonNull QuizQuestion newItem) {
                    return areContentsSame(oldItem, newItem);
//                    return (oldItem.getName().equalsIgnoreCase(newItem.getName()) && oldItem.isAvailable() == newItem.isAvailable());
                }
            };
}