package com.example.kanjuice.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanjuice.R;
import com.example.kanjuice.models.HotDrink;
import com.example.kanjuice.models.TeaItem;
import com.example.kanjuice.utils.JuiceDecorator;

import java.util.ArrayList;
import java.util.List;

public class CTLAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "CTLAdapter";
    public static final int ANIMATION_DURATION = 500;
    private ArrayList<TeaItem> teaItems;
    private final LayoutInflater inflater;
    Context context;

    private int[] quantityNumbers = {R.id.one, R.id.two, R.id.three, R.id.sugarlessCheckbox};

    public CTLAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        teaItems = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return teaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return teaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? newView(parent) : convertView;
        bind(view, teaItems.get(position));
        return view;
    }

    private void bind(View view, final TeaItem teaItem) {
        final ViewHolder h = (ViewHolder) view.getTag();

        if (teaItem.animate) {
            showContentWithAnimation(h, teaItem);
        } else {
            showContent(h, teaItem);
        }

        if (teaItem.isMultiSelected) {
            h.multiSelect.titleView.setText(teaItem.teaName);
            if (teaItem.isSugarless) {
                h.multiSelect.sugarlessCheckbox.setChecked(true);
            }
            for (View v : h.multiSelect.quantityViews) {
                v.setSelected(false);
                v.setTag(teaItem);
            }
            h.multiSelect.quantityViews.get(teaItem.selectedQuantity - 1).setSelected(true);
        } else {
            h.singleSelect.titleView.setText(teaItem.teaName);
            h.singleSelect.titleInKanView.setText(teaItem.kanResId);
            h.singleSelect.imageView.setImageResource(teaItem.imageResId);
        }
    }

    private void showContentWithAnimation(final ViewHolder h, final TeaItem teaItem) {
        if (h.multiSelectView.getVisibility() == View.INVISIBLE && teaItem.isMultiSelected) {
            h.multiSelectView.setVisibility(View.VISIBLE);
            h.multiSelect.sugarlessCheckbox.setChecked(false);
            ObjectAnimator anim = ObjectAnimator.ofFloat(h.multiSelectView, "translationY", 500f, 0f);
            anim.setDuration(ANIMATION_DURATION);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.multiSelectView.setVisibility(View.VISIBLE);
                }
            });
            anim.start();

            ObjectAnimator anim1 = ObjectAnimator.ofFloat(h.singleItemView, "translationY", 0f, -500f);
            anim1.setDuration(ANIMATION_DURATION);
            anim1.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.singleItemView.setVisibility(View.INVISIBLE);
                    teaItem.animate = false;
                }
            });
            anim1.start();

        } else if (h.multiSelectView.getVisibility() == View.VISIBLE && !teaItem.isMultiSelected) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(h.multiSelectView, "translationY", -0f, 500f);
            anim.setDuration(ANIMATION_DURATION);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.multiSelectView.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

            h.singleItemView.setVisibility(View.VISIBLE);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(h.singleItemView, "translationY", -500f, 0f);
            anim1.setDuration(ANIMATION_DURATION);
            anim1.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.singleItemView.setVisibility(View.VISIBLE);
                    teaItem.animate = false;
                }
            });
            anim1.start();
        }
    }

    private void showContent(ViewHolder h, TeaItem teaItem) {
        h.multiSelectView.setTranslationY(0f);
        h.multiSelectView.setTranslationY(0f);
        h.singleItemView.setTranslationY(0f);
        h.singleItemView.setTranslationY(0f);

        h.multiSelectView.setVisibility(teaItem.isMultiSelected ? View.VISIBLE : View.INVISIBLE);
        h.singleItemView.setVisibility(teaItem.isMultiSelected ? View.INVISIBLE : View.VISIBLE);
    }

    private View newView(ViewGroup parent) {
        View juiceItemView = inflater.inflate(R.layout.juice_item, parent, false);

        ViewHolder h = new ViewHolder();
        h.singleItemView = (LinearLayout) juiceItemView.findViewById(R.id.single_select_layout);
        h.multiSelectView = (LinearLayout) juiceItemView.findViewById(R.id.multi_select_layout);
        h.multiSelect.titleView = (TextView) juiceItemView.findViewById(R.id.multi_select_title);
        h.multiSelect.sugarlessCheckbox = (CheckBox) juiceItemView.findViewById(R.id.sugarlessCheckbox);
        h.singleSelect.titleView = (TextView) juiceItemView.findViewById(R.id.single_select_title);
        h.singleSelect.titleInKanView = (TextView) juiceItemView.findViewById(R.id.single_select_title_in_kan);
        h.singleSelect.imageView = (ImageView) juiceItemView.findViewById(R.id.image);

        h.multiSelect.sugarlessCheckbox.setChecked(false);


        List<View> quantityViews = new ArrayList<>();
        for (int id : quantityNumbers) {
            quantityViews.add(juiceItemView.findViewById(id));
        }
        h.multiSelect.quantityViews = quantityViews;

        for (View view : quantityViews) {
            view.setOnClickListener(this);
        }

        juiceItemView.setTag(h);
        return juiceItemView;
    }


    @Override
    public void onClick(View view) {
        final TeaItem selectedTeaItem = (TeaItem) view.getTag();
        if (view.getId() == R.id.sugarlessCheckbox) {
            selectedTeaItem.isSugarless = !((TeaItem) view.getTag()).isSugarless;
            Toast.makeText(context, "You selected " + (selectedTeaItem.isSugarless ? "without sugar" : "with sugar"), Toast.LENGTH_SHORT).show();
            Log.d(TAG, " is sugarless : " + selectedTeaItem.isSugarless);
        } else {
            Log.d(TAG, "clicked on juice : " + selectedTeaItem.teaName
                    + " qty: " + Integer.parseInt(((TextView) view).getText().toString()) + " is Sugarless : " + selectedTeaItem.isSugarless);
            selectedTeaItem.selectedQuantity = Integer.parseInt(((TextView) view).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void toggleSelectionChoice(int position) {
        teaItems.get(position).isMultiSelected = !teaItems.get(position).isMultiSelected;
        teaItems.get(position).animate = true;
        notifyDataSetChanged();
    }

    public TeaItem[] getSelectedJuices() {
        List<TeaItem> selectedTeaItems = new ArrayList<>();
        for (TeaItem item : teaItems) {
            if (item.isMultiSelected) {
                selectedTeaItems.add(item);
            }
        }

        TeaItem[] selectedJuicesArray = new TeaItem[selectedTeaItems.size()];
        selectedTeaItems.toArray(selectedJuicesArray);
        return selectedJuicesArray;
    }

    public void reset() {
        for (TeaItem teaItem : teaItems) {
            teaItem.animate = teaItem.isMultiSelected;
            teaItem.isMultiSelected = false;
            teaItem.selectedQuantity = 1;
            teaItem.isSugarless = false;
        }
        notifyDataSetChanged();
    }

    public void addAll(List<HotDrink> juices) {
        teaItems = new ArrayList<>();

        for (HotDrink juice : juices) {
            if (juice.available) {
                teaItems.add(new TeaItem(juice.name, juice.imageId, juice.kanId, juice.isSugarless, false));
            }
        }
        addRegisterOption();

        notifyDataSetChanged();
    }

    private void addRegisterOption() {
        String registerUser = "Register User";
        teaItems.add(new TeaItem(registerUser, JuiceDecorator.matchImage(registerUser),
                JuiceDecorator.matchKannadaName(registerUser), false, false));
    }

    public static class ViewHolder {
        public LinearLayout singleItemView;
        public LinearLayout multiSelectView;

        public SingleSelect singleSelect = new SingleSelect();
        public MultiSelect multiSelect = new MultiSelect();


        private class MultiSelect {
            public TextView titleView;
            public List<View> quantityViews;
            public CheckBox sugarlessCheckbox;
        }

        public class SingleSelect {
            public TextView titleView;
            public TextView titleInKanView;
            public ImageView imageView;
        }
    }

}
