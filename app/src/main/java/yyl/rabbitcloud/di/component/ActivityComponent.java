package yyl.rabbitcloud.di.component;

import dagger.Component;
import yyl.rabbitcloud.di.component.AppComponent;
import yyl.rabbitcloud.livebycate.LiveTypeDetailActivity;
import yyl.rabbitcloud.liveroom.LiveRoomActivity;
import yyl.rabbitcloud.slash.SplashActivity;

/**
 * Created by yyl on 2017/6/15.
 */

@Component(dependencies = AppComponent.class)
public interface ActivityComponent {
    //对SlpshActivity进行依赖注入 目标类
    SplashActivity inject(SplashActivity splashActivity);

    //对LiveTypeDetailActivity进行依赖注入
    LiveTypeDetailActivity inject(LiveTypeDetailActivity typeDetailActivity);

    //对LiveRoomActivity进行依赖注入
    LiveRoomActivity inject(LiveRoomActivity liveRoomActivity);

}
