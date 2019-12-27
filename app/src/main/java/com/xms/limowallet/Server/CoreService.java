package com.xms.limowallet.Server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.xms.limowallet.constant.Constant;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class CoreService extends Service {
    private Server mServer;

    InetAddress ip;


    @Override
    public void onCreate() {
        try {
            ip = InetAddress.getByName(Constant.NATIVE_LAN);
            mServer = AndServer.serverBuilder()
//                    .inetAddress(NetUtils.getLocalIPAddress())//设置局域网的ip
                    .inetAddress(ip)//设置本机的ip
                    .port(Constant.PORT_MAIN)//设置端口号
                    .timeout(10, TimeUnit.SECONDS)//设置等待超时的时间
                    .listener(new Server.ServerListener() {//服务监听
                        @Override
                        public void onStarted() {
                            String hostAddress = mServer.getInetAddress().getHostAddress();
                            ServerManager.onServerStart(CoreService.this, hostAddress);
                        }

                        @Override
                        public void onStopped() {
                            ServerManager.onServerStop(CoreService.this);
                        }

                        @Override
                        public void onException(Exception e) {
                            ServerManager.onServerError(CoreService.this, e.getMessage());
                        }
                    })
                    .build();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer.isRunning()) {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.onServerStart(this, hostAddress);
        } else {
            mServer.startup();
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
