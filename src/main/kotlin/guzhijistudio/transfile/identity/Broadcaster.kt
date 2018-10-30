package guzhijistudio.transfile.identity

import guzhijistudio.transfile.utils.Constants
import guzhijistudio.transfile.utils.SocketUtils
import java.io.IOException
import java.net.*


class Broadcaster(
		private val deviceName: String,
		private val groupAddr: SocketAddress
) : Thread() {

	private var running = false

	override fun start() {
		running = true
		super.start()
	}

	fun shutdown() {
		running = false
	}

	private fun isIPv4(ip: String): Boolean {
		val nums = ip.split("\\.".toRegex()).toTypedArray()
		if (nums.size != 4) return false
		try {
			for (num in nums) {
				val n = Integer.parseInt(num)
				if (n < 0 || n > 255)
					return false
			}
			return true
		} catch (_: NumberFormatException) {
			return false
		}
	}

	private fun prepareSockets(): ArrayList<DatagramSocket> {
		val sockets = ArrayList<DatagramSocket>()
		val ifaces = NetworkInterface.getNetworkInterfaces()
		while (ifaces.hasMoreElements()) {
			val iface = ifaces.nextElement()
			if (!iface.isLoopback && iface.isUp) {
				val addrs = iface.inetAddresses
				while (addrs.hasMoreElements()) {
					val addr = addrs.nextElement()
					if (isIPv4(addr.hostAddress)) {
						val socket = DatagramSocket(
								Constants.IDENTITY_LOCAL_PORT, addr)
						socket.broadcast = true
						sockets.add(socket)
					}
				}
			}
		}
		return sockets
	}

	override fun run() {
		var data = ByteArray(256)
		try {
			val sockets = prepareSockets()
			while (running) {
				for (socket in sockets) {
					try {
						socket.connect(groupAddr)
						val pos = SocketUtils.BufPos()
						SocketUtils.writeString(data, "enter", pos)
						SocketUtils.writeString(data, socket.localAddress.hostAddress, pos)
						SocketUtils.writeString(data, deviceName, pos)
						socket.send(DatagramPacket(data, data.size))
					}
					catch (_: IOException) {
					}
				}
				Thread.sleep(1000)
			}
			for (socket in sockets) {
				try {
					socket.connect(groupAddr)
					val pos = SocketUtils.BufPos()
					SocketUtils.writeString(data, "quit", pos)
					SocketUtils.writeString(data, socket.localAddress.hostAddress, pos)
					SocketUtils.writeString(data, deviceName, pos)
					socket.send(DatagramPacket(data, data.size))
				} catch (_: IOException) {
				} finally {
					socket.close()
				}
			}
		} catch (_: SocketException) {
		} catch (_: InterruptedException) {
		}

	}

}

