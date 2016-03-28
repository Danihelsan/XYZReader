package pe.asomapps.xyzreader.ui.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.xyzreader.R;
import pe.asomapps.xyzreader.data.ArticleLoader;
import pe.asomapps.xyzreader.data.ItemsContract;
import pe.asomapps.xyzreader.ui.DynamicHeightNetworkImageView;
import pe.asomapps.xyzreader.ui.ImageLoaderHelper;

/**
 * @author Danihelsan
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ItemHolder> {
    private Cursor mCursor;

    public ArticleListAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ItemHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        final ItemHolder vh = new ItemHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(holder.rootView.getContext()).getImageLoader());

        float aspectRatio = mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO);
        holder.thumbnailView.setAspectRatio(aspectRatio);

        String title = mCursor.getString(ArticleLoader.Query.TITLE);
        String subTitle = DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString()
                + " by "
                + mCursor.getString(ArticleLoader.Query.AUTHOR);

        if (aspectRatio<=1.125f){
            holder.insideContainer.setVisibility(View.VISIBLE);
            holder.outsideContainer.setVisibility(View.GONE);

            holder.insideTitle.setText(title);
            holder.insideSubtitle.setText(subTitle);
        } else {
            holder.outsideContainer.setVisibility(View.VISIBLE);
            holder.insideContainer.setVisibility(View.GONE);

            holder.outsideTitle.setText(title);
            holder.outsideSubtitle.setText(subTitle);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rootView) View rootView;
        @Bind(R.id.thumbnail) DynamicHeightNetworkImageView thumbnailView;
        @Bind(R.id.insideContainer) ViewGroup insideContainer;
        @Bind(R.id.outsideContainer) ViewGroup outsideContainer;
        @Bind(R.id.inside_title) TextView insideTitle;
        @Bind(R.id.inside_subtitle) TextView insideSubtitle;
        @Bind(R.id.outside_title) TextView outsideTitle;
        @Bind(R.id.outside_subtitle) TextView outsideSubtitle;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
