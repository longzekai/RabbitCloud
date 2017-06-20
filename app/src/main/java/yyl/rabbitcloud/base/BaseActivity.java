package yyl.rabbitcloud.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;
import yyl.rabbitcloud.App;
import yyl.rabbitcloud.R;
import yyl.rabbitcloud.di.component.AppComponent;

/**
 * Created by yyl on 2017/6/9.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestLayout();
        ButterKnife.bind(this);
        setupActivityComponent(App.getAppInstance().getAppComponent());
        mToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mToolbar != null) {
            initToolBar();
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_grey_800_36dp);
        }
        initUi();
        initData();
        initListener();

    }

    protected abstract void initToolBar();

    protected abstract void requestLayout();

    protected abstract void initData();

    protected abstract void initUi();

    protected abstract void setupActivityComponent(AppComponent appComponent);

    protected abstract void initListener();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
