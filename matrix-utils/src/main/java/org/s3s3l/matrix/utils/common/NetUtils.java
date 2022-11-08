package org.s3s3l.matrix.utils.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.s3s3l.matrix.utils.bean.exception.NetException;

public abstract class NetUtils {
    public static final String IPV4 = getFirstIpv4Address();

    public static String getFirstIpv4Address() {
        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                if (iface.isLoopback() || iface.isVirtual() || !iface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            throw new NetException(e);
        }

        return null;
    }
}
