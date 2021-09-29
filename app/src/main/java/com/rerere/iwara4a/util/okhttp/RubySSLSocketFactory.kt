package com.rerere.iwara4a.util.okhttp

import android.util.Log
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class RubySSLSocketFactory : SSLSocketFactory() {

    override fun getDefaultCipherSuites() = arrayOf<String>()

    override fun getSupportedCipherSuites() = arrayOf<String>()

    override fun createSocket(socket: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        val address = socket!!.inetAddress
        if (autoClose) socket.close()
        val sslSocket = (getDefault().createSocket(address, port) as SSLSocket).apply { enabledProtocols = supportedProtocols }
        val sslSession = sslSocket.session
        Log.i(
            "!",
            "Address: ${address.hostAddress}, Protocol: ${sslSession.protocol}, PeerHost: ${sslSession.peerHost}, CipherSuite: ${sslSession.cipherSuite}."
        )
        return sslSocket
    }

    override fun createSocket(host: String?, port: Int): Socket? = null

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket? = null

    override fun createSocket(address: InetAddress?, port: Int): Socket? = null

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket? = null
}