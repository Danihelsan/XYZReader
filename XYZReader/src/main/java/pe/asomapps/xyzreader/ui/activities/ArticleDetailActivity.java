package pe.asomapps.xyzreader.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.asomapps.xyzreader.R;
import pe.asomapps.xyzreader.data.ArticleLoader;
import pe.asomapps.xyzreader.data.ItemsContract;
import pe.asomapps.xyzreader.ui.ImageLoaderHelper;
import pe.asomapps.xyzreader.ui.customviews.FadeInNetworkImageView;
import pe.asomapps.xyzreader.ui.fragments.ArticleDetailFragment;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int SHARE_REQUESTCODE = 10001;
    @Bind(R.id.view_pager) ViewPager pager;
    @Bind(R.id.toolbar_image)
    FadeInNetworkImageView toolbarImage;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.share_fab)
    FloatingActionButton shareFab;

    private Cursor cursor;
    private long startId;

    private ArticlePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(pe.asomapps.xyzreader.R.layout.activity_article_detail);
        ButterKnife.bind(this);

        toolbarImage.setDefaultImageResId(R.drawable.image_toolbar);
        toolbarImage.setErrorImageResId(R.drawable.image_toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

        pagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        pager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (cursor != null) {
                    cursor.moveToPosition(position);
                    String title = cursor.getString(ArticleLoader.Query.TITLE);
                    String imageUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
                    collapsingToolbar.setTitle(title);
                    toolbarImage.setImageUrl(
                            imageUrl,ImageLoaderHelper.getInstance(ArticleDetailActivity.this).getImageLoader());
                }
            }
        });

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor!= null && cursor.getPosition()>0){
                    String shareText = cursor.getString(ArticleLoader.Query.TITLE);
                startActivityForResult(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText(getString(R.string.share_text,shareText))
                        .getIntent(), getString(R.string.action_share)),SHARE_REQUESTCODE);
                }
            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                startId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        this.cursor = cursor;
        pagerAdapter.notifyDataSetChanged();

        if (startId > 0) {
            this.cursor.moveToFirst();
            while (!this.cursor.isAfterLast()) {
                if (this.cursor.getLong(ArticleLoader.Query._ID) == startId) {
                    final int position = this.cursor.getPosition();
                    pager.setCurrentItem(position, false);
                    break;
                }
                this.cursor.moveToNext();
            }
            startId = 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==SHARE_REQUESTCODE){
            Log.d("","");
            if (resultCode == RESULT_OK){
                Log.d("","");

            } else if (resultCode == RESULT_CANCELED){
                Log.d("","");

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursor = null;
        pagerAdapter.notifyDataSetChanged();
    }

    private class ArticlePagerAdapter extends FragmentStatePagerAdapter {
        public ArticlePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            cursor.moveToPosition(position);
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(cursor.getLong(ArticleLoader.Query._ID));
            return fragment;
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }
    }
}
