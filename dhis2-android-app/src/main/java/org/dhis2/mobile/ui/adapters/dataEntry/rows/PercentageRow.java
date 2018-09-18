package org.dhis2.mobile.ui.adapters.dataEntry.rows;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Field;

public class PercentageRow extends EditTextRow implements Row {
    private final LayoutInflater inflater;
    private final Field field;
    public boolean readOnly = false;

    public PercentageRow(LayoutInflater inflater, Field field) {
        this.inflater = inflater;
        this.field = field;
    }

    @Override
    public View getView(View convertView) {
        View view;
        EditTextHolder holder;

        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_percentage,
                    null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_percentage_row);

            EditTextWatcher watcher = new EditTextWatcher(field);
            editText.addTextChangedListener(watcher);
            editText.setFilters(new InputFilter[]{new PercentFilter()});

            holder = new EditTextHolder(label, editText, watcher);
            rowRoot.setTag(holder);
            view = rowRoot;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag();
        }

        RowCosmetics.setTextLabel(field, holder.textLabel);

        holder.textWatcher.setField(field);
        holder.editText.addTextChangedListener(holder.textWatcher);
        holder.editText.setText(field.getValue());
        holder.editText.clearFocus();
        holder.editText.setOnEditorActionListener(mOnEditorActionListener);

        if (readOnly) {
            holder.editText.setEnabled(false);
        } else {
            holder.editText.setEnabled(true);
        }
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.PERCENTAGE.ordinal();
    }

    @Override
    public void setReadOnly(boolean value) {
        readOnly = value;
    }

    private class PercentFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spn.length() >= 2 && str.length() >= 1) &&
                    !(spn.toString().equals("10") && str.charAt(0) == '0')) {
                return Field.EMPTY_FIELD;
            }
            if ((str.length() > 0) && (spn.toString().equals("0"))) {
                return Field.EMPTY_FIELD;
            }

            return str;
        }
    }

}
