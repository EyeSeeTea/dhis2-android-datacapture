package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Field;

public class CheckBoxRow implements Row {
    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    private DBItemHolder<Field> mField;
    private OnFieldValueSetListener mListener;

    public CheckBoxRow(DBItemHolder<Field> field) {
        mField = field;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        CheckBoxHolder holder;

        if (convertView == null) {
            View root = inflater.inflate(R.layout.listview_row_checkbox, container, false);
            TextView textLabel = (TextView) root.findViewById(R.id.text_label);
            CheckBox checkBox = (CheckBox) root.findViewById(R.id.checkbox);

            CheckBoxListener listener = new CheckBoxListener();
            holder = new CheckBoxHolder(textLabel, checkBox, listener);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (CheckBoxHolder) view.getTag();
        }

        holder.updateViews(mField, mListener);
        return view;
    }

    @Override
    public void setListener(OnFieldValueSetListener listener) {
        mListener = listener;
    }

    @Override
    public int getViewType() {
        return RowTypes.TRUE_ONLY.ordinal();
    }

    private static class CheckBoxListener implements OnCheckedChangeListener {
        private DBItemHolder<Field> field;
        private OnFieldValueSetListener listener;

        public void setListener(OnFieldValueSetListener listener) {
            this.listener = listener;
        }

        public void setField(DBItemHolder<Field> field) {
            this.field = field;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                setValue(TRUE);
            } else {
                setValue(EMPTY_FIELD);
            }
        }

        private void setValue(String newValue) {
            String currentValue = field.getItem().getValue();
            if (newValue != null && !newValue.equals(currentValue)) {
                field.getItem().setValue(newValue);
                if (listener != null) {
                    listener.onFieldValueSet(field.getDatabaseId(), newValue);
                }
            }
        }
    }

    private static class CheckBoxHolder {
        final TextView textLabel;
        final CheckBox checkBox;
        final CheckBoxListener listener;

        public CheckBoxHolder(TextView textLabel, CheckBox checkBox,
                              CheckBoxListener listener) {
            this.textLabel = textLabel;
            this.checkBox = checkBox;
            this.listener = listener;
        }

        public void updateViews(DBItemHolder<Field> field,
                                OnFieldValueSetListener onValueSetListener) {
            listener.setField(field);
            listener.setListener(onValueSetListener);

            textLabel.setText(field.getItem().getLabel());
            checkBox.setOnCheckedChangeListener(listener);

            String value = field.getItem().getValue();
            if (TRUE.equals(value)) {
                checkBox.setChecked(true);
            } else if (EMPTY_FIELD.equals(value)) {
                checkBox.setChecked(false);
            }
        }
    }
}

