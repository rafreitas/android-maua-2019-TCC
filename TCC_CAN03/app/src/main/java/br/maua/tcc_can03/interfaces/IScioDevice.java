package br.maua.tcc_can03.interfaces;
//package consumerphysics.com.myscioapplication.interfaces;

public interface IScioDevice {
    void onScioButtonClicked();
    void onScioConnected();
    void onScioConnectionFailed();
    void onScioDisconnected();
}
