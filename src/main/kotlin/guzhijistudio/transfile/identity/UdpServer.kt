package guzhijistudio.transfile.identity

import guzhijistudio.transfile.utils.SocketUtils

import java.net.InetAddress
import java.net.SocketAddress
import java.net.MulticastSocket
import java.net.DatagramPacket
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap


class UdpServer(
		addr: SocketAddress,
		group: InetAddress,
		private val listener: UdpServerListener
) : Thread() {

	interface UdpServerListener {
		fun onEnter(ip: String, name: String)
		fun onQuit(ip: String)
	}

	private var running = false
	private val socket = MulticastSocket(addr)
	private val ips = ConcurrentHashMap<String, Long>()
	private val bgThread = object : Thread() {
		override fun run() {
			try {
				while (running) {
					removeExpired()
					Thread.sleep(1000)
				}
			} catch (_: InterruptedException) {
			}
		}
	}

	init {
		socket.joinGroup(group)
	}

	private fun removeExpired() {
		var t = System.currentTimeMillis()
		val expired = LinkedList<String>()
		for ((key, value) in ips) {
			if (t - value > 30000) expired.add(key)
		}
		for (expiredIp in expired) {
			ips.remove(expiredIp)
			listener.onQuit(expiredIp)
		}
	}

	override fun start() {
		running = true
		bgThread.start()
		super.start()
	}

	fun shutdown() {
		running = false
		socket.close()
	}

	override fun run() {
		val data = ByteArray(256)
		var buf = ByteArray(256)
		val dp = DatagramPacket(data, data.size)
		while (running) {
			try {
				socket.receive(dp)
				val t = System.currentTimeMillis()
				val pos = SocketUtils.BufPos()
				val cmd = SocketUtils.toStr(data, buf, pos)
				val ip = SocketUtils.toStr(data, buf, pos)
				val name = SocketUtils.toStr(data, buf, pos)
				if (ip != null && name != null) {
					if ("quit".equals(cmd!!, ignoreCase = true)) {
						listener.onQuit(ip)
						ips.remove(ip)
					} else {
						if (!ips.containsKey(ip)) {
							listener.onEnter(ip, name)
						}
						ips[ip] = t
					}
				}

			}
			catch (_: Exception) {
			}
		}
	}
}

