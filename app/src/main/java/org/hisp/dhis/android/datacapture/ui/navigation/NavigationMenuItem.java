/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.datacapture.ui.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.android.datacapture.R;

public class NavigationMenuItem implements NavigationItem {
    private final int mId;
    private final int mLabelId;
    private final int mIconId;
    private final LayoutInflater mInflater;

    public NavigationMenuItem(LayoutInflater inflater,
                              int id, int labelId, int iconId) {
        mId = id;
        mLabelId = labelId;
        mIconId = iconId;
        mInflater = inflater;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getItemId() {
        return mId;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_navigation_menu_row, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.navigation_menu_item_label),
                    (ImageView) view.findViewById(R.id.navigation_menu_item_icon)
            );

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.label.setText(mLabelId);
        holder.icon.setImageResource(mIconId);

        return view;
    }

    @Override
    public int getItemViewType() {
        return NavigationItemType.NAVIGATION_MENU_ITEM.ordinal();
    }

    private static class ViewHolder {
        public final TextView label;
        public final ImageView icon;

        private ViewHolder(TextView label, ImageView icon) {
            this.label = label;
            this.icon = icon;
        }
    }
}
