package guzhijistudio.transfile.file

import guzhijistudio.transfile.utils.SocketUtils

import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


class FileReceiver(
		private val port: Int,
		private val dir: File,
		private val listener: FileReceiverListener
) : Thread() {

	interface FileReceiverListener {
		fun onFileReceived(file: File)
		fun onFile(file: File)
		fun onMsg(msg: String)
		fun onError(msg: String)
		fun onProgress(file: File, received: Long, total: Long, speed: Long, secs: Long)
	}

	private val socket: ServerSocket = ServerSocket(port)
	private var running = false

	override fun start() {
		running = true
		super.start()
	}

	fun shutdown() {
		running = false
		try {
			socket.close()
		} catch (e: IOException) {
		}
	}

	private val progressListener = object : SocketUtils.Progress {
		override fun onStart(file: File) {
			listener.onFile(file)
		}

		override fun onFinish(file: File) {
			listener.onFileReceived(file)
		}

		override fun onProgress(file: File, progress: Long, total: Long, speed: Long, secs: Long) {
			listener.onProgress(file, progress, total, speed, secs)
		}
	}

	fun process(s: Socket) {
		val buf = ByteArray(10240)
		s.getInputStream().use {
			loop@ while (true) {
				val cmd = SocketUtils.readString(it, buf)
				when (cmd) {
					"file" -> {
						val f = SocketUtils.readFile(it, buf, dir, progressListener)
						if (f == null)
							listener.onError("")
					}
					"msg" -> {
						val m = SocketUtils.readString(it, buf)
						if (!m.isEmpty())
							listener.onMsg(m)
					}
					"close" -> {
						it.close()
						break@loop
					}
				}
			}
		}
		s.close()
	}

	override fun run() {
		try {
			while (running) {
                val socket = socket.accept()
				Thread(Runnable { process(socket) }).start()
			}
		} catch (e: IOException) {
		}
	}

}

